package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.platform.filter.NodeFillter;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.QmJzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.QmphVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gzservice")
public class QmgzServiceImpl implements IQmgzService {

	@Autowired
	private IQcye qcyeservice;
	
	@Autowired
	private IQmclService qmclService;
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;

	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IAccountService accountService;
	
	@Override
	public QmclVO[] query(QueryParamVO vo , String userid, DZFDate d)
			throws DZFWarpException {
		/*CorpVO cvo=CorpCache.getInstance().get(userid, vo.getPk_corp());
		DZFDate corpdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(cvo.getBegindate())) ;
		if(cvo!=null){
			if(cvo.getBegindate()==null){
				throw new BusinessException("公司:'"+cvo.getUnitname()+"'的建账日期为空，可能尚未建账，请检查!");
			}
			if(vo.getBegindate1().before(corpdate)){
				throw new BusinessException("期间起不能在所选公司:'"+cvo.getUnitname()+"'的建账日期"+DateUtils.getPeriod(cvo.getBegindate())+"之前");
			}
		}*/
		/*List<String> corps = new ArrayList<String>();
		corps.add(vo.getPk_corp());*/
		List<QmclVO> vos = qmclService.initquery(vo.getCorpslist(),vo.getBegindate1(), vo.getEnddate(), userid, d, DZFBoolean.FALSE, DZFBoolean.FALSE);
		return vos.toArray(new QmclVO[vos.size()]);
	}
	
// 关账操作，保存关账状态
	@Override
	public void processGzOperate(String pk_corp,String qj, DZFBoolean b ,String userid)
			throws DZFWarpException {
		if (!b.booleanValue()) {
			// 反关账
			String year = qj.substring(0, 4);
			SQLParameter sp1 = new SQLParameter();
			sp1.addParam(pk_corp);
			sp1.addParam(year + "-12");
			String sql1 = " select * from ynt_qmjz where pk_corp = ? and period = ? and nvl(dr,0) = 0 ";
			List<QmJzVO> qmjzvos = (List<QmJzVO>) singleObjectBO.executeQuery(
					sql1, sp1, new BeanListProcessor(QmJzVO.class));
			/*
			 * List<QmJzVO> qmjzvos = new ArrayList<QmJzVO>(); qmjzvos.add(new
			 * QmJzVO()); qmjzvos.get(0).setJzfinish(DZFBoolean.TRUE);
			 */
			if (qmjzvos != null && qmjzvos.size() > 0) {
				if (qmjzvos.get(0).getJzfinish() != null
						&& qmjzvos.get(0).getJzfinish().booleanValue()) {
					throw new BusinessException("该公司年底已结账不能反关账!");
				}
			}
		} else {
			// 关账检查
			List<Object> phlist = gzCheck(pk_corp, qj);
			SsphRes qcphvo = (SsphRes) phlist.get(0);
			QmphVO qmphvo = (QmphVO) phlist.get(1);
			// 自动结转损益
			autoSyjz(qcphvo, qmphvo, pk_corp, qj,userid);
			if (qcphvo != null && qmphvo != null) {
				if (!qcphvo.getYearres().equals("平衡") || !qmphvo.isSuccess()) {
					CorpVO corpvo = corpService.queryByPk(pk_corp);
					throw new BusinessException(corpvo.getUnitname() + "在期间" + qj + "关账检查不通过，请检查！");
				}
			}
		}
		SQLParameter sp = new SQLParameter();
		String sql = " select * from ynt_qmcl where pk_corp = ? and period = ? and nvl(dr,0) = 0 ";
		sp.addParam(pk_corp);
		sp.addParam(qj.substring(0, 7));
		List<QmclVO> qmvos = (List<QmclVO>) singleObjectBO.executeQuery(sql,
				sp, new BeanListProcessor(QmclVO.class));
		if (qmvos != null && qmvos.size() > 0) {
			QmclVO qmclvo = qmvos.get(0);
			qmclvo.setIsgz(b);
			singleObjectBO.updateAry(qmvos.toArray(new QmclVO[qmvos.size()]),
					new String[] { "isgz" });
		}

	}
	
	/**
	 * 无凭证数据时，关账自动损益结转
	 * @param qmph
	 * @param pk_corp
	 * @param period
	 */
	private void autoSyjz(SsphRes qcphvo, QmphVO qmphvo, String pk_corp, String period,String userid) {
		String sql = "select 1 from ynt_tzpz_h where pk_corp = ? and period = ? and nvl(dr,0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		boolean hasVoucher = singleObjectBO.isExists(pk_corp, sql, sp);
		if (!hasVoucher && qcphvo.getYearres().equals("平衡")
				&& qmphvo.getRes().equals("平衡")
				&& !qmphvo.getIssyjz().booleanValue()) {
			QmclVO qmcl = new QmclVO();
			qmcl.setPeriod(period);
			qmcl.setPk_corp(pk_corp);
			qmclService.updateQiJianSunYiJieZhuan(qmcl,userid);
			qmphvo.setIssyjz(DZFBoolean.TRUE);
			qmphvo.setSuccess(true);
		}
	}

	@Override
	public List<Object> gzCheck(String pk_corp,String qj) throws DZFWarpException {
		List<Object> ssresult = new ArrayList<Object>();
		SsphRes ssph = checkSsPh(pk_corp);//检查期初平衡
		ssresult.add(ssph);
		QmphVO qmphvo = new QmphVO();
		qmphvo = checkQmPh(qmphvo, pk_corp, qj);//检查期末平衡
		qmphvo = checkPzPh(qmphvo, qj, pk_corp);//检查凭证是否短号，是否有未记账,是否有暂存凭证
		qmphvo = checkQmcl(qmphvo, qj, pk_corp);//检查期末结转，损益结转
		if (qmphvo.getIshasjz().booleanValue() && qmphvo.getDhres().equals("通过") && qmphvo.getRes().equals("平衡")
				&& !qmphvo.getPztemp().booleanValue() && qmphvo.getIssyjz().booleanValue()){
			qmphvo.setSuccess(true);
		}
		ssresult.add(qmphvo);
		return ssresult;
	}
	
	private QmphVO checkQmcl(QmphVO qmphvo, String qj, String pk_corp) {
		qmphvo.setIssyjz(DZFBoolean.TRUE);
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select * ");
		qrysql.append("  from  ynt_qmcl tt   ");
		qrysql.append(" where tt.pk_corp = ?    ");
		qrysql.append("   and tt.period = ? ");
		qrysql.append(" and nvl(tt.dr,0)=0   ");
		sp.addParam(pk_corp);
		sp.addParam(qj);
		List<QmclVO> qmclvos  = (List<QmclVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(QmclVO.class));
		
		QmclVO qmclvo = null;
		if(qmclvos!=null && qmclvos.size()>0){
			qmclvo = qmclvos.get(0);
		}
		if(qmclvo==null || qmclvo.getIsqjsyjz()== null ||
				!qmclvo.getIsqjsyjz().booleanValue()){
			qmphvo.setIssyjz(DZFBoolean.FALSE);
		}
		
		return qmphvo;
	}

	//检查是否期末平衡 (是否存在科目赤字)
	private QmphVO checkQmPh (QmphVO qmphvo,String pk_corp,String qj){
		CorpVO corpvo =corpService.queryByPk(pk_corp);
		QueryParamVO qvo = new QueryParamVO();
		qvo.setPk_corp(pk_corp);
		int year = Integer.parseInt(qj.substring(0, 4));
		int month = Integer.parseInt(qj.substring(5, 7));
		int day =DZFDate.getDaysMonth(year, month);
		//设置查询条件
		qvo.setBegindate1(new DZFDate(year+"-01"+"-01"));
		qvo.setEnddate(new DZFDate(qj+"-"+day));
		qvo.setIshasjz(DZFBoolean.FALSE);
		qvo.setXswyewfs(DZFBoolean.TRUE);
		qvo.setCjq(1);
		qvo.setCjz(6);
		FseJyeVO[] fvos = zxkjReportService.getFsJyeVOs(qvo,1);
		List<FseJyeVO> fsjyevoList = new ArrayList<FseJyeVO>();
		if(fvos!=null && fvos.length>0){
			for(FseJyeVO fsjye : fvos) {  // 级次
				if(  fsjye.getAlevel() == 1 ){
					fsjyevoList.add(fsjye);
				}
			}
			fvos = fsjyevoList.toArray(new FseJyeVO[0]);
		}
		DZFDouble jf =DZFDouble.ZERO_DBL;
		DZFDouble df =DZFDouble.ZERO_DBL;
		DZFDouble ce =DZFDouble.ZERO_DBL;
		if (fvos != null && fvos.length > 0){
			for (FseJyeVO fvo : fvos) {
				jf=jf.add(fvo.getQmjf()==null?DZFDouble.ZERO_DBL:fvo.getQmjf());
				df=df.add(fvo.getQmdf()==null?DZFDouble.ZERO_DBL:fvo.getQmdf());
			}
			ce=jf.sub(df);
		}
		qmphvo.setJf(jf);
		qmphvo.setDf(df);
		qmphvo.setCe(ce);
		if(qmphvo.getCe().compareTo(DZFDouble.ZERO_DBL)==0){
			qmphvo.setRes("平衡");
		}else{
			qmphvo.setRes("不平衡");
		}

		if(NodeFillter.isHasNode(corpvo.getCorptype(), "资产负债表")){
			checkZcfz(fvos,pk_corp,qmphvo);//资产负债是否平衡
		}
//		checzKmcz(fvos,pk_corp,qmphvo);//科目是否存在赤字
		
		return qmphvo;
	}

	private void checzKmcz(FseJyeVO[] fvos,String pk_corp,QmphVO qmphvo) {
		Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);
		StringBuffer kmmsg = new StringBuffer();
		String[] conkm = null;
		List<String> conkms = new ArrayList<String>();
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue() || corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2007会计准则 // 2013会计准则
			conkm = new String[]{"1001","1002","1012"};
		} else if (corpschema == DzfUtil.POPULARSCHEMA.intValue()) {// 民间
			conkm = new String[]{"1001","1002","1009"};
		} else if (corpschema == DzfUtil.CAUSESCHEMA.intValue()) {// 事业
			conkm = new String[]{"1001","1002"};
		} else if(corpschema ==  DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){//企业会计制度
			conkm =  new String[]{"1001","1002","1009"};
		} 
		else {
			throw new BusinessException("该公司对应的科目方案为空!");
		}
		//07和13科目编码一样
		if (fvos != null && fvos.length > 0) {
			for(FseJyeVO jyvo:fvos){
				conkms = Arrays.asList(conkm);
				if(conkms.contains(jyvo.getKmbm())){
					if(jyvo.getQmjf()!=null && jyvo.getQmjf().doubleValue()<0){
						kmmsg.append(jyvo.getKmmc() + ",");
					}
					if(jyvo.getQmdf()!=null && jyvo.getQmdf().doubleValue()>0){
						kmmsg.append(jyvo.getKmmc() + ",");
					}
				}
			}
		}
		if(kmmsg.toString().length()>0){
			qmphvo.setKmcz(kmmsg + "科目存在赤字！");
		}else{
			qmphvo.setKmcz("通过");
		}
	}

	private void checkZcfz(FseJyeVO[] fvos, String pk_corp,QmphVO qmphvo) {

		Map<String, YntCpaccountVO> mapc = new HashMap<String, YntCpaccountVO>();
		
		YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);
		
		if(cpavos!= null && cpavos.length>0){
			for(YntCpaccountVO cpavo:cpavos){
				mapc.put(cpavo.getAccountcode(), cpavo);
			}
		}

		ZcFzBVO[] dataVOS = zxkjReportService.getZcfzVOs(pk_corp, new String[] { "N", "N", "N", "N","N" }, mapc, fvos);
		
		DZFDouble ncye1 = ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getNcye1() == null ? DZFDouble.ZERO_DBL
				: ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getNcye1();
		DZFDouble ncye2 = ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getNcye2() == null ? DZFDouble.ZERO_DBL
				: ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getNcye2();
		DZFDouble qmye1 = ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getQmye1() == null ? DZFDouble.ZERO_DBL
				: ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getQmye1();
		DZFDouble qmye2 = ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getQmye2() == null ? DZFDouble.ZERO_DBL
				: ((ZcFzBVO) dataVOS[dataVOS.length - 1]).getQmye2();

		StringBuffer message = new StringBuffer();
		if (qmye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(qmye2.setScale(2, DZFDouble.ROUND_HALF_UP))
				.doubleValue() != 0) {
			message.append("期末余额不平");
		}
		if (ncye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(ncye2.setScale(2, DZFDouble.ROUND_HALF_UP))
				.doubleValue() != 0) {
			message.append("年初余额不平");
		}

		if(message.toString().length()>0){
			qmphvo.setZcfzblan("当期资产负债表不平衡，请检查！");
		}else{
			qmphvo.setZcfzblan("通过");
		}
	}

	//检查是否期初平衡
	private SsphRes checkSsPh(String pk_corp){
		SsphRes ssph=qcyeservice.ssph(pk_corp);
		ssph.setYearce(ssph.getYearce()==null?DZFDouble.ZERO_DBL:ssph.getYearce());
		ssph.setYeardf(ssph.getYeardf()==null?DZFDouble.ZERO_DBL:ssph.getYeardf());
		ssph.setYearjf(ssph.getYearjf()==null?DZFDouble.ZERO_DBL:ssph.getYearjf());
		ssph.setYearres(ssph.getYearres()==null?"平衡":ssph.getYearres());
		return ssph;
		
	}
	//检查凭证是否有短号或者没有记账的
	private QmphVO checkPzPh(QmphVO qmphvo, String qj,String pk_corp){
		String sql1 = " select * from YNT_TZPZ_H where  period=? and pk_corp=? and nvl(dr,0)=0 order by pzh asc";
		SQLParameter parama = new SQLParameter();
		parama.addParam(qj);
		parama.addParam(pk_corp);
		List<TzpzHVO> pzvostemp = (List<TzpzHVO>) singleObjectBO.executeQuery(sql1, parama, new BeanListProcessor(TzpzHVO.class));
		StringBuffer b =new StringBuffer();
		qmphvo.setIshasjz(DZFBoolean.TRUE);
		qmphvo.setPztemp(DZFBoolean.FALSE);//是否有暂存凭证
		List<TzpzHVO> pzvos = new ArrayList<TzpzHVO>();
		if(pzvostemp!=null&&pzvostemp.size()>0){
			for(TzpzHVO hvo:pzvostemp){
				if (hvo.getVbillstatus() != null && hvo.getVbillstatus().intValue() == IVoucherConstants.TEMPORARY) {
					qmphvo.setPztemp(DZFBoolean.TRUE);//存在暂存凭证
				}else{
					pzvos.add(hvo);
				}
			}
		}
		
		if(pzvos!=null&&pzvos.size()>0){
			int a1,a2,a,c;
			if (Integer.parseInt(pzvos.get(0).getPzh()) > 2){
				b.append("记"+(Integer.parseInt(pzvos.get(0).getPzh())-1)+"~"+(1)+",");
			}else if(Integer.parseInt(pzvos.get(0).getPzh()) == 2){
				b.append("记"+(1)+",");
			}
			for (int i = 1; i<pzvos.size();i++) {
				a1=Integer.parseInt(pzvos.get(i).getPzh());//当前凭证的凭证号
				a2 = Integer.parseInt(pzvos.get(i-1).getPzh());//上一个的凭证的凭证号
				a =	Integer.parseInt(pzvos.get(i).getPzh())-Integer.parseInt(pzvos.get(0).getPzh());//计算当前凭证号与最小凭证号的差值用以判断是否有断号的凭证
				if(a!=i){
					c =	a1-a2;//当前凭证与上一个凭证号的差值；
					if(c>2){
						b.append("记"+(a2+1)+"~"+(a1-1)+",");
					}else if(c==2){
						b.append("记"+(a1-1)+",");
					}
				}
			}
			for (int i = 0; i < pzvos.size(); i++) {

				if (pzvos.get(i).getIshasjz() == null || !pzvos.get(i).getIshasjz().booleanValue()) {// 所选的凭证有一个没记账，检查不通过
					qmphvo.setIshasjz(DZFBoolean.FALSE);
					break;
				}

			}
		}
		if(!b.toString().equals("")){
			qmphvo.setDhres("不通过");
			
		}else{
			qmphvo.setDhres("通过");
		}
		qmphvo.setNum(b.toString());
		return qmphvo;
		
	}
	@Override
	public boolean isGz(String pk_corp, String startqj) throws DZFWarpException {//12个月的检查放到下边的yearhasGz()方法，通过返回的qmclvo来判断是否关账
		SQLParameter sp = new SQLParameter();
		boolean f = false;
		String sql = " select isgz from ynt_qmcl where pk_corp = ? and period = ? and nvl(dr,0) = 0 ";
		sp.addParam(pk_corp);
		sp.addParam(startqj.substring(0,7));
		List<QmclVO> qmvos= (List<QmclVO>) singleObjectBO.executeQuery(sql, sp, new  BeanListProcessor(QmclVO.class));
		if(qmvos!=null&&qmvos.size()>0){
			if(qmvos.get(0).getIsgz()!=null&&qmvos.get(0).getIsgz().booleanValue()){
				f=  true;
			}
		}
		return f;
	}
	@Override
	public List<QmclVO> yearhasGz(String pk_corp, String qj)//查询一年的qmclvo用来判断12个月中哪个月有关账的
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		CorpVO cvo = corpService.queryByPk(pk_corp);
		String sql;
		sp.addParam(pk_corp);
		sp.addParam("%"+qj.substring(0,4)+"%");
		if(String.valueOf(cvo.getBegindate().getYear()).equals(qj.substring(0,4))){//如果建账日期在查询日期
			sp.addParam(DateUtils.getPeriod(cvo.getBegindate()));
			sql = " select * from ynt_qmcl where pk_corp = ? and period like ? and period >= ? and  nvl(dr,0) = 0 ";
		}else{
			sql = " select * from ynt_qmcl where pk_corp = ? and period like ? and nvl(dr,0) = 0 ";
		}
		List<QmclVO> qmvos = (List<QmclVO>) singleObjectBO.executeQuery(sql, sp, new  BeanListProcessor (QmclVO.class));
		for(QmclVO vo : qmvos){
			if(vo.getIsgz()==null){
				vo.setIsgz(DZFBoolean.FALSE);
			}
		}
		return qmvos;
	}

	@Override
	public boolean checkLaterMonthGz(String pk_corp, String qj)
			throws DZFWarpException {
		String sql = " select 1 from ynt_qmcl where pk_corp = ? and period > ? and isgz = 'Y' and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		return singleObjectBO.isExists(pk_corp, sql, sp);
	}

	@Override
	public void cancelGzPeriodAndLater(String pk_corp, String qj)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qj);
		String sql = "select pk_qmcl, pk_corp, period from ynt_qmcl where pk_corp = ? and period >= ? and isgz = 'Y' and nvl(dr,0) = 0 ";
		List<QmclVO> rs = (List<QmclVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(QmclVO.class));
		if (rs != null && rs.size() > 0) {
			String year = qj.substring(0,4);
			sp.clearParams();
			sp.addParam(pk_corp);
			sp.addParam(year + "-12");
			String sql1 = " select jzfinish from ynt_qmjz where pk_corp = ? and period = ? and nvl(dr,0) = 0 ";
			List<QmJzVO> qmjzvos = (List<QmJzVO>) singleObjectBO.executeQuery(sql1, sp, new  BeanListProcessor (QmJzVO.class));
			if(qmjzvos != null && qmjzvos.size() > 0){
				if(qmjzvos.get(0).getJzfinish() !=null && qmjzvos.get(0).getJzfinish().booleanValue()){
					throw new BusinessException("该公司年底已结账不能反关账!");
				}
			}
			for (QmclVO qmclVO : rs) {
				qmclVO.setIsgz(DZFBoolean.FALSE);
			}
			singleObjectBO.updateAry(rs.toArray(new QmclVO[0]), new String[]{"isgz"});
		}
	}
	

}

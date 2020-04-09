package com.dzf.zxkj.platform.service.jzcl.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.AgeReportQueryVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgSetVO;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgVo;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.SsphRes;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zcgl.ZcdzVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.jzcl.IQmGzBgService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.zcgl.IZczzdzReportService;
import com.dzf.zxkj.platform.service.zncs.IBillcategory;
import com.dzf.zxkj.platform.util.VoUtils;
import com.dzf.zxkj.report.service.IZxkjReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

/**
 * 期末关账报表
 * @author zhangj
 *
 */
@Service("gl_qmgzbgserv")
@Slf4j
public class QmGzBgServiceImpl implements IQmGzBgService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;

	@Autowired
	private IQcye gl_qcyeserv;//期初试算平衡


	@Autowired
	private YntBoPubUtil yntBoPubUtil = null;

	@Autowired
	private ICpaccountService gl_cpacckmserv ;

	@Autowired
	private ICpaccountCodeRuleService gl_accountcoderule ;

	@Autowired
	private IBillcategory billcategory;

	@Autowired
	private IZxkjReportService zxkjReportService;

	@Autowired
	private IZczzdzReportService zczzdzReportService;

	@Override
	public Map<String, List<QmGzBgVo>> queryQmgzZb(String pk_corp, String period) throws DZFWarpException {

		//获取从建账开始到现在的数据
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		if(StringUtil.isEmpty(cpvo.getChargedeptname())){
			cpvo.setChargedeptname("小规模纳税人");
		}

		Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);

		//获取设置数据
		Map<String, QmGzBgSetVO> setmap =getSetMap(pk_corp);

		//获取科目明细表的数据
		Object[] kmmxobjs = getBaseData(period, cpvo);
		Object[] fsobjs = zxkjReportService.getEveryPeriodFsJyeVOs(cpvo.getBegindate(), DateUtils.getPeriodEndDate(period), pk_corp, kmmxobjs,"",null);
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) fsobjs[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) fsobjs[1];

		// 发生额余额表数据
		List<FseJyeVO> curr_fsvos = getFsvos(period,monthmap);//当期数据值

		//利润表季报数据
		Map<String, LrbquarterlyVO[]>  lrbjbs = getLrbQuarterly(cpvo, period, kmmxobjs);

		//获取科目数据
		Map<String,YntCpaccountVO> mapc = convert(mp);

		Map<String,List<QmGzBgVo>> qmgzbgmap  = new LinkedHashMap<String,List<QmGzBgVo>>();
		// 资产负债表数据
		// 财务处理完整性
		handCwcl(qmgzbgmap, pk_corp, period,cpvo);
		// 余额异常
		handYe(qmgzbgmap, curr_fsvos,cpvo,period);
		// 往来异常
		handWl(qmgzbgmap, pk_corp, period,cpvo,mapc);
		// 关键指标
		handGjZb(qmgzbgmap, pk_corp, period,curr_fsvos,mapc,cpvo, kmmxobjs,corpschema);
		// 经营数据分析
		handJySjFx(qmgzbgmap, pk_corp, period,curr_fsvos,cpvo,monthmap,lrbjbs,mapc,setmap,corpschema);
		// 小规模指标检查
		handXgmZb(qmgzbgmap, cpvo, period,lrbjbs,monthmap,setmap);

		return qmgzbgmap;
	}

	private Map<String, QmGzBgSetVO> getSetMap(String pk_corp) {
		Map<String, QmGzBgSetVO> map =  new HashMap<String,QmGzBgSetVO>();

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		QmGzBgSetVO[] setvos =  (QmGzBgSetVO[]) singleObjectBO.queryByCondition(QmGzBgSetVO.class, "nvl(dr,0)=0 and pk_corp = ?", sp);

		if (setvos != null && setvos.length > 0) {
			for (QmGzBgSetVO vo : setvos) {
				map.put(vo.getVxm(), vo);
			}
		}

		return map;
	}

	/**
	 * 查询科目明细账数据
	 * @param cpvo
	 * @return
	 */
	private Object[] getBaseData(String period, CorpVO cpvo) {
		Object[] obj;
		QueryParamVO paramvo = getBasetParamVO(period, cpvo);
		obj =  zxkjReportService.getKMMXZVOs1(paramvo, false);//获取基础数据(科目明细账)
		return obj;
	}

	private QueryParamVO getBasetParamVO(String period, CorpVO cpvo) {
		DZFDate begdate = cpvo.getBegindate();
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setPk_corp(cpvo.getPk_corp());
		paramvo.setBegindate1(begdate);
		paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
		paramvo.setIshasjz(DZFBoolean.FALSE);
		paramvo.setXswyewfs(DZFBoolean.FALSE);
		paramvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
		paramvo.setCjq(1);
		paramvo.setCjz(6);
		return paramvo;
	}

	private Map<String, YntCpaccountVO> convert(Map<String, YntCpaccountVO> mp) {
		Map<String, YntCpaccountVO> mp1 = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO b : mp.values()) {
			mp1.put(b.getAccountcode(), b);
		}
		return mp1;
	}

	public Map<String,YntCpaccountVO> setCpAccountGroup(YntCpaccountVO[] cvos1){
		if(cvos1 == null || cvos1.length == 0){
			return null;
		}
		Map<String,YntCpaccountVO> map = new HashMap<String,YntCpaccountVO>();
		for(YntCpaccountVO c : cvos1){
			String code = c.getAccountcode();
			map.put(code, c);
		}
		return map;
	}


	private List<FseJyeVO> getFsvos(String period, Map<String, List<FseJyeVO>> monthmap) {

		List<FseJyeVO> fsvos =monthmap.get(period);

		if(fsvos==null){
			return new ArrayList<FseJyeVO>();
		}

		return fsvos;
	}

	/**
	 * 小规模指标检查
	 *
	 * @param period
	 */
	private void handXgmZb(Map<String,List<QmGzBgVo>> qmgzbgmap,CorpVO cpvo, String period,
						   Map<String, LrbquarterlyVO[]>  lrbjbs,Map<String, List<FseJyeVO>> monthmap
			,Map<String,QmGzBgSetVO> setmap) {
		String charname = cpvo.getChargedeptname();
		if("小规模纳税人".equals(charname)){
			List<QmGzBgVo> reslist = new ArrayList<QmGzBgVo>();
			//收入预警
			sryjCheck(reslist,cpvo,period,monthmap,setmap);
			//免税收入预警
			mssryjCheck(reslist,period,cpvo,lrbjbs,setmap);
			if(reslist!=null && reslist.size()>0){
				qmgzbgmap.put("xgmzb", reslist);//小规模指标检查
			}
		}
	}

	private void sryjCheck(List<QmGzBgVo> reslist, CorpVO cpvo,
						   String period, Map<String, List<FseJyeVO>> monthmap,Map<String,QmGzBgSetVO> setmap) {
		QmGzBgSetVO setvo = setmap.get("收入预警") == null ? new QmGzBgSetVO():setmap.get("收入预警");
		QmGzBgVo vo = new QmGzBgVo();
		vo.setXm("收入预警");
		vo.setIssuccess(DZFBoolean.TRUE);
		vo.setVmemo("通过");
		vo.setYjz(setvo.getNmax() == null ? new DZFDouble(750000.00) : setvo.getNmax());

		try {
			if(monthmap!=null && monthmap.size()>0){
				String[] codes = getZyywsr(cpvo);//获取主营业务收入
				if(codes == null || codes.length == 0){
					return;
				}
				DZFDouble sum = DZFDouble.ZERO_DBL;
				//从当前期间往前推12个月
				String curr_period = period;
				List<FseJyeVO> listvos = null;
				for(int i=0;i<12;i++){
					if(i>0){
						curr_period = DateUtils.getPreviousPeriod(curr_period);
					}
					listvos = monthmap.get(curr_period);
					if(listvos!=null && listvos.size()>0){
						for(String code:codes){
							for(FseJyeVO votemp:listvos){
								if(votemp.getKmbm().equals(code)){
									sum = SafeCompute.add(sum, votemp.getFsdf());
								}
							}
						}
					}
				}
				if(sum.doubleValue()>vo.getYjz().doubleValue()){
					vo.setIssuccess(DZFBoolean.FALSE);
					vo.setVmemo("连续十二个月收入达到预警值,请检查");
				}
			}
		} catch (Exception e) {
			handleError(vo, e);
		}
		reslist.add(vo);
	}

	private void mssryjCheck(List<QmGzBgVo> reslist, String period,CorpVO cpvo,
							 Map<String, LrbquarterlyVO[]> lrbjbs,Map<String,QmGzBgSetVO> setmap) {
		if(lrbjbs == null || lrbjbs.size() ==0){//利润表不支持的则不显示
			return ;
		}
		QmGzBgSetVO setvo = setmap.get("免税收入预警") == null ? new QmGzBgSetVO(): setmap.get("免税收入预警");
		QmGzBgVo vo = new QmGzBgVo();
		vo.setXm("免税收入预警");
		vo.setIssuccess(DZFBoolean.TRUE);
		vo.setVmemo("通过");
		vo.setYjz(setvo.getNmax() == null ? new DZFDouble(90000.00) : setvo.getNmax());

		try {
			String jd = isJdMonth(period);

			LrbquarterlyVO[] vos =  lrbjbs.get(jd);

			if(vos!=null && vos.length>0){
				DZFDouble sum = DZFDouble.ZERO_DBL;
				for(LrbquarterlyVO votemp: vos){
					DZFDouble tempvalue = DZFDouble.ZERO_DBL;
					if(jd.indexOf("第一季度")>=0){
						tempvalue = votemp.getQuarterFirst();
					}else if(jd.indexOf("第二季度")>=0){
						tempvalue = votemp.getQuarterSecond();
					}else if(jd.indexOf("第三季度")>=0){
						tempvalue = votemp.getQuarterThird();
					}else if(jd.indexOf("第四季度")>=0){
						tempvalue = votemp.getQuarterFourth();
					}

					if(votemp.getXm().indexOf("营业收入")>=0){
						sum = SafeCompute.add(sum, tempvalue);
					}
				}

				if(sum.doubleValue()>vo.getYjz().doubleValue()){
					vo.setIssuccess(DZFBoolean.FALSE);
					vo.setVmemo("季度销售额高于9万免税收入值,请检查");
				}
			}
		} catch (Exception e) {
			handleError(vo, e);
		}

		reslist.add(vo);
	}

	/**
	 * 经营数据分析
	 *
	 * @param pk_corp
	 * @param period
	 */
	private void handJySjFx(Map<String,List<QmGzBgVo>> qmgzbgmap,
							String pk_corp, String period,List<FseJyeVO> curr_fsvos ,CorpVO cpvo,
							Map<String, List<FseJyeVO>> monthmap,Map<String, LrbquarterlyVO[]>  lrbjbs
			,	Map<String,YntCpaccountVO> mapc,Map<String,QmGzBgSetVO> setmap,Integer corpschema) {
		List<QmGzBgVo> reslist = new ArrayList<QmGzBgVo>();
		//增值税税负率
		zzssfl(reslist,period,cpvo,curr_fsvos,setmap,corpschema);
		//所得税税负率
		ssdCheck(reslist,period,lrbjbs,setmap,corpschema);
		//毛利率
		mllCheck(reslist,period,curr_fsvos,cpvo,setmap);
		//资产负债率
		zcfzlCheck(reslist,period,pk_corp,mapc,curr_fsvos,setmap);
		qmgzbgmap.put("jysjfx", reslist);
	}

	/**
	 * 增值税税负率
	 * @param reslist
	 * @param period
	 */
	private void zzssfl(List<QmGzBgVo> reslist, String period,CorpVO cpvo
			,List<FseJyeVO> curr_fsvos,Map<String,QmGzBgSetVO> setmap,Integer corpschema) {
		if(corpschema != DzfUtil.SEVENSCHEMA.intValue()
				&& corpschema != DzfUtil.THIRTEENSCHEMA.intValue()  ){
			return ;
		}

		QmGzBgSetVO setvo = setmap.get("增值税税负率") == null ? new QmGzBgSetVO():  setmap.get("增值税税负率");
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("增值税税负率");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		bgvo.setMin(setvo.getNmin() == null ? DZFDouble.ZERO_DBL:setvo.getNmin());
		bgvo.setMax(setvo.getNmax() == null ? new DZFDouble(100.00):setvo.getNmax());

		try{
			DZFDouble xx = DZFDouble.ZERO_DBL;//销项金额
			DZFDouble jx = DZFDouble.ZERO_DBL;//进项金额
			DZFDouble ywsr = DZFDouble.ZERO_DBL;//主营业务收入
			DZFDouble ld= DZFDouble.ZERO_DBL;//本期留底
			DZFDouble sl = DZFDouble.ZERO_DBL;//增值税税负率

			String[] zyywsrcodes = getZyywsr(cpvo);

			if (zyywsrcodes == null || zyywsrcodes.length == 0) {// 非07 和13的
				return;
			}
			String charname = cpvo.getChargedeptname();

			String queryAccountRule = gl_cpacckmserv.queryAccountRule(cpvo.getPk_corp());

			String xxcode = gl_accountcoderule.getNewRuleCode("22210102", DZFConstant.ACCOUNTCODERULE, queryAccountRule);//销项

			String jxcode = gl_accountcoderule.getNewRuleCode("22210101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);//进项

			String ldcode = gl_accountcoderule.getNewRuleCode("222109", DZFConstant.ACCOUNTCODERULE, queryAccountRule);//留底

			//主营业务收入
			if(curr_fsvos!=null && curr_fsvos.size()>0){
				for(FseJyeVO vo:curr_fsvos){
					if(vo.getKmbm().equals(xxcode)){
						xx = VoUtils.getDZFDouble(vo.getFsdf());
					}
					if(vo.getKmbm().equals(jxcode)){
						jx =VoUtils.getDZFDouble(vo.getFsjf()) ;
					}

					if (vo.getKmbm().equals(ldcode)) {
						ld = VoUtils.getDZFDouble(vo.getQcdf()).doubleValue() < 0 ? VoUtils.getDZFDouble(vo.getQcdf())
								: DZFDouble.ZERO_DBL;
					}

					if(vo.getKmbm().equals(zyywsrcodes[0])){//默认就是一个
						ywsr = VoUtils.getDZFDouble(vo.getFsdf());
					}
				}
				DZFDouble tax = null;
				if("小规模纳税人".equals(charname)){
					tax = xx;
				}else{
					tax = xx.sub(jx).add(ld);
				}
				if (tax.doubleValue() < 0 || ywsr.doubleValue() <= 0) {
					sl = DZFDouble.ZERO_DBL;
				} else {
					sl = tax.div(ywsr).multiply(100);
				}
				if(sl.doubleValue()<bgvo.getMin().doubleValue()){
					bgvo.setVmemo("增值税税负率为"+sl.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏低");
					bgvo.setIssuccess(DZFBoolean.FALSE);
				}
				if(sl.doubleValue()>bgvo.getMax().doubleValue()){
					bgvo.setVmemo("增值税税负率为"+sl.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏高");
					bgvo.setIssuccess(DZFBoolean.FALSE);
				}
				bgvo.setValue(sl.setScale(2, DZFDouble.ROUND_HALF_UP));
			}
		}catch (Exception e){
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	private String[] getZyywsr(CorpVO  cpvo){
		String zyywsrcode= "";
		Integer corpschema = yntBoPubUtil.getAccountSchema(cpvo.getPk_corp());
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {// 2007会计准则
			zyywsrcode = "6001";
		} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则
			zyywsrcode = "5001";
		}
//		else if(corpschema == DzfUtil.POPULARSCHEMA.intValue()){//民间非盈利
//			zyywsrcode = "";
//			return new String[]{"4301","4501"};
//		}
		else {
			return null;
		}
		return new String[]{zyywsrcode};
	}

	private void zcfzlCheck(List<QmGzBgVo> reslist, String period,
							String pk_corp,Map<String,YntCpaccountVO> mapc,
							List<FseJyeVO> curr_fsvos,Map<String, QmGzBgSetVO> map) {
		QmGzBgSetVO setvo = map.get("资产负债率") == null ? new QmGzBgSetVO(): map.get("资产负债率");
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("资产负债率");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		bgvo.setMin(setvo.getNmin() == null ? DZFDouble.ZERO_DBL:setvo.getNmin());
		bgvo.setMax(setvo.getNmax() == null ? new DZFDouble(100.00):setvo.getNmax());


		try {
			String[]  hasyes=new String[]{"N","N","N","N","N"};
			ZcFzBVO[] zcfzbvos =  zxkjReportService.getZcfzVOs(pk_corp, hasyes, mapc, curr_fsvos.toArray(new FseJyeVO[0]));

			if(zcfzbvos!=null && zcfzbvos.length>0){
				DZFDouble zz_sum = DZFDouble.ZERO_DBL;//资产总额
				DZFDouble fz_sum = DZFDouble.ZERO_DBL;//负债总额
				DZFDouble zcfzl = DZFDouble.ZERO_DBL;
				for(ZcFzBVO bvo:zcfzbvos){
					if(!StringUtil.isEmpty(bvo.getZc())
							&& bvo.getZc().equals("资产总计")){
						zz_sum = SafeCompute.add(zz_sum, bvo.getQmye1());
					}
					if(!StringUtil.isEmpty(bvo.getFzhsyzqy())
							&& bvo.getFzhsyzqy().equals("负债合计")){
						fz_sum = SafeCompute.add(fz_sum, bvo.getQmye2());//负债
					}
				}
				zcfzl= fz_sum.div(zz_sum).multiply(100);
				if(zcfzl.doubleValue()<bgvo.getMin().doubleValue()){
					bgvo.setIssuccess(DZFBoolean.FALSE);
					bgvo.setVmemo("资产负债率为"+zcfzl.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏低");
				}
				if(zcfzl.doubleValue()>bgvo.getMax().doubleValue()){
					bgvo.setIssuccess(DZFBoolean.FALSE);
					bgvo.setVmemo("资产负债率为"+zcfzl.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏高");
				}
				bgvo.setValue(zcfzl.setScale(2, DZFDouble.ROUND_HALF_UP));
			}
		} catch (DZFWarpException e) {
			log.error(e.getMessage(), e);
		}
		reslist.add(bgvo);
	}

	private void mllCheck(List<QmGzBgVo> reslist, String period,
						  List<FseJyeVO> curr_fsvos,CorpVO cpvo,Map<String, QmGzBgSetVO> map) {
		QmGzBgSetVO setvo = map.get("毛利率")==null ? new QmGzBgSetVO():map.get("毛利率");
		QmGzBgVo bzbgvo = new QmGzBgVo();
		bzbgvo.setIssuccess(DZFBoolean.TRUE);
		bzbgvo.setXm("毛利率");
		bzbgvo.setVmemo("通过");
		bzbgvo.setMin(setvo.getNmin() == null ? DZFDouble.ZERO_DBL:setvo.getNmin());
		bzbgvo.setMax(setvo.getNmax() == null ? new DZFDouble(100.00):setvo.getNmax());

		try {
			Integer corpschema = yntBoPubUtil.getAccountSchema(cpvo.getPk_corp());

			String srkmbm = "";
			String cbkmbm = "";
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {// 2007会计准则
				srkmbm = "6001";
				cbkmbm = "6401";
			} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则
				srkmbm = "5001";
				cbkmbm = "5401";
			}else{
				return;
			}

			DZFDouble jf = DZFDouble.ZERO_DBL;
			DZFDouble  df = DZFDouble.ZERO_DBL;
			DZFDouble mll = DZFDouble.ZERO_DBL;
			if(curr_fsvos!=null && curr_fsvos.size()>0){
				for(FseJyeVO fsvo:curr_fsvos){
					if(srkmbm.equals(fsvo.getKmbm())){
						df = VoUtils.getDZFDouble(fsvo.getFsdf());
					}
					if(cbkmbm.equals(fsvo.getKmbm())){
						jf = VoUtils.getDZFDouble(fsvo.getFsjf());
					}
				}
			}
			mll = (df.sub(jf)).div(df).multiply(100);
			if(mll.doubleValue()<bzbgvo.getMin().doubleValue()){
				bzbgvo.setVmemo("毛利率为"+mll.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏低");
				bzbgvo.setIssuccess(DZFBoolean.FALSE);
			}
			if(mll.doubleValue()>bzbgvo.getMax().doubleValue()){
				bzbgvo.setVmemo("毛利率为"+mll.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏高");
				bzbgvo.setIssuccess(DZFBoolean.FALSE);
			}
			bzbgvo.setValue(mll.setScale(2, DZFDouble.ROUND_HALF_UP));
		} catch (DZFWarpException e) {
			handleError(bzbgvo, e);
		}

		reslist.add(bzbgvo);
	}

	private void ssdCheck(List<QmGzBgVo> reslist,String period,
						  Map<String, LrbquarterlyVO[]>  lrbjbs,Map<String, QmGzBgSetVO> map,Integer corpschema) {
		if(corpschema != DzfUtil.SEVENSCHEMA.intValue()
				&& corpschema != DzfUtil.THIRTEENSCHEMA.intValue()
				&& corpschema != DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){
			return ;
		}
		QmGzBgSetVO setvo = map.get("所得税税负率") == null ? new QmGzBgSetVO(): map.get("所得税税负率");
		QmGzBgVo bzbgvo = new QmGzBgVo();
		bzbgvo.setIssuccess(DZFBoolean.TRUE);
		bzbgvo.setXm("所得税税负率");
		bzbgvo.setVmemo("通过");
		bzbgvo.setMin(setvo.getNmin() == null ? DZFDouble.ZERO_DBL:setvo.getNmin());
		bzbgvo.setMax(setvo.getNmax() == null ? new DZFDouble(100.00):setvo.getNmax());

		String jd = isJdMonth(period);

		if(!StringUtil.isEmpty(jd)){//季度月份才处理数据
			try {
				LrbquarterlyVO[] vos =  lrbjbs.get(jd);

				DZFDouble  lrze = DZFDouble.ZERO_DBL;//利润总额
				DZFDouble  yysr = DZFDouble.ZERO_DBL;//营业收入
				DZFDouble sfl = DZFDouble.ZERO_DBL;
				if(vos!=null && vos.length>0){
					for(LrbquarterlyVO votemp: vos){
						DZFDouble tempvalue = DZFDouble.ZERO_DBL;
						if(jd.indexOf("第一季度")>=0){
							tempvalue = votemp.getQuarterFirst();
						}else if(jd.indexOf("第二季度")>=0){
							tempvalue = votemp.getQuarterSecond();
						}else if(jd.indexOf("第三季度")>=0){
							tempvalue = votemp.getQuarterThird();
						}else if(jd.indexOf("第四季度")>=0){
							tempvalue = votemp.getQuarterFourth();
						}
						if(votemp.getXm().indexOf("利润总额")>0){
							lrze = SafeCompute.add(lrze, tempvalue);
						}else if(votemp.getXm().indexOf("营业收入")>0){
							yysr = SafeCompute.add(yysr, tempvalue);
						}
					}
				}
				lrze = lrze.multiply(0.25);
				sfl = lrze.div(yysr).multiply(100);

				if(sfl.doubleValue()<bzbgvo.getMin().doubleValue() ){
					bzbgvo.setVmemo("所得税税负率为"+sfl.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏低");
					bzbgvo.setIssuccess(DZFBoolean.FALSE);
				}
				if(sfl.doubleValue()>bzbgvo.getMax().doubleValue()){
					bzbgvo.setVmemo("所得税税负率为"+sfl.setScale(2, DZFDouble.ROUND_HALF_UP)+"%,偏高");
					bzbgvo.setIssuccess(DZFBoolean.FALSE);
				}

				bzbgvo.setValue(sfl.setScale(2, DZFDouble.ROUND_HALF_UP));
			} catch (Exception e) {
				handleError(bzbgvo, e);
			}
			reslist.add(bzbgvo);
		}

	}

	private Map<String, LrbquarterlyVO[]>  getLrbQuarterly(CorpVO cpvo,String period, Object[] kmmxobjs) {
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setBegindate1(cpvo.getBegindate());
		paramvo.setPk_corp(cpvo.getPk_corp());
		paramvo.setEnddate(DateUtils.getPeriodEndDate(period));
		Map<String, LrbquarterlyVO[]> lrbjb = new HashMap<String, LrbquarterlyVO[]>();
		try {
			lrbjb = zxkjReportService.getLRBquarterlyVOs(paramvo, kmmxobjs);
		} catch (DZFWarpException e) {
			log.error(e.getMessage(), e);
		}
		return lrbjb;
	}

	/**
	 * 关键指标
	 *
	 * @param pk_corp
	 * @param period
	 */
	private void handGjZb(Map<String,List<QmGzBgVo>> qmgzbgmap , String pk_corp,
						  String period,List<FseJyeVO> fsvos,	Map<String,YntCpaccountVO> mapc,CorpVO cpvo,Object[] kmmxobjs,Integer corpschema ) {
		List<QmGzBgVo> reslist = new ArrayList<QmGzBgVo>();
		// 年初余额是否平整
		ncCheck(pk_corp,reslist,cpvo);
		// 期末余额是否平整
		qmCheck(reslist,fsvos,period,cpvo);
		// 资产负债是否平衡
		zcfzCheck(reslist,cpvo,mapc,period,kmmxobjs);
		// 资产负债表与利润表勾稽关系是否平衡
		zcfzAndLrbCheck(reslist,cpvo,mapc,period,kmmxobjs, corpschema);
		// 固定资产与总账对账
		gdzcdzCheck(reslist,pk_corp,period,cpvo);
		qmgzbgmap.put("gjzb", reslist);//关键指标
	}

	private void zcfzAndLrbCheck(List<QmGzBgVo> reslist, CorpVO cpvo, Map<String, YntCpaccountVO> mapc, String period,
								 Object[] kmmxobjs,Integer corpschema) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("资产负债表与利润表勾稽关系是否平衡");
		bgvo.setVmemo("通过");
		bgvo.setName("资产负债表与利润表勾稽关系");
		bgvo.setIssuccess(DZFBoolean.TRUE);

		try {
			//重新查询资产负债表数据
			QueryParamVO paramvo = getBasetParamVO(period, cpvo);
			FseJyeVO[]  fsvos =  zxkjReportService.getFsJyeVOs(paramvo, kmmxobjs);

			if (fsvos != null && fsvos.length > 0) {
				DZFDouble wfp_res = getWfpFromZcfz(cpvo,mapc, kmmxobjs,period);// 资产负债表-未分配
				DZFDouble jlr_res = getJlrFromLrb(paramvo, cpvo, fsvos, mapc,corpschema);// 利润表-净利润
				DZFDouble sytz = getSytz(cpvo, corpschema,kmmxobjs,period);
				DZFDouble ce = SafeCompute.add(wfp_res, sytz).sub(jlr_res);
				QmGzBgVo.GjGx gjgx = bgvo.new GjGx();
				gjgx.setCe(ce);
				gjgx.setWfp(SafeCompute.add(wfp_res, sytz) );
				gjgx.setJlr(jlr_res);
				if(ce.doubleValue() !=0){
					bgvo.setIssuccess(DZFBoolean.FALSE);
					bgvo.setVmemo("资产负债表，利润表("+period+"),不平衡");
				}
				bgvo.setGjgx(gjgx);
			}
		} catch (DZFWarpException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	private DZFDouble getSytz(CorpVO cpvo,Integer corpschema,Object[] kmmxobjs,String period) {
		DZFDouble res = DZFDouble.ZERO_DBL;
		if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
			if(kmmxobjs!=null && kmmxobjs.length>0){
				if( kmmxobjs[0]!=null && ((List[]) kmmxobjs[0]).length>0){
					List<KmMxZVO> kvoList = ((List[]) kmmxobjs[0])[0];
					String queryAccountRule = gl_cpacckmserv.queryAccountRule(cpvo.getPk_corp());
					String wfpcode = gl_accountcoderule.getNewRuleCode("410401", DZFConstant.ACCOUNTCODERULE, queryAccountRule);//未分配利润
					//先查损益类科目(410401 未分配利润，4103本年利润)
					Set<String> pzlist = new HashSet<String>();
					for(KmMxZVO mxzvo:kvoList){
						if(!StringUtil.isEmpty(mxzvo.getPk_tzpz_h())){
							if(wfpcode.equals(mxzvo.getKmbm()) || "4103".equals(mxzvo.getKmbm())  ){
								pzlist.add(mxzvo.getPk_tzpz_h());
							}
						}
					}
					//再次循环获取6901科目(以前年度损益)
					String begperiod = period.substring(0,4)+"-01";
					if(pzlist.size()>0){
						for(KmMxZVO mxzvo:kvoList){
							if(!StringUtil.isEmpty(mxzvo.getPk_tzpz_h())
									&& pzlist.contains(mxzvo.getPk_tzpz_h()) && !StringUtil.isEmpty(mxzvo.getRq())
									&& "6901".equals(mxzvo.getKmbm())){
								//是否期间内
								if(begperiod.compareTo(mxzvo.getRq().substring(0, 7))<=0
										&& period.compareTo(mxzvo.getRq().substring(0, 7))>=0){
									res = res.add(SafeCompute.sub(mxzvo.getDf(), mxzvo.getJf()));
								}
							}
						}
					}
				}
			}

		}
		return res;
	}

	private DZFDouble getJlrFromLrb(QueryParamVO vo, CorpVO cpvo,FseJyeVO[] fsevos,Map<String, YntCpaccountVO> mp,Integer corpschema) {
		DZFDouble res = DZFDouble.ZERO_DBL;
		try {
			vo.setQjq(DateUtils.getPeriod(vo.getBegindate1()));
			vo.setQjz(DateUtils.getPeriod(vo.getEnddate()));
			vo.setRptsource("lrb");
			vo.setFirstlevelkms(zxkjReportService.queryLrbKmsFromDaima(cpvo.getPk_corp(),null));
//			LrbVO[] lrbvos = gl_rep_lrbserv.getLrbVosFromFs(vo, mp, cpvo.getPk_corp(), fsevos);
//			vo.setFirstlevelkms(rptsetser.queryLrbKmsFromDaima(cpvo.getPk_corp()));
			LrbVO[] lrbvos = zxkjReportService.getLRBVOs(vo);
			String name = "净利润";
			for (LrbVO lrbvo : lrbvos) {
				if (!StringUtil.isEmpty(lrbvo.getXm()) && lrbvo.getXm().indexOf(name) >= 0) {
					res = VoUtils.getDZFDouble(lrbvo.getBnljje());
					if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){//07 企业会计准则有多个净利润，取第一个
						break;
					}
				}
			}
		} catch (DZFWarpException e) {
			log.error(e.getMessage());
		}
		return res;
	}

	private DZFDouble getWfpFromZcfz(CorpVO cpvo,Map<String, YntCpaccountVO> mapc,Object[] kmmxobjs,String period) {
		QueryParamVO paramvo = getBasetParamVO(period, cpvo);
		paramvo.setBegindate1(DateUtils.getPeriodStartDate(period.substring(0, 4)+"-01"));
		paramvo.setQjq(period.substring(0, 4)+"-01");
		FseJyeVO[] fsvos = zxkjReportService.getFsJyeVOs(paramvo, 1);
		String[] hasyes = new String[] { "N", "N", "N", "N","N" };
		ZcFzBVO[] zcfzbvos = zxkjReportService.getZcfzVOs(cpvo.getPk_corp(), hasyes, mapc, fsvos);
		String name = "未分配利润";
		DZFDouble value = DZFDouble.ZERO_DBL;
		for (ZcFzBVO bvo : zcfzbvos) {
			if (!StringUtil.isEmpty(bvo.getFzhsyzqy()) && bvo.getFzhsyzqy().indexOf(name) >= 0) {
				value = SafeCompute.sub(bvo.getQmye2(), bvo.getNcye2());
				break;
			}
		}
		return value;
	}

	private void gdzcdzCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {
		if (cpvo.getBusibegindate() == null || period.compareTo(DateUtils.getPeriod(cpvo.getBusibegindate())) < 0){
			return;
		}
		QmGzBgVo bgvo = new  QmGzBgVo();
		bgvo.setXm("资产与总账对账");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String,Object> map = getPubParam(cpvo);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("zzdz");
//		bgvo.setUrl("gl/am_zcreport/asst_gl_compr.jsp?"+getPubParam(cpvo));
		bgvo.setName("总账对账");

		ZcdzVO[] zcdzvos;
		try {
			zcdzvos = zczzdzReportService.queryAssetCheckVOs(pk_corp, period);

			if(zcdzvos!=null && zcdzvos.length>0){

				DZFDouble zc_sum = DZFDouble.ZERO_DBL;//资产金额

				DZFDouble zz_sum = DZFDouble.ZERO_DBL;//总账金额

				for(ZcdzVO zcdzvo:zcdzvos){
					zc_sum = SafeCompute.add(zc_sum, zcdzvo.getZcje());
					zz_sum = SafeCompute.add(zz_sum, zcdzvo.getZzje());
				}

				if(zc_sum.sub(zz_sum).doubleValue()!=0){
					bgvo.setVmemo("资产与总账不平");
					bgvo.setIssuccess(DZFBoolean.FALSE);
				}
			}
		} catch (DZFWarpException e) {
			if(e instanceof BusinessException){
				bgvo.setVmemo(e.getMessage());
				bgvo.setIssuccess(DZFBoolean.FALSE);
			}else{
				bgvo.setVmemo("资产与总账不平");
				bgvo.setIssuccess(DZFBoolean.FALSE);
			}
		}
		reslist.add(bgvo);
	}

	private void zcfzCheck(List<QmGzBgVo> reslist,CorpVO cpvo,Map<String,YntCpaccountVO> mapc ,String period,
						   Object[] kmmxobjs) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("资产负债表是否平衡");
		bgvo.setVmemo("通过");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		Map<String, Object> map = getPubParam(cpvo);
		map.put("qj", DateUtils.getPeriodEndDate(period));
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("zcfz-report");
//		bgvo.setUrl("gl/gl_cwreport/gl_rep_zcfz.jsp?"+getPubParam(cpvo)+"&qj="+DateUtils.getPeriodEndDate(period));
		bgvo.setName("资产负债表");

		String[]  hasyes=new String[]{"N","N","N","N","N"};

		try {
			//重新查询资产负债表数据
			QueryParamVO paramvo = getBasetParamVO(period, cpvo);
			FseJyeVO[]  fsvos =  zxkjReportService.getFsJyeVOs(paramvo, kmmxobjs);

			if(fsvos!=null && fsvos.length>0){
				ZcFzBVO[] zcfzbvos =  zxkjReportService.getZcfzVOs(cpvo.getPk_corp(), hasyes, mapc, fsvos);

				if(zcfzbvos!=null && zcfzbvos.length>0){

					String tips =  isZcfzBlance(zcfzbvos,period);

					if(tips!=null && tips.length()>0){
						bgvo.setIssuccess(DZFBoolean.FALSE);
						bgvo.setVmemo(tips);
					}
				}
			}
		} catch (DZFWarpException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}


	public String isZcfzBlance(ZcFzBVO[] dataVOS,String period) {
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
			message.append("期末余额,");
		}
		if (ncye1.setScale(2, DZFDouble.ROUND_HALF_UP).sub(ncye2.setScale(2, DZFDouble.ROUND_HALF_UP))
				.doubleValue() != 0) {
			message.append("年初余额，");
		}

		if (message.toString().trim().length() > 0) {
			return "资产负债("+period+")：" + message.substring(0, message.toString().trim().length() - 1) + "不平";
		} else {
			return null;
		}
	}


	/**
	 * 期末余额是否平衡
	 * @param reslist
	 * @param fsvos
	 */
	private void qmCheck(List<QmGzBgVo> reslist, List<FseJyeVO> fsvos,String period,CorpVO cpvo) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("期末余额是否平衡");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> map = getPubParam(cpvo);
		map.put("qj", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("fsyeb-report");
//		bgvo.setUrl("gl/gl_kmreport/gl_rep_fsyeb.jsp?"+getPubParam(cpvo)+"&qj="+period);
		bgvo.setName("发生额及余额表");
		bgvo.setBz(" *");

		try {
			//取合计值
			if(fsvos!=null && fsvos.size()>0){
				DZFDouble qcjfhj = DZFDouble.ZERO_DBL;
				DZFDouble qcdfhj = DZFDouble.ZERO_DBL;
				DZFDouble fsjfhj = DZFDouble.ZERO_DBL;
				DZFDouble fsdfhj = DZFDouble.ZERO_DBL;
				DZFDouble jftotalhj = DZFDouble.ZERO_DBL;
				DZFDouble dftotalhj =DZFDouble.ZERO_DBL;
				DZFDouble qmjfhj = DZFDouble.ZERO_DBL;
				DZFDouble qmdfhj = DZFDouble.ZERO_DBL;
				for (FseJyeVO fsevo : fsvos) {
					if(fsevo.getAlevel() == 1){
						qcjfhj = SafeCompute.add(qcjfhj, fsevo.getQcjf());
						qcdfhj = SafeCompute.add(qcdfhj, fsevo.getQcdf());
						fsjfhj = SafeCompute.add(fsjfhj, fsevo.getFsjf());
						fsdfhj = SafeCompute.add(fsdfhj, fsevo.getFsdf());
						jftotalhj = SafeCompute.add(jftotalhj, fsevo.getJftotal());
						dftotalhj = SafeCompute.add(dftotalhj, fsevo.getDftotal());
						qmjfhj = SafeCompute.add(qmjfhj, fsevo.getQmjf());
						qmdfhj = SafeCompute.add(qmdfhj, fsevo.getQmdf());
					}
				}

				DZFDouble ce = qmjfhj.sub(qmdfhj);
				if(ce.doubleValue() !=0){
					bgvo.setVmemo("期末余额借贷方不平衡,差额"+ce.doubleValue());
					bgvo.setIssuccess(DZFBoolean.FALSE);
				}
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void ncCheck(String pk_corp, List<QmGzBgVo> reslist,CorpVO cpvo) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("年初余额是否平衡");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> map = getPubParam(cpvo);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("km-qc");
//		bgvo.setUrl("gl/gl_qcset/gl_qcye.jsp?"+getPubParam(cpvo));
		bgvo.setName("科目期初");
		bgvo.setBz(" *");
		try {
			SsphRes res = gl_qcyeserv.ssph(pk_corp);


			if(res!=null){
				StringBuffer tips = new StringBuffer();

				if(res.getYearres()!=null && "不平衡".equals(res.getYearres())){
					tips.append("本年期初不平整,差额"+res.getYearce().doubleValue());
				}

				if(res.getMonthres()!=null && "不平衡".equals(res.getMonthres())){
					tips.append("本月期初不平整,差额"+res.getMonthce().doubleValue());
				}

				if(tips.length()>0){
					bgvo.setIssuccess(DZFBoolean.FALSE);
					bgvo.setVmemo(tips.toString());
				}

			}
		} catch (DZFWarpException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	/**
	 * 往来异常
	 *
	 * @param pk_corp
	 * @param period
	 */
	private void handWl(Map<String,List<QmGzBgVo>> qmgzbgmap, String pk_corp,
						String period,CorpVO cpvo,Map<String,YntCpaccountVO> mapc) {
		//往来挂账超过一年,如果1122 和2202 科目及下级科目有超过一年的挂账提示 XX客户或供应商挂账已超过一年
		List<QmGzBgVo>  reslist = new ArrayList<QmGzBgVo>();

		wlCheck(reslist,period,pk_corp,cpvo,mapc);

		qmgzbgmap.put("wlyc",reslist);

	}

	private void wlCheck(List<QmGzBgVo> reslist, String period,
						 String pk_corp,CorpVO cpvo,Map<String,YntCpaccountVO> mapc) {
		QmGzBgVo vo = new QmGzBgVo();
		vo.setIssuccess(DZFBoolean.TRUE);
		vo.setVmemo("通过");
		vo.setXm("往来挂账超过一年");
		Map<String, Object> map = getPubParam(cpvo);
		map.put("kmbm", "1122");
		map.put("fzlb", "-1");
		map.put("corp", pk_corp);
		map.put("enddate", DateUtils.getPeriodEndDate(period));
		map.put("zllx", 2);
		map.put("unit", 360);
		map.put("jz_date", cpvo.getBegindate());
		map.put("pk_age", "00000100000000ZZnTcA002K");
		vo.setParamstr(JsonUtils.serialize(map));
		vo.setUrl("wlzlye-report");
//		vo.setUrl("gl/gl_kmreport/gl_rep_zlyeb.jsp?"+getPubParam(cpvo));
		vo.setName("往来账龄余额");

		try {
			AgeReportQueryVO queryvo = new AgeReportQueryVO();
			queryvo.setPk_corp(pk_corp);
			queryvo.setEnd_date(DateUtils.getPeriodEndDate(period));
			queryvo.setAccount_code("1122");
			queryvo.setFzlb(-1);
			queryvo.setAge_type(2);
			queryvo.setAge_unit(360);
			queryvo.setJz_date(cpvo.getBegindate());
			queryvo.setPk_age("00000100000000ZZnTcA002K");

			AgeReportResultVO resvo_1122 = zxkjReportService.query(queryvo);

			List<String> fznames_km = new ArrayList<String>();
			List<String> fznames_fz = new ArrayList<String>();

			handwl(resvo_1122,"1122",fznames_km,fznames_fz,mapc);

			queryvo.setAccount_code("2202");
			AgeReportResultVO resvo_2202 = zxkjReportService.query(queryvo);

			handwl(resvo_2202,"2202",fznames_km,fznames_fz,mapc);

			String values = handwlTips(fznames_km, fznames_fz);

			if(!StringUtil.isEmpty(values)){
				vo.setIssuccess(DZFBoolean.FALSE);
				vo.setVmemo(values+"挂账已超过一年");
			}
		} catch (DZFWarpException e) {
			handleError(vo, e);
		}

		reslist.add(vo);
		//往来挂账混乱
		String sql = "select VDIRECT,vcode,zy, sum(JFMNY) as JFMNY, sum(DFMNY) as DFMNY from (SELECT decode(substr(vcode, 0, 1),'2',1,0) as VDIRECT, NVL (JFMNY, 0) AS JFMNY, NVL (DFMNY, 0) AS DFMNY, substr(vcode, 0, 4) as vcode, nvl(FZHSX1,0) || ','|| nvl(FZHSX2,0)|| ','||nvl(FZHSX3,0)|| ','||nvl(FZHSX4,0)|| ','||nvl(FZHSX5,0)|| ','||nvl(FZHSX6,0)|| ','||nvl(FZHSX7,0)|| ','||nvl(FZHSX8,0)|| ','||nvl(FZHSX9,0)|| ','||nvl(FZHSX10,0) as zy FROM YNT_TZPZ_B b WHERE PK_CORP = ? and  NVL (DR, 0) = 0 AND (vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ?) and EXISTS (select 1 from YNT_TZPZ_H h where b.PK_TZPZ_H = h.PK_TZPZ_H and h.period <= ?) ) where zy != '0,0,0,0,0,0,0,0,0,0' group by VDIRECT,vcode,zy";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<String> wlList = null;
		//只做了 小企业 企业会计准则 企业会计制度
		if(TaxRptConst.KJQJ_2007.equals(cpvo.getCorptype())){
			wlList = Arrays.asList("1122","2203","2202","1123","1221","2241");
		}else if(TaxRptConst.KJQJ_2013.equals(cpvo.getCorptype())){
			wlList = Arrays.asList("1122","2203","2202","1123","1221","2241");
		}else if(TaxRptConst.KJQJ_QYKJZD.equals(cpvo.getCorptype())){
			wlList = Arrays.asList("1131","1151","2151","1151","1133","2181");
		}

		if(wlList == null)  return;

		for(String vcode : wlList){
			sp.addParam(vcode+"%");
		}

		List<FzhsqcVO> fzhsqcVOList = queryWlFzhsqcListByCorpAndAccount(pk_corp,sp);

		sp.addParam(period);
		List<TzpzBVO> tzpzBVOS = (List<TzpzBVO>) singleObjectBO.executeQuery(
				sql, sp, new BeanListProcessor(TzpzBVO.class));
		Map<String, DZFDouble[]> fzyeData  = queryFzyeData(fzhsqcVOList, tzpzBVOS, wlList);

		filterGzhlData(fzyeData);
		QmGzBgVo vo1 = new QmGzBgVo();
		vo1.setIssuccess(new DZFBoolean(fzyeData.isEmpty()));
		vo1.setVmemo("通过");
		vo1.setName("往来挂账混乱"+ StringUtils.join(wlList,","));
		try {
			if(!vo1.getIssuccess().booleanValue()){
				StringBuilder sb = new StringBuilder();
				SQLParameter sp1 = new SQLParameter();
				sb.append(" select PK_AUACOUNT_B, name, PK_AUACOUNT_H,code from ynt_fzhs_b where ");
				sb.append(" pk_corp = ? ");
				sp1.addParam(pk_corp);
				List<AuxiliaryAccountBVO> rs = (List<AuxiliaryAccountBVO>) singleObjectBO.executeQuery(sb.toString(), sp1,
						new BeanListProcessor(AuxiliaryAccountBVO.class));

				Map<String, AuxiliaryAccountBVO> auxiliaryAccountMapping = new HashMap<>();

				for(AuxiliaryAccountBVO auxiliaryAccountBVO : rs){
					auxiliaryAccountMapping.put(auxiliaryAccountBVO.getPrimaryKey(),auxiliaryAccountBVO);
				}
				vo1.setUrl(createShowData(fzyeData, auxiliaryAccountMapping, period));
				StringBuilder sf = new StringBuilder();

				vo1.setVmemo("存在挂账混乱的往来"+fzyeData.size()+"家未通过");
			} else {
				vo1.setName("辅助余额表");
				vo1.setUrl("auxiliary-balance-report");
			}
			map = getPubParam(cpvo);
			map.put("begindate", DateUtils.getPeriodStartDate(period));
			map.put("enddate", DateUtils.getPeriodEndDate(period));
			map.put("qjq", period);
			map.put("qjz", period);
			map.put("corpIds", pk_corp);
			vo1.setParamstr(JsonUtils.serialize(map));
			vo1.setXm("往来挂账混乱");
		} catch (DAOException e) {
			handleError(vo1, e);
		}
		reslist.add(vo1);
	}

	private String createShowData(Map<String, DZFDouble[]> data, Map<String, AuxiliaryAccountBVO> auxiliaryAccountMapping, String period) {
		StringBuilder sb = new StringBuilder();
		sb.append(createShowDataByMap(data, auxiliaryAccountMapping, period));
		return sb.toString();
	}

	private String createShowDataByMap(Map<String, DZFDouble[]> data, Map<String, AuxiliaryAccountBVO> auxiliaryAccountMapping, String period) {

		List<Map.Entry<String,DZFDouble[]>> list = new ArrayList<Map.Entry<String,DZFDouble[]>>(data.entrySet());

		Collections.sort(list, new MyComparator(auxiliaryAccountMapping));

		StringBuilder sb = new StringBuilder();

		for(Map.Entry<String,DZFDouble[]> entry : list){
			String pk = entry.getKey();
			DZFDouble[] value = entry.getValue();
			String name = "";
			if(name == null) continue;
			boolean isSetDeafault = false;
			String[] pk_arr = pk.split(",");
			for(int i = 0; i < pk_arr.length; i++){
				if("0".equals(pk_arr[i])){
					continue;
				}

				if(!isSetDeafault){
					sb.append(pk_arr[i]+","+(i+1)+",");
					sb.append(auxiliaryAccountMapping.get(pk_arr[i]).getCode()+",");
					isSetDeafault = true;
				}
				name += auxiliaryAccountMapping.get(pk_arr[i]).getName()+"_";
			}

			sb.append(name.substring(0,name.length()-1)+",");
			sb.append(period+",");
			sb.append(value[0] == null? "0," : value[0].toString()+",");
			sb.append(value[1] == null? "0," : value[1].toString()+",");
			sb.append(value[2] == null? "0," : value[2].toString()+",");
			sb.append(value[3] == null? "0," : value[3].toString()+",");
			sb.append(value[4] == null? "0," : value[4].toString()+",");
			sb.append(value[5] == null? "0,#" : value[5].toString()+",#");

		}

		if(StringUtil.isEmpty(sb.toString())) return "";

		String result = sb.toString();

		return result.substring(0,result.length() -2);
	}

	private boolean isAllNotZero(DZFDouble a, DZFDouble b){
		a = a == null ? DZFDouble.ZERO_DBL : a;
		b = b == null ? DZFDouble.ZERO_DBL : b;
		return !DZFDouble.ZERO_DBL.equals(a) && !DZFDouble.ZERO_DBL.equals(b);
	}

	private void filterGzhlData(Map<String, DZFDouble[]> data) {
		Iterator<Map.Entry<String, DZFDouble[]>> it = data.entrySet().iterator();
		while(it.hasNext()){
			Map.Entry<String, DZFDouble[]> entry = it.next();
			DZFDouble[] value = entry.getValue();
			if(!(isAllNotZero(value[0], value[1]) ||  isAllNotZero(value[2], value[3]) || isAllNotZero(value[4], value[5]))){
				it.remove();
			}
		}
	}

	private DZFDouble sumYe(Integer direct, DZFDouble jfmny, DZFDouble dfmny, DZFDouble value){
		if(direct == 0) { // 借方   加借方减贷方
			value = SafeCompute.add(value, jfmny);
			value = SafeCompute.sub(value, dfmny);
		}else{
			value = SafeCompute.add(value, dfmny);
			value = SafeCompute.sub(value, jfmny);
		}
		return value;
	}

	private Map<String, DZFDouble[]> queryFzyeData(List<FzhsqcVO> fzhsqcVOList, List<TzpzBVO> tzpzBVOS, List<String> wlList) {
		Map<String, DZFDouble[]> result = new HashMap<>();

		for(FzhsqcVO fzhsqcVO : fzhsqcVOList){
			Integer direct = fzhsqcVO.getDirect();
			DZFDouble jfmny = direct == 0 ? fzhsqcVO.getThismonthqc() : DZFDouble.ZERO_DBL;
			DZFDouble dfmny = direct == 1 ? fzhsqcVO.getThismonthqc() : DZFDouble.ZERO_DBL;
			DZFDouble[] value = result.get(fzhsqcVO.getMemo());
			if(value == null){
				value = new DZFDouble[6];
			}
			value[wlList.indexOf(fzhsqcVO.getVcode())] = sumYe(direct, jfmny, dfmny, value[wlList.indexOf(fzhsqcVO.getVcode())]);
			result.put(fzhsqcVO.getMemo(),value);
		}

		for(TzpzBVO tzpzBVO : tzpzBVOS){
			Integer direct = tzpzBVO.getVdirect();
			DZFDouble[] value = result.get(tzpzBVO.getZy());
			if(value == null){
				value = new DZFDouble[6];
			}

			value[wlList.indexOf(tzpzBVO.getVcode())] = sumYe(direct, tzpzBVO.getJfmny(), tzpzBVO.getDfmny(), value[wlList.indexOf(tzpzBVO.getVcode())]);
			result.put(tzpzBVO.getZy(),value);
		}
		return result;
	}


	/*
		获取辅助核算期初
		gzx
	 */
	private List<FzhsqcVO> queryWlFzhsqcListByCorpAndAccount(String pk_corp, SQLParameter sp){
		String sql = "select vcode,direct, sum(thismonthqc) as thismonthqc, memo from (select substr(a.vcode, 0, 4) as vcode, a.direct, a.thismonthqc, nvl(FZHSX1,0) || ','|| nvl(FZHSX2,0)|| ','||nvl(FZHSX3,0)|| ','||nvl(FZHSX4,0)|| ','||nvl(FZHSX5,0)|| ','||nvl(FZHSX6,0)|| ','||nvl(FZHSX7,0)|| ','||nvl(FZHSX8,0)|| ','||nvl(FZHSX9,0)|| ','||nvl(FZHSX10,0) as memo from ynt_fzhsqc a where a.pk_corp = ?  AND (vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ? OR vcode LIKE ?) and nvl(a.dr, 0) = 0 and a.thismonthqc is not null and a.thismonthqc <> 0 ) where memo != '0,0,0,0,0,0,0,0,0,0' group by vcode,direct,memo";
		return (List<FzhsqcVO>) singleObjectBO.executeQuery(
				sql, sp, new BeanListProcessor(FzhsqcVO.class));
	}

	private String handwl(AgeReportResultVO resvo_1122,String kmbm,List<String> fznames_km,List<String> fznames_fz,Map<String, YntCpaccountVO> cpamap) {
		Object obj =  resvo_1122.getResult();
		if(obj!=null){
			List<AgeBalanceVO> rsList = (List<AgeBalanceVO>) obj;
			if(rsList!=null && rsList.size()>0){
				YntCpaccountVO cpaccoutnvo = null;
				Map<String, DZFDouble> periodmap = null;
				for(int i = 0;i<rsList.size();i++){
					periodmap = rsList.get(i).getPeriod_mny();
					if(periodmap!=null && periodmap.size()>0){
						DZFDouble value = VoUtils.getDZFDouble(periodmap.get("1-2"));
						value = SafeCompute.add(value, periodmap.get("2-3"));
						value = SafeCompute.add(value, periodmap.get("3-"));
						if(value!=null && value.doubleValue()!=0){
							if(!StringUtil.isEmpty(rsList.get(i).getFzhsx1()) &&  !fznames_fz.contains(rsList.get(i).getFzhsx1())){
								fznames_fz.add(rsList.get(i).getFzhsx1());
								continue;
							}
							if(!StringUtil.isEmpty(rsList.get(i).getFzhsx2()) &&  !fznames_fz.contains(rsList.get(i).getFzhsx2())){
								fznames_fz.add(rsList.get(i).getFzhsx2());
								continue;
							}
							cpaccoutnvo = cpamap.get(rsList.get(i).getAccount_code());
							if(!StringUtil.isEmpty(rsList.get(i).getAccount_code())
									&& !fznames_km.contains(rsList.get(i).getAccount_name())){
								if(cpaccoutnvo!=null && cpaccoutnvo.getIsleaf()!=null
										&& cpaccoutnvo.getIsleaf().booleanValue()){
									fznames_km.add(rsList.get(i).getAccount_name());
								}
							}
						}
					}
				}
//				handwlTips(fznames_km, fznames_fz);
			}
		}
		return "";
	}

	private String handwlTips(List<String> fznames_km, List<String> fznames_fz) {
		StringBuffer tips = new StringBuffer();
		StringBuffer tiptemp = new StringBuffer();
		if(fznames_km.size()>0){
			for(String str:fznames_km){
				tiptemp.append(str+",");
			}
			if(tiptemp.length()>0){
				tips.append("末级科目"+tiptemp.substring(0, tiptemp.length()-1)+" ") ;
			}
		}
		if(fznames_fz.size()>0 ){
			tiptemp = new StringBuffer();
			for(String str:fznames_fz){
				tiptemp.append(str+"、");
			}
			if(tiptemp.length()>0){
				tips.append("辅助"+tiptemp.substring(0, tiptemp.length()-1)+"") ;
			}
		}
		if(tips.length()>0){
			return tips.toString();
		}

		return "";
	}

	/**
	 * 余额异常
	 *
	 * @param period
	 */
	private void handYe(Map<String,List<QmGzBgVo>> qmgzbgmap, List<FseJyeVO> fsvos,CorpVO cpvo,String period) {
		List<QmGzBgVo> reslist = new ArrayList<QmGzBgVo>();
		// 库存现金(借方)(1001),期末余额是否有赤字
		kcxjCheck(fsvos,reslist,period,cpvo);
		// 银行存款(借方)(1002) 金额是否有赤字
		yhckCheck(fsvos,reslist,period,cpvo);
		// 原材料(借方)(1403) 金额是否有赤字
		yclCheck(fsvos,reslist,period,cpvo);
		// 库存商品(借方)(1405)金额是否有赤字
		kcspCheck(fsvos,reslist,period,cpvo);
		qmgzbgmap.put("yeyc", reslist);//余额异常
	}

	private void kcspCheck(List<FseJyeVO> fsvos, List<QmGzBgVo> reslist,String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("库存商品");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_kmreport/gl_rep_fsyeb.jsp?"+getPubParam(cpvo)+"&kms_first=1405&kms_last=1405&kms_first_name=库存商品&kms_last_name=库存商品&qj="+period);
		Map<String, Object> map = getPubParam(cpvo);
		map.put("kms_first", "1405");
		map.put("kms_last", "1405");
		map.put("kms_first_name", "库存商品");
		map.put("kms_last_name", "库存商品");
		map.put("qj", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("fsyeb-report");
		bgvo.setName("发生额及余额表");

		kmCheck(fsvos, bgvo,"1405");

		reslist.add(bgvo);
	}

	private void yclCheck(List<FseJyeVO> fsvos, List<QmGzBgVo> reslist,String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("原材料");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_kmreport/gl_rep_fsyeb.jsp?"+getPubParam(cpvo)+"&kms_first=1403&kms_last=1403&kms_first_name=原材料&kms_last_name=原材料&qj="+period);
		Map<String, Object> map = getPubParam(cpvo);
		map.put("kms_first", "1403");
		map.put("kms_last", "1403");
		map.put("kms_first_name", "原材料");
		map.put("kms_last_name", "原材料");
		map.put("qj", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("fsyeb-report");
		bgvo.setName("发生额及余额表");

		kmCheck(fsvos, bgvo,"1403");

		reslist.add(bgvo);
	}

	private void yhckCheck(List<FseJyeVO> fsvos, List<QmGzBgVo> reslist,String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("银行存款");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_kmreport/gl_rep_fsyeb.jsp?"+getPubParam(cpvo)+"&kms_first=1002&kms_last=1002&kms_first_name=银行存款&kms_last_name=银行存款&qj="+period);
		Map<String, Object> map = getPubParam(cpvo);
		map.put("kms_first", "1002");
		map.put("kms_last", "1002");
		map.put("kms_first_name", "银行存款");
		map.put("kms_last_name", "银行存款");
		map.put("qj", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("fsyeb-report");
		bgvo.setName("发生额及余额表");

		kmCheck(fsvos, bgvo,"1002");

		reslist.add(bgvo);
	}

	private void kcxjCheck(List<FseJyeVO> fsvos, List<QmGzBgVo> reslist,String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("库存现金");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> map = getPubParam(cpvo);
		map.put("kms_first", "1001");
		map.put("kms_last", "1001");
		map.put("kms_first_name", "库存现金");
		map.put("kms_last_name", "库存现金");
		map.put("qj", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("fsyeb-report");
//		bgvo.setUrl("gl/gl_kmreport/gl_rep_fsyeb.jsp?"+getPubParam(cpvo)+"&kms_first=1001&kms_last=1001&kms_first_name=库存现金&kms_last_name=库存现金&qj="+period);
		bgvo.setName("发生额及余额表");

		kmCheck(fsvos, bgvo,"1001");

		reslist.add(bgvo);
	}

	private void kmCheck(List<FseJyeVO> fsvos, QmGzBgVo bgvo,String kmbm) {
		try {
			if(fsvos!=null && fsvos.size()>0){
				for(FseJyeVO fsvo:fsvos){
					if(fsvo.getKmbm().equals(kmbm)
							&& VoUtils.getDZFDouble(fsvo.getQmjf()).doubleValue()<0){
						bgvo.setVmemo("余额有赤字");
						bgvo.setIssuccess(DZFBoolean.FALSE);
					}
				}
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
	}

	/**
	 * 财务处理完整性
	 *
	 * @param pk_corp
	 * @param period
	 */
	public void handCwcl(Map<String,List<QmGzBgVo>> qmgzbgmap, String pk_corp, String period,CorpVO cpvo) {
		List<QmGzBgVo> reslist = new ArrayList<QmGzBgVo>();

		List<TzpzHVO> tzpzlist = qryPeriodPz(pk_corp, period);
		// 凭证断号及号码
		pzhCheck(reslist, tzpzlist,period,cpvo);
		//凭证记账
		pzjzCheck(reslist, tzpzlist,period,cpvo);
		// 待处理暂存凭证
		tempPzCheck(reslist,tzpzlist,period,pk_corp,cpvo);
		// 销项发票生成凭证
		xsfpCheck(reslist,pk_corp,period,cpvo);
		// 进项发票生成凭证
		jxfpCheck(reslist,pk_corp,period,cpvo);
		//银行对账单生成凭证
		bankCheck(reslist,pk_corp,period,cpvo);
		//票据生成凭证
		billCheck(reslist,pk_corp,period,cpvo);
		// 待处理图片
//        imageCheck(reslist,pk_corp,period,cpvo);
		// 库存单据未生成凭证
		inventoryCheck(reslist,cpvo,period);
		// 成本结转
		// 期末调汇
		// 计提折旧
		// 增值税结转
		// 计提税金及附加
		// 计提所得税
		// 损益结转
		qmclCheck(reslist,pk_corp,period,cpvo);
		// 工资薪金计提
		gzCheck(reslist,pk_corp,period,cpvo);
		//关单
//        gdCheck(reslist,pk_corp,period,cpvo);
		//资产转总账
		zcToVoucherCheck(reslist,pk_corp,period,cpvo);

		qmgzbgmap.put("cwcl", reslist);//财务处理完整性
	}

	private void billCheck(List<QmGzBgVo> reslist, String pk_corp, String period, CorpVO cpvo) {

		if (cpvo.getIschannel() != null && cpvo.getIschannel().booleanValue()) {// 加盟商不显示
			return;
		}
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("票据生成凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String,Object> map = new HashMap<>();
		map.put("period", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("billWorkbench");
//		bgvo.setUrl("zncs/billworkbench.html?period=" + period); // 链接到票据工作台
		bgvo.setName("票据工作台");
		try{
			Integer count = billcategory.checkInvoiceForGz(pk_corp, period); // 调智能产品线接口，得到检查期未生成凭证的票据张数
			if (count != null && count.intValue() > 0) {
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("有" + count + "张票据还未生成凭证");
			}
		}catch (Exception e){
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	/**
	 * 资产是否转总账
	 * @param reslist
	 * @param pk_corp
	 * @param period
	 * @param cpvo
	 */
	private void zcToVoucherCheck(List<QmGzBgVo> reslist, String pk_corp, String period, CorpVO cpvo) {
		if (cpvo.getBusibegindate() == null || period.compareTo(DateUtils.getPeriod(cpvo.getBusibegindate()))<0) {
			return;
		}
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("资产转总账");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> map = new HashMap<>();
		map.put("startDate",DateUtils.getPeriodStartDate(period));
		map.put("endDate",DateUtils.getPeriodEndDate(period));
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("zckp");
//		bgvo.setUrl("am/am_zcgl/am_kpgl.jsp?startDate=" + DateUtils.getPeriodStartDate(period) + "&endDate="
//				+ DateUtils.getPeriodEndDate(period));
		bgvo.setName("资产卡片");

		try {
			StringBuffer qrysql = new StringBuffer();
			qrysql.append(" select count(1)  ");
			qrysql.append("  from " + AssetcardVO.TABLE_NAME);
			qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
			qrysql.append(" and substr(period,0,7) = ?  ");
			qrysql.append(" and nvl(isperiodbegin,'N') = 'N' and pk_voucher is null ");// 未转总账的
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(period);

			BigDecimal value = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());
			if (value != null && value.intValue() > 0) {
				bgvo.setVmemo("有" + value.intValue() + "张资产卡片未转总账");
				bgvo.setIssuccess(DZFBoolean.FALSE);
			}
		} catch (DAOException e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void gdCheck(List<QmGzBgVo> reslist, String pk_corp, String period, CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("关单生成凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		bgvo.setUrl("gl/gl_pjgl/customsForm.jsp?"+getPubParam(cpvo));
		bgvo.setName("关单");

		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();

		qrysql.append(" select count(1) ");
		qrysql.append("	  from ynt_customsform t1 ");
		qrysql.append(" where t1.export_date like ? ");
		qrysql.append("   and t1.pk_corp = ? ");
		qrysql.append("   and nvl(t1.dr, 0) = 0 ");
		qrysql.append("   and pk_voucher is null ");


		sp.addParam(period+"%");
		sp.addParam(pk_corp);

		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());

		if(count!=null && count.intValue()>0){
			bgvo.setIssuccess(DZFBoolean.FALSE);
			bgvo.setVmemo("关单未生成凭证");
		}

		reslist.add(bgvo);
	}

	private void pzjzCheck(List<QmGzBgVo> reslist, List<TzpzHVO> tzpzlist, String period, CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("未记账凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		bgvo.setUrl("voucher-manage");
		Map<String, Object> parammap = getPubParam(cpvo);
		parammap.put("pz_status",11);
		parammap.put("serdate","serDay");
		parammap.put("pzbegdate",DateUtils.getPeriodStartDate(period));
		parammap.put("pzenddate",DateUtils.getPeriodEndDate(period));
		parammap.put("beginPeriod", period);
		parammap.put("endPeriod", period);
		bgvo.setParamstr(JsonUtils.serialize(parammap));
//		bgvo.setUrl("gl/gl_pzgl/gl_pzgl.jsp?"+getPubParam(cpvo)+"&pz_status=11&serdate=serDay&pzbegdate="+DateUtils.getPeriodStartDate(period)+"&pzenddate="+DateUtils.getPeriodEndDate(period));
		bgvo.setName("凭证管理");
		bgvo.setBz(" *");
		try{
			if(tzpzlist!=null && tzpzlist.size()>0){
				Integer count = 0;
				for(TzpzHVO hvo:tzpzlist){
					if(hvo.getIshasjz()==null || !hvo.getIshasjz().booleanValue() ){
						count++;
					}
				}
				if(count>0){
					bgvo.setIssuccess(DZFBoolean.FALSE);
					bgvo.setVmemo("存在"+count+"张未记账凭证");
				}
			}
		}catch (Exception e){
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	/**
	 * 工资处理
	 * @param reslist
	 * @param pk_corp
	 * @param period
	 */
	private void gzCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {
		//无工资表数据则不显示
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select count(1)");
		qrysql.append(" from ynt_salaryreport t1 ");
		qrysql.append(" where t1.qj = ? ");
		qrysql.append("   and t1.pk_corp = ? ");
		qrysql.append("   and nvl(t1.dr, 0) = 0 ");
		sp.addParam(period);
		sp.addParam(pk_corp);
		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());
		if(count == null || count.intValue() ==0){
			return;
		}

		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("工资薪金计提");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> map = getPubParam(cpvo);
		map.put("period",period);
		map.put("billtype", "01");
		map.put("pk_corp", pk_corp);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("salary-report");
//		bgvo.setUrl("gl/gl_gzgl/gl_gzb2.jsp?"+getPubParam(cpvo)+"&period="+period+"&billtype=01&pk_corp="+pk_corp);
		bgvo.setName("工资表");
		try {
			qrysql = new StringBuffer();
			qrysql.append(" select count(1)");
			qrysql.append(" from ynt_salaryreport t1 ");
			qrysql.append(" where t1.qj = ? ");
			qrysql.append("   and t1.pk_corp = ? ");
			qrysql.append("   and nvl(t1.dr, 0) = 0 ");
			qrysql.append("   and not exists (select 1 ");
			qrysql.append("          from ynt_tzpz_h h ");
			qrysql.append("         where h.sourcebilltype = t1.pk_corp || t1.qj || 'gzjt' ");
			qrysql.append("           and nvl(h.dr, 0) = 0 ");
			qrysql.append("          and h.pk_corp = ? ) ");
			sp.clearParams();
			sp.addParam(period);
			sp.addParam(pk_corp);
			sp.addParam(pk_corp);
			count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());
			if(count!=null && count.intValue()>0){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("工资薪金未计提");
			}
		} catch (DAOException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	/**
	 * 期末结转计算
	 * @param reslist
	 * @param pk_corp
	 * @param period
	 */
	private void qmclCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {

		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select * ");
		qrysql.append("  from  ynt_qmcl tt   ");
		qrysql.append(" where tt.pk_corp = ?    ");
		qrysql.append("   and tt.period = ? ");
		qrysql.append(" and nvl(tt.dr,0)=0   ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<QmclVO> qmclvos  = (List<QmclVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(QmclVO.class));

		QmclVO qmclvo = null;
		if(qmclvos!=null && qmclvos.size()>0){
			qmclvo = qmclvos.get(0);//默认取第一条
		}

		cbjzCheck(qmclvo,pk_corp,period,reslist,cpvo);//成本结转

		hdtzCheck(qmclvo,pk_corp,period,reslist,cpvo);//期末调汇

		jtzjCheck(qmclvo,pk_corp,period,reslist,cpvo);//计提折旧

		zzsjz(qmclvo,pk_corp,period,reslist,cpvo);//增值税结转

		jtsjfj(qmclvo,reslist,period,cpvo);//计提税金附加

		jtsds(qmclvo,reslist,period,cpvo);//计提所得税

		qjsyjz(qmclvo,reslist,pk_corp,period,cpvo);//损益结转
	}

	private void putQmclBgvo(QmGzBgVo bgvo,String period,CorpVO cpvo) {
		Map<String, Object> map = getPubParam(cpvo);
		map.put("rq", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("qmcl");
	}

	private void qjsyjz(QmclVO qmclvo, List<QmGzBgVo> reslist,String pk_corp,String period,CorpVO cpvo) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("损益结转");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("期末结转");
		bgvo.setBz(" *");
		// 赋值url地址
		putQmclBgvo(bgvo, period, cpvo);

		try {
			if(qmclvo==null || qmclvo.getIsqjsyjz()== null ||
					!qmclvo.getIsqjsyjz().booleanValue()){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("期间损益未结转");
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void jtsds(QmclVO qmclvo, List<QmGzBgVo> reslist,String period,CorpVO cpvo) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("计提所得税");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("期末结转");
		// 赋值url地址
		putQmclBgvo(bgvo, period, cpvo);

		String month = period.substring(5, 7);

		if (!"03".equals(month) && !"06".equals(month) && !"09".equals(month) && !"12".equals(month)) {
			return;//
		}

		try {
			if(qmclvo==null || qmclvo.getQysdsjz()== null ||
					!qmclvo.getQysdsjz().booleanValue()){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("所得税未计提");
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private String isJdMonth(String period){//是否季度月份
		String month = period.substring(5, 7);
		String year = period.substring(0,4);
		if("03".equals(month)){
			return year+"第一季度";
		}else if( "06".equals(month)){
			return year+"第二季度";
		}else if("09".equals(month)){
			return year+"第三季度";
		}else if("12".equals(month)){
			return year+"第四季度";
		}
		return null;
	}

	private void jtsjfj(QmclVO qmclvo, List<QmGzBgVo> reslist, String period,CorpVO cpvo) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("计提税金及附加");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("期末结转");
		// 赋值url地址
		putQmclBgvo(bgvo, period, cpvo);

		String month = period.substring(5, 7);

		if("小规模纳税人".equals(cpvo.getChargedeptname())){
			if(!"03".equals(month)
					&& !"06".equals(month) && !"09".equals(month) && !"12".equals(month)){
				return;//
			}
		}

		try {
			if(qmclvo==null || qmclvo.getIsjtsj()== null ||
					!qmclvo.getIsjtsj().booleanValue()){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("税金及附加未计提");
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void zzsjz(QmclVO qmclvo,String pk_corp,String period, List<QmGzBgVo> reslist,CorpVO cpvo) {
		String charname = cpvo.getChargedeptname();
		if("小规模纳税人".equals(charname)){
			return;//小规模不做检查
		}
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("增值税结转");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("期末结转");
		// 赋值url地址
		putQmclBgvo(bgvo, period, cpvo);

		try {
			if(qmclvo==null || qmclvo.getZzsjz()== null ||
					!qmclvo.getZzsjz().booleanValue()){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("增值税未结转");
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void jtzjCheck(QmclVO qmclvo, String pk_corp,String period,List<QmGzBgVo> reslist,CorpVO cpvo) {
		if (cpvo.getBusibegindate() == null || period.compareTo(DateUtils.getPeriod(cpvo.getBusibegindate())) < 0){//没启用固定资产的不做处理
			return;//不检查
		}


		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("计提折旧");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("期末结转");
		// 赋值url地址
		putQmclBgvo(bgvo, period, cpvo);

		try {
			if( qmclvo==null || qmclvo.getIszjjt()== null ||
					!qmclvo.getIszjjt().booleanValue()){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("折旧未计提");
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void hdtzCheck(QmclVO qmclvo,String pk_corp,String period, List<QmGzBgVo> reslist,CorpVO cpvo) {
		//如果该公司没启用汇率档案，则不进行处理

		String hlsq = "select count(1) from ynt_exrate where pk_corp =?  and nvl(dr,0)=0 ";
		SQLParameter sp  = new SQLParameter();
		sp.addParam(cpvo.getPk_corp());
		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(hlsq.toString(), sp, new ColumnProcessor());

		if(count!=null && count.intValue() > 0){
			QmGzBgVo bgvo = null;
			try {
				bgvo = new QmGzBgVo();
				bgvo.setXm("期末调汇");
				bgvo.setIssuccess(DZFBoolean.TRUE);
				bgvo.setVmemo("通过");
//				bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
				// 赋值url地址
				putQmclBgvo(bgvo, period, cpvo);
				bgvo.setName("期末结转");

				if(qmclvo==null || qmclvo.getIshdsytz()== null ||
						!qmclvo.getIshdsytz().booleanValue()){
					bgvo.setIssuccess(DZFBoolean.FALSE);
					bgvo.setVmemo("汇兑未调整");
				}
			} catch (Exception e) {
				handleError(bgvo, e);
			}
			reslist.add(bgvo);
		}

	}

	private void cbjzCheck(QmclVO qmclvo, String pk_corp,String period,List<QmGzBgVo> reslist,CorpVO cpvo) {
		QmGzBgVo bgvo = new QmGzBgVo();
		bgvo.setXm("成本结转");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
//		bgvo.setUrl("gl/gl_jzcl/gl_qmcl.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("期末结转");
		// 赋值url地址
		putQmclBgvo(bgvo, period, cpvo);

		try {
			if(qmclvo==null || qmclvo.getIscbjz() == null ||
					!qmclvo.getIscbjz().booleanValue()){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("成本未结转");
			}
		} catch (Exception e) {
			handleError(bgvo, e);
		}
		reslist.add(bgvo);
	}

	private void inventoryCheck(List<QmGzBgVo> reslist, CorpVO cpvo, String period) {
//		DZFBoolean buildic = cpvo.getBbuildic();

		if(!IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){//没启用库存不显示
			return;
		}
		QmGzBgVo  rkbgvo = new QmGzBgVo();
		rkbgvo.setXm("入库单未生成凭证");
		rkbgvo.setIssuccess(DZFBoolean.TRUE);
		rkbgvo.setVmemo("通过");
		Map<String,Object> map = getPubParam(cpvo);
		map.put("rqq",DateUtils.getPeriodStartDate(period));
		map.put("rqz",DateUtils.getPeriodEndDate(period));
		rkbgvo.setParamstr(JsonUtils.serialize(map));
		rkbgvo.setUrl("icbill-tradein");
//		rkbgvo.setUrl("ic/ic_trade/ic_purchin.jsp?"+getPubParam(cpvo)+"&rqq="+DateUtils.getPeriodStartDate(period)+"&rqz="+DateUtils.getPeriodEndDate(period));
		rkbgvo.setName("入库单");

		QmGzBgVo  ckbgvo = new QmGzBgVo();
		ckbgvo.setXm("出库单未生成凭证");
		ckbgvo.setIssuccess(DZFBoolean.TRUE);
		ckbgvo.setVmemo("通过");
		ckbgvo.setParamstr(JsonUtils.serialize(map));
		ckbgvo.setUrl("icbill-tradeout");
//		ckbgvo.setUrl("ic/ic_trade/ic_saleout.jsp?"+getPubParam(cpvo)+"&rqq="+DateUtils.getPeriodStartDate(period)+"&rqz="+DateUtils.getPeriodEndDate(period));
		ckbgvo.setName("出库单");

		try {
			StringBuffer qrysql = new StringBuffer();
			SQLParameter sp = new SQLParameter();
			qrysql.append("  select *  ");
			qrysql.append("  from ynt_ictrade_h   ");
			qrysql.append("   where pk_corp = ?  ");
			qrysql.append("   and dbilldate like ? ");
			qrysql.append("   and nvl(dr,0)=0 ");
			qrysql.append("   and nvl(isjz,'N')= 'N' ");//尚未生成凭证的数据
			sp.addParam(cpvo.getPk_corp());
			sp.addParam(period+"%");

			List<IntradeHVO> inhvos =  (List<IntradeHVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(IntradeHVO.class));

			if (inhvos != null && inhvos.size() > 0) {
				int in_count = 0;
				int out_count = 0;
				for(IntradeHVO hvo:inhvos){
					if(IBillTypeCode.HP70.equals(hvo.getCbilltype())){//入库单
						in_count++;
					}else if(IBillTypeCode.HP75.equals(hvo.getCbilltype())){//出库单
						out_count++;
					}
				}

				if(in_count>0){
					rkbgvo.setIssuccess(DZFBoolean.FALSE);
					rkbgvo.setVmemo("有"+in_count+"张入库单未生成凭证");
				}else{
					rkbgvo = null;
				}

				if(out_count>0){
					ckbgvo.setIssuccess(DZFBoolean.FALSE);
					ckbgvo.setVmemo("有"+out_count+"张出库单未生成凭证");
				}else{
					ckbgvo = null;
				}
			}
		} catch (DAOException e) {
			handleError(rkbgvo, e);
			handleError(ckbgvo, e);
		}

		if(rkbgvo!=null){
			reslist.add(rkbgvo);
		}
		if(ckbgvo!=null){
			reslist.add(ckbgvo);
		}
	}

	private void bankCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("银行对账单生成凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String,Object> map = getPubParam(cpvo);
		map.put("rq", DateUtils.getPeriodStartDate(period));
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("yhdzd");
//		bgvo.setUrl("gl/gl_pjgl/gl_yhdzd2.jsp?"+getPubParam(cpvo)+"&rq="+DateUtils.getPeriodStartDate(period));
		bgvo.setName("银行对账单");

		try {
			StringBuffer qrysql = new StringBuffer();
			SQLParameter sp = new SQLParameter();
			qrysql.append(" select count(1)  ");
			qrysql.append("  from ynt_bankstatement t1  ");
			qrysql.append("  where t1.pk_tzpz_h is null ");
			qrysql.append("  and nvl(t1.dr,0)=0 ");
			qrysql.append("  and t1.pk_corp =? and t1.inperiod = ? ");
			sp.addParam(pk_corp);
			sp.addParam(period);

			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());

			if(count!=null && count.intValue()>0){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("有"+count+"条银行交易未生成凭证");
			}else{
				return;//
			}
		} catch (DAOException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	private void imageCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("待处理图片");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		bgvo.setUrl("gl/gl_pzgl/gl_tpll.jsp?"+getPubParam(cpvo)+"&pic_status=1&rqq="+DateUtils.getPeriodStartDate(period)+"&rqz="+DateUtils.getPeriodEndDate(period));
		bgvo.setName("图片浏览");

		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select count(1) from ynt_image_group t2 ");
		qrysql.append("  where t2.pk_corp = ? ");
		qrysql.append(" and t2.cvoucherdate like ? ");
		qrysql.append(" and nvl(t2.dr,0)=0 and t2.istate = ? ");
		sp.addParam(pk_corp);
		sp.addParam(period+"%");
		sp.addParam(PhotoState.state0);//未处理图片

		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());

		if(count!=null && count.intValue()>0){
			bgvo.setIssuccess(DZFBoolean.FALSE);
			bgvo.setVmemo("有"+count+"张待处理图片");
		}

		reslist.add(bgvo);
	}

	private void jxfpCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("进项发票生成凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> map =getPubParam(cpvo);
		map.put("period", period);
		bgvo.setParamstr(JsonUtils.serialize(map));
		bgvo.setUrl("incomeBill");
//		bgvo.setUrl("gl/gl_pjgl/gl_jxfp2.jsp?"+getPubParam(cpvo)+"&period="+period);
		bgvo.setName("进项发票");

		try {
			StringBuffer qrysql = new StringBuffer();
			SQLParameter sp = new SQLParameter();
			qrysql.append("select count(1) from ynt_vatincominvoice  tt ");
			qrysql.append(" where tt.pk_tzpz_h is null and tt.pk_corp= ? ");
			qrysql.append(" and  tt.inperiod=?");
			qrysql.append("    and nvl(tt.dr,0) =0 ");
			sp.addParam(pk_corp);
			sp.addParam(period);

			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());

			if(count!=null && count.intValue()>0){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("有"+count+"张进项发票未生成凭证");
			}else{
				return;
			}
		} catch (DAOException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	private void xsfpCheck(List<QmGzBgVo> reslist, String pk_corp, String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("销项发票生成凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String,Object> parammap = getPubParam(cpvo);
		parammap.put("period", period);
		bgvo.setParamstr(JsonUtils.serialize(parammap));
		bgvo.setUrl("outputBill");
//		bgvo.setUrl("gl/gl_pjgl/gl_xxfp2.jsp?"+getPubParam(cpvo)+"&period="+period);
		bgvo.setName("销项发票");

		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		qrysql.append(" select count(1)   ");
		qrysql.append(" from  ynt_vatsaleinvoice ");
		qrysql.append("  where nvl(dr,0)=0 and pk_tzpz_h  is null ");
		qrysql.append(" and pk_corp = ? and  inperiod = ? ");
		sp.addParam(pk_corp);
		sp.addParam(period);

		try {
			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnProcessor());

			if(count!=null && count.intValue()>0){
				bgvo.setIssuccess(DZFBoolean.FALSE);
				bgvo.setVmemo("有"+count+"张销项发票未生成凭证");
			}else{
				return;//如果没数据不显示
			}
		} catch (DAOException e) {
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	private void tempPzCheck(List<QmGzBgVo> reslist, List<TzpzHVO> tzpzlist,
							 String period,String pk_corp,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("待处理暂存态凭证");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		Map<String, Object> parammap = getPubParam(cpvo);
		parammap.put("pz_status",-1);
		parammap.put("serdate","serDay");
		parammap.put("pzbegdate",DateUtils.getPeriodStartDate(period));
		parammap.put("pzenddate",DateUtils.getPeriodEndDate(period));
		parammap.put("beginPeriod", period);
		parammap.put("endPeriod", period);
		bgvo.setParamstr(JsonUtils.serialize(parammap));
		bgvo.setUrl("voucher-manage");
//		bgvo.setUrl("gl/gl_pzgl/gl_pzgl.jsp?"+getPubParam(cpvo)+"&pz_status=-1&serdate=serDay&pzbegdate="+DateUtils.getPeriodStartDate(period)+"&pzenddate="+DateUtils.getPeriodEndDate(period));
		bgvo.setName("凭证管理");
		bgvo.setBz(" *");
		try {
			if(tzpzlist!=null && tzpzlist.size()>0){
				for(TzpzHVO hvo:tzpzlist){
					if(hvo.getVbillstatus()!=null && hvo.getVbillstatus().intValue() == IVoucherConstants.TEMPORARY ){
						bgvo.setIssuccess(DZFBoolean.FALSE);
						bgvo.setVmemo("存在待处理暂存态凭证!");
						break;
					}
				}
			}
		}catch (Exception e){
			handleError(bgvo, e);
		}

		reslist.add(bgvo);
	}

	private void handleError(QmGzBgVo  bgvo, Exception e){
		log.error("关账检查["+bgvo.getXm()+"]系统异常", e);
		bgvo.setIssuccess(DZFBoolean.FALSE);
		bgvo.setVmemo("无法检查");
		bgvo.setUrl(null);
	}

	/**
	 * 凭证断号检查
	 *
	 * @param reslist
	 * @param period
	 */
	private void pzhCheck(List<QmGzBgVo> reslist, List<TzpzHVO> pzhlist,String period,CorpVO cpvo) {
		QmGzBgVo  bgvo = new QmGzBgVo();
		bgvo.setXm("凭证是否断号");
		bgvo.setIssuccess(DZFBoolean.TRUE);
		bgvo.setVmemo("通过");
		bgvo.setUrl("voucher-manage");
//		bgvo.setUrl("gl/gl_pzgl/gl_pzgl.jsp?"+getPubParam(cpvo,parammap)+"&serdate=serDay&pzbegdate="+DateUtils.getPeriodStartDate(period)+"&pzenddate="+DateUtils.getPeriodEndDate(period));
		bgvo.setName("凭证管理");
		bgvo.setBz(" *");
		String voucherId = null;
		try{
			if(pzhlist!=null && pzhlist.size()>0){
				Integer pzh_int_cur = 0;
				int count = 1;
				for(int i =0;i<pzhlist.size();i++){
//				if(pzhlist.get(i).getVbillstatus() == null
//						|| pzhlist.get(i).getVbillstatus().intValue() ==  IVoucherConstants.TEMPORARY){//暂存态不考虑
//					continue;
//				}


					pzh_int_cur = Integer.parseInt(pzhlist.get(i).getPzh());
					if(pzh_int_cur.intValue()!=(count)){
						if(count == 1){
							bgvo.setVmemo("凭证号"+pzhlist.get(i).getPzh()+"之前缺失,请检查");
							voucherId = pzhlist.get(i).getPk_tzpz_h();
						}else{
							bgvo.setVmemo("凭证号"+pzhlist.get(i-1).getPzh()+"之后缺失,请检查");
							voucherId = pzhlist.get(i-1).getPk_tzpz_h();
						}
						bgvo.setIssuccess(DZFBoolean.FALSE);
						break;
					}
					count++;
				}
			}
		}catch (Exception e){
			handleError(bgvo, e);
		}

		Map<String, Object> parammap = getPubParam(cpvo);
		parammap.put("serdate", "serDay");
		parammap.put("pzbegdate", DateUtils.getPeriodStartDate(period));
		parammap.put("pzenddate", DateUtils.getPeriodEndDate(period));
		parammap.put("beginPeriod", period);
		parammap.put("endPeriod", period);
		parammap.put("focusId", voucherId);
		bgvo.setParamstr(JsonUtils.serialize(parammap));
		reslist.add(bgvo);
	}

	private Map<String ,Object> getPubParam(CorpVO cpvo) {
		Map<String ,Object> map = new HashMap<>();
		map.put("source","gzbg");
		map.put("corpIds",cpvo.getPk_corp());
		map.put("gsname",CodeUtils1.deCode(cpvo.getUnitname()));
		return map;
	}

	/**
	 * 查询期间凭证
	 * @param pk_corp
	 * @param period
	 * @return
	 */
	private List<TzpzHVO> qryPeriodPz(String pk_corp, String period) {
		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
		qrysql.append("select * from  ynt_tzpz_h  ");
		qrysql.append("  where pk_corp = ? and nvl(dr,0)=0 ");
		qrysql.append(" and period = ? ");
		qrysql.append(" order by pzh  ");
		sp.addParam(pk_corp);
		sp.addParam(period);

		List<TzpzHVO> pzhlist =  (List<TzpzHVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(TzpzHVO.class));
		return pzhlist;
	}

	@Override
	public void saveQmgzBg(QmGzBgSetVO setvo,String userid) throws DZFWarpException {

		String xm = setvo.getVxm();
		String pk_corp = setvo.getPk_corp();

		String qrysql = "select * from "+QmGzBgSetVO.TABLE_NAME+" where nvl(dr,0)=0 and pk_corp =? and vxm = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(xm);

		List<QmGzBgSetVO> setvos = (List<QmGzBgSetVO>) singleObjectBO.executeQuery(qrysql, sp, new BeanListProcessor(QmGzBgSetVO.class));

		if(setvos!=null && setvos.size()>0){
			QmGzBgSetVO upvo = setvos.get(0);
			upvo.setNmax(setvo.getNmax());
			upvo.setNmin(setvo.getNmin());
			singleObjectBO.update(upvo);
		}else{
			setvo.setDoperatordate(new DZFDateTime());
			setvo.setCoperatorid(userid);
			singleObjectBO.saveObject(setvo.getPk_corp(), setvo);
		}
	}

	class MyComparator implements Comparator<Map.Entry<String,DZFDouble[]>>{

		Map<String, AuxiliaryAccountBVO> auxiliaryAccountMapping = null;
		MyComparator ( Map<String, AuxiliaryAccountBVO> auxiliaryAccountMapping){
			this.auxiliaryAccountMapping = auxiliaryAccountMapping;
		}

		private  int convert(DZFDouble[] d){
			int n = 111;
			if(!isAllNotZero(d[0],d[1])){
				n -= 100;
			}
			if(!isAllNotZero(d[2],d[3])){
				n -= 10;
			}
			if(!isAllNotZero(d[4],d[5])){
				n -= 1;
			}
			return n;
		}

		private List<Integer> orderNum = new ArrayList<Integer>(){{
			add(1);
			add(11);
			add(10);
			add(101);
			add(111);
			add(110);
			add(100);
		}};

		@Override
		public int compare(Map.Entry<String, DZFDouble[]> entry1, Map.Entry<String, DZFDouble[]> entry2) {

			String key1 = entry1.getKey();
			String key2 = entry2.getKey();
			DZFDouble[] value1 = entry1.getValue();
			DZFDouble[] value2 = entry2.getValue();
			int n1 = convert(value1), n2 = convert(value2);
			int r = orderNum.indexOf(n1) - orderNum.indexOf(n2);

			if(r > 0){
				return -1;
			}else if(r == 0){
				// 辅助编码排序
				String code1 = "";
				String code2 = "";
				String[] pk_arr1 = key1.split(",");
				String[] pk_arr2 = key2.split(",");
				for(int i = 0; i < pk_arr1.length; i++){
					if("0".equals(pk_arr1[i])){
						continue;
					}
					code1 = auxiliaryAccountMapping.get(pk_arr1[i]).getCode();
				}
				for(int i = 0; i < pk_arr2.length; i++){
					if("0".equals(pk_arr2[i])){
						continue;
					}
					code2 = auxiliaryAccountMapping.get(pk_arr2[i]).getCode();
				}
				return code1.compareTo(code2);
			}
			return 1;
		}
	}
}

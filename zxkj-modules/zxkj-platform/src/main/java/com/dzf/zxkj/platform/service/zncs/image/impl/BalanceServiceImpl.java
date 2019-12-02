package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.DZFBalanceVO;
import com.dzf.zxkj.platform.model.zncs.DZFBalanceBVO;
import com.dzf.zxkj.platform.model.zncs.QueryBalanceVO;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.zncs.image.IBalanceService;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("balanceServImpl")
public class BalanceServiceImpl implements IBalanceService {
	 private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IFctpubService ifctService;
	@Override
	public DZFBalanceVO recharge(DZFBalanceVO balanceVO) throws DZFWarpException {
		List<CorpVO> corpvo = null;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		DZFBalanceVO resvo = null;
		String pk_corp_yy = null;
		String pk_user = null;
		if (balanceVO.getChildren()[0].getAttributeValue("pk_temp_user") != null) {
			sql.append("select isaccount,pk_user from app_temp_user where pk_temp_user=?");
			sp.addParam(balanceVO.getChildren()[0].getAttributeValue("pk_temp_user"));
			List<Object[]> tempuser = (List<Object[]>) singleObjectBO.executeQuery(sql.toString(), sp, new ArrayListProcessor());
			if (tempuser.size() > 0) {
				if (tempuser.get(0)[0] != null && tempuser.get(0)[0].equals("Y")) {
					pk_user = (String) tempuser.get(0)[1];
					sql = new StringBuffer();
					sp = new SQLParameter();
					sql.append("select pk_corp from sm_user where cuserid=?");
					sp.addParam(pk_user);

					corpvo = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(CorpVO.class));
					if (corpvo.size() > 0) {
						pk_corp_yy = queryCascadeCorps(corpvo.get(0).getPk_corp());
						balanceVO.setPk_corp_yy(pk_corp_yy == null ? corpvo.get(0).getPk_corp() : pk_corp_yy);
					}
				}
			}
		} else {
			pk_corp_yy = balanceVO.getPk_corp_yy();
		}
		if (pk_corp_yy != null) {
			sql = new StringBuffer();
			sp = new SQLParameter();
			sql.append("select * from DZF_BALANCE where pk_corp_yy = ? and PK_DZFSERVICEDES=? and nvl(dr,0)=0");
			sp.addParam(pk_corp_yy);
			sp.addParam(balanceVO.getPk_dzfservicedes());
			List<DZFBalanceVO> hvoslist = (List<DZFBalanceVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(DZFBalanceVO.class));
			if (hvoslist.size() == 0) {
				balanceVO.setRemainingcount(balanceVO.getTotalcount());
				balanceVO.setUsedcount(new DZFDouble());
				balanceVO.getChildren()[0].setAttributeValue("pk_corp", corpvo == null ? pk_corp_yy : corpvo.get(0).getPk_corp());
				balanceVO.getChildren()[0].setAttributeValue("pk_user", pk_user);
				balanceVO.getChildren()[0].setAttributeValue("pk_corp_yy", pk_corp_yy);
				balanceVO.getChildren()[0].setAttributeValue("opdate", new DZFDate());
				balanceVO.getChildren()[0].setAttributeValue("pk_corpkjgs", corpvo == null ? pk_corp_yy : corpvo.get(0).getPk_corp());
				balanceVO.getChildren()[0].setAttributeValue("period", new DZFDate().toString().substring(0, 7));
				resvo = (DZFBalanceVO) singleObjectBO.saveObject(balanceVO.getPk_corp_yy(), balanceVO);
			} else {
				DZFBalanceVO hvo = hvoslist.get(0);
				DZFBalanceBVO[] bvo = (DZFBalanceBVO[]) balanceVO.getChildren();
				if (bvo.length > 0 && bvo[0].getIsadd() == 0) {
					hvo.setTotalcount(hvo.getTotalcount().add(bvo[0].getChangedcount()));
					hvo.setRemainingcount(hvo.getRemainingcount().add(bvo[0].getChangedcount()));
					bvo[0].setPk_corp(corpvo == null ? pk_corp_yy : corpvo.get(0).getPk_corp());
					bvo[0].setPk_user(pk_user);
					bvo[0].setPk_corp_yy(pk_corp_yy);
					bvo[0].setPk_corpkjgs(corpvo == null ? pk_corp_yy : corpvo.get(0).getPk_corp());
					bvo[0].setOpdate(new DZFDate());
					bvo[0].setPeriod(new DZFDate().toString().substring(0, 7));
				}
				hvo.setChildren(bvo);
				resvo = (DZFBalanceVO) singleObjectBO.saveObject("000001", hvo);
			}
		}
		return resvo;
	}

	private String queryCascadeCorps(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = "select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CorpVO.class));
		if (list != null && list.size() >= 2) {
			return list.get(list.size() - 2).getPk_corp();
		}
		return null;

	}

	@Override
	public void consumption(DZFBalanceBVO balanceBVO) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		String pk_corp_yy = queryCascadeCorps(balanceBVO.getPk_corp());
		balanceBVO.setPk_corp_yy(pk_corp_yy);
		if(StringUtil.isEmpty(pk_corp_yy)){
			throw new BusinessException("没有找到上级会计公司!");
		}
		synchronized(balanceBVO.getPk_corp_yy().intern()){
			try{
				if (isFreeCorp(balanceBVO.getPk_dzfservicedes(), pk_corp_yy) == DZFBoolean.TRUE) {
					return;
				}
				sql.append("select * from DZF_BALANCE where nvl(dr,0)=0 and pk_corp_yy=? and PK_DZFSERVICEDES=?");
				sp.addParam(pk_corp_yy);
				sp.addParam(balanceBVO.getPk_dzfservicedes());
				List<DZFBalanceVO> list = (List<DZFBalanceVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(DZFBalanceVO.class));
				if (list.size() > 0 && list.get(0).getRemainingcount().compareTo(balanceBVO.getChangedcount()) >= 0) {
					balanceBVO.setPk_balance(list.get(0).getPk_balance());
					balanceBVO.setPk_corp_yy(pk_corp_yy);
					balanceBVO.setOpdate(new DZFDate());
					balanceBVO.setPeriod(balanceBVO.getPeriod() == null ? new DZFDate().toString().substring(0, 7) : balanceBVO.getPeriod());
					singleObjectBO.saveObject(balanceBVO.getPk_corp(), balanceBVO);
					sp = new SQLParameter();
					sql = new StringBuffer();
					sql.append("update DZF_BALANCE set ");
					sql.append(" usedcount = usedcount+" + balanceBVO.getChangedcount() + ",REMAININGCOUNT = REMAININGCOUNT-" + balanceBVO.getChangedcount() + " where pk_corp_yy = ? and nvl(dr,0)=0 and PK_DZFSERVICEDES=?");
					sp.addParam(pk_corp_yy);
					sp.addParam(balanceBVO.getPk_dzfservicedes());
					singleObjectBO.executeUpdate(sql.toString(), sp);
				} else {
					throw new BusinessException("余额不足！");
				}
			}catch(Exception e){
				log.error("错误",e);
				throw e;
			}
		}
	}

	@Override
	public DZFBoolean isAlreadyConsumption(String pk_dzfservicedes, String period, String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_dzfservicedes) || StringUtil.isEmpty(period) || StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("传入参数错误。");
		}
		if (isFreeCorp(pk_dzfservicedes, queryCascadeCorps(pk_corp)) == DZFBoolean.TRUE) {
			return DZFBoolean.TRUE;
		}
		String sql = "select * from dzf_balance_b where nvl(dr,0)=0 and isadd=1 and pk_dzfservicedes=? and period=? and pk_corp=?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_dzfservicedes);
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<DZFBalanceBVO> list = (List<DZFBalanceBVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DZFBalanceBVO.class));
		if (list != null && list.size() > 0) {
			return DZFBoolean.TRUE;
		}
		return DZFBoolean.FALSE;
	}

	@Override
	public List<DZFBalanceBVO> queryBalanceDetails(QueryBalanceVO queryVO) throws DZFWarpException {
		if (StringUtil.isEmpty(queryVO.getPk_dzfservicedes())) {
			throw new BusinessException("产品不能为空。");
		}
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("select * from dzf_balance_b where nvl(dr,0)=0 and isadd=1 ");
		sb.append(" and pk_dzfservicedes =? ");
		sp.addParam(queryVO.getPk_dzfservicedes());
		if (queryVO.getPk_corpkjgs() != null && queryVO.getPk_corpkjgs().length > 0) {
			sb.append(" and  " + SqlUtil.buildSqlForIn("pk_corpkjgs", queryVO.getPk_corpkjgs()));
		}
		if (queryVO.getBeginDate() != null) {
			sb.append(" and opdate >=? ");
			sp.addParam(queryVO.getBeginDate());
		}
		if (queryVO.getEndDate() != null) {
			sb.append(" and opdate <=?");
			sp.addParam(queryVO.getEndDate());
		}
		return (List<DZFBalanceBVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(DZFBalanceBVO.class));
	}

	/**
	 * 
	 * @param pk_dzfservicedes
	 * @param pk_corp_yy
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFBoolean isFreeCorp(String pk_dzfservicedes, String pk_corp_yy) throws DZFWarpException {
		String fileName = null;
		if (pk_dzfservicedes.equals(IDzfServiceConst.DzfServiceProduct_04)) {
			fileName = "/freecorp.properties";
		} else {
			return DZFBoolean.FALSE;
		}
		InputStream in=null;
		try {
			Properties prop = new Properties();
			// 读取属性文件jedis_config.properties
			in = getClass().getResourceAsStream(fileName);
			prop.load(in); // / 加载属性列表
			String user_code_kf = prop.getProperty("freecorp_DzfServiceProduct_04");
			String[] freecorps = user_code_kf.split(",");
			for (int i = 0; i < freecorps.length; i++) {
				if (freecorps[i].equals(pk_corp_yy)) {
					return DZFBoolean.TRUE;
				}
			}
			return DZFBoolean.FALSE;
		} catch (Exception e) {
			return DZFBoolean.FALSE;
		}finally {
			if(in!=null){
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public synchronized void consumptionByFct(DZFBalanceBVO balanceBVO,DZFDate date) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		String pk_corp_yy = ifctService.getAthorizeFactoryCorp(date, balanceBVO.getPk_corp());
		balanceBVO.setPk_corp_yy(pk_corp_yy);
		if(StringUtil.isEmpty(pk_corp_yy)){
			throw new BusinessException("没有找到会计工厂委托关系!");
		}
		synchronized(balanceBVO.getPk_corp_yy().intern()){
			try{
				if (isFreeCorp(balanceBVO.getPk_dzfservicedes(), pk_corp_yy) == DZFBoolean.TRUE) {
					return;
				}
				sql.append("select * from DZF_BALANCE where nvl(dr,0)=0 and pk_corp_yy=? and PK_DZFSERVICEDES=?");
				sp.addParam(pk_corp_yy);
				sp.addParam(balanceBVO.getPk_dzfservicedes());
				List<DZFBalanceVO> list = (List<DZFBalanceVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(DZFBalanceVO.class));
				if (list.size() > 0 && list.get(0).getRemainingcount().compareTo(balanceBVO.getChangedcount()) >= 0) {
					balanceBVO.setPk_balance(list.get(0).getPk_balance());
					balanceBVO.setPk_corp_yy(pk_corp_yy);
					balanceBVO.setOpdate(new DZFDate());
					balanceBVO.setPeriod(balanceBVO.getPeriod() == null ? new DZFDate().toString().substring(0, 7) : balanceBVO.getPeriod());
					singleObjectBO.saveObject(balanceBVO.getPk_corp(), balanceBVO);
					sp = new SQLParameter();
					sql = new StringBuffer();
					sql.append("update DZF_BALANCE set ");
					sql.append(" usedcount = usedcount+" + balanceBVO.getChangedcount() + ",REMAININGCOUNT = REMAININGCOUNT-" + balanceBVO.getChangedcount() + " where pk_corp_yy = ? and nvl(dr,0)=0 and PK_DZFSERVICEDES=?");
					sp.addParam(pk_corp_yy);
					sp.addParam(balanceBVO.getPk_dzfservicedes());
					singleObjectBO.executeUpdate(sql.toString(), sp);
				} else {
					throw new BusinessException("余额不足！");
				}
			}catch(Exception e){
				log.error("错误",e);
				throw e;
			}
		}
	}
}

package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.VoUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZgVoucher {

	private SingleObjectBO singleObjectBO  ;

	private IYntBoPubUtil yntBoPubUtil = null;

	private ICbComconstant gl_cbconstant;
	private IParameterSetService parameterserv;
	public ZgVoucher(ICbComconstant gl_cbconstant,SingleObjectBO singleObjectBO,IYntBoPubUtil yntBoPubUtil,IParameterSetService parameterserv){
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.gl_cbconstant = gl_cbconstant;
		this.parameterserv=parameterserv;
	}



	//  查询第一分支的最末级科目
	private YntCpaccountVO queryAccount(QmclVO vo, String code) throws BusinessException {

		StringBuffer strb = new StringBuffer();

		strb.append(" SELECT * FROM  ynt_cpaccount t ");
		strb.append("  where t.accountcode like ? ");
		strb.append("  and pk_corp = ? and  nvl(isleaf,'N')='Y'  ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(code+"%");
		sp.addParam(vo.getPk_corp());

		List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO.executeQuery(strb.toString(), sp,
				new BeanListProcessor(YntCpaccountVO.class));
		if(list == null || list.size()==0){
			throw new BusinessException("查找暂估科目出错!");
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	/**
	 * 生成本月暂估凭证
	 */
	public TzpzHVO createPzvos(QmclVO vo, TempInvtoryVO[]  zgvos, String userid)throws BusinessException {
		if(zgvos == null || zgvos.length == 0)
			return null;
		DZFDouble jf = DZFDouble.ZERO_DBL;
		// 根据商品查找对应科目
		List<String> list = new ArrayList<String>();
		for(TempInvtoryVO invo:zgvos){
			list.add(invo.getPk_invtory());
		}

		Map<String,TempInvtoryVO>  map  =queryInventoryVO(list);
		String priceStr = parameterserv.queryParamterValueByCode(vo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		YntCpaccountVO accvo1 = queryAccount(vo,gl_cbconstant.getZg_code());
		TempInvtoryVO  tempvo = null;
		TempInvtoryVO  zgvo = null;
		int len = zgvos.length;
		List<TzpzBVO> tlist = new ArrayList<>();
		TzpzBVO tzpzvo = null;
		for(int i = 0 ;i < len; i++){//借库存商品 1405
			zgvo = zgvos[i];
			tempvo = map.get(zgvo.getPk_invtory());
			if(tempvo == null){
				throw new BusinessException("查找商品科目出错!");
			}
			if(zgvo.getNnumber() == null)
				zgvo.setNnumber(DZFDouble.ZERO_DBL);
			tzpzvo = new TzpzBVO() ;
			tzpzvo.setPk_inventory(zgvo.getPk_invtory());
			tzpzvo.setNnumber(zgvo.getNnumber());
			tzpzvo.setVcode(tempvo.getKmbm());
			tzpzvo.setVname(tempvo.getKmname());
			tzpzvo.setPk_accsubj(tempvo.getKmid()) ;
			tzpzvo.setJfmny(zgvo.getNmny()) ;
			tzpzvo.setNprice(calcPrice(zgvo.getNmny(),zgvo.getNnumber(),iprice));//单价
			tzpzvo.setDfmny(null);
			tzpzvo.setZy(vo.getPeriod()+"月暂估") ;
			tzpzvo.setPk_currency(yntBoPubUtil.getCNYPk()) ;
			tzpzvo.setNrate(new DZFDouble(1));
			tzpzvo.setPk_corp(vo.getPk_corp());
			tlist.add(tzpzvo);
			jf = SafeCompute.add(jf, zgvo.getNmny());
			if (zgvo.getNnumber_old() != null && zgvo.getNnumber_old().compareTo(zgvo.getNnumber()) > 0) {
				tzpzvo = (TzpzBVO) tzpzvo.clone();
				tzpzvo.setNnumber(SafeCompute.sub(zgvo.getNnumber_old(), zgvo.getNnumber()));
				tzpzvo.setNprice(DZFDouble.ZERO_DBL);
				tzpzvo.setJfmny(DZFDouble.ZERO_DBL) ;
				tzpzvo.setNprice(DZFDouble.ZERO_DBL);//单价
				tzpzvo.setDfmny(null);
				tlist.add(tzpzvo);
			}
		}
		//
		tzpzvo = new TzpzBVO();
		tzpzvo.setPk_accsubj(accvo1.getPk_corp_account()) ;
		tzpzvo.setJfmny(null) ;
		tzpzvo.setDfmny(jf);
		tzpzvo.setZy(vo.getPeriod()+"月暂估") ;
		tzpzvo.setPk_currency(yntBoPubUtil.getCNYPk()) ;
		tzpzvo.setNrate(new DZFDouble(1));
		tzpzvo.setPk_corp(vo.getPk_corp());
		tlist.add(tzpzvo);
		//
		TzpzHVO headVO = new TzpzHVO() ;
		headVO.setPk_corp(vo.getPk_corp()) ;
		headVO.setJfmny(jf) ;
		headVO.setDfmny(jf) ;
		headVO.setCoperatorid(userid) ;
		headVO.setIshasjz(DZFBoolean.FALSE) ;
		DZFDate nowDatevalue =  getPeroidDZFDate(vo) ;
		headVO.setDoperatedate(nowDatevalue) ;
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), nowDatevalue)) ;
		if (StringUtil.isEmpty(accvo1.get__parentId())
				&& (accvo1.getIsfzhs() == null || "0000000000".equals(accvo1.getIsfzhs()))) {
			headVO.setVbillstatus(8);// 默认自由态
		} else {
			headVO.setVbillstatus(-1);// 默认暂存态
		}
		//记录单据来源
		headVO.setSourcebillid(vo.getPk_qmcl()) ;
		headVO.setSourcebilltype(IBillTypeCode.HP34) ;
		headVO.setPeriod(vo.getPeriod());
		headVO.setVyear(Integer.valueOf(vo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		//
		headVO.setChildren(tlist.toArray(new TzpzBVO[tlist.size()]));
		return headVO;
	}



	/**
	 * 查询库存商品VO
	 */
	@SuppressWarnings("unchecked")
	private Map<String,TempInvtoryVO> queryInventoryVO(List<String> list ){
		StringBuffer sf = new StringBuffer();
		sf.append(" select t1.name,t2.accountname,t1.pk_inventory, t2.accountcode,t2.pk_corp_account from ynt_inventory t1 ");
		sf.append(" join ynt_cpaccount t2 on t1.pk_subject = t2.pk_corp_account ");
		sf.append(" and  t1.pk_corp = t2.pk_corp and nvl(t1.dr,0) = 0 and nvl(t2.dr,0) = 0 ");
		sf.append(" where  ");
		sf.append(SqlUtil.buildSqlForIn("t1.pk_inventory", list.toArray(new String[list.size()])));
		Map<String,TempInvtoryVO> map = (Map<String,TempInvtoryVO>)singleObjectBO.executeQuery(sf.toString(), null, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {

				Map<String,TempInvtoryVO> map = new HashMap<String,TempInvtoryVO>();
				TempInvtoryVO vo = null;
				while(rs.next()){
					vo = new TempInvtoryVO();
					String name = rs.getString("name");
					String accountname = rs.getString("accountname");
					String accountcode = rs.getString("accountcode");
					String pk_corp_account = rs.getString("pk_corp_account");
					String pk_inventory = rs.getString("pk_inventory");
					vo.setKmname(accountname);
					vo.setInvname(name);
					vo.setKmbm(accountcode);
					vo.setKmid(pk_corp_account);
					map.put(pk_inventory, vo);
				}
				return map;
			}

		});
		return map;
	}
	/**
	 * 生成本月暂估凭证 不启用库存
	 */
	public TzpzHVO createPzvosNoIC(QmclVO vo,TempInvtoryVO[]  zgvos,String cbjzCount,String userid,Map<String, YntCpaccountVO> kmsmap)throws BusinessException {
		if(zgvos == null || zgvos.length == 0)
			return null;
		String priceStr = parameterserv.queryParamterValueByCode(vo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		DZFDouble jf = DZFDouble.ZERO_DBL;
//		TzpzBVO[] bodyvos = new TzpzBVO[zgvos.length+1];
//		YntCpaccountVO accvo = queryAccount(vo,gl_cbconstant.getKcsp_code());
		//zpm修改暂估入账科目
		String[] zgkms = getZgDataNOic(vo);
		if(zgkms == null ||zgkms.length ==0 || StringUtil.isEmpty(zgkms[0])){
			throw new BusinessException("暂估入账科目没有设置！");
		}
		int len = zgvos.length;
		TempInvtoryVO  zgvo = null;
		List<TzpzBVO> list = new ArrayList<>();
		TzpzBVO tzpzvo = null;
		for(int i = 0 ;i < len; i++){
			tzpzvo = new TzpzBVO() ;
			zgvo = zgvos[i];
//			bodyvos[i].setPk_inventory(zgvos[i].getPk_invtory());
			tzpzvo.setNnumber(zgvo.getNnumber());
			tzpzvo.setVcode(zgvo.getKmbm());
			tzpzvo.setVname(zgvo.getKmname());
			tzpzvo.setPk_accsubj(zgvo.getKmid()) ;
			tzpzvo.setFzhsx6(zgvo.getFzid());
			tzpzvo.setJfmny(zgvo.getNmny()) ;
			tzpzvo.setNprice(calcPrice(zgvo.getNmny(),zgvo.getNnumber(),iprice));//单价
			tzpzvo.setDfmny(null);
			tzpzvo.setZy(vo.getPeriod()+"月暂估") ;
			tzpzvo.setPk_currency(yntBoPubUtil.getCNYPk()) ;
			tzpzvo.setNrate(new DZFDouble(1));
			tzpzvo.setPk_corp(vo.getPk_corp());
			list.add(tzpzvo);
			jf = SafeCompute.add(jf, zgvo.getNmny());
			if (zgvo.getNnumber_old() != null && zgvo.getNnumber_old().compareTo(zgvo.getNnumber()) > 0) {
				tzpzvo = (TzpzBVO) tzpzvo.clone();
				tzpzvo.setNnumber(SafeCompute.sub(zgvo.getNnumber_old(), zgvo.getNnumber()));
				tzpzvo.setNprice(DZFDouble.ZERO_DBL);
				tzpzvo.setJfmny(DZFDouble.ZERO_DBL) ;
				tzpzvo.setNprice(DZFDouble.ZERO_DBL);//单价
				tzpzvo.setDfmny(null);
				list.add(tzpzvo);
			}
		}
		//
		tzpzvo = new TzpzBVO();
		tzpzvo.setPk_accsubj(zgkms[0]) ;
		if(zgkms.length ==2){
			tzpzvo.setFzhsx2(zgkms[1]);
		}
		tzpzvo.setJfmny(null) ;
		tzpzvo.setDfmny(jf);
		tzpzvo.setZy(vo.getPeriod()+"月暂估") ;
		tzpzvo.setPk_currency(yntBoPubUtil.getCNYPk()) ;
		tzpzvo.setNrate(new DZFDouble(1));
		tzpzvo.setPk_corp(vo.getPk_corp());
		list.add(tzpzvo);
		//
		TzpzHVO headVO = new TzpzHVO() ;
		headVO.setPk_corp(vo.getPk_corp()) ;
		headVO.setJfmny(jf) ;
		headVO.setDfmny(jf) ;
		headVO.setCoperatorid(userid) ;
		headVO.setIshasjz(DZFBoolean.FALSE) ;
		DZFDate nowDatevalue =  getPeroidDZFDate(vo) ;
		headVO.setDoperatedate(getPeriodFirst(vo)) ;//暂估凭证放到本月的第一天
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), nowDatevalue)) ;
		headVO.setNbills(0);//附单据数

		YntCpaccountVO kmvo = kmsmap.get(zgkms[0]);
		if(kmvo == null){
			throw new BusinessException("暂估入账科目没有设置！");
		}
		if(zgkms.length ==1 && StringUtil.isEmpty(kmvo.get__parentId())
				&& (kmvo.getIsfzhs() == null || "0000000000".equals(kmvo.getIsfzhs()))){
			headVO.setVbillstatus(8);// 默认自由态
		}else if(zgkms.length ==2){//zgkms.length == 2 的情况
			if(kmvo.getIsfzhs() == null || "0000000000".equals(kmvo.getIsfzhs())){
				headVO.setVbillstatus(8);// 默认自由态
			}else if(kmvo.getIsfzhs()!=null
					&& "1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)))
					&& !StringUtil.isEmpty(zgkms[1])){//辅助项目启用了,,供应商
				headVO.setVbillstatus(8);// 默认自由态
			}else{
				headVO.setVbillstatus(-1);// 默认暂存态
			}
		}else{
			headVO.setVbillstatus(-1);// 默认暂存态
		}
		//记录单据来源
		headVO.setSourcebillid(vo.getPk_qmcl()) ;
		headVO.setCbjzCount(cbjzCount);//工业成本结转分步删除凭证标识
		headVO.setSourcebilltype(IBillTypeCode.HP34) ;
		headVO.setPeriod(vo.getPeriod());
		headVO.setVyear(Integer.valueOf(vo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		//
		headVO.setChildren(list.toArray(new TzpzBVO[list.size()]));
		return headVO;
	}
	public DZFDouble calcPrice(DZFDouble mny,DZFDouble nnum,int iprice){

		if(nnum != null && nnum.doubleValue() != 0){
			DZFDouble price = SafeCompute.div(mny, nnum);
			price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
			return price;
		}
		return null;
	}


	//不启用库存取得暂估科目
	private String[] getZgDataNOic(QmclVO vo) throws BusinessException{
		//从设置表中取
		String[] str = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		InventorySetVO[] bodys  = (InventorySetVO[])singleObjectBO.queryByCondition(InventorySetVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp);
		if(bodys == null || bodys.length == 0){
			YntCpaccountVO accvo1 = queryAccount(vo,gl_cbconstant.getZg_code());
			str = new String[]{accvo1.getPk_corp_account()};
		}else{
			if(StringUtil.isEmpty(bodys[0].getZgrkdfkm())){//没有保存此字段
				YntCpaccountVO accvo1 = queryAccount(vo,gl_cbconstant.getZg_code());
				str = new String[]{accvo1.getPk_corp_account()};
			}else{
				YntCpaccountVO kmvo = (YntCpaccountVO)singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, bodys[0].getZgrkdfkm());
				if(kmvo== null || kmvo.getIsleaf() == null || !kmvo.getIsleaf().booleanValue()){
					throw new BusinessException("存货设置暂估科目不是末级科目，请重新设置");
				}
				//重新给暂估辅助，，赋值
				if(!StringUtil.isEmpty(bodys[0].getZgkhfz())){
					if(kmvo==null
							|| StringUtil.isEmpty(kmvo.getIsfzhs())
							|| !"1".equals(String.valueOf(kmvo.getIsfzhs().charAt(1)))){//供应商辅助
						bodys[0].setZgkhfz(null);
					}
				}
				//
				if(StringUtil.isEmpty(bodys[0].getZgkhfz())){
					str = new String[]{bodys[0].getZgrkdfkm(),""};
				}else{
					//
                    IAuxiliaryAccountService gl_fzhsserv  = (IAuxiliaryAccountService) SpringUtils.getBean("gl_fzhsserv");
                    boolean b = gl_fzhsserv.isExistFz(vo.getPk_corp(), bodys[0].getZgkhfz(), AuxiliaryConstant.ITEM_SUPPLIER);
                    if (b) {
                        str = new String[]{bodys[0].getZgrkdfkm(), bodys[0].getZgkhfz()};
                    } else {
                        str = new String[]{bodys[0].getZgrkdfkm(), ""};
                    }
				}
			}
		}
		return str;
	}
	/**
	 * 取所属月的第一天
	 */
	public DZFDate getPeriodFirst(QmclVO vo){
		DZFDate first = new DZFDate(vo.getPeriod() + "-01");
		return first;
	}
	/**
	 * 取期间所属月的最后一天
	 */
	public DZFDate getPeroidDZFDate(QmclVO vo){
		DZFDate period = new DZFDate(vo.getPeriod() + "-01");
		period = new DZFDate(vo.getPeriod() + "-" + period.getDaysMonth()) ;
		return period;
	}

	/**
	 * 生成下月冲销暂估凭证，启用库存
	 */
	public TzpzHVO queryNextcode(QmclVO vo,TzpzHVO billVO)throws BusinessException {
		//设置表头
		TzpzHVO  headvo = (TzpzHVO)billVO.clone();
		TzpzBVO[] bodyvos = (TzpzBVO[])billVO.getChildren();
		DZFDate doperatedate = getPeroidDZFDate(vo).getDateAfter(1);
		headvo.setDoperatedate(doperatedate);
		headvo.setPk_tzpz_h(null);
		headvo.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), doperatedate)) ;
		//设置表体
		List<TzpzBVO> list = new ArrayList<>();
		TzpzBVO tzpzvo = null;
		for(int i= 0 ;i<bodyvos.length;i++){
			tzpzvo = (TzpzBVO)bodyvos[i].clone();
			if(tzpzvo.getJfmny() != null && tzpzvo.getJfmny().doubleValue() !=0 ){
				tzpzvo.setJfmny(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getJfmny()), new DZFDouble(-1)));
				tzpzvo.setNnumber(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getNnumber()), new DZFDouble(-1)));
				tzpzvo.setZy("冲销"+vo.getPeriod()+"月暂估");
				tzpzvo.setPk_tzpz_b(null);
				tzpzvo.setPk_tzpz_h(null);
				tzpzvo.setDfmny(null);
				list.add(tzpzvo);
			}else if(tzpzvo.getDfmny() != null){
				tzpzvo.setDfmny(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getDfmny()), new DZFDouble(-1)));
				tzpzvo.setJfmny(null);
				tzpzvo.setNnumber(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getNnumber()), new DZFDouble(-1)));
				tzpzvo.setZy("冲销"+vo.getPeriod()+"月暂估");
				tzpzvo.setPk_tzpz_b(null);
				tzpzvo.setPk_tzpz_h(null);
				list.add(tzpzvo);
			}else{
                tzpzvo.setDfmny(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getDfmny()), new DZFDouble(-1)));
                tzpzvo.setJfmny(null);
                tzpzvo.setNnumber(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getNnumber()), new DZFDouble(-1)));
                tzpzvo.setZy("冲销"+vo.getPeriod()+"月暂估");
                tzpzvo.setPk_tzpz_b(null);
                tzpzvo.setPk_tzpz_h(null);
                list.add(tzpzvo);
            }
		}
		headvo.setChildren(list.toArray(new TzpzBVO[list.size()]));
		return headvo;
	}

	// 红冲，不启用库存
	public TzpzHVO queryNextcodeNoIC(QmclVO vo,TzpzHVO billVO) {
		//设置表头
		TzpzHVO  headvo = (TzpzHVO)billVO.clone();
		TzpzBVO[] bodyvos = (TzpzBVO[])billVO.getChildren();
		DZFDate doperatedate = getPeroidDZFDate(vo).getDateAfter(1);
		headvo.setDoperatedate(doperatedate);
		headvo.setPk_tzpz_h(null);
		headvo.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), doperatedate)) ;
		//设置表体
		List<TzpzBVO> list = new ArrayList<>();
		TzpzBVO tzpzvo = null;
		for(int i= 0 ;i<bodyvos.length;i++){
			tzpzvo = (TzpzBVO)bodyvos[i].clone();
			if(tzpzvo.getJfmny() != null && tzpzvo.getJfmny().doubleValue() !=0 ){
				tzpzvo.setJfmny(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getJfmny()), new DZFDouble(-1)));
				tzpzvo.setNnumber(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getNnumber()), new DZFDouble(-1)));
				tzpzvo.setDfmny(null);
				tzpzvo.setZy("冲销"+vo.getPeriod()+"月暂估");
				tzpzvo.setPk_tzpz_b(null);
				tzpzvo.setPk_tzpz_h(null);
				list.add(tzpzvo);
			}else if(tzpzvo.getDfmny() != null){
				tzpzvo.setDfmny(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getDfmny()), new DZFDouble(-1)));
				tzpzvo.setJfmny(null);
				tzpzvo.setNnumber(SafeCompute.multiply(VoUtils.getDZFDouble(tzpzvo.getNnumber()), new DZFDouble(-1)));
				tzpzvo.setZy("冲销"+vo.getPeriod()+"月暂估");
				tzpzvo.setPk_tzpz_b(null);
				tzpzvo.setPk_tzpz_h(null);
				list.add(tzpzvo);
			}

		}
		headvo.setChildren(list.toArray(new TzpzBVO[list.size()]));
		return headvo;
	}
}
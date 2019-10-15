package com.dzf.zxkj.platform.service.report.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.report.ZcfzRptSetVo;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.util.VoUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 资产负债取数(村集体)
 * @author zhangj
 *
 */
public class OtherSystemForZcfzImpl {
	
	
	
	public ZcFzBVO[] getCompanyZCFZBVOs(Map<String, FseJyeVO> map, String[] hasyes, Map<String, YntCpaccountVO> mapc,
										SingleObjectBO sbo, String hy, String pk_corp) throws BusinessException {

		if(StringUtil.isEmpty(hy)){
			throw new BusinessException("行业为空!");
		}
		// 查询设置数据
		SQLParameter sp = new SQLParameter();
		sp.addParam(hy);
		ZcfzRptSetVo[] setvos = (ZcfzRptSetVo[]) sbo.queryByCondition(ZcfzRptSetVo.class,
				" nvl(dr,0)=0 and pk_trade_accountschema= ? order by ordernum", sp);

		if (setvos == null || setvos.length == 0) {
			throw new BusinessException("该制度暂不支持,敬请期待");
		}
		
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");

		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		
		List<ZcFzBVO> zcfzlist = new ArrayList<ZcFzBVO>();

		for (ZcfzRptSetVo setvo : setvos) {
			ZcFzBVO zcfzbvo = new ZcFzBVO();
			if(!StringUtil.isEmpty(setvo.getZcname())){
				zcfzbvo.setZc(setvo.getZcname().replace(" ", "　"));
			}
			if(!StringUtil.isEmpty(setvo.getFzname())){
				zcfzbvo.setFzhsyzqy(setvo.getFzname().replace(" ", "　"));
			}
			zcfzbvo.setHc1(setvo.getZchc());// 资产行次
			zcfzbvo.setHc2(setvo.getFzhc());// 负债行次
			zcfzbvo.setHc1_id(setvo.getZchc_id());
			zcfzbvo.setHc2_id(setvo.getFzhc_id());
			// 设置金额
			String zckm = getkm(hasyes,setvo,0);
			String fzkm = getkm(hasyes,setvo,1);
			String[] getzckms = getkms(zckm);
			String[] getfzkms = getkms(fzkm);
			
			//处理合计信息
			//资产
			if(getzckms!=null && getzckms.length>0){
				if(getzckms[0].indexOf("=")>=0){
					total(zcfzbvo,zcfzlist,getzckms,true);
				}else{
					for(String str:getzckms){
						if(str.indexOf("(重)")>=0){
							getZCFZBVO1(map, zcfzbvo, true, mapc,gl_accountcoderule,newrule,str.replace("(重)", ""));
						}else{
							getZCFZBVO(map, zcfzbvo, true, gl_accountcoderule,newrule,str );
						}
					}
				}
			}
			//负债
			if(getfzkms!=null && getfzkms.length>0){
				if(getfzkms[0].indexOf("=")>=0){
					total(zcfzbvo,zcfzlist,getfzkms,false);
				}else{
					for(String str:getfzkms){
						if(str.indexOf("(重)")>=0){
							getZCFZBVO1(map, zcfzbvo, false, mapc, gl_accountcoderule,newrule,str.replace("(重)", ""));
						}else{
							getZCFZBVO(map, zcfzbvo, false, gl_accountcoderule,newrule, str);
						}
					}
					
				}
			}
			

			zcfzlist.add(zcfzbvo);
		}

		return zcfzlist.toArray(new ZcFzBVO[0]);
	}
	
	/**
	 * 
	 * @param hasyes
	 * @param setvo
	 * @param lx 0是资产 1是负债
	 * @return
	 */
	private String getkm(String[] hasyes, ZcfzRptSetVo setvo,Integer lx) {
		if("Y".equals(hasyes[1])){//应收和预收
			if(!StringUtil.isEmpty(setvo.getZcname())
				&&	"  应收账款".equals(setvo.getZcname()) && lx == 0){
				return setvo.getZckm_re();
			}else if(!StringUtil.isEmpty(setvo.getFzname())
					&&	"  预收账款".equals(setvo.getFzname())  && lx == 1){
				return setvo.getFzkm_re();
			}
		}
		if("Y".equals(hasyes[2])){//应付预付
			if(!StringUtil.isEmpty(setvo.getZcname())
					&&	"  预付账款".equals(setvo.getZcname())  && lx == 0){
					return setvo.getZckm_re();
				}else if(!StringUtil.isEmpty(setvo.getFzname())
						&&	"  应付账款".equals(setvo.getFzname())  && lx == 1){
					return setvo.getFzkm_re();
				}
		}
		if("Y".equals(hasyes[3])){//其他应收，其他应付
			if(!StringUtil.isEmpty(setvo.getZcname())
					&&	"  其他应收款".equals(setvo.getZcname())  && lx == 0 ){
					return setvo.getZckm_re();
				}else if(!StringUtil.isEmpty(setvo.getFzname())
						&&	"  其他应付款".equals(setvo.getFzname())  && lx == 1){
					return setvo.getFzkm_re();
				}
		}
		if(lx == 0){
			return setvo.getZckm();
		}else{
			return setvo.getFzkm();
		}
	}

	private void total(ZcFzBVO zcfzbvo, List<ZcFzBVO> zcfzlist, String[] getfzkms, boolean iszc) {
		DZFDouble ncye1 = VoUtils.getDZFDouble(zcfzbvo.getNcye1());
		DZFDouble qmye1 = VoUtils.getDZFDouble(zcfzbvo.getQmye1());
		DZFDouble ncye2 = VoUtils.getDZFDouble(zcfzbvo.getNcye2());
		DZFDouble qmye2 = VoUtils.getDZFDouble(zcfzbvo.getQmye2());

		String xm = "";
		for (int i = 0; i < getfzkms.length; i++) {
			Integer multify = 1;
			if (getfzkms[i].indexOf("=") >= 0) {
				xm = getfzkms[i].substring(getfzkms[i].indexOf("=") + 1);
			} else {
				xm = getfzkms[i];
			}
			if(xm.startsWith("-")){
				xm = xm.substring(1);
				multify = -1;
			}
			
			for (ZcFzBVO vo : zcfzlist) {
				if (iszc) {
					if (xm.equals(vo.getHc1())) {
						ncye1 = SafeCompute.add(ncye1, VoUtils.getDZFDouble(vo.getNcye1()).multiply(multify));
						qmye1 = SafeCompute.add(qmye1, VoUtils.getDZFDouble(vo.getQmye1()).multiply(multify));
					}
				} else {
					if (xm.equals(vo.getHc2())) {
						ncye2 = SafeCompute.add(ncye2, VoUtils.getDZFDouble(vo.getNcye2()).multiply(multify));
						qmye2 = SafeCompute.add(qmye2, VoUtils.getDZFDouble(vo.getQmye2()).multiply(multify));
					}
				}
			}
		}

		zcfzbvo.setNcye1(ncye1);
		zcfzbvo.setQmye1(qmye1);
		zcfzbvo.setNcye2(ncye2);
		zcfzbvo.setQmye2(qmye2);

	}

	private String[] getkms(String zckm) {
		String regex = ",";
		if(StringUtil.isEmpty(zckm)){
			return null;
		}
		String[] strs = zckm.split(regex);
		//去掉借贷括号
		List<String> list = new ArrayList<String>();
		String value = "";
		for(String str:strs){
			value = str.replace("(借)", "");
			value = value.replace("(贷)", "");
			list.add(value);
		}
		return list.toArray(new String[0]);
	}

	public void clearTempVO(ZcFzBVO votemp){
		votemp.setNcye1(DZFDouble.ZERO_DBL);
		votemp.setQmye1(DZFDouble.ZERO_DBL);
		votemp.setNcye2(DZFDouble.ZERO_DBL);
		votemp.setQmye2(DZFDouble.ZERO_DBL);
	}
	
	
	private ZcFzBVO getZCFZBVO(Map<String,FseJyeVO> map, ZcFzBVO vo,boolean is1,
			ICpaccountCodeRuleService gl_accountcoderule ,String newrule,String...kms){
		
		List<FseJyeVO> ls=null;
		DZFDouble ufd=null;
		int len=kms==null?0:kms.length;
		
		StringBuffer appkms = new StringBuffer();
		if(is1){
			appkms= new StringBuffer(StringUtil.isEmpty(vo.getZcconkms()) ? "" : vo.getZcconkms()+",");
		}else{
			appkms= new StringBuffer(StringUtil.isEmpty(vo.getFzconkms()) ? "" : vo.getFzconkms()+",");
		}
		String kmtemp = "";
		for (int i = 0; i < len; i++) {
			kmtemp = kms[i];
			Integer multily = 1;
			if(kmtemp.startsWith("-")){
				multily = -1;
				kmtemp = kms[i].substring(1);
			}
			ls=getData(map, kmtemp, gl_accountcoderule,newrule);
			appkms.append(kmtemp+",");
			if(ls==null)continue;
			for (FseJyeVO fvo : ls) {
				if(is1){
					ufd=VoUtils.getDZFDouble(vo.getQmye1());
					DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf =  VoUtils.getDZFDouble(fvo.getQmdf());
					ufd=ufd.add(qmjf.add(qmdf).multiply(multily));//
					
					vo.setQmye1(ufd);
					ufd=VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = VoUtils.getDZFDouble(fvo.getQcdf());
					ufd=ufd.add(qcjf.add(qcdf).multiply(multily));
					vo.setNcye1(ufd) ;
					
					//本月期初
					ufd=VoUtils.getDZFDouble(vo.getQcye1());
					DZFDouble byqcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcjf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcjf());
					DZFDouble bqqcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getLastmqcdf()).multiply(-1) : VoUtils.getDZFDouble(fvo.getLastmqcdf());
					ufd=ufd.add(byqcjf.add(bqqcdf).multiply(multily));
					vo.setQcye1(ufd);
				}else{
					ufd=VoUtils.getDZFDouble(vo.getQmye2());
					DZFDouble qmjf = VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = VoUtils.getDZFDouble(fvo.getQmdf());
					ufd=ufd.add(qmjf.add(qmdf).multiply(multily));//默认 贷方减借方
					vo.setQmye2(ufd);
					
					
					//年初
					ufd=VoUtils.getDZFDouble(vo.getNcye2());
					DZFDouble qcjf =  VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf =  VoUtils.getDZFDouble(fvo.getQcdf());
					ufd=ufd.add(qcjf.add(qcdf).multiply(multily));//默认 贷方减借方
					vo.setNcye2(ufd) ;
					
					//本月期初
					ufd=VoUtils.getDZFDouble(vo.getQcye2());
					ufd=ufd.add(SafeCompute.add(fvo.getLastmqcjf(), fvo.getLastmqcdf()).multiply(multily));//只有一方有值
					vo.setQcye2(ufd) ;
				}
			}
			
			if(is1){
				vo.setZcconkms(appkms.toString().substring(0,appkms.length()-1));
			}else{
				vo.setFzconkms(appkms.toString().substring(0,appkms.length()-1));
			}
		}
		return vo;
	}
	
	//往来分析
	private ZcFzBVO getZCFZBVO1(Map<String, FseJyeVO> map, ZcFzBVO vo, boolean is1, Map<String, YntCpaccountVO> mapc,
			ICpaccountCodeRuleService gl_accountcoderule ,String newrule,
			String... kms) {

		List<FseJyeVO> ls = null;
		DZFDouble ufd = null;
		int len = kms == null ? 0 : kms.length;
		StringBuffer appkms = null;
		if (is1) {
			appkms = new StringBuffer(vo.getZcconkms() == null ? "" : vo.getZcconkms() + ",");
		} else {
			appkms = new StringBuffer(vo.getFzconkms() == null ? "" : vo.getFzconkms() + ",");
		}
		for (int i = 0; i < len; i++) {
			ls = getData1(map, kms[i], mapc,gl_accountcoderule,newrule);
			if(StringUtil.isEmpty(kms[i]) && kms[i].startsWith("-")){
				appkms.append(kms[i].substring(1) + ",");
			}else{
				appkms.append(kms[i] + ",");
			}
			if (ls == null)
				continue;
			for (FseJyeVO fvo : ls) {
				if (is1) {// 资产
					ufd = VoUtils.getDZFDouble(vo.getQmye1());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmdf());
					if ("借".equals(fvo.getFx()) && qmjf.doubleValue() > 0) {
						ufd = ufd.add(qmjf);
					} else if (qmdf.doubleValue() < 0) {
						ufd = ufd.sub(qmdf);
					}
					vo.setQmye1(ufd);
					ufd = VoUtils.getDZFDouble(vo.getNcye1());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcdf());
					if ("借".equals(fvo.getFx()) && qcjf.doubleValue() > 0) {
						ufd = ufd.add(qcjf);
					} else if (qcdf.doubleValue() < 0) {
						ufd = ufd.sub(qcdf);
					}
					vo.setNcye1(ufd);
				} else {// 负债
					ufd = VoUtils.getDZFDouble(vo.getQmye2());
					DZFDouble qmjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmjf());
					DZFDouble qmdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQmdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQmdf());
					if ("贷".equals(fvo.getFx()) && qmdf.doubleValue() > 0) {
						ufd = ufd.add(qmdf);
					} else if (qmjf.doubleValue() < 0) {
						ufd = ufd.sub(qmjf);
					}
					vo.setQmye2(ufd);
					//
					ufd = VoUtils.getDZFDouble(vo.getNcye2());
					DZFDouble qcjf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcjf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcjf());
					DZFDouble qcdf = fvo.getKmbm().equals("1407") ? VoUtils.getDZFDouble(fvo.getQcdf()).multiply(-1)
							: VoUtils.getDZFDouble(fvo.getQcdf());
					if ("贷".equals(fvo.getFx()) && qcdf.doubleValue() > 0) {
						ufd = ufd.add(qcdf);
					} else if (qcjf.doubleValue() < 0) {
						ufd = ufd.sub(qcjf);
					}
					vo.setNcye2(ufd);
				}
			}
		}
		if (is1) {
			vo.setZcconkms(appkms.toString().substring(0, appkms.length() - 1));
		} else {
			vo.setFzconkms(appkms.toString().substring(0, appkms.length() - 1));
		}

		return vo;
	}

	private List<FseJyeVO> getData1(Map<String, FseJyeVO> map, String kmtemp, Map<String, YntCpaccountVO> mapc
			,ICpaccountCodeRuleService gl_accountcoderule,String newrule) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		//考虑科目升级
		String km = gl_accountcoderule.getNewRuleCode(kmtemp, DZFConstant.ACCOUNTCODERULE, newrule);
		for (FseJyeVO fsejyevo : map.values()) {
			String kmbm = null;
			if (fsejyevo.getKmbm().indexOf("_") > 0) {
				kmbm = fsejyevo.getKmbm().split("_")[0];
			} else {
				kmbm = fsejyevo.getKmbm();
			}
			if (kmbm.startsWith(km)) {
				YntCpaccountVO xs = mapc.get(kmbm);
				if (xs.getIsleaf() != null && xs.getIsleaf().booleanValue()) {
					if (xs.getIsfzhs().indexOf("1") >= 0 && fsejyevo.getKmbm().indexOf("_") >= 0) {// 包含辅助项目
						list.add(fsejyevo);
					} else if (xs.getIsfzhs().indexOf("1") < 0) {// 如果不包含辅助项目处理
						list.add(fsejyevo);
					}
				}
			}
		}
		return list;
	}
	
	private List<FseJyeVO> getData(Map<String,FseJyeVO> map,String kmtemp,ICpaccountCodeRuleService gl_accountcoderule,String newrule){
		//考虑科目升级
		String km = gl_accountcoderule.getNewRuleCode(kmtemp, DZFConstant.ACCOUNTCODERULE, newrule);
		List<FseJyeVO> list=new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if(fsejyevo.getKmbm().equals(km)){//已经合计过了，不要用startwidh
				list.add(fsejyevo);
				break;
			}
		}
		return list;
	}


}

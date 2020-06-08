package com.dzf.zxkj.report.utils;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZcFzBVO;
import com.dzf.zxkj.platform.model.report.ZcfzRptSetVo;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 资产负债取数(村集体)
 * @author zhangj
 *
 */
@SuppressWarnings("all")
public class OtherSystemForZcfzImpl {
	

	private IZxkjPlatformService zxkjPlatformService;

	public OtherSystemForZcfzImpl(IZxkjPlatformService zxkjPlatformService) {
		this.zxkjPlatformService = zxkjPlatformService;
	}

	public ZcFzBVO[] getCompanyZCFZBVOs(Map<String, FseJyeVO> map, String[] hasyes, Map<String, YntCpaccountVO> mapc,
										SingleObjectBO sbo, String hy, String pk_corp, String versionno) throws BusinessException {

		if(StringUtil.isEmpty(hy)){
			throw new BusinessException("行业为空!");
		}
		// 查询设置数据
		SQLParameter sp = new SQLParameter();
		sp.addParam(hy);
		StringBuffer wherepart = new StringBuffer();
		wherepart.append(" nvl(dr,0)=0 and pk_trade_accountschema= ? ");
		if (!StringUtil.isEmpty(versionno)) {
			sp.addParam(versionno);
			wherepart.append(" and versionno = ? ");
		}
		wherepart.append(" order by ordernum ");
		ZcfzRptSetVo[] setvos = (ZcfzRptSetVo[]) sbo.queryByCondition(ZcfzRptSetVo.class,
				wherepart.toString(), sp);

		if (setvos == null || setvos.length == 0) {
			throw new BusinessException("该制度暂不支持,敬请期待");
		}
		
		String newrule = zxkjPlatformService.queryAccountRule(pk_corp);
		
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
			String zckm = getkm(hasyes,setvo,0,hy);
			String fzkm = getkm(hasyes,setvo,1,hy);
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
							getZCFZBVO1(map, zcfzbvo, true, mapc,false,newrule,hy,str.replace("(重)", ""));
						}else{
							getZCFZBVO(map, zcfzbvo, true,newrule,hy,str );
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
						if ("Y".equals(hasyes[4]) && "00000100AA10000000000BMF".equals(hy)
								&& "　应交税费".equals(setvo.getFzname())) {
							//应交税费重分类取数
							zcfzbvo = getZcFzBVO_YJSF_CFL(map, mapc, newrule, zcfzbvo,hy);
						} else {
							if(str.indexOf("(重)")>=0){
								getZCFZBVO1(map, zcfzbvo, false, mapc,false,newrule,hy,str.replace("(重)", ""));
							}else{
								getZCFZBVO(map, zcfzbvo, false,newrule,hy, str);
							}
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
	private String getkm(String[] hasyes, ZcfzRptSetVo setvo,Integer lx, String pk_trade_accountschema) {
		if ("00000100AA10000000000BMF".equals(pk_trade_accountschema)){
			if("Y".equals(hasyes[1])){//应收和预收
				if(!StringUtil.isEmpty(setvo.getZcname())
						&&	"  应收账款".equals(setvo.getZcname()) && lx == 0){
					return setvo.getZckm_re();
				}else if(!StringUtil.isEmpty(setvo.getFzname())
						&&	"　预收款项".equals(setvo.getFzname())  && lx == 1){
					return setvo.getFzkm_re();
				}
			}
			if("Y".equals(hasyes[2])){//应付预付
				if(!StringUtil.isEmpty(setvo.getZcname())
						&&	"　预付款项".equals(setvo.getZcname())  && lx == 0){
					return setvo.getZckm_re();
				}else if(!StringUtil.isEmpty(setvo.getFzname())
						&&	"  应付账款".equals(setvo.getFzname())  && lx == 1){
					return setvo.getFzkm_re();
				}
			}
			if("Y".equals(hasyes[3])){//其他应收，其他应付
				if(!StringUtil.isEmpty(setvo.getZcname())
						&&	"其他应收款".equals(setvo.getZcname())  && lx == 0 ){
					return setvo.getZckm_re();
				}else if(!StringUtil.isEmpty(setvo.getFzname())
						&&	"　其他应付款".equals(setvo.getFzname())  && lx == 1){
					return setvo.getFzkm_re();
				}
			}
		} else {

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
	
	
	private ZcFzBVO getZCFZBVO(Map<String,FseJyeVO> map, ZcFzBVO vo,boolean is1,String newrule,String pk_trade_accountschema,String...kms){
		
		List<FseJyeVO> ls=null;
		DZFDouble ufd=null;
		int len=kms==null?0:kms.length;
		
		StringBuffer appkms = new StringBuffer();
		if(is1){
			appkms= new StringBuffer(StringUtil.isEmpty(vo.getZcconkms()) ? "" : vo.getZcconkms()+",");
		}else{
			appkms= new StringBuffer(StringUtil.isEmpty(vo.getFzconkms()) ? "" : vo.getFzconkms()+",");
		}
		// 获取工程施工，工程结算差额
		DZFDouble[] cefrom0102 = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL };// getCeFrom0102(map,"4401","4402");
		String kmtemp = "";
		for (int i = 0; i < len; i++) {
			kmtemp = kms[i];
			Integer multily = 1;
			if(kmtemp.startsWith("-")){
				multily = -1;
				kmtemp = kms[i].substring(1);
			}
			ls=getData(map, kmtemp,newrule);
			appkms.append(kmtemp+",");
			if(ls==null)continue;
			if ( !StringUtil.isEmpty(vo.getZc()) && vo.getZc().replace("　","").equals("存货") && (kms[i].equals("5401") || kms[i].equals("5402"))) {// 07工程结算
				cefrom0102 = getCeFrom0102(map, "5401", "5402");
				continue;
			}
			if ((!StringUtil.isEmpty(vo.getFzhsyzqy())
			&& vo.getFzhsyzqy().replace("　","").equals("预收款项")) && (kms[i].equals("5401") || kms[i].equals("5402"))) {// 07
				// 工程结算
				cefrom0102 = getCeFrom0102(map, "5401", "5402");
				continue;
			}
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
			// 07 13 写在了一块了
			if ("00000100AA10000000000BMF".equals(pk_trade_accountschema)) {
				if (!StringUtil.isEmpty(vo.getZc()) && vo.getZc().replace("　","").equals("存货") ) {
					// 获取工程施工，工程结算差额
					if (cefrom0102[0].doubleValue() > 0) {
						vo.setNcye1(SafeCompute.add(vo.getNcye1(), cefrom0102[0]));
					}
					if (cefrom0102[1].doubleValue() > 0) {
						vo.setQmye1(SafeCompute.add(vo.getQmye1(), cefrom0102[1]));
					}
				}
				if ((!StringUtil.isEmpty(vo.getFzhsyzqy())
						&& (vo.getFzhsyzqy().replace("　","").equals("预收款项")
				         || vo.getFzhsyzqy().replace("　","").equals("预收账款")))) {
					// 获取工程施工，工程结算差额
					if (cefrom0102[0].doubleValue() < 0) {
						vo.setNcye2(SafeCompute.add(vo.getNcye2(), cefrom0102[0].multiply(-1)));
					}
					if (cefrom0102[1].doubleValue() < 0) {
						vo.setQmye2(SafeCompute.add(vo.getQmye2(), cefrom0102[1].multiply(-1)));
					}
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
								boolean bthislevel,String newrule,String pk_trade_accountschema,String... kms) {

		List<FseJyeVO> ls = null;
		DZFDouble ufd = null;
		int len = kms == null ? 0 : kms.length;
		StringBuffer appkms = null;
		if (is1) {
			appkms = new StringBuffer(vo.getZcconkms() == null ? "" : vo.getZcconkms() + ",");
		} else {
			appkms = new StringBuffer(vo.getFzconkms() == null ? "" : vo.getFzconkms() + ",");
		}
		DZFDouble[] cefrom0102 = new DZFDouble[] { DZFDouble.ZERO_DBL, DZFDouble.ZERO_DBL };
		for (int i = 0; i < len; i++) {
			ls = getData1(map, kms[i], mapc,bthislevel,newrule);
			if(StringUtil.isEmpty(kms[i]) && kms[i].startsWith("-")){
				appkms.append(kms[i].substring(1) + ",");
			}else{
				appkms.append(kms[i] + ",");
			}
			if ("00000100AA10000000000BMF".equals(pk_trade_accountschema)) {
				if ((!StringUtil.isEmpty(vo.getFzhsyzqy())
						&& vo.getFzhsyzqy().replace("　","").equals("预收款项")) && (kms[i].equals("5401") || kms[i].equals("5402"))) {// 07
					// 工程结算
					cefrom0102 = getCeFrom0102(map, "5401", "5402");
					continue;
				}
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
		if ("00000100AA10000000000BMF".equals(pk_trade_accountschema)) {
			if ((!StringUtil.isEmpty(vo.getFzhsyzqy())
					&& (vo.getFzhsyzqy().replace("　","").equals("预收款项")
					|| vo.getFzhsyzqy().replace("　","").equals("预收账款")))) {
				// 获取工程施工，工程结算差额
				if (cefrom0102[0].doubleValue() < 0) {
					vo.setNcye2(SafeCompute.add(vo.getNcye2(), cefrom0102[0].multiply(-1)));
				}
				if (cefrom0102[1].doubleValue() < 0) {
					vo.setQmye2(SafeCompute.add(vo.getQmye2(), cefrom0102[1].multiply(-1)));
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
			,boolean bthislevel,String newrule) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		//考虑科目升级
		String km = zxkjPlatformService.getNewRuleCode(kmtemp, DZFConstant.ACCOUNTCODERULE, newrule);
		for (FseJyeVO fsejyevo : map.values()) {
			String kmbm = null;
			if (fsejyevo.getKmbm().indexOf("_") > 0) {
				kmbm = fsejyevo.getKmbm().split("_")[0];
			} else {
				kmbm = fsejyevo.getKmbm();
			}
			if(bthislevel){
				if(kmbm.equals(km)){
					list.add(fsejyevo);
					return list;
				}
			}else{
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
		}
		return list;
	}
	
	private List<FseJyeVO> getData(Map<String,FseJyeVO> map,String kmtemp,String newrule){
		//考虑科目升级
		//当期科目非DZFConstant.ACCOUNTCODERULE 则不用升级
		String km = "";
		if(bexitsRule(kmtemp, DZFConstant.ACCOUNTCODERULE)) {
			km = zxkjPlatformService.getNewRuleCode(kmtemp, DZFConstant.ACCOUNTCODERULE, newrule);
		}else {
			km = kmtemp;
		}
//		String km = zxkjPlatformService.getNewRuleCode(kmtemp, DZFConstant.ACCOUNTCODERULE, newrule);
		List<FseJyeVO> list=new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if(fsejyevo.getKmbm().equals(km)){//已经合计过了，不要用startwidh
				list.add(fsejyevo);
				break;
			}
		}
		return list;
	}

	private boolean bexitsRule(String kmtemp, String accountcoderule) {
		String[] rules = accountcoderule.split("/");
		int sum = 0;
		for (String str: rules) {
			sum = sum + Integer.parseInt(str);
			if (kmtemp.length() == sum){
				return true;
			}
		}
		return false;
	}

	private ZcFzBVO getZcFzBVO_YJSF_CFL(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mapc,
										String queryAccountRule, ZcFzBVO vo6, String pk_trade_accountschema) {
		List<String> list = getYSJFKmList(queryAccountRule);
		ZcFzBVO vo6temp = new ZcFzBVO();
		vo6temp = getZCFZBVO1(map, vo6temp, false, mapc,true,queryAccountRule,
				pk_trade_accountschema,list.toArray(new String[0]));
		//2221下的所有科目
		List<String> orderlist = new ArrayList<String>();
		String filtercode = zxkjPlatformService.getNewRuleCode("222114",DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		for (Map.Entry<String, YntCpaccountVO> entry : mapc.entrySet()) {
			if (entry.getValue() != null && entry.getValue().getAccountcode().startsWith("2221")
					&& entry.getValue().getAccountlevel() ==2
			) {//只是核算2级科目
				if (!list.contains(entry.getValue().getAccountcode())
						&& !filtercode.equals(entry.getValue().getAccountcode()) ) {
					orderlist.add(entry.getValue().getAccountcode());
				}
			}
		}
		// 不需要重分类的科目
		vo6 = getZCFZBVO(map, vo6, false,queryAccountRule,
				pk_trade_accountschema, orderlist.toArray(new String[0]));
		vo6.setNcye2(SafeCompute.add(vo6temp.getNcye2(), vo6.getNcye2()));
		vo6.setQmye2(SafeCompute.add(vo6temp.getQmye2(), vo6.getQmye2()));
		vo6.setFzconkms(vo6.getFzconkms()+","+vo6temp.getFzconkms());
		return vo6;
	}

	/**
	 * 应交税费科目list
	 * @param queryAccountRule
	 * @return
	 */
	private List<String> getYSJFKmList(String queryAccountRule) {
		// 需要重分类的科目
		String[] kms = new String[] { "222101", "222109", "222110", "222113", "222115" };
		return  Arrays.asList(kms);
	}

	private DZFDouble[] getCeFrom0102(Map<String, FseJyeVO> map, String str1, String str2) {
		DZFDouble qcd1 = DZFDouble.ZERO_DBL;
		DZFDouble qcd2 = DZFDouble.ZERO_DBL;
		DZFDouble qmd1 = DZFDouble.ZERO_DBL;
		DZFDouble qmd2 = DZFDouble.ZERO_DBL;
		FseJyeVO fsevo1 = null;
		FseJyeVO fsevo2 = null;
		if (!StringUtil.isEmpty(str1)) {
			fsevo1 = map.get(str1);
		}
		if (!StringUtil.isEmpty(str2)) {
			fsevo2 = map.get(str2);
		}
		if (fsevo1 != null) {
			qcd1 = SafeCompute.add(fsevo1.getQcjf(), fsevo1.getQcdf());
			qmd1 = SafeCompute.add(fsevo1.getQmjf(), fsevo1.getQmdf());
		}
		if (fsevo2 != null) {
			qcd2 = SafeCompute.add(fsevo2.getQcjf(), fsevo2.getQcdf());
			qmd2 = SafeCompute.add(fsevo2.getQmjf(), fsevo2.getQmdf());
		}

		return new DZFDouble[] { qcd1.sub(qcd2), qmd1.sub(qmd2) };
	}
}

package com.dzf.zxkj.report.utils;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbRptSetVo;
import com.dzf.zxkj.platform.model.report.LrbVO;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 利润表(企业会计制度)
 * 
 * @author zhangj
 *
 */
public class OtherSystemForLrb {

	public LrbVO[] getCompanyVos(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String qjz, String pk_corp,
								 String xmmcid, SingleObjectBO sbo, String hy) {

		if(StringUtil.isEmpty(hy)){
			throw new BusinessException("行业信息不能为空!");
		}
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(hy);
		LrbRptSetVo[] setvos = (LrbRptSetVo[]) sbo.queryByCondition(LrbRptSetVo.class,
				"nvl(dr,0)=0 and pk_trade_accountschema = ? ", sp);

		if (setvos == null || setvos.length == 0) {
			throw new BusinessException("该制度暂不支持,敬请期待");
		}

		List<LrbVO> list = new ArrayList<LrbVO>();
		for (LrbRptSetVo setvo : setvos) {
			LrbVO lrbvo = new LrbVO();
			if(!StringUtil.isEmpty(setvo.getXm())){
				lrbvo.setXm(setvo.getXm().replace("  ", "　"));
			}
			if(!StringUtil.isEmpty(setvo.getXm2())){
				lrbvo.setXm2(setvo.getXm2().replace("  ", "　"));
			}
			lrbvo.setHs(setvo.getHc());
			lrbvo.setHs_id(setvo.getHc_id());
			lrbvo.setHs2(setvo.getHc2());
			lrbvo.setPk_corp(IGlobalConstants.currency_corp);
			lrbvo.setLevel(setvo.getIlevel());
			String[] kms = getKms(setvo.getKm());
			if (kms != null && kms.length > 0) {
				lrbvo = calXmValue(map, mp, qjz, xmmcid, list, lrbvo, kms,"vconkms");
			}
			
			String[] kms2 = getKms(setvo.getKm2());
			if (kms2 != null && kms2.length > 0) {
				lrbvo = calXmValue(map, mp, qjz, xmmcid, list, lrbvo, kms2,"vconkms2");
			}
			
			
			list.add(lrbvo);
		}

		return list.toArray(new LrbVO[0]);
	}

	private LrbVO calXmValue(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String qjz, String xmmcid,
			List<LrbVO> list, LrbVO lrbvo, String[] kms,String conkmsname) {
		if (kms[0].indexOf("=") >= 0) {
			totalLrb(list, lrbvo, kms);
		}else {
			lrbvo = getLRBVO(map, mp, qjz, xmmcid, lrbvo, kms);
			StringBuffer conkms = new StringBuffer();
			for(String str:kms){
				if(str.startsWith("-")){
					conkms.append(str.substring(1)+",");
				}else{
					conkms.append(str+",");
				}
			}
			lrbvo.setAttributeValue(conkmsname, conkms.substring(0, conkms.length()-1).toString());
//			lrbvo.setVconkms(conkms.substring(0, conkms.length()-1));
		}
		return lrbvo;
	}

	private void totalLrb(List<LrbVO> list, LrbVO lrbvo, String[] kms) {

		DZFDouble byje = VoUtils.getDZFDouble(lrbvo.getByje());

		DZFDouble bnljje = VoUtils.getDZFDouble(lrbvo.getBnljje());

		String tvalue = "";
		for (int i = 0; i < kms.length; i++) {
			if (i == 0) {
				tvalue = kms[i].substring(kms[i].indexOf("=") + 1);
			} else {
				tvalue = kms[i];
			}
			Integer multify = 1;
			if(tvalue.startsWith("-")){
				multify = -1;
				tvalue = tvalue.substring(1);
			}
			for (LrbVO vo : list) {
				if (tvalue.equals(vo.getHs())) {
					byje = SafeCompute.add(byje, VoUtils.getDZFDouble(vo.getByje()).multiply(multify) );
					bnljje = SafeCompute.add(bnljje, VoUtils.getDZFDouble( vo.getBnljje()).multiply(multify));
				}
			}
		}

		lrbvo.setByje(byje);
		lrbvo.setBnljje(bnljje);

	}

	private String[] getKms(String km) {
		String regex = ",";
		if(StringUtil.isEmpty(km)){
			return null;
		}
		String[] strs = km.split(regex);
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

	private LrbVO getLRBVO(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq, String xmmcid,
			LrbVO vo, String... kms) {

		DZFDouble ufd = null;
		int direction = 0;
		int len = kms == null ? 0 : kms.length;
		YntCpaccountVO km = null;
		List<FseJyeVO> ls = null;
		String km_temp = "";
		
		for (int i = 0; i < len; i++) {
			Integer multify = 1;
			km_temp =kms[i] ;
			if(km_temp.startsWith("-")){
				multify = -1;
				km_temp = km_temp.substring(1);
			}
			km = mp.get(km_temp);
			if (km == null)
				continue;
			direction = km.getDirection();
			ls = getData(map, km_temp, mp, xmmcid);
			for (FseJyeVO fvo : ls) {
				if (direction == 0) {
					if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
						ufd = VoUtils.getDZFDouble(vo.getByje());

						vo.setByje(ufd.add(SafeCompute.sub(fvo.getEndfsjf(), fvo.getEndfsdf()).multiply(multify)));
					}
					ufd = VoUtils.getDZFDouble(vo.getBnljje());
					vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getJftotal(),fvo.getDftotal()).multiply(multify)));
				} else {
					if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
						ufd = VoUtils.getDZFDouble(vo.getByje());
						vo.setByje(ufd.add(SafeCompute.sub(fvo.getEndfsdf(),fvo.getEndfsjf()).multiply(multify)));
					}
					ufd = VoUtils.getDZFDouble(vo.getBnljje());
					vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getDftotal(),fvo.getJftotal()).multiply(multify)));
				}
			}
		}
		return vo;
	}

	private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km, Map<String, YntCpaccountVO> mp,
			String xmmcid) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if (!StringUtil.isEmpty(xmmcid)) {// 查询对应某个项目的利润表数据
				if (!StringUtil.isEmpty(fsejyevo.getPk_km()) && fsejyevo.getKmbm().startsWith(km)
						&& fsejyevo.getPk_km().indexOf("_" + xmmcid) > 0) {
					list.add(fsejyevo);
				}
			} else {
				if (fsejyevo.getKmbm().equals(km)) {
					list.add(fsejyevo);
				}
			}
		}
		return list;
	}

}

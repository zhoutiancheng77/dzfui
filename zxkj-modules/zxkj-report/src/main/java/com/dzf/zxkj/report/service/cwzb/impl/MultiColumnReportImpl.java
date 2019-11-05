package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.ExMultiVO;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IFzKmmxReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.IMultiColumnReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 多栏账查询
 * @author zhangj
 *
 */
@Service("gl_rep_multiserv")
public class MultiColumnReportImpl implements IMultiColumnReport {

	@Autowired
	private IKMMXZReport gl_rep_kmmxjserv;
	
	@Autowired
	private IFzKmmxReport gl_rep_fzkmmxjrptserv;

	@Autowired
	private IZxkjPlatformService zxkjPlatformService;
	
	@Override
	public Object[] getMulColumns(KmReoprtQueryParamVO vo) throws DZFWarpException {
		
		Object[] objs = new Object[2];
		/** 默认赋值 */
		if(StringUtil.isEmpty(vo.getKms_first())){
			throw new BusinessException("查询科目信息为空!");
		}
		
		/** 开始日期不能晚于截至日期 */
		DZFDate begindate= vo.getBegindate1();
		DZFDate enddate = vo.getEnddate();
		if(begindate.after(enddate)){
			throw new BusinessException("开始日期不能晚于截至日期!");
		}
		KmMxZVO[] kmmxvos =  gl_rep_kmmxjserv.getKMMXZVOs(vo,null);
		if(!StringUtil.isEmpty(vo.getFzlb())){
			getExtColumnFromFz(vo,objs,kmmxvos);
		}else{
			getExtColumnFromKm(vo, objs, kmmxvos);
		}
		return objs;
	}


	/**
	 * 根据辅助项目查询
	 * @param vo
	 * @param objs
	 * @param kmmxvos
	 */
	private void getExtColumnFromFz(KmReoprtQueryParamVO vo, Object[] objs, KmMxZVO[] kmmxvos) {
		/** 所有的科目出来了，还需要查询对应的(下级科目) */
		Map<String, YntCpaccountVO> mapres =  zxkjPlatformService.queryMapByPk(vo.getPk_corp());
		vo.setKms_last(vo.getKms_first());
		YntCpaccountVO[] cpvos =  zxkjPlatformService.queryByPk(vo.getPk_corp());
		YntCpaccountVO  currentvo= null;
		for(YntCpaccountVO cpvo:cpvos){
			if(cpvo.getAccountcode().equals(vo.getKms_first())){
				currentvo = cpvo;
				break;
			}
		}
		vo.setXskm(DZFBoolean.TRUE);
		Map<String,List<FzKmmxVO>> fzvomap =  gl_rep_fzkmmxjrptserv.getAllFzKmmxVos(vo);
		List<String> columnlist = new ArrayList<String>();
		List<ExMultiVO> reslist = new ArrayList<ExMultiVO>();
		String firstkm = vo.getKms_first();
	
		ExMultiVO multi = null;
		String zy = null;
		HashMap<String, Object> map=null;
		/** 不存在辅助项目的情况 */
		if(StringUtil.isEmpty(vo.getFzxm())){
			for(KmMxZVO kmmxvo:kmmxvos){
				if(kmmxvo.getKmbm().equals(firstkm)){//只是显示本级
					multi = new ExMultiVO();
					map=multi.getHash();
					map.put("rq", kmmxvo.getRq());
					map.put("pzh",kmmxvo.getPzh() == null?"":kmmxvo.getPzh());
					map.put("zy", kmmxvo.getZy() == null ?"" :kmmxvo.getZy());
					map.put("jf", formaterMny(DZFDouble.getUFDouble( kmmxvo.getJf())));
					map.put("df", formaterMny(DZFDouble.getUFDouble(kmmxvo.getDf())));
					map.put("fx", kmmxvo.getFx());
					map.put("ye", formaterMny(DZFDouble.getUFDouble(kmmxvo.getYe())));
					map.put("pk_accsubj", kmmxvo.getPk_accsubj());
					map.put("pk_tzpz_h", kmmxvo.getPk_tzpz_h());
					zy = kmmxvo.getPk_tzpz_b();
					
					currentvo = mapres.get(kmmxvo.getPk_accsubj());
					for (Map.Entry<String,List<FzKmmxVO>> entry : fzvomap.entrySet()) { 
						String keyfz = entry.getKey();
						List<FzKmmxVO> listfz = entry.getValue();
						if(listfz!=null && listfz.size() > 0){
							for(FzKmmxVO fzvotemp:listfz){
								if((!StringUtil.isEmpty(fzvotemp.getPk_tzpz_b()) && !StringUtil.isEmpty(zy) 
										&& fzvotemp.getPk_tzpz_b().equals(zy) )
										){
									if(currentvo.getDirection() == 0){
										map.put(keyfz.split("_")[0], formaterMny(SafeCompute.sub(fzvotemp.getJf(), fzvotemp.getDf())));
									}else {
										map.put(keyfz.split("_")[0],formaterMny(SafeCompute.sub(fzvotemp.getDf(), fzvotemp.getJf())));
									}
									if(!columnlist.contains(keyfz)){
										columnlist.add(keyfz);
									}
								}else if(StringUtil.isEmpty(fzvotemp.getPk_tzpz_b()) &&
												"期初余额".equals(fzvotemp.getZy()) && "期初余额".equals(kmmxvo.getZy())
												&& fzvotemp.getRq().equals(kmmxvo.getRq())
										){
									if(fzvotemp.getYe()!=null && fzvotemp.getYe().doubleValue() != 0){
										map.put(keyfz.split("_")[0],formaterMny(fzvotemp.getYe()));
										if(!columnlist.contains(keyfz)){
											columnlist.add(keyfz);
										}
									}
								}
							}
						}
						
					}
					reslist.add(multi);//添加数据金额
				}
			}
		}else{
			for (Map.Entry<String,List<FzKmmxVO>> entry : fzvomap.entrySet()) { 
				String keyfz = entry.getKey();
				List<FzKmmxVO> listfz = entry.getValue();
				if(listfz!=null && listfz.size() > 0){
					for(FzKmmxVO fzvotemp:listfz){
						multi = new ExMultiVO();
						map=multi.getHash();
						map.put("rq", fzvotemp.getRq());
						map.put("pzh",fzvotemp.getPzh() == null?"":fzvotemp.getPzh());
						map.put("zy", fzvotemp.getZy() == null ?"" :fzvotemp.getZy());
						map.put("jf", formaterMny(DZFDouble.getUFDouble( fzvotemp.getJf())));
						map.put("df", formaterMny(DZFDouble.getUFDouble(fzvotemp.getDf())));
						map.put("fx", fzvotemp.getFx());
						map.put("ye", formaterMny(DZFDouble.getUFDouble(fzvotemp.getYe())));
						map.put("pk_accsubj", fzvotemp.getPk_accsubj());
						map.put("pk_tzpz_h", fzvotemp.getPk_tzpz_h());
						
						if(!StringUtil.isEmpty(fzvotemp.getPk_tzpz_h())){
							if(currentvo.getDirection() == 0 ){
								map.put(keyfz.split("_")[0], formaterMny(SafeCompute.sub(fzvotemp.getJf(), fzvotemp.getDf())));
							}else {
								map.put(keyfz.split("_")[0],formaterMny(SafeCompute.sub(fzvotemp.getDf(), fzvotemp.getJf())));
							}
						}
						
						if("期初余额".equals(fzvotemp.getZy())){
							map.put(keyfz.split("_")[0], formaterMny(fzvotemp.getYe()));
						}
						
						if(!columnlist.contains(keyfz)){
							columnlist.add(keyfz);
						}
						reslist.add(multi);//添加数据金额
					}
				}
			}
		}
		objs[0]= reslist.toArray(new ExMultiVO[0]);
		objs[1]= columnlist;
	}
	

	/**
	 * 根据科目查询
	 * @param vo
	 * @param objs
	 * @param kmmxvos
	 */
	private void getExtColumnFromKm(KmReoprtQueryParamVO vo, Object[] objs, KmMxZVO[] kmmxvos) {
		/** 所有的科目出来了，还需要查询对应的(下级科目) */
		Map<String,YntCpaccountVO> mapres =  zxkjPlatformService.queryMapByPk(vo.getPk_corp());
		List<String> columnlist = new ArrayList<String>();
		List<ExMultiVO> reslist = new ArrayList<ExMultiVO>();
		String firstkm = vo.getKms_first();
		DZFBoolean isxsxj = vo.getIsxsxj() == null ? DZFBoolean.FALSE:vo.getIsxsxj();//是否显示下级
		DZFBoolean isxsmj = vo.getIsxsmx() == null ? DZFBoolean.FALSE:vo.getIsxsmx();//是否显示明细
		YntCpaccountVO parenttempvo =null;
		Integer sx = null;
		Integer parentcj =null;
		ExMultiVO multi = null;
		String zy = null;
		YntCpaccountVO currtempvo =null;
		Integer cj =null;
		String zytemp = null;
		DZFDouble comvalue = null;
		DZFDouble tempvalue =null;
		HashMap<String, Object> map=null;
		for(KmMxZVO kmmxvo:kmmxvos){
			if(kmmxvo.getKmbm().equals(firstkm)){/** 只是显示本级*/
				parenttempvo = mapres.get(kmmxvo.getPk_accsubj());
				/** 查询当前科目的科目属性 */
				sx = parenttempvo.getAccountkind();
				parentcj = parenttempvo.getAccountlevel();
				multi = new ExMultiVO();
				map=multi.getHash();
				map.put("rq", kmmxvo.getRq().trim());
				map.put("pzh",kmmxvo.getPzh() == null?"":kmmxvo.getPzh());
				map.put("zy", kmmxvo.getZy() == null ?"" :kmmxvo.getZy().trim());
				map.put("jf", formaterMny(DZFDouble.getUFDouble( kmmxvo.getJf())));
				map.put("df", formaterMny(DZFDouble.getUFDouble(kmmxvo.getDf())));
				map.put("fx", kmmxvo.getFx());
				map.put("ye", formaterMny(DZFDouble.getUFDouble(kmmxvo.getYe())));
				map.put("pk_accsubj", kmmxvo.getPk_accsubj());
				map.put("pk_tzpz_h", kmmxvo.getPk_tzpz_h());
				zy = kmmxvo.getPk_tzpz_b();
				
				if(sx!=null){
					for(KmMxZVO kmmxvo2:kmmxvos){
						currtempvo = mapres.get(kmmxvo2.getPk_accsubj());
						cj =currtempvo.getAccountlevel();
						if(isxsxj.booleanValue()){/** 只是显示下级(层级比这小一位的) */
							if(kmmxvo2.getKmbm().startsWith(firstkm) && (cj.intValue()-1 == parentcj)){
								zytemp = kmmxvo2.getPk_tzpz_b();
								if((!StringUtil.isEmpty(zytemp)  && zytemp.equals(zy))
										|| (StringUtil.isEmpty(zytemp) &&
												(("本月合计".equals(kmmxvo2.getZy()) && "本月合计".equals(kmmxvo.getZy()))
														|| ("本年累计".equals(kmmxvo2.getZy())
																&& "本年累计".equals(kmmxvo.getZy())))
												&& kmmxvo.getRq().equals(kmmxvo2.getRq()))
										){
									tempvalue = map.get(kmmxvo2.getKmbm()) == null?DZFDouble.ZERO_DBL: new DZFDouble((String)map.get(kmmxvo2.getKmbm()));
									if(currtempvo.getDirection() ==0){
										comvalue =  SafeCompute.sub(kmmxvo2.getJf(), kmmxvo2.getDf());
										map.put(kmmxvo2.getKmbm(), formaterMny(SafeCompute.add(tempvalue, DZFDouble.getUFDouble(comvalue))));
									}else{
										comvalue =  SafeCompute.sub(kmmxvo2.getDf(), kmmxvo2.getJf());
										map.put(kmmxvo2.getKmbm(),formaterMny(SafeCompute.add(tempvalue,  DZFDouble.getUFDouble(comvalue))));
									}
									if(!columnlist.contains(kmmxvo2.getKmbm() +"_"+ currtempvo.getAccountname())){
										columnlist.add(kmmxvo2.getKmbm() +"_"+ currtempvo.getAccountname());
									}
								}else if(StringUtil.isEmpty(zytemp) &&
										"期初余额".equals(kmmxvo2.getZy()) && "期初余额".equals(kmmxvo.getZy() )
										&& kmmxvo.getRq().equals(kmmxvo2.getRq())) {
									if (kmmxvo2.getYe() != null && kmmxvo2.getYe().doubleValue() != 0) {
										map.put(kmmxvo2.getKmbm(), formaterMny(kmmxvo2.getYe()));
										if (!columnlist.contains(kmmxvo2.getKmbm() + "_" + currtempvo.getAccountname())) {
											columnlist.add(kmmxvo2.getKmbm() + "_" + currtempvo.getAccountname());
										}
									}
								}
							}
						}
						
						if(isxsmj.booleanValue()){/** 是否显示末级 */
							if(kmmxvo2.getKmbm().startsWith(firstkm) && currtempvo.getIsleaf()!=null && currtempvo.getIsleaf().booleanValue()){
								 zytemp = kmmxvo2.getPk_tzpz_b();
								if((!StringUtil.isEmpty(zytemp) && zytemp.equals(zy))
										|| (StringUtil.isEmpty(zytemp) &&
												(("本月合计".equals(kmmxvo2.getZy()) && "本月合计".equals(kmmxvo.getZy()))
														|| ("本年累计".equals(kmmxvo2.getZy())
																&& "本年累计".equals(kmmxvo.getZy())))
												&& kmmxvo.getRq().equals(kmmxvo2.getRq()))
										){
									if(currtempvo.getDirection() ==0){
									    comvalue =  SafeCompute.sub(kmmxvo2.getJf(), kmmxvo2.getDf());
										multi.getHash().put(kmmxvo2.getKmbm(), formaterMny(DZFDouble.getUFDouble(comvalue)));
									}else{
										comvalue =  SafeCompute.sub(kmmxvo2.getDf(), kmmxvo2.getJf());
										multi.getHash().put(kmmxvo2.getKmbm(), formaterMny(DZFDouble.getUFDouble(comvalue)));
									}
									if(!columnlist.contains(kmmxvo2.getKmbm() +"_"+ currtempvo.getAccountname())){
										columnlist.add(kmmxvo2.getKmbm() +"_"+ currtempvo.getAccountname());
									}
								}else if(StringUtil.isEmpty(zytemp) &&
										"期初余额".equals(kmmxvo2.getZy()) && "期初余额".equals(kmmxvo.getZy() )
										&& kmmxvo.getRq().equals(kmmxvo2.getRq())) {
									if (kmmxvo2.getYe() != null && kmmxvo2.getYe().doubleValue() != 0) {
										map.put(kmmxvo2.getKmbm(), formaterMny(kmmxvo2.getYe()));
										if (!columnlist.contains(kmmxvo2.getKmbm() + "_" + currtempvo.getAccountname())) {
											columnlist.add(kmmxvo2.getKmbm() + "_" + currtempvo.getAccountname());
										}
									}
								}
							}
						}
					}
				}
				reslist.add(multi);//添加数据金额
			}
		}
		objs[0]= reslist.toArray(new ExMultiVO[0]);
		objs[1]= columnlist;
	}

	
	private String formaterMny(DZFDouble value) {
		if (value.doubleValue() == 0) {
			return "";
		}
		DecimalFormat df = new DecimalFormat("#,###.00");
		// 设置舍入模式
		df.setRoundingMode(RoundingMode.FLOOR);
		return df.format(value.doubleValue());
	}
	
}

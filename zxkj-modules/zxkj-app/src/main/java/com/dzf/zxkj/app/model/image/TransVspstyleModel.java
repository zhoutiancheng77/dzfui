package com.dzf.zxkj.app.model.image;


import com.dzf.zxkj.common.constant.FieldConstant;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;

/**
 * 手机上传图片摘要转化票据类型，以及结算方式转化
 * 手机上传摘要、结算方式为真值，而非参照值，故而需转化
 * 后期如果上传信息按参照传值，该类可删除
 */
public class TransVspstyleModel {
	
	/**
	 * 摘要转化票据类型
	 * @param value
	 * @return
	 */
	public static String transVspstyle(String value,String chargedeptname){
		String vspstyle = "";
		if(value != null && value.length() != 0 ){
			if("商品收入".equals(value) || "服务收入".equals(value)
					|| "购买商品".equals(value) || "购买材料".equals(value)
					|| "其他收入".equals(value) || "购买其他".equals(value) 
					|| "购买资产".equals(value) ){
				if(StringUtil.isEmpty(chargedeptname) || "小规模纳税人".equals(chargedeptname)){
					vspstyle = FieldConstant.FPSTYLE_02;//普通发票
				}else{
					vspstyle = FieldConstant.FPSTYLE_01;//专用发票
				}
			}else if("差旅费".equals(value) || "办公费".equals(value)
					|| "招待费".equals(value) || "其他费用".equals(value)
					|| "薪资费用".equals(value) 
					|| "领料".equals(value) || "其他".equals(value)){
				vspstyle = FieldConstant.FPSTYLE_21;
			}else if("银行费用".equals(value)){
				vspstyle = FieldConstant.FPSTYLE_20;
			} else if("收入-商品收入".equals(value) || "收入-服务收入".equals(value)
					|| "收入-其他收入".equals(value) || "销售商品".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_01;
			}else if("购货-商品".equals(value) || "购货-材料".equals(value)
					|| "购货-其他商品".equals(value)  ){
				vspstyle = FieldConstant.FPSTYLE_02;
			}else if("购货-固定资产".equals(value)){
				vspstyle = FieldConstant.FPSTYLE_17;
			}else if("费用-工资及福利".equals(value)){
				vspstyle = FieldConstant.FPSTYLE_13;
			}else if("费用-办公费".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_09;
			}else if("费用-差旅费".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_08;
			}else if("费用-招待费".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_09;
			}else if("费用-银行费用".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_15;
			}else if("费用-其他费用".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_09;
			}else if("内部单据-领料单".equals(value) ){
				vspstyle = FieldConstant.FPSTYLE_16;
			}else if("内部单据-其他单据".equals(value) ){
				//无对应
			}
		}
		return vspstyle;
	}
	
	/**
	 * 摘要转换摘要
	 * @param value
	 * @param chargedeptname
	 * @return
	 */
	public static String transVsMemotyle(String value,Integer corptype,String chargedeptname){
		String vmemo = null;
		if("商品收入".equals(value)){
			vmemo  = "销售收入";
		}else if("劳务收入".equals(value)){
			vmemo  = "服务收入";
		}else if("差旅费".equals(value)){
			vmemo = "差旅报销单";
		}else if( "其他".equals(value)){
			vmemo = "费用报销单";
		}else if("招待费".equals(value) ){
			vmemo = "报招待费";
		}else if("办公费".equals(value)){
			vmemo = "报办公费";
		}else if("购买资产".equals(value)){
			if("一般纳税人".equals(chargedeptname)){
				vmemo = "资产购入（一般人）";
			}else{
				vmemo = "资产购入（小规模）";
			}
		}else if("银行费用".equals(value)){
			if(corptype == DzfUtil.SEVENSCHEMA.intValue()){
				vmemo = "银行手续费";
			}else if(corptype == DzfUtil.THIRTEENSCHEMA.intValue()){
				vmemo = "银行费用";
			}
		}else if("领料".equals(value)){
			vmemo = "领料单";
		}else if("购买其他".equals(value)){
			if("一般纳税人".equals(chargedeptname)){
				vmemo = "购买其他（一般人）";
			}else {
				vmemo = "购买其他（小规模）";
			}
		}else if("购买商品".equals(value)){
			if("一般纳税人".equals(chargedeptname)){
				vmemo = "购买商品（一般人）";
			}else{
				vmemo = "购买商品（小规模）";
			}
		}else if("购买材料".equals(value)){
			if("一般纳税人".equals(chargedeptname)){
				vmemo = "购买材料（一般人）";
			}else{
				vmemo = "购买材料（小规模）";
			}
			
		}else{
			vmemo = value;
		}
		return vmemo;
	}
	
	
	/**
	 * 手机上传结算方式转化
	 * @param value
	 * @return
	 */
	public static String transSzstyle(String memo, String value){
		String szstyle = "";
		if(value != null && value.length() != 0 ){
			if("现金-收款".equals(value) 
					|| ("销售商品".equals(memo) && "现金".equals(value) )
					|| ("服务收入".equals(memo) && "现金".equals(value) )
					|| ("其他收入".equals(memo) && "现金".equals(value) )
					|| ("商品收入".equals(memo) && "现金".equals(value) )
					){
				szstyle = FieldConstant.SZSTYLE_01;
			}else if("现金-付款".equals(value)
					|| ("差旅费".equals(memo) && "现金".equals(value) )
					|| ("办公费".equals(memo) && "现金".equals(value) )
					|| ("购买商品".equals(memo) && "现金".equals(value) )
					|| ("购买材料".equals(memo) && "现金".equals(value) )
					|| ("招待费".equals(memo) && "现金".equals(value) )
					|| ("其他费用".equals(memo) && "现金".equals(value) )
					|| ("购买资产".equals(memo) && "现金".equals(value) )
					|| ("购买其他".equals(memo) && "现金".equals(value) )
					|| ("薪资费用".equals(memo) && "现金".equals(value) )
					|| ("银行费用".equals(memo) && "现金".equals(value) )
					|| ("领料单".equals(memo) && "现金".equals(value) )
					|| ("其他单据".equals(memo) && "现金".equals(value) )
					|| ("其他".equals(memo) && "现金".equals(value))
					){
				szstyle = FieldConstant.SZSTYLE_02;
			}else if("银行-收款".equals(value)
					||  ("销售商品".equals(memo) && "银行".equals(value) )
					|| ("服务收入".equals(memo) && "银行".equals(value) )
					|| ("其他收入".equals(memo) && "银行".equals(value) )
					|| ("商品收入".equals(memo) && "银行".equals(value) )
					){
				szstyle = FieldConstant.SZSTYLE_03;
			}else if("银行-付款".equals(value)
					|| ("差旅费".equals(memo) && "银行".equals(value) )
					|| ("办公费".equals(memo) && "银行".equals(value) )
					|| ("购买商品".equals(memo) && "银行".equals(value) )
					|| ("购买材料".equals(memo) && "银行".equals(value) )
					|| ("招待费".equals(memo) && "银行".equals(value) )
					|| ("其他费用".equals(memo) && "银行".equals(value) )
					|| ("购买资产".equals(memo) && "银行".equals(value) )
					|| ("购买其他".equals(memo) && "银行".equals(value) )
					|| ("薪资费用".equals(memo) && "银行".equals(value) )
					|| ("银行费用".equals(memo) && "银行".equals(value) )
					|| ("领料单".equals(memo) && "银行".equals(value) )
					|| ("其他单据".equals(memo) && "银行".equals(value) )
					|| ("其他".equals(memo) && "银行".equals(value))
					){
				szstyle = FieldConstant.SZSTYLE_04;
			}else if("其他-收款".equals(value)
					|| ("销售商品".equals(memo) && "其他".equals(value) )
					|| ("服务收入".equals(memo) && "其他".equals(value) )
					|| ("其他收入".equals(memo) && "其他".equals(value) )
					|| ("商品收入".equals(memo) && "其他".equals(value) )
					){
				szstyle = FieldConstant.SZSTYLE_05;
			}else if("其他-付款".equals(value)
					|| ("差旅费".equals(memo) && "其他".equals(value) )
					|| ("办公费".equals(memo) && "其他".equals(value) )
					|| ("购买商品".equals(memo) && "其他".equals(value) )
					|| ("购买材料".equals(memo) && "其他".equals(value) )
					|| ("招待费".equals(memo) && "其他".equals(value) )
					|| ("其他费用".equals(memo) && "其他".equals(value) )
					|| ("购买资产".equals(memo) && "其他".equals(value) )
					|| ("购买其他".equals(memo) && "其他".equals(value) )
					|| ("薪资费用".equals(memo) && "其他".equals(value) )
					|| ("银行费用".equals(memo) && "其他".equals(value) )
					|| ("领料单".equals(memo) && "其他".equals(value) )
					|| ("其他单据".equals(memo) && "其他".equals(value) )
					|| ("其他".equals(memo) && "其他".equals(value))
					|| ("领料".equals(memo))
					){
				szstyle = FieldConstant.SZSTYLE_06;
			}
		}
		return szstyle;
	}

}

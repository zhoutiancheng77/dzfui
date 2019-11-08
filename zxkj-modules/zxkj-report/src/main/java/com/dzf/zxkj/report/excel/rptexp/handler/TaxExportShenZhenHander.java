package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.report.excel.rptexp.TaxExportUtil;
import org.dom4j.Element;

import java.util.Map;

public class TaxExportShenZhenHander extends TaxExportHander{

	public void putXjllXml_yb(Map<String, XjllbVO> xjllmap, XjllTaxVo[] xjlltaxvos, Element ybqycwbbxx, Integer areatype,
							  String corptype) {
		Element ybqyxjllbVO = ybqycwbbxx.addElement("ybqyxjllbVO");
		Element ybqyxjllbGrid = ybqyxjllbVO.addElement("ybqyxjllbGrid");
		Element child =null;
		String value = "";
		String[] contents = new String[]{"ewbhxh","hmc","bqje","sqje1"};
		for(XjllTaxVo taxvo:xjlltaxvos){
			//获取现金流量vo
			XjllbVO xjllvo = null;
			if(taxvo.getHc_ref()!=null){
				xjllvo = xjllmap.get(taxvo.getHc_ref()+"");
			}
			
			Element ybqyxjllbGridlb = ybqyxjllbGrid.addElement("ybqyxjllbGridlb");
			for(String key:contents){
				child = ybqyxjllbGridlb.addElement(key);
				if(key.equals("ewbhxh")){
					value = taxvo.getHc()+"";
				}else if(key.equals("hmc")){
					value = taxvo.getVname();
				}else if(key.equals("bqje")){//累计数
					value = xjllvo!=null? TaxExportUtil.getDzfDouble(xjllvo.getSqje()):"0";
				}else if(key.equals("sqje1")){//去年累计数
					value = xjllvo!=null? TaxExportUtil.getDzfDouble(xjllvo.getSqje_last()):"0";
				}
				if(!StringUtil.isEmpty(value)){
					child.setText(value);
				}
			}
		}
	}


	
	public void putZcfzXml_Yb(Map<String, ZcFzBVO>  zcfzmap, ZcfzTaxVo[] zcfztaxvos, Element root, Integer areatype, String corptype) {
		// 创建syxqyzcfzb
		Element syxqyzcfzb = root.addElement("ybqyzcfzbVO");
		// 创建syxqyzcfzbGrid
		Element syxqyzcfzbGrid = syxqyzcfzb.addElement("ybqyzcfzbzbGrid");

		String[] childs = new String[]{"ewbhxh","zcxmmc","qmyeZc","ncyeZc","qyxmmc","qmyeQy","ncyeQy"};
		// 取学生信息的Bean列表
		for(ZcfzTaxVo taxvo:zcfztaxvos){
			//获取资产负债VO数据
			Integer zchc_ref = taxvo.getZchc_ref();
			Integer fzhc_ref = taxvo.getFzhc_ref();
			ZcFzBVO bvo_zc = null;
			ZcFzBVO bvo_fz = null;
			if (zchc_ref != null) 
				bvo_zc = zcfzmap.get(zchc_ref+"");
			if (fzhc_ref != null) 
				bvo_fz = zcfzmap.get(fzhc_ref+"");
			
			//拼接tax数据
			Element ybqyzcfzbzbGridlb = syxqyzcfzbGrid.addElement("ybqyzcfzbzbGridlb");
			String value = "";
			Element ele = null;
			for(String key:childs){
				ele = ybqyzcfzbzbGridlb.addElement(key);
				if(key.equals("ewbhxh")){
					value = taxvo.getHc()+"";
				}else if(key.equals("zcxmmc")){
					value = taxvo.getZcname();
				} else if (key.equals("qmyeZc")) {// 资产期末
					value =bvo_zc!=null? TaxExportUtil.getDzfDouble(bvo_zc.getQmye1()):"0";
				}else if(key.equals("ncyeZc")){//资产年初
					value =bvo_zc!=null? TaxExportUtil.getDzfDouble(bvo_zc.getNcye1()):"0";
				}else if(key.equals("qyxmmc")){
					value = taxvo.getFzname();
				}else if(key.equals("qmyeQy")){
					value = bvo_fz == null ?"0":TaxExportUtil.getDzfDouble(bvo_fz.getQmye2());
				}else if(key.equals("ncyeQy")){
					value = bvo_fz == null?"0":TaxExportUtil.getDzfDouble(bvo_fz.getNcye2());
				}
				if(!StringUtil.isEmpty(value)){
					ele.setText(value);
				}
			}
		}
	}


	public void putZcfzXml_xqy(Map<String, ZcFzBVO> zcfzmap, ZcfzTaxVo[] zcfztaxvos, Element root,Integer areatype,String corptype) {
		// 创建syxqyzcfzb
		Element syxqyzcfzb = root.addElement("syxqyzcfzb");
		// 创建syxqyzcfzbGrid
		Element syxqyzcfzbGrid = syxqyzcfzb.addElement("syxqyzcfzbGrid");

		// 取学生信息的Bean列表
		String[] contents = new String[] { "ewbhxh", "zcxmmc", "qmyeZc", "ncyeZc", "qyxmmc", "qmyeQy", "ncyeQy" };
		
		for(ZcfzTaxVo taxvo:zcfztaxvos){
			//获取资产负债VO数据
			Integer zchc_ref = taxvo.getZchc_ref();
			Integer fzhc_ref = taxvo.getFzhc_ref();
			ZcFzBVO bvo_zc = null;
			ZcFzBVO bvo_fz = null;
			if (zchc_ref != null) 
				bvo_zc = zcfzmap.get(zchc_ref+"");
			if (fzhc_ref != null) 
				bvo_fz = zcfzmap.get(fzhc_ref+"");
			
			Element xqyzcfzbGridlb = syxqyzcfzbGrid.addElement("xqyzcfzbGridlb");
			Element child = null;
			String value = "";
			for(String key:contents){
				child = xqyzcfzbGridlb.addElement(key);
				if(key.equals("ewbhxh")){
					value = taxvo.getHc() + "";
				}else if(key.equals("zcxmmc")){
					value = taxvo.getZcname();
				}else if(key.equals("qmyeZc")){
					value = bvo_zc!=null? TaxExportUtil.getDzfDouble(bvo_zc.getQmye1()):"0";
				}else if(key.equals("ncyeZc")){
					value = bvo_zc !=null? TaxExportUtil.getDzfDouble(bvo_zc.getNcye1()):"0";
				}else if(key.equals("qyxmmc")){
					value = taxvo.getFzname();
				}else if(key.equals("qmyeQy")){
					value = bvo_fz!=null?TaxExportUtil.getDzfDouble(bvo_fz.getQmye2()):"0";
				}else if(key.equals("ncyeQy")){
					value = bvo_fz!=null?TaxExportUtil.getDzfDouble(bvo_fz.getNcye2()):"0";
				}
				if(!StringUtil.isEmpty(value)){
					child.setText(value);
				}
			}
		}
	}
	
	public void putLrbXml_Yb(Map<String, LrbVO> lrbmap, LrbTaxVo[] lrbtaxvos, Element root, Integer area_type, String corptype) {
		// 创建syxqyzcfzb
		Element ybqylrbVO = root.addElement("ybqylrbVO");
		// 创建syxqyzcfzbGrid
		Element ybqylrbGrid = ybqylrbVO.addElement("ybqylrbGrid");

		String[] childs = new String[] { "ewbhxh", "hmc", "bqje", "sqje1" };
		for (LrbTaxVo taxvo : lrbtaxvos) {
			// 获取利润表数据
			LrbVO lrbvo = null;
			if (taxvo.getHc_ref() != null) {
				lrbvo = lrbmap.get(taxvo.getHc_ref() + "");
			}

			// 拼接数据
			Element ybqylrbGridlb = ybqylrbGrid.addElement("ybqylrbGridlb");
			String value = "";
			Element elechild = null;
			for (String key : childs) {
				elechild = ybqylrbGridlb.addElement(key);
				if (key.equals("ewbhxh")) {
					value = taxvo.getHc() + "";
				} else if (key.equals("hmc")) {
					value = taxvo.getVname();
				} else if (key.equals("bqje")) {
					value = lrbvo != null ? TaxExportUtil.getDzfDouble(lrbvo.getBnljje()) : "0";
				} else if (key.equals("sqje1")) {
					value = lrbvo != null ? TaxExportUtil.getDzfDouble(lrbvo.getLastyear_bnljje()) : "0";
				}
				if(!StringUtil.isEmpty(value)){
					elechild.setText(value);
				}
			}
		}
	}

	public void putLrbXml_xqy( Map<String, LrbVO> lrbmap, LrbTaxVo[] lrbtaxvos, Element root,Integer area_type,String corptype) {
		// 创建syxqyzcfzb
		Element syxqylrb = root.addElement("syxqylrb");
		// 创建syxqyzcfzbGrid
		Element syxqylrbGrid = syxqylrb.addElement("syxqylrbGrid");
		// 取学生信息的Bean列表
		String[] contents_lrb = new String[] { "ewbhxh", "hmc", "bnljje", "byje" };
		
		for(LrbTaxVo taxvo:lrbtaxvos){
			// 获取利润表数据
			LrbVO lrbvo = null;
			if (taxvo.getHc_ref() != null) {
				lrbvo = lrbmap.get(taxvo.getHc_ref() + "");
			}
			
			Element syxqylrbGridlb = syxqylrbGrid.addElement("syxqylrbGridlb");
			Element child = null;
			String value = "";
			for (String key : contents_lrb) {
				child = syxqylrbGridlb.addElement(key);
				if (key.equals("ewbhxh")) {
					value = taxvo.getHc() + "";
				} else if (key.equals("hmc")) {
					value = taxvo.getVname();
				} else if (key.equals("bnljje")) {
					value = lrbvo != null ? TaxExportUtil.getDzfDouble(lrbvo.getBnljje()) : "0";
				} else if (key.equals("byje")) {
					value = lrbvo != null ? TaxExportUtil.getDzfDouble(lrbvo.getByje()) : "0";
				}
				if(!StringUtil.isEmpty(value)){
					child.setText(value);
				}
			}
		}
	}

	@Override
	public void putXjllXml_xqy(Map<String, XjllbVO> xjllmap, XjllTaxVo[] xjlltaxvos, Element ybqycwbbxx, Integer areatype, String corptype) {

	}
}

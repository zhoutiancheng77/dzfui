package com.dzf.zxkj.report.excel.rptexp;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import java.util.HashMap;
import java.util.Map;


/**
 * 河北的TAX文件导出
 * @author zhangj
 *
 */
@Deprecated
public class TaxUtilToHeBei {
	/**
	 * 用DOM写XML文档，把学生信息以XML文档的形式存储
	 * 
	 *            学生信息
	 * @throws Exception
	 */
	public static Document writeZcfzXMLFile(ZcFzBVO[] zcfzbvos, LrbVO[] lrbvo, XjllbVO[] xjllbvo, CorpVO corpVO, Integer areatype) throws Exception {
		//资产负债转换成map
		Map<String, ZcFzBVO> zcfzmap = converZcfzmap(zcfzbvos);
		//利润表转换成map
		Map<String, LrbVO> lrbmap = converLrbMap(lrbvo);
		//现金流量转换map
		Map<String, XjllbVO> xjllmap = converXjllMap(xjllbvo);
		
		// 新建一个空文档
		Document doc = null;
		doc = DocumentHelper.createDocument();
		// 下面是建立XML文档内容的过程.
		Element root = doc.addElement("taxML","http://www.chinatax.gov.cn/dataspec/");
		
		if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {//小企业
			root.addAttribute("xsi:type", "yhljrqycwbbzywbw");
			root.addAttribute("xmlbh", "String");
			root.addAttribute("bbh", "String");
			root.addAttribute("xmlmc", "String");
			root.addAttribute("xsi:schemaLocation", "http://www.chinatax.gov.cn/dataspec/TaxMLbw_yhljrqycwbb_V1.0.xsd");
			root.addAttribute("xmlns", "http://www.chinatax.gov.cn/dataspec/");
			root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

			//资产负债信息
			putZcfzXml_xqy(zcfzmap,root,areatype,corpVO.getCorptype());
			
			//利润表信息(本月，本年)
			putLrbXml_xqy(lrbmap,root,areatype,corpVO.getCorptype());
			
			//现金流量表
			putXjllXml_xqy(xjllmap,root,areatype,corpVO.getCorptype());
			
		} else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {//一般企业
			root.addAttribute("xsi:type", "ybqykjzzcwbywbw");
			root.addAttribute("xmlbh", "String");
			root.addAttribute("bbh", "String");
			root.addAttribute("xmlmc", "String");
			root.addAttribute("xsi:schemaLocation", "http://www.chinatax.gov.cn/dataspec/TaxMLbw_yhljrqycwbb_V1.0.xsd");
			root.addAttribute("xmlns", "http://www.chinatax.gov.cn/dataspec/");
			root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

			//资产负债信息
			putZcfzXml_Yb(zcfzmap, root,areatype,corpVO.getCorptype());
			//利润表信息(本月，去年同期)
			putLrbXml_Yb(lrbmap, root,areatype,corpVO.getCorptype());
			//现金流量()
			putXjllXml_Yb(xjllmap,root,areatype,corpVO.getCorptype());
		}else {
			throw new BusinessException("不支持当前科目方案结转");
		}
		return doc;
	}

	private static void putXjllXml_Yb(Map<String, XjllbVO> xjllmap, Element root, Integer areatype, String corptype) {
		//查询现金流量数据
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(areatype);
		sp.addParam(corptype);
		XjllTaxVo[] xjlltaxvos = (XjllTaxVo[]) sbo.queryByCondition(XjllTaxVo.class,
				"nvl(dr,0)=0 and area_type = ? and corptype = ? order by ordernum", sp);
		if(xjlltaxvos == null || xjlltaxvos.length == 0){
			return;
		}
		
		Element ybqyxjllbVO = root.addElement("ybqyxjllbVO");
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
					value = xjllvo!=null?getDzfDouble(xjllvo.getSqje()):"0";
				}else if(key.equals("sqje1")){//去年累计数
					value = xjllvo!=null? getDzfDouble(xjllvo.getSqje_last()):"0";
				}
				if(!StringUtil.isEmpty(value)){
					child.setText(value);
				}
			}
		}

	}

	private static Map<String, XjllbVO> converXjllMap(XjllbVO[] xjllbvo) {
		Map<String, XjllbVO> xjllmap = new HashMap<String, XjllbVO>();
		if(xjllbvo!=null && xjllbvo.length>0){
			for(XjllbVO vo:xjllbvo){
				if(!StringUtil.isEmpty(vo.getHc())){
					xjllmap.put(vo.getHc(), vo);
				}
			}
		}
		return xjllmap;
	}

	private static void putXjllXml_xqy(Map<String, XjllbVO> xjllmap, Element root, Integer areatype, String corptype) {
		//查询现金流量数据
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(areatype);
		sp.addParam(corptype);
		XjllTaxVo[] xjlltaxvos = (XjllTaxVo[]) sbo.queryByCondition(XjllTaxVo.class,
				"nvl(dr,0)=0 and area_type = ? and corptype = ? order by ordernum", sp);
		if(xjlltaxvos == null || xjlltaxvos.length == 0){
			return;
		}
		
		// 创建syxqyzcfzb
		Element syxqyxjllb = root.addElement("syxqyxjllb");
		// 创建syxqyzcfzbGrid
		Element xqyxjllbGrid = syxqyxjllb.addElement("xqyxjllbGrid");
		
		String[] childvalues = new String[]{"ewbhxh","hmc","bnljje","byje"};
		
		for (XjllTaxVo taxvo : xjlltaxvos) {
			//获取现金流量vo
			XjllbVO xjllvo = null;
			if(taxvo.getHc_ref()!=null){
				xjllvo = xjllmap.get(taxvo.getHc_ref()+"");
			}
			
			//拼接xml
			Element xqyxjllbGridlb = xqyxjllbGrid.addElement("xqyxjllbGridlb");
			Element child = null;
			String value = "";
			for(String key:childvalues){
				 child = xqyxjllbGridlb.addElement(key);
				 if(key.equals("ewbhxh")){
					 value = taxvo.getHc()+"";
				 }else if(key.equals("hmc")){
					 value = taxvo.getVname();
				 }else if(key.equals("bnljje")){
					 value = xjllvo!=null?getDzfDouble(xjllvo.getSqje()):"0";
				 }else if(key.equals("byje")){
					 value = xjllvo!=null?getDzfDouble(xjllvo.getBqje()):"0";
				 }
				 
				 if(!StringUtil.isEmpty(value)){
					 child.setText(value);
				 }
			}
		}
	}

	private static Map<String, LrbVO> converLrbMap(LrbVO[] lrbvo) {
		Map<String,  LrbVO> lrbmap = new HashMap<String,LrbVO>();
		if(lrbvo!=null && lrbvo.length>0){
			for(LrbVO vo:lrbvo){
				if(!StringUtil.isEmpty(vo.getHs())){
					lrbmap.put(vo.getHs(), vo);
				}
			}
		}
		return lrbmap;
	}

	private static Map<String, ZcFzBVO> converZcfzmap(ZcFzBVO[] zcfzbvos) {
		Map<String, ZcFzBVO> zcfzmap = new HashMap<String, ZcFzBVO>();
		
		if(zcfzbvos!=null && zcfzbvos.length>0){
			for(ZcFzBVO bvo:zcfzbvos){
				if(!StringUtil.isEmpty(bvo.getHc1())){
					zcfzmap.put(bvo.getHc1(), bvo);
				}
				if(!StringUtil.isEmpty(bvo.getHc2())){
					zcfzmap.put(bvo.getHc2(), bvo);
				}
			}
		}
		return zcfzmap;
	}

	public static String getDzfDouble(DZFDouble value){
		if(value == null){
			return "0";
		}else{
			return value.setScale(2, DZFDouble.ROUND_HALF_UP).toString();
		}
	}
	
	private static void putZcfzXml_Yb(Map<String, ZcFzBVO>  zcfzmap, Element root,Integer areatype,String corptype) {
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(areatype);
		sp.addParam(corptype);
		ZcfzTaxVo[] zcfztaxvos = (ZcfzTaxVo[]) sbo.queryByCondition(ZcfzTaxVo.class, "nvl(dr,0)=0 and area_type = ? and corptype =?   order by ordernum", sp);
		if(zcfztaxvos == null || zcfztaxvos.length ==0){
			return ;
		}
		
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
					value =bvo_zc!=null? getDzfDouble(bvo_zc.getQmye1()):"0";
				}else if(key.equals("ncyeZc")){//资产年初
					value =bvo_zc!=null? getDzfDouble(bvo_zc.getNcye1()):"0";
				}else if(key.equals("qyxmmc")){
					value = taxvo.getFzname();
				}else if(key.equals("qmyeQy")){
					value = bvo_fz == null ?"0":getDzfDouble(bvo_fz.getQmye2());
				}else if(key.equals("ncyeQy")){
					value = bvo_fz == null?"0":getDzfDouble(bvo_fz.getNcye2());
				}
				if(!StringUtil.isEmpty(value)){
					ele.setText(value);
				}
			}
		}
	}


	private static void putZcfzXml_xqy(Map<String, ZcFzBVO> zcfzmap, Element root,Integer areatype,String corptype) {
		//获取对照数据(资产负债表)
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(areatype);
		sp.addParam(corptype);
		ZcfzTaxVo[] zcfztaxvos = (ZcfzTaxVo[]) sbo.queryByCondition(ZcfzTaxVo.class, "nvl(dr,0)=0 and area_type = ? and corptype =?   order by ordernum", sp);
		if(zcfztaxvos == null || zcfztaxvos.length ==0){
			return ;
		}
		
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
					value = bvo_zc!=null? getDzfDouble(bvo_zc.getQmye1()):"0";
				}else if(key.equals("ncyeZc")){
					value = bvo_zc !=null? getDzfDouble(bvo_zc.getNcye1()):"0";
				}else if(key.equals("qyxmmc")){
					value = taxvo.getFzname();
				}else if(key.equals("qmyeQy")){
					value = bvo_fz!=null?getDzfDouble(bvo_fz.getQmye2()):"0";
				}else if(key.equals("ncyeQy")){
					value = bvo_fz!=null?getDzfDouble(bvo_fz.getNcye2()):"0";
				}
				if(!StringUtil.isEmpty(value)){
					child.setText(value);
				}
			}
		}
	}
	
	private static void putLrbXml_Yb(Map<String, LrbVO> lrbmap, Element root, Integer area_type,String corptype) {
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(area_type);
		sp.addParam(corptype);
		LrbTaxVo[] lrbtaxvos = (LrbTaxVo[]) sbo.queryByCondition(LrbTaxVo.class,
				"nvl(dr,0)=0 and area_type = ? and corptype = ? order by ordernum", sp);

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
					value = lrbvo != null ? getDzfDouble(lrbvo.getBnljje()) : "0";
				} else if (key.equals("sqje1")) {
					value = lrbvo != null ? getDzfDouble(lrbvo.getLastyear_bnljje()) : "0";
				}
				if(!StringUtil.isEmpty(value)){
					elechild.setText(value);
				}
			}
		}
	}

	private static void putLrbXml_xqy( Map<String, LrbVO> lrbmap, Element root,Integer area_type,String corptype) {
		SingleObjectBO sbo = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		SQLParameter sp = new SQLParameter();
		sp.addParam(area_type);
		sp.addParam(corptype);
		LrbTaxVo[] lrbtaxvos = (LrbTaxVo[]) sbo.queryByCondition(LrbTaxVo.class,
				"nvl(dr,0)=0 and area_type = ? and corptype = ? order by ordernum", sp);
		if(lrbtaxvos == null || lrbtaxvos.length == 0){
			return;
		}
		
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
					value = lrbvo != null ? getDzfDouble(lrbvo.getBnljje()) : "0";
				} else if (key.equals("byje")) {
					value = lrbvo != null ? getDzfDouble(lrbvo.getByje()) : "0";
				}
				if(!StringUtil.isEmpty(value)){
					child.setText(value);
				}
			}
		}
	}
}

package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.enums.ExportTemplateEnum;
import com.dzf.zxkj.platform.model.report.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.util.Map;

@Slf4j
public abstract class TaxExportHander {

    protected String trim(String str){
        if(StringUtil.isEmpty(str)){
            return "";
        }
        str = str.replaceAll("　| |：|\\(|\\)|:|（|）|","").trim();
        if(str.indexOf("、") != -1){
            str = str.substring(str.indexOf("、")+1, str.length());
        }
        return str;
    }

    public Document writeZcfzXMLFile(ZcFzBVO[] zcfzbvos, LrbVO[] lrbvo, XjllbVO[] xjllbvo, LrbTaxVo[] lrbtaxvos, ZcfzTaxVo[] zcfztaxvos, XjllTaxVo[] xjlltaxvos, CorpVO corpVO, Integer areatype) {
        //资产负债转换成map
        Map<String, ZcFzBVO> zcfzmap = TaxExportUtil.converZcfzmap(zcfzbvos);
        //利润表转换成map
        Map<String, LrbVO> lrbmap = TaxExportUtil.converLrbMap(lrbvo);
        //现金流量转换map
        Map<String, XjllbVO> xjllmap = TaxExportUtil.converXjllMap(xjllbvo);

        Document doc = null;


        if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {//小企业
            if(areatype == 0){
                // 新建一个空文档
                doc = DocumentHelper.createDocument();
                // 下面是建立XML文档内容的过程.
                Element root = doc.addElement("taxML","http://www.chinatax.gov.cn/dataspec/");
                root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                root.addAttribute("xsi:type", "xqyRequest");
                root.addAttribute("cnName", "String");
                root.addAttribute("name", "xqyRequest");
                root.addAttribute("version", "1.0");

                // 创建基础数据
                Element xqycwbbxx = root.addElement("xqycwbbxx");
                xqycwbbxx.addAttribute("cnName", "String");
                xqycwbbxx.addAttribute("name", "String");
                xqycwbbxx.addAttribute("version", "1.0");

                // 创建bblx
                Element bblx = xqycwbbxx.addElement("bblx");

                bblx.setText("01");
                //资产负债信息
                putZcfzXml_xqy(zcfzmap, zcfztaxvos, xqycwbbxx,areatype,corpVO.getCorptype());

                //利润表信息(本月，本年)
                putLrbXml_xqy(lrbmap, lrbtaxvos, xqycwbbxx,areatype,corpVO.getCorptype());

                //现金流量表
                putXjllXml_xqy(xjllmap, xjlltaxvos, xqycwbbxx,areatype,corpVO.getCorptype());
            }else{
                try {
                    String templatePath = "report/江苏/小企业会计准则财务报表.xml";
                    Resource resource = ResourceUtil.get(ExportTemplateEnum.JIANGSU, ResourceUtil.ResourceEnum.KJ2013ALL);
                    File f = resource.getFile();
                    SAXReader reader = new SAXReader();
                    doc = reader.read(f);
                } catch (Exception e) {
//                    e.printStackTrace();
                    log.error("税局模式tax导出异常！",e);
                }
                Element root = doc.getRootElement();
                //资产负债信息
                putZcfzXml_xqy(zcfzmap, zcfztaxvos, root,areatype,corpVO.getCorptype());

                //利润表信息(本月，本年)
                putLrbXml_xqy(lrbmap, lrbtaxvos, root,areatype,corpVO.getCorptype());

                //现金流量表
                putXjllXml_xqy(xjllmap, xjlltaxvos, root,areatype,corpVO.getCorptype());
            }


        } else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {//一般企业
            if(areatype == 0){
                // 新建一个空文档
                doc = DocumentHelper.createDocument();
                // 下面是建立XML文档内容的过程.
                Element root = doc.addElement("taxML","http://www.chinatax.gov.cn/dataspec/");
                root.addAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
                root.addAttribute("xsi:type", "ybqyRequest");
                root.addAttribute("cnName", "String");
                root.addAttribute("name", "xqyRequest");
                root.addAttribute("version", "1.0");

                // 创建基础数据
                Element ybqycwbbxx = root.addElement("ybqycwbbxx");
                ybqycwbbxx.addAttribute("cnName", "String");
                ybqycwbbxx.addAttribute("name", "String");
                ybqycwbbxx.addAttribute("version", "1.0");
                // 创建bblx
                Element bblx = ybqycwbbxx.addElement("bblx");

                bblx.setText("02");
                //资产负债信息
                putZcfzXml_Yb(zcfzmap, zcfztaxvos, ybqycwbbxx,areatype,corpVO.getCorptype());
                //利润表信息(本月，去年同期)
                putLrbXml_Yb(lrbmap, lrbtaxvos, ybqycwbbxx,areatype,corpVO.getCorptype());
                //现金流量表
                putXjllXml_yb(xjllmap, xjlltaxvos, ybqycwbbxx,areatype,corpVO.getCorptype());
            }else{
                try {
                    String templatePath = "report/江苏/企业会计准则财务报表.xml";
                    Resource resource = ResourceUtil.get(ExportTemplateEnum.JIANGSU, ResourceUtil.ResourceEnum.KJ2007ALL);
                    File f = resource.getFile();
                    SAXReader reader = new SAXReader();
                    doc = reader.read(f);
                } catch (Exception e) {
//                    e.printStackTrace();
                    log.error("税局模式tax导出异常！",e);
                }
                Element root = doc.getRootElement();

                //资产负债信息
                putZcfzXml_Yb(zcfzmap, zcfztaxvos, root,areatype,corpVO.getCorptype());
                //利润表信息(本月，去年同期)
                putLrbXml_Yb(lrbmap, lrbtaxvos, root,areatype,corpVO.getCorptype());
                //现金流量表
                putXjllXml_yb(xjllmap, xjlltaxvos, root,areatype,corpVO.getCorptype());
            }

        }else {
            throw new BusinessException("不支持当前科目方案结转");
        }


        return doc;
    }

    public abstract void putXjllXml_yb(Map<String, XjllbVO> xjllmap, XjllTaxVo[] xjlltaxvos, Element ybqycwbbxx, Integer areatype, String corptype);

    public abstract void putZcfzXml_Yb(Map<String, ZcFzBVO>  zcfzmap,ZcfzTaxVo[] zcfztaxvos, Element root,Integer areatype,String corptype);

    public abstract void putZcfzXml_xqy(Map<String, ZcFzBVO> zcfzmap, ZcfzTaxVo[] zcfztaxvos, Element root,Integer areatype,String corptype);

    public abstract void putLrbXml_Yb(Map<String, LrbVO> lrbmap, LrbTaxVo[] lrbtaxvos, Element root, Integer area_type,String corptype);

    public abstract void putLrbXml_xqy( Map<String, LrbVO> lrbmap, LrbTaxVo[] lrbtaxvos, Element root,Integer area_type,String corptype);

    public abstract void putXjllXml_xqy(Map<String, XjllbVO> xjllmap, XjllTaxVo[] xjlltaxvos, Element ybqycwbbxx, Integer areatype, String corptype);
}

package com.dzf.zxkj.report.excel.rptexp.handler;

import com.dzf.model.gl.gl_cwreport.*;
import com.dzf.service.gl.gl_cwreport.rptexp.TaxExportUtil;
import org.dom4j.Element;

import java.util.List;
import java.util.Map;

public class TaxExportJiangSuHander extends TaxExportHander{

    @Override
    public void putXjllXml_yb(Map<String, XjllbVO> xjllmap, XjllTaxVo[] xjlltaxvos, Element root, Integer areatype, String corptype) {
        Map<String, String> xjlltaxMap = TaxExportUtil.converXjllTax(xjlltaxvos);
        if(xjlltaxMap == null || xjlltaxMap.size() == 0 || xjllmap == null || xjllmap.size() == 0){
            return;
        }

        Element ybqyxjllbVO = root.element("ybqyxjllbVO");
        Element ybqyxjllbGrid = ybqyxjllbVO.element("ybqyxjllbGrid");
        List<Element> ybqyxjllbGridlbList =  ybqyxjllbGrid.elements("ybqyxjllbGridlb");

        for(Element ybqyxjllbGridlb: ybqyxjllbGridlbList){
            Element xmElement = ybqyxjllbGridlb.element("hmc");
            String xm = trim(xmElement.getTextTrim());
            if(xjlltaxMap.containsKey(xm) && xjlltaxMap.get(xm) != null){
                String hc = xjlltaxMap.get(xm);
                if(xjllmap.containsKey(hc) && xjllmap.get(hc) != null){
                    XjllbVO xjllbVO = xjllmap.get(hc);
                    if(xjllbVO.getSqje() != null){
                        ybqyxjllbGridlb.element("bqje").setText(xjllbVO.getSqje().toString());
                    }
                    if(xjllbVO.getSqje_last() != null){
                        ybqyxjllbGridlb.element("sqje1").setText(xjllbVO.getSqje_last().toString());
                    }
                }
            }
        }
    }

    @Override
    public void putZcfzXml_Yb(Map<String, ZcFzBVO> zcfzmap, ZcfzTaxVo[] zcfztaxvos, Element root, Integer areatype, String corptype) {

        Map<String, String> zcfztaxMap = TaxExportUtil.converZcfzTax(zcfztaxvos);

        if(zcfztaxMap == null || zcfztaxMap.size() == 0 || zcfzmap == null || zcfzmap.size() == 0){
            return;
        }

        Element ybqyzcfzbVO = root.element("ybqyzcfzbVO");
        Element ybqyzcfzbzbGrid = ybqyzcfzbVO.element("ybqyzcfzbzbGrid");
        List<Element> ybqyzcfzbzbGridlbList =  ybqyzcfzbzbGrid.elements("ybqyzcfzbzbGridlb");
        for(Element ybqyzcfzbzbGridlb : ybqyzcfzbzbGridlbList){
            Element zcxmmcElement = ybqyzcfzbzbGridlb.element("zcxmmc");
            String zcxmmc = trim(zcxmmcElement.getTextTrim());
            if(zcfztaxMap.containsKey(zcxmmc) && zcfztaxMap.get(zcxmmc) != null){
                String hc = zcfztaxMap.get(zcxmmc);
                if(zcfzmap.containsKey(hc) && zcfzmap.get(hc) != null){
                    ZcFzBVO zcFzBVO = zcfzmap.get(hc);
                    if(zcFzBVO.getQmye1() != null){
                        ybqyzcfzbzbGridlb.element("qmyeZc").setText(zcFzBVO.getQmye1().toString());
                    }
                    if(zcFzBVO.getNcye1() != null){
                        ybqyzcfzbzbGridlb.element("ncyeZc").setText(zcFzBVO.getNcye1().toString());
                    }
                }
            }
            Element qyxmmcElement = ybqyzcfzbzbGridlb.element("qyxmmc");
            String qyxmmc = trim(qyxmmcElement.getTextTrim());
            if(zcfztaxMap.containsKey(qyxmmc) && zcfztaxMap.get(qyxmmc) != null){
                String hc = zcfztaxMap.get(qyxmmc);
                if(zcfzmap.containsKey(hc) && zcfzmap.get(hc) != null){
                    ZcFzBVO zcFzBVO = zcfzmap.get(hc);
                    if(zcFzBVO.getQmye2() != null){
                        ybqyzcfzbzbGridlb.element("qmyeQy").setText(zcFzBVO.getQmye2().toString());
                    }
                    if(zcFzBVO.getNcye2() != null){
                        ybqyzcfzbzbGridlb.element("ncyeQy").setText(zcFzBVO.getNcye2().toString());
                    }
                }
            }
        }
    }

    @Override
    public void putZcfzXml_xqy(Map<String, ZcFzBVO> zcfzmap, ZcfzTaxVo[] zcfztaxvos, Element root, Integer areatype, String corptype) {
        Map<String, String> zcfztaxMap = TaxExportUtil.converZcfzTax(zcfztaxvos);

        if(zcfztaxMap == null || zcfztaxMap.size() == 0 || zcfzmap == null || zcfzmap.size() == 0){
            return;
        }

        Element syxqyzcfzb = root.element("syxqyzcfzb");
        Element syxqyzcfzbGrid = syxqyzcfzb.element("syxqyzcfzbGrid");
        List<Element> xqyzcfzbGridlbList =  syxqyzcfzbGrid.elements("xqyzcfzbGridlb");


        for(Element xqyzcfzbGridlb : xqyzcfzbGridlbList){
            Element zcxmmcElement = xqyzcfzbGridlb.element("zcxmmc");
            String zcxmmc = trim(zcxmmcElement.getTextTrim());
            if(zcfztaxMap.containsKey(zcxmmc) && zcfztaxMap.get(zcxmmc) != null){
                String hc = zcfztaxMap.get(zcxmmc);
                if(zcfzmap.containsKey(hc) && zcfzmap.get(hc) != null){
                    ZcFzBVO zcFzBVO = zcfzmap.get(hc);
                    if(zcFzBVO.getQmye1() != null){
                        xqyzcfzbGridlb.element("qmyeZc").setText(zcFzBVO.getQmye1().toString());
                    }
                    if(zcFzBVO.getNcye1() != null){
                        xqyzcfzbGridlb.element("ncyeZc").setText(zcFzBVO.getNcye1().toString());
                    }
                }
            }
            Element qyxmmcElement = xqyzcfzbGridlb.element("qyxmmc");
            String qyxmmc = trim(qyxmmcElement.getTextTrim());
            if(zcfztaxMap.containsKey(qyxmmc) && zcfztaxMap.get(qyxmmc) != null){
                String hc = zcfztaxMap.get(qyxmmc);
                if(zcfzmap.containsKey(hc) && zcfzmap.get(hc) != null){
                    ZcFzBVO zcFzBVO = zcfzmap.get(hc);
                    if(zcFzBVO.getQmye2() != null){
                        xqyzcfzbGridlb.element("qmyeQy").setText(zcFzBVO.getQmye2().toString());
                    }
                    if(zcFzBVO.getNcye2() != null){
                        xqyzcfzbGridlb.element("ncyeQy").setText(zcFzBVO.getNcye2().toString());
                    }
                }
            }
        }
    }

    @Override
    public void putLrbXml_Yb(Map<String, LrbVO> lrbmap, LrbTaxVo[] lrbtaxvos, Element root, Integer area_type, String corptype) {
        Map<String, String> lebtaxMap = TaxExportUtil.converLrbTax(lrbtaxvos);
        if(lebtaxMap == null || lebtaxMap.size() == 0 || lrbmap == null || lrbmap.size() == 0){
            return;
        }

        Element ybqylrbVO = root.element("ybqylrbVO");
        Element ybqylrbGrid = ybqylrbVO.element("ybqylrbGrid");
        List<Element> ybqylrbGridlbList =  ybqylrbGrid.elements("ybqylrbGridlb");

        for(Element ybqylrbGridlb: ybqylrbGridlbList){
            Element xmElement = ybqylrbGridlb.element("hmc");
            String xm = trim(xmElement.getTextTrim());
            if(lebtaxMap.containsKey(xm) && lebtaxMap.get(xm) != null){
                String hc = lebtaxMap.get(xm);
                if(lrbmap.containsKey(hc) && lrbmap.get(hc) != null){
                    LrbVO lrbVO = lrbmap.get(hc);
                    if(lrbVO.getBnljje() != null){
                        ybqylrbGridlb.element("bqje").setText(lrbVO.getBnljje().toString());
                    }
                    if(lrbVO.getLastyear_bnljje() != null){
                        ybqylrbGridlb.element("sqje1").setText(lrbVO.getLastyear_bnljje().toString());
                    }
                }
            }
        }

    }

    @Override
    public void putLrbXml_xqy(Map<String, LrbVO> lrbmap, LrbTaxVo[] lrbtaxvos, Element root, Integer area_type, String corptype) {
        Map<String, String> lebtaxMap = TaxExportUtil.converLrbTax(lrbtaxvos);
        if(lebtaxMap == null || lebtaxMap.size() == 0 || lrbmap == null || lrbmap.size() == 0){
            return;
        }

        Element syxqylrb = root.element("syxqylrb");
        Element syxqylrbGrid = syxqylrb.element("syxqylrbGrid");
        List<Element> syxqylrbGridlbList =  syxqylrbGrid.elements("syxqylrbGridlb");

        for(Element syxqylrbGridlb: syxqylrbGridlbList){
            Element xmElement = syxqylrbGridlb.element("hmc");
            String xm = trim(xmElement.getTextTrim());
            if(lebtaxMap.containsKey(xm) && lebtaxMap.get(xm) != null){
                String hc = lebtaxMap.get(xm);
                if(lrbmap.containsKey(hc) && lrbmap.get(hc) != null){
                    LrbVO lrbVO = lrbmap.get(hc);
                    if(lrbVO.getBnljje() != null){
                        syxqylrbGridlb.element("bnljje").setText(lrbVO.getBnljje().toString());
                    }
                    if(lrbVO.getByje() != null){
                        syxqylrbGridlb.element("byje").setText(lrbVO.getByje().toString());
                    }
                }
            }
        }
    }

    @Override
    public void putXjllXml_xqy(Map<String, XjllbVO> xjllmap, XjllTaxVo[] xjlltaxvos, Element root, Integer areatype, String corptype) {
        Map<String, String> xjlltaxMap = TaxExportUtil.converXjllTax(xjlltaxvos);
        if(xjlltaxMap == null || xjlltaxMap.size() == 0 || xjllmap == null || xjllmap.size() == 0){
            return;
        }

        Element syxqyxjllb = root.element("syxqyxjllb");

        Element xqyxjllbGrid = syxqyxjllb.element("xqyxjllbGrid");
        List<Element> xqyxjllbGridlbList =  xqyxjllbGrid.elements("xqyxjllbGridlb");

        for(Element xqyxjllbGridlb: xqyxjllbGridlbList){
            Element xmElement = xqyxjllbGridlb.element("hmc");
            String xm = trim(xmElement.getTextTrim());
            if(xjlltaxMap.containsKey(xm) && xjlltaxMap.get(xm) != null){
                String hc = xjlltaxMap.get(xm);
                if(xjllmap.containsKey(hc) && xjllmap.get(hc) != null){
                    XjllbVO xjllbVO = xjllmap.get(hc);
                    if(xjllbVO.getSqje() != null){
                        xqyxjllbGridlb.element("bnljje").setText(xjllbVO.getSqje().toString());
                    }
                    if(xjllbVO.getBqje() != null){
                        xqyxjllbGridlb.element("byje").setText(xjllbVO.getBqje().toString());
                    }
                }
            }
        }
    }
}

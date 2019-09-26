package com.dzf.zxkj.platform.util;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.*;

import java.util.HashMap;
import java.util.Map;

public class TaxExportUtil {

    protected static String trim(String str){
        if(StringUtil.isEmpty(str)){
            return "";
        }
        str = str.replaceAll("　| |：|\\(|\\)|:|（|）|","").trim();
        if(str.indexOf("、") != -1){
            str = str.substring(str.indexOf("、")+1, str.length());
        }
        return str;
    }

    public static Map<String, LrbVO> converLrbMap(LrbVO[] lrbvo) {
        Map<String,  LrbVO> lrbmap = new HashMap<String,LrbVO>();
        for(LrbVO vo:lrbvo){
            if(!StringUtil.isEmpty(vo.getHs())){
                lrbmap.put(vo.getHs(), vo);
            }
        }
        return lrbmap;
    }

    public static Map<String, ZcFzBVO> converZcfzmap(ZcFzBVO[] zcfzbvos) {
        Map<String, ZcFzBVO> zcfzmap = new HashMap<String, ZcFzBVO>();

        for(ZcFzBVO bvo:zcfzbvos){
            if(!StringUtil.isEmpty(bvo.getHc1())){
                zcfzmap.put(bvo.getHc1(), bvo);
            }
            if(!StringUtil.isEmpty(bvo.getHc2())){
                zcfzmap.put(bvo.getHc2(), bvo);
            }
        }
        return zcfzmap;
    }


    public static Map<String, XjllbVO> converXjllMap(XjllbVO[] xjllbvo) {
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

    public static String getDzfDouble(DZFDouble value){
        if(value == null){
            return "0";
        }else{
            return value.setScale(2, DZFDouble.ROUND_HALF_UP).toString();
        }
    }

    public static Map<String, String> converZcfzTax(ZcfzTaxVo[] zcfztaxvos) {
        Map<String, String> zcfzTaxVoMap = new HashMap<>();

        for(ZcfzTaxVo zcfzTaxVo : zcfztaxvos){
            if(zcfzTaxVo.getZcname() != null && zcfzTaxVo.getZchc_ref() != null){
                zcfzTaxVoMap.put(trim(zcfzTaxVo.getZcname()),zcfzTaxVo.getZchc_ref().toString());
            }
            if(zcfzTaxVo.getFzname() != null && zcfzTaxVo.getFzhc_ref() != null){
                zcfzTaxVoMap.put(trim(zcfzTaxVo.getFzname()),zcfzTaxVo.getFzhc_ref().toString());
            }
        }
        return zcfzTaxVoMap;
    }


    public static Map<String, String> converLrbTax(LrbTaxVo[] lrbTaxVos) {
        Map<String, String> lrbTaxVoMap = new HashMap();
        for(LrbTaxVo lrbTaxVo : lrbTaxVos){
            if(lrbTaxVo.getVname() != null && lrbTaxVo.getHc_ref() != null){
                lrbTaxVoMap.put(trim(lrbTaxVo.getVname()), lrbTaxVo.getHc_ref().toString());
            }
        }
        return lrbTaxVoMap;
    }

    public static Map<String, String> converXjllTax(XjllTaxVo[] xjllTaxVos) {
        Map<String, String> XjllTaxVoMap = new HashMap<>();

        for(XjllTaxVo xjllTaxVo : xjllTaxVos){
            if(xjllTaxVo.getVname() != null && xjllTaxVo.getHc_ref() != null){
                XjllTaxVoMap.put(trim(xjllTaxVo.getVname()), xjllTaxVo.getHc_ref().toString());
            }
        }
        return XjllTaxVoMap;
    }
}

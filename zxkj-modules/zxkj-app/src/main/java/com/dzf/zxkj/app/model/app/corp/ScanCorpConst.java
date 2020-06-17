package com.dzf.zxkj.app.model.app.corp;

import java.util.HashMap;

/**
 *
 */
public interface ScanCorpConst {
    
    public static HashMap<String, Integer> corpInfoMap=new HashMap<String, Integer>(){{    
        put("统一社会信用代码", 0); 
        put("注册号", 0); 
        put("企业名称", 1); 
        put("名称", 1);
        put("类型", 2); 
        put("法定代表人", 3); 
        put("经营者", 3); 
        put("负责人", 3); 
        put("执行事务合伙人", 3); 
        put("投资人", 3); 
        put("注册资本", 4); 
        put("成立日期", 5); 
        put("注册日期", 5); 
        put("营业期限自", 6); 
        put("营业期限至", 7); 
        put("登记机关", 8); 
        put("核准日期", 9); 
        put("登记状态", 10); 
        put("住所", 11);
        put("营业场所", 11);
        put("主要经营场所", 11);
        put("经营场所", 11);
        put("经营范围", 12); 
    }};  
}

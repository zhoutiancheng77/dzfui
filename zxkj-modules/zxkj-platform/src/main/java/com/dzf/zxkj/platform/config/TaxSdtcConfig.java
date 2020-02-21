package com.dzf.zxkj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaxSdtcConfig {
    @Value("${zxkj.taxrpt.sdtc.testtaxurl}")
    public String testtaxurl;// 测试地址
    @Value("${zxkj.taxrpt.sdtc.protaxurl}")
    public String protaxurl;// 正式地址
    @Value("${zxkj.taxrpt.sdtc.clientno}")
    public String clientno;
    @Value("${zxkj.taxrpt.sdtc.applicationid}")
    public String applicationid;
    @Value("${zxkj.taxrpt.sdtc.supplier}")
    public String supplier;// 大账房账号
    @Value("${zxkj.taxrpt.sdtc.password}")
    public String password;// 大账房密码
    @Value("${zxkj.taxrpt.sdtc.enabled}")
    public String enabled;
    @Value("${zxkj.taxrpt.sdtc.istest}")
    public String istest;// 是否测试环境
}

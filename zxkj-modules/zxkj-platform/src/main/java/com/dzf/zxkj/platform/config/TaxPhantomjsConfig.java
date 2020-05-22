package com.dzf.zxkj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaxPhantomjsConfig {
    @Value("${zxkj.taxrpt.phantomjs.phantomjs_URL}")
    public String phantomjs_URL;// 测试地址
    @Value("${zxkj.taxrpt.phantomjs.devmode}")
    public String devmode;// 正式地址
    @Value("${zxkj.taxrpt.phantomjs.test}")
    public String test;

}

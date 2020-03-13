package com.dzf.zxkj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaxJstcConfig {
    @Value("${zxkj.taxrpt.jstc.url}")
    public String url;
    @Value("${zxkj.taxrpt.jstc.app_secret}")
    public String app_secret;
    @Value("${zxkj.taxrpt.jstc.clientid}")
    public String clientid;
    @Value("${zxkj.taxrpt.jstc.action}")
    public String action;
//    @Value("${zxkj.taxrpt.jstc.service_switch}")
    public String service_switch = "off";
    @Value("${zxkj.taxrpt.jstc.dev_mode}")
    public String dev_mode;
}

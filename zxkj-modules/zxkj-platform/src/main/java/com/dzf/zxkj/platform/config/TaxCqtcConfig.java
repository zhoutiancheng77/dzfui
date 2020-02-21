package com.dzf.zxkj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TaxCqtcConfig {
    @Value("${zxkj.taxrpt.cqtc.authkey}")
    public String authkey;//双方系统约定authkey
    @Value("${zxkj.taxrpt.cqtc.ipEnable}")
    public String ipEnable;////限定的ip
    @Value("${zxkj.taxrpt.cqtc.ipAddrs}")
    public String ipAddrs;
    @Value("${zxkj.taxrpt.cqtc.url}")
    public String url;
    @Value("${zxkj.taxrpt.cqtc.socketip}")
    public String socketip;
    @Value("${zxkj.taxrpt.cqtc.socketport}")
    public String socketport;
}

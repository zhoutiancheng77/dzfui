package com.dzf.zxkj.app.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    @Value("${app.democorpcode}")
    public String democorpcode;
//    @Value("${app.chat_user_code_kf}")
    public String chat_user_code_kf;
//    @Value("${app.chat_corp_code_kf}")
    public String chat_corp_code_kf;
//    @Value("${app.invite_DZFURL}")
    public String invite_DZFURL;
    @Value("${app.sms.appid}")
    public String appid;
    @Value("${app.sms.appkey}")
    public String appkey;
    @Value("${app.sms.smsurl}")
    public String smsurl;

}

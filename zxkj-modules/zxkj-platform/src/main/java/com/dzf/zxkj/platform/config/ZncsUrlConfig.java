package com.dzf.zxkj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ZncsUrlConfig {
    @Value("${zncs.ptjx.ptjxurl}")
    public String ptjx_ptjxurl;//票通进项
    @Value("${zncs.fpcy.fpcyurl}")
    public String fpcy_fpcyurl;//发票查验
    @Value("${zncs.fpcy.ocrurl}")
    public String fpcy_ocrurl;
    @Value("${zncs.fpcy.busiurl}")
    public String fpcy_busiurl;
    @Value("${zncs.fpcy.authurl}")
    public String fpcy_authurl;
    @Value("${zncs.fpcy.version}")
    public String fpcy_version;
    @Value("${zncs.fpcy.appKey}")
    public String fpcy_appKey;
    @Value("${zncs.fpcy.appSecret}")
    public String fpcy_appSecret;
    @Value("${zncs.fpcy.uid}")
    public String fpcy_uid;
    @Value("${zncs.fpcy.randnum}")
    public String fpcy_randnum;
    @Value("${zncs.fpcy.unzip}")
    public String fpcy_unzip;
    @Value("${zncs.fpcy.unencry}")
    public String fpcy_unencry;
    @Value("${zncs.fpcy.endes}")
    public String fpcy_endes;
    @Value("${zncs.fpcy.enca}")
    public String fpcy_enca;
    @Value("${zncs.fpcy.codeType}")
    public String fpcy_codeType;
    @Value("${zncs.fpcy.standardtime}")
    public String fpcy_standardtime;
    @Value("${zncs.fpcy.regpwd}")
    public String fpcy_regpwd;
    @Value("${zncs.fpcy.pagesize}")
    public String fpcy_pagesize;
    @Value("${zncs.cft.busiurl}")
    public String cft_busiurl;//财房通
    @Value("${zncs.cft.getcorpnameurl}")
    public String cft_getcorpnameurl;
    @Value("${zncs.cft.appId}")
    public String cft_appId;
    @Value("${zncs.cft.version}")
    public String cft_version;
    @Value("${zncs.cft.randnum}")
    public String cft_randnum;
    @Value("${zncs.cft.regpwd}")
    public String cft_regpwd;
    @Value("${zncs.cft.encryptCode}")
    public String cft_encryptCode;
    @Value("${zncs.ptb.ptxxurl}")
    public String ptb_ptxxurl;//票通宝
    @Value("${zncs.ptb.xxptbm}")
    public String ptb_xxptbm;
    @Value("${zncs.ptb.platformCode}")
    public String ptb_platformCode;
    @Value("${zncs.ptb.signType}")
    public String ptb_signType;
    @Value("${zncs.ptb.format}")
    public String ptb_format;
    @Value("${zncs.ptb.xxversion}")
    public String ptb_xxversion;
    @Value("${zncs.ptb.xxpwd}")
    public String ptb_xxpwd;
    @Value("${zncs.ptb.xxsize}")
    public String ptb_xxsize;
    @Value("${zncs.ptb.privateKey}")
    public String ptb_privateKey;
    @Value("${zncs.ptb.publicKey}")
    public String ptb_publicKey;
    @Value("${zncs.ocr.inv_ip}")
    public String inv_ip;
    @Value("${zncs.ocr.webrow}")
    public String webrow;

    @Value("${zncs.ptkp.url}")// 开具 后续模块迁移 代码要调整
    public String ptkp_url;
    @Value("${zncs.ptkp.xxptbm}")
    public String ptkp_xxptbm;
    @Value("${zncs.ptkp.platformCode}")
    public String ptkp_platformCode;
    @Value("${zncs.ptkp.signType}")
    public String ptkp_signType;
    @Value("${zncs.ptkp.format}")
    public String ptkp_format;
    @Value("${zncs.ptkp.xxversion}")
    public String ptkp_xxversion;
    @Value("${zncs.ptkp.xxpwd}")
    public String ptkp_xxpwd;
    @Value("${zncs.ptkp.privateKey}")
    public String ptkp_privateKey;
    @Value("${zncs.ptkp.publicKey}")
    public String ptkp_publicKey;
}

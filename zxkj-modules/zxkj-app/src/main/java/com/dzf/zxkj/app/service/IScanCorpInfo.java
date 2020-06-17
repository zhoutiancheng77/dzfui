package com.dzf.zxkj.app.service;

import com.dzf.zxkj.app.model.app.corp.ScanCorpInfoVO;

/**
 * 扫描营业执照二维码信息
 * @author gejw
 * @time 2018年3月26日 上午9:41:49
 *
 */
public interface IScanCorpInfo {

    public ScanCorpInfoVO scanPermit(String url) ;
    
    /**
     * 我的客户录入时，抓取工商网站企业信息
     * @author gejw
     * @time 上午9:16:05
     * @param url
     * @
     */
    public ScanCorpInfoVO pickEnterpriseData(String url) ;
}

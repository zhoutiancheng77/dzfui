package com.dzf.zxkj.platform.config;

import com.dzf.admin.zxkj.service.contract.IZxkjContractService;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboReferenceConfig {
    @Reference(version = "1.0.0")
    private IZxkjReportService zxkjReportService;

    // 加盟商合同
    @Reference(version = "1.0.0")
    private IZxkjContractService zxkjContractService;

    @Bean("zxkjReportService")
    public IZxkjReportService zxkjReportService() {
        return zxkjReportService;
    }

    @Bean("zxkjContractService")
    public IZxkjContractService zxkjContractService() {
        return zxkjContractService;
    }
}

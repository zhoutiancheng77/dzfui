package com.dzf.zxkj.platform.config;

import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DubboReferenceConfig {
    @Reference(version = "1.0.0")
    private IZxkjReportService zxkjReportService;

    @Bean("zxkjReportService")
    public IZxkjReportService zxkjReportService() {
        return zxkjReportService;
    }
}

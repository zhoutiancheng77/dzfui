package com.dzf.zxkj.report.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class ReportConfig {
    @Value("${report.template.local.path}")
    private String path;
}

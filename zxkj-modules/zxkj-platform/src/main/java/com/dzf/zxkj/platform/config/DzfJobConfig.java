package com.dzf.zxkj.platform.config;

import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DzfJobConfig {

    @Value("${job.admin.addersses}")
    private String adminAddresses;

    @Value("${job.executor.appname}")
    private String appName;

    @Value("${job.executor.ip}")
    private String ip;

    @Value("${job.executor.port}")
    private int port;

    @Value("${job.accessToken}")
    private String accessToken;

    @Value("${job.executor.logpath}")
    private String logPath;

    @Value("${job.executor.logretentiondays}")
    private int logRetentionDays;

    @Bean
    public XxlJobSpringExecutor xxlJobExecutor(@Autowired InetUtils inetUtils) {
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppName(appName);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        if (StringUtils.isNotBlank(ip)) {
            xxlJobSpringExecutor.setIp(ip);
        } else {
            xxlJobSpringExecutor.setIp(inetUtils.findFirstNonLoopbackHostInfo().getIpAddress());
        }
        return xxlJobSpringExecutor;
    }
}


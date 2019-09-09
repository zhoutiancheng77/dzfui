package com.dzf.zxkj.log;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.log.service.IOperatorLogService;
import com.dzf.zxkj.log.service.impl.OperatorLogServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
@Configuration
@ConditionalOnBean(SingleObjectBO.class)
@ComponentScan("com.dzf.zxkj.log")
@EnableAspectJAutoProxy
public class LogAutoConfiguration {

    @Bean
    public IOperatorLogService getIOperatorLogService(){
        return new OperatorLogServiceImpl();
    }


}

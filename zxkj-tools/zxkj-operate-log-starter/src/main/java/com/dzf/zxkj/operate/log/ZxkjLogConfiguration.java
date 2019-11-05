package com.dzf.zxkj.operate.log;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.base.IOperatorLogService;
import com.dzf.zxkj.operate.log.service.OperatorLogServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnBean(SingleObjectBO.class)
public class ZxkjLogConfiguration {

    @Bean("sys_ope_log")
    public IOperatorLogService operatorLogService(){
        return new OperatorLogServiceImpl();
    }

}

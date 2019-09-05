package com.dzf.zxkj.base;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@ConditionalOnBean(DataSource.class)
@Configuration
@ComponentScan
public class ZxkjPersistentFrameworkAutoConfiguration {
    @Bean
    public SingleObjectBO singleObjectBO(){
        return new SingleObjectBO();
    }
}

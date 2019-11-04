package com.dzf.zxkj.base;

import com.dzf.zxkj.base.dao.MultBodyObjectBO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.SpringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@ConditionalOnBean(DataSource.class)
@Configuration
public class ZxkjPersistentFrameworkAutoConfiguration {
    @Bean
    public SingleObjectBO singleObjectBO() {
        return new SingleObjectBO();
    }
    @Bean
    public SpringUtils springUtils(){
        return new SpringUtils();
    }

    @Bean("multBodyObjectBO")
    public MultBodyObjectBO multBodyObjectBO(){
        return new MultBodyObjectBO();
    }
}

package com.dzf.zxkj.base.framework;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.sql.DataSource;

public class DataSourceFactory implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    public static DataSource getDataSource(String user, String corp) {
        DataSource ds = null;
        try {
            ds = applicationContext.getBean("dataSource", DataSource.class);
        } catch (Exception e) {

        }
        return ds;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

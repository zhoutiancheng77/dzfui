package com.dzf.zxkj.platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QmjzByDzfConfig {
    @Value("${zxkj.qmjz.dzf_pk_gs}")
    public String dzf_pk_gs;
    @Value("${zxkj.qmjz.jfcode}")
    public String jfcode = null;//借方编码
    @Value("${zxkj.qmjz.dfcode}")
    public String dfcode = null;//贷方编码
    @Value("${zxkj.qmjz.deptcode}")
    public String deptcode = null;//部门辅助核算指定编码
    @Value("${zxkj.qmjz.projcode}")
    public String projcode = null;//项目辅助核算指定编码

    //账簿导出预警
    @Value("${zxkj.dcyj.sql}")
    public String sql;
    @Value("${zxkj.dcyj.whitelist}")
    public String whitelist;
    //is channel
    @Value("${zxkj.dcyj.chalnum}")
    public String chalnum;
    //is not channel
    @Value("${zxkj.dcyj.unchalnum}")
    public String unchalnum;
    //smtp
    @Value("${zxkj.dcyj.smtp}")
    public String smtp;
    //send emails
    @Value("${zxkj.dcyj.from}")
    public String from;
    //receive emails
    @Value("${zxkj.dcyj.to}")
    public String to;
    //cc
    @Value("${zxkj.dcyj.copyto}")
    public String copyto;
    //user name
    @Value("${zxkj.dcyj.username}")
    public String username;
    //user password
    @Value("${zxkj.dcyj.password}")
    public String password;

}

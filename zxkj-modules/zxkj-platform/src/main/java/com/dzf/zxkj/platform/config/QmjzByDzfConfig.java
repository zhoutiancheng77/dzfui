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
}

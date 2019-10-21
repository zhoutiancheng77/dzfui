package com.dzf.zxkj.report.utils;

import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class SystemUtil {

    @Reference(version = "2.0.0")
    private IZxkjPlatformService zxkjPlatformService;

    public static SystemUtil systemUtil;

    @PostConstruct
    public void init(){
        systemUtil = this;
    }

    public static CorpVO queryCorp(String pk_corp){
        return systemUtil.zxkjPlatformService.queryCorpByPk(pk_corp);
    }

    public static UserVO queryUser(String userid){
        return systemUtil.zxkjPlatformService.queryUserById(userid);
    }

}

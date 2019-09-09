package com.dzf.zxkj.platform.dubbo.service;

import com.dzf.zxkj.platform.api.IPermissionService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
@Service(version = "1.0.1", protocol = {"dubbo"})
@Slf4j
public class PermissionServiceImpl implements IPermissionService {
    @Override
    public List<String> queryAllPermissionVo() {
        log.info("--------------------queryAllPermissionVo------------------------");
        return null;
    }

    @Override
    public List<String> queryPermissionVoByUserName(String username) {
        log.info("--------------------queryPermissionVoByUserName------------------------");
        return null;
    }
}

package com.dzf.zxkj.platform.auth.service.impl;

import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.service.INotifyService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;

@Service(version = "1.0.1", protocol = {"dubbo"},group="zxkj")
@Slf4j
public class INotifyServiceImpl implements INotifyService {
    @Override
    public void notify(NotifyType type, String token, UserVO vo) {
        log.info("接收统一登录通知------>", token);
    }
}

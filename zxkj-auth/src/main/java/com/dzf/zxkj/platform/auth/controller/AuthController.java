package com.dzf.zxkj.platform.auth.controller;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    private ILoginService loginService;

    @PostMapping("/login")
    public ReturnData<String> login(@RequestBody LoginUser loginUser) {
        if (StringUtils.isAnyBlank(loginUser.getUsername(), loginUser.getPassword())) {
            return ReturnData.error().message("用户名或密码不能为空！");
        }

        String token = loginService.login(loginUser.getUsername(), loginUser.getPassword());

        if (StringUtils.isBlank(token)) {
            return ReturnData.error("用户名或密码不正确！");
        }

        return ReturnData.ok().data(token);
    }

}

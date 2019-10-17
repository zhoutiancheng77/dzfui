package com.dzf.zxkj.platform.auth.controller;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import com.dzf.zxkj.platform.auth.util.RSAUtils;
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
    public ReturnData<Grid> login(@RequestBody LoginUser loginUser) {
        Grid<LoginUser> grid = new Grid<>();
        String username = RSAUtils.decryptStringByJs(loginUser.getUsername());
        String password = RSAUtils.decryptStringByJs(loginUser.getPassword());

        if (StringUtils.isAnyBlank(username, password)) {
            grid.setSuccess(false);
            grid.setMsg("用户名或密码不能为空！");
            return ReturnData.ok().data(grid);
        }

        loginUser = loginService.login(username, password);

        if (loginUser == null) {
            grid.setSuccess(false);
            grid.setMsg("用户名或密码不正确！");
            return ReturnData.ok().data(grid);
        }
        grid.setSuccess(true);
        grid.setRows(loginUser);
        return ReturnData.ok().data(grid);
    }

}

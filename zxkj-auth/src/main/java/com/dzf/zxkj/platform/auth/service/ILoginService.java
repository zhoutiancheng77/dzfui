package com.dzf.zxkj.platform.auth.service;

import com.dzf.zxkj.platform.auth.entity.LoginUser;

public interface ILoginService {
    LoginUser login(String usernam, String password) throws Exception;

    LoginUser exchange(String resource) throws Exception;

    void refresh(String token);
}

package com.dzf.zxkj.platform.auth.service;

import com.dzf.zxkj.platform.auth.entity.LoginUser;

public interface ILoginService {
    LoginUser login(String usernam, String password, boolean flag);
    LoginUser exchange(String resource);

    void refresh(String token);
}

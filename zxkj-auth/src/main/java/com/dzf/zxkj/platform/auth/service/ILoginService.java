package com.dzf.zxkj.platform.auth.service;

public interface ILoginService {
    String login(String usernam, String password);

    void refresh(String token);
}

package com.dzf.zxkj.platform.auth.model.jwt;

import java.io.Serializable;

/**
 * @Auther: dandelion
 * @Date: 2019-08-13
 * @Description:
 */
public class JWTInfo implements Serializable, IJWTInfo {
    private static final long serialVersionUID = -7931679042997311430L;
    private String username;
    private String userid;

    public JWTInfo(String username, String userid) {
        this.username = username;
        this.userid = userid;
    }


    @Override
    public String getSubject() {
        return username;
    }

    @Override
    public String getBody() {
        return userid;
    }
}

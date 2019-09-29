package com.dzf.zxkj.platform.auth.service;

import java.util.List;

public interface IAuthService {
    byte[] getPubKey();
    List<String> getPkCorpByUserId(String userid);
}

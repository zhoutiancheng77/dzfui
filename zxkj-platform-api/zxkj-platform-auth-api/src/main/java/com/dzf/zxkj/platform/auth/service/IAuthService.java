package com.dzf.zxkj.platform.auth.service;

import java.util.List;
import java.util.Set;

public interface IAuthService {
    byte[] getPubKey();
    List<String> getPkCorpByUserId(String userid);
    Set<String> getAllPermission();
    Set<String> getPermisssionByUserid(String userid);
}

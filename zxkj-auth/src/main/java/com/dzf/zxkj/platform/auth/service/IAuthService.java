package com.dzf.zxkj.platform.auth.service;

import java.util.List;
import java.util.Set;

public interface IAuthService {
    byte[] getPubKey();
    List<String> getPkCorpByUserId(String userid);
    Set<String> getAllPermission();
    Set<String> getPermisssionByUseridAndPkCorp(String userid, String pk_corp);
    boolean validateTokenEx(String token, String clientid);
    boolean validateTokenByInter(String token);//调用统一认证
    boolean validateMultipleLogin(String userid, String clientid);
}

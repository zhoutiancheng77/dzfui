package com.dzf.zxkj.platform.auth.secret.service;

public interface ISecretKeyService {
    String getPubKey();
    String getPreKey();
    String getDefaultKey();
}

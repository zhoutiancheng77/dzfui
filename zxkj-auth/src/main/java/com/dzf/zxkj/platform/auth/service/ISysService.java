package com.dzf.zxkj.platform.auth.service;


import com.dzf.zxkj.auth.model.sys.CorpModel;
import com.dzf.zxkj.auth.model.sys.UserModel;

public interface ISysService {
    UserModel queryByUserId(String userid);
    CorpModel queryCorpByPk(String pk_corp);
    UserModel queryByUserName(String username);
}

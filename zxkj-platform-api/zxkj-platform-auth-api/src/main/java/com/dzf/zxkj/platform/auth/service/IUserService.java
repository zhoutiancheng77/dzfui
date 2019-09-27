package com.dzf.zxkj.platform.auth.service;

import com.dzf.zxkj.platform.auth.model.UserModel;

public interface IUserService {
    UserModel queryByUserId(String userid);
}

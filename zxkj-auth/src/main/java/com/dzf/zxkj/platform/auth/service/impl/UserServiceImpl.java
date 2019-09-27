package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.auth.mapper.UserMapper;
import com.dzf.zxkj.platform.auth.model.UserModel;
import com.dzf.zxkj.platform.auth.service.IUserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(version = "1.0.0", timeout = 2 * 60 * 1000) //version方便上线
public class UserServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserModel queryByUserId(String userid) {
        if (StringUtil.isEmptyWithTrim(userid)) {
            return null;
        }
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserModel::getCuserid, userid).and(condition -> condition.eq(UserModel::getDr, "0").or().isNull(UserModel::getDr));
        UserModel userModel = userMapper.selectOne(queryWrapper);
        return userModel;
    }
}

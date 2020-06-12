package com.dzf.zxkj.platform.auth.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.auth.model.sys.CorpModel;
import com.dzf.zxkj.auth.model.sys.UserModel;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.auth.mapper.CorpMapper;
import com.dzf.zxkj.platform.auth.mapper.UserMapper;
import com.dzf.zxkj.platform.auth.service.ISysService;
import com.dzf.zxkj.platform.auth.service.impl.fallback.SysServiceFallBack;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("all")
@Service
public class SysServiceImpl implements ISysService {

    @Autowired
    private CorpMapper corpMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @SentinelResource(value="auth-resource", fallbackClass= SysServiceFallBack.class, fallback = "queryByUserId")
    public UserModel queryByUserId(String userid) {
        if (StringUtil.isEmptyWithTrim(userid)) {
            return null;
        }
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserModel::getCuserid, userid).and(condition -> condition.eq(UserModel::getDr, "0").or().isNull(UserModel::getDr));
        UserModel userModel = userMapper.selectOne(queryWrapper);
        return userModel;
    }

    @Override
    @SentinelResource(value="auth-resource", fallbackClass= SysServiceFallBack.class, fallback = "queryCorpByPk")
    public CorpModel queryCorpByPk(String pk_corp) {
        if(StringUtil.isEmptyWithTrim(pk_corp)){
            return null;
        }
        QueryWrapper<CorpModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(CorpModel::getPk_corp, pk_corp).and(condition -> condition.eq(CorpModel::getDr, "0").or().isNull(CorpModel::getDr));
        CorpModel corpModel = corpMapper.selectOne(queryWrapper);
        if(corpModel == null){
            return null;
        }
        corpModel.setUnitname(CodeUtils1.deCode(corpModel.getUnitname()));
        corpModel.setUnitshortname(CodeUtils1.deCode(corpModel.getUnitshortname()));
        return corpModel;
    }

    @Override
    public UserModel queryByUserName(String username) {
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserModel::getUser_code, username).and(condition -> condition.eq(UserModel::getDr, "0").or().isNull(UserModel::getDr));
        UserModel userModel = userMapper.selectOne(queryWrapper);
        return userModel;
    }
}

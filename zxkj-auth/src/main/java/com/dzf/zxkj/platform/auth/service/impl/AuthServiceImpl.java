package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.entity.UserCorpRelation;
import com.dzf.zxkj.platform.auth.mapper.UserCorpRelationMapper;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service(version = "1.0.0")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private UserCorpRelationMapper userCorpRelationMapper;

    @Override
    public byte[] getPubKey() {
        return rsaKeyConfig.getUserPubKey();
    }

    @Override
    public List<String> getPkCorpByUserId(String userid) {
        QueryWrapper<UserCorpRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserCorpRelation::getUserid, userid).and(condition -> condition.eq(UserCorpRelation::getDr, "0").or().isNull(UserCorpRelation::getDr));
        List<UserCorpRelation> userCorpRelationList = userCorpRelationMapper.selectList(queryWrapper);
        if(userCorpRelationList == null || userCorpRelationList.size() == 0){
            return new ArrayList();
        }
        return userCorpRelationList.stream().map(UserCorpRelation::getPk_corp).collect(Collectors.toList());
    }
}

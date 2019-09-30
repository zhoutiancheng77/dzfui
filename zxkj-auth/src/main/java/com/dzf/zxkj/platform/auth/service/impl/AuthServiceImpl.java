package com.dzf.zxkj.platform.auth.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.entity.FunNode;
import com.dzf.zxkj.platform.auth.entity.UserCorpRelation;
import com.dzf.zxkj.platform.auth.mapper.FunNodeMapper;
import com.dzf.zxkj.platform.auth.mapper.UserCorpRelationMapper;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.dzf.zxkj.platform.auth.service.impl.fallback.AuthServiceFallBack;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(version = "1.0.0", timeout = 2 * 60 * 1000)
@SuppressWarnings("all")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private UserCorpRelationMapper userCorpRelationMapper;

    @Autowired
    private FunNodeMapper funNodeMapper;

    @Override
    public byte[] getPubKey() {
        return rsaKeyConfig.getUserPubKey();
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = AuthServiceFallBack.class, fallback = "getPkCorpByUserId")
    public List<String> getPkCorpByUserId(String userid) {
        QueryWrapper<UserCorpRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserCorpRelation::getUserid, userid).and(condition -> condition.eq(UserCorpRelation::getDr, "0").or().isNull(UserCorpRelation::getDr));
        List<UserCorpRelation> userCorpRelationList = userCorpRelationMapper.selectList(queryWrapper);
        if (userCorpRelationList == null || userCorpRelationList.size() == 0) {
            return new ArrayList();
        }
        return userCorpRelationList.stream().map(UserCorpRelation::getPk_corp).collect(Collectors.toList());
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = AuthServiceFallBack.class, fallback = "getAllPermission")
    public Set<String> getAllPermission() {
        QueryWrapper<FunNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FunNode::getModule, "dzf_kj").and(condition -> condition.eq(FunNode::getDr, "0").or().isNull(FunNode::getDr));
        List<FunNode> funNodeList = funNodeMapper.selectList(queryWrapper);
        return funNodeList.stream().filter(v -> StringUtils.isNotBlank(v.getNodeurl())).map(FunNode::getNodeurl).flatMap(str -> Stream.of(str.split(","))).collect(Collectors.toSet());
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = AuthServiceFallBack.class, fallback = "getPermisssionByUseridAndPkCorp")
    public Set<String> getPermisssionByUseridAndPkCorp(String userid, String pk_corp) {
        List<FunNode> funNodeList = funNodeMapper.getFunNodeByUseridAndPkCorp(userid, pk_corp);
        return funNodeList.stream().filter(v -> StringUtils.isNotBlank(v.getNodeurl())).map(FunNode::getNodeurl).flatMap(str -> Stream.of(str.split(","))).collect(Collectors.toSet());
    }
}

package com.dzf.zxkj.platform.auth.service.impl;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.result.Result;
import com.dzf.auth.api.service.ILoginService;
import com.dzf.zxkj.platform.auth.cache.AuthCache;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.FunNode;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.entity.UserCorpRelation;
import com.dzf.zxkj.platform.auth.mapper.FunNodeMapper;
import com.dzf.zxkj.platform.auth.mapper.UserCorpRelationMapper;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.dzf.zxkj.platform.auth.service.impl.fallback.AuthServiceFallBack;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service(version = "1.0.0", timeout = 2 * 60 * 1000)
@Slf4j
@SuppressWarnings("all")
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private UserCorpRelationMapper userCorpRelationMapper;
    @Autowired
    private AuthCache authCache;

    @Autowired
    private FunNodeMapper funNodeMapper;

    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    //    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private ILoginService userService;

    @Override
    public byte[] getPubKey() {
        return rsaKeyConfig.getUserPubKey();
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = AuthServiceFallBack.class, fallback = "getPkCorpByUserId")
    public List<String> getPkCorpByUserId(String userid) {
        QueryWrapper<UserCorpRelation> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserCorpRelation::getUserid, userid).and(condition -> condition.eq(UserCorpRelation::getDr, 0).or().isNull(UserCorpRelation::getDr));
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

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = AuthServiceFallBack.class, fallback = "validateTokenEx")
    public boolean validateTokenEx(String userid, String clientId) {
        //过期返回true 结合redis实现
        LoginUser loginUser = authCache.getLoginUser(userid);
        if (loginUser == null) {
            return true;
        }
        authCache.putLoginUser(userid, loginUser);
        authCache.putLoginUnique(userid, clientId);
        return false;
    }

    @Override
    @SentinelResource(value = "auth-resource", fallbackClass = AuthServiceFallBack.class, fallback = "validateTokenByInter")
    public boolean validateTokenByInter(String token) {
        Result<UserVO> rs = userService.exchangeResource(zxkjPlatformAuthConfig.getPlatformName(), token);
        if (rs.getData() != null) {
            return true;
        }
        return false;
    }

    @Override
    public boolean validateMultipleLogin(String userid, String clientid) {
        boolean r = false;
        try {
            r = authCache.checkIsMulti(userid, clientid);
        } catch (Exception e) {
            log.error(String.format("authCache方法：checkIsMulti异常: %s, 参数{userid：%s, clientid：%s}]", e.getMessage(), userid, clientid), e);
        }
        return r;
    }
}

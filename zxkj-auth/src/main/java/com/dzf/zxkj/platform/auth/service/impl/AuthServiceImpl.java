package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.result.Result;
import com.dzf.auth.api.service.ILoginService;
import com.dzf.zxkj.common.entity.CachedLoginUser;
import com.dzf.zxkj.platform.auth.cache.AuthCache;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.FunNode;
import com.dzf.zxkj.platform.auth.entity.UserCorpRelation;
import com.dzf.zxkj.platform.auth.mapper.FunNodeMapper;
import com.dzf.zxkj.platform.auth.mapper.UserCorpRelationMapper;
import com.dzf.zxkj.platform.auth.service.IAuthService;
import com.dzf.zxkj.platform.auth.service.IVersionMngService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@SuppressWarnings("all")
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private IVersionMngService versionMngService;

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
//    @Cached(name = "user_corp", expire = 3600, cacheType = CacheType.REMOTE)
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
//    @Cached(name = "all_perssion", expire = Integer.MAX_VALUE, cacheType = CacheType.REMOTE)
    public Set<String> getAllPermission() {
        QueryWrapper<FunNode> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(FunNode::getModule, "dzf_kj").and(condition -> condition.eq(FunNode::getDr, "0").or().isNull(FunNode::getDr));
        List<FunNode> funNodeList = funNodeMapper.selectList(queryWrapper);
        return funNodeList.stream().filter(v -> StringUtils.isNotBlank(v.getNodeurl())).map(FunNode::getNodeurl).flatMap(str -> Stream.of(str.split(","))).collect(Collectors.toSet());
    }

    @Override
//    @Cached(name = "corp_user_perssion", expire = Integer.MAX_VALUE, key = "#userid+'-'+#pk_corp", cacheType = CacheType.REMOTE)
    public Set<String> getPermisssionByUseridAndPkCorp(String userid, String pk_corp) {
        List<FunNode> funNodeList = versionMngService.getFunNodeByUseridAndPkCorp(userid, pk_corp);
        return funNodeList.stream().filter(v -> StringUtils.isNotBlank(v.getNodeurl())).map(FunNode::getNodeurl).flatMap(str -> Stream.of(str.split(","))).collect(Collectors.toSet());
    }

    @Override
    public boolean validateTokenEx(String userid, String clientId) {
        //过期返回true 结合redis实现
        CachedLoginUser loginUser = authCache.getLoginUser(userid);
        if (loginUser == null) {
            return true;
        }
        authCache.putLoginUser(userid, loginUser);
        authCache.putLoginUnique(userid, clientId);
        return false;
    }

    @Override
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
            r = authCache.checkIsMulti(userid, clientid) ;
        } catch (Exception e) {
            log.error(String.format("authCache方法：checkIsMulti异常: %s, 参数{userid：%s, clientid：%s}]", e.getMessage(), userid, clientid), e);
        }
        return r;
    }
}

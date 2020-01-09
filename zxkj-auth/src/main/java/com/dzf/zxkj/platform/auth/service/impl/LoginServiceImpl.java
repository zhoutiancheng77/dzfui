package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.auth.api.model.platform.PlatformVO;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.result.Result;
import com.dzf.zxkj.common.utils.Encode;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.mapper.LoginUserMapper;
import com.dzf.zxkj.platform.auth.mapper.UserMapper;
import com.dzf.zxkj.platform.auth.model.jwt.JWTInfo;
import com.dzf.zxkj.platform.auth.model.sys.UserModel;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import com.dzf.zxkj.platform.auth.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    @Autowired
    private UserMapper userMapper;

    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private com.dzf.auth.api.service.ILoginService userService;

    private String platformName = "zxkj";

    public LoginUser login(String username, String password) {
        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        try {
            UserVO userVO = getRemoteLoginUser(username, password);
            if (userVO != null) {
                if(StringUtils.isBlank(userVO.getPlatformUserId())){
                    UserModel userModel = queryUser(String.valueOf(userVO.getId()));
                    userVO.setPlatformUserId(userModel.getCuserid());
                }
                return transfer(userVO);
            }
        } catch (Exception e) {
            log.error("用户中心异常", e);
        }

        return getLocalLoginUser(username, password);
    }

    //获取用户中心用户信息
    private UserVO getRemoteLoginUser(String username, String password) {
        Result<UserVO> rs = userService.loginByLoginName(platformName, username, password);
        if (rs.getData() != null) {
            return rs.getData();
        }
        return null;
    }

    @Override
    public LoginUser exchange(String resource) throws Exception {
        Result<UserVO> rs = userService.exchangeResource(zxkjPlatformAuthConfig.getPlatformName(),resource);
        if (rs.getCode() == 200) {
            return transferToZxkjUser(rs.getData());
        }
        return null;
    }

    //获取本地用户信息
    private LoginUser getLocalLoginUser(String username, String password) {
        LoginUser loginUser = queryLoginUser(username);
        if (loginUser == null) {
            return null;
        }
        if (new Encode().encode(password).equals(loginUser.getPassword())) {
            String token = null;
            try {
                createToken(loginUser);
            } catch (Exception e) {
                log.info("用户名密码错误！");
            }
            return loginUser;
        }
        return null;
    }

    private void createToken(LoginUser loginUser) throws Exception {
        String token = JWTUtil.generateToken(new JWTInfo(loginUser.getUsername(), loginUser.getUserid()), rsaKeyConfig.getUserPriKey(), 60 * 24 * 60 * 60);
        loginUser.setToken(token);
    }

    private LoginUser queryLoginUser(String username) {
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LoginUser::getUsername, username).and(condition -> condition.eq(LoginUser::getDr, "0").or().isNull(LoginUser::getDr));
        return loginUserMapper.selectOne(queryWrapper);
    }

    private LoginUser transferToZxkjUser(UserVO uservo) {

        if(StringUtils.isBlank(uservo.getPlatformUserId())){
            UserModel userModel = queryUser(String.valueOf(uservo.getId()));
            uservo.setPlatformUserId(userModel.getCuserid());
        }

        LoginUser loginUser = queryUserById(uservo.getPlatformUserId());

        if (StringUtils.equalsAny(uservo.getPlatformTag(), zxkjPlatformAuthConfig.getPlatformName(), zxkjPlatformAuthConfig.getPlatformAdminName())) {
            loginUser.setUsername(uservo.getLoginName());
            loginUser.setDzfAuthToken(uservo.getUserToken());
            loginUser.setUserid(uservo.getPlatformUserId());
            if(uservo.getCanJumpPlatforms() != null && uservo.getCanJumpPlatforms().size() > 0){
                Set<PlatformVO> list = uservo.getCanJumpPlatforms().stream().filter(k -> k.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(k.getPlatformTag())).collect(Collectors.toSet());
                loginUser.setPlatformVOSet(list);
            }
            try {
                createToken(loginUser);
            } catch (Exception e) {
                log.error("跳转在线会计生成token失败", e);
            }
        } else {
            Optional<UserVO> userVOOptional = uservo.getBindUsers().stream().filter(v -> StringUtils.equalsAny(v.getPlatformTag(), zxkjPlatformAuthConfig.getPlatformName(), zxkjPlatformAuthConfig.getPlatformAdminName())).findFirst();
            userVOOptional.ifPresent(v -> {
                loginUser.setUsername(v.getLoginName());
                loginUser.setDzfAuthToken(uservo.getUserToken());
                loginUser.setUserid(v.getPlatformUserId());
                if(uservo.getCanJumpPlatforms() != null && uservo.getCanJumpPlatforms().size() != 0){
                    Set<PlatformVO> list = uservo.getCanJumpPlatforms().stream().filter(k -> k.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(k.getPlatformTag())).collect(Collectors.toSet());
                    loginUser.setPlatformVOSet(list);
                }
                try {
                    createToken(loginUser);
                } catch (Exception e) {
                    log.error("跳转在线会计生成token失败", e);
                }
            });
        }

        return loginUser;
    }

    private LoginUser transfer(UserVO uservo) {
        LoginUser loginUser = queryUserById(uservo.getPlatformUserId());
        loginUser.setUsername(uservo.getLoginName());
        loginUser.setDzfAuthToken(uservo.getUserToken());
        loginUser.setUserid(uservo.getPlatformUserId());
        Set<PlatformVO> list = uservo.getCanJumpPlatforms().stream().filter(v -> v.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(v.getPlatformTag())).collect(Collectors.toSet());
        loginUser.setPlatformVOSet(list);
        try {
            createToken(loginUser);
        } catch (Exception e) {
            log.error("在线会计生成token失败", e);
            return null;
        }
        return loginUser;
    }

    @Override
    public void refresh(String token) {

    }

    private UserModel queryUser(String unifiedid) {
        QueryWrapper<UserModel> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(UserModel::getUnifiedid, unifiedid).and(condition -> condition.eq(UserModel::getDr, "0").or().isNull(UserModel::getDr));
        return userMapper.selectOne(queryWrapper);
    }

    @Override
    public LoginUser queryUserById(String userId) {
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LoginUser::getUserid, userId).and(condition -> condition.eq(LoginUser::getDr, "0").or().isNull(LoginUser::getDr));
        return loginUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void updatePassword(LoginUser loginUser) {
        loginUserMapper.updatePassWord(loginUser.getUserid(), loginUser.getPassword());
    }


    public void logout(String token) {

    }
}

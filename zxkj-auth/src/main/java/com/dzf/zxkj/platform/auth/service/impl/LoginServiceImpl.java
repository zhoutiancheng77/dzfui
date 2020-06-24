package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.auth.api.model.platform.PlatformVO;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.result.Result;
import com.dzf.auth.api.service.IPasswordService;
import com.dzf.zxkj.auth.model.jwt.JWTInfo;
import com.dzf.zxkj.auth.model.sys.UserModel;
import com.dzf.zxkj.auth.utils.JWTUtil;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.Encode;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.mapper.CorpMapper;
import com.dzf.zxkj.platform.auth.mapper.LoginUserMapper;
import com.dzf.zxkj.platform.auth.mapper.UserMapper;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@SuppressWarnings("all")
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private IPasswordService passwordService;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CorpMapper corpMapper;

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
                if (StringUtils.isBlank(userVO.getPlatformUserId())) {
                    UserModel userModel = queryUser(String.valueOf(userVO.getId()));
                    userVO.setPlatformUserId(userModel.getCuserid());
                }
                //查询是否是加盟商
                DZFBoolean isChannel = corpMapper.queryIsChannelByUserName(username);
                LoginUser loginUser = transfer(userVO);
                loginUser.setIsChannel(isChannel == null ? false : isChannel.booleanValue());
                if(!loginUser.getIsChannel()){
                    //非加盟商判断是否是重庆地区
                    String bankAccountArea = corpMapper.queryAreaByUserName(username);
                    loginUser.setIsBnakAccount(StringUtils.isNoneBlank(bankAccountArea)&& zxkjPlatformAuthConfig.getBankAcountArea().equals(bankAccountArea));
                }
                return loginUser;
            }
        } catch (Exception e) {
            log.error("用户中心异常", e);
        }

        return null;
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
        Result<UserVO> rs = userService.exchangeResource(zxkjPlatformAuthConfig.getPlatformName(), resource);
        if (rs.getCode() == 200) {
            UserVO userVO = rs.getData();
            if (zxkjPlatformAuthConfig.getPlatformName().equalsIgnoreCase(userVO.getPlatformTag())
                    || zxkjPlatformAuthConfig.getPlatformAdminName().equalsIgnoreCase(userVO.getPlatformTag())) {
                return transferToZxkjUser(userVO);
            } else {
                if (userVO.getBindUsers() != null) {
                    Optional<UserVO> u = userVO.getBindUsers().stream().filter(v -> zxkjPlatformAuthConfig.getPlatformName().equalsIgnoreCase(v.getPlatformTag()) || zxkjPlatformAuthConfig.getPlatformAdminName().equalsIgnoreCase(v.getPlatformTag())).findFirst();
                    if (u.isPresent()) {
                        UserVO uvo = u.get();
                        uvo.setUserToken(userVO.getUserToken());
                        uvo.setCanJumpPlatforms(userVO.getCanJumpPlatforms());
                        uvo.setBindUsers(userVO.getBindUsers());
                        return transferToZxkjUser(uvo);
                    }
                }
            }
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

            //查询是否是加盟商
            DZFBoolean isChannel = corpMapper.queryIsChannelByUserName(username);
            loginUser.setIsChannel(isChannel == null ? false : isChannel.booleanValue());
            if(!loginUser.getIsChannel()){
                //非加盟商判断是否是重庆地区
                String bankAccountArea = corpMapper.queryAreaByUserName(username);
                loginUser.setIsBnakAccount(StringUtils.isNoneBlank(bankAccountArea)&& zxkjPlatformAuthConfig.getBankAcountArea().equals(bankAccountArea));
            }else{
                loginUser.setIsBnakAccount(false);
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

        if (StringUtils.isBlank(uservo.getPlatformUserId())) {
            UserModel userModel = queryUser(String.valueOf(uservo.getId()));
            uservo.setPlatformUserId(userModel.getCuserid());
        }

        LoginUser loginUser = queryUserById(uservo.getPlatformUserId());

        if (StringUtils.equalsAny(uservo.getPlatformTag(), zxkjPlatformAuthConfig.getPlatformName(), zxkjPlatformAuthConfig.getPlatformAdminName())) {
            loginUser.setUsername(uservo.getLoginName());
            loginUser.setDzfAuthToken(uservo.getUserToken());
            loginUser.setUserid(uservo.getPlatformUserId());
            Set<PlatformVO> platformVOS = uservo.getCanJumpPlatforms() != null ? uservo.getCanJumpPlatforms().stream().filter(vo -> vo.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(vo.getPlatformTag())).collect(Collectors.toSet()) : new HashSet<>();
            boolean isExistsXwwy = platformVOS.stream().anyMatch(vo -> zxkjPlatformAuthConfig.getPlatformAdminName().equalsIgnoreCase(vo.getPlatformTag()));
            if (!isExistsXwwy) {
                PlatformVO platformVO = new PlatformVO();
                platformVO.setPlatformName("管理平台");
                platformVO.setPlatformDomain("http://ntadmin.dazhangfang.vip");
                platformVO.setPlatformTag("xwwy");
                platformVO.setPlatformIndexPage("/auth/jumpToAdmin");
                platformVO.setShow(true);
                platformVOS.add(platformVO);
            }
            loginUser.setPlatformVOSet(platformVOS);
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
                Set<PlatformVO> platformVOS = uservo.getCanJumpPlatforms() != null ? uservo.getCanJumpPlatforms().stream().filter(vo -> vo.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(vo.getPlatformTag())).collect(Collectors.toSet()) : new HashSet<>();
                boolean isExistsXwwy = platformVOS.stream().anyMatch(vo -> zxkjPlatformAuthConfig.getPlatformAdminName().equalsIgnoreCase(vo.getPlatformTag()));
                if (!isExistsXwwy) {
                    PlatformVO platformVO = new PlatformVO();
                    platformVO.setPlatformName("管理平台");
                    platformVO.setPlatformDomain("http://ntadmin.dazhangfang.vip");
                    platformVO.setPlatformTag("xwwy");
                    platformVO.setPlatformIndexPage("/auth/jumpToAdmin");
                    platformVO.setShow(true);
                    platformVOS.add(platformVO);
                }
                loginUser.setPlatformVOSet(platformVOS);
                try {
                    createToken(loginUser);
                } catch (Exception e) {
                    log.error("跳转在线会计生成token失败", e);
                }
            });
        }

        if(loginUser != null && StringUtils.isNotBlank(loginUser.getUsername())){
            //查询是否是加盟商
            DZFBoolean isChannel = corpMapper.queryIsChannelByUserName(loginUser.getUsername());
            loginUser.setIsChannel(isChannel == null ? false : isChannel.booleanValue());
            if(!loginUser.getIsChannel()){
                //非加盟商判断是否是重庆地区
                String bankAccountArea = corpMapper.queryAreaByUserName(loginUser.getUsername());
                loginUser.setIsBnakAccount(StringUtils.isNoneBlank(bankAccountArea)&& zxkjPlatformAuthConfig.getBankAcountArea().equals(bankAccountArea));
            }
        }

        return loginUser;
    }

    private LoginUser transfer(UserVO uservo) {
        LoginUser loginUser = queryUserById(uservo.getPlatformUserId());
        loginUser.setUsername(uservo.getLoginName());
        loginUser.setDzfAuthToken(uservo.getUserToken());
        loginUser.setUserid(uservo.getPlatformUserId());
        Set<PlatformVO> platformVOS = uservo.getCanJumpPlatforms() != null ? uservo.getCanJumpPlatforms().stream().filter(vo -> vo.isShow() && !zxkjPlatformAuthConfig.getPlatformName().equals(vo.getPlatformTag())).collect(Collectors.toSet()) : new HashSet<>();
        boolean isExistsXwwy = platformVOS.stream().anyMatch(vo -> zxkjPlatformAuthConfig.getPlatformAdminName().equalsIgnoreCase(vo.getPlatformTag()));
        if (!isExistsXwwy) {
            PlatformVO platformVO = new PlatformVO();
            platformVO.setPlatformName("管理平台");
            platformVO.setPlatformDomain("http://ntadmin.dazhangfang.vip");
            platformVO.setPlatformTag("xwwy");
            platformVO.setPlatformIndexPage("/auth/jumpToAdmin");
            platformVO.setShow(true);
            platformVOS.add(platformVO);
        }
        loginUser.setPlatformVOSet(platformVOS);
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
    public void updatePassword(LoginUser loginUser, String psw2) throws Exception {
        loginUserMapper.updatePassWord(loginUser.getUserid(), loginUser.getPassword());
        Result<Boolean> booleanResult = passwordService.updatePassword(zxkjPlatformAuthConfig.getPlatformAdminName(), loginUser.getUsername(), psw2);
        if (!booleanResult.isSucc()) {
            throw new Exception("修改失败");
        }
    }


    public void logout(String token) {

    }
}

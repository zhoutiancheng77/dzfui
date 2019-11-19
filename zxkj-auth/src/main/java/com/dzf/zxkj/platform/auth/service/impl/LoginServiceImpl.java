package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dzf.auth.api.model.platform.PlatformVO;
import com.dzf.auth.api.model.user.UserVO;
import com.dzf.auth.api.result.Result;
import com.dzf.zxkj.common.utils.Encode;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.mapper.LoginUserMapper;
import com.dzf.zxkj.platform.auth.model.jwt.JWTInfo;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import com.dzf.zxkj.platform.auth.utils.JWTUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

//    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private com.dzf.auth.api.service.ILoginService userService;

    public LoginUser login(String username, String password, boolean flag) {

        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }

        LoginUser loginUser = null;
//        if(flag){//校验用户(后期改成对接公司用户中心)
//            loginUser = getLoginUserInter(username, password);
//        }else{
            loginUser = getLoginUserSelf(username, password);
//        }

        return loginUser;
    }

    @Override
    public LoginUser exchange(String resource) {
        Result<UserVO> rs = userService.exchangeResource(resource);
        if(rs.getCode() == 200){
            return transfer(rs.getData());
        }
        return null;
    }

    private LoginUser transfer(UserVO uservo){
        LoginUser loginUser = null;
        Set<PlatformVO> list = uservo.getCanJumpPlatforms();
        if(list != null && list.size() > 0){
            loginUser = new LoginUser();
            loginUser.setUsername(uservo.getUserName());
            loginUser.setToken(uservo.getUserToken());
            loginUser.setUserid(uservo.getPlatformUserId());
            loginUser.setUsername(uservo.getUserName());
            for(PlatformVO pvo : list){
                if("zxkj".equals(pvo.getPlatformTag())){
//                    loginUser.setUserid(pvo.getPlatformUserId());
//                    loginUser.setUsername(pvo.getUserName());
                }
            }
        }
        return loginUser;
    }

    private LoginUser getLoginUserSelf(String username, String password){
        LoginUser loginUser = queryLoginUser(username);

        if(loginUser == null){
            return null;
        }

        if (new Encode().encode(password).equals(loginUser.getPassword())) {
            String token = null;
            try {
                token = JWTUtil.generateToken(new JWTInfo(loginUser.getUsername(), loginUser.getUserid()), rsaKeyConfig.getUserPriKey(), 60 * 24 * 60 * 60);
            } catch (Exception e) {
                log.info("用户名密码错误！");
            }
            loginUser.setToken(token);
            return loginUser;
        }

        return null;
    }

    private LoginUser getLoginUserInter(String username, String password){

        Result<UserVO> rs = userService.loginByLoginName("zxkj", username, password);
        if(rs.getData() != null){
            return transfer(rs.getData());
        }
        return null;
    }

    private LoginUser queryLoginUser(String username) {
        QueryWrapper<LoginUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(LoginUser::getUsername, username).and(condition -> condition.eq(LoginUser::getDr, "0").or().isNull(LoginUser::getDr));
        return loginUserMapper.selectOne(queryWrapper);
    }

    @Override
    public void refresh(String token) {

    }

    public void logout(String token) {

    }
}

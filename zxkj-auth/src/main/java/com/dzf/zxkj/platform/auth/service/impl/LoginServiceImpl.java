package com.dzf.zxkj.platform.auth.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

@Slf4j
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private LoginUserMapper loginUserMapper;

    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    public String login(String username, String password) {

        if (StringUtils.isAnyBlank(username, password)) {
            return null;
        }
        //校验用户(后期改成对接公司用户中心)
        LoginUser loginUser = queryLoginUser(username);

        System.out.println(password);
        System.out.println(loginUser.getPassword());
        System.out.println(new Encode().encode(password));
        System.out.println(new Encode().decode(loginUser.getPassword()));

        if (new Encode().encode(password).equals(loginUser.getPassword())) {
            String token = null;
            try {
                token = JWTUtil.generateToken(new JWTInfo(loginUser.getUsername(), loginUser.getUserid()), rsaKeyConfig.getUserPriKey(), 2 * 24 * 60 * 1000);
            } catch (Exception e) {
                log.info("用户名密码错误！");
            }
            return token;
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

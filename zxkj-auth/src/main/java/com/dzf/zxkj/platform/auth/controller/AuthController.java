package com.dzf.zxkj.platform.auth.controller;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.auth.cache.AuthCache;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.LoginGrid;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import com.dzf.zxkj.platform.auth.util.RSAUtils;
import com.dzf.zxkj.platform.auth.util.SystemUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@Slf4j
public class AuthController {
    //redis缓存 过期时间10分钟
    @CreateCache(name = "zxkj:check:code", cacheType = CacheType.REMOTE, expire = 10 * 60)
    private Cache<String, String> checkCodeCache;

    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private com.dzf.auth.api.service.ILoginService userService;
    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    @Autowired
    private ILoginService loginService;
    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired
    private AuthCache authCache;

    @RequestMapping("/captcha")
    public ReturnData captcha() throws Exception {
        // 设置请求头为输出图片类型
        Grid<Map> result = new Grid<>();
        Map<String, String> checkCode = new HashMap<>();

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(110, 49, 4);
        // 设置字体
        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_ONLY_NUMBER);
        String verCode = specCaptcha.text().toLowerCase();
        // 验证码存入缓存
        String key = UUID.randomUUID().toString();
        checkCodeCache.put(key, verCode);
        // 输出图片流
        result.setSuccess(true);
        checkCode.put("key", key);
        checkCode.put("image", specCaptcha.toBase64());
        result.setRows(checkCode);
        return ReturnData.ok().data(result);
    }


    @PostMapping("/login")
    public ReturnData<Grid> login(@RequestBody LoginUser loginUser) {

        String force = loginUser.getF();

        Grid<LoginUser> grid = new Grid<>();
        String verify = checkCodeCache.get(loginUser.getKey());

        if (verify == null || !verify.equals(loginUser.getVerify())) {
            grid.setSuccess(false);
            grid.setMsg("验证码错误！");
            return ReturnData.ok().data(grid);
        }

        String username = RSAUtils.decryptStringByJs(loginUser.getUsername());
        String password = RSAUtils.decryptStringByJs(loginUser.getPassword());

        if (StringUtils.isAnyBlank(username, password)) {
            grid.setSuccess(false);
            grid.setMsg("用户名或密码不能为空！");
            return ReturnData.ok().data(grid);
        }

        try {
            loginUser = loginService.login(username, password);
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg("系统异常！");
            return ReturnData.ok().data(grid);
        }

        if (loginUser == null) {
            grid.setSuccess(false);
            grid.setMsg("用户名或密码不正确！");
            return ReturnData.ok().data(grid);
        }

        String clientid = SystemUtil.getClientId();
        //普通登录  多人在线
        if(StringUtils.isNoneBlank(force) && force.equals("0") && authCache.checkIsMulti(loginUser.getUserid(), clientid)){
            LoginGrid g = new LoginGrid();
            g.setSuccess(false);
            g.setStatus(-100);
            return ReturnData.ok().data(g);
        }
        authCache.putLoginUnique(loginUser.getUserid(), clientid);
        authCache.putLoginUser(loginUser.getUserid(), loginUser);

        grid.setSuccess(true);
        grid.setRows(loginUser);
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/logout")
    public void logout() {
        LoginUser loginUser = authCache.getLoginUser(SystemUtil.getLoginUserId());
        userService.logout(zxkjPlatformAuthConfig.getPlatformName(), loginUser.getDzfAuthToken());
        authCache.logout(SystemUtil.getLoginUserId());
    }

    @GetMapping("loginByToken")
    public ReturnData loginByToken(@RequestParam("token") String token) {
        log.info("token登录---------------->", token);
        Grid<LoginUser> grid = new Grid<>();
        try {
            LoginUser loginUser = loginService.exchange(token);
            if (loginUser == null) {
                return ReturnData.ok().data(grid);
            }
            grid.setSuccess(true);
            authCache.putLoginUnique(loginUser.getUserid(), SystemUtil.getClientId());
            grid.setRows(loginUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnData.ok().data(grid);
    }

}

package com.dzf.zxkj.platform.auth.controller;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.auth.api.service.IPasswordService;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.CachedLoginUser;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.tool.IpUtil;
import com.dzf.zxkj.common.utils.Encode;
import com.dzf.zxkj.platform.auth.cache.AuthCache;
import com.dzf.zxkj.platform.auth.config.RsaKeyConfig;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.LoginGrid;
import com.dzf.zxkj.platform.auth.entity.LoginLogVo;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.entity.UpdateUserVo;
import com.dzf.zxkj.platform.auth.mapper.LoginLogMapper;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import com.dzf.zxkj.platform.auth.util.LoginUserUtil;
import com.dzf.zxkj.platform.auth.util.RSAUtils;
import com.dzf.zxkj.platform.auth.util.SystemUtil;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@Slf4j
public class AuthController {
    //redis缓存 过期时间10分钟
    @CreateCache(name = "zxkj:check:code", cacheType = CacheType.REMOTE, expire = 10 * 60)
    private Cache<String, String> checkCodeCache;

    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private com.dzf.auth.api.service.ILoginService userService;

    @Reference(version = "1.0.1", protocol = "dubbo", timeout = 9000)
    private IPasswordService passwordService;

    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    @Autowired
    private ILoginService loginService;
    @Autowired
    private RsaKeyConfig rsaKeyConfig;

    @Autowired(required = false)
    private HttpServletRequest request;

    @Autowired
    private LoginLogMapper loginLogMapper;

    @Autowired
    private AuthCache authCache;

    @RequestMapping("/captcha")
    public ReturnData captcha() throws Exception {
        // 设置请求头为输出图片类型
        Grid<Map> result = new Grid<>();
        Map<String, String> checkCode = new HashMap<>();

        // 三个参数分别为宽、高、位数
        SpecCaptcha specCaptcha = new SpecCaptcha(110, 49, 4);
        //设置内置字体
        int[] font = {0, 1, 2, 5, 8};
        specCaptcha.setFont(font[new Random().nextInt(5)]);//5以内随机数
        // 设置字体
//        specCaptcha.setFont(new Font("Verdana", Font.PLAIN, 32));  // 有默认字体，可以不用设置
        // 设置类型，纯数字、纯字母、字母数字混合
        specCaptcha.setCharType(Captcha.TYPE_DEFAULT);
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

    private LoginLogVo getLoginVo(String project) {
        LoginLogVo loginLogVo = new LoginLogVo();
        try {
            loginLogVo.setLogindate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
            loginLogVo.setLoginsession(SystemUtil.getClientId());
            loginLogVo.setLoginstatus(0);
            loginLogVo.setSys_version("1.0.0");
            loginLogVo.setProject_name(project);
            loginLogVo.setLoginip(IpUtil.getIpAddr(request));
        } catch (Exception e) {
            log.error("错误", e);
        }
        return loginLogVo;
    }

    @PostMapping("/login")
    public ReturnData<Grid> login(@RequestBody LoginUser loginUser) {

        String force = loginUser.getF();

        LoginLogVo loginLogVo = getLoginVo(ISysConstants.SYS_KJ);

        Grid<LoginUser> grid = new Grid<>();
        String verify = checkCodeCache.get(loginUser.getKey());

        if(!"test".equals(zxkjPlatformAuthConfig.getProfile())){//配合测试压力 如果是测试环境 不校验验证码
            if (verify == null) {
                grid.setSuccess(false);
                grid.setMsg("由于长时间未登录，请刷新后重新操作！");
                return ReturnData.ok().data(grid);
            } else if (!verify.equalsIgnoreCase(loginUser.getVerify())) {
                grid.setSuccess(false);
                grid.setMsg("验证码错误！");
                return ReturnData.ok().data(grid);
            }
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
        if (StringUtils.isNoneBlank(force) && force.equals("0") && authCache.checkIsMulti(loginUser.getUserid(), clientid)) {
            LoginGrid g = new LoginGrid();
            g.setSuccess(false);
            g.setStatus(-100);
            return ReturnData.ok().data(g);
        }

        authCache.putLoginUnique(loginUser.getUserid(), clientid);
        authCache.putLoginUser(loginUser.getUserid(), LoginUserUtil.transform(loginUser));
        loginUser.setPassword("");
        loginLogVo.setMemo("登陸成功");
        loginLogVo.setPk_user(loginUser.getUserid());


        try {
            loginLogMapper.insert(loginLogVo);
        } catch (Exception e) {
            log.error("记录登录日志异常", e);
        }
        grid.setSuccess(true);

        //初始密码修改
        if (password.equals("dzf12345678")) {
            grid.setSuccess(false);
            grid.setStatus(-200);
        }

        grid.setRows(loginUser);
        return ReturnData.ok().data(grid);
    }


    @PostMapping("/logout")
    public ReturnData logout() {
        try {
            CachedLoginUser loginUser = authCache.getLoginUser(SystemUtil.getLoginUserId());
            authCache.logout(SystemUtil.getLoginUserId(), SystemUtil.getClientId());
            userService.logout(zxkjPlatformAuthConfig.getPlatformName(), loginUser.getDzfAuthToken());
        } catch (Exception e) {
            log.error("登出异常：", e);
        }
        return ReturnData.ok();
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
            authCache.putLoginUser(loginUser.getUserid(), LoginUserUtil.transform(loginUser));
            loginUser.setPassword("");
            grid.setRows(loginUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ReturnData.ok().data(grid);
    }

    @GetMapping("selectSys")
    public ReturnData<LoginUser> selectSys() {
        CachedLoginUser loginUser = authCache.getLoginUser(SystemUtil.getLoginUserId());
        return ReturnData.ok().data(loginUser);
    }

    @PostMapping("updatePassword")
    public ReturnData updatePassword(@RequestBody UpdateUserVo updateUserVo) {
        Json json = new Json();
        String userid = SystemUtil.getLoginUserId();
        LoginUser loginUser = loginService.queryUserById(userid);
        if (loginUser == null) {
            json.setMsg("未找到用户信息！");
            json.setStatus(-200);
            json.setSuccess(false);
            return ReturnData.ok().data(json);
        }
        json.setStatus(-200);
        json.setSuccess(false);
        String password = RSAUtils.decryptStringByJs(updateUserVo.getUser_password());
        String psw2 = RSAUtils.decryptStringByJs(updateUserVo.getPsw2());
        String psw3 = RSAUtils.decryptStringByJs(updateUserVo.getPsw3());
        String p = new Encode().encode(password);
        try {
            if (updateUserVo.getUser_password() == null || updateUserVo.getUser_password().trim().length() == 0 || !(new Encode().encode(password)).equals(loginUser.getPassword())) {
                json.setMsg("输入初始密码错误！");
            } else if (psw2 != null && psw3 != null && psw2.trim().length() > 0 && psw3.trim().length() > 0) {
                if (psw2.equals(password)) {
                    json.setMsg("旧密码和新密码不能一致！");
                } else if (psw2.equals(psw3)) {
                    loginUser.setPassword(new Encode().encode(psw2));
                    //修改后密码校验
                    StringBuffer sf = new StringBuffer();
                    boolean flag = checkUserPWD(psw2, sf);
                    if (!flag) {
                        json.setMsg(sf.toString());
                        json.setSuccess(false);
                    } else {
                        loginService.updatePassword(loginUser, psw2);
                        json.setMsg("修改成功!");
                        json.setSuccess(true);
                        json.setStatus(200);
                    }
                } else {
                    json.setMsg("两次输入密码不一致，请检查！");
                }
            } else {
                json.setMsg("请输入密码信息！");
            }
        } catch (Exception e) {
            json.setMsg("操作失败");
            log.error("操作失败", e);
            json.setSuccess(false);
        }

        return ReturnData.ok().data(json);
    }

    String[] INIT_PASSWORD = {"123abc!@#", "1234abcd!@#$", "dzf12345678"};

    private boolean checkUserPWD(String pwd, StringBuffer eInfo) {
        if (pwd.length() < 8) {
            eInfo.append("密码长度不能小于8\n");
            return false;
        }
        if (!pwd.matches(".*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*")) {
            eInfo.append("密码必须含有数字、字母\n");
            return false;
        }
        //判断是否为初始化密码
        if (Arrays.asList(INIT_PASSWORD).contains(pwd)) {
            eInfo.append("密码为初始化密码!\n");
            return false;
        }
        String regEx = "[~!@#$%^&*()<>?+=]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(pwd);
        if (!m.find()) {
            eInfo.append("密码必须含有特殊字符\n");
            return false;
        }
        return true;
    }
}

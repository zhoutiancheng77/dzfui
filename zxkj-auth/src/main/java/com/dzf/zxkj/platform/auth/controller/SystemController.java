package com.dzf.zxkj.platform.auth.controller;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.auth.api.model.platform.PlatformVO;
import com.dzf.zxkj.platform.auth.config.ZxkjPlatformAuthConfig;
import com.dzf.zxkj.platform.auth.entity.LoginUser;
import com.dzf.zxkj.platform.auth.service.ILoginService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Controller
@Slf4j
public class SystemController {

    @CreateCache(name = "zxkj-platform-user", cacheType = CacheType.LOCAL, expire = 5, timeUnit = TimeUnit.DAYS)
    private Cache<String, LoginUser> platformUserCache;
    @Autowired
    private ILoginService loginService;
    @Autowired
    private ZxkjPlatformAuthConfig zxkjPlatformAuthConfig;

    /**
     * 跳去别处
     *
     * @param request
     * @param response
     * @param platformTag
     * @param userid
     */
    @GetMapping(value = "/to/{platform}/{userid}")
    public void jumpToOther(HttpServletRequest request,
                            HttpServletResponse response,
                            @PathVariable(value = "platform") String platformTag, @PathVariable(value = "userid") String userid) {
        LoginUser vo = platformUserCache.get(userid);

        Optional<PlatformVO> platformOptional = vo.getPlatformVOSet()
                .stream()
                .filter(e -> e.getPlatformTag().equals(platformTag))
                .findFirst();
        PlatformVO platform = platformOptional.get();
        StringBuilder sb = new StringBuilder("http://localhost:8084")
                .append("/external/login")
                .append("?token=")
                .append(vo.getDzfAuthToken());
        try {
            response.sendRedirect(sb.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 接收外部跳转
     */
    @GetMapping("/external/login")
    public void jumpToZxkj(HttpServletResponse response, @RequestParam("token") String token) {
        try {
            LoginUser loginUser = loginService.exchange(token);
            if (loginUser == null) {
                return;
            }
            platformUserCache.put(loginUser.getUserid(), loginUser);
            StringBuilder sb = new StringBuilder(zxkjPlatformAuthConfig.getUrl())
                    .append("?token=")
                    .append(loginUser.getToken());
            try {
                response.sendRedirect(sb.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            log.error("第三方跳转登陆失败", e);
        }
    }
}

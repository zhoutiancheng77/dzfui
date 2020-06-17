package com.dzf.zxkj.gateway.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.common.entity.CachedLoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@Slf4j
public class AuthCache {

    @CreateCache(name = "zxkj:auth:user:v1", cacheType = CacheType.REMOTE, expire = 8, timeUnit = TimeUnit.HOURS)
    private Cache<String, CachedLoginUser> platformUserCache;

    @CreateCache(name = "zxkj:auth:online:v1", cacheType = CacheType.REMOTE, expire = 8, timeUnit = TimeUnit.HOURS)
    private Cache<String, List<String>> platformUserOnlineCache;

    public CachedLoginUser getLoginUser(String userid) {
        return platformUserCache.get(userid);
    }

    public void putLoginUser(String userid, CachedLoginUser loginUser) {
        platformUserCache.put(userid, loginUser);
    }

    public void putLoginUnique(String userid, String clientId) {

        List<String> clients = platformUserOnlineCache.get(userid);

        if (clients == null) {
            clients = new ArrayList<>();
        }

        String prefix = clientId.substring(0, 4);

        if (StringUtils.isAllUpperCase(prefix)) {
            clients = clients.stream().filter(v -> !v.startsWith(prefix)).collect(Collectors.toList());
        } else {
            clients = clients.stream().filter(v -> StringUtils.isAllUpperCase(v.substring(0, 4))).collect(Collectors.toList());
        }

        clients.add(clientId);

        platformUserOnlineCache.put(userid, clients);
    }

    public boolean checkIsMulti(String userid, String clientId) {
        List<String> client = platformUserOnlineCache.get(userid);
        if (client == null) {
            return false;
        }

        if (StringUtils.isBlank(clientId)) {
            return false;
        }

        String prefix = clientId.substring(0, 4);

        if (StringUtils.isAllUpperCase(prefix)) {
            Optional<String> clientOption = client.stream().filter(v -> v.startsWith(prefix) && !v.equalsIgnoreCase(clientId)).findFirst();
            return clientOption.isPresent();
        } else {
            Optional<String> clientOption = client.stream().filter(v -> !StringUtils.isAllUpperCase(v.substring(0, 4)) && !v.equalsIgnoreCase(clientId)).findFirst();
            return clientOption.isPresent();
        }
    }

    public void logout(String userid, String client) {
        List<String> clts = platformUserOnlineCache.get(userid);
        if (clts != null && clts.contains(client)) {
            platformUserOnlineCache.put(userid, clts.stream().filter(v -> !v.equalsIgnoreCase(client)).collect(Collectors.toList()));
            platformUserCache.remove(userid);
        }
    }

}

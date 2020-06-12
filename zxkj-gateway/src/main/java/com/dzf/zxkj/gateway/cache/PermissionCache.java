package com.dzf.zxkj.gateway.cache;

import com.alicp.jetcache.Cache;
import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.CreateCache;
import com.dzf.zxkj.common.constant.AuthConstant;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
public class PermissionCache {
    @CreateCache(name = AuthConstant.AUTH_PERMISSION_PREFIX, cacheType = CacheType.REMOTE)
    private Cache<String, Set<String>> permissionCache;

    public void putAllPermission(Set<String> allPermissions) {
        permissionCache.put(AuthConstant.AUTH_ALL_PERMISSION, allPermissions);
    }

    public Set<String> getAllPermission() {
        return permissionCache.get(AuthConstant.AUTH_ALL_PERMISSION);
    }


    public void putUserCorpPermission(String userId, String pk_corp, Set<String> permissions){
        permissionCache.put(userId+"_"+pk_corp, permissions);
    }

    public Set<String> getUserCorpPermission(String userId, String pk_corp){
        return permissionCache.get(userId+"_"+pk_corp);
    }
}

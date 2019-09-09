package com.dzf.zxkj.platform.api;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */
public interface IPermissionService {
    List<String> queryAllPermissionVo();
    List<String> queryPermissionVoByUserName(String username);
}

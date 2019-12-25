package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.UpdateVersionVO;

public interface IUpdateLogService {
    UpdateVersionVO query(String pk_user, String module) throws DZFWarpException;

    void save(String pk_corp, String pk_user, String versionid) throws DZFWarpException;
}

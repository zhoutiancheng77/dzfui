package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CustServVO;

public interface ICustServService {
    CustServVO query(String paramString) throws DZFWarpException;
}

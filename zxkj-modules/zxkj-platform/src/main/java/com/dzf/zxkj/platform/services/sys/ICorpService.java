package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface ICorpService {
    CorpVO queryByPk(String pk_corp) throws DZFWarpException;
}

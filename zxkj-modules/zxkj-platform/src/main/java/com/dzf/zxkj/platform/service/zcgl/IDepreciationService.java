package com.dzf.zxkj.platform.service.zcgl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zcgl.DepreciationVO;

public interface IDepreciationService {
    DepreciationVO[] query(String corp, String voucher) throws DZFWarpException;
}

package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.vo.am.zcgl.ValuemodifyQueryVO;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IValuemodifyService {
    ValuemodifyVO save(ValuemodifyVO vo) throws DZFWarpException;

    List<ValuemodifyVO> query(ValuemodifyQueryVO valuemodifyQueryVO) throws DZFWarpException;

    ValuemodifyVO queryById(String id) throws DZFWarpException;

    void update(ValuemodifyVO vo) throws DZFWarpException;

    void updateAVToGLState(String pk_assetvalueChange, boolean istogl, String pk_voucher) throws DZFWarpException;

    void delete(ValuemodifyVO data) throws DZFWarpException;
}

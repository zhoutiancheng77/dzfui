package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.services.am.zcgl.IValuemodifyService;
import com.dzf.zxkj.platform.vo.am.zcgl.ValuemodifyQueryVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@Service
public class ValuemodifyServiceImpl implements IValuemodifyService {
    @Override
    public ValuemodifyVO save(ValuemodifyVO vo) throws DZFWarpException {
        return null;
    }

    @Override
    public List<ValuemodifyVO> query(ValuemodifyQueryVO valuemodifyQueryVO) throws DZFWarpException {
        return null;
    }

    @Override
    public ValuemodifyVO queryById(String id) throws DZFWarpException {
        return null;
    }

    @Override
    public void update(ValuemodifyVO vo) throws DZFWarpException {

    }

    @Override
    public void updateAVToGLState(String pk_assetvalueChange, boolean istogl, String pk_voucher) throws DZFWarpException {

    }

    @Override
    public void delete(ValuemodifyVO data) throws DZFWarpException {

    }
}

package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.RemittanceVO;

import java.util.List;

public interface IRemittanceService {

    RemittanceVO save(RemittanceVO vo) throws DZFWarpException;

    List<RemittanceVO> query(String pk_corp) throws DZFWarpException;

    void delete(RemittanceVO vo) throws DZFWarpException;

    RemittanceVO queryById(String id) throws DZFWarpException;

}

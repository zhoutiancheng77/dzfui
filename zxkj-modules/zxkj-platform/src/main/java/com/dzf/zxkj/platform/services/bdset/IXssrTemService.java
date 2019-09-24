package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.XssrVO;

import java.util.List;

public interface IXssrTemService {

    List<XssrVO> query(String pk_corp) throws DZFWarpException;

    void save(XssrVO vo) throws DZFWarpException;

    void delete(XssrVO vo) throws DZFWarpException;

    XssrVO queryById(String pk) throws DZFWarpException;
}

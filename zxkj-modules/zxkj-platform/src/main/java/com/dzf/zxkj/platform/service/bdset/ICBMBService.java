package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;

import java.util.List;

public interface ICBMBService {

    //保存
    CpcosttransVO save(CpcosttransVO vo) throws DZFWarpException;

    //保存
    List<CpcosttransVO> saveDatas(String pk_corp,CpcosttransVO[] vos) throws DZFWarpException;

    //更新
    void update(CpcosttransVO vo) throws DZFWarpException;

    //查询
    List<CpcosttransVO> query(String pk_corp) throws DZFWarpException;

    //查询
    CpcosttransVO queryById(String id) throws DZFWarpException;

    //删除
    void delete(CpcosttransVO vo) throws DZFWarpException;

}

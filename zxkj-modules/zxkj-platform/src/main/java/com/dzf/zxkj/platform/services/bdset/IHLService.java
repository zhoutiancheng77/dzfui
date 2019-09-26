package com.dzf.zxkj.platform.services.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;

import java.util.List;

public interface IHLService {

    //保存
    ExrateVO save(ExrateVO vo) throws DZFWarpException;

    //更新
    void update(ExrateVO vo) throws DZFWarpException;

    //查询
    List<ExrateVO> query(String pk_corp) throws DZFWarpException;

    //查询
    ExrateVO queryById(String id) throws DZFWarpException;

    //删除
    void delete(ExrateVO vo) throws DZFWarpException;

}

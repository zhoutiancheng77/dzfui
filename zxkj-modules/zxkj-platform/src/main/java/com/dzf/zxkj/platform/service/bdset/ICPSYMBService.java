package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbBVO;
import com.dzf.zxkj.platform.model.bdset.YntCptransmbHVO;

import java.util.HashSet;
import java.util.List;

public interface ICPSYMBService {

    // 保存
    YntCptransmbHVO save(YntCptransmbHVO vo, String pk_corp, YntCptransmbBVO[] bodyvos, String corpid, String date, String userid) throws DZFWarpException;

    // 查询
    List<YntCptransmbHVO> query(String pk_corp) throws DZFWarpException;

    // 删除
    void delete(YntCptransmbHVO vo) throws DZFWarpException;

    YntCptransmbHVO queryById(String id) throws DZFWarpException;

    void exist(String pk_corp, String pk_km, String pk_id, HashSet<String> bodyvos) throws DZFWarpException;

    void update(YntCptransmbHVO vo);

    // 查询子表
    List<YntCptransmbBVO> queryChildsById(String id, String pk_corp);

}

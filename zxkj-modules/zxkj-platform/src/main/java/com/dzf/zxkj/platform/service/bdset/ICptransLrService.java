package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCptranslrHVO;

import java.util.List;

public interface ICptransLrService {
    // 保存
    YntCptranslrHVO save(YntCptranslrHVO vo) throws DZFWarpException;

    // 查询
    List<YntCptranslrHVO> query(String pk_corp, boolean isgroup) throws DZFWarpException;

    // 删除
    void delete(YntCptranslrHVO vo) throws DZFWarpException;

    YntCptranslrHVO queryById(String id) throws DZFWarpException;

}

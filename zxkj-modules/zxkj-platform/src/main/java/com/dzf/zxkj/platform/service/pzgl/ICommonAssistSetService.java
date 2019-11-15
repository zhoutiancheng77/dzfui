package com.dzf.zxkj.platform.service.pzgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.CommonAssistVO;

import java.util.List;


public interface ICommonAssistSetService {
    List<CommonAssistVO> query(String pk_corp, boolean hasAssistData) throws DZFWarpException;

    CommonAssistVO save(CommonAssistVO vo) throws DZFWarpException;

    void delete(CommonAssistVO vo) throws DZFWarpException;

    boolean checkExist(CommonAssistVO vo) throws DZFWarpException;
}

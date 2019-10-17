package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;

import java.util.List;

public interface IPzmbbService {

    //保存
    PzmbbVO save(PzmbbVO vo) throws BusinessException;

    //修改
    void update(PzmbbVO vo);

    //查询
    List<PzmbbVO> query();

    //查询
    List<PzmbbVO> queryByPId(String PId);

    //删除
    void delete(PzmbbVO vo) throws BusinessException;

}

package com.dzf.zxkj.platform.services.common;

import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.entity.ConditionVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IBgPubService {
    //分页查询
    <T> List<T> queryInfovo(Class className, String tableName, String condition,
                            SQLParameter params, int pageNo, int pageSize, String order) throws DZFWarpException;

    //不分页查询
    <T> List<T> queryInfovo(Class className, CorpVO corpVo,
                            UserVO uservo, String sort, String order) throws DZFWarpException;

    //条件查询
    <T> List<T> queryWithCondtion(Class className, ConditionVO[] cd, String sort, String order) throws DZFWarpException;

    //删除
    void deleteInfovo(SuperVO bean) throws DZFWarpException;

    //删除
    void deleteInfovoDzf(SuperVO bean) throws DZFWarpException;

    //获取总行数
    int getTotalRow(String tablename, String condition, SQLParameter sp) throws DZFWarpException;

    //新增保存
    <T> T saveNew(SuperVO vo) throws DZFWarpException;

    //更新
    void update(SuperVO vo) throws DZFWarpException;

    //更新
    void updateDzf(SuperVO vo) throws DZFWarpException;

    //按字段更新
    void updateByColumn(SuperVO vo, String[] columns) throws DZFWarpException;

    /**
     * 根据公司查询
     *
     * @param pk_corp
     * @return
     */
    <T> List<T> queryByPkcorp(Class className, String pk_corp) throws DZFWarpException;


}

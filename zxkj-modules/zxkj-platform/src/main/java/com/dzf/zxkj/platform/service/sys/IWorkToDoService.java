package com.dzf.zxkj.platform.service.sys;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgVo;
import com.dzf.zxkj.platform.model.sys.CorpRoleVO;
import com.dzf.zxkj.platform.model.sys.WorkToDoVo;

import java.util.List;

/**
 * 工作代办事项
 *
 * @author zhangj
 */
public interface IWorkToDoService {

    public List<QmGzBgVo> queryTodo(String pk_corp, String qj, String opetorid) throws DZFWarpException;

    /**
     * 保存代办事项
     *
     * @param todovo
     * @throws DZFWarpException
     */
    public void saveWorkTodo(WorkToDoVo todovo) throws DZFWarpException;


    /**
     * 处理
     *
     * @param id
     * @throws DZFWarpException
     */
    public void updateHandToDo(String id) throws DZFWarpException;


    /**
     * 删除代办事项
     *
     * @param id
     * @throws DZFWarpException
     */
    public void delteTodo(String id) throws DZFWarpException;


    /**
     * 有权限的用户
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public List<CorpRoleVO> getPowUvos(String pk_corp) throws DZFWarpException;

}

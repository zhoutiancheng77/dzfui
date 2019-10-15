package com.dzf.zxkj.platform.service.sys;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpRoleVO;
import com.dzf.zxkj.platform.model.sys.UserRoleVO;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的客户---权限分配
 * @author gejw
 * @time 2018年5月8日 下午8:58:49
 *
 */
public interface ICorpURoleService {
    /**
     * 权限分配：保存
     * @throws DZFWarpException
     */
    public void saveRoleUser(String loginCorp, CorpRoleVO[] listR) throws DZFWarpException;

    /**
     * 查询选中客户的权限信息
     * @param loginCorp
     * @param corpids
     * @throws DZFWarpException
     */
    public ArrayList<CorpRoleVO> queryUserPower(String loginCorp, String[] corpids) throws DZFWarpException;

    /**
     * 删除用户-角色-客户权限
     * @throws DZFWarpException
     */
    public void delRoleUser(CorpRoleVO paramvo) throws DZFWarpException;

    /**
     * 转派
     * @param crvo
     * @throws DZFWarpException
     */
    public void updateRoleUser(String loginCorp, CorpRoleVO crvo) throws DZFWarpException;

    /**
     * 分配主办会计角色后，更新客户档案主办会计信息
     * @author gejw
     * @time 上午9:27:27
     * @throws DZFWarpException
     */
    public void updateCorpUser(String loginCorp, UserRoleVO[] listR) throws DZFWarpException;
    
    /**
     * 查询指定用户拥有指定公司角色
     * @author gejw
     * @time 下午5:22:33
     * @param pk
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public List<UserRoleVO> queryUserRoleVO(String pk, String pk_corp) throws DZFWarpException;
    
    
}


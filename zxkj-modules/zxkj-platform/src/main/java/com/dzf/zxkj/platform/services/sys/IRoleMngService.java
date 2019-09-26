package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.*;

import java.util.List;

public interface IRoleMngService {

	public void delete(RoleVO vo, UserVO userVo) throws DZFWarpException;

	public RoleVO save(RoleVO vo) throws DZFWarpException;

	public void update(RoleVO vo, UserVO userVo)throws DZFWarpException;

	public boolean exist(RoleVO vo) throws DZFWarpException;

	public SysFunNodeVO[] queryFunNode(String role_id) throws DZFWarpException;

	public void sign(String pk_corp, RoleVO rolevo) throws DZFWarpException;

	public List<RoleVO> query(SysPowerConditVO conditvo) throws DZFWarpException;

	public SysFunNodeVO[] queryFunNode(String role_id, int roletype, CorpVO cvo) throws DZFWarpException;

	public List<RoleVO> queryPowerRole(SysPowerConditVO conditvo) throws DZFWarpException;
	/**
	 * 取关键角色，供批量建账时分配角色使用
	 * @return
	 * @throws DZFWarpException
	 */
	public RoleVO[] queryKeyRole() throws DZFWarpException;

	/**
	 * 根据角色编码查询
	 */
	public RoleVO queryRoleBycode(String rolecode) throws DZFWarpException;

	public RoleVO queryRoleByID(String pk_role) throws DZFWarpException;

	public RoleVO[] queryByCondition(String condition) throws DZFWarpException;

	/**
	 * 会计工厂角色权限调用
	 * @param roleCorp 角色所属的公司
	 * @param userCreateCorp 当前操作用户的创建公司
	 * @return
	 * @throws DZFWarpException
	 */
	public SysFunNodeVO[] queryFunNode(String role_id, int roletype, String roleCorp, String userCreateCorp) throws DZFWarpException;

	/**
	 * 角色封存-解封
	 * */
	public void updateSeal(RoleVO vo, UserVO userVo)throws DZFWarpException;
}

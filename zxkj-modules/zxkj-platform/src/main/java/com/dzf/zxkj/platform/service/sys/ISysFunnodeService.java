package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.SysFunNodeVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.ArrayList;
import java.util.List;



/**
 * 权限节点
 *
 */
public interface ISysFunnodeService {
	
	public SysFunNodeVO[] querySysNode(String pk_parent) throws DZFWarpException;
	
	public SysFunNodeVO save(SysFunNodeVO vo) throws DZFWarpException;
	
	public void delete(SysFunNodeVO vo) throws DZFWarpException;
	
	public SysFunNodeVO queryVOByID(String pk_funnode) throws DZFWarpException;

	public SysFunNodeVO[] querySysnodeByUserId(String userId) throws DZFWarpException;
	
	public List<SysFunNodeVO> querySysnodeByUserAndCorp(UserVO user, CorpVO corp, String path) throws DZFWarpException;
	
	public List<SysFunNodeVO> querySysnodeByUser(UserVO user, String path) throws DZFWarpException;
	
	public List<SysFunNodeVO> querySysnodeByUser1(UserVO user, String path, String pk_corp) throws DZFWarpException;
	
	/**
	 * 财税服务平台权限节点
	 * @author gejw
	 * @time 上午9:46:30
	 * @param user
	 * @param path
	 * @return
	 * @throws DZFWarpException
	 */
	public ArrayList<SysFunNodeVO> queryFatSysnodeByUser(UserVO user, String path) throws DZFWarpException;
}
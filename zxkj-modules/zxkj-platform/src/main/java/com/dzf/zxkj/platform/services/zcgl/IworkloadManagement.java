package com.dzf.zxkj.platform.services.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zcgl.WorkloadManagementVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

import java.util.List;

public interface IworkloadManagement {
	/**
	 * 查询工作量法数据
	 * @param paramvo
	 * @return
	 * @throws BusinessException
	 */
	public List<WorkloadManagementVO> queryWorkloadManagement(QueryParamVO paramvo) throws DZFWarpException;

	List<WorkloadManagementVO> queryBypk_assetcard(QueryParamVO paramvo)throws DZFWarpException;

	public void save(WorkloadManagementVO data)throws DZFWarpException;
}

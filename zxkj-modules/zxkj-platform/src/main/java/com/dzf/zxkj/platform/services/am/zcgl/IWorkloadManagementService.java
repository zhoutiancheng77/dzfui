package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.WorkloadManagementVO;
import com.dzf.zxkj.platform.vo.am.zcgl.WorkloadManagementQueryVo;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IWorkloadManagementService {
    List<WorkloadManagementVO> queryWorkloadManagement(WorkloadManagementQueryVo workloadManagementQueryVo) throws DZFWarpException;
    List<WorkloadManagementVO> queryBypk_assetcard(WorkloadManagementQueryVo workloadManagementQueryVo)throws DZFWarpException;
    void save(WorkloadManagementVO data)throws DZFWarpException;
}

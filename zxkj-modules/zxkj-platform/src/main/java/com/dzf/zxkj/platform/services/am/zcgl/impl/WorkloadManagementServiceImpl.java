package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.WorkloadManagementVO;
import com.dzf.zxkj.platform.services.am.zcgl.IWorkloadManagementService;
import com.dzf.zxkj.platform.vo.am.zcgl.WorkloadManagementQueryVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@Service
public class WorkloadManagementServiceImpl implements IWorkloadManagementService {
    @Override
    public List<WorkloadManagementVO> queryWorkloadManagement(WorkloadManagementQueryVo workloadManagementQueryVo) throws DZFWarpException {
        return null;
    }

    @Override
    public List<WorkloadManagementVO> queryBypk_assetcard(WorkloadManagementQueryVo workloadManagementQueryVo) throws DZFWarpException {
        return null;
    }

    @Override
    public void save(WorkloadManagementVO data) throws DZFWarpException {

    }
}

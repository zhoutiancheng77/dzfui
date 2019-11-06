package com.dzf.zxkj.platform.service.zcgl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zcgl.ZcdzVO;

public interface IZczzdzReportService {
    /**
     *  资产总账对账查询
     * @param pk_corp
     * @param period
     * @return
     * @throws DZFWarpException
     */
    ZcdzVO[] queryAssetCheckVOs(String pk_corp, String period) throws DZFWarpException;
}

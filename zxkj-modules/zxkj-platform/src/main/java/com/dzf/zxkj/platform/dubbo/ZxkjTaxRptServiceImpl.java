package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.IZxkjTaxRptService;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellBVO;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellVO;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptCalCellService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjTaxRptServiceImpl implements IZxkjTaxRptService {

    @Autowired
    private ITaxRptCalCellService taxRptCalCellService;

    @Override
    public TaxRptCalCellVO getTaxRptCalCell(TaxRptCalCellBVO[] params) {
        try {
            return taxRptCalCellService.getTaxRptCalCell(params);
        } catch (DZFWarpException e) {
            log.error(String.format("调用getTaxRptCalCell异常,异常信息:%s", e.getMessage()), e);
            return null;
        }
    }
}

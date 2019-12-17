package com.dzf.zxkj.platform;

import com.dzf.zxkj.platform.taxrpt.model.TaxRptCalCellBVO;
import com.dzf.zxkj.platform.taxrpt.model.TaxRptCalCellVO;

public interface IZxkjTaxRptService {

    TaxRptCalCellVO getTaxRptCalCell(TaxRptCalCellBVO[] params);
}

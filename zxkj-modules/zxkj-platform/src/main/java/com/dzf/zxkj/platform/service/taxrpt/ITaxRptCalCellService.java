package com.dzf.zxkj.platform.service.taxrpt;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.tax.workbench.TaxDeclareResult;
import com.dzf.zxkj.platform.taxrpt.model.TaxRptCalCellBVO;
import com.dzf.zxkj.platform.taxrpt.model.TaxRptCalCellVO;

import java.util.List;

public interface ITaxRptCalCellService {

	public TaxRptCalCellVO getTaxRptCalCell(TaxRptCalCellBVO[] params)
			throws DZFWarpException;

	public List<TaxDeclareResult> zeroDeclaration(List<String> corps, List<String> zsxm_dms,
												  String period, String userid) throws DZFWarpException;
}

package com.dzf.zxkj.platform.model.taxrpt.shandong;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategyByID;
import com.dzf.zxkj.platform.model.tax.TaxPosContrastVO;

public class TaxVOTreeStrategy extends AccTreeCreateStrategyByID {

	private TaxPosContrastVO root = null;

	@Override
	public Object getNodeId(Object obj) {
		return ((TaxPosContrastVO) obj).getPrimaryKey();
	}

	@Override
	public Object getParentNodeId(Object obj) {
		return ((TaxPosContrastVO) obj).getPk_parent();
	}

	@Override
	public SuperVO getRootVO() {
		if (root == null)
			root = new TaxPosContrastVO();
		return root;
	}

}

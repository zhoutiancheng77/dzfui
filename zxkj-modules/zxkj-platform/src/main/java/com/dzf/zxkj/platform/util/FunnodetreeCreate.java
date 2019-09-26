package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.tree.AccTreeCreateStrategyByID;
import com.dzf.zxkj.platform.model.sys.SysFunNodeVO;

public class FunnodetreeCreate extends AccTreeCreateStrategyByID {

	private SysFunNodeVO root;

	@Override
	public SuperVO getRootVO() {
		if(root==null){
			root = new SysFunNodeVO();
		}
		return root;
	}

	@Override
	public Object getNodeId(Object obj) {
		return ((SysFunNodeVO)obj).getPrimaryKey();
	}

	@Override
	public Object getParentNodeId(Object obj) {
		return ((SysFunNodeVO)obj).getPk_parent();
	}
	
	
}

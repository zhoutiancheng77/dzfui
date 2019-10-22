package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategyByPk;
import com.dzf.zxkj.common.tree.INodeFilter;

public class KmConFzVoTreeStrateGyByPk extends AccTreeCreateStrategyByPk {

	

	public KmConFzVoTreeStrateGyByPk(DZFBoolean isshowfs, DZFBoolean isxswyewfs, DZFBoolean isxswyewfs_bn) {
		super();
	}

	@Override
	public Object getNodeId(Object arg0) {
		FseJyeVO vo = (FseJyeVO) arg0;
		return vo.getPk_km();
	}

	@Override
	public Object getParentNodeId(Object arg0) {
		FseJyeVO vo = (FseJyeVO) arg0;
		return vo.getPk_km_parent();
	}

	@Override
	public SuperVO getRootVO() {
		return new FseJyeVO();
	}

	@Override
	public INodeFilter getNodeFileter() {
		return new INodeFilter() {
			@Override
			public boolean dontInsert(Object arg0) {
				FseJyeVO fsjye = (FseJyeVO) arg0;
				return false;
			}
		};
	}

}

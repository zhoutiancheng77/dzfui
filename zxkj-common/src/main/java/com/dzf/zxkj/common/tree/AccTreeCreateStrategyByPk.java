package com.dzf.zxkj.common.tree;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 通过id获取
 *
 */
public abstract class AccTreeCreateStrategyByPk implements ITreeCreateStrategy {

	private SuperVO rootvo;

	public AccTreeCreateStrategyByPk() {
		super();
	}

	public abstract SuperVO getRootVO();

	public SuperVO getRootNode() {
		if (rootvo == null) {
			rootvo = getRootVO();
		}
		return rootvo;
	}

	public int getInsertType() {
		return WHEN_LOSE_PARENT_INSERT_TO_ROOT;
	}

	public boolean isCodeTree() {
		return false;
	}

	public SuperVO createTreeNode(Object userObj) {
		return (SuperVO) userObj;
	}

	public String getCircularRule() {
		return null;
	}

	public SuperVO createDefaultTreeNodeForLoseNode(Object codeValue) {
		return null;
	}

	public SuperVO getOtherTreeNode() {
		return null;
	}


	@Override
	public String getCodeRule() {
		return null;
	}

	@Override
	public String getCodeValue(Object userObj) {
		return null;
	}

}

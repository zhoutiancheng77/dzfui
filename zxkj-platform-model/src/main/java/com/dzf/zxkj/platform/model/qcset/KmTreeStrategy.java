package com.dzf.zxkj.platform.model.qcset;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategy;

/**
 * 科目编码树编码规则
 *
 */
public class KmTreeStrategy extends AccTreeCreateStrategy {
	

	public KmTreeStrategy(String codeRule) {
		super(codeRule);
	}

	@Override
	public SuperVO getRootVO() {
		return new QcYeVO();
	}

	@Override
	public String getCodeValue(Object userObj) {
		return ((QcYeVO)userObj).getVcode();
	}

}

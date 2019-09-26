package com.dzf.zxkj.platform.model.bdset;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.tree.AccTreeCreateStrategy;

/**
 * 科目编码树编码规则
 *
 */
public class AccountTreeStrategy extends AccTreeCreateStrategy {
	

	public AccountTreeStrategy(String codeRule) {
		super(codeRule);
	}

	@Override
	public SuperVO getRootVO() {
		return new YntCpaccountVO();
	}

	@Override
	public String getCodeValue(Object userObj) {
		return ((YntCpaccountVO)userObj).getAccountcode();
	}

}

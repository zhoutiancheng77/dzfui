package com.dzf.zxkj.report.tree;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategy;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;

/**
 * 辅助明细（科目）votree
 * @author zhangj
 *
 */
public class KmmxFzVoTreeStrategy extends AccTreeCreateStrategy {

	public KmmxFzVoTreeStrategy(String codeRule) {
		super(codeRule);
	}

	@Override
	public String getCodeValue(Object arg0) {
		return ((FzKmmxVO)arg0).getKmcode() ;
	}

	@Override
	public SuperVO getRootVO() {
		return new FzKmmxVO();
	}

}

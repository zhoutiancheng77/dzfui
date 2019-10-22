package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategy;

/**
 * 辅助明细（科目）votree
 * @author zhangj
 *
 */
public class KmConFzVoTreeStrategy extends AccTreeCreateStrategy {

	public KmConFzVoTreeStrategy(String codeRule) {
		super(codeRule);
	}

	@Override
	public String getCodeValue(Object arg0) {
		return ((KmmxConFzMxVO)arg0).getCode() ;
	}

	@Override
	public SuperVO getRootVO() {
		return new KmmxConFzMxVO();
	}

}

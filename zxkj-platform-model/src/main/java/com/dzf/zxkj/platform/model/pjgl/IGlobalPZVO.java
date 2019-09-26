package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 生成凭证获取业务类型模板字段
 * @author wangzhn
 *
 */
public interface IGlobalPZVO {

	public DZFDouble getTotalmny();//总金额
	
	public DZFDouble getMny();//金额
	
	public DZFDouble getWsmny();//无税金额
	
	public DZFDouble getSmny();//税额
}

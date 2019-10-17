package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * 凭证模板
 * @author zhangj
 *
 */
public interface IVoucherTemplate {


	/**
	 * 根据vo查询对应的模板，如果公司没，则查询集团级的模板
	 * @param classname
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public SuperVO[] queryTempateByName(String classname, String pk_corp) throws DZFWarpException;
	
}

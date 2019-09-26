package com.dzf.zxkj.platform.services.bdset;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;

/**
 * 工资表设置
 * @author zhangj
 *
 */
public interface IPersonalSetService {

//	public void save(GxhszVO vo, String pk_corp) throws DZFWarpException;

	public GxhszVO query(String pk_corp) throws DZFWarpException;
}

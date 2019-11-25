package com.dzf.zxkj.platform.service.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IOperatorType {
	
	/**
	 * 不同公司在操作日志里面，显示的操作类型不一样
	 * @param cpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<LogRecordEnum> getLogEnum(CorpVO cpvo) throws DZFWarpException;

	
	/**
	 * 当前公司存在多少用户
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<UserVO> getListUservo(String pk_corp) throws DZFWarpException;
	
}

package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.jzcl.AccountQryVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IAccountQryService {
	
	/**
	 * 查询数据条数
	 * @param pamvo
	 * @param uvo
	 * @param ischannel
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer queryTotalRow(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException;

	/**
	 * 查询数据
	 * @param pamvo
	 * @param uvo
	 * @param ischannel
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AccountQryVO> query(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException;
	
	/**
	 * 查询所有数据
	 * @param paramvo
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AccountQryVO> queryAllData(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException;

    public List<AccountQryVO> queryYjxx(List<AccountQryVO> accountQryVOList, String period) throws DZFWarpException;
    
    public List<AccountQryVO> queryYjxxByMulti(List<AccountQryVO> accountQryVOList, String period) throws DZFWarpException;
}

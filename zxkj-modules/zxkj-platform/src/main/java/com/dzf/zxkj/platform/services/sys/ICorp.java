package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface ICorp {

	/**
	 * 用VO对象的属性值更新数据库。
	 *
	 * 创建日期：(2001-5-16)
	 * @param corp nc.vo.bd.CorpVO
	 * @throws DZFWarpException 
	 */
	public abstract void cancelCorp(CorpVO corp) throws DZFWarpException;

	/**
	 * 根据主键在数据库中删除一个VO对象。
	 *
	 * 创建日期：(2001-5-16)
	 * @param key String
	 * @throws DZFWarpException
	 */
	public abstract int delete(CorpVO vo) throws DZFWarpException;

	/**
	 * 向数据库插入一个VO对象。
	 *
	 * 创建日期：(2001-5-16)
	 * @param node nc.vo.bd.CorpVO
	 * @throws DZFWarpException
	 */
	public abstract String insert(CorpVO corp) throws DZFWarpException;

	/**
	 * 增加公司和插入公司的第一个管理员
	 * 创建日期：(01-6-12 11:29:35)
	 * @param corpVO 公司信息
	 * @param accountMsgInfo 公司管理员信息
	 * 修改：李充蒲
	 * 2002.7.3 licp modify lockpk invoke
	 * @throws DZFWarpException
	 */
	public abstract String insertCorp(CorpVO corp, String[] accountMsgInfo)
			throws DZFWarpException;
	

	/**
	 * 插入数据，并且返回插入后的数据库数据（主要是有ts）
	 * @param corp
	 * @param accountMsgInfo
	 * @return
	 * @throws DZFWarpException
	 */
	public abstract CorpVO insertCorpReturnSelf(CorpVO corp, String[] accountMsgInfo)
	throws DZFWarpException;
	
	/**
	 * 建帐时增加默认公司和插入公司的第一个管理员
	 * 创建日期：(01-6-12 11:29:35)
	 * @param corpVO 公司信息
	 * @param accountMsgInfo 公司管理员信息
	 * 修改：李充蒲
	 * @throws DZFWarpException
	 */
	public abstract String insertDefaultCorp(String dsName, CorpVO corp,
                                             String[] accountMsgInfo) throws DZFWarpException;

	/**
	 * 用VO对象的属性值更新数据库。
	 *
	 * 创建日期：(2001-5-16)
	 * @param corp nc.vo.bd.CorpVO
	 * @throws DZFWarpException
	 */
	public abstract String update(CorpVO corp) throws DZFWarpException;
	
	/**
	 * 更新数据并且返回更新后的数据
	 * @param corp
	 * @return
	 * @throws DZFWarpException
	 */
	public abstract CorpVO updateReturnSelf(CorpVO corp) throws DZFWarpException;

	/**
	 * 根据OID更新字段isHasAccount的值
	 *
	 * 创建日期：(2001-5-16)
	 * @param corp nc.vo.bd.CorpVO
	 * @throws DZFWarpException
	 */
	public abstract void updateAccountFlag(String pkCorp, boolean bHasAccount)
			throws DZFWarpException;
	
	
	/**
	 * 如果是会计公司，同时更新到会计公司表
	 * @author gjw
	 * @param corpVO
	 * @throws DZFWarpException
	 */
	public abstract void updateAccountVO(CorpVO corpVO) throws DZFWarpException;
	
	/**
	 * 如果是会计公司，同时增加到会计公司表
	 * @author gjw
	 * @param corpVO
	 * @throws DZFWarpException
	 */
	public abstract void insertAccountVO(CorpVO corpVO) throws DZFWarpException;
	
	public CorpVO findCorpVOByPK(String key) throws DZFWarpException;
	
	public CorpVO queryCorpByName(String unitname)throws DZFWarpException;
	

}

package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.jzcl.QmJzVO;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;

/**
 * 期末结账接口
 *
 * @author zhangj
 */
public interface ITerminalSettle {
	
	/**
	 * 结账检查
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public QmJzVO[] updatecheckTerminalSettleData(QmJzVO[] vos) throws DZFWarpException;

	
	/**
	 * 结账，保存期末结账、并存入期末、期初数
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public QmJzVO[] saveTerminalSettleData(QmJzVO[] vos, DZFDate logdate, String userid) throws DZFWarpException ;

	/**
	 * 结账，保存期末结账、并存入期末、期初数(从凭证开始,支持多币种)
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public QmJzVO[] saveTerminalSettleDataFromPZ(QmJzVO[] vos, DZFDate logdate, String userid) throws DZFWarpException ;


	/**
	 * 反结账，删除期末结账、并存入期末、期初数
	 * @param vos
	 * @return
	 * @throws BusinessException
	 */
	public QmJzVO[] updatecancelTerminalSettleData(QmJzVO[] vos) throws DZFWarpException ;

	/**
	 * 根据查询条件获取对应的期末记账数据
	 * @return
	 * @throws BusinessException
	 */
	public QmJzVO[] initQueryQmJzVO(QueryParamVO queryvo)throws DZFWarpException;

	public boolean isExistsJZ(String pk_corp, DZFDate ufd) throws DZFWarpException;
	/**
	 * 利润结转
	 * @param pk_corp
	 * @param period
	 * @throws BusinessException
	 */
	public void updateProfitJz(String pk_corp, DZFDate period, QmJzVO vo, DZFBoolean b, String userid)throws DZFWarpException;

	/**
	 * 反利润结转
	 * @param pk_corp
	 * @param period
	 * @throws BusinessException
	 */
	public void updateFanLiRunJz(String pk_corp, QmJzVO vo, DZFDate period)throws DZFWarpException;

	/**
	 * 检查是否年结
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	boolean checkIsYearClose(String pk_corp, String period) throws DZFWarpException;
}

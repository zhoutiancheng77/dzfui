package com.dzf.zxkj.platform.services.icset;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.icset.AggIcTradeVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.IntradeParamVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.io.File;
import java.util.List;

public interface ISaleoutService {
	
	/**
	 * 查询所有数据
	 * @param paramvo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<IntradeHVO> query(IntradeParamVO paramvo) throws DZFWarpException;
	
	public String getNewBillNo(String pk_corp, DZFDate dbilldate, String prefix) throws DZFWarpException;

	public IntradeHVO saveSale(IntradeHVO headvo, boolean flag, boolean isImpl) throws DZFWarpException;

	public List<IntradeoutVO> querySub(IntradeHVO vo) throws DZFWarpException;

	public void deleteSale(IntradeHVO vo, String pk_corp) throws DZFWarpException;

	public void saveToGL(IntradeHVO vo, CorpVO corpvo, String userid, String zy) throws DZFWarpException;

	public void saveDashBack(IntradeHVO vo, CorpVO corpvo, String userid, String login_date) throws DZFWarpException;

	public void rollbackTogl(IntradeHVO vo, String pk_corp) throws DZFWarpException;

	public void deleteIntradeoutBill(TzpzHVO pzHeadVO) throws DZFWarpException;

	public IntradeHVO queryIntradeHVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException;

	public void saveToGL(IntradeHVO[] vos, String pk_corp, String userid, String zy) throws DZFWarpException;

	public AggIcTradeVO[] queryAggIntradeVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException ;

	public String saveImp(File file, String pk_corp, String fileType, String cuserid) throws DZFWarpException;
	
	public StringBuffer buildQmjzMsg(List<String> periodList, String pk_corp) throws DZFWarpException;

}

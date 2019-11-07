package com.dzf.zxkj.platform.service.icbill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.icset.AggIcTradeVO;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.IntradeParamVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IPurchInService {

	public List<IntradeHVO> query(IntradeParamVO paramvo) throws DZFWarpException;

	public IntradeHVO save(IntradeHVO vo, boolean isImpl) throws DZFWarpException;

	public void delete(IntradeHVO data, String pk_corp) throws DZFWarpException;

	public IctradeinVO[] queryIctradeinVO(String pk_ictrade_h, String pk_corp) throws DZFWarpException;

	public IntradeHVO queryIntradeHVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException;

	public IntradeHVO saveIntradeHVOToZz(IntradeHVO data, CorpVO corpvo) throws DZFWarpException;

	public void rollbackIntradeHVOToZz(IntradeHVO data, CorpVO corpvo) throws DZFWarpException;;

	// public void check(IntradeHVO data, String pk_corp) throws
	// DZFWarpException;

	public String getNewBillNo(String pk_corp, DZFDate billdate, String prefix) throws DZFWarpException;

	public void deleteIntradeBill(TzpzHVO pzHeadVO) throws DZFWarpException;

	// 批量生成一张凭证
	public Object saveIntradeHVOToZz(IntradeHVO[] datas, CorpVO corpvo) throws DZFWarpException;

	public AggIcTradeVO[] queryAggIntradeVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException ;

	public String saveImp(MultipartFile file, String pk_corp, String fileType, String cuserid) throws DZFWarpException;

	// 查询暂估数量
	public List<TempInvtoryVO> queryZgVOs(CorpVO corpvo, String userid, DZFDate doped) throws DZFWarpException;

	// 保存暂估单
	public void saveZg(TempInvtoryVO[] bodyvos, CorpVO corpvo, String userid, String pk_zggys) throws DZFWarpException;

	public void check(IntradeHVO hvo, String pk_corp, boolean iscopy, boolean istogl) throws DZFWarpException;

	StringBuffer buildQmjzMsg(List<String> periodList, String pk_corp) throws DZFWarpException;
}

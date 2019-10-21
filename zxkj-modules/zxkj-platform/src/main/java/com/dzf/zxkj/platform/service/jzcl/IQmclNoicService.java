package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;


/**
 * 不启用库存
 * 
 * 结转接口类
 *
 */
public interface IQmclNoicService {

	public QmclVO queryById(String pk_qmcl)throws DZFWarpException;
	
	public QmclVO jzfuzhusccb(QmclVO qmvo, List<CostForwardVO> listz, String userid, String cbjzCount)throws DZFWarpException;
	
	//完工入库商品，不启用库存
	public QmclVO saveWgVoucherNoic(String userid, Map<QmclVO, List<CostForwardInfo>> map, String jztype, String cbjzCount)throws DZFWarpException;

	public List<QMJzsmNoICVO> queryCBJZAccountVOS(String pk_gs, String userid, String jztype)
			throws DZFWarpException;
	
	public List<QMJzsmNoICVO> queryCBJZqcpzAccountVOS(String pk_gs,String userid,String begindate,String enddate,String[] kmbms,String jztype)
			throws DZFWarpException;

	//生成销售结转凭证
	public QmclVO saveToSalejzVoucher(String userid,
			Map<QmclVO, List<QMJzsmNoICVO>> map,String jztype1,String cbjzCount, String xjxcf) throws DZFWarpException;


	public QmclVO rollbackCbjzNoic(QmclVO vos, String cbjzCount)
			throws DZFWarpException;

	public QmclVO queryIsXjxcf(String pk_gs, String userid) throws DZFWarpException;
	

	public List<CostForwardInfo> queryCBJZAccountVOSwg(String pk_gs,
			String userid, String jztype)throws DZFWarpException;


	public void judgeLastPeriod(String pk_gs, String userid, String qj,String costype) throws DZFWarpException;
	
	
	//取成本模板校验
	public void checkCbjzmb(String pk_gs, String costype) throws DZFWarpException;
	
	public List<TempInvtoryVO> getZgDataByCBB(QmclVO qmclvo, List<QMJzsmNoICVO> list, CorpVO corpVo, int jztype, String cbjzCount, DZFBoolean isxjxcf, String userid,
											  Map<String, YntCpaccountVO> kmsmap) throws DZFWarpException;
	
}

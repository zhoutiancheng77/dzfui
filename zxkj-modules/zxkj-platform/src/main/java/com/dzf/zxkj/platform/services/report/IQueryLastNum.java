package com.dzf.zxkj.platform.services.report;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;

import java.util.List;
import java.util.Map;

public interface IQueryLastNum {
	
	
	public Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn)throws DZFWarpException;

	public List<IcbalanceVO> queryLastBanlanceVOs_byList1(String currentenddate, String pk_corp, String pk_invtory, String pk_invclass, boolean isafternonn)throws  DZFWarpException;

	public Map<String, QcYeVO> queryLastBanlanceVOs_byMap1NoIC(
            String startDate, String pk_corp, String pk_invtory, boolean b)throws  DZFWarpException;

	// 取本月的销售成本        期初+截止本月月末的入库-截止上月月末的出库  （用于月末一次加权平均结转）
	public List<IcbalanceVO> queryLastBanlanceVOs_byList3(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn)throws  DZFWarpException;

	public Map<String,IcbalanceVO> queryLastBanlanceVOs_byMap3(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn)throws  DZFWarpException;

	public Map<String,IcbalanceVO> queryLastBanlanceVOs_byMap4(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn)throws  DZFWarpException;
}

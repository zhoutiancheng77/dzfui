package com.dzf.zxkj.platform.service.gzgl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;


public interface ISalaryReportService {

	SalaryReportVO[] query(String pk_corp, String qj, String billtype) throws DZFWarpException;
	
	SalaryReportVO[] queryAllType(String pk_corp, String qj) throws DZFWarpException;
	
	QueryPageVO queryBodysBypage(String pk_corp, String qj, String billtype, int page, int rows);

	Object[] saveImpExcel(String loginDate, String cuserid, CorpVO loginCorpInfo, SalaryReportVO[] vos,
                          String opdate, int type, String billtype) throws DZFWarpException;

	SalaryReportVO[] saveCopyByMonth(String pk_corp, String copyFromdate, String copyTodate, String cuserid,
                                     String billtype) throws DZFWarpException;

	TzpzHVO saveToVoucher(CorpVO corpVO,String qj, String cuserid, String str) throws DZFWarpException;

	SalaryReportVO[] save(String pk_corp, List<SalaryReportVO> list, String qj, String cuserid, String billtype)
			throws DZFWarpException;

	SalaryReportVO[] delete(String pk_corp, String pk, String qj2) throws DZFWarpException;

	DZFBoolean queryIsGZ(String pk_corp, String qj) throws DZFWarpException;

	DZFBoolean isGZ(String pk_corp, String qj, String isgz) throws DZFWarpException;

	String judgeHasPZ(String pk_corp, String qj) throws DZFWarpException;

	void checkPz(String pk_corp, String qj, String str, boolean ischeckdata) throws DZFWarpException;

	SalaryAccSetVO getSASVO(String pk_corp, String billtype) throws BusinessException;

	void updateFykm(String pk_corp, String fykmid, String pk, String qj, String billtype) throws BusinessException;

	SalaryReportVO[] getSalarySetInfo(String pk_corp, String billtype, String cpersonids, String qj) throws BusinessException;

	void updateDeptid(String pk_corp, String deptid, String pk, String qj, String billtype) throws BusinessException;

	Object[] saveImpExcelForTax(String loginDate, String cuserid, CorpVO loginCorpInfo, SalaryReportVO[] vos,
                                String opdate, int type, String billtype) throws DZFWarpException;

	public Map<String, List<String>> queryAllTypeBeforeCurr(String pk_corp, String qj) throws DZFWarpException;

	public SalaryReportVO[] calLjData(String pk_corp, String cpersonids, String billtype, String opdate)
			throws BusinessException;
	
	void calGzb(String pk_corp, String[] pids, String qj, String billtype) throws BusinessException;

}

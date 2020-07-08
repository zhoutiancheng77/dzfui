package com.dzf.zxkj.platform.service.gzgl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryKmDeptVO;

public interface ISalaryAccSetService {

	SalaryAccSetVO query(String pk_corp) throws DZFWarpException;

	SalaryAccSetVO queryTable(String pk_corp) throws DZFWarpException;

	SalaryAccSetVO save(SalaryAccSetVO vo1) throws DZFWarpException;

	SalaryAccSetVO queryGroupVO(String pk_corp, boolean isbuild) throws DZFWarpException;

	SalaryAccSetVO saveGroupVO(String pk_corp, boolean isbuild) throws DZFWarpException;

	SalaryAccSetVO saveVoByColumn(String pk_corp, SalaryAccSetVO vo1, String[] strs) throws DZFWarpException;
	
	
	SalaryKmDeptVO[] saveFykm(String pk_corp, SalaryKmDeptVO[] vos) throws DZFWarpException;
	
	SalaryKmDeptVO[] queryFykm(String pk_corp) throws DZFWarpException;

}

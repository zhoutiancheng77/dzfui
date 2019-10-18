package com.dzf.zxkj.platform.service.gzgl;


import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.vo.QueryPageVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryBaseVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;

import java.util.List;
import java.util.Map;

public interface ISalaryBaseService {

	SalaryBaseVO[] query(String pk_corp, String qj) throws DZFWarpException;
	
	QueryPageVO queryBodysBypage(String pk_corp, String qj, int page, int rows)throws DZFWarpException;

	Map<String, SalaryBaseVO> getSalaryBaseVO(String pk_corp, String qj) throws BusinessException;

	void saveChangeNum(String pk_corp, String cuserid, String ids, SalaryBaseVO basevo, SalaryAccSetVO accsetvo,
                       String qj) throws BusinessException;

	void saveChangeNumGroup(String pk_corp, String cuserid, SalaryAccSetVO accsetvo) throws BusinessException;

	void updateChangeNum(String pk_corp, SalaryBaseVO setvo, String pk, String qj, String billtype)
			throws BusinessException;

	SalaryBaseVO[] save(String pk_corp, List<SalaryBaseVO> list, String qj) throws DZFWarpException;

	SalaryBaseVO[] delete(String pk_corp, String pk, String qj) throws DZFWarpException;

	SalaryReportVO getSalarySetInfo(String pk_corp, String cpersonids, String qj) throws BusinessException;
}

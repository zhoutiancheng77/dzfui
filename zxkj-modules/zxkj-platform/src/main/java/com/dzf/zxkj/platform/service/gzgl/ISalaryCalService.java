package com.dzf.zxkj.platform.service.gzgl;


import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryBaseVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryReportVO;

public interface ISalaryCalService {

	void calSbGjj(SalaryReportVO vo, SalaryAccSetVO setvo, SalaryBaseVO basevo);

	void calSbGjj(SalaryReportVO vo, SalaryAccSetVO setvo, SalaryBaseVO basevo, String billtype, boolean isAllowNull,
                  boolean isCopy);
}

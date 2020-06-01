package com.dzf.zxkj.app.service.report;


import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportResBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;


import java.util.List;

public interface INssbDMOService {

	public List qryNssbDataYear(ReportBeanVO reportBean) throws DZFWarpException;

	public List qryNssbDataPeriod(String pk_corp, String period) throws DZFWarpException;

}

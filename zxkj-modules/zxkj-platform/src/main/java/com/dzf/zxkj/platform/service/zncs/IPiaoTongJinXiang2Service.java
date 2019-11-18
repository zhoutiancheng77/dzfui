package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATInComInvoiceVO2;

import java.util.Map;

/**
 * 票通进项接口
 * @author wangzhn
 *
 */
public interface IPiaoTongJinXiang2Service {

	public Map<String, VATInComInvoiceVO2> savePt(CorpVO corpvo, String jspbh, String userid, String invoiceDateStart, String invoiceDateEnd, String serType, String rzPeriod, DZFDate kprj) throws DZFWarpException;
	
	public String compareCorpName(String taxNum, String vertifyCode, String unitname)throws DZFWarpException;
}

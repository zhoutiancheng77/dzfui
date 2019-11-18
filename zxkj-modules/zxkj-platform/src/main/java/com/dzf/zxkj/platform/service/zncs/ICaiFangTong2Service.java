package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceVO2;

import java.util.Map;


/**
 * 财房通接口
 * @author wangzhn
 *
 */
public interface ICaiFangTong2Service {

	public Map<String, VATSaleInvoiceVO2> saveCft(CorpVO corpvo, String userid, DZFDate loginDate, StringBuffer msg) throws DZFWarpException;
}

package com.dzf.zxkj.platform.service.zncs;

import java.util.Map;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

/**
 * 财房通接口
 * @author wangzhn
 *
 */
public interface ICaiFangTongService {

	public Map<String, VATSaleInvoiceVO> saveCft(CorpVO corpvo, String userid, DZFDate loginDate, StringBuffer msg) throws DZFWarpException;
}

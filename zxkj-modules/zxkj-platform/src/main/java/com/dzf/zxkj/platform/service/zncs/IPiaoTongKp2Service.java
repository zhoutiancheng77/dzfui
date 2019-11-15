package com.dzf.zxkj.platform.service.zncs;

import java.util.Map;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceVO2;

/**
 * 发票处理接口
 * @author wangzhn
 *
 */
public interface IPiaoTongKp2Service {

	public Map<String, VATSaleInvoiceVO2> saveKp(CorpVO corpvo, String userid, VATSaleInvoiceVO2 paramvo) throws DZFWarpException;
}

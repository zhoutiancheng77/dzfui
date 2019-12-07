package com.dzf.zxkj.platform.service.zncs;

import java.util.Map;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

/**
 * 发票处理接口
 * @author wangzhn
 *
 */
public interface IPiaoTongKpService {

	public Map<String, VATSaleInvoiceVO> saveKp(CorpVO corpvo, String userid, VATSaleInvoiceVO paramvo) throws DZFWarpException;
}

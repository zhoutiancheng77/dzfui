package com.dzf.zxkj.platform.service.zncs;

import java.util.Map;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.pjgl.VATInComInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

/**
 * 票通进项接口
 * @author wangzhn
 *
 */
public interface IPiaoTongJinXiangService {

	public Map<String, VATInComInvoiceVO> savePt(CorpVO corpvo, String jspbh, String userid, DZFDate loginDate) throws DZFWarpException;
}

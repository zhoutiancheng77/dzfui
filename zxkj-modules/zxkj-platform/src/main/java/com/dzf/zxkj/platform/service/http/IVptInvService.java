package com.dzf.zxkj.platform.service.http;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongReqVO;

public interface IVptInvService {

    boolean updateInvoice(PiaoTongReqVO reqVO) throws DZFWarpException;
}

package com.dzf.zxkj.platform.service.pzgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;

/**
 * 凭证签字
 * @author zhangj
 *
 */
public interface IPzqzService {
	
	public void saveSignPz(TzpzHVO hvo, String cashid, DZFDate signdate) throws DZFWarpException;
	
	public void saveCancelSignPz(TzpzHVO hvo, String cashid) throws DZFWarpException;

}

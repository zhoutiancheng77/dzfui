package com.dzf.zxkj.platform.service.icset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.icset.InvAccSetVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IInvAccSetService {

	InvAccSetVO query(String pk_corp) throws DZFWarpException;

	InvAccSetVO save(InvAccSetVO vo1) throws DZFWarpException;
	
	InvAccSetVO saveGroupVO(CorpVO cpvo) throws DZFWarpException;
}

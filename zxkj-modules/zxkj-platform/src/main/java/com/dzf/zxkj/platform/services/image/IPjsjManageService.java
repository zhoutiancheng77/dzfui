package com.dzf.zxkj.platform.services.image;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.pjgl.PjCheckBVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IPjsjManageService {

	/**
	 * 更新收检票数量
	 * @param selcorp
	 * @param qj
	 * @param pjlxType
	 * @param from
	 * @param pk_qj
	 * @param userVO
	 * @param corpVo
	 * @param calNum 累计的数量
	 * @throws DZFWarpException
	 */
	public void updateCountByPjlx(String selcorp, String qj, String pjlxType, String from, String pk_qj, UserVO userVO, CorpVO corpVo, int calNum) throws DZFWarpException;
	
	public List<PjCheckBVO> queryPjlxTypes(String pk_corp) throws DZFWarpException;
}

package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.zcgl.IAssetcardHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@SuppressWarnings("all")
@Service("assetcardHelperImpl")
public class AssetcardHelperImpl implements IAssetcardHelper {
	@Autowired
	private SingleObjectBO singleObjectBO;

	/**
	 * 校验期间是否已经结账，如果已经结账，则当月不允许进行任何操作
	 * 
	 * @param pk_corp
	 * @param date
	 * @throws BusinessException
	 */
	public void checkPeriodIsSettle(String pk_corp, DZFDate date)
			throws BusinessException {
//		String period = DateUtils.getPeriod(date);
//		String where = "pk_corp=? and period=? and nvl(dr,0)=0";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(period);
//		// , pk_corp, period);
//		GdzcjzVO[] gdzcjzVOs = (GdzcjzVO[]) singleObjectBO.queryByCondition(
//				GdzcjzVO.class, where, sp);
//		if (gdzcjzVOs != null && gdzcjzVOs.length > 0
//				&& gdzcjzVOs[0].getJzfinish() != null
//				&& gdzcjzVOs[0].getJzfinish().booleanValue())
//			throw new BusinessException(String.format(
//					"月份%s 已经进行固定资产结账，不允许进行操作！", period));
	}

	/**
	 * 是否启用固定资产模块
	 * 
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public boolean enableAssetModule(String pk_corp) throws BusinessException {
		CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
				CorpVO.class);
		return corpVO != null && corpVO.getHoldflag() != null
				&& corpVO.getHoldflag().booleanValue();
	}
}

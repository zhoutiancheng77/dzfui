package com.dzf.zxkj.platform.service.zcgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardDisplayColumnVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.common.query.QueryParamVO;

import java.util.List;

public interface IKpglService {

	/**
	 * 保存(卡片)
	 * @param vo
	 * @return
	 * @throws BusinessException
	 */
	public AssetcardVO save(CorpVO corpvo, AssetcardVO vo) throws DZFWarpException;


	/**
	 * 资产卡片的生成(其他来源)
	 * @param pk_corp
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public AssetcardVO saveCard(String pk_corp, AssetcardVO vo) throws DZFWarpException;

	/**
	 * 生成资产卡片
	 * @param pkCorp
	 * @return
	 * @throws DZFWarpException
	 */
	public String buildAssetcardCode(String pkCorp) throws DZFWarpException;

	/**
	 * 查询所有数据
	 * @return
	 * @throws BusinessException
	 */
	public List<AssetcardVO> query(QueryParamVO paramvo) throws DZFWarpException;

	/**
	 * 删除
	 * @param vo
	 * @throws BusinessException
	 */
	public void delete(AssetcardVO[] vos) throws DZFWarpException;

	/**
	 * 根据主键查询
	 * @param id
	 * @return
	 * @throws BusinessException
	 */
	public AssetcardVO queryById(String id) throws DZFWarpException;

	/**
	 * 修改
	 * @param vo
	 * @throws BusinessException
	 */
	public AssetcardVO update(CorpVO corpvo, AssetcardVO vo) throws DZFWarpException;

	/**
	 * 根据公司查询（原值变更使用）
	 * @param pk_corp
	 * @return
	 */
	public List<AssetcardVO> queryByPkcorp(String loginDate, String pk_corp, String isclear) throws DZFWarpException;

	/**
	 * 根据公司查询（资产清理使用）
	 * @param pk_corp
	 * @return
	 */
	public List<AssetcardVO> queryByPkCorp(String pk_corp) throws DZFWarpException;

	/**
	 * 转总账
	 * @param vo
	 * @throws BusinessException
	 */
	public void saveToGl(AssetcardVO vo) throws DZFWarpException;

	/**
	 * 资产清理
	 * @param selectedVOs
	 * @throws BusinessException
	 */
	public void updateAssetClear(String loginDate, CorpVO corpvo, AssetcardVO[] selectedVOs, String loginuserid) throws DZFWarpException ;

	/**
	 * 根据语句查询
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public List<AssetcardVO> queryByIds(String ids) throws DZFWarpException;

	/**
	 * 期初资产,TS 是否变化是否允许修改
	 * @return
	 * @throws BusinessException
	 */
	public String checkEdit(String pk_corp, AssetcardVO cardvo)throws DZFWarpException;

	public Object[] impExcel(String loginDate, String userid, CorpVO corpvo, AssetcardVO[] selectedVOs)throws DZFWarpException;
	/**
	 * 自动整理编号
	 * @param pk_corp
	 * @throws BusinessException
	 */
	public void updateOrder(String pk_corp)throws DZFWarpException;

	public void checkCorp(String pk_corp, List<String> ids) throws DZFWarpException;
	/**
	 * 参照数据查询
	 * @return
	 * @throws BusinessException
	 */
//	public List<AssetcardVO> queryRefData(String pk_corp) throws BusinessException;

	/**
	 * 根据公司+资产类别查询默认的科目赋值
	 * @param pk_corp
	 * @param pk_zclb
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BdTradeAssetCheckVO> queryDefaultFromZclb(String pk_corp, String pk_zclb) throws DZFWarpException;


	/**
	 * 批量转总账
	 * @param assetids
	 * @throws DZFWarpException
	 */
	public String saveVoucherFromZc(String[] assetids, String pk_corp, String coperatorid, DZFDate currDate, DZFBoolean bhb) throws DZFWarpException;

	/**
	 * 查询显示列
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<AssetcardDisplayColumnVO> qryDisplayColumns(String pk_corp) throws DZFWarpException;

	/**
	 * 保存显示列
	 * @param displayvo
	 * @throws DZFWarpException
	 */
	public void saveDisplayColumn(AssetcardDisplayColumnVO displayvo, String pk_corp) throws DZFWarpException;

	/**
	 * 查询 taxstyle = 2的税目信息
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TaxitemVO>  qrytaxitems(String pk_corp) throws DZFWarpException;

	/**
	 * 获取最小的资产使用日期
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String getMinAssetPeriod(String pk_corp) throws DZFWarpException;


	/**
	 * 使用年限调整
	 * @param id
	 * @param newlimit
	 * @throws DZFWarpException
	 */
	public void updateAdjustLimit(String id, Integer newlimit) throws DZFWarpException;


	/**
	 * 资产编码+资产名称+资产原值，是否重复(单个)
	 * @return
	 * @throws DZFWarpException
	 */
	public String brepeat_sig(AssetcardVO kpvos, String pk_corp) throws DZFWarpException;
	/**
	 * 资产编码+资产名称+资产原值，是否重复(批量)
	 * @return
	 * @throws DZFWarpException
	 */
	public String brepeat_mul(List<AssetcardVO> kpvos, String pk_corp) throws DZFWarpException;
	
}

package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.common.utils.AssetUtil;
import com.dzf.zxkj.platform.model.bdset.BdTradeAssetTemplateVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpmbBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpmbVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepTemplate;
import com.dzf.zxkj.platform.model.zcgl.BdTradeAssetTemplateBVO;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IAssetTemplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
@Service("assetTempletImpl")
public class AssetTempletImpl implements IAssetTemplet {
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil = null;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;
	/**
	 * 取固定资产折旧 清理模板（先取公司级折旧清理模版，如果不存在，再取行业级折旧清理模版）
	 * @param pk_corp
	 * @param categoryVO
	 * @return
	 * @throws BusinessException
	 */
	@Override
	public AssetDepTemplate[] getAssetDepTemplate(String pk_corp, Integer tempkind,
												  BdAssetCategoryVO categoryVO, HashMap<String, AssetDepTemplate[]> depTemplateMap) throws DZFWarpException {
		String key = pk_corp + categoryVO.getPrimaryKey();
		if (depTemplateMap.containsKey(key))
			return (AssetDepTemplate[])depTemplateMap.get(key);

		CorpVO corpvo = corpService.queryByPk(pk_corp);
		AssetDepTemplate[] template = getCorpAssetDepTemplate(pk_corp,tempkind, categoryVO);
		if (template == null)
			template = getTradeAssetDepTemplate(pk_corp,tempkind, categoryVO);
		if (template == null){
			if(categoryVO.getAssetproperty().intValue()  ==1 ){
				throw new BusinessException(String.format(
						"没有找到公司:%s, 资产属性为%s, 资产类别为%s的对应模板", new Object[] {
								corpvo.getUnitname(),
								AssetUtil.getAssetProperty(categoryVO.getAssetproperty()
										.intValue()), categoryVO.getCatename() }));
			}else{
				throw new BusinessException(String.format(
						"没有找到公司:%s, 资产属性为%s, 资产类别为%s的固定资产折旧模板", new Object[] {
								corpvo.getUnitname(),
								AssetUtil.getAssetProperty(categoryVO.getAssetproperty()
										.intValue()), categoryVO.getCatename() }));
			}
			
		}
		depTemplateMap.put(key, template);
		return template;
	}
	
	/**
	 * 公司级配置的资产折旧清理模版
	 * @param pk_corp 
	 * @param categoryVO
	 * @param tempkind 模版类别：0-固定资产清理，1-固定资产折旧
	 * @return
	 * @throws BusinessException
	 */
	private AssetDepTemplate[] getCorpAssetDepTemplate(String pk_corp,Integer tempkind,
			BdAssetCategoryVO categoryVO) throws DZFWarpException {
		int assetpropertyInt = categoryVO.getAssetproperty().intValue();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(assetpropertyInt);
		sp.addParam(tempkind);
		StringBuilder sb = new StringBuilder();
		sb.append(" nvl(dr,0)=0 and pk_corp=? and nvl(assetproperty,-1)=?");
		sb.append(" and nvl(tempkind,1)=? ");
		
		String corpWhere = sb.toString();//"nvl(dr,0)=0 and pk_corp=? and nvl(assetproperty,-1)=?";
		String corpCategoryFilter =" and pk_assetcategory=?";
		sp.addParam(categoryVO.getPrimaryKey());
		String corpNonCategoryFilter = " and nvl(pk_assetcategory,'0')='0'";

		YntCpmbVO[] headVOs = (YntCpmbVO[]) singleObjectBO.queryByCondition(
				YntCpmbVO.class, corpWhere + corpCategoryFilter, sp);
		if (headVOs == null || headVOs.length == 0){
			sp.clearParams();
			sp.addParam(pk_corp);
			sp.addParam(assetpropertyInt);
			sp.addParam(tempkind);
			headVOs = (YntCpmbVO[]) singleObjectBO.queryByCondition(
					YntCpmbVO.class, corpWhere + corpNonCategoryFilter, sp);
		}
		if (headVOs == null || headVOs.length == 0)
			return null;
		sp.clearParams();
		sp.addParam(headVOs[0].getPrimaryKey() );
		String bodyWhere = "nvl(dr,0)=0 and pk_corp_assettemplate=?";
		YntCpmbBVO[] childVOs = (YntCpmbBVO[]) singleObjectBO.queryByCondition(
				YntCpmbBVO.class, bodyWhere, sp);

		if (childVOs == null || childVOs.length == 0)
			return null;

		YntCpmbVO headVO = headVOs[0];
		String assetproperty = AssetUtil.getAssetProperty(assetpropertyInt);
		String assetcategory = (headVO.getPk_assetcategory() == null)
				|| (headVO.getPk_assetcategory().equals("")) ? "空" : categoryVO
				.getCatename();
		CorpVO cvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		String errorPrefix = String.format(
				"公司%s的固定资产折旧模板(资产属性为[%s], 资产类别为[%s])中", new Object[] { SecretCodeUtils.deCode(cvo.getUnitname()),
						assetproperty, assetcategory });

		int debitcount = 0;
		int creditcount = 0;
		Map<String, YntCpaccountVO> map = accountService.queryMapByPk(pk_corp);
		ArrayList<AssetDepTemplate> list = new ArrayList<AssetDepTemplate>();
		for (int i = 0; i < childVOs.length; i++) {
			AssetDepTemplate template = new AssetDepTemplate();
			YntCpaccountVO accountvo = map.get(childVOs[i].getPk_account());
			if (childVOs[i].getAccountdirect().intValue() == 1) {
				if(tempkind == 1){
					if (childVOs[i].getAccountkind().intValue() != 2) {
						throw new BusinessException(
								String.format("%s贷方科目的取数类别不是[累计折旧]",new Object[] { errorPrefix }));
					}
				}
				creditcount++;
			} else {
				if(tempkind == 1){
					if (childVOs[i].getAccountkind().intValue() != 3) {
						throw new BusinessException(
								String.format("%s借方科目的取数类别不是[费用科目]",new Object[] { errorPrefix }));
					}
				}
				debitcount++;
			}
			template.setPk_account(childVOs[i].getPk_account());
			template.setSubcode(accountvo.getAccountcode());
			template.setSubname(accountvo.getAccountname());
			template.setAbstracts(childVOs[i].getAbstracts());
			template.setAccountkind(childVOs[i].getAccountkind());
			template.setDirect(childVOs[i].getAccountdirect());
			list.add(template);
		}
		if(tempkind == 1){
			if (creditcount == 0)
				throw new BusinessException(String.format("%s不存在贷方科目",
						new Object[] { errorPrefix }));
			if (creditcount > 1) {
				throw new BusinessException(String.format("%s贷方科目不能多于1个",
						new Object[] { errorPrefix }));
			}
			if (debitcount == 0)
				throw new BusinessException(String.format("%s不存在借方科目",
						new Object[] { errorPrefix }));
			if (debitcount > 1) {
				throw new BusinessException(String.format("%s贷方科目不能多于1个",
						new Object[] { errorPrefix }));
			}
		}
		if(list != null && list.size() > 0){
			return list.toArray(new AssetDepTemplate[0]);
		}
		return null;
	}
	
	/**
	 * 行业级资产折旧清理模版
	 * @param pk_corp
	 * @param categoryVO
	 * @return
	 * @throws BusinessException
	 */
	private AssetDepTemplate[] getTradeAssetDepTemplate(String pk_corp,Integer tempkind,
			BdAssetCategoryVO categoryVO) throws DZFWarpException {
		int assetpropertyInt = categoryVO.getAssetproperty().intValue();
		StringBuilder sb = new StringBuilder();
		sb.append("nvl(dr,0)=0 and nvl(tempkind,-1)="+tempkind);
		sb.append(" and pk_trade_accountschema in ");
		sb.append("(select corptype from bd_corp where pk_corp='"+pk_corp+"')");
		sb.append(" and nvl(assetproperty,-1)="+assetpropertyInt);
		String where = sb.toString();
//				"nvl(dr,0)=0 and nvl(tempkind,-1)="+tempkind+" and pk_trade_accountschema in "
//				+ "(select corptype from bd_corp where pk_corp='"+pk_corp+"') and nvl(assetproperty,-1)="+assetpropertyInt;
		String andAssetCategoryFilter = " and pk_assetcategory='"+ categoryVO.getPrimaryKey() +"'";
		String andNonAssetCategoryFilter = " and nvl(pk_assetcategory,'0')='0'";

		BdTradeAssetTemplateVO[] headVOs = (BdTradeAssetTemplateVO[]) singleObjectBO
				.queryByCondition(BdTradeAssetTemplateVO.class, where
						+ andAssetCategoryFilter, new SQLParameter());
		if (headVOs == null || headVOs.length == 0)
			headVOs = (BdTradeAssetTemplateVO[]) singleObjectBO
					.queryByCondition(BdTradeAssetTemplateVO.class, where
							+ andNonAssetCategoryFilter, new SQLParameter());
		if (headVOs == null || headVOs.length == 0)
			return null;

		BdTradeAssetTemplateVO headVO = headVOs[0];
		String assetproperty = AssetUtil.getAssetProperty(assetpropertyInt);
		String assetcategory = (headVO.getPk_assetcategory() == null)
				|| (headVO.getPk_assetcategory().equals("")) ? "空" : categoryVO
				.getCatename();
		String errorPrefix = String.format(
				"公司%s所对应的行业固定资产折旧模板(资产属性为[%s], 资产类别为[%s])中", new Object[] {
						pk_corp, assetproperty, assetcategory });
		String sql = "nvl(dr,0)=0 and pk_trade_assettemplate=?";
		SQLParameter param = new SQLParameter();
		param.addParam(headVO.getPrimaryKey());
		BdTradeAssetTemplateBVO[] childVOs = (BdTradeAssetTemplateBVO[]) singleObjectBO
				.queryByCondition(BdTradeAssetTemplateBVO.class, sql,param);
		if (childVOs == null || childVOs.length == 0)
			return null;

		int debitcount = 0;
		int creditcount = 0;
		ArrayList<AssetDepTemplate> list = new ArrayList<AssetDepTemplate>();
		for (int i = 0; i < childVOs.length; i++) {
			AssetDepTemplate template = new AssetDepTemplate();
			String pk_account = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(childVOs[i].getPk_account(), pk_corp);
			if (childVOs[i].getAccountdirect().intValue() == 1) {
				if(tempkind == 1){
					if (childVOs[i].getAccountkind().intValue() != 2) {
						throw new BusinessException(String.format("%s贷方科目的取数类别不是[累计折旧]", new Object[] { errorPrefix }));
					}
				}
				creditcount++;
			} else {
				if(tempkind == 1){
					if (childVOs[i].getAccountkind().intValue() != 3) {
						throw new BusinessException( String.format("%s借方科目的取数类别不是[费用科目]", new Object[] { errorPrefix }));
					}
				}
				debitcount++;
			}
			template.setPk_account(pk_account);
			template.setAbstracts(childVOs[i].getAbstracts());
			template.setAccountkind(childVOs[i].getAccountkind());
			template.setDirect(childVOs[i].getAccountdirect());
			list.add(template);
		}
		if(tempkind == 1){
			if (creditcount == 0)
				throw new BusinessException(String.format("%s不存在贷方科目",
						new Object[] { errorPrefix }));
			if (creditcount > 1) {
				throw new BusinessException(String.format("%s贷方科目不能多于1个",
						new Object[] { errorPrefix }));
			}
			if (debitcount == 0)
				throw new BusinessException(String.format("%s不存在借方科目",
						new Object[] { errorPrefix }));
			if (debitcount > 1) {
				throw new BusinessException(String.format("%s贷方科目不能多于1个",
						new Object[] { errorPrefix }));
			}
		}
		if(list != null && list.size() > 0){
			return list.toArray(new AssetDepTemplate[0]);
		}
		return null;
	}

}

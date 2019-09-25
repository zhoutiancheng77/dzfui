package com.dzf.zxkj.platform.services.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.*;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.pzgl.IVoucherService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.zcgl.IAssetCard;
import com.dzf.zxkj.platform.services.zcgl.IAssetCleanService;
import com.dzf.zxkj.platform.util.SpringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service("am_assetclsserv")
@SuppressWarnings("all")
public class AssetCleanServiceImpl implements IAssetCleanService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IAssetCard assetCard;
	@Autowired
	private ICpaccountService gl_cpacckmserv ;
	@Autowired
	private ICpaccountCodeRuleService gl_accountcoderule ;

	@Autowired
	private IAccountService accountService;

	@Override
	public List<AssetCleanVO> query(String pk_corp, AssetQueryCdtionVO qryVO)
			throws DZFWarpException {
		String sql = buildSql(pk_corp, qryVO);
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(pk_corp);
		if (qryVO.getStart_date().compareTo(qryVO.getEnd_date()) == 0) {
			parameter.addParam(qryVO.getStart_date());
		} else {
			parameter.addParam(qryVO.getStart_date());
			parameter.addParam(qryVO.getEnd_date());
		}
		if (!StringUtil.isEmpty(qryVO.getAsscd_id())) {
			parameter.addParam(qryVO.getAsscd_id());
		}
		if (!StringUtil.isEmpty(qryVO.getAscode())) {
			parameter.addParam("%" + qryVO.getAscode() + "%");
		}

		List<AssetCleanVO> listVO = (List<AssetCleanVO>) singleObjectBO
				.executeQuery(sql, parameter, new BeanListProcessor(
						AssetCleanVO.class));
		return listVO;
	}

	public String buildSql(String pk_corp, AssetQueryCdtionVO paramvo) {
		StringBuilder sb = new StringBuilder();
		String[] joinFields = getJoinFields();
		sb.append("select ");
		for (String field : joinFields) {
			sb.append(field + ",");
		}
		sb.append(" ynt_assetclear.* ");
		sb.append(" from ynt_assetclear ynt_assetclear ");
		sb.append(" left join ynt_assetcard ynt_assetcard on ynt_assetcard.pk_assetcard = ynt_assetclear.pk_assetcard ");
		sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_assetclear.pk_voucher ");
		sb.append(" where nvl(ynt_assetclear.dr,0) = 0 and nvl(ynt_assetcard.dr,0) =0 and ynt_assetclear.pk_corp = ? ");
		if (paramvo.getStart_date().compareTo(paramvo.getEnd_date()) == 0) {
			sb.append(" and ynt_assetclear.businessdate = ? ");
		} else {
			sb.append(" and (ynt_assetclear.businessdate >= ? and ynt_assetclear.businessdate <= ? )");
		}
		if (!StringUtil.isEmpty(paramvo.getAsscd_id())) {
			sb.append(" and ynt_assetclear.pk_assetcard =  ? ");
		}
		if (!StringUtil.isEmpty(paramvo.getAscode())) {
			sb.append(" and ynt_assetcard.assetcode like ? ");
		}
		sb.append(" order by ynt_assetcard.assetcode,  ynt_assetclear.businessdate");
		return sb.toString();
	}

	public String[] getJoinFields() {
		String[] joinFields = new String[] { "ynt_tzpz_h.pzh as voucherno",
				// "ynt_assetcard.assetname as pk_assetcard_name"
				"ynt_assetcard.assetcode as pk_assetcard_name" };
		return joinFields;
	}

	@Override
	public void delete(AssetCleanVO vo) throws DZFWarpException {
		if (vo.getIstogl().booleanValue()) {
			throw new BusinessException("已转总账，不允许删除。");
		}
		checkPeriodIsSettle(vo.getPk_corp(), vo.getBusinessdate());
		singleObjectBO.deleteObject(vo);
		assetCard.updateIsClear(new String[] { vo.getPk_assetcard() }, false);
	}

	@Override
	public void insertToGL(String loginDate, CorpVO corpvo, AssetCleanVO vo)
			throws DZFWarpException {
		checkPeriodIsSettle(corpvo.getPk_corp(), vo.getBusinessdate());
		processToGL(loginDate, corpvo, vo);
	}

	public void checkPeriodIsSettle(String pk_corp, DZFDate date)
			throws DZFWarpException {
//		String period = DateUtils.getPeriod(date);
//		SQLParameter param = new SQLParameter();
//		param.addParam(pk_corp);
//		param.addParam(period);
//		String where = "pk_corp=? and period=? and nvl(dr,0)=0";
//
//		GdzcjzVO[] gdzcjzVOs = (GdzcjzVO[]) singleObjectBO.queryByCondition(
//				GdzcjzVO.class, where, param);
//
//		if (gdzcjzVOs != null && gdzcjzVOs.length > 0
//				&& gdzcjzVOs[0].getJzfinish() != null
//				&& gdzcjzVOs[0].getJzfinish().booleanValue())
//			throw new BusinessException(String.format(
//					"月份%s 已经进行固定资产结账，不允许进行操作！", period));
	}

	/**
	 * 处理转总账
	 */
	public void processToGL(String loginDate, CorpVO corpvo,
			AssetCleanVO clearVO) throws DZFWarpException {
		checkAssetClearConcurrency(clearVO);
		if (clearVO.getIstogl() != null && clearVO.getIstogl().booleanValue())
			throw new BusinessException(String.format("资产清理单%s已经转总账，不允许重复转总账",
					clearVO.getVbillno()));
		if (clearVO.getIssettle() != null
				&& clearVO.getIssettle().booleanValue())
			throw new BusinessException(String.format("资产清理单%s已经结账，不允许转总账",
					clearVO.getVbillno()));
		// 生成凭证
		String pk_voucher = createVoucher(loginDate, corpvo, clearVO);
		// 回写标记
		updateToGLState(clearVO, true, pk_voucher);
	}

	private void updateToGLState(AssetCleanVO vo, boolean istogl,
			String pk_voucher) throws DZFWarpException {
		if (vo == null)
			throw new BusinessException("资产清理单已经被他人删除，请刷新界面");
		if (vo.getIssettle() != null && vo.getIssettle().booleanValue())
			throw new BusinessException(String.format("资产清理单%s已经结账，不允许修改",
					vo.getVbillno()));

		vo.setIstogl(new DZFBoolean(istogl));
		vo.setPk_voucher(pk_voucher);
		singleObjectBO.update(vo, new String[] { "istogl", "pk_voucher" });
	}

	private void checkAssetClearConcurrency(AssetCleanVO AssetCleanVO)
			throws DZFWarpException {
		AssetCleanVO oldVO = (AssetCleanVO) singleObjectBO.queryByPrimaryKey(
				AssetCleanVO.class, AssetCleanVO.getPrimaryKey());
		// 当数据已被其他用户并发删除时
		if (oldVO == null) {
			throw new BusinessException("该数据已经被他人删除，请刷新界面");
		}
		// 当数据已被其他用户并发修改时
		// if
		// (!oldVO.getAttributeValue("ts").equals(AssetCleanVO.getAttributeValue("ts")))
		// {
		// throw new BusinessException("该数据已经被他人修改，请刷新界面，重做业务");
		// }
	}

	/**
	 * 根据资产清理单生成凭证
	 */
	public String createVoucher(String loginDate, CorpVO corpvo, AssetCleanVO vo)
			throws DZFWarpException {
		AssetcardVO AssetcardVO = (AssetcardVO) singleObjectBO
				.queryByPrimaryKey(AssetcardVO.class, vo.getPk_assetcard());
		return createVoucher(loginDate, corpvo, vo.getPrimaryKey(), AssetcardVO);
	}

	/**
	 * 根据资产清理单生成凭证
	 */
	public String createVoucher(String loginDate, CorpVO corpvo,
			String pk_assetclear, AssetcardVO assetcardVO)
			throws DZFWarpException {
		YntCpaccountVO[] accountvos = accountService.queryByPk(corpvo.getPk_corp());
		if(accountvos == null || accountvos.length ==0){
			throw new BusinessException("该公司科目不存在");
		}
		// templatevoMap.clear();
		BdAssetCategoryVO categoryVO = (BdAssetCategoryVO) singleObjectBO
				.queryByPrimaryKey(BdAssetCategoryVO.class,
						assetcardVO.getAssetcategory());

		// BdTradeAssetTemplateVO templateVO =
		// getAssetClearTemplateVO(assetcardVO.getPk_corp(),
		// categoryVO.getAssetproperty(), assetcardVO.getAssetcategory());
		// if(templateVO == null)
		// throw new BusinessException(String.format("没有找到公司%s对应的固定资产清理模板！",
		// assetcardVO.getPk_corp()));

//		HashMap<String, AssetDepTemplate[]> depTemplateMap = new HashMap<String, AssetDepTemplate[]>();
//		IAssetTemplet assetTempletImpl = (IAssetTemplet) SpringUtils
//				.getBean("assetTempletImpl");
//		AssetDepTemplate[] templates = assetTempletImpl.getAssetDepTemplate(
//				assetcardVO.getPk_corp(), 0, categoryVO, depTemplateMap);
//		if (templates == null || templates.length == 0)
//			throw new BusinessException(String.format("没有找到公司%s对应的固定资产清理模板！",
//					assetcardVO.getPk_corp()));
		//不走模板，根据系统生成科目
		AssetDepTemplate[] vos = getClearVoucherTemplet(corpvo, assetcardVO, accountvos, categoryVO);

		return createVoucher(loginDate, corpvo, pk_assetclear, assetcardVO, vos);
	}

	private AssetDepTemplate[] getClearVoucherTemplet(CorpVO corpvo, AssetcardVO assetcardVO,
			YntCpaccountVO[] accountvos, BdAssetCategoryVO categoryVO) {
		String zy = "固定资产清理";
		if(categoryVO!= null){
			if(categoryVO.getAssetproperty()!=null){
				if(categoryVO.getAssetproperty().intValue() ==1){
					zy = "无形资产清理";
				}else if(categoryVO.getAssetproperty().intValue() ==3){
					zy = "待摊费用清理";
				}
			}
		}
		AssetDepTemplate[] vos = null;
		if(categoryVO.getAssetproperty().intValue() ==3){//待摊费用
			vos = new AssetDepTemplate[2];
			//累计折旧
			vos[0]= new AssetDepTemplate();
			vos[0].setPk_account(assetcardVO.getPk_zjfykm());//借，费用科目
			vos[0].setAccountkind(1);
			vos[0].setDirect(0);
			vos[0].setAbstracts(zy);
			//固定资产(0)
			vos[1] = new AssetDepTemplate();
			vos[1].setPk_account(assetcardVO.getPk_jtzjkm());//贷，折旧科目
			vos[1].setAccountkind(1);
			vos[1].setDirect(1);
			vos[1].setAbstracts(zy);
		}else{
			vos = new AssetDepTemplate[3];
			//累计折旧
			vos[0]= new AssetDepTemplate();
			vos[0].setPk_account(assetcardVO.getPk_jtzjkm());
			vos[0].setAccountkind(2);
			vos[0].setDirect(0);
			vos[0].setAbstracts(zy);
			//资产清理科目
			vos[1] = new AssetDepTemplate();
			vos[1].setPk_account(getAssetClearAccountId(corpvo,accountvos,categoryVO));
			vos[1].setAccountkind(1);
			vos[1].setDirect(0);
			vos[1].setAbstracts(zy);
			//固定资产(0)
			vos[2] = new AssetDepTemplate();
			vos[2].setPk_account(assetcardVO.getPk_zckm());//资产科目
			vos[2].setAccountkind(0);
			vos[2].setDirect(1);
			vos[2].setAbstracts(zy);
		}
		return vos;
	}

	private String getAssetClearAccountId(CorpVO corpvo,YntCpaccountVO[] accountvos,BdAssetCategoryVO categoryVO) {
		Map<String, YntCpaccountVO> code_map = new HashMap<String, YntCpaccountVO>();
		for(YntCpaccountVO vo:accountvos){
			code_map.put(vo.getAccountcode(), vo);
		}
		Integer corpschema = yntBoPubUtil.getAccountSchema(corpvo.getPk_corp());
		String accountcode = "";
		
		String queryAccountRule = gl_cpacckmserv.queryAccountRule(corpvo.getPk_corp());
		
		if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则 小企业会计准则
			if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
				accountcode = gl_accountcoderule.getNewRuleCode("571101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
			}else{//固定资产
				accountcode = "1606";
			}
		} else if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {//2007会计准则 企业会计准则
			if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
				accountcode = gl_accountcoderule.getNewRuleCode("6115", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
			}else{//固定资产
				accountcode = "1606";
			}
		} else if (corpschema == DzfUtil.POPULARSCHEMA.intValue()) {// 民间
			if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
				accountcode = "5401";
			}else{//固定资产
				accountcode = "1509";
			}
		} else if (corpschema == DzfUtil.CAUSESCHEMA.intValue()) {// 事业
			accountcode = "1701";
		}else if(corpschema ==  DzfUtil.COMPANYACCOUNTSYSTEM.intValue() ){//企业会计制度
			if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
				accountcode = gl_accountcoderule.getNewRuleCode("560101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
			}else{//固定资产
				accountcode = "1701";
			}
		} else {
			throw new BusinessException("该制度资产清理,敬请期待!");
		}
		YntCpaccountVO resvo = code_map.get(accountcode);
		if(resvo == null){
			throw new BusinessException("清理科目不存在，请进行科目升级");
		}
		return resvo.getPk_corp_account();
	}

	/**
	 * 更新资产清理单的已转总账标记
	 */
	@Override
	public void updateACToGLState(String pk_assetclear, boolean istogl,
			String pk_voucher) throws DZFWarpException {
		AssetCleanVO vo = (AssetCleanVO) singleObjectBO.queryByPrimaryKey(
				AssetCleanVO.class, pk_assetclear);
		updateToGLState(vo, istogl, pk_voucher);
	}

	/**
	 * 生成资产清理单
	 */
	public SuperVO createAssetClear(String loginDate, SuperVO vo)
			throws DZFWarpException {
		if (vo != null && vo instanceof AssetcardVO) {
			AssetcardVO AssetcardVO = (AssetcardVO) vo;
			if (AssetcardVO.getIsclear() != null
					&& AssetcardVO.getIsclear().booleanValue())
				throw new BusinessException(String.format(
						"资产卡片%s已经清理，不允许生成资产清理单", AssetcardVO.getAssetcode()));

			AssetCleanVO clearVO = new AssetCleanVO();
			// 单据号
			// clearVO.setVbillno(getHYPubBO().getBillNo(IBillTypeCode.HP61,
			// AssetcardVO.getPk_corp(), null, null));
			// 公司
			clearVO.setPk_corp(AssetcardVO.getPk_corp());
			// 创建人
			clearVO.setCoperatorid(AssetcardVO.getCoperatorid());
			// 创建日期
			DZFDate date = new DZFDate(loginDate);
			clearVO.setDoperatedate(date);
			// 资产卡片
			clearVO.setPk_assetcard(AssetcardVO.getPrimaryKey());
			// 清理日期
			clearVO.setBusinessdate(date);

			AssetCleanVO saveVO = (AssetCleanVO) singleObjectBO.saveObject(
					clearVO.getPk_corp(), clearVO);
			return saveVO;
		}
		return null;
	}

	/**
	 * 根据资产清理单生成凭证
	 * 
	 */
	public String createVoucher(String loginDate, CorpVO corpvo,
			String pk_assetclear, SuperVO vo, AssetDepTemplate[] templates)
			throws DZFWarpException {
		if (vo == null)
			return null;
		if (templates == null || templates.length == 0)
			throw new BusinessException("传入的固定资产清理模板为空异常");
		AssetcardVO AssetcardVO = null;
		if (vo instanceof AssetcardVO) {
			AssetcardVO = (AssetcardVO) vo;
		}
		if(AssetcardVO == null){
			throw new BusinessException("资产为空");
		}
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(AssetcardVO.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setCoperatorid(AssetcardVO.getCoperatorid());
		headVO.setDoperatedate(new DZFDate(loginDate));
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(AssetcardVO.getPk_corp(),
				headVO.getDoperatedate()));
		headVO.setSourcebillid(pk_assetclear); // 来源单据ID
		headVO.setSourcebilltype(IBillTypeCode.HP61); // 来源单据类别
		headVO.setVbillstatus(8); // 状态为自由态

		// BdTradeAssetTemplateBVO[] bodyTemplateVOs =
		// (BdTradeAssetTemplateBVO[]) templateVO.getChildren();
		// TzpzBVO[] bodyVOs = new TzpzBVO[bodyTemplateVOs.length];
		Vector<TzpzBVO> vec_bodyVOs = new Vector<TzpzBVO>();

		DZFDouble mny = DZFDouble.ZERO_DBL;
		DZFDouble totalDebit= DZFDouble.ZERO_DBL;
		DZFDouble totalCredit = DZFDouble.ZERO_DBL;
		for (AssetDepTemplate template : templates) {
			mny = new DZFDouble(getAssetAccountMny(AssetcardVO, template.getAccountkind()
					.intValue()));
			if (mny.doubleValue() == 0)
				continue; // 金额为0不显示

			TzpzBVO bodyVO = new TzpzBVO();
			bodyVO.setPk_accsubj(template.getPk_account()); // 科目
			bodyVO.setVdirect(template.getDirect());//设置方向
			bodyVO.setZy(template.getAbstracts());// 摘要
			bodyVO.setPk_currency(yntBoPubUtil.getCNYPk()); // 币种
			if (template.getDirect().intValue() == BdTradeAssetTemplateBVO.DIRECT_CREDIT) { // 贷方
				bodyVO.setDfmny(new DZFDouble(mny)); // 贷方金额
				totalCredit = totalCredit.add(mny);
			} else {
				bodyVO.setJfmny(new DZFDouble(mny)); // 借方金额
				totalDebit = totalDebit.add(mny);
			}

			vec_bodyVOs.add(bodyVO);

			// bodyVOs[index++] = bodyVO;
		}
		// 贷方合计
		headVO.setDfmny(new DZFDouble(totalCredit));
		// 借方合计
		headVO.setJfmny(new DZFDouble(totalDebit));

		if (totalCredit.doubleValue() != totalDebit.doubleValue()) {
			throw new BusinessException(String.format(
					"资产卡片%s清理生成的凭证，借方合计%.2f不等于贷方合计%.2f",
					AssetcardVO.getAssetcode(), totalDebit.doubleValue(), totalCredit.doubleValue()));
		}

		TzpzBVO[] bodyVOs = vec_bodyVOs.toArray(new TzpzBVO[0]);

		headVO.setChildren(bodyVOs);

		IVoucherService gl_tzpzserv = (IVoucherService) SpringUtils
				.getBean("gl_tzpzserv");

		headVO = gl_tzpzserv.saveVoucher(corpvo, headVO);
		// headVO = (TzpzHVO) singleObjectBO.saveObject(headVO.getPk_corp(),
		// headVO);

		return headVO.getPrimaryKey();
	}

	/**
	 * 根据固定资产取数科目类型，获取资产金额
	 * 
	 * @param AssetcardVO
	 * @param accountkind
	 * @return
	 */
	public double getAssetAccountMny(AssetcardVO AssetcardVO, int accountkind) {
		switch (accountkind) {
		case BdTradeAssetTemplateBVO.KIND_FYKM: // 费用科目
			return 0;
		case BdTradeAssetTemplateBVO.KIND_GDZC: // 固定资产
			return AssetcardVO.getAssetmny().getDouble();//资产原值
		case BdTradeAssetTemplateBVO.KIND_GDZCQL: // 固定资产清理
//			return AssetcardVO.getAssetnetvalue().getDouble();//资产净值
			return SafeCompute.sub( AssetcardVO.getAssetmny(), AssetcardVO.getDepreciation()).doubleValue();
		case BdTradeAssetTemplateBVO.KIND_LJZJ: // 累计折旧
			return AssetcardVO.getDepreciation() == null ? 0 : AssetcardVO
					.getDepreciation().getDouble();//资产折旧值
		default:
			return 0;
		}
	}

	/**
	 * 根据资产清理单删除凭证
	 */
	public void delVoucherByAssetClear(String pk_assetclear)
			throws DZFWarpException {
		AssetCleanVO clearVO = (AssetCleanVO) singleObjectBO.queryByPrimaryKey(
				AssetCleanVO.class, pk_assetclear);
		if (clearVO.getIssettle() != null
				&& clearVO.getIssettle().booleanValue())
			throw new BusinessException(String.format(
					"资产清理单%s已经结账，不允许删除资产清理单对应的凭证", clearVO.getVbillno()));
		SQLParameter param = new SQLParameter();
		param.addParam(pk_assetclear);
		TzpzHVO[] headVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(
				TzpzHVO.class,
				"nvl(dr,0)=0 and sourcebilltype='H404' and sourcebillid=?",
				param);
		if (headVOs == null || headVOs.length == 0)
			return;

		// TzpzBVO[] bodyVOs;
		for (int i = 0; i < headVOs.length; i++) {
			singleObjectBO.deleteObjectByID(headVOs[i].getPrimaryKey(),
					new Class[] { TzpzHVO.class, TzpzBVO.class });
		}
	}

	/**
	 * 获取固定资产清理模板
	 */
	// public BdTradeAssetTemplateVO getAssetClearTemplateVO(String pk_corp, int
	// assetproperty, String pk_assetcategory) throws BusinessException {
	// // String key = pk_corp + assetproperty + pk_assetcategory;
	// // if(!templatevoMap.containsKey(key)){
	// CorpVO cvo=CorpCache.getInstance().get(null, pk_corp);
	// SQLParameter paramt = new SQLParameter();
	// paramt.addParam(cvo.getCorptype());
	// paramt.addParam(assetproperty);
	// paramt.addParam(pk_assetcategory);
	// String where =
	// "nvl(dr,0)=0 and nvl(tempkind,-1)=0 and pk_trade_accountschema=? and nvl(assetproperty,-1)=?";
	//
	// //String where =
	// "nvl(dr,0)=0 and nvl(tempkind,-1)=0 and pk_trade_accountschema in (select corptype from bd_corp where pk_corp='%s') and nvl(assetproperty,-1)=%d";
	// String andAssetCategoryFilter = " and pk_assetcategory=?";
	// String andNonAssetCategoryFilter = " and nvl(pk_assetcategory,'0')='0'";
	//
	// // paramt.clearParams();
	// // paramt.addParam(cvo.getCorptype());
	// // paramt.addParam(assetproperty);
	// // 先查找指定资产类别的模板，如果找不到，则找资产类别为空的模板
	// BdTradeAssetTemplateVO[] headVOs = (BdTradeAssetTemplateVO[])
	// singleObjectBO.queryByCondition(BdTradeAssetTemplateVO.class, where +
	// andAssetCategoryFilter,paramt);
	// if(headVOs == null || headVOs.length == 0){
	// paramt.clearParams();
	// paramt.addParam(cvo.getCorptype());
	// paramt.addParam(assetproperty);
	// headVOs = (BdTradeAssetTemplateVO[])
	// singleObjectBO.queryByCondition(BdTradeAssetTemplateVO.class, where +
	// andNonAssetCategoryFilter,paramt);
	// }
	// if(headVOs == null || headVOs.length == 0) return null;
	//
	//
	// BdTradeAssetTemplateVO headVO = headVOs[0];
	// paramt.clearParams();
	// paramt.addParam(headVO.getPrimaryKey());
	//
	// BdTradeAssetTemplateBVO[] childVOs = (BdTradeAssetTemplateBVO[])
	// singleObjectBO.queryByCondition(BdTradeAssetTemplateBVO.class,
	// "nvl(dr,0)=0 and pk_trade_assettemplate=?",paramt);
	// for(int i = 0; i<childVOs.length; i++){
	// // 根据行业会计科目查找公司会计科目
	// childVOs[i].setPk_corpaccount(pub_utilserv.getCorpAccountPkByTradeAccountPk(childVOs[i].getPk_account(),
	// pk_corp));
	// }
	//
	// headVO.setChildren(childVOs);
	// //templatevoMap.put(key, headVO);
	// return headVO;
	// // }
	// // return templatevoMap.get(key);
	// }

	/**
	 * 处理固定资产清理
	 */
	@Override
	public void processAssetClears(String loginDate, CorpVO corpvo,
			SuperVO[] vos,String loginuserid) throws DZFWarpException {
		if (vos == null || vos.length == 0)
			return;
		if (vos[0] instanceof AssetcardVO) {
			AssetcardVO[] assetcardVOs = (AssetcardVO[]) vos;
			// 固定资产清理之前做计提折旧
//			IAssetDepreciation assetDepreciationImpl = (IAssetDepreciation) SpringUtils.getBean("assetDepreciationImpl");
//			
//			assetDepreciationImpl.clearProcessDep(corpvo, loginDate,assetcardVOs);

			IAssetCard assetCardImpl = (IAssetCard) SpringUtils.getBean("assetCardImpl");
			//循环生成凭证
			for(AssetcardVO votemp: assetcardVOs){
				//一个资产生成一个折旧凭证和清理凭证
				assetCardImpl.updateExecuteDepreciate(corpvo, new AssetcardVO[]{votemp}, loginDate.substring(0, 7), loginuserid);
			}
//			assetCardImpl.updateExecuteDepreciate(corpvo, assetcardVOs, loginDate.substring(0, 7), loginuserid);
			
			// templatevoMap.clear();
			AssetCleanVO clearVO;
			for (int i = 0; i < assetcardVOs.length; i++) {
				//判断当期日子是否已经计提折旧，如果无计提则不进行清理
				String zcdepreciationdate = assetcardVOs[i].getDepreciationdate() ;
				DZFDouble assetnetvalue = assetcardVOs[i].getAssetnetvalue();//资产净值
//				DZFDouble depreciation  = assetcardVOs[i].getDepreciation();//总累计折旧
				DZFDouble plansalvage = assetcardVOs[i].getPlansalvage();//预估残值
				if(StringUtil.isEmpty(assetcardVOs[i].getDepreciationdate())){
					//后面比较的是已经计提的月份，所以要提前一个月
					zcdepreciationdate = DateUtils.getPreviousPeriod(DateUtils.getPeriod(assetcardVOs[i].getAccountdate()));
					//非期初的固定资产，则从下月开始计提折旧
					if(assetcardVOs[i].getIsperiodbegin()==null || !assetcardVOs[i].getIsperiodbegin().booleanValue()){
						BdAssetCategoryVO catevo = (BdAssetCategoryVO) singleObjectBO.queryByPrimaryKey(BdAssetCategoryVO.class	, assetcardVOs[i].getAssetcategory());
						if(catevo != null && catevo.getAssetproperty() == 0){//固定资产
							zcdepreciationdate = DateUtils.getPeriod(assetcardVOs[i].getAccountdate());
						}
					}
				}
				//净值等于残值率时不用提示需要折旧
				if( SafeCompute.sub(assetnetvalue, plansalvage).doubleValue()!=0
						&& loginDate.substring(0,7).compareTo(zcdepreciationdate)>0){
					throw new BusinessException("待清理资产("+assetcardVOs[i].getAssetname()+")当月("+loginDate.substring(0,7)+")需先进行计提折旧");
				}
				// 生成固定资产清理单
				clearVO = (AssetCleanVO) createAssetClear(loginDate,
						assetcardVOs[i]);
				// 处理转总账
				processToGL(loginDate, corpvo, clearVO);
				assetCard.updateIsClear(assetcardVOs[i], true);
			}
		}
	}

	/**
	 * 固定资产清理之前做计提折旧
	 * 
	 * @param assetcardVOs
	 * @throws BusinessException
	 */
	// private void clearProcessDep(String loginDate,AssetcardVO[] assetcardVOs)
	// throws BusinessException{
	// // 对于固定资产，当月清理时仍计提折旧；无形资产和长期待摊费用，当月清理时不计提
	// String period = DateUtils.getPeriod(new DZFDate(loginDate));
	// AssetcardVO[] needDepVOs = getNeedAssetDepVOs(assetcardVOs, period);
	// if(needDepVOs == null || needDepVOs.length==0) return;
	//
	// assetCard.processDepreciations(loginDate,needDepVOs,
	// period,DZFBoolean.TRUE);//清理的计提折旧 modify by zhangj
	// }

	/**
	 * 获取需要进行计提折旧的固定资产
	 * 
	 * @param assetVOs
	 * @param period
	 * @return
	 * @throws BusinessException
	 */
	// private AssetcardVO[] getNeedAssetDepVOs(AssetcardVO[] assetVOs, String
	// period) throws BusinessException{
	// // // 对于固定资产，当月清理时仍计提折旧；无形资产和长期待摊费用，当月清理时不计提
	// // HashMap<String, BdAssetCategoryVO> cateMap = new HashMap<String,
	// BdAssetCategoryVO>();
	// ArrayList<AssetcardVO> resultVOs = new ArrayList<AssetcardVO>();
	//
	// DZFDate periodEndDate = DZFDate.getDate(period + "-01");
	// periodEndDate =
	// periodEndDate.getDateAfter(periodEndDate.getDaysMonth()).getDateBefore(1);
	// IAssetCard assetCardImpl = (IAssetCard)
	// SpringUtils.getBean("assetCardImpl");
	// HashMap<String, BdAssetCategoryVO> catagoryMap =
	// assetCardImpl.queryAssetCategoryMap();
	// for(int i = assetVOs.length-1; i>=0; i--){
	// AssetcardVO assetVO = assetVOs[i];
	// // 净值小于或等于残值，不参与折旧
	// // double assetnetvalue = assetVO.getAssetnetvalue()== null ?
	// 0:assetVO.getAssetnetvalue().getDouble();
	// // double salvage =
	// assetVO.getPlansalvage()==null?0:assetVO.getPlansalvage().getDouble();
	// // if(assetnetvalue<=salvage)
	// // continue;
	// DZFDouble assetnetvalue = getDZFDouble(assetVO.getAssetnetvalue());
	// DZFDouble salvage = getDZFDouble(assetVO.getPlansalvage());
	// if(assetnetvalue.compareTo(salvage) <=0){
	// continue;
	// }
	//
	// // 可使用月份<=累计折旧月份，不参与折旧
	// int uselimit = getIntegerNullAsZero(assetVO.getUselimit());
	// int depreciationPeriod =
	// getIntegerNullAsZero(assetVO.getDepreciationperiod());
	// if(uselimit<=depreciationPeriod) continue;
	//
	// if(assetVO.getIsperiodbegin() != null &&
	// assetVO.getIsperiodbegin().booleanValue()){ // 期初的要进行折旧
	// resultVOs.add(assetVO);
	// } else if(assetVO.getAccountdate().equals(periodEndDate) ||
	// assetVO.getAccountdate().before(periodEndDate)){ // 入账日期是当期之前
	// resultVOs.add(assetVO);
	//
	// BdAssetCategoryVO cateVO = catagoryMap.get(assetVO.getAssetcategory());
	//
	// if(DateUtils.getPeriod(assetVO.getAccountdate()).equals(period) &&
	// (cateVO.getAssetproperty().equals(1) ||
	// cateVO.getAssetproperty().equals(2))) // 无形资产和长期待摊费用
	// resultVOs.remove(assetVO);
	// }
	// }
	//
	// return resultVOs.toArray(new AssetcardVO[resultVOs.size()]);
	// }

	public DZFDouble getDZFDouble(DZFDouble obj) {
		return obj == null ? DZFDouble.ZERO_DBL : obj;
	}

	public int getIntegerNullAsZero(Integer value) {
		return value == null ? 0 : value.intValue();
	}

	@Override
	public AssetCleanVO refresh(String pk_acs) throws DZFWarpException {
		return (AssetCleanVO) singleObjectBO.queryByPrimaryKey(
				AssetCleanVO.class, pk_acs);
	}

	@Override
	public AssetCleanVO queryById(String id) throws DZFWarpException {
		// TODO Auto-generated method stub
		return (AssetCleanVO) singleObjectBO
				.queryVOByID(id, AssetCleanVO.class);
	}
}

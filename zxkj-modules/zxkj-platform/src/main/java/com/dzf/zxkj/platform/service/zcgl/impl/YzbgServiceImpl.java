package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IAssetCard;
import com.dzf.zxkj.platform.service.zcgl.IAssetcardHelper;
import com.dzf.zxkj.platform.service.zcgl.IKpglService;
import com.dzf.zxkj.platform.service.zcgl.IYzbgService;
import com.dzf.zxkj.common.query.QueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("am_yzbgserv")
@SuppressWarnings("all")
public class YzbgServiceImpl implements IYzbgService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private ICorpService corpService;
	@Override
	public ValuemodifyVO save(ValuemodifyVO vo) throws DZFWarpException {
		setDefaultData(vo);
		AssetcardVO assetvo = (AssetcardVO) singleObjectBO.queryVOByID(vo.getPk_assetcard(),
				AssetcardVO.class);
		if(StringUtil.isEmpty(vo.getPk_assetcard()) ||  assetvo == null){
			throw new BusinessException("该资产不存在!");
		}

		if(assetvo.getZjtype() == 3){
			throw new BusinessException("年数总和法不支持原值变更!");
		}
	
		checkBeforeSave(assetvo,vo);
		ValuemodifyVO revo = (ValuemodifyVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
		revo = queryById(revo.getPrimaryKey());
		if (revo != null) {
			updateAssetCard(assetvo,vo);
		}
		return revo;
	}
	/**
	 * 更改固定资产原值
	 * @param vo
	 * @throws BusinessException
	 */
	public void updateAssetCard(AssetcardVO assetvo,ValuemodifyVO vo) throws DZFWarpException {
		IAssetCard assetCardImpl = (IAssetCard) SpringUtils.getBean("assetCardImpl");
		assetCardImpl.updateAssetMny(assetvo, vo.getNewvalue()
				.getDouble(),vo);
	}

	/**
	 * 组织部分字段默认值
	 * 
	 * @param vo
	 */
	public void setDefaultData(ValuemodifyVO vo) {
		vo.setDr(new Integer(0));
		// 变更前原值
		DZFDouble originalvalue = vo.getOriginalvalue();
		if(originalvalue == null ){
			throw new BusinessException("变更前原值不能为空");
		}
		// 变更后原值
		DZFDouble newvalue = vo.getNewvalue();
		// 原值变更
		DZFDouble changevalue = newvalue.sub(originalvalue);
		vo.setChangevalue(changevalue);
	}

	@Override
	public List<ValuemodifyVO> query(QueryParamVO paramvo) throws DZFWarpException {
		String sql = buildSql(paramvo);
		SQLParameter parameter = new SQLParameter();
		parameter.addParam( paramvo.getPk_corp());
		if(paramvo.getBegindate1().compareTo(paramvo.getEnddate()) == 0){
			parameter.addParam( paramvo.getBegindate1());
		}else{
			parameter.addParam( paramvo.getBegindate1());
			parameter.addParam(paramvo.getEnddate());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_assetcard())){
			parameter.addParam(paramvo.getPk_assetcard());
		}
		if(!StringUtil.isEmpty(paramvo.getAscode())){
			parameter.addParam("%"+paramvo.getAscode()+"%");
		}
		List<ValuemodifyVO> listVO = (List<ValuemodifyVO>) singleObjectBO.executeQuery(sql, parameter, new BeanListProcessor(ValuemodifyVO.class));
		//赋值资产科目和原值变更科目
		putKmByPk(listVO,paramvo.getPk_corp());
		return listVO;
	}
	
	private void putKmByPk(List<ValuemodifyVO> listVO,String pk_corp) {
		if(listVO!=null && listVO.size()>0){
			Map<String, YntCpaccountVO> kmmap = accountService.queryMapByPk(pk_corp);
			for(ValuemodifyVO vo:listVO){
				if(!StringUtil.isEmpty(vo.getPk_bgkm())
						&& kmmap.containsKey(vo.getBgkm())){
					vo.setBgkm(kmmap.get(vo.getBgkm()).getAccountname());
				}
				if(!StringUtil.isEmpty(vo.getPk_zckm())
						&& kmmap.containsKey(vo.getPk_zckm())){
					vo.setZckm(kmmap.get(vo.getPk_zckm()).getAccountname());
				}
			}
		}
	}
	public String buildSql(QueryParamVO paramvo){
		StringBuilder sb = new StringBuilder();
		String[] joinFields = getJoinFields();
		sb.append("select ");
		for(String field : joinFields){
			sb.append(field);
			sb.append(",");
		}
		sb.append(" ynt_valuemodify.* ");
		sb.append(" from ynt_valuemodify ynt_valuemodify ");
		sb.append(" left join ynt_assetcard ynt_assetcard on ynt_assetcard.pk_assetcard = ynt_valuemodify.pk_assetcard   ");
		sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_valuemodify.pk_voucher ");
		sb.append(" where nvl(ynt_valuemodify.dr,0) = 0 and nvl(ynt_assetcard.dr,0)=0    and ynt_valuemodify.pk_corp = ? ");
		if(paramvo.getBegindate1().compareTo(paramvo.getEnddate()) == 0){
			sb.append(" and ynt_valuemodify.businessdate =  ? ");
		}else{
			sb.append(" and (ynt_valuemodify.businessdate >= ? and ynt_valuemodify.businessdate <=  ? )");
		}
		if(!StringUtil.isEmpty(paramvo.getPk_assetcard())){
			sb.append(" and ynt_valuemodify.pk_assetcard =  ? ");
		}
		if(!StringUtil.isEmpty(paramvo.getAscode())){
			sb.append(" and ynt_assetcard.assetcode like  ? ");
		}
		sb.append(" order by ynt_assetcard.assetcode ,ynt_valuemodify.businessdate");
		return sb.toString();
	}
	
	public String[] getJoinFields(){
		String[] joinFields = new String[]{
			"ynt_tzpz_h.pzh as voucherno",
			"ynt_assetcard.assetname as assetcard_name",
			"ynt_assetcard.assetcode as pk_assetcard_name"
		};
		return joinFields;
	}

	@Override
	public ValuemodifyVO queryById(String id) throws DZFWarpException {
		ValuemodifyVO vo = (ValuemodifyVO) singleObjectBO.queryVOByID(id,
				ValuemodifyVO.class);
		if(vo == null){
			return null;
		}
		HashMap<String, AssetcardVO> assetCardMap = queryAssetcardByPkcorp(null,vo.getPk_corp());
		if(assetCardMap.get(vo.getPk_assetcard())!=null){
			vo.setPk_assetcard_name(assetCardMap.get(vo.getPk_assetcard()).getAssetname());
		}
		List<ValuemodifyVO> list = new ArrayList<ValuemodifyVO>();
		list.add(vo);
		putKmByPk(list, vo.getPk_corp());
		return list.get(0);
	}

	@Override
	public void update(ValuemodifyVO vo) throws DZFWarpException {
		checkData(vo);
		singleObjectBO.update(vo);
	}
	
	/**
	 * 修改数据校验
	 * @param vo
	 * @throws BusinessException
	 */
	public void checkData(ValuemodifyVO vo) throws DZFWarpException{
		if(vo != null){
			if(vo.getIstogl() != null && vo.getIstogl().booleanValue()){
				throw new BusinessException("已转总账，不允许修改操作。");
			}
		}
	}

	/**
	 * 查询资产卡片数据
	 * 
	 * @param pkCorp
	 * @return
	 * @throws BusinessException
	 */
	private HashMap<String, AssetcardVO> queryAssetcardByPkcorp(DZFDate loginDate, String pkCorp)
			throws DZFWarpException {
		IKpglService am_kpglserv = (IKpglService) SpringUtils.getBean("am_kpglserv");
		HashMap<String, AssetcardVO> assetCardMap = new HashMap<String, AssetcardVO>();
		String date = loginDate == null ? null : loginDate.toString();
		List<AssetcardVO> listVO = (List<AssetcardVO>) am_kpglserv
				.queryByPkcorp(date,pkCorp,null);
		for (AssetcardVO assetcardVO : listVO) {
			if (!assetCardMap.containsKey(assetcardVO.getPk_assetcard())) {
				assetCardMap.put(assetcardVO.getPk_assetcard(), assetcardVO);
			}
		}
		return assetCardMap;
	}

	/**
	 * 保存前校验
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkBeforeSave(AssetcardVO assetvo,ValuemodifyVO vo) throws DZFWarpException {
		DZFDate accountdate = assetvo.getAccountdate();//开始使用日期
		DZFDate businessdate = vo.getBusinessdate();
		if(businessdate.compareTo(accountdate)<0){
			throw new BusinessException("原值变更日期必须晚于资产开始使用日期。");
		}
		
		//该资产已经折旧，不能原值变更
		String zjmonth = assetvo.getDepreciationdate();
		if(!StringUtil.isEmpty(zjmonth)){
			DZFDate mindate = DateUtils.getPeriodStartDate(zjmonth);
			if(mindate.after(businessdate)){
				throw new BusinessException("原值变更日期必须晚于资产折旧日期("+DateUtils.getPeriod(mindate)+")");
			}
		}
		String condition = " nvl(dr,0) = 0 and businessdate > ? and pk_corp=?  and pk_assetcard = ? ";
		SQLParameter params = new SQLParameter();
		params.addParam(vo.getBusinessdate());
		params.addParam(vo.getPk_corp());
		params.addParam(vo.getPk_assetcard());
		ValuemodifyVO[] vos = queryByWhere(condition,params);
		if(vos != null && vos.length > 0){
			throw new BusinessException("资产("+assetvo.getAssetname()+")已存在此变更日期后的变更数据，不允许保存。");
		}
		IAssetcardHelper assetcardHelperImpl = (IAssetcardHelper) SpringUtils
				.getBean("assetcardHelperImpl");
		assetcardHelperImpl.checkPeriodIsSettle(vo.getPk_corp(),
				vo.getBusinessdate());
	}

	@Override
	public void updateAVToGLState(String pk_assetvalueChange, boolean istogl,
			String pk_voucher) throws DZFWarpException {
		ValuemodifyVO vo = (ValuemodifyVO) singleObjectBO.queryByPrimaryKey(ValuemodifyVO.class, pk_assetvalueChange);
		if(vo == null)
			throw new BusinessException("资产原值变更单已经被他人删除，请刷新界面");
		if(istogl){
			if(vo.getIstogl() != null && vo.getIstogl().booleanValue())
				throw new BusinessException("资产原值变更单已经生成凭证，不允许再次生成。");
		}
		if(vo.getIssettle() != null && vo.getIssettle().booleanValue())
			throw new BusinessException("资产原值变更单已经结账，不允许修改");
		
		vo.setIstogl(new DZFBoolean(istogl));
		vo.setPk_voucher(pk_voucher);
		singleObjectBO.update(vo,new String[]{"istogl","pk_voucher"});
	}

	@Override
	public void delete(ValuemodifyVO vo) throws DZFWarpException {
		checkDel(vo);
		singleObjectBO.deleteObject(vo);
		//反回写固定资产
		AssetcardVO assetvo = (AssetcardVO) singleObjectBO.queryVOByID(vo.getPk_assetcard(),
				AssetcardVO.class);
		IAssetCard assetCardImpl = (IAssetCard) SpringUtils.getBean("assetCardImpl");
		assetCardImpl.updateAssetMny(assetvo, vo.getOriginalvalue().doubleValue(),vo);
	}
	
	private void checkDel(ValuemodifyVO vo)throws DZFWarpException{
		if(vo.getIstogl() != null && vo.getIstogl().booleanValue()){
			throw new BusinessException("资产原值变更单已经生成凭证，不允许删除。");
		}
		if(vo.getIssettle() != null && vo.getIssettle().booleanValue())
			throw new BusinessException("资产原值变更单已经结账，不允许删除");
//		DZFDateTime ts = vo.getTs();
//		if(ts == null){
//			throw new BusinessException("请刷新数据。");
//		}
		String condition = " nvl(dr,0) = 0 and businessdate > ? and pk_corp=? and pk_assetcard = ?  and ts >to_date('"+vo.getTs().toString()+"', 'yyyy-mm-dd HH24:MI:SS')";
//		String condition = " nvl(dr,0) = 0 and businessdate > ? and pk_corp=? ";
		SQLParameter params = new SQLParameter();
		params.addParam(vo.getBusinessdate());
		params.addParam(vo.getPk_corp());
		params.addParam(vo.getPk_assetcard());
		ValuemodifyVO[] vos = queryByWhere(condition,params);
		if(vos != null && vos.length > 0){
			throw new BusinessException("请按顺序从后向前删除原值变更。");
		}
	}
	
	private ValuemodifyVO[] queryByWhere(String condition,SQLParameter params)throws DZFWarpException{
		ValuemodifyVO[] vos = (ValuemodifyVO[]) singleObjectBO.queryByCondition(ValuemodifyVO.class, condition, params);
		return vos;
	}
	
	
	@Override
	public TzpzHVO createTzpzVoById(String id, String coperatorid, DZFDate currDate) throws DZFWarpException {
		
		if(StringUtil.isEmpty(id)){
			throw new BusinessException("id信息不能为空");
		}
		
		//查询id
		ValuemodifyVO modifyvo = (ValuemodifyVO) singleObjectBO.queryByPrimaryKey(ValuemodifyVO.class, id);
		
		AssetcardVO assetcardvo = (AssetcardVO) singleObjectBO.queryByPrimaryKey(AssetcardVO.class, modifyvo.getPk_assetcard());
		
		if(modifyvo == null){
			throw new BusinessException("信息不存在!");
		}
		
		if(assetcardvo == null){
			throw new BusinessException("资产不存在!");
		}
		
		List<TzpzBVO> listbvos = createTzpzBvo(id,modifyvo,assetcardvo, modifyvo.getPk_corp());
		
		CorpVO corpvo = corpService.queryByPk(modifyvo.getPk_corp());
		
		DZFDouble sumvalue = SafeCompute.add(  listbvos.get(0).getJfmny(), listbvos.get(0).getDfmny());//,list
		
		TzpzHVO hvo =  createVoucher(corpvo, coperatorid, currDate, listbvos, sumvalue,id);
		
		return hvo;
	}
	
	private List<TzpzBVO> createTzpzBvo(String id,ValuemodifyVO modifyvo,AssetcardVO assetcardvo,String pk_corp){
		List<TzpzBVO> listbvo = new ArrayList<TzpzBVO>();
		
		DZFDouble ce = SafeCompute.sub(modifyvo.getOriginalvalue(), modifyvo.getNewvalue());
		
		if(ce.doubleValue() == 0){
			throw new BusinessException("暂无变化");
		}
		
		Map<String, YntCpaccountVO> cpamp  = accountService.queryMapByPk(pk_corp);
		
		String jfkm = modifyvo.getPk_bgkm();
		String dfkm = modifyvo.getPk_zckm();
		String zy ="资产卡片"+ assetcardvo.getAssetname()+"原值变更";
		
		if(!StringUtil.isEmpty(jfkm) && cpamp.containsKey(jfkm)){
			jfkm = cpamp.get(jfkm).getAccountcode();
		}
		if(!StringUtil.isEmpty(dfkm) && cpamp.containsKey(dfkm)){
			dfkm = cpamp.get(dfkm).getAccountcode();
		}
		
//		Integer corpschema = yntBoPubUtil.getAccountSchema(pk_corp);
		
//		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {// 2007会计准则
//			jfkm = "1606";
//			dfkm = "1601";
//		} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则
//			jfkm = "1606";
//			dfkm = "1601";
//		} else if (corpschema == DzfUtil.POPULARSCHEMA.intValue()) {// 民间
//			jfkm = "1509";
//			dfkm = "1501";
//		} else if(corpschema == DzfUtil.VILLAGECOLLECTIVE.intValue()){//村集体
//			jfkm = "1503";
//			dfkm = "1501";
//		}else if(corpschema ==  DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){//企业会计制度
//			jfkm = "1701";
//			dfkm = "1501";
//		} else {
//			throw new BusinessException("该该制度暂不支持利润表,敬请期待!");
//		}
		
		YntCpaccountVO[] cpacountvos =  accountService.queryByPk(pk_corp);
		
		TzpzBVO tzpzjf = new TzpzBVO();
		YntCpaccountVO jfaccountvo = getAccountVo(jfkm,cpacountvos);
		tzpzjf.setPk_accsubj(jfaccountvo.getPk_corp_account());
		tzpzjf.setZy(zy);
		tzpzjf.setVcode(jfaccountvo.getAccountcode());
		tzpzjf.setVname(jfaccountvo.getAccountname());
		tzpzjf.setKmmchie(jfaccountvo.getFullname());
		tzpzjf.setPk_currency("00000100AA10000000000BKT");
		if(ce.doubleValue()<0){//资产原值增加时，转总账生成凭证，结算科目在贷方
			tzpzjf.setDfmny(ce.multiply(-1));
			tzpzjf.setYbdfmny(ce.multiply(-1));
		}else{//资产原值减少时，结算科目在借方
			tzpzjf.setJfmny(ce);
			tzpzjf.setYbjfmny(ce);
		}
		tzpzjf.setPk_corp(pk_corp);
		tzpzjf.setDr(0);
		tzpzjf.setRowno(1);
		tzpzjf.setVdirect(0);
		
		
		TzpzBVO tzpzdf = new TzpzBVO();
		YntCpaccountVO dfaccountvo = getAccountVo(dfkm,cpacountvos);
		tzpzdf.setPk_accsubj(dfaccountvo.getPk_corp_account());
		tzpzdf.setZy(zy);
		tzpzdf.setVcode(dfaccountvo.getAccountcode());
		tzpzdf.setVname(dfaccountvo.getAccountname());
		tzpzdf.setKmmchie(dfaccountvo.getFullname());
		tzpzdf.setPk_currency("00000100AA10000000000BKT");
		if(ce.doubleValue()<0){
			tzpzdf.setJfmny(ce.multiply(-1));
			tzpzdf.setYbjfmny(ce.multiply(-1));
		}else{
			tzpzdf.setDfmny(ce);
			tzpzdf.setYbdfmny(ce);
		}
		tzpzdf.setPk_corp(pk_corp);
		tzpzdf.setDr(0);
		tzpzdf.setRowno(2);
		tzpzdf.setVdirect(1);
		
		if(ce.doubleValue()<0){
			listbvo.add(tzpzdf);
			listbvo.add(tzpzjf);
		}else{
			listbvo.add(tzpzjf);
			listbvo.add(tzpzdf);
		}
		
		return listbvo;
	}
	
	private YntCpaccountVO getAccountVo(String jfkm,YntCpaccountVO[] cpaccountvo) {
		for(YntCpaccountVO vo:cpaccountvo){
			if(vo.getIsleaf()!=null && vo.getIsleaf().booleanValue()){
				if(vo.getAccountcode().startsWith(jfkm)){
					return vo;
				}
			}
		}
		return null;
	}
	private TzpzHVO createVoucher(CorpVO corpvo, String coperatorid,
			DZFDate currDate, List<TzpzBVO> tzpzBVoList, DZFDouble Jfmny,String id)
			throws DZFWarpException {
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(corpvo.getPk_corp());
		headVO.setPzlb(Integer.valueOf(0));
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setCoperatorid(coperatorid);
		headVO.setDoperatedate(currDate);
		headVO.setPeriod(DateUtils.getPeriod(currDate));
		headVO.setVyear(currDate.getYear());
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(),
				headVO.getDoperatedate()));
		headVO.setSourcebilltype(IBillTypeCode.HP60);
		headVO.setSourcebillid(id);
		headVO.setVbillstatus(Integer.valueOf(8));
		headVO.setJfmny(Jfmny);
		headVO.setDfmny(Jfmny);

		TzpzBVO[] children = (TzpzBVO[]) tzpzBVoList
				.toArray(new TzpzBVO[tzpzBVoList.size()]);
		headVO.setChildren(children);

		return headVO;
	}

}

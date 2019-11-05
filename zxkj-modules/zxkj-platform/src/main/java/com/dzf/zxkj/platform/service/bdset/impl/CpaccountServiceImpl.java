package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.enums.KmschemaCash;
import com.dzf.zxkj.platform.model.CodeName;
import com.dzf.zxkj.platform.model.bdset.AccountTreeStrategy;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemPzShowVO;
import com.dzf.zxkj.platform.model.tax.TaxitemRelationVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.jzcl.impl.TerminalCurrSettleDMO;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.tax.ITaxitemsetService;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@SuppressWarnings("all")
@Service("gl_cpacckmserv")
public class CpaccountServiceImpl implements ICpaccountService {
	@Autowired
	private IReferenceCheck irefCheck;
	@Autowired
	private IPersonalSetService gl_gxhszserv;
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private ICpaccountCodeRuleService gl_accountcoderule;
	@Autowired
	private ITaxitemsetService sys_taxsetserv;
	@Autowired
	private SaveCpaccount kmzfz_SaveCpaccount;
	@Autowired
	private SaveCpaccount1 kmzfz_SaveCpaccount1;
	@Reference(version = "1.0.0")
	private IZxkjReportService zxkjReportService;

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IAccountService accountService;

	@Override
	public YntCpaccountVO saveNew(YntCpaccountVO svo) throws DZFWarpException {
		//保存税目zpm
		String shuimuids = svo.getShuimuid();
		saveShuimu(shuimuids,svo);
		return insertByCorpAccountVO(svo.getPk_corp(), svo);
	}
	//删除税目
	public void deleteshuimu(CorpVO rpvo, YntCpaccountVO svo){
		if(rpvo == null || svo == null)
			return;
		String sql = " delete from ynt_taxrelation where pk_corp = ? and subj_code = ?  ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(rpvo.getPk_corp());
		sp.addParam(svo.getAccountcode());
		singleObjectBO.executeUpdate(sql, sp);
	}
	
	public void saveShuimu(String shuimuids,YntCpaccountVO svo){
		if(svo == null)
			return;
		CorpVO rpvo = corpService.queryByPk(svo.getPk_corp());
		deleteshuimu(rpvo,svo);
		if(StringUtil.isEmpty(shuimuids))
			return;
		String[] strs = shuimuids.split(",");
		List<TaxitemRelationVO> list = new ArrayList<TaxitemRelationVO>();
		if(strs!=null && strs.length>0){
			for(String id : strs){
				if(!StringUtil.isEmpty(id)){
					TaxitemRelationVO vo = new TaxitemRelationVO();
					vo.setChargedeptname(rpvo.getChargedeptname());
					vo.setCorptype(rpvo.getCorptype());
					vo.setPk_corp(rpvo.getPk_corp());
					vo.setPk_taxitem(id);
					vo.setSubj_code(svo.getAccountcode());
					vo.setShuimushowpz(svo.getShuimushowpz());
					vo.setDr(0);
					list.add(vo);
				}
			}
		}
		if(list != null && list.size()>0){
			singleObjectBO.insertVOArr(rpvo.getPk_corp(), list.toArray(new TaxitemRelationVO[0]));
		}
	}

	/**
	 * 公司会计科目新增
	 */
	private YntCpaccountVO insertByCorpAccountVO(String pk_corp,
			YntCpaccountVO vo) throws DZFWarpException {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		check.checkAdd(vo);
		YntCpaccountVO parentVO = check.getParentVOByID(vo);
		
		if(parentVO == null)
			throw new BusinessException("当前新增科目，上级科目不存在！");
		
		if (check.checkParentVerification(vo)) {
			vo.setIsverification(DZFBoolean.TRUE);
		}
		parentVO.setIsleaf(DZFBoolean.FALSE);
		parentVO.setIsfzhs(AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT);
		//zpm
		parentVO.setIsnum(DZFBoolean.FALSE);
		parentVO.setIswhhs(DZFBoolean.FALSE);
		parentVO.setExc_crycode(null);
		parentVO.setExc_cryname(null);
		parentVO.setExc_pk_currency(null);
		parentVO.setFsefxkz(DZFBoolean.FALSE);//发生额控制
		parentVO.setYefxkz(DZFBoolean.FALSE);//余额方向控制
		parentVO.setIsverification(DZFBoolean.FALSE);//往来核销
		parentVO.setBunhdtz(DZFBoolean.FALSE);//不汇兑调整
		//
		if (vo.getShuilv() == null && parentVO.getShuilv() != null) {
			vo.setShuilv(parentVO.getShuilv());
		}
		parentVO.setShuilv(null);
		singleObjectBO.update(parentVO);
		// 新节点的是否末级为true
		vo.setIsleaf(DZFBoolean.TRUE);
		// 设置科目层次
		Integer cc = new Integer(parentVO == null ? 1 : parentVO
				.getAccountlevel().intValue() + 1);
		vo.setAccountlevel(cc);
		vo.setPk_corp(pk_corp);
		// 插入新节点到数据库
		if (parentVO.getBisseal().booleanValue()) {// 如果当前科目被封存，先解封
			parentVO.setBisseal(DZFBoolean.FALSE);
			singleObjectBO.update(parentVO, new String[] { "bisseal" });
		}
		boolean isQuote = checkIsQuote(vo);
		// 科目全称
		if (parentVO != null) {
			vo.setFullname(parentVO.getFullname() + "/" + vo.getAccountname());
		} 
		YntCpaccountVO resultvo = (YntCpaccountVO) singleObjectBO
				.saveObject(vo.getPk_corp(), vo);
		if (isQuote) {
			updateQuote(resultvo, parentVO);
		}
		return resultvo;
	}

	/**
	 * 增加第一个下级科目时，如果科目被引用，将业务数据转移到下级科目
	 * 
	 * @param vo
	 * @param parentVO
	 */
	private void updateQuote(YntCpaccountVO vo, YntCpaccountVO parentVO)
			throws DZFWarpException {
		// 成本模板
		String[] values = new String[] { vo.getPk_corp_account(),
				vo.getAccountcode(), vo.getAccountname(), vo.getPk_corp(),
				parentVO.getPk_corp_account() };
		String sqlUpdate = " update ynt_cpcosttrans set pk_debitaccount = ?,dvcode=?,dvname=? where pk_corp=? and pk_debitaccount=?";
		updateExecute(sqlUpdate, values);
		// 成本模板
		sqlUpdate = " update ynt_cpcosttrans set pk_creditaccount = ?,jvcode=?,jvname=? where pk_corp=? and pk_creditaccount=?";
		updateExecute(sqlUpdate, values);
		// 凭证常用模板
		updateExecuteTable("ynt_cppztemmb_b", values);
		// 科目期初
		// updateExecuteTable("ynt_qcye", values);
		insertKmqc(vo, parentVO);
		// 辅助期初
		updateExecuteTable("ynt_fzhsqc", values);
		// 科目期末结账查询
		updateExecuteTable("ynt_kmqmjz", values);
		// 折旧清理模板
		sqlUpdate = " update ynt_cpmb_b set pk_account = ?,vcode=?,vname=? where pk_corp=? and pk_account=?";
		updateExecute(sqlUpdate, values);
		// 凭证
		values = new String[] { vo.getPk_corp_account(), vo.getAccountcode(),
				vo.getAccountname(), vo.getAccountname(), vo.getFullname(), vo.getPk_corp(),
				parentVO.getPk_corp_account() };
		sqlUpdate = " update ynt_tzpz_b set pk_accsubj = ?,vcode=?,vname=?,subj_name=?,kmmchie=? where pk_corp=? and pk_accsubj=?";
		updateExecute(sqlUpdate, values);
		// 汇兑损益模板
		values = new String[] { vo.getPk_corp_account(), vo.getAccountcode(),
				vo.getPk_corp(), parentVO.getPk_corp_account() };
		sqlUpdate = " update ynt_remittance set pk_corp_account = ?,accountcode=? where pk_corp=? and pk_corp_account=?";
		updateExecute(sqlUpdate, values);
		// 汇兑损益模板
		sqlUpdate = " update ynt_remittance set pk_out_account = ?,outatcode=? where pk_corp=? and pk_out_account=?";
		updateExecute(sqlUpdate, values);
		// 期间损益模板
		sqlUpdate = " update ynt_cptransmb set pk_transferinaccount = ?,accountcode=? where pk_corp=? and pk_transferinaccount=?";
		updateExecute(sqlUpdate, values);
		// 现金流量
		values = new String[] { vo.getPk_corp_account(), vo.getPk_corp(),
				parentVO.getPk_corp_account() };
		sqlUpdate = " update ynt_xjll set pk_accsubj = ? where pk_corp=? and pk_accsubj=?";
		updateExecute(sqlUpdate, values);
//		// 商品
//		sqlUpdate = " update ynt_inventory set pk_subject = ? where pk_corp=? and pk_subject=?";
//		updateExecute(sqlUpdate, values);
//		// 库存期初
//		sqlUpdate = " update ynt_icbalance set pk_subject = ? where pk_corp=? and pk_subject=?";
//		updateExecute(sqlUpdate, values);
		// 资产与总账对照表
		values = new String[] { vo.getPk_corp_account(), vo.getAccountcode(),
				vo.getPk_corp(), parentVO.getPk_corp_account() };
		sqlUpdate = " update ynt_tdcheck set pk_glaccount = ?,accountcode=? where pk_corp=? and pk_glaccount=?";
		updateExecute(sqlUpdate, values);
		
		//库存期初
		values = new String[] { vo.getPk_corp_account(), vo.getPk_corp(),
				parentVO.getPk_corp_account() };
		sqlUpdate = " update ynt_icbalance set pk_subject = ? where pk_corp=? and pk_subject=?";
		updateExecute(sqlUpdate, values);
		//存货
		sqlUpdate = " update ynt_inventory set pk_subject = ? where pk_corp=? and pk_subject=?";
		updateExecute(sqlUpdate, values);
		//入库单
		sqlUpdate = " update ynt_ictradein set pk_subject = ? where pk_corp=? and pk_subject=?";
		updateExecute(sqlUpdate, values);
		//出库单
		sqlUpdate = " update ynt_ictradeout set pk_subject = ? where pk_corp=? and pk_subject=?";
		updateExecute(sqlUpdate, values);
		
		//工资表费用科目关系
		sqlUpdate = " update ynt_salarykmdept set ckjkmid = ? where pk_corp=? and ckjkmid=?";
		updateExecute(sqlUpdate, values);
		
	}

	private void updateExecuteTable(String tableName, String[] values)
			throws BusinessException {
		String sqlUpdate = " update "
				+ tableName
				+ " set pk_accsubj = ?,vcode=?,vname=? where pk_corp=? and pk_accsubj=?";
		SQLParameter params = new SQLParameter();
		for (String value : values) {
			params.addParam(value);
		}
		singleObjectBO.executeUpdate(sqlUpdate, params);
	}

	private void updateExecute(String sqlUpdate, String[] values)
			throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		for (String value : values) {
			params.addParam(value);
		}
		singleObjectBO.executeUpdate(sqlUpdate, params);
	}

	private void insertKmqc(YntCpaccountVO vo, YntCpaccountVO parentVO) {
		String condition = " pk_corp=? and pk_accsubj=? and nvl(dr,0)=0";
		SQLParameter params = new SQLParameter();
		params.addParam(parentVO.getPk_corp());
		params.addParam(parentVO.getPk_corp_account());
		QcYeVO[] qcyeVOs = (QcYeVO[]) singleObjectBO.queryByCondition(
				QcYeVO.class, condition, params);
		if (qcyeVOs != null && qcyeVOs.length > 0) {
			for(QcYeVO  qcye:qcyeVOs){//期初多币种
				QcYeVO qcyeVO = qcye;
				qcyeVO.setPk_accsubj(vo.getPk_corp_account());
				qcyeVO.setVcode(vo.getAccountcode());
				qcyeVO.setVname(vo.getAccountname());
				qcyeVO.setPk_qcye(null);
				qcyeVO.setCoperatorid(vo.getCoperatorid());
//				qcyeVO.setPk_currency(qcye.getPk_currency());
				singleObjectBO.saveObject(vo.getPk_corp(), qcyeVO);
			}
		}
		
		//更新未核销期初
		params.clearParams();
		condition = " update ynt_verify_begin set pk_accsubj = ? where pk_accsubj = ? and pk_corp =? ";
		params.addParam(vo.getPk_corp_account());
		params.addParam(parentVO.getPk_corp_account());
		params.addParam(vo.getPk_corp());
		singleObjectBO.executeUpdate(condition, params);
	}

	/**
	 * 公司会计科目修改
	 */
	@Override
	public void update(YntCpaccountVO vo, CorpVO corpvo)
			throws DZFWarpException {
		YntCpaccountVO svo = queryById(vo.getPk_corp_account());
		if (svo == null)
			throw new BusinessException("当前科目不存在或已被删除，请验证！");
		if (!svo.getPk_corp().equals(vo.getPk_corp()))
			throw new BusinessException("只能修改该公司权限范围内的数据");
		svo.setCoperatorid(vo.getCoperatorid());
		svo.setDoperatedate(vo.getDoperatedate());
		svo.setAccountkind(vo.getAccountkind());
		svo.setAccountname(vo.getAccountname());
		svo.setCurrname(vo.getCurrname());
		svo.setDirection(vo.getDirection());
		svo.setIsadjust(vo.getIsadjust());
		svo.setIsnum(vo.getIsnum());
		svo.setIswhhs(vo.getIswhhs());
		svo.setMeasurename(vo.getMeasurename());
		svo.setIsfzhs(vo.getIsfzhs());
		svo.setFsefxkz(vo.getFsefxkz());
		svo.setYefxkz(vo.getYefxkz());
		svo.setIsverification(vo.getIsverification());
		svo.setShuilv(vo.getShuilv());
		svo.setShuimuid(vo.getShuimuid());
		svo.setShuimu1(vo.getShuimu1());
		svo.setShuimushowpz(vo.getShuimushowpz());
		svo.setBunhdtz(vo.getBunhdtz());//不进行汇兑调整
		svo.setBuncashkm(vo.getBuncashkm());//非现金类科目
		if ((svo.getIsleaf() == null || !svo.getIsleaf().booleanValue())
				&& vo.getShuilv() != null && vo.getShuilv().doubleValue() != 0) {
			throw new BusinessException("该科目为非末级科目，不允许设置税率");
		}
		if (!svo.getIsnum().booleanValue()) {
			svo.setMeasurename(null);
		}
		if (svo.getIsnum() != null && svo.getIsnum().booleanValue()) {
			// 是否允许数量为空
//			svo.setAllow_empty_num(vo.getAllow_empty_num());
			if (!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
//				if (svo.getMeasurename() == null
//						|| svo.getMeasurename().equals("")) {
//					throw new BusinessException("请设置具体的计量单位!");
//				}
			} else {
				String kmcode = svo.getAccountcode();
				////库存模式。 1、代表 由库存推总账。 
				////          0、或者没有代表由总账推库存。
				//针对老模式库存依旧控制。1403、1405必须为末级科目。
				if(corpvo.getIbuildicstyle() == null ||  corpvo.getIbuildicstyle() == 0){
					if ("1403".equals(kmcode.substring(0, 4))
							|| "1405".equals(kmcode.substring(0, 4))) {
						if (kmcode.length() > 4)
							throw new BusinessException("当前公司启用进销存!");
					}
				}
			}
		} else {
//			svo.setAllow_empty_num(null);
		}
//		checkFsye(svo,corpvo); 移到action中
		if (svo.getIswhhs() != null && svo.getIswhhs().booleanValue()
				&& vo.getExc_pk_currency() != null
				&& vo.getExc_pk_currency().length() > 0) {
			svo.setExc_pk_currency(vo.getExc_pk_currency());
			svo.setExc_crycode(vo.getExc_crycode());
			svo.setExc_cryname(vo.getExc_cryname());
		} else {
			svo.setIswhhs(DZFBoolean.FALSE);
			svo.setExc_pk_currency(null);
			svo.setExc_crycode(null);
			svo.setExc_cryname(null);
		}
		// 公司会计科目更新前数据检查
		CpaccountServiceCheck2 check2 = new CpaccountServiceCheck2(
				singleObjectBO);
		if (!"22210101".equals(svo.getAccountcode())
				&& !"进项税额".equals(svo.getAccountname())) {
			check2.checkUpdate(svo);
		}
		// 科目全称
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		YntCpaccountVO parentVO = check.getParentVOByID(svo);
		if ((svo.getIsverification() == null || !svo.getIsverification().booleanValue())
				&& check.checkParentVerification(svo)) {
			throw new BusinessException("上级科目已设置往来核销，请先取消上级科目往来核销！");
		}
		updateChildrenVerify(svo);
		if (parentVO != null) {
			svo.setFullname(parentVO.getFullname() + "/" + svo.getAccountname());
		} else {
			svo.setFullname(svo.getAccountname());
		}
		//同时更新税目表信息zpm
		String shuimuids = svo.getShuimuid();
		saveShuimu(shuimuids,svo);
		//zpm
		singleObjectBO.update(svo);
		updateRefCapname(svo);
	}

	@Override
	public String checkFsye(YntCpaccountVO vo, CorpVO corpvo) {
		Map<String, YntCpaccountVO> map = accountService.queryMapByPk(corpvo.getPk_corp());
		YntCpaccountVO oldvo = map.get(vo.getPk_corp_account());
		if (oldvo == null)
			return null;

		String isfzhs = vo.getIsfzhs();
		String oisfzhs = oldvo.getIsfzhs();
		if (isfzhs.equals(oisfzhs)) {
			return null;
		} else { //保持原来的代码  新加辅助情况

			//对修改后取消的辅助进行校验 追加的不处理
			int len = isfzhs.length();
			List<Integer> cancelFzhs = new ArrayList();
			for (int i = 0; i < len; i++) {
				char ochar = oisfzhs.charAt(i);
				char schar = isfzhs.charAt(i);
				if (ochar > schar) {
					cancelFzhs.add(i);
				}
			}

			if("0000000000".equals(oisfzhs) || cancelFzhs.isEmpty()){
				boolean  isye =false;
				QueryParamVO qvo = new QueryParamVO();
				qvo.setIsnomonthfs(DZFBoolean.TRUE);
				qvo.setXswyewfs(DZFBoolean.FALSE);// 无余额无发生不显示
				qvo.setBtotalyear(DZFBoolean.TRUE);
				qvo.setIshowfs(DZFBoolean.TRUE);// 有余额无发生也显示
				qvo.setIshasjz(DZFBoolean.FALSE);
				qvo.setIshassh(DZFBoolean.FALSE);
				qvo.setKms_first(vo.getAccountcode());
				qvo.setKms_last(vo.getAccountcode());
				qvo.setBegindate1(corpvo.getBegindate());
				DZFDate ddate = new DZFDate();
				qvo.setQjq(DateUtils.getPeriod(ddate));
				long nextyear = DateUtils.getNextYear(ddate.getMillis());
				DZFDate enddate = new DZFDate(nextyear);
				qvo.setEnddate(enddate);
				qvo.setQjz(DateUtils.getPeriod(enddate));
				qvo.setPk_corp(corpvo.getPk_corp());
				FseJyeVO[] fsejyevos = zxkjReportService.getFsJyeVOs(qvo, 1);
				if (fsejyevos != null && fsejyevos.length > 0) {
					FseJyeVO fse = fsejyevos[0];
					DZFDouble temp = DZFDouble.ZERO_DBL;
					if ("借".equals(fse.getFx())) {
						temp = fse.getQmjf();
					} else {
						temp = fse.getQmdf();
					}
					if (temp != null && temp.doubleValue() != 0) {
						isye =true;
					}
				}
				if(isye && "0000000000".equals(oisfzhs)){ //无辅助核算的科目：启用辅助（1或多个）
					throw new
							BusinessException(vo.getAccountname()+"科目有余额，请先【增加】下级科目再操作【科目下级转辅助】"
							+ "或先通过做凭证将余额转到其他科目，启用辅助后再通过凭证转回来。");
				}

				if(isye && cancelFzhs.isEmpty()){ //有一个或多个辅助核算的科目追加辅助项
					return "科目有余额，追加辅助项后若调整余额请自行通过做凭证调整。";
				}
			}else{ //有辅助核算的科目减少一个、多个或全部辅助核算项
				//查询该科目的辅助期初
				List<FzhsqcVO> fzhsqcVOList = queryFzhsqcListByCorpAndAccount(corpvo.getPk_corp(), vo.getPk_corp_account());
				//查询该科目的所有凭证
				List<TzpzBVO> tzpzlist = queryTzpzListByCorpAndAccount(corpvo.getPk_corp(), vo.getPk_corp_account());
				//存储辅助期初余额
				DZFDouble[] fzhsYe = new DZFDouble[10];

				for(FzhsqcVO fzhsqcVO : fzhsqcVOList){
					for(Integer i : cancelFzhs){
						//判断该辅助是否设置期初余额
						String value = (String)fzhsqcVO.getAttributeValue("fzhsx"+(i+1));
						if(!StringUtil.isEmpty(value)){
							//取本月期初余额
							fzhsYe[i] = SafeCompute.add(fzhsYe[i],fzhsqcVO.getThismonthqc());
						}
					}
				}

				for(TzpzBVO tzpzBVO : tzpzlist){
					for(Integer i : cancelFzhs){
						String value = (String)tzpzBVO.getAttributeValue("fzhsx"+(i+1));
						if(!StringUtil.isEmpty(value)){
							//计算最终余额
							if(vo.getDirection() == 0){
								fzhsYe[i] = SafeCompute.add(fzhsYe[i],tzpzBVO.getJfmny());
								fzhsYe[i] = SafeCompute.sub(fzhsYe[i],tzpzBVO.getDfmny());
							}else{
								fzhsYe[i] = SafeCompute.add(fzhsYe[i],tzpzBVO.getDfmny());
								fzhsYe[i] = SafeCompute.sub(fzhsYe[i],tzpzBVO.getJfmny());
							}
						}
					}
				}

				Iterator<Integer> iterator = cancelFzhs.iterator();
				//计算取消的辅助哪些有余额
				while(iterator.hasNext()){
					Integer index = iterator.next();
					if(fzhsYe[index] == null || fzhsYe[index].equals(DZFDouble.ZERO_DBL)){
						iterator.remove();
					}
				}
				if(!cancelFzhs.isEmpty()){

					String codeStr = "";

					for(int i = 0; i < cancelFzhs.size(); i++){
						codeStr +="'"+(cancelFzhs.get(i)+1)+"'";
						if(i == cancelFzhs.size() -1){
							continue;
						}
						codeStr += ",";
					}
					//查询辅助名称
					SQLParameter sp = new SQLParameter();
					sp.addParam(corpvo.getPk_corp());
					sp.addParam(IDefaultValue.DefaultGroup);
					List<String> fzhs = (List) singleObjectBO.executeQuery(
							"select name from ynt_fzhs_h where (pk_corp = ? or pk_corp = ?) and code in ("+codeStr+") and nvl(dr,0)=0 ",
							sp, new ColumnListProcessor("name"));

					throw new BusinessException("请先将要取消的\"" +fzhs+"\"辅助余额通过做凭证转走再取消该辅助项");
				}

			}
		}
		return null;
	}
	/*
		获取辅助核算期初
		gzx
	 */
	private List<FzhsqcVO> queryFzhsqcListByCorpAndAccount(String pk_corp, String pk_corp_account){
		String sql = "select a.pk_accsubj, a.thismonthqc, a.fzhsx1, a.fzhsx2, a.fzhsx3, a.fzhsx4, a.fzhsx5, a.fzhsx6, a.fzhsx7, a.fzhsx8, a.fzhsx9, a.fzhsx10  from ynt_fzhsqc a where a.pk_corp = ? and pk_accsubj = ? and nvl(a.dr, 0) = 0 and a.thismonthqc is not null and a.thismonthqc <> 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp_account);
		return (List<FzhsqcVO>) singleObjectBO.executeQuery(
				sql, sp, new BeanListProcessor(FzhsqcVO.class));
	}
	/*
		获取凭证发生额
		gzx
	 */
	private List<TzpzBVO> queryTzpzListByCorpAndAccount(String pk_corp, String pk_corp_account){
		String sql = "select * from ynt_tzpz_b where nvl(dr,0)=0 and pk_corp = ? and pk_accsubj = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp_account);
		return (List<TzpzBVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(TzpzBVO.class));
	}

	/**
	 * 公司会计科目删除
	 */
	@Override
	public void deleteInfovo(YntCpaccountVO bean) throws DZFWarpException {
		YntCpaccountVO svo = queryById(bean.getPk_corp_account());
		if (svo == null)
			throw new BusinessException("当前科目不存在或已被删除，请验证！");
		if (!svo.getPk_corp().equals(bean.getPk_corp()))
			throw new BusinessException("只能删除该公司权限范围内的数据");
		CpaccountServiceCheck3 check = new CpaccountServiceCheck3(
				singleObjectBO);
		check.checkDelete(svo);
		CorpVO rpvo = corpService.queryByPk(svo.getPk_corp());
		// 删除本级
		singleObjectBO.deleteObject(svo);
		YntCpaccountVO parentvo = check.getParentVOByID(svo);
		boolean ishaschild = check.hasChildren(parentvo.getPk_corp(), parentvo);
		if (!ishaschild) {
			parentvo.setIsleaf(DZFBoolean.TRUE);
			singleObjectBO.update(parentvo, new String[] { "isleaf" });
		}
		//如果有期初，删除期初,包含辅助期初余额数据
		SQLParameter sp = new SQLParameter();
		String updatesql1 = "update ynt_qcye set dr =1 where pk_accsubj = ? and nvl(dr,0)=0 and pk_corp = ?";
		sp.addParam(bean.getPk_corp_account());
		sp.addParam(bean.getPk_corp());
		int kmqccount = singleObjectBO.executeUpdate(updatesql1, sp);
		
		String updatesql2 = "update ynt_fzhsqc set dr =1 where pk_accsubj = ? and nvl(dr,0)=0 and pk_corp = ? ";
		sp.clearParams();
		sp.addParam(bean.getPk_corp_account());
		sp.addParam(bean.getPk_corp());
		int fzqccount = singleObjectBO.executeUpdate(updatesql2, sp);
		//删除税目
		deleteshuimu(rpvo,svo);
	}

	/**
	 * 封存
	 */
	@Override
	public void updateSeal(YntCpaccountVO vo) throws DZFWarpException {
		YntCpaccountVO svo = queryById(vo.getPk_corp_account());
		String newrule = queryAccountRule(vo.getPk_corp());
		if (svo == null)
			throw new BusinessException("当前科目不存在或已被删除，请验证！");
		updateSealByCorpAccountVO(svo.getPk_corp(), svo, newrule);
	}

	private void updateSealByCorpAccountVO(String pk_corp, YntCpaccountVO vo,
			String newrule) throws DZFWarpException {
		// 往下-down
		String accountcode = vo.getAccountcode();
		if (vo.getIsleaf() != null && vo.getIsleaf().booleanValue()) {// 是末级
			vo.setBisseal(DZFBoolean.TRUE);
			singleObjectBO.update(vo);
		} else {// 非末级
			String downqrysql = "select * from YNT_CPACCOUNT where accountcode like ? and pk_corp=?  and nvl(dr,0)=0 and nvl(bisseal,'N')='N'";// 包括本次
			SQLParameter sp = new SQLParameter();
			sp.addParam(accountcode + "%");
			sp.addParam(pk_corp);
			List<YntCpaccountVO> listdownvo = (List<YntCpaccountVO>) singleObjectBO
					.executeQuery(downqrysql, sp,
							new BeanListProcessor(YntCpaccountVO.class));
			for (YntCpaccountVO downvo : listdownvo) {
				downvo.setBisseal(DZFBoolean.TRUE);
			}
			singleObjectBO.updateAry(
					listdownvo.toArray(new YntCpaccountVO[0]));
		}
		// 往上-up--递归更新
		if (vo.getAccountlevel() > 1) {
			updateUpSealVO(vo, newrule);
		}
	}

	private void updateUpSealVO(YntCpaccountVO vo, String newrule)
			throws DZFWarpException {
		String accountvalue = DZfcommonTools.getParentCode(vo.getAccountcode(),
				newrule);// 取上级的值
		// 获取对应的上级vo
		String upqrysql = "select * from YNT_CPACCOUNT where accountcode like ? and pk_corp= ?  and nvl(dr,0)=0 and nvl(bisseal,'N')='N' order by accountcode";// 包括本次
		SQLParameter sp = new SQLParameter();
		sp.addParam(accountvalue + "%");
		sp.addParam(vo.getPk_corp());
		List<YntCpaccountVO> listupvo = (List<YntCpaccountVO>) singleObjectBO
				.executeQuery(upqrysql, sp,
						new BeanListProcessor(YntCpaccountVO.class));
		if (listupvo != null && listupvo.size() == 1) {// 是否父节点是空的话则继续迭代
			listupvo.get(0).setBisseal(DZFBoolean.TRUE);
			singleObjectBO.update(listupvo.get(0),
					new String[] { "bisseal" });
			updateUpSealVO(listupvo.get(0), newrule);
		} else {
			return;
		}
	}

	/**
	 * 取消封存
	 */
	@Override
	public void updateUnSeal(YntCpaccountVO vo) throws DZFWarpException {
		YntCpaccountVO svo = queryById(vo.getPk_corp_account());
		String newrule = queryAccountRule(vo.getPk_corp());
		if (svo == null)
			throw new BusinessException("当前科目不存在或已被删除，请验证！");
		updateCancelSealByCorpAccountVO(svo.getPk_corp(), svo, newrule);
	}

	private void updateCancelSealByCorpAccountVO(String pk_corp,
			YntCpaccountVO vo, String newrule) throws DZFWarpException {
		// 往下-down
		String accountcode = vo.getAccountcode();
		if (vo.getIsleaf() != null && vo.getIsleaf().booleanValue()) {// 是末级
			vo.setBisseal(DZFBoolean.FALSE);
			singleObjectBO.update(vo);
		} else {// 非末级
			String downqrysql = "select * from YNT_CPACCOUNT where accountcode like ? and pk_corp=?  and nvl(dr,0)=0 ";// 包括本次
			SQLParameter sp = new SQLParameter();
			sp.addParam(accountcode + "%");
			sp.addParam(pk_corp);
			List<YntCpaccountVO> listdownvo = (List<YntCpaccountVO>) singleObjectBO
					.executeQuery(downqrysql, sp,
							new BeanListProcessor(YntCpaccountVO.class));
			for (YntCpaccountVO downvo : listdownvo) {
				downvo.setBisseal(DZFBoolean.FALSE);
			}
			singleObjectBO.updateAry(
					listdownvo.toArray(new YntCpaccountVO[0]));
		}
		// 往上-up--递归更新
		if (vo.getAccountlevel() > 1) {
			updateUpCancelSealVO(vo, newrule);
		}
	}

	private void updateUpCancelSealVO(YntCpaccountVO vo, String newrule)
			throws DZFWarpException {
		String accountvalue = DZfcommonTools.getParentCode(vo.getAccountcode(),
				newrule);// 取上级的值
		// 获取对应的上级vo
		String upqrysql = "select * from YNT_CPACCOUNT where accountcode =? and pk_corp=? and nvl(dr,0)=0  order by accountcode";// 包括本次
		SQLParameter sp = new SQLParameter();
		sp.addParam(accountvalue);
		sp.addParam(vo.getPk_corp());
		List<YntCpaccountVO> listupvo = (List<YntCpaccountVO>) singleObjectBO
				.executeQuery(upqrysql, sp,
						new BeanListProcessor(YntCpaccountVO.class));
		if (listupvo != null && listupvo.size() == 1) {// 是否父节点是空的话则继续迭代
			listupvo.get(0).setBisseal(DZFBoolean.FALSE);
			singleObjectBO.update(listupvo.get(0),
					new String[] { "bisseal" });
			updateUpCancelSealVO(listupvo.get(0), newrule);
		} else {
			return;
		}
	}

	@Override
	public YntCpaccountVO queryById(String id) throws DZFWarpException {
		YntCpaccountVO vo = null;
		vo = (YntCpaccountVO) singleObjectBO.queryVOByID(id,
				YntCpaccountVO.class);
		return vo;
	}

	@Override
	public YntCpaccountVO[] queryAccountVOS(String pk_corp, Integer acckind)
			throws DZFWarpException {
		YntCpaccountVO[] vos = accountService.queryByPk(pk_corp,
				acckind);
		for (YntCpaccountVO vo : vos) {
			vo.setChildren(null);
		}
		YntCpaccountVO vo = (YntCpaccountVO) BDTreeCreator.createTree(vos,
				new AccountTreeStrategy(queryAccountRule(pk_corp)));
		YntCpaccountVO[] bodyvos = (YntCpaccountVO[]) DZfcommonTools
				.convertToSuperVO(vo.getChildren());
		return bodyvos;
	}

	@Override
	public YntCpaccountVO[] queryAccountVOSByCorp(String pk_corp,
			Integer acckind) throws DZFWarpException {
		YntCpaccountVO[] vos = accountService.queryByPk(pk_corp,
				acckind);
		int len = vos == null ? 0 : vos.length;
		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();
		for (int i = 0; i < len; i++) {
			if (vos[i].getBisseal() == null
					|| vos[i].getBisseal().booleanValue() == false)
				list.add(vos[i]);
		}
		if(len > 0){
			for (YntCpaccountVO vo : vos) {
				vo.setChildren(null);
			}
		}
		
		vos = doIteratorVO(list, pk_corp);
		return vos;
	}

	@Override
	public YntCpaccountVO[] queryAccountByPz(String pk_corp)
			throws DZFWarpException {
		YntCpaccountVO[] vosCache = accountService.queryByPk(pk_corp);
		GxhszVO gxh = gl_gxhszserv.query(pk_corp);
		Integer kmShow = gxh.getPzSubject();
		
		int length = vosCache.length;
		YntCpaccountVO[] vos = new YntCpaccountVO[length];
		for (int i = 0; i < length; i++) {
			YntCpaccountVO vo = (YntCpaccountVO) vosCache[i].clone();
			String fullname = vo.getFullname();
			if (kmShow == 0) {
				fullname = vo.getAccountname();
			} else if (kmShow == 1) {
				String[] fullnameArr = fullname.split("/");
				int namelength = fullnameArr.length;
				if (namelength > 1) {
					fullname = fullnameArr[0] + "/" + vo.getAccountname();
				}
			}
			vo.setFullname(fullname);
			vos[i] = vo;
		}
		return vos;
	}

	private YntCpaccountVO[] doIteratorVO(List<YntCpaccountVO> list,
			String pk_corp) {
		YntCpaccountVO vo = (YntCpaccountVO) BDTreeCreator.createTree(list
				.toArray(new YntCpaccountVO[0]), new AccountTreeStrategy(
				queryAccountRule(pk_corp)));
		YntCpaccountVO[] bodyvos = (YntCpaccountVO[]) DZfcommonTools
				.convertToSuperVO(vo.getChildren());
		return bodyvos;
	}

	@Override
	public boolean checkIsQuote(YntCpaccountVO vo) throws DZFWarpException {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		return check.checkIsQuote(vo);
	}

	@Override
	public boolean checkQCYE(YntCpaccountVO vo) throws DZFWarpException {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		return check.checkQCYE(vo);
	}

	@Override
	public boolean checkFzhsQCYE(YntCpaccountVO vo) throws DZFWarpException {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		return check.checkFzhsQCYE(vo);
	}

	@Override
	public boolean checkIsPzRef(YntCpaccountVO vo) throws DZFWarpException {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		return check.checkIsPz(vo.getPk_corp(), vo);
	}
	
	public String queryCurrencyRef(YntCpaccountVO vo) throws DZFWarpException{
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(vo.getPk_corp_account());
		sqlp.addParam(vo.getPk_corp());
		
		//期初辅助核算引用 的币种
		String sql1 = " select distinct pk_currency from ynt_fzhsqc where pk_accsubj = ? and pk_corp = ? and thismonthqc is not null and thismonthqc <> 0 and nvl(dr,0)=0 ";
		//期初引用 的币种
		String sql2 = " select distinct pk_currency from ynt_qcye where pk_accsubj = ? and pk_corp = ? and thismonthqc is not null and thismonthqc <> 0 and nvl(dr,0)=0 ";
		//凭证引用的币种
		String sql3 =  " select distinct pk_currency from ynt_tzpz_b where pk_accsubj= ? and pk_corp = ? and nvl(dr,0) = 0 ";
		
		List<Object[]> list1 = (List<Object[]>) singleObjectBO.executeQuery(sql1, sqlp, new ArrayListProcessor());
		List<Object[]> list2 = (List<Object[]>) singleObjectBO.executeQuery(sql2, sqlp, new ArrayListProcessor());
		List<Object[]> list3 = (List<Object[]>) singleObjectBO.executeQuery(sql3, sqlp, new ArrayListProcessor());
		
		Set<String> set = new HashSet<String>();
		if(list1 != null && list1.size() > 0){
			for(Object[] obj : list1){
				set.add((String) obj[0]);
			}
		}
		if(list2 != null && list2.size() > 0){
			for(Object obj[] : list2){
				set.add((String) obj[0]);
			}
		}
		if(list3 != null && list3.size() > 0){
			for(Object[] obj : list3){
				set.add((String) obj[0]);
			}
		}
		
		StringBuffer sf = new StringBuffer();
		for(String s : set){
			sf.append(s).append(";");
		}
		
		return sf.toString();
	}

	// 同步行业科目方案，如果科目编码被占用，则此类数据不能同步。
	@Override
	public void saveSyncTdAccount(CorpVO cvo) throws DZFWarpException {
		SQLParameter sq = new SQLParameter();
		// sq.addParam(cvo.getPk_corp());
		sq.addParam(cvo.getCorptype());
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_tdacc cc  ");
		// sf.append(" where not exists (select 1 from ynt_cpaccount cp where nvl(cp.dr,0) = 0 and cp.pk_corp = ? and cc.accountcode = cp.accountcode) ");
		sf.append(" where cc.pk_trade_accountschema = ? and nvl(cc.dr,0) = 0 order by cc.accountcode ");
		List<BdTradeAccountVO> list = (List<BdTradeAccountVO>) singleObjectBO
				.executeQuery(sf.toString(), sq,
						new BeanListProcessor(BdTradeAccountVO.class));
		if (list == null || list.size() == 0)
			return;
		// 科目编码被占用，则此类数据不能同步。
		YntCpaccountVO[] cpavos = accountService.queryByPk(
				cvo.getPk_corp());
		List<String> codelist = new ArrayList<String>();
		for (YntCpaccountVO cpavo : cpavos) {
			codelist.add(cpavo.getAccountcode());
		}
		String coderule = queryAccountRule(cvo.getPk_corp());
		for (BdTradeAccountVO cpavo : list) {
			String accountcode = gl_accountcoderule.getNewRuleCode(
					cpavo.getAccountcode(), DZFConstant.ACCOUNTCODERULE,
					coderule);
			cpavo.setAccountcode(accountcode);
		}
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		List<YntCpaccountVO> insertVOs = new ArrayList<YntCpaccountVO>();
		List<YntCpaccountVO> updateVOs = new ArrayList<YntCpaccountVO>();
		for (int i = 0, size = list.size(); i < size; i++) {
			if (codelist.contains(list.get(i).getAccountcode())) {
				continue;
			}
			YntCpaccountVO vo = covertCPvo(list.get(i), cvo.getPk_corp());
			// 取上级vo
			YntCpaccountVO pvo = check.getParentVOByID(vo);
			insertVOs.add(vo);
			if (pvo != null) {
				pvo.setIsleaf(DZFBoolean.FALSE);
				updateVOs.add(pvo);
			}
		}
		singleObjectBO.insertVOWithPK(cvo.getPk_corp(), insertVOs.toArray(new YntCpaccountVO[0]));
		singleObjectBO.updateAry(updateVOs.toArray(new YntCpaccountVO[0]), new String[] { "isleaf" });
	}

	private YntCpaccountVO covertCPvo(BdTradeAccountVO vo2, String pk_corp)
			throws DZFWarpException {
		if (vo2 == null)
			return null;
		String[] attrNames = vo2.getAttributeNames();
		YntCpaccountVO acorpVO = new YntCpaccountVO();
		// 修改公司
		for (String attName : attrNames) {
			if ("children".equals(attName) || "dr".equals(attName))
				continue;
			if ("pk_trade_account".equals(attName) || "".equals(attName)
					|| attName == null)
				continue;
			if (attName.equals("pk_trade_accountschema")) {
				acorpVO.setPk_corp_accountschema(vo2
						.getPk_trade_accountschema());
			} else {
				acorpVO.setAttributeValue(attName,
						vo2.getAttributeValue(attName));
			}
		}
		acorpVO.setIssyscode(DZFBoolean.TRUE);
		acorpVO.setPk_corp(pk_corp);
		return acorpVO;
	}

	/**
	 * 修改科目名称，被引用的业务数据同时做修改
	 * 
	 * @param vo
	 * @throws BusinessException
	 */
	private void updateRefCapname(YntCpaccountVO vo) throws DZFWarpException {
		// 成本模板
		String[] values = new String[] { vo.getAccountname(), vo.getPk_corp(),
				vo.getPk_corp_account() };
		String sqlUpdate = " update ynt_cpcosttrans set dvname=? where pk_corp=? and pk_debitaccount=?";
		updateExecute(sqlUpdate, values);
		// 成本模板
		sqlUpdate = " update ynt_cpcosttrans set jvname=? where pk_corp=? and pk_creditaccount=?";
		updateExecute(sqlUpdate, values);

		sqlUpdate = " update ynt_cppztemmb_b set vname=? where pk_corp=? and pk_accsubj=?";
		updateExecute(sqlUpdate, values);
		// 科目期初
		sqlUpdate = " update ynt_qcye set vname=? where pk_corp=? and pk_accsubj=?";
		updateExecute(sqlUpdate, values);
		// 科目期末结账查询
		sqlUpdate = " update ynt_kmqmjz set vname=? where pk_corp=? and pk_accsubj=?";
		updateExecute(sqlUpdate, values);
		// 折旧清理模板
		sqlUpdate = " update ynt_cpmb_b set vname=? where pk_corp=? and pk_account=?";
		updateExecute(sqlUpdate, values);
		// 凭证
		values = new String[] { vo.getAccountname(), vo.getAccountname(), vo.getFullname(),
				vo.getPk_corp(), vo.getPk_corp_account() };
		sqlUpdate = " update ynt_tzpz_b set vname=?, subj_name=?, kmmchie=? where pk_corp=? and pk_accsubj=?";
		updateExecute(sqlUpdate, values);
		
		//业务类型模板
		values = new String[] { vo.getAccountname(), vo.getPk_corp(), vo.getPk_corp_account() };
		sqlUpdate = " update ynt_dcmodel_b set kmmc= ? where pk_corp= ? and pk_accsubj = ?";
		updateExecute(sqlUpdate, values);
	}

	/**
	 * 查询对应的科目编码规则
	 */
	@Override
	public String queryAccountRule(String pk_corp) throws DZFWarpException {

		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("查询科目编码时:公司信息不能为空!");
		}
		String kmrulesql = "select accountcoderule from bd_corp where pk_corp = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<String> kmrulelist = (List<String>) singleObjectBO.executeQuery(
				kmrulesql, sp, new ColumnListProcessor());
		if (kmrulelist == null || kmrulelist.size() == 0) {
			return DZFConstant.ACCOUNTCODERULE;
		} else {
			if (StringUtil.isEmpty(kmrulelist.get(0))) {
				return DZFConstant.ACCOUNTCODERULE;
			}
			return kmrulelist.get(0);
		}
	}

	@Override
	public boolean checkParentVerification(YntCpaccountVO vo)
			throws DZFWarpException {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		return check.checkParentVerification(vo);
	}
	//同步更新下级科目往来核销字段
	private void updateChildrenVerify (YntCpaccountVO vo) {
		DZFBoolean isverification = vo.getIsverification() == null ? DZFBoolean.FALSE : vo.getIsverification();
		String sql = " update ynt_cpaccount set isverification = ? where pk_corp = ? and nvl(dr, 0) = 0 and accountcode like ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(isverification);
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getAccountcode() + "%");
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public Map<String, List<YntCpaccountVO>> queryAccountVO(String userid,String pk_corp, String isShowFC) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String FCParam = "";
		if(!StringUtils.equals(isShowFC, "true")){
			FCParam = " and nvl(bisseal,'N')='N'";
		}
		Collection<YntCpaccountVO> c =  singleObjectBO.retrieveByClause(YntCpaccountVO.class, " pk_corp = ? and nvl(dr,0) = 0 "+FCParam,"accountcode", sp);
		if(c == null || c.size() == 0)
			return null;
		YntCpaccountVO[] vos = c.toArray(new YntCpaccountVO[0]);
//		//查询科目的税目信息，进行合并
//		List<TaxitemPzShowVO> taxitemvos = sys_taxsetserv.queryKMShow(userid, pk_corp);
//		Map<String,List<TaxitemPzShowVO>> map = DZfcommonTools.hashlizeObject(taxitemvos, new String[]{"pk_accsubj"});
//		mergeshuimu(vos,map);
		if(vos == null || vos.length == 0)
			return null;
		YntCpaccountVO vo = (YntCpaccountVO) BDTreeCreator.createTree(vos, new AccountTreeStrategy(queryAccountRule(pk_corp)));
		if(vo == null)
			return null;
		vos = (YntCpaccountVO[])vo.getChildren();
		if(vos == null || vos.length == 0)
			return null;
		List<YntCpaccountVO> abc = new ArrayList<YntCpaccountVO>(Arrays.asList(vos));
		Map<String, List<YntCpaccountVO>> maps = DZfcommonTools.hashlizeObject(abc,new String[] { "accountkind" });
		return maps;
	}
	
	private void mergeshuimu(YntCpaccountVO[] vos,Map<String,List<TaxitemPzShowVO>> map)throws DZFWarpException {
		if(vos == null || vos.length == 0 || map == null || map.size() ==0)
			return;
		String id = null;
		String name=null;
		for(YntCpaccountVO vo : vos){
			id = null;
			name=null;
//			if(StringUtil.isEmpty(vo.getShuimuid())
//					&& map.containsKey(vo.getPk_corp_account())){
			if(map.containsKey(vo.getPk_corp_account())){//以ynt_taxrelation表中的数据为准
				List<TaxitemPzShowVO> list = map.get(vo.getPk_corp_account());
				if(list == null || list.size() == 0)
					continue;
				for(int i = 0 ;i < list.size() ;i++){
					TaxitemPzShowVO v2 = list.get(i);
					if(i==0){
						id = v2.getPk_taxitem();
						name =v2.getShortname();
					}else{
						id = id+","+v2.getPk_taxitem();
						name =name+","+v2.getShortname();
					}
				}
				vo.setShuimu1(name);
				vo.setShuimuid(id);
				vo.setShuimushowpz(list.get(0).getShuimushowpz());
			}
		}
	}
	
	@Override
	public List<CodeName> queryNoleafKm(String userid, String pk_corp)throws DZFWarpException {
		if(StringUtil.isEmpty(userid) || StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = " select pk_corp_account id,accountcode code,accountcode ||'_'||accountname name from ynt_cpaccount t where  t.pk_corp = ? and nvl(dr,0) = 0  and t.isleaf = 'N' order by t.accountcode ";
		List<CodeName> list = (List<CodeName>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CodeName.class));
		return list;
	}
	@Override
	public List<CodeName> queryFZhsName(String userid,String pk_corp)throws DZFWarpException {
		if(StringUtil.isEmpty(userid) || StringUtil.isEmpty(pk_corp))
			return null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(IDefaultValue.DefaultGroup);
		String sql = " select h.pk_auacount_h id ,code,name  from ynt_fzhs_h h where (pk_corp = ? or pk_corp = ? ) and nvl(dr,0) = 0  order by code ";
		List<CodeName> list = (List<CodeName>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CodeName.class));
		return list;
	}
	private boolean isExistqmjz(String pk_corp){
		SQLParameter sp = new SQLParameter();
		sp.addParam("Y");
		sp.addParam(pk_corp);
		sp.addParam("%-12");
		String sql = " select * from ynt_qmjz  where jzfinish = ? and pk_corp = ? and period like ? and nvl(dr,0) = 0 ";
		boolean isexist = singleObjectBO.isExists(pk_corp, sql, sp);
		return isexist;
	}
	@Override
	public void saveKmzhuanFz(String userid, String pk_corp, String pk_km, String pk_fz) throws DZFWarpException {
		//1、校验
		if(StringUtil.isEmpty(userid) || StringUtil.isEmpty(pk_corp)
				|| StringUtil.isEmpty(pk_km) || StringUtil.isEmpty(pk_fz))
			throw new BusinessException("科目下级转辅助参数为空");
		YntCpaccountVO vo = queryCpaccountVO(pk_corp,pk_km);
		if(vo == null){
			throw new BusinessException("所选的科目不存在");
		}
		if(vo.getIsleaf() != null && vo.getIsleaf().booleanValue()){
			throw new BusinessException("所选的科目为末级，不能转辅助");
		}
		boolean isexist = isExistqmjz(pk_corp);
		//先删除年结数据，最后再年结已删除的数据
		if(isexist){
//			throw new BusinessException("存在年结数据，请反年结后，再下级转辅助！");
			new TerminalCurrSettleDMO(singleObjectBO).deleteTerminalSettleData(pk_corp, new String[]{vo.getAccountcode()});;
		}
		//查询该科目下面的所有末级科目。
		YntCpaccountVO[] vos = queryCpAccountVOs(pk_corp,vo.getAccountcode());
		check(vos,vo);
		//2、开始转辅助
		CorpVO cpvo = corpService.queryByPk(pk_corp);
		//查询辅助项目
		List<CodeName> listfz = queryFZhsName(userid,pk_corp);
		Map<String,CodeName> mapfz = DZfcommonTools.hashlizeObjectByPk(listfz, new String[]{"id"});
		if(!mapfz.containsKey(pk_fz))
			return;
		CodeName fzcodename = mapfz.get(pk_fz);
		if("存货".equals(fzcodename.getName())//存货，并且启用进销存
				&& IcCostStyle.IC_ON.equals(cpvo.getBbuildic())){
			//校验科目必须为1403或者1405
			if(!("1403".equals(vo.getAccountcode())
					|| "1405".equals(vo.getAccountcode()))){
				throw new BusinessException("当前公司启用库存模块，转存货辅助，科目必须为1403或者1405");
			}
			kmzfz_SaveCpaccount.save(vos, vo, fzcodename, cpvo, pk_fz, userid);
		}else{
			kmzfz_SaveCpaccount1.save(vos, vo, fzcodename, cpvo, pk_fz, userid);
		}
		if(isexist){
			new TerminalCurrSettleDMO(singleObjectBO).saveSomeKmTerminalSettleDataFromPZ(pk_corp,null,userid,new String[]{vo.getPrimaryKey()});
		}
	}
	
	private void check(YntCpaccountVO[] vos,YntCpaccountVO vo) throws BusinessException{
		if(vo == null || vos == null || vos.length == 0){
			throw new BusinessException("所选的科目下级不存在");
		}
		boolean iswb = false;
		List<String> listwb = new ArrayList<String>();
		List<String> codes = new ArrayList<String>();
		for(YntCpaccountVO vv : vos){
			if(vo.getAccountcode().equals(vv.getAccountcode())){
				continue;
			}
			//如果下级科目中有辅助、有数量、有外币，均不让操作
			if(!"0000000000".equals(vv.getIsfzhs())){
				throw new BusinessException("所选的科目下级["+vv.getAccountcode()+"]已启用辅助核算");
			}
//			if(vv.getIsnum()!=null && vv.getIsnum().booleanValue()){
//				throw new BusinessException("所选的科目下级["+vv.getAccountcode()+"]已启用数量核算");
//			}
			if(vv.getIswhhs()!=null && vv.getIswhhs().booleanValue()){
				iswb = true;
				listwb.add(vv.getExc_pk_currency());
				codes.add(vv.getAccountcode());
			}
			if(vv.getIssyscode()!=null && vv.getIssyscode().booleanValue()){
				throw new BusinessException("所选的科目下级["+vv.getAccountcode()+"]为系统科目");
			}
		}
		//下级科目启用外币
		if(iswb){
			if(vo.getIswhhs() != null && vo.getIswhhs().booleanValue()){
				if(StringUtil.isEmpty(vo.getExc_pk_currency())){
					throw new BusinessException("所选的科目下级"+codes.toString()+"存在启用外币的科目，请将当前科目启用相同的外币后，在重新操作");
				}
				for(String wb:listwb){
					String[] res = wb.split(",");
					if(res == null || res.length == 0)
						continue;
					for(String s : res){
						if(!StringUtil.isEmpty(s)){
							if(!vo.getExc_pk_currency().contains(s)){
								throw new BusinessException("所选的科目下级"+codes.toString()+"存在启用外币的科目，请将当前科目启用相同的外币后，在重新操作");
							}
						}
					}
				}
			}else{
				throw new BusinessException("所选的科目下级"+codes.toString()+"存在启用外币的科目，请将当前科目启用相同的外币后，在重新操作");
			}
		}
	}
	
	private YntCpaccountVO[] queryCpAccountVOs(String pk_corp,String accountcode){
		SQLParameter sp = new SQLParameter();
		sp.addParam(accountcode+"%");
		sp.addParam(pk_corp);
		sp.addParam("Y");
		String where = " accountcode like ? and pk_corp = ? and isleaf = ? and nvl(dr,0)=0 order by accountcode ";
		YntCpaccountVO[] vos = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, where, sp);
		return vos;
	}
	
	private YntCpaccountVO queryCpaccountVO(String pk_corp, String pk_km){
		YntCpaccountVO vo = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_km);
		sp.addParam(pk_corp);
		String where = " pk_corp_account = ? and pk_corp = ? and nvl(dr,0)=0 ";
		YntCpaccountVO[] vos = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, where, sp);
		if(vos !=null && vos.length>0){
			vo = vos[0];
		}
		return vo;
	}
	
	@Override
	public void perCheckForAddKeMu(YntCpaccountVO data, CorpVO corpvo) {
		CpaccountServiceCheck check = new CpaccountServiceCheck(singleObjectBO);
		check.preCheckAdd(data, corpvo);
	}

	@Override
	public String getNewSubCode(YntCpaccountVO vo, CorpVO corpvo) {
		String parentCode = vo.getAccountcode();
		String codeRule = corpvo.getAccountcoderule();
		if (codeRule == null) {
			codeRule = DZFConstant.ACCOUNTCODERULE;
		}
		String[] levelNum = codeRule.split("/");
		Integer level = vo.getAccountlevel();

		String newCode = null;
		if (level != null && level < levelNum.length) {
			String numStr = levelNum[level];
			int subLevelLen = Integer.valueOf(numStr);
			String codeLike = parentCode;
			for (int i = 0; i < subLevelLen; i++) {
				codeLike += "_";
			}
			SQLParameter sp = new SQLParameter();
			sp.addParam(corpvo.getPk_corp());
			sp.addParam(codeLike);
			String sql = " select max(accountcode) from ynt_cpaccount "
					+ " where pk_corp = ? and accountcode like ? and nvl(dr,0) = 0";
			String maxCode = (String) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());

			int subCode = 1;
			if (maxCode != null && !parentCode.equals(maxCode)) {
				String maxSubCode = maxCode.substring(parentCode.length());
				subCode = Integer.valueOf(maxSubCode) + 1;
				if (String.valueOf(subCode).length() <= maxSubCode.length()) {
					newCode = parentCode + String.format("%0" + subLevelLen + "d", subCode);
				}
			} else {
				newCode = parentCode + String.format("%0" + subLevelLen + "d", subCode);
			}
		}
		return newCode;
	}
	@Override
	public String[] queryXjkm(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司信息不存在");
		}
		CorpVO cpvo = corpService.queryByPk(pk_corp);

		if (cpvo == null) {
			throw new BusinessException("公司不存在");
		}
		YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);

		if (cpavos == null || cpavos.length == 0) {
			throw new BusinessException("公司科目不存在");
		}

		Set<String> kmcodes = KmschemaCash.getCashSubjectCode(cpavos, cpvo.getCorptype());

		return kmcodes.toArray(new String[0]);
	}

	@Override
	public Map<String, BdTradeAccountVO> getTradeKmMap(String corpType) {
		if(StringUtil.isEmpty(corpType)){
			return new HashMap<>();
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpType);
		BdTradeAccountVO[] bdTradeAccountVOS = (BdTradeAccountVO[]) singleObjectBO.queryByCondition(BdTradeAccountVO.class, "nvl(dr,0)=0 and pk_trade_accountschema = ? ", sp);
		if(bdTradeAccountVOS == null || bdTradeAccountVOS.length == 0){
			return new HashMap<>();
		}
		return Arrays.stream(bdTradeAccountVOS).collect(Collectors.toMap(BdTradeAccountVO::getPk_trade_account, v -> v, (k1, k2) -> k1));
	}
}
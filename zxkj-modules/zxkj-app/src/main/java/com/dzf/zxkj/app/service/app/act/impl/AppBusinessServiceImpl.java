package com.dzf.zxkj.app.service.app.act.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import com.dzf.zxkj.app.model.image.TransVspstyleModel;
import com.dzf.zxkj.app.model.ticket.ZzsTicketHVO;
import com.dzf.zxkj.app.service.app.act.IAppBusinessService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.CpaccountUtil;
import com.dzf.zxkj.app.utils.TaxCalUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.FieldConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.image.DcModelBVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 业务处理
 * 
 * @author zhangj
 *
 */
@Slf4j
@Service("appbusihand")
public class AppBusinessServiceImpl implements IAppBusinessService {


	@Autowired
	private SingleObjectBO singleObjectBO;
	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;

//	@Autowired
//	private YntBoPubUtil yntBoPubUtil;
//
//	@Autowired
//	private IAppPubservice apppubservice;
//
//	@Autowired
//	private IAppApproveService appapprovehand;
//
//	@Autowired
//	private IAppTicketService apppthand;
//
//	@Autowired
//	private IParameterSetService paramService;
//
//	@Autowired
//	private IBankStatementService gl_yhdzdserv;
//
//	@Autowired
//	private IJtsjTemService sys_jtsjtemserv;
//
//	@Autowired
//	private IUserService userServiceImpl;
//
	@Override
	public String saveVoucherFromPic(String groupkey, String pk_corp,SingleObjectBO sbo) throws DZFWarpException {

		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(pk_corp);
		if(StringUtil.isEmpty(groupkey))
			return "该图片不存在!";

		ImageGroupVO groupvo = (ImageGroupVO) sbo.queryByPrimaryKey(
				ImageGroupVO.class, groupkey);

		if(groupvo == null){
			return "该图片不存在!";
		}

		if( groupvo.getIstate() != PhotoState.state0){
			return "图片状态已改变!";
		}

		String flowid = groupvo.getPicflowid();

		if(StringUtil.isEmpty(flowid)){
			return "";
		}

		int iflow = Integer.parseInt(flowid);

		if( PhotoState.TREAT_TYPE_0 == iflow){
			return "";
		}

//		if(PhotoState.TREAT_TYPE_1 == iflow || PhotoState.TREAT_TYPE_3 == iflow
//				|| PhotoState.TREAT_TYPE_5 == iflow || PhotoState.TREAT_TYPE_6 == iflow || PhotoState.TREAT_TYPE_7 == iflow){//需要走只能识别
		if(PhotoState.TREAT_TYPE_7 == iflow){
//			IOcrImageGroupService  gl_pzimageserv = (IOcrImageGroupService) SpringUtils.getBean("gl_ocrimageserv");
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(groupkey);
//			ImageLibraryVO[] libvos = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, "nvl(dr,0)=0 and pk_image_group = ? ", sp);
//			if (libvos != null && libvos.length > 0) {
//				for (ImageLibraryVO libvo : libvos) {
//					gl_pzimageserv.saveData(cpvo, libvo, null, 0, null);
//				}
//			}

		}else{//自己查找模板生成凭证
			CorpVO corpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, pk_corp);//CorpCache.getInstance().get(null, pk_corp);
			DcModelHVO hvo = queryVoucherMode(iflow,groupvo.getSettlemode(), groupvo.getMemo(), corpvo, pk_corp);
			try {

				createVoucherFromPic(hvo, groupvo, corpvo);

				groupvo.setIstate(PhotoState.state101);

				sbo.update(groupvo, new String[]{"istate"});

				groupvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, groupvo.getPrimaryKey());

			} catch (Exception e) {
				if(e instanceof BusinessException){
					throw new BusinessException(e.getMessage());
				}else{
					log.error(e.getMessage(),e);
					throw new BusinessException("凭证生成失败，请手工生成!");
				}
			}
		}


		return "";//成功，什么都不传，成功消息交给调用方


	}

		/**
	 * 根据公司+结算方式+公司性质+票据类型+业务类型 获取对应的模板信息
	 *
	 * @param uvo
	 */
	private DcModelHVO queryVoucherMode(int iflow,String  paymethod,String memo, CorpVO cpvo, String dcpk_corp) {

		//如果公司性质为空，则更新bdcorp
		CorpVO cptempvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, cpvo.getPrimaryKey());
		if(cptempvo!=null){
			if(StringUtil.isEmpty(cptempvo.getChargedeptname())){
				cptempvo.setChargedeptname("小规模纳税人");
				singleObjectBO.update(cptempvo, new String[]{"chargedeptname"});
				cpvo.setChargedeptname("小规模纳税人");
			}
		}


		Map<String, DcModelHVO> mapmodels = queryDcModelVO(dcpk_corp);

		String corptype = cpvo.getCorptype();// 科目方案

		Integer corpschema = iZxkjRemoteAppService.getAccountSchema(cpvo.getPk_corp());
		// 公司方案
		String fpstylecode = TransVspstyleModel.transVspstyle(memo, cpvo.getChargedeptname());// 票据类型
		String szstylecode = TransVspstyleModel.transSzstyle(memo, paymethod);// 收支类型
		String vmemocode = TransVspstyleModel.transVsMemotyle(memo,corpschema,cpvo.getChargedeptname());


		String chargedeptname = cpvo.getChargedeptname();

		if (StringUtil.isEmpty(corptype)) {
			throw new BusinessException("生成凭证失败:您公司科目方案为空！");
		}

		if (StringUtil.isEmpty(chargedeptname)) {
			throw new BusinessException("生成凭证失败：您公司性质为空！");
		}

		String key = corptype + "_"   + fpstylecode + "_" + szstylecode + "_" + vmemocode;

		DcModelHVO hvo = mapmodels.get(key);


		if(hvo == null || iflow == PhotoState.TREAT_TYPE_0){
			hvo = queryVoucherFixedMode(cpvo);
		}


		return hvo;
	}
	/**
	 * 查询数据中心凭证模版
	 *
	 * @return
	 */
	private HashMap<String, DcModelHVO> queryDcModelVO(String dcpk_corp) {
		HashMap<String, DcModelHVO> map = new HashMap<>();
		List<DcModelHVO> mhvos = iZxkjRemoteAppService.queryDcModelHVO(dcpk_corp);
		//
		if(mhvos == null || mhvos.size() ==0){
			return map;
		}
		String key = null;
		for (DcModelHVO hvo : mhvos) {
			key = hvo.getPk_trade_accountschema() + "_"  + hvo.getVspstylecode() + "_"
					+ hvo.getSzstylecode() + "_" + hvo.getBusitypetempname();
			map.put(key, hvo);
		}
		return map;
	}
	/**
	 * 生成图片凭证单据
	 * @param hvo
	 * @param groupvo
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	private void createVoucherFromPic(DcModelHVO hvo, ImageGroupVO groupvo, CorpVO corpvo) throws DZFWarpException{
		//是否暂存态
		boolean isTemporary = false;
		String pk_curr = iZxkjRemoteAppService.getCNYPk();
		Map<String, YntCpaccountVO> ccountMap = iZxkjRemoteAppService.queryMapByPk(corpvo.getPk_corp());
		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
		// 生成子表数据
		List<DcModelBVO> mBVOs = iZxkjRemoteAppService.queryByPId(hvo.getPk_model_h(), corpvo.getPk_corp());
		if (mBVOs == null || mBVOs.size() == 0) {
			throw new BusinessException("扫码票据生成凭证失败,没找到对应的模板信息！");
		}
		DZFDouble mny = null;
		DZFDouble hjmny =  (DZFDouble) groupvo.getAttributeValue("mny") == null ? DZFDouble.ZERO_DBL: (DZFDouble) groupvo.getAttributeValue("mny");;
		TzpzBVO tzpzbvo = null;
		YntCpaccountVO cvo = null;
		boolean isleaf = false;
		boolean iskhfz = false;
		DZFDouble slv = TaxCalUtil.taxCal(groupvo.getMemo(), corpvo.getChargedeptname());
		for(DcModelBVO bvo : mBVOs){
			mny = (DZFDouble) groupvo.getAttributeValue("mny") == null ? DZFDouble.ZERO_DBL: (DZFDouble) groupvo.getAttributeValue("mny");
			if("totalmny".equals(bvo.getVfield())){//总金额
				mny = (DZFDouble) groupvo.getAttributeValue("mny");
			}else if("mny".equals(bvo.getVfield())){//总金额
				mny = (DZFDouble) groupvo.getAttributeValue("mny");
			}else if("wsmny".equals(bvo.getVfield())){//无税金额
				mny =  mny.div(SafeCompute.add(new DZFDouble(1), slv)).setScale(2, DZFDouble.ROUND_HALF_UP);
			}else if("smny".equals(bvo.getVfield())){//税额
				mny =SafeCompute.sub(mny, mny.div(SafeCompute.add(new DZFDouble(1), slv)).setScale(2, DZFDouble.ROUND_HALF_UP));
			}
//			mny = (DZFDouble) groupvo.getAttributeValue("totalmny".equals(bvo.getVfield()) ? "mny" : bvo.getVfield());
			mny = mny == null ? DZFDouble.ZERO_DBL : mny;
			tzpzbvo = new TzpzBVO();
			cvo = ccountMap.get(bvo.getPk_accsubj());

			if(bvo.getDirection() == 0){
				tzpzbvo.setJfmny(mny);
				tzpzbvo.setYbjfmny(mny);
			}else{
				tzpzbvo.setDfmny(mny);
				tzpzbvo.setYbdfmny(mny);
			}
			isleaf = cvo.getIsleaf().booleanValue();//是否是末级
			iskhfz = cvo.getIsfzhs().charAt(1) > 0;//
			if(iskhfz || !isleaf){
				isTemporary = false;
			}

			tzpzbvo.setPk_currency(pk_curr);
			tzpzbvo.setPk_accsubj(cvo.getPk_corp_account());
			tzpzbvo.setVcode(cvo.getAccountcode());
			tzpzbvo.setVname(cvo.getAccountname());

			tzpzbvo.setKmmchie(cvo.getFullname());
			tzpzbvo.setSubj_code(cvo.getAccountcode());
			tzpzbvo.setSubj_name(cvo.getAccountname());

			tzpzbvo.setZy(bvo.getZy());
			tzpzbvo.setNrate(DZFDouble.ONE_DBL);
			tzpzbvo.setPk_corp(corpvo.getPk_corp());

			bodyList.add(tzpzbvo);
			cvo = null;
		}

		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(corpvo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(hjmny);
		headVO.setDfmny(hjmny);
		headVO.setCoperatorid(groupvo.getCoperatorid());
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setDoperatedate(groupvo.getCvoucherdate());
		headVO.setPzh(iZxkjRemoteAppService.getNewVoucherNo(corpvo.getPk_corp(), groupvo.getCvoucherdate()));
		headVO.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(groupvo.getPrimaryKey());
		headVO.setSourcebilltype(IBillTypeCode.HP100);

		if ("01".equals(hvo.getVspstylecode())) {
			headVO.setFp_style(2);
		} else if ("02".equals(hvo.getVspstylecode())) {
			headVO.setFp_style(1);
		}//1/2/3  普票/专票/未开票 空：不处理改字段
		if (!StringUtil.isEmpty(groupvo.getPrimaryKey())) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(corpvo.getPk_corp());
			sp.addParam(groupvo.getPrimaryKey());
			String imgQuery = " select count(1) from ynt_image_library where nvl(dr,0)=0 and pk_corp = ? and pk_image_group = ? ";
			headVO.setPk_image_group(groupvo.getPrimaryKey());
			BigDecimal imgNum = (BigDecimal) singleObjectBO.executeQuery(imgQuery, sp, new ColumnProcessor());
			//设置单据张数
			headVO.setNbills(imgNum.intValue());
		}

		String period = groupvo.getCvoucherdate().toString().substring(0, 7);
		headVO.setPeriod(period);
		headVO.setVyear(Integer.valueOf(period.substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		headVO.setMemo(null);
		headVO.setChildren(bodyList.toArray(new TzpzBVO[0]));
		boolean lock = false;
		try {
			lock = iZxkjRemoteAppService.tryGetDistributedFairLock(groupvo.getTableName()+groupvo.getPrimaryKey());
			if(lock){
				iZxkjRemoteAppService.checkCreatePZ(headVO.getPk_corp(), headVO);
				headVO.setVbillstatus(IVoucherConstants.TEMPORARY);//暂存态
				TzpzHVO tzpzhvo = (TzpzHVO) singleObjectBO.saveObject(corpvo.getPk_corp(), headVO);
//			headVO = gl_tzpzserv.saveVoucher(corpvo, headVO);
			}
		} catch (Exception e) {
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else{
				throw new BusinessException("凭证生成失败!");
			}
		}finally {
			if(lock){
				iZxkjRemoteAppService.releaseDistributedFairLock(groupvo.getTableName()+groupvo.getPrimaryKey());
			}
		}
	}

	/**
	 * 拼装构造固定的模板
	 * @param corpvo
	 * @return
	 */
	private DcModelHVO queryVoucherFixedMode(CorpVO corpvo){
		DcModelHVO hvo = null;
		Map<String,YntCpaccountVO> cpamap = iZxkjRemoteAppService.queryMapByPk(corpvo.getPk_corp());
		if(StringUtil.isEmpty(corpvo.getChargedeptname()))
			return null;

		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0 and pk_corp = ? and pk_trade_accountschema = ? and vspstylecode = ?  and szstylecode = ? and busitypetempname = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(corpvo.getCorptype());
		sp.addParam(FieldConstant.FPSTYLE_21);
//		sp.addParam(corpvo.getChargedeptname());
		sp.addParam(FieldConstant.SZSTYLE_04);
		sp.addParam(FieldConstant.YWSTYLE_21);

		List<DcModelHVO> list =  (List<DcModelHVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new Class[]{DcModelHVO.class, DcModelBVO.class});

		if(list != null && list.size() > 0){
			hvo =  list.get(0);
		}else {
			throw new BusinessException("默认模板不存在!");
		}

		hvo = transNewCpidFromId(hvo, corpvo.getPk_corp(), cpamap);

		return hvo;
	}


	/**
	 * 科目方案转化成对应公司科目
	 * @param hvo
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private DcModelHVO transNewCpidFromId(DcModelHVO hvo, String pk_corp, Map<String, YntCpaccountVO> cpamap) throws DZFWarpException{

		DcModelBVO[] bvos = hvo.getChildren();

		if(bvos == null || bvos.length ==0){
			return null;
		}

		YntCpaccountVO cpavo;
		String relkmid;
		for(DcModelBVO vo : bvos){
			relkmid = iZxkjRemoteAppService.getCpidFromTd(vo.getPk_accsubj(), pk_corp, null);
			cpavo = cpamap.get(relkmid);
			if(cpavo == null){
				log.error("原科目主键:"+ vo.getPk_accsubj() + ", 转化后科目主键:" + relkmid);
				return null;
			}

			vo.setPk_accsubj(relkmid);
			vo.setKmbm(cpavo.getAccountcode());
			vo.setKmmc(cpavo.getAccountname());
		}

		return hvo;
	}
	/**
	 * 根据图片信息生成对应的凭证信息
	 * @param uvo
	 * @param cpvo
	 * @param zzshvo
	 * @param groupvo
	 */
	public void saveVoucherFromTicket(DZFDate kprq, DZFDouble mny, String paymetod, String memo, String pk_corp,
									  String account_id, ZzsTicketHVO zzshvo, ImageGroupVO groupvo) throws DZFWarpException {

		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		if(zzshvo == null){
			throw new BusinessException("扫码票据信息不存在!");
		}

		// 找到对应的凭证模板
		DcModelHVO hvo = queryVoucherMode(PhotoState.TREAT_TYPE_7,paymetod,memo, cpvo, pk_corp);

		if (hvo == null) {
			throw new BusinessException("扫码票据生成凭证失败：对应的凭证模板为空!");
		}

		// 组装生成凭证信息
		packvo(hvo, pk_corp, account_id, groupvo.getPrimaryKey(),kprq, mny,zzshvo);

		groupvo.setIstate(PhotoState.state101);

		singleObjectBO.update(groupvo,new String[]{"istate"});
	}
	private void packvo(DcModelHVO hvo,String pk_corp,String account_id,
			  String pk_image_group, DZFDate kprq,DZFDouble mny,ZzsTicketHVO zzshvo)
			throws DZFWarpException {

		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			throw new BusinessException("扫码票据生成凭证失败，公司信息不能为空!");
		}

		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

	    DZFDouble compare= SafeCompute.sub(new DZFDouble(zzshvo.getJshj()), mny);

		if (StringUtil.isEmpty(zzshvo.getKprq())) {
			throw new BusinessException("扫码票据生成凭证失败，开票日期为空！");
		}
		String newrule = iZxkjRemoteAppService.queryAccountRule(pk_corp);

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		YntCpaccountVO[] accounts = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" nvl(dr,0)=0 and pk_corp = ?  ", sp);

		// 主表数据
		TzpzHVO headVO = new TzpzHVO();
		headVO.setDr(0);
		headVO.setPk_corp(pk_corp);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(compare.doubleValue() != 0 ? mny
				: new DZFDouble((String) zzshvo.getAttributeValue("jshj")));
		headVO.setDfmny(compare.doubleValue() != 0 ? mny
				: new DZFDouble((String) zzshvo.getAttributeValue("jshj")));
		headVO.setCoperatorid(account_id);
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setDoperatedate(kprq);
		headVO.setPzh(iZxkjRemoteAppService.getNewVoucherNo(pk_corp, kprq));
		headVO.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
		headVO.setPeriod(kprq.toString().substring(0, 7));
		headVO.setVyear(Integer.valueOf(kprq.toString().substring(0, 4)));
		String[] strs = zzshvo.getDrcode().split(",");
		if (strs[1].equals("01") ) {
			headVO.setFp_style(2);
		}else if(strs[1].equals("04") ||strs[1].equals("10")  || strs[1].equals("51")){
			headVO.setFp_style(1);
		}
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		headVO.setPk_image_group(pk_image_group);
		headVO.setNbills(1);// 设置单据张数

		// 生成子表数据
		List<DcModelBVO> bvos = iZxkjRemoteAppService.queryByPId(hvo.getPk_model_h(), pk_corp);

		if (bvos == null || bvos.size() == 0) {
			throw new BusinessException("扫码票据生成凭证失败,没找到对应的模板信息！");
		}

		List<TzpzBVO> pzbvos = new ArrayList<TzpzBVO>();

		YntCpaccountVO account = null;
		DZFDouble mnyvalue = new DZFDouble(new DZFDouble(zzshvo.getJe()));

		String modelcode = null;
		for (DcModelBVO model : bvos) {

//			modelcode = gl_accountcoderule.getNewRuleCode(model.getKmbm(), DZFConstant.ACCOUNTCODERULE, newrule);
//
//			account = getAccountByCode(modelcode, accounts);

			Object[] objs = CpaccountUtil.getInstance().getNextKmOrFzxm(model.getKmbm(), pk_corp, accounts, "", "");

			account = (YntCpaccountVO)objs[0];

			if ("totalmny".equals(model.getVfield())) {
				mnyvalue = (compare.doubleValue() != 0 ? mny
						: new DZFDouble((String) zzshvo.getAttributeValue("jshj")));
			} else if ("smny".equals(model.getVfield())) {
				mnyvalue = (compare.doubleValue() != 0 ? DZFDouble.ZERO_DBL
						:new DZFDouble((String) zzshvo.getAttributeValue("se")));
			} else if ("wsmny".equals(model.getVfield())) {
				mnyvalue = (compare.doubleValue() != 0 ? mny
						:new DZFDouble((String) zzshvo.getAttributeValue("je")));
			}

			TzpzBVO entry = new TzpzBVO();
			if (mnyvalue == null || mnyvalue.doubleValue() == 0)
				continue;
			entry.setPk_accsubj(account.getPk_corp_account());
			entry.setDr(0);
			entry.setZy(model.getZy());// 摘要
			// 币种，默认人民币
			entry.setPk_currency(iZxkjRemoteAppService.getCNYPk());// (pub_utilserv.getCNYPk())
			entry.setNrate(new DZFDouble(1));
			entry.setPk_corp(headVO.getPk_corp());
			entry.setKmmchie(account.getFullname());
			entry.setVcode(account.getAccountcode());
			entry.setVname(account.getAccountname());
			// 该科目挂辅助项目则也不生成信息
			if (!StringUtil.isEmpty(account.getIsfzhs()) && account.getIsfzhs().indexOf("1")>=0) {
				int value= account.getIsfzhs().indexOf("1") ;
				entry.setAttributeValue("fzhsx"+(value+1), ((AuxiliaryAccountBVO)objs[1]).getPrimaryKey());
			}

			if (model.getDirection() == 0) {// 借方
				entry.setJfmny(mnyvalue);
			} else {// 贷方
				entry.setDfmny(mnyvalue);
			}
			pzbvos.add(entry);
		}

		headVO.setChildren(pzbvos.toArray(new TzpzBVO[0]));
		try {
//			TzpzHVO tzpzhvo = gl_tzpzserv.saveVoucher(cpvo, headVO);
			iZxkjRemoteAppService.checkCreatePZ(headVO.getPk_corp(), headVO);
			headVO.setVbillstatus(IVoucherConstants.TEMPORARY);//暂存态
			TzpzHVO tzpzhvo = (TzpzHVO) singleObjectBO.saveObject(pk_corp, headVO);

			String upsql = "update ynt_image_group set istate = 100 where pk_image_group = ? ";
			sp.clearParams();
			sp.addParam(pk_image_group);
			singleObjectBO.executeUpdate(upsql, sp);

//			String upgroupsql = "update app_zzstiket set pk_tzpz_h = ? where PK_ZZSTIKET = ? ";
//			sp.clearParams();
//			sp.addParam(tzpzhvo.getPrimaryKey());
//			sp.addParam(zzshvo.getPrimaryKey());
//			singleObjectBO.executeUpdate(upgroupsql, sp);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new BusinessException(e.getMessage());
		}

	}

//	@Override
//	public BusinessResonseBeanVO getWorkTips(UserBeanVO uvo) throws DZFWarpException {
//
//		BusinessResonseBeanVO resvo = new BusinessResonseBeanVO();
//
//		if (AppCheckValidUtils.isEmptyCorp(uvo.getPk_corp())) {
//			resvo.setRescode(IConstant.FIRDES);
//			resvo.setResmsg("公司信息为空!");
//			return resvo;
//		}
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(uvo.getPk_corp());
//		sp.addParam(uvo.getPk_corp());
//		StringBuffer messsql = new StringBuffer();
//		messsql.append(" select * from ");
//		messsql.append(" ( select ynt_app_message.vcontent,ynt_app_message.vsenddate, ");
//		messsql.append("  bd_account.unitname as cname,ynt_app_message.ts, 0 as msgtype ");
//		messsql.append(" from ynt_app_message  ");
//		messsql.append(" left join bd_account on ynt_app_message.pk_corp = bd_account.pk_corp");
//		messsql.append(" where nvl(ynt_app_message.dr,0)=0 and ynt_app_message.pk_corpk = ?   ");// order
//																									// by
//																									// ynt_app_message.vsenddate
//																									// desc
//
//		messsql.append(" union all");
//		messsql.append(" select  ynt_sendmesg.vcontent,substr(ynt_sendmesg.vsenddate,0,10) as vsenddate , ");
//		messsql.append(" bd_account.unitname as cname,ynt_sendmesg_b.ts , 1 as msgtype  ");
//		messsql.append(" from ynt_sendmesg  ");
//		messsql.append(" inner join ynt_sendmesg_b  on ynt_sendmesg.pk_sendmesg= ynt_sendmesg_b.pk_sendmesg ");
//		messsql.append(" left join bd_account on ynt_sendmesg.pk_corp = bd_account.pk_corp");
//		messsql.append(
//				" where ynt_sendmesg_b.pk_corpk = ? and nvl(ynt_sendmesg.dr,0)=0 and nvl(ynt_sendmesg_b.dr,0)=0   )  t1");
//		messsql.append(" order by t1.vsenddate desc,t1.ts desc ");// order by
//																	// ynt_sendmesg.vsenddate
//																	// desc
//		List<AppMessageVO> messvos = (List<AppMessageVO>) singleObjectBO.executeQuery(messsql.toString(), sp,
//				new BeanListProcessor(AppMessageVO.class));
//
//		if (messvos == null || messvos.size() == 0) {
//			resvo.setRescode(IConstant.FIRDES);
//			resvo.setResmsg("信息为空!");
//			return resvo;
//		}
//		resvo.setRescode(IConstant.DEFAULT);
//		resvo.setResmsg(
//				QueryDeCodeUtils.decKeyUtils(new String[] { "cname" }, messvos.toArray(new AppMessageVO[0]), 1));
//		return resvo;
//	}
//
//	@Override
//	public void saveVoucherFromTicket(UserBeanVO uvo,ImageGroupVO groupvo) throws DZFWarpException {
//
//		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, uvo.getPk_corp());
//
//		// 根据发票号获取发票的信息
//		ZzsTicketHVO zzshvo = getZzsVos(uvo.getDrcode());
//
//		if(uvo.getKpdate() == null){//如果开票日期为空，则取票据的开票日期
//			uvo.setKpdate(zzshvo.getKprq());
//		}
//
////		if(StringUtil.isEmpty(uvo.getMny())){//如果金额为空则取
////			uvo.setMny(zzshvo.getJshj());
////		}
//
//		if (zzshvo == null) {
//			throw new BusinessException("扫码票据生成凭证失败：发票信息不存在!");
//		}
//
//		if (StringUtil.isEmpty(zzshvo.getKprq())) {
//			throw new BusinessException("扫码票据生成凭证失败：开票日期为空!");
//		}
//
//		if (cpvo == null || cpvo.getBegindate() == null) {
//			throw new BusinessException("扫码票据生成凭证失败：您公司尚未建账!");
//		}
//
//		if (new DZFDate(uvo.getKpdate()).before(cpvo.getBegindate())) {
//			throw new BusinessException("扫码票据生成凭证失败：开票日期在正式签约日期前!");
//		}
//
//		if(StringUtil.isEmpty(uvo.getAccount_id())){
//			throw new BusinessException("当前帐号信息为空!");
//		}
//
////		if(groupvo.getIstate() != null && (PhotoState.state100 == groupvo.getIstate() || PhotoState.state101 == groupvo.getIstate())){
////			throw new BusinessException("发票已生成凭证，不能多次生成!");
////		}
//
//		if(groupvo.getIstate()!=null && PhotoState.state200 == groupvo.getIstate()){
//			throw new BusinessException("当前票待审核，不能生成凭证!");
//		}
//
//		boolean buse = appapprovehand.bOpenApprove(uvo.getPk_corp());
//
//		//是否需要走审批(大账房app)
//		if (buse && !ISysConstants.SYS_ADMIN.equals(uvo.getSourcesys())) {
//			BusiReqBeanVo ubean = new BusiReqBeanVo();
//			ubean.setPk_image_group(groupvo.getPrimaryKey());
//			ubean.setPk_corp(uvo.getPk_corp());
//			ubean.setAccount_id(uvo.getAccount_id());
//			appapprovehand.updateApprove(ubean,singleObjectBO);
//		}
//
//		if (!buse || ISysConstants.SYS_ADMIN.equals(uvo.getSourcesys())) {
//			try {
//				saveVoucherFromTicket(new DZFDate(uvo.getKpdate()),new DZFDouble(uvo.getMny()),uvo.getPaymethod(), uvo.getMemo(), uvo.getPk_corp(),
//						uvo.getAccount_id(), zzshvo, groupvo);
//			} catch (Exception e) {
//				if(e instanceof BusinessException){
//					throw new BusinessException(e.getMessage()+"请查询图片,手工生成凭证!");
//				}else{
//					throw new BusinessException("生成凭证失败:请查询图片,手工生成凭证!!");
//				}
//			}
//		}
//
//	}
//

//	private ImageGroupVO genImageGroup(CorpVO corpvo, UserBeanVO uvo,String pk_ticket_h,String path) {
//		ImageGroupVO groupvo = new ImageGroupVO();
//		groupvo.setPk_corp(corpvo.getPk_corp());
//		groupvo.setCoperatorid(uvo.getAccount_id());
//		groupvo.setDoperatedate(new DZFDate());
//		groupvo.setMemo(uvo.getMemo());
//		groupvo.setMemo1(uvo.getMemo1());//备注
//		groupvo.setSettlemode(uvo.getPaymethod());
//		groupvo.setImagecounts(1);// 图片张数
//		groupvo.setMny(uvo.getMny()==null?DZFDouble.ZERO_DBL:new DZFDouble(uvo.getMny()));
//		groupvo.setPk_ticket_h(pk_ticket_h);//票通的信息主键
//		// istate=0 标识对应直接生单,不启用切图、识图，以后用PhotoState常量类
//		groupvo.setIstate(PhotoState.state0);// 0
//		// 保存为凭证日期
//		groupvo.setCvoucherdate(new DZFDate(uvo.getKpdate()));
//		long maxCode = getNowMaxImageGroupCode(corpvo.getPk_corp());
//		if (maxCode > 0) {
//			groupvo.setGroupcode(maxCode + 1 + "");
//		} else {
//			groupvo.setGroupcode(getCurDate() + "0001");
//		}
//		groupvo.setSessionflag(groupvo.getGroupcode());
//
//
//		ImageLibraryVO il = new ImageLibraryVO();
//		il.setImgpath(path);
//		il.setImgname(groupvo.getGroupcode() + "-001.jpg");
//		il.setPk_corp(corpvo.getPk_corp());
//		il.setCoperatorid(uvo.getAccount_id());
//		il.setDoperatedate(new DZFDate());
//		il.setCvoucherdate(new DZFDate(uvo.getKpdate()));//生成凭证时间
//
//		groupvo.addChildren(il);
//
//		groupvo = (ImageGroupVO) singleObjectBO.saveObject(corpvo.getPk_corp(), groupvo);
//
//		// 生成imagelibvo
//		return groupvo;
//	}
//
//	// 获取当前年月日
//	private static String getCurDate() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		return format.format(Calendar.getInstance().getTime());
//	}
//
//	public long getNowMaxImageGroupCode(String pk_corp) {
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		params.addParam(new DZFDate().toString());
//
//		String sql = "select max(groupcode) from ynt_image_group where pk_corp = ? and doperatedate = ? ";
//		long maxcode = 0;
//
//		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
//		if (array != null && array.length > 0) {
//			if (array[0] != null)
//				maxcode = Long.parseLong(array[0].toString());
//		}
//		return maxcode;
//	}
//
//	private String genImage(String unitcode, String unitname, ZzsTicketHVO hvo) {
//		String imgFileNm = UUID.randomUUID().toString() + ".png";
//		String path = unitcode + "/" + getCurDate() + "/" + imgFileNm;
//		String outpath = Common.imageBasePath + path;
//
//		URL xmlpath = getXmlPath_new(hvo);
//
//		List<FontText> fonttexts = new ArrayList<FontText>();
//
//		newGenImg(hvo, fonttexts);
//
//		GenTickImageUtil.drawTextInImg(xmlpath.getFile(), outpath, fonttexts.toArray(new FontText[0]));
//
//		return path;
//	}
//
//	private URL getXmlPath_new(ZzsTicketHVO hvo) {
//		// 通过drcode 查询对应的信息
//		URL xmlpath = this.getClass().getClassLoader().getResource("app_model_dz.jpg");
//		String[] strs = hvo.getDrcode().split(",");
//		if(strs[1].equals("01")){//专票
//			xmlpath = this.getClass().getClassLoader().getResource("app_model_zp.jpg");
//		}else if(strs[1].equals("04")){//普票
//			xmlpath = this.getClass().getClassLoader().getResource("app_model_pp.jpg");
//		}
//		return xmlpath;
//	}
//
//	private URL getXmlPath_old(String unitname, ZzsTicketHVO hvo) {
//		// 通过drcode 查询对应的信息
//		URL xmlpath = this.getClass().getClassLoader().getResource("app_model.png");
//		if(unitname.equals(((ZzsTicketHVO) hvo).getGfmc())){
//			//进项发票
//			xmlpath = this.getClass().getClassLoader().getResource("jxfp_model.png");
//		}else if(unitname.equals(((ZzsTicketHVO) hvo).getXfmc())){
//			//销项发票
//			xmlpath = this.getClass().getClassLoader().getResource("xxfp_model.png");
//		}
//		return xmlpath;
//	}
//
//	private void newGenImg(ZzsTicketHVO hvo, List<FontText> fonttexts) {
//
//		//表头显示
//		fonttexts.add(new FontText(hvo.getFpdm(),500,16));
//		fonttexts.add(new FontText(hvo.getFphm(),500,36));
//		fonttexts.add(new FontText(hvo.getKprq().substring(0, 4), 512, 54));
//		fonttexts.add(new FontText(hvo.getKprq().substring(5, 7), 552, 54));
//		fonttexts.add(new FontText(hvo.getKprq().substring(8), 578, 54));
//		fonttexts.add(new FontText(hvo.getJym(),500, 72));
//
//		fonttexts.add(new FontText(hvo.getGfmc(),168,96));//购买方
//		fonttexts.add(new FontText(hvo.getGfsbh(),168,116));//购买方识别号
//		fonttexts.add(new FontText(hvo.getGfdzdh(),168,136));//购买方地址，电话
//		fonttexts.add(new FontText(hvo.getGfyhzh(),168,156));//购买方开户银行账号
//
//		//价税合计
//		fonttexts.add(new FontText(NumberToCN.number2CnFromStr(hvo.getJshj()),236,260));
//		fonttexts.add(new FontText(hvo.getJshj(),538,260));
//
//		//销售方
//		fonttexts.add(new FontText(hvo.getXfmc(),164,284));//销方名称
//		fonttexts.add(new FontText(hvo.getXfsbh(),164,300));
//		fonttexts.add(new FontText(hvo.getXfdzdh(),164,314));
//		fonttexts.add(new FontText(hvo.getXfyhzh(),164,334));//银行账号
//
//
//		//查询对应的子表项目
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(hvo.getPk_zzstiket());
//		ZzsTicketBVO[] bvos = (ZzsTicketBVO[]) singleObjectBO.queryByCondition(ZzsTicketBVO.class, "nvl(dr,0)=0 and pk_zzstiket = ? ", sp);
//		// 发票项目
//		if(bvos!=null && bvos.length>0){
//			for(int i =0;i<bvos.length;i++){
//				if(i==5){
//					fonttexts.add(new FontText("...", 58, 172 + i * 12, 12));
//					break;
//				}
//				String hwmc = bvos[i].getHwmc();
//				if(!StringUtil.isEmpty(hwmc) && hwmc.length()>20){
//					hwmc = hwmc.substring(0, 20)+"...";
//				}
//				fonttexts.add(new FontText(hwmc, 58, 176 + i * 12, 12));
//				fonttexts.add(new FontText(bvos[i].getSl(),380, 176 + i * 12, 12));
//				fonttexts.add(new FontText(bvos[i].getDj(),434, 176 + i * 12, 12));//单价
//				fonttexts.add(new FontText(bvos[i].getJe(),500,176 + i * 12, 12));//金额
//				fonttexts.add(new FontText(bvos[i].getSlv(),550,176 + i * 12, 12));//税率
//				fonttexts.add(new FontText(bvos[i].getSe(),604,176 + i * 12, 12));//税额
//			}
//		}
//	}
//
//
//
//	private void oldGenImg(ZzsTicketHVO hvo, List<FontText> fonttexts) {
//		String[] strs = hvo.getDrcode().split(",");
//		String title = "";
//		if(strs[1].equals("01")){
//			title = "增值税专用发票" ;
//		}else if(strs[1].equals("04")){
//			title = "增值税普通发票";
//		}else {
//			title = "增值税普通电子发票";
//		}
//
//		//表头显示
//		fonttexts.add(new FontText(title, 0, 90, "#7F4E2A", 26, "黑体"));
//		fonttexts.add(new FontText(hvo.getKprq(), 205, 185));
//		fonttexts.add(new FontText(hvo.getFphm(), 648, 185));
//		fonttexts.add(new FontText(hvo.getGfmc(), 248, 262));
//		// 发票项目
//		if (!StringUtil.isEmpty(hvo.getFpxms()) && hvo.getFpxms().indexOf(";") > 0) {
//			String[] fpxmss = hvo.getFpxms().split(";");
//			for (int i = 1; i <= fpxmss.length; i++) {
//				if(fpxmss.length >10 && i==10){
//					fonttexts.add(new FontText("...", 50, 350 + i * 20, 16));
//					break;
//				}
//				fonttexts.add(new FontText(fpxmss[i - 1], 50, 350 + i * 20, 16));
//			}
//		} else if (!StringUtil.isEmpty(hvo.getFpxms())) {
//			fonttexts.add(new FontText(hvo.getFpxms(), 50, 350, 16));
//		}
//
//		// 发票金额
//		if (!StringUtil.isEmpty(hvo.getFpjes()) && hvo.getFpjes().indexOf(";") > 0) {
//			String[] fpjes = hvo.getFpjes().split(";");
//			for (int i = 1; i <= fpjes.length; i++) {
//				if(fpjes.length >10 && i==10){
//					fonttexts.add(new FontText("...", 670, 350 + i * 20, 16));
//					break;
//				}
//				fonttexts.add(new FontText(fpjes[i - 1], 670, 350 + i * 20, 16));
//			}
//		} else if (!StringUtil.isEmpty(hvo.getFpjes())) {
//			fonttexts.add(new FontText(hvo.getFpjes(), 670, 350, 16));
//		}
//
//		fonttexts.add(new FontText(hvo.getXfmc(), 260, 600));
//	}
//

//
//	private YntCpaccountVO getAccountByCode(String code, YntCpaccountVO[] accounts) {
//		for (YntCpaccountVO yntCpaccountVO : accounts) {
//			if (yntCpaccountVO.getAccountcode().equals(code))
//				return yntCpaccountVO;
//		}
//		return null;
//	}
//




//	@Override
//	public BusinessResonseBeanVO saveTickMsg(UserBeanVO uvo) throws DZFWarpException {
//
//		BusinessResonseBeanVO beanvo = new BusinessResonseBeanVO();
//
//		CorpVO cpvo = CorpCache.getInstance().get("", uvo.getPk_corp());
//
//		SuperVO vo = saveTickFromPt(uvo.getPk_corp(), null, uvo.getDrcode(), uvo.getAccount_id(), null,
//				3);
//
//		if (vo == null) {
//			throw new BusinessException("提示：查无此票！注意，发票信息会延迟一天，建议您开票次日扫描或拍照上传。");
//		} else {
//			beanvo.setRescode(IConstant.DEFAULT);
//			beanvo.setResmsg(vo);
//		}
//
//		return beanvo;
//	}
//
//	private void saveTickRecord(String pk_corp,String account_id, SuperVO vo) {
//		UserVO uvo =  userServiceImpl.queryUserJmVOByID(account_id) ;//UserCache.getInstance().get(account_id, "");
//		String user_code = null;
//		if (uvo != null) {
//			user_code = uvo.getUser_code();
//		}
//
//		TicketRecordVO recordvo = new TicketRecordVO();
//
//		recordvo.setPk_corp(pk_corp);
//
//		recordvo.setUser_code(user_code);
//
//		recordvo.setCuserid(account_id);
//
//		recordvo.setVgfmc(vo.getAttributeValue("gfmc")==null?"":(String)vo.getAttributeValue("gfmc"));
//
//		recordvo.setVxfmc(vo.getAttributeValue("xfmc")==null?"":(String)vo.getAttributeValue("xfmc"));
//
//		recordvo.setDsmdate(new DZFDateTime());
//
//		recordvo.setPk_zzstiket(vo.getPrimaryKey());
//
//		singleObjectBO.saveObject(pk_corp, recordvo);
//	}
//
//	public String getUserCode(String account_id){
//
//		if(StringUtil.isEmpty(account_id)){
//			throw new BusinessException("用户信息不能为空!");
//		}
//
//		StringBuffer qrysql = new StringBuffer();
//		qrysql.append(" select user_code from ");
//		qrysql.append(" app_temp_user ");
//		qrysql.append(" where nvl(dr,0)=0 and pk_temp_user = ?");
//		qrysql.append(" union all");
//		qrysql.append(" select user_code from ");
//		qrysql.append(" sm_user ");
//		qrysql.append(" where nvl(dr,0)=0 and pk_user =?  ");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(account_id);
//		sp.addParam(account_id);
//
//		List<String> usercodelist =  (List<String>) singleObjectBO.executeQuery(qrysql.toString(), sp, new ColumnListProcessor());
//
//		if(usercodelist==null || usercodelist.size()==0){
//			throw new BusinessException("用户信息不能为空!");
//		}
//
//		return usercodelist.get(0);
//
//	}
//
//	public ZzsTicketHVO getZzsVos(String drcode) throws DZFWarpException {
//
//		if (StringUtil.isEmpty(drcode)) {
//			throw new BusinessException("二维码信息为空!");
//		}
//
//		if (drcode.indexOf(",") < 0) {
//			throw new BusinessException("二维码信息缺失!");
//		}
//
//		String[] strs = drcode.split(",");
//
//		if (!strs[1].equals("01") && !strs[1].equals("04") && !strs[1].equals("10") && !strs[1].equals("51")) {
//			throw new BusinessException("暂不支持当前发票类型!");
//		}
//
//		ZzsTicketHVO hvo = null;
//
//		SQLParameter sp = new SQLParameter();
//		String qrysqlh = "select * from " + ZzsTicketHVO.TABLENAME + " where nvl(dr,0)=0 and drcode = ? ";
//		sp.addParam(drcode);
//		List<ZzsTicketHVO> hvoslist = (List<ZzsTicketHVO>) singleObjectBO.executeQuery(qrysqlh, sp,
//				new BeanListProcessor(ZzsTicketHVO.class));
//
//		if (hvoslist != null && hvoslist.size() > 0) {
//			hvo = hvoslist.get(0);
//			hvo.setFpzl(strs[1]);// 发票种类
//			sp.clearParams();
//			String bodysql = "select  * from " + ZzsTicketBVO.TABLENAME + " where nvl(dr,0)=0 and pk_zzstiket = ?";
//
//			sp.addParam(hvoslist.get(0).getPrimaryKey());// 二维码 默认是不一样的
//
//			List<ZzsTicketBVO> bvolists = (List<ZzsTicketBVO>) singleObjectBO.executeQuery(bodysql, sp,
//					new BeanListProcessor(ZzsTicketBVO.class));
//
//			if (bvolists != null && bvolists.size() > 0) {
//				hvo.setChildren(bvolists.toArray(new ZzsTicketBVO[0]));
//			}
//		}
//		return hvo;
//	}
//
//
//
//	/**
//	 * 获取参数设置
//	 */
//	private boolean isParamSysCreatePZ(String pk_corp) throws DZFWarpException{
//		boolean result = false;
//		YntParameterSet parameter = paramService.queryParamterbyCode(pk_corp, PhotoParaCtlState.PhotoParaCtlCode);
//		if(parameter == null){
//			return true;
//		}
//		Integer pardetailValue = parameter.getPardetailvalue();
//		if(pardetailValue == PhotoParaCtlState.PhotoParaCtlValue_Sys){//参数如果不是会计公司生成，返回
//			result = true;
//		}
//		return result;
//	}
//

//	/**
//	 * 从票通，获取发票信息
//	 * @param uvo
//	 * @return
//	 */
//	public SuperVO saveTickFromPt(String pk_corp ,String admincorpid,
//			String drcode,String account_id,Integer power,Integer limitcount)  throws DZFWarpException{
//
//		//校验是否有该权限
//		String corpid = validateTickFromPt(pk_corp, admincorpid, drcode, account_id, limitcount);
//
//		CorpVO cpvo  = CorpCache.getInstance().get("", pk_corp);
//
//		SuperVO vo = getZzsVos(drcode);
//
//		if (vo == null) {
//
//			FpMsgClient fpclient = new FpMsgClient();
//
//			String[] value = fpclient.sendPostXml(drcode);
//
//			// 根据生成的value 解析xml
//			GenTicketUtil util = new GenTicketUtil();
//
//			vo = util.genTickMsgVO(value[0], drcode, account_id);
//
//			vo.setAttributeValue("drcode", drcode);
//
//			vo.setAttributeValue("pk_corp", corpid);
//
//			vo = singleObjectBO.saveObject(Common.tempidcreate, vo);
//
//		}
//		String firstcorpid = pk_corp;
//		String memo = "商品收入";
//		if(vo instanceof ZzsTicketHVO){
//			SuperVO[] childvo= vo.getChildren();
//			if(cpvo!=null){
//				if(cpvo.getUnitname().equals(((ZzsTicketHVO) vo).getGfmc())){
//					((ZzsTicketHVO) vo).setFplx("0");
//					((ZzsTicketHVO) vo).setFplx_str("进项发票");
//				}else if(cpvo.getUnitname().equals(((ZzsTicketHVO) vo).getXfmc())){
//					((ZzsTicketHVO) vo).setFplx("1");
//					((ZzsTicketHVO) vo).setFplx_str("销项发票");
//				}
//			}
//
//			if(childvo!=null && childvo.length>0){
//				ZzsTicketBVO bvo =null;
//				DZFDouble jshj = DZFDouble.ZERO_DBL;
//				DZFDouble se = DZFDouble.ZERO_DBL;
//				DZFDouble je = DZFDouble.ZERO_DBL;
//				for(SuperVO bvotemp:childvo){
//					bvo = (ZzsTicketBVO) bvotemp;
//					if(!StringUtil.isEmpty( bvo.getHwmc()) && (bvo.getHwmc().indexOf("服务")>0
//							|| bvo.getHwmc().indexOf("费")>0 || bvo.getHwmc().indexOf("维修")>0)){
//						memo = "服务收入";
//					}
//					if(!StringUtil.isEmpty(bvo.getJe())){
//						je = new DZFDouble(bvo.getJe());
//					}else{
//						je = DZFDouble.ZERO_DBL;
//					}
//					if(!StringUtil.isEmpty(bvo.getSe())){
//						se = new DZFDouble(bvo.getSe());
//					}else{
//						se = DZFDouble.ZERO_DBL;
//					}
//					jshj =  SafeCompute.add(je, se);
//					bvotemp.setAttributeValue("jshj", AppStringUtils.fmtMicrometer(jshj.toString()));
//				}
//			}
//		}
//
//		// 保存扫描记录
//		saveTickRecord(corpid, account_id, vo);
//
//		//管理端赋值公司列表(小微无忧app使用)
//		firstcorpid = putAdminCorpList(admincorpid, account_id, power, vo, firstcorpid);
//
//		putMemoAndPayMend(firstcorpid, vo, memo);
//
//		return vo;
//	}
//
//	private String validateTickFromPt(String pk_corp, String admincorpid, String drcode, String account_id,
//			Integer limitcount) {
//		if(StringUtil.isEmpty(drcode)){
//			throw new BusinessException("发票二维码信息不全!");
//		}
//		String corpid = pk_corp;
//		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
//			corpid = admincorpid;
//		}
//		if(AppCheckValidUtils.isEmptyCorp(corpid)){
//			throw new BusinessException("公司不能为空!");
//		}
//
//		//是否有次数限制
//		StringBuffer checksql = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		checksql.append(" select count(1) ");
//		checksql.append(" from " + TicketRecordVO.TABLE_NAME);
//		checksql.append(" where 1=1 ");
//		checksql.append(" and pk_corp = ? and cuserid = ?  ");
//		checksql.append(" and dsmdate like ?");
//		sp.addParam(corpid);
//		sp.addParam(account_id);
//		sp.addParam(new DZFDate().toString() + "%");
//
//		BigDecimal resbig = (BigDecimal) singleObjectBO.executeQuery(checksql.toString(), sp,
//				new ColumnProcessor());
//
//		if (limitcount!=null && resbig != null && resbig.intValue() >= limitcount) {
//			throw new BusinessException("超过您当前最多查验次数!");
//		}
//		return corpid;
//	}
//
//	private String putAdminCorpList(String admincorpid, String account_id, Integer power, SuperVO vo,
//			String firstcorpid) {
//		if (!StringUtil.isEmpty(admincorpid)) {
//
//			String gfmc = (String) vo.getAttributeValue("gfmc");// 购物方名称
//
//			String xfmc = (String) vo.getAttributeValue("xfmc");
//
//			String[] cpnames = new String[] { gfmc, xfmc };
//
//			Map<String, String> tempmap = apppubservice.getCorpid(cpnames, admincorpid, account_id, power);// 公司对照
//
//			String[][] strs = null;
//			if (tempmap != null && tempmap.size() > 0) {
//				strs = new String[tempmap.size()][];
//				int index = 0;
//				for (Entry<String, String> entry : tempmap.entrySet()) {
//					strs[index] = new String[] {entry.getKey(), entry.getValue() };
//					index++;
//				}
//			}
//
//			String[] strtemp = null;
//			if(strs!=null && strs.length==2 && strs[1][1].equals(gfmc)){//如果购方在下面，则调下顺序
//				strtemp = strs[0];
//				strs[0]= strs[1];
//				strs[1]= strtemp;
//			}
//
//			if (strs != null && strs.length > 0) {
//				List<MapBean> beanlist = new ArrayList<MapBean>();
//				MapBean beantemp =null;
//				for(String[] temps :strs){
//					beantemp = new MapBean();
//					beantemp.setClient_id(temps[0]);
//					beantemp.setClient_name(temps[1]);
//					beanlist.add(beantemp);
//				}
//
//				vo.setAttributeValue("kpgsmap", beanlist);
//
//				firstcorpid = beanlist.get(0).getClient_id();
//			} else {
//				vo.setAttributeValue("scanmsg", "无该公司上传权限");
//			}
//		}
//		return firstcorpid;
//	}
//
//	private void putMemoAndPayMend(String pk_corp, SuperVO vo, String memo) {
//		//判断摘要结算方式
//		CorpVO corpvo = CorpCache.getInstance().get("", pk_corp);
//		if(corpvo!=null){
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(corpvo.getPk_corp());
//			CorpTaxVo[] corptaxvos = (CorpTaxVo[]) singleObjectBO.queryByCondition(CorpTaxVo.class, "nvl(dr,0)=0 and pk_corp = ?", sp);
//			String corpname = corpvo.getUnitname();
//			String taxcode ="";// corpvo.getTaxcode();
//			if(corptaxvos!=null && corptaxvos.length>0){
//				taxcode = corptaxvos[0].getTaxcode();
//			}
//			if((!StringUtil.isEmpty(((ZzsTicketHVO) vo).getGfmc())
//					&& ((ZzsTicketHVO) vo).getGfmc().equals(corpname))
//					|| (!StringUtil.isEmpty(((ZzsTicketHVO) vo).getGfsbh())
//					&& ((ZzsTicketHVO) vo).getGfsbh().equals(taxcode))
//					){//进项发票
//				vo.setAttributeValue("memo", "办公费");
//				vo.setAttributeValue("method", PayMethodEnum.BANK.getCode());
//			}
//			if((!StringUtil.isEmpty(((ZzsTicketHVO) vo).getXfmc())
//					&& ((ZzsTicketHVO) vo).getXfmc().equals(corpname))
//					|| (!StringUtil.isEmpty(((ZzsTicketHVO) vo).getXfsbh())
//					&& ((ZzsTicketHVO) vo).getXfsbh().equals(taxcode))
//					){//销项发票
//				vo.setAttributeValue("memo", memo);
//				vo.setAttributeValue("method", PayMethodEnum.OTHER.getCode());
//			}
//		}
//		if(vo.getAttributeValue("memo") == null ){
//			vo.setAttributeValue("memo", "其他费用");
//		}
//		if(vo.getAttributeValue("method") == null ){
//			vo.setAttributeValue("method", PayMethodEnum.OTHER.getCode());
//		}
//	}
//
//	@Override
//	public ImageGroupVO saveImgFromTicket(UserBeanVO uvo) throws DZFWarpException {
//
//		if(AppCheckValidUtils.isEmptyCorp(uvo.getPk_corp())){
//			throw new BusinessException("您公司不存在!");
//		}
//
//		CorpVO cpvo = CorpCache.getInstance().get("", uvo.getPk_corp());
//
//		if(cpvo == null){
//			throw new BusinessException("您公司不存在!");
//		}
//
//		// 根据发票号获取发票的信息
//		ZzsTicketHVO zzshvo = getZzsVos(uvo.getDrcode());
//
//
//		if(uvo.getKpdate() == null){//如果开票日期为空，则取票据的开票日期
//			uvo.setKpdate(zzshvo.getKprq());
//		}
//
//		if(StringUtil.isEmpty(uvo.getMny())){//如果金额为空则取
//			uvo.setMny(zzshvo.getJshj());
//		}
//
//		if(zzshvo == null){
//			throw new BusinessException("生成图片失败，发票信息不存在!");
//		}
//
//		List<ImageGroupVO>  gplist = apppthand.qryGroupFromPt(uvo.getPk_corp(), zzshvo.getPk_zzstiket());
//
//		//获取图片组信息
//		ImageGroupVO groupvo = null;
//
//		if(gplist!=null && gplist.size() >1 ){
//			throw new BusinessException("发票已生成多张图片，请查询图片处理!");
//		} else if(gplist!=null && gplist.size() ==1 ){
//			groupvo = gplist.get(0);
//		}
//
//		if (groupvo == null) {
//			String path = genImage(cpvo.getUnitcode(),cpvo.getUnitname(), zzshvo); // 生成图片信息
//			groupvo = genImageGroup(cpvo, uvo, zzshvo.getPrimaryKey(), path); // 生成图片信息(图片信息是已生成凭证的信息)
//			log.info("地址>>>>>>>>>>>>>>>>>>>>>>>"+path);
//		}
//
//		//更新票据信息
//		groupvo.setSettlemode(uvo.getPaymethod());
//		groupvo.setMemo(uvo.getMemo());//摘要
//		groupvo.setMny(new DZFDouble(uvo.getMny()));
//		groupvo.setMemo1(uvo.getMemo1());//备注
//		if(uvo.getKpdate()!=null){
//			groupvo.setCvoucherdate(new DZFDate(uvo.getKpdate()));//开票日期
//		}
//		singleObjectBO.update(groupvo);
//
//
//		return groupvo;
//	}
//
//	@Override
//	public List<ProblemVo> getProblems() throws DZFWarpException {
//
//		ProblemVo[] vos = (ProblemVo[]) singleObjectBO.queryByCondition(ProblemVo.class, "nvl(dr,0)=0 order by iorder",
//				new SQLParameter());
//
//		if (vos == null || vos.length == 0) {
//			throw new BusinessException("暂无数据");
//		}
//
//		return Arrays.asList(vos);
//	}
//
//	@Override
//	public List<MoreServiceHVo> queryMoreService(String pk_corp) throws DZFWarpException {
//
//		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
//			throw new BusinessException("非签约公司");
//		}
//
//		CorpVO cpvo = CorpCache.getInstance().get("", pk_corp);
//		if (cpvo == null) {
//			throw new BusinessException("该签约公司不存在");
//		}
//
//		List<MoreServiceBVo> qrylist = queryMoreDetail(cpvo,"");
//
//		Map<String, List<MoreServiceBVo>> result_map = new LinkedHashMap<String, List<MoreServiceBVo>>();
//		List<MoreServiceHVo> result = new ArrayList<MoreServiceHVo>();
//		if (qrylist != null && qrylist.size() > 0) {
//			String fullname = "";
//			for (MoreServiceBVo bvo : qrylist) {
//				// 价格处理
//				bvo.setJg(Common.format(VoUtils.getDZFDouble(bvo.getNprice())));
//				if (!StringUtil.isEmpty(bvo.getVimageurl())) {// 图片路径
//					bvo.setVimageurl(CryptUtil.getInstance().encryptAES(bvo.getVimageurl()));
//				}
//				fullname = getFullName(bvo.getVprovincename(), bvo.getVcityname(), bvo.getVareaname());
//				if (result_map.containsKey(fullname)) {
//					result_map.get(fullname).add(bvo);
//				} else {
//					List<MoreServiceBVo> temp_list = new ArrayList<MoreServiceBVo>();
//					temp_list.add(bvo);
//					result_map.put(fullname, temp_list);
//				}
//			}
//			// map转换成list
//			MoreServiceHVo hvo = null;
//			for (Entry<String, List<MoreServiceBVo>> entry : result_map.entrySet()) {
//				hvo = new MoreServiceHVo();
//				hvo.setMc(entry.getKey());
//				hvo.setBlist(entry.getValue());
//				result.add(hvo);
//			}
//		}
//		return result;
//	}
//
//	private List<MoreServiceBVo> queryMoreDetail(CorpVO cpvo,String id) {
//		StringBuffer qrysql = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//
//		qrysql.append(" select a.vprovincename, ");
//		qrysql.append("   a.vcityname, ");
//		qrysql.append("   a.vareaname, ");
//		qrysql.append("   b.vbusitypename, ");
//		qrysql.append("   b.vphone, ");
//		qrysql.append("   b.user_name, ");
//		qrysql.append("   b.nprice, ");
//		qrysql.append("   t.unitname, ");
//		qrysql.append("   e.vimageurl, ");
//		qrysql.append("   b.pk_busitype , ");//小类id
//		qrysql.append("   b.pk_prorelease_b , b.iprovince ,b.icity ,b.iarea   ");
//		qrysql.append(" from wz_prorelease a ");
//		qrysql.append(" inner join wz_prorelease_b b ");
//		qrysql.append("    on a.pk_prorelease = b.pk_prorelease ");
//		qrysql.append(" left join wz_serviceproset e ");
//		qrysql.append("   on b.pk_busitype = e.pk_busitype ");
//		qrysql.append(" left join bd_account t on a.pk_corp = t.pk_corp ");
//		qrysql.append(" where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(e.dr,0)=0  ");
//		qrysql.append(" and a.pk_corp = ?  and b.billstatus = 3");//审核通过
//		sp.addParam(cpvo.getFathercorp());
//		if(!StringUtil.isEmpty(id)){//小类id
//			qrysql.append(" and  b.pk_prorelease_b = ? ");
//			sp.addParam(id);
//		}
//		qrysql.append(" order by  a.vprovincename,a.vcityname,a.vareaname");
//
//		List<MoreServiceBVo> qrylist = (List<MoreServiceBVo>) singleObjectBO.executeQuery(qrysql.toString(), sp,
//				new BeanListProcessor(MoreServiceBVo.class));
//
//		QueryDeCodeUtils.decKeyUtils(new String[]{"unitname"}, qrylist, 1);
//
//		return qrylist;
//	}
//
//	private String getFullName(String province, String city, String area) {
//		String value = "";
//		if (!StringUtil.isEmpty(province)) {
//			value = province;
//		}
//		if (!StringUtil.isEmpty(city)) {
//			value = value + "-" + city;
//		}
//		if (!StringUtil.isEmpty(area)) {
//			value = value + "-" + area;
//		}
//
//		return value;
//
//	}
//
//	@Override
//	public MoreServiceBVo queryMoreServiceDetail(String pk_corp, String id,String domain) throws DZFWarpException {
//		if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
//			throw new BusinessException("非签约公司");
//		}
//		CorpVO cpvo = CorpCache.getInstance().get("", pk_corp);
//		if (cpvo == null) {
//			throw new BusinessException("该签约公司不存在");
//		}
//
//		List<MoreServiceBVo> qrylist = queryMoreDetail(cpvo,id);
//
//		if(qrylist!=null && qrylist.size()>0){
//			MoreServiceBVo vo = qrylist.get(0);
//			vo.setJg(Common.format(VoUtils.getDZFDouble(vo.getNprice())));
//			if (!StringUtil.isEmpty(vo.getVimageurl())) {// 图片路径
//				vo.setVimageurl(CryptUtil.getInstance().encryptAES(vo.getVimageurl()));
//			}
//			//获取服务详情
//			SQLParameter sp = new SQLParameter();
//			StringBuffer qrysql = new StringBuffer();
//			qrysql.append(" select vservicepre ");
//			qrysql.append(" from wz_servicedes_b b ");
//			qrysql.append(" where  nvl(dr,0)=0 and pk_busitype = ? ");
//			sp.addParam(vo.getPk_busitype());
//			if(vo.getIprovince()!=null ){
//				qrysql.append(" and iprovince =? ");
//				sp.addParam(vo.getIprovince());
//			}
//			if(vo.getIcity()!=null){
//				qrysql.append(" and icity = ?  ");
//				sp.addParam(vo.getIcity());
//			}
//			if(vo.getIarea()!=null){
//				qrysql.append("  and iarea = ?  ");
//				sp.addParam(vo.getIarea());
//			}
//			List<String> strlist =  (List<String>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BaseProcessor(){
//				@Override
//				public Object processResultSet(ResultSet res) throws SQLException {
//					List<String> reslist = new ArrayList<String>();
//					try {
//						while(res.next()){
//							if(res.getBlob("vservicepre") !=null){
//								reslist.add(new String(res.getBlob("vservicepre").getBytes((long)1, (int)res.getBlob("vservicepre").length()),"utf-8"));
//							}
//						}
//					} catch (UnsupportedEncodingException e) {
//					}
//					return reslist;
//				}
//			});
//			if(strlist!=null &&strlist.size()>0){
//				StringBuffer script_str = new StringBuffer();
//				//样式
//				script_str.append(" <style> ");
//				script_str.append(" .serviceIntroduction img { ");
//				script_str.append("   max-width: 100%; ");
//				script_str.append(" } ");
//				script_str.append(" .serviceIntroduction table { ");
//				script_str.append("   width: 100%; ");
//				script_str.append(" } ");
//				script_str.append(" </style> ");
//				//-------------脚本
//				script_str.append("<script> ");
//				script_str.append(" window.onload = function(){ ");
//				script_str.append("     var aImgs = document.getElementsByTagName('img'); ");
//				script_str.append("  for(var i=0;i < aImgs.length;i++){ ");
//				script_str.append("  aImgs[i].src = '"+domain+"/'+aImgs[i].getAttribute('src');");
//				script_str.append("  } ");
//				script_str.append("   }; ");
//				script_str.append("</script> " );
//				script_str.append("<div class = 'serviceIntroduction'>");
//				script_str.append(strlist.get(0));
//				script_str.append("</div>");
//				vo.setDetais(script_str.toString());
//			}
//			return vo;
//		}
//
//		return null;
//	}
//
}

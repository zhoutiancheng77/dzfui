package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDGenerate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxDeclarationService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptService;
import com.dzf.zxkj.platform.service.taxrpt.bo.RptBillFactory;
import com.dzf.zxkj.platform.service.taxrpt.jiangsurequest.ITaxRequestSrv;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.taxrpt.TaxReportPath;
import com.dzf.zxkj.platform.util.taxrpt.TaxRptemptools;
import com.dzf.zxkj.platform.util.taxrpt.conn.ConnPhantomjsPoolUtil;
import com.dzf.zxkj.platform.util.taxrpt.phantomjs.Phantomjs;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("taxdection_serv1")
@Slf4j
public class TaxDeclarationServiceImpl implements ITaxDeclarationService {

	@Autowired
	private SingleObjectBO sbo;

	private IUserService iuserService;

	private ITaxRequestSrv taxrequestsrv;

	@Autowired
	private ITaxBalaceCcrService taxbalancesrv;

	private ISysMessageJPush sysmsgsrv;// 推送
	@Autowired
	private RptBillFactory rptbillfactory;
	
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private ICorpService corpService;

	public ISysMessageJPush getSysmsgsrv() {
		return sysmsgsrv;
	}

	@Autowired
	public void setSysmsgsrv(ISysMessageJPush sysmsgsrv) {
		this.sysmsgsrv = sysmsgsrv;
	}

//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	@Override
//	public List<TaxZsxmVO> getClassifyComboboxData() throws DZFWarpException {
//
//		String condition = "nvl(dr,0)=0 order by zsxm_dm";
//		List list = (List<TaxZsxmVO>) sbo.retrieveByClause(TaxZsxmVO.class, condition, null);
//		if (list != null) {
//			log.info("获取征收项目条数为：" + list.size());
//		}
//		return list;
//
//	}

	public IUserService getIuserService() {
		return iuserService;
	}

	@Autowired
	public void setIuserService(IUserService iuserService) {
		this.iuserService = iuserService;
	}

	private boolean checkAuthority(UserVO userVO, String pk_corp) {
		Set<String> corps = getIuserService().querypowercorpSet(userVO.getCuserid());
		if (corps == null || !corps.contains(pk_corp)) {

			return false;
		}
		return true;
	}

//	/****
//	 * 获取申报种类信息
//	 * 
//	 * @throws DZFWarpException
//	 */
//	@Override
//	public List<TaxSbzlVO> getSbzlComboboxData(String pk_corp, UserVO userVO) throws DZFWarpException {
//
//		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), pk_corp);
//
//		if (checkAuthority(userVO, corpvo.getPk_corp()) == false) {
//			throw new BusinessException("对不起，您无操作权限！");
//		}
//
//		String stype = corpvo.getChargedeptname(); // 纳税人规模
//		String sb_zlbh = "'0'";
//		if (stype != null) {
//			if (stype.startsWith("一般纳税人")) {
//				sb_zlbh += ",'10101'";
//			} else if (stype.startsWith("小规模纳税人")) {
//				sb_zlbh += ",'10102'";
//			}
//		}
//		String condition = "nvl(dr,0)=0 and sb_zlbh in (" + sb_zlbh + ") order by sb_zlbh";
//		List list = (List<TaxSbzlVO>) sbo.retrieveByClause(TaxSbzlVO.class, condition, null);
//		if (list != null) {
//			log.debug("获取申报应用类型条数为：" + list.size());
//		}
//		return list;
//
//	}

	public SingleObjectBO getSbo() {
		return sbo;
	}

	public void setSbo(SingleObjectBO sbo) {
		this.sbo = sbo;
	}

//	/**
//	 * 获取token
//	 *
//	 * @param pk_corp
//	 * @return
//	 */
//	private String getToken(String pk_corp) throws DZFWarpException {
//		if (1 == 1)
//			return "";
//		String strReturn = null;
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		UserVO[] uservos = (UserVO[]) getSbo().queryByCondition(UserVO.class,
//				"nvl(dr,0)=0 and zfuser_code is not null and zf_refreshtoken is not null and pk_corp=?", params);
//		if (uservos != null && uservos.length > 0) {
//			strReturn = getZFTokenService().getZFToken(uservos[0].getUser_code());
//		}
//		return strReturn;
//	}

//	/**
//	 * 查询报表VO
//	 * 
//	 * @param paravo
//	 * @param userVO
//	 * @param pk_taxtypelistdetail
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	@Override
//	public TaxReportVO[] getTaxReportVO(TaxReportVO paravo, UserVO userVO,
//			String pk_taxtypelistdetail) throws DZFWarpException {
//		TaxTypeListDetailVO typedetailvo = (TaxTypeListDetailVO) getSbo()
//				.queryByPrimaryKey(TaxTypeListDetailVO.class,
//						pk_taxtypelistdetail);
//		TaxTypeListVO typelistvo = (TaxTypeListVO) getSbo().queryByPrimaryKey(
//				TaxTypeListVO.class, typedetailvo.getPk_taxtypelist());
//
//		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(),
//				typelistvo.getPk_corp());
//
//		if (checkAuthority(userVO, corpvo.getPk_corp()) == false) {
//			throw new BusinessException("对不起，您无操作权限！");
//		}
//		ITaxRptService rpt = rptbillfactory.produce(corpvo);
//		String location = rpt.getLocation(corpvo);
//		paravo.setLocation(location);
//
//		// 检查是否有上期报表，是否已审核
//		checkPrevPeriodApprove(paravo);
//
//		TaxReportVO[] vos = null;
//		String strCondition = " nvl(dr,0) = 0 ";
//		SQLParameter param = new SQLParameter();
//
//		// 公司
//		if (paravo.getPk_corp() != null) {
//			strCondition += " and pk_corp=?";
//			param.addParam(paravo.getPk_corp());
//		}
//		// 纳税人识别号
//		paravo.setNsrsbh(typelistvo.getNsrsbh());
//		// 纳税人电子档案号
//		paravo.setNsrdzdah(typelistvo.getNsrdzdah());
//		// 征收项目代码
//		if (typedetailvo.getZsxm_dm() != null) {
//			strCondition += " and zsxm_dm=?";
//			param.addParam(typedetailvo.getZsxm_dm());
//			paravo.setZsxm_dm(typedetailvo.getZsxm_dm());
//		}
//		// 填报周期 periodtype INTEGER FALSE FALSE FALSE
//		if (typedetailvo.getPeriodtype() != null) {
//			strCondition += " and periodtype=?";
//			param.addParam(typedetailvo.getPeriodtype());
//			paravo.setPeriodtype(typedetailvo.getPeriodtype());
//		}
//		// 税款所属时间起 periodfrom CHAR(10) 10 FALSE FALSE FALSE
//		if (typedetailvo.getPeriodfrom() != null) {
//			strCondition += " and periodfrom=?";
//			param.addParam(typedetailvo.getPeriodfrom());
//			paravo.setPeriodfrom(typedetailvo.getPeriodfrom());
//		}
//		// 税款所属时间止 periodto CHAR(10) 10 FALSE FALSE FALSE
//		if (typedetailvo.getPeriodto() != null) {
//			strCondition += " and periodto=?";
//			param.addParam(typedetailvo.getPeriodto());
//			paravo.setPeriodto(typedetailvo.getPeriodto());
//		}
//		// 申报种类编号
//		if (typedetailvo.getSb_zlbh() != null) {
//			strCondition += " and sb_zlbh=?";
//			param.addParam(typedetailvo.getSb_zlbh());
//			paravo.setSb_zlbh(typedetailvo.getSb_zlbh());
//		}
//		
//		try {
//			LockUtil.getInstance().tryLockKey(corpvo.getPk_corp(), strCondition, 30);
//			vos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class, strCondition, param);
//			if (vos == null || vos.length == 0) {
//				// // 根据pk_corp获得纳税人识别号
//				// String nsrsbh = corpvo.getTaxcode();
//				List<RptBillVO> volist = null;
//				volist = rpt.getRptBillVO(paravo, getSbo(), corpvo);
//				paravo.setVbillstatus(8);
//				paravo.setDr(0);
//				if (volist != null && volist.size() > 0) {
//					TaxReportVO taxrptvo = new TaxReportVO();
//					for (String key : taxrptvo.getAttributeNames()) {
//						if (key.equals("children"))
//							continue;
//						taxrptvo.setAttributeValue(key, paravo.getAttributeValue(key));
//					}
//					// 处理代账会计公司
//					if (corpvo.getIsaccountcorp() != null && corpvo.getIsaccountcorp().booleanValue()) {
//						// 当前公司是代账公司，报税公司就应该是自己
//						taxrptvo.setPk_corp_account(corpvo.getPrimaryKey());
//					} else {
//						// 父公司就是代账公司或者代账公司的分支机构
//						if (StringUtil.isEmpty(corpvo.getFathercorp())) {
//							throw new BusinessException("公司 [" + corpvo.getUnitname() + "] 没有代账公司，不能报税!");
//						} else {
//							taxrptvo.setPk_corp_account(corpvo.getFathercorp());
//						}
//					}
//
//					taxrptvo.setDoperatedate(new DZFDate());
//					taxrptvo.setDr(0);
//
//					taxrptvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_UnSubmit); // 101：未提交
//																				// 大账房自定义属性
//
//					String pk = IDGenerate.getInstance().getNextID(taxrptvo.getPk_corp());
//					taxrptvo.setPrimaryKey(pk);
//					getSbo().insertVOWithPK(taxrptvo);
//
//					TaxReportDetailVO[] detailvos = new TaxReportDetailVO[volist.size()];
//					String reportcodes = "";
//					for (RptBillVO billvo : volist) {
//						reportcodes += (reportcodes.length() == 0 ? "'" : ",'") + billvo.getBb_zlid() + "'";
//					}
//					SQLParameter param2 = new SQLParameter();
//					param2.addParam(paravo.getLocation());
//					param2.addParam(paravo.getSb_zlbh());//新增  申报种类编号
//					TaxRptTempletVO[] templetvos = (TaxRptTempletVO[]) getSbo().queryByCondition(TaxRptTempletVO.class,
//							"nvl(dr,0)=0 and reportcode in (" + reportcodes + ") and rtrim(location) =? and sb_zlbh = ? ", param2);
//					HashMap<String, TaxRptTempletVO> hmTaxRptTempletVO = new HashMap<String, TaxRptTempletVO>();
//					for (TaxRptTempletVO vo : templetvos) {
//						hmTaxRptTempletVO.put(vo.getReportcode(), vo);
//					}
//					int idetail = 0;
//					for (RptBillVO billvo : volist) {
//						TaxRptTempletVO templetvo = hmTaxRptTempletVO.get(billvo.getBb_zlid());
//
//						TaxReportDetailVO detailvo = new TaxReportDetailVO();
//						detailvo.setPk_corp(corpvo.getPk_corp());
//						detailvo.setOrderno(Integer.parseInt(billvo.getXh()));
//						detailvo.setTaxorder(templetvo.getTaxorder());
//						detailvo.setPk_taxreport(pk);
//						detailvo.setReportcode(billvo.getBb_zlid());
//						detailvo.setReportname(templetvo.getReportname());
//						detailvo.setSb_zlbh(templetvo.getSb_zlbh());
//						detailvo.setPk_taxrpttemplet(templetvo.getPrimaryKey());
//						detailvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_UnSubmit); // 101未提交
//						detailvo.setDr(0);
//						detailvo.setRows(idetail);
//						detailvos[idetail++] = detailvo;
//					}
//					getSbo().insertVOArr(taxrptvo.getPk_corp(), detailvos);
//					// 下面这句再查询更新TS用！
//					taxrptvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, taxrptvo.getPrimaryKey());
//					taxrptvo.setChildren(detailvos);
//					vos = new TaxReportVO[] { taxrptvo };
//				}
//			} else {
//				for (TaxReportVO pvo : vos) {
//					SQLParameter params = new SQLParameter();
//					params.addParam(pvo.getPrimaryKey());
//					TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(
//							TaxReportDetailVO.class, "nvl(dr,0)=0 and pk_taxreport=? order by orderno", params);
//					pvo.setChildren(detailvos);
//				}
//			}
//
//		} catch (Exception e) {
//			log.error("错误",e);
//		}finally {
//			LockUtil.getInstance().unLock_Key(corpvo.getPk_corp(), strCondition);
//		}
//
//		// 刷新列表时更新申报状态
//
//		for (TaxReportVO taxReportVO : vos) {
//			rpt.updateDeclareStatus(corpvo, taxReportVO);
//		}
//		return vos;
//	}

//	public IZFTokenService getZFTokenService() {
//		return zftokenservice;
//	}

//	public void setZFTokenService(IZFTokenService zftokenservice) {
//		this.zftokenservice = zftokenservice;
//	}
//
//	public ITaxRequestSrv getTaxrequestsrv() {
//		if (taxrequestsrv == null) {
//			taxrequestsrv = new TaxRequestSrvImpl();
//		}
//		return taxrequestsrv;
//	}

	private CorpVO getCorpVO(String pk_user, String pk_corp) {
		// CorpVO corpvo = CorpCache.getInstance().get(pk_user, pk_corp);
		CorpVO corpvo = (CorpVO) getSbo().queryByPrimaryKey(CorpVO.class, pk_corp);
		SuperVO[] corpvos = QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, new SuperVO[] { corpvo }, 1);
		return (CorpVO) corpvos[0];
	}

	/**
	 * 获取报表json格式字符串
	 * 
	 * @param pk_taxreport
	 * @param userVO
	 * @param reportname
	 * @param readonly
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public String getSpreadJSData(String pk_taxreport, UserVO userVO,String reportname,Boolean readonly) throws DZFWarpException {
		String sReturn = null;

		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		if(reportvo == null){
			throw new BusinessException("报表数据不存在！请刷新后操作");
		}
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		//查询子表数据
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxreport);
		TaxReportDetailVO[] vos = (TaxReportDetailVO[])
				getSbo().queryByCondition(TaxReportDetailVO.class, "nvl(dr,0)=0 and pk_taxreport=? order by orderno", params);
		if(vos == null || vos.length == 0){
			throw new BusinessException("报表子表数据不存在！请到纳税信息节点确认后，刷新重试");
		}
		
		//zpm
//
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_taxreport);
//		params.addParam(reportCode);
//		params.addParam(reportName);
//		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//				"nvl(dr,0)=0 and pk_taxreport=? and reportcode=? and reportname=?", params);
//
//		String sb_zlbh = vos[0].getSb_zlbh();
//
//		params = new SQLParameter();
//		params.addParam(pk_taxreport);
//		params.addParam(sb_zlbh);
//		TaxReportDetailVO[] vodetails = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//				"nvl(dr,0)=0 and pk_taxreport=? and sb_zlbh=? order by orderno", params);

		// 同一申报种类编号的模板组
		List<String> listRptName = new ArrayList<String>();
		for (TaxReportDetailVO detailvo : vos) {
			listRptName.add(detailvo.getReportname().trim());
		}

		if(StringUtil.isEmpty(reportname)){
			reportname = listRptName.get(0);
		}
		try {
			SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
			if (vos[0].getSpreadfile() == null || vos[0].getSpreadfile().trim().length() == 0) {
				// 无模板，重新生成
				// 模板主键
				String pk_templet = vos[0].getPk_taxrpttemplet();
				TaxRptTempletVO rpttempletvo = (TaxRptTempletVO) getSbo().queryByPrimaryKey(TaxRptTempletVO.class,
						pk_templet);
				String strJson = TaxRptemptools.readFileString(rpttempletvo.getSpreadtemplet());
				ObjectMapper objectMapper = getObjectMapper();
				Map objMap = (Map) objectMapper.readValue(strJson, LinkedHashMap.class);
				YntCpaccountVO[] accounts = accountService.queryByPk(corpvo.getPk_corp());
				Map objMapRet = spreadtool.fillDataToJsonTemplet(
						readonly || reportvo.getVbillstatus() != 8, objMap, listRptName, reportname, reportvo, corpvo,corptaxvo,
						getQcData(corpvo, reportvo), accounts);
				//  设置企业所得税的封面字段
				
				SpecialSheetSetter setter = new SpecialSheetSetter(sbo, new SpreadTool(taxbalancesrv), corpService);
				setter.setSpecialSheetDefaultValue(reportvo, objMapRet);
				spreadtool.getCellXYByName(objMapRet, "实缴额");
				sReturn = objectMapper.writeValueAsString(objMapRet);
			} else {
				File f = new File(vos[0].getSpreadfile());
				if ((f.exists() && f.isFile() ) || !StringUtil.isEmpty(vos[0].getSpreadfile())) {
					String strJson = TaxRptemptools.readFileString(vos[0].getSpreadfile());
					ObjectMapper objectMapper = getObjectMapper();
					Map objMap = (Map) objectMapper.readValue(strJson, HashMap.class);
					Map objMapRet = spreadtool.initOldReport(
							readonly || reportvo.getVbillstatus() != 8, objMap, listRptName,reportname , reportvo,
							corpvo);
					
					spreadtool.getCellXYByName(objMapRet, "实缴额");
					sReturn = objectMapper.writeValueAsString(objMapRet);
				} else {
					throw new BusinessException("服务器上缺少报表文件");
				}
			}
		} catch (DZFWarpException dzfe) {
			throw dzfe;
		} catch (Exception e) {
			throw new WiseRunException(e);
		}

		return sReturn;
	}

	@Override
	public String saveReport(String pk_taxreport, String corpid, String jsonString, UserVO userVO,
			String logindate, String ts) throws DZFWarpException {
		//查询主表数据
		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		if(reportvo == null)
			throw new BusinessException("当前数据已被删除，请刷新后操作！");
		//查询子表数据
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_taxreport);
		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,"nvl(dr,0)=0 and pk_taxreport=?", sp);
		if(detailvos == null || detailvos.length == 0)
			throw new BusinessException("当前报表数据子表信息不存在，请刷新后操作！");
		String sb_zlbh = reportvo.getSb_zlbh();
		if(StringUtil.isEmpty(sb_zlbh)){
			//根据 id查询得到sbcode
			TaxTypeSBZLVO sbzlvo = (TaxTypeSBZLVO)getSbo().queryByPrimaryKey(TaxTypeSBZLVO.class, reportvo.getPk_taxsbzl());
			sb_zlbh = sbzlvo.getSbcode();
		}
		// 检查时间戳
		// checkTS(reportvo, ts);
		//查验权限
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		if(!corpid.equals(reportvo.getPk_corp())){
			throw new BusinessException("对不起，操作非选择公司数据，请重试！");
		}
		//业务校验
		Map mapJson = readJsonValue(jsonString, LinkedHashMap.class);
		
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		
		String warningMessage = onCheckReportData(mapJson, reportvo, corptaxvo);
		
		dealBySpec(mapJson, reportvo, corptaxvo);

		String saveData = new SpreadTool(taxbalancesrv).adjustBeforeSave(jsonString, null, reportvo);
		String fastid = reportvo.getSpreadfile();
		if(StringUtil.isEmpty(fastid)){
			fastid = detailvos[0].getSpreadfile();
		}
		//存文件
		String filename = saveFileDfs(pk_taxreport, sb_zlbh, fastid ,saveData);

		// 更新其他相同申报种类编号的记录行
		for (TaxReportDetailVO detail : detailvos) {
			if (detail.getSb_zlbh().equals(sb_zlbh)) {
				detail.setSpreadfile(filename);
			}
		}
		getSbo().updateAry(detailvos);
		//更新主表
		reportvo.setDoperatedate(new DZFDate(logindate));
		reportvo.setCoperatorid(userVO.getPrimaryKey());
		//主表也记录文件
		reportvo.setSpreadfile(filename);
		getSbo().update(reportvo);
		return warningMessage;
	}

	@Override
	public String createReportDataByTemplate(TaxReportVO reportvo, CorpTaxVo corptaxvo) throws DZFWarpException {
		//查询子表数据
		SQLParameter sp = new SQLParameter();
		sp.addParam(reportvo.getPk_taxreport());
		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				" nvl(dr,0)=0 and pk_taxreport=? ", sp);
		String sb_zlbh = reportvo.getSb_zlbh();
		if(StringUtil.isEmpty(sb_zlbh)){
			//根据 id查询得到sbcode
			TaxTypeSBZLVO sbzlvo = (TaxTypeSBZLVO)getSbo().queryByPrimaryKey(TaxTypeSBZLVO.class, reportvo.getPk_taxsbzl());
			sb_zlbh = sbzlvo.getSbcode();
		}
		if(detailvos != null && detailvos.length > 0){
			if (StringUtil.isEmpty(detailvos[0].getSpreadfile())) {
				String jsonString = null;
				List<String> listRptName = new ArrayList<>();
				for (TaxReportDetailVO detailvo : detailvos) {
					listRptName.add(detailvo.getReportname().trim());
				}
				String pk_templet = detailvos[0].getPk_taxrpttemplet();
				TaxRptTempletVO rpttempletvo = (TaxRptTempletVO) getSbo().queryByPrimaryKey(TaxRptTempletVO.class,
						pk_templet);
				String strJson = TaxRptemptools.readFileString(rpttempletvo.getSpreadtemplet());
				ObjectMapper objectMapper = getObjectMapper();
				try {
					CorpVO corpvo = corpService.queryByPk(reportvo.getPk_corp());
					Map objMap = objectMapper.readValue(strJson, LinkedHashMap.class);
					YntCpaccountVO[] accounts = accountService.queryByPk(corpvo.getPk_corp());
					Map objMapRet = new SpreadTool(taxbalancesrv).fillDataToJsonTemplet(
							false, objMap, listRptName, listRptName.get(0), reportvo, corpvo,corptaxvo,
							getQcData(corpvo, reportvo), accounts);
					//  设置企业所得税的封面字段
					SpecialSheetSetter setter = new SpecialSheetSetter(sbo, new SpreadTool(taxbalancesrv), corpService);
					setter.setSpecialSheetDefaultValue(reportvo, objMapRet);

					jsonString = objectMapper.writeValueAsString(objMapRet);
				} catch (Exception e) {
					log.error("获取税表数据失败", e);
				}
				if (jsonString != null) {
					String saveData = new SpreadTool(taxbalancesrv).adjustBeforeSave(jsonString, null, reportvo);
					//存文件
					String filename = null;
					try {
						filename = saveFileDfs(reportvo.getPk_taxreport(), sb_zlbh, null ,saveData);
					} catch (Exception e) {
						log.error("自动生成税表-保存失败", e);
					}
					if (filename != null) {
						// 更新其他相同申报种类编号的记录行
						for (TaxReportDetailVO detail : detailvos) {
							if (detail.getSb_zlbh().equals(sb_zlbh)) {
								detail.setSpreadfile(filename);
							}
						}
						getSbo().updateAry(detailvos);
						//主表也记录文件
						reportvo.setSpreadfile(filename);
						getSbo().update(reportvo);
					}
				}
			}
		}
		return null;
	}
	
	private void dealBySpec(Map mapJson, TaxReportVO reportvo, CorpTaxVo corptaxvo){
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		List<String> listReportName = spreadtool.getReportNameList(mapJson);
		//处理表样实缴额
		dealTaxMny(mapJson, reportvo, corptaxvo, listReportName, spreadtool);
		//纳税信息维护 存期末从业人数
		dealTaxIDNumber(mapJson, reportvo, corptaxvo, listReportName, spreadtool);
	}
	
	private void dealTaxMny(Map mapJson, TaxReportVO reportvo, 
			CorpTaxVo corptaxvo,
			List<String> listReportName,
			SpreadTool spreadtool){
		Object[] xy = spreadtool.getCellXYByName(mapJson, "实缴额");
		if(xy != null && xy.length ==3){
			int x = (int) xy[0];
			int y = (int) xy[1];
			String name = (String) xy[2];
			DZFDouble value = getDzfDouble(spreadtool.getCellValue(mapJson,name,  y, x));
			reportvo.setTaxmny(value);//设置实缴额
		}
		
	}

	private void dealTaxIDNumber(Map mapJson, TaxReportVO reportvo, 
			CorpTaxVo corptaxvo,
			List<String> listReportName,
			SpreadTool spreadtool) throws DZFWarpException{
		if(PeriodType.jidureport != reportvo.getPeriodtype()){//不是季报表不走
			return;
		}
		
		String sbzlbh = reportvo.getSb_zlbh();
		String rname = null;
		int rowIndex = 0;
		int celIndex = 0;
		if(TaxRptConst.SB_ZLBH10412.equals(sbzlbh)){
			rname = "A200000所得税月(季)度预缴纳税申报表";
			
			if(isXiamenCorp(corptaxvo)){//厦门特殊处理
				rowIndex = 33;
				celIndex = 8;
			}else if(isHubeiCorp(corptaxvo)){
				rowIndex = 33;
				celIndex = 3;
			}else{
				rowIndex = 34;
				celIndex = 8;
			}
		}else if(TaxRptConst.SB_ZLBH10413.equals(sbzlbh)){
			rname = "主表";
			rowIndex = 24;
			celIndex = 8;
		}
		
		if(!StringUtil.isEmpty(rname)){
			
			if(listReportName != null && listReportName.contains(rname)){
				DZFDouble d = getDzfDouble(spreadtool.getCellValue(mapJson, rname, rowIndex, celIndex));
				if(d != null){
					String pk_corp = reportvo.getPk_corp();
					corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
					corptaxvo.setIdnumber(d.toString());
					sbo.saveObject(pk_corp, corptaxvo);
				}
			}
		}
	} 

	/**
	 * 以普通字符串方式保存spreadJS的json报表文件
	 * 
	 * @param filename
	 * @param data
	 * @throws DZFWarpException
	 */
	private void saveFile(String filename, String data) throws DZFWarpException {
		File f = new File(filename);
		if (f.exists()) {
			f.delete();
		}
		FileOutputStream fos = null;

		try {
			fos = new FileOutputStream(f);
			fos.write(data.getBytes("utf-8"));
			fos.flush();

		} catch (IOException e) {
			throw new WiseRunException(e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
				} catch (IOException e) {
					log.error("错误",e);
				}
			}
		}
	}

	private String saveFileDfs(String pk_taxreport, String sb_zlbh, String fastid, String data) throws DZFWarpException {
		//存文件
		DZFDate dzfnow = new DZFDate();
		String sFileDir = TaxReportPath.taxReportPath + "spreadfile/" + String.valueOf(dzfnow.getYear())
				+ String.valueOf(dzfnow.getMonth());
		String savedFile = null;
		String filename = sFileDir + "/spread" + pk_taxreport + sb_zlbh + ".ssjson";
		try {
			if(!StringUtil.isEmpty(data)){
				if(!StringUtil.isEmpty(fastid) && fastid.startsWith("*")){
					((FastDfsUtil) SpringUtils.getBean("connectionPool")).deleteFile(fastid.substring(1));
					log.error("删除成功,文件id:"+fastid.substring(1));
				}
				String id = ((FastDfsUtil)SpringUtils.getBean("connectionPool"))
						.upload(data.getBytes("utf-8"), filename, new HashMap<>());

				if(!StringUtil.isEmpty(id)){
					savedFile = "*"+id.substring(1);
				}else{
					throw new BusinessException("获取文件id失败!");
				}
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return savedFile;
	}

	private ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {

			@Override
			public void serialize(Object value, JsonGenerator jg, SerializerProvider sp)
					throws IOException, JsonProcessingException {
				jg.writeString("");
			}
		});
		objectMapper.setSerializationInclusion(Include.ALWAYS);
		objectMapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
		objectMapper.configure(SerializationFeature.WRITE_SINGLE_ELEM_ARRAYS_UNWRAPPED, false);
		objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
		return objectMapper;
	}

	private TaxRptTempletPosVO[] getRptTempletPosVOs(String pk_taxrpttemplet) throws DZFWarpException {
		// 查询行列对照数据表
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxrpttemplet);
		return (TaxRptTempletPosVO[]) getSbo().queryByCondition(TaxRptTempletPosVO.class,
				"nvl(dr,0)=0 and pk_taxrpttemplet=?", params);
	}

//	/**
//	 * 企业确认
//	 *
//	 * @param pk_taxreport
//	 * @param userVO
//	 * @param message
//	 * @throws DZFWarpException
//	 */
//	@Override
//	public void processEntConfirm(String pk_taxreport, UserVO userVO, String message) throws DZFWarpException {
//
//		ITaxMessageServer server = (ITaxMessageServer) SpringUtils.getBean("taxservice");
//
//		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
//
//		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
//			throw new BusinessException("对不起，您无操作权限！");
//		}
//
////		CorpVO corpvo  = CorpCache.getInstance().get(null, reportvo.getPk_corp());
//		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
//
//		if (!isBeijingCorp(taxvo) && reportvo.getVbillstatus() != TaxRptConst.IBILLSTATUS_APPROVEED) {//企业确认按钮去掉，该行代码不调整了，逻辑应该是所有地区都可以确认
//			throw new BusinessException("报表状态不是已审核状态，不能发送确认信息！");
//		}
//
//		String user = server.getManageUserid(reportvo.getPk_corp());
//		if (user == null) {
//			throw new BusinessException("没有找到报账公司的管理员人员信息!");
//		}
//
//		TaxReportEntVO reportentvo = new TaxReportEntVO();
//		reportentvo.setPk_taxreport(pk_taxreport);
//		reportentvo.setVbillstatus(TaxRptConst.IBILLSTATUS_UNCONFIRM);// 待确认
//		reportentvo.setEntmanagerinfo(message);// 企业主确认内容
//		reportentvo.setEntdate(new DZFDateTime().toString());// 企业确认时间
//		reportentvo.setPk_corp(reportvo.getPk_corp());
//		reportentvo.setPk_corp_account(reportvo.getPk_corp_account());
//		reportentvo.setPeriodtype(reportvo.getPeriodtype());// 周期
//		reportentvo.setDr(0);
//
//		JPMessageBean bean = new JPMessageBean();
//		Map<String, String> extras = new HashMap<String, String>();
//		extras.put("message", message);// 消息
//		extras.put("vbillstatus", reportentvo.getVbillstatus().toString());
//		extras.put("entdate", reportentvo.getEntdate());
//		extras.put("periodtype", reportvo.getPeriodtype().toString());// 周期 0
//																		// 月,1,季报,2年报
//		extras.put("pk_corp", reportvo.getPk_corp());// 公司
//		bean.setExtras(extras);
//		bean.setUserids(new String[] { user });// user "15115760510"
//		bean.setMessage(message);
//
//		// bean.setCorptag(new
//		// String[]{reportvo.getPk_corp()});//reportvo.getPk_corp() "lili001"
//
//		sysmsgsrv.sendSysMessage(bean);
//
//		server.insertTaxMessageVO(reportentvo);// 插入消息数据表
//		server.updateReprotStatus(reportvo, 9);// 更新单据状态
//	}
//
//	/**
//	 * 快捷申报
//	 *
//	 * @param pk_taxreport
//	 * @Param userVO
//	 * @throws DZFWarpException
//	 */
//	@Override
//	public void processShortDeclare(String pk_taxreport, UserVO userVO, String ts) throws DZFWarpException {
//		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
//
//		// 检查时间戳
//		// checkTS(reportvo, ts);
//
//		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
//			throw new BusinessException("对不起，您无操作权限！");
//		}
//
//		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());
//		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
//
//		if (reportvo.getVbillstatus() == TaxRptConst.IBILLSTATUS_UNAPPROVE) // 未审核
//		{
//			throw new BusinessException("报表未审核，不能申报.");
//		}
//		if ("101".equals(reportvo.getSbzt_dm()) == false) {
//			throw new BusinessException("申报状态代码不是待提交状态，不能申报.");
//		}
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_taxreport);
//		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//				"nvl(dr,0)=0 and pk_taxreport=?", params);
//		// 正在处理的申报种类编号
//		String sb_zlbh = "";
//
//		HashMap<String, Object> hmReportValue = new HashMap<String, Object>();
//
//		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
//		Map<String, Map> hmMapValue = null;
//		for (TaxReportDetailVO detailvo : detailvos) {
//			if (detailvo.getSb_zlbh().equals(sb_zlbh) == false) {
//				sb_zlbh = detailvo.getSb_zlbh();
//				hmMapValue = spreadtool.getMapValue(TaxRptemptools.readFileString(detailvo.getSpreadfile()));
//			}
//
//			TaxRptTempletPosVO[] posvos = getRptTempletPosVOs(detailvo.getPk_taxrpttemplet());
//
//			spreadtool.fillReportVOFValue(hmReportValue, posvos, hmMapValue.get(detailvo.getReportname()));
//
//		}
//		// 处理公共表头数据
//		// 税款所属时间_年
//		hmReportValue.put("qsrq_y", reportvo.getPeriodfrom().substring(0, 4));
//		hmReportValue.put("qsrq_m", reportvo.getPeriodfrom().substring(5, 7));
//		hmReportValue.put("qsrq_d", reportvo.getPeriodfrom().substring(8, 10));
//		hmReportValue.put("jzrq_y", reportvo.getPeriodto().substring(0, 4));
//		hmReportValue.put("jzrq_m", reportvo.getPeriodto().substring(5, 7));
//		hmReportValue.put("jzrq_d", reportvo.getPeriodto().substring(8, 10));
//		// 纳税人识别号，名称
//		hmReportValue.put("nsrsbh", taxvo.getTaxcode());
//		hmReportValue.put("nsrmc", corpvo.getUnitname());
//		// 填报日期
//		hmReportValue.put("tbrq_y", "" + reportvo.getDoperatedate().getYear());
//		hmReportValue.put("tbrq_m", "" + reportvo.getDoperatedate().getMonth());
//		hmReportValue.put("tbrq_d", "" + reportvo.getDoperatedate().getDay());
//		try {
//			String jsonrpt = getObjectMapper().writeValueAsString(hmReportValue).toUpperCase();
//			StringBuffer req_data = new StringBuffer();
//			// req_data.append("<![CDATA[");
//			req_data.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
//			req_data.append("<REQUEST>");
//			req_data.append("<BUSINESS_DATALIST>");
//			req_data.append("<BUSINESS_DATA>");
//			req_data.append(
//					"<pdfFormData>{\"opinion\":[],\"sub\":[{\"tableName\":\"sb_ybnsr_dkdjsstzdkqd\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_trccfhdncpzzs\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_cbfhdncpzzsjxse\",\"fields\":[]},");
//			req_data.append(
//					"{\"tableName\":\"sb_ybnsr_gjncpzjxshdncpzzs\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_gjycpyyscyjbgchdncpzz\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_jmsmx_jsxm\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_jmsmx_msxm\",\"fields\":[]}],\"main\":{\"fields\":");
//			req_data.append(jsonrpt);
//			req_data.append("}}</pdfFormData>");
//
//			req_data.append(
//					"<formData>{\"opinion\":[],\"sub\":[{\"tableName\":\"sb_ybnsr_dkdjsstzdkqd\",\"fields\":[]},");
//			req_data.append(
//					"{\"tableName\":\"sb_ybnsr_trccfhdncpzzs\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_cbfhdncpzzsjxse\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_gjncpzjxshdncpzzs\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_gjycpyyscyjbgchdncpzz\",\"fields\":[]},");
//			req_data.append(
//					"{\"tableName\":\"sb_ybnsr_jmsmx_jsxm\",\"fields\":[]},{\"tableName\":\"sb_ybnsr_jmsmx_msxm\",\"fields\":[]}],\"main\":{\"fields\":");
//			req_data.append(jsonrpt);
//			req_data.append("}}</formData>");
//			req_data.append("<mainTableName>sb_ybnsr</mainTableName>");
//			req_data.append("</BUSINESS_DATA>");
//			req_data.append("</BUSINESS_DATALIST>");
//			req_data.append("</REQUEST>");
//
//			// req_data.append("]]>");
//
//			// System.out.println(req_data.toString());
//
//			getTaxrequestsrv().rptSubmit(getToken(reportvo.getPk_corp()), reportvo, req_data.toString());
//			reportvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_Submitted); // 已提交，不是税务系统返回的，只是自己发送成功临时标注，
//																		// 真正状态需要从税务系统刷新。
//			reportvo.setTs(new DZFDateTime());
//			getSbo().update(reportvo);
//
//			for (TaxReportDetailVO vo : detailvos) {
//				vo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_Submitted);
//			}
//			getSbo().updateAry(detailvos);
//
//		} catch (DZFWarpException dzfe) {
//			throw dzfe;
//		} catch (Exception e) {
//			throw new WiseRunException(e);
//		}
//	}

	/**
	 * 刷新申报状态
	 * 
	 * @param pk_taxreport
	 * @param userVO
	 * @throws DZFWarpException
	 */
	@Override
	public TaxReportVO processRefreshDclStatus(String pk_taxreport, UserVO userVO) throws DZFWarpException {

		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		if (reportvo.getSbzt_dm().equals("101")) {
			throw new BusinessException("未申报的报表不能刷新申报状态!");
		}
		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		if (checkAuthority(userVO, corpvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		ITaxRptService taxRptService = rptbillfactory.produce(corptaxvo);
		taxRptService.getDeclareStatus(corpvo, corptaxvo,reportvo);
		return reportvo;
	}

	/**
	 * 生成pdf文件
	 * 
	 * @param detailvos
	 * @throws DZFWarpException
	 */
	private void createPdfFile(TaxReportDetailVO[] detailvos, TaxReportVO reportvo, CorpVO corpvo,CorpTaxVo taxvo)
			throws DZFWarpException {
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		String sb_zlbh = "";
		Map hmMapValue = null;
		for (TaxReportDetailVO detailvo : detailvos) {
			if (detailvo.getSb_zlbh().equals(sb_zlbh) == false) {
				String strJson = TaxRptemptools.readFileString(detailvo.getSpreadfile());
				hmMapValue = spreadtool.getMapValue(strJson);
				sb_zlbh = detailvo.getSb_zlbh();
			}
			if (detailvo.getPdffile() != null && detailvo.getPdffile().trim().length() > 0) {
				File oldfile = new File(detailvo.getPdffile());
				if (oldfile.exists()) {
					oldfile.delete();
				}
			}
			TaxRptTempletVO templetvo = (TaxRptTempletVO) getSbo().queryByPrimaryKey(TaxRptTempletVO.class,
					detailvo.getPk_taxrpttemplet());

			String pdffilename = TaxReportPath.taxReportPath + "pdffile/pdf" + detailvo.getPk_taxreport()
					+ detailvo.getReportcode() + ".pdf";
			detailvo.setPdffile(pdffilename);
			File pdfFileOut = new File(pdffilename);
			spreadtool.fillPDFValue(templetvo.getPdftemplet(), reportvo,taxvo,
					getRptTempletPosVOs(detailvo.getPk_taxrpttemplet()),
					(HashMap) hmMapValue.get(detailvo.getReportname()), pdfFileOut, corpvo);

		}

	}

	private <T> T readJsonValue(String strJSON, Class<T> clazz) throws DZFWarpException {
		try {
			return getObjectMapper().readValue(strJSON, clazz);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}

	private void checkPrevPeriodApprove(TaxReportVO reportvo) throws DZFWarpException {
		// 检查是否有上期报表，是否审核
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_corp());
		params.addParam(reportvo.getSb_zlbh());
		params.addParam(reportvo.getPeriodfrom());
		params.addParam(TaxRptConst.IBILLSTATUS_UNAPPROVE);
		TaxReportVO[] rptvos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class,
				"nvl(dr,0)=0 and pk_corp=? and sb_zlbh=? and periodfrom <? and vbillstatus=?", params);
		if (rptvos != null && rptvos.length > 0) {
			throw new BusinessException("上期间报表未审核，请先审核上期间报表");
		}
	}

	/**
	 * 审核
	 */
	@Override
	public void processApprove(String pk_taxreport, UserVO userVO, String logindate, String ts)
			throws DZFWarpException {

		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);

		// 检查时间戳
		// checkTS(reportvo, ts);
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}

		if (reportvo.getVbillstatus() != TaxRptConst.IBILLSTATUS_UNAPPROVE) // 8
																			// 未审核状态
		{
			throw new BusinessException("报表状态不是待审核状态，不能审核!");
		}
		// 表间数据检查
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_taxreport());
		TaxReportDetailVO[] reportdetailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=?", params);
		if (StringUtil.isEmpty(reportdetailvos[0].getSpreadfile())) {
			throw new BusinessException("未填写的报表不能审核!");
		}
		String strJson = TaxRptemptools.readFileString(reportdetailvos[0].getSpreadfile());

		Map mapJson = readJsonValue(strJson, LinkedHashMap.class);
		
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		// 检查表间数据
		onCheckReportData(mapJson, reportvo, corptaxvo);

		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());

//		if (isBeijingCorp(corpvo)) {
//
//		} else {
//			// 检查是否有上期报表，是否已审核
//			checkPrevPeriodApprove(reportvo);
//
//			// 如果不是江苏省客户，审核后同时生成pdf
//			// 汇算清缴不生成pdf
//			if (isJiangsuCorp(corpvo) == false && !TaxRptConst.SB_ZLBH_SETTLEMENT.equals(reportvo.getSb_zlbh())) {
//				params = new SQLParameter();
//				params.addParam(reportvo.getPk_taxreport());
//				TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//						"pk_taxreport=?", params);
//				createPdfFile(detailvos, reportvo, corpvo);
//				getSbo().updateAry(detailvos);
//			}
//		}
//		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		ITaxRptService rpt =rptbillfactory.produce(corptaxvo);
		rpt.checkBeforeProcessApprove(reportvo, sbo, corpvo);
		
		reportvo.setVbillstatus(1);
		reportvo.setVapproveid(userVO.getCuserid());
		reportvo.setDapprovedate(new DZFDate(logindate).toString());
		reportvo.setTs(new DZFDateTime());
		getSbo().update(reportvo);

	}

	/**
	 * 读取报表审核检查条件数组
	 * 
	 * @param pk_taxreport
	 * @param userVO
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO) throws DZFWarpException {
		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
//		List<String> listCondition = new ArrayList<String>();
//		if (isBeijingCorp(corpvo)) {
//			if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
//				String[] sacondition = TaxRptChk10101_beijing.saCheckCondition;
//				// 读取报表内容
//				SQLParameter params = new SQLParameter();
//				params.addParam(reportvo.getPk_taxreport());
//				TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//						"pk_taxreport=?", params);
//				HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
//				for (TaxReportDetailVO detailvo : vos) {
//					hmDetail.put(detailvo.getReportname().trim(), detailvo);
//				}
//
//				// 排除公式中含有没有显示报表的公式
//
//				lab1: for (String condition : sacondition) {
//					String[] saReportname = getReportNameFromCondition(condition);
//					for (String reportname : saReportname) {
//						if (hmDetail.containsKey(reportname.trim()) == false) {
//							continue lab1;
//						}
//					}
//					listCondition.add(condition);
//				}
//			} else if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())) {
//				String[] sacondition = TaxRptChk10102_beijing.saCheckCondition;
//				// 读取报表内容
//				SQLParameter params = new SQLParameter();
//				params.addParam(reportvo.getPk_taxreport());
//				TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//						"pk_taxreport=?", params);
//				HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
//				for (TaxReportDetailVO detailvo : vos) {
//					hmDetail.put(detailvo.getReportname().trim(), detailvo);
//				}
//
//				// 排除公式中含有没有显示报表的公式
//
//				lab1: for (String condition : sacondition) {
//					String[] saReportname = getReportNameFromCondition(condition);
//					for (String reportname : saReportname) {
//						if (hmDetail.containsKey(reportname) == false) {
//							continue lab1;
//						}
//					}
//					listCondition.add(condition);
//				}
//			}
//		} else {
//			if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
//				String[] sacondition = TaxRptChk10101.saCheckCondition;
//				// 读取报表内容
//				SQLParameter params = new SQLParameter();
//				params.addParam(reportvo.getPk_taxreport());
//				TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//						"pk_taxreport=?", params);
//				HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
//				for (TaxReportDetailVO detailvo : vos) {
//					hmDetail.put(detailvo.getReportname().trim(), detailvo);
//				}
//
//				// 排除公式中含有没有显示报表的公式
//
//				lab1: for (String condition : sacondition) {
//					String[] saReportname = getReportNameFromCondition(condition);
//					for (String reportname : saReportname) {
//						if (hmDetail.containsKey(reportname.trim()) == false) {
//							continue lab1;
//						}
//					}
//					listCondition.add(condition);
//				}
//			} else if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())) {
//				String[] sacondition = TaxRptChk10102.saCheckCondition;
//				// 读取报表内容
//				SQLParameter params = new SQLParameter();
//				params.addParam(reportvo.getPk_taxreport());
//				TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
//						"pk_taxreport=?", params);
//				HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
//				for (TaxReportDetailVO detailvo : vos) {
//					hmDetail.put(detailvo.getReportname().trim(), detailvo);
//				}
//
//				// 排除公式中含有没有显示报表的公式
//
//				lab1: for (String condition : sacondition) {
//					String[] saReportname = getReportNameFromCondition(condition);
//					for (String reportname : saReportname) {
//						if (hmDetail.containsKey(reportname) == false) {
//							continue lab1;
//						}
//					}
//					listCondition.add(condition);
//				}
//			}
//		}
		ITaxRptService rpt =rptbillfactory.produce(corptaxvo);
		return rpt.getCondition(pk_taxreport, userVO, reportvo, getSbo());
	}

	private String[] getReportNameFromCondition(String condition) {
		List<String> listreportname = new ArrayList<String>();

		String regex = "([^!\\(:=><\\+\\-\\*/]*?)\\!";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(condition);

		while (m.find()) {

			String sname = m.group(1).trim();
			if (listreportname.contains(sname) == false) {
				listreportname.add(sname);
			}
		}

		// int iIndex = condition.indexOf("!");
		// while (iIndex > 0)
		// {
		// String strTemp = condition.substring(0, iIndex);
		//
		// int iFrom = strTemp.indexOf("(");
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf(":");
		// }
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.lastIndexOf("=");
		// }
		//
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf(">");
		// }
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf("<");
		// }
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf("+");
		// }
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf("-");
		// }
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf("*");
		// }
		// if (iFrom < 0)
		// {
		// iFrom = strTemp.indexOf("/");
		// }
		//
		//
		// if (iFrom < 0)
		// {
		// iFrom = 0;
		// }
		// else
		// {
		// iFrom += 1;
		// }
		// String tmpReportName = strTemp.substring(iFrom).trim();
		// if (listreportname.contains(tmpReportName) == false)
		// {
		// listreportname.add(tmpReportName);
		// }
		// condition = condition.substring(iIndex + 1);
		// iIndex = condition.indexOf("!");
		// }
		return listreportname.toArray(new String[0]);
	}

	/**
	 * 反审核
	 */
	@Override
	public void processUnApprove(String pk_taxreport, UserVO userVO, String ts) throws DZFWarpException {

		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		// 检查时间戳
		// checkTS(reportvo, ts);
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}

		if (reportvo.getVbillstatus() == TaxRptConst.IBILLSTATUS_UNAPPROVE) // 未审核状态
		{
			throw new BusinessException("报表状态是未审核状态，不能反审核!");
		}
		int isbzt_dm = Integer.parseInt(reportvo.getSbzt_dm());
		if (!(isbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit
				|| isbzt_dm == TaxRptConst.iSBZT_DM_AcceptFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel)) {
			throw new BusinessException("报表的申报状态是" + TaxRptConst.getSBzt_mc(isbzt_dm) + ", 不能反审核!");
		}
		// 检查后期间是否有已审核报表
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_corp());
		params.addParam(reportvo.getSb_zlbh());
		params.addParam(reportvo.getPeriodfrom());

		TaxReportVO[] rptvos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class,
				"nvl(dr,0)=0 and pk_corp=? and sb_zlbh=? and periodfrom >? and vbillstatus<>"
						+ TaxRptConst.IBILLSTATUS_UNAPPROVE,
				params);
		if (rptvos != null && rptvos.length > 0) {
			throw new BusinessException("后续期间报表已审核或已确认申报，不能弃审本期间报表");
		}
		reportvo.setVbillstatus(TaxRptConst.IBILLSTATUS_UNAPPROVE); // 8：未审核
		reportvo.setVapproveid(null);
		reportvo.setDapprovedate(null);
		reportvo.setVapprovenote(null);
		reportvo.setTs(new DZFDateTime());
		// 不更新申报状态
//		reportvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_UnSubmit);

		getSbo().update(reportvo);

		params = new SQLParameter();
		params.addParam(reportvo.getPk_taxreport());
		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=?", params);
		for (TaxReportDetailVO detailvo : vos) {
			detailvo.setSb_lsh(null);
			detailvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_UnSubmit);
			if (StringUtil.isEmpty(detailvo.getPdffile()) == false) {
				File file = new File(detailvo.getPdffile());
				if (file.exists() && file.isFile()) {
					file.delete();
				}
				detailvo.setPdffile(null);
			}
		}
		getSbo().updateAry(vos);

	}

	/**
	 * 报表数据检查
	 * 
	 * @param mapJson
	 *            mapJson 报表的spreaadJS转成的json数据
	 * @param reportvo
	 * @throws DZFWarpException
	 */
	private String onCheckReportData(Map mapJson, TaxReportVO reportvo, CorpTaxVo corptaxvo) throws DZFWarpException {

		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_taxreport());

		CorpVO corpvo = getCorpVO(reportvo.getCoperatorid(), reportvo.getPk_corp());

		TaxReportDetailVO[] rptdetailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=?", params);
		// 把报表明细放入hashmap
		HashMap<String, TaxReportDetailVO> hmRptDetail = new HashMap<String, TaxReportDetailVO>();
		for (TaxReportDetailVO detailvo : rptdetailvos) {
			hmRptDetail.put(detailvo.getReportname().trim(), detailvo);
		}
		String errmsg = "";

//		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10101)) {
//			if (isBeijingCorp(corpvo)) {
//				errmsg = checkForSB_ZLBH10101_bj(mapJson, corpvo, reportvo, hmRptDetail);
//			} else {
//				errmsg = checkForSB_ZLBH10101(mapJson, corpvo, reportvo, hmRptDetail);
//			}
//		}
		
		ITaxRptService rpt =rptbillfactory.produce(corptaxvo);
		errmsg =rpt.checkReportData(mapJson, corpvo, reportvo, hmRptDetail, getSbo());
		String warningMessage = rpt.checkReportDataWarning(mapJson, corpvo, reportvo, hmRptDetail, getSbo());
		if (errmsg.length() > 0)
			throw new BusinessException(errmsg);
		return warningMessage;
	}

	private DZFDouble getDzfDouble(Object obj) {
		if (obj == null || obj.toString().trim().length() == 0) {
			return DZFDouble.ZERO_DBL;
		} else {
			try {
				return new DZFDouble(obj.toString().replaceAll(",", ""));
			} catch (Exception e) {
				// 转数字失败，按零处理
				return DZFDouble.ZERO_DBL;
			}
		}
	}

	private String checkForSB_ZLBH10101(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail) throws DZFWarpException {
		String errmsg = "";
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);

		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		if (listReportName.contains(TaxRptConst.SRPTNAME10101001)) {
			// 本期应补(退)税额=glamtoccr("222109+2221009","@year","@period","y","cr","@corp")
			// G43+L43 42行6列和11列
			YntCpaccountVO[] accountVO  = accountService.queryByPk(corpvo.getPk_corp());
			DZFDouble v1 = getDzfDouble(spreadtool.getCellValue(mapJson, TaxRptConst.SRPTNAME10101001, 42, 6));
			DZFDouble v2 = getDzfDouble(spreadtool.getCellValue(mapJson, TaxRptConst.SRPTNAME10101001, 42, 11));

			DZFDouble v3 = getDzfDouble(spreadtool.getFormulaValue(null,
					"glamtoccr(\"222109+2221009\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\")",
					getQcData(corpvo, reportvo), corpvo, reportvo,accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);

			if (v1.add(v2).doubleValue() != 0) {
				DZFDouble d1 = v1.add(v2).setScale(2, DZFDouble.ROUND_HALF_UP);

				if (d1.equals(v3) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + TaxRptConst.SRPTNAME10101001
							+ ">  [本期应补(退)税额(G43+L43) 与记账凭证中计提增值税额数不符，请检查] ; <br>(数值：" + d1.toString() + " = "
							+ v3.toString() + ")";
				}
			}
		}
		return errmsg;
	}

	private String checkForSB_ZLBH10101_bj(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail) throws DZFWarpException {
		String errmsg = "";
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);

		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		String maintablename = "增值税纳税申报表（适用于增值税一般纳税人）";
		if (listReportName.contains(maintablename)) {
			// 本期应补(退)税额=glamtoccr("222109+2221009","@year","@period","y","cr","@corp")
			// G43+L43 42行6列和11列
			DZFDouble v1 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 44, 6));
			DZFDouble v2 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 44, 11));
			YntCpaccountVO[] accountVO  = accountService.queryByPk(corpvo.getPk_corp());
			DZFDouble v3 = getDzfDouble(spreadtool.getFormulaValue(null,
					"glamtoccr(\"222109+2221009\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\")",
					getQcData(corpvo, reportvo), corpvo, reportvo,accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);

			if (v1.add(v2).doubleValue() != 0) {
				DZFDouble d1 = v1.add(v2).setScale(2, DZFDouble.ROUND_HALF_UP);

				if (d1.equals(v3) == false) // 保留两位小数比较，凭证与界面两边都保留到分来计算
				{
					errmsg += (errmsg.length() > 0 ? "\r\n数据有如下错误:<" : "<") + maintablename
							+ ">  [本期应补(退)税额(G45+L45) 与记账凭证中计提增值税额数不符，请检查] ; <br>(数值：" + d1.toString() + " = "
							+ v3.toString() + ")";
				}
			}
			// 当主表24行1、3列之和大于0时，附表四第4列2、3、4、5行合计值只能小于等于主表24行1、3列合计数。请修改附表四第4列2、3、4、5行数据。
			// "增值税纳税申报表（适用于增值税一般纳税人）!G35 +增值税纳税申报表（适用于增值税一般纳税人）!L35 >=
			// SUM(增值税纳税申报表附列资料（四）!F8 : F11) and 增值税纳税申报表（适用于增值税一般纳税人）!G35
			// +增值税纳税申报表（适用于增值税一般纳税人）!L35 > 0"

			String fb4 = "增值税纳税申报表附列资料（四）";
			if (listReportName.contains(fb4)) {
				v1 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 34, 6)); // G35
				v2 = getDzfDouble(spreadtool.getCellValue(mapJson, maintablename, 39, 6)); // L35
				v3 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 7, 5)); // F8
				DZFDouble v4 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 8, 5)); // F9
				DZFDouble v5 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 9, 5)); // F10
				DZFDouble v6 = getDzfDouble(spreadtool.getCellValue(mapJson, fb4, 10, 5)); // F11
				if (v1.add(v2).compareTo(DZFDouble.ZERO_DBL) > 0) {
					if (v1.add(v2).compareTo(v3.add(v4).add(v5).add(v6)) < 0) {
						errmsg += (errmsg.length() > 0 ? "<br>" : "")
								+ "当主表24行1、3列之和大于0时，附表四第4列2、3、4、5行合计值只能小于等于主表24行1、3列合计数。请修改附表四第4列2、3、4、5行数据。<br>(数值: "
								+ v3.add(v4).add(v5).add(v6).setScale(2, DZFDouble.ROUND_HALF_UP).toString() + " <= "
								+ v1.add(v2).setScale(2, DZFDouble.ROUND_HALF_UP).toString() + ")";
					}
				}
			}
		}
		return errmsg;
	}

	/**
	 * 
	 * @param pk_taxrpttemplet
	 * @return Map<String, TaxRptTempletPosVO> key:itemkey (属性名称)
	 */
	private Map<String, TaxRptTempletPosVO> getHMRptTepmletPos(String pk_taxrpttemplet) {
		Map<String, TaxRptTempletPosVO> hmReturn = new HashMap<String, TaxRptTempletPosVO>();
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxrpttemplet);
		TaxRptTempletPosVO[] vos = (TaxRptTempletPosVO[]) getSbo().queryByCondition(TaxRptTempletPosVO.class,
				"nvl(dr,0)=0 and pk_taxrpttemplet=?", params);
		for (TaxRptTempletPosVO vo : vos) {
			hmReturn.put(vo.getItemkey().trim(), vo);
		}
		return hmReturn;
	}

	/**
	 * 
	 * @param filepathname
	 *            文件存放未知
	 * @return map key : filename, value: String key: bytedata value: byte[] ,
	 *         key : filesize value :Integer
	 */
	public Map getPdfFile(String filepathname) throws DZFWarpException {
		Map<String, Object> retMap = new HashMap<String, Object>();
		byte[] bytes = TaxRptemptools.readFileBytes(filepathname);
		retMap.put("bytedata", bytes);
		retMap.put("filesize", bytes.length);

		String filename = filepathname;
		if (filepathname.indexOf("/") >= 0) {
			filename = filepathname.substring(filepathname.lastIndexOf("/") + 1);
		}
		retMap.put("filename", filename);
		return retMap;
	}

	/**
	 * 申报作废
	 * 
	 * @param pk_taxreport
	 * @param userVO
	 * @param ts
	 *            时间戳
	 * @throws DZFWarpException
	 */
	@Override
	public void processDeclareCancel(String pk_taxreport, UserVO userVO, String ts) throws DZFWarpException {
		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);

		// 检查时间戳
		checkTS(reportvo, ts);

		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());

		if (checkAuthority(userVO, corpvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
//		if (isJiangsuCorp(corpvo) == false) {
//			throw new BusinessException("您所属地区申报作废功能正在开发中, 目前尚不能使用！");
//		}
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		ITaxRptService taxRptService = rptbillfactory.produce(corptaxvo);
		taxRptService.processObsoleteDeclare(corpvo, reportvo);
		
		/*SQLParameter params = new SQLParameter();
		params.addParam(pk_taxreport);
		TaxReportDetailVO[] reportdetailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=?", params);
		// 申报流水号
		List<String> listlsh = new ArrayList<String>();
		List<String> listerrmsg = new ArrayList<String>();
		for (TaxReportDetailVO detailvo : reportdetailvos) {
			int iSbzt_dm = Integer.parseInt(detailvo.getSbzt_dm());
			if (iSbzt_dm == TaxRptConst.iSBZT_DM_PaySuccess) {
				if (listerrmsg.contains("已缴款成功的申报不能作废!") == false) {
					listerrmsg.add("已缴款成功的申报不能作废!");
				}
			}
			if (iSbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel) {
				if (listerrmsg.contains("已作废的申报不能再次作废!") == false) {
					listerrmsg.add("已作废的申报不能再次作废!");
				}
			}
			if (iSbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit) {
				if (listerrmsg.contains("未提交的申报不能作废!") == false) {
					listerrmsg.add("未提交的申报不能作废!");
				}
			}
			if (iSbzt_dm != TaxRptConst.iSBZT_DM_PaySuccess && iSbzt_dm != TaxRptConst.iSBZT_DM_ReportCancel
					&& iSbzt_dm != TaxRptConst.iSBZT_DM_UnSubmit) {
				if (StringUtil.isEmpty(detailvo.getSb_lsh())) {
					throw new BusinessException("没有流水号，请先刷新申报状态！");
				}
				if (listlsh.contains(detailvo.getSb_lsh()) == false) {
					listlsh.add(detailvo.getSb_lsh());
				}
			}
			// 此行代码仅供测试
			// detailvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_ReportCancel);
		}
		if (listlsh.size() == 0) {
			if (listerrmsg.size() > 0) {
				String serrmsg = "";
				for (String msg : listerrmsg) {
					serrmsg += msg + "\n";
				}
				throw new BusinessException(serrmsg);
			}
		} else {
			for (String lsh : listlsh) {
				// 调用申报作废接口
				getTaxrequestsrv().cancelRpt(getToken(corpvo.getPk_corp()), reportvo, lsh);
			}
			// 此行代码仅供测试
			// getSbo().updateAry(reportdetailvos);
			// reportvo.setSbzt_dm("" + TaxRptConst.iSBZT_DM_ReportCancel);
			// getSbo().update(reportvo);
		}*/
	}

	/**
	 * 删除
	 * 
	 * @param pk_taxreport
	 * @param loginDate
	 * @Param userVO
	 * @param ts
	 *            时间戳
	 * @throws DZFWarpException
	 */
	@Override
	public void processDelete(String pk_taxreport, String corpid,String loginDate, UserVO userVO, String ts) throws DZFWarpException {
		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		// 检查时间戳
		// checkTS(reportvo, ts);
		String pk_corp = reportvo.getPk_corp();
		if (checkAuthority(userVO, pk_corp) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		if(!corpid.equals(reportvo.getPk_corp())){
			throw new BusinessException("对不起，操作非选择公司数据，请重试！");
		}

//		if (reportvo.getVbillstatus() != TaxRptConst.IBILLSTATUS_UNAPPROVE) // 未审核
//		{
//			throw new BusinessException("不是待审核状态的报表不能删除!");
//		}
		int isbzt_dm = Integer.parseInt(reportvo.getSbzt_dm());
		if (!(isbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit
				|| isbzt_dm == TaxRptConst.iSBZT_DM_AcceptFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportFailute
				|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel)) {
			throw new BusinessException("报表的申报状态是" + TaxRptConst.getSBzt_mc(isbzt_dm) + ", 不能删除!");
		}
		// 查询子表
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxreport);
		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=?", params);
		for (TaxReportDetailVO detailvo : vos) {
			if (StringUtil.isEmpty(detailvo.getPdffile()) == false) {
				File f = new File(detailvo.getPdffile());
				if (f.exists()) {
					f.delete();
				}
			}
			if (StringUtil.isEmpty(detailvo.getSpreadfile()) == false) {
				File f = new File(detailvo.getSpreadfile());
				if (f.exists()) {
					f.delete();
				}
			}
		}
		getSbo().deleteVOArray(vos);
		getSbo().deleteObject(reportvo);
		// 删除select * from ynt_taxtypelist
		
		////////////////-------------------zpm 删除
		
		///////////////---------------------
//		params = new SQLParameter();
//		params.addParam(pk_corp);
//		params.addParam(loginDate.substring(0, 7));
//		TaxTypeListVO[] typelistvos = (TaxTypeListVO[]) getSbo().queryByCondition(TaxTypeListVO.class,
//				"nvl(dr,0) = 0 and pk_corp=? and yearmonth=?", params);
//		if (typelistvos != null && typelistvos.length > 0) {
//			params = new SQLParameter();
//			params.addParam(typelistvos[0].getPk_taxtypelist());
//			TaxTypeListDetailVO[] detailvos = (TaxTypeListDetailVO[]) getSbo()
//					.queryByCondition(TaxTypeListDetailVO.class, "nvl(dr,0)= 0 and pk_taxtypelist=?", params);
//			if (detailvos != null && detailvos.length > 0) {
//				getSbo().deleteVOArray(detailvos);
//			}
//			getSbo().deleteVOArray(typelistvos);
//		}
	}

	/**
	 * 重算
	 * 
	 * @param jsonString
	 * @param pk_taxreport
	 * @param userVO
	 * @param reportname
	 * @param isCalAll
	 * @param ts
	 *            时间戳
	 * @throws DZFWarpException
	 */
	public String onRecal(String jsonString, String pk_taxreport, UserVO userVO, String corpid, String reportname,Boolean isCalAll,
			String ts) throws DZFWarpException {
		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		// 检查时间戳
//		checkTS(reportvo, ts);
//		if (reportvo.getVbillstatus() != TaxRptConst.IBILLSTATUS_UNAPPROVE) // 8
//																			// 未审核
//		{
//			throw new BusinessException("只有未审核状态的报表可执行重算功能！");
//		}
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		if(!corpid.equals(reportvo.getPk_corp())){
			throw new BusinessException("对不起，操作非选择公司数据，请重试！");
		}
		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());

		// ObjectMapper objectMapper = getObjectMapper();

		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);

		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxreport);
		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=?", params);

		Map objMapReport = readJsonValue(jsonString, LinkedHashMap.class);

		List<String> listRptName = spreadtool.getReportNameList(objMapReport);
		// 查询原始报表模板
		String pk_templet = vos[0].getPk_taxrpttemplet();
		TaxRptTempletVO templetvo = (TaxRptTempletVO) getSbo().queryByPrimaryKey(TaxRptTempletVO.class, pk_templet);
		// spread模板
		String strTempletJson = TaxRptemptools.readFileString(templetvo.getSpreadtemplet());
		Map objTempletMapReport = readJsonValue(strTempletJson, LinkedHashMap.class);

		// 公式缓存
		Map mapValue = new HashMap<String, Object>();

		HashMap<String, Object> hmQCData = getQcData(corpvo, reportvo);
		LinkedHashMap hmsheets = null;
		LinkedHashMap hmsheet = null;
		LinkedHashMap hmData = null;
		LinkedHashMap hmdataTable = null;
		YntCpaccountVO[] accountVO  = accountService.queryByPk(corpvo.getPk_corp());
		for (TaxReportDetailVO detailvo : vos) {
			if (isCalAll == false && detailvo.getReportname().equals(reportname) == false) {
				hmsheets = (LinkedHashMap) objMapReport.get("sheets");
				hmsheet = (LinkedHashMap) hmsheets.get(detailvo.getReportname());
				hmData = (LinkedHashMap) hmsheet.get("data");
				hmdataTable = (LinkedHashMap) hmData.get("dataTable");
				for (Object rowkey : hmdataTable.keySet()) {
					int iRow = Integer.parseInt(rowkey.toString());
					LinkedHashMap hmRow = (LinkedHashMap) hmdataTable.get(rowkey);
					for (Object columnkey : hmRow.keySet()) {
						int iColumn = Integer.parseInt(columnkey.toString());
						LinkedHashMap cell = (LinkedHashMap) hmRow.get(columnkey);
						if (cell.containsKey("formula")) {

							Object objFormula = cell.get("formula");

							if (objFormula == null || objFormula.toString().trim().length() == 0)
								continue;

							// String strValue =
							// spreadtool.getFormulaValue(mapValue, objFormula,
							// hmQCData, corpvo, reportvo);
							objFormula = spreadtool.getFormularBetweenTable(listRptName, (String) objFormula);

							// 对填报报表重新赋值公式
							cell.put("formula", objFormula);
							// spreadtool.setCellFormula(objMapReport,
							// detailvo.getReportname(), iRow, iColumn,
							// objFormula);
						}
					}
				}
			} else {
				hmsheets = (LinkedHashMap) objTempletMapReport.get("sheets");
				hmsheet = (LinkedHashMap) hmsheets.get(detailvo.getReportname());
				hmData = (LinkedHashMap) hmsheet.get("data");
				hmdataTable = (LinkedHashMap) hmData.get("dataTable");
				for (Object rowkey : hmdataTable.keySet()) {
					int iRow = Integer.parseInt(rowkey.toString());
					LinkedHashMap hmRow = (LinkedHashMap) hmdataTable.get(rowkey);
					for (Object columnkey : hmRow.keySet()) {
						int iColumn = Integer.parseInt(columnkey.toString());
						LinkedHashMap cell = (LinkedHashMap) hmRow.get(columnkey);
						if (cell.containsKey("formula")) {

							Object objFormula = cell.get("formula");

							if (objFormula == null || objFormula.toString().trim().length() == 0)
								continue;

							String strValue = spreadtool.getFormulaValue(mapValue, objFormula, hmQCData, corpvo,
									reportvo,accountVO);
							strValue = spreadtool.getFormularBetweenTable(listRptName, strValue);

							// 对填报报表重新赋值公式
							cell.put("formula", strValue);
							spreadtool.setCellFormula(objMapReport, detailvo.getReportname(), iRow, iColumn, strValue);
							spreadtool.setCellValue(objMapReport, detailvo.getReportname(), iRow, iColumn, null);
						}
					}
				}
			}

		}

		// 设置索引
		spreadtool.setactiveSheetIndex(objMapReport, reportname);

		String str = writeValueAsString(objMapReport);
		// saveFile("d:/abc.ssjson", str);
		return str;

	}

	private String writeValueAsString(Object obj) throws DZFWarpException {
		try {
			return getObjectMapper().writeValueAsString(obj);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}

	private void checkTS(TaxReportVO reportvo, String ts) throws DZFWarpException {
		// 下面代码不能放开注释！！
		// if (reportvo.getTs().toString().equals(ts) == false)
		// {
		// throw new BusinessException("数据已经发生改变，请刷新界面后重试!");
		// }
	}

	private HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException {
		// 期初数据，待查询
		HashMap hmQCData = new HashMap();
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
		ITaxRptService rpt =rptbillfactory.produce(corptaxvo);
		hmQCData =rpt.getQcData(corpvo, reportvo, getSbo());

//		if (isJiangsuCorp(corpvo)) {
//			String nsrsbh = corpvo.getTaxcode();
//			String periodfrom = reportvo.getPeriodfrom();
//			String periodto = reportvo.getPeriodto();
//
//			String filename = TaxReportPath.taxReportPath + "qcdata/01_" + periodfrom + "_" + periodto + "_"
//					+ (nsrsbh == null ? "empty" : nsrsbh.trim()) + ".qcdata";
//			File f = new File(filename);
//			if (f.exists()) {
//				String initdata = readFileString(filename);
//
//				LinkedHashMap<String, Object> mapQcdata = readJsonValue(initdata.toLowerCase(), LinkedHashMap.class);
//				for (String key : mapQcdata.keySet()) {
//					String newKey = key;
//					Object objValue = mapQcdata.get(key);
//					if (key.indexOf(":") >= 0) {
//						newKey = newKey.substring(newKey.lastIndexOf(":") + 1).trim();
//					}
//					if (key.lastIndexOf(".") >= 0) {
//						key = key.substring(key.lastIndexOf(".") + 1).trim();
//					}
//					hmQCData.put(newKey.toLowerCase(), objValue);
//				}
//			}
//		} else if (isBeijingCorp(corpvo)) {
//			// 北京暂时不处理期初
//			// 没有查到可取数的报表
//			hmQCData = new HashMap();
//
//		} else {
//			// 从旧报表中取期初
//			String filename = getPrevPeriodSpreadFileName(reportvo);
//			File file = new File(filename);
//			if (file.exists() == false) {
//				throw new BusinessException("报表文件丢失!");
//			}
//			Map objMapReport = readJsonValue(readFileString(filename), LinkedHashMap.class);
//
//			SQLParameter params = new SQLParameter();
//			params.addParam(reportvo.getSb_zlbh());
//			TaxRptTempletVO[] templetvos = (TaxRptTempletVO[]) getSbo().queryByCondition(TaxRptTempletVO.class,
//					"nvl(dr,0)=0 and rtrim(location)='通用' and sb_zlbh=?", params);
//			// 模板放入hashmap
//			HashMap<String, TaxRptTempletVO> hmTemplet = new HashMap<String, TaxRptTempletVO>();
//
//			String templetpks = "";
//			for (TaxRptTempletVO vo : templetvos) {
//				hmTemplet.put(vo.getPk_taxrpttemplet(), vo);
//				templetpks += (templetpks.length() == 0 ? "'" : ",'") + vo.getPrimaryKey() + "'";
//			}
//			TaxRptTempletPosVO[] posvos = (TaxRptTempletPosVO[]) getSbo().queryByCondition(TaxRptTempletPosVO.class,
//					"nvl(dr,0)=0 and pk_taxrpttemplet in (" + templetpks + ") and itemkeyinitname is not null", null);
//
//			SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
//
//			for (TaxRptTempletPosVO posvo : posvos) {
//				if (StringUtil.isEmpty(posvo.getItemkeyinitname())) {
//					continue;
//				}
//				// 从模板取值，赋值到期初hashmap
//				Object oValue = spreadtool.getCellValue(objMapReport,
//						hmTemplet.get(posvo.getPk_taxrpttemplet()).getReportname(), posvo);
//				if (oValue != null) {
//					hmQCData.put(posvo.getItemkeyinitname().toLowerCase().trim(), oValue);
//				}
//			}
//
//			// 如果是年初，期初都是零。 必须放在末尾，不能放前面，上面需要先检查上张报表审核状态
//			if (reportvo.getPeriodfrom().endsWith("-01-01")) {
//				hmQCData = new HashMap();
//			}
//
//		}
		return hmQCData;
	}

	/**
	 * 计算上一个报表名称
	 * 
	 * @param reportvo
	 * @return
	 */
	private String getPrevPeriodSpreadFileName(TaxReportVO reportvo) throws DZFWarpException {
		String filename = null;

		DZFDate periodFrom = new DZFDate(reportvo.getPeriodfrom());
		DZFDate newPeriodFrom = null;
		DZFDate newPeriodTo = null;
		Integer iPeriodType = reportvo.getPeriodtype();
		if (iPeriodType == 0) {
			// 月报
			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(1).toString().substring(0, 7));
			newPeriodTo = periodFrom.getDateBefore(1);
		} else if (iPeriodType == 1) {
			// 季报
			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(63).toString().substring(0, 7));
			newPeriodTo = periodFrom.getDateBefore(1);
		} else if (iPeriodType == 2) {
			// 年报
			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(360).toString().substring(0, 7));
			newPeriodTo = periodFrom.getDateBefore(1);
		} else {
			throw new BusinessException("期间类型错误");
		}
		// 查询是否有上一张报表
		String condition = "nvl(dr,0)=0 and pk_corp=? and zsxm_dm=? and sb_zlbh=? and periodtype=? and periodfrom=? and periodto=?";
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_corp());
		params.addParam(reportvo.getZsxm_dm());
		params.addParam(reportvo.getSb_zlbh());
		params.addParam(iPeriodType);
		params.addParam(newPeriodFrom.toString());
		params.addParam(newPeriodTo.toString());

		TaxReportVO[] prevReportvos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class, condition, params);
		if (prevReportvos != null && prevReportvos.length > 0) {

			// 有上一期间的报表
			filename = TaxReportPath.taxReportPath + "spreadfile/spread" + prevReportvos[0].getPk_taxreport()
					+ prevReportvos[0].getSb_zlbh() + ".ssjson";
			// 检查报表是否填写
			params = new SQLParameter();
			params.addParam(prevReportvos[0].getPk_taxreport());
			TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
					"nvl(dr,0)=0 and pk_taxreport=?", params);
			if (StringUtil.isEmpty(detailvos[0].getSpreadfile())) {
				throw new BusinessException("上期报表已创建但未填写，无法读取期初数据!");
			}
			if (prevReportvos[0].getVbillstatus() == TaxRptConst.IBILLSTATUS_UNAPPROVE) {
				throw new BusinessException("上期间报表未审核，请先审核上期间报表");
			}
		} else {
			// 判断前期是否录入过报表
			condition = "nvl(dr,0)=0 and pk_corp=? and zsxm_dm=? and sb_zlbh=? and periodtype=? and periodfrom<? order by periodfrom desc";
			params = new SQLParameter();
			params.addParam(reportvo.getPk_corp());
			params.addParam(reportvo.getZsxm_dm());
			params.addParam(reportvo.getSb_zlbh());
			params.addParam(iPeriodType);
			params.addParam(newPeriodFrom.toString());
			prevReportvos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class, condition, params);
			if (prevReportvos != null && prevReportvos.length > 0) {
				throw new BusinessException("您录入的报表期间与上一个期间 " + prevReportvos[0].getPeriodfrom() + " 至 "
						+ prevReportvos[0].getPeriodto() + " 不连续");
			}
			// 查询期初录入
			condition = "nvl(dr,0)=0 and pk_corp=? and sb_zlbh=? ";
			params = new SQLParameter();
			params.addParam(reportvo.getPk_corp());
			params.addParam(reportvo.getSb_zlbh());

			TaxReportInitVO[] initvos = (TaxReportInitVO[]) getSbo().queryByCondition(TaxReportInitVO.class, condition,
					params);
			if (initvos != null && initvos.length > 0) {
				// 判断期初的所属期间是否是当前录入报表起始期间
				if (periodFrom.toString().substring(0, 7).equals((initvos[0].getPeriod())) == false
						&& initvos[0].getPeriod()
								.equals(corpService.queryByPk(initvos[0].getPk_corp()).getBegindate()
										.toString().substring(0, 7)) == false) {
					throw new BusinessException("期初数据所属期间是: " + initvos[0].getPeriod() + ", 与您填报起始期间不一致!");
				}

				filename = initvos[0].getSpreadfile();
			} else {
				throw new BusinessException("您需要先录入纳税报表期初数据!");
			}

		}
		return filename;
	}

	/**
	 * 北京地区 计算上一个报表名称
	 * 
	 * @param reportvo
	 * @return
	 */
	private String getPrevPeriodSpreadFileName_2(TaxReportVO reportvo) throws DZFWarpException {
		String filename = null;

		DZFDate periodFrom = new DZFDate(reportvo.getPeriodfrom());
		DZFDate newPeriodFrom = null;
		DZFDate newPeriodTo = null;
		Integer iPeriodType = reportvo.getPeriodtype();
		if (iPeriodType == 0) {
			// 月报
			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(1).toString().substring(0, 7));
			newPeriodTo = periodFrom.getDateBefore(1);
		} else if (iPeriodType == 1) {
			// 季报
			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(63).toString().substring(0, 7));
			newPeriodTo = periodFrom.getDateBefore(1);
		} else if (iPeriodType == 2) {
			// 年报
			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(360).toString().substring(0, 7));
			newPeriodTo = periodFrom.getDateBefore(1);
		} else {
			throw new BusinessException("期间类型错误");
		}
		// 查询是否有上一张报表
		String condition = "nvl(dr,0)=0 and pk_corp=? and zsxm_dm=? and sb_zlbh=? and periodtype=? and periodfrom=? and periodto=?";
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_corp());
		params.addParam(reportvo.getZsxm_dm());
		params.addParam(reportvo.getSb_zlbh());
		params.addParam(iPeriodType);
		params.addParam(newPeriodFrom.toString());
		params.addParam(newPeriodTo.toString());

		TaxReportVO[] prevReportvos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class, condition, params);
		if (prevReportvos != null && prevReportvos.length > 0) {

			// 有上一期间的报表
			filename = TaxReportPath.taxReportPath + "spreadfile/spread" + prevReportvos[0].getPk_taxreport()
					+ prevReportvos[0].getSb_zlbh() + ".ssjson";
			// 检查报表是否填写
			params = new SQLParameter();
			params.addParam(prevReportvos[0].getPk_taxreport());
			TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
					"nvl(dr,0)=0 and pk_taxreport=?", params);
			if (StringUtil.isEmpty(detailvos[0].getSpreadfile())) {
				throw new BusinessException("上期报表已创建但未填写，无法读取期初数据!");
			}
			if (prevReportvos[0].getVbillstatus() == TaxRptConst.IBILLSTATUS_UNAPPROVE) {
				throw new BusinessException("上期间报表未审核，请先审核上期间报表");
			}
		} else {
			// 判断前期是否录入过报表
			condition = "nvl(dr,0)=0 and pk_corp=? and zsxm_dm=? and sb_zlbh=? and periodtype=? and periodfrom<? order by periodfrom desc";
			params = new SQLParameter();
			params.addParam(reportvo.getPk_corp());
			params.addParam(reportvo.getZsxm_dm());
			params.addParam(reportvo.getSb_zlbh());
			params.addParam(iPeriodType);
			params.addParam(newPeriodFrom.toString());
			prevReportvos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class, condition, params);
			if (prevReportvos != null && prevReportvos.length > 0) {
				// throw new BusinessException("您录入的报表期间与上一个期间 " +
				// prevReportvos[0].getPeriodfrom() + " 至 " +
				// prevReportvos[0].getPeriodto() + " 不连续");
			} else {
				// 查询期初录入
				condition = "nvl(dr,0)=0 and pk_corp=? and sb_zlbh=? ";
				params = new SQLParameter();
				params.addParam(reportvo.getPk_corp());
				params.addParam(reportvo.getSb_zlbh());

				TaxReportInitVO[] initvos = (TaxReportInitVO[]) getSbo().queryByCondition(TaxReportInitVO.class,
						condition, params);
				if (initvos != null && initvos.length > 0) {
					// 判断期初的所属期间是否是当前录入报表起始期间
					if (periodFrom.toString().substring(0, 7).equals((initvos[0].getPeriod())) == false
							&& initvos[0].getPeriod()
									.equals(corpService.queryByPk(initvos[0].getPk_corp()).getBegindate()
											.toString().substring(0, 7)) == false) {
						// throw new BusinessException("期初数据所属期间是: " +
						// initvos[0].getPeriod() + ", 与您填报起始期间不一致!");
					} else {
						filename = initvos[0].getSpreadfile();
					}
				} else {
					// throw new BusinessException("您需要先录入纳税报表期初数据!");
				}
			}

		}
		return filename;
	}

	/**
	 * 获取报表Spread手机发送信息字符串
	 * 
	 * @param pk_taxreport
	 * @param userVO
	 * 
	 * @return
	 * @throws Exception
	 */
	@Override
	public List<DZFDouble> getSpreadSendData(String pk_taxreport, UserVO userVO) throws DZFWarpException {

		List<DZFDouble> listReturn = new ArrayList<DZFDouble>();

		TaxReportVO reportvo = (TaxReportVO) getSbo().queryByPrimaryKey(TaxReportVO.class, pk_taxreport);

//		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), reportvo.getPk_corp());// CorpCache.getInstance().get(userVO.getPrimaryKey(),
																					// typelistvo.getPk_corp());
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(reportvo.getPk_corp());
		String reportCode = "";
		String sb_zlbh = reportvo.getSb_zlbh();
		if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh)) {
			reportCode = "10101001";
		} else if (TaxRptConst.SB_ZLBH10102.equals(sb_zlbh)) {
			reportCode = "10102001";
		} else if (TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)) // 企业所得税
		{
			reportCode = "10412001";
		} else if (TaxRptConst.SB_ZLBH50101.equals(sb_zlbh)) // 代征地税-一般纳税人
		{
			reportCode = "50101001";
		} else if (TaxRptConst.SB_ZLBH50102.equals(sb_zlbh)) // 代征地税-小规模纳税人
		{
			reportCode = "50102001";
		} else {
			throw new BusinessException("不支持" + sb_zlbh + "申报种类的信息");
		}
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxreport);
		params.addParam(reportCode);

		TaxReportDetailVO[] vos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,
				"nvl(dr,0)=0 and pk_taxreport=? and reportcode=? ", params);

		// params = new SQLParameter();
		// params.addParam(sb_zlbh);
		// params.addParam(reportCode);
		// TaxRptTempletVO[] templetvo =
		// (TaxRptTempletVO[])getSbo().queryByCondition(TaxRptTempletVO.class,
		// "nvl(dr,0)=0 and sb_zlbh= ? and reportcode=? ", params);
		//

		if (vos[0].getSpreadfile() == null || vos[0].getSpreadfile().trim().length() == 0) {
			return null;
		} else {
			String reportName = vos[0].getReportname();

			File f = new File(vos[0].getSpreadfile());
			if (f.exists() && f.isFile()) {
				String strJson = TaxRptemptools.readFileString(vos[0].getSpreadfile());
				ObjectMapper objectMapper = getObjectMapper();
				Map objMap = (Map) readJsonValue(strJson, HashMap.class);
				SpreadTool tool = new SpreadTool(taxbalancesrv);

				Object objValue = null;
				DZFDouble dbSE = DZFDouble.ZERO_DBL;
				if (TaxRptConst.SB_ZLBH10101.equals(sb_zlbh)) {
					int iRow = 42;
					int iCloumn1 = 6;
					int iColumn2 = 11;
					if (isBeijingCorp(corptaxvo)) // 北京报表，行增加2
					{
						iRow = 44;
					}
					objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn1);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = dbSE.add(new DZFDouble(objValue.toString()));
					}
					objValue = tool.getCellValue(objMap, reportName, iRow, iColumn2);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = dbSE.add(new DZFDouble(objValue.toString()));
					}
					listReturn.add(dbSE);
				} else if (TaxRptConst.SB_ZLBH10102.equals(sb_zlbh)) {
					int iRow = 28;
					int iCloumn1 = 4;
					int iColumn2 = 5;
					if (isBeijingCorp(corptaxvo)) // 北京报表，行增加2
					{
						iRow = 26;
					}
					objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn1);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = dbSE.add(new DZFDouble(objValue.toString()));
					}
					objValue = tool.getCellValue(objMap, reportName, iRow, iColumn2);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = dbSE.add(new DZFDouble(objValue.toString()));
					}
					listReturn.add(dbSE);
				} else if (TaxRptConst.SB_ZLBH10412.equals(sb_zlbh)) // 企业所得税
				{

					if (isBeijingCorp(corptaxvo)) {
						int iRow = 6; // I7 营业收入
						int iCloumn = 8;
						objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn);
						if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
							dbSE = new DZFDouble(objValue.toString());
						}
						listReturn.add(dbSE);

						iRow = 7; // I8营业成本
						dbSE = DZFDouble.ZERO_DBL;
						objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn);
						if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
							dbSE = new DZFDouble(objValue.toString());
						}
						listReturn.add(dbSE);

						iRow = 8; // I9利润总额
						dbSE = DZFDouble.ZERO_DBL;
						objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn);
						if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
							dbSE = new DZFDouble(objValue.toString());
						}
						listReturn.add(dbSE);
					}
				} else if (TaxRptConst.SB_ZLBH50101.equals(sb_zlbh) || // 代征地税-一般纳税人
						TaxRptConst.SB_ZLBH50102.equals(sb_zlbh)) // 代征地税-小规模纳税人
				{
					int iRow = 11; // L12城建税
					int iCloumn = 11;
					objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = new DZFDouble(objValue.toString());
					}
					listReturn.add(dbSE);

					iRow = 13; // L14教育费附加
					dbSE = DZFDouble.ZERO_DBL;
					objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = new DZFDouble(objValue.toString());
					}
					listReturn.add(dbSE);

					iRow = 15; // L16地方教育附加
					dbSE = DZFDouble.ZERO_DBL;
					objValue = tool.getCellValue(objMap, reportName, iRow, iCloumn);
					if (objValue != null && StringUtil.isEmpty(objValue.toString()) == false) {
						dbSE = new DZFDouble(objValue.toString());
					}
					listReturn.add(dbSE);
				}

			}
		}
		return (listReturn.size() > 0 ? listReturn : null);
	}

	private boolean isBeijingCorp(CorpTaxVo corptaxvo) {
		return (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 2);
	}
	
	private boolean isshandongCorp(CorpTaxVo corptaxvo) {
		return (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 16);
	}

//	private boolean isJiangsuCorp(CorpTaxVo corptaxvo) {
//		return (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 11);
//	}
	
	private boolean isXiamenCorp(CorpTaxVo corptaxvo) {
		return (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 151);
	}
	
	private boolean isHubeiCorp(CorpTaxVo corptaxvo) {
		return (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 18);
	}

	private String getQuarterStartDate(String yearmonth) {
		int iYear = Integer.parseInt(yearmonth.substring(0, 4));
		int iMonth = Integer.parseInt(yearmonth.substring(5, 7));
		switch (iMonth) {
		case 1:
		case 2:
		case 3: {
			return "" + (iYear - 1) + "-10-01";
		}
		case 4:
		case 5:
		case 6: {
			return "" + iYear + "-01-01";
		}
		case 7:
		case 8:
		case 9: {
			return "" + iYear + "-04-01";
		}
		case 10:
		case 11:
		case 12: {
			return "" + iYear + "-07-01";
		}
		}
		return null;

	}

	private String getQuarterEndDate(String yearmonth) {
		int iYear = Integer.parseInt(yearmonth.substring(0, 4));
		int iMonth = Integer.parseInt(yearmonth.substring(5, 7));
		switch (iMonth) {
		case 1:
		case 2:
		case 3: {
			return "" + (iYear - 1) + "-12-31";
		}
		case 4:
		case 5:
		case 6: {
			return "" + iYear + "-03-31";
		}
		case 7:
		case 8:
		case 9: {
			return "" + iYear + "-06-30";
		}
		case 10:
		case 11:
		case 12: {
			return "" + iYear + "-09-30";
		}
		}
		return null;

	}

	/**
	 * 查询填报类型列表
	 * 
	 * @param pk_corp
	 *            待查询填报类型公司pk
	 * @param userVO
	 *            当前登录用户
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public List<TaxReportVO> initGetTypeList(String pk_corp, UserVO userVO,
			String period, String operatorid, String operatedate)
			throws DZFWarpException {
		if (checkAuthority(userVO, pk_corp) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}

		CorpVO corpvo = getCorpVO(userVO.getPrimaryKey(), pk_corp);// CorpCache.getInstance().get(userVO.getPrimaryKey(),
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		ITaxRptService rpt = rptbillfactory.produce(corptaxvo);
		List<TaxReportVO> list = rpt.getTypeList(corpvo,corptaxvo, period, operatorid, operatedate, sbo);
		// 刷新列表时更新申报状态
		if(list == null || list.size() == 0)
			return null;
		for (TaxReportVO taxReportVO : list) {
			try {
				rpt.getDeclareStatus(corpvo,corptaxvo, taxReportVO);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			}
		}
		return list;
	}

	/**
	 * 获取期初数据报表
	 * 
	 * @param pk_corp
	 * @param sb_zlbh
	 * @param period
	 * @param userVO
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public TaxReportInitVO getInitSpreadJSData(String pk_corp, String sb_zlbh, String period, UserVO userVO)
			throws DZFWarpException {
		TaxReportInitVO initvo = null;

		if (checkAuthority(userVO, pk_corp) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		String strJson = null;
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(sb_zlbh);
		TaxReportInitVO[] initvos = (TaxReportInitVO[]) getSbo().queryByCondition(TaxReportInitVO.class,
				"nvl(dr,0)=0 and pk_corp=? and sb_zlbh=?", params);
		if (initvos != null && initvos.length > 0) {
			// String filename = TaxReportPath.taxReportPath + "qcdata/" +
			// pk_corp + "_" + sb_zlbh + ".ssjson";
			initvo = initvos[0];
			strJson = TaxRptemptools.readFileString(initvo.getSpreadfile());

		}

		else {
			// 没有期初表，从期初模板建立
			strJson = TaxRptemptools.readFileString(TaxReportPath.taxReportPath + "qctemplet/" + sb_zlbh + ".ssjson");

			initvo = new TaxReportInitVO();
			initvo.setPeriod(period);

		}
		// ObjectMapper objectMapper = getObjectMapper();
		Map objMap = readJsonValue(strJson, LinkedHashMap.class);

		Map objMapRet = new SpreadTool(taxbalancesrv).adjustEditCell(objMap);

		String sReturn = writeValueAsString(objMapRet);

		initvo.setSpreadfile(sReturn);

		return initvo;

	}

	/**
	 * 保存期初报表
	 * 
	 * @param taxreportinitvo
	 * @param jsonString
	 * @Param userVO return pk_taxreportinitvo
	 * @throws DZFWarpException
	 */
	@Override
	public String saveInitReport(TaxReportInitVO taxreportinitvo, String jsonString, UserVO userVO)
			throws DZFWarpException {
		String pk_corp = taxreportinitvo.getPk_corp();

		if (checkAuthority(userVO, pk_corp) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		String sb_zlbh = taxreportinitvo.getSb_zlbh();
		String pk_taxreportinitvo = taxreportinitvo.getPk_taxreportinit();
		String filename = TaxReportPath.taxReportPath + "qcdata/" + pk_corp + "_" + sb_zlbh + ".ssjson";
		taxreportinitvo.setSpreadfile(filename);

		// 检查保存期间前是否已录入报表
		String condition = "nvl(dr,0)= 0 and pk_corp=? and sb_zlbh=? and periodfrom<?";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(sb_zlbh);
		params.addParam(taxreportinitvo.getPeriod() + "-01");
		TaxReportVO[] vos = (TaxReportVO[]) getSbo().queryByCondition(TaxReportVO.class, condition, params);
		if (vos != null && vos.length > 0) {
			throw new BusinessException(
					"系统已经录入了开始日期为 " + vos[0].getPeriodfrom() + " 的报表，期初期间 " + taxreportinitvo.getPeriod() + " 不正确!");
		}
		if (StringUtil.isEmpty(pk_taxreportinitvo)) {
			// 新增保存
			String pk = IDGenerate.getInstance().getNextID(pk_corp);
			taxreportinitvo.setPrimaryKey(pk);

			getSbo().insertVOWithPK(taxreportinitvo);
		} else {
			// 修改保存
			taxreportinitvo.setTs(new DZFDateTime());
			getSbo().update(taxreportinitvo);
		}

		File file = new File(filename);
		if (file.exists() && file.isFile()) {
			file.delete();
		}
		saveFile(filename, jsonString);
		return taxreportinitvo.getPrimaryKey();
	}

	@Override
	public DZFDouble getQsbbqsData(String filePath, String reportName, String x, String y) throws DZFWarpException {
		DZFDouble dReturn = null;
		String strJson = TaxRptemptools.readFileString(filePath);
		ObjectMapper objectMapper = getObjectMapper();
		try {
			Map objMap = (Map) objectMapper.readValue(strJson, HashMap.class);
			dReturn = new SpreadTool(taxbalancesrv).getQsbbqsData(objMap, reportName, x, y);
		} catch (Exception e) {
			dReturn = new DZFDouble("0.0");//赋值为零
			log.error(e.getMessage(), e);
		}
		return dReturn;
	}

	@Override
	public Object processSendTaxReport(CorpVO corpVO, UserVO userVO,String corpid,String pk_taxreport) throws DZFWarpException {

		//zpm
		corpVO = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, corpid);
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corpid);
//		TaxReportDetailVO detailvo = (TaxReportDetailVO) sbo.queryByPrimaryKey(TaxReportDetailVO.class, pk_detail);

		TaxReportVO reportvo = (TaxReportVO) sbo.queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		//查询子表数据
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_taxreport);
		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) getSbo().queryByCondition(TaxReportDetailVO.class,"nvl(dr,0)=0 and pk_taxreport=?", sp);
		if(reportvo == null || detailvos == null || detailvos.length == 0)
			throw new BusinessException("当前报表数据信息不存在，请刷新后操作！");
		String spreadfile = reportvo.getSpreadfile();
		if(StringUtil.isEmpty(spreadfile)){
			spreadfile = detailvos[0].getSpreadfile();
		}
		int isbzt_dm = Integer.parseInt(reportvo.getSbzt_dm());
		if(isshandongCorp(corptaxvo)){//山东//山东的 申报状态为   	已提交 0，山东可以多次提交。
			if (!(isbzt_dm == TaxRptConst.iSBZT_DM_Submitted
					|| isbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit
					|| isbzt_dm == TaxRptConst.iSBZT_DM_AcceptFailute
					|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportFailute
					|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel)) {
				throw new BusinessException("报表的申报状态是" + TaxRptConst.getSBzt_mc(isbzt_dm) + ", 不能重复申报");
			}
		}else{
			if (!(isbzt_dm == TaxRptConst.iSBZT_DM_UnSubmit
					|| isbzt_dm == TaxRptConst.iSBZT_DM_AcceptFailute
					|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportFailute
					|| isbzt_dm == TaxRptConst.iSBZT_DM_ReportCancel)) {
				throw new BusinessException("报表的申报状态是" + TaxRptConst.getSBzt_mc(isbzt_dm) + ", 不能重复申报");
			}
		}
		
		if (StringUtil.isEmpty(spreadfile)) {
			throw new BusinessException("纳税申报信息未填写");
		}

		
		//查验权限
		if (checkAuthority(userVO, reportvo.getPk_corp()) == false) {
			throw new BusinessException("对不起，您无操作权限！");
		}
		if(!corpid.equals(reportvo.getPk_corp())){
			throw new BusinessException("对不起，操作非选择公司数据，请重试！");
		}
//		if (reportvo.getVbillstatus() == 8 || reportvo.getVbillstatus() == -1) {
//			throw new BusinessException("未审核报表不能上报");
//		}
		
		Map objMapReport = readJsonValue(TaxRptemptools.readFileString(spreadfile), LinkedHashMap.class);
		
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		
		ITaxRptService rpt =rptbillfactory.produce(corptaxvo);
		rpt.sendTaxReport(corpVO, userVO, objMapReport, spreadtool, reportvo, getSbo());
		return reportvo;
	}

	@Override
	public String getReportTemplet(String filePath) throws DZFWarpException {
		String strJson = TaxRptemptools.readFileString(filePath);
		
		return strJson;
	}
	
	public void delTaxReportFile(String filename){
		try {
			if(!StringUtil.isEmpty(filename)
					&& filename.startsWith("*")){
				((FastDfsUtil)SpringUtils.getBean("connectionPool")).deleteFile(filename.substring(1));
			}
		} catch (Exception e) {
			throw new BusinessException("文件名:" + filename + "删除失败");
		}
		
	}

	public String uploadTaxReportFile(String file, String filename ){
		
		String id = null;
		try {
			id = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).upload(file.getBytes("utf-8"), filename,
					new HashMap<String,String>());
		} catch (Exception e) {
			throw new BusinessException("文件名:" + filename + "上传失败");
		}
		
		if(!StringUtil.isEmpty(id)){
			id = "*"+id.substring(1);
		}else{
			throw new BusinessException("获取文件id失败!");
		}
		
		return id;
	}

	@Override
	public List<TaxReportVO> queryTaxReprotVOs(String periodfrom, String peridto, String pk_corp, String sbzt_dm)
			throws DZFWarpException {
		List<String> yearlist = getQueryYear(periodfrom,peridto);
		SQLParameter params = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append("  select t2.sbcode,t2.sbname,t2.sbzq periodtype ,t1.*  ");
		sf.append(" from ynt_taxreport t1 ");
		sf.append("    join ynt_tax_sbzl t2 on t1.pk_taxsbzl = t2.pk_taxsbzl ");
		sf.append("  where nvl(t1.dr,0)=0 and nvl(t2.dr,0) = 0 ");
		sf.append("  and t1.pk_corp = ?   ");
		params.addParam(pk_corp);
		if(yearlist != null && yearlist.size() > 0){
			sf.append("  and ((t1.period >= ? and t1.period <= ?) or t1.period in (");
			params.addParam(periodfrom);
			params.addParam(peridto);
			for(int i = 0 ;i<yearlist.size();i++){
				if(i ==0){
					sf.append("?");
				}else{
					sf.append(",?");
				}
				params.addParam(yearlist.get(i));
			}
			sf.append(" ))");
		}else{
			sf.append("  and t1.period >= ? and t1.period <= ?");
			params.addParam(periodfrom);
			params.addParam(peridto);
		}
		if(!StringUtil.isEmpty(sbzt_dm)){
			sf.append("  and  t1.sbzt_dm = ? ");
			params.addParam(sbzt_dm);
		}
		sf.append(" order by t1.period,t2.showorder ");
		List<TaxReportVO> list = (List<TaxReportVO>)sbo.executeQuery(sf.toString(), params, new BeanListProcessor(TaxReportVO.class));
		return list;
	}
	
	private List<String> getQueryYear(String period1,String period2){
		if(StringUtil.isEmpty(period1) || StringUtil.isEmpty(period2))
			return null;
		int year1 = Integer.valueOf(period1.substring(0,4)).intValue()-1;
		int year2 = Integer.valueOf(period2.substring(0,4)).intValue()-1;
		List<String> list = new ArrayList<String>();
		for(;year1<=year2;year1++){
			list.add(String.valueOf(year1));
		}
		if(period1.compareTo(period1.substring(0,4)+"-06")>=0){
			list.remove(0);
		}
		return list;
	}

	@Override
	public List<TaxReportDetailVO> queryTaxReprotDetailsVOs(String pk_corp, String pk_taxreport)
			throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_taxreport);
		String sql = " select * from  ynt_taxreportdetail where nvl(dr,0)=0 and pk_taxreport=? order by orderno ";
		List<TaxReportDetailVO> list = (List<TaxReportDetailVO>)sbo.executeQuery(sql, params, new BeanListProcessor(TaxReportDetailVO.class));
		return list;
	}

	@Override
	public TaxPaymentVO[] queryTaxPayment(String corpid, String pk_taxreport)
			throws DZFWarpException {
		CorpVO corpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, corpid);
		TaxReportVO reportvo = (TaxReportVO) sbo.queryByPrimaryKey(TaxReportVO.class, pk_taxreport);
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corpid);
		ITaxRptService taxRptService = rptbillfactory.produce(corptaxvo);
		TaxPaymentVO[] payments = taxRptService.queryTaxPayment(corpvo, reportvo);
		return payments;
	}

	/**
	 * 一台应用服务器，最多10个线程同时访问。
	 * 
	 * @param corp_id
	 * @param userVO
	 * @param logindate
	 * @throws DZFWarpException
	 */
	@Override
	public String saveBatWriteInfo(String corp_id,UserVO userVO,String logindate,Cookie[] cookies) throws DZFWarpException {
		if(StringUtil.isEmpty(corp_id)){
			throw new BusinessException("批量填写公司为空，请重试");
		}
		String requestid = null;
		String uuid = UUID.randomUUID().toString();
		StringBuffer sf = new StringBuffer();
		try{
			requestid = ConnPhantomjsPoolUtil.getInstance().checkout();
			if(StringUtil.isEmpty(requestid)){
				throw new BusinessException("服务器繁忙，请稍候在试!");
			}
			//当前公司加锁
//			LockUtil.getInstance().tryLockKey(corp_id, "batwrite", uuid, 30);
			//获取当前公司
			CorpVO vo = corpService.queryByPk(corp_id);
			CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corp_id);
			String corpname = vo.getUnitname();
			Integer ccounty = corptaxvo.getTax_area();
			//查询当前公司没有填写的申报信息
			String period = DateUtils.getPeriod(new DZFDate());
			ITaxRptService rpt = rptbillfactory.produce(corptaxvo);
			List<TaxReportVO> list = rpt.getTypeList(vo,corptaxvo, period, userVO.getCuserid(),logindate, sbo);
			//这里事务可能现在问题。在刷新状态的时候
//			List<TaxReportVO> list = getTypeList(corp_id,userVO,period,userVO.getCuserid(),logindate);
			if(list == null || list.size() == 0)
				throw new BusinessException("没有可填写的数据");
			for(TaxReportVO rvo : list){
				//已填写
				if(!StringUtil.isEmpty(rvo.getSpreadfile())){
					continue;
				}
				//如果 申报状态不是  未提交
				if(!String.valueOf(TaxRptConst.iSBZT_DM_UnSubmit).equals(rvo.getSbzt_dm())){
					continue;
				}
				boolean flag = Phantomjs.savewrite(userVO.getCuserid(), corp_id, corpname, ccounty, logindate, rvo,cookies);
				if(flag){
					sf.append(rvo.getSbname()+"填写成功</font><br>");
				}else{
					sf.append("<font color='red'>"+rvo.getSbname()+"填写失败<br>");
				}
			}
		}catch(Exception e){
			throw new WiseRunException(e);
		}finally{
			if(!StringUtil.isEmpty(requestid)){
				ConnPhantomjsPoolUtil.getInstance().checkin(requestid);
			}
//			LockUtil.getInstance().unLock_Key(corp_id, "batwrite", uuid);
		}
		return sf.toString();
	}

	@Override
	public String qryTaxReportValid(List<TaxReportVO> list, String pk_corp) throws DZFWarpException {
		CorpVO corpvo = getCorpVO(null, pk_corp);
		CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
		if (corptaxvo.getTax_area()!= null && corptaxvo.getTax_area() == 11) {//江苏
			ITaxRptService rpt =rptbillfactory.produce(corptaxvo);
			return rpt.checkReportList(corpvo,corptaxvo, list);
		}
		if(list == null || list.size() == 0)
			return null;
		List<String> zlist = querySelTaxRptVO(pk_corp);
		if(zlist == null || zlist.size() == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		for(TaxReportVO vo :list){
			if(!zlist.contains(vo.getSbcode()+","+String.valueOf(vo.getPeriodtype()))){
				sf.append("<font color='red'>在已勾选的纳税申报中，没有"+vo.getSbname()+"，目前已填写，请确认后手工删除</font><br>");
			}
		}
		return sf.toString();
	}
	
	
	private List<String> querySelTaxRptVO(String pk_corp){
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select distinct zl.sbcode,zl.sbzq from ynt_tax_sbzl zl ");
		sf.append(" join ynt_taxrpttemplet t1 on zl.pk_taxsbzl = t1.pk_taxsbzl ");
		sf.append(" join ynt_taxrpt t2 on t1.pk_taxrpttemplet = t2.pk_taxrpttemplet ");
		sf.append(" where nvl(t1.dr,0) = 0 and nvl(t2.dr,0) = 0 and nvl(zl.dr,0)=0 ");
		sf.append(" and t2.pk_corp = ?  ");
		List<String> list = (List<String>)sbo.executeQuery(sf.toString(), params, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				while(rs.next()){
					list.add(rs.getString("sbcode")+","+String.valueOf(rs.getInt("sbzq")));
				}
				return list;
			}
		});
		return list;
	}
}
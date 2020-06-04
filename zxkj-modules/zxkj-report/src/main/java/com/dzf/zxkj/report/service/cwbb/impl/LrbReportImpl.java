package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.LrbVO;
import com.dzf.zxkj.platform.model.report.LrbquarterlyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.ILrbQuarterlyReport;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IRptSetService;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.OtherSystemForLrb;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

//import com.dzf.model.gl.gl_bdset.YntCpaccountVO;

/**
 * 利润表
 * 
 * 
 */
@Service("gl_rep_lrbserv")
@SuppressWarnings("all")
public class LrbReportImpl implements ILrbReport {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IFsYeReport gl_rep_fsyebserv;

	@Autowired
	private IRptSetService rptsetser;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;


	/**
	 * 利润表取数
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public LrbVO[] getLRBVOs(QueryParamVO vo) throws DZFWarpException {
		return getLRBVOsConXm(vo, null);
	}
	
	@Override
	public LrbVO[] getLRBVOsConXm(QueryParamVO vo, List<String> xmid) throws DZFWarpException {
		String pk_corp = vo.getPk_corp();
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		/** 来源利润表 */
		vo.setRptsource("lrb");
		vo.setFirstlevelkms(rptsetser.queryLrbKmsFromDaima(cpvo.getPk_corp(),xmid));
		int year = new DZFDate(vo.getQjq() + "-01").getYear();
		vo.setPk_corp(pk_corp);
		vo.setQjq(year + "-01");
		if (!StringUtil.isEmpty(vo.getXmlbid()) && !StringUtil.isEmpty(vo.getXmmcid())) {
			vo.setSfzxm(DZFBoolean.TRUE);
		}
		Object[] obj = gl_rep_fsyebserv.getFsJyeVOs1(vo);
		FseJyeVO[] fvos = (FseJyeVO[]) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		mp = convert(mp);
		Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
		int len = fvos == null ? 0 : fvos.length;
		for (int i = 0; i < len; i++) {
			if (fvos[i] != null) {
				map.put(fvos[i].getKmbm(), fvos[i]);
			}
		}

		LrbVO[] lrbvos = getLrbVos(vo, pk_corp, mp, map, vo.getXmmcid());
		if(xmid != null && xmid.size() > 0
				&& lrbvos != null && lrbvos.length > 0){
			List<LrbVO> lrList = new ArrayList<LrbVO>();
			for(LrbVO lrvo : lrbvos){
				if(xmid.contains(lrvo.getHs())){
					lrList.add(lrvo);
				}
			}
			
			if(lrList.size() > 0){
				lrbvos = lrList.toArray(new LrbVO[0]);
			}
		}
		
		return lrbvos;
	}

	/**
	 * 通过发生额余额表生成利润表数据
	 * 
	 * @param vo
	 * @param pk_corp
	 * @param mp
	 * @param map
	 * @return
	 */
	public LrbVO[] getLrbVos(QueryParamVO vo, String pk_corp, Map<String, YntCpaccountVO> mp,
			Map<String, FseJyeVO> map, String xmmcid) {

		Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
		LrbVO[] lrbvos = null;
		/** 2007会计准则(一般) */
		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
			String zxzc = zxkjPlatformService.queryParamterValueByCode(pk_corp, "dzf025");
			if ("财会【2019】6号".equals(zxzc)) { // 财会【2019】6号
				OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
				lrbvos = lrb_qykj.getCompanyVos(map, mp, vo.getQjz(), pk_corp, xmmcid,singleObjectBO,"00000100AA10000000000BMF","");
			}else {
				lrbvos = getLRB2007VOs(map, mp, vo.getQjz(), pk_corp, xmmcid);
			}
		} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {/** 2013会计准则(小企业) */
			lrbvos = getLRB2013VOs(map, mp, vo.getQjz(), pk_corp, xmmcid);
		} else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){/** 企业会计制度 */
			OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
			lrbvos = lrb_qykj.getCompanyVos(map, mp, vo.getQjz(), pk_corp, xmmcid,singleObjectBO,"00000100000000Ig4yfE0005","");
		} 
//		else if(corpschema == DzfUtil.RURALCOOPERATIVE.intValue()){//农村合作社
//			OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
//			lrbvos = lrb_qykj.getCompanyVos(map, mp, vo.getQjz(), pk_corp, xmmcid,singleObjectBO,"00000100000000Ig4yfE0006");
//		} 
		else {
			throw new BusinessException("该制度暂不支持利润表,敬请期待!");
		}
		
		if(lrbvos!=null && lrbvos.length>0){
			for(LrbVO lrbvo:lrbvos){
				lrbvo.setKmfa(corpschema+"");
			}
		}
		return lrbvos;
	}


	private LrbVO getLRBVO(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq, String xmmcid,
			LrbVO vo, String... kms) {

		DZFDouble ufd = null;
		int direction = 0;
		int len = kms == null ? 0 : kms.length;
		YntCpaccountVO km = null;
		List<FseJyeVO> ls = null;
		for (int i = 0; i < len; i++) {
			km = mp.get(kms[i]);
			if (km == null)
				continue;
			direction = km.getDirection();
			ls = getData(map, kms[i], mp, xmmcid);
			for (FseJyeVO fvo : ls) {

				if (direction == 0) {
					if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
						ufd = VoUtils.getDZFDouble(vo.getByje());

						vo.setByje(ufd.add(SafeCompute.sub(fvo.getEndfsjf(), fvo.getEndfsdf())));//借-贷方
					}
					ufd = VoUtils.getDZFDouble(vo.getBnljje());
					vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getJftotal(), fvo.getDftotal())));
				} else {
					if (rq.substring(0, 7).equals(fvo.getEndrq().substring(0, 7))) {
						ufd = VoUtils.getDZFDouble(vo.getByje());
						vo.setByje(ufd.add(SafeCompute.sub(fvo.getEndfsdf(),fvo.getEndfsjf())));
					}
					ufd = VoUtils.getDZFDouble(vo.getBnljje());
					vo.setBnljje(ufd.add(SafeCompute.sub(fvo.getDftotal(),fvo.getJftotal())));
				}
			}
		}
		return vo;
	}

	private Map<String, YntCpaccountVO> convert(Map<String, YntCpaccountVO> mp) {
		Map<String, YntCpaccountVO> mp1 = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO b : mp.values()) {
			mp1.put(b.getAccountcode(), b);
		}
		return mp1;
	}

	private List<FseJyeVO> getData(Map<String, FseJyeVO> map, String km, Map<String, YntCpaccountVO> mp,
			String xmmcid) {
		List<FseJyeVO> list = new ArrayList<FseJyeVO>();
		for (FseJyeVO fsejyevo : map.values()) {
			if (!StringUtil.isEmpty(xmmcid)) {// 查询对应某个项目的利润表数据
				if (!StringUtil.isEmpty(fsejyevo.getPk_km()) && fsejyevo.getKmbm().startsWith(km)
						&& fsejyevo.getPk_km().indexOf("_" + xmmcid) > 0) {
					list.add(fsejyevo);
				}
			} else {
				if (fsejyevo.getKmbm().equals(km)) {
					list.add(fsejyevo);
				}
			}
		}
		return list;
	}

	/**
	 * 2007会计准则的利润表
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private LrbVO[] getLRB2007VOs(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String qjz, String pk_corp,
			String xmmcid) throws DZFWarpException {
		String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
		LrbVO vo1 = new LrbVO();
		vo1.setXm("一、营业收入");
		vo1 = getLRBVO(map, mp, qjz, xmmcid, vo1, "6001", "6051");
		vo1.setHs("1");
		vo1.setHs_id("LR-001");
		vo1.setLevel(1);
		vo1.setVconkms("6001,6051");

		LrbVO vo2 = new LrbVO();
		vo2.setXm("减：营业成本");
		vo2 = getLRBVO(map, mp, qjz, xmmcid, vo2, "6401", "6402");
		vo2.setHs("2");
		vo2.setHs_id("LR-002");
		vo2.setLevel(2);
		vo2.setVconkms("6401,6402");

		LrbVO vo3 = new LrbVO();
		vo3.setXm("税金及附加");
		vo3 = getLRBVO(map, mp, qjz, xmmcid, vo3, "6403");
		vo3.setHs("3");
		vo3.setHs_id("LR-003");
		vo3.setLevel(3);
		vo3.setVconkms("6403");

		LrbVO vo4 = new LrbVO();
		vo4.setXm("销售费用");
		vo4 = getLRBVO(map, mp, qjz, xmmcid, vo4, "6601");
		vo4.setHs("4");
		vo4.setHs_id("LR-004");
		vo4.setLevel(3);
		vo4.setVconkms("6601");

		LrbVO vo5 = new LrbVO();
		vo5.setXm("管理费用");
		vo5 = getLRBVO(map, mp, qjz, xmmcid, vo5, "6602");
		vo5.setHs("5");
		vo5.setHs_id("LR-005");
		vo5.setLevel(3);
		vo5.setVconkms("6602");

		LrbVO vo6 = new LrbVO();
		vo6.setXm("财务费用");
		vo6 = getLRBVO(map, mp, qjz, xmmcid, vo6, "6603");
		vo6.setHs("6");
		vo6.setHs_id("LR-006");
		vo6.setLevel(3);
		vo6.setVconkms("6603");

		LrbVO vo7 = new LrbVO();
		vo7.setXm("　资产减值损失");
		vo7 = getLRBVO(map, mp, qjz, xmmcid, vo7, "6701");
		vo7.setHs("7");
		vo7.setHs_id("LR-007");
		vo7.setLevel(3);
		vo7.setVconkms("6701");

		LrbVO vo8 = new LrbVO();
		vo8.setXm("加：公允价值变动收益（损失以“-”号填列）");
		vo8 = getLRBVO(map, mp, qjz, xmmcid, vo8, "6101");
		vo8.setHs("8");
		vo8.setHs_id("LR-008");
		vo8.setLevel(2);
		vo8.setVconkms("6101");

		LrbVO vo9 = new LrbVO();
		vo9.setXm("　　投资收益（损失以“-”号填列）");
		vo9 = getLRBVO(map, mp, qjz, xmmcid, vo9, "6111");
		vo9.setHs("9");
		vo9.setHs_id("LR-009");
		vo9.setLevel(3);
		vo9.setVconkms("6111");

		LrbVO vo10 = new LrbVO();
		vo10.setXm("　　其中:对联营企业和合营企业的投资收益");
		vo10.setHs("10");
		vo10.setHs_id("LR-010");
		vo10.setLevel(3);

		LrbVO vo21 = new LrbVO();
		vo21.setXm("　　资产处置收益（损失以“-”号填列）");
		vo21 = getLRBVO(map, mp, qjz, xmmcid, vo21, "6115");
		vo21.setHs("11");
		vo21.setHs_id("LR-011");
		vo21.setLevel(3);
		vo21.setVconkms("6115");

		LrbVO vo22 = new LrbVO();
		vo22.setXm("　　其他收益");
		vo22 = getLRBVO(map, mp, qjz, xmmcid, vo22, "6117");
		vo22.setHs("12");
		vo22.setHs_id("LR-012");
		vo22.setLevel(3);
		vo22.setVconkms("6117");

		LrbVO vo11 = new LrbVO();
		vo11.setXm("二、营业利润（亏损以“-”号填列）");
		vo11.setFormula(vo1.getHs_id()+",-"+vo2.getHs_id()+",-"+vo3.getHs_id()+",-"+vo4.getHs_id()+",-"+vo5.getHs_id()+",-"+vo6.getHs_id()+
				",-"+vo7.getHs_id()+",+"+vo8.getHs_id()+",+"+vo21.getHs_id()+",+"+vo22.getHs_id()+",+"+vo9.getHs_id());
		vo11.setByje(VoUtils.getDZFDouble(vo1.getByje()).sub(VoUtils.getDZFDouble(vo2.getByje())).sub(VoUtils.getDZFDouble(vo3.getByje()))
				.sub(VoUtils.getDZFDouble(vo4.getByje())).sub(VoUtils.getDZFDouble(vo5.getByje())).sub(VoUtils.getDZFDouble(vo6.getByje()))
				.sub(VoUtils.getDZFDouble(vo7.getByje())).add(VoUtils.getDZFDouble(vo8.getByje())).add(VoUtils.getDZFDouble(vo21.getByje()))
				.add(VoUtils.getDZFDouble(vo22.getByje())).add(VoUtils.getDZFDouble(vo9.getByje())));
		vo11.setBnljje(VoUtils.getDZFDouble(vo1.getBnljje()).sub(VoUtils.getDZFDouble(vo2.getBnljje())).sub(VoUtils.getDZFDouble(vo3.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo4.getBnljje())).sub(VoUtils.getDZFDouble(vo5.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo6.getBnljje())).sub(VoUtils.getDZFDouble(vo7.getBnljje()))
						.add(VoUtils.getDZFDouble(vo8.getBnljje())).add(VoUtils.getDZFDouble(vo21.getBnljje()))
						.add(VoUtils.getDZFDouble(vo22.getBnljje())).add(VoUtils.getDZFDouble(vo9.getBnljje())));
		vo11.setHs("13");
		vo11.setHs_id("LR-013");
		vo11.setLevel(1);

		LrbVO vo12 = new LrbVO();
		vo12.setXm("加：营业外收入");
		vo12 = getLRBVO(map, mp, qjz, xmmcid, vo12, "6301");
		vo12.setHs("14");
		vo12.setHs_id("LR-014");
		vo12.setLevel(2);
		vo12.setVconkms("6301");
		
		LrbVO vo12_1 = new LrbVO();
		vo12_1.setXm("　　其中：非流动资产处置利得");
		String newRuleCode = zxkjPlatformService.getNewRuleCode("630101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		vo12_1 = getLRBVO(map, mp, qjz, xmmcid, vo12_1, newRuleCode);
		vo12_1.setHs("15");
		vo12_1.setHs_id("LR-015");
		vo12_1.setLevel(2);
		vo12_1.setVconkms("630101");

		LrbVO vo13 = new LrbVO();
		vo13.setXm("减：营业外支出");
		vo13 = getLRBVO(map, mp, qjz, xmmcid, vo13, "6711");
		vo13.setHs("16");
		vo13.setHs_id("LR-016");
		vo13.setLevel(2);
		vo13.setVconkms("6711");

		LrbVO vo14 = new LrbVO();
		newRuleCode = zxkjPlatformService.getNewRuleCode("671101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		vo14 = getLRBVO(map, mp, qjz, xmmcid, vo14, newRuleCode);
		vo14.setXm("　　其中：非流动资产处置损失");
		vo14.setHs("17");
		vo14.setHs_id("LR-017");
		vo14.setLevel(3);
		vo14.setVconkms(newRuleCode);

		LrbVO vo15 = new LrbVO();
		vo15.setXm("三、利润总额（亏损总额以“-”号填列）");
		vo15.setByje(VoUtils.getDZFDouble(vo11.getByje()).add(VoUtils.getDZFDouble(vo12.getByje())).sub(VoUtils.getDZFDouble(vo13.getByje())));
		vo15.setBnljje(
				VoUtils.getDZFDouble(vo11.getBnljje()).add(VoUtils.getDZFDouble(vo12.getBnljje())).sub(VoUtils.getDZFDouble(vo13.getBnljje())));
		vo15.setFormula(vo11.getHs_id()+",+"+vo12.getHs_id()+",-"+vo13.getHs_id());
		vo15.setHs("18");
		vo15.setHs_id("LR-018");
		vo15.setLevel(1);

		LrbVO vo16 = new LrbVO();
		vo16.setXm("减：所得税费用");
		vo16 = getLRBVO(map, mp, qjz, xmmcid, vo16, "6801");
		vo16.setHs("19");
		vo16.setHs_id("LR-019");
		vo16.setLevel(2);
		vo16.setVconkms("6801");

		LrbVO vo17 = new LrbVO();
		vo17.setXm("四、净利润（净亏损以“-”号填列）");
		vo17.setByje(VoUtils.getDZFDouble(vo15.getByje()).sub(VoUtils.getDZFDouble(vo16.getByje())));
		vo17.setBnljje(VoUtils.getDZFDouble(vo15.getBnljje()).sub(VoUtils.getDZFDouble(vo16.getBnljje())));
		vo17.setFormula(vo15.getHs_id()+",-"+vo16.getHs_id());
		vo17.setHs("20");
		vo17.setHs_id("LR-020");
		vo17.setLevel(1);
		
		LrbVO vo17_1 = new LrbVO();
		vo17_1.setXm("（一）持续经营净利润（净亏损以“-”号填列）");
		vo17_1.setHs("21");
		vo17_1.setHs_id("LR-021");
		vo17_1.setLevel(2);
		
		LrbVO vo17_2 = new LrbVO();
		vo17_2.setXm("（二）终止经营净利润（净亏损以“-”号填列）");
		vo17_2.setHs("22");
		vo17_2.setHs_id("LR-022");
		vo17_2.setLevel(2);
		
		LrbVO vo17_3 = new LrbVO();
		vo17_3.setXm("五、其他综合收益的税后净额");
		vo17_3.setHs("23");
		vo17_3.setHs_id("LR-023");
		vo17_3.setLevel(2);
		
		LrbVO vo17_4 = new LrbVO();
		vo17_4.setXm("（一）以后不能重分类进损益的其他综合收益");
		vo17_4.setHs("24");
		vo17_4.setHs_id("LR-024");
		vo17_4.setLevel(2);
		
		LrbVO vo17_5 = new LrbVO();
		vo17_5.setXm("1.重新计量设定受益计划净负债或净资产的变动");
		vo17_5.setHs("25");
		vo17_5.setHs_id("LR-025");
		vo17_5.setLevel(2);
		
		LrbVO vo17_6 = new LrbVO();
		vo17_6.setXm("2.权益法下在被投资单位不能重分类进损益的其他综合收益中享有的份额");
		vo17_6.setHs("26");
		vo17_6.setHs_id("LR-026");
		vo17_6.setLevel(2);
		
		LrbVO vo17_7 = new LrbVO();
		vo17_7.setXm("（二）以后将重分类进损益的其他综合收益");
		vo17_7.setHs("27");
		vo17_7.setHs_id("LR-027");
		vo17_7.setLevel(2);
		
		LrbVO vo17_8 = new LrbVO();
		vo17_8.setXm("1.权益法下在被投资单位以后将重分类进损益的其他综合收益中享有的份额 ");
		vo17_8.setHs("28");
		vo17_8.setHs_id("LR-028");
		vo17_8.setLevel(2);
		
		LrbVO vo17_9 = new LrbVO();
		vo17_9.setXm("2.可供出售金融资产公允价值变动损益");
		vo17_9.setHs("29");
		vo17_9.setHs_id("LR-029");
		vo17_9.setLevel(2);
		
		LrbVO vo17_10 = new LrbVO();
		vo17_10.setXm("3.持有至到期投资重分类为可供出售金融资产损益");
		vo17_10.setHs("30");
		vo17_10.setHs_id("LR-030");
		vo17_10.setLevel(2);
		
		LrbVO vo17_11 = new LrbVO();
		vo17_11.setXm("4.现金流量套期损益的有效部分");
		vo17_11.setHs("31");
		vo17_11.setHs_id("LR-031");
		vo17_11.setLevel(2);
		
		LrbVO vo17_12 = new LrbVO();
		vo17_12.setXm("5.外币财务报表折算差额");
		vo17_12.setHs("32");
		vo17_12.setHs_id("LR-032");
		vo17_12.setLevel(2);
		
		LrbVO vo17_13 = new LrbVO();
		vo17_13.setXm("六、综合收益总额");
		vo17_13.setHs("33");
		vo17_13.setHs_id("LR-033");
		vo17_13.setByje(VoUtils.getDZFDouble(vo17.getByje()).add(VoUtils.getDZFDouble(vo17_3.getByje())));
		vo17_13.setBnljje(VoUtils.getDZFDouble(vo17.getBnljje()).add(VoUtils.getDZFDouble(vo17_3.getBnljje())));
		vo17_13.setFormula(vo17.getHs_id()+",-"+vo17_3.getHs_id());
		vo17_13.setLevel(2);

		LrbVO vo18 = new LrbVO();
		vo18.setXm("七、每股收益：");
		vo18.setHs("34");
		vo18.setHs_id("LR-034");
		vo18.setLevel(1);

		LrbVO vo19 = new LrbVO();
		vo19.setXm("　（一）基本每股收益");
		vo19.setHs("35");
		vo19.setHs_id("LR-035");
		vo19.setLevel(2);

		LrbVO vo20 = new LrbVO();
		vo20.setXm("　（二）稀释每股收益");
		vo20.setHs("36");
		vo20.setHs_id("LR-036");
		vo20.setLevel(2);
		
		LrbVO[] resvos = new LrbVO[] { vo1, vo2, vo3, vo4, vo5, vo6, vo7, vo8, vo9, vo10,vo21, vo22, vo11, vo12,vo12_1, vo13, vo14, vo15, vo16,
				vo17,vo17_1,vo17_2,vo17_3,vo17_4,vo17_5,vo17_6,vo17_7,vo17_8,vo17_9,vo17_10,vo17_11,vo17_12,vo17_13, vo18, vo19, vo20 };
		return resvos;

	}

	/**
	 * 2013会计准则的利润表
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private LrbVO[] getLRB2013VOs(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String rq, String pk_corp,
			String xmmcid) throws DZFWarpException {
		LrbVO vo1 = new LrbVO();
		vo1.setXm("一、营业收入");
		vo1 = getLRBVO(map, mp, rq, xmmcid, vo1, "5001", "5051");
		vo1.setHs("1");
		vo1.setLevel(1);
		vo1.setVconkms("5001,5051");
		vo1.setHs_id("LR-001");

		LrbVO vo2 = new LrbVO();
		vo2.setXm("减：营业成本");
		vo2 = getLRBVO(map, mp, rq, xmmcid, vo2, "5401", "5402");
		vo2.setHs("2");
		vo2.setLevel(2);
		vo2.setVconkms("5401,5402");
		vo2.setHs_id("LR-002");

		LrbVO vo3 = new LrbVO();
		vo3.setXm("税金及附加");
		vo3 = getLRBVO(map, mp, rq, xmmcid, vo3, "5403");
		vo3.setHs("3");
		vo3.setLevel(3);
		vo3.setVconkms("5403");
		vo3.setHs_id("LR-003");

		LrbVO vo4 = new LrbVO();
		vo4.setXm("其中：消费税");
		String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
		String newRuleCode = zxkjPlatformService.getNewRuleCode("540301", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		vo4 = getLRBVO(map, mp, rq, xmmcid, vo4, newRuleCode);
		vo4.setHs("4");
		vo4.setLevel(4);
		vo4.setVconkms(newRuleCode);
		vo4.setHs_id("LR-004");

		LrbVO vo5 = new LrbVO();
		vo5.setXm("　　　营业税");
		String newRuleCode1 = zxkjPlatformService.getNewRuleCode("540302", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo5 = getLRBVO(map, mp, rq, xmmcid, vo5, newRuleCode1);
		vo5.setHs("5");
		vo5.setLevel(4);
		vo5.setVconkms(newRuleCode1);
		vo5.setHs_id("LR-005");

		LrbVO vo6 = new LrbVO();
		vo6.setXm("　　　城市维护建设税");
		String newRuleCode2 = zxkjPlatformService.getNewRuleCode("540303", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo6 = getLRBVO(map, mp, rq, xmmcid, vo6, newRuleCode2);
		vo6.setHs("6");
		vo6.setLevel(4);
		vo6.setVconkms(newRuleCode2);
		vo6.setHs_id("LR-006");

		LrbVO vo7 = new LrbVO();
		vo7.setXm("　　　资源税");
		String newRuleCode3 = zxkjPlatformService.getNewRuleCode("540304", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo7 = getLRBVO(map, mp, rq, xmmcid, vo7, newRuleCode3);
		vo7.setHs("7");
		vo7.setLevel(4);
		vo7.setVconkms(newRuleCode3);
		vo7.setHs_id("LR-007");

		LrbVO vo8 = new LrbVO();
		vo8.setXm("　　　土地增值税");
		String newRuleCode4 = zxkjPlatformService.getNewRuleCode("540305", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo8 = getLRBVO(map, mp, rq, xmmcid, vo8, newRuleCode4);
		vo8.setHs("8");
		vo8.setLevel(4);
		vo8.setVconkms(newRuleCode4);
		vo8.setHs_id("LR-008");

		LrbVO vo9 = new LrbVO();
		vo9.setXm("　　　城镇土地使用税、房产税、车船税、印花税");
		String newRuleCode5 = zxkjPlatformService.getNewRuleCode("540306", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo9 = getLRBVO(map, mp, rq, xmmcid, vo9, newRuleCode5);
		vo9.setHs("9");
		vo9.setLevel(4);
		vo9.setVconkms(newRuleCode5);
		vo9.setHs_id("LR-009");

		LrbVO vo10 = new LrbVO();
		vo10.setXm("　　　教育费附加、矿产资源补偿费、排污费");
		String newRuleCode6 = zxkjPlatformService.getNewRuleCode("540307", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo10 = getLRBVO(map, mp, rq, xmmcid, vo10, newRuleCode6);
		vo10.setHs("10");
		vo10.setLevel(4);
		vo10.setVconkms(newRuleCode6);
		vo10.setHs_id("LR-010");

		LrbVO vo11 = new LrbVO();
		vo11.setXm("销售费用");
		vo11 = getLRBVO(map, mp, rq, xmmcid, vo11, "5601");
		vo11.setHs("11");
		vo11.setLevel(3);
		vo11.setVconkms("5601");
		vo11.setHs_id("LR-011");

		LrbVO vo12 = new LrbVO();
		vo12.setXm("其中：商品维修费");
		String newRuleCode7 = zxkjPlatformService.getNewRuleCode("560116", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo12 = getLRBVO(map, mp, rq, xmmcid, vo12, newRuleCode7);
		vo12.setHs("12");
		vo12.setLevel(4);
		vo12.setVconkms(newRuleCode7);
		vo12.setHs_id("LR-012");

		LrbVO vo13 = new LrbVO();
		vo13.setXm("　　　广告费和业务宣传费");
		String newRuleCode8 = zxkjPlatformService.getNewRuleCode("560105", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo13 = getLRBVO(map, mp, rq, xmmcid, vo13, newRuleCode8);
		vo13.setHs("13");
		vo13.setLevel(4);
		vo13.setVconkms(newRuleCode8);
		vo13.setHs_id("LR-013");

		LrbVO vo14 = new LrbVO();
		vo14.setXm("管理费用");
		vo14 = getLRBVO(map, mp, rq, xmmcid, vo14, "5602");
		vo14.setHs("14");
		vo14.setLevel(3);
		vo14.setVconkms("5602");
		vo14.setHs_id("LR-014");

		LrbVO vo15 = new LrbVO();
		vo15.setXm("其中：开办费");
		String newRuleCode9 = zxkjPlatformService.getNewRuleCode("560208", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo15 = getLRBVO(map, mp, rq, xmmcid, vo15, newRuleCode9);
		vo15.setHs("15");
		vo15.setLevel(4);
		vo15.setVconkms(newRuleCode9);
		vo15.setHs_id("LR-015");

		LrbVO vo16 = new LrbVO();
		vo16.setXm("　　　业务招待费");
		String newRuleCode10 = zxkjPlatformService.getNewRuleCode("560204", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo16 = getLRBVO(map, mp, rq, xmmcid, vo16, newRuleCode10);
		vo16.setHs("16");
		vo16.setLevel(5);
		vo16.setVconkms(newRuleCode10);
		vo16.setHs_id("LR-016");

		LrbVO vo17 = new LrbVO();
		vo17.setXm("　　　研究费用");
		String newRuleCode11 = zxkjPlatformService.getNewRuleCode("560220", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo17 = getLRBVO(map, mp, rq, xmmcid, vo17, newRuleCode11);
		vo17.setHs("17");
		vo17.setLevel(5);
		vo17.setVconkms(newRuleCode11);
		vo17.setHs_id("LR-017");

		LrbVO vo18 = new LrbVO();
		vo18.setXm("财务费用");
		vo18 = getLRBVO(map, mp, rq, xmmcid, vo18, "5603");
		vo18.setHs("18");
		vo18.setLevel(3);
		vo18.setVconkms("5603");
		vo18.setHs_id("LR-018");

		LrbVO vo19 = new LrbVO();
		String newRuleCode12 = zxkjPlatformService.getNewRuleCode("560301", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo19 = getLRBVO(map, mp, rq, xmmcid, vo19, newRuleCode12);
		vo19.setXm("其中：利息费用（收入以“-”号填列）");
		vo19.setHs("19");
		vo19.setLevel(4);
		vo19.setVconkms(newRuleCode12);
		vo19.setHs_id("LR-019");

		LrbVO vo20 = new LrbVO();
		vo20.setXm("加：投资收益（损失以“-”号填列）");
		vo20 = getLRBVO(map, mp, rq, xmmcid, vo20, "5111");
		vo20.setHs("20");
		vo20.setLevel(2);
		vo20.setVconkms("5111");
		vo20.setHs_id("LR-020");

		LrbVO vo21 = new LrbVO();
		vo21.setXm("二、营业利润（亏损以“-”号填列） ");
		vo21.setFormula(vo1.getHs_id() + ",-" + vo2.getHs_id() + ",-" + vo3.getHs_id() + ",-" + vo11.getHs_id() + ",-"
				+ vo14.getHs_id() + ",-" + vo18.getHs_id() + ",+" + vo20.getHs_id());
		vo21.setByje(VoUtils.getDZFDouble(vo1.getByje()).sub(VoUtils.getDZFDouble(vo2.getByje())).sub(VoUtils.getDZFDouble(vo3.getByje()))
				.sub(VoUtils.getDZFDouble(vo11.getByje())).sub(VoUtils.getDZFDouble(vo14.getByje())).sub(VoUtils.getDZFDouble(vo18.getByje()))
				.add(VoUtils.getDZFDouble(vo20.getByje())));
		vo21.setBnljje(
				VoUtils.getDZFDouble(vo1.getBnljje()).sub(VoUtils.getDZFDouble(vo2.getBnljje())).sub(VoUtils.getDZFDouble(vo3.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo11.getBnljje())).sub(VoUtils.getDZFDouble(vo14.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo18.getBnljje())).add(VoUtils.getDZFDouble(vo20.getBnljje())));
		vo21.setHs("21");
		vo21.setLevel(1);
		vo21.setHs_id("LR-021");

		LrbVO vo22 = new LrbVO();
		vo22.setXm("加：营业外收入");
		vo22 = getLRBVO(map, mp, rq, xmmcid, vo22, "5301");
		vo22.setHs("22");
		vo22.setLevel(1);
		vo22.setVconkms("5301");
		vo22.setHs_id("LR-022");

		LrbVO vo23 = new LrbVO();
		vo23.setXm("其中：政府补助");
		String newRuleCode13 = zxkjPlatformService.getNewRuleCode("530104", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo23 = getLRBVO(map, mp, rq, xmmcid, vo23, newRuleCode13);
		vo23.setHs("23");
		vo23.setLevel(4);
		vo23.setVconkms(newRuleCode13);
		vo23.setHs_id("LR-023");

		LrbVO vo24 = new LrbVO();
		vo24.setXm("减：营业外支出");
		vo24 = getLRBVO(map, mp, rq, xmmcid, vo24, "5711");
		vo24.setHs("24");
		vo24.setLevel(1);
		vo24.setVconkms("5711");
		vo24.setHs_id("LR-024");

		LrbVO vo25 = new LrbVO();
		vo25.setXm("其中：坏账损失");
		String newRuleCode14 = zxkjPlatformService.getNewRuleCode("571108", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo25 = getLRBVO(map, mp, rq, xmmcid, vo25, newRuleCode14);
		vo25.setHs("25");
		vo25.setLevel(4);
		vo25.setVconkms(newRuleCode14);
		vo25.setHs_id("LR-025");

		LrbVO vo26 = new LrbVO();
		vo26.setXm("　　　无法收回的长期债券投资损失");
		String newRuleCode15 = zxkjPlatformService.getNewRuleCode("571109", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo26 = getLRBVO(map, mp, rq, xmmcid, vo26, newRuleCode15);
		vo26.setHs("26");
		vo26.setLevel(5);
		vo26.setVconkms(newRuleCode15);
		vo26.setHs_id("LR-026");

		LrbVO vo27 = new LrbVO();
		vo27.setXm("　　　无法收回的长期股权投资损失");
		String newRuleCode16 = zxkjPlatformService.getNewRuleCode("571110", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo27 = getLRBVO(map, mp, rq, xmmcid, vo27, newRuleCode16);
		vo27.setHs("27");
		vo27.setLevel(5);
		vo27.setVconkms(newRuleCode16);
		vo27.setHs_id("LR-027");

		LrbVO vo28 = new LrbVO();
		vo28.setXm("　　　自然灾害等不可抗力因素造成的损失");
		String newRuleCode17 = zxkjPlatformService.getNewRuleCode("571111", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo28 = getLRBVO(map, mp, rq, xmmcid, vo28, newRuleCode17);
		vo28.setHs("28");
		vo28.setLevel(5);
		vo28.setVconkms(newRuleCode17);
		vo28.setHs_id("LR-028");

		LrbVO vo29 = new LrbVO();
		vo29.setXm("　　　税收滞纳金");
		String newRuleCode18 = zxkjPlatformService.getNewRuleCode("571112", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo29 = getLRBVO(map, mp, rq, xmmcid, vo29, newRuleCode18);
		vo29.setHs("29");
		vo29.setLevel(5);
		vo29.setVconkms(newRuleCode18);
		vo29.setHs_id("LR-029");

		LrbVO vo30 = new LrbVO();
		vo30.setXm("三、利润总额（亏损总额以“-”号填列）");
		vo30.setFormula(vo21.getHs_id()+",+"+vo22.getHs_id()+",-"+vo24.getHs_id());
		vo30.setByje(VoUtils.getDZFDouble(vo21.getByje()).add(VoUtils.getDZFDouble(vo22.getByje())).sub(VoUtils.getDZFDouble(vo24.getByje())));
		vo30.setBnljje(
				VoUtils.getDZFDouble(vo21.getBnljje()).add(VoUtils.getDZFDouble(vo22.getBnljje())).sub(VoUtils.getDZFDouble(vo24.getBnljje())));
		vo30.setHs("30");
		vo30.setLevel(1);
		vo30.setHs_id("LR-030");

		LrbVO vo31 = new LrbVO();
		vo31.setXm("减：所得税费用");
		vo31 = getLRBVO(map, mp, rq, xmmcid, vo31, "5801");
		vo31.setHs("31");
		vo31.setLevel(1);
		vo31.setVconkms("5801");
		vo31.setHs_id("LR-031");

		LrbVO vo32 = new LrbVO();
		vo32.setXm("四、净利润（净亏损以“-”号填列）");
		vo32.setFormula(vo30.getHs_id()+",-"+vo31.getHs_id());
		vo32.setByje(VoUtils.getDZFDouble(vo30.getByje()).sub(VoUtils.getDZFDouble(vo31.getByje())));
		vo32.setBnljje(VoUtils.getDZFDouble(vo30.getBnljje()).sub(VoUtils.getDZFDouble(vo31.getBnljje())));
		vo32.setHs("32");
		vo32.setLevel(1);
		vo32.setHs_id("LR-032");

		return new LrbVO[] { vo1, vo2, vo3, vo4, vo5, vo6, vo7, vo8, vo9, vo10, vo11, vo12, vo13, vo14, vo15, vo16,
				vo17, vo18, vo19, vo20, vo21, vo22, vo23, vo24, vo25, vo26, vo27, vo28, vo29, vo30, vo31, vo32 };

	}

	/**
	 * 按照年取每年的利润表
	 */
	@Override
	public Map<String, DZFDouble> getYearLRBVOs(String year, String pk_corp, Object[] objs) throws DZFWarpException {
		Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp, objs,"lrb");
		Map<String, DZFDouble> mapyearmny = new HashMap<String, DZFDouble>();

		/** 为了防止空值，每个月都有值 */
		mapyearmny.put(year + "-01", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-02", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-03", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-04", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-05", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-06", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-07", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-08", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-09", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-10", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-11", DZFDouble.ZERO_DBL);
		mapyearmny.put(year + "-12", DZFDouble.ZERO_DBL);

		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];

		Set<String> yearset = monthmap.keySet();
		for (String str : yearset) {
			List<FseJyeVO> listfs = monthmap.get(str);
			DZFDate datevalue = new DZFDate(str + "-01");
			str = str + "-" + datevalue.getDaysMonth();
			FseJyeVO[] fvos = (FseJyeVO[]) listfs.toArray(new FseJyeVO[0]);
			mp = convert(mp);
			Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
			int len = fvos == null ? 0 : fvos.length;
			for (int i = 0; i < len; i++) {
				if (fvos[i] != null) {
					map.put(fvos[i].getKmbm(), fvos[i]);
				}
			}
			Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
			LrbVO[] lrbvos = null;
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
				/** 2007会计准则 */
				lrbvos = getLRB2007VOs(map, mp, str, pk_corp, "");
				mapyearmny.put(str.substring(0, 7),
						lrbvos[16].getByje() == null ? DZFDouble.ZERO_DBL : lrbvos[16].getByje());
			} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
				/** 2013会计准则 */
				lrbvos = getLRB2013VOs(map, mp, str, pk_corp, "");
				mapyearmny.put(str.substring(0, 7),
						lrbvos[31].getByje() == null ? DZFDouble.ZERO_DBL : lrbvos[31].getByje());
			}else{
				throw new BusinessException("该制度暂不支持利润表,敬请期待!");
			}
		}

		return mapyearmny;
	}

	@Override
	public Map<String, List<LrbVO>> getYearLrbMap(String year, String pk_corp, String xmmcid, Object[] objs,DZFBoolean ishasjz)
			throws DZFWarpException {
		Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp, xmmcid, objs,"lrb",ishasjz);
		Map<String, List<LrbVO>> mapyearmny = new HashMap<String, List<LrbVO>>();

		/** 为了防止空值，每个月都有值 */
		mapyearmny.put(year + "-01", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-02", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-03", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-04", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-05", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-06", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-07", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-08", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-09", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-10", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-11", new ArrayList<LrbVO>());
		mapyearmny.put(year + "-12", new ArrayList<LrbVO>());

		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];

		Set<String> yearset = monthmap.keySet();
		
		Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
		for (String str : yearset) {
			List<FseJyeVO> listfs = monthmap.get(str);
			DZFDate datevalue = new DZFDate(str + "-01");
			str = str + "-" + datevalue.getDaysMonth();
			FseJyeVO[] fvos = (FseJyeVO[]) listfs.toArray(new FseJyeVO[0]);
			mp = convert(mp);
			Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
			int len = fvos == null ? 0 : fvos.length;
			for (int i = 0; i < len; i++) {
				if (fvos[i] != null) {
					map.put(fvos[i].getKmbm(), fvos[i]);
				}
			}

			LrbVO[] lrbvos = null;
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
				String zxzc = zxkjPlatformService.queryParamterValueByCode(pk_corp, "dzf025");
				/** 2007会计准则 */
				if ("财会【2019】6号".equals(zxzc)) { // 财会【2019】6号
					OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
					lrbvos = lrb_qykj.getCompanyVos(map, mp, str.substring(0, 7), pk_corp, xmmcid,singleObjectBO,"00000100AA10000000000BMF","");
				}else {
					lrbvos = getLRB2007VOs(map, mp, str.substring(0, 7), pk_corp, xmmcid);
				}
			} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
				/** 2013会计准则 */
				lrbvos = getLRB2013VOs(map, mp, str.substring(0, 7), pk_corp, xmmcid);
			} else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){//企业会计制度
				OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
				lrbvos = lrb_qykj.getCompanyVos(map, mp,str.substring(0, 7), pk_corp, xmmcid,singleObjectBO,"00000100000000Ig4yfE0005","");
			} 
//			else if(corpschema == DzfUtil.RURALCOOPERATIVE.intValue()){//农村合作社
//				OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
//				lrbvos = lrb_qykj.getCompanyVos(map, mp,str.substring(0, 7), pk_corp, xmmcid,singleObjectBO,"00000100000000Ig4yfE0006");
//
//			}
			else {
				throw new BusinessException("该制度暂不支持利润表,敬请期待!");
			}
			if (lrbvos != null && lrbvos.length > 0) {
				for (LrbVO vo : lrbvos) {
					vo.setPeriod(str);
				}
			}
			mapyearmny.put(str.substring(0, 7), Arrays.asList(lrbvos));
		}

		return mapyearmny;
	}

	@Override
	public List<LrbVO[]> getBetweenLrbMap(DZFDate begdate, DZFDate enddate, String pk_corp, String xmmcid,
			Object[] objs,DZFBoolean ishasjz) throws DZFWarpException {

		if (begdate == null || enddate == null || StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("查询参数不能为空!");
		}

		List<String> periods = ReportUtil.getPeriods(begdate, enddate);
		Object[] obj = gl_rep_fsyebserv.getEveryPeriodFsJyeVOs(begdate, enddate, pk_corp, objs,"lrb",ishasjz);

		/** 为了防止空值，每个月都有值 */
		Map<String, List<FseJyeVO>> monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];

		List<LrbVO[]> res = new ArrayList<LrbVO[]>();

		for (String str : periods) {
			List<FseJyeVO> listfs = monthmap.get(str);
			DZFDate datevalue = new DZFDate(str + "-01");
			str = str + "-" + datevalue.getDaysMonth();
			FseJyeVO[] fvos = (FseJyeVO[]) listfs.toArray(new FseJyeVO[0]);
			mp = convert(mp);
			Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
			int len = fvos == null ? 0 : fvos.length;
			for (int i = 0; i < len; i++) {
				if (fvos[i] != null) {
					map.put(fvos[i].getKmbm(), fvos[i]);
				}
			}

			LrbVO[] lrbvos = null;
			Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
				String zxzc = zxkjPlatformService.queryParamterValueByCode(pk_corp, "dzf025");
				if ("财会【2019】6号".equals(zxzc)) { // 财会【2019】6号
					OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
					lrbvos = lrb_qykj.getCompanyVos(map, mp, str.substring(0, 7), pk_corp, xmmcid,singleObjectBO,"00000100AA10000000000BMF","【2019】6号");
				}else {
					/** 2007会计准则 */
					lrbvos = getLRB2007VOs(map, mp, str.substring(0, 7), pk_corp, xmmcid);
				}
			} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
				/** 2013会计准则 */
				lrbvos = getLRB2013VOs(map, mp, str.substring(0, 7), pk_corp, xmmcid);
			} else if(corpschema == DzfUtil.COMPANYACCOUNTSYSTEM.intValue()){
				OtherSystemForLrb lrb_qykj = new OtherSystemForLrb();
				lrbvos = lrb_qykj.getCompanyVos(map, mp,  str.substring(0, 7), pk_corp, xmmcid,singleObjectBO,"00000100000000Ig4yfE0005","");
			}else {
				continue;
			}
			if (lrbvos != null && lrbvos.length > 0) {
				for (LrbVO vo : lrbvos) {
					vo.setPeriod(str);
				}
			}
			res.add(lrbvos);
		}

		return res;
	}

	@Override
	public LrbVO[] getLRBVOsByPeriod(QueryParamVO vo) throws DZFWarpException {
		String pk_corp = vo.getPk_corp();
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(pk_corp);
		/** 来源利润表 */
		vo.setRptsource("lrb");
		vo.setFirstlevelkms(rptsetser.queryLrbKmsFromDaima(cpvo.getPk_corp(),null));
		if (!StringUtil.isEmpty(vo.getXmlbid()) && !StringUtil.isEmpty(vo.getXmmcid())) {
			vo.setSfzxm(DZFBoolean.TRUE);
		}
		Object[] obj = gl_rep_fsyebserv.getFsJyeVOs1(vo);
		FseJyeVO[] fvos = (FseJyeVO[]) obj[0];
		Map<String, YntCpaccountVO> mp = (Map<String, YntCpaccountVO>) obj[1];
		mp = convert(mp);
		Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
		int len = fvos == null ? 0 : fvos.length;
		for (int i = 0; i < len; i++) {
			if (fvos[i] != null) {
				map.put(fvos[i].getKmbm(), fvos[i]);
			}
		}

		return getLrbVosByPeriod(vo, pk_corp, mp, map, vo.getXmmcid());
	}
	
	
	/**
	 * 通过发生额余额表生成利润表数据
	 * 
	 * @param vo
	 * @param pk_corp
	 * @param mp
	 * @param map
	 * @return
	 */
	private LrbVO[] getLrbVosByPeriod(QueryParamVO vo, String pk_corp, Map<String, YntCpaccountVO> mp,
			Map<String, FseJyeVO> map, String xmmcid) {

		Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);

		if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
			/** 2007会计准则 */
			return getLRB2007VOsByPeriod(map, mp, pk_corp, xmmcid);
		} else if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
			/** 2013会计准则 */
			return getLRB2013VOsByPeriod(map, mp,  pk_corp, xmmcid);
		} else {
			throw new BusinessException("该制度暂不支持利润表,敬请期待!");
		}
	}
	
	/**
	 * 2007会计准则的利润表
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private LrbVO[] getLRB2007VOsByPeriod(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String pk_corp,
			String xmmcid) throws DZFWarpException {

		LrbVO vo1 = new LrbVO();
		vo1.setXm("一、营业收入");
		// 本月金额=fs(6001,月,"贷",,年)+fs(6051,月,"贷",,年)
		vo1 = getLRBVO(map, mp, xmmcid, vo1, "6001", "6051");
		// vo1.setByje(getBQFSEByKm("6001", pk_corp, period,
		// 1).add(getBQFSEByKm("6051", pk_corp, period, 1))) ;
		// //本年累计金额=lfs(6001,月,"贷",,年)+llfs(6051,月,"贷",,年)
		// vo1.setBnljje(getBNLJFSEByKm("6001", pk_corp, period,
		// 1).add(getBNLJFSEByKm("6051", pk_corp, period, 1))) ;
		vo1.setHs("1");
		vo1.setLevel(1);
		vo1.setVconkms("6001,6051");

		LrbVO vo2 = new LrbVO();
		vo2.setXm("减：营业成本");
		// fs(6401,月,"借",,年)+fs(6402,月,"借",,年)
		vo2 = getLRBVO(map, mp,xmmcid, vo2, "6401", "6402");
		// vo2.setByje(getBQFSEByKm("6401", pk_corp, period,
		// 0).add(getBQFSEByKm("6402", pk_corp, period, 0))) ;
		// //lfs(6401,月,"借",,年)+lfs(6402,月,"借",,年)
		// vo2.setBnljje(getBNLJFSEByKm("6401", pk_corp, period,
		// 0).add(getBNLJFSEByKm("6402", pk_corp, period, 0))) ;
		vo2.setHs("2");
		vo2.setLevel(2);
		vo2.setVconkms("6401,6402");

		LrbVO vo3 = new LrbVO();
		vo3.setXm("税金及附加");
		// fs(6403,月,"借",,年)
		vo3 = getLRBVO(map, mp, xmmcid, vo3, "6403");
		// vo3.setByje(getBQFSEByKm("6403", pk_corp, period, 0)) ;
		// //lfs(6403,月,"借",,年)
		// vo3.setBnljje(getBNLJFSEByKm("6403", pk_corp, period, 0)) ;
		vo3.setHs("3");
		vo3.setLevel(3);
		vo3.setVconkms("6403");

		LrbVO vo4 = new LrbVO();
		vo4.setXm("销售费用");
		// fs(6601,月,"借",,年)
		vo4 = getLRBVO(map, mp, xmmcid, vo4, "6601");
		// vo4.setByje(getBQFSEByKm("6601", pk_corp, period, 0)) ;
		// //lfs(6601,月,"借",,年)
		// vo4.setBnljje(getBNLJFSEByKm("6601", pk_corp, period, 0)) ;
		vo4.setHs("4");
		vo4.setLevel(3);
		vo4.setVconkms("6601");

		LrbVO vo5 = new LrbVO();
		vo5.setXm("管理费用");
		// fs(6602,月,"借",,年)
		vo5 = getLRBVO(map, mp, xmmcid, vo5, "6602");
		// vo5.setByje(getBQFSEByKm("6602", pk_corp, period, 0)) ;
		// //lfs(6602,月,"借",,年)
		// vo5.setBnljje(getBNLJFSEByKm("6602", pk_corp, period, 0)) ;
		vo5.setHs("5");
		vo5.setLevel(3);
		vo5.setVconkms("6602");

		LrbVO vo6 = new LrbVO();
		vo6.setXm("财务费用");
		// fs(6603,月,"借",,年)
		vo6 = getLRBVO(map, mp, xmmcid, vo6, "6603");
		// vo6.setByje(getBQFSEByKm("6603", pk_corp, period, 0)) ;
		// //lfs(6603,月,"借",,年)
		// vo6.setBnljje(getBNLJFSEByKm("6603", pk_corp, period, 0)) ;
		vo6.setHs("6");
		vo6.setLevel(3);
		vo6.setVconkms("6603");

		LrbVO vo7 = new LrbVO();
		vo7.setXm("　资产减值损失");
		// fs(6701,月,"借",,年)
		vo7 = getLRBVO(map, mp, xmmcid, vo7, "6701");
		// vo7.setByje(getBQFSEByKm("6701", pk_corp, period, 0)) ;
		// //lfs(6701,月,"借",,年)
		// vo7.setBnljje(getBNLJFSEByKm("6701", pk_corp, period, 0)) ;
		vo7.setHs("7");
		vo7.setLevel(3);
		vo7.setVconkms("6701");

		LrbVO vo8 = new LrbVO();
		vo8.setXm("加：公允价值变动收益（损失以“-”号填列）");
		// fs(6101,月,"贷",,年)
		vo8 = getLRBVO(map, mp, xmmcid, vo8, "6101");
		// vo8.setByje(getBQFSEByKm("6101", pk_corp, period, 0)) ;
		// //lfs(6101,月,"贷",,年)
		// vo8.setBnljje(getBNLJFSEByKm("6101", pk_corp, period, 0)) ;
		vo8.setHs("8");
		vo8.setLevel(2);
		vo8.setVconkms("6101");

		LrbVO vo9 = new LrbVO();
		vo9.setXm("　　投资收益（损失以“-”号填列）");
		// fs(6111,月,"贷",,年)
		vo9 = getLRBVO(map, mp, xmmcid, vo9, "6111");
		// vo9.setByje(getBQFSEByKm("6111", pk_corp, period, 0)) ;
		// //lfs(6111,月,"贷",,年)
		// vo9.setBnljje(getBNLJFSEByKm("6111", pk_corp, period, 0)) ;
		vo9.setHs("9");
		vo9.setLevel(3);
		vo9.setVconkms("6111");

		LrbVO vo10 = new LrbVO();
		vo10.setXm("　　其中:对联营企业和合营企业的投资收益");
		vo10.setHs("10");
		vo10.setLevel(3);

		LrbVO vo11 = new LrbVO();
		vo11.setXm("二、营业利润（亏损以“-”号填列）");
		// ?C5()-?C6-?C7-?C8-?C9-?C10-?C11+?C12+?C13
		// ?D5-?D6-?D7-?D8-?D9-?D10-?D11+?D12+?D13
		vo11.setByje(VoUtils.getDZFDouble(vo1.getByje()).sub(VoUtils.getDZFDouble(vo2.getByje())).sub(VoUtils.getDZFDouble(vo3.getByje()))
				.sub(VoUtils.getDZFDouble(vo4.getByje())).sub(VoUtils.getDZFDouble(vo5.getByje())).sub(VoUtils.getDZFDouble(vo6.getByje()))
				.sub(VoUtils.getDZFDouble(vo7.getByje())).add(VoUtils.getDZFDouble(vo8.getByje())).add(VoUtils.getDZFDouble(vo9.getByje())));
		vo11.setBnljje(
				VoUtils.getDZFDouble(vo1.getBnljje()).sub(VoUtils.getDZFDouble(vo2.getBnljje())).sub(VoUtils.getDZFDouble(vo3.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo4.getBnljje())).sub(VoUtils.getDZFDouble(vo5.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo6.getBnljje())).sub(VoUtils.getDZFDouble(vo7.getBnljje()))
						.add(VoUtils.getDZFDouble(vo8.getBnljje())).add(VoUtils.getDZFDouble(vo9.getBnljje())));
		vo11.setHs("11");
		vo11.setLevel(1);

		LrbVO vo12 = new LrbVO();
		vo12.setXm("加：营业外收入");
		// fs(6301,月,"贷",,年)
		vo12 = getLRBVO(map, mp, xmmcid, vo12, "6301");
		// vo12.setByje(getBQFSEByKm("6301", pk_corp, period, 0)) ;
		// //lfs(6301,月,"贷",,年)
		// vo12.setBnljje(getBNLJFSEByKm("6301", pk_corp, period, 0)) ;
		vo12.setHs("12");
		vo12.setLevel(2);
		vo12.setVconkms("6301");

		LrbVO vo13 = new LrbVO();
		vo13.setXm("减：营业外支出");
		// fs(6711,月,"借",,年)
		vo13 = getLRBVO(map, mp, xmmcid, vo13, "6711");
		// vo13.setByje(getBQFSEByKm("6711", pk_corp, period, 0)) ;
		// //lfs(6711,月,"借",,年)
		// vo13.setBnljje(getBNLJFSEByKm("6711", pk_corp, period, 0)) ;
		vo13.setHs("13");
		vo13.setLevel(2);
		vo13.setVconkms("6711");

		LrbVO vo14 = new LrbVO();
		// 本期金额：fs(671101,月,"借",,年) 本年累计金额：lfs(671101,月,"借",,年)

		String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
		String newRuleCode = zxkjPlatformService.getNewRuleCode("671101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		vo14 = getLRBVO(map, mp, xmmcid, vo14, newRuleCode);
		// vo14.setByje(getBQFSEByKm("671101", pk_corp, period, 0)) ;
		// vo14.setBnljje(getBNLJFSEByKm("671101", pk_corp, period, 0)) ;
		vo14.setXm("　　其中：非流动资产处置损失");
		vo14.setHs("14");
		vo14.setLevel(3);
		vo14.setVconkms(newRuleCode);

		LrbVO vo15 = new LrbVO();
		vo15.setXm("三、利润总额（亏损总额以“-”号填列）");
		vo15.setByje(VoUtils.getDZFDouble(vo11.getByje()).add(VoUtils.getDZFDouble(vo12.getByje())).sub(VoUtils.getDZFDouble(vo13.getByje())));
		vo15.setBnljje(
				VoUtils.getDZFDouble(vo11.getBnljje()).add(VoUtils.getDZFDouble(vo12.getBnljje())).sub(VoUtils.getDZFDouble(vo13.getBnljje())));
		vo15.setHs("15");
		vo15.setLevel(1);

		LrbVO vo16 = new LrbVO();
		vo16.setXm("减：所得税费用");
		// fs(6801,月,"借",,年)
		vo16 = getLRBVO(map, mp, xmmcid, vo16, "6801");
		// vo16.setByje(getBQFSEByKm("6801", pk_corp, period, 0)) ;
		// //lfs(6711,月,"借",,年)
		// vo16.setBnljje(getBNLJFSEByKm("6801", pk_corp, period, 0)) ;
		vo16.setHs("16");
		vo16.setLevel(2);
		vo16.setVconkms("6801");

		LrbVO vo17 = new LrbVO();
		vo17.setXm("四、净利润（净亏损以“-”号填列）");
		vo17.setByje(VoUtils.getDZFDouble(vo15.getByje()).sub(VoUtils.getDZFDouble(vo16.getByje())));
		vo17.setBnljje(VoUtils.getDZFDouble(vo15.getBnljje()).sub(VoUtils.getDZFDouble(vo16.getBnljje())));
		vo17.setHs("17");
		vo17.setLevel(1);

		LrbVO vo18 = new LrbVO();
		vo18.setXm("五、每股收益：");
		vo18.setHs("18");
		vo18.setLevel(1);

		LrbVO vo19 = new LrbVO();
		vo19.setXm("　（一）基本每股收益");
		vo19.setHs("19");
		vo19.setLevel(2);

		LrbVO vo20 = new LrbVO();
		vo20.setXm("　（二）稀释每股收益");
		vo20.setHs("20");
		vo20.setLevel(2);

		return new LrbVO[] { vo1, vo2, vo3, vo4, vo5, vo6, vo7, vo8, vo9, vo10, vo11, vo12, vo13, vo14, vo15, vo16,
				vo17, vo18, vo19, vo20 };

	}

	/**
	 * 2013会计准则的利润表
	 * 
	 * @param period
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private LrbVO[] getLRB2013VOsByPeriod(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String pk_corp,
			String xmmcid) throws DZFWarpException {
		LrbVO vo1 = new LrbVO();
		vo1.setXm("一、营业收入");
		vo1 = getLRBVO(map, mp, xmmcid, vo1, "5001", "5051");
		vo1.setHs("1");
		vo1.setLevel(1);
		vo1.setVconkms("5001,5051");

		LrbVO vo2 = new LrbVO();
		vo2.setXm("减：营业成本");
		vo2 = getLRBVO(map, mp,  xmmcid, vo2, "5401", "5402");
		vo2.setHs("2");
		vo2.setLevel(2);
		vo2.setVconkms("5401,5402");

		LrbVO vo3 = new LrbVO();
		vo3.setXm("税金及附加");
		vo3 = getLRBVO(map, mp,  xmmcid, vo3, "5403");
		vo3.setHs("3");
		vo3.setLevel(3);
		vo3.setVconkms("5403");

		LrbVO vo4 = new LrbVO();
		vo4.setXm("其中：消费税");
		String queryAccountRule = zxkjPlatformService.queryAccountRule(pk_corp);
		String newRuleCode = zxkjPlatformService.getNewRuleCode("540301", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
		vo4 = getLRBVO(map, mp,  xmmcid, vo4, newRuleCode);
		vo4.setHs("4");
		vo4.setLevel(4);
		vo4.setVconkms(newRuleCode);

		LrbVO vo5 = new LrbVO();
		vo5.setXm("　　　营业税");
		String newRuleCode1 = zxkjPlatformService.getNewRuleCode("540302", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo5 = getLRBVO(map, mp, xmmcid, vo5, newRuleCode1);
		vo5.setHs("5");
		vo5.setLevel(4);
		vo5.setVconkms(newRuleCode1);

		LrbVO vo6 = new LrbVO();
		vo6.setXm("　　　城市维护建设税");
		String newRuleCode2 = zxkjPlatformService.getNewRuleCode("540303", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo6 = getLRBVO(map, mp,  xmmcid, vo6, newRuleCode2);
		vo6.setHs("6");
		vo6.setLevel(4);
		vo6.setVconkms(newRuleCode2);

		LrbVO vo7 = new LrbVO();
		vo7.setXm("　　　资源税");
		String newRuleCode3 = zxkjPlatformService.getNewRuleCode("540304", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo7 = getLRBVO(map, mp, xmmcid, vo7, newRuleCode3);
		vo7.setHs("7");
		vo7.setLevel(4);
		vo7.setVconkms(newRuleCode3);

		LrbVO vo8 = new LrbVO();
		vo8.setXm("　　　土地增值税");
		String newRuleCode4 = zxkjPlatformService.getNewRuleCode("540305", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo8 = getLRBVO(map, mp,  xmmcid, vo8, newRuleCode4);
		vo8.setHs("8");
		vo8.setLevel(4);
		vo8.setVconkms(newRuleCode4);

		LrbVO vo9 = new LrbVO();
		vo9.setXm("　　　城镇土地使用税、房产税、车船税、印花税");
		String newRuleCode5 = zxkjPlatformService.getNewRuleCode("540306", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo9 = getLRBVO(map, mp,  xmmcid, vo9, newRuleCode5);
		vo9.setHs("9");
		vo9.setLevel(4);
		vo9.setVconkms(newRuleCode5);

		LrbVO vo10 = new LrbVO();
		vo10.setXm("　　　教育费附加、矿产资源补偿费、排污费");
		String newRuleCode6 = zxkjPlatformService.getNewRuleCode("540307", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo10 = getLRBVO(map, mp,  xmmcid, vo10, newRuleCode6);
		vo10.setHs("10");
		vo10.setLevel(4);
		vo10.setVconkms(newRuleCode6);

		LrbVO vo11 = new LrbVO();
		vo11.setXm("销售费用");
		vo11 = getLRBVO(map, mp,  xmmcid, vo11, "5601");
		vo11.setHs("11");
		vo11.setLevel(3);
		vo11.setVconkms("5601");

		LrbVO vo12 = new LrbVO();
		vo12.setXm("其中：商品维修费");
		String newRuleCode7 = zxkjPlatformService.getNewRuleCode("560116", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo12 = getLRBVO(map, mp, xmmcid, vo12, newRuleCode7);
		vo12.setHs("12");
		vo12.setLevel(4);
		vo12.setVconkms(newRuleCode7);

		LrbVO vo13 = new LrbVO();
		vo13.setXm("　　　广告费和业务宣传费");
		String newRuleCode8 = zxkjPlatformService.getNewRuleCode("560105", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo13 = getLRBVO(map, mp, xmmcid, vo13, newRuleCode8);
		vo13.setHs("13");
		vo13.setLevel(4);
		vo13.setVconkms(newRuleCode8);

		LrbVO vo14 = new LrbVO();
		vo14.setXm("管理费用");
		vo14 = getLRBVO(map, mp,  xmmcid, vo14, "5602");
		vo14.setHs("14");
		vo14.setLevel(3);
		vo14.setVconkms("5602");

		LrbVO vo15 = new LrbVO();
		vo15.setXm("其中：开办费");
		String newRuleCode9 = zxkjPlatformService.getNewRuleCode("560208", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo15 = getLRBVO(map, mp,  xmmcid, vo15, newRuleCode9);
		vo15.setHs("15");
		vo15.setLevel(4);
		vo15.setVconkms(newRuleCode9);

		LrbVO vo16 = new LrbVO();
		vo16.setXm("　　　业务招待费");
		String newRuleCode10 = zxkjPlatformService.getNewRuleCode("560204", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo16 = getLRBVO(map, mp,  xmmcid, vo16, newRuleCode10);
		vo16.setHs("16");
		vo16.setLevel(5);
		vo16.setVconkms(newRuleCode10);

		LrbVO vo17 = new LrbVO();
		vo17.setXm("　　　研究费用");
		String newRuleCode11 = zxkjPlatformService.getNewRuleCode("560220", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo17 = getLRBVO(map, mp,  xmmcid, vo17, newRuleCode11);
		vo17.setHs("17");
		vo17.setLevel(5);
		vo17.setVconkms(newRuleCode11);

		LrbVO vo18 = new LrbVO();
		vo18.setXm("财务费用");
		vo18 = getLRBVO(map, mp,  xmmcid, vo18, "5603");
		vo18.setHs("18");
		vo18.setLevel(3);
		vo18.setVconkms("5603");

		LrbVO vo19 = new LrbVO();
		String newRuleCode12 = zxkjPlatformService.getNewRuleCode("560301", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo19 = getLRBVO(map, mp,  xmmcid, vo19, newRuleCode12);
		vo19.setXm("其中：利息费用（收入以“-”号填列）");
		vo19.setHs("19");
		vo19.setLevel(4);
		vo19.setVconkms(newRuleCode12);

		LrbVO vo20 = new LrbVO();
		vo20.setXm("加：投资收益（损失以“-”号填列）");
		vo20 = getLRBVO(map, mp,  xmmcid, vo20, "5111");
		vo20.setHs("20");
		vo20.setLevel(2);
		vo20.setVconkms("5111");

		LrbVO vo21 = new LrbVO();
		vo21.setXm("二、营业利润（亏损以“-”号填列） ");
		// ?D5-?D6-?D7-?D15-?D18-?D22-?D22+?D24
		vo21.setByje(VoUtils.getDZFDouble(vo1.getByje()).sub(VoUtils.getDZFDouble(vo2.getByje())).sub(VoUtils.getDZFDouble(vo3.getByje()))
				.sub(VoUtils.getDZFDouble(vo11.getByje())).sub(VoUtils.getDZFDouble(vo14.getByje())).sub(VoUtils.getDZFDouble(vo18.getByje()))
				.add(VoUtils.getDZFDouble(vo20.getByje())));
		vo21.setBnljje(
				VoUtils.getDZFDouble(vo1.getBnljje()).sub(VoUtils.getDZFDouble(vo2.getBnljje())).sub(VoUtils.getDZFDouble(vo3.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo11.getBnljje())).sub(VoUtils.getDZFDouble(vo14.getBnljje()))
						.sub(VoUtils.getDZFDouble(vo18.getBnljje())).add(VoUtils.getDZFDouble(vo20.getBnljje())));
		vo21.setHs("21");
		vo21.setLevel(1);

		LrbVO vo22 = new LrbVO();
		vo22.setXm("加：营业外收入");
		vo22 = getLRBVO(map, mp,  xmmcid, vo22, "5301");
		vo22.setHs("22");
		vo22.setLevel(1);
		vo22.setVconkms("5301");

		LrbVO vo23 = new LrbVO();
		vo23.setXm("其中：政府补助");
		String newRuleCode13 = zxkjPlatformService.getNewRuleCode("530104", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo23 = getLRBVO(map, mp,  xmmcid, vo23, newRuleCode13);
		vo23.setHs("23");
		vo23.setLevel(4);
		vo23.setVconkms(newRuleCode13);

		LrbVO vo24 = new LrbVO();
		vo24.setXm("减：营业外支出");
		vo24 = getLRBVO(map, mp,  xmmcid, vo24, "5711");
		vo24.setHs("24");
		vo24.setLevel(1);
		vo24.setVconkms("5711");

		LrbVO vo25 = new LrbVO();
		vo25.setXm("其中：坏账损失");
		String newRuleCode14 = zxkjPlatformService.getNewRuleCode("571108", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo25 = getLRBVO(map, mp, xmmcid, vo25, newRuleCode14);
		vo25.setHs("25");
		vo25.setLevel(4);
		vo25.setVconkms(newRuleCode14);

		LrbVO vo26 = new LrbVO();
		vo26.setXm("　　　无法收回的长期债券投资损失");
		String newRuleCode15 = zxkjPlatformService.getNewRuleCode("571109", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo26 = getLRBVO(map, mp,  xmmcid, vo26, newRuleCode15);
		vo26.setHs("26");
		vo26.setLevel(5);
		vo26.setVconkms(newRuleCode15);

		LrbVO vo27 = new LrbVO();
		vo27.setXm("　　　无法收回的长期股权投资损失");
		String newRuleCode16 = zxkjPlatformService.getNewRuleCode("571110", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo27 = getLRBVO(map, mp,  xmmcid, vo27, newRuleCode16);
		vo27.setHs("27");
		vo27.setLevel(5);
		vo27.setVconkms(newRuleCode16);

		LrbVO vo28 = new LrbVO();
		vo28.setXm("　　　自然灾害等不可抗力因素造成的损失");
		String newRuleCode17 = zxkjPlatformService.getNewRuleCode("571111", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo28 = getLRBVO(map, mp,  xmmcid, vo28, newRuleCode17);
		vo28.setHs("28");
		vo28.setLevel(5);
		vo28.setVconkms(newRuleCode17);

		LrbVO vo29 = new LrbVO();
		vo29.setXm("　　　税收滞纳金");
		String newRuleCode18 = zxkjPlatformService.getNewRuleCode("571112", DZFConstant.ACCOUNTCODERULE,
				queryAccountRule);
		vo29 = getLRBVO(map, mp,  xmmcid, vo29, newRuleCode18);
		vo29.setHs("29");
		vo29.setLevel(5);
		vo29.setVconkms(newRuleCode18);

		LrbVO vo30 = new LrbVO();
		vo30.setXm("三、利润总额（亏损总额以“-”号填列）");
		vo30.setByje(VoUtils.getDZFDouble(vo21.getByje()).add(VoUtils.getDZFDouble(vo22.getByje())).sub(VoUtils.getDZFDouble(vo24.getByje())));
		vo30.setBnljje(
				VoUtils.getDZFDouble(vo21.getBnljje()).add(VoUtils.getDZFDouble(vo22.getBnljje())).sub(VoUtils.getDZFDouble(vo24.getBnljje())));
		vo30.setHs("30");
		vo30.setLevel(1);

		LrbVO vo31 = new LrbVO();
		vo31.setXm("减：所得税费用");
		vo31 = getLRBVO(map, mp,  xmmcid, vo31, "5801");
		vo31.setHs("31");
		vo31.setLevel(1);
		vo31.setVconkms("5801");

		LrbVO vo32 = new LrbVO();
		vo32.setXm("四、净利润（净亏损以“-”号填列）");
		vo32.setByje(VoUtils.getDZFDouble(vo30.getByje()).sub(VoUtils.getDZFDouble(vo31.getByje())));
		vo32.setBnljje(VoUtils.getDZFDouble(vo30.getBnljje()).sub(VoUtils.getDZFDouble(vo31.getBnljje())));
		vo32.setHs("32");
		vo32.setLevel(1);

		return new LrbVO[] { vo1, vo2, vo3, vo4, vo5, vo6, vo7, vo8, vo9, vo10, vo11, vo12, vo13, vo14, vo15, vo16,
				vo17, vo18, vo19, vo20, vo21, vo22, vo23, vo24, vo25, vo26, vo27, vo28, vo29, vo30, vo31, vo32 };

	}


	private LrbVO getLRBVO(Map<String, FseJyeVO> map, Map<String, YntCpaccountVO> mp, String xmmcid,
			LrbVO vo, String... kms) {

		// FSEJYEVO fvo=null;

		DZFDouble ufd = null;
		int direction = 0;
		int len = kms == null ? 0 : kms.length;
		YntCpaccountVO km = null;
		List<FseJyeVO> ls = null;
		for (int i = 0; i < len; i++) {
			km = mp.get(kms[i]);
			if (km == null)
				continue;
			direction = km.getDirection();
			ls = getData(map, kms[i], mp, xmmcid);
			for (FseJyeVO fvo : ls) {

				if (direction == 0) {
					ufd = VoUtils.getDZFDouble(vo.getByje());
					vo.setByje(ufd.add(VoUtils.getDZFDouble(fvo.getJftotal())));
				} else {
					ufd = VoUtils.getDZFDouble(vo.getByje());
					vo.setByje(ufd.add(VoUtils.getDZFDouble(fvo.getDftotal())));
				}
			}
		}
		return vo;
	}

	@Override
	public LrbVO[] getLrbDataForCwBs(String qj, String corpIds, String qjlx) throws DZFWarpException {
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(corpIds);
		QueryParamVO paramVO = new QueryParamVO();
		LrbVO[] lrbvos  =null;
		LrbVO[] bef_lrbvos  =null;
		paramVO.setRptsource("lrb");//来源利润表
		paramVO.setQjq(qj.substring(0, 7));
		paramVO.setQjz(qj.substring(0, 7));
		paramVO.setEnddate(DateUtils.getPeriodEndDate(qj.substring(0, 7)));
		paramVO.setBegindate1(DateUtils.getPeriodStartDate(qj.substring(0, 7)));
		paramVO.setIshasjz(DZFBoolean.FALSE);
		paramVO.setPk_corp(corpIds);
		if("0".equals(qjlx)){//月份
//			ILrbReport gl_rep_lrbserv = (ILrbReport) SpringUtils.getBean("gl_rep_lrbserv");
//			lrbvos = gl_rep_lrbserv.getLRBVOs(paramVO);
			lrbvos =getLRBVOs(paramVO);
			/** 如果是一般企业则取上年数据 */
			Integer corpschema = zxkjPlatformService.getAccountSchema(corpIds);
			/** 07科目体系(一般企业) */
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
				/** 查询上年数据 */
				String bef_period = DateUtils.getPreviousYearPeriod(qj.substring(0, 7));
				/** 公司建账日期在查询日期后时 */
				if(DateUtils.getPeriod(cpvo.getBegindate()).compareTo(bef_period)<=0){
					paramVO.setQjq(bef_period);
					paramVO.setQjz(bef_period);
					paramVO.setEnddate(DateUtils.getPeriodEndDate(bef_period));
					paramVO.setBegindate1(DateUtils.getPeriodStartDate(bef_period));
					bef_lrbvos = getLRBVOs(paramVO);
					for(LrbVO lrbvo:lrbvos){
						for(LrbVO lastyearlrbvo:bef_lrbvos){
							if(!StringUtil.isEmpty(lrbvo.getXm())
									&& lrbvo.getXm().equals(lastyearlrbvo.getXm())){
								lrbvo.setLastyear_bnljje(lastyearlrbvo.getBnljje());
								break;
							}
						}
					}
				}
			}
		}else if("1".equals(qjlx)){/** 季度 */
			ILrbQuarterlyReport gl_rep_lrbquarterlyserv = (ILrbQuarterlyReport) SpringUtils.getBean("gl_rep_lrbquarterlyserv");
			LrbquarterlyVO[] lrbjb = gl_rep_lrbquarterlyserv.getLRBquarterlyVOs(paramVO);
			lrbvos = convertLrb(lrbjb, qj);
		}
		return lrbvos;
	}
	
	private LrbVO[] convertLrb(LrbquarterlyVO[] listVo,String qj) {
		if(listVo == null || listVo.length ==0){
			throw new BusinessException("利润表季报数据为空");
		}
		
		String month = qj.substring(5, 7);
		
		List<LrbVO> reslist = new ArrayList<LrbVO>();
		
		LrbVO tlrbvo = null;
		for (LrbquarterlyVO quaryvo : listVo) {
			tlrbvo = new LrbVO();

			tlrbvo.setXm(quaryvo.getXm());
			
			tlrbvo.setHs(quaryvo.getHs());

			tlrbvo.setBnljje(quaryvo.getBnlj());
			
			if("03".equals(month)){
				tlrbvo.setByje(quaryvo.getQuarterFirst());
				tlrbvo.setLastyear_bnljje(quaryvo.getLastquarterFirst());
			}else if("06".equals(month)){
				tlrbvo.setByje(quaryvo.getQuarterSecond());
				tlrbvo.setLastyear_bnljje(quaryvo.getLastquarterSecond());
			}else if("09".equals(month)){
				tlrbvo.setByje(quaryvo.getQuarterThird());
				tlrbvo.setLastyear_bnljje(quaryvo.getLastquarterThird());
			}else{
				tlrbvo.setByje(quaryvo.getQuarterFourth());
				tlrbvo.setLastyear_bnljje(quaryvo.getLastquarterFourth());
			}
			reslist.add(tlrbvo);
		}
		
		return reslist.toArray(new LrbVO[0]);
	}

	@Override
	public LrbVO[] getLrbVosFromFs(QueryParamVO paramVO,Map<String, YntCpaccountVO> mp,String pk_corp ,FseJyeVO[] fvos) throws DZFWarpException {
		Map<String, FseJyeVO> map = new HashMap<String, FseJyeVO>();
		int len = fvos == null ? 0 : fvos.length;
		for (int i = 0; i < len; i++) {
			if (fvos[i] != null) {
				map.put(fvos[i].getKmbm(), fvos[i]);
			}
		}
		return getLrbVos(paramVO, pk_corp, mp, map, "");
	}


}

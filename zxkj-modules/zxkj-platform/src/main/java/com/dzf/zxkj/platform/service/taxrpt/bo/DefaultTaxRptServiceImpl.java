package com.dzf.zxkj.platform.service.taxrpt.bo;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.constant.TaxRptConstPub;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.*;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10101_beijing;
import com.dzf.zxkj.platform.model.tax.chk.TaxRptChk10102_beijing;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxBalaceCcrService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptService;
import com.dzf.zxkj.platform.service.taxrpt.spreadjs.SpreadTool;
import com.dzf.zxkj.platform.util.taxrpt.TaxReportPath;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//全国通用
@Service("taxRptservice_default")
@Slf4j
public class DefaultTaxRptServiceImpl implements ITaxRptService {

	@Autowired
	protected SingleObjectBO singleObjectBO;
	@Autowired
	protected ICorpTaxService corpTaxService;
	@Autowired
	protected IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private IAccountService accountService;
	
	@Override
	public List<TaxReportVO> getTypeList(CorpVO corpvo, CorpTaxVo corptaxvo, String period,
										 String userid, String operatedate, SingleObjectBO sbo)
					throws DZFWarpException {
		List<TaxReportVO> list1 = null;
		//取当前期间，以这里的为准，不以参数为准
//		period = DateUtils.getPeriod(new DZFDate());
		DZFDate operdate =  new DZFDate(operatedate);
		String requestid = UUID.randomUUID().toString();
		try{
//			LockUtil.getInstance().tryLockKey(corpvo.getPk_corp(), "typelist",requestid, 30);//锁定30秒，前提必须启用redis.否则这里报错
			String pk_corp = corpvo.getPk_corp();
			//获取上一期间
			period = DateUtils.getPreviousPeriod(period);
			//查询当前公司所选的要申报的报表
			List<String> list = queryCorpTaxRptVO(corpvo, period);
			if(list == null || list.size() == 0){
				//自动生成当前公司默认的申报报表信息
				//这个方法可以用。不过还是让他们先维护数据信息算了
//				corpTaxService.saveInitCorpRptVOs(corpvo);
//				list = queryCorpTaxRptVO(pk_corp);
				throw new BusinessException("当前公司没有维护纳税信息，请到纳税信息节点进行维护");
//				throw new BusinessException("当前公司没有维护纳税信息，请到账套设置节点进行维护");
			}
			if(list == null || list.size() == 0)
				return null;
			//查询当前公司当期期间要申报上期的报表
			list1 = querytypeDetails(pk_corp,period);
			buildTaxDetailVO(userid,operdate, corpvo,corptaxvo, period,list,list1);
			//数据过滤，返回最终结果
			list1 = filterDetailVOs(pk_corp,period);
		}catch(Exception e){
			log.error("错误",e);
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else{
				throw new WiseRunException(e);
			}
		}finally{
//			LockUtil.getInstance().unLock_Key(corpvo.getPk_corp(), "typelist",requestid);
		}
		return list1;
	}
	/**
	 * 数据过滤年报
	 * @param pk_corp 当前公司
	 * @param period 当前期间的取上一期间
	 */
	private List<TaxReportVO> filterDetailVOs(String pk_corp,String period){
		List<TaxReportVO> list1 = querytypeDetails(pk_corp,period);
		if(list1 == null || list1.size() == 0)
			return null;
		List<TaxReportDetailVO> list2 = queryTaxDetails(list1);
		Map<String,List<TaxReportDetailVO>> map = DZfcommonTools.hashlizeObject(list2, new String[]{"pk_taxreport"});
		List<TaxReportVO> list3 = new ArrayList<TaxReportVO>();
		DZFDate date = new DZFDate();
		int year = date.getYear();
		DZFDate five31 = new DZFDate(String.valueOf(year)+"-05-31");
		for(TaxReportVO vo : list1){
			List<TaxReportDetailVO> clist = map.get(vo.getPk_taxreport());
			if(clist!=null && clist.size() >0){
				vo.setChildren(clist.toArray(new TaxReportDetailVO[0]));
			}
			//特殊考虑(长沙市润浩企业咨询服务有限公司)注销，提前年报申报。,,,zpm 注掉。2018.11.13 
//			if(vo.getPeriodtype() == PeriodType.yearreport 
//					&& "003WDH".equals(pk_corp)){
//				list3.add(vo);
//				continue;
//			}
			//特殊情况考虑--
			if(vo.getPeriodtype() == PeriodType.yearreport
					&& date.compareTo(five31)>0){//年报
				continue;
			}else if(TaxRptConst.SB_ZLBHGS.equals(vo.getSb_zlbh())){//个税
				continue;
			}else{
				list3.add(vo);
			}
		}
		return list3;
	}
	/**
	 * @param userid
	 * @param period 当前期间的取上一期间
	 */
	private void buildTaxDetailVO(String userid,DZFDate operdate,
			CorpVO corpvo,CorpTaxVo corptaxvo,String period,List<String> list,List<TaxReportVO> list1){
		if(list == null || list.size() == 0)
			return;
		Map<String,TaxReportVO> map = null;
		if(list1 != null && list1.size() > 0){
			map = DZfcommonTools.hashlizeObjectByPk(list1, new String[]{"pk_taxsbzl"});
		}
		List<String> addliststr = new ArrayList<String>();
		List<TaxReportVO> editlistsrt = null;
		List<TaxReportVO> adddetails = null;
		List<TaxReportVO> deldetails = null;
		TaxReportVO protvo = null;
		if(map != null&& map.size() >0){
			for(String key : list){
				if(!map.containsKey(key)){
					addliststr.add(key);
				}else{
					protvo = map.get(key);
					if(protvo != null 
						&& StringUtil.isEmpty(protvo.getSpreadfile())//没有填写
						&& protvo.getSbzt_dm()!=null //申报状态为未提交
						&& Integer.valueOf(protvo.getSbzt_dm()).intValue() == TaxRptConst.iSBZT_DM_UnSubmit){
						if(editlistsrt == null)
							editlistsrt = new ArrayList<TaxReportVO>();
						editlistsrt.add(protvo);
					}
					map.remove(key);
				}
			}
		}else{
			addliststr = list;
		}
		if(addliststr != null && addliststr.size() > 0){
			adddetails = buildaddDetailVOs(userid,operdate,corpvo,corptaxvo,period,addliststr);
		}
		//删除没有状态，即未填写的税种
		if(map!=null && map.size()>0){
			for(TaxReportVO dtvo : map.values()){
				//申报状态非 [未提交]状态
				if(dtvo.getSbzt_dm()!=null 
						&& Integer.valueOf(dtvo.getSbzt_dm()).intValue() != TaxRptConst.iSBZT_DM_UnSubmit)
					continue;
				//未填写状态
				if(StringUtil.isEmpty(dtvo.getSpreadfile())){
					if(deldetails == null)
						deldetails = new ArrayList<TaxReportVO>();
					deldetails.add(dtvo);
				}
			}
		}
		//保存申报的税种
		saveListDetailVOs(corpvo,corptaxvo,adddetails,deldetails,editlistsrt,period);
	}
	/**
	 * 查询申报种类信息
	 * @return
	 */
	private Map<String, TaxTypeSBZLVO> queryTypeSBZLVO(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		TaxTypeSBZLVO[] vos = (TaxTypeSBZLVO[])singleObjectBO.queryByCondition(TaxTypeSBZLVO.class, " nvl(dr,0) =0 and pk_corp = ? ", sp);
		if(vos == null || vos.length == 0)
			return null;
		List<TaxTypeSBZLVO> ltax = new ArrayList<TaxTypeSBZLVO>(Arrays.asList(vos));
		Map<String,TaxTypeSBZLVO> map = DZfcommonTools.hashlizeObjectByPk(ltax, new String[]{"pk_taxsbzl"});
		return map;
	}
	
	protected List<TaxTypeSBZLVO> queryTypeSBZLVOs(){
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		TaxTypeSBZLVO[] vos = (TaxTypeSBZLVO[])singleObjectBO.queryByCondition(TaxTypeSBZLVO.class, " nvl(dr,0) =0 and pk_corp = ? ", sp);
		if(vos == null || vos.length == 0)
			return null;
		List<TaxTypeSBZLVO> ltax = new ArrayList<TaxTypeSBZLVO>(Arrays.asList(vos));
		return ltax;
	}
	/**
	 * @param userid
	 * @param period 当前期间的取上一期间
	 */
	private List<TaxReportVO> buildaddDetailVOs(String userid,DZFDate doperdate,
			CorpVO corpvo,CorpTaxVo corptaxvo,String period,List<String> addliststr){
		if(addliststr == null || addliststr.size() ==0){
			return null;
		}
		Map<String,TaxTypeSBZLVO> map = queryTypeSBZLVO();
		//生成当期需要申报税种的表数据
		List<TaxReportVO> list = new ArrayList<TaxReportVO>();
		for(String key : addliststr){
			TaxReportVO vo = new TaxReportVO();
			vo.setPk_corp(corpvo.getPk_corp());
			//赋值
			boolean falg = setDefaultValue(vo,key,period,map);
			if(!falg)
				continue;
			vo.setPk_taxsbzl(key);
			vo.setDoperatedate(doperdate);
			vo.setCoperatorid(userid);
			vo.setVbillstatus(TaxRptConst.IBILLSTATUS_UNAPPROVE);
			vo.setSbzt_dm(String.valueOf(TaxRptConst.iSBZT_DM_UnSubmit));
			vo.setDr(0);
			checkCb(vo, corpvo,corptaxvo);
			list.add(vo);
		}
		return list;
	}
	
	// 财报校验
	private void checkCb(TaxReportVO reportvo, CorpVO corpvo,CorpTaxVo corptaxvo) {
		if (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 11) {
			String corpType = null;
			String realType = null;
			String selected = null;
			String shouldSelect = null;
			if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {
				corpType = "小企业会计准则";
				selected = "小企业财报";
			} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {
				corpType = "企业会计准则";
				selected = "一般企业财报";
			} else if (TaxRptConst.KJQJ_QYKJZD.equals(corpvo.getCorptype())) {
				corpType = "企业会计制度";
				selected = "企业会计制度财报";
			}
			if (TaxRptConst.SB_ZLBHC1.equals(reportvo.getSb_zlbh())) {
				realType = "小企业会计准则";
				shouldSelect = "小企业财报";
			} else if (TaxRptConst.SB_ZLBHC2.equals(reportvo.getSb_zlbh())) {
				realType = "企业会计准则";
				shouldSelect = "一般企业财报";
			} else if (TaxRptConst.SB_ZLBH29805.equals(reportvo.getSb_zlbh())) {
				realType = "企业会计制度";
				shouldSelect = "企业会计制度财报";
			}
			if (corpType != null && realType != null
					&& !corpType.equals(realType)) {
				StringBuilder remark = new StringBuilder();
				remark.append("该公司在税局备案的会计制度是").append(realType)
				.append("，应该申报的财务报表是").append(shouldSelect)
				.append("；您建账时选择的会计制度是").append(corpType)
				.append("，对应的财报是").append(selected)
				.append("；所以无法实现申报；请去管理端建账节点修改会计制度");
				reportvo.setRemark(remark.toString());
			}
		}
	}
	
	/**
	 * @param period 当前期间的取上一期间
	 */
	private boolean setDefaultValue(TaxReportVO vo,String sbzlpk,String period,Map<String,TaxTypeSBZLVO> map){
		boolean falg = false;
		if(StringUtil.isEmpty(sbzlpk) || vo == null || map == null)
			return falg;
		TaxTypeSBZLVO sbvo = map.get(sbzlpk);
		if(sbvo == null){
			return falg;
		}
		int sbzq = sbvo.getSbzq();
		if(sbzq == PeriodType.monthreport){//月
			vo.setSb_zlbh(sbvo.getSbcode());
			vo.setZsxm_dm(sbvo.getZsxmcode());
			vo.setPeriod(period);
			vo.setPeriodtype(sbzq);
			vo.setPeriodfrom(period+"-01");
			vo.setPeriodto(DateUtils.getPeriodEndDate(period).toString());
			falg = true;
		}else if(sbzq == PeriodType.jidureport){//季
			String month = period.substring(5,7);
			if("03".equals(month)
					|| "06".equals(month)
					|| "09".equals(month)
					|| "12".equals(month)){
				vo.setSb_zlbh(sbvo.getSbcode());
				vo.setZsxm_dm(sbvo.getZsxmcode());
				vo.setPeriod(period);
				vo.setPeriodtype(sbzq);
				String startdate = period+"-01";
				long date = DateUtils.getPreviousMonth(DateUtils.getPreviousMonth(new DZFDate(startdate).getMillis()));
				vo.setPeriodfrom(new DZFDate(date).toString());
				vo.setPeriodto(DateUtils.getPeriodEndDate(period).toString());
				falg = true;
			}
		}else if(sbzq == PeriodType.yearreport){//年
			//**注:如果系统时间为2019-01-01，传入该方法的period为2018-12，会有问题，需要将period再后调一个月
			String after = DateUtils.getNextPeriod(period);
			vo.setSb_zlbh(sbvo.getSbcode());
			vo.setZsxm_dm(sbvo.getZsxmcode());
			//取上一年
			String year = String.valueOf(Integer.valueOf(after.substring(0, 4))-1);
			vo.setPeriod(year);
			vo.setPeriodtype(sbzq);
			vo.setPeriodfrom(year+"-01-01");
			vo.setPeriodto(year+"-12-31");
			falg = true;
		}
		return falg;
	}
	/**
	 * 保存数据
	 * @param corpvo
	 * @param adddetails
	 * @param deldetails
	 */
	private void saveListDetailVOs(CorpVO corpvo,CorpTaxVo corptaxvo,List<TaxReportVO> adddetails,List<TaxReportVO> deldetails,List<TaxReportVO> editlistsrt,String period){
		//新增申报的税种
		if(adddetails!=null && adddetails.size()>0){
			//新增主表
			singleObjectBO.insertVOArr(corpvo.getPk_corp(), adddetails.toArray(new TaxReportVO[0]));
			//新增子表
			List<TaxReportDetailVO> list = queryReportDetailVOs(corpvo,adddetails,period);
			if(list!=null&&list.size()>0){
				singleObjectBO.insertVOArr(corpvo.getPk_corp(), list.toArray(new TaxReportDetailVO[0]));
			}
		}
		//删除申报的税种
		if(deldetails!=null && deldetails.size()>0){
			//删除子表
			List<TaxReportDetailVO> bodyvos = queryTaxDetails(deldetails);
			if(bodyvos != null && bodyvos.size() >0){
				singleObjectBO.deleteVOArray(bodyvos.toArray(new TaxReportDetailVO[0]));
			}
			//删除主表
			singleObjectBO.deleteVOArray(deldetails.toArray(new TaxReportVO[0]));
		}
		if (corptaxvo.getTax_area() != null && corptaxvo.getTax_area() == 11) {
			// 江苏走出表规则的，暂不走勾选表
			int size = editlistsrt == null ? 0 : editlistsrt.size();
			for (int i = size - 1; i >= 0; i--) {
				TaxReportVO vo = editlistsrt.get(i);
				if (TaxRptConst.SB_ZLBH10101.equals(vo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH10102.equals(vo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH1010201.equals(vo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH10412.equals(vo.getSb_zlbh())
						|| TaxRptConst.SB_ZLBH10413.equals(vo.getSb_zlbh())) {
					editlistsrt.remove(i);
				}
			}
		}
		//修改的申报税种
		if(editlistsrt != null && editlistsrt.size()>0){
			//查询子表数据
			List<TaxReportDetailVO> bodyvos = queryTaxDetails(editlistsrt);
			Map<String,List<TaxReportDetailVO>> map1 = DZfcommonTools.hashlizeObject(bodyvos, new String[]{"pk_taxreport"});
			//查询申报
			List<TaxRptTempletVO> rptlist = queryRptTempletVOs(corpvo.getPk_corp(),period);
			Map<String,List<TaxRptTempletVO>> map2 = DZfcommonTools.hashlizeObject(rptlist, new String[]{"pk_taxsbzl"});
			String pk_taxreport = null;
			String pk_taxsbzl = null;
			List<TaxReportDetailVO> dellist = new ArrayList<TaxReportDetailVO>();
			List<TaxReportDetailVO> addlist = new ArrayList<TaxReportDetailVO>();
			for(TaxReportVO evo : editlistsrt){
				pk_taxreport = evo.getPk_taxreport();
				pk_taxsbzl = evo.getPk_taxsbzl();
				List<TaxReportDetailVO> list1 = map1.get(pk_taxreport);
				List<TaxRptTempletVO> list2 = map2.get(pk_taxsbzl);
				doBodyDatacompare(evo,list1,list2,dellist,addlist);
			}
			//
			if(dellist!=null && dellist.size()>0){
				singleObjectBO.deleteVOArray(dellist.toArray(new TaxReportDetailVO[0]));
			}
			if(addlist!=null && addlist.size()>0){
				singleObjectBO.insertVOArr(corpvo.getPk_corp(), addlist.toArray(new TaxReportDetailVO[0]));
			}
		}
	}
	
	private void doBodyDatacompare(TaxReportVO headvo,List<TaxReportDetailVO> list1,List<TaxRptTempletVO> list2,List<TaxReportDetailVO> dellist,List<TaxReportDetailVO> addlist){
		if(list2 == null || list2.size() ==0)
			return;
		Map<String,TaxRptTempletVO> map = DZfcommonTools.hashlizeObjectByPk(list2, new String[]{"pk_taxrpttemplet"});
		List<TaxRptTempletVO> list3 = null;
		if(list1!=null && list1.size() > 0){
			String pk_taxrpttemplet = null;
			for(TaxReportDetailVO vo : list1){
				pk_taxrpttemplet = vo.getPk_taxrpttemplet();
				if(map.containsKey(pk_taxrpttemplet)){
					map.remove(pk_taxrpttemplet);
				}else{
					dellist.add(vo);
				}
			}
			if(map.size() >0){
				list3 = new ArrayList<TaxRptTempletVO>(map.values());
			}
		}else{
			list3 = list2;
		}
		//新增
		if(list3!=null && list3.size()>0){
			List<TaxReportDetailVO> list4 = buildTaxReportDetailVO(headvo,list3);
			addlist.addAll(list4);
		}
	}
	
	protected List<TaxReportDetailVO> queryReportDetailVOs(CorpVO corpvo, List<TaxReportVO> rptlist,String period){
		List<TaxRptTempletVO> list = queryRptTempletVOs(corpvo.getPk_corp(),period);
		if(list == null || list.size() == 0)
			return null;
		Map<String,List<TaxRptTempletVO>> map = DZfcommonTools.hashlizeObject(list, new String[]{"pk_taxsbzl"});
		List<TaxReportDetailVO> list2 = new ArrayList<TaxReportDetailVO>();
		for(TaxReportVO vo : rptlist){
			List<TaxRptTempletVO> list1 = map.get(vo.getPk_taxsbzl());
			List<TaxReportDetailVO> list3 = buildTaxReportDetailVO(vo,list1);
			if(list3!=null && list3.size()>0)
				list2.addAll(list3);
		}
		return list2;
	}
	
	protected List<TaxReportDetailVO> buildTaxReportDetailVO(TaxReportVO vo,List<TaxRptTempletVO> list1){
		if(vo == null || list1 == null || list1.size() == 0)
			return null;
		List<TaxReportDetailVO> z1 = new ArrayList<TaxReportDetailVO>();
		for(TaxRptTempletVO v : list1){
			TaxReportDetailVO dvo = new TaxReportDetailVO();
			dvo.setOrderno(v.getOrderno());
			dvo.setPk_taxreport(vo.getPk_taxreport());
			dvo.setPk_taxrpttemplet(v.getPk_taxrpttemplet());
			dvo.setSb_zlbh(vo.getSb_zlbh());
			dvo.setReportcode(v.getReportcode());
			dvo.setReportname(v.getReportname());
			dvo.setSbzt_dm(vo.getSbzt_dm());
			dvo.setPk_corp(vo.getPk_corp());
			dvo.setDr(0);
			z1.add(dvo);
		}
		return z1;
	}
	
	/**
	 * 查询当期填报清单
	 * @param pk_corp 公司
	 * @param period 当前日期所在期间的，前一期间
	 */
	private List<TaxReportVO> querytypeDetails(String pk_corp,String period){
		if(StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(period))
			return null;
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(period);
		//取当前日期所在期间的上一年度，查询年度报表
		String after = DateUtils.getNextPeriod(period);
		params.addParam(String.valueOf(Integer.valueOf(after.substring(0, 4))-1));
		StringBuffer sf = new StringBuffer();
		sf.append("  select t2.sbcode,t2.sbname,t2.sbzq periodtype ,t1.*  ");
		sf.append(" from ynt_taxreport t1 ");
		sf.append("    join ynt_tax_sbzl t2 on t1.pk_taxsbzl = t2.pk_taxsbzl ");
		sf.append("  where nvl(t1.dr,0)=0 and nvl(t2.dr,0) = 0 ");
		sf.append("  and t1.pk_corp = ? and t1.period in(?,?) order by t2.showorder ");
		List<TaxReportVO> list = (List<TaxReportVO>)singleObjectBO.executeQuery(sf.toString(), params,
				new BeanListProcessor(TaxReportVO.class));
		return list;
	}
	
	//根据主表查询子表信息
	private List<TaxReportDetailVO> queryTaxDetails(List<TaxReportVO> list){
		if(list == null || list.size() == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_taxreportdetail ");
		sf.append(" where  nvl(dr,0) = 0 and pk_taxreport in( ");
		String key = "";
		SQLParameter sp = new SQLParameter();
		for(int i = 0 ;i<list.size();i++){
			key = list.get(i).getPk_taxreport();
			if(StringUtil.isEmpty(key))
				continue;
			sp.addParam(key);
			if(i==0){
				sf.append("?");
			}else{
				sf.append(",?");
			}
		}
		sf.append(") ");
		List<TaxReportDetailVO> zlist = (List<TaxReportDetailVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(TaxReportDetailVO.class));
		return zlist;
	}
	
	protected List<String> queryCorpTaxRptVO(CorpVO corpvo, String period){
		List<TaxRptTempletVO> list = queryRptTempletVOs(corpvo.getPk_corp(),period);
		if(list == null || list.size() == 0)
			return null;
		List<String> zlist = new ArrayList<String>();
		for(TaxRptTempletVO b1 : list){
			if(StringUtil.isEmpty(b1.getPk_taxsbzl()))
				continue;
			if(zlist.contains(b1.getPk_taxsbzl()))
				continue;
			zlist.add(b1.getPk_taxsbzl());
		}
		return zlist;
	}
	
	public List<TaxRptTempletVO> queryRptTempletVOs(String pk_corp,String period) throws DZFWarpException{
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select t1.sb_zlbh sbcode, t1.* from ynt_taxrpttemplet t1 ");
		sf.append(" join ynt_taxrpt t2 on t1.pk_taxrpttemplet = t2.pk_taxrpttemplet ");
		sf.append(" where nvl(t1.dr,0) = 0 and nvl(t2.dr,0) = 0  and t2.pk_corp = ? order by orderno");
		List<TaxRptTempletVO> list1 = (List<TaxRptTempletVO>)
				singleObjectBO.executeQuery(sf.toString(), params,new BeanListProcessor(TaxRptTempletVO.class));
		///根据公司期间过滤核定征收或者查账征收的税表
    	TaxEffeHistVO hvo = corpTaxService.queryTaxEffHisVO(pk_corp,period);
    	Integer type = hvo.getIncomtaxtype();
    	if(type == null)
    		return list1;
    	if(type == TaxRptConstPub.INCOMTAXTYPE_QY){//企业所得税
    		if(hvo.getTaxlevytype() == null || hvo.getTaxlevytype() == TaxRptConstPub.TAXLEVYTYPE_CZZS){//为空默认查账征收
    			list1 = filterQybsReport(list1,TaxRptConstPub.SHOWSDSRPT_A);
    		}else if(hvo.getTaxlevytype() != null && hvo.getTaxlevytype() == TaxRptConstPub.TAXLEVYTYPE_HDZS){//为核定征收
    			list1 = filterQybsReport(list1,TaxRptConstPub.SHOWSDSRPT_B);
    		}
    	}else if(type == TaxRptConstPub.INCOMTAXTYPE_GR){//个人所得税
    		list1 = filterQybsReport(list1,TaxRptConstPub.SHOWSDSRPT_GR);
    	}
    	return list1;
	}
	
    private List<TaxRptTempletVO> filterQybsReport(List<TaxRptTempletVO> list1,int showsdsrpt){
    	if(list1 == null || list1.size() == 0)
    		return list1;
    	List<TaxRptTempletVO> list = new ArrayList<TaxRptTempletVO>();
    	for(TaxRptTempletVO vo : list1){
    		if(showsdsrpt == TaxRptConstPub.SHOWSDSRPT_A){
    			if(TaxRptConstPub.SB_ZLBH10413.equals(vo.getSbcode()) 
    					|| TaxRptConstPub.SB_ZLBHA06442.equals(vo.getSbcode())){
    				continue;
    			}
    		}else if(showsdsrpt == TaxRptConstPub.SHOWSDSRPT_B){
				if(TaxRptConstPub.SB_ZLBH10412.equals(vo.getSbcode())
						|| TaxRptConstPub.SB_ZLBHA06442.equals(vo.getSbcode())){
					continue;
				}
    		}else if(showsdsrpt == TaxRptConstPub.SHOWSDSRPT_GR){
    			if(TaxRptConstPub.SB_ZLBH10412.equals(vo.getSbcode()) 
    					|| TaxRptConstPub.SB_ZLBH10413.equals(vo.getSbcode())){
    				continue;
    			}
    		}
    		list.add(vo);
    	}
    	return list;
    }
	
	
//	@Override
//	public TaxReportVO[] getTypeList(CorpVO corpvo, String yearmonth,
//			String operatorid, String operatedate, SingleObjectBO sbo)
//			throws DZFWarpException {
//		
//		
//		/***加入并发  防止重复新增数据*****/
//		TaxReportVO[] vos = null;
//		try {
//			LockUtil.getInstance().tryLockKey(corpvo.getPk_corp(), yearmonth, 30);
//			String pk_corp = corpvo.getPk_corp();
//			
//			TaxTypeListVO headold = null;
//			TaxReportVO[] vosold = null;
//			SQLParameter params = new SQLParameter();
//			params.addParam(pk_corp);
//			params.addParam(yearmonth);
//			boolean showYearInTax = Integer.valueOf(yearmonth.substring(5)) <= 5;
//			TaxTypeListVO[] voheads = (TaxTypeListVO[]) sbo.queryByCondition(TaxTypeListVO.class,
//					"nvl(dr,0) = 0 and pk_corp=? and yearmonth=?", params);
//			if (voheads != null && voheads.length > 0) {
//				headold = voheads[0];
//				params = new SQLParameter();
//				params.addParam(headold.getPrimaryKey());
//				vosold = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class,
//						"nvl(dr,0)=0 and pk_taxtypelist=?", params);
//			}
//
//			vos = getTaxReportVO(showYearInTax, yearmonth, corpvo, sbo);
//
//			// 建立主表记录
//			String pk = IDGenerate.getInstance().getNextID(pk_corp);
//
//			TaxTypeListVO headvo = new TaxTypeListVO();
//			headvo.setPk_taxtypelist(pk);
//			headvo.setPk_corp(pk_corp);
//			headvo.setNsrsbh(corpvo.getTaxcode());
//			headvo.setNsrdzdah(vos[0].getNsrdzdah());
//			headvo.setCoperatorid(operatorid);
//			headvo.setDoperatedate(new DZFDate(operatedate));
//			headvo.setYearmonth(yearmonth);
//			headvo.setDr(0);
//			for (TaxReportVO vo : vos) {
//				pk = IDGenerate.getInstance().getNextID(pk_corp);
//				vo.setPk_taxtypelistdetail(pk);
//				vo.setPk_corp(pk_corp);
//				vo.setPk_taxtypelist(headvo.getPrimaryKey());
//				vo.setDr(0);
//				vo.setPeriodtype(getPeriodType(vo.getPeriodfrom(), vo.getPeriodto()));
//			}
//			if (headold != null) {
//				sbo.deleteObject(headold);
//			}
//			if (vosold != null) {
//				sbo.deleteVOArray(vosold);
//			}
//			// 增加主表
//			sbo.insertVOWithPK(headvo);
//			// 增加子表
//			sbo.insertVOArr(pk_corp, vos);
//		} catch (Exception e) {
//			log.error("错误",e);
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				throw new WiseRunException(e);
//			}
//		}finally {
//			LockUtil.getInstance().unLock_Key(corpvo.getPk_corp(), yearmonth);
//		}
//		return vos;
//	}

//	@Override
//	public List<RptBillVO> getRptBillVO(TaxReportVO paravo, SingleObjectBO sbo, CorpVO corpvo) throws DZFWarpException {
//		
//		return getDefaultRptBillVO(paravo, sbo, corpvo);
//		
//	}
		/*
		 * List<RptBillVO> volist = null; if
		 * (TaxRptConst.SB_ZLBH_SETTLEMENT.equals(paravo.getSb_zlbh())) { volist
		 * = getDefaultRptBillVO(paravo, sbo, corpvo); } else if
		 * (TaxRptConst.SB_ZLBH10101.equals(paravo.getSb_zlbh())) { // 从旧报表中取期初,
		 * 先实验取文件名称， String filename = getPrevPeriodSpreadFileName(paravo, sbo);
		 * if (StringUtil.isEmpty(filename) || new File(filename).exists() ==
		 * false) { throw new BusinessException("没有找到报表文件，无法获得期初数据!"); }
		 * 
		 * volist = new ArrayList<RptBillVO>(); RptBillVO vo = new RptBillVO();
		 * vo.setXh("1"); vo.setBb_zlid("10101002");
		 * vo.setBb_zlmc("增值税纳税申报表附列资料（一）"); volist.add(vo);
		 * 
		 * vo = new RptBillVO(); vo.setXh("2"); vo.setBb_zlid("10101003");
		 * vo.setBb_zlmc("增值税纳税申报表附列资料（二）"); volist.add(vo);
		 * 
		 * vo = new RptBillVO(); vo.setXh("3"); vo.setBb_zlid("10101004");
		 * vo.setBb_zlmc("增值税纳税申报表附列资料（三）"); volist.add(vo);
		 * 
		 * vo = new RptBillVO(); vo.setXh("4"); vo.setBb_zlid("10101005");
		 * vo.setBb_zlmc("增值税纳税申报表附列资料（四）"); volist.add(vo);
		 * 
		 * // vo = new RptBillVO(); // vo.setXh("5"); //
		 * vo.setBb_zlid("10101023"); // vo.setBb_zlmc("增值税纳税申报表附列资料（五）"); //
		 * volist.add(vo); // // vo = new RptBillVO(); // vo.setXh("6"); //
		 * vo.setBb_zlid("10101006"); // vo.setBb_zlmc("固定资产（不含不动产）进项税额抵扣情况表");
		 * // volist.add(vo); // // vo = new RptBillVO(); // vo.setXh("7"); //
		 * vo.setBb_zlid("10101022"); // vo.setBb_zlmc("本期抵扣进项税额结构明细表"); //
		 * volist.add(vo); // // vo = new RptBillVO(); // vo.setXh("8"); //
		 * vo.setBb_zlid("10101021"); // vo.setBb_zlmc("增值税减免税申报明细表"); //
		 * volist.add(vo); // // vo = new RptBillVO(); // vo.setXh("9"); //
		 * vo.setBb_zlid("10101024"); // vo.setBb_zlmc("营改增税负分析测算明细表"); //
		 * volist.add(vo); // vo = new RptBillVO(); vo.setXh("24");
		 * vo.setBb_zlid("10101001"); vo.setBb_zlmc("增值税纳税申报表"); volist.add(vo);
		 * } else if (TaxRptConst.SB_ZLBH10102.equals(paravo.getSb_zlbh())) {
		 * 
		 * // 从旧报表中取期初, 先实验取文件名称， String filename =
		 * getPrevPeriodSpreadFileName(paravo, sbo); if
		 * (StringUtil.isEmpty(filename) || new File(filename).exists() ==
		 * false) { throw new BusinessException("没有找到报表文件，无法获得期初数据!"); }
		 * 
		 * volist = new ArrayList<RptBillVO>(); RptBillVO vo = new RptBillVO();
		 * // vo.setXh("1"); // vo.setBb_zlid("10102002"); //
		 * vo.setBb_zlmc("增值税纳税申报表（小规模纳税人适用）附列资料"); // volist.add(vo); // // vo
		 * = new RptBillVO(); // vo.setXh("2"); // vo.setBb_zlid("10102003"); //
		 * vo.setBb_zlmc("增值税纳税申报表附列资料（四）（税额抵减情况表）"); // volist.add(vo); // //
		 * vo = new RptBillVO(); // vo.setXh("3"); // vo.setBb_zlid("10102004");
		 * // vo.setBb_zlmc("增值税减免税申报明细表"); // volist.add(vo); // // vo = new
		 * RptBillVO(); // vo.setXh("4"); // vo.setBb_zlid("10102005"); //
		 * vo.setBb_zlmc("应税服务减除项目清单"); // volist.add(vo);
		 * 
		 * vo = new RptBillVO(); vo.setXh("5"); vo.setBb_zlid("10102001");
		 * vo.setBb_zlmc("增值税纳税申报表"); volist.add(vo); }
		 * 
		 * return volist;
		 */

//	/**
//	 * 计算上一个报表名称
//	 * 
//	 * @param reportvo
//	 * @return
//	 */
//	private String getPrevPeriodSpreadFileName(TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
//		String filename = null;
//
//		DZFDate periodFrom = new DZFDate(reportvo.getPeriodfrom());
//		DZFDate newPeriodFrom = null;
//		DZFDate newPeriodTo = null;
//		Integer iPeriodType = reportvo.getPeriodtype();
//		if (iPeriodType == 0) {
//			// 月报
//			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(1).toString().substring(0, 7));
//			newPeriodTo = periodFrom.getDateBefore(1);
//		} else if (iPeriodType == 1) {
//			// 季报
//			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(63).toString().substring(0, 7));
//			newPeriodTo = periodFrom.getDateBefore(1);
//		} else if (iPeriodType == 2) {
//			// 年报
//			newPeriodFrom = DateUtils.getPeriodStartDate(periodFrom.getDateBefore(360).toString().substring(0, 7));
//			newPeriodTo = periodFrom.getDateBefore(1);
//		} else {
//			throw new BusinessException("期间类型错误");
//		}
//		// 查询是否有上一张报表
//		String condition = "nvl(dr,0)=0 and pk_corp=? and zsxm_dm=? and sb_zlbh=? and periodtype=? and periodfrom=? and periodto=?";
//		SQLParameter params = new SQLParameter();
//		params.addParam(reportvo.getPk_corp());
//		params.addParam(reportvo.getZsxm_dm());
//		params.addParam(reportvo.getSb_zlbh());
//		params.addParam(iPeriodType);
//		params.addParam(newPeriodFrom.toString());
//		params.addParam(newPeriodTo.toString());
//
//		TaxReportVO[] prevReportvos = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, condition, params);
//		if (prevReportvos != null && prevReportvos.length > 0) {
//
//			// 有上一期间的报表
//			filename = TaxReportPath.taxReportPath + "spreadfile/spread" + prevReportvos[0].getPk_taxreport()
//					+ prevReportvos[0].getSb_zlbh() + ".ssjson";
//			// 检查报表是否填写
//			params = new SQLParameter();
//			params.addParam(prevReportvos[0].getPk_taxreport());
//			TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
//					"nvl(dr,0)=0 and pk_taxreport=?", params);
//			if (StringUtil.isEmpty(detailvos[0].getSpreadfile())) {
//				throw new BusinessException("上期报表已创建但未填写，无法读取期初数据!");
//			}
//			if (prevReportvos[0].getVbillstatus() == TaxRptConst.IBILLSTATUS_UNAPPROVE) {
//				throw new BusinessException("上期间报表未审核，请先审核上期间报表");
//			}
//		} else {
//			// 判断前期是否录入过报表
//			condition = "nvl(dr,0)=0 and pk_corp=? and zsxm_dm=? and sb_zlbh=? and periodtype=? and periodfrom<? order by periodfrom desc";
//			params = new SQLParameter();
//			params.addParam(reportvo.getPk_corp());
//			params.addParam(reportvo.getZsxm_dm());
//			params.addParam(reportvo.getSb_zlbh());
//			params.addParam(iPeriodType);
//			params.addParam(newPeriodFrom.toString());
//			prevReportvos = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class, condition, params);
//			if (prevReportvos != null && prevReportvos.length > 0) {
//				throw new BusinessException("您录入的报表期间与上一个期间 " + prevReportvos[0].getPeriodfrom() + " 至 "
//						+ prevReportvos[0].getPeriodto() + " 不连续");
//			}
//			// 查询期初录入
//			condition = "nvl(dr,0)=0 and pk_corp=? and sb_zlbh=? ";
//			params = new SQLParameter();
//			params.addParam(reportvo.getPk_corp());
//			params.addParam(reportvo.getSb_zlbh());
//
//			TaxReportInitVO[] initvos = (TaxReportInitVO[]) sbo.queryByCondition(TaxReportInitVO.class, condition,
//					params);
//			if (initvos != null && initvos.length > 0) {
//				// 判断期初的所属期间是否是当前录入报表起始期间
//				if (periodFrom.toString().substring(0, 7).equals((initvos[0].getPeriod())) == false
//						&& initvos[0].getPeriod()
//								.equals(CorpCache.getInstance()
//										.get(initvos[0].getCoperatorid(), initvos[0].getPk_corp()).getBegindate()
//										.toString().substring(0, 7)) == false) {
//					throw new BusinessException("期初数据所属期间是: " + initvos[0].getPeriod() + ", 与您填报起始期间不一致!");
//				}
//
//				filename = initvos[0].getSpreadfile();
//			} else {
//				throw new BusinessException("您需要先录入纳税报表期初数据!");
//			}
//
//		}
//		return filename;
//	}

//	public List<RptBillVO> getDefaultRptBillVO(TaxReportVO paravo, SingleObjectBO sbo, CorpVO corpvo)
//			throws DZFWarpException {
//		List<RptBillVO> volist = null;
//		// 先查询客户档案中勾选的报表
//
//		SQLParameter params = new SQLParameter();
//		params.addParam(paravo.getPk_corp());
//		CorpTaxRptVO[] taxrptvos = (CorpTaxRptVO[]) sbo.queryByCondition(CorpTaxRptVO.class,
//				"nvl(dr,0)=0 and pk_corp=? and taxrptcode like '" + paravo.getSb_zlbh() + "%'", params);
//		List listRptcode = new ArrayList<String>();
//		
//		DZFBoolean isSave = DZFBoolean.FALSE;
//		if (taxrptvos != null && taxrptvos.length > 0) {
//			isSave = DZFBoolean.TRUE;
//			DZFBoolean isselect = null;
//			for (CorpTaxRptVO vo : taxrptvos) {
//				isselect = vo.getIsselect();
//				if(isselect != null && isselect.booleanValue()){
//					listRptcode.add(vo.getTaxrptcode());
//				}
//				
//			}
//		}
//
//		// 查询全部默认必填报表
//		params = new SQLParameter();
//		params.addParam(paravo.getLocation());
//		params.addParam(paravo.getSb_zlbh());
//		TaxRptTempletVO[] votemplets = (TaxRptTempletVO[]) sbo.queryByCondition(TaxRptTempletVO.class,
//				"nvl(dr,0)=0 and rtrim(location)= ? and sb_zlbh=? order by orderno", params);
//		volist = new ArrayList<RptBillVO>();
//
//		RptBillVO vo = null;
//		int iXH = 1;
//		for (TaxRptTempletVO tvo : votemplets) {
////			if (listRptcode.size() > 0) // 客户档案设置过填报列表，没有设置的报表跳过
//			if(isSave.booleanValue())
//			{
//				if (listRptcode.contains(tvo.getReportcode()) == false) {
//					continue;
//				}
//			} 
//			else // 客户档案没设置过填报列表，只填写必填内容
//			{
//				if ((tvo.getIfrequired() == null || tvo.getIfrequired().booleanValue() == false)
//						&& !TaxRptConst.SB_ZLBHD1.equals(tvo.getSb_zlbh())) {//印花税单笔明细申报
//					continue;
//				}
//			}
//			vo = new RptBillVO();
//			vo.setXh(String.valueOf(iXH++));
//			vo.setBb_zlid(tvo.getReportcode());
//			vo.setBb_zlmc(tvo.getReportname());
//			volist.add(vo);
//		}
//		return volist;
//	}

	@Override
	public void checkBeforeProcessApprove(TaxReportVO reportvo, SingleObjectBO sbo, CorpVO corpvo)
			throws DZFWarpException {
		// 检查是否有上期报表，是否已审核
		checkPrevPeriodApprove(reportvo, sbo);
	}

	private void checkPrevPeriodApprove(TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
		// 检查是否有上期报表，是否审核
		SQLParameter params = new SQLParameter();
		params.addParam(reportvo.getPk_corp());
		params.addParam(reportvo.getSb_zlbh());
		params.addParam(reportvo.getPeriodfrom());
		params.addParam(TaxRptConst.IBILLSTATUS_UNAPPROVE);
		TaxReportVO[] rptvos = (TaxReportVO[]) sbo.queryByCondition(TaxReportVO.class,
				"nvl(dr,0)=0 and pk_corp=? and sb_zlbh=? and periodfrom <? and vbillstatus=?", params);
		if (rptvos != null && rptvos.length > 0) {
			throw new BusinessException("上期间报表未审核，请先审核上期间报表");
		}
	}

	@Override
	public String checkReportData(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10101)) {

			errmsg = checkForSB_ZLBH10101(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} else if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10412)){
			
			errmsg = checkForSB_ZLBH10412(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} else if (reportvo.getSb_zlbh().equals(TaxRptConst.SB_ZLBH10413)){
			
			errmsg = checkForSB_ZLBH10413(mapJson, corpvo, reportvo, hmRptDetail, sbo);
		} 
		return errmsg;
	}
	
	protected String checkForSB_ZLBH10413(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		
		List<String> listReportName = spreadtool.getReportNameList(mapJson);
		
		String rpt1 = "主表";
		if (listReportName.contains(rpt1)) {
			if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 26, 8))) {//小型微利企业
				errmsg += "纳税人必须填写是否小型微利企业<br>";
			}
			if (getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 24, 8)).doubleValue() <= 0) {
				errmsg += "纳税人必须填写季末从业人数，且人数必须大于0且为整数<br>";
			}
			
			if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 25, 3)).doubleValue() < 0) {
				errmsg += "纳税人必须填写季初资产总额（万元），且季初资产总额必须大于等于0<br>";
			}
			if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 25, 8)).doubleValue() < 0) {
				errmsg += "纳税人必须填写季末资产总额（万元），且季末资产总额必须大于等于0<br>";
			}
		}
		
		return errmsg;
	}
	
	protected String checkForSB_ZLBH10412(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";
		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);
		
		List<String> listReportName = spreadtool.getReportNameList(mapJson);
		
		String rpt1 = "A200000所得税月(季)度预缴纳税申报表";
		if (listReportName.contains(rpt1)) {
			String qylx = (String) spreadtool.getCellValue(mapJson, rpt1, 5, 1);
			if ("一般企业".equals(qylx) || "跨地区经营汇总纳税企业总机构".equals(qylx)) {
				
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 31, 8))) {
					errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否科技型中小企业<br>";
				}
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 31, 3))) {
					errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否高新技术企业<br>";
				}
				if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 32, 3))) {
					errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否技术入股递延纳税事项<br>";
				}
				
//				if(PeriodType.jidureport == reportvo.getPeriodtype()){//季报才校验
					if (StringUtil.isEmpty((String) spreadtool.getCellValue(mapJson, rpt1, 36, 8))) {//小型微利企业
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写是否小型微利企业<br>";
					}
					if (getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 34, 8)).doubleValue() <= 0) {
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写季末从业人数，且人数必须大于0且为整数<br>";
					}
					
					if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 35, 3)).doubleValue() < 0) {
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写季初资产总额（万元），且季初资产总额必须大于等于0<br>";
					}
					if (getDzfDoubleByMinus(spreadtool.getCellValue(mapJson, rpt1, 35, 8)).doubleValue() < 0) {
						errmsg += "企业类型为“一般企业”或“跨地区经营汇总纳税企业总机构”的纳税人必须填写季末资产总额（万元），且季末资产总额必须大于等于0<br>";
					}
//				}
				
			}
			
			DZFDouble line12 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 19, 8));
			DZFDouble line11 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt1, 18, 8));
			
			if (line12.doubleValue() > line11.doubleValue()
					|| line12.doubleValue() < 0) {
				errmsg += "A200000所得税月(季)度预缴纳税申报表第12行<=主表第11行应纳所得税额本年累计金额，且第12行≥0<br>";
			}
			
		}
		
		String rpt2 = "A201010免税收入、减计收入、所得减免等优惠明细表";
		if(listReportName.contains(rpt2)){
			DZFDouble v = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 7, 5));
			DZFDouble v1 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 8, 5));
			DZFDouble v2 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 9, 5));
			DZFDouble v3 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 10, 5));
			DZFDouble v4 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 11, 5));
			
			DZFDouble result = SafeCompute.add(v1, v2);
			result = SafeCompute.add(result, v3);
			result = SafeCompute.add(result, v4);
			
			if(v.doubleValue() < result.doubleValue()){
				errmsg += "A201010免税收入、减计收入、所得减免等优惠明细表 第3行>=第4+5+6+7行<br>";
			}
			
			DZFDouble ss = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 39, 5));
			DZFDouble s1 = getDzfDouble(spreadtool.getCellValue(mapJson, rpt2, 40, 5));
			
			if(ss.doubleValue() < s1.doubleValue()){
				errmsg += "A201010免税收入、减计收入、所得减免等优惠明细表 第33行>=第33.1行<br>";
			}
		}
		
		return errmsg;
	}

	private String checkForSB_ZLBH10101(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		String errmsg = "";

		ITaxBalaceCcrService taxbalancesrv = (ITaxBalaceCcrService) SpringUtils.getBean("gl_tax_formulaimpl");
		SpreadTool spreadtool = new SpreadTool(taxbalancesrv);

		List<String> listReportName = spreadtool.getReportNameList(mapJson);

		if (listReportName.contains(TaxRptConst.SRPTNAME10101001)) {
			YntCpaccountVO[] accountVO  = accountService.queryByPk(corpvo.getPk_corp());
			// 本期应补(退)税额=glamtoccr("222109+2221009","@year","@period","y","cr","@corp")
			// G43+L43 42行6列和11列
			DZFDouble v1 = getDzfDouble(spreadtool.getCellValue(mapJson, TaxRptConst.SRPTNAME10101001, 42, 6));
			DZFDouble v2 = getDzfDouble(spreadtool.getCellValue(mapJson, TaxRptConst.SRPTNAME10101001, 42, 11));

			DZFDouble v3 = getDzfDouble(spreadtool.getFormulaValue(null,
					"glamtoccr(\"222109+2221009\",\"@year\",\"@period\",\"y\",\"cr\",\"@corp\")",
					getQcData(corpvo, reportvo, sbo), corpvo, reportvo,accountVO)).setScale(2, DZFDouble.ROUND_HALF_UP);

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

	protected DZFDouble getDzfDouble(Object obj) {
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
	
	protected DZFDouble getDzfDoubleByMinus(Object obj){
		if (obj == null || obj.toString().trim().length() == 0) {
			return new DZFDouble(-100);
		} else {
			try {
				return new DZFDouble(obj.toString().replaceAll(",", ""));
			} catch (Exception e) {
				return new DZFDouble(-100);
			}
		}
	}

	@Override
	public HashMap<String, Object> getQcData(CorpVO corpvo, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {
		return new HashMap();
		/*
		 * // 期初数据，待查询 HashMap hmQCData = new HashMap();
		 * 
		 * // 年度汇算清缴不需要期初 if ("04".equals(reportvo.getZsxm_dm()) &&
		 * TaxRptConst.SB_ZLBH_SETTLEMENT.equals(reportvo.getSb_zlbh())) {
		 * return hmQCData; }
		 * 
		 * // 从旧报表中取期初 String filename = getPrevPeriodSpreadFileName(reportvo,
		 * sbo); File file = new File(filename); if (file.exists() == false) {
		 * throw new BusinessException("报表文件丢失!"); } Map objMapReport =
		 * readJsonValue(readFileString(filename), LinkedHashMap.class);
		 * 
		 * SQLParameter params = new SQLParameter();
		 * params.addParam(reportvo.getSb_zlbh()); TaxRptTempletVO[] templetvos
		 * = (TaxRptTempletVO[]) sbo.queryByCondition(TaxRptTempletVO.class,
		 * "nvl(dr,0)=0 and rtrim(location)='通用' and sb_zlbh=?", params); //
		 * 模板放入hashmap HashMap<String, TaxRptTempletVO> hmTemplet = new
		 * HashMap<String, TaxRptTempletVO>();
		 * 
		 * String templetpks = ""; for (TaxRptTempletVO vo : templetvos) {
		 * hmTemplet.put(vo.getPk_taxrpttemplet(), vo); templetpks +=
		 * (templetpks.length() == 0 ? "'" : ",'") + vo.getPrimaryKey() + "'"; }
		 * TaxRptTempletPosVO[] posvos = (TaxRptTempletPosVO[])
		 * sbo.queryByCondition(TaxRptTempletPosVO.class,
		 * "nvl(dr,0)=0 and pk_taxrpttemplet in (" + templetpks +
		 * ") and itemkeyinitname is not null", null); ITaxBalaceCcrService
		 * taxbalancesrv = (ITaxBalaceCcrService)
		 * SpringUtils.getBean("gl_tax_formulaimpl"); SpreadTool spreadtool =
		 * new SpreadTool(taxbalancesrv);
		 * 
		 * for (TaxRptTempletPosVO posvo : posvos) { if
		 * (StringUtil.isEmpty(posvo.getItemkeyinitname())) { continue; } //
		 * 从模板取值，赋值到期初hashmap Object oValue =
		 * spreadtool.getCellValue(objMapReport,
		 * hmTemplet.get(posvo.getPk_taxrpttemplet()).getReportname(), posvo);
		 * if (oValue != null) {
		 * hmQCData.put(posvo.getItemkeyinitname().toLowerCase().trim(),
		 * oValue); } }
		 * 
		 * // 如果是年初，期初都是零。 必须放在末尾，不能放前面，上面需要先检查上张报表审核状态 if
		 * (reportvo.getPeriodfrom().endsWith("-01-01")) { hmQCData = new
		 * HashMap(); }
		 * 
		 * return hmQCData;
		 */
	}

	protected String readFileString(String filepath) throws DZFWarpException {
		String sReturn = null;
		if(filepath.startsWith("*")){
			try {
				byte[] bytes = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).downFile(filepath.substring(1));
				
				if(bytes!=null && bytes.length>0){
					sReturn = new String(bytes, "utf-8");
				}
				return sReturn;
			} catch (Exception e) {
				throw new WiseRunException(e);
			}
			
		}
		
		File f = new File(filepath);
		if (f.exists() && f.isFile()) {
			int byteread = 0;
			int bytesum = 0;

			FileInputStream inStream = null;
			ByteOutputStream bos = null;
			try {
				inStream = new FileInputStream(f);

				bos = new ByteOutputStream();
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					bos.write(buffer, 0, byteread);
				}
				bos.flush();
				byte[] bs = bos.toByteArray();
				sReturn = new String(bs, "utf-8");
			} catch (Exception e) {
				throw new WiseRunException(e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException ioe) {
					}
				}
				if (bos != null)
					bos.close();
			}
		}

		return sReturn;
	}

	protected <T> T readJsonValue(String strJSON, Class<T> clazz) throws DZFWarpException {
		try {
			return getObjectMapper().readValue(strJSON, clazz);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
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

//	@Override
//	public TaxReportVO[] getTaxReportVO(boolean showYearInTax, String yearmonth, CorpVO corpvo,
//			SingleObjectBO sbo) throws DZFWarpException {
//		List<TaxReportVO> list = new ArrayList<TaxReportVO>();
//		// TaxReportVO[] vos = new TaxReportVO[showYearInTax ? 3
//		// : 2];// TaxReportVO[1];
//		//
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("一般纳税人")) {
//			TaxReportVO detailvo = new TaxReportVO();
//			detailvo.setSb_zlbh("10101");
//
//			// detailvo.setNsrdzdah(null); //纳税人电子档案号，没从江苏网站获取，所以无值
//			String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//			detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo.setZsxm_dm("01"); // 增值税
//			detailvo.setPeriodtype(0); // 月报
//
//			list.add(detailvo);
//			
//			// 代征地税
//			TaxReportVO detailvo2 = new TaxReportVO();
//			detailvo2.setSb_zlbh("50101");
//
//			detailvo2.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo2.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo2.setZsxm_dm("80"); // 代征地税
//			detailvo2.setPeriodtype(0); // 月报
//
//			list.add(detailvo2);
//
//			// 一般纳税人，所得税季报，A类
//			TaxReportVO detailvo1 = new TaxReportVO();
//			detailvo1.setSb_zlbh("10412");
//
//			detailvo1.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo1.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo1.setZsxm_dm("04"); // 所得税
//			detailvo1.setPeriodtype(1); // 季报报
//
//			list.add(detailvo1);
//			
//			// 一般纳税人，所得税季报，B类
//			TaxReportVO intaxB = new TaxReportVO();
//			intaxB.setSb_zlbh("10413");
//			intaxB.setPeriodfrom(getQuarterStartDate(yearmonth));
//			intaxB.setPeriodto(getQuarterEndDate(yearmonth));
//			intaxB.setZsxm_dm("10413"); // 所得税
//			intaxB.setPeriodtype(1); // 季报报
//			list.add(intaxB);
//			
//			// 一般纳税人 ，文化事业建设费
//			TaxReportVO detailvo3 = new TaxReportVO();
//			detailvo3.setSb_zlbh(TaxRptConst.SB_ZLBH10601);
//			detailvo3.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//			detailvo3.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//			detailvo3.setZsxm_dm(TaxRptConst.ZSXMDM_WHJS);
//			detailvo3.setPeriodtype(PeriodType.monthreport);
//			
//			list.add(detailvo3);
//		} else if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("小规模纳税人")) {
//			TaxReportVO detailvo = new TaxReportVO();
//			detailvo.setSb_zlbh("10102");
//			// detailvo.setNsrdzdah(null); //纳税人电子档案号，没从江苏网站获取，所以无值
//			// String sQueryPeriod = new DZFDate(yearmonth +
//			// "-01").getDateBefore(1).toString().substring(0, 7);
//
//			detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo.setZsxm_dm("01"); // 增值税
//			detailvo.setPeriodtype(1); // 季报
//
//			list.add(detailvo);
//			
//			// 代征地税
//			TaxReportVO detailvo2 = new TaxReportVO();
//			detailvo2.setSb_zlbh("50102");
//
//			detailvo2.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo2.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo2.setZsxm_dm("80"); // 代征地税
//			detailvo2.setPeriodtype(1); // 季报
//
//			list.add(detailvo2);
//
//			// 小规模纳税人，所得税季报，A类
//			TaxReportVO detailvo1 = new TaxReportVO();
//			detailvo1.setSb_zlbh("10412");
//
//			detailvo1.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo1.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo1.setZsxm_dm("04"); // 所得税
//			detailvo1.setPeriodtype(1); // 季报报
//
//			list.add(detailvo1);
//			
//			// 小规模，所得税季报，B类
//			TaxReportVO intaxB = new TaxReportVO();
//			intaxB.setSb_zlbh("10413");
//			intaxB.setPeriodfrom(getQuarterStartDate(yearmonth));
//			intaxB.setPeriodto(getQuarterEndDate(yearmonth));
//			intaxB.setZsxm_dm("10413"); // 所得税
//			intaxB.setPeriodtype(1); // 季报报
//			list.add(intaxB);
//			
//			// 小规模纳税人， 文件事业建设费
//			TaxReportVO detailvo3 = new TaxReportVO();
//			detailvo3.setSb_zlbh(TaxRptConst.SB_ZLBH10601);
//			detailvo3.setPeriodfrom(getQuarterStartDate(yearmonth));
//			detailvo3.setPeriodto(getQuarterEndDate(yearmonth));
//			detailvo3.setZsxm_dm(TaxRptConst.ZSXMDM_WHJS);
//			detailvo3.setPeriodtype(PeriodType.jidureport);
//
//			list.add(detailvo3);
//		} else {
//			throw new BusinessException("当前功能仅支持一般纳税人和小规模纳税人增值税申报、所得税申报。");
//		}
//		if (showYearInTax) {
//			// 所得税年度汇算清缴
//			TaxReportVO detailvo = new TaxReportVO();
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBH_SETTLEMENT);
//			int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1;
//			detailvo.setPeriodfrom(year + "-01-01");
//			detailvo.setPeriodto(year + "-12-31");
//			detailvo.setZsxm_dm("04"); // 所得税
//			detailvo.setPeriodtype(2); // 年报
//			list.add(detailvo);
//		}
//		// 增加财报
//		addFinReport(corpvo, yearmonth, list);
//		addFinReport2(corpvo, yearmonth, list);
//		addOtherReport(corpvo, yearmonth, list);
//		addStampReport(corpvo, yearmonth, list);
//		return list.toArray(new TaxReportVO[0]);
//	}

//	protected void addOtherReport(CorpVO corpvo, String yearmonth, List<TaxReportVO> list) {
//		String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//		String periodBegin = DateUtils.getPeriodStartDate(sQueryPeriod).toString();
//		String periodEnd = DateUtils.getPeriodEndDate(sQueryPeriod).toString();
//		if (corpvo.getChargedeptname() != null && corpvo.getChargedeptname().startsWith("小规模纳税人")) {
//			TaxReportVO addTax = new TaxReportVO();
//			addTax.setSb_zlbh("1010201");
//			addTax.setPeriodfrom(periodBegin);
//			addTax.setPeriodto(periodEnd);
//			addTax.setZsxm_dm("01"); // 增值税
//			addTax.setPeriodtype(0); // 月报
//			list.add(addTax);
//		}
//		TaxReportVO detailvo1 = new TaxReportVO();
//		detailvo1.setSb_zlbh("10412");
//		detailvo1.setPeriodfrom(periodBegin);
//		detailvo1.setPeriodto(periodEnd);
//		detailvo1.setZsxm_dm("04"); // 所得税
//		detailvo1.setPeriodtype(0); // 月报
//		list.add(detailvo1);
//		
//		TaxReportVO inTaxB = new TaxReportVO();
//		inTaxB.setSb_zlbh("10413");
//		inTaxB.setPeriodfrom(periodBegin);
//		inTaxB.setPeriodto(periodEnd);
//		inTaxB.setZsxm_dm("10413"); // 所得税
//		inTaxB.setPeriodtype(0); // 月报
//		list.add(inTaxB);
//	}

//	protected void addFinReport(CorpVO corpvo, String yearmonth, List<TaxReportVO> list) {
//		TaxReportVO detailvo = new TaxReportVO();
//		if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
//																		// 会计期间
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC1);
//		} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
//																				// 会计期间
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC2);
//		}
//		// String sQueryPeriod = new DZFDate(yearmonth +
//		// "-01").getDateBefore(1).toString().substring(0, 7);
//		detailvo.setPeriodfrom(getQuarterStartDate(yearmonth));
//		detailvo.setPeriodto(getQuarterEndDate(yearmonth));
//		detailvo.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//		detailvo.setPeriodtype(1);// 季报
//		if (StringUtil.isEmpty(detailvo.getSb_zlbh()))
//			return;
//		list.add(detailvo);
//		
//		boolean showYearInTax = Integer.valueOf(yearmonth.substring(5)) <= 5;
//		if (showYearInTax) {
//			int year = Integer.valueOf(yearmonth.substring(0, 4)) - 1;
//			if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {
//				TaxReportVO finTax = new TaxReportVO();
//				finTax.setSb_zlbh(TaxRptConst.SB_ZLBH39806);//小企业财报年报
//				finTax.setPeriodfrom(year + "-01-01");
//				finTax.setPeriodto(year + "-12-31");
//				finTax.setZsxm_dm("C"); // 财报
//				finTax.setPeriodtype(2); // 年报
//				list.add(finTax);
//			} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {
//				TaxReportVO finTax = new TaxReportVO();
//				finTax.setSb_zlbh(TaxRptConst.SB_ZLBH39801);//一般企业财报年报
//				finTax.setPeriodfrom(year + "-01-01");
//				finTax.setPeriodto(year + "-12-31");
//				finTax.setZsxm_dm("C"); // 财报
//				finTax.setPeriodtype(2); // 年报
//				list.add(finTax);
//			}
//		}
//	}
	
	/**
	 * 财报月报
	 */
//	protected void addFinReport2(CorpVO corpvo, String yearmonth, List<TaxReportVO> list) {
//		TaxReportVO detailvo = new TaxReportVO();
//		if (TaxRptConst.KJQJ_2013.equals(corpvo.getCorptype())) {// 2003
//																		// 会计期间
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC1);
//		} else if (TaxRptConst.KJQJ_2007.equals(corpvo.getCorptype())) {// 2007
//																				// 会计期间
//			detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHC2);
//		}
//		
//		String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//		String periodBegin = DateUtils.getPeriodStartDate(sQueryPeriod).toString();
//		String periodEnd = DateUtils.getPeriodEndDate(sQueryPeriod).toString();
//		
//		detailvo.setPeriodfrom(periodBegin);
//		detailvo.setPeriodto(periodEnd);
//		detailvo.setZsxm_dm(ITaxReportConst.SB_ZSDM_CB); // 财报
//		detailvo.setPeriodtype(0);// 月报
//		if (StringUtil.isEmpty(detailvo.getSb_zlbh()))
//			return;
//		list.add(detailvo);
//	}
	
//	protected void addStampReport(CorpVO corpvo, String yearmonth, List<TaxReportVO> list) {
//		TaxReportVO detailvo = new TaxReportVO();
//		String sQueryPeriod = new DZFDate(yearmonth + "-01").getDateBefore(1).toString().substring(0, 7);
//		detailvo.setPeriodfrom(DateUtils.getPeriodStartDate(sQueryPeriod).toString());
//		detailvo.setPeriodto(DateUtils.getPeriodEndDate(sQueryPeriod).toString());
//		detailvo.setSb_zlbh(TaxRptConst.SB_ZLBHD1);
//		detailvo.setZsxm_dm(TaxRptConst.SB_ZLBH_YHS); // 增值税
//		detailvo.setPeriodtype(PeriodType.monthreport); // 月报
//
//		list.add(detailvo);
//	}
	
	protected String getQuarterStartDate(String yearmonth) {
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

	protected String getQuarterEndDate(String yearmonth) {
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

	@Override
	public String[] getCondition(String pk_taxreport, UserVO userVO, TaxReportVO reportvo, SingleObjectBO sbo)
			throws DZFWarpException {

		List<String> listCondition = new ArrayList<String>();
		if (TaxRptConst.SB_ZLBH10101.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10101_beijing.saCheckCondition;
			// 读取报表内容
			SQLParameter params = new SQLParameter();
			params.addParam(reportvo.getPk_taxreport());
			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
					"pk_taxreport = ? and nvl(dr,0) = 0", params);
			HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
			for (TaxReportDetailVO detailvo : vos) {
				hmDetail.put(detailvo.getReportname().trim(), detailvo);
			}

			// 排除公式中含有没有显示报表的公式

			lab1: for (String condition : sacondition) {
				String[] saReportname = getReportNameFromCondition(condition);
				for (String reportname : saReportname) {
					if (hmDetail.containsKey(reportname.trim()) == false) {
						continue lab1;
					}
				}
				listCondition.add(condition);
			}
		} else if (TaxRptConst.SB_ZLBH10102.equals(reportvo.getSb_zlbh())) {
			String[] sacondition = TaxRptChk10102_beijing.saCheckCondition;
			// 读取报表内容
			SQLParameter params = new SQLParameter();
			params.addParam(reportvo.getPk_taxreport());
			TaxReportDetailVO[] vos = (TaxReportDetailVO[]) sbo.queryByCondition(TaxReportDetailVO.class,
					"pk_taxreport = ? and nvl(dr,0) = 0", params);
			HashMap<String, TaxReportDetailVO> hmDetail = new HashMap<String, TaxReportDetailVO>();
			for (TaxReportDetailVO detailvo : vos) {
				hmDetail.put(detailvo.getReportname().trim(), detailvo);
			}

			// 排除公式中含有没有显示报表的公式

			lab1: for (String condition : sacondition) {
				String[] saReportname = getReportNameFromCondition(condition);
				for (String reportname : saReportname) {
					if (hmDetail.containsKey(reportname) == false) {
						continue lab1;
					}
				}
				listCondition.add(condition);
			}
		}
		return listCondition.toArray(new String[0]);
	}

	protected String[] getReportNameFromCondition(String condition) {
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

	@Override
	public Object sendTaxReport(CorpVO corpVO, UserVO userVO, Map objMapReport, SpreadTool spreadtool,
			TaxReportVO reportvo, SingleObjectBO sbo) throws DZFWarpException {
		throw new BusinessException("当前地区不支持上报。");
	}

	@Override
	public void getDeclareStatus(CorpVO corpvo,CorpTaxVo corptaxvo,TaxReportVO reportvo) throws DZFWarpException {
		// throw new BusinessException("当前地区不支持该操作");
	}

	@Override
	public void processObsoleteDeclare(CorpVO corpvo, TaxReportVO reportvo) throws DZFWarpException {
		throw new BusinessException("当前地区不支持该操作");
	}

	@Override
	public String getLocation(CorpVO corpvo) throws DZFWarpException {
		return "通用";
	}

	protected int getPeriodType(String sdatefrom, String sdateto) {
		int iret = PeriodType.yearreport;
		DZFDate datefrom = new DZFDate(sdatefrom);
		DZFDate dateto = new DZFDate(sdateto);
		int iBetween = DZFDate.getDaysBetween(datefrom, dateto);
		if (iBetween <= 31) {
			iret = PeriodType.monthreport;
		} else if (iBetween < 100) {
			iret = PeriodType.jidureport;
		}
		return iret;
	}

	@Override
	public void processZeroDeclaration(TaxReportVO typedetailvo,
			CorpVO corpvo,CorpTaxVo corptaxvo, SingleObjectBO singleObjectBO)
			throws DZFWarpException {
		throw new BusinessException("当前地区暂不支持零申报");
	}
	
	protected void processSaveFile(String jsonString, TaxReportVO reportvo,
			CorpVO corpvo) {
		TaxReportDetailVO[] detailvos = (TaxReportDetailVO[]) reportvo
				.getChildren();
		TaxReportDetailVO detailvo = detailvos[0];
		String sb_zlbh = detailvo.getSb_zlbh();

		DZFDate dzfnow = new DZFDate();
		String sFileDir = TaxReportPath.taxReportPath + "spreadfile/"
				+ String.valueOf(dzfnow.getYear())
				+ String.valueOf(dzfnow.getMonth());
		File f = new File(sFileDir);
		if (f.exists() == false) {
			f.mkdir();
		}
		String filename = sFileDir + "/spread" + reportvo.getPk_taxreport()
				+ sb_zlbh + ".ssjson";
		if (StringUtil.isEmptyWithTrim(detailvo.getSpreadfile()) == false) {
			if (!detailvo.getSpreadfile().trim().startsWith("*")) {// 全部转成文件里面
				filename = detailvo.getSpreadfile().trim();
			}
		}
		String filecont = new SpreadTool().adjustBeforeSave(jsonString, corpvo,
				reportvo);
		try {
			if (!StringUtil.isEmpty(filecont)) {
				String fastid = "";
				for (TaxReportDetailVO detail : detailvos) {
					if (detail.getSb_zlbh().equals(sb_zlbh)) {
						fastid = detail.getSpreadfile();
					}
				}
				String id = "";
				if (!StringUtil.isEmpty(fastid) && fastid.startsWith("*")) {
					((FastDfsUtil) SpringUtils.getBean("connectionPool"))
							.deleteFile(fastid.substring(1));
					log.error("删除成功,文件id:" + fastid.substring(1));
				}
				id = ((FastDfsUtil) SpringUtils.getBean("connectionPool"))
						.upload(filecont.getBytes(), filename,
								new HashMap<String, String>());

				if (!StringUtil.isEmpty(id)) {
					filename = "*" + id.substring(1);
				} else {
					throw new BusinessException("获取文件id失败!");
				}
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		// 更新其他相同申报种类编号的记录行
		reportvo.setSpreadfile(filename);
		for (TaxReportDetailVO detail : detailvos) {
			if (detail.getSb_zlbh().equals(sb_zlbh)) {
				detail.setSpreadfile(filename);
			}
		}
	}

	@Override
	public String checkReportDataWarning(Map mapJson, CorpVO corpvo, TaxReportVO reportvo,
			HashMap<String, TaxReportDetailVO> hmRptDetail, SingleObjectBO sbo) throws DZFWarpException {
		return "";
	}
	@Override
	public TaxPaymentVO[] queryTaxPayment(CorpVO corpvo, TaxReportVO reportvo)
			throws DZFWarpException {
		return null;
	}
	@Override
	public String checkReportList(CorpVO corpvo, CorpTaxVo corptaxvo,List<TaxReportVO> list)
			throws DZFWarpException {
		// TODO Auto-generated method stub
		return null;
	}
}

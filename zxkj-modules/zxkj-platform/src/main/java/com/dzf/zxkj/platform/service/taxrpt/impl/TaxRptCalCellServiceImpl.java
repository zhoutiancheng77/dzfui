package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.PeriodType;
import com.dzf.zxkj.common.constant.TaxRptConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxReportVO;
import com.dzf.zxkj.platform.model.tax.workbench.TaxDeclareResult;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellBVO;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxDeclarationService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptCalCellService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptService;
import com.dzf.zxkj.platform.service.taxrpt.bo.RptBillFactory;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("taxrptcalcellserv")
@Slf4j
public class TaxRptCalCellServiceImpl implements ITaxRptCalCellService {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private ITaxDeclarationService taxDeclarationService;
	@Autowired
	private RptBillFactory rptbillfactory;
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;
	
	@Override
	public TaxRptCalCellVO getTaxRptCalCell(TaxRptCalCellBVO[] params) throws DZFWarpException {
		
		TaxRptCalCellVO hvo = new TaxRptCalCellVO();
		try {
			beforeCheckData(params);
			
			Map<String, List<TaxRptCalCellBVO>> taxMap = taxCalObj(params);
			List<TaxReportVO> reportList = queryTaxReports(params);
			
			Map<String, String> fileMap = taxRptFile(taxMap, reportList);
			calcResult(taxMap, fileMap);
			
			hvo.setChildren(params);//赋值
			hvo.setMsg("查询成功");
			hvo.setSuccess(DZFBoolean.TRUE);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(e instanceof BusinessException){
				hvo.setMsg(e.getMessage());
			}else{
				hvo.setMsg("查询失败");
			}
			
			hvo.setSuccess(DZFBoolean.FALSE);
		}
		
		return hvo;
	}
	
	private List<TaxReportVO> queryTaxReports(TaxRptCalCellBVO[] bvos) throws DZFWarpException{
		StringBuffer sqlf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		
		int count = 0;
		String key;
		Map<String, String> flagMap = new HashMap<String, String>();
		String period;
		String pk_corp;
		for(TaxRptCalCellBVO bvo : bvos){
			pk_corp = bvo.getPk_corp();
			period = bvo.getPeriod();
			key = pk_corp + "_" + period;
			if(flagMap.containsKey(key)){
				continue;//不再往下拼接sql
			}else{
				flagMap.put(key, key);
			}
			
			if(count++ != 0){
				sqlf.append(" or ");
			}
			
			sqlf.append(" ( nvl(dr,0) = 0 and pk_corp = ? and ( period = ? or period = ? ) ) ");
			sp.addParam(bvo.getPk_corp());

			sp.addParam(period);
			sp.addParam(period.substring(0, 4));//获取年
		}
		
		sqlf.append(" order by rowid desc ");
		List<TaxReportVO> reportList = (List<TaxReportVO>) singleObjectBO.executeQuery(sqlf.toString(), sp, 
				new Class[]{TaxReportVO.class});//, TaxReportDetailVO.class
		
		return reportList;
	}
	
	private void calcResult(Map<String, List<TaxRptCalCellBVO>> taxMap, Map<String, String> fileMap){
		if(taxMap == null || taxMap.size() == 0 
				|| fileMap == null || fileMap.size() == 0)
			return;
		
		String reportName = null;
		String x = null;
		String y = null;
		
		List<TaxRptCalCellBVO> list = null;
		String key = null;
		String filepath = null;
		DZFDouble value = null;
		String[] xy = null;
		DZFDouble mny = null;
		for(Map.Entry<String, List<TaxRptCalCellBVO>> entry : taxMap.entrySet()){
			key = entry.getKey();
			list = entry.getValue();
			
			if(list == null || list.size() == 0)
				continue;
			
			filepath = fileMap.get(key);
			if(StringUtil.isEmpty(filepath))
				continue;
			
			for(TaxRptCalCellBVO bvo : list){
				mny = bvo.getMny();//实缴数
				if(mny != null){
					if(TaxRptConst.SB_ZLBH50101.equals(bvo.getSbzlbh())
							|| TaxRptConst.SB_ZLBH50102.equals(bvo.getSbzlbh())){
						continue;
					}else{
						break;
					}
					
				}
				reportName = bvo.getReportname();
				x = bvo.getX();
				y = bvo.getY();
				
				if(isContinue(bvo, x, y)){
					continue;
				}
				
				xy = transCoordinate(x, y);
				value = taxDeclarationService.getQsbbqsData(filepath, reportName, xy[0], xy[1]);
				bvo.setMny(value);
			}
		}
		
	}
	
	private String[] transCoordinate(String x, String y){
		String[] xy = new String[2];
		xy[0] = fromNumSystem26(x) - 1 + "";
		xy[1] = Integer.parseInt(y) - 1 + "";
		
		return xy;
	}
	
	/**
	 * 26进制转化10进制
	 * @param str
	 * @return
	 */
	private static int fromNumSystem26(String str){
		int n = 0;
		
		if(StringUtil.isEmptyWithTrim(str))
			return n;
		
		for(int i = str.length() - 1, j = 1; i >= 0; i--, j *= 26){
			char c = Character.toUpperCase(str.charAt(i));
			
			if(c < 'A' || c > 'Z')
				return 0;
			
			n +=((int) c - 64) * j;
		}
		
		return n;
	}
	
	private boolean isContinue(TaxRptCalCellBVO bvo, String x, String y){
		
		if(StringUtil.isEmpty(x) || StringUtil.isEmpty(y)){
			return true;
		}
		
		return false;
	}
	
	private Map<String, String> taxRptFile(Map<String, List<TaxRptCalCellBVO>> taxMap,
			List<TaxReportVO> reportList){
		Map<String, String> fileMap = new HashMap<String, String>();
		
		if(taxMap == null 
				|| taxMap.size() == 0
				|| reportList == null 
				|| reportList.size() == 0)
			return fileMap;
		
		TaxReportVO tvo = null;
		List<TaxRptCalCellBVO> list = null;
		String spreadfile = null;
		boolean flag = false;//如果表中已存实缴额，那么其他的vo没必要取值了
		for(int i = 0; i < reportList.size(); i++){//构造文件路径map
			tvo = reportList.get(i);
			
			if (tvo.getSbzt_dm() != null
					&& Integer.valueOf(tvo.getSbzt_dm()).intValue() != TaxRptConst.iSBZT_DM_UnSubmit) {
				// 已申报更新申报状态
				try {
					CorpVO corpvo = getCorpVO(tvo.getPk_corp());
					CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(tvo.getPk_corp());
					ITaxRptService rptService = rptbillfactory.produce(corptaxvo);
					rptService.getDeclareStatus(corpvo,corptaxvo, tvo);
				} catch (Exception e) {
					log.error("更新申报状态失败", e);
				}
			}
			
			StringBuffer sf = new StringBuffer();
			list = getRptCalCellListByKey(tvo, taxMap, sf);
			
			
			if(list == null || list.size() == 0)
				continue;
			
			spreadfile = (String)tvo.getAttributeValue("spreadfile");
			
			flag = true;
			for(TaxRptCalCellBVO bvo : list){
				bvo.setSbzt_dm(tvo.getSbzt_dm());//更新状态
				bvo.setTxstatus(StringUtil.isEmpty(spreadfile) ? 98 : 99);//填写状态  98未填写  99已填写
				if(flag && (TaxRptConst.SB_ZLBHGS.equals(tvo.getSb_zlbh())
						|| tvo.getTaxmny() != null)){//为适应老数据
					bvo.setMny(tvo.getTaxmny());
					flag = false;
				}
			}
			
			fileMap.put(sf.toString(), spreadfile);
			
		}
		
		return fileMap;
	}
	
	private List<TaxRptCalCellBVO> getRptCalCellListByKey(TaxReportVO tvo, 
			Map<String, List<TaxRptCalCellBVO>> taxMap,
			StringBuffer sf){
		List<TaxRptCalCellBVO> list = null;
		String key = null;
		String pk_corp = tvo.getPk_corp();
		String sb_zlbh = tvo.getSb_zlbh();
		int periodType = tvo.getPeriodtype();
		
		key = buildKey(new String[]{ pk_corp, tvo.getPeriod(), sb_zlbh });
		list = taxMap.get(key);
		if(list != null && list.size() > 0){
			sf.append(key);
			return list;
		}
		
		if(periodType == PeriodType.monthreport && sb_zlbh.length() > 2){
			key = buildKey(new String[]{ pk_corp, tvo.getPeriod(), sb_zlbh.substring(0, sb_zlbh.length() - 2) });
			list = taxMap.get(key);
			
			if(list != null && list.size() > 0){
				sf.append(key);
				return list;
			}
		}
		
		return list;
	}
	
	private String buildKey(String[] arr){
		StringBuffer sf = new StringBuffer();
		for(int i = 0; i < arr.length; i++){
			sf.append(arr[i]);
			
			if(i != arr.length - 1){
				sf.append("_");
			}
			
		}
		
		return sf.toString();
	}
	
	private static String[][] getInstance(){
		return new String[][]{
			{"pk_corp", "公司"},
			{"period", "期间"},
//			{"periodtype", "申报周期"},
			{"sbzlbh", "申报种类编号"}
		};
	}
	
	private Map<String, List<TaxRptCalCellBVO>> taxCalObj(TaxRptCalCellBVO[] bvos){
		Map<String, List<TaxRptCalCellBVO>> map = new HashMap<String, List<TaxRptCalCellBVO>>();
		String[][] mapping = getInstance();
		StringBuffer sf = new StringBuffer();
		
		String value = null;
		String key = null;
		List<TaxRptCalCellBVO> list = null;
		for(TaxRptCalCellBVO bvo : bvos){//
			
			sf.setLength(0);//重置
			for(int i = 0; i < mapping.length; i++){
				value = (String) bvo.getAttributeValue(mapping[i][0]);
				if(StringUtil.isEmpty(value)){
					throw new BusinessException(mapping[i][1] + "参数为空，请检查");
				}
				
				sf.append(value);
				
				if(i != mapping.length - 1){
					sf.append("_");
				}
				
			}
			
			key = sf.toString();
			if(map.containsKey(key)){
				list = map.get(key);
				list.add(bvo);
			}else{
				list = new ArrayList<TaxRptCalCellBVO>();
				list.add(bvo);
				map.put(key, list);
			}
			
		}
	
		return map;
	}

	private void beforeCheckData(TaxRptCalCellBVO[] params) throws DZFWarpException{
		if(params == null || params.length == 0)
			throw new BusinessException("参数为空，请检查");
		
		if(params.length > 100)
			throw new BusinessException("列表数据过多，请检查");
	}

	@Override
	public List<TaxDeclareResult> zeroDeclaration(List<String> corps, List<String> zsxm_dms,
												  String period, String userid) {
		List<TaxDeclareResult> rsList = new ArrayList<TaxDeclareResult>();
		for (String pk_corp : corps) {
			CorpVO corpvo = getCorpVO(pk_corp);
			CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
			if (StringUtil.isEmpty(corpvo.getVsoccrecode())) {
				addResult(corpvo, rsList, zsxm_dms, "纳税人识别号为空");
				continue;
			}

			if (StringUtil.isEmpty(taxvo.getVstatetaxpwd())) {
				addResult(corpvo, rsList, zsxm_dms, "纳税密码为空");
				continue;
			}
			ITaxRptService rptService = rptbillfactory.produce(taxvo);
			List<TaxReportVO> typelist = null;
			Map<String, TaxReportVO> zsxmMap = new HashMap<String, TaxReportVO>();
			try {
				typelist = rptService.getTypeList(corpvo, taxvo,period, userid,
						new DZFDate().toString(), singleObjectBO);
				for (TaxReportVO taxTypeListDetailVO : typelist) {
					String zsxm = taxTypeListDetailVO.getZsxm_dm();
					if (zsxm_dms.contains(zsxm)) {
						if (zsxmMap.containsKey(zsxm)) {
							if ("一般纳税人".equals(corpvo.getChargedeptname())
									&& taxTypeListDetailVO.getPeriodtype() == PeriodType.monthreport) {
								// 一般人月报
								zsxmMap.put(zsxm, taxTypeListDetailVO);
							}
						} else {
							zsxmMap.put(zsxm, taxTypeListDetailVO);
						}
					}
				}
			} catch (Exception e) {
				String errorMsg = "初始化征收项目失败";
				if (e instanceof BusinessException) {
					errorMsg += "，" + e.getMessage();
				} else {
					log.error(errorMsg, e);
				}
				addResult(corpvo, rsList, zsxm_dms, errorMsg);
				continue;
			}
			for (String zsxm_dm : zsxm_dms) {
				TaxDeclareResult rs = new TaxDeclareResult();
				try {
					TaxReportVO rptvo = zsxmMap.get(zsxm_dm);
					if (rptvo == null) {
						throw new BusinessException("没有该应征收项目");
					}
					rptService.processZeroDeclaration(rptvo,corpvo,taxvo, singleObjectBO);
					rs.setPk_corp(corpvo.getPk_corp());
					rs.setUnitname(corpvo.getUnitname());
					rs.setZsxm_dm(zsxm_dm);
					rs.setMsg("提交成功");
					rs.setSuccess(true);
				} catch (Exception e) {
					rs.setPk_corp(corpvo.getPk_corp());
					rs.setUnitname(corpvo.getUnitname());
					rs.setZsxm_dm(zsxm_dm);
					if (e instanceof BusinessException) {
						rs.setMsg(e.getMessage());
					} else {
						log.error("零申报失败", e);
						rs.setMsg("提交失败");
					}
				}
				rsList.add(rs);
			}
		}
		return rsList;
	}
	
	private void addResult(CorpVO corp, List<TaxDeclareResult> rsList, List<String> zsxm_dms,String msg) {
		for (String  zsxm_dm : zsxm_dms) {
			TaxDeclareResult rs = new TaxDeclareResult();
			rs.setMsg(msg);
			rs.setPk_corp(corp.getPk_corp());
			rs.setUnitname(corp.getUnitname());
			rs.setSuccess(false);
			rs.setZsxm_dm(zsxm_dm);
			rsList.add(rs);
		}
	}
	
	private CorpVO getCorpVO(String pk_corp) {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		SuperVO[] corpvos = QueryDeCodeUtils.decKeyUtils(new String[] { "unitname" }, new SuperVO[] { corpvo }, 1);
		return (CorpVO) corpvos[0];
	}
}

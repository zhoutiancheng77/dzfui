package com.dzf.zxkj.platform.service.glic.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.ICrkMxService;
import com.dzf.zxkj.platform.service.glic.IInventoryQcService;
import com.dzf.zxkj.platform.service.pzgl.impl.QueryVoucher;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;


@Service("gl_rep_crkmxserv")
public class CrkMxServiceImpl implements ICrkMxService {
	
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IInventoryQcService gl_ic_invtoryqcserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accService;


	@Override
	public Map<String, List<IcDetailVO>> queryMx(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException {
		//转换为对应的map
		Map<String, AuxiliaryAccountBVO> fzhsmap = getFzhsMap(paramvo);
		
		Map<String, InventoryQcVO> qcmap = getQcMx(paramvo,fzhsmap);//期初值
		
		List<IcDetailVO> fslist = queryFsDetails(paramvo, corpvo);
		
		//期初=录入的期初余额+查询期间前的金额
		upfsToQcMap(fslist,qcmap,paramvo,fzhsmap);
		
		//赋值本期，本年累计值
		fslist = addBqBnVo(fslist,paramvo,fzhsmap);
		
		//处理发生数据
		fslist =  handFslsit(fslist,paramvo);
		
		
		//合并数据
		Map<String, List<IcDetailVO>> resmap = new HashMap<String, List<IcDetailVO>>();
		
		putResMap(fslist,qcmap,resmap,paramvo);
		
		//赋值名称
		Map<String, String[]> spbmmap = new HashMap<String, String[]>();
		
		calSpmc(corpvo, resmap,spbmmap);
		
		//计算结存金额
		caljcMny(resmap);
		
		//赋值价格信息
		calPrice(resmap);
		
		//排序resmap(根据编码排序)
		resmap = sortMap(resmap, spbmmap);
		
		return resmap;
	}
	private Map<String, AuxiliaryAccountBVO> getFzhsMap(QueryParamVO paramvo) {
		Map<String, AuxiliaryAccountBVO> fzhsmap = new HashMap<String, AuxiliaryAccountBVO>();
		//查询所有的存货
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryAllBByLb(paramvo.getPk_corp(), "6");
		
//		//查询所有的科目
//		Map<String, YntCpaccountVO> cpamap = AccountCache.getInstance().getMap("", paramvo.getPk_corp());
		
		if(bvos!=null && bvos.length>0){
			YntCpaccountVO cpavo = null;
			for(AuxiliaryAccountBVO bvo:bvos){
				fzhsmap.put(bvo.getPk_auacount_b(), bvo);
//				if(!StringUtil.isEmpty(bvo.getKmclassify())
//						&& cpamap.containsKey(bvo.getKmclassify()) ){
//					cpavo = cpamap.get(bvo.getKmclassify());
//					bvo.setKmclassifyname(cpavo.getAccountname());
//					bvo.setKmclassifycode(cpavo.getAccountcode());
//				}
			}
		}
		return fzhsmap;
	}
	private Map<String, List<IcDetailVO>> sortMap(Map<String , List<IcDetailVO>> map,final Map<String, String[]> spbmmap){
		
		
		if(map == null || map.size() == 0){
			return null;
		}
		
		Map<String, List<IcDetailVO>> sortMap = new TreeMap<String, List<IcDetailVO>>(new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				if(!StringUtil.isEmpty(o1) && !StringUtil.isEmpty(o2)){
					String[] bm1 = spbmmap.get(o1);
					
					String[] bm2 = spbmmap.get(o2);
					
					if(bm1 ==null || bm2 == null){
						return 0;
					}
					int i =0;
					i = bm1[0].compareTo(bm2[0]);//先按照列表排序
					if(i == 0){
						i = bm1[1].compareTo(bm2[1]);//按照辅助编码排序
					}
					
					
					return i;
					
				}
				return 0;
			}
		});
		
		sortMap.putAll(map);
		
		return sortMap;
	}
	
	
	
	/**
	 * 添加本期和本年数据
	 * @param 'resmap'
	 */
	private List<IcDetailVO> addBqBnVo(List<IcDetailVO> listvos,QueryParamVO paramVO,Map<String, AuxiliaryAccountBVO> fzhsmap) {
		Map<String, List<IcDetailVO>> resmap = new HashMap<String, List<IcDetailVO>>();
		if(listvos!=null && listvos.size()>0){
			IcDetailVO lbvo = null;
			String lbid = "";
			for(IcDetailVO vo:listvos){
				//商品
				if(resmap.containsKey(vo.getPk_sp())){
					resmap.get(vo.getPk_sp()).add(vo);
				}else{
					List<IcDetailVO> tlist = new ArrayList<IcDetailVO>();
					tlist.add(vo);
					resmap.put(vo.getPk_sp(), tlist);
				}
				//商品分类
				if(fzhsmap.get(vo.getPk_sp())!=null && !StringUtil.isEmpty(fzhsmap.get(vo.getPk_sp()).getKmclassify()) ){
					lbid = fzhsmap.get(vo.getPk_sp()).getKmclassify()+"_fl";
					lbvo = new IcDetailVO();
					BeanUtils.copyNotNullProperties(vo, lbvo);
					lbvo.setPk_sp(lbid);
					if(resmap.containsKey(lbid)){
						resmap.get(lbid).add(lbvo);
					}else{
						List<IcDetailVO> tlist = new ArrayList<IcDetailVO>();
						tlist.add(lbvo);
						resmap.put(lbid, tlist);
					}
				}
			}
		}
		//获取所有的期间
		List<String> periods = ReportUtil.getPeriods(paramVO.getBegindate1(), paramVO.getEnddate());
		
		if (resmap != null && resmap.size() > 0) {
			for (Entry<String, List<IcDetailVO>> entry : resmap.entrySet()) {
				List<IcDetailVO> values = entry.getValue();
				Map<String, DZFDouble> bqsrnum = new HashMap<String, DZFDouble>();// 本期数量(收入)
				Map<String, DZFDouble> bqsrje = new HashMap<String, DZFDouble>();// 本期金额(收入)
				Map<String, DZFDouble> bqfcnum = new HashMap<String, DZFDouble>();// 本期数量(发出)
				Map<String, DZFDouble> bqfcje = new HashMap<String, DZFDouble>();// 本期金额(发出)
				Map<String, DZFDouble> bnsrnum = new HashMap<String, DZFDouble>();// 本年数量(收入)
				Map<String, DZFDouble> bnsrje = new HashMap<String, DZFDouble>();// 本年金额(收入)
				Map<String, DZFDouble> bnfcnum = new HashMap<String, DZFDouble>();// 本年数量(发出)
				Map<String, DZFDouble> bnfcje = new HashMap<String, DZFDouble>();// 本年金额(发出)
				if (values != null && values.size() > 0) {
					String qj = "";
					String year = "";
					List<IcDetailVO> reslist = new ArrayList<IcDetailVO>();
					for (int i = 0; i < values.size(); i++) {
						qj = DateUtils.getPeriod(values.get(i).getDbilldate());
						
						year = qj.substring(0, 4);
						
						//收入
						putMap(values, bqsrnum, qj, values.get(i).getSrsl());
						putMap(values, bqsrje, qj, values.get(i).getSrje());
						
						//发出
						putMap(values, bqfcnum, qj, values.get(i).getFcsl());
						putMap(values, bqfcje, qj, values.get(i).getFcje());
						
						//本年收入
						putMap(values, bnsrnum, year, values.get(i).getSrsl());
						putMap(values, bnsrje, year, values.get(i).getSrje());
						
						//本年支出
						putMap(values, bnfcnum, year, values.get(i).getFcsl());
						putMap(values, bnfcje, year, values.get(i).getFcje());
						
						reslist.add(values.get(i));
						
					}
					
					//根据期间赋值
					for (int i = 0; i < periods.size(); i++) {
						//本期，本年计算
						addBqBnVo1(bqsrnum, bqsrje, bqfcnum, bqfcje, bnsrnum, bnsrje, bnfcnum, bnfcje, 
								reslist, entry.getKey(),periods,i);
					}
					
					//排序list数据
					Collections.sort(reslist, new Comparator<IcDetailVO>() {
						@Override
						public int compare(IcDetailVO o1, IcDetailVO o2) {
							// h.period,b.vicbillcodetype,h.vicbillcode,b.rowno
							int i = 0;
							i = o1.getDbilldate().compareTo(o2.getDbilldate());
							if(i == 0){
								if ("期初余额".equals(o1.getZy()) && ReportUtil.bSysZy(o1))
									i = 1;
								else if ("本期合计".equals(o1.getZy()) && ReportUtil.bSysZy(o1))
									i = 3;
								else if ("本年累计".equals(o1.getZy()) && ReportUtil.bSysZy(o1))
									i = 4;
							}
							return i;
						}
					});
					
					resmap.put(entry.getKey(), reslist);
				}
			}
		}
		
		List<IcDetailVO> reslist1 = new ArrayList<IcDetailVO>();
		if(resmap.size()>0){
			for(Entry<String, List<IcDetailVO>> entry:resmap.entrySet()){
				reslist1.addAll(entry.getValue());
			}
		}
		
		return reslist1;
	}


	private void addBqBnVo1(Map<String, DZFDouble> bqsrnum, Map<String, DZFDouble> bqsrje,
			Map<String, DZFDouble> bqfcnum, Map<String, DZFDouble> bqfcje, Map<String, DZFDouble> bnsrnum,
			Map<String, DZFDouble> bnsrje, Map<String, DZFDouble> bnfcnum, Map<String, DZFDouble> bnfcje,
			List<IcDetailVO> reslist, String key,List<String> periods,int i) {
		String qj = periods.get(i);
		IcDetailVO bqhj = new IcDetailVO();
		bqhj.setZy("本期合计");
		bqhj.setBsyszy(DZFBoolean.TRUE);
		bqhj.setPk_sp(key);
		bqhj.setPzh("");
		bqhj.setVicbillcode("");
		bqhj.setDbilldate(DateUtils.getPeriodEndDate(qj));
		bqhj.setSrsl(bqsrnum.get(qj));
		bqhj.setSrje(bqsrje.get(qj));
		bqhj.setFcsl(bqfcnum.get(qj));
		bqhj.setFcje(bqfcje.get(qj));
		reslist.add(bqhj);
		if(qj.substring(5, 7).equals("12") || i == periods.size()-1){
			IcDetailVO bnhj = new IcDetailVO();
			bnhj.setZy("本年累计");
			bnhj.setBsyszy(DZFBoolean.TRUE);
			bnhj.setPk_sp(key);
			bnhj.setPzh("");
			bnhj.setVicbillcode("");
			bnhj.setDbilldate(DateUtils.getPeriodEndDate(qj));
			bnhj.setSrsl(bnsrnum.get(qj.substring(0, 4)));
			bnhj.setSrje(bnsrje.get(qj.substring(0, 4)));
			bnhj.setFcsl(bnfcnum.get(qj.substring(0, 4)));
			bnhj.setFcje(bnfcje.get(qj.substring(0, 4)));
			reslist.add(bnhj);
		}
	}


	private void putMap(List<IcDetailVO> values, Map<String, DZFDouble> map,
			String qj, DZFDouble value) {
		if(map.containsKey(qj)){
			map.put(qj, SafeCompute.add(map.get(qj), value));
		}else{
			map.put(qj, value);
		}
	}


	private void caljcMny(Map<String, List<IcDetailVO>> resmap) {
		if(resmap!=null && resmap.size()>0){
			List<IcDetailVO> volist = null;
			for(Entry<String, List<IcDetailVO>> entry:resmap.entrySet()){
				volist = entry.getValue();
				if(volist!=null && volist.size()>0){
					DZFDouble jcnum = DZFDouble.ZERO_DBL;//结存数量
					DZFDouble jcmny = DZFDouble.ZERO_DBL;//结存金额
					for(int i =0;i<volist.size();i++){
						if(i == 0){//0是期初金额
							if(!("本期合计".equals(volist.get(i).getZy()) && ReportUtil.bSysZy(volist.get(i)))
									&& !("本年累计".equals(volist.get(i).getZy()) && ReportUtil.bSysZy(volist.get(i))) ){
								jcnum = VoUtils.getDZFDouble(volist.get(i).getJcsl());
								jcmny = VoUtils.getDZFDouble(volist.get(i).getJcje());
							}
						}else{
							if(!("本期合计".equals(volist.get(i).getZy()) && ReportUtil.bSysZy(volist.get(i)))
									&& !("本年累计".equals(volist.get(i).getZy()) && ReportUtil.bSysZy(volist.get(i))) ){
								jcnum = jcnum.add(SafeCompute.sub(volist.get(i).getSrsl(), volist.get(i).getFcsl()));
								jcmny = jcmny.add(SafeCompute.sub(volist.get(i).getSrje(), volist.get(i).getFcje()));
							}else{
//								System.out.println(jcnum);
							}
							volist.get(i).setJcsl(jcnum);
							volist.get(i).setJcje(jcmny);
						}
					}
				}
			}
		}
		
	}


	/**
	 * 根据期初，计算发生
	 * 
	 * @param 'key'
	 * @param 'qcVo'
	 * @param'periodBf'
	 * @param 'period'
	 * @param 'account'
	 * @return
	 */

	private void calPrice(Map<String, List<IcDetailVO>> result) {
		if (result == null) {
			return;
		}
		List<IcDetailVO> volist = null;
		DZFDouble qcje = null;
		DZFDouble qcsl = null;
		DZFDouble qcdj = null;
		DZFDouble srje = null;
		DZFDouble srsl = null;
		DZFDouble srdj = null;
		DZFDouble fcje = null;
		DZFDouble fcsl = null;
		DZFDouble fcdj = null;
		DZFDouble jcje = null;
		DZFDouble jcsl = null;
		DZFDouble jcdj = null;
		for (Map.Entry<String, List<IcDetailVO>> entry : result.entrySet()) {
			volist = entry.getValue();
			if(volist!=null &&volist.size()>0){
				for(IcDetailVO vo:volist){
					
					qcje = vo.getQcje();
					qcsl = vo.getQcsl();
					qcdj = SafeCompute.div(qcje, qcsl);
					vo.setQcdj(qcdj);
					
					srje = vo.getSrje();
					srsl = vo.getSrsl();
					srdj = SafeCompute.div(srje, srsl);
					if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
						vo.setSrdj(srdj);
					}else if(srje!=null && srje.doubleValue()!=0){
						vo.setSrdj(vo.getNprice());
					}
					
					fcje = vo.getFcje();
					fcsl = vo.getFcsl();
					fcdj = SafeCompute.div(fcje, fcsl);
//					if (StringUtil.isEmpty(vo.getPk_tzpz_h())) {
						vo.setFcdj(fcdj);
//					}else if(fcje!=null && fcje.doubleValue()!=0){
//						vo.setFcdj(vo.getNprice());
//					}
					
					jcje = vo.getJcje();
					jcsl = vo.getJcsl();
					jcdj = SafeCompute.div(jcje, jcsl);
					vo.setJcdj(jcdj);
				}
				
			}
			
		}
	}
	
	private void putResMap(List<IcDetailVO> fslist, Map<String, InventoryQcVO> qcmap, Map<String, List<IcDetailVO>> resmap,QueryParamVO paramvo) {
		//期初赋值
		if(qcmap!=null && qcmap.size()>0){
			IcDetailVO tvo = null;
			List<IcDetailVO> tlist= null;
			for(Entry<String, InventoryQcVO> entry:qcmap.entrySet()){
				tvo = new IcDetailVO();
				tlist = new ArrayList<IcDetailVO>();
				tvo.setZy("期初余额");
				tvo.setBsyszy(DZFBoolean.TRUE);
				tvo.setPk_sp(entry.getValue().getPk_inventory());
				tvo.setJcsl(entry.getValue().getMonthqmnum());
				tvo.setJcje(entry.getValue().getThismonthqc());
				tlist.add(tvo);
				resmap.put(entry.getKey(), tlist);
			}
		}
		
		//发生赋值
		if(fslist!=null && fslist.size()>0){
			for(IcDetailVO vo:fslist){
				if(resmap.containsKey(vo.getPk_sp())){
					resmap.get(vo.getPk_sp()).add(vo);
				}else{//赋值0期初
					List<IcDetailVO> tlist = new ArrayList<IcDetailVO>();
					IcDetailVO qcvo = new IcDetailVO();
					qcvo.setZy("期初余额");
					qcvo.setBsyszy(DZFBoolean.TRUE);
					qcvo.setPk_sp(vo.getPk_sp());
					tlist.add(qcvo);
					tlist.add(vo);
					resmap.put(vo.getPk_sp(), tlist);
				}
			}
		}
		
		if(resmap!=null && resmap.size()>0){
			for(Entry<String, List<IcDetailVO>> entry:resmap.entrySet()){
				List<IcDetailVO> volist = entry.getValue();
				if(volist!=null && volist.size()>0){
					if(volist.size() == 1){
						volist.get(0).setDbilldate(paramvo.getBegindate1());
					}else{//期初日期
						volist.get(0).setDbilldate(
								DateUtils.getPeriodStartDate(volist.get(1).getDbilldate().toString().substring(0, 7)) );
					}
				}
			}
		}
		
	}

	private void calSpmc(CorpVO corpvo, Map<String, List<IcDetailVO>> result,
			Map<String,String[]>  spmcmap){
		
		if(result == null || result.size() == 0)
			return;
		
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(
				AuxiliaryConstant.ITEM_INVENTORY, corpvo.getPk_corp(), null);
		
//		YntCpaccountVO[] cpavos = AccountCache.getInstance().get("", corpvo.getPk_corp());
		Map<String, YntCpaccountVO> cpamap = accService.queryMapByPk(corpvo.getPk_corp());
		
		if(bvos == null || bvos.length == 0)
			return;
		
		String key;
		List<IcDetailVO> iclistvo;
		//存货辅助核算
		for(AuxiliaryAccountBVO bvo : bvos){
			key = bvo.getPk_auacount_b();
			if(result.containsKey(key)){
				iclistvo = result.get(key);
				for(IcDetailVO icvo:iclistvo){
					icvo.setSpbm(bvo.getCode());
					icvo.setSpmc(bvo.getName());
					icvo.setJldw(bvo.getUnit());
					icvo.setSpfl(bvo.getKmclassify());//商品分类
					icvo.setSpgg(bvo.getSpec());//规格型号
					icvo.setSpfl_name(cpamap.get(bvo.getKmclassify()).getAccountname());
					if(cpamap.containsKey(bvo.getKmclassify())){
						spmcmap.put(icvo.getPk_sp(),new String[]{cpamap.get(bvo.getKmclassify()).getAccountcode(),bvo.getCode()});
					}else{
						spmcmap.put(icvo.getPk_sp(),new String[]{"",bvo.getCode()} );
					}
				}
			}
		}
		//存货大类
		YntCpaccountVO cpavo;
		for(Map.Entry<String, YntCpaccountVO> entry : cpamap.entrySet()){
			cpavo = entry.getValue();
			key = cpavo.getPk_corp_account()+"_fl";
			if(result.containsKey(key)){
				iclistvo = result.get(key);
				for(IcDetailVO icvo:iclistvo){
					icvo.setSpbm(cpavo.getAccountcode());
//					icvo.setSpmc(cpavo.getAccountname());//商品
					icvo.setJldw("");
					icvo.setSpfl(cpavo.getPk_corp_account());//商品分类
					icvo.setSpfl_name(cpavo.getAccountname());//商品分类名称
					icvo.setSpgg("");//规格型号
					spmcmap.put(icvo.getPk_sp(), new String[]{cpavo.getAccountcode(),""});
				}
			}
		}
	}
	
	private List<IcDetailVO> handFslsit(List<IcDetailVO> fslist,QueryParamVO paramvo) {

		List<IcDetailVO> reslist = new ArrayList<IcDetailVO>();
		
		if (fslist != null && fslist.size() > 0) {
			for(IcDetailVO vo:fslist){
				if(StringUtil.isEmpty(vo.getPk_tzpz_h())
						&& !"本期合计".equals(vo.getZy()) && !"本年累计".equals(vo.getZy())
						){//过滤期初的
					continue;
				}
				if (paramvo.getBegindate1().before(vo.getDbilldate())
						|| paramvo.getBegindate1().equals(vo.getDbilldate())) {
					reslist.add(vo);
				}
			}
		}
		
		return reslist;
	}

	private void upfsToQcMap(List<IcDetailVO> fslist, Map<String, InventoryQcVO> qcmap,
			QueryParamVO paramvo,Map<String, AuxiliaryAccountBVO> fzhsmap) {
		
		if(paramvo.getBegindate1() == null){
			throw new BusinessException("查询开始日期为空");
		}
		
		if(fslist!=null && fslist.size()>0){
			for(IcDetailVO vo:fslist){
				if(StringUtil.isEmpty(vo.getPk_tzpz_h())){
					continue;
				}
				//判断期初数据(凭证日期在查询日期前)
				if(vo.getDbilldate()!=null && vo.getDbilldate().compareTo(paramvo.getBegindate1())<0){
					String key = vo.getPk_sp();
					//商品本身
					upfsToQcMap1(qcmap, vo, key);
					//商品对应的分类
					if(fzhsmap.get(key)!=null && !StringUtil.isEmpty(fzhsmap.get(key).getKmclassify()) ){
						key = fzhsmap.get(key).getKmclassify()+"_fl";//分类id
						upfsToQcMap1(qcmap, vo, key);
					}
				}
			}
		}
		
	}
	private void upfsToQcMap1(Map<String, InventoryQcVO> qcmap, IcDetailVO vo, String key) {
		InventoryQcVO fzqcvo = null;
		if (qcmap.containsKey(key)) {
			fzqcvo = qcmap.get(key);
		}else{
			fzqcvo = new InventoryQcVO();
			fzqcvo.setPk_inventory(key);//(关键)
		}
		//期初+借方-贷方
		fzqcvo.setThismonthqc(VoUtils.getDZFDouble(fzqcvo.getThismonthqc()).add(SafeCompute.sub(vo.getJfmny(), vo.getDfmny())));
		if(VoUtils.getDZFDouble(vo.getJfmny()).doubleValue()!=0){
			fzqcvo.setMonthqmnum(SafeCompute.add(fzqcvo.getMonthqmnum(), vo.getNnum()));
		}else{
			fzqcvo.setMonthqmnum(SafeCompute.sub(fzqcvo.getMonthqmnum(), vo.getNnum()));
		}
		qcmap.put(key, fzqcvo);
	}

	private List<IcDetailVO> queryFsDetails(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		//取总账存货　启用日期
		DZFDate jzDate =  gl_ic_invtoryqcserv.queryInventoryQcDate(corpvo.getPk_corp());
		if(jzDate == null){
			jzDate = corpvo.getBegindate();//取建账日期
		}		
		
		//------期初发生数据-----------
		String year = jzDate.getYear()+"";
		sf.append(" select '"+year+"-01-01' as dbilldate,'1900-01' as period,c.pk_inventory as pk_sp, ");
		sf.append("  0 as nnum,c.yearjffse as jfmny,c.yeardffse as dfmny,'in' as vicbillcodetype,'' as pzh,'' as zy  ,'' as pk_tzpz_h,'' as vicbillcode,0 as nprice, ");
		sf.append(" c.bnfsnum jfnum,c.bndffsnum  dfnum ");
		sf.append(" from ynt_glicqc c  ");
		sf.append(" left join  ynt_fzhs_b fzb1 on c.pk_inventory = fzb1.pk_auacount_b ");
		sf.append(" where nvl(c.dr,0)=0 and c.pk_corp = ?  and c.pk_inventory is not null  ");
		sp.addParam(paramvo.getPk_corp());
		if(!StringUtil.isEmpty(paramvo.getPk_inventory())){
			sf.append("  and c.pk_inventory = ?  ");
			sp.addParam(paramvo.getPk_inventory());
		}
		if(!StringUtil.isEmpty(paramvo.getXmlbid())){
			sf.append(" and fzb1.kmclassify = ? ");
			sp.addParam(paramvo.getXmlbid());
		}
		sf.append(" union all");
		
		//---------凭证数据-------
		sf.append(" select * from ( ");
		sf.append(" Select h.doperatedate dbilldate, h.period, case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end pk_sp, ");
		sf.append(" b.glchhsnum nnum,  b.glcgmny as jfmny, b.xsjzcb as dfmny, b.vicbillcodetype,h.pzh,b.zy,h.pk_tzpz_h,h.vicbillcode ,b.nprice, ");
		sf.append(" 0 as jfnum,0  dfnum ");
		sf.append(" From ynt_tzpz_b b join ynt_tzpz_h h on h.pk_tzpz_h = b.pk_tzpz_h  ");
		sf.append(" left join ynt_fzhs_b fzb on (b.fzhsx6 || b.pk_inventory )= fzb.pk_auacount_b  ");
		sf.append(" Where b.pk_corp = ? and nvl(b.dr,0) = 0 and nvl(h.dr, 0) = 0 and b.vicbillcodetype in ('in', 'out') ");
		sf.append(" and h.period >= ? and h.period <= ?   ");
		if(!StringUtil.isEmpty(paramvo.getXmlbid()) ){
			sf.append(" and fzb.kmclassify = ? ");
		}
		sf.append(" order by h.period, h.vicbillcode,b.rowno  ");
		sf.append(" )  tt ");
		sf.append(" where  tt.pk_sp is not null  ");
		if(!StringUtil.isEmpty(paramvo.getPk_inventory())){
			sf.append(" and tt.pk_sp = ? ");
		}
		
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(DateUtils.getPeriod(jzDate));
		sp.addParam(DateUtils.getPeriod(paramvo.getEnddate()));
		if(!StringUtil.isEmpty(paramvo.getXmlbid()) ){
			sp.addParam(paramvo.getXmlbid());
		}
		if(!StringUtil.isEmpty(paramvo.getPk_inventory())){
			sp.addParam(paramvo.getPk_inventory());
		}
		
		List<IcDetailVO> list = (List<IcDetailVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(IcDetailVO.class));
		
		convertData(list);
		return list;
	}
	
	private void convertData(List<IcDetailVO> list) {
		if(list == null || list.size() == 0)
			return;
		
		for(IcDetailVO vo : list){
			if("in".equals(vo.getVicbillcodetype())){
				vo.setSrsl(vo.getNnum());
				vo.setSrje(SafeCompute.add(vo.getJfmny(), vo.getDfmny()));
				if(StringUtil.isEmpty(vo.getPk_tzpz_h())){//期初
					vo.setSrje(vo.getJfmny());
					vo.setFcje(vo.getDfmny());
					vo.setSrsl(vo.getJfnum());
					vo.setFcsl(vo.getDfnum());
				}
			}else if("out".equals(vo.getVicbillcodetype())){
				vo.setFcsl(vo.getNnum());
				vo.setFcje(SafeCompute.add(vo.getJfmny(), vo.getDfmny()));
			} 
			
			
		}
	}
	
	/**
	 * 查询期初数据
	 * 
	 * @param paramVo
	 * @return
	 */
	private Map<String, InventoryQcVO> getQcMx(QueryParamVO paramVo,Map<String, AuxiliaryAccountBVO> fzhsmap) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramVo.getPk_corp());
		StringBuffer sf = new StringBuffer();
		sf.append(" select y.* from ynt_glicqc y ");
		sf.append(" left join ynt_fzhs_b b on y.pk_inventory = b.pk_auacount_b ");
		sf.append(" Where y.pk_corp = ? and nvl(y.dr,0) =0 and y.pk_inventory is not null ");
		sf.append(" and nvl(b.dr,0)=0 ");
		if(!StringUtil.isEmpty( paramVo.getPk_inventory())){
			sf.append(" and y.pk_inventory =? ");
			sp.addParam(paramVo.getPk_inventory());
		}
		if(!StringUtil.isEmpty(paramVo.getXmlbid())){
			sf.append(" and b.kmclassify = ? ");
			sp.addParam(paramVo.getXmlbid());
		}
		
		List<InventoryQcVO> fzrs = (List<InventoryQcVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(InventoryQcVO.class));
		
		Map<String, InventoryQcVO> qcMap = hashlizeObject(fzrs,fzhsmap);

		return qcMap;
	}
	
	private Map<String, InventoryQcVO> hashlizeObject(List<InventoryQcVO> fzrs,Map<String, AuxiliaryAccountBVO> fzhsmap) {
		Map<String, InventoryQcVO> result = new HashMap<String, InventoryQcVO>();

		if (fzrs == null || fzrs.size() == 0) {
			return result;
		}

		String key = null;
		InventoryQcVO tempvo = null;
		AuxiliaryAccountBVO bvo = null;
		for (InventoryQcVO vo : fzrs) {
			tempvo = new InventoryQcVO();
			BeanUtils.copyNotNullProperties(vo, tempvo);
			//存货项目
			key = vo.getPk_inventory();
			hashlizeObject1(result, key, tempvo);
			
			//存货分类
			bvo = fzhsmap.get(key);
			if(bvo != null && !StringUtil.isEmpty(bvo.getKmclassify())){
				tempvo = new InventoryQcVO();
				BeanUtils.copyNotNullProperties(vo, tempvo);
				key = bvo.getKmclassify()+"_fl";//_fl说明是分类信息
				tempvo.setPk_inventory(key);//大类默认是对应的存货（关键）
				hashlizeObject1(result, key, tempvo);
			}
		}

		return result;
	}
	private void hashlizeObject1(Map<String, InventoryQcVO> result, String key, InventoryQcVO vo) {
		InventoryQcVO tempvo;
		if (!result.containsKey(key)) {
			result.put(key, vo);
		}else{
			tempvo = result.get(key);
			tempvo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), tempvo.getThismonthqc()));
			tempvo.setMonthqmnum(SafeCompute.add(vo.getMonthqmnum(), tempvo.getMonthqmnum()));
		}
	}


	@Override
	public List<IcDetailVO> queryCrkmx(String crkcode,String pk_corp,String rq) throws DZFWarpException {
		
		if(StringUtil.isEmpty(crkcode)
				|| StringUtil.isEmpty(pk_corp) ){
			throw new BusinessException("查询参数为空!");
		}
		List<IcDetailVO> list = getCrkMxFromSql(new String[]{ crkcode},null, pk_corp,rq);
		
		return list;
	}


	private List<IcDetailVO> getCrkMxFromSql(String[] crkcode, String[] tzpzids ,String pk_corp,String rq) {
		crkcode = ReportUtil.filterNull(crkcode);
		tzpzids = ReportUtil.filterNull(tzpzids);
		DZFDate jzDate =  gl_ic_invtoryqcserv.queryInventoryQcDate(pk_corp);
		StringBuffer qrysql = new StringBuffer();
		SQLParameter sp  = new SQLParameter();
		sp.addParam(pk_corp);
		qrysql.append(" select case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end pk_sp,h.pk_tzpz_h,  ");
		qrysql.append(" b.nnumber nnum,b.nprice, b.jfmny as jfmny, b.dfmny as dfmny,b.zy,h.pzh,b.glchhsnum as glchhsnum,b.xsjzcb as xsjzcb,");
		qrysql.append("  h.doperatedate dbilldate,h.coperatorid,h.nbills,h.vicbillcode ,b.vicbillcodetype ");
		qrysql.append("  from ynt_tzpz_b b  ");
		qrysql.append(" inner join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h ");
		qrysql.append(" where nvl(b.dr,0)=0 and nvl(h.dr,0)=0 and  h.pk_corp = ?  ");
		if(jzDate != null){
			qrysql.append("  and h.doperatedate >= ?  ");
			sp.addParam(jzDate);
		}
		if(!StringUtil.isEmpty(rq)){
			qrysql.append(" and h.doperatedate like ?  ");
			sp.addParam(rq+"%");
		}
		qrysql.append("  and b.vicbillcodetype is not null  " );
		qrysql.append(" and ( b.fzhsx6 is not null or b.pk_inventory is not null ) ");
		if(crkcode!=null && crkcode.length>0){
			qrysql.append("  and "+ SqlUtil.buildSqlForIn("h.vicbillcode", crkcode));
		}else if(tzpzids!=null && tzpzids.length>0){
			qrysql.append("  and "+ SqlUtil.buildSqlForIn("h.pk_tzpz_h", tzpzids));
		}else{
			throw new BusinessException("查询参数为空");
		}
		qrysql.append(" order by h.period,h.pzh,b.vicbillcodetype,b.rowno ");
		
		
		
		
		List<IcDetailVO> list =  (List<IcDetailVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(IcDetailVO.class));
		
		if(list!=null && list.size()>0){
			AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(
					AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);
			for(IcDetailVO vo:list){
				if(!StringUtil.isEmpty(vo.getPk_sp())){
					for(AuxiliaryAccountBVO bvo:bvos){
						if(vo.getPk_sp().equals(bvo.getPk_auacount_b())){
							vo.setSpbm(bvo.getCode());
							vo.setSpmc(bvo.getName());
							vo.setJldw(bvo.getUnit());
							vo.setSpgg(bvo.getSpec());//规格型号
							break;
						}
					}
				}
				//过滤蓝冲的凭证
				if(InventoryConstant.IC_STYLE_OUT.equals(vo.getVicbillcodetype())){
//					if(vo.getGlchhsnum()!=null && vo.getGlchhsnum().doubleValue()<0){//
//						if(vo.getJfmny()!=null && vo.getJfmny().doubleValue()>0){//处理蓝冲
//							vo.setJfmny(vo.getJfmny().multiply(-1));
//							vo.setNnum(VoUtils.getDZFDouble(vo.getNnum()).multiply(-1));
//						}
//					}
					//单价计算
					vo.setJfmny(vo.getXsjzcb());
					vo.setDfmny(DZFDouble.ZERO_DBL);
					if(VoUtils.getDZFDouble(vo.getNnum()).doubleValue()!=0){
						vo.setNprice(VoUtils.getDZFDouble(vo.getXsjzcb()).div(vo.getNnum()));
					}else{
						vo.setNprice(DZFDouble.ZERO_DBL);
					}
				}
			}
		}
		
		return list;
	}


	@Override
	public Map<String, List<IcDetailVO>> queryCrkmxs(String[] crkcodes,String[] tzpzids ,String pk_corp,String rq) throws DZFWarpException {
		List<IcDetailVO> list =  getCrkMxFromSql(crkcodes, tzpzids,pk_corp,rq);
		if(list == null || list.size() ==0){
			if (tzpzids != null && tzpzids.length > 0) {
				String msg = getCrkVoucherMsg(pk_corp, tzpzids);
				if (!StringUtil.isEmpty(msg)) {
					throw new BusinessException(msg);
				}
			}
			throw new BusinessException("暂无出入库数据");
		}
		//根据出入库单号获取
		Map<String, List<IcDetailVO>> resmap = new LinkedHashMap<String, List<IcDetailVO>>();
		
		if(list!=null && list.size()>0){
			for(IcDetailVO vo:list){
				if(resmap.containsKey(vo.getVicbillcode()+"_"+vo.getVicbillcodetype()+"_"+DateUtils.getPeriod(vo.getDbilldate()))){
					resmap.get(vo.getVicbillcode()+"_"+vo.getVicbillcodetype()+"_"+DateUtils.getPeriod(vo.getDbilldate())).add(vo);
				}else{
					List<IcDetailVO> tlist = new ArrayList<IcDetailVO>();
					tlist.add(vo);
					resmap.put(vo.getVicbillcode()+"_"+vo.getVicbillcodetype()+"_"+DateUtils.getPeriod(vo.getDbilldate()), tlist);
				}
			}
		}
		return resmap;
	}

	private String getCrkVoucherMsg(String pk_corp, String[] tzpzids) {
		Map<String, YntCpaccountVO> map = accService.queryMapByPk(pk_corp);
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		QueryVoucher queryVoucher = new QueryVoucher(singleObjectBO,gl_fzhsserv);
		List<TzpzHVO> hvos = queryVoucher.queryVoucherByids(Arrays.asList(tzpzids));
		String period = null;
		if (hvos != null && hvos.size() > 0) {
			for (TzpzHVO hvo : hvos) {
				boolean isCrk = checkIsCrkVoucher(corpvo, hvo, map);
				if (isCrk) {
					period = hvo.getPeriod();
				}
			}
		}
		String msg = null;
		if (period != null) {
//			DZFDate jzDate =  gl_ic_invtoryqcserv.queryInventoryQcDate(pk_corp);
			msg = "凭证期间" + period + "未启用存货管理， 暂无出入库单， 请检查";
		}
		return msg;
	}
	private boolean checkIsCrkVoucher(CorpVO corpvo, TzpzHVO hvo, Map<String, YntCpaccountVO> map) {
		boolean isCrk = false;
		TzpzBVO[] bodyvos = (TzpzBVO[]) hvo.getChildren();
		if (bodyvos == null || bodyvos.length == 0)
			return isCrk;
		for (TzpzBVO bvo : bodyvos) {
			YntCpaccountVO accountVO = map.get(bvo.getPk_accsubj());
			if (accountVO == null)
				continue;
			if ((Kmschema.isKcspbm(corpvo.getCorptype(), accountVO.getAccountcode())
					|| Kmschema.isYclbm(corpvo.getCorptype(), accountVO.getAccountcode()))
					&& accountVO.getIsnum() != null && accountVO.getIsnum().booleanValue()) {
				if (bvo.getVdirect() == 0) {
					isCrk = true;
				} else if (bvo.getVdirect() == 1) {
					if (!Kmschema.ischengbenpz(corpvo, bodyvos)) {
						isCrk = true;
					} else if (Kmschema.ischengbenpz(corpvo, bodyvos)
							&& (bvo.getNnumber() == null || bvo.getNnumber()
									.doubleValue() == 0)) {
						isCrk = true;
					}
				}
			} else if (Kmschema.isshouru(corpvo.getCorptype(), accountVO.getAccountcode())
					&& accountVO.getIsnum() != null && accountVO.getIsnum().booleanValue()
					&& !Kmschema.isbennianlirunpz(corpvo, bodyvos)) {
				isCrk = true;
			}
			if (isCrk) {
				break;
			}
		}
		return isCrk;
	}
}

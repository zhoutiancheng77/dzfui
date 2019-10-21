package com.dzf.zxkj.report.service.cwzb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwzb.IFzKmmxReport;
import com.dzf.zxkj.report.tree.KmmxFzVoTreeStrategy;
import com.dzf.zxkj.report.utils.BeanUtils;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.VoUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.Map.Entry;

@SuppressWarnings("all")
@Service("gl_rep_fzkmmxjrptserv")
public class FzKmmxRptImpl implements IFzKmmxReport {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Reference(version = "2.0.0")
	private IZxkjPlatformService zxkjPlatformService;

	@Override
	public Object[] getFzkmmxVos(KmReoprtQueryParamVO paramavo, DZFBoolean bshowcolumn) throws DZFWarpException {
		
		validate(paramavo);
		
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(paramavo.getPk_corp());
		
		YntCpaccountVO[] cpavos =   zxkjPlatformService.queryByPk(paramavo.getPk_corp());
		
		Map<String , YntCpaccountVO> kmmap = convertMap(cpavos);
		
		List<String> kmwherepart = getKmwhere(paramavo,true,cpavos);
		
		String kmqrysql = getKmSql(paramavo, true);
		
		List<String> kmwherepart1 = getKmwhere(paramavo,false,cpavos);
		/** 获取当前有关系的项目 */
		List<FzKmmxVO> xmvos = getConXm(paramavo,kmwherepart1,kmmap);
		
		queryInvtoryXm(xmvos,paramavo,kmwherepart1,kmmap,cpvo);
	
		String firstxm =null;
		String firstxmname = null;
		if(!StringUtil.isEmpty(paramavo.getSelectxmid())){
			firstxm= paramavo.getSelectxmid();
			for(FzKmmxVO vo:xmvos){
				if(vo.getId().split("~")[0].equals(firstxm)){
					firstxmname = vo.getText();
					break;
				}
			}
		}
		
		List<String> periods = ReportUtil.getPeriods(paramavo.getBegindate1(), paramavo.getEnddate());
		/** 1:查询期初 */
		Map<String, FzKmmxVO> qcmapvos = queryQcVos(paramavo,kmwherepart,kmqrysql,kmmap);
		/** 2:查询发生*/
		Map<String, List<FzKmmxVO>> fsmapvos = queryFsVos(paramavo,kmwherepart,kmqrysql,kmmap);
		/** 过滤（项目+科目） */
		xmfilter(xmvos,qcmapvos,fsmapvos,paramavo.getXswyewfs(),
				paramavo.getIshowfs(),paramavo.getBegindate1(),
				paramavo.getEnddate(),paramavo.getCjq(),paramavo.getCjz(),paramavo.getPk_corp());
		if(xmvos != null &&xmvos.size() == 0){
			Object[] res = new Object[2];
			res[0] = new ArrayList<FzKmmxVO>();
			res[1] = xmvos;
			return res;
		}else{
			/**  取出第一个(然后过滤) */
			if (xmvos != null && xmvos.size() > 0 && StringUtil.isEmpty(firstxm)) {
				firstxm = xmvos.get(0).getId().split("~")[0];
				firstxmname = xmvos.get(0).getText();
			}
		}
		/** 过滤qcmap,fsmap */
		if(bshowcolumn!=null  && bshowcolumn.booleanValue()){
			fsFilter(qcmapvos,fsmapvos,firstxm);
		}
		/** 对应期末数据 */
		List<FzKmmxVO> resvos = getResultVos(qcmapvos, fsmapvos, periods,kmmap,paramavo.getKms_last(),xmvos,paramavo.getXswyewfs(),paramavo.getIshowfs());
		
		/** 取类别的第一个项目 */
		List<FzKmmxVO> firstxmvos = new ArrayList<FzKmmxVO>();
		firstxmvos = resvos;
		if(firstxmvos.size()>0 && bshowcolumn!=null  && bshowcolumn.booleanValue()){//是否显示单个项目
			if (!StringUtil.isEmpty(paramavo.getSelectkmid()) &&  paramavo.getXskm()!=null && paramavo.getXskm().booleanValue()){//默认取一个(如果selectid没值)
				YntCpaccountVO cpavo = kmmap.get(paramavo.getSelectkmid());
				for(FzKmmxVO kmmxvo : firstxmvos){
					kmmxvo.setFzxmname(cpavo.getAccountcode()+"_"+cpavo.getAccountname());
				}
			}else{
				for (FzKmmxVO kmmxvo : firstxmvos) {
					if (kmmxvo.getFzxm().equals(firstxm)) {
						kmmxvo.setFzxmname(firstxmname);
					}
				}
			}
		}
		Object[] res = new Object[2];

		firstxmvos.stream().forEach(v ->{
			v.setPk_currency(paramavo.getPk_currency());
		});

		if(xmvos != null){
			xmvos.stream().forEach(v -> {
				v.setPk_currency(paramavo.getPk_currency());
			});
		}

		res[0] = firstxmvos;
		res[1] = xmvos;
		return res;
	}

	private Map<String, YntCpaccountVO> convertMap(YntCpaccountVO[] cpavos) {
		Map<String, YntCpaccountVO> map = new HashMap<String, YntCpaccountVO>();
		if(cpavos!=null && cpavos.length>0){
			for(YntCpaccountVO vo:cpavos){
				map.put(vo.getPk_corp_account(), vo);
			}
		}
		return map;
	}

	private void fsFilter(Map<String, FzKmmxVO> qcmapvos, Map<String, List<FzKmmxVO>> fsmapvos, String firstxm) {
		
		if(qcmapvos!=null && qcmapvos.size()>0){
			FzKmmxVO tempvo = qcmapvos.get(firstxm);
			qcmapvos.clear();
			qcmapvos.put(firstxm, tempvo);
		}

		if(fsmapvos!=null&& fsmapvos.size()>0){
			List<FzKmmxVO> listtemp = fsmapvos.get(firstxm);
			fsmapvos.clear();
			if(listtemp!=null && listtemp.size()>0){
				fsmapvos.put(firstxm, listtemp);
			}
		}
		
		if(!qcmapvos.containsKey(firstxm) && !fsmapvos.containsKey(firstxm)){//不包含赋值空的
			qcmapvos.put(firstxm, new FzKmmxVO());
		}
		
	}

	/**
	 * 
	 * @param xmvos
	 * @param qcmapvos
	 * @param fsmapvos
	 */
	private void xmfilter(List<FzKmmxVO> xmvos, Map<String, FzKmmxVO> qcmapvos, Map<String, List<FzKmmxVO>> fsmapvos,
						  DZFBoolean xswyewfs, DZFBoolean ishowfs, DZFDate begindate, DZFDate enddate, Integer cjq, Integer cjz, String pk_corp ) {
		
		if((xswyewfs==null || !xswyewfs.booleanValue())
				&& (ishowfs!=null && ishowfs.booleanValue())){//全部显示
			return;
		}
		
		List<FzKmmxVO> reslist = new ArrayList<FzKmmxVO>();
		FzKmmxVO[] childlist = null;
		List<FzKmmxVO> childtemplist = null;
		
		for(FzKmmxVO fzvo:xmvos){
			FzKmmxVO qcvotemp = null;
			if(qcmapvos !=null){
				qcvotemp =  qcmapvos.get(fzvo.getId().split("~")[0]);
			}
			if((qcmapvos !=null && qcvotemp!=null && qcvotemp.getYe().doubleValue()!=0)
					|| (fsmapvos!=null &&  fsmapvos.containsKey(fzvo.getId().split("~")[0]))){
				
				childlist =(FzKmmxVO[]) DZfcommonTools.convertToSuperVO(fzvo.getChildren());// fzvo.getChildren();
				
				if(childlist!=null && childlist.length>0){
					//重新的list集合
					childtemplist = new ArrayList<FzKmmxVO>();
					//获取对应的科目list
					for(FzKmmxVO child:childlist){
						if(StringUtil.isEmpty(child.getKm())){
							continue;
						}
						
						boolean isexist = bexistPeriod(fsmapvos, fzvo.getId().split("~")[0]+"~"+child.getKm(),begindate,enddate);//是否在该期间存发生
						
						boolean isexist_qc = bexistPeriodQc(fzvo.getId().split("~")[0]+"~"+child.getKm(),qcmapvos,fsmapvos,begindate,enddate);
						
						if(isexist_qc){
							if(ishowfs!=null && !ishowfs.booleanValue() && !isexist){//有余额无发生不显示
								continue;
							}
							childtemplist.add(child);
							continue;
						}
						if(isexist){//无余额，有发生
							childtemplist.add(child);
						}
					}
					fzvo.setChildren(childtemplist.toArray(new FzKmmxVO[0]));
				}
				
				if(ishowfs!=null && !ishowfs.booleanValue()){//有余额无发生不显示
					if(fsmapvos==null || fsmapvos.size()==0){
						continue;
					}
					
					boolean isexist = bexistPeriod(fsmapvos, fzvo.getId().split("~")[0],begindate,enddate);
					
					if(!fsmapvos.containsKey(fzvo.getId().split("~")[0]) ){
						continue;
					}else if(!isexist){
						continue; 
					}
				} 
				reslist.add(fzvo);
			}
		}
		
		xmvos.clear();
		List<FzKmmxVO> childlist1 = null;
		FzKmmxVO vo_child = null;
		for(FzKmmxVO vo:reslist){
			cjq = (cjq == null ? 1 : cjq);
			cjz = (cjz == null ? 10 : cjz);
			childlist1 = getChildvo(vo.getChildren(), cjq, cjz);
			if(childlist1!=null && childlist1.size()>0){
				vo_child = (FzKmmxVO) BDTreeCreator.createTree(childlist1.toArray(new FzKmmxVO[0]), new KmmxFzVoTreeStrategy(zxkjPlatformService.queryAccountRule(pk_corp)));
				FzKmmxVO[] bodyvos = (FzKmmxVO[]) DZfcommonTools.convertToSuperVO(vo_child.getChildren());
				vo.setChildren(bodyvos);
			}else{
				vo.setState("");
				vo.setChildren(null);
			}
			handleBodyState(vo.getChildren());
			xmvos.add(vo);
		}
	}
	
	private List<FzKmmxVO> getChildvo(SuperVO[] childvos, Integer cjq, Integer cjz) {
		List<FzKmmxVO> reslit = new ArrayList<FzKmmxVO>();
		if(childvos!=null && childvos.length>0){
			Integer level; 
			for(SuperVO chidvo:childvos){
				level =((FzKmmxVO)chidvo).getLevel();
				if(level!=null && level>=cjq && level<=cjz){
					reslit.add((FzKmmxVO)chidvo);
				}
			}
		}
		return reslit;
	}

	private boolean bexistPeriodQc(String key, Map<String, FzKmmxVO> qcmapvos,
			Map<String, List<FzKmmxVO>> fsmapvos, DZFDate begindate, DZFDate enddate) {
		if(qcmapvos.containsKey(key) && qcmapvos.get(key).getYe()!=null 
				&& qcmapvos.get(key).getYe().doubleValue()!=0 ){
			return true;
		}
		List<FzKmmxVO> fslist = fsmapvos.get(key);
		DZFDouble jftotal = DZFDouble.ZERO_DBL;
		DZFDouble dftotal = DZFDouble.ZERO_DBL; 
		if(fslist!=null && fslist.size()>0){
			for(FzKmmxVO mxvo:fslist){
				if(!StringUtil.isEmpty(mxvo.getRq())
					&& (begindate.after(new DZFDate(mxvo.getRq())))){
					jftotal = SafeCompute.add(mxvo.getJf(), jftotal);
					dftotal = SafeCompute.add(mxvo.getDf(), dftotal);
				}
			} 
			if(SafeCompute.sub(jftotal, dftotal).doubleValue()!=0){
				return true;
			}
		}
		return false;
	}

	private boolean bexistPeriod(Map<String, List<FzKmmxVO>> fsmapvos, String key, DZFDate begindate, DZFDate enddate) {
		
		if(fsmapvos == null || fsmapvos.size()==0){
			return false;
		}
		
		List<FzKmmxVO> listfzvos = fsmapvos.get(key);
		
		enddate = DateUtils.getPeriodEndDate(DateUtils.getPeriod(enddate));
		if(listfzvos!=null && listfzvos.size()>0){
			for(FzKmmxVO mxvo:listfzvos){
				if(!StringUtil.isEmpty(mxvo.getRq())
					&& !(begindate.after(new DZFDate(mxvo.getRq())) || enddate.before(new DZFDate(mxvo.getRq())))){
					return true;
				}
			} 
			
			return false;
		}
		
		return false;
	}

	private YntCpaccountVO[] getKmMap(List<String> kmlist , String pk_corp,Map<String , YntCpaccountVO> kmmap) {
		if(kmlist == null || kmlist.size() ==0){
			return new YntCpaccountVO[0];
		}
		List<YntCpaccountVO> vos = new ArrayList<YntCpaccountVO>();
		for(String str:kmlist){
			vos.add(kmmap.get(str));
		}
		VOUtil.ascSort(vos, new String[]{"accountcode"});
		return vos.toArray(new YntCpaccountVO[0]);
	}

	/**
	 * 科目条件
	 * @param paramavo
	 * @return
	 */
	private List<String> getKmwhere(KmReoprtQueryParamVO paramavo, boolean isshowselect,YntCpaccountVO[] cpalist) {
		List<String> kmres = new ArrayList<String>();
		if(cpalist!=null && cpalist.length>0){
			for(YntCpaccountVO cpavo:cpalist){
				if (!StringUtil.isEmpty(paramavo.getKms_first()) && cpavo.getAccountcode().compareTo(paramavo.getKms_first())<0) {
					continue;
				}
				
				if(!StringUtil.isEmpty(paramavo.getKms_last()) && cpavo.getAccountcode().compareTo(paramavo.getKms_last())>0){
					continue;
				}
				if (isshowselect && !StringUtil.isEmpty(paramavo.getSelectkmid()) && paramavo.getXskm() != null
						&& paramavo.getXskm().booleanValue()) {
					if(paramavo.getSelectkmid().equals(cpavo.getPk_corp_account())){
						kmres.add(cpavo.getPrimaryKey());
					}
					continue;
				}
				kmres.add(cpavo.getPrimaryKey());
			}
		}

		return kmres;
	}
	
	private String getKmSql(KmReoprtQueryParamVO paramavo, boolean isshowselect){
		StringBuffer sql = new StringBuffer();
		sql.append(" ( select pk_corp_account from ( select  t.pk_corp_account from ynt_cpaccount t ");
		sql.append(" where nvl(t.dr,0)=0 ");
		sql.append(" and   t.pk_corp = '"+paramavo.getPk_corp()+"'");
		if (!StringUtil.isEmpty(paramavo.getKms_first())) {
			sql.append(" and   t.accountcode >='"+paramavo.getKms_first()+"'");
		}
		if (!StringUtil.isEmpty(paramavo.getKms_last())) {
			sql.append(" and  ( t.accountcode <='"+paramavo.getKms_last()+"'  or t.accountcode  like '" + paramavo.getKms_last() + "%' )");
		}

		if (isshowselect && !StringUtil.isEmpty(paramavo.getSelectkmid()) && paramavo.getXskm() != null
				&& paramavo.getXskm().booleanValue()) {
			sql.append(" and  t.pk_corp_account = '"+paramavo.getSelectkmid()+"'");
		}
		sql.append(" ) )   tempsql ");
		return sql.toString();
	}

	/**
	 * 校验日期
	 * @param paramavo
	 * @throws BusinessException
	 */
	private void validate(KmReoprtQueryParamVO paramavo) throws DZFWarpException {
		if(paramavo.getBegindate1() == null){
			throw new BusinessException("查询开始日期不能为空!");
		}
		if(paramavo.getEnddate() == null){
			throw new BusinessException("查询结束日期不能为空!");
		}
		paramavo.setEnddate(DateUtils.getPeriodEndDate(DateUtils.getPeriod(paramavo.getEnddate())));
		
		if(paramavo.getEnddate().before(paramavo.getBegindate1())){
			throw new BusinessException("结束日期不能在开始日期前!");
		}
		
		/** 开始日期不能再建账日期前 */
		CorpVO corpvo = zxkjPlatformService.queryCorpByPk(paramavo.getPk_corp());
		if(corpvo.getBegindate()!=null){
			DZFDate corpdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpvo.getBegindate()));
			if(corpdate.after(paramavo.getBegindate1())){
				throw new BusinessException("查询开始日期不能再建账日期("+corpdate.toString()+")前!");
			}
		}
	}

	private List<FzKmmxVO> getConXm(KmReoprtQueryParamVO paramavo,List<String> kmlist,Map<String , YntCpaccountVO> kmmap) {
		/** 根据辅助类别 获取对应的辅助项目和相应的科目 */
		StringBuffer fzxmpart = getParamPart(paramavo);
		
		/** 获取查询结果 */
		List<FzKmmxVO> resfzvos = getConXmQryList(paramavo, fzxmpart);
		
		/** 赋值科目(child) */
		getConXmPutChildKm(paramavo, resfzvos,kmlist,kmmap);
		
		return resfzvos;
	}

	private void getConXmPutChildKm(KmReoprtQueryParamVO paramavo, List<FzKmmxVO> resfzvos,
			List<String> kmlist,Map<String , YntCpaccountVO> kmmap) {
		YntCpaccountVO[] cpavos = getKmMap(kmlist,paramavo.getPk_corp(),kmmap);
		/** 是否显示科目 */
		if(paramavo.getXskm()!=null && paramavo.getXskm().booleanValue()){
			/** 是否显示科目 */
			List<FzKmmxVO> kmmxvos = null;
			List<YntCpaccountVO> parentkeys = null;
			Integer code = null;
			Map<String, List<YntCpaccountVO>> parentlist = getParentKey(cpavos);
			/**每个类别有多少相关的辅助项目*/
			Map<Integer, List<YntCpaccountVO>> fzfl_km_map = getKmMapFromFzlb(resfzvos,cpavos);
			for(FzKmmxVO fzkmmx:resfzvos){
				fzkmmx.setState("closed");
				fzkmmx.setIskmid(DZFBoolean.FALSE);
				fzkmmx.setPk_currency(paramavo.getPk_currency());
				code =Integer.parseInt(fzkmmx.getCode().trim());
				/** 是否显示科目 */
				kmmxvos = new ArrayList<FzKmmxVO>();
				if(fzfl_km_map.get(code)!=null && fzfl_km_map.get(code).size()>0){
					for(YntCpaccountVO cpa:fzfl_km_map.get(code)){
						/** 是否选中了辅助项目 */
						parentkeys =parentlist.get(cpa.getPrimaryKey());
						if(parentkeys!=null && parentkeys.size()>0){
							for(YntCpaccountVO votemp:parentkeys){
								createFzkmmx(fzkmmx, votemp,kmmxvos);
							}
						}
						createFzkmmx(fzkmmx, cpa,kmmxvos);
					}
				}
				fzkmmx.setId(fzkmmx.getId()+"~"+"");
				fzkmmx.setChildren(kmmxvos.toArray(new FzKmmxVO[0]));
			}
		}
	}

	private Map<Integer, List<YntCpaccountVO>> getKmMapFromFzlb(List<FzKmmxVO> resfzvos, YntCpaccountVO[] cpavos) {
		Map<Integer, List<YntCpaccountVO>> fzmap = new HashMap<Integer, List<YntCpaccountVO>>();
		Integer code = null;
		for(FzKmmxVO fzkmmx:resfzvos){
			code =Integer.parseInt(fzkmmx.getCode().trim());
			if(!fzmap.containsKey(code)){
				fzmap.put(code, new ArrayList<YntCpaccountVO>());
			}
		}
		String value = null;
		String valuetemp = null;
		for(YntCpaccountVO cpa:cpavos){
			value  = cpa.getIsfzhs();
			if(StringUtil.isEmpty(value)){
				continue;
			}
			for(Entry<Integer, List<YntCpaccountVO>>  entry:fzmap.entrySet()){
				if(value.length() < entry.getKey().intValue()){
					continue;
				}
				valuetemp = value.substring(entry.getKey()-1, entry.getKey());
				/** 是否选中了辅助项目 */
				if("1".equals(valuetemp)){
					fzmap.get(entry.getKey()).add(cpa);
				}
			}
		}
		return fzmap;
	}

	private List<FzKmmxVO> getConXmQryList(KmReoprtQueryParamVO paramavo, StringBuffer fzxmpart) {
		StringBuffer sql =new StringBuffer();
		sql.append(" select b.code || '_'||b.name as text ,    ");
		sql.append(" h.code,b.pk_auacount_b as id , ");
		sql.append(" b.code as fzcode,b.name as fzname ");
		sql.append(" from ynt_fzhs_b b    ");
		sql.append(" inner join ynt_fzhs_h h on b.pk_auacount_h = h.pk_auacount_h " );
		sql.append(" where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 and b.pk_corp in(?,'000001')  and h.pk_auacount_h = ?  ");
		if(fzxmpart.length()>0){
			sql.append(" and "+fzxmpart.toString());
		}
//		sql.append("  order by h.code,b.code ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramavo.getPk_corp());
		sp.addParam(paramavo.getFzlb());
		List<FzKmmxVO> resfzvos  =  (List<FzKmmxVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(FzKmmxVO.class));
		VOUtil.ascSort(resfzvos, new String[]{"code","fzcode"});
		return resfzvos;
	}

	private StringBuffer getParamPart(KmReoprtQueryParamVO paramavo) {
		StringBuffer fzxmpart = new StringBuffer();
		String fzxm = paramavo.getFzxm();
		if(!StringUtil.isEmpty(fzxm)){
			if(fzxm.split(",")!=null){
			   String[] value = fzxm.split(",");
			   fzxmpart.append(SqlUtil.buildSqlForIn("b.code", value));
			}
		}
		return fzxmpart;
	}


	private void handleBodyState(SuperVO[] bodyvos) {
		if(bodyvos!=null && bodyvos.length>0){
			for(SuperVO vo:bodyvos){
				if(((FzKmmxVO)vo).getChildren()!=null && ((FzKmmxVO)vo).getChildren().length>0){
					((FzKmmxVO)vo).setState("closed");
					handleBodyState(((FzKmmxVO)vo).getChildren());
				}
			}
		}
	}

//	private List getParentKey(YntCpaccountVO vo, YntCpaccountVO[] cpavos) {
//		List<YntCpaccountVO> lists = new ArrayList<YntCpaccountVO>();
//		for(YntCpaccountVO entry:cpavos){
//			if(vo.getAccountcode().startsWith(entry.getAccountcode())
//					&& entry.getAccountcode().length() < vo.getAccountcode().length() ){
//				lists.add(entry);
//			}
//		}
//		return lists;
//	}
	
	private Map<String, List<YntCpaccountVO>> getParentKey(YntCpaccountVO[] cpavos) {
		 Map<String, List<YntCpaccountVO>> rsmap = new HashMap<String, List<YntCpaccountVO>>();
		List<YntCpaccountVO> lists = null;
		for(YntCpaccountVO vo:cpavos){
			lists = new ArrayList<YntCpaccountVO>();
			for(YntCpaccountVO entry:cpavos){
				if(vo.getAccountcode().startsWith(entry.getAccountcode())
						&& entry.getAccountcode().length() < vo.getAccountcode().length() ){
					lists.add(entry);
				}
			}
			rsmap.put(vo.getPrimaryKey(), lists);
		}
		return rsmap;
	}

	private void createFzkmmx(FzKmmxVO fzkmmx, YntCpaccountVO cpa,List<FzKmmxVO> kmmxvos) {
		for(FzKmmxVO fzmxvo:kmmxvos){
			if(fzmxvo.getId().equals(fzkmmx.getId() +"~"+ cpa.getPk_corp_account())){
				return;
			}
		}
		FzKmmxVO tempvo = new FzKmmxVO();
		tempvo.setIskmid(DZFBoolean.TRUE);
		tempvo.setId(fzkmmx.getId() +"~"+ cpa.getPk_corp_account());
		tempvo.setText(cpa.getAccountcode()+"_"+cpa.getAccountname());
		tempvo.setFzcode(fzkmmx.getFzcode());
		tempvo.setFzname(fzkmmx.getFzname());
		tempvo.setKmcode(cpa.getAccountcode());
		tempvo.setKmname(cpa.getAccountname());
		tempvo.setLevel(cpa.getAccountlevel());
		tempvo.setKm(cpa.getPk_corp_account());
		kmmxvos.add(tempvo);
	}

	/**
	 * 查询对应的期末的结果集(添加期初+发生，同时按照日期排序，计算本期和本年累计)
	 * 
	 */
	private List<FzKmmxVO> getResultVos(Map<String, FzKmmxVO> qcmapvos, Map<String, List<FzKmmxVO>> fsmapvos,
			List<String> periods,Map<String , YntCpaccountVO> kmmap,String kms_last,
			List<FzKmmxVO> xmvos,DZFBoolean xswyewfs,DZFBoolean ishowfs) {
		List<FzKmmxVO> reslistvos = new ArrayList<FzKmmxVO>();
		Map<String,List<FzKmmxVO>> resmap = new HashMap<String,List<FzKmmxVO>>();
		
		if(qcmapvos!=null && qcmapvos.size()>0){
			for(String key:qcmapvos.keySet()){
				resmap.put(key, new ArrayList<FzKmmxVO>());
			}
		}
		
		if(fsmapvos!=null && fsmapvos.size()>0){
			for(String key:fsmapvos.keySet()){
				if(resmap.containsKey(key)){
					for(FzKmmxVO tempvo:fsmapvos.get(key)){
						resmap.get(key).add(tempvo);
					}
				}else{
					List<FzKmmxVO> templist = fsmapvos.get(key);
					resmap.put(key, templist);
				}
			}
		}
		
		Map<String, FzKmmxVO> xmnamemap = new HashMap<String, FzKmmxVO>();
		if(xmvos!=null && xmvos.size()>0){
			for(FzKmmxVO vo:xmvos){
				xmnamemap.put(vo.getId().split("~")[0], vo);
				putFzchildMap(xmnamemap, vo);
			}
		}
		/** 对每个map进行日期的排序 */
		if(resmap.size()>0){
			for(String key:resmap.keySet()){
				List<FzKmmxVO> listtemp = resmap.get(key);
				List<FzKmmxVO> listemp2 = calXmMx(qcmapvos, periods, kmmap, key, listtemp,xswyewfs,ishowfs);
				resmap.put(key, listemp2);
			}
			FzKmmxVO fzxmvo = null;
			/** 重新循环赋值 */
			for(String key:resmap.keySet()){
				List<FzKmmxVO> listtemp = resmap.get(key);
				fzxmvo = xmnamemap.get(key);
				if(fzxmvo == null){
					continue;
				}
				if(StringUtil.isEmpty(fzxmvo.getText())){
					continue;
				}
				if(listtemp!=null ){
					for(FzKmmxVO mxvo:listtemp){
						FzKmmxVO tmxvo = new FzKmmxVO();
						BeanUtils.copyNotNullProperties(mxvo, tmxvo);
						tmxvo.setText(fzxmvo.getText());
						tmxvo.setFzcode(fzxmvo.getFzcode());
						tmxvo.setFzname(fzxmvo.getFzname());
						tmxvo.setKmcode(fzxmvo.getKmcode());
						tmxvo.setKmname(fzxmvo.getKmname());
						tmxvo.setFzxm(key);//辅助项目赋值
						reslistvos.add(tmxvo);
					}
				}
			}
			
			FzKmmxVO[] sort_vos = reslistvos.toArray(new FzKmmxVO[0]);
			
			Arrays.sort(sort_vos, new Comparator<FzKmmxVO>() {

				public int compare(FzKmmxVO o1, FzKmmxVO o2) {
					int i = 0;
					if(i ==0){
						i = o1.getFzcode().compareTo(o2.getFzcode());
					}
					if(i == 0){
						String o1value =StringUtil.isEmpty(o1.getKmcode())?  "": o1.getKmcode();
						String o2value = StringUtil.isEmpty(o2.getKmcode()) ? "" : o2.getKmcode();
						i = o1value.compareTo(o2value);
					}
					return i;
				}
			});
			reslistvos = Arrays.asList(sort_vos);
		}
		return reslistvos;
	}

	private void putFzchildMap(Map<String, FzKmmxVO> xmnamemap, FzKmmxVO vo) {
		FzKmmxVO fxxm_t;
		if(vo.getChildren()!=null && vo.getChildren().length>0){
			for(SuperVO childvo:vo.getChildren()){
				fxxm_t = new FzKmmxVO();
				BeanUtils.copyNotNullProperties(childvo, fxxm_t);
				fxxm_t.setAttributeValue("text", (String)childvo.getAttributeValue("kmcode")+"_"+vo.getFzcode()
				+"_"+(String)childvo.getAttributeValue("kmname") +"_"+ vo.getFzname());
				xmnamemap.put((String)childvo.getAttributeValue("id"),fxxm_t);

				if(childvo.getChildren()!=null && childvo.getChildren().length>0){
					putFzchildMap(xmnamemap, (FzKmmxVO)childvo);
				}
			}
		}
	}

	private List<FzKmmxVO> calXmMx(Map<String, FzKmmxVO> qcmapvos, List<String> periods,
			Map<String, YntCpaccountVO> kmmap, String key, List<FzKmmxVO> listtemp,DZFBoolean xswyewfs,DZFBoolean ishowfs) {
		for(String period:periods){
			FzKmmxVO monthkmmx = new FzKmmxVO();
			monthkmmx.setRq(DateUtils.getPeriodEndDate(period).toString());
			monthkmmx.setFzxm(key);
			monthkmmx.setZy("本月合计");
			monthkmmx.setBsyszy(DZFBoolean.TRUE);
			listtemp.add(monthkmmx);

			FzKmmxVO yearkmmx = new FzKmmxVO();
			yearkmmx.setRq(DateUtils.getPeriodEndDate(period).toString());
			yearkmmx.setFzxm(key);
			yearkmmx.setZy("本年累计");
			yearkmmx.setBsyszy(DZFBoolean.TRUE);
			listtemp.add(yearkmmx);
		}

		FzKmmxVO[] mxvos = listtemp.toArray(new FzKmmxVO[0]);
		FzKmmxVO qckmmx = new FzKmmxVO();
		qckmmx.setRq(DateUtils.getPeriodStartDate(periods.get(0)).toString());
		qckmmx.setFzxm(key);
		qckmmx.setYe(qcmapvos.get(key) == null?DZFDouble.ZERO_DBL:qcmapvos.get(key).getYe());
		qckmmx.setZy("期初余额");
		qckmmx.setBsyszy(DZFBoolean.TRUE);
		listtemp.add(qckmmx);
		FzKmmxVO[] reskmmxvos = ArrayUtil.mergeArray(new FzKmmxVO[]{qckmmx}, mxvos);
		/** 重新排序 */
		Arrays.sort(reskmmxvos, new Comparator<FzKmmxVO>() {
			private int get(KmMxZVO o1) {
				int i = 0;
				if (o1.getZy() == null)
					i = 2;
				else if (o1.getZy().equals("期初余额") && ReportUtil.bSysZy(o1))
					i = 1;
				else if (o1.getZy().equals("本月合计") && ReportUtil.bSysZy(o1))
					i = 3;
				else if (o1.getZy().equals("本年累计") && ReportUtil.bSysZy(o1))
					i = 4;
				else
					i = 2;
				return i;
			}
			
			public int compare(FzKmmxVO o1, FzKmmxVO o2) {
				int i = 0;
				if(i ==0){
					i = o1.getRq().compareTo(o2.getRq());
				}
				if(i == 0){
					Integer o1value = get(o1);
					Integer o2value = get(o2);
					i = o1value.compareTo(o2value);
				}
			    if(i == 0){
			    	String pzh1 = o1.getPzh() == null ? "99999" : o1.getPzh();
			    	String pzh2 = o2.getPzh() == null ? "99999" : o2.getPzh();
			    	i = pzh1.compareTo(pzh2);
			    }
				return i;
			}
		});
		
		/** 循环数据，同时赋值 */
		DZFDouble sumvalue = DZFDouble.ZERO_DBL;
		DZFDouble ybsumvalue = DZFDouble.ZERO_DBL;//原币
		DZFDouble jfmonthvalue =  DZFDouble.ZERO_DBL;
		DZFDouble monthvalueabs = DZFDouble.ZERO_DBL;//本月绝对值，为了查看是否有发生
		DZFDouble ybjfmonthvalue =  DZFDouble.ZERO_DBL;//原币
		DZFDouble dfmonthvalue =  DZFDouble.ZERO_DBL;
		DZFDouble ybdfmonthvalue =  DZFDouble.ZERO_DBL;//原币
		DZFDouble jfyearvalue = DZFDouble.ZERO_DBL;
		DZFDouble ybjfyearvalue = DZFDouble.ZERO_DBL;//原币
		DZFDouble dfyearvalue =DZFDouble.ZERO_DBL;
		DZFDouble ybdfyearvalue =DZFDouble.ZERO_DBL;//原币
		
		/** 数量 */
		DZFDouble sumnum = DZFDouble.ZERO_DBL;
		DZFDouble jfmonthnum = DZFDouble.ZERO_DBL;
		DZFDouble dfmonthnum  = DZFDouble.ZERO_DBL;
		DZFDouble jfyearnum = DZFDouble.ZERO_DBL;
		DZFDouble dfyearnum = DZFDouble.ZERO_DBL;
		
		List<FzKmmxVO> listemp2 = new ArrayList<FzKmmxVO>();
		List<FzKmmxVO> listemp3 = new ArrayList<FzKmmxVO>();//余额过滤
		Map<String, DZFDouble> jftotalmap = new HashMap<String, DZFDouble>();
		Map<String, DZFDouble> dftotalmap = new HashMap<String, DZFDouble>();
		Map<String, DZFDouble> ybjftotalmap = new HashMap<String, DZFDouble>();
		Map<String, DZFDouble> ybdftotalmap = new HashMap<String, DZFDouble>();
		
		Map<String, DZFDouble> jfnumtotalmap = new HashMap<String, DZFDouble>();
		Map<String, DZFDouble> dfnumtotalmap = new HashMap<String,DZFDouble>();
		String year = null;
		Map<String, Object[]> bwyewfsmap = new HashMap<String, Object[]>();//无余额无发生不显示
		Map<String, Object[]> bwshowfsmap = new HashMap<String, Object[]>();//有余额无发生不显示
		
		for(FzKmmxVO tempvo:reskmmxvos){
			jfyearvalue = SafeCompute.add(jfyearvalue, tempvo.getJf());
			dfyearvalue = SafeCompute.add(dfyearvalue, tempvo.getDf());
			
			//原币
			ybjfyearvalue = SafeCompute.add(ybjfyearvalue, tempvo.getYbjf());
			ybdfyearvalue = SafeCompute.add(ybdfyearvalue, tempvo.getYbdf());
			
			jfyearnum = SafeCompute.add(jfyearnum, tempvo.getJfnum());
			dfyearnum = SafeCompute.add(dfyearnum, tempvo.getDfnum());
			
			year = tempvo.getRq().substring(0, 4);
			
			/** --------本位币------ */
			putYearMapValue(jftotalmap, year, tempvo.getJf());//借方
			
			putYearMapValue(dftotalmap, year, tempvo.getDf());//贷方
			
			/** ---------原币值--------*/
			putYearMapValue(ybjftotalmap, year, tempvo.getYbjf());
			
			putYearMapValue(ybdftotalmap, year, tempvo.getYbdf());
			
			/**-----------数量----------- */
			putYearMapValue(jfnumtotalmap, year, tempvo.getJfnum());//借方数量
			
			putYearMapValue(dfnumtotalmap, year, tempvo.getDfnum());//贷方数量
			
			if(tempvo.getZy()!=null && "期初余额".equals(tempvo.getZy()) && ReportUtil.bSysZy(tempvo)){
				if(qcmapvos.get(key)!=null){
					sumvalue = SafeCompute.add(sumvalue, qcmapvos.get(key).getYe());//sumvalue.add(getDzfDouble(qcmapvos.get(key).getYe()));
					
					ybsumvalue =SafeCompute.add(ybsumvalue, qcmapvos.get(key).getYbye());
					
					sumnum = SafeCompute.add(sumnum, qcmapvos.get(key).getYenum());//数量
				}
			}else{
				YntCpaccountVO accountvo = 	kmmap.get(tempvo.getKm());
				
				if(accountvo != null){
					sumvalue = SafeCompute.add(sumvalue, SafeCompute.sub(tempvo.getJf(), tempvo.getDf()));// sumvalue.add(getDzfDouble(tempvo.getJf())).sub(getDzfDouble(tempvo.getDf()));
					
					ybsumvalue = SafeCompute.add(ybsumvalue, SafeCompute.sub(tempvo.getYbjf(), tempvo.getYbdf()));// ybsumvalue.add(VoUtils.getDZFDouble(tempvo.getYbjf())).sub(getDzfDouble(tempvo.getYbdf()));
					
					sumnum = SafeCompute.add(sumnum, SafeCompute.sub(tempvo.getJfnum(), tempvo.getDfnum()));//数量
					
				}
			}
			if(!periods.contains(tempvo.getRq().substring(0, 7))){
				continue;
			}
			jfmonthvalue = SafeCompute.add(jfmonthvalue, tempvo.getJf());
			dfmonthvalue = SafeCompute.add(dfmonthvalue, tempvo.getDf());
			monthvalueabs = monthvalueabs.add(VoUtils.getDZFDouble(tempvo.getJf()).abs()).add(VoUtils.getDZFDouble(tempvo.getDf()).abs());
			
			/** ---------原币---------- */
			ybjfmonthvalue = SafeCompute.add(ybjfmonthvalue, tempvo.getYbjf());
			ybdfmonthvalue = SafeCompute.add(ybdfmonthvalue, tempvo.getYbdf());
			
			/** 数量 */
			jfmonthnum = SafeCompute.add(jfmonthnum, tempvo.getJfnum());
			dfmonthnum = SafeCompute.add(dfmonthnum, tempvo.getDfnum());
			
			tempvo.setYe(sumvalue);//余额
			
			tempvo.setYbye(ybsumvalue);//原币余额
			
			tempvo.setYenum(sumnum);//数量
			
			tempvo.setFzxm(key);
			
			if(tempvo.getZy()!=null && "本月合计".equals(tempvo.getZy()) && ReportUtil.bSysZy(tempvo)){
				tempvo.setJf(jfmonthvalue);
				tempvo.setDf(dfmonthvalue);
				
				/** 原币 */
				tempvo.setYbjf(ybjfmonthvalue);
				tempvo.setYbdf(ybdfmonthvalue);
				
				/** 数量 */
				tempvo.setJfnum(jfmonthnum);
				tempvo.setDfnum(dfmonthnum);
				
				jfmonthvalue = DZFDouble.ZERO_DBL;
				dfmonthvalue = DZFDouble.ZERO_DBL;
				
				ybjfmonthvalue = DZFDouble.ZERO_DBL;//原币
				ybdfmonthvalue = DZFDouble.ZERO_DBL;
				
				if(monthvalueabs.doubleValue() == 0 && VoUtils.getDZFDouble(tempvo.getYe()).doubleValue() == 0){//无余额无发生不显示
					bwyewfsmap.put(tempvo.getRq().substring(0, 7), new Object[]{DZFBoolean.TRUE,""});
				}else{
					String period_ye = DateUtils.getPeriodStartDate(tempvo.getRq().substring(0, 7)).toString();
					bwyewfsmap.put(tempvo.getRq().substring(0, 7), new Object[]{DZFBoolean.FALSE,period_ye});
				}
				
				/** 有余额无发生不显示 */
				if(monthvalueabs.doubleValue() == 0){
					bwshowfsmap.put(tempvo.getRq().substring(0, 7), new Object[]{DZFBoolean.TRUE,""});
				}else{
					String period_ye = DateUtils.getPeriodStartDate(tempvo.getRq().substring(0, 7)).toString();
					bwshowfsmap.put(tempvo.getRq().substring(0, 7), new Object[]{DZFBoolean.FALSE,period_ye});
				}
				monthvalueabs = DZFDouble.ZERO_DBL;
			}
			
			if(tempvo.getZy()!=null && "本年累计".equals(tempvo.getZy()) && ReportUtil.bSysZy(tempvo)){
				tempvo.setJf(jftotalmap.get(year));
				tempvo.setDf(dftotalmap.get(year));
				
				/** ----------原币---------- */
				tempvo.setYbjf(ybjftotalmap.get(year));
				tempvo.setYbdf(ybdftotalmap.get(year));
				
				/** ---------数量----------*/
				tempvo.setJfnum(jfnumtotalmap.get(year));
				tempvo.setDfnum(dfnumtotalmap.get(year));
			}
			
			if(tempvo.getYe().doubleValue() == 0){
				tempvo.setFx("平");
			}else if(tempvo.getYe().doubleValue()>0){
				tempvo.setFx("借");
			}else{
				tempvo.setFx("贷");
			}
			listemp2.add(tempvo);
		}
		
		String qcrq = "";
		for(FzKmmxVO mxvo:listemp2){
			if(mxvo.getYe().doubleValue()<0){
				mxvo.setYe(mxvo.getYe().multiply(-1));
			}
			
			if(mxvo.getYbye().doubleValue()<0){
				mxvo.setYbye(mxvo.getYbye().multiply(-1));
			}
			if(ishowfs!=null && !ishowfs.booleanValue() && !("期初余额".equals(mxvo.getZy()) && ReportUtil.bSysZy(mxvo))){
				Object[] objs = bwshowfsmap.get(mxvo.getRq().substring(0, 7));
				DZFBoolean obj1 = (DZFBoolean) objs[0];
				if(obj1.booleanValue()){
					continue;
				}else if(StringUtil.isEmpty(qcrq)) {
					qcrq = (String)objs[1];
				}
			}
			
			if(xswyewfs!=null && xswyewfs.booleanValue() && !("期初余额".equals(mxvo.getZy()) && ReportUtil.bSysZy(mxvo))){
				Object[] objs = bwyewfsmap.get(mxvo.getRq().substring(0, 7));
				DZFBoolean obj1 = (DZFBoolean) objs[0];
				if(obj1.booleanValue()){
					continue;
				}else if(StringUtil.isEmpty(qcrq)) {
					qcrq = (String)objs[1];
				}
			} 
			
			listemp3.add(mxvo);
		}
		if((xswyewfs!=null && xswyewfs.booleanValue()) || (ishowfs!=null && !ishowfs.booleanValue())){
			if(listemp3.size() == 1){//只有期初余额就不显示
				listemp3.remove(0);
			}else{
				listemp3.get(0).setRq(qcrq);
			}
		}
		return listemp3;
	}

	private void putYearMapValue(Map<String, DZFDouble> jftotalmap, String year, DZFDouble value) {
		if(jftotalmap.containsKey(year)){
			jftotalmap.put(year, SafeCompute.add(jftotalmap.get(year), value));
		}else{
			jftotalmap.put(year, value== null ? DZFDouble.ZERO_DBL: value);//tempvo.getJf()
		}
	}
	

	/**
	 * 发生的结果集
	 * @param paramavo
	 * @return
	 */
	private Map<String,List<FzKmmxVO>> queryFsVos(KmReoprtQueryParamVO paramavo,List<String> kmlist,String kmqrysql,Map<String,YntCpaccountVO> cpamap) {
		DZFDate begindate =  DateUtils.getPeriodStartDate(DateUtils.getPeriod(new DZFDate(paramavo.getBegindate1().getYear() + "-01-01")));
		String pk_corp = paramavo.getPk_corp();
		CorpVO corpvo=zxkjPlatformService.queryCorpByPk(pk_corp);
		DZFDate corpdate =corpvo.getBegindate();
		List<FzKmmxVO> vec = null;
		if (corpdate.after(begindate)) {
			vec = getKmFSByPeriodQC(pk_corp, paramavo.getIshasjz(), begindate, paramavo.getEnddate(),kmqrysql,paramavo.getPk_currency());
		} else {
			vec = getKmFSByPeriod(pk_corp, paramavo.getIshasjz(),begindate, paramavo.getEnddate(),kmqrysql,paramavo.getPk_currency());
		}
		/** 查询的发生额 */
		Map<String,List<FzKmmxVO>> fsmap = new HashMap<String,List<FzKmmxVO>>();
		FzKmmxVO tkmmxvo1 = null;
		if(vec!= null && vec.size()>0){
			String key =  null;
			FzKmmxVO tkmmxvo = null;
			for(FzKmmxVO fzmxvo:vec){
				for(int i=1;i<11;i++){
				     key = (String) fzmxvo.getAttributeValue("fzhsx"+i);
					if(StringUtil.isEmpty(key)){
						continue;
					}
					for(int k = 0;k< 2;k++){
						if(k == 0){
							key = (String) fzmxvo.getAttributeValue("fzhsx"+i);
						}else{
							key = (String) fzmxvo.getAttributeValue("fzhsx"+i) + "~"+fzmxvo.getPk_accsubj();
						}
						tkmmxvo1 = new FzKmmxVO();
						BeanUtils.copyNotNullProperties(fzmxvo, tkmmxvo1);
						putFsMap(fsmap, tkmmxvo1, key);
						if(k == 1){
							/** 父级赋值 */
							List<String> parent_lists = getParentKey(fzmxvo.getPk_accsubj(), cpamap);
							if(parent_lists!=null && parent_lists.size()>0){
								for(String str:parent_lists){
									tkmmxvo = new FzKmmxVO();
									BeanUtils.copyNotNullProperties(fzmxvo, tkmmxvo);
									tkmmxvo.setKm(str);
									tkmmxvo.setPk_accsubj(str);
									putFsMap(fsmap, tkmmxvo, (String) fzmxvo.getAttributeValue("fzhsx"+i) + "~"+str);
								}
							}
						}
					}
					
					
				}
			}
		}
		return fsmap;
	}

	private void putFsMap(Map<String, List<FzKmmxVO>> fsmap, FzKmmxVO fzmxvo, String key) {
		if(fsmap.containsKey(key)){
			fsmap.get(key).add(fzmxvo);
		}else{
			List<FzKmmxVO> fzkmmsvos = new ArrayList<FzKmmxVO>();
			fzkmmsvos.add(fzmxvo);
			fsmap.put(key, fzkmmsvos);
		}
	}

	
	public List<FzKmmxVO> getKmFSByPeriodQC(String pk_corp, DZFBoolean ishasjz, DZFDate start, DZFDate end,String kmqrysql,
			String pk_currency )
			throws DZFWarpException {
		SQLParameter parameter = new SQLParameter();
		String sql1 = getQuerySqlByPeriodForQC(start, end, pk_corp, ishasjz,parameter,kmqrysql,pk_currency);
		ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1, parameter, new BeanListProcessor(FzKmmxVO.class));

		List<FzKmmxVO> vec_details = new ArrayList<FzKmmxVO>();
		/** 累计借方 */
		DZFDouble ljJF = DZFDouble.ZERO_DBL;
		/** 累计贷方 */
		DZFDouble ljDF = DZFDouble.ZERO_DBL;
		if (result1 != null && !result1.isEmpty()) {
			for (Object o : result1) {
				FzKmmxVO vo = (FzKmmxVO) o;
				if(vo.getJf() ==null && vo.getDf() ==null){
					continue;
				}
				vo.setJf(VoUtils.getDZFDouble(vo.getJf()));
				vo.setDf(VoUtils.getDZFDouble(vo.getDf()));
				
				/** 原币借方 */
				vo.setYbjf(VoUtils.getDZFDouble(vo.getYbjf()));
				/** 原币贷方 */
				vo.setYbdf(VoUtils.getDZFDouble(vo.getYbdf()));
				
				/** 成本的凭证不考虑数量 */
				if("HP34".equals( vo.getSourcebilltype()) ){
					vo.setJfnum(DZFDouble.ZERO_DBL);
					vo.setDfnum(DZFDouble.ZERO_DBL);
				}else{
					vo.setJfnum(VoUtils.getDZFDouble(vo.getJfnum()));
					vo.setDfnum(VoUtils.getDZFDouble(vo.getDfnum()));
				}
				
				ljJF = ljJF.add(vo.getJf());
				ljDF = ljDF.add(vo.getDf());
				if ("0".equals(vo.getFx())) {
					/** 借方 */
					vo.setFx("借");
				} else {
					/** 贷方 */
					vo.setFx("贷");
				}
				vec_details.add(vo);
			}
		}
		return vec_details;
	}
	
	/**
	 * 包含期初
	 * 
	 * @param start
	 * @param end
	 * @param kmwhere
	 * @param pk_corp
	 * @param ishasjz
	 * @param ishassh
	 * @return
	 */
	protected String getQuerySqlByPeriodForQC(DZFDate start, DZFDate end, String pk_corp, 
			DZFBoolean ishasjz,SQLParameter parameter,String kmqrysql,String pk_currency ) {
		String startperiod = DateUtils.getPeriod(start);
		StringBuffer sb = new StringBuffer();
		sb.append(" select * from  ");
		sb.append("  ( select h.period as qj,h.doperatedate as rq,h.pzh as pzh, a.accountcode,a.pk_corp_account as km ,b.zy, "  );
		sb.append("    b.jfmny as jf ,b.ybjfmny as ybjf ,b.dfmny as df ,b.ybdfmny as ybdf , " );
		sb.append("  a.direction as fx,b.pk_tzpz_h ,b.pk_tzpz_b,  ");
		sb.append("     b.fzhsx1 as fzhsx1, b.fzhsx2 as fzhsx2, b.fzhsx3 as fzhsx3, b.fzhsx4 as fzhsx4, b.fzhsx5 as fzhsx5, " );
	    /** 启用库存  存货作为辅助核算 */
		sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6, ");
		sb.append("    b.fzhsx7 as fzhsx7, b.fzhsx8 as fzhsx8, b.fzhsx9 as fzhsx9, b.fzhsx10 as fzhsx10,b.pk_accsubj " );
		sb.append("   ,c.currencycode as bz  , to_char(b.nrate) as hl, ");
		sb.append(" case when  nvl(b.jfmny,0) !=0  then b.nnumber else 0  end as jfnum,  ");
		sb.append(" case  when nvl(b.dfmny,0) !=0 then b.nnumber else 0 end as dfnum , h.sourcebilltype as sourcebilltype ");
		sb.append("     from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sb.append("     inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account  ");
		sb.append("     inner join  "+kmqrysql+" on tempsql.pk_corp_account  = b.pk_accsubj");
		sb.append("      left join  ynt_bd_currency c  on c.pk_currency=b.pk_currency  ");
		sb.append("     where nvl(h.dr,0)=0 and nvl(b.dr,0)=0 ");
		Date dd = start.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), 1);
		DZFDate d1 = new DZFDate(dd);
		sb.append(" and h.doperatedate>='").append(d1);
		dd = end.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
		d1 = new DZFDate(dd);
		sb.append("' and h.doperatedate<='").append(d1).append("'");
		sb.append(" and h.pk_corp=?");
		parameter.addParam(pk_corp);
		sb.append(" and a.pk_corp = ? ");
		parameter.addParam(pk_corp);
		if (ishasjz.booleanValue()) {
			/**  不包含未记账，即只查询已记账的 */
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		if(!StringUtil.isEmpty(pk_currency)){
			sb.append(" and b.pk_currency =  '"+pk_currency+"'");
		}
		sb.append(" order by h.pzh asc  )    ");
		StringBuffer qcyesql =new StringBuffer();
		qcyesql.append("  union all  ");
		qcyesql.append("  select '"+startperiod+"' as qj, '"+startperiod+"'||'-01' as rq, ");
		qcyesql.append("  ''as pzh,a.accountcode,a.pk_corp_account as km,'' as zy, ");
		qcyesql.append("  b.yearjffse as jf, b.ybyearjffse as ybjf,b.yeardffse as df, b.ybyeardffse as ybdf,");
		qcyesql.append("  1 as fx ,'' as  pk_tzpz_h , '' as pk_tzpz_b,  ");
		qcyesql.append("  b.fzhsx1 as fzhsx1, b.fzhsx2 as fzhsx2, b.fzhsx3 as fzhsx3, b.fzhsx4 as fzhsx4, b.fzhsx5 as fzhsx5, " );
		qcyesql.append("  b.fzhsx6 as fzhsx6, b.fzhsx7 as fzhsx7, b.fzhsx8 as fzhsx8, b.fzhsx9 as fzhsx9, b.fzhsx10 as fzhsx10,b.pk_accsubj " );
		qcyesql.append("  ,c.currencycode as  bz, '0.00' as hl, ");
		qcyesql.append(" b.bnfsnum   as jfnum,  b.bndffsnum as dfnum  , '' as sourcebilltype  ");
		qcyesql.append("  from ynt_fzhsqc b inner join ynt_cpaccount a  on b.pk_accsubj = a.pk_corp_account and a.isleaf='Y'   ");
		qcyesql.append("     inner join  "+kmqrysql+" on tempsql.pk_corp_account  = b.pk_accsubj");
		qcyesql.append("  left join  ynt_bd_currency c  on c.pk_currency=a.pk_currency ");
		qcyesql.append("  where (1=1) and b.pk_corp='" + pk_corp + "' and nvl(b.dr,0)=0 and a.pk_corp = '"+pk_corp+"'");
		if(!StringUtil.isEmpty(pk_currency)){
			qcyesql.append(" and b.pk_currency =  '"+pk_currency+"'");
		}
		return sb.toString() + qcyesql.toString();
	}
	
	public List<FzKmmxVO> getKmFSByPeriod(String pk_corp, DZFBoolean ishasjz,DZFDate start, DZFDate end,String kmqrysql,
			String pk_currency )
			throws DZFWarpException {
		SQLParameter parameter = new SQLParameter();
		String sql1 = getQuerySqlByPeriod(start, end, pk_corp, ishasjz,parameter,kmqrysql,pk_currency);
		ArrayList result1 = (ArrayList) singleObjectBO.executeQuery(sql1, parameter, new BeanListProcessor(FzKmmxVO.class));

		List<FzKmmxVO> vec_details = new ArrayList<FzKmmxVO>();
		/** 累计借方 */
		DZFDouble ljJF = DZFDouble.ZERO_DBL;
		/** 累计贷方 */
		DZFDouble ljDF = DZFDouble.ZERO_DBL;
		if (result1 != null && !result1.isEmpty()) {
			for (Object o : result1) {
				FzKmmxVO vo = (FzKmmxVO) o;
				vo.setJf(VoUtils.getDZFDouble(vo.getJf()));
				vo.setDf(VoUtils.getDZFDouble(vo.getDf()));
				
				/** 原币 */
				vo.setYbjf(VoUtils.getDZFDouble(vo.getYbjf()));
				vo.setYbdf(VoUtils.getDZFDouble(vo.getYbdf()));
				
				/** 数量计算 */
				/** 成本的凭证不考虑数量 */
				if("HP34".equals( vo.getSourcebilltype()) ){
					vo.setJfnum(DZFDouble.ZERO_DBL);
					vo.setDfnum(DZFDouble.ZERO_DBL);
				}else{
					vo.setJfnum(VoUtils.getDZFDouble(vo.getJfnum()));
					vo.setDfnum(VoUtils.getDZFDouble(vo.getDfnum()));
				}
				
				ljJF = ljJF.add(vo.getJf());
				ljDF = ljDF.add(vo.getDf());
				if ("0".equals(vo.getFx())) {
					/** 借方 */
					vo.setFx("借");
				} else {
					/** 贷方 */
					vo.setFx("贷");
				}

				vec_details.add(vo);
			}
		}
		return vec_details;
	}
	
	protected String getQuerySqlByPeriod(DZFDate start, DZFDate end, String pk_corp, DZFBoolean ishasjz,
			SQLParameter parameter,String kmqrysql,String pk_currency ) {
		StringBuffer sb = new StringBuffer();
		sb.append(" select h.period as qj,h.doperatedate as rq,h.pzh as pzh, ");
		sb.append("   a.accountcode,a.pk_corp_account as km ,b.zy ,  c.currencycode as bz  , b.nrate as hl,"  );
		sb.append("  b.jfmny as jf , b.ybjfmny as ybjf ,b.dfmny as df ,  b.ybdfmny as ybdf ,  ");
		sb.append("   b.fzhsx1 as fzhsx1, b.fzhsx2 as fzhsx2, b.fzhsx3 as fzhsx3, b.fzhsx4 as fzhsx4, b.fzhsx5 as fzhsx5, " );
	    /** 启用库存  存货作为辅助核算 */
		sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6, ");
		sb.append("   b.fzhsx7 as fzhsx7, b.fzhsx8 as fzhsx8, b.fzhsx9 as fzhsx9, b.fzhsx10 as fzhsx0, " );
		sb.append("   a.direction as fx, b.pk_tzpz_h ,b.pk_tzpz_b,b.pk_accsubj, ");
		sb.append(" case when nvl(b.jfmny,0) !=0 then b.nnumber else 0  end as jfnum,  ");
		sb.append(" case when nvl(b.dfmny,0) !=0  then b.nnumber else 0 end as dfnum, ");
		sb.append(" h.sourcebilltype as sourcebilltype ");
		sb.append("   from ynt_tzpz_b b  ");
		sb.append("   inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h ");
		sb.append("   inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account  ");
		sb.append("  inner join  "+ kmqrysql + " on b.pk_accsubj =tempsql.pk_corp_account ");
		sb.append("   left join  ynt_bd_currency c  on c.pk_currency=b.pk_currency  ");
		sb.append("   where nvl(h.dr,0)=0 and nvl(b.dr,0)=0  " );
		Date dd = start.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), 1);
		DZFDate d1 = new DZFDate(dd);
		sb.append(" and h.doperatedate>='").append(d1);
		dd = end.toDate();
		dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
		d1 = new DZFDate(dd);
		sb.append("' and h.doperatedate<='").append(d1).append("'");
		parameter.addParam(pk_corp);
		sb.append(" and h.pk_corp= ?");
		parameter.addParam(pk_corp);
		sb.append("  and a.pk_corp = ? ");
		if (ishasjz.booleanValue()) {
			/**  不包含未记账，即只查询已记账的 */
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		if(!StringUtil.isEmpty(pk_currency)){
			sb.append(" and b.pk_currency = '"+pk_currency+"'");
		}
		sb.append(" order by h.pzh asc ");
		return sb.toString();
	}
	
	/**
	 * 期初的结果集
	 * @param paramavo
	 * @return
	 */
	private Map<String,FzKmmxVO> queryQcVos(KmReoprtQueryParamVO paramavo,List<String> kmlist,String kmqrysql ,Map<String,YntCpaccountVO> cpamap ) throws DZFWarpException {
		Map<String,FzKmmxVO> qcmap = new HashMap<String,FzKmmxVO>();
		/** 如果查询日期在建账日期前 */
		if(StringUtil.isEmpty(paramavo.getPk_corp())){
			throw new BusinessException("公司信息不能为空!");
		}
		if(paramavo.getBegindate1() == null){
			throw new BusinessException("查询开始日期不能为空!");
		}
		if(paramavo.getEnddate() == null){
			throw new BusinessException("查询结束日期不能为空!");
		}
		DZFDate begindate =  DateUtils.getPeriodStartDate(DateUtils.getPeriod(new DZFDate(paramavo.getBegindate1().getYear() + "-01-01")));
		CorpVO corpvo = zxkjPlatformService.queryCorpByPk(paramavo.getPk_corp()) ;
		DZFDate beindate = corpvo.getBegindate();
		String maxperiod = getMAXPeriod(paramavo.getPk_corp(), begindate);
		String min_period = DateUtils.getPeriod(paramavo.getBegindate1());
		DZFDate qcstart = null;
		DZFDate qcend = null;
		String wherepart = "";
		SQLParameter sp = new SQLParameter();
		if(!StringUtil.isEmpty(paramavo.getPk_currency())){
			wherepart = "  and pk_currency = ?";
			sp.addParam(paramavo.getPk_currency());
		}
		/** 无年结 */
		if ((beindate.after(begindate)) || StringUtil.isEmpty(maxperiod)){
			qcend = begindate.getDateBefore(1);
			sp.addParam(paramavo.getPk_corp());
			
			FzhsqcVO[] fzhsqcvos =  (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class,"nvl(dr,0)=0"+wherepart+" and pk_corp = ? and "+SqlUtil.buildSqlForIn("pk_accsubj", kmlist.toArray(new String[0])), sp);
			if(fzhsqcvos!= null && fzhsqcvos.length>0){
				for(FzhsqcVO fzvo :fzhsqcvos){
					if (beindate.after(begindate)) {
						putQcMap(qcmap, cpamap, fzvo.getPk_accsubj(),min_period,fzvo,fzvo.getYearqc(),fzvo.getYbyearqc(),
								DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,fzvo.getBnqcnum());
					}else{
						putQcMap(qcmap, cpamap, fzvo.getPk_accsubj(),min_period,fzvo,fzvo.getThismonthqc(),fzvo.getYbthismonthqc(),
								DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,fzvo.getMonthqmnum());
					}
			   }
			}
		}else if (min_period.compareTo(maxperiod) > 0) {/** 开始日期大于年结日期 */
			sp.addParam(paramavo.getPk_corp());
			sp.addParam(maxperiod);
			KMQMJZVO[] kmqmjzvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class, "nvl(dr,0)=0 "+wherepart+"and pk_corp=?  and period= ? and nvl(dr,0)=0    and "+SqlUtil.buildSqlForIn("pk_accsubj", kmlist.toArray(new String[0]))+"  order by period", sp);
			if(kmqmjzvos!=null && kmqmjzvos.length>0){
				for(KMQMJZVO kmqmjz:kmqmjzvos){
					putQcMap(qcmap, cpamap, kmqmjz.getPk_accsubj(),min_period, kmqmjz,kmqmjz.getThismonthqc(),kmqmjz.getYbthismonthqc(),
							DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,kmqmjz.getMonthqmnum());
				}
			}
			qcend = begindate.getDateBefore(1);
			if(qcstart != null){
				qcstart = new DZFDate(new Date(qcstart.toDate().getYear(), qcstart.toDate().getMonth() + 1, 1));
			}
		}else{
			sp.addParam(paramavo.getPk_corp());
			String nextperiod =String.valueOf((Integer.parseInt(maxperiod.substring(0, 4))+1))  ;
			sp.addParam(nextperiod+"-12");
			sp.addParam(nextperiod+"-12");
			List<KMQMJZVO> kmqmjzvos = (List<KMQMJZVO>) singleObjectBO .retrieveByClause(KMQMJZVO.class, " nvl(dr,0)=0"+ wherepart+" and pk_corp=? and period>=? and period<=? and nvl(dr,0)=0   and "+SqlUtil.buildSqlForIn("pk_accsubj", kmlist.toArray(new String[0]))+"  order by period ", sp);
			if(kmqmjzvos!=null && kmqmjzvos.size()>0){
				for(KMQMJZVO kmqmjz:kmqmjzvos){
					putQcMap(qcmap, cpamap, kmqmjz.getPk_accsubj(),min_period,
							kmqmjz,kmqmjz.getThismonthqc(),kmqmjz.getYbthismonthqc(),
							DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,DZFDouble.ZERO_DBL,kmqmjz.getMonthqmnum());
				}
			}
		}
		boolean bb = qcstart != null || qcend != null;
		if (bb && qcstart != null && qcend != null) {
			bb = qcstart.compareTo(qcend) < 0;
		}
		if (bb) {/** 期初*/
			 List<KmZzVO> listkmzzvo =  getQCKmFSByPeriod(paramavo.getPk_corp(), paramavo.getIshasjz(), qcstart, qcend,kmqrysql,paramavo.getPk_currency() );
			 String key;
			 List<String> parentlist;
			 if(listkmzzvo!=null && listkmzzvo.size()>0){
				 for(KmZzVO zzvo:listkmzzvo){
					 for(int i =1;i<11;i++){
						 key = (String) zzvo.getAttributeValue("fzhsx"+String.valueOf(i));
						if(StringUtil.isEmpty(key)){
							continue;
						}
						
						for(int k=0;k<2;k++){
							if(k == 0){
								putFsToQCMap(zzvo, key, zzvo.getKm(),cpamap,qcmap);
							}else{
								putFsToQCMap(zzvo, key+"~"+zzvo.getKm(), zzvo.getKm(),cpamap,qcmap);
								/** 赋值上级数据 */
								parentlist = getParentKey(zzvo.getKm(), cpamap);
								if(parentlist!=null && parentlist.size()>0){
									for(String parentkey:parentlist){
										putFsToQCMap(zzvo, key+"~"+parentkey, parentkey,cpamap,qcmap);
									}
								}
							}
						}
					 }
				 }
			 }
		}
		return qcmap;
	}
	
	
 /**
	 * 发生的数据添加的期初里面
	 * @param vec0
	 * @param mp
	 * @param qcfzmap
	 */
	private void putFsToQCMap(KmZzVO zzvo, String key , String pk_accsubj,Map<String,YntCpaccountVO> cpamap, Map<String, FzKmmxVO> qcfzmap) {
		FzKmmxVO mxzvo = null;
		YntCpaccountVO accountvo = null;
		DZFDouble tempvalue = DZFDouble.ZERO_DBL;
		/** 原币 */
		DZFDouble ybtempvalue = DZFDouble.ZERO_DBL;
		
		/** 数量*/
		DZFDouble tempnum = DZFDouble.ZERO_DBL;
		
		accountvo = cpamap.get(pk_accsubj);
		
		tempvalue =  SafeCompute.sub(zzvo.getJf(), zzvo.getDf());
		
		/** 原币 */
		ybtempvalue = SafeCompute.sub(zzvo.getYbjf(), zzvo.getYbdf());
		
		/** 数量 */
		tempnum = SafeCompute.sub(zzvo.getJfnnumber(), zzvo.getDfnnumber());
		
		if(qcfzmap.containsKey(key.toString())){
			mxzvo = qcfzmap.get(key.toString());
			if(accountvo.getDirection() == 0){
				mxzvo.setFx("借");
			}else{
				mxzvo.setFx("贷");
			}
			mxzvo.setKm(mxzvo.getKm()+"~"+pk_accsubj);
			mxzvo.setYe(SafeCompute.add(mxzvo.getYe(), tempvalue));
			
			/** 原币余额 */
			mxzvo.setYbye(SafeCompute.add(mxzvo.getYbye(), ybtempvalue));
			
			/** 余额数量 */
			mxzvo.setYenum(SafeCompute.add(mxzvo.getYenum(), tempnum));
		}else{
			mxzvo = new FzKmmxVO();
			if(accountvo.getDirection() == 0){
				mxzvo.setFx("借");
			}else{
				mxzvo.setFx("贷");
			}
			mxzvo.setPk_accsubj(key.toString());
			mxzvo.setYe(tempvalue);
			
			/** 原币值 */
			mxzvo.setYbye(ybtempvalue);
			mxzvo.setKm(pk_accsubj);
			
			mxzvo.setYenum(tempnum);
			
			qcfzmap.put(key.toString(), mxzvo);
		}
	}

	
	public List<KmZzVO> getQCKmFSByPeriod(String pk_corp, DZFBoolean ishasjz,DZFDate start, DZFDate end,String kmqrysql,String pk_currency )
			throws DZFWarpException {
		if (start == null && end == null)
			return null;
		SQLParameter parameter = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append(" select b.jfmny as jf ,b.ybjfmny as ybjf , b.dfmny as df,  b.ybdfmny as ybdf,b.pk_accsubj  as km , ");
		sb.append(" b.fzhsx1 as fzhsx1, b.fzhsx2 as fzhsx2, b.fzhsx3 as fzhsx3, b.fzhsx4 as fzhsx4, b.fzhsx5 as fzhsx5, " );
		sb.append(" case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6, ");
		sb.append("  b.fzhsx7 as fzhsx7, b.fzhsx8 as fzhsx8, b.fzhsx9 as fzhsx9, b.fzhsx10 as fzhsx10, " );
		sb.append(" case when nvl(b.jfmny,0) !=0 then b.nnumber else 0  end as jfnnumber,  ");
		sb.append(" case when nvl(b.dfmny,0) !=0 then b.nnumber else 0 end as dfnnumber ");
		sb.append( "  from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h");
		//sb.append( "    inner join  ynt_cpaccount a  on b.pk_accsubj=a.pk_corp_account  " );
		sb.append("  inner join  "+ kmqrysql + " on b.pk_accsubj =tempsql.pk_corp_account ");
		sb.append( "    where nvl(b.dr,0)=0 " );
		Date dd = null;
		if (start != null) {
			dd = start.toDate();
			dd = new Date(dd.getYear(), dd.getMonth(), 1);
			DZFDate d1 = new DZFDate(dd);
			sb.append(" and h.doperatedate>='").append(d1).append("'");
		}
		if (end != null) {
			dd = end.toDate();
			dd = new Date(dd.getYear(), dd.getMonth(), end.getDaysMonth());
			DZFDate d1 = new DZFDate(dd);
			sb.append(" and h.doperatedate<='").append(d1).append("'");
		}
		sb.append(" and h.pk_corp='" + pk_corp + "'");
		if (ishasjz.booleanValue()) {
			sb.append(" and h.ishasjz='Y' ");
			sb.append(" and h.vbillstatus=1 ");
		}
		if(!StringUtil.isEmpty(pk_currency)){
			sb.append(" and b.pk_currency = ? ");
			parameter.addParam(pk_currency);
		}
		return (List<KmZzVO>) singleObjectBO.executeQuery(sb.toString(), parameter, new BeanListProcessor(KmZzVO.class));
	}
	

	private void putQcMap(Map<String, FzKmmxVO> qcmap, Map<String, YntCpaccountVO> cpamap,String pk_accsubj,String rq,
			SuperVO fzvo,DZFDouble qcye,DZFDouble ybqcye,DZFDouble jffs,DZFDouble ybjffs,
			DZFDouble dffs,DZFDouble ybdffs,DZFDouble qcnum ) {
		/** 本年借方发生数量 */
		DZFDouble bnfsnum = (DZFDouble) fzvo.getAttributeValue("bnfsnum");
		/** 本年贷方发生数量 */
		DZFDouble bndffsnum = (DZFDouble) fzvo.getAttributeValue("bndffsnum");
		
		for(int i=1;i<11;i++){
			String key = (String) fzvo.getAttributeValue("fzhsx"+i);
			if(StringUtil.isEmpty(key)){
				continue;
			}
			
			for(int k =0;k<2;k++){
				if(k == 0){
					key =  (String) fzvo.getAttributeValue("fzhsx"+i);//辅助核算本身
					/** 最末级 */
					putQcMap1(qcmap, cpamap, pk_accsubj, rq, qcye, ybqcye, jffs, ybjffs, dffs, ybdffs, qcnum, key);
				}else{
					key =  (String) fzvo.getAttributeValue("fzhsx"+i) + "~" + pk_accsubj;//辅助核算+科目
					/** 最末级 */
					putQcMap1(qcmap, cpamap, pk_accsubj, rq, qcye, ybqcye, jffs, ybjffs, dffs, ybdffs, qcnum, key);
				}
				/** 获取所有的key */
				if(k ==1){//如果有上级，查找所有的上级key
					List<String> parentkeys = getParentKey(pk_accsubj,cpamap);
					if (parentkeys != null && parentkeys.size() > 0) {
						for (String str : parentkeys) {
							key = (String) fzvo.getAttributeValue("fzhsx" + i) + "~" + str;
							putQcMap1(qcmap, cpamap, str, rq, qcye, ybqcye, jffs, ybjffs, dffs, ybdffs, qcnum, key);
						}
					}
				}
			}
			
		}
	}

	private List<String> getParentKey(String pk_accsubj, Map<String, YntCpaccountVO> cpamap) {
		YntCpaccountVO vo = cpamap.get(pk_accsubj);
		List<String> lists = new ArrayList<String>();
		for(Entry<String, YntCpaccountVO> entry:cpamap.entrySet()){
			if(vo.getAccountcode().startsWith(entry.getValue().getAccountcode())
					&& entry.getValue().getAccountcode().length() < vo.getAccountcode().length() ){
				lists.add(entry.getKey());
			}
		}
		return lists;
	}

	private void putQcMap1(Map<String, FzKmmxVO> qcmap, Map<String, YntCpaccountVO> cpamap, String pk_accsubj,
			String rq, DZFDouble qcye, DZFDouble ybqcye, DZFDouble jffs, DZFDouble ybjffs, DZFDouble dffs,
			DZFDouble ybdffs, DZFDouble qcnum, String key) {
		YntCpaccountVO accountvo = cpamap.get(pk_accsubj);
		if(qcmap.containsKey(key)){
			FzKmmxVO kmmxvo = qcmap.get(key);
			kmmxvo.setJf(SafeCompute.add(kmmxvo.getJf(), jffs));
			kmmxvo.setDf(SafeCompute.add(kmmxvo.getDf(), dffs));
			if(VoUtils.getDZFDouble(qcye).doubleValue()!=0){//待完善 To be perfect
				if(accountvo!=null && accountvo.getDirection().intValue() == 0){
					kmmxvo.setYe(VoUtils.getDZFDouble(kmmxvo.getYe()).add(VoUtils.getDZFDouble(qcye)));
					kmmxvo.setYbye(VoUtils.getDZFDouble(kmmxvo.getYbye()).add(VoUtils.getDZFDouble(ybqcye)));//原币
					
					kmmxvo.setYenum(VoUtils.getDZFDouble(kmmxvo.getYenum()).add(VoUtils.getDZFDouble(qcnum)));
				}else{
					kmmxvo.setYe(VoUtils.getDZFDouble(kmmxvo.getYe()).sub(VoUtils.getDZFDouble(qcye)));
					kmmxvo.setYbye(VoUtils.getDZFDouble(kmmxvo.getYbye()).sub(VoUtils.getDZFDouble(ybqcye)));//原币
					
					kmmxvo.setYenum(VoUtils.getDZFDouble(kmmxvo.getYenum()).sub(VoUtils.getDZFDouble(qcnum)));
				}
			}else{
				if(accountvo!=null && accountvo.getDirection().intValue() == 0){
					kmmxvo.setYe(VoUtils.getDZFDouble(kmmxvo.getYe()).add(VoUtils.getDZFDouble(jffs)).sub(VoUtils.getDZFDouble(dffs)));
					kmmxvo.setYbye(VoUtils.getDZFDouble(kmmxvo.getYbye()).add(VoUtils.getDZFDouble(ybjffs)).sub(VoUtils.getDZFDouble(ybdffs)));//原币
				}else{
					kmmxvo.setYe(VoUtils.getDZFDouble(kmmxvo.getYe()).add(VoUtils.getDZFDouble(dffs)).sub(VoUtils.getDZFDouble(jffs)));
					kmmxvo.setYbye(VoUtils.getDZFDouble(kmmxvo.getYbye()).add(VoUtils.getDZFDouble(ybdffs)).sub(VoUtils.getDZFDouble(ybjffs)));//原币
				}
			}
		    if(kmmxvo.getYe().doubleValue() == 0){
				kmmxvo.setFx("平");
			}else if(kmmxvo.getYe().doubleValue()>0){
				kmmxvo.setFx("借");
			}else  if(kmmxvo.getYe().doubleValue()<0){
				kmmxvo.setFx("贷");
			}
		    kmmxvo.setKm(kmmxvo.getKm() + "~" +pk_accsubj);
		    kmmxvo.setPk_accsubj(pk_accsubj);
			qcmap.put(key, kmmxvo);
		}else{
			FzKmmxVO  kmmxvo = new FzKmmxVO();
			YntCpaccountVO cpavo = cpamap.get(pk_accsubj);
			if(accountvo!=null && accountvo.getDirection().intValue() == 0){
				kmmxvo.setYe(VoUtils.getDZFDouble(qcye));
				
				kmmxvo.setYbye(VoUtils.getDZFDouble(ybqcye));//原币
				
				kmmxvo.setYenum(qcnum);//数量余额
			}else{
				kmmxvo.setYe(VoUtils.getDZFDouble(qcye).multiply(-1));
				
				kmmxvo.setYbye(VoUtils.getDZFDouble(ybqcye).multiply(-1));//原币
				
				kmmxvo.setYenum(VoUtils.getDZFDouble(qcnum).multiply(-1));//数量余额
			}
			if(kmmxvo.getYe().doubleValue() == 0){
				kmmxvo.setFx("平");
			}else if(kmmxvo.getYe().doubleValue()>0){
				kmmxvo.setFx("借");
			}else  if(kmmxvo.getYe().doubleValue()<0){
				kmmxvo.setFx("贷");
			}
			kmmxvo.setRq(rq.toString());
			kmmxvo.setFzxm(key);
			if(cpavo!=null){
				kmmxvo.setKmmc(cpavo.getAccountname());
				kmmxvo.setKmmx(cpavo.getAccountcode());
			}
			kmmxvo.setJf(VoUtils.getDZFDouble(jffs));
			kmmxvo.setDf(VoUtils.getDZFDouble(dffs));
			
			kmmxvo.setYbjf(VoUtils.getDZFDouble(ybjffs));//原币
			kmmxvo.setYbdf(VoUtils.getDZFDouble(ybdffs));//原币贷方
			
			kmmxvo.setJfnum(DZFDouble.ZERO_DBL);//借方数量
			kmmxvo.setDfnum(DZFDouble.ZERO_DBL);//贷方数量
			
			kmmxvo.setKm(pk_accsubj);
			kmmxvo.setPk_accsubj(pk_accsubj);
			
			kmmxvo.setZy("期初余额");
			kmmxvo.setBsyszy(DZFBoolean.TRUE);
			qcmap.put(key, kmmxvo);
		}
	}
	

	/**
	 * 获取最晚的结账日期
	 * @param pk_corp
	 * @param enddate 取开始日期
	 * @return
	 * @throws DAOException
	 */
	private String getMAXPeriod(String pk_corp, DZFDate begdate)
			throws DAOException {
		String period = DateUtils.getPeriod(begdate);
		String sql = "select max(period) from YNT_QMJZ where pk_corp='" + pk_corp + "' and period<'" + period + "' and nvl(jzfinish,'N')='Y' and nvl(dr,0)=0 ";
		SQLParameter parameter = new SQLParameter();
		Object[] obj = (Object[]) singleObjectBO.executeQuery(sql, parameter,
				new ArrayProcessor());

		if (obj != null && obj.length > 0) {
			String i = (String) obj[0];
			return i;
		}
		return null;
	}

	@Override
	public Map<String, List<FzKmmxVO>> getAllFzKmmxVos(KmReoprtQueryParamVO paramavo) throws DZFWarpException {
		
		Map<String, List<FzKmmxVO>> resmap = new LinkedHashMap<String, List<FzKmmxVO>>();
		
		CorpVO cpvo = zxkjPlatformService.queryCorpByPk(paramavo.getPk_corp());
		YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(paramavo.getPk_corp());
		Map<String, YntCpaccountVO> kmmap = convertMap(cpavos);
		
		List<String> kmwherepart = getKmwhere(paramavo,true,cpavos);
		String kmqrysql = getKmSql(paramavo, true);
		/** 获取当前有关系的项目 */
		List<String> kmwhere = getKmwhere(paramavo,false,cpavos);
		List<FzKmmxVO> xmvos = getConXm(paramavo,kmwhere,kmmap);// 获取所属的项目(全部项目)
		queryInvtoryXm(xmvos,paramavo,kmwhere,kmmap,cpvo);
		
		List<String> periods = ReportUtil.getPeriods(paramavo.getBegindate1(), paramavo.getEnddate());
		/** 1:查询期初 */
		Map<String, FzKmmxVO> qcmapvos = queryQcVos(paramavo,kmwherepart,kmqrysql,kmmap);
		/** 2:查询发生 */
		Map<String, List<FzKmmxVO>> fsmapvos = queryFsVos(paramavo,kmwherepart,kmqrysql,kmmap);
		/** 3:过滤（项目+科目） **/
		xmfilter(xmvos,qcmapvos,fsmapvos,paramavo.getXswyewfs(),
				paramavo.getIshowfs(),paramavo.getBegindate1(),paramavo.getEnddate(),
				paramavo.getCjq(),paramavo.getCjz(),paramavo.getPk_corp());
		if(xmvos.size() == 0){
			return resmap;
		}
		/** 过滤qcmap ，fsmap */
		Map<String, FzKmmxVO> qcmapvostemp = null;
		Map<String, List<FzKmmxVO>> fsmapvostemp = null;
		List<FzKmmxVO> resvos  = null;
		for(FzKmmxVO tempvo: xmvos){
			String key = tempvo.getId().split("~")[0];
			qcmapvostemp = new HashMap<String, FzKmmxVO>();
			qcmapvostemp.putAll(qcmapvos);
			fsmapvostemp = new HashMap<String, List<FzKmmxVO>>();
			fsmapvostemp.putAll(fsmapvos);
			fsFilter(qcmapvostemp,fsmapvostemp,key);
			/** 对应期末数据 */
			resvos = getResultVos(qcmapvostemp, fsmapvostemp, periods,kmmap,paramavo.getKms_last(),xmvos,paramavo.getXswyewfs(),paramavo.getIshowfs());
			resmap.put(tempvo.getText(), resvos);
		}
		return resmap;
	}
	
	private List<FzKmmxVO> queryInvtoryXm(List<FzKmmxVO> xmvos, KmReoprtQueryParamVO paramavo, List<String> kmList
			,Map<String , YntCpaccountVO> kmmap,CorpVO corp) {

		/**  启用库存的时候*/ 
		if (!IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
			return xmvos;
		} else {
			if (!"000001000000000000000006".equals(paramavo.getFzlb())) {
				return xmvos;
			}
		}
		/** 根据辅助类别 获取对应的辅助项目和相应的科目*/
		String fzxm = paramavo.getFzxm();
		StringBuffer fzxmpart = new StringBuffer();
		if (!StringUtil.isEmpty(fzxm)) {
			if (fzxm.split(",") != null) {
				String[] value = fzxm.split(",");
				for (int i = 0; i < value.length; i++) {
					fzxmpart.append("'" + value[i] + "',");
				}
			}
		}
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.code ,b.name,b.pk_inventory     ");
		sql.append(" from ynt_inventory b  ");
		sql.append(" where nvl(b.dr,0)=0 and b.pk_corp in(?,'000001') ");
		if (fzxmpart.length() > 0) {
			sql.append(" and b.code in(" + fzxmpart.substring(0, fzxmpart.length() - 1) + ")");
		}
		sql.append("  order by b.code");
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramavo.getPk_corp());
		List<InventoryVO> resfzvos = (List<InventoryVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(InventoryVO.class));

		FzKmmxVO fzkmmx = null;
		if (paramavo.getXskm() != null && paramavo.getXskm().booleanValue()) {// 是否显示科目
			YntCpaccountVO[] cpavos = getKmMap(kmList, paramavo.getPk_corp(),kmmap);
			Map<String, List<YntCpaccountVO>> parentlist = getParentKey(cpavos);
			List<FzKmmxVO> kmmxvos =null;
			for (InventoryVO invvo : resfzvos) {
				fzkmmx = new FzKmmxVO();
				fzkmmx.setState("closed");
				fzkmmx.setIskmid(DZFBoolean.FALSE);
				fzkmmx.setText(invvo.getCode() + "_" + invvo.getName());
				fzkmmx.setFzcode(invvo.getCode());
				fzkmmx.setFzname(invvo.getName());
				Integer code = 6;
				// 是否显示科目
				kmmxvos = new ArrayList<FzKmmxVO>();
				for (YntCpaccountVO cpa : cpavos) {
					String value = cpa.getIsfzhs();
					if (StringUtil.isEmpty(value)) {
						continue;
					}
					if (value.length() < code.intValue()) {
						continue;
					}
					String valuetemp = value.substring(code - 1, code);
					if ("1".equals(valuetemp)) {// 是否选中了辅助项目
						List<YntCpaccountVO> parentkeys =parentlist.get(cpa.getPrimaryKey());// getParentKey(cpa, cpavos);
						if(parentkeys!=null && parentkeys.size()>0){
							for(YntCpaccountVO votemp:parentkeys){
								createIntoryFzkmmx(invvo, kmmxvos, votemp);
							}
						}
						createIntoryFzkmmx(invvo, kmmxvos, cpa);
					}
				}
				fzkmmx.setId(invvo.getPk_inventory() + "~" + "");
				fzkmmx.setChildren(kmmxvos.toArray(new FzKmmxVO[0]));
				if (kmmxvos != null && kmmxvos.size() > 0) {
					xmvos.add(fzkmmx);
				}
			}
		}else{
			for (InventoryVO invvo : resfzvos) {
				fzkmmx = new FzKmmxVO();
				fzkmmx.setIskmid(DZFBoolean.FALSE);
				fzkmmx.setFzcode(invvo.getCode());
				fzkmmx.setFzname(invvo.getName());
				fzkmmx.setText(invvo.getCode() + "_" + invvo.getName());
				fzkmmx.setId(invvo.getPk_inventory() + "~" + "");
				xmvos.add(fzkmmx);
			}
		}
		return xmvos;
	}

	private void createIntoryFzkmmx(InventoryVO invvo, List<FzKmmxVO> kmmxvos, YntCpaccountVO cpa) {
		for(FzKmmxVO fzmxvo:kmmxvos){
			if(fzmxvo.getId().equals(invvo.getPk_inventory() + "~" + cpa.getPk_corp_account())){
				return;
			}
		}
		FzKmmxVO tempvo = new FzKmmxVO();
		tempvo.setIskmid(DZFBoolean.TRUE);
		tempvo.setId(invvo.getPk_inventory() + "~" + cpa.getPk_corp_account());
		tempvo.setText(cpa.getAccountcode() + "_" + cpa.getAccountname());
		tempvo.setFzcode(invvo.getCode());
		tempvo.setFzname(invvo.getName());
		tempvo.setKmcode(cpa.getAccountcode());
		tempvo.setLevel(cpa.getAccountlevel());
		tempvo.setKmname(cpa.getAccountname());
		tempvo.setKm(cpa.getPk_corp_account());
		kmmxvos.add(tempvo);
	}
}

package com.dzf.zxkj.platform.service.glic.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.model.jzcl.QMJzsmNoICVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryQcService;
import com.dzf.zxkj.platform.service.glic.IKcCbb;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmclNoicService;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.impl.ZgVoucher;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("ic_rep_kccbbserv")
@Slf4j
public class KcCbbImpl implements IKcCbb {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private ICbComconstant gl_cbconstant;
	@Autowired
	private YntBoPubUtil yntBoPubUtil;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private IQmclService gl_qmclserv;
	@Autowired
	private IQmclNoicService gl_qmclnoicserv;
	@Autowired
	private IInventoryQcService gl_ic_invtoryqcserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accService;

	@Override
	public Map<String, IcDetailVO> queryDetail(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException {
		// 查询凭证数据
		List<IcDetailVO> detailVOs = queryFsDetails(paramvo, corpvo);
		// 查询期初数据
		Map<String, InventoryQcVO> qcMap = getQcMx(paramvo);

		// 计算发生
		List<Map<String, IcDetailVO>> fsMap = calFsByPeriod(paramvo, detailVOs);

		String pk_corp = corpvo.getPk_corp();
		Map<String, YntCpaccountVO> cpaMap = accService.queryMapByPk(pk_corp);
		// 获取结果
		Map<String, IcDetailVO> result = new HashMap<String, IcDetailVO>();

		result = createResult(qcMap, fsMap, paramvo);
		
		//汇总金额数量到上级
		addParents(result, cpaMap);
		
		calSpmc(corpvo, result, pk_corp, cpaMap);
		
		// 计算单价
		calPrice(result);

		return result;
	}
	
	private void addParents(Map<String, IcDetailVO> result,
			Map<String, YntCpaccountVO> cpaMap){
		if(result == null || result.size() == 0)
			return;
		
		Map<String, List<IcDetailVO>> map = hashlizeObject(result);
		
		if(map != null && map.size() > 0){
			String key;
			IcDetailVO vo;
			List<IcDetailVO> ll;
			for(Map.Entry<String, List<IcDetailVO>> entry : map.entrySet()){
				key = entry.getKey();
				ll = entry.getValue();
				vo = buildParentVO(ll, key);
				result.put(key, vo);
			}
		}
	}
	
	private IcDetailVO buildParentVO(List<IcDetailVO> list, String key){
		IcDetailVO vo = new IcDetailVO();
		
		String[] fileds = { "qcsl", "qcje", "srsl", "srje", "jcsl", "jcje" };
		DZFDouble value;
		for(IcDetailVO detailvo : list){
			for(String filed : fileds){
				value = SafeCompute.add((DZFDouble)vo.getAttributeValue(filed),
							(DZFDouble)detailvo.getAttributeValue(filed));
				vo.setAttributeValue(filed, value);
			}
		}
		vo.setPk_sp(key + "_fl");
		return vo;
	}
	
	private Map<String, List<IcDetailVO>> hashlizeObject(Map<String, IcDetailVO> result){
		Map<String, List<IcDetailVO>> map = new HashMap<String, List<IcDetailVO>>();
		String key;
		IcDetailVO vo;
		List<IcDetailVO> ll;
		
		for(Map.Entry<String, IcDetailVO> entry : result.entrySet()){
			key = entry.getKey();
			if(key.length() < 49)
				continue;
			
			key = key.substring(25, key.length());
			
			vo = entry.getValue();
			if(map.containsKey(key)){
				map.get(key).add(vo);
			}else{
				ll = new ArrayList<IcDetailVO>();
				ll.add(vo);
				map.put(key, ll);
			}
		}
		
		return map;
	}
	
	public List<TempInvtoryVO> queryZgVOs(CorpVO corpvo, String userid, DZFDate doped) throws DZFWarpException{
		//查询最小未成本结转期间
		String period = queryMaxCbjzPeriod(corpvo);

		//查询该期间期末处理数据
		String pk_corp = corpvo.getPk_corp();
		List<String> corppks = new ArrayList<String>();
		corppks.add(pk_corp);
		DZFDate date = DateUtils.getPeriodStartDate(period);
		List<QmclVO> list = gl_qmclserv.initquery(corppks, date, date,
				userid, doped, DZFBoolean.FALSE, DZFBoolean.FALSE);
		if(list == null || list.size() == 0){
			throw new BusinessException("未找到期末处理的数据，请检查");
		}
		
		//查询成本结转的数据
		List<QMJzsmNoICVO> vos = gl_qmclnoicserv.queryCBJZqcpzAccountVOS(pk_corp,userid,
				date.toString(), date.toString(), null, null);
		
		Map<String, YntCpaccountVO> kmsmap = accService.queryMapByPk(pk_corp);
		
		List<TempInvtoryVO> zlist = gl_qmclnoicserv.getZgDataByCBB(
				list.get(0), vos, corpvo, 3, null, DZFBoolean.FALSE, userid, kmsmap);
		
		tempInvtory(zlist, kmsmap, corpvo, period);
		
		return zlist;
	}
	
	private void tempInvtory(List<TempInvtoryVO> list, 
			Map<String, YntCpaccountVO> kmsmap, 
			CorpVO corpvo, String period){
		if(list == null || list.size() == 0){
			return;
		}
		
		String pk_corp = corpvo.getPk_corp();
		
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(
				AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);
		Map<String, List<TempInvtoryVO>> map = DZfcommonTools.hashlizeObject(list, new String[]{ "fzid" });//存货辅助
		
		String key;
		String kmclassify;
		YntCpaccountVO cpavo;
		List<TempInvtoryVO> temp;
		for(AuxiliaryAccountBVO bvo : bvos){
			if(map == null || map.size() == 0)
				break;
			
			key = bvo.getPk_auacount_b();
			if(map.containsKey(key)){
				temp = map.get(key);
				if(temp != null && temp.size() > 0){
					for(TempInvtoryVO vo : temp){
						vo.setId(UUID.randomUUID().toString());
						vo.setPk_invtory(bvo.getPk_auacount_b());
						vo.setSpbm(bvo.getCode());
						vo.setInvname(bvo.getName());
						vo.setSpgg(bvo.getSpec());
						vo.setJldw(bvo.getUnit());
						vo.setPk_gs(pk_corp);
						vo.setGsname(corpvo.getUnitname());
						vo.setPeriod(period);
						
						kmclassify = bvo.getKmclassify();
						if(!StringUtil.isEmpty(kmclassify)){
							cpavo = kmsmap.get(kmclassify);
							if(cpavo != null){
								vo.setSpfl(cpavo.getAccountname());
							}
						}
					}
				}
			}
		}
	}
	
	private String queryMaxCbjzPeriod(CorpVO corpvo){
		String pk_corp = corpvo.getPk_corp();
		DZFDate beginDate = corpvo.getBegindate();
		
		String period = DateUtils.getPeriod(beginDate);
		
		String qmclsql = "select max(period) from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and period >= ? and nvl(iscbjz,'N') ='Y'";
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		
		String maxperiod = (String) singleObjectBO.executeQuery(qmclsql, sp, new ColumnProcessor());
		
		if(StringUtil.isEmpty(maxperiod)){
			maxperiod = period;
		}else{
			//后续添加逻辑
			maxperiod = DateUtils.getPeriod(DateUtils.getPeriodEndDate(maxperiod).getDateAfter(1));
		}
		
		return maxperiod;
	}
	
	private QmclVO getQmcl(String pk_corp, String period){
		
		String sql = " select * from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and period = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		QmclVO qmclvo = (QmclVO) singleObjectBO.executeQuery(sql, sp, new BeanProcessor(QmclVO.class));
		
		return qmclvo;
	}
	
	private void calSpmc(CorpVO corpvo, 
			Map<String, IcDetailVO> result, 
			String pk_corp,
			Map<String, YntCpaccountVO> cpaMap){
		if(result == null || result.size() == 0)
			return;
		
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(
				AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);
		
		if(bvos == null || bvos.length == 0)
			return;
		
		String key;
		String kmclassify;
		IcDetailVO icvo;
		YntCpaccountVO cpavo;
		for(AuxiliaryAccountBVO bvo : bvos){
			key = bvo.getPk_auacount_b() + "_" + bvo.getKmclassify();
			
			if(result.containsKey(key)){
				icvo = result.get(key);
				icvo.setSpbm(bvo.getCode());
				icvo.setSpmc(bvo.getName());
				icvo.setSpgg(bvo.getSpec());//规格型号
				icvo.setJldw(bvo.getUnit());
				
				kmclassify = bvo.getKmclassify();
				if(!StringUtil.isEmpty(kmclassify)){
					cpavo = cpaMap.get(kmclassify);
					if(cpavo != null){
						icvo.setSpfl(kmclassify);
						icvo.setSpfl_name(cpavo.getAccountname());
					}
				}
				
			}
			
			key = bvo.getKmclassify();
			if(!StringUtil.isEmpty(key) 
					&& result.containsKey(key)){
				cpavo = cpaMap.get(key);
				icvo = result.get(key);
				
				icvo.setSpfl(key);
				icvo.setSpbm(cpavo.getAccountcode());
				icvo.setSpfl_name(cpavo.getAccountname());
				
			}
		}
		
	}

	private List<IcDetailVO> queryFsDetails(QueryParamVO paramvo, CorpVO corpvo) throws DZFWarpException {

		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		//取总账存货　启用日期
		DZFDate jzDate =  gl_ic_invtoryqcserv.queryInventoryQcDate(corpvo.getPk_corp());
		if(jzDate == null){
			jzDate = corpvo.getBegindate();//取建账日期
		}
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(DateUtils.getPeriod(jzDate));
		sp.addParam(DateUtils.getPeriod(paramvo.getEnddate()));
		
		sf.append(" Select h.doperatedate dbilldate, h.period, b.fzhsx6 pk_sp, fzb.kmclassify spfl, ");
		sf.append(" b.pk_accsubj, b.glchhsnum nnum, b.vicbillcodetype, b.xsjzcb ncost, b.glcgmny ");
		sf.append(" From ynt_tzpz_b b join ynt_tzpz_h h on h.pk_tzpz_h = b.pk_tzpz_h  ");
		sf.append(" left join ynt_fzhs_b fzb on fzb.pk_auacount_b = b.fzhsx6 ");
		sf.append(" Where b.pk_corp = ? and nvl(b.dr,0) = 0 and nvl(h.dr, 0) = 0 and b.vicbillcodetype in ('in', 'out') ");
		sf.append(" and h.period >= ? and h.period <= ? ");
		if(!StringUtil.isEmpty(paramvo.getPk_inventory())){
			sf.append(" and b.fzhsx6 = ? ");
			sp.addParam(paramvo.getPk_inventory());
		}
		if(!StringUtil.isEmpty(paramvo.getXmlbid())){
			sf.append(" and fzb.kmclassify = ? ");
			sp.addParam(paramvo.getXmlbid());
		}
		
		
		List<IcDetailVO> list = (List<IcDetailVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(IcDetailVO.class));
		
		convertData(list);//
		return list;
	}

	/**
	 * 查询期初数据
	 * 
	 * @param paramVo
	 * @return
	 */
	private Map<String, InventoryQcVO> getQcMx(QueryParamVO paramVo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramVo.getPk_corp());
		StringBuffer sf = new StringBuffer();
		sf.append(" select y.*, b.kmclassify chlb from ynt_glicqc y ");
		sf.append(" left join ynt_fzhs_b b on b.pk_auacount_b = y.pk_inventory ");
		sf.append(" Where y.pk_corp = ? and nvl(y.dr,0) =0 and nvl(b.dr,0) =0 and y.pk_inventory is not null ");
		
		if(!StringUtil.isEmpty(paramVo.getPk_inventory())){
			sf.append(" and y.pk_inventory= ? ");
			sp.addParam(paramVo.getPk_inventory());
		}
		if(!StringUtil.isEmpty(paramVo.getXmlbid())){
			sf.append(" and b.kmclassify = ? ");
			sp.addParam(paramVo.getXmlbid());
		}
		
		List<InventoryQcVO> fzrs = (List<InventoryQcVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(InventoryQcVO.class));
		
		Map<String, InventoryQcVO> qcMap = hashlizeObject(fzrs);

		return qcMap;
	}

	private void convertData(List<IcDetailVO> list) {
		if(list == null || list.size() == 0)
			return;
		
		for(IcDetailVO vo : list){
			if("in".equals(vo.getVicbillcodetype())){
				vo.setSrsl(vo.getNnum());
				vo.setSrje(vo.getGlcgmny());//取入库金额
			}else if("out".equals(vo.getVicbillcodetype())){
				vo.setFcsl(vo.getNnum());
				vo.setFcje(vo.getNcost());//取结转成本
			}
			
		}
	}

	private Map<String, InventoryQcVO> hashlizeObject(List<InventoryQcVO> fzrs) {
		Map<String, InventoryQcVO> result = new HashMap<String, InventoryQcVO>();

		if (fzrs == null || fzrs.size() == 0) {
			return result;
		}

		String key = null;
		InventoryQcVO tempvo = null;
		for (InventoryQcVO vo : fzrs) {
			key = vo.getPk_inventory() + "_" + vo.getChlb();

			if (!result.containsKey(key)) {
				result.put(key, vo);
			}else{
				tempvo = result.get(key);
				tempvo.setThismonthqc(SafeCompute.add(vo.getThismonthqc(), tempvo.getThismonthqc()));
				tempvo.setMonthqmnum(SafeCompute.add(vo.getMonthqmnum(), tempvo.getMonthqmnum()));
			}
		}

		return result;
	}

	/**
	 * 按区间计算合计
	 * 
	 * @return
	 */
	private List<Map<String, IcDetailVO>> calFsByPeriod(QueryParamVO paramVo, List<IcDetailVO> list) {
		List<Map<String, IcDetailVO>> rs = new ArrayList<Map<String, IcDetailVO>>();

		// 查询期间前
		Map<String, IcDetailVO> periodBefore = new HashMap<String, IcDetailVO>();
		// 查询期间
		Map<String, IcDetailVO> periodSum = new HashMap<String, IcDetailVO>();

		rs.add(periodBefore);
		rs.add(periodSum);

		DZFDate qjc = paramVo.getBegindate1();

		for (IcDetailVO vo : list) {
			if (vo.getDbilldate().before(qjc)) {
				sumDetail(periodBefore, vo);
			} else {
				sumDetail(periodSum, vo);
			}
		}

		return rs;
	}

	/**
	 * 计算合计
	 * 
	 * @param sumMap
	 * @param vo
	 */
	private void sumDetail(Map<String, IcDetailVO> sumMap, IcDetailVO vo) {

		String key = vo.getPk_sp() + "_" + vo.getSpfl(); 

		IcDetailVO icdvo = sumMap.get(key);
		if (icdvo == null) {
			icdvo = (IcDetailVO) vo.clone();
			sumMap.put(key, icdvo);
		} else {
			icdvo.setSrsl(SafeCompute.add(vo.getSrsl(), icdvo.getSrsl()));
			icdvo.setSrje(SafeCompute.add(vo.getSrje(), icdvo.getSrje()));

			icdvo.setFcsl(SafeCompute.add(vo.getFcsl(), icdvo.getFcsl()));
			icdvo.setFcje(SafeCompute.add(vo.getFcje(), icdvo.getFcje()));
		}

	}

	/**
	 * 封装结果数据
	 */
	private Map<String, IcDetailVO> createResult(Map<String, InventoryQcVO> qcMap, 
			List<Map<String, IcDetailVO>> fsMap,
			QueryParamVO paramvo) {
		Map<String, IcDetailVO> result = new HashMap<String, IcDetailVO>();
		Set<String> keySet = new HashSet<String>();

		Map<String, IcDetailVO> PeriodBeforeFs = fsMap.get(0);
		
		Map<String, IcDetailVO> PeriodFs = fsMap.get(1);
		keySet.addAll(qcMap.keySet());
		keySet.addAll(PeriodBeforeFs.keySet());
		keySet.addAll(PeriodFs.keySet());

		if (keySet.size() > 0) {
			InventoryQcVO qcvo;
			IcDetailVO periodBf;
			IcDetailVO period;
			IcDetailVO rs;
			for(String key : keySet){
				qcvo = qcMap.get(key);
				periodBf = PeriodBeforeFs.get(key);
				period = PeriodFs.get(key);
				
				rs = calculateAll(key, qcvo, periodBf, period);
				result.put(key, rs);
			}

		}

		return result;
	}
	
	private IcDetailVO calculateAll(String key, 
			InventoryQcVO qcvo, 
			IcDetailVO periodBf, 
			IcDetailVO period){
		DZFDouble qcsl = DZFDouble.ZERO_DBL;
		DZFDouble qcje = DZFDouble.ZERO_DBL;
		
		DZFDouble srsl = DZFDouble.ZERO_DBL;
		DZFDouble srje = DZFDouble.ZERO_DBL;
		
		DZFDouble fcsl = DZFDouble.ZERO_DBL;
		DZFDouble fcje = DZFDouble.ZERO_DBL;
		
		DZFDouble jcsl = DZFDouble.ZERO_DBL;
		DZFDouble jcje = DZFDouble.ZERO_DBL;
		
		
		if(qcvo != null){
			
			qcsl = qcvo.getMonthqmnum();
			qcje = qcvo.getThismonthqc();
		}
		
		if(periodBf != null){
			
			qcsl = SafeCompute.add(qcsl, SafeCompute.sub(periodBf.getSrsl(), periodBf.getFcsl()));
			qcje = SafeCompute.add(qcje, SafeCompute.sub(periodBf.getSrje(), periodBf.getFcje()));
		}
		
		if(period != null){
			
			srsl = period.getSrsl();
			srje = period.getSrje();
			
			fcsl = period.getFcsl();
			fcje = period.getFcje();
			
		}
		
		jcsl = SafeCompute.sub(SafeCompute.add(qcsl, srsl), fcsl);
		jcje = SafeCompute.sub(SafeCompute.add(qcje, srje), fcje);
		
		IcDetailVO vo = new IcDetailVO();
		vo.setPk_sp(key);
		vo.setQcsl(qcsl);
		vo.setQcje(qcje);
		vo.setSrsl(srsl);
		vo.setSrje(srje);
		vo.setFcsl(fcsl);
		vo.setFcje(fcje);
		vo.setJcsl(jcsl);
		vo.setJcje(jcje);
		
		return vo;
	}

	/**
	 * 根据期初，计算发生
	 * 
	 * @param 'key'
	 * @param 'qcVo'
	 * @param 'periodBf'
	 * @param 'period'
	 * @param 'account'
	 * @return
	 */

	private void calPrice(Map<String, IcDetailVO> result) {
		if (result == null) {
			return;
		}
		IcDetailVO vo = null;
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
		for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
			vo = entry.getValue();
			
			qcje = vo.getQcje();
			qcsl = vo.getQcsl();
			qcdj = SafeCompute.div(qcje, qcsl);
			vo.setQcdj(qcdj);
			
			srje = vo.getSrje();
			srsl = vo.getSrsl();
			srdj = SafeCompute.div(srje, srsl);
			vo.setSrdj(srdj);

			fcje = vo.getFcje();
			fcsl = vo.getFcsl();
			fcdj = SafeCompute.div(fcje, fcsl);
			vo.setFcdj(fcdj);

			jcje = vo.getJcje();
			jcsl = vo.getJcsl();
			jcdj = SafeCompute.div(jcje, jcsl);
			vo.setJcdj(jcdj);
		}
	}

	@Override
	public void saveZg(TempInvtoryVO[] bodyvos, 
			String pk_corp, 
			String userid) throws DZFWarpException {
		if(bodyvos == null || bodyvos.length == 0)
			throw new BusinessException("解析暂估数据不完整,请检查");
		String period = bodyvos[0].getPeriod();
		QmclVO qmclvo = getQmcl(pk_corp, period);
		if(qmclvo == null)
			throw new BusinessException("校验数据不合规，请检查");
		
		CorpVO corpvo =corpService.queryByPk(pk_corp);
		Map<String, YntCpaccountVO> kmsmap =accService.queryMapByPk(pk_corp);
		ZgVoucher zg = new ZgVoucher(gl_cbconstant, singleObjectBO, yntBoPubUtil,parameterserv);
		
		TzpzHVO billvo = zg.createPzvosNoIC(qmclvo, bodyvos, null,userid,kmsmap);
		TzpzHVO nextvo = zg.queryNextcodeNoIC(qmclvo, billvo);
		voucher.saveVoucher(corpvo, billvo);
		voucher.saveVoucher(corpvo, nextvo);
		
	}
	
//	private void setTempByTempInvtory(TempInvtoryVO[] bodyvos, String pk_corp){
//		YntCpaccountVO[] cpavos = AccountCache.getInstance().get(null, pk_corp);
//		YntCpaccountVO cpavo = null;
//		for(YntCpaccountVO vo : cpavos){
//			if(vo.getIsleaf().booleanValue() && vo.getAccountcode().startsWith("1405")){
//				if(!StringUtil.isEmpty(vo.getIsfzhs())
//						&& vo.getIsfzhs().charAt(5) == '1'){
//					cpavo = vo;
//					break;
//				}
//				
//			}
//		}
//		
//		for(TempInvtoryVO vo : bodyvos){
//			vo.setFzid(vo.getPk_invtory());
//			if(cpavo != null){
//				vo.setKmid(cpavo.getPk_corp_account());
//				vo.setKmbm(cpavo.getAccountcode());
//				vo.setKmname(cpavo.getAccountname());
//			}
//			
//		}
//	}
	
	@Override
	public TzpzHVO queryJzPz(String pk_corp, String period) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		//首先判断该月是否成本结转，如未结转，直接返回
		QmclVO qmclvo = getQmcl(pk_corp, period);
		if(qmclvo == null 
				|| qmclvo.getIscbjz() == null
				|| !qmclvo.getIscbjz().booleanValue())
			return null;
		
		sf.append(" Select * From ynt_tzpz_h h ");
		sf.append(" Where nvl(h.dr,0) = 0 and h.pk_corp = ? and h.period = ? and h.sourcebilltype = ? ");
		
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(IBillTypeCode.HP34);
		
		List<TzpzHVO> list = (List<TzpzHVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(TzpzHVO.class));
		
		if(list != null && list.size() > 1){
			Collections.sort(list, new Comparator<TzpzHVO>() {

				@Override
				public int compare(TzpzHVO o1, TzpzHVO o2) {
					return o2.getPzh().compareTo(o1.getPzh());
				}
			});
		}
		
		return list == null || list.size() == 0 
				? null : list.get(0);
	}
	
}

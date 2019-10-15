package com.dzf.zxkj.platform.service.qcset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.qcset.IFzhsqcService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.util.ReportUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;

@Service("gl_fzhsqcserv")
public class FzhsqcServiceImpl implements IFzhsqcService {
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IQcye gl_qcyeserv = null;
	
	@Autowired
	private IInventoryService iservice;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;

	@Autowired
	private IAccountService accountService;
	
	@Override
	public FzhsqcVO saveCombo(FzhsqcVO vo, UserVO user, CorpVO corp) throws DZFWarpException {
		String pk_corp = corp.getPk_corp();
		Map<String, YntCpaccountVO> kmMap = new HashMap<String, YntCpaccountVO>();
		YntCpaccountVO account = (YntCpaccountVO) singleObjectBO.queryVOByID(vo.getPk_accsubj(),
				YntCpaccountVO.class);
		kmMap.put(vo.getPk_accsubj(), account);
		vo.setVcode(account.getAccountcode());
		vo.setVname(account.getAccountname());
		vo.setVlevel(account.getAccountlevel());
		vo.setDirect(account.getDirection());
//		vo.setPk_currency(IGlobalConstants.RMB_currency_id);
		vo.setIsfzhs(account.getIsfzhs());
		DZFDate jzdate = corp.getBegindate();
		checkExist(vo);
		vo.setDoperatedate(jzdate);
		vo.setCoperatorid(user.getCuserid());
		vo.setDr(0);
		if(jzdate != null){
			vo.setVyear(Integer.valueOf(jzdate.toString().substring(0,4)));
			vo.setPeriod(jzdate.toString().substring(0, 7));
		}
		vo = (FzhsqcVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
		setDefaultVal(new FzhsqcVO[]{vo}, pk_corp, kmMap);
		return vo;
	}
	
	private void checkExist (FzhsqcVO vo) throws DZFWarpException {
		StringBuilder sql = new StringBuilder();
		String isfzhs = vo.getIsfzhs();
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPk_accsubj());
		sql.append("select count(1) from ynt_fzhsqc where pk_corp =? and nvl(dr,0)=0 and pk_accsubj = ? ");
		if (isfzhs.charAt(0) == '1') {
			sql.append(" and fzhsx1 = ? ");
			sp.addParam(vo.getFzhsx1());
		}
		if (isfzhs.charAt(1) == '1') {
			sql.append(" and fzhsx2 = ? ");
			sp.addParam(vo.getFzhsx2());
		}
		if (isfzhs.charAt(2) == '1') {
			sql.append(" and fzhsx3 = ? ");
			sp.addParam(vo.getFzhsx3());
		}
		if (isfzhs.charAt(3) == '1') {
			sql.append(" and fzhsx4 = ? ");
			sp.addParam(vo.getFzhsx4());
		}
		if (isfzhs.charAt(4) == '1') {
			sql.append(" and fzhsx5 = ? ");
			sp.addParam(vo.getFzhsx5());
		}
		if (isfzhs.charAt(5) == '1') {
			sql.append(" and fzhsx6 = ? ");
			sp.addParam(vo.getFzhsx6());
		}
		if (isfzhs.charAt(6) == '1') {
			sql.append(" and fzhsx7 = ? ");
			sp.addParam(vo.getFzhsx7());
		}
		if (isfzhs.charAt(7) == '1') {
			sql.append(" and fzhsx8 = ? ");
			sp.addParam(vo.getFzhsx8());
		}
		if (isfzhs.charAt(8) == '1') {
			sql.append(" and fzhsx9 = ? ");
			sp.addParam(vo.getFzhsx9());
		}
		if (isfzhs.charAt(9) == '1') {
			sql.append(" and fzhsx10 = ? ");
			sp.addParam(vo.getFzhsx10());
		}
		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor());
		if (count != null && count.intValue() > 0) {
			throw new BusinessException("新增核算组合失败！已经添加核算项目");
		}
		
	}
	public void delete (FzhsqcVO vo) {
		
	}
	@Override
	public FzhsqcVO[] queryByPk(String pk_corp,String pk_accsubj) throws DZFWarpException {
		String condition = " pk_accsubj = ? and pk_corp = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_accsubj);
		sp.addParam(pk_corp);
		FzhsqcVO[] results = (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class, condition, sp);
		return results;
	}
	
	@Override
	public FzhsqcVO[] queryFzQc (String pk_corp, String pk_accsubj)  throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_accsubj);
		StringBuffer sf = new StringBuffer();
		sf.append("  nvl(dr,0) = 0 and pk_corp = ? and pk_accsubj = ? order by vcode ");
		FzhsqcVO[] rs = (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class, sf.toString(), sp);
		if (rs != null && rs.length > 0) {
			YntCpaccountVO kmVo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, pk_accsubj);
			Map<String, YntCpaccountVO> kmMap = new HashMap<String, YntCpaccountVO>();
			kmMap.put(kmVo.getPk_corp_account(), kmVo);
			setDefaultVal(rs, pk_corp, kmMap);
			//按编码排序
			Arrays.sort(rs,new Comparator<FzhsqcVO>() {
				@Override
				public int compare(FzhsqcVO o1, FzhsqcVO o2) {
					return o1.getVcode().compareTo(o2.getVcode());
				}
			});
		}
		return rs;
		
	}
	
	private void setDefaultVal (FzhsqcVO[] vos, String pk_corp,
			Map<String, YntCpaccountVO> cpaccountMap)  throws DZFWarpException {
//		Map<String, AuxiliaryAccountBVO> invmap =	getInvAuaccount(pk_corp);
		Map<String, AuxiliaryAccountBVO> auaccountMap = gl_fzhsserv.queryMap(pk_corp);
		for (FzhsqcVO qc : vos) {
			YntCpaccountVO account = cpaccountMap.get(qc.getPk_accsubj());
			qc.setDirect(account.getDirection());
			qc.setVcode(account.getAccountcode());
			qc.setVname(account.getAccountname());
			String[] combStr = getCombStr(qc, auaccountMap, null);
			qc.setIsnum(account.getIsnum());
			if (account.getIsnum() != null && account.getIsnum().booleanValue()) {
				qc.setJldw(StringUtil.isEmpty(combStr[2]) ? account.getMeasurename() : combStr[2]);
			}
			qc.setSpec(combStr[3]);
			qc.setVcode(combStr[0]);
			qc.setVname(combStr[1]);
		}
	}
	
	private String[] getCombStr (FzhsqcVO vo, Map<String, AuxiliaryAccountBVO> auaccountMap,Map<String, AuxiliaryAccountBVO> invmap) {
		String[] str = new String[4];
		StringBuffer code = new StringBuffer(vo.getVcode());
		StringBuffer name = new StringBuffer(vo.getVname());
		AuxiliaryAccountBVO auaccount = null;
		if (!StringUtil.isEmpty(vo.getFzhsx1())) {
			auaccount = auaccountMap.get(vo.getFzhsx1());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx2())) {
			auaccount = auaccountMap.get(vo.getFzhsx2());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx3())) {
			auaccount = auaccountMap.get(vo.getFzhsx3());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx4())) {
			auaccount = auaccountMap.get(vo.getFzhsx4());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx5())) {
			auaccount = auaccountMap.get(vo.getFzhsx5());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx6())) {
			auaccount = auaccountMap.get(vo.getFzhsx6());
			if(auaccount == null && invmap != null){
				auaccount = invmap.get(vo.getFzhsx6());
			}
			if(auaccount != null){
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
				str[2] = auaccount.getUnit();
				str[3] = auaccount.getSpec();
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx7())) {
			auaccount = auaccountMap.get(vo.getFzhsx7());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx8())) {
			auaccount = auaccountMap.get(vo.getFzhsx8());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx9())) {
			auaccount = auaccountMap.get(vo.getFzhsx9());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		if (!StringUtil.isEmpty(vo.getFzhsx10())) {
			auaccount = auaccountMap.get(vo.getFzhsx10());
			if (auaccount != null) {
				code.append("_");
				code.append(auaccount.getCode());
				name.append("_");
				name.append(auaccount.getName());
			}
		}
		str[0] = code.toString();
		str[1] = name.toString();
		return str;
	}
	

	private Map<String, AuxiliaryAccountBVO> getInvAuaccount(String pk_corp) {

		Map<String, AuxiliaryAccountBVO> invmap = new HashMap<String, AuxiliaryAccountBVO>();
		List<InventoryVO> list = iservice.queryInfo(pk_corp,null);

		if (list == null || list.isEmpty()) {
			return invmap;
		}
		AuxiliaryAccountBVO bvo1 = null;
		for (InventoryVO invo : list) {
			if (!invmap.containsKey(invo.getPk_inventory())) {
				bvo1 = new AuxiliaryAccountBVO();
				bvo1.setCode(invo.getCode());
				bvo1.setName(invo.getName());
				bvo1.setPk_auacount_b(invo.getPk_inventory());
				bvo1.setUnit(invo.getMeasurename());
				bvo1.setPk_corp(invo.getPk_corp());
				invmap.put(invo.getPk_inventory(), bvo1);
			}
		}
		return invmap;

	}
	@Override
	public void saveFzQc(String pk_accsubj, FzhsqcVO[] fzvos, String currency, QcYeVO qcvo, UserVO user, CorpVO corp) throws DZFWarpException {
		if (fzvos != null && fzvos.length > 0) {
			checkVos(pk_accsubj, currency, fzvos, user, corp);
		}
		gl_qcyeserv.saveOne(user.getCuserid(), corp.getBegindate(),
				currency, corp.getPk_corp(), new QcYeVO[]{qcvo});
		if (fzvos != null && fzvos.length > 0) {
			for (FzhsqcVO fzqc : fzvos) {
				calFzQc(fzqc);
			}
		}
		String sql = "delete from ynt_fzhsqc where pk_corp = ? and pk_accsubj = ? and pk_currency = ?  and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(corp.getPk_corp());
		sp.addParam(pk_accsubj);
		sp.addParam(currency);
		singleObjectBO.executeUpdate(sql, sp);
		singleObjectBO.insertVOArr(corp.getPk_corp(), fzvos);
	}
	
	private void checkVos (String pk_accsubj, String currency,
			FzhsqcVO[] fzvos, UserVO user, CorpVO corp) {
		Set<String> fzhsCombo = new HashSet<String>();
		String pk_corp = corp.getPk_corp();
		Map<String, YntCpaccountVO> kmMap = accountService.queryMapByPk(pk_corp);
		YntCpaccountVO account = kmMap.get(pk_accsubj);
		for (FzhsqcVO fzhsqcVO : fzvos) {
//			fzhsqcVO.setVcode(account.getAccountcode());
//			fzhsqcVO.setVname(account.getAccountname());
			fzhsqcVO.setVlevel(account.getAccountlevel());
			fzhsqcVO.setDirect(account.getDirection());
			fzhsqcVO.setPk_currency(currency);
			fzhsqcVO.setIsfzhs(account.getIsfzhs());
			DZFDate jzdate = corp.getBegindate();
			fzhsqcVO.setDoperatedate(jzdate);
			fzhsqcVO.setCoperatorid(user.getCuserid());
			fzhsqcVO.setDr(0);
			fzhsqcVO.setPk_accsubj(pk_accsubj);
			fzhsqcVO.setPk_corp(pk_corp);
			String combo = ReportUtil.getFzKey(fzhsqcVO);
			if (fzhsCombo.contains(combo)) {
				throw new BusinessException("辅助核算组合重复！");
			}
			fzhsCombo.add(combo);
		}
		
	}
	
	private void calFzQc (FzhsqcVO fzqc) {
		if (fzqc == null)
			return;
		DZFDouble qm = null;
		DZFDouble qcnum = null ;
		DZFDouble ybqm = null;
		if (fzqc.getDirect() == 0) {// 借方
			qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYearjffse()),fzqc.getYeardffse());
			ybqm =  SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyearjffse()),fzqc.getYbyeardffse());
			qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBnfsnum()),fzqc.getBndffsnum());
		} else {// 贷方
			qm = SafeCompute.sub(SafeCompute.add(fzqc.getYearqc(), fzqc.getYeardffse()),fzqc.getYearjffse());
			ybqm =  SafeCompute.sub(SafeCompute.add(fzqc.getYbyearqc(), fzqc.getYbyeardffse()),fzqc.getYbyearjffse());
			qcnum = SafeCompute.sub(SafeCompute.add(fzqc.getBnqcnum(), fzqc.getBndffsnum()),fzqc.getBnfsnum());
		}
		fzqc.setThismonthqc(qm);
		fzqc.setYbthismonthqc(ybqm);
		fzqc.setMonthqmnum(qcnum);
	}
	
	@Override
	public FzhsqcVO[] queryAll(String pk_corp, String pk_currency)
			throws DZFWarpException {
		// 综合本位币
		boolean isBwb = DZFConstant.ZHBWB.equals(pk_currency);
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0 and pk_corp = ? ");
		sf.append(" and pk_accsubj in (select pk_corp_account from ynt_cpaccount where  pk_corp = ? and nvl(dr,0) = 0) ");
		sp.addParam(pk_corp);
		if (!isBwb) {
			sf.append(" and pk_currency = ? ");
			sp.addParam(pk_currency);
		}
		FzhsqcVO[] rs = (FzhsqcVO[]) singleObjectBO.queryByCondition(FzhsqcVO.class, sf.toString(), sp);
		if (rs != null && rs.length > 0) {
			if (isBwb) {
				// 处理综合本位币
				rs = dealZHBWB(rs);
			}
			setDefaultVal(rs, pk_corp, accountService.queryMapByPk(pk_corp));
		}
		//按编码排序
		Arrays.sort(rs,new Comparator<FzhsqcVO>() {
			@Override
			public int compare(FzhsqcVO o1, FzhsqcVO o2) {
				return o1.getVcode().compareTo(o2.getVcode());
			}
		});
		return rs;
	}
	
	private FzhsqcVO[] dealZHBWB(FzhsqcVO[] vos) {
		Map<String, FzhsqcVO> map = new LinkedHashMap<String, FzhsqcVO>();
		for (FzhsqcVO qc : vos) {
			String key = qc.getPk_accsubj() + ReportUtil.getFzKey(qc);
			if (map.containsKey(key)) {
				FzhsqcVO existQc = map.get(key);
				existQc.setYearqc(SafeCompute.add(existQc.getYearqc(), qc.getYearqc()));
				existQc.setYearjffse(SafeCompute.add(existQc.getYearjffse(), qc.getYearjffse()));
				existQc.setYeardffse(SafeCompute.add(existQc.getYeardffse(), qc.getYeardffse()));
				existQc.setThismonthqc(SafeCompute.add(existQc.getThismonthqc(), qc.getThismonthqc()));
			} else {
				qc.setYbyearqc(null);
				qc.setYbyearjffse(null);
				qc.setYbyeardffse(null);
				qc.setYbthismonthqc(null);
				map.put(key, qc);
			}
		}
		return map.values().toArray(new FzhsqcVO[0]);
	
	}

}
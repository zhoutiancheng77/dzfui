package com.dzf.zxkj.platform.services.qcset.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.tree.BDTreeCreator;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventorySetVO;
import com.dzf.zxkj.platform.model.qcset.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.icset.IInventoryAccSetService;
import com.dzf.zxkj.platform.services.qcset.IFzhsqcService;
import com.dzf.zxkj.platform.services.qcset.IQcService;
import com.dzf.zxkj.platform.services.qcset.IQcye;
import com.dzf.zxkj.platform.services.sys.*;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.ReportUtil;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service("gl_qcyeserv")
@SuppressWarnings("all")
public class QcyeImpl implements IQcye {

	private String zhbwb = DZFConstant.ZHBWB;// 综合本位币
	@Autowired
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private GDZcSyncImpl gdzcsync = null;
	@Autowired
	private ICpaccountService cpaccountService = null;
	@Autowired
	private IFzhsqcService gl_fzhsqcserv;
	@Autowired
	private IQcService iservice;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv;
	@Autowired
	private IUserService userServiceImpl;
	@Autowired
	private IBDCurrencyService sys_currentserv;

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IAccountService accountService;

	private QcYeCommon qcyecommon = null;

	public QcYeCommon getQcyecommon() {
		if (qcyecommon == null) {
			qcyecommon = new QcYeCommon();
		}
		return qcyecommon;
	}

	public void setQcyecommon(QcYeCommon qcyecommon) {
		this.qcyecommon = qcyecommon;
	}

	//此方法增加了重载 多一个参数查询是否停用，修改业务注意同步
	public List<QcYeVO> queryAllQcInfo(String pk_corp, String pk_currence,
									   String rmb) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select ynt_qcye.pk_qcye,acc.pk_corp,ynt_qcye.coperatorid,ynt_qcye.doperatedate,acc.isfzhs,acc.isverification,");
		sf.append(" acc.pk_corp_account pk_accsubj,ynt_qcye.pk_currency,acc.accountcode vcode,acc.accountname vname,acc.accountkind, ");
		sf.append("  acc.direction direct,acc.accountlevel vlevel,ynt_qcye.thismonthqc,ynt_qcye.yearjffse,  ");
		sf.append("  ynt_qcye.yeardffse,ynt_qcye.yearqc,ynt_qcye.memo,ynt_qcye.ybyearjffse,  ");
		sf.append("  ynt_qcye.ybyeardffse,ynt_qcye.ybyearqc,ynt_qcye.ybthismonthqc ,acc.isleaf,acc.exc_pk_currency , ");
		sf.append("  ynt_qcye.bnqcnum bnqcnum,ynt_qcye.bnfsnum bnfsnum,ynt_qcye.bndffsnum bndffsnum,ynt_qcye.monthqmnum monthqmnum ,acc.measurename jldw ,acc.isnum isnum  ");
		sf.append("  from    ynt_cpaccount acc ");
		sf.append("  left join ynt_qcye  on acc.pk_corp_account = ynt_qcye.pk_accsubj ");
		sf.append("  and nvl(ynt_qcye.dr,0) = 0 and ynt_qcye.pk_corp = ?  ");
		if (!zhbwb.equals(pk_currence)) {
			if (rmb.equals(pk_currence)) {// 人民币
				sf.append(" and nvl(ynt_qcye.pk_currency,'" + pk_currence
						+ "') = '" + pk_currence + "'  ");
			} else {
				sf.append(" and ynt_qcye.pk_currency = '" + pk_currence + "'  ");
			}
		}
		sf.append("  where nvl(acc.dr,0) = 0 and acc.pk_corp = ? order by acc.accountcode ");
		List<QcYeVO> list = (List<QcYeVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(QcYeVO.class));
		return list;
	}
	
	//此方法增加了重载 少一个参数，修改业务注意同步
	public List<QcYeVO> queryAllQcInfo(String pk_corp, String pk_currence,
			String rmb, String isShowFC) throws DZFWarpException {
		String FCParam = "";
		if(StringUtil.nequals(isShowFC, "true")){
			FCParam = " and bisseal!='Y'";
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select ynt_qcye.pk_qcye,acc.pk_corp,ynt_qcye.coperatorid,ynt_qcye.doperatedate,acc.isfzhs,acc.isverification,");
		sf.append(" acc.pk_corp_account pk_accsubj,ynt_qcye.pk_currency,acc.accountcode vcode,acc.accountname vname,acc.accountkind, ");
		sf.append("  acc.direction direct,acc.accountlevel vlevel,ynt_qcye.thismonthqc,ynt_qcye.yearjffse,  ");
		sf.append("  ynt_qcye.yeardffse,ynt_qcye.yearqc,ynt_qcye.memo,ynt_qcye.ybyearjffse,  ");
		sf.append("  ynt_qcye.ybyeardffse,ynt_qcye.ybyearqc,ynt_qcye.ybthismonthqc ,acc.isleaf,acc.exc_pk_currency , ");
		sf.append("  ynt_qcye.bnqcnum bnqcnum,ynt_qcye.bnfsnum bnfsnum,ynt_qcye.bndffsnum bndffsnum,ynt_qcye.monthqmnum monthqmnum ,acc.measurename jldw ,acc.isnum isnum  ");
		sf.append("  from    ynt_cpaccount acc ");
		sf.append("  left join ynt_qcye  on acc.pk_corp_account = ynt_qcye.pk_accsubj ");
		sf.append("  and nvl(ynt_qcye.dr,0) = 0 and ynt_qcye.pk_corp = ?  ");
		if (!zhbwb.equals(pk_currence)) {
			if (rmb.equals(pk_currence)) {// 人民币
				sf.append(" and nvl(ynt_qcye.pk_currency,'" + pk_currence
						+ "') = '" + pk_currence + "'  ");
			} else {
				sf.append(" and ynt_qcye.pk_currency = '" + pk_currence + "'  ");
			}
		}
		sf.append("  where nvl(acc.dr,0) = 0 and acc.pk_corp = ? "+FCParam+" order by acc.accountcode ");
		List<QcYeVO> list = (List<QcYeVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(QcYeVO.class));
		list = removeRepeat(list);
		return list;
	}
	
	//增加此方法，是因为期初表估计有大量重复的数据。加载时会报错，而失败，此种数据出现，修改保存后，即消失。
	private List<QcYeVO> removeRepeat(List<QcYeVO> list){
		if(list == null || list.size() == 0){
			return new ArrayList<QcYeVO>();
		}
		List<QcYeVO> zlist = new ArrayList<QcYeVO>();
		Set<String> set = new HashSet<String>();
		for(QcYeVO vo : list){
			String key = vo.getVcode() + vo.getPk_currency();
			if(!set.contains(key)){
				set.add(key);
				zlist.add(vo);
			}
		}
		set.clear();
		return zlist;
	}
	
	private List<FzhsqcVO> queryAll(String pk_corp, String pk_currence,
									String rmb) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select ynt_qcye.pk_qcye,acc.pk_corp,ynt_qcye.coperatorid,ynt_qcye.doperatedate,acc.isfzhs,  ");
		sf.append(" acc.pk_corp_account pk_accsubj,ynt_qcye.pk_currency,acc.accountcode vcode,acc.accountname vname,acc.accountkind, ");
		sf.append("  acc.direction direct,acc.accountlevel vlevel,ynt_qcye.thismonthqc,ynt_qcye.yearjffse,  ");
		sf.append("  ynt_qcye.yeardffse,ynt_qcye.yearqc,ynt_qcye.memo,ynt_qcye.ybyearjffse,  ");
		sf.append("  ynt_qcye.ybyeardffse,ynt_qcye.ybyearqc,ynt_qcye.ybthismonthqc ,acc.isleaf,acc.exc_pk_currency , ");
		sf.append("  ynt_qcye.bnqcnum bnqcnum,ynt_qcye.bnfsnum bnfsnum,ynt_qcye.bndffsnum bndffsnum,ynt_qcye.monthqmnum monthqmnum ,acc.measurename jldw ,acc.isnum isnum  ");
		sf.append("  from    ynt_cpaccount acc ");
		sf.append("  left join ynt_qcye  on acc.pk_corp_account = ynt_qcye.pk_accsubj ");
		sf.append("  and nvl(ynt_qcye.dr,0) = 0 and ynt_qcye.pk_corp = ?  ");
		if (!zhbwb.equals(pk_currence)) {
			if (rmb.equals(pk_currence)) {// 人民币
				sf.append(" and nvl(ynt_qcye.pk_currency,'" + pk_currence
						+ "') = '" + pk_currence + "'  ");
			} else {
				sf.append(" and ynt_qcye.pk_currency = '" + pk_currence + "'  ");
			}
		}
		sf.append("  where nvl(acc.dr,0) = 0 and acc.pk_corp = ? order by acc.accountcode ");
		List<FzhsqcVO> list = (List<FzhsqcVO>) singleObjectBO.executeQuery(
				sf.toString(), sp, new BeanListProcessor(FzhsqcVO.class));
		return list;
	}

	@Override
	public Map<String, QcYeVO[]> query(String pk_corp, String pk_currence, String isShowFC)
			throws DZFWarpException {
		String rmb = IGlobalConstants.RMB_currency_id;// 人民币
		List<QcYeVO> list = queryAllQcInfo(pk_corp, pk_currence, rmb, isShowFC);
		
		//  如果是老流程    启用数量核算默认存货辅助
//		setDefaultInvFz(list, pk_corp);
		
		createID(pk_corp, list);
		// 处理综合本位币
		list = doZHBWB(list, pk_currence);
		// 科目编码规则
		String coderule = getCodeRule(pk_corp);
		// 处理币种
		List<QcYeVO> newlist = queryQcYe(list, pk_currence, rmb, pk_corp, coderule);
		operchild(newlist);
		// 分组
		Map<String, QcYeVO[]> map = new HashMap<String, QcYeVO[]>();
		Map<String, List<QcYeVO>> maps = DZfcommonTools.hashlizeObject(newlist,
				new String[] { "accountkind" });
		Iterator<String> it = maps.keySet().iterator();
		while (it.hasNext()) {
			String key = it.next();
			List<QcYeVO> list1 = maps.get(key);
			QcYeVO[] qcvos = doIteratorVO(list1, pk_corp, coderule);
			map.put(key, qcvos);
		}
		return map;
	}

	// 处理币种
	private List<QcYeVO> queryQcYe(List<QcYeVO> list, String pk_currence,
			String rmb, String pk_corp, String coderule) {
		if (list == null || list.size() == 0)
			return null;
		if (pk_currence != null && rmb != null && pk_currence.equals(rmb))// 人民币直接返回
			return list;
		if (zhbwb.equals(pk_currence))
			return list;
		QcYeVO[] vos = doIteratorVO(list, pk_corp, coderule);
		Map<String, QcYeVO> map = new HashMap<String, QcYeVO>();
		getIteratorMap(vos, map);
		List<QcYeVO> listz = queryYe(vos, pk_currence, map, null);
		return listz;
	}

	public void operchild(List<QcYeVO> listz) {
		if (listz == null || listz.size()  == 0)
			return;
		for (QcYeVO c : listz) {
			c.setChildren(null);
		}
	}

	public void getIteratorMap(QcYeVO[] vos, Map<String, QcYeVO> map) {
		if (vos != null && vos.length > 0) {
			for (QcYeVO c : vos) {
				map.put(c.getVcode(), c);
				QcYeVO[] cvos = c.getChildren();
				if (cvos != null && cvos.length > 0) {
					getIteratorMap(cvos, map);
				}
			}
		}
	}

	public List<QcYeVO> queryYe(QcYeVO[] vos, String pk_currence,
			Map<String, QcYeVO> map, Map<String, QcYeVO> map1) {
		if (map1 == null) {
			map1 = new TreeMap<String, QcYeVO>();
		}
		if (vos != null && vos.length > 0) {
			QcYeVO[] cvos = null;
			for (int i = 0; i < vos.length; i++) {
				cvos = vos[i].getChildren();
				if (cvos == null || cvos.length == 0) {
					if (vos[i].getIsleaf() != null
							&& vos[i].getIsleaf().booleanValue()) {// 末级
						if (vos[i].getExc_pk_currency() != null
								&& vos[i].getExc_pk_currency().contains(
										pk_currence)) {
							getParentInfo(vos[i].getVcode(), map, map1,
									vos[i].getPk_corp());
							map1.put(vos[i].getVcode(), vos[i]);
						}
					}
				} else {
					queryYe(cvos, pk_currence, map, map1);
				}
			}
		}
		return new ArrayList<QcYeVO>(map1.values());
	}

	public void getParentInfo(String childcode, Map<String, QcYeVO> map,
			Map<String, QcYeVO> map1, String pk_corp) {
		// if(coderule==null || coderule.length()==0){
		String coderule = getCodeRule(pk_corp);
		// }
		String parcode = DZfcommonTools.getParentCode(childcode, coderule);
		if (parcode != null && !"".equals(parcode)) {
			QcYeVO v = map.get(parcode);
			if (!map1.containsKey(v.getVcode())) {
				map1.put(v.getVcode(), v);
			}
			getParentInfo(parcode, map, map1, pk_corp);
		}
	}

	private String getCodeRule(String pk_corp) {
		String accountrule = cpaccountService.queryAccountRule(pk_corp);
		return accountrule;
	}

	/**
	 * 处理综合本位币
	 */
	public List<QcYeVO> doZHBWB(List<QcYeVO> list, String pk_currence) {
		if (list == null || list.size() == 0 || !zhbwb.equals(pk_currence))
			return list;
		Map<String, QcYeVO> map = new HashMap<String, QcYeVO>();
		for (QcYeVO c : list) {
			// 清空原币的值
			clearYbValue(c);
			String key = c.getPk_accsubj();
			if (map.containsKey(key)) {
				QcYeVO z = map.get(key);
				QcYeVO v1 = mergeValue(z, c);// 合并
				map.put(key, v1);
			} else {
				map.put(key, c);
			}
		}
		// 排序
		List<QcYeVO> z = new ArrayList<QcYeVO>(map.values());
		Collections.sort(z);
		return z;
	}

	/**
	 * 清空原币的值
	 */
	public void clearYbValue(QcYeVO c) {
		if (c == null)
			return;
		c.setYbyearqc(null);
		c.setYbyearjffse(null);
		c.setYbyeardffse(null);
		c.setYbthismonthqc(null);
	}

	/**
	 * 本币值相加
	 */
	public QcYeVO mergeValue(QcYeVO c1, QcYeVO c2) {
		if (c1 == null)
			return c2;
		if (c2 == null)
			return c1;
		c1.setYearqc(SafeCompute.add(c1.getYearqc(), c2.getYearqc()));
		c1.setYearjffse(SafeCompute.add(c1.getYearjffse(), c2.getYearjffse()));
		c1.setYeardffse(SafeCompute.add(c1.getYeardffse(), c2.getYeardffse()));
		c1.setThismonthqc(SafeCompute.add(c1.getThismonthqc(),
				c2.getThismonthqc()));
		return c1;
	}

	public void createID(String pk_corp, List<QcYeVO> list) {
		if (list == null || list.size() == 0)
			return;
		List<QcYeVO> zs = new ArrayList<QcYeVO>();
		for (QcYeVO c : list) {
			if (c.getPk_qcye() == null || "".equals(c.getPk_qcye())) {
				zs.add(c);
			}
		}
		if (zs.size() > 0) {
			String[] npks = IDGenerate.getInstance().getNextIDS(pk_corp,
					zs.size());
			for (int i = 0; i < zs.size(); i++) {
				zs.get(i).setPk_qcye(npks[i]);
			}
		}
	}

	private QcYeVO[] doIteratorVO(List<QcYeVO> list, String pk_corp, String coderule) {
		// if(coderule==null || coderule.length()==0){
		if (coderule == null) {
			coderule = getCodeRule(pk_corp);
		}
		// }
		QcYeVO vo = (QcYeVO) BDTreeCreator.createTree(
				list.toArray(new QcYeVO[0]), new KmTreeStrategy(coderule));
		QcYeVO[] bodyvos = (QcYeVO[]) DZfcommonTools.convertToSuperVO(vo
				.getChildren());
		return bodyvos;
	}

	/**
	 * 重新累计
	 */
	public RowValue calc(QcYeVO[] qcvos, RowValue parent) {
		if (qcvos == null || qcvos.length == 0)
			return new RowValue();
		for (QcYeVO cu : qcvos) {
			RowValue rv = new RowValue();
			if (cu.getChildren() != null && cu.getChildren().length > 0) {
				rv.setDirect(cu.getDirect());
				rv = calc(cu.getChildren(), rv);
				// 赋值本身
				setValueQc(cu, rv);
			} else {
				// 本身进行运算根据期初计算
				doOneRowCalc(cu);
				setValue(cu, rv);
			}
			parent = valueAddCalc(parent, rv);
		}
		return parent;
	}

	public void doOneRowCalc(QcYeVO qc) {
		if (qc == null)
			return;
		DZFDouble qm = null;
		DZFDouble qcnum = null;
		DZFDouble ybqm = null;
		if (qc.getDirect() == 0) {// 借方
			qm = SafeCompute.sub(
					SafeCompute.add(qc.getYearqc(), qc.getYearjffse()),
					qc.getYeardffse());
			ybqm = SafeCompute.sub(
					SafeCompute.add(qc.getYbyearqc(), qc.getYbyearjffse()),
					qc.getYbyeardffse());
			qcnum = SafeCompute.sub(
					SafeCompute.add(qc.getBnqcnum(), qc.getBnfsnum()),
					qc.getBndffsnum());
		} else {// 贷方
			qm = SafeCompute.sub(
					SafeCompute.add(qc.getYearqc(), qc.getYeardffse()),
					qc.getYearjffse());
			ybqm = SafeCompute.sub(
					SafeCompute.add(qc.getYbyearqc(), qc.getYbyeardffse()),
					qc.getYbyearjffse());
			qcnum = SafeCompute.sub(
					SafeCompute.add(qc.getBnqcnum(), qc.getBndffsnum()),
					qc.getBnfsnum());
		}
		qc.setThismonthqc(qm);
		qc.setYbthismonthqc(ybqm);
		qc.setMonthqmnum(qcnum);
	}

	public void setValue(QcYeVO cu, RowValue rv) {
		if (cu != null && rv != null) {
			rv.setQc(cu.getYearqc());
			rv.setJf(cu.getYearjffse());
			rv.setDf(cu.getYeardffse());
			rv.setQm(cu.getThismonthqc());
			rv.setYbqc(cu.getYbyearqc());
			rv.setYbjf(cu.getYbyearjffse());
			rv.setYbdf(cu.getYbyeardffse());
			rv.setYbqm(cu.getYbthismonthqc());
			rv.setDirect(cu.getDirect());
			rv.setBnqcnum(cu.getBnqcnum());
			rv.setBnfsnum(cu.getBnfsnum());
			rv.setBndffsnum(cu.getBndffsnum());
			rv.setMonthqmnum(cu.getMonthqmnum());
		}
	}

	public void setValueQc(QcYeVO cu, RowValue rv) {
		if (cu != null && rv != null) {
			cu.setYearqc(rv.getQc());
			cu.setYearjffse(rv.getJf());
			cu.setYeardffse(rv.getDf());
			cu.setThismonthqc(rv.getQm());
			cu.setYbyearqc(rv.getYbqc());
			cu.setYbyearjffse(rv.getYbjf());
			cu.setYbyeardffse(rv.getYbdf());
			cu.setYbthismonthqc(rv.getYbqm());
			cu.setBnqcnum(rv.getBnqcnum());
			cu.setBnfsnum(rv.getBnfsnum());
			cu.setBndffsnum(rv.getBndffsnum());
			cu.setMonthqmnum(rv.getMonthqmnum());
		}
	}

	public RowValue valueAddCalc(RowValue parent, RowValue rv) {
		if (parent == null) {
			parent = new RowValue();
		}
		if (rv != null) {
			if (parent != null
					&& parent.getDirect() != null
					&& parent.getDirect().intValue() != rv.getDirect()
							.intValue()) {
				parent.setQc(SafeCompute.sub(parent.getQc(), rv.getQc()));
			} else {
				parent.setQc(SafeCompute.add(parent.getQc(), rv.getQc()));
			}
			if (parent != null
					&& parent.getDirect() != null
					&& parent.getDirect().intValue() != rv.getDirect()
							.intValue()) {
				parent.setBnqcnum(SafeCompute.sub(parent.getBnqcnum(),
						rv.getBnqcnum()));
			} else {
				parent.setBnqcnum(SafeCompute.add(parent.getBnqcnum(),
						rv.getBnqcnum()));
			}
			parent.setJf(SafeCompute.add(parent.getJf(), rv.getJf()));
			parent.setDf(SafeCompute.add(parent.getDf(), rv.getDf()));
			parent.setBnfsnum(SafeCompute.add(parent.getBnfsnum(),
					rv.getBnfsnum()));
			parent.setBndffsnum(SafeCompute.add(parent.getBndffsnum(),
					rv.getBndffsnum()));
			if (parent != null
					&& parent.getDirect() != null
					&& parent.getDirect().intValue() != rv.getDirect()
							.intValue()) {
				parent.setQm(SafeCompute.sub(parent.getQm(), rv.getQm()));
			} else {
				parent.setQm(SafeCompute.add(parent.getQm(), rv.getQm()));
			}
			if (parent != null
					&& parent.getDirect() != null
					&& parent.getDirect().intValue() != rv.getDirect()
							.intValue()) {
				parent.setYbqc(SafeCompute.sub(parent.getYbqc(), rv.getYbqc()));
			} else {
				parent.setYbqc(SafeCompute.add(parent.getYbqc(), rv.getYbqc()));
			}
			parent.setYbjf(SafeCompute.add(parent.getYbjf(), rv.getYbjf()));
			parent.setYbdf(SafeCompute.add(parent.getYbdf(), rv.getYbdf()));
			if (parent != null
					&& parent.getDirect() != null
					&& parent.getDirect().intValue() != rv.getDirect()
							.intValue()) {
				parent.setYbqm(SafeCompute.sub(parent.getYbqm(), rv.getYbqm()));
			} else {
				parent.setYbqm(SafeCompute.add(parent.getYbqm(), rv.getYbqm()));
			}
			if (parent != null
					&& parent.getDirect() != null
					&& parent.getDirect().intValue() != rv.getDirect()
							.intValue()) {
				parent.setMonthqmnum(SafeCompute.sub(parent.getMonthqmnum(),
						rv.getMonthqmnum()));
			} else {
				parent.setMonthqmnum(SafeCompute.add(parent.getMonthqmnum(),
						rv.getMonthqmnum()));
			}
		}
		return parent;
	}

	@Override
	public void save(String userid, DZFDate jzdate, String pk_currency,
					 String pk_corp, QcYeVO[] qcvos) throws DZFWarpException {
		// 已经结账，不让保存
		String njsql = "select max(period) from ynt_qmjz where pk_corp =? and nvl(dr,0)=0 and nvl(jzfinish,'N') = 'Y'";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String maxvalue = (String) singleObjectBO.executeQuery(njsql, sp,
				new ColumnProcessor());
		if (!StringUtil.isEmpty(maxvalue)) {
			throw new BusinessException("当前最近结账日期为:" + maxvalue + "不能保存!");
		}
		
		//zpm 新增加辅助核算计算 保存，计算期末
		saveFZhs(pk_corp,qcvos);

		// 重新累计
		calc(qcvos, null);
		// 保存
		List<QcYeVO> list = new ArrayList<QcYeVO>();
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class,
				pk_corp);
		if (!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			beforesave(qcvos, list);
		} else {
			beforesave2(qcvos, list);
		}
		operateAction(userid, jzdate, pk_currency, list);
		sp.clearParams();
		sp.addParam(pk_corp);
		sp.addParam(pk_currency);
		// 先删除
		singleObjectBO
				.executeUpdate(
						" delete from ynt_qcye where pk_corp = ? and nvl(dr,0) =  0 and nvl(pk_currency,'00000100AA10000000000BKT') = ?",
						sp);
		// 后插入
		singleObjectBO.insertVOArr(pk_corp, list.toArray(new QcYeVO[0]));
		
	}
	
	private void calcQcyeMap(Map<String,QcYeVO> map,QcYeVO[] qcvos){
		if (qcvos == null || qcvos.length == 0)
			return;
		for(QcYeVO v : qcvos){
			if(v!=null){
				map.put(v.getPk_accsubj(), v);
				QcYeVO[] childs = v.getChildren();
				if(childs!=null && childs.length>0){
					calcQcyeMap(map,childs);
				}
			}
		}
	}
	
	public void saveFZhs(String pk_corp,QcYeVO[] qcvos){
		if (qcvos == null || qcvos.length == 0)
			return;
		FzhsqcVO[] fzhsvos = queryFzhsQcVOs(pk_corp);
		if(fzhsvos == null || fzhsvos.length == 0)
			return;
		Map<String,QcYeVO> qcyemap = new HashMap<String,QcYeVO>();
		calcQcyeMap(qcyemap,qcvos);
		for (FzhsqcVO fzqc : fzhsvos) {
			calFzQc(fzqc);
		}
		singleObjectBO.updateAry(fzhsvos, new String[]{"thismonthqc","ybthismonthqc","monthqmnum"});
		//回写上层科目的合计值
		List<FzhsqcVO> list = new ArrayList<FzhsqcVO>(Arrays.asList(fzhsvos));
		Map<String, List<FzhsqcVO>> map = DZfcommonTools.hashlizeObject(list, new String[]{"pk_accsubj"});
		for(String key : map.keySet()){
			QcYeVO qcyevo = qcyemap.get(key);
			List<FzhsqcVO> zlist = map.get(key);
			//计算合计值
			calcMergeValue(qcyevo,zlist);
		}
	}
	
	private void calcMergeValue(QcYeVO cu,List<FzhsqcVO> zlist){
		if(cu == null || zlist == null || zlist.size() == 0)
			return;
		cu.setYearqc(null);
		cu.setYearjffse(null);
		cu.setYeardffse(null);
		cu.setThismonthqc(null);
		//
		cu.setYbyearqc(null);
		cu.setYbyearjffse(null);
		cu.setYbyeardffse(null);
		cu.setYbthismonthqc(null);
		//
		cu.setBnqcnum(null);
		cu.setBnfsnum(null);
		cu.setBndffsnum(null);
		cu.setMonthqmnum(null);
		for(FzhsqcVO vo : zlist){
			cu.setYearqc(SafeCompute.add(cu.getYearqc(), vo.getYearqc()));
			cu.setYearjffse(SafeCompute.add(cu.getYearjffse(), vo.getYearjffse()));
			cu.setYeardffse(SafeCompute.add(cu.getYeardffse(), vo.getYeardffse()));
			cu.setThismonthqc(SafeCompute.add(cu.getThismonthqc(), vo.getThismonthqc()));
			//
			cu.setYbyearqc(SafeCompute.add(cu.getYbyearqc(), vo.getYbyearqc()));
			cu.setYbyearjffse(SafeCompute.add(cu.getYbyearjffse(), vo.getYbyearjffse()));
			cu.setYbyeardffse(SafeCompute.add(cu.getYbyeardffse(), vo.getYbyeardffse()));
			cu.setYbthismonthqc(SafeCompute.add(cu.getYbthismonthqc(), vo.getYbthismonthqc()));
			//
			cu.setBnqcnum(SafeCompute.add(cu.getBnqcnum(), vo.getBnqcnum()));
			cu.setBnfsnum(SafeCompute.add(cu.getBnfsnum(), vo.getBnfsnum()));
			cu.setBndffsnum(SafeCompute.add(cu.getBndffsnum(), vo.getBndffsnum()));
			cu.setMonthqmnum(SafeCompute.add(cu.getMonthqmnum(), vo.getMonthqmnum()));
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
	//本年利润 利润分配     小企业会计准则/企业会计准则/企业会计制度
	private final String[] corpType = {"00000100AA10000000000BMD", "00000100AA10000000000BMF","00000100000000Ig4yfE0005"};
	private final String[][] lrkm = {{"3103","3104"},{"4103","4104"},{"3131","3141"}};
	private final String[][] srkm = {{"5001","5051","5111","5301"},{"6001","6051","6101","6111","6301"},{"5101","5102","5201","5203","5301"}};
	private final String[][] fykm = {{"5401","5402","5403","5601","5602","5603","5711","5801"},{"6401","6402","6403","6601","6602","6603","6701","6711","6801"},{"5401","5402","5405","5501","5502","5503","5601","5701"}};

	private int searchIndexForArray(String[] arr, String obj){
		int result = -1;

		for(int i = 0; i < arr.length; i++){
			if(obj.equals(arr[i])){
				result = i;
				break;
			}
		}

		return result;
	}

	public SsphRes ssph(String pk_corp) throws DZFWarpException {
		List<QcYeVO> qcvos = queryAllQcInfo(pk_corp, zhbwb, null);

		CorpVO cpvo  = corpService.queryByPk(pk_corp);

		if (qcvos == null || qcvos.size() == 0) {
			return new SsphRes();
		}
		DZFDouble z1 = null;
		DZFDouble z2 = null;
		DZFDouble z3 = null;
		DZFDouble z4 = null;
		//“本年利润+利润分配”本年累计=净利润本年累计
		DZFDouble z5 = new DZFDouble(0);
		DZFDouble z6 = new DZFDouble(0);
		int positon = searchIndexForArray(corpType, cpvo.getCorptype());

		for (QcYeVO c : qcvos) {
//			if(c.getVlevel() != null && c.getVlevel()==1){
//			if (c.getIsleaf() != null && c.getIsleaf().booleanValue()) {
			if(c.getVcode()!=null && c.getVcode().length() ==4){//代表一级
			//统计末级的时候，存在进项税的问题。(应收税费一级为贷方，进项税为3级为借方)
				if (c.getDirect() == 0) {// 借方
					z1 = SafeCompute.add(c.getYearqc(), z1);
					z2 = SafeCompute.add(c.getThismonthqc(), z2);
				} else {
					z3 = SafeCompute.add(c.getYearqc(), z3);
					z4 = SafeCompute.add(c.getThismonthqc(), z4);
				}
			}

			if(positon != -1){
				//本年利润+利润分配”本年累计=【本年利润】（本年贷方发生额-本年借方发生
				//额）+【利润分配】（本年贷方发生金额-本年借方发生金额）
				if(searchIndexForArray(lrkm[positon], c.getVcode()) >= 0){
					z5 = SafeCompute.add(SafeCompute.sub(c.getYeardffse(),c.getYearjffse()),z5);
				}
//				净利润本年累计=【收入利得科目】本年贷方发生额-【费用损失科目】本年借方发
//						生额
				//加收入  一级科目
				if(c.getVcode().length() ==4 && c.getAccountkind() == 5){

					if((cpvo.getCorptype().equals("00000100AA10000000000BMF") && c.getVcode().startsWith("6901")) ||  (cpvo.getCorptype().equals("00000100000000Ig4yfE0005") && c.getVcode().startsWith("5801"))){
						continue;
					}

					if(c.getDirect() == 0){ //借方
						z6 = SafeCompute.sub(z6,c.getYearjffse());
					}else{
						z6 = SafeCompute.add(c.getYeardffse(),z6);
					}
				}
			}
		}
		return setResult(z1, z2, z3, z4, z5,z6);
	}

	public SsphRes setResult(DZFDouble z1, DZFDouble z2, DZFDouble z3,
			DZFDouble z4,DZFDouble z5,DZFDouble z6) {
		SsphRes ss = new SsphRes();
		DZFDouble ce = SafeCompute.sub(z1.setScale(2, DZFDouble.ROUND_HALF_UP), z3.setScale(2, DZFDouble.ROUND_HALF_UP));
		DZFDouble ce1 = SafeCompute.sub(z2.setScale(2, DZFDouble.ROUND_HALF_UP), z4.setScale(2, DZFDouble.ROUND_HALF_UP));
		ss.setYearjf(z1);
		ss.setYeardf(z3);
		ss.setYearce(ce);
		ss.setYearres(ce.doubleValue() == 0 ? "平衡" : "不平衡");
		ss.setMonthjf(z2);
		ss.setMonthdf(z4);
		ss.setMonthce(ce1);
		ss.setMonthres(ce1.doubleValue() == 0 ? "平衡" : "不平衡");

		DZFDouble lr = SafeCompute.sub(z5.setScale(2, DZFDouble.ROUND_HALF_UP), z6.setScale(2, DZFDouble.ROUND_HALF_UP));
		ss.setYearlr(z5);
		ss.setYearlrlj(z6);
		ss.setYearlrce(lr);
		ss.setYearlrres(lr.doubleValue() == 0 ? "平衡" : "不平衡");
		return ss;
	}

	public void operateAction(String userid, DZFDate jzdate,
			String pk_currency, List<QcYeVO> list) {
		for (QcYeVO c : list) {
			c.setDoperatedate(jzdate);
			c.setPk_currency(pk_currency);// 币种
			c.setChildren(null);
			c.setDr(0);
			c.setCoperatorid(userid);
			if (jzdate != null) {
				c.setVyear(Integer.valueOf(jzdate.toString().substring(0, 4)));
				c.setPeriod(jzdate.toString().substring(0, 7));
			}
		}
	}

	public void beforesave(QcYeVO[] qcvos, List<QcYeVO> list)
			throws DZFWarpException {
		if (qcvos != null && qcvos.length > 0) {
			for (QcYeVO c : qcvos) {
				// if((c.getBnfsnum()!=null || c.getBnqcnum()!=null ||
				// c.getBndffsnum()!=null)&& !c.getIsnum().booleanValue()){
				// throw new BusinessException("不启用库存，科目不开启数量则无法添加数量");
				// }
				list.add(c);
				QcYeVO[] vos = c.getChildren();
				beforesave(vos, list);
			}
		}
	}

	public void beforesave2(QcYeVO[] qcvos, List<QcYeVO> list)
			throws DZFWarpException {
		if (qcvos != null && qcvos.length > 0) {
			for (QcYeVO c : qcvos) {
				list.add(c);
				QcYeVO[] vos = c.getChildren();
				beforesave2(vos, list);
			}
		}
	}

	// 固定资产同步
	@Override
	public void saveGdzcsync(String userid, DZFDate jzdate, String pk_corp)
			throws DZFWarpException {
		//固定资产启用日期和建账日期不一样
		CorpVO cpvo  = corpService.queryByPk(pk_corp);
		if(cpvo.getBusibegindate()!=null && 
				!cpvo.getBusibegindate().equals(cpvo.getBegindate())){
			throw new BusinessException("固定资产启用日期("+cpvo.getBusibegindate()+")与建账日期("+cpvo.getBegindate()+")不一致，不能同步期初资产");
		}
		String rmb = IGlobalConstants.RMB_currency_id;// 人民币
		// 查询人民币
		List<QcYeVO> list = queryAllQcInfo(pk_corp, rmb, rmb);
		if (list != null && list.size() > 0) {
			gdzcsync.saveAssetSync(list.toArray(new QcYeVO[0]), pk_corp,
					singleObjectBO);
		}
	}
	
	// 清除所有数据
	@Override
	public void deleteAll(String pk_corp)
			throws DZFWarpException {
		String sql = new String(" delete from ynt_qcye where pk_corp = ? ");// 人民币
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(sql, sp);
		sql = new String(" delete from ynt_fzhsqc where pk_corp = ? ");
		singleObjectBO.executeUpdate(sql, sp);
		
		sql = " delete from ynt_verify_begin where pk_corp = ? ";
		singleObjectBO.executeUpdate(sql, sp);
	}
	//清除发生
	public void deleteFs(String pk_corp)throws DZFWarpException {
		List<QcYeVO> qcvos = queryAllQcInfo(pk_corp, zhbwb, null);
		if(qcvos != null && qcvos.size() > 0){
			for(QcYeVO vo : qcvos){
				vo.setYeardffse(null);
				vo.setYearjffse(null);
				vo.setYbyearjffse(null);
				vo.setYbyeardffse(null);
				vo.setBndffsnum(null);
				vo.setBnfsnum(null);
				vo.setMonthqmnum(vo.getBnqcnum());
				vo.setThismonthqc(vo.getYearqc());
				vo.setYbthismonthqc(vo.getYbyearqc());
			}
			singleObjectBO.updateAry(qcvos.toArray(new QcYeVO[0]),new String[]{
					"yeardffse","yearjffse","ybyearjffse","ybyeardffse","bndffsnum","bnfsnum","monthqmnum","thismonthqc","ybthismonthqc"	
				});	
		}
		//清除辅助核算发生
		FzhsqcVO[] fzhsvos = queryFzhsQcVOs(pk_corp);
		if(fzhsvos != null && fzhsvos.length >0){
			for(FzhsqcVO vo : fzhsvos){
				vo.setYeardffse(null);
				vo.setYearjffse(null);
				vo.setYbyearjffse(null);
				vo.setYbyeardffse(null);
				vo.setBndffsnum(null);
				vo.setBnfsnum(null);
				vo.setMonthqmnum(vo.getBnqcnum());
				vo.setThismonthqc(vo.getYearqc());
				vo.setYbthismonthqc(vo.getYbyearqc());
			}
			singleObjectBO.updateAry(fzhsvos,new String[]{
					"yeardffse","yearjffse","ybyearjffse","ybyeardffse","bndffsnum","bnfsnum","monthqmnum","thismonthqc","ybthismonthqc"	
				});	
		}
	}
	
	
	public FzhsqcVO[] queryFzhsQcVOs(String pk_corp)throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		FzhsqcVO[] vos = (FzhsqcVO[])singleObjectBO.queryByCondition(FzhsqcVO.class, "nvl(dr,0) = 0 and pk_corp = ? ", sp);
		return vos;
	}

	@Override
	public QcYeCurrency[] queryCur(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		Map<String, String> map = new HashMap<String, String>();
		List<YntCpaccountVO> list1 = queryCurrence(sp);
		if (list1 != null && list1.size() > 0) {
			for (YntCpaccountVO c : list1) {
				String pks = c.getExc_pk_currency();
				String[] rows = pks.split(",");
				for (String s : rows) {
					map.put(s, s);
				}
			}
		}// 得到币种主键
		List<QcYeCurrency> zlist = new ArrayList<QcYeCurrency>();
		zlist.add(getZHbwb());
		zlist.add(getRMB());
		String[] pks = map.values().toArray(new String[0]);
		if (pks != null && pks.length > 0) {
            BdCurrencyVO[] currencyVOS = sys_currentserv.queryCurrency();
            Map<String, BdCurrencyVO> currencyVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(currencyVOS),
                    new String[]{"pk_currency"});
			Map<String, QcYeCurrency> maps = queryQcYeCurrency(sp, pks);
			for (String pk : pks) {
				QcYeCurrency v = maps.get(pk);
				if (v == null) {
					QcYeCurrency vo = new QcYeCurrency();
					vo.setPk_currency(pk);
					if (currencyVOMap.containsKey(pk)) {
                        vo.setCurrencyname(currencyVOMap.get(pk).getCurrencyname());
                    }
					vo.setConvmode(0);
					vo.setExrate(new DZFDouble(1));
					zlist.add(vo);
				} else {
					zlist.add(v);
				}
			}
		}
		return getQcyecommon().outRepeat(zlist);
	}

	public QcYeCurrency getZHbwb() {
		QcYeCurrency vo = new QcYeCurrency();
		vo.setPk_currency(zhbwb);
		vo.setCurrencyname("综合本位币");
		vo.setConvmode(0);
		vo.setExrate(new DZFDouble(1));
		return vo;
	}

	public QcYeCurrency getRMB() {
		QcYeCurrency vo = new QcYeCurrency();
		vo.setPk_currency(IGlobalConstants.RMB_currency_id);
		vo.setCurrencyname("人民币");
		vo.setConvmode(0);
		vo.setExrate(new DZFDouble(1));
		return vo;
	}

	public Map<String, QcYeCurrency> queryQcYeCurrency(SQLParameter sp,
			String[] args) {
		String where = getQcyecommon().getInWhereClauseVO(args);
		Map<String, QcYeCurrency> map = null;
		if (where != null && where.length() > 0) {
			StringBuffer sf = new StringBuffer();
			sf.append(" select t1.pk_currency,t1.currencyname,t2.exrate,t2.convmode from ynt_bd_currency t1 ");
			sf.append(" join ynt_exrate t2 on t1.pk_currency = t2.pk_currency ");
			sf.append(" where t2.pk_corp = ? and nvl(t1.dr,0)= 0 and nvl(t2.dr,0)=0 ");
			sf.append(" and t1.pk_currency  " + where);
			List<QcYeCurrency> list = (List<QcYeCurrency>) singleObjectBO
					.executeQuery(sf.toString(), sp, new BeanListProcessor(
							QcYeCurrency.class));
			map = getQcyecommon().hashlizeObject(list);
		}
		return map;
	}

	// 查询
	public List<YntCpaccountVO> queryCurrence(SQLParameter sp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		sf.append(" select exc_pk_currency from ynt_cpaccount where pk_corp = ? and  exc_pk_currency is not null and nvl(dr,0) = 0  ");
		List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO
				.executeQuery(sf.toString(), sp, new BeanListProcessor(
						YntCpaccountVO.class));
		return list;
	}

	@Override
	public QcYeVO queryByPrimaryKey(String primaryKey) {
		SuperVO msvo = singleObjectBO.queryByPrimaryKey(QcYeVO.class,
				primaryKey);
		return (QcYeVO) msvo;
	}

	@Override
	public void saveOne(String userid, DZFDate jzdate, String pk_currency,
			String pk_corp, QcYeVO[] qcvos) throws DZFWarpException {
		// 已经结账，不让保存
		String njsql = "select max(period) from ynt_qmjz where pk_corp =? and nvl(dr,0)=0 and nvl(jzfinish,'N') = 'Y'";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String maxvalue = (String) singleObjectBO.executeQuery(njsql, sp,
				new ColumnProcessor());
		if (!StringUtil.isEmpty(maxvalue)) {
			throw new BusinessException("当前最近结账日期为:" + maxvalue + "不能保存!");
		}
		// 重新累计
		calc(qcvos, null);
		// 保存
		List<QcYeVO> list = new ArrayList<QcYeVO>();
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class,
				pk_corp);
		if (!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			beforesave(qcvos, list);
		} else {
			beforesave2(qcvos, list);
		}
		operateAction(userid, jzdate, pk_currency, list);

		sp.clearParams();
		sp.addParam(pk_corp);
		sp.addParam(pk_currency);

		StringBuffer kmids = new StringBuffer("(");
		for (int i = 0, size = list.size(); i < size; i++) {
			if (i > 0)
				kmids.append(",");
			kmids.append("?");
			sp.addParam(list.get(i).getPk_accsubj());
		}
		kmids.append(")");
		StringBuffer sql = new StringBuffer();
		sql.append("delete from ynt_qcye where pk_corp = ? and nvl(dr,0) =  0 and nvl(pk_currency,'00000100AA10000000000BKT') = ?");
		sql.append(" and pk_accsubj in  ");
		sql.append(kmids.toString());
		// 先删除
		singleObjectBO.executeUpdate(sql.toString(), sp);
		// 后插入
		singleObjectBO.insertVOArr(pk_corp, list.toArray(new QcYeVO[0]));
	}

	@Override
	public void saveVerifyBegin(String pk_accsubj, CorpVO corp, VerifyBeginVo[] vos) throws DZFWarpException {
		DZFDate beginDate = corp.getBegindate();
		for (VerifyBeginVo verifyBeginVo : vos) {
			if (verifyBeginVo.getOccur_date() == null) {
				throw new BusinessException("业务发生日期不能为空!");
			}
			if (beginDate.before(verifyBeginVo.getOccur_date())) {
				throw new BusinessException("业务发生日期不能晚于建账日期!");
			}
			verifyBeginVo.setPk_accsubj(pk_accsubj);
			verifyBeginVo.setPk_corp(corp.getPk_corp());
			verifyBeginVo.setDr(0);
			verifyBeginVo.setTs(new DZFDateTime());
		}
		String sql = "delete from ynt_verify_begin where pk_corp = ? and pk_accsubj = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(corp.getPk_corp());
		sp.addParam(pk_accsubj);
		singleObjectBO.executeUpdate(sql, sp);
		if (vos.length > 0) {
			singleObjectBO.insertVOArr(corp.getPk_corp(), vos);
		}
	}

	@Override
	public List<String> queryVerifyBeginAccounts(String pk_corp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		
		sf.append(" select distinct b.accountcode ")
		.append(" from ynt_verify_begin a left join ynt_cpaccount b on a.pk_accsubj = b.pk_corp_account ")
		.append(" where a.pk_corp = ? and nvl(a.dr,0) = 0 ");
		sp.addParam(pk_corp);
		List<String> rs = (List<String>) singleObjectBO
				.executeQuery(sf.toString(), sp, new ColumnListProcessor());
		return rs;
	}

	@Override
	public List<VerifyBeginVo> queryVerifyBegin(String pk_corp, String pk_accsubj)
			throws DZFWarpException {
		
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		
		sf.append(" select a.*, b.accountcode as vcode, b.accountname as vname, b.isfzhs as isfzhs ")
		.append(" from ynt_verify_begin a left join ynt_cpaccount b on a.pk_accsubj = b.pk_corp_account ")
		.append(" where a.pk_corp = ? and a.pk_accsubj = ? and nvl(a.dr,0) = 0 ");
		sp.addParam(pk_corp);
		sp.addParam(pk_accsubj);
		List<VerifyBeginVo> rs = (List<VerifyBeginVo>) singleObjectBO
				.executeQuery(sf.toString(), sp, new BeanListProcessor(
						VerifyBeginVo.class));
		
		if (rs.size() > 0) {
			String isfzhs = rs.get(0).getIsfzhs();
			if (!AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT.equals(isfzhs)) {
				Map<String, AuxiliaryAccountBVO> invmap = ReportUtil.getInvAuaccount(pk_corp);
				Map<String, AuxiliaryAccountBVO> auaccountMap = gl_fzhsserv.queryMap(pk_corp);
				for (VerifyBeginVo verifyBeginVo : rs) {
					String[] comboStr = ReportUtil.getCombStr(verifyBeginVo, "vcode", "vname", auaccountMap, invmap);
					verifyBeginVo.setVcode(comboStr[0]);
					verifyBeginVo.setVname(comboStr[1]);
				}
			}
		}
		
		return rs;
	}
	
	@Override
	public byte[] exportExcel(String pk_corp, boolean showQuantity) throws DZFWarpException {

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("期初导入");
		
		sheet.setDefaultColumnWidth(15);
		
		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true); // 加粗
		HSSFCellStyle headStyle = workbook.createCellStyle();
		headStyle.setFont(font);
		headStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
		headStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中
		
		Map<Integer, String> usedFz = getUsedfz(pk_corp);
		
		List<Integer> fzCode = new ArrayList<Integer>(usedFz.keySet());
		
		createSheetHead(sheet, headStyle, new ArrayList<String>(usedFz.values()), showQuantity);
		
		HSSFDataFormat df = workbook.createDataFormat(); // 此处设置数据格式
		HSSFCellStyle numberCell2 = workbook.createCellStyle();
		font = workbook.createFont();
		font.setBold(false);
		numberCell2.setFont(font);
		numberCell2.setAlignment(HorizontalAlignment.CENTER);
		numberCell2.setVerticalAlignment(VerticalAlignment.CENTER);
		numberCell2.setDataFormat(df.getFormat("#,##0.00")); // 保留小数点后2位
		
		int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
		HSSFCellStyle numberCell4 = workbook.createCellStyle();
		numberCell4.cloneStyleFrom(numberCell2);
		numberCell4.setDataFormat(df.getFormat("#,##0.00000000".substring(0, 6 + numPrecision)));
		
		//获取期初数据
		String rmb = IGlobalConstants.RMB_currency_id;
		List<FzhsqcVO> qclist = queryAll(pk_corp, rmb, rmb);
		FzhsqcVO[] fzqc = gl_fzhsqcserv.queryAll(pk_corp, rmb);
		qclist.addAll(Arrays.asList(fzqc));
		
		Collections.sort(qclist, new Comparator<FzhsqcVO>() {
			@Override
			public int compare(FzhsqcVO o1, FzhsqcVO o2) {
				return o1.getVcode().compareTo(o2.getVcode());
			}
		});
		
		//期初金额列
		int qcBegin = 4 + usedFz.size();
		//公式
		String[] formulas = getExcelFormula(qcBegin);
		
		int rowIndex = 2;
		for (FzhsqcVO fzhsqcVO : qclist) {
			HSSFRow row = sheet.createRow(rowIndex);
			HSSFCell cell = row.createCell(0);
			cell.setCellValue(fzhsqcVO.getVcode());
			cell = row.createCell(1);
			cell.setCellValue(fzhsqcVO.getVname());
			
			cell = row.createCell(2);
			cell.setCellValue(fzhsqcVO.getDirect() == 0 ? "借" : "贷");
			
			cell = row.createCell(3);
			cell.setCellValue(
					AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT
					.equals(fzhsqcVO.getIsfzhs()) ? "否" : "是");
			
			cell = row.createCell(qcBegin);
			cell.setCellStyle(numberCell4);
			if (fzhsqcVO.getBnqcnum() != null && fzhsqcVO.getBnqcnum().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getBnqcnum().doubleValue());
			}
			
			String[] codes = fzhsqcVO.getVcode().split("_");
			if(codes.length > 1) {
				for (int i = 1, j = 1; i <= 10; i++) {
					if (fzhsqcVO.getAttributeValue("fzhsx" + i) != null) {
						int column = 4 + fzCode.indexOf(i);
						cell = row.createCell(column);
						cell.setCellValue(codes[j]);
						j++;
					}
				}
			}
			
			cell = row.createCell(qcBegin + 1);
			cell.setCellStyle(numberCell2);
			if (fzhsqcVO.getYearqc() != null && fzhsqcVO.getYearqc().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getYearqc().doubleValue());
			}
			
			cell = row.createCell(qcBegin + 2);
			cell.setCellStyle(numberCell4);
			if (fzhsqcVO.getBnfsnum() != null && fzhsqcVO.getBnfsnum().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getBnfsnum().doubleValue());
			}
			
			cell = row.createCell(qcBegin + 3);
			cell.setCellStyle(numberCell2);
			if (fzhsqcVO.getYearjffse() != null && fzhsqcVO.getYearjffse().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getYearjffse().doubleValue());
			}
			
			cell = row.createCell(qcBegin + 4);
			cell.setCellStyle(numberCell4);
			if (fzhsqcVO.getBndffsnum() != null && fzhsqcVO.getBndffsnum().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getBndffsnum().doubleValue());
			}
			
			cell = row.createCell(qcBegin + 5);
			cell.setCellStyle(numberCell2);
			if (fzhsqcVO.getYeardffse() != null && fzhsqcVO.getYeardffse().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getYeardffse().doubleValue());
			}
			
			cell = row.createCell(qcBegin + 6);
			cell.setCellStyle(numberCell4);
			cell.setCellFormula(formulas[0]);
			if (fzhsqcVO.getMonthqmnum() != null && fzhsqcVO.getMonthqmnum().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getMonthqmnum().doubleValue());
			}
			cell = row.createCell(qcBegin + 7);
			cell.setCellStyle(numberCell2);
			cell.setCellFormula(formulas[1]);
			if (fzhsqcVO.getThismonthqc() != null && fzhsqcVO.getThismonthqc().doubleValue() != 0) {
				cell.setCellValue(fzhsqcVO.getThismonthqc().doubleValue());
			}
			
			rowIndex++;
		}
		ByteArrayOutputStream bao = null;
		byte[] data = null;
		try {
			bao = new ByteArrayOutputStream();
			workbook.write(bao);
			data = bao.toByteArray();
			bao.close();
		} catch (IOException e) {
			
		} finally {
			if (bao != null) {
				try {
					bao.close();
				} catch (IOException e) {
					
				}
			}
		}
		return data;
	}
	
	private void createSheetHead(HSSFSheet sheet, HSSFCellStyle headStyle,
                                 List<String> usedFz, boolean showQuantity) {
		
		
		HSSFRow row = sheet.createRow(0);
		row.setHeightInPoints(20);
		HSSFCell cell = row.createCell(0);
		cell.setCellValue("科目编码");
		cell.setCellStyle(headStyle);
		
		cell = row.createCell(1);
		cell.setCellValue("科目名称");
		cell.setCellStyle(headStyle);
		
		cell = row.createCell(2);
		cell.setCellValue("方向");
		cell.setCellStyle(headStyle);
		
		cell = row.createCell(3);
		cell.setCellValue("是否辅助核算");
		cell.setCellStyle(headStyle);
		
		int qcBegin = 4;
		for (String fzName : usedFz) {
			cell = row.createCell(qcBegin);
			cell.setCellValue(fzName);
			cell.setCellStyle(headStyle);
			qcBegin++;
		}
		
		
		cell = row.createCell(qcBegin);
		cell.setCellValue("本年期初");
		cell.setCellStyle(headStyle);
		
		cell = row.createCell(qcBegin + 2);
		cell.setCellValue("本年借方发生");
		cell.setCellStyle(headStyle);
		
		cell = row.createCell(qcBegin + 4);
		cell.setCellValue("本年贷方发生");
		cell.setCellStyle(headStyle);
		
		cell = row.createCell(qcBegin + 6);
		cell.setCellValue("本月期初");
		cell.setCellStyle(headStyle);
		
		row = sheet.createRow(1);
		row.setHeightInPoints(20);
		
		cell = row.createCell(qcBegin);
		cell.setCellValue("数量");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 1);
		cell.setCellValue("金额");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 2);
		cell.setCellValue("数量");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 3);
		cell.setCellValue("金额");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 4);
		cell.setCellValue("数量");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 5);
		cell.setCellValue("金额");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 6);
		cell.setCellValue("数量");
		cell.setCellStyle(headStyle);
		cell = row.createCell(qcBegin + 7);
		cell.setCellValue("金额");
		cell.setCellStyle(headStyle);
		
		if (!showQuantity) {
			sheet.setColumnHidden(qcBegin, true);
			sheet.setColumnHidden(qcBegin + 2, true);
			sheet.setColumnHidden(qcBegin + 4, true);
			sheet.setColumnHidden(qcBegin + 6, true);
		}
		
		for (int i = 0; i < qcBegin; i++) {
			sheet.addMergedRegion(new CellRangeAddress(0, 1, i, i));
		}
		
		for (int i = 0; i < 4; i++) {
			sheet.addMergedRegion(new CellRangeAddress(0, 0, qcBegin, qcBegin + 1));
			qcBegin += 2;
		}
	}
	
	private String[] getExcelFormula(int begin) {
		String qcnum = CellReference.convertNumToColString(begin++);
		String qcmny = CellReference.convertNumToColString(begin++);
		String jfnum = CellReference.convertNumToColString(begin++);
		String jfmny = CellReference.convertNumToColString(begin++);
		String dfnum = CellReference.convertNumToColString(begin++);
		String dfmny = CellReference.convertNumToColString(begin);
		
		String quantityFormula = "IF(ISERROR(IF($C:$C=\"借\","
				+ "IF(ISNUMBER($qcnum:$qcnum),$G:$G,0)"
				+ "+IF(ISNUMBER($jfnum:$jfnum),$jfnum:$jfnum,0)"
				+ "-IF(ISNUMBER($dfnum:$dfnum),$dfnum:$dfnum,0),"
				+ "IF(ISNUMBER($qcnum:$qcnum),$qcnum:$qcnum,0)"
				+ "-IF(ISNUMBER($jfnum:$jfnum),$jfnum:$jfnum,0)"
				+ "+IF(ISNUMBER($dfnum:$dfnum),$dfnum:$dfnum,0))),"
				+ "\"\",IF($C:$C=\"借\",IF(ISNUMBER($qcnum:$qcnum),$qcnum:$qcnum,0)"
				+ "+IF(ISNUMBER($jfnum:$jfnum),$jfnum:$jfnum,0)-IF(ISNUMBER($dfnum:$dfnum),$dfnum:$dfnum,0),"
				+ "IF(ISNUMBER($qcnum:$qcnum),$qcnum:$qcnum,0)-IF(ISNUMBER($jfnum:$jfnum),$jfnum:$jfnum,0)"
				+ "+IF(ISNUMBER($dfnum:$dfnum),$dfnum:$dfnum,0)))";
		
		quantityFormula = quantityFormula.replaceAll("qcnum", qcnum)
				.replaceAll("jfnum", jfnum).replaceAll("dfnum", dfnum);
		String amountFormula = "IF(ISERROR(IF($C:$C=\"借\",IF(ISNUMBER($qcmny:$qcmny),"
				+ "$qcmny:$qcmny,0)+IF(ISNUMBER($jfmny:$jfmny),$jfmny:$jfmny,0)-"
				+ "IF(ISNUMBER($dfmny:$dfmny),$dfmny:$dfmny,0),IF(ISNUMBER($qcmny:$qcmny),$qcmny:$qcmny,0)"
				+ "-IF(ISNUMBER($jfmny:$jfmny),$jfmny:$jfmny,0)+IF(ISNUMBER($dfmny:$dfmny),$dfmny:$dfmny,0))),"
				+ "\"\",IF($C:$C=\"借\",IF(ISNUMBER($qcmny:$qcmny),$qcmny:$qcmny,0)"
				+ "+IF(ISNUMBER($jfmny:$jfmny),$jfmny:$jfmny,0)-IF(ISNUMBER($dfmny:$dfmny),$dfmny:$dfmny,0),"
				+ "IF(ISNUMBER($qcmny:$qcmny),$qcmny:$qcmny,0)-IF(ISNUMBER($jfmny:$jfmny),$jfmny:$jfmny,0)"
				+ "+IF(ISNUMBER($dfmny:$dfmny),$dfmny:$dfmny,0)))";
		amountFormula = amountFormula.replaceAll("qcmny", qcmny)
				.replaceAll("jfmny", jfmny).replaceAll("dfmny", dfmny);
		return new String[]{quantityFormula, amountFormula};
	}

	@Override
	public void processImportExcel(String pk_corp, DZFDate jzdate, File file) throws DZFWarpException {
		// TODO Auto-generated method stub
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);
			
			String rmb = IGlobalConstants.RMB_currency_id;
			List<QcYeVO> qclist = queryAllQcInfo(pk_corp, rmb, rmb);
			FzhsqcVO[] fzqc = gl_fzhsqcserv.queryAll(pk_corp, rmb);
			Map<String, QcYeVO> qcMap = new HashMap<String, QcYeVO>();
			Map<String, FzhsqcVO> fzqcMap = new HashMap<String, FzhsqcVO>();
			for (QcYeVO qcYeVO : qclist) {
				qcYeVO.setBnqcnum(null);
				qcYeVO.setBnfsnum(null);
				qcYeVO.setBndffsnum(null);
				qcYeVO.setYearqc(null);
				qcYeVO.setYearjffse(null);
				qcYeVO.setYeardffse(null);
				qcYeVO.setDoperatedate(jzdate);
				qcYeVO.setPk_currency(IGlobalConstants.RMB_currency_id);// 币种
				qcYeVO.setDr(0);
//				qcYeVO.setCoperatorid(userid);
				if (jzdate != null) {
					qcYeVO.setVyear(Integer.valueOf(jzdate.toString().substring(0, 4)));
					qcYeVO.setPeriod(jzdate.toString().substring(0, 7));
				}
				qcMap.put(qcYeVO.getVcode(), qcYeVO);
			}
			for (FzhsqcVO fzhsqcVO : fzqc) {
				fzhsqcVO.setBnqcnum(null);
				fzhsqcVO.setBnfsnum(null);
				fzhsqcVO.setBndffsnum(null);
				fzhsqcVO.setYearqc(null);
				fzhsqcVO.setYearjffse(null);
				fzhsqcVO.setYeardffse(null);
				fzhsqcVO.setDoperatedate(jzdate);
				fzhsqcVO.setPk_currency(IGlobalConstants.RMB_currency_id);// 币种
				fzhsqcVO.setDr(0);
//				fzhsqcVO.setCoperatorid(userid);
				if (jzdate != null) {
					fzhsqcVO.setVyear(Integer.valueOf(jzdate.toString().substring(0, 4)));
					fzhsqcVO.setPeriod(jzdate.toString().substring(0, 7));
				}
				fzqcMap.put(fzhsqcVO.getVcode(), fzhsqcVO);
			}
			
			HSSFRow row = sheet.getRow(0);
			Map<String, Integer> headIndex = new HashMap<String, Integer>();
			for (int i = 0; i <= row.getLastCellNum(); i++) {
				if (row.getCell(i) != null) {
					String columnName = row.getCell(i).getStringCellValue();
					if (!StringUtil.isEmpty(columnName)) {
						headIndex.put(columnName, i);
					}
				}
			}
			// 数量精度
			int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
			//金额2位精度
			int jePrecision = 2;

			int rowNum = sheet.getLastRowNum();
			
			int colIndex = 0;
			if (headIndex.containsKey("本年期初")) {
				colIndex = headIndex.get("本年期初");
			} else {
				throw new BusinessException("导入失败，模板不匹配，请检查");
			}
			for(int rowIndex = 2; rowIndex <= rowNum; rowIndex++){
				row = sheet.getRow(rowIndex);
				if(row == null)
					continue;
				String vcode = row.getCell(0).getStringCellValue();
				boolean isfzhs = vcode.indexOf("_") > -1;
				SuperVO qcvo = null;
				if (isfzhs) {
					qcvo = fzqcMap.get(vcode);
					if (qcvo == null)
						throw new BusinessException("导入失败，模板中存在辅助核算编码不存在的数据，请检查");
				} else {
					qcvo = qcMap.get(vcode);
					if (qcvo == null)
						throw new BusinessException("导入失败，模板中存在科目编码不存在的数据，请检查");
				}
//				SuperVO qcvo = vcode.indexOf("_") > -1 ? fzqcMap.get(vcode) : qcMap.get(vcode);
				if (qcvo == null
						|| !isfzhs && !AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT.equals(qcvo.getAttributeValue("isfzhs"))) {
					continue;
				}
				DZFBoolean accIsNum = (DZFBoolean) qcvo.getAttributeValue("isnum");
				boolean isnum = accIsNum != null && accIsNum.booleanValue() ? true : false;
				
				if (isnum) {
					double bnqcnum = row.getCell(colIndex).getNumericCellValue();
					if (bnqcnum != 0) {
						qcvo.setAttributeValue("bnqcnum", new DZFDouble(bnqcnum, numPrecision));
					}
					double bnfsnum = row.getCell(colIndex + 2).getNumericCellValue();
					if (bnfsnum != 0) {
						qcvo.setAttributeValue("bnfsnum", new DZFDouble(bnfsnum, numPrecision));
					}
					double bndffsnum = row.getCell(colIndex + 4).getNumericCellValue();
					if (bndffsnum != 0) {
						qcvo.setAttributeValue("bndffsnum", new DZFDouble(bndffsnum, numPrecision));
					}
					double monthqmnum = row.getCell(colIndex + 6).getNumericCellValue();
					if (monthqmnum != 0) {
						qcvo.setAttributeValue("monthqmnum", new DZFDouble(monthqmnum, numPrecision));
					}
				}
				double yearqc = row.getCell(colIndex + 1).getNumericCellValue();
				if (yearqc != 0) {
					qcvo.setAttributeValue("yearqc", new DZFDouble(yearqc, jePrecision));
				}
				
				double yearjffse = row.getCell(colIndex + 3).getNumericCellValue();
				if (yearjffse != 0) {
					qcvo.setAttributeValue("yearjffse", new DZFDouble(yearjffse, jePrecision));
				}
				
				double yeardffse = row.getCell(colIndex + 5).getNumericCellValue();
				if (yeardffse != 0) {
					qcvo.setAttributeValue("yeardffse", new DZFDouble(yeardffse, jePrecision));
				}
				
				if (jzdate.getMonth() == 1 && (yearjffse != 0 || yeardffse != 0) ) {
					throw new BusinessException("期初建账，不允许录入发生额，请检查");
				}
				
				double thismonthqc = row.getCell(colIndex + 7).getNumericCellValue();
				if (thismonthqc != 0) {
					qcvo.setAttributeValue("thismonthqc", new DZFDouble(thismonthqc, jePrecision));
				}
				
			}
			
			is.close();
			
			reCalculate(pk_corp, qcMap, fzqcMap);
			
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			String sql = "delete from ynt_fzhsqc where pk_corp = ? and nvl(dr,0) = 0 ";
			singleObjectBO.executeUpdate(sql, sp);
			singleObjectBO.insertVOArr(pk_corp, fzqcMap.values().toArray(new FzhsqcVO[0]));
			
			//删除辅助期初
			sql = " delete from ynt_verify_begin where pk_corp = ? ";
			singleObjectBO.executeUpdate(sql, sp);
			
			sp.addParam(IGlobalConstants.RMB_currency_id);
			// 先删除
			singleObjectBO
					.executeUpdate(
							" delete from ynt_qcye where pk_corp = ? and nvl(dr,0) =  0 and nvl(pk_currency,'00000100AA10000000000BKT') = ?",
							sp);
			// 后插入
			singleObjectBO.insertVOArr(pk_corp, qcMap.values().toArray(new QcYeVO[0]));
			
			
		} catch (FileNotFoundException e) {
			throw new BusinessException("未找到导入文件");
		} catch (IOException e) {
			throw new BusinessException("导入文件格式错误");
		}finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	private void reCalculate(String pk_corp, Map<String, QcYeVO> qcMap, Map<String, FzhsqcVO> fzqcMap) {
		for (String code : fzqcMap.keySet()) {
			
			FzhsqcVO fzqcvo = fzqcMap.get(code);
			DZFDouble qm = null;
			DZFDouble qcnum = null ;
			DZFDouble ybqm = null;
			if (fzqcvo.getDirect() == 0) {// 借方
				qm = SafeCompute.sub(SafeCompute.add(fzqcvo.getYearqc(), fzqcvo.getYearjffse()),fzqcvo.getYeardffse());
				ybqm =  SafeCompute.sub(SafeCompute.add(fzqcvo.getYbyearqc(), fzqcvo.getYbyearjffse()),fzqcvo.getYbyeardffse());
				qcnum = SafeCompute.sub(SafeCompute.add(fzqcvo.getBnqcnum(), fzqcvo.getBnfsnum()),fzqcvo.getBndffsnum());
			} else {// 贷方
				qm = SafeCompute.sub(SafeCompute.add(fzqcvo.getYearqc(), fzqcvo.getYeardffse()),fzqcvo.getYearjffse());
				ybqm =  SafeCompute.sub(SafeCompute.add(fzqcvo.getYbyearqc(), fzqcvo.getYbyeardffse()),fzqcvo.getYbyearjffse());
				qcnum = SafeCompute.sub(SafeCompute.add(fzqcvo.getBnqcnum(), fzqcvo.getBndffsnum()),fzqcvo.getBnfsnum());
			}
			fzqcvo.setThismonthqc(qm);
			fzqcvo.setYbthismonthqc(ybqm);
			fzqcvo.setMonthqmnum(qcnum);
			
			String vcode = code.split("_")[0];
			QcYeVO qcvo = qcMap.get(vcode);
			
			if (fzqcvo.getYearqc() != null) {
				qcvo.setYearqc(SafeCompute.add(qcvo.getYearqc(), fzqcvo.getYearqc()));
			}
			if (fzqcvo.getYearjffse() != null) {
				qcvo.setYearjffse(SafeCompute.add(qcvo.getYearjffse(), fzqcvo.getYearjffse()));
			}
			if (fzqcvo.getYeardffse() != null) {
				qcvo.setYeardffse(SafeCompute.add(qcvo.getYeardffse(), fzqcvo.getYeardffse()));
			}
			if (fzqcvo.getBnqcnum() != null) {
				qcvo.setBnqcnum(SafeCompute.add(qcvo.getBnqcnum(), fzqcvo.getBnqcnum()));
			}
			if (fzqcvo.getBndffsnum() != null) {
				qcvo.setBndffsnum(SafeCompute.add(qcvo.getBndffsnum(), fzqcvo.getBndffsnum()));
			}
			if (fzqcvo.getBnfsnum() != null) {
				qcvo.setBnfsnum(SafeCompute.add(qcvo.getBnfsnum(), fzqcvo.getBnfsnum()));
			}
			
		}
		
		QcYeVO[] qcvos = doIteratorVO(new ArrayList<QcYeVO>(qcMap.values()), pk_corp, null);
		calc(qcvos, null);
	}
	
	private Map<Integer, String> getUsedfz(String pk_corp) {
		Map<Integer, String> fzMap = new TreeMap<Integer, String>();
		StringBuffer sb = new StringBuffer();
		sb.append("select count(fzhsx1) fzhsx1, count(fzhsx2) fzhsx2,")
		.append(" count(fzhsx3) fzhsx3, count(fzhsx4) fzhsx4,")
		.append(" count(fzhsx5) fzhsx5, count(fzhsx6) fzhsx6,")
		.append(" count(fzhsx7) fzhsx7, count(fzhsx8) fzhsx8, ")
		.append("  count(fzhsx9) fzhsx9, count(fzhsx10) fzhsx10 ")
		.append("from ynt_fzhsqc where pk_corp = ? and nvl(dr, 0) = 0");
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		
		String fzhs = (String) singleObjectBO.executeQuery(sb.toString(), sp, new ResultSetProcessor() {

			@Override
			public Object handleResultSet(ResultSet rs)
					throws SQLException {
				String fzhs = "";
				while (rs.next()) {
					for (int i = 1; i <= 10; i++) {
						int count = rs.getInt(i);
						fzhs += (count > 0 ? "1" : "0");
					}
				}
				return fzhs;
			}
			
		} );
		
		Map<Integer, String> fzhsNameMap = queryFzNames(pk_corp); 
		for (int i = 0; i < fzhs.length(); i++) {
			if ('1' == fzhs.charAt(i)) {
				fzMap.put(i + 1, fzhsNameMap.get(i + 1));
			}
		}
		return fzMap;
	}
	
	@SuppressWarnings("unchecked")
	private Map<Integer, String> queryFzNames(String pk_corp) {
		StringBuffer sb = new StringBuffer();
		sb.append("select name, code from ynt_fzhs_h")
		.append(" where pk_corp = ? or pk_corp = ? and nvl(dr, 0) = 0 order by code");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(IDefaultValue.DefaultGroup);
		Map<Integer, String> fzhsNameMap = (Map<Integer, String>) singleObjectBO.executeQuery(sb.toString(), sp, new ResultSetProcessor() {
			@Override
			public Map<Integer, String> handleResultSet(ResultSet rs)
					throws SQLException {
				Map<Integer, String> fzhs = new HashMap<Integer, String>();
				while (rs.next()) {
					fzhs.put(rs.getInt("code"), rs.getString("name"));
				}
				return fzhs;
			}
			
		} );
		return fzhsNameMap;
	}

	@Override
	public void saveKc2GLSync(String userid, String pk_corp) throws DZFWarpException {
		String rmb = IGlobalConstants.RMB_currency_id;// 人民币
		// 查询人民币
		List<QcYeVO> list = queryAllQcInfo(pk_corp, rmb, rmb);
		if (list != null && list.size() > 0) {
			iservice.saveIcSync(userid, pk_corp);
		}		
	}

	@Override
	public void saveGL2KcSync(String userid, String pk_corp, StringBuffer msg) throws DZFWarpException {
		String rmb = IGlobalConstants.RMB_currency_id;//人民币
		
		FzhsqcVO[] qcvos = gl_fzhsqcserv.queryAll(pk_corp, rmb);
		
		judgeIsInvenFzhs(pk_corp, msg);
		
		List<FzhsqcVO> fzhsqcList = filterFzhsqcData(qcvos);
		
		if(fzhsqcList != null && fzhsqcList.size() > 0){
			iservice.saveGL2KcSync(userid, pk_corp, fzhsqcList, msg);
		}
	}
	
	private void judgeIsInvenFzhs(String pk_corp, StringBuffer msg){
		
		Map<String, YntCpaccountVO> cpamap = accountService.queryMapByPk(pk_corp);
		
		//判断1403、1405下级是否挂存货
		boolean ycl  = true;
		boolean kcsp = true;
		
		String code;
		YntCpaccountVO cpavo;
		for(Map.Entry<String, YntCpaccountVO> entry : cpamap.entrySet()){
			cpavo = entry.getValue();
			code = cpavo.getAccountcode();
			
			if(!StringUtil.isEmpty(code)){
				if(isInvenFzhs("1403", cpavo)){
					ycl = false;
				}
				
				if(isInvenFzhs("1405", cpavo)){
					kcsp = false;
				}
			}
		}
		
		String part = "";
		if(ycl){
			part = "原材料";
		}
		
		if(ycl && kcsp){
			part += "/";
		}
		
		if(kcsp){
			part += "库存商品";
		}
		
		if(!StringUtil.isEmpty(part)){
			msg.append(String.format("<p>%s同步失败,未启用辅助核算暂不支持“库存期初同步”</p>", 
					new String[]{ part }));
		}
	}
	
	private boolean isInvenFzhs(String chcode, YntCpaccountVO cpavo){
		boolean result = false;
		String code = cpavo.getAccountcode();
		if(!StringUtil.isEmpty(code) && code.startsWith(chcode)){
			String fzhs = cpavo.getIsfzhs();
			if(!StringUtil.isEmpty(fzhs) && fzhs.charAt(5) == '1'){
				result = true;
			}
		}
		
		return result;
	}
	
	private List<FzhsqcVO> filterFzhsqcData(FzhsqcVO[] qcvos){
		List<FzhsqcVO> aftervos = new ArrayList<FzhsqcVO>();
		
		if(qcvos != null && qcvos.length > 0){
			String vcode = null;
			
			for(FzhsqcVO vo : qcvos){
				vcode = vo.getVcode();
				if(!StringUtil.isEmpty(vo.getFzhsx6())
						&& !StringUtil.isEmpty(vcode)){
					
					if(vcode.startsWith("1403") || vcode.startsWith("1405")){
						aftervos.add(vo);
					}
					
				}
			}
			
		}
		
		return aftervos;
		
	}

	@Override
	public void processFzImportExcel(CorpVO corpVo, String userId,
			String pk_accsubj, DZFDate jzdate, File file)
			throws DZFWarpException {
		if (pk_accsubj == null) {
			throw new BusinessException("未选择科目");
		}
		FileInputStream is = null;
		try {
			String pk_corp = corpVo.getPk_corp();
			YntCpaccountVO accountVo = (YntCpaccountVO) singleObjectBO
					.queryByPrimaryKey(YntCpaccountVO.class, pk_accsubj);
			String isfzhs = accountVo.getIsfzhs();
			if (isfzhs == null) {
				isfzhs = AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT;
			}
			int fzlbIndex = isfzhs.indexOf("1");
			if (fzlbIndex == -1 || fzlbIndex == 2
					|| fzlbIndex != isfzhs.lastIndexOf("1")) {
				throw new BusinessException("该科目暂不支持导入辅助期初");
			}
			
			is = new FileInputStream(file);
			HSSFWorkbook workbook = new HSSFWorkbook(is);
			HSSFSheet sheet = workbook.getSheetAt(0);

			AuxiliaryAccountHVO fzlb = queryFzlbByCode(pk_corp, fzlbIndex + 1);
			String pk_auacount_h = fzlb.getPk_auacount_h();
			
			// 大类核算
			boolean isInvCategory = false;
			// 库存模块存货
			boolean isInvMode = false;
			// 是否存货辅助
			boolean isInvAssit = AuxiliaryConstant.ITEM_INVENTORY.equals(pk_auacount_h);
			if (isInvAssit) {
				if (IcCostStyle.IC_ON.equals(corpVo.getBbuildic())) {
					// 启用库存暂不支持自动新增
//					throw new BusinessException("存货不存在，请在存货节点增加后再导入");
					isInvMode = true;
				}
				InventorySetVO invSet = gl_ic_invtorysetserv.query(pk_corp);
				if (invSet!= null && invSet.getChcbjzfs() == 1
						&& Kmschema.isKmclassify(pk_corp, corpVo.getCorptype(), accountVo.getPk_corp_account())) {
					isInvCategory = true;
//					YntCpaccountVO[] accounts = AccountCache.getInstance().get(userId, pk_corp);
//					Map<String,YntCpaccountVO> kmmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accounts),
//							new String[]{"accountcode"});
//					String ckKm = Kmschema.getKmid(corpVo.getCorptype(), Kmschema.style_shouru, kmmap);
//					for (AuxiliaryAccountBVO fzVo : newFzVos) {
//						fzVo.setKmclassify(accountVo.getPk_corp_account());
//						fzVo.setChukukmid(ckKm);
//					}
				}
			}
			
			AuxiliaryAccountBVO[] fzvos = gl_fzhsserv.queryB(pk_auacount_h,
					pk_corp, accountVo.getPk_corp_account());
			Map<String, AuxiliaryAccountBVO> fzMap = new HashMap<String, AuxiliaryAccountBVO>();
			if (fzvos != null) {
				for (AuxiliaryAccountBVO auxiliaryAccountBVO : fzvos) {
					fzMap.put(auxiliaryAccountBVO.getCode(), auxiliaryAccountBVO);
				}
			}
			Map<String, AuxiliaryAccountBVO> allFzMap = null;
			if (isInvCategory) {
				// 所有存货
				AuxiliaryAccountBVO[] allVos = gl_fzhsserv.queryB(pk_auacount_h,
						pk_corp, null);
				allFzMap = new HashMap<String, AuxiliaryAccountBVO>();
				if (allVos != null) {
					for (AuxiliaryAccountBVO auxiliaryAccountBVO : allVos) {
						allFzMap.put(auxiliaryAccountBVO.getCode(), auxiliaryAccountBVO);
					}
				}
			}
			
			String rmb = IGlobalConstants.RMB_currency_id;

			Map<String, Integer> headIndex = new HashMap<String, Integer>();
			Row headRow = sheet.getRow(0);
			for (int i = 0; i < 35; i++) {
				Cell cell = headRow.getCell(i);
				if (cell == null) {
					break;
				}
				String val = cell.getRichStringCellValue().getString();
				val = val.trim();
				headIndex.put(val, i);
			}
			// 数量精度
			int numPrecision = Integer.valueOf(sys_parameteract
					.queryParamterValueByCode(pk_corp, "dzf009"));

			// 需要新增的辅助核算
			List<AuxiliaryAccountBVO> newFzVos = new ArrayList<AuxiliaryAccountBVO>();
			// 辅助期初
			List<FzhsqcVO> fzQcVos = new ArrayList<FzhsqcVO>();
			// 需要新增辅助核算的期初
			Map<String, FzhsqcVO> newFzQcMap = new HashMap<String, FzhsqcVO>();

			int rowNum = sheet.getLastRowNum();
			for (int rowIndex = 1; rowIndex <= rowNum; rowIndex++) {
				Row row = sheet.getRow(rowIndex);
				if (row == null)
					continue;
				String fzCode = ReportUtil.getExcelCellValue(
						row.getCell(getHeadIndex(headIndex, "编码")), false);
				String fzName = ReportUtil.getExcelCellValue(
						row.getCell(getHeadIndex(headIndex, "名称")), false);
				if (StringUtil.isEmpty(fzCode)) {
					if (StringUtil.isEmpty(fzName)) {
						continue;
					} else {
						throw new BusinessException("第" + (rowIndex + 1) + "行编码未填写，请检查");
					}
				}
				if (StringUtil.isEmpty(fzName)) {
					throw new BusinessException("第" + (rowIndex + 1) + "行名称未填写，请检查");
				}
//				int splitIndex = fullCode.indexOf("_");
//				int lastSplitIndex = fullCode.lastIndexOf("_");
//				if (splitIndex == -1 || splitIndex != lastSplitIndex
//						|| lastSplitIndex == fullCode.length() - 1)
//					throw new BusinessException("第" + (rowIndex + 1) + "行编码格式错误，请检查");
//				// 辅助核算编码
//				String fzCode = fullCode
//						.substring(lastSplitIndex + 1);
//				
//				splitIndex = fullName.indexOf("_");
//				lastSplitIndex = fullName.lastIndexOf("_");
//				if (splitIndex == -1 || splitIndex != lastSplitIndex
//						|| lastSplitIndex == fullName.length() - 1)
//					throw new BusinessException("第" + (rowIndex + 1) + "行名称格式错误，请检查");
//				// 辅助核算名称
//				String fzName = fullName
//						.substring(lastSplitIndex + 1);
				
				FzhsqcVO qcvo = new FzhsqcVO();

				DZFBoolean accIsNum = accountVo.getIsnum();
				boolean isnum = accIsNum != null && accIsNum.booleanValue() ? true
						: false;
				if (isnum) {
					if (headIndex.containsKey("本年期初数量")) {
						String bnqcnum = ReportUtil.getExcelCellValue(
								row.getCell(headIndex.get("本年期初数量")), true);
						if (!StringUtil.isEmpty(bnqcnum)) {
							qcvo.setBnqcnum(new DZFDouble(bnqcnum, numPrecision));
						}
					}
					if (headIndex.containsKey("本年借方发生数量")) {
						String bnfsnum = ReportUtil.getExcelCellValue(
								row.getCell(headIndex.get("本年借方发生数量")), true);
						if (!StringUtil.isEmpty(bnfsnum)) {
							qcvo.setBnfsnum(new DZFDouble(bnfsnum, numPrecision));
						}
					}
					if (headIndex.containsKey("本年贷方发生数量")) {
						String bndffsnum = ReportUtil.getExcelCellValue(
								row.getCell(headIndex.get("本年贷方发生数量")), true);
						if (!StringUtil.isEmpty(bndffsnum)) {
							qcvo.setBndffsnum(new DZFDouble(bndffsnum, numPrecision));
						}
					}
					if (headIndex.containsKey("本月期初数量")) {
						String monthqmnum = ReportUtil.getExcelCellValue(
								row.getCell(headIndex.get("本月期初数量")), true);
						if (!StringUtil.isEmpty(monthqmnum)) {
							qcvo.setMonthqmnum(new DZFDouble(monthqmnum,
									numPrecision));
						}
					}
				}
				
				if (headIndex.containsKey("本年期初金额")) {
					String yearqc = ReportUtil.getExcelCellValue(
							row.getCell(headIndex.get("本年期初金额")), true);
					if (!StringUtil.isEmpty(yearqc)) {
						qcvo.setYearqc(new DZFDouble(yearqc, 2));
					}
				}

				if (headIndex.containsKey("本年借方发生金额")) {
					String yearjffse = ReportUtil.getExcelCellValue(
							row.getCell(headIndex.get("本年借方发生金额")), true);
					if (!StringUtil.isEmpty(yearjffse)) {
						qcvo.setYearjffse(new DZFDouble(yearjffse, 2));
					}
				}
				if (headIndex.containsKey("本年贷方发生金额")) {
					String yeardffse = ReportUtil.getExcelCellValue(
							row.getCell(headIndex.get("本年贷方发生金额")), true);
					if (!StringUtil.isEmpty(yeardffse)) {
						qcvo.setYeardffse(new DZFDouble(yeardffse, 2));
					}
				}

				if (jzdate.getMonth() == 1
						&& (qcvo.getYearjffse() != null
								&& !qcvo.getYearjffse().equals(
										DZFDouble.ZERO_DBL) || qcvo
								.getYeardffse() != null
								&& !qcvo.getYeardffse().equals(
										DZFDouble.ZERO_DBL))) {
					throw new BusinessException("期初建账，不允许录入发生额，请检查");
				}

				if (headIndex.containsKey("本月期初金额")) {
					String thismonthqc = ReportUtil.getExcelCellValue(
							row.getCell(headIndex.get("本月期初金额")), true);
					if (!StringUtil.isEmpty(thismonthqc)) {
						qcvo.setThismonthqc(new DZFDouble(thismonthqc, 2));
					}
				}
				fzQcVos.add(qcvo);
				if (fzMap.containsKey(fzCode)) {
					qcvo.setAttributeValue("fzhsx" + (fzlbIndex + 1),
							fzMap.get(fzCode).getPk_auacount_b());
				} else {
					if (isInvMode) {
						throw new BusinessException("存货（" + fzCode + " "
								+ fzName + "）不存在，请在存货节点增加后重试");
					} else if (isInvCategory && allFzMap != null
							&& allFzMap.containsKey(fzCode)) {
						StringBuilder tips = new StringBuilder();
						tips.append("存货（").append(fzCode).append(" ")
							.append(fzName).append("）")
							.append("不属于存货大类（").append(accountVo.getAccountcode())
							.append(" ").append(accountVo.getAccountname())
							.append("），请检查");
						throw new BusinessException(tips.toString());
					}
					// 辅助核算不存在，需要新增
					AuxiliaryAccountBVO fzVo = new AuxiliaryAccountBVO();
					fzVo.setCode(fzCode);
					fzVo.setName(fzName);
					fzVo.setPk_corp(pk_corp);
					fzVo.setPk_auacount_h(pk_auacount_h);
					if (isInvAssit) {
						if (headIndex.containsKey("规格(型号)")) {
							String spec = ReportUtil
									.getExcelCellValue(row.getCell(headIndex
											.get("规格(型号)")), false);
							if (!StringUtil.isEmpty(spec)) {
								fzVo.setSpec(spec);
							}
						}
						if (headIndex.containsKey("计量单位")) {
							String unit = ReportUtil.getExcelCellValue(
									row.getCell(headIndex.get("计量单位")), false);
							if (!StringUtil.isEmpty(unit)) {
								fzVo.setUnit(unit);
							}
						}
					}
					newFzVos.add(fzVo);
					newFzQcMap.put(fzCode, qcvo);
				}
			}

			// 关闭流
			is.close();

			for (FzhsqcVO fzhsqcVO : fzQcVos) {
				fzhsqcVO.setDoperatedate(jzdate);
				fzhsqcVO.setPk_currency(IGlobalConstants.RMB_currency_id);// 币种
				fzhsqcVO.setDr(0);
				fzhsqcVO.setCoperatorid(userId);
				if (jzdate != null) {
					fzhsqcVO.setVyear(Integer.valueOf(jzdate.toString()
							.substring(0, 4)));
					fzhsqcVO.setPeriod(jzdate.toString().substring(0, 7));
				}
			}

			if (fzQcVos.size() == 0) {
				throw new BusinessException("没有可导入数据");
			}
			
			QcYeVO ancestor = getAncestorByAccount(pk_corp,
					accountVo.getAccountcode(), fzQcVos);

			if (newFzVos.size() > 0) {
				Set<String> codeSet = new HashSet<>();
				Set<String> nameSet = new HashSet<>();
				for (AuxiliaryAccountBVO bvo: newFzVos) {
					String nameKey;
					if (isInvAssit) {
						StringBuilder sb = new StringBuilder();
						sb.append(bvo.getName());
						sb.append(bvo.getSpec());
						sb.append(bvo.getInvtype());
						sb.append(bvo.getUnit());
						nameKey = sb.toString();
					} else {
						nameKey = bvo.getName();
					}
					if (codeSet.contains(bvo.getCode()) || nameSet.contains(nameKey)) {
						throw new BusinessException("导入文件编码" + bvo.getCode()
								+ "，名称" + bvo.getName() + "存在重复数据，请检查");
					} else {
						codeSet.add(bvo.getCode());
						nameSet.add(nameKey);
					}
				}
				if (isInvCategory) {
					YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
					Map<String,YntCpaccountVO> kmmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(accounts),
							new String[]{"accountcode"});
					String ckKm = Kmschema.getKmid(corpVo.getCorptype(), Kmschema.style_shouru, kmmap);
					for (AuxiliaryAccountBVO fzVo : newFzVos) {
						fzVo.setKmclassify(accountVo.getPk_corp_account());
						fzVo.setChukukmid(ckKm);
					}
				}

				// 新增辅助核算
				gl_fzhsserv.saveBs(newFzVos, true);
				for (AuxiliaryAccountBVO fzVo : newFzVos) {
					// 新增的辅助核算主键赋值
					newFzQcMap.get(fzVo.getCode()).setAttributeValue("fzhsx" + (fzlbIndex + 1),
							fzVo.getPk_auacount_b());
				}
			}
			// 删除未核销期初
			String sql = " delete from ynt_verify_begin where pk_corp = ? and pk_accsubj = ? ";
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk_accsubj);
			singleObjectBO.executeUpdate(sql, sp);
			// 保存辅助期初
			gl_fzhsqcserv.saveFzQc(pk_accsubj,
					fzQcVos.toArray(new FzhsqcVO[0]), rmb, ancestor,
					userServiceImpl.queryUserJmVOByID(userId), corpService.queryByPk(pk_corp));
		} catch (FileNotFoundException e) {
			throw new BusinessException("未找到导入文件");
		} catch (IOException e) {
			throw new BusinessException("导入文件格式错误");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	/**
	 * 
	 * @param pk_corp
	 * @param accountCode
	 * @return
	 */
	private QcYeVO getAncestorByAccount(String pk_corp, String accountCode, List<FzhsqcVO> fzQcVos) {
		String ancestorCode = accountCode.substring(0,4);
		List<QcYeVO> list = queryAllQcInfo(pk_corp, IGlobalConstants.RMB_currency_id,
				IGlobalConstants.RMB_currency_id);
		QcYeVO curQc = null;
		Iterator<QcYeVO> it = list.iterator();
		while (it.hasNext()) {
			QcYeVO vo = it.next();
			if (accountCode.equals(vo.getVcode())) {
				curQc = vo;
			}
			if (!vo.getVcode().startsWith(ancestorCode)) {
				it.remove();
			}
		}
		curQc.setYearqc(DZFDouble.ZERO_DBL);
		curQc.setYearjffse(DZFDouble.ZERO_DBL);
		curQc.setYeardffse(DZFDouble.ZERO_DBL);
		curQc.setBnqcnum(DZFDouble.ZERO_DBL);
		curQc.setBndffsnum(DZFDouble.ZERO_DBL);
		curQc.setBnfsnum(DZFDouble.ZERO_DBL);
		for (FzhsqcVO fzqcvo : fzQcVos) {
			if (fzqcvo.getYearqc() != null) {
				curQc.setYearqc(SafeCompute.add(curQc.getYearqc(), fzqcvo.getYearqc()));
			}
			if (fzqcvo.getYearjffse() != null) {
				curQc.setYearjffse(SafeCompute.add(curQc.getYearjffse(), fzqcvo.getYearjffse()));
			}
			if (fzqcvo.getYeardffse() != null) {
				curQc.setYeardffse(SafeCompute.add(curQc.getYeardffse(), fzqcvo.getYeardffse()));
			}
			if (fzqcvo.getBnqcnum() != null) {
				curQc.setBnqcnum(SafeCompute.add(curQc.getBnqcnum(), fzqcvo.getBnqcnum()));
			}
			if (fzqcvo.getBndffsnum() != null) {
				curQc.setBndffsnum(SafeCompute.add(curQc.getBndffsnum(), fzqcvo.getBndffsnum()));
			}
			if (fzqcvo.getBnfsnum() != null) {
				curQc.setBnfsnum(SafeCompute.add(curQc.getBnfsnum(), fzqcvo.getBnfsnum()));
			}
		}
		QcYeVO[] qcvos = doIteratorVO(list, pk_corp, null);
		QcYeVO ancestor = null;
		if (qcvos.length == 1) {
			ancestor = qcvos[0];
		}
		return ancestor;
	}

	private AuxiliaryAccountHVO queryFzlbByCode(String pk_corp, int code) {
		String condition = " (pk_corp = ? or pk_corp = ?) and code = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(code);
		AuxiliaryAccountHVO[] results = (AuxiliaryAccountHVO[]) singleObjectBO
				.queryByCondition(AuxiliaryAccountHVO.class, condition, sp);
		AuxiliaryAccountHVO hvo = null;
		if (results != null && results.length > 0) {
			hvo = results[0];
		}
		return hvo;
	}

	@Override
	public byte[] exportFzExcel(String pk_corp, String pk_accsubj,
			String tempPath, boolean showQuantity) throws DZFWarpException {
		byte[] byteArray = null;
		// FileInputStream is = null;

		// is = new FileInputStream(tempPath);
		YntCpaccountVO accountVo = (YntCpaccountVO) singleObjectBO
				.queryByPrimaryKey(YntCpaccountVO.class, pk_accsubj);
		Workbook workbook = new HSSFWorkbook();
		Sheet sheet = workbook.createSheet("辅助期初导入("
				+ accountVo.getAccountcode() + ")");

		Map<String, Integer> headIndex = createFzSheetHead(workbook, sheet,
				pk_corp, accountVo);

		FzhsqcVO[] fzqcVo = gl_fzhsqcserv.queryFzQc(pk_corp, pk_accsubj);
		if (fzqcVo == null || fzqcVo.length == 0) {
			fzqcVo = new FzhsqcVO[2];
			FzhsqcVO temp1 = new FzhsqcVO();
			fzqcVo[0] = temp1;
			temp1.setVcode("1001_001");
			temp1.setVname("名称_名称1");
			temp1.setYearqc(new DZFDouble(100));
			temp1.setThismonthqc(new DZFDouble(100));
			temp1.setBnqcnum(new DZFDouble(20));
			temp1.setMonthqmnum(new DZFDouble(20));
			FzhsqcVO temp2 = new FzhsqcVO();
			fzqcVo[1] = temp2;
			temp2.setVcode("1001_002");
			temp2.setVname("名称_名称2");
			temp2.setYearqc(new DZFDouble(200));
			temp2.setThismonthqc(new DZFDouble(200));
			temp2.setBnqcnum(new DZFDouble(50));
			temp2.setMonthqmnum(new DZFDouble(50));
		}
		int rowNum = 1;
		for (FzhsqcVO fzhsqcVO : fzqcVo) {
			Row row = sheet.createRow(rowNum++);
			if (headIndex.containsKey("编码")) {
				Cell cell = row.createCell(headIndex.get("编码"));
				cell.setCellValue(fzhsqcVO.getVcode().substring(
						fzhsqcVO.getVcode().indexOf("_") + 1));
			}
			if (headIndex.containsKey("名称")) {
				Cell cell = row.createCell(headIndex.get("名称"));
				cell.setCellValue(fzhsqcVO.getVname().substring(
						fzhsqcVO.getVname().indexOf("_") + 1));
			}
			if (headIndex.containsKey("规格(型号)")) {
				Cell cell = row.createCell(headIndex.get("规格(型号)"));
				cell.setCellValue(fzhsqcVO.getSpec());
			}
			if (headIndex.containsKey("计量单位")) {
				Cell cell = row.createCell(headIndex.get("计量单位"));
				cell.setCellValue(fzhsqcVO.getJldw());
			}
			if (headIndex.containsKey("本年期初数量")) {
				Cell cell = row.createCell(headIndex.get("本年期初数量"));
				if (fzhsqcVO.getBnqcnum() != null
						&& fzhsqcVO.getBnqcnum().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getBnqcnum().doubleValue());
				}
			}
			if (headIndex.containsKey("本年期初金额")) {
				Cell cell = row.createCell(headIndex.get("本年期初金额"));
				if (fzhsqcVO.getYearqc() != null
						&& fzhsqcVO.getYearqc().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getYearqc().doubleValue());
				}
			}
			if (headIndex.containsKey("本年借方发生数量")) {
				Cell cell = row.createCell(headIndex.get("本年借方发生数量"));
				if (fzhsqcVO.getBnfsnum() != null
						&& fzhsqcVO.getBnfsnum().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getBnfsnum().doubleValue());
				}
			}
			if (headIndex.containsKey("本年借方发生金额")) {
				Cell cell = row.createCell(headIndex.get("本年借方发生金额"));
				if (fzhsqcVO.getYearjffse() != null
						&& fzhsqcVO.getYearjffse().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getYearjffse().doubleValue());
				}
			}
			if (headIndex.containsKey("本年贷方发生数量")) {
				Cell cell = row.createCell(headIndex.get("本年贷方发生数量"));
				if (fzhsqcVO.getBndffsnum() != null
						&& fzhsqcVO.getBndffsnum().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getBndffsnum().doubleValue());
				}
			}
			if (headIndex.containsKey("本年贷方发生金额")) {
				Cell cell = row.createCell(headIndex.get("本年贷方发生金额"));
				if (fzhsqcVO.getYeardffse() != null
						&& fzhsqcVO.getYeardffse().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getYeardffse().doubleValue());
				}
			}
			if (headIndex.containsKey("本月期初数量")) {
				Cell cell = row.createCell(headIndex.get("本月期初数量"));
				if (fzhsqcVO.getMonthqmnum() != null
						&& fzhsqcVO.getMonthqmnum().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getMonthqmnum().doubleValue());
				}
			}
			if (headIndex.containsKey("本月期初金额")) {
				Cell cell = row.createCell(headIndex.get("本月期初金额"));
				if (fzhsqcVO.getThismonthqc() != null
						&& fzhsqcVO.getThismonthqc().doubleValue() != 0) {
					cell.setCellValue(fzhsqcVO.getThismonthqc().doubleValue());
				}
			}
		}
		ByteArrayOutputStream bao = null;
		try {
			bao = new ByteArrayOutputStream();
			workbook.write(bao);
			byteArray = bao.toByteArray();
			bao.close();
		} catch (IOException e) {

		} finally {
			if (bao != null) {
				try {
					bao.close();
				} catch (IOException e) {

				}
			}
		}

		return byteArray;
	}

	private Map<String, Integer> createFzSheetHead(Workbook workbook,
                                                   Sheet sheet, String pk_corp, YntCpaccountVO account) {
		Map<String, Integer> headMap = new HashMap<String, Integer>();

		Font font = workbook.createFont();
		font.setBold(false);

		CellStyle headStyle = workbook.createCellStyle();
		headStyle.setFont(font);
		headStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
		headStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

		Font redFont = workbook.createFont();
		redFont.setColor(Font.COLOR_RED);
		redFont.setBold(false);
		CellStyle redFontStyle = workbook.createCellStyle();
		redFontStyle.cloneStyleFrom(headStyle);
		redFontStyle.setFont(redFont);

		DataFormat df = workbook.createDataFormat();

		CellStyle numberCell2 = workbook.createCellStyle();
		numberCell2.setFont(font);
		numberCell2.setAlignment(HorizontalAlignment.CENTER);
		numberCell2.setVerticalAlignment(VerticalAlignment.CENTER);
		numberCell2.setDataFormat(df.getFormat("#,##0.00")); // 保留小数点后2位

		CellStyle textCellStyle = workbook.createCellStyle();
		textCellStyle.setDataFormat(df.getFormat("@"));

		int numPrecision = Integer.valueOf(sys_parameteract
				.queryParamterValueByCode(pk_corp, "dzf009"));
		CellStyle numberCell4 = workbook.createCellStyle();
		numberCell4.cloneStyleFrom(numberCell2);
		numberCell4.setDataFormat(df.getFormat("#,##0.00000000".substring(0,
				6 + numPrecision)));

		// 显示数量
		boolean showQuantity = account.getIsnum() != null
				&& account.getIsnum().booleanValue() ? true : false;
		// 存货辅助核算
		boolean isInve = account.getIsfzhs().charAt(5) == '1' ? true : false;
		Row row = sheet.createRow(0);
		row.setHeightInPoints(20);

		int colIndex = 0;
		Cell cell = row.createCell(colIndex);
		cell.setCellValue("编码");
		cell.setCellStyle(redFontStyle);
		headMap.put("编码", colIndex);
		sheet.setDefaultColumnStyle(colIndex, textCellStyle);
		sheet.setColumnWidth(colIndex, 17 * 256);

		colIndex++;
		cell = row.createCell(colIndex);
		cell.setCellValue("名称");
		cell.setCellStyle(redFontStyle);
		headMap.put("名称", colIndex);
		sheet.setDefaultColumnStyle(colIndex, textCellStyle);
		sheet.setColumnWidth(colIndex, 17 * 256);

		if (isInve) {
			colIndex++;
			cell = row.createCell(colIndex);
			cell.setCellValue("规格(型号)");
			cell.setCellStyle(headStyle);
			headMap.put("规格(型号)", colIndex);
			sheet.setDefaultColumnStyle(colIndex, textCellStyle);
			sheet.setColumnWidth(colIndex, 17 * 256);
		}

		if (showQuantity) {
			colIndex++;
			cell = row.createCell(colIndex);
			cell.setCellValue("计量单位");
			cell.setCellStyle(headStyle);
			headMap.put("计量单位", colIndex);
			sheet.setDefaultColumnStyle(colIndex, textCellStyle);
			sheet.setColumnWidth(colIndex, 17 * 256);

			colIndex++;
			cell = row.createCell(colIndex);
			cell.setCellValue("本年期初数量");
			cell.setCellStyle(headStyle);
			headMap.put("本年期初数量", colIndex);
			sheet.setDefaultColumnStyle(colIndex, numberCell4);
			sheet.setColumnWidth(colIndex, 17 * 256);
		}
		colIndex++;
		cell = row.createCell(colIndex);
		cell.setCellValue("本年期初金额");
		cell.setCellStyle(headStyle);
		headMap.put("本年期初金额", colIndex);
		sheet.setDefaultColumnStyle(colIndex, numberCell2);
		sheet.setColumnWidth(colIndex, 17 * 256);

		if (showQuantity) {
			colIndex++;
			cell = row.createCell(colIndex);
			cell.setCellValue("本年借方发生数量");
			cell.setCellStyle(headStyle);
			headMap.put("本年借方发生数量", colIndex);
			sheet.setDefaultColumnStyle(colIndex, numberCell4);
			sheet.setColumnWidth(colIndex, 17 * 256);
		}

		colIndex++;
		cell = row.createCell(colIndex);
		cell.setCellValue("本年借方发生金额");
		cell.setCellStyle(headStyle);
		headMap.put("本年借方发生金额", colIndex);
		sheet.setDefaultColumnStyle(colIndex, numberCell2);
		sheet.setColumnWidth(colIndex, 17 * 256);

		if (showQuantity) {
			colIndex++;
			cell = row.createCell(colIndex);
			cell.setCellValue("本年贷方发生数量");
			cell.setCellStyle(headStyle);
			headMap.put("本年贷方发生数量", colIndex);
			sheet.setDefaultColumnStyle(colIndex, numberCell4);
			sheet.setColumnWidth(colIndex, 17 * 256);
		}

		colIndex++;
		cell = row.createCell(colIndex);
		cell.setCellValue("本年贷方发生金额");
		cell.setCellStyle(headStyle);
		headMap.put("本年贷方发生金额", colIndex);
		sheet.setDefaultColumnStyle(colIndex, numberCell2);
		sheet.setColumnWidth(colIndex, 17 * 256);

		if (showQuantity) {
			colIndex++;
			cell = row.createCell(colIndex);
			cell.setCellValue("本月期初数量");
			cell.setCellStyle(headStyle);
			headMap.put("本月期初数量", colIndex);
			sheet.setDefaultColumnStyle(colIndex, numberCell4);
			sheet.setColumnWidth(colIndex, 17 * 256);
		}
		colIndex++;
		cell = row.createCell(colIndex);
		cell.setCellValue("本月期初金额");
		cell.setCellStyle(headStyle);
		headMap.put("本月期初金额", colIndex);
		sheet.setDefaultColumnStyle(colIndex, numberCell2);
		sheet.setColumnWidth(colIndex, 17 * 256);

		return headMap;
	}

	private int getHeadIndex(Map<String, Integer> headMap, String headName) {
		if (headMap.containsKey(headName)) {
			return headMap.get(headName);
		} else {
			throw new BusinessException(headName + "列不存在，请检查模板！");
		}
	}
}

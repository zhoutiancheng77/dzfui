package com.dzf.zxkj.platform.service.icset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.enums.IcBillTypeEnum;
import com.dzf.zxkj.common.enums.IcPayWayEnum;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.exception.IcExBusinessException;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdTradeCostTransferVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.icset.*;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pjgl.IVATSaleInvoiceService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.pzgl.impl.CaclTaxMny;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("ic_saleoutserv")
public class SaleoutServiceImpl implements ISaleoutService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private IInvAccSetService ic_chkmszserv;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired
	private IInventoryService ic_invserv;
	@Autowired
	private IInvclassifyService splbService;
	@Autowired
	private IMeasureService jldwService;
	@Autowired
	private ICbComconstant gl_cbconstant;
	@Autowired
	private IYHZHService gl_yhzhserv;

	@Autowired
	IAuxiliaryAccountService gl_fzhsserv;

	@Autowired(required = false)
	private IVATSaleInvoiceService gl_vatsalinvserv;
	@Autowired
	IReferenceCheck refcheck;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IImageGroupService img_groupserv;

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IAccountService accountService;

	@Override
	public List<IntradeHVO> query(IntradeParamVO paramvo) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" Select y.* ,fb.name custname ");
		sf.append("   From ynt_ictrade_h y ");
		sf.append("   left join ynt_fzhs_b fb ");
		sf.append("     on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ? ");
		sf.append("  Where y.pk_corp = ? ");
		sf.append("    and nvl(y.dr, 0) = 0 ");
		sf.append("    and cbilltype = ? ");
		sp.addParam(AuxiliaryConstant.ITEM_CUSTOMER);// 客户
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(IBillTypeCode.HP75);

		addCondition(sf, paramvo, sp);

		List<IntradeHVO> listVO = (List<IntradeHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IntradeHVO.class));
		IntradeHVO[] vos = null;
		if (listVO != null && listVO.size() > 0) {

			Set<String> set = new HashSet<>();

			for (IntradeHVO hvo : listVO) {
				set.add(hvo.getPk_ictrade_h());
			}
			sp.clearParams();
			sp.addParam(listVO.get(0).getPk_corp());
			String idwhere = SqlUtil.buildSqlForIn(" pk_ictrade_h ", set.toArray(new String[set.size()]));
			IntradeoutVO[] bvos = (IntradeoutVO[]) singleObjectBO.queryByCondition(IntradeoutVO.class,
					" nvl(dr,0) = 0 and pk_corp = ? and " + idwhere, sp);

			if (bvos != null && bvos.length > 0) {
				List<IntradeoutVO> list = null;
				Map<String, List<IntradeoutVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bvos),
						new String[] { "pk_ictrade_h" });
				for (IntradeHVO hvo : listVO) {
					DZFDouble nnum = DZFDouble.ZERO_DBL; // 数量
					DZFDouble ncost = DZFDouble.ZERO_DBL; // 成本金额
					sp.clearParams();
					sp.addParam(hvo.getPk_corp());
					sp.addParam(hvo.getPrimaryKey());

					list = map.get(hvo.getPrimaryKey());
					if (list != null && list.size() > 0) {
						for (SuperVO child : list) {
							IntradeoutVO invo = (IntradeoutVO) child;
							nnum = SafeCompute.add(nnum, invo.getNnum());
							ncost = SafeCompute.add(ncost, invo.getNcost());
						}
						hvo.setVdef2(SafeCompute.div(ncost, nnum));
						hvo.setVdef3(ncost);
						hvo.setVdef4(SafeCompute.div(hvo.getNmny(), nnum));
						hvo.setVdef5(nnum);
					}
				}
			}

			vos = listVO.toArray(new IntradeHVO[listVO.size()]);
			VOUtil.ascSort(vos, new String[] { "dbilldate", "dbillid" });
		}
		return vos == null || vos.length == 0 ? null : Arrays.asList(vos);
	}

	private void addCondition(StringBuffer buffer, IntradeParamVO paramvo, SQLParameter sp) {

		if (paramvo.getBegindate() != null) {
			buffer.append(" and dbilldate >= ?");
			sp.addParam(paramvo.getBegindate());

		}

		if (StringUtils.isNotEmpty(paramvo.getQinvid())) {
			buffer.append(
					"and exists (select 1 from ynt_ictradeout b where y.PK_ICTRADE_H = b.PK_ICTRADE_H and b.pk_inventory = ?)");
			sp.addParam(paramvo.getQinvid());
		}

		if (paramvo.getEnddate() != null) {
			buffer.append(" and  dbilldate <= ?");
			sp.addParam(paramvo.getEnddate());
		}
		if (!StringUtil.isEmpty(paramvo.getCbusitype())) {
			buffer.append(" and  nvl(cbusitype,'" + IcConst.XSTYPE + "')  = ? ");
			sp.addParam(paramvo.getCbusitype());
		}

		if (!StringUtil.isEmpty(paramvo.getQcorpid())) {
			buffer.append(" and  pk_cust = ?");
			sp.addParam(paramvo.getQcorpid());
		} else {
			if (!StringUtil.isEmpty(paramvo.getQcorpname())) {
				buffer.append(" and code like ? ");
				sp.addParam("%" + paramvo.getQcorpname() + "%");
			}
		}

		if (paramvo.getMny1() != null) {
			buffer.append(" and  nvl(nmny,0) >= ?");
			sp.addParam(paramvo.getMny1());
		}

		if (paramvo.getMny2() != null) {
			buffer.append(" and  nvl(nmny,0) <= ?");
			sp.addParam(paramvo.getMny2());
		}

		addDbillidWhere(buffer, paramvo, sp);
	}

	private void addDbillidWhere(StringBuffer buffer, IntradeParamVO paramvo, SQLParameter sp) {

		if (!StringUtil.isEmpty(paramvo.getDjh1()) && !StringUtil.isEmpty(paramvo.getDjh2())) {
			String djh1 = getdjh(paramvo.getDjh1());
			String djh2 = getdjh(paramvo.getDjh2());
			int len1 = djh1.length();
			int len2 = djh2.length();
			if (len1 < 5 && len2 < 5) {
				buffer.append(" and substr(dbillid,10,4) between ? and ? ");
				sp.addParam(djh1);
				sp.addParam(djh2);
			} else {
				buffer.append(" and dbillid between ? and ? ");
				sp.addParam(djh1);
				sp.addParam(djh2);
			}

		} else if (!StringUtil.isEmpty(paramvo.getDjh1())) {
			String djh1 = getdjh(paramvo.getDjh1());
			int len1 = djh1.length();
			if (len1 < 5) {
				buffer.append(" and substr(dbillid,10,4) >= ? ");
				sp.addParam(djh1);
			} else {
				buffer.append(" and dbillid >= ?");
				sp.addParam(djh1);
			}
		} else if (!StringUtil.isEmpty(paramvo.getDjh2())) {
			String djh2 = getdjh(paramvo.getDjh2());
			int len2 = djh2.length();
			if (len2 < 5) {
				buffer.append(" and substr(dbillid,10,4) <= ? ");
				sp.addParam(djh2);
			} else {
				buffer.append(" and dbillid <= ?");
				sp.addParam(djh2);
			}
		}

	}

	private String getdjh(String djh) {

		if (StringUtil.isEmpty(djh)) {
			return null;
		}
		try {
			Integer.parseInt(djh);
		} catch (Exception e) {
			return djh;
		}

		djh = addZeroForNum(djh, 4);

		return djh;
	}

	private String getdjh(String djh, DZFDate ddate) {

		if (StringUtil.isEmpty(djh) || ddate == null) {
			return null;
		}

		Object month = ddate.getMonth() >= 10 ? ((Object) (Integer.valueOf(ddate.getMonth())))
				: ((Object) ((new StringBuilder("0")).append(ddate.getMonth()).toString()));

		String prefix = "XS-" + ddate.getYear() + month;
		int len = djh.length();
		if (len > 4) {
			return prefix + djh;
		}

		try {
			Integer.parseInt(djh);
		} catch (Exception e) {
			return prefix + djh;
		}

		djh = addZeroForNum(djh, 4);

		return prefix + djh;
	}

	private String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				// sb.append(str).append("0");//右补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}

	@Override
	public IntradeHVO saveSale(IntradeHVO headvo, boolean ischeckfull, boolean isImpl) throws DZFWarpException {

		String speriod = DateUtils.getPeriod(headvo.getDbilldate());
		SQLParameter sp = new SQLParameter();
		String qmclsqlwhere = "select * from YNT_QMCL  where nvl(dr,0)=0 and pk_corp  = ? and period =  ? ";
		sp.addParam(headvo.getPk_corp());
		sp.addParam(speriod);
		List<QmclVO> qmcllist = (List<QmclVO>) singleObjectBO.executeQuery(qmclsqlwhere, sp,
				new BeanListProcessor(QmclVO.class));
		if (qmcllist != null && qmcllist.size() > 0) {
			QmclVO clvo = qmcllist.get(0);
			CorpVO corpvo = corpService.queryByPk(headvo.getPk_corp());

			if (corpvo == null) {
				throw new BusinessException("公司主键为" + headvo.getPk_corp() + "的公司已被删除!");
			}

			boolean isgz = qmgzService.isGz(headvo.getPk_corp(), speriod);
			if (isgz) {// 是否关账
				throw new BusinessException("公司" + corpvo.getUnitname() + speriod + "月份已关账,不允许保存!");
			}

			// if (clvo.getIscbjz() != null && clvo.getIscbjz().booleanValue())
			// {
			// throw new BusinessException("本期已经成本结转,不允许保存单据!");
			// }
			//
			// if (clvo.getIsqjsyjz() != null &&
			// clvo.getIsqjsyjz().booleanValue()) {
			// throw new BusinessException("已损益结转,不允许保存单据!");
			// }
		}

		// 校验发票号码是否唯一
		checkInvidCodeIsUnique(headvo);

		checkHead(headvo, headvo.getPk_corp());
		StringBuffer strb = new StringBuffer();
		checkBodys((IntradeoutVO[]) headvo.getChildren(), headvo, strb);
		if (strb.length() > 0) {
			throw new BusinessException(strb.toString());
		}
		IntradeHVO ret = null;
		String pk_corp = headvo.getPk_corp();
		if (StringUtil.isEmpty(headvo.getDbillid())) {
			String code = this.getNewBillNo(pk_corp, headvo.getDbilldate(), headvo.getCbusitype());
			headvo.setDbillid(code);
		} else {

			if (StringUtil.isEmpty(headvo.getPk_ictrade_h())) {
				if (!isImpl) {
					String prefix = getPrefix(headvo.getCbusitype());
					prefix = prefix + "-" + headvo.getDbilldate().getYear() + headvo.getDbilldate().getStrMonth();

					if (!headvo.getDbillid().startsWith(prefix)) {
						String code = this.getNewBillNo(pk_corp, headvo.getDbilldate(), headvo.getCbusitype());
						headvo.setDbillid(code);
					}
				}
			} else {
				// 校验数据是否被他人修改
				refcheck.isDataEffective(headvo);
				// 修改到其他月份 更改单据号
				IntradeHVO oldvo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class,
						headvo.getPrimaryKey());
				if (oldvo != null) {
					String period = DateUtils.getPeriod(headvo.getDbilldate());
					String oldperiod = DateUtils.getPeriod(oldvo.getDbilldate());
					if (!period.equals(oldperiod)) {
						String prefix = getPrefix(headvo.getCbusitype());
						prefix = prefix + "-" + headvo.getDbilldate().getYear() + headvo.getDbilldate().getStrMonth();
						if (!headvo.getDbillid().startsWith(prefix)) {
							String code = this.getNewBillNo(pk_corp, headvo.getDbilldate(), headvo.getCbusitype());
							headvo.setDbillid(code);
						}
					}
					headvo.setPk_image_group(oldvo.getPk_image_group());
					headvo.setPk_image_library(oldvo.getPk_image_library());
					headvo.setSourcebillid(oldvo.getSourcebillid());
					headvo.setSourcebilltype(oldvo.getSourcebilltype());
				}
			}

			if (!checkCodeIsUnique(headvo)) {
				throw new BusinessException("单据编码重复,请重新输入!");
			}
		}

		headvo.setCbilltype(IBillTypeCode.HP75);
		headvo.setModifydate(new DZFDate());
		headvo.setPk_currency(DzfUtil.PK_CNY);
		if (DZFValueCheck.isEmpty(headvo.getIsinterface()) || !headvo.getIsinterface().booleanValue()) {
			if (StringUtil.isEmpty(headvo.getPzid())) {
				headvo.setIsjz(DZFBoolean.FALSE);
			} else {
				headvo.setIsjz(DZFBoolean.TRUE);
			}
		}
		headvo.setDr(0);
		if (headvo.getFp_style() == null)
			headvo.setFp_style(IFpStyleEnum.NOINVOICE.getValue());

		DZFDouble nmny = DZFDouble.ZERO_DBL; // 金额
		DZFDouble ntaxmny = DZFDouble.ZERO_DBL;// 税额
		DZFDouble ntotaltaxmny = DZFDouble.ZERO_DBL;// 价税合计

		// Map<String, IcbalanceVO> map =
		// ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(
		// DateUtils.getPeriodEndDate(headvo.getDbilldate().toString().substring(0,
		// 7)).toString(),
		// headvo.getPk_corp(), null, true);// 根据单据日期查询

		// Map<String, DZFDouble> numMap = new HashMap<String, DZFDouble>();
		for (SuperVO child : headvo.getChildren()) {
			IntradeoutVO invo = (IntradeoutVO) child;
			String pk_inventory = (String) child.getAttributeValue("pk_inventory");
			DZFDouble salenum = (DZFDouble) child.getAttributeValue("nnum");
			child.setAttributeValue("pk_corp", pk_corp);
			child.setAttributeValue("dbilldate", headvo.getDbilldate());
			child.setAttributeValue("cbilltype", IBillTypeCode.HP75);
			invo.setCbusitype(headvo.getCbusitype());
			// child.setAttributeValue("ncost",
			// child.getAttributeValue("nymny")); 成本不设置

			child.setAttributeValue("dr", 0);
			child.setAttributeValue("pk_billmaker", headvo.getCreator());
			child.setAttributeValue("pk_currency", headvo.getPk_currency());

			invo.setPk_voucher(null);
			invo.setPk_voucher_b(null);
			invo.setZy(null);
			invo.setPzh(null);

			// 领料 其他出库 设置税率 税额 为空
			if (headvo.getCbusitype() != null && (IcConst.LLTYPE.equals(headvo.getCbusitype())
					|| IcConst.QTCTYPE.equals(headvo.getCbusitype()))) {
				invo.setNtax(DZFDouble.ZERO_DBL);
				invo.setNtaxmny(DZFDouble.ZERO_DBL);
				invo.setNprice(DZFDouble.ZERO_DBL);
				invo.setNymny(DZFDouble.ZERO_DBL);
				invo.setNtotaltaxmny(DZFDouble.ZERO_DBL);
			} else if (headvo.getCbusitype() != null && (IcConst.CBTZTYPE.equals(headvo.getCbusitype()))) {
				invo.setNtax(DZFDouble.ZERO_DBL);
				invo.setNtaxmny(DZFDouble.ZERO_DBL);
				invo.setNprice(DZFDouble.ZERO_DBL);
				invo.setNymny(DZFDouble.ZERO_DBL);
				invo.setNtotaltaxmny(DZFDouble.ZERO_DBL);
				invo.setVdef1(null);
			} else {
				if (headvo.getIsback() == null || !headvo.getIsback().booleanValue()) {
					invo.setNcost(DZFDouble.ZERO_DBL);
					invo.setVdef1(null);
				}
			}
			if (invo.getNtax() == null)
				invo.setNtax(DZFDouble.ZERO_DBL);
			// invo.setNcost(null);
			ntotaltaxmny = SafeCompute.add(ntotaltaxmny, (DZFDouble) child.getAttributeValue("ntotaltaxmny"));
			ntaxmny = SafeCompute.add(ntaxmny, (DZFDouble) child.getAttributeValue("ntaxmny"));
			nmny = SafeCompute.add(nmny, (DZFDouble) child.getAttributeValue("nymny"));

			// 为后续校验库存量做准备
			// if (numMap.containsKey(pk_inventory)) {
			// DZFDouble temp = SafeCompute.add(numMap.get(pk_inventory),
			// salenum);
			// numMap.put(pk_inventory, temp);
			// } else {
			// numMap.put(pk_inventory, salenum);
			// }

		}
		checkBodyInventory(headvo.getChildren(), pk_corp);
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		checkAssistExist(corpvo, headvo);
		// 是否校验库存
		if (ischeckfull) {
			// checkBeyond(headvo, numMap, map, isImpl);
		}
		// zpm2017.8.23注释，成本、单价，由总账期末成本结转时回写。这里的回写的成本、单价、为时点成本、单价，对于小企业不具有参考价值
		/*
		 * if (StringUtil.isEmpty(headvo.getCbusitype()) ||
		 * IcConst.XSTYPE.equals(headvo.getCbusitype())) { // 设置成本 for (SuperVO
		 * child : headvo.getChildren()) { IntradeoutVO invo = (IntradeoutVO)
		 * child; String pk_inventory = invo.getPk_inventory(); DZFDouble
		 * salenum = invo.getNnum(); //这里计算成本zpm 2017.4.20
		 * child.setAttributeValue("ncost", calcSaleCost(map, pk_inventory,
		 * salenum)); //设置成本单价 invo.setVdef1(SafeCompute.div(invo.getNcost(),
		 * invo.getNnum()).setScale(2, 0).toString()); } }
		 */

		headvo.setNmny(nmny);
		headvo.setNtaxmny(ntaxmny);
		headvo.setNtotaltaxmny(ntotaltaxmny);

		if (StringUtil.isEmpty(headvo.getPk_ictrade_h())) {
			ret = (IntradeHVO) singleObjectBO.saveObject(headvo.getPk_corp(), headvo);
		} else {
			IntradeHVO oldvo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, headvo.getPrimaryKey());
			if (oldvo.getIsjz() != null && oldvo.getIsjz() == DZFBoolean.TRUE) {
				throw new BusinessException("已转总账的单据，不允许重复转总账");
			}

			// 先删除子表
			String delsqlb = "delete from ynt_ictradeout where pk_ictrade_h = ? and pk_corp = ?";
			sp.clearParams();
			sp.addParam(headvo.getPk_ictrade_h());
			sp.addParam(pk_corp);
			singleObjectBO.executeUpdate(delsqlb, sp);
			// 然后保存主子表
			IntradeoutVO[] resvos = (IntradeoutVO[]) headvo.getChildren();
			for (int i = 0; i < resvos.length; i++) {
				resvos[i].setPrimaryKey(null);
			}
			ret = (IntradeHVO) singleObjectBO.saveObject(pk_corp, headvo);
		}
		return ret;

	}

	private void checkHead(IntradeHVO headvo, String pk_corp) {
		CorpVO corp = corpService.queryByPk(pk_corp);

		if (corp == null) {
			throw new BusinessException("公司不存在!");
		}
		DZFDate ddate = headvo.getDbilldate();
		if (ddate == null) {
			throw new BusinessException("单据日期为空!");
		}

		if (ddate.before(corp.getIcbegindate())) {
			throw new BusinessException("单据日期不能早于启用库存日期，请确认!");

		}
		DZFDate invdate = headvo.getDinvdate();
		if (invdate != null) {
			if (invdate.after(ddate)) {
				throw new BusinessException("发票日期不能晚于单据日期，请确认!");
			}
		}

		if (!StringUtil.isEmpty(headvo.getDinvid()) && headvo.getDinvid().length() > 25) {
			throw new BusinessException("发票号过长!");
		}

	}

	private void checkBodys(IntradeoutVO[] bodyvos, IntradeHVO headvo, StringBuffer strb) {

		if (bodyvos == null || bodyvos.length == 0) {
			throw new BusinessException("表体不允许为空!");
		}
		// List<String> list =new ArrayList<String>();
		int len = bodyvos.length;
		IntradeoutVO vo = null;
		for (int i = 0; i < len; i++) {
			vo = bodyvos[i];
			String inventory = vo.getPk_inventory();

			if (StringUtil.isEmpty(inventory)) {
				strb.append("第" + (i + 1) + "行,存在存货为空的数据!\n");
			}
			if (headvo.getCbusitype() == null || headvo.getCbusitype().equals(IcConst.XSTYPE)) {
				if (vo.getNymny() == null || vo.getNymny().compareTo(DZFDouble.ZERO_DBL) == 0) {
					strb.append("第" + (i + 1) + "行,存在金额为空或零的数据!\n");
				}
			} else {
				if (vo.getNcost() == null || vo.getNcost().compareTo(DZFDouble.ZERO_DBL) == 0) {
					strb.append("第" + (i + 1) + "行,存在成本金额为空或零的数据!\n");
				}
			}

		}
	}

	// zpm2017.8.23注释
	// private DZFDouble calcSaleCost(Map<String, IcbalanceVO> map, String
	// pk_inventory, DZFDouble salenum) {
	// if (salenum == null || salenum.doubleValue() == 0) {
	// throw new BusinessException("出库数量不能为空！");
	// }
	// if (map == null || map.size() == 0) {
	// throw new BusinessException("当前无库存不能出库！");
	// }
	// IcbalanceVO v = map.get(pk_inventory);
	// if (v == null) {
	// throw new BusinessException("当前存货无库存不能出库！");
	// }
	// DZFDouble z = SafeCompute.div(SafeCompute.multiply(v.getNcost(),
	// salenum), v.getNnum());
	// return z.setScale(2, DZFDouble.ROUND_HALF_UP);
	// }

	private void checkBeyond(IntradeHVO headvo, Map<String, DZFDouble> numMap, Map<String, IcbalanceVO> map,
			boolean isImpl) {
		if (headvo.getDbilldate() == null || StringUtil.isEmptyWithTrim(headvo.getDbilldate().toString())) {
			throw new BusinessException("单据日期为空，请检查");
		}
		if (!StringUtil.isEmptyWithTrim(headvo.getPrimaryKey())) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(headvo.getPk_corp());
			sp.addParam(headvo.getPrimaryKey());
			IntradeoutVO[] bvo = (IntradeoutVO[]) singleObjectBO.queryByCondition(IntradeoutVO.class,
					" nvl(dr,0) = 0 and pk_corp = ? and pk_ictrade_h = ? ", sp);
			if (bvo != null && bvo.length > 0) {
				for (int i = 0; i < bvo.length; i++) {
					if (numMap.containsKey(bvo[i].getPk_inventory())) {
						DZFDouble temp = SafeCompute.sub(numMap.get(bvo[i].getPk_inventory()), bvo[i].getNnum());
						numMap.put(bvo[i].getPk_inventory(), temp);
					}
				}

			}
		}
		Map<String, IntradeoutVO> map1 = new HashMap<>();
		for (SuperVO child : headvo.getChildren()) {
			IntradeoutVO invo = (IntradeoutVO) child;
			map1.put(invo.getPk_inventory(), invo);
		}

		List<IntradeoutVO> errList = new ArrayList<>();
		IntradeoutVO outvo = null;
		for (Map.Entry<String, DZFDouble> entry : numMap.entrySet()) {
			String inventory = entry.getKey();
			DZFDouble temp = entry.getValue();
			IcbalanceVO balancevo = map.get(inventory);
			outvo = new IntradeoutVO();
			setInvAttr(outvo, map1.get(inventory));
			if (balancevo == null) {
				outvo.setPk_inventory(inventory);
				outvo.setNnum(temp);
				errList.add(outvo);
			} else {
				DZFDouble temp2 = new DZFDouble(0);
				temp2 = SafeCompute.sub(balancevo.getNnum(), temp);
				if (temp2.compareTo(DZFDouble.ZERO_DBL) < 0) {
					outvo.setPk_inventory(inventory);
					outvo.setNnum(temp2.abs());
					errList.add(outvo);
				}
			}
		}

		IcExBusinessException ice = new IcExBusinessException(IICConstants.EXE_CONFIRM_CODE);

		if (errList != null && errList.size() > 0) {

			if (isImpl) {
				throw new BusinessException("存货库存量不足!");
			} else {
				ice.setErrList(errList);
				throw ice;
			}
		}
	}

	private void setInvAttr(IntradeoutVO newvo, IntradeoutVO oldvo) {

		if (newvo == null || oldvo == null)
			return;

		newvo.setInvclassname(oldvo.getInvclassname());
		newvo.setInvcode(oldvo.getInvcode());
		newvo.setInvname(oldvo.getInvname());
		newvo.setInvspec(oldvo.getInvspec());
		newvo.setInvtype(oldvo.getInvtype());
		newvo.setMeasure(oldvo.getMeasure());
		newvo.setKmmc(oldvo.getKmmc());
		newvo.setKmbm(oldvo.getKmbm());
		newvo.setPk_subject(oldvo.getPk_subject());
	}

	/**
	 * 校验发票号码是否唯一（同一公司范围内
	 * 
	 * @param vo
	 * @return
	 */
	private void checkInvidCodeIsUnique(IntradeHVO vo) {
		if (!StringUtil.isEmptyWithTrim(vo.getDinvid()) && !DZFBoolean.TRUE.equals(vo.getIsback())) {// 存在才校验
																										// 并且
																										// 不是退回
			StringBuffer sf = new StringBuffer();
			sf.append(" Select 1 From ynt_ictrade_h y Where nvl(dr, 0) = 0 and pk_corp = ? and y.dinvid = ? ");// and
																												// y.cbilltype
																												// =
																												// ?
			SQLParameter sp = new SQLParameter();
			// sp.addParam(IBillTypeCode.HP75);//销售单
			sp.addParam(vo.getPk_corp());
			sp.addParam(vo.getDinvid());
			if (!StringUtil.isEmptyWithTrim(vo.getPk_ictrade_h())) {
				sf.append(" and y.pk_ictrade_h != ? ");
				sp.addParam(vo.getPk_ictrade_h());
			}
			boolean b = singleObjectBO.isExists(vo.getPk_corp(), sf.toString(), sp);

			if (b)
				throw new BusinessException("发票号重复,请重新输入!");
		}
	}

	/**
	 * 检查编码是否唯一
	 * 
	 * @param code
	 * @return
	 */
	private boolean checkCodeIsUnique(IntradeHVO vo) {
		boolean ret = false;
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select count(1) as count from ynt_ictrade_h");
		sql.append(" where nvl(dr,0) = 0 and dbillid = ? and cbilltype = ? and pk_corp = ? ");
		sp.addParam(vo.getDbillid());
		sp.addParam(IBillTypeCode.HP75);
		sp.addParam(vo.getPk_corp());
		if (!StringUtil.isEmpty(vo.getPk_ictrade_h())) {
			sql.append(" and pk_ictrade_h != ?");
			sp.addParam(vo.getPk_ictrade_h());
		}
		String res = singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor("count")).toString();
		int num = Integer.valueOf(res);
		if (num <= 0)
			ret = true;
		return ret;
	}
	// private void deletesub(IntradeHVO headvo){
	// singleObjectBO.deleteObjectByID(headvo.getPrimaryKey(), new
	// Class[]{IntradeHVO.class, IntradeoutVO.class});
	// }

	@Override
	public List<IntradeoutVO> querySub(IntradeHVO vo) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select y.* ,fb.name invname, fb.code invcode, fb.invtype,fb.invspec, ");
		sf.append(" me.name measure ,fb.pk_subject,ct.accountname kmmc,ct.accountcode kmbm,fy.name invclassname");
		sf.append(" From ynt_ictradeout y ");
		sf.append(" left join ynt_inventory fb ");
		sf.append(" on y.pk_inventory = fb.pk_inventory ");
		sf.append(" left join ynt_cpaccount ct on fb.pk_subject = ct.pk_corp_account and nvl(ct.dr, 0) = 0 ");
		sf.append(" left join ynt_measure me on fb.pk_measure = me.pk_measure and nvl(me.dr, 0) = 0 ");
		sf.append(" left join ynt_invclassify fy on fb.pk_invclassify = fy.pk_invclassify and  nvl(fy.dr, 0) = 0  ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		sf.append(" and nvl(fb.dr, 0) = 0 ");
		sf.append(" and pk_ictrade_h = ? ");

		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPrimaryKey());

		List<IntradeoutVO> list = (List<IntradeoutVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IntradeoutVO.class));

		if (list != null && list.size() > 0) {
			for (IntradeoutVO outvo : list) {
				if (outvo.getVdef1() == null) {
					outvo.setVdef1(SafeCompute.div(outvo.getNcost(), outvo.getNnum()).setScale(2, 0).toString());
				}
			}
		}
		return list;
	}

	@Override
	public void deleteSale(IntradeHVO vo, String pk_corp) throws DZFWarpException {

		if (StringUtil.isEmpty(vo.getPk_ictrade_h())) {
			return;
		}

		IntradeHVO oldvo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, vo.getPrimaryKey());
		checkIntradeH(oldvo, pk_corp);

		// 更新生成销售退回来源单据的单据
		if (!StringUtil.isEmptyWithTrim(oldvo.getSourcebillid())
				&& !StringUtil.isEmptyWithTrim(oldvo.getSourcebilltype())) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(oldvo.getSourcebillid());
			IntradeHVO prevo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, vo.getSourcebillid());
			if (prevo != null) {
				prevo.setIsback(DZFBoolean.FALSE);
				singleObjectBO.update(prevo, new String[] { "isback" });
			}

		}

		singleObjectBO.deleteObjectByID(vo.getPrimaryKey(), new Class[] { IntradeHVO.class, IntradeoutVO.class });

		// 如果来源于销项发票
		if (!StringUtil.isEmpty(oldvo.getSourcebillid()) && IBillTypeCode.HP90.equals(oldvo.getSourcebilltype())) {
			gl_vatsalinvserv.updateICStatus(oldvo.getSourcebillid(), pk_corp, null);
		}
	}

	private void checkIntradeH(IntradeHVO hvo, String pk_corp) {
		if (hvo == null) {
			throw new BusinessException("该条数据不存在,请刷新后再试!");
		}
		if (!StringUtil.isEmpty(hvo.getPk_corp())) {
			if (!hvo.getPk_corp().equals(pk_corp)) {
				throw new BusinessException("没有操作该条数据的权限!");
			}
		}

		if (hvo.getIsjz() != null && hvo.getIsjz().booleanValue()) {
			throw new BusinessException("已经转总账,不允许其他操作!");
		}
	}

	@Override
	public void saveToGL(IntradeHVO vo, CorpVO corpvo, String userid, String zy) throws DZFWarpException {
		String pk_corp = corpvo.getPk_corp();
		IntradeHVO intradevo = queryIntradeHVOByID(vo.getPk_ictrade_h(), pk_corp);
		if (!StringUtil.isEmpty(intradevo.getCbusitype()) && IcConst.QTCTYPE.equals(intradevo.getCbusitype())) {
			// throw new BusinessException("其他出库单不能转总账");
		}

		checkIntradeH(intradevo, pk_corp);
		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
		TzpzHVO headvo = createGLVO(intradevo, userid, zy, ccountMap, corpvo);
		headvo.setIsqxsy(DZFBoolean.TRUE);
//		headvo.setNbills(getNbills(intradevo.getPk_image_group(), intradevo.getPk_corp()));
		if (IBillTypeCode.HP90.equals(intradevo.getSourcebilltype())) {
			headvo.setNbills(1);
		}else{
			headvo.setNbills(0);
		}
		headvo = voucher.saveVoucher(corpvo, headvo);

		writeBackSale(intradevo, headvo);
	}

	private List<TzpzBVO> createRedGLVO(IntradeHVO ivo, String userid, String zy, Map<String, YntCpaccountVO> ccountMap,
										CorpVO corp, int iprice) {
		// 存货关系设置
		CpcosttransVO model = getcbModel(ivo.getPk_corp());

		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> bodyList = null;
		zy = "红冲成本";
		if (StringUtil.isEmpty(model.getPk_debitaccount())) {
			throw new BusinessException("借方科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, model.getPk_debitaccount(), userid, zy, "ncost", 0, ccountMap, 998, corp,
				null, iprice);
		finBodyList.addAll(bodyList);//

		bodyList = createCommonTzpzBVO(ivo, "", userid, zy, "ncost", 1, ccountMap, 999, corp, null, iprice);
		finBodyList.addAll(bodyList);//

		return finBodyList;
	}

	private CpcosttransVO getcbModel(String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String where = " pk_corp= ? and nvl(dr,0)=0 and jztype= 3";
		CpcosttransVO[] mbvos = (CpcosttransVO[]) singleObjectBO.queryByCondition(CpcosttransVO.class, where, sp);
		// 销售结转
		if (mbvos == null || mbvos.length == 0) {// 查找行业级成本结转模板
			// 将行业成本结转翻译成公司级模板
			mbvos = translateCpcosttransVO(pk_corp);
			if (mbvos == null || mbvos.length == 0) {
				throw new BusinessException("当前公司、行业成本结转模板都为空！请设置！");
			}
		}
		if (mbvos.length != 1) {
			throw new BusinessException("销售成本结转模板只能设置一个！");
		}
		return mbvos[0];
	}

	/**
	 * 将行业成本结转翻译成公司级模板
	 */
	private CpcosttransVO[] translateCpcosttransVO(String pk_corp) throws BusinessException {
		CpcosttransVO[] vos = null;
		try {
			String where = " pk_trade_accountschema in (select corptype from bd_corp where pk_corp='" + pk_corp
					+ "' and nvl(dr,0)=0 )  and nvl(dr,0)=0 ";
			BdTradeCostTransferVO[] hymbVOs = (BdTradeCostTransferVO[]) singleObjectBO
					.queryByCondition(BdTradeCostTransferVO.class, where, new SQLParameter());
			if (hymbVOs == null || hymbVOs.length < 1) {
				throw new BusinessException("公司及行业未设置成本结转模板，请检查！");
			}
			vos = new CpcosttransVO[hymbVOs.length];
			for (int i = 0; i < hymbVOs.length; i++) {
				BdTradeCostTransferVO hymbVO = hymbVOs[i];
				// 借方科目
				String jfkm = hymbVO.getPk_debitaccount();
				// 根据行业会计科目主键找到公司会计科目主键
				jfkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(jfkm, pk_corp);

				// 贷方科目
				String dfkm = hymbVO.getPk_creditaccount();
				dfkm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(dfkm, pk_corp);

				// 取数科目
				String qskm = hymbVO.getPk_fillaccount();
				qskm = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(qskm, pk_corp);

				vos[i] = new CpcosttransVO();
				vos[i].setPk_debitaccount(jfkm);
				vos[i].setPk_creditaccount(dfkm);
				vos[i].setPk_fillaccount(qskm);
				vos[i].setAbstracts(hymbVO.getAbstracts());
			}
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return vos;
	}

	private TzpzHVO createGLVO(IntradeHVO ivo, String userid, String zy, Map<String, YntCpaccountVO> ccountMap,
			CorpVO corp) {
		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(ivo.getPk_corp());

		if (setvo == null) {
			throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
		}
		String priceStr = parameterserv.queryParamterValueByCode(corp.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		List<TzpzBVO> bodyList = createTzpzBVO(ivo, userid, zy, setvo, ccountMap, corp, iprice);
		if (bodyList == null || bodyList.size() == 0) {
			throw new BusinessException("生成凭证表体数据失败!");
		}

		// 红字回冲 生成红冲凭证分录
		if (ivo.getIsback() != null && ivo.getIsback().booleanValue()) {
			List<TzpzBVO> finBodyList = createRedGLVO(ivo, userid, zy, ccountMap, corp, iprice);
			if (finBodyList != null && finBodyList.size() > 0) {
				bodyList.addAll(finBodyList);
			}
		}

		DZFDouble totalDebit = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : bodyList) {
			totalDebit = SafeCompute.add(totalDebit, bvo.getDfmny());
		}

		TzpzHVO headVO = createTzpzHVO(ivo, totalDebit, zy, ivo.getDbilldate());
		headVO.setChildren(bodyList.toArray(new TzpzBVO[0]));
		return headVO;
	}

	private List<TzpzBVO> createTzpzBVO(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {

		List<TzpzBVO> finBodyList = null;
		if (StringUtil.isEmpty(ivo.getCbusitype()) || ivo.getCbusitype().equals(IcConst.XSTYPE)) {

			finBodyList = createTzpzBVOXS(ivo, userid, zy, setvo, ccountMap, corp, iprice);
		} else if (ivo.getCbusitype().equals(IcConst.LLTYPE)) {
			finBodyList = createTzpzBVOLL(ivo, userid, zy, setvo, ccountMap, corp, iprice);
		} else if (ivo.getCbusitype().equals(IcConst.QTCTYPE)) {
			finBodyList = createTzpzBVOQTC(ivo, userid, zy, setvo, ccountMap, corp, iprice);
		} else if (ivo.getCbusitype().equals(IcConst.CBTZTYPE)) {
			finBodyList = createTzpzBVOCBTZ(ivo, userid, zy, setvo, ccountMap, corp, iprice);
		} else {

		}
		DZFDouble totalDebit = DZFDouble.ZERO_DBL;
		if (finBodyList != null) {
			for (TzpzBVO bvo : finBodyList) {
				totalDebit = SafeCompute.add(totalDebit, bvo.getDfmny());
			}
		}
		// if (totalDebit.compareTo(DZFDouble.ZERO_DBL) == 0) {
		// throw new BusinessException("生成凭证金额不能为零!");
		// }
		return finBodyList;
	}

	private List<TzpzBVO> createTzpzBVOXS(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {

		List<TaxitemVO> taxItems = voucher.getTaxItems(corp.getChargedeptname());
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		String pk_accsunj = null;

		if (ivo.getIpayway() == 0) {// 现金
			if (StringUtil.isEmpty(setvo.getXs_xjskkm())) {
				throw new BusinessException("现金收款科目未设置!");
			}
			pk_accsunj = setvo.getXs_xjskkm();
		} else if (ivo.getIpayway() == 1) { // 欠款
			if (StringUtil.isEmpty(setvo.getXs_yszkkm())) {
				throw new BusinessException("应收账款科目未设置!");
			}
			pk_accsunj = setvo.getXs_yszkkm();
		} else if (ivo.getIpayway() == 2) { // 银行

			if (StringUtil.isEmpty(ivo.getPk_bankaccount())) { // 查询银行的最末级科目
				YntCpaccountVO accvo = getFisrtNextLeafAccount("1002", ccountMap);

				if (accvo == null) {
					throw new BusinessException("获取银行科目出错!");
				}
				pk_accsunj = accvo.getPrimaryKey();
			} else {// 查询银行账号对应的科目
				BankAccountVO bankvo = (BankAccountVO) singleObjectBO.queryByPrimaryKey(BankAccountVO.class,
						ivo.getPk_bankaccount());
				if (bankvo == null) {
					throw new BusinessException("获取银行档案出错!");
				}
				pk_accsunj = bankvo.getRelatedsubj();
				if (StringUtil.isEmpty(pk_accsunj)) {
					throw new BusinessException("银行档案设置的科目未设置!");
				}
			}
		} else {
			throw new BusinessException("获取借方科目出错!");
		}

		List<TzpzBVO> bodyList = null;
		bodyList = createCommonTzpzBVO(ivo, pk_accsunj, userid, zy, "nymny&ntaxmny", 0, ccountMap, 0, corp, null,
				iprice);
		finBodyList.addAll(bodyList);//

		// 营业收入（材料销售收入 商品销售收入）
		bodyList = createCommonTzpzBVO(ivo, setvo, userid, zy, "nymny", 1, ccountMap, 1, corp, taxItems, iprice);
		finBodyList.addAll(bodyList);//

		// 应交销项税
		if (StringUtil.isEmpty(setvo.getXs_yjxxskm())) {
			throw new BusinessException("应交销项税科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getXs_yjxxskm(), userid, zy, "ntaxmny", 1, ccountMap, 2, corp, null,
				iprice);
		finBodyList.addAll(bodyList);//

		return finBodyList;
	}

	private List<TzpzBVO> createTzpzBVOLL(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {

		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		zy = "领料出库";
		List<TzpzBVO> bodyList = null;
		// 材料成本科目
		if (StringUtil.isEmpty(setvo.getLl_clcbkm())) {
			throw new BusinessException("材料成本科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getLl_clcbkm(), userid, zy, "ncost", 0, ccountMap, 0, corp, null,
				iprice);
		finBodyList.addAll(bodyList);//

		// 原材料科目
		if (StringUtil.isEmpty(setvo.getLl_yclkm())) {
			throw new BusinessException("原材料科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getLl_yclkm(), userid, zy, "ncost", 1, ccountMap, 1, corp, null,
				iprice);
		finBodyList.addAll(bodyList);// 将bodyList存储的凭证贷方分录放在借方下
		return finBodyList;
	}

	private List<TzpzBVO> createTzpzBVOCBTZ(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {

		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> bodyList = null;
		if (StringUtil.isEmpty(ivo.getMemo())) {
			zy = "成本调整";
		} else {
			zy = ivo.getMemo();
		}

		if (StringUtil.isEmpty(setvo.getVdef5())) {
			throw new BusinessException("成本科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getVdef5(), userid, zy, "ncost", 0, ccountMap, 0, corp, null, iprice);
		finBodyList.addAll(bodyList);//

		if (StringUtil.isEmpty(setvo.getVdef6())) {
			throw new BusinessException("存货科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getVdef6(), userid, zy, "ncost", 1, ccountMap, 1, corp, null, iprice);
		finBodyList.addAll(bodyList);//
		return finBodyList;
	}

	private List<TzpzBVO> createTzpzBVOQTC(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {

		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> bodyList = null;
		zy = "其他出库";
		if (StringUtil.isEmpty(setvo.getVdef4())) {
			throw new BusinessException("其他出库对方科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getVdef4(), userid, zy, "ncost", 0, ccountMap, 0, corp, null, iprice);
		finBodyList.addAll(bodyList);//

		if (StringUtil.isEmpty(setvo.getVdef3())) {
			throw new BusinessException("其他出库科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getVdef3(), userid, zy, "ncost", 1, ccountMap, 1, corp, null, iprice);
		finBodyList.addAll(bodyList);//
		return finBodyList;
	}

	private List<TzpzBVO> createCommonTzpzBVO(IntradeHVO ivo, InvAccSetVO setvo, String userid, String zy,
			String column1, int vdirect, Map<String, YntCpaccountVO> ccountMap, int rowno, CorpVO corp,
			List<TaxitemVO> taxItems, int iprice) {
		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
		SuperVO[] ibodyvos = (SuperVO[]) ivo.getChildren();
		if (ibodyvos == null || ibodyvos.length == 0)
			return bodyList;

		String column2 = null;
		String column3 = null;

		String yysr = setvo.getXs_yysrkm();
		String clsr = setvo.getXs_clsrkm();

		List<TzpzBVO> list = new ArrayList<>();
		// 转换成凭证vo
		DZFDouble taxratio = (DZFDouble) ibodyvos[0].getAttributeValue("ntax");
		for (SuperVO body : ibodyvos) {
			IntradeoutVO ibody = (IntradeoutVO) body;
			String inv = ibody.getPk_inventory();
			if (StringUtil.isEmpty(inv)) {
				throw new BusinessException("存货为空!");
			}
			YntCpaccountVO cvo = null;
			if (StringUtil.isEmpty(ibody.getPk_subject())) {
				throw new BusinessException("存货对应科目为空");
			} else {
				cvo = ccountMap.get(ibody.getPk_subject());
				if (cvo == null)
					throw new BusinessException("存货科目不存在");

				if (cvo.getAccountcode().startsWith(gl_cbconstant.getKcsp_code())) {
					// 贷主营业务收入
					if (StringUtil.isEmpty(yysr)) {
						throw new BusinessException("商品销售收入科目未设置!");
					}
					cvo = ccountMap.get(yysr);
					if (cvo == null)
						throw new BusinessException("商品销售收入科目不存在");

				} else if (cvo.getAccountcode().startsWith(gl_cbconstant.getYcl_code())) {
					if (StringUtil.isEmpty(clsr)) {
						throw new BusinessException("材料销售收入科目未设置!");
					}
					cvo = ccountMap.get(clsr);
					if (cvo == null)
						throw new BusinessException("材料销售收入科目不存在");

				}
				boolean isleaf = cvo.getIsleaf() == null ? false : cvo.getIsleaf().booleanValue();
				if (!isleaf) {//
					// 第一个下级的最末级
					cvo = getFisrtNextLeafAccount(cvo.getAccountcode(), ccountMap);
				}
			}
			///
			DZFDouble nmny = DZFDouble.ZERO_DBL;
			if (column1.indexOf("&") > 0) {
				column2 = column1.split("&")[0];
				column3 = column1.split("&")[1];
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column3), nmny);
			} else {
				column2 = column1;
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
			}
			// 金额为零的 不记录凭证行
			TzpzBVO bvo = createSingleTzpzBVO(cvo, zy, ivo, vdirect, nmny, ibody, corp, taxItems, iprice);
			bvo.setRowno(rowno);
			list.add(bvo);
		}

		// 汇总vo

		Map<String, TzpzBVO> map = new LinkedHashMap();
		for (TzpzBVO bvo : list) {
			String inv = constructTzpzKey(bvo);
			if (StringUtil.isEmpty(inv)) {
				inv = "aaaaa";
			}
			TzpzBVO temp = null;
			if (!map.containsKey(inv)) {
				temp = bvo;
			} else {
				temp = map.get(inv);
				temp.setNnumber(SafeCompute.add(temp.getNnumber(), bvo.getNnumber()));
				temp.setDfmny(SafeCompute.add(temp.getDfmny(), bvo.getDfmny()));
				temp.setYbdfmny(SafeCompute.add(temp.getYbdfmny(), bvo.getYbdfmny()));
				temp.setJfmny(SafeCompute.add(temp.getJfmny(), bvo.getJfmny()));
				temp.setYbjfmny(SafeCompute.add(temp.getYbjfmny(), bvo.getYbjfmny()));
				combinTaxItem(temp, bvo);
			}
			if (temp.getNnumber() != null && DZFDouble.ZERO_DBL.compareTo(temp.getNnumber()) != 0) {
				if (vdirect == 1) {
					DZFDouble price = SafeCompute.div(temp.getDfmny(), temp.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
				} else {
					DZFDouble price = SafeCompute.div(temp.getJfmny(), temp.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
				}

			}
			map.put(inv, temp);
		}

		for (TzpzBVO value : map.values()) {
			if (vdirect == 1) {
				if (value.getDfmny() == null || value.getDfmny().doubleValue() == 0) {
					continue;
				}
			} else {
				if (value.getJfmny() == null || value.getJfmny().doubleValue() == 0) {
					continue;
				}
			}
			bodyList.add(value);
		}
		return bodyList;
	}

	private List<TzpzBVO> createCommonTzpzBVO(IntradeHVO ivo, String pk_accsubj, String userid, String zy,
			String column1, int vdirect, Map<String, YntCpaccountVO> ccountMap, int rowno, CorpVO corp,
			List<TaxitemVO> taxItems, int iprice) {
		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
		SuperVO[] ibodyvos = (SuperVO[]) ivo.getChildren();
		if (ibodyvos == null || ibodyvos.length == 0)
			return bodyList;

		String column2 = null;
		String column3 = null;

		List<TzpzBVO> list = new ArrayList<>();
		// 转换成凭证vo
		DZFDouble taxratio = (DZFDouble) ibodyvos[0].getAttributeValue("ntax");
		for (SuperVO body : ibodyvos) {
			IntradeoutVO ibody = (IntradeoutVO) body;
			String inv = ibody.getPk_inventory();
			if (StringUtil.isEmpty(inv)) {
				throw new BusinessException("存货为空!");
			}

			YntCpaccountVO cvo = ccountMap.get(pk_accsubj);
			if (StringUtil.isEmpty(pk_accsubj)) {
				if (StringUtil.isEmpty(ibody.getPk_subject())) {
					throw new BusinessException("存货对应科目为空");
				} else {
					cvo = ccountMap.get(ibody.getPk_subject());
					if (cvo == null)
						throw new BusinessException("存货科目不存在");
				}
			} else {
				cvo = ccountMap.get(pk_accsubj);
				if (cvo == null)
					throw new BusinessException("入账科目不存在");

			}
			boolean isleaf = cvo.getIsleaf() == null ? false : cvo.getIsleaf().booleanValue();

			if (!isleaf) {//
				// 第一个下级的最末级
				cvo = getFisrtNextLeafAccount(cvo.getAccountcode(), ccountMap);
			}

			///
			DZFDouble nmny = DZFDouble.ZERO_DBL;
			if (column1.indexOf("&") > 0) {
				column2 = column1.split("&")[0];
				column3 = column1.split("&")[1];
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column3), nmny);
			} else {
				column2 = column1;
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
			}
			// 金额为零的 不记录凭证行
			TzpzBVO bvo = createSingleTzpzBVO(cvo, zy, ivo, vdirect, nmny, ibody, corp, taxItems, iprice);
			bvo.setRowno(rowno);
			list.add(bvo);
		}

		// 汇总vo

		Map<String, TzpzBVO> map = new LinkedHashMap();
		for (TzpzBVO bvo : list) {
			String inv = constructTzpzKey(bvo);
			if (StringUtil.isEmpty(inv)) {
				inv = "aaaaa";
			}
			TzpzBVO temp = null;
			if (!map.containsKey(inv)) {
				temp = bvo;
			} else {
				temp = map.get(inv);
				temp.setNnumber(SafeCompute.add(temp.getNnumber(), bvo.getNnumber()));
				temp.setDfmny(SafeCompute.add(temp.getDfmny(), bvo.getDfmny()));
				temp.setYbdfmny(SafeCompute.add(temp.getYbdfmny(), bvo.getYbdfmny()));
				temp.setJfmny(SafeCompute.add(temp.getJfmny(), bvo.getJfmny()));
				temp.setYbjfmny(SafeCompute.add(temp.getYbjfmny(), bvo.getYbjfmny()));
				combinTaxItem(temp, bvo);
			}
			if (temp.getNnumber() != null && DZFDouble.ZERO_DBL.compareTo(temp.getNnumber()) != 0) {
				if (vdirect == 1) {
					DZFDouble price = SafeCompute.div(temp.getDfmny(), temp.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
				} else {
					DZFDouble price = SafeCompute.div(temp.getJfmny(), temp.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
				}

			}
			map.put(inv, temp);
		}

		for (TzpzBVO value : map.values()) {
			if (vdirect == 1) {
				if (value.getDfmny() == null || value.getDfmny().doubleValue() == 0) {
					continue;
				}
			} else {
				if (value.getJfmny() == null || value.getJfmny().doubleValue() == 0) {
					continue;
				}
			}
			bodyList.add(value);
		}
		return bodyList;
	}

	private void combinTaxItem(TzpzBVO temp, TzpzBVO bvo) {
		// 判断税目是否需要合并
		List<PZTaxItemRadioVO> taxlist = bvo.getTax_items();
		List<PZTaxItemRadioVO> taxlist1 = temp.getTax_items();
		if (!DZFValueCheck.isEmpty(taxlist)) {
			if (DZFValueCheck.isEmpty(taxlist1)) {
				temp.setTax_items(taxlist);
			} else {
				Map<String, PZTaxItemRadioVO> map = DZfcommonTools.hashlizeObjectByPk(taxlist1,
						new String[] { "pk_taxitem" });
				for (PZTaxItemRadioVO taxvo : taxlist) {
					if (!map.containsKey(taxvo.getPk_taxitem())) {
						taxlist1.add(taxvo);
					} else {
						PZTaxItemRadioVO taxvo1 = map.get(taxvo.getPk_taxitem());
						taxvo1.setMny(SafeCompute.add(taxvo1.getMny(), taxvo.getMny()));
						taxvo1.setTaxmny(SafeCompute.add(taxvo1.getTaxmny(), taxvo.getTaxmny()));
					}
				}
				temp.setTax_items(taxlist1);
			}
		}
	}

	private TzpzBVO createSingleTzpzBVO(YntCpaccountVO cvo, String zy, IntradeHVO ivo, int vdirect,
			DZFDouble totalDebit, IntradeoutVO ibody, CorpVO corp, List<TaxitemVO> taxItems, int iprice) {
		TzpzBVO depvo = new TzpzBVO();
		depvo.setPk_accsubj(cvo.getPk_corp_account());
		depvo.setVcode(cvo.getAccountcode());
		depvo.setVname(cvo.getAccountname());
		depvo.setZy(zy);// 摘要
		if (cvo.getIsfzhs().charAt(0) == '1') {
			if (StringUtil.isEmpty(ivo.getPk_cust())) {
				throw new BusinessException("科目【" + cvo.getAccountname() + "】启用客户辅助核算,客户必须录入!");
			}
			depvo.setFzhsx1(ivo.getPk_cust());
		}

		// zpm 去掉校验 2018.12.27
		// StringBuffer strb = new StringBuffer();
		// if (Kmschema.isshouru(corp.getCorptype(), cvo.getAccountcode())) {
		// if (cvo.getIsfzhs().charAt(5) != '1') {
		// strb.append("科目【" + cvo.getAccountname() + "】请启用存货辅助核算");
		// }
		//
		// if (StringUtil.isEmpty(ivo.getCbusitype()) ||
		// !IcConst.CBTZTYPE.equalsIgnoreCase(ivo.getCbusitype())) {
		// if (cvo.getIsnum() == null || !cvo.getIsnum().booleanValue()) {
		// if (strb.length() > 0) {
		// strb.append(",数量核算");
		// } else {
		// strb.append("科目【" + cvo.getAccountname() + "】请启用数量核算");
		// }
		// }
		//
		// }
		// if (strb.length() > 0) {
		// strb.append("!");
		// throw new BusinessException(strb.toString());
		// }
		// }

		if (cvo.getIsfzhs().charAt(5) == '1') {
			depvo.setPk_inventory(ibody.getPk_inventory());
		}

		depvo.setVdirect(vdirect);
		totalDebit = totalDebit.setScale(2, DZFDouble.ROUND_HALF_UP);
		if (vdirect == 1) {
			depvo.setDfmny(totalDebit);
			depvo.setYbdfmny(totalDebit);
			// 启用数量核算
			if (cvo.getIsnum() != null && cvo.getIsnum().booleanValue()) {
				depvo.setNnumber(ibody.getNnum());
				DZFDouble price = SafeCompute.div(depvo.getDfmny(), depvo.getNnumber());
				price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
				depvo.setNprice(price);
			}
		} else {
			depvo.setJfmny(totalDebit);
			depvo.setYbjfmny(totalDebit);
			// 启用数量核算
			if (cvo.getIsnum() != null && cvo.getIsnum().booleanValue()) {
				depvo.setNnumber(ibody.getNnum());
				DZFDouble price = SafeCompute.div(depvo.getJfmny(), depvo.getNnumber());
				price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
				depvo.setNprice(price);
			}
		}

		depvo.setPk_currency(DzfUtil.PK_CNY);
		depvo.setNrate(DZFDouble.ONE_DBL);
		depvo.setPk_corp(ivo.getPk_corp());

		DZFDouble taxratio = SafeCompute.div(ibody.getNtax(), new DZFDouble(100));

		// TaxitemParamVO taxparam = new
		// TaxitemParamVO.Builder(depvo.getPk_corp(),
		// taxratio).UserId(ivo.getCreator())
		// .InvName(ibody.getInvname()).Fp_style(ivo.getFp_style()).build();
		Integer fpStyle = null;
		boolean isChargedept = !StringUtil.isEmpty(corp.getChargedeptname()) && corp.getChargedeptname().equals("一般纳税人")
				? true : false;
		// 小规模默认普票

		CaclTaxMny cactool = new CaclTaxMny();
		if (!isChargedept) {
			fpStyle = ivo.getFp_style();
			if (fpStyle == null)
				fpStyle = IFpStyleEnum.COMMINVOICE.getValue();
		}else{
			CaclTaxMny.adjustTaxItemSequence(corp.getPk_corp(), taxItems);
		}
		TaxitemVO taxitem = cactool.matchTaxItem(corp, cvo.getAccountcode(), taxItems, "1", taxratio, fpStyle);
		dealTaxItem(depvo, ibody, taxitem, fpStyle);
		return depvo;

	}

	// 增加税目
	private void dealTaxItem(TzpzBVO depvo, IntradeoutVO ibody, TaxitemVO itemvo, Integer fpStyle) {
		if (itemvo != null) {
			PZTaxItemRadioVO item = new PZTaxItemRadioVO();
			item.setPk_taxitem(itemvo.getPk_taxitem());
			item.setTaxcode(itemvo.getTaxcode());
			item.setTaxname(itemvo.getTaxname());
			if (itemvo.getTaxratio() == null) {
				item.setTaxratio(DZFDouble.ZERO_DBL);
			} else {
				item.setTaxratio(itemvo.getTaxratio());
			}

			if (ibody.getNtaxmny() == null) {
				item.setTaxmny(DZFDouble.ZERO_DBL);
			} else {
				item.setTaxmny(ibody.getNtaxmny());
			}
			if (ibody.getNymny() == null) {
				item.setMny(DZFDouble.ZERO_DBL);
			} else {
				item.setMny(ibody.getNymny());
			}
			item.setVdirect(depvo.getVdirect());
			item.setFp_style(fpStyle);
			List<PZTaxItemRadioVO> tax_items = new ArrayList<>();
			tax_items.add(item);
			depvo.setTax_items(tax_items);
		}
	}

	private TzpzHVO createTzpzHVO(IntradeHVO ivo, DZFDouble totalDebit, String zy, DZFDate pzdate) {
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(ivo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(totalDebit);
		headVO.setDfmny(totalDebit);
		headVO.setCoperatorid(ivo.getCreator());
		headVO.setIshasjz(DZFBoolean.FALSE);
		String speriod = DateUtils.getPeriod(pzdate);
		headVO.setDoperatedate(pzdate);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(ivo.getPk_corp(), pzdate));
		headVO.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
		headVO.setPk_image_group(ivo.getPk_image_group());
		headVO.setPk_image_library(ivo.getPk_image_library());
		// 记录单据来源
		headVO.setSourcebillid(ivo.getPrimaryKey());
		headVO.setSourcebilltype(ivo.getCbilltype());
		headVO.setPeriod(speriod);
		headVO.setVyear(Integer.valueOf(speriod.substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		headVO.setMemo(zy);
		headVO.setFp_style(ivo.getFp_style());// 1普票 2专票3未开票
		// if (!StringUtil.isEmpty(ivo.getCbusitype()) &&
		// IcConst.CBTZTYPE.equalsIgnoreCase(ivo.getCbusitype())) {
		// headVO.setIsNumNull(DZFBoolean.TRUE);
		// } else {
		// headVO.setIsNumNull(DZFBoolean.FALSE);
		// }
		return headVO;

	}

	private void writeBackSale(IntradeHVO ivo, TzpzHVO headvo) {
		ivo.setPzid(headvo.getPrimaryKey());
		ivo.setPzh(headvo.getPzh());
		ivo.setDjzdate(new DZFDate());
		ivo.setIsjz(DZFBoolean.TRUE);
		List<IntradeoutVO> list = new ArrayList<IntradeoutVO>();

		TzpzBVO[] bvos = (TzpzBVO[]) headvo.getChildren();
		SuperVO[] ibodyvos = ivo.getChildren();

		for (SuperVO ibvo : ibodyvos) {
			for (TzpzBVO bvo : bvos) {
				IntradeoutVO ibody = (IntradeoutVO) ibvo;
				if (ibody.getPk_inventory().equals(bvo.getPk_inventory())) {
					ibody.setPzh(headvo.getPzh());
					ibody.setPk_voucher(headvo.getPk_tzpz_h());
					ibody.setPk_voucher_b(bvo.getPk_tzpz_b());
					ibody.setZy(bvo.getZy());
					// ibody.setPk_subject(bvo.getPk_accsubj());
					list.add(ibody);
					break;
				}
			}
		}

		singleObjectBO.updateAry(list.toArray(new IntradeoutVO[list.size()]),
				new String[] { "pk_voucher", "pzh", "pk_voucher_b", "zy" });

		singleObjectBO.update(ivo);
		// singleObjectBO.update(ivo);

		// 如果来源于销项
		gl_vatsalinvserv.updatePZH(headvo);
		// 更新图片已经被占用
		updateRepeatedInfo(headvo.getPk_image_group());
	}

	private void updateRepeatedInfo(String pk_group) {
		// 图片生成凭证
		String sql = " update  ynt_image_group set  istate=?,isuer='Y' where  pk_image_group = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state100);
		sp.addParam(pk_group);
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public void saveDashBack(IntradeHVO vo, CorpVO corpvo, String userid, String login_date) throws DZFWarpException {

		String pk_corp = corpvo.getPk_corp();
		IntradeHVO intradevo = (IntradeHVO) singleObjectBO.queryObject(vo.getPrimaryKey(),
				new Class[] { IntradeHVO.class, IntradeoutVO.class });

		if (intradevo == null) {
			throw new BusinessException("该条数据不存在,请刷新后再试!");
		}
		if (!StringUtil.isEmpty(intradevo.getPk_corp())) {
			if (!intradevo.getPk_corp().equals(pk_corp)) {
				throw new BusinessException("没有操作该条数据的权限!");
			}
		}

		if (StringUtil.isEmpty(intradevo.getCbusitype()) || IcConst.XSTYPE.equalsIgnoreCase(intradevo.getCbusitype())) {

		} else {
			throw new BusinessException("不需红字冲回,请确认!");
		}

		if (intradevo.getIsjz() == null || !intradevo.getIsjz().booleanValue()) {
			throw new BusinessException("未转总账的单据,不允许红字冲回操作!");
		}

		if (intradevo.getIsback() != null && intradevo.getIsback().booleanValue()) {
			throw new BusinessException("红字冲回的单据,不允许再次红字冲回操作!");
		}

		isExistBack(intradevo);

		// 构造销售退回vo
		IntradeHVO dashInTravo = createDashTntradeH(intradevo, userid, login_date);
		// 销售退回标识
		dashInTravo.setIsback(DZFBoolean.TRUE);
		// 保存
		saveSale(dashInTravo, false, false);

		String zy = "红字冲回";
		// 转总账
		saveToGL(dashInTravo, corpvo, userid, zy);

		// 更新原单据退回标识
		// intradevo.setIsback(DZFBoolean.TRUE);
		// singleObjectBO.update(intradevo, new String[] { "isback" });

	}

	private void isExistBack(IntradeHVO intradevo) {
		SQLParameter params = new SQLParameter();
		params.addParam(intradevo.getPk_ictrade_h());
		params.addParam(intradevo.getCbilltype());
		IntradeHVO[] vos = (IntradeHVO[]) singleObjectBO.queryByCondition(IntradeHVO.class,
				" nvl(dr,0) = 0 and sourcebillid=? and sourcebilltype=? ", params);
		if (vos != null && vos.length > 0)
			throw new BusinessException("已红字冲回的单据,不允许再次红字冲回操作!");

	}

	private IntradeHVO createDashTntradeH(IntradeHVO vo, String userid, String login_date) throws DZFWarpException {
		IntradeHVO dashvo = new IntradeHVO();
		SuperVO[] intradeoutvos = (SuperVO[]) vo.getChildren();
		IntradeoutVO[] dashoutvos = new IntradeoutVO[intradeoutvos.length];

		// 主表
		dashvo.setIsback(DZFBoolean.TRUE);// 销售退回
		dashvo.setCbilltype(IBillTypeCode.HP75);
		dashvo.setDbilldate(new DZFDate(login_date));// 设置为当前登录时间
		dashvo.setPk_corp(vo.getPk_corp());
		dashvo.setDbillid(getNewBillNo(vo.getPk_corp(), vo.getDbilldate(), vo.getCbusitype()));// 单据编号
		dashvo.setPk_currency(DzfUtil.PK_CNY);
		dashvo.setPk_cust(vo.getPk_cust());
		dashvo.setDinvdate(vo.getDinvdate());// 发票日期
		dashvo.setDinvid(vo.getDinvid());// 发票编号
		dashvo.setIpayway(vo.getIpayway());
		dashvo.setNmny(vo.getNmny() == null ? null : vo.getNmny().multiply(-1));
		dashvo.setNtaxmny(vo.getNtaxmny() == null ? null : vo.getNtaxmny().multiply(-1));
		dashvo.setNtotaltaxmny(vo.getNtotaltaxmny() == null ? null : vo.getNtotaltaxmny().multiply(-1));
		dashvo.setCreator(userid);
		dashvo.setSourcebilltype(vo.getCbilltype());
		dashvo.setSourcebillid(vo.getPrimaryKey());
		dashvo.setTs(new DZFDateTime());
		dashvo.setCbusitype(IcConst.XSTYPE);
		dashvo.setFp_style(vo.getFp_style());

		// 子表
		IntradeoutVO dashoutvo = null;
		for (int i = 0; i < intradeoutvos.length; i++) {
			dashoutvo = new IntradeoutVO();
			IntradeoutVO outvo = (IntradeoutVO) intradeoutvos[i];
			dashoutvo.setPk_inventory(outvo.getPk_inventory());
			dashoutvo.setDbilldate(dashvo.getDbilldate());
			dashoutvo.setPk_corp(vo.getPk_corp());
			dashoutvo.setPk_billmaker(outvo.getPk_billmaker());
			dashoutvo.setPk_currency(vo.getPk_currency());
			dashoutvo.setCbilltype(vo.getCbilltype());
			dashoutvo.setPk_subject(outvo.getPk_subject());
			dashoutvo.setNnum(outvo.getNnum() == null ? null : outvo.getNnum().multiply(-1));
			dashoutvo.setNymny(outvo.getNymny() == null ? null : outvo.getNymny().multiply(-1));
			dashoutvo.setNcost(outvo.getNcost() == null ? null : outvo.getNcost().multiply(-1));
			dashoutvo.setNtax(outvo.getNtax() == null ? null : outvo.getNtax().multiply(-1));
			dashoutvo.setNtaxmny(outvo.getNtaxmny() == null ? null : outvo.getNtaxmny().multiply(-1));
			dashoutvo.setNtotaltaxmny(outvo.getNtotaltaxmny() == null ? null : outvo.getNtotaltaxmny().multiply(-1));
			dashoutvo.setNprice(outvo.getNprice());
			dashoutvo.setVdef1(outvo.getVdef1());
			dashoutvo.setCbusitype(dashvo.getCbusitype());
			dashoutvos[i] = dashoutvo;
		}

		dashvo.setChildren(dashoutvos);

		return dashvo;

	}

	@Override
	public String getNewBillNo(String pk_corp, DZFDate billdate, String cbusitype) throws DZFWarpException {
		if (StringUtil.isEmptyWithTrim(pk_corp) || billdate == null) {
			throw new BusinessException("获取单据编号失败，公司为空");
		}

		// 老数据默认销售单
		if (StringUtil.isEmpty(cbusitype)) {
			cbusitype = IcConst.XSTYPE;
		}
		String prefix = getPrefix(cbusitype);
		// String period = DateUtils.getPeriod(billdate);
		String ym = billdate.getYear() + billdate.getStrMonth();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		// sp.addParam(period);
		sp.addParam(IBillTypeCode.HP75);
		sp.addParam(cbusitype);

		// String sql = "select max(dbillid) as dbillid from ynt_ictrade_h where
		// pk_corp=? and dbillid like 'XS-2%' and substr(dbilldate,0,7)=? and
		// cbilltype = ?";

		String sql = "select to_number(substr(dbillid,10,length(dbillid))) as dbillid from ynt_ictrade_h where pk_corp=?  and dbillid like '"
				+ prefix + "-" + ym + "%'  and cbilltype = ? and nvl(dr,0) = 0 and nvl(cbusitype,'" + IcConst.XSTYPE
				+ "') =? ";
		List<BigDecimal> nolist = (List<BigDecimal>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());
		int flowno = 1;
		if (nolist != null && nolist.size() > 0) {// 缺号补号逻辑
			for (int i = 1;; i++) {
				if (!nolist.contains(new BigDecimal(i))) {
					flowno = i;
					break;
				}
			}
		}
		return String.format(prefix + "-%s%04d", ym, flowno);
	}

	@Override
	public void rollbackTogl(IntradeHVO vo, String pk_corp) throws DZFWarpException {
		IntradeHVO intradevo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, vo.getPrimaryKey());

		if (StringUtil.isEmptyWithTrim(vo.getSourcebilltype()) && StringUtil.isEmptyWithTrim(vo.getSourcebillid())
				&& DZFBoolean.TRUE.equals(vo.getIsback())) {
			throw new BusinessException("已生成红字冲回的单据，不允许取消转总账");
		}

		// if (!StringUtil.isEmpty(intradevo.getCbusitype())
		// && IcConst.QTCTYPE.equalsIgnoreCase(intradevo.getCbusitype())) {
		// throw new BusinessException("其他出库单不能取消转总账");
		// }
		checkCanl(intradevo, pk_corp);

		deletePz(intradevo);

		// // 删除红字冲回凭证
		// if(vo.getIsback() != null && vo.getIsback().booleanValue()){
		// deleteRedPz(intradevo);
		// }

	}

	// 删除红字冲回凭证
	private void deleteRedPz(IntradeHVO intradevo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(intradevo.getPrimaryKey());

		TzpzHVO[] pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class,
				" Sourcebillid =? and nvl(dr,0)=0 ", sp);

		if (pzHeadVOs != null && pzHeadVOs.length > 0) {

			TzpzHVO headVO = pzHeadVOs[0];
			if (headVO.getIshasjz() != null && headVO.getIshasjz().booleanValue()) {
				// 已有凭证记账
				throw new BusinessException("凭证号：" + headVO.getPzh() + "已记账，不能反操作");
			}
			if (headVO.getVbillstatus() == 1) {
				// 已有凭证审核通过
				throw new BusinessException("凭证号：" + headVO.getPzh() + "已审核，不能反操作");
			}
			sp.clearParams();
			sp.addParam(headVO.getPrimaryKey());
			TzpzBVO[] bvos = (TzpzBVO[]) singleObjectBO.queryByCondition(TzpzBVO.class,
					" pk_tzpz_h=?  and nvl(dr,0)=0 ", sp);
			headVO.setChildren(bvos);
			voucher.deleteVoucher(headVO);
		}
	}

	/**
	 * 查找已经生成过的凭证，并删除之
	 * 
	 * @param intradevo
	 */
	private void deletePz(IntradeHVO intradevo) {
		SQLParameter sp = new SQLParameter();

		sp.addParam(intradevo.getPrimaryKey());

		TzpzHVO[] pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class,
				" pk_tzpz_h in ( select distinct pzid from ynt_ictrade_h  where pk_ictrade_h =? and nvl(dr,0)=0 ) and nvl(dr,0)=0 ",
				sp);

		if (pzHeadVOs != null && pzHeadVOs.length > 0) {

			TzpzHVO headVO = pzHeadVOs[0];
			if (headVO.getIshasjz() != null && headVO.getIshasjz().booleanValue()) {
				// 已有凭证记账
				throw new BusinessException("凭证号：" + headVO.getPzh() + "已记账，不能反操作");
			}
			if (headVO.getVbillstatus() == 1) {
				// 已有凭证审核通过
				throw new BusinessException("凭证号：" + headVO.getPzh() + "已审核，不能反操作");
			}
			sp.clearParams();
			sp.addParam(headVO.getPrimaryKey());
			TzpzBVO[] bvos = (TzpzBVO[]) singleObjectBO.queryByCondition(TzpzBVO.class,
					" pk_tzpz_h=?  and nvl(dr,0)=0 ", sp);
			headVO.setChildren(bvos);
			headVO.setIsqxsy(DZFBoolean.TRUE);
			voucher.deleteVoucher(headVO);
		} else {
			// IntradeHVO head = hvos[0];
			sp.clearParams();
			sp.addParam(intradevo.getPzid());
			String sql = "update  ynt_ictrade_h set pzid = null ,pzh=null,djzdate=null,isjz='N'  where pzid=? ";
			singleObjectBO.executeUpdate(sql, sp);

			sql = "update  ynt_ictradein set pzh = null ,zy=null,pk_voucher_b=null,pk_voucher=null  where pk_voucher=? ";
			singleObjectBO.executeUpdate(sql, sp);

		}

	}

	// 反操作校验
	private void checkCanl(IntradeHVO intradevo, String pk_corp) {

		if (!StringUtil.isEmpty(intradevo.getPk_corp())) {
			if (!intradevo.getPk_corp().equals(pk_corp)) {
				throw new BusinessException("没有操作该条数据的权限!");
			}
		}

		if (intradevo.getIsjz() == null || !intradevo.getIsjz().booleanValue()) {
			throw new BusinessException("没有转总账!");
		}

		String speriod = DateUtils.getPeriod(intradevo.getDbilldate());
		SQLParameter sp = new SQLParameter();
		String qmclsqlwhere = "select * from YNT_QMCL  where nvl(dr,0)=0 and pk_corp  = ? and period =  ? ";
		sp.addParam(pk_corp);
		sp.addParam(speriod);
		List<QmclVO> qmcllist = (List<QmclVO>) singleObjectBO.executeQuery(qmclsqlwhere, sp,
				new BeanListProcessor(QmclVO.class));
		if (qmcllist == null || qmcllist.size() == 0)
			return;
		QmclVO vo = qmcllist.get(0);

		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());

		if (corpvo == null) {
			throw new BusinessException("公司主键为" + pk_corp + "的公司已被删除");
		}

		boolean isgz = qmgzService.isGz(vo.getPk_corp(), vo.getPeriod().toString());
		if (isgz) {// 是否关账
			throw new BusinessException(
					"公司" + corpvo.getUnitname() + "在" + vo.getPeriod().toString() + "月份已关账，不能取消转总账");
		}

		// if (vo.getIscbjz() != null && vo.getIscbjz().booleanValue()) {
		// throw new BusinessException("已经做期间成本结转，请先反期间成本结转");
		// }

	}

	/**
	 * 凭证删除更新库存
	 * 
	 * @param pzHeadVO
	 */
	public void deleteIntradeoutBill(TzpzHVO pzHeadVO) throws DZFWarpException {

		// String condition = " nvl(dr,0)=0 and cbilltype = ? and pk_ictrade_h =
		// ? and pk_corp = ? ";
		String condition = " nvl(dr,0)=0 and  cbilltype = ? and pzid = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillTypeCode.HP75);
		// sp.addParam(pzHeadVO.getSourcebillid());
		sp.addParam(pzHeadVO.getPrimaryKey());
		sp.addParam(pzHeadVO.getPk_corp());
		IntradeHVO[] hvos = (IntradeHVO[]) singleObjectBO.queryByCondition(IntradeHVO.class, condition, sp);

		if (hvos == null || hvos.length == 0) {
			// throw new BusinessException("没有相应的库存单据!");
			return;
		}
		sp.clearParams();
		sp.addParam(pzHeadVO.getPk_tzpz_h());
		String sql = "update  ynt_ictrade_h set pzid = null ,pzh=null,djzdate=null,isjz='N'  where pzid=? ";
		singleObjectBO.executeUpdate(sql, sp);

		sql = "update  ynt_ictradeout set pzh = null ,zy=null,pk_voucher_b=null,pk_voucher=null  where pk_voucher=? ";
		singleObjectBO.executeUpdate(sql, sp);

		// 入库单如果来源于销项发票，也需要删除进项的凭证号
		boolean flag = false;
		for (IntradeHVO hvo : hvos) {
			if (flag) {
				break;
			}

			if (IBillTypeCode.HP90.equals(hvo.getSourcebilltype())) {
				gl_vatsalinvserv.deletePZH(pzHeadVO.getPk_corp(), pzHeadVO.getPk_tzpz_h());
				flag = true;
			}
		}

	}

	@Override
	public IntradeHVO queryIntradeHVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_ictrade_h)) {
			return null;
		}
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.* ,fb.name custname, pz.pzh");
		sf.append(" From ynt_ictrade_h y ");
		sf.append(" left join ynt_fzhs_b fb ");
		sf.append(" on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ? ");
		sf.append(" left join ynt_tzpz_h pz on pz.pk_tzpz_h = y.pzid ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		// sf.append(" and nvl(fb.dr, 0) = 0 ");
		sf.append(" and pk_ictrade_h = ? ");
		sp.addParam(AuxiliaryConstant.ITEM_CUSTOMER);// 客户
		sp.addParam(pk_corp);
		sp.addParam(pk_ictrade_h);
		List<IntradeHVO> listVO = (List<IntradeHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IntradeHVO.class));

		IntradeHVO hvo = null;
		if (listVO != null && listVO.size() > 0) {
			hvo = listVO.get(0);
			sf.setLength(0);
			List<IntradeoutVO> list = querySub(hvo);
			if (list != null && list.size() > 0) {
				hvo.setChildren(list.toArray(new IntradeoutVO[list.size()]));
			}
		}
		return hvo;
	}

	@Override
	public void saveToGL(IntradeHVO[] datas, String pk_corp, String userid, String zy) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(pk_corp);
		if (setvo == null) {
			throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
		}
		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

		List<TzpzBVO> bodyList = new ArrayList<>();
		List<IntradeHVO> inlist = new ArrayList<>();

		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
		DZFDate pzdate = null;
		List<String> imageGroupList = new ArrayList<>();
		int nbills = 0;
		for (IntradeHVO ivo : datas) {
			if (!StringUtil.isEmpty(ivo.getPk_image_group()) && !imageGroupList.contains(ivo.getPk_image_group())) {
				imageGroupList.add(ivo.getPk_image_group());
			}
			if (IBillTypeCode.HP90.equals(ivo.getSourcebilltype())) {
				nbills++;
			}
			IntradeHVO intradevo = queryIntradeHVOByID(ivo.getPk_ictrade_h(), pk_corp);
			checkIntradeH(intradevo, pk_corp);

			List<TzpzBVO> list = createTzpzBVO(intradevo, userid, zy, setvo, ccountMap, corpvo, iprice);
			if (list == null || list.size() == 0) {
				throw new BusinessException("生成凭证表体数据失败!");
			}
			inlist.add(intradevo);

			if (pzdate == null) {
				pzdate = intradevo.getDbilldate();
			} else {
				if (pzdate.before(intradevo.getDbilldate())) {
					pzdate = intradevo.getDbilldate();
				}
			}

			for (TzpzBVO vo : list) {
				bodyList.add(vo);
			}
		}
		if (bodyList == null || bodyList.size() == 0) {
			throw new BusinessException("生成凭证表体数据失败!");
		}

		sortTzpz(bodyList);
		Map<String, TzpzBVO> map1 = new LinkedHashMap<>();

		DZFDouble price = DZFDouble.ZERO_DBL;
		for (TzpzBVO vo : bodyList) {
			String key = constructTzpzKey(vo);
			if (!map1.containsKey(key)) {
				map1.put(key, vo);
			} else {
				TzpzBVO temp = map1.get(key);
				if (temp != null) {
					temp.setDfmny(SafeCompute.add(vo.getDfmny(), temp.getDfmny()));
					temp.setJfmny(SafeCompute.add(vo.getJfmny(), temp.getJfmny()));
					temp.setYbdfmny(SafeCompute.add(vo.getYbdfmny(), temp.getYbdfmny()));
					temp.setYbjfmny(SafeCompute.add(vo.getYbjfmny(), temp.getYbjfmny()));
					temp.setNnumber(SafeCompute.add(vo.getNnumber(), temp.getNnumber()));
					price = SafeCompute.div(vo.getDfmny(), vo.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
					combinTaxItem(temp, vo);
				} else {
					map1.put(key, vo);
				}
			}
		}
		if (map1 == null || map1.size() == 0) {
			throw new BusinessException("合并科目数据失败!");
		} else {
			finBodyList.addAll(map1.values());
		}

		TzpzBVO[] bvos = finBodyList.toArray(new TzpzBVO[finBodyList.size()]);
		VOUtil.ascSort(bvos, new String[] { "vdirect", "vcode" });
		DZFDouble totalDebit = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : bvos) {
			totalDebit = SafeCompute.add(totalDebit, bvo.getDfmny());
		}

		TzpzHVO headvo = createTzpzHVO(inlist.get(0), totalDebit, zy, pzdate);
		List<String> pks = new ArrayList<String>();
		for (IntradeHVO ivo : inlist) {
			pks.add(ivo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));
		headvo.setSourcebillid(sourcebillid);
		String groupId = null;
		if (imageGroupList != null && imageGroupList.size() > 0) {
			// 合并图片组
			groupId = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			headvo.setPk_image_group(groupId);
			updateImageGroup(groupId);
		}
//		headvo.setNbills(getNbills(groupId, pk_corp));
		headvo.setNbills(nbills);
		headvo.setChildren(bvos);
		headvo.setIsqxsy(DZFBoolean.TRUE);
		headvo = voucher.saveVoucher(corpvo, headvo);
		for (IntradeHVO ivo : inlist) {
			writeBackSale(ivo, headvo);
		}
	}

	private int getNbills(String groupId, String pk_corp) {
		int num = 0;
		if (!StringUtil.isEmpty(groupId)) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(groupId);
			String imgQuery = " select count(1) from ynt_image_library where nvl(dr,0)=0 and pk_corp = ? and pk_image_group = ? ";
			BigDecimal imgNum = (BigDecimal) singleObjectBO.executeQuery(imgQuery, sp, new ColumnProcessor());
			num = imgNum.intValue();
		}
		return num;
	}

	private void updateImageGroup(String pk_image_group) {
		// 图片生成凭证
		String sql = " update  ynt_image_group set  istate=?,isuer='Y' where  pk_image_group = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state100);
		sp.addParam(pk_image_group);
		singleObjectBO.executeUpdate(sql, sp);
	}

	protected String constructTzpzKey(TzpzBVO bvo) {
		StringBuffer sf = new StringBuffer();
		sf.append("&").append(bvo.getPk_accsubj()).append("&").append(bvo.getPk_inventory()).append("&")
				.append(bvo.getPk_taxitem()).append("&");

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();

	}

	private String getPrefix(String cbusitype) {
		String prefix = null;
		if (StringUtil.isEmpty(cbusitype)) {
			prefix = "XS";
		} else {
			if (cbusitype.equals(IcConst.XSTYPE)) {
				prefix = "XS";
			} else if (cbusitype.equals(IcConst.LLTYPE)) {
				prefix = "LL";
			} else if (cbusitype.equals(IcConst.QTCTYPE)) {
				prefix = "QT";
			} else if (cbusitype.equals(IcConst.CBTZTYPE)) {
				prefix = "TZ";
			} else {
				prefix = "XS";
			}
		}
		return prefix;
	}

	/**
	 * 检查出库单引用的客户是否存在
	 * 
	 * @param corpvo
	 * @param hvo
	 */
	private void checkAssistExist(CorpVO corpvo, IntradeHVO vo) {
		Set<String> assists = new HashSet<String>();
		if (!StringUtil.isEmpty(vo.getPk_cust())) {
			assists.add(vo.getPk_cust());
			StringBuilder sb = new StringBuilder();
			sb.append("select count(1) from ynt_fzhs_b where pk_corp = ? and pk_auacount_h = ? and pk_auacount_b in ")
					.append(SQLHelper.getInSQL(new ArrayList<String>(assists))).append(" and nvl(dr,0)=0 ");
			SQLParameter sp = new SQLParameter();
			sp.addParam(corpvo.getPk_corp());
			sp.addParam(AuxiliaryConstant.ITEM_CUSTOMER);
			for (String pk : assists) {
				sp.addParam(pk);
			}

			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sb.toString(), sp, new ColumnProcessor());
			if (count == null || count.intValue() != assists.size()) {
				throw new BusinessException("客户[" + vo.getCustname() + "]不存在，或已被删除，请检查");
			}
		}
	}

	// 检验存货是否存在
	private void checkBodyInventory(SuperVO[] childs, String pk_corp) {
		if (childs == null || childs.length == 0)
			return;

		Map<String, InventoryVO> map = new HashMap<String, InventoryVO>();
		List<InventoryVO> list = ic_invserv.query(pk_corp);

		if (list == null || list.size() == 0)
			return;
		for (InventoryVO invo : list) {
			String pk_inventory = invo.getPk_inventory();
			if (!map.containsKey(pk_inventory)) {
				map.put(pk_inventory, invo);
			}
		}

		for (SuperVO child : childs) {
			IntradeoutVO invo = (IntradeoutVO) child;
			if (map.get(invo.getPk_inventory()) == null) {
				// InventoryVO invo1
				// =(InventoryVO)singleObjectBO.queryByPrimaryKey(InventoryVO.class,
				// invo.getPk_inventory());
				throw new BusinessException("存货[" + invo.getInvcode() + "]不存在,或者已删除!");
			}
		}
	}

	@Override
	public AggIcTradeVO[] queryAggIntradeVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_ictrade_h)) {
			return null;
		}
		String strids = SqlUtil.buildSqlConditionForIn(pk_ictrade_h.split(","));
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.*,t.*,fb.name vcustname,ry.code invcode,ry.name invname, ");
		sf.append(" ry.invspec,ry.invtype,re.name measure,fy.name invclassname,yt.bankname vbankaccname");
		sf.append(" From ynt_ictrade_h y ");
		sf.append(" join ynt_ictradeout t on y.pk_ictrade_h= t.pk_ictrade_h ");
		sf.append(" left join ynt_fzhs_b fb ");
		sf.append(" on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ? ");
		sf.append(" left join ynt_inventory ry on t.pk_inventory = ry.pk_inventory ");
		sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" left join ynt_bankaccount yt on y.pk_bankaccount = yt.pk_bankaccount ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		// sf.append(" and nvl(fb.dr, 0) = 0 ");
		sf.append(" and y.pk_ictrade_h in ( ").append(strids).append(" ) ");
		sp.addParam(AuxiliaryConstant.ITEM_CUSTOMER);// 客户
		sp.addParam(pk_corp);

		List<AggIcTradeVO> listVO = (List<AggIcTradeVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(AggIcTradeVO.class));
		CorpVO corpvo = corpService.queryByPk(pk_corp);

		for (AggIcTradeVO vo : listVO) {
			vo.setVcorpname(corpvo.getUnitname());

			if (IcConst.XSTYPE.equals(vo.getCbusitype())) {
				if (vo.getIpayway() != null) {
					IcPayWayEnum wayEnum = IcPayWayEnum.getTypeEnumByValue(vo.getIpayway());
					if (wayEnum != null) {
						vo.setVpaywayname(wayEnum.getName());
					}
				}
			}
			if (StringUtil.isEmpty(vo.getCbusitype())) {
				vo.setCbusitype(IcBillTypeEnum.XSTYPE.getName());
			} else {
				if (IcBillTypeEnum.getTypeEnumByValue(vo.getCbusitype()) == null) {
					vo.setCbusitype(IcBillTypeEnum.XSTYPE.getName());
				} else {
					vo.setCbusitype(IcBillTypeEnum.getTypeEnumByValue(vo.getCbusitype()).getName());
				}
			}

			if (StringUtil.isEmpty(vo.getFp_style())) {
				vo.setFp_style(IFpStyleEnum.NOINVOICE.getName());
			} else {
				if (IFpStyleEnum.getTypeEnumByValue(Integer.parseInt(vo.getFp_style())) == null) {
					vo.setFp_style(IFpStyleEnum.NOINVOICE.getName());
				} else {
					vo.setFp_style(IFpStyleEnum.getTypeEnumByValue(Integer.parseInt(vo.getFp_style())).getName());
				}
			}
		}

		Collections.sort(listVO, new Comparator<AggIcTradeVO>() {
			@Override
			public int compare(AggIcTradeVO o1, AggIcTradeVO o2) {
				int i = o1.getDbillid().compareTo(o2.getDbillid());
				return i;
			}
		});

		Collections.sort(listVO, new Comparator<AggIcTradeVO>() {
			@Override
			public int compare(AggIcTradeVO o1, AggIcTradeVO o2) {
				int i = o1.getDbilldate().compareTo(o2.getDbilldate());
				return i;
			}
		});
		return listVO.toArray(new AggIcTradeVO[listVO.size()]);
	}

	@Override
	public String saveImp(File file, String pk_corp, String fileType, String cuserid) throws DZFWarpException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(file);
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			Sheet sheet1 = impBook.getSheetAt(0);

			// List<AggIcTradeVO> list = new ArrayList<AggIcTradeVO>();
			Map<Integer, String> fieldColumn = IntradeoutVO.getExcelFieldColumn();
			Cell codeCell = null;
			String key = null;
			int length = sheet1.getLastRowNum();
			if (length > 1000) {
				throw new BusinessException("最多可导入1000行");
			}
			Map<String, InvclassifyVO> invclassmap = new HashMap<>();
			Map<String, MeasureVO> jldwmap = new HashMap<>();
			Map<String, InventoryVO> invmap = new HashMap<>();
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			Map<String, BankAccountVO> bankmap = new HashMap<>();
			Map<String, AuxiliaryAccountBVO> accmap = new HashMap<>();

			List<InvclassifyVO> spflVO = splbService.query(pk_corp);
			List<MeasureVO> jldwVO = jldwService.query(pk_corp);
			List<InventoryVO> invVO = queryInventoryVO(pk_corp);

			if (spflVO != null && spflVO.size() > 0) {
				for (InvclassifyVO spflvo : spflVO) {
					invclassmap.put(replaceBlank(spflvo.getName().trim()), spflvo);
				}
			}

			if (jldwVO != null && jldwVO.size() > 0) {
				for (MeasureVO spflvo : jldwVO) {
					jldwmap.put(replaceBlank(spflvo.getName().trim()), spflvo);
				}
			}

			List<BankAccountVO> list = gl_yhzhserv.query(pk_corp, null);
			AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_CUSTOMER, pk_corp, null);

			if (list != null && list.size() > 0) {
				for (BankAccountVO spflvo : list) {
					bankmap.put(replaceBlank(spflvo.getBankname().trim()), spflvo);
				}
			}

			if (bvos != null && bvos.length > 0) {
				for (AuxiliaryAccountBVO spflvo : bvos) {
					accmap.put(replaceBlank(spflvo.getName().trim()), spflvo);
				}
			}

			if (invVO != null && invVO.size() > 0) {
				for (InventoryVO invvo : invVO) {
					String key1 = getCheckKey(invvo);
					invmap.put(key1, invvo);
				}
			}

			StringBuffer msg = new StringBuffer();
			AggIcTradeVO vo = null;

			Map<String, List<AggIcTradeVO>> billmap = new LinkedHashMap();
			List<AggIcTradeVO> billlist = null;
			String tempkey = null;
			boolean isrownull = true;
			for (int iBegin = 1; iBegin <= length; iBegin++) {
				isrownull = true;
				vo = new AggIcTradeVO();
				for (Map.Entry<Integer, String> entry : fieldColumn.entrySet()) {

					if (sheet1.getRow(iBegin) == null)
						continue;

					codeCell = sheet1.getRow(iBegin).getCell(entry.getKey());
					if (codeCell == null)
						continue;
					else {
						if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							if (codeCell.getNumericCellValue() == 0) {
								continue;
							} else {
								isrownull = false;
							}
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							if (codeCell.getRichStringCellValue() == null) {
								continue;
							} else {
								isrownull = false;
							}
						} else {
							continue;
						}
					}
					key = entry.getValue();
					if (key.equals("nnum") || key.equals("nprice") || key.equals("nymny") || key.equals("ntax")
							|| key.equals("ntaxmny") || key.equals("vdef1") || key.equals("ncost")) {
						if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
							vo.setAttributeValue(key, codeCell.getNumericCellValue());
						} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							vo.setAttributeValue(key, replaceBlank(codeCell.getRichStringCellValue().getString()));
						}
					} else {
						String value = null;
						if (key.equals("dbilldate") || key.equals("dinvdate")) {
							if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
								// 判断是否为日期类型
								value = codeCell.getRichStringCellValue().getString();
							} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								if (HSSFDateUtil.isCellDateFormatted(codeCell)) {
									// 用于转化为日期格式
									Date date = codeCell.getDateCellValue();
									DateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
									value = formater.format(date);
								} else {
									// 用于格式化数字，只保留数字的整数部分
									DecimalFormat df = new DecimalFormat("########");
									value = df.format(codeCell.getNumericCellValue());
								}
							}
						} else {
							if (codeCell != null && codeCell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
								value = codeCell.getRichStringCellValue().getString();
								value = replaceBlank(value.trim());
							} else if (codeCell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
								int codeVal = Double.valueOf(codeCell.getNumericCellValue()).intValue();
								value = String.valueOf(codeVal);
							}
						}
						vo.setAttributeValue(key, value);
					}
				}
				if (isrownull || (!StringUtil.isEmpty(vo.getDbilldate()) && vo.getDbilldate().contains("合计")))
					continue;
				try {
					checkImpData(vo, invclassmap, jldwmap, invmap, bankmap, accmap, corpvo);
					String billkey = getbillkey(vo);
					if (StringUtil.isEmpty(tempkey)) {
						tempkey = billkey + iBegin;
					} else {
						if (!tempkey.startsWith(billkey)) {
							tempkey = billkey + iBegin;
						}
					}

					if (billmap.containsKey(tempkey)) {
						billlist = billmap.get(tempkey);
					} else {
						billlist = new ArrayList<>();
					}
					billlist.add(vo);
					billmap.put(tempkey, billlist);
				} catch (BusinessException e) {
					msg.append("<p>第").append(iBegin + 1).append("行的" + e.getMessage() + ",请检查！</p>");
				} catch (Exception e) {
					msg.append("<p>第").append(iBegin + 1).append("行出现未知异常,请检查！</p>");
				}
			}

			// if (msg != null && msg.length() > 0) {
			// return msg.toString();
			// } else {
			for (Map.Entry<String, List<AggIcTradeVO>> entry : billmap.entrySet()) {
				try {
					saveSaleList(entry.getValue(), pk_corp, cuserid);
				} catch (BusinessException e) {
					msg.append(
							"<p>单据号为" + entry.getValue().get(0).getDbillid() + "的出库单据," + e.getMessage() + ",请检查！</p>");
				} catch (Exception e) {
					msg.append("<p>单据号为" + entry.getValue().get(0).getDbillid() + "的出库单据,出现未知异常,请检查！</p>");
				}
			}
			return msg.toString();
			// }
		} catch (

		FileNotFoundException e) {
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			throw new BusinessException("导入文件格式错误");
		} catch (Exception e) {
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

	private Map<String, Integer> getPreMap(String pk_corp) {
		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		Map<String, Integer> preMap = new HashMap<String, Integer>();
		preMap.put(IParameterConstants.DZF009, num);
		preMap.put(IParameterConstants.DZF010, price);

		return preMap;
	}

	private void saveSaleList(List<AggIcTradeVO> list, String pk_corp, String cuserid) {

		if (list == null || list.size() == 0)
			return;

		Map<String, Integer> preMap = getPreMap(pk_corp);
		IntradeHVO headvo = new IntradeHVO();
		List<IntradeoutVO> clist = new ArrayList<>();
		Integer num = preMap.get(IParameterConstants.DZF009);// 数量
		Integer price = preMap.get(IParameterConstants.DZF010);// 单价
		int i = 0;
		for (AggIcTradeVO vo : list) {
			if (i == 0) {
				headvo.setDbilldate(new DZFDate(vo.getDbilldate()));
				headvo.setDbillid(vo.getDbillid());
				headvo.setCbusitype(vo.getCbusitype());
				headvo.setIpayway(vo.getIpayway());
				headvo.setPk_corp(pk_corp);
				headvo.setIarristatus(1);

				headvo.setPk_cust(vo.getPk_cust());
				headvo.setPk_bankaccount(vo.getPk_bankaccount());
				headvo.setDinvdate(vo.getDinvdate());
				headvo.setDinvid(vo.getDinvid());
				headvo.setCreator(cuserid);

				if (!StringUtil.isEmpty(vo.getFp_style())) {
					if (IFpStyleEnum.getTypeEnumByName(vo.getFp_style()) == null) {
						headvo.setFp_style(IFpStyleEnum.NOINVOICE.getValue());
					} else {
						headvo.setFp_style(IFpStyleEnum.getTypeEnumByName(vo.getFp_style()).getValue());
					}
				}
			}
			IntradeoutVO childvo = new IntradeoutVO();
			childvo.setPk_inventory(vo.getPk_inventory());
			childvo.setPk_subject(vo.getPk_subject());
			childvo.setNnum(vo.getNnum().setScale(num, DZFDouble.ROUND_HALF_UP));
			childvo.setNprice(
					SafeCompute.div(vo.getNymny(), childvo.getNnum()).setScale(price, DZFDouble.ROUND_HALF_UP));
			childvo.setNymny(vo.getNymny());
			childvo.setNtax(vo.getNtax());
			childvo.setNtaxmny(vo.getNtaxmny());
			childvo.setNtotaltaxmny(SafeCompute.add(vo.getNymny(), vo.getNtaxmny()));
			childvo.setNcost(vo.getNcost());
			childvo.setVdef1(vo.getVdef1());
			clist.add(childvo);
		}
		headvo.setChildren(clist.toArray(new IntradeoutVO[clist.size()]));
		saveSale(headvo, false, true);

	}

	private void checkImpData(AggIcTradeVO vo, Map<String, InvclassifyVO> invclassmap, Map<String, MeasureVO> jldwmap,
			Map<String, InventoryVO> invmap, Map<String, BankAccountVO> bankmap,
			Map<String, AuxiliaryAccountBVO> accmap, CorpVO corpvo) {

		if (StringUtil.isEmpty(vo.getDbilldate())) {
			throw new BusinessException("单据日期不能为空或格式不正确");
		}

		if (StringUtil.isEmpty(vo.getCbusitype())) {
			throw new BusinessException("出库类型不能为空");
		} else {
			if (IcBillTypeEnum.getTypeEnumByName(vo.getCbusitype()) == null) {
				throw new BusinessException("出库类型不存在");
			} else {
				vo.setCbusitype(IcBillTypeEnum.getTypeEnumByName(vo.getCbusitype()).getValue());
			}
		}

		if (!StringUtil.isEmpty(vo.getDbillid())) {
			String prefix = getPrefix(vo.getCbusitype());
			prefix = prefix + "-" + vo.getDbilldate().substring(0, 4) + vo.getDbilldate().substring(5, 7);
			if (!vo.getDbillid().startsWith(prefix)) {
				throw new BusinessException("单据号格式不对");
			}
		}

		if (StringUtil.isEmpty(vo.getVcorpname())) {
			throw new BusinessException("公司不能为空");
		} else {
			if (!vo.getVcorpname().equals(corpvo.getUnitname())) {
				throw new BusinessException("公司与当前登录公司不符");
			}
		}

		if (StringUtil.isEmpty(vo.getInvcode())) {
			throw new BusinessException("存货编码不能为空");
		}

		if (StringUtil.isEmpty(vo.getInvname())) {
			throw new BusinessException("存货名称不能为空");
		}

		InvclassifyVO classvo = invclassmap.get(vo.getInvclassname());
		if (classvo != null) {
			vo.setInvclassname(classvo.getPk_invclassify());
		}

		MeasureVO measvo = jldwmap.get(vo.getMeasure());
		if (measvo != null) {
			vo.setMeasure(measvo.getPrimaryKey());
		}

		String key = getCheckKey1(vo);

		InventoryVO invvo = invmap.get(key);

		if (invvo == null) {
			throw new BusinessException("存货信息不存在");
		} else {
			vo.setPk_inventory(invvo.getPk_inventory());
			vo.setPk_subject(invvo.getPk_subject());
		}

		if (vo.getNnum() == null) {
			throw new BusinessException("数量不能为空");
		}

		if (IcConst.XSTYPE.equals(vo.getCbusitype())) {
			vo.setVdef1(null);
			vo.setNcost(null);
			if (StringUtil.isEmpty(vo.getVpaywayname())) {
				throw new BusinessException("付款方式不能为空");
			} else {
				if (IcPayWayEnum.getTypeEnumByName(vo.getVpaywayname()) == null) {
					throw new BusinessException("付款方式不存在");
				} else {
					int ipayway = IcPayWayEnum.getTypeEnumByName(vo.getVpaywayname()).getValue();
					vo.setIpayway(ipayway);
					// 录入银行卡号
					if (ipayway == IcPayWayEnum.BANK.getValue()) {
						if (!StringUtil.isEmpty(vo.getVbankaccname())) {
							BankAccountVO bank = bankmap.get(vo.getVbankaccname());
							if (bank == null) {
								throw new BusinessException("银行账户不存在");
							} else {
								vo.setPk_bankaccount(bank.getPk_bankaccount());
							}
						}

					}
				}
			}

			if (!StringUtil.isEmpty(vo.getVcustname())) {
				AuxiliaryAccountBVO bvo = accmap.get(vo.getVcustname());
				if (bvo == null) {
					throw new BusinessException("客户不存在");
				} else {
					vo.setPk_cust(bvo.getPk_auacount_b());
				}
			}

			if (vo.getNprice() == null)
				throw new BusinessException("销售单价不能为空");
			if (vo.getNymny() == null)
				throw new BusinessException("销售金额不能为空");

		} else {
			vo.setIpayway(IcPayWayEnum.CASH.getValue());
			vo.setDinvdate(null);
			vo.setDinvid(null);
			vo.setNprice(null);
			vo.setNtax(null);
			vo.setNtaxmny(null);
			vo.setNymny(null);
			if (vo.getVdef1() == null)
				throw new BusinessException("成本单价不能为空");
			if (vo.getNcost() == null)
				throw new BusinessException("成本金额不能为空");
		}

		if (!StringUtil.isEmpty(vo.getFp_style())) {
			if (IFpStyleEnum.getTypeEnumByName(vo.getFp_style()) == null) {
				throw new BusinessException("发票类型不存在");
			}
		}
	}

	private String getCheckKey(InventoryVO invvo) {
		StringBuffer sb = new StringBuffer();

		if (StringUtil.isEmpty(invvo.getCode())) {
			sb.append(" ");
		} else {
			sb.append(replaceBlank(invvo.getCode().trim()));
		}

		if (StringUtil.isEmpty(invvo.getName())) {
			sb.append(" ");
		} else {
			sb.append(replaceBlank(invvo.getName().trim()));
		}

		if (StringUtil.isEmpty(invvo.getInvspec())) {
			sb.append(" ");
		} else {
			sb.append(replaceBlank(invvo.getInvspec().trim()));
		}

		// if (StringUtil.isEmpty(invvo.getInvtype())) {
		// sb.append(" ");
		// } else {
		// sb.append(replaceBlank(invvo.getInvtype().trim()));
		// }

		if (StringUtil.isEmpty(invvo.getPk_invclassify())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getPk_invclassify());
		}

		if (StringUtil.isEmpty(invvo.getPk_measure())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getPk_measure());
		}
		return sb.toString();
	}

	private String getCheckKey1(AggIcTradeVO invvo) {
		StringBuffer sb = new StringBuffer();

		if (StringUtil.isEmpty(invvo.getInvcode())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getInvcode());
		}

		if (StringUtil.isEmpty(invvo.getInvname())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getInvname());
		}

		if (StringUtil.isEmpty(invvo.getInvspec())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getInvspec());
		}

		// if (StringUtil.isEmpty(invvo.getInvtype())) {
		// sb.append(" ");
		// } else {
		// sb.append(invvo.getInvtype());
		// }

		if (StringUtil.isEmpty(invvo.getInvclassname())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getInvclassname());
		}

		if (StringUtil.isEmpty(invvo.getMeasure())) {
			sb.append(" ");
		} else {
			sb.append(invvo.getMeasure());
		}
		return sb.toString();
	}

	private List<InventoryVO> queryInventoryVO(String pk_corp) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("pk_corp=? and nvl(dr,0)=0");
		sp.addParam(pk_corp);
		List<InventoryVO> listVo = (List<InventoryVO>) singleObjectBO.retrieveByClause(InventoryVO.class, sb.toString(),
				sp);
		return listVo;
	}

	private AuxiliaryAccountBVO[] autoMatchAuxiliaryAccount(String pk_auacount_h, String pk_corp) {

		String condition = " nvl(dr,0) = 0  and  pk_auacount_h = ?  and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_auacount_h);
		sp.addParam(pk_corp);
		AuxiliaryAccountBVO[] vos = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
				condition, sp);

		if (vos == null || vos.length == 0) {
			return null;
		}
		return vos;
	}

	private String getDateData(String sdate) {
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = null;
			StringTokenizer st = new StringTokenizer(sdate, "-/.");
			if (st.countTokens() == 3) {
				DZFDate ddate = new DZFDate(sdate);
				date = ddate.toDate();
			} else {
				date = formatter.parse(sdate);
			}
			String dateString = formatter.format(date);
			return dateString;
		} catch (Exception e) {
		} finally {
		}
		return sdate;
	}

	private String getbillkey(AggIcTradeVO vo) {
		// 单据日期、公司、出库类型、单据编码、付款方式、发票号、发票日期、银行账户、客户、发票类型
		StringBuffer strb = new StringBuffer();

		strb.append(vo.getDbilldate());
		strb.append(vo.getPk_corp());
		strb.append(vo.getCbusitype());
		strb.append(vo.getDbillid());
		strb.append(vo.getIpayway());
		strb.append(vo.getDinvid());
		strb.append(vo.getDinvdate());
		strb.append(vo.getPk_bankaccount());
		strb.append(vo.getPk_cust());
		strb.append(vo.getFp_style());
		return strb.toString();
	}

	private String replaceBlank(String str) {
		String dest = "";
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	// 查询第一分支的最末级科目
	private YntCpaccountVO getFisrtNextLeafAccount(String accountcode, Map<String, YntCpaccountVO> ccountMap) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目
		for (YntCpaccountVO accvo : ccountMap.values()) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(accountcode)) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	private void sortTzpz(List<TzpzBVO> tblist) {
		if (tblist == null || tblist.size() == 0)
			return;

		Collections.sort(tblist, new Comparator<TzpzBVO>() {
			@Override
			public int compare(TzpzBVO o1, TzpzBVO o2) {
				int i = o1.getRowno().compareTo(o2.getRowno());
				return i;
			}
		});

		int rowno = 1;
		for (TzpzBVO bvo : tblist) {
			bvo.setRowno(rowno++);
		}
	}

	@Override
	public StringBuffer buildQmjzMsg(List<String> periodList, String pk_corp) throws DZFWarpException {
		if (periodList == null || periodList.size() == 0)
			return null;

		String part = SqlUtil.buildSqlForIn("period", periodList.toArray(new String[0]));

		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and ");
		sf.append(part);
		sp.addParam(pk_corp);

		List<QmclVO> list = (List<QmclVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(QmclVO.class));
		VOUtil.ascSort(list, new String[] { "period" });
		sf.setLength(0);
		if (list != null && list.size() > 0) {
			String period;
			DZFBoolean value;
			for (QmclVO vo : list) {
				period = vo.getPeriod();

				value = vo.getIsqjsyjz();
				if (value != null && value.booleanValue()) {
					sf.append("<p><font color = 'red'>").append(period).append("期间损益已结转，生成凭证后，请重新结转期间损益!</font></p>");
				}

				value = vo.getIshdsytz();
				if (value != null && value.booleanValue()) {
					sf.append("<p><font color = 'red'>").append(period).append("汇兑调整已完成，生成凭证后，请重新进行汇兑调整!</font></p>");
				}
			}
		}

		return sf;
	}

}

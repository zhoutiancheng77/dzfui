package com.dzf.zxkj.platform.service.icbill.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.*;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.enums.IcBillTypeEnum;
import com.dzf.zxkj.common.enums.IcPayWayEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IYHZHService;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.icset.IInvAccSetService;
import com.dzf.zxkj.platform.service.icset.IInvclassifyService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.zncs.IVATInComInvoice2Service;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Service("ic_purchinserv")
public class PurchInServiceImpl implements IPurchInService {

	@Autowired
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private IInvAccSetService ic_chkmszserv;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private IInventoryService ic_invserv;
	@Autowired
	private IInvclassifyService splbService;
	@Autowired
	private IMeasureService jldwService;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private IYHZHService gl_yhzhserv;
	@Autowired
	IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IVATInComInvoice2Service vatincomserv;
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
		// 根据查询条件查询公司的信息
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.* ,fb.name custname");
		sf.append(" From ynt_ictrade_h y ");
		sf.append(" left join ynt_fzhs_b fb ");
		sf.append(" on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ?");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		sf.append(" and cbilltype = ? ");
		sp.addParam(AuxiliaryConstant.ITEM_SUPPLIER);// 客户
		sp.addParam(paramvo.getPk_corp());
		sp.addParam(IBillTypeCode.HP70);

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
			IctradeinVO[] bvos = (IctradeinVO[]) singleObjectBO.queryByCondition(IctradeinVO.class,
					" nvl(dr,0) = 0 and pk_corp = ? and " + idwhere, sp);
			if (bvos != null && bvos.length > 0) {
				List<IctradeinVO> list = null;
				Map<String, List<IctradeinVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(bvos),
						new String[] { "pk_ictrade_h" });
				for (IntradeHVO hvo : listVO) {
					DZFDouble nnum = DZFDouble.ZERO_DBL; // 金额
					list = map.get(hvo.getPrimaryKey());
					if (list != null && list.size() > 0) {
						for (SuperVO child : list) {
							IctradeinVO invo = (IctradeinVO) child;
							nnum = SafeCompute.add(nnum, invo.getNnum());
						}
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
					"and exists (select 1 from ynt_ictradein b where y.PK_ICTRADE_H = b.PK_ICTRADE_H and b.pk_inventory = ?)");
			sp.addParam(paramvo.getQinvid());
		}

		if (paramvo.getEnddate() != null) {
			buffer.append(" and  dbilldate <= ?");
			sp.addParam(paramvo.getEnddate());
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
		if (!StringUtil.isEmpty(paramvo.getCbusitype())) {
			buffer.append(" and  nvl(cbusitype,'" + IcConst.CGTYPE + "')  = ? ");
			sp.addParam(paramvo.getCbusitype());
		}

		if (paramvo.getIszg() != null) {
			if (paramvo.getIszg().booleanValue()) {
				buffer.append(" and  (nvl(iszg,'N')  = ? or nvl(isczg,'N')  = ?)");
				sp.addParam(paramvo.getIszg());
				sp.addParam(paramvo.getIszg());
			} else {
				buffer.append(" and  (nvl(iszg,'N')  = ? and nvl(isczg,'N')  = ?)");
				sp.addParam(paramvo.getIszg());
				sp.addParam(paramvo.getIszg());
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

		String prefix = "CG-" + ddate.getYear() + month;
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
	public IntradeHVO save(IntradeHVO vo, boolean isImpl) throws DZFWarpException {

		String speriod = DateUtils.getPeriod(vo.getDbilldate());
		SQLParameter sp = new SQLParameter();
		String qmclsqlwhere = "select * from YNT_QMCL  where nvl(dr,0)=0 and pk_corp  = ? and period =  ? ";
		sp.addParam(vo.getPk_corp());
		sp.addParam(speriod);
		List<QmclVO> qmcllist = (List<QmclVO>) singleObjectBO.executeQuery(qmclsqlwhere, sp,
				new BeanListProcessor(QmclVO.class));
		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());
		if (qmcllist != null && qmcllist.size() > 0) {
			QmclVO clvo = qmcllist.get(0);

			if (corpvo == null) {
				throw new BusinessException("公司主键为" + vo.getPk_corp() + "的公司已被删除!");
			}

			boolean isgz = qmgzService.isGz(vo.getPk_corp(), speriod);
			if (isgz) {// 是否关账
				throw new BusinessException("公司" + corpvo.getUnitname() + "在" + speriod + "月份已关账,不允许保存!");
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

		checkHead(vo, corpvo);
		StringBuffer strb = new StringBuffer();
		checkBodys(vo, (IctradeinVO[]) vo.getChildren(), strb);
		if (strb.length() > 0) {
			throw new BusinessException(strb.toString());
		}
		IntradeHVO ret = null;
		String pk_corp = vo.getPk_corp();

		// 校验发票号码是否唯一
		checkInvidCodeIsUnique(vo);
		if (StringUtil.isEmpty(vo.getDbillid())) {

			String code = this.getNewBillNo(pk_corp, vo.getDbilldate(), vo.getCbusitype());
			vo.setDbillid(code);
		} else {

			// 校验数据是否被他人修改
			refcheck.isDataEffective(vo);
			if (StringUtil.isEmpty(vo.getPk_ictrade_h())) {
				if (!isImpl) {
					String prefix = getPrefix(vo.getCbusitype());
					prefix = prefix + "-" + vo.getDbilldate().getYear() + vo.getDbilldate().getStrMonth();

					if (!vo.getDbillid().startsWith(prefix)) {
						String code = this.getNewBillNo(pk_corp, vo.getDbilldate(), vo.getCbusitype());
						vo.setDbillid(code);
					}
				}
			} else {
				// 修改到其他月份 更改单据号
				IntradeHVO oldvo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, vo.getPrimaryKey());
				if (oldvo != null) {
					String period = DateUtils.getPeriod(vo.getDbilldate());
					String oldperiod = DateUtils.getPeriod(oldvo.getDbilldate());
					if (!period.equals(oldperiod)) {
						String prefix = getPrefix(vo.getCbusitype());
						prefix = prefix + "-" + vo.getDbilldate().getYear() + vo.getDbilldate().getStrMonth();

						if (!vo.getDbillid().startsWith(prefix)) {
							String code = this.getNewBillNo(pk_corp, vo.getDbilldate(), vo.getCbusitype());
							vo.setDbillid(code);
						}
					}
					vo.setPk_image_group(oldvo.getPk_image_group());
					vo.setPk_image_library(oldvo.getPk_image_library());
					vo.setSourcebillid(oldvo.getSourcebillid());
					vo.setSourcebilltype(oldvo.getSourcebilltype());
				}
			}

			if (!checkCodeIsUnique(vo)) {
				throw new BusinessException("单据编码重复,请重新输入!");
			}
		}

		vo.setCbilltype(IBillTypeCode.HP70);
		vo.setModifydate(new DZFDate());
		vo.setDr(0);
		vo.setPk_currency(DzfUtil.PK_CNY);
		if (vo.getFp_style() == null)
			vo.setFp_style(IFpStyleEnum.NOINVOICE.getValue());
		if (vo.getIszg() == null) {
			vo.setIszg(DZFBoolean.FALSE);
		}

		// if (vo.getIsczg() == null) {
		vo.setIsczg(DZFBoolean.FALSE);

		// }

		if (DZFValueCheck.isEmpty(vo.getIsinterface()) || !vo.getIsinterface().booleanValue()) {
			if (StringUtil.isEmpty(vo.getPzid())) {
				vo.setIsjz(DZFBoolean.FALSE);
			} else {
				vo.setIsjz(DZFBoolean.TRUE);
			}
		}

		DZFDouble nmny = DZFDouble.ZERO_DBL; // 金额
		DZFDouble ntaxmny = DZFDouble.ZERO_DBL;// 税额
		DZFDouble ntotaltaxmny = DZFDouble.ZERO_DBL;// 价税合计

		// Map<String, DZFDouble> numMap = new HashMap<String, DZFDouble>();
		for (SuperVO child : vo.getChildren()) {
			IctradeinVO invo = (IctradeinVO) child;
			invo.setPk_corp(pk_corp);
			invo.setDbilldate(vo.getDbilldate());
			invo.setCbilltype(IBillTypeCode.HP70);
			invo.setCbusitype(vo.getCbusitype());
			invo.setNcost(invo.getNymny());
			if (invo.getNtax() == null)
				invo.setNtax(DZFDouble.ZERO_DBL);
			invo.setDr(0);
			invo.setPk_billmaker(vo.getCreator());
			invo.setPk_currency(vo.getPk_currency());

			if (vo.getCbusitype() == null || !IcConst.WGTYPE.equals(vo.getCbusitype())) {
				invo.setPk_voucher(null);
				invo.setPk_voucher_b(null);
				invo.setZy(null);
				invo.setPzh(null);
			}

			// 暂估设置税率 税额 为空
			if (vo.getIszg() != null && vo.getIszg().booleanValue()) {
				invo.setNtax(DZFDouble.ZERO_DBL);
				invo.setNtaxmny(DZFDouble.ZERO_DBL);
				invo.setNtotaltaxmny(invo.getNymny());
			}
			// 完工 其他入库 设置税率 税额 为空
			if (vo.getCbusitype() != null
					&& (IcConst.WGTYPE.equals(vo.getCbusitype()) || IcConst.QTRTYPE.equals(vo.getCbusitype()))) {
				invo.setNtax(DZFDouble.ZERO_DBL);
				invo.setNtaxmny(DZFDouble.ZERO_DBL);
				invo.setNtotaltaxmny(invo.getNymny());
			}

			ntaxmny = SafeCompute.add(ntaxmny, invo.getNtaxmny());
			nmny = SafeCompute.add(nmny, invo.getNymny());
			// vo.setNtotaltaxmny(SafeCompute.add(invo.getNtaxmny(),
			// invo.getNymny()));
			ntotaltaxmny = SafeCompute.add(ntotaltaxmny, invo.getNtotaltaxmny());

			// String pk_inventory = (String)
			// child.getAttributeValue("pk_inventory");
			// 为后续校验库存量做准备
			// if (numMap.containsKey(pk_inventory)) {
			// DZFDouble temp = SafeCompute.add(numMap.get(pk_inventory),
			// (DZFDouble) child.getAttributeValue("nnum"));
			// numMap.put(pk_inventory, temp);
			// } else {
			// numMap.put(pk_inventory, ((DZFDouble)
			// child.getAttributeValue("nnum")));
			// }
		}
		checkBodyInventory(vo.getChildren(), pk_corp);
		checkAssistExist(corpvo, vo);
		vo.setNmny(nmny);
		vo.setNtaxmny(ntaxmny);
		vo.setNtotaltaxmny(ntotaltaxmny);

		boolean isAdd = false;
		if (StringUtil.isEmpty(vo.getPk_ictrade_h())) {
			ret = (IntradeHVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
			isAdd = true;
		} else {
			// checkBeyond(vo, numMap);
			// 先删除子表
			String delsqlb = "delete from ynt_ictradein where pk_ictrade_h = ? and pk_corp = ?";
			sp.clearParams();
			sp.addParam(vo.getPk_ictrade_h());
			sp.addParam(pk_corp);
			singleObjectBO.executeUpdate(delsqlb, sp);
			// 然后保存主子表
			IctradeinVO[] resvos = (IctradeinVO[]) vo.getChildren();
			for (int i = 0; i < resvos.length; i++) {
				resvos[i].setPk_ictradein(null);
			}
			ret = (IntradeHVO) singleObjectBO.saveObject(pk_corp, vo);
		}

		// 生成下月初负入库 2017-08-25 去掉下月冲暂估
		if (ret.getIszg() != null && ret.getIszg().booleanValue()) {
			// 进销存暂估采购单下月是否自动回冲 0--是 1---否
			YntParameterSet setvo = sys_parameteract.queryParamterbyCode(ret.getPk_corp(), "dzf005");
			if (setvo != null && setvo.getPardetailvalue() == 0) {
				saveZgIC(isAdd, ret, pk_corp);
			}
		} else {
			if (!isAdd) {
				// 删除暂估对应负库存
				IntradeHVO[] hvos = getNextIntradeHVO(ret, pk_corp);
				if (hvos != null && hvos.length > 0) {
					deleteByIntradeHVO(hvos[0], pk_corp);
				}
			}
		}

		return ret;
	}

	private void checkHead(IntradeHVO headvo, CorpVO corp) {
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

	private void checkBodys(IntradeHVO headvo, IctradeinVO[] bodyvos, StringBuffer strb) {

		if (bodyvos == null || bodyvos.length == 0) {
			throw new BusinessException("表体不允许为空!");
		}
		int len = bodyvos.length;
		IctradeinVO vo = null;
		for (int i = 0; i < len; i++) {
			vo = bodyvos[i];
			String inventory = vo.getPk_inventory();
			if (StringUtil.isEmpty(inventory)) {
				strb.append("第" + (i + 1) + "行,存在存货为空的数据!\n");
			}

			// if (vo.getNnum() == null ||
			// vo.getNnum().compareTo(DZFDouble.ZERO_DBL) == 0) {
			// strb.append("第" + (i + 1) + "行,存在数量为空或零的数据!\n");
			// }

			if (vo.getNymny() == null || vo.getNymny().compareTo(DZFDouble.ZERO_DBL) == 0) {
				strb.append("第" + (i + 1) + "行,存在金额为空或零的数据!\n");
			}

		}
	}

	/**
	 * 检查入库单引用的供应商是否存在
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
			sp.addParam(AuxiliaryConstant.ITEM_SUPPLIER);
			for (String pk : assists) {
				sp.addParam(pk);
			}

			BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sb.toString(), sp, new ColumnProcessor());
			if (count == null || count.intValue() != assists.size()) {
				throw new BusinessException("供应商[" + vo.getCustname() + "]不存在，或已被删除，请检查");
			}
		}
	}

	private void checkBeyond(IntradeHVO headvo, Map<String, DZFDouble> numMap) {
		if (headvo.getDbilldate() == null || StringUtil.isEmptyWithTrim(headvo.getDbilldate().toString())) {
			throw new BusinessException("单据日期为空，请检查");
		}
		Map<String, IcbalanceVO> map = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(
				DateUtils.getPeriodEndDate(headvo.getDbilldate().toString().substring(0, 7)).toString(),
				headvo.getPk_corp(), null, true);// 根据单据日期查询
		if (!StringUtil.isEmptyWithTrim(headvo.getPrimaryKey())) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(headvo.getPk_corp());
			sp.addParam(headvo.getPrimaryKey());
			IctradeinVO[] bvo = (IctradeinVO[]) singleObjectBO.queryByCondition(IctradeinVO.class,
					" nvl(dr,0) = 0 and pk_corp = ? and pk_ictrade_h = ? ", sp);
			if (bvo != null && bvo.length > 0) {
				for (int i = 0; i < bvo.length; i++) {
					if (numMap == null || numMap.size() == 0) {
						DZFDouble temp = SafeCompute.sub(DZFDouble.ZERO_DBL, bvo[i].getNnum());
						numMap.put(bvo[i].getPk_inventory(), temp);
					} else {
						if (numMap.containsKey(bvo[i].getPk_inventory())) {
							DZFDouble temp = SafeCompute.sub(numMap.get(bvo[i].getPk_inventory()), bvo[i].getNnum());
							numMap.put(bvo[i].getPk_inventory(), temp);
						} else {
							DZFDouble temp = SafeCompute.sub(DZFDouble.ZERO_DBL, bvo[i].getNnum());
							numMap.put(bvo[i].getPk_inventory(), temp);
						}
					}
				}
			}
		}
		for (Map.Entry<String, DZFDouble> entry : numMap.entrySet()) {
			String inventory = entry.getKey();
			DZFDouble temp = entry.getValue();
			IcbalanceVO balancevo = map.get(inventory);
			DZFDouble temp2 = new DZFDouble(0);
			if (balancevo == null) {
				temp2 = temp;
			} else {
				temp2 = SafeCompute.add(balancevo.getNnum(), temp);

			}
			if (temp2.compareTo(DZFDouble.ZERO_DBL) < 0)
				throw new BusinessException("库存量不足");
		}
	}

	private void saveZgIC(boolean isAdd, IntradeHVO invo, String pk_corp) {

		IntradeHVO newvo = createDashTntradeH(invo);
		if (!isAdd) {
			IntradeHVO[] hvos = getNextIntradeHVO(invo, pk_corp);
			if (hvos != null && hvos.length > 0) {
				// 删除子表
				IntradeHVO vo = hvos[0];
				String delsqlb = "delete from ynt_ictradein where pk_ictrade_h = ? and pk_corp = ?";
				SQLParameter sp = new SQLParameter();
				sp.addParam(vo.getPk_ictrade_h());
				sp.addParam(pk_corp);
				singleObjectBO.executeUpdate(delsqlb, sp);

				newvo.setPk_ictrade_h(vo.getPk_ictrade_h());
				// newvo.setDbillid(vo.getDbillid());
			}

		}
		singleObjectBO.saveObject(pk_corp, newvo);
	}

	private IntradeHVO[] getNextIntradeHVO(IntradeHVO invo, String pk_corp) {
		String condition = " nvl(dr,0)=0 and  sourcebilltype = ? and sourcebillid = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillTypeCode.HP70);
		sp.addParam(invo.getPrimaryKey());
		sp.addParam(pk_corp);
		IntradeHVO[] hvos = (IntradeHVO[]) singleObjectBO.queryByCondition(IntradeHVO.class, condition, sp);
		return hvos;

	}

	// CG-2
	public String getNewBillNo(String pk_corp, DZFDate billdate, String cbusitype) throws DZFWarpException {

		if (pk_corp == null || billdate == null) {
			throw new BusinessException("获取单据编号失败，公司或者日期为空!");
		}
		// 老数据默认采购单
		if (StringUtil.isEmpty(cbusitype)) {
			cbusitype = IcConst.CGTYPE;
		}
		String prefix = getPrefix(cbusitype);
		SQLParameter sp = new SQLParameter();
		// String month = billdate.getYear() + "-" + billdate.getStrMonth();
		String ym = billdate.getYear() + billdate.getStrMonth();
		sp.addParam(pk_corp);
		// sp.addParam(getPrefix(cbusitype));
		// sp.addParam(month);
		sp.addParam(IBillTypeCode.HP70);
		sp.addParam(cbusitype);
		// 加上docno like 'FR-2%'，排除垃圾数据单号
		// String sql = "select max(dbillid) as dbillid from ynt_ictrade_h where
		// pk_corp=? and dbillid like 'CG-2%' and substr(dbilldate,0,7)=? and
		// cbilltype = ?"; // 或docno
		// like
		// '201606%'
		String sql = "select to_number(substr(dbillid,10,length(dbillid))) as dbillid from ynt_ictrade_h where pk_corp=?  and dbillid like '"
				+ prefix + "-" + ym + "%' and cbilltype = ? and nvl(dr,0) = 0 and nvl(cbusitype,'" + IcConst.CGTYPE
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

		// if (!StringUtil.isEmptyWithTrim(maxDocNo)) {//按顺序增号逻辑
		// if (maxDocNo.length() >= 13) { // LA-2016060001
		// try {
		// flowno = Integer.parseInt(maxDocNo.substring(9)) + 1;
		// } catch (Exception e) {
		// flowno = 1;
		// }
		// }
		// }

		return String.format(prefix + "-%s%04d", ym, flowno);
	}

	/**
	 * 校验发票号码是否唯一（同一公司范围内
	 *
	 * @param vo
	 * @return
	 */
	private void checkInvidCodeIsUnique(IntradeHVO vo) {
		if (!StringUtil.isEmptyWithTrim(vo.getDinvid())) {// 存在才校验
			StringBuffer sf = new StringBuffer();
			sf.append(" Select 1 From ynt_ictrade_h y Where nvl(dr, 0) = 0 and pk_corp = ? and y.dinvid = ? ");// and
																												// y.cbilltype
																												// =
																												// ?
			SQLParameter sp = new SQLParameter();
			// sp.addParam(IBillTypeCode.HP70);//采购单
			sp.addParam(vo.getPk_corp());
			sp.addParam(vo.getDinvid());
			if (!StringUtil.isEmptyWithTrim(vo.getPk_ictrade_h())) {
				sf.append(" and y.pk_ictrade_h != ? ");
				sp.addParam(vo.getPk_ictrade_h());
			}
			boolean b = singleObjectBO.isExists(vo.getPk_corp(), sf.toString(), sp);

			if (b)
				throw new BusinessException("发票号[" + vo.getDinvid() + "]重复,请重新输入!");
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
		sp.addParam(IBillTypeCode.HP70);
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

	@Override
	public void delete(IntradeHVO data, String pk_corp) throws DZFWarpException {

		if (StringUtil.isEmpty(data.getPk_ictrade_h())) {
			return;
		}

		IntradeHVO hvo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, data.getPk_ictrade_h());

		if (hvo == null) {
			throw new BusinessException("该条数据不存在,请刷新后再试!");
		}

		check(hvo, pk_corp, false, false);

		deleteByIntradeHVO(hvo, pk_corp);

		if (hvo.getIszg() != null && hvo.getIszg().booleanValue()) {
			// 删除暂估对应负库存
			IntradeHVO[] hvos = getNextIntradeHVO(hvo, pk_corp);

			if (hvos != null && hvos.length > 0) {
				IntradeHVO vo = hvos[0];
				deletePz(hvos[0]);
				deleteByIntradeHVO(vo, pk_corp);
			}
		}

		// 如果来源进项发票
		if (!StringUtil.isEmpty(hvo.getSourcebillid()) && IBillTypeCode.HP95.equals(hvo.getSourcebilltype())) {
			vatincomserv.updateICStatus(hvo.getSourcebillid(), pk_corp, null);
		}
	}

	private void deleteByIntradeHVO(IntradeHVO hvo, String pk_corp) {

		// checkBeyond(hvo, new HashMap<String, DZFDouble>());
		// 先删除子表
		String delsqlb = "update ynt_ictradein set dr = 1 where pk_ictrade_h = ? and pk_corp = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(hvo.getPk_ictrade_h());
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(delsqlb, sp);
		// 删除主表

		String delsqlh = "update ynt_ictrade_h set dr = 1 where pk_ictrade_h = ? and pk_corp = ?";
		sp.clearParams();
		sp.addParam(hvo.getPk_ictrade_h());
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(delsqlh, sp);

	}

	// private IctradeinVO[] queryIctradeinVOByID(String pk_ictrade_h, String
	// pk_corp) throws DZFWarpException {
	// if (StringUtil.isEmpty(pk_ictrade_h)) {
	// return null;
	// }
	//
	// String condition = " nvl(dr,0)=0 and pk_ictrade_h = ?";
	// SQLParameter sp = new SQLParameter();
	// sp.addParam(pk_ictrade_h);
	// IctradeinVO[] bvos = (IctradeinVO[])
	// singleObjectBO.queryByCondition(IctradeinVO.class, condition, sp);
	// return bvos;
	// }

	@Override
	public IntradeHVO queryIntradeHVOByID(String pk_ictrade_h, String pk_corp) throws DZFWarpException {

		if (StringUtil.isEmpty(pk_ictrade_h)) {
			return null;
		}
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.* ,fb.name custname");
		sf.append(" From ynt_ictrade_h y ");
		sf.append(" left join ynt_fzhs_b fb ");
		sf.append(" on y.pk_cust = fb.pk_auacount_b and fb.pk_auacount_h = ? ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" ");
		sf.append(" and nvl(y.dr, 0) = 0 ");
		// sf.append(" and nvl(fb.dr, 0) = 0 ");
		sf.append(" and pk_ictrade_h = ? ");
		sp.addParam(AuxiliaryConstant.ITEM_SUPPLIER);// 供应商
		sp.addParam(pk_corp);
		sp.addParam(pk_ictrade_h);
		List<IntradeHVO> listVO = (List<IntradeHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IntradeHVO.class));
		IntradeHVO hvo = null;
		if (listVO != null && listVO.size() > 0) {

			hvo = listVO.get(0);

			sf.setLength(0);

			IctradeinVO[] bvos = queryIctradeinVO(pk_ictrade_h, pk_corp);
			hvo.setChildren(bvos);
		}
		return hvo;
	}

	@Override
	public IntradeHVO saveIntradeHVOToZz(IntradeHVO ihvo, CorpVO corpvo) throws DZFWarpException {
		IntradeHVO intradevo = queryIntradeHVOByID(ihvo.getPk_ictrade_h(), corpvo.getPk_corp());

		// String sourcetype = intradevo.getSourcebilltype();
		// if (IBillTypeCode.HP70.equals(sourcetype)) {
		// throw new BusinessException("冲暂估单据,不能转总账!");
		// }

		if (!StringUtil.isEmpty(intradevo.getCbusitype())
				&& IcConst.WGTYPE.equalsIgnoreCase(intradevo.getCbusitype())) {
			throw new BusinessException("完工入库单不能转总账");
		}

		check(intradevo, corpvo.getPk_corp(), false, true);
		// 暂估 需要生成下月初的负库存 下月暂估 本月暂估
		if (intradevo.getIszg() != null && intradevo.getIszg().booleanValue()) {
			String speriod = DateUtils.getPeriod(intradevo.getDbilldate());
			String zy = speriod + "月暂估";
			TzpzHVO headvo = createGLVO(intradevo, corpvo, zy);
			headvo = voucher.saveVoucher(corpvo, headvo);
			writeBackSale(intradevo, headvo);

			// 去掉下月冲暂估生成的凭证
			IntradeHVO[] hvos = getNextIntradeHVO(intradevo, corpvo.getPk_corp());
			if (hvos != null && hvos.length > 0) {
				IntradeHVO intradevo1 = (IntradeHVO) singleObjectBO.queryObject(hvos[0].getPrimaryKey(),
						new Class[] { IntradeHVO.class, IctradeinVO.class });

				if (StringUtil.isEmpty(intradevo1.getPzh())) {
					TzpzHVO nextvo = queryNextcode(intradevo1, corpvo, speriod);
					nextvo = voucher.saveVoucher(corpvo, nextvo);
					writeBackSale(intradevo1, nextvo);
				}
			}

		} else {
			String zy = "采购商品";
			if (intradevo.getIsczg() != null && intradevo.getIsczg().booleanValue()) {
				String speriod = DateUtils.getPreviousPeriod(DateUtils.getPeriod(intradevo.getDbilldate()));
				zy = "冲销" + speriod + "月暂估";
			}

			TzpzHVO headvo = createGLVO(intradevo, corpvo, zy);
			// headvo.setNbills(getNbills(intradevo.getPk_image_group(),
			// intradevo.getPk_corp()));

			if (IBillTypeCode.HP95.equals(intradevo.getSourcebilltype())) {
				headvo.setNbills(1);
			}else{
				headvo.setNbills(0);
			}

			headvo = voucher.saveVoucher(corpvo, headvo);
			writeBackSale(intradevo, headvo);
		}

		return null;
	}

	private TzpzHVO createGLVO(IntradeHVO ivo, CorpVO corp, String zy) {

		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(corp.getPk_corp());
		if (setvo == null) {
			throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
		}
		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(ivo.getPk_corp());
		List<TzpzBVO> bodyList = createTzpzBVO(ivo, null, zy, setvo, ccountMap, corp);

		if (bodyList == null || bodyList.size() == 0) {
			throw new BusinessException("生成凭证失败!");
		}
		DZFDouble totalDebit = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : bodyList) {
			totalDebit = SafeCompute.add(totalDebit, bvo.getDfmny());
		}
		TzpzHVO headVO = creatTzpzHVO(ivo, totalDebit, zy, ivo.getDbilldate());
		headVO.setChildren(bodyList.toArray(new TzpzBVO[0]));

		return headVO;
	}

	private List<TzpzBVO> createTzpzBVO(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp) {
		String priceStr = parameterserv.queryParamterValueByCode(corp.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		List<TzpzBVO> finBodyList = null;
		if (StringUtil.isEmpty(ivo.getCbusitype()) || ivo.getCbusitype().equals(IcConst.CGTYPE)) {

			finBodyList = createTzpzBVOCG(ivo, userid, zy, setvo, ccountMap, corp, iprice);
		} else if (ivo.getCbusitype().equals(IcConst.QTRTYPE)) {
			finBodyList = createTzpzBVOQTR(ivo, userid, zy, setvo, ccountMap, corp, iprice);
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

	private List<TzpzBVO> createTzpzBVOCG(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		String pk_accsunj = null;
		boolean isChargedept = !StringUtil.isEmpty(corp.getChargedeptname()) && corp.getChargedeptname().equals("一般纳税人")
				? true : false;

		// 暂估
		boolean iszg = ivo.getIszg() != null && ivo.getIszg().booleanValue() ? true : false;

		String colmun = null;
		// 一般纳税人有应交税费
		List<TzpzBVO> bodyList = null;
		if (isChargedept) {
			if (iszg) {
				colmun = "nymny";
				bodyList = createCommonTzpzBVO(ivo, null, userid, zy, colmun, 0, ccountMap, 0, corp, iprice);
				finBodyList.addAll(bodyList);//
			} else {
				// 一般人专票 显示税额
				if (ivo.getFp_style() == null || ivo.getFp_style().intValue() == IFpStyleEnum.SPECINVOICE.getValue()) {
					String cg_yjjxskm = setvo.getCg_yjjxskm();
					if (DZFValueCheck.isEmpty(ivo.getIsrz()) || !ivo.getIsrz().booleanValue()) {
						cg_yjjxskm = getyjjxskm(cg_yjjxskm, ivo, corp);
					} else {
						if (StringUtil.isEmpty(cg_yjjxskm)) {
							throw new BusinessException("应交进项税科目未设置!");
						}
						cg_yjjxskm = setvo.getCg_yjjxskm();
					}

					colmun = "nymny";
					bodyList = createCommonTzpzBVO(ivo, null, userid, zy, colmun, 0, ccountMap, 0, corp, iprice);
					finBodyList.addAll(bodyList);//

					bodyList = createCommonTzpzBVO(ivo, cg_yjjxskm, userid, zy, "ntaxmny", 0, ccountMap, 1, corp,
							iprice);
					finBodyList.addAll(bodyList);//
					colmun = "nymny&ntaxmny";
				} else {
					colmun = "nymny&ntaxmny";
					bodyList = createCommonTzpzBVO(ivo, null, userid, zy, colmun, 0, ccountMap, 0, corp, iprice);
					finBodyList.addAll(bodyList);//
				}

			}
		} else {
			if (iszg) {
				colmun = "nymny";
			} else {
				colmun = "nymny&ntaxmny";
			}
			bodyList = createCommonTzpzBVO(ivo, null, userid, zy, colmun, 0, ccountMap, 0, corp, iprice);
			finBodyList.addAll(bodyList);//
		}

		if (ivo.getIpayway() == 0) {// 现金
			if (StringUtil.isEmpty(setvo.getCg_xjfkkm())) {
				throw new BusinessException("现金付款科目未设置!");
			}
			pk_accsunj = setvo.getCg_xjfkkm();
		} else if (ivo.getIpayway() == 1) { // 往来
			if (StringUtil.isEmpty(setvo.getCg_yfzkkm())) {
				throw new BusinessException("应付账款科目未设置!");
			}
			pk_accsunj = setvo.getCg_yfzkkm();
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
			throw new BusinessException("获取贷方科目出错!");
		}

		// 暂估 取暂估入库贷方科目
		if (iszg) {
			pk_accsunj = setvo.getZgrkdfkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("暂估入库贷方科目未设置!");
			}
			YntCpaccountVO cvo = ccountMap.get(pk_accsunj);
			if (cvo != null) {
				boolean isleaf = cvo.getIsleaf() == null ? false : cvo.getIsleaf().booleanValue();
				if (!isleaf) {
					ivo.setPzstatus(IVoucherConstants.TEMPORARY);// 暂存态
				}
			}

		}

		bodyList = createCommonTzpzBVO(ivo, pk_accsunj, userid, zy, colmun, 1, ccountMap, 2, corp, iprice);
		finBodyList.addAll(bodyList);//

		return finBodyList;
	}

	private String getyjjxskm(String cg_yjjxskm, IntradeHVO ivo, CorpVO corp) {
		String corptype = corp.getCorptype();// 取科目方案
		/// 小企业会计准则：222110_待抵扣进项税 企业会计准则：222110_待抵扣进项税
		if ("00000100AA10000000000BMD".equals(corptype) || "00000100AA10000000000BMF".equals(corptype)) {

			YntCpaccountVO accvo = getAccVOByCode("222113", corp.getPrimaryKey());

			if (DZFValueCheck.isEmpty(accvo)) {
				accvo = getAccVOByCode("222110", corp.getPrimaryKey());
			}

			if (DZFValueCheck.isEmpty(accvo)) {
				throw new BusinessException("222110_待抵扣进项税科目不存在!");
			} else {
				cg_yjjxskm = accvo.getPrimaryKey();
			}
			if (DZFValueCheck.isEmpty(cg_yjjxskm)) {
				throw new BusinessException("222110_待抵扣进项税科目不存在!");
			}
		} else {
			if (StringUtil.isEmpty(cg_yjjxskm)) {
				throw new BusinessException("应交进项税科目未设置!");
			}
		}
		return cg_yjjxskm;
	}

	private YntCpaccountVO getAccVOByCode(String acccode, String pk_corp) {
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		String olerule = DZFConstant.ACCOUNTCODERULE;
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
				.getBean("gl_accountcoderule");
		String newaccount = gl_accountcoderule.getNewRuleCode(acccode, olerule, newrule);
		SQLParameter sp = new SQLParameter();
		sp.addParam(newaccount);
		sp.addParam(pk_corp);

		String condition = "  accountcode=? and pk_corp=? and nvl(dr,0)=0 ";
		YntCpaccountVO[] gsjfkmVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, condition,
				sp);
		if (gsjfkmVOs == null || gsjfkmVOs.length < 1) {
			return null;
		}
		return gsjfkmVOs[0];
	}

	private List<TzpzBVO> createTzpzBVOQTR(IntradeHVO ivo, String userid, String zy, InvAccSetVO setvo,
			Map<String, YntCpaccountVO> ccountMap, CorpVO corp, int iprice) {
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> bodyList = null;
		zy = "其他入库";
		if (StringUtil.isEmpty(setvo.getVdef1())) {
			throw new BusinessException("其他入库科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getVdef1(), userid, zy, "nymny", 0, ccountMap, 0, corp, iprice);
		finBodyList.addAll(bodyList);//

		if (StringUtil.isEmpty(setvo.getVdef2())) {
			throw new BusinessException("其他入库对方科目未设置!");
		}
		bodyList = createCommonTzpzBVO(ivo, setvo.getVdef2(), userid, zy, "nymny", 1, ccountMap, 1, corp, iprice);
		finBodyList.addAll(bodyList);//
		return finBodyList;
	}

	private List<TzpzBVO> createCommonTzpzBVO(IntradeHVO ivo, String pk_accsubj, String userid, String zy,
			String column1, int vdirect, Map<String, YntCpaccountVO> ccountMap, int rowno, CorpVO corp, int iprice) {
		SuperVO[] ibodyvos = (SuperVO[]) ivo.getChildren();
		if (ibodyvos == null || ibodyvos.length == 0)
			return null;

		String column2 = null;
		String column3 = null;

		List<TzpzBVO> list = new ArrayList<>();
		// 转换成凭证vo
		DZFDouble taxratio = (DZFDouble) ibodyvos[0].getAttributeValue("ntax");
		for (SuperVO body : ibodyvos) {
			IctradeinVO ibody = (IctradeinVO) body;
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
			if (nmny.compareTo(DZFDouble.ZERO_DBL) != 0) {
				TzpzBVO bvo = createSingleTzpzBVO(cvo, zy, ivo, vdirect, nmny, ibody, corp, iprice);
				bvo.setRowno(rowno);
				list.add(bvo);
			}
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

		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();

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

	private TzpzBVO createSingleTzpzBVO(YntCpaccountVO cvo, String zy, IntradeHVO ivo, int vdirect,
			DZFDouble totalDebit, IctradeinVO ibody, CorpVO corp, int iprice) {
		TzpzBVO depvo = new TzpzBVO();
		depvo.setPk_accsubj(cvo.getPk_corp_account());
		depvo.setVcode(cvo.getAccountcode());
		depvo.setVname(cvo.getAccountname());
		depvo.setZy(zy);// 摘要
		// if (cvo.getIsfzhs().charAt(0) == '1') {
		// if (StringUtil.isEmpty(ivo.getPk_cust())) {
		// throw new BusinessException("科目【" + cvo.getAccountname() +
		// "】启用客户辅助核算,客户必须录入!");
		// }
		// depvo.setFzhsx1(ivo.getPk_cust());
		// }

		if (cvo.getIsfzhs().charAt(1) == '1') {
			if (StringUtil.isEmpty(ivo.getPk_cust())) {
				throw new BusinessException("科目【" + cvo.getAccountname() + "】启用供应商辅助核算,供应商必须录入!");
			}
			depvo.setFzhsx2(ivo.getPk_cust());
		}

		// StringBuffer strb = new StringBuffer();
		// if (Kmschema.isKcspbm(corp.getCorptype(), cvo.getAccountcode())
		// || Kmschema.isKcspbm(corp.getCorptype(), cvo.getAccountcode())) {
		// if (cvo.getIsfzhs().charAt(5) != '1') {
		// strb.append("科目【" + cvo.getAccountname() + "】请启用存货辅助核算");
		// }
		//
		// if (cvo.getIsnum() == null || !cvo.getIsnum().booleanValue()) {
		// if (strb.length() > 0) {
		// strb.append(",数量核算");
		// }else{
		// strb.append("科目【" + cvo.getAccountname() + "】请启用数量核算");
		// }
		// }
		//
		// if (strb.length() > 0) {
		// strb.append("!");
		// throw new BusinessException(strb.toString());
		// }
		// }
		if (cvo.getIsfzhs().charAt(5) == '1') {
			depvo.setPk_inventory(ibody.getPk_inventory());
		}
		totalDebit = totalDebit.setScale(2, DZFDouble.ROUND_HALF_UP);
		depvo.setVdirect(vdirect);
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

		// DZFDouble taxratio = SafeCompute.div(ibody.getNtax(), new
		// DZFDouble(100));
		// TaxitemParamVO taxparam = new
		// TaxitemParamVO.Builder(depvo.getPk_corp(),
		// taxratio).UserId(ivo.getCreator())
		// .InvName(ibody.getInvname()).Fp_style(null).build();
		// TaxItemUtil.dealTaxItem(depvo, taxparam, cvo);
		return depvo;

	}

	private TzpzHVO creatTzpzHVO(IntradeHVO ivo, DZFDouble totalDebit, String zy, DZFDate pzdate) {
		TzpzHVO headVO = new TzpzHVO();
		headVO.setPk_corp(ivo.getPk_corp());
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(totalDebit);
		headVO.setDfmny(totalDebit);
		headVO.setCoperatorid(ivo.getCreator());
		headVO.setIshasjz(DZFBoolean.FALSE);
		// DZFDate nowDate = DZFDate.getDate(new
		// Long(InvocationInfoProxy.getInstance().getDate())) ;
		// headVO.setDoperatedate(nowDate) ;
		String speriod = DateUtils.getPeriod(pzdate);
		// DZFDate nowDatevalue = getPeroidDZFDate(speriod);
		// DZFDate date = new DZFDate();
		headVO.setDoperatedate(pzdate);
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(ivo.getPk_corp(), pzdate));
		if (ivo.getPzstatus() != null) {
			headVO.setVbillstatus(ivo.getPzstatus());
		} else {
			headVO.setVbillstatus(8);// 默认自由态
		}

		// 记录单据来源
		headVO.setSourcebillid(ivo.getPrimaryKey());
		headVO.setSourcebilltype(ivo.getCbilltype());
		headVO.setPk_image_group(ivo.getPk_image_group());
		headVO.setPk_image_library(ivo.getPk_image_library());
		headVO.setPeriod(speriod);
		headVO.setVyear(Integer.valueOf(speriod.substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		headVO.setMemo(zy);
		headVO.setFp_style(ivo.getFp_style());// 1普票 2专票3未开票

		return headVO;
	}

	/**
	 * 生成下月冲销暂估凭证
	 */
	private TzpzHVO queryNextcode(IntradeHVO ivo, CorpVO corpvo, String speriod) throws BusinessException {
		// String speriod = DateUtils.getPeriod(ivo.getDbilldate());
		String zy = "冲销" + speriod + "月暂估";
		TzpzHVO headvo = createGLVO(ivo, corpvo, zy);
		return headvo;
	}

	/**
	 * 取期间所属月的最后一天
	 */
	private DZFDate getPeroidDZFDate(String speriod) {
		DZFDate period = new DZFDate(speriod + "-01");
		period = new DZFDate(speriod + "-" + period.getDaysMonth());
		return period;
	}

	/**
	 * 查询库存商品VO
	 */
	@SuppressWarnings("unchecked")
	private Map<String, TempInvtoryVO> queryInventoryVO(List<String> list) {
		StringBuffer sf = new StringBuffer();
		sf.append(
				" select t1.name,t2.accountname,t1.pk_inventory, t2.accountcode,t2.pk_corp_account,t2.isfzhs,t2.isnum from ynt_inventory t1 ");
		sf.append(" join ynt_cpaccount t2 on t1.pk_subject = t2.pk_corp_account ");
		sf.append(" and  t1.pk_corp = t2.pk_corp and nvl(t1.dr,0) = 0 and nvl(t2.dr,0) = 0 ");
		sf.append(" where  ");
		sf.append(SqlUtil.buildSqlForIn("t1.pk_inventory", list.toArray(new String[list.size()])));
		Map<String, TempInvtoryVO> map = (Map<String, TempInvtoryVO>) singleObjectBO.executeQuery(sf.toString(), null,
				new ResultSetProcessor() {
					@Override
					public Object handleResultSet(ResultSet rs) throws SQLException {

						Map<String, TempInvtoryVO> map = new HashMap<String, TempInvtoryVO>();
						TempInvtoryVO vo = null;
						while (rs.next()) {
							vo = new TempInvtoryVO();
							String name = rs.getString("name");
							String accountname = rs.getString("accountname");
							String accountcode = rs.getString("accountcode");
							String pk_corp_account = rs.getString("pk_corp_account");
							String pk_inventory = rs.getString("pk_inventory");
							String isfzhs = rs.getString("isfzhs");
							String isnum = rs.getString("isnum");
							vo.setKmname(accountname);
							vo.setInvname(name);
							vo.setKmbm(accountcode);
							vo.setKmid(pk_corp_account);
							vo.setFzhs(isfzhs);
							vo.setIsnum(isnum);
							map.put(pk_inventory, vo);
						}
						return map;
					}

				});
		return map;
	}

	private void writeBackSale(IntradeHVO ivo, TzpzHVO headvo) {
		ivo.setPzid(headvo.getPrimaryKey());
		ivo.setPzh(headvo.getPzh());
		ivo.setDjzdate(new DZFDate());
		ivo.setIsjz(DZFBoolean.TRUE);

		List<IctradeinVO> list = new ArrayList<IctradeinVO>();

		TzpzBVO[] bvos = (TzpzBVO[]) headvo.getChildren();
		SuperVO[] ibodyvos = ivo.getChildren();

		for (SuperVO ibvo : ibodyvos) {
			for (TzpzBVO bvo : bvos) {
				IctradeinVO ibody = (IctradeinVO) ibvo;
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

		singleObjectBO.updateAry(list.toArray(new IctradeinVO[list.size()]),
				new String[] { "pk_voucher", "pzh", "pk_voucher_b", "zy" });

		singleObjectBO.update(ivo);

		// 如果来源于进项
		vatincomserv.updatePZH(headvo);

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

	public void check(IntradeHVO hvo, String pk_corp, boolean iscopy, boolean istogl) throws DZFWarpException {

		if (!StringUtil.isEmpty(hvo.getPk_corp())) {
			if (!hvo.getPk_corp().equals(pk_corp)) {
				throw new BusinessException("没有操作该条数据的权限!");
			}
		}

		if (!iscopy) {
			if (hvo.getIsjz() != null && hvo.getIsjz().booleanValue()) {
				throw new BusinessException("已经转总账,不允许操作!");
			}

			if (!istogl) {
				String sourcetype = hvo.getSourcebilltype();
				if (IBillTypeCode.HP70.equals(sourcetype)) {
					throw new BusinessException("冲暂估单据,不允许操作!");
				}
			}
		} else {
			if (!StringUtil.isEmpty(hvo.getCbusitype()) && IcConst.WGTYPE.equalsIgnoreCase(hvo.getCbusitype())) {
				throw new BusinessException("完工入库单不能复制!");
			}
		}
	}

	private IntradeHVO createDashTntradeH(IntradeHVO vo) throws DZFWarpException {
		IntradeHVO dashvo = new IntradeHVO();
		SuperVO[] intradeinvos = (SuperVO[]) vo.getChildren();
		IctradeinVO[] dashinvos = new IctradeinVO[intradeinvos.length];

		// 主表

		// DZFDate date =new DZFDate();
		String speriod = DateUtils.getPeriod(vo.getDbilldate());
		DZFDate doperatedate = getPeroidDZFDate(speriod).getDateAfter(1);
		dashvo.setPk_corp(vo.getPk_corp());
		dashvo.setDbilldate(doperatedate);
		dashvo.setDbillid(getNewBillNo(vo.getPk_corp(), doperatedate, vo.getCbusitype()));// 单据编号
		dashvo.setPk_currency(DzfUtil.PK_CNY);
		dashvo.setPk_cust(vo.getPk_cust());
		dashvo.setNmny(vo.getNmny() == null ? null : vo.getNmny().multiply(-1));
		dashvo.setNtaxmny(vo.getNtaxmny() == null ? null : vo.getNtaxmny().multiply(-1));
		dashvo.setNtotaltaxmny(vo.getNtotaltaxmny() == null ? null : vo.getNtotaltaxmny().multiply(-1));
		dashvo.setCreator(vo.getCreator());
		dashvo.setIszg(DZFBoolean.FALSE);
		dashvo.setIarristatus(vo.getIarristatus());
		dashvo.setIpayway(vo.getIpayway());
		dashvo.setDinvdate(vo.getDinvdate());
		dashvo.setDinvid(vo.getDinvid());
		dashvo.setIsjz(DZFBoolean.FALSE);
		dashvo.setDjzdate(doperatedate);
		dashvo.setCbilltype(IBillTypeCode.HP70);
		dashvo.setModifydate(doperatedate);
		dashvo.setDr(0);
		dashvo.setSourcebillid(vo.getPrimaryKey());
		dashvo.setSourcebilltype(vo.getCbilltype());
		dashvo.setIsczg(DZFBoolean.TRUE);
		dashvo.setCbusitype(vo.getCbusitype());
		dashvo.setFp_style(vo.getFp_style());
		// 子表
		IctradeinVO dashinvo = null;
		for (int i = 0; i < intradeinvos.length; i++) {
			dashinvo = new IctradeinVO();
			IctradeinVO invo = (IctradeinVO) intradeinvos[i];
			dashinvo.setPk_inventory(invo.getPk_inventory());
			dashinvo.setDbilldate(dashvo.getDbilldate());
			dashinvo.setPk_corp(vo.getPk_corp());
			dashinvo.setPk_billmaker(invo.getPk_billmaker());
			dashinvo.setPk_currency(vo.getPk_currency());
			dashinvo.setCbilltype(vo.getCbilltype());
			dashinvo.setNnum(invo.getNnum() == null ? null : invo.getNnum().multiply(-1));
			dashinvo.setNymny(invo.getNymny() == null ? null : invo.getNymny().multiply(-1));
			dashinvo.setNcost(invo.getNcost() == null ? null : invo.getNcost().multiply(-1));
			dashinvo.setNtax(invo.getNtax());
			dashinvo.setNtaxmny(invo.getNtaxmny() == null ? null : invo.getNtaxmny().multiply(-1));
			dashinvo.setNtotaltaxmny(invo.getNtotaltaxmny() == null ? null : invo.getNtotaltaxmny().multiply(-1));
			dashinvo.setNprice(invo.getNprice());
			dashinvo.setDr(0);
			dashinvo.setPk_subject(invo.getPk_subject());
			dashinvo.setCbusitype(dashvo.getCbusitype());
			dashinvos[i] = dashinvo;
		}

		dashvo.setChildren(dashinvos);

		return dashvo;

	}

	@Override
	public void rollbackIntradeHVOToZz(IntradeHVO data, CorpVO corpvo) throws DZFWarpException {

		IntradeHVO intradevo = (IntradeHVO) singleObjectBO.queryByPrimaryKey(IntradeHVO.class, data.getPrimaryKey());

		// String sourcetype = intradevo.getSourcebilltype();
		// if (IBillTypeCode.HP70.equals(sourcetype)) {
		// throw new BusinessException("冲暂估单据,不能取消转总账!");
		// }

		if (intradevo == null)
			throw new BusinessException("正在处理中，请刷新重试！");

		if (!StringUtil.isEmpty(intradevo.getCbusitype())
				&& IcConst.WGTYPE.equalsIgnoreCase(intradevo.getCbusitype())) {
			throw new BusinessException("完工入库单不能取消转总账");
		}
		checkCanl(intradevo, corpvo);

		deletePz(intradevo);
	}

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

	// 凭证删除更新库存
	public void deleteIntradeBill(TzpzHVO pzHeadVO) {

		// String condition = " nvl(dr,0)=0 and cbilltype = ? and pk_ictrade_h =
		// ? and pk_corp = ? ";
		String condition = " nvl(dr,0)=0 and  cbilltype = ? and pzid = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillTypeCode.HP70);
		// sp.addParam(pzHeadVO.getSourcebillid());
		sp.addParam(pzHeadVO.getPrimaryKey());
		sp.addParam(pzHeadVO.getPk_corp());
		IntradeHVO[] hvos = (IntradeHVO[]) singleObjectBO.queryByCondition(IntradeHVO.class, condition, sp);

		if (hvos == null || hvos.length == 0) {
			return;
			// throw new BusinessException("没有相应的库存单据!");
		}

		// IntradeHVO head = hvos[0];

		sp.clearParams();
		sp.addParam(pzHeadVO.getPk_tzpz_h());
		String sql = "update  ynt_ictrade_h set pzid = null ,pzh=null,djzdate=null,isjz='N'  where pzid=? ";
		singleObjectBO.executeUpdate(sql, sp);

		sql = "update  ynt_ictradein set pzh = null ,zy=null,pk_voucher_b=null,pk_voucher=null  where pk_voucher=? ";
		singleObjectBO.executeUpdate(sql, sp);

		boolean flag = false;
		// 去掉暂估的取消转总账 2017-08-29
		for (IntradeHVO head : hvos) {
			if (head.getIszg() != null && head.getIszg().booleanValue()) {
				IntradeHVO[] nexts = getNextIntradeHVO(head, head.getPk_corp());
				if (nexts != null && nexts.length > 0) {
					deletePz(nexts[0]);
				}
			}

			// 入库单如果来源于进项发票，也需要删除进项的凭证号
			if (flag) {
				break;
			}
			if (IBillTypeCode.HP95.equals(head.getSourcebilltype())) {
				vatincomserv.deletePZH(pzHeadVO.getPk_corp(), pzHeadVO.getPk_tzpz_h());
				flag = true;
			}

		}

	}

	// 反操作校验
	private void checkCanl(IntradeHVO intradevo, CorpVO corpvo) {

		String pk_corp = corpvo.getPk_corp();
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

		boolean isgz = qmgzService.isGz(vo.getPk_corp(), vo.getPeriod().toString());
		if (isgz) {// 是否关账
			throw new BusinessException(
					"公司" + corpvo.getUnitname() + "在" + vo.getPeriod().toString() + "月份已关账，不能取消转总账");
		}

		// if (vo.getIscbjz() != null && vo.getIscbjz().booleanValue()) {
		// throw new BusinessException("已经成本结转，请先反成本结转");
		// }
		//
		// if (vo.getIsqjsyjz() != null && vo.getIsqjsyjz().booleanValue()) {
		// throw new BusinessException("已经损益结转，请先反损益结转");
		// }

	}

	@Override
	public IctradeinVO[] queryIctradeinVO(String pk_ictrade_h, String pk_corp) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.* ,fb.name invname,fb.code invcode, fb.invtype,fb.invspec");
		sf.append(" ,me.name measure ,fb.pk_subject,ct.accountname kmmc,ct.accountcode kmbm,fy.name invclassname");
		sf.append(" From ynt_ictradein y ");
		sf.append(" left join ynt_inventory fb ");
		sf.append(" on y.pk_inventory = fb.pk_inventory and nvl(fb.dr, 0) = 0 ");
		sf.append(" left join ynt_cpaccount ct on fb.pk_subject = ct.pk_corp_account and nvl(ct.dr, 0) = 0 ");
		sf.append(" left join ynt_measure me on fb.pk_measure = me.pk_measure and nvl(me.dr, 0) = 0 ");
		sf.append(" left join ynt_invclassify fy on fb.pk_invclassify = fy.pk_invclassify and  nvl(fy.dr, 0) = 0  ");
		sf.append(" where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr, 0) = 0 ");

		sf.append(" and pk_ictrade_h = ? ");
		sp.addParam(pk_corp);
		sp.addParam(pk_ictrade_h);
		List<IctradeinVO> listBVO = (List<IctradeinVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IctradeinVO.class));
		if (listBVO == null || listBVO.size() == 0) {
			return null;
		}
		return listBVO.toArray(new IctradeinVO[listBVO.size()]);
	}

	@Override
	public Object saveIntradeHVOToZz(IntradeHVO[] datas, CorpVO corpvo) throws DZFWarpException {

		String pk_corp = corpvo.getPk_corp();
		String zy = "采购商品";
		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(pk_corp);

		if (setvo == null) {
			throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
		}

		// Map<String, TzpzBVO> map = new HashMap<String, TzpzBVO>();
		List<TzpzBVO> bodyList = new ArrayList<>();
		List<IntradeHVO> inlist = new ArrayList<>();
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		DZFDate pzdate = null;
		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
		List<String> imageGroupList = new ArrayList<>();

		int nbills = 0;
		for (IntradeHVO ivo : datas) {
			if (!StringUtil.isEmpty(ivo.getPk_image_group()) && !imageGroupList.contains(ivo.getPk_image_group())) {
				imageGroupList.add(ivo.getPk_image_group());
			}

			if (IBillTypeCode.HP95.equals(ivo.getSourcebilltype())) {
				nbills++;
			}

			IntradeHVO intradevo = queryIntradeHVOByID(ivo.getPk_ictrade_h(), pk_corp);
			check(intradevo, pk_corp, false, true);
			// checkBeyond(intradevo, new HashMap<String, DZFDouble>());
			List<TzpzBVO> list = createTzpzBVO(intradevo, null, zy, setvo, ccountMap, corpvo);
			if (list == null || list.size() == 0) {
				throw new BusinessException("生成凭证失败!");
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
			throw new BusinessException("生成凭证失败!");
		}

		sortTzpz(bodyList);
		Map<String, TzpzBVO> map1 = new LinkedHashMap();

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
					price = SafeCompute.div(vo.getJfmny(), vo.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
					map1.put(key, temp);
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

		TzpzHVO headvo = creatTzpzHVO(inlist.get(0), totalDebit, zy, pzdate);
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
		// headvo.setNbills(getNbills(groupId, pk_corp));
		headvo.setNbills(nbills);
		headvo.setChildren(bvos);
		headvo = voucher.saveVoucher(corpvo, headvo);

		for (IntradeHVO ivo : inlist) {
			writeBackSale(ivo, headvo);
		}
		return null;
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
			prefix = "CG";
		} else {
			if (cbusitype.equals(IcConst.CGTYPE)) {
				prefix = "CG";
			} else if (cbusitype.equals(IcConst.WGTYPE)) {
				prefix = "WG";
			} else if (cbusitype.equals(IcConst.QTRTYPE)) {
				prefix = "QT";
			} else {
				prefix = "CG";
			}
		}
		return prefix;
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
			IctradeinVO invo = (IctradeinVO) child;
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
		String strids = SqlUtil.buildSqlConditionForIn(DZFStringUtil.getString2Array(pk_ictrade_h, ","));
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select y.*,t.*,fb.name vcustname,ry.code invcode,ry.name invname, ");
		sf.append(" ry.invspec,ry.invtype,re.name measure,fy.name invclassname,yt.bankname vbankaccname");
		sf.append(" From ynt_ictrade_h y ");
		sf.append(" join ynt_ictradein t on y.pk_ictrade_h= t.pk_ictrade_h ");
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
		sp.addParam(AuxiliaryConstant.ITEM_SUPPLIER);// 供应商
		sp.addParam(pk_corp);

		List<AggIcTradeVO> listVO = (List<AggIcTradeVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(AggIcTradeVO.class));
		CorpVO corpvo = corpService.queryByPk(pk_corp);

		for (AggIcTradeVO vo : listVO) {
			vo.setVcorpname(corpvo.getUnitname());

			if (IcConst.CGTYPE.equals(vo.getCbusitype())) {
				if (vo.getIpayway() != null) {
					IcPayWayEnum wayEnum = IcPayWayEnum.getTypeEnumByValue(vo.getIpayway());
					if (wayEnum != null) {
						vo.setVpaywayname(wayEnum.getName());
					}
				}
			}
			if (StringUtil.isEmpty(vo.getIszg()) || "N".equalsIgnoreCase(vo.getIszg())) {
				vo.setIszg("否");
			} else {
				vo.setIszg("是");
			}

			if (StringUtil.isEmpty(vo.getCbusitype())) {
				vo.setCbusitype(IcBillTypeEnum.CGTYPE.getName());
			} else {
				if (IcBillTypeEnum.getTypeEnumByValue(vo.getCbusitype()) == null) {
					vo.setCbusitype(IcBillTypeEnum.CGTYPE.getName());
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
	public String saveImp(MultipartFile file, String pk_corp, String fileType, String cuserid) throws DZFWarpException {
		InputStream is = null;
		try {
			is = file.getInputStream();
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			Sheet sheet1 = impBook.getSheetAt(0);

			Map<Integer, String> fieldColumn = IctradeinVO.getExcelFieldColumn();
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
			AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_SUPPLIER, pk_corp, null);

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
					// String billid = vo.getDbillid();
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
					saveSaleList(entry.getValue(), corpvo, cuserid);
				} catch (BusinessException e) {
					msg.append(
							"<p>单据号为" + entry.getValue().get(0).getDbillid() + "的入库单据," + e.getMessage() + ",请检查！</p>");
				} catch (Exception e) {
					msg.append("<p>单据号为" + entry.getValue().get(0).getDbillid() + "的入库单据,出现未知异常,请检查！</p>");
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

	private void saveSaleList(List<AggIcTradeVO> list, CorpVO corp, String cuserid) {

		if (list == null || list.size() == 0)
			return;
		Map<String, Integer> preMap = getPreMap(corp.getPk_corp());

		IntradeHVO headvo = new IntradeHVO();
		List<IctradeinVO> clist = new ArrayList<>();
		Integer num = preMap.get(IParameterConstants.DZF009);// 数量
		Integer price = preMap.get(IParameterConstants.DZF010);// 单价
		int i = 0;
		for (AggIcTradeVO vo : list) {
			if (i == 0) {
				headvo.setDbilldate(new DZFDate(vo.getDbilldate()));
				headvo.setDbillid(vo.getDbillid());
				headvo.setCbusitype(vo.getCbusitype());
				headvo.setIpayway(vo.getIpayway());
				headvo.setPk_corp(corp.getPk_corp());
				headvo.setIarristatus(1);

				headvo.setPk_cust(vo.getPk_cust());
				headvo.setPk_bankaccount(vo.getPk_bankaccount());
				headvo.setDinvdate(vo.getDinvdate());
				headvo.setDinvid(vo.getDinvid());
				headvo.setCreator(cuserid);
				headvo.setIszg(new DZFBoolean(vo.getIszg()));
				if (IFpStyleEnum.getTypeEnumByName(vo.getFp_style()) == null) {
					headvo.setFp_style(IFpStyleEnum.NOINVOICE.getValue());
				} else {
					headvo.setFp_style(IFpStyleEnum.getTypeEnumByName(vo.getFp_style()).getValue());
				}
				boolean isChargedept = !StringUtil.isEmpty(corp.getChargedeptname())
						&& corp.getChargedeptname().equals("一般纳税人") ? true : false;

				// 非暂估
				boolean iszg = headvo.getIszg() != null && headvo.getIszg().booleanValue() ? true : false;

				if (isChargedept) {
					if (!iszg) {
						// 一般人专票 默认认证
						if (headvo.getFp_style() == null
								|| headvo.getFp_style().intValue() == IFpStyleEnum.SPECINVOICE.getValue()) {
							headvo.setIsrz(DZFBoolean.TRUE);
						}
					}
				}
			}
			IctradeinVO childvo = new IctradeinVO();
			childvo.setPk_inventory(vo.getPk_inventory());
			childvo.setPk_subject(vo.getPk_subject());
			childvo.setNnum(vo.getNnum().setScale(num, DZFDouble.ROUND_HALF_UP));
			childvo.setNprice(
					SafeCompute.div(vo.getNymny(), childvo.getNnum()).setScale(price, DZFDouble.ROUND_HALF_UP));
			childvo.setNymny(vo.getNymny());
			childvo.setNtax(vo.getNtax());
			childvo.setNtaxmny(vo.getNtaxmny());
			childvo.setNtotaltaxmny(SafeCompute.add(vo.getNymny(), vo.getNtaxmny()));
			clist.add(childvo);
		}
		headvo.setChildren(clist.toArray(new IctradeinVO[clist.size()]));
		save(headvo, true);

	}

	private void checkImpData(AggIcTradeVO vo, Map<String, InvclassifyVO> invclassmap, Map<String, MeasureVO> jldwmap,
			Map<String, InventoryVO> invmap, Map<String, BankAccountVO> bankmap,
			Map<String, AuxiliaryAccountBVO> accmap, CorpVO corpvo) {

		if (vo.getDbilldate() == null) {
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

		if (IcConst.CGTYPE.equals(vo.getCbusitype())) {
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
					throw new BusinessException("供应商不存在");
				} else {
					vo.setPk_cust(bvo.getPk_auacount_b());
				}
			}

			if (vo.getIszg() == null || "否".equals(vo.getIszg())) {
				vo.setIszg("N");
			} else {
				vo.setIszg("Y");
			}

		} else {
			vo.setIpayway(IcPayWayEnum.CASH.getValue());
			vo.setDinvdate(null);
			vo.setDinvid(null);
			vo.setNtax(null);
			vo.setNtaxmny(null);
			vo.setIszg("N");
		}

		if (vo.getNprice() == null)
			throw new BusinessException("单价不能为空");
		if (vo.getNymny() == null)
			throw new BusinessException("金额不能为空");

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

	private String getbillkey(AggIcTradeVO vo) {
		// 单据日期、公司、入库类型、单据编码、付款方式、发票号、发票日期、银行账户、供应商、是否暂估列
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
		strb.append(vo.getIszg());
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

	@Override
	public List<TempInvtoryVO> queryZgVOs(CorpVO corpvo, String userid, DZFDate doped) throws DZFWarpException {
		// 查询最小未成本结转期间
		String period = queryMaxCbjzPeriod(corpvo);

		// 查询该期间期末处理数据
		String pk_corp = corpvo.getPk_corp();
		List<String> corppks = new ArrayList<String>();
		corppks.add(pk_corp);

		Map<String, IcbalanceVO> map = ic_rep_cbbserv
				.queryLastBanlanceVOs_byMap1(DateUtils.getPeriodEndDate(period).toString(), pk_corp, null, true);// 根据单据日期查询
		if (map == null || map.size() == 0) {
			return null;
		}

		Map<String, IcbalanceVO> lastmap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(
				DateUtils.getPeriodEndDate(DateUtils.getPreviousPeriod(period)).toString(), pk_corp, null, true);// 根据单据日期查询上期结存

		String begindate = DateUtils.getPeriodStartDate(period).toString();
		String enddate = DateUtils.getPeriodEndDate(period).toString();

		List<IcbalanceVO> outlist = queryCurrentPeriodSaleOut(begindate, enddate, pk_corp);
		Map<String, IcbalanceVO> outMap = DZfcommonTools.hashlizeObjectByPk(outlist, new String[] { "pk_inventory" });

		List<IcbalanceVO> inlist = queryCurrentPeriodPurchIn(begindate, enddate, pk_corp);
		Map<String, IcbalanceVO> inMap = DZfcommonTools.hashlizeObjectByPk(inlist, new String[] { "pk_inventory" });

		List<InventoryVO> invlist = ic_invserv.query(pk_corp);
		Map<String, InventoryVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invlist, new String[] { "pk_inventory" });
		List<TempInvtoryVO> list = new ArrayList<>();
		IcbalanceVO zgvo = null;

		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);

		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(pk_corp);

		String pk_accsubj = setvo.getCg_yfzkkm();
		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
		YntCpaccountVO cvo = ccountMap.get(pk_accsubj);

		if (cvo == null)
			throw new BusinessException("入账科目不存在");
		boolean isleaf = cvo.getIsleaf() == null ? false : cvo.getIsleaf().booleanValue();
		if (!isleaf) {//
			// 第一个下级的最末级
			cvo = getFisrtNextLeafAccount(cvo.getAccountcode(), ccountMap);
		}
		for (Map.Entry<String, IcbalanceVO> entry : map.entrySet()) {
			IcbalanceVO balvo = entry.getValue();

			if (balvo == null || balvo.getNnum() == null || balvo.getNnum().doubleValue() >= 0)
				continue;
			TempInvtoryVO vo = new TempInvtoryVO();
			vo.setId(UUID.randomUUID().toString());
			if (tempMap != null && tempMap.size() > 0) {
				if (tempMap.get(balvo.getPk_inventory()) != null)
					vo.setKmid(tempMap.get(balvo.getPk_inventory()).getPk_subject());
			}
			vo.setPk_invtory(balvo.getPk_inventory());
			vo.setSpbm(balvo.getInventorycode());
			vo.setInvname(balvo.getInventoryname());
			String xh = balvo.getInvtype();

			if (StringUtil.isEmpty(xh)) {
				vo.setSpgg(balvo.getInvspec());
			} else {
				if (StringUtil.isEmpty(balvo.getInvspec())) {
					vo.setSpgg("(" + xh + ")");
				} else {
					vo.setSpgg(balvo.getInvspec() + "(" + xh + ")");
				}
			}

			// 设置数量精度
			DZFDouble numm = balvo.getNnum();
			if (numm != null) {
				numm = numm.setScale(num, DZFDouble.ROUND_HALF_UP);
			} else {
				numm = DZFDouble.ZERO_DBL;
			}

			vo.setJldw(balvo.getMeasurename());
			vo.setPk_gs(pk_corp);
			vo.setGsname(corpvo.getUnitname());
			vo.setPeriod(period);
			vo.setNnumber_old(numm);
			vo.setNnumber(numm.abs());
			zgvo = getZgvo(balvo.getPk_inventory(), lastmap, outMap, inMap);
			if (zgvo != null) {
				vo.setNprice(zgvo.getNprice().setScale(iprice, DZFDouble.ROUND_HALF_UP));
				vo.setNmny(SafeCompute.multiply(vo.getNnumber(), vo.getNprice()).setScale(2, DZFDouble.ROUND_HALF_UP));
			}

			list.add(vo);
			vo.setKmname(cvo.getAccountname());
		}

		return list;
	}

	private IcbalanceVO getZgvo(String pk_inventory, Map<String, IcbalanceVO> lastmap, Map<String, IcbalanceVO> outMap,
			Map<String, IcbalanceVO> inMap) {
		// 暂估单价优先取上期结存单价（成本表）、无上期结存取本期购入平均价（该存货入库单未税金
		// 额相加/数量合计）、无本期购入，取本期发出平均销售价（该存货出库单未税金额相加/数量合计）
		IcbalanceVO zgvo = lastmap.get(pk_inventory);

		DZFDouble nprice = DZFDouble.ZERO_DBL;
		if (zgvo != null) {
			if (zgvo != null) {
				nprice = getZgprice(zgvo, true);
				zgvo.setNymny(zgvo.getNcost());
			}
		}

		if (nprice.doubleValue() <= 0) {
			zgvo = inMap.get(pk_inventory);
			if (zgvo != null) {
				nprice = getZgprice(zgvo, false);
			}
		}
		if (nprice.doubleValue() <= 0) {
			zgvo = outMap.get(pk_inventory);
			if (zgvo != null) {
				nprice = getZgprice(zgvo, false);
			}
		}
		if (zgvo != null) {
			zgvo.setNprice(nprice);
		}
		return zgvo;
	}

	private DZFDouble getZgprice(IcbalanceVO zgvo, boolean isqc) {

		DZFDouble nmny = DZFDouble.ZERO_DBL;
		if (isqc) {
			nmny = zgvo.getNcost();
		} else {
			nmny = zgvo.getNymny();
		}
		DZFDouble nprice = SafeCompute.div(nmny, zgvo.getNnum());
		return nprice;
	}

	private String queryMaxCbjzPeriod(CorpVO corpvo) {
		String pk_corp = corpvo.getPk_corp();
		DZFDate beginDate = corpvo.getBegindate();

		String period = DateUtils.getPeriod(beginDate);

		String qmclsql = "select max(period) from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and period >= ? and nvl(iscbjz,'N') ='Y'";

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);

		String maxperiod = (String) singleObjectBO.executeQuery(qmclsql, sp, new ColumnProcessor());

		if (StringUtil.isEmpty(maxperiod)) {
			maxperiod = period;
		} else {
			// 后续添加逻辑
			maxperiod = DateUtils.getPeriod(DateUtils.getPeriodEndDate(maxperiod).getDateAfter(1));
		}

		return maxperiod;
	}

	@Override
	public void saveZg(TempInvtoryVO[] bodyvos, CorpVO corpvo, String userid, String pk_zggys) throws DZFWarpException {

		if (bodyvos == null || bodyvos.length == 0)
			throw new BusinessException("解析暂估数据不完整,请检查");
		String period = bodyvos[0].getPeriod();
		IntradeHVO headvo = buildIntradeHVO(bodyvos, corpvo.getPk_corp(), userid, period, pk_zggys);
		headvo = save(headvo, false);
		saveIntradeHVOToZz(headvo, corpvo);
	}

	private IntradeHVO buildIntradeHVO(TempInvtoryVO[] bodyvos, String pk_corp, String userid, String period,
			String pk_zggys) {

		List<IctradeinVO> icList = new ArrayList<IctradeinVO>();
		IctradeinVO icbvo = null;

		String spmc = null;
		for (TempInvtoryVO inbvo : bodyvos) {
			spmc = inbvo.getInvname();
			if (StringUtil.isEmpty(spmc))
				continue;
			icbvo = new IctradeinVO();
			icbvo.setPk_inventory(inbvo.getPk_invtory());
			icbvo.setPk_subject(inbvo.getKmid());
			icbvo.setNnum(inbvo.getNnumber());
			icbvo.setNprice(inbvo.getNprice());
			icbvo.setNymny(inbvo.getNmny());// 金额
			icbvo.setNtax(DZFDouble.ZERO_DBL);
			icbvo.setNtaxmny(DZFDouble.ZERO_DBL);
			icbvo.setNtotaltaxmny(SafeCompute.add(icbvo.getNymny(), icbvo.getNtaxmny()));// 价税合计
			icList.add(icbvo);
		}

		if (icList.size() == 0) {
			throw new BusinessException("暂估数据出错，暂估失败");
		}

		IntradeHVO ichvo = new IntradeHVO();
		ichvo.setDbilldate(DateUtils.getPeriodEndDate(period));
		ichvo.setDbillid(null);// 设置为空，调用入库接口会重新生成
		ichvo.setCbusitype(IcConst.CGTYPE);// 采购入库
		ichvo.setIpayway(IcPayWayEnum.ARREARS.getValue());// 欠款
		ichvo.setPk_corp(pk_corp);
		ichvo.setIarristatus(1);
		ichvo.setCreator(userid);
		ichvo.setIszg(DZFBoolean.TRUE);// 是暂估
		ichvo.setChildren(icList.toArray(new IctradeinVO[0]));
		ichvo.setPk_cust(pk_zggys);

		return ichvo;
	}

	private List<IcbalanceVO> queryCurrentPeriodSaleOut(String lastdate, String currentenddate, String pk_corp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(nymny) nymny,pk_inventory from ( ");
		// 出库
		sf.append("  (select out1.pk_inventory,nnum as nnum ,nymny as nymny from ynt_ictradeout out1 ");
		sf.append("   where out1.dbilldate >= ? and out1.dbilldate <= ? and out1.pk_corp =  ? and nvl(out1.dr,0)=0 ");
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append("  )) group by pk_inventory ");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
	}

	private List<IcbalanceVO> queryCurrentPeriodPurchIn(String lastdate, String currentenddate, String pk_corp)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(nymny) nymny,pk_inventory from ( ");
		sf.append("  (select in1.pk_inventory,nnum,nymny from ynt_ictradein in1 ");
		sf.append("   where in1.dbilldate >= ? and in1.dbilldate <= ? and in1.pk_corp = ? and nvl(in1.dr,0)=0");
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append("  )");

		sf.append("  ) group by pk_inventory");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
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
		if(periodList == null || periodList.size() == 0)
			return null;

		String part = SqlUtil.buildSqlForIn("period", periodList.toArray(new String[0]));

		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and ");
		sf.append(part);
		sp.addParam(pk_corp);

		List<QmclVO> list = (List<QmclVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(QmclVO.class));

		VOUtil.ascSort(list, new String[]{"period"});
		sf.setLength(0);
		if(list != null && list.size() > 0){
			String period;
			DZFBoolean value;
			for(QmclVO vo : list){
				period = vo.getPeriod();

				value = vo.getIsqjsyjz();
				if(value != null && value.booleanValue()){
					sf.append("<p><font color = 'red'>").append(period).append("期间损益已结转，生成凭证后，请重新结转期间损益!</font></p>");
				}

				value = vo.getIscbjz();
				if(value != null && value.booleanValue()){
					sf.append("<p><font color = 'red'>").append(period).append("成本已结转，生成凭证后，请重新结转成本!</font></p>");
				}

				value = vo.getIshdsytz();
				if(value != null && value.booleanValue()){
					sf.append("<p><font color = 'red'>").append(period).append("汇兑调整已完成，生成凭证后，请重新进行汇兑调整!</font></p>");
				}
			}
		}

		return sf;
	}
}

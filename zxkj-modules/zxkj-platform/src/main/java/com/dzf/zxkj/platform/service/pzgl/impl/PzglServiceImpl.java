package com.dzf.zxkj.platform.service.pzgl.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pjgl.PictureBrowseVO;
import com.dzf.zxkj.platform.model.pzgl.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.jzcl.ITerminalSettle;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.ImageCopyUtil;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.TZPZHVOSort;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@SuppressWarnings("all")
@Slf4j
@Service("gl_pzglserv")
public class PzglServiceImpl implements IPzglService {

	private SingleObjectBO singleObjectBO = null;

	private IQmgzService qmgzService;
	@Autowired
	private IImageGroupService img_groupserv;

	@Autowired
	private IVoucherService gl_tzpzserv;

	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	
	@Autowired
	private IParameterSetService sys_parameteract;

	public IQmgzService getQmgzService() {
		return qmgzService;
	}

	@Autowired
	public void setQmgzService(IQmgzService qmgzService) {
		this.qmgzService = qmgzService;
	}

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IAccountService accountService;

	@Override
	public List<TzpzHVO> query(String period, String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer qrySql = new StringBuffer();
		// period = period + "%";
		sp.addParam(period);
		sp.addParam(pk_corp);
		qrySql.append("select * from ynt_tzpz_h ");
		qrySql.append("where period= ? ");
		qrySql.append("and pk_corp = ? and nvl(dr,0)=0 order by pzh");
		List<TzpzHVO> listres = (List<TzpzHVO>) singleObjectBO.executeQuery(qrySql.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));
		return listres;
	}

	@Override
	public List<TzpzBVO> queryB(String hid, String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(hid);
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if (!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			sf.append("select ynt_cpaccount.measurename as meaname, tb1.* from ynt_tzpz_b tb1");
			sf.append(
					" left join ynt_cpaccount ynt_cpaccount on ynt_cpaccount.PK_CORP_ACCOUNT = tb1.pk_accsubj and ynt_cpaccount.pk_corp = tb1.pk_corp ");
			sf.append(" where tb1.pk_tzpz_h = ? and tb1.pk_corp = ? and nvl(tb1.dr,0) = 0 order by tb1.rowno ");
		} else {
			sf.append("select ynt_inventory.name as invname, ynt_measure.name as meaname, tb1.* from ynt_tzpz_b tb1");
			sf.append(
					" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = tb1.pk_inventory and ynt_inventory.pk_corp = tb1.pk_corp ");
			sf.append(" left join ynt_measure ynt_measure on ynt_inventory.pk_measure = ynt_measure.pk_measure");
			sf.append(" where tb1.pk_tzpz_h = ? and tb1.pk_corp = ? and nvl(tb1.dr,0) = 0 order by tb1.rowno ");
		}
		List<TzpzBVO> vos = (List<TzpzBVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(TzpzBVO.class));
		if (vos != null && vos.size() > 0) {
			Map<String, AuxiliaryAccountBVO> fzhsMap = gl_fzhsserv.queryMap(pk_corp);
			for (TzpzBVO tzpzBVO : vos) {
				tzpzBVO.setFzhs_list(getFzhs(tzpzBVO, fzhsMap, null));
			}
		}
		return vos;
	}

	@Override
	public TzpzHVO queryByID(String id, String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" pk_tzpz_h = ? and nvl(dr,0)=0 and pk_corp = ? ");
		sp.addParam(id);
		sp.addParam(pk_corp);
		List<TzpzHVO> ancevos = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new Class[] { TzpzHVO.class, TzpzBVO.class });
		if (ancevos != null && ancevos.size() > 0) {
			return ancevos.get(0);
		}
		return null;
	}

	@Override
	public void updateAudit(List<TzpzHVO> hvos, String userid) throws DZFWarpException {
		for (TzpzHVO hvo: hvos) {
			hvo.setVbillstatus(IVoucherConstants.AUDITED);
			// 审核人
			hvo.setVapproveid(userid);
		}
		singleObjectBO.updateAry(hvos.toArray(new TzpzHVO[0]),
				new String[]{"vbillstatus", "dapprovedate", "vapproveid"});
	}

	@Override
	public void updateUnAudit(List<TzpzHVO> hvos) throws DZFWarpException {
		if (hvos != null && hvos.size() > 0) {
			for (TzpzHVO tzpzvo : hvos) {
				tzpzvo.setVbillstatus(IVoucherConstants.FREE);
				tzpzvo.setDapprovedate(null);
				tzpzvo.setVapproveid(null);
			}
			singleObjectBO.updateAry(hvos.toArray(new TzpzHVO[0]),
					new String[]{"vbillstatus", "dapprovedate", "vapproveid"});
		}
	}

	@Override
	public void updateAccounting(List<TzpzHVO> hvos, String userid) throws DZFWarpException {
		for (TzpzHVO hvo: hvos) {
			hvo.setIshasjz(DZFBoolean.TRUE);
			hvo.setVjzoperatorid(userid);
		}
		singleObjectBO.updateAry(hvos.toArray(new TzpzHVO[0]),
				new String[]{"ishasjz", "djzdate", "vjzoperatorid"});
	}

	@Override
	public void updateUnAccounting(List<TzpzHVO> hvos) throws DZFWarpException {
		if (hvos != null && hvos.size() > 0) {
			for (TzpzHVO tzpzvo : hvos) {
				tzpzvo.setIshasjz(DZFBoolean.FALSE);
				tzpzvo.setDjzdate(null);
				tzpzvo.setVjzoperatorid(null);
			}
			singleObjectBO.updateAry(hvos.toArray(new TzpzHVO[0]),
					new String[]{"ishasjz", "djzdate", "vjzoperatorid"});
		}
	}

	@Override
	public List<TzpzHVO> queryByIDs(String ids, VoucherPrintParam param) throws DZFWarpException {
		if (StringUtil.isEmpty(ids)) {
			return null;
		}
		List<String> idList = Arrays.asList(ids.split(","));
		String inSQL_1 = SqlUtil.buildSqlForIn(" pk_tzpz_h ", idList.toArray(new String[0]));// SQLHelper.getInSQL(idList);
		String inSQL_2 = SqlUtil.buildSqlForIn(" tb1.pk_tzpz_h ", idList.toArray(new String[0]));// SQLHelper.getInSQL(idList);
		//SQLParameter sp = SQLHelper.getSQLParameter(idList);
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
//		sf.append(" pk_tzpz_h in ").append(inSQL);
		sf.append(inSQL_1);
		sf.append(" and nvl(dr,0)=0 order by pk_corp,period, pzh ");
		// sp.addParam(ids);
		// sp.addParam(pk_corp);
		List<TzpzHVO> hvos = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new Class[] { TzpzHVO.class });

		sf = new StringBuffer();

		sf.append("select ynt_inventory.name as invname, ynt_inventory.code as invcode, ");
		sf.append(" ynt_measure.name as meaname, ynt_cpaccount.measurename as vdef7, tb1.* from ynt_tzpz_b tb1");
		sf.append(
				" left join ynt_cpaccount ynt_cpaccount on ynt_cpaccount.PK_CORP_ACCOUNT = tb1.pk_accsubj and ynt_cpaccount.pk_corp = tb1.pk_corp ");
		sf.append(
				" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = tb1.pk_inventory and ynt_inventory.pk_corp = tb1.pk_corp ");
		sf.append(" left join ynt_measure ynt_measure on ynt_inventory.pk_measure = ynt_measure.pk_measure");
//		sf.append(" where tb1.pk_tzpz_h in ").append(inSQL).append(" and nvl(tb1.dr,0) = 0 ");
		sf.append(" where ").append(inSQL_2).append(" and nvl(tb1.dr,0) = 0 ");

		// List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(
		// sf.toString(), sp, new Class[] {TzpzBVO.class});
		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(TzpzBVO.class));

		// 根据pk_tzpz_h分组
		Map<String, List<TzpzBVO>> bvoMap = DZfcommonTools.hashlizeObject(bvos, new String[] { "pk_tzpz_h" });
		Map<String, Map<String, AuxiliaryAccountBVO>> accountMapAll = new HashMap<String, Map<String, AuxiliaryAccountBVO>>();
		Map<String, YntCpaccountVO[]> kmCacheAll = new HashMap<String, YntCpaccountVO[]>();
		if (hvos != null && hvos.size() > 0) {
			Map<String, boolean[]> assistSetMap = new HashMap<>();
			if (param != null && param.getAssistSetting() != null && param.getAssistSetting() != null
					&& param.getShow_auxiliary() != null && param.getShow_auxiliary().booleanValue()) {
				for (VoucherPrintAssitSetVO assistSet: param.getAssistSetting()) {
					boolean[] assistShowArray = new boolean[10];
					for (int i = 0; i < 10; i++) {
						Boolean assistShow = (Boolean) assistSet.getAttributeValue("printfzhs" + (i + 1));
						assistShowArray[i] = assistShow != null && assistShow.booleanValue();
					}
					assistSetMap.put(assistSet.getPk_accsubj(), assistShowArray);
				}
			}
			for (TzpzHVO th : hvos) {
				String pk_corp = th.getPk_corp();
				Map<String, AuxiliaryAccountBVO> auaccountMap = null;
				if (accountMapAll.containsKey(pk_corp)) {
					auaccountMap = accountMapAll.get(pk_corp);
				} else {
					auaccountMap = gl_fzhsserv.queryMap(pk_corp);
					accountMapAll.put(pk_corp, auaccountMap);
				}
				bvos = bvoMap.get(th.getPrimaryKey());
				if (bvos != null && !bvos.isEmpty()) {
					// 根据行号排序
					Collections.sort(bvos, new Comparator<TzpzBVO>() {
						public int compare(TzpzBVO bvo1, TzpzBVO bvo2) {
							Integer num1 = bvo1.getRowno() == null ? 0 : bvo1.getRowno();
							Integer num2 = bvo2.getRowno() == null ? 0 : bvo2.getRowno();
							return num1.compareTo(num2);
						}
					});
					for (TzpzBVO tzpzBVO : bvos) {
						if (StringUtil.isEmpty(tzpzBVO.getMeaname())) {
							tzpzBVO.setMeaname(tzpzBVO.getVdef7());
						}
						tzpzBVO.setFzhs_list(getFzhs(tzpzBVO, auaccountMap, assistSetMap.get(tzpzBVO.getPk_accsubj())));

						//是否打印辅助
						if(param!=null && param.getShow_auxiliary_noinv() != null && param.getShow_auxiliary_noinv().booleanValue()){
							//显示辅助核算项目(不打印存货)
							if(!StringUtil.isEmpty(tzpzBVO.getFzhsx6())
									|| !StringUtil.isEmpty(tzpzBVO.getPk_inventory())){
								List<AuxiliaryAccountBVO> bvolist = tzpzBVO.getFzhs_list();
								if(bvolist!=null && bvolist.size()>0){
									bvolist.remove(auaccountMap.get(tzpzBVO.getFzhsx6()));
								}
								tzpzBVO.setInvcode(null);
								tzpzBVO.setInvname(null);
								tzpzBVO.setNnumber(null);
								tzpzBVO.setMeaname(null);
							}
						}else if(param!=null && (param.getShow_auxiliary() == null || !param.getShow_auxiliary().booleanValue())){
							//显示辅助核算项目
							tzpzBVO.setFzhs_list(null);
							tzpzBVO.setInvcode(null);
							tzpzBVO.setInvname(null);
							tzpzBVO.setNnumber(null);
							tzpzBVO.setMeaname(null);
						}
					}
					if (param != null && param.getCollect() != null && param.getCollect().booleanValue()) {
						YntCpaccountVO[] kms = null;
						if (kmCacheAll.containsKey(pk_corp)) {
							kms = kmCacheAll.get(pk_corp);
						} else {
							kms = accountService.queryByPk(pk_corp);
							kmCacheAll.put(pk_corp, kms);
						}
						accountCollect(th, bvos, kms, param);
					}
					th.setChildren(bvos.toArray(new TzpzBVO[0]));
					;
				}
			}
		}

		return hvos;
	}

	@Override
	public void update(TzpzHVO vo) throws DZFWarpException {
		singleObjectBO.update(vo);

	}

	@Override
	public int doVoucherOrder3(String pk_corp, JSONArray list) throws DZFWarpException {
		checkVoucher(pk_corp, list);
		int len = list == null ? 0 : list.size();
		int len1 = 0;
		int nsum = 0;
		JSONObject obj = null;
		StringBuffer sb = new StringBuffer();

		String strvalue = "select ynt_tzpz_h.*,a.b as qrydope,a.c as qrypzh from xxxxx a ,YNT_TZPZ_H  where YNT_TZPZ_H.pk_tzpz_h=a.a";

		// String str = " update YNT_TZPZ_H set YNT_TZPZ_H.doperatedate=a.b
		// ,YNT_TZPZ_H.pzh=a.c from xxxxx a,YNT_TZPZ_H on
		// YNT_TZPZ_H.pk_tzpz_h=a.a";
		for (int i = 0; i < len; i += 200) {
			len1 = i + 200;
			if (len1 > len)
				len1 = len;
			sb = new StringBuffer("(");
			for (int j = i; j < len1; j++) {
				obj = (JSONObject) list.get(j);
				if (j > i)
					sb.append(" union all ");

				sb.append(" select '").append(obj.getString("id")).append("' a,'")
						.append(obj.getString("newDate") != null && !obj.getString("newDate").trim().isEmpty()
								? obj.getString("newDate") : obj.getString("zdrq"))
						.append("' b,'").append(obj.getString("newpzh")).append("' c from dual").append("\r\n");

			}
			sb.append(")");

			// modify by zhangj 对特殊字符处理$
			String retemp = java.util.regex.Matcher.quoteReplacement(sb.toString());

			List<TzpzHVO> tzpzhlistvo = (List<TzpzHVO>) singleObjectBO.executeQuery(
					strvalue.replaceAll("xxxxx", retemp), new SQLParameter(), new BeanListProcessor(TzpzHVO.class));
			CorpVO corp = corpService.queryByPk(pk_corp);

			SQLParameter sqlp = new SQLParameter();
			for (TzpzHVO hvo : tzpzhlistvo) {
				hvo.setPzh(hvo.getQrypzh());
				hvo.setDoperatedate(new DZFDate(hvo.getQrydope()));
				updateIctradePzh(pk_corp, hvo);
			}

			singleObjectBO.updateAry(tzpzhlistvo.toArray(new TzpzHVO[0]));
			nsum = tzpzhlistvo.size();
			// nsum += singleObjectBO.executeUpdate(str.replaceAll("xxxxx",
			// retemp), null);

		}
		return nsum;
	}

	private void checkVoucher(String pk_corp, JSONArray list) throws DZFWarpException {
		int len = list == null ? 0 : list.size();
		JSONObject obj = null;
		int ii0 = 0;
		int ii1 = 0;
		DZFDate ufd = null;
		DZFDate ufdmin = new DZFDate("9999-01-01");
		boolean b = false;
		for (int i = 0; i < len; i++) {
			obj = (JSONObject) list.get(i);
			if (obj.getString("newDate") != null && !obj.getString("newDate").trim().isEmpty()) {
				String a = obj.getString("newDate");
				ufd = DZFDate.getDate(obj.getString("zdrq"));
				ii0 = ufd.getYear() << 4;
				ii0 += ufd.getMonth();
				ufd = DZFDate.getDate(obj.getString("newDate"));
				ii1 = ufd.getYear() << 4;
				ii1 += ufd.getMonth();
				if (ii0 != ii1) {
					b = true;
					if (ufd.compareTo(ufdmin) < 0)
						ufdmin = ufd;
				}
			}
		}
		if (b == false)
			return;
		ITerminalSettle gl_qmjzserv = (ITerminalSettle) SpringUtils.getBean("gl_qmjzserv");
		if (gl_qmjzserv.isExistsJZ(pk_corp, ufdmin))
			throw new BusinessException("已经结账不能修改日期");

	}

	@Override
	public String doVoucherOrder(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException {

		StringBuffer returnmeg = new StringBuffer();
		int updateCounts = 0;
		if (pk_corps == null || pk_corps.length < 1) {
			throw new BusinessException("传入公司参数不能为空");
		}

		if (beginperiod == null) {
			throw new BusinessException("传入期间起参数不能为空");
		}
		if (endperiod == null) {
			throw new BusinessException("传入期间至参数不能为空");
		}
		// select p.* ,row_number()over(partition by p.code order order by a.id
		// desc) as row_index from t_pi_part p;
		List<String> plist = new ArrayList<String>();
		String period;
		Integer beginMonth = beginperiod.getMonth();
		// 获得期间
		for (int year = beginperiod.getYear(); year <= endperiod.getYear(); year++) {
			if (year == endperiod.getYear()) {
				for (; beginMonth <= endperiod.getMonth(); beginMonth++) {
					period = year + "-" + (beginMonth > 9 ? beginMonth : "0" + beginMonth);
					plist.add(period);
				}
			} else {
				for (; beginMonth < 13; beginMonth++) {
					period = year + "-" + (beginMonth > 9 ? beginMonth : "0" + beginMonth);
					plist.add(period);
				}
			}
			beginMonth = 1;
		}

		TzpzHVO hvo = null;
		SQLParameter sqlp = null;
		String corpname = null;
		String condition = null;
		SQLParameter sp = null;
		TzpzHVO[] vos = null;
		TZPZHVOSort sort = null;
		List<TzpzHVO> list = null;
		String pzh = null;
		String num = null;
		for (String pk_corp : pk_corps) {
			for (String periodParam : plist) {
				boolean isgz = qmgzService.isGz(pk_corp, periodParam);
				if (isgz) {// 是否关账
					corpname = corpService.queryByPk(pk_corp).getUnitname();
					returnmeg.append("公司：" + corpname + "在" + periodParam + "已经关账，不允许整理哦。<br/>");
					continue;
				}
				sp = new SQLParameter();
				periodParam = periodParam + "%";
				sp.addParam(pk_corp);
				sp.addParam(periodParam);
				condition = "pk_corp=? and doperatedate like ? and nvl(dr,0)=0 order by doperatedate ";
				vos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, condition, sp);
				if (vos == null || vos.length == 0) {
					// 该期间没有凭证
					continue;
				}
				sort = new TZPZHVOSort();// 排序
				list = new ArrayList<TzpzHVO>(Arrays.asList(vos));
				Collections.sort(list, sort);
				//损益凭证排最后
				sortSypz(list);

				vos = list.toArray(new TzpzHVO[0]);
				if (vos != null && vos.length > 0) {
					for (int z = 1; z <= vos.length; z++) {
						num = "0001";
						if (z <= 9) {
							num = "000" + z;
						} else if (z > 9 && z < 100) {
							num = "00" + z;
						} else if (z >= 100 && z < 1000) {
							num = "0" + z;
						} else {
							num = "" + z;
						}

						pzh = num;

						// 整理的凭证数
						hvo = vos[z - 1];
						updateCounts++;
						sqlp = new SQLParameter();
						sqlp.addParam(pzh);
						sqlp.addParam(hvo.getPrimaryKey());
						hvo.setPzh(pzh);
						singleObjectBO.executeUpdate(" update YNT_TZPZ_H set pzh= ? where pk_tzpz_h= ? ", sqlp);
						updateIctradePzh(pk_corp, hvo);
					}
				}
			}

		}
		return returnmeg.toString() + "成功整理" + updateCounts + "条";
	}

	private void updateIctradePzh(String pk_corp, TzpzHVO hvo) {
		// 更新采购单 销售单凭证号
		SQLParameter sqlp = new SQLParameter();
		sqlp.addParam(hvo.getPzh());
		sqlp.addParam(hvo.getPrimaryKey());
		sqlp.addParam(pk_corp);
		CorpVO corp = corpService.queryByPk(pk_corp);

		if (corp.getIbuildicstyle() != null && corp.getIbuildicstyle() == 1) {
			// 这是新模式库存
			if (!StringUtil.isEmpty(hvo.getSourcebilltype())) {
				if (hvo.getSourcebilltype().equals(IBillTypeCode.HP70) || hvo.getSourcebilltype().equals(IBillTypeCode.HP34)) {
					singleObjectBO.executeUpdate(" update ynt_ictrade_h set pzh= ? where pzid= ? and pk_corp = ? ",
							sqlp);
					singleObjectBO.executeUpdate(
							" update ynt_ictradein set pzh= ? where pk_voucher= ? and pk_corp = ? ", sqlp);
				} else if (hvo.getSourcebilltype().equals(IBillTypeCode.HP75)) {
					singleObjectBO.executeUpdate(" update ynt_ictrade_h set pzh= ? where pzid= ? and pk_corp = ? ",
							sqlp);
					singleObjectBO.executeUpdate(
							" update ynt_ictradeout set pzh= ? where pk_voucher= ? and pk_corp = ? ", sqlp);
				}
			}
		} else if (corp.getIbuildicstyle() != null && corp.getIbuildicstyle() == 0) {
			// 这是老模式库存
			if (StringUtil.isEmpty(hvo.getSourcebilltype())) {
				singleObjectBO.executeUpdate(" update ynt_ictradein set pzh= ? where pk_voucher= ? and pk_corp = ? ",
						sqlp);
				singleObjectBO.executeUpdate(" update ynt_ictradeout set pzh= ? where pk_voucher= ? and pk_corp = ? ",
						sqlp);
			}
		}

		// 其它单据

	}

	/*
	 * 图片浏览
	 */
	// @Override
	// public List<ImageLibraryVO> search(String pk_corp, String
	// begindate,String enddate,
	// String group1,String group2,String nowpage,String pagesize,String status)
	// throws DZFWarpException {
	// SQLParameter sp=new SQLParameter();
	// StringBuffer qrySql = new StringBuffer();
	// //System.out.println("在serviceImpl中的pk_corp: " + pk_corp);
	// qrySql.append("select a.*,b.*,c.unitname,su.user_name ");
	// qrySql.append("from ynt_image_group a ");
	// qrySql.append("left join ynt_image_library b on a.pk_image_group =
	// b.pk_image_group ");
	// qrySql.append("left join bd_corp c on a.pk_corp = c.pk_corp ");
	// qrySql.append("left join sm_user su on su.cuserid = a.coperatorid where
	// 1=1 and nvl(a.dr,0) = 0 and nvl(b.dr,0) = 0 ");
	// qrySql.append(" and nvl(a.istate,10) <> 10 ");//过滤数据中心切图，华道生单
	// qrySql.append(getFrontStateSql(status));
	// if(pk_corp !=null && pk_corp.equals("") == false){
	// sp.addParam(pk_corp);
	// qrySql.append(" and b.pk_corp = ? ");
	// }
	//
	// if(begindate !=null && begindate.equals("")==false && enddate !=null &&
	// enddate.equals("")==false){
	// sp.addParam(begindate);
	// sp.addParam(enddate);
	// qrySql.append(" and b.cvoucherdate between ? and ?
	// ");//查询条件由上传时间(doperatedate)改为凭证时间(cvoucherdate)
	// }
	//
	//
	// if(group1 !=null && group1.equals("")==false && group1 !=null &&
	// group1.equals("")==false){
	// sp.addParam(group1);
	// sp.addParam(group2);
	// qrySql.append(" and a.groupcode between ? and ? ");
	// }
	// qrySql.append("order by
	// a.pk_image_group,b.pk_image_library,a.groupcode");
	// List<ImageLibraryVO> imageList =
	// (List<ImageLibraryVO>)singleObjectBO.executeQuery(qrySql.toString(), sp,
	// new BeanListProcessor(ImageLibraryVO.class));
	// //排序
	// Collections.sort(imageList, new ImageLibrarySort());
	// return imageList;
	//
	// }
	/**
	 * 方法只是转换前台传过来的状态值，前台传来的状态值与数据库存储不一致
	 * 
	 * @param status
	 * @return
	 */
	private String getFrontStateSql(String status) {

		StringBuffer stateSql = new StringBuffer();
		if ("0".equals(status)) {
			// 不做处理
		} else if ("1".equals(status)) {
			stateSql.append(" and nvl(a.istate, 10) not in (").append(PhotoState.state80).append(",")
					.append(PhotoState.state100).append(",").append(PhotoState.state101).append(")");
		} else if ("3".equals(status)) {
			stateSql.append(" and nvl(a.istate,10) = ");
			stateSql.append(PhotoState.state80);
			stateSql.append(" ");
		} else if ("4".equals(status)) {
			stateSql.append(" and nvl(a.istate,10) in ( ").append(PhotoState.state100).append(",")
					.append(PhotoState.state101).append(")").append(" ");
		} else if ("2".equals(status)) {
			stateSql.append(" and nvl(a.istate,10) not in ( '").append(PhotoState.state0).append("','")
					.append(PhotoState.state80).append("','").append(PhotoState.state100).append("' )");
		} else if ("10".equals(status)) {
			// 未制证
			stateSql.append(" and nvl(a.istate, 10) not in (").append(PhotoState.state80).append(",").append(PhotoState.state205).append(",")
					.append(PhotoState.state100).append(",").append(PhotoState.state101).append(") and ((yi.pk_invoice is not null and yi.pk_billcategory is not null) or c.def4 <> ").append(PhotoState.TREAT_TYPE_7).append(")");
		} else if ("11".equals(status)) {
			// 暂存凭证
			stateSql.append(" and h.vbillstatus = -1 and h.iautorecognize <> 1 ");
		} else if ("12".equals(status)) {
			// 正式凭证
			stateSql.append(" and (h.vbillstatus > 0 or h.vbillstatus = -1 and h.iautorecognize = 1) ");
		} else if ("13".equals(status)) {
			// 重复
			stateSql.append(" and nvl(a.istate,10) = ").append(PhotoState.state102).append(" ");
		} else if ("14".equals(status)) {
			// 已识别
			stateSql.append(" and (nvl(c.def4, '0') <> '").append(PhotoState.TREAT_TYPE_7)
					.append("' or nvl(a.istate,10) not in (")
					.append(PhotoState.state0).append("))");
		} else if ("15".equals(status)) {
			// 统计-未处理
			stateSql.append(" and nvl(c.def4, '0') = '").append(PhotoState.TREAT_TYPE_7)
					.append("' and nvl(a.istate,10) = ").append(PhotoState.state0).append(" ");
		}else if ("16".equals(status)) {
			// 统计-已作废
			stateSql.append(" and nvl(a.istate,10) = '").append(PhotoState.state205).append("'");
		}else if ("17".equals(status)) {
			// 统计-做账
			stateSql.append(" and nvl(a.istate, 10)  in (")
					.append(PhotoState.state100).append(",").append(PhotoState.state101).append(")");
		}else if ("18".equals(status)) {
			// 识别中
			stateSql.append(" and ( (yi.pk_invoice is null or yi.pk_billcategory is null)  and yc.pk_image_ocrlibrary is not null and  a.istate not in (205,100,101) )").append(" and c.def4 = ").append(PhotoState.TREAT_TYPE_7);

		}

		return stateSql.toString();

	}

	@Override
	public List<UserVO> queryPowerUser(String pk_corp) throws DZFWarpException {
		StringBuffer qrySql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		String date = new DZFDate().toString();
		sp.addParam(pk_corp);
		sp.addParam(date);
		qrySql.append(" select distinct su.* from sm_user su ");
		qrySql.append(" join sm_user_role re on re.cuserid = su.cuserid ");
		qrySql.append(" where re.pk_corp = ? and nvl(re.dr,0) = 0 and nvl(su.dr,0) = 0");
		qrySql.append(" and nvl(su.locked_tag,'N') = 'N' and (su.disable_time is null or su.disable_time > ?) ");

		List<UserVO> users = (List<UserVO>) singleObjectBO.executeQuery(qrySql.toString(), sp,
				new BeanListProcessor(UserVO.class));
		return users;
	}

	private List<AuxiliaryAccountBVO> getFzhs(TzpzBVO tzpzBVO,
											  Map<String, AuxiliaryAccountBVO> fzhsMap, boolean[] assist) {
		List<AuxiliaryAccountBVO> fzhsList = new ArrayList<AuxiliaryAccountBVO>();
		for (int i = 1; i <= 10; i++) {
			String fzhsID = (String) tzpzBVO.getAttributeValue("fzhsx" + i);
			if (!StringUtil.isEmpty(fzhsID) && (assist == null || assist[i - 1])) {
				AuxiliaryAccountBVO fzhs = fzhsMap.get(fzhsID);
				
				if(fzhs == null)
					continue;
				
				if (i == 6) {
					tzpzBVO.setMeaname(!StringUtil.isEmpty(fzhs.getUnit()) ? fzhs.getUnit() : tzpzBVO.getMeaname());
				}
				fzhsList.add(fzhsMap.get(fzhsID));
			}
		}
		if (assist != null && !assist[5]) {
			tzpzBVO.setInvcode(null);
			tzpzBVO.setInvname(null);
			tzpzBVO.setNnumber(null);
			tzpzBVO.setMeaname(null);
		}
		return fzhsList;
	}

	/**
	 * 图片浏览
	 */
	@Override
	public List<PictureBrowseVO> search(ImageParamVO imgparamvo) throws DZFWarpException {

		String status = imgparamvo.getPic_status();
		String begindate = imgparamvo.getBegindate();
		String enddate = imgparamvo.getEnddate();
		String begindate2 = imgparamvo.getBegindate2();
		String enddate2 = imgparamvo.getEnddate2();

		String pk_corp = imgparamvo.getPk_corp();
		String group1 = imgparamvo.getGroup1();
		String group2 = imgparamvo.getGroup2();
		String nowpage = imgparamvo.getNowpage();
		String pagesize = imgparamvo.getPagesize();
		String pjlxzt = imgparamvo.getPjlxzt();
		String recognition_type = imgparamvo.getRecognition_type();

		SQLParameter sp = new SQLParameter();
		StringBuffer qrySql = new StringBuffer();
		qrySql.append("select a.*,b.*, yc.keycode as ocr_batch,c.unitname, c.unitcode,su.user_name,h.vbillstatus pzdt,h.iautorecognize, ");
		qrySql.append("  case when ( (yi.pk_invoice is null or yi.pk_billcategory is null) and  yc.pk_image_ocrlibrary is not null and  a.istate not in (205,100,101) ) and c.def4 =7 then 18 else a.istate end as istate ");
		qrySql.append("from ynt_image_group a ");
		qrySql.append("left join ynt_image_library  b  on a.pk_image_group = b.pk_image_group ");
		qrySql.append("left join bd_corp c on a.pk_corp = c.pk_corp ");
		qrySql.append("left join ynt_tzpz_h h on h.pk_image_group = a.pk_image_group and nvl(h.dr,0) = 0  ");
		qrySql.append(" left join ynt_image_ocrlibrary yc on b.pk_image_library = yc.crelationid ");
		qrySql.append(" left join ynt_interface_invoice yi on yi.ocr_id = yc.pk_image_ocrlibrary and nvl(yi.dr,0)=0 ");
		if ("1".equals(recognition_type)) {
			qrySql.append(" left join ynt_vatincominvoice bu on bu.pk_image_library = b.pk_image_library and nvl(bu.dr, 0) = 0");
		} else if ("2".equals(recognition_type)) {
			qrySql.append(" left join ynt_vatsaleinvoice sl on sl.pk_image_library = b.pk_image_library and nvl(sl.dr, 0) = 0");
		} else if ("3".equals(recognition_type)) {
			qrySql.append(" left join ynt_bankstatement bk on bk.sourcebillid = b.pk_image_library and nvl(bk.dr, 0) = 0");
		} else if ("4".equals(recognition_type)) {
			qrySql.append(" left join ynt_vatincominvoice bu on bu.pk_image_library = b.pk_image_library and nvl(bu.dr, 0) = 0");
			qrySql.append(" left join ynt_vatsaleinvoice sl on sl.pk_image_library = b.pk_image_library and nvl(sl.dr, 0) = 0");
			qrySql.append(" left join ynt_bankstatement bk on bk.sourcebillid = b.pk_image_library and nvl(bk.dr, 0) = 0");
		}
		qrySql.append(
				"left join sm_user su on su.cuserid = a.coperatorid where 1=1 and nvl(a.dr,0) = 0 and nvl(b.dr,0) = 0 ");
		qrySql.append(" and nvl(a.istate,10) <> ");// 过滤数据中心切图，华道生单
		qrySql.append(PhotoState.state10);
		qrySql.append(" and nvl(a.istate,10) <>  ");//过滤退回图片
		qrySql.append(PhotoState.state80);
		qrySql.append(getFrontStateSql(status));
		if (!StringUtil.isEmpty(pk_corp)) {// pk_corp !=null &&
			// pk_corp.equals("") == false
			sp.addParam(pk_corp);
			qrySql.append(" and b.pk_corp = ? ");
		}

		if("serSc".equals(imgparamvo.getSerdate())){
			if (!StringUtil.isEmpty(begindate) && !StringUtil.isEmpty(enddate)) {
				sp.addParam(begindate);
				sp.addParam(enddate);
				qrySql.append(" and b.cvoucherdate between ? and ? ");
			}
		}else if("serRz".equals(imgparamvo.getSerdate())){
			if (!StringUtil.isEmpty(begindate2) && !StringUtil.isEmpty(enddate2)) {
				sp.addParam(begindate2);
				sp.addParam(enddate2);
				qrySql.append(" and b.doperatedate between ? and ? ");
			}
		}


		if (recognition_type != null) {
			// 启用智能识别公司，不包含未处理图片
			qrySql.append(" and (nvl(c.def4, '0') <> '").append(PhotoState.TREAT_TYPE_7)
					.append("' or nvl(a.istate,10) not in (")
					.append(PhotoState.state0).append("))");
			if ("1".equals(recognition_type)) {
				// 入库发票
				qrySql.append(" and bu.ioperatetype = 21 and bu.pk_vatincominvoice is not null ");
			} else if ("2".equals(recognition_type)) {
				// 销项发票
				qrySql.append(" and sl.pk_vatsaleinvoice is not null ");
			} else if ("3".equals(recognition_type)) {
				// 银行票据
				qrySql.append(" and bk.pk_bankstatement is not null ");
			} else if ("4".equals(recognition_type)) {
				// 其他票据
				qrySql.append(" and sl.pk_vatsaleinvoice is null and bk.pk_bankstatement is null ")
						.append(" and (bu.pk_vatincominvoice is null or bu.ioperatetype is null or bu.ioperatetype <> 21) ");
			}
		}

		if (!StringUtil.isEmpty(group1) && !StringUtil.isEmpty(group2)) {
			sp.addParam(group1);
			sp.addParam(group2);
			qrySql.append(" and a.groupcode between ? and ? ");
		}
		if (!StringUtil.isEmpty(pjlxzt)) {
			if ("-1".equals(pjlxzt)) {
				qrySql.append(" and a.pjlxstatus is null ");
			} else {
				sp.addParam(pjlxzt);
				qrySql.append(" and a.pjlxstatus = ? ");
			}
		}
		qrySql.append(" and ( vapprovetor is null or vapprovetor like 'END%' ) ");//过滤掉app上传还在审批中的
		qrySql.append("order by a.pk_image_group,b.pk_image_library,a.groupcode");//a.pk_image_group,b.pk_image_library,a.groupcode
		List<PictureBrowseVO> imageList = (List<PictureBrowseVO>) singleObjectBO.executeQuery(qrySql.toString(), sp,
				new BeanListProcessor(PictureBrowseVO.class));
		// 排序
		Collections.sort(imageList, new Comparator<PictureBrowseVO>() {

			@Override
			public int compare(PictureBrowseVO arg0, PictureBrowseVO arg1) {
				int i = arg0.getImgname().compareTo(arg1.getImgname());
				return i;
			}
		});
		Set<String> gids = new HashSet<String>();
		for (PictureBrowseVO pic : imageList) {
			gids.add(pic.getPk_image_library());
		}
		Map<String, OcrInvoiceVO> map = queryInvoiceInfo(pk_corp, gids);
		for (PictureBrowseVO pic : imageList) {
			if (map.containsKey(pic.getPk_image_library())) {
				pic.setInvoice_info(map.get(pic.getPk_image_library()));
			}
		}
		return imageList;


	}
	
	public Map<String, OcrInvoiceVO> queryInvoiceInfo(String pk_corp, Collection<String> groups) {
		if (groups.size() == 0) {
			return null;
		}
		SQLParameter sp = new SQLParameter();
		StringBuilder sql = new StringBuilder();
		sql.append(" select e.*,oy.crelationid ocr_id from ynt_interface_invoice e ");
		sql.append(" join ynt_image_ocrlibrary oy on e.ocr_id = oy.pk_image_ocrlibrary ");
		sql.append(" where nvl(e.dr,0) = 0 and nvl(oy.dr,0) = 0 and ")
			.append(SqlUtil.buildSqlForIn("oy.crelationid", groups.toArray(new String[0])));
		if (!StringUtil.isEmpty(pk_corp)) {
            sql.append(" and e.pk_corp = ? ");
            sp.addParam(pk_corp);
        }
		List<OcrInvoiceVO> invoiceList = (List<OcrInvoiceVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(OcrInvoiceVO.class));
		
		Map<String, OcrInvoiceVO> map = new HashMap<String, OcrInvoiceVO>();
		for (OcrInvoiceVO ocrInvoiceVO : invoiceList) {
			map.put(ocrInvoiceVO.getOcr_id(), ocrInvoiceVO);
		}
		
		Set<String> invoiceIds = new HashSet<String>();
		for (OcrInvoiceVO ocrInvoiceVO : invoiceList) {
//			if (ocrInvoiceVO.getInvoicetype() != null
//					&& ocrInvoiceVO.getInvoicetype().indexOf("增值税") > -1) {
//			}
			invoiceIds.add(ocrInvoiceVO.getPk_invoice());
		}
		if (invoiceIds.size() > 0) {
			sp = new SQLParameter();
//			sp.addParam(pk_corp);
			sql = new StringBuilder();
			sql.append(" select * from ynt_interface_invoice_detail where ")
//			.append(" pk_corp = ? and ")
			.append(" nvl(dr,0) = 0 and")
			.append(SqlUtil.buildSqlForIn("pk_invoice", invoiceIds.toArray(new String[0])));
			
			List<OcrInvoiceDetailVO> details = (List<OcrInvoiceDetailVO>) singleObjectBO.executeQuery(sql.toString(), sp,
					new BeanListProcessor(OcrInvoiceDetailVO.class));
			
			if (details.size() > 0) {
				Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(details, new String[] { "pk_invoice" });
				for (OcrInvoiceVO ocrInvoiceVO : invoiceList) {
					if (detailMap.containsKey(ocrInvoiceVO.getPk_invoice())) {
						ocrInvoiceVO.setChildren(detailMap.get(ocrInvoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
					}
				}
			}
			
		}
		return map;
		
	}

	@Override
	public void reuploadImage(File[] infiles, String imgName, String pk_image_library, String reuploadName, String reuploadType, CorpVO corpvo) throws DZFWarpException {
		// 检查该图片是否已上传
//		String imgMD = img_groupserv.getUploadImgMD(infiles[0], corpvo);
		int dirTypeIndex = imgName.lastIndexOf(".");
		String dirFileType = imgName.substring(dirTypeIndex);

		// 校验图片组状态
		List<ImageGroupVO> imageGroups = img_groupserv.queryImageGroupByCondition(corpvo, imgName, "ImageName");
		ImageLibraryVO imageLibraryVO = img_groupserv.queryLibByID(corpvo.getPk_corp(), pk_image_library);
		if ((imageLibraryVO == null) || !imgName.equals(imageLibraryVO.getImgname())) {
			throw new BusinessException("校验图片信息失败，请检查");
		}
		if (imageGroups.size() > 1) {
			throw new BusinessException("校验图片组状态失败，请检查");
		}

		String imgPathName = imageLibraryVO.getImgpath();
		String simgFileNm = imageLibraryVO.getSmallimgpath();
		String mimgFileNm = imageLibraryVO.getMiddleimgpath();
		// int index = imgPathName.lastIndexOf("/") == -1 ?
		// imgPathName.lastIndexOf("\\") : imgPathName.lastIndexOf("/");
		// imgPathName = imgPathName.substring(index + 1);
		String type = null;
		// if(imageLibraryVO != null &&
		// imgPathName.equals(imageLibraryVO.getImgname())){
		// imgPathName = imageLibraryVO.getImgpath();
		if (imgPathName.startsWith("ImageOcr")) {
			type = "ImageOcr";
		} else {
			int pathindex = imgPathName.lastIndexOf("/") == -1 ? imgPathName.lastIndexOf("\\")
					: imgPathName.lastIndexOf("/");
			imgPathName = imgPathName.substring(pathindex + 1);

			int spathindex = simgFileNm.lastIndexOf("/") == -1 ? simgFileNm.lastIndexOf("\\")
					: simgFileNm.lastIndexOf("/");
			simgFileNm = simgFileNm.substring(spathindex + 1);

			if (!StringUtil.isEmpty(mimgFileNm)) {
				int mpathindex = mimgFileNm.lastIndexOf("/") == -1 ? mimgFileNm.lastIndexOf("\\")
						: mimgFileNm.lastIndexOf("/");
				mimgFileNm = mimgFileNm.substring(mpathindex + 1);
			}
			type = "vchImg";
		}
		// }
		File imgFile = getImageFolder(type, corpvo, imgPathName, imgName);
		File dir = getImageFolder(type, corpvo, null, imgName);

		ImageGroupVO imageGroupVO = imageGroups.get(0);
		Integer imageState = imageGroupVO.getIstate();
		if (imageState != null) {
			if (imageState != PhotoState.state0 && imageState != PhotoState.state80) {// 只有未处理以及退回图片可以进行重传
				throw new BusinessException("未处理以及已退回图片才能进行重传，请确认图片状态");
			}

		}
		// 2017-05-11 管理端批量上传的图片不能删除
		Integer sourcemode = imageGroupVO.getSourcemode();

		if (sourcemode != null && sourcemode.intValue() == PhotoState.SOURCEMODE_10) {
			String warnstr = String.format("图片%s是管理端上传的图片，不能重传！", imageLibraryVO.getImgname());
			throw new BusinessException(warnstr);
		}

		int reuploadTypeIndex = reuploadName.lastIndexOf(".");// 获取重传图片格式
		String reuploadFileType = reuploadName.substring(reuploadTypeIndex);
		if (StringUtil.isEmpty(dirFileType) || StringUtil.isEmpty(reuploadFileType)
				|| !dirFileType.toLowerCase().equals(reuploadFileType.toLowerCase())) {
			throw new BusinessException("重传图片格式与第一次上传图片格式不一致，请检查");
		}

		storageImgFile(infiles[0], imgFile, dirFileType, dir, null, null, simgFileNm, mimgFileNm, imageLibraryVO);
		// 校验第一次图片格式与重传图片格式是否一致,如批量上传插件做格式转化后，该校验无效
		// if(!(".jpg".equalsIgnoreCase(dirFileType) ||
		// ".jpeg".equalsIgnoreCase(dirFileType))){
		// int reuploadTypeIndex = reuploadName.lastIndexOf(".");//获取重传图片格式
		// String reuploadFileType = reuploadName.substring(reuploadTypeIndex);
		// if(!dirFileType.equalsIgnoreCase(reuploadFileType)){
		// throw new BusinessException("重传图片格式与第一次上传图片格式不一致，请检查");
		// }
		// }
		//
		// try{
		// if(".jpg".equalsIgnoreCase(dirFileType)
		// //暂时这么写，原因是批量上传并未对png以及bmp格式的文件做转化
		// && ("IMAGE/PNG".equalsIgnoreCase(reuploadType) ||
		// "IMAGE/BMP".equalsIgnoreCase(reuploadType))){
		// ImageCopyUtil.compressCoye(infiles[0],dir);
		// }else{
		// ImageCopyUtil.copy(infiles[0],dir);
		// }
		// }catch(Exception e){
		// throw new BusinessException("重传图片失败");
		// }

		// 更新及备份图片组信息
		img_groupserv.saveImageGroupBackUp(imageGroupVO, PhotoState.state3);// 重传
		// 更新图片信息
		imageLibraryVO.setAttributeValue("isback", DZFBoolean.FALSE);
//		imageLibraryVO.setAttributeValue("imgmd", imgMD);
		img_groupserv.update(imageLibraryVO);

	}

	private void storageImgFile(File srcFile, File destFile, String nameSuffixTemp, File dir, String nameSuffix,
			String imgFileNm, String simgFileNm, String mimgFileNm, ImageLibraryVO imageLibraryVO) {

		try {
			boolean isComBySale = srcFile.length() > 100 * 1024;
//			ImageCopyUtil.copy(srcFile, destFile);
			if (isComBySale && StringUtil.isEmpty(mimgFileNm)) {
				mimgFileNm = UUID.randomUUID().toString() + "." + nameSuffixTemp;
				imageLibraryVO.setAttributeValue("middleimgpath", mimgFileNm);
			} else if (!isComBySale) {
				mimgFileNm = null;
				imageLibraryVO.setAttributeValue("middleimgpath", mimgFileNm);
			}
			ImageCopyUtil.storageImgFile(srcFile, nameSuffixTemp, dir, null, null, simgFileNm, mimgFileNm, isComBySale);
		} catch (Exception e) {
			throw new BusinessException("保存图片失败");
		}

	}

	// private static File getImageFolder(String type,CorpVO corpvo, String
	// imgPathName,String imgName){
	// File dir = null;
	// String dateFolder = imgName.substring(0, 8);
	// //System.out.println(dateFolder);
	// if("vchImg".equals(type)){
	// String imgfolder = ImageCommonPath.getDataCenterPhotoPath() +
	// File.separator + corpvo.getUnitcode() + File.separator + dateFolder;
	// File f = new File(imgfolder);
	// if(!f.exists()){
	// f.mkdirs();
	// }
	// String folder =imgfolder + File.separator + imgPathName;
	// //DZFConstant.DZF_KJ_UPLOAD_BASE + imgfolder;
	// dir = new File(folder);
	// }
	//
	// return dir;
	// }

	private static File getImageFolder(String type, CorpVO corpvo, String imgPathName, String imgName) {
		File dir = null;
		String dateFolder = imgName.substring(0, 8);
		if ("vchImg".equals(type)) {
			String imgfolder = ImageCommonPath.getDataCenterPhotoPath() + "/" + corpvo.getUnitcode() + "/" + dateFolder;
			if (StringUtil.isEmpty(imgPathName)) {
				String folder = imgfolder; // DZFConstant.DZF_KJ_UPLOAD_BASE +
											// imgfolder;
				dir = new File(folder);
			} else {
				String folder = imgfolder + "/" + imgPathName; // DZFConstant.DZF_KJ_UPLOAD_BASE
																// + imgfolder;
				dir = new File(folder);
			}
		} else if ("ImageOcr".equals(type)) {
			// String imgfolder = ImageCommonPath.getDataCenterPhotoPath() + "/"
			// + corpvo.getUnitcode() + "/" + dateFolder;
			String folder = ImageCommonPath.getDataCenterPhotoPath() + "/" + imgPathName; // DZFConstant.DZF_KJ_UPLOAD_BASE
																							// +
																							// imgfolder;
			dir = new File(folder);
		}

		return dir;
	}

	private void accountCollect(TzpzHVO hvo, List<TzpzBVO> bvos, YntCpaccountVO[] accounts, VoucherPrintParam param) {
		Integer level = param.getAccount_level();
		Set<String> codes = new HashSet<String>();
		Map<String, Integer> accountIndex = new HashMap<String, Integer>();
		for (TzpzBVO tzpzBVO : bvos) {
			codes.add(tzpzBVO.getVcode());
		}
		Map<String, YntCpaccountVO> assignAccounts = getAssignLevelAccount(codes, level, accounts);
		for (ListIterator<TzpzBVO> bvoIt = bvos.listIterator(); bvoIt.hasNext();) {
			TzpzBVO tzpzBVO = bvoIt.next();
			if (assignAccounts.containsKey(tzpzBVO.getVcode())) {
				YntCpaccountVO assign = assignAccounts.get(tzpzBVO.getVcode());
				StringBuilder key = new StringBuilder();
				key.append(assign.getPk_corp_account());
				//非末级，肯定不显示辅助
				if (assign.getIsleaf() == null || !assign.getIsleaf().booleanValue()) {
					tzpzBVO.setFzhs_list(null);
					tzpzBVO.setPk_currency(null);
					tzpzBVO.setInvcode(null);
					tzpzBVO.setInvname(null);
					tzpzBVO.setNnumber(null);
					tzpzBVO.setMeaname(null);
					tzpzBVO.setIsCur(null);
				} else {
					// 是否显示辅助核算
//					if ((param.getShow_auxiliary() == null || !param.getShow_auxiliary().booleanValue())
//							&& (param.getShow_auxiliary_noinv() ==null || !param.getShow_auxiliary_noinv().booleanValue())) {
//						tzpzBVO.setFzhs_list(null);
//						tzpzBVO.setInvname(null);
//						tzpzBVO.setNnumber(null);
//						tzpzBVO.setMeaname(null);
//					} else {
						// 是否显示辅助核算 (不打印存货)
//						if(param.getShow_auxiliary_noinv() !=null && param.getShow_auxiliary_noinv().booleanValue()){
//							if(!StringUtil.isEmpty(tzpzBVO.getFzhsx6())){
//								tzpzBVO.setFzhs_list(null);
//								tzpzBVO.setInvname(null);
//								tzpzBVO.setNnumber(null);
//								tzpzBVO.setMeaname(null);
//							}
//						}
						
						List<AuxiliaryAccountBVO> fzhs = tzpzBVO.getFzhs_list();
						if (fzhs != null && fzhs.size() > 0) {
							for (AuxiliaryAccountBVO auxiliaryAccountBVO : fzhs) {
								key.append(auxiliaryAccountBVO.getCode());
							}
						}
						if (tzpzBVO.getInvcode() != null)
							key.append(tzpzBVO.getInvcode());
//					}
				}

				if (tzpzBVO.getPk_currency() != null
						&& !IGlobalConstants.RMB_currency_id.equals(tzpzBVO.getPk_currency())) {
					key.append(tzpzBVO.getPk_currency());
					if (tzpzBVO.getNrate() != null) {
						key.append(tzpzBVO.getNrate().toString());
					}
				}
				// 是否按借贷方向分别汇总
				if (param.getDeb_cred() != null && param.getDeb_cred().booleanValue()) {
					key.append((tzpzBVO.getJfmny() == null ||  tzpzBVO.getJfmny().doubleValue() == 0) ? "1" : "0");
				}
				String keyStr = key.toString();
				if (accountIndex.containsKey(keyStr)) {
					TzpzBVO bvo = bvos.get(accountIndex.get(keyStr));
					bvo.setJfmny(SafeCompute.add(bvo.getJfmny(), tzpzBVO.getJfmny()));
					bvo.setDfmny(SafeCompute.add(bvo.getDfmny(), tzpzBVO.getDfmny()));
					bvo.setYbjfmny(SafeCompute.add(bvo.getYbjfmny(), tzpzBVO.getYbjfmny()));
					bvo.setYbdfmny(SafeCompute.add(bvo.getYbdfmny(), tzpzBVO.getYbdfmny()));
					bvoIt.remove();
				} else {
					tzpzBVO.setVcode(assign.getAccountcode());
					tzpzBVO.setVname(assign.getAccountname());
					tzpzBVO.setKmmchie(assign.getFullname());
					// tzpzBVO.setMeaname(assign.getMeasurename());
					accountIndex.put(keyStr.toString(), bvoIt.previousIndex());
				}
			}
//			else if((param.getShow_auxiliary() == null || !param.getShow_auxiliary().booleanValue())
//					&& (param.getShow_auxiliary_noinv() ==null || !param.getShow_auxiliary_noinv().booleanValue()) 
//					){//如果不存在，则处理是否显示辅助
//				tzpzBVO.setFzhs_list(null);
//				tzpzBVO.setInvname(null);
//				tzpzBVO.setNnumber(null);
//				tzpzBVO.setMeaname(null);
//			}else if(param.getShow_auxiliary_noinv() !=null && param.getShow_auxiliary_noinv().booleanValue()){
//				//是否显示辅助核算 (不打印存货)
//				if(!StringUtil.isEmpty(tzpzBVO.getFzhsx6())){
//					tzpzBVO.setFzhs_list(null);
//					tzpzBVO.setInvname(null);
//					tzpzBVO.setNnumber(null);
//					tzpzBVO.setMeaname(null);
//				}
//			}
		}
		calculateSum(hvo, bvos);
		if (param.getHide_zero() != null && param.getHide_zero().booleanValue()) {
			removeZeroEntry(bvos);
		}
	}

	private Map<String, YntCpaccountVO> getAssignLevelAccount(Set<String> codes, Integer level,
			YntCpaccountVO[] accounts) {
		Map<String, YntCpaccountVO> accountMap = new HashMap<String, YntCpaccountVO>();
		for (YntCpaccountVO yntCpaccountVO : accounts) {
			for (Iterator<String> it = codes.iterator(); it.hasNext();) {
				String code = it.next();
				if (code == null) {
					continue;
				}
				if (code.equals(yntCpaccountVO.getAccountcode())
						&& yntCpaccountVO.getAccountlevel().intValue() < level.intValue()
						|| code.startsWith(yntCpaccountVO.getAccountcode())
						&& yntCpaccountVO.getAccountlevel().intValue() == level.intValue()) {
					accountMap.put(code, yntCpaccountVO);
					it.remove();
				}
			}
			if (codes.size() == 0)
				break;
		}
		return accountMap;

	}

	private void removeZeroEntry(List<TzpzBVO> bvos) {
		for (Iterator<TzpzBVO> it = bvos.iterator(); it.hasNext();) {
			TzpzBVO bvo = it.next();
			if (bvo.getJfmny().doubleValue() == 0 && bvo.getDfmny().doubleValue() == 0) {
				it.remove();
			}
		}
		// 按借贷方排序
		sortEntryByDirection(bvos);
	}

	private void calculateSum(TzpzHVO hvo, List<TzpzBVO> bvos) {
		DZFDouble jfSum = DZFDouble.ZERO_DBL;
		DZFDouble dfSum = DZFDouble.ZERO_DBL;
		for (TzpzBVO tzpzBVO : bvos) {
			mergeDifferentDirection(tzpzBVO);
			jfSum = SafeCompute.add(jfSum, tzpzBVO.getJfmny());
			dfSum = SafeCompute.add(dfSum, tzpzBVO.getDfmny());
		}
		if (hvo != null) {
			hvo.setJfmny(jfSum);
			hvo.setDfmny(dfSum);
		}
	}
	/**
	 * 借贷方都有值，合并到一个方向
	 * 
	 * @param bvo
	 */
	private void mergeDifferentDirection(TzpzBVO bvo) {
		DZFDouble jf = bvo.getJfmny();
		DZFDouble df = bvo.getDfmny();
		boolean hasJf = jf != null && jf.doubleValue() != 0;
		boolean hasDf = df != null && df.doubleValue() != 0;
		if (hasJf && hasDf) {
			// 取较大值方向
			DZFDouble minus = SafeCompute.sub(jf, df);
			if (minus.doubleValue() >= 0) {
				jf = minus;
				df = DZFDouble.ZERO_DBL;
				bvo.setVdirect(0);
			} else {
				df = SafeCompute.sub(df, jf);
				jf = DZFDouble.ZERO_DBL;
				bvo.setVdirect(1);
			}
			bvo.setJfmny(jf);
			bvo.setDfmny(df);
		} else if (hasJf) {
			bvo.setVdirect(0);
		} else {
			bvo.setVdirect(1);
		}
	}

	@Override
	public void updateCreator(List<String> ids, String newCreator) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(newCreator);
		for (String id : ids) {
			sp.addParam(id);
		}
		StringBuilder sb = new StringBuilder();
		String inSql = SQLHelper.getInSQL(ids);
		sb.append(" update ynt_tzpz_h set coperatorid = ? where pk_tzpz_h in ").append(inSql);
		sb.append(" and nvl(dr, 0) = 0 ");
		singleObjectBO.executeUpdate(sb.toString(), sp);
	}

	private void sortSypz(List<TzpzHVO> period_hvos){
		if (period_hvos == null || period_hvos.size() <= 1) {
			return;
		}
		List<String> h = new ArrayList();

		for(TzpzHVO tzpzHVO : period_hvos){
			h.add(tzpzHVO.getPk_tzpz_h());
		}


		SQLParameter sp = new SQLParameter();

//		String inSql = SQLHelper.getInSQL(h);
		String inSql = SqlUtil.buildSqlForIn(" pk_tzpz_h ", h.toArray(new String[0]));
		StringBuilder sb = new StringBuilder();
		sb.append("select * from ynt_tzpz_b where ").append(inSql).append(" and nvl(dr, 0) = 0 ");

//		for(TzpzHVO tzpzHVO : period_hvos){
//			sp.addParam(tzpzHVO.getPk_tzpz_h());
//		}

		List<TzpzBVO> tzpzBVOList = (List<TzpzBVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(TzpzBVO.class));

		Set<String> zglist = new HashSet();
		Set<String> srlist = new HashSet();
		Set<String> fylist = new HashSet();
		Set<String> wfplrlist = new HashSet<>();

		for(TzpzBVO tzpzBVO : tzpzBVOList){
			if(!StringUtil.isEmpty(tzpzBVO.getZy()) && tzpzBVO.getZy().indexOf("暂估") != -1){
				zglist.add(tzpzBVO.getPk_tzpz_h());
				continue;
			}
			if(!StringUtil.isEmpty(tzpzBVO.getZy()) && tzpzBVO.getZy().indexOf("收入") != -1){
				srlist.add(tzpzBVO.getPk_tzpz_h());
				continue;
			}
			if(!StringUtil.isEmpty(tzpzBVO.getZy()) && tzpzBVO.getZy().indexOf("费用") != -1){
				fylist.add(tzpzBVO.getPk_tzpz_h());
				continue;
			}

			if(!StringUtil.isEmpty(tzpzBVO.getZy()) && tzpzBVO.getZy().indexOf("损益") != -1){
				wfplrlist.add(tzpzBVO.getPk_tzpz_h());
				continue;
			}
		}


//		李姐确认的：期末结转的凭证都排在后面，结转凭证顺序：
//		折旧生成的凭证、     HP66 HP67
//		汇兑损益凭证、	HCH10535
//		成本结转凭证、   HP34
//		增值税、         HP120
//		附加税、	 		HP39
//		所得税、         HP125
//		期间损益收入类凭证、 HP32
//		期间损益费用类凭证   HP32
		List<TzpzHVO> jtzj_hvos = new ArrayList<>(); //计提折旧  HP66 HP67
		List<TzpzHVO> hdsy_hvos = new ArrayList<>(); //汇兑损益  HCH10535
		List<TzpzHVO> cbjz_hvos = new ArrayList<>(); //成本结转  HP34
		List<TzpzHVO> zzs_hvos = new ArrayList<>(); //增值税     HP120
		List<TzpzHVO> fjs_hvos = new ArrayList<>(); //附加税     HP39
		List<TzpzHVO> sds_hvos = new ArrayList<>(); //所得税     HP125
		List<TzpzHVO> syjz_hvos_sr = new ArrayList<>(); //期间损益收入  HP32
		List<TzpzHVO> syjz_hvos_fy = new ArrayList<>(); //期间损益费用  HP32
		List<TzpzHVO> syjz_hvos_wfp = new ArrayList<>(); // 损益结转 HP32
		List<TzpzHVO> lrjz_hvos = new ArrayList<>(); //年结 利润结转 HP28

		Iterator<TzpzHVO> it = period_hvos.iterator();
		while(it.hasNext()){
			TzpzHVO t = it.next();
			if(zglist.contains(t.getPk_tzpz_h())){
				continue;
			}
			//计提折旧  HP66 HP67
			if(IBillTypeCode.HP66.equals(t.getSourcebilltype()) || IBillTypeCode.HP67.equals(t.getSourcebilltype())){
				jtzj_hvos.add(t);
				it.remove();
				continue;
			}
			//汇兑损益  HCH10535
			if("HCH10535".equals(t.getSourcebilltype())){
				hdsy_hvos.add(t);
				it.remove();
				continue;
			}
			//成本结转  HP34
			if(IBillTypeCode.HP34.equals(t.getSourcebilltype())){
				cbjz_hvos.add(t);
				it.remove();
				continue;
			}
			//增值税     HP120
			if(IBillTypeCode.HP120.equals(t.getSourcebilltype())){
				zzs_hvos.add(t);
				it.remove();
				continue;
			}
			//附加税     HP39
			if(IBillTypeCode.HP39.equals(t.getSourcebilltype())){
				fjs_hvos.add(t);
				it.remove();
				continue;
			}
			//所得税     HP125
			if(IBillTypeCode.HP125.equals(t.getSourcebilltype())){
				sds_hvos.add(t);
				it.remove();
				continue;
			}
			//期间损益收入  HP32
			if(IBillTypeCode.HP32.equals(t.getSourcebilltype()) && srlist.contains(t.getPk_tzpz_h())){
				syjz_hvos_sr.add(t);
				it.remove();
				continue;
			}

			//期间损益收入  HP32
			if(IBillTypeCode.HP32.equals(t.getSourcebilltype()) && fylist.contains(t.getPk_tzpz_h())){
				syjz_hvos_fy.add(t);
				it.remove();
				continue;
			}
			//期间损益未分配利润
			if(IBillTypeCode.HP32.equals(t.getSourcebilltype()) && wfplrlist.contains(t.getPk_tzpz_h())){
				syjz_hvos_wfp.add(t);
				it.remove();
				continue;
			}

			//年结利润结转  HP28
			if(IBillTypeCode.HP28.equals(t.getSourcebilltype())){
				lrjz_hvos.add(t);
				it.remove();
				continue;
			}

		}

		period_hvos.addAll(jtzj_hvos);
		period_hvos.addAll(hdsy_hvos);
		period_hvos.addAll(cbjz_hvos);
		period_hvos.addAll(zzs_hvos);
		period_hvos.addAll(fjs_hvos);
		period_hvos.addAll(sds_hvos);
		period_hvos.addAll(syjz_hvos_sr);
		period_hvos.addAll(syjz_hvos_fy);
		period_hvos.addAll(syjz_hvos_wfp);
		period_hvos.addAll(lrjz_hvos);
	}

	@Override
	public String updateNumByDate(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException {
		String period_begin = DateUtils.getPeriod(beginperiod);
		String period_end = DateUtils.getPeriod(endperiod);
		List<String> corps = Arrays.asList(pk_corps);
		String corp_in_sql = SQLHelper.getInSQL(corps);
		SQLParameter sp = SQLHelper.getSQLParameter(corps);
		sp.addParam(period_begin);
		sp.addParam(period_end);
		StringBuilder sql = new StringBuilder();
		sql.append("select pk_tzpz_h, sourcebilltype, pk_corp, pzh, period from ynt_tzpz_h ");
		sql.append(" where pk_corp in ").append(corp_in_sql).append(" and period >= ? and period <= ? ");
		sql.append(" and nvl(dr, 0) = 0 order by doperatedate, pzh ");
		List<TzpzHVO> hvos = (List<TzpzHVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));
		Map<String, List<TzpzHVO>> groupMap = DZfcommonTools.hashlizeObject(hvos, new String[] { "pk_corp", "period" });
		List<TzpzHVO> sortedVO = new ArrayList<TzpzHVO>();
		StringBuilder returnmeg = new StringBuilder();
		for (String key : groupMap.keySet()) {
			List<TzpzHVO> period_hvos = groupMap.get(key);
			String[] info = key.split(",");
			String pk_corp = info[0];
			String period = info[1];
			boolean isgz = qmgzService.isGz(pk_corp, period);
			if (isgz) {// 是否关账
				String corpname = corpService.queryByPk(pk_corp).getUnitname();
				returnmeg.append("公司：" + corpname + "在" + period + "已经关账，不允许整理哦。<br/>");
				continue;
			}

			sortSypz(period_hvos);

			for (int index = 0; index < period_hvos.size(); index++) {
				TzpzHVO hvo = period_hvos.get(index);
				hvo.setPzh(getVchNum(index + 1));
				updateIctradePzh(pk_corp, hvo);
			}
			sortedVO.addAll(period_hvos);
		}
		TzpzHVO[] sortedVOArray = sortedVO.toArray(new TzpzHVO[0]);
		int updateCount = singleObjectBO.updateAry(sortedVOArray, new String[] { "pzh" });

		return returnmeg.toString() + "成功整理" + updateCount + "条";
	}

	private String getVchNum(int index) {
		String num = null;
		if (index <= 9) {
			num = "000" + index;
		} else if (index > 9 && index < 100) {
			num = "00" + index;
		} else if (index >= 100 && index < 1000) {
			num = "0" + index;
		} else {
			num = "" + index;
		}
		return num;
	}

	@Override
	public List<TzpzHVO> saveImportVoucher(CorpVO corp, String user_id, String fileType, InputStream inputStream,
			boolean checkVoucherNumber) throws DZFWarpException {
		List<TzpzHVO> vouchers = new ArrayList<TzpzHVO>();
		try {
			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(inputStream);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(inputStream);
			} else {
				throw new BusinessException("不支持的文件格式 ");
			}
			String pk_corp = corp.getPk_corp();
			DZFDate beginDate = corp.getBegindate();

			Map<String, YntCpaccountVO> accountMap = new HashMap<String, YntCpaccountVO>();
			YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
			for (YntCpaccountVO account : accounts) {
				if (account.getIsleaf().booleanValue()) {
					accountMap.put(account.getAccountcode(), account);
				}
			}
			
			int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
			int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
			int ratePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf011"));
			
			BdCurrencyVO[] currencys = (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class,
					"nvl(dr,0)=0", new SQLParameter());
			Map<String, String> currencyMap = new HashMap<String, String>();
			for (BdCurrencyVO bdCurrencyVO : currencys) {
				currencyMap.put(bdCurrencyVO.getCurrencycode(), bdCurrencyVO.getPk_currency());
			}

			AuxiliaryAccountHVO[] auxhvos = gl_fzhsserv.queryH(pk_corp);
			Map<Integer, AuxiliaryAccountHVO> auxCodeMap = new HashMap<Integer, AuxiliaryAccountHVO>();

			for (AuxiliaryAccountHVO auxiliaryAccountHVO : auxhvos) {
				auxCodeMap.put(auxiliaryAccountHVO.getCode(), auxiliaryAccountHVO);
			}

			Map<String, AuxiliaryAccountBVO> auxMap = new HashMap<String, AuxiliaryAccountBVO>();
			AuxiliaryAccountBVO[] auxbvos = gl_fzhsserv.queryAllB(pk_corp);
			for (AuxiliaryAccountBVO auxiliaryAccountBVO : auxbvos) {
				auxMap.put(auxiliaryAccountBVO.getPk_auacount_h() + auxiliaryAccountBVO.getCode(), auxiliaryAccountBVO);
			}

			Sheet sheet1 = impBook.getSheetAt(0);
			List<TzpzBVO> bvos = new ArrayList<TzpzBVO>();
			String preVoucherTag = null;
			TzpzHVO hvo = null;
			DZFDouble jfTotal = DZFDouble.ZERO_DBL;
			DZFDouble dfTotal = DZFDouble.ZERO_DBL;

			boolean isic = IcCostStyle.IC_ON.equals(corp.getBbuildic());//是否启用进销存模块
			boolean isIcToGl = corp.getIbuildicstyle() != null && corp.getIbuildicstyle() == 1;

			int length = sheet1.getLastRowNum();

			Map<String, Integer> headMap = new HashMap<String, Integer>();
			Row headRow = sheet1.getRow(0);
			for (int i = 0; i < 50; i++) {
				Cell cell = headRow.getCell(i);
				if (cell == null) {
					break;
				}
				String val = cell.getRichStringCellValue().getString();
				val = val.trim();
				headMap.put(val, i);
			}
			for (int iBegin = 1; iBegin <= length; iBegin++) {

				Row row = sheet1.getRow(iBegin);
				TzpzBVO bvo = new TzpzBVO();
				bvo.setPk_corp(pk_corp);
				// 日期
				String date = getCellValue(row, iBegin, headMap.get("日期"), false);
				// 凭证号
				String pzh = getCellValue(row, iBegin, headMap.get("凭证号"), false);

				if (StringUtil.isEmpty(date) && StringUtil.isEmpty(pzh)) {
					continue;
				}
				if (StringUtil.isEmpty(date)) {
					throw new BusinessException("第" + (iBegin + 1) + "行日期 为空，请确认");
				}
				DZFDate vchDate = new DZFDate(date);

				if (beginDate.after(vchDate)) {
					throw new BusinessException("第" + (iBegin + 1) + "行凭证日期" + vchDate.toString() + "早于建账日，请确认");
				}

				if (headMap.containsKey("公司")) {
					if (!corp.getUnitname().equals(getCellValue(row, iBegin, headMap.get("公司"), false))) {
						throw new BusinessException("只能导入当前登录公司凭证，请确认");
					}
				} else if (headMap.containsKey("公司名称")) {
					if (!corp.getUnitname().equals(getCellValue(row, iBegin, headMap.get("公司名称"), false))) {
						throw new BusinessException("只能导入当前登录公司凭证，请确认");
					}
				}

				if (StringUtil.isEmpty(pzh)) {
					throw new BusinessException("第" + (iBegin + 1) + "行凭证号 为空，请确认");
				}
				if (pzh.length() != 4) {
					throw new BusinessException("第" + (iBegin + 1) + "行凭证号格式错误，请确认");
				}

				String voucherTag = date + pzh;
				if (preVoucherTag == null || !preVoucherTag.equals(voucherTag)) {
					preVoucherTag = voucherTag;
					if (hvo != null) {
						hvo.setJfmny(jfTotal);
						hvo.setDfmny(dfTotal);
						hvo.setChildren(bvos.toArray(new TzpzBVO[0]));
						vouchers.add(hvo);
					}
					hvo = new TzpzHVO();
					jfTotal = DZFDouble.ZERO_DBL;
					dfTotal = DZFDouble.ZERO_DBL;

					bvos.clear();

					hvo.setDr(0);
					hvo.setPk_corp(pk_corp);
					hvo.setPzlb(0);
					hvo.setCoperatorid(user_id);
					hvo.setIshasjz(DZFBoolean.FALSE);
					hvo.setDoperatedate(vchDate);
					hvo.setPzh(pzh);
					hvo.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
					hvo.setIsfpxjxm(DZFBoolean.FALSE);
					// 附单据
					String bills = getCellValue(row, iBegin, headMap.get("附单据"), false);
					if (!StringUtil.isEmpty(bills)) {
						if (bills.matches("^[0-9]+$")) {
							hvo.setNbills(Integer.valueOf(bills));
						} else {
							throw new BusinessException("第" + (iBegin + 1) + "行附单据格式错误，不为0或正整数");
						}
					} else {
						hvo.setNbills(0);
					}
					// 票据类型
					String fpTypeName = getCellValue(row, iBegin, headMap.get("票据类型"), false);
					Integer fpStyle = null;
					if (!StringUtil.isEmpty(fpTypeName)) {
						switch (fpTypeName) {
							case "普票":
								fpStyle = 1;
								break;
							case "专票":
								fpStyle = 2;
								break;
							case "未开票":
								fpStyle = 3;
								break;
							default:
								break;
						}
					}
					hvo.setFp_style(fpStyle);
				}

				// 摘要
				String zy = getCellValue(row, iBegin, headMap.get("摘要"), false);
				if (StringUtil.isEmpty(zy)) {
					throw new BusinessException("第" + (iBegin + 1) + "行摘要 为空，请确认");
				}
				// 科目编码
				String accountCode = getCellValue(row, iBegin, headMap.get("科目编码"), false);
				if (StringUtil.isEmpty(accountCode)) {
					throw new BusinessException("第" + (iBegin + 1) + "行科目编码 为空，请确认");
				}
				String accountName = getCellValue(row, iBegin, headMap.get("科目名称"), false);
				if (StringUtil.isEmpty(accountName)) {
					throw new BusinessException("第" + (iBegin + 1) + "行科目名称 为空，请确认");
				}
				YntCpaccountVO accountVO = accountMap.get(accountCode);
				if (accountVO == null) {
					throw new BusinessException("第" + (iBegin + 1) + "行科目编码 " + accountCode + "与系统编码不一致，请确认");
				} else if (!accountVO.getFullname().equals(accountName)) {
					throw new BusinessException("第" + (iBegin + 1) + "行科目名称 " + accountName + "与系统名称不一致，请确认");
				}

				// 借方金额
				String jf = getCellValue(row, iBegin, headMap.get("借方金额"), true);
				// 贷方金额
				String df = getCellValue(row, iBegin, headMap.get("贷方金额"), true);
				DZFDouble dfmny = new DZFDouble(df).setScale(2, DZFDouble.ROUND_HALF_UP);
				DZFDouble jfmny = new DZFDouble(jf).setScale(2, DZFDouble.ROUND_HALF_UP);
				if (dfmny.doubleValue() == 0 && jfmny.doubleValue() == 0) {
					throw new BusinessException("第" + (iBegin + 1) + "行金额为0，请确认");
				}
				if (dfmny.doubleValue() != 0 && jfmny.doubleValue() != 0) {
					throw new BusinessException("第" + (iBegin + 1) + "行借贷方不能同时填写，请检查");
				}
				DZFDouble mny = dfmny.doubleValue() == 0 ? jfmny : dfmny;
				// 数量
				String number = getCellValue(row, iBegin, headMap.get("数量"), true);
				DZFDouble nnumber = new DZFDouble(number, numPrecision);
				// 单价
				String price = getCellValue(row, iBegin, headMap.get("单价"), true);
				DZFDouble nprice = new DZFDouble(price, pricePrecision);
				if (accountVO.getIsnum() != null && accountVO.getIsnum().booleanValue()) {
					//zpm 不校验这些东西。有些调整成本的科目，数量和单价就是空，或者0
//					if (nnumber.doubleValue() == 0) {
//						throw new BusinessException("第" + (iBegin + 1) + "行缺失数量 ，请检查");
//					}
//					if (nprice.doubleValue() == 0) {
//						throw new BusinessException("第" + (iBegin + 1) + "行缺失单价，请检查");
//					}

					bvo.setNnumber(nnumber);
					bvo.setNprice(nprice);
//					if (!mny.equals(nprice.multiply(nnumber, 2))) {
//						throw new BusinessException("第" + (iBegin + 1) + "行数量*单价不等于金额，请检查");
//					}
				}

				String yb = getCellValue(row, iBegin, headMap.get("原币金额"), true);
				DZFDouble ybmny = new DZFDouble(yb).setScale(4, DZFDouble.ROUND_HALF_UP);
				if (dfmny.doubleValue() == 0) {
					bvo.setYbdfmny(DZFDouble.ZERO_DBL);
					bvo.setYbjfmny(ybmny);
				} else {
					bvo.setYbdfmny(ybmny);
					bvo.setYbjfmny(DZFDouble.ZERO_DBL);
				}

				String curCode = getCellValue(row, iBegin, headMap.get("币别"), false);
				if (StringUtil.isEmpty(curCode)) {
					curCode = "CNY";
				} else {
					curCode = curCode.toUpperCase();
				}
				if (accountVO.getExc_pk_currency() == null && !"CNY".equals(curCode) // 科目币种不一致
						|| !currencyMap.containsKey(curCode) // 币种不存在
						|| accountVO.getExc_pk_currency() != null
						&& accountVO.getExc_pk_currency().indexOf(currencyMap.get(curCode)) == -1) {
					throw new BusinessException("第" + (iBegin + 1) + "行科目外汇核算与币别匹配不一致，请确认");
				} else {
//					bvo.setPk_currency(yntBoPubUtil.getCNYPk());
					bvo.setPk_currency(currencyMap.get(curCode));
				}

				String rate = getCellValue(row, iBegin, headMap.get("汇率"), true);
				DZFDouble nrate = new DZFDouble(rate, ratePrecision);
				if (nrate.doubleValue() == 0) {
					nrate = new DZFDouble(1);
				}
				bvo.setNrate(nrate);
				StringBuilder isfzhs = new StringBuilder(accountVO.getIsfzhs());
				if (isic && !isIcToGl
						&& accountVO.getIsnum() != null && accountVO.getIsnum().booleanValue()) {
					isfzhs.setCharAt(5, '1');
				}
				for (int charIndex = 0; charIndex < 10; charIndex++) {
					if (isfzhs.charAt(charIndex) == '1') {
						AuxiliaryAccountHVO auxhvo = auxCodeMap.get(charIndex + 1);
						if (auxhvo != null) {
							String auxCode = getCellValue(row, iBegin, headMap.get(auxhvo.getName() + "编码"), false);
							if (StringUtil.isEmpty(auxCode)) {
								throw new BusinessException(
										"第" + (iBegin + 1) + "行辅助项目" + auxhvo.getName() + "编码为空，请确认");
							}
							String auxName = getCellValue(row, iBegin, headMap.get(auxhvo.getName() + "名称"), false);
							if (StringUtil.isEmpty(auxName)) {
								throw new BusinessException(
										"第" + (iBegin + 1) + "行辅助项目" + auxhvo.getName() + "名称为空，请确认");
							}
							AuxiliaryAccountBVO fzhs_b = auxMap.get(auxhvo.getPk_auacount_h() + auxCode);
							if (fzhs_b == null) {
								throw new BusinessException(
										"第" + (iBegin + 1) + "行辅助项目编码" + auxCode + "与系统不一致，请确认");
							} else if (!fzhs_b.getName().equals(auxName)) {
								throw new BusinessException(
										"第" + (iBegin + 1) + "行辅助项目名称" + auxName + "与系统不一致，请确认");
							}
							if (charIndex == 5 && isic) {
								bvo.setPk_inventory(fzhs_b.getPk_auacount_b());
							} else {
								bvo.setAttributeValue("fzhsx" + (charIndex + 1), fzhs_b.getPk_auacount_b());
							}
						}
					}
				}

				bvo.setZy(zy);

				bvo.setPk_accsubj(accountVO.getPk_corp_account());
				bvo.setDfmny(dfmny);
				dfTotal = dfTotal.add(dfmny);
				bvo.setJfmny(jfmny);
				jfTotal = jfTotal.add(jfmny);

				bvo.setDirect(dfmny.doubleValue() != 0 ? 0 : 1);

				bvos.add(bvo);
			}

			if (hvo != null) {
				hvo.setJfmny(jfTotal);
				hvo.setDfmny(dfTotal);
				hvo.setChildren(bvos.toArray(new TzpzBVO[0]));
				vouchers.add(hvo);
			}

			// 提示凭证重复
			if (checkVoucherNumber) {
				SQLParameter sp = new SQLParameter();
				String sql = "select 1 from ynt_tzpz_h where pk_corp = ?"
						+ " and period = ? and pzh = ? and nvl(dr,0)=0";
				for (TzpzHVO voucher : vouchers) {
					sp.addParam(pk_corp);
					sp.addParam(DateUtils.getPeriod(voucher.getDoperatedate()));
					sp.addParam(voucher.getPzh());

					if (singleObjectBO.isExists(pk_corp, sql, sp)) {
						throw new BusinessException("-150");
					}
					sp.clearParams();
				}
			}

			for (TzpzHVO voucher : vouchers) {
				gl_tzpzserv.saveVoucher(corp, voucher);
			}

		} catch (FileNotFoundException e) {
			throw new BusinessException("导入文件未找到 ");
		} catch (IOException e) {
			throw new BusinessException("导入文件格式错误");
		}
		return vouchers;
	}

	private String getCellValue(Row row, int rowIndex, Integer colIndex, boolean isNum) {
		if (colIndex == null) {
			return null;
		}
		String val = null;
		Cell cell = row.getCell(colIndex);
		if (cell == null) {
			return val;
		}
		if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
			val = cell.getRichStringCellValue().getString();
			val = val.trim();
		} else if (HSSFDateUtil.isCellDateFormatted(cell)) {
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
			val = dateFormatter.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
		} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
			DecimalFormat format = isNum ? new DecimalFormat("#.########") : new DecimalFormat("#");
			double numVal = cell.getNumericCellValue();
			val = format.format(numVal);
		}

		return val;
	}

	@Override
	public byte[] exportExcel(String ids) throws DZFWarpException {

		List<TzpzHVO> vouchers = queryByIDs(ids, null);

		List<String> corps = new ArrayList<String>();
		for (TzpzHVO hvo : vouchers) {
			if (!corps.contains(hvo.getPk_corp())) {
				corps.add(hvo.getPk_corp());
			}
		}
		StringBuilder sb = new StringBuilder();
		SQLParameter sp = new SQLParameter();
		sb.append(" nvl(dr,0)=0 and (pk_corp = '").append(IGlobalConstants.DefaultGroup).append("' or ")
				.append(SqlUtil.buildSqlForIn("pk_corp", corps.toArray(new String[0]))).append(") order by code");
		List<AuxiliaryAccountHVO> auxhvos = (List<AuxiliaryAccountHVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new Class[] { AuxiliaryAccountHVO.class });

		List<String> fzNames = new ArrayList<String>();
		Map<String, AuxiliaryAccountHVO> auxhvoMap = new HashMap<String, AuxiliaryAccountHVO>();
		for (AuxiliaryAccountHVO auxhvo : auxhvos) {
			auxhvoMap.put(auxhvo.getPk_auacount_h(), auxhvo);
			if (!fzNames.contains(auxhvo.getName())) {
				fzNames.add(auxhvo.getName());
			}
		}

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("凭证导出");

		sheet.setDefaultColumnWidth(15);
		BdCurrencyVO[] currencys = (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class, "nvl(dr,0)=0",
				new SQLParameter());

		Map<String, String> currencyMap = new HashMap<String, String>();
		for (BdCurrencyVO bdCurrencyVO : currencys) {
			currencyMap.put(bdCurrencyVO.getPk_currency(), bdCurrencyVO.getCurrencycode());
		}

		HSSFFont font = workbook.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setBold(true);
		HSSFCellStyle headStyle = workbook.createCellStyle();
		headStyle.setFont(font);
		headStyle.setAlignment(HorizontalAlignment.CENTER); // 水平居中
		headStyle.setVerticalAlignment(VerticalAlignment.CENTER); // 垂直居中

		Map<String, Integer> headIndexMap = createSheetHead(sheet, headStyle, fzNames);

		Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();

		HSSFDataFormat df = workbook.createDataFormat(); // 此处设置数据格式
		HSSFCellStyle numberCell2 = workbook.createCellStyle();
		font = workbook.createFont();
		font.setBold(false);
		numberCell2.setFont(font);
		numberCell2.setAlignment(HorizontalAlignment.CENTER);
		numberCell2.setVerticalAlignment(VerticalAlignment.CENTER);
		numberCell2.setDataFormat(df.getFormat("#,##0.00")); // 保留小数点后2位
		styleMap.put(2, numberCell2);

		HSSFCellStyle numberCell4 = workbook.createCellStyle();
		numberCell4.cloneStyleFrom(numberCell2);
		numberCell4.setDataFormat(df.getFormat("#,##0.0000"));
		styleMap.put(4, numberCell4);
		
		Map<String, Integer> precisionMap = new HashMap<String, Integer>();

		int rowIndex = 1;
		for (TzpzHVO hvo : vouchers) {
			CellStyle numCellStyle = getCellStyle(hvo.getPk_corp(), "dzf009", precisionMap, styleMap, workbook, numberCell2, df);
			CellStyle priceCellStyle = getCellStyle(hvo.getPk_corp(), "dzf010", precisionMap, styleMap, workbook, numberCell2, df);
			CellStyle rateCellStyle = getCellStyle(hvo.getPk_corp(), "dzf011", precisionMap, styleMap, workbook, numberCell2, df);

			for (SuperVO cld : hvo.getChildren()) {
				TzpzBVO bvo = (TzpzBVO) cld;
				HSSFRow row = sheet.createRow(rowIndex);
				// 日期
				HSSFCell cell = row.createCell(headIndexMap.get("日期"));
				cell.setCellValue(hvo.getDoperatedate().toString());
				// 公司
				cell = row.createCell(headIndexMap.get("公司"));
				cell.setCellValue(corpService.queryByPk(hvo.getPk_corp()).getUnitname());
				// 凭证号
				cell = row.createCell(headIndexMap.get("凭证号"));
				cell.setCellValue(hvo.getPzh());
				// 附单据
				cell = row.createCell(headIndexMap.get("附单据"));
				cell.setCellValue(hvo.getNbills() == null ? 0 : hvo.getNbills());
				// 票据类型
				cell = row.createCell(headIndexMap.get("票据类型"));
				String fpTypeName = "";
				if (hvo.getFp_style() != null) {
					switch (hvo.getFp_style()) {
						case 1:
							fpTypeName = "普票";
							break;
						case 2:
							fpTypeName = "专票";
							break;
						case 3:
							fpTypeName = "未开票";
							break;
						default:
							break;
					}
				}
				cell.setCellValue(fpTypeName);
				// 摘要
				cell = row.createCell(headIndexMap.get("摘要"));
				cell.setCellValue(bvo.getZy());
				// 科目编码
				cell = row.createCell(headIndexMap.get("科目编码"));
				cell.setCellValue(bvo.getVcode());
				// 科目名称
				cell = row.createCell(headIndexMap.get("科目名称"));
				cell.setCellValue(bvo.getKmmchie());
				// 数量
				cell = row.createCell(headIndexMap.get("数量"));
				cell.setCellStyle(numCellStyle);
				if (bvo.getNnumber() == null || bvo.getNnumber().doubleValue() == 0) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(bvo.getNnumber().doubleValue());
				}
				// 单价
				cell = row.createCell(headIndexMap.get("单价"));
				cell.setCellStyle(priceCellStyle);
				if (bvo.getNprice() == null || bvo.getNprice().doubleValue() == 0) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(bvo.getNprice().doubleValue());
				}
				// 借方金额
				cell = row.createCell(headIndexMap.get("借方金额"));
				cell.setCellStyle(numberCell2);
				if (bvo.getJfmny() == null || bvo.getJfmny().doubleValue() == 0) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(bvo.getJfmny().doubleValue());
				}
				// 贷方金额
				cell = row.createCell(headIndexMap.get("贷方金额"));
				cell.setCellStyle(numberCell2);
				if (bvo.getDfmny() == null || bvo.getDfmny().doubleValue() == 0) {
					cell.setCellValue("");
				} else {
					cell.setCellValue(bvo.getDfmny().doubleValue());
				}

				String curCode = bvo.getPk_currency() == null ? "CNY" : currencyMap.get(bvo.getPk_currency());

				// 原币金额
				cell = row.createCell(headIndexMap.get("原币金额"));
				cell.setCellStyle(numberCell4);
				if (!"CNY".equals(curCode)) {
					if ((bvo.getYbjfmny() == null || bvo.getYbjfmny().doubleValue() == 0) && bvo.getYbdfmny() != null) {
						cell.setCellValue(bvo.getYbdfmny().doubleValue());
					} else if ((bvo.getYbdfmny() == null || bvo.getYbdfmny().doubleValue() == 0)
							&& bvo.getYbjfmny() != null) {
						cell.setCellValue(bvo.getYbjfmny().doubleValue());
					}
				}
				// 币别
				cell = row.createCell(headIndexMap.get("币别"));
				cell.setCellValue(currencyMap.get(bvo.getPk_currency()));
				// 汇率
				cell = row.createCell(headIndexMap.get("汇率"));
				cell.setCellStyle(rateCellStyle);
				if ("CNY".equals(curCode)) {
					cell.setCellValue(1);
				} else if (bvo.getNrate() != null) {
					cell.setCellValue(bvo.getNrate().doubleValue());
				}

				List<AuxiliaryAccountBVO> fzList = bvo.getFzhs_list();
				if (!StringUtil.isEmpty(bvo.getPk_inventory())) {
					AuxiliaryAccountBVO auxBvo = new AuxiliaryAccountBVO();
					auxBvo.setCode(bvo.getInvcode());
					auxBvo.setName(bvo.getInvname());
					auxBvo.setPk_auacount_h(AuxiliaryConstant.ITEM_INVENTORY);
					if (fzList == null) {
						fzList = new ArrayList<AuxiliaryAccountBVO>();
					}
					fzList.add(auxBvo);
				}
				if (fzList != null) {
					for (AuxiliaryAccountBVO auxiliaryAccountBVO : fzList) {
						if (auxiliaryAccountBVO != null) {
							AuxiliaryAccountHVO assistType = auxhvoMap.get(auxiliaryAccountBVO.getPk_auacount_h());
							if (assistType != null) {
								String typeName = assistType.getName();
								Integer colIndex = headIndexMap.get(typeName + "编码");
								if (colIndex != null) {
									cell = row.createCell(colIndex);
									cell.setCellValue(auxiliaryAccountBVO.getCode());
								}
								colIndex = headIndexMap.get(typeName + "名称");
								if (colIndex != null) {
									cell = row.createCell(colIndex);
									cell.setCellValue(auxiliaryAccountBVO.getName());
								}
							}
						}
					}
				}

				rowIndex++;
			}
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

	private CellStyle getCellStyle(String pk_corp, String paramCode, Map<String, Integer> precisionMap,
                                   Map<Integer, CellStyle> styleMap, Workbook workbook, CellStyle baseCellStyle, DataFormat df) {
		String key = pk_corp + paramCode;
		int precision = 4;
		if (precisionMap.containsKey(key)) {
			precision = precisionMap.get(key);
		} else {
			precision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, paramCode));
			precisionMap.put(key, precision);
		}
		CellStyle cellStyle = null;
		if (styleMap.containsKey(precision)) {
			cellStyle = styleMap.get(precision);
		} else {
			cellStyle = workbook.createCellStyle();
			if (baseCellStyle != null) {
				cellStyle.cloneStyleFrom(baseCellStyle);
			} else {
				Font font = workbook.createFont();
				font.setBold(false);
				cellStyle.setFont(font);
				cellStyle.setAlignment(HorizontalAlignment.CENTER);
				cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
			}
			cellStyle.setDataFormat(df.getFormat("#,##0.00000000".substring(0, 6 + precision)));
			styleMap.put(precision, cellStyle);
		}
		return cellStyle;
	}
	
	private Map<String, Integer> createSheetHead(HSSFSheet sheet, HSSFCellStyle headStyle, List<String> fzNames) {
		Map<String, Integer> headIndexMap = new HashMap<String, Integer>();
		HSSFRow row = sheet.createRow(0);
		row.setHeightInPoints(20);
		int colIndex = 0;
		String[] colNames = new String[]{ "日期", "公司",
				"凭证号", "附单据", "票据类型", "摘要", "科目编码", "科目名称",
				"数量", "单价", "借方金额", "贷方金额", "原币金额",
				"币别", "汇率" };
		for (String colName: colNames) {
			HSSFCell cell = row.createCell(colIndex);
			cell.setCellValue(colName);
			cell.setCellStyle(headStyle);
			headIndexMap.put(colName, colIndex);
			colIndex++;
		}
		for (String fzName : fzNames) {
			String colName = fzName + "编码";
			HSSFCell cell = row.createCell(colIndex);
			cell.setCellValue(colName);
			cell.setCellStyle(headStyle);
			headIndexMap.put(colName, colIndex);
			colIndex++;

			colName = fzName + "名称";
			cell = row.createCell(colIndex);
			cell.setCellValue(colName);
			cell.setCellStyle(headStyle);
			headIndexMap.put(colName, colIndex);
			colIndex++;
		}
		return headIndexMap;
	}

	@Override
	public byte[] exportTemplate(String pk_corp, String tempPath) throws DZFWarpException {
		byte[] byteArray = null;
		InputStream is = null;
		try {
			is = this.getClass().getResourceAsStream("/template/凭证模板-导入.xls");
			Workbook impBook = new HSSFWorkbook(is);
			
			// 处理精度
			Sheet exampleRefSheet = impBook.getSheetAt(0);
			Map<String, Integer> precisionMap = new HashMap<String, Integer>();
			Map<Integer, CellStyle> styleMap = new HashMap<Integer, CellStyle>();
			DataFormat df = impBook.createDataFormat();
			CellStyle baseStyle = exampleRefSheet.getRow(1).getCell(0).getCellStyle();
			for (int i = 1; i < 8; i++) {
				Row row = exampleRefSheet.getRow(i);
				// 数量
				row.getCell(6).setCellStyle(getCellStyle(pk_corp, "dzf009", precisionMap, styleMap, impBook, baseStyle, df));
				// 单价
				row.getCell(7).setCellStyle(getCellStyle(pk_corp, "dzf010", precisionMap, styleMap, impBook, baseStyle, df));
				// 汇率
				row.getCell(12).setCellStyle(getCellStyle(pk_corp, "dzf011", precisionMap, styleMap, impBook, baseStyle, df));
			}
			
			Sheet kmRefSheet = impBook.getSheetAt(1);
			Row headRow = impBook.getSheetAt(0).getRow(0);
			CellStyle style = headRow.getCell(0).getCellStyle();
			int headIndex = 13;
			AuxiliaryAccountHVO[] auxhvos = gl_fzhsserv.queryH(pk_corp);
			Map<Integer, String> auxCodeMap = new HashMap<Integer, String>();
			Map<String, String> auxPkMap = new HashMap<String, String>();

			for (AuxiliaryAccountHVO auxiliaryAccountHVO : auxhvos) {
				auxCodeMap.put(auxiliaryAccountHVO.getCode(), auxiliaryAccountHVO.getName());
				auxPkMap.put(auxiliaryAccountHVO.getPk_auacount_h(), auxiliaryAccountHVO.getName());
				Cell cell = headRow.createCell(headIndex++);
				cell.setCellStyle(style);
				cell.setCellValue(auxiliaryAccountHVO.getName() + "编码");

				cell = headRow.createCell(headIndex++);
				cell.setCellStyle(style);
				cell.setCellValue(auxiliaryAccountHVO.getName() + "名称");
			}
			YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

			HashSet<String> accountCurrencies = new HashSet<String>();
			int rowNum = 1;
			for (int i = 0; i < accounts.length; i++) {
				YntCpaccountVO account = accounts[i];
				if (account.getIsleaf().booleanValue()) {
					Row row = kmRefSheet.createRow(rowNum++);
					Cell cell = row.createCell(0);
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(account.getAccountcode());

					cell = row.createCell(1);
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(account.getFullname());

					if (account.getMeasurename() != null) {
						cell = row.createCell(2);
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(account.getMeasurename());
					}

					if (!AuxiliaryConstant.ACCOUNT_FZHS_DEFAULT.equals(account.getIsfzhs())) {
						String val = "";
						for (int charIndex = 0; charIndex < 10; charIndex++) {
							if (account.getIsfzhs().charAt(charIndex) == '1') {
								val += auxCodeMap.get(charIndex + 1) + "/";
							}
						}
						val = val.substring(0, val.length() - 1);
						cell = row.createCell(3);
						cell.setCellType(XSSFCell.CELL_TYPE_STRING);
						cell.setCellValue(val);
					}

					String pk_currency = account.getExc_pk_currency();
					if (pk_currency != null) {
						String[] curs = pk_currency.split(",");
						for (String cur : curs) {
							accountCurrencies.add(cur);
						}
					}
				}
			}

			Sheet auxRefSheet = impBook.getSheetAt(2);
			rowNum = 1;
			AuxiliaryAccountBVO[] auxbvos = gl_fzhsserv.queryAllB(pk_corp);
			for (AuxiliaryAccountBVO auxiliaryAccountBVO : auxbvos) {
				Row row = auxRefSheet.createRow(rowNum++);
				Cell cell = row.createCell(0);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(auxPkMap.get(auxiliaryAccountBVO.getPk_auacount_h()));

				cell = row.createCell(1);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(auxiliaryAccountBVO.getCode());

				cell = row.createCell(2);
				cell.setCellType(XSSFCell.CELL_TYPE_STRING);
				cell.setCellValue(auxiliaryAccountBVO.getName());
			}

			Sheet curRefSheet = impBook.getSheetAt(3);
			BdCurrencyVO[] currencys = (BdCurrencyVO[]) singleObjectBO.queryByCondition(BdCurrencyVO.class,
					"nvl(dr,0)=0", new SQLParameter());

			rowNum = 1;
			for (BdCurrencyVO bdCurrencyVO : currencys) {
				if (accountCurrencies.contains(bdCurrencyVO.getPk_currency())
						|| "CNY".equals(bdCurrencyVO.getCurrencycode())) {
					Row row = curRefSheet.createRow(rowNum++);
					Cell cell = row.createCell(0);
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(bdCurrencyVO.getCurrencycode());

					cell = row.createCell(1);
					cell.setCellType(XSSFCell.CELL_TYPE_STRING);
					cell.setCellValue(bdCurrencyVO.getCurrencyname());
				}
			}

			ByteArrayOutputStream bao = null;
			try {
				bao = new ByteArrayOutputStream();
				impBook.write(bao);
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

		} catch (FileNotFoundException e) {
			throw new BusinessException("未找到模板");
		} catch (IOException e) {
			throw new BusinessException("获取模版失败");
		} finally {
			try {
				if (is != null) {
					is.close();
				}
			} catch (IOException e) {
			}
		}

		return byteArray;
	}

	@Override
	public String readColumnSetting(String userid) throws DZFWarpException {
		UserColumnSettingVO settingVO = getColumnSettingVo(userid);
		if (settingVO != null) {
			return settingVO.getCol_setting();
		}
		return null;
	}

	private UserColumnSettingVO getColumnSettingVo(String userid) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(userid);
		sp.addParam("凭证管理");
		UserColumnSettingVO[] rs = (UserColumnSettingVO[]) singleObjectBO.queryByCondition(UserColumnSettingVO.class,
				" cuserid = ? and nodename = ? and nvl(dr,0)=0 ", sp);
		if (rs.length > 0) {
			return rs[0];
		}
		return null;
	}

	@Override
	public void saveColumnSetting(String pk_corp, String userid, String setting) throws DZFWarpException {
		UserColumnSettingVO settingVO = getColumnSettingVo(userid);
		if (settingVO == null) {
			settingVO = new UserColumnSettingVO();
		}

		settingVO.setNodename("凭证管理");
		settingVO.setCuserid(userid);
		settingVO.setCol_setting(setting);
		singleObjectBO.saveObject(pk_corp, settingVO);

	}
	
	//合并成一张凭证，两张凭证以上
	private String[] saveMergeVoucher(List<TzpzHVO> vouchers,String userid,String pk_corp, VoucherMergeSettingVO setting){
		if(vouchers == null || vouchers.size() <= 1)
			return null;
		Map<String, TzpzBVO> voucherEntryMap = new HashMap<String, TzpzBVO>();
		StringBuilder identifyKey = new StringBuilder();
		List<String> imageGroupList = new ArrayList<String>();
		// 附单据数
		int nbills = 0;
		Integer vbillstatus = 8;
		int iautorecognize = 0;//非识别
		Integer fpStyle = null;
		// 税目分析错误
		Boolean isErrorTax = null;
//		List<String> zyList = new ArrayList<String>();
		int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
		int ratePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf011"));

		boolean isMergeSameEntry = setting.getEntry_type() == null || setting.getEntry_type() == VoucherMergeSettingVO.ENTRY_SAME_DIRECT;
		boolean notMergeBank = setting.getNot_merge_bank() != null && setting.getNot_merge_bank().booleanValue();
		List<String> pzhlist = new ArrayList<String>();
		int bankIdentify = 0;
		List<PzSourceRelationVO> relations = new ArrayList<PzSourceRelationVO>();
		List<PZTaxItemRadioVO> taxItems = new ArrayList<>();
		for (TzpzHVO tzpzHVO : vouchers) {
			if (!StringUtil.isEmpty(tzpzHVO.getSourcebilltype())) {
				PzSourceRelationVO relation = new PzSourceRelationVO();
				relation.setSourcebilltype(tzpzHVO.getSourcebilltype());
				relation.setSourcebillid(tzpzHVO.getSourcebillid());
				relations.add(relation);
			} else if (tzpzHVO.getSource_relation() != null) {
				relations.addAll(Arrays.asList(tzpzHVO.getSource_relation()));
			}
			if (tzpzHVO.getNbills() != null) {
				nbills += tzpzHVO.getNbills();
			}
			if(!StringUtil.isEmpty(tzpzHVO.getPzh())){
				pzhlist.add(tzpzHVO.getPzh());
			}
			// 图片制单
			if (!StringUtil.isEmpty(tzpzHVO.getPk_image_group())) {
				imageGroupList.add(tzpzHVO.getPk_image_group());
			}
			if(tzpzHVO.getVbillstatus()!=null 
					&& tzpzHVO.getVbillstatus().intValue() == -1){
				vbillstatus = -1;
			}
			if(tzpzHVO.getIautorecognize() == 1){//识别
				iautorecognize = 1;
			}
			if (fpStyle == null && tzpzHVO.getFp_style() != null) {
				fpStyle = tzpzHVO.getFp_style();
			}
			if (isErrorTax == null
					&& tzpzHVO.getIs_tax_analyse() != null && tzpzHVO.getIs_tax_analyse()
					&& tzpzHVO.getError_tax_analyse() != null && tzpzHVO.getError_tax_analyse()) {
				// 有分析错误的税目
				isErrorTax = true;
			}
			TzpzBVO[] bvos = (TzpzBVO[]) tzpzHVO.getChildren();
			mergeVoucherEntries(voucherEntryMap, bvos, isMergeSameEntry, notMergeBank,
					ratePrecision, pricePrecision, bankIdentify);
			bankIdentify += bvos.length;
		}
		TzpzHVO voucher = vouchers.get(0);
		List<TzpzBVO> entries = dealMergedEntries(voucher, voucherEntryMap,
				isMergeSameEntry, pricePrecision, setting.getZy(), true);

		voucher.setNbills(nbills);
		voucher.setIsMerge("Y");
		voucher.setIsqxsy(DZFBoolean.TRUE);
		voucher.setSourcebilltype(null);
		voucher.setSourcebillid(null);
		//单据状态
		voucher.setVbillstatus(vbillstatus);
		voucher.setIautorecognize(iautorecognize);
		voucher.setError_tax_analyse(isErrorTax);
		voucher.setFp_style(fpStyle);
		voucher.setCoperatorid(userid);
		voucher.setIscutzy(setting.getCut_zy());

		CorpVO corpVO = corpService.queryByPk(pk_corp);

		sortVoucherEntry(entries, corpVO);
		voucher.setChildren(entries.toArray(new TzpzBVO[0]));
		
		if (relations.size() > 0) {
			for (PzSourceRelationVO relation : relations) {
				relation.setPk_tzpz_h(voucher.getPk_tzpz_h());
				relation.setPk_corp(voucher.getPk_corp());
			}
			voucher.setSource_relation(relations.toArray(new PzSourceRelationVO[0]));
		}
		// 移除合并的凭证
		vouchers.remove(0);
		//更新进销项清单。目前银行对账单没有2018.5.18
		updateVatInvoice(vouchers,voucher.getPk_tzpz_h(),
				voucher.getPzh(),pk_corp, voucher.getSourcebilltype());
		// 删除凭证
		for (TzpzHVO vch : vouchers) {
			vch.setIsqxsy(DZFBoolean.TRUE);
			vch.setBsign(null);
			vch.setIsMerge("Y");
			gl_tzpzserv.deleteVoucher(vch);	
		}
		if (imageGroupList.size() > 0) {
			// 合并图片组，凭证删除后合并，避免凭证删除时被拆分
			String mergedGroup = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			voucher.setPk_image_group(mergedGroup);
		}
		// 保存合并凭证
		gl_tzpzserv.saveVoucher(corpVO, voucher);

		// 这里先不排序了，让他们手工整理凭证号
//		sortAfterMerge(vouchers);
		return new String[]{voucher.getPk_tzpz_h(), getPromptPzhmes(pzhlist)};
	}

	private void updateVatInvoice(List<TzpzHVO> vouchers,String pzhid,String pzh,
			String pk_corp, String sourceType){
		if(vouchers == null || vouchers.size() == 0)
			return;
		SQLParameter[] ters = new SQLParameter[vouchers.size()];
		for(int i = 0 ;i< vouchers.size();i++){
			ters[i] = new SQLParameter();
			ters[i].addParam(pzhid);
			ters[i].addParam(pzh);
			ters[i].addParam(pk_corp);
			ters[i].addParam(vouchers.get(i).getPk_tzpz_h());
		}
		
		String sql1 = " update ynt_vatincominvoice set pk_tzpz_h = ?,pzh = ?  where pk_corp  = ? and pk_tzpz_h = ? and nvl(dr,0) = 0 ";
		String sql2 = " update ynt_vatsaleinvoice set pk_tzpz_h = ?,pzh = ?  where pk_corp  = ? and pk_tzpz_h = ? and nvl(dr,0) = 0 ";
		String sql3 = " update ynt_bankstatement set pk_tzpz_h = ?,pzh = ?  where pk_corp  = ? and pk_tzpz_h = ? and nvl(dr,0) = 0 ";
		String sql4 = " update ynt_ictrade_h set pzid = ?,pzh = ?  where pk_corp  = ? and pzid = ? and nvl(dr,0) = 0 ";
		String sql5 = " update ynt_ictradein set pk_voucher = ?,pzh = ?  where pk_corp  = ? and pk_voucher = ? and nvl(dr,0) = 0 ";
		String sql6 = " update ynt_ictradeout set pk_voucher = ?,pzh = ?  where pk_corp  = ? and pk_voucher = ? and nvl(dr,0) = 0 ";
		singleObjectBO.executeBatchUpdate(sql1, ters);
		singleObjectBO.executeBatchUpdate(sql2, ters);
		singleObjectBO.executeBatchUpdate(sql3, ters);
		singleObjectBO.executeBatchUpdate(sql4, ters);
		singleObjectBO.executeBatchUpdate(sql5, ters);
		singleObjectBO.executeBatchUpdate(sql6, ters);
	}

	@Override
	public String[] processMergeVoucher(String userid,String pk_corp,
										String[] ids, String zy) throws DZFWarpException {
		List<TzpzHVO> vouchers = getVoucherDo(pk_corp, ids);
		if (vouchers.size() <= 1) {
			throw new BusinessException("请选择要合并的凭证");
		}
		VoucherMergeSettingVO setting = queryMergeSetting(pk_corp);
		if (setting == null) {
			setting = getDefaultMergeSetting();
		}
		if (!StringUtils.isEmpty(zy)) {
			setting.setZy(zy);
		}
		List<String> strlist = new ArrayList<String>();
		List<TzpzHVO> toMergeList = new LinkedList<TzpzHVO>();
//		List<TzpzHVO> list2 = new ArrayList<TzpzHVO>();
		for(TzpzHVO z :vouchers ){
			/*if (IVoucherConstants.FREE != z.getVbillstatus()
					&& !(IVoucherConstants.TEMPORARY == z.getVbillstatus() && z.getIautorecognize() == 1)
					|| IBillTypeCode.HP67.equals(z.getSourcebilltype())// 资产折旧
					|| IBillTypeCode.HP34.equals(z.getSourcebilltype())// 成本
					|| IBillTypeCode.HP32.equals(z.getSourcebilltype())// 损益结转
					|| "HCH10535".equals(z.getSourcebilltype())// 汇兑损益结转
					|| IBillTypeCode.HP39.equals(z.getSourcebilltype()) // 计提税金
					|| IBillTypeCode.HP120.equals(z.getSourcebilltype())// 增值税结转
					|| IBillTypeCode.HP125.equals(z.getSourcebilltype()) // 企业所得税结转
					|| "factory".equals(z.getSourcebilltype()) // 工厂
					) {
				strlist.add(z.getPzh());
			} else {
				if (IBillTypeCode.HP100.equals(z.getSourcebilltype())) {
					// 智能识别暂存凭证手工保存后
					z.setSourcebilltype(IBillTypeCode.HP110);
				}
				toMergeList.add(z);
			}*/
			if((IVoucherConstants.FREE == z.getVbillstatus()
					|| IVoucherConstants.TEMPORARY == z.getVbillstatus() && z.getIautorecognize() == 1)
					&& (StringUtil.isEmpty(z.getSourcebilltype()) || !StringUtil.isEmpty(z.getSourcebilltype())
					&& (IBillTypeCode.HP110.equals(z.getSourcebilltype())
					|| IBillTypeCode.HP100.equals(z.getSourcebilltype())
					|| IBillTypeCode.HP85.equals(z.getSourcebilltype())// 银行对账单
					|| IBillTypeCode.HP90.equals(z.getSourcebilltype())// 销项发票
					|| IBillTypeCode.HP95.equals(z.getSourcebilltype())// 进项发票
					|| IBillTypeCode.HP70.equals(z.getSourcebilltype())// 采购单
					|| IBillTypeCode.HP75.equals(z.getSourcebilltype())// 销售单
					|| z.getSourcebilltype().endsWith("gzjt")// 工资计提
					|| z.getSourcebilltype().endsWith("gzff")// 工资发放
					))){
				if (IBillTypeCode.HP100.equals(z.getSourcebilltype())) {
					// 智能识别暂存凭证手工保存后
					z.setSourcebilltype(IBillTypeCode.HP110);
				}
				toMergeList.add(z);
//			}else if(StringUtil.isEmpty(z.getSourcebilltype())
//					&& IVoucherConstants.FREE == z.getVbillstatus()){//手工
//				list2.add(z);
			}else{
				strlist.add(z.getPzh());
			}
		}
		
		List<List<TzpzHVO>> groupList = groupMergeVoucher(toMergeList, setting);
		for (List<TzpzHVO> list : groupList) {
			checkBeforeMerge(list);
		}
		String destVch = null;
		StringBuilder sf = new StringBuilder();
		StringBuilder logMsg = new StringBuilder();
		getPzhprompt(strlist, sf);
		for (List<TzpzHVO> list : groupList) {
			getPzhpromptz(list, sf, setting);
			String[] rs = saveMergeVoucher(list, userid, pk_corp, setting);
			if (rs != null) {
				if(!StringUtil.isEmpty(rs[1])){
					sf.append(rs[1]);
					logMsg.append(rs[1]);
				}
				if (destVch == null) {
					destVch = rs[0];
				}
			}
		}
		return new String[]{destVch, sf.toString(),
				logMsg.toString().replaceAll("<br>", "；")};
	}

	private List<List<TzpzHVO>> groupMergeVoucher(List<TzpzHVO> pzList, VoucherMergeSettingVO setting) {
		List<List<TzpzHVO>> groupList = new ArrayList<>();
		// 往来
		List<List<TzpzHVO>> groupConnectionList = null;
		if (setting.getGroup_type() != null && setting.getGroup_type() == VoucherMergeSettingVO.GROUP_CONTACT) {
			groupConnectionList = groupVoucherByConnection(pzList, setting);
		} else {
			groupConnectionList = new ArrayList<>();
			groupConnectionList.add(pzList);
		}
		for (List<TzpzHVO> list : groupConnectionList) {
			groupList.addAll(groupVoucherByInvoiceType(list));
		}
		return groupList;
	}
	// 按票据类型分组
	private List<List<TzpzHVO>> groupVoucherByInvoiceType(List<TzpzHVO> pzList) {
		boolean hasTwoMoreType = false;
		Integer fpStyle = null;
		for (TzpzHVO tzpzHVO : pzList) {
			if (tzpzHVO.getFp_style() != null) {
				if (fpStyle == null) {
					fpStyle = tzpzHVO.getFp_style();
				} else if (!tzpzHVO.getFp_style().equals(fpStyle)) {
					hasTwoMoreType = true;
					break;
				}
			}
		}
		List<List<TzpzHVO>> groupList = new ArrayList<List<TzpzHVO>>();
		if (hasTwoMoreType) {
			Map<Integer, List<TzpzHVO>> typeMap = new HashMap<Integer, List<TzpzHVO>>();
			for (TzpzHVO tzpzHVO : pzList) {
				List<TzpzHVO> list = null;
				if (typeMap.containsKey(tzpzHVO.getFp_style())) {
					list = typeMap.get(tzpzHVO.getFp_style());
				} else {
					list = new ArrayList<TzpzHVO>();
					typeMap.put(tzpzHVO.getFp_style(), list);
				}
				tzpzHVO.setGroup_invoice(true);
				list.add(tzpzHVO);
			}
			groupList.addAll(typeMap.values());
		} else {
			groupList.add(pzList);
		}
		return groupList;
	}
	// 按往来单位分组
	private List<List<TzpzHVO>> groupVoucherByConnection(List<TzpzHVO> pzList,
														 VoucherMergeSettingVO setting) {
		List<List<TzpzHVO>> groupList = new ArrayList<List<TzpzHVO>>();
		// 按往来单位分组
		// 无往来单位凭证
		List<TzpzHVO> noContactList = new ArrayList<TzpzHVO>();
		// 是否合并无往来单位凭证
		boolean mergeNoContact = setting.getNo_contact_rule() == null
				|| setting.getNo_contact_rule() == VoucherMergeSettingVO.NO_CONTACT_MERGE;
		List<Set<String>> keysList = new LinkedList<Set<String>>();
		for (TzpzHVO hvo : pzList) {
			TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
			Set<String> keys = new HashSet<String>();
			for (TzpzBVO bvo : bvos) {
				if (!StringUtil.isEmpty(bvo.getFzhsx1())) {
					keys.add(bvo.getFzhsx1());
				}
				if (!StringUtil.isEmpty(bvo.getFzhsx2())) {
					keys.add(bvo.getFzhsx2());
				}
			}
			if (keys.size() == 0) {
				noContactList.add(hvo);
			} else {
				boolean containsKeys = false;
				int keySetIndex = 0;
				keySetLoop: for (; keySetIndex < keysList.size(); keySetIndex++) {
					for (String key : keys) {
						Set<String> keySet = keysList.get(keySetIndex);
						if (keySet.contains(key)) {
							keySet.addAll(keys);
							containsKeys = true;
							break keySetLoop;
						}
					}
				}
				if (!containsKeys) {
					keysList.add(keys);
				}
				List<TzpzHVO> list = null;
				if (keySetIndex >= groupList.size()) {
					list = new ArrayList<TzpzHVO>();
					groupList.add(list);
				} else {
					list = groupList.get(keySetIndex);
				}
				hvo.setGroup_connect(true);
				list.add(hvo);
			}
		}
		if (mergeNoContact) {
			groupList.add(noContactList);
		} else {
			for (TzpzHVO hvo: noContactList) {
				ArrayList<TzpzHVO> list = new ArrayList<>();
				list.add(hvo);
				groupList.add(list);
			}
		}
		return groupList;
	}
	private void getPzhprompt(List<String> pzhlist,StringBuilder sf){
		if(pzhlist != null &&  pzhlist.size() > 0){
			sf.append("<font color='red'>凭证号");
			sf.append(pzhlist.toString());
			sf.append("暂不支持合并</font><br>");
		}
	}
	
	private void getPzhpromptz(List<TzpzHVO> pzlist,StringBuilder sf, VoucherMergeSettingVO setting){
		if(pzlist != null &&  pzlist.size() == 1){
			TzpzHVO hvo = pzlist.get(0);
			sf.append("<span style=\"color:#ee9402\">凭证号[")
				.append(pzlist.get(0).getPzh())
				.append("]");
			if (hvo.getGroup_connect() != null && hvo.getGroup_connect()) {
				if (hvo.getGroup_invoice() != null && hvo.getGroup_invoice()) {
					sf.append("没有票据性质相同的往来单位的凭证，请检查");
				} else {
					sf.append("没有相同往来单位的凭证，请检查");
				}
			} else if (hvo.getGroup_invoice() != null && hvo.getGroup_invoice()) {
				sf.append("没有相同票据性质的凭证，请检查");
			} else {
				sf.append("没有可合并的凭证，请检查");
			}
			sf.append("</span><br>");
		}
	}
	
	private String getPromptPzhmes(List<String> pzhlist){
		if(pzhlist == null || pzhlist.size() == 0){
			return null;
		}
		StringBuffer sf = new StringBuffer();
		sf.append("凭证号");
		sf.append(pzhlist.toString());
		sf.append("合并生成凭证成功，凭证号为:"+pzhlist.get(0)+"<br>");
		return sf.toString();
	}
	
	private String getVoucherType(String typeCode) {
		String type = "";
		switch (typeCode) {
		case IBillTypeCode.HP110:
			type = "智能识别";
			break;
		case IBillTypeCode.HP85:
			type = "银行对账单";
			break;
		case IBillTypeCode.HP90:
			type = "销项发票";
			break;
		case IBillTypeCode.HP95:
			type = "进项发票";
			break;
		case IBillTypeCode.HP70:
			type = "入库";
			break;
		case IBillTypeCode.HP75:
			type = "出库";
			break;
		default:
			break;
		}
		return type;
	}
	
	/**
	 * 合并凭证图片组
	 * 
	 * @param pk_corp
	 * @param imageGroupList
	 * @return
	 */
	private String mergeImage(String pk_corp, List<String> imageGroupList) {
		int count = imageGroupList.size();
		String groupId = imageGroupList.get(0);
		if (count == 1) {
			return groupId;
		}
		imageGroupList.remove(0);
		String inSQL = SQLHelper.getInSQL(imageGroupList);
		SQLParameter sp = new SQLParameter();
		sp.addParam(groupId);
		sp.addParam(pk_corp);
		for (String pk_group : imageGroupList) {
			sp.addParam(pk_group);
		}
		singleObjectBO.executeUpdate(
				"update ynt_image_library set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.deleteByPKs(ImageGroupVO.class, imageGroupList.toArray(new String[0]));

		
		singleObjectBO.executeUpdate(
				"update ynt_vatsaleinvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.executeUpdate(
				"update ynt_vatincominvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.executeUpdate(
				" update ynt_bankstatement set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_image_group in  " + inSQL
				, sp);
		return groupId;
	}

	/**
	 * 合并凭证分录，摘要合并
	 * 
	 * @param vo1
	 * @param vo2
	 */
	private void mergeVoucherEntry(TzpzBVO vo1, TzpzBVO vo2) {
		if(StringUtil.isEmpty(vo1.getZy())){
			vo1.setZy(vo2.getZy());
		}else if(!vo1.getZy().contains(vo2.getZy())){
			vo1.setZy(vo1.getZy()+","+vo2.getZy());
		}
		if (vo1.getNnumber() != null || vo2.getNnumber() != null) {
			if (vo1.getVdirect().equals(vo2.getVdirect())) {
				vo1.setNnumber(SafeCompute.add(vo1.getNnumber(), vo2.getNnumber()));
			} else {
				DZFDouble mny1 = null;
				DZFDouble mny2 = null;
				if (vo1.getVdirect() == 0) {
					mny1 = vo1.getJfmny();
				} else {
					mny1 = vo1.getDfmny();
				}
				if (vo2.getVdirect() == 0) {
					mny2 = vo2.getJfmny();
				} else {
					mny2 = vo2.getDfmny();
				}
				DZFDouble number = null;
				if (mny1.compareTo(mny2) >= 0) {
					number = SafeCompute.sub(vo1.getNnumber(), vo2.getNnumber());
				} else {
					number = SafeCompute.sub(vo2.getNnumber(), vo1.getNnumber());
				}
				vo1.setNnumber(number);
			}
		}
		if (vo1.getGlchhsnum() != null || vo2.getGlchhsnum() != null) {
			vo1.setGlchhsnum(SafeCompute.add(vo1.getGlchhsnum(), vo2.getGlchhsnum()));
		}
		if (vo1.getGlcgmny() != null || vo2.getGlcgmny() != null) {
			vo1.setGlcgmny(SafeCompute.add(vo1.getGlcgmny(), vo2.getGlcgmny()));
		}
		if (vo1.getXsjzcb() != null || vo2.getXsjzcb() != null) {
			vo1.setXsjzcb(SafeCompute.add(vo1.getXsjzcb(), vo2.getXsjzcb()));
		}
		vo1.setJfmny(SafeCompute.add(vo1.getJfmny(), vo2.getJfmny()));
		vo1.setDfmny(SafeCompute.add(vo1.getDfmny(), vo2.getDfmny()));
		if (vo1.getJfmny().doubleValue() != 0 && vo1.getDfmny().doubleValue() != 0) {
			if (vo1.getJfmny().compareTo(vo1.getDfmny()) >= 0) {
				vo1.setJfmny(SafeCompute.sub(vo1.getJfmny(), vo1.getDfmny()));
				vo1.setDfmny(DZFDouble.ZERO_DBL);
			} else {
				vo1.setDfmny(SafeCompute.sub(vo1.getDfmny(), vo1.getJfmny()));
				vo1.setJfmny(DZFDouble.ZERO_DBL);
			}
		}
		if (!IGlobalConstants.RMB_currency_id.equals(vo1.getPk_currency())) {
			vo1.setYbjfmny(SafeCompute.add(vo1.getYbjfmny(), vo2.getYbjfmny()));
			vo1.setYbdfmny(SafeCompute.add(vo1.getYbdfmny(), vo2.getYbdfmny()));
		}
		if (vo1.getTax_items() != null) {
			if (vo2.getTax_items() != null) {
				vo1.setTax_items(mergeTaxItem(vo1.getTax_items(), vo2.getTax_items()));
			}
		} else {
			vo1.setTax_items(vo2.getTax_items());
		}
	}
	// 合并税目
	private List<PZTaxItemRadioVO> mergeTaxItem(List<PZTaxItemRadioVO> list1, List<PZTaxItemRadioVO> list2) {
		for (PZTaxItemRadioVO item2: list2) {
			boolean isSame = false;
			for (PZTaxItemRadioVO item1: list1) {
				if (item2.getPk_taxitem().equals(item1.getPk_taxitem())
						&& item2.getTaxratio().equals(item1.getTaxratio())) {
					item1.setMny(SafeCompute.add(item1.getMny(), item2.getMny()));
					item1.setTaxmny(SafeCompute.add(item1.getTaxmny(), item2.getTaxmny()));
					isSame = true;
					break;
				}
			}
			if (!isSame) {
				list1.add(item2);
			}
		}
		return list1;
	}

	/**
	 * 合并前检查
	 * @param vouchers
	 */
	private void checkBeforeMerge(List<TzpzHVO> vouchers) {
		if (vouchers == null || vouchers.size() <= 1) {
			return;
		}
		TzpzHVO voucher = vouchers.get(0);
		String pk_corp = voucher.getPk_corp();
		CorpVO corp = corpService.queryByPk(pk_corp);
		String period = voucher.getPeriod();
		// 专普票
		Integer fpStyle = voucher.getFp_style();
		
		boolean isgz = qmgzService.isGz(pk_corp, voucher.getDoperatedate().toString());
		if (isgz) {
			throw new BusinessException(voucher.getPeriod() + "月份已关账，不允许合并凭证");
		}

		Set<String> sourceSet = new HashSet<String>();
		for (TzpzHVO tzpzHVO : vouchers) {
			if (!pk_corp.equals(tzpzHVO.getPk_corp())) {
				throw new BusinessException("公司不同，请检查");
			}
			if (tzpzHVO.getIsimp() != null && tzpzHVO.getIsimp().booleanValue()) {
				throw new BusinessException("导入凭证不允许合并");
			}
			if (!period.equals(tzpzHVO.getPeriod())) {
				throw new BusinessException("期间不同，请检查");
			}
			Integer thisFpStyle = tzpzHVO.getFp_style();
			if (thisFpStyle != null) {
				if (fpStyle == null) {
					fpStyle = thisFpStyle;
				} else if (!thisFpStyle.equals(fpStyle)) {
					throw new BusinessException("票据性质不同，请检查");
				}
				fpStyle = thisFpStyle;
			}
			if (!StringUtil.isEmpty(tzpzHVO.getSourcebilltype())) {
				String sourceType = tzpzHVO.getSourcebilltype();
//				if (sourceType.endsWith("gzjt")) {
//					sourceType = "gzjt";
//				} else if (sourceType.endsWith("gzff")) {
//					sourceType = "gzff";
//				}
				sourceSet.add(sourceType);
			}
			if (tzpzHVO.getSource_relation() != null) {
				for (PzSourceRelationVO relationVO : tzpzHVO.getSource_relation()) {
					sourceSet.add(relationVO.getSourcebilltype());
				}
			}
		}
		if (sourceSet.contains(IBillTypeCode.HP90) && sourceSet.contains(IBillTypeCode.HP95)) {
			throw new BusinessException("进项和销项凭证暂不支持合并");
		}
		if (sourceSet.contains(IBillTypeCode.HP70) && sourceSet.contains(IBillTypeCode.HP75)) {
			throw new BusinessException("出库和入库凭证暂不支持合并");
		}
		if (sourceSet.contains(IBillTypeCode.HP75) && sourceSet.contains(IBillTypeCode.HP95)) {
			throw new BusinessException("进项和出库凭证暂不支持合并");
		}
		if (sourceSet.contains(IBillTypeCode.HP70) && sourceSet.contains(IBillTypeCode.HP90)) {
			throw new BusinessException("入库和销项凭证暂不支持合并");
		}
//		if (sourceSet.contains("gzjt") && sourceSet.contains("gzff")) {
//			throw new BusinessException("工资计提和工资发放凭证暂不支持合并");
//		}
	}

	/**
	 * 获取合并凭证的DO
	 * 
	 * @param pk_corp
	 * @param voucherId
	 * @return
	 */
	private List<TzpzHVO> getVoucherDo(String pk_corp, String[] voucherId) {
		StringBuilder sql = new StringBuilder();
		List<String> idList = Arrays.asList(voucherId);
		String inSql = SQLHelper.getInSQL(idList);
		SQLParameter sp = SQLHelper.getSQLParameter(idList);
		sp.addParam(pk_corp);
		sql.append("select * from ynt_tzpz_h where ").append(" pk_tzpz_h in ").append(inSql)
				.append(" and nvl(dr,0) = 0 and pk_corp = ? order by ynt_tzpz_h.period, ynt_tzpz_h.pzh ");

		List<TzpzHVO> hvo = (List<TzpzHVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(TzpzHVO.class));
		sql.setLength(0);
		sql.append("select tb.* from ynt_tzpz_b tb ")
				.append(" where tb.pk_tzpz_h in ").append(inSql)
				.append(" and nvl(tb.dr,0) = 0 and tb.pk_corp = ? order by tb.rowno");
		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(TzpzBVO.class));
		Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(pk_corp);
		Map<String, BdCurrencyVO> currencyMap = getCurrencyMap();
		for (TzpzBVO tzpzBVO : bvos) {
			setEntryFullcode(tzpzBVO, assistMap, currencyMap);
		}
		Map<String, List<TzpzBVO>> map = DZfcommonTools.hashlizeObject(bvos, new String[] { "pk_tzpz_h" });
		// 查询关联关系表
		sql.setLength(0);
		sql.append("select * from ynt_pz_sourcerelation where pk_tzpz_h in ").append(inSql)
		.append(" and pk_corp = ? and nvl(dr,0)=0");
		List<PzSourceRelationVO> relations = (List<PzSourceRelationVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(PzSourceRelationVO.class));
		Map<String, List<PzSourceRelationVO>> relationMap = DZfcommonTools.hashlizeObject(relations, new String[] { "pk_tzpz_h" });
		// 查询税表表项
		sql.setLength(0);
		sql.append("select * from ynt_pztaxitem where pk_tzpz_h in ").append(inSql)
				.append(" and pk_corp = ? and nvl(dr,0)=0");
		List<PZTaxItemRadioVO> taxItems = (List<PZTaxItemRadioVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(PZTaxItemRadioVO.class));
		Map<String, List<PZTaxItemRadioVO>> taxItemMap = DZfcommonTools.hashlizeObject(taxItems,
				new String[] { "pk_tzpz_h", "pk_tzpz_b" });
		if (hvo != null && hvo.size() > 0) {
			for (TzpzHVO th : hvo) {
				List<TzpzBVO> liz = map.get(th.getPrimaryKey());
				if (liz != null && !liz.isEmpty()) {
					for (TzpzBVO bvo : liz) {
						List<PZTaxItemRadioVO> taxList = taxItemMap.get(th.getPk_tzpz_h() + "," + bvo.getPk_tzpz_b());
						if (taxList != null && taxList.size() > 0) {
							bvo.setTax_items(taxList);
						}
					}
					th.setChildren(liz.toArray(new TzpzBVO[0]));
				}
				List<PzSourceRelationVO> relationList = relationMap.get(th.getPrimaryKey());
				if (relationList != null && relationList.size() > 0) {
					th.setSource_relation(relationList.toArray(new PzSourceRelationVO[0]));
				}
			}
		}
		return hvo;
	}
	private Map<String, BdCurrencyVO> getCurrencyMap() {
		BdCurrencyVO[] vos = (BdCurrencyVO[]) singleObjectBO.queryByCondition(
				BdCurrencyVO.class, "pk_corp = '000001' and nvl(dr, 0) = 0",
				new SQLParameter());
		Map<String, BdCurrencyVO> map = new HashMap<String, BdCurrencyVO>();
		if (vos != null && vos.length > 0) {
			for (BdCurrencyVO bdCurrencyVO : vos) {
				map.put(bdCurrencyVO.getPk_currency(), bdCurrencyVO);
			}
		}
		return map;
	}

	private void setEntryFullcode(TzpzBVO vo,
			Map<String, AuxiliaryAccountBVO> assistMap,
			Map<String, BdCurrencyVO> currencyMap) {
		StringBuilder fullcode = new StringBuilder();
		if (vo.getVcode() != null) {
			fullcode.append(vo.getVcode());
		}
		for (int i = 1; i <= 10; i++) {
			String fzhsID = (String) vo.getAttributeValue("fzhsx" + i);
			if (i == 6 && StringUtil.isEmpty(fzhsID)) {
				fzhsID = vo.getPk_inventory();
			}
			if (!StringUtil.isEmpty(fzhsID)) {
				AuxiliaryAccountBVO assist = assistMap.get(fzhsID);
				if (assist != null) {
					fullcode.append("_").append(assist.getCode());
				}
			}
		}
		if (vo.getPk_currency() != null
				&& !IGlobalConstants.RMB_currency_id
						.equals(vo.getPk_currency())) {
			if (currencyMap.containsKey(vo.getPk_currency())) {
				fullcode.append("_").append(
						currencyMap.get(vo.getPk_currency()).getCurrencycode());
			}
		}
		vo.setFullcode(fullcode.toString());
	}
	
	/**
	 * 按借贷方向排序
	 * 
	 * @param bvos
	 */
	private void sortEntryByDirection(List<TzpzBVO> bvos) {
	    Collections.sort(bvos, new Comparator<TzpzBVO>() {
	        @Override
	        public int compare(TzpzBVO o1, TzpzBVO o2) {
	            int jf1 = o1.getJfmny() == null
	                    || o1.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int jf2 = o2.getJfmny() == null
	                    || o2.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int cp = jf2 - jf1;
	            return cp;
	        }
	    });
	}
	
	private void sortVoucherEntry(List<TzpzBVO> bvos, CorpVO corpVO) {
		String taxCode = null;
		if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())
				|| "00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
			// 进项税额，待认证进项税额，销项税额
			taxCode = "^(22210?13|22210+10+(1|2))";
		} else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
			// 企业会计制度 进项税额，销项税额
			taxCode = "^21710+10+(1|5)";
		} else if ("00000100AA10000000000BMQ".equals(corpVO.getCorptype())) {
			// 民间非盈利 进项税额，待抵扣进项税，销项税额
			taxCode = "^2206(0?10|0+10+(1|2))";
		}
		final String taxMatch = taxCode;
	    Collections.sort(bvos, new Comparator<TzpzBVO>() {
	        @Override
	        public int compare(TzpzBVO o1, TzpzBVO o2) {
	            int jf1 = o1.getJfmny() == null
	                    || o1.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int jf2 = o2.getJfmny() == null
	                    || o2.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int cp = jf2 - jf1;
	            if (cp == 0) {
	            	String code1 = o1.getFullcode() == null ? o1.getVcode() : o1.getFullcode();
	            	String code2 = o2.getFullcode() == null ? o2.getVcode() : o2.getFullcode();
	            	boolean isTax1 = o1.getIstaxsubj() != null && o1.getIstaxsubj().booleanValue()
                            || taxMatch != null && code1.matches(taxMatch);
	            	boolean isTax2 = o2.getIstaxsubj() != null && o2.getIstaxsubj().booleanValue()
                            || taxMatch != null && code2.matches(taxMatch);
	            	if (isTax1 || isTax2) {
	            		if (isTax1 && !isTax2) {
		            		code1 = "999";
						} else if (isTax2 && !isTax1) {
							code2 = "999";
						}
					}
	                cp = code1.compareTo(code2);
	            }
	            return cp;
	        }
	    });
	}

	/**
	 * 合并后排序
	 * 
	 * @param vouchers
	 *            删除的凭证
	 */
	private void sortAfterMerge(List<TzpzHVO> vouchers) {
		TzpzHVO first = vouchers.get(0);
		String pk_corp = first.getPk_corp();
		List<Integer> numList = new ArrayList<Integer>();
		for (TzpzHVO vch : vouchers) {
			numList.add(Integer.valueOf(vch.getPzh()));
		}
		String sql = " pzh > ? and nvl(dr,0) = 0 and period = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(first.getPzh());
		sp.addParam(first.getPeriod());
		sp.addParam(pk_corp);
		TzpzHVO[] sortVchs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, sql, sp);
		int size = vouchers.size();
		// 凭证号调整值
		int index = 1;
		for (TzpzHVO vch : sortVchs) {
			int pzh = Integer.valueOf(vch.getPzh());
			if (index < size && pzh > numList.get(index)) {
				index++;
			}
			pzh -= index;
			vch.setPzh(String.format("%04d", pzh));
			updateIctradePzh(pk_corp, vch);
		}
		singleObjectBO.updateAry(sortVchs, new String[] { "pzh" });
	}

	@Override
	public String pzsortByuploadpic(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod) throws DZFWarpException {
		StringBuffer returnmeg = new StringBuffer();
		int updateCounts = 0;
		if (pk_corps == null || pk_corps.length < 1) {
			throw new BusinessException("传入公司参数不能为空");
		}

		if (beginperiod == null) {
			throw new BusinessException("传入期间起参数不能为空");
		}
		if (endperiod == null) {
			throw new BusinessException("传入期间至参数不能为空");
		}

		List<String> plist = new ArrayList<String>();
		String period;
		Integer beginMonth = beginperiod.getMonth();
		// 获得期间
		for (int year = beginperiod.getYear(); year <= endperiod.getYear(); year++) {
			if (year == endperiod.getYear()) {
				for (; beginMonth <= endperiod.getMonth(); beginMonth++) {
					period = year + "-" + (beginMonth > 9 ? beginMonth : "0" + beginMonth);
					plist.add(period);
				}
			} else {
				for (; beginMonth < 13; beginMonth++) {
					period = year + "-" + (beginMonth > 9 ? beginMonth : "0" + beginMonth);
					plist.add(period);
				}
			}
			beginMonth = 1;
		}

		String corpname = null;
		String condition = null;
		SQLParameter sp = new SQLParameter();
		TzpzHVO[] vos = null;
		for (String pk_corp : pk_corps) {
			for (String periodParam : plist) {
				boolean isgz = qmgzService.isGz(pk_corp, periodParam);
				if (isgz) {// 是否关账
					corpname = corpService.queryByPk(pk_corp).getUnitname();
					returnmeg.append("公司：" + corpname + "在" + periodParam + "已经关账，不允许整理哦。<br/>");
					continue;
				}
				sp.clearParams();
				sp.addParam(pk_corp);
				sp.addParam(periodParam);
				condition = "pk_corp=? and period = ? and nvl(dr,0)=0 order by doperatedate,pzh ";
				vos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, condition, sp);
				if (vos == null || vos.length == 0) {
					// 该期间没有凭证
					continue;
				}
				vos = ontzpzPicSort(vos, pk_corp, periodParam);

				List<TzpzHVO> resultList= new ArrayList<>(Arrays.asList(vos));

				sortSypz(resultList);

				vos = resultList.toArray(new TzpzHVO[0]);

				updateCounts = updatePZH(vos, pk_corp, updateCounts);
			}
		}
		return returnmeg.toString() + "成功整理" + updateCounts + "条";
	}

	private int updatePZH(TzpzHVO[] vos, String pk_corp, int updateCounts) {
		String pzh = null;
		String num = null;
		TzpzHVO hvo = null;
		SQLParameter sqlp = null;

		if (vos != null && vos.length > 0) {
			for (int z = 1; z <= vos.length; z++) {
				num = "0001";
				if (z <= 9) {
					num = "000" + z;
				} else if (z > 9 && z < 100) {
					num = "00" + z;
				} else if (z >= 100 && z < 1000) {
					num = "0" + z;
				} else {
					num = "" + z;
				}

				pzh = num;

				// 整理的凭证数
				hvo = vos[z - 1];
				updateCounts++;
				sqlp = new SQLParameter();
				sqlp.addParam(pzh);
				sqlp.addParam(hvo.getPrimaryKey());
				hvo.setPzh(pzh);
				singleObjectBO.executeUpdate(" update YNT_TZPZ_H set pzh= ? where pk_tzpz_h= ? ", sqlp);
				updateIctradePzh(pk_corp, hvo);
			}
		}
		return updateCounts;
	}

	private TzpzHVO[] ontzpzPicSort(TzpzHVO[] vos, String pk_corp, String period) {
		List<String> tzpzpks = queryPicSort(pk_corp, period);
		if (tzpzpks == null || tzpzpks.size() == 0)
			return vos;
		List<String> pzdate = new ArrayList<String>();
		for (TzpzHVO hvo : vos) {
			if (hvo.getDoperatedate() != null && !pzdate.contains(hvo.getDoperatedate().toString())) {
				pzdate.add(hvo.getDoperatedate().toString());
			}
		}
		List<TzpzHVO> listpzvo = new ArrayList<TzpzHVO>();
		Map<String, List<TzpzHVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(vos),
				new String[] { "doperatedate" });
		for (String key : pzdate) {
			List<TzpzHVO> zvos = map.get(key);
			List<TzpzHVO> newlist = onPzListSort(zvos, tzpzpks);
			listpzvo.addAll(newlist);
			map.remove(key);
		}
		// 假如没有凭证日期的单据在排序放到最开头，或者最末尾。这个可以调。这里放到最末尾吧。
		if (map.size() > 0) {
			Collection<List<TzpzHVO>> res = map.values();
			if (res != null && res.size() > 0) {
				for (List<TzpzHVO> c1 : res) {
					if (c1 != null && c1.size() > 0) {
						listpzvo.addAll(c1);
					}
				}
			}
		}
		return listpzvo.toArray(new TzpzHVO[0]);
	}

	private List<TzpzHVO> onPzListSort(List<TzpzHVO> zvos, List<String> tzpzpks) {
		if (zvos == null || zvos.size() == 0)
			return null;
		List<String> zlist = new ArrayList<String>();
		for (TzpzHVO vo : zvos) {
			zlist.add(vo.getPk_tzpz_h());
		}
		List<TzpzHVO> alllist = new ArrayList<TzpzHVO>();
		Map<String, TzpzHVO> map = DZfcommonTools.hashlizeObjectByPk(zvos, new String[] { "pk_tzpz_h" });
		for (String key : tzpzpks) {
			if (map.containsKey(key)) {
				alllist.add(map.get(key));
				map.remove(key);
			}
		}
		for (String key : zlist) {
			if (map.containsKey(key)) {
				alllist.add(map.get(key));
				map.remove(key);
			}
		}
		return alllist;
	}

	private List<String> queryPicSort(String pk_corp, String period) {
		StringBuffer sf = new StringBuffer();
		sf.append(" select h.pk_tzpz_h,o.iorder from ynt_tzpz_h h ");
		sf.append(" join ynt_image_library y on y.pk_image_group = h.pk_image_group ");
		sf.append(" join ynt_image_ocrlibrary o on y.pk_image_library = o.crelationid ");
		sf.append(" where h.pk_corp = ? and h.period = ? and nvl(h.dr,0) = 0 order by o.iorder ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		// 可能会出现多行。以第一行数据为准
		List<String> list = (List<String>) singleObjectBO.executeQuery(sf.toString(), sp, new ResultSetProcessor() {

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				while (rs.next()) {
					String pk_tzpz_h = rs.getString("pk_tzpz_h");
					if (!list.contains(pk_tzpz_h)) {
						list.add(pk_tzpz_h);
					}
				}
				return list;
			}

		});
		return list;
	}

	@Override
	public String savechurukubillcodesort(String[] pk_corps, DZFDate beginperiod, DZFDate endperiod)
			throws DZFWarpException {
		StringBuffer returnmeg = new StringBuffer();
		int updateCounts = 0;
		if (pk_corps == null || pk_corps.length < 1) {
			throw new BusinessException("传入公司参数不能为空");
		}

		if (beginperiod == null) {
			throw new BusinessException("传入期间起参数不能为空");
		}
		if (endperiod == null) {
			throw new BusinessException("传入期间至参数不能为空");
		}

		List<String> plist = new ArrayList<String>();
		String period;
		Integer beginMonth = beginperiod.getMonth();
		// 获得期间
		for (int year = beginperiod.getYear(); year <= endperiod.getYear(); year++) {
			if (year == endperiod.getYear()) {
				for (; beginMonth <= endperiod.getMonth(); beginMonth++) {
					period = year + "-" + (beginMonth > 9 ? beginMonth : "0" + beginMonth);
					plist.add(period);
				}
			} else {
				for (; beginMonth < 13; beginMonth++) {
					period = year + "-" + (beginMonth > 9 ? beginMonth : "0" + beginMonth);
					plist.add(period);
				}
			}
			beginMonth = 1;
		}

		String corpname = null;
		String condition = null;
		SQLParameter sp = new SQLParameter();
		TzpzHVO[] vos = null;
		for (String pk_corp : pk_corps) {
			for (String periodParam : plist) {
				boolean isgz = qmgzService.isGz(pk_corp, periodParam);
				if (isgz) {// 是否关账
					corpname = corpService.queryByPk(pk_corp).getUnitname();
					returnmeg.append("公司：" + corpname + "在" + periodParam + "已经关账，不允许整理哦。<br/>");
					continue;
				}
				sp.clearParams();
				sp.addParam(pk_corp);
				sp.addParam(periodParam);
				condition = " pk_corp=? and period = ? and nvl(dr,0)=0  and vicbillcodetype is not null order by vicbillcode ";
				vos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, condition, sp);
				if (vos == null || vos.length == 0) {
					// 该期间没有凭证
					continue;
				}
				sortVIcBillcode(vos);
				if(vos != null){
					updateCounts = vos.length+updateCounts;
				}
				singleObjectBO.updateAry(vos, new String[]{"vicbillcode"});
			}
		}
		return returnmeg.toString() + "成功整理" + updateCounts + "条";
	}
	
	private void sortVIcBillcode(TzpzHVO[] vos){
		if(vos == null || vos.length ==0)
			return;
		Map<String,List<TzpzHVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(vos), new String[]{"vicbillcodetype"});
		for(String key : map.keySet()){
			List<TzpzHVO> list = map.get(key);
			sortSypz(list);
			createIcbillcode(list);
		}
	}
	
	private void createIcbillcode(List<TzpzHVO> list){
		if(list == null || list.size() ==0)
			return;
		String type = list.get(0).getVicbillcodetype();
		String fix = InventoryConstant.IC_IN_PREFIX;
		if(InventoryConstant.IC_STYLE_IN.equals(type)){
			fix = InventoryConstant.IC_IN_PREFIX;
		}else if(InventoryConstant.IC_STYLE_OUT.equals(type)){
			fix = InventoryConstant.IC_OUT_PREFIX;
		}
		Long code = 1l;
		for(TzpzHVO hvo : list){
			hvo.setVicbillcode(getFinalcode(fix,code));
			code++;
		}
	}
	
	private String getFinalcode(String fix,Long code){
		String str = "";
		if(code > 0 && code < 10){
			str = "000"+String.valueOf(code);
		}else if(code > 9 && code < 100){
			str = "00"+String.valueOf(code);
		}else if(code > 99 && code < 1000){
			str = "0"+String.valueOf(code);
		}else {
			str = String.valueOf(code);
		}
		return fix+str;
	}

	@Override
	public VoucherMergeSettingVO saveMergeSetting(String pk_corp,
			VoucherMergeSettingVO setting) throws DZFWarpException {
		VoucherMergeSettingVO oldVO = queryMergeSetting(pk_corp);
		if (oldVO != null) {
			setting.setPk_merge_setting(oldVO.getPk_merge_setting());
			singleObjectBO.update(setting);
		} else {
			singleObjectBO.saveObject(pk_corp, setting);
		}
		return setting;
	}

	@Override
	public TzpzBVO[] mergeVoucherEntries(String pk_corp, TzpzBVO[] bvos, String summary) throws DZFWarpException {
		VoucherMergeSettingVO setting = queryMergeSetting(pk_corp);
		if (setting == null) {
			setting = getDefaultMergeSetting();
		}
		boolean isMergeSameEntry = setting.getEntry_type() == null || setting.getEntry_type() == VoucherMergeSettingVO.ENTRY_SAME_DIRECT;
		boolean notMergeBank = setting.getNot_merge_bank() != null && setting.getNot_merge_bank().booleanValue();
		List<String> pzhlist = new ArrayList<String>();
		int bankIdentify = 0;
		int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
		int ratePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf011"));
		Map<String, TzpzBVO> voucherEntryMap = new LinkedHashMap<String, TzpzBVO>();
		mergeVoucherEntries(voucherEntryMap, bvos, isMergeSameEntry, notMergeBank,
				ratePrecision, pricePrecision, bankIdentify);
		List<TzpzBVO> list = dealMergedEntries(null, voucherEntryMap,
                isMergeSameEntry, pricePrecision, summary, false);
		if (list.size() > 0) {
			sortVoucherEntry(list, corpService.queryByPk(pk_corp));
		}
		return list.toArray(new TzpzBVO[0]);
	}

	@Override
	public void savePrintAssistSetting(String pk_corp, String userid,
									   VoucherPrintAssitSetVO[] vos) throws DZFWarpException {
		if (vos == null || vos.length == 0) {
			return;
		}
		for (VoucherPrintAssitSetVO vo: vos) {
			vo.setPk_corp(pk_corp);
			vo.setCoperatorid(userid);
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate("delete from ynt_settings_assistprint where pk_corp = ?", sp);
		singleObjectBO.insertVOArr(pk_corp, vos);
	}

	@Override
	public VoucherPrintAssitSetVO[] queryPrintAssistSetting(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		return (VoucherPrintAssitSetVO[]) singleObjectBO.queryByCondition(VoucherPrintAssitSetVO.class,
				" pk_corp = ? and nvl(dr,0)=0 ", sp);
	}

	// 合并分录
	private void mergeVoucherEntries(Map<String, TzpzBVO> voucherEntryMap, TzpzBVO[] bvos, boolean isMergeSameEntry,
									 boolean notMergeBank, int ratePrecision, int pricePrecision, int bankIdentify) {
		for (int i = 0; i < bvos.length; i++) {
			TzpzBVO bvo = bvos[i];
			StringBuilder identifyKey = new StringBuilder();
			bvo.setVdirect(bvo.getJfmny() == null || bvo.getJfmny().doubleValue() == 0 ? 1 : 0);
			if (isMergeSameEntry) {
				// 同方向分录合并
				identifyKey.append(bvo.getVdirect());
			}
			// 科目
			identifyKey.append("_").append(bvo.getPk_accsubj());
			if (bvo.getVcode().startsWith("1002") && notMergeBank) {
				// 银行科目不合并
				identifyKey.append("_").append(bankIdentify++);
			}
			// 币种
			identifyKey.append("_")
					.append(bvo.getPk_currency() == null ? IGlobalConstants.RMB_currency_id : bvo.getPk_currency());
			// 汇率
			DZFDouble rate = bvo.getNrate() == null ? DZFDouble.ONE_DBL : bvo.getNrate();
			identifyKey.append("_").append(rate.setScale(ratePrecision, DZFDouble.ROUND_HALF_UP));
			// 辅助核算
			identifyKey.append("_").append(ReportUtil.getFzKey(bvo));
			// 商品
			identifyKey.append("_").append(bvo.getPk_inventory());
			// 税目
			identifyKey.append("_").append(bvo.getPk_taxitem());

			// 库存成本
			identifyKey.append("_").append(bvo.getVicbillcodetype());
			String key = identifyKey.toString();
			if (voucherEntryMap.containsKey(key)) {
				mergeVoucherEntry(voucherEntryMap.get(key), bvo);
			} else {
				voucherEntryMap.put(key, bvo);
			}
		}
	}
	// 处理合并后的分录
	private List<TzpzBVO> dealMergedEntries(TzpzHVO hvo, Map<String, TzpzBVO> voucherEntryMap,
									 boolean isMergeSameEntry, int pricePrecision, String zy, boolean checkZero) {
		List<TzpzBVO> entries = new ArrayList<TzpzBVO>(voucherEntryMap.values());
		if (!isMergeSameEntry) {
			calculateSum(hvo, entries);
		}
		// 手工录入的摘要
		if (zy != null && zy.length() > 200) {
			zy = zy.substring(0, 200);
		}
		Iterator<TzpzBVO> entryIt = entries.iterator();
		while (entryIt.hasNext()) {
			TzpzBVO bvo = entryIt.next();
			bvo.setRowno(null);
			if (bvo.getDfmny().doubleValue() == 0 && bvo.getJfmny().doubleValue() == 0) {
				entryIt.remove();
				continue;
			}
			if (zy != null) {
				bvo.setZy(zy);
			} else if(bvo.getZy() != null && bvo.getZy().length() > 200) {
				bvo.setZy(bvo.getZy().substring(0, 200));
			}
			if (bvo.getNnumber() == null || bvo.getNnumber().doubleValue() == 0) {
				bvo.setNnumber(null);
				bvo.setNprice(null);
			} else {
				DZFDouble mny = bvo.getJfmny().doubleValue() == 0 ? bvo.getDfmny() : bvo.getJfmny();
				// 重新计算单价
				DZFDouble price = mny.div(bvo.getNnumber())
						.setScale(pricePrecision, DZFDouble.ROUND_HALF_UP);
				bvo.setNprice(price);
			}
		}
		if (entries.size() == 0 && checkZero) {
			throw new BusinessException("合并后的所有分录金额都为0，请检查！");
		}
		return entries;
	}

	@Override
	public VoucherMergeSettingVO queryMergeSetting(String pk_corp) {
		String condition = " pk_corp = ? and nvl(dr, 0) = 0 ";
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		VoucherMergeSettingVO[] rs = (VoucherMergeSettingVO[]) singleObjectBO
				.queryByCondition(VoucherMergeSettingVO.class, condition, param);

		VoucherMergeSettingVO vo = null;
		if (rs != null && rs.length > 0) {
			vo = rs[0];
		}
		return vo;
	}
	
	private VoucherMergeSettingVO getDefaultMergeSetting() {
		VoucherMergeSettingVO vo = new VoucherMergeSettingVO();
		vo.setNot_merge_bank(DZFBoolean.FALSE);
		vo.setGroup_type(VoucherMergeSettingVO.GROUP_NONE);
		vo.setEntry_type(VoucherMergeSettingVO.ENTRY_SAME_DIRECT);
		vo.setAbstract_type(VoucherMergeSettingVO.ABSTRACT_AUTO);
		return vo;
	}
}

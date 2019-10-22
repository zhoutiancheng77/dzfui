package com.dzf.zxkj.platform.controller.salary;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryBaseService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryReportService;
import com.dzf.zxkj.platform.service.gzgl.ImpExcel.impl.SalaryReportExcelFactory;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 工资表
 */
@RestController
@RequestMapping("/salary/gl_gzbact2")
@Slf4j
public class SalaryReportController {

	@Autowired
	private ISalaryReportService gl_gzbserv = null;

	@Autowired
	private ISecurityService securityserv;

	@Autowired
	private SingleObjectBO singlebo = null;

	@Autowired
	private SalaryReportExcelFactory factory;
	@Autowired
	protected IBDCorpTaxService sys_corp_tax_serv;

	@Autowired
	private IUserService userServiceImpl;

	@Autowired
	private ISalaryBaseService gl_gzbbaseserv;

//	public void query() {
//		Json json = new Json();
//
//		try {
//			int page = data.getPage();
//			int rows = data.getRows();
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String isfenye = getRequest().getParameter("isfenye");
//			if ("Y".equals(isfenye)) {// 分页
//				QueryPageVO pagevo = gl_gzbserv.queryBodysBypage(pk_corp, qj, billtype, page, rows);
//
//				json.setTotal(Long.valueOf(pagevo.getTotal()));
//				json.setRows(pagevo.getPagevos());
//			} else {
//				SalaryReportVO[] vos = gl_gzbserv.query(pk_corp, qj, billtype);// 查询工资表数据
//				if (vos == null || vos.length == 0) {
//					vos = new SalaryReportVO[0];
//				}
//				json.setRows(vos);
//			}
//			DZFBoolean bool = gl_gzbserv.queryIsGZ(pk_corp, qj);// 查询是否关账
//			if (bool.booleanValue()) {
//				json.setStatus(500);
//			} else {
//				json.setStatus(-600);
//			}
//			// SalaryReportVO nvo = calTotal(vos);
//			// json.setData(nvo);
//
//			json.setMsg("查询成功");
//			json.setSuccess(true);
//			log.info("查询成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			// json.setSuccess(false);
//			printErrorLog(json, log, e, "查询失败！");
//		}
//		writeJson(json);
//	}
//
//	private SalaryReportVO calTotal(SalaryReportVO[] vos) {
//		// 计算合计行数据
//
//		String[] columns = { "yfgz", "yanglaobx", "yiliaobx", "shiyebx", "zfgjj", "ynssde", "grsds", "sfgz", "znjyzc",
//				"jxjyzc", "zfdkzc", "zfzjzc", "sylrzc", "ljsre", "ljznjyzc", "ljjxjyzc", "ljzfdkzc", "ljzfzjzc",
//				"ljsylrzc", "ljynse", "yyjse", "ljzxkc", "qyyanglaobx", "qyyiliaobx", "qyshiyebx", "qyzfgjj", "qygsbx",
//				"qyshybx" };
//
//		SalaryReportVO nvo = new SalaryReportVO();
//		nvo.setYgbm("合计");
//
//		for (String column : columns) {
//			DZFDouble d1 = DZFDouble.ZERO_DBL;
//			for (SalaryReportVO svo : vos) {
//				if (svo.getYfgz() == null)
//					svo.setYfgz(DZFDouble.ZERO_DBL);
//				d1 = SafeCompute.add(d1,
//						DZFNumberUtil.toNotNullValue(ValueUtils.getDZFDouble(svo.getAttributeValue(column))).setScale(2,
//								DZFDouble.ROUND_HALF_UP));
//				nvo.setAttributeValue(column, d1);
//			}
//		}
//		return nvo;
//	}
//
//	// judgeHasPZ
//	public void judgeHasPZ() {
//		Json json = new Json();
//		String qj = getRequest().getParameter("opdate");
//		try {
//			String msg = gl_gzbserv.judgeHasPZ(getLogincorppk(), qj);
//			json.setRows(null);
//			json.setMsg(msg);
//			json.setSuccess(true);
//			log.info("查询成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			// json.setSuccess(false);
//			printErrorLog(json, log, e, "查询失败！");
//		}
//		writeJson(json);
//	}
//
//	public void isGZ() {
//		Json json = new Json();
//		String qj = getRequest().getParameter("opdate");
//		String isgz = getRequest().getParameter("isgz");
//		try {
//			DZFBoolean bool = gl_gzbserv.isGZ(getLogincorppk(), qj, isgz);
//			if (bool == null) {
//				json.setStatus(500);
//			} else {
//				if (bool.booleanValue()) {
//					json.setStatus(600);
//				} else {
//					json.setStatus(700);
//				}
//			}
//			json.setRows(null);
//			json.setMsg("操作成功");
//			json.setSuccess(true);
//			log.info("操作成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "操作失败！");
//		}
//		if (!StringUtil.isEmpty(qj)) {
//			DZFDate from = new DZFDate(qj + "-01");
//			String info = null;
//			if ("true".equals(isgz)) {
//				info = "关账：";
//			}
//			if ("false".equals(isgz)) {
//				info = "取消关账：";
//			}
//			if (!StringUtil.isEmpty(info)) {
//				writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(),
//						info + from.getYear() + "年" + from.getMonth() + "月", ISysConstants.SYS_2);
//			}
//
//		}
//
//		writeJson(json);
//	}
//
//	public void save() {
//		Json json = new Json();
//
//		try {
//			String[] strArr = getRequest().getParameterValues("strArr");
//
//			if (strArr == null || strArr.length == 0) {
//				throw new BusinessException("数据为空");
//			}
//
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			securityserv.checkSecurityForSave(pk_corp, getLogincorppk(), getLoginUserid());
//
//			Map<String, String> fieldMapping = FieldMapping.getFieldMapping(new SalaryReportVO());
//			List<SalaryReportVO> list = new ArrayList<SalaryReportVO>();
//			SalaryReportVO vo1 = null;
//			JSON js = null;
//			for (String str : strArr) {
//				js = (JSON) JSON.parse(str);
//				vo1 = DzfTypeUtils.cast(js, fieldMapping, SalaryReportVO.class, JSONConvtoJAVA.getParserConfig());
//				list.add(vo1);
//			}
//			SalaryReportVO[] vo = gl_gzbserv.save(pk_corp, list, qj, getLoginUserid(), billtype);
//
//			json.setMsg("工资表保存成功");
//			log.info("工资表保存成功");
//			json.setRows(vo);
//			json.setSuccess(true);
//
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "保存失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "工资表保存", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void delete() {
//		Json json = new Json();
//
//		try {
//			String qj = getRequest().getParameter("opdate");
//
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String pk_corp = getRequest().getParameter("ops");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			String primaryKey = getRequest().getParameter("pks");
//			if (StringUtil.isEmpty(primaryKey)) {
//				throw new BusinessException("删除数据为空");
//			}
//
//			securityserv.checkSecurityForDelete(pk_corp, getLogincorppk(), getLoginUserid());
//
//			SalaryReportVO[] vo = gl_gzbserv.delete(pk_corp, primaryKey, qj);
//			json.setRows(vo);
//			json.setMsg("删除成功");
//			json.setSuccess(true);
//			log.info("查询成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "删除失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "工资表删除", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void check() {
//		GzbPzJson json = new GzbPzJson();
//		String qj = getRequest().getParameter("opdate");
//		String str = getRequest().getParameter("operate");
//		try {
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			if (!pk_corp.equals(getLogincorppk())) {
//				CorpVO corp = CorpCache.getInstance().get(null, pk_corp);
//				throw new BusinessException("请切换到" + corp.getUnitname() + "公司,再进行操作！");
//			}
//
//			gl_gzbserv.checkPz(pk_corp, qj, str, true);
//
//			json.setSuccess(true);
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "校验失败！");
//		}
//		writeJson(json);
//
//	}
//
//	public void gzjt() {
//		GzbPzJson json = new GzbPzJson();
//		try {
//			String gzjttotal = getRequest().getParameter("gzjttotal");
//			String qj = getRequest().getParameter("opdate");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			securityserv.checkSecurityForSave(pk_corp, pk_corp, getLoginUserid());
//
//			CorpVO corp = CorpCache.getInstance().get(null, pk_corp);
//			TzpzHVO msg = gl_gzbserv.saveToVoucher(corp, gzjttotal, null, null, null, null, qj, getLoginUserid(),
//					"gzjt");
//			// json.setHvo(msg);
//			json.setData(msg);
//			json.setSuccess(true);
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "工资计提生成凭证失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "工资计提", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//
//	public void queryGlpz(){
//		Json grid = new Json();
//		String sourcebilltype = getRequest().getParameter("sourcebilltype");
//		String pk_corp = getRequest().getParameter("pk_corp");
//		String period = getRequest().getParameter("period");
//		try {
//
//			sourcebilltype = pk_corp + period +"gzjt,"+ pk_corp + period +"gzff";
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(pk_corp);
//			StringBuffer wheresql = new StringBuffer(" pk_corp = ? and nvl(dr,0) = 0 ");
//			if(!StringUtil.isEmpty(sourcebilltype)){
//				if(sourcebilltype.contains(",")){
//					String[] sourcebilltypeArr = sourcebilltype.split(",");
//					wheresql.append(" and ");
//					wheresql.append(SqlUtil.buildSqlForIn("sourcebilltype", sourcebilltypeArr));
//				}else{
//					wheresql.append(" and sourcebilltype = ? ");
//					sp.addParam(sourcebilltype);
//				}
//			}
//			TzpzHVO[] hvos = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, wheresql.toString(), sp);
//			if(hvos == null || hvos.length==0){
//				grid.setData(hvos);
//				grid.setTotal((long)0);
//			}else{
//				grid.setData(hvos);
//				grid.setTotal((long)hvos.length);
//			}
//			grid.setSuccess(true);
//			grid.setMsg("联查成功！");
//		} catch (Exception e) {
//			grid.setRows(new ArrayList<QmclVO>());
//			printErrorLog(grid, log, e, "查询弥补金额失败！");
//		}
//		writeJson(grid);
//	}
//
//	public void gzff() {
//		GzbPzJson json = new GzbPzJson();
//		try {
//			String bxtotal = getRequest().getParameter("bxtotal");
//			String gjjtotal = getRequest().getParameter("gjjtotal");
//			String grsdstotal = getRequest().getParameter("grsdstotal");
//			String sfgztotal = getRequest().getParameter("sfgztotal");
//			String qj = getRequest().getParameter("opdate");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//			CorpVO corp = CorpCache.getInstance().get(null, pk_corp);
//			TzpzHVO msg = gl_gzbserv.saveToVoucher(corp, null, bxtotal, gjjtotal, grsdstotal, sfgztotal, qj,
//					getLogin_userid(), "gzff");
//			// json.setHvo(msg);
//			json.setData(msg);
//			json.setSuccess(true);
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "工资发放生成凭证失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "工资发放", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void getCopyMonth() {
//		Json json = new Json();
//		String copyTodate = getRequest().getParameter("copyTodate");
//		try {
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			SalaryReportVO[] vos = gl_gzbserv.queryAllType(pk_corp, copyTodate);// 查询工资表数据
//			if (vos != null && vos.length > 0) {
//
//			} else {
//				String sql = "select max(qj) from  ynt_salaryreport t where t.qj <? and t.pk_corp =? and nvl(t.dr,0)=0 ";
//				SQLParameter sp = new SQLParameter();
//				sp.addParam(copyTodate);
//				sp.addParam(pk_corp);
//				Object o = singlebo.executeQuery(sql, sp, new ColumnProcessor());
//				json.setData(o);
//			}
//
//			json.setMsg("获取复制期间成功");
//			json.setSuccess(true);
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "获取复制期间失败！");
//		}
//		writeJson(json);
//	}
//
//	public void copyByMonth() {
//		Json json = new Json();
//		String copyFromdate = getRequest().getParameter("copyFromdate");
//		String copyTodate = getRequest().getParameter("copyTodate");
//		try {
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String auto = getRequest().getParameter("auto");
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			SalaryReportVO[] vos = null;
//			if (!StringUtil.isEmpty(auto)) {
//				// 复制最近月份工资表到当前月份
//				if ("Y".equalsIgnoreCase(auto)) {
//					vos = gl_gzbserv.saveCopyByMonth(pk_corp, copyFromdate, copyTodate, getLoginUserid(), null);
//				}
//			} else {
//				vos = gl_gzbserv.saveCopyByMonth(pk_corp, copyFromdate, copyTodate, getLoginUserid(), null);
//			}
//			json.setRows(vos);
//			json.setMsg("复制成功");
//			json.setSuccess(true);
//			log.info("复制成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "复制失败！");
//		}
//
//		if (!StringUtil.isEmpty(copyFromdate)) {// &&
//			// !StringUtil.isEmpty(copyFromdate)
//			DZFDate from = new DZFDate(copyFromdate + "-01");
//			DZFDate to = new DZFDate(copyTodate + "-01");
//			writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(),
//					"复制工资表：" + from.getYear() + "年" + from.getMonth() + "月-" + to.getYear() + "年" + to.getMonth() + "月",
//					ISysConstants.SYS_2);
//		}
//
//		writeJson(json);
//	}
//
//	private int[] deleteArr(int index, int array[]) {
//		// 数组的删除其实就是覆盖前一位
//		int[] arrNew = new int[array.length - 1];
//		for (int i = index; i < array.length - 1; i++) {
//			array[i] = array[i + 1];
//		}
//		System.arraycopy(array, 0, arrNew, 0, arrNew.length);
//		return arrNew;
//	}
//
//	private String[] deleteArr(int index, String array[]) {
//		// 数组的删除其实就是覆盖前一位
//		String[] arrNew = new String[array.length - 1];
//		for (int i = index; i < array.length - 1; i++) {
//			array[i] = array[i + 1];
//		}
//		System.arraycopy(array, 0, arrNew, 0, arrNew.length);
//		return arrNew;
//	}
//
//	public void printAction() {
//		String qijian = null;
//		try {
//			String strlist = getRequest().getParameter("list");
//			String type = getRequest().getParameter("type");
//			String pageOrt = getRequest().getParameter("pageOrt");
//			String left = getRequest().getParameter("left");
//			String top = getRequest().getParameter("top");
//			String printdate = getRequest().getParameter("printdate");
//			String font = getRequest().getParameter("font");
//			String pageNum = getRequest().getParameter("pageNum");
//			String hiddenphone = getRequest().getParameter("hiddenphone");
//			String zbr = getRequest().getParameter("zbr");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("公司为空");
//
//			String opdate = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(opdate))
//				throw new BusinessException("期间为空");
//
//			Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//			pmap.put("type", type);
//			pmap.put("pageOrt", pageOrt);
//			pmap.put("left", left);
//			pmap.put("top", top);
//			pmap.put("printdate", printdate);
//			pmap.put("font", font);
//			pmap.put("pageNum", pageNum);
//			// if (strlist == null) {
//			// return;
//			// }
//			setIscross(DZFBoolean.TRUE);// 是否横向
//			// JSONArray array = (JSONArray) JSON.parseArray(strlist);
//			// Map<String, String> bodymapping =
//			// FieldMapping.getFieldMapping(new SalaryReportVO());
//			// SalaryReportVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping,
//			// SalaryReportVO[].class,
//			// JSONConvtoJAVA.getParserConfig());
//
//			// 查询工资表数据
//			SalaryReportVO[] bodyvos = gl_gzbserv.query(pk_corp, opdate, billtype);
//			if (bodyvos == null || bodyvos.length == 0)
//				return;
//			for (SalaryReportVO vo : bodyvos) {
//				vo.setZjlx(SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()).getName());
//			}
//			qijian = bodyvos[0].getQj();
//			CorpVO cvo = CorpCache.getInstance().get(null, getLogincorppk());
//			bodyvos[0].setPk_corp(cvo.getUnitname());// 用pk_corp属性传递
//			// 公司名称
//			Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title参数
//			tmap.put("公司", cvo.getUnitname());
//			tmap.put("期间", bodyvos[0].getQj());
//			if (!StringUtil.isEmpty(zbr) && new DZFBoolean(zbr).booleanValue()) {
//				tmap.put("制表人", getLoginUserInfo().getUser_name());
//			}
//			setTableHeadFount(new Font(getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
//			List<Integer> hiddenColList = getHiddenColumn(billtype);
//			if (!StringUtil.isEmpty(hiddenphone) && new DZFBoolean(hiddenphone).booleanValue()) {
//				// 隐藏手机号
//				hiddenColList.add(1);
//			}
//			List<SalaryReportVO> list = new ArrayList<SalaryReportVO>();
//			for (SalaryReportVO vo : bodyvos) {
//				list.add(vo);
//			}
//			SalaryReportVO nvo = calTotal(list.toArray(new SalaryReportVO[list.size()]));
//			list.add(nvo);
//			setLineheight(22F);
//
//			String[] columns = SalaryReportColumn.getCodes(hiddenColList);
//			String[] columnNames = SalaryReportColumn.getNames(hiddenColList, billtype);
//			int[] widths = SalaryReportColumn.getWidths(hiddenColList);
//			for (int i = 0; i < columnNames.length; i++) {
//				if ("费用科目".equals(columnNames[i])) {
//					columnNames = deleteArr(i, columnNames);
//					columns = deleteArr(i, columns);
//					widths = deleteArr(i, widths);
//					break;
//				}
//			}
//			if (pmap.get("type").equals("4")) {
//				rotate = DZFBoolean.TRUE;
//			}
//
//			printHz(new HashMap<String, List<SuperVO>>(), list.toArray(new SalaryReportVO[list.size()]),
//					"工 资 表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")", columns, columnNames,
//					widths, 60, type, pmap, tmap);
//
//		} catch (DocumentException e) {
//			log.error("操作失败!", e);
//		} catch (IOException e) {
//			log.error("操作失败!", e);
//		} catch (Exception e) {
//			log.error("操作失败!", e);
//		}
//
//		if (!StringUtil.isEmpty(qijian)) {
//			DZFDate from = new DZFDate(qijian + "-01");
//			writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(),
//					"工资表打印：" + from.getYear() + "年" + from.getMonth() + "月", ISysConstants.SYS_2);
//		}
//	}
//
//	// impExcel
//	public void impExcel() {
//		Json json = new Json();
//		String opdate = null;
//		try {
//
//			File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("impfile");
//			if (infiles == null || infiles.length == 0) {
//				throw new BusinessException("请选择导入文件!");
//			}
//			String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames("impfile");
//			opdate = ((MultiPartRequestWrapper) getRequest()).getParameter("period");
//			String billtype = ((MultiPartRequestWrapper) getRequest()).getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			File infile = infiles[0];
//			String filename = fileNames[0];
//			int index = filename.lastIndexOf(".");
//			String filetype = filename.substring(index + 1);
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//			CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//			Object[] vos = onBoImp(infile, opdate, filetype, billtype, corpvo);
//
//			json.setMsg("");
//			json.setRows(vos);
//			json.setSuccess(true);
//
//		} catch (Exception e) {
//			// log.error("文件导入失败!" , e);
//			printErrorLog(json, log, e, "文件导入失败!");
//			// json.setSuccess(false);
//			// json.setMsg("文件导入失败!\n" ,e);
//		}
//		if (!StringUtil.isEmpty(opdate)) {
//			DZFDate date = new DZFDate(opdate + "-01");
//			writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(),
//					"导入工资表：" + date.getYear() + "年" + date.getMonth() + "月", ISysConstants.SYS_2);
//		}
//		writeJson(json);
//	}
//
//	private Object[] onBoImp(File infile, String opdate, String filename, String billtype, CorpVO corpvo)
//			throws Exception {
//		FileInputStream is = null;
//		try {
//			is = new FileInputStream(infile);
//			Workbook rwb = null;
//			if ("xls".equals(filename)) {
//				rwb = new HSSFWorkbook(is);
//			} else if ("xlsx".equals(filename)) {
//				rwb = new XSSFWorkbook(is);
//			} else {
//				throw new BusinessException("不支持的文件格式");
//			}
//			// XSSFWorkbook rwb = new XSSFWorkbook(is);
//			int sheetno = rwb.getNumberOfSheets();
//			if (sheetno == 0) {
//				throw new Exception("需要导入的数据为空。");
//			}
//			Sheet sheets = rwb.getSheetAt(0);// 取第2个工作簿
//			CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(corpvo.getPk_corp());
//			ISalaryReportExcel salExcel = null;
//			if ("2019-01".compareTo(opdate) > 0
//					|| (!StringUtil.isEmpty(billtype) && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
//				salExcel = factory.produce(corptaxvo);
//			} else {
//				salExcel = factory.produce2019(corptaxvo);
//			}
//			SalaryReportVO[] vos = salExcel.impExcel(infile.getPath(), sheets, opdate, billtype, corptaxvo);
//
//			/// 根据类型拆成多个数组
//			if (vos == null || vos.length <= 0) {
//				throw new BusinessException("导入文件数据为空，请检查。");
//			}
//
//			Object[] objs = gl_gzbserv.saveImpExcel(getLoginDate(), getLoginUserid(), corpvo, vos, opdate,
//					vos[0].getImpmodeltype(), billtype);
//
//			return objs;
//		} catch (FileNotFoundException e2) {
//			throw new Exception("文件未找到");
//		} catch (IOException e2) {
//			throw new Exception("文件格式不正确，请选择导入文件");
//		} catch (Exception e2) {
//			throw e2;
//		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (Exception e) {
//					throw e;
//				}
//			}
//		}
//	}
//
//	public void expExcelData() {
//		HttpServletRequest request = getRequest();
//		request.getParameterNames();
//		/*
//		 * String ids = request.getParameter("id"); List<KmZzVO> listVo =
//		 * am_kpglserv.queryByIds(ids);
//		 */
//
//		String strlist = getRequest().getParameter("list");
//
//		String billtype = getRequest().getParameter("billtype");
//		if (StringUtil.isEmpty(billtype))
//			throw new BusinessException("类型为空");
//
//		String pk_corp = getRequest().getParameter("pk_corp");
//		if (StringUtil.isEmpty(billtype))
//			throw new BusinessException("公司为空");
//
//		String opdate = getRequest().getParameter("opdate");
//		if (StringUtil.isEmpty(opdate))
//			throw new BusinessException("期间为空");
//
//		// JSONArray array = (JSONArray) JSON.parseArray(strlist);
//		// Map<String, String> bodymapping = FieldMapping.getFieldMapping(new
//		// SalaryReportVO());
//		// SalaryReportVO[] listVo = DzfTypeUtils.cast(array, bodymapping,
//		// SalaryReportVO[].class,
//		// JSONConvtoJAVA.getParserConfig());
//
//		// 查询工资表数据
//		SalaryReportVO[] listVo = gl_gzbserv.query(pk_corp, opdate, billtype);
//
//		if (listVo == null || listVo.length == 0)
//			return;
//
//		String zbr = getRequest().getParameter("zbr");// 制表人
//		for (SalaryReportVO vo : listVo) {
//			vo.setZjlx(SalaryReportEnum.getTypeEnumByValue(vo.getZjlx()).getName());
//			if (!StringUtil.isEmpty(zbr) && new DZFBoolean(zbr).booleanValue()) {
//				if (StringUtil.isEmpty(vo.getCoperatorid())) {
//					vo.setCoperatorid(getLoginUserid());
//				}
//			} else {
//				vo.setCoperatorid(null);
//			}
//		}
//		String qj = listVo[0].getQj();
//		String[] qjval = qj.split("-");
//		StringBuffer sb = new StringBuffer();
//		sb.append(qjval[0]).append("年").append(qjval[1])
//				.append("月工资表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ").xls");
//		HttpServletResponse response = getResponse();
//		ExcelReport<SalaryReportVO> ex = new ExcelReport<SalaryReportVO>();
//		String hiddenphone = getRequest().getParameter("hiddenphone");
//
//		List<Integer> hiddenColList = getHiddenColumn(billtype);
//		if (!StringUtil.isEmpty(hiddenphone) && new DZFBoolean(hiddenphone).booleanValue()) {
//			// 隐藏手机号
//			hiddenColList.add(1);
//		}
//		Map<String, String> map = SalaryReportColumn.getMapColumn(hiddenColList, billtype);
//
//		map.remove("fykmname");
//
//		String[] enFields = new String[map.size()];
//		String[] cnFields = new String[map.size()];
//		// 填充普通字段数组
//		int count = 0;
//		for (Entry<String, String> entry : map.entrySet()) {
//			enFields[count] = entry.getKey();
//			cnFields[count] = entry.getValue();
//			count++;
//		}
//
//		List<SalaryReportVO> list = new ArrayList<SalaryReportVO>();
//		for (SalaryReportVO vo : listVo) {
//			list.add(vo);
//		}
//
//		SalaryReportVO nvo = calTotal(list.toArray(new SalaryReportVO[list.size()]));
//		list.add(nvo);
//		OutputStream toClient = null;
//		DZFDate udate = new DZFDate();
//		try {
//			response.reset();
//			String exName = sb.toString();
//			exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");
//			String date = DateUtils.getDate(udate.toDate());
//			response.addHeader("Content-Disposition", "attachment;filename=" + exName);
//			toClient = new BufferedOutputStream(response.getOutputStream());
//			response.setContentType("application/vnd.ms-excel;charset=gb2312");
//			byte[] length = ex.exportExcel("工 资 表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")",
//					cnFields, enFields, list, getLoginCorpInfo().getUnitname(), qj, toClient, userServiceImpl);
//			String srt2 = new String(length, "UTF-8");
//			response.addHeader("Content-Length", srt2);
//			toClient.flush();
//			response.getOutputStream().flush();
//
//		} catch (IOException e) {
//			log.error("excel导出错误", e);
//		} catch (Exception e) {
//			log.error("excel导出错误", e);
//		} finally {
//			try {
//				if (toClient != null) {
//					toClient.close();
//				}
//			} catch (IOException e) {
//				log.error("excel导出错误", e);
//			}
//			try {
//				if (response != null && response.getOutputStream() != null) {
//					response.getOutputStream().close();
//				}
//			} catch (IOException e) {
//				log.error("excel导出错误", e);
//			}
//		}
//
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "导出工资表：" + qjval[0] + "年" + qjval[1] + "月",
//				ISysConstants.SYS_2);
//	}
//
//	// 导出模板内容
//	// 导出模板内容
//	public void expExcel() {
//		HttpServletResponse response = getResponse();
//		OutputStream toClient = null;
//		try {
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//			String type = getRequest().getParameter("type");
//			if (StringUtil.isEmpty(type))
//				type = "1";
//			response.reset();
//			String fileName = null;
//			byte[] length = null;
//			if ("1".equals(type)) {
//				if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
//					fileName = "salarytemplate.xlsx";
//				} else if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
//					fileName = "salarytemplate_lw.xls";
//				} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
//					fileName = "salarytemplate_wj.xls";
//				} else if (billtype.equals(SalaryTypeEnum.ANNUALBONUS.getValue())) {
//					fileName = "salarytemplate_nz.xls";
//				}
//
//				// 设置response的Header
//				String date = "工资表(" + SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ")";
//				String exName = new String(date.getBytes("GB2312"), "ISO_8859_1");
//				if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
//					response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName + ".xlsx"));
//				} else {
//					response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName + ".xls"));
//				}
//				toClient = new BufferedOutputStream(response.getOutputStream());
//				response.setContentType("application/vnd.ms-excel;charset=gb2312");
//				length = expExcel(toClient, fileName);
//			} else {
//				// 设置response的Header
//				String exName = new String("操作说明".getBytes("GB2312"), "ISO_8859_1");
//				response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName + ".docx"));
//				toClient = new BufferedOutputStream(response.getOutputStream());
//				response.setContentType("application/vnd.ms-excel;charset=gb2312");
//				fileName = "instructions.docx";
//				length = expDoc(toClient, fileName);
//			}
//
//			String srt2 = new String(length, "UTF-8");
//			response.addHeader("Content-Length", srt2);
//			toClient.flush();
//			response.getOutputStream().flush();
//		} catch (IOException e) {
//			log.error("excel导出错误", e);
//		} catch (Exception e) {
//			log.error("excel导出错误", e);
//		} finally {
//			try {
//				if (toClient != null) {
//					toClient.close();
//				}
//			} catch (Exception e) {
//				log.error("excel导出错误", e);
//			}
//			try {
//				if (response != null && response.getOutputStream() != null) {
//					response.getOutputStream().close();
//				}
//			} catch (Exception e) {
//				log.error("excel导出错误", e);
//			}
//		}
//	}
//
//	public byte[] expDoc(OutputStream out, String fileName) throws Exception {
//		ByteArrayOutputStream bos = null;
//		InputStream is = null;
//		try {
//
//			Resource exportTemplate = new ClassPathResource("template/report/taxdeclaration/" + fileName);
//			is = exportTemplate.getInputStream();
//			bos = new ByteArrayOutputStream();
//			int byteRead = 0;
//			byte[] buffer = new byte[512];
//			while ((byteRead = is.read(buffer)) != -1) {
//				out.write(buffer, 0, byteRead);
//			}
//			is.close();
//			bos = new ByteArrayOutputStream();
//			bos.writeTo(out);
//			return bos.toByteArray();
//		} catch (IOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//
//				}
//			}
//
//			if (bos != null) {
//				try {
//					bos.close();
//				} catch (IOException e) {
//
//				}
//			}
//		}
//	}
//
//	public byte[] expExcel(OutputStream out, String fileName) throws Exception {
//		ByteArrayOutputStream bos = null;
//		InputStream is = null;
//		try {
//
//			Resource exportTemplate = new ClassPathResource("template/report/taxdeclaration/" + fileName);
//			is = exportTemplate.getInputStream();
//			bos = new ByteArrayOutputStream();
//			if (fileName.indexOf(".xlsx") > 0) {
//				XSSFWorkbook xworkbook = new XSSFWorkbook(is);
//				is.close();
//				bos = new ByteArrayOutputStream();
//				xworkbook.write(bos);
//			} else {
//				HSSFWorkbook gworkbook = new HSSFWorkbook(is);
//				is.close();
//				bos = new ByteArrayOutputStream();
//				gworkbook.write(bos);
//			}
//			bos.writeTo(out);
//			return bos.toByteArray();
//		} catch (IOException e) {
//			throw e;
//		} catch (Exception e) {
//			throw e;
//		} finally {
//			if (is != null) {
//				try {
//					is.close();
//				} catch (IOException e) {
//
//				}
//			}
//
//			if (bos != null) {
//				try {
//					bos.close();
//				} catch (IOException e) {
//
//				}
//			}
//		}
//	}
//
//	public void expNSSBB() {
//
//		HttpServletResponse response = getResponse();
//		OutputStream toClient = null;
//		String period = getRequest().getParameter("period");
//		try {
//
//			if (StringUtil.isEmpty(period))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp))
//				throw new BusinessException("公司为空");
//
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			// 查询工资表数据
//			SalaryReportVO[] vos = gl_gzbserv.query(pk_corp, period, billtype);
//			if (vos.length == 0)
//				return;
//
//			String json = JSONProcessor.toJSONString(vos, new FastjsonFilter(), new SerializerFeature[] {
//					SerializerFeature.WriteDateUseDateFormat, SerializerFeature.DisableCircularReferenceDetect });
//
//			JSONArray jsonArray = (JSONArray) JSON.parseArray(json);
//
//			response.reset();
//			CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
//			// CorpVO corpvo = (CorpVO) singlebo.queryByPrimaryKey(CorpVO.class,
//			// pk_corp);
//			ISalaryReportExcel salExcel = null;
//			if ("2019-01".compareTo(period) > 0
//					|| (!StringUtil.isEmpty(billtype) && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
//				salExcel = factory.produce(corptaxvo);
//			} else {
//				salExcel = factory.produce2019(corptaxvo);
//			}
//
//			String exName = new String(SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + "("
//					+ salExcel.getAreaName(corptaxvo) + ").xls");
//
//			if (StringUtil.isEmpty(salExcel.getAreaName(corptaxvo))) {
//				exName = new String(SalaryTypeEnum.getTypeEnumByValue(billtype).getName() + ".xls");
//			}
//			exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
//			toClient = new BufferedOutputStream(response.getOutputStream());
//			response.setContentType("application/vnd.ms-excel;charset=gb2312");
//			byte[] length = null;
//			Map<String, Integer> tabidsheetmap = new HashMap<String, Integer>();
//			tabidsheetmap.put("B100000", 0);
//
//			length = salExcel.exportExcel(jsonArray, toClient, billtype, corptaxvo, getLoginUserInfo());
//			String srt2 = new String(length, "UTF-8");
//			response.addHeader("Content-Length", srt2);
//			toClient.flush();
//			response.getOutputStream().flush();
//		} catch (Exception e) {
//			log.error("excel导出错误", e);
//		} finally {
//			try {
//				if (toClient != null) {
//
//					toClient.close();
//				}
//			} catch (Exception e) {
//				log.error("excel导出错误", e);
//			}
//			try {
//				if (response != null && response.getOutputStream() != null) {
//					response.getOutputStream().close();
//				}
//			} catch (Exception e) {
//				log.error("excel导出错误", e);
//			}
//			String date[] = period.split("-");
//			writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "导出纳税申报表：" + date[0] + "年" + date[1] + "月",
//					ISysConstants.SYS_2);
//		}
//	}
//
//	public void expPerson() {
//
//		HttpServletResponse response = getResponse();
//		Json json = new Json();
//		String period = getRequest().getParameter("period");
//		String isexp = null;
//		try {
//
//			if (StringUtil.isEmpty(period))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp))
//				throw new BusinessException("公司为空");
//
//			isexp = getRequest().getParameter("isexp");
//
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			// 查询工资表数据
//			SalaryReportVO[] vos = gl_gzbserv.queryAllType(pk_corp, period);
//			if (vos == null || vos.length == 0)
//				return;
//			Map<String, List<String>> map = gl_gzbserv.queryAllTypeBeforeCurr(pk_corp, period);
//			String qj = vos[0].getQj();
//
//			String preqj = DateUtils.getPreviousPeriod(qj);
//			SalaryReportVO[] vos1 = gl_gzbserv.queryAllType(vos[0].getPk_corp(), preqj);// 查询上一个月工资表数据
//
//			List<SalaryReportVO> list = new ArrayList<>();
//			List<String> slist = new ArrayList<>();
//			for (SalaryReportVO vo : vos) {
//				String minqj = null;
//				List<String> qlist = map.get(vo.getCpersonid());
//				if (qlist == null || qlist.size() == 0) {
//					minqj = period;
//				} else {
//					minqj = qlist.get(0);
//				}
//				if (StringUtil.isEmpty(vo.getVdef1()))
//					vo.setVdef1(DateUtils.getPeriodStartDate(minqj).toString());
//				vo.setRyzt("正常");
//				list.add(vo);
//				slist.add(vo.getZjbm());
//			}
//
//			if (vos1 != null && vos1.length > 0) {
//				for (SalaryReportVO vo : vos1) {
//					if (!slist.contains(vo.getZjbm())) {
//						String minqj = null;
//						String maxqj = null;
//						List<String> qlist = map.get(vo.getCpersonid());
//						if (qlist == null || qlist.size() == 0) {
//							minqj = period;
//							maxqj = period;
//						} else {
//							minqj = qlist.get(0);
//							maxqj = qlist.get(qlist.size() - 1);
//						}
//						if (StringUtil.isEmpty(vo.getVdef1()))
//							vo.setVdef1(DateUtils.getPeriodStartDate(minqj).toString());
//						if (StringUtil.isEmpty(vo.getVdef2()))
//							vo.setVdef2(DateUtils.getPeriodEndDate(maxqj).toString());
//						vo.setRyzt("非正常");
//						list.add(vo);
//					}
//				}
//			}
//
//			if (StringUtil.isEmpty(isexp) || "N".equals(isexp)) {
//				if ("2019-01".compareTo(period) > 0) {
//				} else {
//					for (SalaryReportVO vo : list) {
//						checkPersonInfo(vo);
//					}
//				}
//
//			} else {
//				expPerson(response, list, pk_corp, period, billtype);
//			}
//			json.setMsg("excel导出成功");
//			json.setSuccess(true);
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "excel导出失败！");
//		}
//		if (StringUtil.isEmpty(isexp) || "N".equals(isexp)) {
//			writeJson(json);
//		}
//
//	}
//
//	private void expPerson(HttpServletResponse response, List<SalaryReportVO> list, String pk_corp, String period,
//			String billtype) {
//
//		OutputStream toClient = null;
//		try {
//
//			CorpTaxVo corptaxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
//
//			ISalaryReportExcel salExcel = null;
//			if ("2019-01".compareTo(period) > 0
//					|| (!StringUtil.isEmpty(billtype) && !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
//				salExcel = factory.produce(corptaxvo);
//			} else {
//				salExcel = factory.produce2019(corptaxvo);
//				for (SalaryReportVO vo : list) {
//					if (SalaryTypeEnum.REMUNERATION.getValue().equals(vo.getBilltype())) {
//						vo.setSfgy("雇员");
//					} else {
//						vo.setSfgy("雇员");
//					}
//				}
//			}
//
//			String json = JSONProcessor.toJSONString(list.toArray(new SalaryReportVO[list.size()]),
//					new FastjsonFilter(), new SerializerFeature[] { SerializerFeature.WriteDateUseDateFormat,
//							SerializerFeature.DisableCircularReferenceDetect });
//			JSONArray jsonArray = (JSONArray) JSON.parseArray(json);
//			// CorpVO corpvo = (CorpVO) singlebo.queryByPrimaryKey(CorpVO.class,
//			// pk_corp);
//			response.reset();
//			String exName = new String("人员信息.xls");
//			exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
//			toClient = new BufferedOutputStream(response.getOutputStream());
//			response.setContentType("application/vnd.ms-excel;charset=gb2312");
//			byte[] length = null;
//			length = salExcel.expPerson(jsonArray, response.getOutputStream(), billtype);
//			String srt2 = new String(length, "UTF-8");
//			response.addHeader("Content-Length", srt2);
//			toClient.flush();
//			response.getOutputStream().flush();
//		} catch (Exception e) {
//			log.error("excel导出错误", e);
//		} finally {
//			try {
//				if (toClient != null) {
//					toClient.close();
//				}
//			} catch (Exception e) {
//				log.error("excel导出错误", e);
//			}
//			try {
//				if (response != null && response.getOutputStream() != null) {
//					response.getOutputStream().close();
//				}
//			} catch (Exception e) {
//				log.error("excel导出错误", e);
//			}
//			String date[] = period.split("-");
//			writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "个人信息：" + date[0] + "年" + date[1] + "月",
//					ISysConstants.SYS_2);
//		}
//	}
//
//	private void checkPersonInfo(SalaryReportVO vo) {
//		if (StringUtil.isEmpty(vo.getZjlx())) {
//			throw new BusinessException("证件类型不能为空");
//		} else {
//			if (!SalaryReportEnum.IDCARD.getValue().equals(vo.getZjlx())) {
//				if (StringUtil.isEmpty(vo.getYgname())) {
//					throw new BusinessException("姓名不能为空");
//				}
//				if (StringUtil.isEmpty(vo.getVarea())) {
//					throw new BusinessException("国籍不能为空");
//				}
//				if (StringUtil.isEmpty(vo.getVdef1())) {
//					throw new BusinessException("任职受雇日期不能为空");
//				}
////				if (StringUtil.isEmpty(vo.getVdef2())) {
////					throw new BusinessException("离职日期不能为空");
////				}
//				if (StringUtil.isEmpty(vo.getVdef3())) {
//					throw new BusinessException("出生日期不能为空");
//				}
//				if (StringUtil.isEmpty(vo.getVdef4())) {
//					throw new BusinessException(" 性别不能为空");
//				}
//			}
//		}
//	}
//
//	private List<Integer> getHiddenColumn(String billtype) {
//		List<Integer> list = new ArrayList<>();
//		int[] hidenCol = null;
//		if (billtype.equals(SalaryTypeEnum.REMUNERATION.getValue())) {
//			hidenCol = SalaryReportColumn.LWHIDEN;
//		} else if (billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue())) {
//			hidenCol = SalaryReportColumn.WJGZHIDEN;
//		} else if (billtype.equals(SalaryTypeEnum.ANNUALBONUS.getValue())) {
//			hidenCol = SalaryReportColumn.NZJHIDEN;
//		} else if (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())) {
//			hidenCol = SalaryReportColumn.ZCHIDEN;
//		}
//
//		if (hidenCol == null || hidenCol.length == 0)
//			return list;
//		for (int col : hidenCol) {
//			list.add(col);
//		}
//		return list;
//	}
//
//	public void setFykm() {
//		Json json = new Json();
//
//		try {
//
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("ops");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String fykmid = getRequest().getParameter("fykmid");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("更新科目为空");
//			}
//
//			String primaryKey = getRequest().getParameter("pks");
//			if (StringUtil.isEmpty(primaryKey)) {
//				throw new BusinessException("更新数据为空");
//			}
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			gl_gzbserv.updateFykm(pk_corp, fykmid, primaryKey, qj, billtype);
//			json.setMsg("更新费用科目成功");
//			json.setSuccess(true);
//			log.info("更新费用科目成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "更新费用科目失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "更新费用科目", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void setDept() {
//		Json json = new Json();
//
//		try {
//
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("ops");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String cdeptid = getRequest().getParameter("cdeptid");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("更新部门为空");
//			}
//
//			String primaryKey = getRequest().getParameter("pks");
//			if (StringUtil.isEmpty(primaryKey)) {
//				throw new BusinessException("更新数据为空");
//			}
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			gl_gzbserv.updateDeptid(pk_corp, cdeptid, primaryKey, qj, billtype);
//			json.setMsg("更新部门成功");
//			json.setSuccess(true);
//			log.info("更新部门成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "更新费用科目失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "更新费用科目", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void getSalaryAccSet() {
//		Json json = new Json();
//		try {
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String isnew = getRequest().getParameter("isnew");
//			SalaryReportVO[] vos = null;
//			String cpersonids = getRequest().getParameter("cpersonids");
//			String opdate = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(opdate)) {
//				throw new BusinessException("期间为空");
//			}
//			if (isnew == null || !"true".equals(isnew)) {
//				vos = gl_gzbserv.getSalarySetInfo(pk_corp, billtype, cpersonids, opdate);
//			} else {
//				if ("2019-01".compareTo(opdate) > 0 || (!StringUtil.isEmpty(billtype)
//						&& !billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue()))) {
//					vos = gl_gzbserv.getSalarySetInfo(pk_corp, billtype, cpersonids, opdate);
//				} else {
//					vos = gl_gzbserv.calLjData(pk_corp, cpersonids, billtype, opdate);
//				}
//			}
//
//			json.setMsg("获取工资科目设置成功");
//			json.setRows(vos);
//			json.setSuccess(true);
//			log.info("获取工资科目设置成功");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "获取工资设置信息失败！");
//		}
//		writeJson(json);
//	}
//
//	public void changeNum() {
//		Json json = new Json();
//		try {
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String billtype = getRequest().getParameter("billtype");
//			if (StringUtil.isEmpty(billtype))
//				throw new BusinessException("类型为空");
//
//			String pk_corp = getRequest().getParameter("ops");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String primaryKey = getRequest().getParameter("pks");
//			if (StringUtil.isEmpty(primaryKey)) {
//				throw new BusinessException("更新数据为空");
//			}
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			String strlist = getRequest().getParameter("chgdata");
//			if (!StringUtil.isEmpty(strlist) && (billtype.equals(SalaryTypeEnum.NORMALSALARY.getValue())
//					|| billtype.equals(SalaryTypeEnum.FOREIGNSALARY.getValue()))) {
//				JSONObject array = (JSONObject) JSON.parseObject(strlist);
//				Map<String, String> bodymapping = FieldMapping.getFieldMapping(new SalaryBaseVO());
//				SalaryBaseVO setvo = DzfTypeUtils.cast(array, bodymapping, SalaryBaseVO.class,
//						JSONConvtoJAVA.getParserConfig());
//				gl_gzbbaseserv.updateChangeNum(pk_corp, setvo, primaryKey, qj, billtype);
//			}
//			json.setMsg("调整基数成功");
//			json.setSuccess(true);
//			log.info("调整基数成功");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "调整基数失败！");
//		}
//
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "调整基数", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void checkJzDate() {
//
//		Json json = new Json();
//		try {
//			String date = getRequest().getParameter("date");
//			String pk_corp = getRequest().getParameter("corp_id");
//
//			securityserv.checkSecurityForSave(null, null, pk_corp, pk_corp, getLoginUserid());
//
//			CorpVO corpVo = CorpCache.getInstance().get(null, pk_corp);
//			if (corpVo == null)
//				throw new BusinessException("选择公司出错！");
//			else if (corpVo.getBegindate() == null)
//				throw new BusinessException("公司建账日期为空！");
//			json.setData(corpVo.getBegindate().toDate());
//			json.setSuccess(true);
//		} catch (Exception e) {
//			log.error("错误", e);
//			printErrorLog(json, log, e, "校验日期失败！");
//		}
//		writeJson(json);
//
//	}
//
//	public void getNationalArea() {
//		Json json = new Json();
//		try {
//			securityserv.checkSecurityForSave(null, null, getLogincorppk(), getLogincorppk(), getLoginUserid());
//			String nationalArea = NationalAreaUtil.getNationalArea();
//			String[] nationals = nationalArea.split(",");
//			List<SalaryReportVO> list = new ArrayList<>();
//			for (String str : nationals) {
//				SalaryReportVO vo = new SalaryReportVO();
//				vo.setVarea(str);
//				list.add(vo);
//			}
//			json.setRows(list);
//			json.setSuccess(true);
//		} catch (Exception e) {
//			log.error("错误", e);
//			printErrorLog(json, log, e, "校验日期失败！");
//		}
//		writeJson(json);
//
//	}
}

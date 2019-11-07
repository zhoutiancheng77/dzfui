//package com.dzf.zxkj.platform.controller.icreport;
//
//import java.io.BufferedOutputStream;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.net.URLEncoder;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//import org.apache.struts2.convention.annotation.Action;
//import org.apache.struts2.convention.annotation.Namespace;
//import org.apache.struts2.convention.annotation.ParentPackage;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.dzf.action.gl.lxsexport.KccbExcelField;
//import com.dzf.model.ic.ic_bdset.IcbalanceVO;
//import com.dzf.model.ic.ic_bdset.InventoryVO;
//import com.dzf.model.pub.Grid;
//import com.dzf.model.pub.PrintParamVO;
//import com.dzf.model.pub.QueryParamVO;
//import com.dzf.model.sys.sys_power.CorpVO;
//import com.dzf.pub.BusinessException;
//import com.dzf.pub.DzfTypeUtils;
//import com.dzf.pub.ISysConstants;
//import com.dzf.pub.StringUtil;
//import com.dzf.pub.SuperVO;
//import com.dzf.pub.Field.FieldMapping;
//import com.dzf.pub.cache.CorpCache;
//import com.dzf.pub.excel.Excelexport2003;
//import com.dzf.pub.lang.DZFBoolean;
//import com.dzf.pub.lang.DZFDate;
//import com.dzf.pub.param.IParameterConstants;
//import com.dzf.pub.util.DZfcommonTools;
//import com.dzf.pub.util.DateUtils;
//import com.dzf.pub.util.JSONConvtoJAVA;
//import com.dzf.pub.util.SafeCompute;
//import com.dzf.service.gl.gl_pzgl.impl.Kmschema;
//import com.dzf.service.ic.ic_bdset.IInventoryService;
//import com.dzf.service.ic.ic_report.IQueryLastNum;
//import com.dzf.service.pub.LogRecordEnum;
//import com.dzf.service.pub.report.PrintReportAction;
//import com.dzf.service.sys.sys_power.IUserService;
//import com.dzf.service.sys.sys_set.IParameterSetService;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Font;
//
///**
// * 库存成本表
// *
// */
//@ParentPackage("basePackage")
//@Namespace("/ic")
//@Action(value = "ic_rep_cbbact")
//public class ICbbController extends PrintReportAction<IcbalanceVO> {// implements
//																// ModelDriven<IcbalanceVO>{
//
//	private Logger log = Logger.getLogger(this.getClass());
//
//	@Autowired
//	private IQueryLastNum ic_rep_cbbserv;
//	@Autowired
//	private IParameterSetService parameterserv;
//	@Autowired
//	private IInventoryService iservice;
//	@Autowired
//	private IUserService userService;
//
//	// 查询
//	public void query() {
//		Grid grid = new Grid();
//		grid.setRows(new ArrayList<IcbalanceVO>());
//		// String corpid = (String)
//		// getSession().getAttribute(IGlobalConstants.login_corp);
//		String qryDate = null;
//		try {
//
//			int page = getPage();
//			int rows = getRows();
//			if (page < 1 || rows < 1) {
//				throw new BusinessException("查询失败！");
//			}
//			// QueryParamVO queryParamvo = getQueryParamVO();
//			qryDate = data.getDbilldate();
//			checkPowerDate(data);
//			String pk_invtory = data.getPk_inventory();
//			String xsyye = getRequest().getParameter("xsyye");
//			String pk_subjectname = data.getPk_subjectname();
//			String priceStr = parameterserv.queryParamterValueByCode(getLogincorppk(), IParameterConstants.DZF010);
//			// String pk_invclassify =
//			// getRequest().getParameter("pk_invclassify");
//			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//			List<IcbalanceVO> flist = queryList(qryDate, pk_invtory, xsyye, pk_subjectname, price);
//
//			grid.setTotal(Long.valueOf(flist == null ? 0 : flist.size()));
//			if (flist != null && flist.size() > 0) {
//				IcbalanceVO[] pvos = getPageVOs(flist.toArray(new IcbalanceVO[flist.size()]), page, rows);
//				flist = Arrays.asList(pvos);
//			}
//			grid.setRows(flist == null ? new ArrayList<IcbalanceVO>() : flist);
//			grid.setSuccess(true);
//			grid.setMsg("查询成功");
//		} catch (Exception e) {
//			// log.info("查询失败！");
//			// grid.setTotal(Long.valueOf(0));
//			// grid.setSuccess(false);
//			// grid.setMsg("查询失败！");
//			printErrorLog(grid, log, e, "查询失败");
//		}
//		// 日志记录
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT.getValue(),
//				new StringBuffer().append("库存成本表查询:").append(qryDate).toString(), ISysConstants.SYS_2);
//		writeJson(grid);
//	}
//
//	private List<IcbalanceVO> queryList(String qryDate, String pk_invtory, String xsyye, String pk_subjectname,
//			int price) {
//		String corpid = getLogincorppk();
//
//		List<IcbalanceVO> flist = new ArrayList<>();
//		if (corpid != null && !StringUtil.isEmptyWithTrim(qryDate)) {
//			List<IcbalanceVO> list = ic_rep_cbbserv.queryLastBanlanceVOs_byList1(qryDate, corpid, pk_invtory, null,
//					true);
//			if (list != null && list.size() > 0) {
//				List<InventoryVO> splist = iservice.queryInfo(getLogincorppk(), null);
//				Map<String, InventoryVO> invMap = DZfcommonTools.hashlizeObjectByPk(splist,
//						new String[] { "pk_inventory" });
//				CorpVO corpVo = CorpCache.getInstance().get(null, getLogincorppk());
//
//				Map<String, IcbalanceVO> balMap1 = ic_rep_cbbserv.queryLastBanlanceVOs_byMap4(qryDate.toString(),
//						getLogincorppk(), null, true);
//				for (IcbalanceVO vo : list) {
//					if (corpVo.getIbuildicstyle() != null && corpVo.getIbuildicstyle() == 1) {// 新模式库存
//						IcbalanceVO balvo1 = balMap1.get(vo.getPk_inventory());
//						if (balvo1 != null) {
//							if ((vo.getNnum() == null || vo.getNnum().doubleValue() == 0)
//									&& (vo.getNcost() == null || vo.getNcost().doubleValue() == 0)) {
//							} else {
//								vo.setNprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum()).setScale(price, 2));
//								vo.setNcost(balvo1.getNcost());
//							}
//						}
//					} else {
//						if ((vo.getNnum() == null || vo.getNnum().doubleValue() == 0)
//								&& (vo.getNcost() == null || vo.getNcost().doubleValue() == 0)) {
//						} else {
//							vo.setNprice(SafeCompute.div(vo.getNcost(), vo.getNnum()).setScale(price, 2));
//						}
//					}
//
//					if ("Y".equalsIgnoreCase(xsyye)) {
//						if ((vo.getNnum() == null || vo.getNnum().doubleValue() == 0)
//								&& (vo.getNcost() == null || vo.getNcost().doubleValue() == 0)) {
//							continue;
//						}
//					}
//
//					if (StringUtil.isEmpty(pk_subjectname)) {
//						flist.add(vo);
//					} else if ("库存商品".equals(pk_subjectname)) {
//						InventoryVO invvo = invMap.get(vo.getPk_inventory());
//						if (invvo != null) {
//							if (Kmschema.isKcspbm(corpVo.getCorptype(), invvo.getKmcode())) {
//								flist.add(vo);
//							}
//						}
//
//					} else if ("原材料".equals(pk_subjectname)) {
//						InventoryVO invvo = invMap.get(vo.getPk_inventory());
//						if (invvo != null) {
//							if (Kmschema.isYclbm(corpVo.getCorptype(), invvo.getKmcode())) {
//								flist.add(vo);
//							}
//						}
//					} else {
//						flist.add(vo);
//					}
//				}
//			}
//			if (flist != null && flist.size() > 0) {
//				Collections.sort(flist, new Comparator<IcbalanceVO>() {
//					@Override
//					public int compare(IcbalanceVO o1, IcbalanceVO o2) {
//						int i = o1.getInventorycode().compareTo(o2.getInventorycode());
//						return i;
//					}
//				});
//			}
//		}
//		return flist;
//	}
//
//	private void checkPowerDate(IcbalanceVO vo) {
//		String pk_corp = getLogincorppk();
//		Set<String> powercorpSet = userService.querypowercorpSet(getLoginUserid());
//		if (!powercorpSet.contains(pk_corp)) {
//			throw new BusinessException("无权操作！");
//		}
//
//		// 开始日期应该在启用库存日期前
//		CorpVO currcorp = CorpCache.getInstance().get("", pk_corp);
//		DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getIcbegindate()));
//		if (begdate.after(new DZFDate(vo.getDbilldate()))) {
//			throw new BusinessException("截止日期不能在启用库存日期(" + DateUtils.getPeriod(begdate) + ")前!");
//		}
//	}
//
//	// 将查询后的结果分页
//	private IcbalanceVO[] getPageVOs(IcbalanceVO[] pageVos, int page, int rows) {
//		int beginIndex = rows * (page - 1);
//		int endIndex = rows * page;
//		if (endIndex >= pageVos.length) {// 防止endIndex数组越界
//			endIndex = pageVos.length;
//		}
//		pageVos = Arrays.copyOfRange(pageVos, beginIndex, endIndex);
//		return pageVos;
//	}
//
//	private QueryParamVO getQueryParamVO() {
//		QueryParamVO paramvo = new QueryParamVO();
//		paramvo = (QueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//
//		if (StringUtil.isEmptyWithTrim(paramvo.getPk_corp())) {
//			paramvo.setPk_corp(getLogincorppk());// 设置默认公司PK
//		}
//
//		return paramvo;
//	}
//
//	/**
//	 * 打印操作
//	 */
//	public void printAction() {
//		try {
//			// String strlist = getRequest().getParameter("list");
//			// String type = getRequest().getParameter("type");
//			// String pageOrt=getRequest().getParameter("pageOrt");
//			// String left =getRequest().getParameter("left");
//			// String top =getRequest().getParameter("top");
//			// String printdate=getRequest().getParameter("printdate");
//			// String font=getRequest().getParameter("font");
//			// String pageNum=getRequest().getParameter("pageNum");
//			PrintParamVO printParamVO = (PrintParamVO) DzfTypeUtils.cast(getRequest(), new PrintParamVO());
//			Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//			String type = printParamVO.getType();
//			pmap.put("type", type);
//			pmap.put("pageOrt", printParamVO.getType());
//			pmap.put("left", printParamVO.getLeft());
//			pmap.put("top", printParamVO.getTop());
//			pmap.put("printdate", printParamVO.getPrintdate());
//			pmap.put("font", printParamVO.getFont());
//			pmap.put("pageNum", printParamVO.getPageNum());
//
//			String curr_print = getRequest().getParameter("curr_print");
//			IcbalanceVO[] bodyvos = null;
//			DZFBoolean dboolean = new DZFBoolean(curr_print);
//			if (dboolean.booleanValue()) {// 是否当前显示页
//				String strlist = printParamVO.getList();
//				if (strlist == null) {
//					bodyvos = new IcbalanceVO[0];
//				} else {
//					JSONArray array = (JSONArray) JSON.parseArray(strlist);
//					Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IcbalanceVO());
//					bodyvos = DzfTypeUtils.cast(array, bodymapping, IcbalanceVO[].class,
//							JSONConvtoJAVA.getParserConfig());
//				}
//
//			} else {
//				String qryDate = data.getDbilldate();
//				String pk_invtory = data.getPk_inventory();
//				String xsyye = getRequest().getParameter("xsyye");
//				String pk_subjectname = data.getPk_subjectname();
//				String priceStr = parameterserv.queryParamterValueByCode(getLogincorppk(), IParameterConstants.DZF010);
//				int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//				List<IcbalanceVO> flist = queryList(qryDate, pk_invtory, xsyye, pk_subjectname, price);
//				bodyvos = flist.toArray(new IcbalanceVO[flist.size()]);
//				bodyvos[0].setGs(getLoginCorpInfo().getUnitname());
//				bodyvos[0].setDjrq(qryDate);
//			}
//
//			if (bodyvos == null || bodyvos.length == 0)
//				return;
//
//			Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存title
//			tmap.put("公司", bodyvos[0].getGs());
//			tmap.put("查询日期", bodyvos[0].getDjrq());
//			setTableHeadFount(new Font(getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
//
//			setDefaultValue(bodyvos, getLogincorppk());// 为后续设置精度赋值
//
//			printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存成本表",
//					new String[] { "inventorycode", "inventoryname", "invspec", "measurename", "inventorytype",
//							"pk_subjectname", "nnum", "nprice", "ncost" },
//					new String[] { "存货编码", "存货名称", "规格(型号)", "计量单位", "存货分类", "科目名称", "结存数量", "结存单价", "结存成本" },
//					new int[] { 2, 2, 2, 2, 2, 2, 2, 2, 2 }, 20, type, pmap, tmap);
//		} catch (DocumentException e) {
//			log.error("库存报表打印失败", e);
//		} catch (IOException e) {
//			log.error("库存报表打印失败", e);
//		}
//	}
//
//	private void setDefaultValue(IcbalanceVO[] bodyvos, String pk_corp) {
//		if (bodyvos != null && bodyvos.length > 0) {
//			for (IcbalanceVO vo : bodyvos) {
//				vo.setPk_corp(pk_corp);
//			}
//		}
//	}
//
//	/**
//	 * 导出excel
//	 */
//	public void excelReport() {
//		HttpServletRequest request = getRequest();
//
//		HttpServletResponse response = getResponse();
//		OutputStream toClient = null;
//
//		try {
//
//			response.reset();
//			Excelexport2003<IcbalanceVO> lxs = new Excelexport2003<IcbalanceVO>();
//
//			String pk_corp = getLogincorppk();
//			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
//			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
//			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
//			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//
//			String curr_excel = request.getParameter("curr_excel");
//			IcbalanceVO[] listVo = null;
//			DZFBoolean dboolean = new DZFBoolean(curr_excel);
//			if (dboolean.booleanValue()) {// 是否当前显示页
//				String strlist = request.getParameter("list");
//				JSONArray array = (JSONArray) JSON.parseArray(strlist);
//				Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IcbalanceVO());
//				listVo = DzfTypeUtils.cast(array, bodymapping, IcbalanceVO[].class, JSONConvtoJAVA.getParserConfig());
//			} else {
//				String qryDate = data.getDbilldate();
//				String pk_invtory = data.getPk_inventory();
//				String xsyye = getRequest().getParameter("xsyye");
//				String pk_subjectname = data.getPk_subjectname();
//
//				List<IcbalanceVO> flist = queryList(qryDate, pk_invtory, xsyye, pk_subjectname, price);
//				listVo = flist.toArray(new IcbalanceVO[flist.size()]);
//				listVo[0].setGs(getLoginCorpInfo().getUnitname());
//				listVo[0].setDjrq(qryDate);
//			}
//
//			if (listVo == null || listVo.length == 0)
//				return;
//
//			String gs = listVo[0].getGs();
//			String qj = listVo[0].getDjrq();
//
//			KccbExcelField field = new KccbExcelField(num, price);
//			field.setExpvos(listVo);
//			field.setQj(qj);
//			field.setCreator(getLoginUserInfo().getUser_name());
//			field.setCorpName(gs);
//			String fileName = field.getExcelport2003Name();
//			String formattedName = URLEncoder.encode(fileName, "UTF-8");
//			response.addHeader("Content-Disposition",
//					"attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
//			toClient = new BufferedOutputStream(response.getOutputStream());
//			lxs.exportExcel(field, toClient);
//			toClient.flush();
//			response.getOutputStream().flush();
//		} catch (IOException e) {
//			log.error("错误", e);
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
//	}
//
//}

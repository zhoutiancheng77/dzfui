//package com.dzf.zxkj.platform.controller.icbill;
//
//import java.io.BufferedOutputStream;
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.apache.log4j.Logger;
//import org.apache.struts2.convention.annotation.Action;
//import org.apache.struts2.convention.annotation.Namespace;
//import org.apache.struts2.convention.annotation.ParentPackage;
//import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONArray;
//import com.dzf.model.gl.gl_bdset.AuxiliaryAccountBVO;
//import com.dzf.model.gl.gl_kmreport.VoUtils;
//import com.dzf.model.gl.gl_pjgl.IFpStyleEnum;
//import com.dzf.model.ic.ic_trade.AggIcTradeVO;
//import com.dzf.model.ic.ic_trade.IcConst;
//import com.dzf.model.ic.ic_trade.IcExBusinessException;
//import com.dzf.model.ic.ic_trade.IntradeHVO;
//import com.dzf.model.ic.ic_trade.IntradeParamVO;
//import com.dzf.model.ic.ic_trade.IntradeoutVO;
//import com.dzf.model.pub.Grid;
//import com.dzf.model.pub.Json;
//import com.dzf.model.pub.PrintParamVO;
//import com.dzf.model.pub.ic.IICConstants;
//import com.dzf.model.sys.sys_power.CorpVO;
//import com.dzf.pub.BusinessException;
//import com.dzf.pub.DZFWarpException;
//import com.dzf.pub.DzfTypeUtils;
//import com.dzf.pub.ISysConstants;
//import com.dzf.pub.StringUtil;
//import com.dzf.pub.SuperVO;
//import com.dzf.pub.Field.FieldMapping;
//import com.dzf.pub.cache.CorpCache;
//import com.dzf.pub.constant.AuxiliaryConstant;
//import com.dzf.pub.lang.DZFBoolean;
//import com.dzf.pub.lang.DZFDate;
//import com.dzf.pub.lang.DZFDouble;
//import com.dzf.pub.param.IParameterConstants;
//import com.dzf.pub.util.DZFValueCheck;
//import com.dzf.pub.util.DZfcommonTools;
//import com.dzf.pub.util.DateUtils;
//import com.dzf.pub.util.JSONConvtoJAVA;
//import com.dzf.pub.util.SafeCompute;
//import com.dzf.service.gl.gl_bdset.IAuxiliaryAccountService;
//import com.dzf.service.gl.gl_pjgl.IBankStatementService;
//import com.dzf.service.ic.ic_pub.ISecurityService;
//import com.dzf.service.ic.ic_trade.ISaleoutService;
//import com.dzf.service.pub.LogRecordEnum;
//import com.dzf.service.pub.report.PrintReportAction;
//import com.dzf.service.sys.sys_set.IParameterSetService;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Font;
//
///**
// * 出库出库
// *
// */
//
//@ParentPackage("basePackage")
//@Namespace("/ic")
//@Action(value = "ic_saleoutact")
//public class SaleoutController extends PrintReportAction<IntradeHVO> {
//
//	/**
//	 *
//	 */
//	private static final long serialVersionUID = 1L;
//
//	private Logger log = Logger.getLogger(this.getClass());
//
//	@Autowired
//	ISaleoutService ic_saleoutserv;
//	@Autowired
//	private IParameterSetService parameterserv;
//	@Autowired
//	private ISecurityService securityserv;
//	@Autowired
//	private IBankStatementService gl_yhdzdserv;
//
//	@Autowired
//	private IAuxiliaryAccountService gl_fzhsserv;
//
//	public void query() {
//		Grid grid = new Grid();
//		try {
//			int page = data.getPage();
//			int rows = data.getRows();
//			IntradeParamVO paramvo = getQueryParamVO();
//			if (paramvo != null) {
//				List<IntradeHVO> list = ic_saleoutserv.query(paramvo);
//				if (list != null && list.size() > 0) {
//					grid.setTotal((long) list.size());
//					// grid.setRows(list);
//					IntradeHVO[] PzglPagevos = getPagedZZVOs(list.toArray(new IntradeHVO[list.size()]), page, rows);
//					grid.setRows(Arrays.asList(PzglPagevos));
//				} else {
//					grid.setTotal((long) 0);
//				}
//				grid.setSuccess(true);
//				grid.setMsg("查询成功！");
//			}
//		} catch (Exception e) {
//			printErrorLog(grid, log, e, "查询失败");
//		}
//		writeJson(grid);
//	}
//
//	// 将查询后的结果分页
//	private IntradeHVO[] getPagedZZVOs(IntradeHVO[] PzglPagevos, int page, int rows) {
//		int beginIndex = rows * (page - 1);
//		int endIndex = rows * page;
//		if (endIndex >= PzglPagevos.length) {// 防止endIndex数组越界
//			endIndex = PzglPagevos.length;
//		}
//		PzglPagevos = Arrays.copyOfRange(PzglPagevos, beginIndex, endIndex);
//		return PzglPagevos;
//	}
//
//	private IntradeParamVO getQueryParamVO() {
//		String head = getRequest().getParameter("para");
//		JSON headjs = (JSON) JSON.parse(head);
//		Map<String, String> headmaping = FieldMapping.getFieldMapping(new IntradeParamVO());
//		IntradeParamVO paramvo = DzfTypeUtils.cast(headjs, headmaping, IntradeParamVO.class,
//				JSONConvtoJAVA.getParserConfig());
//		paramvo.setPk_corp(getLogincorppk());
//		if ("serMon".equals(paramvo.getSerdate())) {
//			if (paramvo.getStartYear() != null && paramvo.getStartMonth() != null) {
//				paramvo.setBegindate(
//						DZFDate.getDate(paramvo.getStartYear() + "-" + paramvo.getStartMonth() + "-" + "01"));
//				paramvo.setEnddate(TrunLastDay(paramvo.getEndYear(), paramvo.getEndMonth()));
//			} else {
//				throw new BusinessException("查询出库单失败！" + "请填写正确的日期");
//			}
//		}
//		return paramvo;
//	}
//
//	// 根据年和月得到月份的最后日期
//	private DZFDate TrunLastDay(String selYear, String selMon) throws DZFWarpException {
//		String peroid = selYear + "-" + selMon;
//		return DateUtils.getPeriodEndDate(peroid);
//	}
//
//	/**
//	 *
//	 */
//	public void saveSale() {
//		// Grid grid = new Grid();
//		Json json = new Json();
//		IntradeHVO headvo = null;
//		boolean isadd = false;
//		try {
//			String head = getRequest().getParameter("head");
//			String body = getRequest().getParameter("body");
//			String repeat = getRequest().getParameter("repeat");
//			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
//			JSON headjson = (JSON) JSON.parse(head);
//			JSONArray bodyarray = (JSONArray) JSON.parseArray(body);
//
//			if (bodyarray.isEmpty()) {
//				throw new BusinessException("表体不允许为空!");
//			}
//
//			Map<String, String> headmaping = FieldMapping.getFieldMapping(new IntradeHVO());
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IntradeoutVO());
//
//			headvo = DzfTypeUtils.cast(headjson, headmaping, IntradeHVO.class, JSONConvtoJAVA.getParserConfig()); // 表头
//			IntradeoutVO[] bodyvos = DzfTypeUtils.cast(bodyarray, bodymapping, IntradeoutVO[].class,
//					JSONConvtoJAVA.getParserConfig()); // 表体
//			// 赋默认值
//			// setDefaultValue(headvo, bodyvos);
//			if (StringUtil.isEmpty(headvo.getPk_ictrade_h())) {
//				isadd = true;
//			}
//			securityserv.checkSecurityForSave(IntradeHVO.class, headvo.getPrimaryKey(), getLogincorppk(), getLogincorppk(), getLoginUserid());
//
//			headvo.setCreator(getLoginUserid());
//			headvo.setPk_corp(getLogincorppk());
//			headvo.setChildren(bodyvos);
//			if (DZFBoolean.valueOf(repeat).booleanValue()) {
//				ic_saleoutserv.saveSale(headvo, false, false);
//			} else {
//				ic_saleoutserv.saveSale(headvo, true, false);
//			}
//
//			json.setSuccess(true);
//			json.setStatus(200);
//			json.setMsg("保存成功");
//		} catch (IcExBusinessException ie) {
//			List<IntradeoutVO> errList = ie.getErrList();
//			json.setStatus(IICConstants.STATUS_RECONFM_CODE);
//			json.setMsg("库存量不足");
//			json.setRows(errList.toArray(new IntradeoutVO[0]));
//			writeJson(json);
//			return;
//		} catch (Exception e) {
//			if (IICConstants.EXE_CONFIRM_CODE.equals(e.getMessage())) {
//				json.setStatus(IICConstants.STATUS_RECONFM_CODE);
//			}
//			// printErrorLog(grid, log, e, "保存失败");
//			printErrorLog(json, log, e, "保存失败");
//		}
//		if (isadd) {
//			if (headvo != null && !StringUtil.isEmpty(headvo.getDbillid())) {
//				writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "新增出库单：" + headvo.getDbillid(),
//						ISysConstants.SYS_2);
//			}
//
//		} else {
//			if (headvo != null && !StringUtil.isEmpty(headvo.getDbillid())) {
//				writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "修改出库单：" + headvo.getDbillid(),
//						ISysConstants.SYS_2);
//			}
//		}
//		// writeJson(grid);
//		writeJson(json);
//	}
//
//	/**
//	 * 查询子表信息
//	 */
//	public void querySub() {
//		Grid grid = new Grid();
//		if (data != null) {
//			List<IntradeoutVO> list = ic_saleoutserv.querySub(data);
//			if (list != null && list.size() > 0) {
//				grid.setSuccess(true);
//				grid.setTotal((long) list.size());
//				grid.setRows(list);
//				grid.setMsg("查询成功！");
//			}
//		} else {
//			grid.setSuccess(false);
//			grid.setMsg("查询参数为空");
//		}
//		writeJson(grid);
//	}
//
//	/**
//	 * 删除
//	 */
//	public void delSale() {
//
//		Json json = new Json();
//		StringBuffer strb = new StringBuffer();
//		IntradeHVO[] bodyvos = null;
//		try {
//			String body = getRequest().getParameter("head"); //
//			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
//			JSONArray array = (JSONArray) JSON.parseArray(body);
//
//			if (array == null) {
//				throw new BusinessException("数据为空,删除失败!!");
//			}
//
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IntradeHVO());
//			bodyvos = DzfTypeUtils.cast(array, bodymapping, IntradeHVO[].class, JSONConvtoJAVA.getParserConfig());
//
//			if (bodyvos == null || bodyvos.length == 0) {
//				throw new BusinessException("数据为空,删除失败!!");
//			}
//			securityserv.checkSecurityForDelete(bodyvos[0].getPk_corp(), getLogincorppk(), getLoginUserid());
//			for (IntradeHVO head : bodyvos) {
//				try {
//					ic_saleoutserv.deleteSale(head, getLogincorppk());
//
//				} catch (Exception e) {
//					printErrorLog(json, log, e, "删除失败!");
//					strb.append("<p>出库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
//				}
//			}
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "删除失败!");
//			strb.append("删除失败");
//		}
//		if (strb.length() == 0) {
//			json.setSuccess(true);
//			json.setMsg("删除成功!");
//		} else {
//			json.setSuccess(false);
//			json.setMsg(strb.toString());
//		}
//		writeLogRecords(bodyvos, "删除出库单");
//		writeJson(json);
//	}
//
//	private void writeLogRecords(IntradeHVO[] bodyvos, String msg) {
//
//		if (bodyvos == null || bodyvos.length == 0)
//			return;
//		if (bodyvos.length == 1) {
//			writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), msg + ",单据号" + bodyvos[0].getDbillid() + ".",
//					ISysConstants.SYS_2);
//		} else {
//			writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "批量删除出库单，单据号" + bodyvos[0].getDbillid() + "等.",
//					ISysConstants.SYS_2);
//		}
//
//	}
//
//	/**
//	 * 转总账
//	 */
//	public void togl() {
//
//		Json json = new Json();
//		StringBuffer strb = new StringBuffer();
//		try {
//			String body = getRequest().getParameter("head"); //
//			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
//			JSONArray array = (JSONArray) JSON.parseArray(body);
//
//			if (array == null) {
//				throw new BusinessException("数据为空,转总账失败!");
//			}
//
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IntradeHVO());
//			IntradeHVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, IntradeHVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//
//			if (bodyvos == null || bodyvos.length == 0) {
//				throw new BusinessException("数据为空,转总账失败!");
//			}
//			securityserv.checkSecurityForSave(getLogincorppk(), getLogincorppk(), getLoginUserid());
//			String zy = "销售商品";
//			List<String> periodSet = new ArrayList<String>();
//			int flag = 0;
//			boolean iscbtz = true;
//			for (IntradeHVO head : bodyvos) {
//				try {
//					periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
//					ic_saleoutserv.saveToGL(head, getLoginCorpInfo(), getLoginUserid(), zy);
//					if (StringUtil.isEmpty(head.getCbusitype())
//							|| !IcConst.CBTZTYPE.equalsIgnoreCase(head.getCbusitype())) {
//						if (iscbtz) {
//							iscbtz = false;
//						}
//					}
//					flag++;
//				} catch (Exception e) {
//					printErrorLog(json, log, e, "转总账失败");
//					strb.append("<p>出库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
//				}
//			}
//			if (flag > 0) {
//				String msg = checkJzMsg(periodSet, getLogincorppk(), iscbtz);
//				if (!StringUtil.isEmpty(msg)) {
//					strb.append(msg);
//				}
//			}
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "转总账失败");
//			strb.append(json.getMsg());
//		}
//		if (strb.length() == 0) {
//			json.setSuccess(true);
//			json.setMsg("转总账成功!");
//		} else {
//			json.setSuccess(false);
//			json.setMsg(strb.toString());
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "出库单转总账", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	/**
//	 * 转总账
//	 */
//	public void toTotalGL() {
//
//		Json json = new Json();
//		StringBuffer strb = new StringBuffer();
//		try {
//			String body = getRequest().getParameter("head"); //
//			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
//			JSONArray array = (JSONArray) JSON.parseArray(body);
//
//			if (array == null) {
//				throw new BusinessException("数据为空,转总账失败!");
//			}
//
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IntradeHVO());
//			IntradeHVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, IntradeHVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//
//			if (bodyvos == null || bodyvos.length == 0) {
//				throw new BusinessException("数据为空,转总账失败!");
//			}
//			String zy = "销售商品";
//			securityserv.checkSecurityForSave( getLogincorppk(), getLogincorppk(), getLoginUserid());
//			Map<String, List<IntradeHVO>> map = new LinkedHashMap<String, List<IntradeHVO>>();
//			List<IntradeHVO> list = null;
//			List<IntradeHVO> zlist = null;
//			List<String> periodSet = new ArrayList<String>();
//			int flag = 0;
//			boolean iscbtz = true;
//			for (IntradeHVO head : bodyvos) {
//				if (head.getIsjz() != null && head.getIsjz().booleanValue()) { // 转总账
//					printErrorLog(json, log, null, "已经转总账!");
//					strb.append("<p>出库单[" + head.getDbillid() + "],已经转总账,不允许操作!</p>");
//				} else {
//
//					StringBuffer keys = new StringBuffer();
//					if (head.getDbilldate() == null) {
//						keys.append(" ");
//					} else {
//						keys.append(head.getDbilldate().toString().substring(0, 7));
//					}
//					keys.append(head.getPk_cust());
//
//					if (head.getCbusitype() == null || IcConst.XSTYPE.equalsIgnoreCase(head.getCbusitype())) {
//						keys.append(IcConst.XSTYPE);
//					} else {
//						keys.append(head.getCbusitype());
//					}
//					CorpVO corp = CorpCache.getInstance().get(null, head.getPk_corp());
//					boolean isChargedept = !StringUtil.isEmpty(corp.getChargedeptname())
//							&& corp.getChargedeptname().equals("一般纳税人") ? true : false;
//					if (head.getFp_style() == null) {
//						if (isChargedept) {
//							keys.append(IFpStyleEnum.SPECINVOICE.getValue());
//						} else {
//							keys.append(IFpStyleEnum.COMMINVOICE.getValue());
//						}
//
//					} else {
//						keys.append(head.getFp_style());
//					}
//
//					if (map.containsKey(keys.toString())) {
//						list = map.get(keys.toString());
//					} else {
//						list = new ArrayList<>();
//					}
//					list.add(head);
//					map.put(keys.toString(), list);
//
//				}
//			}
//
//			try {
//				for (Map.Entry<String, List<IntradeHVO>> entry : map.entrySet()) {
//					zlist = entry.getValue();
//					if (zlist != null && zlist.size() > 0) {
//						for (IntradeHVO head : zlist) {
//							periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
//							if (StringUtil.isEmpty(head.getCbusitype())
//									|| !IcConst.CBTZTYPE.equalsIgnoreCase(head.getCbusitype())) {
//								if (iscbtz) {
//									iscbtz = false;
//								}
//							}
//						}
//						ic_saleoutserv.saveToGL(zlist.toArray(new IntradeHVO[zlist.size()]), getLogincorppk(),
//								getLoginUserid(), zy);
//						flag++;
//					}
//				}
//			} catch (Exception e) {
//				printErrorLog(json, log, e, "转总账失败");
//				if (zlist != null && zlist.size() > 0) {
//					for (IntradeHVO vo : zlist) {
//						strb.append("<p>出库单[" + vo.getDbillid() + "]," + json.getMsg() + "</p>");
//					}
//				}
//			}
//			if (flag > 0) {
//				String msg = checkJzMsg(periodSet, getLogincorppk(), iscbtz);
//				if (!StringUtil.isEmpty(msg)) {
//					strb.append(msg);
//				}
//			}
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "转总账失败");
//			strb.append(json.getMsg());
//		}
//		if (strb.length() == 0) {
//			json.setSuccess(true);
//			json.setMsg("转总账成功!");
//		} else {
//			json.setSuccess(false);
//			json.setMsg(strb.toString());
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "出库单汇总转总账", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	/**
//	 * 出库红字冲回
//	 */
//	public void dashBack() {
//		Grid grid = new Grid();
//		StringBuffer strb = new StringBuffer();
//		try {
//			if (data != null) {
//
//				DZFDate dbdate = data.getDbilldate();
//				if (dbdate != null) {
//					DZFDate lgdate = DateUtils.getPeriodStartDate(getLoginDate().substring(0, 7));
//					if (dbdate.after(lgdate)) {
//						throw new BusinessException("单据日期不晚于登录期间，请确认!");
//					}
//				}
//				securityserv.checkSecurityForSave(getLogincorppk(), getLogincorppk(), getLoginUserid());
//				ic_saleoutserv.saveDashBack(data, getLoginCorpInfo(), getLoginUserid(), getLoginDate());
//				List<String> periodSet = new ArrayList<String>();
//				periodSet.add(getLoginDate().substring(0, 7));
//				String msg = checkJzMsg(periodSet, getLogincorppk(), false);
//				if (!StringUtil.isEmpty(msg)) {
//					strb.append(msg);
//				}
//			} else {
//				strb.append("出库红字冲回参数为空");
//			}
//		} catch (Exception e) {
//			printErrorLog(grid, log, e, "出库红字冲回失败");
//			if (IICConstants.EXE_CONFIRM_CODE.equals(e.getMessage())) {
//				strb.append("出库红字冲回失败");
//			} else {
//				strb.append(e.getMessage());
//			}
//		}
//
//		if (strb.length() == 0) {
//			grid.setSuccess(true);
//			grid.setMsg("出库红字冲回成功");
//		} else {
//			grid.setSuccess(false);
//			grid.setMsg(strb.toString());
//		}
//		writeJson(grid);
//	}
//
//	/**
//	 * 获取单据编号
//	 */
//	public void getBillno() {
//		Json json = new Json();
//		try {
//			if (data == null) {
//				throw new BusinessException("数据为空,获取单据失败!");
//			}
//			if (data.getDbilldate() == null) {
//				throw new BusinessException("数据为空,获取单据失败!");
//			}
//			String code = ic_saleoutserv.getNewBillNo(getLogincorppk(), data.getDbilldate(), data.getCbusitype());
//			json.setData(code);
//			json.setSuccess(true);
//			json.setMsg("获取单据成功!");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "获取单据号失败");
//		}
//		writeJson(json);
//	}
//
//	/**
//	 * 取消转总账
//	 */
//	public void rollbackTogl() {
//
//		Json json = new Json();
//		StringBuffer strb = new StringBuffer();
//		try {
//			String body = getRequest().getParameter("head"); //
//			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
//			JSONArray array = (JSONArray) JSON.parseArray(body);
//
//			if (array == null) {
//				throw new BusinessException("数据为空,取消转总账失败!");
//			}
//
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IntradeHVO());
//			IntradeHVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, IntradeHVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//
//			if (bodyvos == null || bodyvos.length == 0) {
//				throw new BusinessException("数据为空,取消转总账失败!");
//			}
//			securityserv.checkSecurityForSave( getLogincorppk(), getLogincorppk(), getLoginUserid());
//			Map<String, DZFBoolean> map = new HashMap<>();
//			List<String> periodSet = new ArrayList<String>();
//			int flag = 0;
//			boolean iscbtz = true;
//			for (IntradeHVO head : bodyvos) {
//				try {
//					periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
//					if (!map.containsKey(head.getPzid())) {
//						if (StringUtil.isEmpty(head.getCbusitype())
//								|| !IcConst.CBTZTYPE.equalsIgnoreCase(head.getCbusitype())) {
//							if (iscbtz) {
//								iscbtz = false;
//							}
//						}
//						ic_saleoutserv.rollbackTogl(head, getLogincorppk());
//						map.put(head.getPzid(), DZFBoolean.TRUE);
//					}
//					flag++;
//				} catch (Exception e) {
//					printErrorLog(json, log, e, "取消转总账失败");
//					strb.append("<p>出库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
//				}
//			}
//			if (flag > 0) {
//				String msg = checkJzMsg(periodSet, getLogincorppk(), iscbtz);
//				if (!StringUtil.isEmpty(msg)) {
//					strb.append(msg);
//				}
//			}
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "取消转总账失败");
//			strb.append(json.getMsg());
//		}
//		if (strb.length() == 0) {
//			json.setSuccess(true);
//			json.setMsg("取消转总账成功!");
//		} else {
//			json.setSuccess(false);
//			json.setMsg(strb.toString());
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "出库单取消转总账", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void queryBYH() {
//		Grid grid = new Grid();
//		try {
//			if (!StringUtil.isEmptyWithTrim(data.getPk_ictrade_h())) {
//				IntradeHVO vo = ic_saleoutserv.queryIntradeHVOByID(data.getPk_ictrade_h(), getLogincorppk());
//				if (vo != null) {
//					grid.setTotal(1L);
//					// grid.setRows(list);
//					List<IntradeHVO> list = new ArrayList<IntradeHVO>();
//					list.add(vo);
//					grid.setRows(list);
//				} else {
//					grid.setTotal((long) 0);
//				}
//				grid.setSuccess(true);
//				grid.setMsg("查询成功！");
//			}
//		} catch (Exception e) {
//			printErrorLog(grid, log, e, "查询失败");
//		}
//		writeJson(grid);
//	}
//
//	/**
//	 * 打印操作
//	 */
//	public void printAction() {
//		try {
//			PrintParamVO printParamVO = (PrintParamVO) DzfTypeUtils.cast(getRequest(), new PrintParamVO());
//			Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//
//			if (printParamVO == null)
//				return;
//			pmap.put("type", printParamVO.getType());
//			pmap.put("pageOrt", printParamVO.getPageOrt());
//			pmap.put("left", printParamVO.getLeft());
//			pmap.put("top", printParamVO.getTop());
//			pmap.put("printdate", printParamVO.getPrintdate());
//			pmap.put("font", printParamVO.getFont());
//			pmap.put("pageNum", printParamVO.getPageNum());
//			if (StringUtil.isEmpty(printParamVO.getList())) {
//				return;
//			}
//			if (printParamVO.getPageOrt().equals("Y")) {
//				setIscross(DZFBoolean.TRUE);// 是否横向
//			} else {
//				setIscross(DZFBoolean.FALSE);// 是否横向
//			}
//
//			String list = printParamVO.getList();
//			String[] strs = list.split(",");
//			String priceStr = parameterserv.queryParamterValueByCode(getLogincorppk(), IParameterConstants.DZF010);
//			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//			Map<String, List<SuperVO>> vomap = new LinkedHashMap<>();
//
//			AuxiliaryAccountBVO[] fzvos =gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_CUSTOMER, getLogincorppk(), null);
//			Map<String, AuxiliaryAccountBVO> aumap =DZfcommonTools.hashlizeObjectByPk(Arrays.asList(fzvos), new String[]{"pk_auacount_b"});
//			for (String id : strs) {
//
//				if (StringUtil.isEmpty(id))
//					continue;
//				IntradeHVO head = ic_saleoutserv.queryIntradeHVOByID(id, getLogincorppk());
//				if (head == null)
//					continue;
//				AuxiliaryAccountBVO custvo = 	aumap.get(head.getPk_cust());
//				SuperVO[] bodyvos = head.getChildren();
//				List<SuperVO> alist = new ArrayList<>();
//				for (SuperVO body : bodyvos) {
//					IntradeoutVO ivo = (IntradeoutVO) body;
//
//					if(DZFValueCheck.isNotEmpty(custvo)){
//						ivo.setCustname(custvo.getName());
//					}
//					if (StringUtil.isEmpty(ivo.getPk_voucher())) {
//						ivo.setPk_voucher(head.getPzid());
//					}
//					if (StringUtil.isEmpty(ivo.getPzh())) {
//						ivo.setPzh(head.getPzh());
//					}
//					ivo.setCreator(head.getCreator());
//					ivo.setDbillid(head.getDbillid());
//					String cbusitype = ivo.getCbusitype();
//					if (StringUtil.isEmpty(cbusitype)) {
//						ivo.setCbusitype("销售出库");
//					} else {
//						if (cbusitype.equalsIgnoreCase(IcConst.LLTYPE)) {
//							ivo.setCbusitype("领料出库");
//						} else if (cbusitype.equalsIgnoreCase(IcConst.QTCTYPE)) {
//							ivo.setCbusitype("其他出库");
//						} else {
//							ivo.setCbusitype("销售出库");
//						}
//					}
//					if (ivo.getNcost() != null) {
//						ivo.setNprice(SafeCompute.div(ivo.getNcost(), ivo.getNnum()).setScale(price, 0));// 设置成本单价
//					}
//					alist.add(ivo);
//				}
//				IntradeoutVO nvo = calTotal(bodyvos);
//				alist.add(nvo);
//				vomap.put(id, Arrays.asList(alist.toArray(new SuperVO[alist.size()])));
//			}
//			String type = printParamVO.getType();
//			Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
//			setTableHeadFount(new Font(getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
//
//			String title = "出 库 单";
//			String[] columns = new String[] { "cbusitype", "invclassname", "invname", "invspec", "measure",
//					"nnum", "nprice", "ncost" };
//			String[] columnnames = new String[] { "出库类型", "存货分类", "存货名称", "规格(型号)", "计量单位", "数量", "成本单价", "成本金额" };
//			int[] widths = new int[] { 1, 1, 4, 2, 1, 2, 2, 2 };
//
//			boolean isCombin = false;
//			if (printParamVO != null && !StringUtil.isEmpty(printParamVO.getIsmerge())
//					&& printParamVO.getIsmerge().equals("Y")) {
//				isCombin = true;
//			}
//			setLineheight(22f);
//			Map<String,String> invmaps = new HashMap<>();
//			invmaps.put("isHiddenPzh",printParamVO.getIshidepzh());
//			if(pmap.get("type").equals("3")){//发票纸模板打印
//				printICInvoice(vomap, null, title, columns, columnnames, widths, 20,invmaps, pmap, tmap);
//			}else{
//				if (!isCombin) {
//					printHz(vomap, null, title, columns, columnnames, widths, 20, type, invmaps,pmap, tmap);
//				} else {
//					if (pmap.get("type").equals("1"))
//						printGroupCombin(vomap, title, columns, columnnames, null, widths, 20, pmap,invmaps); // A4纸张打印
//					else if (pmap.get("type").equals("2"))
//						printB5Combin(vomap, title, columns, columnnames, null, widths, 20, pmap,invmaps);
//				}
//			}
//
//		} catch (DocumentException e) {
//			log.error("出库单打印失败", e);
//		} catch (IOException e) {
//			log.error("出库单打印失败", e);
//		} catch (Exception e) {
//			log.error("出库单打印失败", e);
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "打印出库单", ISysConstants.SYS_2);
//	}
//
//	private IntradeoutVO calTotal(SuperVO[] bodyvos) {
//		// 计算合计行数据
//		DZFDouble d1 = DZFDouble.ZERO_DBL;
//		DZFDouble d2 = DZFDouble.ZERO_DBL;
//		for (SuperVO body : bodyvos) {
//			IntradeoutVO ivo = (IntradeoutVO) body;
//			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(ivo.getNcost()).setScale(2, DZFDouble.ROUND_HALF_UP));
//			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(ivo.getNnum()));
//		}
//		IntradeoutVO nvo = new IntradeoutVO();
//		nvo.setKmmc(((IntradeoutVO) bodyvos[0]).getKmmc());
//		nvo.setCbusitype("合计");
//		nvo.setNcost(d1);
//		nvo.setNnum(d2);
//		return nvo;
//	}
//
//	public void expExcel() {
//
//		HttpServletResponse response = getResponse();
//		OutputStream toClient = null;
//
//		try {
//
//			PrintParamVO printParamVO = (PrintParamVO) DzfTypeUtils.cast(getRequest(), new PrintParamVO());
//			if (StringUtil.isEmpty(printParamVO.getList())) {
//				return;
//			}
//
//			String list = printParamVO.getList();
//			AggIcTradeVO[] aggvos = null;
//			String exName = null;
//			boolean isexp = false;
//			if (list.contains("download")) {
//				exName = new String("出库单导入模板.xls");
//				aggvos = new AggIcTradeVO[1];
//				AggIcTradeVO aggvo = new AggIcTradeVO();
//				aggvo.setVcorpname(getLoginCorpInfo().getUnitname());
//				DZFDate billdate = new DZFDate(getLoginDate());
//				aggvo.setDbilldate(billdate.toString());
//				aggvo.setDbillid(ic_saleoutserv.getNewBillNo(getLogincorppk(), billdate, null));
//				aggvos[0] = aggvo;
//				isexp = true;
//			} else {
//				String where = list.substring(2, list.length() - 1);
//				aggvos = ic_saleoutserv.queryAggIntradeVOByID(where, getLogincorppk());
//				exName = new String("出库单.xls");
//				List<AggIcTradeVO> tlist = calTotalRow(aggvos);
//				aggvos = tlist.toArray(new AggIcTradeVO[tlist.size()]);
//			}
//
//			Map<String, Integer> preMap = getPreMap();// 设置精度
//
//			response.reset();
//			exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
//			response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
//			toClient = new BufferedOutputStream(response.getOutputStream());
//			response.setContentType("application/vnd.ms-excel;charset=gb2312");
//			byte[] length = null;
//			Map<String, Integer> tabidsheetmap = new HashMap<String, Integer>();
//			tabidsheetmap.put("B100000", 0);
//			IcBillExport exp = new IcBillExport();
//			length = exp.exportExcel(aggvos, toClient, 0, isexp, preMap);
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
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "导出出库单", ISysConstants.SYS_2);
//	}
//
//	private List<AggIcTradeVO> calTotalRow(AggIcTradeVO[] aggvos) {
//		List<AggIcTradeVO> tlist = new ArrayList<>();
//		if (aggvos != null && aggvos.length > 0) {
//			Map<String, List<AggIcTradeVO>> map = hashlizeObjectByPk(aggvos);
//			for (List<AggIcTradeVO> list : map.values()) {
//				if (list == null || list.size() == 0)
//					continue;
//				AggIcTradeVO nvo = calTotal(list);
//				for (AggIcTradeVO aggvo : list) {
//					tlist.add(aggvo);
//				}
//				tlist.add(nvo);
//			}
//		}
//		return tlist;
//	}
//
//	private Map<String, List<AggIcTradeVO>> hashlizeObjectByPk(AggIcTradeVO[] aggvos) throws BusinessException {
//		Map<String, List<AggIcTradeVO>> result = new LinkedHashMap<>();
//		if (aggvos != null && aggvos.length > 0) {
//			for (int i = 0; i < aggvos.length; i++) {
//				if (result.containsKey(aggvos[i].getPk_ictrade_h())) {
//					((List<AggIcTradeVO>) result.get(aggvos[i].getPk_ictrade_h())).add(aggvos[i]);
//				} else {
//					List<AggIcTradeVO> list = new ArrayList<>();
//					list.add(aggvos[i]);
//					result.put(aggvos[i].getPk_ictrade_h(), list);
//				}
//			}
//		}
//		return result;
//	}
//
//	private AggIcTradeVO calTotal(List<AggIcTradeVO> list) {
//		// 计算合计行数据
//		DZFDouble d1 = DZFDouble.ZERO_DBL;
//		DZFDouble d2 = DZFDouble.ZERO_DBL;
//		DZFDouble d3 = DZFDouble.ZERO_DBL;
//		DZFDouble d4 = DZFDouble.ZERO_DBL;
//		DZFDouble d5 = DZFDouble.ZERO_DBL;
//		for (AggIcTradeVO body : list) {
//			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(body.getNymny()).setScale(2, DZFDouble.ROUND_HALF_UP));
//			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(body.getNnum()));
//			d3 = SafeCompute.add(d3, VoUtils.getDZFDouble(body.getNtaxmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
//			d4 = SafeCompute.add(d4, VoUtils.getDZFDouble(body.getNcost()).setScale(2, DZFDouble.ROUND_HALF_UP));
//			d5 = SafeCompute.add(d5, VoUtils.getDZFDouble(body.getNtotaltaxmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
//		}
//		AggIcTradeVO nvo = new AggIcTradeVO();
//		nvo.setDbilldate("合计");
//		nvo.setNymny(d1);
//		nvo.setNnum(d2);
//		nvo.setNtaxmny(d3);
//		nvo.setNcost(d4);
//		nvo.setNtotaltaxmny(d5);
//		return nvo;
//	}
//
//	private Map<String, Integer> getPreMap() {
//		String pk_corp = getLogincorppk();
//		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
//		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
//		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
//		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//		Map<String, Integer> preMap = new HashMap<String, Integer>();
//		preMap.put(IParameterConstants.DZF009, num);
//		preMap.put(IParameterConstants.DZF010, price);
//
//		return preMap;
//	}
//
//	public void impExcel() {
//		Json json = new Json();
//		json.setSuccess(false);
//		try {
//
//			File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("impfile");
//			if (infiles == null || infiles.length == 0) {
//				throw new BusinessException("请选择导入文件!");
//			}
//			String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames("impfile");
//			String fileType = null;
//			if (fileNames != null && fileNames.length > 0) {
//				String fileName = fileNames[0];
//				fileType = fileNames[0].substring(fileName.lastIndexOf(".") + 1, fileName.length());
//			}
//			String pk_corp = getLogincorppk();
//			securityserv.checkSecurityForSave(getLogincorppk(), getLogincorppk(), getLoginUserid());
//			String msg = ic_saleoutserv.saveImp(infiles[0], pk_corp, fileType, getLoginUserid());
//			if (StringUtil.isEmpty(msg)) {
//				json.setMsg("出库单导入成功!");
//				json.setSuccess(true);
//			} else {
//				json.setMsg(msg);
//				json.setSuccess(false);
//			}
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "导入失败!");
//		}
//		writeJson(json);
//	}
//
//	private String checkJzMsg(List<String> periodSet, String pk_corp, boolean iscbtz) {
//
//		StringBuffer headMsg = null;
//		if (iscbtz) {
//			headMsg = ic_saleoutserv.buildQmjzMsg(periodSet, pk_corp);
//		} else {
//			headMsg = gl_yhdzdserv.buildQmjzMsg(periodSet, pk_corp);
//		}
//		if (headMsg != null && headMsg.length() > 0) {
//			return headMsg.toString();
//		}
//		return null;
//	}
//}

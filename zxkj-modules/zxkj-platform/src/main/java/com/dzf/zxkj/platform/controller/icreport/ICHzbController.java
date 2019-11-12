package com.dzf.zxkj.platform.controller.icreport;

import com.dzf.zxkj.base.utils.DZFNumberUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DZFMapUtil;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.excel.IcHzbExcelField;
import com.dzf.zxkj.platform.model.report.IcDetailVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icreport.IICHzb;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * 库存汇总表
 *
 * @author zhw
 *
 */
@RestController
@RequestMapping("/icreport/rep_hzbact")
@Slf4j
public class ICHzbController {

	@Autowired
	private IICHzb ic_rep_hzbserv;
	@Autowired
	private IUserService userService;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ICorpService corpService;

	// 查询
	@GetMapping("/query")
	public ReturnData query(@RequestParam Map<String, String> param) {
		ReportDataGrid grid = new ReportDataGrid();
		QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
		queryParamvo = getQueryParamVO(queryParamvo);

		int page = queryParamvo == null ? 1 : queryParamvo.getPage();
		int rows = queryParamvo == null ? 100000 : queryParamvo.getRows();

		checkPowerDate(queryParamvo);
		Map<String, IcDetailVO> result = null;

		result = ic_rep_hzbserv.queryDetail(queryParamvo, SystemUtil.getLoginCorpVo());

		result = filter(result, queryParamvo);

		// 将查询后的数据分页展示
		List<IcDetailVO> list = getPagedMXZVos(result, page, rows, grid);

		grid.setRows(list);
		grid.setSuccess(true);
		grid.setMsg("查询成功！");
		return ReturnData.ok().data(grid);
	}

	private String getInvKey(IcDetailVO o1) {
		String key = o1.getSpbm() + "," + o1.getSpmc() + "," + o1.getSpxh() + "," + o1.getSpgg() + "," + o1.getJldw()
				+ "," + o1.getPk_inventory();
		return key;

	}

	private Map<String, IcDetailVO> filter(Map<String, IcDetailVO> result, QueryParamVO paramvo) {
		Map<String, IcDetailVO> newresult = null;
		if (DZFMapUtil.isEmpty(result) || DZFValueCheck.isEmpty(paramvo.getIshowfs())
				|| paramvo.getIshowfs().booleanValue()) {
			newresult = result;
		} else {
			newresult = new HashMap<>();
			List<String> qckeylist = new ArrayList<>();
			List<String> hjkeylist = new ArrayList<>();
			for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
				IcDetailVO vo = entry.getValue();
				if (vo == null)
					continue;
				if (DZFNumberUtil.isNotNullAndNotZero(vo.getSrje()) || DZFNumberUtil.isNotNullAndNotZero(vo.getSrsl())
						|| DZFNumberUtil.isNotNullAndNotZero(vo.getFcje())
						|| DZFNumberUtil.isNotNullAndNotZero(vo.getFcsl())) {

					newresult.put(entry.getKey(), vo);
					String key = vo.getPk_accsubj() + "," + vo.getPk_sp();
					if (!qckeylist.contains(key))
						qckeylist.add(key);
					key = vo.getPk_accsubj() + "," + vo.getPk_sp() + "," + DateUtils.getPeriod(vo.getDbilldate())
							+ ",bj";
					if (!hjkeylist.contains(key))
						hjkeylist.add(key);
				}
			}

			for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {

				if (qckeylist.contains(entry.getKey())) {
					newresult.put(entry.getKey(), entry.getValue());
				}

				if (hjkeylist.contains(entry.getKey())) {
					newresult.put(entry.getKey(), entry.getValue());
				}
			}
		}

		return newresult;
	}

	private List<IcDetailVO> getPagedMXZVos(Map<String, IcDetailVO> result, int page, int rows, ReportDataGrid grid) {

		List<IcDetailVO> spList = new ArrayList<IcDetailVO>();
		Set<Map.Entry<String, IcDetailVO>> entrySet = result.entrySet();
		Iterator<Map.Entry<String, IcDetailVO>> iter = entrySet.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, IcDetailVO> entry = iter.next();
			spList.add(entry.getValue());
		}

		List<IcDetailVO> resList = new ArrayList<IcDetailVO>();
		if (spList != null && spList.size() > 0) {
			int start = (page - 1) * rows;
			for (int i = start; i < page * rows && i < spList.size(); i++) {
				resList.add(spList.get(i));
			}
			grid.setTotal((long) spList.size());
		} else {
			spList = new ArrayList<IcDetailVO>();
			grid.setTotal(0L);
		}

		return resList;
	}

	private void checkPowerDate(QueryParamVO vo) {
		String pk_corp = SystemUtil.getLoginCorpId();
		Set<String> powercorpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
		if (!powercorpSet.contains(pk_corp)) {
			throw new BusinessException("无权操作！");
		}

		// 开始日期应该在启用库存日期前
		CorpVO currcorp = corpService.queryByPk(SystemUtil.getLoginCorpId());
		DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getIcbegindate()));
		if (begdate.after(vo.getBegindate1())) {
			throw new BusinessException("开始日期不能在启用库存日期(" + DateUtils.getPeriod(begdate) + ")前!");
		}
	}

	private QueryParamVO getQueryParamVO(QueryParamVO paramvo) {
		if (StringUtil.isEmptyWithTrim(paramvo.getPk_corp())) {
			paramvo.setPk_corp(SystemUtil.getLoginCorpId());// 设置默认公司PK
		}
		return paramvo;
	}

	public void printAction() {
//		try {
//			String strlist = getRequest().getParameter("list");
//			String type = getRequest().getParameter("type");
//			String pageOrt = getRequest().getParameter("pageOrt");
//			String left = getRequest().getParameter("left");
//			String top = getRequest().getParameter("top");
//			String printdate = getRequest().getParameter("printdate");
//			String font = getRequest().getParameter("font");
//			String pageNum = getRequest().getParameter("pageNum");
//			Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//			pmap.put("type", type);
//			pmap.put("pageOrt", pageOrt);
//			pmap.put("left", left);
//			pmap.put("top", top);
//			pmap.put("printdate", printdate);
//			pmap.put("font", font);
//			pmap.put("pageNum", pageNum);
//			if (strlist == null) {
//				return;
//			}
//			if (pageOrt.equals("Y")) {
//				setIscross(DZFBoolean.TRUE);// 是否横向
//			} else {
//				setIscross(DZFBoolean.FALSE);// 是否横向
//			}
//			JSONArray array = (JSONArray) JSON.parseArray(strlist);
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IcDetailVO());
//			IcDetailVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, IcDetailVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//
//			String gs = bodyvos[0].getGs();
//			String period = bodyvos[0].getTitlePeriod();
//			String current = getRequest().getParameter("print_curr");
//			DZFBoolean dboolean = new DZFBoolean(current);
//			if (!dboolean.booleanValue()) {
//				bodyvos = queryVos(getQueryParamVO());
//
//			}
//
//			Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
//			mxmap = reloadVOs(bodyvos, getQueryParamVO());
//
//			Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
//			tmap.put("公司", gs);
//			// tmap.put("存货名称", bodyvos[0].getSpmc());
//			tmap.put("期间", period);
//
//			String corp = (String) getRequest().getSession().getAttribute(IGlobalConstants.login_corp);
//			CorpVO corpvo = CorpCache.getInstance().get(null, corp);
//
//			boolean bisfenye = false;
//			String isfenye = getRequest().getParameter("isfenye");
//			if (!StringUtil.isEmpty(isfenye) && isfenye.equals("Y")) {
//				bisfenye = true;
//			}
//			// 老模式 启用库存
//			if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
//				String[] columnames = new String[] { "存货编码", "存货名称", "规格(型号)", "计量单位", "期初数量", "期初单价", "期初金额", "收入数量",
//						"收入单价", "收入金额", "发出数量", "发出单价", "发出金额", "结存数量", "结存单价", "结存金额" };
//				String[] columnkeys = new String[] { "spbm", "spmc", "spgg", "jldw", "qcsl", "qcdj", "qcje", "srsl",
//						"srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
//				int[] widths = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
//				setTableHeadFount(new Font(getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
//				printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存汇总表", columnkeys, columnames, widths, 60,
//						type, pmap, tmap);
//			} else {
//				String[] columnames = new String[] { "存货编码", "存货名称", "规格(型号)", "计量单位", "期初数量", "期初单价", "期初金额", "收入数量",
//						"收入单价", "收入金额", "发出数量", "发出单价", "发出金额", "结存数量", "结存单价", "结存金额" };
//				String[] columnkeys = new String[] { "spbm", "spmc", "spgg", "jldw", "qcsl", "qcdj", "qcje", "srsl",
//						"srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
//				int[] widths = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
//				setTableHeadFount(new Font(getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
//				printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存汇总表", columnkeys, columnames, widths, 60,
//						type, pmap, tmap);
//			}
//
//		} catch (DocumentException e) {
//			log.error("打印错误", e);
//		} catch (IOException e) {
//			log.error("打印错误", e);
//		}
	}

	private IcDetailVO[] queryVos(QueryParamVO queryParamVO) {
		Map<String, IcDetailVO> result = null;

		result = ic_rep_hzbserv.queryDetail(queryParamVO, SystemUtil.getLoginCorpVo());

		List<IcDetailVO> list = getPagedMXZVos(result, 1, Integer.MAX_VALUE, new ReportDataGrid());

		return list.stream().toArray(IcDetailVO[]::new);
	}

	private Map<String, List<SuperVO>> reloadVOs(IcDetailVO[] bodyvos, QueryParamVO paramvo) {
		if (bodyvos == null || bodyvos.length == 0) {
			return null;
		}
		//
		// Map<String, IcDetailVO> icMap = ic_rep_mxzserv.queryDetail(paramvo,
		// SystemUtil.getLoginCorpVo());
		// if (icMap == null) {
		// return null;
		// }
		// icMap = filter(icMap, paramvo);
		List<SuperVO> flist = null;
		String mxkey = null;
		Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
		for (IcDetailVO icv : bodyvos) {
			mxkey = icv.getSpbm() + " " + icv.getSpmc() + " " + icv.getSpxh() + " " + icv.getSpgg();
			icv.setPk_corp(paramvo.getPk_corp());// 后续设置精度使用
			if (mxmap.containsKey(mxkey)) {
				mxmap.get(mxkey).add(icv);// icv.getPk_sp()
			} else {
				flist = new ArrayList<SuperVO>();
				flist.add(icv);
				mxmap.put(mxkey, flist);//
			}
		}

		if (mxmap == null || mxmap.isEmpty()) {
			return null;
		}
		List<SuperVO> sortList = null;
		Map<String, List<SuperVO>> sortMap = new TreeMap<String, List<SuperVO>>(new Comparator<String>() {
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}
		});
		sortMap.putAll(mxmap);
		return sortMap;
	}

	private void setExprotInfo(IcHzbExcelField xsz, CorpVO corpvo) {
		// 老模式 启用库存
		if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
			xsz.setFields(xsz.getFields2());
		} else {
			xsz.setFields(xsz.getFields1());
		}

	}

	// 导出excel
	public void excelReport() {

//		HttpServletResponse response = getResponse();
//		OutputStream toClient = null;
//		try {
//			String strlist = getRequest().getParameter("list");
//			JSONArray array = (JSONArray) JSON.parseArray(strlist);
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new IcDetailVO());
//			IcDetailVO[] vo = DzfTypeUtils.cast(array, bodymapping, IcDetailVO[].class,
//					JSONConvtoJAVA.getParserConfig());
//			String gs = vo[0].getGs();
//			String qj = vo[0].getTitlePeriod();
//
//			String current = getRequest().getParameter("export_curr");
//			DZFBoolean dboolean = new DZFBoolean(current);
//			if (!dboolean.booleanValue()) {
//				vo = queryVos(getQueryParamVO());
//			}
//
//			// vo = reloadExcelData(getQueryParamVO());
//			Excelexport2003<IcDetailVO> lxs = new Excelexport2003<IcDetailVO>();
//
//			String pk_corp = SystemUtil.getLoginCorpId();
//			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
//			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
//			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
//			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
//			CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//			IcHzbExcelField xsz = new IcHzbExcelField(num, price);
//			setExprotInfo(xsz, corpvo);
//			xsz.setIcDetailVos(vo);
//			xsz.setQj(qj);
//			xsz.setCreator(getLoginUserInfo().getUser_name());
//			xsz.setCorpName(gs);
//			response.reset();
//			// String filename = xsz.getExcelport2007Name();
//			String filename = xsz.getExcelport2003Name();
//			String formattedName = URLEncoder.encode(filename, "UTF-8");
//			response.addHeader("Content-Disposition",
//					"attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
//			// response.addHeader("Content-Disposition",
//			// "attachment;filename="+new String(filename.getBytes("UTF-8"),
//			// "ISO8859-1"));
//			toClient = new BufferedOutputStream(response.getOutputStream());
//			response.setContentType("application/vnd.ms-excel;charset=gb2312");
//			lxs.exportExcel(xsz, toClient);
//			toClient.flush();
//			response.getOutputStream().flush();
//		} catch (IOException e) {
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
	}

}

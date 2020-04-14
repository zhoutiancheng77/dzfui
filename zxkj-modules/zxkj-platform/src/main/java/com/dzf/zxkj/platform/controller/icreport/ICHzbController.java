package com.dzf.zxkj.platform.controller.icreport;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFNumberUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DZFMapUtil;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.IcHzbExcelField;
import com.dzf.zxkj.platform.model.report.IcDetailVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.icreport.IICHzb;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
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
public class ICHzbController  extends BaseController {

	@Autowired
	private IICHzb ic_rep_hzbserv;
	@Autowired
	private IUserService userService;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ICorpService corpService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

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

//		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
		grid.setRows(list == null ? new ArrayList<IcDetailVO>() : list);
		grid.setSuccess(true);
		grid.setMsg("查询成功！");
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT,new StringBuffer().append("库存汇总表查询:").append(param.get("beginPeriod")).append("至").append(param.get("endPeriod")).toString(), ISysConstants.SYS_2);
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

		if (spList != null && spList.size() > 0) {
			Collections.sort(spList, new Comparator<IcDetailVO>() {
				@Override
				public int compare(IcDetailVO o1, IcDetailVO o2) {
					int i = o1.getSpbm().compareTo(o2.getSpbm());
					return i;
				}
			});
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
		if(currcorp.getIcbegindate() == null)
			throw new BusinessException("启用库存日期为空!");

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

    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap, HttpServletResponse response) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
            PrintParamVO printParamVO = JsonUtils.convertValue(pmap, PrintParamVO.class);//
            if (DZFValueCheck.isEmpty(pmap.get("list"))) {
                return;
            }
            if ("Y".equals(pmap.get("pageOrt"))) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }

			String strlist = printParamVO.getList().replace("}{", "},{");
            IcDetailVO[] bodyvos= JsonUtils.deserialize(strlist, IcDetailVO[].class);
			String gs = bodyvos[0].getGs();
			String period = bodyvos[0].getTitlePeriod();
			String current = pmap.get("print_curr");
            QueryParamVO queryParamvo = JsonUtils.convertValue(pmap, QueryParamVO.class);
			DZFBoolean dboolean = new DZFBoolean(current);
			if (!dboolean.booleanValue()) {
				bodyvos = queryVos(getQueryParamVO(queryParamvo));
			}
			if(bodyvos != null && bodyvos.length>0){
                for(IcDetailVO icDetailVO : bodyvos){
                    icDetailVO.setPk_corp(SystemUtil.getLoginCorpId());
                }
            }
			Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
			tmap.put("公司", gs);
			tmap.put("期间", period);
			printReporUtil.setLineheight(22f);
			CorpVO corpvo = SystemUtil.getLoginCorpVo();
			// 老模式 启用库存
			if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
				String[] columnames = new String[] { "存货编码", "存货名称", "规格(型号)", "计量单位", "期初数量", "期初单价", "期初金额", "收入数量",
						"收入单价", "收入金额", "发出数量", "发出单价", "发出金额", "结存数量", "结存单价", "结存金额" };
				String[] columnkeys = new String[] { "spbm", "spmc", "spgg", "jldw", "qcsl", "qcdj", "qcje", "srsl",
						"srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
				int[] widths = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
                printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存汇总表", columnkeys, columnames, widths, 60, pmap, tmap);
			} else {
				String[] columnames = new String[] { "存货编码", "存货名称", "规格(型号)", "计量单位", "期初数量", "期初单价", "期初金额", "收入数量",
						"收入单价", "收入金额", "发出数量", "发出单价", "发出金额", "结存数量", "结存单价", "结存金额" };
				String[] columnkeys = new String[] { "spbm", "spmc", "spgg", "jldw", "qcsl", "qcdj", "qcje", "srsl",
						"srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
				int[] widths = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
                printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存汇总表", columnkeys, columnames, widths, 60, pmap, tmap);
			}

		} catch (DocumentException e) {
			log.error("库存汇总表打印错误", e);
		} catch (IOException e) {
			log.error("库存汇总表打印错误", e);
		}catch (Exception e) {
            log.error("库存汇总表打印错误", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存汇总表打印错误", e);
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT,"库存汇总表打印", ISysConstants.SYS_2);
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

	/**
	 * 导出excel
	 */
	@PostMapping("/expExcel")
	public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
		OutputStream toClient = null;
		try {
			String strlist = param.get("list");
			IcDetailVO[] vo= JsonUtils.deserialize(strlist, IcDetailVO[].class);
			String gs = vo[0].getGs();
			String qj = vo[0].getTitlePeriod();

			String current =  param.get("export_curr");
			DZFBoolean dboolean = new DZFBoolean(current);
            QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
			if (!dboolean.booleanValue()) {
				vo = queryVos(getQueryParamVO(queryParamvo));
			}

			// vo = reloadExcelData(getQueryParamVO());
			Excelexport2003<IcDetailVO> lxs = new Excelexport2003<IcDetailVO>();

			String pk_corp = SystemUtil.getLoginCorpId();
			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
			CorpVO corpvo =corpService.queryByPk(pk_corp);
			IcHzbExcelField xsz = new IcHzbExcelField(num, price);
			setExprotInfo(xsz, corpvo);
			xsz.setIcDetailVos(vo);
			xsz.setQj(qj);
			xsz.setCreator(SystemUtil.getLoginUserVo().getUser_name());
			xsz.setCorpName(gs);
			response.reset();
			// String filename = xsz.getExcelport2007Name();
			String filename = xsz.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			// response.addHeader("Content-Disposition",
			// "attachment;filename="+new String(filename.getBytes("UTF-8"),
			// "ISO8859-1"));
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			lxs.exportExcel(xsz, toClient);
			toClient.flush();
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("库存汇总表excel导出错误", e);
		} catch (Exception e) {
            log.error("库存汇总表excel导出错误", e);
        } finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (IOException e) {
				log.error("库存汇总表excel导出错误", e);
			}
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("库存汇总表excel导出错误", e);
			}
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT,"库存汇总表导出", ISysConstants.SYS_2);
		}
	}

}

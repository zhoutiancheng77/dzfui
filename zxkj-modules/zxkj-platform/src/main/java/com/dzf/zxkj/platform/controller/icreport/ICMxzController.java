package com.dzf.zxkj.platform.controller.icreport;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFNumberUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DZFMapUtil;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.IcMxExcelField;
import com.dzf.zxkj.platform.model.report.IcDetailFzVO;
import com.dzf.zxkj.platform.model.report.IcDetailVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.icreport.IICMxz;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.ReportUtil;
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

import static com.dzf.zxkj.platform.util.SystemUtil.getRequest;


/**
 * 库存明细账
 *
 * @author wangzhn
 *
 */
@SuppressWarnings("serial")
@RestController
@RequestMapping("/icreport/mxzact")
@Slf4j
public class ICMxzController {

	@Autowired
	private IICMxz ic_rep_mxzserv;
	@Autowired
	private IUserService userService;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;

	@GetMapping("/queryAction")
	public ReturnData queryAction(@RequestParam Map<String, String> param) {
		ReportDataGrid grid = new ReportDataGrid();
		String currsp = getRequest().getParameter("currsp");
		String currspfl = getRequest().getParameter("currspfl");

		QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
		queryParamvo = getQueryParamVO(queryParamvo);
		int page = queryParamvo == null ? 1 : queryParamvo.getPage();
		int rows = queryParamvo == null ? 100000 : queryParamvo.getRows();

		checkPowerDate(queryParamvo);
		Map<String, IcDetailVO> result = null;
		result = ic_rep_mxzserv.queryDetail(queryParamvo, SystemUtil.getLoginCorpVo());
		result = filter(result, queryParamvo);
		List<IcDetailFzVO> listsps = createRightTree(result, currsp);
		// 将查询后的数据分页展示
		List<IcDetailVO> list = getPagedMXZVos(listsps, result, page, rows, grid, currsp, currspfl);
		// 再次排序
		Collections.sort(list, new Comparator<IcDetailVO>() {
			@Override
			public int compare(IcDetailVO o1, IcDetailVO o2) {
				// 先根据项目名
				int i = 0;
				String key1 = getInvKey(o1);
				String key2 = getInvKey(o2);
				i = key1.compareTo(key2);
				if (i == 0) {
					i = o1.getDbilldate().compareTo(o2.getDbilldate());
				}
				if (i == 0) {
					if (("本期合计".equals(o1.getZy()) && ReportUtil.bSysZy(o1))
							|| ("期初余额".equals(o2.getZy()) && ReportUtil.bSysZy(o2)))
						i = 1;
					else if (("本期合计".equals(o2.getZy()) && ReportUtil.bSysZy(o2))
							|| ("期初余额".equals(o1.getZy()) && ReportUtil.bSysZy(o1)))
						i = -1;
					else if (!StringUtil.isEmpty(o1.getDbillid()) && !StringUtil.isEmpty(o2.getDbillid())) {
						i = o1.getDbillid().compareTo(o2.getDbillid());
					}
				}
				return i;
			}
		});
		grid.setIccombox(listsps);
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
		grid.setRows(list == null ? new ArrayList<>() : list);
		grid.setIcDetail(list == null ? new ArrayList<>() : list);
		grid.setSuccess(true);
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
				|| !paramvo.getIshowfs().booleanValue()) {
			newresult = result;
		} else {
			newresult = new HashMap<String, IcDetailVO>();
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

	private List<IcDetailVO> getPagedMXZVos(List<IcDetailFzVO> listsps, Map<String, IcDetailVO> result, int page,
			int rows, ReportDataGrid grid, String currsp, String currspfl) {
		// if(!StringUtil.isEmpty(currsp) && currsp.startsWith("all")){
		// }

		if (listsps == null || listsps.size() == 0) {
			grid.setTotal(0L);
			return new ArrayList<IcDetailVO>();
		}

		if (StringUtil.isEmpty(currsp)) {
			currsp = listsps.get(0).getId();
			if ("all".equals(currsp)) {// 取第二个
				currsp = listsps.get(1).getId();
				if (listsps.get(1).getChildren() != null && listsps.get(1).getChildren().length > 0) {
					currspfl = listsps.get(1).getId();
				}
			} else {
				if (listsps.get(0).getChildren() != null && listsps.get(0).getChildren().length > 0) {
					currspfl = listsps.get(0).getId();
				}
			}
		}
		// 存货
		String[] currsps = currsp.split(",");

		// 存货分类
		String[] currspfls = null;
		if (!StringUtil.isEmpty(currspfl)) {
			currspfls = currspfl.split(",");
		}
		List<IcDetailVO> spList = new ArrayList<IcDetailVO>();
		if (!StringUtil.isEmpty(currsp) && currsp.startsWith("all")) {
			for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
				spList.add(entry.getValue());
			}
		} else {

			// 如果选中的不包含分类
			if (currspfls == null || currspfls.length == 0) {
				Map<String, List<IcDetailVO>> spMap = new HashMap<String, List<IcDetailVO>>();
				IcDetailVO icv = null;
				List<IcDetailVO> flist = null;
				for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
					icv = entry.getValue();
					if (spMap.containsKey(icv.getPk_sp())) {
						spMap.get(icv.getPk_sp()).add(icv);
					} else {
						flist = new ArrayList<IcDetailVO>();
						flist.add(icv);
						spMap.put(icv.getPk_sp(), flist);
					}
				}
				for (String str : currsps) {
					if (spMap.get(str) != null) {
						for (IcDetailVO detailvo : spMap.get(str)) {
							spList.add(detailvo);
						}
					}
				}
			} else {
				// 记录存货分类下的存货
				Map<String, List<String>> map = new HashMap<>();
				List<String> flist1 = null;
				IcDetailVO icv = null;
				for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
					icv = entry.getValue();
					if (map.containsKey(icv.getSpflid())) {
						map.get(icv.getSpflid()).add(icv.getPk_sp());
					} else {
						flist1 = new ArrayList<String>();
						flist1.add(icv.getPk_sp());
						map.put(icv.getSpflid(), flist1);
					}
				}
				// 获取分类下存货id 如果分类和分类下的存货id同时存在 已分类下的存货id为主
				List<String> allinvl = new ArrayList<>();
				for (String str : currspfls) {
					List<String> slist = map.get(str);
					boolean iscontain = false;
					if (slist == null || slist.size() == 0) {
						iscontain = true;
					} else {
						for (String sp : currsps) {
							if (slist.contains(sp)) {
								iscontain = true;
								break;
							}
						}
					}
					if (!iscontain) {
						for (String sp : slist) {
							allinvl.add(sp);
						}
					} else {
						allinvl.add(str);
					}
				}
				// 获取存货id
				for (String str : currsps) {
					allinvl.add(str);
				}
				for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
					icv = entry.getValue();
					if (allinvl.contains(icv.getPk_sp())) {
						spList.add(icv);
					}
				}
			}
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
		CorpVO currcorp =corpService.queryByPk(SystemUtil.getLoginCorpId());
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

	private List<IcDetailFzVO> createRightTree(Map<String, IcDetailVO> result, String currsp) {

		IcDetailFzVO fzvo = null;
		Set<String> conkeys = new HashSet<String>();
		IcDetailVO vo = null;
		List<IcDetailFzVO> listsps = new ArrayList<IcDetailFzVO>();

		for (Map.Entry<String, IcDetailVO> entry : result.entrySet()) {
			String key = entry.getKey();
			key = key.length() > 49 ? key.substring(0, 49) : key;
			vo = entry.getValue();
			if (!conkeys.contains(key) && !StringUtil.isEmpty(vo.getSpbm())) {
				fzvo = new IcDetailFzVO();
				fzvo.setId(vo.getPk_sp());
				fzvo.setText(vo.getSpbm() + " " + vo.getSpmc());
				fzvo.setCode(vo.getSpbm());
				fzvo.setSpflid(vo.getSpflid());
				fzvo.setSpflcode(vo.getSpflcode());
				fzvo.setSpfl(vo.getSpfl());
				conkeys.add(key);
				listsps.add(fzvo);
			}

		}
		if (listsps != null && listsps.size() > 0) {

			Map<String, List<IcDetailFzVO>> map = DZfcommonTools.hashlizeObject(listsps, new String[] { "spflid" });
			listsps.clear();
			for (Map.Entry<String, List<IcDetailFzVO>> entry : map.entrySet()) {
				List<IcDetailFzVO> listvo = entry.getValue();
				if (listvo != null && listvo.size() > 0) {
					if (StringUtil.isEmpty(listvo.get(0).getSpfl())) {
						listsps.addAll(listvo);
					} else {
						fzvo = new IcDetailFzVO();
						fzvo.setId(listvo.get(0).getSpflid());
						fzvo.setText(listvo.get(0).getSpflcode() + " " + listvo.get(0).getSpfl());
						fzvo.setCode(listvo.get(0).getSpflcode());
						fzvo.setState("closed");
						fzvo.setIsfl(DZFBoolean.TRUE);
						Collections.sort(listvo, new Comparator<IcDetailFzVO>() {
							@Override
							public int compare(IcDetailFzVO o1, IcDetailFzVO o2) {
								int i = o1.getText().compareTo(o2.getText());
								return i;
							}
						});
						fzvo.setChildren(listvo.toArray(new IcDetailFzVO[listvo.size()]));
						listsps.add(fzvo);
					}

				}
			}
		}
		if (listsps != null && listsps.size() > 0) {
			List<String> checklist = new ArrayList<String>();
			Collections.sort(listsps, new Comparator<IcDetailFzVO>() {
				@Override
				public int compare(IcDetailFzVO o1, IcDetailFzVO o2) {
					int i = o1.getText().compareTo(o2.getText());
					return i;
				}
			});
			if (!StringUtil.isEmpty(currsp)) {
				String[] values = currsp.split(",");
				checklist = Arrays.asList(values);
			}
			// else{//默认选中第一条
			// checklist.add(listsps.get(0).getId());
			// }

			IcDetailFzVO alldetailvo = new IcDetailFzVO();
			alldetailvo.setCode("all");
			alldetailvo.setId("all");
			alldetailvo.setText("全选");
			listsps.add(0, alldetailvo);

			for (IcDetailFzVO vo1 : listsps) {
				if (checklist.contains(vo1.getId()) || (!StringUtil.isEmpty(currsp) && currsp.startsWith("all"))) {
					vo1.setChecked("true");
					if (StringUtil.isEmpty(currsp)) {// 科目为空，自动带出默认值
						vo1.setBdefault(DZFBoolean.TRUE);
					}
				}

			}
		}

		return listsps;
	}

	@PostMapping("print")
	public void printAction(@RequestParam Map<String, String> pmap, HttpServletResponse response) {
		try {
			PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
			PrintParamVO printParamVO = JsonUtils.convertValue(pmap, PrintParamVO.class);//

			if ("Y".equals(pmap.get("pageOrt"))) {
				printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
			} else {
				printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
			}
            String gs =pmap.get("gs");
            String titlePeriod =pmap.get("titlePeriod");
            String currsp = getRequest().getParameter("currsp");
            QueryParamVO queryParamvo = JsonUtils.convertValue(pmap, QueryParamVO.class);
            IcDetailVO[] bodyvos = queryVos(getQueryParamVO(queryParamvo),currsp);
            if(bodyvos == null || bodyvos.length ==0){
                return;
            }
            bodyvos[0].setGs(gs);
            bodyvos[0].setTitlePeriod(titlePeriod);

			Map<String, List<SuperVO>> mxmap = new HashMap<>();
			mxmap = reloadVOs(bodyvos, getQueryParamVO(queryParamvo));

			Map<String, String> tmap = new HashMap<>();// 声明一个map用来存前台传来的设置参数
			tmap.put("公司", gs);
			tmap.put("期间", titlePeriod);
			CorpVO corpvo =SystemUtil.getLoginCorpVo();
			boolean bisfenye = false;
			String isfenye = getRequest().getParameter("isfenye");
			if (!StringUtil.isEmpty(isfenye) && (isfenye.equals("Y") || isfenye.equals("true"))) {
				bisfenye = true;
			}

			if (!bisfenye) {
				// 老模式 启用库存
				if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
					String[] columnames = new String[] { "科目", "存货分类", "存货编码", "存货名称", "规格(型号)", "计量单位", "日期", "摘要",
							"收入数量", "收入单价", "收入金额", "发出数量", "发出单价", "发出金额", "结存数量", "结存单价", "结存金额" };
					String[] columnkeys = new String[] { "km", "spfl", "spbm", "spmc", "spgg", "jldw", "dbilldate",
							"zy", "srsl", "srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
					printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
					int[] widths = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
					printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
					printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存明细账", columnkeys, columnames, widths, 60, pmap, tmap);
				} else {
					String[] columnames = new String[] { "科目", "存货分类", "存货编码", "存货名称", "规格(型号)", "计量单位", "日期", "单据号",
							"摘要", "收入数量", "收入单价", "收入金额", "发出数量", "发出单价", "发出金额", "结存数量", "结存单价", "结存金额" };
					String[] columnkeys = new String[] { "km", "spfl","spbm", "spmc", "spgg", "jldw", "dbilldate", "dbillid", "zy",
							"srsl", "srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
					int[] widths = new int[] { 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 };
					printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
					printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "库存明细账", columnkeys, columnames, widths, 60, pmap, tmap);
				}
			} else {
				// 老模式 启用库存
				if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
					String[] columnames = new String[] { "科目", "存货分类", "规格(型号)", "计量单位", "日期", "摘要", "收入", "发出", "结存",
							"数量", "单价", "金额", "数量", "单价", "金额", "数量", "单价", "金额" };
					String[] columnkeys = new String[] { "km", "spfl", "spgg", "jldw", "dbilldate", "zy", "srsl",
							"srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
					LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
					for (int i = 0; i < columnames.length; i++) {
						ColumnCellAttr attr = new ColumnCellAttr();
						attr.setColumname(columnames[i]);
						if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5) {
							attr.setRowspan(2);
						} else if (i == 6 || i == 7 || i == 8) {
							attr.setColspan(3);
						}

						columnlist.add(attr);
					}
					printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
					if (pmap.get("type").equals("1"))
						printReporUtil.printGroup(mxmap, null, bodyvos[0].getSpmc() + "库存明细账", columnkeys, columnames, columnlist,
								new int[] { 3, 3, 4, 3, 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);// A4纸张打印
					else if (pmap.get("type").equals("2")) {
						printReporUtil.printB5(mxmap, null, bodyvos[0].getSpmc() + "库存明细账", columnkeys, columnames, columnlist,
								new int[] { 3, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);
					} else if (pmap.get("type").equals("4")) {// A5纸张{
						printReporUtil.printA5(mxmap, null, bodyvos[0].getSpmc() + "库存明细账", columnkeys, columnames, columnlist,
								new int[] { 3, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);
					}
				} else {
					String[] columnames = new String[] { "科目", "存货分类", "规格(型号)", "计量单位", "日期", "单据号", "摘要", "收入", "发出",
							"结存", "数量", "单价", "金额", "数量", "单价", "金额", "数量", "单价", "金额" };
					String[] columnkeys = new String[] { "km", "spfl", "spgg", "jldw", "dbilldate", "dbillid", "zy",
							"srsl", "srdj", "srje", "fcsl", "fcdj", "fcje", "jcsl", "jcdj", "jcje" };
					LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
					for (int i = 0; i < columnames.length; i++) {
						ColumnCellAttr attr = new ColumnCellAttr();
						attr.setColumname(columnames[i]);
						if (i == 0 || i == 1 || i == 2 || i == 3 || i == 4 || i == 5 || i == 6) {
							attr.setRowspan(2);
						} else if (i == 7 || i == 8 || i == 9) {
							attr.setColspan(3);
						}

						columnlist.add(attr);
					}
					printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
					if (pmap.get("type").equals("1"))
						printReporUtil.printGroup(mxmap, null, bodyvos[0].getSpmc() + "库存明细账", columnkeys, columnames, columnlist,
								new int[] { 3, 3, 4, 3, 3, 3, 5, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);// A4纸张打印
					else if (pmap.get("type").equals("2")) {
						printReporUtil.printB5(mxmap, null, bodyvos[0].getSpmc() + "库存明细账", columnkeys, columnames, columnlist,
								new int[] { 3, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);
					} else if (pmap.get("type").equals("4")) {// A5纸张
						printReporUtil.printA5(mxmap, null, bodyvos[0].getSpmc() + "库存明细账", columnkeys, columnames, columnlist,
								new int[] { 3, 3, 4, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 0, null, pmap, tmap);
					}
				}
			}
		} catch (DocumentException e) {
			log.error("库存明细账打印错误", e);
		} catch (IOException e) {
			log.error("库存明细账打印错误", e);
		}catch (Exception e) {
            log.error("库存明细账打印失败", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存明细账打印错误", e);
            }
        }
	}

	private IcDetailVO[] queryVos(QueryParamVO queryParamVO,String currsp) {
		Map<String, IcDetailVO> result = null;

		result = ic_rep_mxzserv.queryDetail(queryParamVO, SystemUtil.getLoginCorpVo());

		result = filter(result, queryParamVO);

		List<IcDetailFzVO> listsps = createRightTree(result, currsp);
		// 将查询后的数据分页展示
		List<IcDetailVO> list = getPagedMXZVos(listsps, result, 1, Integer.MAX_VALUE, new ReportDataGrid(), currsp, null);
		// 再次排序
		Collections.sort(list, new Comparator<IcDetailVO>() {
			@Override
			public int compare(IcDetailVO o1, IcDetailVO o2) {
				// 先根据项目名
				int i = 0;
				String key1 = getInvKey(o1);
				String key2 = getInvKey(o2);
				i = key1.compareTo(key2);
				if (i == 0) {
					i = o1.getDbilldate().compareTo(o2.getDbilldate());
				}
				if (i == 0) {
					if (("本期合计".equals(o1.getZy()) && ReportUtil.bSysZy(o1))
							|| ("期初余额".equals(o2.getZy()) && ReportUtil.bSysZy(o2)))
						i = 1;
					else if (("本期合计".equals(o2.getZy()) && ReportUtil.bSysZy(o2))
							|| ("期初余额".equals(o1.getZy()) && ReportUtil.bSysZy(o1)))
						i = -1;
					else if (!StringUtil.isEmpty(o1.getDbillid()) && !StringUtil.isEmpty(o2.getDbillid())) {
						i = o1.getDbillid().compareTo(o2.getDbillid());
					}
				}
				return i;
			}
		});

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
		for (Map.Entry<String, List<SuperVO>> entry : mxmap.entrySet()) {
			sortList = entry.getValue();
			Collections.sort(sortList, new Comparator<SuperVO>() {

				@Override
				public int compare(SuperVO s1, SuperVO s2) {
					IcDetailVO o1 = (IcDetailVO) s1;
					IcDetailVO o2 = (IcDetailVO) s2;
					int i = 0;
					i = o1.getDbilldate().compareTo(o2.getDbilldate());
					if (i == 0) {
						if (("本期合计".equals(o1.getZy()) && ReportUtil.bSysZy(o1))
								|| ("期初余额".equals(o2.getZy()) && ReportUtil.bSysZy(o2)))
							i = 1;
						else if (("本期合计".equals(o2.getZy()) && ReportUtil.bSysZy(o2))
								|| ("期初余额".equals(o1.getZy()) && ReportUtil.bSysZy(o1)))
							i = -1;
						else if (!StringUtil.isEmpty(o1.getDbillid()) && !StringUtil.isEmpty(o2.getDbillid())) {
							i = o1.getDbillid().compareTo(o2.getDbillid());
						}
					}
					return i;
				}
			});
		}

		Map<String, List<SuperVO>> sortMap = new TreeMap<String, List<SuperVO>>(new Comparator<String>() {
			public int compare(String str1, String str2) {
				return str1.compareTo(str2);
			}
		});
		sortMap.putAll(mxmap);
		return sortMap;
	}

	private void setExprotInfo(IcMxExcelField xsz, CorpVO corpvo) {
		// 老模式 启用库存
		if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
			xsz.setFields(xsz.getFields2());
		} else {
			xsz.setFields(xsz.getFields1());
		}

	}

	// 导出excel
    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
		OutputStream toClient = null;
		try {

            QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
            String gs =param.get("gs");
            String titlePeriod =param.get("titlePeriod");
            String currsp = getRequest().getParameter("currsp");
            IcDetailVO[] vo = queryVos(getQueryParamVO(queryParamvo),currsp);
			Excelexport2003<IcDetailVO> lxs = new Excelexport2003<>();
			String pk_corp = SystemUtil.getLoginCorpId();
			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			IcMxExcelField xsz = new IcMxExcelField(num, price);
			setExprotInfo(xsz, corpvo);
			xsz.setIcDetailVos(vo);
			xsz.setQj(titlePeriod);
			xsz.setCreator(SystemUtil.getLoginUserVo().getUser_name());
			xsz.setCorpName(gs);
			response.reset();
			// String filename = xsz.getExcelport2007Name();
			String filename = xsz.getExcelport2003Name();
			String formattedName = URLEncoder.encode(filename, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			lxs.exportExcel(xsz, toClient);
			toClient.flush();
			response.getOutputStream().flush();
		}  catch (IOException e) {
            log.error("库存明细账excel导出错误", e);
        } catch (Exception e) {
            log.error("库存明细账excel导出错误", e);
        } finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (IOException e) {
				log.error("库存明细账excel导出错误", e);
			}
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("库存明细账excel导出错误", e);
			}
		}
	}
}

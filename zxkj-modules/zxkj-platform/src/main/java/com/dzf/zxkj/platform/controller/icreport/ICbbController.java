package com.dzf.zxkj.platform.controller.icreport;


import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.KccbExcelField;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.icreport.IQueryLastNum;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.Kmschema;
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
 * 库存成本表
 *
 */

@RestController
@RequestMapping("/icreport/rep_cbbact")
@Slf4j
public class ICbbController  extends BaseController {
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IInventoryService iservice;
	@Autowired
	private IUserService userService;
    @Autowired
    private ICorpService corpService;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;
	// 查询
    @GetMapping("/query")
	public ReturnData query(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		grid.setRows(new ArrayList<IcbalanceVO>());
		String qryDate = null;

        IcbalanceVO data = JsonUtils.convertValue(param, IcbalanceVO.class);
        int page = data.getPage();
        int rows = data.getRows();
        if (page < 1 || rows < 1) {
            throw new BusinessException("查询失败！");
        }
        qryDate = data.getDbilldate();
        checkPowerDate(data);
        String pk_invtory = data.getPk_inventory();
        String xsyye = param.get("xsyye");
        String pk_subjectname = data.getPk_subjectname();
        String priceStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF010);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
        List<IcbalanceVO> flist = queryList(qryDate, pk_invtory, xsyye, pk_subjectname, price);
		grid.setTotal(Long.valueOf(flist == null ? 0 : flist.size()));
        if (flist != null && flist.size() > 0) {
            IcbalanceVO[] pvos = getPageVOs(flist.toArray(new IcbalanceVO[flist.size()]), page, rows);
            flist = Arrays.asList(pvos);
        }
        grid.setRows(flist == null ? new ArrayList<IcbalanceVO>() : flist);
        grid.setSuccess(true);
        grid.setMsg("查询成功");

//		// 日志记录
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT,
				new StringBuffer().append("库存成本表查询:").append(qryDate).toString(), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
	}

	private List<IcbalanceVO> queryList(String qryDate, String pk_invtory, String xsyye, String pk_subjectname,
			int price) {
		String corpid = SystemUtil.getLoginCorpId();

		List<IcbalanceVO> flist = new ArrayList<>();
		if (corpid != null && !StringUtil.isEmptyWithTrim(qryDate)) {
			List<IcbalanceVO> list = ic_rep_cbbserv.queryLastBanlanceVOs_byList1(qryDate, corpid, pk_invtory, null,
					true);
			if (list != null && list.size() > 0) {
				List<InventoryVO> splist = iservice.queryInfo(SystemUtil.getLoginCorpId(), null);
				Map<String, InventoryVO> invMap = DZfcommonTools.hashlizeObjectByPk(splist,
						new String[] { "pk_inventory" });
				CorpVO corpVo =corpService.queryByPk(SystemUtil.getLoginCorpId());

				Map<String, IcbalanceVO> balMap1 = ic_rep_cbbserv.queryLastBanlanceVOs_byMap4(qryDate.toString(),
						SystemUtil.getLoginCorpId(), null, true);
				for (IcbalanceVO vo : list) {
					if (corpVo.getIbuildicstyle() != null && corpVo.getIbuildicstyle() == 1) {// 新模式库存
						IcbalanceVO balvo1 = balMap1.get(vo.getPk_inventory());
						if (balvo1 != null) {
							if ((vo.getNnum() == null || vo.getNnum().doubleValue() == 0)
									&& (vo.getNcost() == null || vo.getNcost().doubleValue() == 0)) {
							} else {
								if(vo.getNnum() == null || vo.getNnum().doubleValue() == 0){
								}else{
									vo.setNprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum()).setScale(price, 2));
								}
								vo.setNcost(balvo1.getNcost());
							}
						}
					} else {
						if ((vo.getNnum() == null || vo.getNnum().doubleValue() == 0)
								&& (vo.getNcost() == null || vo.getNcost().doubleValue() == 0)) {
						} else {
							vo.setNprice(SafeCompute.div(vo.getNcost(), vo.getNnum()).setScale(price, 2));
						}
					}

					if ("Y".equalsIgnoreCase(xsyye) || "true".equalsIgnoreCase(xsyye) ) {
						if ((vo.getNnum() == null || vo.getNnum().doubleValue() == 0)
								&& (vo.getNcost() == null || vo.getNcost().doubleValue() == 0)) {
							continue;
						}
					}

					if (StringUtil.isEmpty(pk_subjectname)) {
						flist.add(vo);
					} else if ("库存商品".equals(pk_subjectname)) {
						InventoryVO invvo = invMap.get(vo.getPk_inventory());
						if (invvo != null) {
							if (Kmschema.isKcspbm(corpVo.getCorptype(), invvo.getKmcode())) {
								flist.add(vo);
							}
						}

					} else if ("原材料".equals(pk_subjectname)) {
						InventoryVO invvo = invMap.get(vo.getPk_inventory());
						if (invvo != null) {
							if (Kmschema.isYclbm(corpVo.getCorptype(), invvo.getKmcode())) {
								flist.add(vo);
							}
						}
					} else {
						flist.add(vo);
					}
				}
			}
			if (flist != null && flist.size() > 0) {
				Collections.sort(flist, new Comparator<IcbalanceVO>() {
					@Override
					public int compare(IcbalanceVO o1, IcbalanceVO o2) {
						int i = o1.getInventorycode().compareTo(o2.getInventorycode());
						return i;
					}
				});
			}
		}
		return flist;
	}

	private void checkPowerDate(IcbalanceVO vo) {
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
		if (begdate.after(new DZFDate(vo.getDbilldate()))) {
			throw new BusinessException("截止日期不能在启用库存日期(" + DateUtils.getPeriod(begdate) + ")前!");
		}
	}

	// 将查询后的结果分页
	private IcbalanceVO[] getPageVOs(IcbalanceVO[] pageVos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= pageVos.length) {// 防止endIndex数组越界
			endIndex = pageVos.length;
		}
		pageVos = Arrays.copyOfRange(pageVos, beginIndex, endIndex);
		return pageVos;
	}

	/**
	 * 打印操作
	 */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap, HttpServletResponse response) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
            if (DZFValueCheck.isEmpty(pmap.get("list"))) {
                return;
            }
            if ("Y".equals(pmap.get("pageOrt"))) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
			String pk_corp = SystemUtil.getLoginCorpId();
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
			String curr_print = pmap.get("curr_print");
			IcbalanceVO[] listVo = getData(pmap,curr_print,price);
			if (listVo == null || listVo.length == 0)
				return;
			Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存title
			tmap.put("公司", listVo[0].getGs());
			tmap.put("查询日期", listVo[0].getDjrq());
			printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体

			setDefaultValue(listVo, SystemUtil.getLoginCorpId());// 为后续设置精度赋值

			printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), listVo, "库存成本表",
					new String[] { "inventorycode", "inventoryname", "invspec", "measurename", "inventorytype",
							"pk_subjectname", "nnum", "nprice", "ncost" },
					new String[] { "存货编码", "存货名称", "规格(型号)", "计量单位", "存货分类", "科目名称", "结存数量", "结存单价", "结存成本" },
					new int[] { 2, 2, 2, 2, 2, 2, 2, 2, 2 }, 20, pmap, tmap);
		} catch (DocumentException e) {
			log.error("库存成本表打印失败", e);
		} catch (IOException e) {
			log.error("库存成本表打印失败", e);
		}catch (Exception e) {
            log.error("库存成本表打印失败", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存成本表打印错误", e);
            }
        }
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT,"库存成本表打印", ISysConstants.SYS_2);
	}

	private void setDefaultValue(IcbalanceVO[] bodyvos, String pk_corp) {
		if (bodyvos != null && bodyvos.length > 0) {
			for (IcbalanceVO vo : bodyvos) {
				vo.setPk_corp(pk_corp);
			}
		}
	}

	/**
	 * 导出excel
	 */
	@PostMapping("/expExcel")
	public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
		OutputStream toClient = null;
		try {
			response.reset();
			Excelexport2003<IcbalanceVO> lxs = new Excelexport2003<>();

			String pk_corp = SystemUtil.getLoginCorpId();
			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

			String curr_excel = param.get("curr_excel");
			IcbalanceVO[] listVo = getData(param,curr_excel,price);
			if (listVo == null || listVo.length == 0)
				return;
			String gs = listVo[0].getGs();
			String qj = listVo[0].getDjrq();

			KccbExcelField field = new KccbExcelField(num, price);
			field.setExpvos(listVo);
			field.setQj(qj);
			field.setCreator(SystemUtil.getLoginUserVo().getUser_name());
			field.setCorpName(gs);
			String fileName = field.getExcelport2003Name();
			String formattedName = URLEncoder.encode(fileName, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
			toClient = new BufferedOutputStream(response.getOutputStream());
			lxs.exportExcel(field, toClient);
			toClient.flush();
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("库存成本表excel导出错误", e);
		} catch (Exception e) {
            log.error("库存成本表excel导出错误", e);
        }finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (IOException e) {
				log.error("库存成本表excel导出错误", e);
			}
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("库存成本表excel导出错误", e);
			}
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_REPORT,"库存成本表导出", ISysConstants.SYS_2);
		}
	}


	private IcbalanceVO[] getData( Map<String, String> param,String curr_excel,int price ){
		DZFBoolean dboolean = new DZFBoolean(curr_excel);
		IcbalanceVO[] bodyvos = null;
		if (dboolean.booleanValue()) {// 是否当前显示页
			String strlist = param.get("list");
			if (strlist == null) {
				bodyvos = new IcbalanceVO[0];
			} else {
				strlist = strlist.replace("}{", "},{");
				bodyvos= JsonUtils.deserialize(strlist, IcbalanceVO[].class);
			}
		} else {
			String qryDate =  param.get("djrqa");
			String pk_invtory =  param.get("spid");
			String xsyye = param.get("xsyye");
			String pk_subjectname =  param.get("kmmc");
			List<IcbalanceVO> flist = queryList(qryDate, pk_invtory, xsyye, pk_subjectname, price);
			bodyvos = flist.toArray(new IcbalanceVO[flist.size()]);
			bodyvos[0].setGs(SystemUtil.getLoginCorpVo().getUnitname());
			bodyvos[0].setDjrq(qryDate);
		}
		return bodyvos;
	}

}

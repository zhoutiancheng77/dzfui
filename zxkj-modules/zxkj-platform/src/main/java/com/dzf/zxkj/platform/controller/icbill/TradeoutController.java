package com.dzf.zxkj.platform.controller.icbill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DZFArrayUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.icset.AggIcTradeVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.icbill.ITradeoutService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 出库单
 *
 */

@RestController
@RequestMapping("/icbill/tradeoutact")
@Slf4j
public class TradeoutController extends BaseController {

	@Autowired
	private ITradeoutService ic_tradeoutserv = null;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;
	// 查询
	@GetMapping("/query")
	public ReturnData query(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		QueryParamVO paramvo = JsonUtils.convertValue(param, QueryParamVO.class);
		int page = paramvo.getPage();
		int rows = paramvo.getRows();
		if (page < 1 || rows < 1) {
			throw new BusinessException("查询失败！");
		}
        paramvo =getQueryParamVO(paramvo);
		List<IntradeoutVO> list = null;
		if (paramvo != null) {
			list = ic_tradeoutserv.query(paramvo);
		}
		IntradeoutVO[] vos = null;
		if (list != null && list.size() > 0) {
			vos = getPageVOs(list.toArray(new IntradeoutVO[list.size()]), page, rows);
		}
		grid.setTotal(Long.valueOf(vos == null ? 0 : vos.length));
		grid.setRows(vos == null ? new IntradeoutVO[0] : vos);
		grid.setSuccess(true);
		grid.setMsg("查询成功");

		String begindate = null;
		String endate = null;
		DZFDate udate = new DZFDate();
		// 日志记录
		if (paramvo == null) {
			begindate = udate.toString();
			endate = udate.toString();
		} else {
			begindate = paramvo.getBegindate1() == null ? udate.toString() : paramvo.getBegindate1().toString();
			endate = paramvo.getEnddate() == null ? udate.toString() : paramvo.getEnddate().toString();
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI,
//				new StringBuffer().append("出库单查询:").append(begindate).append("-").append(endate).toString(),
//				ISysConstants.SYS_2);
		return ReturnData.ok().data(grid);
	}

    private QueryParamVO getQueryParamVO(QueryParamVO paramvo) {
        // CorpVO corpvo = getLoginCorpInfo();
        paramvo.setPk_corp(SystemUtil.getLoginCorpId());
        if (paramvo.getBegindate1() == null) {
            return null;
        }
        if (paramvo.getEnddate() == null) {
            return null;
        }
        return paramvo;
    }

	// 将查询后的结果分页
	private IntradeoutVO[] getPageVOs(IntradeoutVO[] pageVos, int page, int rows) {
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
            PrintParamVO printParamVO = JsonUtils.convertValue(pmap, PrintParamVO.class);//
            if (DZFValueCheck.isEmpty(pmap.get("list"))) {
                return;
            }
            if ("Y".equals(pmap.get("pageOrt"))) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
			String body = printParamVO.getList();
			body = body.replace("}{", "},{");
			IntradeoutVO[] bodyvos= JsonUtils.deserialize(body, IntradeoutVO[].class);
			String type = printParamVO.getType();
			Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存title
			tmap.put("公司", bodyvos[0].getGs());
			tmap.put("期间", bodyvos[0].getTitlePeriod());
			IntradeoutVO nvo = calTotal(bodyvos);
            bodyvos = DZFArrayUtil.combineArray(bodyvos,new IntradeoutVO[]{nvo});
			setDefaultValue(bodyvos, SystemUtil.getLoginCorpId());//为后续设置精度赋值
			printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
			printReporUtil.setLineheight(22F);
			printReporUtil.printHz(new HashMap<>(),bodyvos, "出 库 单",
					new String[] { "kmmc", "invname", "zy", "invspec","measure", "dbilldate", "nnum",
							"ncost", "pzh", "memo" },
					new String[] { "科目", "存货", "摘要", "规格(型号)", "计量单位", "单据日期", "数量", "成本", "凭证号", "备注" },
					new int[] { 2, 3, 4, 4, 2, 2, 3, 2, 3, 2 }, 20, pmap, tmap);
		} catch (DocumentException e) {
			log.error("出库单打印失败", e);
		} catch (IOException e) {
			log.error("出库单打印失败", e);
		}catch (Exception e) {
			log.error("出库单打印失败", e);
		} finally {
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("出库单打印错误", e);
			}
		}
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "打印出库单", ISysConstants.SYS_2);
	}

	private IntradeoutVO calTotal(SuperVO[] bodyvos) {
		// 计算合计行数据
		DZFDouble d1 = DZFDouble.ZERO_DBL;
		DZFDouble d2 = DZFDouble.ZERO_DBL;
		for (SuperVO body : bodyvos) {
			IntradeoutVO ivo = (IntradeoutVO) body;
			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(ivo.getNcost()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(ivo.getNnum()));
		}
		IntradeoutVO nvo = new IntradeoutVO();
        nvo.setKmmc("合计");
		nvo.setNcost(d1);
		nvo.setNnum(d2);
		return nvo;
	}

	private void setDefaultValue(IntradeoutVO[] bodyvos, String pk_corp){
		if(bodyvos != null && bodyvos.length > 0){
			for(IntradeoutVO vo : bodyvos){
				vo.setPk_corp(pk_corp);
			}
		}
	}

	@PostMapping("/expExcel")
	public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
		OutputStream toClient = null;
		try {
			String body = param.get("list");
			body = body.replace("}{", "},{");
			AggIcTradeVO[] bodyvos =JsonUtils.deserialize(body,AggIcTradeVO[].class); // 表体
			response.reset();
			String exName = new String("出库单.xls");
			exName = new String(exName.getBytes("GB2312"), "ISO_8859_1");// 解决中文乱码问题
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName));
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = null;

			Map<String, Integer> preMap = getPreMap();//设置精度

			IcBillExport exp = new IcBillExport();
			length = exp.exportExcel(bodyvos, toClient, 2, false, preMap);
			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
			toClient.flush();
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("出库单excel导出错误", e);
		} catch (Exception e) {
			log.error("出库单excel导出错误", e);
		} finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (IOException e) {
				log.error("出库单excel导出错误", e);
			}
			try {
				if (response!=null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("出库单excel导出错误", e);
			}
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI,"导出出库单",ISysConstants.SYS_2);
		}
	}

	private Map<String, Integer> getPreMap(){
		String pk_corp = SystemUtil.getLoginCorpId();
		String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
		String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		Map<String, Integer> preMap = new HashMap<String, Integer>();
		preMap.put(IParameterConstants.DZF009, num);
		preMap.put(IParameterConstants.DZF010, price);
		return preMap;
	}

}
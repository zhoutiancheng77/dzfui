package com.dzf.zxkj.platform.controller.icbill;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.IcConst;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.icset.AggIcTradeVO;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.IntradeParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * 入库单
 *
 * @author
 *
 */

@RestController
@RequestMapping("/icbill/purchinact")
@Slf4j
public class PurchInController {

	@Autowired
    IPurchInService ic_purchinserv;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ISecurityService securityserv;
    @Autowired
    private ICorpService corpService;
	@Autowired
	private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;

    // 查询
    @GetMapping("/query")
    public ReturnData query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        IntradeParamVO data = JsonUtils.convertValue(param, IntradeParamVO.class);
        int page = data.getPage();
        int rows = data.getRows();
        IntradeParamVO paramvo = getQueryParamVO(data);
        if (paramvo != null) {
            List<IntradeHVO> list = ic_purchinserv.query(paramvo);
            if (list != null && list.size() > 0) {
                grid.setTotal((long) list.size());
                IntradeHVO[] PzglPagevos = getPagedZZVOs(list.toArray(new IntradeHVO[list.size()]), page, rows);
                grid.setRows(Arrays.asList(PzglPagevos));
            } else {
                grid.setTotal((long) 0);
            }
            grid.setSuccess(true);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        }
        return ReturnData.ok().data(grid);
	}

	// 将查询后的结果分页
	private IntradeHVO[] getPagedZZVOs(IntradeHVO[] PzglPagevos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= PzglPagevos.length) {// 防止endIndex数组越界
			endIndex = PzglPagevos.length;
		}
		PzglPagevos = Arrays.copyOfRange(PzglPagevos, beginIndex, endIndex);
		return PzglPagevos;
	}

	private IntradeParamVO getQueryParamVO( IntradeParamVO paramvo ) {
		paramvo.setPk_corp(SystemUtil.getLoginCorpId());
		if ("serMon".equals(paramvo.getSerdate())) {
			if (paramvo.getStartYear() != null && paramvo.getStartMonth() != null) {
				paramvo.setBegindate(
						DZFDate.getDate(paramvo.getStartYear() + "-" + paramvo.getStartMonth() + "-" + "01"));
				paramvo.setEnddate(TrunLastDay(paramvo.getEndYear(), paramvo.getEndMonth()));
			} else {
				throw new BusinessException("查询失败！" + "请填写正确的日期");
			}
		}
		return paramvo;
	}

	// 根据年和月得到月份的最后日期
	private DZFDate TrunLastDay(String selYear, String selMon) throws DZFWarpException {
		String peroid = selYear + "-" + selMon;
		return DateUtils.getPeriodEndDate(peroid);
	}

	@PostMapping("/save")
	public ReturnData save(@RequestBody Map<String, String> param) {
		Json json = new Json();
		IntradeHVO headvo = null;
		boolean isadd = false;
        String head =param.get("head");
        String body = param.get("body"); // 子表
        body = body.replace("}{", "},{");
//        body = "[" + body + "]";
        headvo = JsonUtils.deserialize(head, IntradeHVO.class);

        if (head == null) {
            throw new BusinessException("表头不允许为空!");
        }
        String pk_corp = headvo.getPk_corp();
        if (StringUtil.isEmpty(pk_corp)) {
            pk_corp = SystemUtil.getLoginCorpId();
            headvo.setPk_corp(pk_corp);
        }
        securityserv.checkSecurityForSave(pk_corp, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        if (StringUtil.isEmpty(headvo.getPk_ictrade_h())) {
            isadd = true;
        }
        IctradeinVO[] bodyvos = JsonUtils.deserialize(body, IctradeinVO[].class);// form提交保存
        headvo.setChildren(bodyvos);
        //
        headvo.setIarristatus(1);
        headvo.setCreator(SystemUtil.getLoginUserId());

        StringBuffer strb = new StringBuffer();
        checkBodys(headvo, (IctradeinVO[]) headvo.getChildren(), strb);
        if (strb.length() > 0) {
            throw new BusinessException(strb.toString());
        }
        ic_purchinserv.save(headvo, false);
        json.setSuccess(true);
        json.setMsg("保存成功!");
		if (isadd) {
			if (headvo != null && !StringUtil.isEmpty(headvo.getDbillid())) {
//				writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "新增入库单：" + headvo.getDbillid(),
//						ISysConstants.SYS_2);
			}

		} else {
			if (headvo != null && !StringUtil.isEmpty(headvo.getDbillid())) {
//				writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "修改入库单：" + headvo.getDbillid(),
//						ISysConstants.SYS_2);
			}
		}
        return ReturnData.ok().data(json);
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

			if (vo.getNymny() == null || vo.getNymny().compareTo(DZFDouble.ZERO_DBL) == 0) {
				strb.append("第" + (i + 1) + "行,存在金额为空或零的数据!\n");
			}
		}
	}

    @PostMapping("/delete")
	public ReturnData delete(@RequestBody Map<String, String> param) {
		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		IntradeHVO[] bodyvos = null;
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
            bodyvos = JsonUtils.deserialize(body, IntradeHVO[].class);// form提交保存

			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,删除失败!!");
			}
			securityserv.checkSecurityForDelete(bodyvos[0].getPk_corp(), SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
			for (IntradeHVO head : bodyvos) {
				try {
					ic_purchinserv.delete(head, SystemUtil.getLoginCorpId());

				} catch (Exception e) {
					printErrorLog(json, e, "删除失败!");
					strb.append("<p>入库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
				}
			}
		} catch (Exception e) {
			printErrorLog(json, e, "删除失败!");
			strb.append("删除失败!");
		}
		if (strb.length() == 0) {
			json.setSuccess(true);
			json.setMsg("删除成功!");
		} else {
			json.setSuccess(false);
			json.setMsg(strb.toString());
		}
//		writeLogRecords(bodyvos, "删除入库单");
//		writeJson(json);
        return ReturnData.ok().data(json);
	}

    private void printErrorLog(Json json, Throwable e, String errorinfo){
        if(StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if(e instanceof BusinessException){
            json.setMsg(e.getMessage());
        }else{
            json.setMsg(errorinfo);
            log.error(errorinfo,e);
        }
        json.setSuccess(false);
    }
	private void writeLogRecords(IntradeHVO[] bodyvos, String msg) {

		if (bodyvos == null || bodyvos.length == 0)
			return;
		if (bodyvos.length == 1) {
//			writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), msg + ",单据号" + bodyvos[0].getDbillid() + ".",
//					ISysConstants.SYS_2);
		} else {
//			writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "批量删除入库单，单据号" + bodyvos[0].getDbillid() + "等.",
//					ISysConstants.SYS_2);
		}

	}

    @PostMapping("/saveToZz")
	public ReturnData saveToZz(@RequestBody Map<String, String> param) {
		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		IntradeHVO[] bodyvos = null;
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
            bodyvos = JsonUtils.deserialize(body,IntradeHVO[].class);
			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,转总账失败!");
			}
			securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
			List<String> periodSet = new ArrayList<String>();
			int flag = 0;
			for (IntradeHVO head : bodyvos) {
				try {
					periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
					ic_purchinserv.saveIntradeHVOToZz(head, SystemUtil.getLoginCorpVo());
					flag++;
				} catch (Exception e) {
					printErrorLog(json, e, "转总账失败");
					strb.append("<p>入库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
				}
			}
			if (flag > 0) {
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId());
				if (!StringUtil.isEmpty(msg)) {
					strb.append(msg);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, e, "转总账失败");
			strb.append(json.getMsg());
		}
		if (strb.length() == 0) {
			json.setSuccess(true);
			json.setMsg("转总账成功!");
		} else {
			json.setSuccess(false);
			json.setMsg(strb.toString());
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "入库单转总账", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

    @PostMapping("/saveToTotalZz")
	public ReturnData saveToTotalZz(@RequestBody Map<String, String> param) {
		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		IntradeHVO[] bodyvos = null;
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
            bodyvos = JsonUtils.deserialize(body,IntradeHVO[].class);
			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,转总账失败!");
			}
			securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
			int flag = 0;
			Map<String, List<IntradeHVO>> map = new LinkedHashMap<String, List<IntradeHVO>>();
			List<IntradeHVO> list = null;
			List<IntradeHVO> zlist = null;
			List<String> periodSet = new ArrayList<String>();
			for (IntradeHVO head : bodyvos) {
				if (!StringUtil.isEmpty(head.getCbusitype()) && IcConst.WGTYPE.equalsIgnoreCase(head.getCbusitype())) {
					printErrorLog(json, null, "完工入库单不能转总账!");
					strb.append("<p>完工入库单[" + head.getDbillid() + "],不能转总账!</p>");
				} else if (head.getIsjz() != null && head.getIsjz().booleanValue()) { // 转总账
					printErrorLog(json, null, "已经转总账!");
					strb.append("<p>入库单[" + head.getDbillid() + "],已经转总账,不允许操作!</p>");
				} else if (head.getIsczg() != null && head.getIsczg().booleanValue()) {// 冲暂估
					printErrorLog(json, null, "冲暂估单据不允许操作!");
					strb.append("<p>入库单[" + head.getDbillid() + "],为冲暂估单据不允许操作!</p>");
				} else if (head.getIszg() != null && head.getIszg().booleanValue()) {// 暂估
					try {
						periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
						ic_purchinserv.saveIntradeHVOToZz(head, SystemUtil.getLoginCorpVo());
						flag++;

					} catch (Exception e) {
						printErrorLog(json, e, "转总账失败");
						strb.append("<p>入库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
					}
				} else {

					StringBuffer keys = new StringBuffer();
					if (head.getDbilldate() == null) {
						keys.append(" ");
					} else {
						keys.append(head.getDbilldate().toString().substring(0, 7));
					}
					keys.append(head.getPk_cust());
					if (head.getCbusitype() == null || IcConst.CGTYPE.equalsIgnoreCase(head.getCbusitype())) {
						keys.append(IcConst.CGTYPE);
					} else {
						keys.append(head.getCbusitype());
					}

					CorpVO corp =corpService.queryByPk(head.getPk_corp());
					boolean isChargedept = !StringUtil.isEmpty(corp.getChargedeptname())
							&& corp.getChargedeptname().equals("一般纳税人") ? true : false;
					if (head.getFp_style() == null) {
						if (isChargedept) {
							keys.append(IFpStyleEnum.SPECINVOICE.getValue());
						} else {
							keys.append(IFpStyleEnum.COMMINVOICE.getValue());
						}

					} else {
						keys.append(head.getFp_style());
					}
					if (map.containsKey(keys.toString())) {
						list = map.get(keys.toString());
					} else {
						list = new ArrayList<>();
					}
					list.add(head);
					map.put(keys.toString(), list);

				}
			}
			try {

				for (Map.Entry<String, List<IntradeHVO>> entry : map.entrySet()) {
					zlist = entry.getValue();
					if (zlist != null && zlist.size() > 0) {
						for (IntradeHVO head : zlist) {
							periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
						}
						ic_purchinserv.saveIntradeHVOToZz(zlist.toArray(new IntradeHVO[zlist.size()]),
								SystemUtil.getLoginCorpVo());
						flag++;
					}
				}
			} catch (Exception e) {
				printErrorLog(json, e, "转总账失败");
				if (zlist != null && zlist.size() > 0) {
					for (IntradeHVO vo : zlist) {
						strb.append("<p>入库单[" + vo.getDbillid() + "]," + json.getMsg() + "</p>");
					}
				}
			}
			if (flag > 0) {
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId());
				if (!StringUtil.isEmpty(msg)) {
					strb.append(msg);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, e, "转总账失败");
			strb.append(json.getMsg());
		}
		if (strb.length() == 0) {
			json.setSuccess(true);
			json.setMsg("转总账成功!");
		} else {
			json.setSuccess(false);
			json.setMsg(strb.toString());
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "入库单汇总转总账", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

    @PostMapping("/rollbackToZz")
	public ReturnData rollbackToZz(@RequestBody Map<String, String> param) {
		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
			IntradeHVO[] bodyvos = JsonUtils.deserialize(body,IntradeHVO[].class);
			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,取消转总账失败!");
			}
			securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
			Map<String, DZFBoolean> map = new HashMap<>();

			int flag = 0;
			List<String> periodSet = new ArrayList<String>();
			for (IntradeHVO head : bodyvos) {
				try {
					periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
					if (!map.containsKey(head.getPzid())) {
						ic_purchinserv.rollbackIntradeHVOToZz(head,SystemUtil.getLoginCorpVo());
						map.put(head.getPzid(), DZFBoolean.TRUE);
						flag++;
					}
				} catch (Exception e) {
					printErrorLog(json, e, "取消转总账失败");
					strb.append("<p>入库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
				}
			}

			if (flag > 0) {
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId());
				if (!StringUtil.isEmpty(msg)) {
					strb.append(msg);
				}
			}
		} catch (Exception e) {
			printErrorLog(json, e, "取消转总账失败");
			strb.append(json.getMsg());
		}
		if (strb.length() == 0) {
			json.setSuccess(true);
			json.setMsg("取消转总账成功!");
		} else {
			json.setSuccess(false);
			json.setMsg(strb.toString());
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "入库单取消转总账", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

    @GetMapping("/copy")
	public ReturnData copy(@RequestParam Map<String, String> param) {
        return queryIntradeBillVO(true,param);
	}

    @GetMapping("/modify")
	public ReturnData modify(@RequestParam Map<String, String> param) {
		return queryIntradeBillVO(false,param);
	}

	private ReturnData queryIntradeBillVO(boolean iscopy,@RequestParam Map<String, String> param) {
		Json json = new Json();
        String ignoreCheck = param.get("ignoreCheck");
        IntradeHVO vo = ic_purchinserv.queryIntradeHVOByID(param.get("id_ictrade_h"), SystemUtil.getLoginCorpId());
        if (!"Y".equals(ignoreCheck)) {
            ic_purchinserv.check(vo, SystemUtil.getLoginCorpId(), iscopy, false);
            if (iscopy) {
                vo.setPk_ictrade_h(null);
                vo.setDjzdate(null);
                vo.setImppzh(null);
                vo.setIsback(DZFBoolean.FALSE);
                vo.setIsczg(DZFBoolean.FALSE);
                vo.setIsjz(DZFBoolean.FALSE);
                vo.setPk_image_group(null);
                vo.setPk_image_library(null);
                vo.setPzid(null);
                vo.setPzh(null);
                vo.setSourcebillid(null);
                vo.setSourcebilltype(null);
                SuperVO[] childs = vo.getChildren();
                for (SuperVO child : childs) {
					child.setPrimaryKey(null);
                    child.setAttributeValue("pk_voucher", null);
                    child.setAttributeValue("imppzh", null);
                    child.setAttributeValue("pzh", null);
                    child.setAttributeValue("pk_voucher_b", null);
                }
            }
        }
        json.setSuccess(true);
        json.setData(vo);
        json.setMsg("查询成功!");
        return ReturnData.ok().data(json);
	}

    @GetMapping("/getbillno")
	public ReturnData getbillno(@RequestParam Map<String, String> param) {
		Json json = new Json();
        IntradeHVO data = JsonUtils.convertValue(param, IntradeHVO.class);// form提交保存
        if (data == null || data.getDbilldate() == null) {
            throw new BusinessException("数据为空,获取单据失败!");
        }
        String code = ic_purchinserv.getNewBillNo(SystemUtil.getLoginCorpId(), data.getDbilldate(), data.getCbusitype());
        json.setData(code);
        json.setSuccess(true);
        json.setMsg("获取单据成功!");
        return ReturnData.ok().data(json);
	}

	/**
	 * 打印操作
	 */
	@PostMapping("print")
	public void printAction(@RequestBody Map<String, String> pmap, HttpServletResponse response) {
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
			CorpVO corpvo = corpService.queryByPk(SystemUtil.getLoginCorpId());
			String chargedeptname = corpvo.getChargedeptname();
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体
			String title = "入 库 单";

			String[] columns = null;
			String[] columnnames = null;
			int[] widths = null;
			Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
			if ("一般纳税人".equals(chargedeptname)) {
				columns = new String[] { "cbusitype", "invclassname", "invname", "invspec", "measure", "nnum", "nprice",
						"nymny" };
				columnnames = new String[] { "入库类型", "存货分类", "存货名称", "规格(型号)", "计量单位", "数量", "单价", "金额" };
				widths = new int[] { 1, 1, 4, 2, 1, 2, 2, 2 };
			} else {
				columns = new String[] { "cbusitype", "invclassname", "invname", "invspec", "measure", "nnum", "nprice",
						"nymny" };
				columnnames = new String[] { "入库类型", "存货分类", "存货名称", "规格(型号)", "计量单位", "数量", "单价", "金额" };
				widths = new int[] { 1, 1, 4, 2,1, 2, 2, 2 };
			}
			boolean isCombin = false;

			if (printParamVO != null && !StringUtil.isEmpty(printParamVO.getIsmerge())
					&& printParamVO.getIsmerge().equals("Y")) {
				isCombin = true;
			}
            printReporUtil.setLineheight(22f);
			Map<String, String> invmaps = new HashMap<>();
			invmaps.put("isHiddenPzh", printParamVO.getIshidepzh());

            Map<String, List<SuperVO>> vomap = getVoMap(printParamVO);
			if (pmap.get("type").equals("3")) {// 发票纸模板打印
                printReporUtil.printICInvoice(vomap, null, title, columns, columnnames, widths, 20, invmaps, pmap, tmap);
			} else {
				if (!isCombin) {
                    printReporUtil.printHz(vomap, null, title, columns, columnnames, widths, 20, pmap.get("type"), invmaps, pmap, tmap);
				} else {
					if (pmap.get("type").equals("1"))
                        printReporUtil.printGroupCombin(vomap, title, columns, columnnames, null, widths, 20, pmap, invmaps); // A4纸张打印
					else if (pmap.get("type").equals("2"))
                        printReporUtil.printB5Combin(vomap, title, columns, columnnames, null, widths, 20, pmap, invmaps);
				}
			}
		} catch (DocumentException e) {
			log.error("入库单打印失败", e);
		} catch (IOException e) {
			log.error("入库单打印失败", e);
		} catch (Exception e) {
			log.error("入库单打印失败", e);
		}finally {
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("入库单打印错误", e);
			}
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "打印入库单", ISysConstants.SYS_2);
	}

    private Map<String, List<SuperVO>> getVoMap(PrintParamVO printParamVO) {
        String list = printParamVO.getList();

        String[] strs = list.split(",");

        Map<String, List<SuperVO>> vomap = new LinkedHashMap<>();
        AuxiliaryAccountBVO[] fzvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_SUPPLIER, SystemUtil.getLoginCorpId(), null);
        Map<String, AuxiliaryAccountBVO> aumap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(fzvos),
                new String[] { "pk_auacount_b" });
        for (String id : strs) {

            if (StringUtil.isEmpty(id))
                continue;
            IntradeHVO head = ic_purchinserv.queryIntradeHVOByID(id, SystemUtil.getLoginCorpId());
            if (head == null)
                continue;
            SuperVO[] bodyvos = head.getChildren();
            AuxiliaryAccountBVO custvo = aumap.get(head.getPk_cust());
            List<SuperVO> alist = new ArrayList<>();
            for (SuperVO body : bodyvos) {
                IctradeinVO ivo = (IctradeinVO) body;
                if (DZFValueCheck.isNotEmpty(custvo)) {
                    ivo.setCustname(custvo.getName());
                }

                ivo.setCreator(head.getCreator());
                ivo.setDbillid(head.getDbillid());
                // 如果子表凭证id和凭证号无，则从主表获取
                if (StringUtil.isEmpty(ivo.getPk_voucher())) {
                    ivo.setPk_voucher(head.getPzid());
                }
                if (StringUtil.isEmpty(ivo.getPzh())) {
                    ivo.setPzh(head.getPzh());
                }
                String cbusitype = ivo.getCbusitype();
                if (StringUtil.isEmpty(cbusitype)) {
                    ivo.setCbusitype("采购入库");
                } else {
                    if (cbusitype.equalsIgnoreCase(IcConst.WGTYPE)) {
                        ivo.setCbusitype("完工入库");
                    } else if (cbusitype.equalsIgnoreCase(IcConst.QTRTYPE)) {
                        ivo.setCbusitype("其他入库");
                    } else {
                        ivo.setCbusitype("采购入库");
                    }
                }
                alist.add(ivo);
            }

            IctradeinVO nvo = calTotal(bodyvos);
            alist.add(nvo);
            vomap.put(id, alist);
        }
        return vomap;
    }

	private IctradeinVO calTotal(SuperVO[] bodyvos) {
		// 计算合计行数据
		DZFDouble d1 = DZFDouble.ZERO_DBL;
		DZFDouble d2 = DZFDouble.ZERO_DBL;
		for (SuperVO body : bodyvos) {
			IctradeinVO ivo = (IctradeinVO) body;
			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(ivo.getNymny()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(ivo.getNnum()));
		}
		IctradeinVO nvo = new IctradeinVO();
		nvo.setKmmc(((IctradeinVO) bodyvos[0]).getKmmc());
		nvo.setCbusitype("合计");
		nvo.setNymny(d1);
		nvo.setNnum(d2);
		return nvo;
	}

    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response,  @RequestParam Map<String, String> pmap) {
		OutputStream toClient = null;
		try {

            PrintParamVO printParamVO =  JsonUtils.convertValue(pmap,PrintParamVO.class);
            if (StringUtil.isEmpty(printParamVO.getList())) {
                return;
            }
			String list = printParamVO.getList();
			AggIcTradeVO[] aggvos = null;
			String exName = null;
			boolean isexp = false;
			if (list.contains("download")) {
				aggvos = new AggIcTradeVO[1];
				exName = "入库单导入模板.xls";
				AggIcTradeVO aggvo = new AggIcTradeVO();
				aggvo.setVcorpname(SystemUtil.getLoginCorpVo().getUnitname());
				DZFDate billdate = new DZFDate(SystemUtil.getLoginDate());
				aggvo.setDbilldate(billdate.toString());
				aggvo.setDbillid(ic_purchinserv.getNewBillNo(SystemUtil.getLoginCorpId(), billdate, null));
				aggvos[0] = aggvo;
				isexp = true;
			} else {
//				String where = list.substring(2, list.length() - 1);
				aggvos = ic_purchinserv.queryAggIntradeVOByID(list, SystemUtil.getLoginCorpId());
				exName = "入库单.xls";
				List<AggIcTradeVO> tlist = calTotalRow(aggvos);
				aggvos = tlist.toArray(new AggIcTradeVO[tlist.size()]);
			}

			Map<String, Integer> preMap = getPreMap();// 设置精度

			response.reset();

			String formattedName = URLEncoder.encode(exName, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + exName + ";filename*=UTF-8''" + formattedName);
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			Map<String, Integer> tabidsheetmap = new HashMap<String, Integer>();
			tabidsheetmap.put("B100000", 0);
			IcBillExport exp = new IcBillExport();
			exp.exportExcel(aggvos, toClient, 1, isexp, preMap);
//			String srt2 = new String(length, "UTF-8");
//			response.addHeader("Content-Length", srt2);
			toClient.flush();
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("入库单excel导出错误", e);
		} catch (Exception e) {
			log.error("入库单excel导出错误", e);
		} finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (IOException e) {
				log.error("入库单excel导出错误", e);
			}
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("入库单excel导出错误", e);
			}
		}

//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI.getValue(), "导出入库单", ISysConstants.SYS_2);
	}

	private List<AggIcTradeVO> calTotalRow(AggIcTradeVO[] aggvos) {
		List<AggIcTradeVO> tlist = new ArrayList<>();
		if (aggvos != null && aggvos.length > 0) {
			Map<String, List<AggIcTradeVO>> map = hashlizeObjectByPk(aggvos);
			for (List<AggIcTradeVO> list : map.values()) {
				if (list == null || list.size() == 0)
					continue;
				AggIcTradeVO nvo = calTotal(list);
				for (AggIcTradeVO aggvo : list) {
					tlist.add(aggvo);
				}
				tlist.add(nvo);
			}
		}
		return tlist;
	}

	private Map<String, List<AggIcTradeVO>> hashlizeObjectByPk(AggIcTradeVO[] aggvos) throws BusinessException {
		Map<String, List<AggIcTradeVO>> result = new LinkedHashMap<>();
		if (aggvos != null && aggvos.length > 0) {
			for (int i = 0; i < aggvos.length; i++) {
				if (result.containsKey(aggvos[i].getPk_ictrade_h())) {
					((List<AggIcTradeVO>) result.get(aggvos[i].getPk_ictrade_h())).add(aggvos[i]);
				} else {
					List<AggIcTradeVO> list = new ArrayList<>();
					list.add(aggvos[i]);
					result.put(aggvos[i].getPk_ictrade_h(), list);
				}
			}
		}
		return result;
	}

	private AggIcTradeVO calTotal(List<AggIcTradeVO> list) {
		// 计算合计行数据
		DZFDouble d1 = DZFDouble.ZERO_DBL;
		DZFDouble d2 = DZFDouble.ZERO_DBL;
		DZFDouble d3 = DZFDouble.ZERO_DBL;
		DZFDouble d4 = DZFDouble.ZERO_DBL;
		for (AggIcTradeVO body : list) {
			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(body.getNymny()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(body.getNnum()));
			d3 = SafeCompute.add(d3, VoUtils.getDZFDouble(body.getNtaxmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d4 = SafeCompute.add(d4, VoUtils.getDZFDouble(body.getNtotaltaxmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
		}
		AggIcTradeVO nvo = new AggIcTradeVO();
		nvo.setDbilldate("合计");
		nvo.setNymny(d1);
		nvo.setNnum(d2);
		nvo.setNtaxmny(d3);
		nvo.setNtotaltaxmny(d4);
		return nvo;
	}

	private Map<String, Integer> getPreMap() {
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

    @PostMapping("/impExcel")
	public ReturnData impExcel(HttpServletRequest request) {
		Json json = new Json();
		json.setSuccess(false);
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile infile = multipartRequest.getFile("impfile");
        if (infile == null) {
            throw new BusinessException("请选择导入文件!");
        }
        String filename = infile.getOriginalFilename();
        int index = filename.lastIndexOf(".");
        String fileType = filename.substring(index + 1);
        String pk_corp = SystemUtil.getLoginCorpId();

        securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        String msg = ic_purchinserv.saveImp(infile, pk_corp, fileType, SystemUtil.getLoginUserId());
        if (StringUtil.isEmpty(msg)) {
            json.setMsg("入库单导入成功!");
            json.setSuccess(true);
        } else {
            json.setMsg(msg);
            json.setSuccess(false);
        }
        return ReturnData.ok().data(json);
	}

	private String checkJzMsg(List<String> periodSet, String pk_corp) {
		StringBuffer headMsg = ic_purchinserv.buildQmjzMsg(periodSet, pk_corp);
		if (headMsg != null && headMsg.length() > 0) {
			return headMsg.toString();
		}
		return null;
	}

}

package com.dzf.zxkj.platform.controller.icbill;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.enums.LogRecordEnum;
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
import com.dzf.zxkj.platform.exception.IcExBusinessException;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.icset.AggIcTradeVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.IntradeParamVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icbill.ISaleoutService;
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
 * 出库出库
 *
 */
@RestController
@RequestMapping("/icbill/saleoutact")
@Slf4j
public class SaleoutController extends BaseController {

	@Autowired
    ISaleoutService ic_saleoutserv;
	@Autowired
	private IParameterSetService parameterserv;
    @Autowired
    IPurchInService ic_purchinserv;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private ICorpService corpService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    // 查询
    @GetMapping("/query")
    public ReturnData query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        IntradeParamVO data = JsonUtils.convertValue(param, IntradeParamVO.class);
        int page = data.getPage();
        int rows = data.getRows();
        IntradeParamVO paramvo = getQueryParamVO(data);
        if (paramvo != null) {
            List<IntradeHVO> list = ic_saleoutserv.query(paramvo);
			IntradeHVO[] vos = null;
			grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
			if (list != null && list.size() > 0) {
				vos = getPagedZZVOs(list.toArray(new IntradeHVO[list.size()]), page, rows);
            }
			grid.setRows(vos == null ? new IntradeHVO[0] : vos);
			grid.setSuccess(true);
            grid.setMsg("查询成功！");
        }
        String begindate = null;
        String endate = null;
        DZFDate udate = new DZFDate();
        // 日志记录
        if (paramvo == null) {
            begindate = udate.toString();
            endate = udate.toString();
        } else {
            begindate = paramvo.getBegindate() == null ? udate.toString() : paramvo.getBegindate().toString();
            endate = paramvo.getEnddate() == null ? udate.toString() : paramvo.getEnddate().toString();
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI,
//                new StringBuffer().append("出库单查询:").append(begindate).append("-").append(endate).toString(),
//                ISysConstants.SYS_2);
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
				throw new BusinessException("查询出库单失败！" + "请填写正确的日期");
			}
		}
		return paramvo;
	}

	// 根据年和月得到月份的最后日期
	private DZFDate TrunLastDay(String selYear, String selMon) throws DZFWarpException {
		String peroid = selYear + "-" + selMon;
		return DateUtils.getPeriodEndDate(peroid);
	}

	@PostMapping("/saveSale")
	public ReturnData saveSale(@RequestBody Map<String, String> param) {
		Json json = new Json();
		IntradeHVO headvo = null;
		boolean isadd = false;
		try {
			String head = param.get("head");
			String body = param.get("body");
			String repeat = param.get("repeat");
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
			headvo = JsonUtils.deserialize(head, IntradeHVO.class);
			IntradeoutVO[] bodyvos =JsonUtils.deserialize(body,IntradeoutVO[].class); // 表体
			// 赋默认值
			// setDefaultValue(headvo, bodyvos);
			if (StringUtil.isEmpty(headvo.getPk_ictrade_h())) {
				isadd = true;
			}
			headvo.setCreator(SystemUtil.getLoginUserId());
			headvo.setPk_corp(SystemUtil.getLoginCorpId());
            checkSecurityData(new IntradeHVO[]{headvo},null,null,StringUtil.isEmpty(headvo.getPk_ictrade_h()));
			headvo.setChildren(bodyvos);
			if (DZFBoolean.valueOf(repeat).booleanValue()) {
				ic_saleoutserv.saveSale(headvo, false, false);
			} else {
				ic_saleoutserv.saveSale(headvo, true, false);
			}

			json.setSuccess(true);
			json.setStatus(200);
			json.setMsg("保存成功");
		} catch (IcExBusinessException ie) {
			List<IntradeoutVO> errList = ie.getErrList();
			json.setStatus(IICConstants.STATUS_RECONFM_CODE);
			json.setMsg("库存量不足");
			json.setRows(errList.toArray(new IntradeoutVO[0]));

		} catch (Exception e) {
			if (IICConstants.EXE_CONFIRM_CODE.equals(e.getMessage())) {
				json.setStatus(IICConstants.STATUS_RECONFM_CODE);
			}
			printErrorLog(json, e, "保存失败");
		}
		if (isadd) {
			if (headvo != null && !StringUtil.isEmpty(headvo.getDbillid())) {
				writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "新增出库单：" + headvo.getDbillid(),
						ISysConstants.SYS_2);
			}

		} else {
			if (headvo != null && !StringUtil.isEmpty(headvo.getDbillid())) {
				writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "修改出库单：" + headvo.getDbillid(),
						ISysConstants.SYS_2);
			}
		}
		return ReturnData.ok().data(json);
	}

	/**
	 * 查询子表信息
	 */
    @PostMapping("/querySub")
	public ReturnData querySub(@RequestBody Map<String, String> param) {
		Grid grid = new Grid();
        IntradeHVO  data= JsonUtils.convertValue(param, IntradeHVO.class);
        List<IntradeoutVO> list = ic_saleoutserv.querySub(data);
        if (list != null && list.size() > 0) {
            grid.setSuccess(true);
            grid.setTotal((long) list.size());
            grid.setRows(list);
            grid.setMsg("查询成功！");
        }else{
			grid.setMsg("查询失败！");
		}
        return ReturnData.ok().data(grid);
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
		IntradeHVO vo = ic_saleoutserv.queryIntradeHVOByID(param.get("id_ictrade_h"), SystemUtil.getLoginCorpId());
        checkSecurityData(new IntradeHVO[]{vo},null,null,true);
		if (!"Y".equals(ignoreCheck)) {
            ic_saleoutserv.check(vo, SystemUtil.getLoginCorpId(), iscopy);
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
                    if (vo.getCbusitype() == null || (IcConst.XSTYPE.equals(vo.getCbusitype()))) {
                        child.setAttributeValue("ncost", DZFDouble.ZERO_DBL);
                        child.setAttributeValue("vdef1", DZFDouble.ZERO_DBL);
                    }
				}
			}
		}
		json.setSuccess(true);
		json.setData(vo);
		json.setMsg("查询成功!");
		return ReturnData.ok().data(json);
	}

	/**
	 * 删除
	 */
    @PostMapping("/delSale")
	public ReturnData delSale(@RequestBody Map<String, String> param) {
		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		IntradeHVO[] bodyvos = null;
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
			bodyvos = JsonUtils.deserialize(body, IntradeHVO[].class);
			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,删除失败!!");
			}
			checkSecurityData(bodyvos,null,null);
			for (IntradeHVO head : bodyvos) {
				try {
					ic_saleoutserv.deleteSale(head, SystemUtil.getLoginCorpId());

				} catch (Exception e) {
					printErrorLog(json, e, "删除失败!");
					strb.append("<p>出库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
				}
			}
		} catch (Exception e) {
			printErrorLog(json, e, "删除失败!");
			strb.append("删除失败");
		}
		if (strb.length() == 0) {
			json.setSuccess(true);
			json.setMsg("删除成功!");
		} else {
			json.setSuccess(false);
			json.setMsg(strb.toString());
		}
		writeLogRecords(bodyvos, "删除出库单");
        return ReturnData.ok().data(json);
	}

	private void writeLogRecords(IntradeHVO[] bodyvos, String msg) {

		if (bodyvos == null || bodyvos.length == 0)
			return;
		if (bodyvos.length == 1) {
			writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, msg + ",单据号" + bodyvos[0].getDbillid() + ".",
					ISysConstants.SYS_2);
		} else {
			writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "批量删除出库单，单据号" + bodyvos[0].getDbillid() + "等.",
					ISysConstants.SYS_2);
		}

	}

	/**
	 * 转总账
	 */
    @PostMapping("/togl")
	public ReturnData togl(@RequestBody Map<String, String> param) {

		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
			IntradeHVO[] bodyvos = JsonUtils.deserialize(body, IntradeHVO[].class);

			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,转总账失败!");
			}
            checkSecurityData(bodyvos,null,null,true);
			String zy = "销售商品";
			List<String> periodSet = new ArrayList<String>();
			int flag = 0;
			boolean iscbtz = true;
			for (IntradeHVO head : bodyvos) {
				try {
					periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
					ic_saleoutserv.saveToGL(head, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserId(), zy);
					if (StringUtil.isEmpty(head.getCbusitype())
							|| !IcConst.CBTZTYPE.equalsIgnoreCase(head.getCbusitype())) {
						if (iscbtz) {
							iscbtz = false;
						}
					}
					flag++;
				} catch (Exception e) {
					printErrorLog(json, e, "转总账失败");
					strb.append("<p>出库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
				}
			}
			if (flag > 0) {
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId(), iscbtz);
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
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "出库单转总账", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

	/**
	 * 转总账
	 */
    @PostMapping("/toTotalGL")
	public ReturnData toTotalGL(@RequestBody Map<String, String> param) {

		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
            IntradeHVO[]  bodyvos = JsonUtils.deserialize(body, IntradeHVO[].class);
			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,转总账失败!");
			}
			String zy = "销售商品";
            checkSecurityData(bodyvos,null,null,true);
			Map<String, List<IntradeHVO>> map = new LinkedHashMap<String, List<IntradeHVO>>();
			List<IntradeHVO> list = null;
			List<IntradeHVO> zlist = null;
			List<String> periodSet = new ArrayList<String>();
			int flag = 0;
			boolean iscbtz = true;
			for (IntradeHVO head : bodyvos) {
				if (head.getIsjz() != null && head.getIsjz().booleanValue()) { // 转总账
					printErrorLog(json, null, "已经转总账!");
					strb.append("<p>出库单[" + head.getDbillid() + "],已经转总账,不允许操作!</p>");
				} else {

					StringBuffer keys = new StringBuffer();
					if (head.getDbilldate() == null) {
						keys.append(" ");
					} else {
						keys.append(head.getDbilldate().toString().substring(0, 7));
					}
					keys.append(head.getPk_cust());

					if (head.getCbusitype() == null || IcConst.XSTYPE.equalsIgnoreCase(head.getCbusitype())) {
						keys.append(IcConst.XSTYPE);
					} else {
						keys.append(head.getCbusitype());
					}
					CorpVO corp = corpService.queryByPk( head.getPk_corp());
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
							if (StringUtil.isEmpty(head.getCbusitype())
									|| !IcConst.CBTZTYPE.equalsIgnoreCase(head.getCbusitype())) {
								if (iscbtz) {
									iscbtz = false;
								}
							}
						}
						ic_saleoutserv.saveToGL(zlist.toArray(new IntradeHVO[zlist.size()]), SystemUtil.getLoginCorpId(),
								SystemUtil.getLoginUserId(), zy);
						flag++;
					}
				}
			} catch (Exception e) {
				printErrorLog(json, e, "转总账失败");
				if (zlist != null && zlist.size() > 0) {
					for (IntradeHVO vo : zlist) {
						strb.append("<p>出库单[" + vo.getDbillid() + "]," + json.getMsg() + "</p>");
					}
				}
			}
			if (flag > 0) {
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId(), iscbtz);
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
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "出库单汇总转总账", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

	/**
	 * 出库红字冲回
	 */
    @PostMapping("/dashBack")
	public ReturnData dashBack(@RequestBody Map<String, String> param) {
		Grid grid = new Grid();
		StringBuffer strb = new StringBuffer();
		try {
            IntradeHVO data =  JsonUtils.convertValue(param,IntradeHVO.class);
			if (data != null) {
				DZFDate dbdate = data.getDbilldate();
				if (dbdate != null) {
					DZFDate lgdate = DateUtils.getPeriodStartDate(SystemUtil.getLoginDate().substring(0, 7));
					if (dbdate.after(lgdate)) {
						throw new BusinessException("单据日期不晚于登录期间，请确认!");
					}
				}
                checkSecurityData(new IntradeHVO[]{data},null,null,true);
				ic_saleoutserv.saveDashBack(data, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserId(), SystemUtil.getLoginDate());
				List<String> periodSet = new ArrayList<String>();
				periodSet.add(SystemUtil.getLoginDate().substring(0, 7));
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId(), false);
				if (!StringUtil.isEmpty(msg)) {
					strb.append(msg);
				}
			} else {
				strb.append("出库红字冲回参数为空");
			}
		} catch (Exception e) {
			printErrorLog(grid, e, "出库红字冲回失败");
			if (IICConstants.EXE_CONFIRM_CODE.equals(e.getMessage())) {
				strb.append("出库红字冲回失败");
			} else {
				strb.append(e.getMessage());
			}
		}

		if (strb.length() == 0) {
			grid.setSuccess(true);
			grid.setMsg("出库红字冲回成功");
		} else {
			grid.setSuccess(false);
			grid.setMsg(strb.toString());
		}
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "出库单红字冲回", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
	}

	/**
	 * 获取单据编号
	 */

    @GetMapping("/getBillno")
	public ReturnData getBillno(@RequestParam Map<String, String> param) {
		Json json = new Json();
        IntradeHVO data =  JsonUtils.convertValue(param,IntradeHVO.class);
        if (data == null || data.getDbilldate() == null) {
            throw new BusinessException("数据为空,获取单据失败!");
        }
        String code = ic_saleoutserv.getNewBillNo(SystemUtil.getLoginCorpId(), data.getDbilldate(), data.getCbusitype());
        json.setData(code);
        json.setSuccess(true);
        json.setMsg("获取单据成功!");
        return ReturnData.ok().data(json);
	}

	/**
	 * 取消转总账
	 */
    @PostMapping("/rollbackTogl")
	public ReturnData rollbackTogl(@RequestBody Map<String, String> param) {

		Json json = new Json();
		StringBuffer strb = new StringBuffer();
		try {
			String body = param.get("head"); //
			body = body.replace("}{", "},{");
//			body = "[" + body + "]";
			IntradeHVO[] bodyvos =  JsonUtils.deserialize(body,IntradeHVO[].class);

			if (bodyvos == null || bodyvos.length == 0) {
				throw new BusinessException("数据为空,取消转总账失败!");
			}
            checkSecurityData(bodyvos,null,null,true);
			Map<String, DZFBoolean> map = new HashMap<>();
			List<String> periodSet = new ArrayList<String>();
			int flag = 0;
			boolean iscbtz = true;
			for (IntradeHVO head : bodyvos) {
				try {
					periodSet.add(DateUtils.getPeriod(head.getDbilldate()));
					if (!map.containsKey(head.getPzid())) {
						if (StringUtil.isEmpty(head.getCbusitype())
								|| !IcConst.CBTZTYPE.equalsIgnoreCase(head.getCbusitype())) {
							if (iscbtz) {
								iscbtz = false;
							}
						}
						ic_saleoutserv.rollbackTogl(head, SystemUtil.getLoginCorpId());
						map.put(head.getPzid(), DZFBoolean.TRUE);
					}
					flag++;
				} catch (Exception e) {
					printErrorLog(json, e, "取消转总账失败");
					strb.append("<p>出库单[" + head.getDbillid() + "]," + json.getMsg() + "</p>");
				}
			}
			if (flag > 0) {
				String msg = checkJzMsg(periodSet, SystemUtil.getLoginCorpId(), iscbtz);
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
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "出库单取消转总账", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

    @GetMapping("/queryBYH")
	public ReturnData queryBYH(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
        IntradeHVO data =  JsonUtils.convertValue(param,IntradeHVO.class);
        if (!StringUtil.isEmptyWithTrim(data.getPk_ictrade_h())) {
            IntradeHVO vo = ic_saleoutserv.queryIntradeHVOByID(data.getPk_ictrade_h(), SystemUtil.getLoginCorpId());
            if (vo != null) {
                grid.setTotal(1L);
                // grid.setRows(list);
                List<IntradeHVO> list = new ArrayList<IntradeHVO>();
                list.add(vo);
                grid.setRows(list);
            } else {
                grid.setTotal((long) 0);
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        }
        return ReturnData.ok().data(grid);
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
			Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存title
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));// 设置表头字体

			String title = "出 库 单";
			String[] columns = new String[] { "cbusitype", "invclassname", "invname", "invspec", "measure",
					"nnum", "nprice", "ncost" };
			String[] columnnames = new String[] { "出库类型", "存货分类", "存货名称", "规格(型号)", "计量单位", "数量", "成本单价", "成本金额" };
			int[] widths = new int[] { 1, 1, 4, 2, 1, 2, 2, 2 };

			boolean isCombin = false;
			if (printParamVO != null && !StringUtil.isEmpty(printParamVO.getIsmerge())
					&& (printParamVO.getIsmerge().equals("Y") || printParamVO.getIsmerge().equals("true"))) {
				isCombin = true;
			}
            printReporUtil.setLineheight(22f);
			Map<String,String> invmaps = new HashMap<>();
			if(printParamVO != null && !StringUtil.isEmpty(printParamVO.getIshidepzh())
					&& (printParamVO.getIshidepzh().equals("Y") || printParamVO.getIshidepzh().equals("true"))){
				invmaps.put("isHiddenPzh","Y");
			}else{
				invmaps.put("isHiddenPzh","N");
			}
//			会计
			if(!pmap.get("ishidekj").equals("true")){
                pmap.put("会计","");
			}
			//库管员
			if(!pmap.get("ishidekgy") .equals("true")){
                pmap.put("库管员",pmap.get("ishidekgyname"));
			}
            Map<String, List<SuperVO>> vomap = getVoMap(printParamVO);
			if(pmap.get("type").equals("3")){//发票纸模板打印
                printReporUtil.printICInvoice(vomap, null, title, columns, columnnames, widths, 20,invmaps, pmap, tmap);
			}else{
				if (!isCombin) {
                    printReporUtil.printHz(vomap, null, title, columns, columnnames, widths, 20, pmap.get("type"), invmaps,pmap, tmap);
				} else {
					if (pmap.get("type").equals("1"))
                        printReporUtil.printGroupCombin(vomap, title, columns, columnnames, null, widths, 20, pmap,invmaps); // A4纸张打印
					else if (pmap.get("type").equals("2"))
                        printReporUtil.printB5Combin(vomap, title, columns, columnnames, null, widths, 20, pmap,invmaps);
				}
			}
		} catch (DocumentException e) {
			log.error("出库单打印失败", e);
		} catch (IOException e) {
			log.error("出库单打印失败", e);
		} catch (Exception e) {
			log.error("出库单打印失败", e);
		} finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("出库单打印失败", e);
            }
        }
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "打印出库单", ISysConstants.SYS_2);
	}
    private Map<String, List<SuperVO>> getVoMap(PrintParamVO printParamVO) {
        String list = printParamVO.getList();
        String[] strs = list.split(",");
        String priceStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF010);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
        Map<String, List<SuperVO>> vomap = new LinkedHashMap<>();

        AuxiliaryAccountBVO[] fzvos =gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_CUSTOMER, SystemUtil.getLoginCorpId(), null);
        Map<String, AuxiliaryAccountBVO> aumap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(fzvos), new String[]{"pk_auacount_b"});
        for (String id : strs) {

            if (StringUtil.isEmpty(id))
                continue;
            IntradeHVO head = ic_saleoutserv.queryIntradeHVOByID(id, SystemUtil.getLoginCorpId());
            if (head == null)
                continue;
            AuxiliaryAccountBVO custvo = 	aumap.get(head.getPk_cust());
            SuperVO[] bodyvos = head.getChildren();
            List<SuperVO> alist = new ArrayList<>();
            for (SuperVO body : bodyvos) {
                IntradeoutVO ivo = (IntradeoutVO) body;

                if(DZFValueCheck.isNotEmpty(custvo)){
                    ivo.setCustname(custvo.getName());
                }
                if (StringUtil.isEmpty(ivo.getPk_voucher())) {
                    ivo.setPk_voucher(head.getPzid());
                }
                if (StringUtil.isEmpty(ivo.getPzh())) {
                    ivo.setPzh(head.getPzh());
                }
                ivo.setCreator(head.getCreator());
                ivo.setDbillid(head.getDbillid());
                String cbusitype = ivo.getCbusitype();
                if (StringUtil.isEmpty(cbusitype)) {
                    ivo.setCbusitype("销售出库");
                } else {
                    if (cbusitype.equalsIgnoreCase(IcConst.LLTYPE)) {
                        ivo.setCbusitype("领料出库");
                    } else if (cbusitype.equalsIgnoreCase(IcConst.QTCTYPE)) {
                        ivo.setCbusitype("其他出库");
                    } else {
                        ivo.setCbusitype("销售出库");
                    }
                }
                if (ivo.getNcost() != null) {
                    ivo.setNprice(SafeCompute.div(ivo.getNcost(), ivo.getNnum()).setScale(price, 0));// 设置成本单价
                }
                alist.add(ivo);
            }
            IntradeoutVO nvo = calTotal(bodyvos);
            alist.add(nvo);
            vomap.put(id, Arrays.asList(alist.toArray(new SuperVO[alist.size()])));
        }
        return vomap;
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
		nvo.setKmmc(((IntradeoutVO) bodyvos[0]).getKmmc());
		nvo.setCbusitype("合计");
		nvo.setNcost(d1);
		nvo.setNnum(d2);
		return nvo;
	}

	@PostMapping("/expExcel")
	public void expExcel(HttpServletResponse response,  @RequestParam Map<String, String> pmap) {
		OutputStream toClient = null;
        String title = "";
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
                title ="出库单导入模板";
				aggvos = new AggIcTradeVO[1];
				AggIcTradeVO aggvo = new AggIcTradeVO();
				aggvo.setVcorpname(SystemUtil.getLoginCorpVo().getUnitname());
				DZFDate billdate = new DZFDate(SystemUtil.getLoginDate());
				aggvo.setDbilldate(billdate.toString());
				aggvo.setDbillid(ic_saleoutserv.getNewBillNo(SystemUtil.getLoginCorpId(), billdate, null));
				aggvos[0] = aggvo;
				isexp = true;
			} else {
//				String where = list.substring(2, list.length() - 1);
				aggvos = ic_saleoutserv.queryAggIntradeVOByID(list, SystemUtil.getLoginCorpId());
                title ="出库单";
				List<AggIcTradeVO> tlist = calTotalRow(aggvos);
				aggvos = tlist.toArray(new AggIcTradeVO[tlist.size()]);
			}

			Map<String, Integer> preMap = getPreMap();// 设置精度

			response.reset();
            exName = title+".xls";
			String formattedName = URLEncoder.encode(exName, "UTF-8");
			response.addHeader("Content-Disposition",
					"attachment;filename=" + exName + ";filename*=UTF-8''" + formattedName);
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = null;
			Map<String, Integer> tabidsheetmap = new HashMap<String, Integer>();
			tabidsheetmap.put("B100000", 0);
			IcBillExport exp = new IcBillExport();
			exp.exportExcel(aggvos, toClient, 0, isexp, preMap);
//			String srt2 = new String(length, "UTF-8");
//			response.addHeader("Content-Length", srt2);
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
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (IOException e) {
				log.error("出库单excel导出错误", e);
			}
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "导出"+title, ISysConstants.SYS_2);
		}
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
		DZFDouble d5 = DZFDouble.ZERO_DBL;
		for (AggIcTradeVO body : list) {
			d1 = SafeCompute.add(d1, VoUtils.getDZFDouble(body.getNymny()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d2 = SafeCompute.add(d2, VoUtils.getDZFDouble(body.getNnum()));
			d3 = SafeCompute.add(d3, VoUtils.getDZFDouble(body.getNtaxmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d4 = SafeCompute.add(d4, VoUtils.getDZFDouble(body.getNcost()).setScale(2, DZFDouble.ROUND_HALF_UP));
			d5 = SafeCompute.add(d5, VoUtils.getDZFDouble(body.getNtotaltaxmny()).setScale(2, DZFDouble.ROUND_HALF_UP));
		}
		AggIcTradeVO nvo = new AggIcTradeVO();
		nvo.setDbilldate("合计");
		nvo.setNymny(d1);
		nvo.setNnum(d2);
		nvo.setNtaxmny(d3);
		nvo.setNcost(d4);
		nvo.setNtotaltaxmny(d5);
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
        String msg = ic_saleoutserv.saveImp(infile, pk_corp, fileType, SystemUtil.getLoginUserId());
        if (StringUtil.isEmpty(msg)) {
            json.setSuccess(true);
        } else {
            json.setMsg(msg);
            json.setSuccess(true);
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_BUSI, "导入出库单", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

	private String checkJzMsg(List<String> periodSet, String pk_corp, boolean iscbtz) {

		StringBuffer headMsg = null;
		if (iscbtz) {
			headMsg = ic_saleoutserv.buildQmjzMsg(periodSet, pk_corp);
		} else {
			headMsg = ic_purchinserv.buildQmjzMsg(periodSet, pk_corp);
		}
		if (headMsg != null && headMsg.length() > 0) {
			return headMsg.toString();
		}
		return null;
	}
}

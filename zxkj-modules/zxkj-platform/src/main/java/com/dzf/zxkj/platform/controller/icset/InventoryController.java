package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.report.IQueryLastNum;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.dzf.zxkj.platform.util.SystemUtil.getRequest;

/**
 * 
 * 存货
 *
 */
@RestController
@RequestMapping("/icset/inventoryact")
@Slf4j
public class InventoryController {

	@Autowired
	private IInventoryService iservice;
	@Autowired
	private IQueryLastNum ic_rep_cbbserv;
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private ISecurityService securityserv;
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private ICorpService corpService;

	@GetMapping("/queryInfoByKmId")
	public ReturnData queryInfoByKmId(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		InventoryVO queryParamvo = JsonUtils.convertValue(param, InventoryVO.class);
		if (StringUtil.isEmpty(queryParamvo.getPk_subject())) {// kmid == null ||
										// kmid.trim().length() == 0
			grid.setSuccess(false);
			grid.setTotal(0L);
			grid.setRows(new ArrayList<InventoryVO>());
			return ReturnData.error().data(grid);
		}
		List<InventoryVO> list = iservice.query(SystemUtil.getLoginCorpId(), queryParamvo.getPk_subject());
		if (queryParamvo != null && queryParamvo.getIsshow() != null && queryParamvo.getIsshow().booleanValue()) {
			String vchDate = param.get("vdate");

			DZFDate vDate = new DZFDate();
			if (!StringUtil.isEmpty(vchDate)) {
				vDate = new DZFDate(vchDate);
			}
			setNjzValue(list, vDate);
		}
		log.info("查询成功！");
		if (list == null)
			list = new ArrayList<InventoryVO>();
		grid.setTotal(Long.valueOf(list.size()));
		grid.setRows(list);
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	private void setNjzValue(List<InventoryVO> list, DZFDate date) {

		Map<String, IcbalanceVO> balMap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(date.toString(), SystemUtil.getLoginCorpId(),
				null, true);
		if (list != null && list.size() > 0) {
			for (InventoryVO vo : list) {
				if (balMap != null && balMap.size() > 0) {
					IcbalanceVO balvo = balMap.get(vo.getPk_inventory());
					if (balvo != null) {
						vo.setNjzmny(balvo.getNcost());
						vo.setNjznum(balvo.getNnum());
					}
				}
			}
		}
	}
	@GetMapping("/queryInfo")
	public ReturnData queryInfo(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		InventoryVO data = JsonUtils.convertValue(param, InventoryVO.class);
		String pk_invclassify = param.get("pk_invclassify");
		List<InventoryVO> list = iservice.queryInfo(SystemUtil.getLoginCorpId(), pk_invclassify);

		String filtervalue = param.get("filtervalue");

		if (!DZFValueCheck.isEmpty(filtervalue)) {
			list = filterList(list, filtervalue);
		}
		if (data != null && data.getIsshow() != null && data.getIsshow().booleanValue()) {
			setJcInfo(list);
		}
		if (data != null && data.getIspage() != null && data.getIspage().booleanValue()) {
			int page = data.getPage();
			int rows = data.getRows();
			if (list != null && list.size() > 0) {
				grid.setTotal((long) list.size());
				InventoryVO[] PzglPagevos = getPagedZZVOs(list.toArray(new InventoryVO[list.size()]), page, rows);
				grid.setRows(Arrays.asList(PzglPagevos));
			}
		} else {
			if (list != null && list.size() > 0) {
				grid.setTotal((long) list.size());
				grid.setRows(list);
			}
		}
		grid.setMsg("查询成功！");
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
	}

	private List<InventoryVO> filterList(List<InventoryVO> list,String value) {
		List<InventoryVO> tlist = new ArrayList<>();
		if (!DZFValueCheck.isEmpty(list)) {
			for (InventoryVO vo : list) {
				if (DZFValueCheck.isEmpty(vo))
					continue;
				if ((!DZFValueCheck.isEmpty(vo.getCode()) && vo.getCode().indexOf(value) >= 0)
						|| (!DZFValueCheck.isEmpty(vo.getName()) && vo.getName().indexOf(value) >= 0)
						|| (!DZFValueCheck.isEmpty(vo.getInvspec()) && vo.getInvspec().indexOf(value) >= 0)
						) {
					tlist.add(vo);
				}
			}
		}
		return tlist;
	}

	// 将查询后的结果分页
	private InventoryVO[] getPagedZZVOs(InventoryVO[] PzglPagevos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= PzglPagevos.length) {// 防止endIndex数组越界
			endIndex = PzglPagevos.length;
		}
		PzglPagevos = Arrays.copyOfRange(PzglPagevos, beginIndex, endIndex);
		return PzglPagevos;
	}

	public void setJcInfo(List<InventoryVO> list){

		if (list == null  || list.size() == 0)
			return;
		String vchStr = getRequest().getParameter("vdate");
		DZFDate vDate = new DZFDate();

		if (!StringUtil.isEmpty(vchStr)) {
			vDate = new DZFDate(vchStr);
		}
		Map<String, IcbalanceVO> balMap = ic_rep_cbbserv.queryLastBanlanceVOs_byMap1(vDate.toString(),
				SystemUtil.getLoginCorpId(), null, true);

		// 新模式模式 启用库存
		CorpVO corpVo = (CorpVO) corpService.queryByPk(SystemUtil.getLoginCorpId());// 防止vo信息有变化
		Map<String, IcbalanceVO> balMap1 = ic_rep_cbbserv.queryLastBanlanceVOs_byMap4(vDate.toString(),
				SystemUtil.getLoginCorpId(), null, true);
		String numStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF009);
		int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
		String priceStr = parameterserv.queryParamterValueByCode(SystemUtil.getLoginCorpId(), IParameterConstants.DZF010);
		int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		for (InventoryVO vo : list) {
			if (corpVo.getIbuildicstyle() != null && corpVo.getIbuildicstyle() == 1) {// 新模式库存
				if (balMap != null && balMap.size() > 0) {
					IcbalanceVO balvo = balMap.get(vo.getPk_inventory());
					if (balvo != null) {
						//
						vo.setNjznum(balvo.getNnum()==null?balvo.getNnum():new DZFDouble(balvo.getNnum().toString(),num));
					}

					IcbalanceVO balvo1 = balMap1.get(vo.getPk_inventory());
					if (balvo1 != null) {
						if ((vo.getNjznum() == null || vo.getNjznum().doubleValue() == 0)
								&& (vo.getNjzmny() == null || vo.getNjzmny().doubleValue() == 0)) {
						} else {
							vo.setNcbprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum())
									.setScale(price, 2));
							vo.setNjzmny(balvo1.getNcost());
							vo.setJsprice(SafeCompute.div(balvo1.getNcost(), balvo1.getNnum())
									.setScale(price, 2));
						}
					}

				}
			} else {
				if ((vo.getNjznum() == null || vo.getNjznum().doubleValue() == 0)
						&& (vo.getNjzmny() == null || vo.getNjzmny().doubleValue() == 0)) {
				} else {
					vo.setNcbprice(SafeCompute.div(vo.getNjzmny(), vo.getNjznum()).setScale(price, 2));
					//vo.setJsprice(SafeCompute.div(vo.getNjzmny(), vo.getNjznum()).setScale(price, 2));
				}
			}
		}
	}

	@GetMapping("/queryInfoBypage")
	public ReturnData queryInfoBypage(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		InventoryVO data = JsonUtils.convertValue(param, InventoryVO.class);
		int page = data.getPage();
		int rows = data.getRows();
		if (page < 1 || rows < 1) {
			throw new BusinessException("查询失败！");
		}
		List<InventoryVO> list = iservice.queryInfo(SystemUtil.getLoginCorpId(), null, data);
		log.info("查询成功！");
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));

		if (list != null && list.size() > 0) {
			InventoryVO[] pvos = getPageVOs(list.toArray(new InventoryVO[list.size()]), page, rows);
			list = Arrays.asList(pvos);
		}

		grid.setRows(list == null ? new ArrayList<InventoryVO>() : list);
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
	}

	@GetMapping("/queryInfo_kcsp")
	public ReturnData queryInfo_kcsp() {
		Grid grid = new Grid();
		List<InventoryVO> list = iservice.querysp(SystemUtil.getLoginCorpId());
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
		grid.setRows(list == null ? new ArrayList<InventoryVO>() : list);
		grid.setSuccess(true);
		return ReturnData.ok().data(grid);
	}

	@PostMapping("/save")
	public ReturnData save(@RequestParam Map<String, String> param) {
		Json json = new Json();
		InventoryVO[] bodyvos;
		String spInfo =param.get("body"); // 获得前台传进来的
		if (!StringUtil.isEmpty(spInfo)) {
			spInfo = spInfo.replace("}{", "},{"); // 修改格式，对象之间用 "," 分隔
			spInfo = "[" + spInfo + "]"; // 最外层加上中括号，转化为json数组的格式
			bodyvos = JsonUtils.convertValue(spInfo, InventoryVO[].class);
		} else {
			bodyvos = new InventoryVO[] {  JsonUtils.convertValue(param, InventoryVO.class)  };// form提交保存
			json.setRows(bodyvos[0]);
		}
		String pk_corp = SystemUtil.getLoginCorpId(); // 获取公司主键
		setAddDefaultValue(bodyvos); // 设置公司名、创建时间、创建者
		String action = param.get("action"); // 获得前台传进来的
		securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		String ids = param.get("ids");
		// 存货合并
		if (!StringUtil.isEmpty(ids)) {
			InventoryVO rtndata = iservice.saveMergeData(pk_corp, ids,bodyvos);
			json.setData(rtndata);
		} else {
			// 参照新增保存 节点单行修改保存
			if (!StringUtil.isEmpty(action) && "add".equals(action)) {
				InventoryVO[] rtndata = iservice.save1(pk_corp, bodyvos);
				json.setData(rtndata);
			} else {
				// 节点保存
				iservice.save(pk_corp, bodyvos); // 保存数据，返回一个标志位，如果是true添加成功，false为失败
			}
		}
		json.setSuccess(true);
		json.setMsg("保存成功");
		return ReturnData.ok().data(json);
	}

	@PostMapping("/batchSave")
	public ReturnData batchSave(@RequestParam Map<String, String> param) {
		Json json = new Json();
		String firstCode = null;
		String firstName = null;
		int total = 0;

		String ids = param.get("ids");
		String codes = param.get("codes");
		String names = param.get("names");
		String[] idsArr = ids.split(",");
		if (idsArr.length < 1) {
			throw new BusinessException("您未选择要更新的行数据");
		}
		firstCode = codes.split(",")[0];
		firstName = names.split(",")[0];
		total = idsArr.length;
		String spflid = param.get("splxidbatch");
		String unit = param.get("jldwidbatch");
		String spec = param.get("specbatch");
		String invtype = param.get("typebatch");
		if (StringUtil.isEmpty(invtype) && StringUtil.isEmpty(spflid) && StringUtil.isEmpty(unit)
				&& StringUtil.isEmpty(spec)) {
			throw new BusinessException("没有可以修改的数据");
		}

		String pk_corp = param.get("pk_corp");
		if (StringUtil.isEmpty(pk_corp)) {
			pk_corp = SystemUtil.getLoginCorpId();
		} else {
			securityserv.checkSecurityForSave(pk_corp, SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		}

		InventoryVO update = new InventoryVO();
		update.setPk_invclassify(spflid);
		update.setPk_measure(unit);
		update.setInvspec(spec);
//			update.setInvtype(invtype);
		String susflag = iservice.updateBatch(pk_corp, ids, update);
		if (!StringUtil.isEmpty(susflag)) {
			// 日志记录
//			writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//					"存货档案_批量修改存货成功 : 编码：" + firstCode + "， 名称：" + firstName + "，等" + total + "条；",
//					ISysConstants.SYS_2);
			log.info("保存成功");
			json.setMsg("保存成功");
			json.setSuccess(true);
		} else {
//			writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//					"存货档案_批量修改存货失败 : 编码：" + firstCode + "， 名称：" + firstName + "，等" + total + "条；",
//					ISysConstants.SYS_2);
			log.info("保存失败");
			json.setMsg("保存失败");
			json.setSuccess(false);
		}
		return ReturnData.ok().data(json);
	}

	@PostMapping("/mergeData")
	public ReturnData mergeData(@RequestParam Map<String, String> param) {
		Json json = new Json();
		String spid = param.get("spid");
		String pk_corp = param.get("pk_corp");
		if (StringUtil.isEmpty(pk_corp)) {
			pk_corp = SystemUtil.getLoginCorpId();
		} else {
			securityserv.checkSecurityForSave(pk_corp, SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		}
		String body = param.get("body"); // 子表
		body = body.replace("}{", "},{");
		body = "[" + body + "]";
		InventoryVO[] bodyvos = JsonUtils.convertValue(body, InventoryVO[].class);
		if (DZFValueCheck.isEmpty(bodyvos)) {
			throw new BusinessException("被合并的存货不允许为空!");
		}
		if(DZFValueCheck.isEmpty(spid))
			throw new BusinessException("合并的存货不允许为空!");
		InventoryVO vo = iservice.saveMergeData(pk_corp, spid, bodyvos);
		json.setMsg("存货合并成功");
		json.setSuccess(true);

//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "存货合并", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}

	private void setAddDefaultValue(InventoryVO[] vos) {
		if (vos == null || vos.length == 0)
			return;
		for (InventoryVO v : vos) {
			if (v != null && StringUtil.isEmpty(v.getPk_inventory())) {//// 每条数据不为空，主键不为空
				v.setPk_corp(SystemUtil.getLoginCorpId()); // 设置公司
				v.setCreatetime(new DZFDateTime(new Date())); // 设置创建时间
				v.setCreator(SystemUtil.getLoginUserId()); // 设置创建者
			}
		}
	}

	// 删除记录
	@PostMapping("/onDelete")
	public ReturnData onDelete(@RequestParam Map<String, String[]> param) {
		String[] paramValues = param.get("ids[]");
		String[] pk_corps = param.get("gss[]");
		Json json = new Json();
		if (paramValues != null && paramValues.length != 0) {
			for (String pk : pk_corps) {
				if (!SystemUtil.getLoginCorpId().equals(pk)) {
					json.setSuccess(false);
					json.setMsg("您无操作权限！");
					return ReturnData.error().data(json);
				}
			}
			securityserv.checkSecurityForDelete(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
			String errmsg = iservice.deleteBatch(paramValues, SystemUtil.getLoginCorpId());
			json.setSuccess(true);
			if (StringUtil.isEmpty(errmsg)) {
				json.setMsg("删除成功!");
			} else {
				json.setMsg(errmsg);
			}
		} else {
			json.setSuccess(false);
			json.setMsg("删除失败");
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "删除存货", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}

	// 查询特定科目信息
	@GetMapping("/queryBySpecialKM")
	public ReturnData queryBySpecialKM() {
		Grid grid = new Grid();
		List<InventoryVO> list = iservice.querySpecialKM(SystemUtil.getLoginCorpId());// corpVo.getPk_corp()
		log.info("查询成功！");
		grid.setRows(list == null ? new ArrayList<InventoryVO>() : list);
		grid.setSuccess(true);
		grid.setMsg("查询成功");
		return ReturnData.ok().data(grid);
	}

	@PostMapping("/impExcel")
	public ReturnData impExcel(HttpServletRequest request) {
		Json json = new Json();
		json.setSuccess(false);
		String userid =SystemUtil.getLoginUserId();
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		MultipartFile infile = multipartRequest.getFile("impfile");
		if (infile == null) {
			throw new BusinessException("请选择导入文件!");
		}
		String filename = infile.getOriginalFilename();
		int index = filename.lastIndexOf(".");
		String fileType = filename.substring(index + 1);
		String pk_corp = SystemUtil.getLoginCorpId();
		securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		String msg = iservice.saveImp(infile, pk_corp, fileType, userid);
		json.setMsg(msg);
		json.setSuccess(true);
		return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "导入存货", ISysConstants.SYS_2);
	}

	// 将查询后的结果分页
	private InventoryVO[] getPageVOs(InventoryVO[] pageVos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= pageVos.length) {// 防止endIndex数组越界
			endIndex = pageVos.length;
		}
		pageVos = Arrays.copyOfRange(pageVos, beginIndex, endIndex);
		return pageVos;
	}

	// 查询单据号
	@GetMapping("/queryDjCode")
	public ReturnData queryDjCode() {
		Json grid = new Json();
		String invcode = yntBoPubUtil.getInventoryCode(SystemUtil.getLoginCorpId());
		log.info("获取单据号成功！");
		grid.setData(invcode);
		grid.setSuccess(true);
		grid.setMsg("获取单据号成功");
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	@PostMapping("/createPrice")
	public ReturnData createPrice(@RequestParam Map<String, String> param) {
		Json json = new Json();
		String pk_corp = param.get("pk_corp");
		if (StringUtil.isEmpty(pk_corp)) {
			pk_corp = SystemUtil.getLoginCorpId();
		} else {
			securityserv.checkSecurityForSave(pk_corp, SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		}
		String bili = param.get("bili");
		String priceway = param.get("priceway");
		String vchStr = param.get("vdate");
		if (StringUtil.isEmpty(priceway)) {
			throw new BusinessException("生成结算价的规则不允许为空!");
		}
		if("2".equals(priceway)){
			if (StringUtil.isEmpty(bili)) {
				throw new BusinessException("销售平均单价的比例不能为空!");
			}
		}

		String body = param.get("body"); // 子表
		body = body.replace("}{", "},{");
		body = "[" + body + "]";
		InventoryVO[] bodyvos = JsonUtils.convertValue(body, InventoryVO[].class);
		if (DZFValueCheck.isEmpty(bodyvos)) {
			throw new BusinessException("生成结算价的存货不允许为空!");
		}
		iservice.createPrice(pk_corp,priceway,bili, vchStr,bodyvos);
		json.setMsg("生成结算价成功");
		json.setSuccess(true);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "生成结算价", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}


}

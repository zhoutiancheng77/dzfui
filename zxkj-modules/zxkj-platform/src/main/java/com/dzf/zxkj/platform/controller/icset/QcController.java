package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.icset.IQcService;
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
 * 库存期初
 *
 */

@RestController
@RequestMapping("/icset/qcact")
@Slf4j
public class QcController {

	@Autowired
	private IQcService iservice;
	@Autowired
	private ISecurityService securityserv;
	@Autowired
	private IParameterSetService parameterserv;

	@GetMapping("/queryInfo")
	public ReturnData queryInfo(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
		QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
		int page = queryParamvo.getPage();
		int rows = queryParamvo.getRows();
		if (page < 1 || rows < 1) {
			throw new BusinessException("查询失败！");
		}
		List<IcbalanceVO> list = iservice.quyerInfovoic(SystemUtil.getLoginCorpId());
		String searchField = getRequest().getParameter("searchField");
		list = filterData(list, searchField);
		log.info("查询成功！");
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));

		if (list != null && list.size() > 0) {
			IcbalanceVO[] pvos = getPageVOs(list.toArray(new IcbalanceVO[list.size()]), page, rows);
			list = Arrays.asList(pvos);
		}
		grid.setRows(list == null ? new ArrayList<IcbalanceVO>() : list);
		grid.setSuccess(true);
		grid.setMsg("查询成功");
		return ReturnData.ok().data(grid);
	}

	private List<IcbalanceVO> filterData(List<IcbalanceVO> list, String name) {
		if (StringUtil.isEmpty(name) || list == null || list.size() == 0)
			return list;

		List<IcbalanceVO> newList = new ArrayList<IcbalanceVO>();
		String inInvenName = null;
		for (IcbalanceVO vo : list) {
			inInvenName = vo.getInventoryname();

			if (!StringUtil.isEmpty(inInvenName) && inInvenName.contains(name)) {
				newList.add(vo);
			}
		}
		return newList;
	}

	// 保存(包含新增 修改)
	@PostMapping("/onUpdate")
	public ReturnData onUpdate(@RequestParam Map<String, String> param) {
		Json json = new Json();
		IcbalanceVO[] bodyvos;
		String spInfo =param.get("body"); // 获得前台传进来的
		if (!StringUtil.isEmpty(spInfo)) {
			spInfo = spInfo.replace("}{", "},{"); // 修改格式，对象之间用 "," 分隔
			spInfo = "[" + spInfo + "]"; // 最外层加上中括号，转化为json数组的格式
			bodyvos = JsonUtils.convertValue(spInfo, IcbalanceVO[].class);
		} else {
			bodyvos = new IcbalanceVO[] { JsonUtils.convertValue(param, IcbalanceVO.class) };// form提交保存
			json.setRows(bodyvos[0]);
		}
		String pk_corp = SystemUtil.getLoginCorpId(); // 获取公司主键
		securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		setAddDefaultValue(bodyvos); // 设置公司名、创建时间、创建者
		iservice.save(pk_corp, bodyvos,SystemUtil.getLoginUserId()); // 保存数据
		json.setSuccess(true);
		json.setMsg("保存成功");
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "库存期初设置", 2);
		return ReturnData.ok().data(json);
	}

	private void setAddDefaultValue(IcbalanceVO[] vos) {
		if (vos == null || vos.length == 0)
			return;
		for (IcbalanceVO v : vos) {
			if (v != null && StringUtil.isEmpty(v.getPk_icbalance())) {//// 每条数据不为空，主键不为空
				v.setPk_corp(SystemUtil.getLoginCorpId()); // 设置公司
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
			if(StringUtil.isEmpty(errmsg)){
				json.setMsg("删除成功!");
			}else{
				json.setMsg(errmsg);
			}
		} else {
			json.setSuccess(false);
			json.setMsg("您无操作权限！");
			return ReturnData.error().data(json);
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "删除库存期初", ISysConstants.SYS_2);
//		writeJson(json);
		return ReturnData.ok().data(json);
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

		CorpVO corpvo = SystemUtil.getLoginCorpVo();
		DZFDate icdate = corpvo.getIcbegindate();
		securityserv.checkSecurityForSave(SystemUtil.getLoginCorpId(), SystemUtil.getLoginCorpId(),SystemUtil.getLoginUserId());
		String msg = iservice.saveImp(infile, corpvo.getPk_corp(), fileType, userid, icdate);
		json.setMsg(msg);
		json.setSuccess(true);
		return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "导入库存期初", ISysConstants.SYS_2);
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
}

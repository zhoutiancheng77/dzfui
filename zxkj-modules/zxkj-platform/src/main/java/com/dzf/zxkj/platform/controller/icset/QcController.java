package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icset.IQcService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

import static com.dzf.zxkj.platform.util.SystemUtil.getRequest;

/**
 * 库存期初
 *
 */

@RestController
@RequestMapping("/icset/qcact")
@Slf4j
public class QcController  extends BaseController {

	@Autowired
	private IQcService iservice;
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
		grid.setTotal(Long.valueOf(list == null ? 0 : list.size()));
		if (list != null && list.size() > 0) {
			IcbalanceVO[] pvos = getPageVOs(list.toArray(new IcbalanceVO[list.size()]), page, rows);
			list = Arrays.asList(pvos);
		}
		grid.setRows(list == null ? new ArrayList<IcbalanceVO>() : list);
		grid.setSuccess(true);
		grid.setMsg("查询成功");
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET,"库存期初查询", ISysConstants.SYS_2);
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
	public ReturnData onUpdate(@RequestBody Map<String, String> param) {
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
		setAddDefaultValue(bodyvos); // 设置公司名、创建时间、创建者
        if(bodyvos != null && bodyvos.length>0){
            checkSecurityData(bodyvos,null,null, !StringUtil.isEmpty(bodyvos[0].getPk_icbalance()));
        }
		iservice.save(pk_corp, bodyvos,SystemUtil.getLoginUserId()); // 保存数据
		json.setSuccess(true);
		json.setMsg("保存成功");
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "库存期初设置", 2);
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
	public ReturnData onDelete(@RequestBody Map<String, String> param) {
		String paramValues = param.get("ids");
		String pk_corp = param.get("pk_corp");
		Json json = new Json();
        checkSecurityData(null,new String[]{pk_corp},null);
		String[] pkss = DZFStringUtil.getString2Array(paramValues, ",");
		if (DZFValueCheck.isEmpty(pkss)){
			throw new BusinessException("数据为空,删除失败!");
		}
		String errmsg = iservice.deleteBatch(pkss, SystemUtil.getLoginCorpId());
		json.setSuccess(true);
		if(StringUtil.isEmpty(errmsg)){
			json.setMsg("删除成功!");
		}else{
			json.setMsg(errmsg);
		}
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "删除库存期初", ISysConstants.SYS_2);
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
		String msg = iservice.saveImp(infile, corpvo.getPk_corp(), fileType, userid, icdate);
		json.setMsg(msg);
		json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "导入库存期初", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
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

	@PostMapping("/expExcel")
	public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> pmap) {
		OutputStream toClient = null;
		try {
			response.reset();
			String  fileName = "kucunqichu.xls";
			// 设置response的Header
			String date = "库存期初模板";
			String exName = new String(date.getBytes("GB2312"), "ISO_8859_1");
			response.addHeader("Content-Disposition", "attachment;filename=" + new String(exName + ".xls"));
			toClient = new BufferedOutputStream(response.getOutputStream());
			response.setContentType("application/vnd.ms-excel;charset=gb2312");
			byte[] length = expExcel(toClient, fileName);

			String srt2 = new String(length, "UTF-8");
			response.addHeader("Content-Length", srt2);
			toClient.flush();
			response.getOutputStream().flush();
		} catch (IOException e) {
			log.error("excel导出错误", e);
		} catch (Exception e) {
			log.error("excel导出错误", e);
		} finally {
			try {
				if (toClient != null) {
					toClient.close();
				}
			} catch (Exception e) {
				log.error("excel导出错误", e);
			}
			try {
				if (response != null && response.getOutputStream() != null) {
					response.getOutputStream().close();
				}
			} catch (Exception e) {
				log.error("excel导出错误", e);
			}
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "导出库存期初模板", ISysConstants.SYS_2);
		}
	}

	private byte[] expExcel(OutputStream out, String fileName) throws Exception {
		ByteArrayOutputStream bos = null;
		InputStream is = null;
		try {
			Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + fileName);
			is = exportTemplate.getInputStream();
			bos = new ByteArrayOutputStream();
			if (fileName.indexOf(".xlsx") > 0) {
				XSSFWorkbook xworkbook = new XSSFWorkbook(is);
				is.close();
				bos = new ByteArrayOutputStream();
				xworkbook.write(bos);
			} else {
				HSSFWorkbook gworkbook = new HSSFWorkbook(is);
				is.close();
				bos = new ByteArrayOutputStream();
				gworkbook.write(bos);
			}
			bos.writeTo(out);
			return bos.toByteArray();
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			throw e;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (IOException e) {
				}
			}
		}
	}
}

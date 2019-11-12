package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.icset.InvclassifyVO;
import com.dzf.zxkj.platform.service.icset.IInvclassifyService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * 存货类别
 */
@RestController
@RequestMapping("/icset/invclassify")
@Slf4j
public class InvclassifyController {

	@Autowired
	private IInvclassifyService ic_inclsserv = null;

	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	@GetMapping("/queryInv")
	public ReturnData queryInv() {
		List<InvclassifyVO> list = null;
		Grid grid = new Grid();
		list = ic_inclsserv.queryByPkcorp(InvclassifyVO.class, SystemUtil.getLoginCorpId());//
		if (list != null && list.size() > 0) {
			// log.info("查询成功！");
			grid.setSuccess(true);
			grid.setTotal((long) list.size());
			grid.setRows(list);
		} else {
			grid.setSuccess(false);
		}
		return ReturnData.ok().data(grid);
	}

	@PostMapping("/save")
	public ReturnData save(@RequestParam Map<String, String> param) {
		Json json = new Json();
		InvclassifyVO data = JsonUtils.convertValue(param, InvclassifyVO.class);
		if (data != null) {
			// 验证 根据主键校验为当前公司的记录
			String pk_corp =SystemUtil.getLoginCorpId();
			// 修改保存前数据安全验证
			String primaryKey = data.getPrimaryKey();
			if (!StringUtil.isEmpty(primaryKey)) {
				InvclassifyVO getvo = ic_inclsserv.queryByPrimaryKey(primaryKey);
				if (getvo != null && !pk_corp.equals(getvo.getPk_corp())) {
					throw new BusinessException("出现数据无权问题，无法修改！");
				}
			}
			data.setPk_corp(pk_corp);
			ic_inclsserv.save(data);
			json.setData(data);
			json.setSuccess(true);
			json.setMsg("成功");
		} else {
			json.setSuccess(false);
			json.setMsg("失败");
		}
		return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "存货分类设置", ISysConstants.SYS_2);
	}

	@GetMapping("/query")
	public ReturnData query() {
		Grid grid = new Grid();
		List<InvclassifyVO> list;
		list = ic_inclsserv.query(SystemUtil.getLoginCorpId());
		if (list != null && list.size() > 0) {
			grid.setTotal((long) list.size());
			grid.setSuccess(true);
			grid.setRows(list);
		} else {
			log.info("查询数据为空");
			grid.setSuccess(false);
		}
		return ReturnData.ok().data(grid);
	}

	@PostMapping("/delete")
	public ReturnData delete(@RequestParam Map<String, String> param) {
		Json json = new Json();
		InvclassifyVO data = JsonUtils.convertValue(param, InvclassifyVO.class);
		if (data != null) {
			// InvclassifyVO vo = (InvclassifyVO)data;
			// 验证 根据主键校验为当前公司的记录
			if (!data.getPk_corp().equals(SystemUtil.getLoginCorpId())) {
				throw new BusinessException("无权操作！");
			}
			// 删除前数据安全验证
			InvclassifyVO getvo = ic_inclsserv.queryByPrimaryKey(data.getPrimaryKey());
			if (getvo != null && !SystemUtil.getLoginCorpId().equals(getvo.getPk_corp())) {
				throw new BusinessException("出现数据无权问题，无法删除！");
			}
			ic_inclsserv.delete(data);
			json.setSuccess(true);
			json.setRows(data);
			json.setMsg("成功");
		} else {
			json.setSuccess(false);
			json.setMsg("失败");
		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "删除存货分类", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
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
		String pk_corp =SystemUtil.getLoginCorpId();
		String msg = ic_inclsserv.saveImp(infile, pk_corp, fileType);
		json.setMsg(msg);
		json.setSuccess(true);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "导入存货分类", ISysConstants.SYS_2);
		return ReturnData.ok().data(json);
	}

	// 查询单据号
	@GetMapping("/queryDjCode")
	public ReturnData queryDjCode() {
		Json grid = new Json();
		String invcode = yntBoPubUtil.getInvclCode(SystemUtil.getLoginCorpId());
		log.info("获取单据号成功！");
		grid.setData(invcode);
		grid.setSuccess(true);
		grid.setMsg("获取单据号成功");
		return ReturnData.ok().data(grid);
	}
}

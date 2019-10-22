package com.dzf.zxkj.platform.controller.salary;

import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.gzgl.ISalaryBaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * 工资基数设置
 */
@RestController
@RequestMapping("/salary/gl_gzbbase")
@Slf4j
public class SalaryBaseController {

	@Autowired
	private ISalaryBaseService gl_gzbbaseserv;

	@Autowired
	private ISecurityService securityserv;

//	public void query() {
//		Json json = new Json();
//		int page = data.getPage();
//		int rows = data.getRows();
//		try {
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			String isfenye = getRequest().getParameter("isfenye");
//
//			if ("Y".equals(isfenye)) {// 分页
//				QueryPageVO pagevo = gl_gzbbaseserv.queryBodysBypage(pk_corp, qj, page, rows);
//				json.setTotal(Long.valueOf(pagevo.getTotal()));
//				json.setRows(pagevo.getPagevos());
//			} else {
//				SalaryBaseVO[] vos = gl_gzbbaseserv.query(pk_corp, qj);// 查询工资表基数
//				if (vos == null || vos.length == 0) {
//					vos = new SalaryBaseVO[0];
//				}
//				json.setRows(vos);
//			}
//			json.setMsg("查询成功");
//			json.setSuccess(true);
//
//			log.info("查询成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			// json.setSuccess(false);
//			printErrorLog(json, log, e, "查询失败！");
//		}
//		writeJson(json);
//	}
//
//	public void save() {
//		Json json = new Json();
//
//		try {
//			String[] strArr = getRequest().getParameterValues("strArr");
//
//			if (strArr == null || strArr.length == 0) {
//				throw new BusinessException("数据为空");
//			}
//
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			securityserv.checkSecurityForSave(pk_corp, getLogincorppk(), getLoginUserid());
//
//			Map<String, String> fieldMapping = FieldMapping.getFieldMapping(new SalaryBaseVO());
//			List<SalaryBaseVO> list = new ArrayList<>();
//			SalaryBaseVO vo1 = null;
//			JSON js = null;
//			for (String str : strArr) {
//				js = (JSON) JSON.parse(str);
//				vo1 = DzfTypeUtils.cast(js, fieldMapping, SalaryBaseVO.class, JSONConvtoJAVA.getParserConfig());
//				list.add(vo1);
//			}
//			SalaryBaseVO[] vo = gl_gzbbaseserv.save(pk_corp, list, qj);
//
//			json.setMsg("社保公积金保存成功");
//			log.info("社保公积金保存成功");
//			json.setRows(vo);
//			json.setSuccess(true);
//
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "保存失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "社保公积金保存", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void delete() {
//		Json json = new Json();
//
//		try {
//			String qj = getRequest().getParameter("opdate");
//
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			String pk_corp = getRequest().getParameter("ops");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//			String ids = getRequest().getParameter("pks");
//			if (StringUtil.isEmpty(ids)) {
//				throw new BusinessException("删除数据为空");
//			}
//
//			securityserv.checkSecurityForDelete(pk_corp, getLogincorppk(), getLoginUserid());
//
//			SalaryBaseVO[] vo = gl_gzbbaseserv.delete(pk_corp, ids, qj);
//			json.setRows(vo);
//			json.setMsg("删除成功");
//			json.setSuccess(true);
//			log.info("查询成功");
//		} catch (Exception e) {
//			// log.error("失败!" , e);
//			printErrorLog(json, log, e, "删除失败！");
//		}
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "工资表删除", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void getSalaryAccSet() {
//		Json json = new Json();
//		try {
//
//			String pk_corp = getRequest().getParameter("pk_corp");
//			if (StringUtil.isEmpty(pk_corp)) {
//				throw new BusinessException("公司为空");
//			}
//
//			String cpersonids = getRequest().getParameter("cpersonids");
//			String opdate = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(opdate)) {
//				throw new BusinessException("期间为空");
//			}
//			SalaryReportVO vos = gl_gzbbaseserv.getSalarySetInfo(pk_corp, cpersonids, opdate);
//
//			json.setMsg("获取工资科目设置成功");
//			json.setRows(vos);
//			json.setSuccess(true);
//			log.info("获取工资科目设置成功");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "获取工资设置信息失败！");
//		}
//		writeJson(json);
//	}
//
//	public void changeNum() {
//		Json json = new Json();
//		try {
//			String qj = getRequest().getParameter("opdate");
//			if (StringUtil.isEmpty(qj))
//				throw new BusinessException("期间为空");
//
//			qj = qj.substring(0, 7);
//			String ids = getRequest().getParameter("ids");
//
//			String chgdata = getRequest().getParameter("chgdata");
//			if (!StringUtil.isEmpty(chgdata)) {
//				chgdata = chgdata.replaceAll("data.", "");
//				JSONObject array = (JSONObject) JSON.parseObject(chgdata);
//				Map<String, String> bodymapping = FieldMapping.getFieldMapping(new SalaryBaseVO());
//				SalaryBaseVO basevo = DzfTypeUtils.cast(array, bodymapping, SalaryBaseVO.class,
//						JSONConvtoJAVA.getParserConfig());
//
//				Map<String, String> bodymapping1 = FieldMapping.getFieldMapping(new SalaryAccSetVO());
//				SalaryAccSetVO accsetvo = DzfTypeUtils.cast(array, bodymapping1, SalaryAccSetVO.class,
//						JSONConvtoJAVA.getParserConfig());
//				if (DZFValueCheck.isEmpty(basevo.getPk_corp())) {
//					basevo.setPk_corp(getLogincorppk());
//				}
//				String pk_corp = basevo.getPk_corp();
//				securityserv.checkSecurityForSave(pk_corp, getLogincorppk(), getLoginUserid());
//
//				gl_gzbbaseserv.saveChangeNum(pk_corp, getLoginUserid(), ids, basevo, accsetvo, qj);
//			}
//			json.setMsg("调整基数成功");
//			json.setSuccess(true);
//			log.info("调整基数成功");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "调整基数失败！");
//		}
//
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "调整基数", ISysConstants.SYS_2);
//		writeJson(json);
//	}
//
//	public void changeNumGroup() {
//		Json json = new Json();
//		try {
//			String chgdata = getRequest().getParameter("chgdata");
//			if (!StringUtil.isEmpty(chgdata)) {
//				chgdata = chgdata.replaceAll("data.", "");
//				JSONObject array = (JSONObject) JSON.parseObject(chgdata);
//				Map<String, String> bodymapping = FieldMapping.getFieldMapping(new SalaryAccSetVO());
//				SalaryAccSetVO accsetvo = DzfTypeUtils.cast(array, bodymapping, SalaryAccSetVO.class,
//						JSONConvtoJAVA.getParserConfig());
//				securityserv.checkSecurityForSave(getLogincorppk(), getLogincorppk(), getLoginUserid());
//
//				gl_gzbbaseserv.saveChangeNumGroup(getLogincorppk(), getLoginUserid(), accsetvo);
//			}
//			json.setMsg("统一基数设置成功");
//			json.setSuccess(true);
//			log.info("统一基数设置成功");
//		} catch (Exception e) {
//			printErrorLog(json, log, e, "统一基数设置失败！");
//		}
//
//		writeLogRecord(LogRecordEnum.OPE_KJ_SALARY.getValue(), "调整基数", ISysConstants.SYS_2);
//		writeJson(json);
//	}
}

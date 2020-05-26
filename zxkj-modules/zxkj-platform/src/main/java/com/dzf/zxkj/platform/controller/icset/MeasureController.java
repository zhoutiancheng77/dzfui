package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.ExcelReport;
import com.dzf.zxkj.platform.util.SystemUtil;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 * 计量单位
 *
 */
@RestController
@RequestMapping("/icset/meausreact")
@Slf4j
public class MeasureController extends BaseController {
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;
	@Autowired
	private IMeasureService ims;

	@GetMapping("/query")
	public ReturnData queryInv(@RequestParam Map<String, String> param) {
		List<MeasureVO> list = null;
		Grid grid = new Grid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
		String sort = queryParamvo.getSort();
		if (StringUtil.isEmpty(sort))
			sort = "code";
		list = ims.quyerByPkcorp(SystemUtil.getLoginCorpId(), sort, queryParamvo.getOrder());
		if (list != null && list.size() > 0) {
			log.info("查询成功！");
			grid.setSuccess(true);
			grid.setTotal((long) list.size());
			grid.setRows(list);
		} else {
			grid.setSuccess(false);
		}
//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET,"计量单位查询", ISysConstants.SYS_2);
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	@GetMapping("/queryInfo")
	public ReturnData queryInfo(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        String sort = queryParamvo.getSort();
        if (StringUtil.isEmpty(sort))
            sort = "code";
        // 查询并分页
        List<MeasureVO> list = ims.quyerByPkcorp(SystemUtil.getLoginCorpId(), sort, queryParamvo.getOrder());
        // list变成数组
        grid.setTotal((long) (list == null ? 0 : list.size()));
        // 分页
        MeasureVO[] vos = null;
        if (list != null && list.size() > 0) {
            String isfenye = param.get("isfenye");
            if("Y".equals(isfenye)) {
                int page = queryParamvo.getPage();
                int rows = queryParamvo.getRows();
                SuperVO[] vos1 = list.toArray(new MeasureVO[list.size()]);
                vos = (MeasureVO[])getPageVOs(vos1, page, rows);
            }else{
                vos = list.toArray(new MeasureVO[list.size()]);
            }
        }
        grid.setRows(vos == null ? new ArrayList<MeasureVO>() : Arrays.asList(vos));
        grid.setSuccess(true);
        grid.setMsg("查询成功");
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	private MeasureVO[] getPagedZZVOs(MeasureVO[] vos, int page, int rows) {
		int beginIndex = rows * (page - 1);
		int endIndex = rows * page;
		if (endIndex >= vos.length) {// 防止endIndex数组越界
			endIndex = vos.length;
		}
		vos = Arrays.copyOfRange(vos, beginIndex, endIndex);
		return vos;
	}

	// 修改保存
    @PostMapping("/onUpdate")
	public ReturnData onUpdate(@RequestBody Map<String, String> param) {
		Json json = new Json();
        List<MeasureVO> list = new ArrayList<>();
        DZFBoolean isAddNew = DZFBoolean.FALSE;
        MeasureVO lastVo = JsonUtils.convertValue(param, MeasureVO.class);
        if (StringUtil.isEmpty(lastVo.getPrimaryKey())) {
            isAddNew = DZFBoolean.TRUE;
        }
        list.add(lastVo);
        if (!isAddNew.booleanValue()) {
            checkSecurityData(list.toArray(new MeasureVO[list.size()]),null,null,true);
        }
//        String spInfo = getRequest().getParameter("action"); // 获得前台传进来的
        // 参照新增保存
        if (isAddNew.booleanValue()) {
            MeasureVO[] rtndata = ims.savenNewVOArr(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), list);
            json.setData(rtndata);
        } else {
            // 节点保存
            ims.updateVOArr(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(),list);
        }

        json.setStatus(200);
        json.setSuccess(true);
        json.setMsg("保存成功！");
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "计量单位设置", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
//
	}

	// 删除记录
    @PostMapping("/onDelete")
	public ReturnData onDelete(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String paramValues = param.get("ids");

        String[] pkss = DZFStringUtil.getString2Array(paramValues, ",");
        if (DZFValueCheck.isEmpty(pkss)){
            throw new BusinessException("数据为空,删除失败!");
        }
        String errmsg = ims.deleteBatch(pkss, SystemUtil.getLoginCorpId());
        if (StringUtil.isEmpty(errmsg)) {
            json.setSuccess(true);
        } else {
            json.setSuccess(true);
            json.setMsg(errmsg);
        }
		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "删除计量单位", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}
    @PostMapping("/impExcel")
	public ReturnData impExcel(HttpServletRequest request) {
		Json json = new Json();
		json.setSuccess(false);

		String userid = SystemUtil.getLoginUserId();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile infile = multipartRequest.getFile("impfile");
        if (infile == null) {
            throw new BusinessException("请选择导入文件!");
        }
        String filename = infile.getOriginalFilename();
        int index = filename.lastIndexOf(".");
        String fileType = filename.substring(index + 1);
        String pk_corp = SystemUtil.getLoginCorpId();
        String msg = ims.saveImp(infile, pk_corp, fileType, userid);
        json.setMsg(msg);
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "导入计量单位", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
	}

	// 查询单据号
    @GetMapping("/queryDjCode")
	public ReturnData queryDjCode() {
		Json grid = new Json();
        String invcode = yntBoPubUtil.getMeasureCode(SystemUtil.getLoginCorpId());
        log.info("获取单据号成功！");
        grid.setData(invcode);
        grid.setSuccess(true);
        grid.setMsg("获取单据号成功");
        return ReturnData.ok().data(grid);
	}

    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> pmap) {
        OutputStream toClient = null;
        try {
            response.reset();
            String  fileName = "jiliangdanwei.xls";
            // 设置response的Header
            String date = "计量单位";
            String formattedName = URLEncoder.encode(date, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName+ ".xls");
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            ExcelReport<AuxiliaryAccountBVO> ex = new ExcelReport<>();
            ex.expFile(toClient, fileName);
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
            writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET, "导出计量单位模板", ISysConstants.SYS_2);
        }
    }

}

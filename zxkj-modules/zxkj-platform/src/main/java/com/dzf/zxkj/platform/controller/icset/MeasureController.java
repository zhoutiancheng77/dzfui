package com.dzf.zxkj.platform.controller.icset;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static com.dzf.zxkj.platform.util.SystemUtil.getRequest;

/**
 * 
 * 计量单位
 *
 */
@RestController
@RequestMapping("/icset/meausreact")
@Slf4j
public class MeasureController{
	@Autowired
	private IYntBoPubUtil yntBoPubUtil;

	@Autowired
	private IMeasureService ims;
	@Autowired
	private ISecurityService securityserv;

	@GetMapping("/query")
	public ReturnData queryInv(@RequestParam Map<String, String> param) {
		List<MeasureVO> list = null;
		Grid grid = new Grid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
		String sort = queryParamvo.getSort();
		if (StringUtil.isEmpty(sort))
			sort = "pk_measure";
		list = ims.quyerByPkcorp(SystemUtil.getLoginCorpId(), sort, queryParamvo.getOrder());
		if (list != null && list.size() > 0) {
			log.info("查询成功！");
			grid.setSuccess(true);
			grid.setTotal((long) list.size());
			grid.setRows(list);
		} else {
			grid.setSuccess(false);
		}
		return ReturnData.ok().data(grid);
//		writeJson(grid);
	}

	@GetMapping("/queryInfo")
	public ReturnData queryInfo(@RequestParam Map<String, String> param) {
		Grid grid = new Grid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        // 查询并分页
        List<MeasureVO> list = ims.quyerByPkcorp(SystemUtil.getLoginCorpId(), queryParamvo.getSort(), queryParamvo.getOrder());
        // list变成数组
        grid.setTotal((long) (list == null ? 0 : list.size()));
        // 分页
        MeasureVO[] vos = null;
        if (list != null && list.size() > 0) {
            vos = getPagedZZVOs(list.toArray(new MeasureVO[0]), queryParamvo.getPage(), queryParamvo.getRows());
        }
        log.info("查询成功！");
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
	public ReturnData onUpdate(@RequestBody Map<String, String[]> param) {
		Json json = new Json();
        String[] strArr = param.get("strArr[]");
        List<MeasureVO> list = new ArrayList<>();
        DZFBoolean isAddNew = DZFBoolean.FALSE;
        MeasureVO lastVo = JsonUtils.convertValue(strArr[strArr.length - 1], MeasureVO.class);
        if (StringUtil.isEmpty(lastVo.getPrimaryKey())) {
            isAddNew = DZFBoolean.TRUE;
        }
        MeasureVO vo = null;
        for (String str : strArr) {
            vo =  JsonUtils.convertValue(str, MeasureVO.class);
            list.add(vo);
        }
        if (!isAddNew.booleanValue()) {
            securityserv.checkSecurityForSave(list.get(0).getPk_corp(), SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        }
        String spInfo = getRequest().getParameter("action"); // 获得前台传进来的
        // 参照新增保存
        if (!StringUtil.isEmpty(spInfo) && "add".equals(spInfo)) {
            MeasureVO[] rtndata = ims.savenNewVOArr(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), list);
            json.setData(rtndata);
        } else {
            // 节点保存
            ims.updateVOArr(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(),list);
        }

        json.setStatus(200);
        json.setSuccess(true);
        json.setMsg("保存成功！");
        return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "计量单位设置", ISysConstants.SYS_2);
	}

	// 删除记录
    @PostMapping("/onDelete")
	public ReturnData onDelete(@RequestBody Map<String, String> param) {
		// MeasureVO msvo = super.getActionVO(MeasureVO.class);
		Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        // 验证 根据主键校验为当前公司的记录
        MeasureVO data = JsonUtils.convertValue(param, MeasureVO.class);
        if (!data.getPk_corp().equals(pk_corp)) {
            throw new BusinessException("无权操作！");
        }
        securityserv.checkSecurityForDelete(MeasureVO.class, data.getPrimaryKey(), data.getPk_corp(), pk_corp,
                SystemUtil.getLoginUserId());
        ims.delete(data);
        json.setSuccess(true);
        json.setStatus(200);
        json.setRows(data);
        json.setMsg("成功");
        return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "删除计量单位", ISysConstants.SYS_2);
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
        return ReturnData.ok().data(json);
//		writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(), "导入计量单位", ISysConstants.SYS_2);
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

}

package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventoryQcVO;
import com.dzf.zxkj.platform.service.glic.IInventoryQcService;
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
 * 存货期初
 *
 */
@RestController
@RequestMapping("/glic/gl_icinvqc")
@Slf4j
public class InvtoryQcController{
    @Autowired
    private IInventoryQcService gl_ic_invtoryqcserv;

    @GetMapping("/query")
    public ReturnData query() {
        Grid grid = new Grid();
        List<InventoryQcVO> vos = gl_ic_invtoryqcserv.query(SystemUtil.getLoginCorpId());
        if (vos != null && vos.size() > 0) {
            grid.setRows(vos);
            grid.setMsg("查询成功");
        } else {
            grid.setMsg("查询数据为空");
        }
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestParam Map<String, String> param) {
        Json json = new Json();
        InventoryQcVO data = JsonUtils.convertValue(param, InventoryQcVO.class);
        data.setPk_corp(SystemUtil.getLoginCorpId());
        InventoryQcVO vo = gl_ic_invtoryqcserv.save(SystemUtil.getLoginUserId(),SystemUtil.getLoginCorpId(),data);
        json.setRows(vo);
        json.setMsg("保存成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//                "总账存货管理-存货期初，保存", ISysConstants.SYS_2);
    }

    @PostMapping("/onDelete")
    public ReturnData onDelete(@RequestBody Map<String, String> map) {
        Json json = new Json();
        String rows = map.get("rows");
        if (DZFValueCheck.isEmpty(rows)) {
            throw new BusinessException("数据为空,删除失败!");
        }
        InventoryQcVO[] vos = JsonUtils.deserialize(rows, InventoryQcVO[].class);
        if (vos == null || vos.length == 0) {
            throw new BusinessException("数据为空,删除失败!");
        }
        gl_ic_invtoryqcserv.delete(vos);
        json.setMsg("删除成功");
        json.setSuccess(true);

//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//                "总账存货管理-存货期初，删除", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/sync")
    public ReturnData sync(@RequestParam("date") String date) {
        Json json = new Json();
        gl_ic_invtoryqcserv.processSyncData(SystemUtil.getLoginUserId(),
                SystemUtil.getLoginCorpId(), date);
        json.setMsg("同步成功");
        json.setSuccess(true);
//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//                "总账存货管理-存货期初，同步", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/updateDate")
    public ReturnData updateDate(@RequestParam("date") String date) {
        Json json = new Json();
        gl_ic_invtoryqcserv.updateDate( SystemUtil.getLoginCorpId(), date);
        json.setMsg("修改成功");
        json.setSuccess(true);
//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//                "总账存货管理-存货期初，修改启用期间", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/importExcel")
    public ReturnData importExcel(HttpServletRequest request) {
        Json json = new Json();
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile infile = multipartRequest.getFile("impfile");
        if (infile == null) {
            throw new BusinessException("请选择导入文件!");
        }

        String date = multipartRequest.getParameter("date");
        String fileName = infile.getOriginalFilename();
        String   fileType =fileName.substring(fileName.lastIndexOf(".") + 1,
                fileName.length());

        String msg = gl_ic_invtoryqcserv.processImportExcel(SystemUtil.getLoginCorpVo(),
                SystemUtil.getLoginUserId(), fileType, infile, date);
        json.setMsg(msg);
        json.setSuccess(true);

//        writeLogRecord(LogRecordEnum.OPE_KJ_IC_SET.getValue(),
//                "总账存货管理-存货期初，导入", ISysConstants.SYS_2);
//        writeJson(json);
        return ReturnData.ok().data(json);
    }
}
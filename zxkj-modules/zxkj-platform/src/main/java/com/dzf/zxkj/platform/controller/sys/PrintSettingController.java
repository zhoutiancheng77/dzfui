package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.PrintSettingVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IPrintSettingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gl_printsetting")
@Slf4j
public class PrintSettingController {
    @Autowired
    private IPrintSettingService gl_print_setting_serv;

    @PostMapping("query")
    public ReturnData<Grid> query(@MultiRequestBody String nodeName, @MultiRequestBody String pk_corp, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Grid<PrintSettingVO> json = new Grid();
        try {
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = corpVO.getPk_corp();
            }
            PrintSettingVO vo = gl_print_setting_serv.query(pk_corp, userVO.getCuserid(), nodeName);
            json.setRows(vo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            json.setMsg("查询失败");
            json.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("save")
    public ReturnData<Grid> save(String pk_corp, PrintSettingVO printSettingVO, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Grid json = new Grid();
        try {
            if (StringUtil.isEmpty(pk_corp)) {
                pk_corp = corpVO.getPk_corp();
            }
            printSettingVO.setPk_corp(pk_corp);
            printSettingVO.setCuserid(userVO.getCuserid());
            gl_print_setting_serv.save(printSettingVO);
            json.setMsg("保存成功");
            json.setRows(printSettingVO);
            json.setSuccess(true);
        } catch (Exception e) {
            json.setMsg("保存打印设置失败！");
            json.setSuccess(false);
            log.error("保存打印设置失败！", e);
        }
        return ReturnData.ok().data(json);
    }
}

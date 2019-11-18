package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.service.sys.IAreaSearch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("/sys/sys_area")
@Slf4j
public class AreaController {

    @Autowired
    private IAreaSearch areaService;

    /**
     * 查询网站区域数据
     *
     * @return
     */
    @GetMapping("/queryArea")
    public ReturnData<Json> queryArea(String parenter_id, String isbs) {
        Json json = new Json();
        ArrayList list = null;
        try {
            if(!StringUtil.isEmpty(isbs) && "Y".equals(isbs)){
                list = (ArrayList) areaService.queryBsArea();
            }else{
                list = (ArrayList) areaService.queryArea(parenter_id);
            }
            if (list != null && list.size() > 0) {
                json.setMsg("查询成功");
                json.setSuccess(true);
                json.setRows(list);
            } else {
                json.setSuccess(false);
                json.setMsg("查询失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg("查询失败");
        }
        return ReturnData.ok().data(json);
    }
}

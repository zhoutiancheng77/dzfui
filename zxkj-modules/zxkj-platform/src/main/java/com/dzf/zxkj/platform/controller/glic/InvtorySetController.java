package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 存货设置
 *
 */
@RestController("glicnvtorySetController")
@RequestMapping("/glic/gl_icinvset")
@Slf4j
public class InvtorySetController {
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv = null;

    @GetMapping("/query")
    public ReturnData query(){
        Json json = new Json();
        InventorySetVO vo = gl_ic_invtorysetserv.query(SystemUtil.getLoginCorpId());
        if(vo != null){
            json.setRows(vo);
            json.setSuccess(true);
            json.setMsg("查询成功");
        }else{
            json.setRows(null);
            json.setSuccess(false);
            json.setMsg("查询数据为空");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestParam Map<String, String> param){
        Json json = new Json();

        String isqzsave = param.get("isqzsave");//强制保存
        boolean ischeck = true;
        if("Y".equals(isqzsave)){
            ischeck = false;
        }
        InventorySetVO data = JsonUtils.convertValue(param, InventorySetVO.class);
        InventorySetVO vo = gl_ic_invtorysetserv.save(SystemUtil.getLoginUserId(),SystemUtil.getLoginCorpId() ,data,ischeck);
        json.setRows(vo);
        json.setMsg("保存成功");
        json.setSuccess(true);
        if(!StringUtil.isEmpty(vo.getErrorinfo())){
            json.setMsg("返回提示信息");
        }

        if(!StringUtil.isEmpty(vo.getLoginfo())){
//                writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                        vo.getLoginfo(), ISysConstants.SYS_2);
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/setdefaultvalue")
    public ReturnData setdefaultvalue(){
        Json json = new Json();
        InventorySetVO vo = gl_ic_invtorysetserv.getDefaultValue(SystemUtil.getLoginUserId(),SystemUtil.getLoginCorpVo());
        json.setRows(vo);
        json.setMsg("设置默认数据成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}
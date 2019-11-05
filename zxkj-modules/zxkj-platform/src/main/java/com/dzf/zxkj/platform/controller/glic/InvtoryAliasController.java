package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccAliasService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

/**
 * 别名设置
 *
 */
@RestController
@RequestMapping("/glic/gl_icinvalias")
@Slf4j
public class InvtoryAliasController {
    @Autowired
    private IInventoryAccAliasService gl_ic_invtoryaliasserv = null;

    @GetMapping("/query")
    public ReturnData query(@RequestParam("pk_inventory") String pk_inventory){
        Grid grid = new Grid();
        InventoryAliasVO[] vos = gl_ic_invtoryaliasserv.query(SystemUtil.getLoginCorpId(),pk_inventory);
        if(vos != null && vos.length>0){
            grid.setRows(new ArrayList<InventoryAliasVO>(Arrays.asList(vos)));
            grid.setTotal(Long.valueOf(vos.length));
            grid.setMsg("查询成功");
        }else{
            grid.setTotal(0l);
            grid.setMsg("查询数据为空");
        }
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestParam Map<String, String> param){
        Json json = new Json();
        String operateType = StringUtil.nequals(param.get("type"), "add")?"修改":"新增";
        String cateName = param.get("cate_name");
        InventoryAliasVO data = JsonUtils.convertValue(param, InventoryAliasVO.class);
        data.setPk_corp(SystemUtil.getLoginCorpId());
        InventoryAliasVO vo = gl_ic_invtoryaliasserv.save(data);
        json.setRows(vo);
        json.setMsg("保存成功");
        json.setSuccess(true);
//        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(), "存货档案_"+operateType+"存货'"+cateName+"'别名：别名："+data.getAliasname()+";", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/onDelete")
    public ReturnData onDelete(@RequestParam Map<String, String> param){
        Json json = new Json();
        String pk_alias = param.get("pk_alias");
        String cateName = param.get("cate_name");
        String aliasname =param.get("aliasname");
        gl_ic_invtoryaliasserv.delete(pk_alias, SystemUtil.getLoginCorpId());
        json.setMsg("删除成功");
        json.setSuccess(true);
//            writeLogRecord(LogRecordEnum.OPE_KJ_BDSET.getValue(), "存货档案_删除存货'"+cateName+"'别名：别名："+aliasname,ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}
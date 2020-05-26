package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZFStringUtil;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccAliasService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 别名设置
 *
 */
@RestController
@RequestMapping("/glic/gl_icinvalias")
@Slf4j
public class InvtoryAliasController extends BaseController {
    @Autowired
    private IInventoryAccAliasService gl_ic_invtoryaliasserv = null;

    @GetMapping("/query")
    public ReturnData query(@RequestParam Map<String, String> param){
        Grid grid = new Grid();
        String   pk_inventory= param.get("pk_inventory");
        InventoryAliasVO[] vos = gl_ic_invtoryaliasserv.query(SystemUtil.getLoginCorpId(),pk_inventory);

        String isfenye = param.get("isfenye");
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        grid.setTotal(vos == null ? 0L : vos.length );
        if (vos != null && vos.length > 0) {
            if("Y".equals(isfenye)) {
                int page = queryParamvo.getPage();
                int rows = queryParamvo.getRows();
                vos = (InventoryAliasVO[])getPageVOs(vos, page, rows);
            }
        }
        grid.setRows(vos == null ? new InventoryAliasVO[0] : vos);
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody Map<String, String> param){
        Json json = new Json();
        String operateType = StringUtil.nequals(param.get("type"), "add")?"修改":"新增";
        String cateName = param.get("cate_name");
        InventoryAliasVO data = JsonUtils.convertValue(param, InventoryAliasVO.class);
        data.setPk_corp(SystemUtil.getLoginCorpId());
        InventoryAliasVO vo = gl_ic_invtoryaliasserv.save(data);
        json.setRows(vo);
        json.setMsg("保存成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "存货档案_"+operateType+"存货'"+cateName+"'别名：别名："+data.getAliasname()+";", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/onDelete")
    public ReturnData onDelete(@RequestBody Map<String, String> param){
        Json json = new Json();
        String pk_alias = param.get("pk_aliass");
        String cateName = param.get("cate_name");
        String aliasname =param.get("aliasname");

        String[] pkss = DZFStringUtil.getString2Array(pk_alias, ",");
        if (DZFValueCheck.isEmpty(pkss)){
            throw new BusinessException("数据为空,删除失败!");
        }
        gl_ic_invtoryaliasserv.deleteByPks(pkss, SystemUtil.getLoginCorpId());
        json.setMsg("删除成功");
        json.setSuccess(true);
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "存货档案_删除存货'"+cateName+"'别名：别名："+aliasname,ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
}
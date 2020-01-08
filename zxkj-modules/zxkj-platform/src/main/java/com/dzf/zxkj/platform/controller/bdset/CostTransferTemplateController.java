package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.service.bdset.ICBMBService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 成本结转模板
 */
@RestController
@RequestMapping("/bdset/gl_cpcbmbact")
public class CostTransferTemplateController extends BaseController {
    @Autowired
    private ICBMBService gl_cpcbmbserv;

    // 保存
    @PostMapping("/save")
    public ReturnData save(@RequestBody CpcosttransVO data) {
        Json json = new Json();
        convertToFloat(data);
        data.setPk_corp(SystemUtil.getLoginCorpId());
        data.setCoperatorid(SystemUtil.getLoginUserId());
        data.setDoperatedate(new DZFDate(SystemUtil.getLoginDate()));
        data.setDr(0);
        gl_cpcbmbserv.save(data);
        json.setSuccess(true);
        json.setRows(data);
        json.setMsg("保存成功");
        return ReturnData.ok().data(json);
    }

    // 修改
    @PostMapping("/update")
    public ReturnData update(@RequestBody CpcosttransVO data) {
        Json json = new Json();
        //校验id
        CpcosttransVO cpVo = gl_cpcbmbserv.queryById(data.getPk_corp_costtransfer());
        if (cpVo == null) {
            throw new BusinessException("该数据不存在或已删除，请核对!");
        } else if (!cpVo.getPk_corp().equals(SystemUtil.getLoginCorpId())) {
            throw new BusinessException("只能操作当前登录公司数据！");
        }
        convertToFloat(data);
        gl_cpcbmbserv.update(data);
        json.setSuccess(true);
        json.setRows(data);
        json.setMsg("更新成功");
        return ReturnData.ok().data(json);
    }

    // 查询
    @GetMapping("/query")
    public ReturnData query() {
        Grid grid = new Grid();
        List<CpcosttransVO> list = gl_cpcbmbserv.query(SystemUtil.getLoginCorpId());
        if (list != null && list.size() > 0) {
            grid.setTotal((long) list.size());
            grid.setRows(list);
            for (CpcosttransVO vo : list) {
                convertToPrecent(vo);
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET,
                "成本结转模板查询");
        return ReturnData.ok().data(grid);
    }

    // 查询一条
    @GetMapping("/queryOne")
    public ReturnData queryOne(@RequestParam("id_fillaccount") String id) {
        Json json = new Json();
        CpcosttransVO vo = gl_cpcbmbserv.queryById(id);
        convertToPrecent(vo);
        json.setSuccess(true);
        json.setMsg("查询成功");
        json.setRows(vo);
        return ReturnData.ok().data(json);
    }

    // 删除
    @PostMapping("/delete")
    public ReturnData delete(@RequestBody CpcosttransVO data) {
        Json json = new Json();
        //校验id：
        CpcosttransVO cpVo = gl_cpcbmbserv.queryById(data.getPk_corp_costtransfer());
        if (cpVo == null) {
            throw new BusinessException("该数据不存在或已删除，请核对!");
        } else if (!cpVo.getPk_corp().equals(SystemUtil.getLoginCorpId())) {
            throw new BusinessException("只能操作当前登录公司数据！");
        }
        gl_cpcbmbserv.delete(cpVo);
        json.setSuccess(true);
        json.setRows(cpVo);
        json.setMsg("删除成功");
        return ReturnData.ok().data(json);
    }

    //转成小数
    private void convertToFloat(CpcosttransVO data) {
        if (data.getTransratio() != null) {
            DZFDouble result = SafeCompute.div(data.getTransratio(), new DZFDouble(100));
            data.setTransratio(result);
        }
    }

    //转成百分数
    private void convertToPrecent(CpcosttransVO vo) {
        if (vo != null && vo.getTransratio() != null) {
            DZFDouble result = SafeCompute.multiply(vo.getTransratio(), new DZFDouble(100));
            vo.setTransratio(result);
        }
    }
}

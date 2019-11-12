package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bdset.RemittanceVO;
import com.dzf.zxkj.platform.service.bdset.IRemittanceService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 汇兑损益模板
 */
@RestController
@RequestMapping("/bdset/gl_remittance")
public class ExchangeTemplateController {
    @Autowired
    private IRemittanceService gl_remitserv;

    @PostMapping("/save")
    public ReturnData save(@RequestBody RemittanceVO data) {
        Json json = new Json();
        data.setCoperatorid(SystemUtil.getLoginUserId());
        data.setDoperatedate(new DZFDate(SystemUtil.getLoginDate()));
        data.setPk_corp(SystemUtil.getLoginCorpId());
        if (data.getPrimaryKey() != null && !"".equals(data.getPrimaryKey())) {
            RemittanceVO vo = gl_remitserv.queryById(data.getPrimaryKey());
            if (vo == null)
                throw new BusinessException("该数据不存在或已删除，请核对!");
            if (!vo.getPk_corp().equals(data.getPk_corp()))
                throw new BusinessException("只能操作该公司权限内的数据");
        }
        gl_remitserv.save(data);
        json.setSuccess(true);
        json.setRows(data);
        json.setMsg("保存成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/query")
    public ReturnData query() {
        Grid grid = new Grid();
        List<RemittanceVO> list = gl_remitserv.query(SystemUtil.getLoginCorpId());
        if (list != null && list.size() > 0) {
            grid.setTotal((long) list.size());
            grid.setRows(list);
            grid.setSuccess(true);
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/delete")
    public ReturnData delete(@RequestBody RemittanceVO data) {
        Json json = new Json();
        RemittanceVO vo = gl_remitserv.queryById(data.getPrimaryKey());
        if (vo == null)
            throw new BusinessException("该数据不存在或已删除，请核对!");
        if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
            throw new BusinessException("只能操作该公司权限内的数据");
        gl_remitserv.delete(data);
        json.setSuccess(true);
        json.setRows(data);
        json.setMsg("成功");
        return ReturnData.ok().data(json);
    }

    /**
     * 校验是否可增加
     */
    @GetMapping("/canAdd")
    public ReturnData canAdd() {
        List<RemittanceVO> vos = gl_remitserv.query(SystemUtil.getLoginCorpId());
        Json json = new Json();
        if (vos != null && vos.size() > 0) {
            json.setSuccess(false);
            json.setMsg("本公司已存在汇兑损益模板，不能增加。");
        } else {
            json.setSuccess(true);
            json.setMsg("可增加");
        }
        return ReturnData.ok().data(json);
    }
}

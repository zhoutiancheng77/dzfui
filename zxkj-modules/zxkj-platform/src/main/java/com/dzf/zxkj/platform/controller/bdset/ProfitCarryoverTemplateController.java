package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCptranslrHVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.ICptransLrService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 利润结转模板
 */
@RestController
@RequestMapping("/bdset/gl_cplrmbact")
public class ProfitCarryoverTemplateController {
    @Autowired
    private ICptransLrService gl_cplrmbserv;
    @Autowired
    private ICpaccountService cpaccountService;

    @GetMapping("/query")
    public ReturnData query() {
        Grid grid = new Grid();
        List<YntCptranslrHVO> list = gl_cplrmbserv.query(SystemUtil.getLoginCorpId(), true);
        if (list != null && list.size() > 0) {
            grid.setSuccess(true);
            grid.setTotal((long) list.size());
            YntCptranslrHVO hvo = list.get(0);
            if (hvo != null) {
                YntCpaccountVO incvo = cpaccountService.queryById (hvo.getPk_transferinaccount());
                YntCpaccountVO outcvo = cpaccountService.queryById (hvo.getPk_transferoutaccount());
                hvo.setAccountname(incvo.getAccountcode() + "_" + incvo.getAccountname());
                hvo.setVname(outcvo.getAccountcode() + "_" + outcvo.getAccountname());
                List<YntCptranslrHVO> list1 = new ArrayList<>();
                list1.add(hvo);
                grid.setRows(list1);
            }
        } else {
            grid.setTotal(Long.valueOf(0));
        }
        return ReturnData.ok().data(grid);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody YntCptranslrHVO data) {
        Json json = new Json();
        if (data != null) {
            if (IDefaultValue.DefaultGroup.equals(data.getPk_corp())) {
                data.setPk_corp_translr_h(null);
            }
            data.setPk_corp(SystemUtil.getLoginCorpId());
            data.setDr(0);//赋默认值
            if (data.getPrimaryKey() != null && !"".equals(data.getPrimaryKey())) {//不是新增情况，校验
                YntCptranslrHVO vo = gl_cplrmbserv.queryById(data.getPrimaryKey());

                if (vo == null)
                    throw new BusinessException("该数据不存在或已删除，请核对!");
                if (!vo.getPk_corp().equals(data.getPk_corp()))
                    throw new BusinessException("只能操作当前登录公司权限内的数据");
            }
            YntCptranslrHVO hvo = gl_cplrmbserv.save(data);
            json.setSuccess(true);
            json.setMsg("保存成功!");
            hvo.setAccountname(hvo.getAccountcode() + "_" + hvo.getAccountname());
            hvo.setVname(hvo.getVcode() + "_" + hvo.getVname());
            json.setRows(hvo);
        } else {
            throw new BusinessException("数据不能为空！");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData delete(@RequestBody YntCptranslrHVO data) {
        Json json = new Json();
        //校验id和pk_corp
        YntCptranslrHVO vo = gl_cplrmbserv.queryById(data.getPrimaryKey());
        if (vo == null)
            throw new BusinessException("该数据不存在或已删除！");
        if (!vo.getPk_corp().equals(SystemUtil.getLoginCorpId()))
            throw new BusinessException("只能操作该公司权限内的数据");
        gl_cplrmbserv.delete(vo);
        json.setSuccess(true);
        json.setRows(data);
        json.setMsg("成功");
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryOne")
    public ReturnData queryOne(@RequestParam("mainid") String id) {
        Json json = new Json();
        YntCptranslrHVO vo = gl_cplrmbserv.queryById(id);
        json.setSuccess(true);
        json.setRows(vo);
        return ReturnData.ok().data(json);
    }
}

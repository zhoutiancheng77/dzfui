package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.bdset.SurtaxTemplateVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ISurtaxTemplateService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bdset/gl_surtaxtempact")
public class SurtaxTemplateController {
    @Autowired
    private ISurtaxTemplateService gl_surtaxtempserv;

    @GetMapping("/query")
    public ReturnData query(String preset) {
        Json json = new Json();
        CorpVO corp = SystemUtil.getLoginCorpVo();
        List<SurtaxTemplateVO> list = "Y".equals(preset) ? gl_surtaxtempserv.getPresetTemplate(corp)
                : gl_surtaxtempserv.query(corp);
        json.setRows(list);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody SurtaxTemplateVO[] temps) {
        Json json = new Json();
        temps = gl_surtaxtempserv.save(SystemUtil.getLoginCorpId(), temps);
        json.setRows(temps);
        json.setSuccess(true);
        json.setMsg("保存成功");
        return ReturnData.ok().data(json);
    }
}

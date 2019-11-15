package com.dzf.zxkj.platform.controller.voucher;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.bdset.CommonAssistVO;
import com.dzf.zxkj.platform.service.pzgl.ICommonAssistSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/voucher-manage/common-assist")
public class CommonAssistSetContoller {
    @Autowired
    private ICommonAssistSetService gl_cyfzhsserv;

    @GetMapping("/query")
    public ReturnData query(String hasAssistData) {
        Json json = new Json();
        List<CommonAssistVO> rs = gl_cyfzhsserv.query(SystemUtil.getLoginCorpId(), "Y".equals(hasAssistData));
        json.setRows(rs);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody CommonAssistVO data) {
        Json json = new Json();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        data.setCoperatorid(SystemUtil.getLoginUserId());
        data = gl_cyfzhsserv.save(data);
        json.setRows(data);
        json.setMsg("设置成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData delete(@RequestBody CommonAssistVO data) {
        Json json = new Json();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        gl_cyfzhsserv.delete(data);
        json.setMsg("取消成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/checkExist")
    public ReturnData checkExist(@RequestBody CommonAssistVO data) {
        Json json = new Json();
        data.setPk_corp(SystemUtil.getLoginCorpId());
        json.setSuccess(gl_cyfzhsserv.checkExist(data));
        return ReturnData.ok().data(json);
    }
}

package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UpdateVersionVO;
import com.dzf.zxkj.platform.service.sys.IUpdateLogService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gl_logpicact")
@Slf4j
public class UpdateLogController extends BaseController {

    @Autowired
    private IUpdateLogService gl_logpicserv = null;

    // 查询
    @PostMapping("query")
    public ReturnData query() {
        Json json = new Json();
        try {
            String module = "dzfkj";
            UpdateVersionVO vo = gl_logpicserv.query(SystemUtil.getLoginUserId(),module);
            if(vo!=null && !StringUtil.isEmpty(vo.getPk_userversion())){
                vo.setIsread(DZFBoolean.TRUE);
            }else{
                vo.setIsread(DZFBoolean.FALSE);
            }
            json.setSuccess(true);
            json.setData(vo);
            json.setMsg("查询成功！");
            log.info("查询成功！");
        } catch ( Exception e) {
            printErrorLog(json, e, "查询失败！");
        }
        return ReturnData.ok().data(json);
    }
}

package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.report.query.cwzb.MultiColumnQueryVO;
import com.dzf.zxkj.report.service.cwzb.IMultiColumnService;
import com.dzf.zxkj.report.vo.cwzb.MultiColumnVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("gl_rep_multiserv")
public class MultiColumnController {

    @Autowired
    private IMultiColumnService multiColumnService;

    @RequestMapping("query")
    public ReturnData<MultiColumnVO> query(@RequestBody MultiColumnQueryVO multiColumnQueryVo){
        return ReturnData.ok().message("查询成功").data(multiColumnService.query(multiColumnQueryVo));
    }
}

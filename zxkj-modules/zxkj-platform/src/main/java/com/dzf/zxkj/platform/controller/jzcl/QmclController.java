package com.dzf.zxkj.platform.controller.jzcl;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: zpm
 * @Description:期末处理
 * @Date:Created by 2019/11/14
 * @Modified By:
 */
@RestController
@RequestMapping("/gl/gl_qmclact")
@Slf4j
@SuppressWarnings("all")
public class QmclController {


    @Autowired
    private IQmclService gl_qmclserv = null;

    @PostMapping("/query")
    public ReturnData<Grid> query(@MultiRequestBody QueryParamVO queryParamvo) {
        Grid grid = new Grid();
        try {
            List<String> corppks = queryParamvo.getCorpslist();
            DZFDate begindate1 = queryParamvo.getBegindate1();
            DZFDate enddate1 = queryParamvo.getEnddate();
            String userid = queryParamvo.getUserid();
            DZFDate logindate = queryParamvo.getClientdate();
            DZFBoolean iscarover = queryParamvo.getIscarover();
            DZFBoolean isuncarover = queryParamvo.getIsuncarover();
            List<QmclVO> list = gl_qmclserv.initquery(corppks, begindate1, enddate1, userid, logindate, iscarover, isuncarover);

            grid.setRows(list);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
            log.error("查询失败!", e);
        }
        return ReturnData.ok().data(grid);
    }

}

package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.CustServVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.ICustServService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: zpm
 * @Description: 客服
 * @Date:Created by 2020/01/08
 * @Modified By:
 */
@RestController
@RequestMapping("/sys/custServAct")
@Slf4j
public class CustServController {

    @Autowired
    private ICustServService custServServiceImpl;

    @GetMapping("/query")
    public ReturnData<Json> query(@MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            CustServVO csvo = custServServiceImpl.query(corpVO.getPk_corp());
            csvo.setCuserid(userVO.getCuserid());
            if (csvo != null) {
                json.setMsg("查询成功");
                json.setSuccess(true);
                json.setRows(csvo);
            } else {
                json.setSuccess(false);
                json.setMsg("查询失败");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            json.setSuccess(false);
            json.setMsg("查询失败");
        }
        return ReturnData.ok().data(json);
    }

}

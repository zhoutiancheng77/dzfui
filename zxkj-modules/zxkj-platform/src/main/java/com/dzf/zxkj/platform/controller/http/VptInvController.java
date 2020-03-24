package com.dzf.zxkj.platform.controller.http;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/vpt/vptinv")
@Slf4j
public class VptInvController extends BaseController {

    @PostMapping("/register")
    public ReturnData<Json> register(@RequestParam("params") String param){
        Json json = new Json();

        return ReturnData.ok().data(json);
    }

    @PostMapping("/invoiceinfo")
    public ReturnData<Json> invoice(@RequestParam("params") String param){
        Json json = new Json();


        return ReturnData.ok().data(json);
    }
}

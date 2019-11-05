package com.dzf.zxkj.platform.controller.zcgl;

import com.dzf.zxkj.platform.service.zcgl.IZclbService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("sys_zclbact")
@Slf4j
public class ZclbController {
    @Autowired
    private IZclbService sys_zclbserv;



}

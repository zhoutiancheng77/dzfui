package com.dzf.zxkj.platform.controller;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.custom.type.DZFBoolean;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: dandelion
 * @Date: 2019-09-09
 * @Description:
 */

@RestController
@RequestMapping("/fzhs")
@Slf4j
public class AuxiliaryAccountController {

    @PostMapping("/save")
    public ReturnData save(@RequestBody AuxiliaryAccountBVO auxiliaryAccountBVO) {

        System.out.println(auxiliaryAccountBVO);
        return ReturnData.ok();
    }

    @GetMapping("get/{id}")
    public ReturnData<AuxiliaryAccountBVO> get() {
        AuxiliaryAccountBVO auxiliaryAccountBVO = new AuxiliaryAccountBVO();
        auxiliaryAccountBVO.setIsimp(new DZFBoolean(false));
        return ReturnData.ok().data(auxiliaryAccountBVO);
    }

}

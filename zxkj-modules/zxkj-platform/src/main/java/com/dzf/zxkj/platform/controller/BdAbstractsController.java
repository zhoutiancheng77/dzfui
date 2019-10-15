package com.dzf.zxkj.platform.controller;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.platform.model.bdset.BDabstractsVO;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.services.bdset.IBDabstractsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: zpm
 * @Description: 摘要controller
 * @Date:Created by 2019/09/10
 * @Modified By:
 */


@RestController
@Slf4j
@RequestMapping("/abstract")
public class BdAbstractsController {

    @Autowired
    private IBDabstractsService bdAbstractsService;

    @GetMapping("query/{pk_corp}")
    public ReturnData<BDabstractsVO[]> query(@PathVariable("pk_corp") String pk_corp) {
        BDabstractsVO[] abstractvos = bdAbstractsService.query(pk_corp);
        return ReturnData.ok().data(abstractvos);
    }


    @PostMapping("save")
    public ReturnData<BankAccountVO> save(@RequestBody BDabstractsVO abstractvo) {
        //set Default Value
        abstractvo.setDoperatedate(new DZFDate());
        abstractvo.setDr(0);
        bdAbstractsService.existCheck(abstractvo);
        return ReturnData.ok().message("保存成功").data(bdAbstractsService.save(abstractvo));
    }

    @PostMapping("delete")
    public ReturnData delete(@RequestBody BDabstractsVO abstractvo){
        bdAbstractsService.delete(abstractvo);
        return ReturnData.ok().message("删除成功");
    }

}

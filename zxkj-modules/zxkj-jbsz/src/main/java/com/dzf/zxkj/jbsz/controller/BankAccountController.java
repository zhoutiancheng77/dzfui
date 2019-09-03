package com.dzf.zxkj.jbsz.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jbsz.service.IBankAccountService;
import com.dzf.zxkj.jbsz.vo.BankAccountVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
@RestController
@Slf4j
@RequestMapping("/gl/gl_yhzhact")
public class BankAccountController {

    @Autowired
    private IBankAccountService bankAccountService;

    @PostMapping("list")
    public ReturnData<List<BankAccountVO>> list(@RequestParam("pk_corp") String pk_corp, @RequestParam("isnhsty") String isnhsty, @RequestParam(required = false, defaultValue = "1") Integer pageNum, @RequestParam(required = false, defaultValue = "10") Integer pageSize){
        Page page = new Page(pageNum, pageSize);
        return ReturnData.ok().data(bankAccountService.query(page, pk_corp, isnhsty));
    }

    @GetMapping("get/{id}")
    public ReturnData<BankAccountVO> queryOne(@PathVariable("id") String id){
        return ReturnData.ok().data(bankAccountService.queryById(id));
    }

    @PostMapping("save")
    public ReturnData<BankAccountVO> save(@RequestBody BankAccountVO bankAccountVO){
        return ReturnData.ok().message("保存成功").data(bankAccountService.save(bankAccountVO));
    }
}

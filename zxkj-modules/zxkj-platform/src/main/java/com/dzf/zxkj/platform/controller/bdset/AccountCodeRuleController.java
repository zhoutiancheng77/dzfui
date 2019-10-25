package com.dzf.zxkj.platform.controller.bdset;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountChangeVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("gl_dataupgrade")
@Slf4j
public class AccountCodeRuleController {
    @Autowired
    private ICpaccountCodeRuleService codeRuleService;

    @Autowired
    private ICpaccountService accountservice;

    @GetMapping("loaddata")
    public ReturnData<Grid> loaddata(){
        Grid grid = new Grid();
        List<YntCpaccountChangeVO> list = new ArrayList<YntCpaccountChangeVO>();
        try {
            YntCpaccountChangeVO[] vos = codeRuleService.loadData(SystemUtil.getLoginCorpId());
            if(vos==null){
                vos = new YntCpaccountChangeVO[0];
            }
            list = Arrays.asList(vos);
            if (list != null) {
                grid.setTotal((long) list.size());
                grid.setRows(list);
                if(0==list.size()){
                    grid.setMsg("没有变化数据");
                }else{
                    grid.setMsg("加载数据成功！");
                }

            }
            grid.setSuccess(true);
            log.info("没有变更数据");
        } catch (Exception e) {
            log.info("加载数据失败！");
            grid.setRows(list);
            grid.setSuccess(false);
            grid.setMsg("加载数据失败:");
        }
        return ReturnData.ok().data(grid);
    }

    //更新编码规则
    @PostMapping("update")
    public ReturnData<Grid> update(@MultiRequestBody String newcoderule, @MultiRequestBody String memo) {
        Grid grid = new Grid();
        List<YntCpaccountChangeVO> list = new ArrayList<YntCpaccountChangeVO>();
        try {
            String pk_corp = SystemUtil.getLoginCorpId();
            String oldrule = accountservice.queryAccountRule(pk_corp);

            YntCpaccountChangeVO[] vos = codeRuleService.updateCodeRule(pk_corp, oldrule, newcoderule,memo);
            if(vos==null){
                vos = new YntCpaccountChangeVO[0];
            }
            for(YntCpaccountChangeVO vo: vos){
                vo.setMemo(memo);
            }
            list = Arrays.asList(vos);
            if (list != null && list.size() > 0) {
                grid.setTotal((long) list.size());
                grid.setRows(list);
                grid.setMsg("更新成功！");
            }
            grid.setSuccess(true);
            log.info("更新成功！");
        } catch ( Exception e) {
            log.info("更新失败！");
            grid.setRows(list);
            grid.setSuccess(false);
            grid.setMsg("更新失败");
        }
        return ReturnData.ok().data(grid);
    }
}

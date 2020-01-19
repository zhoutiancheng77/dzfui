package com.dzf.zxkj.platform.controller.taxrpt;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.tax.TaxRptTempletVO;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/sys/corpTaxact")
@Slf4j
public class CorpTaxController  extends BaseController {

    @Autowired
    private ICorpTaxService corpTaxact;

    @GetMapping("/queryTaxRpt")
    public ReturnData<Grid> queryTaxRpt(QueryParamVO paramvo){

        Grid grid = new Grid();
        try {
            checkOwnCorp(paramvo.getPk_corp());
            List<TaxRptTempletVO> revo = corpTaxact.queryCorpTaxRpt(paramvo);
            int len = revo==null?0:revo.size();
            if(len > 0){
                grid.setTotal((long)len);
                grid.setRows(revo);
                grid.setSuccess(true);
                grid.setMsg("查询成功！");
            }else{
                grid.setTotal(Long.valueOf(0));
                grid.setSuccess(true);
                grid.setMsg("查询结果为空！");
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException ? e.getMessage() : "查询失败");
        }

        return ReturnData.ok().data(grid);
    }
}

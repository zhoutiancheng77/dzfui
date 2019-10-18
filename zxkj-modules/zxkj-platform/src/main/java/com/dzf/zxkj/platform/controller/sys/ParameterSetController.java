package com.dzf.zxkj.platform.controller.sys;

import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/sys/sys_parameteract")
@Slf4j
public class ParameterSetController {
    @Autowired
    private IParameterSetService iservice;

    /**
     * 查询方法
     */
    @GetMapping("/queryInfo")
    public ReturnData<Grid> queryInfo(@MultiRequestBody CorpVO cpvo){
        Grid grid = new Grid();
        try {
            String level = "2";
            String pk_corp = cpvo.getPk_corp();
            if(IDefaultValue.DefaultGroup.equals(pk_corp)){//集团为 0
                level = "0";
            }else if(cpvo.getIsaccountcorp() != null
                    && cpvo.getIsaccountcorp().booleanValue()){//会计公司或者会计工厂为 1
                level = "1";
            }else{//公司为2
                level = "2";
            }
            List<YntParameterSet> list= iservice.queryParamter(pk_corp);
            list = filterResult(list, level, cpvo);
            grid.setRows(list==null?new ArrayList<YntParameterSet>():list);
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        }catch (Exception e) {
            grid.setMsg("查询失败");
            grid.setSuccess(false);
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }
    /**
     *  根据level过滤
     * @param list
     * @param level
     * @return
     */
    private List<YntParameterSet> filterResult(List<YntParameterSet> list, String level,CorpVO cpvo){
        if(!StringUtil.isEmpty(level)){//level不为空
            int teLe = Integer.parseInt(level);
            List<YntParameterSet> result = new ArrayList<YntParameterSet>();
            Integer paramlevel = null;
            if(list != null && list.size() > 0 ){
                for(YntParameterSet param : list){
                    paramlevel = param.getPlevel();
                    if("1".equals(level)
                            && "dzf008".equals(param.getParameterbm())
                            && !isshowFactoryParam(cpvo))
                        continue;
                    if("2".equals(level) //公司
                            && "dzf005".equals(param.getParameterbm()) //进销存暂估采购单下月是否自动回冲
                            && !IcCostStyle.IC_ON.equals(cpvo.getBbuildic())//没有启用进销存
                    ){
                        continue;
                    }
                    //
                    if(paramlevel != null && paramlevel >= teLe){//取大于等于
                        result.add(param);
                    }
                }
                list = result;
            }
        }
        return list;
    }

    /**
     * 判断dzf008 这个参数为会计工厂参数。
     */
    private boolean isshowFactoryParam(CorpVO cpvo){
        boolean ishow = false;
        if(cpvo.getIsaccountcorp() != null
                && cpvo.getIsaccountcorp().booleanValue()
                && cpvo.getIsfactory() != null
                && cpvo.getIsfactory().booleanValue()){
            ishow = true;
        }
        return ishow;
    }


    /**
     * 保存方法
     */
    @PostMapping("/onSave")
    public ReturnData<Grid> onSave(@MultiRequestBody("setvo") YntParameterSet setvo, @MultiRequestBody CorpVO cpvo){
        Grid grid = new Grid();
        try{
            String pk_corp = cpvo.getPk_corp();
            if (setvo != null) {
                iservice.saveParamter(pk_corp,setvo);
                grid.setSuccess(true);
                grid.setMsg("保存成功!");
            }else{
                grid.setSuccess(false);
                grid.setMsg("保存失败!");
            }
        }catch(Exception e){
            grid.setMsg("保存失败!");
            grid.setSuccess(false);
            log.error("保存失败!", e);
        }
        return ReturnData.ok().data(grid);
    }
}
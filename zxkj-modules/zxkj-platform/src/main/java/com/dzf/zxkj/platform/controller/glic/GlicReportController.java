package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Set;

@Slf4j
public class GlicReportController extends BaseController {
    @Autowired
    private IUserService userService;

    protected void checkPowerDate(QueryParamVO vo){
        CorpVO  corpVO = SystemUtil.getLoginCorpVo();
        if(StringUtil.isEmpty(vo.getPk_corp())){
            //如果编制单位为空则取当前默认公司
            vo.setPk_corp(corpVO.getPk_corp());
        }
        Set<String> powercorpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
        if(!powercorpSet.contains(vo.getPk_corp())){
            throw new BusinessException("无权操作！");
        }
        //开始日期应该在建账日期前
        DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVO.getBegindate())) ;
        if(vo.getBegindate1() == null  && !StringUtil.isEmpty(vo.getQjq())){
            vo.setBegindate1(DateUtils.getPeriodEndDate(vo.getQjq()));
        }
        if(begdate.after(vo.getBegindate1())){
            throw new BusinessException("开始日期不能在建账日期("+DateUtils.getPeriod(begdate)+")前!");
        }
    }

}

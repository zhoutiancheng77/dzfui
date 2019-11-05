package com.dzf.zxkj.report.controller;

import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class ReportBaseController extends BaseController {

    public QueryParamVO getQueryParamVO(QueryParamVO queryvo,CorpVO corpVO){
        if(StringUtil.isEmpty(queryvo.getPk_corp())){
            //如果编制单位为空则取当前默认公司
            queryvo.setPk_corp(corpVO.getPk_corp());
        }
        /** 把字符串变成codelist集合 */
        if(!StringUtil.isEmpty(queryvo.getKms())){
            List<String> codelist = Arrays.asList(queryvo.getKms().split(","));
            queryvo.setKmcodelist(codelist);
        }
        return queryvo;
    }

    public void checkPowerDate(QueryParamVO vo,CorpVO corpVO)  {
        //开始日期应该在建账日期前
        DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVO.getBegindate())) ;
        if(vo.getBegindate1() == null  && !StringUtil.isEmpty(vo.getQjq())){
            vo.setBegindate1(DateUtils.getPeriodEndDate(vo.getQjq()));
        }
        if(begdate.after(vo.getBegindate1())){
            throw new BusinessException("开始日期不能在建账日期("+DateUtils.getPeriod(begdate)+")前!");
        }
    }

    public String getPrintTitleName (){
        return "";
    }



}

package com.dzf.zxkj.report.controller;

import com.dzf.zxkj.base.controller.PrintAndExcelExportController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.power.IButtonPowerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ReportBaseController extends PrintAndExcelExportController {

    @Autowired
    private IButtonPowerService btn_power_ser;

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

    public boolean checkExcelExport(String pk_corp,HttpServletResponse response) {
        String tips = btn_power_ser.qryButtonPower(pk_corp);
        if (!StringUtil.isEmpty(tips)) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("text/html;charset=utf-8");
            response.addHeader("message-type", "export");
            try {
                response.addHeader("message-info",  java.net.URLEncoder.encode(tips,  "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                log.error("checkExcelExport------------->", e);
            }

            return false;
        }
        return true;
    }



}

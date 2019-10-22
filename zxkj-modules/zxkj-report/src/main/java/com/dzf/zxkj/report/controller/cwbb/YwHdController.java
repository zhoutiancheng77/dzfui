package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.report.YwHdVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwbb.IYwHdReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("gl_rep_ywhdbact")
@Slf4j
public class YwHdController  extends ReportBaseController {

    @Autowired
    private IYwHdReport gl_rep_ywhdserv;

    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        Grid grid = new Grid();

        QueryParamVO queryParamvo = getQueryParamVO(queryvo,corpVO);
        try {
            String begindate = DateUtils.getPeriod(queryParamvo.getBegindate1());
            String ishajz = "N";
            if(queryParamvo.getIshasjz()!=null && queryParamvo.getIshasjz().booleanValue()){
                ishajz = "Y";
            }
            queryParamvo.setQjq(begindate);
            queryParamvo.setQjz(begindate);
            queryParamvo.setIshasjz(new DZFBoolean(ishajz));
            queryParamvo.setIshassh(DZFBoolean.TRUE);
            queryParamvo.setXswyewfs(DZFBoolean.FALSE);

            //开始日期应该在建账日期前
            checkPowerDate(queryParamvo,corpVO);

            YwHdVO[] ywvos =  gl_rep_ywhdserv.queryYwHdValues(queryParamvo);

            if(ywvos==null || ywvos.length ==0){
                grid.setMsg("当前数据为空!");
            }else{
                grid.setMsg("查询成功");
            }
            grid.setTotal((long) (ywvos == null ? 0 : ywvos.length));
            grid.setRows(ywvos == null ? new ArrayList<YwHdVO>() : Arrays.asList(ywvos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<YwHdVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(),
//                "业务活动表查询:"+queryParamvo.getBegindate1().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    public QueryParamVO getQueryParamVO(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        if(StringUtil.isEmpty(queryvo.getPk_corp())){
            //如果编制单位为空则取当前默认公司
            queryvo.setPk_corp(corpVO.getPk_corp());
        }
        return queryvo;
    }

    private void checkPowerDate(QueryParamVO vo,CorpVO corpVO)  {
        //开始日期应该在建账日期前
        DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corpVO.getBegindate())) ;
        if(begdate.after(vo.getBegindate1())){
            throw new BusinessException("开始日期不能在建账日期("+DateUtils.getPeriod(begdate)+")前!");
        }
    }


}

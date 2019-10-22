package com.dzf.zxkj.report.controller.cwbb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.report.ReportDataGrid.XjllMsgVo;
import com.dzf.zxkj.platform.model.report.XjllMxvo;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.service.cwbb.IXjllbReport;
import com.dzf.zxkj.report.utils.VoUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;

@RestController
@RequestMapping("gl_rep_xjlybact")
@Slf4j
public class XjllController extends ReportBaseController {

    @Autowired
    private IXjllbReport gl_rep_xjlybserv;

    @Reference
    private IZxkjPlatformService zxkjPlatformService;

    /**
     *
     */
    @PostMapping("/queryAction")
    public ReturnData<ReportDataGrid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
        try {
            if (vo != null) {
                checkPowerDate(vo,corpVO);
                XjllbVO[] xjllbvos = gl_rep_xjlybserv.query(vo);
                if (xjllbvos != null && xjllbvos.length > 0) {
                    grid.setTotal((long) xjllbvos.length);
                    grid.setRows(Arrays.asList(xjllbvos));
                }
                //赋值不平衡信息
                putBlanceMsg(grid,xjllbvos);
            }
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XjllbVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        // 日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(), "现金流量表查询:" + vo.getQjq(), ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);
    }


    private void putBlanceMsg(ReportDataGrid grid, XjllbVO[] xjllbvos) {
        grid.setBlancemsg(true);
        grid.setBlancetitle("");
        DZFDouble xjlltotal = DZFDouble.ZERO_DBL;
        DZFDouble kmqcvalue = DZFDouble.ZERO_DBL;
        DZFDouble kmqmvalue = DZFDouble.ZERO_DBL;
        if (xjllbvos != null && xjllbvos.length > 0) {
            XjllMsgVo xjllmsgvo = grid.new XjllMsgVo();
            for (XjllbVO bvo : xjllbvos) {
                if (bvo.getBxjlltotal() != null && bvo.getBxjlltotal().booleanValue()) {
                    xjlltotal = VoUtils.getDZFDouble(bvo.getBqje()).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
                if (bvo.getBkmqc() != null && bvo.getBkmqc().booleanValue()) {
                    kmqcvalue = VoUtils.getDZFDouble(bvo.getBqje()).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
                if (bvo.getBkmqm() != null && bvo.getBkmqm().booleanValue()) {
                    kmqmvalue = VoUtils.getDZFDouble(bvo.getBqje()).setScale(2, DZFDouble.ROUND_HALF_UP);
                }
            }
            DZFDouble ce = xjlltotal.sub(kmqmvalue.sub(kmqcvalue));
            xjllmsgvo.setXjlltotal(xjlltotal);
            xjllmsgvo.setKmqcvalue(kmqcvalue);
            xjllmsgvo.setKmqmvalue(kmqmvalue);
            xjllmsgvo.setCe(ce);
            if (ce.doubleValue()!=0) {
                grid.setBlancemsg(false);
                grid.setBlancetitle("不平衡");
            }
            grid.setXjll_jyx(xjllmsgvo);
        }
    }

    /**
     * 联查现金流量明细账
     */
    @PostMapping("/queryMxAction")
    public ReturnData<Grid> queryMxAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        Grid grid = new Grid();
        try {
            QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
            if(vo != null){
                XjllMxvo[] xjllMxvo = gl_rep_xjlybserv.getXJllMX(vo.getQjq(), vo.getPk_corp(), vo.getHc());
                if(xjllMxvo != null && xjllMxvo.length > 0){
                    grid.setTotal((long)xjllMxvo.length);
                    grid.setRows(Arrays.asList(xjllMxvo));
                }
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XjllMxvo>());
            printErrorLog(grid, e, "查询失败！");
        }
        return ReturnData.ok().data(grid);
    }


    private String getPubParam(CorpVO cpvo) {
        return "corpIds="+cpvo.getPk_corp()+"&gsname="+ CodeUtils1.deCode(cpvo.getUnitname());
    }

    @PostMapping("/linkPz")
    public  ReturnData<Json> linkPz(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO){
        Json json = new Json();
        try {
            QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
            //凭证查询vo
            VoucherParamVO pzparamvo = new VoucherParamVO();
            pzparamvo.setPk_corp(vo.getPk_corp());
            pzparamvo.setPage(1);
            pzparamvo.setBegindate(DateUtils.getPeriodStartDate(vo.getQjq()));
            pzparamvo.setEnddate(DateUtils.getPeriodEndDate(vo.getQjq()));
            pzparamvo.setSerdate("serMon");
            pzparamvo.setStartYear(vo.getQjq().substring(0, 4));
            pzparamvo.setStartMonth(pzparamvo.getBegindate().getStrMonth());
            pzparamvo.setEndYear(pzparamvo.getEnddate().getYear()+"");
            pzparamvo.setEndMonth(pzparamvo.getEnddate().getStrMonth());
            pzparamvo.setPz_status(0);
            pzparamvo.setRows(50);
            pzparamvo.setIs_error_cash(Boolean.TRUE);
            QueryPageVO pagevo = zxkjPlatformService.processQueryVoucherPaged(pzparamvo);
            TzpzHVO[] vos = (TzpzHVO[]) pagevo.getPagevos();
            String url = "";
            if(vos!=null && vos.length ==1){
                //填制凭证界面
                url = vos[0].getPk_tzpz_h();
            }else{
                url = "gl/gl_pzgl/gl_pzgl.jsp?"+getPubParam(corpVO)+"&is_error_cash=true&source=xjll&pzbegdate="+DateUtils.getPeriodStartDate(vo.getQjq())+"&pzenddate="+DateUtils.getPeriodEndDate(vo.getQjq());
            }
            json.setSuccess(true);
            json.setMsg("成功");
            json.setRows(url);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
        }
        return  ReturnData.ok().data(json);
    }
}

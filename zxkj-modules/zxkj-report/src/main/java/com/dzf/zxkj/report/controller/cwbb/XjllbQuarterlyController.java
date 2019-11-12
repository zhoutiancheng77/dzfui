package com.dzf.zxkj.report.controller.cwbb;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.base.utils.DzfTypeUtils;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.bdset.PzmbbVO;
import com.dzf.zxkj.platform.model.report.XjllbVO;
import com.dzf.zxkj.platform.model.report.XjllquarterlyVo;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwbb.XjllQuarterlyExcelField;
import com.dzf.zxkj.report.service.cwbb.IXjllbQuarterlyReport;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_xjlyquarbact")
@Slf4j
public class XjllbQuarterlyController extends ReportBaseController {
    @Autowired
    private IXjllbQuarterlyReport gl_rep_xjlyquarbserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
        String[] resvalue = getJdValue(vo.getBegindate1());
        try {
            if (vo != null) {
                checkPowerDate(vo,corpVO);
                List<XjllquarterlyVo> xjllbvos = gl_rep_xjlyquarbserv.getXjllQuartervos(vo,resvalue[1]);
                if (xjllbvos != null && xjllbvos.size() > 0) {
                    grid.setTotal((long) xjllbvos.size());
                    grid.setRows(xjllbvos);
                }
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XjllbVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        // 日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(), "现金流量季报查询:" +resvalue[0], ISysConstants.SYS_2);

        return ReturnData.ok().data(grid);

    }

    private String[] getJdValue(DZFDate date){
        String month = date.toString().substring(5, 7);
        String res = null;
        String count = "0";
        if(month.equals("02") || month.equals("01") || month.equals("03") ){
            res = "第一季度";
            count ="1";
        }else if(month.equals("04") || month.equals("05") || month.equals("06") ){
            res = "第二季度";
            count ="2";
        }else if(month.equals("07") || month.equals("08") || month.equals("09") ){
            res = "第三季度";
            count ="3";
        }else if(month.equals("10") || month.equals("11") || month.equals("12") ){
            res = "第四季度";
            count ="4";
        }
        return new String[]{res,count};
    }

    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response){

        XjllquarterlyVo[] listVo = JsonUtils.deserialize(excelExportVO.getList(),XjllquarterlyVo[].class);//
        String gs=  excelExportVO.getCorpName();
        String qj= excelExportVO.getTitleperiod();
        Excelexport2003<XjllquarterlyVo> lxs = new Excelexport2003<XjllquarterlyVo>();
        XjllQuarterlyExcelField lrb = new XjllQuarterlyExcelField();
        lrb.setLrbvos(listVo);

        lrb.setQj(qj);
        lrb.setCreator(userVO.getUser_name());
        lrb.setCorpName(gs);

        baseExcelExport(response,lxs,lrb);

        // 日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CWREPORT.getValue(), "现金流量季报导出:" +qj, ISysConstants.SYS_2);
    }
    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(String corpName, String period, PrintParamVO printParamVO, QueryParamVO queryparamvo, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String strlist = printParamVO.getList();
            String type = printParamVO.getType();
            String font = printParamVO.getFont();
            if(strlist==null){
                return;
            }
            XjllquarterlyVo[] bodyvos = JsonUtils.deserialize(strlist, XjllquarterlyVo[].class);
            Map<String,String> tmap=new LinkedHashMap<String,String>();//声明一个map用来存前台传来的设置参数
            tmap.put("公司",  printParamVO.getCorpName());
            tmap.put("期间",  printParamVO.getTitleperiod());
            tmap.put("单位",  "元");
            QueryParamVO paramvo = new QueryParamVO();
            paramvo.setPk_corp(corpVO.getPk_corp());
            List<CorpTaxVo> listVos = zxkjPlatformService.queryTaxVoByParam(paramvo, userVO);
            if(listVos != null && listVos.size() > 0){
                Optional<CorpTaxVo> optional = listVos.stream().filter(v-> corpVO.getPk_corp().equals(v.getPk_corp())).findFirst();
                optional.ifPresent(corpTaxVo ->{
                    if(!StringUtil.isEmpty(corpTaxVo.getLegalbodycode())){
                        pmap.put("单位负责人", corpTaxVo.getLegalbodycode());
                    }
                    if(!StringUtil.isEmpty(corpTaxVo.getLinkman1())){
                        pmap.put("财务负责人", corpTaxVo.getLinkman1());
                    }
                    pmap.put("制表人", userVO.getUser_name());
                });
            }
            if(type.equals("2")){
                printReporUtil.setLineheight(12f);
            }
            printReporUtil.setBf_Bold(printReporUtil.getBf());
//			setBasecolor(new BaseColor(127,127,127));//设置单元格线颜色
            printReporUtil.setBasecolor(new BaseColor(0,0,0));//设置单元格线颜色
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(),bodyvos,"现 金 流 量 表 季 报",
                    new String[]{"xm","bnlj","jd1","jd2","jd3","jd4","bf_bnlj"},
                    new String[]{"项目","本年累计","第一季度","第二季度","第三季度","第四季度","上年同期数"},
                    new int[]{6,2,2,2,2,2,2},20,pmap,tmap);
        } catch (DocumentException e) {
            log.error("打印失败", e);
        } catch (IOException e) {
            log.error("打印失败", e);
        }
    }




}

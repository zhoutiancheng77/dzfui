package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.XsZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.XszExcelField;
import com.dzf.zxkj.report.service.cwzb.IXsZReport;
import com.dzf.zxkj.report.service.power.IButtonPowerService;
import com.dzf.zxkj.report.utils.ReportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("gl_rep_xszact")
@Slf4j
public class XszController  extends ReportBaseController {

    @Autowired
    private IXsZReport gl_rep_xszserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @Autowired
    private IButtonPowerService btn_power_ser;

    /**
     * 查询科目明细数据
     */
    @PostMapping("/query")
    public  ReturnData<Grid> queryAction(@MultiRequestBody KmReoprtQueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            queryvo= getQueryParamVO(queryvo,corpVO);
            int page = queryvo.getPage();
            int rows = queryvo.getRows();
            XsZVO[] kmmxvos = queryVOsFromCon(queryvo,corpVO);
            grid.setTotal((long) (kmmxvos==null?0:kmmxvos.length));
            if(kmmxvos!=null  && kmmxvos.length >0){
                kmmxvos = getPagedXSZVOs(kmmxvos,page,rows);
                grid.setRows(Arrays.asList(kmmxvos));
            }
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XsZVO>());
            grid.setSuccess(false);
            grid.setMsg("查询失败");
            log.error(e.getMessage(),e);
        }
        /** 日志记录 */
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "序时账查询:"+queryvo.getBegindate1() +"-"+ queryvo.getEnddate(), ISysConstants.SYS_2);
//        writeJson(grid);
        return ReturnData.ok().data(grid);
    }

    private KmReoprtQueryParamVO getQueryParamVO(KmReoprtQueryParamVO queryvo,CorpVO corpVO){
        if(StringUtil.isEmpty(queryvo.getPk_corp())){
            //如果编制单位为空则取当前默认公司
            queryvo.setPk_corp(corpVO.getPk_corp());
        }
        //把字符串变成codelist集合
        if(!StringUtil.isEmpty(queryvo.getKms())){
            List<String> codelist = Arrays.asList(queryvo.getKms().split(","));
            queryvo.setKmcodelist(codelist);
        }
        return queryvo;
    }

    private XsZVO[] queryVOsFromCon( KmReoprtQueryParamVO queryvo,CorpVO corpVO) {
        String pk_corp = queryvo.getPk_corp();
        /** 验证 查询范围应该在当前登录人的权限范围内 */
        checkPowerDate(queryvo,corpVO);
        String kms = null;
        String kmsx = null;
        String zdr  = null;
        String shr = null;
        XsZVO[] kmmxvos = null;
        kmmxvos = gl_rep_xszserv.getXSZVOs(pk_corp ,  kms ,  kmsx , zdr ,  shr, queryvo);
        Integer jd = new ReportUtil(zxkjPlatformService).getHlJd(pk_corp);
        if(kmmxvos!=null && kmmxvos.length>0){
            for(XsZVO xszvo:kmmxvos){
                if(xszvo.getHl()!=null){
                    xszvo.setHl(xszvo.getHl().setScale(jd, DZFDouble.ROUND_HALF_UP));
                }
            }
        }
        return kmmxvos;
    }

    /**
     * 将查询后的结果分页
     * @param kmmxvos
     * @param page
     * @param rows
     * @return
     */
    private XsZVO[] getPagedXSZVOs(XsZVO[] kmmxvos,int page,int rows){
        int beginIndex = rows * (page-1);
        int endIndex = rows*page;
        if(endIndex>=kmmxvos.length){//防止endIndex数组越界
            endIndex=kmmxvos.length;
        }
        kmmxvos = Arrays.copyOfRange(kmmxvos, beginIndex, endIndex);
        return kmmxvos;
    }

    private boolean checkExcelExport(String pk_corp,HttpServletResponse response) {
        String tips = btn_power_ser.qryButtonPower(pk_corp);
        if (!StringUtil.isEmpty(tips)) {
            PrintWriter pw = null;
            try {
                pw = response.getWriter();
                pw.write("<h4 style = 'margin:15% auto;color:red;font-size: 20px;text-align:center;padding:0px '>"+tips+"</h4>");
                pw.flush();
            } catch (IOException e) {

            } finally {
                if (pw != null) {
                    pw.close();
                }
            }
            return false;
        }
        return true;
    }
    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response){
        boolean bexport = checkExcelExport(queryparamvo.getPk_corp(),response);
        if(!bexport){
            return;
        }
//        XsZVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(),XsZVO[].class);
//        CorpVO qrycorpvo = zxkjPlatformService.queryCorpByPk(queryparamvo.getPk_corp());
        String gs= excelExportVO.getCorpName();
        String qj=  excelExportVO.getTitleperiod();
        XsZVO[] listVo = queryVOsFromCon(queryparamvo,corpVO);
        String currencyname = new ReportUtil().getCurrencyDw(queryparamvo.getCurrency());
        String[] periods = new String[]{qj};
        String[] allsheetname = new String[]{"序时账"};

        XszExcelField field = new XszExcelField("序时账", queryparamvo.getPk_currency(), currencyname, periods, allsheetname, qj,gs);
        List<XsZVO[]> result = new ArrayList<XsZVO[]>();
        result.add(listVo);
        field.setAllsheetzcvos(result);
        Excelexport2003<XsZVO> lxs = new Excelexport2003<XsZVO>();
        baseExcelExport(response,lxs,field);

        //日志记录
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "序时账导出:"+queryvo.getBegindate1() +"-"+ queryvo.getEnddate(), ISysConstants.SYS_2);
    }
}

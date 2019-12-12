package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.XsZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.XszExcelField;
import com.dzf.zxkj.report.service.cwzb.IXsZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("gl_rep_xszact")
@Slf4j
public class XszController  extends ReportBaseController {

    @Autowired
    private IXsZReport gl_rep_xszserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


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
            grid.setMsg("查询成功");
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<XsZVO>());
            grid.setSuccess(false);
            grid.setMsg("查询失败");
            log.error(e.getMessage(),e);
        }
        /** 日志记录 */
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "序时账查询:"+queryvo.getBegindate1() +"-"+ queryvo.getEnddate(), ISysConstants.SYS_2);
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
     * @param xsZVOS
     * @param page
     * @param rows
     * @return
     */
    private XsZVO[] getPagedXSZVOs(XsZVO[] xsZVOS,int page,int rows){
        int beginIndex = rows * (page-1);
        int endIndex = rows*page;
        if(endIndex>=xsZVOS.length){//防止endIndex数组越界
            endIndex=xsZVOS.length;
        }
        xsZVOS = Arrays.copyOfRange(xsZVOS, beginIndex, endIndex);
        return xsZVOS;
    }

    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response){
        boolean bexport = checkExcelExport(queryparamvo.getPk_corp(),response);
        if(!bexport){
            return;
        }
        String gs= excelExportVO.getCorpName();
        String qj=  excelExportVO.getTitleperiod();
        XsZVO[] listVo = queryVOsFromCon(queryparamvo,corpVO);
        String currencyname = new ReportUtil().getCurrencyDw(queryparamvo.getCurrency());
        String[] periods = new String[]{qj};
        String[] allsheetname = new String[]{"序时账"};

        XszExcelField field = new XszExcelField("序时账", queryparamvo.getPk_currency(), currencyname, periods, allsheetname, qj,gs, queryparamvo.getIsxshl());
        List<XsZVO[]> result = new ArrayList<XsZVO[]>();
        result.add(listVo);
        field.setAllsheetzcvos(result);
        Excelexport2003<XsZVO> lxs = new Excelexport2003<XsZVO>();
        baseExcelExport(response,lxs,field);

        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "序时账导出:"+queryparamvo.getBegindate1() +"-"+ queryparamvo.getEnddate(), ISysConstants.SYS_2);
    }

    public XsZVO[] reloadNewValue(String titlePeriod,String gs,KmReoprtQueryParamVO queryParamvo,CorpVO corpVO){
        //重新赋以下2个值
        XsZVO[] bodyvos = queryVOsFromCon(queryParamvo, corpVO);
        bodyvos[0].setGs(gs);
        bodyvos[0].setTitlePeriod(titlePeriod);
        return bodyvos;
    }


    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(@MultiRequestBody PrintParamVO printParamVO, @MultiRequestBody KmReoprtQueryParamVO queryParamvo, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            printReporUtil.setIscross(new DZFBoolean(pmap.get("pageOrt")));
            XsZVO[] bodyvos = reloadNewValue(printParamVO.getTitleperiod(),printParamVO.getCorpName(),queryParamvo,corpVO);

            ColumnCellAttr[] columncellattrvos = null;
            String isxshl= printParamVO.getIsxshl();
            List<ColumnCellAttr> list = new ArrayList<ColumnCellAttr>();
            if("true".equals(isxshl)){
                list.add(new ColumnCellAttr("日期",null,null,2,"rq",1));
                list.add(new ColumnCellAttr("年度",null,null,2,"year",1));
                list.add(new ColumnCellAttr("期间",null,null,2,"qj",1));
                list.add(new ColumnCellAttr("凭证字",null,null,2,"pzz",1));
                list.add(new ColumnCellAttr(" 凭证号",null,null,2,"pzh",1));
                list.add(new ColumnCellAttr("摘要",null,null,2,"zy",1));
                list.add(new ColumnCellAttr("科目编码",null,null,2,"kmbm",1));
                list.add(new ColumnCellAttr("科目名称",null,null,2,"kmmc",2));
                list.add(new ColumnCellAttr("币种",null,null,2,"bz",1));
                list.add(new ColumnCellAttr("汇率",null,null,2,"hl",1));
                list.add(new ColumnCellAttr("借方",null,2,null,null,4));
                list.add(new ColumnCellAttr("贷方",null,2,null,null,4));
                list.add(new ColumnCellAttr("原币",null,null,null,"ybjf",1));
                list.add(new ColumnCellAttr("本位币",null,null,null,"jfmny",1));
                list.add(new ColumnCellAttr("原币",null,null,null,"ybdf",1));
                list.add(new ColumnCellAttr("本位币",null,null,null,"dfmny",1));

            }else{
                list.add(new ColumnCellAttr("日期",null,null,null,"rq",1));
                list.add(new ColumnCellAttr(" 凭证号",null,null,null,"pzh",1));
                list.add(new ColumnCellAttr("摘要",null,null,null,"zy",1));
                list.add(new ColumnCellAttr("科目编码",null,null,null,"kmbm",1));
                list.add(new ColumnCellAttr("科目名称",null,null,null,"kmmc",2));
                list.add(new ColumnCellAttr("借方",null,null,null,"jfmny",1));
                list.add(new ColumnCellAttr("贷方",null,null,null,"dfmny",1));
            }
            columncellattrvos = list.toArray(new ColumnCellAttr[0]);


            //初始化表头
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", bodyvos[0].getGs());
            tmap.put("期间", bodyvos[0].getTitlePeriod());
            printReporUtil.setLineheight(22f);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));//设置表头字体
            //初始化表体列编码和列名称
            printReporUtil.printReport(bodyvos,"序 时 账", Arrays.asList(columncellattrvos),18,pmap.get("type"),pmap,tmap);

        } catch (Exception e) {
            log.error("打印错误",e);
        }
    }
}

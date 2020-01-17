package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.FzKmmxVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.FzmxMuiltSheetExcelField;
import com.dzf.zxkj.report.service.cwzb.IFzKmmxReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

@RestController
@RequestMapping("gl_rep_fzkmmxjact")
@Slf4j
public class FzKmmxController extends ReportBaseController {

    @Autowired
    private IFzKmmxReport gl_rep_fzkmmxjrptserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        queryparamvo = (KmReoprtQueryParamVO)getQueryParamVO(queryparamvo,corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
            /** 开始日期应该在建账日期前 */
            checkPowerDate(queryparamvo,corpVO);
            Object[] objs = gl_rep_fzkmmxjrptserv.getFzkmmxVos(queryparamvo, DZFBoolean.FALSE);
            List<FzKmmxVO> rsfzvos = (List<FzKmmxVO>) objs[0];
            formaterFzmxvo(rsfzvos, queryparamvo.getPk_corp());
            List<FzKmmxVO> fzkmms = (objs[1] == null)? new ArrayList<FzKmmxVO>() :(List<FzKmmxVO>) objs[1];
            if (fzkmms.size() > 0) {
                FzKmmxVO allfzkm = new FzKmmxVO();
                allfzkm.setId("all");
                allfzkm.setText("全选");
                fzkmms.add(0,allfzkm);
            }
            if (rsfzvos == null || rsfzvos.size() == 0) {
                grid.setRows(new ArrayList<FzKmmxVO>());
                grid.setSuccess(true);
                grid.setTotal((long)0);
                grid.setFzkmmx(fzkmms);
                grid.setMsg("查询数据为空!");
            } else {
                new ReportUtil().updateKFx(fzkmms.toArray(new FzKmmxVO[0]));
                grid.setFzkmmx(fzkmms);
                grid.setRows(rsfzvos);
                grid.setTotal((long)rsfzvos.size());
                grid.setSuccess(true);
                grid.setMsg("查询成功!");
            }

        } catch (Exception e) {
            grid.setRows(new ArrayList<FzKmmxVO>());
            printErrorLog(grid, e, "辅助明细账查询失败!");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT, "辅助明细账查询:"+queryparamvo.getBegindate1().toString().substring(0, 7)
                        +"-"+ queryparamvo.getEnddate().toString().substring(0, 7));
        return ReturnData.ok().data(grid);
    }

    private void putHlJd(FzKmmxVO[] bodyvos, Integer hljd) {
        if(bodyvos!=null && bodyvos.length>0){
            for(FzKmmxVO vo:bodyvos){
                if(!StringUtil.isEmpty(vo.getBz())){
                    String hl = DZFDouble.ZERO_DBL.setScale(hljd, DZFDouble.ROUND_HALF_UP).toString();
                    if(!StringUtil.isEmpty(vo.getHl())){
                        hl = new DZFDouble(vo.getHl()).setScale(hljd, DZFDouble.ROUND_HALF_UP).toString();
                    }
                    vo.setBz(vo.getBz()+"/"+hl);
                }
            }
        }
    }


    private void formaterFzmxvo(List<FzKmmxVO> rsfzvos, String pk_corp) {
        if  (rsfzvos!= null && rsfzvos.size() > 0) {
            Integer hljd =  new ReportUtil(zxkjPlatformService).getHlJd(pk_corp);
            for (FzKmmxVO mxzvo: rsfzvos) {
                if(!StringUtil.isEmpty(mxzvo.getBz()) && !StringUtil.isEmpty(mxzvo.getHl())){
                    mxzvo.setBz(mxzvo.getBz()+"/"+new DZFDouble(mxzvo.getHl()).setScale(hljd, DZFDouble.ROUND_HALF_UP).toString());
                }
            }
        }
    }
    /**
     * 导出Excel
     */
    @PostMapping("export/excel")
    public void excelReport( @MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo,@MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        FzKmmxVO[] listVo = null;
        queryparamvo = (KmReoprtQueryParamVO)getQueryParamVO(queryparamvo, corpVO);
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        String gs = excelExportVO.getCorpName();
        String qj = "";

        /** 获取汇率精度 */
        Integer hljd = new ReportUtil(zxkjPlatformService).getHlJd(queryparamvo.getPk_corp());
        if (!StringUtil.isEmpty(excelExportVO.getExport_all()) && "Y".equals(excelExportVO.getExport_all())) {
            Object[] objs = gl_rep_fzkmmxjrptserv.getFzkmmxVos(queryparamvo, DZFBoolean.FALSE);
            List<FzKmmxVO> rsfzvos = (List<FzKmmxVO>) objs[0];
            putFzlb_name(rsfzvos,excelExportVO.getFzlb_name());
            if (rsfzvos == null) {
                log.error("数据为空!");
                return;
            }
            ReportUtil.updateKFx(rsfzvos.toArray(new FzKmmxVO[0]));
            qj = DateUtils.getPeriod(queryparamvo.getBegindate1()) + "~" + DateUtils.getPeriod(queryparamvo.getEnddate());
            listVo = rsfzvos.toArray(new FzKmmxVO[0]);
            putHlJd(listVo, hljd);
        } else {
            String strlist = excelExportVO.getList();
            listVo =  JsonUtils.deserialize(strlist,FzKmmxVO[].class);
            putFzlb_name(Arrays.asList(listVo),excelExportVO.getFzlb_name());
            qj = excelExportVO.getTitleperiod();
        }
        if (listVo == null) {
            log.error("数据为空!");
            return;
        }
        Excelexport2003<FzKmmxVO> lxs = new Excelexport2003<FzKmmxVO>();
        FzmxMuiltSheetExcelField zcfz = getExcelField(listVo, gs, qj,excelExportVO.getIsPaging(),userVO);
        zcfz.setPk_currency(queryparamvo.getPk_currency());
        zcfz.setCurrencyname(new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
        baseExcelExport(response,lxs,zcfz);

        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "辅助明细账导出:" + queryparamvo.getBegindate1().toString().substring(0, 7) + "-"
                        + queryparamvo.getEnddate().toString().substring(0, 7));
    }

    private FzmxMuiltSheetExcelField getExcelField(FzKmmxVO[] listVo, String gs, String qj,String ispage,UserVO uservo) {
        FzmxMuiltSheetExcelField zcfz = new FzmxMuiltSheetExcelField();

        List<FzKmmxVO[]> listbvos = new ArrayList<FzKmmxVO[]>();

        List<String> periods = new ArrayList<String>();
        List<String> titlename = new ArrayList<String>();

        if("Y".equals(ispage)){
            Map<String, List<FzKmmxVO>> pagemap = new LinkedHashMap<String, List<FzKmmxVO>>();
            String fzname = "";
            String kmname = "";
            for (FzKmmxVO fzvo : listVo) {
                fzname = fzvo.getFzname() == null ? "":fzvo.getFzname();
                kmname = fzvo.getKmname() == null ? "":fzvo.getKmname()+"_";
                if (pagemap.containsKey(kmname +fzname )) {
                    pagemap.get(kmname +fzname ).add(fzvo);
                } else {
                    List<FzKmmxVO> list = new ArrayList<FzKmmxVO>();
                    list.add(fzvo);
                    pagemap.put(kmname +fzname , list);
                }
            }

            for (Map.Entry<String, List<FzKmmxVO>> entry : pagemap.entrySet()) {
                if(entry.getValue()!=null && entry.getValue().size()>0){
                    listbvos.add(entry.getValue().toArray(new FzKmmxVO[0]));
                    titlename.add(entry.getKey());
                    periods.add(qj);
                }
            }
        }else{
            listbvos.add(listVo);
            titlename.add("辅助明细表");
            periods.add(qj);
        }

        zcfz.setPeriods(periods.toArray(new String[0]));
        zcfz.setAllsheetname(titlename.toArray(new String[0]));
        zcfz.setZcfzvos(listVo);
        zcfz.setAllsheetzcvos(listbvos);
        zcfz.setQj(qj);
        zcfz.setCreator(uservo.getUser_name());
        zcfz.setCorpName(gs);
        return zcfz;
    }

    private void putFzlb_name(List<FzKmmxVO> rsfzvos, String fzlb_name) {
        if(rsfzvos!=null && rsfzvos.size()>0){
            for(FzKmmxVO vo:rsfzvos){
                vo.setFzlb(fzlb_name);
            }
        }
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {

            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            KmReoprtQueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), KmReoprtQueryParamVO.class);

            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            String strlist = printParamVO.getList();
            if (strlist == null) {
                return;
            }
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String print_all = pmap.get("print_all");
            FzKmmxVO[] bodyvos = JsonUtils.deserialize(printParamVO.getList(),FzKmmxVO[].class);
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));//设置表头字体

            List<Object> columns = getcolumns(queryparamvo.getPk_currency(), print_all, printParamVO.getIsPaging(),printParamVO.getShowbm());//字段名字

            printReporUtil.setLineheight(22f);
            if(!StringUtil.isEmpty(print_all) && "Y".equals(print_all)){
                Object[] objs = gl_rep_fzkmmxjrptserv.getFzkmmxVos(queryparamvo,DZFBoolean.FALSE);
                List<FzKmmxVO> rsfzvos = (List<FzKmmxVO>) objs[0];
                if(rsfzvos == null){
                    log.error("数据为空!");
                    return;
                }
                ReportUtil.updateKFx(rsfzvos.toArray(new FzKmmxVO[0]));
                tmap.put("期间", DateUtils.getPeriod(queryparamvo.getBegindate1())+"~"+DateUtils.getPeriod(queryparamvo.getEnddate()));
                bodyvos =  rsfzvos.toArray(new FzKmmxVO[0]);

                //汇率精度
                Integer hljd = new ReportUtil(zxkjPlatformService).getHlJd(queryparamvo.getPk_corp());

                putHlJd(bodyvos, hljd);

            }
            Map<String,List<SuperVO>> pagemap =  new LinkedHashMap<String, List<SuperVO>>();

            String titlename  = "辅助明细账";
            if("Y".equals(pmap.get("showlb")) && !StringUtil.isEmpty( pmap.get("fzlb_name"))){
                titlename = pmap.get("fzlb_name")+titlename;
            }
            if("Y".equals(pmap.get("isPaging"))){
                for(FzKmmxVO fzvo: bodyvos){
                    if(pagemap.containsKey(fzvo.getFzname()+fzvo.getKmname())){
                        pagemap.get(fzvo.getFzname()+fzvo.getKmname()).add(fzvo);
                    }else{
                        List<SuperVO> list = new ArrayList<SuperVO>();
                        list.add(fzvo);
                        pagemap.put(fzvo.getFzname()+fzvo.getKmname(), list);
                    }
                }
                titlename= "FZMX_辅助明细账";
            }

            if(StringUtil.isEmpty(pmap.get("showbm")) || "N".equals(pmap.get("showbm"))){//不显示编码
                if(bodyvos!=null && bodyvos.length>0){
                    for(FzKmmxVO vo:bodyvos){
                        if(!StringUtil.isEmpty(vo.getKmname())){
                            vo.setText(vo.getKmname() + "_"+vo.getFzname());
                        }else{
                            vo.setText(vo.getFzname());
                        }
                    }
                }
            }

            printReporUtil.printHz(pagemap, bodyvos, titlename,
                    (String[])columns.get(0), (String[])columns.get(1), (int[])columns.get(2), 20, pmap,tmap);

        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    private List<Object> getcolumns(String pk_currency,String print_all,String isPage,String showbm){
        List<Object> list = new ArrayList<>();
        if(!StringUtil.isEmpty(pk_currency) && !DzfUtil.PK_CNY.equals(pk_currency)){//外币
            if("Y".equals(isPage) || (StringUtil.isEmpty(print_all) || "N".endsWith(print_all))){//单独打印和分页打印显示项目名称
                list.add( new String[] {"rq", "pzh", "zy","bz", "ybjf","jf", "ybdf","df", "fx", "ybye", "ye"  });
                list.add( new String[] { "日期", "凭证号", "摘要","币别", "借方_原币","借方_本位币", "贷方_原币","贷方_本位币", "方向", "余额_原币", "余额_本位币" });
                list.add( new int[]{ 3, 2, 5, 3,3,3, 3,3, 1, 3,3 });
            }else{
                list.add( new String[] {"text", "rq", "pzh", "zy","bz", "ybjf","jf", "ybdf","df", "fx", "ybye", "ye"  });
                list.add( new String[] {"项目", "日期", "凭证号", "摘要","币别", "借方_原币","借方_本位币", "贷方_原币","贷方_本位币", "方向", "余额_原币", "余额_本位币" });
                list.add( new int[]{5, 3, 2, 5, 3,3,3, 3,3, 1, 3,3 });
            }
        }else{//人民币
            list.add( new String[] { "text","rq", "pzh", "zy", "jf", "df", "fx", "ye" });
            list.add(new String[] {"项目", "日期", "凭证号", "摘要", "借方", "贷方", "方向", "余额" });
            list.add(new int[] {5, 3, 2, 5, 3, 3, 1, 3 });
        }
        return list;
    }

}

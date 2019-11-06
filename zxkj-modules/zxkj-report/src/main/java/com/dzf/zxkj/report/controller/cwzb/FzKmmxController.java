package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            /** 开始日期应该在建账日期前 */
            checkPowerDate(queryparamvo,corpVO);
            Object[] objs = gl_rep_fzkmmxjrptserv.getFzkmmxVos(queryparamvo, DZFBoolean.FALSE);
            List<FzKmmxVO> rsfzvos = (List<FzKmmxVO>) objs[0];
            List<FzKmmxVO> fzkmms = (objs[1] == null)? new ArrayList<FzKmmxVO>() :(List<FzKmmxVO>) objs[1];
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
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "辅助明细账查询:"+queryparamvo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ queryparamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
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

    /**
     * 导出Excel
     */
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo,@MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        FzKmmxVO[] listVo = null;
        queryparamvo = (KmReoprtQueryParamVO)getQueryParamVO(queryparamvo, corpVO);
        String gs = corpVO.getUnitname();
        String qj = "";

        /** 获取汇率精度 */
        Integer hljd = new ReportUtil().getHlJd(queryparamvo.getPk_corp());
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
            qj = listVo.length > 0 ? listVo[0].getTitlePeriod() : "";
        }
        if (listVo == null) {
            log.error("数据为空!");
            return;
        }
        Excelexport2003<FzKmmxVO> lxs = new Excelexport2003<FzKmmxVO>();
        FzmxMuiltSheetExcelField zcfz = getExcelField(listVo, gs, qj,excelExportVO.getIsPaging(),userVO);
        zcfz.setPk_currency(queryparamvo.getPk_currency());
        zcfz.setCurrencyname(new ReportUtil().getCurrencyByPk(queryparamvo.getPk_currency()));
        baseExcelExport(response,lxs,zcfz);

//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "辅助明细账导出:" + queryparamvo.getBegindate1().toString().substring(0, 7) + "-"
//                        + queryparamvo.getEnddate().toString().substring(0, 7),
//                ISysConstants.SYS_2);
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


}

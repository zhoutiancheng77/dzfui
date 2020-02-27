package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.SubjectCollectGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.excel.cwzb.KmHzExcelField;
import com.dzf.zxkj.report.service.cwzb.IKmHzReport;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * 科目汇总表
 */
@RestController
@RequestMapping("gl_rep_kmhzbact")
@Slf4j
public class KmhzReportController extends ReportBaseController {

    @Autowired
    private IKmHzReport gl_rep_kmhzbserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询科目明细数据
     */
    @GetMapping("/queryAction")
    public ReturnData<Grid> queryAction(@RequestParam Map<String, String> param, @MultiRequestBody CorpVO corpVO) {
        SubjectCollectGrid grid = new SubjectCollectGrid();
        QueryParamVO vo = JsonUtils.convertValue(param, QueryParamVO.class);
        DZFDate begin = null;
        DZFDate end = null;
        try {
            // 校验
            checkSecurityData(null, new String[]{vo.getPk_corp()},null);
            /** 验证权限 */
            checkPowerDate(vo, corpVO);
            begin = vo.getBegindate1();
            end = vo.getEnddate();
            /** 查询 */
            List<Object> kmmxvos = gl_rep_kmhzbserv.getKMHzVOs(vo);
            /** 获取rows */
            List<FseJyeVO> reslist = getGridRows(vo, kmmxvos);
            /** 获取凭证数 */
            grid.setVoucherCount(kmmxvos.size() > 0 ? (Integer) kmmxvos.get(0) : 0);
            /** 获取附件单数 */
            grid.setBillCount(kmmxvos.size() > 1 ? (Integer) kmmxvos.get(1) : 0);
            grid.setTotal((long) (reslist == null ? 0 : reslist.size()));
            grid.setRows(reslist);
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<FseJyeVO>());
            log.error(e.getMessage(), e);
            grid.setSuccess(false);
            grid.setMsg(e instanceof BusinessException? e.getMessage() : "查询失败！");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "科目汇总表查询:" + begin +" - "+ end, ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    private List<FseJyeVO> getGridRows(QueryParamVO vo, List<Object> kmmxvos) {
        List<FseJyeVO> reslist = new ArrayList<FseJyeVO>();
        if (kmmxvos != null && kmmxvos.size() > 0) {
            reslist = Arrays.asList((FseJyeVO[]) kmmxvos.get(2));
        }
        /** 转换成tree类型 */
        reslist = conversionTree(reslist, vo.getCjq(), vo.getCjz());
        reslist = getTotalRow(reslist);
        return reslist;
    }

    private List<FseJyeVO> getTotalRow(List<FseJyeVO> reslist) {
        if(reslist==null || reslist.size()==0){
            return reslist;
        }
        FseJyeVO total = new FseJyeVO();

        DZFDouble fsjf = DZFDouble.ZERO_DBL;
        DZFDouble fsdf = DZFDouble.ZERO_DBL;
        for(FseJyeVO vo:reslist){
            fsjf = SafeCompute.add(fsjf, vo.getFsjf());
            fsdf = SafeCompute.add(fsdf, vo.getFsdf());
        }
        total.setKmbm("合计");
        total.setFsjf(fsjf);
        total.setFsdf(fsdf);
        reslist.add(total);

        return reslist;
    }

    private List<FseJyeVO> conversionTree(List<FseJyeVO> reslist, Integer cjq,Integer cjz) {
        List<FseJyeVO> res = new ArrayList<FseJyeVO>();
        for(FseJyeVO vo:reslist){
            if(vo.getAlevel() == cjq.intValue()){
                getTree(vo,reslist,vo.getAlevel()+1,cjz);
                res.add(vo);
            }
        }
        return res;
    }

    private void getTree(FseJyeVO vo, List<FseJyeVO> fsejyevos, int i,int cjz) {
        for(FseJyeVO childvo:fsejyevos){
            if(i>cjz){
                return;
            }else{
                if(childvo.getAlevel() == i && childvo.getKmbm().startsWith(vo.getKmbm())){
                    getTree(childvo, fsejyevos, i+1,cjz);
                    vo.addChildren(childvo);
                }
            }

        }
    }
    /**
     * 打印操作
     */
    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            // 校验
            checkSecurityData(null, new String[]{corpVO.getPk_corp()},null);
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            SubjectCollectGrid collVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), SubjectCollectGrid.class);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> params = printReporUtil.getPrintMap(printParamVO);

            String strlist = params.get("list");
            String type = params.get("type");
            String pageOrt = params.get("pageOrt");
            String left = params.get("left");
            String top = params.get("top");
            String printdate = params.get("printdate");
            String font = params.get("font");
            String pageNum = params.get("pageNum");
            /** 声明一个map用来存前台传来的设置参数 */
            Map<String, String> pmap = new HashMap<String, String>();
            pmap.put("type", type);
            pmap.put("pageOrt", pageOrt);
            pmap.put("left", left);
            pmap.put("top", top);
            pmap.put("printdate", printdate);
            pmap.put("font", font);
            pmap.put("pageNum", pageNum);
            if (strlist == null) {
                return;
            }
            /** 是否横向 */
            if (pageOrt.equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);
            }
//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new PzmbbVO());
//            FseJyeVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, FseJyeVO[].class,
//                    JSONConvtoJAVA.getParserConfig());
            FseJyeVO[] bodyvos = JsonUtils.deserialize(strlist, FseJyeVO[].class);
            /** 声明一个map用来存前台传来的设置参数 */
            Map<String,String> tmap=new LinkedHashMap<String,String>();
            tmap.put("公司",  printParamVO.getCorpName());
            tmap.put("期间",  printParamVO.getTitleperiod());
            tmap.put("凭证数",  collVO.getVoucherCount() + "");
            tmap.put("附件数",  collVO.getBillCount() + "");
            /** 设置表头字体 */
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));
            printReporUtil.setLineheight(22f);
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "科 目 汇 总 表",
                    new String[] { "kmlb", "kmbm", "kmmc", "fsjf", "fsdf" },
                    new String[] { "科目类别", "科目编码", "科目名称", "本期发生借方", "本期发生贷方" }, new int[] { 1, 1, 3, 2, 2 }, 0, type,
                    pmap,tmap);
            writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                    "科目汇总表打印:" + printParamVO.getTitleperiod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }
    /**
     * 导出excel
     */
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody PrintParamVO printParamVO,
                            @MultiRequestBody QueryParamVO qryvo,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        // 校验
        checkSecurityData(null, new String[]{corpVO.getPk_corp()},null);
//        HttpServletRequest request = getRequest();
        String strlist = printParamVO.getList();
//        JSONArray array = (JSONArray) JSON.parseArray(strlist);
//        Map<String, String> bodymapping = FieldMapping.getFieldMapping(new PzmbbVO());
//        FseJyeVO[] vo = DzfTypeUtils.cast(array, bodymapping, FseJyeVO[].class, JSONConvtoJAVA.getParserConfig());
        FseJyeVO[] vo = JsonUtils.deserialize(strlist, FseJyeVO[].class);
        String gs = printParamVO.getCorpName();
        String qj = printParamVO.getTitleperiod();
        Excelexport2003<FseJyeVO> lxs = new Excelexport2003<FseJyeVO>();
        KmHzExcelField xsz = new KmHzExcelField();
        xsz.setFseJyeVOs(vo);
        xsz.setQj(qj);
        xsz.setCreator(userVO.getUser_name());
        xsz.setCorpName(gs);
//        HttpServletResponse response = getResponse();
        OutputStream toClient = null;
        try {
            response.reset();
            String filename = xsz.getExcelport2003Name();
            String formattedName = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            lxs.exportExcel(xsz, toClient);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("excel导出错误",e);
        }finally{
            try{
                if(toClient != null){
                    toClient.close();
                }
            }catch(IOException e){
                log.error("excel导出错误",e);
            }
            try{
                if(response!=null && response.getOutputStream() != null){
                    response.getOutputStream().close();
                }
            }catch(IOException e){
                log.error("excel导出错误",e);
            }
        }

//        QueryParamVO qryvo = getQueryParamVO();
//
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "科目汇总表导出:"+ qryvo.getBegindate1() +" - "+ qryvo.getEnddate(), ISysConstants.SYS_2);
    }

}

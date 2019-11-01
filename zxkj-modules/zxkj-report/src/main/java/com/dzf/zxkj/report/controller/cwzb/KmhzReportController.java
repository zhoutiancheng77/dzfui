package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.SubjectCollectGrid;
import com.dzf.zxkj.report.service.cwzb.IKmHzReport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.management.Query;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 科目汇总表
 */
@RestController
@RequestMapping("gl_rep_kmhzbact")
@Slf4j
public class KmhzReportController {

    @Autowired
    private IKmHzReport gl_rep_kmhzbserv;

    /**
     * 查询科目明细数据
     */
    @GetMapping("/queryAction")
    public ReturnData<Grid> queryAction(@RequestParam Map<String, String> param) {
        SubjectCollectGrid grid = new SubjectCollectGrid();
        QueryParamVO vo = JsonUtils.convertValue(param, QueryParamVO.class);
        try {
            /** 验证权限 */
//            checkPowerDate(vo);
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
            grid.setMsg("查询失败！");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "科目汇总表查询:"+vo.getBegindate1() +"-"+ vo.getEnddate(), ISysConstants.SYS_2);
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

//    private QueryParamVO getQueryParamVO(){
//        QueryParamVO paramvo = new QueryParamVO();
////        paramvo = (QueryParamVO) DzfTypeUtils.cast(getRequest(), paramvo);
//        if(paramvo.getPk_corp()==null || paramvo.getPk_corp().trim().length()==0){
//            /** 如果编制单位为空则取当前默认公司 */
//            String corpVo = SystemUtil.getLoginCorpId();
//            paramvo.setPk_corp(corpVo);
//        }
//
//        /** 把字符串变成codelist集合 */
//        if(paramvo.getKms()!=null && paramvo.getKms().length()>0){
//            List<String> codelist = Arrays.asList(paramvo.getKms().split(","));
//            paramvo.setKmcodelist(codelist);
//        }
//        return paramvo;
//    }
//    /**
//     * 打印操作
//     */
//    public void printAction() {
//        try {
//            String strlist = getRequest().getParameter("list");
//            String type = getRequest().getParameter("type");
//            String pageOrt = getRequest().getParameter("pageOrt");
//            String left = getRequest().getParameter("left");
//            String top = getRequest().getParameter("top");
//            String printdate = getRequest().getParameter("printdate");
//            String font = getRequest().getParameter("font");
//            String pageNum = getRequest().getParameter("pageNum");
//            /** 声明一个map用来存前台传来的设置参数 */
//            Map<String, String> pmap = new HashMap<String, String>();
//            pmap.put("type", type);
//            pmap.put("pageOrt", pageOrt);
//            pmap.put("left", left);
//            pmap.put("top", top);
//            pmap.put("printdate", printdate);
//            pmap.put("font", font);
//            pmap.put("pageNum", pageNum);
//            if (strlist == null) {
//                return;
//            }
//            /** 是否横向 */
//            if (pageOrt.equals("Y")) {
//                setIscross(DZFBoolean.TRUE);
//            } else {
//                setIscross(DZFBoolean.FALSE);
//            }
//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String, String> bodymapping = FieldMapping.getFieldMapping(new PzmbbVO());
//            FseJyeVO[] bodyvos = DzfTypeUtils.cast(array, bodymapping, FseJyeVO[].class,
//                    JSONConvtoJAVA.getParserConfig());
//            /** 声明一个map用来存前台传来的设置参数 */
//            Map<String,String> tmap=new LinkedHashMap<String,String>();
//            tmap.put("公司",  bodyvos[0].getGs());
//            tmap.put("期间",  bodyvos[0].getTitlePeriod());
//            tmap.put("凭证数",  bodyvos[0].getBsh());
//            tmap.put("附件数",  bodyvos[0].getBills());
//            /** 设置表头字体 */
//            setTableHeadFount(new Font(getBf(), Float.parseFloat(font), Font.NORMAL));
//            setLineheight(22f);
//            printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "科 目 汇 总 表",
//                    new String[] { "kmlb", "kmbm", "kmmc", "fsjf", "fsdf" },
//                    new String[] { "科目类别", "科目编码", "科目名称", "本期发生借方", "本期发生贷方" }, new int[] { 1, 1, 3, 2, 2 }, 0, type,
//                    pmap,tmap);
//        } catch (DocumentException e) {
//            log.error("打印错误",e);
//        } catch (IOException e) {
//            log.error("打印错误",e);
//        }
//    }
//    /**
//     * 导出excel
//     */
//    public void excelReport(){
//        HttpServletRequest request = getRequest();
//        String strlist = request.getParameter("list");
//        JSONArray array = (JSONArray) JSON.parseArray(strlist);
//        Map<String, String> bodymapping = FieldMapping.getFieldMapping(new PzmbbVO());
//        FseJyeVO[] vo = DzfTypeUtils.cast(array, bodymapping, FseJyeVO[].class, JSONConvtoJAVA.getParserConfig());
//        String gs = vo[0].getGs();
//        String qj = vo[0].getTitlePeriod();
//        Excelexport2003<FseJyeVO> lxs = new Excelexport2003<FseJyeVO>();
//        KmHzExcelField xsz = new KmHzExcelField();
//        xsz.setFseJyeVOs(vo);
//        xsz.setQj(qj);
//        xsz.setCreator(getLoginUserInfo().getUser_name());
//        xsz.setCorpName(gs);
//        HttpServletResponse response = getResponse();
//        OutputStream toClient = null;
//        try {
//            response.reset();
//            String filename = xsz.getExcelport2003Name();
//            String formattedName = URLEncoder.encode(filename, "UTF-8");
//            response.addHeader("Content-Disposition", "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
//            toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/vnd.ms-excel;charset=gb2312");
//            lxs.exportExcel(xsz, toClient);
//            toClient.flush();
//            response.getOutputStream().flush();
//        } catch (IOException e) {
//            log.error("excel导出错误",e);
//        }finally{
//            try{
//                if(toClient != null){
//                    toClient.close();
//                }
//            }catch(IOException e){
//                log.error("excel导出错误",e);
//            }
//            try{
//                if(response!=null && response.getOutputStream() != null){
//                    response.getOutputStream().close();
//                }
//            }catch(IOException e){
//                log.error("excel导出错误",e);
//            }
//        }
//
//        QueryParamVO qryvo = getQueryParamVO();
//
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "科目汇总表导出:"+qryvo.getBegindate1() +"-"+ qryvo.getEnddate(), ISysConstants.SYS_2);
//    }
//
//    private void checkPowerDate(QueryParamVO vo) {
//        Set<String> powercorpSet = userService.querypowercorpSet(getLoginUserid());
//        if (!powercorpSet.contains(vo.getPk_corp())) {
//            throw new BusinessException("无权操作！");
//        }
//
//        /** 开始日期应该在建账日期前 */
//        CorpVO currcorp = CorpCache.getInstance().get("", vo.getPk_corp());
//        DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getBegindate()));
//        if (begdate.after(vo.getBegindate1())) {
//            throw new BusinessException("开始日期不能在建账日期(" + DateUtils.getPeriod(begdate) + ")前!");
//        }
//    }
}

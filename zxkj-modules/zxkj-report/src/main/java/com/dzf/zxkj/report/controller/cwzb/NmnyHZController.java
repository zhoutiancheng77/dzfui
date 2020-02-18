package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryCondictionVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.NumMnyGlVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.excel.cwzb.NmnyHzExcelField;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.SystemUtil;
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
 * 数量金额总账
 */
@RestController
@RequestMapping("gl_rep_sljezzact")
@Slf4j
public class NmnyHZController extends ReportBaseController {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private INummnyReport gl_rep_nmdtserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("/queryAction")
    public ReturnData<Grid> query(@RequestParam Map<String, String> param) {
        Grid grid = new Grid();
        QueryCondictionVO paramVO = new  QueryCondictionVO();
        try {
            paramVO = JsonUtils.convertValue(param, QueryCondictionVO.class);
            checkOwnCorp(paramVO.getPk_corp());
            //前台传参三个值,前两个值为必输项目，第三个为非必输项目
            CorpVO corp = zxkjPlatformService.queryCorpByPk(SystemUtil.getLoginCorpId());
            paramVO.setPk_corp(corp.getPk_corp());
            paramVO.setJzdate(corp.getBegindate());
            paramVO.setIsic(new DZFBoolean(IcCostStyle.IC_ON.equals(corp.getBbuildic())));

            // 开始日期应该在建账日期前
//            CorpVO currcorp = CorpCache.getInstance().get("", paramVO.getPk_corp());
            DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(corp.getBegindate()));
            DZFDate qrybegdate = DateUtils.getPeriodEndDate(paramVO.getQjq());
            if (begdate.after(qrybegdate)) {
                throw new BusinessException("开始日期不能在建账日期(" + DateUtils.getPeriod(begdate) + ")前!");
            }

            List<NumMnyGlVO> kmmxvos = gl_rep_nmdtserv.getNumMnyGlVO(paramVO);

            if(kmmxvos!=null && kmmxvos.size()>0){
                ReportUtil.updateKFx(kmmxvos.toArray(new NumMnyGlVO[0]));
            }
            kmmxvos = conversionTree(kmmxvos,paramVO.getCjq(),paramVO.getCjz(),paramVO.getPk_corp());


            grid.setTotal((long) (kmmxvos==null?0:kmmxvos.size()));
            grid.setRows(kmmxvos);
            grid.setSuccess(true);
            grid.setMsg("查询成功！");
        } catch (Exception e) {
			log.error(e.getMessage());
			grid.setSuccess(false);
            grid.setRows(new ArrayList<NumMnyGlVO>());
			grid.setMsg("查询失败:"+e.getMessage());
        }
        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                new StringBuffer().append("数量金额总账查询:").
                        append(paramVO.getQjq()).append("-").append(paramVO.getQjz()).toString(),
                ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }
    private List<NumMnyGlVO> conversionTree(List<NumMnyGlVO> kmmxvos, Integer cjq,Integer cjz,String pk_corp) {
        YntCpaccountVO[] cpavos = zxkjPlatformService.queryByPk(pk_corp);
        Map<String,YntCpaccountVO> map = new HashMap<String,YntCpaccountVO>();
        for(YntCpaccountVO vo:cpavos){
            map.put(vo.getAccountcode(), vo);
        }
        //赋值科目层级
        YntCpaccountVO tvo = null;
        for(NumMnyGlVO vo:kmmxvos){
            tvo = map.get(vo.getKmbm());
            if(tvo!=null){
                vo.setAccountlevel(tvo.getAccountlevel());
            }
        }
        List<NumMnyGlVO>  reslist = new ArrayList<NumMnyGlVO>();
        for(NumMnyGlVO vo:kmmxvos){
            if(vo.getAccountlevel()!=null && vo.getAccountlevel() ==  cjq.intValue()){
                getTree(vo, kmmxvos, cjq+1,cjz.intValue());
                reslist.add(vo);
            }
        }
        return reslist;
    }

    private void getTree(NumMnyGlVO vo, List<NumMnyGlVO> kmmxvos, int i,int cjz) {
        for(NumMnyGlVO childvo:kmmxvos){
            if(i>cjz+1){
                return;
            } else {
                if (childvo.getKmbm().startsWith(vo.getKmbm())) {
                    if (childvo.getAccountlevel() != null && childvo.getAccountlevel() == i) {
                        getTree(childvo, kmmxvos, i + 1,cjz);
                        vo.addChildren(childvo);
                    }else if (childvo.getAccountlevel() == null && childvo.getKmbm().indexOf("_") > 0
                            && vo.getKmbm().equals(childvo.getKmbm().split("_")[0])
                    ) {
                        vo.addChildren(childvo);
                    }
                }
            }

        }
    }

    @PostMapping("print")
    public void print(@RequestParam Map<String, String> pmap1,
                      @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
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
            String showbnljjf = printParamVO.getShowjf();
            String showbnljdf = printParamVO.getShowdf();
            Map<String,String> pmap=new HashMap<String,String>();//声明一个map用来存前台传来的设置参数
            pmap.put("type",type);
            pmap.put("pageOrt",pageOrt);
            pmap.put("left",left);
            pmap.put("top",top);
            pmap.put("printdate",printdate);
            pmap.put("font",font);
            pmap.put("pageNum",pageNum);
            if(strlist==null){
                return;
            }
            if(pageOrt.equals("Y")){
                printReporUtil.setIscross(DZFBoolean.TRUE);//是否横向
            }
            else{
                printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向
            }
//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String,String> bodymapping=FieldMapping.getFieldMapping(new ZcZzVO());
//            NumMnyGlVO[] bodyvos =DzfTypeUtils.cast(array,bodymapping, NumMnyGlVO[].class, JSONConvtoJAVA.getParserConfig());
            NumMnyGlVO[] bodyvos = JsonUtils.deserialize(strlist, NumMnyGlVO[].class);
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", bodyvos[0].getGs());
            tmap.put("期间", bodyvos[0].getTitlePeriod());
            String corp = corpVO.getPk_corp();
            CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, corp);

            printReporUtil.setLineheight(22f);
            setDefaultValue(bodyvos, corp);//为后续设置精度赋值

            String[] columnames = new String[]{"科目编码","科目名称","计量单位","方向","期初余额","本期借方","本期贷方","本年累计借方","本年累计贷方","期末余额","数量","单价","金额","数量","金额","数量","金额","数量","金额","数量","金额","数量","单价","金额"};
            String[] columnkeys = new String[]{"kmbm","kmmc","dw","dir","qcnum","qcprice","qcmny","bqjfnum","bqjfmny","bqdfnum","bqdfmny","bnjfnum","bnjfmny","bndfnum","bndfmny","qmnum","qmprice","qmmny"};
            int[] columnints = new int[]{3,5,3,1,3,3,3,3,3,3,3,3,3,3,3,3,3,3};
            LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
            List<String> columnkeylist  = new ArrayList<String>();
            List<String> columnameslist  = new ArrayList<String>();
            List<Integer> columnintlist = new ArrayList<Integer>();
            for(int i =0;i<columnkeys.length;i++){
                if("N".equals(showbnljjf) && ("bnjfnum".equals(columnkeys[i]) || "bnjfmny".equals(columnkeys[i]))){
                    continue;
                }
                if("N".equals(showbnljdf) && ("bndfnum".equals(columnkeys[i]) || "bndfmny".equals(columnkeys[i]))){
                    continue;
                }
                columnkeylist.add(columnkeys[i]);
                columnintlist.add(columnints[i]);
            }
            for(int i=0;i<columnames.length;i++){
                if("N".equals(showbnljjf) && (i == 7  || i== 13 || i ==14)){//本年累计借方
                    continue;
                }
                if("N".equals(showbnljdf) && (i == 8 || i== 15 || i ==16) ){//本年累计贷方
                    continue;
                }
                columnameslist.add(columnames[i]);
                ColumnCellAttr attr = new ColumnCellAttr();
                attr.setColumname(columnames[i]);
                if(i==0 ||  i==1 || i==2|| i==3){
                    attr.setRowspan(2);
                }else if(i == 4){
                    attr.setColspan(3);
                }else if(i==5){
                    attr.setColspan(2);
                }else if(i==6){
                    attr.setColspan(2);
                }else if(i==7){
                    attr.setColspan(2);
                }else if(i==8){
                    attr.setColspan(2);
                }else if(i==9){
                    attr.setColspan(3);
                }

                columnlist.add(attr);
            }
            columnints = new int[columnintlist.size()];
            for(int i =0;i<columnintlist.size();i++){
                columnints[i] = columnintlist.get(i);
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            if (type.equals("1"))
                printReporUtil.printGroup(new HashMap<String, List<SuperVO>>(),bodyvos, "数 量 金 额 总 账", columnkeylist.toArray(new String[0]),
                        columnameslist.toArray(new String[0]), columnlist,columnints, 0,null,pmap,tmap);// A4纸张打印
            else if(type.equals("2")){
                printReporUtil.printB5(new HashMap<String, List<SuperVO>>(),bodyvos, "数 量 金 额 总 账", columnkeylist.toArray(new String[0]),
                        columnameslist.toArray(new String[0]), columnlist, columnints, 0,null,pmap,tmap);
            }

        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    private void setDefaultValue(NumMnyGlVO[] bodyvos, String pk_corp){
        if(bodyvos != null && bodyvos.length > 0){
            for(NumMnyGlVO vo : bodyvos){
                vo.setPk_corp(pk_corp);
            }
        }
    }

    private void setExprotInfo(NmnyHzExcelField xsz){
        xsz.setFields(xsz.getFields1());
    }

    //导出excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody PrintParamVO printParamVO,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
//        QueryCondictionVO paramVO = new  QueryCondictionVO();
//        paramVO = JsonUtils.convertValue(params, QueryCondictionVO.class);
//        paramVO = (QueryCondictionVO) DzfTypeUtils.cast(getRequest(), paramVO);

        String showbnljjf = printParamVO.getShowjf();
        String showbnljdf = printParamVO.getShowdf();

        String strlist = printParamVO.getList();
//        JSONArray array = (JSONArray) JSON.parseArray(strlist);
//        Map<String,String> bodymapping= FieldMapping.getFieldMapping(new PzmbbVO());
//        NumMnyGlVO[] vo =DzfTypeUtils.cast(array,bodymapping, NumMnyGlVO[].class, JSONConvtoJAVA.getParserConfig());
        NumMnyGlVO[] vo = JsonUtils.deserialize(strlist, NumMnyGlVO[].class);

        String gs =  vo[0].getGs();
        String qj =  vo[0].getTitlePeriod();
        Excelexport2003<NumMnyGlVO> lxs = new Excelexport2003<NumMnyGlVO>();

        String pk_corp = corpVO.getPk_corp();
        String numStr = printParamVO.getNumstr();
        String priceStr = printParamVO.getPricestr();
        int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

        NmnyHzExcelField xsz = new NmnyHzExcelField(num, price,showbnljjf,showbnljdf);
        setExprotInfo(xsz);
        xsz.setNumMnyGlVOs(vo);
        xsz.setQj(qj);
        xsz.setCreator(userVO.getUser_name());
        xsz.setCorpName(gs);
        OutputStream toClient = null;
        try {
            response.reset();
//			String filename = xsz.getExcelport2007Name();
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
        //日志记录
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                new StringBuffer().append("数量金额总账导出:")
                        .append(qj).toString(), ISysConstants.SYS_2);
    }


}

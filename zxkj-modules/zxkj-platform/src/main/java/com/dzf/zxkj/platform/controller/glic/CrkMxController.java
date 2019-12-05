package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.CrkMxNewExcelField;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.report.IcDetailFzVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.glic.ICrkMxService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.SystemUtil;
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
 * 出入库明细
 * @author zhangj
 *
 */
@RestController
@RequestMapping("/glic/gl_rep_crkmxact")
@Slf4j
public class CrkMxController extends GlicReportController{

    @Autowired
    private ICrkMxService gl_rep_crkmxserv;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IParameterSetService parameterserv;

    @GetMapping("/query")
    public ReturnData queryAction(@RequestParam Map<String, String> param){
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        checkPowerDate(queryParamvo);
        String currsp = param.get("currsp");
        Map<String, List<IcDetailVO>> result = null;
        result = gl_rep_crkmxserv.queryMx(queryParamvo, SystemUtil.getLoginCorpVo());

        if(result == null || result.size() == 0){
            throw new BusinessException("查询数据为空");
        }
        List<IcDetailFzVO> listsps = createRightTree(result,currsp);
        //将查询后的数据分页展示
        List<IcDetailVO> list = getPagedMXZVos(listsps, result, queryParamvo.getPage(), queryParamvo.getRows(), grid, currsp);
        grid.setIccombox(listsps);
        grid.setKcDetail(list);
        grid.setRows(list);
        grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    private List<IcDetailVO> getPagedMXZVos(List<IcDetailFzVO> listsps,
                                            Map<String, List<IcDetailVO>> result, int page, int rows,
                                            ReportDataGrid grid, String currsp){

        if(listsps == null || listsps.size() == 0){
            grid.setTotal(0L);
            return new ArrayList<IcDetailVO>();
        }
        if(StringUtil.isEmpty(currsp)){
            currsp = listsps.get(0).getId();
            if("all".equals(currsp)){//取第二个
                currsp = listsps.get(1).getId();
            }
        }

        Map<String, List<IcDetailVO>> spMap = new LinkedHashMap<String, List<IcDetailVO>>();
        List<IcDetailVO> icvlist = null;
        List<IcDetailVO> flist = null;
        for(Map.Entry<String, List<IcDetailVO>> entry : result.entrySet()){
            icvlist = entry.getValue();
            for(IcDetailVO icv:icvlist){
                if(spMap.containsKey(icv.getPk_sp())){
                    spMap.get(icv.getPk_sp()).add(icv);
                }else{
                    flist = new ArrayList<IcDetailVO>();
                    flist.add(icv);
                    spMap.put(icv.getPk_sp(), flist);
                }
            }
        }
        List<IcDetailVO> spList = new ArrayList<IcDetailVO>();//spMap.get(currsp);
        if(!StringUtil.isEmpty(currsp) && currsp.startsWith("all")){
            for(Map.Entry<String, List<IcDetailVO>> entry:spMap.entrySet()){
                if(entry.getValue()!=null && entry.getValue().size()>0){
                    for(IcDetailVO detailvo:entry.getValue()){
                        spList.add(detailvo);
                    }
                }
            }
        }else{
            String[] currsps = currsp.split(",");
            for(String str:currsps){
                if(spMap.get(str)!=null){
                    for(IcDetailVO detailvo:spMap.get(str)){
                        spList.add(detailvo);
                    }
                }
            }

            if(spList == null || spList.size() == 0){
                String firstkey = "";
                for(Map.Entry<String, List<IcDetailVO>> entry:spMap.entrySet()){
                    if(!StringUtil.isEmpty( entry.getKey())){
                        firstkey = entry.getKey();
                        break;
                    }
                }
                spList = spMap.get(firstkey);
            }
        }

        List<IcDetailVO> resList= new ArrayList<IcDetailVO>();
        if(spList != null && spList.size() > 0){
            int start= (page-1)*rows;
            for(int i = start ; i < page * rows && i < spList.size(); i++){
                resList.add(spList.get(i));
            }
            grid.setTotal((long)spList.size());
        }else{
            spList = new ArrayList<IcDetailVO>();
            grid.setTotal(0L);
        }

        return resList;
    }

    private List<IcDetailFzVO> createRightTree(Map<String, List<IcDetailVO>> result,String currsp){
        IcDetailFzVO fzvo = null;
        Set<String> conkeys = new HashSet<String>();
        List<IcDetailVO> volist = null;
        IcDetailVO vofirst = null;
        List<IcDetailFzVO> listsps_temp = new ArrayList<IcDetailFzVO>();

        for(Map.Entry<String, List<IcDetailVO>> entry : result.entrySet()){
            String key = entry.getKey();
            key = key.length() > 49 ? key.substring(0, 49) : key;
            volist = entry.getValue();
            vofirst = volist.get(0);
            if(!conkeys.contains(key) && !StringUtil.isEmpty(vofirst.getSpbm())){
                fzvo = new IcDetailFzVO();
                fzvo.setId(vofirst.getPk_sp());
                fzvo.setSpfl(vofirst.getSpfl());//商品分类

                if(StringUtil.isEmpty(vofirst.getSpgg())){
                    fzvo.setText(vofirst.getSpbm() + " " + vofirst.getSpmc());
                }else{
                    fzvo.setText(vofirst.getSpbm() + " " + vofirst.getSpmc()+"("+vofirst.getSpgg()+")");
                }

                if(StringUtil.isEmpty(vofirst.getSpmc())
                        && vofirst.getPk_sp().endsWith("_fl")){//商品分类
                    fzvo.setText(vofirst.getSpbm()+" "+vofirst.getSpfl_name());
                }
                fzvo.setCode(vofirst.getSpbm());
                conkeys.add(key);
                listsps_temp.add(fzvo);
            }

        }
        if(listsps_temp != null && listsps_temp.size() > 0){
            List<String> checklist = new ArrayList<String>();
            Collections.sort(listsps_temp, new Comparator<IcDetailFzVO>() {
                @Override
                public int compare(IcDetailFzVO o1, IcDetailFzVO o2) {
                    int i = o1.getText().compareTo(o2.getText());
                    return i;
                }
            });

            if(!StringUtil.isEmpty(currsp)){
                String[] values = currsp.split(",");
                checklist = Arrays.asList(values);
            }

            IcDetailFzVO alldetailvo = new IcDetailFzVO();
            alldetailvo.setCode("all");
            alldetailvo.setId("all");
            alldetailvo.setText("全选");
            listsps_temp.add(0, alldetailvo);

            for(IcDetailFzVO vo1:listsps_temp){
                if(checklist.contains(vo1.getId()) || (!StringUtil.isEmpty(currsp) && currsp.startsWith("all")) ){
                    vo1.setChecked("true");
                    if(StringUtil.isEmpty(currsp)){//科目为空，自动带出默认值
                        vo1.setBdefault(DZFBoolean.TRUE);
                    }
                }

            }
        }


        //分组区分（怎么取根节点）
        List<IcDetailFzVO> listsps = new ArrayList<IcDetailFzVO>();
        //如果没有分类，或者分类的id是空则是根节点(按照分类核算，按照明细核算)
        for(IcDetailFzVO vo:listsps_temp){
            if(vo.getId().endsWith("_fl") ||  StringUtil.isEmpty(vo.getSpfl())){
                listsps.add(vo);
            }
        }
        //该商品下的则是子vo
        String flid = "";
        for(IcDetailFzVO vo_parent:listsps){//父vo
            List<IcDetailFzVO> childvos = new ArrayList<IcDetailFzVO>();
            for(IcDetailFzVO vo:listsps_temp){
                flid = vo.getSpfl()+"_fl";
                if(!StringUtil.isEmpty(vo.getSpfl()) && (flid).equals(vo_parent.getId()) && !vo.getId().equals(vo_parent.getId())){
                    vo_parent.setState("closed");
                    childvos.add(vo);
                }
            }
            if(childvos!=null && childvos.size()>0){
                vo_parent.setChildren((SuperVO[])childvos.toArray(new IcDetailFzVO[0]));
            }
        }
        return listsps;
    }


    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap, HttpServletResponse response) {
        String period = "";
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
            PrintParamVO printvo = JsonUtils.convertValue(pmap, PrintParamVO.class);//
            String strlist = pmap.get("list");
            if (strlist == null) {
                return;
            }
            if (printvo.getPageOrt().equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
            strlist = strlist.replace("}{", "},{");
            IcDetailVO[] bodyvos =JsonUtils.deserialize(strlist, IcDetailVO[].class);
            period = bodyvos[0].getTitlePeriod();
            String gs = bodyvos[0].getGs();

            String current =pmap.get("curr_print");
            QueryParamVO queryParamvo = JsonUtils.convertValue(pmap, QueryParamVO.class);
            bodyvos = queryVos(getQueryParamVO(queryParamvo), current,pmap);

            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", gs);
            tmap.put("期间", period);
            printReporUtil.setTableHeadFount(new Font( printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));//设置表头字体
            List<ColumnCellAttr> list = new ArrayList<>();
            String strclassif = pmap.get("classif");
            DZFBoolean flag = new DZFBoolean(strclassif);

            list.add(new ColumnCellAttr("日期",null,null,2,"dbilldate",1));
            if(!flag.booleanValue())
                list.add(new ColumnCellAttr("存货类别",null,null,2,"spfl",1));
            list.add(new ColumnCellAttr("存货名称",null,null,2,"spmc",1));
            list.add(new ColumnCellAttr("摘要",null,null,2,"zy",1));
            list.add(new ColumnCellAttr("出入库单号",null,null,2,"vicbillcode",1));
            list.add(new ColumnCellAttr("凭证号",null,null,2,"pzh",1));
            list.add(new ColumnCellAttr("规格(型号)",null,null,2,"spgg",1));
            list.add(new ColumnCellAttr("计量单位",null,null,2,"jldw",1));
            list.add(new ColumnCellAttr("入库",null,3,null,null,6));
            list.add(new ColumnCellAttr("出库",null,3,null,null,6));
            list.add(new ColumnCellAttr("结存",null,3,null,null,6));
            list.add(new ColumnCellAttr("数量",null,null,null,"srsl",1));
            list.add(new ColumnCellAttr("单价",null,null,null,"srdj",1));
            list.add(new ColumnCellAttr("金额",null,null,null,"srje",1));

            list.add(new ColumnCellAttr("数量",null,null,null,"fcsl",1));
            list.add(new ColumnCellAttr("单价",null,null,null,"fcdj",1));
            list.add(new ColumnCellAttr("金额",null,null,null,"fcje",1));

            list.add(new ColumnCellAttr("数量",null,null,null,"jcsl",1));
            list.add(new ColumnCellAttr("单价",null,null,null,"jcdj",1));
            list.add(new ColumnCellAttr("金额",null,null,null,"jcje",1));
            //初始化表体列编码和列名称
            printReporUtil.printReport(bodyvos,"出入库明细表",list,18,pmap.get("type"),pmap,tmap);
        } catch (DocumentException e) {
            log.error("出入库明细表打印错误", e);
        } catch (IOException e) {
            log.error("出入库明细表打印错误", e);
        }catch (Exception e) {
            log.error("出入库明细表打印失败", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("出入库明细表打印错误", e);
            }
        }
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "出入库明细表:打印期间“" + period + "”存货数据", ISysConstants.SYS_2);
    }

    private QueryParamVO getQueryParamVO( QueryParamVO paramvo){

        if(StringUtil.isEmptyWithTrim(paramvo.getPk_corp())){
            paramvo.setPk_corp(SystemUtil.getLoginCorpId());//设置默认公司PK
        }
        return paramvo;
    }

    private IcDetailVO[] queryVos(QueryParamVO queryParamVO, String iscurr,Map<String, String> pmap) {
        Map<String, List<IcDetailVO>> result = null;
        String currsp = pmap.get("currsp");
        if("N".equals(iscurr)){
            currsp = "all";
        }
        result = gl_rep_crkmxserv.queryMx(queryParamVO,  SystemUtil.getLoginCorpVo());
        if(result == null || result.size() == 0){
            throw new BusinessException("查询数据为空");
        }
        List<IcDetailFzVO> listsps = createRightTree(result,currsp);
        //将查询后的数据分页展示
        List<IcDetailVO> list = getPagedMXZVos(listsps, result, 1, Integer.MAX_VALUE, new ReportDataGrid(), currsp);

        Map<String, YntCpaccountVO> yntCpaccountVOMap =accountService.queryMapByPk(SystemUtil.getLoginCorpId());
        IcDetailVO[] bodyvos = list.stream().toArray(IcDetailVO[]::new);
        for(IcDetailVO icDetailVO : bodyvos){
            if(yntCpaccountVOMap.containsKey(icDetailVO.getSpfl())){
                icDetailVO.setSpfl(yntCpaccountVOMap.get(icDetailVO.getSpfl()).getAccountname());
            }else{
                icDetailVO.setSpfl("");
            }
        }
        return bodyvos;
    }

    private Map<String, List<SuperVO>>  reloadVOs(IcDetailVO[] bodyvos, QueryParamVO paramvo){

        Map<String, List<IcDetailVO>> icMap = gl_rep_crkmxserv.queryMx(paramvo, SystemUtil.getLoginCorpVo()) ;
        if(icMap == null){
            return null;
        }
        List<IcDetailVO> icvlist = null;
        List<SuperVO> flist = null;
        String mxkey = null;
        Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
        for(Map.Entry<String, List<IcDetailVO>> entry : icMap.entrySet()){
            icvlist = entry.getValue();
            if(icvlist!=null && icvlist.size()>0){
                for(IcDetailVO icv:icvlist){
                    mxkey = icv.getSpbm() + " " + icv.getSpmc();
                    icv.setPk_corp(paramvo.getPk_corp());//后续设置精度使用
                    if(mxmap.containsKey(mxkey)){
                        mxmap.get(mxkey).add(icv);//icv.getPk_sp()
                    }else{
                        flist = new ArrayList<SuperVO>();
                        flist.add(icv);
                        mxmap.put(mxkey, flist);//
                    }
                }
            }
        }
        if(mxmap == null || mxmap.isEmpty()){
            return null;
        }
        Map<String,  List<SuperVO>> sortMap = new TreeMap<String,  List<SuperVO>>(new Comparator<String>() {
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        });
        sortMap.putAll(mxmap);

        return sortMap;
    }

    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
        OutputStream toClient = null;
        String qj = "";
        try {
            String strlist = param.get("list");
            if (strlist == null) {
                return;
            }
            strlist = strlist.replace("}{", "},{");
            IcDetailVO[] vo =JsonUtils.deserialize(strlist, IcDetailVO[].class);
            String gs = vo[0].getGs();
            qj = vo[0].getTitlePeriod();
            String current = param.get("curr_export");
            QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
            vo = queryVos(getQueryParamVO(queryParamvo), current,param);
            Excelexport2003<IcDetailVO> lxs = new Excelexport2003<>();
            String pk_corp = SystemUtil.getLoginCorpId();
			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

            CrkMxNewExcelField xsz = new CrkMxNewExcelField(num, price);
            xsz.setFields(xsz.getFields1());
            xsz.setIcDetailVos(vo);
            xsz.setQj(qj);
            xsz.setCreator(SystemUtil.getLoginUserVo().getUser_name());
            xsz.setCorpName(gs);
            response.reset();
            String filename = xsz.getExcelport2003Name();
            String formattedName = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            lxs.exportExcel(xsz, toClient);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response!=null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
        }
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "出入库明细表:导出期间“" + qj + "”存货数据", ISysConstants.SYS_2);
    }

    /**
     * 联查出入库明细信息
     */
    public void linkCrkmx() {
//        Rectangle pageSize = PageSize.A4;
//        float leftsize = 47f;
//        float rightsize = 15f;
//        float topsize = 36f;
//        Document document = new Document(pageSize, leftsize, rightsize, topsize, 4);
//        ByteArrayOutputStream buffer = null;
//        try {
//            String vicbillcode = getRequest().getParameter("vicbillcode");
//            String rq = getRequest().getParameter("rq");
//            Map<String, List<IcDetailVO>> crkmxlist = gl_rep_crkmxserv.queryCrkmxs(new String[]{vicbillcode},null, getLogincorppk(),rq);
//            CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, getLogincorppk());
//            buffer = new ByteArrayOutputStream();
//            PdfWriter writer = PdfWriter.getInstance(document, buffer);
//            document.open();
//            PdfContentByte canvas = writer.getDirectContent();
//            CrkPrintUtil printutil = new CrkPrintUtil();
//            // 赋值首字符的值
//            printutil.batchPrintCrkContent(leftsize, topsize, document, canvas, crkmxlist, cpvo);
//        } catch (Exception e) {
//            log.error("错误",e);
//        } finally {
//            document.close();
//        }
//        ServletOutputStream out = null;
//        try {
//            getResponse().setContentType("application/pdf");
//            getResponse().setCharacterEncoding("utf-8");
//            getResponse().setContentLength(buffer.size());
//            out = getResponse().getOutputStream();
//            buffer.writeTo(out);
//            buffer.flush();// flush 放在finally的时候流关闭失败报错
//            out.flush();
//        } catch (IOException e) {
//
//        } finally {
//            try {
//                if (buffer != null) {
//                    buffer.close();
//                }
//            } catch (IOException e) {
//            }
//            try {
//                if (out != null) {
//                    out.close();
//                }
//            } catch (IOException e) {
//            }
//        }
    }

    private IcDetailVO[] reloadExcelData(QueryParamVO paramvo){
//        Map<String, List<IcDetailVO>> icMap = gl_rep_crkmxserv.queryMx(paramvo, getLoginCorpInfo());
//        if(icMap == null || icMap.isEmpty()){
//            return null;
//        }
//        List<IcDetailVO> list = new ArrayList<IcDetailVO>();
//        List<IcDetailVO> icvlist = null;
//        Map<String, YntCpaccountVO> cpamap = AccountCache.getInstance().getMap("", getLogincorppk());
//
//        for(Map.Entry<String, List<IcDetailVO>> entry : icMap.entrySet()){
//            icvlist = entry.getValue();
//            for(IcDetailVO icv:icvlist){
//                if(icv != null && (!StringUtil.isEmpty(icv.getSpbm())|| !StringUtil.isEmpty(icv.getSpmc()))){
//                    list.add(icv);
//                }
//                if(cpamap!=null && !StringUtil.isEmpty(icv.getSpfl())){
//                    if(cpamap.containsKey(icv.getSpfl())){
//                        icv.setSpfl(cpamap.get(icv.getSpfl()).getAccountname());
//                    }
//                }
//            }
//        }
//
//        return list.toArray(new IcDetailVO[0]);
        return null;
    }
}

package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.report.NumMnyDetailVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.excel.cwzb.NmnyDetailExcelField;
import com.dzf.zxkj.report.service.cwzb.INummnyReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.dzf.zxkj.report.utils.SystemUtil;
import com.dzf.zxkj.report.utils.VoUtils;
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

@RestController
@RequestMapping("gl_rep_sljemxzact")
@Slf4j
public class NmnyMXController extends ReportBaseController {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private INummnyReport gl_rep_nmdtserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @PostMapping("/query")
    public ReturnData query(@RequestBody Map<String, String> param) {
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO paramvo = new QueryParamVO();
        try {
            paramvo = JsonUtils.convertValue(param, QueryParamVO.class);
            String currkmbm = param.get("currkmbm");
            String xsfzhs = param.get("xsfzhs");

            //判断请求是否联查而来，如是，设置科目参数
            if(!StringUtil.isEmptyWithTrim(currkmbm)){

                paramvo.setKms_last("_9999999999999999");//加上限
            }

            //前台传参三个值,前两个值为必输项目，第三个为非必输项目
            if(!paramvo.getBegindate1().after(paramvo.getEnddate())){
                String startDate = (paramvo.getBegindate1()).toString();//举例2015-01-10
                String enddate = (paramvo.getEnddate()).toString();//举例2015-08-10
                String pk_inventory = paramvo.getPk_inventory() ;
                String pk_bz = paramvo.getPk_currency();

                if("undefined".equals(pk_bz)){
                    pk_bz = null;
                }

                if(startDate == null || "".equals(startDate)
                        || enddate == null || "".equals(enddate)){
                    grid.setSuccess(false);
                    grid.setRows(new ArrayList<NumMnyDetailVO>());
                    grid.setMsg("参数为空!");
                }else{
                    //开始日期应该在建账日期前
                    CorpVO currcorp = SystemUtil.getLoginCorpVo();
                    DZFDate begdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(currcorp.getBegindate())) ;
                    if(begdate.after(new DZFDate(startDate))){
                        throw new BusinessException("开始日期不能在建账日期("+DateUtils.getPeriod(begdate)+")前!");
                    }
                    String pk_corp = SystemUtil.getLoginCorpId();
                    String userid  = SystemUtil.getLoginUserId();
                    List<NumMnyDetailVO> kmmxvostemp = gl_rep_nmdtserv.getNumMnyDetailVO(startDate, enddate, pk_inventory,paramvo, pk_corp,userid,pk_bz,xsfzhs,begdate);
                    List<NumMnyDetailVO> kmmxvos = handleFs(kmmxvostemp,paramvo);
                    Map<String,NumMnyDetailVO> map = new TreeMap<String, NumMnyDetailVO>();
//					int count = 1;

                    int len = kmmxvos == null ? 0 : kmmxvos.size();
                    for (int i = 0; i < len; i++) {
                        if(!map.containsKey(kmmxvos.get(i).getKmbm())){
                            map.put(kmmxvos.get(i).getKmbm(), kmmxvos.get(i));
                        }

                    }

                    if(map!=null&&map.size()>0){
                        Map<String,NumMnyDetailVO> treemap = new TreeMap<String, NumMnyDetailVO>();
                        List<String> keys = new ArrayList<String>(map.keySet());
                        Iterator it = map.entrySet().iterator();
                        while (it.hasNext()) {
                            Map.Entry entry =(Map.Entry) it.next();
                            String key = (String) entry.getKey();
                            NumMnyDetailVO value=(NumMnyDetailVO) entry.getValue();
                            for(int i = keys.size()-1;i>-1;i--){
                                String parentkey = keys.get(i);

                                if(parentkey.indexOf("_")>-1){
                                    parentkey = parentkey.substring(0, parentkey.indexOf("_"));
                                }

                                if(key.startsWith(parentkey)&&key.length()>parentkey.length()){
                                    NumMnyDetailVO vo = map.get(parentkey);
                                    if(vo == null){
                                        continue;
                                    }
                                    NumMnyDetailVO[] child = (NumMnyDetailVO[]) vo.getChildren();
                                    List<NumMnyDetailVO> childlist = new ArrayList<NumMnyDetailVO>();
                                    if(child!=null){
                                        List<NumMnyDetailVO> list = Arrays.asList(child);
                                        childlist.addAll(list);
                                    }
                                    childlist.add(value);
                                    vo.setChildren(childlist.toArray(new NumMnyDetailVO[childlist.size()]));
//				        			nvo.add(vo);
//				        			if(!treemap.containsKey(vo.getKmbm())){
//				        				treemap.put(vo.getKmbm(), vo);
//									}
                                    break;
                                }
                            }
                            if(!treemap.containsKey(value.getKmbm())&&value.getKmbm().length()==4){
                                treemap.put(value.getKmbm(), value);
                            }
                        }
                        grid.setNumcombox(new ArrayList<NumMnyDetailVO>(map.values()));
                        grid.setNumMnyDetail(new ArrayList<NumMnyDetailVO>(treemap.values()));
                    }
//				}
                    if(kmmxvos!=null && kmmxvos.size()>0){
                        ReportUtil.updateKFx(kmmxvos.toArray(new NumMnyDetailVO[0]));
                    }
                    grid.setTotal((long) (kmmxvos==null?0:kmmxvos.size()));
                    grid.setRows(kmmxvos);
                    grid.setSuccess(true);
                }
            }else{
                grid.setSuccess(false);
                grid.setRows(new ArrayList<NumMnyDetailVO>());
                grid.setMsg("查询失败:查询开始日期，应该在查询结束日期前!!");
            }
        } catch (Exception e) {
            grid.setRows(new ArrayList<NumMnyDetailVO>());
//            printErrorLog(grid, log, e, "查询失败!");
            grid.setSuccess(false);
            grid.setMsg("查询失败!");
        }
        //日志记录
        String qjq = paramvo.getBegindate1() != null ? paramvo.getBegindate1().getYear() + "-" +paramvo.getBegindate1().getMonth() : "";
        String qjm = paramvo.getEnddate() != null ? paramvo.getEnddate().getYear() + "-" +paramvo.getEnddate().getMonth() : "";
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                new StringBuffer().append("数量金额明细账查询:")
                        .append(qjq).append("-").append(qjm).toString(), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);

    }

    /**
     * 处理无发生无余额不显示，有余额无发生不显示(包含无余额无发生不显示)
     * @param kmmxvos
     */
    private List<NumMnyDetailVO> handleFs(List<NumMnyDetailVO> kmmxvos, QueryParamVO paramvo) {
        List<NumMnyDetailVO> result = new ArrayList<NumMnyDetailVO>();

        Map<String, DZFBoolean> periodvalue = new HashMap<String, DZFBoolean>();
        Map<String,String> qcperiodvalue = new HashMap<String,String>();//期初期间

        if(kmmxvos!=null && kmmxvos.size()>0
                && ((paramvo.getXswyewfs()!=null && paramvo.getXswyewfs().booleanValue())
                || (paramvo.getIshowfs()!=null && !paramvo.getIshowfs().booleanValue()))
        ){
            String period = "";
            String key = "";
            for(NumMnyDetailVO vo:kmmxvos){
                period = vo.getOpdate().substring(0, 7);
                key = vo.getKmbm();
                if(StringUtil.isEmpty(vo.getZy()) || !vo.getZy().equals("本期合计")){
                    continue;
                }

                if(paramvo.getIshowfs()!=null && !paramvo.getIshowfs().booleanValue()){
                    if(VoUtils.getDZFDouble(vo.getNmny()).doubleValue()==0
                            && VoUtils.getDZFDouble(vo.getNdmny()).doubleValue()==0
                    ){
                        periodvalue.put(key+period,  DZFBoolean.TRUE);
                    }else{
                        periodvalue.put(key+period,  DZFBoolean.FALSE);
                        if(!qcperiodvalue.containsKey(key)){
                            qcperiodvalue.put(key, DateUtils.getPeriodStartDate(vo.getOpdate().substring(0,7)).toString() );
                        }
                    }
                }else if(paramvo.getXswyewfs()!=null && paramvo.getXswyewfs().booleanValue()){
                    if(VoUtils.getDZFDouble(vo.getNmny()).doubleValue()==0
                            && VoUtils.getDZFDouble(vo.getNdmny()).doubleValue()==0
                            && VoUtils.getDZFDouble(vo.getNymny()).doubleValue() == 0){
                        periodvalue.put(key+period,  DZFBoolean.TRUE);
                    }else{
                        periodvalue.put(key+period, DZFBoolean.FALSE);
                        if(!qcperiodvalue.containsKey(key)){
                            qcperiodvalue.put(key, DateUtils.getPeriodStartDate(vo.getOpdate().substring(0,7)).toString() );
                        }
                    }
                }
            }

            for(NumMnyDetailVO vo:kmmxvos){
                period = vo.getQj().substring(0, 7);
                key = vo.getKmbm();
                DZFBoolean bxs = periodvalue.get(key+period);
                String qcperiod = qcperiodvalue.get(key);
                if(!StringUtil.isEmpty(qcperiod) && "期初余额".equals(vo.getZy())){
                    vo.setOpdate(qcperiod);
                    result.add(vo);
                }
                if(bxs!=null &&  !bxs.booleanValue() && !"期初余额".equals(vo.getZy())){
                    result.add(vo);
                }
            }
        }else{
            return kmmxvos;
        }

        return result;
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(@RequestParam Map<String, String> pmap1,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
        try {

            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> params = printReporUtil.getPrintMap(printParamVO);
            String strlist = params.get("list");
            String type = params.get("type");
            String left = params.get("left");
            String top = params.get("top");
            String printdate = params.get("printdate");
            String font = params.get("font");
            String pageNum = params.get("pageNum");

            String showjfdj = printParamVO.getShowjf();
            String showdfdj = printParamVO.getShowdf();
            String showyedj = printParamVO.getShowye();
            Map<String,String> pmap = new HashMap<String,String>();//声明一个map用来存前台传来的设置参数
            Map<String,String> tmap = new HashMap<String,String>();//声明一个map用来存标题
            pmap.put("type",type);
            pmap.put("left",left);
            pmap.put("top",top);
            pmap.put("printdate",printdate);
            pmap.put("font",font);
            pmap.put("pageNum",pageNum);
            if(strlist==null){
                return;
            }
//            JSONArray array = (JSONArray) JSON.parseArray(strlist);
//            Map<String,String> bodymapping= FieldMapping.getFieldMapping(new NumMnyDetailVO());
//            NumMnyDetailVO[] bodyvos = DzfTypeUtils.cast(array,bodymapping, NumMnyDetailVO[].class, JSONConvtoJAVA.getParserConfig());
            NumMnyDetailVO[] bodyvos = JsonUtils.deserialize(strlist, NumMnyDetailVO[].class);
            String[] columnames = new String[]{"编码","名称","日期","凭证号","摘要","借方","贷方","方向","余额","数量","单价","金额","数量","单价","金额","期末数量","期末单价","期末金额"};
            String[] columnkeys = new String[]{"kmbm","kmmc","opdate","pzh","zy","nnum","nprice","nmny","ndnum","ndprice","ndmny","dir","nynum","nyprice","nymny"};
            //动态列过滤
            List<String> colmnkeylist = new ArrayList<String>();
            int[] columnints = null;
            if (type.equals("1")){
                columnints = new int[]{2,4,2,1,4,2,2,2,2,2,2,1,2,2,2};
            }else{
                columnints = new int[]{2,4,3,2,4,2,2,2,2,2,2,1,2,2,2};
            }
            List<Integer> columnintlist = new ArrayList<Integer>();
            for(int i =0;i<columnkeys.length;i++){
                if("N".equals(showjfdj) && "nprice".equals(columnkeys[i])){
                    continue;
                }
                if("N".equals(showdfdj) && "ndprice".equals(columnkeys[i])){
                    continue;
                }
                if("N".equals(showyedj) && "nyprice".equals(columnkeys[i])){
                    continue;
                }
                colmnkeylist.add(columnkeys[i]);
                columnintlist.add(columnints[i]);
            }

            printReporUtil.setIscross(DZFBoolean.TRUE);//是否横向
            LinkedList<ColumnCellAttr> columnlist = new LinkedList<>();
            for(int i=0;i<columnames.length;i++){
                ColumnCellAttr attr = new ColumnCellAttr();
                if(i == 10 && "N".equals(showjfdj)){
                    continue;
                }
                if(i == 13 && "N".equals(showdfdj) ){
                    continue;
                }
                if(i == 16 && "N".equals(showyedj)){
                    continue;
                }
                attr.setColumname(columnames[i]);
                if(i==0 ||  i==1 || i==2  ||  i==3 || i==4  ){
                    attr.setRowspan(2);
                }else if(i == 5){
                    if("Y".equals(showjfdj)){
                        attr.setColspan(3);
                    }else{
                        attr.setColspan(2);
                    }
                }else if(i==6){
                    if("Y".equals(showdfdj)){
                        attr.setColspan(3);
                    }else{
                        attr.setColspan(2);
                    }
                }else if(i==7){
                    attr.setRowspan(2);
                }else if(i==8){
                    if("Y".equals(showyedj)){
                        attr.setColspan(3);
                    }else{
                        attr.setColspan(2);
                    }
                }
                columnlist.add(attr);
            }
            Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
            String corp = SystemUtil.getLoginCorpId();
            CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, corp);
//			if(corpvo == null || corpvo.getBbuildic() == null || !corpvo.getBbuildic().booleanValue()){
            for (NumMnyDetailVO mxvo : bodyvos) {
                List<SuperVO> mxlist = null;


                if (!mxmap.containsKey(mxvo.getKmbm())) { // map里的key
                    // 不包含当前数据商品名称
                    mxlist = new ArrayList<SuperVO>(); // 就 创建一个list 吧这条数据
                    mxvo.setPk_corp(corp);      		// 加进去
                    mxlist.add(mxvo);
                } else {
                    mxlist = mxmap.get(mxvo.getKmbm()); // map里的key 包含当前商品名称
                    mxvo.setPk_corp(corp);
                    mxlist.add(mxvo);
                }
                mxmap.put(mxvo.getKmbm(), mxlist);
            }

            mxmap = sortMapByKey(mxmap);
            printReporUtil.setLineheight(22f);
            List<InventoryVO> list = zxkjPlatformService.queryInventoryVOs(corp);
            Map<String,String> invmaps = groupInventoryVO(list);
            if(corpvo.getBbuildic()!=null){
                invmaps.put("iskucun", String.valueOf(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())));
            }else{
                invmaps.put("iskucun", String.valueOf(false));
            }
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            if (type.equals("1")){
                printReporUtil.printGroup(mxmap,new SuperVO[]{}, "数 量 金 额 明 细 账", colmnkeylist.toArray(new String[0]), null, columnlist, converListToint(columnintlist) ,0,invmaps,pmap,tmap);// A4纸张打印
            }
            else if(type.equals("2")){
                printReporUtil.printB5(mxmap, new SuperVO[]{}, "数 量 金 额 明 细 账", colmnkeylist.toArray(new String[0]), null, columnlist, converListToint(columnintlist), 0,invmaps,pmap,tmap);
            }

        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    private int[] converListToint(List<Integer> columnintlist) {
        int[] res = new int[columnintlist.size()];
        for(int i =0;i<columnintlist.size();i++){
            res[i] = columnintlist.get(i);
        }
        return res;
    }

    public Map<String,String> groupInventoryVO(List<InventoryVO> list){
        Map<String,String> map = new HashMap<String,String>();
        if(list == null || list.size() == 0){
            return map;
        }
        for(InventoryVO s :list){
            map.put(s.getPk_inventory(), s.getMeasurename());
        }
        return map;
    }


    //导出excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody PrintParamVO printParamVO,
                            @MultiRequestBody QueryParamVO paramvo,
                            @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){
//        QueryParamVO paramvo = (QueryParamVO) DzfTypeUtils.cast(getRequest(), new QueryParamVO());
        String strlist = printParamVO.getList();
        String showjfdj = printParamVO.getShowjf();
        String showdfdj = printParamVO.getShowdf();
        String showyedj = printParamVO.getShowye();

//        JSONArray array = (JSONArray) JSON.parseArray(strlist);
//        Map<String,String> bodymapping=FieldMapping.getFieldMapping(new NumMnyDetailVO());
//        NumMnyDetailVO[] vo =DzfTypeUtils.cast(array,bodymapping, NumMnyDetailVO[].class, JSONConvtoJAVA.getParserConfig());
        NumMnyDetailVO[] vo = JsonUtils.deserialize(strlist, NumMnyDetailVO[].class);
        String gs =  printParamVO.getCorpName();
        String qj =  printParamVO.getTitleperiod();
        Excelexport2003<NumMnyDetailVO> lxs = new Excelexport2003<NumMnyDetailVO>();

        String pk_corp = corpVO.getPk_corp();
        String numStr = printParamVO.getNumstr();
        String priceStr = printParamVO.getPricestr();
        int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
        int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

        NmnyDetailExcelField xsz = new NmnyDetailExcelField(num, price,showjfdj,showdfdj,showyedj);
        setExprotInfo(xsz);
        xsz.setNumMnyDetailVOs(vo);
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

        String qjq = paramvo.getBegindate1() != null ? paramvo.getBegindate1().getYear() + "-" +paramvo.getBegindate1().getMonth() : "";
        String qjm = paramvo.getEnddate() != null ? paramvo.getEnddate().getYear() + "-" +paramvo.getEnddate().getMonth() : "";
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                new StringBuffer().append("数量金额明细账导出:")
                        .append(qjq).append("-").append(qjm).toString(), ISysConstants.SYS_2);
    }

    private void setExprotInfo(NmnyDetailExcelField xsz){
        xsz.setFields(xsz.getFields1());
    }

    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    public  Map<String,  List<SuperVO>> sortMapByKey(Map<String, List<SuperVO>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String,  List<SuperVO>> sortMap = new TreeMap<String,  List<SuperVO>>(new Comparator<String>() {
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        }
        );
        sortMap.putAll(map);

        return sortMap;
    }
}

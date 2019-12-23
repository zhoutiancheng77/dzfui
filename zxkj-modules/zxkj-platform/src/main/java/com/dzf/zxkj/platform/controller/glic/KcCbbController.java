package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.VOSortUtils;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.excel.KccbNewExcelField;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.jzcl.TempInvtoryVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.IcDetailFzVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.IKcCbb;
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
 * 库存成本表
 *
 */
@RestController
@RequestMapping("/glic/gl_rep_kcccbact")
@Slf4j
public class KcCbbController extends GlicReportController{
    @Autowired
    private IKcCbb ic_rep_cbbserv;
    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IParameterSetService parameterserv;
    @GetMapping("/query")
    public ReturnData<Json> queryAction(@RequestParam Map<String, String> param){
        ReportDataGrid grid = new ReportDataGrid();
        QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
        checkPowerDate(queryParamvo);
        Map<String, IcDetailVO>  result = ic_rep_cbbserv.queryDetail(queryParamvo, SystemUtil.getLoginCorpVo());
        String currsp =  param.get("currsp");
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
                                            Map<String, IcDetailVO> result, int page, int rows,
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

        Map<String, List<IcDetailVO>> spMap = new TreeMap<String, List<IcDetailVO>>();
        IcDetailVO icv = null;
        List<IcDetailVO> flist = null;
        for(Map.Entry<String, IcDetailVO> entry : result.entrySet()){
            icv = entry.getValue();
            if(spMap.containsKey(icv.getPk_sp())){
                spMap.get(icv.getPk_sp()).add(icv);
            }else{
                flist = new ArrayList<IcDetailVO>();
                flist.add(icv);
                spMap.put(icv.getPk_sp(), flist);
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

            if(spList.size() > 1){
                final Map<String, Integer> sortScore = new HashMap<String, Integer>();
                int count = 0;
                IcDetailFzVO[] children;
                for(IcDetailFzVO ic : listsps){
                    sortScore.put(ic.getId(), count++);

                    children = (IcDetailFzVO[]) ic.getChildren();

                    if(children != null && children.length > 0){
                        for(IcDetailFzVO icbvo : children){
                            sortScore.put(icbvo.getId(), count++);
                        }
                    }
                }

                Collections.sort(spList, new Comparator<IcDetailVO>() {

                    @Override
                    public int compare(IcDetailVO o1, IcDetailVO o2) {
                        Integer i1 = sortScore.get(o1.getPk_sp());
                        i1 = i1 == null ? -1 : i1;

                        Integer i2 = sortScore.get(o2.getPk_sp());
                        i2 = i2 == null ? -1 : i2;

                        return i1.compareTo(i2);
                    }
                });
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

            if(spList== null || spList.size() == 0){
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
            grid.setTotal(0L);
        }

        return resList;
    }

    private List<IcDetailFzVO> createRightTree(Map<String, IcDetailVO> result,String currsp){
        IcDetailFzVO fzvo = null;
        Set<String> conkeys = new HashSet<String>();
        IcDetailVO vo = null;
        List<IcDetailFzVO> listsps_temp = new ArrayList<IcDetailFzVO>();

        for(Map.Entry<String, IcDetailVO> entry : result.entrySet()){
            String key = entry.getKey();
            key = key.length() > 49 ? key.substring(0, 49) : key;
            vo = entry.getValue();

            if(!conkeys.contains(key) && !StringUtil.isEmpty(vo.getSpbm())){
                fzvo = new IcDetailFzVO();
                fzvo.setId(vo.getPk_sp());
                fzvo.setSpfl(vo.getSpfl());
                if(StringUtil.isEmpty(vo.getSpgg())){
                    fzvo.setText(vo.getSpbm() + " " + vo.getSpmc());
                }else{
                    fzvo.setText(vo.getSpbm() + " " + vo.getSpmc()+"("+vo.getSpgg()+")");
                }

                fzvo.setSpgg(vo.getSpgg());
                if(key.length() == 24){
                    fzvo.setText(vo.getSpbm() + " " + vo.getSpfl_name());
                }
                fzvo.setCode(vo.getSpbm());
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
        for(IcDetailFzVO zvo:listsps_temp){
            if((!StringUtil.isEmpty(zvo.getId()) && zvo.getId().endsWith("_fl"))
                    || StringUtil.isEmpty(zvo.getSpfl())){
                listsps.add(zvo);
            }
        }
        //该商品下的则是子vo
        String flid = "";
        for(IcDetailFzVO vo_parent:listsps){//父vo
            List<IcDetailFzVO> childvos = new ArrayList<IcDetailFzVO>();
            for(IcDetailFzVO vvo : listsps_temp){

                flid = vvo.getSpfl()+"_fl";
                if(!StringUtil.isEmpty(vvo.getSpfl()) && (flid).equals(vo_parent.getId())
                        && !vvo.getId().equals(vo_parent.getId())){
                    vo_parent.setState("closed");
                    childvos.add(vvo);
                }
            }
            if(childvos!=null && childvos.size()>0){
                vo_parent.setChildren((SuperVO[])childvos.toArray(new IcDetailFzVO[0]));
            }
        }

        return listsps;
    }

    @GetMapping("/showzg")
    public ReturnData<Json> showzg(){
        Grid grid = new Grid();
            CorpVO corpvo = SystemUtil.getLoginCorpVo();
            String userid = SystemUtil.getLoginUserId();
            DZFDate doped = new DZFDate(SystemUtil.getLoginDate());

            List<TempInvtoryVO> cvos = ic_rep_cbbserv.queryZgVOs(corpvo, userid, doped);
            if(cvos != null && cvos.size() > 1){
                Collections.sort(cvos, new Comparator<TempInvtoryVO>() {
                    @Override
                    public int compare(TempInvtoryVO o1, TempInvtoryVO o2) {
                        return VOSortUtils.compareContainsNull(o1.getKmbm(), o2.getKmbm());
                    }
                });
            }
            grid.setRows(cvos== null ? new ArrayList<TempInvtoryVO>() : cvos);
            grid.setTotal(cvos == null ? 0L : (long)cvos.size());
            grid.setSuccess(true);
        return ReturnData.ok().data(grid);
    }

    @GetMapping("/adjustKc")
    public ReturnData adjustKc( @RequestParam("period") String period){
        Json json = new Json();
        if(StringUtil.isEmpty(period))
            throw new BusinessException("期间数据为空,请检查");
        String pk_corp =SystemUtil.getLoginCorpId() ;
        TzpzHVO headvo = ic_rep_cbbserv.queryJzPz(pk_corp, period);
        boolean flag = true;
        if(headvo == null){
            flag = false;
        }
        json.setSuccess(flag);
        json.setHead(headvo);
        json.setMsg(flag ? "查询成功" : period + "没有结转凭证，请检查");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/zgsave")
    public ReturnData zgsave(@RequestBody Map<String, String> map){
        Grid grid = new Grid();
        //处理暂估
        String period = "";
        String zg = map.get("zg");
        if(StringUtil.isEmpty(zg))
            throw new BusinessException("暂估数据不全，请检查");
        zg = zg.replace("}{", "},{");
//        zg = "[" + zg + "]";
        TempInvtoryVO[] bodyvos = JsonUtils.deserialize(zg, TempInvtoryVO[].class);
        period = bodyvos[0].getPeriod();
        ic_rep_cbbserv.saveZg(bodyvos, SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId());
        grid.setSuccess(true);
        grid.setMsg("暂估处理成功");
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:存货暂估期间“" + period + "”存货数据", ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap, HttpServletResponse response) {
        String period = "";
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserVo(), response);
            PrintParamVO printvo = JsonUtils.convertValue(pmap, PrintParamVO.class);//
            if (printvo.getPageOrt().equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);// 是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            }
            period =  pmap.get("titlePeriod");
            String gs = pmap.get("gs");
            QueryParamVO queryParamvo = JsonUtils.convertValue(pmap, QueryParamVO.class);
            IcDetailVO[] bodyvos = queryVos(getQueryParamVO(queryParamvo));
            Map<String, String> tmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", gs);
            tmap.put("期间", period);
//            ColumnCellAttr[] columncellattrvos= JsonUtils.convertValue(printvo.getColumnslist(), ColumnCellAttr[].class);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));
            //初始化表体列编码和列名称
            printReporUtil.setLineheight(22F);
            List<ColumnCellAttr> list = new ArrayList<>();
            String strclassif = pmap.get("classif");
            DZFBoolean flag = new DZFBoolean(strclassif);
//            if(!flag.booleanValue())
//             list.add(new ColumnCellAttr("存货类别",null,null,2,"spfl_name",3));
            list.add(new ColumnCellAttr("存货名称",null,null,2,"spmc",3));
            list.add(new ColumnCellAttr("规格(型号)",null,null,2,"spgg",3));
            list.add(new ColumnCellAttr("计量单位",null,null,2,"jldw",2));
            list.add(new ColumnCellAttr("期初",null,3,null,null,3));
            list.add(new ColumnCellAttr("入库",null,3,null,null,3));
            list.add(new ColumnCellAttr("出库",null,3,null,null,3));
            list.add(new ColumnCellAttr("结存",null,3,null,null,3));
            list.add(new ColumnCellAttr("数量",null,null,null,"qcsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"qcdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"qcje",3));

            list.add(new ColumnCellAttr("数量",null,null,null,"srsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"srdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"srje",3));

            list.add(new ColumnCellAttr("数量",null,null,null,"fcsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"fcdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"fcje",3));

            list.add(new ColumnCellAttr("数量",null,null,null,"jcsl",3));
            list.add(new ColumnCellAttr("单价",null,null,null,"jcdj",3));
            list.add(new ColumnCellAttr("金额",null,null,null,"jcje",3));

            printReporUtil.printReport(bodyvos,"库存成本表",list,15,pmap.get("type"),pmap,tmap);
        } catch (DocumentException e) {
            log.error("库存成本表打印错误", e);
        } catch (IOException e) {
            log.error("库存成本表打印错误", e);
        }catch (Exception e) {
            log.error("库存成本表打印失败", e);
        }finally {
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存成本表打印错误", e);
            }
        }
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:打印期间“" + period + "”存货数据", ISysConstants.SYS_2);
    }

    private QueryParamVO getQueryParamVO( QueryParamVO paramvo){

        if(StringUtil.isEmptyWithTrim(paramvo.getPk_corp())){
            paramvo.setPk_corp(SystemUtil.getLoginCorpId());//设置默认公司PK
        }
        return paramvo;
    }

    private IcDetailVO[] queryVos(QueryParamVO queryParamVO) {
        Map<String, IcDetailVO> result = null;

        queryParamVO.setXmlbid(null);
        queryParamVO.setPk_inventory(null);
        result = ic_rep_cbbserv.queryDetail(queryParamVO, SystemUtil.getLoginCorpVo());

        List<IcDetailFzVO> listsps = createRightTree(result,"all");
        //将查询后的数据分页展示
        List<IcDetailVO> list = getPagedMXZVos(listsps, result, 1, Integer.MAX_VALUE, new ReportDataGrid(), "all");

        return list.stream().toArray(IcDetailVO[]::new);
    }
    /**
     * 导出excel
     */
    @PostMapping("/expExcel")
    public void expExcel(HttpServletResponse response, @RequestParam Map<String, String> param){
        OutputStream toClient = null;
        String qj = "";
        try {
            String strlist = param.get("list");
            if (strlist == null) {
                return;
            }
            String strclassif = param.get("classif");
            qj =  param.get("titlePeriod");
            String gs = param.get("gs");
            QueryParamVO queryParamvo = JsonUtils.convertValue(param, QueryParamVO.class);
            IcDetailVO[] bodyvos = queryVos(getQueryParamVO(queryParamvo));
            Excelexport2003<IcDetailVO> lxs = new Excelexport2003<IcDetailVO>();

            String pk_corp = SystemUtil.getLoginCorpId();
			String numStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF009);
			String priceStr = parameterserv.queryParamterValueByCode(pk_corp, IParameterConstants.DZF010);
			int num = StringUtil.isEmpty(numStr) ? 4 : Integer.parseInt(numStr);
			int price = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);

            KccbNewExcelField xsz = new KccbNewExcelField(num, price);
            DZFBoolean flag = new DZFBoolean(strclassif);

            xsz.setFields(flag.booleanValue() ? xsz.getFields2() : xsz.getFields1());
            xsz.setIcDetailVos(bodyvos);
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
            log.error("库存成本表excel导出错误", e);
        }catch (Exception e) {
            log.error("库存成本表excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("库存成本表excel导出错误", e);
            }
            try {
                if (response!=null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("库存成本表excel导出错误", e);
            }
        }
//        //日志记录接口
//        writeLogRecord(LogRecordEnum.OPE_KJ_CHGL.getValue(),
//                "库存成本表:导出期间“" + qj + "”存货数据", ISysConstants.SYS_2);
    }

    @GetMapping("/getClassifyFlag")
    public ReturnData getClassifyFlag(){
        Json json = new Json();
        String pk_corp = SystemUtil.getLoginCorpId();
        InventorySetVO vo = gl_ic_invtorysetserv.query(pk_corp);

        boolean flag = true;
        if(vo != null && vo.getChcbjzfs() == 1){
            flag = false;
        }
        json.setSuccess(true);
        json.setHead(flag);
        json.setMsg("查询成功");
        return ReturnData.ok().data(json);
    }
}

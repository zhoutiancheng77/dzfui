package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.ArrayUtil;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.report.KmConFzVoTreeStrategy;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.report.KmmxConFzMxVO;
import com.dzf.zxkj.platform.model.report.ReportDataGrid;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.KmmxExcelField;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.*;

@RestController
@RequestMapping("gl_rep_kmmxjact")
@Slf4j
public class KmMxrController extends ReportBaseController {

    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;


    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData queryAction(@MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO) {
        ReportDataGrid grid = new ReportDataGrid();
        KmReoprtQueryParamVO queryParamvo = (KmReoprtQueryParamVO)getQueryParamVO(queryparamvo,corpVO);
        try {
            int page = queryParamvo == null ?1: queryParamvo.getPage();
            if(queryParamvo.getBswitch()!=null && queryParamvo.getBswitch().booleanValue()){
                page = 1;
            }
            int rows =queryParamvo ==null? 100000: queryParamvo.getRows();
            KmMxZVO[] vos = null;
            //验证 查询范围应该在当前登录人的权限范围内
            checkPowerDate(queryParamvo,corpVO);
            queryParamvo.setIsnomonthfs(DZFBoolean.FALSE);
            queryParamvo.setBtotalyear(DZFBoolean.TRUE);//是否显示本年累计
            KmMxZVO[] kmmxvos = gl_rep_kmmxjserv.getKMMXZConFzVOs(queryParamvo,null);
            new ReportUtil().updateKFx(kmmxvos);
            //过滤期初数据
            vos = filterQcVos(kmmxvos,queryParamvo.getPk_corp(),zxkjPlatformService);
            List<KmmxConFzMxVO> listkms = createRighTree(vos,queryParamvo.getPk_corp(),queryParamvo);
            vos = getPagedMXZVOs(vos,page,rows,grid,queryParamvo.getCurrkmbm());
            grid.setRighttree(listkms);
            grid.setRows(Arrays.asList(vos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<KmMxZVO>());
            printErrorLog(grid, e, "查询失败！");
        }
//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "科目明细账查询:"+queryParamvo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    private List<KmmxConFzMxVO> createRighTree(KmMxZVO[] kmmxvos,String pk_corp,KmReoprtQueryParamVO paramgvo){
        List<KmmxConFzMxVO> listkms = new ArrayList<KmmxConFzMxVO>();
        Map<String,List<KmmxConFzMxVO>> fzlistmapkms = new HashMap<String,List<KmmxConFzMxVO>>();
        Set<String> conkeys = new HashSet<String>();
        Set<String> fzconkeys = new HashSet<String>();
        KmmxConFzMxVO tempfzvo = null;
        List<String> checklist = new ArrayList<String>();
        if(!StringUtil.isEmpty(paramgvo.getCurrkmbm())){
            String[] values = paramgvo.getCurrkmbm().split(",");
            checklist = Arrays.asList(values);
        }
        for(KmMxZVO mxzvo:kmmxvos){
            //如果不存在，拼tree数据
            if(mxzvo.getPk_accsubj().length() ==24 && !conkeys.contains(mxzvo.getPk_accsubj())){//只是负责科目的计算
                tempfzvo = new KmmxConFzMxVO();
                tempfzvo.setId(mxzvo.getPk_accsubj());
                tempfzvo.setText(mxzvo.getKmbm()+"    "+mxzvo.getKm());
                tempfzvo.setCode(mxzvo.getKmbm());
                if(checklist.contains(tempfzvo.getId()) || (!StringUtil.isEmpty(paramgvo.getCurrkmbm())
                        && paramgvo.getCurrkmbm().startsWith("all"))){//全选
                    tempfzvo.setBchecked("true");
                    tempfzvo.setChecked("true");
                    if(StringUtil.isEmpty(paramgvo.getCurrkmbm())){//科目为空，自动带出默认值
                        tempfzvo.setBdefault(DZFBoolean.TRUE);
                    }
                }
                listkms.add(tempfzvo);
                conkeys.add(mxzvo.getPk_accsubj());
            }

            if(mxzvo.getPk_accsubj().length()>24 && !fzconkeys.contains(mxzvo.getPk_accsubj())){
                tempfzvo = new KmmxConFzMxVO();
                tempfzvo.setId(mxzvo.getPk_accsubj());
                tempfzvo.setText(mxzvo.getKmbm()+"    "+mxzvo.getKm());
                tempfzvo.setCode(mxzvo.getKmbm());
                if(checklist.contains(tempfzvo.getId()) || (!StringUtil.isEmpty(paramgvo.getCurrkmbm())
                        && paramgvo.getCurrkmbm().startsWith("all"))){//全选
                    tempfzvo.setBchecked("true");
                    tempfzvo.setChecked("true");
                }
                if(fzlistmapkms.containsKey(mxzvo.getPk_accsubj().split("_")[0])){
                    fzlistmapkms.get(mxzvo.getPk_accsubj().split("_")[0]).add(tempfzvo);
                }else{
                    List<KmmxConFzMxVO> fzlist = new ArrayList<KmmxConFzMxVO>();
                    fzlist.add(tempfzvo);
                    fzlistmapkms.put(mxzvo.getPk_accsubj().split("_")[0], fzlist);
                }
                fzconkeys.add(mxzvo.getPk_accsubj());
            }
        }

        KmmxConFzMxVO[] cpavos = listkms.toArray(new KmmxConFzMxVO[0]);
        KmmxConFzMxVO vo = (KmmxConFzMxVO) BDTreeCreator.createTree(cpavos, new KmConFzVoTreeStrategy(zxkjPlatformService.queryAccountRule(pk_corp)));
        KmmxConFzMxVO[] bodyvos = (KmmxConFzMxVO[]) DZfcommonTools.convertToSuperVO(vo.getChildren());

        //重新转换集合
        listkms.clear();
        if(bodyvos!=null && bodyvos.length>0){
            //最上面添加一条
            KmmxConFzMxVO allmxvo = new KmmxConFzMxVO();
            allmxvo.setCode("all");
            allmxvo.setId("all");
            allmxvo.setText("全选");
            if(checklist.contains(allmxvo.getCode())){
                allmxvo.setBchecked("true");
                allmxvo.setChecked("true");
            }
            listkms.add(allmxvo);
            //辅助项匹配
            upKmBdVos(bodyvos,fzlistmapkms);
            for(int i=0;i<bodyvos.length;i++){
                listkms.add(bodyvos[i]);
            }
        }

        return listkms;
    }

    @SuppressWarnings("unchecked")
    private void upKmBdVos(KmmxConFzMxVO[] bodyvos,Map<String,List<KmmxConFzMxVO>> fzlistkms) {
        int childlen = bodyvos.length;
        SuperVO[] svos = null;
        for(int i=0;i<childlen;i++){
            String pkkm = bodyvos[i].getId();
            if(fzlistkms.get(pkkm)!=null){
                svos = bodyvos[i].getChildren();
                if(svos == null){
                    svos = new SuperVO[]{};
                };
                bodyvos[i].setChildren(ArrayUtil.mergeArray(svos, fzlistkms.get(pkkm).toArray(new KmmxConFzMxVO[0])));
            }
            if(bodyvos[i].getChildren()!=null && bodyvos[i].getChildren().length>0){
                bodyvos[i].setState("closed");
                upKmBdVos((KmmxConFzMxVO[])bodyvos[i].getChildren(),fzlistkms);
            }
        }
    }

    /**
     * 过滤期初数据
     * @param kmmxvos
     * @return
     */
    public static KmMxZVO[] filterQcVos(KmMxZVO[] kmmxvos,String pk_corp,IZxkjPlatformService zxkjPlatformService) {
        Integer hljd =  new ReportUtil(zxkjPlatformService).getHlJd(pk_corp);
        KmMxZVO[] vos;
        //存在一次的不显示期初余额
        HashMap<String, DZFBoolean> mapshow = new HashMap<String, DZFBoolean>();
        for(KmMxZVO mxzvo:kmmxvos){
            mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.FALSE);
        }
        List<KmMxZVO> listmx = new ArrayList<KmMxZVO>();
        for(KmMxZVO mxzvo:kmmxvos){
            if(mapshow.get(mxzvo.getPk_accsubj()).booleanValue()  && "期初余额".equals(mxzvo.getZy()) && ReportUtil.bSysZy(mxzvo)){
                continue;
            }
            listmx.add(mxzvo);
            if(!mapshow.get(mxzvo.getPk_accsubj()).booleanValue()  && "期初余额".equals(mxzvo.getZy())  && ReportUtil.bSysZy(mxzvo) ){
                mapshow.put(mxzvo.getPk_accsubj(),  DZFBoolean.TRUE) ;
            }

            if(!StringUtil.isEmpty(mxzvo.getBz()) && !StringUtil.isEmpty(mxzvo.getHl())){
                mxzvo.setBz(mxzvo.getBz()+"/"+new DZFDouble(mxzvo.getHl()).setScale(hljd, DZFDouble.ROUND_HALF_UP).toString());
            }
        }
        vos = listmx.toArray(new KmMxZVO[0]);
        return vos;
    }

    /**
     * 将查询后的结果分页
     * @param kmmxvos
     * @param page
     * @param rows
     * @return
     */
    public KmMxZVO[] getPagedMXZVOs(KmMxZVO[] kmmxvos,int page,int rows,ReportDataGrid grid,String currkm) throws DZFWarpException {
        if(kmmxvos == null || kmmxvos.length ==0){
            grid.setTotal((long)0);
            return kmmxvos;
        }

        //如果当前科目编码为空，则取第一个科目，
        if(StringUtil.isEmpty(currkm)){
            currkm = kmmxvos[0].getPk_accsubj();
        }

        List<KmMxZVO> listresmxvo = new ArrayList<KmMxZVO>();//需要返回的结果集

        KmMxZVO[] kmlist = getCurrKm(kmmxvos, currkm);

        //如果结果集是空，则默认选结果集中第一个科目
        if(kmlist.length == 0 ){
            kmlist = getCurrKm(kmmxvos,kmmxvos[0].getPk_accsubj());
        }

        //分页
        if(kmlist!=null && kmlist.length>0){
            int start= (page-1)*rows;
            for(int i=start;i<page*rows && i<kmlist.length;i++){
                listresmxvo.add(kmlist[i]);
            }
            grid.setTotal((long)kmlist.length);
            kmmxvos = listresmxvo.toArray(new KmMxZVO[0]);
        }else{
            kmmxvos = new KmMxZVO[0];
            grid.setTotal((long)0);
        }

        return kmmxvos;
    }

    private KmMxZVO[] getCurrKm(KmMxZVO[] kmmxvos, String currkm) {
        if(!StringUtil.isEmpty(currkm) && currkm.startsWith("all")){
            return kmmxvos;
        }

        if(StringUtil.isEmpty(currkm)){
            return kmmxvos;
        }

        Map<String,List<KmMxZVO>> kmmap = new HashMap<String,List<KmMxZVO>>();

        //vo变成map
        for(KmMxZVO zvo:kmmxvos){
            if(kmmap.containsKey(zvo.getPk_accsubj())){
                kmmap.get(zvo.getPk_accsubj()).add(zvo);
            }else{
                List<KmMxZVO> templist = new ArrayList<KmMxZVO>();
                templist.add(zvo);
                kmmap.put(zvo.getPk_accsubj(), templist);
            }
        }

        List<KmMxZVO> kmlist =new ArrayList<>();
        String[] currkms = currkm.split(",");
        List<KmMxZVO> tlist = null;
        for(String str:currkms){
            tlist =  kmmap.get(str);
            if(tlist!=null && tlist.size()>0){
                for(KmMxZVO mxzvo:tlist){
                    kmlist.add(mxzvo);
                }
            }
        }

        return kmlist.toArray(new KmMxZVO[0]);
    }

    @SuppressWarnings("rawtypes")
    public void kmValue(SuperVO kmvo,Map<String, List<SuperVO>> mxmap, int kmbmLength){
        if(kmbmLength>4){
            String kmkey=((String)kmvo.getAttributeValue("kmbm")).substring(0, kmbmLength-2);
            if(mxmap.get(kmkey)!=null){
                String kmmc= (String) mxmap.get(kmkey).get(0).getAttributeValue("km")+"/"+kmvo.getAttributeValue("km");
                kmvo.setAttributeValue("km",kmmc);
            }
//			kmbmLength=kmbmLength-2;
        }
    }

    public KmMxZVO[] reloadNewValue(KmMxZVO[] bodyvos,KmReoprtQueryParamVO queryParamvo){
        if(bodyvos == null || bodyvos.length == 0)
            return null;
        //重新赋以下3个值
        String titlePeriod = bodyvos[0].getTitlePeriod();
        String gs = bodyvos[0].getGs();
        String isPaging = bodyvos[0].getIsPaging();
        queryParamvo.setBtotalyear(DZFBoolean.TRUE);//是否显示本年累计
        bodyvos = gl_rep_kmmxjserv.getKMMXZConFzVOs(queryParamvo,null);//KmmxReportCache.getInstance().get(userid);
        bodyvos = filterQcVos(bodyvos,queryParamvo.getPk_corp(),zxkjPlatformService);
        bodyvos = getCurrKm(bodyvos, queryParamvo.getCurrkmbm());
        bodyvos[0].setIsPaging(isPaging);
        bodyvos[0].setGs(gs);
        bodyvos[0].setTitlePeriod(titlePeriod);
        return bodyvos;
    }

    /**
     * 使用 Map按key进行排序
     * @param map
     * @return
     */
    @SuppressWarnings("rawtypes")
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


    @PostMapping("export/excel")
    public void excelReport(ReportExcelExportVO excelExportVO, KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response){

        KmMxZVO[] listVo = JsonUtils.deserialize(excelExportVO.getList(),KmMxZVO[].class);

        CorpVO qrycorpvo = zxkjPlatformService.queryCorpByPk(queryparamvo.getPk_corp());
        String gs= CodeUtils1.deCode(qrycorpvo.getUnitname());
        String qj=  listVo[0].getTitlePeriod();
        String pk_currency = queryparamvo.getPk_currency();
        String userid= userVO.getCuserid();
        queryparamvo.setBtotalyear(DZFBoolean.TRUE);//是否显示本年累计
        KmMxZVO[] kmmxvos = gl_rep_kmmxjserv.getKMMXZConFzVOs(queryparamvo,null);
        kmmxvos = filterQcVos(kmmxvos,queryparamvo.getPk_corp(),zxkjPlatformService);
        kmmxvos = getCurrKm(kmmxvos, queryparamvo.getCurrkmbm());
        ReportUtil.updateKFx(kmmxvos);
        listVo =kmmxvos ;
        String currencyname = new ReportUtil().getCurrencyDw(queryparamvo.getCurrency());
        String[] periods = new String[]{qj};
        String[] allsheetname = new String[]{"科目明细账"};

        KmmxExcelField field = new KmmxExcelField("科目明细账", queryparamvo.getPk_currency(), currencyname, periods, allsheetname, qj,
                CodeUtils1.deCode(qrycorpvo.getUnitname()));

        Excelexport2003<KmMxZVO> lxs = new Excelexport2003<KmMxZVO>();
        baseExcelExport(response,lxs,field);

//        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT.getValue(),
//                "科目明细账导出:"+queryParamvo.getBegindate1().toString().substring(0, 7)
//                        +"-"+ queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
    }




}

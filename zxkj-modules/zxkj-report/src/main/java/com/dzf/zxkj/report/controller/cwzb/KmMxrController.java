package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.ArrayUtil;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.pdf.ReportCoverPrintUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
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
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.List;

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
        KmReoprtQueryParamVO queryParamvo = (KmReoprtQueryParamVO) getQueryParamVO(queryparamvo, corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryParamvo.getPk_corp()}, null);
            int page = queryParamvo == null ? 1 : queryParamvo.getPage();
            if (queryParamvo.getBswitch() != null && queryParamvo.getBswitch().booleanValue()) {
                page = 1;
            }
            int rows = queryParamvo == null ? 100000 : queryParamvo.getRows();
            KmMxZVO[] vos = null;
            //验证 查询范围应该在当前登录人的权限范围内
            corpVO = zxkjPlatformService.queryCorpByPk(queryParamvo.getPk_corp());
            checkPowerDate(queryParamvo, corpVO);
            queryParamvo.setIsnomonthfs(DZFBoolean.FALSE);
            queryParamvo.setBtotalyear(DZFBoolean.TRUE);//是否显示本年累计
            KmMxZVO[] kmmxvos = gl_rep_kmmxjserv.getKMMXZConFzVOs(queryParamvo, null);
            new ReportUtil().updateKFx(kmmxvos);
            //过滤期初数据
            vos = filterQcVos(kmmxvos, queryParamvo.getPk_corp(), zxkjPlatformService);
            List<KmmxConFzMxVO> listkms = createRighTree(vos, queryParamvo.getPk_corp(), queryParamvo);
            vos = getPagedMXZVOs(vos, page, rows, grid, queryParamvo.getCurrkmbm());
            grid.setRighttree(listkms);
            grid.setRows(Arrays.asList(vos));
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<KmMxZVO>());
            printErrorLog(grid, e, "查询失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "科目明细账查询:" + queryParamvo.getBegindate1().toString().substring(0, 7)
                        + "-" + queryParamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    private List<KmmxConFzMxVO> createRighTree(KmMxZVO[] kmmxvos, String pk_corp, KmReoprtQueryParamVO paramgvo) {
        List<KmmxConFzMxVO> listkms = new ArrayList<KmmxConFzMxVO>();
        Map<String, List<KmmxConFzMxVO>> fzlistmapkms = new HashMap<String, List<KmmxConFzMxVO>>();
        Set<String> conkeys = new HashSet<String>();
        Set<String> fzconkeys = new HashSet<String>();
        KmmxConFzMxVO tempfzvo = null;
        List<String> checklist = new ArrayList<String>();
        if (!StringUtil.isEmpty(paramgvo.getCurrkmbm())) {
            String[] values = paramgvo.getCurrkmbm().split(",");
            checklist = Arrays.asList(values);
        }
        for (KmMxZVO mxzvo : kmmxvos) {
            //如果不存在，拼tree数据
            if (mxzvo.getPk_accsubj().length() == 24 && !conkeys.contains(mxzvo.getPk_accsubj())) {//只是负责科目的计算
                tempfzvo = new KmmxConFzMxVO();
                tempfzvo.setId(mxzvo.getPk_accsubj());
                tempfzvo.setText(mxzvo.getKmbm() + "    " + mxzvo.getKm());
                tempfzvo.setCode(mxzvo.getKmbm());
                if (checklist.contains(tempfzvo.getId()) || (!StringUtil.isEmpty(paramgvo.getCurrkmbm())
                        && paramgvo.getCurrkmbm().startsWith("all"))) {//全选
                    tempfzvo.setBchecked("true");
                    tempfzvo.setChecked("true");
                    if (StringUtil.isEmpty(paramgvo.getCurrkmbm())) {//科目为空，自动带出默认值
                        tempfzvo.setBdefault(DZFBoolean.TRUE);
                    }
                }
                listkms.add(tempfzvo);
                conkeys.add(mxzvo.getPk_accsubj());
            }

            if (mxzvo.getPk_accsubj().length() > 24 && !fzconkeys.contains(mxzvo.getPk_accsubj())) {
                tempfzvo = new KmmxConFzMxVO();
                tempfzvo.setId(mxzvo.getPk_accsubj());
                tempfzvo.setText(mxzvo.getKmbm() + "    " + mxzvo.getKm());
                tempfzvo.setCode(mxzvo.getKmbm());
                if (checklist.contains(tempfzvo.getId()) || (!StringUtil.isEmpty(paramgvo.getCurrkmbm())
                        && paramgvo.getCurrkmbm().startsWith("all"))) {//全选
                    tempfzvo.setBchecked("true");
                    tempfzvo.setChecked("true");
                }
                if (fzlistmapkms.containsKey(mxzvo.getPk_accsubj().split("_")[0])) {
                    fzlistmapkms.get(mxzvo.getPk_accsubj().split("_")[0]).add(tempfzvo);
                } else {
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
        if (bodyvos != null && bodyvos.length > 0) {
            //最上面添加一条
            KmmxConFzMxVO allmxvo = new KmmxConFzMxVO();
            allmxvo.setCode("all");
            allmxvo.setId("all");
            allmxvo.setText("全选");
            if (checklist.contains(allmxvo.getCode())) {
                allmxvo.setBchecked("true");
                allmxvo.setChecked("true");
            }
            listkms.add(allmxvo);
            //辅助项匹配
            upKmBdVos(bodyvos, fzlistmapkms);
            for (int i = 0; i < bodyvos.length; i++) {
                listkms.add(bodyvos[i]);
            }
        }

        return listkms;
    }

    @SuppressWarnings("unchecked")
    private void upKmBdVos(KmmxConFzMxVO[] bodyvos, Map<String, List<KmmxConFzMxVO>> fzlistkms) {
        int childlen = bodyvos.length;
        SuperVO[] svos = null;
        for (int i = 0; i < childlen; i++) {
            String pkkm = bodyvos[i].getId();
            if (fzlistkms.get(pkkm) != null) {
                svos = bodyvos[i].getChildren();
                if (svos == null) {
                    svos = new SuperVO[]{};
                }
                ;
                bodyvos[i].setChildren(ArrayUtil.mergeArray(svos, fzlistkms.get(pkkm).toArray(new KmmxConFzMxVO[0])));
            }
            if (bodyvos[i].getChildren() != null && bodyvos[i].getChildren().length > 0) {
                bodyvos[i].setState("closed");
                upKmBdVos((KmmxConFzMxVO[]) bodyvos[i].getChildren(), fzlistkms);
            }
        }
    }

    /**
     * 过滤期初数据
     *
     * @param kmmxvos
     * @return
     */
    public static KmMxZVO[] filterQcVos(KmMxZVO[] kmmxvos, String pk_corp, IZxkjPlatformService zxkjPlatformService) {
        Integer hljd = new ReportUtil(zxkjPlatformService).getHlJd(pk_corp);
        KmMxZVO[] vos;
        //存在一次的不显示期初余额
        HashMap<String, DZFBoolean> mapshow = new HashMap<String, DZFBoolean>();
        for (KmMxZVO mxzvo : kmmxvos) {
            mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.FALSE);
        }
        List<KmMxZVO> listmx = new ArrayList<KmMxZVO>();
        for (KmMxZVO mxzvo : kmmxvos) {
            if (mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy()) && ReportUtil.bSysZy(mxzvo)) {
                continue;
            }
            listmx.add(mxzvo);
            if (!mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy()) && ReportUtil.bSysZy(mxzvo)) {
                mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.TRUE);
            }

            if (!StringUtil.isEmpty(mxzvo.getBz()) && !StringUtil.isEmpty(mxzvo.getHl())) {
                mxzvo.setBz(mxzvo.getBz() + "/" + new DZFDouble(mxzvo.getHl()).setScale(hljd, DZFDouble.ROUND_HALF_UP).toString());
            }
        }
        vos = listmx.toArray(new KmMxZVO[0]);
        return vos;
    }

    /**
     * 将查询后的结果分页
     *
     * @param kmmxvos
     * @param page
     * @param rows
     * @return
     */
    public KmMxZVO[] getPagedMXZVOs(KmMxZVO[] kmmxvos, int page, int rows, ReportDataGrid grid, String currkm) throws DZFWarpException {
        if (kmmxvos == null || kmmxvos.length == 0) {
            grid.setTotal((long) 0);
            return kmmxvos;
        }

        //如果当前科目编码为空，则取第一个科目，
        if (StringUtil.isEmpty(currkm)) {
            currkm = kmmxvos[0].getPk_accsubj();
        }

        List<KmMxZVO> listresmxvo = new ArrayList<KmMxZVO>();//需要返回的结果集

        KmMxZVO[] kmlist = getCurrKm(kmmxvos, currkm);

        //如果结果集是空，则默认选结果集中第一个科目
        if (kmlist.length == 0) {
            kmlist = getCurrKm(kmmxvos, kmmxvos[0].getPk_accsubj());
        }

        //分页
        if (kmlist != null && kmlist.length > 0) {
            int start = (page - 1) * rows;
            for (int i = start; i < page * rows && i < kmlist.length; i++) {
                listresmxvo.add(kmlist[i]);
            }
            grid.setTotal((long) kmlist.length);
            kmmxvos = listresmxvo.toArray(new KmMxZVO[0]);
        } else {
            kmmxvos = new KmMxZVO[0];
            grid.setTotal((long) 0);
        }

        return kmmxvos;
    }

    private KmMxZVO[] getCurrKm(KmMxZVO[] kmmxvos, String currkm) {
        if (!StringUtil.isEmpty(currkm) && currkm.startsWith("all")) {
            return kmmxvos;
        }

        if (StringUtil.isEmpty(currkm)) {
            return kmmxvos;
        }

//        Map<String, List<KmMxZVO>> kmmap = new HashMap<String, List<KmMxZVO>>();

        List<KmMxZVO> kmlist = new ArrayList<>();
        String[] currkms = currkm.split(",");
        List<String> currkmslist = Arrays.asList(currkms);
        for (KmMxZVO zvo : kmmxvos) {
            if (!StringUtil.isEmpty(zvo.getPk_accsubj())
              && currkmslist.contains(zvo.getPk_accsubj())) {
                kmlist.add(zvo);
            }
//            if (kmmap.containsKey(zvo.getPk_accsubj())) {
//                kmmap.get(zvo.getPk_accsubj()).add(zvo);
//            } else {
//                List<KmMxZVO> templist = new ArrayList<KmMxZVO>();
//                templist.add(zvo);
//                kmmap.put(zvo.getPk_accsubj(), templist);
//            }
        }
//        List<KmMxZVO> tlist = null;
//        for (String str : currkms) {
//            tlist = kmmap.get(str);
//            if (tlist != null && tlist.size() > 0) {
//                for (KmMxZVO mxzvo : tlist) {
//                    kmlist.add(mxzvo);
//                }
//            }
//        }

        return kmlist.toArray(new KmMxZVO[0]);
    }

    @SuppressWarnings("rawtypes")
    public void kmValue(SuperVO kmvo, Map<String, List<SuperVO>> mxmap, int kmbmLength) {
        if (kmbmLength > 4) {
            String kmkey = ((String) kmvo.getAttributeValue("kmbm")).substring(0, kmbmLength - 2);
            if (mxmap.get(kmkey) != null) {
                String kmmc = (String) mxmap.get(kmkey).get(0).getAttributeValue("km") + "/" + kmvo.getAttributeValue("km");
                kmvo.setAttributeValue("km", kmmc);
            }
//			kmbmLength=kmbmLength-2;
        }
    }

    public KmMxZVO[] reloadNewValue(String titlePeriod, String gs, String isPaging, KmReoprtQueryParamVO queryParamvo,IKMMXZReport gl_rep_kmmxjserv,
                                    IZxkjPlatformService zxkjPlatformService) {
        //重新赋以下3个值
        queryParamvo.setBtotalyear(DZFBoolean.TRUE);//是否显示本年累计
        KmMxZVO[] bodyvos = gl_rep_kmmxjserv.getKMMXZConFzVOs(queryParamvo, null);//KmmxReportCache.getInstance().get(userid);
        bodyvos = filterQcVos(bodyvos, queryParamvo.getPk_corp(), zxkjPlatformService);
        bodyvos = getCurrKm(bodyvos, queryParamvo.getCurrkmbm());
        bodyvos[0].setIsPaging(isPaging);
        bodyvos[0].setGs(gs);
        bodyvos[0].setTitlePeriod(titlePeriod);
        return bodyvos;
    }

    /**
     * 使用 Map按key进行排序
     *
     * @param map
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Map<String, List<SuperVO>> sortMapByKey(Map<String, List<SuperVO>> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        Map<String, List<SuperVO>> sortMap = new TreeMap<String, List<SuperVO>>(new Comparator<String>() {
            public int compare(String str1, String str2) {
                return str1.compareTo(str2);
            }
        }
        );
        sortMap.putAll(map);

        return sortMap;
    }


    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {

        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()}, null);
        String gs = excelExportVO.getCorpName();
        String qj = excelExportVO.getTitleperiod();
        String pk_currency = queryparamvo.getPk_currency();
        String userid = userVO.getCuserid();
        queryparamvo.setBtotalyear(DZFBoolean.TRUE);//是否显示本年累计
        KmMxZVO[] kmmxvos = gl_rep_kmmxjserv.getKMMXZConFzVOs(queryparamvo, null);
        kmmxvos = filterQcVos(kmmxvos, queryparamvo.getPk_corp(), zxkjPlatformService);
        kmmxvos = getCurrKm(kmmxvos, queryparamvo.getCurrkmbm());
        ReportUtil.updateKFx(kmmxvos);
        KmMxZVO[] listVo = kmmxvos;
        String currencyname = new ReportUtil().getCurrencyDw(queryparamvo.getCurrency());
        String[] periods = new String[]{qj};
        String[] allsheetname = new String[]{"科目明细账"};

        KmmxExcelField field = new KmmxExcelField("科目明细账", queryparamvo.getPk_currency(), currencyname, periods, allsheetname, qj,
                gs);
        List<KmMxZVO[]> results = new ArrayList<KmMxZVO[]>();
        results.add(kmmxvos);
        field.setAllsheetzcvos(results);

        Excelexport2003<KmMxZVO> lxs = new Excelexport2003<KmMxZVO>();
        baseExcelExport(response, lxs, field);

        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "科目明细账导出:" + queryparamvo.getBegindate1().toString().substring(0, 7)
                        + "-" + queryparamvo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
    }

    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
            KmReoprtQueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), KmReoprtQueryParamVO.class);

            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()}, null);

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            printReporUtil.setIscross(new DZFBoolean(pmap.get("pageOrt")));
            KmMxZVO[] bodyvos = reloadNewValue(printParamVO.getTitleperiod(), printParamVO.getCorpName(), printParamVO.getIsPaging(),
                    queryparamvo,gl_rep_kmmxjserv,zxkjPlatformService);
            ReportUtil.updateKFx(bodyvos);
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数
            String km = bodyvos[0].getKm();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            printReporUtil.setLineheight(StringUtil.isEmpty(lineHeight) ? 22f : Float.parseFloat(lineHeight));
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            Object[] obj = null;
            // 打印设置(列设置)
            printAction(printParamVO, queryparamvo, printReporUtil, pmap, bodyvos, tmap,zxkjPlatformService);
            writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                    "科目明细账打印:" + printParamVO.getTitleperiod(), ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
    }

    public void printAction(PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo,
                            PrintReporUtil printReporUtil, Map<String, String> pmap,
                            KmMxZVO[] bodyvos, Map<String, String> tmap, IZxkjPlatformService zxkjPlatformService)
            throws DocumentException, IOException {
        Object[] obj;
        if (printParamVO.getIsPaging() != null && printParamVO.getIsPaging().equals("Y")) {  //需要分页打印
            Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
            for (KmMxZVO mxvo : bodyvos) {
                List<SuperVO> mxlist = null;
                mxvo.setGs(bodyvos[0].getGs());
                mxvo.setTitlePeriod(bodyvos[0].getTitlePeriod());
                if (!mxmap.containsKey(mxvo.getKmbm())) {  //map里的key 不包含当前数据科目编码
                    mxlist = new ArrayList<SuperVO>();     // 就 创建一个list  把这条数据 加进去
                    mxlist.add(mxvo);
                } else {
                    mxlist = mxmap.get(mxvo.getKmbm()); //map里的key 包含当前数据科目编码
                    mxlist.add(mxvo);
                }
                mxmap.put(mxvo.getKmbm(), mxlist);       // key=kmbn   value=list
            }
            //排序--根据key排序
            mxmap = sortMapByKey(mxmap);
            Map<String, YntCpaccountVO> cpamap = zxkjPlatformService.queryMapByPk(queryparamvo.getPk_corp());
            String kmfullname = "";
            for (Map.Entry<String, List<SuperVO>> kmEntry : mxmap.entrySet()) {
                List<SuperVO> kmList = kmEntry.getValue();// 得到当前科目 所对应的 数据
                SuperVO kmvo = kmEntry.getValue().get(0);
                String id = (String) kmList.get(0).getAttributeValue("pk_accsubj");
                if (cpamap.containsKey(id)) {
                    kmList.get(0).setAttributeValue("km", cpamap.get(id).getAccountname());
                }
                if (kmvo.getAttributeValue("pk_accsubj") != null
                        && ((String) kmvo.getAttributeValue("pk_accsubj")).length() > 24) {//默认有辅助项目
                    kmfullname = kmvo.getAttributeValue("kmfullname") + "/" + kmvo.getAttributeValue("km") + "(" + kmEntry.getKey() + ")";
                } else {
                    kmfullname = kmvo.getAttributeValue("kmfullname") + "(" + kmEntry.getKey() + ")";
                }
                kmList.get(0).setAttributeValue("kmfullname", kmfullname);
            }
            if (!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !queryparamvo.getPk_currency().equals(DzfUtil.PK_CNY)) {
                obj = getPrintXm(3);
            } else {
                obj = getPrintXm(2);
            }
            //打印
            printReporUtil.printHz(mxmap, new SuperVO[]{}, "*科目明细账", (String[]) obj[0],
                    (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
        } else {//不需要分页打印
            if (!StringUtil.isEmpty(queryparamvo.getPk_currency())) {
                obj = getPrintXm(1);
            } else {
                obj = getPrintXm(0);
            }
            //打印
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "科目明细账", (String[]) obj[0],
                    (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
        }
    }

    @PostMapping("printcover/pdf")
    public void printCover(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
        Rectangle pageSize = PageSize.A4;
        if ("2".equals(printParamVO.getType())) {
            pageSize = PageSize.B5;
        } else {
            if ("Y".equals(printParamVO.getPageOrt())) {//横向
                pageSize = new Rectangle(pageSize.getHeight(), pageSize.getWidth());
            }
        }
        String page_num = pmap1.get("pagenum");
        if (StringUtil.isEmpty(page_num)) {
            page_num = "1";
        }
        float leftsize = Float.parseFloat(printParamVO.getLeft()) * 2.83f;
        float topsize = Float.parseFloat(printParamVO.getTop()) * 2.83f;
        Document document = new Document(pageSize, leftsize, 0, topsize, 4);
        ByteArrayOutputStream buffer = null;
        try {
            String cids = pmap1.get("corpIds");
            if (StringUtil.isEmpty(cids)) {
                cids = corpVO.getPk_corp();
            }
            // 校验
            checkSecurityData(null, cids.split(","), null);
            buffer = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, buffer);
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            ReportCoverPrintUtil printutil = new ReportCoverPrintUtil(zxkjPlatformService);
            // 赋值首字符的值
            printutil.kmCoverPrint(leftsize, topsize, document, canvas, cids.split(","), page_num, "");
        } catch (Exception e) {
            log.error("错误", e);
        } finally {
            document.close();
        }
        ServletOutputStream out = null;
        try {
            response.setContentType("application/pdf");
            response.setCharacterEncoding("utf-8");
//            response.setContentLength(buffer.size());
            out = response.getOutputStream();
            buffer.writeTo(out);
            buffer.flush();// flush 放在finally的时候流关闭失败报错
            out.flush();
        } catch (IOException e) {

        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public Object[] getPrintXm(int type) {
        Object[] obj = new Object[4];
        switch (type) {
            case 3:
                obj[0] = new String[]{"rq", "pzh", "zy", "bz", "ybjf", "jf", "ybdf", "df", "fx", "ybye", "ye"};
                obj[1] = new String[]{"日期", "凭证号", "摘要", "币别", "借方_原币", "借方_本位币", "贷方_原币", "贷方_本位币", "方向", "余额_原币", "余额_本位币"};
                obj[2] = new int[]{2, 2, 5, 1, 3, 3, 3, 3, 1, 3, 3};
                obj[3] = 20;
                break;
            case 2:
                obj[0] = new String[]{"rq", "pzh", "zy", "jf", "df", "fx", "ye"};
                obj[1] = new String[]{"日期", "凭证号", "摘要", "借方", "贷方", "方向", "余额"};
                obj[2] = new int[]{2, 2, 5, 3, 3, 1, 3};
                obj[3] = 20;
                break;
            case 1:
                obj[0] = new String[]{"km", "rq", "pzh", "zy", "bz", "ybjf", "jf", "ybdf", "df", "fx", "ybye", "ye"};
                obj[1] = new String[]{"科目", "日期", "凭证号", "摘要", "币别", "借方_原币", "借方_本位币", "贷方_原币", "贷方_本位币", "方向", "余额_原币", "余额_本位币"};
                obj[2] = new int[]{7, 2, 2, 5, 1, 2, 2, 2, 2, 1, 2, 2};
                obj[3] = 20;
                break;
            case 0:
                obj[0] = new String[]{"km", "rq", "pzh", "zy", "jf", "df", "fx", "ye"};
                obj[1] = new String[]{"科目", "日期", "凭证号", "摘要", "借方", "贷方", "方向", "余额"};
                obj[2] = new int[]{7, 3, 1, 5, 3, 3, 1, 3};//没考虑横纵向的问题，结果也不一样
                obj[3] = 20;
                break;
            default:
                break;
        }
        return obj;
    }


}

package com.dzf.zxkj.report.controller.cwzb;

import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.excel.util.Excelexport2003;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.KmConFzVoTreeStrateGyByPk;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.ReportBaseController;
import com.dzf.zxkj.report.entity.ReportExcelExportVO;
import com.dzf.zxkj.report.excel.cwzb.FsYeBExcelField;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
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
@RequestMapping("gl_rep_fsyebact")
@Slf4j
public class FsYeController  extends ReportBaseController {
    @Autowired
    private IFsYeReport gl_rep_fsyebserv;
    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    /**
     * 查询科目明细数据
     */
    @PostMapping("/queryAction")
    public ReturnData<Grid> queryAction(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
        try {
            // 校验
            checkSecurityData(null, new String[]{queryvo.getPk_corp()},null);
            /** 验证 查询范围应该在当前登录人的权限范围内 */
            corpVO = zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
            checkPowerDate(queryvo,corpVO);
            DZFBoolean isshowfs = vo.getIshowfs();
            DZFBoolean isxswyewfs = vo.getXswyewfs();
            DZFBoolean isxswyewfs_bn = vo.getXswyewfs_bn();
            vo.setIsnomonthfs(DZFBoolean.TRUE);
            FseJyeVO[] fsejyevos = null;
            /** 无余额无发生不显示 */
            vo.setXswyewfs(DZFBoolean.FALSE);
            vo.setBtotalyear(DZFBoolean.TRUE);
            /** (前提:发生为零的情况)有余额无发生也显示(科目账表查询需要显示无发生也显示) */
            vo.setIshowfs(DZFBoolean.TRUE);
            fsejyevos = gl_rep_fsyebserv.getFsJyeVOs(vo,1);
            List<FseJyeVO> fsjyevoList = new ArrayList<FseJyeVO>();
            if (fsejyevos != null && fsejyevos.length > 0) {
                Set<String> conkmids  = filterDatas(isshowfs,isxswyewfs,isxswyewfs_bn,fsejyevos,vo);
                for (FseJyeVO fsjye : fsejyevos) {// 级次
                    String kmid = fsjye.getPk_km().substring(0, 24);
                    if(conkmids.contains(kmid)){
                        fsjyevoList.add(fsjye);
                    }
                }
                fsejyevos = fsjyevoList.toArray(new FseJyeVO[0]);
                /** 转换成tree类型 */
//                FseJyeVO fsvo = (FseJyeVO) BDTreeCreator.createTree(fsejyevos, new KmConFzVoTreeStrateGyByPk(isshowfs,isxswyewfs,isxswyewfs_bn));
//                fsejyevos = (FseJyeVO[]) fsvo.getChildren();
                fsejyevos = getTotalRow(fsejyevos,false);
                log.info("查询成功！");
                grid.setSuccess(true);
                grid.setTotal(fsejyevos == null ? 0 : (long) Arrays.asList(fsejyevos).size());
                grid.setRows(fsejyevos == null ? new ArrayList<FseJyeVO>() : Arrays.asList(fsejyevos));
            }else{
                grid.setSuccess(false);
                grid.setRows(new ArrayList<FseJyeVO>());
                grid.setMsg("查询数据为空");
            }
        } catch (Exception e) {
            grid.setRows(new ArrayList<FseJyeVO>());
            printErrorLog(grid, e, "查询失败！");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT,
                "发生额及余额表查询:"+vo.getBegindate1().toString().substring(0, 7) +"-"+
                        vo.getEnddate().toString().substring(0, 7), ISysConstants.SYS_2);
        return ReturnData.ok().data(grid);
    }

    public Set<String> filterDatas(DZFBoolean isshowfs, DZFBoolean isxswyewfs, DZFBoolean isxswyewfs_bn,
                                    FseJyeVO[] fsejyevos,QueryParamVO vo) {
        Set<String> ids = new HashSet<String>();
        for (int i = fsejyevos.length - 1; i >= 0; i--) {
            if (vo.getCjq() > fsejyevos[i].getAlevel() || fsejyevos[i].getAlevel() > vo.getCjz()) {
                continue;
            }
            //科目id
            String kmid = fsejyevos[i].getPk_km().substring(0, 24);
            boolean isadd = filterData(isshowfs, isxswyewfs, isxswyewfs_bn, fsejyevos[i]);
            if (!isadd || ids.contains(kmid)) {
                //本级id和相关的上级id
                ids.add(fsejyevos[i].getPk_km());
                if(!StringUtil.isEmpty(fsejyevos[i].getPk_km_parent())){
                    ids.add(fsejyevos[i].getPk_km_parent());
                }
            }
        }
        return ids;
    }

    private boolean filterData(DZFBoolean isshowfs, DZFBoolean isxswyewfs, DZFBoolean isxswyewfs_bn, FseJyeVO fsjye){
        if (isshowfs == null || !isshowfs.booleanValue()) {
            if (ReportUtil.isNullNum(fsjye.getFsjf()) && ReportUtil.isNullNum(fsjye.getFsdf())) {
                return true;
            }
        }
        if (isxswyewfs != null && isxswyewfs.booleanValue()) {
            if (ReportUtil.isNullNum(fsjye.getFsjf()) && ReportUtil.isNullNum(fsjye.getFsdf())
                    && ReportUtil.isNullNum(fsjye.getQmjf()) && ReportUtil.isNullNum(fsjye.getQmdf())) {
                return true;
            }
        }

        if (isxswyewfs_bn != null && isxswyewfs_bn.booleanValue()) {
            if (ReportUtil.isNullNum(fsjye.getJftotal()) && ReportUtil.isNullNum(fsjye.getDftotal())
                    && ReportUtil.isNullNum(fsjye.getQmjf()) && ReportUtil.isNullNum(fsjye.getQmdf())) {
                return true;
            }
        }
        return false;
    }

    private FseJyeVO[] getTotalRow(FseJyeVO[] fsejyevo,boolean btree){
        if(fsejyevo==null || fsejyevo.length==0){
            return fsejyevo;
        }
        List<String> codelist = new ArrayList<String>();
        DZFDouble qcjfhj = DZFDouble.ZERO_DBL;
        DZFDouble qcdfhj = DZFDouble.ZERO_DBL;
        DZFDouble fsjfhj = DZFDouble.ZERO_DBL;
        DZFDouble fsdfhj = DZFDouble.ZERO_DBL;
        DZFDouble jftotalhj = DZFDouble.ZERO_DBL;
        DZFDouble dftotalhj = DZFDouble.ZERO_DBL;
        DZFDouble qmjfhj = DZFDouble.ZERO_DBL;
        DZFDouble qmdfhj = DZFDouble.ZERO_DBL;

        /** -----------原币----------- */
        DZFDouble ybqcjfhj = DZFDouble.ZERO_DBL;
        DZFDouble ybqcdfhj = DZFDouble.ZERO_DBL;
        DZFDouble ybfsjfhj = DZFDouble.ZERO_DBL;
        DZFDouble ybfsdfhj = DZFDouble.ZERO_DBL;
        DZFDouble ybjftotalhj = DZFDouble.ZERO_DBL;
        DZFDouble ybdftotalhj = DZFDouble.ZERO_DBL;
        DZFDouble ybqmjfhj = DZFDouble.ZERO_DBL;
        DZFDouble ybqmdfhj = DZFDouble.ZERO_DBL;

        /** 是否已这个开始的 */
        for (int i = 0; i < fsejyevo.length; i++) {
            //如果是树状结构，不需要continue
            if(!btree){
                if(codelist.size()>0){
                    boolean tt = bKmStartWith(fsejyevo, codelist, i);
                    if(tt){
                        continue;
                    }
                }
            }
            codelist.add(fsejyevo[i].getKmbm());

            qcjfhj = SafeCompute.add(qcjfhj, fsejyevo[i].getQcjf());
            qcdfhj = SafeCompute.add(qcdfhj, fsejyevo[i].getQcdf());
            fsjfhj = SafeCompute.add(fsjfhj, fsejyevo[i].getFsjf());
            fsdfhj = SafeCompute.add(fsdfhj, fsejyevo[i].getFsdf());
            jftotalhj = SafeCompute.add(jftotalhj, fsejyevo[i].getJftotal());
            dftotalhj = SafeCompute.add(dftotalhj, fsejyevo[i].getDftotal());
            qmjfhj = SafeCompute.add(qmjfhj, fsejyevo[i].getQmjf());
            qmdfhj = SafeCompute.add(qmdfhj, fsejyevo[i].getQmdf());

            /** ---------------原币------------------*/
            ybqcjfhj = SafeCompute.add(ybqcjfhj, fsejyevo[i].getYbqcjf());
            ybqcdfhj = SafeCompute.add(ybqcdfhj, fsejyevo[i].getYbqcdf());
            ybfsjfhj = SafeCompute.add(ybfsjfhj, fsejyevo[i].getYbfsjf());
            ybfsdfhj = SafeCompute.add(ybfsdfhj, fsejyevo[i].getYbfsdf());
            ybjftotalhj = SafeCompute.add(ybjftotalhj, fsejyevo[i].getYbjftotal());
            ybdftotalhj = SafeCompute.add(ybdftotalhj, fsejyevo[i].getYbdftotal());
            ybqmjfhj = SafeCompute.add(ybqmjfhj, fsejyevo[i].getYbqmjf());
            ybqmdfhj = SafeCompute.add(ybqmdfhj, fsejyevo[i].getYbqmdf());
        }
        FseJyeVO total = new FseJyeVO();
        total.setPk_km("999999999999999");
        total.setKmbm("");
        total.setQcjf(qcjfhj);
        total.setQcdf(qcdfhj);
        total.setFsjf(fsjfhj);
        total.setFsdf(fsdfhj);
        total.setJftotal(jftotalhj);
        total.setDftotal(dftotalhj);
        total.setQmjf(qmjfhj);
        total.setQmdf(qmdfhj);
        total.setYbqcjf(ybqcjfhj);
        total.setYbqcdf(ybqcdfhj);
        total.setYbfsjf( ybfsjfhj);
        total.setYbfsdf( ybfsdfhj);
        total.setYbjftotal( ybjftotalhj);
        total.setYbdftotal(ybdftotalhj);
        total.setYbqmjf(ybqmjfhj);
        total.setYbqmdf( ybqmdfhj );
        FseJyeVO[] res = new FseJyeVO[fsejyevo.length+1];
        System.arraycopy(fsejyevo, 0, res, 0, fsejyevo.length);
        res[fsejyevo.length] = total;
        return res;
    }

    private boolean bKmStartWith(FseJyeVO[] fsejyevo, List<String> codelist, int i) {
        for(String code:codelist){
            if(fsejyevo[i].getKmbm().startsWith(code)){
                return true;
            }
        }
        return false;
    }

    private FseJyeVO[] conversionTree(FseJyeVO[] fsejyevos,Integer level,Integer cjz,String firstkm
            ,DZFBoolean isshowfs, DZFBoolean isxswyewfs, DZFBoolean isxswyewfs_bn
    ) {
        if(fsejyevos == null || fsejyevos.length ==0){
            return fsejyevos;
        }
        if(cjz == null ){
            /** 最多10级 */
            cjz = 10;
        }
        List<FseJyeVO> res = new ArrayList<FseJyeVO>();
        for (FseJyeVO vo : fsejyevos) {
            if ((vo.getAlevel() == level.intValue()
                    && (StringUtil.isEmpty(vo.getPk_km()) || vo.getPk_km().length() == 24))
                    || (!StringUtil.isEmpty(firstkm) && firstkm.equals(vo.getKmbm()))) {
                getTree(vo, fsejyevos, vo.getAlevel() + 1, cjz);
                res.add(vo);
            }
        }
        return res.toArray(new FseJyeVO[0]);
    }


    private void getTree(FseJyeVO vo, FseJyeVO[] fsejyevos, int i,int cjz) {
        for (FseJyeVO childvo : fsejyevos) {
            if (childvo.getAlevel() == i
                    && (StringUtil.isEmpty(childvo.getPk_km()) || childvo.getPk_km().length() == 24)
                    && childvo.getKmbm().startsWith(vo.getKmbm())) {
                if (i <= cjz) {
                    getTree(childvo, fsejyevos, i + 1, cjz);
                    vo.addChildren(childvo);
                }
            }
            /** 辅助项目 */
            else if (!StringUtil.isEmpty(childvo.getPk_km()) && childvo.getPk_km().length() > 24
                    && childvo.getKmbm().startsWith(vo.getKmbm()) && childvo.getAlevel() + 1 == i) {
                vo.addChildren(childvo);
            }
        }
    }

    @PostMapping("/queryYear")
    public ReturnData<Grid> queryYear(@MultiRequestBody QueryParamVO queryvo, @MultiRequestBody CorpVO corpVO) {
        // 校验
        checkSecurityData(null, new String[]{queryvo.getPk_corp()},null);
        Map<String,FseJyeVO[]> map = new HashMap<String,FseJyeVO[]>();
        QueryParamVO vo = getQueryParamVO(queryvo,corpVO);
        String accountrule = zxkjPlatformService.queryAccountRule(vo.getPk_corp());
        /** 进项税额 */
        String jxkm = "22210101";
        /** 销项税额 */
        String xxkm = "22210102";
        /** 未交增值税 */
        String wjzzs = "222109";
        /** 减免税款 */
        String jmsk = "22210106";
        if(accountrule.startsWith("4/3/3")){
            jxkm = "2221001001";
            xxkm = "2221001002";
            wjzzs = "2221009";
            jmsk = "2221001006";
        }else if(accountrule.startsWith("4/3/2")){
            jxkm = "222100101";
            xxkm = "222100102";
            wjzzs = "2221009";
            jmsk = "222100106";
        }
        String kms = "1001,1002,1012," + jxkm + "," + xxkm + "," + wjzzs
                + "," + jmsk + ",5001,5051,6001,6051";
        vo.setKms(kms);
        vo.setKmcodelist(Arrays.asList(kms.split(",")));

        vo.setCjq(1);
        vo.setCjz(6);
        vo.setIshasjz(DZFBoolean.FALSE);
        vo.setXswyewfs(DZFBoolean.FALSE);
        /** 验证 查询范围应该在当前登录人的权限范围内 */
        DZFDate bdate = vo.getBegindate1();
        DZFDate jzdate = corpVO.getBegindate();
        int jzyear = jzdate.getYear();
        int year = bdate.getYear();
        int jzmonth = jzdate.getMonth();
        if(jzyear == year){
            vo.setBegindate1(jzdate);
        }else{
            jzmonth = 1;
        }
        vo.setEnddate(new DZFDate(year+"-12-01"));
        KmMxZVO[] kmmxvos = gl_rep_kmmxjserv.getKMMXZVOs(vo,null);
        String m =null;
        FseJyeVO fsevo =null;
        FseJyeVO fsevo1 =null;
        FseJyeVO fsevo2 =null;
        FseJyeVO fsevo3 =null;
        FseJyeVO fsevo4 =null;
        FseJyeVO fsevo5 =null;
        FseJyeVO fsevo6 =null;
        FseJyeVO fsevo7 =null;
        if(kmmxvos!=null && kmmxvos.length>0){
            for(int i =jzmonth;i<13;i++){
                m = i<10?"0"+i : ""+i;
                fsevo = new FseJyeVO();
                fsevo1 = new FseJyeVO();
                fsevo2 = new FseJyeVO();
                fsevo3 = new FseJyeVO();
                fsevo4 = new FseJyeVO();
                fsevo5 = new FseJyeVO();
                fsevo6 = new FseJyeVO();
                fsevo7 = new FseJyeVO();
                fsevo.setKmmc("进项税额");
                fsevo1.setKmmc("主营业务收入");
                fsevo2.setKmmc("销项税额");
                fsevo3.setKmmc("未交增值税");
                fsevo4.setKmmc("货币资金净发生");
                fsevo5.setKmmc("货币资金余额");
                fsevo6.setKmmc("其他业务收入");
                fsevo7.setKmmc("减免税款");
                for(KmMxZVO kmvo : kmmxvos){
                    if(kmvo.getRq().substring(0, 7).equals(year+"-"+m)){
                        if(kmvo.getKmbm().equals(jxkm)){
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)){
                                fsevo.setFsdf(kmvo.getDf());
                                fsevo.setFsjf(kmvo.getJf());
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)){
                                fsevo.setDftotal(kmvo.getDf());
                                fsevo.setJftotal(kmvo.getJf());
                            }
                        } else if(kmvo.getKmbm().equals(xxkm)){
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)){
                                fsevo2.setFsdf(kmvo.getDf());
                                fsevo2.setFsjf(kmvo.getJf());
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)){
                                fsevo2.setDftotal(kmvo.getDf());
                                fsevo2.setJftotal(kmvo.getJf());
                            }
                        } else if(kmvo.getKmbm().equals(jmsk)){
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)){
                                fsevo7.setFsdf(kmvo.getDf());
                                fsevo7.setFsjf(kmvo.getJf());
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo)){
                                fsevo7.setDftotal(kmvo.getDf());
                                fsevo7.setJftotal(kmvo.getJf());
                            }
                        } else if (kmvo.getKmbm().equals(wjzzs)) {
                            if("期初余额".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo3.setQcdf(kmvo.getYe());
                            }
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo3.setFsdf(kmvo.getDf());
                                fsevo3.setFsjf(kmvo.getJf());
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo3.setQmdf(kmvo.getYe());
                                fsevo3.setDftotal(kmvo.getDf());
                                fsevo3.setJftotal(kmvo.getJf());
                            }
                        } else if((kmvo.getKmbm().equals("6001")||kmvo.getKmbm().equals("5001"))){
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo1.setFsdf(kmvo.getDf());
                                fsevo1.setFsjf(kmvo.getJf());
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo1.setDftotal(kmvo.getDf());
                                fsevo1.setJftotal(kmvo.getJf());
                            }
                        } else if((kmvo.getKmbm().equals("6051")||kmvo.getKmbm().equals("5051"))){
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo6.setFsdf(kmvo.getDf());
                                fsevo6.setFsjf(kmvo.getJf());
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo6.setDftotal(kmvo.getDf());
                                fsevo6.setJftotal(kmvo.getJf());
                            }
                        } else if ("1001".equals(kmvo.getKmbm())
                                || "1002".equals(kmvo.getKmbm())
                                || "1012".equals(kmvo.getKmbm())) {
                            if("本月合计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                DZFDouble mny = SafeCompute.sub(kmvo.getJf(), kmvo.getDf());
                                fsevo4.setFsjf(SafeCompute.add(fsevo4.getFsjf(), mny));
                            }
                            if("本年累计".equals(kmvo.getZy()) && ReportUtil.bSysZy(kmvo) ){
                                fsevo5.setQmjf(SafeCompute.add(fsevo5.getQmjf(), kmvo.getYe()));
                            }
                        }
                    }
                }
                map.put(year+"-"+m, new FseJyeVO[]{fsevo,fsevo1,fsevo2, fsevo3,
                        fsevo4, fsevo5, fsevo6, fsevo7});
            }
        }
        return ReturnData.ok().data(map);
    }

    @Override
    public String getPrintTitleName() {
        return "发 生 额 及 余 额 表";
    }




    //导出Excel
    @PostMapping("export/excel")
    public void excelReport(@MultiRequestBody ReportExcelExportVO excelExportVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO, HttpServletResponse response) {
        boolean bexport = checkExcelExport(corpVO.getPk_corp(),response);
        if(!bexport){
            return;
        }
        // 校验
        checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
        Excelexport2003<FseJyeVO> lxs = new Excelexport2003<FseJyeVO>();
        FsYeBExcelField fsyebfield =  getFsExcel(excelExportVO,queryparamvo,corpVO);
        baseExcelExport(response,lxs,fsyebfield);
//        QueryParamVO vo = getQueryParamVO();
        writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT, "发生额及余额表导出:"
                        + queryparamvo.getBegindate1().toString().substring(0, 7) + "-" + queryparamvo.getEnddate().toString().substring(0, 7),
                ISysConstants.SYS_2);
    }
    private String getCNName(int month) {
        String[] cnnames = new String[] { "一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月", "九月", "十月", "十一月", "十二月" };

        return cnnames[month - 1];
    }

    private FsYeBExcelField getFsExcel(ReportExcelExportVO excelExportVO,KmReoprtQueryParamVO vo,CorpVO cpvo) {
        String nodename = "发生额余额表";
        String currencyname = new ReportUtil().getCurrencyDw(vo.getCurrency());
        String[] periods = null;
        String[] allsheetname = null;
        String qj = "";
        String corpname = "";

//        CorpVO querycorpvo = zxkjPlatformService.queryCorpByPk(vo.getPk_corp());
        corpname = excelExportVO.getCorpName();
        qj = excelExportVO.getTitleperiod();

        List<FseJyeVO[]> fslist = new ArrayList<FseJyeVO[]>();
        if ("0".equals(excelExportVO.getExcelsel())) {// 当前期间
            allsheetname = new String[] { getCNName(vo.getBegindate1().getMonth()) };
            periods = new String[] { qj };
//            FseJyeVO[] fsejyevos = gl_rep_fsyebserv.getFsJyeVOs(vo,1);
            FseJyeVO[] fsejyevos = (FseJyeVO[])JsonUtils.deserialize(excelExportVO.getList(), FseJyeVO[].class);
//            fsejyevos = getTotalRow(fsejyevos,false);
            putFsyeOnKmlb(fsejyevos);
            fslist.add(fsejyevos);
        } else {
            vo.setBegindate1(new DZFDate(vo.getEnddate().getYear() + "-01-01"));
            vo.setEnddate(new DZFDate(vo.getEnddate().getYear() + "-12-01"));
            // 根据vo查询明细账数据
            if(cpvo.getBegindate().getYear() == vo.getBegindate1().getYear()){
                vo.setBegindate1(cpvo.getBegindate());
            }
            Object[] qryobjs = gl_rep_kmmxjserv.getKMMXZVOs1(vo, false);
            String year = vo.getBegindate1().getYear() + "";
            String pk_corp = vo.getPk_corp();
            Object[] fsobjs = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp, qryobjs, "");
            Map<String, List<FseJyeVO>> maps = (Map<String, List<FseJyeVO>>) fsobjs[0];
            List<String> sheetnamelist = new ArrayList<String>();
            FseJyeVO[] tvos = null;
            List<String> periodlist = ReportUtil.getPeriods(vo.getBegindate1(), vo.getEnddate());
            for(String period:periodlist){
                tvos = getTotalRow(maps.get(period).toArray(new FseJyeVO[0]),false);
                putFsyeOnKmlb(tvos);
                fslist.add(tvos);
                sheetnamelist.add(getCNName(Integer.parseInt(period.substring(5, 7))));
            }
            allsheetname = sheetnamelist.toArray(new String[0]);
            periods = periodlist.toArray(new String[0]);
        }
        FsYeBExcelField field = new FsYeBExcelField(nodename, vo.getPk_currency(), currencyname, periods, allsheetname, qj,
                corpname);
        field.setAllsheetzcvos(fslist);
        return field;
    }

    public void putFsyeOnKmlb(FseJyeVO[] vos) {
        if (vos != null && vos.length > 0) {
            for(FseJyeVO vo:vos){
                if(StringUtil.isEmpty(vo.getKmlb())){
                    vo.setKmlb("");
                }
                switch (vo.getKmlb()) {
                    case "0":
                        vo.setKmlb("资产");
                        break;
                    case "1":
                        vo.setKmlb("负债");
                        break;
                    case "2":
                        vo.setKmlb("共同");
                        break;
                    case "3":
                        vo.setKmlb("所有者权益");
                        break;
                    case "4":
                        vo.setKmlb("成本");
                        break;
                    case "5":
                        vo.setKmlb("损益");
                        break;
                    default:
                        vo.setKmlb("合计:");
                        break;
                }
            }
        }
    }

    @PostMapping("print")
    public void printAction(@RequestParam Map<String, String> pmap1, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response){

        PrintParamVO printParamVO = JsonUtils.deserialize(JsonUtils.serialize(pmap1), PrintParamVO.class);
        KmReoprtQueryParamVO queryparamvo = JsonUtils.deserialize(JsonUtils.serialize(pmap1), KmReoprtQueryParamVO.class);

        try {
            // 校验
            checkSecurityData(null, new String[]{queryparamvo.getPk_corp()},null);
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            /** 是否横向 */
            printReporUtil.setIscross(DZFBoolean.TRUE);
            FseJyeVO[] bodyvos = JsonUtils.deserialize(printParamVO.getList(), FseJyeVO[].class);
            putFsyeOnKmlb(bodyvos);
            /** 声明一个map用来存title */
            Map<String, String> tmap = new LinkedHashMap<String, String>();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil().getCurrencyDw(queryparamvo.getCurrency()));

            /** 设置表头字体 */
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));
            Object[] obj = null;
            printReporUtil.setLineheight(22f);
            if(!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !queryparamvo.getPk_currency().equals(DzfUtil.PK_CNY)){
                obj = getPrintXm(1);
            }else{
                obj = getPrintXm(0);
            }
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>() ,bodyvos,"发 生 额 及 余 额 表",(String[])obj[0],
                    (String[])obj[1], (int[])obj[2],(int)obj[3],pmap,tmap);
            writeLogRecord(LogRecordEnum.OPE_KJ_KMREPORT, "发生额及余额表打印:"
                            + printParamVO.getTitleperiod(),
                    ISysConstants.SYS_2);
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
    }

    public Object[] getPrintXm(int type){
        Object[] obj = new Object[4];
        switch (type) {
            case 1:
                obj[0] = new String[]{"kmlb","kmbm","kmmc","ybqcjf","qcjf","ybqcdf","qcdf",
                        "ybfsjf","fsjf","ybfsdf","fsdf",
                        "ybjftotal","jftotal","ybdftotal","dftotal",
                        "ybqmjf","qmjf","ybqmdf","qmdf"};
                obj[1] = new String[]{"科目类别","科目编码","科目名称",
                        "期初余额_借方(原币)","期初余额_借方(本位币)","期初余额_贷方(原币)","期初余额_贷方(本位币 )",
                        "本期发生额_借方(原币)","本期发生额_借方(本位币)",
                        "本期发生额_贷方(原币)","本期发生额_贷方(本位币)",
                        "本年累计发生额_借方(原币)","本年累计发生额_借方(本位币)",
                        "本年累计发生额_贷方(原币)","本年累计发生额_贷方(本位币)",
                        "期末余额_借方(原币)","期末余额_借方(本位币)","期末余额_贷方(原币)","期末余额_贷方(本位币)"};
                obj[2] = new int[]{2,2,5,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3};
                obj[3] = 60;
                break;
            case 0:
                obj[0] = new String[]{"kmlb","kmbm","kmmc","qcjf","qcdf","fsjf","fsdf","jftotal","dftotal","qmjf","qmdf"};
                obj[1] = new String[]{"科目类别","科目编码","科目名称","期初余额_借方","期初余额_贷方","本期发生额_借方","本期发生额_贷方","本年累计发生额_借方","本年累计发生额_贷方","期末余额_借方","期末余额_贷方"};
                obj[2] = new int[]{3,4,6,5,5,5,5,5,5,5,5};
                obj[3] = 60;
                break;
            default:
                break;
        }
        return obj;
    }


}

package com.dzf.zxkj.platform.controller.qcset;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.QcYeCurJson;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.qcset.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.qcset.IFzhsqcService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 *  科目期初
 */
@RestController
@RequestMapping("/qcset/gl_qcyeact")
@Slf4j
public class QcyeController extends BaseController {
    @Autowired
    private IQcye gl_qcyeserv;
    @Autowired
    private IFzhsqcService gl_fzhsqcserv;
    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private ISecurityService securityserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;

    @GetMapping("query")
    public ReturnData<QueryVOClassify> query(String rmb,String isShowFc) {

        QueryVOClassify json = new QueryVOClassify();
        String corpid = SystemUtil.getLoginCorpId();
        try {
            if (corpid != null && !"".equals(corpid)) {
                Map<String, QcYeVO[]> maps = gl_qcyeserv.query(corpid, rmb,isShowFc);
                json.setO0(maps.get("0"));
                json.setO1(maps.get("1"));
                json.setO2(maps.get("2"));
                json.setO3(maps.get("3"));
                json.setO4(maps.get("4"));
                json.setO5(maps.get("5"));
                json.setFzqc(gl_fzhsqcserv.queryAll(corpid, rmb));
            }
            json.setSuccess(true);
            json.setMsg("查询成功！");
        } catch (Exception e) {
            printQueryErrorLog(json, e, "查询失败！");
        }
        return  ReturnData.ok().data(json);
    }
    @GetMapping("gdzcsync")
    public ReturnData gdzcsync() {
        Json json = new Json();
        try {
            CorpVO corpVO = SystemUtil.getLoginCorpVo();
            UserVO userVO = SystemUtil.getLoginUserVo();
            gl_qcyeserv.saveGdzcsync(userVO.getCuserid(), corpVO.getBegindate(),corpVO.getPk_corp());
            json.setSuccess(true);
            json.setMsg("固定资产同步成功！");
        } catch (Exception e) {
            printErrorLog(json, e, "固定资产同步失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "固定资产同步", ISysConstants.SYS_2);
        return ReturnData.ok().data(json);
    }
    @GetMapping("kcsync")
    public ReturnData kcsync(String pk_corp, @MultiRequestBody CorpVO corpVO,@MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            securityserv.checkSecurityForOther(pk_corp, corpVO.getPk_corp(), userVO.getCuserid());
            /*
             *  库存老模式：总账同步到库存
             *	库存新模式：库存同步到总账
             */
            StringBuffer msg = new StringBuffer();
            String userid = userVO.getCuserid();
            Integer isstyle = corpVO.getIbuildicstyle();
            if(IcCostStyle.IC_ON.equals(corpVO.getBbuildic())){
                if(isstyle == null || isstyle == 0){
                    gl_qcyeserv.saveGL2KcSync(userid, pk_corp, msg);
                }else if(isstyle == 1){
                    gl_qcyeserv.saveKc2GLSync(userid, pk_corp);
                }
            }else{
                throw new BusinessException("未启用库存模块，请检查");
            }

            boolean syncflag = msg.length() == 0;

            json.setSuccess(syncflag);
            json.setMsg(syncflag ? "库存期初同步成功！" : msg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "库存期初同步失败！");
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "库存期初同步", ISysConstants.SYS_2);
        return  ReturnData.ok().data(json);
    }
    @PostMapping("save")
    public ReturnData save(@MultiRequestBody QcYeVO[] bodyvos, @MultiRequestBody String rmb, @MultiRequestBody CorpVO corpVO , @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            for (QcYeVO qcYeVO : bodyvos) {
                // 权限验证
                if (!corpVO.getPk_corp().equals(qcYeVO.getPk_corp())) {
                    throw new BusinessException("无权操作！");
                }
                // 修改保存前数据安全验证
                QcYeVO getvo = gl_qcyeserv.queryByPrimaryKey(qcYeVO
                        .getPrimaryKey());
                if (getvo != null && !corpVO.getPk_corp().equals(getvo.getPk_corp())) {
                    throw new BusinessException("出现数据无权问题，无法修改！");
                }
            }
            DZFDate jzDate = corpVO.getBegindate();
            int year = jzDate.getYear();
            List<QmclVO> qmcls = qmgzService.yearhasGz(corpVO.getPk_corp(),
                    String.valueOf(year));
            for (QmclVO qmvo : qmcls) {
                if (qmvo.getIsgz().booleanValue()) {
                    throw new BusinessException("已经存在关账的月份，不允许修改期初数据哦！");
                }
            }
            String pkcurrence = rmb;
//            String srt = rmb;
//            if (srt != null) {
//                Object o = JSON.parse(srt);
//                pkcurrence = ((Map<String, String>) o).get("rmb");
//            }
            jzDate = DZFDate.getDate(DateUtils.getPeriod(jzDate) + "-01");
            if (!StringUtil.isEmpty(pkcurrence)) {
                gl_qcyeserv.save(userVO.getCuserid(), jzDate, pkcurrence, corpVO.getPk_corp(), bodyvos);
                json.setSuccess(true);
                json.setMsg("保存成功！");
            } else {
                json.setSuccess(false);
                json.setMsg("币种为空，保存失败！");
            }
        } catch (Exception e) {
            printErrorLog(json, e, "保存失败！");
        }

        writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "期初科目余额编辑", ISysConstants.SYS_2);

        return ReturnData.ok().data(json);
    }

    /**
     * 清楚全部数据
     */
    @GetMapping("deleteAct")
    public ReturnData deleteAct(String atype) {
        Json json = new Json();
        try {
            CorpVO corpVO = SystemUtil.getLoginCorpVo();
            String corpid = corpVO.getPk_corp();
            if ( corpid == null	|| "".equals(corpid) ) {
                throw new BusinessException("出现数据无权问题，无法修改！");
            }
            CorpVO corp = corpVO;
            DZFDate jzDate = corp.getBegindate();
            int year = jzDate.getYear();
            List<QmclVO> qmcls = qmgzService.yearhasGz(corpid,String.valueOf(year));
            for (QmclVO qmvo : qmcls) {
                if (qmvo.getIsgz().booleanValue()) {
                    throw new BusinessException("已经存在关账的月份，不允许删除期初数据哦！");
                }
            }
            if("0".equals(atype)){
                gl_qcyeserv.deleteFs(corpid);
                json.setMsg("清除发生数据成功！");
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "科目期初-清除发生", ISysConstants.SYS_2);
            }else if("1".equals(atype)){
                gl_qcyeserv.deleteAll(corpid);
                json.setMsg("清除所有数据成功！");
                writeLogRecord(LogRecordEnum.OPE_KJ_BDSET, "科目期初-全部清除", ISysConstants.SYS_2);
            }
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "清除失败！");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 试算平衡
     */
    @PostMapping("ssph")
    public ReturnData ssph(@MultiRequestBody CorpVO corpVO , @MultiRequestBody UserVO userVO) {
        SsphRes ss = null;
        try {
            String corpid = corpVO.getPk_corp();
            ss = gl_qcyeserv.ssph(corpid);
            ss.setSuccess(true);
            ss.setMsg("试算成功！");
        } catch (Exception e) {
            printSSphErrorLog(ss, e, "试算失败！");
        }
        return ReturnData.ok().data(ss);
    }

    public QcYeVO[] getForwardData(HttpServletRequest request) {
        List<QcYeVO> list = new ArrayList<QcYeVO>();
        Map<String, String> bodymapping = null;
        QcYeVO[] bodyvos = null;
        String str = "";
        for (int i = 0; i < 6; i++) {
            str = request.getParameter("kmdata" + i);
            bodyvos = JsonUtils.deserialize(str, QcYeVO[].class);
            if (bodyvos != null && bodyvos.length > 0) {
                list.addAll(new ArrayList<QcYeVO>(Arrays.asList(bodyvos)));
            }
        }
        return list.toArray(new QcYeVO[0]);
    }
    @PostMapping("verifySave")
    public ReturnData verifySave (HttpServletRequest request) {
        Json json = new Json();
        try {
            CorpVO corp = SystemUtil.getLoginCorpVo();
            DZFDate jzDate = corp.getBegindate();
            int year = jzDate.getYear();
            List<QmclVO> qmcls = qmgzService.yearhasGz(corp.getPk_corp(),
                    String.valueOf(year));
            for (QmclVO qmvo : qmcls) {
                if (qmvo.getIsgz().booleanValue()) {
                    throw new BusinessException("已经存在关账的月份，不允许修改未核销期初！");
                }
            }
            String pk_km = request.getParameter("pk_km");
            VerifyBeginVo[] vos = JsonUtils.deserialize(request.getParameter("vdata"),VerifyBeginVo[].class);
            gl_qcyeserv.saveVerifyBegin(pk_km, corp, vos);
            json.setMsg("保存成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "保存未核销期初失败！");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("queryVerifyBegin")
    public ReturnData queryVerifyBegin (HttpServletRequest request, @MultiRequestBody CorpVO corpVO , @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            String pk_km = request.getParameter("pk_km");
            List<VerifyBeginVo> rs = gl_qcyeserv.queryVerifyBegin(corpVO.pk_corp, pk_km);
            json.setRows(rs);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询未核销期初失败！");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("queryVerifyBeginAccounts")
    public ReturnData queryVerifyBeginAccounts (HttpServletRequest request, @MultiRequestBody CorpVO corpVO , @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            List<String> rs = gl_qcyeserv.queryVerifyBeginAccounts(corpVO.getPk_corp());
            json.setRows(rs.size() > 0 ? StringUtil.toString(rs) : "");
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败！");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 打印操作
     */
    @PostMapping("print/pdf")
    public void printAction(String corpName, String period, @MultiRequestBody PrintParamVO printParamVO, @MultiRequestBody KmReoprtQueryParamVO queryparamvo, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, response);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            Boolean showAmount = Boolean.valueOf(printParamVO.getShowAmount());
            String strlist = printParamVO.getList();
            if (strlist == null) {
                return;
            }
            QcYeVO qc = new QcYeVO();
            Map<String, String> bodymapping = FieldMapping.getFieldMapping(qc);
            QcYeVO[] bodyvos =  JsonUtils.deserialize(strlist, QcYeVO[].class);
            printReporUtil.setIscross(DZFBoolean.FALSE);// 是否横向
            Map<String, String> tmap = new LinkedHashMap<String, String>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("币别", printParamVO.getCurrencyname());
            tmap.put("汇率", printParamVO.getExtra());
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(pmap.get("font")), Font.NORMAL));//设置表头字体
            if (showAmount) {
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos,
                        "科目期初", new String[] { "kminfo", "doperatedate",
                                "direct", "jldw", "bnqcnum", "yearqc",
                                "bnfsnum", "yearjffse", "bndffsnum",
                                "yeardffse", "monthqmnum", "thismonthqc" },
                        new String[] { "科目", "录入日期", "方向", "计量单位", "本年期初数量",
                                "本年期初金额", "本年借方发生数量", "本年借方发生金额", "本年贷方发生数量",
                                "本年贷方发生金额", "本月期初数量", "本月期初金额" }, new int[] {
                                5, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3 }, 50,
                        pmap,tmap);
            } else {
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos,
                        "科目期初", new String[] { "kminfo", "doperatedate",
                                "direct", "yearqc", "yearjffse", "yeardffse",
                                "thismonthqc" }, new String[] { "科目", "录入日期",
                                "方向", "本年期初金额", "本年借方发生金额", "本年贷方发生金额",
                                "本月期初金额" }, new int[] { 5, 3, 1, 3, 3, 3, 3 },
                        50, pmap,tmap);
            }
        } catch (Exception e) {
            log.error("打印错误:", e);
        }
    }

    private void printCurErrorLog(QcYeCurJson json, Throwable e,
                                  String errorinfo) {
        if (StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if (e instanceof BusinessException) {
            json.setMsg(e.getMessage());
        } else {
            json.setMsg(errorinfo);
            log.error(errorinfo, e);
        }
        json.setSuccess(false);
    }

    private void printSSphErrorLog(SsphRes json,Throwable e,
                                   String errorinfo) {
        if (StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if (e instanceof BusinessException) {
            json.setMsg(e.getMessage());
        } else {
            json.setMsg(errorinfo);
            log.error(errorinfo, e);
        }
        json.setSuccess(false);
    }

    private void printQueryErrorLog(QueryVOClassify json,
                                    Throwable e, String errorinfo) {
        if (StringUtil.isEmpty(errorinfo))
            errorinfo = "操作失败";
        if (e instanceof BusinessException) {
            json.setMsg(e.getMessage());
        } else {
            json.setMsg(errorinfo);
            log.error(errorinfo, e);
        }
        json.setSuccess(false);
    }

    @PostMapping("export/excel")
    public void exportExcel(@RequestBody Map<String, String> map, HttpServletRequest request, HttpServletResponse response) {
        OutputStream toClient = null;
        try {
//            String isFzqc = request.getParameter("fzqc");
            String isFzqc = map.get("fzqc");
            boolean showQuantity = Boolean.valueOf(map.get("showQuantity"));

            byte[] excelData = null;
            String fileName = null;
            if ("Y".equals(isFzqc)) {
                fileName = "辅助期初导入.xls";
                String pk_km = map.get("pk_km");
                String tempName = map.get("tempName");
                Resource exportTemplate = new ClassPathResource(DZFConstant.DZF_KJ_EXCEL_TEMPLET + tempName);
                excelData = gl_qcyeserv.exportFzExcel(SystemUtil.getLoginCorpId(), pk_km, ((ClassPathResource) exportTemplate).getPath(), showQuantity);
            } else {
                fileName = "科目期初导入" + new DZFDate() + ".xls";
                excelData = gl_qcyeserv.exportExcel(SystemUtil.getLoginCorpId(), showQuantity);
            }

            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + formattedName);
            response.setContentType("application/vnd.ms-excel");
            toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(excelData);
            toClient.flush();
            response.getOutputStream().flush();

        } catch (IOException e) {
            log.error("excel导出错误",e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误",e);
            }
            try {
                if(response!=null && response.getOutputStream() != null){
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误",e);
            }
        }
    }

    @PostMapping("importExcel")
    public ReturnData importExcel(@RequestParam("impfile") MultipartFile file,  HttpServletRequest request) {
        Json json = new Json();
        String isFzqc = request.getParameter("fzqc");
        String pk_km = request.getParameter("pk_km");
//        File[] infiles = ((MultiPartRequestWrapper) getRequest()).getFiles("impfile");
//        String[] fileNames = ((MultiPartRequestWrapper) getRequest()).getFileNames("impfile");
        if (file == null) {
            throw new BusinessException("请选择要导入的文件");
        }
        try {
            CorpVO corp = SystemUtil.getLoginCorpVo();
            DZFDate jzDate = corp.getBegindate();
            int year = jzDate.getYear();
            List<QmclVO> qmcls = qmgzService.yearhasGz(corp.getPk_corp(),
                    String.valueOf(year));
            for (QmclVO qmvo : qmcls) {
                if (qmvo.getIsgz().booleanValue()) {
                    throw new BusinessException("导入失败，已经存在关账的月份");
                }
            }
            jzDate = DZFDate.getDate(DateUtils.getPeriod(jzDate) + "-01");
            if ("Y".equals(isFzqc)) {
                gl_qcyeserv.processFzImportExcel(corp, SystemUtil.getLoginUserId(), pk_km, jzDate, file.getInputStream());
            } else {
                gl_qcyeserv.processImportExcel(corp.getPk_corp(), jzDate,  file.getInputStream());
            }
            json.setSuccess(true);
            json.setMsg("导入成功");
        } catch (Exception e) {
            printErrorLog(json, e, "导入失败");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("queryCur")
    public ReturnData<QcYeCurJson> queryCur() {
        QcYeCurJson js = new QcYeCurJson();
        try {
            String corpid = SystemUtil.getLoginCorpId();
            QcYeCurrency[] vos = gl_qcyeserv.queryCur(corpid);
            for (QcYeCurrency c : vos) {
                if ("人民币".equals(c.getCurrencyname())) {
                    js.setDefaultvalue(c.getPk_currency());
                    break;
                }
            }
            js.setSuccess(true);
            js.setMsg("查询当前公司外币成功!");
            js.setRows(vos);
        } catch (Exception e) {
            js.setSuccess(false);
            js.setMsg("查询当前公司外币失败!");
        }
        return ReturnData.ok().data(js);
    }

    @GetMapping("queryCurByPkCorp")
    public ReturnData<QcYeCurJson> queryCurByPkCorp(String pk_corp) {
        QcYeCurJson js = new QcYeCurJson();
        try {
            QcYeCurrency[] vos = gl_qcyeserv.queryCur(pk_corp);
            for (QcYeCurrency c : vos) {
                if ("人民币".equals(c.getCurrencyname())) {
                    js.setDefaultvalue(c.getPk_currency());
                    break;
                }
            }
            js.setSuccess(true);
            js.setMsg("查询当前公司外币成功!");
            js.setRows(vos);
        } catch (Exception e) {
            js.setSuccess(false);
            js.setMsg("查询当前公司外币失败!");
        }
        return ReturnData.ok().data(js);
    }
}

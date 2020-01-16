package com.dzf.zxkj.platform.controller.taxrpt;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.excel.util.ExportExcel;
import com.dzf.zxkj.jackson.annotation.MultiRequestBody;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.workbench.*;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.IbsWorkbenchService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.vo.WorkBenchExportVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

@RestController
@RequestMapping("workbench")
@SuppressWarnings("all")
@Slf4j
@ConditionalOnBean(FastDfsUtil.class)
public class BsWorkbenchController extends BaseController {
    @Autowired
    private IbsWorkbenchService bs_workbenchserv;

    @Autowired
    private IUserService userser;

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private ICorpService corpService;

    @Autowired
    private FastDfsUtil  fastDfsUtil;

    /**
     * 查询所有客户的数据
     */
    @PostMapping("query")
    public ReturnData query(@MultiRequestBody UserVO uservo, @MultiRequestBody CorpVO corpVO, @MultiRequestBody QueryParamVO paramvo) {
        Grid grid = new Grid();
        try {
            if (paramvo.getLevelq() == null) {
                paramvo.setLevelq(-1);
            }
            if (StringUtil.isEmpty(paramvo.getFathercorp())) {
                paramvo.setFathercorp(getLoginFcorp(corpVO));
            }
            CorpVO fcorpvo = corpService.queryByPk(paramvo.getFathercorp());
            List<BsWorkbenchVO> list = bs_workbenchserv.query(paramvo, uservo, null, fcorpvo);
            int page = paramvo == null ? 1 : paramvo.getPage();
            int rows = paramvo == null ? 10000 : paramvo.getRows();
            int len = list == null ? 0 : list.size();
            if (len > 0) {
                grid.setTotal((long) (len));
                BsWorkbenchVO[] vos = list.toArray(new BsWorkbenchVO[0]);
                vos = getPagedVOs(vos, page, rows);
                grid.setRows(Arrays.asList(vos));
            } else {
                grid.setTotal(Long.valueOf(0));
                grid.setRows(new ArrayList<BsWorkbenchVO>());
            }

            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 将查询后的结果分页
     *
     * @param cvos
     * @param page
     * @param rows
     * @return
     * @throws DZFWarpException
     */
    private BsWorkbenchVO[] getPagedVOs(BsWorkbenchVO[] cvos, int page, int rows) throws DZFWarpException {
        int beginIndex = rows * (page - 1);
        int endIndex = rows * page;
        if (endIndex >= cvos.length) {//防止endIndex数组越界
            endIndex = cvos.length;
        }
        cvos = Arrays.copyOfRange(cvos, beginIndex, endIndex);
        return cvos;
    }

    /**
     * 查询单个客户的数据
     */
    @PostMapping("queryByCorp")
    public ReturnData queryByCorp(@MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO cuserVO, @MultiRequestBody QueryParamVO paramvo) {
        Grid json = new Grid();
        try {
            if (paramvo.getLevelq() == null) {
                paramvo.setLevelq(-1);
            }
            String year = paramvo.getQjq();
            String pk_corp = paramvo.getPk_corp();
            if (StringUtil.isEmpty(pk_corp)) {
                throw new BusinessException("客户信息不能为空！");
            }
            checkCorp(pk_corp, cuserVO, corpVO);
            Map<String, BsWorkbenchVO> voMap = bs_workbenchserv.queryByCorp(paramvo);
            List<BsWorkbenchVO> retlist = new ArrayList<BsWorkbenchVO>();
            CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
            int month = 0;
            String period = null;
            BsWorkbenchVO vo = null;
            String uname = null;
            String chargedeptname = "";
            if (corpvo != null) {
                CorpVO fcorpvo = corpService.queryByPk(getLoginFcorp(corpVO));
                if (fcorpvo != null && fcorpvo.getIschannel() == null || !fcorpvo.getIschannel().booleanValue()) {
                    UserVO userVO = userser.queryUserJmVOByID(corpvo.getVsuperaccount());
                    if (userVO != null) {
                        uname = userVO.getUser_name();
                    }
                }
                chargedeptname = corpvo.getChargedeptname();
            }
            String unitname = CodeUtils1.deCode(corpvo.getUnitname());
            for (int i = 0; i < 12; i++) {
                month = i + 1;
                period = year + "-" + (month < 10 ? "0" + month : month);
                vo = voMap.get(period);
                if (vo == null) {
                    vo = getWorkBenchVO(period, pk_corp);
                }
                vo.setChargedeptname(chargedeptname);
                vo.setPcountname(uname);
                vo.setBegindate(corpvo.getBegindate());
                vo.setKhCode(corpvo.getInnercode());
                vo.setKhName(unitname);
                retlist.add(vo);
            }
            json.setRows(retlist);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败！");
            log.error("查询失败！", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 获取返回信息
     *
     * @param period
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    private BsWorkbenchVO getWorkBenchVO(String period, String pk_corp) throws DZFWarpException {
        BsWorkbenchVO vo = new BsWorkbenchVO();
        vo.setPeriod(period);
        vo.setPk_corp(pk_corp);
        vo.setIsptx(0);
        vo.setIacctcheck(0);
        vo.setTaxStateCopy(0);
        vo.setTaxStateFinish(0);
        vo.setTaxStateClean(0);
        vo.setIpzjjzt(0);
        vo.setIsZeroDeclare(DZFBoolean.FALSE);
        vo.setErningStatus(0);
        vo.setAddStatus(0);
        vo.setExciseStatus(0);
        vo.setIncomeStatus(0);
        vo.setCulturalStatus(0);
        vo.setAdditionalStatus(0);
        vo.setCityStatus(0);
        vo.setEducaStatus(0);
        vo.setLocalEducaStatus(0);
        vo.setPersonStatus(0);
        vo.setStampStatus(0);
        return vo;
    }

    /**
     * 保存
     */
    @PostMapping("save")
    public ReturnData save(@MultiRequestBody BsWorkbenchVO[] bsVOs, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (bsVOs != null && bsVOs.length > 0) {
                Set<String> corpSet = bs_workbenchserv.queryPowerCorpSet(userVO, getLoginFcorp(corpVO));
                List<BsWorkbenchVO> retlist = new ArrayList<BsWorkbenchVO>();
                StringBuffer errmsg = new StringBuffer();
                StringBuffer msg = new StringBuffer();
                int rignum = 0;
                int errnum = 0;
                if (bsVOs != null && bsVOs.length > 0) {
                    CorpVO corpvo = null;
                    for (BsWorkbenchVO bsvo : bsVOs) {
                        try {
                            if(StringUtils.isBlank(bsvo.getPeriod())){
                                continue;
                            }
                            if (!corpSet.contains(bsvo.getPk_corp())) {
                                corpvo = corpService.queryByPk(bsvo.getPk_corp());
                                if (corpvo != null) {
                                    msg.append("客户：").append(corpvo.getUnitname());
                                }
                                msg.append("无权操作；");
                                throw new BusinessException(msg.toString());
                            }
                            bsvo.setCoperatorid(userVO.getCuserid());
                            bsvo.setFathercorp(getLoginFcorp(corpVO));
                            bsvo = bs_workbenchserv.save(bsvo, null, userVO);
                            retlist.add(bsvo);
                            rignum++;
                        } catch (Exception e) {
                            errmsg.append(e.getMessage());
                            errnum++;
                        }
                    }
                }
                json.setSuccess(true);
                if (rignum > 0 && rignum == bsVOs.length) {
                    json.setRows(retlist);
                    json.setMsg("保存成功" + rignum + "条");
                } else if (errnum > 0) {
                    json.setMsg("保存成功" + rignum + "条，保存失败" + errnum + "条，失败原因：" + errmsg.toString());
                    json.setStatus(-1);
                    if (rignum > 0) {
                        json.setRows(retlist);
                    }
                }
                if (rignum > 0) {
                    writeLogRecord(LogRecordEnum.OPE_KJ_NSGZT, "修改纳税信息", ISysConstants.SYS_2);
                }
            }
        } catch (Exception e) {
            printErrorLog(json, e, "保存失败");
            log.error("保存失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 取数
     */
    @PostMapping("fetchData")
    public ReturnData fetchData(@MultiRequestBody String period, @MultiRequestBody UserVO userVO, @MultiRequestBody String pk_corpks, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (StringUtil.isEmpty(pk_corpks)) {
                throw new BusinessException("取数客户信息不能为空");
            }
            String[] corpks = pk_corpks.split(",");
            Set<String> corpSet = bs_workbenchserv.queryPowerCorpSet(userVO, getLoginFcorp(corpVO));
            StringBuffer msg = new StringBuffer();
            CorpVO corpvo = null;
            for (String pk : corpks) {
                if (!corpSet.contains(pk)) {
                    corpvo = corpService.queryByPk(pk);
                    if (corpvo != null) {
                        msg.append("客户：").append(corpvo.getUnitname());
                    }
                    msg.append("无权操作；");
                }
            }
            if (msg != null && msg.length() > 0) {
                throw new BusinessException(msg.toString());
            }
            bs_workbenchserv.saveFetchData(getLoginFcorp(corpVO), userVO, period, corpks);
            List<BsWorkbenchVO> uplist = bs_workbenchserv.getTaxDeclare(getLoginFcorp(corpVO), userVO, period, corpks);
            if (uplist != null && uplist.size() > 0) {
                bs_workbenchserv.updateTaxDeclare(uplist);
            }
            json.setSuccess(true);
            json.setMsg("取数成功");
            writeLogRecord(LogRecordEnum.OPE_KJ_NSGZT, "读取" + period + "纳税信息", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json, e, "取数失败！");
            log.error("取数失败！", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 权限校验
     *
     * @param pk_corp
     */
    private void checkCorp(String pk_corp, UserVO userVO, CorpVO corpVO) {
        Set<String> corpSet = bs_workbenchserv.queryPowerCorpSet(userVO, getLoginFcorp(corpVO));
        if (!corpSet.contains(pk_corp)) {
            throw new BusinessException("无权操作！");
        }
    }

    /**
     * 提醒
     */
    @PostMapping("saveRemind")
    public ReturnData saveRemind(@MultiRequestBody BsWorkbenchVO[] bsVOs, @MultiRequestBody String msgtype, @MultiRequestBody String qj, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (StringUtil.isEmpty(msgtype)) {
                throw new BusinessException("提醒类型不能为空");
            }

            Set<String> corpSet = bs_workbenchserv.queryPowerCorpSet(userVO, getLoginFcorp(corpVO));
            //获取发送提醒的客户
            int rignum = 0;
            int errnum = 0;
            StringBuffer errmsg = new StringBuffer();
            String fathercprp = "";
            String pk_corp = "";
            for (BsWorkbenchVO bvo : bsVOs) {
                try {
                    pk_corp = bvo.getPk_corp();
                    if (!corpSet.contains(pk_corp)) {
                        throw new BusinessException("无权操作；");
                    }
                    fathercprp = bvo.getFathercorp();
                    if (StringUtil.isEmpty(fathercprp)) {
                        fathercprp = getLoginFcorp(corpVO);
                    }
                    bs_workbenchserv.saveRemindMsg(fathercprp, pk_corp, msgtype, userVO, qj);
                    rignum++;
                } catch (Exception e) {
                    errnum++;
                    errmsg.append("客户【").append(bvo.getKhName()).append("】");
                    errmsg.append(e.getMessage()).append("<br>");
                }
            }
            if (errnum > 0) {
                json.setMsg("成功" + rignum + "条，失败" + errnum + "条，失败原因：" + errmsg.toString());
                json.setStatus(-1);
            } else {
                json.setMsg("成功" + rignum + "条");
            }

            json.setSuccess(true);
            json.setRows(0);
            writeLogRecord(LogRecordEnum.OPE_ADMIN_NSGZT, "修改提醒设置", ISysConstants.SYS_2);
        } catch (Exception e) {
            printErrorLog(json, e, "提醒设置失败");
            log.error("提醒设置失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 提醒历史
     */
    @PostMapping("queryHistory")
    public ReturnData queryHistory(@MultiRequestBody String corpid, @MultiRequestBody String qj, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            checkCorp(corpid, userVO, corpVO);
            String pk_corp = getLoginFcorp(corpVO);
            CorpMsgVO[] vos = bs_workbenchserv.queryMsgAdminVO(pk_corp, corpid, qj);
            json.setRows(vos);
            json.setSuccess(true);
            json.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(json, e, "查询送票提醒历史失败");
            log.error("查询送票提醒历史失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 导出excel
     */
    @PostMapping("export/excel")
    public void export(@MultiRequestBody WorkBenchExportVo workBenchExportVo, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        // 获取需要导出数据
        if (StringUtil.isEmpty(workBenchExportVo.getStrlist())) {
            return;
        }

        JSONArray array = (JSONArray) JSON.parseArray(workBenchExportVo.getStrlist());
        Map<String, Object[]> map = null;
        CorpVO fcorpvo = corpService.queryByPk(getLoginFcorp(corpVO));
        if ("true".equals(workBenchExportVo.getIsSingle())) {
            if (fcorpvo != null && fcorpvo.getIschannel() != null && fcorpvo.getIschannel().booleanValue()) {
                map = getJmSingleExportList();
            } else {
                map = getQjSingleExportList();
            }
        } else {
            if (fcorpvo != null) {
                workBenchExportVo.setCorpname(fcorpvo.getUnitname());
            }
            if (fcorpvo != null && fcorpvo.getIschannel() != null && fcorpvo.getIschannel().booleanValue()) {
                map = getJmExportList();
            } else {
                map = getQjExportList();
            }
        }
        String[] exptitls = (String[]) map.get("exptitls");
        String[] expfieids = (String[]) map.get("expfieids");
        String[] hbltitls = (String[]) map.get("hbltitls");
        Integer[] hblindexs = (Integer[]) map.get("hblindexs");
        String[] hbhtitls = (String[]) map.get("hbhtitls");
        Integer[] hbhindexs = (Integer[]) map.get("hbhindexs");

        List<String> strslist = Arrays.asList((String[]) map.get("strStrs"));
        List<String> mnylist = Arrays.asList((String[]) map.get("mnyStrs"));
        List<String> stalist = Arrays.asList((String[]) map.get("stateStrs"));
        List<String> taxlist = Arrays.asList((String[]) map.get("taxStrs"));

        ExportExcel<BsWorkbenchVO> ex = new ExportExcel<BsWorkbenchVO>();
        ServletOutputStream servletOutputStream = null;
        OutputStream toClient = null;
        try {
            response.reset();
            String date = DateUtils.getDate(new Date());
            String fileName = null;
            response.addHeader("Content-Disposition", "attachment;filename=" + fileName
                    + new String(date + ".xls"));
            servletOutputStream = response.getOutputStream();
            toClient = new BufferedOutputStream(servletOutputStream);
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            byte[] length = ex.expBsWorkbenchExcel("纳税工作台", exptitls, expfieids, hbltitls, hblindexs, hbhtitls,
                    hbhindexs, array, toClient, "", strslist, mnylist, stalist, taxlist, workBenchExportVo.getCorpname(), workBenchExportVo.getPeriod());
        } catch (IOException e) {
            log.error("错误", e);
        } finally {
            if (toClient != null) {
                try {
                    toClient.flush();
                    toClient.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
            if (servletOutputStream != null) {
                try {
                    servletOutputStream.flush();
                    servletOutputStream.close();
                } catch (IOException e) {
                    log.error("错误", e);
                }
            }
        }
        writeLogRecord(LogRecordEnum.OPE_KJ_NSGZT, "导出" + workBenchExportVo.getPeriod() + "纳税信息", ISysConstants.SYS_2);
    }

    /**
     * 获取导出字段的相关信息（加盟商）
     *
     * @return
     */
    private Map<String, Object[]> getJmExportList() {
        // 1、导出字段名称
        String[] exptitls = new String[]{"客户编码", "客户名称", "纳税人资格",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "财报",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴合计", "实缴合计", "备注"};
        // 2、导出字段编码
        String[] expfieids = new String[]{"khbm", "khmc", "chname",
                "spzt", "accheck", "cszt", "taxconsta", "wczt", "qkzt", "pzjjzt", "iszerodec", "income",
                "zzs", "zzspaid", "zzsstat", "addittax", "addittaxpaid", "addittaxstat",
                "cjs", "cjspaid", "cjsstat", "jyffj", "jyffjpaid", "jyffjstat",
                "dfjyf", "dfjyfpaid", "dfjyfstat", "erstat",
                "sds", "sdspaid", "sdsstat", "xfs", "xfspaid", "xfsstat", "cultax", "cultaxpaid", "cultaxstat",
                "stamptax", "stamppaidtax", "stampstat", "grsds", "grsdspaid", "grsdsstat", "paymny", "paidmny",
                "memo"};
        // 3、合并列字段名称
        String[] hbltitls = new String[]{"增值税", "附加税合计", "城建税", "教育费附加", "地方教育费附加", "企业所得税", "消费税",
                "文化事业建设费", "印花税", "个人所得税"};
        Integer[] hblindexs = new Integer[]{12, 15, 18, 21, 24, 27, 31, 34, 37, 40};
        // 5、合并行字段名称
        String[] hbhtitls = new String[]{"客户编码", "客户名称", "纳税人资格",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "财报", "应缴合计", "实缴合计", "备注"};
        // 6、合并行字段下标
        Integer[] hbhindexs = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 30, 43, 44, 45};
        // 7、字符集合
        String[] strStrs = new String[]{"khbm", "khmc", "chname", "taxconsta", "memo",};
        // 8、金额集合
        String[] mnyStrs = new String[]{"income", "zzs", "zzspaid", "xfs", "xfspaid", "sds", "sdspaid", "cultax",
                "cultaxpaid", "addittax", "addittaxpaid", "cjs", "cjspaid", "jyffj", "jyffjpaid", "dfjyf", "dfjyfpaid",
                "grsds", "grsdspaid", "stamptax", "stamppaidtax", "paymny", "paidmny"};
        // 9、状态常量集合
        String[] stateStrs = new String[]{"iszerodec"};
        // 10、票税常量集合
        String[] taxStrs = new String[]{"accheck", "spzt", "cszt", "wczt", "qkzt", "pzjjzt", "stampstat",
                "zzsstat", "xfsstat", "sdsstat", "erstat", "cultaxstat", "addittaxstat",
                "cjsstat", "jyffjstat", "dfjyfstat", "grsdsstat"};

        Map<String, Object[]> map = new HashMap<String, Object[]>();
        map.put("exptitls", exptitls);
        map.put("expfieids", expfieids);
        map.put("hbltitls", hbltitls);
        map.put("hblindexs", hblindexs);
        map.put("hbhtitls", hbhtitls);
        map.put("hbhindexs", hbhindexs);

        map.put("strStrs", strStrs);
        map.put("mnyStrs", mnyStrs);
        map.put("stateStrs", stateStrs);
        map.put("taxStrs", taxStrs);
        return map;
    }

    /**
     * 获取导出字段的相关信息（旗舰版）
     *
     * @return
     */
    private Map<String, Object[]> getQjExportList() {
        // 1、导出字段名称
        String[] exptitls = new String[]{"客户编码", "客户名称", "纳税人资格", "主办会计",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "财报",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴合计", "实缴合计", "备注"};
        // 2、导出字段编码
        String[] expfieids = new String[]{"khbm", "khmc", "chname", "pcount",
                "spzt", "accheck", "cszt", "taxconsta", "wczt", "qkzt", "pzjjzt", "iszerodec", "income",
                "zzs", "zzspaid", "zzsstat", "addittax", "addittaxpaid", "addittaxstat",
                "cjs", "cjspaid", "cjsstat", "jyffj", "jyffjpaid", "jyffjstat",
                "dfjyf", "dfjyfpaid", "dfjyfstat", "erstat",
                "sds", "sdspaid", "sdsstat", "xfs", "xfspaid", "xfsstat", "cultax", "cultaxpaid", "cultaxstat",
                "stamptax", "stamppaidtax", "stampstat", "grsds", "grsdspaid", "grsdsstat", "paymny", "paidmny",
                "memo"};
        // 3、合并列字段名称
        String[] hbltitls = new String[]{"增值税", "附加税合计", "城建税", "教育费附加", "地方教育费附加", "企业所得税", "消费税",
                "文化事业建设费", "印花税", "个人所得税"};
        Integer[] hblindexs = new Integer[]{13, 16, 19, 22, 25, 28, 32, 35, 38, 41};
        // 5、合并行字段名称
        String[] hbhtitls = new String[]{"客户编码", "客户名称", "纳税人资格", "主办会计",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "财报", "应缴合计", "实缴合计", "备注"};
        // 6、合并行字段下标
        Integer[] hbhindexs = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 31, 44, 45, 46};
        // 7、字符集合
        String[] strStrs = new String[]{"khbm", "khmc", "chname", "pcount", "taxconsta", "memo",};
        // 8、金额集合
        String[] mnyStrs = new String[]{"income", "zzs", "zzspaid", "xfs", "xfspaid", "sds", "sdspaid", "cultax",
                "cultaxpaid", "addittax", "addittaxpaid", "cjs", "cjspaid", "jyffj", "jyffjpaid", "dfjyf", "dfjyfpaid",
                "grsds", "grsdspaid", "stamptax", "stamppaidtax", "paymny", "paidmny"};
        // 9、状态常量集合
        String[] stateStrs = new String[]{"iszerodec"};
        // 10、票税常量集合
        String[] taxStrs = new String[]{"accheck", "spzt", "cszt", "wczt", "qkzt", "pzjjzt", "stampstat",
                "zzsstat", "xfsstat", "sdsstat", "erstat", "cultaxstat", "addittaxstat",
                "cjsstat", "jyffjstat", "dfjyfstat", "grsdsstat"};

        Map<String, Object[]> map = new HashMap<String, Object[]>();
        map.put("exptitls", exptitls);
        map.put("expfieids", expfieids);
        map.put("hbltitls", hbltitls);
        map.put("hblindexs", hblindexs);
        map.put("hbhtitls", hbhtitls);
        map.put("hbhindexs", hbhindexs);

        map.put("strStrs", strStrs);
        map.put("mnyStrs", mnyStrs);
        map.put("stateStrs", stateStrs);
        map.put("taxStrs", taxStrs);
        return map;
    }

    /**
     * 获取单个客户导出相关字段（加盟商）
     *
     * @return
     */
    private Map<String, Object[]> getJmSingleExportList() {
        // 1、导出字段名称
        String[] exptitls = new String[]{"月份", "纳税人资格",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "财报",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴合计", "实缴合计", "备注"};
        // 2、导出字段编码
        String[] expfieids = new String[]{"period", "chname",
                "spzt", "accheck", "cszt", "taxconsta", "wczt", "qkzt", "pzjjzt", "iszerodec", "income",
                "zzs", "zzspaid", "zzsstat", "addittax", "addittaxpaid", "addittaxstat",
                "cjs", "cjspaid", "cjsstat", "jyffj", "jyffjpaid", "jyffjstat",
                "dfjyf", "dfjyfpaid", "dfjyfstat", "erstat",
                "sds", "sdspaid", "sdsstat", "xfs", "xfspaid", "xfsstat", "cultax", "cultaxpaid", "cultaxstat",
                "stamptax", "stamppaidtax", "stampstat", "grsds", "grsdspaid", "grsdsstat", "paymny", "paidmny",
                "memo"};
        // 3、合并列字段名称
        String[] hbltitls = new String[]{"增值税", "附加税合计", "城建税", "教育费附加", "地方教育费附加", "企业所得税", "消费税",
                "文化事业建设费", "印花税", "个人所得税"};
        Integer[] hblindexs = new Integer[]{11, 14, 17, 20, 23, 26, 30, 33, 36, 39};
        // 5、合并行字段名称
        String[] hbhtitls = new String[]{"月份", "纳税人资格",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "财报", "应缴合计", "实缴合计", "备注"};
        // 6、合并行字段下标
        Integer[] hbhindexs = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 29, 42, 43, 44};
        // 7、字符集合
        String[] strStrs = new String[]{"period", "chname", "pcount", "memo",};
        // 8、金额集合
        String[] mnyStrs = new String[]{"income", "zzs", "zzspaid", "xfs", "xfspaid", "sds", "sdspaid", "cultax",
                "cultaxpaid", "addittax", "addittaxpaid", "cjs", "cjspaid", "jyffj", "jyffjpaid", "dfjyf", "dfjyfpaid",
                "grsds", "grsdspaid", "stamptax", "stamppaidtax", "paymny", "paidmny"};
        // 9、状态常量集合
        String[] stateStrs = new String[]{"iszerodec"};
        // 10、票税常量集合
        String[] taxStrs = new String[]{"accheck", "spzt", "cszt", "wczt", "qkzt", "pzjjzt", "zzsstat", "xfsstat", "sdsstat", "erstat", "cultaxstat", "addittaxstat",
                "cjsstat", "jyffjstat", "dfjyfstat", "grsdsstat", "stampstat"};

        Map<String, Object[]> map = new HashMap<String, Object[]>();
        map.put("exptitls", exptitls);
        map.put("expfieids", expfieids);
        map.put("hbltitls", hbltitls);
        map.put("hblindexs", hblindexs);
        map.put("hbhtitls", hbhtitls);
        map.put("hbhindexs", hbhindexs);

        map.put("strStrs", strStrs);
        map.put("mnyStrs", mnyStrs);
        map.put("stateStrs", stateStrs);
        map.put("taxStrs", taxStrs);
        return map;
    }

    /**
     * 获取附件列表
     */
    @PostMapping("getAttaches")
    public ReturnData getAttaches(@MultiRequestBody BsWorkDocVO bsWorkDocVO, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
//            bsWorkDocVO.setPk_corp(corpVO.getPk_corp());
            if (StringUtil.isEmpty(bsWorkDocVO.getFathercorp())) {
                bsWorkDocVO.setFathercorp(corpVO.getFathercorp());
            }
            List<BsWorkDocVO> list = bs_workbenchserv.getAttatches(bsWorkDocVO);
            list.stream().forEach(v -> {
                String fileName = v.getDocname();
                String type = fileName.substring(fileName.lastIndexOf('.')+1, fileName.length());
                v.setExt(type);
                if(!StringUtils.equalsAnyIgnoreCase(type, "xlsx", "xls", "pdf","pptx","txt","docx","zip")){
                    try {
                        v.setImgae(fastDfsUtil.downFile(v.getVfilepath()));
                    } catch (AppException e) {
                        log.error("预览图片异常", e);
                    }
                }
            });
            json.setRows(list);
            json.setSuccess(true);
            json.setMsg("获取附件成功");
        } catch (Exception e) {
            printErrorLog(json, e, "获取附件失败");
        }
        return ReturnData.ok().data(json);
    }
    /**
     * 获取单个客户导出相关字段（旗舰版）
     *
     * @return
     */
    private Map<String, Object[]> getQjSingleExportList() {
        // 1、导出字段名称
        String[] exptitls = new String[]{"月份", "纳税人资格", "主办会计",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "财报",
                "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态", "应缴", "实缴", "申报状态",
                "应缴", "实缴", "申报状态", "应缴合计", "实缴合计", "备注"};
        // 2、导出字段编码
        String[] expfieids = new String[]{"period", "chname", "pcount",
                "spzt", "accheck", "cszt", "taxconsta", "wczt", "qkzt", "pzjjzt", "iszerodec", "income",
                "zzs", "zzspaid", "zzsstat", "addittax", "addittaxpaid", "addittaxstat",
                "cjs", "cjspaid", "cjsstat", "jyffj", "jyffjpaid", "jyffjstat",
                "dfjyf", "dfjyfpaid", "dfjyfstat", "erstat",
                "sds", "sdspaid", "sdsstat", "xfs", "xfspaid", "xfsstat", "cultax", "cultaxpaid", "cultaxstat",
                "stamptax", "stamppaidtax", "stampstat", "grsds", "grsdspaid", "grsdsstat", "paymny", "paidmny",
                "memo"};
        // 3、合并列字段名称
        String[] hbltitls = new String[]{"增值税", "附加税合计", "城建税", "教育费附加", "地方教育费附加", "企业所得税", "消费税",
                "文化事业建设费", "印花税", "个人所得税"};
        Integer[] hblindexs = new Integer[]{12, 15, 18, 21, 24, 27, 31, 34, 37, 40};
        // 5、合并行字段名称
        String[] hbhtitls = new String[]{"月份", "纳税人资格", "主办会计",
                "送票", "关账", "抄税", "税款确认状态", "申报状态", "可清卡", "凭证交接", "零申报", "收入",
                "财报", "应缴合计", "实缴合计", "备注"};
        // 6、合并行字段下标
        Integer[] hbhindexs = new Integer[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 30, 43, 44, 45};
        // 7、字符集合
        String[] strStrs = new String[]{"period", "chname", "pcount", "memo",};
        // 8、金额集合
        String[] mnyStrs = new String[]{"income", "zzs", "zzspaid", "xfs", "xfspaid", "sds", "sdspaid", "cultax",
                "cultaxpaid", "addittax", "addittaxpaid", "cjs", "cjspaid", "jyffj", "jyffjpaid", "dfjyf", "dfjyfpaid",
                "grsds", "grsdspaid", "stamptax", "stamppaidtax", "paymny", "paidmny"};
        // 9、状态常量集合
        String[] stateStrs = new String[]{"iszerodec"};
        // 10、票税常量集合
        String[] taxStrs = new String[]{"accheck", "spzt", "cszt", "wczt", "qkzt", "pzjjzt", "zzsstat", "xfsstat", "sdsstat", "erstat", "cultaxstat", "addittaxstat",
                "cjsstat", "jyffjstat", "dfjyfstat", "grsdsstat", "stampstat"};

        Map<String, Object[]> map = new HashMap<String, Object[]>();
        map.put("exptitls", exptitls);
        map.put("expfieids", expfieids);
        map.put("hbltitls", hbltitls);
        map.put("hblindexs", hblindexs);
        map.put("hbhtitls", hbhtitls);
        map.put("hbhindexs", hbhindexs);

        map.put("strStrs", strStrs);
        map.put("mnyStrs", mnyStrs);
        map.put("stateStrs", stateStrs);
        map.put("taxStrs", taxStrs);
        return map;
    }

    /**
     * 提醒设置
     */
    @PostMapping("sendReminSet")
    public ReturnData sendReminSet(@MultiRequestBody BsWorkbenchVO[] bsVOs, @MultiRequestBody RemindSetVO[] remVOs, @MultiRequestBody CorpVO corpVO, @MultiRequestBody UserVO userVO) {
        Json json = new Json();
        try {
            Map<Integer, RemindSetVO> remap = new HashMap<Integer, RemindSetVO>();
            for (RemindSetVO remvo : remVOs) {
                remap.put(remvo.getIremindtype(), remvo);
            }
            StringBuffer retmsg = new StringBuffer();
            StringBuffer errmsg = new StringBuffer();
            CorpVO corpvo = null;
            int rignum = 0;
            Set<String> corpSet = bs_workbenchserv.queryPowerCorpSet(userVO, getLoginFcorp(corpVO));
            for (BsWorkbenchVO bsvo : bsVOs) {
                try {
                    if (!corpSet.contains(bsvo.getPk_corp())) {
                        throw new BusinessException("无权操作");
                    }
                    bs_workbenchserv.saveRemindSet(bsvo, remVOs, remap, getLoginFcorp(corpVO), SystemUtil.getLoginUserId());
                    rignum++;
                } catch (Exception e) {
                    corpvo = corpService.queryByPk(bsvo.getPk_corp());
                    if (corpvo != null) {
                        errmsg.append(corpvo.getUnitname()).append("、");
                    }
                }
            }
            retmsg.append("成功设置").append(rignum).append("个客户。");
            if (errmsg != null && errmsg.length() > 0) {
                json.setStatus(-1);
                retmsg.append("其中").append(errmsg.substring(0, errmsg.length() - 1)).append("设置失败");
            }
            json.setSuccess(true);
            json.setMsg(retmsg.toString());
        } catch (Exception e) {
            printErrorLog(json, e, "提醒设置失败");
            log.error("提醒设置失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 删除附件
     */
    @PostMapping("delAttaches")
    public ReturnData delAttaches(@MultiRequestBody String[]  delData, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (delData == null || delData.length == 0) {
                throw new BusinessException("没有选择附件，请确认！");
            }
            bs_workbenchserv.delAttaches(corpVO.getFathercorp(), delData);
            json.setSuccess(true);
            json.setMsg("删除附件成功");
        } catch (Exception e) {
            printErrorLog(json, e, "删除附件失败");
            log.error("删除失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 查询期间的数据
     */
    @PostMapping("qryRemindSet")
    public ReturnData qryRemindSet(@MultiRequestBody BsWorkbenchVO[] bsVOs, @MultiRequestBody UserVO userVO, @MultiRequestBody CorpVO corpVO) {
        Grid grid = new Grid();
        try {
            Set<String> corpSet = bs_workbenchserv.queryPowerCorpSet(userVO, getLoginFcorp(corpVO));
            StringBuffer msg = new StringBuffer();
            CorpVO corpvo = null;
            if (!corpSet.contains(bsVOs[0].getPk_corp())) {
                corpvo = corpService.queryByPk(bsVOs[0].getPk_corp());
                if (corpvo != null) {
                    msg.append("客户：").append(corpvo.getUnitname());
                }
                msg.append("无权操作；");
                throw new BusinessException(msg.toString());
            }
            RemindSetVO[] retVOs = bs_workbenchserv.qryRemSet(bsVOs[0], getLoginFcorp(corpVO));
            if (retVOs != null && retVOs.length > 0) {
                grid.setRows(Arrays.asList(retVOs));
            } else {
                grid.setRows(new ArrayList<RemindSetVO>());
            }
            grid.setSuccess(true);
            grid.setMsg("查询成功");
        } catch (Exception e) {
            printErrorLog(grid, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取当前客户所属的会计公司
     *
     * @return
     * @throws DZFWarpException
     */
    private String getLoginFcorp(CorpVO corpvo) throws DZFWarpException {
        if (corpvo != null) {
            return corpvo.getFathercorp();
        } else {
            throw new BusinessException("会计公司信息不能为空");
        }
    }

    /**
     * 查询主办会计
     *
     * @throws Exception
     */
    @PostMapping("queryUser")
    public ReturnData queryUser(@MultiRequestBody String rolecode, @MultiRequestBody CorpVO corpVO) throws Exception {
        Grid grid = new Grid();
        try {
            ;
            UserVO[] resvos = bs_workbenchserv.queryUser(getLoginFcorp(corpVO), rolecode);
            resvos = (UserVO[]) QueryDeCodeUtils.decKeyUtils(new String[]{"user_name"}, resvos, 1);
            grid.setMsg("查询成功!");
            grid.setRows(Arrays.asList(resvos));
            grid.setTotal((long) resvos.length);
            grid.setSuccess(true);
        } catch (Exception e) {
            grid.setRows(new ArrayList<UserVO>());
            grid.setTotal((long) 0);
            printErrorLog(grid, e, "查询失败！");
            log.error("查询失败！", e);
        }
        return ReturnData.ok().data(grid);
    }

    /**
     * 获取财务进度
     */
    @GetMapping("getFinanceProgress")
    public ReturnData getFinanceProgress(String period) {
        Json json = new Json();
        try {
            if (period == null) {
                period = SystemUtil.getLoginDate().substring(0, 7);
            }
            BsWorkbenchVO vo = bs_workbenchserv.getFinanceProgress(SystemUtil.getLoginCorpId(), period);
            json.setData(vo);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询财务进度失败");
            log.error("查询财务进度失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 更新财务进度
     */
    @PostMapping("updateFinanceProgress")
    public ReturnData updateFinanceProgress(@MultiRequestBody String period, @MultiRequestBody String field, @MultiRequestBody String status) {
        Json json = new Json();
        try {
            if (period == null) {
                period = SystemUtil.getLoginDate().substring(0, 7);
            }
            bs_workbenchserv.updateFinanceProgress(SystemUtil.getLoginCorpId(), period,
                    field, Integer.valueOf(status));
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "更新财务进度失败");
            log.error("更新财务进度失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 查询
     */
    @RequestMapping("queryCol")
    public ReturnData queryCol(@MultiRequestBody ColumnSetupVO pamvo, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (pamvo == null) {
                pamvo = new ColumnSetupVO();
            }
            setDefaultValue(pamvo, corpVO);
            ColumnSetupVO retvo = bs_workbenchserv.queryCol(pamvo);
            json.setRows(retvo);
            json.setMsg("查询成功");
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "查询失败");
            log.error("查询失败", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 保存
     */
    @PostMapping("saveCol")
    public ReturnData saveCol(@MultiRequestBody ColumnSetupVO pamvo, @MultiRequestBody CorpVO corpVO) {
        Json json = new Json();
        try {
            if (pamvo == null) {
                pamvo = new ColumnSetupVO();
            }
            setDefaultValue(pamvo, corpVO);
            bs_workbenchserv.saveCol(pamvo);
            json.setMsg("保存成功");
            json.setRows(pamvo);
            json.setSuccess(true);
        } catch (Exception e) {
            printErrorLog(json, e, "保存打印设置失败！");
            log.error("保存打印设置失败！", e);
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 设置默认值
     *
     * @param pamvo
     * @throws DZFWarpException
     */
    private void setDefaultValue(ColumnSetupVO pamvo, CorpVO corpVO) throws DZFWarpException {
        pamvo.setPk_corp(getLoginFcorp(corpVO));
        pamvo.setCoperatorid(SystemUtil.getLoginUserId());
        pamvo.setDoperatedate(new DZFDate());
        pamvo.setDr(0);
    }

    /**
     * 上传附件
     */
    @RequestMapping("uploadFile")
    @ResponseBody
    public ReturnData uploadFile(@RequestParam("files") MultipartFile[] files, HttpServletRequest req) {
        Json json = new Json();
        try {
            List<MultipartFile> filestt = ((MultipartHttpServletRequest) req).getFiles("files");
            String fathercorp = SystemUtil.getLoginCorpVo().getFathercorp();
            UserVO uservo = SystemUtil.getLoginUserVo();
            if (files == null) {
                throw new BusinessException("附件不能为空");
            }
            List<String> filenames = new ArrayList<String>();
            List<byte[]> filebytes =  new ArrayList<>();
            for (MultipartFile tfile : files) {
                filenames.add(tfile.getOriginalFilename());
                filebytes.add(readFileData(tfile));
            }
            String pk_corpperiod = req.getParameter("cpperiod");
            String pk_corp = req.getParameter("khid");
            String period = req.getParameter("period");
            bs_workbenchserv.uploadFile(fathercorp, pk_corpperiod, filenames.toArray(new String[0]), filebytes, uservo, pk_corp, period);
            json.setRows(0);
            json.setSuccess(true);
            json.setMsg("保存成功");
        } catch (Exception e) {
            printErrorLog(json, e, "上传失败");
        }
        return ReturnData.ok().data(json);
    }


    /**
     * 读取vo数据
     *
     */
    private byte[] readFileData(MultipartFile tfile) {

        //读取文件
        InputStream in = null;
        ByteArrayOutputStream baos = null;
        byte[] byteData = null;
        try {
            // 一次读多个字节
            byte[] tempbytes = new byte[100];
            baos = new ByteArrayOutputStream();
            int byteread = 0;
            in = tfile.getInputStream();
            // 读入多个字节到字节数组中，byteread为一次读入的字节数
            while ((byteread = in.read(tempbytes)) != -1) {
                baos.write(tempbytes, 0, byteread);
            }
            byteData = baos.toByteArray();
        } catch (Exception e1) {
            log.error("错误",e1);
            throw new BusinessException("读取纳税申报数据出错，该文件不存在！");
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e1) {

                }
            }
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                    log.error("错误",e);
                }
            }
        }

        return byteData;
    }

    /**
     * 附件文件下载
     */
    @PostMapping("downloadAttach")
    public void downloadAttach(@MultiRequestBody BsWorkDocVO paramvo, @MultiRequestBody CorpVO corpVO, HttpServletResponse response) {
        OutputStream output = null;
        if (StringUtil.isEmpty(paramvo.getFathercorp())) {
            paramvo.setFathercorp(corpVO.getFathercorp());
        }
        List<BsWorkDocVO> list = bs_workbenchserv.getAttatches(paramvo);
        BsWorkDocVO docvo = null;
        if (list != null && list.size() > 0) {
            docvo = list.get(0);
        } else {
            return;
        }
        if (docvo != null && !StringUtil.isEmpty(docvo.getVfilepath())) {
            try {
                response.setContentType("application/octet-stream");
                String formattedName = URLEncoder.encode(docvo.getDocname(), "UTF-8");
                response.addHeader("Content-Disposition",
                        "attachment;filename=" + new String(docvo.getDocname().getBytes("UTF-8"), "ISO8859-1")
                                + ";filename*=UTF-8''" + formattedName);
                output = response.getOutputStream();
                byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(docvo.getVfilepath());
                output.write(bytes);
                output.flush();
            } catch (Exception e) {
                log.error("文件下载失败", e);
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (IOException e) {
                    log.error("文件下载失败", e);
                }
            }
        }
    }
}

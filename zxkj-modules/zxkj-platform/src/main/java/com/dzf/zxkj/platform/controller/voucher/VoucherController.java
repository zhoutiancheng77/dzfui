package com.dzf.zxkj.platform.controller.voucher;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.controller.glic.CrkPrintUtil;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.ImageParamVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.*;
import com.dzf.zxkj.platform.model.report.XjllVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.voucher.CopyParam;
import com.dzf.zxkj.platform.model.voucher.PzglmessageVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.bdset.IPzmbhService;
import com.dzf.zxkj.platform.service.glic.ICrkMxService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.jzcl.ITerminalSettle;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.pzgl.impl.CaclTaxMny;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.util.ReportUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
@RequestMapping("/voucher-manage/voucher")
@Slf4j
public class VoucherController {
    @Autowired
    private IVoucherService gl_tzpzserv;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IBDCurrencyService sys_currentserv;
    @Autowired
    private IPersonalSetService gl_gxhszserv;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;
    @Autowired
    private IQmclService gl_qmclserv;
    @Autowired
    private CheckInventorySet inventory_setcheck;
    @Autowired
    private IPzmbhService pzmbhService;
    @Autowired
    private IPzglService gl_pzglserv;
    @Autowired
    private IParameterSetService sys_parameteract;
    @Autowired
    private IUserService userService;
    @Autowired
    private ITerminalSettle gl_qmjzserv;
    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;

    // 查询
    @GetMapping("/query")
    public ReturnData query(VoucherParamVO paramvo) {
        Grid grid = new Grid();
        List<String> pkcorp_list = new ArrayList<>();
        String corps_id = paramvo.getPk_corp();
        if (!StringUtils.isEmpty(corps_id)) {
            pkcorp_list = Arrays.asList(corps_id.split(","));

        } else {
            pkcorp_list.add(SystemUtil.getLoginCorpId());
        }
        if ("2".equals(paramvo.getDateType())) {
            paramvo.setBegindate(DateUtils.getPeriodStartDate(paramvo.getBeginPeriod()));
            paramvo.setEnddate(DateUtils.getPeriodEndDate(paramvo.getEndPeriod()));
        }
        List<PzglPageVo> pzglList = new ArrayList<>();
        long total = 0;
        for (String pk_corp : pkcorp_list) {
            CorpVO corpVo = corpService.queryByPk(pk_corp);

            paramvo.setPk_corp(pk_corp);
            QueryPageVO pagedVO = gl_tzpzserv.query(paramvo);
            TzpzHVO[] vos = (TzpzHVO[]) pagedVO.getPagevos();
            if (vos != null && vos.length > 0) {
                total += pagedVO.getTotal();
                for (TzpzHVO hvo : vos) {
                    try {
                        hvo.setZd_user(CodeUtils1.deCode(hvo.getZd_user()));
                        hvo.setSh_user(CodeUtils1.deCode(hvo.getSh_user()));
                        hvo.setJz_user(CodeUtils1.deCode(hvo.getJz_user()));
                        hvo.setCn_user(CodeUtils1.deCode(hvo.getCn_user()));
                        if ("HP80".equals(hvo.getSourcebilltype())
                                && StringUtils.isEmpty(hvo.getZd_user())) {
                            hvo.setZd_user("大账房系统");
                        }
                    } catch (Exception e) {
                        log.error("解密失败！", e);
                    }
                }
                GxhszVO gxh = gl_gxhszserv.query(pk_corp);
                Integer kmShow = gxh.getPzSubject();
                BdCurrencyVO[] cvos = sys_currentserv.queryCurrency();
                Map<String, BdCurrencyVO> currmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(cvos), new String[]{"pk_currency"});
                for (TzpzHVO vo : vos) {
                    PzglPageVo pvo = new PzglPageVo();

                    BeanUtils.copyProperties(vo, pvo);
                    TzpzBVO[] bvoArray = (TzpzBVO[]) vo.getChildren();
                    if (bvoArray != null && bvoArray.length > 0) {
                        for (TzpzBVO bvo : bvoArray) {
                            if (kmShow == 0) {
                                bvo.setKmmchie(bvo.getSubj_name());
                            } else if (kmShow == 1) {
                                String kmmchie = bvo.getKmmchie();
                                if (kmmchie != null) {
                                    String[] fullname = bvo.getKmmchie().split("/");
                                    if (fullname.length > 1) {
                                        bvo.setKmmchie(fullname[0] + "/" + bvo.getSubj_name());
                                    }
                                }
                            }
                            //取币种编码
                            String curcode = null;
                            if (StringUtils.isEmpty(bvo.getPk_currency())) {
                                curcode = "CNY";//人民币
                            } else {
                                if (IGlobalConstants.RMB_currency_id.equals(bvo.getPk_currency())) {
                                    curcode = "CNY";//人民币
                                } else {
                                    BdCurrencyVO cyvo = currmap.get(bvo.getPk_currency());
                                    curcode = cyvo.getCurrencycode();
                                }
                            }

                            String[] fzhsStr = getFzhsStr(bvo.getFzhs_list());
                            // 摘要显示字段
                            StringBuilder showZy = new StringBuilder(bvo.getZy() == null ? "" : bvo.getZy());
                            boolean hasForeignCur = curcode != null && !curcode.equals("CNY");
                            boolean hasNumber = bvo.getNnumber() != null && bvo.getNnumber().doubleValue() != 0;
                            if (hasForeignCur || hasNumber) {
                                showZy.append("(");
                                if (hasForeignCur) {
                                    showZy.append(curcode).append(":");
                                    if (bvo.getYbjfmny() != null && bvo.getYbjfmny().doubleValue() != 0) {
                                        showZy.append(bvo.getYbjfmny().toString().replaceAll("\\.0+$", ""));
                                    } else {
                                        showZy.append(bvo.getYbdfmny() == null ? ""
                                                : bvo.getYbdfmny().toString().replaceAll("\\.0+$", ""));
                                    }
                                    showZy.append(",汇率:")
                                            .append(bvo.getNrate() == null ? ""
                                                    : bvo.getNrate().toString().replaceAll("\\.0+$", ""));
                                }
                                if (hasNumber) {
                                    if (hasForeignCur) {
                                        showZy.append("; ");
                                    }
                                    showZy.append("数量:")
                                            .append(bvo.getNnumber().toString().replaceAll("\\.0+$", ""));
                                    String measureName = getMeasureName(bvo);
                                    showZy.append(measureName == null ? "" : measureName);
                                    showZy.append(",单价:")
                                            .append(bvo.getNprice() == null ? ""
                                                    : bvo.getNprice().toString().replaceAll("\\.0+$", ""));
                                }
                                showZy.append(")");
                            }
                            // 科目显示字段
                            StringBuilder showKm = new StringBuilder();
                            showKm.append(bvo.getVcode() == null ? "" : bvo.getVcode()).append(fzhsStr[0]).append(" ")
                                    .append(bvo.getKmmchie() == null ? "" : bvo.getKmmchie()).append(fzhsStr[1]);
                            if (bvo.getInvname() != null) {
                                showKm.append("_").append(bvo.getInvname());
                                boolean hasSpec = !StringUtils.isEmpty(bvo.getInvspec());
                                boolean hasType = !StringUtils.isEmpty(bvo.getInvtype());
                                if (hasSpec || hasType) {
                                    showKm.append("(");
                                    if (hasSpec) {
                                        showKm.append(bvo.getInvspec());
                                    }
                                    if (hasType) {
                                        showKm.append(bvo.getInvtype());
                                    }
                                    showKm.append(")");
                                }
                            }
                            bvo.setKmmchie(showKm.toString());
                            bvo.setTmpzy(showZy.toString());

                            pvo.setVdef4(corpVo.getUnitname());
                        }
                    } else {
                        pvo.setVdef4(corpVo.getUnitname());//仅为转会计报错添加
                    }
                    pzglList.add(pvo);
                }
            }
        }
        grid.setTotal(total);
        grid.setSuccess(true);
        grid.setRows(pzglList);
        return ReturnData.ok().data(grid);
    }

    private String[] getFzhsStr(List<AuxiliaryAccountBVO> fzhsvos) {
        String[] fzhsStr = new String[2];
        StringBuilder code = new StringBuilder();
        StringBuilder name = new StringBuilder();
        if (fzhsvos != null && fzhsvos.size() > 0) {
            for (AuxiliaryAccountBVO fzhs : fzhsvos) {
                if (fzhs != null) {
                    code.append("_").append(fzhs.getCode());
                    name.append("_").append(fzhs.getName());
                    if (AuxiliaryConstant.ITEM_INVENTORY.equals(fzhs.getPk_auacount_h())) {
                        if (!StringUtils.isEmpty(fzhs.getSpec())) {
                            name.append("(").append(fzhs.getSpec()).append(")");
                        }
                    }
                }
            }
        }
        fzhsStr[0] = code.toString();
        fzhsStr[1] = name.toString();
        return fzhsStr;
    }

    private String getMeasureName(TzpzBVO bvo) {
        String measureName = bvo.getMeaname();
        List<AuxiliaryAccountBVO> fzvos = bvo.getFzhs_list();
        if (fzvos != null) {
            for (AuxiliaryAccountBVO fzvo : fzvos) {
                if (fzvo != null && AuxiliaryConstant.ITEM_INVENTORY.equals(fzvo.getPk_auacount_h())
                        && fzvo.getUnit() != null) {
                    measureName = fzvo.getUnit();
                }
            }
        }
        return measureName;
    }

    @GetMapping("/queryById")
    public ReturnData queryById(@RequestParam String id) {
        Json json = new Json();
        TzpzHVO tzpzH = gl_tzpzserv.queryHeadVoById(id);
        if (tzpzH == null || !tzpzH.getPk_corp().equals(tzpzH.getPk_corp())) {
            json.setSuccess(false);
            json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
            json.setMsg("凭证不存在，请刷新重试");
        } else {
            GxhszVO gxh = gl_gxhszserv.query(tzpzH.getPk_corp());
            Integer kmShow = gxh.getPzSubject();
            if (tzpzH.getChildren() != null) {
                if (kmShow == 0) {
                    TzpzBVO[] bvos = (TzpzBVO[]) tzpzH.getChildren();
                    for (TzpzBVO bvo : bvos) {
                        bvo.setKmmchie(bvo.getSubj_name());
                    }
                } else if (kmShow == 1) {
                    TzpzBVO[] bvos = (TzpzBVO[]) tzpzH.getChildren();
                    String[] fullname = null;
                    for (TzpzBVO bvo : bvos) {
                        fullname = bvo.getKmmchie().split("/");
                        if (fullname.length > 1) {
                            bvo.setKmmchie(fullname[0] + "/" + fullname[fullname.length - 1]);
                        }
                    }
                }
            }
            try {
                if (tzpzH.getZd_user() != null)
                    tzpzH.setZd_user(CodeUtils1.deCode(tzpzH.getZd_user()));
                if (tzpzH.getSh_user() != null)
                    tzpzH.setSh_user(CodeUtils1.deCode(tzpzH.getSh_user()));
                if (tzpzH.getJz_user() != null)
                    tzpzH.setJz_user(CodeUtils1.deCode(tzpzH.getJz_user()));
                if (tzpzH.getCn_user() != null) {
                    tzpzH.setCn_user(CodeUtils1.deCode(tzpzH.getCn_user()));
                }
                if ("HP80".equals(tzpzH.getSourcebilltype())
                        && StringUtils.isEmpty(tzpzH.getZd_user())) {
                    tzpzH.setZd_user("大账房系统");
                }
            } catch (Exception e) {
                log.error("解密失败", e);
            }
            json.setMsg("查询凭证成功！");
            json.setSuccess(true);
            json.setData(tzpzH);
        }
        return ReturnData.ok().data(json);
    }

    //按月复制凭证
    @PostMapping("/copy")
    public ReturnData copy(@RequestBody CopyParam copyParam) {
        Json json = new Json();
        String[] corpAry;
        TzpzHVO[] sourceVouchers = copyParam.getSourceVoucher();
        Map<String, List<String>> sourceMap = null;
        if (sourceVouchers != null) {
            sourceMap = new HashMap<>();
            for (TzpzHVO source : sourceVouchers) {
                if (sourceMap.containsKey(source.getPk_corp())) {
                    sourceMap.get(source.getPk_corp()).add(source.getPk_tzpz_h());
                } else {
                    List<String> list = new ArrayList<>();
                    list.add(source.getPk_tzpz_h());
                    sourceMap.put(source.getPk_corp(), list);
                }
            }
            corpAry = sourceMap.keySet().toArray(new String[0]);
        } else {
            // 按期间复制
            String corps = copyParam.getCorps();
            if (StringUtils.isEmpty(corps)) {
                corps = SystemUtil.getLoginCorpId();
            }
            corpAry = corps.split(",");
        }
        String copyPeriod = copyParam.getSourcePeriod();
        String aimPeriod = copyParam.getTargetPeriod();
        String aimDate = copyParam.getTargetDate();
        DZFBoolean isqxsy = new DZFBoolean(copyParam.getForce());
        StringBuilder msg = new StringBuilder();
        String userId = SystemUtil.getLoginUserId();
        for (String pk_corp : corpAry) {
            CorpVO corpvo = corpService.queryByPk(pk_corp);
            try {
                String jzPeriod = corpvo.getBegindate().toString().substring(0, 7);
                List<String> ids = null;
                if (sourceMap != null) {
                    ids = sourceMap.get(pk_corp);
                } else {
                    if (jzPeriod.compareTo(copyPeriod) > 0) {
                        throw new BusinessException("复制月份不在建账期间内，不可复制");
                    }
                    if (copyPeriod.compareTo(aimPeriod) > 0) {
                        throw new BusinessException("目标日期应在复制日期之后");
                    }
                }
                if (jzPeriod.compareTo(aimPeriod) > 0) {
                    throw new BusinessException("目标日期不在建账期间内，不可复制");
                }
                TzpzHVO headVO = new TzpzHVO();
                headVO.setIsqxsy(isqxsy);
                headVO.setPk_corp(corpvo.getPk_corp());
                headVO.setDoperatedate(new DZFDate(aimPeriod + "-01"));
                gl_tzpzserv.checkQjsy(headVO);
                List<TzpzHVO> vos = gl_tzpzserv.processCopyVoucher(corpvo, ids, copyPeriod, aimPeriod, aimDate, userId);
                msg.append(corpvo.getUnitname()).append("成功复制").append(vos.size()).append("条数据<br/>");
            } catch (Exception e) {
                String errorMsg;
                if (e instanceof BusinessException) {
                    errorMsg = e.getMessage();
                } else {
                    log.error("复制失败", e);
                    errorMsg = "复制失败";
                }
                if (IVoucherConstants.EXE_RECONFM_CODE.equals(e.getMessage())) {
                    errorMsg = "目标月已损益结转";
                    if (corpAry.length == 1) {
                        json.setStatus(IVoucherConstants.STATUS_RECONFM_CODE);
                    }
                }
                msg.append(corpvo.getUnitname())
                        .append("复制失败，原因：").append(errorMsg)
                        .append("<br/>");
            }
        }
        json.setSuccess(true);
        json.setMsg(msg.toString());
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getTaxItem")
    public ReturnData getTaxItem() {
        Json json = new Json();
        CorpVO corp = SystemUtil.getLoginCorpVo();
        Map<String, String> subjectRule = new HashMap<>();
        String[] rules = CaclTaxMny.getSubjectRule(corp);
        subjectRule.put("cargo", rules[0]);
        subjectRule.put("service", rules[1]);
        subjectRule.put("purchase", rules[2]);
        subjectRule.put("traffic", rules[6]);
        subjectRule.put("inTax", rules[3]);
        subjectRule.put("outTax", rules[4]);
        subjectRule.put("profit", rules[5]);
        List<TaxitemVO> items = gl_tzpzserv.getTaxItems(corp.getChargedeptname());
        json.setHead(subjectRule);
        json.setRows(items);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getNewCode")
    public ReturnData getNewCode(String pk_corp, String date) {
        Json json = new Json();
        if (pk_corp == null) {
            pk_corp = SystemUtil.getLoginCorpId();
        }
        if (date == null) {
            date = SystemUtil.getLoginDate();
        }
        json.setData(yntBoPubUtil.getNewVoucherNo(pk_corp, new DZFDate(date)));
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/save")
    public ReturnData save(@RequestBody TzpzHVO headvo) {
        Json json = new Json();
        try {
            CorpVO corpvo = SystemUtil.getLoginCorpVo();

            headvo.setPk_corp(corpvo.getPk_corp());
            headvo.setIshasjz(DZFBoolean.FALSE);
            headvo.setTs(new DZFDateTime(Calendar.getInstance().getTime().getTime()));
            headvo.setDr(0);
            boolean isNew = false;
            if (StringUtils.isEmpty(headvo.getPk_tzpz_h())) {
                if ((headvo.getPreserveCode() == null || !headvo.getPreserveCode().booleanValue())
                        && !StringUtils.isEmpty(headvo.getSourcebilltype()) && !"Y".equals(headvo.getIsInsert())) {
                    if (!headvo.getSourcebilltype().endsWith("gzjt") & !headvo.getSourcebilltype().endsWith("gzff")) {
                        headvo.setPzh(yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(), headvo.getDoperatedate()));
                    }
                }
                isNew = true;
                headvo.setIsfpxjxm(DZFBoolean.FALSE);
                headvo.setVbillstatus(8);
                headvo.setCoperatorid(SystemUtil.getLoginUserId());
            } else {
                if (Integer.valueOf(headvo.getPzh()) > 9999) {
                    throw new BusinessException("凭证号最大允许9999");
                }
            }
            TzpzBVO[] bodyvos = headvo.getChildren();
            for (int i = 0; i < bodyvos.length; i++) {
                bodyvos[i].setPk_corp(corpvo.getPk_corp());
                bodyvos[i].setTs(new DZFDateTime());
            }
            //数据中心与在线会计平台针对直接生单方式使用抢占模式,所以保存之前先检验
            if (!StringUtils.isEmpty(headvo.getPk_image_group())) {
                checkIsCreated(headvo);
            }
            checkVoucherData(headvo);

            gl_tzpzserv.checkQjsy(headvo);
            //校验总账存货，提示性
            checkInventorySet(headvo, corpvo);

            headvo = gl_tzpzserv.saveVoucher(corpvo, headvo);
            String msg = getMsgOnSave(headvo, corpvo);
            json.setMsg(msg);
            json.setStatus(200);
            json.setSuccess(true);
            json.setData(headvo);
        } catch (BusinessException e) {
            String errorMsg = e.getMessage();
            json.setMsg(errorMsg);
            if (IVoucherConstants.EXE_RECONFM_CODE.equals(errorMsg)) {
                json.setStatus(IVoucherConstants.STATUS_RECONFM_CODE);
            }
            if (errorMsg.startsWith(IVoucherConstants.EXE_INVGL_CODE)) {
                errorMsg = errorMsg.replaceAll(IVoucherConstants.EXE_INVGL_CODE, "");
                errorMsg = errorMsg + "点击[<font color='blue'>确定</font>]按钮，该凭证继续保存，影响成本核算，请后续修改！<br>点击[<font color='blue'>取消</font>]按钮，即取消本次保存操作！";
                json.setStatus(IVoucherConstants.STATUS_INVGL_CODE);
                json.setMsg(errorMsg);
            }
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 校验上传图片是否已生单
     *
     * @param headvo
     * @return
     * @throws BusinessException
     */
    private void checkIsCreated(TzpzHVO headvo) {
        if (StringUtils.isEmpty(headvo.getPk_tzpz_h())) {//如是修改则跳过
            ImageGroupVO groupVO = (ImageGroupVO) gl_tzpzserv.queryImageGroupByPrimaryKey(headvo.getPk_image_group());
            if (groupVO == null)
                return;
            if (DZFBoolean.TRUE == groupVO.getIsuer() && PhotoState.state101 != groupVO.getIstate()) {
                throw new BusinessException("图片组" + groupVO.getGroupcode() + "已直接生单,不能再次生单!");
            }
        }
    }

    private void checkVoucherData(TzpzHVO headvo) {
        String pk_corp = headvo.getPk_corp();
        if (!StringUtils.isEmpty(headvo.getPk_tzpz_h())) {
//			修改时的验证
            TzpzHVO old_hvo = gl_tzpzserv.queryVoucherById(headvo.getPk_tzpz_h());
            if (old_hvo == null) {
                throw new BusinessException("凭证不存在，请刷新重试");
            }
            if (!old_hvo.getPk_corp().equals(pk_corp)) {
                throw new BusinessException("无权操作！");
            }
            if (old_hvo != null && old_hvo.getIshasjz() != null && old_hvo.getIshasjz().booleanValue()) {
                throw new BusinessException("修改失败,已记账凭证不能修改！");
            }
            if (old_hvo != null && old_hvo.getVbillstatus() != 8 && old_hvo.getVbillstatus() != -1) {//-1状态为转会计生成凭证使用
                throw new BusinessException("修改失败,已审核凭证不能修改！");
            }
            if ((headvo.getPreserveCode() == null || !headvo.getPreserveCode().booleanValue())
                    && !headvo.getDoperatedate().toString().substring(0, 7).equals(old_hvo.getDoperatedate().toString().substring(0, 7))) {
                headvo.setPzh(yntBoPubUtil.getNewVoucherNo(headvo.getPk_corp(), headvo.getDoperatedate()));
            }
            headvo.setIsfpxjxm(old_hvo.getIsfpxjxm());
        } else {
//			新增时验证
            if (!headvo.getPk_corp().equals(pk_corp)) {
                throw new BusinessException("无权操作！");
            }
        }

//		验证凭证分录，必需与登录公司pk相同
        if (headvo.getChildren() != null && headvo.getChildren().length > 0) {
            TzpzBVO bvo = null;
            for (int i = 0; i < headvo.getChildren().length; i++) {
                bvo = headvo.getChildren()[i];
                if (!bvo.getPk_corp().equals(pk_corp)) {
                    throw new BusinessException("分录无权操作！");
                }
            }
        }

    }

    //总账存货 保存提示性校验
    private void checkInventorySet(TzpzHVO headvo, CorpVO cpvo) throws BusinessException {
        if (headvo == null
                || (headvo.getIsglicsave() != null
                && headvo.getIsglicsave().booleanValue()))
            return;
        String error = inventory_setcheck.checkInventorySetByPZ(null, cpvo, headvo);
        if (!StringUtils.isEmpty(error)) {
            throw new BusinessException(IVoucherConstants.EXE_INVGL_CODE + error);
        }
    }

    private String getMsgOnSave(TzpzHVO hvo, CorpVO corpVO) {
        StringBuilder msg = new StringBuilder();
        int jflag = 0;
        int dflag = 0;
        TzpzBVO[] bodyvos = (TzpzBVO[]) hvo.getChildren();
        boolean hasIncome = false;
        String incomeCode = null;
        boolean isCbjz = false;
        try {
            QmclVO qmclVO = gl_qmclserv.queryQmclVO(hvo.getPk_corp(), hvo.getPeriod());
            // 成本结转
            isCbjz = qmclVO != null && qmclVO.getIscbjz() != null && qmclVO.getIscbjz().booleanValue();
        } catch (Exception e) {
            log.error("获取成本结转状态失败", e);
        }
        if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())) {
            incomeCode = "^5001\\d+$";
        } else if ("00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
            incomeCode = "^6001\\d+$";
        } else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
            incomeCode = "^5101\\d+$";
        }
        for (int i = 0; i < bodyvos.length; i++) {
            Integer fx = bodyvos[i].getVdirect();
            jflag += fx != null && fx == 0 ? 1 : 0;
            dflag += fx != null && fx == 1 ? 1 : 0;
            String code = bodyvos[i].getVcode();
            if (isCbjz && !hasIncome && code != null
                    && incomeCode != null && code.matches(incomeCode)) {
                hasIncome = true;
            }
        }
        if (hvo.getAutoAnaly() != null && hvo.getAutoAnaly().booleanValue()) {
            if (jflag > 1 && dflag > 1) {//多借多贷提示语修改,原因为多借多贷现金流量自动分析不准确
                msg.append("现金流量已自动分析,多借多贷凭证请手工确认<br>");
            } else {
                msg.append("保存凭证成功,现金流量已自动分析<br>");
            }
        }
        if (hasIncome) {
            msg.append("销售收入数据已变更，请重新进行成本结转<br>");
        }
        if (msg.length() == 0) {
            msg.append("保存凭证成功");

        }
        return msg.toString();
    }

    @GetMapping("/queryImage")
    public ReturnData queryImage(String imgIds, String corpId,
                                 String beginDate, String endDate) {
        if (StringUtils.isEmpty(corpId)) {
            corpId = SystemUtil.getLoginCorpId();
        }
        String currentDate = new DZFDate().toString();
        if (beginDate == null) {
            beginDate = currentDate;
        }
        if (endDate == null) {
            endDate = currentDate;
        }
        Json json = new Json();
        ImageParamVO param = new ImageParamVO();
        if (!StringUtils.isEmpty(imgIds)) {
            param.setImgIds(imgIds);
        } else {
            param.setBegindate(beginDate);
            param.setEnddate(endDate);
            param.setPk_corp(corpId);
        }
        List<ImageGroupVO> list = gl_tzpzserv.queryImageGroupByPicture(param);
        json.setRows(list);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getImageById")
    public ReturnData getImageById(@RequestParam String id) {
        Json json = new Json();
        ImageLibraryVO[] imageArray = gl_tzpzserv.queryImageVO(id);
        if (imageArray != null && imageArray.length > 0) {
            ImageGroupVO groupVo = gl_tzpzserv.queryImageGroupByPrimaryKey(id);
            groupVo.setChildren(imageArray);
            json.setData(groupVo);
            json.setSuccess(true);
        } else {
            throw new BusinessException("无图片信息");
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/audit")
    public ReturnData audit(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String ids = param.get("ids");
        String mode = param.get("mode");
        if (StringUtils.isEmpty(ids)) {
            throw new BusinessException("请选择凭证");
        }
        String[] idsArray = ids.split(",");
        String pk_corp = SystemUtil.getLoginCorpId();
        DZFDate loginDate = DZFDate.getDate(SystemUtil.getLoginDate());
        String userid = SystemUtil.getLoginUserId();
        Set<String> corpSet = userService.querypowercorpSet(userid);
        YntParameterSet setvo = sys_parameteract.queryParamterbyCode(pk_corp, "dzf003");

        int fail = 0;
        Map<String, Boolean> statusMap = new HashMap<>();
        StringBuilder datePz = new StringBuilder();
        StringBuilder auditPz = new StringBuilder();
        String errMsg = "";
        boolean cnqz = setvo != null && setvo.getPardetailvalue() == 0;
        List<TzpzHVO> hvos = gl_tzpzserv.queryVoucherByIds(Arrays.asList(idsArray), cnqz,
                false, false);
        if (hvos == null) {
            throw new BusinessException("凭证信息为空!");
        }
        List<TzpzHVO> updateList = new ArrayList<>();
        for (TzpzHVO hvo : hvos) {
            if (!corpSet.contains(hvo.getPk_corp())) {
                if ("".equals(errMsg)) {
                    errMsg = "无权操作！";
                }
                fail++;
                continue;
            }
            if (hvo.getVbillstatus() == IVoucherConstants.TEMPORARY && hvo.getIautorecognize() != 1) {
                // 暂存态且未识别不能审核
                fail++;
                if ("".equals(errMsg)) {
                    errMsg = "暂存态不能进行审核！";
                }
                continue;
            }
            if (hvo.getVbillstatus() == IVoucherConstants.AUDITED) {// 审核通过
                auditPz.append(hvo.getPzh() + "，");
                fail++;
                if ("".equals(errMsg)) {
                    errMsg = "不能重复审核！";
                }
                continue;
            }
            if (cnqz) {// 需要出纳签字
                TzpzBVO[] bvos = hvo.getChildren();
                DZFBoolean iszj = DZFBoolean.FALSE;
                if (bvos != null && bvos.length > 0 && (hvo.getBsign() == null || !hvo.getBsign().booleanValue())) {
                    for (TzpzBVO bvo : bvos) {
                        if (bvo.getVcode().startsWith("1001") || bvo.getVcode().startsWith("1002")
                                || bvo.getVcode().startsWith("1012")) {
                            iszj = DZFBoolean.TRUE;
                        }
                    }
                    if (iszj.booleanValue()) {
                        auditPz.append(hvo.getPzh() + "，");
                        fail++;
                        if ("".equals(errMsg)) {
                            errMsg = "暂未签字，不能审核！";
                        }
                        continue;
                    }
                }
            }
            DZFDate auditdate = loginDate;
            if (auditdate.before(hvo.getDoperatedate())) {
                if ("0".equals(mode)) {
                    auditdate = hvo.getDoperatedate();
                } else {
                    datePz.append(hvo.getPzh() + "，");
                    fail++;
                    if ("".equals(errMsg)) {
                        errMsg = "审核时间不能早于制单日期！";
                    }
                    continue;
                }

            }
            hvo.setDapprovedate(auditdate);
            updateList.add(hvo);
            statusMap.put(hvo.getPk_tzpz_h(), true);
        }
        if (updateList.size() > 0) {
            gl_pzglserv.updateAudit(updateList, userid);
        }
        if (idsArray.length == 1) {
            if (fail > 0) {
                json.setMsg(errMsg);
                json.setSuccess(false);
            } else {
                json.setMsg("审核成功");
                json.setSuccess(true);
            }
        } else {
            StringBuilder msg = new StringBuilder();
            if (fail > 0) {
                json.setStatus(2);
                msg.append("成功：")
                        .append(updateList.size())
                        .append("，失败：").append(fail)
                        .append("".equals(errMsg) ? "" : (",原因：" + errMsg));
            } else {
                json.setStatus(0);
                msg.append("审核成功").append(updateList.size()).append("条");
            }
            json.setMsg(msg.toString());
            json.setSuccess(true);
        }
        json.setData(statusMap);

        return ReturnData.ok().data(json);
    }

    @PostMapping("/unAudit")
    public ReturnData unAudit(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String ids = param.get("ids");
        if (StringUtils.isEmpty(ids)) {
            throw new BusinessException("请选择凭证");
        }
        String[] idsArray = ids.split(",");
        List<TzpzHVO> updateList = new ArrayList<TzpzHVO>();
        Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());

        int fail = 0;
        Map<String, Boolean> statusMap = new HashMap<String, Boolean>();
        StringBuilder msg = new StringBuilder();
        String errorMsg = null;

        List<TzpzHVO> hvos = gl_tzpzserv.queryVoucherByIds(Arrays.asList(idsArray),
                false, false, false);
        if (hvos == null) {
            throw new BusinessException("凭证信息为空!");
        }

        Map<String, Boolean> closeStatus = new HashMap<>();
        for (TzpzHVO hvo : hvos) {
            if (checkCloseStatus(hvo.getPk_corp(), hvo.getPeriod(), closeStatus)) {
                fail++;
                if (errorMsg == null) {
                    errorMsg = "已年结不能取消审核";
                }
                continue;
            }
            if (hvo.getIshasjz() != null && hvo.getIshasjz().booleanValue()) {
                fail++;
                if (errorMsg == null) {
                    errorMsg = "已记账,不能取消审核";
                }
                continue;
            }
            if (hvo.getVbillstatus() == IVoucherConstants.TEMPORARY
                    || hvo.getVbillstatus() == IVoucherConstants.FREE) {
                fail++;
                if (errorMsg == null) {
                    errorMsg = "未审核,不能取消审核";
                }
                continue;
            }
            if (!corpSet.contains(hvo.getPk_corp())) {
                fail++;
                continue;
            }
            updateList.add(hvo);
            statusMap.put(hvo.getPk_tzpz_h(), true);
        }
        int success = updateList.size();
        if (success > 0) {
            gl_pzglserv.updateUnAudit(updateList);
        }
        if (idsArray.length == 1) {
            if (fail > 0) {
                json.setMsg(errorMsg);
                json.setSuccess(false);
            } else {
                json.setMsg("反审核成功");
                json.setSuccess(true);
            }
        } else {
            if (fail > 0) {
                json.setStatus(2);
                msg.append("成功：")
                        .append(updateList.size())
                        .append("，失败：").append(fail);
                if (errorMsg != null) {
                    msg.append(",原因：").append(errorMsg);
                }
            } else {
                json.setStatus(0);
                msg.append("反审核成功" + success + "条");
            }
            json.setMsg(msg.toString());
            json.setSuccess(true);
        }
        json.setData(statusMap);
        return ReturnData.ok().data(json);
    }

    // 记账
    @PostMapping("/account")
    public ReturnData account(@RequestBody Map<String, String> param) {
        Json json = new Json();

        String ids = param.get("ids");
        String mode = param.get("mode");
        if (StringUtils.isEmpty(ids)) {
            throw new BusinessException("请选择凭证");
        }
        String[] idsArray = ids.split(",");
        DZFDate loginDate = new DZFDate(SystemUtil.getLoginDate());
        String userid = SystemUtil.getLoginUserId();
        Set<String> corpSet = userService.querypowercorpSet(userid);
        int fail = 0;
        Map<String, Boolean> statusMap = new HashMap<String, Boolean>();
        StringBuilder datePz = new StringBuilder();
        StringBuilder auditPz = new StringBuilder();
        StringBuilder jzPz = new StringBuilder();
        StringBuilder msg = new StringBuilder();

        List<TzpzHVO> hvos = gl_tzpzserv.queryVoucherByIds(Arrays.asList(idsArray), false, false, false);
        if (hvos == null) {
            throw new BusinessException("凭证信息为空!");
        }

        List<TzpzHVO> updateList = new ArrayList<>();
        for (TzpzHVO hvo : hvos) {
            if (hvo.getVbillstatus() != IVoucherConstants.AUDITED) {
                auditPz.append(hvo.getPzh()).append("，");
                fail++;
                continue;
            }
            DZFDate jzdate = loginDate;
            if (loginDate.before(hvo.getDapprovedate())) {
                if (mode.equals("0")) {
                    jzdate = hvo.getDoperatedate();
                } else {
                    datePz.append(hvo.getPzh()).append("，");
                    fail++;
                    continue;
                }
            }
            if (hvo.getIshasjz() != null && hvo.getIshasjz().booleanValue()) {
                jzPz.append(hvo.getPzh()).append("，");
                fail++;
                continue;
            }
            if (!corpSet.contains(hvo.getPk_corp())) {
                fail++;
                continue;
            }
            hvo.setDjzdate(jzdate);
            updateList.add(hvo);
            statusMap.put(hvo.getPk_tzpz_h(), true);
        }
        if (updateList.size() > 0) {
            gl_pzglserv.updateAccounting(updateList, userid);
        }
        if (fail > 0) {
            json.setStatus(2);
            msg.append("成功：" + updateList.size() + "，失败：" + fail);
        } else {
            json.setStatus(0);
            msg.append("记账成功" + updateList.size() + "条");
        }
        json.setData(statusMap);
        json.setSuccess(true);
        json.setMsg(msg.toString());
        return ReturnData.ok().data(json);
    }

    // 取消记账
    @PostMapping("/unAccount")
    public ReturnData unAccount(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String ids = param.get("ids");
        if (StringUtils.isEmpty(ids)) {
            throw new BusinessException("请选择凭证");
        }
        String[] idsArray = ids.split(",");
        List<TzpzHVO> updateList = new ArrayList<>();
        Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
        int fail = 0;
        Map<String, Boolean> statusMap = new HashMap<>();
        StringBuilder msg = new StringBuilder();
        StringBuilder jzPz = new StringBuilder();
        String reason = "";
        String period = null;
        // 关账
        Map<String, Boolean> gzStatus = new HashMap<>();
        // 年结
        Map<String, Boolean> closeStatus = new HashMap<>();
        List<TzpzHVO> hvos = gl_tzpzserv.queryVoucherByIds(Arrays.asList(idsArray), false, false, false);
        if (hvos == null) {
            throw new BusinessException("凭证信息为空!");
        }

        for (TzpzHVO hvo : hvos) {
            if (checkGzStatus(hvo.getPk_corp(), hvo.getPeriod(), gzStatus)) {
                fail++;
                if (reason.equals("")) {
                    reason = " 原因：已关账不能取消记账";
                }
                continue;
            }
            if (checkCloseStatus(hvo.getPk_corp(), hvo.getPeriod(), closeStatus)) {
                fail++;
                if (reason.equals("")) {
                    reason = " 原因：已年结不能取消记账";
                }
                continue;
            }
            if (hvo.getIshasjz() != null && !hvo.getIshasjz().booleanValue()) {
                jzPz.append(hvo.getPzh()).append("，");
                fail++;
                continue;
            }
            if (!corpSet.contains(hvo.getPk_corp())) {
                fail++;
                continue;
            }
            updateList.add(hvo);
            statusMap.put(hvo.getPk_tzpz_h(), true);
        }
        int success = updateList.size();
        if (success > 0) {
            gl_pzglserv.updateUnAccounting(updateList);
        }
        if (fail > 0) {
            json.setStatus(2);
            msg.append("成功：").append(success).append("，失败：").append(fail).append(reason);
        } else {
            json.setStatus(0);
            msg.append("取消记账成功").append(success).append("条");
        }
        json.setData(statusMap);
        json.setSuccess(true);
        json.setMsg(msg.toString());
        return ReturnData.ok().data(json);
    }

    // 自动整理
    @PostMapping("/sort")
    public ReturnData sort(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String corpIds = param.get("companys");
        if (StringUtils.isEmpty(corpIds)) {
            corpIds = SystemUtil.getLoginCorpId();
        }
        String[] companys = corpIds.split(",");
        String beginPeriod = param.get("begindate");
        String endPeriod = param.get("enddate");
        DZFDate bdate = DateUtils.getPeriodStartDate(beginPeriod);
        DZFDate edate = DateUtils.getPeriodEndDate(endPeriod);
        String sort_type = param.get("sort_type");
        Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
        if (companys != null && companys.length > 0) {
            for (String pk_corp : companys) {
                if (!corpSet.contains(pk_corp)) {
                    throw new BusinessException("整理失败,公司错误！");
                }
            }
        }
        String msg = null;
        if ("auto_date".equals(sort_type)) {
            msg = gl_pzglserv.updateNumByDate(companys, bdate, edate);
        } else if ("auto_number".equals(sort_type)) {
            msg = gl_pzglserv.doVoucherOrder(companys, bdate, edate);
        } else if ("auto_uploadpic".equals(sort_type)) {
            msg = gl_pzglserv.pzsortByuploadpic(companys, bdate, edate);
        } else if ("auto_churukubillcode".equals(sort_type)) {
            msg = gl_pzglserv.savechurukubillcodesort(companys, bdate, edate);
        }
        json.setSuccess(true);
        json.setMsg(msg);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/delete")
    public ReturnData delete(@RequestBody TzpzHVO[] dataArray) {
        if (dataArray.length == 1) {
            return deleteSingleVoucher(dataArray[0]);
        }
        Json json = new Json();
        Set<String> powerCorpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
        Map<String, List<TzpzHVO>> groupData = new HashMap<>();

        List<String> corpInfos = new ArrayList<>(groupData.keySet());
        Collections.sort(corpInfos);
        // 选择线程安全的
        List<PzglmessageVO> errorlist = new Vector<>();
        ExecutorService pool = null;
        try {
            pool = Executors.newFixedThreadPool(Math.min(100, dataArray.length));
            List<Future<String>> vc = new Vector<>();
            for (TzpzHVO obj : dataArray) {
                Future<String> future = pool.submit(new VoucherDeleteTask(obj, powerCorpSet,
                        errorlist, gl_pzglserv, gl_tzpzserv));
                vc.add(future);
            }
            // 默认执行 线程池操作结果，等待本组数据执行完成
            for (Future<String> fu : vc) {
                fu.get();
            }
            pool.shutdown();
        } catch (Exception e) {
            log.error("凭证删除错误", e);
        } finally {
            try {
                if (pool != null) {
                    pool.shutdown();
                }
            } catch (Exception e) {
            }
        }
        String msg = getResultMsg(errorlist);
        json.setMsg(msg);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    private ReturnData deleteSingleVoucher(TzpzHVO data) {
        Json json = new Json();
        data = gl_tzpzserv.queryVoucherById(data.getPk_tzpz_h());

        if (data != null) {
            data.setIsqxsy(DZFBoolean.TRUE);
            try {
                gl_tzpzserv.deleteVoucher(data);
                json.setMsg("删除成功");
                json.setSuccess(true);
            } catch (Exception e) {
                json.setSuccess(false);
                if (e instanceof BusinessException) {
                    json.setMsg(e.getMessage());
                } else {
                    json.setMsg("删除失败");
                }
                if (IVoucherConstants.EXE_RECONFM_CODE.equals(e.getMessage())) {
                    json.setStatus(IVoucherConstants.STATUS_RECONFM_CODE);
                }
            }
        } else {
            throw new BusinessException("凭证不存在, 请检查");
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/checkChannelContract")
    public ReturnData checkChannelContract(@RequestParam String pk_corp) {
        Json json = new Json();
        String msg = gl_tzpzserv.checkChannelContract(pk_corp);
        json.setMsg(msg);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    // 查询对当前登录公司有权限的用户
    @GetMapping("/queryPowerUser")
    public ReturnData queryPowerUser() {
        Json json = new Json();
        List<UserVO> users = gl_pzglserv.queryPowerUser(SystemUtil.getLoginCorpId());
        if (users != null && users.size() > 0) {
            for (UserVO userVO : users) {
                try {
                    userVO.setUser_name(CodeUtils1.deCode(userVO.getUser_name()));
                } catch (Exception e) {
                }
            }
        }
        json.setRows(users);
        json.setMsg("查询成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    // 修改制单人
    @PostMapping("/modifyCreator")
    public ReturnData modifyCreator(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String ids = param.get("ids");
        if (StringUtils.isEmpty(ids)) {
            throw new BusinessException("请选择凭证");
        }
        String newCreator = param.get("creator");
        if (StringUtils.isEmpty(newCreator)) {
            throw new BusinessException("请选择制单人");
        }
        String[] pklist = ids.split(",");
        gl_pzglserv.updateCreator(Arrays.asList(pklist), newCreator);
        json.setSuccess(true);
        json.setMsg("修改成功！");
        return ReturnData.ok().data(json);
    }

    // 凭证合并
    @PostMapping("/mergeVoucher")
    public ReturnData mergeVoucher(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String pk_corp = param.get("pk_corp");
        String ids = param.get("ids");
        String zy = param.get("zy");
        String[] rs = gl_pzglserv.processMergeVoucher(SystemUtil.getLoginUserId(),
                pk_corp, ids.split(","), zy);
        json.setSuccess(true);
        json.setMsg("合并成功");
        String msg = rs[1];
        if (!StringUtils.isEmpty(msg)) {
            json.setMsg(msg);
        }
        // 合并后的凭证ID
        json.setData(rs[0]);
        /*String logMsg = rs[2];
        if (StringUtils.isEmpty(logMsg)) {
            logMsg = "无合并成功凭证";
        }*/
        return ReturnData.ok().data(json);
    }

    /**
     * 查询合并规则
     */
    @GetMapping("/queryMergeSetting")
    public ReturnData queryMergeSetting() {
        Json json = new Json();
        VoucherMergeSettingVO setting = gl_pzglserv.queryMergeSetting(SystemUtil.getLoginCorpId());
        json.setData(setting);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    /**
     * 保存合并规则
     */
    @PostMapping("/saveMergeSetting")
    public ReturnData saveMergeSetting(@RequestBody VoucherMergeSettingVO setting) {
        Json json = new Json();
        setting.setPk_corp(SystemUtil.getLoginCorpId());
        gl_pzglserv.saveMergeSetting(setting.getPk_corp(), setting);
        json.setMsg("设置成功");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getTempById")
    public ReturnData getTempById(@RequestParam String tempId, String mny) {
        Json json = new Json();
        DZFDouble amount = null;
        if (!StringUtils.isEmpty(mny)) {
            try {
                amount = new DZFDouble(mny);
            } catch (Exception e) {
                throw new BusinessException("金额错误");
            }
        }
        CorpVO loginCorp = SystemUtil.getLoginCorpVo();
        List<TzpzBVO> tzpzBList = new ArrayList<TzpzBVO>();
        List<PzmbbVO> list = pzmbhService.queryB(tempId);
        if (list != null && list.size() > 0) {
            TzpzBVO tzpzBVO = null;
            String pk_corp = list.get(0).getPk_corp();
            Map<String, YntCpaccountVO> kmMap = accountService.queryMapByPk(pk_corp);
            Map<String, AuxiliaryAccountBVO> auxMap = gl_fzhsserv.queryMap(pk_corp);
            // 10个辅助核算项属性名
            String[] fzhsAttNames = new String[10];
            for (int i = 1; i <= 10; i++) {
                fzhsAttNames[i - 1] = "fzhsx" + i;
            }
            for (PzmbbVO pvo : list) {
                if (!pvo.getPk_corp().equals(loginCorp.getPk_corp())) {
                    throw new BusinessException("无权使用该模板！");
                }

                tzpzBVO = new TzpzBVO();
                tzpzBVO.setPk_accsubj(pvo.getPk_accsubj());
                tzpzBVO.setZy(pvo.getAbstracts());
                tzpzBVO.setVdirect(pvo.getDirection());
                tzpzBVO.setPk_corp(pvo.getPk_corp());
                tzpzBVO.setSubj_name(pvo.getVname());
                tzpzBVO.setPk_taxitem(pvo.getPk_taxitem());
                tzpzBVO.setTaxcode(pvo.getTaxcode());
                tzpzBVO.setTaxname(pvo.getTaxname());
                tzpzBVO.setTaxratio(pvo.getTaxratio());

                YntCpaccountVO cpaccount = kmMap.get(pvo.getPk_accsubj());
                if (cpaccount != null) {
                    tzpzBVO.setVname(cpaccount.getAccountname());
                    tzpzBVO.setSubj_allname(cpaccount.getFullname());
                    tzpzBVO.setKmmchie(cpaccount.getFullname());
                    tzpzBVO.setVcode(cpaccount.getAccountcode());
                    tzpzBVO.setSubj_code(cpaccount.getAccountcode());
                    tzpzBVO.setVdirect(cpaccount.getDirection());
                    if (cpaccount.getIswhhs() != null && cpaccount.getIswhhs().booleanValue()) {
                        List<BdCurrencyVO> bzList = cpaccount.getExc_cur_array();
                        if (bzList != null && bzList.size() > 0) {
                            tzpzBVO.setExc_cur_array(bzList);
                            String pk_currency = pvo.getPk_currency() == null ? bzList.get(0).getPk_currency() : pvo.getPk_currency();
                            tzpzBVO.setPk_currency(pk_currency);
                        }
                    }
                    // 启用库存的话，凭证模板没有保存存货，显示数量有问题
                    if (cpaccount.getIsnum() != null && cpaccount.getIsnum().booleanValue()) {
                        tzpzBVO.setIsnum(DZFBoolean.TRUE);
                        tzpzBVO.setMeaname(cpaccount.getMeasurename());
                        if (IcCostStyle.IC_ON.equals(loginCorp.getBbuildic())) {
                            tzpzBVO.setMeacode(pvo.getMeacode());
                            tzpzBVO.setMeaname(pvo.getMeaname());
                            tzpzBVO.setPk_inventory(pvo.getPk_inventory());
                            tzpzBVO.setInvcode(pvo.getInvcode());
                            tzpzBVO.setInvname(pvo.getInvname());
                        }
                    }
                }
                tzpzBVO.setNnumber(pvo.getNnumber());
                tzpzBVO.setNprice(pvo.getNprice());
                tzpzBVO.setNrate(pvo.getNrate());
                for (String attName : fzhsAttNames) {
                    // 复制辅助核算项属性
                    tzpzBVO.setAttributeValue(attName, pvo.getAttributeValue(attName));
                }
                List<AuxiliaryAccountBVO> fzhsList = ReportUtil.getFzhsList(pvo.getPk_corp(), pvo, auxMap);
                if (fzhsList.size() > 0)
                    tzpzBVO.setFzhs_list(fzhsList);
                if (pvo.getDirection() == 0) {
                    // 借方金额
                    tzpzBVO.setJfmny(pvo.getMny() == null ? amount : pvo.getMny());
                    tzpzBVO.setYbjfmny(pvo.getYbmny());
                } else {
                    tzpzBVO.setDfmny(pvo.getMny() == null ? amount : pvo.getMny());
                    tzpzBVO.setYbdfmny(pvo.getYbmny());
                }
                tzpzBList.add(tzpzBVO);
            }
        } else {
            throw new BusinessException("模板内容为空！");
        }

        json.setRows(tzpzBList);
        json.setSuccess(true);
        json.setStatus(200);
        json.setMsg("成功！");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveAsTemplate")
    public ReturnData saveAsTemplate(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String childrenStr = param.get("children");
        String tempCode = param.get("tempCode");
        String tempName = param.get("tempName");
        String saveMny = param.get("saveMny");
        //检查模板编码和模板名称是否重复query
        List<PzmbhVO> pzmbList = pzmbhService.query(SystemUtil.getLoginCorpId());
        if (!StringUtil.isEmpty(tempCode) && !StringUtil.isEmpty(tempName)) {
            for (PzmbhVO pzmbVo : pzmbList) {
                if (tempCode.equals(pzmbVo.getVtemplatecode())) {
                    json.setMsg("模板编码不能重复");
                    json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
                    json.setSuccess(false);
                    json.setRows(new PzmbhVO());
                    throw new BusinessException("模板编码不能重复");
                }
            }
        } else {
            json.setMsg("模板编码或名称不能为空！");
            json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
            json.setSuccess(false);
            json.setRows(new PzmbhVO());
            throw new BusinessException("模板编码或名称不能为空！");
        }
        TzpzBVO[] voucherBodyVos = JsonUtils.deserialize(childrenStr, TzpzBVO[].class);
        PzmbbVO[] templateBodyVos = JsonUtils.deserialize(childrenStr, PzmbbVO[].class);

        PzmbhVO pzmbHead = new PzmbhVO();
        //给模板设置默认值
        setTempDefaultInfo(pzmbHead, templateBodyVos, voucherBodyVos, Boolean.valueOf(saveMny));

        pzmbHead.setVtemplatename(tempName);//模板编码
        pzmbHead.setVtemplatecode(tempCode);//模板名称

        pzmbHead.setChildren(templateBodyVos);

        pzmbhService.save(pzmbHead);
        json.setMsg("保存凭证模板成功");
        json.setStatus(200);
        json.setSuccess(true);
        json.setRows(pzmbHead);
        return ReturnData.ok().data(json);
    }

    private void setTempDefaultInfo(PzmbhVO pzmbHead, PzmbbVO[] bodyvos, TzpzBVO[] Tbodyvos, boolean saveMny) {
        String pk_corp = SystemUtil.getLoginCorpId();
        int len = Tbodyvos == null ? 0 : Tbodyvos.length;
        // 给凭证模板赋凭证数据的值
        for (int i = 0; i < len; i++) {
            PzmbbVO mb = bodyvos[i];
            TzpzBVO pz = Tbodyvos[i];
            mb.setPk_corp(pk_corp);
            mb.setTs(new DZFDateTime());
            mb.setDr(0);
            if (pz.getJfmny() != null
                    && pz.getJfmny().doubleValue() != 0) {
                mb.setDirection(0);// 有借方金额为0
                if (saveMny) {
                    mb.setMny(pz.getJfmny());
                    mb.setYbmny(pz.getYbjfmny());
                }
            } else if (pz.getDfmny() != null
                    && pz.getDfmny().doubleValue() != 0) {
                mb.setDirection(1);// 有贷方金额为1
                if (saveMny) {
                    mb.setMny(pz.getDfmny());
                    mb.setYbmny(pz.getYbdfmny());
                }
            }
            if (saveMny) {
                mb.setPk_currency(pz.getPk_currency());
                mb.setNnumber(pz.getNnumber());
                mb.setNprice(pz.getNprice());
                mb.setNrate(pz.getNrate());
            } else {
                mb.setNnumber(null);
                mb.setNprice(null);
            }
            mb.setAbstracts(pz.getZy());// 摘要
            mb.setVcode(pz.getVcode());
            mb.setPk_taxitem(pz.getPk_taxitem());
            mb.setVname(pz.getVname());
            mb.setPk_accsubj(pz.getPk_accsubj());
        }
        pzmbHead.setPk_corp(pk_corp);
        pzmbHead.setTs(new DZFDateTime());
        pzmbHead.setDr(0);
        pzmbHead.setCoperatorid(SystemUtil.getLoginUserId());
        pzmbHead.setDoperatedate(new DZFDate());
    }

    @GetMapping("/img")
    public void readImage(@RequestParam String corpCode, @RequestParam String path,
                          HttpServletResponse response, HttpSession session) {

        ServletOutputStream sos = null;
        InputStream fis = null;
        try {
            File imgFile = null;
            String dateFolder = path.substring(0, 8);
            if (path.startsWith("ImageOcr")) {
                String folder = ImageCommonPath.getDataCenterPhotoPath() + "/" + path;
                imgFile = new File(folder);

            } else {
                if (path.indexOf("/") < 0 && path.indexOf("\\") < 0) {
                    String imgfolder = ImageCommonPath.getDataCenterPhotoPath() + "/" + corpCode + "/" + dateFolder;
                    String folder = imgfolder + "/" + path;
                    imgFile = new File(folder);
                } else {
                    String folder = ImageCommonPath.getDataCenterPhotoPath() + "/" + path;
                    imgFile = new File(folder);
                }
            }

            if (!imgFile.exists()) {
                fis = VoucherController.class.getResourceAsStream("/img/picnoexist.jpg");
            } else {
                fis = new FileInputStream(imgFile);
            }
            sos = response.getOutputStream();

            //读取文件流
            int i = 0;
            byte[] buffer = new byte[1024];
            while ((i = fis.read(buffer)) != -1) {
                //写文件流
                sos.write(buffer, 0, i);
            }
            sos.flush();
            fis.close();
        } catch (Exception e) {
            log.error("查询图片失败！", e);
        } finally {
            if (sos != null) {
                try {
                    sos.close();
                } catch (Exception e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (Exception e) {
                }
            }
        }

    }

    @GetMapping("/getVoucherCashFlow")
    public ReturnData getVoucherCashFlow(String id) {
        Json json = new Json();
        XjllVO[] xjVos = gl_tzpzserv.queryCashFlow(id, SystemUtil.getLoginCorpId());
        json.setRows(xjVos);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/deleteVoucherCashFlow")
    public ReturnData deleteVoucherCashFlow(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String id = param.get("id");
        if (StringUtils.isEmpty(id)) {
            throw new BusinessException("凭证id为空");
        }
        gl_tzpzserv.deleteCashFlow(id, SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setMsg("删除成功");
        return ReturnData.ok().data(json);
    }

    //现金流量分配
    @PostMapping("/saveVoucherCashFlow")
    public ReturnData saveVoucherCashFlow(@RequestBody XjllVO[] xjllvos) {
        Json json = new Json();
        DZFDate date = new DZFDate();
        String pk_corp = SystemUtil.getLoginCorpId();
        String userid = SystemUtil.getLoginUserId();
        for (XjllVO vo : xjllvos) {
            vo.setPk_corp(pk_corp);
            vo.setCoperatorid(userid);
            vo.setDoperatedate(date);
        }
        List<XjllVO> rs = gl_tzpzserv.addCashFlow(xjllvos);
        json.setMsg("保存成功");
        json.setSuccess(true);
        json.setRows(rs);
        return ReturnData.ok().data(json);
    }

    //导出Excel
    @PostMapping("/exportExcel")
    public void exportExcel(@RequestParam String ids, HttpServletResponse response) {
//        boolean bexport = checkExcelExport();
//        if(!bexport){
//            return;
//        }
        OutputStream toClient = null;
        try {
            String fileName = "凭证导出" + new DZFDate() + ".xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename*=UTF-8''" + formattedName + ";filename=" + fileName + "");
            response.setContentType("application/vnd.ms-excel");
            toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(gl_pzglserv.exportExcel(ids));
            toClient.flush();
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("凭证导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("凭证导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("凭证导出错误", e);
            }
        }
    }

    @PostMapping("/importVoucher")
    public ReturnData importVoucher(String checkNum, MultipartFile impfile) {
        Json json = new Json();
        json.setSuccess(false);
        try {
            String fileName = impfile.getOriginalFilename();
            String fileType = fileName.substring(fileName.lastIndexOf(".") + 1);
            List<TzpzHVO> imported = gl_pzglserv.saveImportVoucher(SystemUtil.getLoginCorpVo(),
                    SystemUtil.getLoginUserId(), fileType, impfile.getInputStream(),
                    !"N".equals(checkNum));
            if (imported != null && imported.size() > 0) {
                String periodBegin = null;
                String periodEnd = null;
                for (int i = 0; i < imported.size(); i++) {
                    TzpzHVO hvo = imported.get(i);
                    if (i == 0) {
                        periodBegin = hvo.getPeriod();
                        periodEnd = hvo.getPeriod();
                    } else {
                        if (hvo.getPeriod().compareTo(periodBegin) < 0) {
                            periodBegin = hvo.getPeriod();
                        } else if (hvo.getPeriod().compareTo(periodEnd) > 0) {
                            periodEnd = hvo.getPeriod();
                        }
                    }
                }
                Map<String, String> periodData = new HashMap<String, String>();
                periodData.put("periodBegin", periodBegin);
                periodData.put("periodEnd", periodEnd);
                json.setData(periodData);
            }

            json.setMsg("导入成功");
            json.setSuccess(true);
        } catch (Exception e) {
            json.setSuccess(false);
            if ("-150".equals(e.getMessage())) {
                json.setStatus(-150);
            } else if (e instanceof BusinessException) {
                json.setMsg(e.getMessage());
            } else {
                log.error("导入失败", e);
                json.setMsg("导入失败");
            }
        }
        return ReturnData.ok().data(json);
    }

    @PostMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) {
        OutputStream toClient = null;
        try {
            String tempPath = "/template/凭证模板-导入.xls";
            byte[] data = gl_pzglserv.exportTemplate(SystemUtil.getLoginCorpId(), tempPath);

            String fileName = "凭证模板-导入.xls";
            String formattedName = URLEncoder.encode(fileName, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename*=UTF-8''" + formattedName + ";filename=" + fileName + "");
            response.setContentType("application/vnd.ms-excel");
            toClient = new BufferedOutputStream(response.getOutputStream());
            toClient.write(data);
            toClient.flush();
            response.getOutputStream().flush();
        } catch (BusinessException e) {
            try {
                response.setContentType("String");
                response.getOutputStream().write(e.getMessage().getBytes());
            } catch (IOException e1) {
            }
        } catch (IOException e) {
            log.error("Export Error", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("Export Error", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("Export Error", e);
            }
        }
    }

    @GetMapping("/queryAssistPrintSetting")
    public ReturnData queryAssistPrintSetting() {
        Json json = new Json();
        VoucherPrintAssitSetVO[] vos = gl_pzglserv.queryPrintAssistSetting(SystemUtil.getLoginCorpId());
        json.setRows(vos);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveAssistPrintSetting")
    public ReturnData saveAssistPrintSetting(@RequestBody VoucherPrintAssitSetVO[] settingArray) {
        Json json = new Json();
        gl_pzglserv.savePrintAssistSetting(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), settingArray);
        json.setSuccess(true);
        json.setMsg("保存成功");
        return ReturnData.ok().data(json);
    }

    private String getResultMsg(List<PzglmessageVO> errorlist) {
        if (errorlist == null || errorlist.size() == 0) {
            return "删除凭证完成！";
        }
        Collections.sort(errorlist);// 排序
        StringBuilder sf = new StringBuilder();
        String error = "删除失败！";
        for (PzglmessageVO vo : errorlist) {
            if (!StringUtils.isEmpty(vo.getErrorinfo())) {
                error = vo.getErrorinfo();
            }
            sf.append("<font color = 'red'>公司:" + vo.getGsname() + "，期间:" + vo.getPeriod() + "，凭证号:" + vo.getPzh() + "，"
                    + error + "</font><br>");
        }
        return sf.toString();
    }

    private boolean checkCloseStatus(String pk_corp, String period, Map<String, Boolean> statusMap) {
        String key = pk_corp + period.substring(0, 4);
        boolean status = false;
        if (statusMap.containsKey(key)) {
            status = statusMap.get(key);
        } else {
            status = gl_qmjzserv.checkIsYearClose(pk_corp, period);
            statusMap.put(key, status);
        }
        return status;
    }

    private boolean checkGzStatus(String pk_corp, String period, Map<String, Boolean> statusMap) {
        String key = pk_corp + period;
        boolean status = false;
        if (statusMap.containsKey(key)) {
            status = statusMap.get(key);
        } else {
            status = qmgzService.isGz(pk_corp, period);
            statusMap.put(key, status);
        }
        return status;
    }

    @RequestMapping("/quickCreateVoucher")
    public ReturnData<Json> quickCreateVoucher(@RequestBody Map<String, String> param) {
        Json json = new Json();
        try {
            JSONObject rawData = JSON.parseObject(param.get("data"));
            gl_tzpzserv.createVoucherQuick(SystemUtil.getLoginCorpVo(), SystemUtil.getLoginUserId(), rawData);
            json.setSuccess(true);
            json.setMsg("制单成功！");
            json.setStatus(200);
        } catch (Exception e) {
            json.setStatus(-200);
            log.error("Export Error", e);

            if (e instanceof BusinessException) {
                if ("当月已结转损益，不能操作！".equals(e.getMessage())) {
                    json.setStatus(IVoucherConstants.STATUS_RECONFM_CODE);
                    json.setMsg("当月已结转损益，不能操作！");
                }
                if ("生成凭证失败:借贷方金额不等！".equals(e.getMessage())) {
                    log.error("Export Error", e);
                    json.setMsg("生成凭证失败:借贷方金额不等！");
                }
                if ("找不到对应模板！".equals(e.getMessage())) {
                    log.error("Export Error", e);
                    json.setMsg("找不到对应模板！");
                }
            }
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 根据图片pk查询出凭证的pk
     * 图片浏览使用
     */
    @RequestMapping("/serVoucherByImagePk")
    public ReturnData<Json> serVoucherByImagePk(@RequestBody VoucherParamVO paramvo) {
        Json json = new Json();

        paramvo.setPk_corp(SystemUtil.getLoginCorpId());
        List<TzpzHVO> tzpzB = new ArrayList<TzpzHVO>();
        try {
            if (paramvo == null || StringUtil.isEmpty(paramvo.getPk_tzpz_h())) {
                throw new BusinessException("联查凭证失败!");
            } else {
                //参数： 制单日期和需要回冲的凭证号
                tzpzB = gl_tzpzserv.serVoucherByImgPk(paramvo);
                if (tzpzB == null || tzpzB.size() == 0) {
                    log.info("联查凭证失败!");
                    json.setMsg("联查凭证失败!");
                    json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
                    json.setSuccess(false);
                    json.setRows(new TzpzHVO());
                } else {
                    log.info("联查凭证成功");
                    json.setMsg("联查凭证成功");
                    json.setStatus(200);
                    json.setSuccess(true);
                    json.setRows(tzpzB);
                }
            }
        } catch (Exception e) {
//			log.info("联查凭证失败" + e.getMessage());
//			json.setMsg("联查凭证失败" + e.getMessage());
            json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
//			json.setSuccess(false);
            json.setRows(new TzpzHVO());
            log.error("Export Error", e);
        }
        return ReturnData.ok().data(json);
    }

    @GetMapping("/getVoucherTaxItem")
    public ReturnData getVoucherTaxItem(@RequestParam String voucherId) {
        Json json = new Json();
        PZTaxItemRadioVO[] items = gl_tzpzserv.getVoucherTaxItem(voucherId, SystemUtil.getLoginCorpId());
        json.setRows(items);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveVoucherTaxItem")
    public ReturnData saveVoucherTaxItem(@RequestBody PZTaxItemRadioVO[] items) {
        Json json = new Json();
        gl_tzpzserv.saveVoucherTaxItem(items, SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setMsg("保存成功");
        return ReturnData.ok().data(json);
    }

    @PostMapping("/deleteVoucherTaxItem")
    public ReturnData deleteVoucherTaxItem(@RequestBody Map<String, String> param) {
        Json json = new Json();
        String pk_voucher = param.get("voucherId");
        gl_tzpzserv.deleteVoucherTaxItem(pk_voucher, SystemUtil.getLoginCorpId());
        json.setSuccess(true);
        json.setMsg("删除成功");
        return ReturnData.ok().data(json);
    }

    @RequestMapping("/getNewInfo")
    public ReturnData getNewInfo(@RequestBody Map<String, String> param) {
        Json json = new Json();
        try {
            String date = param.get("vchdate");
            DZFDate doperatedate = null;
            if (StringUtil.isEmptyWithTrim(date)) {
                doperatedate = new DZFDate(SystemUtil.getLoginDate());
            } else {
                try {
                    doperatedate = new DZFDate(date);
                } catch (Exception e) {
                    log.error("凭证号查询日期解析失败", e);
                    throw new BusinessException("日期格式错误，获取凭证号失败！");
                }
            }
            Map<String, Object> data = getNewInfoJson(doperatedate);
            json.setStatus(200);
            json.setSuccess(true);
            json.setMsg("获取凭证号成功");
            json.setData(data);
        } catch (Exception e) {
            log.error("获取凭证号失败!", e);
        }
        return ReturnData.ok().data(json);
    }

    private Map<String, Object> getNewInfoJson(DZFDate vchdate) {
        String newVoucherNum = yntBoPubUtil.getNewVoucherNo(SystemUtil.getLoginCorpId(), vchdate);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("vchNum", newVoucherNum);
        data.put("vchYear", vchdate.getYear());
        data.put("qj", vchdate.getMonth());
        return data;
    }

    /**
     * 校验是否存在出库数据
     */
    @GetMapping("/checkCrkmx")
    public ReturnData checkCrkmx(@RequestParam String ids) {
        Json json = new Json();
        ICrkMxService gl_rep_crkmxserv = (ICrkMxService) SpringUtils.getBean("gl_rep_crkmxserv");
        Map<String, List<IcDetailVO>> crkmxlist = gl_rep_crkmxserv.queryCrkmxs(null, ids.split(","),
                SystemUtil.getLoginCorpId(), "");
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    /**
     * 打印出入库明细
     */
    @PostMapping("/printCrkmx")
    public void printCrkmx(@RequestParam String ids, HttpServletResponse response) {
        Rectangle pageSize = PageSize.A4;
        float leftsize = 47f;
        float rightsize = 15f;
        float topsize = 36f;
        Document document = new Document(pageSize, leftsize, rightsize, topsize, 4);
        ByteArrayOutputStream buffer = null;
        try {
            String loginCorp = SystemUtil.getLoginCorpId();
            ICrkMxService gl_rep_crkmxserv = (ICrkMxService) SpringUtils.getBean("gl_rep_crkmxserv");
            SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
            Map<String, List<IcDetailVO>> crkmxlist = gl_rep_crkmxserv.queryCrkmxs(null,
                    ids.split(","), loginCorp, "");
            CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, loginCorp);
            buffer = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, buffer);
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            CrkPrintUtil printutil = new CrkPrintUtil();
            // 赋值首字符的值
            printutil.batchPrintCrkContent(leftsize, topsize, document, canvas, crkmxlist, cpvo);
        } catch (Exception e) {
            log.error("打印出入库明细", e);
        } finally {
            document.close();
        }
        ServletOutputStream out = null;
        try {
            response.setContentType("application/pdf");
            response.setCharacterEncoding("utf-8");
            response.setContentLength(buffer.size());
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

    @GetMapping("/readColumnSetting")
    public ReturnData readColumnSetting() {
        Json json = new Json();
        String setting = gl_pzglserv.readColumnSetting(SystemUtil.getLoginUserId());
        json.setSuccess(true);
        json.setRows(setting);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/saveColumnSetting")
    public ReturnData saveColumnSetting(@RequestBody Map<String, String> data) {
        Json json = new Json();
        String setting = data.get("setting");
        if (StringUtils.isEmpty(setting)) {
            throw new BusinessException("参数为空");
        }
        gl_pzglserv.saveColumnSetting(SystemUtil.getLoginCorpId(), SystemUtil.getLoginUserId(), setting);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryHeadByPeriod")
    public ReturnData queryHeadByPeriod(@RequestParam String period, String corpId) {
        Json json = new Json();
        if (StringUtils.isEmpty(corpId)) {
            corpId = SystemUtil.getLoginCorpId();
        }
        List<TzpzHVO> list = gl_pzglserv.query(period, corpId);
        json.setSuccess(true);
        json.setRows(list);
        return ReturnData.ok().data(json);
    }

    @GetMapping("/queryChildren")
    public ReturnData queryChildren(@RequestParam String voucherId, @RequestParam String corpId) {
        Json json = new Json();
        List<TzpzBVO> list = gl_pzglserv.queryB(voucherId, corpId);
        String curcode;
        BdCurrencyVO cvo;
        StringBuilder sb;
        for (TzpzBVO bvo : list) {
            curcode = bvo.getCur_code();
            if (StringUtils.isEmpty(curcode)) {
                curcode = bvo.getPk_currency();
                if (StringUtils.isEmpty(curcode))
                    curcode = IGlobalConstants.RMB_currency_id;
                cvo = sys_currentserv.queryCurrencyVOByPk(curcode);
                if (cvo != null)
                    curcode = cvo.getCurrencycode();
                else
                    curcode = null;
            }
            String[] fzStr = getFzhsStr(bvo.getFzhs_list());
            if (!"".equals(fzStr[1]))
                bvo.setVcode(bvo.getVcode() + fzStr[0]);
            sb = new StringBuilder();
            sb.append(bvo.getVname() == null ? "未找到" : bvo.getVname()).append(fzStr[1])
                    .append(bvo.getInvname() == null ? "" : "_" + bvo.getInvname())
                    .append(curcode == null || curcode.equals("CNY") ? "" : "_" + curcode);
            bvo.setVname(sb.toString());
        }
        json.setRows(list);
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }

    @PostMapping("/manualSort")
    public ReturnData manualSort(@RequestBody Map<String, String> data) {
        Json json = new Json();
        JSONArray array = JSONArray.parseArray(data.get("data"));
        String corp = data.get("corpId");
        if (!StringUtil.isEmpty(corp)) {
            corp = SystemUtil.getLoginCorpId();
        }
        if (array != null && array.size() > 0) {
            JSONObject obj = (JSONObject) array.get(0);
            String qj = obj.getString("zdrq");
            if (qmgzService.isGz(obj.getString("corpId"), qj)) {
                throw new BusinessException("整理失败,该公司已关账不能凭证整理哦。");
            }
            gl_pzglserv.doVoucherOrder3(corp, array);
            json.setSuccess(true);
            json.setMsg("整理成功");
        } else {
            json.setSuccess(false);
            json.setMsg("未选择凭证！");
        }
        return ReturnData.ok().data(json);
    }

    /**
     * 查询凭证是否已经回冲过
     * 根据摘要判断
     * 摘要：红冲 + 制单日期 + 回冲的凭证号
     */
    @GetMapping("/checkHasScarletLetter")
    public ReturnData checkHasScarletLetter(@RequestParam String pzh, @RequestParam String date) {
        Json json = new Json();
        VoucherParamVO param = new VoucherParamVO();
        param.setPzh(pzh);
        param.setZdrq(new DZFDate(date));
        param.setPk_corp(SystemUtil.getLoginCorpId());
        //参数： 制单日期和需要回冲的凭证号
        List<TzpzHVO> list = gl_tzpzserv.serHasRedBack(param);
        if (list == null || list.size() == 0) {
            json.setMsg("该凭证可以进行红字回冲");
            json.setData(false);
        } else {
            json.setMsg("该凭证不可再进行红字回冲");
            json.setData(true);
        }
        json.setSuccess(true);
        return ReturnData.ok().data(json);
    }
}

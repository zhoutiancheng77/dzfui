package com.dzf.zxkj.platform.controller.voucher;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageParamVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.PzglPageVo;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.voucher.CopyParam;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.bdset.IPzmbhService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.pzgl.impl.CaclTaxMny;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IBDCurrencyService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

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
                            bvo.setZy(showZy.toString());

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
            json.setRows(headvo);
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

    @GetMapping("/queryPicture")
    public ReturnData queryPicture(String imgIds, String corp_id,
                                   String begindate, String enddate) {
        if (StringUtils.isEmpty(corp_id)) {
            corp_id = SystemUtil.getLoginCorpId();
        }
        String currentdate = new DZFDate().toString();
        if (begindate == null) {
            begindate = currentdate;
        }
        if (enddate == null) {
            enddate = currentdate;
        }
        Json json = new Json();
        ImageParamVO param = new ImageParamVO();
        if (!StringUtils.isEmpty(imgIds)) {
            param.setImgIds(imgIds);
        } else {
            param.setBegindate(begindate);
            param.setEnddate(enddate);
            param.setPk_corp(corp_id);
        }
        List<ImageGroupVO> list = gl_tzpzserv.queryImageGroupByPicture(param);
        if (list == null || list.size() == 0) {
            json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
            json.setSuccess(false);
            json.setMsg("没找到图片！");
        } else {
            json.setData(list);
            json.setStatus(200);
            json.setSuccess(true);
            json.setMsg("成功");
        }
        return ReturnData.ok().data(json);
    }
}

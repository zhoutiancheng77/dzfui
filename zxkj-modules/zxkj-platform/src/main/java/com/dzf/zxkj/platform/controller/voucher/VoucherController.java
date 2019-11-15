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
import com.dzf.zxkj.common.query.QueryPageVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.pzgl.PzglPageVo;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.voucher.CopyParam;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
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
        if(tzpzH == null || !tzpzH.getPk_corp().equals(tzpzH.getPk_corp())){
            json.setSuccess(false);
            json.setStatus(IVoucherConstants.STATUS_ERROR_CODE);
            json.setMsg("凭证不存在，请刷新重试");
        } else {
            GxhszVO gxh = gl_gxhszserv.query(tzpzH.getPk_corp());
            Integer kmShow = gxh.getPzSubject();
            if(tzpzH.getChildren() != null){
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
                if (tzpzH.getCn_user() != null){
                    tzpzH.setCn_user(CodeUtils1.deCode(tzpzH.getCn_user()));
                }
                if("HP80".equals(tzpzH.getSourcebilltype())
                        && StringUtils.isEmpty(tzpzH.getZd_user())){
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
}

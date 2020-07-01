package com.dzf.zxkj.report.service.jcsz.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.jcsz.IAccountService;
import com.dzf.zxkj.report.service.jcsz.IAuxiliaryAccountService;
import com.dzf.zxkj.report.service.jcsz.ICorpService;
import com.dzf.zxkj.report.utils.LetterNumberSortUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AuxiliaryAccountServiceImpl implements IAuxiliaryAccountService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ICorpService corpService;

    @Override
    public Map<String, AuxiliaryAccountBVO> queryMap(String pk_corp) throws DZFWarpException {
        AuxiliaryAccountBVO[] auxiliaryAccountBVOS = queryAllB(pk_corp);
        return auxiliaryAccountBVOS == null ? new HashMap() : Arrays.stream(auxiliaryAccountBVOS).collect(Collectors.toMap(AuxiliaryAccountBVO::getPk_auacount_b, auxiliaryAccountBVO -> auxiliaryAccountBVO));
    }

    public List<InventoryVO> queryInfo(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select  ry.*,fy.name invclassname,re.name measurename,ry.pk_subject from ynt_inventory  ry  ");
        sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
        sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
        sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");

        List<InventoryVO> inventoryVOList = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(InventoryVO.class));
        if (inventoryVOList == null || inventoryVOList.size() == 0) {
            return new ArrayList<>();
        }
        Map<String, YntCpaccountVO> map = accountService.queryMapByPk(pk_corp);

        YntCpaccountVO yntCpaccountVO;
        for (InventoryVO vo : inventoryVOList) {
            yntCpaccountVO = map.get(vo.getPk_subject());
            if (yntCpaccountVO != null) {
                vo.setKmcode(yntCpaccountVO.getAccountcode());
                vo.setKmname(yntCpaccountVO.getAccountname());
            }
        }
        inventoryVOList.sort(Comparator.comparing(InventoryVO::getCode, Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));
        return inventoryVOList;
    }

    private List<InventoryVO> query(String pk_corp, String kmid) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(kmid);
        StringBuffer sf = new StringBuffer();
        sf.append(
                " select  ry.*,fy.name invclassname,re.name measurename,nt.accountname kmname from ynt_inventory  ry  ");
        sf.append(" left join ynt_invclassify fy on ry.pk_invclassify = fy.pk_invclassify ");
        sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
        sf.append(" left join ynt_cpaccount nt on nt.pk_corp_account = ry.pk_subject ");
        sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? and ry.pk_subject = ?");
        List<InventoryVO> inventoryVOList = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(InventoryVO.class));
        if (inventoryVOList == null || inventoryVOList.size() == 0)
            return null;
        inventoryVOList.sort(Comparator.comparing(InventoryVO::getCode, Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));
        return inventoryVOList;
    }


    private AuxiliaryAccountBVO[] queryInventory(String pk_corp) {
        List<InventoryVO> list = queryInfo(pk_corp);
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<AuxiliaryAccountBVO> alist = new ArrayList<>();
        AuxiliaryAccountBVO bvo;
        for (InventoryVO invo : list) {
            bvo = new AuxiliaryAccountBVO();
            bvo.setCode(invo.getCode());
            bvo.setName(invo.getName());
            bvo.setPk_auacount_h("000001000000000000000006");
            bvo.setPk_auacount_b(invo.getPk_inventory());
            bvo.setUnit(invo.getMeasurename());
            bvo.setSpec(invo.getInvspec());// 规格
            bvo.setPk_corp(invo.getPk_corp());
            bvo.setTaxratio(invo.getTaxratio());// 税率
            bvo.setVmemo(invo.getMemo());// 备注
            bvo.setPk_accsubj(invo.getPk_subject());//科目
            bvo.setSubjname(invo.getKmname());//科目名称
            alist.add(bvo);
        }
        alist.sort(Comparator.comparing(AuxiliaryAccountBVO::getCode, Comparator.nullsFirst(LetterNumberSortUtil.letterNumberOrder())));
        return alist.toArray(new AuxiliaryAccountBVO[alist.size()]);
    }

    @Override
    public AuxiliaryAccountBVO[] queryAllB(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        CorpVO corpvo = corpService.queryCorpByPk(pk_corp);
        AuxiliaryAccountBVO[] auxiliaryAccountBVOS;
        //启用进销存
        if (IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
            auxiliaryAccountBVOS = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
                    " pk_corp = ? and nvl(dr,0) = 0  and pk_auacount_h <> '000001000000000000000006'  ", sp);
            List<AuxiliaryAccountBVO> bvoList;
            if (auxiliaryAccountBVOS == null || auxiliaryAccountBVOS.length == 0) {
                bvoList = new ArrayList<>();
            } else {
                bvoList = new ArrayList<>(Arrays.asList(auxiliaryAccountBVOS));
            }
            //查询存货档案
            AuxiliaryAccountBVO[] invos = queryInventory(pk_corp);
            if (invos != null && invos.length > 0) {
                bvoList.addAll(new ArrayList<>(Arrays.asList(invos)));
            }
            auxiliaryAccountBVOS = bvoList.toArray(auxiliaryAccountBVOS);
        } else {
            auxiliaryAccountBVOS = (AuxiliaryAccountBVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountBVO.class,
                    " pk_corp = ? and nvl(dr,0) = 0 ", sp);
        }
        VOUtil.ascSort(auxiliaryAccountBVOS, new String[]{"pk_auacount_h", "code"});
        return auxiliaryAccountBVOS;
    }

    @Override
    public AuxiliaryAccountHVO queryHByCode(String pk_corp, String fzlb) {
        AuxiliaryAccountHVO[] hvos = queryHByPkCorp(pk_corp);
        if (hvos != null && hvos.length > 0) {
            int fzlbcode = Integer.parseInt(fzlb);
            for (AuxiliaryAccountHVO hvo : hvos) {
                if (fzlbcode == hvo.getCode().intValue()) {
                    return hvo;
                }
            }
        }
        return null;
    }

    @Override
    public AuxiliaryAccountHVO[] queryHByPkCorp(String pk_corp) {
        String condition = " pk_corp in(?,?) and nvl(dr,0) = 0  ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(IDefaultValue.DefaultGroup);
        AuxiliaryAccountHVO[] results = (AuxiliaryAccountHVO[]) singleObjectBO
                .queryByCondition(AuxiliaryAccountHVO.class, condition, sp);
        VOUtil.ascSort(results, new String[]{"code"});
        return results;
    }

    @Override
    public AuxiliaryAccountBVO[] queryBByFzlb(String pk_corp, String fzlb) {
        AuxiliaryAccountBVO[] resvos = queryAllB(pk_corp);

        List<AuxiliaryAccountBVO> list = new ArrayList<>();

        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(fzlb);
        AuxiliaryAccountHVO[] hvos = (AuxiliaryAccountHVO[]) singleObjectBO.queryByCondition(AuxiliaryAccountHVO.class,
                " nvl(dr,0)=0 and pk_corp in( ?,'000001') and code = ?", sp);

        if (hvos != null && hvos.length > 0) {
            String pk_headid = hvos[0].getPk_auacount_h();
            if (resvos != null && resvos.length > 0) {
                for (AuxiliaryAccountBVO bvo : resvos) {
                    if (pk_headid.equals(bvo.getPk_auacount_h())) {
                        list.add(bvo);
                    }
                }
            }
        }

        return list.toArray(new AuxiliaryAccountBVO[0]);
    }

    @Override
    public boolean isExistFz(String pk_corp, String pk_auacount_b, String pk_auacount_h) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(pk_auacount_b)) {
            return false;
        }
        String sql = "select 1 from ynt_fzhs_b where  pk_corp=? and pk_auacount_b=?  and nvl(dr,0)=0";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(pk_auacount_b);
        if (!StringUtil.isEmpty(pk_auacount_h)) {
            sql = sql + " and pk_auacount_h=? ";
            sp.addParam(pk_auacount_h);
        }
        return singleObjectBO.isExists(pk_auacount_b, sql, sp);//(InvclassifyVO.class, where, sp);
    }
}

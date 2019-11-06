package com.dzf.zxkj.platform.service.zcgl.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.AssetUtil;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.ZcZzVO;
import com.dzf.zxkj.platform.model.zcgl.ZcdzVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IZcCommonService;
import com.dzf.zxkj.platform.service.zcgl.IZczzdzReportService;
import com.dzf.zxkj.report.service.IZxkjReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("zczzdzReportService")
public class ZczzdzReportImpl implements IZczzdzReportService {

    @Autowired
    private IZxkjReportService zxkjReportService;

    @Autowired
    private IZcCommonService zcCommonService;

    @Autowired
    private ICpaccountService gl_cpacckmserv;

    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private ICorpService corpService;

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public ZcdzVO[] queryAssetCheckVOs(String pk_corp, String period) throws DZFWarpException {
        CorpVO corpVO =  corpService.queryByPk(pk_corp);
        BdTradeAssetCheckVO[] bdTradeAssetCheckVOS = zcCommonService.getAssetcheckVOs(corpVO.getCorptype(), pk_corp);
        if ((bdTradeAssetCheckVOS == null) || (bdTradeAssetCheckVOS.length == 0)) {
            return new ZcdzVO[0];
        }

        ZcdzVO[] zcdzVOs = new ZcdzVO[bdTradeAssetCheckVOS.length];
        ZcdzVO zcdzVO;
        int index = 0;
        Map<String, BdAssetCategoryVO> cateMap = zcCommonService.queryAssetCategoryMap();
        String newRule = gl_cpacckmserv.queryAccountRule(corpVO.getPk_corp());

        //公司级的
        //替换下面两个查询  gzx
        YntCpaccountVO[] yntCpaccountVOS = gl_cpacckmserv.queryAccountByPz( corpVO.getPk_corp());
        Map<String, YntCpaccountVO> cpacodemap = Arrays.stream(yntCpaccountVOS).collect(Collectors.toMap(YntCpaccountVO::getAccountcode, a -> a, (k1, k2) -> k1));
        Map<String, YntCpaccountVO> cpakeymap = Arrays.stream(yntCpaccountVOS).collect(Collectors.toMap(YntCpaccountVO::getPrimaryKey, a -> a, (k1, k2) -> k1));
        //Map<String, YntCpaccountVO> cpacodemap = getCorpKmMap(cpvo);
        //Map<String, YntCpaccountVO> cpakeymap = AccountCache.getInstance().getMap(null, pk_corp);
        //行业级的
        Map<String, BdTradeAccountVO> bdTradeAccountVOMap = gl_cpacckmserv.getTradeKmMap(corpVO.getCorptype());
        //获取发生额
        Map<String, FseJyeVO> fseJyeVOMap = zxkjReportService.getFsJyeVOs(pk_corp, period, 1);

        List<ZcZzVO> zcZzVOS = getAssetCardList(pk_corp, period);

        FseJyeVO fseJyeVO;
        for (BdTradeAssetCheckVO bdTradeAssetCheckVO : bdTradeAssetCheckVOS) {
            int assetProperty = bdTradeAssetCheckVO.getAssetproperty();
            int assetAccount = bdTradeAssetCheckVO.getAssetaccount();
            String pk_asset_category = bdTradeAssetCheckVO.getPk_assetcategory();
            double totalAssetMny = zcZzVOS.stream().filter(v -> {
                boolean flag = true;
                if (!StringUtil.isEmptyWithTrim(pk_asset_category)) {
                    flag = flag && pk_asset_category.equals(v.getZclbbm());
                }
                return flag && Integer.toString(assetProperty).equals(v.getZcsx());
            }).mapToDouble(v -> {
                switch (assetAccount) {
                    case 0:
                        return v.getYzye() == null ? 0.0D : v.getYzye().getDouble();
                    case 1:
                        return v.getLjye() == null ? 0.0D : v.getLjye().getDouble();
                    case 2:
                        return v.getJzye() == null ? 0.0D : v.getJzye().getDouble();
                    case 3:
                        return v.getJzye() == null ? 0.0D : v.getJzye().getDouble();//取余额
                    default:
                        return 0.0D;
                }
            }).sum();
            //优化 gzx
//            double totalAssetMny = 0; //getTotalAssetMny(pk_corp, period, assetProperty, pk_asset_category, assetAccount, false);
            String pk_corp_account;
            if (IGlobalConstants.currency_corp.equals(bdTradeAssetCheckVO.getPk_corp())) {
                pk_corp_account = hyColumnHandle(newRule, cpacodemap, bdTradeAccountVOMap, bdTradeAssetCheckVO.getPk_glaccount());// yntBoPubUtil.getCorpAccountPkByTradeAccountPk(, pk_corp);
            } else {
                pk_corp_account = bdTradeAssetCheckVO.getPk_glaccount();
            }

            fseJyeVO = fseJyeVOMap.get(pk_corp_account);
            double totalAccMny = 0;
            if (fseJyeVO != null) {
                totalAccMny = SafeCompute.add(fseJyeVO.getQmjf(), fseJyeVO.getQmdf()).doubleValue();
            }
            zcdzVO = new ZcdzVO();
            zcdzVO.setQj(period);

            zcdzVO.setZcsx(AssetUtil.getAssetProperty(assetProperty));

            if (!StringUtil.isEmptyWithTrim(pk_asset_category)) {
                BdAssetCategoryVO cateVO = cateMap.get(pk_asset_category);
                if (cateVO != null) {
                    zcdzVO.setZclb(cateVO.getCatename());
                }
            }

            if (assetProperty == 1 && (pk_asset_category == null || pk_asset_category.trim().length() == 0)) {
                zcdzVO.setZckm("累计摊销");
            } else {
                zcdzVO.setZckm(AssetUtil.getAssetAccount(assetAccount));
            }

            YntCpaccountVO accountVO = cpakeymap.get(pk_corp_account);

            if(accountVO!=null){
            	zcdzVO.setZzkmbh(accountVO.getAccountcode());
            	zcdzVO.setZzkmmc(accountVO.getAccountname());
            }

            zcdzVO.setZcje(new DZFDouble(totalAssetMny));

            zcdzVO.setZzje(new DZFDouble(totalAccMny));

            zcdzVOs[(index++)] = zcdzVO;
        }
        return zcdzVOs;
    }

    private List<ZcZzVO> getAssetCardList(String pk_corp, String period) {
        DZFDate periodBeginDate = DZFDate.getDate(period + "-01");
        DZFDate periodEndDate = periodBeginDate.getDateAfter(periodBeginDate
                .getDaysMonth() - 1);
        SQLParameter sp = new SQLParameter();
        String sql = getQuerySql(pk_corp, sp, periodBeginDate, periodEndDate);
        ArrayList<ZcZzVO> zcZzVOList = (ArrayList<ZcZzVO>) singleObjectBO
                .executeQuery(sql, sp, new BeanListProcessor(ZcZzVO.class));

        if (zcZzVOList == null || zcZzVOList.isEmpty()) {
            return new ArrayList<>();
        }

        return zcZzVOList;
    }

    private String getQuerySql(String pk_corp, SQLParameter sp, DZFDate begin, DZFDate end) {

        StringBuilder sb = new StringBuilder();
        sb.append("select * from (");
        // 资产建账
        sb.append(" select b.PK_ASSETCATEGORY as zclbbm, 1 xh,  a.accountdate rq, substr(a.accountdate, 1, 7) qj,");
        sb.append("   a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, null ywdh, h.pzh pzzh, '资产建账' zy, ");
        sb.append("   a.accountmny yzjf, null yzdf, a.accountmny yzye, null ljjf, a.initdepreciation ljdf, a.initdepreciation ljye, a.accountmny-nvl(a.initdepreciation,0) jzye,   ");
        sb.append("   h.pk_tzpz_h as pzpk    ");
        sb.append(" from ynt_assetcard a inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on a.pk_voucher=h.pk_tzpz_h");
        sb.append(" where nvl(a.dr,0)=0 and a.pk_corp = ?  and a.accountdate<= ? ");
        sp.addParam(pk_corp);
        sp.addParam(end.toString());
        // 资产增加
        sb.append(" union all");
        sb.append(" select b.PK_ASSETCATEGORY as zclbbm,2 xh, m.businessdate rq,substr(m.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, m.vbillno ywdh, h.pzh pzzh, '资产增加' zy,");
        sb.append("  m.changevalue yzjf, null yzdf, m.changevalue yzye, null ljjf, null ljdf, null ljye, m.changevalue jzye,  ");
        sb.append("  h.pk_tzpz_h as pzpk ");
        sb.append(" from ynt_valuemodify m inner join ynt_assetcard a on m.pk_assetcard=a.pk_assetcard and nvl(a.dr,0)=0 inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on m.pk_voucher=h.pk_tzpz_h");
        sb.append(" where nvl(m.dr,0)=0 and m.changevalue>0 and m.pk_corp= ?  and m.businessdate<= ?");
        sp.addParam(pk_corp);
        sp.addParam(end.toString());

        // 资产减少
        sb.append(" union all");
        sb.append(" select b.PK_ASSETCATEGORY as zclbbm,3 xh, m.businessdate rq,substr(m.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, m.vbillno ywdh, h.pzh pzzh, '资产减少' zy, ");
        sb.append(" null yzjf, -m.changevalue yzdf, m.changevalue yzye, null ljjf, null ljdf, null ljye, m.changevalue jzye ");
        sb.append(" , h.pk_tzpz_h as pzpk ");
        sb.append(" from ynt_valuemodify m inner join ynt_assetcard a on m.pk_assetcard=a.pk_assetcard and nvl(a.dr,0)=0 inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on m.pk_voucher=h.pk_tzpz_h");
        sb.append(" where nvl(m.dr,0)=0 and m.changevalue<0 and m.pk_corp= ?  and m.businessdate<= ? ");
        sp.addParam(pk_corp);
        sp.addParam(end.toString());

        // 累计折旧
        sb.append(" union all");
        sb.append(" select b.PK_ASSETCATEGORY as zclbbm,4 xh, n.businessdate rq,substr(n.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, '' ywdh, h.pzh pzzh, '累计折旧' zy,");
        sb.append(" null yzjf, null yzdf, null yzye, null ljjf, n.originalvalue ljdf, n.originalvalue ljye, -n.originalvalue jzye, h.pk_tzpz_h as pzpk ");
        sb.append(" from ynt_depreciation n inner join ynt_assetcard a on n.pk_assetcard=a.pk_assetcard and nvl(a.dr,0)=0 inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on n.pk_voucher=h.pk_tzpz_h");
        sb.append(" where nvl(n.dr,0)=0 and a.pk_corp= ?  and n.businessdate<= ? ");
        sp.addParam(pk_corp);
        sp.addParam(end.toString());

        // 资产清理
        sb.append(" union all");
        sb.append(" select b.PK_ASSETCATEGORY as zclbbm,5 xh, l.businessdate rq,substr(l.businessdate, 1, 7) qj,  a.assetcode zcbh, a.assetname zcmc, b.catename zclb, b.assetproperty zcsx, l.vbillno ywdh, h.pzh pzzh, '资产清理' zy, null yzjf, a.assetmny yzdf, -a.assetmny yzye, a.depreciation ljjf, null ljdf, -a.depreciation ljye, -a.assetmny+a.depreciation jzye, h.pk_tzpz_h as pzpk");
        sb.append(" from ynt_assetclear l inner join ynt_assetcard a on l.pk_assetcard=a.pk_assetcard inner join ynt_category b on a.assetcategory=b.pk_assetcategory left join ynt_tzpz_h h on l.pk_voucher=h.pk_tzpz_h");
        sb.append(" where nvl(l.dr,0)=0 and l.pk_corp= ?  and l.businessdate<= ? ");
        sp.addParam(pk_corp);
        sp.addParam(end.toString());
        sb.append(")  asset ");

        return sb.toString();
    }


    private String hyColumnHandle(String newrule, Map<String, YntCpaccountVO> cpacodemap,
                                  Map<String, BdTradeAccountVO> tradecodemap, String hykmid) {
        YntCpaccountVO tempvo;
        BdTradeAccountVO hytempvo;
        String gscode;
        if (!StringUtil.isEmpty(hykmid)) {
            hytempvo = tradecodemap.get(hykmid);
            if (hytempvo != null) {
                gscode = gl_accountcoderule.getNewRuleCode(hytempvo.getAccountcode(), DZFConstant.ACCOUNTCODERULE,
                        newrule);
                tempvo = cpacodemap.get(gscode);
                if (tempvo != null) {
                    return tempvo.getPk_corp_account();
                }
            }
        }
        return "";
    }
}

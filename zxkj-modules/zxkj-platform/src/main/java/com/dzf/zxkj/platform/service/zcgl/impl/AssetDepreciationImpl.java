package com.dzf.zxkj.platform.service.zcgl.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepTemplate;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zcgl.BdTradeAssetTemplateBVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IAssetCard;
import com.dzf.zxkj.platform.service.zcgl.IAssetDepreciation;
import com.dzf.zxkj.platform.service.zcgl.IAssetTemplet;
import com.dzf.zxkj.platform.service.zcgl.IZcCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 资产卡片资产清理时，折旧业务处理
 *
 * @author dzf
 */
@Service("assetDepreciationImpl")
@SuppressWarnings("all")
public class AssetDepreciationImpl implements IAssetDepreciation {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Autowired
    private IZcCommonService zcCommonService;

    @Autowired
    private ICorpService corpService;

    /**
     * 固定资产清理之前做计提折旧
     *
     * @param assetcardVOs
     * @throws BusinessException
     */
    @Override
    public void clearProcessDep(CorpVO corpvo, String loginDate,
                                AssetcardVO[] assetcardVOs) throws BusinessException {
        // 对于固定资产，当月清理时仍计提折旧；无形资产和长期待摊费用，当月清理时不计提
        String period = DateUtils.getPeriod(new DZFDate(loginDate));
        AssetcardVO[] needDepVOs = getNeedAssetDepVOs(assetcardVOs, period);
        if (needDepVOs == null || needDepVOs.length == 0)
            return;

        processDepreciations(corpvo, loginDate, needDepVOs, period,
                DZFBoolean.TRUE);// 清理的计提折旧 modify by zhangj
    }

    /**
     * 获取需要进行计提折旧的固定资产
     *
     * @param assetVOs
     * @param period
     * @return
     * @throws BusinessException
     */
    private AssetcardVO[] getNeedAssetDepVOs(AssetcardVO[] assetVOs,
                                             String period) throws BusinessException {
        // // 对于固定资产，当月清理时仍计提折旧；无形资产和长期待摊费用，当月清理时不计提
        // HashMap<String, BdAssetCategoryVO> cateMap = new HashMap<String,
        // BdAssetCategoryVO>();
        ArrayList<AssetcardVO> resultVOs = new ArrayList<AssetcardVO>();

        DZFDate periodEndDate = DZFDate.getDate(period + "-01");
        periodEndDate = periodEndDate
                .getDateAfter(periodEndDate.getDaysMonth()).getDateBefore(1);
        IAssetCard assetCardImpl = (IAssetCard) SpringUtils
                .getBean("assetCardImpl");
        Map<String, BdAssetCategoryVO> catagoryMap = zcCommonService
                .queryAssetCategoryMap();
        for (int i = assetVOs.length - 1; i >= 0; i--) {
            AssetcardVO assetVO = assetVOs[i];
            // 净值小于或等于残值，不参与折旧
            DZFDouble assetnetvalue = getDZFDouble(assetVO.getAssetnetvalue());
            DZFDouble salvage = getDZFDouble(assetVO.getPlansalvage());
            if (assetnetvalue.compareTo(salvage) <= 0) {
                continue;
            }

            // 可使用月份<=累计折旧月份，不参与折旧
            int uselimit = getIntegerNullAsZero(assetVO.getUselimit());
            int depreciationPeriod = getIntegerNullAsZero(assetVO
                    .getDepreciationperiod());
            if (uselimit <= depreciationPeriod)
                continue;

            if (assetVO.getIsperiodbegin() != null
                    && assetVO.getIsperiodbegin().booleanValue()) { // 期初的要进行折旧
                resultVOs.add(assetVO);
            } else if (assetVO.getAccountdate().equals(periodEndDate)
                    || assetVO.getAccountdate().before(periodEndDate)) { // 入账日期是当期之前
                resultVOs.add(assetVO);
                BdAssetCategoryVO cateVO = catagoryMap.get(assetVO
                        .getAssetcategory());
                if (DateUtils.getPeriod(assetVO.getAccountdate())
                        .equals(period)
                        && (cateVO.getAssetproperty().equals(1) || cateVO
                        .getAssetproperty().equals(2))) // 无形资产和待摊费用
                    resultVOs.remove(assetVO);
            }
        }
        return resultVOs.toArray(new AssetcardVO[resultVOs.size()]);
    }

    private void processDepreciations(CorpVO corpvo, String loginDate,
                                      AssetcardVO[] vos, String period, DZFBoolean isclear)// add by
        // zhangj
        // 是否清理添加
            throws BusinessException {
        if ((vos == null) || (vos.length == 0))
            return;
        AssetcardVO[] assetcardVOs = (AssetcardVO[]) vos;
        checkDepCardVOs(assetcardVOs, period);

        // this.depTemplateMap.clear();
        DZFDate currDate = new DZFDate(loginDate);
        DZFDate businessDate = currDate;
        // if (!getPeriod(currDate).equals(period)) {//搞不懂这个比较什么意思，干掉
        // (计提利息，生成凭证时日期不对)modify by zhangj
        businessDate = DZFDate.getDate(period + "-01");
        businessDate = businessDate
                .getDateAfter(businessDate.getDaysMonth() - 1);
        // }

        // add by zhangj 如果资产清理则生成凭证的日期是当前登录日期
        if (isclear != null && isclear.booleanValue()) {
            businessDate = currDate;
        }
        // end 2015.6.10

        DZFDouble depmny = DZFDouble.ZERO_DBL;

        for (AssetcardVO assetcardVO : assetcardVOs) {
            if (period.equals(assetcardVO.getDepreciationdate()))
                continue;
            // depmny = calcDepreciationMny(assetcardVO);
            depmny = assetcardVO.getMonthzj();
            if (depmny.compareTo(DZFDouble.ZERO_DBL) <= 0) {
                continue;
            }
            updateAssetcard(assetcardVO, depmny, period);

            // 资产折旧明细
            AssetDepreciaTionVO assetdepVO = new AssetDepreciaTionVO();
            assetdepVO.setPk_assetcard(assetcardVO.getPrimaryKey());
            assetdepVO.setPk_corp(assetcardVO.getPk_corp());
            assetdepVO.setBusinessdate(businessDate);
            assetdepVO.setCoperatorid(assetcardVO.getCoperatorid());
            assetdepVO.setDoperatedate(currDate);
            assetdepVO.setOriginalvalue(depmny);
            assetdepVO.setAssetmny(assetcardVO.getAssetmny());
            assetdepVO.setDepreciationmny(assetcardVO.getDepreciation());
            assetdepVO.setAssetnetmny(assetcardVO.getAssetnetvalue());
            assetdepVO = (AssetDepreciaTionVO) singleObjectBO.saveObject(
                    assetdepVO.getPk_corp(), assetdepVO);

            processAssetDepToGL(corpvo, assetdepVO, assetcardVO);
        }
    }

    private void updateAssetcard(AssetcardVO assetcardVO, DZFDouble depmny,
                                 String period) throws BusinessException {
        int accdepPeriod = getIntegerNullAsZero(assetcardVO
                .getAccountdepreciationperiod());
        assetcardVO.setAccountdepreciationperiod(Integer
                .valueOf(accdepPeriod + 1));

        int accusePeriod = getIntegerNullAsZero(assetcardVO
                .getAccountusedperiod());
        assetcardVO.setAccountusedperiod(Integer.valueOf(accusePeriod + 1));

        DZFDouble accdepmny = getDZFDouble(assetcardVO.getAccountdepreciation());
        assetcardVO.setAccountdepreciation(accdepmny.add(depmny));

        DZFDouble totaldepmny = getDZFDouble(assetcardVO.getDepreciation());
        assetcardVO.setDepreciation(totaldepmny.add(depmny));

        int totalusePeriod = getIntegerNullAsZero(assetcardVO.getUsedperiod());
        assetcardVO.setUsedperiod(Integer.valueOf(totalusePeriod + 1));

        int totaldepPeriod = getIntegerNullAsZero(assetcardVO
                .getDepreciationperiod());
        assetcardVO.setDepreciationperiod(Integer.valueOf(totaldepPeriod + 1));

        DZFDouble assetnetvalue = getDZFDouble(assetcardVO.getAssetnetvalue());
        assetcardVO.setAssetnetvalue(assetnetvalue.sub(depmny));
        assetcardVO.setDepreciationdate(period);
        String[] fields = new String[]{"accountdepreciationperiod",
                "accountusedperiod", "accountdepreciation", "depreciation",
                "usedperiod", "depreciationperiod", "assetnetvalue",
                "depreciationdate"};

        singleObjectBO.update(assetcardVO, fields);
    }

    /**
     * 资产折旧明细生产凭证
     *
     * @param assetdepVO
     * @param assetcardVO
     * @throws BusinessException
     */
    @Override
    public void processAssetDepToGL(CorpVO corpvo,
                                    AssetDepreciaTionVO assetdepVO, AssetcardVO assetcardVO)
            throws BusinessException {
        String period = DateUtils.getPeriod(assetdepVO.getDoperatedate());
        if (assetdepVO.getIstogl() != null
                && assetdepVO.getIstogl().booleanValue())
            throw new BusinessException(String.format(
                    "资产卡片%s期间%s的累计折旧已经转总账，不允许重复转总账",
                    new Object[]{assetcardVO.getAssetcode(), period}));
        if (assetdepVO.getIssettle() != null
                && assetdepVO.getIssettle().booleanValue()) {
            throw new BusinessException(String.format(
                    "资产卡片%s期间%s的累计折旧已经结账，不允许转总账",
                    new Object[]{assetcardVO.getAssetcode(), period}));
        }
        HashMap<String, AssetDepTemplate[]> depTemplateMap = new HashMap<String, AssetDepTemplate[]>();
        BdAssetCategoryVO assetCategoryVO = getAssetCategoryVO(assetcardVO);
        // AssetDepTemplate template =
        // getAssetDepTemplate(assetcardVO.getPk_corp(),
        // assetCategoryVO,depTemplateMap);
        IAssetTemplet assetTempletImpl = (IAssetTemplet) SpringUtils
                .getBean("assetTempletImpl");
        AssetDepTemplate[] template = assetTempletImpl.getAssetDepTemplate(
                assetcardVO.getPk_corp(), 1, assetCategoryVO, depTemplateMap);
        String pk_voucher = createDepVoucher(corpvo, assetdepVO, assetcardVO,
                template, assetdepVO.getBusinessdate());

        assetdepVO.setIstogl(DZFBoolean.TRUE);
        assetdepVO.setPk_voucher(pk_voucher);
        singleObjectBO.update(assetdepVO,
                new String[]{"istogl", "pk_voucher"});
    }

    private BdAssetCategoryVO getAssetCategoryVO(AssetcardVO assetcardVO)
            throws BusinessException {
        return (BdAssetCategoryVO) singleObjectBO.queryVOByID(
                assetcardVO.getAssetcategory(), BdAssetCategoryVO.class);
    }

    /**
     * 生成凭证
     *
     * @param assetdepVO
     * @param assetcardVO
     * @param depTemplate
     * @param currDate
     * @return
     * @throws BusinessException
     */
    private String createDepVoucher(CorpVO corpvo,
                                    AssetDepreciaTionVO assetdepVO, AssetcardVO assetcardVO,
                                    AssetDepTemplate[] depTemplates, DZFDate currDate)
            throws BusinessException {
        TzpzHVO headVO = new TzpzHVO();
        headVO.setPk_corp(assetcardVO.getPk_corp());
        headVO.setPzlb(Integer.valueOf(0));
        headVO.setIshasjz(DZFBoolean.FALSE);
        headVO.setCoperatorid(assetcardVO.getCoperatorid());
        headVO.setDoperatedate(currDate);
        headVO.setPeriod(DateUtils.getPeriod(currDate));
        headVO.setVyear(currDate.getYear());
        headVO.setPzh(yntBoPubUtil.getNewVoucherNo(assetcardVO.getPk_corp(),
                headVO.getDoperatedate()));
        headVO.setSourcebillid(assetdepVO.getPrimaryKey());
        headVO.setSourcebilltype(IBillTypeCode.HP66);
        headVO.setVbillstatus(Integer.valueOf(8));
        headVO.setJfmny(assetdepVO.getOriginalvalue());
        headVO.setDfmny(assetdepVO.getOriginalvalue());

        String pk_currency = yntBoPubUtil.getCNYPk();
        TzpzBVO creditVO = new TzpzBVO();
        TzpzBVO debitVO = new TzpzBVO();
        for (AssetDepTemplate depTemplate : depTemplates) {
            if (depTemplate.getDirect() == BdTradeAssetTemplateBVO.DIRECT_CREDIT) {// 贷方
                debitVO.setPk_accsubj(depTemplate.getPk_account());
                debitVO.setZy(depTemplate.getAbstracts() + "-"
                        + assetcardVO.getAssetname());
                debitVO.setVcode(depTemplate.getSubcode());
                debitVO.setVname(depTemplate.getSubname());
                debitVO.setPk_currency(pk_currency);
                debitVO.setNrate(new DZFDouble(1));
                // debitVO.setJfmny(assetdepVO.getOriginalvalue());
                // debitVO.setYbjfmny(assetdepVO.getOriginalvalue());//贷方写借方去了，，谁写的，气死我了
                debitVO.setDfmny(assetdepVO.getOriginalvalue());
                debitVO.setYbdfmny(assetdepVO.getOriginalvalue());
                debitVO.setPk_corp(assetcardVO.getPk_corp());
                debitVO.setDr(0);
                debitVO.setRowno(2);
                debitVO.setVdirect(1);
            } else {// 借方
                creditVO.setPk_accsubj(depTemplate.getPk_account());
                creditVO.setZy(depTemplate.getAbstracts() + "-"
                        + assetcardVO.getAssetname());
                creditVO.setVcode(depTemplate.getSubcode());
                creditVO.setVname(depTemplate.getSubname());
                creditVO.setPk_currency(pk_currency);
                creditVO.setNrate(new DZFDouble(1));
                // creditVO.setDfmny(assetdepVO.getOriginalvalue());//贷方写借方去了，，谁写的，气死我了
                // creditVO.setYbdfmny(assetdepVO.getOriginalvalue());
                creditVO.setJfmny(assetdepVO.getOriginalvalue());
                creditVO.setYbjfmny(assetdepVO.getOriginalvalue());
                creditVO.setPk_corp(assetcardVO.getPk_corp());
                creditVO.setDr(0);
                creditVO.setRowno(1);
                creditVO.setVdirect(0);
            }
        }

        TzpzBVO[] children = new TzpzBVO[]{creditVO, debitVO};
        headVO.setChildren(children);

        IVoucherService gl_tzpzserv = (IVoucherService) SpringUtils
                .getBean("gl_tzpzserv");

        headVO = gl_tzpzserv.saveVoucher(corpvo, headVO);
        // headVO = (TzpzHVO)
        // singleObjectBO.saveObject(assetcardVO.getPk_corp(),headVO);
        return headVO.getPk_tzpz_h();
    }

    private void checkDepCardVOs(AssetcardVO[] assetcardVOs, String period)
            throws BusinessException {
        if ((assetcardVOs == null) || (assetcardVOs.length == 0))
            return;
        DZFDate perioddate = new DZFDate(period + "-01");
        DZFDate rowdate = new DZFDate();
        HashMap<String, DZFDate> corpBegindateMap = new HashMap<String, DZFDate>();
        // HashMap cateMap = new HashMap();
        ArrayList<String> checkCorps = new ArrayList<String>();
        HashMap<String, BdAssetCategoryVO> cateMap = queryAssetCategoryMap();
        for (AssetcardVO assetcardVO : assetcardVOs) {
            if (!checkCorps.contains(assetcardVO.getPk_corp())) {
                checkPeriodIsSettle(assetcardVO.getPk_corp(), perioddate);
                checkCorps.add(assetcardVO.getPk_corp());
            }

            if (StringUtil.isEmpty(assetcardVO.getDepreciationdate())) {
                if ((assetcardVO.getIsperiodbegin() != null)
                        && (assetcardVO.getIsperiodbegin().booleanValue())) {
                    if (!corpBegindateMap.containsKey(assetcardVO.getPk_corp())) {
                        CorpVO corpVO = corpService.queryByPk(
                                assetcardVO.getPk_corp());// (CorpVO)
                        // singleObjectBO.queryVOByID(
                        // assetcardVO.getPk_corp(), CorpVO.class);
                        if (corpVO == null)
                            throw new BusinessException(String.format(
                                    "找不到公司PK %s 对应的记录",
                                    new Object[]{assetcardVO.getPk_corp()}));
                        if (corpVO.getBusibegindate() == null)
                            throw new BusinessException(String.format(
                                    "公司 %s 的固定资产启用日期为空！",
                                    new Object[]{corpVO.getUnitname()}));
                        corpBegindateMap.put(assetcardVO.getPk_corp(),
                                corpVO.getBusibegindate());
                    }

                    rowdate = (DZFDate) corpBegindateMap.get(assetcardVO
                            .getPk_corp());
                    rowdate = rowdate.getDateBefore(rowdate.getDay());
                    rowdate = rowdate.getDateBefore(rowdate.getDay() - 1);
                } else {
                    BdAssetCategoryVO cateVO = (BdAssetCategoryVO) cateMap
                            .get(assetcardVO.getAssetcategory());
                    rowdate = assetcardVO.getAccountdate();
                    if (cateVO.getAssetproperty().equals(Integer.valueOf(0))) {
                        rowdate = rowdate.getDateBefore(rowdate.getDay() - 1);
                    } else {
                        rowdate = rowdate.getDateBefore(rowdate.getDay());
                        rowdate = rowdate.getDateBefore(rowdate.getDay() - 1);
                    }
                }
            } else
                rowdate = new DZFDate(assetcardVO.getDepreciationdate() + "-01");

            if (rowdate.before(perioddate)) {
                if (!perioddate.equals(rowdate.getDateAfter(rowdate
                        .getDaysMonth())))
                    throw new BusinessException(
                            String.format(
                                    "资产卡片%s在本期[%s]的上一期未进行计提折旧，本期不能进行资产清理！",
                                    new Object[]{assetcardVO.getAssetcode(),
                                            period}));
            } else if (rowdate.after(perioddate))
                throw new BusinessException(String.format(
                        "资产卡片%s最近计提折旧的期间[%s]大于本期[%s]，本期不能进行资产清理！",
                        new Object[]{assetcardVO.getAssetcode(),
                                assetcardVO.getDepreciationdate(), period}));
        }
    }

    private void checkPeriodIsSettle(String pk_corp, DZFDate date)
            throws BusinessException {
//		String period = DateUtils.getPeriod(date);
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(period);
//		String where = "pk_corp=? and period=? and nvl(dr,0)=0";
//		GdzcjzVO[] gdzcjzVOs = (GdzcjzVO[]) singleObjectBO.queryByCondition(
//				GdzcjzVO.class, where, sp);
//		if ((gdzcjzVOs != null) && (gdzcjzVOs.length > 0)
//				&& (gdzcjzVOs[0].getJzfinish() != null)
//				&& (gdzcjzVOs[0].getJzfinish().booleanValue()))
//			throw new BusinessException(String.format(
//					"月份%s 已经进行固定资产结账，不允许进行操作！", new Object[] { period }));
    }

    private HashMap<String, BdAssetCategoryVO> queryAssetCategoryMap()
            throws BusinessException {
        BdAssetCategoryVO[] vos = (BdAssetCategoryVO[]) singleObjectBO
                .queryByCondition(BdAssetCategoryVO.class, "nvl(dr,0)=0", null);
        HashMap<String, BdAssetCategoryVO> map = new HashMap<String, BdAssetCategoryVO>();
        if (vos != null && vos.length > 0) {
            for (BdAssetCategoryVO vo : vos) {
                map.put(vo.getPk_assetcategory(), vo);
            }
        }
        return map;
    }

    private DZFDouble getDZFDouble(DZFDouble obj) {
        return obj == null ? DZFDouble.ZERO_DBL : obj;
    }

    private int getIntegerNullAsZero(Integer value) {
        return value == null ? 0 : value.intValue();
    }

}

package com.dzf.zxkj.platform.services.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.GdzcjzVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.*;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.pzgl.IVoucherService;
import com.dzf.zxkj.platform.services.report.IFsYeReport;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.services.sys.IAccountService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.zcgl.*;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
@Service("assetCardImpl")
@SuppressWarnings("all")
public class AssetCardImpl implements IAssetCard {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IZclbService sys_zclbserv;
    @Autowired
    private IworkloadManagement am_workloadmagserv;
    @Autowired
    private IAssetcardReport am_rep_zcmxserv;
    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private ICpaccountService gl_cpacckmserv;

    @Autowired
    private IFsYeReport gl_rep_fsyebserv;

    @Autowired
    private IZcCommonService zcCommonService;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;

    @Override
    public void updateToGLState(String pk_assetCard, boolean istogl,
                                String pk_voucher, Integer ope) throws DZFWarpException {
        AssetcardVO[] vos = null;
        if (!StringUtil.isEmpty(pk_assetCard)) {
            AssetcardVO vo = (AssetcardVO) singleObjectBO.queryVOByID(pk_assetCard, AssetcardVO.class);
            if (vo == null) {
                throw new BusinessException("资产卡片已经被他人删除，请刷新界面");
            }
            vos = new AssetcardVO[]{vo};
        } else {//批量转总账走
            //根据凭证id来反查询资产id
            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_voucher);
            vos = (AssetcardVO[]) singleObjectBO.queryByCondition(AssetcardVO.class, "nvl(dr,0)=0 and pk_voucher= ? ", sp);
        }
        if (vos == null || vos.length == 0) {
            throw new BusinessException("资产卡片已经被他人删除，请刷新界面");
        }
        for (AssetcardVO vo : vos) {
            if (istogl && !StringUtil.isEmpty(vo.getPk_voucher()) && !pk_voucher.equals(vo.getPk_voucher())) {
                if ((vo.getIstogl() != null) && (vo.getIstogl().booleanValue())) {
                    throw new BusinessException(String.format(
                            "资产卡片%s已经生成凭证，不允许重复生成。",
                            new Object[]{vo.getAssetcode()}));
                }
            }
//			if ((vo.getIssettle() != null) && (vo.getIssettle().booleanValue()))
//				throw new BusinessException(String.format("资产卡片%s已经结账，不允许修改",
//						new Object[] { vo.getAssetcode() }));
            if ((vo.getIsclear() != null) && (vo.getIsclear().booleanValue())) {
                throw new BusinessException(String.format("资产卡片%s已经清理，不允许修改",
                        new Object[]{vo.getAssetcode()}));
            }

            if (!StringUtil.isEmpty(vo.getDepreciationdate()) &&
                    vo.getAccountdepreciationperiod() != null &&
                    vo.getAccountdepreciationperiod().intValue() > 0) {//折旧月份和折旧期间数都不为空的时候显示已经折旧
                throw new BusinessException(String.format("资产卡片%s已经计提折旧，不允许修改",
                        new Object[]{vo.getAssetcode()}));
            }

            //如果计提折旧，也不能删除凭证
            vo.setIstogl(new DZFBoolean(istogl));
            vo.setPk_voucher(ope == 0 ? pk_voucher : "");//删除是空

            singleObjectBO.update(vo, new String[]{"istogl", "pk_voucher"});
        }
    }

    @Override
    public void updateAssetMny(AssetcardVO vo, double assetmny, ValuemodifyVO modifyvo)
            throws DZFWarpException {
        // AssetcardVO vo = (AssetcardVO)
        // singleObjectBO.queryVOByID(pk_assetCard,
        // AssetcardVO.class);
        if (vo == null)
            throw new BusinessException("资产卡片已经被他人删除，请刷新界面");
        if ((vo.getIsclear() != null) && (vo.getIsclear().booleanValue()))
            throw new BusinessException(String.format("资产卡片%s已经清理，不允许做原值变更",
                    new Object[]{vo.getAssetcode()}));

        boolean bvalidate = true;
        if (modifyvo != null && !StringUtil.isEmpty(modifyvo.getPrimaryKey())) {
            ValuemodifyVO modifyqryvo = (ValuemodifyVO) singleObjectBO.queryByPrimaryKey(ValuemodifyVO.class, modifyvo.getPrimaryKey());
            if (modifyqryvo == null || (modifyqryvo.getDr() != null && modifyqryvo.getDr() == 1)) {//如果删除则不校验
                bvalidate = false;
            }
        }
        if (bvalidate && ((vo.getIsperiodbegin() == null) || (!vo.getIsperiodbegin().booleanValue()))
                && ((vo.getIstogl() == null) || (!vo.getIstogl().booleanValue())))
            throw new BusinessException(String.format("资产卡片%s未转总账，不允许做原值变更",
                    new Object[]{vo.getAssetcode()}));
        if (assetmny < 0.0D) {
            throw new BusinessException("资产原值不能小于0");
        }
        vo.setAssetmny(new DZFDouble(assetmny));

        double salvageratio = vo.getSalvageratio() == null ? 0.0D : vo
                .getSalvageratio().getDouble();
        double salvage = assetmny * salvageratio;
        vo.setPlansalvage(new DZFDouble(salvage));

        double depreciation = vo.getDepreciation() == null ? 0.0D : vo
                .getDepreciation().getDouble();
        double assetNetValue = assetmny - depreciation;
        if (assetNetValue < 0.0D)
            throw new BusinessException(String.format(
                    "资产卡片%s资产原值%f不能小于资产累计折旧值%f",
                    new Object[]{vo.getAssetcode(), Double.valueOf(assetmny),
                            Double.valueOf(depreciation)}));
        if (assetNetValue < salvage) {
            throw new BusinessException(String.format(
                    "资产卡片%s资产净值%f不能小于资产累计折旧值%f",
                    new Object[]{vo.getAssetcode(), Double.valueOf(salvage),
                            Double.valueOf(depreciation)}));
        }
        vo.setAssetnetvalue(new DZFDouble(assetNetValue));

        // 计算月折旧【(资产原值-残值)/使用月份】
        if (vo.getZjtype() == 0) { // --平均年限法
            DZFDouble plansalvage = DZFDouble.getUFDouble(vo.getPlansalvage());// 残值
            DZFDouble depreciation1 = DZFDouble.getUFDouble(vo
                    .getDepreciation());// 累计折旧
            Integer uselimit = vo.getUselimit();
            Integer depreciationperiod = vo.getDepreciationperiod();
            if (depreciationperiod == null) {
                depreciationperiod = 0;
            }
            Integer sub = uselimit.intValue() - depreciationperiod.intValue();
            DZFDouble assetmnyd = new DZFDouble(assetmny);
            DZFDouble monthzj = assetmnyd.sub(plansalvage).sub(depreciation1)
                    .div(sub);
            vo.setMonthzj(monthzj.setScale(2, DZFDouble.ROUND_HALF_UP));
            singleObjectBO.update(vo, new String[]{"assetmny", "plansalvage",
                    "assetnetvalue", "monthzj"});
        } else if (vo.getZjtype() == 1) { // --工作量法
            DZFDouble gzzl = DZFDouble.getUFDouble(vo.getGzzl());// 工作总量
            DZFDouble ljgzl = DZFDouble.getUFDouble(vo.getLjgzl());// 累计工作量
            DZFDouble sub = gzzl.sub(ljgzl);// 工作总量-累计工作量
            DZFDouble assetmnyd = new DZFDouble(assetmny); // 原值
            DZFDouble plansalvage = DZFDouble.getUFDouble(vo.getPlansalvage());// 残值
            DZFDouble depreciation1 = DZFDouble.getUFDouble(vo
                    .getDepreciation());// 累计折旧
            DZFDouble sub1 = assetmnyd.sub(plansalvage).sub(depreciation1);// 原值-残值-累计折旧
            DZFDouble setassetnetvalue = assetmnyd.sub(depreciation1);// 计算资产净值
            DZFDouble dwzj = sub1.div(sub);// 计算单位折旧
            vo.setDwzj(dwzj);
            vo.setAssetnetvalue(setassetnetvalue);
            singleObjectBO.update(vo, new String[]{"assetmny", "gzzl",
                    "ljgzl", "assetnetvalue", "dwzj", "plansalvage"});
        } else if (vo.getZjtype() == 2) { // --双倍余递减法
            Integer uselimit = vo.getUselimit(); // 预计使用年限（月）
            Integer uselimit1 = vo.getDepreciationperiod(); // 已计提折旧期间
            if (uselimit1 == null) {
                uselimit1 = 0;
            }
            DZFDouble plansalvage = DZFDouble.getUFDouble(vo.getPlansalvage());// 残值
            DZFDouble assetmnyd = new DZFDouble(assetmny); // 资产原值
            DZFDouble depreciation1 = DZFDouble.getUFDouble(vo
                    .getDepreciation());// 累计折旧

            if (uselimit.intValue() - uselimit1.intValue() > 24) { // --不是最后两年的算法
                DZFDouble sub = assetmnyd.sub(depreciation1);// 资产原值-累计折旧
                DZFDouble div = new DZFDouble(2).div(uselimit);// 2÷预计使用月×100%
                DZFDouble monthzj = sub.multiply(div);// 计算月折旧额
                vo.setMonthzj(monthzj);
            } else {// --最后两年算法
                DZFDouble sub = assetmnyd.sub(depreciation1).sub(plansalvage);// 固定资产原值-累计折旧-残值
                DZFDouble monthzj = sub.div(new DZFDouble(24));// 计算月折旧额
                vo.setMonthzj(monthzj);
            }

            singleObjectBO.update(vo, new String[]{"assetmny", "plansalvage",
                    "assetnetvalue", "monthzj"});
        } else {
            singleObjectBO.update(vo, new String[]{"assetmny", "plansalvage",
                    "assetnetvalue", "monthzj"});
        }
    }

    @Override
    public void updateIsClear(String[] pk_assetCards, boolean isclear)
            throws DZFWarpException {
        if ((pk_assetCards == null) || (pk_assetCards.length == 0))
            return;
        ArrayList<AssetcardVO> list = new ArrayList<AssetcardVO>();
        AssetcardVO vo = null;
        for (String pk : pk_assetCards) {
            vo = (AssetcardVO) singleObjectBO.queryByPrimaryKey(AssetcardVO.class, pk);
            if (vo == null) {
                throw new BusinessException("资产不存在\"pk\"");
            }
            vo.setPk_assetcard(pk);
            vo.setIsclear(new DZFBoolean(isclear));
            if (!isclear) {//会写资产净值和期初净值
                vo.setAssetnetvalue(SafeCompute.sub(vo.getAssetmny(), vo.getDepreciation()));
            }
            list.add(vo);
        }
        if (list != null && list.size() > 0) {
            singleObjectBO.updateAry(list.toArray(new AssetcardVO[0]),
                    new String[]{"isclear", "assetnetvalue"});
        }
    }

    /**
     * 更新固定资产是否清理标识
     */
    @Override
    public void updateIsClear(AssetcardVO assetcardVO, boolean isclear)
            throws DZFWarpException {
        if (assetcardVO != null) {
            assetcardVO.setIsclear(new DZFBoolean(isclear));
            //期初净值和资产净值
            assetcardVO.setAssetnetvalue(DZFDouble.ZERO_DBL);
            singleObjectBO.update(assetcardVO, new String[]{"isclear", "assetnetvalue"});
        }
    }

    private void checkPeriodIsSettle(String pk_corp, DZFDate date)
            throws DZFWarpException {
        //节点已隐藏，该校验不加
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
        //节点已隐藏，该校验不加
    }

    @Override
    public QmclVO updateDepreciate(CorpVO corpvo, QmclVO qmclvo, String coperatorid)
            throws DZFWarpException {
        if (qmclvo == null)
            return qmclvo;
        String period = qmclvo.getPeriod();
        AssetcardVO[] assetcardVOs = getCurrPeriodDepCardVOs(qmclvo, period);

        if (assetcardVOs != null && assetcardVOs.length > 0) {
            // 判断是否转总账
            StringBuilder cardno = new StringBuilder();
            for (AssetcardVO assetvo : assetcardVOs) {
                if (assetvo.getIstogl() == null || !assetvo.getIstogl().booleanValue()) {
                    if (assetvo.getIsperiodbegin() == null || !assetvo.getIsperiodbegin().booleanValue()) {
                        cardno.append(assetvo.getAssetcode() + ",");
                    }
                }
            }

            if (cardno.toString().length() > 0) {
                throw new BusinessException("存在未转总账的资产，资产号为:" + cardno.substring(0, cardno.length() - 1));
            }

            DZFDate date = DateUtils.getPeriodEndDate(period);

            processDepreciations(corpvo, date, assetcardVOs, period,
                    DZFBoolean.FALSE, coperatorid);// 非清理的计提折旧 modify by zhangj
        }
        List<QmclVO> insert = new ArrayList<QmclVO>();
        List<QmclVO> update = new ArrayList<QmclVO>();
        if ((qmclvo.getIszjjt() == null) || (!qmclvo.getIszjjt().booleanValue())) {
            qmclvo.setIszjjt(DZFBoolean.TRUE);
            if (StringUtil.isEmpty(qmclvo.getPrimaryKey())) {
                insert.add(qmclvo);
            } else {
                update.add(qmclvo);
            }
        }
        if (insert.size() > 0)
            singleObjectBO.insertVOArr(corpvo.getPk_corp(), insert.toArray(new QmclVO[0]));
        if (update.size() > 0)
            singleObjectBO.updateAry(update.toArray(new QmclVO[0]), new String[]{"iszjjt"});
        return qmclvo;
    }

    @Override
    public QmclVO rollbackDepreciate(QmclVO qmclVO)
            throws DZFWarpException {
        if (qmclVO == null)
            return qmclVO;
        String period = qmclVO.getPeriod();

        //折旧与资产关系
        HashMap<AssetDepreciaTionVO, AssetcardVO> map = getRollbackDepCardVOs(qmclVO, period);

        //查询折旧资产(非清理)
        String where = " nvl(dr,0)=0 and pk_corp = ?  and nvl(depreciationdate,'1900-10') <= ? and nvl(isclear,'N')='N' ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(qmclVO.getPk_corp());
        sp.addParam(period);
        AssetcardVO[] assetcardVOstemp1 = (AssetcardVO[]) singleObjectBO.queryByCondition(AssetcardVO.class, where, sp);

        //查询期初资产(非清理)
        String beginwhere = " nvl(dr,0)=0 and pk_corp = ? and nvl(isperiodbegin,'N')='Y'  and nvl(isclear,'N')='N' ";
        sp.clearParams();
        sp.addParam(qmclVO.getPk_corp());
        AssetcardVO[] assetcardVOstemp2 = (AssetcardVO[]) singleObjectBO.queryByCondition(AssetcardVO.class, beginwhere, sp);
        List<AssetcardVO> assetcardVOslist = new ArrayList<AssetcardVO>();
        List<String> pklistvalue1 = new ArrayList<String>();
        for (AssetcardVO assetvo : assetcardVOstemp1) {
            pklistvalue1.add(assetvo.getPrimaryKey());
            assetcardVOslist.add(assetvo);
        }
        if (assetcardVOstemp2 != null && assetcardVOstemp2.length > 0) {
            for (AssetcardVO assetvo : assetcardVOstemp2) {
                if (!pklistvalue1.contains(assetvo.getPrimaryKey())) {// 去除重复的
                    assetcardVOslist.add(assetvo);
                }
            }
        }

        AssetcardVO[] assetcardVOstemp = assetcardVOslist.toArray(new AssetcardVO[0]);
        List<String> containlist = new ArrayList<String>();// 已经反计提的主键
        List<AssetcardVO> assetcardVOliststemp = new ArrayList<AssetcardVO>();// 需要反计提的数据(会写折旧日期，使用月份等)

        if ((map != null) && (map.size() > 0)) {
            List<String> checkCorps = new ArrayList<String>();
            for (AssetDepreciaTionVO assetdepVO : map.keySet()) {

                AssetcardVO assetcardVO = (AssetcardVO) map.get(assetdepVO);

                if (!checkCorps.contains(assetcardVO.getPk_corp())) {
                    checkPeriodIsSettle(assetcardVO.getPk_corp(), new DZFDate(period + "-01"));
                    checkCorps.add(assetcardVO.getPk_corp());
                }

//				if ((assetdepVO.getIssettle() != null) && (assetdepVO.getIssettle().booleanValue())) {
//					throw new BusinessException(
//							String.format( "资产卡片%s期间%s的累计折旧已经结账，不允许删除折旧明细", 
//									new Object[] { assetcardVO.getAssetcode(), period }));
//				}
                if ((assetdepVO.getIstogl() != null) && (assetdepVO.getIstogl().booleanValue())) {
                    TzpzHVO tzpzVO = (TzpzHVO) singleObjectBO.queryByPrimaryKey(TzpzHVO.class, assetdepVO.getPk_voucher());
                    if (tzpzVO != null) {
                        if ((tzpzVO.getIshasjz() != null) && (tzpzVO.getIshasjz().booleanValue())) {
                            throw new BusinessException("凭证" + tzpzVO.getPzh() + "已记账，不能反操作");
                        }
                        if (tzpzVO.getVbillstatus().intValue() == 1) {
                            throw new BusinessException("凭证" + tzpzVO.getPzh() + "已审核，不能反操作");
                        }
                    }

                    singleObjectBO.executeUpdate(String.format(
                            "delete  from ynt_tzpz_b where pk_tzpz_h='%s'",
                            new Object[]{assetdepVO.getPk_voucher()}),
                            new SQLParameter());
                    singleObjectBO.executeUpdate(String.format(
                            "delete  from  ynt_tzpz_h where pk_tzpz_h='%s'",
                            new Object[]{assetdepVO.getPk_voucher()}),
                            new SQLParameter());
                }

                rollbackAssetcard(assetcardVO, period, assetdepVO.getOriginalvalue());
                containlist.add(assetcardVO.getPrimaryKey());// add by zhangj  计提折旧成功的

                singleObjectBO.deleteObject(assetdepVO);
            }
        }
        for (AssetcardVO cardvo : assetcardVOstemp) {
            if (!containlist.contains(cardvo.getPrimaryKey()) && cardvo.getDepreciationdate() != null) {
                int accusePeriod = cardvo.getAccountusedperiod() == null ? 0 : cardvo.getAccountusedperiod().intValue();
                if (accusePeriod == 0) {
                    cardvo.setAccountusedperiod(0);
                } else {
                    cardvo.setAccountusedperiod(Integer.valueOf(accusePeriod - 1));
                }
                assetcardVOliststemp.add(cardvo);
            }
        }
        if (assetcardVOliststemp.size() > 0) {
            singleObjectBO.updateAry(assetcardVOliststemp.toArray(new AssetcardVO[0]));
        }

        if ((qmclVO.getIszjjt() != null) && (qmclVO.getIszjjt().booleanValue())) {
            qmclVO.setIszjjt(DZFBoolean.FALSE);
            singleObjectBO.update(qmclVO, new String[]{"iszjjt"});
        }
        return qmclVO;
    }

    @Override
    public void processDepreciations(CorpVO corpvo, DZFDate loginDate,
                                     AssetcardVO[] assetcardVOs, String period, DZFBoolean isclear,
                                     String coperatorid)// add by zhangj 是否清理添加
            throws DZFWarpException {

        if ((assetcardVOs == null) || (assetcardVOs.length == 0))
            return;

        //过滤资产
        assetcardVOs = processCurrNewAssetVOs(assetcardVOs, period);

        checkDepCardVOs(assetcardVOs, period);

        DZFDate businessDate = loginDate;
        // if (!getPeriod(currDate).equals(period)) {//搞不懂这个比较什么意思，干掉
        // (计提利息，生成凭证时日期不对)modify by zhangj
        businessDate = DZFDate.getDate(period + "-01");
        businessDate = businessDate.getDateAfter(businessDate.getDaysMonth() - 1);
        // }

        // add by zhangj 如果资产清理则生成凭证的日期是当前登录日期
        if (isclear != null && isclear.booleanValue()) {
            businessDate = loginDate;
        }
        // end 2015.6.10

        createVoucher(corpvo, period, coperatorid, assetcardVOs, loginDate,
                businessDate, isclear);

    }

    private void createVoucher(CorpVO corpvo, String period,
                               String coperatorid, AssetcardVO[] assetcardVOs, DZFDate currDate,
                               DZFDate businessDate, DZFBoolean isclear) throws DZFWarpException {

        // 获取资产类别
        Map<String, BdAssetCategoryVO> categoryVoMap = new TreeMap<String, BdAssetCategoryVO>();
        Map<String, AssetDepreciaTionVO> assetDepreciaTionVOMap = new TreeMap<String, AssetDepreciaTionVO>();

        try {
            List<BdAssetCategoryVO> categoryVoList = null;
            BdAssetCategoryVO[] categoryVOs = sys_zclbserv
                    .queryAssetCategory(corpvo.getPk_corp());
            categoryVoList = new ArrayList<BdAssetCategoryVO>(
                    Arrays.asList(categoryVOs));
            if (categoryVoList != null && categoryVoList.size() > 0) {
                for (BdAssetCategoryVO cg : categoryVoList) {
                    categoryVoMap.put(cg.getPk_assetcategory(), cg);
                    processCircCategoryChildren(cg, categoryVoMap);
                }
            }
        } catch (BusinessException e) {
            log.error("失败", e);
        }
        DZFDouble depmny = DZFDouble.ZERO_DBL;
        Map<String, Map<String, List<AssetcardVO>>> assetcardToVoucherMap = new TreeMap<String, Map<String, List<AssetcardVO>>>();
        HashMap<String, AssetDepTemplate[]> depTemplateMap = new HashMap<String, AssetDepTemplate[]>();

        depmny = executeAssetCardCategory(corpvo, period, assetcardVOs,
                currDate, businessDate, categoryVoMap, assetDepreciaTionVOMap,
                depmny, assetcardToVoucherMap, depTemplateMap);

        // 循环assetcardToVoucherMap，生成凭证
        for (Map.Entry<String, Map<String, List<AssetcardVO>>> entry : assetcardToVoucherMap
                .entrySet()) {
            Map<String, List<AssetcardVO>> secondCategoryMap = entry.getValue();
            List<TzpzBVO> tzpzBVoList = new ArrayList<TzpzBVO>();
            List<AssetcardVO> assetcardVO2List = new ArrayList<AssetcardVO>();
            DZFDouble Jfmny = DZFDouble.ZERO_DBL;
            for (Map.Entry<String, List<AssetcardVO>> entry2 : secondCategoryMap
                    .entrySet()) {
                List<AssetcardVO> assetcardVOList = entry2.getValue();
                assetcardVO2List.addAll(assetcardVOList);
                Jfmny = Jfmny.add(createTzpzBVos(corpvo, categoryVoMap,
                        assetDepreciaTionVOMap, depTemplateMap,
                        assetcardVOList, tzpzBVoList, period));
            }

            // 保存凭证
            TzpzHVO headVO = saveVoucher(corpvo, coperatorid, currDate,
                    tzpzBVoList, Jfmny, isclear);

            // 更新折旧明细
            for (AssetcardVO assetcardVO : assetcardVO2List) {
                AssetDepreciaTionVO assetdepVO = assetDepreciaTionVOMap
                        .get(assetcardVO.getPk_assetcard());

                if (assetdepVO.getIstogl() != null
                        && assetdepVO.getIstogl().booleanValue()) {
                    throw new BusinessException(String.format(
                            "资产卡片%s期间%s的累计折旧已经生成凭证，不允许再次生成。", new Object[]{
                                    assetcardVO.getAssetcode(), period}));
                }
                if (assetdepVO.getIssettle() != null
                        && assetdepVO.getIssettle().booleanValue()) {
                    throw new BusinessException(String.format(
                            "资产卡片%s期间%s的累计折旧已经结账，不允许删除折旧明细", new Object[]{
                                    assetcardVO.getAssetcode(), period}));
                }

                assetdepVO.setIstogl(DZFBoolean.TRUE);
                assetdepVO.setPk_voucher(headVO.getPk_tzpz_h());
                singleObjectBO.update(assetdepVO, new String[]{"istogl",
                        "pk_voucher"});
            }
        }
    }

    private TzpzHVO saveVoucher(CorpVO corpvo, String coperatorid,
                                DZFDate currDate, List<TzpzBVO> tzpzBVoList, DZFDouble Jfmny, DZFBoolean isclear)
            throws DZFWarpException {
        TzpzHVO headVO = new TzpzHVO();
        headVO.setPk_corp(corpvo.getPk_corp());
        headVO.setPzlb(Integer.valueOf(0));
        headVO.setIshasjz(DZFBoolean.FALSE);
        headVO.setCoperatorid(coperatorid);
        headVO.setDoperatedate(currDate);
        headVO.setPeriod(DateUtils.getPeriod(currDate));
        headVO.setVyear(currDate.getYear());
        headVO.setPzh(yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(),
                headVO.getDoperatedate()));
        // headVO.setSourcebillid(assetdepVO.getPrimaryKey());
        if (isclear != null && isclear.booleanValue()) {//来源资产清理
            headVO.setSourcebilltype(IBillTypeCode.HP66);//
        } else {
            headVO.setSourcebilltype(IBillTypeCode.HP67);
        }
        headVO.setVbillstatus(Integer.valueOf(8));
        headVO.setJfmny(Jfmny);
        headVO.setDfmny(Jfmny);

        TzpzBVO[] children = (TzpzBVO[]) tzpzBVoList
                .toArray(new TzpzBVO[tzpzBVoList.size()]);
        headVO.setChildren(children);

        IVoucherService gl_tzpzserv = (IVoucherService) SpringUtils
                .getBean("gl_tzpzserv");
        headVO = gl_tzpzserv.saveVoucher(corpvo, headVO);
        return headVO;
    }

    private void processCircCategoryChildren(BdAssetCategoryVO cg,
                                             Map<String, BdAssetCategoryVO> categoryVoMap) {
        if (cg.getChildren() != null && cg.getChildren().length > 0) {
            for (BdAssetCategoryVO c : cg.getChildren()) {
                processCircCategoryChildren(c, categoryVoMap);
            }
        }
        categoryVoMap.put(cg.getPk_assetcategory(), cg);
    }

    private DZFDouble createTzpzBVos(CorpVO corpvo,
                                     Map<String, BdAssetCategoryVO> categoryVoMap,
                                     Map<String, AssetDepreciaTionVO> assetDepreciaTionVOMap,
                                     HashMap<String, AssetDepTemplate[]> depTemplateMap,
                                     List<AssetcardVO> assetcardVOList, List<TzpzBVO> tzpzBVoList,
                                     String period) throws DZFWarpException {
        DZFDouble Jfmny;
        TzpzBVO creditVO = null;
        TzpzBVO debitVO = null;
        Jfmny = DZFDouble.ZERO_DBL;
        int rowno = 0;

        Map<String, TzpzBVO> keymap = new HashMap<String, TzpzBVO>();
        for (TzpzBVO bvo : tzpzBVoList) {
            String dir;
            if (bvo.getJfmny() != null && bvo.getJfmny().doubleValue() > 0) {
                dir = "" + 0;// 借方
            } else {
                dir = "" + 1;// 贷方
            }
            keymap.put(dir + "_" + bvo.getPk_accsubj(), bvo);
        }

        Map<String, YntCpaccountVO> cpamap = accountService.queryMapByPk(corpvo.getPk_corp());
        for (AssetcardVO assetcardVO : assetcardVOList) {
            // 生成凭证, 查询资产类型编码
            if (categoryVoMap.get(assetcardVO.getAssetcategory()) == null) {
                throw new BusinessException(String.format("资产类别错误！"));
            }
            String key = corpvo.getPk_corp() + assetcardVO.getAssetcategory();
            AssetDepTemplate[] depTemplates = depTemplateMap.get(key);
            String pk_currency = yntBoPubUtil.getCNYPk();
            AssetDepreciaTionVO assetdepVO = assetDepreciaTionVOMap
                    .get(assetcardVO.getPk_assetcard());
            if (!StringUtil.isEmpty(assetcardVO.getPk_jtzjkm())
                    && !StringUtil.isEmpty(assetcardVO.getPk_zjfykm())) {
                if (cpamap.get(assetcardVO.getPk_jtzjkm()) == null
                        || cpamap.get(assetcardVO.getPk_zjfykm()) == null) {
                    throw new BusinessException("折旧科目不存在!");
                }
                // 借方
                if (keymap.containsKey((0 + "_" + assetcardVO.getPk_zjfykm()))) {
                    for (TzpzBVO bbvo : tzpzBVoList) {
                        String dir;
                        if (bbvo.getJfmny() != null
                                && bbvo.getJfmny().doubleValue() > 0) {
                            dir = "" + 0;// 借方
                        } else {
                            dir = "" + 1;// 贷方
                        }
                        String keytemp = dir + "_" + bbvo.getPk_accsubj();

                        if (keytemp.equals((0 + "_" + assetcardVO
                                .getPk_zjfykm()))) {
                            bbvo.setJfmny(SafeCompute.add(bbvo.getJfmny(),
                                    assetdepVO.getOriginalvalue()));
                            bbvo.setYbjfmny(SafeCompute.add(bbvo.getYbjfmny(),
                                    assetdepVO.getOriginalvalue()));
                            break;
                        }
                    }
                } else {
                    creditVO = new TzpzBVO();
                    Jfmny = Jfmny.add(assetdepVO.getOriginalvalue());
                    creditVO.setPk_accsubj(assetcardVO.getPk_zjfykm());
                    creditVO.setZy(period + "月折旧(摊销)");
                    creditVO.setVcode(cpamap.get(assetcardVO.getPk_zjfykm())
                            .getAccountcode());
                    creditVO.setVname(cpamap.get(assetcardVO.getPk_zjfykm())
                            .getAccountname());
                    creditVO.setPk_currency(pk_currency);
                    creditVO.setNrate(new DZFDouble(1));
                    creditVO.setJfmny(assetdepVO.getOriginalvalue());
                    creditVO.setYbjfmny(assetdepVO.getOriginalvalue());
                    creditVO.setPk_corp(assetcardVO.getPk_corp());
                    creditVO.setDr(0);
                    creditVO.setRowno(2 * rowno + 1);
                    creditVO.setVdirect(0);
                    keymap.put(0 + "_" + assetcardVO.getPk_zjfykm(), creditVO);
                    tzpzBVoList.add(creditVO);
                }

                // 贷方
                if (keymap.containsKey((1 + "_" + assetcardVO.getPk_jtzjkm()))) {
                    for (TzpzBVO bbvo : tzpzBVoList) {
                        String dir;
                        if (bbvo.getJfmny() != null
                                && bbvo.getJfmny().doubleValue() > 0) {
                            dir = "" + 0;// 借方
                        } else {
                            dir = "" + 1;// 贷方
                        }
                        String keytemp = dir + "_" + bbvo.getPk_accsubj();

                        if (keytemp.equals((1 + "_" + assetcardVO
                                .getPk_jtzjkm()))) {
                            bbvo.setDfmny(SafeCompute.add(bbvo.getDfmny(),
                                    assetdepVO.getOriginalvalue()));
                            bbvo.setYbdfmny(SafeCompute.add(bbvo.getYbdfmny(),
                                    assetdepVO.getOriginalvalue()));
                            break;
                        }
                    }
                } else {
                    debitVO = new TzpzBVO();
                    debitVO.setPk_accsubj(assetcardVO.getPk_jtzjkm());
                    debitVO.setZy(period + "月折旧(摊销)");
                    debitVO.setVcode(cpamap.get(assetcardVO.getPk_jtzjkm())
                            .getAccountcode());
                    debitVO.setVname(cpamap.get(assetcardVO.getPk_jtzjkm())
                            .getAccountname());
                    debitVO.setPk_currency(pk_currency);
                    debitVO.setNrate(new DZFDouble(1));
                    debitVO.setDfmny(assetdepVO.getOriginalvalue());
                    debitVO.setYbdfmny(assetdepVO.getOriginalvalue());
                    debitVO.setPk_corp(assetcardVO.getPk_corp());
                    debitVO.setDr(0);
                    debitVO.setRowno(2 * rowno + 2);
                    debitVO.setVdirect(1);
                    keymap.put(1 + "_" + assetcardVO.getPk_jtzjkm(), debitVO);
                    tzpzBVoList.add(debitVO);
                }

            } else {
                for (AssetDepTemplate depTemplate : depTemplates) {
                    if (depTemplate.getDirect() == BdTradeAssetTemplateBVO.DIRECT_CREDIT) {// 贷方
                        if (keymap.containsKey((1 + "_" + depTemplate
                                .getPk_account()))) {
                            for (TzpzBVO bbvo : tzpzBVoList) {
                                String dir;
                                if (bbvo.getJfmny() != null
                                        && bbvo.getJfmny().doubleValue() > 0) {
                                    dir = "" + 0;// 借方
                                } else {
                                    dir = "" + 1;// 贷方
                                }
                                String keytemp = dir + "_"
                                        + bbvo.getPk_accsubj();

                                if (keytemp.equals((1 + "_" + depTemplate
                                        .getPk_account()))) {
                                    bbvo.setDfmny(SafeCompute.add(
                                            bbvo.getDfmny(),
                                            assetdepVO.getOriginalvalue()));
                                    bbvo.setYbdfmny(SafeCompute.add(
                                            bbvo.getYbdfmny(),
                                            assetdepVO.getOriginalvalue()));
                                    break;
                                }
                            }
                        } else {
                            debitVO = new TzpzBVO();
                            debitVO.setPk_accsubj(depTemplate.getPk_account());
                            debitVO.setZy(depTemplate.getAbstracts()
                                    + "-"
                                    + categoryVoMap.get(
                                    assetcardVO.getAssetcategory())
                                    .getCatename());
                            debitVO.setVcode(depTemplate.getSubcode());
                            debitVO.setVname(depTemplate.getSubname());
                            debitVO.setPk_currency(pk_currency);
                            debitVO.setNrate(new DZFDouble(1));
                            debitVO.setDfmny(assetdepVO.getOriginalvalue());
                            debitVO.setYbdfmny(assetdepVO.getOriginalvalue());
                            debitVO.setPk_corp(assetcardVO.getPk_corp());
                            debitVO.setDr(0);
                            debitVO.setRowno(2 * rowno + 2);
                            debitVO.setVdirect(1);
                            keymap.put(1 + "_" + depTemplate.getPk_account(),
                                    debitVO);
                            tzpzBVoList.add(debitVO);
                        }
                    } else {// 借方

                        if (keymap.containsKey((0 + "_" + depTemplate
                                .getPk_account()))) {
                            for (TzpzBVO bbvo : tzpzBVoList) {
                                String dir;
                                if (bbvo.getJfmny() != null
                                        && bbvo.getJfmny().doubleValue() > 0) {
                                    dir = "" + 0;// 借方
                                } else {
                                    dir = "" + 1;// 贷方
                                }
                                String keytemp = dir + "_"
                                        + bbvo.getPk_accsubj();

                                if (keytemp.equals((0 + "_" + depTemplate
                                        .getPk_account()))) {
                                    bbvo.setJfmny(SafeCompute.add(
                                            bbvo.getJfmny(),
                                            assetdepVO.getOriginalvalue()));
                                    bbvo.setYbjfmny(SafeCompute.add(
                                            bbvo.getYbjfmny(),
                                            assetdepVO.getOriginalvalue()));
                                    break;
                                }
                            }
                        } else {
                            creditVO = new TzpzBVO();
                            Jfmny = Jfmny.add(assetdepVO.getOriginalvalue());
                            creditVO.setPk_accsubj(depTemplate.getPk_account());
                            creditVO.setZy(depTemplate.getAbstracts()
                                    + "-"
                                    + categoryVoMap.get(
                                    assetcardVO.getAssetcategory())
                                    .getCatename());
                            creditVO.setVcode(depTemplate.getSubcode());
                            creditVO.setVname(depTemplate.getSubname());
                            creditVO.setPk_currency(pk_currency);
                            creditVO.setNrate(new DZFDouble(1));
                            creditVO.setJfmny(assetdepVO.getOriginalvalue());
                            creditVO.setYbjfmny(assetdepVO.getOriginalvalue());
                            creditVO.setPk_corp(assetcardVO.getPk_corp());
                            creditVO.setDr(0);
                            creditVO.setRowno(2 * rowno + 1);
                            creditVO.setVdirect(0);
                            keymap.put(0 + "_" + depTemplate.getPk_account(),
                                    creditVO);
                            tzpzBVoList.add(creditVO);
                        }
                    }
                }
            }
            rowno++;
        }
        // } catch (BusinessException e) {
        // //e.printStackTrace();
        // throw new BusinessException(e);
        // }

        return Jfmny;
    }


    private DZFDouble executeAssetCardCategory(CorpVO corpvo, String period,
                                               AssetcardVO[] assetcardVOs, DZFDate currDate, DZFDate businessDate,
                                               Map<String, BdAssetCategoryVO> categoryVoMap,
                                               Map<String, AssetDepreciaTionVO> assetDepreciaTionVOMap,
                                               DZFDouble depmny,
                                               Map<String, Map<String, List<AssetcardVO>>> assetcardToVoucherMap,
                                               HashMap<String, AssetDepTemplate[]> depTemplateMap)
            throws DZFWarpException {
        for (AssetcardVO assetcardVO : assetcardVOs) {
            int uselimit = 0;
            int uselimit1 = 0;
            if (assetcardVO.getZjtype() != 1) {
                uselimit = assetcardVO.getUselimit(); // 预计使用年限（月）
                uselimit1 = assetcardVO.getDepreciationperiod() == null ? 0
                        : assetcardVO.getDepreciationperiod(); // 已计提折旧期间
            }

            if (period.equals(assetcardVO.getDepreciationdate()))
                continue;
            // depmny = calcDepreciationMny(assetcardVO);
            depmny = assetcardVO.getMonthzj();
            // 工作量用完不再计提
            if (assetcardVO.getZjtype() == 1) {
                if (assetcardVO.getGzzl().getDouble() <= assetcardVO.getLjgzl()
                        .getDouble()) {
                    continue;
                }
            }
            if (depmny == null || assetcardVO.getZjtype() != 0) {
                depmny = calcDepreciationMny(assetcardVO, period);
            }
            if (assetcardVO.getZjtype() == 2) {

                int month = uselimit - uselimit1;
                if (month < 24) {
                    depmny = assetcardVO.getMonthzj();
                }

            }
            // 平均年限和双倍余额法最后一个月折旧--倒挤操作
            if (assetcardVO.getZjtype() != 1 && uselimit - uselimit1 == 1) {
                depmny = assetcardVO
                        .getAssetnetvalue()
                        .sub(assetcardVO.getPlansalvage() == null ? DZFDouble.ZERO_DBL
                                : assetcardVO.getPlansalvage());
            }
            // 工作量法剩余工作量为0--倒挤操作
            DZFDouble sygzl = getsygzl(assetcardVO, period);
            if (assetcardVO.getZjtype() == 1 && sygzl == DZFDouble.ZERO_DBL) {
                depmny = SafeCompute.sub(assetcardVO.getAssetnetvalue(), assetcardVO.getPlansalvage());
            }

            //如果是当月一次计提折旧，则折旧额等于资产净值-预估残值
            if (assetcardVO.getOnetimedep() != null && assetcardVO.getOnetimedep().booleanValue()) {
                depmny = SafeCompute.sub(assetcardVO.getAssetnetvalue(), assetcardVO.getPlansalvage());
            }

            if (depmny.compareTo(DZFDouble.ZERO_DBL) <= 0
                    && assetcardVO.getZjtype() != 1) {
                continue;
            }

            // getBygzl(assetcardVO, period);
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

            assetDepreciaTionVOMap
                    .put(assetdepVO.getPk_assetcard(), assetdepVO);
            if (assetdepVO.getIstogl() != null
                    && assetdepVO.getIstogl().booleanValue())
                throw new BusinessException(String.format(
                        "资产卡片%s期间%s的累计折旧已经转总账，不允许重复转总账", new Object[]{
                                assetcardVO.getAssetcode(), period}));
            if (assetdepVO.getIssettle() != null
                    && assetdepVO.getIssettle().booleanValue()) {
                throw new BusinessException(String.format(
                        "资产卡片%s期间%s的累计折旧已经结账，不允许转总账", new Object[]{
                                assetcardVO.getAssetcode(), period}));
            }

            if (categoryVoMap.get(assetcardVO.getAssetcategory()) != null
                    && categoryVoMap.get(assetcardVO.getAssetcategory())
                    .getCatecode() != null
                    && categoryVoMap.get(assetcardVO.getAssetcategory())
                    .getCatecode().length() > 3) {

                mergeLevelOne(corpvo, categoryVoMap, assetcardToVoucherMap, depTemplateMap, assetcardVO);
//				mergeLevelTwo(corpvo, categoryVoMap, assetcardToVoucherMap, depTemplateMap, assetcardVO);
            } else {
                throw new BusinessException(String.format("资产类别错误！"));
            }
            // assetDepreciationImpl.processAssetDepToGL(corpvo, assetdepVO,
            // assetcardVO);
        }
        return depmny;
    }

    private void mergeLevelOne(CorpVO corpvo, Map<String, BdAssetCategoryVO> categoryVoMap,
                               Map<String, Map<String, List<AssetcardVO>>> assetcardToVoucherMap,
                               HashMap<String, AssetDepTemplate[]> depTemplateMap, AssetcardVO assetcardVO) {
        // 1级分类合并
        String secondCategory = categoryVoMap
                .get(assetcardVO.getAssetcategory()).getCatecode()
                .substring(0, 1);
        if (assetcardToVoucherMap.get(secondCategory) == null) {
            assetcardToVoucherMap.put(secondCategory,
                    new TreeMap<String, List<AssetcardVO>>());
        }
        // 查找模板
        IAssetTemplet assetTempletImpl = (IAssetTemplet) SpringUtils
                .getBean("assetTempletImpl");
        // 如果卡片存在折旧类型，则不查询模板
        if (StringUtil.isEmpty(assetcardVO.getPk_jtzjkm())
                || StringUtil.isEmpty(assetcardVO.getPk_zjfykm())) {
            AssetDepTemplate[] template = assetTempletImpl
                    .getAssetDepTemplate(assetcardVO.getPk_corp(), 1,
                            categoryVoMap.get(assetcardVO
                                    .getAssetcategory()),
                            depTemplateMap);
            // 多个模板，使用最后一个
            if (template == null || template.length == 0) {
                throw new BusinessException(String.format("未找到模板！"));
            }
        }
        String key = corpvo.getPk_corp()
                + categoryVoMap.get(assetcardVO.getAssetcategory())
                .getPrimaryKey();
        if (assetcardToVoucherMap.get(secondCategory).get(key) == null) {
            assetcardToVoucherMap.get(secondCategory).put(key,
                    new ArrayList<AssetcardVO>());
        }
        assetcardToVoucherMap.get(secondCategory).get(key)
                .add(assetcardVO);
    }

    private void mergeLevelTwo(CorpVO corpvo, Map<String, BdAssetCategoryVO> categoryVoMap,
                               Map<String, Map<String, List<AssetcardVO>>> assetcardToVoucherMap,
                               HashMap<String, AssetDepTemplate[]> depTemplateMap, AssetcardVO assetcardVO) {
        // 2级分类合并
        String secondCategory = categoryVoMap
                .get(assetcardVO.getAssetcategory()).getCatecode()
                .substring(0, 4);
        if (assetcardToVoucherMap.get(secondCategory) == null) {
            assetcardToVoucherMap.put(secondCategory,
                    new TreeMap<String, List<AssetcardVO>>());
        }
        // 查找模板
        IAssetTemplet assetTempletImpl = (IAssetTemplet) SpringUtils
                .getBean("assetTempletImpl");
        // 如果卡片存在折旧类型，则不查询模板
        if (StringUtil.isEmpty(assetcardVO.getPk_jtzjkm())
                || StringUtil.isEmpty(assetcardVO.getPk_zjfykm())) {
            AssetDepTemplate[] template = assetTempletImpl
                    .getAssetDepTemplate(assetcardVO.getPk_corp(), 1,
                            categoryVoMap.get(assetcardVO
                                    .getAssetcategory()),
                            depTemplateMap);
            // 多个模板，使用最后一个
            if (template == null || template.length == 0) {
                throw new BusinessException(String.format("未找到模板！"));
            }
        }
        String key = corpvo.getPk_corp()
                + categoryVoMap.get(assetcardVO.getAssetcategory())
                .getPrimaryKey();
        if (assetcardToVoucherMap.get(secondCategory).get(key) == null) {
            assetcardToVoucherMap.get(secondCategory).put(key,
                    new ArrayList<AssetcardVO>());
        }
        assetcardToVoucherMap.get(secondCategory).get(key)
                .add(assetcardVO);
    }

    /**
     * 资产折旧明细生产凭证
     *
     * @param assetdepVO
     * @param assetcardVO
     * @throws BusinessException
     */
    // private void processAssetDepToGL(AssetDepreciaTionVO assetdepVO,
    // AssetcardVO assetcardVO) throws BusinessException {
    // String period = DateUtils.getPeriod(assetdepVO.getDoperatedate());
    // if (assetdepVO.getIstogl() != null &&
    // assetdepVO.getIstogl().booleanValue())
    // throw new BusinessException(String.format(
    // "资产卡片%s期间%s的累计折旧已经转总账，不允许重复转总账",
    // new Object[] { assetcardVO.getAssetcode(), period }));
    // if (assetdepVO.getIssettle() != null &&
    // assetdepVO.getIssettle().booleanValue()) {
    // throw new BusinessException(String.format(
    // "资产卡片%s期间%s的累计折旧已经结账，不允许转总账",
    // new Object[] { assetcardVO.getAssetcode(), period }));
    // }
    // HashMap<String, AssetDepTemplate> depTemplateMap = new HashMap<String,
    // AssetDepTemplate>();
    // BdAssetCategoryVO assetCategoryVO = getAssetCategoryVO(assetcardVO);
    // // AssetDepTemplate template =
    // getAssetDepTemplate(assetcardVO.getPk_corp(),
    // assetCategoryVO,depTemplateMap);
    // IAssetTemplet assetTempletImpl = (IAssetTemplet)
    // SpringUtils.getBean("assetTempletImpl");
    // AssetDepTemplate template =
    // assetTempletImpl.getAssetDepTemplate(assetcardVO.getPk_corp(),
    // assetCategoryVO, depTemplateMap);
    // String pk_voucher = createDepVoucher(assetdepVO, assetcardVO,
    // template,assetdepVO.getBusinessdate());
    //
    // assetdepVO.setIstogl(DZFBoolean.TRUE);
    // assetdepVO.setPk_voucher(pk_voucher);
    // singleObjectBO.update(assetdepVO,new String[]{"istogl","pk_voucher"});
    // }

    // private Map<String, YntCpaccountVO> queryKmBycorp(String pk_corp){
    // Map<String, YntCpaccountVO> map = queryMap(YntCpaccountVO.class,pk_corp);
    // return map;
    // }

    // public<T> Map<String,T> queryMap(Class className,String pk_corp) throws
    // BusinessException{
    // Map<String,T> rsmap = new HashMap<String,T>();
    // try{
    // SQLParameter sp=new SQLParameter();
    // sp.addParam(pk_corp);
    // List<T> listVo = (List<T>)
    // getSingleObjectBO().retrieveByClause(className, "pk_corp=?", sp);
    // if(listVo != null && listVo.size() > 0){
    // for(T pvo : listVo){
    // rsmap.put(((SuperVO)pvo).getPrimaryKey(), pvo);
    // }
    // }
    // }catch(Exception e){
    // throw new BusinessException(e);
    // }
    // return rsmap;
    // }

    // private BdAssetCategoryVO getAssetCategoryVO(AssetcardVO assetcardVO)
    // throws BusinessException {
    // return (BdAssetCategoryVO) singleObjectBO.queryVOByID(
    // assetcardVO.getAssetcategory(), BdAssetCategoryVO.class);
    // }
//    @Override
//    public HashMap<String, BdAssetCategoryVO> queryAssetCategoryMap()
//            throws DZFWarpException {
//        BdAssetCategoryVO[] vos = (BdAssetCategoryVO[]) singleObjectBO
//                .queryByCondition(BdAssetCategoryVO.class, "nvl(dr,0)=0", null);
//        HashMap<String, BdAssetCategoryVO> map = new HashMap<String, BdAssetCategoryVO>();
//        if (vos != null && vos.length > 0) {
//            for (BdAssetCategoryVO vo : vos) {
//                map.put(vo.getPk_assetcategory(), vo);
//            }
//        }
//        return map;
//    }

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
    // private String createDepVoucher(AssetDepreciaTionVO assetdepVO,
    // AssetcardVO assetcardVO, AssetDepTemplate depTemplate,
    // DZFDate currDate) throws BusinessException {
    // TzpzHVO headVO = new TzpzHVO();
    // headVO.setPk_corp(assetcardVO.getPk_corp());
    // headVO.setPzlb(Integer.valueOf(0));
    // headVO.setIshasjz(DZFBoolean.FALSE);
    // headVO.setCoperatorid(assetcardVO.getCoperatorid());
    // headVO.setDoperatedate(currDate);
    // headVO.setPeriod(DateUtils.getPeriod(currDate));
    // headVO.setVyear(currDate.getYear());
    // headVO.setPzh(yntBoPubUtil.getNewVoucherNo(assetcardVO.getPk_corp(),
    // headVO.getDoperatedate()));
    // headVO.setSourcebillid(assetdepVO.getPrimaryKey());
    // headVO.setSourcebilltype(IBillTypeCode.HP66);
    // headVO.setVbillstatus(Integer.valueOf(8));
    // headVO.setJfmny(assetdepVO.getOriginalvalue());
    // headVO.setDfmny(assetdepVO.getOriginalvalue());
    //
    // String pk_currency = yntBoPubUtil.getCNYPk();
    // TzpzBVO creditVO = new TzpzBVO();
    // creditVO.setPk_accsubj(depTemplate.pk_creditAccount);
    // creditVO.setZy(depTemplate.creditAbstracts + "-" +
    // assetcardVO.getAssetname());
    // creditVO.setVcode(depTemplate.credit_subcode);
    // creditVO.setVname(depTemplate.credit_subname);
    // creditVO.setPk_currency(pk_currency);
    // creditVO.setNrate(new DZFDouble(1));
    // creditVO.setDfmny(assetdepVO.getOriginalvalue());
    // creditVO.setYbdfmny(assetdepVO.getOriginalvalue());
    // creditVO.setPk_corp(assetcardVO.getPk_corp());
    // creditVO.setDr(0);
    // creditVO.setRowno(1);
    // creditVO.setVdirect(0);
    //
    // TzpzBVO debitVO = new TzpzBVO();
    // debitVO.setPk_accsubj(depTemplate.pk_debitAccount);
    // debitVO.setZy(depTemplate.debitAbstracts + "-" +
    // assetcardVO.getAssetname());
    // debitVO.setVcode(depTemplate.debit_subcode);
    // debitVO.setVname(depTemplate.debit_subname);
    // debitVO.setPk_currency(pk_currency);
    // debitVO.setNrate(new DZFDouble(1));
    // debitVO.setJfmny(assetdepVO.getOriginalvalue());
    // debitVO.setYbjfmny(assetdepVO.getOriginalvalue());
    // debitVO.setPk_corp(assetcardVO.getPk_corp());
    // debitVO.setDr(0);
    // debitVO.setRowno(2);
    // debitVO.setVdirect(1);
    //
    // TzpzBVO[] children = new TzpzBVO[] { creditVO, debitVO };
    // headVO.setChildren(children);
    //
    // headVO = (TzpzHVO)
    // singleObjectBO.saveObject(assetcardVO.getPk_corp(),headVO);
    // return headVO.getPk_tzpz_h();
    // }
    private void updateAssetcard(AssetcardVO assetcardVO, DZFDouble depmny,
                                 String period) throws DZFWarpException {
        int accdepPeriod = getIntegerNullAsZero(assetcardVO
                .getAccountdepreciationperiod());
        assetcardVO.setAccountdepreciationperiod(Integer
                .valueOf(accdepPeriod + 1));

        int accusePeriod = getIntegerNullAsZero(assetcardVO
                .getAccountusedperiod());
        assetcardVO.setAccountusedperiod(Integer.valueOf(accusePeriod + 1));

        DZFDouble accdepmny = getDZFDoubleNullAsZero(assetcardVO
                .getAccountdepreciation());
        assetcardVO.setAccountdepreciation(accdepmny.add(depmny));

        DZFDouble totaldepmny = getDZFDoubleNullAsZero(assetcardVO
                .getDepreciation());
        assetcardVO.setDepreciation(totaldepmny.add(depmny));

        if (assetcardVO.getZjtype() == 1) {
            DZFDouble Ljgzl = getDZFDoubleNullAsZero(assetcardVO.getLjgzl());
            assetcardVO.setLjgzl(Ljgzl.add(getBygzl(assetcardVO, period)));
        }

        int totalusePeriod = getIntegerNullAsZero(assetcardVO.getUsedperiod());
        assetcardVO.setUsedperiod(Integer.valueOf(totalusePeriod + 1));

        int totaldepPeriod = getIntegerNullAsZero(assetcardVO
                .getDepreciationperiod());
        assetcardVO.setDepreciationperiod(Integer.valueOf(totaldepPeriod + 1));

        DZFDouble assetnetvalue = getDZFDoubleNullAsZero(assetcardVO
                .getAssetnetvalue());
        assetcardVO.setAssetnetvalue(assetnetvalue.sub(depmny));
        assetcardVO.setDepreciationdate(period);
        if (assetcardVO.getZjtype() == 2) {
            assetcardVO.setMonthzj(depmny);
        }
        String[] fields = new String[]{"accountdepreciationperiod",
                "accountusedperiod", "accountdepreciation", "depreciation",
                "usedperiod", "depreciationperiod", "assetnetvalue",
                "depreciationdate", "ljgzl", "monthzj"};

        singleObjectBO.update(assetcardVO, fields);
    }

    private int getIntegerNullAsZero(Integer value) {
        return value == null ? 0 : value.intValue();
    }

    private DZFDouble getDZFDoubleNullAsZero(DZFDouble value) {
        return value == null ? DZFDouble.ZERO_DBL : value;
    }

    private DZFDouble getBygzl(AssetcardVO assetcardVO, String period)
            throws DZFWarpException {
        QueryParamVO paramvo = new QueryParamVO();
        paramvo.setBegindate1(new DZFDate(period + "-01"));
        paramvo.setPk_assetcard(assetcardVO.getPk_assetcard());

        List<WorkloadManagementVO> list = am_workloadmagserv
                .queryBypk_assetcard(paramvo);
        DZFDouble bygzl = null;
        if (list == null || list.size() == 0) {
            throw new BusinessException("存在没有录入的本月工作量的卡片，无法计提折旧");
        }
        for (WorkloadManagementVO vo : list) {
            if (vo.getBygzl() == null) {
                throw new BusinessException("存在没有录入的本月工作量的卡片，无法计提折旧");
            } else {
                bygzl = vo.getBygzl();
            }
        }
        return bygzl;
    }

    private DZFDouble getsygzl(AssetcardVO assetcardVO, String period)
            throws DZFWarpException {
        QueryParamVO paramvo = new QueryParamVO();
        paramvo.setBegindate1(new DZFDate(period + "-01"));
        paramvo.setPk_assetcard(assetcardVO.getPk_assetcard());

        List<WorkloadManagementVO> list = am_workloadmagserv
                .queryBypk_assetcard(paramvo);
        DZFDouble sygzl = null;
        if (list == null || list.size() == 0) {
            return null;
        }
        for (WorkloadManagementVO vo : list) {
            if (vo.getSygzl() == null) {
                return null;
            } else {
                sygzl = vo.getSygzl();
            }
        }
        return sygzl;
    }


    private int getYearTotal(int n) {
        int year = n % 12 == 0 ? n / 12 : n / 12 + 1;
        int sum = 0;
        for (int i = 1; i <= year; i++) {
            sum += i;
        }
        return sum;
    }

    private DZFDouble calcDepreciationMny(AssetcardVO assetcardVO, String period)
            throws DZFWarpException {

        int zjtype = assetcardVO.getZjtype().intValue();
        // 平均年限法
        if (zjtype == 0) {
            double assetmny = assetcardVO.getAssetmny().getDouble();

            double salvage = assetcardVO.getPlansalvage().getDouble();

            double depreciation1 = assetcardVO.getDepreciation() == null ? DZFDouble.ZERO_DBL
                    .getDouble() : assetcardVO.getDepreciation().getDouble();// 累计折旧

            int uselimit = assetcardVO.getUselimit().intValue();

            double assetnetvalue = assetcardVO.getAssetnetvalue().getDouble();

            int depperiod = assetcardVO.getDepreciationperiod();

            double depmny = (assetmny - salvage - depreciation1)
                    / (uselimit - depperiod);
            if (depmny < 0.0D)
                throw new BusinessException(String.format(
                        "资产卡片%s的每个月折旧额%f小于0",
                        new Object[]{assetcardVO.getAssetcode(),
                                Double.valueOf(depmny)}));
            if (assetnetvalue < salvage) {
                throw new BusinessException(String.format(
                        "资产卡片%s的资产净值%f小于资产残值%f",
                        new Object[]{assetcardVO.getAssetcode(),
                                Double.valueOf(assetnetvalue),
                                Double.valueOf(salvage)}));
            }
            if (depmny > assetnetvalue - salvage)
                depmny = assetnetvalue - salvage;
            return new DZFDouble(depmny).setScale(2, DZFDouble.ROUND_HALF_UP);
            // 工作量法
        } else if (zjtype == 1) {
            // DZFDouble gzzl =assetcardVO.getGzzl();//工作总量
            // DZFDouble ljgzl;
            // if(assetcardVO.getLjgzl()!=null){
            // ljgzl = assetcardVO.getLjgzl();//累计工作量
            // }else{
            // ljgzl=DZFDouble.ZERO_DBL;
            // }
            // Double bygzl=assetcardVO.getBygzl().getDouble();//本月工作量
            DZFDouble bygzl = getBygzl(assetcardVO, period);// 本月工作量
            // assetcardVO.setLjgzl(ljgzl.add(bygzl));
            // Double sub=gzzl.sub(ljgzl).toDouble();//工作总量-累计工作量
            // Double assetmnyd = assetcardVO.getAssetmny().getDouble(); //原值
            DZFDouble plansalvage1 = assetcardVO.getPlansalvage();// 残值
            Double plansalvage = plansalvage1 == null ? new Double(0) : plansalvage1.getDouble();
            Double assetnetvalue = assetcardVO.getAssetnetvalue().getDouble();// 资产净值
            // Double sub1=assetmnyd-plansalvage;//原值-残值
            // Double dwzj = sub1/sub;//计算单位折旧
            Double dwzj = assetcardVO.getDwzj().getDouble();// 计算单位折旧
            Double depmny = bygzl.toDouble() * dwzj;
            if (depmny < 0.0D)
                throw new BusinessException(String.format(
                        "资产卡片%s的每个月折旧额%f小于0",
                        new Object[]{assetcardVO.getAssetcode(),
                                Double.valueOf(depmny)}));
            if (assetnetvalue < plansalvage) {
                throw new BusinessException(String.format(
                        "资产卡片%s的资产净值%f小于资产残值%f",
                        new Object[]{assetcardVO.getAssetcode(),
                                Double.valueOf(assetnetvalue),
                                Double.valueOf(plansalvage)}));
            }
            return new DZFDouble(depmny).setScale(2, DZFDouble.ROUND_HALF_UP);

        } else if (zjtype == 3) {
            Double monthzj;
            int totalMonth = assetcardVO.getUselimit(); // 预计使用年限（月）
            int userMonth = assetcardVO.getDepreciationperiod() == null ? 0
                    : assetcardVO.getDepreciationperiod(); // 已计提折旧期间
            DZFDouble plansalvage = assetcardVO.getPlansalvage() == null ? DZFDouble.ZERO_DBL
                    : assetcardVO.getPlansalvage();// 残值
            DZFDouble assetmnyd = assetcardVO.getAssetmny(); // 资产原值
            DZFDouble depreciation1 = assetcardVO.getDepreciation() == null ? DZFDouble.ZERO_DBL
                    : assetcardVO.getDepreciation();// 累计折旧
            DZFDouble assetnetvalue = assetcardVO.getAssetnetvalue();// 资产净值

            int lastYearMonth = totalMonth % 12;
            int userdYear = (userMonth + 1) % 12 == 0 ? (userMonth + 1) / 12 : ((userMonth + 1) / 12 + 1);
            int totalYear = totalMonth % 12 == 0 ? totalMonth / 12 : (totalMonth / 12 + 1);
            if (lastYearMonth == 0 || totalMonth - userMonth > lastYearMonth) {
                monthzj = assetmnyd.sub(plansalvage).multiply(new DZFDouble(totalYear - userdYear + 1)).div(new DZFDouble(getYearTotal(totalMonth))).div(new Double(12)).doubleValue();
            } else {
                if (totalMonth - userMonth == 1) {
                    monthzj = assetmnyd.sub(depreciation1).doubleValue();
                } else {
                    monthzj = assetmnyd.sub(plansalvage).multiply(new DZFDouble(totalYear - userdYear)).div(new DZFDouble(getYearTotal(totalMonth))).div(new Double(lastYearMonth)).doubleValue();
                }
            }
            if (monthzj < 0.0D)
                throw new BusinessException(String.format(
                        "资产卡片%s的每个月折旧额%f小于0",
                        new Object[]{assetcardVO.getAssetcode(),
                                Double.valueOf(monthzj)}));
            if (assetnetvalue.toDouble() < plansalvage.toDouble()) {
                throw new BusinessException(String.format(
                        "资产卡片%s的资产净值%f小于资产残值%f",
                        new Object[]{assetcardVO.getAssetcode(),
                                assetnetvalue.doubleValue(),
                                plansalvage.doubleValue()}));
            }
            return new DZFDouble(monthzj).setScale(2, DZFDouble.ROUND_HALF_UP);
        } else {// 双倍余额递减法
            Double monthzj;
            int uselimit = assetcardVO.getUselimit(); // 预计使用年限（月）
            int uselimit1 = assetcardVO.getDepreciationperiod() == null ? 0
                    : assetcardVO.getDepreciationperiod(); // 已计提折旧期间
            DZFDouble plansalvage = assetcardVO.getPlansalvage() == null ? DZFDouble.ZERO_DBL
                    : assetcardVO.getPlansalvage();// 残值
            DZFDouble assetmnyd = assetcardVO.getAssetmny(); // 资产原值
            DZFDouble depreciation1 = assetcardVO.getDepreciation() == null ? DZFDouble.ZERO_DBL
                    : assetcardVO.getDepreciation();// 累计折旧
            DZFDouble assetnetvalue = assetcardVO.getAssetnetvalue();// 资产净值

            double assetmnyd1 = new BigDecimal(assetmnyd.getDouble()).setScale(
                    2, RoundingMode.HALF_UP).doubleValue();
            double depreciation2 = new BigDecimal(depreciation1.getDouble())
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            double plansalvage1 = new BigDecimal(plansalvage.getDouble())
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();

            if (uselimit - uselimit1 > 24) { // --不是最后两年的算法
                Double sub = assetmnyd1 - depreciation2;// 资产原值-累计折旧
                Double div = new Double(2) / uselimit;// 2÷预计使用月×100%
                monthzj = sub * div;// 计算月折旧额
            } else {// --最后两年算法
                Double sub = assetmnyd1 - depreciation2 - plansalvage1;// 固定资产原值-累计折旧-残值
                monthzj = sub / 24;// 计算月折旧额
            }
            if (monthzj < 0.0D)
                throw new BusinessException(String.format(
                        "资产卡片%s的每个月折旧额%f小于0",
                        new Object[]{assetcardVO.getAssetcode(),
                                Double.valueOf(monthzj)}));
            if (assetnetvalue.toDouble() < plansalvage.toDouble()) {
                throw new BusinessException(String.format(
                        "资产卡片%s的资产净值%f小于资产残值%f",
                        new Object[]{assetcardVO.getAssetcode(),
                                assetnetvalue.doubleValue(),
                                plansalvage.doubleValue()}));
            }
            return new DZFDouble(monthzj).setScale(2, DZFDouble.ROUND_HALF_UP);
        }

    }

    private HashMap<AssetDepreciaTionVO, AssetcardVO> getRollbackDepCardVOs(
            QmclVO qmclVOs, String period) throws DZFWarpException {
        if (qmclVOs == null)
            return null;

        ArrayList<String> pk_corps = new ArrayList<String>();
        QmclVO qmclVO = qmclVOs;
        if (!period.equals(qmclVO.getPeriod()))
            throw new BusinessException("选择需要进行反计提折旧的期间不能多于1个");
        if (!pk_corps.contains(qmclVO.getPk_corp())) {
            pk_corps.add(qmclVO.getPk_corp());
        }

        String strpk_corps = SQLHelper.getInSQL(pk_corps);

        SQLParameter sp = SQLHelper.getSQLParameter(pk_corps);

        StringBuilder sb = new StringBuilder();
        sb.append(" nvl(dr,0)=0 and pk_corp in ");
        sb.append(strpk_corps);
//		sb.append(" and nvl(depreciationdate,'1900-10') = ? and nvl(isclear,'N')='N' ");
//		sb.append(" and nvl(isclear,'N')='N' ");
//		sp.addParam(period);
        AssetcardVO[] assetcardVOs = (AssetcardVO[]) singleObjectBO.queryByCondition(AssetcardVO.class, sb.toString(), sp);
        if ((assetcardVOs == null) || (assetcardVOs.length == 0))
            return null;

        sb.setLength(0);
        sb.append(" nvl(dr,0)=0 and pk_assetcard in (select pk_assetcard from ynt_assetcard where  nvl(dr,0)=0 and pk_corp in ");
        sb.append(strpk_corps);
//		sb.append(" and nvl(depreciationdate,'1900-10') = ? and businessdate like ? and nvl(isclear,'N')='N') ");
        sb.append("  and businessdate like ?  ) ");
        sp.addParam(period + "%");
        AssetDepreciaTionVO[] assetdepVOs = (AssetDepreciaTionVO[]) singleObjectBO
                .queryByCondition(AssetDepreciaTionVO.class, sb.toString(), sp);
        if ((assetdepVOs == null) || (assetdepVOs.length == 0))
            return null;

        HashMap<AssetDepreciaTionVO, AssetcardVO> map = new HashMap<AssetDepreciaTionVO, AssetcardVO>();

        for (AssetDepreciaTionVO assetdepVO : assetdepVOs) {
            AssetcardVO findcardVO = null;
            for (AssetcardVO assetcardVO : assetcardVOs) {
                if (assetcardVO.getPrimaryKey().equals(assetdepVO.getPk_assetcard())) {
                    if (assetcardVO.getIsclear() != null && assetcardVO.getIsclear().booleanValue()) {
                        throw new BusinessException("资产(" + assetcardVO.getAssetname() + ")存在清理数据，请删除资产清理数据");
                    }
                    if (!DateUtils.getPeriod(assetdepVO.getBusinessdate()).equals(assetcardVO.getDepreciationdate())) {
                        throw new BusinessException("资产(" + assetcardVO.getAssetname() + ")的折旧日期与反计提日期不匹配(请查询是否有折旧清理凭证)!");
                    }
                    findcardVO = assetcardVO;
                    break;
                }
            }
            if (findcardVO == null)
                throw new BusinessException(
                        String.format("没有找到折旧明细账%s所对应的资产卡片记录",
                                new Object[]{assetdepVO.getPrimaryKey()}));
            map.put(assetdepVO, findcardVO);
        }
        return map;
    }

    private AssetcardVO[] getCurrPeriodDepCardVOs(QmclVO qmclVO,
                                                  String period) throws DZFWarpException {
        if (qmclVO == null)
            return null;

        // StringBuilder bf = new StringBuilder();
        List<String> pk_corps = new ArrayList<String>();

        if ((qmclVO.getIszjjt() == null)
                || (!qmclVO.getIszjjt().booleanValue())) {
            if (!period.equals(qmclVO.getPeriod()))
                throw new BusinessException("选择需要进行计提折旧的期间不能多于1个");
            if (!pk_corps.contains(qmclVO.getPk_corp())) {
                pk_corps.add(qmclVO.getPk_corp());
            }
        }

        if (pk_corps.size() == 0) {
            throw new BusinessException("公司信息不能为空");
        }

        String strpk_corps = SQLHelper.getInSQL(pk_corps);// bf.deleteCharAt(bf.length()
        // - 1).toString();
        SQLParameter sp = SQLHelper.getSQLParameter(pk_corps);
        sp.addParam(period);
        // modify by zhangj nvl(uselimit,0)>nvl(depreciationperiod,0)
        // 的判断写在代码里面，需要算已使用月份
        String where = "nvl(dr,0)=0 and pk_corp in " + strpk_corps
                + " and nvl(depreciationdate,'1900-10') != ? and nvl(isclear,'N')='N'  order by assetcode desc ";
        // end 2015.6.4

        AssetcardVO[] assetVOs = (AssetcardVO[]) singleObjectBO
                .queryByCondition(AssetcardVO.class, where, sp);
        if ((assetVOs == null) || (assetVOs.length == 0))
            return null;

        return processCurrNewAssetVOs(assetVOs, period);
    }

    private AssetcardVO[] processCurrNewAssetVOs(AssetcardVO[] assetVOs,
                                                 String period) throws DZFWarpException {
        ArrayList<AssetcardVO> resultVOs = new ArrayList<AssetcardVO>();

        DZFDate periodEndDate = DateUtils.getPeriodEndDate(period);//DZFDate.getDate(period + "-01");
//		periodEndDate = periodEndDate
//				.getDateAfter(periodEndDate.getDaysMonth()).getDateBefore(1);
        Map<String, BdAssetCategoryVO> cateMap = zcCommonService.queryAssetCategoryMap();
        for (int i = assetVOs.length - 1; i >= 0; i--) {// 为啥是倒序，真奇怪??
            AssetcardVO assetVO = assetVOs[i];
            // modify by zhangj 有可能累计折旧是0.00333333之类的 这样要当作是零考虑
            //资产净值
            double assetnetvalue = 0;
            if(assetVO.getAssetnetvalue() != null){
            	assetnetvalue =new BigDecimal(assetVO.getAssetnetvalue().getDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
            }
            //预估残值
            DZFDouble ygcz = assetVO.getPlansalvage() == null ? DZFDouble.ZERO_DBL : assetVO.getPlansalvage();
            double salvage = new BigDecimal(ygcz.getDouble()).setScale(2, RoundingMode.HALF_UP).doubleValue();
            //可使用的月份
            int uselimit = getIntegerNullAsZero(assetVO.getUselimit());
            //累计折旧月份
            int depreciationperiod = getIntegerNullAsZero(assetVO.getDepreciationperiod());
            if (uselimit <= depreciationperiod && assetVO.getZjtype() != 1) {//1 工作量算法
                int accusePeriod = getIntegerNullAsZero(assetVO.getAccountusedperiod());//建账已使用期间数(月)
                assetVO.setAccountusedperiod(Integer.valueOf(accusePeriod + 1));
                singleObjectBO.update(assetVO);
                continue;
            }
            // end 2015.6.4
            if (assetnetvalue <= salvage) {
                // add by zhangj 计提折旧中建账已使用期间数+1
                int accusePeriod = getIntegerNullAsZero(assetVO.getAccountusedperiod());
                assetVO.setAccountusedperiod(Integer.valueOf(accusePeriod + 1));
                singleObjectBO.update(assetVO);
                // end 2015.6.4
                continue;
            }

            if ((assetVO.getAccountdate().equals(periodEndDate)) || (assetVO.getAccountdate().before(periodEndDate))) {
                resultVOs.add(assetVO);
                BdAssetCategoryVO cateVO = (BdAssetCategoryVO) cateMap.get(assetVO.getAssetcategory());
                if (DateUtils.getPeriod(assetVO.getAccountdate()).equals(period) && cateVO.getAssetproperty() != null
                        && cateVO.getAssetproperty() == 0) {// 固定资产当月不折旧
                    if (assetVO.getIsperiodbegin() == null || !assetVO.getIsperiodbegin().booleanValue()) {// 必须是非期初资产
                        if (assetVO.getOnetimedep() == null || !assetVO.getOnetimedep().booleanValue()) {//不是当月折旧完毕
                            resultVOs.remove(assetVO);
                        }
                    }
                }
            }
        }
        return (AssetcardVO[]) resultVOs.toArray(new AssetcardVO[resultVOs
                .size()]);
    }

    private void checkDepCardVOs(AssetcardVO[] assetcardVOs, String period)
            throws DZFWarpException {
        if ((assetcardVOs == null) || (assetcardVOs.length == 0))
            return;
        DZFDate perioddate = new DZFDate(period + "-01");
        DZFDate rowdate = new DZFDate();
        Map<String, DZFDate> corpBegindateMap = new HashMap<String, DZFDate>();
        ArrayList<String> checkCorps = new ArrayList<String>();
        Map<String, BdAssetCategoryVO> cateMap = zcCommonService.queryAssetCategoryMap();
        for (AssetcardVO assetcardVO : assetcardVOs) {
            if (!checkCorps.contains(assetcardVO.getPk_corp())) {
                checkPeriodIsSettle(assetcardVO.getPk_corp(), perioddate);
                checkCorps.add(assetcardVO.getPk_corp());
            }
            if (assetcardVO.getZjtype() != null && assetcardVO.getZjtype() == 1
                    && getDZFDoubleNullAsZero(assetcardVO.getGzzl())
                    .getDouble() <= getDZFDoubleNullAsZero(assetcardVO.getLjgzl()).getDouble()) {
                continue;
            }

            //期初资产分两种情况，第一个开始使用日期在启用日期之前，第二个，开始使用日期在计提日期之前
            if ((assetcardVO.getIsperiodbegin() != null) && (assetcardVO.getIsperiodbegin().booleanValue())
                    && assetcardVO.getAccountdate().after(DateUtils.getPeriodEndDate(period))) {
                continue;//使用日期在计提日期后，则不属于计提范围内的
            }
            if (StringUtil.isEmpty(assetcardVO.getDepreciationdate())) {
                //期初资产
                if ((assetcardVO.getIsperiodbegin() != null) && (assetcardVO.getIsperiodbegin().booleanValue())) {
                    if (!corpBegindateMap.containsKey(assetcardVO.getPk_corp())) {
                        CorpVO corpVO = corpService.queryByPk(assetcardVO.getPk_corp());
                        if (corpVO == null)
                            throw new BusinessException(String.format(
                                    "找不到公司PK %s 对应的记录", new Object[]{assetcardVO.getPk_corp()}));
                        if (corpVO.getBusibegindate() == null)
                            throw new BusinessException(String.format(
                                    "公司 %s 的固定资产启用日期为空！", new Object[]{corpVO.getUnitname()}));
                        corpBegindateMap.put(assetcardVO.getPk_corp(), corpVO.getBusibegindate());
                    }

                    rowdate = (DZFDate) corpBegindateMap.get(assetcardVO.getPk_corp());
                    rowdate = rowdate.getDateBefore(rowdate.getDay());
                    rowdate = rowdate.getDateBefore(rowdate.getDay() - 1);

                    //取开始使用日期
                    if (assetcardVO.getAccountdate() != null && assetcardVO.getAccountdate().after(rowdate)) {
                        rowdate = DateUtils.getPeriodStartDate(DateUtils.getPeriod(assetcardVO.getAccountdate()));
                    }

                } else {
                    BdAssetCategoryVO cateVO = (BdAssetCategoryVO) cateMap.get(assetcardVO.getAssetcategory());
                    rowdate = assetcardVO.getAccountdate();
                    if (cateVO.getAssetproperty() == null) {
                        throw new BusinessException("资产类别对应的资产属性为空，请检查!");
                    }
                    if (cateVO.getAssetproperty().equals(Integer.valueOf(0))) {//固定资产
                        rowdate = rowdate.getDateBefore(rowdate.getDay() - 1);
                    } else {
                        rowdate = rowdate.getDateBefore(rowdate.getDay());
                        rowdate = rowdate.getDateBefore(rowdate.getDay() - 1);
                    }
                }
            } else
                rowdate = new DZFDate(assetcardVO.getDepreciationdate() + "-01");

            if (rowdate.before(perioddate)) {
                if (!perioddate.equals(rowdate.getDateAfter(rowdate.getDaysMonth())))
                    throw new BusinessException(String.format(
                            "资产卡片%s在本期[%s]的上一期未进行计提折旧，本期不能进行计提折旧！",
                            new Object[]{assetcardVO.getAssetcode(), period}));
            } else if (rowdate.after(perioddate))
                throw new BusinessException(String.format(
                        "资产卡片%s最近计提折旧的期间[%s]大于本期[%s]，本期不能进行计提折旧！",
                        new Object[]{assetcardVO.getAssetcode(),
                                assetcardVO.getDepreciationdate() == null ? "" : assetcardVO.getDepreciationdate(),
                                period}));
        }
    }

    @Override
    public GdzcjzVO[] saveCheckAssetCards(GdzcjzVO[] gdzcjzVOs, CorpVO corpvo)
            throws DZFWarpException {
        if ((gdzcjzVOs == null) || (gdzcjzVOs.length == 0))
            return gdzcjzVOs;

        for (GdzcjzVO gdzcjzVO : gdzcjzVOs) {
            checkAssetGLEqual(gdzcjzVO, corpvo);

            // gdzcjzVO.setIsgdzjjz(DZFBoolean.TRUE);
            checkAssetGdzjjz(gdzcjzVO);

            checkAssetDepComplete(gdzcjzVO);

            checkAssetAllToGL(gdzcjzVO);

            saveGDZCJZVO(gdzcjzVO);
        }
        return gdzcjzVOs;
    }

    private void saveGDZCJZVO(GdzcjzVO gdzcjzVO) throws DZFWarpException {
        if (gdzcjzVO.getPrimaryKey() == null
                || gdzcjzVO.getPrimaryKey().equals("")) {
            singleObjectBO.saveObject(
                    gdzcjzVO.getPk_corp(), gdzcjzVO);
        } else {
            singleObjectBO.update(gdzcjzVO);
        }
    }

    /**
     * 资产总账与资产明细账是否相符。 原则上一定相符。
     *
     * @param gdzcjzVO
     * @throws BusinessException
     */
    private void checkAssetGdzjjz(GdzcjzVO gdzcjzVO) throws DZFWarpException {
        gdzcjzVO.setIsgdzjjz(DZFBoolean.TRUE);
        // String sql = "select count(pk_assetcard) as totalsum from "
        // +
        // "ynt_assetcard where nvl(dr,0)=0 and pk_corp=? and  substr(doperatedate,1,7) = ? ";
        // SQLParameter sp = new SQLParameter();
        // sp.addParam(gdzcjzVO.getPk_corp());
        // sp.addParam(gdzcjzVO.getPeriod());
        // String
        // .format("select count(pk_assetcard) as totalsum from ynt_assetcard where nvl(dr,0)=0 and pk_corp='%s' and  substr(doperatedate,1,7) = '%s' ",
        // new Object[] { // modify by zhangj 把accountdate
        // // 改成doperatedate
        // gdzcjzVO.getPk_corp(), gdzcjzVO.getPeriod() });
        // if (executeSqlAsInt(sql, sp) == 0) {
        // // gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
        // gdzcjzVO.setIsgdzjjz(DZFBoolean.FALSE);// modify by zhangj
        // // setIsgdzjjz改动
        // return;
        // }
    }

    private void checkAssetAllToGL(GdzcjzVO gdzcjzVO) throws DZFWarpException {
        gdzcjzVO.setIsqjsyjz(DZFBoolean.TRUE);
        SQLParameter sp = new SQLParameter();
        sp.addParam(gdzcjzVO.getPk_corp());
        sp.addParam(gdzcjzVO.getPeriod());
        // String sql =
        // "select count(pk_assetcard) as totalsum from ynt_assetcard where nvl(dr,0)=0 and pk_corp=? and nvl(isperiodbegin,'N')='N' and  substr(doperatedate,1,7) = ? ";
        // if (executeSqlAsInt(sql, sp) != 0) {
        // sql =
        // "select count(pk_assetcard) as totalsum from ynt_assetcard where nvl(dr,0)=0 and pk_corp=? and nvl(isperiodbegin,'N')='N' and  substr(doperatedate,1,7) = ? and nvl(istogl,'N')='N'";
        // if (executeSqlAsInt(sql, sp) != 0) {
        // gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
        // return;
        // }
        // }
        String sql = "select count(pk_assetcard) as totalsum from ynt_assetcard where nvl(dr,0)=0 and pk_corp=? and nvl(isperiodbegin,'N')='N' and  substr(doperatedate,1,7) = ? and nvl(istogl,'N')='N'";
        if (executeSqlAsInt(sql, sp) != 0) {
            gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
            return;
        }

        sql = "select count(*) from ynt_valuemodify where nvl(dr,0)=0 and pk_corp=? and substr(businessdate,1,7) = ? and nvl(istogl,'N')='N'";
        if (executeSqlAsInt(sql, sp) != 0) {
            gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
            return;
        }

        sql = "select count(*) from ynt_assetclear where nvl(dr,0)=0 and pk_corp=? and substr(businessdate,1,7) = ? and nvl(istogl,'N')='N'";
        if (executeSqlAsInt(sql, sp) != 0) {
            gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
            return;
        }

        sql = "select count(*) from ynt_depreciation where nvl(dr,0)=0 and pk_corp=?  and substr(businessdate,1,7) = ? and nvl(istogl,'N')='N'";
        if (executeSqlAsInt(sql, sp) != 0) {
            gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
            return;
        }

        // add by zhangj 是否计提折旧，如果没折旧过则 false return 掉
        // DZFBoolean iscbjz = gdzcjzVO.getIscbjz() == null ? DZFBoolean.FALSE
        // : gdzcjzVO.getIscbjz();
        // if (!iscbjz.booleanValue()) {
        // gdzcjzVO.setIsqjsyjz(DZFBoolean.FALSE);
        // return;
        // }
        // end 2015.6.4
    }

    private int executeSqlAsInt(String sql, SQLParameter sp)
            throws DZFWarpException {
        Object[] values = (Object[]) singleObjectBO.executeQuery(sql, sp,
                new ArrayProcessor());
        if ((values == null) || (values.length == 0))
            return 0;

        return values[0] == null ? 0 : Integer.parseInt(values[0].toString());
    }

    private void checkAssetDepComplete(GdzcjzVO gdzcjzVO)
            throws DZFWarpException {
        gdzcjzVO.setIscbjz(DZFBoolean.FALSE);
        SQLParameter sp = new SQLParameter();
        sp.addParam(gdzcjzVO.getPk_corp());
        sp.addParam(gdzcjzVO.getPeriod());

        // String sql = "select count(pk_assetcard) as totalsum,  "
        // +
        // "sum(case when depreciationdate>=? or round(nvl(assetnetvalue,0),2)<0.01   or nvl(uselimit,0)<=nvl(depreciationperiod,0)     then 1 else 0 end) as depsum  "
        // + "from ynt_assetcard where nvl(dr,0)=0 and pk_corp=?";
        String sql = "select count(pk_qmcl) from ynt_qmcl where pk_corp=? and nvl(dr,0)=0 and nvl(iszjjt,'N') = 'Y' and period=?";
        Object[] values = (Object[]) singleObjectBO.executeQuery(sql, sp,
                new ArrayProcessor());
        if (values == null || values.length == 0)
            return;

        int result = values[0] == null ? 0 : Integer.parseInt(values[0]
                .toString());
        if (result != 0) {
            gdzcjzVO.setIscbjz(DZFBoolean.TRUE);
        }
        // int totalsum = values[0] == null ? 0 : Integer.parseInt(values[0]
        // .toString());
        // int depsum = values[1] == null ? 0 : Integer.parseInt(values[1]
        // .toString());
        // if (totalsum != depsum)
        // gdzcjzVO.setIscbjz(DZFBoolean.FALSE);
    }

    private void checkAssetGLEqual(GdzcjzVO gdzcjzVO, CorpVO corpvo)
            throws DZFWarpException {
        // modify by zhangj 从上面来的代码怎么可能是空呢
        ZcdzVO[] zcdzVOs = queryAssetCheckVOsForjz(corpvo, gdzcjzVO.getPk_corp(), gdzcjzVO.getPeriod());

        if ((zcdzVOs == null) || (zcdzVOs.length == 0)) {
            gdzcjzVO.setIszjjt(DZFBoolean.TRUE);//数据为空则也能结账
            return;
        }

        int res = 0;
        for (ZcdzVO zdvo : zcdzVOs) {
            if (zdvo == null) {
                res++;
            }
        }

        if (res == zcdzVOs.length) {
            // gdzcjzVO.setIszjjt(DZFBoolean.FALSE);
            gdzcjzVO.setIszjjt(DZFBoolean.TRUE);
            return;
        }
        // SuperVO[] zcdzVOs = queryAssetCheckVOs(gdzcjzVO.getPk_corp(),
        // gdzcjzVO.getPeriod());
        // end 2015.6.8
        gdzcjzVO.setIszjjt(DZFBoolean.TRUE);
        for (ZcdzVO zcdzVO : zcdzVOs) {
            if (zcdzVO == null) {
                continue;
            }
            if (!zcdzVO.getZcje().equals(zcdzVO.getZzje())) {
                gdzcjzVO.setIszjjt(DZFBoolean.FALSE);
                break;
            }
        }
    }

//    @Override
//    public ZcdzVO[] queryAssetCheckVOs(String pk_corp, String period)
//            throws DZFWarpException {
//        CorpVO corpVO = CorpCache.getInstance().get("", pk_corp);
//        BdTradeAssetCheckVO[] bdTradeAssetCheckVOS = getAssetcheckVOs(null, pk_corp);
//        if ((bdTradeAssetCheckVOS == null) || (bdTradeAssetCheckVOS.length == 0)) {
//            return new ZcdzVO[0];
//        }
//
//        ZcdzVO[] zcdzVOs = new ZcdzVO[bdTradeAssetCheckVOS.length];
//        ZcdzVO zcdzVO;
//        int index = 0;
//        HashMap<String, BdAssetCategoryVO> cateMap = queryAssetCategoryMap();
//        String newRule = gl_cpacckmserv.queryAccountRule(corpVO.getPk_corp());
//
//        //公司级的
//        //替换下面两个查询  gzx
//        YntCpaccountVO[] yntCpaccountVOS = AccountCache.getInstance().get("", corpVO.getPk_corp());
//        Map<String, YntCpaccountVO> cpacodemap = Arrays.stream(yntCpaccountVOS).collect(Collectors.toMap(YntCpaccountVO::getAccountcode, a -> a, (k1, k2) -> k1));
//        Map<String, YntCpaccountVO> cpakeymap = Arrays.stream(yntCpaccountVOS).collect(Collectors.toMap(YntCpaccountVO::getPrimaryKey, a -> a, (k1, k2) -> k1));
//        //Map<String, YntCpaccountVO> cpacodemap = getCorpKmMap(cpvo);
//        //Map<String, YntCpaccountVO> cpakeymap = AccountCache.getInstance().getMap(null, pk_corp);
//        //行业级的
//        Map<String, BdTradeAccountVO> bdTradeAccountVOMap = getTradeKmMap(corpVO.getCorptype());
//        //获取发生额
//        Map<String, FseJyeVO> fseJyeVOMap = getFsmap(pk_corp, period);
//
//        ZcZzVO[] zcZzVOS = getAssetCardList(pk_corp, period);
//
//        FseJyeVO fseJyeVO;
//        for (BdTradeAssetCheckVO bdTradeAssetCheckVO : bdTradeAssetCheckVOS) {
//            int assetProperty = bdTradeAssetCheckVO.getAssetproperty();
//            int assetAccount = bdTradeAssetCheckVO.getAssetaccount();
//            String pk_asset_category = bdTradeAssetCheckVO.getPk_assetcategory();
//            //优化 gzx
//            double totalAssetMny = getTotalAssetMny(pk_corp, period, assetProperty, pk_asset_category, assetAccount, false);
//            String pk_corp_account;
//            if (IGlobalConstants.currency_corp.equals(bdTradeAssetCheckVO.getPk_corp())) {
//                pk_corp_account = hyColumnHandle(newRule, cpacodemap, bdTradeAccountVOMap, bdTradeAssetCheckVO.getPk_glaccount());// yntBoPubUtil.getCorpAccountPkByTradeAccountPk(, pk_corp);
//            } else {
//                pk_corp_account = bdTradeAssetCheckVO.getPk_glaccount();
//            }
//
//            fseJyeVO = fseJyeVOMap.get(pk_corp_account);
//            double totalAccMny = 0;
//            if (fseJyeVO != null) {
//                totalAccMny = SafeCompute.add(fseJyeVO.getQmjf(), fseJyeVO.getQmdf()).doubleValue();
//            }
//            zcdzVO = new ZcdzVO();
//            zcdzVO.setQj(period);
//
//            zcdzVO.setZcsx(AssetUtil.getAssetProperty(assetProperty));
//
//            if (!StringUtil.isEmptyWithTrim(pk_asset_category)) {
//                BdAssetCategoryVO cateVO = cateMap.get(pk_asset_category);
//                if (cateVO != null) {
//                    zcdzVO.setZclb(cateVO.getCatename());
//                }
//            }
//
//            if (assetProperty == 1 && (pk_asset_category == null || pk_asset_category.trim().length() == 0)) {
//                zcdzVO.setZckm("累计摊销");
//            } else {
//                zcdzVO.setZckm(AssetUtil.getAssetAccount(assetAccount));
//            }
//
//            YntCpaccountVO accountVO = cpakeymap.get(pk_corp_account);
//
//            zcdzVO.setZzkmbh(accountVO.getAccountcode());
//            zcdzVO.setZzkmmc(accountVO.getAccountname());
//
//            zcdzVO.setZcje(new DZFDouble(totalAssetMny));
//
//            zcdzVO.setZzje(new DZFDouble(totalAccMny));
//
//            zcdzVOs[(index++)] = zcdzVO;
//        }
//
//        return zcdzVOs;
//    }

//    private String hyColumnHandle(String newrule, Map<String, YntCpaccountVO> cpacodemap,
//                                  Map<String, BdTradeAccountVO> tradecodemap, String hykmid) {
//        YntCpaccountVO tempvo;
//        BdTradeAccountVO hytempvo;
//        String gscode;
//        if (!StringUtil.isEmpty(hykmid)) {
//            hytempvo = tradecodemap.get(hykmid);
//            if (hytempvo != null) {
//                gscode = gl_accountcoderule.getNewRuleCode(hytempvo.getAccountcode(), DZFConstant.ACCOUNTCODERULE,
//                        newrule);
//                tempvo = cpacodemap.get(gscode);
//                if (tempvo != null) {
//                    return tempvo.getPk_corp_account();
//                }
//            }
//        }
//        return "";
//    }

    /**
     * @param pk_corp
     * @return [accountCode: O]
     * 获取公司会计科目
     */
//    private Map<String, YntCpaccountVO> getCorpKmMap(String pk_corp) {
//        YntCpaccountVO[] yntCpaccountVOS = AccountCache.getInstance().get("", pk_corp);
//        return Arrays.stream(yntCpaccountVOS).collect(Collectors.toMap(YntCpaccountVO::getAccountcode, v -> v, (k1, k2) -> k1));
//    }

//    private Map<String, BdTradeAccountVO> getTradeKmMap(String corpType) {
//        SQLParameter sp = new SQLParameter();
//        sp.addParam(corpType);
//        BdTradeAccountVO[] bdTradeAccountVOS = (BdTradeAccountVO[]) singleObjectBO.queryByCondition(BdTradeAccountVO.class, "nvl(dr,0)=0 and pk_trade_accountschema = ? ", sp);
//        return Arrays.stream(bdTradeAccountVOS).collect(Collectors.toMap(BdTradeAccountVO::getPk_trade_account, v -> v, (k1, k2) -> k1));
//    }

//    private ZcZzVO[] getAssetCardList(String pk_corp, String period){
//        DZFDate periodBeginDate = DZFDate.getDate(period + "-01");
//        DZFDate periodEndDate = periodBeginDate.getDateAfter(periodBeginDate
//                .getDaysMonth() - 1);
//        return am_rep_zcmxserv.queryAssetcardTotal(pk_corp, periodBeginDate, periodEndDate, null, new SQLParameter(), null, null, null);
//    }

    private double getTotalAssetMny(String pk_corp, String period,
                                    int assetproperty, String pk_assetcategory, int assetacckind,
                                    boolean isinitPeriod) throws DZFWarpException {
        if (isinitPeriod) {
            String sumfield = "";
            switch (assetacckind) {
                case 0:
                    sumfield = "assetmny";
                    break;
                case 1:
                    sumfield = "depreciation";
                    break;
                case 2:
                    sumfield = "assetnetvalue";
                    break;
                case 3:
                    sumfield = "qcnetvalue";//取期初净值
                    break;
            }

            SQLParameter sp = new SQLParameter();
            sp.addParam(pk_corp);
            sp.addParam(Integer.valueOf(assetproperty));

            StringBuffer qrysql = new StringBuffer();
            qrysql.append(" select sum(" + sumfield + ") as assetmny ");
            qrysql.append(" from ynt_assetcard  ");
            qrysql.append("  where pk_corp=? and nvl(isperiodbegin,'N')='Y'  ");
            qrysql.append(" and nvl(dr,0)=0 and assetcategory in ");
            qrysql.append(" (select pk_assetcategory from ynt_category where assetproperty=?) ");
            if (!StringUtil.isEmpty(pk_assetcategory)) {
                qrysql.append(" and assetcategory=? ");
                sp.addParam(pk_assetcategory);
            }
            Object[] values = (Object[]) singleObjectBO.executeQuery(qrysql.toString(), sp, new ArrayProcessor());
            return (values == null) || (values.length == 0)
                    || (values[0] == null) ? 0.0D : Double.parseDouble(values[0].toString());
        }
        DZFDate periodBeginDate = DZFDate.getDate(period + "-01");
        DZFDate periodEndDate = periodBeginDate.getDateAfter(periodBeginDate
                .getDaysMonth() - 1);
        String where = "b.assetproperty=" + Integer.valueOf(assetproperty);
        SQLParameter sp = new SQLParameter();
        if (!StringUtil.isEmpty(pk_assetcategory)) {
            where = where + " and b.pk_assetcategory='" + pk_assetcategory + "'";
        }
        ZcZzVO[] zczzVOs = am_rep_zcmxserv.queryAssetcardTotal(pk_corp, periodBeginDate, periodEndDate, where, sp, null, null, null);
        if (zczzVOs == null || zczzVOs.length == 0)
            return 0.0D;

        ZcZzVO zczzVO = zczzVOs[(zczzVOs.length - 1)];
        switch (assetacckind) {
            case 0:
                return zczzVO.getYzye() == null ? 0.0D : zczzVO.getYzye().getDouble();
            case 1:
                return zczzVO.getLjye() == null ? 0.0D : zczzVO.getLjye().getDouble();
            case 2:
                return zczzVO.getJzye() == null ? 0.0D : zczzVO.getJzye().getDouble();
            case 3:
                return zczzVO.getJzye() == null ? 0.0D : zczzVO.getJzye().getDouble();//取余额
        }
        return 0.0D;
    }


    private String getCorpInitPeriod(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        String sql = "select begindate from bd_corp where pk_corp=?";
        Object[] obs = (Object[]) singleObjectBO.executeQuery(sql, sp,
                new ArrayProcessor());
        if ((obs == null) || (obs.length == 0) || (obs[0] == null))
            return "";
        String beginDate = obs[0].toString();
        if (StringUtil.isEmptyWithTrim(beginDate))
            return "";

        return DateUtils.getPeriod(DZFDate.getDate(beginDate));
    }

    @Override
    public GdzcjzVO[] updateSettleAssetCards(GdzcjzVO[] gdzcjzVOs,
                                             boolean issettle) throws DZFWarpException {
        if ((gdzcjzVOs == null) || (gdzcjzVOs.length == 0))
            return gdzcjzVOs;
        String b = issettle ? "Y" : "N";

        for (GdzcjzVO gdzcjzVO : gdzcjzVOs) {
            String corpInitPeriod = getCorpInitPeriod(gdzcjzVO.getPk_corp());
            String sql = null;
            SQLParameter para = new SQLParameter();
            if (corpInitPeriod.equals(gdzcjzVO.getPeriod())) {
                sql = "update ynt_assetcard set issettle= ? where nvl(dr,0)=0 and pk_corp= ? "
                        + "and ((nvl(isperiodbegin,'N')='N' and substr(accountdate,0,7) = ?)  or (nvl(isperiodbegin,'N')='Y'))";
                para.clearParams();
                para.addParam(b);
                para.addParam(gdzcjzVO.getPk_corp());
                para.addParam(gdzcjzVO.getPeriod());
            } else {
                sql = "update ynt_assetcard set issettle= ? where nvl(dr,0)=0 and pk_corp= ? "
                        + "and (nvl(isperiodbegin,'N')='N' and substr(accountdate,0,7) = ?)";
                para.clearParams();
                para.addParam(b);
                para.addParam(gdzcjzVO.getPk_corp());
                para.addParam(gdzcjzVO.getPeriod());
            }
            singleObjectBO.executeUpdate(sql, para);

            sql = "update ynt_valuemodify set issettle=? where nvl(dr,0)=0 and pk_corp=? and substr(businessdate,0,7) = ?  ";
            para.clearParams();
            para.addParam(b);
            para.addParam(gdzcjzVO.getPk_corp());
            para.addParam(gdzcjzVO.getPeriod());
            singleObjectBO.executeUpdate(sql, para);

            sql = "update ynt_assetclear set issettle=? where nvl(dr,0)=0 and pk_corp=? and substr(businessdate,0,7) = ? ";
            para.clearParams();
            para.addParam(b);
            para.addParam(gdzcjzVO.getPk_corp());
            para.addParam(gdzcjzVO.getPeriod());
            singleObjectBO.executeUpdate(sql, para);

            sql = "update ynt_depreciation set issettle=? where nvl(dr,0)=0 and pk_corp=? and substr(businessdate,0,7) = ? ";
            para.clearParams();
            para.addParam(b);
            para.addParam(gdzcjzVO.getPk_corp());
            para.addParam(gdzcjzVO.getPeriod());
            singleObjectBO.executeUpdate(sql, para);

            gdzcjzVO.setJzfinish(new DZFBoolean(issettle));
            saveGDZCJZVO(gdzcjzVO);
        }
        return gdzcjzVOs;
    }

    @Override
    public void deleteAssetDepreciation(AssetDepreciaTionVO assetdepVO)
            throws DZFWarpException {
        if (assetdepVO == null)
            return;

        String period = DateUtils.getPeriod(assetdepVO.getDoperatedate());
        AssetcardVO assetcardVO = (AssetcardVO) singleObjectBO
                .queryByPrimaryKey(AssetcardVO.class,
                        assetdepVO.getPk_assetcard());
        if (assetdepVO.getIstogl() != null
                && assetdepVO.getIstogl().booleanValue())
            throw new BusinessException(String.format(
                    "资产卡片%s期间%s的累计折旧已经转总账，不允许删除折旧明细", new Object[]{
                            assetcardVO.getAssetcode(), period}));
        if (assetdepVO.getIssettle() != null
                && assetdepVO.getIssettle().booleanValue()) {
            throw new BusinessException(String.format(
                    "资产卡片%s期间%s的累计折旧已经结账，不允许删除折旧明细",
                    new Object[]{assetcardVO.getAssetcode(), period}));
        }
        rollbackAssetcard(assetcardVO, period, assetdepVO.getOriginalvalue());

        singleObjectBO.deleteObject(assetdepVO);

        updateQMCLVO(assetcardVO.getPk_corp(), period);
    }

    private void updateQMCLVO(String pk_corp, String period)
            throws DZFWarpException {
        String where = " nvl(dr,0)=0 and pk_corp=? and period=? ";
        SQLParameter param = new SQLParameter();
        param.addParam(pk_corp);
        param.addParam(period);
        QmclVO[] qmclVOs = (QmclVO[]) singleObjectBO.queryByCondition(
                QmclVO.class, where, param);
        if ((qmclVOs == null) || (qmclVOs.length == 0))
            return;

        if (qmclVOs[0].getIszjjt() != null
                && qmclVOs[0].getIszjjt().booleanValue()) {
            qmclVOs[0].setIszjjt(DZFBoolean.FALSE);
            singleObjectBO.update(qmclVOs[0], new String[]{"iszjjt"});
        }
    }

    private void rollbackAssetcard(AssetcardVO assetcardVO, String period,
                                   DZFDouble depmny) throws DZFWarpException {
        DZFDouble depMny = new DZFDouble(depmny);
        if (assetcardVO.getDepreciationdate() == null)
            throw new BusinessException(String.format(
                    "资产卡片%s建账后未进行计提折旧，不允许做反折旧",
                    new Object[]{assetcardVO.getAssetcode()}));
        if (!assetcardVO.getDepreciationdate().equals(period)) {
            throw new BusinessException(String.format(
                    "资产卡片%s的最近一次折旧月份为%s,而需要进行反折旧的月份为%s，不允许进行跨月做反折旧",
                    new Object[]{assetcardVO.getAssetcode(),
                            assetcardVO.getDepreciationdate(), period}));
        }
        if ((assetcardVO.getIsclear() != null)
                && (assetcardVO.getIsclear().booleanValue())) {
            throw new BusinessException(String.format(
                    "资产卡片%s已经清理，不允许删除，请删除资产清理单。",
                    new Object[]{assetcardVO.getAssetcode()}));
        }

        int accdepPeriod = getIntegerNullAsZero(assetcardVO
                .getAccountdepreciationperiod());
        assetcardVO.setAccountdepreciationperiod(Integer
                .valueOf(accdepPeriod - 1));

        int accusePeriod = getIntegerNullAsZero(assetcardVO
                .getAccountusedperiod());
        assetcardVO.setAccountusedperiod(Integer.valueOf(accusePeriod - 1));

        DZFDouble accdepmny = getDZFDoubleNullAsZero(assetcardVO
                .getAccountdepreciation());
        assetcardVO.setAccountdepreciation(accdepmny.sub(depMny));// 减

        DZFDouble totaldepmny = getDZFDoubleNullAsZero(assetcardVO
                .getDepreciation());
        assetcardVO.setDepreciation(totaldepmny.sub(depMny));// 减

        int totalusePeriod = getIntegerNullAsZero(assetcardVO.getUsedperiod());
        assetcardVO.setUsedperiod(Integer.valueOf(totalusePeriod - 1));

        int totaldepPeriod = getIntegerNullAsZero(assetcardVO
                .getDepreciationperiod());
        assetcardVO.setDepreciationperiod(Integer.valueOf(totaldepPeriod - 1));

        DZFDouble assetnetvalue = getDZFDoubleNullAsZero(assetcardVO
                .getAssetnetvalue());
        assetcardVO.setAssetnetvalue(assetnetvalue.add(depMny));// 加

        DZFDate currDate = new DZFDate(period + "-01");
        DZFDate lastDate = currDate.getDateBefore(1);
        assetcardVO.setDepreciationdate(DateUtils.getPeriod(lastDate));

        // modfiby zhangj如果总累计折旧是零则折旧月份是空
        if (assetcardVO.getDepreciation() == null
                || assetcardVO.getDepreciation().doubleValue() <= 0.0D
                || (assetcardVO.getIsperiodbegin() != null && assetcardVO.getIsperiodbegin().booleanValue()
                && SafeCompute.sub(assetcardVO.getDepreciation(), assetcardVO.getInitdepreciation()).doubleValue() == 0.0D)) {
            assetcardVO.setDepreciationdate(null);
        }
        // 反计提--减去累加的本月工作量
        if (assetcardVO.getZjtype() == 1) {
            DZFDouble ljgzl = getDZFDoubleNullAsZero(assetcardVO.getLjgzl());
            assetcardVO.setLjgzl(ljgzl.sub(getBygzl(assetcardVO, period)));
        }
        // 重新计算月折旧
        if (assetcardVO.getZjtype() == 2) {
            // assetcardVO.setMonthzj(depmny);
            Double monthzj;
            int uselimit = assetcardVO.getUselimit(); // 预计使用年限（月）
            int uselimit1 = assetcardVO.getDepreciationperiod() == null ? 0
                    : assetcardVO.getDepreciationperiod(); // 已计提折旧期间
            DZFDouble plansalvage = assetcardVO.getPlansalvage() == null ? DZFDouble.ZERO_DBL
                    : assetcardVO.getPlansalvage();// 残值
            DZFDouble assetmnyd = assetcardVO.getAssetmny(); // 资产原值
            DZFDouble depreciation1 = assetcardVO.getDepreciation() == null ? DZFDouble.ZERO_DBL
                    : assetcardVO.getDepreciation();// 累计折旧
            // DZFDouble assetnetvalue = assetcardVO.getAssetnetvalue();//资产净值
            double assetmnyd1 = new BigDecimal(assetmnyd.getDouble()).setScale(
                    2, RoundingMode.HALF_UP).doubleValue();
            double depreciation2 = new BigDecimal(depreciation1.getDouble())
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();
            double plansalvage1 = new BigDecimal(plansalvage.getDouble())
                    .setScale(2, RoundingMode.HALF_UP).doubleValue();

            if (uselimit - uselimit1 > 24) { // --不是最后两年的算法
                Double sub = assetmnyd1 - depreciation2;// 资产原值-累计折旧
                Double div = new Double(2) / uselimit;// 2÷预计使用月×100%
                monthzj = sub * div;// 计算月折旧额
            } else if (uselimit - uselimit1 == 24) {// --最后两年算法
                Double sub = assetmnyd1 - depreciation2 - plansalvage1;// 固定资产原值-累计折旧-残值
                monthzj = sub / 24;// 计算月折旧额
            } else {
                monthzj = assetcardVO.getMonthzj().toDouble();
            }
            assetcardVO.setMonthzj(new DZFDouble(monthzj).setScale(2, DZFDouble.ROUND_HALF_UP));
        }

        // end 2015.6.5
        String[] fields = new String[]{"accountdepreciationperiod",
                "accountusedperiod", "accountdepreciation", "depreciation",
                "usedperiod", "depreciationperiod", "assetnetvalue",
                "depreciationdate", "ljgzl", "monthzj"};
        singleObjectBO.update(assetcardVO, fields);
        if (assetcardVO.getZjtype() == 1) {
//            deleteWorkload(assetcardVO, period);
        }

    }

    //工作量法--反计提删除当月工作量
    private void deleteWorkload(AssetcardVO assetcardVO, String period) {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sp.addParam(assetcardVO.getPk_assetcard());
        sp.addParam(period + "%");
        sql.append(" delete from ynt_workloadmanagement  "
                + "where pk_assetcard = ? and doperatedate like ?");
        singleObjectBO.executeUpdate(sql.toString(), sp);
    }

    @Override
    public void updateDepToGLState(String pk_assetdep, boolean istogl,
                                   String pk_voucher) throws DZFWarpException {
        AssetDepreciaTionVO assetdepVO = (AssetDepreciaTionVO) singleObjectBO
                .queryByPrimaryKey(AssetDepreciaTionVO.class, pk_assetdep);
        if (assetdepVO == null) {
            //如果查询不到，通过凭证查询对应的折旧明细账
            if (!StringUtil.isEmpty(pk_voucher)) {
                TzpzHVO hvo = (TzpzHVO) singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk_voucher);
                if (hvo != null) {
                    String period = hvo.getPeriod();
                    SQLParameter sp = new SQLParameter();
                    sp.addParam(pk_voucher);
                    sp.addParam(period + "%");
                    AssetDepreciaTionVO[] assetdepvos = (AssetDepreciaTionVO[]) singleObjectBO.queryByCondition(AssetDepreciaTionVO.class, "nvl(dr,0)=0 and pk_voucher = ?  and businessdate like ?", sp);
                    if (assetdepvos != null && assetdepvos.length == 1) {//只是查询一张才这么处理，如果是多，则不处理(多个折旧明细，生成一张折旧凭证)
                        assetdepVO = assetdepvos[0];
                    }
                }
            }
        }
        if (assetdepVO == null) {
            throw new BusinessException("折旧明细账已经被他人删除，请刷新界面。");
        }
        String period = DateUtils.getPeriod(assetdepVO.getDoperatedate());
        AssetcardVO assetcardVO = (AssetcardVO) singleObjectBO
                .queryByPrimaryKey(AssetcardVO.class,
                        assetdepVO.getPk_assetcard());
        if (istogl) {
            if (assetdepVO.getIstogl() != null
                    && assetdepVO.getIstogl().booleanValue()) {
                throw new BusinessException(String.format(
                        "资产卡片%s期间%s的累计折旧已经生成凭证，不允许再次生成。", new Object[]{
                                assetcardVO.getAssetcode(), period}));
            }
        }
        if (assetdepVO.getIssettle() != null
                && assetdepVO.getIssettle().booleanValue()) {
            throw new BusinessException(String.format(
                    "资产卡片%s期间%s的累计折旧已经结账，不允许删除折旧明细",
                    new Object[]{assetcardVO.getAssetcode(), period}));
        }
        assetdepVO.setIstogl(new DZFBoolean(istogl));
        assetdepVO.setPk_voucher(pk_voucher);//容易造成 非转总账凭证id也有值（不是bug 是后续也要操作）
        singleObjectBO.update(assetdepVO, new String[]{"istogl", "pk_voucher"});

    }

    /**
     * 行业资产与总账对照表,两个参数必须有一个不为空(先查询公司的档案，如果为空则查询集团的)
     *
     * @param corpvo
     * @param pk_corp
     * @return
     * @throws BusinessException
     */
//    private BdTradeAssetCheckVO[] getAssetcheckVOs(CorpVO corpvo, String pk_corp)
//            throws DZFWarpException {
//        StringBuilder sb = new StringBuilder();
//        SQLParameter param = new SQLParameter();
//        sb.append("  pk_corp = ? and ");
//        param.addParam(pk_corp);
//        if (pk_corp != null) {
//            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema in");
//            sb.append("(select corptype from bd_corp where pk_corp=?)  order by assetproperty,pk_assetcategory ");
//            param.addParam(pk_corp);
//        } else {
//            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema =?");
//            sb.append(" order by assetproperty,pk_assetcategory ");
//            param.addParam(corpvo.getCorptype());
//        }
//        BdTradeAssetCheckVO[] assetcheckVOs = (BdTradeAssetCheckVO[]) singleObjectBO
//                .queryByCondition(BdTradeAssetCheckVO.class, sb.toString(),
//                        param);
//
//        if (assetcheckVOs == null || assetcheckVOs.length == 0) {
//            // 如果没有，查询行业的档案
//            BdTradeAssetCheckVO[] assetcheckVOsHy = getAssetcheckVOsHY(corpvo,
//                    pk_corp);
//            if (assetcheckVOsHy == null || assetcheckVOsHy.length == 0) {
//                throw new BusinessException("行业资产科目对照为空，请检查!");
//            }
//            return assetcheckVOsHy;
//        }
//        return assetcheckVOs;
//
//    }
//
//    private BdTradeAssetCheckVO[] getAssetcheckVOsHY(CorpVO corpvo,
//                                                     String pk_corp) throws DZFWarpException {
//        StringBuilder sb = new StringBuilder();
//        SQLParameter param = new SQLParameter();
//        sb.append("  pk_corp = ? and ");
//        param.addParam(IGlobalConstants.DefaultGroup);
//        if (pk_corp != null) {
//            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema in");
//            sb.append("(select corptype from bd_corp where pk_corp=?)  order by assetproperty,pk_assetcategory ");
//            param.addParam(pk_corp);
//        } else {
//            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema =?");
//            sb.append(" order by assetproperty,pk_assetcategory ");
//            param.addParam(corpvo.getCorptype());
//        }
//        BdTradeAssetCheckVO[] assetcheckVOs = (BdTradeAssetCheckVO[]) singleObjectBO
//                .queryByCondition(BdTradeAssetCheckVO.class, sb.toString(),
//                        param);
//        return assetcheckVOs;
//    }

    @Override
    public QcYeVO[] doAssetSync(QcYeVO[] qcyevos) throws DZFWarpException {
        if ((qcyevos == null) || (qcyevos.length == 0))
            return qcyevos;

        BdTradeAssetCheckVO[] assetcheckVOs = zcCommonService.getAssetcheckVOs(null,
                qcyevos[0].getPk_corp());
        if ((assetcheckVOs == null) || (assetcheckVOs.length == 0))
            return qcyevos;
        String pk_curr = yntBoPubUtil.getCNYPk();

        for (BdTradeAssetCheckVO assetcheckVO : assetcheckVOs) {
            String pk_account = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(
                    assetcheckVO.getPk_glaccount(), qcyevos[0].getPk_corp());

            double totalAssetmny = getTotalAssetMny(qcyevos[0].getPk_corp(),
                    "", assetcheckVO.getAssetproperty().intValue(),
                    assetcheckVO.getPk_assetcategory(), assetcheckVO
                            .getAssetaccount().intValue(), true);
            for (QcYeVO qcyevo : qcyevos)
                if (qcyevo.getPk_accsubj().equals(pk_account)) {
                    DZFDouble oldvalue = getDZFDoubleNullAsZero(qcyevo
                            .getThismonthqc());
                    DZFDouble newvalue = new DZFDouble(totalAssetmny);
                    qcyevo.setThismonthqc(newvalue);
                    qcyevo.setPk_currency(pk_curr);
                    calcYearqc(qcyevo);
                    if (qcyevo.getPk_qcye() == null) {
                        if (qcyevo.getThismonthqc().compareTo(
                                DZFDouble.ZERO_DBL) > 0
                                || qcyevo.getYearqc().compareTo(
                                DZFDouble.ZERO_DBL) > 0) {
                            singleObjectBO.saveObject(qcyevo.getPk_corp(),
                                    qcyevo);
                        }
                    } else {
                        singleObjectBO.update(qcyevo);
                    }

                    String parentAcccode = getParentAccCode(qcyevo.getVcode());
                    recurIncAccMny(qcyevos, parentAcccode, oldvalue, newvalue);
                }
        }
        return qcyevos;
    }

    private String getParentAccCode(String acccode) {
        if ((acccode == null) || (acccode.equals("")))
            return "";
        if (acccode.length() < 4)
            return "";
        return acccode.substring(0, acccode.length() - 2);
    }

    private void calcYearqc(QcYeVO qcyevo) {
        int vdirect = qcyevo.getDirect() == null ? -1 : qcyevo.getDirect()
                .intValue();
        DZFDouble bnjffs = getDZFDoubleNullAsZero(qcyevo.getYearjffse());
        // qcyevo.getYearjffse() == null ? DZFDouble.ZERO_DBL
        // : qcyevo.getYearjffse();
        DZFDouble bndffs = getDZFDoubleNullAsZero(qcyevo.getYeardffse());
        DZFDouble thismonthqc = getDZFDoubleNullAsZero(qcyevo.getThismonthqc());

        if (vdirect == 0) {
            qcyevo.setYearqc(thismonthqc.add(bndffs).sub(bnjffs));
        } else if (vdirect == 1) {
            qcyevo.setYearqc(thismonthqc.add(bnjffs).sub(bndffs));
        }
    }

    private void recurIncAccMny(QcYeVO[] qcyevos, String acccode,
                                DZFDouble oldvalue, DZFDouble newvalue) throws DZFWarpException {
        if ((acccode == "") || (acccode.equals("")))
            return;
        for (QcYeVO qcyevo : qcyevos)
            if (qcyevo.getVcode().equals(acccode)) {
                DZFDouble mny = getDZFDoubleNullAsZero(qcyevo.getThismonthqc());
                qcyevo.setThismonthqc(mny.sub(oldvalue).add(newvalue));
                // qcyevo.setThismonthqc(SafeCompute.add(
                // SafeCompute.sub(mny, oldvalue), newvalue));

                calcYearqc(qcyevo);
                if (qcyevo.getPk_qcye() == null) {
                    if (qcyevo.getThismonthqc().compareTo(DZFDouble.ZERO_DBL) > 0
                            || qcyevo.getYearqc().compareTo(DZFDouble.ZERO_DBL) > 0) {
                        singleObjectBO.saveObject(qcyevo.getPk_corp(), qcyevo);
                    }
                } else {
                    singleObjectBO.update(qcyevo);
                }
                // getHYPubBO().update(qcyevo);

                String parentAcccode = getParentAccCode(qcyevo.getVcode());
                recurIncAccMny(qcyevos, parentAcccode, oldvalue, newvalue);
            }
    }

    // add by zhangj 结账检查调用
    private ZcdzVO[] queryAssetCheckVOsForjz(CorpVO corpvo, String pk_corp,
                                             String period) throws DZFWarpException {
        BdTradeAssetCheckVO[] assetcheckVOs = zcCommonService.getAssetcheckVOs(corpvo.getCorptype(), pk_corp);
        if ((assetcheckVOs == null) || (assetcheckVOs.length == 0))
            return null;

        ZcdzVO[] zcdzVOs = new ZcdzVO[assetcheckVOs.length];
        ZcdzVO zcdzVO = null;
        int index = 0;
        Map<String, BdAssetCategoryVO> map = zcCommonService.queryAssetCategoryMap();
        for (BdTradeAssetCheckVO assetcheckVO : assetcheckVOs) {
            int assetproperty = assetcheckVO.getAssetproperty();
            int assetaccount = assetcheckVO.getAssetaccount();
            Double totalAssetmny = getTotalAssetMny(pk_corp, period,
                    assetproperty, assetcheckVO.getPk_assetcategory(),
                    assetaccount, false);
            if (totalAssetmny == 0) {
                continue;
            }
            String pk_corp_account;
            if (assetcheckVO.getPk_corp() == IGlobalConstants.currency_corp) {
                pk_corp_account = yntBoPubUtil
                        .getCorpAccountPkByTradeAccountPk(
                                assetcheckVO.getPk_glaccount(), pk_corp);
            } else {
                pk_corp_account = assetcheckVO.getPk_glaccount();
            }
            DZFDouble totalAccmny = yntBoPubUtil.getQmMny(pk_corp, period,
                    pk_corp_account);

            zcdzVO = new ZcdzVO();
            zcdzVO.setQj(period);

            zcdzVO.setZcsx(AssetUtil.getAssetProperty(assetproperty));

            if (!StringUtil.isEmptyWithTrim(assetcheckVO.getPk_assetcategory())) {
                BdAssetCategoryVO cateVO = map.get(assetcheckVO
                        .getPk_assetcategory());
                if (cateVO != null) {
                    zcdzVO.setZclb(cateVO.getCatename());
                }
            }

            zcdzVO.setZckm(AssetUtil.getAssetAccount(assetaccount));
            YntCpaccountVO accountVO = accountService.queryMapByPk(pk_corp).get(pk_corp_account);
            zcdzVO.setZzkmbh(accountVO.getAccountcode());
            zcdzVO.setZzkmmc(accountVO.getAccountname());

            zcdzVO.setZcje(new DZFDouble(totalAssetmny));

            zcdzVO.setZzje(totalAccmny);

            zcdzVOs[(index++)] = zcdzVO;
        }
        // 同一个资产科目进行合并
        List<ZcdzVO> reszcdz = new ArrayList<ZcdzVO>();
        for (ZcdzVO zvo : zcdzVOs) {
            if (zvo == null) {
                continue;
            }
            DZFBoolean iscon = DZFBoolean.FALSE;
            if (reszcdz.size() == 0) {
                reszcdz.add(zvo);
            } else {
                for (ZcdzVO zvo1 : reszcdz) {
                    if (zvo1.getZzkmbh().equals(zvo.getZzkmbh())) {
                        zvo1.setZcje(SafeCompute.add(zvo.getZcje(),
                                zvo1.getZcje()));
                        iscon = DZFBoolean.TRUE;
                    }
                }
                if (!iscon.booleanValue()) {
                    reszcdz.add(zvo);
                }
            }
        }
        return reszcdz.toArray(new ZcdzVO[0]);
    }

    @Override
    public void updateExecuteDepreciate(CorpVO corpvo, AssetcardVO[] assetcardVOs, String period, String coperatorid) throws DZFWarpException {
        if (assetcardVOs != null && assetcardVOs.length > 0) {
            // 判断是否转总账
            StringBuilder cardno = new StringBuilder();
            for (AssetcardVO assetvo : assetcardVOs) {
                if (assetvo.getIstogl() == null
                        || !assetvo.getIstogl().booleanValue()) {
                    if (assetvo.getIsperiodbegin() == null
                            || !assetvo.getIsperiodbegin().booleanValue()) {
                        cardno.append(assetvo.getAssetcode() + ",");
                    }
                }
            }

            if (cardno.toString().length() > 0) {
                throw new BusinessException("存在未转总账的资产，资产号为:"
                        + cardno.substring(0, cardno.length() - 1));
            }
            DZFDate date = DateUtils.getPeriodEndDate(period);

            processDepreciations(corpvo, date, assetcardVOs, period,
                    DZFBoolean.TRUE, coperatorid);// 非清理的计提折旧 modify by zhangj
        }
    }
}

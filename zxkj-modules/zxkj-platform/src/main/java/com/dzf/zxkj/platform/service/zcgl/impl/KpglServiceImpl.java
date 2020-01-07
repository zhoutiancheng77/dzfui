package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.PzSourceRelationVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardDisplayColumnVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IAssetCleanService;
import com.dzf.zxkj.platform.service.zcgl.IAssetcardHelper;
import com.dzf.zxkj.platform.service.zcgl.IKpglService;
import com.dzf.zxkj.platform.util.Kmschema;
import com.dzf.zxkj.platform.util.TsCheckUtil;
import com.dzf.zxkj.platform.util.VoUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("am_kpglserv")
@SuppressWarnings("all")
public class KpglServiceImpl implements IKpglService {
    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private ICpaccountService gl_cpacckmserv;

    @Autowired
    private IYntBoPubUtil yntBoPubUtil;

    @Autowired
    private IAuxiliaryAccountService gl_fzhsserv;

    @Autowired
    private IImageGroupService img_groupserv;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ICorpService corpService;

    @Override
    public AssetcardVO save(CorpVO corpvo, AssetcardVO vo)
            throws DZFWarpException {
        if (StringUtil.isEmpty(vo.getPk_assetcard())) {
            vo.setAssetcode(buildAssetcardCode(vo.getPk_corp()));
        }
        checkBeforeSave(corpvo, vo);
        checkBeforeDelAndUpd(vo);
        //非末级科目不让保存
        Map<String, YntCpaccountVO> map = accountService.queryMapByPk(vo.getPk_corp());
        if (vo.getIsperiodbegin() == null ||
                vo.getIsperiodbegin().booleanValue()) {//非期初资产校验
            validateKm(map, vo.getPk_zckm(), "资产科目");//固定(无形)资产科目
            validateKm(map, vo.getPk_jskm(), "结算科目");//结算科目
        }
        validateKm(map, vo.getPk_jtzjkm(), "累计折旧科目");//累计折旧科目
        validateKm(map, vo.getPk_zjfykm(), "折旧费用科目");//折旧费用科目

        AssetcardVO revo = (AssetcardVO) singleObjectBO.saveObject(
                vo.getPk_corp(), vo);
        return revo;
    }

    private void validateKm(Map<String, YntCpaccountVO> map, String key, String tips) {
//		if(!StringUtil.isEmpty(key)){//资产科目
        if (StringUtil.isEmpty(key) || map.get(key) == null) {
            throw new BusinessException(tips + "不存在!");
        } else if (map.get(key).getIsleaf() == null || !map.get(key).getIsleaf().booleanValue()) {
            throw new BusinessException(tips + "(" + map.get(key).getAccountname() + ")非末级科目");
        }
//		}
    }

    /**
     * 保存前校验
     *
     * @param vo
     * @throws BusinessException
     */
    private void checkBeforeSave(CorpVO corpvo, AssetcardVO vo)
            throws DZFWarpException {

        DZFBoolean istogl = vo.getIstogl();
        if (istogl != null && istogl.booleanValue())
            throw new BusinessException("资产卡片已转总账，不允许修改！");
        // 校验期间是否已经结账，如果已经结账，则当月不允许进行任何操作
        DZFDate accountDate = vo.getAccountdate();
        IAssetcardHelper assetcardHelper = (IAssetcardHelper) SpringUtils
                .getBean("assetcardHelperImpl");
        assetcardHelper.checkPeriodIsSettle(vo.getPk_corp(), accountDate);

        DZFDouble salvage = getDZFDouble(vo.getPlansalvage());
        DZFDouble assetmny = getDZFDouble(vo.getAssetmny());
        DZFDouble depreciation = getDZFDouble(vo.getDepreciation());
        DZFDouble assetNetValue = getDZFDouble(vo.getAssetnetvalue());
        if (salvage.compareTo(assetmny) > 0)
            throw new BusinessException("资产原值" + assetmny.toString()
                    + "不能小于资产净值" + salvage.toString());
        if (depreciation.compareTo(assetmny) > 0)
            throw new BusinessException("资产原值不能小于总累计折旧");
        if (assetNetValue.compareTo(salvage) < 0)
            throw new BusinessException("资产净值不能小于资产残值");

        DZFDouble salvageratio = getDZFDouble(vo.getSalvageratio());
        if (salvageratio.compareTo(new DZFDouble(1)) >= 0) {
            throw new BusinessException("[残值率]不能大于1");
        }
        if (assetmny.compareTo(DZFDouble.ZERO_DBL) <= 0)
            throw new BusinessException("[资产原值]不能小于或等于0");
        // if(StringUtil.isEmptyWithTrim(str)==false){
        // // 可使用年限
        // throw new BusinessException(str);
        // }

        int uselimit = vo.getUselimit() == null ? 0 : vo.getUselimit();
        if (uselimit <= 0 && vo.getZjtype() != 1)
            throw new BusinessException("[预计使用年限]不能小于或等于0");
        // 当前资产的日期不能早于计提折旧的日期
        String qmclsql = "select max(period) from ynt_qmcl where nvl(dr,0)=0 and pk_corp = ? and period >= ?  and nvl(iszjjt,'N') ='Y'";
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(DateUtils.getPeriod(corpvo.getBusibegindate()));
        String maxperiod = (String) singleObjectBO.executeQuery(qmclsql, sp,
                new ColumnProcessor());

        if (!StringUtil.isEmpty(maxperiod)) {
//			DZFDate datetemp = DateUtils.getPeriodStartDate(maxperiod);
            if (DateUtils.getPeriod(vo.getAccountdate()).compareTo(maxperiod) <= 0
                    || DateUtils.getPeriod(vo.getPeriod()).compareTo(maxperiod) <= 0) {// 开始使用日期在计提之前不能录入资产
                String year = maxperiod.substring(0, 4);
                String month = maxperiod.substring(5, 7);
                String date_tips = year + "年" + month + "月";
                throw new BusinessException(date_tips + "已计提折旧，不能再录入" + date_tips + "31日之前的卡片，若需录入，请在【总账-期末结转】节点“反计提折旧”后再录入。");
            }
        }


        // 期初=true时，期初折旧和期初折旧期间不能为0
        DZFBoolean isperiodbegin = vo.getIsperiodbegin();
        if (isperiodbegin != null && isperiodbegin.booleanValue()) {
            DZFDouble initDepreciation = getDZFDouble(vo.getInitdepreciation());
            int initDepreciationPeriod = getIntNullAsZero(vo
                    .getInitdepreciationperiod());
            int initusedperiod = getIntNullAsZero(vo.getInitusedperiod());
            if (initDepreciation.compareTo(DZFDouble.ZERO_DBL) < 0)
                throw new BusinessException("录入前已生成凭证资产的[录入前累计折旧]不能小于0");
            if (initDepreciationPeriod < 0)
                throw new BusinessException("录入前已生成凭证资产的[录入前折旧期间数]不能小于0");
            if (initusedperiod < 0)
                throw new BusinessException("录入前已生成凭证资产的[录入前已使用期间数]不能小于0");
            // 入账日期要小于公司固定资产启用日期
//			DZFDate accountdate = vo.getAccountdate();
//			if (corpvo.getBusibegindate() != null
//					&& (accountdate.after(corpvo.getBusibegindate()) || accountdate
//							.equals(corpvo.getBusibegindate()))) {
				/*throw new BusinessException(String.format(
						"期初资产的[资产使用日期]不能大于或等于公司的[固定资产启用日期]%s", corpvo
								.getBusibegindate().toString()));*/
//			}
//			DZFDate period = vo.getPeriod();
//			if (corpvo.getBusibegindate() != null && period.before(corpvo.getBusibegindate())) {
//				throw new BusinessException(String.format(
//						"期初资产的[录入日期]不能在[固定资产启用日期]%s之前", corpvo
//								.getBusibegindate().toString()));
//			}

//			if(!StringUtil.isEmpty(maxperiod)){
//				DZFDate datetemp = DateUtils.getPeriodEndDate(maxperiod);
//				if (vo.getAccountdate().before(datetemp) || vo.getAccountdate().equals(datetemp) ) {
//					throw new BusinessException("期初资产的开始使用日期不能在计提日期(" + maxperiod
//							+ ")之前");
//				}
//			}
        }
//		else {
//			DZFDate accountdate = vo.getAccountdate();
//			if (corpvo.getBusibegindate() != null
//					&& (accountdate.before(corpvo.getBusibegindate()))) {
//				throw new BusinessException(String.format(
//						"非期初资产的[资产使用日期]不能小于公司的[固定资产启用日期]%s", corpvo
//								.getBusibegindate().toString()));
//			}
//		}
        DZFDate period = vo.getPeriod();
        if (corpvo.getBusibegindate() != null && period.before(corpvo.getBusibegindate())) {
            throw new BusinessException(String.format(
                    "资产的[入账日期]不能早于[固定资产启用日期](%s)", corpvo
                            .getBusibegindate().toString()));
        }
        // add by zhangj 如果非期初则默认是1
        // String depreciationdate = (String)
        // getBillCardPanelWrapper().getBillCardPanel().getHeadItem("depreciationdate").getValueObject();
        // if(depreciationdate ==null || depreciationdate.trim().length()
        // ==0){//没折旧过
        // if(!asBoolean(getBillCardPanelWrapper().getBillCardPanel().getHeadItem("isperiodbegin").getValueObject())
        // ){
        // UIRefPane pane = (UIRefPane)
        // getBillCardPanelWrapper().getBillCardPanel().getHeadItem("assetcategory").getComponent();
        // String assetcategory = pane.getRefCode();
        // if(assetcategory!=null && assetcategory.startsWith("01")){
        // getBillCardPanelWrapper().getBillCardPanel().setHeadItem("accountusedperiod",
        // 1);
        // calcUsedPeriod();//重新计算下已使用数
        // }else{
        // getBillCardPanelWrapper().getBillCardPanel().setHeadItem("accountusedperiod",
        // 0);
        // }
        // }else{
        // getBillCardPanelWrapper().getBillCardPanel().setHeadItem("accountusedperiod",
        // 0);
        // }
        // }

    }

    @Override
    public List<AssetcardVO> query(QueryParamVO paramvo)
            throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sqlParam = new SQLParameter();
        sb.append("select ");
        sb.append(StringUtils.join(getJoinFields(),","));
        sb.append(" ,ynt_assetcard.* ");
        sb.append(" from ynt_assetcard ynt_assetcard ");
        sb.append(" left join ynt_category ynt_category on ynt_category.pk_assetcategory = ynt_assetcard.assetcategory ");
        sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_assetcard.pk_voucher ");
        sb.append(" where nvl(ynt_assetcard.dr,0) = 0 and ynt_assetcard.pk_corp = ? ");
        sqlParam.addParam(paramvo.getPk_corp());
        if (paramvo.getBegindate1() != null && paramvo.getEnddate() != null) {
            if (paramvo.getBegindate1().compareTo(paramvo.getEnddate()) == 0) {
                sb.append(" and ynt_assetcard.period = ? ");
                sqlParam.addParam(paramvo.getBegindate1());
            } else {
                sb.append(" and (ynt_assetcard.period >= ? and ynt_assetcard.period <= ?) ");
                sqlParam.addParam(paramvo.getBegindate1());
                sqlParam.addParam(paramvo.getEnddate());
            }
        }
        if (!StringUtil.isEmpty(paramvo.getPk_assetcategory())) {
            sb.append(" and ynt_assetcard.assetcategory = ? ");
            sqlParam.addParam(paramvo.getPk_assetcategory());
        }
        if (!StringUtil.isEmpty(paramvo.getAscode())) {
            sb.append(" and ynt_assetcard.assetcode like ? ");
            sqlParam.addParam("%" + paramvo.getAscode() + "%");
        }
        if (!StringUtil.isEmpty(paramvo.getAsname())) {
            sb.append(" and ynt_assetcard.assetname like ? ");
            sqlParam.addParam("%" + paramvo.getAsname() + "%");
        }
        if (!StringUtil.isEmpty(paramvo.getPk_assetcard())) {
            sb.append(" and ynt_assetcard.pk_assetcard = ? ");
            sqlParam.addParam(paramvo.getPk_assetcard());
        }
        if (paramvo.getIsqc() != null) {
            sb.append(" and nvl(ynt_assetcard.isperiodbegin,'N') = ? ");
            sqlParam.addParam(paramvo.getIsqc());
        }
        if (paramvo.getIstogl() != null) {
            sb.append(" and nvl(ynt_assetcard.istogl, 'N') = ? ");
            sqlParam.addParam(paramvo.getIstogl());
        }
        if (paramvo.getIsclear() != null) {
            sb.append(" and nvl(ynt_assetcard.isclear, 'N') = ? ");
            sqlParam.addParam(paramvo.getIsclear());
        }
        sb.append(" order by assetcode ");

        List<AssetcardVO> listVO = (List<AssetcardVO>) singleObjectBO
                .executeQuery(sb.toString(), sqlParam, new BeanListProcessor(
                        AssetcardVO.class));
        dealResulttVo(listVO);
        return listVO;
    }

    /**
     * 处理查询后结果数据
     *
     * @param listVO
     * @throws BusinessException
     */
    private void dealResulttVo(List<AssetcardVO> listVO)
            throws DZFWarpException {
        if (listVO == null || listVO.size() == 0) {
            return;
        }
        String pk_corp = listVO.get(0).getPk_corp();
        HashMap<String, YntCpaccountVO> accountMap = queryKmByPkcorp(pk_corp);
        //赋值辅助信息
        Map<String, AuxiliaryAccountBVO> map = gl_fzhsserv.queryMap(pk_corp);
        for (AssetcardVO vo : listVO) {
            YntCpaccountVO accountdVO = accountMap.get(vo.getPk_yzrzkm());
            if (accountdVO != null) {
                vo.setYzrzkm(accountdVO.getAccountname());
            }
            if (accountMap.get(vo.getPk_zjkm()) != null) {
                vo.setZjkm(accountMap.get(vo.getPk_zjkm()).getAccountname());
            }
            if (accountMap.get(vo.getPk_fykm()) != null) {
                vo.setFykm(accountMap.get(vo.getPk_fykm()).getAccountname());
            }

            if (accountMap.get(vo.getPk_zckm()) != null) {
                vo.setZckmcode(accountMap.get(vo.getPk_zckm()).getAccountcode());
                vo.setZckm(vo.getZckmcode() + " " + accountMap.get(vo.getPk_zckm()).getFullname());
            }
            if (accountMap.get(vo.getPk_jskm()) != null) {
                StringBuffer fzname = getFzName(accountMap, map, vo, "jsfzhsx", vo.getPk_jskm());
                StringBuffer fzcode = getFzCode(accountMap, map, vo, "jsfzhsx", vo.getPk_jskm());
                vo.setJskm(fzcode + " " + fzname.toString());
                vo.setJskmcode(fzcode.toString());
            }
            if (accountMap.get(vo.getPk_zjfykm()) != null) {
//                vo.setZjfykmcode(accountMap.get(vo.getPk_zjfykm()).getAccountcode());
//                vo.setZjfykm(vo.getZjfykmcode() + " " + accountMap.get(vo.getPk_zjfykm()).getFullname());
                StringBuffer fzname = getFzName(accountMap, map, vo, "zjfyfzhsx", vo.getPk_zjfykm());
                StringBuffer fzcode = getFzCode(accountMap, map, vo, "zjfyfzhsx", vo.getPk_zjfykm());
                vo.setZjfykm(fzcode + " " + fzname.toString());
                vo.setZjfykmcode(fzcode.toString());
            }
            if (accountMap.get(vo.getPk_jtzjkm()) != null) {
                vo.setJtzjkmcode(accountMap.get(vo.getPk_jtzjkm()).getAccountcode());
                vo.setJtzjkm(vo.getJtzjkmcode() + " " + accountMap.get(vo.getPk_jtzjkm()).getFullname());
            }

            vo.setSumres(SafeCompute.add(vo.getNjxsf(), vo.getAssetmny()));
        }
    }

    private StringBuffer getFzCode(HashMap<String, YntCpaccountVO> accountMap, Map<String, AuxiliaryAccountBVO> map,
                                   AssetcardVO vo, String fieldName, String kmbm) {
        StringBuffer fzname = new StringBuffer();
        fzname.append(accountMap.get(kmbm).getAccountcode());
        String key = "";
        AuxiliaryAccountBVO fzvo = null;
        for (int i = 1; i <= 10; i++) {
            key = (String) vo.getAttributeValue(fieldName + i);
            if (!StringUtil.isEmpty(key)) {
                fzvo = map.get(key);
                if (fzvo != null) {
                    fzname.append("_" + fzvo.getCode());
                }
            }
        }
        return fzname;
    }

    private StringBuffer getFzName(HashMap<String, YntCpaccountVO> accountMap, Map<String, AuxiliaryAccountBVO> map,
                                   AssetcardVO vo, String fieldName, String kmbm) {
        StringBuffer fzname = new StringBuffer();
        fzname.append(accountMap.get(kmbm).getAccountname());
        String key = "";
        AuxiliaryAccountBVO fzvo = null;
        for (int i = 1; i <= 10; i++) {
            key = (String) vo.getAttributeValue(fieldName + i);
            if (!StringUtil.isEmpty(key)) {
                fzvo = map.get(key);
                if (fzvo != null) {
                    fzname.append("_" + fzvo.getName());
                }
            }
        }
        return fzname;
    }

    /**
     * 查询科目数据
     *
     * @param pkCorp
     * @return
     * @throws BusinessException
     */
    private HashMap<String, YntCpaccountVO> queryKmByPkcorp(String pkCorp)
            throws DZFWarpException {
        List<YntCpaccountVO> listVO = (List<YntCpaccountVO>) singleObjectBO
                .retrieveByClause(YntCpaccountVO.class,
                        "nvl(dr,0) = 0 and pk_corp='" + pkCorp + "'", null);

        HashMap<String, YntCpaccountVO> accountMap = listVO.stream().collect(Collectors.toMap(YntCpaccountVO::getPk_corp_account, a -> a,(k1, k2)->k1, HashMap::new));

        return accountMap;
    }

    private String[] getJoinFields() {
        String[] joinFields = new String[]{
                "ynt_tzpz_h.pzh as voucherno",
                "ynt_category.catename as assetcate",
                "ynt_category.catecode || '_' ||ynt_category.catename as assetcateall",
                "case ynt_category.assetproperty when 0 then '固定资产' when 1 then '无形资产' when 2 then '固定资产'  when 3 then '待摊费用' end as assetproperty"};
        return joinFields;
    }

    @Override
    public void delete(AssetcardVO[] vos) throws DZFWarpException {
        for (AssetcardVO vo : vos) {
            checkBeforeDelAndUpd(vo);
            IAssetcardHelper assetcardHelper = (IAssetcardHelper) SpringUtils
                    .getBean("assetcardHelperImpl");
            assetcardHelper.checkPeriodIsSettle(vo.getPk_corp(),
                    vo.getAccountdate());
        }
        singleObjectBO.deleteVOArray(vos);
    }

    @Override
    public AssetcardVO queryById(String id) throws DZFWarpException {
        return (AssetcardVO) singleObjectBO.queryVOByID(id, AssetcardVO.class);

    }

    @Override
    public AssetcardVO update(CorpVO corpvo, AssetcardVO vo)
            throws DZFWarpException {
        checkBeforeDelAndUpd(vo);
        checkBeforeSave(corpvo, vo);
        vo.setAccountmny(vo.getAssetmny());
        checkUpTs(vo);
        Map<String, YntCpaccountVO> cpamap = accountService.queryMapByPk(vo.getPk_corp());
        validateKm(cpamap, vo.getPk_zckm(), "资产科目");//固定(无形)资产科目
        validateKm(cpamap, vo.getPk_jskm(), "结算科目");//结算科目
        validateKm(cpamap, vo.getPk_jtzjkm(), "累计折旧科目");//累计折旧科目
        validateKm(cpamap, vo.getPk_zjfykm(), "折旧费用科目");//折旧费用科目
        AssetcardVO revo = (AssetcardVO) singleObjectBO.saveObject(
                vo.getPk_corp(), vo);

        return revo;
    }

    private void checkUpTs(AssetcardVO vo) {
        if (!StringUtil.isEmpty(vo.getPrimaryKey())) {
            AssetcardVO qryvo = (AssetcardVO) singleObjectBO.queryByPrimaryKey(AssetcardVO.class, vo.getPrimaryKey());
            if (!qryvo.getUpdatets().equals(vo.getUpdatets())) {
                throw new BusinessException("数据已变化，请刷新重试");
            }
        }
    }

    private boolean isEmptyObj(Object obj) {
        if (obj instanceof String) {
            return "".equals(obj);
        } else {
            return obj == null;
        }
    }

    /**
     * 删除与修改前校验
     *
     * @param vo
     * @throws BusinessException
     */
    private void checkBeforeDelAndUpd(AssetcardVO vo) throws DZFWarpException {
        if (vo.getIstogl() != null && vo.getIstogl().booleanValue())
            throw new BusinessException(String.format("资产卡片%s 已转总账，不能操作！",
                    vo.getAssetcode()));
        //已经清理不能修改保存
        if (vo.getIsclear() != null && vo.getIsclear().booleanValue()) {
            throw new BusinessException(String.format("资产卡片%s 已清理，不能操作！",
                    vo.getAssetcode()));
        }

        // 已经原值变更，不能删除
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_assetcard());
        sp.addParam(vo.getPk_corp());
        ValuemodifyVO[] bgvos = (ValuemodifyVO[]) singleObjectBO
                .queryByCondition(ValuemodifyVO.class,
                        " nvl(dr,0)=0 and pk_assetcard = ? and pk_corp =? ", sp);
        if (bgvos != null && bgvos.length > 0) {
            throw new BusinessException(String.format("资产卡片%s 已原值变更，不能操作！",
                    vo.getAssetcode()));
        }
        checkEdit(vo.getPk_corp(), vo);
        if (vo.getIsperiodbegin() != null && vo.getIsperiodbegin().booleanValue()) {
            checkHasJT(vo);
        }
    }

    private void checkHasJT(AssetcardVO vo) throws DZFWarpException {
        CorpVO assetCorpVo = corpService.queryByPk(vo.getPk_corp());
        DZFDate busIBeginDate = assetCorpVo.getBusibegindate();
        if (busIBeginDate == null) {
            throw new BusinessException("当前公司启用固定资产日期为空!");
        }
    }

    @Override
    public List<AssetcardVO> queryByPkcorp(String loginDate, String pk_corp,
                                           String isclear) throws DZFWarpException {
        if (StringUtil.isEmptyWithTrim(isclear)) {
            isclear = "N";
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(isclear);
        sp.addParam(pk_corp);
        StringBuilder sb = new StringBuilder();
        sb.append(" nvl(dr,0) = 0 and (nvl(istogl,'N') = 'Y' or nvl(isperiodbegin,'N') = 'Y')");
        sb.append(" and nvl(isclear,'N')=?  and pk_corp=? ");
        if(!"Y".equals(isclear)){
            sb.append(" and  nvl(assetnetvalue,0)>nvl(plansalvage,0) ");//如果已经折旧完毕，则不显示(如果资产净值大于预估残值，才显示)
        }
        if (loginDate != null) {
            sb.append(" and accountdate <=?");
            sp.addParam(loginDate);
        }
        sb.append("order by assetcode ");
        List<AssetcardVO> listVo = (List<AssetcardVO>) singleObjectBO
                .retrieveByClause(AssetcardVO.class, sb.toString(), sp);
        dealResulttVo(listVo);
        return listVo;
    }

    @Override
    public List<AssetcardVO> queryByPkCorp(String pk_corp)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<AssetcardVO> listVo = (List<AssetcardVO>) singleObjectBO
                .retrieveByClause(AssetcardVO.class,
                        " nvl(dr,0) = 0 and pk_corp=?", sp);
        return listVo;
    }

    @Override
    public void saveToGl(AssetcardVO vo) throws DZFWarpException {
        checkBeforeToGl(vo);
    }

    /**
     * 转总账前校验
     *
     * @param vo
     * @throws BusinessException
     */
    private void checkBeforeToGl(AssetcardVO vo) throws DZFWarpException {
        if (vo == null)
            return;
        if (vo.getIstogl() != null && vo.getIstogl().booleanValue()) {
            throw new BusinessException(String.format("资产卡片%s 已经转总账，不能重复转总账！",
                    vo.getAssetcode()));
        }
        if (vo.getIssettle() != null && vo.getIssettle().booleanValue()) {
            throw new BusinessException(String.format("资产卡片%s 已经结账，不能转总账！",
                    vo.getAssetcode()));
        }
        if (vo.getIsclear() != null && vo.getIsclear().booleanValue()) {
            throw new BusinessException(String.format("资产卡片%s 已经清理，不能转总账！",
                    vo.getAssetcode()));
        }
        if (vo.getIsperiodbegin() != null
                && vo.getIsperiodbegin().booleanValue()) {
            throw new BusinessException(String.format("资产卡片%s是录入前已生成凭证资产，不能转总账",
                    vo.getAssetcode()));
        }
        IAssetcardHelper assetCardHelper = (IAssetcardHelper) SpringUtils
                .getBean("assetcardHelperImpl");
        assetCardHelper.checkPeriodIsSettle(vo.getPk_corp(),
                vo.getAccountdate());

    }

    private DZFDouble getDZFDouble(DZFDouble obj) {
        return obj == null ? DZFDouble.ZERO_DBL : obj;
    }

    private int getIntNullAsZero(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * 构建固定资产编号
     *
     * @return
     */
    public String buildAssetcardCode(String pkCorp) throws DZFWarpException {
        SQLParameter params = new SQLParameter();
        params.addParam(pkCorp);
        String sql = "select max(assetcode) from ynt_assetcard where nvl(dr,0)=0 and length(assetcode)=6 and pk_corp= ?";

        int maxcode = 0;

        Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params,
                new ArrayProcessor());
        if (array != null && array.length > 0) {
            if (array[0] != null)
                maxcode = Integer.parseInt(array[0].toString());
        }

        return String.format("%06d", maxcode + 1);
    }

    private Integer buildMaxCode(String pkCorp) throws DZFWarpException {
        SQLParameter params = new SQLParameter();
        params.addParam(pkCorp);
        String sql = "select max(assetcode) from ynt_assetcard where nvl(dr,0)=0 and length(assetcode)=6 and pk_corp= ?";

        int maxCode = 0;

        Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params,
                new ArrayProcessor());
        if (array != null && array.length > 0) {
            if (array[0] != null)
                maxCode = Integer.parseInt(array[0].toString());
        }
        return maxCode;
    }

    /**
     * 资产清理
     *
     * @throws Exception
     */
    @Override
    public void updateAssetClear(String loginDate, CorpVO corpvo,
                                 AssetcardVO[] selectedVOs, String loginuserid) throws DZFWarpException {
        if (selectedVOs == null || selectedVOs.length == 0)
            throw new BusinessException("没有选择资产卡片记录");
        //需要重新查询资产id
        List<String> zcids = new ArrayList<>();
        for (AssetcardVO cardvo : selectedVOs) {
            zcids.add(cardvo.getPk_assetcard());
        }
        selectedVOs = (AssetcardVO[]) singleObjectBO.queryByCondition(AssetcardVO.class, "nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_assetcard", zcids.toArray(new String[0])), new SQLParameter());
        if (selectedVOs == null || selectedVOs.length == 0)
            throw new BusinessException("没有选择资产卡片记录");

        ArrayList<String> periods = new ArrayList<String>();
        IAssetcardHelper assetCardHelper = (IAssetcardHelper) SpringUtils.getBean("assetcardHelperImpl");
        for (AssetcardVO selectedVO : selectedVOs) {
            checkBeforeClear(loginDate, selectedVO);
            // 入账期间
            String period = DateUtils.getPeriod(selectedVO.getAccountdate());
            if (!periods.contains(period)) {
                assetCardHelper.checkPeriodIsSettle(selectedVO.getPk_corp(),
                        new DZFDate(loginDate));
                periods.add(period);
            }
        }
        IAssetCleanService assetClearImpl = (IAssetCleanService) SpringUtils
                .getBean("am_assetclsserv");
        assetClearImpl.processAssetClears(loginDate, corpvo, selectedVOs, loginuserid);
    }

    private void checkBeforeClear(String loginDate, AssetcardVO vo)
            throws DZFWarpException {
        DZFDate accountDate = vo.getAccountdate();
        if (accountDate.compareTo(new DZFDate(loginDate)) > 0) {
            throw new BusinessException("资产卡片[" + vo.getAssetcode()
                    + "]清理日期必须晚于开始使用日期。");
        }
        if (vo.getIsperiodbegin() == null
                || !vo.getIsperiodbegin().booleanValue()) {
            if (vo.getIstogl() == null || !vo.getIstogl().booleanValue()) {
                throw new BusinessException("资产卡片" + vo.getAssetcode()
                        + "未转总账，不允许生成资产清理单。");
            }
        }
        if (vo.getIsclear() != null && vo.getIsclear().booleanValue()) {
            throw new BusinessException("资产卡片" + vo.getAssetcode()
                    + "已经清理，不允许生成资产清理单。");
        } else {
            AssetcardVO newVo = (AssetcardVO) singleObjectBO.queryByPrimaryKey(
                    AssetcardVO.class, vo.getPk_assetcard());
            if (newVo.getIsclear() != null && newVo.getIsclear().booleanValue()) {
                throw new BusinessException("资产卡片" + newVo.getAssetcode()
                        + "已经清理，不允许生成资产清理单。");
            }
        }
    }

    @Override
    public List<AssetcardVO> queryByIds(String ids) throws DZFWarpException {
        String sql = buildExpSql(ids);
        List<AssetcardVO> listVO = (List<AssetcardVO>) singleObjectBO
                .executeQuery(sql, null, new BeanListProcessor(
                        AssetcardVO.class));
        dealResulttVo(listVO);
        return listVO;
    }

    private String buildExpSql(String ids) {
        StringBuilder sb = new StringBuilder();
        String[] joinFields = getJoinFields();
        sb.append("select ");
        for (String field : joinFields) {
            sb.append(field);
            sb.append(",");
        }
        sb.append(" ynt_assetcard.* ");
        sb.append(" from ynt_assetcard ynt_assetcard ");
        sb.append(" left join ynt_category ynt_category on ynt_category.pk_assetcategory = ynt_assetcard.assetcategory ");
        sb.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_assetcard.pk_voucher ");
        sb.append(" where nvl(ynt_assetcard.dr,0) = 0 and ynt_assetcard.pk_assetcard in (");
        sb.append(ids);
        sb.append(")");
        sb.append(" order by assetcode ");
        return sb.toString();
    }

    @Override
    public String checkEdit(String pk_corp, AssetcardVO cardvo) throws DZFWarpException {

        if (cardvo.getIsperiodbegin() != null && cardvo.getIsperiodbegin().booleanValue()) {
            //如果已经折旧则不允许修改
            if (!StringUtil.isEmpty(cardvo.getPrimaryKey())) {
                SQLParameter sp = new SQLParameter();
                sp.addParam(cardvo.getPk_assetcard());
                sp.addParam(pk_corp);
                AssetDepreciaTionVO[] vos = (AssetDepreciaTionVO[]) singleObjectBO.queryByCondition(AssetDepreciaTionVO.class, "nvl(dr,0)=0 and pk_assetcard=? and pk_corp= ?", sp);
                if (vos != null && vos.length > 0) {
                    throw new BusinessException("该期初资产已经折旧，不允许修改");
                }
            }
        }

        if (!StringUtil.isEmpty(cardvo.getPrimaryKey())) {
            boolean ischange = TsCheckUtil.isDataChange(cardvo);
            if (ischange) {
                throw new BusinessException("数据已变更,请刷新页面");
            }
        }

        return "";
    }

    @Override
    public Object[] impExcel(String loginDate, String userid, CorpVO corpvo,
                             AssetcardVO[] selectedVOs) throws DZFWarpException {
        int len = selectedVOs.length;
        HashMap<String, BdAssetCategoryVO> map = queryAssetCategoryMap(corpvo.getPk_corp());
        Integer maxCode = buildMaxCode(corpvo.getPk_corp());
        StringBuilder tips = new StringBuilder();
        YntCpaccountVO[] cpavos = accountService.queryByPk(
                corpvo.getPk_corp());
        if (cpavos.length == 0) {
            throw new BusinessException("导入文档失败：该公司对应的科目信息为空!");
        }
        Map<String, YntCpaccountVO> cpamap = new HashMap<String, YntCpaccountVO>();
        for (YntCpaccountVO cpavo : cpavos) {
            cpamap.put(cpavo.getAccountcode(), cpavo);
        }
        List<AssetcardVO> resassetvos = new ArrayList<AssetcardVO>();
        ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils
                .getBean("gl_accountcoderule");
        ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils
                .getBean("gl_cpacckmserv");
        String newrule = gl_cpacckmserv.queryAccountRule(corpvo.getPrimaryKey());
        StringBuilder tips2 = new StringBuilder();// 资产科目提示
        Map<String, AuxiliaryAccountBVO[]> fzmap = new HashMap<String, AuxiliaryAccountBVO[]>();
        for (int i = 0; i < len; i++) {
            try {
                if(selectedVOs[i].getZccode() != null && selectedVOs[i].getZccode().length() > 15){
                    tips.append("资产编码:" + selectedVOs[i].getZccode()
                            + "长度超过15位<br>");
                    continue;
                }
                String assetcode = String.format("%06d", maxCode + (i + 1));
                if (selectedVOs[i].getZjtype() == 1) {//工作量法
                    selectedVOs[i].setUselimit(null);//预计使用年限为空
                }
                selectedVOs[i].setAssetcode(assetcode);
                selectedVOs[i].setAccountdate(selectedVOs[i].getPeriod());
                selectedVOs[i].setPk_corp(corpvo.getPk_corp());
                selectedVOs[i].setDoperatedate(new DZFDate(loginDate));
                selectedVOs[i].setCoperatorid(userid);
                selectedVOs[i].setIstogl(DZFBoolean.FALSE);
                if (selectedVOs[i].getAssetmny() != null) {
                    selectedVOs[i].setAccountmny(selectedVOs[i].getAssetmny().setScale(2, DZFDouble.ROUND_HALF_UP));
                }
                String assetcate = selectedVOs[i].getAssetcate();
                String[] assetcates = assetcate.split("_");
                BdAssetCategoryVO vo = map.get(assetcates[0]);
                if (vo == null) {
                    tips.append("资产编码:" + selectedVOs[i].getZccode()
                            + "对应的资产类别" + assetcates[0] + "不存在，导入失败!<br>");
                    continue;
                }
                // 1.7 导入资产卡片校验科目末级  201908需求
                String tip = checkKmIsLeaf(cpamap, selectedVOs[i]);
                if(!StringUtil.isEmptyWithTrim(tip)){
                    tips.append(tip);
                    continue;
                }

                if (selectedVOs[i].getZjtype() == null) {
                    tips.append("资产编码:" + selectedVOs[i].getZccode()
                            + "对应的折旧方式不存在，导入失败!<br>");
                    continue;
                }

                selectedVOs[i].setAssetcategory(vo.getPk_assetcategory());
                selectedVOs[i].setAssetcate(vo.getCatename());
                String assetproperty = getAssetProperty(vo.getAssetproperty());
                selectedVOs[i].setAssetproperty(assetproperty);
               
                if (selectedVOs[i].getSalvageratio() != null
                        && selectedVOs[i].getAssetmny() != null) {
                    selectedVOs[i].setPlansalvage(selectedVOs[i].getAssetmny()
                            .multiply(selectedVOs[i].getSalvageratio())
                            .setScale(2, DZFDouble.ROUND_HALF_UP));// 预计残值
                }
                if (selectedVOs[i].getGzzl() == null) {
                    selectedVOs[i].setGzzl(DZFDouble.ZERO_DBL);
                }
                if (selectedVOs[i].getLjgzl() == null) {
                    selectedVOs[i].setLjgzl(DZFDouble.ZERO_DBL);
                }
                //如果是期初，做如下操作
                if (selectedVOs[i].getIsperiodbegin() != null && selectedVOs[i].getIsperiodbegin().booleanValue()) {
                    //qcljgzl//期初累计工作量               ljgzl//累计工作量
                    selectedVOs[i].setLjgzl(selectedVOs[i].getQcljgzl());
                    //initdepreciation//期初累计折旧                 depreciation //总累计折旧
                    if (selectedVOs[i].getInitdepreciation() != null) {
                        selectedVOs[i].setDepreciation(selectedVOs[i].getInitdepreciation().setScale(2, DZFDouble.ROUND_HALF_UP));
                    }
                    //initdepreciationperiod//期初折旧期间数(月)    depreciationperiod//已计提折旧期间
                    selectedVOs[i].setDepreciationperiod(selectedVOs[i].getInitdepreciationperiod());
                    //initusedperiod//期初已使用期间数(月)   usedperiod//总累计使用期间月(数)
                    selectedVOs[i].setUsedperiod(selectedVOs[i].getInitusedperiod());
                    //assetmny//原值                        //合计sumres
                    if (selectedVOs[i].getAssetmny() != null) {
                        selectedVOs[i].setSumres(selectedVOs[i].getAssetmny().setScale(2, DZFDouble.ROUND_HALF_UP));
                    }
                }
                //资产净值
                selectedVOs[i].setAssetnetvalue(SafeCompute.sub(
                        selectedVOs[i].getAssetmny(),
                        selectedVOs[i].getDepreciation()).setScale(2, DZFDouble.ROUND_HALF_UP));
                selectedVOs[i].setQcnetvalue(SafeCompute.sub(
                        selectedVOs[i].getAssetmny(),
                        selectedVOs[i].getDepreciation()).setScale(2, DZFDouble.ROUND_HALF_UP));
                selectedVOs[i].setDwzj(calDwzj(selectedVOs[i]).setScale(2, DZFDouble.ROUND_HALF_UP));
                selectedVOs[i].setMonthzj(calcYzj(selectedVOs[i]).setScale(2, DZFDouble.ROUND_HALF_UP));//月折旧
                checkNull(selectedVOs[i]);
                checkBeforeSave(corpvo, selectedVOs[i]);
                //是否重复
                String repeat_tips = brepeat_sig(selectedVOs[i], corpvo.getPk_corp());
                if (!StringUtil.isEmpty(repeat_tips)) {
                    throw new BusinessException(repeat_tips + "<br>");
                }
                handleKM(selectedVOs, cpamap, tips2, i, gl_accountcoderule,
                        newrule, corpvo.getPk_corp(), fzmap);// 处理科目信息
                singleObjectBO.insertVOArr(corpvo.getPk_corp(),
                        new AssetcardVO[]{selectedVOs[i]});// 每次只是保存一个
                resassetvos.add(selectedVOs[i]);
            } catch (BusinessException e) {
                tips.append("资产编码:" + selectedVOs[i].getZccode() + "对应的："
                        + e.getMessage() + "<br>");
            }
        }

        Object[] objs = new Object[3];
        objs[0] = resassetvos.toArray(new AssetcardVO[0]);
        if (tips.length() > 0) {
            objs[1] = "导入失败:<br>" + tips.toString();
        } else {
            objs[1] = "";
        }
        if (tips2.length() > 0) {
            objs[2] = "警告:<br>" + tips2.toString();
        } else {
            objs[2] = "";
        }
        return objs;
    }

    /**
     *  检验末级
     */
    private String checkKmIsLeaf(Map<String, YntCpaccountVO> cpamap, AssetcardVO assetcardVO) {
        String[] checkKm = new String[]{assetcardVO.getPk_zckm(), assetcardVO.getPk_jskm(), assetcardVO.getPk_jtzjkm(), assetcardVO.getPk_zjfykm()};
        String[] tipXm = new String[]{"固定（无形）资产科目","结算科目","折旧（摊销）科目","折旧（摊销）费用科目"};
        String tip = "";
        for(int i =0; i < checkKm.length; i++){
            if (!StringUtil.isEmptyWithTrim(checkKm[i]) && cpamap.containsKey(checkKm[i])) {
                YntCpaccountVO yntCpaccountVO = cpamap.get(checkKm[i]);
                if (!yntCpaccountVO.getIsleaf().booleanValue()) {
                    tip += "资产的["+tipXm[i]+":"+checkKm[i]+"]非末级，";
//                    break;
                }
            }
        }

        if(!StringUtil.isEmptyWithTrim(tip)){
            tip = "资产编码:"+assetcardVO.getZccode()+"对应的："+tip + "请检查<br/>";
        }

        return tip;
    }

    /**
     * 计算单位折旧
     *
     * @param assetcardVO
     * @return
     */
    private DZFDouble calDwzj(AssetcardVO assetcardVO) {
        DZFDouble dwzj = DZFDouble.ZERO_DBL;
        if (assetcardVO.getZjtype() != null
                && assetcardVO.getZjtype() == 1) {//工作量法//zpm修改
            //原值
            DZFDouble yz = SafeCompute.sub(assetcardVO.getAssetmny(), assetcardVO.getDepreciation());
            //总量
            DZFDouble zl = SafeCompute.sub(assetcardVO.getGzzl(), assetcardVO.getLjgzl());
            DZFDouble newyz = SafeCompute.sub(yz, assetcardVO.getPlansalvage());//预计残值
            if (zl.doubleValue() != 0) {
                dwzj = SafeCompute.div(newyz, zl);
                dwzj = dwzj.setScale(2, DZFDouble.ROUND_HALF_UP);
            }
        }
        return dwzj;
    }

    private void handleKM(AssetcardVO[] selectedVOs,
                          Map<String, YntCpaccountVO> cpamap, StringBuilder tips2, int i,
                          ICpaccountCodeRuleService gl_accountcoderule, String newrule, String pk_corp, Map<String, AuxiliaryAccountBVO[]> fzmap) {
        if (!StringUtil.isEmpty(selectedVOs[i].getPk_zckm())) {
            YntCpaccountVO tempvo = cpamap.get(selectedVOs[i].getPk_zckm());
            if (tempvo == null) {
                tempvo = cpamap.get(gl_accountcoderule
                        .getNewRuleCode(selectedVOs[i].getPk_zckm(),
                                DZFConstant.ACCOUNTCODERULE, newrule));
            }
            if (tempvo == null) {
                tips2.append("资产编码:" + selectedVOs[i].getZccode()
                        + "对应的固定(无形)资产科目" + selectedVOs[i].getPk_zckm()
                        + "不存在!<br>");
                selectedVOs[i].setPk_zckm(null);
            } else {
                selectedVOs[i].setZckm(tempvo.getAccountname());
                selectedVOs[i].setPk_zckm(tempvo.getPrimaryKey());
            }
        }
        if (!StringUtil.isEmpty(selectedVOs[i].getPk_jskm())) {
            String jskm = selectedVOs[i].getPk_jskm();
            String[] jskms = jskm.split("_");
            YntCpaccountVO tempvo = cpamap.get(jskms[0]);
            if (tempvo == null) {
                tempvo = cpamap.get(gl_accountcoderule.getNewRuleCode(jskms[0], DZFConstant.ACCOUNTCODERULE, newrule));
            }
            if (tempvo == null) {
                tips2.append("资产编码:" + selectedVOs[i].getZccode() + "对应的结算科目"
                        + selectedVOs[i].getPk_jskm() + "不存在!<br>");
                selectedVOs[i].setPk_jskm(null);
            } else {
                // 处理辅助核算
                selectedVOs[i].setJskm(tempvo.getAccountname());
                selectedVOs[i].setPk_jskm(tempvo.getPrimaryKey());
                handFzhs(selectedVOs, tips2, i, pk_corp, fzmap, jskms, tempvo);
            }
        }
        if (!StringUtil.isEmpty(selectedVOs[i].getPk_jtzjkm())) {
            YntCpaccountVO tempvo = cpamap.get(selectedVOs[i].getPk_jtzjkm());
            if (tempvo == null) {
                tempvo = cpamap.get(gl_accountcoderule
                        .getNewRuleCode(selectedVOs[i].getPk_jtzjkm(),
                                DZFConstant.ACCOUNTCODERULE, newrule));
            }
            if (tempvo == null) {
                tips2.append("资产编码:" + selectedVOs[i].getZccode()
                        + "对应的计提(摊销)科目" + selectedVOs[i].getPk_jtzjkm()
                        + "不存在!<br>");
                selectedVOs[i].setPk_jtzjkm(null);
            } else {
                selectedVOs[i].setJtzjkm(tempvo.getAccountname());
                selectedVOs[i].setPk_jtzjkm(tempvo.getPrimaryKey());
            }
        }
        if (!StringUtil.isEmpty(selectedVOs[i].getPk_zjfykm())) {
            YntCpaccountVO tempvo = cpamap.get(selectedVOs[i].getPk_zjfykm());
            if (tempvo == null) {
                tempvo = cpamap.get(gl_accountcoderule
                        .getNewRuleCode(selectedVOs[i].getPk_zjfykm(),
                                DZFConstant.ACCOUNTCODERULE, newrule));
            }
            if (tempvo == null) {
                tips2.append("资产编码:" + selectedVOs[i].getZccode()
                        + "对应的计提(摊销)费用科目" + selectedVOs[i].getPk_zjfykm()
                        + "不存在!<br>");
                selectedVOs[i].setPk_zjfykm(null);
            } else {
                selectedVOs[i].setZjfykm(tempvo.getAccountname());
                selectedVOs[i].setPk_zjfykm(tempvo.getPrimaryKey());
            }
        }
    }

    private void handFzhs(AssetcardVO[] selectedVOs, StringBuilder tips2, int i, String pk_corp,
                          Map<String, AuxiliaryAccountBVO[]> fzmap, String[] jskms, YntCpaccountVO tempvo) {
        //查找对应的辅助核算的数组
        List<Integer> fzhsIndex = new ArrayList<>();
        if (tempvo.getIsfzhs().contains("1")) {
            for (int j = 0; j < 10; j++) {
                String value = tempvo.getIsfzhs().substring(j, j + 1);
                if ("1".equals(value)) {
                    fzhsIndex.add(j + 1);
                }
            }
        }

        if (fzhsIndex.size() != (jskms.length - 1)) {
            tips2.append("资产编码:" + selectedVOs[i].getZccode() + "结算科目中辅助核算值与科目设置的辅助核算对应错误!<br>");
        }

        if (fzhsIndex.size() > 0) {
            boolean bmatch;
            for (int k = 1; k < jskms.length; k++) {
                bmatch = false;
                //查找对应的辅助核算
                Integer fzhslx = fzhsIndex.get(k - 1);
                if (!fzmap.containsKey(fzhslx + "")) {
                    AuxiliaryAccountBVO[] lbvos = gl_fzhsserv.queryAllBByLb(pk_corp, fzhslx + "");
                    fzmap.put(fzhslx + "", lbvos);
                }
                AuxiliaryAccountBVO[] bvos = fzmap.get(fzhslx + "");
                if (bvos == null || bvos.length == 0) {
                    tips2.append("资产编码:" + selectedVOs[i].getZccode() + "结算科目对应的辅助项不存在!<br>");
                } else {
                    for (AuxiliaryAccountBVO vo : bvos) {
                        if (vo.getCode().equals(jskms[k])) {
                            selectedVOs[i].setAttributeValue("jsfzhsx" + k, vo.getPk_auacount_b());
                            selectedVOs[i].setJskm(selectedVOs[i].getJskm() + "_" + vo.getName());
                            bmatch = true;
                        }
                    }
                }
                if (!bmatch) {
                    tips2.append("资产编码:" + selectedVOs[i].getZccode() + "结算科目对应的辅助项不存在!<br>");
                }
            }
        }
    }

    private void checkNull(AssetcardVO vo) throws DZFWarpException {
        String zcName = vo.getAssetname();
        if (StringUtil.isEmpty(zcName)) {
            throw new BusinessException("资产名称不能为空！");
        }
        if (StringUtil.isEmpty(vo.getAssetcategory())) {
            throw new BusinessException("资产类别不能为空！");
        }

        if(vo.getPeriod() == null ){
        	throw new BusinessException("入账日期格式不正确或内容为空，请检查!");
        }
        if (vo.getAccountdate() == null) {
            throw new BusinessException("开始日期格式不正确或内容为空，请检查!");
        }

//        if((vo.getIsperiodbegin() == null || !vo.getIsperiodbegin().booleanValue()) && StringUtil.isEmptyWithTrim(vo.getJskmcode())){
//            throw new BusinessException("结算科目为空，请检查!");
//        }

        if (vo.getZjtype() == null) {
            throw new BusinessException("折旧方式格式不正确或内容为空，请检查!");
        }
        if (vo.getZjtype() != null) {
            if (vo.getZjtype() != 1) {
                if (vo.getUselimit() == null) {
                    throw new BusinessException("预计使用年限格式不正确或内容为空，请检查!");
                }

                if (vo.getDepreciationperiod() != null
                        && vo.getDepreciationperiod() > vo
                        .getUselimit()) {
                    throw new BusinessException("已计提折旧期间大于预计使用期间!");
                }
            }
        }
        if (vo.getAssetmny() == null) {
            throw new BusinessException("原值格式不正确或内容为空，请检查!");
        }
    }

    private HashMap<String, BdAssetCategoryVO> queryAssetCategoryMap(String pk_corp)
            throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(IGlobalConstants.currency_corp);
        BdAssetCategoryVO[] vos = (BdAssetCategoryVO[]) singleObjectBO
                .queryByCondition(BdAssetCategoryVO.class, "nvl(dr,0)=0 and (pk_corp = ? or pk_corp = ?)", sp);
        HashMap<String, BdAssetCategoryVO> map = new HashMap<>();
        if (vos != null && vos.length > 0) {
            for (BdAssetCategoryVO vo : vos) {
                map.put(vo.getCatecode(), vo);
            }
        }
        return map;
    }

    private String getAssetProperty(int assetProperty) {
        switch (assetProperty) {
            case 0:
                return "固定资产";
            case 1:
                return "无形资产";
            case 2:
                return "待摊费用";
        }
        return "";
    }

    /**
     * 计算月折旧
     *
     * @param vo
     * @return
     */
    private DZFDouble calcYzj(AssetcardVO vo) {
        DZFDouble yzj = DZFDouble.ZERO_DBL;
        if (vo.getZjtype() == 0) {//平均年限法//zpm重写
            DZFDouble yz = SafeCompute.sub(vo.getAssetmny(), vo.getDepreciation());
            DZFDouble newyz = SafeCompute.sub(yz, vo.getPlansalvage());//预计残值
            DZFDouble month = SafeCompute.sub(new DZFDouble(vo.getUselimit() == null ? 0 : vo.getUselimit()),
                    new DZFDouble(vo.getDepreciationperiod() == null ? 0 : vo.getDepreciationperiod()));
            if (month.doubleValue() != 0) {
                yzj = SafeCompute.div(newyz, month);
            }
        } else if (vo.getZjtype() == 2) {// 双倍
            Integer month = vo.getUselimit();//预计使用年限
            if (month == null)
                return yzj;
            //账面净值
            DZFDouble yz = SafeCompute.sub(vo.getAssetmny(), vo.getDepreciation());
            if (month.doubleValue() > 24) {//两年前

//				//计算月折旧率
//				DZFDouble monthzj = SafeCompute.div(new DZFDouble(2),new DZFDouble(month));
//				//DZFDouble 保留8位有效数字
//				yzj = SafeCompute.multiply(yz, monthzj);

                //以下和前端js保持一致，保留10位
                BigDecimal monthzj = new BigDecimal(2).divide(new BigDecimal(month), 10, BigDecimal.ROUND_HALF_UP);
                BigDecimal yzjs = monthzj.multiply(yz.toBigDecimal());
                yzj = new DZFDouble(yzjs);
            } else {//两年内
                DZFDouble newyz = SafeCompute.sub(yz, vo.getPlansalvage());//预计残值
                yzj = SafeCompute.div(newyz, new DZFDouble(month));
            }
        }
        if (yzj != null) {
            yzj = yzj.setScale(2, DZFDouble.ROUND_HALF_UP);
        }
        return yzj;
    }

    @Override
    public void updateOrder(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        String condition = "pk_corp=? and nvl(dr,0)=0 order by assetcode ";
        AssetcardVO[] cards = (AssetcardVO[]) singleObjectBO.queryByCondition(
                AssetcardVO.class, condition, sp);
        if (cards != null && cards.length > 0) {
            for (int i = 1, length = cards.length; i <= length; i++) {
                String code = String.format("%06d", i);
                cards[i - 1].setAssetcode(code);
            }
            singleObjectBO.updateAry(cards, new String[]{"assetcode"});
        }
    }

    @Override
    public void checkCorp(String pk_corp, List<String> ids) throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter sp = SQLHelper.getSQLParameter(ids);
        sb.append(" select pk_corp from ynt_assetcard where pk_assetcard in ");
        sb.append(SQLHelper.getInSQL(ids));
        sb.append(" and pk_corp = ? ");
        sp.addParam(pk_corp);
        List<AssetcardVO> rs = (List<AssetcardVO>) singleObjectBO.executeQuery(sb.toString(), sp,
                new BeanListProcessor(AssetcardVO.class));
        if (ids.size() != rs.size()){
            throw new BusinessException("只能操作当前登录公司权限内的数据！");
        }
    }

    @Override
    public List<BdTradeAssetCheckVO> queryDefaultFromZclb(String pk_corp, String pk_zclb) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_zclb)) {
            throw new BusinessException("类别不能为空");
        }
        CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        BdAssetCategoryVO zcvo = (BdAssetCategoryVO) singleObjectBO.queryByPrimaryKey(BdAssetCategoryVO.class, pk_zclb);
        if (zcvo == null) {
            throw new BusinessException("资产不能为空");
        }
        String newRule = gl_cpacckmserv.queryAccountRule(pk_corp);
        YntCpaccountVO[] cpavos = accountService.queryByPk(pk_corp);
        Map<String, YntCpaccountVO> cpaCodeMap = new HashMap<String, YntCpaccountVO>();
        Map<String, YntCpaccountVO> cpaKeyMap = new HashMap<String, YntCpaccountVO>();
        //vo 转map,通过编码
        for (YntCpaccountVO vo : cpavos) {
            cpaCodeMap.put(vo.getAccountcode(), vo);
            cpaKeyMap.put(vo.getPk_corp_account(), vo);
        }
        StringBuffer qrysql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        List<BdTradeAssetCheckVO> listvos = null;
        //先查询公司级的
        qrysql.append("  select * from ynt_tdcheck  ");
        qrysql.append("  where nvl(dr,0)=0   ");
        qrysql.append(" and pk_corp =? ");
        sp.addParam(pk_corp);
        if (zcvo.getAssetproperty() != null && zcvo.getAssetproperty() == 1) {//无形资产类别有可能为空
            qrysql.append(" and ( pk_assetcategory = ? or (assetproperty = ? and  assetaccount=1)) ");
            sp.addParam(pk_zclb);
            sp.addParam(zcvo.getAssetproperty());
        } else {
            qrysql.append("  and pk_assetcategory = ? ");
            sp.addParam(pk_zclb);
        }

        listvos = (List<BdTradeAssetCheckVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(BdTradeAssetCheckVO.class));

        if (listvos == null || listvos.size() == 0) {
            qrysql = new StringBuffer();
            //集团级的
            sp.clearParams();
            qrysql.append(" select * from ynt_tdcheck ");
            qrysql.append(" where nvl(dr,0)=0  ");
            if (zcvo.getAssetproperty() != null && zcvo.getAssetproperty() == 1) {//无形资产类别有可能为空
                qrysql.append(" and ( pk_assetcategory = ? or (assetproperty = ? and  assetaccount=1)) ");
                sp.addParam(pk_zclb);
                sp.addParam(zcvo.getAssetproperty());
            } else {
                qrysql.append("  and pk_assetcategory = ? ");
                sp.addParam(pk_zclb);
            }
            qrysql.append(" and pk_trade_accountschema = ? ");
            qrysql.append(" and pk_corp = ? ");
            sp.addParam(cpvo.getCorptype());
            sp.addParam("000001");
            listvos = (List<BdTradeAssetCheckVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(BdTradeAssetCheckVO.class));
            //根据科目编码找对应的公司的科目（考虑科目升级的问题）
            sp.clearParams();
            sp.addParam(cpvo.getCorptype());
            BdTradeAccountVO[] tradevos = (BdTradeAccountVO[]) singleObjectBO.queryByCondition(BdTradeAccountVO.class, "nvl(dr,0)=0 and pk_trade_accountschema = ? ", sp);
            Map<String, BdTradeAccountVO> traDecodeMap = new HashMap<>();
            for (BdTradeAccountVO vo : tradevos) {
                traDecodeMap.put(vo.getPk_trade_account(), vo);
            }
            //行业级的
            for (BdTradeAssetCheckVO hyvo : listvos) {
                hyColumnHandle(newRule, cpaCodeMap, traDecodeMap, hyvo, "pk_glaccount", "zzkmmc", "zzkmcode");//总账科目
                hyColumnHandle(newRule, cpaCodeMap, traDecodeMap, hyvo, "pk_jskm", "jskmmc", "jskmcode");//结算科目
                hyColumnHandle(newRule, cpaCodeMap, traDecodeMap, hyvo, "pk_zjfykm", "zjfykmmc", "zjfykmcode");//折旧费用科目
            }

        } else {
            YntCpaccountVO tempvo = null;
            for (BdTradeAssetCheckVO gsvo : listvos) {
                if (!StringUtil.isEmpty(gsvo.getPk_glaccount())) {
                    tempvo = cpaKeyMap.get(gsvo.getPk_glaccount());
                    gsvo.setZzkmcode(tempvo.getAccountcode());
                    gsvo.setZzkmmc(gsvo.getZzkmcode() + " " + tempvo.getFullname());//总账科目
                }

                if (!StringUtil.isEmpty(gsvo.getPk_jskm())) {//结算科目
                    tempvo = cpaKeyMap.get(gsvo.getPk_jskm());
                    gsvo.setJskmcode(tempvo.getAccountcode());
                    gsvo.setJskmmc(gsvo.getJskmcode() + " " + tempvo.getFullname());
                }
                if (!StringUtil.isEmpty(gsvo.getPk_zjfykm())) {//折旧费用科目
                    tempvo = cpaKeyMap.get(gsvo.getPk_zjfykm());
                    gsvo.setZjfykmcode(tempvo.getAccountcode());
                    gsvo.setZjfykmmc(gsvo.getZjfykmcode() + " " + tempvo.getFullname());
                }
            }
        }
        return listvos;
    }

    private void hyColumnHandle(String newrule, Map<String, YntCpaccountVO> cpacodemap,
                                Map<String, BdTradeAccountVO> tradecodemap, BdTradeAssetCheckVO hyvo, String columnid, String columnname, String columncode) {
        YntCpaccountVO tempvo;
        BdTradeAccountVO hytempvo;
        String gscode;
        String id = (String) hyvo.getAttributeValue(columnid);
        if (!StringUtil.isEmpty(id)) {
            hytempvo = tradecodemap.get(id);
            if (hytempvo != null) {
                gscode = gl_accountcoderule.getNewRuleCode(hytempvo.getAccountcode(), DZFConstant.ACCOUNTCODERULE, newrule);
                tempvo = cpacodemap.get(gscode);
                if (tempvo != null) {
                    hyvo.setAttributeValue(columnid, tempvo.getPk_corp_account());//公司级的主键
                    hyvo.setAttributeValue(columncode, tempvo.getAccountcode());
                    hyvo.setAttributeValue(columnname, tempvo.getAccountcode() + " " + tempvo.getFullname());
                }
            }
        }
    }

    @Override
    public String saveVoucherFromZc(String[] assetids, String pk_corp, String coperatorid, DZFDate currDate, DZFBoolean bhb) throws DZFWarpException {
        CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        // 根据资产id批量转总账
        if (assetids == null || assetids.length == 0) {
            throw new BusinessException("数据不能为空");
        }

        String wherepart = SqlUtil.buildSqlForIn("pk_assetcard", assetids);
        StringBuffer qrysql = new StringBuffer();
        qrysql.append(" select t.* ,b.assetproperty ");
        qrysql.append(" from ynt_assetcard t  ");
        qrysql.append(" inner join ynt_category b on t.assetcategory = b.pk_assetcategory ");
        qrysql.append(" where nvl(t.dr,0)=0 and " + wherepart);
        qrysql.append(" order by period,assetcode ");

        List<AssetcardVO> cardvos = (List<AssetcardVO>) singleObjectBO.executeQuery(qrysql.toString(), new SQLParameter(), new BeanListProcessor(AssetcardVO.class));
        //根据类型区分
        Map<String, List<AssetcardVO>> groupmap = new HashMap<String, List<AssetcardVO>>();
        String period = "";
        String key = "";
        for (AssetcardVO vo : cardvos) {
            period = DateUtils.getPeriod(vo.getPeriod());
            if (bhb != null && bhb.booleanValue()) { //合并制证
                key = vo.getAssetproperty() + "&" + period;
            } else {//转总账
                key = vo.getAssetproperty() + "&" + period + "&" + vo.getPrimaryKey();
            }
            if (groupmap.containsKey(key)) {
                groupmap.get(key).add(vo);
            } else {
                List<AssetcardVO> list = new ArrayList<AssetcardVO>();
                list.add(vo);
                groupmap.put(key, list);
            }
        }

        //统计发票生成的资产卡片数量
        int totalCount = cardvos.stream().filter(v -> !StringUtil.isEmpty(v.getPk_image_group())).collect(Collectors.toList()).size();
        //发票生成的资产卡片 根据pk_image_group分组
        final Map<String, List<AssetcardVO>> fpCardList = totalCount > 0 ? getFpCardList(pk_corp) : null;

        List<String> tipslist = new ArrayList<>();//是否设置入账科目
        List<String> qctipslist = new ArrayList<>();//期初资产
        List<String> vouchertipslist = new ArrayList<>();//是否已经转总账
        //发票资产卡片验证
        Set<String> fpTipsList = new HashSet<>();

        //生成凭证
        for (Map.Entry<String, List<AssetcardVO>> entry : groupmap.entrySet()) {
            boolean flag = checkFpAssetCard(entry.getValue(), fpCardList, fpTipsList, vouchertipslist, (bhb != null && bhb.booleanValue()));

            //!(bhb != null && bhb.booleanValue()) &&
            if(flag){
                continue;
            }

            List<TzpzBVO> tzpzBVoList = new ArrayList<>();
            List<AssetcardVO> upvos = new ArrayList<>();
            Object[] objs = createVoucherB(tzpzBVoList, entry.getValue().toArray(new AssetcardVO[0]),
                    pk_corp, upvos, entry.getKey(), tipslist, qctipslist, vouchertipslist);

            List<String> imageGroupList = new ArrayList<>(entry.getValue().stream().filter(v -> !StringUtil.isEmpty(v.getPk_image_group())).map(AssetcardVO::getPk_image_group).collect(Collectors.toSet()));

            if (upvos.size() > 0) {
                DZFDouble Jfmny = (DZFDouble) objs[1];
                //预留pk_image_group 未实现 要考虑多个
                TzpzHVO headvo = saveVoucher(corpvo, coperatorid, DateUtils.getPeriodEndDate(entry.getKey().split("&")[1]), tzpzBVoList, Jfmny, upvos, imageGroupList);
                for (AssetcardVO vo : upvos) {
                    vo.setPk_voucher(headvo.getPrimaryKey());
                }
                singleObjectBO.updateAry(upvos.toArray(new AssetcardVO[0]), new String[]{"pk_voucher", "istogl"});

                List<String> pks = entry.getValue().stream().filter(v -> !StringUtil.isEmpty(v.getPk_invoice())).map(AssetcardVO::getPk_invoice).collect(Collectors.toList());
                if(pks != null && pks.size() > 0){
                    StringBuffer sb = new StringBuffer(" update ynt_vatincominvoice y set y.pk_tzpz_h = ?,y.pzh = ? where");
                    sb.append(SqlUtil.buildSqlForIn("vdef13",pks.toArray(new String[0])));
                    SQLParameter sp = new SQLParameter();
                    sp.addParam(headvo.getPrimaryKey());
                    sp.addParam(headvo.getPzh());
                    singleObjectBO.executeUpdate(sb.toString(), sp);
                }
            }
        }

        StringBuffer alltips = new StringBuffer();
        if (tipslist.size() > 0) {
            StringBuffer tips = getTipsStr(tipslist);
            if (tips.length() > 0) {
                alltips.append("编号为" + tips.substring(0, tips.length() - 1) + "资产生成凭证失败,原因未设置对应的入账科目<br>");
            }
        }

        if(fpTipsList.size() > 0){
            alltips.append(fpTipsList.stream().collect(Collectors.joining("<br/>"))+"<br/>");
        }

        if (qctipslist.size() > 0) {
            StringBuffer tips = getTipsStr(qctipslist);
            if (tips.length() > 0) {
                alltips.append("编号为" + tips.substring(0, tips.length() - 1) + "资产录入前已生成凭证，不需要转总账<br/>");
            }
        }
        if (vouchertipslist.size() > 0) {
            List<String> voucherlist = vouchertipslist.stream().distinct().collect(Collectors.toList());
            StringBuffer tips = getTipsStr(voucherlist);
            if (tips.length() > 0) {
                alltips.append("编号为" + tips.substring(0, tips.length() - 1) + "已转总账,不允许再次转总账<br/>");
            }
        }
        if (bhb != null && bhb.booleanValue() && alltips.length() > 0) {//合并的抛出异常
            throw new BusinessException(alltips.toString());
        } else {
            return alltips.toString();
        }
    }

    private boolean checkFpAssetCard(List<AssetcardVO> assetcardVOList, final Map<String,List<AssetcardVO>> fpCardMap, Set<String> fpTipsSet,List<String> vouchertipslist, boolean isHb) {

        AtomicBoolean result = new AtomicBoolean(false);

        //合并制证为空直接返回
        if(assetcardVOList == null || assetcardVOList.size() == 0 || fpCardMap == null || fpCardMap.size() == 0){
            return result.get();
        }
        Map<String, List<AssetcardVO>> assetcardMap = assetcardVOList.stream().filter(v -> !StringUtil.isEmpty(v.getPk_image_group())).collect(Collectors.groupingBy(AssetcardVO:: getPk_image_group));
        //合并制证中不存在发票生成的资产卡片直接返回
        if(assetcardMap == null || assetcardMap.size() == 0){
            return result.get();
        }

        List<String> assetCodes = assetcardVOList.stream().filter(v -> v.getIstogl() != null && v.getIstogl().booleanValue()).map(AssetcardVO::getAssetcode).collect(Collectors.toList());

        if(assetCodes != null && assetCodes.size() > 0){
            vouchertipslist.addAll(assetCodes);
            return true;
        }

        assetcardMap.forEach((k,v) ->{
            boolean flag = true;
            //一张发票生成多张卡片，卡片期间不一致转总账时，给出提示“发票号 XX 共
            //生成 XX 张卡片，资产编号 XX、YY 录入日期不一致，请检查”
            if(fpCardMap.containsKey(k) &&  fpCardMap.get(k).stream().map(AssetcardVO::getPeriod).collect(Collectors.toSet()).size() > 1){
                StringBuilder sb = new StringBuilder("发票号");
                String fphm = "";
                for(AssetcardVO assetcardVO : v){
                    if(!StringUtil.isEmpty(assetcardVO.getFp_hm())){
                        fphm = assetcardVO.getFp_hm();
                    }
                }
                sb.append(fphm);
                sb.append("共生成");
                sb.append(fpCardMap.get(k).size());
                sb.append("张卡片,请将资产编号");
                sb.append(fpCardMap.get(k).stream().map(AssetcardVO::getAssetcode).sorted().collect(Collectors.joining("、")));
                sb.append("入账日期不一致，请检查");
                fpTipsSet.add(sb.toString());
                result.set(true);
                flag = false;
            }
            //一张发票生成多张卡片：勾选其中一张卡片转总账时，给出提示“发票号 XX
            //共生成 XX 张卡片，请将资产编号 XX、YY 全部勾选后转总账”
            //要合并的卡片和数据库中卡片  发票号对应的资产卡片编号数量相同
            if(flag && fpCardMap.containsKey(k) && v.size() !=  fpCardMap.get(k).size()){
                StringBuilder sb = new StringBuilder("发票号");

                String fphm = "";
                for(AssetcardVO assetcardVO : v){
                    if(!StringUtil.isEmpty(assetcardVO.getFp_hm())){
                        fphm = assetcardVO.getFp_hm();
                    }
                }
                sb.append(fphm);
                sb.append("共生成");
                sb.append(fpCardMap.get(k).size());
                sb.append("张卡片,请将资产编号");
                sb.append(fpCardMap.get(k).stream().map(AssetcardVO::getAssetcode).sorted().collect(Collectors.joining("、")));
                if(isHb){
                    sb.append("全部勾选后生成凭证");
                }else{
                    sb.append("全部勾选后点合并制证按钮");
                }

                fpTipsSet.add(sb.toString());
                result.set(true);
            }
        });
        return result.get();
    }


    /**
     *  查询公司所有的发票生成的资产卡片
     *  [pk_image_group: {pk_assetcard..}]
     * @param pk_corp
     * @return
     */
    private Map<String,List<AssetcardVO>> getFpCardList(String pk_corp) {
        List<AssetcardVO> assetcardVOList = queryByPkCorp(pk_corp);
        return assetcardVOList.stream().filter(v -> !StringUtil.isEmpty(v.getPk_image_group())).collect(Collectors.groupingBy(AssetcardVO::getPk_image_group));
    }

    private StringBuffer getTipsStr(List<String> tipslist) {
        Collections.sort(tipslist);
        StringBuffer tips = new StringBuffer();
        for (String str : tipslist) {
            tips.append(str + ",");
        }
        return tips;
    }

    private Object[] createVoucherB(List<TzpzBVO> tzpzBVoList, AssetcardVO[] cardvos,
                                    String pk_corp, List<AssetcardVO> upvos,
                                    String zcsx, List<String> tipslist, List<String> qctipslist, List<String> vouchertipslist) {

        String zy = "";
        zcsx = zcsx.split("&")[0];
        if ("0".equals(zcsx) || "2".equals(zcsx)) {
            zy = "购买固定资产";
        } else if ("3".equals(zcsx)) {
            zy = "长期摊销";
        } else {
            zy = "购买无形资产";
        }

        Map<String, YntCpaccountVO> cpamap = accountService.queryMapByPk(pk_corp);
        DZFDouble total = DZFDouble.ZERO_DBL;

        Set<String> jxsflist = new HashSet<>();
        StringBuilder tips = new StringBuilder();
        Map<String, DZFDouble> jfzcmap = new LinkedHashMap<>();//借方资产科目
        Map<String, DZFDouble> dfjsmap = new LinkedHashMap<>();//结算科目
        String zckmid = null;//借资产科目+进项税额
        String jskmid = null;//贷结算科目
        DZFDouble tvalue = DZFDouble.ZERO_DBL;
        String sl = "";
        for (AssetcardVO vo : cardvos) {

            if (vo.getIstogl() != null && vo.getIstogl().booleanValue()) {//已转总账的不用转
                tips.append(vo.getAssetcode() + ",");//资产编码
                vouchertipslist.add(vo.getAssetcode());
                continue;
            }

            if (vo.getIsperiodbegin() != null && vo.getIsperiodbegin().booleanValue()) {
                qctipslist.add(vo.getAssetcode());
                continue;
            }

            sl = vo.getNsl() == null ? "0" : vo.getNsl().toString();
            zckmid = vo.getPk_zckm() + "_" + sl;
            validateKm(cpamap, vo.getPk_zckm(), "资产编号(" + vo.getAssetcode() + ")资产科目");
            jskmid = getAssetJskmId(vo);//
            validateKm(cpamap, vo.getPk_jskm(), "资产编号(" + vo.getAssetcode() + ")结算科目");
            String pk_jxsf = getJxsfid(pk_corp, sl);//获取进项税费id
            jxsflist.add(pk_jxsf);



            if (StringUtil.isEmpty(zckmid) || StringUtil.isEmpty(jskmid)) {
                tips.append(vo.getAssetcode() + ",");//资产编码
                tipslist.add(vo.getAssetcode());
                continue;
            }

            if (StringUtil.isEmpty(pk_jxsf) && vo.getNjxsf() != null && vo.getNjxsf().doubleValue() != 0) {
                tips.append(vo.getAssetcode() + ",");//资产编码
                tipslist.add(vo.getAssetcode());
                continue;
            }

            //借方
            if (jfzcmap.containsKey(zckmid)) {
                tvalue = jfzcmap.get(zckmid);
                tvalue = SafeCompute.add(tvalue, vo.getAssetmny());
            } else {
                tvalue = VoUtils.getDZFDouble(vo.getAssetmny());
            }
            jfzcmap.put(zckmid, tvalue);

            if (!StringUtil.isEmpty(pk_jxsf) && vo.getNjxsf() != null && vo.getNjxsf().doubleValue() != 0) {//进项税费
                tvalue = jfzcmap.get(pk_jxsf) == null ? DZFDouble.ZERO_DBL : jfzcmap.get(pk_jxsf);
                jfzcmap.put(pk_jxsf, SafeCompute.add(tvalue, vo.getNjxsf()));
            }
            //贷方
            if (dfjsmap.containsKey(jskmid)) {
                tvalue = dfjsmap.get(jskmid);
                tvalue = SafeCompute.add(tvalue, vo.getAssetmny()).add(VoUtils.getDZFDouble(vo.getNjxsf()));
            } else {
                tvalue = SafeCompute.add(vo.getAssetmny(), vo.getNjxsf());
            }
            dfjsmap.put(jskmid, tvalue);

            //转总账的标识
            vo.setIstogl(DZFBoolean.TRUE);
            upvos.add(vo);
        }
        for (Map.Entry<String, DZFDouble> entry : jfzcmap.entrySet()) {// 需要设置税目信息
            String key = entry.getKey();
            DZFDouble value = entry.getValue();
            TzpzBVO debitVO = new TzpzBVO();
            if (!jxsflist.contains(key)) {// 税目信息
                debitVO.setPk_accsubj(key.split("_")[0]);
                debitVO.setNrate(new DZFDouble(key.split("_")[1]));
                key = key.split("_")[0];
            } else {
                debitVO.setPk_accsubj(key);
                debitVO.setNrate(new DZFDouble(1));
            }
            debitVO.setZy(zy);
            debitVO.setVcode(cpamap.get(key).getAccountcode());
            debitVO.setVname(cpamap.get(key).getAccountname());
            debitVO.setPk_currency("00000100AA10000000000BKT");
            debitVO.setJfmny(value);
            debitVO.setYbjfmny(value);
            debitVO.setPk_corp(pk_corp);
            debitVO.setDr(0);
            debitVO.setRowno(tzpzBVoList.size() + 1);
            debitVO.setVdirect(0);
            tzpzBVoList.add(debitVO);
            total = SafeCompute.add(total, value);
        }
        for (Map.Entry<String, DZFDouble> entry : dfjsmap.entrySet()) {
            String key = entry.getKey();
            String[] keys = key.split("@");
            String kmid = keys[0];
            DZFDouble value = entry.getValue();
            TzpzBVO debitVO = new TzpzBVO();
            debitVO.setPk_accsubj(kmid);
            //辅助项目赋值
            if (keys.length > 1) {
                String fzkey = "";
                String fzvalue = "";
                for (int i = 1; i < keys.length; i++) {
                    fzkey = keys[i].split(":")[0];
                    fzvalue = keys[i].split(":")[1];
                    debitVO.setAttributeValue("fzhsx" + fzkey, fzvalue);//辅助核算赋值
                }
            }
            debitVO.setZy(zy);
            debitVO.setVcode(cpamap.get(kmid).getAccountcode());
            debitVO.setVname(cpamap.get(kmid).getAccountname());
            debitVO.setPk_currency("00000100AA10000000000BKT");
            debitVO.setNrate(new DZFDouble(1));
            debitVO.setDfmny(value);
            debitVO.setYbdfmny(value);
            debitVO.setPk_corp(pk_corp);
            debitVO.setDr(0);
            debitVO.setRowno(tzpzBVoList.size() + 1);
            debitVO.setVdirect(1);
            tzpzBVoList.add(debitVO);
        }
        return new Object[]{tips.toString(), total};
    }

    private String getAssetJskmId(AssetcardVO vo) {
        StringBuilder idstr = new StringBuilder();
        idstr.append(vo.getPk_jskm());
        String key = "";
        String value = "";
        for (int i = 1; i < 11; i++) {
            key = i + "";
            value = (String) vo.getAttributeValue("jsfzhsx" + key);
            if (!StringUtil.isEmpty(value)) {
                idstr.append("@" + key + ":" + value);
            }
        }
        return idstr.toString();
    }

    private String getJxsfid(String pk_corp, String sl) {
        String pk_jxsf = "";
        CorpVO cpvo = corpService.queryByPk(pk_corp);
        YntCpaccountVO[] cpaccountvos = accountService.queryByPk(pk_corp);
        String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
        String newcode = Kmschema.getKmCode(Kmschema.style_jxse, cpvo.getCorptype(), newrule);
        DZFDouble sltemp = new DZFDouble(sl);
        sl = sltemp.multiply(100).setScale(2, DZFDouble.ROUND_HALF_UP).toString();
        if (!StringUtil.isEmpty(newcode)) {
            for (YntCpaccountVO vo : cpaccountvos) {
                if (newcode.equals(vo.getAccountcode()) && vo.getIsleaf() != null && vo.getIsleaf().booleanValue()) {//末级科目
                    pk_jxsf = vo.getPk_corp_account();
                    break;
                } else if (vo.getAccountcode().startsWith(newcode) && vo.getIsleaf() != null && vo.getIsleaf().booleanValue()) {
                    //如果没默认取第一个
                    if (StringUtil.isEmpty(pk_jxsf)) {
                        pk_jxsf = vo.getPk_corp_account();
                    }
                    try {
                        String all = getMathcMny(vo.getAccountname());
                        all = new DZFDouble(all).setScale(2, DZFDouble.ROUND_HALF_UP).toString();
                        if (all.equals(sl)) {
                            pk_jxsf = vo.getPk_corp_account();
                            break;
                        }
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }
        return pk_jxsf;
    }

    private String getMathcMny(String name) {
        // 需要取整数和小数的字符串
        // 控制正则表达式的匹配行为的参数(小数)
        Pattern p = Pattern.compile("(\\d+\\.\\d+)");
        //Matcher类的构造方法也是私有的,不能随意创建,只能通过Pattern.matcher(CharSequence input)方法得到该类的实例. 
        Matcher m = p.matcher(name);
        //m.find用来判断该字符串中是否含有与"(\\d+\\.\\d+)"相匹配的子串
        if (m.find()) {
            //如果有相匹配的,则判断是否为null操作
            //group()中的参数：0表示匹配整个正则，1表示匹配第一个括号的正则,2表示匹配第二个正则,在这只有一个括号,即1和0是一样的
            name = m.group(1) == null ? "" : m.group(1);
        } else {
            //如果匹配不到小数，就进行整数匹配
            p = Pattern.compile("(\\d+)");
            m = p.matcher(name);
            if (m.find()) {
                //如果有整数相匹配
                name = m.group(1) == null ? "" : m.group(1);
            } else {
                //如果没有小数和整数相匹配,即字符串中没有整数和小数，就设为空
                name = "";
            }
        }
        return name;
    }

    private TzpzHVO saveVoucher(CorpVO corpvo, String coperatorid,
                                DZFDate currDate, List<TzpzBVO> tzpzBVoList, DZFDouble Jfmny, List<AssetcardVO> upvos, List<String> imageGroupList)
            throws DZFWarpException {
        TzpzHVO headVO = new TzpzHVO();
        headVO.setPk_corp(corpvo.getPk_corp());
        headVO.setPzlb(0);
        headVO.setIshasjz(DZFBoolean.FALSE);
        headVO.setCoperatorid(coperatorid);
        headVO.setDoperatedate(currDate);
        if (imageGroupList != null && imageGroupList.size() > 0) {
            headVO.setNbills(new HashSet<>(imageGroupList).size());
            String groupId = img_groupserv.processMergeGroup(corpvo.getPk_corp(), null, imageGroupList);
            headVO.setPk_image_group(groupId);
        }
        headVO.setPeriod(DateUtils.getPeriod(currDate));
        headVO.setVyear(currDate.getYear());
        headVO.setPzh(yntBoPubUtil.getNewVoucherNo(corpvo.getPk_corp(),
                headVO.getDoperatedate()));
        headVO.setSourcebilltype(IBillTypeCode.HP59);
        headVO.setVbillstatus(8);
        headVO.setJfmny(Jfmny);
        headVO.setDfmny(Jfmny);

        TzpzBVO[] children = tzpzBVoList
                .toArray(new TzpzBVO[tzpzBVoList.size()]);
        headVO.setChildren(children);

		//拼接关联关系  仅限发票生成的卡片
		List<PzSourceRelationVO> sourcelist = new ArrayList<PzSourceRelationVO>();
		for(AssetcardVO cardvo:upvos){
		    if(!StringUtil.isEmpty(cardvo.getPk_invoice())){
                PzSourceRelationVO relVO=new PzSourceRelationVO();
                relVO.setSourcebillid(cardvo.getPk_invoice());
                relVO.setSourcebilltype(IBillTypeCode.HP95);
                relVO.setPk_corp(corpvo.getPk_corp());
                sourcelist.add(relVO);
            }
		}

		if(sourcelist.size() > 0){
            headVO.setSource_relation(sourcelist.toArray(new PzSourceRelationVO[0]));//拼接来源
        }

        IVoucherService gl_tzpzserv = (IVoucherService) SpringUtils.getBean("gl_tzpzserv");
        headVO = gl_tzpzserv.saveVoucher(corpvo, headVO);
        return headVO;
    }

    @Override
    public List<AssetcardDisplayColumnVO> qryDisplayColumns(String pk_corp) throws DZFWarpException {

        StringBuilder qrysql = new StringBuilder();
        qrysql.append(" select *   ");
        qrysql.append(" from " + AssetcardDisplayColumnVO.TABLE_NAME);
        qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);

        List<AssetcardDisplayColumnVO> lists = (List<AssetcardDisplayColumnVO>) singleObjectBO
                .executeQuery(qrysql.toString(), sp, new BeanListProcessor(AssetcardDisplayColumnVO.class));

        return lists;
    }

    @Override
    public void saveDisplayColumn(AssetcardDisplayColumnVO displayvo, String pk_corp) throws DZFWarpException {

        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空");
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        AssetcardDisplayColumnVO[] vos = (AssetcardDisplayColumnVO[]) singleObjectBO.queryByCondition(AssetcardDisplayColumnVO.class, "nvl(dr,0)=0 and pk_corp = ? ", sp);

        if (vos != null && vos.length > 0) {
            vos[0].setSetting(displayvo.getSetting());
            singleObjectBO.update(vos[0]);
        } else {
            displayvo.setPk_corp(pk_corp);

            singleObjectBO.saveObject(pk_corp, displayvo);
        }

    }

    @Override
    public List<TaxitemVO> qrytaxitems(String pk_corp) throws DZFWarpException {

        CorpVO cpvo = corpService.queryByPk(pk_corp);
        if ("小规模纳税人".equals(cpvo.getChargedeptname())) {
            return new ArrayList<>();
        }

        String qrysql = " select taxratio,min(shortname) as shortname from ynt_taxitem where nvl(dr,0)=0 and taxstyle = 2  group by taxratio order by taxratio ";

        List<TaxitemVO> itemvos = (List<TaxitemVO>) singleObjectBO.executeQuery(qrysql, new SQLParameter(),
                new BeanListProcessor(TaxitemVO.class));

        return itemvos;
    }

    @Override
    public String getMinAssetPeriod(String pk_corp) throws DZFWarpException {

        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空");
        }

        CorpVO cpvo = corpService.queryByPk(pk_corp);

        StringBuffer qrysql = new StringBuffer();
        qrysql.append(" select a.*,b.assetproperty from ynt_assetcard a ");
        qrysql.append(" left join ynt_category  b on a.assetcategory = b.pk_assetcategory ");
        qrysql.append(" where nvl(a.dr,0)=0 and a.pk_corp = ?  ");
        qrysql.append(" order by a.accountdate ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<AssetcardVO> list = (List<AssetcardVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
                new BeanListProcessor(AssetcardVO.class));

        if (list == null || list.size() == 0) {// 如果没资产，最小的日期就是空
            return "";
        }

        AssetcardVO firstvo = list.get(0);

        //如果是固定资产非期初资产则需要往后调整一个月
        if (firstvo.getIsperiodbegin() == null || !firstvo.getIsperiodbegin().booleanValue()) {
            if ("0".equals(firstvo.getAssetproperty()) || "2".equals(firstvo.getAssetproperty())) {
                long prelong = DateUtils.getNextMonth(list.get(0).getAccountdate().getMillis());

                return DateUtils.getPeriod(new DZFDate(prelong));
            }
        } else {
            return DateUtils.getPeriod(cpvo.getBusibegindate());//如果是期初资产，则取建账日期
        }

        return DateUtils.getPeriod(list.get(0).getAccountdate());

    }

    @Override
    public void updateAdjustLimit(String id, Integer newlimit) throws DZFWarpException {

        // 计算折旧信息
        if (StringUtil.isEmpty(id)) {
            throw new BusinessException("资产不存在!");
        }
        if (newlimit == null || newlimit <= 0) {
            throw new BusinessException("使用年限不能小于等于0");
        }


        AssetcardVO cardvo = (AssetcardVO) singleObjectBO.queryByPrimaryKey(AssetcardVO.class, id);

        if (cardvo == null) {
            throw new BusinessException("该资产不存在!");
        }

        if(cardvo.getZjtype() == 3){
            throw new BusinessException("年数总和法不支持年限调整!");
        }

        if (cardvo.getIsclear() != null && cardvo.getIsclear().booleanValue()) {
            throw new BusinessException("资产已清理，不能调整");
        }

        if (cardvo.getZjtype() != null && cardvo.getZjtype() == 1) {//1工作量算法
            throw new BusinessException("工作量算法不支持年限调整!");
        }

        if (cardvo.getDepreciationperiod() != null &&
                newlimit <= cardvo.getDepreciationperiod()) {
            throw new BusinessException("调整年限应大于总折旧年限");
        }

        if (cardvo.getUselimit() != null && cardvo.getUselimit().intValue() == newlimit.intValue()) {
            throw new BusinessException("年限未变化");
        }

        cardvo.setUselimit(newlimit);

        DZFDouble yzj = calcYzj(cardvo);

        //月折旧，和使用年限
        cardvo.setMonthzj(yzj);

        if (yzj.doubleValue() <= 0) {
            throw new BusinessException("月折旧不能小于等于0");
        }

        singleObjectBO.update(cardvo, new String[]{"uselimit", "monthzj"});
    }

    public String brepeat_sig(AssetcardVO assetcardvo, String pk_corp) throws DZFWarpException {
        if (!StringUtil.isEmpty(assetcardvo.getPrimaryKey())) {//修改的不做校验
            return null;
        }
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空");
        }
        StringBuilder tips = new StringBuilder();
        SQLParameter sp = new SQLParameter();
        StringBuilder qrysql = new StringBuilder();
        qrysql.append("select zccode,assetcode,pk_assetcard from  ynt_assetcard");
        qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
        sp.addParam(pk_corp);
        if (StringUtil.isEmpty(assetcardvo.getZccode())) {
            qrysql.append(" and zccode is null ");
        } else {
            qrysql.append(" and zccode = ? ");
            sp.addParam(assetcardvo.getZccode());
        }
        if (StringUtil.isEmpty(assetcardvo.getAssetname())) {
            qrysql.append(" and assetname  is null ");
        } else {
            qrysql.append(" and assetname = ? ");
            sp.addParam(assetcardvo.getAssetname());
        }
        qrysql.append(" and nvl(assetmny,0) = ? ");
        sp.addParam(assetcardvo.getAssetmny() == null ? DZFDouble.ZERO_DBL : assetcardvo.getAssetmny());
        if (!StringUtil.isEmpty(assetcardvo.getPrimaryKey())) {
            qrysql.append(" and  pk_assetcard != ? ");
            sp.addParam(assetcardvo.getPrimaryKey());
        }
        List<AssetcardVO> assetvos = (List<AssetcardVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
                new BeanListProcessor(AssetcardVO.class));
        if (assetvos != null && assetvos.size() > 0) {
            tips.append("资产名称(" + assetcardvo.getAssetname() + ")与现有的卡片编码(" + assetvos.get(0).getAssetcode() + ")重复。");
        }
        return tips.toString();
    }

    @Override
    public String brepeat_mul(List<AssetcardVO> kpvos, String pk_corp) throws DZFWarpException {
        if (kpvos == null || kpvos.size() == 0) {
            return "";
        }
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空");
        }
        SQLParameter sp = new SQLParameter();
        StringBuffer qrysql = new StringBuffer();
        qrysql.append("select zccode,assetcode, zccode || assetname as vdef10 ,pk_assetcard from  ynt_assetcard");
        qrysql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
        sp.addParam(pk_corp);
        List<AssetcardVO> assetvos = (List<AssetcardVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
                new BeanListProcessor(AssetcardVO.class));

        StringBuffer tips = new StringBuffer();
        if (assetvos != null && assetvos.size() > 0) {
            String zccode = "";//资产编码
            String zccode_com = "";//待比较的资产编码
            String assetname = "";//资产名称
            String assetname_com = "";//待比较的资产名称
            DZFDouble assetmny = DZFDouble.ZERO_DBL;
            DZFDouble assetmny_com = DZFDouble.ZERO_DBL;//待比较的金额
            for (AssetcardVO assetvo : kpvos) {
                zccode = assetvo.getZccode() == null ? "" : assetvo.getZccode();
                assetname = assetvo.getAssetname() == null ? "" : assetvo.getAssetname();
                assetmny = assetvo.getAssetmny() == null ? DZFDouble.ZERO_DBL : assetvo.getAssetmny();
                for (AssetcardVO vo : assetvos) {
                    zccode_com = vo.getZccode() == null ? "" : vo.getZccode();
                    assetname_com = vo.getAssetname() == null ? "" : vo.getAssetname();
                    assetmny_com = vo.getAssetmny() == null ? DZFDouble.ZERO_DBL : vo.getAssetmny();
                    if (!vo.getPrimaryKey().equals(assetvo.getPrimaryKey())
                            && zccode.equals(zccode_com) && assetname.equals(assetname_com)
                            && assetmny.doubleValue() == assetmny_com.doubleValue()) {
                        tips.append("资产名称(" + assetname + ")与现有的卡片编码(" + vo.getAssetcode() + ")重复<br>");
                    }
                }
            }
        }
        return tips.toString();
    }

    @Override
    public AssetcardVO saveCard(String pk_corp, AssetcardVO vo) throws DZFWarpException {
        //校验来源
        CorpVO cpvo = validateSource(pk_corp, vo);
        // 赋值默认值
        setDefaultValue(vo, pk_corp);

        //保存校验1
        checkBeforeSave(cpvo, vo);

        //保存校验2
        checkBeforeDelAndUpd(vo);

        AssetcardVO revo = (AssetcardVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);

        return revo;

    }

    private CorpVO validateSource(String pk_corp, AssetcardVO vo) {
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空");
        }
        CorpVO cpvo = corpService.queryByPk(pk_corp);
        if (cpvo == null) {
            throw new BusinessException("公司不存在");
        }
        // 来源字段不能为空
        if (StringUtil.isEmpty(vo.getSourcetype())) {
            throw new BusinessException("来源类型不能为空");
        }
        return cpvo;
    }

    /**
     * 赋默认值
     *
     * @param vo
     */
    private void setDefaultValue(AssetcardVO vo, String pk_corp) {
        if (StringUtil.isEmpty(vo.getPk_corp())) {
            vo.setPk_corp(pk_corp);
        }
        if (StringUtil.isEmpty(vo.getPk_assetcard())) {//资产卡片
            vo.setAssetcode(buildAssetcardCode(vo.getPk_corp()));
        } else {
            throw new BusinessException("卡片ID应该为空");
        }
        vo.setAccountmny(vo.getAssetmny());
        vo.setAssetnetvalue(SafeCompute.sub(
                vo.getAssetmny(), vo.getDepreciation()).setScale(2, DZFDouble.ROUND_HALF_UP));//资产净值
        if (vo.getIsperiodbegin() == null || !vo.getIsperiodbegin().booleanValue()) {
            vo.setAccountusedperiod(1);
            vo.setUsedperiod(1);
        }
        //赋值科目
        if (StringUtil.isEmpty(vo.getAssetcategory())) {
            throw new BusinessException("类别不能为空");
        }
        List<BdTradeAssetCheckVO> vos = queryDefaultFromZclb(pk_corp, vo.getAssetcategory());
        if (vos == null || vos.size() == 0) {
            throw new BusinessException("类别默认对照为空");
        }
        String pk_jtzjkm = "";//计提折旧科目
        String pk_zjfykm = "";//折旧费用科目
        for (BdTradeAssetCheckVO tempvo : vos) {
            //固定资产，无形资产
            if (tempvo.getAssetproperty() == 0 || tempvo.getAssetproperty() == 1 || tempvo.getAssetproperty() == 2) {
                if (tempvo.getAssetaccount() == 1) {
                    pk_jtzjkm = tempvo.getPk_glaccount();
                    pk_zjfykm = tempvo.getPk_zjfykm();
                    break;
                }
            } else {
                pk_jtzjkm = tempvo.getPk_glaccount();
                pk_zjfykm = tempvo.getPk_zjfykm();
                break;
            }
        }

        vo.setPk_jtzjkm(pk_jtzjkm);
        vo.setPk_zjfykm(pk_zjfykm);

        //计算残值率
        if (vo.getSalvageratio() != null
                && vo.getAssetmny() != null) {
            vo.setPlansalvage(vo.getAssetmny().multiply(vo.getSalvageratio()).setScale(2, DZFDouble.ROUND_HALF_UP));// 预计残值
        }
        //计算月折旧
        vo.setMonthzj(calcYzj(vo).setScale(2, DZFDouble.ROUND_HALF_UP));

        if (!StringUtil.isEmpty(vo.getPk_voucher())) {
            vo.setIstogl(DZFBoolean.TRUE);//已转总账
        }
    }


}

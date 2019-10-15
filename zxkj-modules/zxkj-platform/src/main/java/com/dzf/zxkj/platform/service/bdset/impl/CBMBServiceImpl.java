package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.StringUtil;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.CpcosttransVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.BdTradeCostTransferVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.bdset.ICBMBService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("gl_cpcbmbserv")
public class CBMBServiceImpl implements ICBMBService {
    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private ICpaccountService gl_cpacckmserv;

    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private IAccountService accountService;

    @Autowired
    private ICorpService corpService;

    @Override
    public CpcosttransVO save(CpcosttransVO vo) throws DZFWarpException {
        CpcosttransVO tvo = new CpcosttransVO();
        BeanUtils.copyProperties(vo, tvo);
        checkBeforeSave(tvo, true);
        CpcosttransVO svo = (CpcosttransVO) singleObjectBO.saveObject(vo.getPk_corp(), tvo);
        return svo;
    }

    @Override
    public List<CpcosttransVO> query(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        StringBuffer sf = new StringBuffer();
        sf.append(" select t1.*,t2.accountname pk_debitaccount_name,t3.accountname pk_creditaccount_name,t4.accountname pk_fillaccount_name from ynt_cpcosttrans t1 ");
        sf.append(" left join ynt_cpaccount t2 on t1.pk_debitaccount = t2.pk_corp_account and t1.pk_corp = t2.pk_corp ");
        sf.append(" left join ynt_cpaccount t3 on t1.pk_creditaccount = t3.pk_corp_account and t1.pk_corp = t3.pk_corp ");
        sf.append(" left join ynt_cpaccount t4 on t1.pk_fillaccount = t4.pk_corp_account and t1.pk_corp = t4.pk_corp ");
        sf.append(" where t1.pk_corp = ? and nvl(t1.dr,0) = 0 ");
        List<CpcosttransVO> clist = (List<CpcosttransVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(CpcosttransVO.class));

        //   取集团的行业模板
        List<CpcosttransVO> hylist = translateCpcosttransVO(pk_corp);

        if(clist != null && clist.size() >0){
            hylist.addAll(clist);
        }

        return hylist;
    }

    /**
     * 将行业成本结转翻译成公司级模板
     */
    public List<CpcosttransVO> translateCpcosttransVO(String pk_corp) throws BusinessException {
        List<CpcosttransVO> list = new ArrayList<>();
        try {
            String where = " pk_trade_accountschema in (select corptype from bd_corp where pk_corp='" + pk_corp + "' and nvl(dr,0)=0 )  and nvl(dr,0)=0 ";
            where = where + " and pk_corp ='" + IDefaultValue.DefaultGroup + "'";
            BdTradeCostTransferVO[] hymbVOs = (BdTradeCostTransferVO[]) singleObjectBO.queryByCondition(BdTradeCostTransferVO.class, where, new SQLParameter());
            if (hymbVOs == null || hymbVOs.length < 1) {
                return list;
            }
            BdTradeAccountVO[] bdTradeAccountVOS = (BdTradeAccountVO[])singleObjectBO.queryByCondition(BdTradeAccountVO.class,"nvl(dr,0)=0", new SQLParameter());
            Map<String, BdTradeAccountVO> bdTradeAccountVOMap = Arrays.asList(bdTradeAccountVOS).stream().collect(Collectors.toMap(BdTradeAccountVO::getPk_trade_account, bdTradeAccountVO -> bdTradeAccountVO,(k1, k2)->k1));
            YntCpaccountVO[] yntCpaccountVOS = accountService.queryByPk(pk_corp);
            Map<String, YntCpaccountVO> yntCpaccountVOMap = Arrays.asList(yntCpaccountVOS).stream().collect(Collectors.toMap(YntCpaccountVO::getAccountcode, yntCpaccountVO -> yntCpaccountVO,(k1, k2)->k1));

            String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
            String olerule = DZFConstant.ACCOUNTCODERULE;

            for (int i = 0; i < hymbVOs.length; i++) {
                BdTradeCostTransferVO hymbVO = hymbVOs[i];
                //借方科目
                String jfkm = hymbVO.getPk_debitaccount();
                //根据行业会计科目主键找到公司会计科目主键
                jfkm = getCorpAccountByTradeAccountPk(jfkm, pk_corp, newrule, olerule, bdTradeAccountVOMap);
                //贷方科目
                String dfkm = hymbVO.getPk_creditaccount();
                dfkm = getCorpAccountByTradeAccountPk(dfkm, pk_corp, newrule, olerule, bdTradeAccountVOMap);

                //取数科目
                String qskm = hymbVO.getPk_fillaccount();
                qskm = getCorpAccountByTradeAccountPk(qskm, pk_corp, newrule, olerule,bdTradeAccountVOMap);

                CpcosttransVO vo = new CpcosttransVO();

                vo.setAbstracts(hymbVO.getAbstracts());
                vo.setTransratio(hymbVO.getTransratio());
                YntCpaccountVO accvo = yntCpaccountVOMap.get(jfkm);
                if (accvo != null) {
                    vo.setPk_debitaccount_name(accvo.getAccountname());
                    vo.setPk_debitaccount(accvo.getPrimaryKey());
                }
                accvo = yntCpaccountVOMap.get(dfkm);
                if (accvo != null) {
                    vo.setPk_creditaccount_name(accvo.getAccountname());
                    vo.setPk_creditaccount(accvo.getPrimaryKey());
                }
                accvo = yntCpaccountVOMap.get(qskm);
                if (accvo != null) {
                    vo.setPk_fillaccount_name(accvo.getAccountname());
                    vo.setPk_fillaccount(accvo.getPrimaryKey());
                }
                vo.setPk_corp(hymbVO.getPk_corp());
                list.add(vo);
            }
        } catch (Exception e) {
            throw new BusinessException(e.getMessage());
        }
        return list;
    }

    private String getCorpAccountByTradeAccountPk(String pk_trade_account, String pk_corp, String newrule, String olerule, Map<String, BdTradeAccountVO> bdTradeAccountVOMap) throws DZFWarpException {
        BdTradeAccountVO jfkmVO = bdTradeAccountVOMap.get(pk_trade_account);
        if(jfkmVO == null){
            throw new BusinessException("行业会计科目主键为" + pk_trade_account
                    + "的科目已被删除，请检查");
        }
        return gl_accountcoderule.getNewRuleCode(jfkmVO.getAccountcode(), olerule, newrule);
    }

    @Override
    public CpcosttransVO queryById(String id) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(id);
        StringBuffer sf = new StringBuffer();
        sf.append(" select t1.*,t2.accountname pk_debitaccount_name,t3.accountname pk_creditaccount_name,t4.accountname pk_fillaccount_name from ynt_cpcosttrans t1 ");
        sf.append(" left join ynt_cpaccount t2 on t1.pk_debitaccount = t2.pk_corp_account and t1.pk_corp = t2.pk_corp ");
        sf.append(" left join ynt_cpaccount t3 on t1.pk_creditaccount = t3.pk_corp_account and t1.pk_corp = t3.pk_corp ");
        sf.append(" left join ynt_cpaccount t4 on t1.pk_fillaccount = t4.pk_corp_account and t1.pk_corp = t4.pk_corp ");
        sf.append(" where t1.pk_corp_costtransfer = ? and nvl(t1.dr,0) = 0 ");
        List<CpcosttransVO> zc = (List<CpcosttransVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(CpcosttransVO.class));
        if (zc != null && zc.size() > 0) {
            return zc.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void update(CpcosttransVO vo) throws DZFWarpException {
        CpcosttransVO tvo = new CpcosttransVO();
        BeanUtils.copyProperties(vo, tvo);
        checkBeforeSave(tvo, false);
        singleObjectBO.saveObject(tvo.getPk_corp(), tvo);
    }

    @Override
    public void delete(CpcosttransVO vo) throws DZFWarpException {
        CpcosttransVO tvo = new CpcosttransVO();
        BeanUtils.copyProperties(vo, tvo);
        singleObjectBO.deleteObject(tvo);
    }

    /**
     * 保存、更新前校验
     *
     * @param vo
     * @throws BusinessException
     */
    public void checkBeforeSave(CpcosttransVO vo, boolean isadd) throws DZFWarpException {
        String debitAccount = vo.getPk_debitaccount();
        String creditAccount = vo.getPk_creditaccount();
        String pk_fillaccount = vo.getPk_fillaccount();
        DZFDouble transratio = vo.getTransratio();
        String acts = vo.getAbstracts();

        if(debitAccount == null){
            throw new BusinessException("借方科目不能为空。");
        }

        if (vo.getJztype().intValue() == 1) {//非完工产品结转 借方科目不允许非末级
            YntCpaccountVO kmvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, debitAccount);
            if (!kmvo.getIsleaf().booleanValue()) {
                throw new BusinessException("材料成本结转借方科目不允许设置非末级。");
            }
        }
        if (vo.getJztype().intValue() == 3) {//非完工产品结转 借方科目不允许非末级
            YntCpaccountVO kmvo = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, debitAccount);
            if (!kmvo.getIsleaf().booleanValue()) {
                throw new BusinessException("销售成本结转借方科目不允许设置非末级。");
            }
        }

        if(creditAccount == null){
            throw new BusinessException("贷方科目不能为空。");
        }

        //销售成本结转模板 校验
        CorpVO corp = corpService.queryByPk(vo.getPk_corp());
        if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
            // 启用库存新模式
            if (corp.getIbuildicstyle() != null && corp.getIbuildicstyle() == 1) {
                if (vo.getJztype() != null && vo.getJztype().intValue() == 3) {
                    String sql = "select pk_corp_costtransfer from ynt_cpcosttrans where nvl(dr,0)=0 and pk_corp=? and jztype = ? ";

                    if (!StringUtil.isEmpty(vo.getPk_corp_costtransfer())) {
                        sql = sql + " and pk_corp_costtransfer <> '" + vo.getPk_corp_costtransfer() + "'";
                    }
                    SQLParameter params = new SQLParameter();
                    params.addParam(vo.getPk_corp());
                    params.addParam(vo.getJztype());
                    boolean isExists = singleObjectBO.isExists(vo.getPk_corp(), sql, params);

                    if (isExists)
                        throw new BusinessException("已经存在其他的销售成本结转模板,不能再新增!");
                }
            }
        }

        if (vo.getJztype().intValue() == 3) {//
            if (StringUtil.isEmpty(pk_fillaccount))
                throw new BusinessException("取数科目不能为空。");
        }

//		if(corp != null && corp.getBbuildic() != null && corp.getBbuildic().booleanValue()){
//			if(corp.getIbuildicstyle() !=null && corp.getIbuildicstyle()==1){//新库存
//				if(vo.getJztype().intValue() !=3 ){
//					if(StringUtil.isEmpty(pk_fillaccount) )
//						throw new BusinessException("取数科目不能为空。");
//				}
//			}else{
//				if(StringUtil.isEmpty(pk_fillaccount) )
//					throw new BusinessException("取数科目不能为空。");
//			}
//			
//		}else{
//			if(vo.getJztype().intValue()==3){
//				if(StringUtil.isEmpty(pk_fillaccount))
//					throw new BusinessException("取数科目不能为空。");
//			}
//		}

        if (corp != null && corp.getIcostforwardstyle() == 1 && transratio == null) {//比例结转
            throw new BusinessException("结转比例不能为空。");
        }

        if (acts == null)
            throw new BusinessException("摘要不能为空。");

        if (debitAccount.equals(creditAccount))
            throw new BusinessException("借方科目不能等于贷方科目。");

        if (isadd) {
            List<CpcosttransVO> list = queryBySql(vo.getPk_corp(), debitAccount, creditAccount, pk_fillaccount);
            if (list != null && list.size() > 0) {
                if (pk_fillaccount.length() > 0) {
                    throw new BusinessException("借方科目、贷方科目、取数科目已存在相同的数据。");
                } else {
                    throw new BusinessException("借方科目、贷方科目已存在相同的数据。");
                }
            }
        }
    }

    public List<CpcosttransVO> queryBySql(String pk_corp, String debitAccount, String creditAccount, String pk_fillaccount) {
        StringBuffer condition = new StringBuffer();
        condition.append(" select * from ynt_cpcosttrans where nvl(dr,0)=0 and pk_corp=? and pk_debitaccount=? and pk_creditaccount=? ");
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        params.addParam(debitAccount);
        params.addParam(creditAccount);
        if (pk_fillaccount != null && pk_fillaccount.length() > 0) {
            condition.append(" and pk_fillaccount=? ");
            params.addParam(pk_fillaccount);
        }
//		else{
//			condition = " select * from ynt_cpcosttrans where nvl(dr,0)=0 and pk_corp=? and pk_debitaccount=? "
//					+ "and pk_creditaccount=? ";
//		}
        return (List<CpcosttransVO>) singleObjectBO.executeQuery(condition.toString(), params, new BeanListProcessor(CpcosttransVO.class));
//		Collections col = (Collections) singleObjectBO.retrieveByClause(CpcosttransVO.class, condition, params);
//		col.
//		CpcosttransVO[] vos = (CpcosttransVO[]) singleObjectBO.queryByCondition(CpcosttransVO.class, condition, params);
    }

}

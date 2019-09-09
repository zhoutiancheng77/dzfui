package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.utils.StringUtil;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardVO;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCleanVO;
import com.dzf.zxkj.platform.model.am.zcgl.AssetDepTemplate;
import com.dzf.zxkj.platform.model.am.zcgl.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetCleanService;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetCardService;
import com.dzf.zxkj.platform.services.bdset.IYntCpaccountService;
import com.dzf.zxkj.platform.vo.am.zcgl.AssetCleanQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@Service
public class AssetCleanServiceImpl implements IAssetCleanService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAssetCardService assetCardService;

    @Autowired
    private IYntCpaccountService yntCpaccountService;

    @Override
    public List<AssetCleanVO> query(AssetCleanQueryVO assetCleanQueryVO) throws DZFWarpException {
        SQLParameter sqlParameter = new SQLParameter();
        String sql = buildSql(assetCleanQueryVO, sqlParameter);
        return (List<AssetCleanVO>) singleObjectBO.executeQuery(sql, sqlParameter, new BeanListProcessor(AssetCleanVO.class));
    }

    private String buildSql(AssetCleanQueryVO assetCleanQueryVO, SQLParameter sqlParameter) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ynt_tzpz_h.pzh as voucherno, ynt_assetcard.assetcode as pk_assetcard_name, ynt_assetclear.*");
        sql.append(" from ynt_assetclear ynt_assetclear ");
        sql.append(" left join ynt_assetcard ynt_assetcard on ynt_assetcard.pk_assetcard = ynt_assetclear.pk_assetcard ");
        sql.append(" left join ynt_tzpz_h ynt_tzpz_h on ynt_tzpz_h.pk_tzpz_h = ynt_assetclear.pk_voucher ");
        sql.append(" where nvl(ynt_assetclear.dr,0) = 0 and nvl(ynt_assetcard.dr,0) =0 ");

        if (!StringUtil.isEmpty(assetCleanQueryVO.getPk_corp())) {
            sql.append(" and ynt_assetclear.pk_corp = ? ");
            sqlParameter.addParam(assetCleanQueryVO.getPk_corp());
        }
        if (assetCleanQueryVO.getStart_date().compareTo(assetCleanQueryVO.getEnd_date()) == 0) {
            sql.append(" and ynt_assetclear.businessdate = ? ");
            sqlParameter.addParam(assetCleanQueryVO.getStart_date());
        } else {
            sql.append(" and (ynt_assetclear.businessdate >= ? and ynt_assetclear.businessdate <= ? )");
            sqlParameter.addParam(assetCleanQueryVO.getStart_date());
            sqlParameter.addParam(assetCleanQueryVO.getEnd_date());
        }
        if (!StringUtil.isEmpty(assetCleanQueryVO.getAsscd_id())) {
            sql.append(" and ynt_assetclear.pk_assetcard =  ? ");
            sqlParameter.addParam(assetCleanQueryVO.getAsscd_id());
        }
        if (!StringUtil.isEmpty(assetCleanQueryVO.getAscode())) {
            sql.append(" and ynt_assetcard.assetcode like ? ");
            sqlParameter.addParam(assetCleanQueryVO.getAscode());
        }
        sql.append(" order by ynt_assetcard.assetcode,  ynt_assetclear.businessdate");
        return sql.toString();
    }

    @Override
    public void delete(AssetCleanVO vo) throws DZFWarpException {
        checkBeforeDelete(vo);
        singleObjectBO.deleteObject(vo);
        assetCardService.updateIsClear(new String[]{vo.getPk_assetcard()}, false);
    }

    private void checkBeforeDelete(AssetCleanVO vo) {
        if (vo.getIstogl().booleanValue()) {
            throw new BusinessException("已转总账，不允许删除。");
        }
    }

    @Override
    public void insertToGL(String loginDate, CorpVO corpvo, AssetCleanVO assetCleanVO) throws DZFWarpException {
        checkBeforeToGl(loginDate, corpvo, assetCleanVO);
        String pk_voucher = createVoucher(loginDate, corpvo, assetCleanVO);
    }

    private String createVoucher(String loginDate, CorpVO corpvo, AssetCleanVO assetCleanVO) {
        AssetCardVO assetcardVO = (AssetCardVO) singleObjectBO
                .queryByPrimaryKey(AssetCardVO.class, assetCleanVO.getPk_assetcard());
        if (assetcardVO == null) {
            throw new BusinessException("资产不存在！");
        }

        YntCpaccountVO[] accountvos = yntCpaccountService.get(corpvo.getPk_corp());

        if (accountvos == null || accountvos.length == 0) {
            throw new BusinessException("该公司科目不存在");
        }

        BdAssetCategoryVO categoryVO = (BdAssetCategoryVO) singleObjectBO
                .queryByPrimaryKey(BdAssetCategoryVO.class,
                        assetcardVO.getAssetcategory());

        AssetDepTemplate[] vos = getClearVoucherTemplet(corpvo, assetcardVO, accountvos, categoryVO);

        return null;
    }

    private AssetDepTemplate[] getClearVoucherTemplet(CorpVO corpvo, AssetCardVO assetcardVO, YntCpaccountVO[] accountvos, BdAssetCategoryVO categoryVO) {
        String zy = "固定资产清理";
        if (categoryVO != null) {
            if (categoryVO.getAssetproperty() != null) {
                if (categoryVO.getAssetproperty().intValue() == 1) {
                    zy = "无形资产清理";
                } else if (categoryVO.getAssetproperty().intValue() == 3) {
                    zy = "待摊费用清理";
                }
            }
        }
        AssetDepTemplate[] vos = null;
        if (categoryVO.getAssetproperty().intValue() == 3) {//待摊费用
            vos = new AssetDepTemplate[2];
            //累计折旧
            vos[0] = new AssetDepTemplate();
            vos[0].setPk_account(assetcardVO.getPk_zjfykm());//借，费用科目
            vos[0].setAccountkind(1);
            vos[0].setDirect(0);
            vos[0].setAbstracts(zy);
            //固定资产(0)
            vos[1] = new AssetDepTemplate();
            vos[1].setPk_account(assetcardVO.getPk_jtzjkm());//贷，折旧科目
            vos[1].setAccountkind(1);
            vos[1].setDirect(1);
            vos[1].setAbstracts(zy);
        } else {
            vos = new AssetDepTemplate[3];
            //累计折旧
            vos[0] = new AssetDepTemplate();
            vos[0].setPk_account(assetcardVO.getPk_jtzjkm());
            vos[0].setAccountkind(2);
            vos[0].setDirect(0);
            vos[0].setAbstracts(zy);
            //资产清理科目
            vos[1] = new AssetDepTemplate();
            vos[1].setPk_account(getAssetClearAccountId(corpvo, accountvos, categoryVO));
            vos[1].setAccountkind(1);
            vos[1].setDirect(0);
            vos[1].setAbstracts(zy);
            //固定资产(0)
            vos[2] = new AssetDepTemplate();
            vos[2].setPk_account(assetcardVO.getPk_zckm());//资产科目
            vos[2].setAccountkind(0);
            vos[2].setDirect(1);
            vos[2].setAbstracts(zy);
        }
        return vos;
    }

    private String getAssetClearAccountId(CorpVO corpvo, YntCpaccountVO[] accountvos, BdAssetCategoryVO categoryVO) {
//        Map<String, YntCpaccountVO> code_map = new HashMap<String, YntCpaccountVO>();
//        for(YntCpaccountVO vo:accountvos){
//            code_map.put(vo.getAccountcode(), vo);
//        }
//        Integer corpschema = yntBoPubUtil.getAccountSchema(corpvo.getPk_corp());
//        String accountcode = "";
//
//        String queryAccountRule = gl_cpacckmserv.queryAccountRule(corpvo.getPk_corp());
//
//        if (corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {// 2013会计准则 小企业会计准则
//            if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
//                accountcode = gl_accountcoderule.getNewRuleCode("571101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
//            }else{//固定资产
//                accountcode = "1606";
//            }
//        } else if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {//2007会计准则 企业会计准则
//            if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
//                accountcode = gl_accountcoderule.getNewRuleCode("6115", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
//            }else{//固定资产
//                accountcode = "1606";
//            }
//        } else if (corpschema == DzfUtil.POPULARSCHEMA.intValue()) {// 民间
//            if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
//                accountcode = "5401";
//            }else{//固定资产
//                accountcode = "1509";
//            }
//        } else if (corpschema == DzfUtil.CAUSESCHEMA.intValue()) {// 事业
//            accountcode = "1701";
//        }else if(corpschema ==  DzfUtil.COMPANYACCOUNTSYSTEM.intValue() ){//企业会计制度
//            if(categoryVO.getAssetproperty().intValue() ==1){//无形资产
//                accountcode = gl_accountcoderule.getNewRuleCode("560101", DZFConstant.ACCOUNTCODERULE, queryAccountRule);
//            }else{//固定资产
//                accountcode = "1701";
//            }
//        } else {
//            throw new BusinessException("该制度资产清理,敬请期待!");
//        }
//        YntCpaccountVO resvo = code_map.get(accountcode);
//        if(resvo == null){
//            throw new BusinessException("清理科目不存在，请进行科目升级");
//        }
//        return resvo.getPk_corp_account();
        return null;
    }

    private void checkBeforeToGl(String loginDate, CorpVO corpvo, AssetCleanVO assetCleanVO) {
        AssetCleanVO oldVO = (AssetCleanVO) singleObjectBO.queryByPrimaryKey(
                AssetCleanVO.class, assetCleanVO.getPrimaryKey());
        if (corpvo == null) {
            throw new BusinessException("公司信息为空");
        }

        if (oldVO == null) {
            throw new BusinessException("该数据已经被他人删除，请刷新界面");
        }
        if (assetCleanVO.getIstogl() != null && assetCleanVO.getIstogl().booleanValue()) {
            throw new BusinessException(String.format("资产清理单%s已经转总账，不允许重复转总账",
                    assetCleanVO.getVbillno()));
        }

        if (assetCleanVO.getIssettle() != null && assetCleanVO.getIssettle().booleanValue()) {
            throw new BusinessException(String.format("资产清理单%s已经结账，不允许转总账",
                    assetCleanVO.getVbillno()));
        }
    }

    @Override
    public AssetCleanVO queryById(String id) throws DZFWarpException {
        return null;
    }

    @Override
    public AssetCleanVO refresh(String pk_acs) throws DZFWarpException {
        return null;
    }

    @Override
    public void processAssetClears(String loginDate, CorpVO corpvo, SuperVO[] assetcardVOs, String loginuserid) throws DZFWarpException {

    }

    @Override
    public void updateACToGLState(String pk_assetclear, boolean istogl, String pk_voucher) throws DZFWarpException {

    }
}

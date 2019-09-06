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
import com.dzf.zxkj.platform.model.am.zcgl.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetCleanService;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetCardService;
import com.dzf.zxkj.platform.services.bdset.IYntCpaccountService;
import com.dzf.zxkj.platform.vo.am.zcgl.AssetCleanQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        if(assetcardVO == null){
            throw new BusinessException("资产不存在！");
        }

        YntCpaccountVO[] accountvos = yntCpaccountService.get(corpvo.getPk_corp());

        if(accountvos == null || accountvos.length ==0){
            throw new BusinessException("该公司科目不存在");
        }

        BdAssetCategoryVO categoryVO = (BdAssetCategoryVO) singleObjectBO
                .queryByPrimaryKey(BdAssetCategoryVO.class,
                        assetcardVO.getAssetcategory());

        return null;
    }

    private void checkBeforeToGl(String loginDate, CorpVO corpvo, AssetCleanVO assetCleanVO) {
        AssetCleanVO oldVO = (AssetCleanVO) singleObjectBO.queryByPrimaryKey(
                AssetCleanVO.class, assetCleanVO.getPrimaryKey());
        if (oldVO == null) {
            throw new BusinessException("该数据已经被他人删除，请刷新界面");
        }
        if (assetCleanVO.getIstogl() != null && assetCleanVO.getIstogl().booleanValue()){
            throw new BusinessException(String.format("资产清理单%s已经转总账，不允许重复转总账",
                    assetCleanVO.getVbillno()));
        }

        if (assetCleanVO.getIssettle() != null && assetCleanVO.getIssettle().booleanValue()){
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

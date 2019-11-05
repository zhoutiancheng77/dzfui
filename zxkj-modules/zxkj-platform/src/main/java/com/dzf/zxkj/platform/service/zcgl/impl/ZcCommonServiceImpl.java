package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdTradeAssetCheckVO;
import com.dzf.zxkj.platform.service.zcgl.IZcCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ZcCommonServiceImpl implements IZcCommonService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    /**
     * 获取公司自己的资产科目对照 如果没有 返回行业资产科目对照
     *
     * @param corpType
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    public BdTradeAssetCheckVO[] getAssetcheckVOs(String corpType, String pk_corp)
            throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter param = new SQLParameter();
        sb.append("  pk_corp = ? and ");
        param.addParam(pk_corp);
        if (pk_corp != null) {
            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema in");
            sb.append("(select corptype from bd_corp where pk_corp=?)  order by assetproperty,pk_assetcategory ");
            param.addParam(pk_corp);
        } else {
            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema =?");
            sb.append(" order by assetproperty,pk_assetcategory ");
            param.addParam(corpType);
        }
        BdTradeAssetCheckVO[] assetcheckVOs = (BdTradeAssetCheckVO[]) singleObjectBO
                .queryByCondition(BdTradeAssetCheckVO.class, sb.toString(),
                        param);

        if (assetcheckVOs == null || assetcheckVOs.length == 0) {
            // 如果没有，查询行业的档案
            assetcheckVOs = getAssetcheckVOsHY(corpType,
                    pk_corp);
            if (assetcheckVOs == null || assetcheckVOs.length == 0) {
                throw new BusinessException("行业资产科目对照为空，请检查!");
            }
        }
//        VOSortUtils.ascSort(assetcheckVOs, new String[]{"assetproperty", "pk_assetcategory"});   排序有问题 空值按最小算
        return assetcheckVOs;

    }

    /**
     * 获取行业资产科目对照
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    private BdTradeAssetCheckVO[] getAssetcheckVOsHY(String corpType, String pk_corp) throws DZFWarpException {
        StringBuilder sb = new StringBuilder();
        SQLParameter param = new SQLParameter();
        sb.append("  pk_corp = ? and ");
        param.addParam(IGlobalConstants.DefaultGroup);
        if (pk_corp != null) {
            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema in");
            sb.append("(select corptype from bd_corp where pk_corp=?)  order by assetproperty,pk_assetcategory ");
            param.addParam(pk_corp);
        } else {
            sb.append(" nvl(dr,0)=0 and pk_trade_accountschema = ?");
            sb.append(" order by assetproperty,pk_assetcategory ");
            param.addParam(corpType);
        }
        BdTradeAssetCheckVO[] assetcheckVOs = (BdTradeAssetCheckVO[]) singleObjectBO
                .queryByCondition(BdTradeAssetCheckVO.class, sb.toString(),
                        param);
        return assetcheckVOs;
    }

    @Override
    public Map<String, BdAssetCategoryVO> queryAssetCategoryMap() throws DZFWarpException {
        BdAssetCategoryVO[] vos = (BdAssetCategoryVO[]) singleObjectBO
                .queryByCondition(BdAssetCategoryVO.class, "nvl(dr,0)=0", null);
        if (vos == null || vos.length == 0) {
            return new HashMap<>();
        }

        return Arrays.stream(vos).filter(v -> !StringUtil.isEmpty(v.getPk_assetcategory())).collect(Collectors.toMap(BdAssetCategoryVO::getPk_assetcategory, v -> v, (k1, k2) -> k1));
    }
}

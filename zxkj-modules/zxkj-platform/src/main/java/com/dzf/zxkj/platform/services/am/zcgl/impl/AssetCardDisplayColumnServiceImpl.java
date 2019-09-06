package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.StringUtil;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardDisplayColumnVO;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetCardDisplayColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@Service
public class AssetCardDisplayColumnServiceImpl implements IAssetCardDisplayColumnService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public List<AssetCardDisplayColumnVO> qryDisplayColumns(String pk_corp) throws DZFWarpException {
        StringBuilder sql = new StringBuilder();
        sql.append(" select *   ");
        sql.append(" from " + AssetCardDisplayColumnVO.TABLE_NAME);
        sql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);

        List<AssetCardDisplayColumnVO> lists = (List<AssetCardDisplayColumnVO>) singleObjectBO
                .executeQuery(sql.toString(), sp, new BeanListProcessor(AssetCardDisplayColumnVO.class));
        return lists;
    }

    @Override
    public void saveDisplayColumn(AssetCardDisplayColumnVO displayvo, String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("公司不能为空");
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        AssetCardDisplayColumnVO[] vos = (AssetCardDisplayColumnVO[]) singleObjectBO.queryByCondition(AssetCardDisplayColumnVO.class, "nvl(dr,0)=0 and pk_corp = ? ", sp);

        if (vos != null && vos.length > 0) {
            vos[0].setSetting(displayvo.getSetting());
            singleObjectBO.update(vos[0]);
        } else {
            displayvo.setPk_corp(pk_corp);
            singleObjectBO.saveObject(pk_corp, displayvo);
        }
    }
}

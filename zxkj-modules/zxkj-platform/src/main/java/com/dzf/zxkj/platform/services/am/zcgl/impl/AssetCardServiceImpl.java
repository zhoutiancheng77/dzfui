package com.dzf.zxkj.platform.services.am.zcgl.impl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardVO;
import com.dzf.zxkj.platform.model.am.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.am.zcgl.IAssetCardService;
import com.dzf.zxkj.platform.vo.am.zcgl.AssetcardQueryVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
@Service
public class AssetCardServiceImpl implements IAssetCardService {
    @Override
    public void updateToGLState(String pk_assetCard, boolean istogl, String pk_voucher, Integer ope) throws DZFWarpException {

    }

    @Override
    public void updateAssetMny(AssetCardVO vo, double assetmny, ValuemodifyVO modifyvo) throws DZFWarpException {

    }

    @Override
    public void updateIsClear(String[] pk_assetCards, boolean isclear) throws DZFWarpException {

    }

    @Override
    public void updateIsClear(AssetCardVO assetcardVO, boolean isclear) throws DZFWarpException {

    }

    @Override
    public void updateExecuteDepreciate(CorpVO corpvo, AssetCardVO[] assetcarvo, String period, String coperatorid) throws DZFWarpException {

    }

    @Override
    public AssetCardVO save(CorpVO corpvo, AssetCardVO vo) throws DZFWarpException {
        return null;
    }

    @Override
    public AssetCardVO saveCard(String pk_corp, AssetCardVO vo) throws DZFWarpException {
        return null;
    }

    @Override
    public String buildAssetcardCode(String pkCorp) throws DZFWarpException {
        return null;
    }

    @Override
    public List<AssetCardVO> query(AssetcardQueryVo assetcardQueryVo) throws DZFWarpException {
        return null;
    }

    @Override
    public void delete(AssetCardVO[] vos) throws DZFWarpException {

    }

    @Override
    public AssetCardVO queryById(String id) throws DZFWarpException {
        return null;
    }

    @Override
    public AssetCardVO update(CorpVO corpvo, AssetCardVO vo) throws DZFWarpException {
        return null;
    }

    @Override
    public List<AssetCardVO> queryByPkcorp(String loginDate, String pk_corp, String isclear) throws DZFWarpException {
        return null;
    }

    @Override
    public List<AssetCardVO> queryByPkCorp(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public void saveToGl(AssetCardVO vo) throws DZFWarpException {

    }

    @Override
    public void updateAssetClear(String loginDate, CorpVO corpvo, AssetCardVO[] selectedVOs, String loginuserid) throws DZFWarpException {

    }

    @Override
    public List<AssetCardVO> queryByIds(String ids) throws DZFWarpException {
        return null;
    }

    @Override
    public String checkEdit(String pk_corp, AssetCardVO cardvo) throws DZFWarpException {
        return null;
    }

    @Override
    public Object[] impExcel(String loginDate, String userid, CorpVO corpvo, AssetCardVO[] selectedVOs) throws DZFWarpException {
        return new Object[0];
    }

    @Override
    public void updateOrder(String pk_corp) throws DZFWarpException {

    }

    @Override
    public void checkCorp(String pk_corp, List<String> ids) throws DZFWarpException {

    }

    @Override
    public String saveVoucherFromZc(String[] assetids, String pk_corp, String coperatorid, DZFDate currDate, DZFBoolean bhb) throws DZFWarpException {
        return null;
    }

    @Override
    public String getMinAssetPeriod(String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public void updateAdjustLimit(String id, Integer newlimit) throws DZFWarpException {

    }

    @Override
    public String brepeat_sig(AssetCardVO kpvos, String pk_corp) throws DZFWarpException {
        return null;
    }

    @Override
    public String brepeat_mul(List<AssetCardVO> kpvos, String pk_corp) throws DZFWarpException {
        return null;
    }
}

package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.custom.type.DZFBoolean;
import com.dzf.zxkj.custom.type.DZFDate;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCardVO;
import com.dzf.zxkj.platform.model.am.zcgl.ValuemodifyVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.vo.am.zcgl.AssetcardQueryVo;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IAssetCardService {
    void updateToGLState(String pk_assetCard, boolean istogl, String pk_voucher, Integer ope) throws DZFWarpException;

    void updateAssetMny(AssetCardVO vo, double assetmny, ValuemodifyVO modifyvo) throws DZFWarpException;

    void updateIsClear(String[] pk_assetCards, boolean isclear) throws DZFWarpException;

    void updateIsClear(AssetCardVO assetcardVO, boolean isclear) throws DZFWarpException;

    void updateExecuteDepreciate(CorpVO corpvo, AssetCardVO[] assetcarvo, String period, String coperatorid) throws DZFWarpException;

    AssetCardVO save(CorpVO corpvo, AssetCardVO vo) throws DZFWarpException;

    AssetCardVO saveCard(String pk_corp, AssetCardVO vo) throws DZFWarpException;

    String buildAssetcardCode(String pkCorp) throws DZFWarpException;

    List<AssetCardVO> query(AssetcardQueryVo assetcardQueryVo) throws DZFWarpException;

    void delete(AssetCardVO[] vos) throws DZFWarpException;

    AssetCardVO queryById(String id) throws DZFWarpException;

    AssetCardVO update(CorpVO corpvo, AssetCardVO vo) throws DZFWarpException;

    List<AssetCardVO> queryByPkcorp(String loginDate, String pk_corp, String isclear) throws DZFWarpException;

    List<AssetCardVO> queryByPkCorp(String pk_corp) throws DZFWarpException;

    void saveToGl(AssetCardVO vo) throws DZFWarpException;

    void updateAssetClear(String loginDate, CorpVO corpvo, AssetCardVO[] selectedVOs, String loginuserid) throws DZFWarpException;

    List<AssetCardVO> queryByIds(String ids) throws DZFWarpException;

    String checkEdit(String pk_corp, AssetCardVO cardvo) throws DZFWarpException;

    Object[] impExcel(String loginDate, String userid, CorpVO corpvo, AssetCardVO[] selectedVOs) throws DZFWarpException;

    void updateOrder(String pk_corp) throws DZFWarpException;

    void checkCorp(String pk_corp, List<String> ids) throws DZFWarpException;

    String saveVoucherFromZc(String[] assetids, String pk_corp, String coperatorid, DZFDate currDate, DZFBoolean bhb) throws DZFWarpException;

    String getMinAssetPeriod(String pk_corp) throws DZFWarpException;

    void updateAdjustLimit(String id, Integer newlimit) throws DZFWarpException;

    String brepeat_sig(AssetCardVO kpvos, String pk_corp) throws DZFWarpException;

    String brepeat_mul(List<AssetCardVO> kpvos, String pk_corp) throws DZFWarpException;
}

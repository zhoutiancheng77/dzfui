package com.dzf.zxkj.platform.services.am.zcgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.am.zcgl.AssetCleanVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.vo.am.zcgl.AssetCleanQueryVO;

import java.util.List;

/**
 * @Auther: dandelion
 * @Date: 2019-09-05
 * @Description:
 */
public interface IAssetCleanService {
    List<AssetCleanVO> query(AssetCleanQueryVO assetCleanQueryVO) throws DZFWarpException;

    void delete(AssetCleanVO vo) throws DZFWarpException;

    void insertToGL(String loginDate, CorpVO corpvo, AssetCleanVO vo) throws DZFWarpException;

    AssetCleanVO queryById(String id) throws DZFWarpException;

    AssetCleanVO refresh(String pk_acs) throws DZFWarpException;

    void processAssetClears(String loginDate, CorpVO corpvo, SuperVO[] assetcardVOs, String loginuserid) throws DZFWarpException;

    void updateACToGLState(String pk_assetclear, boolean istogl, String pk_voucher) throws DZFWarpException;
}

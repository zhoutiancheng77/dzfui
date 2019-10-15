package com.dzf.zxkj.platform.service.qcset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

public interface IFzhsqcService {
    /**
     * 新增辅助核算组合
     *
     * @param vo
     * @param user
     * @param corp
     * @return
     * @throws BusinessException
     */
    FzhsqcVO saveCombo(FzhsqcVO vo, UserVO user, CorpVO corp) throws DZFWarpException;

    FzhsqcVO[] queryByPk(String pk_corp, String pk_accsubj) throws DZFWarpException;

    FzhsqcVO[] queryFzQc(String pk_corp, String pk_accsubj) throws DZFWarpException;

    void saveFzQc(String pk_accsubj, FzhsqcVO[] fzvos, String currency, QcYeVO qcvo, UserVO user, CorpVO corp)
            throws DZFWarpException;

    FzhsqcVO[] queryAll(String pk_corp, String pk_currency) throws DZFWarpException;
}

package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.BDabstractsVO;

public interface IBDabstractsService {

    BDabstractsVO[] query(String pk_corp) throws DZFWarpException;

    BDabstractsVO queryByID(String pk_abstracts) throws DZFWarpException;

    BDabstractsVO save(BDabstractsVO vo) throws DZFWarpException;

    void delete(BDabstractsVO vo) throws DZFWarpException;

    void existCheck(BDabstractsVO vo) throws DZFWarpException;

    BDabstractsVO[] queryParent(String pk_corp) throws DZFWarpException;

}

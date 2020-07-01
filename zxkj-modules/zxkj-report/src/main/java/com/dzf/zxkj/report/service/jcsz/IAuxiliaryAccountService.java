package com.dzf.zxkj.report.service.jcsz;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;

import java.util.Map;

public interface IAuxiliaryAccountService {
     Map<String, AuxiliaryAccountBVO> queryMap(String pk_corp) throws DZFWarpException;
    AuxiliaryAccountBVO[] queryAllB(String pk_corp) throws DZFWarpException;
    AuxiliaryAccountHVO queryHByCode(String pk_corp, String fzlb);
    AuxiliaryAccountHVO[] queryHByPkCorp(String pk_corp);
    AuxiliaryAccountBVO[] queryBByFzlb(String pk_corp, String fzlb);
    boolean isExistFz(String pk_corp, String pk_auacount_b,String pk_auacount_h) throws DZFWarpException;
}

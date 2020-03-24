package com.dzf.zxkj.platform.service.bill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;

import java.util.List;

public interface IBillingProcessService {
    List<BillApplyVO> query(String pk_corp, String customer, Integer status) throws DZFWarpException;

    List<BillApplyDetailVo> queryB(String pk_apply, String pk_corp) throws DZFWarpException;

    String billing(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String sentOut(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String accounting(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String tax(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String open(BillApplyVO[] bills, String userid) throws DZFWarpException;
}

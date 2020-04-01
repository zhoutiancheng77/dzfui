package com.dzf.zxkj.platform.service.bill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IBillingProcessService {
    List<BillApplyVO> query(String pk_corp, BillApplyVO appvo) throws DZFWarpException;

    List<BillApplyDetailVo> queryB(String pk_apply, String pk_corp) throws DZFWarpException;

    String billing(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String sentOut(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String accounting(BillApplyVO[] bills, String userid) throws DZFWarpException;

    String tax(BillApplyVO[] bills, String userid) throws DZFWarpException;

    boolean createKp(BillApplyVO vo, UserVO uservo, StringBuffer msg) throws DZFWarpException;

    boolean createHc(BillApplyVO vo, UserVO uservo, StringBuffer msg) throws DZFWarpException;

    boolean delete(BillApplyVO vo, UserVO uservo, StringBuffer msg) throws DZFWarpException;

    BillApplyVO queryBySerialNo(String serialno) throws DZFWarpException;
}

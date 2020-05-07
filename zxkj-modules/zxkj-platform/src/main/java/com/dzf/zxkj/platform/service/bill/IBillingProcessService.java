package com.dzf.zxkj.platform.service.bill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bill.BillApplyDetailVo;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.io.InputStream;
import java.util.List;

public interface IBillingProcessService {
    public List<BillApplyVO> query(String userid, String pk_corp, BillApplyVO apvo) throws DZFWarpException;

    public List<BillApplyDetailVo> queryB(String pk_apply,String pk_corp) throws DZFWarpException;

    public void saveImp(InputStream is, BillApplyVO paramvo, String pk_corp, String fileType, String userid, StringBuffer msg) throws DZFWarpException;

    public boolean createKp (BillApplyVO vo, UserVO uservo) throws DZFWarpException;

    public BillApplyVO queryBySerialNo(String serialno) throws DZFWarpException;
    public BillApplyVO queryByFPDMHM(String fpdm, String fphm, String pk_corp) throws DZFWarpException;

    public BillApplyVO saveHcBill (BillApplyVO vo, UserVO uservo) throws DZFWarpException;

    public boolean createHc (BillApplyVO vo, UserVO uservo) throws DZFWarpException;

    public void delete (BillApplyVO vo, UserVO uservo) throws DZFWarpException;

    public boolean createBilling (BillApplyVO vo, UserVO uservo) throws DZFWarpException;

    public String billing (BillApplyVO[] bills, String userid) throws DZFWarpException;

    public String sentOut (BillApplyVO[] bills, String userid) throws DZFWarpException;

    public String accounting (BillApplyVO[] bills, String userid) throws DZFWarpException;

    public String tax (BillApplyVO[] bills, String userid) throws DZFWarpException;
}

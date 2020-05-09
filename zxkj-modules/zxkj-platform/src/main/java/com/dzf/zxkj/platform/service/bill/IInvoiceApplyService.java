package com.dzf.zxkj.platform.service.bill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResVO;

import java.util.List;

public interface IInvoiceApplyService {

    List<InvoiceApplyVO> query(String userid) throws DZFWarpException;

    InvoiceApplyVO queryByGs(String pk_corp) throws DZFWarpException;

    List<InvoiceApplyVO> queryInviceByCode(String unitname, String vsoccrecode) throws DZFWarpException;

    List<InvoiceApplyVO> save(String userid, String pk_corp, String[] gss) throws DZFWarpException;

    PiaoTongResVO saveApply(String userid, InvoiceApplyVO vo) throws DZFWarpException;
}

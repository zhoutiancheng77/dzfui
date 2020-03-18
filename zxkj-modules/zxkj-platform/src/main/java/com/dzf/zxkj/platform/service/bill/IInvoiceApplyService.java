package com.dzf.zxkj.platform.service.bill;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;

import java.util.List;

public interface IInvoiceApplyService {

    List<InvoiceApplyVO> query(String userid, int page, int rows, InvoiceApplyVO paramvo) throws DZFWarpException;

    List<InvoiceApplyVO> save(String userid, String pk_corp, String[] gss) throws DZFWarpException;

    void saveApply(String userid, String pk_corp, InvoiceApplyVO[] vos) throws DZFWarpException;
}

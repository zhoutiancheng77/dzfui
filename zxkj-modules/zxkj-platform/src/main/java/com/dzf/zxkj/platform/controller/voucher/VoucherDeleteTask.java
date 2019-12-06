package com.dzf.zxkj.platform.controller.voucher;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.voucher.PzglmessageVO;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
public class VoucherDeleteTask implements Callable<String> {
    public VoucherDeleteTask(TzpzHVO delData, Set<String> powerCorpSet, List<PzglmessageVO> errorlist,
                             IPzglService gl_pzglserv,
                             IVoucherService gl_tzpzserv) {
        this.delData = delData;
        this.powerCorpSet = powerCorpSet;
        this.errorlist = errorlist;
        this.gl_pzglserv = gl_pzglserv;
        this.gl_tzpzserv = gl_tzpzserv;
    }

    private TzpzHVO delData;
    private Set<String> powerCorpSet;
    private List<PzglmessageVO> errorlist;
    private IPzglService gl_pzglserv;
    private IVoucherService gl_tzpzserv;

    @Override
    public String call() {
        String result = deleteData(delData, powerCorpSet, errorlist);
        return result;
    }

    private String deleteData(TzpzHVO delData, Set<String> powerCorpSet, List<PzglmessageVO> errorlist) {
        String end = "ok";
        if (delData == null)
            return end;
        String pzh = delData.getPzh();
        String pk_corp = delData.getPk_corp();
        String id = delData.getPk_tzpz_h();
        TzpzHVO tzpzH = null;
        try {
            tzpzH = gl_pzglserv.queryByID(id, pk_corp);
            if (tzpzH == null)
                return end;
            if (!powerCorpSet.contains(tzpzH.getPk_corp()))
                return end;
            tzpzH.setIsqxsy(DZFBoolean.TRUE);
            tzpzH.setIssvbk(DZFBoolean.FALSE);
            gl_tzpzserv.deleteVoucher(tzpzH);
        } catch (Exception e) {
            String msg = null;
            if (e instanceof BusinessException) {
                msg = e.getMessage();
            }
            PzglmessageVO vo = createPzglVO(pzh, pk_corp, delData.getVdef4(), tzpzH.getPeriod(), msg);
            errorlist.add(vo);
            log.error("凭证号：" + pzh + "，pk_corp:" + pk_corp + "，id:" + id + "，删除凭证失败!", e);
        }
        return end;
    }

    private PzglmessageVO createPzglVO(String pzh, String pk_corp,
                                       String corpName, String period, String msg) {
        PzglmessageVO vo = new PzglmessageVO();
        vo.setPzh(pzh);
        vo.setPk_corp(pk_corp);
        vo.setGsname(corpName);
        vo.setPeriod(period);
        vo.setErrorinfo(msg);
        return vo;
    }
}

package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.bill.IAppBillService;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.base.exception.DZFWarpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class AppBillDubbo implements IAppBillService {

    @Autowired
    private IAppCorpService appCorpService;

    @Override
    public ResponseBaseBeanVO qrykpmsg(String pk_corp, String pk_temp_corp, String account_id, String account) {
        try {
            return appCorpService.qrykpmsg(pk_corp, pk_temp_corp, account_id, account);
        } catch (DZFWarpException e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public ResponseBaseBeanVO saveKpmsg(String pk_corp, String pk_temp_corp, String account_id, String corpname, String sh, String gsdz, String kpdh, String khh, String khzh, String grdh, String gryx) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        try {
            appCorpService.saveKpmsg(pk_corp,pk_temp_corp,account_id,corpname,
                    sh,gsdz,kpdh,khh,khzh, grdh, gryx);
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("保存开票信息成功!");
        } catch (DZFWarpException e) {
            log.error(e.getMessage(),e);
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("保存开票信息失败!");
        }
        return bean;
    }
}

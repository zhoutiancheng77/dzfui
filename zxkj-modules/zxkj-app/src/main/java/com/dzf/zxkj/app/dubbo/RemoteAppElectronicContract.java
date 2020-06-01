package com.dzf.zxkj.app.dubbo;

import com.dzf.admin.dzfapp.model.econtract.AppEContQryVO;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.econtract.IDzfAppEcontractService;
import com.dzf.zxkj.app.electroniccontract.IApplEctronicContract;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@org.springframework.stereotype.Service("applEctronicContract")
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class RemoteAppElectronicContract implements IApplEctronicContract {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppEcontractService iDzfAppEcontractService;

    @Override
    public AppResult updateSign(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.confirmSign(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public AppResult querySignEContDet(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.querySignEContDet(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public AppResult queryUnSignEContDet(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.queryUnSignEContDet(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public AppResult queryEContract(AppEContQryVO pamVO) {
        try {
            return iDzfAppEcontractService.queryEContract(pamVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
}

package com.dzf.zxkj.app.dubbo;

import com.dzf.admin.dzfapp.model.econtract.AppSealVO;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.econtract.IDzfAppSealService;
import com.dzf.zxkj.app.electronicseal.IAppElectronicSealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@org.springframework.stereotype.Service("appElectronicSealService")
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class RemoteAppElectronicSealDubbo implements IAppElectronicSealService {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppSealService dzfAppSealService;

    @Override
    public AppResult<Boolean> haveSealStatus(AppSealVO sealVO) {
        try {
            return dzfAppSealService.haveSealStatus(sealVO);
        } catch (Exception e) {
           log.error(e.getMessage(),e);
        }
        return null;

    }

    @Override
    public AppResult updateSealStatus(AppSealVO sealVO) {
        try {
            return dzfAppSealService.confirmSealStatus(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public AppResult getCorpkSeals(AppSealVO sealVO) {
        try {
            return dzfAppSealService.getCorpkSeals(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public AppResult savePersonSign(AppSealVO sealVO) {
        try {
            return dzfAppSealService.confirmPersonSign(sealVO, null);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }

    @Override
    public AppResult<byte[]> getSealImg(AppSealVO sealVO) {
        try {
            return dzfAppSealService.getSealImg(sealVO);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return null;
    }
}

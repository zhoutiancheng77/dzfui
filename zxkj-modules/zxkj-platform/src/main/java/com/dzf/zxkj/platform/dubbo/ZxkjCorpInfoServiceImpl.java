package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.IZxkjCorpInfoService;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjCorpInfoServiceImpl implements IZxkjCorpInfoService {

    @Autowired
    IInventoryAccSetService accSetService;

    @Autowired
    private ICorpService corpService;

    @Override
    public void saveDefaultValue(String pk_corp){
        try {
            CorpVO corpvo =corpService.queryByPk(pk_corp);
            if(corpvo == null)
                throw new BusinessException("传入公司出错");
            accSetService.saveDefaultValue(null,corpvo,true);
        } catch (DZFWarpException e) {
            log.error(String.format("调用saveDefaultValue异常,异常信息:%s", e.getMessage()), e);
        }
    }

    @Override
    public void deleteDefaultValue(String pk_corp){
        accSetService.deleteDefaultValue(pk_corp);
    }
}



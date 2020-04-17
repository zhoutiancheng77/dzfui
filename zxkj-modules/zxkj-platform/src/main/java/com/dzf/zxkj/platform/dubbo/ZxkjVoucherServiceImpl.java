package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.Json;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.voucher.service.IVoucherService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjVoucherServiceImpl implements IVoucherService {

    @Autowired
    private com.dzf.zxkj.platform.service.pzgl.IVoucherService gl_tzpzserv;
    @Autowired
    private ICorpService corpService;

    @Override
    public String saveVoucher(String voucherJson) {
        Json json = new Json();
        try {
            TzpzHVO voucher = JsonUtils.deserialize(voucherJson, TzpzHVO.class);
            CorpVO corp = corpService.queryByPk(voucher.getPk_corp());
            voucher = gl_tzpzserv.saveVoucher(corp, voucher);
            json.setData(voucher);
            json.setSuccess(true);
        } catch (Exception e) {
            if (e instanceof BusinessException) {
                json.setMsg(e.getMessage());
            } else {
                json.setMsg("保存失败");
            }
            log.error("调用凭证接口失败", e);
        }
        return JsonUtils.serialize(json);
    }
}

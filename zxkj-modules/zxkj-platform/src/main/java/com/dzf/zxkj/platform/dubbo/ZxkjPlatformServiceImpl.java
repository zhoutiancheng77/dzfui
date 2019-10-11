package com.dzf.zxkj.platform.dubbo;

import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.services.bdset.ICpaccountService;
import com.dzf.zxkj.platform.services.bdset.IIncomeWarningService;
import com.dzf.zxkj.platform.services.report.impl.YntBoPubUtil;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class ZxkjPlatformServiceImpl implements IZxkjPlatformService {

    @Autowired
    private ICpaccountService gl_cpacckmserv;
    @Autowired
    private ICpaccountCodeRuleService gl_accountcoderule;

    @Autowired
    private YntBoPubUtil yntBoPubUtil;

    @Autowired
    private IIncomeWarningService iw_serv;// 预警信息

    @Override
    public Integer getAccountSchema(String pk_corp) {
        return yntBoPubUtil.getAccountSchema(pk_corp);
    }

    @Override
    public IncomeWarningVO[] queryIncomeWarningVOs(String pk_corp) {
        return iw_serv.query(pk_corp);
    }

    @Override
    public IncomeWarningVO[] queryFseInfo(IncomeWarningVO[] ivos, String pk_corp, String enddate) {
        iw_serv.queryFseInfo(ivos, pk_corp, enddate);
        return ivos;
    }

}

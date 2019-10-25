package com.dzf.zxkj.platform.controller.qcset;

import com.dzf.zxkj.common.entity.QcYeCurJson;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.qcset.IFzhsqcService;
import com.dzf.zxkj.platform.service.qcset.IQcye;
import com.dzf.zxkj.platform.util.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/qcset/gl_qcyeact")
@Slf4j
public class QcyeController {
    @Autowired
    private IQcye gl_qcyeserv;
    @Autowired
    private IFzhsqcService gl_fzhsqcserv;
    @Autowired
    private IQmgzService qmgzService;
    @Autowired
    private ISecurityService securityserv;

    @GetMapping("queryCur")
    public ReturnData<QcYeCurJson> queryCur() {
        QcYeCurJson js = new QcYeCurJson();
        try {
            String corpid = SystemUtil.getLoginCorpId();
            QcYeCurrency[] vos = gl_qcyeserv.queryCur(corpid);
            for (QcYeCurrency c : vos) {
                if ("人民币".equals(c.getCurrencyname())) {
                    js.setDefaultvalue(c.getPk_currency());
                    break;
                }
            }
            js.setSuccess(true);
            js.setMsg("查询当前公司外币成功!");
            js.setRows(vos);
        } catch (Exception e) {
            js.setSuccess(false);
            js.setMsg("查询当前公司外币失败!");
        }
        return ReturnData.ok().data(js);
    }
}

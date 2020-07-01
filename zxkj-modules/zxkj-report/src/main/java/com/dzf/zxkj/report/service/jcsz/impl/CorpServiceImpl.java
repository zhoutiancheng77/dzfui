package com.dzf.zxkj.report.service.jcsz.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.jcsz.ICorpService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CorpServiceImpl implements ICorpService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public CorpVO queryCorpByPk(String pk_corp) {
        CorpVO cvo = (CorpVO) singleObjectBO.queryVOByID(pk_corp, CorpVO.class);
        if (cvo != null) {
            try {
                cvo.setUnitname(CorpSecretUtil.deCode(cvo.getUnitname()));
                cvo.setUnitshortname(CorpSecretUtil.deCode(cvo.getUnitshortname()));
                cvo.setPhone1(CorpSecretUtil.deCode(cvo.getPhone1()));
                cvo.setPhone2(CorpSecretUtil.deCode(cvo.getPhone2()));
            } catch (Exception e) {
                log.error("解密失败！", e);
            }
        }
        return cvo;
    }
}

package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CorpServiceImpl implements ICorpService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public CorpVO queryByPk(String pk_corp) throws DZFWarpException {
        CorpVO cvo=	(CorpVO) singleObjectBO.queryVOByID(pk_corp, CorpVO.class);
        if (cvo != null) {
            try {
                cvo.setUnitname(CodeUtils1.deCode(cvo.getUnitname()));
                cvo.setUnitshortname(CodeUtils1.deCode(cvo.getUnitshortname()));
                cvo.setPhone1(CodeUtils1.deCode(cvo.getPhone1()));
                cvo.setPhone2(CodeUtils1.deCode(cvo.getPhone2()));
            } catch (Exception e) {
                log.error("解密失败！",e);
            }
        }
        return cvo;
    }
}

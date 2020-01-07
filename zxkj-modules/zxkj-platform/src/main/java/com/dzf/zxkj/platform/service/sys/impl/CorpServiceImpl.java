package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
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
                cvo.setUnitname(SecretCodeUtils.deCode(cvo.getUnitname()));
                cvo.setUnitshortname(SecretCodeUtils.deCode(cvo.getUnitshortname()));
                cvo.setPhone1(SecretCodeUtils.deCode(cvo.getPhone1()));
                cvo.setPhone2(SecretCodeUtils.deCode(cvo.getPhone2()));
            } catch (Exception e) {
                log.error("解密失败！",e);
            }
        }
        return cvo;
    }

    @Override
    public CorpVO[] queryByPks(String[] pk_corp) throws DZFWarpException {
        CorpVO[] cvos=	(CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class,
                "nvl(dr,0)=0 and" + SqlUtil.buildSqlForIn("pk_corp", pk_corp), new SQLParameter());
        if (cvos != null && cvos.length > 0) {
            try {
                for (CorpVO cvo: cvos) {
                    cvo.setUnitname(SecretCodeUtils.deCode(cvo.getUnitname()));
                    cvo.setUnitshortname(SecretCodeUtils.deCode(cvo.getUnitshortname()));
                    cvo.setPhone1(SecretCodeUtils.deCode(cvo.getPhone1()));
                    cvo.setPhone2(SecretCodeUtils.deCode(cvo.getPhone2()));
                }
            } catch (Exception e) {
                log.error("解密失败！",e);
            }
        }
        return cvos;
    }
}

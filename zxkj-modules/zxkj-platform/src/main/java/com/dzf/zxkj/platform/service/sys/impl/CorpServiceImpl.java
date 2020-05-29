package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CorpServiceImpl implements ICorpService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public CorpVO queryByPk(String pk_corp) throws DZFWarpException {
        CorpVO cvo = (CorpVO) singleObjectBO.queryVOByID(pk_corp, CorpVO.class);
        if (cvo != null) {
            try {
                cvo.setUnitname(SecretCodeUtils.deCode(cvo.getUnitname()));
                cvo.setUnitshortname(SecretCodeUtils.deCode(cvo.getUnitshortname()));
                cvo.setPhone1(SecretCodeUtils.deCode(cvo.getPhone1()));
                cvo.setPhone2(SecretCodeUtils.deCode(cvo.getPhone2()));
            } catch (Exception e) {
                log.error("解密失败！", e);
            }
        }
        return cvo;
    }

    @Override
    public CorpVO[] queryByPks(String[] pk_corp) throws DZFWarpException {
        CorpVO[] cvos = (CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class,
                "nvl(dr,0)=0 and" + SqlUtil.buildSqlForIn("pk_corp", pk_corp), new SQLParameter());
        if (cvos != null && cvos.length > 0) {
            try {
                for (CorpVO cvo : cvos) {
                    cvo.setUnitname(SecretCodeUtils.deCode(cvo.getUnitname()));
                    cvo.setUnitshortname(SecretCodeUtils.deCode(cvo.getUnitshortname()));
                    cvo.setPhone1(SecretCodeUtils.deCode(cvo.getPhone1()));
                    cvo.setPhone2(SecretCodeUtils.deCode(cvo.getPhone2()));
                }
            } catch (Exception e) {
                log.error("解密失败！", e);
            }
        }
        return cvos;
    }

    @Override
    public String getDefaultLoginDate(String pk_corp) throws DZFWarpException {

        if(StringUtils.isBlank(pk_corp)){
            return "";
        }

        CorpVO corpVO = queryByPk(pk_corp);
        String beginPeriod = DateUtils.getPeriod(corpVO.getBegindate());
        String date;
        String sql = "select max(period) period from ynt_qmcl where pk_corp = ? and isqjsyjz = 'Y' and nvl(dr,0) = 0";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        String period = (String) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
        if (period != null && period.compareTo(beginPeriod) >= 0) {
            period = DateUtils.getNextPeriod(period);
        } else {
            period = beginPeriod;
        }
        date = DateUtils.getPeriodEndDate(period).toString();
        return date;
    }
}

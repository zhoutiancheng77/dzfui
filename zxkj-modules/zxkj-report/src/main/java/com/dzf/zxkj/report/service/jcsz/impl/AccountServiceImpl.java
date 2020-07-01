package com.dzf.zxkj.report.service.jcsz.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.dao.YntCpaccountDao;
import com.dzf.zxkj.report.service.jcsz.IAccountService;
import com.dzf.zxkj.report.service.jcsz.ICorpService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private YntCpaccountDao yntCpaccountDao;
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private ICorpService corpService;

    @Override
    public Integer getAccountSchema(String pk_corp) {
        CorpVO corpVO = corpService.queryCorpByPk(pk_corp);
        if (corpVO != null) {
            if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
                BdtradeAccountSchemaVO schemaVO = (BdtradeAccountSchemaVO) singleObjectBO.queryVOByID(corpVO.getCorptype(), BdtradeAccountSchemaVO.class);
                if (schemaVO != null) {
                    return schemaVO.getAccountstandard();
                }
            }
        }
        return -1;
    }

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp) throws DZFWarpException {
        return yntCpaccountDao.queryByPkCorp(pk_corp);
    }

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp, int kind) throws DZFWarpException {
        return Arrays.stream(queryByPk(pk_corp)).filter(v -> v.getAccountkind() == kind).toArray(YntCpaccountVO[]::new);
    }

    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) throws DZFWarpException {
        return Arrays.stream(queryByPk(pk_corp)).collect(Collectors.toMap(YntCpaccountVO::getPrimaryKey, v -> v, (k1, k2) -> k1));
    }

    /**
     * 获取一个oldcode数组推出来一个map集合
     *
     * @param oldcode
     * @param oldrule
     * @param newrule
     * @return
     * @throws BusinessException
     */
    @Override
    public String[] getNewCodes(String[] oldCode, String oldRule, String newRule) throws DZFWarpException {

        if (oldCode == null || oldCode.length == 0) {
            throw new BusinessException("转换编码不能为空!");
        }

        if (StringUtils.isAnyBlank(oldRule, newRule)) {
            throw new BusinessException("编码规则不能为空!");
        }

        return Arrays.stream(oldCode).map(v -> getNewCode(v, oldRule, newRule)).toArray(String[]::new);
    }

    @Override
    public Map<String, String> getNewCodeMap(String[] oldCode, String oldRule, String newRule) throws DZFWarpException {
        Map<String, String> resmap = new HashMap<String, String>();
        if (oldCode == null || oldCode.length == 0) {
            throw new BusinessException("转换编码不能为空!");
        }

        if (StringUtils.isAnyBlank(oldRule, newRule)) {
            throw new BusinessException("编码规则不能为空!");
        }
        for (int i = 0; i < oldCode.length; i++) {
            String restemp = getNewCode(oldCode[i], oldRule, newRule);
            resmap.put(oldCode[i], restemp);
        }
        return resmap;
    }

    @Override
    public String queryAccountRule(String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp)) {
            throw new BusinessException("查询科目编码时:公司信息不能为空!");
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<String> kmrulelist = (List<String>) singleObjectBO.executeQuery(
                "select accountcoderule from bd_corp where pk_corp = ?", sp, new ColumnListProcessor());
        if (kmrulelist == null || kmrulelist.size() == 0) {
            return DZFConstant.ACCOUNTCODERULE;
        } else {
            if (StringUtil.isEmpty(kmrulelist.get(0))) {
                return DZFConstant.ACCOUNTCODERULE;
            }
            return kmrulelist.get(0);
        }
    }

    @Override
    public String getNewPartCode(String newcodeRulePart, String oldpartCode) throws DZFWarpException {

        String newPartCode = oldpartCode;
        int newPartLen = Integer.parseInt(newcodeRulePart);
        int oldPartLen = oldpartCode.trim().length();
        if (oldPartLen == newPartLen) {
            return newPartCode;
        }

        for (int i = 0; i < (newPartLen - oldPartLen); i++) {
            newPartCode = "0" + newPartCode;
        }

        return newPartCode;
    }

    private final String SPLIT = "/";

    @Override
    public String getNewCode(String oldcode, String oldrule, String newrule) throws DZFWarpException {
        try {
            String[] odru = oldrule.split(SPLIT);
            String[] newru = newrule.split(SPLIT);

            String newcode = "";
            int startIndex = 0;

            for (int i = 0; i < odru.length; i++) {
                int codelen = new BigInteger(String.valueOf(odru[i])).intValue();
                String oldpartCode = oldcode.substring(startIndex, startIndex + codelen);
                startIndex += codelen;
                String newpartCode = getNewPartCode(newru[i], oldpartCode);
                newcode += newpartCode;
                if (startIndex == oldcode.trim().length()) {
                    break;
                }
            }

            return newcode;
        } catch (Exception e) {
            log.error("获取新的科目编码异常", e);
            throw new BusinessException("获取新的科目编码异常");
        }
    }

    @Override
    public YntCpaccountVO queryById(String id) throws DZFWarpException {
        return (YntCpaccountVO) singleObjectBO.queryVOByID(id, YntCpaccountVO.class);
    }

    public List<YntCpaccountVO> queryVerifyAccount(String pk_corp) {
        return Arrays.stream(queryByPk(pk_corp)).filter(vo -> vo.getIsverification() != null && vo.getIsverification().booleanValue()).collect(Collectors.toList());
    }

    public List<YntCpaccountVO> queryVerifyAccountByCode(String pk_corp, String code) {
        return Arrays.stream(queryByPk(pk_corp)).filter(vo -> vo.getIsverification() != null && vo.getIsverification().booleanValue() && vo.getAccountcode().startsWith(code) && (vo.getIsleaf() == null || !vo.getIsleaf().booleanValue())).collect(Collectors.toList());
    }
}

package com.dzf.zxkj.report.service.jcsz;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;

import java.util.Map;

public interface IAccountService {

    Integer getAccountSchema(String pk_corp);

    YntCpaccountVO[] queryByPk(String pk_corp) throws DZFWarpException;

    YntCpaccountVO[] queryByPk(String pk_corp, int kind) throws DZFWarpException;

    Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) throws DZFWarpException;

    String queryAccountRule(String pk_corp) throws DZFWarpException;

    String getNewCode(String oldcode, String oldrule, String newrule) throws DZFWarpException;

    String getNewPartCode(String newcodeRulePart, String oldpartCode) throws DZFWarpException;

    String[] getNewCodes(String[] oldcode,String oldrule,String newrule) throws DZFWarpException;

    Map<String, String> getNewCodeMap(String[] oldcode, String oldrule, String newrule) throws DZFWarpException;

    YntCpaccountVO queryById(String id) throws DZFWarpException;
}

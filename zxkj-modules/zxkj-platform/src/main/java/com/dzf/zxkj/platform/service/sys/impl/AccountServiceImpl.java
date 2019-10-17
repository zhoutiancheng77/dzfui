package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AccountServiceImpl implements IAccountService {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        YntCpaccountVO[] cpvos = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp);
        if(cpvos != null && cpvos.length > 0){
            Arrays.sort(cpvos, new Comparator<YntCpaccountVO>() {
                @Override
                public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
                    return arg0.getAccountcode().compareTo(arg1.getAccountcode());
                }
            });
        }
        return cpvos == null ? new YntCpaccountVO[0] : cpvos;
    }

    @Override
    public YntCpaccountVO[] queryByPk(String pk_corp, int kind) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(kind);
        YntCpaccountVO[] cpvos = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and accountkind = ? and nvl(dr,0) = 0 ", sp);
        if(cpvos != null && cpvos.length > 0){
            Arrays.sort(cpvos, new Comparator<YntCpaccountVO>() {
                @Override
                public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
                    return arg0.getAccountcode().compareTo(arg1.getAccountcode());
                }
            });
        }
        return cpvos == null ? new YntCpaccountVO[0] : cpvos;
    }

    @Override
    public Map<String, YntCpaccountVO> queryMapByPk(String pk_corp) throws DZFWarpException {
        Map<String, YntCpaccountVO> m = new HashMap<String, YntCpaccountVO>();
        YntCpaccountVO[] yvo = queryByPk(pk_corp);
        int len = yvo == null ? 0 : yvo.length;
        for (int i = 0; i < len; i++) {
            m.put(yvo[i].getPrimaryKey(), yvo[i]);
        }
        return m;
    }

    public List<YntCpaccountVO> queryVerifyAccount (String pk_corp) {
        List<YntCpaccountVO> accountList = new ArrayList<>();
        YntCpaccountVO[] account = queryByPk(pk_corp);
        for (YntCpaccountVO vo : account) {
            if (vo.getIsverification() != null && vo.getIsverification().booleanValue()) {
                accountList.add(vo);
            }
        }
        return accountList;
    }

    public List<YntCpaccountVO> queryVerifyAccountByCode (String pk_corp, String code) {
        List<YntCpaccountVO> accountList = new ArrayList<YntCpaccountVO>();
        YntCpaccountVO[] account = queryByPk(pk_corp);
        for (YntCpaccountVO vo : account) {
            if (vo.getIsverification() != null && vo.getIsverification().booleanValue()
                    && vo.getAccountcode().startsWith(code)
                    && (vo.getIsleaf() == null || !vo.getIsleaf().booleanValue())) {
                accountList.add(vo);
            }
        }
        return accountList;
    }
}

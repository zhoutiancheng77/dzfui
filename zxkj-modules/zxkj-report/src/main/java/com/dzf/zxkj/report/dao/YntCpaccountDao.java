package com.dzf.zxkj.report.dao;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.Comparator;

@Repository
public class YntCpaccountDao {
    @Autowired
    private SingleObjectBO singleObjectBO;

    public YntCpaccountVO[] queryByPkCorp(String pk_corp){
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

}

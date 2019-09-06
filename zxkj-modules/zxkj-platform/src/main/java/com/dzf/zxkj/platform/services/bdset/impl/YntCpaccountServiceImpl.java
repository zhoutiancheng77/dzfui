package com.dzf.zxkj.platform.services.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.services.bdset.IYntCpaccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: dandelion
 * @Date: 2019-09-06
 * @Description:
 */
@Service
public class YntCpaccountServiceImpl implements IYntCpaccountService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public YntCpaccountVO[] get(String pk_corp, int kind) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        sp.addParam(kind);
        YntCpaccountVO[] cpvos = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and accountkind = ? and nvl(dr,0) = 0 ", sp);
        return sort(cpvos);
    }

    private YntCpaccountVO[] sort(YntCpaccountVO[] yntCpaccountVOS){
        if(yntCpaccountVOS == null){
            return new YntCpaccountVO[0];
        }
        Arrays.sort(yntCpaccountVOS, new Comparator<YntCpaccountVO>() {
            @Override
            public int compare(YntCpaccountVO arg0, YntCpaccountVO arg1) {
                return arg0.getAccountcode().compareTo(arg1.getAccountcode());
            }
        });
        return yntCpaccountVOS;
    }

    @Override
    public YntCpaccountVO[] get(String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        YntCpaccountVO[] cpvos = (YntCpaccountVO[])singleObjectBO.queryByCondition(YntCpaccountVO.class, " pk_corp = ? and nvl(dr,0) = 0 ", sp);
        return sort(cpvos);
    }

    @Override
    public Map<String, YntCpaccountVO> getMap(String pk_corp) throws DZFWarpException {
        Map<String, YntCpaccountVO> m = new HashMap<String, YntCpaccountVO>();
        YntCpaccountVO[] yvo = get(pk_corp);
        int len = yvo == null ? 0 : yvo.length;
        for (int i = 0; i < len; i++) {
            m.put(yvo[i].getPrimaryKey(), yvo[i]);
        }
        return m;
    }
}

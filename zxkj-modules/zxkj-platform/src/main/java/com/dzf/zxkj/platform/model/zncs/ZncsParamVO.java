package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.HashMap;
import java.util.Map;

public class ZncsParamVO {
    private CorpVO corpvo;
    private Map<String, YntCpaccountVO> accountMap;
    private YntCpaccountVO[] accVOs;
    private Map<String,Map<String, Object>> paramMap = new HashMap<String,Map<String, Object>>();

    public Map<String, Map<String, Object>> getParamMap() {
        return paramMap;
    }

//    public void setParamMap(Map<String, Map<String, Object>> paramMap) {
//        this.paramMap = paramMap;
//    }

    public CorpVO getCorpvo() {
        return corpvo;
    }

    public void setCorpvo(CorpVO corpvo) {
        this.corpvo = corpvo;
    }

    public Map<String, YntCpaccountVO> getAccountMap() {
        return accountMap;
    }

    public void setAccountMap(Map<String, YntCpaccountVO> accountMap) {
        this.accountMap = accountMap;
    }

    public YntCpaccountVO[] getAccVOs() {
        return accVOs;
    }

    public void setAccVOs(YntCpaccountVO[] accVOs) {
        this.accVOs = accVOs;
    }
}

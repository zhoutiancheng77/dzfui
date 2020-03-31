package com.dzf.zxkj.platform.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class KmzzUtil {
    public static List<KmZzVO> filterQC(KmZzVO[] kmmxvos) {
        HashMap<String, DZFBoolean> mapshow = new HashMap<String, DZFBoolean>();
        for (KmZzVO mxzvo : kmmxvos) {
            mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.FALSE);
        }
        List<KmZzVO> listmx = new ArrayList<KmZzVO>();
        for (KmZzVO mxzvo : kmmxvos) {
            if (mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy())) {
                continue;
            }

            listmx.add(mxzvo);
            if (!mapshow.get(mxzvo.getPk_accsubj()).booleanValue() && "期初余额".equals(mxzvo.getZy())) {
                mapshow.put(mxzvo.getPk_accsubj(), DZFBoolean.TRUE);
            }
        }
        return listmx;
    }
}

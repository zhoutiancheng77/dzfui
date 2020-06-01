package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public class CorpUtil {


    public static CorpVO getCorpvo (CorpVO cvo) {
        // 字段解密
        if (cvo != null) {
            cvo.setUnitname(CodeUtils1.deCode(cvo.getUnitname()));
            cvo.setUnitshortname(CodeUtils1.deCode(cvo.getUnitshortname()));
            cvo.setPhone1(CodeUtils1.deCode(cvo.getPhone1()));
            cvo.setPhone2(CodeUtils1.deCode(cvo.getPhone2()));
        }
        return cvo;
    }
}

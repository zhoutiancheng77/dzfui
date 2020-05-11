package com.dzf.zxkj.platform.service.batchprint.impl.util;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;

public class DefaultBatchPrintSetFactory {


    /**
     * 获取默认的打印设置
     * @param type
     * @return
     */
    public static BatchPrintFileSetVo getSetvo (String type) {
        BatchPrintFileSetVo setvo = new BatchPrintFileSetVo();

        switch (type) {
            case "month":
                setvo.setVprintname("gzb,crk,cbft,zjmx,fsye");
                setvo.setKmpage("");
                break;
            case "year":
                setvo.setVprintname("kmzz,kmmx,xjrj,lrb,zcfz,mly,mxzfp");
                setvo.setKmpage("kmzz");
                break;
        }
        setvo.setVmobelsel("A4");
        setvo.setReviewdir("zx");
        setvo.setDleftmargin(new DZFDouble(5));
        setvo.setDtopmargin(new DZFDouble(5));
        setvo.setVfontsize(new DZFDouble(9));
        setvo.setDprintdate(new DZFDate());
        return setvo;
    }

}

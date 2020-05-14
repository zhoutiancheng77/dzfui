package com.dzf.zxkj.platform.service.batchprint.impl.util;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;

public class SetCovertTask {

    /**
     * 打印设置转换成打印任务
     * @return
     */
    public static BatchPrintSetVo convertTask (BatchPrintFileSetVo setvo,String pk_corp,String period,String cuserid) {
        BatchPrintSetVo taskvo = new BatchPrintSetVo();
        taskvo.setPk_corp(pk_corp);
        taskvo.setVprintperiod(period);
        taskvo.setVprintname(PrintNodeMap.getVprintName(setvo.getVprintname()));
        taskvo.setDoperadatetime(new DZFDateTime());
        taskvo.setVoperateid(cuserid);
        taskvo.setVmobelsel(setvo.getVmobelsel());
        taskvo.setReviewdir(setvo.getReviewdir());// 横纵向
        taskvo.setVfontsize(setvo.getVfontsize());
        taskvo.setDprintdate(setvo.getDprintdate());
        taskvo.setDleftmargin(setvo.getDleftmargin());
        taskvo.setDtopmargin(setvo.getDtopmargin());
        taskvo.setVprintcode(setvo.getVprintname());
        taskvo.setSetselect(setvo.getSetselect());
        taskvo.setKmpage(setvo.getKmpage());
        return taskvo;
    }


}

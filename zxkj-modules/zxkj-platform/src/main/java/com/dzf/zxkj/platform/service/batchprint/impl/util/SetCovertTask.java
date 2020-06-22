package com.dzf.zxkj.platform.service.batchprint.impl.util;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintFileSetVo;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;

public class SetCovertTask {

    /**
     * 打印设置转换成打印任务
     * @return
     */
    public static BatchPrintSetVo convertTask (BatchPrintFileSetVo setvo,String pk_corp,String period,
                                               String cuserid,String vprintdate, String bsysdate) {
        BatchPrintSetVo taskvo = new BatchPrintSetVo();
        taskvo.setPk_corp(pk_corp);
        taskvo.setVprintperiod(period);
        taskvo.setVprintname(PrintNodeMap.getVprintName(setvo.getVprintname()));
        taskvo.setDoperadatetime(new DZFDateTime());
        taskvo.setVoperateid(cuserid);
        taskvo.setVmobelsel(setvo.getVmobelsel());
        taskvo.setReviewdir(setvo.getReviewdir());// 横纵向
        taskvo.setVfontsize(setvo.getVfontsize());
        if ("Y".equals(bsysdate)) {
            taskvo.setDprintdate(new DZFDate());
        } else {
            if (!StringUtil.isEmpty(vprintdate)) {
                taskvo.setDprintdate(new DZFDate(vprintdate));
            }
        }
        taskvo.setDleftmargin(setvo.getDleftmargin());
        taskvo.setDtopmargin(setvo.getDtopmargin());
        taskvo.setVprintcode(setvo.getVprintname());
        taskvo.setSetselect(setvo.getSetselect());
        taskvo.setKmpage(setvo.getKmpage());
        return taskvo;
    }

    /**
     * 打印设置转换成打印任务
     * @return
     */
    public static BatchPrintSetVo convertTaskFormPldy (BatchPrintSetVo printSetVo,String pk_corp,String period,
                                               String cuserid,String vprintdate, String bsysdate) {
        BatchPrintSetVo taskvo = new BatchPrintSetVo();
        taskvo.setPk_corp(pk_corp);
        taskvo.setVprintperiod(period);
        taskvo.setVprintname(PrintNodeMap.getVprintName(printSetVo.getVprintcode()));
        taskvo.setDoperadatetime(new DZFDateTime());
        taskvo.setVoperateid(cuserid);
        taskvo.setVmobelsel("A4");
        taskvo.setReviewdir("zx");// 横纵向
        taskvo.setVfontsize(printSetVo.getVfontsize());
        if ("Y".equals(bsysdate)) {
            taskvo.setDprintdate(new DZFDate());
        } else {
            if (!StringUtil.isEmpty(vprintdate)) {
                taskvo.setDprintdate(new DZFDate(vprintdate));
            }
        }
        taskvo.setDleftmargin(printSetVo.getDleftmargin());
        taskvo.setDtopmargin(printSetVo.getDtopmargin());
        taskvo.setVprintcode(printSetVo.getVprintcode());
        taskvo.setSetselect("month");
        return taskvo;
    }


}

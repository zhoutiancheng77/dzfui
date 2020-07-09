package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.cwzb.XjyhrjzController;
import com.dzf.zxkj.report.service.cwzb.IXjRjZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class XjrjPrint extends  AbstractPrint {


    private IXjRjZReport gl_rep_xjyhrjzserv;

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;


    public XjrjPrint(IXjRjZReport gl_rep_xjyhrjzserv, IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.gl_rep_xjyhrjzserv = gl_rep_xjyhrjzserv;
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print(BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
            XjyhrjzController xjyhrjzController = new XjyhrjzController();
            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            printReporUtil.setIscross(new DZFBoolean(pmap.get("pageOrt")));

            /** 声明一个map用来存前台传来的设置参数 */
            String pk_currency = queryparamvo.getPk_currency();
            Map<String, String> tmap = new LinkedHashMap<>();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间",printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            KmMxZVO[] bodyvos = gl_rep_xjyhrjzserv.getXJRJZVOsConMo(queryparamvo.getPk_corp(),
                    queryparamvo.getKms_first(),queryparamvo.getKms_last(),  queryparamvo.getBegindate1(), queryparamvo.getEnddate(),
                    queryparamvo.getXswyewfs(),queryparamvo.getXsyljfs(),
                    queryparamvo.getIshasjz(), queryparamvo.getIshassh(), queryparamvo.getPk_currency(), null,null);//默认人民币
            if (bodyvos == null || bodyvos.length ==0) {
                return null;
            }
            xjyhrjzController.putKmRq(bodyvos);
            printReporUtil.setLineheight(22f);
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            for (int i = 0; i < bodyvos.length; i++) {
                bodyvos[i].km=bodyvos[i].km.trim();
            }
            Object[] obj = xjyhrjzController.getPrintXm(0,"false");
            printReporUtil.printHz(new HashMap<String, List<SuperVO>>(),bodyvos,"现金/银行日记账",(String[])obj[0],
                    (String[])obj[1], (int[])obj[2],(int)obj[3],pmap,tmap);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("打印错误",e);
        } catch (IOException e) {
            log.error("打印错误",e);
        }
        return null;
    }
}

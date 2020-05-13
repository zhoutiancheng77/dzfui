package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.cwzb.KmzzController;
import com.dzf.zxkj.report.service.cwzb.IKMZZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class KmzzPrint extends  AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private IKMZZReport gl_rep_kmzjserv;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;


    public KmzzPrint(IZxkjPlatformService zxkjPlatformService,IKMZZReport gl_rep_kmzjserv, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.gl_rep_kmzjserv = gl_rep_kmzjserv;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print(BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO){
        try {
            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);
            KmzzController kmzzController = new KmzzController();
            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE);
            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);
            String pageOrt = pmap.get("pageOrt");
            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            String type = pmap.get("type");
            if (pageOrt.equals("Y")) {
                printReporUtil.setIscross(DZFBoolean.TRUE);//是否横向
            } else {
                printReporUtil.setIscross(DZFBoolean.FALSE);//是否横向
            }
            queryparamvo.setBtotalyear(DZFBoolean.TRUE);
            queryparamvo.setIsnomonthfs(DZFBoolean.TRUE);
            KmZzVO[] bodyvos =kmzzController.reloadNewValue(printParamVO.getTitleperiod(), printParamVO.getCorpName(),
                    printParamVO.getIsPaging(), queryparamvo,gl_rep_kmzjserv);
            if (bodyvos == null || bodyvos.length ==0) {
                return null;
            }
            ReportUtil.updateKFx(bodyvos);
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数

            if (bodyvos != null && bodyvos.length > 0) {
                tmap.put("公司", printParamVO.getCorpName());
                tmap.put("期间", printParamVO.getTitleperiod());
                tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            }
            printReporUtil.setLineheight(StringUtil.isEmpty(lineHeight) ? 22f : Float.parseFloat(lineHeight));
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            // 具体打印配置
            kmzzController.printAction(printParamVO, queryparamvo, printReporUtil, pmap, bodyvos, tmap);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("错误", e);
        } catch (IOException e) {
            log.error("错误", e);
        }
        return null;
    }
}

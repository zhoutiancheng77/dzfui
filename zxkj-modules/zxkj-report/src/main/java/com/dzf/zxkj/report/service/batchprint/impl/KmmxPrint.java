package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.report.KmMxZVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.controller.cwzb.KmMxrController;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.utils.ReportUtil;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class KmmxPrint extends  AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private IKMMXZReport gl_rep_kmmxjserv;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public KmmxPrint(IZxkjPlatformService zxkjPlatformService, IKMMXZReport gl_rep_kmmxjserv, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.gl_rep_kmmxjserv = gl_rep_kmmxjserv;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    /**
     * 科目明细账
     */
    public byte[] print(BatchPrintSetVo setVo,CorpVO corpVO, UserVO userVO) {
        try {

            KmMxrController kmMxrController = new KmMxrController();

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);

            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE); // 保存到文件服务器

            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);

            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            printReporUtil.setIscross(new DZFBoolean(pmap.get("pageOrt")));

            KmMxZVO[] bodyvos = kmMxrController.reloadNewValue(printParamVO.getTitleperiod(), printParamVO.getCorpName(),
                    printParamVO.getIsPaging(), queryparamvo,gl_rep_kmmxjserv,zxkjPlatformService);

            if (bodyvos == null || bodyvos.length ==0) {
                return null;
            }
            ReportUtil.updateKFx(bodyvos);
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数
            String km = bodyvos[0].getKm();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            printReporUtil.setLineheight(StringUtil.isEmpty(lineHeight) ? 22f : Float.parseFloat(lineHeight));
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            Object[] obj = null;
            // 打印设置(列设置)
            printParamVO.setIsPaging("N"); // 是否分页
            if (!StringUtil.isEmpty(setVo.getKmpage())) {
                if (setVo.getKmpage().indexOf("kmmx") > 0) {
                    printParamVO.setIsPaging("Y"); // 是否分页
                    if (setVo.getVprintcode().indexOf("mly") > 0) {
                        printReporUtil.setBmly(DZFBoolean.TRUE);
                    }
                }
            }
            kmMxrController.printAction(printParamVO, queryparamvo, printReporUtil, pmap, bodyvos, tmap,zxkjPlatformService);
            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
        return null;
    }
}

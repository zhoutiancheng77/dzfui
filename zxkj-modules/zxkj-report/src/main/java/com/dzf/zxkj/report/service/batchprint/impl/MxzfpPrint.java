package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.pdf.ReportCoverPrintUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.itextpdf.text.Document;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Slf4j
public class MxzfpPrint extends  AbstractPrint {

    private IZxkjPlatformService zxkjPlatformService;

    private PrintParamVO printParamVO;

    private KmReoprtQueryParamVO queryparamvo;

    public MxzfpPrint(IZxkjPlatformService zxkjPlatformService, PrintParamVO printParamVO, KmReoprtQueryParamVO queryparamvo) {
        this.zxkjPlatformService = zxkjPlatformService;
        this.printParamVO = printParamVO;
        this.queryparamvo = queryparamvo;
    }

    public byte[] print (BatchPrintSetVo setVo,CorpVO corpVO , UserVO userVO) {
        Rectangle pageSize = PageSize.A4;
        if ("2".equals(printParamVO.getType())) {
            pageSize = PageSize.B5;
        } else {
            if ("Y".equals(printParamVO.getPageOrt())) {//横向
                pageSize = new Rectangle(pageSize.getHeight(), pageSize.getWidth());
            }
        }
        String page_num = "1";
        float leftsize = Float.parseFloat(printParamVO.getLeft()) * 2.83f;
        float topsize = Float.parseFloat(printParamVO.getTop()) * 2.83f;
        Document document = new Document(pageSize, leftsize, 0, topsize, 4);
        ByteArrayOutputStream buffer = null;
        try {
            String cids = corpVO.getPk_corp();
            // 校验
            buffer = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, buffer);
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            ReportCoverPrintUtil printutil = new ReportCoverPrintUtil(zxkjPlatformService);
            // 赋值首字符的值
            printutil.kmCoverPrint(leftsize, topsize, document, canvas, cids.split(","), page_num, "");
        } catch (Exception e) {
            log.error("错误", e);
        } finally {
            document.close();
        }
        try {
            buffer.flush();// flush 放在finally的时候流关闭失败报错
            return buffer.toByteArray();
        } catch (IOException e) {

        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }
}

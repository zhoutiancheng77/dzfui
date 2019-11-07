package com.dzf.zxkj.controller;

import com.dzf.zxkj.common.base.BaseController;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.excel.util.Excelexport2003;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

@Slf4j
public class PrintAndExcelExportController extends BaseController {
    public void baseExcelExport(HttpServletResponse response, Excelexport2003 lxs, IExceport yhd){
        OutputStream toClient = null;
        try {
            response.reset();
            String filename = yhd.getExcelport2003Name();
            String formattedName = URLEncoder.encode(filename, "UTF-8");
            response.addHeader("Content-Disposition",
                    "attachment;filename=" + filename + ";filename*=UTF-8''" + formattedName);
            toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/vnd.ms-excel;charset=gb2312");
            lxs.exportExcel(yhd, toClient);
            toClient.flush();
            response.getOutputStream().flush();

        } catch (IOException e) {
            log.error("excel导出错误", e);
        } finally {
            try {
                if (toClient != null) {
                    toClient.close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
            try {
                if (response != null && response.getOutputStream() != null) {
                    response.getOutputStream().close();
                }
            } catch (IOException e) {
                log.error("excel导出错误", e);
            }
        }
    }

}

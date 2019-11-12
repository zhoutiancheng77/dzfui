package com.dzf.zxkj.base.controller;

import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.excel.util.Excelexport2003;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Workbook;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    /**
     * 压缩包导出
     * @param response
     * @param workBookMap
     * @param zipFileName
     * @return
     */
    public boolean exportExcelToZip(HttpServletResponse response, Map<String, Workbook> workBookMap, String
            zipFileName) {
        if (workBookMap == null || workBookMap.isEmpty()) {
            return false;
        }
        OutputStream out = null;
        ZipOutputStream zos = null;
        try {
            Set keys = workBookMap.keySet();
            if (keys != null) {
                // 清空输出流
                response.resetBuffer();
                // 设置reponse返回数据类型，文件名
                String fileName = zipFileName + ".zip";
                //String fileName = URLEncoder.encode(fileName, "UTF-8");

                response.setContentType("application/zip; charset=utf-8");
                response.setHeader("Connection", "close"); // 表示不能用浏览器直接打开
                response.setHeader("Accept-Ranges", "bytes");// 告诉客户端允许断点续传多线程连接下载
                // llh 解决文件名乱码问题
                // 1、filename=<fileName>：是给IE、Chrome用的，其中<fileName>可以用URLEncoded的名称，也可以不转码直接用中文原文，浏览器能识别中文文件名。
                // 2、filename*=UTF-8''<encodedFileName>：是给FireFox用的。其中<encodedFileName>为URLEncoded的名称，并需在文件名前面加上前缀UTF-8''，如：filename*=UTF-8''%e8%b5%84%e4%ba%a7%e8%b4%9f%e5%80%ba%e8%a1%a8.xls
                response.addHeader("Content-Disposition", "attachment;filename=" + fileName + ";filename*=UTF-8''" + URLEncoder.encode(fileName, "UTF-8"));
                //response.setHeader("Content-Disposition", "attachment;filename=" + new String(fileName.getBytes("GB2312"), "ISO8859-1"));

                // 取得输出流
                out = response.getOutputStream();
                // 压缩输出流
                zos = new ZipOutputStream(out);
                // 获取所有Excel名字
                Iterator<String> it = keys.iterator();
                while (it.hasNext()) {
                    String name = it.next();
                    Workbook book = workBookMap.get(name);
                    if (book != null) {
                        // 创建一个压缩对象，并赋上文件名
                        ZipEntry entry = new ZipEntry(name + ".xls");
                        // 将一个要压缩的对象放进压缩流中
                        zos.putNextEntry(entry);

                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
                        book.write(bos);
                        bos.writeTo(zos);
                        zos.closeEntry();
                    }
                }
            }
        } catch (IOException e) {
            return false;
        } finally {
            try {
                // 关闭顺序必须是zos在前面，否则在用wrar解压时报错：“不可预料的压缩文件末端是什么原因”
                if (zos != null) {
                    zos.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {

            }
        }
        return true;
    }

}

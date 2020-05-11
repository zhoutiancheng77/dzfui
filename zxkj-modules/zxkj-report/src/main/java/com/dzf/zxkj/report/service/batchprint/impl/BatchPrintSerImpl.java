package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.batchprint.PrintStatusEnum;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.batchprint.IBatchPrintSer;
import com.dzf.zxkj.report.service.batchprint.LrbPrint;
import com.dzf.zxkj.report.service.cwbb.ILrbReport;
import com.dzf.zxkj.report.service.cwbb.IZcFzBReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.service.cwzb.IKMMXZReport;
import com.dzf.zxkj.report.service.cwzb.IKMZZReport;
import com.dzf.zxkj.report.service.cwzb.IXjRjZReport;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.HashMap;
import java.util.UUID;

@Service("gl_rep_batchprinterv")
@Slf4j
public class BatchPrintSerImpl implements IBatchPrintSer {

    @Autowired
    private ILrbReport gl_rep_lrbserv;
    @Autowired
    private IZcFzBReport gl_rep_zcfzserv;
    @Autowired
    private IFsYeReport gl_rep_fsyebserv;
    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;
    @Autowired
    private IKMZZReport gl_rep_kmzjserv;
    @Autowired
    private  IXjRjZReport gl_rep_xjyhrjzserv;
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Override
    public void print(BatchPrintSetVo[] setvos,UserVO userVO) throws DZFWarpException {
        if (setvos != null && setvos.length > 0) {
            //byte[] 字节生成pdf文件
            FastDfsUtil util = (FastDfsUtil) SpringUtils.getBean("connectionPool");
            OutputStream bOut = null;
            InputStream in = null;
            try {
                for (BatchPrintSetVo setvo: setvos) {
                    // 想着走Action 打印方法
                    String printcode = setvo.getVprintcode();
                    CorpVO corpVO = zxkjPlatformService.queryCorpByPk(setvo.getPk_corp());
                    // 打印参数设定
                    PrintParamVO printParamVO = getPrintParamVO(setvo);
                    // 查询参数设定
                    KmReoprtQueryParamVO queryparamvo = getKmReoprtQueryParamVO(setvo, corpVO);
                    // 合并数据
                    PDFMergerUtility mergePdf = new PDFMergerUtility();
                    if (!StringUtil.isEmpty(printcode)) {
                        String[] printcodes = printcode.split(",");
                        // 涉及异常的处理，目前先不处理，生成的文件，放在文件服务器中
                        bOut = new ByteArrayOutputStream();
                        String filename = UUID.randomUUID().toString();
                        for (String code: printcodes) {
                            // 代码来源是每个action对应的Print方法
                            if ("kmmx".equals(code)) { // 科目明细账
                                KmmxPrint kmmxprint = new KmmxPrint(zxkjPlatformService,gl_rep_kmmxjserv,printParamVO,queryparamvo);
                                byte[] byts = kmmxprint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("科目明细账执行完毕!");
                            } else if ("kmzz".equals(code)) {
                                KmzzPrint kmzzPrint = new KmzzPrint(zxkjPlatformService,gl_rep_kmzjserv,printParamVO,queryparamvo);
                                byte[] byts = kmzzPrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("科目总账执行完毕!");
                            } else if ("xjrj".equals(code)) {
                                XjrjPrint xjrjPrint = new XjrjPrint(gl_rep_xjyhrjzserv,zxkjPlatformService,printParamVO,queryparamvo);
                                byte[] byts = xjrjPrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("现金/银行日记账执行完毕!");
                            } else if ("fsye".equals(code)) {
                                FsyePrint fsyePrint = new FsyePrint(gl_rep_fsyebserv,zxkjPlatformService,printParamVO,queryparamvo);
                                byte[] byts = fsyePrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("发生额及余额表执行完毕!");
                            } else if ("zcfz".equals(code)) {
                                ZcfzPrint zcfzPrint = new ZcfzPrint(zxkjPlatformService,printParamVO,queryparamvo,gl_rep_zcfzserv);
                                byte[] byts = zcfzPrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("资产负债执行完毕!");
                            } else if ("lrb".equals(code)) {
                                LrbPrint lrbPrint = new LrbPrint(gl_rep_lrbserv,zxkjPlatformService,printParamVO,queryparamvo);
                                byte[] byts = lrbPrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("利润表执行完毕!");
                            } else if ("gzb".equals(code)) {
                                GzbPrint gzbPrint = new GzbPrint(zxkjPlatformService,printParamVO,queryparamvo);
                                byte[] byts = gzbPrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("工资表执行完毕!");
                            } else if ("crk".equals(code)) {
//                                CkPrint ckPrint = new CkPrint(zxkjPlatformService,printParamVO,queryparamvo);
//                                byte[] byts = ckPrint.print(corpVO,userVO);
//                                if (byts!=null && byts.length > 0) {
//                                    in = new ByteArrayInputStream(byts);
//                                    mergePdf.addSource(in);
//                                }
//                                log.info("出库表执行完毕!");
//
//
//                                RkPrint rkPrint = new RkPrint(zxkjPlatformService,printParamVO, queryparamvo);
//                                byte[] byts2 = rkPrint.print(corpVO,userVO);
//                                if (byts2!=null && byts2.length > 0) {
//                                    in = new ByteArrayInputStream(byts2);
//                                    mergePdf.addSource(in);
//                                }
//                                log.info("入库表执行完毕!");
                            } else if ("zjmx".equals(code)) {
                                ZjmxPrint zjmxPrint = new ZjmxPrint(zxkjPlatformService, printParamVO, queryparamvo);
                                byte[] byts = zjmxPrint.print(corpVO,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("折旧明细执行完毕!");
                            }
                        }
                        mergePdf.setDestinationStream(bOut);
                        //合并pdf
                        mergePdf.mergeDocuments(null);
                        byte[] resbytes = ((ByteArrayOutputStream) bOut).toByteArray();
                        if (resbytes != null && resbytes.length > 0){
                            String id = util.upload(resbytes, filename+"kmmx.pdf", new HashMap<String,String>());
                            setvo.setVfilepath(id);
                            setvo.setVfilename(filename+"kmmx.pdf");
                            setvo.setVmemo("文件已生成!");
                        } else{
                            setvo.setVmemo("无数据");
                        }
                        setvo.setIfilestatue(PrintStatusEnum.GENERATE.getCode());
                        setvo.setDgendatetime(new DZFDateTime());
                        singleObjectBO.update(setvo);
                    }
                }
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            } finally {
                try {
                    if (bOut != null) {
                        bOut.close();
                    }
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }
    }



    private String getModelType (String name) {
        if ("A4".equals(name)) {
            return "1";
        } else if ("B5".equals(name)) {
            return "2";
        } else if ("A5".equals(name)) {
            return "4";
        }
        return "";
    }

    private String getDirCode (String name){
        if ("hx".equals(name)) {
            return "Y";
        } else if ("zx".equals(name)) {
            return "N";
        }
        return "";
    }

    private KmReoprtQueryParamVO getKmReoprtQueryParamVO(BatchPrintSetVo setvo, CorpVO corpVO) {
        KmReoprtQueryParamVO queryparamvo = new KmReoprtQueryParamVO();
        queryparamvo.setPk_corp(corpVO.getPk_corp());
        queryparamvo.setBegindate1(DateUtils.getPeriodStartDate(setvo.getVprintperiod().split("~")[0]) );
        queryparamvo.setEnddate(DateUtils.getPeriodEndDate(setvo.getVprintperiod().split("~")[0]));
        queryparamvo.setIshasjz(DZFBoolean.FALSE);
        queryparamvo.setXswyewfs(DZFBoolean.FALSE);
        queryparamvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
        queryparamvo.setCjq(1);
        queryparamvo.setCjz(6);
        return queryparamvo;
    }

    private PrintParamVO getPrintParamVO(BatchPrintSetVo setvo) {
        PrintParamVO printParamVO = new PrintParamVO();
        printParamVO.setType(getModelType(setvo.getVmobelsel()));
        printParamVO.setFont(setvo.getVfontsize().intValue() + "");
        printParamVO.setLeft(setvo.getDleftmargin().intValue() + "");
        printParamVO.setTop(setvo.getDtopmargin().intValue() + "");
        printParamVO.setPrintdate(setvo.getVprintperiod());// 打印日期
        printParamVO.setIsPaging("kmmx".equals(setvo.getKmpage()) ? "Y": "N"); // 是否分页
        printParamVO.setPageOrt(getDirCode(setvo.getReviewdir()));
        return printParamVO;
    }
}

package com.dzf.zxkj.report.service.batchprint.impl;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.batchprint.PrintStatusEnum;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.batchprint.IBatchPrintSer;
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
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
    public void batchexectask (BatchPrintSetVo[] setvos,UserVO userVO) {
        if (setvos != null && setvos.length > 0) {
            ExecutorService pool = null;
            try {
                int maxcount = 100;

                if (setvos.length <= maxcount) {
                    maxcount = setvos.length;
                }
                pool = Executors.newFixedThreadPool(maxcount);

                List<Future<String>> vc = new Vector<Future<String>>();

                for (BatchPrintSetVo setvo : setvos) {
                    Future<String> future = pool.submit(new ExecTask(setvo,userVO));

                    vc.add(future);
                }

                for (Future<String> fu : vc) {
                    fu.get();
                }
                pool.shutdown();
            } catch (Exception e) {
                log.error("错误",e);
            } finally {
                try {
                    if (pool != null) {
                        pool.shutdown();
                    }
                } catch (Exception e) {
                    log.error("错误",e);
                }

            }
        }
    }

    private class ExecTask implements Callable<String> {

        private BatchPrintSetVo setvo = null;

        private UserVO userVO =null;

        public ExecTask(BatchPrintSetVo setvo, UserVO userVO) {
            this.setvo = setvo;
            this.userVO = userVO;
        }

        @Override
        public String call() throws Exception {
            try {
               print(setvo,userVO);
            } catch (Exception e) {
            } finally {
            }
            return "end";
        }
    }

    public void print(BatchPrintSetVo setvo,UserVO userVO) throws DZFWarpException {
        //byte[] 字节生成pdf文件
        FastDfsUtil util = (FastDfsUtil) SpringUtils.getBean("connectionPool");
        CorpVO corpVO = zxkjPlatformService.queryCorpByPk(setvo.getPk_corp());
        // 打印参数设定
        PrintParamVO printParamVO = getPrintParamVO(setvo,corpVO,"");
        // 查询参数设定
        KmReoprtQueryParamVO queryparamvo = getKmReoprtQueryParamVO(setvo, corpVO);
        InputStream in = null;
        try {
            // 想着走Action 打印方法
            String printcode = setvo.getVprintcode();
            // 合并数据
            PDFMergerUtility mergePdf = new PDFMergerUtility(); // 纵向
            PDFMergerUtility mergePdfHx = new PDFMergerUtility();// 横向
            PDFMergerUtility mergePdfPz = new PDFMergerUtility(); // 凭证
            byte[] resbytes = null;
            byte[] resbytesHx = null;
            byte[] resbytesPz = null;
            SimpleDateFormat f=new SimpleDateFormat("yyyy年MM月dd日HH点mm分ss秒");
            String str_time = f.format(new Date());
            String filename =corpVO.getUnitname()+"("+str_time+")";
            if (!StringUtil.isEmpty(printcode)) {
                String[] printcodes = printcode.split(",");
                // 涉及异常的处理，目前先不处理，生成的文件，放在文件服务器中
                for (String code: printcodes) {
                    // 代码来源是每个action对应的Print方法
                    AbstractPrint print = null;
                    if ("kmmx".equals(code)) { // 科目明细账
                        print = new KmmxPrint(zxkjPlatformService,gl_rep_kmmxjserv,printParamVO,queryparamvo);
                    } else if ("kmzz".equals(code)) {
                        print= new KmzzPrint(zxkjPlatformService,gl_rep_kmzjserv,printParamVO,queryparamvo);
                    } else if ("xjrj".equals(code)) {
                        print = new XjrjPrint(gl_rep_xjyhrjzserv,zxkjPlatformService,printParamVO,queryparamvo);
                    } else if ("fsye".equals(code)) {
                        print = new FsyePrint(gl_rep_fsyebserv,zxkjPlatformService,printParamVO,queryparamvo);
                    } else if ("zcfz".equals(code)) {
                        print = new ZcfzPrint(zxkjPlatformService,printParamVO,queryparamvo,gl_rep_zcfzserv);
                    } else if ("lrb".equals(code)) {
                        print = new LrbPrint(gl_rep_lrbserv,zxkjPlatformService,printParamVO,queryparamvo);
                    } else if ("gzb".equals(code)) {
                        print = new GzbPrint(zxkjPlatformService,printParamVO,queryparamvo);
                    } else if ("crk".equals(code)) {
                        print = new CkPrint(zxkjPlatformService,printParamVO,queryparamvo);
                        // 先出库
                        in = mergeByte(setvo, userVO, in, corpVO, printParamVO, mergePdf, mergePdfHx, mergePdfPz, code, print);
                        print = new RkPrint(zxkjPlatformService,printParamVO, queryparamvo);
                    } else if ("zjmx".equals(code)) {
                        print = new ZjmxPrint(zxkjPlatformService, printParamVO, queryparamvo);
                    } else if ("mxzfp".equals(code)) {
                        print = new MxzfpPrint(zxkjPlatformService,printParamVO, queryparamvo);
                    } else if ("pzfp".equals(code)) {
                        print = new PzFpPrint(zxkjPlatformService,printParamVO, queryparamvo);
                    } else if ("voucher".equals(code)) {
                        print = new VoucherPrint(zxkjPlatformService,printParamVO, queryparamvo);
                    }
                    // 合并数据
                    if (print != null) {
                        in = mergeByte(setvo, userVO, in, corpVO, printParamVO, mergePdf, mergePdfHx, mergePdfPz, code, print);
                    }
                    log.info(code+"执行完毕!");
                }
                resbytes = getPdfByte(mergePdf);
                resbytesHx = getPdfByte(mergePdfHx);
                resbytesPz = getPdfByte(mergePdfPz);
            }
            if ((resbytes != null && resbytes.length > 0) ||
                    (resbytesHx != null && resbytesHx.length > 0)||
                    (resbytesPz != null && resbytesPz.length > 0)){
                List<byte[]> listbytes = new ArrayList<byte[]>();
                List<String> listname = new ArrayList<String>();
                if (resbytes != null && resbytes.length > 0) {
                    listbytes.add(resbytes);
                    listname.add("hor.pdf");
                }
                if (resbytesHx != null && resbytesHx.length > 0) {
                    listbytes.add(resbytesHx);
                    listname.add("por.pdf");
                }
                if (resbytesPz != null && resbytesPz.length > 0) {
                    listbytes.add(resbytesPz);
                    listname.add("voucher.pdf");
                }
                resbytes = zip(listbytes, listname);
                String id = util.upload(resbytes, filename+".zip", new HashMap<String,String>());
                setvo.setVfilepath(id);
                setvo.setVfilename(filename+".zip");
                setvo.setVmemo("文件已生成!");
            } else{
                setvo.setVmemo("无数据");
            }
            setvo.setIfilestatue(PrintStatusEnum.GENERATE.getCode());
            setvo.setDgendatetime(new DZFDateTime());
            singleObjectBO.update(setvo);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    private byte[] getPdfByte(PDFMergerUtility mergePdf) {
        OutputStream bOut = null;
        try {
            bOut = new ByteArrayOutputStream();
            mergePdf.setDestinationStream(bOut);
            //合并pdf
            mergePdf.mergeDocuments(null);
            return ((ByteArrayOutputStream) bOut).toByteArray();
        }catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            try {
                if (bOut != null) {
                    bOut.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(),e);
            }
        }
        return null;
    }

    private InputStream mergeByte(BatchPrintSetVo setvo, UserVO userVO, InputStream in, CorpVO corpVO, PrintParamVO printParamVO,
                                  PDFMergerUtility mergePdf, PDFMergerUtility mergePdfHx,
                                  PDFMergerUtility mergePdfPz, String code, AbstractPrint print) {
        String[] pzpages = new String[]{"pzfp","voucher"};// 凭证分组
        String[] zxpages = new String[]{"zcfz","lrb"};// 纵向打印分组
        String[] hxpages = new String[]{"fsye","gzb"};// 横向分组
        byte[] byts = print.print(setvo,corpVO,userVO);
        if (byts!=null && byts.length > 0) {
            in = new ByteArrayInputStream(byts);
            if (Arrays.asList(hxpages).contains(code)) {
                mergePdfHx.addSource(in);
            } else if (Arrays.asList( zxpages).contains(code)) {
                mergePdf.addSource(in);
            } else if (Arrays.asList(pzpages).contains(code)) {
                mergePdfPz.addSource(in);
            } else {
                if ( "Y".equals(printParamVO.getPageOrt())) { // 横向
                    mergePdfHx.addSource(in);
                } else {
                    mergePdf.addSource(in);
                }
            }
        }
        return in;
    }


    public byte[] zip(List<byte[]> list,List<String> listname) throws IOException {
        ByteArrayOutputStream bos = null;
        ZipOutputStream zipOutputStream =null;

        try {
            bos = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(bos);
            int count = 0;
            for (byte[] bytes : list) {
                zipOutputStream.putNextEntry(new ZipEntry(listname.get(count)));
                zipOutputStream.write(bytes);
                count++;
            }
            zipOutputStream.close();
            return bos.toByteArray();
        } catch (Exception ex) {
            log.error("解压失败", ex);
            throw new WiseRunException(ex);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    log.error("解压失败", e);
                }
            }
            if (zipOutputStream != null) {
                try {
//                    zipOutputStream.closeEntry();
                    zipOutputStream.close();
                } catch (IOException e) {
                    log.error("解压失败", e);
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
        queryparamvo.setEnddate(DateUtils.getPeriodEndDate(setvo.getVprintperiod().split("~")[1]));
        queryparamvo.setIshasjz(DZFBoolean.FALSE);
        queryparamvo.setXswyewfs(DZFBoolean.FALSE);
        queryparamvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
        queryparamvo.setCjq(1);
        queryparamvo.setCjz(6);
        return queryparamvo;
    }

    private PrintParamVO getPrintParamVO(BatchPrintSetVo setvo,CorpVO corpVO,String fycode) {
        PrintParamVO printParamVO = new PrintParamVO();
        printParamVO.setCorpName(corpVO.getUnitname());
        printParamVO.setTitleperiod(setvo.getVprintperiod());
        printParamVO.setType(getModelType(setvo.getVmobelsel()));
        printParamVO.setFont(setvo.getVfontsize().intValue() + "");
        printParamVO.setLeft(setvo.getDleftmargin().intValue() + "");
        printParamVO.setTop(setvo.getDtopmargin().intValue() + "");
        printParamVO.setPrintdate(setvo.getDprintdate() == null ? "" : setvo.getDprintdate().toString());// 打印期间

        if (!StringUtil.isEmpty(setvo.getKmpage())) {
            if (setvo.getKmpage().indexOf(fycode) > 0) {
                printParamVO.setIsPaging("Y"); // 是否分页
            } else {
                printParamVO.setIsPaging("N"); // 是否分页
            }
        } else {
            printParamVO.setIsPaging("N"); // 是否分页
        }
        printParamVO.setPageOrt(getDirCode(setvo.getReviewdir()));
        return printParamVO;
    }
}

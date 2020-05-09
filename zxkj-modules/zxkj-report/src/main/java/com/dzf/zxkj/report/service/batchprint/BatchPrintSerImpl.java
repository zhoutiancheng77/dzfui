package com.dzf.zxkj.report.service.batchprint;

import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.KmReoprtQueryParamVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintReporUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.batchprint.PrintStatusEnum;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
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
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

@Service("gl_rep_batchprinterv")
@Slf4j
public class BatchPrintSerImpl implements  IBatchPrintSer {

    @Autowired
    private IZxkjPlatformService zxkjPlatformService;
    @Autowired
    private IKMMXZReport gl_rep_kmmxjserv;
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
                    CorpVO corpvo = zxkjPlatformService.queryCorpByPk(setvo.getPk_corp());
                    // 合并数据
                    PDFMergerUtility mergePdf = new PDFMergerUtility();
                    if (!StringUtil.isEmpty(printcode)) {
                        String[] printcodes = printcode.split(",");
                        // 涉及异常的处理，目前先不处理
                        // 生成的文件，放在文件服务器中
                        bOut = new ByteArrayOutputStream();
                        String filename = UUID.randomUUID().toString();
                        for (String code: printcodes) {
                            // 代码来源是每个action对应的Print方法
                            if ("kmmx".equals(code)) { // 科目明细账
                                byte[] byts = handleKmmx(setvo,corpvo,userVO);
                                if (byts!=null && byts.length > 0) {
                                    in = new ByteArrayInputStream(byts);
                                    mergePdf.addSource(in);
                                }
                                log.info("科目明细账执行完毕!");
                            } else if ("kmzz".equals(code)) {

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



    /**
     * 科目明细账
     */
    private byte[] handleKmmx(BatchPrintSetVo setvo, CorpVO corpVO, UserVO userVO) {
        try {

            KmMxrController kmMxrController = new KmMxrController();

            // 打印参数设定
            PrintParamVO printParamVO = new PrintParamVO();
            printParamVO.setType(getModelType(setvo.getVmobelsel()));
            printParamVO.setFont(setvo.getVfontsize().intValue() + "");
            printParamVO.setLeft(setvo.getDleftmargin().intValue() + "");
            printParamVO.setTop(setvo.getDtopmargin().intValue() + "");
            printParamVO.setPrintdate(setvo.getVprintperiod());// 打印日期
            printParamVO.setIsPaging("kmmx".equals(setvo.getKmpage()) ? "Y": "N"); // 是否分页
            printParamVO.setPageOrt(getDirCode(setvo.getReviewdir()));

            // 查询参数设定
            KmReoprtQueryParamVO queryparamvo = new KmReoprtQueryParamVO();
            queryparamvo.setPk_corp(corpVO.getPk_corp());
            queryparamvo.setBegindate1(DateUtils.getPeriodStartDate(setvo.getVprintperiod().split("~")[0]) );
            queryparamvo.setEnddate(DateUtils.getPeriodEndDate(setvo.getVprintperiod().split("~")[0]));
            queryparamvo.setIshasjz(DZFBoolean.FALSE);
            queryparamvo.setXswyewfs(DZFBoolean.FALSE);
            queryparamvo.setBtotalyear(DZFBoolean.TRUE);//本年累计
            queryparamvo.setCjq(1);
            queryparamvo.setCjz(6);

            PrintReporUtil printReporUtil = new PrintReporUtil(zxkjPlatformService, corpVO, userVO, null);

            printReporUtil.setbSaveDfsSer(DZFBoolean.TRUE); // 保存到文件服务器

            Map<String, String> pmap = printReporUtil.getPrintMap(printParamVO);

            String lineHeight = pmap.get("lineHeight");
            String font = pmap.get("font");
            printReporUtil.setIscross(new DZFBoolean(pmap.get("pageOrt")));

            KmMxZVO[] bodyvos = kmMxrController.reloadNewValue(printParamVO.getTitleperiod(), printParamVO.getCorpName(),
                    printParamVO.getIsPaging(), queryparamvo,gl_rep_kmmxjserv,zxkjPlatformService);

            ReportUtil.updateKFx(bodyvos);
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数
            String km = bodyvos[0].getKm();
            tmap.put("公司", printParamVO.getCorpName());
            tmap.put("期间", printParamVO.getTitleperiod());
            tmap.put("单位", new ReportUtil(zxkjPlatformService).getCurrencyByPk(queryparamvo.getPk_currency()));
            printReporUtil.setLineheight(StringUtil.isEmpty(lineHeight) ? 22f : Float.parseFloat(lineHeight));
            printReporUtil.setTableHeadFount(new Font(printReporUtil.getBf(), Float.parseFloat(font), Font.NORMAL));//设置表头字体
            Object[] obj = null;
            if (printParamVO.getIsPaging() != null && printParamVO.getIsPaging().equals("Y")) {  //需要分页打印
                Map<String, List<SuperVO>> mxmap = new HashMap<String, List<SuperVO>>();
                for (KmMxZVO mxvo : bodyvos) {
                    List<SuperVO> mxlist = null;
                    mxvo.setGs(bodyvos[0].getGs());
                    mxvo.setTitlePeriod(bodyvos[0].getTitlePeriod());
                    if (!mxmap.containsKey(mxvo.getKmbm())) {  //map里的key 不包含当前数据科目编码
                        mxlist = new ArrayList<SuperVO>();     // 就 创建一个list  把这条数据 加进去
                        mxlist.add(mxvo);
                    } else {
                        mxlist = mxmap.get(mxvo.getKmbm()); //map里的key 包含当前数据科目编码
                        mxlist.add(mxvo);
                    }
                    mxmap.put(mxvo.getKmbm(), mxlist);       // key=kmbn   value=list
                }
                //排序--根据key排序
                mxmap = kmMxrController.sortMapByKey(mxmap);
                Map<String, YntCpaccountVO> cpamap = zxkjPlatformService.queryMapByPk(queryparamvo.getPk_corp());
                String kmfullname = "";
                for (Map.Entry<String, List<SuperVO>> kmEntry : mxmap.entrySet()) {
                    List<SuperVO> kmList = kmEntry.getValue();// 得到当前科目 所对应的 数据
                    SuperVO kmvo = kmEntry.getValue().get(0);
                    String id = (String) kmList.get(0).getAttributeValue("pk_accsubj");
                    if (cpamap.containsKey(id)) {
                        kmList.get(0).setAttributeValue("km", cpamap.get(id).getAccountname());
                    }
                    if (kmvo.getAttributeValue("pk_accsubj") != null
                            && ((String) kmvo.getAttributeValue("pk_accsubj")).length() > 24) {//默认有辅助项目
                        kmfullname = kmvo.getAttributeValue("kmfullname") + "/" + kmvo.getAttributeValue("km") + "(" + kmEntry.getKey() + ")";
                    } else {
                        kmfullname = kmvo.getAttributeValue("kmfullname") + "(" + kmEntry.getKey() + ")";
                    }
                    kmList.get(0).setAttributeValue("kmfullname", kmfullname);
                }
                if (!StringUtil.isEmpty(queryparamvo.getPk_currency()) && !queryparamvo.getPk_currency().equals(DzfUtil.PK_CNY)) {
                    obj = kmMxrController.getPrintXm(3);
                } else {
                    obj = kmMxrController.getPrintXm(2);
                }
                //打印
                printReporUtil.printHz(mxmap, new SuperVO[]{}, "*科目明细账", (String[]) obj[0],
                        (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
            } else {//不需要分页打印
                if (!StringUtil.isEmpty(queryparamvo.getPk_currency())) {
                    obj = kmMxrController.getPrintXm(1);
                } else {
                    obj = kmMxrController.getPrintXm(0);
                }
                //打印
                printReporUtil.printHz(new HashMap<String, List<SuperVO>>(), bodyvos, "科目明细账", (String[]) obj[0],
                        (String[]) obj[1], (int[]) obj[2], (int) obj[3], pmap, tmap);
            }

            return printReporUtil.getContents();
        } catch (DocumentException e) {
            log.error("打印错误", e);
        } catch (IOException e) {
            log.error("打印错误", e);
        }
        return null;
    }
}

package com.dzf.zxkj.platform.controller.glic;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.glic.IcDetailVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherPrintParam;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.ICrkMxService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.util.NumberToCN;
import com.dzf.zxkj.secret.CorpSecretUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 出入库打印公共类
 *
 * @author zhangj
 */
@Slf4j
public class CrkPrintUtil {

    private static final float PDF_PERCENT = 0.75f;


    public byte[] batchPrintCrkContentToByte(BatchPrintSetVo setvo, UserVO userVO, CorpVO corpVO){
        Rectangle pageSize = PageSize.A4;
        float leftsize = 47f;
        float rightsize = 15f;
        float topsize = 36f;
        Document document = new Document(pageSize, leftsize, rightsize, topsize, 4);
        ByteArrayOutputStream buffer = null;
//        FileOutputStream fileOutputStream = null;
        try{
            String printperiod =  setvo.getVprintperiod();
            IPzglService gl_pzglserv = (IPzglService) SpringUtils.getBean("gl_pzglserv");
            List<String> idlists =  gl_pzglserv.queryIds(printperiod.split("~")[0] , printperiod.split("~")[1], corpVO.getPk_corp());
            ICrkMxService gl_rep_crkmxserv = (ICrkMxService) SpringUtils.getBean("gl_rep_crkmxserv");
            SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
            Map<String, List<IcDetailVO>> crkmxlist = gl_rep_crkmxserv.queryCrkmxs(null,
                    idlists.toArray(new String[0]), corpVO.getPk_corp(), "");
            buffer = new ByteArrayOutputStream();
//            fileOutputStream = new FileOutputStream(new File("d:/dddd.pdf"));
            PdfWriter writer = PdfWriter.getInstance(document, buffer);
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            CrkPrintUtil printutil = new CrkPrintUtil();
            // 赋值首字符的值
            batchPrintCrkContent(leftsize, topsize, document, canvas, crkmxlist, corpVO);
            document.close();
            byte[] bytes =  buffer.toByteArray();
            return bytes;
        }  catch (Exception e) {
            log.error("打印出入库明细", e);
        }finally {
//            if (document !=null) {
//                document.close();
//            }
            try {
                if (buffer != null) {
                    buffer.close();
                }
            } catch (IOException e) {
            }
        }
       return null;

    }

    /**
     * 批量打印出入库明细
     *
     * @throws IOException
     */
    public void batchPrintCrkContent(float leftsize, float topsize, Document document,
                                     PdfContentByte canvas, Map<String, List<IcDetailVO>> lists,
                                     CorpVO cpvo) throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont("/font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titlefont = new Font(bf, 15, Font.BOLD);
        Font basefont = new Font(bf, 9, Font.NORMAL);
        //判断是否从新开始新的页数
        float totalhead = 0;
        float t_totalhead = 0;
        float t_totalhead_table = 0;
        float begin_pos = 825 - topsize;
        IParameterSetService parameterserv = (IParameterSetService) SpringUtils.getBean("sys_parameteract");
        String numStr = parameterserv.queryParamterValueByCode(cpvo.getPk_corp(), IParameterConstants.DZF009);
        String priceStr = parameterserv.queryParamterValueByCode(cpvo.getPk_corp(), IParameterConstants.DZF010);
        int numjd = 4;
        int pricejd = 4;
        if (!StringUtil.isEmpty(numStr)) {
            numjd = Integer.parseInt(numStr);
        }
        if (!StringUtil.isEmpty(priceStr)) {
            pricejd = Integer.parseInt(priceStr);
        }
        int count = 0;

        for (Entry<String, List<IcDetailVO>> entry : lists.entrySet()) {
            Map<String, String> headmap = new HashMap<String, String>();
            List<IcDetailVO> list = lists.get(entry.getKey());
            getCrkHeadMap(entry.getKey(), list, cpvo, headmap);
            if (begin_pos <= (100)) {//打印完的情况
                totalhead = 0;
                document.newPage();
                begin_pos = 825 - topsize;
            }
            BaseColor basecolor = new BaseColor(167, 167, 167);
            Phrase gs = new Phrase("--------" + headmap.get("gs") + "--------", basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, gs, 240, begin_pos, 0);

            begin_pos = begin_pos - 20;
            Phrase title = new Phrase(headmap.get("title"), titlefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, title, 260, begin_pos, 0);
            Phrase no = new Phrase("NO:" + headmap.get("no"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, no, 360, begin_pos, 0);
            Phrase fj = new Phrase("附件：" + headmap.get("fj"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, fj, 450, begin_pos, 0);


            begin_pos = begin_pos - 15;
            // 赋值供应商，凭证号，日期
            Phrase gys = new Phrase(headmap.get("gys"), basefont);//"供应商："+
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, gys, leftsize, begin_pos, 0);
            Phrase pzh = new Phrase("凭证号：" + headmap.get("pzh"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, pzh, leftsize + 230, begin_pos, 0);
            Phrase rq = new Phrase(headmap.get("rq"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_RIGHT, rq, leftsize + 496, begin_pos, 0);

            begin_pos = begin_pos - 10;
            //根据list循环
            // 新增表格
            String[] columnnames = new String[]{"序号", "存货编码", "存货名称", "规格(型号)", "单位", "数量", "单价", "金额", "备注"};
            String[] columnkeys = new String[]{"spbm", "spmc", "spgg", "Jldw", "nnum", "nprice", "je", "null"};
            PdfPTable table = null;
            PdfPCell cell;
            table = getHeadTable(basefont, basecolor, columnnames);
            t_totalhead_table = 0;
            for (int i = 0; i < list.size(); i++) {
                if (t_totalhead_table >= (begin_pos - 100)) {
                    if (i == 0) {
                        table.writeSelectedRows(0, -1, leftsize, begin_pos, canvas);
                    } else {
                        table.writeSelectedRows(0, -1, leftsize, begin_pos, canvas);
                    }
//					//打印日期，第几页
//					Phrase dyrq = new Phrase("打印日期:"+new DZFDate().toString(), basefont);
//					ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, dyrq, leftsize,begin_pos-table.getTotalHeight()-15 , 0);
                    begin_pos = 825 - topsize;
                    table = getHeadTable(basefont, basecolor, columnnames);//重新获取table
                    totalhead = 0;
                    document.newPage();
                    t_totalhead_table = 0;
                }
                cell = new PdfPCell(new Phrase((i + 1) + "", basefont));
                cell.setBorderColor(basecolor);
                table.addCell(cell);
                for (int j = 0; j < columnkeys.length; j++) {
                    DZFDouble numvalue = DZFDouble.ZERO_DBL;
                    Object tvalue = null;
                    if ("je".equals(columnkeys[j])) {
                        numvalue = SafeCompute.add(list.get(i).getJfmny(), list.get(i).getDfmny());
                        cell = new PdfPCell(new Phrase(Common.format(numvalue.setScale(2, DZFDouble.ROUND_HALF_UP).toString()), basefont));
                        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else if ("nprice".equals(columnkeys[j])) {
                        numvalue = (DZFDouble) list.get(i).getAttributeValue(columnkeys[j]);
                        cell = new PdfPCell(new Phrase(numvalue == null ? "" : numvalue.setScale(pricejd, DZFDouble.ROUND_HALF_UP).toString(), basefont));
                        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else if ("nnum".equals(columnkeys[j])) {
                        numvalue = (DZFDouble) list.get(i).getAttributeValue(columnkeys[j]);
                        cell = new PdfPCell(new Phrase(numvalue == null ? "" : numvalue.setScale(numjd, DZFDouble.ROUND_HALF_UP).toString(), basefont));
                        cell.setVerticalAlignment(Element.ALIGN_RIGHT);
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    } else {
                        tvalue = list.get(i).getAttributeValue(columnkeys[j]);
                        cell = new PdfPCell(new Phrase(tvalue == null ? "" : tvalue.toString() + "", basefont));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                        cell.setVerticalAlignment(Element.ALIGN_CENTER);
                    }
                    cell.setBorderColor(basecolor);
                    cell.setFixedHeight(28);
                    table.addCell(cell);
                }
                t_totalhead_table = t_totalhead_table + 28;
            }
            // 合并单元格
            PdfPTable table1 = new PdfPTable(2);
            table1.setWidths(new int[]{1, 3});
            table1.setWidthPercentage(100);
            table1.setTotalWidth((float) 497);
            cell = new PdfPCell(new Phrase("合计人民币（大写）", basefont));
            cell.setBorderColor(basecolor);
            table1.addCell(cell);
            cell = new PdfPCell(new Phrase(headmap.get("sumzn")
                    + "   ¥  " + headmap.get("sum"), basefont));
            cell.setBorderColor(basecolor);
            table1.addCell(cell);

            table.writeSelectedRows(0, -1, leftsize, begin_pos, canvas);
            table1.writeSelectedRows(0, -1, leftsize, begin_pos - table.getTotalHeight(), canvas);

            begin_pos = begin_pos - table.getTotalHeight() - table1.getTotalHeight() - 10;
            // 表尾信息
            Phrase zg = new Phrase("主管:" + headmap.get("zg"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, zg, leftsize,
                    begin_pos, 0);

            Phrase kj = new Phrase("会计:" + headmap.get("kj"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, kj, leftsize + 200,
                    begin_pos, 0);

            Phrase jsr = new Phrase("经手人:" + headmap.get("jsr"), basefont);
            ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT, jsr, leftsize + 400,
                    begin_pos, 0);

            t_totalhead = totalhead + table.getTotalHeight() + table1.getTotalHeight() + topsize + 60f;
            count++;//判断是否最后一个
            totalhead = totalhead + table.getTotalHeight() + table1.getTotalHeight() + topsize;
            begin_pos = begin_pos - 10;
            if (t_totalhead < (PageSize.A4.getHeight() - 160) && count != lists.size()) {//虚线
                drawLine(canvas, 0, begin_pos, PageSize.A4.getWidth(), begin_pos);
            }
            begin_pos = begin_pos - 20;
        }

    }

    private PdfPTable getHeadTable(Font basefont, BaseColor basecolor, String[] columnnames) throws DocumentException {
        PdfPTable table;
        table = new PdfPTable(columnnames.length);
        table.setTotalWidth((float) 497);
        table.setWidths(new int[]{1, 3, 3, 3, 1, 2, 2, 2, 2});
        table.setWidthPercentage(100);
        PdfPCell cell;
        for (String str : columnnames) {
            cell = new PdfPCell(new Phrase(str, basefont));
            cell.setBorderColor(basecolor);
            cell.setVerticalAlignment(Element.ALIGN_CENTER);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setFixedHeight(20);
            table.addCell(cell);
        }
        return table;
    }

    private void getCrkHeadMap(String vicbillcode, List<IcDetailVO> crkmxlist, CorpVO cpvo,
                               Map<String, String> headmap) {
        SimpleDateFormat dateformat = new SimpleDateFormat("yyyy年MM月dd日");
        IcDetailVO firstvo = null;
        if (crkmxlist != null && crkmxlist.size() > 0) {
            firstvo = crkmxlist.get(0);
        }
        Integer lx = 0;//0入口 1出库
        headmap.put("gs", CorpSecretUtil.deCode(cpvo.getUnitname()));//XXX有限公司
        if (!StringUtil.isEmpty(vicbillcode) && vicbillcode.indexOf("_in") >= 0) {
            headmap.put("title", "入  库  单");//入  库  单
            lx = 0;
        } else {
            lx = 1;
            headmap.put("title", "出  库  单");//
        }
        headmap.put("no", vicbillcode.substring(0, vicbillcode.indexOf("_")));//NO:CG-201808001
        if (firstvo != null && firstvo.getNbills().intValue() != 0) {
            headmap.put("fj", firstvo.getNbills() + "");//附件：1
        } else {
            headmap.put("fj", "");//附件：1
        }
        headmap.put("gys", (lx == 0 ? "供应商：" : "客户：") + getGys(firstvo, cpvo.getPk_corp()));//供应商：
        headmap.put("pzh", firstvo != null ? firstvo.getPzh() : "");//凭证号：
        headmap.put("rq", firstvo != null ? dateformat.format(firstvo.getDbilldate().toDate()) : "");//2017年03月31日
        headmap.put("zg", "");//主管:
//		if(!StringUtil.isEmpty(cpvo.getVsuperaccount())){//主管
//			UserVO uvo =  UserCache.getInstance().get(cpvo.getVsuperaccount(), "");
//			if(uvo!=null){
//				headmap.put("zg", uvo.getUser_name());//主管:
//			}
//		}
        headmap.put("kj", "");//会计:
//		if(firstvo!=null && !StringUtil.isEmpty(firstvo.getCoperatorid())){
//			UserVO uvo =  UserCache.getInstance().get(firstvo.getCoperatorid(), "");
//			if(uvo!=null){
//				headmap.put("kj", uvo.getUser_name());//会计:
//			}
//		}
        headmap.put("jsr", "");//经手人:

        DZFDouble sum = DZFDouble.ZERO_DBL;
        if (crkmxlist != null && crkmxlist.size() > 0) {
            for (IcDetailVO vo : crkmxlist) {
                sum = sum.add(SafeCompute.add(vo.getJfmny(), vo.getDfmny()));
            }
        }
        headmap.put("sum", Common.format(sum.setScale(2, DZFDouble.ROUND_HALF_UP).toString()));//合计
        headmap.put("sumzn", NumberToCN.getZnValue(sum.setScale(2, DZFDouble.ROUND_HALF_UP).toString()));
    }

    private static void drawLine(PdfContentByte canvas, float begin_x, float begin_y, float end_x, float end_y) {
        canvas.saveState();
        canvas.setLineDash(1, 1, 0);
        canvas.moveTo(begin_x, begin_y);
        canvas.lineTo(end_x, end_y);
        canvas.stroke();
        canvas.restoreState();
    }

    /**
     * 获取供应商信息
     *
     * @param firstvo
     * @return
     */
    private String getGys(IcDetailVO firstvo, String pk_corp) {
        if (firstvo == null) {
            return "";
        }

        if (StringUtil.isEmpty(firstvo.getPk_tzpz_h())) {
            return "";
        }
        IAccountService accountService = (IAccountService) SpringUtils.getBean(IAccountService.class);

        YntCpaccountVO[] kmvos = accountService.queryByPk(pk_corp);

        Map<String, YntCpaccountVO> kmmap = new HashMap<String, YntCpaccountVO>();

        for (YntCpaccountVO kmvo : kmvos) {
            kmmap.put(kmvo.getPk_corp_account(), kmvo);
        }

        Map<String, AuxiliaryAccountBVO> fzmap = getFzMap(pk_corp);

        SQLParameter sp = new SQLParameter();
        sp.addParam(firstvo.getPk_tzpz_h());
        SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
        TzpzBVO[] bvos = (TzpzBVO[]) singleObjectBO.queryByCondition(TzpzBVO.class, "nvl(dr,0)=0 and pk_tzpz_h = ? ", sp);

        if (bvos != null && bvos.length > 0) {
            YntCpaccountVO cpavo = null;
            AuxiliaryAccountBVO fzbvo = null;
            for (TzpzBVO bvo : bvos) {
                if (bvo.getVicbillcodetype() != null) {
                    continue;
                }
                cpavo = kmmap.get(bvo.getPk_accsubj());
                if (cpavo.getFullname().startsWith("应收账款") || cpavo.getFullname().startsWith("应付账款")) {
                    if (!StringUtil.isEmpty(bvo.getFzhsx1())
                            || !StringUtil.isEmpty(bvo.getFzhsx2())) {//取辅助项目值
                        String fzkey = "";
                        if (!StringUtil.isEmpty(bvo.getFzhsx1())) {
                            fzkey = bvo.getFzhsx1();
                        } else if (!StringUtil.isEmpty(bvo.getFzhsx2())) {
                            fzkey = bvo.getFzhsx2();
                        }
                        fzbvo = fzmap.get(fzkey);
                        if (fzbvo != null) {
                            return fzbvo.getName();
                        }
                    } else if (cpavo.getAccountlevel() > 1) {
                        return cpavo.getAccountname();
                    }
                }
            }
        }
        return "";
    }

    private Map<String, AuxiliaryAccountBVO> getFzMap(String pk_corp) {
        Map<String, AuxiliaryAccountBVO> fzmap = new HashMap<String, AuxiliaryAccountBVO>();

        IAuxiliaryAccountService gl_fzhsserv = (IAuxiliaryAccountService) SpringUtils.getBean("gl_fzhsserv");

        AuxiliaryAccountBVO[] fzbvos = gl_fzhsserv.queryAllB(pk_corp);

        for (AuxiliaryAccountBVO fzbvo : fzbvos) {
            fzmap.put(fzbvo.getPk_auacount_b(), fzbvo);
        }
        return fzmap;
    }
}

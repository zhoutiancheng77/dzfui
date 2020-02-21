package com.dzf.zxkj.platform.controller.voucher;

import com.dzf.zxkj.base.controller.BaseController;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.enums.LogRecordEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.bdset.PrintSettingVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherPrintAssitSetVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherPrintParam;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.model.voucher.VoucherPrintTemplate;
import com.dzf.zxkj.platform.service.bdset.IPersonalSetService;
import com.dzf.zxkj.platform.service.bdset.IPrintSettingService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.sys.IBDCurrencyService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.AmountUtil;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import com.itextpdf.text.pdf.draw.LineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.List;

@RestController
@RequestMapping("/voucher-manage/voucherPrint")
@Slf4j
public class VoucherPrintController extends BaseController {

    @Autowired
    private IPzglService gl_pzglserv;
    @Autowired
    private IPersonalSetService gl_gxhserv;
    @Autowired
    private IUserService userService;
    @Autowired
    private IBDCurrencyService sys_currentserv;
    @Autowired
    private ICorpService corpService;
    @Autowired
    private IPrintSettingService gl_print_setting_serv;

    @PostMapping("/print")
    public void print(VoucherPrintParam param, HttpServletResponse response) {
        String pk_corp = SystemUtil.getLoginCorpId();
        //先走后台查询(如果有数据则赋值)
        PrintSettingVO vo = gl_print_setting_serv.query(pk_corp,
                SystemUtil.getLoginUserId(), "凭证管理");
        if (vo != null && !StringUtils.isEmpty(vo.getPrint_setting())) {
            VoucherPrintParam savedParam = JsonUtils.deserialize(vo.getPrint_setting(), VoucherPrintParam.class);
            savedParam.setIds(param.getIds());//凭证信息单独处理
            param = savedParam;
        }
        VoucherPrintAssitSetVO[] printAssitSetVOs = gl_pzglserv.queryPrintAssistSetting(pk_corp);
        param.setAssistSetting(printAssitSetVOs);

        try {
            writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "打印凭证");
        } catch (Exception e) {
        }

        printVoucherByTemplate(param, response);
    }

    /**
     * 根据模板打印
     *
     * @param param
     */
    private void printVoucherByTemplate(VoucherPrintParam param, HttpServletResponse response) {
        if (StringUtils.isEmpty(param.getIds())) {
            return;
        }
        VoucherPrintTemplate template = getTemplate(param);

        // 打印页面总数
        Integer zdType = param.getZdr();
        String zdr = null;
        Set<String> corpSet = userService.querypowercorpSet(SystemUtil.getLoginUserId());
        if (zdType == null) {
            zdType = IVoucherConstants.ORIGIN_USER;
        } else if (zdType == IVoucherConstants.ASSIGN_USER) {
            zdr = param.getUser_name();
        } else if (zdType == IVoucherConstants.CURRENT_USER) {
            UserVO user = SystemUtil.getLoginUserVo();
            if (user != null) {
                zdr = user.getUser_name();
            }
        }
        // 是否显示审核人
        boolean isshow_vappr = param.getShow_vappr() == null ? true : param
                .getShow_vappr().booleanValue();
        // 是否显示记账人
        boolean isshow_vjz = param.getShow_vjz() == null ? true : param
                .getShow_vjz().booleanValue();

        float originalTop = template.getMarginTop();

        String pk_corp = SystemUtil.getLoginCorpId();
        Document document = null;
        PdfWriter writer = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        Map<String, String> userMap = new HashMap<String, String>();
        Map<String, String> corpMap = new HashMap<String, String>();
        BdCurrencyVO[] currencyVOS = sys_currentserv.queryCurrency();
        Map<String, BdCurrencyVO> currencyVOMap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(currencyVOS),
                new String[]{"pk_currency"});
        int tableCount = 1;
        boolean isSuccess = true;
        try {
            Rectangle rec = new Rectangle(template.getDocumentWidth(),
                    template.getDocumentHeight());
            document = new Document(rec, template.getMarginLeft(), 0,
                    template.getMarginTop(), 0);
            writer = PdfWriter.getInstance(document, buffer);
            if (template.getRotate() != null) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, template.getRotate());
            }
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            float marginLeft = template.getMarginLeft();
            float marginTop = template.getMarginTop();
            // 凭证高度
            float voucherHeight = 0;
            if (template.getVoucherNumber() > 1) {
                voucherHeight = template.getDocumentHeight() / template.getVoucherNumber();
            }
            List<TzpzHVO> hvos = gl_pzglserv.queryByIDs(param.getIds(), param);
            Map<String, DZFBoolean> signmap = new HashMap<>();

            TzpzBVO[] bvoArray = null;
            String date = null;
            int len = 0;
            Font infoFont = template.getInfoFont();
            for (TzpzHVO hvo : hvos) {
                if (!corpSet.contains(hvo.getPk_corp())) {
                    continue;
                }
                DZFBoolean isshowcn = getSignPar(hvo.getPk_corp(), signmap);

                bvoArray = hvo.getChildren();
                date = hvo.getDoperatedate().toString();
                len = bvoArray == null ? 0 : bvoArray.length;
                for (int b = 0; b < len || b == 0; b += template
                        .getVoucherRows()) {
                    if (tableCount > 1) {
                        marginTop += voucherHeight;
                    }

                    if (template.isShowUnderline()) {
                        LineSeparator line = new LineSeparator();
                        line.setLineWidth(0.8f);
                        line.drawLine(canvas, template.getUnderlineLeft()
                                + marginLeft, template.getUnderlineRight()
                                + marginLeft, template.getUnderlineTop()
                                - marginTop);
                    }

                    Phrase title = new Phrase("记  账  凭  证",
                            template.getTitleFont());
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            title, template.getTitleLeft() + marginLeft,
                            template.getTitleTop() - marginTop, 0);

                    int bills = hvo.getNbills() == null ? 0 : hvo.getNbills();
                    Phrase bill = new Phrase("附单据数：" + bills, infoFont);
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            bill, template.getBillLeft() + marginLeft,
                            template.getBillTop() - marginTop, 0);

                    Phrase corpPhrase = new Phrase();
                    String corpName = getCorpName(hvo.getPk_corp(), corpMap);
                    float corpFontSize = getFontSizeByWidth(template.getPzhLeft() - 55, corpName,
                            infoFont.getSize(), infoFont.getBaseFont());
                    corpPhrase.add(new Chunk("核算单位：", infoFont));
                    corpPhrase.add(new Chunk(corpName,
                            new Font(infoFont.getBaseFont(), corpFontSize)));
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            corpPhrase, marginLeft,
                            template.getInfoTop() - marginTop, 0);

                    Phrase cdate = new Phrase();
                    cdate.add(new Chunk("日期：", infoFont));
                    cdate.add(new Chunk(date, infoFont));
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            cdate, template.getDateLeft() + marginLeft,
                            template.getDateTop() - marginTop, 0);
                    Phrase pzh = new Phrase();
                    pzh.add(new Chunk("凭证号：", infoFont));
                    int rows = template.getVoucherRows();
                    if (len / rows >= 1 && len != rows) {
                        pzh.add(new Chunk(hvo.getPzh()
                                + " "
                                + Integer.valueOf(b / rows + 1)
                                + "/"
                                + Integer.valueOf(len / rows
                                + (len % rows == 0 ? 0 : 1)),
                                infoFont));
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                pzh, template.getPzhLeft() + marginLeft - 23,
                                template.getInfoTop() - marginTop, 0);// 凭证号
                    } else {
                        pzh.add(new Chunk(hvo.getPzh(), infoFont));
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                pzh, template.getPzhLeft() + marginLeft,
                                template.getInfoTop() - marginTop, 0);// 凭证号
                    }

                    PdfPTable table = getTableByTzpzBvo(hvo, bvoArray, b,
                            template, currencyVOMap);
                    table.writeSelectedRows(0, -1, template.getMarginLeft(),
                            template.getTableTop() - marginTop, canvas);

                    String jzr = getUserName(hvo.getVjzoperatorid(), userMap);
                    String shr = getUserName(hvo.getVapproveid(), userMap);
                    String cnr = getUserName(hvo.getVcashid(), userMap);
                    if (zdType == IVoucherConstants.ORIGIN_USER) {
                        zdr = getUserName(hvo.getCoperatorid(), userMap);
                        if ("HP80".equals(hvo.getSourcebilltype())
                                && StringUtils.isEmpty(zdr)) {
                            zdr = "大账房系统";
                        }
                    }
                    // 偏移量 只针对打印尾部的偏移
                    int offset = 0;
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            new Phrase("主管：", infoFont),
                            template.getOperatorLeft() + marginLeft,
                            template.getOperatorTop() - marginTop, 0);
                    if (isshow_vjz) {
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("记账：" + jzr, infoFont), offset
                                        + template.getOperatorLeft()
                                        + marginLeft, template.getOperatorTop()
                                        - marginTop, 0);
                    }
                    if (isshow_vappr) {
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("审核：" + shr, infoFont), offset
                                        + template.getOperatorLeft()
                                        + marginLeft, template.getOperatorTop()
                                        - marginTop, 0);
                    }
                    if (isshowcn != null && isshowcn.booleanValue()) {
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("出纳：" + cnr, infoFont), offset
                                        + template.getOperatorLeft()
                                        + marginLeft, template.getOperatorTop()
                                        - marginTop, 0);
                    }
                    offset += template.getOperatorOffset();
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            new Phrase("制单：" + zdr, infoFont), offset
                                    + template.getOperatorLeft() + marginLeft,
                            template.getOperatorTop() - marginTop, 0);
                    if (tableCount == template.getVoucherNumber()) {
                        // 插入新页
                        document.newPage();
                        if (template.getRotate() != null) {
                            writer.addPageDictEntry(PdfName.ROTATE,
                                    template.getRotate());
                        }
                        tableCount = 0;
                        marginTop = originalTop;
                    } else {
                        if (param.getPrint_splitline() != null && param.getPrint_splitline().booleanValue()) {
                            float lineTop = template.getDocumentHeight() - voucherHeight * tableCount;
                            DottedLineSeparator line = new DottedLineSeparator();
                            line.draw(canvas, 0, 0, template.getDocumentWidth(), 0, lineTop);
                        }
                    }
                    tableCount++;
                }
            }
        } catch (Exception e) {
            isSuccess = false;
            log.error(param.getIds());
            log.error("打印出错", e);
        } finally {
            try {
                int pdfTotalPage = 0;
                if (writer != null) {
                    pdfTotalPage = writer.getPageNumber();
                    if (writer.isPageEmpty()) {
                        pdfTotalPage--;
                    }
                }
                // 文档内容为空时不能调用close方法
                if (document != null && pdfTotalPage > 0) {
                    document.close();
                }
            } catch (Exception e) {
                log.error("凭证打印", e);
            }

        }
        ServletOutputStream out = null;
        try {
            if (isSuccess) {
                GxhszVO gxh = gl_gxhserv.query(pk_corp);
                if (gxh != null && gxh.getPrintType() == 1) {
                    response.setContentType("application/octet-stream");
                    response.addHeader("Content-Disposition",
                            "attachment;filename=voucher.pdf");
                } else {
                    response.setContentType("application/pdf");
                }
                response.setContentLength(buffer.size());
                out = response.getOutputStream();
                buffer.writeTo(out);
                buffer.flush();
                out.flush();
            }
        } catch (IOException e) {
            log.error("打印出错", e);
        } finally {
            if (buffer != null) {
                try {
                    buffer.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private void insertSpaceLine(PdfPTable table, float cellHeight) {
        PdfPCell cell = new PdfPCell(new Phrase(""));
        cell.setFixedHeight(cellHeight);
        int cols = table.getNumberOfColumns();
        for (int i = 0; i < cols; i++) {
            table.addCell(cell);
        }
    }

    public PdfPTable getTableByTzpzBvo(TzpzHVO hvo, TzpzBVO[] bvoArray,
                                       int startIndex, VoucherPrintTemplate template, Map<String, BdCurrencyVO> currencyVOMap) {
        boolean isShowCurrencyCol = template.isShowCurrencyColumn()
                && isContainsForeignCurrency(bvoArray);
        PdfPTable table = new PdfPTable(isShowCurrencyCol ? 5 : 4);
        table.setSpacingBefore(36);
        // table.setWidthPercentage(95);
        table.setTotalWidth(template.getTableWidth());
        try {
            table.setWidths(isShowCurrencyCol ? new int[]{3, 5, 3, 2, 2} : new int[]{3, 5, 2, 2});
        } catch (DocumentException e) {
        }
        // 表头
        addTabHead(template.getTableHeadFont(), table,
                template.getTableHeadHight(), isShowCurrencyCol);
        int line = 0;
        int len = bvoArray == null ? 0 : bvoArray.length;
        for (int j = startIndex; line < template.getVoucherRows() && j < len; j++, line++) {
            TzpzBVO bvo = bvoArray[j];
            addTableMny(template.getTableBodyFont(), table, bvo,
                    template.getTableBodyHight(), isShowCurrencyCol, currencyVOMap);
        }
        for (; line < template.getVoucherRows(); line++) {
            insertSpaceLine(table, template.getTableBodyHight());
        }
        totalAmount(template.getTableBodyFont(), table, hvo,
                template.getTableBodyHight());
        return table;
    }

    private boolean isContainsForeignCurrency(TzpzBVO[] bvoArray) {
        boolean isContains = false;
        for (TzpzBVO tzpzBVO : bvoArray) {
            if (tzpzBVO.getPk_currency() != null
                    && !tzpzBVO.getPk_currency().equals(IGlobalConstants.RMB_currency_id)) {
                isContains = true;
                break;
            }
        }
        return isContains;
    }

    private void addTableMny(Font fonts, PdfPTable table, TzpzBVO bvo,
                             float totalMnyHight, boolean isShowCurrencyCol, Map<String, BdCurrencyVO> currencyVOMap) {
        // bvo.getIsCur() 是否外币
        // bvo.getIsnum() 是否存货（数量核算）
        BaseFont baseFont = fonts.getBaseFont();
        PdfPCell cell = new PdfPCell();
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setFixedHeight(totalMnyHight);

        String curcode = bvo.getCur_code();

        if (StringUtils.isEmpty(curcode)) {
            curcode = bvo.getPk_currency();
            if (StringUtils.isEmpty(curcode))
                curcode = IGlobalConstants.RMB_currency_id;
            BdCurrencyVO cvo = currencyVOMap.get(curcode);
            if (cvo != null)
                curcode = cvo.getCurrencycode();
            else
                curcode = null;
        }
        StringBuilder addtionalInfo = new StringBuilder();
        boolean hasForeignCur = !isShowCurrencyCol && curcode != null && !curcode.equals("CNY");
        boolean hasNumber = bvo.getNnumber() != null && bvo.getNnumber().doubleValue() != 0;
        if (hasForeignCur || hasNumber) {
            addtionalInfo.append("(");
            if (hasForeignCur) {
                addtionalInfo.append("币种:")
                        .append(curcode)
                        .append(",汇率:")
                        .append(bvo.getNrate());
            }
            if (hasNumber) {
                if (hasForeignCur) {
                    addtionalInfo.append("; ");
                }
                addtionalInfo.append("数量:")
                        .append(filterZero(bvo.getNnumber().toString()));
                if (bvo.getMeaname() != null) {
                    addtionalInfo.append(bvo.getMeaname());
                }
                addtionalInfo.append(",单价:")
                        .append(filterZero(bvo.getNprice().toString()));
            }
            addtionalInfo.append(")");
        }

        String addtionalInfoStr = addtionalInfo.toString();
        Phrase zy = new Phrase();
        String zyString = bvo.getZy() == null ? "" : bvo.getZy();
        Font zyFont = getFont(table.getAbsoluteWidths()[0], cell,
                zyString + addtionalInfoStr, fonts);
        Chunk zyChunk = new Chunk(zyString, zyFont);
        zyChunk.setSplitCharacter(TabSplitCharacter.TAB);
        zy.add(zyChunk);
        Chunk addtionalZyChunk = new Chunk(addtionalInfoStr, new Font(baseFont, zyFont.getSize() - 1));
        zyChunk.setSplitCharacter(TabSplitCharacter.TAB);
        zy.add(addtionalZyChunk);
        cell.setPhrase(zy);
        table.addCell(cell);// 摘要
        List<AuxiliaryAccountBVO> fzhsvos = bvo.getFzhs_list();
        String fzhsCode = "";
        String fzhsName = "";
        if (fzhsvos != null && fzhsvos.size() > 0) {
            for (AuxiliaryAccountBVO fzhs : fzhsvos) {
                fzhsCode = fzhsCode + "_" + fzhs.getCode();
                fzhsName = fzhsName + "_" + fzhs.getName();
            }
        }
        StringBuilder vname = new StringBuilder();
        vname.append(bvo.getVcode() == null ? "未找到" : bvo.getVcode()).append(fzhsCode)
                .append(" ")
                .append(bvo.getKmmchie() == null ? "未找到" : bvo.getKmmchie())
                .append(fzhsName)
                .append(bvo.getInvname() == null ? "" : "_" + bvo.getInvname());
        if (!isShowCurrencyCol
                && curcode != null && !curcode.equals("CNY")) {
            vname.append("_").append(curcode);
        }
        bvo.setVname(vname.toString());
        // int vlength = bvo.getVname().getBytes("gbk").length;
        cell.setPhrase(new Phrase(bvo.getVname(),
                getFont(table.getAbsoluteWidths()[1], cell,
                        bvo.getVname(), fonts)));
        table.addCell(cell);// 科目
        // 币种
        if (isShowCurrencyCol) {
            PdfPCell curCell = new PdfPCell();
            curCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            curCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            curCell.setFixedHeight(totalMnyHight);
            if (bvo.getIsCur() != null && bvo.getIsCur().booleanValue()) {
                StringBuilder curCellContent = new StringBuilder();
                DZFDouble ybmny = DZFDouble.ZERO_DBL;
                double sub = SafeCompute.sub(bvo.getJfmny(), bvo.getDfmny()).doubleValue();
                if (sub > 0) {
                    ybmny = SafeCompute.sub(bvo.getYbjfmny(), bvo.getYbdfmny());
                } else if (sub < 0) {
                    ybmny = SafeCompute.sub(bvo.getYbdfmny(), bvo.getYbjfmny());
                }
                curCellContent.append(curcode).append("：")
                        .append(bvo.getNrate() == null ? "" : bvo.getNrate())
                        .append("\r原币：").append(String.format("%1$,.4f", ybmny.doubleValue()));
                String curStr = curCellContent.toString();
                curCell.setPhrase(new Phrase(curStr,
                        getFont(table.getAbsoluteWidths()[2], curCell,
                                curStr, fonts)));
            }
            table.addCell(curCell);
        }
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);// 金额右对齐
        cell.setPhrase(new Phrase(bvo.getJfmny() == null
                || bvo.getJfmny().compareTo(DZFDouble.ZERO_DBL) == 0 ? ""
                : String.format("%1$,.2f", (bvo.getJfmny().doubleValue())),
                fonts));
        table.addCell(cell);// 借方
        cell.setPhrase(new Phrase((bvo.getDfmny() == null
                || bvo.getDfmny().compareTo(DZFDouble.ZERO_DBL) == 0 ? ""
                : String.format("%1$,.2f", bvo.getDfmny().doubleValue())),
                fonts));
        table.addCell(cell);// 贷方
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
    }

    private void totalAmount(Font tableBodyFonts, PdfPTable table, TzpzHVO hvo,
                             float cellHeight) {
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("合计："
                + AmountUtil.toChinese(String.format("%1$,.2f", hvo.getJfmny()
                .doubleValue())), tableBodyFonts));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setColspan(2);
        cell.setFixedHeight(cellHeight);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.format("%1$,.2f", hvo.getJfmny()
                .doubleValue()), tableBodyFonts));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase(String.format("%1$,.2f", hvo.getDfmny()
                .doubleValue()), tableBodyFonts));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        table.addCell(cell);
        // document.add(table);
    }

    private void addTabHead(Font fonts10_bold, PdfPTable table,
                            float totalMnyHight, boolean isShowCurrencyCol) {
        PdfPCell cell;
        cell = new PdfPCell(new Phrase("摘    要", fonts10_bold));
        cell.setFixedHeight(totalMnyHight);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("科    目", fonts10_bold));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        if (isShowCurrencyCol) {
            cell = new PdfPCell(new Phrase("币    别", fonts10_bold));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
        }
        cell = new PdfPCell(new Phrase("借  方", fonts10_bold));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
        cell = new PdfPCell(new Phrase("贷  方", fonts10_bold));
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(cell);
    }

    /**
     * 文字过多时获取适应单元格的字体
     *
     * @param cellWidth
     * @param cell
     * @param content
     * @param fontSize
     * @param baseFont
     * @return
     */
    private float getFontSize(float cellWidth, PdfPCell cell,
                              String content, float fontSize, BaseFont baseFont) {
        if (fontSize < 3) {
            return fontSize;
        }
        cellWidth = cellWidth - cell.getPaddingLeft() - cell.getPaddingRight();
        float cellHeight = cell.getFixedHeight() - cell.getPaddingBottom() - cell.getPaddingTop();
        int maxLines = (int) Math.floor(cellHeight / fontSize);
        String[] contentLines = content.split("\r");
        int calLines = 0;
        for (String lineStr : contentLines) {
            float contentWidth = baseFont.getWidthPoint(lineStr, fontSize);
            calLines += (int) Math.ceil(contentWidth / cellWidth);
        }
        if (calLines > maxLines) {
            fontSize = getFontSize(cellWidth, cell,
                    content, fontSize - 1, baseFont);
        }
        return fontSize;
    }

    private Font getFont(float cellWidth, PdfPCell cell,
                         String content, Font font) {
        float originalSize = font.getSize();
        BaseFont baseFont = font.getBaseFont();
        float calSize = getFontSize(cellWidth, cell, content,
                originalSize, baseFont);
        if (calSize < originalSize) {
            font = new Font(baseFont, calSize);
        }
        return font;
    }

    private float getFontSizeByWidth(float width,
                                     String content, float fontSize, BaseFont baseFont) {
        if (fontSize < 3) {
            return fontSize;
        }
        float contentWidth = baseFont.getWidthPoint(content, fontSize);
        if (contentWidth > width) {
            fontSize = getFontSizeByWidth(width, content,
                    fontSize - 1, baseFont);
        }
        return fontSize;
    }

    /**
     * 去掉多余的0
     *
     * @param number
     * @return
     */
    private String filterZero(String number) {
        if (number.indexOf(".") > 0) {
            number = number.replaceAll("0+$", "");// 去掉多余的0
            number = number.replaceAll("\\.$", "");// 如最后一位是.则去掉
        }
        return number;
    }

    public DZFBoolean getSignPar(String pk_corp, Map<String, DZFBoolean> signmap) {

        if (signmap.containsKey(pk_corp)) {
            return signmap.get(pk_corp);
        } else {
            IParameterSetService sys_parameteract = (IParameterSetService) SpringUtils.getBean("sys_parameteract");
            DZFBoolean issign = null;
            YntParameterSet setvo = sys_parameteract.queryParamterbyCode(pk_corp, "dzf003");

            if (setvo == null || setvo.getPardetailvalue() == 1) {
                issign = DZFBoolean.FALSE;
            } else {
                issign = DZFBoolean.TRUE;
            }
            signmap.put(pk_corp, issign);
            return issign;
        }

    }

    private String getUserName(String userId, Map<String, String> userMap) {
        if (userId == null) {
            return "";
        }
        String userName = null;
        if (userMap.containsKey(userId)) {
            userName = userMap.get(userId);
        } else {
            userName = userService.queryUserJmVOByID(userId).getUser_name();
            userMap.put(userId, userName);
        }
        return userName;
    }

    private String getCorpName(String corpId, Map<String, String> corpMap) {
        if (corpId == null) {
            return "";
        }
        String corpName = null;
        if (corpMap.containsKey(corpId)) {
            corpName = corpMap.get(corpId);
        } else {
            CorpVO corpVo = corpService.queryByPk(corpId);
            if (corpVo != null) {
                corpName = corpVo.getUnitname();
            }
            corpMap.put(corpId, corpName);
        }
        return corpName;
    }

    private BaseFont getBaseFont(String fontName) {
        fontName = fontName == null || !"MSYH".equals(fontName.toUpperCase()) ? "SIMKAI.TTF" : "MSYH.TTF";
        String fontPath = "font/" + fontName;
        BaseFont bf = null;
        try {
            bf = BaseFont.createFont(fontPath,
                    BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } catch (DocumentException | IOException e) {
            log.error("获取字体失败", e);
        }
        return bf;
    }

    private VoucherPrintTemplate getTemplate(VoucherPrintParam param) {
        VoucherPrintTemplate template = new VoucherPrintTemplate();
        DZFDouble left = param.getLeft();
        DZFDouble top = param.getTop();
        float marginLeft = left == null ? 47f : left.floatValue() * 2.8346f;
        float marginTop = top == null ? 36f : top.floatValue() * 2.8346f;
        BaseFont bf = getBaseFont(param.getFont_name());
        int type = param.getType() == null
                ? IVoucherConstants.PRINT_VOUHER_LAND : param.getType();
        if (type == IVoucherConstants.PRINT_A4_TENROW) {
            type = IVoucherConstants.PRINT_A4_TWO;
            param.setPrint_rows(10);
        } else if (type == IVoucherConstants.PRINT_A5_TENROW) {
            type = IVoucherConstants.PRINT_A5;
            param.setPrint_rows(10);
        }
        if (type == IVoucherConstants.PRINT_A4_TWO) {
            // 页面大小
            template.setDocumentHeight(842);
            template.setDocumentWidth(595);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(2);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(11);
            template.setInfoFontSize(11);
            template.setTableHeadHight(33);
            template.setTableBodyHight(33);
            template.setTableWidth(497);
            template.setTableHeight(231);
            template.setTitleLeft(190);
            template.setTitleTop(828);
            template.setBillLeft(437);
            template.setBillTop(822);
            template.setInfoTop(789);
            template.setDateLeft(207);
            template.setDateTop(805);
            template.setPzhLeft(434);
            template.setTableTop(785);
            // 操作人
            template.setOperatorTop(539);
            template.setOperatorOffset(100);
        } else if (type == IVoucherConstants.PRINT_A4_TENROW) {
            // 页面大小
            template.setDocumentHeight(842);
            template.setDocumentWidth(595);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(2);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(10);
            template.setInfoFontSize(10);
            template.setTableHeadHight(22);
            template.setTableBodyHight(22);
            template.setTableWidth(497);
            template.setTableHeight(231);
            template.setTitleLeft(190);
            template.setTitleTop(824);
            template.setBillLeft(437);
            template.setBillTop(822);
            template.setInfoTop(789);
            template.setDateLeft(207);
            template.setDateTop(805);
            template.setPzhLeft(434);
            template.setTableTop(785);
            // 操作人
            template.setOperatorTop(510);
            template.setOperatorOffset(100);
        } else if (type == IVoucherConstants.PRINT_A4_TREE) {
            // 页面大小
            template.setDocumentHeight(842);
            template.setDocumentWidth(595);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(3);
            template.setTitleFontSize(17);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(10);
            template.setInfoFontSize(10);
            template.setTableHeadHight(24);
            template.setTableBodyHight(24);
            template.setTableWidth(501);
            template.setTableHeight(168);
            template.setTitleLeft(190);
            template.setTitleTop(828);
            template.setBillLeft(437);
            template.setBillTop(825);
            template.setInfoTop(798);
            template.setDateLeft(195);
            template.setDateTop(810);
            template.setPzhLeft(434);
            template.setTableTop(794);
            // 操作人
            template.setOperatorTop(613);
            template.setOperatorOffset(100);
        } else if (type == IVoucherConstants.PRINT_VOUHER_LAND
                || type == IVoucherConstants.PRINT_VOUHER_PORTRAIT) {
            if (type == IVoucherConstants.PRINT_VOUHER_PORTRAIT) {
                template.setRotate(PdfPage.SEASCAPE);
            }
            // 页面大小
            template.setDocumentHeight(340);
            template.setDocumentWidth(595);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(1);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(11);
            template.setInfoFontSize(11);
            template.setTableHeadHight(26);
            template.setTableBodyHight(26);
            template.setTableWidth(497);
            template.setTableHeight(182);
            template.setTitleLeft(185);
            template.setTitleTop(330);
            template.setBillLeft(437);
            template.setBillTop(320);
            template.setInfoTop(292);
            template.setDateLeft(200);
            template.setDateTop(308);
            template.setPzhLeft(432);
            template.setTableTop(288);
            // 操作人
            template.setOperatorTop(96);
            template.setOperatorOffset(100);
            // 标题下划线
            template.setShowUnderline(true);
            template.setUnderlineLeft(153);
            template.setUnderlineRight(363);
            template.setUnderlineTop(322);
            // 显示币种列
            template.setShowCurrencyColumn(true);
        } else if (type == IVoucherConstants.PRINT_A5) {
            // 旋转方向
            template.setRotate(PdfPage.SEASCAPE);
            // 页面大小
            template.setDocumentHeight(420);
            template.setDocumentWidth(595);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(1);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(12);
            template.setInfoFontSize(12);
            template.setTableHeadHight(34);
            template.setTableBodyHight(34);
            template.setTableWidth(497);
            template.setTableHeight(238);
            template.setTitleLeft(185);
            template.setTitleTop(408);
            template.setBillLeft(432);
            template.setBillTop(398);
            template.setInfoTop(371);
            template.setDateLeft(200);
            template.setDateTop(385);
            template.setPzhLeft(426);
            template.setTableTop(365);
            template.setOperatorTop(116);
            // 操作人
            template.setOperatorOffset(100);
            // 标题下划线
            template.setShowUnderline(true);
            template.setUnderlineLeft(153);
            template.setUnderlineRight(363);
            template.setUnderlineTop(400);
        } else if (type == IVoucherConstants.PRINT_A5_TENROW) {
            // 旋转方向
            template.setRotate(PdfPage.SEASCAPE);
            // 页面大小
            template.setDocumentHeight(420);
            template.setDocumentWidth(595);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(1);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(10);
            template.setInfoFontSize(10);
            template.setTableHeadHight(23);
            template.setTableBodyHight(23);
            template.setTableWidth(497);
            template.setTableHeight(276);
            template.setTitleLeft(185);
            template.setTitleTop(408);
            template.setBillLeft(432);
            template.setBillTop(398);
            template.setInfoTop(371);
            template.setDateLeft(215);
            template.setDateTop(385);
            template.setPzhLeft(426);
            template.setTableTop(365);
            // 操作人
            template.setOperatorTop(75);
            template.setOperatorOffset(100);
            // 标题下划线
            template.setShowUnderline(true);
            template.setUnderlineLeft(153);
            template.setUnderlineRight(363);
            template.setUnderlineTop(400);
        } else if (type == IVoucherConstants.PRINT_B5) {
            // 旋转方向
            template.setRotate(PdfPage.SEASCAPE);
            // 页面大小
            template.setDocumentHeight(728.5f);
            template.setDocumentWidth(515.9f);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(2);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(10);
            template.setInfoFontSize(10);
            template.setTableHeadHight(28);
            template.setTableBodyHight(28);
            template.setTableWidth(421.7f);
            template.setTableHeight(196);
            template.setTitleLeft(153);
            template.setTitleTop(720);
            template.setBillLeft(367);
            template.setBillTop(707);
            template.setInfoTop(695);
            template.setDateLeft(185);
            template.setDateTop(705);
            template.setPzhLeft(362);
            template.setTableTop(690);
            template.setOperatorTop(480);
            // 操作人
            template.setOperatorOffset(90);
        } else if (type == IVoucherConstants.PRINT_INVOICE) {
            // 页面大小
            template.setDocumentHeight(396.67f);
            template.setDocumentWidth(680f);
            // 页边距
            template.setMarginTop(marginTop);
            template.setMarginLeft(marginLeft);
            // 每页打印凭证数
            template.setVoucherNumber(1);
            template.setTitleFontSize(22);
            template.setTableHeadFontSize(13);
            template.setTableBodyFontSize(12);
            template.setInfoFontSize(12);
            template.setTableHeadHight(33);
            template.setTableBodyHight(33);
            template.setTableWidth(578f);
            template.setTableHeight(231);
            template.setTitleLeft(195);
            template.setTitleTop(389);
            template.setBillLeft(511);
            template.setBillTop(376);
            template.setInfoTop(349);
            template.setDateLeft(220);
            template.setDateTop(365);
            template.setPzhLeft(506);
            template.setTableTop(342);
            template.setOperatorTop(96);
            // 操作人
            template.setOperatorOffset(110);
            // 标题下划线
            template.setShowUnderline(true);
            template.setUnderlineLeft(178);
            template.setUnderlineRight(363);
            template.setUnderlineTop(382);
        }
        if (param.getPrint_rows() != null) {
            template.setVoucherRows(param.getPrint_rows());
        } else {
            template.setVoucherRows(5);
        }
        if (template.getVoucherRows() > 5) {
            int rows = template.getVoucherRows() + 2;
            float rowHeight = template.getTableHeight() / rows;
            template.setTableBodyHight(rowHeight);
            template.setTableHeadHight(rowHeight);
            int sizeShrink = template.getVoucherRows() > 8 ? 2 : 1;
            template.setTableHeadFontSize(template.getTableHeadFontSize() - sizeShrink);
            template.setTableBodyFontSize(template.getTableBodyFontSize() - sizeShrink);
        }

        template.setTitleFont(new Font(bf, template.getTitleFontSize(), Font.BOLD));
        template.setTableHeadFont(new Font(bf, template.getTableHeadFontSize(), Font.BOLD));
        template.setTableBodyFont(new Font(bf, template.getTableBodyFontSize(), Font.NORMAL));
        template.setInfoFont(new Font(bf, template.getInfoFontSize(), Font.NORMAL));
        return template;
    }

    @PostMapping("/printCover")
    public void printCover(VoucherPrintParam param,
                           @RequestParam String corpNames,
                           @RequestParam int page,
                           HttpServletResponse response) {
        VoucherPrintTemplate template = getTemplate(param);
        try {
            writeLogRecord(LogRecordEnum.OPE_KJ_OTHERVOUCHER, "打印凭证封皮");
        } catch (Exception e) {
        }
        // 打印页面总数
        int pdfTotalPage = 0;
        float originalTop = template.getMarginTop();

        String pk_corp = SystemUtil.getLoginCorpId();
        Document document = null;
        PdfWriter writer = null;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int tableCount = 1;
        try {
            Rectangle rec = new Rectangle(template.getDocumentWidth(),
                    template.getDocumentHeight());
            document = new Document(rec, template.getMarginLeft(), 0,
                    template.getMarginTop(), 0);
            writer = PdfWriter.getInstance(document, buffer);
            if (template.getRotate() != null) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, template.getRotate());
            }
            document.open();
            PdfContentByte canvas = writer.getDirectContent();
            float marginLeft = template.getMarginLeft();
            float marginTop = template.getMarginTop();
            // 凭证高度
            float voucherHeight = 0;
            if (template.getVoucherNumber() > 1) {
                voucherHeight = template.getDocumentHeight() / template.getVoucherNumber();
            }
            int len = 0;
            Font infoFont = template.getInfoFont();
            infoFont.setSize(13);
            template.setInfoFont(infoFont);

            List<String> corpNameList = new ArrayList<>();

            for (String corpName : corpNames.split(",")) {
                for (int i = 0; i < page; i++) {
                    corpNameList.add(corpName);
                }
            }

            for (String corpName : corpNameList) {
                for (int b = 0; b < len || b == 0; b += template
                        .getVoucherRows()) {
                    if (tableCount > 1) {
                        marginTop += voucherHeight;
                    }

                    Phrase title = new Phrase("记  账  凭  证  封  面",
                            template.getTitleFont());
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            title, template.getTitleLeft() + marginLeft - 50,
                            template.getTitleTop() - marginTop, 0);

                    Phrase corpPhrase = new Phrase();
                    float corpFontSize = getFontSizeByWidth(template.getPzhLeft() - 55, corpName,
                            infoFont.getSize(), infoFont.getBaseFont());
                    corpPhrase.add(new Chunk("单位：", infoFont));
                    corpPhrase.add(new Chunk(corpName,
                            new Font(infoFont.getBaseFont(), corpFontSize)));
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            corpPhrase, marginLeft,
                            template.getInfoTop() - marginTop, 0);

                    Phrase pzh = new Phrase();
                    pzh.add(new Chunk("共    册第    册", infoFont));  //-23
                    ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                            pzh, template.getPzhLeft() + marginLeft - 40,
                            template.getInfoTop() - marginTop, 0);// 凭证号

                    PdfPTable table = getCoverTable(template);
                    table.writeSelectedRows(0, -1, template.getMarginLeft(),
                            template.getTableTop() - marginTop, canvas);

                    // 偏移量 只针对打印尾部的偏移
                    int offset = 0;
                    if (param.getType() == 6) {
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("负责人（章）", infoFont),
                                template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);

                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("会计（章）", infoFont), 70 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("装订（章）", infoFont), 140 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);
                    } else if (param.getType() == 4) {
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("负责人（章）", infoFont),
                                template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);

                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("会计（章）", infoFont), 70 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("装订（章）", infoFont), 140 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);
                    } else if (param.getType() == 1) {
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("负责人（章）", infoFont),
                                template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 50, 0);

                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("会计（章）", infoFont), 70 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 50, 0);
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("装订（章）", infoFont), 140 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 50, 0);
                    } else if (param.getType() == 2) {
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("负责人（章）", infoFont),
                                template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);

                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("会计（章）", infoFont), 70 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("装订（章）", infoFont), 140 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 20, 0);
                    } else if (param.getType() == 5) {
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("负责人（章）", infoFont),
                                template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 50, 0);

                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("会计（章）", infoFont), 70 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 50, 0);
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("装订（章）", infoFont), 140 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 50, 0);
                    } else {
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("负责人（章）", infoFont),
                                template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 40, 0);

                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("会计（章）", infoFont), 70 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 40, 0);
                        offset += template.getOperatorOffset();
                        ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
                                new Phrase("装订（章）", infoFont), 140 + offset
                                        + template.getOperatorLeft() + marginLeft,
                                template.getOperatorTop() - marginTop + 40, 0);
                    }


                    if (tableCount == template.getVoucherNumber()) {
                        // 插入新页
                        document.newPage();
                        if (template.getRotate() != null) {
                            writer.addPageDictEntry(PdfName.ROTATE,
                                    template.getRotate());
                        }
                        tableCount = 0;
                        marginTop = originalTop;
                    } else {
                        if (param.getType() == 1 || param.getType() == 2 || param.getType() == 7) {
                            float lineTop = template.getDocumentHeight() - voucherHeight * tableCount;
                            DottedLineSeparator line = new DottedLineSeparator();
                            line.draw(canvas, 0, 0, template.getDocumentWidth(), 0, lineTop);
                        }
                    }
                    tableCount++;
                }
            }
        } catch (Exception e) {
            log.error(param.getIds());
            log.error("打印出错", e);
        } finally {
            if (writer != null) {
                pdfTotalPage = writer.getPageNumber();
                if (writer.isPageEmpty()) {
                    pdfTotalPage--;
                }
            }
            // 文档内容为空时不能调用close方法
            if (document != null && pdfTotalPage > 0) {
                document.close();
            }
        }

        if (pdfTotalPage <= 0) {
            PrintWriter pw = null;
            try {
                pw = response.getWriter();
                pw.write("打印失败");
                pw.flush();
            } catch (IOException e) {
            } finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException e) {
                    }
                }
                if (pw != null) {
                    pw.close();
                }
            }
        } else {
            ServletOutputStream out = null;
            try {
                GxhszVO gxh = gl_gxhserv.query(pk_corp);
                if (gxh != null && gxh.getPrintType() == 1) {
                    response.setContentType("application/octet-stream");
                    response.addHeader("Content-Disposition",
                            "attachment;filename=voucher.pdf");
                } else {
                    response.setContentType("application/pdf");
                }
                response.setContentLength(buffer.size());
                out = response.getOutputStream();
                buffer.writeTo(out);
                buffer.flush();
                out.flush();
            } catch (IOException e) {
                log.error("打印出错", e);
            } finally {
                if (buffer != null) {
                    try {
                        buffer.close();
                    } catch (IOException e) {
                    }
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }


    private PdfPTable getCoverTable(VoucherPrintTemplate template) throws DocumentException {
        PdfPTable table1 = new PdfPTable(1);
        table1.setTotalWidth(template.getTableWidth());

        //生成三列表格
        PdfPTable table = new PdfPTable(2);
        table.setTotalWidth(template.getTableWidth());
        table.setWidths(new float[]{15f, 50f});

        PdfPCell cell1 = new PdfPCell();
        Phrase title = new Phrase("记  账  凭  证",
                template.getInfoFont());
        cell1.setPhrase(title);
        cell1.setFixedHeight(template.getTableBodyHight() + 10);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell1.setRowspan(2);
        table.addCell(cell1);

        PdfPCell cell = new PdfPCell();
        Phrase title9 = new Phrase("自       月       日至       月       日止",
                template.getInfoFont());
        cell.setPhrase(title9);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell.setFixedHeight(template.getTableBodyHight() + 10);
        table.addCell(cell);

        PdfPCell cell2 = new PdfPCell();
        Phrase title2 = new Phrase("本月共：        号本册自        号至        号",
                template.getInfoFont());
        cell2.setPhrase(title2);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setFixedHeight(template.getTableBodyHight() + 10);
        table.addCell(cell2);

        PdfPCell cell3 = new PdfPCell();
        Phrase title3 = new Phrase("附原始凭证",
                template.getInfoFont());
        cell3.setPhrase(title3);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell3.setFixedHeight(template.getTableBodyHight() + 10);
        table.addCell(cell3);

        PdfPCell cell4 = new PdfPCell();
        Phrase title4 = new Phrase("本册共计：                                张",
                template.getInfoFont());
        cell4.setPhrase(title4);
        cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell4.setFixedHeight(template.getTableBodyHight() + 10);
        table.addCell(cell4);


        PdfPCell cell5 = new PdfPCell();
        Phrase title5 = new Phrase("附    件",
                template.getInfoFont());
        cell5.setPhrase(title5);
        cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell5.setFixedHeight(template.getTableBodyHight() + 10);
        table.addCell(cell5);


        PdfPCell cell6 = new PdfPCell();
        Phrase title6 = new Phrase("本册共计：                                张",
                template.getInfoFont());
        cell6.setPhrase(title6);
        cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell6.setFixedHeight(template.getTableBodyHight() + 10);
        table.addCell(cell6);

        table1.addCell(table);
        return table1;
    }
}

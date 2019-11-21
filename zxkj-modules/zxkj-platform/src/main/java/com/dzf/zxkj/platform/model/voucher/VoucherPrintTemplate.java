package com.dzf.zxkj.platform.model.voucher;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfNumber;

/**
 * 凭证打印模板
 *
 * @author lbj
 */
public class VoucherPrintTemplate {
    // 纸张宽度
    private float documentWidth;
    // 纸张高度
    private float documentHeight;
    // 旋转方向
    private PdfNumber rotate;
    // 打印凭证张数
    private int voucherNumber;
    // 打印一张凭证行数
    private int voucherRows;
    // 标题字体
    private float titleFontSize;
    private Font titleFont;
    // 凭证信息字体
    private float infoFontSize;
    private Font infoFont;
    // 表头单元格字体
    private float tableHeadFontSize;
    private Font tableHeadFont;
    // 表体字体
    private float tableBodyFontSize;
    private Font tableBodyFont;
    // 表头行高度
    private float tableHeadHight;
    // 表体行高度
    private float tableBodyHight;
    // 表格宽度
    private float tableWidth;
    // 表格高度
    private float tableHeight;
    // 表格位置
    private float tableTop;
    // 左边距
    private float marginLeft;
    // 上边距
    private float marginTop;
    // 标题位置
    private float titleLeft;
    private float titleTop;
    // 附单据数位置
    private float billLeft;
    private float billTop;
    // 表格上面信息位置
    private float infoTop;
    // 公司位置
    private float corpLeft;
    // 日期位置
    private float dateLeft;
    // 日期位置
    private float dateTop;
    // 凭证号位置
    private float pzhLeft;
    // 表格下面信息位置
    private float operatorLeft;
    private float operatorTop;
    // 操作人偏移
    private float operatorOffset;
    // 标题是否显示下划线
    private boolean showUnderline;
    // 标题下划线位置
    private float underlineLeft;
    private float underlineRight;
    private float underlineTop;

    // 显示币种列
    private boolean showCurrencyColumn;

    public float getDocumentWidth() {
        return documentWidth;
    }

    public void setDocumentWidth(float documentWidth) {
        this.documentWidth = documentWidth;
    }

    public float getDocumentHeight() {
        return documentHeight;
    }

    public void setDocumentHeight(float documentHeight) {
        this.documentHeight = documentHeight;
    }

    public PdfNumber getRotate() {
        return rotate;
    }

    public void setRotate(PdfNumber rotate) {
        this.rotate = rotate;
    }

    public int getVoucherNumber() {
        return voucherNumber;
    }

    public void setVoucherNumber(int voucherNumber) {
        this.voucherNumber = voucherNumber;
    }

    public int getVoucherRows() {
        return voucherRows;
    }

    public void setVoucherRows(int voucherRows) {
        this.voucherRows = voucherRows;
    }

    public float getTitleFontSize() {
        return titleFontSize;
    }

    public void setTitleFontSize(float titleFontSize) {
        this.titleFontSize = titleFontSize;
    }

    public Font getTitleFont() {
        return titleFont;
    }

    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    public float getInfoFontSize() {
        return infoFontSize;
    }

    public void setInfoFontSize(float infoFontSize) {
        this.infoFontSize = infoFontSize;
    }

    public Font getInfoFont() {
        return infoFont;
    }

    public void setInfoFont(Font infoFont) {
        this.infoFont = infoFont;
    }

    public float getTableHeadFontSize() {
        return tableHeadFontSize;
    }

    public void setTableHeadFontSize(float tableHeadFontSize) {
        this.tableHeadFontSize = tableHeadFontSize;
    }

    public Font getTableHeadFont() {
        return tableHeadFont;
    }

    public void setTableHeadFont(Font tableHeadFont) {
        this.tableHeadFont = tableHeadFont;
    }

    public float getTableBodyFontSize() {
        return tableBodyFontSize;
    }

    public void setTableBodyFontSize(float tableBodyFontSize) {
        this.tableBodyFontSize = tableBodyFontSize;
    }

    public Font getTableBodyFont() {
        return tableBodyFont;
    }

    public void setTableBodyFont(Font tableBodyFont) {
        this.tableBodyFont = tableBodyFont;
    }

    public float getTableHeadHight() {
        return tableHeadHight;
    }

    public void setTableHeadHight(float tableHeadHight) {
        this.tableHeadHight = tableHeadHight;
    }

    public float getTableBodyHight() {
        return tableBodyHight;
    }

    public void setTableBodyHight(float tableBodyHight) {
        this.tableBodyHight = tableBodyHight;
    }

    public float getTableWidth() {
        return tableWidth;
    }

    public void setTableWidth(float tableWidth) {
        this.tableWidth = tableWidth;
    }

    public float getTableHeight() {
        return tableHeight;
    }

    public void setTableHeight(float tableHeight) {
        this.tableHeight = tableHeight;
    }

    public float getTableTop() {
        return tableTop;
    }

    public void setTableTop(float tableTop) {
        this.tableTop = tableTop;
    }

    public float getMarginLeft() {
        return marginLeft;
    }

    public void setMarginLeft(float marginLeft) {
        this.marginLeft = marginLeft;
    }

    public float getMarginTop() {
        return marginTop;
    }

    public void setMarginTop(float marginTop) {
        this.marginTop = marginTop;
    }

    public float getTitleLeft() {
        return titleLeft;
    }

    public void setTitleLeft(float titleLeft) {
        this.titleLeft = titleLeft;
    }

    public float getTitleTop() {
        return titleTop;
    }

    public void setTitleTop(float titleTop) {
        this.titleTop = titleTop;
    }

    public float getBillLeft() {
        return billLeft;
    }

    public void setBillLeft(float billLeft) {
        this.billLeft = billLeft;
    }

    public float getBillTop() {
        return billTop;
    }

    public void setBillTop(float billTop) {
        this.billTop = billTop;
    }

    public float getInfoTop() {
        return infoTop;
    }

    public void setInfoTop(float infoTop) {
        this.infoTop = infoTop;
    }

    public float getCorpLeft() {
        return corpLeft;
    }

    public void setCorpLeft(float corpLeft) {
        this.corpLeft = corpLeft;
    }

    public float getDateLeft() {
        return dateLeft;
    }

    public void setDateLeft(float dateLeft) {
        this.dateLeft = dateLeft;
    }

    public float getDateTop() {
        return dateTop;
    }

    public void setDateTop(float dateTop) {
        this.dateTop = dateTop;
    }

    public float getPzhLeft() {
        return pzhLeft;
    }

    public void setPzhLeft(float pzhLeft) {
        this.pzhLeft = pzhLeft;
    }

    public float getOperatorLeft() {
        return operatorLeft;
    }

    public void setOperatorLeft(float operatorLeft) {
        this.operatorLeft = operatorLeft;
    }

    public float getOperatorTop() {
        return operatorTop;
    }

    public void setOperatorTop(float operatorTop) {
        this.operatorTop = operatorTop;
    }

    public float getOperatorOffset() {
        return operatorOffset;
    }

    public void setOperatorOffset(float operatorOffset) {
        this.operatorOffset = operatorOffset;
    }

    public boolean isShowUnderline() {
        return showUnderline;
    }

    public void setShowUnderline(boolean showUnderline) {
        this.showUnderline = showUnderline;
    }

    public float getUnderlineLeft() {
        return underlineLeft;
    }

    public void setUnderlineLeft(float underlineLeft) {
        this.underlineLeft = underlineLeft;
    }

    public float getUnderlineRight() {
        return underlineRight;
    }

    public void setUnderlineRight(float underlineRight) {
        this.underlineRight = underlineRight;
    }

    public float getUnderlineTop() {
        return underlineTop;
    }

    public void setUnderlineTop(float underlineTop) {
        this.underlineTop = underlineTop;
    }

    public boolean isShowCurrencyColumn() {
        return showCurrencyColumn;
    }

    public void setShowCurrencyColumn(boolean showCurrencyColumn) {
        this.showCurrencyColumn = showCurrencyColumn;
    }

}

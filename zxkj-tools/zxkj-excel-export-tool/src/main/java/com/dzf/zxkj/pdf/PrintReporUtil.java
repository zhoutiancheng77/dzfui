package com.dzf.zxkj.pdf;

import com.alibaba.fastjson.JSONArray;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.PrintParamVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.GxhszVO;
import com.dzf.zxkj.platform.model.report.FzYebVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

@Slf4j
@SuppressWarnings("all")
public class PrintReporUtil {

    private IZxkjPlatformService zxkjPlatformService;

    public DZFBoolean iscross = DZFBoolean.FALSE;// 是否横向

    private HttpServletResponse response;

    private GxhszVO gvo;

    private CorpVO corpVO;

    private UserVO userVO;

    private Float lineheight;// 行高

    private Float firstlineheight;//首行行高

    private Font tableHeadFount;// 行表头字体

    private BaseColor basecolor;// 背景颜色

    private String ispaging;//是否分页打印

    private DZFBoolean bshowzero;//空是否显示0

    public DZFBoolean rotate = DZFBoolean.FALSE;//是否旋转

    private BaseFont bf = null;

    private BaseFont bf_Bold = null;

    protected Map<String, String> precisionMap = null;

    public HttpServletResponse getResponse() {
        return response;
    }

    //凭证字张
    private Rectangle page_pz = new Rectangle(595f, 340f);

    public PrintReporUtil(IZxkjPlatformService zxkjPlatformService, CorpVO corpVO, UserVO userVO, HttpServletResponse response) {
        this.gvo = zxkjPlatformService.queryGxhszVOByPkCorp(corpVO.getPk_corp());
        this.userVO = userVO;
        this.corpVO = corpVO;
        this.response = response;
        this.zxkjPlatformService = zxkjPlatformService;
        try {
            bf = BaseFont.createFont("font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/fonts/simfang.ttf
            bf_Bold = BaseFont.createFont("font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// IGlobalConstants.FONTPATH;
            setLineheight(18f);// 设置行高
            lineheight = 18f;
            basecolor = new BaseColor(167, 167, 167);
        } catch (DocumentException e) {
            log.error("错误", e);
        } catch (IOException e) {
            log.error("错误", e);
        }
    }

    /**
     * 打印， 支持多层表头
     * <p>
     * columnnames参数如：{"科目名称", "期初_借方", "期初_贷方", "本期发生_借方", "本期发生_贷方", …}
     * </p>
     *
     * @param kmmap
     * @param zzvos
     * @param title
     * @param columns
     * @param columnnames 列标题中"_"作为上下表头行的分隔符，相邻两列的标题名中的_前缀相同，即认为合并上层表头行的相邻单元格
     * @param widths
     * @param pmap
     * @throws DocumentException
     * @throws IOException
     */
    public void printHz(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String title, String[] columns,
                        String[] columnnames, int[] widths, Integer pagecount, Map<String, String> pmap,
                        Map<String, String> tmap) throws DocumentException, IOException {
        if (pmap.get("type").equals("1"))//A4纸张
            printGroup(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, null, pmap, tmap); // A4纸张打印
        else if (pmap.get("type").equals("2"))//B5纸张
            printB5(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, null, pmap, tmap);
        else if (pmap.get("type").equals("4")) {//A5纸张
            printA5(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, null, pmap, tmap);
        } else//默认是凭证纸张
            printVoucherPage(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, pmap, tmap);
    }

    public void setDefaultValue(String showbm, FzYebVO[] bodyvos) {
        if(!"Y".equals(showbm)){
            if(bodyvos!=null && bodyvos.length>0){
                for(FzYebVO fzyevo:bodyvos){
                    if("合计".equals(fzyevo.getFzhsxCode())){
                        fzyevo.setFzhsxName(fzyevo.getFzhsxCode());
                    }
                }
            }
        }
    }
    public void printHz(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String title, String[] columns,
                        String[] columnnames, int[] widths, Integer pagecount, String type, Map<String, String> pmap,
                        Map<String, String> tmap) throws DocumentException, IOException {
        if (pmap.get("type").equals("1"))//A4纸张
            printGroup(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, null, pmap, tmap); // A4纸张打印
        else if (pmap.get("type").equals("2"))//B5纸张
            printB5(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, null, pmap, tmap);
        else if (pmap.get("type").equals("4")) {//A5纸张
            printA5(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, null, pmap, tmap);
        } else//默认是凭证纸张
            printVoucherPage(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, pmap, tmap);
    }
    /**
     * 打印， 支持多层表头
     * <p>
     * columnnames参数如：{"科目名称", "期初_借方", "期初_贷方", "本期发生_借方", "本期发生_贷方", …}
     * </p>
     *
     * @param kmmap
     * @param zzvos
     * @param title
     * @param columns
     * @param columnnames 列标题中"_"作为上下表头行的分隔符，相邻两列的标题名中的_前缀相同，即认为合并上层表头行的相邻单元格
     * @param widths
     * @param pmap
     * @throws DocumentException
     * @throws IOException
     */
    public void printHz(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String title, String[] columns,
                        String[] columnnames, int[] widths, Integer pagecount, String type, Map<String, String> invmaps, Map<String, String> pmap,
                        Map<String, String> tmap) throws DocumentException, IOException {
        if (pmap.get("type").equals("1"))//A4纸张
            printGroup(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, invmaps, pmap, tmap); // A4纸张打印
        else if (pmap.get("type").equals("2"))//B5纸张
            printB5(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, invmaps, pmap, tmap);
        else if (pmap.get("type").equals("4")) {//A5纸张
            printA5(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, invmaps, pmap, tmap);
        } else//默认是凭证纸张
            printVoucherPage(kmmap, zzvos, title, columns, columnnames, null, widths, pagecount, pmap, tmap);
    }

    // 凭证纸版
    public void printVoucherPage(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, String[] columns,
                                 String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount, Map<String, String> pmap, Map<String, String> tmap)
            throws DocumentException, IOException {
        BaseFont bf = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/font/simkai.ttf
        BaseFont bf_Bold = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        Font titleFonts = new Font(bf_Bold, 12, Font.BOLD);
        Font tableBodyFounts = new Font(bf, 9, Font.NORMAL);
        Font tableHeadFounts = new Font(bf, 9, Font.NORMAL);
        float totalAmountHight = 13f;
        float totalMnyHight = 13f;
        Document document = null;
        Rectangle pageSize = new Rectangle(595f, 340f);
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
        document = new Document(pageSize, leftsize, 15, topsize, 4);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        Map<String, Float> totalwidthmap = new HashMap<String, Float>();
        int pageNum = 1;
        Chunk printdate = null;
        document.open();
        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            para.put("tableheight", (float) 2.0);// 默认是2
            if (kmmap != null && kmmap.size() > 0) { // ----按科目分页打印----
                for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) { //
                    List<SuperVO> kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
                    Paragraph head = new Paragraph();
                    Chunk corp = null;
                    Chunk cdate = null;
                    Chunk bsh = null;
                    Chunk pzqj = null;
                    cdate = new Chunk(PrintUtil.getSpace(3) + "期间：" + kmList.get(0).getAttributeValue("titlePeriod")
                            + PrintUtil.getSpace(4), tableBodyFounts);
                    corp = new Chunk("公司：" + kmList.get(0).getAttributeValue("gs") + PrintUtil.getSpace(4),
                            tableBodyFounts);
                    head.add(cdate);
                    head.add(corp);
                    if (titlename.equals("科 目 汇 总 表")) {
                        creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
                    }
                    head.setAlignment(Element.ALIGN_LEFT);
                    document.add(Chunk.NEWLINE);
                    document.add(head);
                    List<SuperVO> zzvosList = new ArrayList<SuperVO>();
                    //获取行数
                    int line = getA4LineNotPage(document.getPageSize().getHeight(), para.get("tableheight"), topsize, (float) para.get("totalMnyHight"), titlename);
                    if (titlename.startsWith("出入库明细表") || titlename.startsWith("库存成本表")) {
                        line = line - 1;
                    }
                    for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                        zzvosList.add(kmList.get(i));
                        if ((i + 1) % line == 0 && i != 0) { // 每页显示行数
                            SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
                            // 把数据放进数组。 写入表格
                            PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr,
                                    widths, pagecount, titlename, totalwidthmap);
                            document.add(table); // 打印表格
                            zzvosList.clear();
                            if (i < zzvos.length - 1) {
                                document.newPage();
                                document.add(Chunk.NEWLINE);
                                document.add(head);
                            }
                        }
                    }
                    SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
                    PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                            pagecount, titlename, totalwidthmap);
                    document.add(table);
                    document.newPage(); // 新的一页 导出下一个科目
                }
            }
            if (zzvos != null && zzvos.length > 0) { // ---- 不分页打印----
                Paragraph head = new Paragraph();
                Chunk corp = null;
                Chunk cdate = null;
                Chunk bsh = null;
                Chunk pzqj = null;
                // 末尾信息
                printdate = new Chunk("打印日期：" + pmap.get("printdate") + PrintUtil.getSpace(9) + "第" + pageNum + "页", tableHeadFounts);
                cdate = new Chunk(PrintUtil.getSpace(3) + "期间：" + zzvos[0].getAttributeValue("titlePeriod")
                        + PrintUtil.getSpace(4), tableBodyFounts);
                corp = new Chunk("公司：" + zzvos[0].getAttributeValue("gs") + PrintUtil.getSpace(4), tableBodyFounts);
                if (titlename.equals("科 目 汇 总 表")) {
                    creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
                } else {
                    head.add(cdate);
                    head.add(corp);
                }
                head.setAlignment(Element.ALIGN_LEFT);
                document.add(Chunk.NEWLINE);
                document.add(head);
                List<SuperVO> zzvosList = new ArrayList<SuperVO>();
                int line = getA4LineNotPage(document.getPageSize().getHeight(), para.get("tableheight"), topsize, (float) para.get("totalMnyHight"), titlename);
                if (titlename.startsWith("出入库明细表") || titlename.startsWith("库存成本表")) {
                    line = line - 1;
                }
                //获取行数
                for (int i = 0; i < zzvos.length; i++) {
                    zzvosList.add(zzvos[i]);
                    if ((i + 1) % line == 0 && i != 0) {
                        SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
                        PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                                pagecount, titlename, totalwidthmap);
                        document.add(table);
//                        document.add(printdate);
                        createBottomText(titlename, pmap, tableHeadFounts, document, pageNum+"", corpVO, null);
                        zzvosList.clear();
                        if (i < zzvos.length - 1) {
                            document.newPage();
                            document.add(Chunk.NEWLINE);
                            document.add(head);
                            creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
                            pageNum++;
                            printdate = new Chunk("打印日期：" + pmap.get("printdate") + PrintUtil.getSpace(9) + "第" + pageNum + "页", tableHeadFounts);
                        }
                    }
                }
                SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
                if (zzvoArray != null && zzvoArray.length > 0) {//最后的页数
                    PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths, pagecount,
                            titlename, totalwidthmap);
                    document.add(table);
//                    document.add(printdate);
                    createBottomText(titlename, pmap, tableHeadFounts, document, pageNum+"", corpVO, null);
                }
            }

        } catch (Exception e) {
            throw e;
        } finally {
            document.close();
        }
        //输出文件
        outPutPdf(titlename, buffer);
    }

    private void outPutPdf(String titlename, ByteArrayOutputStream buffer) throws IOException {
        ServletOutputStream out = null;
        try {
            String filename = titlename.replace(" ", "");//titlename.replace(" ", "");
            if (filename.indexOf("*") >= 0) {
                filename = filename.replace("*", "");
            }
            if (filename.indexOf("_") >= 0) {
                filename = filename.replace("_", "");
            }
            if (filename.indexOf("FZMX") >= 0) {
                filename = filename.replace("FZMX", "");
            }
            if (filename.indexOf("/") >= 0) {
                filename = filename.replace("/", "、");
            }
            filename = filename + "_" + new DZFDate().toString();
            if (gvo != null && gvo.getPrintType() != null && gvo.getPrintType() == 1) {
                getResponse().setContentType("application/octet-stream");
                String contentDisposition = "attachment;filename=" + URLEncoder.encode(filename + ".pdf", "UTF-8")
                        + ";filename*=UTF-8''" + URLEncoder.encode(filename + ".pdf", "UTF-8");
                getResponse().addHeader("Content-Disposition", contentDisposition);
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                getResponse().setContentLength(buffer.size());
            } else {
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                String contentDisposition = "inline;filename=" + URLEncoder.encode(filename + ".pdf", "UTF-8")
                        + ";filename*=UTF-8''" + URLEncoder.encode(filename + ".pdf", "UTF-8");
                getResponse().setHeader("Content-Disposition", contentDisposition);
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                getResponse().setContentLength(buffer.size());
            }
            out = getResponse().getOutputStream();
            buffer.writeTo(out);
            buffer.flush();//flush 放在finally的时候流关闭失败报错
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }


    /**
     * 非科目明细账，总账，数量金额明细账，数量金额总账打印
     *
     * @param kmmap
     * @param zzvos
     * @param title
     * @param columns
     * @param columnnames
     * @param widths
     * @param pagecount
     * @param type
     * @param pmap        页面参数，比如字体大小等
     * @param tmap        表头参数 页签名称，等
     */
    public void printReport(SuperVO[] zzvos, String title, List<ColumnCellAttr> listattr, Integer pagecount,
                            String type, Map<String, String> pmap, Map<String, String> tmap) throws DocumentException, IOException {

        List<Integer> widthsize = new ArrayList<>();
        List<String> columnslist = new ArrayList<String>();
        List<String> columnnameslist = new ArrayList<String>();
        int count = 0;
        for (ColumnCellAttr cell : listattr) {
            if (!StringUtil.isEmpty(cell.getColumn())) {
                widthsize.add(cell.getWidth());
                columnslist.add(cell.getColumn());
                columnnameslist.add(cell.getColumname());
            }

        }
        int[] widths = new int[widthsize.size()];
        int countv = 0;
        for (Integer in : widthsize) {
            widths[countv] = in;
            countv++;
        }
        if (pmap.get("type").equals("1"))
            printGroup(null, zzvos, title, columnslist.toArray(new String[0]), columnnameslist.toArray(new String[0]), listattr, widths, pagecount, null, pmap, tmap); // A4纸张打印
        else if (pmap.get("type").equals("2"))
            printB5(null, zzvos, title, columnslist.toArray(new String[0]), columnnameslist.toArray(new String[0]), listattr, widths, pagecount, null, pmap, tmap);
    }

    /**
     * 由带分隔符_的columnsname解析得到能展示多层表头的columncellattrlist
     *
     * @param columnnames
     * @return
     * @author llh
     */
    public static HeaderColumnsInfo parseMultiheadColumns(String[] columnnames) {
        // 具体举例：A_B_C与A_B_D，合并前两层A和B；A_B_C和A_D_E，合并第一层的A；A_X_B和C_X_D，不合并（A下的X和B下的X没有关系，不合并）
        HeaderColumnsInfo headerInfo = new HeaderColumnsInfo();

        // 先解析一遍columnnames得到多层表头的层数
        int maxcount = 0; // 栏目名称中最多的"_"个数
        for (String column : columnnames) {
            int count = 0;
            for (int index = -1; (index = column.indexOf("_", index + 1)) != -1; count++)
                ;
            if (count > maxcount)
                maxcount = count;
        }
        int headrowcount = maxcount + 1;
        // 按行缓存，一行表头存一个colattrlist
        List<List<ColumnCellAttr>> calines = new ArrayList<List<ColumnCellAttr>>(); // {new
        // ArrayList<ColumnCellAttr>()};
        for (int i = 0; i < headrowcount; i++) {
            calines.add(new ArrayList<ColumnCellAttr>());
        }
        ColumnCellAttr colattr;
        for (String column : columnnames) {
            String[] arr = column.split("_");
            if (arr.length == 1) {
                colattr = new ColumnCellAttr(column);
                colattr.setRowspan(headrowcount); // headrowcount-i;
                calines.get(0).add(colattr);
            } else {
                String colname;
                List<ColumnCellAttr> caline;
                ColumnCellAttr prevca;
                boolean bothSame = true;
                for (int i = 0; i < arr.length; i++) {
                    colname = arr[i];
                    caline = calines.get(i); // 当前层表头项
                    prevca = caline.size() == 0 ? null : caline.get(caline.size() - 1); // 同层的左侧相邻单元格
                    // 与本层及以上的左侧相邻单元格的内容相同，且不是最后一层（最后一层不建议做横向合并）
                    bothSame = bothSame && (prevca != null && prevca.getColumname().equals(colname));
                    if (i < arr.length - 1 && bothSame) {
                        prevca.setColspan(prevca.getColspan() + 1); // 左侧单元格colspan+1
                        // colattr = prevca;
                    } else {
                        colattr = new ColumnCellAttr(colname);
                        colattr.setColspan(1); // colspan先设为1，待定
                        caline.add(colattr);

                        // 如果是此栏表头的最后一层，且未到最底部
                        if (i == arr.length - 1 && i < headrowcount - 1) {
                            colattr.setRowspan(headrowcount - i); // 设置rowspan到底
                        }
                    }
                }
            }
        }

        // 将各行表头栏目加入结果列表
        List<ColumnCellAttr> listattr = new ArrayList<ColumnCellAttr>();
        for (List<ColumnCellAttr> line : calines) {
            for (ColumnCellAttr col : line) {
                listattr.add(col);
            }
        }
        headerInfo.setColumnlist(listattr);
        headerInfo.setHeadrowcount(headrowcount);
        return headerInfo;
    }

    /**
     * 生成PDF表格
     * <p>
     * 可以传listattr，也可以传columnnames。当传columnnames时，走新的支持多层表头的逻辑。
     * </p>
     *
     * @param bvoArray
     * @param startIndex
     * @param para
     * @param columns
     * @param columnnames
     * @param listattr
     * @param withs
     * @param pagecount
     * @param tilename
     * @return
     * @throws DocumentException
     */
    private PdfPTable addTableByTzpzBvo(SuperVO[] bvoArray, int startIndex, Map<String, Object> para, String[] columns,
                                        String[] columnnames, List<ColumnCellAttr> listattr, int[] withs, Integer pagecount, String tilename,
                                        Map<String, Float> totalwidthmap) throws DocumentException {
        int headrowcount;
        if (listattr != null && listattr.size() > 0) { // 当传了listattr时，走原逻辑，自动判断是1层还是2层表头
            headrowcount = withs.length != listattr.size() ? 2 : 1;
            if (columns == null) {// columns为空，再从listattr取值
                List<String> columnlist = new ArrayList<String>();
                for (int i = 0; i < listattr.size(); i++) {
                    if (!StringUtil.isEmpty(listattr.get(i).getColumn())) {
                        columnlist.add(listattr.get(i).getColumn());
                    }
                }
                columns = columnlist.toArray(new String[0]);
            }
        } else {
            // 解析得到多层表头的栏目信息
            HeaderColumnsInfo headerInfo = parseMultiheadColumns(columnnames);
            listattr = headerInfo.getColumnlist();
            headrowcount = headerInfo.getHeadrowcount(); // 支持多层表头
        }

        PdfPTable table = new PdfPTable(withs.length);
        table.setHeaderRows(headrowcount);
        table.setSpacingBefore(para.get("tableheight") == null ? (float) 2 : (float) para.get("tableheight"));
        table.setWidthPercentage(100);
        table.setWidths(withs);

        // 表头
        float totalMnyHight_head = (float) para.get("totalMnyHight");
        if (firstlineheight != null && firstlineheight > 0) {
            totalMnyHight_head = firstlineheight;
        }
        addTabHead((Font) para.get("tableHeadFounts"), table, totalMnyHight_head, listattr, totalwidthmap);
        int line = 0;
        for (int j = startIndex; j < bvoArray.length; j++, line++) { // 表体
            SuperVO bvo = bvoArray[j];
            if (tilename.equals("入 库 单") || tilename.equals("出 库 单")) {
                if (!StringUtil.isEmpty(bvo.getAttributeValue("kmmc").toString())
                        && "合计".equals(bvo.getAttributeValue("kmmc").toString())) {
                    addTableMny((Font) para.get("tableHeadFounts"), table, bvo, (float) para.get("totalMnyHight"),
                            columns, tilename, totalwidthmap);
                } else {
                    addTableMny((Font) para.get("tableBodyFounts"), table, bvo, (float) para.get("totalMnyHight"),
                            columns, tilename, totalwidthmap);
                }
            } else {
                addTableMny((Font) para.get("tableBodyFounts"), table, bvo, (float) para.get("totalMnyHight"), columns,
                        tilename, totalwidthmap);
            }
        }
        return table;
    }


    private void addTableMny(Font fonts, PdfPTable table, SuperVO bvo, float totalMnyHight, String[] columns,
                             String tilename, Map<String, Float> totalwidthmap) {
        try {
            PdfPCell cell = null;
            String tvalue = "";
            for (String key : columns) {
                if (StringUtil.isEmpty(key)) {
                    continue;
                }
                if (bvo.getClass().getDeclaredField(key).getType() == DZFDouble.class) {
                    if (bvo.getAttributeValue(key) == null || ((DZFDouble) bvo.getAttributeValue(key)).doubleValue() == 0.00) {
                        if (tilename.equals("财 务 概 要 信 息")) {
                            int colspan = bvo.getAttributeValue("colspan") == null ? 1
                                    : (Integer) bvo.getAttributeValue("colspan");
                            cell = new PdfPCell(new Phrase("", fonts));
                            if (colspan > 1) {
                                if (colspan == 7) {
                                    if (key.equals("bybl") || key.equals("bnljbl") || key.equals("byje")) {
                                        continue;
                                    }
                                    cell.setColspan(7);
                                } else if (colspan == 4) {
                                    if (key.equals("bybl") || key.equals("bnljbl") || key.equals("byje")) {
                                        continue;
                                    }
                                    if (key.equals("bnljje")) {
                                        cell.setColspan(4);
                                    }
                                } else if (colspan == 2) {
                                    if (key.equals("bybl") || key.equals("bnljbl")) {
                                        continue;
                                    }
                                    if (key.equals("bnljje") || key.equals("byje")) {
                                        cell.setColspan(2);
                                    }
                                }
                            }
                        } else {
                            if (tilename.equals("利 润 表") &&
                                    "项　　　　目".equals(bvo.getAttributeValue("xm")) && "byje".equals(key)) {
                                if (getBasecolor() != null) {
                                    cell.setBorderColor(getBasecolor());
                                }
                                cell = new PdfPCell(new Phrase("上年实际数", fonts));
                                cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                                cell.setHorizontalAlignment(Element.ALIGN_CENTER); //水平居中
                                if (totalMnyHight >= 0) {
                                    cell.setFixedHeight(totalMnyHight);
                                }
                                table.addCell(cell);
                                continue;
                            } else {
                                // 如果是空则显示0
                                if (bshowzero != null && bshowzero.booleanValue()) {
                                    cell = new PdfPCell(new Phrase("0.00", fonts));
                                } else {
                                    cell = new PdfPCell(new Phrase("", fonts));
                                }

                            }
                        }

                    } else {
                        if (tilename.equals("数 量 金 额 明 细 账") || tilename.equals("数 量 金 额 总 账")
                                || tilename.equals("出 库 单") || tilename.equals("入 库 单") || tilename.equals("库存成本表")
                                || tilename.equals("科目期初") || tilename.equals("入 库 单")
                                || tilename.equals("出 库 单") || tilename.indexOf(
                                "库存明细账") >= 0 || tilename.equals("出入库明细表") || tilename.equals("毛利率统计表") || tilename.equals("库存汇总表")/*
                         * || tilename.equals(
                         * "工 资 表")
                         */) {
                            String jdslStr[] = new String[]{"nnum", "ndnum", "nynum",
                                    "qcnum", "bqjfnum", "bqdfnum", "bnjfnum", "bndfnum", "qmnum",
                                    "num", "sl", "bnqcnum", "bndffsnum", "monthqmnum", "bnfsnum", "qcsl", "srsl",
                                    "fcsl", "jcsl", "cksl"};
                            String jddjStr[] = new String[]{//单价
                                    "nprice", "ndprice", "nyprice",
                                    "qcprice", "qmprice", "qcdj", "srdj", "fcdj", "jcdj", "ckdj", "xsdj"
                            };

                            List<String> jdsllist = Arrays.asList(jdslStr);//数量
                            List<String> jddjlist = Arrays.asList(jddjStr);//单价

                            String value = "";
                            if (jdsllist.contains(key)) {
                                value = getPrecision(IParameterConstants.DZF009, (String) bvo.getAttributeValue("pk_corp"));
                            } else if (jddjlist.contains(key)) {
                                value = getPrecision(IParameterConstants.DZF010, (String) bvo.getAttributeValue("pk_corp"));
                            } else {
                                value = "2";
                            }


                            if (tilename.equals("毛利率统计表") && "mll".equals(key)) {
                                DecimalFormat df = new DecimalFormat("0.00%");
                                tvalue = df.format(((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
                            } else {
                                tvalue = String.format("%1$,." + value + "f",//%1$,.2f
                                        ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
                            }

                            cell = new PdfPCell(new Phrase(tvalue,
                                    PrintUtil.getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight, DZFBoolean.TRUE)));
                        } else if (tilename.equals("序 时 账") && (key.equals("hl") || key.equals("ybjf") || key.equals("ybdf"))) {
                            String value = "";
                            if (key.equals("hl")) {
                                value = getPrecision(IParameterConstants.DZF011, (String) bvo.getAttributeValue("pk_corp"));
                            } else {
                                value = "4";
                            }
                            tvalue = String.format("%1$,." + value + "f", ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
                            cell = new PdfPCell(new Phrase(tvalue,
                                    PrintUtil.getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight, DZFBoolean.TRUE)));
                        } else if (tilename.equals("财 务 概 要 信 息")) {
                            tvalue = String.format("%1$,.2f", ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
                            String svalue = (String) bvo.getAttributeValue("s" + key);
                            if (!StringUtil.isEmpty(svalue)) {
                                tvalue = svalue;
                            }
                            boolean bcenter = false;
                            if ("10".equals(bvo.getAttributeValue("hs")) && StringUtil.isEmpty((String)bvo.getAttributeValue("xm"))) {
                                if (bvo.getAttributeValue("bnljje") != null && "bnljje".equals(key)) {
                                    if (bvo.getAttributeValue("bnljje").equals(new DZFDouble(100))) {
                                        tvalue = "销项发票";
                                        bcenter = true;
                                    }
                                }
                                if (bvo.getAttributeValue("byje") != null  && "byje".equals(key)) {
                                    if (bvo.getAttributeValue("byje").equals(new DZFDouble(101))) {
                                        tvalue = "进项发票";
                                        bcenter = true;
                                    }
                                }
                            }
                            cell = new PdfPCell(new Phrase(tvalue, PrintUtil.getAutoFont(fonts, tvalue,
                                    totalwidthmap.get(key), totalMnyHight, DZFBoolean.TRUE)));
                            if (bcenter) {
                                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                            }
                            int colspan = bvo.getAttributeValue("colspan") == null ? 1
                                    : (Integer) bvo.getAttributeValue("colspan");
                            if (colspan > 1) {
                                if (colspan == 7) {
                                    if (key.equals("bybl") || key.equals("bnljbl") || key.equals("byje")) {
                                        continue;
                                    }
                                    cell.setColspan(7);
                                } else if (colspan == 4) {
                                    if (key.equals("bybl") || key.equals("bnljbl") || key.equals("byje")) {
                                        continue;
                                    }
                                    if (key.equals("bnljje")) {
                                        cell.setColspan(4);
                                    }
                                } else if (colspan == 2) {
                                    if (key.equals("bybl") || key.equals("bnljbl")) {
                                        continue;
                                    }
                                    if (key.equals("bnljje") || key.equals("byje")) {
                                        cell.setColspan(2);
                                    }
                                }
                            }
                        } else {
                            if (key.equals("ybjf") || key.equals("ybdf") || key.equals("ybye")
                                    || key.equals("ybqcdf") || key.equals("ybqcjf") || key.equals("ybfsjf") || key.equals("ybfsdf")
                                    || key.equals("ybjftotal") || key.equals("ybdftotal") || key.equals("ybqmjf") || key.equals("ybqmdf")
                                    || key.equals("ybqcyejf") || key.equals("ybqcyedf") || key.equals("ybbqfsjf") || key.equals("ybbqfsdf")
                                    || key.equals("ybbnljjf") || key.equals("ybbnljdf") || key.equals("ybqmyejf") || key.equals("ybqmyedf")
                            ) {
                                tvalue = String.format("%1$,.4f", ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
                            } else {
                                tvalue = String.format("%1$,.2f", ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
                            }
                            cell = new PdfPCell(new Phrase(tvalue,
                                    PrintUtil.getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight, DZFBoolean.TRUE)));
                        }
                    }
                    if (getBasecolor() != null) {
                        cell.setBorderColor(getBasecolor());
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    // 如果设置为居中，则不居右显示
                    if (cell.getHorizontalAlignment() == Element.ALIGN_CENTER) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
//					cell.setFixedHeight(totalMnyHight);
                    if (totalMnyHight >= 0) {
                        cell.setFixedHeight(totalMnyHight);
                    }
                    table.addCell(cell);
                } else if (bvo.getClass().getDeclaredField(key).getType() == DZFDate.class) {
                    if (bvo.getAttributeValue(key) == null) {
                        cell = new PdfPCell(new Phrase("", fonts));
                    } else {
                        tvalue = ((DZFDate) bvo.getAttributeValue(key)).toString();
                        cell = new PdfPCell(
                                new Phrase(tvalue, PrintUtil.getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight, DZFBoolean.FALSE)));
                    }
                    if (getBasecolor() != null) {
                        cell.setBorderColor(getBasecolor());
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//					cell.setFixedHeight(totalMnyHight);
                    if (totalMnyHight >= 0) {
                        cell.setFixedHeight(totalMnyHight);
                    }
                    table.addCell(cell);// getDeclaredField可获取所有定义变量，而getField只能获取public的属性变量
                } else if (bvo.getClass().getDeclaredField(key).getType() == Integer.class) {
                    if ("direct".equals(key)) {
                        if (bvo.getAttributeValue("direct").toString().equals("0")) {
                            cell = new PdfPCell(new Phrase("借", fonts));
                        } else {
                            cell = new PdfPCell(new Phrase("贷".toString(), fonts));
                        }
                    } else {
                        tvalue = (bvo.getAttributeValue(key)) == null ? "" : (bvo.getAttributeValue(key)).toString();
                        cell = new PdfPCell(
                                new Phrase(tvalue, PrintUtil.getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight, DZFBoolean.FALSE)));
                    }
                    if (getBasecolor() != null) {
                        cell.setBorderColor(getBasecolor());
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//					cell.setFixedHeight(totalMnyHight);
                    if (totalMnyHight >= 0) {
                        cell.setFixedHeight(totalMnyHight);
                    }
                    table.addCell(cell);
                } else if (bvo.getClass().getDeclaredField(key).getType() == DZFBoolean.class) {
                    if (bvo.getAttributeValue(key) != null) {
                        if ("Y".equals(bvo.getAttributeValue(key).toString())) {// 通过判断值为Y或N来输出“是”“否” bvo.getAttributeValue(key).toString() == "Y"
                            cell = new PdfPCell(new Phrase("是", fonts));
                        } else {
                            cell = new PdfPCell(new Phrase("否", fonts));
                        }
                    } else {
                        cell = new PdfPCell(new Phrase("否", fonts));
                    }
                    if (getBasecolor() != null) {
                        cell.setBorderColor(getBasecolor());
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
//					cell.setFixedHeight(totalMnyHight);
                    if (totalMnyHight >= 0) {
                        cell.setFixedHeight(totalMnyHight);
                    }
                    table.addCell(cell);
                } else {
                    tvalue = (String) bvo.getAttributeValue(key);
                    cell = new PdfPCell(
                            new Phrase(tvalue, PrintUtil.getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight, DZFBoolean.FALSE)));
                    if (tilename.equals("科目总账") && key.equals("km")) {
                        if (bvo.getAttributeValue(key) == null) {
                            continue;
                        }
                        cell.setRowspan(bvo.getAttributeValue("rowspan") == null ? 1
                                : (Integer) bvo.getAttributeValue("rowspan"));
                    } else if (tilename.equals("财 务 概 要 信 息")) {
                        if (key.equals("hs") || key.equals("xmfl")) {
                            if (bvo.getAttributeValue("rowspan") == null) {
                                continue;
                            }
                            cell.setRowspan(bvo.getAttributeValue("rowspan") == null ? 1
                                    : (Integer) bvo.getAttributeValue("rowspan"));
                        } else if (key.equals("xm")) {
                            int colspan = bvo.getAttributeValue("colspan") == null ? 1
                                    : (Integer) bvo.getAttributeValue("colspan");
                            if (colspan == 7) {
                                continue;
                            }
                        }
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    if (tilename.equals("资 产 负 债 表") && ("hc1".equals(key) || "hc2".equals(key))) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if ((tilename.equals("利 润 表") || tilename.equals("分 部 利 润 表") || tilename.equals("利 润 表 季 报"))  && "hs".equals(key)) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else if ((tilename.equals("现 金 流 量 表") || tilename.equals("现 金 流 量 表 季 报")) && "hc".equals(key)) {
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    } else {
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }
                    if (getBasecolor() != null) {
                        cell.setBorderColor(getBasecolor());
                    }
                    if (totalMnyHight >= 0) {
                        cell.setFixedHeight(totalMnyHight);
                    }
                    table.addCell(cell);
                }
            }
        } catch (NoSuchFieldException e) {
            log.error("错误", e);
        } catch (SecurityException e) {
            log.error("错误", e);
        }
    }

    private synchronized String getPrecision(String type, String pk_corp) {
        if (precisionMap == null) {
            precisionMap = new HashMap<String, String>();
        }

        String str = "";

        if (!precisionMap.containsKey(type)) {
            if (!StringUtil.isEmpty(pk_corp)) {
                str = zxkjPlatformService.queryParamterValueByCode(pk_corp, type);
            }

            if (StringUtil.isEmpty(str)) {
                str = "4";//默认为4
            }

            precisionMap.put(type, str);
        } else {
            str = precisionMap.get(type);
        }

        return str;
    }

    public void addTabHead(Font fonts10_bold, PdfPTable table, float totalMnyHight, List<ColumnCellAttr> listattr,
                           Map<String, Float> totalwidthmap) {
        PdfPCell cell = null;
        int count = 0;
        for (ColumnCellAttr columnsname : listattr) {
            cell = new PdfPCell(new Paragraph(columnsname.getColumname(), fonts10_bold));
            if (listattr.get(count).getColspan() != null && listattr.get(count).getColspan().intValue() > 0) {// 合并
                cell.setColspan(listattr.get(count).getColspan());
            }
            if (listattr.get(count).getRowspan() != null && listattr.get(count).getRowspan().intValue() > 0) {// 空行
                cell.setPadding(0);
                cell.setFixedHeight(totalMnyHight * listattr.get(count).getRowspan());
                cell.setRowspan(listattr.get(count).getRowspan());
            }
            if (getBasecolor() != null) {

                cell.setBorderColor(getBasecolor());
            }
            cell.setFixedHeight(totalMnyHight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            count++;
        }
    }

    /**
     * A4宽 打印
     *
     * @param kmmap              分页打印信息
     * @param zzvos              不分页打印信息
     * @param titlename          显示表头
     * @param columns            显示的字读
     * @param columnnames显示的字读名称
     * @param listattr           字段属性
     * @param widths             字段宽度
     * @param pagecount          每页显示行数
     * @param invmaps            数量金额明细账使用
     * @param pmap               页尾信息
     * @param tmap               页头信息
     * @throws DocumentException
     * @throws IOException
     */
    public void printGroup(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, String[] columns,
                           String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                           Map<String, String> invmaps, Map<String, String> pmap, Map<String, String> tmap)
            throws DocumentException, IOException {
        Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
        int font = Integer.parseInt(pmap.get("font"));
        Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
        Font tableHeadFounts = getTableHeadFount();
        if (tableHeadFounts == null) {
            tableHeadFounts = new Font(bf, font, Font.BOLD);
        }
        float totalAmountHight = 13f;
        float totalMnyHight = 0;
        if (getLineheight() == null) {
            totalMnyHight = 22f;// 设置双倍行距解决科目显示不完整问题
        } else {
            totalMnyHight = getLineheight();
        }
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float rightsize = 15;
        if (pmap.get("right") != null) {
            rightsize = (float) (Float.parseFloat(pmap.get("right")) * 2.83);
        }

        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);


        Document document = null;
        Rectangle pageSize = null;
        float totaltablewidth = 0;
        if (iscross != null && iscross.booleanValue()) {
            pageSize = new Rectangle((PageSize.A4.getHeight()), (PageSize.A4.getWidth()));
            document = new Document(pageSize, leftsize, rightsize, topsize, 10);
            totaltablewidth = PageSize.A4.getHeight() - leftsize - 15;
        } else {
            pageSize = PageSize.A4;
            document = new Document(pageSize, leftsize, rightsize, topsize, 4);
            totaltablewidth = PageSize.A4.getWidth() - leftsize - 15;
        }

        Map<String, Float> totalwidthmap = new HashMap<>();
        totalwidthmap = PrintUtil.calculateWidths(columns, columnnames, widths, totaltablewidth);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.open();
        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            if (kmmap != null && kmmap.size() > 0) { // ----按科目分页打印----
                printGroup1(kmmap, titlename, columns, columnnames, listattr, widths, pagecount, invmaps, pmap, tmap,
                        titleFonts, tableHeadFounts, leftsize, rightsize, topsize, document, totalwidthmap, writer, para);
            } else {
                kmmap = new HashMap<>();
                kmmap.put(titlename, Arrays.asList(zzvos));
                printGroup1(kmmap, titlename, columns, columnnames, listattr, widths, pagecount, invmaps, pmap, tmap,
                        titleFonts, tableHeadFounts, leftsize, rightsize, topsize, document, totalwidthmap, writer, para);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            throw e;
        } finally {
            document.close();
        }
        outPutPdf(titlename, buffer);
    }

    /**
     * 财报特殊的表尾
     *
     * @param pmap
     * @param tableHeadFounts
     * @param document
     * @param totalMnyHight
     */
    public void createBottomText(String titlename, Map<String, String> pmap, Font tableHeadFounts, Document document,
                                 String pageNum, CorpVO corpvo, List<SuperVO> kmList) {
        List<String[]> bottomlist = new ArrayList<String[]>();
//        String defaultvalue = "打印日期：" + pmap.get("printdate") + PrintUtil.getSpace(9) + "第" + pageNum + "页";
//        String[] defaultvlaues = new String[]{defaultvalue};
        String[] defaultvlaues = new String[3];
        if(StringUtil.isEmpty(pmap.get("printdate"))){
            defaultvlaues[0] = "";
            defaultvlaues[1] = "第" + pageNum + "页";
            defaultvlaues[2] = "";
        }else{
            defaultvlaues[0] = "打印日期：" + pmap.get("printdate");
            defaultvlaues[1] =  "第" + pageNum + "页";
            defaultvlaues[2] = "";
        }
        if ("资 产 负 债 表".equals(titlename) || "利 润 表".equals(titlename) || "利 润 表 季 报".equals(titlename)
                || "现 金 流 量 表".equals(titlename) || "现 金 流 量 表 季 报".equals(titlename)) {
            String dwzrr = pmap.containsKey("单位负责人") ? pmap.get("单位负责人") : PrintUtil.getSpace(10);
            String cwzrr = pmap.containsKey("财务负责人") ? pmap.get("财务负责人") : PrintUtil.getSpace(10);
            String zbr = pmap.containsKey("制表人") ? pmap.get("制表人") : PrintUtil.getSpace(10);
            String[] value1 = new String[]{"单位负责人：" + dwzrr, "财务负责人: " + cwzrr, "制表人: " + zbr};
            bottomlist.add(value1);
            bottomlist.add(defaultvlaues);
        } else if ("入 库 单".equals(titlename) || "出 库 单".equals(titlename)) {
            // 老库存
            if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle() != 1) {
                bottomlist.add(defaultvlaues);
            } else {
                bottomlist.add(new String[]{"公司：" + corpvo.getUnitname(), getBottomItemIc(pmap, kmList, tableHeadFounts, corpvo)});
            }
        } else {
            bottomlist.add(defaultvlaues);
        }
        //打印信息
        createTopAndBottom(tableHeadFounts, document, bottomlist);
    }

    /**
     * 标题
     *
     * @param tableHeadFounts
     * @param document
     * @param value
     * @param titleFonts
     */
    private void createBt(Font tableHeadFounts, Document document, String[] value, Font titleFonts) {
        //翻倍
        int num = (value.length - 1) * 2 + 1;
        PdfPTable tablet = new PdfPTable(num);
        tablet.setWidthPercentage(100);
        int[] widths = new int[num];
        int start = 0;
        for (int i = 0; i < num; i++) {
            PdfPCell cell1 = null;
            widths[i] = 1;
            start = (i - value.length + 1);
            if (start == 0) {
                if (value[start].length() > 30) {
                    cell1 = new PdfPCell(new Phrase(value[start], new Font(bf_Bold, 15, Font.BOLD)));
                } else {
                    cell1 = new PdfPCell(new Phrase(value[start], titleFonts));
                }
                cell1.setHorizontalAlignment(Element.ALIGN_CENTER); // 水平居中
                widths[i] = 2;
            } else if (start > 0) {
                cell1 = new PdfPCell(new Phrase(value[start], tableHeadFounts));
                cell1.setHorizontalAlignment(Element.ALIGN_RIGHT); // 水平居中
            } else {
                cell1 = new PdfPCell(new Phrase("", tableHeadFounts));
            }
            cell1.disableBorderSide(15);// 不需要边框
            cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
            cell1.setBorderWidthLeft(0.0f);
            cell1.setPadding(0f);
            cell1.setFixedHeight(20);
            tablet.addCell(cell1);
        }
        try {
            tablet.setWidths(widths);
            document.add(tablet);
        } catch (DocumentException e) {

        }
    }

    private void createTopAndBottom(Font tableHeadFounts, Document document, List<String[]> valuelist) {
        for (String[] value : valuelist) {
            PdfPTable tablet = new PdfPTable(value.length);
            tablet.setWidthPercentage(100);
            for (int i = 0; i < value.length; i++) {
                PdfPCell cell1 = new PdfPCell(new Phrase(value[i], tableHeadFounts));
                cell1.disableBorderSide(15);// 不需要边框
                if (i == 0) {
                    cell1.setHorizontalAlignment(Element.ALIGN_LEFT); // 水平居中
                } else if (i == value.length - 1) {
                    cell1.setHorizontalAlignment(Element.ALIGN_RIGHT); // 水平居中
                } else {
                    cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
                }
                cell1.setPadding(0f);
                cell1.setPaddingTop(2f);
                cell1.setFixedHeight(20);
                cell1.setVerticalAlignment(Element.ALIGN_BOTTOM);
                tablet.addCell(cell1);
            }
            try {
                document.add(tablet);
            } catch (DocumentException e) {

            }
        }
    }

    private void printGroup1(Map<String, List<SuperVO>> kmmap, String titlename, String[] columns, String[] columnnames,
                             List<ColumnCellAttr> listattr, int[] widths, Integer pagecount, Map<String, String> invmaps,
                             Map<String, String> pmap, Map<String, String> tmap, Font titleFonts, Font tableHeadFounts, float leftsize, float rightsize,
                             float topsize, Document document, Map<String, Float> totalwidthmap, PdfWriter writer,
                             Map<String, Object> para) throws DocumentException {
        int pageNum = 1;
        List<SuperVO> kmList = null;
        List<SuperVO> zzvosList = null;
        SuperVO[] zzvoArray = null;
        Object[] titleobjs = null;
        PdfPTable table = null;
        for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) {
            if ("资 产 负 债 表".equals(titlename) || "利 润 表".equals(titlename) || "分 部 利 润 表".equals(titlename)
                    || "现 金 流 量 表".equals(titlename) || titlename.startsWith("工 资 表")) {
                tmap.put("期间", kmEntry.getKey());
            }
            kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
            // 插入头部信息
            titleobjs = putA4TitleMsg(document, titlename, invmaps, tmap, titleFonts, tableHeadFounts, leftsize, rightsize,
                    topsize, writer, para, kmList);
            tmap = (Map<String, String>) titleobjs[0];
            String titlenametemp = (String) titleobjs[1];
            // 末尾信息
            document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));

            int line = getA4LineNotPage(document.getPageSize().getHeight(), para.get("tableheight"), topsize,
                    (float) para.get("totalMnyHight"), titlename);// 获取行数
            if (titlename.startsWith("出入库明细表") || titlename.startsWith("库存成本表")) {
                line = line - 1;
            }
            zzvosList = new ArrayList<SuperVO>();
            for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                zzvosList.add(kmList.get(i));
                if ((i + 1) % line == 0 && i != 0) { // 每页只打22行
                    zzvoArray = zzvosList.toArray(new SuperVO[0]);
                    // 把22行数据放进数组。 写入表格
                    table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths, pagecount,
                            titlename, totalwidthmap);
                    document.add(table); // 打印表格
                    createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                    zzvosList.clear();
                    if (i < kmList.size() - 1) {// 整数页最后一条打完不新建页
                        pageNum++;
                        document.newPage();
                        document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
                        creatTitle(document, tmap, tableHeadFounts, titlenametemp, titleFonts);
                    }
                }
            }
            zzvoArray = zzvosList.toArray(new SuperVO[0]);
            if (zzvoArray != null && zzvoArray.length > 0) {// 最后一页
                table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths, pagecount,
                        titlename, totalwidthmap);
                document.add(table);
                createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
            }
            document.newPage(); // 新的一页 导出下一个科目
            pageNum++;
        }
    }

    /**
     * 不分页条数
     *
     * @param zzvos
     * @param titlename
     * @param listattr
     * @param widths
     * @return
     */
    private int getA4LineNotPage(float pagesize, Object tableheight, float topsize, float totalMnyHight, String titlename) {
        //120是默认的head 和 title,和表尾的 高度
        if (tableheight == null) {
            tableheight = 2f;
        }
        float defaultheight = 120f;
//        float defaultheight = 80f;
//        if (!StringUtil.isEmpty(titlename)) {
//            if ("发 生 额 及 余 额 表".equals(titlename) || "辅助余额表".equals(titlename) || titlename.indexOf("科目明细账") > 0) {
//                defaultheight = 120f;
//            }
//        }
        float value = (pagesize - topsize - (float) tableheight - defaultheight) / totalMnyHight;
        if("财 务 概 要 信 息".equals(titlename)){
            return 100;
        }
        return new Float(value).intValue();
    }

    private Object[] putA4TitleMsg(Document document, String titlename, Map<String, String> invmaps, Map<String, String> tmap,
                                   Font titleFonts, Font tableHeadFounts, float leftsize, float rightsize, float topsize, PdfWriter writer,
                                   Map<String, Object> para, List<SuperVO> kmList) {
        Object[] objs = putRptHeadMap(tmap, titlename, kmList, para, invmaps, 0);
        tmap = (Map<String, String>) objs[0];
        titlename = (String) objs[1];
        creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
        return new Object[]{tmap, titlename};
    }

    /**
     * A4 和B5 默认是一样的
     *
     * @param tmap
     * @param titlename
     * @param kmList
     * @param para
     * @param invmaps
     */
    private Object[] putRptHeadMap(Map<String, String> tmap, String titlename, List<SuperVO> kmList,
                                   Map<String, Object> para, Map<String, String> invmaps, Integer pagetype) {

        if (!StringUtil.isEmpty(titlename) && (titlename.indexOf("科目明细账") > 0
                || titlename.indexOf("科目总账") > 0)) {
            String kmbm = "(" + kmList.get(0).getAttributeValue("kmbm").toString() + ")";
            if (titlename.indexOf("科目明细账") > 0) {
                titlename = kmList.get(0).getAttributeValue("km").toString().replace(kmbm, "") + "科目明细账";
            } else if (titlename.indexOf("科目总账") > 0) {
                titlename = kmList.get(0).getAttributeValue("km").toString().replace(kmbm, "") + "科目总账";
            }

            String dw = "";
            if (tmap != null && tmap.containsKey("单位")) {
                dw = tmap.get("单位");
            }

            tmap = new LinkedHashMap<String, String>();
            tmap.put("公司", kmList.get(0).getAttributeValue("gs").toString());
            tmap.put("期间", kmList.get(0).getAttributeValue("titlePeriod").toString());
            tmap.put("科目", "Enter" + kmList.get(0).getAttributeValue("kmfullname").toString());
            if (!StringUtil.isEmptyWithTrim(dw)) {
                tmap.put("单位", dw);
            }
        } else if (!StringUtil.isEmpty(titlename) && titlename.indexOf("库存明细账") > 0) {
            titlename = (String) kmList.get(0).getAttributeValue("spmc") + "库存明细账";
        } else if ("数 量 金 额 明 细 账".equals(titlename)) {
            tmap = new LinkedHashMap<String, String>();
            if (invmaps.get("iskucun").equals("false")) {
                String km = kmList.get(0).getAttributeValue("kmbm") + "/"
                        + kmList.get(0).getAttributeValue("kmmc");
                tmap.put("公司", corpVO.getUnitname());
                String jldw = (String) (kmList.get(0).getAttributeValue("jldw") == null ? ""
                        : kmList.get(0).getAttributeValue("jldw"));
                tmap.put("期间", (String) kmList.get(0).getAttributeValue("titlePeriod"));
                tmap.put("科目", "Enter" + km);
                tmap.put("计量单位", jldw);
            } else {
                String km = kmList.get(0).getAttributeValue("kmbm") + "/"
                        + kmList.get(0).getAttributeValue("kmmc");

                tmap.put("公司", corpVO.getUnitname());
                String jldw = (String) (kmList.get(0).getAttributeValue("jldw") == null ? ""
                        : kmList.get(0).getAttributeValue("jldw"));
                tmap.put("期间", (String) kmList.get(0).getAttributeValue("titlePeriod"));
                tmap.put("科目", "Enter" + km);
                tmap.put("计量单位", jldw);
            }
//            if (pagetype == 0) {//A4(换行使用)
//                if (iscross != null && iscross.booleanValue()) {
//                    para.put("tableheight", (float) 13);
//                } else {
//                    para.put("tableheight", (float) 14);
//                }
//            } else {
//                para.put("tableheight", (float) 10);
//            }
        } else if ("入 库 单".equals(titlename) || "出 库 单".equals(titlename)) {
            SuperVO vo = kmList.get(0);
            if (vo != null) {
                // 老模式 启用库存
                if (corpVO.getIbuildicstyle() == null || corpVO.getIbuildicstyle() != 1) {
                    tmap.put("公司", corpVO.getUnitname());
                    String speriod = DateUtils.getPeriod((DZFDate) vo.getAttributeValue("dbilldate"));
                    tmap.put("期间", speriod);
                    String username = getUsername(kmList);
                    tmap.put("制单人", username);
                } else {
                    tmap.put("单据号", (String) (kmList.get(0).getAttributeValue("dbillid") == null ? "  "
                            : kmList.get(0).getAttributeValue("dbillid")));
                    if (invmaps == null || invmaps.isEmpty() || invmaps.get("isHiddenPzh") == null || !invmaps.get("isHiddenPzh").equals("Y")) {
                        String pzh = getPzh(kmList);
                        tmap.put("凭证号", pzh);
                    }
                    String custname = (String) kmList.get(0).getAttributeValue("custname");
                    if ("入 库 单".equals(titlename)) {
                        tmap.put("供应商", StringUtil.isEmpty(custname) ? "Enter" + "  " : "Enter" + custname);
                    } else {
                        tmap.put("客户", StringUtil.isEmpty(custname) ? "Enter" + "  " : "Enter" + custname);
                    }
                    String speriod = DateUtils.getPeriod((DZFDate) vo.getAttributeValue("dbilldate"));
                    tmap.put("期间", speriod);
                }
            }
        } else if ("资 产 摊 销 明 细 账".equals(titlename) || "折 旧 明 细 账".equals(titlename)) {
            if ("Y".equals(ispaging)) {
//                para.put("tableheight", (float) 14);
                tmap.put("资产名称", "Enter" + kmList.get(0).getAttributeValue("assetname").toString());
                tmap.put("资产类别", kmList.get(0).getAttributeValue("catename").toString());
                tmap.put("资产编码", kmList.get(0).getAttributeValue("assetcode").toString());
            }
        } else if ((!StringUtil.isEmpty(titlename) && titlename.indexOf("FZMX_辅助明细账") >= 0)) {
//        	  para.put("tableheight", (float) 12);//调整表头显示
            if (kmList.get(0).getAttributeValue("kmname") != null) {
                titlename = kmList.get(0).getAttributeValue("kmname").toString() + "辅助明细账";
            } else {
                titlename = kmList.get(0).getAttributeValue("fzname").toString() + "辅助明细账";
            }
            tmap.put("项目", "Enter" + kmList.get(0).getAttributeValue("fzname").toString());
        }
        return new Object[]{tmap, titlename};
    }

    // b5宽
    public void printB5(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, String[] columns,
                        String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                        Map<String, String> invmaps, Map<String, String> pmap, Map<String, String> tmap)
            throws DocumentException, IOException {
        Font titleFonts = new Font(bf_Bold, 16, Font.BOLD);
        int font = Integer.parseInt(pmap.get("font"));
        Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
        float totalMnyHight = getLineheight();
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
        float totaltablewidth = 0;
        if (getLineheight() == null) {
            totalMnyHight = 18f;// 设置双倍行距解决科目显示不完整问题
        }
        Font tableHeadFounts = getTableHeadFount();
        if (tableHeadFounts == null) {
            tableHeadFounts = new Font(bf, font, Font.BOLD);
        }

        Document document = null;
        if (titlename.equals("资 产 负 债 表")) {
            document = new Document(PageSize.B5, 5, 5, 70, 10);
            topsize = 70f;
            totaltablewidth = PageSize.B5.getWidth() - 5 - 5;
        } else {
            document = new Document(PageSize.B5, leftsize, 5, topsize, 10);
            totaltablewidth = PageSize.B5.getWidth() - leftsize - 5;
        }

        Map<String, Float> totalwidthmap = new HashMap<>();
        totalwidthmap = PrintUtil.calculateWidths(columns, columnnames, widths, totaltablewidth);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.open();

        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalMnyHight", totalMnyHight);
            if (kmmap != null && kmmap.size() > 0) { // ----按科目分页打印----
                tmap = printB51(kmmap, titlename, columns, columnnames, listattr, widths, pagecount, invmaps, pmap,
                        tmap, titleFonts, leftsize, topsize, tableHeadFounts, document, totalwidthmap, writer, para);
            } else {
                kmmap = new HashMap<>();
                kmmap.put(titlename, Arrays.asList(zzvos));
                tmap = printB51(kmmap, titlename, columns, columnnames, listattr, widths, pagecount, invmaps, pmap,
                        tmap, titleFonts, leftsize, topsize, tableHeadFounts, document, totalwidthmap, writer, para);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            document.close();
        }

        //打印文件
        outPutPdf(titlename, buffer);
    }

    public void printA5(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, String[] columns,
                        String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                        Map<String, String> invmaps, Map<String, String> pmap, Map<String, String> tmap)
            throws DocumentException, IOException {
        Font titleFonts = new Font(bf_Bold, 16, Font.BOLD);
        int font = Integer.parseInt(pmap.get("font"));
        Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
        float totalMnyHight = getLineheight();
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
        float totaltablewidth = 0;
        if (getLineheight() == null) {
            totalMnyHight = 18f;// 设置双倍行距解决科目显示不完整问题
        }
        Font tableHeadFounts = getTableHeadFount();
        if (tableHeadFounts == null) {
            tableHeadFounts = new Font(bf, font, Font.BOLD);
        }

        Document document = null;
        Rectangle pageSize = new Rectangle((PageSize.A5.getHeight()), (PageSize.A5.getWidth()));
        if (titlename.startsWith("工 资 表")) {
            document = new Document(pageSize, leftsize, 15, topsize, 10);
        } else {
            document = new Document(pageSize, leftsize, 5, topsize, 10);
        }
        totaltablewidth = PageSize.A5.getHeight() - leftsize - 5;

        Map<String, Float> totalwidthmap = new HashMap<>();
        totalwidthmap = PrintUtil.calculateWidths(columns, columnnames, widths, totaltablewidth);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.open();

        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalMnyHight", totalMnyHight);
            if (kmmap != null && kmmap.size() > 0) { // ----按科目分页打印----
                tmap = printA51(kmmap, titlename, columns, columnnames, listattr, widths, pagecount, invmaps, pmap,
                        tmap, titleFonts, leftsize, topsize, tableHeadFounts, document, totalwidthmap, writer, para);
            } else {
                kmmap = new HashMap<>();
                kmmap.put(titlename, Arrays.asList(zzvos));
                tmap = printA51(kmmap, titlename, columns, columnnames, listattr, widths, pagecount, invmaps, pmap,
                        tmap, titleFonts, leftsize, topsize, tableHeadFounts, document, totalwidthmap, writer, para);
            }
        } catch (Exception e) {
            throw e;
        } finally {
            document.close();
        }

        outPutPdf(titlename, buffer);
    }


    private Map<String, String> printB51(Map<String, List<SuperVO>> kmmap, String titlename, String[] columns,
                                         String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                                         Map<String, String> invmaps, Map<String, String> pmap, Map<String, String> tmap, Font titleFonts,
                                         float leftsize, float topsize, Font tableHeadFounts, Document document, Map<String, Float> totalwidthmap,
                                         PdfWriter writer, Map<String, Object> para) throws DocumentException {
        int pageNum = 1;
        int total = kmmap.size();
        for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) { //
            if ("资 产 负 债 表".equals(titlename) || "利 润 表".equals(titlename)
                    || "分 部 利 润 表".equals(titlename) || "现 金 流 量 表".equals(titlename)) {
                tmap.put("期间", kmEntry.getKey());
            }
            List<SuperVO> kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
            // 插入头部信息
            Object[] objs = putRptHeadMap(tmap, titlename, kmList, para, invmaps, 1);//B5纸张
            tmap = (Map<String, String>) objs[0];
            String titlenametemp = (String) objs[1];
//            addIcTitleB5(kmList, writer, tableHeadFounts, iscross, titlename, leftsize, topsize);
            creatTitle(document, tmap, tableHeadFounts, titlenametemp, titleFonts);
            if (titlename.length() > 30) {
                titleFonts = new Font(bf_Bold, 11, Font.BOLD);
            }
            document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));

            List<SuperVO> zzvosList = new ArrayList<SuperVO>();
            for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                zzvosList.add(kmList.get(i));
                int line = getB5Line(document.getPageSize().getHeight(), para.get("tableheight"), topsize, (float) para.get("totalMnyHight"), titlename);
                if (titlename.startsWith("出入库明细表") || titlename.startsWith("库存成本表")) {
                    line = line - 1;
                }
                if ((i + 1) % line == 0 && i != 0) { // 每页只打22行
                    SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
                    // 把48行数据放进数组。 写入表格
                    PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr,
                            widths, pagecount, titlename, totalwidthmap);
                    document.add(table); // 打印表格
                    createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                    zzvosList.clear();
                    if (i < kmList.size() - 1) {// 整数页最后一条打完不新建页
                        pageNum++;
                        document.newPage();
                        document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
//                        addIcTitleB5(kmList, writer, tableHeadFounts, iscross, titlename, leftsize, topsize);
                        creatTitle(document, tmap, tableHeadFounts, titlenametemp, titleFonts);
                    }
                }
            }
            SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
            if (zzvoArray != null && zzvoArray.length > 0) {
                PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                        pagecount, titlename, totalwidthmap);
                document.add(table);
                createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
            }
            document.newPage(); // 新的一页 导出下一个科目
            pageNum++;
        }
        return tmap;
    }


    private Map<String, String> printA51(Map<String, List<SuperVO>> kmmap, String titlename, String[] columns,
                                         String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                                         Map<String, String> invmaps, Map<String, String> pmap, Map<String, String> tmap, Font titleFonts,
                                         float leftsize, float topsize, Font tableHeadFounts, Document document, Map<String, Float> totalwidthmap,
                                         PdfWriter writer, Map<String, Object> para) throws DocumentException {
        int pageNum = 1;
        for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) { //
            List<SuperVO> kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
            Object[] objs = putRptHeadMap(tmap, titlename, kmList, para, invmaps, 1);//B5纸张
            tmap = (Map<String, String>) objs[0];
            String titlenametemp = (String) objs[1];
            creatTitle(document, tmap, tableHeadFounts, titlenametemp, titleFonts);
            if (titlename.length() > 30) {
                titleFonts = new Font(bf_Bold, 11, Font.BOLD);
            }
            document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));

            List<SuperVO> zzvosList = new ArrayList<SuperVO>();
            for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                zzvosList.add(kmList.get(i));
                int line = getB5Line(document.getPageSize().getHeight(), para.get("tableheight"), topsize, (float) para.get("totalMnyHight"), titlename);
                if (titlename.startsWith("出入库明细表") || titlename.startsWith("库存成本表")) {
                    line = line - 1;
                }
                if ((i + 1) % line == 0 && i != 0) { // 每页只打22行
                    SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
                    // 把48行数据放进数组。 写入表格
                    PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames,
                            listattr, widths, pagecount, titlename, totalwidthmap);
                    document.add(table); // 打印表格
                    createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                    zzvosList.clear();
                    if (i < kmList.size() - 1) {// 整数页最后一条打完不新建页
                        pageNum++;
                        document.newPage();
                        if (rotate.booleanValue()) {
                            // 旋转
                            writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
                        }
                        document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
                        creatTitle(document, tmap, tableHeadFounts, titlenametemp, titleFonts);
                    }
                }
            }
            SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
            if (zzvoArray != null && zzvoArray.length > 0) {
                PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                        pagecount, titlename, totalwidthmap);
                document.add(table);
                createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
            }
            document.newPage(); // 新的一页 导出下一个科目
            pageNum++;
        }
        return tmap;
    }

    private int getB5Line(float pagesize, Object tableheight, float topsize, float totalMnyHight, String titlename) {
        //120是默认的head 和 title,和表尾的 高度
        if (tableheight == null) {
            tableheight = 2f;
        }
        float defaultheight = 120f;
//        if("发 生 额 及 余 额 表".equals(titlename) || "辅助余额表".equals(titlename) || titlename.indexOf("科目明细账") > 0){
//    		defaultheight = 120f;
//    	}
        float value = (pagesize - topsize - (float) tableheight - defaultheight) / totalMnyHight;
        if("财 务 概 要 信 息".equals(titlename)){
            return 100;
        }
        return new Float(value).intValue();
    }

    public Paragraph getFoot(Font tableHeadFounts, String printvalue, int pageNum, String titlename) {
        Paragraph foot = new Paragraph();
        String faren = null;
        try {
            faren = CodeUtils1.deCode(corpVO.getLegalbodycode());
        } catch (Exception e) {
            // e.printStackTrace();
        }
        if (titlename.equals("资 产 负 债 表") || titlename.equals("现 金 流 量 表") || titlename.equals("利 润 表")
                || titlename.equals("利 润 表 季 报") || titlename.equals("增值税和营业税月度申报对比表") || titlename.equals("业 务 活 动 表")
                || titlename.equals("收 入 支 出 表")) {
            Chunk user = new Chunk("制表人：" + userVO.getUser_name() + PrintUtil.getSpace(10) + "单位负责人："
                    + (faren == null ? "" : faren) + PrintUtil.getSpace(10) + "打印日期：" + printvalue, tableHeadFounts);
            foot.add(user);
        } else {
            Chunk printdate = new Chunk("打印日期：" + printvalue + PrintUtil.getSpace(9) + "第" + pageNum + "页",
                    tableHeadFounts);
            foot.add(printdate);
        }
        return foot;
    }

    public void creatTitle(Document document, Map<String, String> tmapvalue,
                           Font tableHeadFounts, String titlename, Font titleFonts) throws RuntimeException {
        //通过"enter" 分组tmap
        List<String[]> listmap = getListMap(tmapvalue, titlename);
        //标题
        createBt(tableHeadFounts, document, listmap.get(0), titleFonts);
        if (listmap.size() > 1) {
            List<String[]> list2 = new ArrayList<>();
            for (int i = 1; i < listmap.size(); i++) {
                list2.add(listmap.get(i));
            }
            createTopAndBottom(tableHeadFounts, document, list2);
        }
    }

    /**
     * 一共是只有三行，和标题同行，标题下第一行，标题下第二行
     *
     * @param tmap
     * @param titlename
     * @return
     */
    private List<String[]> getListMap(Map<String, String> tmap, String titlename) {
        List<String[]> reslist = new ArrayList<String[]>();
        //标题平行
        List<String> list0 = new ArrayList<String>();
        list0.add(titlename);
        //第一行
        List<String> list1 = new ArrayList<String>();
        //第二行
        List<String> list2 = new ArrayList<String>();
        int group1 = 1;
        for (Map.Entry<String, String> entry : tmap.entrySet()) {
            if (!StringUtil.isEmpty(entry.getValue()) && entry.getValue().indexOf("Enter") > -1) {
                entry.getValue().replace("Enter", "");
                group1 = 2;//第三组
            } else if ("科 目 汇 总 表".equals(titlename) && (entry.getKey().equals("凭证数") || entry.getKey().equals("附件数"))) {
                group1 = 0;//第一组
            } else if (("入 库 单".equals(titlename) || "出 库 单".equals(titlename))
                    && (entry.getKey().equals("单据号") || entry.getKey().equals("凭证号"))) {
                group1 = 0;//第一组
            }
            if (group1 == 1) {
                list1.add(entry.getKey() + ":" + entry.getValue());
            } else if (group1 == 2) {
                list2.add(entry.getKey() + ":" + entry.getValue().replace("Enter", ""));
            } else if (group1 == 0) {
                list0.add(entry.getKey() + ":" + entry.getValue());
            }
        }
        //最后一次追加
        reslist.add(list0.toArray(new String[0]));
        if (list1.size() > 0) {
            reslist.add(list1.toArray(new String[0]));
        }
        if (list2.size() > 0) {
            reslist.add(list2.toArray(new String[0]));
        }
        return reslist;
    }

    public Float getLineheight() {
        return lineheight;
    }

    public void setLineheight(Float lineheight) {
        this.lineheight = lineheight;
    }

    public DZFBoolean getIscross() {
        return iscross;
    }

    public void setIscross(DZFBoolean iscross) {
        this.iscross = iscross;
    }
    public DZFBoolean getRotate() {
        return rotate;
    }
    public void setRotate(DZFBoolean rotate) {
        this.rotate = rotate;
    }

    public Font getTableHeadFount() {
        return tableHeadFount;
    }

    public void setTableHeadFount(Font tableHeadFount) {
        this.tableHeadFount = tableHeadFount;
    }

    public BaseFont getBf() {
        return bf;
    }

    public void setBf(BaseFont bf) {
        this.bf = bf;
    }

    public BaseFont getBf_Bold() {
        return bf_Bold;
    }

    public void setBf_Bold(BaseFont bf_Bold) {
        this.bf_Bold = bf_Bold;
    }

    public BaseColor getBasecolor() {
        return basecolor;
    }

    public void setBasecolor(BaseColor basecolor) {
        this.basecolor = basecolor;
    }

    public Object[] getPrintXm(int type) {
        return null;
    }

    public Map<String, String> getPrintMap(PrintParamVO printvo) {
        Map<String, String> pmap = new HashMap<String, String>();// 声明一个map用来存前台传来的设置参数
        pmap.put("type", printvo.getType());
        pmap.put("list", printvo.getList());
        pmap.put("pageOrt", printvo.getPageOrt());
        pmap.put("left", printvo.getLeft());
        pmap.put("top", printvo.getTop());
        pmap.put("printdate", printvo.getPrintdate());
        pmap.put("font", printvo.getFont());
        pmap.put("pageNum", printvo.getPageNum());
        pmap.put("isPaging",printvo.getIsPaging());
        pmap.put("lineHeight",printvo.getLineHeight());
        pmap.put("projectname",printvo.getProjectname());
        pmap.put("print_all",printvo.getPrint_all());
        pmap.put("showlb",printvo.getShowlb());
        pmap.put("fzlb_name",printvo.getFzlb_name());
        pmap.put("showbm", printvo.getShowbm());
        return pmap;
    }

    public String getPrintTitleName() {
        return "";
    }

    /****************************** 出入库单合并打印 ************************************/

    /**
     * A4宽 打印
     *
     * @param kmmap              分页打印信息
     * @param zzvos              不分页打印信息
     * @param titlename          显示表头
     * @param columns            显示的字读
     * @param columnnames显示的字读名称
     * @param listattr           字段属性
     * @param widths             字段宽度
     * @param pagecount          每页显示行数
     * @param invmaps            数量金额明细账使用
     * @param pmap               页尾信息
     * @param tmap               页头信息
     * @throws DocumentException
     * @throws IOException
     */
    public void printGroupCombin(Map<String, List<SuperVO>> kmmap, String titlename, String[] columns,
                                 String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                                 Map<String, String> pmap, Map<String, String> invmaps) throws DocumentException, IOException {
        Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
        int font = Integer.parseInt(pmap.get("font"));
        Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
        Font tableHeadFounts = getTableHeadFount();
        if (tableHeadFounts == null) {
            tableHeadFounts = new Font(bf, font, Font.BOLD);
        }
        float totalAmountHight = 13f;
        float totalMnyHight = 0;
        if (getLineheight() == null) {
            totalMnyHight = 22f;// 设置双倍行距解决科目显示不完整问题
        } else {
            totalMnyHight = getLineheight();
        }
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
        Document document = null;
        Rectangle pageSize = null;
        float totaltablewidth = 0;

        float totaltableheight = 0;
        if (iscross != null && iscross.booleanValue()) {
            pageSize = new Rectangle((PageSize.A4.getHeight()), (PageSize.A4.getWidth()));
            document = new Document(pageSize, leftsize, 15, topsize, 10);
            totaltablewidth = PageSize.A4.getHeight() - topsize - 15;
            totaltableheight = PageSize.A4.getWidth() - leftsize - 15;
        } else {
            pageSize = PageSize.A4;
            document = new Document(pageSize, leftsize, 15, topsize, 4);
            totaltablewidth = PageSize.A4.getWidth() - leftsize - 15;
            totaltableheight = PageSize.A4.getHeight() - topsize - 15;
        }

        Map<String, Float> totalwidthmap = new HashMap<>();
        totalwidthmap = PrintUtil.calculateWidths(columns, columnnames, widths, totaltablewidth);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        float totaltableheighttemp = totaltableheight;
        document.open();
        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            if (kmmap != null && kmmap.size() > 0) { // ----按科目分页打印----
                int pageNum = 1;
                List<SuperVO> kmList = null;
                Paragraph title = new Paragraph();
                if (titlename.length() > 30) {
                    title.add(new Chunk(titlename, new Font(bf_Bold, 15, Font.BOLD)));
                } else {
                    title.add(new Chunk(titlename, titleFonts));
                }
                title.setAlignment(Element.ALIGN_CENTER);

                // 增加入库单表体
                totaltableheight = addHeadTitle(title, document, tableHeadFounts, totaltableheight);
                List<SuperVO> zzvosList = null;
                SuperVO[] zzvoArray = null;
                PdfPTable table = null;
                int count = 0;
                for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) {
                    kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
                    zzvosList = new ArrayList<SuperVO>();
                    for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                        zzvosList.add(kmList.get(i));
                        totaltableheight = totaltableheight - 20;
                        if (i == kmList.size() - 1) {
                            // 单据的第一条 增加标头行
                            if (i == 0) {
                                totaltableheight = addTableHeadTitleA4(titlename, kmList, document, tableHeadFounts,
                                        totaltableheight, totaltablewidth, invmaps);
                            }
                            zzvoArray = zzvosList.toArray(new SuperVO[0]);
                            table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                                    pagecount, titlename, totalwidthmap);
                            document.add(table); // 打印表格
                            createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                            document.add(getLineSeparator(true));
                            document.add(Chunk.NEWLINE);
                            zzvosList.clear();
                            // 最后一条数据 或者 页已经打印完成
                            if (count == kmmap.size() - 1) {
                            } else {
                                if (totaltableheight < 80) {
                                    pageNum++;
                                    document.newPage();
                                    totaltableheight = totaltableheighttemp;
                                    totaltableheight = addHeadTitle(title, document, tableHeadFounts, totaltableheight);
                                }
                            }
                        } else {
                            if (totaltableheight < 80) {
                                if (i == 0) {
                                    totaltableheight = addTableHeadTitleA4(titlename, kmList, document, tableHeadFounts,
                                            totaltableheight, totaltablewidth, invmaps);
                                }
                                zzvoArray = zzvosList.toArray(new SuperVO[0]);
                                table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                                        pagecount, titlename, totalwidthmap);
                                document.add(table); // 打印表格
                                zzvosList.clear();
                                createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                                document.add(getLineSeparator(true));
                                document.add(Chunk.NEWLINE);
                                pageNum++;
                                document.newPage();
                                totaltableheight = totaltableheighttemp;
                                totaltableheight = addHeadTitle(title, document, tableHeadFounts, totaltableheight);
                                totaltableheight = addTableHeadTitleA4(titlename, kmList, document, tableHeadFounts,
                                        totaltableheight, totaltablewidth, invmaps);
                            } else {
                                if (i == 0) {
                                    totaltableheight = addTableHeadTitleA4(titlename, kmList, document, tableHeadFounts,
                                            totaltableheight, totaltablewidth, invmaps);
                                }
                            }
                        }

                    }
                    count++;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            document.close();
        }

        ServletOutputStream out = null;
        try {
            if (gvo != null && gvo.getPrintType() != null && gvo.getPrintType() == 1) {
                getResponse().setContentType("application/octet-stream");
                getResponse().addHeader("Content-Disposition", "attachment;filename=voucher.pdf");
            } else {
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                getResponse().setContentLength(buffer.size());
            }
            out = getResponse().getOutputStream();
            buffer.writeTo(out);
            buffer.flush();//flush 放在finally的时候流关闭失败报错
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }

    private float addHeadTitle(Paragraph title, Document document, Font tableHeadFounts, float totaltableheight)
            throws DocumentException {
        document.add(title); // 输入标题
        document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
        return totaltableheight - 30;
    }

    private float addTableHeadTitleA4(String titlename, List<SuperVO> kmList, Document document, Font tableHeadFounts,
                                      float totaltableheight, float totaltablewidth, Map<String, String> invmaps) throws DocumentException {
        // 输入标题

        if (kmList == null || kmList.size() == 0) {
            return totaltableheight;
        }
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        paragraph.setLeading(6f);
        SuperVO vo = kmList.get(0);

        if (invmaps == null || invmaps.isEmpty() || invmaps.get("isHiddenPzh") == null || !invmaps.get("isHiddenPzh").equals("Y")) {
            String username = (String) (vo.getAttributeValue("dbillid") == null ? "" : vo.getAttributeValue("dbillid"));
            Chunk printdate = new Chunk("单据号：" + username, tableHeadFounts);
            printdate.setTextRise(16f);
            printdate.append(PrintUtil.getSpace(15));
            String pzh = getPzh(kmList);
            printdate.append("凭证号：" + pzh);
            paragraph.add(printdate);
        } else {
            String username = (String) (vo.getAttributeValue("dbillid") == null ? "" : vo.getAttributeValue("dbillid"));
            Chunk printdate = new Chunk("单据号：" + username, tableHeadFounts);
            printdate.setTextRise(16f);
            paragraph.add(printdate);
        }

        document.add(paragraph);
        Paragraph paragraph1 = new Paragraph();
        paragraph1.setLeading(2f);
        paragraph1.setAlignment(Element.ALIGN_RIGHT);
        String speriod = DateUtils.getPeriod((DZFDate) vo.getAttributeValue("dbilldate"));
        Chunk qj = new Chunk("期间：" + speriod, tableHeadFounts);
        qj.setTextRise(2f);
        paragraph1.add(qj);
        document.add(paragraph1);
        String custname = (String) (vo.getAttributeValue("custname") == null ? "" : vo.getAttributeValue("custname"));
        Chunk gys = null;
        if ("入 库 单".equals(titlename)) {
            gys = new Chunk("供应商：" + custname, tableHeadFounts);
        } else {
            gys = new Chunk("客户：" + custname, tableHeadFounts);
        }
        gys.setTextRise(2f);
        document.add(gys);
        return totaltableheight - 100;
    }

    // b5宽
    public void printB5Combin(Map<String, List<SuperVO>> kmmap, String titlename, String[] columns,
                              String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
                              Map<String, String> pmap, Map<String, String> invmaps) throws DocumentException, IOException {
        Font titleFonts = new Font(bf_Bold, 16, Font.BOLD);
        int font = Integer.parseInt(pmap.get("font"));
        Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
        float totalAmountHight = 13f;
        float totalMnyHight = getLineheight();
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
        float totaltablewidth = 0;

        if (getLineheight() == null) {
            totalMnyHight = 18f;// 设置双倍行距解决科目显示不完整问题
        }
        Font tableHeadFounts = getTableHeadFount();
        if (tableHeadFounts == null) {
            tableHeadFounts = new Font(bf, font, Font.BOLD);
        }

        Document document = new Document(PageSize.B5, leftsize, 5, topsize, 10);
        totaltablewidth = PageSize.B5.getWidth() - leftsize - 5;
        float totaltableheight = PageSize.B5.getHeight() - topsize - 5;
        Map<String, Float> totalwidthmap = new HashMap<>();

        totalwidthmap = PrintUtil.calculateWidths(columns, columnnames, widths, totaltablewidth);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.open();
        float totaltableheighttemp = totaltableheight;
        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            if (kmmap != null && kmmap.size() > 0) { // ----按科目分页打印----
                int pageNum = 1;
                List<SuperVO> kmList = null;
                Paragraph title = new Paragraph();
                if (titlename.length() > 30) {
                    title.add(new Chunk(titlename, new Font(bf_Bold, 15, Font.BOLD)));
                } else {
                    title.add(new Chunk(titlename, titleFonts));
                }
                title.setAlignment(Element.ALIGN_CENTER);

                // 增加入库单表体
                totaltableheight = addHeadTitle(title, document, tableHeadFounts, totaltableheight);
                List<SuperVO> zzvosList = null;
                SuperVO[] zzvoArray = null;
                PdfPTable table = null;
                int count = 0;
                for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) {
                    kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
                    zzvosList = new ArrayList<SuperVO>();
                    for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                        zzvosList.add(kmList.get(i));
                        totaltableheight = totaltableheight - 20;
                        if (i == kmList.size() - 1) {
                            // 单据的第一条 增加标头行
                            if (i == 0) {
                                totaltableheight = addTableHeadTitleB5(titlename, kmList, document, tableHeadFounts,
                                        totaltableheight, totaltablewidth, invmaps);
                            }
                            zzvoArray = zzvosList.toArray(new SuperVO[0]);
                            table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                                    pagecount, titlename, totalwidthmap);
                            document.add(table); // 打印表格
                            createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                            document.add(getLineSeparator(false));
                            zzvosList.clear();
                            // 最后一条数据 或者 页已经打印完成
                            if (count == kmmap.size() - 1) {
                            } else {
                                if (totaltableheight < 80) {
                                    pageNum++;
                                    document.newPage();
                                    totaltableheight = totaltableheighttemp;
                                    totaltableheight = addHeadTitle(title, document, tableHeadFounts, totaltableheight);
                                }
                            }
                        } else {
                            if (totaltableheight < 80) {
                                if (i == 0) {
                                    totaltableheight = addTableHeadTitleB5(titlename, kmList, document, tableHeadFounts,
                                            totaltableheight, totaltablewidth, invmaps);
                                }
                                zzvoArray = zzvosList.toArray(new SuperVO[0]);
                                table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
                                        pagecount, titlename, totalwidthmap);
                                document.add(table); // 打印表格
                                zzvosList.clear();
                                createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                                document.add(getLineSeparator(false));
                                pageNum++;
                                document.newPage();
                                totaltableheight = totaltableheighttemp;
                                totaltableheight = addHeadTitle(title, document, tableHeadFounts, totaltableheight);
                                totaltableheight = addTableHeadTitleB5(titlename, kmList, document, tableHeadFounts,
                                        totaltableheight, totaltablewidth, invmaps);
                            } else {
                                if (i == 0) {
                                    totaltableheight = addTableHeadTitleB5(titlename, kmList, document, tableHeadFounts,
                                            totaltableheight, totaltablewidth, invmaps);
                                }
                            }
                        }

                    }
                    count++;
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            document.close();
        }

        ServletOutputStream out = null;
        try {
            if (gvo != null && gvo.getPrintType() != null && gvo.getPrintType() == 1) {
                getResponse().setContentType("application/octet-stream");
                getResponse().addHeader("Content-Disposition", "attachment;filename=voucher.pdf");

            } else {
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                getResponse().setContentLength(buffer.size());
            }
            out = getResponse().getOutputStream();
            buffer.writeTo(out);
            buffer.flush();//flush 放在finally的时候流关闭失败报错
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }

    private float addTableHeadTitleB5(String titlename, List<SuperVO> kmList, Document document, Font tableHeadFounts,
                                      float totaltableheight, float totaltablewidth, Map<String, String> invmaps) throws DocumentException {
        // 输入标题

        if (kmList == null || kmList.size() == 0) {
            return totaltableheight;
        }
        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(Element.ALIGN_RIGHT);
        paragraph.setLeading(14f);
        SuperVO vo = kmList.get(0);

        if (invmaps == null || invmaps.isEmpty() || invmaps.get("isHiddenPzh") == null || !invmaps.get("isHiddenPzh").equals("Y")) {
            String username = (String) (vo.getAttributeValue("dbillid") == null ? "" : vo.getAttributeValue("dbillid"));
            Chunk printdate = new Chunk("单据号：" + username, tableHeadFounts);
            printdate.setTextRise(16f);
            printdate.append(PrintUtil.getSpace(15));
            String pzh = getPzh(kmList);
            printdate.append("凭证号：" + pzh);
            paragraph.add(printdate);
        } else {
            String username = (String) (vo.getAttributeValue("dbillid") == null ? "" : vo.getAttributeValue("dbillid"));
            Chunk printdate = new Chunk("单据号：" + username, tableHeadFounts);
            printdate.setTextRise(16f);
            paragraph.add(printdate);
        }

        document.add(paragraph);
        Paragraph paragraph1 = new Paragraph();
        paragraph1.setLeading(2f);
        paragraph1.setAlignment(Element.ALIGN_RIGHT);
        String speriod = DateUtils.getPeriod((DZFDate) vo.getAttributeValue("dbilldate"));
        Chunk qj = new Chunk("期间：" + speriod, tableHeadFounts);
        qj.setTextRise(2f);
        paragraph1.add(qj);
        document.add(paragraph1);
        String custname = (String) (vo.getAttributeValue("custname") == null ? "" : vo.getAttributeValue("custname"));
        Chunk gys = null;
        if ("入 库 单".equals(titlename)) {
            gys = new Chunk("供应商：" + custname, tableHeadFounts);
        } else {
            gys = new Chunk("客户：" + custname, tableHeadFounts);
        }
        gys.setTextRise(2f);
        document.add(gys);
        return totaltableheight - 100;
    }

    public Float getFirstlineheight() {
        return firstlineheight;
    }

    public void setFirstlineheight(Float firstlineheight) {
        this.firstlineheight = firstlineheight;
    }

    public String getIspaging() {
        return ispaging;
    }

    public void setIspaging(String ispaging) {
        this.ispaging = ispaging;
    }

    private Paragraph getLineSeparator(boolean isA4) {
        Paragraph paragraph = new Paragraph();
        DottedLineSeparator line = new DottedLineSeparator();
        line.setLineWidth(0.6f);
        if (isA4) {
            if (iscross != null && iscross.booleanValue()) {
                line.setLineWidth(0.9f);
            }
        }
        line.setOffset(10f);
        paragraph.add(new Chunk(line));
        return paragraph;
    }

    private String getBottomItemIc(Map<String, String> pmap, List<SuperVO> kmList, Font tableHeadFounts,
                                   CorpVO corpvo) {
        StringBuffer value = new StringBuffer();
        String username = getUsername(kmList);
        if(pmap.containsKey("会计") && pmap.containsKey("库管员")){
            if(pmap.containsKey("会计"))
            value.append("会计：" + getPrintString(pmap,username,1));
        if(pmap.containsKey("库管员"))
            value.append("库管员：" +  getPrintString(pmap,pmap.get("库管员"),1));
        }else{
            if(pmap.containsKey("会计"))
                value.append("会计：" +getPrintString(pmap,username,2));
            if(pmap.containsKey("库管员"))
                value.append("库管员：" + getPrintString(pmap,pmap.get("库管员"),2));
        }
        value.append("打印日期：" + pmap.get("printdate"));
        return value.toString();
    }

    private String getPrintString(Map<String, String> pmap,String str,int beishu){
        int defaultlen =14;
        if (pmap.get("type").equals("1")) {//A4纸张
            if(iscross.booleanValue()){
                defaultlen =25;
            }else{
                defaultlen =13;
            }
        }else if (pmap.get("type").equals("3")) {//A5纸张
            defaultlen =20;
        }
        defaultlen = defaultlen*beishu;
        if(StringUtil.isEmpty(str)){
            return PrintUtil.getSpace(defaultlen);
        }else{
            str = str.trim();
            int strlen =str.length();
            int index = 0;
            int count = 0;
            for (int i = 0; i < strlen; i++) {
                char c = str.charAt(i);
                if ((c >= 'һ') && (c <= 40869))
                    index += 2;
                else {
                    index++;
                }
                if(index>defaultlen){
                    break;
                }
                count =i+1;
            }
            if(count<strlen){
                return  str.substring(0,count)+PrintUtil.getSpace(1);
            }else{
                return  str + PrintUtil.getSpace(defaultlen-index+1);
            }
        }
    }

    private String getUsername(List<SuperVO> kmList) {
        return userVO.getUser_name();
    }


    private String getPzh(List<SuperVO> kmList) {
        String pzh = "  ";
        if (kmList == null || kmList.size() == 0)
            return pzh;
        SuperVO vo = kmList.get(0);
        if (!StringUtil.isEmpty((String) (vo.getAttributeValue("pk_voucher")))) {
            if (!StringUtil.isEmpty((String) (vo.getAttributeValue("pzh")))) {
                pzh = (String) vo.getAttributeValue("pzh");
            }
        }
        return pzh;
    }

    /**
     * 发票纸宽 打印
     *
     * @param kmmap              分页打印信息
     * @param zzvos              不分页打印信息
     * @param titlename          显示表头
     * @param columns            显示的字读
     * @param columnnames显示的字读名称
     * @param listattr           字段属性
     * @param widths             字段宽度
     * @param pagecount          每页显示行数
     * @param pmap               页尾信息
     * @param tmap               页头信息
     * @throws DocumentException
     * @throws IOException
     */
    public void printICInvoice(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, String[] columns,
                               String[] columnnames, int[] widths, Integer pagecount, Map<String, String> pmap, Map<String, String> tmap)
            throws DocumentException, IOException {
        printICInvoice(kmmap, zzvos, titlename, columns, columnnames, widths, pagecount, null, pmap, tmap);
    }

    /**
     * 发票纸宽 打印
     *
     * @param kmmap              分页打印信息
     * @param zzvos              不分页打印信息
     * @param titlename          显示表头
     * @param columns            显示的字读
     * @param columnnames显示的字读名称
     * @param listattr           字段属性
     * @param widths             字段宽度
     * @param pagecount          每页显示行数
     * @param invmaps            数量金额明细账使用
     * @param pmap               页尾信息
     * @param tmap               页头信息
     * @throws DocumentException
     * @throws IOException
     */
    public void printICInvoice(Map<String, List<SuperVO>> kmmap, SuperVO[] zzvos, String titlename, String[] columns,
                               String[] columnnames, int[] widths, Integer pagecount, Map<String, String> invmaps, Map<String, String> pmap, Map<String, String> tmap)
            throws DocumentException, IOException {
        Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
        int font = Integer.parseInt(pmap.get("font"));
        Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
        Font tableHeadFounts = getTableHeadFount();
        if (tableHeadFounts == null) {
            tableHeadFounts = new Font(bf, font, Font.BOLD);
        }
        float totalAmountHight = 13f;
        float totalMnyHight = 0;
        if (getLineheight() == null) {
            totalMnyHight = 22f;// 设置双倍行距解决科目显示不完整问题
        } else {
            totalMnyHight = getLineheight();
        }
        float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
        float rightsize = 15;
        if (pmap.get("right") != null) {
            rightsize = (float) (Float.parseFloat(pmap.get("right")) * 2.83);
        }

        float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
        Document document = null;
        Rectangle pageSize = null;
        float totaltablewidth = 0;
        pageSize = new Rectangle(680f, 396.67f);
        document = new Document(pageSize, leftsize, rightsize, topsize, 10);
        totaltablewidth = 140 - leftsize - 15;

        Map<String, Float> totalwidthmap = new HashMap<>();
        totalwidthmap = PrintUtil.calculateWidths(columns, columnnames, widths, totaltablewidth);

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);

        document.open();
        try {
            if (rotate.booleanValue()) {
                // 旋转
                writer.addPageDictEntry(PdfName.ROTATE, PdfPage.SEASCAPE);
            }
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            int pageNum = 1;
            List<SuperVO> kmList = null;
            List<SuperVO> zzvosList = null;
            SuperVO[] zzvoArray = null;
            Object[] titleobjs = null;
            PdfPTable table = null;
            for (Map.Entry<String, List<SuperVO>> kmEntry : kmmap.entrySet()) {
                kmList = kmEntry.getValue(); // 得到当前科目 所对应的 数据
                // 插入头部信息
                titleobjs = putA4TitleMsgInvoice(document, titlename, invmaps, tmap, titleFonts, tableHeadFounts, leftsize, rightsize, topsize,
                        writer, para, kmList);
                tmap = (Map<String, String>) titleobjs[0];
                int line = getA4LineNotPage(document.getPageSize().getHeight(), para.get("tableheight"), topsize, (float) para.get("totalMnyHight"), titlename);
                if (titlename.startsWith("出入库明细表") || titlename.startsWith("库存成本表")) {
                    line = line - 1;
                }
                zzvosList = new ArrayList<SuperVO>();
                for (int i = 0; i < kmList.size(); i++) { // 遍历本科目下的 所有数据
                    zzvosList.add(kmList.get(i));
                    if ((i + 1) % line == 0 && i != 0) { // 每页只打22行
                        zzvoArray = zzvosList.toArray(new SuperVO[0]);
                        // 把22行数据放进数组。 写入表格
                        table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, null, widths,
                                pagecount, titlename, totalwidthmap);
                        document.add(table); // 打印表格
                        createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                        zzvosList.clear();
                        if (i < kmList.size() - 1) {// 整数页最后一条打完不新建页
                            pageNum++;
                            document.newPage();
                            document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
                            creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
                        }
                    }
                }
                zzvoArray = zzvosList.toArray(new SuperVO[0]);
                if (zzvoArray != null && zzvoArray.length > 0) {//最后的页数
                    table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, null, widths, pagecount,
                            titlename, totalwidthmap);
                    document.add(table);
                    createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", corpVO, kmList);
                }
                document.newPage(); // 新的一页 导出下一个科目
                pageNum++;
            }
        } catch (Exception e) {
            throw e;
        } finally {
            document.close();
        }

        outPutPdf(titlename, buffer);
    }

    private Object[] putA4TitleMsgInvoice(Document document, String titlename, Map<String, String> invmaps, Map<String, String> tmap,
                                          Font titleFonts, Font tableHeadFounts, float leftsize, float rightsize, float topsize, PdfWriter writer,
                                          Map<String, Object> para, List<SuperVO> kmList) {
        Object[] objs = putRptHeadMap(tmap, titlename, kmList, para, invmaps, 0);//A4纸张
        tmap = (Map<String, String>) objs[0];
        titlename = (String) objs[1];
        creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
        return new Object[]{tmap};
    }

    public void printMultiColumn(List<Map<String, String>> array, String titlename, List<String> columns, String[] fields, int[] widths, int pagecount, List<ColumnCellAttr> listattr, Map<String, String> pmap, HttpServletResponse response) throws IOException {
        Document document = null;
        ByteArrayOutputStream buffer = null;
        try {
            Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
            int font = Integer.parseInt(pmap.get("font"));
            Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
            Font tableHeadFounts = new Font(bf, font, Font.NORMAL);
            float totalAmountHight = 13f;
            float totalMnyHight;
            totalMnyHight = 22f;//设置双倍行距解决科目显示不完整问题
            int separatorLine = 1;
            int pageVchNum = 1;
            float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
            float rightsize = 15;
            float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
            Rectangle pageSize = null;
            float totaltablewidth = 0;
            if (iscross != null && iscross.booleanValue()) {
                pageSize = new Rectangle((PageSize.A4.getHeight()), (PageSize.A4.getWidth()));
                document = new Document(pageSize, leftsize, 15, topsize, 10);
                totaltablewidth = PageSize.A4.getHeight() - leftsize - 15;
            } else {
                pageSize = PageSize.A4;
                document = new Document(pageSize, leftsize, 15, topsize, 5);
                totaltablewidth = PageSize.A4.getWidth() - leftsize - 15;
            }
            buffer = new ByteArrayOutputStream();
            PdfWriter writer = PdfWriter.getInstance(document, buffer);
            document.open();
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            Map<String, String> tmap = new LinkedHashMap<>();// 声明一个map用来存前台传来的设置参数
            tmap.put("公司", pmap.get("gs"));
            tmap.put("期间", pmap.get("period"));
            tmap.put("单位", "元");
            setTableHeadFount(new Font(getBf(), font, Font.NORMAL));//设置表头字体
            document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
            creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
            int linenum = 0;
            int pageNum = 1;
            linenum = getLineNum(columns, fields);//获取行多少
            JSONArray array1 = new JSONArray();
            Map<String, Float> totalwidthmap = new HashMap<String, Float>();
            totalwidthmap = PrintUtil.calculateWidths(fields, columns.toArray(new String[0]), widths, totaltablewidth);
            for (int i = 0; i < array.size(); i++) {
                array1.add(array.get(i));
                if ((i + 1) % linenum == 0 && i != 0) {
                    PdfPTable table = getTableBody(tableBodyFounts, array1, totalMnyHight, columns, fields, titlename, widths, para, listattr, totalwidthmap);
                    document.add(table);
                    createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", null, null);
                    array1.clear();
                    if (i < array.size() - 1) {
                        pageNum++;
                        document.newPage();
//						createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", null, null);
                        creatTitle(document, tmap, tableHeadFounts, titlename, titleFonts);
                    }
                }
            }

            if (array1 != null && array1.size() > 0) {
                PdfPTable table = getTableBody(tableBodyFounts, array1, totalMnyHight, columns, fields, titlename, widths, para, listattr, totalwidthmap);

                document.add(table);

                createBottomText(titlename, pmap, tableHeadFounts, document, pageNum + "", null, null);
            }
        } catch (Exception e) {
            log.error("错误", e);
        } finally {
            if (document != null)
                document.close();
        }
        ServletOutputStream out = null;
        try {
//			getResponse().setContentType("application/pdf");
//			getResponse().setCharacterEncoding("utf-8");
//			if(buffer != null){
//				getResponse().setContentLength(buffer.size());
//				out = getResponse().getOutputStream();
//				buffer.writeTo(out);
//				buffer.flush();
//				out.flush();
//			}
            GxhszVO gvo = zxkjPlatformService.queryGxhszVOByPkCorp(corpVO.getPk_corp());
            String filename = titlename.replace(" ", "");//titlename.replace(" ", "");
            filename = filename + "_" + new DZFDate().toString();
            if (gvo != null && gvo.getPrintType() != null && gvo.getPrintType() == 1) {
                getResponse().setContentType("application/octet-stream");
                String contentDisposition = "attachment;filename=" + URLEncoder.encode(filename + ".pdf", "UTF-8")
                        + ";filename*=UTF-8''" + URLEncoder.encode(filename + ".pdf", "UTF-8");
                getResponse().addHeader("Content-Disposition", contentDisposition);
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                getResponse().setContentLength(buffer.size());
            } else {
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                String contentDisposition = "inline;filename=" + URLEncoder.encode(filename + ".pdf", "UTF-8")
                        + ";filename*=UTF-8''" + URLEncoder.encode(filename + ".pdf", "UTF-8");
                getResponse().setHeader("Content-Disposition", contentDisposition);
                getResponse().setContentType("application/pdf");
                getResponse().setCharacterEncoding("utf-8");
                getResponse().setContentLength(buffer.size());
            }
            out = getResponse().getOutputStream();
            buffer.writeTo(out);
            buffer.flush();//flush 放在finally的时候流关闭失败报错
            out.flush();
        } catch (IOException e) {
            throw e;
        } finally {
            try {
                if (buffer != null) {
                    buffer.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                throw e;
            }
        }
    }


    public PdfPTable getTableBody(Font fonts, JSONArray array, float totalMnyHight, List<String> columns,
                                  String[] fields, String tilename, int[] widths, Map<String, Object> para, List<ColumnCellAttr> listattr
            , Map<String, Float> totalwidthmap) {
        int columnslength = columns.size();
        int fieldslength = fields.length;
        PdfPTable table = new PdfPTable(fieldslength);
        if (columnslength != fieldslength) {
            table.setHeaderRows(2);
        } else {
            table.setHeaderRows(1);
        }
        table.setSpacingBefore(2);
        table.setWidthPercentage(100);
        try {
            table.setWidths(widths);
        } catch (DocumentException e1) {
        }
        addTabHead((Font) para.get("tableHeadFounts"), table, totalMnyHight, listattr, totalwidthmap);
        try {
            PdfPCell cell = null;
            for (int i = 0; i < array.size(); i++) {
                Map<String, String> map = (Map<String, String>) array.get(i);
                Set<String> keySet = map.keySet();
                keySet.remove("pk_tzpz_h");
                keySet.remove("pk_accsubj");
                for (String key : fields) {
                    if (key.equals("rq") || key.equals("zy") || key.equals("fx") || key.equals("pzh")) {
                        cell = new PdfPCell(new Phrase((String) map.get(key), PrintUtil.getAutoFont(fonts, (String) map.get(key), totalwidthmap.get(key), totalMnyHight, DZFBoolean.FALSE)));
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    } else {
                        cell = new PdfPCell(new Phrase((String) map.get(key), PrintUtil.getAutoFont(fonts, (String) map.get(key), totalwidthmap.get(key), totalMnyHight, DZFBoolean.TRUE)));
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    if (getBasecolor() != null) {
                        cell.setBorderColor(getBasecolor());
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    cell.setFixedHeight(totalMnyHight);
                    table.addCell(cell);
                }
            }
        } catch (Exception e) {

        }
        return table;
    }

    private int getLineNum(List<String> columns, String[] fields) {
        int linenum;
        if (iscross != null && iscross.booleanValue()) {
            if (columns.size() != fields.length) {
                linenum = 20;
            } else {
                linenum = 21;
            }
        } else {
            if (columns.size() != fields.length) {
                linenum = 26;
            } else {
                linenum = 27;
            }
        }
        return linenum;
    }

    public void printSimpleColumn(JSONArray array, String titlename, List<String> columns,String[] fields,
                                 int[] widths, Integer pagecount, List<String> list, HttpServletResponse response) throws Exception {
        BaseFont bf = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/fonts/simfang.ttf
        BaseFont bf_Bold = BaseFont.createFont(IGlobalConstants.FONTPATH, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);//IGlobalConstants.FONTPATH
        Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
        int font= 9;
        Font tableBodyFounts = new Font(bf,font, Font.NORMAL);
        Font tableHeadFounts = new Font(bf,font, Font.BOLD);
        float totalAmountHight = 13f;
        float totalMnyHight ;
        totalMnyHight = 22f;//设置双倍行距解决科目显示不完整问题
        float leftsize =(float) (15);
        float topsize =(float) (20);
        Document document = null;
        Rectangle pageSize=null;
        if(iscross!=null && iscross.booleanValue()){
            pageSize = new Rectangle((PageSize.A4.getHeight()), (PageSize.A4.getWidth()));
            document = new Document(pageSize,leftsize,15,topsize,10);
        }else{
            pageSize=PageSize.A4;
            document = new Document(pageSize,leftsize, 15,topsize, 5);
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PdfWriter writer = PdfWriter.getInstance(document, buffer);
        document.open();
        try {
            Map<String, Object> para = new TreeMap<String, Object>();
            para.put("titleFonts", titleFonts);
            para.put("tableHeadFounts", tableHeadFounts);
            para.put("tableBodyFounts", tableBodyFounts);
            para.put("totalAmountHight", totalAmountHight);
            para.put("totalMnyHight", totalMnyHight);
            Paragraph title = new Paragraph();
            title.add(new Chunk(titlename, titleFonts));
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            int linenum=0;
            if(iscross!=null && iscross.booleanValue()){
                if(columns.size()!=fields.length){
                    linenum=21;
                }else{
                    linenum=22;
                }
            }else{
                if(columns.size()!=fields.length){
                    linenum=32;
                }else{
                    linenum=33;
                }
            }
            JSONArray array1=new JSONArray();
            for (int i = 0; i < array.size(); i++) {
                array1.add(array.get(i));
                if((i+1) % linenum== 0 && i != 0) {
                    PdfPTable table = getTableBody(tableBodyFounts, array1, totalMnyHight, columns, fields, titlename ,widths, para, list);
                    document.add(table);
                    array1.clear();
                    if(i<array.size()-1){
                        document.newPage();
                        document.add(title); //输入标题
                    }
                }
            }
            PdfPTable table= getTableBody(tableBodyFounts, array1, totalMnyHight, columns,fields, titlename, widths, para, list);
            document.add(table);

            document.close();
            getResponse().setContentType("application/pdf");
            getResponse().setCharacterEncoding("utf-8");
            getResponse().setContentLength(buffer.size());
            ResourceUtils.doSOStreamAExec(getResponse(), new ResourceUtils.ICloseAction<ServletOutputStream>(){
                @Override
                public Object doAction(ServletOutputStream out) throws Exception {
                    buffer.writeTo(out);
                    return null;
                }

            });
        }catch (Exception e) {
            throw e;
        } finally {
            if(document != null){
                document.close();
            }
        }
    }

    public PdfPTable getTableBody(Font fonts,JSONArray array, float totalMnyHight, List<String> columns,String[] fields,String tilename,
                                  int[] widths, Map<String, Object> para, List<String> list) throws Exception {
        List<ColumnCellAttr> listattr = new ArrayList<ColumnCellAttr>();
        int columnslength=columns.size();
        int fieldslength=fields.length;
        PdfPTable table = new PdfPTable(fieldslength);
        if(columnslength!= fieldslength){
            table.setHeaderRows(2);
        }else{
            table.setHeaderRows(1);
        }
        table.setSpacingBefore(10);
        table.setWidthPercentage(100);
        try {
            table.setWidths(widths);
        } catch (DocumentException e1) {
            throw e1;
        }
        for(int i =0;i<columnslength;i++){
            ColumnCellAttr attr = new ColumnCellAttr();
            attr.setColumname(columns.get(i));
            if(columnslength>fieldslength){
                if(i<=6 ){
                    attr.setRowspan(2);
                }else if(i == 7){
                    attr.setColspan(fieldslength-7);
                }
            }
            listattr.add(attr);
        }
        addTabHead((Font)para.get("tableHeadFounts"), table,totalMnyHight, listattr);
        try {
            PdfPCell cell = null;
            for (int i = 0; i < array.size(); i++) {
                Map<String, Object> map=new HashMap<String, Object>();
                map = (Map<String, Object>) array.get(i);
                for (String key : fields) {
//                  if(map.get(key)!=null&&!map.get(key).toString().equals("0")){
                    if(map.get(key)!=null){
                        if(!list.contains(key)){
                            DZFDouble doublevalue =new DZFDouble(map.get(key).toString());
                            doublevalue = doublevalue.setScale(2, DZFDouble.ROUND_HALF_UP);
                            cell = new PdfPCell(new Phrase(doublevalue.toString(), fonts));
                        }else{
                            cell = new PdfPCell(new Phrase(map.get(key).toString(), fonts));
                        }
                    }else{
                        cell = new PdfPCell(new Phrase("", fonts));
                    }
                    cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                    if(list.contains(key)){
                        cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    }else{
                        cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                    }
                    cell.setFixedHeight(totalMnyHight);
                    table.addCell(cell);
                }
            }
        }
        catch (Exception e) {
            log.error(e.getMessage(), e);
            throw e;
        }
        return table;
    }

    private void addTabHead(Font fonts10_bold, PdfPTable table,float totalMnyHight, List<ColumnCellAttr> listattr) {
        PdfPCell cell;
        int count =0;
        for (ColumnCellAttr columnsname : listattr) {
            cell = new PdfPCell(new Paragraph(columnsname.getColumname(), fonts10_bold));
            if(listattr.get(count).getColspan()!=null && listattr.get(count).getColspan().intValue()>0){//合并
                cell.setFixedHeight(totalMnyHight);
                cell.setColspan(listattr.get(count).getColspan());
            }
            if(listattr.get(count).getRowspan()!=null && listattr.get(count).getRowspan().intValue()>0){//空行
                cell.setPadding(0);
                cell.setFixedHeight(totalMnyHight*listattr.get(count).getRowspan());
                cell.setRowspan(listattr.get(count).getRowspan());
            }
            cell.setFixedHeight(totalMnyHight);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);
            count++;
        }
    }

    public DZFBoolean getBshowzero() {
        return bshowzero;
    }

    public void setBshowzero(DZFBoolean bshowzero) {
        this.bshowzero = bshowzero;
    }
}

class HeaderColumnsInfo {
    private int headrowcount;
    private List<ColumnCellAttr> columnlist;

    public int getHeadrowcount() {
        return headrowcount;
    }

    public void setHeadrowcount(int headrowcount) {
        this.headrowcount = headrowcount;
    }

    public List<ColumnCellAttr> getColumnlist() {
        return columnlist;
    }

    public void setColumnlist(List<ColumnCellAttr> colattrlist) {
        this.columnlist = colattrlist;
    }
}
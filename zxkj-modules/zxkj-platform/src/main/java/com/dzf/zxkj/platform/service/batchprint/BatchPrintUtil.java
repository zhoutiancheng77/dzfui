package com.dzf.zxkj.platform.service.batchprint;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.TreeMap;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.ColumnCellAttr;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.pdf.PrintUtil;
import com.dzf.zxkj.pdf.ReportCoverPrintUtil;
import com.dzf.zxkj.platform.model.batchprint.BatchPrintSetVo;
import com.dzf.zxkj.platform.model.pzgl.VoucherPrintParam;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.voucher.VoucherPrintTemplate;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfAction;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfOutline;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfPage;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.DottedLineSeparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;


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

@Slf4j
public class BatchPrintUtil {

	private DZFBoolean iscross = DZFBoolean.FALSE;// 是否横向

	@Autowired
	private ICorpService corpService;

	@Autowired
	private IZxkjPlatformService zxkjPlatformService;

	private Float lineheight;// 行高

	private Font tableHeadFount;// 行表头字体

	private BaseColor basecolor;// 背景颜色

	private BaseFont bf = null;

	private BaseFont bf_Bold = null;

	public BatchPrintUtil() {
		try {
			bf = BaseFont.createFont("/data1/webApp/font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// C:/windows/fonts/simfang.ttf
			bf_Bold = BaseFont.createFont("/data1/webApp/font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);// IGlobalConstants.FONTPATH;
			setLineheight(19f);// 设置行高
			lineheight = 19f;
			basecolor = new BaseColor(167, 167, 167);
		} catch (DocumentException e) {
			log.error("错误",e);
		} catch (IOException e) {
			log.error("错误",e);
		}
	}
	
	
	public String print(Map<String, SuperVO[]> zzvomaps, Map<String, String> pmap,
						String userid, CorpVO corp, String filename, BatchPrintSetVo setvo, IUserService userServiceImpl) throws Exception {
		float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
		float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
		Document document = null;
		Rectangle pageSize = null;
		ByteArrayOutputStream byteout = null;
		pageSize = PageSize.A4;
		document = new Document(pageSize, leftsize, 15, topsize, 4);

		try {
			filename  =filename + ".pdf" ;
			byteout  = new ByteArrayOutputStream();
			PdfWriter writer = PdfWriter.getInstance(document, byteout);
			document.open();
			PdfContentByte canvas = writer.getDirectContent();
			int pagenum = 1;
			UserVO uvo = userServiceImpl.queryUserJmVOByID(userid);// UserCache.getInstance().get(userid, "");
			for (Entry<String, SuperVO[]> entry : zzvomaps.entrySet()) {
				//凭证单独处理
				SuperVO[] zzvos = entry.getValue();
				String titlename = entry.getKey().split("_")[0];
				String qj =  entry.getKey().split("_")[1];
				qj = qj.replace("~", "--");
				if(titlename.equals("凭证")){
					VoucherPrintParam pzprintparamvo = getVoucherParamvo(zzvos,setvo);
					pagenum = zzvos.length/3+(zzvos.length%3==0?0:1)+1;
				}else if(titlename.equals("总账明细账封皮")) {
					if(pagenum!=1){//第一页本来就是一个新页面
						document.newPage();
					}
					// 赋值首字符的值
					ReportCoverPrintUtil printutil = new ReportCoverPrintUtil(zxkjPlatformService);
					printutil.kmCoverPrint(leftsize, topsize, document, canvas, new String[]{corp.getPk_corp()},"1",pagenum+"");
					PdfContentByte cb = writer.getDirectContent(); 
					PdfOutline root = cb.getRootOutline();   
					new PdfOutline(root, PdfAction.gotoLocalPage(String.valueOf(pagenum), false),"总账明细账封皮" );
					pagenum = pagenum + 1;
				}else if(titlename.equals("凭证封皮")) {
					if(pagenum!=1){//第一页本来就是一个新页面
						document.newPage();
					}
					//打印封皮
					printVoucherCover(corp, document,pagenum+"",canvas);
					PdfContentByte cb = writer.getDirectContent(); 
					PdfOutline root = cb.getRootOutline();
					new PdfOutline(root, PdfAction.gotoLocalPage(String.valueOf(pagenum), false),"凭证封皮" );
					pagenum = pagenum + 1;
				}else{
					//报表打印信息
					//PrintReportAction action  = getaction(titlename);
//					titlename = getPrintTitleName(titlename);
					Object[] obj = getPrintXm(titlename);
					String[] columns = (String[]) obj[0];
					String[] columnnames  = (String[]) obj[1];
					int[] widths  = (int[]) obj[2];
					Integer pagecount  = (Integer) obj[3];
					pagenum = printGroup(document, writer, zzvos, titlename,qj, columns,
							columnnames, null, widths, pagecount, pmap, userid, corp,pagenum,userServiceImpl);
				}
			}
		} catch (Exception e) {
			log.error("错误",e);
			throw new WiseRunException(e);
		} finally {
			if(document!=null){
				document.close();
			}
			if(byteout!=null){
				byteout.close();
			}
		}
		byteout.size();
		byteout.toByteArray();
		FastDfsUtil util = (FastDfsUtil) SpringUtils.getBean("connectionPool");
		String id = util.upload(byteout.toByteArray(), filename, new HashMap<String,String>());
		return id;
	}
	
	public void printVoucherCover(CorpVO corpvo,Document document,String pageNum,PdfContentByte canvas) {
		VoucherPrintParam param = new VoucherPrintParam();
		param.setType( IVoucherConstants.PRINT_A4_TWO);
		param.setLeft(new DZFDouble(16));
		param.setTop(new DZFDouble(16));
		VoucherPrintTemplate template = getTemplate(param);
		String corpNames = CodeUtils1.deCode(corpvo.getUnitname());
		String pagenum = "2";//默认打印一页

		// 打印页面总数
		int pdfTotalPage = 0;
		float originalTop = template.getMarginTop();

		PdfWriter writer = null;
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int tableCount = 1;
		try {
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

			for(String corpName : corpNames.split(",")){
				for(int i = 0; i < Integer.parseInt(pagenum); i++){
					corpNameList.add(corpName);
				}
			}

			for (String corpName : corpNameList) {
				for (int b = 0; b < len || b == 0; b += template
						.getVoucherRows()) {
					if (tableCount > 1) {
						marginTop += voucherHeight;
					}

					Phrase title = new Phrase(
							new Chunk("记  账  凭  证  封  面",
									template.getTitleFont()).setLocalDestination(pageNum)
							);
					ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
							title, template.getTitleLeft() + marginLeft-50,
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
							pzh, template.getPzhLeft() + marginLeft-40,
							template.getInfoTop() - marginTop, 0);// 凭证号

					PdfPTable table = getCoverTable(template);
					table.writeSelectedRows(0, -1, template.getMarginLeft(),
							template.getTableTop() - marginTop, canvas);

					// 偏移量 只针对打印尾部的偏移
					int offset = 0;
					if(param.getType() == 6){
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("负责人（章）", infoFont),
								template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);

						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("会计（章）", infoFont), 70+ offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);
						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("装订（章）", infoFont), 140 + offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);
					}else if(param.getType() == 4){
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("负责人（章）", infoFont),
								template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);

						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("会计（章）", infoFont), 70+ offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);
						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("装订（章）", infoFont), 140 + offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);
					}else if(param.getType() == 1){
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("负责人（章）", infoFont),
								template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+50, 0);

						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("会计（章）", infoFont), 70+ offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+50, 0);
						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("装订（章）", infoFont), 140 + offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+50, 0);
					}else if(param.getType() == 2){
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("负责人（章）", infoFont),
								template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);

						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("会计（章）", infoFont), 70+ offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);
						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("装订（章）", infoFont), 140 + offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+20, 0);
					}else if(param.getType() == 5){
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("负责人（章）", infoFont),
								template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+50, 0);

						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("会计（章）", infoFont), 70+ offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+50, 0);
						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("装订（章）", infoFont), 140 + offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+50, 0);
					}else{
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("负责人（章）", infoFont),
								template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+40, 0);

						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("会计（章）", infoFont), 70+ offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+40, 0);
						offset += template.getOperatorOffset();
						ColumnText.showTextAligned(canvas, Element.ALIGN_LEFT,
								new Phrase("装订（章）", infoFont), 140 + offset
										+ template.getOperatorLeft() + marginLeft,
								template.getOperatorTop() - marginTop+40, 0);
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
		cell1.setFixedHeight(template.getTableBodyHight()+10);
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
		cell.setFixedHeight(template.getTableBodyHight()+10);
		table.addCell(cell);

		PdfPCell cell2 = new PdfPCell();
		Phrase title2 = new Phrase("本月共：        号本册自        号至        号",
				template.getInfoFont());
		cell2.setPhrase(title2);
		cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell2.setFixedHeight(template.getTableBodyHight()+10);
		table.addCell(cell2);

		PdfPCell cell3 = new PdfPCell();
		Phrase title3 = new Phrase("附原始凭证",
				template.getInfoFont());
		cell3.setPhrase(title3);
		cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell3.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell3.setFixedHeight(template.getTableBodyHight()+10);
		table.addCell(cell3);

		PdfPCell cell4 = new PdfPCell();
		Phrase title4 = new Phrase("本册共计：                                张",
				template.getInfoFont());
		cell4.setPhrase(title4);
		cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell4.setFixedHeight(template.getTableBodyHight()+10);
		table.addCell(cell4);


		PdfPCell cell5 = new PdfPCell();
		Phrase title5 = new Phrase("附    件",
				template.getInfoFont());
		cell5.setPhrase(title5);
		cell5.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell5.setHorizontalAlignment(Element.ALIGN_CENTER);
		cell5.setFixedHeight(template.getTableBodyHight()+10);
		table.addCell(cell5);


		PdfPCell cell6 = new PdfPCell();
		Phrase title6 = new Phrase("本册共计：                                张",
				template.getInfoFont());
		cell6.setPhrase(title6);
		cell6.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell6.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell6.setFixedHeight(template.getTableBodyHight()+10);
		table.addCell(cell6);

		table1.addCell(table);
		return table1;
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


	private VoucherPrintParam getVoucherParamvo(SuperVO[] zzvos,BatchPrintSetVo setvo) {
		VoucherPrintParam pzprintparamvo = new VoucherPrintParam();
		pzprintparamvo.setShow_vappr(DZFBoolean.TRUE);
		pzprintparamvo.setType(1);
		pzprintparamvo.setZdr(setvo.getZdrlx());//制单人类型
		pzprintparamvo.setShow_vjz(DZFBoolean.TRUE);
		pzprintparamvo.setLeft(new DZFDouble(16.6));
		pzprintparamvo.setAccount_level(1);
		pzprintparamvo.setUser_name(setvo.getVothername());//指定制单人
		pzprintparamvo.setTop(new DZFDouble(12.7));
		StringBuffer ids = new StringBuffer();
		for(SuperVO svo:zzvos){
			ids.append(svo.getPrimaryKey()+",");
		}
		pzprintparamvo.setIds(ids.substring(0, ids.length()-1));
		return pzprintparamvo;
	}
	
	/** 
     * 根据byte数组，生成文件 
     */  
    public static void getFile(byte[] bfile, String filePath,String fileName) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            File dir = new File(filePath);  
            if(!dir.exists()&&dir.isDirectory()){//判断文件目录是否存在  
                dir.mkdirs();  
            }  
            file = new File(filePath+"\\"+fileName);  
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                }  
            }  
        }  
    }

    private Object[] getPrintXm(String talbename){
		if("科目总账".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"km", "period", "zy", "jf", "df", "fx", "ye"};
			obj[1] = new String[]{"科目", "期间", "摘要", "借方", "贷方", "方向", "余额"};
			obj[2] = new int[]{7, 2, 5, 3, 3, 1, 3};
			obj[3] = 20;
			return obj;
		}else if("科目明细账".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"km","rq","pzh","zy","jf","df","fx","ye"};
			obj[1] = new String[]{"科目","日期","凭证号","摘要","借方","贷方","方向","余额"};
			obj[2] = new int[]{7,3,1,5,3,3,1,3};//没考虑横纵向的问题，结果也不一样
			obj[3] = 20;
			return obj;
		}else if("现金/银行日记账".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"rq","km","pzh","zy","jf","df","fx","ye"};
			obj[1] = new String[]{"日期","科目","凭证号","摘要","借方","贷方","方向","余额"};
			obj[2] = new int[]{2,3,1,4,2,2,1,2};
			obj[3] = 20;
			return obj;
		}else if("资产负债表".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"zc", "hc1", "qmye1", "ncye1", "fzhsyzqy", "hc2", "qmye2", "ncye2"};
			obj[1] = new String[]{"资      产", "行次", "期末余额", "年初余额", "负债和所有者权益", "行次", "期末余额", "年初余额"};
			obj[2] = new int[]{5, 1, 3, 3, 5, 1, 3, 3};
			obj[3] = 20;
			return obj;
		}else if("利润表".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"xm", "hs", "bnljje", "byje"};
			obj[1] = new String[]{"项            目", "行次", "本年累计金额", "本月金额"};
			obj[2] = new int[]{5, 1, 2, 2};
			obj[3] = 20;
			return obj;
		}else if("发生额及余额表".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"kmlb","kmbm","kmmc","qcjf","qcdf","fsjf","fsdf","jftotal","dftotal","qmjf","qmdf"};
			obj[1] = new String[]{"科目类别","科目编码","科目名称","期初余额_借方","期初余额_贷方","本期发生额_借方","本期发生额_贷方","本年累计发生额_借方","本年累计发生额_贷方","期末余额_借方","期末余额_贷方"};
			obj[2] = new int[]{3,4,6,5,5,5,5,5,5,5,5};
			obj[3] = 60;
			return obj;
		}else if("利润表季报".equals(talbename)){
			Object[] obj = new Object[4];
			obj[0] = new String[]{"xm","hs","bnlj","quarterFirst","quarterSecond","quarterThird","quarterFourth","sntqs"};
			obj[1] = new String[]{"项目","行次","本年累计","第一季度","第二季度","第三季度","第四季度","上年同期数"};
			obj[2] = new int[]{6,1,2,2,2,2,2,2};
			obj[3] = 20;
			return obj;
		}
		return null;
	}



	/**
	 * A4宽 打印
	 * 
	 * @param zzvos
	 *            不分页打印信息
	 * @param titlename
	 *            显示表头
	 * @param columns
	 *            显示的字读
	 * @param columnnames 显示的字读名称
	 *
	 * @param listattr
	 *            字段属性
	 * @param widths
	 *            字段宽度
	 * @param pagecount  每页显示行数
	 *
	 *
	 * @param pmap 页尾信息
	 * @throws DocumentException
	 * @throws IOException
	 */
	public Integer printGroup(Document document, PdfWriter writer, SuperVO[] zzvos, String titlename, String qj, String[] columns,
							  String[] columnnames, List<ColumnCellAttr> listattr, int[] widths, Integer pagecount,
							  Map<String, String> pmap, String userid, CorpVO corp, Integer pagenum11, IUserService userServiceImpl)
			throws DocumentException, IOException {
		//打印表头信息(待定)
		Map<String, String> tmap = new LinkedHashMap<String, String>();
		tmap.put("公司", CodeUtils1.deCode(corp.getUnitname()));
		tmap.put("期间", qj);
		tmap.put("单位", "元");
		
		float leftsize = (float) (Float.parseFloat(pmap.get("left")) * 2.83);
		float topsize = (float) (Float.parseFloat(pmap.get("top")) * 2.83);
		
		float totaltablewidth = PageSize.A4.getWidth() - leftsize - 15;
		
		Map<String, Object> para = new TreeMap<String, Object>();
		Font titleFonts = new Font(bf_Bold, 20, Font.BOLD);
		int font = Integer.parseInt(pmap.get("font"));
		Font tableBodyFounts = new Font(bf, font, Font.NORMAL);
		Font tableHeadFounts = getTableHeadFount();
		if (tableHeadFounts == null) {
			tableHeadFounts = new Font(bf, font, Font.NORMAL);
		}
		float totalAmountHight = 13f;
		float totalMnyHight = getLineheight();
		para.put("titleFonts", titleFonts);
		para.put("tableHeadFounts", tableHeadFounts);
		para.put("tableBodyFounts", tableBodyFounts);
		para.put("totalAmountHight", totalAmountHight);
		para.put("totalMnyHight", totalMnyHight);
		
		if (zzvos != null && zzvos.length > 0) {
			Map<String, Float> totalwidthmap = new HashMap<>();
			totalwidthmap = PrintUtil.calculateWidths(columns, columnnames,widths, totaltablewidth);
			// 插入头部信息
			int pageNum = pagenum11 ;
			if(pageNum!=1){//第一页本来就是一个新页面
				document.newPage();
			}
			Paragraph title = new Paragraph();
			title.add(new Chunk(titlename, titleFonts).setLocalDestination(String.valueOf(pageNum)));
			title.setAlignment(Element.ALIGN_CENTER);
			
			document.add(title);
			
			PdfContentByte cb = writer.getDirectContent();   
			PdfOutline root = cb.getRootOutline();   
			  
			String charpername = titlename;
			if("利 润 表".equals(titlename) || "资 产 负 债 表".equals(titlename)){
				charpername = titlename+"("+qj.substring(0, 7)+")";
			}else if("利 润 表 季 报".equals(titlename)){
				charpername = titlename+"("+qj+")";
			}
			// Code 3   
			new PdfOutline(root, PdfAction.gotoLocalPage(String.valueOf(pagenum11), false),charpername );
			document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
			creatTitle(tmap, writer, tableHeadFounts, iscross, titlename, leftsize, topsize);
			int line = 0;
			List<SuperVO> zzvosList = new ArrayList<SuperVO>();
			line = getPDfLineNum(zzvos, titlename, listattr, widths);// 获取每页显示的行数
			for (int i = 0; i < zzvos.length; i++) {
				zzvosList.add(zzvos[i]);
				if ((i + 1) % line == 0 && i != 0) {
					SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
					PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
							pagecount, titlename, totalwidthmap);
					document.add(table);
					document.add(getFoot(tableHeadFounts, pmap.get("printdate"), pageNum-pagenum11+1, titlename, userid, corp,userServiceImpl));
					zzvosList.clear();

					if (i < zzvos.length - 1) {
						pageNum++;
						document.newPage();
						document.add(title); // 输入标题
						creatTitle(tmap, writer, tableHeadFounts, iscross, titlename, leftsize, topsize);
						document.add(new Chunk(PrintUtil.getSpace(1), tableHeadFounts));
					}
				}
			}
			SuperVO[] zzvoArray = zzvosList.toArray(new SuperVO[0]);
			PdfPTable table = addTableByTzpzBvo(zzvoArray, 0, para, columns, columnnames, listattr, widths,
					pagecount, titlename, totalwidthmap);
			document.add(table);
			if (zzvoArray != null && zzvoArray.length > 0) {
				document.add(getFoot(tableHeadFounts, pmap.get("printdate"), pageNum-pagenum11+1, titlename, userid, corp,userServiceImpl));
			}
			return  pageNum+1;
		}else{
			return 1;
		}
	}

	private int getPDfLineNum(SuperVO[] zzvos, String titlename, List<ColumnCellAttr> listattr, int[] widths) {
		int line;
		if (iscross != null && iscross.booleanValue()) {
			line = 22;
			if (titlename.equals("数 量 金 额 明 细 账 ") || titlename.equals("数 量 金 额 总 账")
					|| titlename.equals("发 生 额 及 余 额 表") || titlename.equals("辅 助 余 额 表")
					|| titlename.equals("序 时 账")) {
				line = 21;
				if (titlename.equals("序 时 账") && widths.length == listattr.size()) {
					line = 22;
				}
			}
			if (titlename.equals("辅 助 明 细 账")) {
				line = 21;
			}
		} else if ("现 金 流 量 表".equals(titlename) && zzvos.length > 33) {
			line = 38;
		} else {
			line = 33;
		}
		return line;
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
		table.setSpacingBefore(para.get("tableheight") == null ? 2 : (float) para.get("tableheight"));
		table.setWidthPercentage(100);
		table.setWidths(withs);

		// 表头
		addTabHead((Font) para.get("tableHeadFounts"), table, (float) para.get("totalMnyHight"), listattr);
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

	/**
	 * 由带分隔符_的columnsname解析得到能展示多层表头的columncellattrlist
	 * 
	 * @author llh
	 * @param columnnames
	 * @return
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

	private void addTabHead(Font fonts10_bold, PdfPTable table, float totalMnyHight, List<ColumnCellAttr> listattr) {
		PdfPCell cell;
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
					if (bvo.getAttributeValue(key) == null
							|| ((DZFDouble) bvo.getAttributeValue(key)).doubleValue() == 0.00) {
						cell = new PdfPCell(new Phrase("", fonts));
					} else {
						if (tilename.equals("数 量 金 额 明 细 账") || tilename.equals("数 量 金 额 总 账")
								|| tilename.equals("出 库 单") || tilename.equals("入 库 单") || tilename.equals("库存成本表")
								|| tilename.equals("科目期初") || tilename.equals("入 库 单")
								|| tilename.equals("出 库 单") || tilename.indexOf(
										"库存明细账") > 0/*
													 * || tilename.equals(
													 * "工 资 表")
													 */) {
							String jdStr[] = new String[] { "nnum", "nprice", "ndnum", "ndprice", "nynum", "nyprice",
									"qcnum", "qcprice", "bqjfnum", "bqdfnum", "bnjfnum", "bndfnum", "qmnum", "qmprice",
									"num", "sl", "bnqcnum", "bndffsnum", "monthqmnum", "bnfsnum", "srsl", "srdj",
									"fcsl", "fcdj", "jcsl", "jcdj" };
							List<String> jdlist = Arrays.asList(jdStr);
							if (jdlist.contains(key)) {
								tvalue = String.format("%1$,.4f",
										((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
								cell = new PdfPCell(new Phrase(tvalue,
										getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
							} else {
								tvalue = String.format("%1$,.2f",
										((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
								cell = new PdfPCell(new Phrase(tvalue,
										getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
							}
						} else if (tilename.equals("序 时 账")
								&& (key.equals("hl") || key.equals("ybjf") || key.equals("ybdf"))) {
							tvalue = String.format("%1$,.4f", ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
							cell = new PdfPCell(new Phrase(tvalue,
									getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
						} else {
							tvalue = String.format("%1$,.2f", ((DZFDouble) bvo.getAttributeValue(key)).doubleValue());
							cell = new PdfPCell(new Phrase(tvalue,
									getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
						}
					}
					if (getBasecolor() != null) {
						cell.setBorderColor(getBasecolor());
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				} else if (bvo.getClass().getDeclaredField(key).getType() == DZFDate.class) {
					if (bvo.getAttributeValue(key) == null) {
						cell = new PdfPCell(new Phrase("", fonts));
					} else {
						tvalue = ((DZFDate) bvo.getAttributeValue(key)).toString();
						cell = new PdfPCell(
								new Phrase(tvalue, getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
					}
					if (getBasecolor() != null) {
						cell.setBorderColor(getBasecolor());
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);// getDeclaredField可获取所有定义变量，而getField只能获取public的属性变量
				} else if (bvo.getClass().getDeclaredField(key).getType() == Integer.class) {
					if ("direct".equals(key)) {
						if (bvo.getAttributeValue("direct").toString().equals("0")) {
							cell = new PdfPCell(new Phrase("借", fonts));
						} else {
							cell = new PdfPCell(new Phrase("贷".toString(), fonts));
						}
					} else {
						tvalue = (bvo.getAttributeValue(key)).toString();
						cell = new PdfPCell(
								new Phrase(tvalue, getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
					}
					if (getBasecolor() != null) {
						cell.setBorderColor(getBasecolor());
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				} else if (bvo.getClass().getDeclaredField(key).getType() == DZFBoolean.class) {
					if (bvo.getAttributeValue(key) != null) {
						if ("Y".equals(bvo.getAttributeValue(key).toString())) {// 通过判断值为Y或N来输出“是”“否”  bvo.getAttributeValue(key).toString() == "Y"
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
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				} else {
					tvalue = (String) bvo.getAttributeValue(key);
					cell = new PdfPCell(
							new Phrase(tvalue, getAutoFont(fonts, tvalue, totalwidthmap.get(key), totalMnyHight)));
					if (tilename.equals("科 目 总 账") && key.equals("km")) {
						if (bvo.getAttributeValue(key) == null) {
							continue;
						}
						cell.setRowspan(bvo.getAttributeValue("rowspan") == null ? 1
								: (Integer) bvo.getAttributeValue("rowspan"));
					} else if (tilename.equals("财 务 概 要 信 息 表") && (key.equals("hs") || key.equals("xmfl"))) {
						if (bvo.getAttributeValue(key) == null) {
							continue;
						}
						cell.setRowspan(bvo.getAttributeValue("rowspan") == null ? 1
								: (Integer) bvo.getAttributeValue("rowspan"));
					}
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					if (tilename.equals("资 产 负 债 表") && ("hc1".equals(key) || "hc2".equals(key))) {
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					} else if (tilename.equals("利 润 表") && "hs".equals(key)) {
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					} else if (tilename.equals("现 金 流 量 表") && "hc".equals(key)) {
						cell.setHorizontalAlignment(Element.ALIGN_CENTER);
					} else {
						cell.setHorizontalAlignment(Element.ALIGN_LEFT);
					}
					if (getBasecolor() != null) {
						cell.setBorderColor(getBasecolor());
					}
					cell.setFixedHeight(totalMnyHight);
					table.addCell(cell);
				}
			}
		} catch (NoSuchFieldException e) {
			log.error("错误",e);
		} catch (SecurityException e) {
			log.error("错误",e);
		}
	}

	private Font getAutoFont(Font fonts, String value, Float width, Float height) {
		if (width == null || width == 0) {
			return fonts;
		}
		if (height == null || height == 0) {
			return fonts;
		}

		Font resfont = null;
		// 获取当前值的长度
		Integer[] wd = PrintUtil.getStrFont(value, ((Float) fonts.getSize()).intValue());// 长宽，维度
		Integer height_str = wd[0];
		Integer widht_str = wd[1];
		int div = (int) (height / height_str);
		if (width >= widht_str || ((div) * width) >= widht_str || fonts.getSize() == 1) {
			resfont = fonts;
			return resfont;
		} else {
			resfont = new Font(fonts.getBaseFont(), fonts.getSize() - 1, fonts.getStyle());
			return getAutoFont(resfont, value, width, height);
		}
	}

	public Paragraph getFoot(Font tableHeadFounts, String printvalue, int pageNum, String titlename, String userid,
			CorpVO corp,IUserService userServiceImpl) {
		Paragraph foot = new Paragraph();
		UserVO userVo = userServiceImpl.queryUserJmVOByID(userid);// UserCache.getInstance().get(userid, corp.getPk_corp());
		String faren = null;
		try {
			faren = CodeUtils1.deCode(corp.getLegalbodycode());
		} catch (Exception e) {
		}
		if (titlename.equals("资 产 负 债 表") || titlename.equals("现 金 流 量 表") || titlename.equals("利 润 表")
				|| titlename.equals("利 润 表 季 报") || titlename.equals("增值税和营业税月度申报对比表") || titlename.equals("业 务 活 动 表")
				|| titlename.equals("收 入 支 出 表")) {
			Chunk user = new Chunk("制表人：" + userVo.getUser_name() + PrintUtil.getSpace(10) + "单位负责人："
					+ (faren == null ? "" : faren) + PrintUtil.getSpace(10) + "打印日期：" + printvalue, tableHeadFounts);
			foot.add(user);
		} else {
			Chunk printdate = new Chunk("打印日期：" + printvalue + PrintUtil.getSpace(9) + "第" + pageNum + "页",
					tableHeadFounts);
			foot.add(printdate);
		}
		return foot;
	}

	private void creatTitle(Map<String, String> tmap, PdfWriter writer, Font tableHeadFounts, DZFBoolean iscross,
			String titlename, float leftsize, float topsize) throws DZFWarpException {
		int a = (int) (807 - topsize);
		int c = (int) (leftsize + 4);
		int d = (int) (270);
		int b = 260;
		int f = 160;
		if (titlename.equals("科目期初")) {
			b = 200;
		}
		if (iscross != null &&!iscross.booleanValue()) {
			if (titlename.equals("入 库 单") || titlename.equals("出 库 单")) {
				b = 130;
			}
		}

		if (iscross != null && iscross.booleanValue()) {
			d = 400;
			a = 540;
		}

		if (titlename.equals("数 量 金 额 明 细 账")) {
			b = 150;
		}
		try {
			int count = 0;
			for (String ck : tmap.keySet()) {
				Chunk chunk = new Chunk(ck + ":" + tmap.get(ck), tableHeadFounts);
				Phrase phrase = new Phrase(chunk);
				ColumnText column = new ColumnText(writer.getDirectContent());
				if (titlename.indexOf("科目明细账") > 0 && ck.equals("科目")) {
					column.setSimpleColumn(phrase, c, 100, 800, a - 13, 5, Element.ALIGN_LEFT);
					column.go();
					continue;
				} else {
					if (count == tmap.size() - 1) {
						if (titlename.equals("入 库 单") || titlename.equals("出 库 单")) {
							column.setSimpleColumn(phrase, c, 100, d - 10, a, 5, Element.ALIGN_LEFT);
						} else if (titlename.equals("数 量 金 额 明 细 账")) {
							column.setSimpleColumn(phrase, 0, 100, d - 56, a, 5, Element.ALIGN_RIGHT);
						} else {
							if (iscross != null && iscross.booleanValue()) {
								column.setSimpleColumn(phrase, 0, 100, PageSize.A4.getHeight() - 15, a, 5,
										Element.ALIGN_RIGHT);
							} else {
								column.setSimpleColumn(phrase, 0, 100, PageSize.A4.getWidth() - 15, a, 5,
										Element.ALIGN_RIGHT);
							}
						}
					} else {
						column.setSimpleColumn(phrase, c, 100, d, a, 5, Element.ALIGN_LEFT);
					}
				}
				column.go();
				c = c + b;
				d = d + f;
				count++;
			}
		} catch (DocumentException e) {
			throw new WiseRunException(e);
		}
	}
	
	public PdfPTable getAccountUnitTable(String pk_corp, Font tableBodyFontsB, Font tableBodyFonts) throws UnsupportedEncodingException {
		CorpVO corpVo = corpService.queryByPk(pk_corp);
		PdfPCell cell = new PdfPCell();
		cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
		cell.setHorizontalAlignment(Element.ALIGN_LEFT);
		cell.setBorder(Rectangle.NO_BORDER);

		Phrase zy = new Phrase();
		zy.add(new Chunk("核算单位：", tableBodyFontsB));
		zy.add(new Chunk(corpVo.getUnitname(), tableBodyFonts));
		cell.setPhrase(zy);
		PdfPTable table = new PdfPTable(1);
		table.setTotalWidth((float) 210);
		table.setLockedWidth(true);
		table.addCell(cell);//
		return table;
	}
	
	public DZFBoolean getIscross() {
		return iscross;
	}

	public void setIscross(DZFBoolean iscross) {
		this.iscross = iscross;
	}

	public Float getLineheight() {
		return lineheight;
	}

	public void setLineheight(Float lineheight) {
		this.lineheight = lineheight;
	}

	public Font getTableHeadFount() {
		return tableHeadFount;
	}

	public void setTableHeadFount(Font tableHeadFount) {
		this.tableHeadFount = tableHeadFount;
	}

	public BaseColor getBasecolor() {
		return basecolor;
	}

	public void setBasecolor(BaseColor basecolor) {
		this.basecolor = basecolor;
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

	private BaseFont getBaseFont(String fontName) {
		fontName = fontName == null || !"MSYH".equals(fontName.toUpperCase()) ? "SIMKAI.TTF" : "MSYH.TTF";
		String fontPath = IGlobalConstants.FONTPATH;
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
}

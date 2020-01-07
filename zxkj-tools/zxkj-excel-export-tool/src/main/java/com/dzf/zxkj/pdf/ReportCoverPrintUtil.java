package com.dzf.zxkj.pdf;

import java.io.IOException;

import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;

/**
 * 页面封皮打印
 * @author zhangj
 *
 */
public class ReportCoverPrintUtil {

	private IZxkjPlatformService zxkjPlatformService;


	public ReportCoverPrintUtil(IZxkjPlatformService zxkjPlatformService) {
		this.zxkjPlatformService = zxkjPlatformService;
	}

	/**
	 * 科目账表打印
	 * @param leftsize
	 * @param topsize
	 * @param document
	 * @param canvas
	 * @throws DocumentException
	 * @throws IOException
	 */
	public void kmCoverPrint( float leftsize, float topsize, Document document,
			PdfContentByte canvas, String[] cpids, String page_num,String start_page_num) throws DocumentException, IOException {
		BaseFont bf = BaseFont.createFont("/data1/webApp/font/simsun.ttc,0", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		Font titlefont = new Font(bf, 15, Font.BOLD);
		Font titlefont1 = new Font(bf, 20, Font.BOLD);//
		Font titlefont_kh = new Font(bf, 12, Font.BOLD);// 括号
		Font basefont = new Font(bf, 10, Font.NORMAL);

		CorpVO[] cpvos =zxkjPlatformService.queryCorpByPks(cpids);

		for (int k =0;k<cpvos.length;k++) {
			if(k !=0){
				document.newPage();
			}
			for (int i = 0; i < Integer.parseInt(page_num); i++) {
				if (i == 0) {
					kmCoverPrint(document, cpvos[k], titlefont, titlefont1, titlefont_kh, basefont,start_page_num);
				} else {
					document.newPage();
					kmCoverPrint(document, cpvos[k], titlefont, titlefont1, titlefont_kh, basefont,start_page_num);
				}
			}
		}
	}

	private void kmCoverPrint(Document document, CorpVO cpvo, Font titlefont, Font titlefont1, Font titlefont_kh,
			Font basefont,String start_page_num) throws DocumentException {
		Paragraph gs = null;
		if(StringUtil.isEmpty(start_page_num)){
			gs = new Paragraph(new Chunk(cpvo.getUnitname(), titlefont));
		}else {
			gs = new Paragraph(new Chunk(cpvo.getUnitname(), titlefont).setLocalDestination(start_page_num));
		}
		gs.setAlignment(Element.ALIGN_CENTER);
		document.add(gs);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		
		Paragraph 	title = new Paragraph(new Chunk("会   计   账   簿", titlefont1));
		title.setAlignment(Element.ALIGN_CENTER);
		document.add(title);
		document.add(Chunk.NEWLINE);
		
		//分类
		Paragraph fl1 = new Paragraph(new Chunk("总分类账：", titlefont));
		fl1.add(new Chunk("□",titlefont_kh));
		fl1.setAlignment(Element.ALIGN_CENTER);
		document.add(fl1);
		Paragraph fl2 = new Paragraph(new Chunk("明 细 账：", titlefont));
		fl2.add(new Chunk("□",titlefont_kh));
		fl2.setAlignment(Element.ALIGN_CENTER);
		document.add(fl2);
		
		
		//内容
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		Paragraph content1 = new Paragraph(new Chunk("第_____册  共_____册",basefont));
		content1.setAlignment(Element.ALIGN_CENTER);
		document.add(content1);
		Paragraph content2 = new Paragraph(new Chunk("会计年度：__________",basefont));
		content2.setAlignment(Element.ALIGN_CENTER);
		document.add(content2);
		Paragraph content3 = new Paragraph(new Chunk("    年    月    日    至     年    月    日",basefont));
		content3.setAlignment(Element.ALIGN_CENTER);
		document.add(content3);
		
		
		//负责人
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		document.add(Chunk.NEWLINE);
		Paragraph cwfzr = new Paragraph(new Chunk("财务负责人：_______ 记账：_______ 复核：_______ 装订：_______ ",basefont));
		cwfzr.setAlignment(Element.ALIGN_CENTER);
		document.add(cwfzr);
	}
	
}

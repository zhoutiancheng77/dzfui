package com.dzf.zxkj.app.pub;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import com.dzf.zxkj.app.model.ticket.FontText;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.utils.StringUtil;

/**
 * 生成发票图片
 * @author zhangj
 *
 */
public class GenTickImageUtil {

	
	public static void main(String[] args) {
		String filePath = "d:\\app_model.jpg";
		String outPath = "d:\\app_model1.jpg";

		List<FontText> fonttexts = new ArrayList<FontText>();

//		fonttexts.add(new FontText("增值税普通发票", 0, 60, "#7F4E2A", 26, "黑体"));
		fonttexts.add(new FontText("2016-01-01", 262, 212));
		fonttexts.add(new FontText("001", 730, 212));
		fonttexts.add(new FontText("北京大账房信息技术有限公司", 288, 292));
		fonttexts.add(new FontText("战神笔记本", 122, 390,20));
		fonttexts.add(new FontText("6700", 656, 390,20));
		fonttexts.add(new FontText("神舟公司", 300, 570));

		drawTextInImg(filePath, outPath, fonttexts.toArray(new FontText[0]));
	}

	public static void drawTextInImg(String filePath, String outPath, FontText[] texts) {
		
		ImageIcon imgIcon = new ImageIcon(filePath);
		Image img = imgIcon.getImage();
		int width = img.getWidth(null);
		int height = img.getHeight(null);
		BufferedImage bimage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

		Graphics2D g = bimage.createGraphics();
		g.drawImage(img, 0, 0, null);
		for (int i = 0;i<texts.length;i++) {
			g.setColor(getColor(texts[i].getWm_text_color()));
			g.setBackground(Color.white);
			Font font = null;
			if (texts[i].getWm_text_font() != null && texts[i].getWm_text_size() != null) {
				font = new Font(texts[i].getWm_text_font(), Font.BOLD, texts[i].getWm_text_size());
			} else {
				font = new Font("宋体", Font.BOLD, 12);
			}
			g.setFont(font);
//			if(i ==0 ){//位置居中自己计算
//				texts[i].setWm_text_pos_w(getCenterWith(g,font,width,texts[i]));
//			}

			g.drawString(texts[i].getText() == null ?"" :texts[i].getText(), texts[i].getWm_text_pos_w(), texts[i].getWm_text_pos_h());
		}
		g.dispose();
		
		File file = new File(outPath);
		if(!file.getParentFile().exists())
			file.getParentFile().mkdirs();
		
		try {
			FileOutputStream out = new FileOutputStream(outPath);
			ImageIO.write(bimage, "JPEG", out);
			out.close();
		} catch (FileNotFoundException e) {
			throw new WiseRunException(e);
		} catch (IOException e) {
			throw new WiseRunException(e);
		}
	}

	private static int getCenterWith(Graphics2D g, Font font, int width, FontText fontText) {
		FontMetrics fm = g.getFontMetrics(font);
		int textWidth = fm.stringWidth(fontText.getText());
		int widthX = (width - textWidth) / 2;
		return widthX;
	}

	public static Color getColor(String color) {
		if(StringUtil.isEmpty(color)){
			color = "#444444";
		}
		if (color.charAt(0) == '#') {
			color = color.substring(1);
		}
		if (color.length() != 6) {
			return null;
		}
		try {
			int r = Integer.parseInt(color.substring(0, 2), 16);
			int g = Integer.parseInt(color.substring(2, 4), 16);
			int b = Integer.parseInt(color.substring(4), 16);
			return new Color(r, g, b);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}
	
}

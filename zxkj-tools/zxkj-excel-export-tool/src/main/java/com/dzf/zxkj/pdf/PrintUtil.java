package com.dzf.zxkj.pdf;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class PrintUtil {

	public static Integer[] getStrFont(String str,Integer fontsize) {
		if (StringUtil.isEmpty(str)) {
			return new Integer[] { 0, 0 };
		}

		Integer[] fs = new Integer[2];

		Font f = new Font("宋体", Font.PLAIN, fontsize);

		FontMetrics fm = sun.font.FontDesignMetrics.getMetrics(f);
		
		fs[0] = fm.getHeight();
		fs[1] = fm.stringWidth(str)+10;//6默认的一个误差值

		return fs;
	}
	
	public static Map<String, Float> calculateWidths(String[] columns,String[] columnames,int[] widths ,float totalWidth)
    {
		Map<String, Float> tmap = new HashMap<String, Float>();
		
		float[] absoluteWidths = new float[widths.length];
        if(totalWidth <= 0.0F)
            return null;
        float total = 0.0F;
        int numCols = widths.length;
        for(int k = 0; k < numCols; k++)
            total += widths[k];

        for(int k = 0; k < numCols; k++){
        	absoluteWidths[k] = (totalWidth * widths[k]) / total;
        	tmap.put(columns[k], absoluteWidths[k]);
			if (columnames != null) {
				tmap.put(columnames[k], absoluteWidths[k]);
			}
        }
        
        return tmap;

    }
	
	//空格
	public static String getSpace(int num) {
		StringBuffer sf = new StringBuffer();
		for (int i = 0; i < num; i++) {
			sf.append(" ");
		}
		return sf.toString();
	}

	
	public static com.itextpdf.text.Font getAutoFont(com.itextpdf.text.Font fonts, String value, Float width, Float height, DZFBoolean isNum) {
		return fonts;
//		if (width == null || width == 0) {
//			return fonts;
//		}
//		if (height == null || height == 0) {
//			return fonts;
//		}
//
//		com.itextpdf.text.Font resfont = null;
//		// 获取当前值的长度
//		Integer[] wd = PrintUtil.getStrFont(value, ((Float) fonts.getSize()).intValue());// 长宽，维度
//		Integer height_str = wd[0];
//		Integer widht_str = wd[1];
//		int div = (int) (height / height_str);
//		if((isNum!=null && isNum.booleanValue() &&  width.intValue() > widht_str+2) || fonts.getSize() == 1){
//			resfont = fonts;
//			return resfont;
//		}else if ( (isNum == null || !isNum.booleanValue()) && (
//				width >= widht_str || ((div) * width) >= widht_str) || fonts.getSize() == 1) {
//			resfont = fonts;
//			return resfont;
//		} else {
//			resfont = new com.itextpdf.text.Font(fonts.getBaseFont(), fonts.getSize() - 1f, fonts.getStyle());
//			return getAutoFont(resfont, value, width, height,isNum);
//		}
	}
	
}

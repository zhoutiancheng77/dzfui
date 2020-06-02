package com.dzf.zxkj.app.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;


@Slf4j
public class PinyinUtil {
	
    /**
     * 将字符串中的中文转化为拼音,其他字符不变
     * @param inputString
     * @return
     */
    public static String getPinYin(String inputString){
        HanyuPinyinOutputFormat format = new HanyuPinyinOutputFormat();
        format.setCaseType(HanyuPinyinCaseType.LOWERCASE);//小写
        format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);//不知道这个是什么意思 写完查api看看
        format.setVCharType(HanyuPinyinVCharType.WITH_V);//
         
        char[] input = inputString.trim().toCharArray();
        StringBuffer output= new StringBuffer();
         
        try {
            for(int i = 0; i < input.length; i++){
                if(java.lang.Character.toString(input[i]).matches("[\\u4E00-\\u9FA5]+")){
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(input[i],format);
                        if(temp != null && temp.length > 0){
                            output.append(temp[0]);
                        }
                }else{
                    output.append(input[i]);
                }
            }
        }catch (Exception e) {
            log.error("错误",e);
        }
        return output.toString();
    }
    /**
     * 获取汉字串拼音首字母，英文字符不变
     * @param chinese  汉字串 
     * @return 汉语拼音首字母
     */
    public static String getFirstSpell(String chinese){
        StringBuffer pybf = new StringBuffer();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        chinese = getManyPY(chinese,true);
        char[] arr = chinese.toCharArray();
        for(int i = 0;i<arr.length;i++){
        	if(isChineseChar(arr[i])){
        		if(arr[i]>128){
        			try {
        				String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i],defaultFormat);
        				if(temp!=null){
        					pybf.append(temp[0].charAt(0));
        				}else{
        					pybf.append(arr[i]);
        				}
        			} catch (BadHanyuPinyinOutputFormatCombination e) {
        				log.error("错误",e);
        			}
        		}
        	}else{
        		pybf.append(arr[i]);
        	}
        }
        return pybf.toString().replaceAll("\\W", "").trim();
    }
    //多音字，这里单独处理
    private static String getManyPY(String hz,boolean isfirstS){
    	if(StringUtil.isEmpty(hz)){
    		return hz;
    	}
    	if(hz.contains("银行")){
    		if(isfirstS){
    			hz = hz.replace("银行", "yh");
    		}else{
    			hz = hz.replace("银行", "yinhang");
    		}
    	}
    	if(hz.contains("厦门")){
    		if(isfirstS){
    			hz = hz.replace("厦门", "xm");
    		}else{
    			hz = hz.replace("厦门", "xiamen");
    		}
    	}
    	if(hz.contains("重庆")){
    		if(isfirstS){
    			hz = hz.replace("重庆", "cq");
    		}else{
    			hz = hz.replace("重庆", "chongqing");
    		}
    	}
    	return hz;
    }
    /**
     * 获取汉字串拼音，英文字符不变  
     * @param chinese  汉字串  
     * @return 汉语拼音 
     */
    public static String getFullSpell(String chinese){
        StringBuffer pybf = new StringBuffer();
        char[] arr = chinese.toCharArray();
         
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        defaultFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
         
        for(int i = 0;i<arr.length;i++){
            try {
                if(isChineseChar(arr[i])){
                    if(arr[i]>128){
                        String[] temp = PinyinHelper.toHanyuPinyinStringArray(arr[i],defaultFormat);
                        if(temp!=null){
                            pybf.append(temp[0]);
                        }else{
                            pybf.append(arr[i]);
                        }
                    }else{
                        pybf.append(arr[i]);
                    }
                }
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                log.error("错误",e);
            }
        }
        return pybf.toString();
    }
    
    public static boolean isChinese(String str){
        boolean temp = false;
        Pattern p=Pattern.compile("[\u4e00-\u9fa5]"); 
        Matcher m=p.matcher(str); 
        if(m.find()){ 
            temp =  true;
        }
        return temp;
    }
    
    private static boolean isChineseChar(char c) {   
//        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);  
//        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS  
//                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS  
//                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A  
//                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION  
//                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION  
//                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {  
//            return true;  
//        }  
//        return false; 
    	//只判断汉字
    	if((c >= 0x4e00)&&(c <= 0x9fbb)) {  
    		return true;  
    	} 
    	return false;
    } 
}
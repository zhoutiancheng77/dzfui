package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;

@Slf4j
public class OcrUtil {

	
	public static String execInvname(String name){
		String oldName=name;
		try {
			// 解析*AAA*BBBB和*AAA**BBB*CCCC
			// *aaaa
			if(StringUtil.isEmpty(name)||!name.startsWith("*")) return name;
			char cname[] = name.toCharArray();
			int type =0;//0代表*AAA*BBBB 1代表*AAA**BBB*CCCC
			for (int i = 1; i < cname.length - 1; i++) {
				if(cname[i]=='*'){
					if(cname.length > i && cname[i+1]=='*'){
						type=1;
						break;
					}
				}
			}
			
			if(type==0){
				name = name.substring(1, name.length());
				name = name.substring(name.indexOf("*") + 1, name.length());
				return name.equals("")?oldName:name;
			}
			if(type == 1){
				int tt = name.indexOf("**");
				name = name.substring(tt + 2, name.length());
				tt = name.indexOf("*");
				name = name.substring(tt + 1, name.length());
				return name.equals("")?oldName:name;
			}
		}
		catch (Exception ex)
		{

			log.error(ex.getMessage(), ex);
		}
		return name.equals("")?oldName:name;
	}
	
	public static void main(String[] args) {
		String ssd = execInvname("*AAA**BBBB1*1111*1");
		System.out.println(ssd);
		ssd = execInvname("*AAA**BBBB1*111*3");
		System.out.println(ssd);
		ssd = execInvname("*AAA**BBBB1*111*3****");
		System.out.println(ssd);
		ssd = execInvname("*AAA**");
		System.out.println(ssd);
		ssd = execInvname("*AAA*B111");
		System.out.println(ssd);
		ssd = execInvname("*AAA*B111*33");
		System.out.println(ssd);
	}
	
	/**
	 * 票上的税率
	 * @param itemtaxrate
	 * @return
	 */
	public static DZFDouble getInvoiceSL(String itemtaxrate){
		if (StringUtil.isEmpty(itemtaxrate)) {
			return DZFDouble.ZERO_DBL;
		} else {// 创建税率目录
			itemtaxrate = itemtaxrate.replaceAll("%", "");
			try {
				DZFDouble slDbl = new DZFDouble(itemtaxrate).setScale(0, DZFDouble.ROUND_HALF_UP);
				return slDbl;
			} catch (Exception e) {
				return DZFDouble.ZERO_DBL;
			}
		}
	}
	
	public static Object clonAll(Object source){
        ObjectOutputStream os = null;
        ObjectInputStream ois = null;
        try{
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            os = new ObjectOutputStream(bos);
            os.writeObject(source);

            ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
            ois = new ObjectInputStream(bis);
            Object target = ois.readObject();
            return target;
        } catch (Exception e) {
        	//Logger log = Logger.getLogger(OcrUtil.class);
			log.error(e.getMessage(), e);

        }finally {
            try {
                ois.close();
                os.close();
            } catch (IOException e) {
                os = null;
                ois=null;
            }
        }
        return null;
    }
	
	public static String addZeroForNum(String str, int strLength) {
		int strLen = str.length();
		if (strLen < strLength) {
			while (strLen < strLength) {
				StringBuffer sb = new StringBuffer();
				sb.append("0").append(str);// 左补0
				str = sb.toString();
				strLen = str.length();
			}
		}
		return str;
	}
	
	public static String turnMnyByCurrency(String mny)throws DZFWarpException {
		String returnDbl=mny;
		if(!StringUtil.isEmpty(mny)){
			try{
				StringBuffer strbuf = new StringBuffer();
				for (char ch : mny.toCharArray())
				{
					if (ch == '+' || ch == '-' || ch == '.' || ch >= '0' && ch <= '9')
					{
						strbuf.append(ch);
					}
				}
				returnDbl = new DZFDouble(strbuf.toString()).toString();
			}catch(Exception e){
				returnDbl=mny.substring(3);
			}
		}
		return returnDbl;
	}
	
	/**
	 * @param unitName 本公司名字，corpVO里的
	 * @param custName 客户或供应商名字
	 * @return
	 */
	public static boolean isSameCompany(String unitName,String custName){
		if(StringUtil.isEmpty(unitName)||StringUtil.isEmpty(custName)){
			return false;
		}
		unitName=unitName.replaceAll("[()（）\\[\\]]", "").trim().toUpperCase();
		custName=custName.replaceAll("[()（）\\[\\]]", "").trim().toUpperCase();
		if(StringUtil.isEmpty(unitName)||StringUtil.isEmpty(custName)){
			return false;
		}
		if(unitName.startsWith(custName)&&unitName.length()-custName.length()<5){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 过滤公司名称，主要用于导入数据时，公司名称字段含义非法字符，过滤掉
	 * 此方法会留下键盘可输入的字符（包括空格），和正常汉字，和一对全角小括号，
	 * @param corpname
	 * @return
	 */
	public static String filterCorpName(String corpname)
	{
		if (StringUtil.isEmptyWithTrim(corpname))
		{
			return null;
		}
		corpname = corpname.trim();
		StringBuffer sbfReturn = new StringBuffer();
		for(int i = 0; i < corpname.length(); i++){
			String s = corpname.substring(i, i + 1);//单个地获取每个字符
	        int iAscii = (int)s.charAt(0);
	        if (iAscii >= 32 && iAscii <= 126 || s.compareTo("\u4e00") >= 0 && s.compareTo("\u9fa5") <= 0 || s.equals("（") || s.equals("）")) 
	        {
	        	sbfReturn.append(s);
	        }
	    }
		return sbfReturn.toString();
	}
	/**
	 * 过滤掉asc码<=32的控制符, 161-160的不可见字符
	 * @param str 公司名称
	 * @return
	 */
	public static String filterString(String str)
	{
		if (StringUtil.isEmptyWithTrim(str))
		{
			return null;
		}
		str = str.trim();
		StringBuffer sbfReturn = new StringBuffer();
		for(int i = 0; i < str.length(); i++){
	        int iAscii = (int)str.charAt(i);
	        if (iAscii >= 32 && iAscii <= 126 || iAscii >= 161) 
	        {
	        	sbfReturn.append(str.charAt(i));
	        }
	    }
		return sbfReturn.toString();
	}
}

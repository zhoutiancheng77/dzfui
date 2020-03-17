package com.dzf.zxkj.common.utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{
  private static final String FOLDER_SEPARATOR = "/";
  private static final String WINDOWS_FOLDER_SEPARATOR = "\\\\";
  private static final String TOP_PATH = "..";
  private static final String CURRENT_PATH = ".";
  public static final String HYPHEN = " ";

  public static String substringBetween(String source, String strBegin, String strEnd)
  {
    if (null == source)
      return null;
    int index = source.indexOf(strBegin);
    int indexEnd = source.indexOf(strEnd);
    if (index < 0)
      index = 0 - strBegin.length();
    if (indexEnd < 0)
      indexEnd = source.length();
    return source.substring(index + strBegin.length(), indexEnd);
  }

  public static String removeStringBetween(String source, String strBegin, String strEnd)
  {
    int index = source.indexOf(strBegin);
    int indexEnd = source.indexOf(strEnd);
    return source.substring(0, index) + source.substring(indexEnd + strEnd.length());
  }

  public static String replaceAllString(String source, String strReplaced, String strReplace)
  {
    if ((isEmpty(source)) || (isEmpty(strReplaced)) || (strReplace == null)) {
      return source;
    }
    StringBuffer buf = new StringBuffer(source.length());
    int start = 0; int end = 0;
    while ((end = source.indexOf(strReplaced, start)) != -1) {
      buf.append(source.substring(start, end)).append(strReplace);
      start = end + strReplaced.length();
    }
    buf.append(source.substring(start));
    return buf.toString();
  }

  public static String replaceIgnoreCase(String source, String strBeReplace, String strReplaced)
  {
    if ((isEmpty(source)) || (isEmpty(strBeReplace)) || (strReplaced == null)) {
      return source;
    }
    StringBuffer buf = new StringBuffer(source.length());
    int start = 0; int end = 0;
    String strReplacedCopy = strBeReplace.toUpperCase();
    String sourceCopy = source.toUpperCase();
    while ((end = sourceCopy.indexOf(strReplacedCopy, start)) != -1) {
      buf.append(source.substring(start, end)).append(strReplaced);
      start = end + strReplacedCopy.length();
    }
    buf.append(source.substring(start));
    return buf.toString();
  }

  public static String replaceFromTo(String source, String strBegin, String strEnd, String replaced)
  {
    if (null == source)
      return null;
    int index = source.indexOf(strBegin);
    int index1 = source.indexOf(strEnd);
    return source.substring(0, index) + replaced + source.substring(index1 + strEnd.length());
  }

  public static String[] gb2Unicode(String[] srcAry)
  {
    String[] strOut = new String[srcAry.length];
    for (int i = 0; i < srcAry.length; i++) {
      strOut[i] = gb2Unicode(srcAry[i]);
    }
    return strOut;
  }

  public static String gb2Unicode(String src)
  {
    src = spaceToNull(src);
    if (src == null) {
      return null;
    }
    char[] c = src.toCharArray();
    int n = c.length;
    byte[] b = new byte[n];
    for (int i = 0; i < n; i++)
      b[i] = ((byte)c[i]);
    return new String(b);
  }

  public static String[] unicode2Gb(String[] srcAry)
  {
    String[] strOut = new String[srcAry.length];
    for (int i = 0; i < srcAry.length; i++) {
      strOut[i] = uniCode2Gb(srcAry[i]);
    }
    return strOut;
  }

  public static String uniCode2Gb(String src)
  {
    src = spaceToNull(src);
    if (src == null) {
      return null;
    }
    byte[] b = src.getBytes();
    int n = b.length;
    char[] c = new char[n];
    for (int i = 0; i < n; i++)
      c[i] = ((char)((short)b[i] & 0xFF));
    return new String(c);
  }

  public static boolean match(String reg, String str)
  {
    return WildcardMatcher.match(reg, str);
  }

  public static boolean matchIgnoreCase(String reg, String str)
  {
    return WildcardMatcher.match(reg, str);
  }

  public static List<String> toList(String inputstring, String splitstr)
  {
    StringTokenizer st = new StringTokenizer(inputstring, splitstr, false);
    List reslist = new ArrayList(st.countTokens());
    while (st.hasMoreTokens()) {
      reslist.add(st.nextToken().trim());
    }
    return reslist;
  }

  public static String getUnionStr(String[] strAry, String unionChar, String appendChar)
  {
    StringBuffer ret = new StringBuffer();
    for (int i = 0; i < strAry.length; i++) {
      if (i != 0)
        ret.append(unionChar);
      ret.append(appendChar + strAry[i] + appendChar);
    }
    return ret.toString();
  }

  public static String getPYIndexStr(String strChinese, boolean bUpCase)
  {
    try
    {
      StringBuffer buffer = new StringBuffer();

      byte[] b = strChinese.getBytes("GBK");

      int i = 0;
      while (i < b.length) {
        if ((b[i] & 0xFF) > 128)
        {
          int char1 = b[(i++)] & 0xFF;
          char1 <<= 8;
          int chart = char1 + (b[i] & 0xFF);
          buffer.append(getPYIndexChar((char)chart, bUpCase));
        } else {
          char c = (char)b[i];
          if (!Character.isJavaIdentifierPart(c))
            c = 'A';
          buffer.append(c);
        }
        i++;
      }
      return buffer.toString();
    } catch (Exception e) {
     // Debug.error("ERRORs happen when get Chinese Pinyin!" + e.getMessage());
    }return null;
  }

  private static char getPYIndexChar(char strChinese, boolean bUpCase)
  {
    int charGBK = strChinese;
    char result;
   // char result;
    if ((charGBK >= 45217) && (charGBK <= 45252)) {
      result = 'A';
    }
    else
    {
   //   char result;
      if ((charGBK >= 45253) && (charGBK <= 45760)) {
        result = 'B';
      }
      else
      {
   //     char result;
        if ((charGBK >= 45761) && (charGBK <= 46317)) {
          result = 'C';
        }
        else
        {
     //     char result;
          if ((charGBK >= 46318) && (charGBK <= 46825)) {
            result = 'D';
          }
          else
          {
        //    char result;
            if ((charGBK >= 46826) && (charGBK <= 47009)) {
              result = 'E';
            }
            else
            {
        //      char result;
              if ((charGBK >= 47010) && (charGBK <= 47296)) {
                result = 'F';
              }
              else
              {
             //   char result;
                if ((charGBK >= 47297) && (charGBK <= 47613)) {
                  result = 'G';
                }
                else
                {
             //     char result;
                  if ((charGBK >= 47614) && (charGBK <= 48118)) {
                    result = 'H';
                  }
                  else
                  {
                //    char result;
                    if ((charGBK >= 48119) && (charGBK <= 49061)) {
                      result = 'J';
                    }
                    else
                    {
                //      char result;
                      if ((charGBK >= 49062) && (charGBK <= 49323)) {
                        result = 'K';
                      }
                      else
                      {
                    //    char result;
                        if ((charGBK >= 49324) && (charGBK <= 49895)) {
                          result = 'L';
                        }
                        else
                        {
                  //        char result;
                          if ((charGBK >= 49896) && (charGBK <= 50370)) {
                            result = 'M';
                          }
                          else
                          {
                         //   char result;
                            if ((charGBK >= 50371) && (charGBK <= 50613)) {
                              result = 'N';
                            }
                            else
                            {
                          //    char result;
                              if ((charGBK >= 50614) && (charGBK <= 50621)) {
                                result = 'O';
                              }
                              else
                              {
                            //    char result;
                                if ((charGBK >= 50622) && (charGBK <= 50905)) {
                                  result = 'P';
                                }
                                else
                                {
                              //    char result;
                                  if ((charGBK >= 50906) && (charGBK <= 51386)) {
                                    result = 'Q';
                                  }
                                  else
                                  {
                                 //   char result;
                                    if ((charGBK >= 51387) && (charGBK <= 51445)) {
                                      result = 'R';
                                    }
                                    else
                                    {
                                  //    char result;
                                      if ((charGBK >= 51446) && (charGBK <= 52217)) {
                                        result = 'S';
                                      }
                                      else
                                      {
                                     //   char result;
                                        if ((charGBK >= 52218) && (charGBK <= 52697)) {
                                          result = 'T';
                                        }
                                        else
                                        {
                                    //      char result;
                                          if ((charGBK >= 52698) && (charGBK <= 52979)) {
                                            result = 'W';
                                          }
                                          else
                                          {
                                          //  char result;
                                            if ((charGBK >= 52980) && (charGBK <= 53688)) {
                                              result = 'X';
                                            }
                                            else
                                            {
                                            //  char result;
                                              if ((charGBK >= 53689) && (charGBK <= 54480)) {
                                                result = 'Y';
                                              }
                                              else
                                              {
                                           //     char result;
                                                if ((charGBK >= 54481) && (charGBK <= 55289))
                                                  result = 'Z';
                                                else
                                                  result = (char)(65 + new Random().nextInt(25)); 
                                              }
                                            }
                                          }
                                        }
                                      }
                                    }
                                  }
                                }
                              }
                            }
                          }
                        }
                      }
                    }
                  }
                }
              }
            }
          }
        }
      }
    }
    if (!bUpCase)
      result = Character.toLowerCase(result);
    return result;
  }

  public static String[] toArray(String str, String delim)
  {
    return toArray(str, delim, false, false);
  }

  public static String[] toArray(String s)
  {
    return toArray(s, ",", false, false);
  }

  public static String[] split(String str, String token)
  {
    return toArray(str, token);
  }

  public static String replaceQuotMark(String strValue)
  {
    String oldMark = "'";
    String strResult = strValue;
    if ((strValue != null) && (strValue.length() > 0) && (strValue.indexOf(oldMark) >= 0))
    {
      boolean hasOneQuoMard = true;
      int pos = 0;
      while (hasOneQuoMard) {
        pos = strResult.indexOf(oldMark, pos);
        if (pos < 0)
          break;
        if (pos >= strResult.length() - 1) {
          strResult = strResult.substring(0, strResult.length()) + oldMark;

          hasOneQuoMard = false;
        }
        else if (strResult.substring(pos + 1, pos + 2).equals(oldMark)) {
          pos += 2;
        } else {
          strResult = strResult.substring(0, pos + 1) + oldMark + strResult.substring(pos + 1);

          pos += 2;
        }
      }
    }

    return strResult;
  }

  public static String spaceToNull(String str)
  {
    if ((str == null) || (str.trim().length() == 0))
      return null;
    return str.trim();
  }
  public static String getSpaceStr(int len)
  {
   if(len==0)return "";
   else{
	   char[] cs=new char[len];
	   Arrays.fill(cs,' ');
	   return new String(cs);
   }
  }
  public static String removeCharFromString(String value, char removeChar)
  {
    if (value == null)
      return null;
    String regular = value.trim();
    String removestr = String.valueOf(removeChar);
    int index = regular.indexOf(removestr);
    while (index > 0) {
      String temp = regular.substring(0, index);
      regular = temp + regular.substring(index + 1);
      index = regular.indexOf(removestr);
    }
    return regular;
  }

  public static String addCharToString(String value, char addChar)
  {
    if (value == null)
      return null;
    String regular = value;

    String sign = "";
    if (regular.substring(0, 1).equals("-")) {
      sign = "-";
      regular = regular.substring(1, regular.length());
    }
    int index = regular.indexOf(".");
    String fracTemp = "";
    if (index > 0) {
      fracTemp = "." + regular.substring(index + 1);
      regular = regular.substring(0, index);
    }

    String after = null;
    String strAdd = String.valueOf(addChar);
    while (regular.length() > 3) {
      String temp = regular.substring(regular.length() - 3, regular.length());

      regular = regular.substring(0, regular.length() - 3);
      if (after == null)
        after = temp;
      else
        after = temp + strAdd + after;
    }
    if (after == null)
      regular = sign + regular + fracTemp;
    else {
      regular = sign + regular + strAdd + after + fracTemp;
    }
    return regular;
  }

  public static int computeStringWidth(FontMetrics fontMetrics, String str)
  {
    if ((str == null) || (str.length() <= 0))
      return 0;
    int width = 10 + SwingUtilities.computeStringWidth(fontMetrics, str);

    int bytesLen = str.getBytes().length;
    if (bytesLen >= 10)
      width += (bytesLen - 10) * 2 + 5;
    return width;
  }

  public static String convExpoToRegular(String value)
  {
    if (value == null)
      return "0";
    String regular = value.toUpperCase();

    String sign = "";
    if (regular.substring(0, 1).equals("-")) {
      sign = "-";
      regular = regular.substring(1, regular.length());
    }
    int index1 = regular.indexOf("E");
    if (index1 > 0) {
      String temp = regular.substring(0, index1);
      String strExep = regular.substring(index1 + 1);
      int exep = Integer.parseInt(strExep);
      int index2 = temp.indexOf(".");
      if (exep >= 0)
      {
        if (index2 > 0)
        {
          String inteTemp = temp.substring(0, index2);
          String fracTemp = temp.substring(index2 + 1);
          if (fracTemp.length() > exep) {
            regular = inteTemp + fracTemp.substring(0, exep) + "." + fracTemp.substring(exep);
          }
          else {
            int diff = exep - fracTemp.length();
            for (int l = 0; l < diff; l++)
              fracTemp = fracTemp + "0";
            regular = inteTemp + fracTemp + ".0";
          }
        }
        else {
          for (int l = 0; l < exep; l++)
            temp = temp + "0";
          regular = temp;
        }
      }
      else {
        String inteTemp = temp;
        String fracTemp = "";
        exep = -exep;
        if (index2 > 0) {
          inteTemp = temp.substring(0, index2);
          fracTemp = temp.substring(index2 + 1);
        }
        if (inteTemp.length() > exep) {
          int diff = inteTemp.length() - exep;
          regular = inteTemp.substring(0, diff) + "." + inteTemp.substring(diff) + fracTemp;
        }
        else {
          int diff = exep - inteTemp.length();
          for (int l = 0; l < diff; l++)
            inteTemp = "0" + inteTemp;
          regular = "0." + inteTemp + fracTemp;
        }
      }
    }
    return sign + regular;
  }

  public static String formatFloat(String str, int precision)
  {
    if (str == null) {
      return "0";
    }
    if (str.indexOf("E") > -1) {
      str = convExpoToRegular(str);
    }
    String preStr = "";

    String numStr = "";
    if (precision < 0)
      precision = 0;
    try {
      int index = str.indexOf(".");
      if (index > -1)
      {
        if (index == 0) {
          preStr = "0";
        } else {
          preStr = str.substring(0, index);
          if (preStr.equals("-"))
            preStr = preStr + "0";
        }
        numStr = str.substring(index + 1);
      } else {
        preStr = str;
      }
      if (precision > 0) {
        preStr = preStr + ".";
        int len = numStr.length();
        if (len < precision)
        {
          for (int i = 0; i < precision - len; i++)
            numStr = numStr + "0";
          preStr = preStr + numStr;
        } else if (len > precision) {
          String s = numStr.substring(precision, precision + 1);
          String temp = numStr.substring(0, precision);
          if (Integer.parseInt(s) >= 5)
          {
            preStr = addOne(preStr, temp, "");
          }
          else preStr = preStr + temp; 
        }
        else
        {
          preStr = preStr + numStr;
        }
      }
    } catch (NumberFormatException e) { return null; }

    return preStr;
  }

  public static boolean stringToBoolean(String str)
  {
    if (str == null)
      return false;
    if (str.equalsIgnoreCase("Y")) {
      return true;
    }
    return false;
  }

  public static String getObjectCode(String obj)
  {
    String code = null;
    if (obj != null) {
      int index = obj.indexOf(" ");
      if (index > -1) {
        code = obj.substring(0, index);
      }
    }
    return code;
  }

  public static String getObjectName(String obj)
  {
    String name = null;
    if (obj != null) {
      int index = obj.indexOf(" ");
      if (index > -1) {
        name = obj.substring(index + 1);
      }
    }
    return name;
  }

  public static String[] getReservedWords()
  {
    return new String[] { " ", "`", "#", "&", "*", "\"", "'", "?", "+", "-", "!" };
  }

  private static String addOne(String preStr, String numStr, String afterStr)
  {
    String result = "";
    if (numStr.length() > 0) {
      String s = numStr.substring(numStr.length() - 1);
      numStr = numStr.substring(0, numStr.length() - 1);
      int value = Integer.parseInt(s);
      if (value == 9) {
        afterStr = "0" + afterStr;
        result = addOne(preStr, numStr, afterStr);
      } else {
        result = preStr + numStr + Integer.toString(value + 1) + afterStr;
      }
    }
    else if (preStr.length() > 0) {
      String s = preStr.substring(preStr.length() - 1);
      preStr = preStr.substring(0, preStr.length() - 1);
      if (s.equals(".")) {
        afterStr = s + afterStr;
        result = addOne(preStr, numStr, afterStr);
      } else {
        int value = Integer.parseInt(s);
        if (value == 9) {
          afterStr = "0" + afterStr;
          result = addOne(preStr, numStr, afterStr);
        } else {
          result = preStr + numStr + Integer.toString(value + 1) + afterStr;
        }
      }
    }
    else {
      result = preStr + "1" + numStr + afterStr;
    }
    return result;
  }

  public static int compareByByte(Object o1, Object o2)
  {
    if (null == o1) {
      return null == o2 ? 0 : -1;
    }
    if (null == o2) {
      return 1;
    }

    boolean isBytes = o1 instanceof byte[];
    byte[] bAry1 = isBytes ? (byte[])o1 : o1.toString().getBytes();
    byte[] bAry2 = isBytes ? (byte[])o2 : o2.toString().getBytes();

    int len1 = bAry1.length;
    int len2 = bAry2.length;
    int n = Math.min(len1, len2);
    int i = 0;
    int j = 0;
    int r = 0;
    while (n-- != 0) {
      if ((r = bAry1[(i++)] - bAry2[(j++)]) != 0)
        break;
    }
    if (r == 0)
      r = len1 - len2;
    if (r == 0)
      return 0;
    if (r > 0)
      return 1;
    return -1;
  }

  public static boolean isEmpty(String str)
  {
    return (str == null) || (str.length() == 0) || "null".equalsIgnoreCase(str);
  }

  public static boolean isEmptyWithTrim(String str)
  {
    return (str == null) || (str.trim().length() == 0) || "null".equalsIgnoreCase(str);
  }
  
  /**
   * 判断字符串中间是否存在空格
   * @author gejw
   * @time 下午4:59:06
   * @param str
   * @return
   */
  public static boolean isExistTrim(String str)
  {
    if(str.indexOf(" ")!=-1){
        return true;
    }
    return false;
  }

  /**
   * 去除收尾空格和一些特殊字符
   * @param str
   * @return
   */
  public static String replaceBlank(String str) {
    String dest = "";
    if (!StringUtil.isEmpty(str)) {
      str = str.trim();
      Pattern p = Pattern.compile("\\s*|\t|\r|\n");
      Matcher m = p.matcher(str);
      dest = m.replaceAll("");
    }
    return dest;
  }

  public static int getHanziCount(String str) {
    if (StringUtil.isEmpty(str)) {
      return 0;
    }
    String regex = "[\u4e00-\u9fa5]";
    return str.length() - str.replaceAll(regex, "").length();
  }

  public static boolean isContainChinese(String str)
  {
    if (isEmpty(str))
      return false;
    for (int i = 0; i < str.length(); i++) {
      if (str.substring(i, i + 1).matches("[\\u4e00-\\u9fa5]+"))
        return true;
    }
    return false;
  }

  public static int lenOfChinesString(String str) {
    int len = 0;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if ((c >= 'һ') && (c <= 40869))
        len += 2;
      else {
        len++;
      }
    }
    return len;
  }

  public static String subChineseString(String str, int from, int len) {
    if ((str == null) || (from < 0) || (len <= 0)) {
      throw new IllegalArgumentException();
    }
    int splitedLen = 0;
    StringBuffer sb = new StringBuffer();
    for (int i = from; (i < str.length()) && (i < from + len); i++) {
      char c = str.charAt(i);
      if ((c >= 'һ') && (c <= 40869))
        splitedLen += 2;
      else {
        splitedLen++;
      }
      if (splitedLen > len) {
        break;
      }
      sb.append(c);
    }

    return sb.toString();
  }

  public static String recoverWrapLineChar(String msg)
  {
    if (msg == null)
      return "";
    StringBuffer dest = new StringBuffer();
    for (int i = 0; i < msg.length(); i++) {
      char aChar = msg.charAt(i);
      if ((aChar == '\\') && (i < msg.length() - 1)) {
        char aCharNext = msg.charAt(++i);
        if (aCharNext == 't')
          aCharNext = '\t';
        else if (aCharNext == 'r')
          aCharNext = '\r';
        else if (aCharNext == 'n')
          aCharNext = '\n';
        else if (aCharNext == 'f')
          aCharNext = '\f';
        else
          dest.append(aChar);
        dest.append(aCharNext);
      } else {
        dest.append(aChar);
      }
    }
    return dest.toString();
  }

  public static char[] toHexChar(byte[] bArray)
  {
    char[] digitChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

    char[] charDigest = new char[bArray.length * 2];

    for (int i = 0; i < bArray.length; i++) {
      charDigest[(i * 2)] = digitChars[(bArray[i] >>> 4 & 0xF)];
      charDigest[(i * 2 + 1)] = digitChars[(bArray[i] & 0xF)];
    }
    return charDigest;
  }

  public static int compare(String str1, String str2) {
    String t1 = "";
    String t2 = "";
    try {
      if (str1 != null)
        t1 = new String(str1.getBytes(), "ISO-8859-1");
      if (str2 != null)
        t2 = new String(str2.getBytes(), "ISO-8859-1");
    } catch (Exception e) {
   //   Logger.error("error", e);
    }
    return t1.compareTo(t2);
  }

  public static String[] toArray(String s, String delimiters, boolean trimTokens, boolean ignoreEmptyTokens)
  {
    if (s == null) {
      return new String[0];
    }
    StringTokenizer st = new StringTokenizer(s, delimiters);
    List tokens = new ArrayList();
    while (st.hasMoreTokens()) {
      String token = st.nextToken();
      if (trimTokens) {
        token = token.trim();
      }
      if ((!ignoreEmptyTokens) || (token.length() != 0)) {
        tokens.add(token);
      }
    }
    return (String[])tokens.toArray(new String[tokens.size()]);
  }

  public static boolean nstartsWith(String str1, String str2)
  {
    if (str2.length() > str1.length())
      return true;
    for (int i = 0; i < str2.length(); i++) {
      if (str2.charAt(i) != str1.charAt(i))
        return true;
    }
    return false;
  }

  public static boolean nendsWith(String str1, String str2)
  {
    if (str2.length() > str1.length())
      return true;
    int indexSrc = str1.length() - 1;
    for (int i = str2.length() - 1; i > 0; i--) {
      if (str2.charAt(i) != str1.charAt(indexSrc--))
        return true;
    }
    return false;
  }

  public static boolean nequals(String str1, String str2) {
    if (str2.length() != str1.length())
      return true;
    for (int i = 0; i < str2.length(); i++) {
      if (str2.charAt(i) != str1.charAt(i))
        return true;
    }
    return false;
  }

  public static boolean hasText(String str)
  {
    int strLen;
    if ((str == null) || ((strLen = str.length()) == 0))
      return false;
 //   int strLen;
    for (int i = 0; i < strLen; i++) {
      if (!Character.isWhitespace(str.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  public static String digest(String strSource) {
    StringBuffer digest = new StringBuffer();
    try {
      MessageDigest md = MessageDigest.getInstance("SHA");

      byte[] sourBytes = strSource.getBytes();
      byte[] digestBytes = md.digest(sourBytes);
      if (digestBytes != null) {
        int i = 0; for (int n = digestBytes.length; i < n; i++)
          digest.append(digestBytes[i]);
      }
    }
    catch (NoSuchAlgorithmException e) {
     // Debug.error(e.getMessage(), e);
    }
    return digest.toString();
  }

  public static String digest(URL[] urls) {
    StringBuffer buffer = new StringBuffer();
    for (int i = 0; i < urls.length; i++) {
      long lastModified = -1L;
      if (i > 1)
        buffer.append(";");
      buffer.append(urls[i]);
      if ("file".equals(urls[i].getProtocol())) {
        File file = new File(urls[i].getPath().replace('/', File.separatorChar).replace('|', ':'));

        lastModified = file.lastModified();
      }
      else {
        try {
          URLConnection connection = urls[i].openConnection();
          lastModified = connection.getLastModified();
        }
        catch (IOException e)
        {
        }
      }
      buffer.append('!').append(lastModified);
    }

    return digest(buffer.toString());
  }

  public static String toString(Object[] arr) {
    return toString(arr, ",");
  }
  public static String toString(Object[] arr,int slen,int elen) {
	  	Object[] os=new Object[elen-slen];
	  	System.arraycopy(arr, slen, os, 0, elen-slen);
	  	Arrays.fill(os,"?");
	    return toString(os, ",");
	  }
  public static String toString(Object value, String delim) {
    if ((value instanceof String)) {
      return (String)value;
    }
    if (value.getClass().isArray())
      return toString((Object[])value, delim);
    if ((value instanceof Iterable)) {
      return toString((Iterable)value, delim);
    }
    return value.toString();
  }

  public static String toString(Iterable c, String delim, String prefix, String suffix)
  {
    if (c == null) {
      return "null";
    }
    StringBuffer sb = new StringBuffer();
    Iterator it = c.iterator();
    int i = 0;
    while (it.hasNext()) {
      if (i++ > 0) {
        sb.append(delim);
      }
      sb.append(prefix + it.next() + suffix);
    }
    return sb.toString();
  }

  public static String toString(Iterable c, String delim) {
    return toString(c, delim, "", "");
  }

  public static String toString(Object value) {
    return toString(value, ",");
  }

  public static String toString(Object[] arr, String delim) {
    if (arr == null) {
      return "null";
    }
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < arr.length; i++) {
      if (i > 0)
        sb.append(delim);
      sb.append(arr[i]);
    }
    return sb.toString();
  }

  public static String unqualify(String qualifiedName, char separator)
  {
    return qualifiedName.substring(qualifiedName.lastIndexOf(separator) + 1);
  }

  public static String capitalize(String str)
  {
    return changeFirstCharacterCase(true, str);
  }

  public static String uncapitalize(String str) {
    return changeFirstCharacterCase(false, str);
  }

  private static String changeFirstCharacterCase(boolean capitalize, String str)
  {
    int strLen;
    if ((str == null) || ((strLen = str.length()) == 0))
      return str;
 //   int strLen;
    StringBuffer buf = new StringBuffer(strLen);
    if (capitalize)
      buf.append(Character.toUpperCase(str.charAt(0)));
    else {
      buf.append(Character.toLowerCase(str.charAt(0)));
    }
    buf.append(str.substring(1));
    return buf.toString();
  }

  public static String unqualify(String qualifiedName) {
    return unqualify(qualifiedName, '.');
  }

  public static String getFilename(String path) {
    int separatorIndex = path.lastIndexOf("/");
    return separatorIndex != -1 ? path.substring(separatorIndex + 1) : path;
  }

  public static String normalizePath(String path)
  {
    String p = path.replaceAll("\\\\", "/");
    String[] pArray = toArray(p, "/");
    List pList = new LinkedList();
    int tops = 0;
    for (int i = pArray.length - 1; i >= 0; i--) {
      if (!".".equals(pArray[i]))
      {
        if ("..".equals(pArray[i])) {
          tops++;
        }
        else if (tops > 0)
          tops--;
        else {
          pList.add(0, pArray[i]);
        }
      }
    }
    return toString(pList, "/");
  }

  public static boolean pathEquals(String path1, String path2) {
    return normalizePath(path1).equals(normalizePath(path2));
  }

  public static URL[] pathToURLs(String path) {
    StringTokenizer st = new StringTokenizer(path, File.pathSeparator);
    URL[] urls = new URL[st.countTokens()];
    int count = 0;
    while (st.hasMoreTokens()) {
      File file = new File(st.nextToken());
      URL url = null;
      try {
        url = file.toURI().toURL();
      } catch (MalformedURLException e) {
        //Logger.error(e.getMessage(), e);
      }
      if (url != null) {
        urls[(count++)] = url;
      }
    }
    if (urls.length != count) {
      URL[] tmp = new URL[count];
      System.arraycopy(urls, 0, tmp, 0, count);
      urls = tmp;
    }
    return urls;
  }

  public static String removeLastFileSeperator(String path) {
    if (path == null)
      return null;
    while ((path.endsWith("\\")) || (path.endsWith("/"))) {
      path = path.substring(0, path.length() - 1);
    }
    return spaceToNull(path.trim());
  }

  public static String getTimeStampString(long l) {
    Calendar cl = Calendar.getInstance();
    cl.setTimeInMillis(l);
    int[] ia = new int[5];
    int year = cl.get(1);
    ia[0] = (cl.get(2) + 1);
    ia[1] = cl.get(5);
    ia[2] = cl.get(11);
    ia[3] = cl.get(12);

    ia[4] = cl.get(13);
    byte[] ba = new byte[19];
    ba[7] = 45; ba[4] = 45;
    ba[10] = 32;
    ba[16] = 58; ba[13] = 58;
    ba[0] = ((byte)(year / 1000 + 48));
    ba[1] = ((byte)(year / 100 % 10 + 48));
    ba[2] = ((byte)(year / 10 % 10 + 48));
    ba[3] = ((byte)(year % 10 + 48));
    for (int i = 0; i < 5; i++) {
      ba[(i * 3 + 5)] = ((byte)(ia[i] / 10 + 48));
      ba[(i * 3 + 6)] = ((byte)(ia[i] % 10 + 48));
    }
    return new String(ba);
  }

  public static String replace(String s, Properties p) {
    String regex = "\\$\\w+\\W|\\$\\{[^}]+\\}";
    Pattern pattern = Pattern.compile(regex);
    Matcher m = pattern.matcher(s);

    while (m.find()) {
      String temp = m.group();
      String key = null;
      if (temp.indexOf("{") != -1) {
        key = temp.substring(temp.indexOf("{") + 1, temp.length() - 1);
      }
      else {
        key = temp.substring(1, temp.length() - 1);
      }
      String value = p.getProperty(key);

      if (value != null) {
        s = s.replace(temp, value);
        m = pattern.matcher(s);
      }
    }

    return s;
  }
}
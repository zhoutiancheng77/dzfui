package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

/**
 * 常用的工具类
 */
public class DZfcommonTools {

    /**
     * 构建 Key 值
     */
    public static String getCombinesKey(Object vo, String[] groupFields) {
        if (vo == null)
            throw new BusinessException("分组数据为空！");
        if (groupFields == null || groupFields.length == 0)
            throw new BusinessException("分组字段为空！");
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < groupFields.length; i++) {
            if (i == 0) {
                result.append(BeanHelper.getProperty(vo, groupFields[i]));
            } else {
                result.append("," + BeanHelper.getProperty(vo, groupFields[i]));
            }
        }
        return result.toString();
    }

    /**
     * 返回in('xxx','xxx'...'xxx')形式
     */
//	public static String getInWhereClause(String[] args){
//		if(args == null || args.length == 0)
//			return null;
//		StringBuffer sf = new StringBuffer();
//		sf.append(" in (");
//		for(int i = 0 ; i < args.length ; i++){
//			if(i == 0 ){
//				sf.append(" '"+args[i]+"'");
//			}else{
//				sf.append(",'"+args[i]+"'");
//			}
//		}
//		sf.append(")");
//		return sf.toString();
//	}


    /**
     * 返回in('xxx','xxx'...'xxx')形式
     */
//	public static <T extends SuperVO> String getInWhereClauseVO(List<T> args){
//		if(args == null || args.size() == 0)
//			return null;
//		StringBuffer sf = new StringBuffer();
//		sf.append(" in (");
//		for(int i = 0 ; i < args.size() ; i++){
//			if(i == 0 ){
//				sf.append(" '"+args.get(i).getPrimaryKey()+"'");
//			}else{
//				sf.append(",'"+args.get(i).getPrimaryKey()+"'");
//			}
//		}
//		sf.append(")");
//		return sf.toString();
//	}

    /**
     * 处理重复key
     */
    public static String[] getNotrepeateKey(String[] array){
        if(array == null || array.length == 0)
            return null;
        Map<String,String> map = new HashMap<String, String>();
        //	String val = null;
        for(String v : array){
            if(isNotNull(v))
                map.put(v, v);
        }
        List<String> list = new ArrayList<String>(map.values());
        return list.toArray(list.toArray(new String[0]));
    }

    public static boolean isNotNull(Object value){
        boolean flag = false;
        if(value != null && !"".equals(value))
            flag = true;
        return flag;
    }


    /**
     * 数组转换
     */
    public static SuperVO[] convertToSuperVO(Object[] arrays){
        if(arrays == null || arrays.length == 0)
            return null;
        int arrayLength = Array.getLength(arrays);
        Object c = Array.newInstance(arrays[0].getClass(), arrays.length);
        System.arraycopy(arrays, 0, c, 0, arrayLength);
        return (SuperVO[]) c;
    }

    /**
     * 分组
     */
    public static <T extends SuperVO> Map<String, List<T>> hashlizeObject(List<T> objs, String[] keys)
            throws BusinessException {
        Map<String, List<T>> result = new HashMap<String, List<T>>();
        if (objs == null || objs.isEmpty())
            return result;
        if (keys == null || keys.length == 0)
            throw new BusinessException("分组字段不能为空。");
        String key = null;
        List<T> list =null;
        for (int i = 0; i < objs.size(); i++) {
            key = getCombinesKey(objs.get(i), keys);
            if(result.containsKey(key)){
                result.get(key).add(objs.get(i));
            }else{
                list = new ArrayList<T>();
                list.add(objs.get(i));
                result.put(key, list);
            }
        }
        return result;
    }

    public static <T extends SuperVO> Map<String, T> hashlizeObjectByPk(List<T> objs, String[] keys)
            throws BusinessException {
        Map<String, T> result = new HashMap<String, T>();
        if (objs == null || objs.isEmpty())
            return result;
        if (keys == null || keys.length == 0)
            throw new BusinessException("分组字段不能为空。");
        String key = null;
        for (int i = 0; i < objs.size(); i++) {
            key = getCombinesKey(objs.get(i), keys);
            result.put(key, objs.get(i));
        }
        return result;
    }

    public static String getFirstCode(String childCode,String codeRule) {
        String firstCode = "";
        int[] codeSection = parseCodeRule(codeRule);
        if(codeSection.length > 0 && childCode.length() >= codeSection[0]){
            firstCode = childCode.substring(0, codeSection[0]);
        }
        return firstCode;
    }


    public static String getParentCode(String childCode,String codeRule) {
        String parentCode = null;
        int sublength = 0;
        int[] codeSection = parseCodeRule(codeRule);
        int length = childCode.length();
        int codeLen = codeSection == null ? 0 : codeSection.length;
        if (codeLen > 0) {
            int index = 0;
            while ((length > 0) && (index < codeLen)) {
                length -= codeSection[index];
                if (length > 0) {
                    sublength += codeSection[index];
                }
                index++;
            }
        }

        if (sublength > 0)
            parentCode = childCode.substring(0, sublength);
        return parentCode;
    }

    public static int[] parseCodeRule(String codeRule) {
        if (codeRule == null) {
            return null;
        }
        StringTokenizer st = new StringTokenizer(codeRule, " ,./\\", false);
        int count = st.countTokens();
        int[] codes = new int[count];
        int index = 0;
        try {
            while (st.hasMoreTokens())
                codes[(index++)] = Integer.parseInt(st.nextToken().trim());
        } catch (Exception e) {
            codes = null;
        }
        return codes;
    }

    //保留两位有效数字
    public static String formatDouble(DZFDouble dv){
        if(dv == null || dv.doubleValue() == 0)
            return "";
        DecimalFormat df = new DecimalFormat("###,###,###,###,###,###,##0.00");
        String result = df.format(Double.valueOf(dv.toString()));
        return result;
    }

}

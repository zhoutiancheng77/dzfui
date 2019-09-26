package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.model.CircularlyAccessibleValueObject;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.*;
import com.dzf.zxkj.common.tool.MapList;
import com.dzf.zxkj.common.utils.DZFArrayUtil;
import com.dzf.zxkj.common.utils.DZFCollectionUtil;
import com.dzf.zxkj.common.utils.DZFMapUtil;
import com.sun.beans.decoder.ValueObject;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 值校验工具类
 * 
 */
public class DZFValueCheck {

    /**
     * 判断Object是否为空
     * 
     * @param obj
     * @return
     */
    public static final boolean isEmpty(Object obj) {
        if (null == obj) {
            return true;
        }
        if (obj instanceof String) {
            return DZFStringUtil.isEmpty((String) obj);
        }
        if (obj.getClass().isArray()) {
            return DZFArrayUtil.isEmpty((Object[]) obj);
        }
        if (obj instanceof Collection) {
            return DZFCollectionUtil.isEmpty((Collection<?>) obj);
        }
        if (obj instanceof Map) {
            return DZFMapUtil.isEmpty((Map<?, ?>) obj);
        }
        if (obj instanceof MapList) {
            return DZFMapUtil.isEmpty((MapList<?, ?>) obj);
        }
        if (obj instanceof StringBuilder) {
            return obj.toString().trim().length() <= 0;
        }
        if (obj instanceof StringBuffer) {
            return obj.toString().trim().length() <= 0;
        }
        return false;
    }

    /**
     * 判断Object[]是否为空
     * 
     * @param objs
     * @return
     */
    public static final boolean isEmpty(Object[] objs) {
        return null == objs || objs.length <= 0;
    }

    /**
     * 判断SuperVO是否为空
     * 
     * @param vo
     * @return
     */
    public static final boolean isEmpty(SuperVO vo) {
        return null == vo;
    }

    /**
     * 判断CircularlyAccessibleValueObject是否为空
     * 
     * @param circularlyAccessibleValueObject
     * @return
     */
    public static final boolean isEmpty(
            CircularlyAccessibleValueObject circularlyAccessibleValueObject) {
        return null == circularlyAccessibleValueObject;
    }

    /**
     * 判断ValueObject是否为空
     * 
     * @param circularlyAccessibleValueObject
     * @return
     */
    public static final boolean isEmpty(ValueObject valueObject) {
        return null == valueObject;
    }


    /**
     * 判断DZFDouble是否为空
     * 
     * @param ufDouble
     * @return
     */
    public static final boolean isEmpty(DZFDouble ufDouble) {
        return null == ufDouble;
    }

    /**
     * 判断DZFDate是否为空
     * 
     * @param ufDate
     * @return
     */
    public static final boolean isEmpty(DZFDate ufDate) {
        return null == ufDate;
    }

    /**
     * 判断DZFDateTime是否为空
     * 
     * @param ufDateTime
     * @return
     */
    public static final boolean isEmpty(DZFDateTime ufDateTime) {
        return null == ufDateTime;
    }

    /**
     * 判断DZFBoolean是否为空
     * 
     * @param ufBoolean
     * @return
     */
    public static final boolean isEmpty(DZFBoolean ufBoolean) {
        return null == ufBoolean;
    }

    /**
     * 判断DZFTime是否为空
     * 
     * @param ufTime
     * @return
     */
    public static final boolean isEmpty(DZFTime ufTime) {
        return null == ufTime;
    }

    /**
     * 判断Character是否为空
     * 
     * @param packCharacter
     * @return
     */
    public static final boolean isEmpty(Character packCharacter) {
        return null == packCharacter;
    }

    /**
     * 判断Byte是否为空
     * 
     * @param packByte
     * @return
     */
    public static final boolean isEmpty(Byte packByte) {
        return null == packByte;
    }

    /**
     * 判断Boolean是否为空
     * 
     * @param packBoolean
     * @return
     */
    public static final boolean isEmpty(Boolean packBoolean) {
        return null == packBoolean;
    }

    /**
     * 判断Short是否为空
     * 
     * @param packShort
     * @return
     */
    public static final boolean isEmpty(Short packShort) {
        return null == packShort;
    }

    /**
     * 判断Integer是否为空
     * 
     * @param packInteger
     * @return
     */
    public static final boolean isEmpty(Integer packInteger) {
        return null == packInteger;
    }

    /**
     * 判断Long是否为空
     * 
     * @param packLong
     * @return
     */
    public static final boolean isEmpty(Long packLong) {
        return null == packLong;
    }

    /**
     * 判断Float是否为空
     * 
     * @param packFloat
     * @return
     */
    public static final boolean isEmpty(Float packFloat) {
        return null == packFloat;
    }

    /**
     * 判断Double是否为空
     * 
     * @param packDouble
     * @return
     */
    public static final boolean isEmpty(Double packDouble) {
        return null == packDouble;
    }

    /**
     * 判断Collection是否为空
     * 
     * @param collection
     * @return
     */
    public static final boolean isEmpty(Collection<?> collection) {
        return null == collection || collection.size() <= 0;
    }

    /**
     * 判断Map是否为空
     * 
     * @param map
     * @return
     */
    public static final boolean isEmpty(Map<?, ?> map) {
        return null == map || map.size() <= 0;
    }

    /**
     * 判断MapList是否为空
     * 
     * @param mapList
     * @return
     */
    public static final boolean isEmpty(MapList<?, ?> mapList) {
        return null == mapList || mapList.size() <= 0;
    }

    /**
     * 判断Set是否为空
     * 
     * @param set
     * @return
     */
    public static final boolean isEmpty(Set<?> set) {
        return null == set || set.size() <= 0;
    }

    /**
     * 判断String是否为空
     * 
     * @param str
     * @return
     */
    public static final boolean isEmpty(String str) {
        return null == str || str.trim().length() <= 0;
    }

    /**
     * 判断StringBuilder是否为空
     * 
     * @param strBuilder
     * @return
     */
    public static final boolean isEmpty(StringBuilder strBuilder) {
        return null == strBuilder || strBuilder.toString().trim().length() <= 0;
    }

    /**
     * 判断StringBuffer是否为空
     * 
     * @param strBuffer
     * @return
     */
    public static final boolean isEmpty(StringBuffer strBuffer) {
        return null == strBuffer || strBuffer.toString().trim().length() == 0;
    }

    /**
     * 判断TableCellEditor是否为空
     * 
     * @param tableCellEditor
     * @return
     */
    public static final boolean isEmpty(TableCellEditor tableCellEditor) {
        return null == tableCellEditor;
    }

    /**
     * 判断JPanel是否为空
     * 
     * @param billForm
     * @return
     */
    public static final boolean isEmpty(JPanel jPanel) {
        return null == jPanel;
    }

    /**
     * 判断AbstractTableModel是否为空
     * 
     * @param abstractTableModel
     * @return
     */
    public static final boolean isEmpty(AbstractTableModel abstractTableModel) {
        return null == abstractTableModel;
    }

    /**
     * 判断JScrollPane是否为空
     * 
     * @param jScrollPane
     * @return
     */
    public static final boolean isEmpty(JScrollPane jScrollPane) {
        return null == jScrollPane;
    }

    /**
     * 判断Object是否为非空
     * 
     * @param obj
     * @return
     */
    public static final boolean isNotEmpty(Object obj) {
        if (null == obj) {
            return false;
        }
        if (obj instanceof String) {
            return DZFStringUtil.isNotEmpty((String) obj);
        }
        if (obj.getClass().isArray()) {
            return DZFArrayUtil.isNotEmpty((Object[]) obj);
        }
        if (obj instanceof Collection) {
            return DZFCollectionUtil.isNotEmpty((Collection<?>) obj);
        }
        if (obj instanceof Map) {
            return DZFMapUtil.isNotEmpty((Map<?, ?>) obj);
        }
        if (obj instanceof MapList) {
            return DZFMapUtil.isNotEmpty((MapList<?, ?>) obj);
        }
        if (obj instanceof StringBuilder) {
            return obj.toString().trim().length() > 0;
        }
        if (obj instanceof StringBuffer) {
            return obj.toString().trim().length() > 0;
        }
        return true;
    }

    /**
     * 判断Object[]是否为非空
     * 
     * @param objs
     * @return
     */
    public static final boolean isNotEmpty(Object[] objs) {
        return null != objs && objs.length > 0;
    }

    /**
     * 判断SuperVO是否为非空
     * 
     * @param vo
     * @return
     */
    public static final boolean isNotEmpty(SuperVO vo) {
        return null != vo;
    }

    /**
     * 判断CircularlyAccessibleValueObject是否为非空
     * 
     * @param circularlyAccessibleValueObject
     * @return
     */
    public static final boolean isNotEmpty(
            CircularlyAccessibleValueObject circularlyAccessibleValueObject) {
        return null != circularlyAccessibleValueObject;
    }


    /**
     * 判断DZFDouble是否为非空
     * 
     * @param ufDouble
     * @return
     */
    public static final boolean isNotEmpty(DZFDouble ufDouble) {
        return null != ufDouble;
    }

    /**
     * 判断DZFDate是否为非空
     * 
     * @param ufDate
     * @return
     */
    public static final boolean isNotEmpty(DZFDate ufDate) {
        return null != ufDate;
    }

    /**
     * 判断DZFDateTime是否为非空
     * 
     * @param ufDateTime
     * @return
     */
    public static final boolean isNotEmpty(DZFDateTime ufDateTime) {
        return null != ufDateTime;
    }

    /**
     * 判断DZFBoolean是否为非空
     * 
     * @param ufBoolean
     * @return
     */
    public static final boolean isNotEmpty(DZFBoolean ufBoolean) {
        return null != ufBoolean;
    }

    /**
     * 判断DZFTime是否为非空
     * 
     * @param ufTime
     * @return
     */
    public static final boolean isNotEmpty(DZFTime ufTime) {
        return null != ufTime;
    }

    /**
     * 判断Character是否为非空
     * 
     * @param packCharacter
     * @return
     */
    public static final boolean isNotEmpty(Character packCharacter) {
        return null != packCharacter;
    }

    /**
     * 判断Byte是否为非空
     * 
     * @param packByte
     * @return
     */
    public static final boolean isNotEmpty(Byte packByte) {
        return null != packByte;
    }

    /**
     * 判断Boolean是否为非空
     * 
     * @param packBoolean
     * @return
     */
    public static final boolean isNotEmpty(Boolean packBoolean) {
        return null != packBoolean;
    }

    /**
     * 判断Short是否为非空
     * 
     * @param packShort
     * @return
     */
    public static final boolean isNotEmpty(Short packShort) {
        return null != packShort;
    }

    /**
     * 判断Integer是否为非空
     * 
     * @param packInteger
     * @return
     */
    public static final boolean isNotEmpty(Integer packInteger) {
        return null != packInteger;
    }

    /**
     * 判断Long是否为非空
     * 
     * @param packLong
     * @return
     */
    public static final boolean isNotEmpty(Long packLong) {
        return null != packLong;
    }

    /**
     * 判断Float是否为非空
     * 
     * @param packFloat
     * @return
     */
    public static final boolean isNotEmpty(Float packFloat) {
        return null != packFloat;
    }

    /**
     * 判断Double是否为非空
     * 
     * @param packDouble
     * @return
     */
    public static final boolean isNotEmpty(Double packDouble) {
        return null != packDouble;
    }

    /**
     * 判断Collection是否为非空
     * 
     * @param collection
     * @return
     */
    public static final boolean isNotEmpty(Collection<?> collection) {
        return null != collection && collection.size() > 0;
    }

    /**
     * 判断Map是否为非空
     * 
     * @param map
     * @return
     */
    public static final boolean isNotEmpty(Map<?, ?> map) {
        return null != map && map.size() > 0;
    }

    /**
     * 判断MapList是否为非空
     * 
     * @param mapList
     * @return
     */
    public static final boolean isNotEmpty(MapList<?, ?> mapList) {
        return null != mapList && mapList.size() > 0;
    }

    /**
     * 判断Set是否为非空
     * 
     * @param set
     * @return
     */
    public static final boolean isNotEmpty(Set<?> set) {
        return null != set && set.size() > 0;
    }

    /**
     * 判断String是否为非空
     * 
     * @param str
     * @return
     */
    public static final boolean isNotEmpty(String str) {
        return null != str && str.trim().length() > 0;
    }

    /**
     * 判断StringBuilder是否为非空
     * 
     * @param strBuilder
     * @return
     */
    public static final boolean isNotEmpty(StringBuilder strBuilder) {
        return null != strBuilder && strBuilder.toString().trim().length() > 0;
    }

    /**
     * 判断StringBuffer是否为非空
     * 
     * @param strBuffer
     * @return
     */
    public static final boolean isNotEmpty(StringBuffer strBuffer) {
        return null != strBuffer && strBuffer.toString().trim().length() > 0;
    }

    /**
     * 判断TableCellEditor是否为非空
     * 
     * @param tableCellEditor
     * @return
     */
    public static final boolean isNotEmpty(TableCellEditor tableCellEditor) {
        return null != tableCellEditor;
    }

    /**
     * 判断JPanel是否为非空
     * 
     * @param jPanel
     * @return
     */
    public static final boolean isNotEmpty(JPanel jPanel) {
        return null != jPanel;
    }

    /**
     * 判断AbstractTableModel是否为非空
     * 
     * @param abstractTableModel
     * @return
     */
    public static final boolean isNotEmpty(AbstractTableModel abstractTableModel) {
        return null != abstractTableModel;
    }

    /**
     * 判断是否为真
     * 
     * @param b
     * @return
     */
    public static boolean isTrue(Boolean b) {
        return null == b ? false : b.booleanValue();
    }

    /**
     * 判断是否为真
     * 
     * @param b
     * @return
     */
    public static boolean isTrue(DZFBoolean b) {
        return null == b ? false : b.booleanValue();
    }

    /**
     * 判断是否为数字或者字母
     * 
     * @param s
     * @return
     */
    public static boolean isNumberOrLetter(String s) {
        java.util.regex.Pattern patten = java.util.regex.Pattern.compile("[0-9A-Za-z]*");
        return patten.matcher(s).matches();
    }

    /**
     * 判断是否为数字
     * 
     * @param s
     * @return
     */
    public static boolean isNumber(String s) {
        java.util.regex.Pattern patten = java.util.regex.Pattern.compile("[0-9]*");
        return patten.matcher(s).matches();
    }
}

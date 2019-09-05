package com.dzf.zxkj.base.framework.processor;

import com.dzf.zxkj.base.framework.util.InOutUtil;
import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.utils.BeanConvertor;
import com.dzf.zxkj.base.utils.BeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.Converter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Method;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.*;


/**
 * Created by IntelliJ IDEA. User: 贺扬 Date: 2005-1-14 Time: 15:10:03
 */
@Slf4j
public class ProcessorUtils {

    //private static Logger log = Logger.getLogger(ProcessorUtils.class);

    private static class MethodAndTypes {

        public String[] propNames = null;

        public Method[] invokes = null;

        public int[] types = null;

        public Converter[] converters = null;

        public int[] colIdx = null;

    }

    /**
     * 结果集转换成数组
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    static public Object[] toArray(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int cols = meta.getColumnCount();
        Object[] result = new Object[cols];

        for (int i = 0; i < cols; i++) {
            result[i] = rs.getObject(i + 1);
        }

        return result;
    }

    /**
     * 结果集转换成HashMap
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    static public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int cols = metaData.getColumnCount();
        Map<String, Object> rsValues = new HashMap<String, Object>();
        for (int i = 1; i <= cols; i++) {
            Object value = getColumnValue(metaData.getColumnType(i), rs, i);
            String propName = metaData.getColumnLabel(i).toLowerCase();
            if (propName == null)
                propName = metaData.getColumnName(i).toLowerCase();
            rsValues.put(propName, value);
        }
        return rsValues;
    }

    /**
     * 结果集合转换成java bean
     *
     * @param resultSet
     * @param type
     * @return
     * @throws SQLException
     */
    static public Object toBean(ResultSet resultSet, Class<?> type)
            throws SQLException {
        if (resultSet == null)
            throw new SQLException("toBean(resultSet,type) : resultset is null");
        return toBeanInner(resultSet, type);
    }

    /**
     * 结果集合转换成java bean
     *
     * @param resultSet
     * @param type
     * @return
     * @throws SQLException
     */
//	static public Object toBean(ResultSet resultSet, Class<?> type) throws SQLException {
//
//		if ((meta == null) || (resultSet == null))
//			throw new SQLException(
//					"toBean(resultSet,type,meta): meta is null or resultSet is null");
//		return toBeanInner(resultSet, type);
//	}
    public static Object toBeanOfCurrentRow(ResultSet resultSet, Class<?> type,
                                            MethodAndTypes methodAndTypes)
            throws SQLException {
        Object target = null;

        target = newInstance(type);
        for (int i = 1; i <= methodAndTypes.types.length; i++) {
            Object value = getColumnValue(methodAndTypes.types[i - 1],
                    resultSet, i);
            if (value == null)
                continue;
            Method invoke = methodAndTypes.invokes[i - 1];
            if (invoke == null) {
                // 如果是null则不赋值
                continue;
            }
            Converter converter = methodAndTypes.converters[i - 1];
            if (converter != null)
                value = converter.convert(invoke.getParameterTypes()[0], value);
            BeanHelper.invokeMethod(target, invoke, value);
        }
        return target;
    }

    public static Object toBeanOfCurrentRow(ResultSet resultSet, Class<?> type) throws SQLException {
        MethodAndTypes methodAndTypes = getBeanInvokeAndTypes(type, resultSet,
                null);
        return toBeanOfCurrentRow(resultSet, type, methodAndTypes);
    }

    /*
     * 进行Bean的转换
     */
    private static Object toBeanInner(ResultSet resultSet, Class<?> type) throws SQLException {
        Object target = null;
        MethodAndTypes methodAndTypes = getBeanInvokeAndTypes(type, resultSet,
                null);
        if (resultSet.next()) {
            target = toBeanOfCurrentRow(resultSet, type, methodAndTypes);
        } // end if
        return target;
    }

    /**
     * 结果集转换成java bean 数组
     *
     * @param resultSet
     * @param type
     * @return
     * @throws SQLException
     */
    @SuppressWarnings("rawtypes")
    static public List toBeanList(ResultSet resultSet, Class<?> type)
            throws SQLException {
        if (resultSet == null)
            throw new SQLException(
                    "toBeanList(resultSet,type) : resultset is null");
        return toBeanListInner(resultSet, type, null);
    }

    private static List<?> toBeanListInner(ResultSet resultSet, Class<?> type,
                                           String columns[]) throws SQLException {
        MethodAndTypes methodAndTypes = getBeanInvokeAndTypes(type, resultSet,
                columns);
        List<Object> list = new ArrayList<Object>();
        while (resultSet.next()) {
            Object target = newInstance(type);
            for (int i = 1; i <= methodAndTypes.types.length; i++) {
                if (methodAndTypes.propNames[i - 1] == null) {
                    continue;
                }
                Object value = getColumnValue(methodAndTypes.types[i - 1],
                        resultSet, methodAndTypes.colIdx[i - 1]);
                if (value == null)
                    continue;
                Method invoke = methodAndTypes.invokes[i - 1];
                if (invoke == null) {
                    // TODO NEED TEST
                    if (target instanceof SuperVO) {
                        String propName = methodAndTypes.propNames[i - 1];
                        if (propName != null) {
                            ((SuperVO) target).setAttributeValue(propName,
                                    value);
                        }
                    }
                    continue;
                }
                Converter converter = methodAndTypes.converters[i - 1];
                if (converter != null)
                    value = converter.convert(
                            invoke.getParameterTypes()[0], value);
                BeanHelper.invokeMethod(target, invoke, value);
            }
            list.add(target);
        }
        return list;
    }

    public static MethodAndTypes getBeanInvokeAndTypes(Class<?> type,
                                                       ResultSet resultSet, String[] columns)
            throws SQLException {
        MethodAndTypes retResult = new MethodAndTypes();

        Object bean = newInstance(type);
        boolean isSuperBean = false;
        if (bean instanceof SuperVO) {
            isSuperBean = true;
        }
        ResultSetMetaData metaData = resultSet.getMetaData();
        int cols = metaData.getColumnCount();
        int len = columns == null ? 0 : columns.length;
        if (len == 0) {
            len = cols;
        }
        Method[] invokes = new Method[len];
        Converter[] converters = new Converter[len];
        String[] propNames = new String[len];
        int[] idxs = new int[len];
        int[] types = new int[len];
        int j = 0;
        for (int i = 0; i < cols && j < len; i++) {
            int t = metaData.getColumnType(i + 1);
            String propName = metaData.getColumnLabel(i + 1).toLowerCase();
            if (propName == null)
                propName = metaData.getColumnName(i + 1).toLowerCase();
            if (columns != null)
                if (!columns[j].toLowerCase().equals(propName)) {
                    continue;
                }

//			if (meta != null) {
//				propName = MappingMetaManager.getMapingMeta(meta)
//						.getAttributeName(propName);
//				if (propName == null)
//					continue;
//				propName = propName.toLowerCase();
//			}
            idxs[j] = i + 1;
            propNames[j] = propName;
            types[j] = t;
            j++;
        }

        Method[] temps = BeanHelper.getMethods(bean, propNames);
        //EsaConverterManager ecm = new EsaConverterManager();
        for (int i = 0; i < len; i++) {
            if (temps[i] == null) {
                if (isSuperBean) {
                    invokes[i] = getSuperBeanInvokeMethod(bean, propNames[i]);
                } else {
                    invokes[i] = null;
                }
            } else {
                invokes[i] = temps[i];
            }
            if (invokes[i] != null) {
                converters[i] = BeanConvertor.getConVerter(invokes[i]
                        .getParameterTypes()[0]);
//				converters[i] = ecm.findConverter(invokes[i]
//						.getParameterTypes()[0]);
            }
        }

        retResult.propNames = propNames;
        retResult.invokes = invokes;
        retResult.types = types;
        retResult.converters = converters;
        retResult.colIdx = idxs;
        return retResult;
    }

    @SuppressWarnings("deprecation")
    private static Method getSuperBeanInvokeMethod(Object bean, String colName) {
        String pkFiledName = ((SuperVO) bean).getPKFieldName();
        if (pkFiledName == null)
            return null;
        pkFiledName = pkFiledName.toLowerCase();
        if (pkFiledName.equals(colName)) {
            return BeanHelper.getMethod(bean, "primarykey");
        }
        return null;

    }

    @SuppressWarnings("rawtypes")
    static public List toBeanList(ResultSet resultSet, Class<?> type, int skip,
                                  int size) throws SQLException {
        MethodAndTypes methodAndTypes = getBeanInvokeAndTypes(type, resultSet,
                null);
        List<Object> list = new ArrayList<Object>();
        int index = -1;
        while (index < skip && resultSet.next()) {
            index++;
        }
        if (index < skip)
            return list;
        int offset = 0;
        do {
            offset++;
            Object target = newInstance(type);
            for (int i = 1; i <= methodAndTypes.types.length; i++) {
                Object value = getColumnValue(methodAndTypes.types[i - 1],
                        resultSet, i);
                if (value == null)
                    continue;
                Method invoke = methodAndTypes.invokes[i - 1];
                if (invoke == null) {
                    // 如果是null则不赋值
                    continue;
                }
                Converter converter = methodAndTypes.converters[i - 1];
                if (converter != null)
                    value = converter.convert(
                            invoke.getParameterTypes()[0], value);
                BeanHelper.invokeMethod(target, invoke, value);
                // populate(target, value, methodAndTypes.invokes[i - 1]);
            }
            list.add(target);
        } while (resultSet.next() && offset < size);

        return list;
    }

    /**
     * 结果集转换成java bean 数组
     *
     * @param resultSet
     * @param type
     * @return
     * @throws SQLException
     */
//	@SuppressWarnings("rawtypes")
//	static public List toBeanList(ResultSet resultSet, Class<?> type) throws SQLException {
//		if ( (resultSet == null))
//			throw new SQLException(
//					"toBeanList(resultSet,type,meta): meta is null or resultSet is null");
//		return toBeanListInner(resultSet, type,  null);
//	}

    /**
     * 结果集转换成java bean 数组
     *
     * @param resultSet
     * @param type
     * @return
     * @throws SQLException
     */
    static public List<?> toBeanList(ResultSet resultSet, Class<?> type,
                                     String columns[]) throws SQLException {
        if (resultSet == null)
            throw new SQLException(
                    "toBeanList(resultset,type,meta,columns): meta is null or resultSet is null");
        return toBeanListInner(resultSet, type, columns);
    }

    /**
     * 结果集转换成向量集合
     *
     * @param rs
     * @return
     * @throws SQLException
     */
    static public Vector<?> toVector(ResultSet rs) throws SQLException {
        Vector<Vector<Object>> v = new Vector<Vector<Object>>();
        ResultSetMetaData rsmd = rs.getMetaData();
        int nColumnCount = rsmd.getColumnCount();
        //limit sql
        int row_ = -1;
        for (int j = 1; j <= nColumnCount; j++) {
            String colName = rsmd.getColumnLabel(j);
            if (colName == null) {
                colName = rsmd.getColumnName(j);
            }
            colName = colName.toLowerCase();
            if ("row_".equalsIgnoreCase(colName) || "rownum_".equalsIgnoreCase(colName)) {
                row_ = j;
                break;
            }
        }
        while (rs.next()) {
            Vector<Object> tmpV = new Vector<Object>();
            for (int i = 1; i <= nColumnCount; i++) {
                if (row_ == i) {
                    continue;
                }
                Object o;
                if (rsmd.getColumnType(i) == Types.CHAR
                        || rsmd.getColumnType(i) == Types.VARCHAR) {
                    o = rs.getString(i);

                } else {
                    o = rs.getObject(i);
                }

                tmpV.addElement(o);
            }
            v.addElement(tmpV);
        }
        return v;
    }

    public static Object getColumnValue(int type, ResultSet resultSet, int i)
            throws SQLException {
        Object value;
        switch (type) {
            case Types.VARCHAR:
            case Types.CHAR:
                value = resultSet.getString(i);
                break;
            // case Types.INTEGER:
            // case Types.DECIMAL:
            // value = new Integer(resultSet.getInt(i));
            // break;
            case Types.BLOB:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
            case Types.BINARY:
                value = InOutUtil.deserialize(resultSet.getBytes(i));
                break;
            case Types.CLOB:
                value = getClob(resultSet, i);
                break;
            default:
                value = resultSet.getObject(i);
                break;
        }
        return value;
    }

    public static Object getColumnValue(int type, ResultSet resultSet,
                                        String colName) throws SQLException {
        Object value;
        switch (type) {
            case Types.VARCHAR:
            case Types.CHAR:
                value = resultSet.getString(colName);
                break;
            // case Types.INTEGER:
            // case Types.DECIMAL:
            // value = new Integer(resultSet.getInt(i));
            // break;
            case Types.BLOB:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
            case Types.BINARY:
                value = InOutUtil.deserialize(resultSet.getBytes(colName));
                break;
            case Types.CLOB:
                value = getClob(resultSet, colName);
                break;
            default:
                value = resultSet.getObject(colName);
                break;
        }
        return value;
    }

    /**
     * @param c
     * @return
     * @throws SQLException
     */
    static private Object newInstance(Class<?> c) throws SQLException {
        try {
            return c.newInstance();

        } catch (InstantiationException e) {
            throw new SQLException("Cannot create " + c.getName() + ": "
                    + e.getMessage());

        } catch (IllegalAccessException e) {
            throw new SQLException("Cannot create " + c.getName() + ": "
                    + e.getMessage());
        }
    }

    /**
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    static public String getClob(ResultSet rs, int columnIndex)
            throws SQLException {
        Reader rsReader = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;
        try {
            rsReader = rs.getCharacterStream(columnIndex);
            if (rsReader == null)
                return null;
            reader = new BufferedReader(rsReader);
            buffer = new StringBuffer();
            while (true) {
                String c = reader.readLine();
                if (c == null) {
                    break;
                }
                buffer.append(c);
                buffer.append("\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (rsReader != null) {
                    rsReader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            try {
                if (reader != null) {

                    reader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }

        return buffer.toString();
    }

    static public String getClob(ResultSet rs, String colName)
            throws SQLException {
        Reader rsReader = null;
        BufferedReader reader = null;
        StringBuffer buffer = null;
        try {
            rsReader = rs.getCharacterStream(colName);
            if (rsReader == null)
                return null;
            reader = new BufferedReader(rsReader);
            buffer = new StringBuffer();
            while (true) {
                String c = reader.readLine();
                if (c == null) {
                    break;
                }
                buffer.append(c);
                buffer.append("\n");
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            try {
                if (rsReader != null) {
                    rsReader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            try {
                if (reader != null) {

                    reader.close();
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }

        }
        return buffer.toString();
    }

    /**
     * @param rs
     * @param columnIndex
     * @return
     * @throws SQLException
     */
    static public byte[] getBlob(ResultSet rs, int columnIndex)
            throws SQLException {
        return rs.getBytes(columnIndex);
    }

}

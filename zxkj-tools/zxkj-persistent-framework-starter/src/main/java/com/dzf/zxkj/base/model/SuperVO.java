package com.dzf.zxkj.base.model;

import com.dzf.zxkj.base.utils.BeanHelper;
import com.dzf.zxkj.common.lang.DZFDateTime;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class SuperVO<T extends SuperVO<T>> extends CircularlyAccessibleValueObject implements
        Serializable, Cloneable {

    protected int page = 1;// 当前页
    protected int rows = 10;// 每页显示记录数
    protected String sort;// 排序字段
    protected String order = "asc";// asc/desc
    private String parent_id = "";// 父节点
    private static transient Map<Class<? extends SuperVO>, String[]> map = new HashMap();
    private static transient ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private T[] children;
    private List<T> listchild;
    private DZFDateTime updatets;//修改时间----数据库类型为char(19)-----2017-01-01 23:12:12

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public void setAttributeValue(String name, Object value) {
        String key = name;
        String pkName = getPKFieldName();
        if ((pkName != null) && (pkName.equals(name))) {
            key = "primarykey";
        }
        BeanHelper.setProperty(this, key, value);
    }

    public Object getAttributeValue(String name) {
        String key = name;
        String pkName = getPKFieldName();
        if ((pkName != null) && (pkName.equals(name))) {
            key = "primarykey";
        }
        return BeanHelper.getProperty(this, key);
    }


    public T[] getChildren() {
        return children;
    }

    public void setChildren(T[] children) {
        this.children = children;
    }

    public void addChildren(T vo) {
        if (children == null || children.length == 0 || listchild == null
                || listchild.size() == 0) {
            listchild = new ArrayList<T>();
        } else if (children != null && children.length > 0
                && (listchild == null || listchild.size() == 0)) {
            listchild = new ArrayList<T>(Arrays.asList(children));
        }
        vo.setParent_id(this.getPrimaryKey());
        listchild.add(vo);
        Class cla = vo.getClass();
        T[] array = (T[]) Array.newInstance(cla, listchild.size());
        children = listchild.toArray(array);
    }


    public Object clone() {
        SuperVO vo = null;
        try {
            vo = (SuperVO) getClass().newInstance();
        } catch (Exception e) {
        }
        String[] fieldNames = getAttributeNames();
        if (fieldNames != null) {
            for (int i = 0; i < fieldNames.length; i++) {
                try {
                    vo.setAttributeValue(fieldNames[i],
                            getAttributeValue(fieldNames[i]));
                } catch (Exception ex) {
                }
            }
        }
        vo.setStatus(getStatus());
        return vo;
    }

    public String getPrimaryKey() {
        String pkName = getPKFieldName();
        if (pkName == null) {
            return null;
        }
        return (String) BeanHelper.getProperty(this, pkName);

    }

    public void setPrimaryKey(String key) {
        String pkName = getPKFieldName();
        if (pkName == null) {
            return;
        }
        BeanHelper.setProperty(this, pkName, key);
        return;
    }

    public Set<String> usedAttributeNames() {
        Set usedSet = new HashSet();
        String[] names = getAttributeNames();
        for (String name : names) {
            usedSet.add(name);
        }
        return usedSet;
    }

    public String toString() {
        StringBuffer buffer = new StringBuffer();
        Set set = usedAttributeNames();
        String[] names = getAttributeNames();
        for (String name : names) {
            boolean flag = set.contains(name);
            Object value = getAttributeValue(name);
            append(buffer, name, value);
            if (!flag) {
                buffer.append("[NOT SETTING]");
            }
            buffer.append("\r\n");
        }
        return buffer.toString();
    }

    private void append(StringBuffer buffer, String name, Object value) {
        buffer.append(name);
        buffer.append("=");
        buffer.append(value);
        return;

    }

    public String[] getAttributeNames() {
        rwl.readLock().lock();
        try {
            return getAttributeAryNoMetaData();
        } finally {
            rwl.readLock().unlock();
        }

    }

    public void validate() throws Exception {
    }


    private String[] getAttributeAryNoMetaData() {
        String[] arys = (String[]) map.get(getClass());
        if (arys == null) {
            rwl.readLock().unlock();
            rwl.writeLock().lock();
            try {
                arys = (String[]) map.get(getClass());
                if (arys == null) {
                    Set set = new HashSet();
                    String[] strAry = BeanHelper.getInstance()
                            .getPropertiesAry(this);

                    for (String str : strAry)
                        if ((getPKFieldName() != null)
                                && (str.equals("primarykey"))) {
                            set.add(getPKFieldName());
                        } else if ((!str.equals("status"))
                                && (!str.equals("dirty"))
                                && (!str.equals("valueIndex"))) {
                            set.add(str);
                        }
                    arys = (String[]) set.toArray(new String[set.size()]);
                    map.put(getClass(), arys);
                }
            } finally {
                rwl.readLock().lock();
                rwl.writeLock().unlock();
            }
        }
        return arys;
    }

    public boolean equalsContent(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        return equalsContent((SuperVO) obj, getAttributeNames());
    }

    public boolean equalsContent(SuperVO vo, String[] attributes) {
        if ((attributes == null) || (vo == null)) {
            return false;
        }
        for (String attribute : attributes) {
            Object value1 = getAttributeValue(attribute);
            Object value2 = vo.getAttributeValue(attribute);
            boolean flag = isAttributeEquals(value1, value2);
            if (!flag) {
                return false;
            }
        }
        return true;
    }

//	public Integer getVersion() {
//		return version;
//	}
//
//	public void setVersion(Integer version) {
//		this.version = version;
//	}

    private boolean isAttributeEquals(Object attrOld, Object attrNew) {
        if (attrOld == attrNew) {
            return true;
        }
        if ((attrOld == null) || (attrNew == null)) {
            return false;
        }
        return attrOld.equals(attrNew);
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public DZFDateTime getUpdatets() {
        return updatets;
    }

    public void setUpdatets(DZFDateTime updatets) {
        this.updatets = updatets;
    }

    public abstract String getParentPKFieldName();

    public abstract String getPKFieldName();

    public abstract String getTableName();
}
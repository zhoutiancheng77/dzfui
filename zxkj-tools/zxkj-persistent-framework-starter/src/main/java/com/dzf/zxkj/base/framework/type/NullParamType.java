package com.dzf.zxkj.base.framework.type;


/**
 * 空类型
 * User: 贺扬
 * Date: 2005-2-3
 * Time: 9:08:38
 */
public class NullParamType implements SQLParamType {
    /**
     * <code>serialVersionUID</code> 的注释
     */
    private static final long serialVersionUID = -6229083933859489148L;
    int type;

    public NullParamType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

package com.dzf.zxkj.common.entity;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * JSON模型
 * <p>
 * 用户后台向前台返回的JSON对象
 *
 * @author 孙宇
 */
public class Json implements java.io.Serializable {

    private boolean success = false;

    private int status = 200;

    private String msg = "";

    // 允许序列化空集合
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object rows = null;

    private Object data = null;

    private Object childs = null;

    private Object head = null;

    private Long total = 0L;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getRows() {
        return rows;
    }

    public void setRows(Object rows) {
        this.rows = rows;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Object getChilds() {
        return childs;
    }

    public void setChilds(Object childs) {
        this.childs = childs;
    }

    public Object getHead() {
        return head;
    }

    public void setHead(Object head) {
        this.head = head;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

}

package com.dzf.zxkj.common.entity;

import com.dzf.zxkj.common.constant.HttpStatus;

import java.io.Serializable;

/**
 * @Auther: dandelion
 * @Date: 2019-09-02
 * @Description:
 */
public class ReturnData<T> implements Serializable {
    private int status = HttpStatus.OK.value();

    private String message;

    public ReturnData() {

    }

    public ReturnData(int status) {
        this.status = status;
    }

    public ReturnData(String message) {
        this.message = message;
    }

    public ReturnData(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public ReturnData(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static ReturnData error(int status){
        return new ReturnData(status);
    }

    public static ReturnData error() {
        return error("未知异常，请联系管理员");
    }

    public static ReturnData error(String message) {
        return new ReturnData(HttpStatus.INTERNAL_SERVER_ERROR.value(), message);
    }

    public static ReturnData ok() {
        return new ReturnData();
    }

    public ReturnData data(T data) {
        this.setData(data);
        return this;
    }

    public ReturnData message(String message) {
        this.setMessage(message);
        return this;
    }

    public ReturnData status(int status) {
        this.setStatus(status);
        return this;
    }
}

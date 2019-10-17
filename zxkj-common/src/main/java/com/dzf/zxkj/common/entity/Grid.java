package com.dzf.zxkj.common.entity;

import lombok.Data;

/**
 * EasyUI DataGrid模型
 *
 * @author 孙宇
 */
@Data
public class Grid<T> implements java.io.Serializable {
    private Long total = 0L;
    private T rows;
    private boolean success;
    private String msg;
}

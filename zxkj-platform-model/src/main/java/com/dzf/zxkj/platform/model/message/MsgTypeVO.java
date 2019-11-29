package com.dzf.zxkj.platform.model.message;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 消息通知类型
 *
 * @author dzf
 */
public class MsgTypeVO extends SuperVO {
    private Integer type_code;
    private String type_name;
    private int new_msg = 0;
    private int total = 0;
    private DZFDateTime latest_time;

    public Integer getType_code() {
        return type_code;
    }

    public void setType_code(Integer type_code) {
        this.type_code = type_code;
    }

    public String getType_name() {
        return type_name;
    }

    public void setType_name(String type_name) {
        this.type_name = type_name;
    }

    public int getNew_msg() {
        return new_msg;
    }

    public void setNew_msg(int new_msg) {
        this.new_msg = new_msg;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public DZFDateTime getLatest_time() {
        return latest_time;
    }

    public void setLatest_time(DZFDateTime latest_time) {
        this.latest_time = latest_time;
    }

    @Override
    public String getParentPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getPKFieldName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getTableName() {
        // TODO Auto-generated method stub
        return null;
    }

}
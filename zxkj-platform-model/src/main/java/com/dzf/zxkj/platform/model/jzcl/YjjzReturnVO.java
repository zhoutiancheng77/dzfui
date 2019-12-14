package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.model.bdset.ExrateVO;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/12/12
 * @Modified By:
 */
public class YjjzReturnVO extends SuperVO {

    private int statuscode = 100;
    /**
     * 100，代表成功
     * 200，代表弹出汇兑调整
     * 300，代表弹出暂估对话框(启用库存)
     * 400，代表弹出工业结转对话框(启用库存)
     * 500，代表弹出不启用库存的对话框(不启用库存)
     * 600，代表弹出不启用库存的工业结转(不启用库存)
     */
    private ExrateVO[] listrate = null;//查询汇兑调整的数据

    private boolean icinvtentory = false; // 是否启用总账存货

    private boolean success = true;

    private String msg = "";//全局消息
    //当前执行的步骤
    private int currentproject = 1;
    //是否将信息返回前台
    private boolean isreturn = false;

    private StringBuffer allmessage = new StringBuffer();

    private String branchmsg = "";//单独返回前台消息

    private ZgDataTrans zginfo = null;

    public int getStatuscode() {
        return statuscode;
    }

    public void setStatuscode(int statuscode) {
        this.statuscode = statuscode;
    }

    public ExrateVO[] getListrate() {
        return listrate;
    }

    public void setListrate(ExrateVO[] listrate) {
        this.listrate = listrate;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return allmessage.toString();
    }

//	public void setMsg(String msg) {
//		this.msg = msg;
//	}

    public ZgDataTrans getZginfo() {
        return zginfo;
    }

    public void setZginfo(ZgDataTrans zginfo) {
        this.zginfo = zginfo;
    }

    public String getBranchmsg() {
        return branchmsg;
    }

    public void setBranchmsg(String branchmsg) {
        this.branchmsg = branchmsg;
    }

    public int getCurrentproject() {
        return currentproject;
    }

    public void setCurrentproject(int currentproject) {
        this.currentproject = currentproject;
    }

    public boolean isIsreturn() {
        return isreturn;
    }

    public void setIsreturn(boolean isreturn) {
        this.isreturn = isreturn;
    }

    public StringBuffer getAllmessage() {
        return allmessage;
    }

    public void setAllmessage(StringBuffer allmessage) {
        this.allmessage = allmessage;
    }

    @Override
    public String getPKFieldName() {
        return null;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return null;
    }

    public boolean isIcinvtentory() {
        return icinvtentory;
    }

    public void setIcinvtentory(boolean icinvtentory) {
        this.icinvtentory = icinvtentory;
    }
}
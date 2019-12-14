package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/12/12
 * @Modified By:
 */
public class ZgDataTrans extends SuperVO {

    private String msg;

    private boolean iszg;

    private TempInvtoryVO[] torys;

    public String getMsg() {
        return msg;
    }
    public void setMsg(String msg) {
        this.msg = msg;
    }
    public boolean isIszg() {
        return iszg;
    }
    public void setIszg(boolean iszg) {
        this.iszg = iszg;
    }
    public TempInvtoryVO[] getTorys() {
        return torys;
    }
    public void setTorys(TempInvtoryVO[] torys) {
        this.torys = torys;
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
}

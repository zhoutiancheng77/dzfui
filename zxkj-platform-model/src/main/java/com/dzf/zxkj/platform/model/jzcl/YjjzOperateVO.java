package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

/**
 * @Author: zpm
 * @Description:
 * @Date:Created by 2019/12/12
 * @Modified By:
 */
public class YjjzOperateVO implements java.io.Serializable{

    private QmclVO qmvo;

    private List<SetJz> jzsets;

    private String pk_corp;

    private String userid;

    private CorpVO corpvo;

    private String operatemsg;

    private boolean isgoonjz = true;//当前记录是否继续一键结转标志

    public QmclVO getQmvo() {
        return qmvo;
    }

    public void setQmvo(QmclVO qmvo) {
        this.qmvo = qmvo;
    }

    public List<SetJz> getJzsets() {
        return jzsets;
    }

    public void setJzsets(List<SetJz> jzsets) {
        this.jzsets = jzsets;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public CorpVO getCorpvo() {
        return corpvo;
    }

    public void setCorpvo(CorpVO corpvo) {
        this.corpvo = corpvo;
    }

    public String getOperatemsg() {
        return operatemsg;
    }

    public void setOperatemsg(String operatemsg) {
        this.operatemsg = operatemsg;
    }

    public boolean isIsgoonjz() {
        return isgoonjz;
    }

    public void setIsgoonjz(boolean isgoonjz) {
        this.isgoonjz = isgoonjz;
    }
}

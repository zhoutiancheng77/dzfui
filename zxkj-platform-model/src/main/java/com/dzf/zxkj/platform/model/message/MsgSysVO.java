package com.dzf.zxkj.platform.model.message;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息通知VO-管理平台
 *
 * @author dzf
 */
public class MsgSysVO extends SuperVO {

    private static final long serialVersionUID = 1L;

    private final String TABLE_NAME = "YNT_MSG_SYS";

    @JsonProperty("pk_id")
    private String pk_message; // 主键

    @JsonProperty("corpid")
    private String pk_corp; // 所属分部

    @JsonProperty("sdman")
    private String sendman;// 发送人

    private String sendmanname;//发送人姓名

    @JsonProperty("vsdate")
    private String vsenddate; // 发送时间

    @JsonProperty("ssend")
    private String sys_send;// 发送端

    @JsonProperty("sreceive")
    private String sys_receive;// 接收端

    @JsonProperty("content")
    private String vcontent; // 消息内容

    @JsonProperty("isread")
    private DZFBoolean isread;// 是否已读

    @JsonProperty("dr")
    private Integer dr; // 删除标记

    @JsonProperty("ts")
    private DZFDateTime ts; // 时间戳

    private Integer msgtype;// 消息类型

    private String msgtypename;// 消息类型名称

    private String vtitle;//标题

    public String getSendmanname() {
        return sendmanname;
    }

    public void setSendmanname(String sendmanname) {
        this.sendmanname = sendmanname;
    }

    public String getVtitle() {
        return vtitle;
    }

    public void setVtitle(String vtitle) {
        this.vtitle = vtitle;
    }

    public String getMsgtypename() {
        return msgtypename;
    }

    public void setMsgtypename(String msgtypename) {
        this.msgtypename = msgtypename;
    }

    public String getSendman() {
        return sendman;
    }

    public void setSendman(String sendman) {
        this.sendman = sendman;
    }

    public String getSys_send() {
        return sys_send;
    }

    public void setSys_send(String sys_send) {
        this.sys_send = sys_send;
    }

    public String getSys_receive() {
        return sys_receive;
    }

    public void setSys_receive(String sys_receive) {
        this.sys_receive = sys_receive;
    }

    public Integer getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(Integer msgtype) {
        this.msgtype = msgtype;
    }

    public DZFBoolean getIsread() {
        return isread;
    }

    public void setIsread(DZFBoolean isread) {
        this.isread = isread;
    }

    public String getPk_message() {
        return pk_message;
    }

    public void setPk_message(String pk_message) {
        this.pk_message = pk_message;
    }

    public String getVsenddate() {
        return vsenddate;
    }

    public void setVsenddate(String vsenddate) {
        this.vsenddate = vsenddate;
    }

    public String getVcontent() {
        return vcontent;
    }

    public void setVcontent(String vcontent) {
        this.vcontent = vcontent;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
    }

    public Integer getDr() {
        return dr;
    }

    public void setDr(Integer dr) {
        this.dr = dr;
    }

    public DZFDateTime getTs() {
        return ts;
    }

    public void setTs(DZFDateTime ts) {
        this.ts = ts;
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getPKFieldName() {
        return "pk_message";
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }

}
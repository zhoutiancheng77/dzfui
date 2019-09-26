package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 消息通知VO
 * 
 * @author dzf
 *
 */
public class MsgAdminVO extends SuperVO {

	private static final long serialVersionUID = 1L;

	private final String TABLE_NAME = "YNT_MSG_ADMIN";

	@JsonProperty("pk_id")
	private String pk_message; // 主键

	@JsonProperty("corpid")
	private String pk_corp; // 所属分部

	@JsonProperty("corpkid")
	private String pk_corpk; // 所属分部
	
	@JsonProperty("ncode")
	private String nodecode;//节点
	
	@JsonProperty("sdman")
	private String sendman;//发送人

	@JsonProperty("vsdate")
	private String vsenddate; // 发送时间
	
	@JsonProperty("ssend")
	private String sys_send;//发送端
	
	@JsonProperty("sreceive")
	private String sys_receive;//接收端

	@JsonProperty("uid")
	private String cuserid; // 接收人

	@JsonProperty("content")
	private String vcontent; // 消息内容
	
	@JsonProperty("lctent")
	private String vlctent; // 办理流程节点

	@JsonProperty("isread")
	private DZFBoolean isread;// 是否已读

	@JsonProperty("dr")
	private Integer dr; // 删除标记

	@JsonProperty("ts")
	private DZFDateTime ts; // 时间戳
	
	private Integer msgtype;//消息类型
	
	private String msgtypename;//消息类型名称
	
	private String vtitle;//标题
	
	private String pk_bill;//消息来源主键
	
	private String pk_bill_b;//消息来源子表主键
	
	private String pk_busitype;//业务小类
	
	private String pk_product;//业务大类
	
	private String pk_order;//订单ID
	
	private String pk_order_b;//订单子表ID
	
	private String vperiod;
	
	private String pk_workflow;
	
	//非数据库字段
	private String bdate;
	private String edate;
	private Integer new_msg;
	@JsonProperty("tcorp")
	private String pk_temp_corp ;//临时公司pk
	
	@JsonProperty("ista")
	private Integer istatus; // 状态 
	
	@JsonProperty("businm")
	private String vbusiname;//服务项目名称
	    
	@JsonProperty("flownm")
	private String vflowname;//节点名称
	    
	@JsonProperty("totmny")
	private DZFDouble ntotalmny;//合计金额
	
	@JsonProperty("busitype")
	private Integer ibusitype;//业务类型
	
	@JsonProperty("vstime")
	private String vsendtime; // 发送时间
	
	public String getVsendtime() {
        return vsendtime;
    }

    public void setVsendtime(String vsendtime) {
        this.vsendtime = vsendtime;
    }

    public Integer getIbusitype() {
        return ibusitype;
    }

    public void setIbusitype(Integer ibusitype) {
        this.ibusitype = ibusitype;
    }

    public String getVbusiname() {
        return vbusiname;
    }

    public void setVbusiname(String vbusiname) {
        this.vbusiname = vbusiname;
    }

    public String getVflowname() {
        return vflowname;
    }

    public void setVflowname(String vflowname) {
        this.vflowname = vflowname;
    }

    public DZFDouble getNtotalmny() {
        return ntotalmny;
    }

    public void setNtotalmny(DZFDouble ntotalmny) {
        this.ntotalmny = ntotalmny;
    }

    public Integer getIstatus() {
        return istatus;
    }

    public void setIstatus(Integer istatus) {
        this.istatus = istatus;
    }

    public String getPk_workflow() {
        return pk_workflow;
    }

    public void setPk_workflow(String pk_workflow) {
        this.pk_workflow = pk_workflow;
    }

    public String getVperiod() {
		return vperiod;
	}

	public void setVperiod(String vperiod) {
		this.vperiod = vperiod;
	}

	public Integer getNew_msg() {
		return new_msg;
	}

	public void setNew_msg(Integer new_msg) {
		this.new_msg = new_msg;
	}

	public String getPk_bill_b() {
		return pk_bill_b;
	}

	public void setPk_bill_b(String pk_bill_b) {
		this.pk_bill_b = pk_bill_b;
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

	public String getNodecode() {
		return nodecode;
	}

	public void setNodecode(String nodecode) {
		this.nodecode = nodecode;
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

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getVlctent() {
		return vlctent;
	}

	public void setVlctent(String vlctent) {
		this.vlctent = vlctent;
	}

	public String getPk_order_b() {
		return pk_order_b;
	}

	public void setPk_order_b(String pk_order_b) {
		this.pk_order_b = pk_order_b;
	}

	public String getPk_bill() {
		return pk_bill;
	}

	public void setPk_bill(String pk_bill) {
		this.pk_bill = pk_bill;
	}

	public String getPk_busitype() {
		return pk_busitype;
	}

	public void setPk_busitype(String pk_busitype) {
		this.pk_busitype = pk_busitype;
	}

	public String getPk_product() {
		return pk_product;
	}

	public void setPk_product(String pk_product) {
		this.pk_product = pk_product;
	}

	public String getPk_order() {
		return pk_order;
	}

	public void setPk_order(String pk_order) {
		this.pk_order = pk_order;
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

	public String getPk_corpk() {
		return pk_corpk;
	}

	public void setPk_corpk(String pk_corpk) {
		this.pk_corpk = pk_corpk;
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

	public String getBdate() {
		return bdate;
	}

	public void setBdate(String bdate) {
		this.bdate = bdate;
	}

	public String getEdate() {
		return edate;
	}

	public void setEdate(String edate) {
		this.edate = edate;
	}
	

	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
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

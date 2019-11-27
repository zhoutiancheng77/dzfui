package com.dzf.zxkj.platform.model.sys;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 代办事项
 * 
 * @author zhangj
 *
 */
public class WorkToDoVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_work_todo";

	public static final String PK_FIELD = "pk_work_todo";

	@JsonProperty("id")
	private String pk_work_todo;//主键
	@JsonProperty("corp")
	private String pk_corp;//公司
	private String coperatorid;//操作人
	private DZFDateTime doperatedate;//操作时间
	@JsonProperty("gzr")
	private String vattentor;//关注人
	@JsonProperty("jzsj")
	private DZFDateTime denddatetime;//截止时间
	@JsonProperty("nr")
	private String vcontent;//内容
	@JsonProperty("zt")
	private Integer istatus;//工作状态 -1 不用处理 0 未处理，1 已处理
	
	@JsonProperty("fcorp")
	private String fathercorp;//会计机构ID
	
	public String getFathercorp() {
        return fathercorp;
    }

    public void setFathercorp(String fathercorp) {
        this.fathercorp = fathercorp;
    }

    public Integer getIstatus() {
		return istatus;
	}

	public void setIstatus(Integer istatus) {
		this.istatus = istatus;
	}

	public String getPk_work_todo() {
		return pk_work_todo;
	}

	public void setPk_work_todo(String pk_work_todo) {
		this.pk_work_todo = pk_work_todo;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDateTime doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getVattentor() {
		return vattentor;
	}

	public void setVattentor(String vattentor) {
		this.vattentor = vattentor;
	}

	public DZFDateTime getDenddatetime() {
		return denddatetime;
	}

	public void setDenddatetime(DZFDateTime denddatetime) {
		this.denddatetime = denddatetime;
	}

	public String getVcontent() {
		return vcontent;
	}

	public void setVcontent(String vcontent) {
		this.vcontent = vcontent;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}

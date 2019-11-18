package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class BsWorkDocVO extends SuperVO {
	
	private static final long serialVersionUID = 1L;

	@JsonProperty("docid")
	private String pk_bsworkdoc; // 主键
	
	@JsonProperty("cpperiod")
	private String pk_corpperiod;//客户期间主键(客户主键+年-月)
	
	@JsonProperty("khid")
	private String pk_corp;//客户主键
	
    @JsonProperty("fcorp")
    private String fathercorp;//会计公司主键
    
    @JsonProperty("period")
    private String period;//期间

	@JsonProperty("docname")
	private String docname; // 附件名称(中文)

	@JsonProperty("fpath")
	private String vfilepath;// 文件存储路径

	@JsonProperty("operid")
	private String coperatorid;//创建人
	
	@JsonProperty("opertime")
	private DZFDateTime doperatetime;//创建时间
	
	private Integer dr; // 删除标记

	private DZFDateTime ts; // 时间

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getPk_corpperiod() {
		return pk_corpperiod;
	}

	public void setPk_corpperiod(String pk_corpperiod) {
		this.pk_corpperiod = pk_corpperiod;
	}

	public String getPk_bsworkdoc() {
		return pk_bsworkdoc;
	}

	public void setPk_bsworkdoc(String pk_bsworkdoc) {
		this.pk_bsworkdoc = pk_bsworkdoc;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getDocname() {
		return docname;
	}

	public void setDocname(String docname) {
		this.docname = docname;
	}

	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatetime() {
		return doperatetime;
	}

	public void setDoperatetime(DZFDateTime doperatetime) {
		this.doperatetime = doperatetime;
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
	public String getPKFieldName() {
		return "pk_bsworkdoc";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_bsworkdoc";
	}

}

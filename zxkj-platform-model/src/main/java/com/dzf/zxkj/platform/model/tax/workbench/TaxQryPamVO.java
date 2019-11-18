package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class TaxQryPamVO extends SuperVO {

	private static final long serialVersionUID = -3652246273046896354L;
	
	@JsonProperty("pamid")
	private String pk_taxqrypam;
	
    @JsonProperty("ovince")
    private Integer vprovince;// 报税地区
	
	@JsonProperty("code")
	private Integer icode;//编码    对应税种：
	//1：增值税；2：消费税；3：企业所得税；4：文化事业建设费；5：附加税合计；6：城建税；7：教育费附加；8：地方教育费附加；9：个人所得税；10：财报；
	
    @JsonProperty("chname")
    private String chargedeptname;// 纳税人资格   一般纳税人/小规模纳税人
	
    @JsonProperty("bh")
	private String sbzlbh;//申报种类编号
	
    @JsonProperty("rename")
	private String reportname;//报表名称
	
	private String x;//坐标x
	
	private String y;//坐标y
	
	private DZFDateTime ts;
	
	private Integer dr;

	public String getPk_taxqrypam() {
		return pk_taxqrypam;
	}

	public void setPk_taxqrypam(String pk_taxqrypam) {
		this.pk_taxqrypam = pk_taxqrypam;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public Integer getIcode() {
		return icode;
	}

	public void setIcode(Integer icode) {
		this.icode = icode;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public String getSbzlbh() {
		return sbzlbh;
	}

	public void setSbzlbh(String sbzlbh) {
		this.sbzlbh = sbzlbh;
	}

	public String getReportname() {
		return reportname;
	}

	public void setReportname(String reportname) {
		this.reportname = reportname;
	}

	public String getX() {
		return x;
	}

	public void setX(String x) {
		this.x = x;
	}

	public String getY() {
		return y;
	}

	public void setY(String y) {
		this.y = y;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
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

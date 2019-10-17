package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 风控体检执行
 * 
 * @author zhangj
 *
 */
public class FkTjSetVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_fktj";
	public static final String PK_FIELD = "pk_fktj";

	@JsonProperty("id")
	private String pk_fktj;//
	private String pk_corp;// 公司
	private String vmemo;// 备注
	@JsonProperty("idate")
	private DZFDateTime inspectdate;// 体检时间
	private String qj;//会计期间
	private String vinspector;// 体检人
	private Integer dr;// 标识
	private DZFDateTime ts;//
	private String hy;//行业
	
	private DZFDate begindate;
	private DZFDate enddate;//结束时间
	
	
	public String getHy() {
		return hy;
	}

	public void setHy(String hy) {
		this.hy = hy;
	}

	public String getQj() {
		return qj;
	}

	public void setQj(String qj) {
		this.qj = qj;
	}

	public DZFDate getBegindate() {
		return begindate;
	}

	public void setBegindate(DZFDate begindate) {
		this.begindate = begindate;
	}

	public DZFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(DZFDate enddate) {
		this.enddate = enddate;
	}

	public String getPk_fktj() {
		return pk_fktj;
	}

	public void setPk_fktj(String pk_fktj) {
		this.pk_fktj = pk_fktj;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public DZFDateTime getInspectdate() {
		return inspectdate;
	}

	public void setInspectdate(DZFDateTime inspectdate) {
		this.inspectdate = inspectdate;
	}

	public String getVinspector() {
		return vinspector;
	}

	public void setVinspector(String vinspector) {
		this.vinspector = vinspector;
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

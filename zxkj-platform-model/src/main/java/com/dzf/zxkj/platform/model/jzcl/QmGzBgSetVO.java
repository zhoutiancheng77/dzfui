package com.dzf.zxkj.platform.model.jzcl;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 期末关账报告设置
 * 
 * @author zhangj
 *
 */
public class QmGzBgSetVO extends SuperVO {

	public static final String TABLE_NAME = "ynt_gzbg_set";

	public static final String PK_FIELD = "pk_gzbg_set";

	private String pk_gzbg_set;// 主键
	@JsonProperty("corp")
	private String pk_corp;// 公司
	@JsonProperty("xm")
	private String vxm;// 项目
	@JsonProperty("max")
	private DZFDouble nmax;// 最大值
	@JsonProperty("min")
	private DZFDouble nmin;// 最小值
	private String coperatorid;// 操作人
	private DZFDateTime doperatordate;// 操作日期
	private Integer dr;//
	private DZFDateTime ts;//
	
	public String getPk_gzbg_set() {
		return pk_gzbg_set;
	}

	public void setPk_gzbg_set(String pk_gzbg_set) {
		this.pk_gzbg_set = pk_gzbg_set;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVxm() {
		return vxm;
	}

	public void setVxm(String vxm) {
		this.vxm = vxm;
	}

	public DZFDouble getNmax() {
		return nmax;
	}

	public void setNmax(DZFDouble nmax) {
		this.nmax = nmax;
	}

	public DZFDouble getNmin() {
		return nmin;
	}

	public void setNmin(DZFDouble nmin) {
		this.nmin = nmin;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDateTime getDoperatordate() {
		return doperatordate;
	}

	public void setDoperatordate(DZFDateTime doperatordate) {
		this.doperatordate = doperatordate;
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

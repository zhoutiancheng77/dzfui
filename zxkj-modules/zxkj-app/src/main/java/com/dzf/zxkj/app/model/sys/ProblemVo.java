package com.dzf.zxkj.app.model.sys;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 常见问题
 * @author zhangj
 *
 */
public class ProblemVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_app_problem";

	public static final String PK_FIELD = "pk_app_problem";

	@JsonProperty("id")
	private String pk_app_problem;//
	@JsonProperty("lx")
	private String vprotype;//
	@JsonProperty("problem")
	private String vproblem;//
	@JsonProperty("answer")
	private String vanswer;//
	private Integer iorder;//
	private String pk_corp;//
	private Integer dr;//
	private DZFDateTime ts;//
	public String getPk_app_problem() {
		return pk_app_problem;
	}

	public void setPk_app_problem(String pk_app_problem) {
		this.pk_app_problem = pk_app_problem;
	}

	public String getVprotype() {
		return vprotype;
	}

	public void setVprotype(String vprotype) {
		this.vprotype = vprotype;
	}

	public String getVproblem() {
		return vproblem;
	}

	public void setVproblem(String vproblem) {
		this.vproblem = vproblem;
	}

	public String getVanswer() {
		return vanswer;
	}

	public void setVanswer(String vanswer) {
		this.vanswer = vanswer;
	}

	public Integer getIorder() {
		return iorder;
	}

	public void setIorder(Integer iorder) {
		this.iorder = iorder;
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

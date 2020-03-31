package com.dzf.zxkj.platform.model.batchprint;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 批量报表打印
 * 
 * @author zhangj
 *
 */
public class BatchPrintSetVo extends SuperVO {

	public static final String TABLE_NAME = "ynt_batch_print_set";

	public static final String PK_FIELD = "pk_batch_print_set";

	@JsonProperty("id")
	private String pk_batch_print_set;// 主键
	@JsonProperty("cid")
	private String pk_corp;// 公司
	@JsonProperty("qj")
	private String vprintperiod;// 打印期间
	@JsonProperty("filename")
	private String vfilename;// 文件名称
	private String vfilepath;// 文件路径
	@JsonProperty("zt")
	private Integer ifilestatue;// 状态
	@JsonProperty("reportname")
	private String vprintname;// 打印名称
	@JsonProperty("reportcode")
	private String vprintcode;//打印编码
	@JsonProperty("czrq")
	private DZFDateTime doperadatetime;// 操作日期
	@JsonProperty("czr")
	private String voperateid;// 操作人
	@JsonProperty("other")
	private String vothername;//制单人(其他)
	@JsonProperty("zdrlx")
	private Integer zdrlx;//制单人类型
	private DZFDateTime dgendatetime;// 生成日期
	private DZFDateTime ts;//
	private Integer dr;//
	@JsonProperty("memo")
	private String vmemo;//备注
	
	@JsonProperty("ztdx")
	private DZFDouble vfontsize;// 字体大小
	@JsonProperty("dyrq")
	private DZFDate dprintdate;// 打印日期
	@JsonProperty("left")
	private DZFDouble dleftmargin;// 左边距
	@JsonProperty("top")
	private DZFDouble dtopmargin;// 上边距
	
	private String cname;//公司名称
	
	public String getVothername() {
		return vothername;
	}

	public void setVothername(String vothername) {
		this.vothername = vothername;
	}

	public Integer getZdrlx() {
		return zdrlx;
	}

	public void setZdrlx(Integer zdrlx) {
		this.zdrlx = zdrlx;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getVprintcode() {
		return vprintcode;
	}

	public void setVprintcode(String vprintcode) {
		this.vprintcode = vprintcode;
	}

	public DZFDouble getVfontsize() {
		return vfontsize;
	}

	public void setVfontsize(DZFDouble vfontsize) {
		this.vfontsize = vfontsize;
	}

	public DZFDate getDprintdate() {
		return dprintdate;
	}

	public void setDprintdate(DZFDate dprintdate) {
		this.dprintdate = dprintdate;
	}

	public DZFDouble getDleftmargin() {
		return dleftmargin;
	}

	public void setDleftmargin(DZFDouble dleftmargin) {
		this.dleftmargin = dleftmargin;
	}

	public DZFDouble getDtopmargin() {
		return dtopmargin;
	}

	public void setDtopmargin(DZFDouble dtopmargin) {
		this.dtopmargin = dtopmargin;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getPk_batch_print_set() {
		return pk_batch_print_set;
	}

	public void setPk_batch_print_set(String pk_batch_print_set) {
		this.pk_batch_print_set = pk_batch_print_set;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVprintperiod() {
		return vprintperiod;
	}

	public void setVprintperiod(String vprintperiod) {
		this.vprintperiod = vprintperiod;
	}

	public String getVfilename() {
		return vfilename;
	}

	public void setVfilename(String vfilename) {
		this.vfilename = vfilename;
	}

	public String getVfilepath() {
		return vfilepath;
	}

	public void setVfilepath(String vfilepath) {
		this.vfilepath = vfilepath;
	}

	public Integer getIfilestatue() {
		return ifilestatue;
	}

	public void setIfilestatue(Integer ifilestatue) {
		this.ifilestatue = ifilestatue;
	}

	public String getVprintname() {
		return vprintname;
	}

	public void setVprintname(String vprintname) {
		this.vprintname = vprintname;
	}

	public DZFDateTime getDoperadatetime() {
		return doperadatetime;
	}

	public void setDoperadatetime(DZFDateTime doperadatetime) {
		this.doperadatetime = doperadatetime;
	}

	public String getVoperateid() {
		return voperateid;
	}

	public void setVoperateid(String voperateid) {
		this.voperateid = voperateid;
	}

	public DZFDateTime getDgendatetime() {
		return dgendatetime;
	}

	public void setDgendatetime(DZFDateTime dgendatetime) {
		this.dgendatetime = dgendatetime;
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

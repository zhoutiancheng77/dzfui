package com.dzf.zxkj.platform.model.tax;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

/**
 * 期初数据存储 存放一键报税接口期初，后续其他地区也可以使用
 * @author wangzhn
 *
 */
public class TaxReportNewQcInitVO extends SuperVO {
	
	private String pk_taxreportnewqcinit;
	@JsonProperty("corpid")
	private String pk_corp;//公司
	private String sb_zlbh;//申报种类编号
	private String period;//期间
	private Integer periodtype;//月报  季报  年报
	private String spreadfile;//存储文件
	private String pk_taxsbzl;
	private String coperatorid;//操作人
	private DZFDate doperatedate;//操作时间
	private DZFDateTime ts;
	private Integer dr;
	
	
	//供接受参数使用
	private Map<String, String> jdsdsQc;//季度所得税
	private Map<String, String> xgmzzsQc;//小规模增值税
	private Map<String, Object> ybzzsQc;//一般增值税
	private Map<String, String> xgmdzdsQc;//附加税—小规模
	private Map<String, String> ybrdzdsQc;//附加税—一般人
	
	private Map<String, String> xgmzzsybQc;//小规模增值税月报
	private Map<String, String> xgmdzdsybQc;//小规模附加税月报
	private Map<String, String> whsyjsQc;//文化事业建设费季报
	private Map<String, String> whsyjsybQc;//文化事业建设费月报
	
	private TaxReportQcSubVO yhsQc;// 印花税
	private TaxReportQcSubVO dfjfQc;// 地方各项基金费
	private TaxReportQcSubVO fjsQc;// 附加税
	
	
	public String getPk_taxreportnewqcinit() {
		return pk_taxreportnewqcinit;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getSb_zlbh() {
		return sb_zlbh;
	}

	public String getPeriod() {
		return period;
	}

	public Integer getPeriodtype() {
		return periodtype;
	}

	public String getSpreadfile() {
		return spreadfile;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setPk_taxreportnewqcinit(String pk_taxreportnewqcinit) {
		this.pk_taxreportnewqcinit = pk_taxreportnewqcinit;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setSb_zlbh(String sb_zlbh) {
		this.sb_zlbh = sb_zlbh;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public void setPeriodtype(Integer periodtype) {
		this.periodtype = periodtype;
	}

	public void setSpreadfile(String spreadfile) {
		this.spreadfile = spreadfile;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public Map<String, String> getJdsdsQc() {
		return jdsdsQc;
	}

	public Map<String, String> getXgmzzsQc() {
		return xgmzzsQc;
	}

	public Map<String, Object> getYbzzsQc() {
		return ybzzsQc;
	}

	public void setJdsdsQc(Map<String, String> jdsdsQc) {
		this.jdsdsQc = jdsdsQc;
	}

	public void setXgmzzsQc(Map<String, String> xgmzzsQc) {
		this.xgmzzsQc = xgmzzsQc;
	}

	public void setYbzzsQc(Map<String, Object> ybzzsQc) {
		this.ybzzsQc = ybzzsQc;
	}
	
	public Map<String, String> getXgmdzdsQc() {
		return xgmdzdsQc;
	}

	public Map<String, String> getYbrdzdsQc() {
		return ybrdzdsQc;
	}

	public void setXgmdzdsQc(Map<String, String> xgmdzdsQc) {
		this.xgmdzdsQc = xgmdzdsQc;
	}

	public void setYbrdzdsQc(Map<String, String> ybrdzdsQc) {
		this.ybrdzdsQc = ybrdzdsQc;
	}

	public Map<String, String> getXgmzzsybQc() {
		return xgmzzsybQc;
	}

	public Map<String, String> getXgmdzdsybQc() {
		return xgmdzdsybQc;
	}

	public Map<String, String> getWhsyjsQc() {
		return whsyjsQc;
	}

	public Map<String, String> getWhsyjsybQc() {
		return whsyjsybQc;
	}

	public void setXgmzzsybQc(Map<String, String> xgmzzsybQc) {
		this.xgmzzsybQc = xgmzzsybQc;
	}

	public void setXgmdzdsybQc(Map<String, String> xgmdzdsybQc) {
		this.xgmdzdsybQc = xgmdzdsybQc;
	}

	public void setWhsyjsQc(Map<String, String> whsyjsQc) {
		this.whsyjsQc = whsyjsQc;
	}

	public void setWhsyjsybQc(Map<String, String> whsyjsybQc) {
		this.whsyjsybQc = whsyjsybQc;
	}


	public TaxReportQcSubVO getYhsQc() {
		return yhsQc;
	}

	public void setYhsQc(TaxReportQcSubVO yhsQc) {
		this.yhsQc = yhsQc;
	}

	public TaxReportQcSubVO getDfjfQc() {
		return dfjfQc;
	}

	public void setDfjfQc(TaxReportQcSubVO dfjfQc) {
		this.dfjfQc = dfjfQc;
	}

	public TaxReportQcSubVO getFjsQc() {
		return fjsQc;
	}

	public void setFjsQc(TaxReportQcSubVO fjsQc) {
		this.fjsQc = fjsQc;
	}

	public String getPk_taxsbzl() {
		return pk_taxsbzl;
	}

	public void setPk_taxsbzl(String pk_taxsbzl) {
		this.pk_taxsbzl = pk_taxsbzl;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_taxreportnewqcinit";
	}

	@Override
	public String getTableName() {
		return "ynt_taxreportnewqcinit";
	}

}

package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 类别设置
 * @author mfz
 *
 */

@SuppressWarnings({ "serial", "rawtypes" })
public class CategorysetVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_categoryset;//主键
	@JsonProperty("corpid")
	private String pk_corp;	
	private Integer dr;
	private DZFDateTime ts;
	@JsonProperty("baseid")
	private String pk_basecategory;//基础票据类别主键
	@JsonProperty("categoryid")
	private String pk_category;//票据类别主键
	@JsonProperty("jsfs")
	private Integer settlement;//结算方式
	@JsonProperty("hbfs")
	private Integer mergemode;//合并方式
	@JsonProperty("zjnx")
	private Integer depreciationmonth;//折旧年限(月数)
	@JsonProperty("czl")
	private DZFDouble salvagerate;//残值率
	@JsonProperty("rzkm")
	private String pk_accsubj;//入账科目
	private String rzkmname;
	@JsonProperty("jskm")
	private String pk_settlementaccsubj;//结算科目
	private String jskmname;
	private String zdyzy;//自定义摘要
	@JsonProperty("oid")
	private String coperatorid;//操作人
	@JsonProperty("odate")
	private DZFDate doperatedate;//操作日期

	private String categoryname;//分类名称(修改分类名称用)
	@JsonProperty("defzy")
	private String defaultZy;//这个编辑目录所对应的默认摘要(在分类入账规则上)
	@JsonProperty("shkm")
	private String pk_taxaccsubj;//税行科目
	private String shkmname;
	
	public String getShkmname() {
		return shkmname;
	}

	public void setShkmname(String shkmname) {
		this.shkmname = shkmname;
	}

	public String getPk_taxaccsubj() {
		return pk_taxaccsubj;
	}

	public void setPk_taxaccsubj(String pk_taxaccsubj) {
		this.pk_taxaccsubj = pk_taxaccsubj;
	}

	public String getRzkmname() {
		return rzkmname;
	}

	public void setRzkmname(String rzkmname) {
		this.rzkmname = rzkmname;
	}

	public String getJskmname() {
		return jskmname;
	}

	public void setJskmname(String jskmname) {
		this.jskmname = jskmname;
	}

	public String getDefaultZy() {
		return defaultZy;
	}

	public void setDefaultZy(String defaultZy) {
		this.defaultZy = defaultZy;
	}

	public String getZdyzy() {
		return zdyzy;
	}

	public void setZdyzy(String zdyzy) {
		this.zdyzy = zdyzy;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public String getPk_categoryset() {
		return pk_categoryset;
	}

	public void setPk_categoryset(String pk_categoryset) {
		this.pk_categoryset = pk_categoryset;
	}

	public String getPk_category() {
		return pk_category;
	}

	public void setPk_category(String pk_category) {
		this.pk_category = pk_category;
	}

	public Integer getSettlement() {
		return settlement;
	}

	public void setSettlement(Integer settlement) {
		this.settlement = settlement;
	}

	public Integer getMergemode() {
		return mergemode;
	}

	public void setMergemode(Integer mergemode) {
		this.mergemode = mergemode;
	}

	public Integer getDepreciationmonth() {
		return depreciationmonth;
	}

	public void setDepreciationmonth(Integer depreciationmonth) {
		this.depreciationmonth = depreciationmonth;
	}

	public DZFDouble getSalvagerate() {
		return salvagerate;
	}

	public void setSalvagerate(DZFDouble salvagerate) {
		this.salvagerate = salvagerate;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public String getPk_settlementaccsubj() {
		return pk_settlementaccsubj;
	}

	public void setPk_settlementaccsubj(String pk_settlementaccsubj) {
		this.pk_settlementaccsubj = pk_settlementaccsubj;
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

	public String getPk_basecategory() {
		return pk_basecategory;
	}

	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}


	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	@Override
	public String getPKFieldName() {
		return "pk_categoryset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_categoryset";
	}
	
	
}

package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 票据类别分类规则表
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class CategoryKeywordVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_category_keyword;//主键
	@JsonProperty("corpid")
	private String pk_corp;	
	private Integer dr;
	private DZFDateTime ts;
	@JsonProperty("baseid")
	private String pk_basecategory;//基础票据类别主键
	@JsonProperty("categoryid")
	private String pk_category;//票据类别主键
	@JsonProperty("baseoid")
	private String pk_basecategory_ori;//原始预制的基础票据类别主键
	@JsonProperty("bs")
	private Integer billsource;//数据来源
	@JsonProperty("ac")
	private Integer aicategory;//票据类别
	@JsonProperty("bt")
	private String aibilltype;//单据类型
	private String pk_trade; //行业主键
	private String hyname; //行业名称
	@JsonProperty("in")
	private Integer inflag;//收方判断标志
	@JsonProperty("out")
	private Integer outflag;//付方判断标志
	@JsonProperty("words")
	private String pk_keywords;//关键字主键数组
	@JsonProperty("yxj")
	private Integer priority;//优先级
	@JsonProperty("oid")
	private String coperatorid;//操作人
	@JsonProperty("odate")
	private DZFDate doperatedate;//操作日期
	@JsonProperty("mid")
	private String cmodifyid;//修改人
	@JsonProperty("mdate")
	private DZFDate dmodifydate;//修改日期
	@JsonProperty("isenable")
	private DZFBoolean useflag;//是否启用
	@JsonProperty("eid")
	private String cenableid;//启用人
	@JsonProperty("edate")
	private DZFDate denabledate;//启用日期
	@JsonProperty("nt")
	private String note;//说明
	
	private String catalogname;//分类的组合名称
	@JsonProperty("oname")
	private String coperatorname;//操作人名称
	@JsonProperty("mname")
	private String cmodifyname;//修改人名称
	@JsonProperty("ename")
	private String cenablename;//启用人名称
	@JsonProperty("wnames")
	private String keywordnames;//关键字名称
	
	private String categorycode;//分类编码
	@JsonProperty("id_ori")
	private String pk_category_keyword_ori;//新学习记录对应的原始来源主键
	
	
	
	
	
	public String getHyname() {
		return hyname;
	}

	public void setHyname(String hyname) {
		this.hyname = hyname;
	}

	public String getPk_trade() {
		return pk_trade;
	}

	public void setPk_trade(String pk_trade) {
		this.pk_trade = pk_trade;
	}

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public String getCatalogname() {
		return catalogname;
	}

	public void setCatalogname(String catalogname) {
		this.catalogname = catalogname;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public String getKeywordnames() {
		return keywordnames;
	}

	public void setKeywordnames(String keywordnames) {
		this.keywordnames = keywordnames;
	}

	public String getCoperatorname() {
		return coperatorname;
	}

	public void setCoperatorname(String coperatorname) {
		this.coperatorname = coperatorname;
	}

	public String getCenablename() {
		return cenablename;
	}

	public void setCenablename(String cenablename) {
		this.cenablename = cenablename;
	}

	public DZFBoolean getUseflag() {
		return useflag;
	}

	public void setUseflag(DZFBoolean useflag) {
		this.useflag = useflag;
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

	public String getCenableid() {
		return cenableid;
	}

	public void setCenableid(String cenableid) {
		this.cenableid = cenableid;
	}

	public DZFDate getDenabledate() {
		return denabledate;
	}

	public void setDenabledate(DZFDate denabledate) {
		this.denabledate = denabledate;
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

	public String getPk_category_keyword() {
		return pk_category_keyword;
	}

	public void setPk_category_keyword(String pk_category_keyword) {
		this.pk_category_keyword = pk_category_keyword;
	}

	public String getPk_basecategory() {
		return pk_basecategory;
	}

	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}

	public String getPk_category() {
		return pk_category;
	}

	public void setPk_category(String pk_category) {
		this.pk_category = pk_category;
	}

	public String getPk_basecategory_ori() {
		return pk_basecategory_ori;
	}

	public void setPk_basecategory_ori(String pk_basecategory_ori) {
		this.pk_basecategory_ori = pk_basecategory_ori;
	}

	public Integer getBillsource() {
		return billsource;
	}

	public void setBillsource(Integer billsource) {
		this.billsource = billsource;
	}

	public String getAibilltype() {
		return aibilltype;
	}

	public void setAibilltype(String aibilltype) {
		this.aibilltype = aibilltype;
	}

	public Integer getAicategory() {
		return aicategory;
	}

	public void setAicategory(Integer aicategory) {
		this.aicategory = aicategory;
	}

	public Integer getInflag() {
		return inflag;
	}

	public void setInflag(Integer inflag) {
		this.inflag = inflag;
	}

	public Integer getOutflag() {
		return outflag;
	}

	public void setOutflag(Integer outflag) {
		this.outflag = outflag;
	}

	public String getPk_keywords() {
		return pk_keywords;
	}

	public void setPk_keywords(String pk_keywords) {
		this.pk_keywords = pk_keywords;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
	}

	public String getCmodifyid() {
		return cmodifyid;
	}

	public void setCmodifyid(String cmodifyid) {
		this.cmodifyid = cmodifyid;
	}

	public DZFDate getDmodifydate() {
		return dmodifydate;
	}

	public void setDmodifydate(DZFDate dmodifydate) {
		this.dmodifydate = dmodifydate;
	}

	public String getCmodifyname() {
		return cmodifyname;
	}

	public void setCmodifyname(String cmodifyname) {
		this.cmodifyname = cmodifyname;
	}

	@Override
	public String getPKFieldName() {
		return "pk_category_keyword";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_category_keyword";
	}

	public String getPk_category_keyword_ori() {
		return pk_category_keyword_ori;
	}

	public void setPk_category_keyword_ori(String pk_category_keyword_ori) {
		this.pk_category_keyword_ori = pk_category_keyword_ori;
	}
	
	
}

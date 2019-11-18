package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 基础票据类别
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class BaseCategoryVO extends SuperVO {
	@JsonProperty("id")
	private String pk_basecategory; //主键
	
	private String pk_corp; //公司主键
	
	private DZFBoolean useflag; //是否启用
	@JsonProperty("_parentId")
	private String pk_parentbasecategory; //父节点主键
	@JsonProperty("kmtx")
	private String pk_accountschema;//科目体系
	@JsonProperty("ctlv")
	private Integer  categorylevel; //类别级次
	
	private DZFBoolean isleaf; //是否末级
	
	private Integer showorder ; //顺序号
	@JsonProperty("ccode")
	private String categorycode; //类别编码 
	@JsonProperty("cname")
	private String categoryname;  //类别名称
	@JsonProperty("desc")
	private String description;//描述
	
	private DZFBoolean allowchild; //是否 允许创建下级
	
	private Integer settype;//设置类型
	
	private Integer categorytype; //节点类型
	
	private String catalogname;//组合名称   ps:银行-转出
	
	private String coperatorid;//创建人
	
	private DZFDate doperatedate; //创建时间
	
	private String cenableid;// 启用人
	
	private DZFDate denabledate; //启用时间
	
	private Integer dr;//是否删除 0不删除  1删除
	
	private Integer childlevel;//允许建下级的层次数量
	
	private DZFDateTime ts;
	
	
	private String parentname;//父节点名称
	
	private String kmtxname;//科目体系名称
	
	private String state;
	private Integer inoutflag;//收支类型 0:无方向1：收入(销售)2: 支出(采购
	
	
	
	
	
	
	public Integer getInoutflag() {
		return inoutflag;
	}

	public void setInoutflag(Integer inoutflag) {
		this.inoutflag = inoutflag;
	}

	public String getKmtxname() {
		return kmtxname;
	}

	public void setKmtxname(String kmtxname) {
		this.kmtxname = kmtxname;
	}

	public String getPk_accountschema() {
		return pk_accountschema;
	}

	public void setPk_accountschema(String pk_accountschema) {
		this.pk_accountschema = pk_accountschema;
	}

	public Integer getChildlevel() {
		return childlevel;
	}

	public void setChildlevel(Integer childlevel) {
		this.childlevel = childlevel;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getParentname() {
		return parentname;
	}

	public void setParentname(String parentname) {
		this.parentname = parentname;
	}

	public String getPk_basecategory() {
		return pk_basecategory;
	}

	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFBoolean getUseflag() {
		return useflag;
	}

	public void setUseflag(DZFBoolean useflag) {
		this.useflag = useflag;
	}

	public String getPk_parentbasecategory() {
		return pk_parentbasecategory;
	}

	public void setPk_parentbasecategory(String pk_parentbasecategory) {
		this.pk_parentbasecategory = pk_parentbasecategory;
	}

	public Integer getCategorylevel() {
		return categorylevel;
	}

	public void setCategorylevel(Integer categorylevel) {
		this.categorylevel = categorylevel;
	}

	public DZFBoolean getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(DZFBoolean isleaf) {
		this.isleaf = isleaf;
	}

	public Integer getShoworder() {
		return showorder;
	}

	public void setShoworder(Integer showorder) {
		this.showorder = showorder;
	}

	public String getCategorycode() {
		return categorycode;
	}

	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}

	public String getCategoryname() {
		return categoryname;
	}

	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public DZFBoolean getAllowchild() {
		return allowchild;
	}

	public void setAllowchild(DZFBoolean allowchild) {
		this.allowchild = allowchild;
	}

	public Integer getSettype() {
		return settype;
	}

	public void setSettype(Integer settype) {
		this.settype = settype;
	}

	public Integer getCategorytype() {
		return categorytype;
	}

	public void setCategorytype(Integer categorytype) {
		this.categorytype = categorytype;
	}

	public String getCatalogname() {
		return catalogname;
	}

	public void setCatalogname(String catalogname) {
		this.catalogname = catalogname;
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
		
		return "pk_basecategory";
	}

	@Override
	public String getParentPKFieldName() {
		
		return "pk_parentbasecategory";
	}

	@Override
	public String getTableName() {
		
		return "ynt_basecategory";
	}

}

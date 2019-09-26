package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

/*
 * 公司票据类别
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class BillCategoryVO extends SuperVO {
	@JsonProperty("id")
	private String pk_category; //主键
	
	private String pk_corp; //公司主键
	
	private String pk_basecategory;//基础票据类别主键
	
	private String period;//期间
	
	private String pk_parentcategory;//父级节点主键
	
	private Integer categorylevel; //类别级次  1,2,3级等，2级及以下级类别层级允许增加自定义类别
	
	private DZFBoolean isleaf; //是否末级 Y：末级 N:非末级
	
	private Integer showorder;//顺序号
	
	private String categorycode; //类别编码 用户新增类别节点，编号递增
	
	private String categoryname; //类别名称
	
	private Integer settype;//设置类型
	
	private DZFBoolean allowchild;//是否允许创建下级
	private Integer childlevel;//允许建下级的层次数量
	private Integer szstylename;//结算方式
	@JsonProperty("rzkm")
	private String pk_accsubj;//入账科目
	@JsonProperty("jskm")
	private String pk_settlementaccsubj;//结算科目
	private String zdyzy;//自定义摘要
	private String description; //描述  说明
	
	private Integer categorytype;//节点类型0：基础票据类别节点1：自定义目录（是系统预制目录）下的自定义类别（这种类别跨期间生效）2：用户在非自定义目录中新增的自定义类别（这种类别只在当期有效）3：销项数据按税率分类目录4：销项数据按客户目录5：银行票据按账户分类目录
	
	private Integer dr;//是否删除 0不删除  1删除
	
	private DZFDateTime ts;
	
	private Integer itype;//0的时候是分类 1的时候是票
	private Integer billcount;//这个分类下有几张票据
	private String billtitle;//票据显示的名称
	
	private DZFBoolean isaccount;//是否做账
	
	private String ntotaltax;//价税合计
	private String nmny;//金额合计
	private String ntaxnmny;//税额合计
	private String taxrate;//税额
	private String errordesc;//错误描述
	@JsonProperty("groupid")
	private String pk_image_group;//图片组ID
	private Integer inoutflag;//收支类型 0:无方向1：收入(销售)2: 支出(采购
	private Integer rowcount;//摘要行数
	
	private Integer errorcount;//错误数量
	
	private String pk_image_library;
	private String istate;
	private String vpurchname;// 购方企业名称
	private String vsalename;// 销方企业名称
	private String dinvoicedate;//开票日期
	
	private String fullcategoryname;	//类别全名称
	
	private DZFBoolean iseditacc;//是否可以编辑3个科目字段
	
	private String pk_bankcode;//点中银行票下的4级及以后有用，记录点的是哪个银行账号下的分类
	
	public String getPk_bankcode() {
		return pk_bankcode;
	}

	public void setPk_bankcode(String pk_bankcode) {
		this.pk_bankcode = pk_bankcode;
	}

	public DZFBoolean getIseditacc() {
		return iseditacc;
	}

	public void setIseditacc(DZFBoolean iseditacc) {
		this.iseditacc = iseditacc;
	}

	public String getIstate() {
		return istate;
	}

	public void setIstate(String istate) {
		this.istate = istate;
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

	public String getZdyzy() {
		return zdyzy;
	}

	public void setZdyzy(String zdyzy) {
		this.zdyzy = zdyzy;
	}

	public String getFullcategoryname() {
		return fullcategoryname;
	}

	public void setFullcategoryname(String fullcategoryname) {
		this.fullcategoryname = fullcategoryname;
	}

	public String getVpurchname() {
		return vpurchname;
	}

	public void setVpurchname(String vpurchname) {
		this.vpurchname = vpurchname;
	}

	public String getVsalename() {
		return vsalename;
	}

	public void setVsalename(String vsalename) {
		this.vsalename = vsalename;
	}

	public String getDinvoicedate() {
		return dinvoicedate;
	}

	public void setDinvoicedate(String dinvoicedate) {
		this.dinvoicedate = dinvoicedate;
	}

	public String getPk_image_library() {
		return pk_image_library;
	}

	public void setPk_image_library(String pk_image_library) {
		this.pk_image_library = pk_image_library;
	}

	public Integer getSzstylename() {
		return szstylename;
	}

	public void setSzstylename(Integer szstylename) {
		this.szstylename = szstylename;
	}

	public Integer getErrorcount() {
		return errorcount;
	}

	public void setErrorcount(Integer errorcount) {
		this.errorcount = errorcount;
	}

	public Integer getRowcount() {
		return rowcount;
	}

	public void setRowcount(Integer rowcount) {
		this.rowcount = rowcount;
	}

	public Integer getInoutflag() {
		return inoutflag;
	}

	public void setInoutflag(Integer inoutflag) {
		this.inoutflag = inoutflag;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}
	public String getErrordesc() {
		return errordesc;
	}

	public void setErrordesc(String errordesc) {
		this.errordesc = errordesc;
	}

	public String getNtotaltax() {
		return ntotaltax;
	}

	public void setNtotaltax(String ntotaltax) {
		this.ntotaltax = ntotaltax;
	}

	public String getNmny() {
		return nmny;
	}

	public void setNmny(String nmny) {
		this.nmny = nmny;
	}

	public String getNtaxnmny() {
		return ntaxnmny;
	}

	public void setNtaxnmny(String ntaxnmny) {
		this.ntaxnmny = ntaxnmny;
	}

	public String getTaxrate() {
		return taxrate;
	}

	public void setTaxrate(String taxrate) {
		this.taxrate = taxrate;
	}

	public DZFBoolean getIsaccount() {
		return isaccount;
	}

	public void setIsaccount(DZFBoolean isaccount) {
		this.isaccount = isaccount;
	}
	public DZFBoolean getAllowchild() {
		return allowchild;
	}

	public void setAllowchild(DZFBoolean allowchild) {
		this.allowchild = allowchild;
	}

	public Integer getChildlevel() {
		return childlevel;
	}

	public void setChildlevel(Integer childlevel) {
		this.childlevel = childlevel;
	}

	public Integer getSettype() {
		return settype;
	}

	public void setSettype(Integer settype) {
		this.settype = settype;
	}

	public Integer getItype() {
		return itype;
	}

	public void setItype(Integer itype) {
		this.itype = itype;
	}

	public String getBilltitle() {
		return billtitle;
	}

	public void setBilltitle(String billtitle) {
		this.billtitle = billtitle;
	}

	public Integer getBillcount() {
		return billcount;
	}

	public void setBillcount(Integer billcount) {
		this.billcount = billcount;
	}

	public String getPk_category() {
		return pk_category;
	}

	public void setPk_category(String pk_category) {
		this.pk_category = pk_category;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_basecategory() {
		return pk_basecategory;
	}

	public void setPk_basecategory(String pk_basecategory) {
		this.pk_basecategory = pk_basecategory;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_parentcategory() {
		return pk_parentcategory;
	}

	public void setPk_parentcategory(String pk_parentcategory) {
		this.pk_parentcategory = pk_parentcategory;
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

	public Integer getCategorytype() {
		return categorytype;
	}

	public void setCategorytype(Integer categorytype) {
		this.categorytype = categorytype;
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
		
		return "pk_category";
	}

	@Override
	public String getParentPKFieldName() {
		
		return "pk_parentbasecategory";
	}

	@Override
	public String getTableName() {
		
		return "ynt_billcategory";
	}

}

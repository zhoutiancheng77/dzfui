package com.dzf.zxkj.platform.model.zncs;

import java.util.List;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 统计分析  票据分类情况
 * @author ry
 *
 */
public class InvoiceinfoVO extends SuperVO {
	
	private String pk_invoice;//票据主键
	private String pk_billcategory;//所在类别主键
	private String categorycode;//类别编码
	private String categoryname;//类别名称
	private String cbilltype;//出入库标志HP70入库HP75出库
	private DZFBoolean isaccount;//是否做账
	private String pk_assetcard;//资产卡片
	public String getPk_invoice() {
		return pk_invoice;
	}
	public void setPk_invoice(String pk_invoice) {
		this.pk_invoice = pk_invoice;
	}
	public String getPk_billcategory() {
		return pk_billcategory;
	}
	public void setPk_billcategory(String pk_billcategory) {
		this.pk_billcategory = pk_billcategory;
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
	public String getCbilltype() {
		return cbilltype;
	}
	public void setCbilltype(String cbilltype) {
		this.cbilltype = cbilltype;
	}
	public DZFBoolean getIsaccount() {
		return isaccount;
	}
	public void setIsaccount(DZFBoolean isaccount) {
		this.isaccount = isaccount;
	}
	public String getPk_assetcard() {
		return pk_assetcard;
	}
	public void setPk_assetcard(String pk_assetcard) {
		this.pk_assetcard = pk_assetcard;
	}
	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	
	
	

}

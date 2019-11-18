package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 分类统计分析
 * @author ry
 *
 */
public class CategoryCountVO extends SuperVO {
	
	private String categoryname;//分类
	private String categorycode;//分类编码
	private Integer zsl;//票据总数量
	private Integer ckd;//出库单
	private Integer rkd;//入库单
	private Integer zckp;//资产卡片
	private Integer yzz;//已做账
	private Integer wzz;//未做账
	
	
	
	
	
	
	
	
	public String getCategoryname() {
		return categoryname;
	}
	public void setCategoryname(String categoryname) {
		this.categoryname = categoryname;
	}
	public String getCategorycode() {
		return categorycode;
	}
	public void setCategorycode(String categorycode) {
		this.categorycode = categorycode;
	}
	public Integer getZsl() {
		return zsl;
	}
	public void setZsl(Integer zsl) {
		this.zsl = zsl;
	}
	public Integer getCkd() {
		return ckd;
	}
	public void setCkd(Integer ckd) {
		this.ckd = ckd;
	}
	public Integer getRkd() {
		return rkd;
	}
	public void setRkd(Integer rkd) {
		this.rkd = rkd;
	}
	public Integer getZckp() {
		return zckp;
	}
	public void setZckp(Integer zckp) {
		this.zckp = zckp;
	}
	public Integer getYzz() {
		return yzz;
	}
	public void setYzz(Integer yzz) {
		this.yzz = yzz;
	}
	public Integer getWzz() {
		return wzz;
	}
	public void setWzz(Integer wzz) {
		this.wzz = wzz;
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

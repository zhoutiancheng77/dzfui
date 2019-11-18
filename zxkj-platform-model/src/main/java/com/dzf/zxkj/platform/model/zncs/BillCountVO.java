package com.dzf.zxkj.platform.model.zncs;

import java.util.List;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 统计分析
 * @author ry
 *
 */
public class BillCountVO extends SuperVO {
	
	
	private Integer scpj;//上传票据
	private Integer kqzbq;//跨期至本期票据
	private Integer kqzqt;//跨期至其他期间票据
	private Integer pjzs;//票据总数
	private Integer ysb;//已识别
	private Integer wsb;//未识别
	private Integer yzz;//已做账
	private Integer wzz;//未做账
	private Integer yzf;//已作废
	private Integer cf;//重复
	private Integer fbgs;//非本公司票据
	private List<InvoiceCountVO> invoicelist; //进销项发票
	private List<CategoryCountVO> categorylist;//分类情况
	
	
	
	
	
	public Integer getScpj() {
		return scpj;
	}
	public void setScpj(Integer scpj) {
		this.scpj = scpj;
	}
	public Integer getKqzbq() {
		return kqzbq;
	}
	public void setKqzbq(Integer kqzbq) {
		this.kqzbq = kqzbq;
	}
	public Integer getKqzqt() {
		return kqzqt;
	}
	public void setKqzqt(Integer kqzqt) {
		this.kqzqt = kqzqt;
	}
	public Integer getPjzs() {
		return pjzs;
	}
	public void setPjzs(Integer pjzs) {
		this.pjzs = pjzs;
	}
	public Integer getYsb() {
		return ysb;
	}
	public void setYsb(Integer ysb) {
		this.ysb = ysb;
	}
	public Integer getWsb() {
		return wsb;
	}
	public void setWsb(Integer wsb) {
		this.wsb = wsb;
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
	public Integer getYzf() {
		return yzf;
	}
	public void setYzf(Integer yzf) {
		this.yzf = yzf;
	}
	public Integer getCf() {
		return cf;
	}
	public void setCf(Integer cf) {
		this.cf = cf;
	}
	public Integer getFbgs() {
		return fbgs;
	}
	public void setFbgs(Integer fbgs) {
		this.fbgs = fbgs;
	}
	public List<InvoiceCountVO> getInvoicelist() {
		return invoicelist;
	}
	public void setInvoicelist(List<InvoiceCountVO> invoicelist) {
		this.invoicelist = invoicelist;
	}
	public List<CategoryCountVO> getCategorylist() {
		return categorylist;
	}
	public void setCategorylist(List<CategoryCountVO> categorylist) {
		this.categorylist = categorylist;
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

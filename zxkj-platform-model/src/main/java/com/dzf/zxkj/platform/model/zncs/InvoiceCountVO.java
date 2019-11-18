package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 进销项统计分析
 * @author ry
 *
 */
public class InvoiceCountVO extends SuperVO {
	
	private String invoicetype;//发票类型
	private Integer zsl;//票据总数
	private DZFDouble je;//金额
	private DZFDouble se;//税额
	private DZFDouble jshj;//价税合计
	private Integer zp;//专票
	private Integer pp;//普票
	private Integer yqs;//已签收
	private Integer wqs;//未签收
	
	
	
	public String getInvoicetype() {
		return invoicetype;
	}
	public void setInvoicetype(String invoicetype) {
		this.invoicetype = invoicetype;
	}
	public Integer getZsl() {
		return zsl;
	}
	public void setZsl(Integer zsl) {
		this.zsl = zsl;
	}
	
	public DZFDouble getJe() {
		return je;
	}
	public void setJe(DZFDouble je) {
		this.je = je;
	}
	public DZFDouble getSe() {
		return se;
	}
	public void setSe(DZFDouble se) {
		this.se = se;
	}
	public DZFDouble getJshj() {
		return jshj;
	}
	public void setJshj(DZFDouble jshj) {
		this.jshj = jshj;
	}
	public Integer getZp() {
		return zp;
	}
	public void setZp(Integer zp) {
		this.zp = zp;
	}
	public Integer getPp() {
		return pp;
	}
	public void setPp(Integer pp) {
		this.pp = pp;
	}
	public Integer getYqs() {
		return yqs;
	}
	public void setYqs(Integer yqs) {
		this.yqs = yqs;
	}
	public Integer getWqs() {
		return wqs;
	}
	public void setWqs(Integer wqs) {
		this.wqs = wqs;
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

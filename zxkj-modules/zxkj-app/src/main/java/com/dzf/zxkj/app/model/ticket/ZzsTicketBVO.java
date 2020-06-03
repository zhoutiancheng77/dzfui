package com.dzf.zxkj.app.model.ticket;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * （1）增值税专用发票返回报文内层内容说明（发票类型:01） 明细信息
 * 
 * @author zhangj
 *
 */
public class ZzsTicketBVO extends SuperVO {
	
	public static final String TABLENAME = "app_zzstiket_b";
	public static final String PKFIELDNAME = "pk_zzstiket_b";

	private String pk_zzstiket_b;
	private String pk_zzstiket;
	private String hwmc;// 货物或应税劳务名称
	private String ggxh;// 规格型号
	private String dw;// 单位
	private String sl;// 数量
	private String dj;// 单价
	private String je;// 金额
	private String slv;// 税率
	private String se;// 税额
	private String jshj;//价税合计
	private DZFDateTime ts;
	private Integer dr;
	
	public String getJshj() {
		return jshj;
	}

	public void setJshj(String jshj) {
		this.jshj = jshj;
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

	public String getPk_zzstiket_b() {
		return pk_zzstiket_b;
	}

	public void setPk_zzstiket_b(String pk_zzstiket_b) {
		this.pk_zzstiket_b = pk_zzstiket_b;
	}

	public String getPk_zzstiket() {
		return pk_zzstiket;
	}

	public void setPk_zzstiket(String pk_zzstiket) {
		this.pk_zzstiket = pk_zzstiket;
	}

	public String getHwmc() {
		return hwmc;
	}

	public void setHwmc(String hwmc) {
		this.hwmc = hwmc;
	}

	public String getGgxh() {
		return ggxh;
	}

	public void setGgxh(String ggxh) {
		this.ggxh = ggxh;
	}

	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public String getSl() {
		return sl;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public String getDj() {
		return dj;
	}

	public void setDj(String dj) {
		this.dj = dj;
	}

	public String getJe() {
		return je;
	}

	public void setJe(String je) {
		this.je = je;
	}

	public String getSlv() {
		return slv;
	}

	public void setSlv(String slv) {
		this.slv = slv;
	}

	public String getSe() {
		return se;
	}

	public void setSe(String se) {
		this.se = se;
	}

	@Override
	public String getPKFieldName() {
		return PKFIELDNAME;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_zzstiket";
	}

	@Override
	public String getTableName() {
		return TABLENAME;
	}

}

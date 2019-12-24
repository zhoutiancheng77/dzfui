package com.dzf.zxkj.platform.model.tax.cqtc;

import com.dzf.zxkj.common.lang.DZFBoolean;

public class CqtcRequestVO {

	public String taxno;
	public String taxname;
	public String fr;//法人
	public String hy;//所属行业
	public String zcdz;//注册地址
	public String nsrzg;//纳税人资格
	public String kjzd;//会计制度
	public String dh;//电话
	public String kysj;//开业设立时间
	public String yylx;//营业类型
	public String zzsyjb;//增值税月季申报方式
	public String sdszs;//所得税征收方式100A,400B
	public String sdsyjb;//所得税月季申报方式
	public String jmdm;//减免性质代码集 （减免性质代码#税务事项代码，以逗号分隔，查无记录时为空串）
	public String zfjglx;//汇总(合并)企业类别：1-非汇总（合并）企业；2-总机构；3-分支机构；4预缴50%；5只申报不缴
	public String message_zzs0;
	public String message_zzs1;
	public String message_sds0;
	public String message_sds1;
	public String error_message;
	
	public DZFBoolean zzs0;
	public DZFBoolean zzs1;
	public DZFBoolean sds0;
	public DZFBoolean sds1;
	
	private String djxh;//登记序号
	
	
	public String getMessage_sds1() {
		return message_sds1;
	}
	public void setMessage_sds1(String message_sds1) {
		this.message_sds1 = message_sds1;
	}
	public DZFBoolean getSds1() {
		return sds1;
	}
	public void setSds1(DZFBoolean sds1) {
		this.sds1 = sds1;
	}
	public DZFBoolean getZzs0() {
		return zzs0;
	}
	public void setZzs0(DZFBoolean zzs0) {
		this.zzs0 = zzs0;
	}
	public DZFBoolean getZzs1() {
		return zzs1;
	}
	public void setZzs1(DZFBoolean zzs1) {
		this.zzs1 = zzs1;
	}
	public DZFBoolean getSds0() {
		return sds0;
	}
	public void setSds0(DZFBoolean sds0) {
		this.sds0 = sds0;
	}
	
	public String getError_message() {
		return error_message;
	}
	public void setError_message(String error_message) {
		this.error_message = error_message;
	}
	public String getJmdm() {
		return jmdm;
	}
	public void setJmdm(String jmdm) {
		this.jmdm = jmdm;
	}
	public String getZfjglx() {
		return zfjglx;
	}
	public void setZfjglx(String zfjglx) {
		this.zfjglx = zfjglx;
	}
	public String getMessage_zzs0() {
		return message_zzs0;
	}
	public void setMessage_zzs0(String message_zzs0) {
		this.message_zzs0 = message_zzs0;
	}
	public String getMessage_zzs1() {
		return message_zzs1;
	}
	public void setMessage_zzs1(String message_zzs1) {
		this.message_zzs1 = message_zzs1;
	}
	public String getMessage_sds0() {
		return message_sds0;
	}
	public void setMessage_sds0(String message_sds0) {
		this.message_sds0 = message_sds0;
	}
	
	public String getTaxno() {
		return taxno;
	}
	public void setTaxno(String taxno) {
		this.taxno = taxno;
	}
	public String getTaxname() {
		return taxname;
	}
	public void setTaxname(String taxname) {
		this.taxname = taxname;
	}
	public String getFr() {
		return fr;
	}
	public void setFr(String fr) {
		this.fr = fr;
	}
	public String getHy() {
		return hy;
	}
	public void setHy(String hy) {
		this.hy = hy;
	}
	public String getZcdz() {
		return zcdz;
	}
	public void setZcdz(String zcdz) {
		this.zcdz = zcdz;
	}
	public String getNsrzg() {
		return nsrzg;
	}
	public void setNsrzg(String nsrzg) {
		this.nsrzg = nsrzg;
	}
	public String getKjzd() {
		return kjzd;
	}
	public void setKjzd(String kjzd) {
		this.kjzd = kjzd;
	}
	public String getDh() {
		return dh;
	}
	public void setDh(String dh) {
		this.dh = dh;
	}
	public String getKysj() {
		return kysj;
	}
	public void setKysj(String kysj) {
		this.kysj = kysj;
	}
	public String getYylx() {
		return yylx;
	}
	public void setYylx(String yylx) {
		this.yylx = yylx;
	}
	public String getZzsyjb() {
		return zzsyjb;
	}
	public void setZzsyjb(String zzsyjb) {
		this.zzsyjb = zzsyjb;
	}
	public String getSdszs() {
		return sdszs;
	}
	public void setSdszs(String sdszs) {
		this.sdszs = sdszs;
	}
	public String getSdsyjb() {
		return sdsyjb;
	}
	public void setSdsyjb(String sdsyjb) {
		this.sdsyjb = sdsyjb;
	}
	public String getDjxh() {
		return djxh;
	}
	public void setDjxh(String djxh) {
		this.djxh = djxh;
	}
	
	
}

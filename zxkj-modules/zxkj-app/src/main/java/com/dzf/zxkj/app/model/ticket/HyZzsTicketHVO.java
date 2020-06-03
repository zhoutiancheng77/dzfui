package com.dzf.zxkj.app.model.ticket;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * （2）货运运输业增值税专用发票返回报文内层内容说明（发票类型:02）
 * 
 * @author zhangj
 *
 */
public class HyZzsTicketHVO extends SuperVO {

	private String fpdm;// 发票代码
	private String fphm;// 发票号码
	private String cycs;// 查验次数
	private String kprq;// 开票日期
	private String cyrmc;// 承运人名称
	private String cyrsbh;// 承运人识别号
	private String spfmc;// 受票方名称
	private String spfsbh;// 受票方识别号
	private String shrmc;// 收货人名称
	private String shrsbh;// 收货人识别号
	private String fhrmc;// 发货人名称
	private String fhrsbh;// 发货人识别号
	private String hjje;// 合计金额
	private String yshwxx;// 运输货物信息
	private String qyd;// 起运地、经由、到达地
	private String slv;// 税率
	private String se;// 税额
	private String skph;// 税控盘号
	private String jshj;// 价税合计
	private String czch;// 车种车号
	private String ccdw;// 车船吨位
	private String swjg_dm;// 主管税务机关
	private String swjg_mc;// 主管税务名称
	private String zfbz;// 作废标记
	private String fpzl;//发票种类
	

	public String getFpzl() {
		return fpzl;
	}

	public void setFpzl(String fpzl) {
		this.fpzl = fpzl;
	}

	public String getFpdm() {
		return fpdm;
	}

	public void setFpdm(String fpdm) {
		this.fpdm = fpdm;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public String getCycs() {
		return cycs;
	}

	public void setCycs(String cycs) {
		this.cycs = cycs;
	}

	public String getKprq() {
		return kprq;
	}

	public void setKprq(String kprq) {
		this.kprq = kprq;
	}

	public String getCyrmc() {
		return cyrmc;
	}

	public void setCyrmc(String cyrmc) {
		this.cyrmc = cyrmc;
	}

	public String getCyrsbh() {
		return cyrsbh;
	}

	public void setCyrsbh(String cyrsbh) {
		this.cyrsbh = cyrsbh;
	}

	public String getSpfmc() {
		return spfmc;
	}

	public void setSpfmc(String spfmc) {
		this.spfmc = spfmc;
	}

	public String getSpfsbh() {
		return spfsbh;
	}

	public void setSpfsbh(String spfsbh) {
		this.spfsbh = spfsbh;
	}

	public String getShrmc() {
		return shrmc;
	}

	public void setShrmc(String shrmc) {
		this.shrmc = shrmc;
	}

	public String getShrsbh() {
		return shrsbh;
	}

	public void setShrsbh(String shrsbh) {
		this.shrsbh = shrsbh;
	}

	public String getFhrmc() {
		return fhrmc;
	}

	public void setFhrmc(String fhrmc) {
		this.fhrmc = fhrmc;
	}

	public String getFhrsbh() {
		return fhrsbh;
	}

	public void setFhrsbh(String fhrsbh) {
		this.fhrsbh = fhrsbh;
	}

	public String getHjje() {
		return hjje;
	}

	public void setHjje(String hjje) {
		this.hjje = hjje;
	}

	public String getYshwxx() {
		return yshwxx;
	}

	public void setYshwxx(String yshwxx) {
		this.yshwxx = yshwxx;
	}

	public String getQyd() {
		return qyd;
	}

	public void setQyd(String qyd) {
		this.qyd = qyd;
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

	public String getSkph() {
		return skph;
	}

	public void setSkph(String skph) {
		this.skph = skph;
	}

	public String getJshj() {
		return jshj;
	}

	public void setJshj(String jshj) {
		this.jshj = jshj;
	}

	public String getCzch() {
		return czch;
	}

	public void setCzch(String czch) {
		this.czch = czch;
	}

	public String getCcdw() {
		return ccdw;
	}

	public void setCcdw(String ccdw) {
		this.ccdw = ccdw;
	}

	public String getSwjg_dm() {
		return swjg_dm;
	}

	public void setSwjg_dm(String swjg_dm) {
		this.swjg_dm = swjg_dm;
	}

	public String getSwjg_mc() {
		return swjg_mc;
	}

	public void setSwjg_mc(String swjg_mc) {
		this.swjg_mc = swjg_mc;
	}

	public String getZfbz() {
		return zfbz;
	}

	public void setZfbz(String zfbz) {
		this.zfbz = zfbz;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}

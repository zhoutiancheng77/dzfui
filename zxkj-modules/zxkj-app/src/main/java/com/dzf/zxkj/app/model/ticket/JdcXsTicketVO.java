package com.dzf.zxkj.app.model.ticket;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 机动车销售统一发票返回报文内层内容说明（发票类型:03）
 * 
 * @author zhangj
 *
 */
public class JdcXsTicketVO extends SuperVO {

	private String fpdm;// 发票代码
	private String fphm;// 发票号码
	private String cycs;// 查验次数
	private String kprq;// 开票日期
	private String skph;// 机器编码
	private String ghdw;// 购买方名称
	private String sfzhm;// 身份证号码/组织机构代码
	private String gfsbh;// 购方纳税人识别号
	private String cllx;// 车辆类型
	private String cpxh;// 厂牌型号
	private String cd;// 产地
	private String hgzs;// 合格证号
	private String bhsj;// 不含税价
	private String sjdh;// 商检单号
	private String fdjhm;// 发动机号
	private String cjhm;// 车辆识别代号/车架号码
	private String jkzmsh;// 进口证明书号
	private String xhdwmc;// 销货单位名称
	private String dh;// 电话
	private String nsrsbh;// 销方纳税人识别号
	private String zh;// 账号
	private String dz;// 地址
	private String khyh;// 开户银行
	private String slv;// 增值税税率或征收率
	private String se;// 增值税税额
	private String swjgdm;// 主管税务机关代码
	private String jshj;// 价税合计
	private String wspzh;// 完税凭证号码
	private String dw;// 吨位
	private String xcrs;// 限乘人数
	private String zfbz;// 作废标志
	private String swjgmc;// 主管税务机关名称
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

	public String getSkph() {
		return skph;
	}

	public void setSkph(String skph) {
		this.skph = skph;
	}

	public String getGhdw() {
		return ghdw;
	}

	public void setGhdw(String ghdw) {
		this.ghdw = ghdw;
	}

	public String getSfzhm() {
		return sfzhm;
	}

	public void setSfzhm(String sfzhm) {
		this.sfzhm = sfzhm;
	}

	public String getGfsbh() {
		return gfsbh;
	}

	public void setGfsbh(String gfsbh) {
		this.gfsbh = gfsbh;
	}

	public String getCllx() {
		return cllx;
	}

	public void setCllx(String cllx) {
		this.cllx = cllx;
	}

	public String getCpxh() {
		return cpxh;
	}

	public void setCpxh(String cpxh) {
		this.cpxh = cpxh;
	}

	public String getCd() {
		return cd;
	}

	public void setCd(String cd) {
		this.cd = cd;
	}

	public String getHgzs() {
		return hgzs;
	}

	public void setHgzs(String hgzs) {
		this.hgzs = hgzs;
	}

	public String getBhsj() {
		return bhsj;
	}

	public void setBhsj(String bhsj) {
		this.bhsj = bhsj;
	}

	public String getSjdh() {
		return sjdh;
	}

	public void setSjdh(String sjdh) {
		this.sjdh = sjdh;
	}

	public String getFdjhm() {
		return fdjhm;
	}

	public void setFdjhm(String fdjhm) {
		this.fdjhm = fdjhm;
	}

	public String getCjhm() {
		return cjhm;
	}

	public void setCjhm(String cjhm) {
		this.cjhm = cjhm;
	}

	public String getJkzmsh() {
		return jkzmsh;
	}

	public void setJkzmsh(String jkzmsh) {
		this.jkzmsh = jkzmsh;
	}

	public String getXhdwmc() {
		return xhdwmc;
	}

	public void setXhdwmc(String xhdwmc) {
		this.xhdwmc = xhdwmc;
	}

	public String getDh() {
		return dh;
	}

	public void setDh(String dh) {
		this.dh = dh;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public String getZh() {
		return zh;
	}

	public void setZh(String zh) {
		this.zh = zh;
	}

	public String getDz() {
		return dz;
	}

	public void setDz(String dz) {
		this.dz = dz;
	}

	public String getKhyh() {
		return khyh;
	}

	public void setKhyh(String khyh) {
		this.khyh = khyh;
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

	public String getSwjgdm() {
		return swjgdm;
	}

	public void setSwjgdm(String swjgdm) {
		this.swjgdm = swjgdm;
	}

	public String getJshj() {
		return jshj;
	}

	public void setJshj(String jshj) {
		this.jshj = jshj;
	}

	public String getWspzh() {
		return wspzh;
	}

	public void setWspzh(String wspzh) {
		this.wspzh = wspzh;
	}

	public String getDw() {
		return dw;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public String getXcrs() {
		return xcrs;
	}

	public void setXcrs(String xcrs) {
		this.xcrs = xcrs;
	}

	public String getZfbz() {
		return zfbz;
	}

	public void setZfbz(String zfbz) {
		this.zfbz = zfbz;
	}

	public String getSwjgmc() {
		return swjgmc;
	}

	public void setSwjgmc(String swjgmc) {
		this.swjgmc = swjgmc;
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

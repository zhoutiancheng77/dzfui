package com.dzf.zxkj.platform.model.piaotong;


import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class CaiFangTongBVO extends SuperVO<CaiFangTongBVO> {
	
	private String pk_caifangtong_b;//子表主键
	private String pk_caifangtong_h;//主表主键

	private String sphxh;//商品行序号
	private String spmc;//商品名称
	private String spsl;//商品数量
	private String spje;//商品金额
	private String spdj;//商品单价
	private String dw;//单位
	private String ggxh;//规格型号
	private String hsjbz;//含税价标志
	private String kce;//扣除额
	private String se;//税额
	private String sl;//税率
	private String spbm;//税商品编码
	private String zxbm;//自行编码
	private String yhzcbs;//优惠政策标识
	private String lslbs;//零税率标识
	private String zzstsgl;//增值税特殊管理
	private String fphxz;//发票行性质
	
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	
	private String pk_corp;
	private int dr;
	private DZFDateTime ts;
	
	public String getPk_caifangtong_b() {
		return pk_caifangtong_b;
	}

	public String getPk_caifangtong_h() {
		return pk_caifangtong_h;
	}

	public String getSphxh() {
		return sphxh;
	}

	public String getSpmc() {
		return spmc;
	}

	public String getSpsl() {
		return spsl;
	}

	public String getSpje() {
		return spje;
	}

	public String getSpdj() {
		return spdj;
	}

	public String getDw() {
		return dw;
	}

	public String getGgxh() {
		return ggxh;
	}

	public String getHsjbz() {
		return hsjbz;
	}

	public String getKce() {
		return kce;
	}

	public String getSe() {
		return se;
	}

	public String getSl() {
		return sl;
	}

	public String getSpbm() {
		return spbm;
	}

	public String getZxbm() {
		return zxbm;
	}

	public String getYhzcbs() {
		return yhzcbs;
	}

	public String getLslbs() {
		return lslbs;
	}

	public String getZzstsgl() {
		return zzstsgl;
	}

	public String getFphxz() {
		return fphxz;
	}

	public String getVdef1() {
		return vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public int getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setPk_caifangtong_b(String pk_caifangtong_b) {
		this.pk_caifangtong_b = pk_caifangtong_b;
	}

	public void setPk_caifangtong_h(String pk_caifangtong_h) {
		this.pk_caifangtong_h = pk_caifangtong_h;
	}

	public void setSphxh(String sphxh) {
		this.sphxh = sphxh;
	}

	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}

	public void setSpsl(String spsl) {
		this.spsl = spsl;
	}

	public void setSpje(String spje) {
		this.spje = spje;
	}

	public void setSpdj(String spdj) {
		this.spdj = spdj;
	}

	public void setDw(String dw) {
		this.dw = dw;
	}

	public void setGgxh(String ggxh) {
		this.ggxh = ggxh;
	}

	public void setHsjbz(String hsjbz) {
		this.hsjbz = hsjbz;
	}

	public void setKce(String kce) {
		this.kce = kce;
	}

	public void setSe(String se) {
		this.se = se;
	}

	public void setSl(String sl) {
		this.sl = sl;
	}

	public void setSpbm(String spbm) {
		this.spbm = spbm;
	}

	public void setZxbm(String zxbm) {
		this.zxbm = zxbm;
	}

	public void setYhzcbs(String yhzcbs) {
		this.yhzcbs = yhzcbs;
	}

	public void setLslbs(String lslbs) {
		this.lslbs = lslbs;
	}

	public void setZzstsgl(String zzstsgl) {
		this.zzstsgl = zzstsgl;
	}

	public void setFphxz(String fphxz) {
		this.fphxz = fphxz;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getParentPKFieldName() {
		return "pk_caifangtong_h";
	}

	@Override
	public String getPKFieldName() {
		return "pk_caifangtong_b";
	}

	@Override
	public String getTableName() {
		return "ynt_caifangtong_b";
	}

}

package com.dzf.zxkj.platform.model.piaotong;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 财房通主表
 * @author wangzhn
 *
 */
public class CaiFangTongHVO extends SuperVO<CaiFangTongBVO> {
	
	private String pk_caifangtong_h;//主键
	
	private String serialno;//批次号
	
	private String fpqqlsh;//发票请求流水号   销货方税号+发票代码+发票号码
	  
	private String xsf_nsrmc;// 销货方纳税人名称

	private String xsf_nsrsbh;// 销货方纳税人识别号

	private String fpdm;// 发票代码

	private String fphm;// 发票号码

	private String kprq;// 开票日期

	private String kplx;// 1-正票 2-红票 3-空白废票4-正废5-负废

	private String kphjje;// 开票合计金额 合计金额+合计税额

	private String hjbhsje;// 合计不含税金额 合计金额

	private String kphjse;// 开票合计税额 合计税额

	private String gmf_nsrsbh;// 购货方纳税人识别号

	private String gmf_nsrmc;// 购货方纳税人名称

	private String gmf_yh;// 购货方银行

	private String gmf_yhzh;// 购货方银行账号

	private String gmf_dz;// 购货方地址

	private String gmf_dh;// 购货方电话

	private String gmf_sf;// 购货方省份

	private String gmf_sj;// 购货方手机

	private String gmf_email;// 购货方邮箱

	private String yfphm;// 原发票号码

	private String yfpdm;// 原发票代码

	private String jqbh;// 机器编号

	private String kpy;// 开票员

	private String sky;// 收款员

	private String fhr;// 复核人

	private String fp_zldm;// 发票种类代码   财房通使用
	
	private String fp_zlmc;// 发票种类名称   发票扫码使用

	private String xsf_dz;// 销售方地址

	private String xsf_dh;// 销售方电话

	private String xsf_yh;// 销售方银行

	private String xsf_yhzh;// 销售方银行账号

	private String fjh;// 分机号

	private String dsptbm;// 电商平台编码

	private String dkbz;// 代开标志

	private String tschbz;// 特殊冲红标志

	private String chyy;// 冲红原因

	private String bmbbbh;// 编码表版本号

	private String skm;// 税控码

	private String ewm;// 二维码

	private String bz;// 备注

	private String fp_mw;// 防伪密文

	private String jym;// 校验码

	private String tspz;// 特殊票种 01农产品销售;02农产品收购;
						// 03稀土矿产品发票04 稀土产成品票;
						// 05:石脑油;其它为空

	private String slbz;// 含税税率标识 0：不含税税率，
							// 1：含税税率（即减按1.5%、中外合作油气田5%），
							// 2：差额征收
	private String sgbz;// 收购标志 默认为0 收购票为1
	//---------进项认证平台取票---------------
	private String fplx;//机动车发票类型
	private String spmc;//商品名称
	private String ggxh;//规格型号
	private String spdj;//商品单价
	private String spsl;//税率
	private String spse;//税额
	private String jshj;//价税合计
	private String maxkprq;
	//--------------进销项一键取票-------------
	private String cllx;//车辆类型
	private String cpxh;//厂牌型号
	private String slv;//税率
	private String xfdh;//销方电话
	private String xfkhzh;//销方开户账号
	private String depositBank;//开户银行
	private String address;//地址
	
	
	
	
	private String vdef1;
	private String vdef2;
	private String vdef3;
	private String vdef4;
	private String vdef5;
	
	private String demo;
	private String demo1;
	private int ly;
	private DZFBoolean isimp;//是否导入
	private DZFBoolean ispz;//是否生成凭证
	private String coperatorid;
	private String pk_corp;
	private DZFDate doperatedate;
	private int dr;
	private DZFDateTime ts;
	
	private CaiFangTongBVO[] fp_kjmx;
	
	
	
	
	
	
	
	public String getDepositBank() {
		return depositBank;
	}

	public void setDepositBank(String depositBank) {
		this.depositBank = depositBank;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
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

	public String getSlv() {
		return slv;
	}

	public void setSlv(String slv) {
		this.slv = slv;
	}

	public String getXfdh() {
		return xfdh;
	}

	public void setXfdh(String xfdh) {
		this.xfdh = xfdh;
	}

	public String getXfkhzh() {
		return xfkhzh;
	}

	public void setXfkhzh(String xfkhzh) {
		this.xfkhzh = xfkhzh;
	}

	public String getSpmc() {
		return spmc;
	}

	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}

	public String getGgxh() {
		return ggxh;
	}

	public void setGgxh(String ggxh) {
		this.ggxh = ggxh;
	}

	public String getSpdj() {
		return spdj;
	}

	public void setSpdj(String spdj) {
		this.spdj = spdj;
	}

	public String getSpsl() {
		return spsl;
	}

	public void setSpsl(String spsl) {
		this.spsl = spsl;
	}

	public String getSpse() {
		return spse;
	}

	public void setSpse(String spse) {
		this.spse = spse;
	}

	public String getJshj() {
		return jshj;
	}

	public void setJshj(String jshj) {
		this.jshj = jshj;
	}

	public String getFplx() {
		return fplx;
	}

	public void setFplx(String fplx) {
		this.fplx = fplx;
	}

	public CaiFangTongBVO[] getFp_kjmx() {
		return fp_kjmx;
	}

	public void setFp_kjmx(CaiFangTongBVO[] fp_kjmx) {
		this.fp_kjmx = fp_kjmx;
	}

	public String getPk_caifangtong_h() {
		return pk_caifangtong_h;
	}

	public String getFpqqlsh() {
		return fpqqlsh;
	}

	public String getXsf_nsrmc() {
		return xsf_nsrmc;
	}

	public String getXsf_nsrsbh() {
		return xsf_nsrsbh;
	}

	public String getFpdm() {
		return fpdm;
	}

	public String getFphm() {
		return fphm;
	}

	public String getKprq() {
		return kprq;
	}

	public String getKplx() {
		return kplx;
	}

	public String getKphjje() {
		return kphjje;
	}

	public String getDemo1() {
		return demo1;
	}

	public void setDemo1(String demo1) {
		this.demo1 = demo1;
	}

	public String getHjbhsje() {
		return hjbhsje;
	}

	public String getKphjse() {
		return kphjse;
	}

	public String getGmf_nsrsbh() {
		return gmf_nsrsbh;
	}

	public String getGmf_nsrmc() {
		return gmf_nsrmc;
	}

	public String getGmf_yh() {
		return gmf_yh;
	}

	public String getGmf_yhzh() {
		return gmf_yhzh;
	}

	public String getGmf_dz() {
		return gmf_dz;
	}

	public String getGmf_dh() {
		return gmf_dh;
	}

	public String getGmf_sf() {
		return gmf_sf;
	}

	public String getGmf_sj() {
		return gmf_sj;
	}

	public String getGmf_email() {
		return gmf_email;
	}

	public String getYfphm() {
		return yfphm;
	}

	public String getYfpdm() {
		return yfpdm;
	}

	public String getJqbh() {
		return jqbh;
	}

	public String getKpy() {
		return kpy;
	}

	public String getSky() {
		return sky;
	}

	public String getFhr() {
		return fhr;
	}

	public String getFp_zldm() {
		return fp_zldm;
	}

	public String getXsf_dz() {
		return xsf_dz;
	}

	public String getXsf_dh() {
		return xsf_dh;
	}

	public String getXsf_yh() {
		return xsf_yh;
	}

	public String getXsf_yhzh() {
		return xsf_yhzh;
	}

	public String getFjh() {
		return fjh;
	}

	public String getDsptbm() {
		return dsptbm;
	}

	public String getDkbz() {
		return dkbz;
	}

	public String getTschbz() {
		return tschbz;
	}

	public String getChyy() {
		return chyy;
	}

	public String getBmbbbh() {
		return bmbbbh;
	}

	public String getSkm() {
		return skm;
	}

	public String getEwm() {
		return ewm;
	}

	public String getBz() {
		return bz;
	}

	public String getFp_mw() {
		return fp_mw;
	}

	public String getJym() {
		return jym;
	}

	public String getTspz() {
		return tspz;
	}

	public String getSlbz() {
		return slbz;
	}

	public String getSgbz() {
		return sgbz;
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

	public DZFBoolean getIsimp() {
		return isimp;
	}

	public DZFBoolean getIspz() {
		return ispz;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public String getMaxkprq() {
		return maxkprq;
	}

	public void setMaxkprq(String maxkprq) {
		this.maxkprq = maxkprq;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public int getDr() {
		return dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public int getLy() {
		return ly;
	}

	public void setLy(int ly) {
		this.ly = ly;
	}

	public String getSerialno() {
		return serialno;
	}

	public void setSerialno(String serialno) {
		this.serialno = serialno;
	}

	public void setPk_caifangtong_h(String pk_caifangtong_h) {
		this.pk_caifangtong_h = pk_caifangtong_h;
	}

	public void setFpqqlsh(String fpqqlsh) {
		this.fpqqlsh = fpqqlsh;
	}

	public void setXsf_nsrmc(String xsf_nsrmc) {
		this.xsf_nsrmc = xsf_nsrmc;
	}

	public void setXsf_nsrsbh(String xsf_nsrsbh) {
		this.xsf_nsrsbh = xsf_nsrsbh;
	}

	public void setFpdm(String fpdm) {
		this.fpdm = fpdm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public void setKprq(String kprq) {
		this.kprq = kprq;
	}

	public void setKplx(String kplx) {
		this.kplx = kplx;
	}

	public void setKphjje(String kphjje) {
		this.kphjje = kphjje;
	}

	public void setHjbhsje(String hjbhsje) {
		this.hjbhsje = hjbhsje;
	}

	public void setKphjse(String kphjse) {
		this.kphjse = kphjse;
	}

	public void setGmf_nsrsbh(String gmf_nsrsbh) {
		this.gmf_nsrsbh = gmf_nsrsbh;
	}

	public void setGmf_nsrmc(String gmf_nsrmc) {
		this.gmf_nsrmc = gmf_nsrmc;
	}

	public void setGmf_yh(String gmf_yh) {
		this.gmf_yh = gmf_yh;
	}

	public void setGmf_yhzh(String gmf_yhzh) {
		this.gmf_yhzh = gmf_yhzh;
	}

	public void setGmf_dz(String gmf_dz) {
		this.gmf_dz = gmf_dz;
	}

	public void setGmf_dh(String gmf_dh) {
		this.gmf_dh = gmf_dh;
	}

	public void setGmf_sf(String gmf_sf) {
		this.gmf_sf = gmf_sf;
	}

	public void setGmf_sj(String gmf_sj) {
		this.gmf_sj = gmf_sj;
	}

	public void setGmf_email(String gmf_email) {
		this.gmf_email = gmf_email;
	}

	public void setYfphm(String yfphm) {
		this.yfphm = yfphm;
	}

	public void setYfpdm(String yfpdm) {
		this.yfpdm = yfpdm;
	}

	public void setJqbh(String jqbh) {
		this.jqbh = jqbh;
	}

	public void setKpy(String kpy) {
		this.kpy = kpy;
	}

	public void setSky(String sky) {
		this.sky = sky;
	}

	public void setFhr(String fhr) {
		this.fhr = fhr;
	}

	public void setFp_zldm(String fp_zldm) {
		this.fp_zldm = fp_zldm;
	}

	public void setXsf_dz(String xsf_dz) {
		this.xsf_dz = xsf_dz;
	}

	public void setXsf_dh(String xsf_dh) {
		this.xsf_dh = xsf_dh;
	}

	public void setXsf_yh(String xsf_yh) {
		this.xsf_yh = xsf_yh;
	}

	public void setXsf_yhzh(String xsf_yhzh) {
		this.xsf_yhzh = xsf_yhzh;
	}

	public void setFjh(String fjh) {
		this.fjh = fjh;
	}

	public void setDsptbm(String dsptbm) {
		this.dsptbm = dsptbm;
	}

	public void setDkbz(String dkbz) {
		this.dkbz = dkbz;
	}

	public void setTschbz(String tschbz) {
		this.tschbz = tschbz;
	}

	public void setChyy(String chyy) {
		this.chyy = chyy;
	}

	public void setBmbbbh(String bmbbbh) {
		this.bmbbbh = bmbbbh;
	}

	public void setSkm(String skm) {
		this.skm = skm;
	}

	public void setEwm(String ewm) {
		this.ewm = ewm;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public void setFp_mw(String fp_mw) {
		this.fp_mw = fp_mw;
	}

	public void setJym(String jym) {
		this.jym = jym;
	}

	public void setTspz(String tspz) {
		this.tspz = tspz;
	}

	public void setSlbz(String slbz) {
		this.slbz = slbz;
	}

	public void setSgbz(String sgbz) {
		this.sgbz = sgbz;
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

	public void setIsimp(DZFBoolean isimp) {
		this.isimp = isimp;
	}

	public void setIspz(DZFBoolean ispz) {
		this.ispz = ispz;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getDemo() {
		return demo;
	}

	public void setDemo(String demo) {
		this.demo = demo;
	}

	public void setDr(int dr) {
		this.dr = dr;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_caifangtong_h";
	}

	@Override
	public String getTableName() {
		return "ynt_caifangtong_h";
	}

}

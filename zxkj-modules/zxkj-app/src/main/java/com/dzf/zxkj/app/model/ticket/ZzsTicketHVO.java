package com.dzf.zxkj.app.model.ticket;

import java.util.List;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * （1）增值税专用发票返回报文内层内容说明（发票类型:01）
 * 
 * @author zhangj
 *
 */
public class ZzsTicketHVO extends SuperVO {

	public static final String TABLENAME = "app_zzstiket";
	public static final String PKFIELDNAME = "pk_zzstiket";

	private String pk_zzstiket;
	private String drcode;
	private String fpdm;// 发票代码
	private String fphm;// 发票号码
	private String cycs;// 查验次数
	private String kprq;// 开票日期
	private String gfmc;// 购方名称
	private String gfsbh;// 购方纳税人识别号
	private String gfdzdh;// 购方地址、电话
	private String gfyhzh;// 购方开户行及账号
	private String xfmc;// 销方名称
	private String xfsbh;// 销方纳税人识别号
	private String xfdzdh;// 销方地址电话
	private String xfyhzh;// 销方开户行及账号
	private String je;// 金额合计
	private String se;// 税额合计
	private String jshj;// 价税合计
	private String jqbh;// 机器编号
	private String jym;// 校验码
	private String zfbz;// 作废标志
	private String fpzl;// 发票种类
	private String fpxms;// 项目信息
	private String fpjes;// 金额信息
	private String pk_tzpz_h;// 凭证信息
	private String pk_corp;// 公司主键
	private DZFDateTime ts;
	private Integer dr;
	private String pzmsg;
	private String vrecord;// 浏览记录
	private String img_url;//图片url地址
	private String file;//文件
	private String file_type;//文件类型
	
	private String fplx;//0 进项 1 销项
	
	private String fplx_str;//发票类型汉字 进项发票，销项发票
	
	
	public String getFplx_str() {
		return fplx_str;
	}

	public void setFplx_str(String fplx_str) {
		this.fplx_str = fplx_str;
	}

	public String getFplx() {
		return fplx;
	}

	public void setFplx(String fplx) {
		this.fplx = fplx;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFile_type() {
		return file_type;
	}

	public void setFile_type(String file_type) {
		this.file_type = file_type;
	}

	//----------不存库------------
	private List<MapBean> kpgsmap;//扫票公司列表
	
	private String scanmsg;//扫描信息
	
	private String memo;//摘要
	
	private Integer method;//结算方式
	
	public String getImg_url() {
		return img_url;
	}

	public void setImg_url(String img_url) {
		this.img_url = img_url;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public Integer getMethod() {
		return method;
	}

	public void setMethod(Integer method) {
		this.method = method;
	}

	public String getScanmsg() {
		return scanmsg;
	}

	public void setScanmsg(String scanmsg) {
		this.scanmsg = scanmsg;
	}

	public List<MapBean> getKpgsmap() {
		return kpgsmap;
	}

	public void setKpgsmap(List<MapBean> kpgsmap) {
		this.kpgsmap = kpgsmap;
	}

	public String getVrecord() {
		return vrecord;
	}

	public void setVrecord(String vrecord) {
		this.vrecord = vrecord;
	}

	public String getPzmsg() {
		return pzmsg;
	}

	public void setPzmsg(String pzmsg) {
		this.pzmsg = pzmsg;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public String getDrcode() {
		return drcode;
	}

	public void setDrcode(String drcode) {
		this.drcode = drcode;
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

	public String getPk_zzstiket() {
		return pk_zzstiket;
	}

	public void setPk_zzstiket(String pk_zzstiket) {
		this.pk_zzstiket = pk_zzstiket;
	}

	public String getFpxms() {
		return fpxms;
	}

	public void setFpxms(String fpxms) {
		this.fpxms = fpxms;
	}

	public String getFpjes() {
		return fpjes;
	}

	public void setFpjes(String fpjes) {
		this.fpjes = fpjes;
	}

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

	public String getGfmc() {
		return gfmc;
	}

	public void setGfmc(String gfmc) {
		this.gfmc = gfmc;
	}

	public String getGfsbh() {
		return gfsbh;
	}

	public void setGfsbh(String gfsbh) {
		this.gfsbh = gfsbh;
	}

	public String getGfdzdh() {
		return gfdzdh;
	}

	public void setGfdzdh(String gfdzdh) {
		this.gfdzdh = gfdzdh;
	}

	public String getGfyhzh() {
		return gfyhzh;
	}

	public void setGfyhzh(String gfyhzh) {
		this.gfyhzh = gfyhzh;
	}

	public String getXfmc() {
		return xfmc;
	}

	public void setXfmc(String xfmc) {
		this.xfmc = xfmc;
	}

	public String getXfsbh() {
		return xfsbh;
	}

	public void setXfsbh(String xfsbh) {
		this.xfsbh = xfsbh;
	}

	public String getXfdzdh() {
		return xfdzdh;
	}

	public void setXfdzdh(String xfdzdh) {
		this.xfdzdh = xfdzdh;
	}

	public String getXfyhzh() {
		return xfyhzh;
	}

	public void setXfyhzh(String xfyhzh) {
		this.xfyhzh = xfyhzh;
	}

	public String getJe() {
		return je;
	}

	public void setJe(String je) {
		this.je = je;
	}

	public String getSe() {
		return se;
	}

	public void setSe(String se) {
		this.se = se;
	}

	public String getJshj() {
		return jshj;
	}

	public void setJshj(String jshj) {
		this.jshj = jshj;
	}

	public String getJqbh() {
		return jqbh;
	}

	public void setJqbh(String jqbh) {
		this.jqbh = jqbh;
	}

	public String getJym() {
		return jym;
	}

	public void setJym(String jym) {
		this.jym = jym;
	}

	public String getZfbz() {
		return zfbz;
	}

	public void setZfbz(String zfbz) {
		this.zfbz = zfbz;
	}

	@Override
	public String getPKFieldName() {
		return PKFIELDNAME;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLENAME;
	}

}

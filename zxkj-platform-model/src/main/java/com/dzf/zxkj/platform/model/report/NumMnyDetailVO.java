package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 
 * 数量金额明细
 *
 */
public class NumMnyDetailVO extends SuperVO {
	//
	private DZFDouble nnumber;
	private DZFDouble dfmny;
	private DZFDouble jfmny;
	
	//辅助核算项改为fzhsx1(客户)～fzhsx10(自定义项4)共10个字段，分别保存各辅助核算项的具体档案(ynt_fzhs_b)的key
	private String fzhsx1;
	private String fzhsx2;
	private String fzhsx3;
	private String fzhsx4;
	private String fzhsx5;
	private String fzhsx6;
	private String fzhsx7;
	private String fzhsx8;
	private String fzhsx9;
	private String fzhsx10;

	private Integer accountlevel;
	private String kmmc;
	private String kmbm;
	private String spmc;
	private String qj;
	private String opdate;
	private String pzh;
	private String pzhhid;
	private String zy;
	private DZFDouble nnum;
	private DZFDouble nprice;
	private DZFDouble nmny;
	private DZFDouble ndnum;
	private DZFDouble ndprice;
	private DZFDouble ndmny;
	private String dir;
	private DZFDouble nynum;
	private DZFDouble nyprice;
	private DZFDouble nymny;
	private String pk_inventory;
	private String pk_subject;
	private String id;
	private String checked;//是否被选中
	private DZFDouble npzprice;//凭证单价
	//打印时  标题区间
	private String titlePeriod;
	private String gs;  //公司
	private String jldw;
	
	private String pk_corp;//记录公司主键
	private Integer vdirect;//方向
	private DZFDouble xsprice;//销售单价 ，平均单价。
	private DZFBoolean bsyszy;//是否系统摘要
	
	private DZFDouble zgxsnum;//暂估销售数量(用于成本结转暂估单价取数计算)
	private DZFDouble zgxsmny;//暂估销售金额(用于成本结转暂估单价取数计算)
	
	private DZFDouble zgcgnum;//暂估采购数量(用于成本结转暂估单价取数计算)
	private DZFDouble zgcgmny;//暂估采购金额(用于成本结转暂估单价取数计算)
	
	public DZFBoolean getBsyszy() {
		return bsyszy;
	}
	public void setBsyszy(DZFBoolean bsyszy) {
		this.bsyszy = bsyszy;
	}
	public String getChecked() {
		return checked;
	}
	public void setChecked(String checked) {
		this.checked = checked;
	}
	public String getId() {//默认等于 pk_subject 为了前台，树状结构使用
		return kmbm;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public DZFDouble getNpzprice() {
		return npzprice;
	}
	public void setNpzprice(DZFDouble npzprice) {
		this.npzprice = npzprice;
	}
	public String getJldw() {
		return jldw;
	}
	public void setJldw(String jldw) {
		this.jldw = jldw;
	}
	public String getKmmc() {
		return kmmc;
	}
	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}
	public String getSpmc() {
		return spmc;
	}
	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}
	public String getQj() {
		return qj;
	}
	public void setQj(String qj) {
		this.qj = qj;
	}
	public String getOpdate() {
		return opdate;
	}
	public void setOpdate(String opdate) {
		this.opdate = opdate;
	}
	public String getPzh() {
		return pzh;
	}
	public void setPzh(String pzh) {
		this.pzh = pzh;
	}
	public String getPzhhid() {
		return pzhhid;
	}
	public void setPzhhid(String pzhhid) {
		this.pzhhid = pzhhid;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	public DZFDouble getNnum() {
		return nnum;
	}
	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}
	public DZFDouble getNprice() {
		return nprice;
	}
	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}
	public DZFDouble getNmny() {
		return nmny;
	}
	public void setNmny(DZFDouble nmny) {
		this.nmny = nmny;
	}
	public DZFDouble getNdnum() {
		return ndnum;
	}
	public void setNdnum(DZFDouble ndnum) {
		this.ndnum = ndnum;
	}
	public DZFDouble getNdprice() {
		return ndprice;
	}
	public void setNdprice(DZFDouble ndprice) {
		this.ndprice = ndprice;
	}
	public DZFDouble getNdmny() {
		return ndmny;
	}
	public void setNdmny(DZFDouble ndmny) {
		this.ndmny = ndmny;
	}
	public String getDir() {
		return dir;
	}
	public void setDir(String dir) {
		this.dir = dir;
	}
	public DZFDouble getNynum() {
		return nynum;
	}
	public void setNynum(DZFDouble nynum) {
		this.nynum = nynum;
	}
	public DZFDouble getNyprice() {
		return nyprice;
	}
	public void setNyprice(DZFDouble nyprice) {
		this.nyprice = nyprice;
	}
	public DZFDouble getNymny() {
		return nymny;
	}
	public void setNymny(DZFDouble nymny) {
		this.nymny = nymny;
	}
	public String getPk_inventory() {
		return pk_inventory;
	}
	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	public String getPk_subject() {
		return pk_subject;
	}
	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}
	@Override
	public String getParentPKFieldName() {
		return null;
	}
	@Override
	public String getPKFieldName() {
		return null;
	}
	@Override
	public String getTableName() {
		return null;
	}
	public String getTitlePeriod() {
		return titlePeriod;
	}
	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}
	public String getGs() {
		return gs;
	}
	public void setGs(String gs) {
		this.gs = gs;
	}
	public DZFDouble getNnumber() {
		return nnumber;
	}
	public void setNnumber(DZFDouble nnumber) {
		this.nnumber = nnumber;
	}
	public DZFDouble getDfmny() {
		return dfmny;
	}
	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}
	public DZFDouble getJfmny() {
		return jfmny;
	}
	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
	}
	public String getKmbm() {
		return kmbm;
	}
	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}
	public Integer getAccountlevel() {
		return accountlevel;
	}
	public void setAccountlevel(Integer accountlevel) {
		this.accountlevel = accountlevel;
	}
	public String getFzhsx1() {
		return fzhsx1;
	}
	public void setFzhsx1(String fzhsx1) {
		this.fzhsx1 = fzhsx1;
	}
	public String getFzhsx2() {
		return fzhsx2;
	}
	public void setFzhsx2(String fzhsx2) {
		this.fzhsx2 = fzhsx2;
	}
	public String getFzhsx3() {
		return fzhsx3;
	}
	public void setFzhsx3(String fzhsx3) {
		this.fzhsx3 = fzhsx3;
	}
	public String getFzhsx4() {
		return fzhsx4;
	}
	public void setFzhsx4(String fzhsx4) {
		this.fzhsx4 = fzhsx4;
	}
	public String getFzhsx5() {
		return fzhsx5;
	}
	public void setFzhsx5(String fzhsx5) {
		this.fzhsx5 = fzhsx5;
	}
	public String getFzhsx6() {
		return fzhsx6;
	}
	public void setFzhsx6(String fzhsx6) {
		this.fzhsx6 = fzhsx6;
	}
	public String getFzhsx7() {
		return fzhsx7;
	}
	public void setFzhsx7(String fzhsx7) {
		this.fzhsx7 = fzhsx7;
	}
	public String getFzhsx8() {
		return fzhsx8;
	}
	public void setFzhsx8(String fzhsx8) {
		this.fzhsx8 = fzhsx8;
	}
	public String getFzhsx9() {
		return fzhsx9;
	}
	public void setFzhsx9(String fzhsx9) {
		this.fzhsx9 = fzhsx9;
	}
	public String getFzhsx10() {
		return fzhsx10;
	}
	public void setFzhsx10(String fzhsx10) {
		this.fzhsx10 = fzhsx10;
	}
	public Integer getVdirect() {
		return vdirect;
	}
	public void setVdirect(Integer vdirect) {
		this.vdirect = vdirect;
	}
	public DZFDouble getXsprice() {
		return xsprice;
	}
	public void setXsprice(DZFDouble xsprice) {
		this.xsprice = xsprice;
	}
	public DZFDouble getZgxsnum() {
		return zgxsnum;
	}
	public DZFDouble getZgxsmny() {
		return zgxsmny;
	}
	public DZFDouble getZgcgnum() {
		return zgcgnum;
	}
	public void setZgxsnum(DZFDouble zgxsnum) {
		this.zgxsnum = zgxsnum;
	}
	public void setZgxsmny(DZFDouble zgxsmny) {
		this.zgxsmny = zgxsmny;
	}
	public void setZgcgnum(DZFDouble zgcgnum) {
		this.zgcgnum = zgcgnum;
	}
	public DZFDouble getZgcgmny() {
		return zgcgmny;
	}
	public void setZgcgmny(DZFDouble zgcgmny) {
		this.zgcgmny = zgcgmny;
	}
}

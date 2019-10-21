package com.dzf.zxkj.platform.model.glic;
/**
 * 总账库存明细
 * @author wangzhn
 *
 */
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

public class IcDetailVO extends SuperVO {
	//查询结果
	private String pk_ictrade_h;
	private DZFDate dbilldate;// 单据日期
	private String dbillid;// 单据编号
	private String pzh;//凭证号
	private String pk_tzpz_h;//凭证主键
	private Integer nbills;//附件数
	private String coperatorid;//操作人
	private String cbilltype;// 单据类型
	private String pk_ictrade_b;//子
	private String pk_inventory;
	
	private DZFDouble nnum;
	private DZFDouble glchhsnum;
	private DZFDouble nymny;
	private DZFDouble ncost;
	private DZFDateTime ts;
	
	//针对总账查询的
	private DZFDouble jfmny;
	private DZFDouble dfmny;
	private DZFDouble jfnum;
	private DZFDouble dfnum;
	private String vicbillcode;//出入库单据号
	private String vicbillcodetype;//出入库 类型
	private DZFDouble nprice;//单价
	private DZFDouble glcgmny;//入库单金额
	private DZFDouble xsjzcb;//销售结转成本
	
	//展示结果
	private String pk_accsubj;
	private String kmbm;
	private String km;
	private String spfl;
	private String spfl_name;//商品分类名称
	private String spgg;
	private String spxh;
	private String pk_sp;
	private String spmc;
	private String spbm;
	private String jldw;
	private String rq;
	private String djbh;
	private String zy;
	
	private DZFDouble qcsl;//期初
	private DZFDouble qcdj;
	private DZFDouble qcje;
	
	private DZFDouble srsl;//收入
	private DZFDouble srdj;
	private DZFDouble srje;
	
	private DZFDouble fcsl;//发出
	private DZFDouble fcdj;
	private DZFDouble fcje;
	
	private DZFDouble jcsl;//结存
	private DZFDouble jcdj;
	private DZFDouble jcje;
	
	private DZFDouble zgdj;//暂估单价
	
	//打印相关
	public String titlePeriod;
    public String gs;
    public String isPaging;
    
    private String pk_corp;
    
    private DZFBoolean bsyszy;//是否系统摘要
    
    
	public DZFBoolean getBsyszy() {
		return bsyszy;
	}
	public void setBsyszy(DZFBoolean bsyszy) {
		this.bsyszy = bsyszy;
	}
	public String getSpfl_name() {
		return spfl_name;
	}
	public void setSpfl_name(String spfl_name) {
		this.spfl_name = spfl_name;
	}
	public DZFDouble getXsjzcb() {
		return xsjzcb;
	}
	public void setXsjzcb(DZFDouble xsjzcb) {
		this.xsjzcb = xsjzcb;
	}
	public DZFDouble getGlchhsnum() {
		return glchhsnum;
	}
	public void setGlchhsnum(DZFDouble glchhsnum) {
		this.glchhsnum = glchhsnum;
	}
	public DZFDouble getJfnum() {
		return jfnum;
	}
	public void setJfnum(DZFDouble jfnum) {
		this.jfnum = jfnum;
	}
	public DZFDouble getDfnum() {
		return dfnum;
	}
	public void setDfnum(DZFDouble dfnum) {
		this.dfnum = dfnum;
	}
	public DZFDouble getGlcgmny() {
		return glcgmny;
	}
	public void setGlcgmny(DZFDouble glcgmny) {
		this.glcgmny = glcgmny;
	}
	public Integer getNbills() {
		return nbills;
	}
	public void setNbills(Integer nbills) {
		this.nbills = nbills;
	}
	public String getCoperatorid() {
		return coperatorid;
	}
	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}
	public DZFDouble getNprice() {
		return nprice;
	}
	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}
	public String getVicbillcode() {
		return vicbillcode;
	}
	public void setVicbillcode(String vicbillcode) {
		this.vicbillcode = vicbillcode;
	}
	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}
	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}
	public String getPzh() {
		return pzh;
	}
	public void setPzh(String pzh) {
		this.pzh = pzh;
	}
	public String getPk_corp() {
		return pk_corp;
	}
	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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
	public String getPk_accsubj() {
		return pk_accsubj;
	}
	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}
	public String getKmbm() {
		return kmbm;
	}
	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}
	public String getKm() {
		return km;
	}
	public void setKm(String km) {
		this.km = km;
	}
	public String getSpfl() {
		return spfl;
	}
	public void setSpfl(String spfl) {
		this.spfl = spfl;
	}
	public String getSpgg() {
		return spgg;
	}
	public void setSpgg(String spgg) {
		this.spgg = spgg;
	}
	public String getSpxh() {
		return spxh;
	}
	public void setSpxh(String spxh) {
		this.spxh = spxh;
	}
	public String getPk_sp() {
		return pk_sp;
	}
	public void setPk_sp(String pk_sp) {
		this.pk_sp = pk_sp;
	}
	public String getSpmc() {
		return spmc;
	}
	public void setSpmc(String spmc) {
		this.spmc = spmc;
	}
	public String getSpbm() {
		return spbm;
	}
	public void setSpbm(String spbm) {
		this.spbm = spbm;
	}
	public String getJldw() {
		return jldw;
	}
	public void setJldw(String jldw) {
		this.jldw = jldw;
	}
	public String getRq() {
		return rq;
	}
	public void setRq(String rq) {
		this.rq = rq;
	}
	public String getDjbh() {
		return djbh;
	}
	public void setDjbh(String djbh) {
		this.djbh = djbh;
	}
	public String getZy() {
		return zy;
	}
	public void setZy(String zy) {
		this.zy = zy;
	}
	
	public String getVicbillcodetype() {
		return vicbillcodetype;
	}
	public void setVicbillcodetype(String vicbillcodetype) {
		this.vicbillcodetype = vicbillcodetype;
	}
	public DZFDouble getJfmny() {
		return jfmny;
	}
	public DZFDouble getDfmny() {
		return dfmny;
	}
	public void setJfmny(DZFDouble jfmny) {
		this.jfmny = jfmny;
	}
	public void setDfmny(DZFDouble dfmny) {
		this.dfmny = dfmny;
	}
	public DZFDouble getQcsl() {
		return qcsl;
	}
	public DZFDouble getQcdj() {
		return qcdj;
	}
	public DZFDouble getQcje() {
		return qcje;
	}
	public void setQcsl(DZFDouble qcsl) {
		this.qcsl = qcsl;
	}
	public void setQcdj(DZFDouble qcdj) {
		this.qcdj = qcdj;
	}
	public void setQcje(DZFDouble qcje) {
		this.qcje = qcje;
	}
	public DZFDouble getSrsl() {
		return srsl;
	}
	public void setSrsl(DZFDouble srsl) {
		this.srsl = srsl;
	}
	public DZFDouble getSrdj() {
		return srdj;
	}
	public void setSrdj(DZFDouble srdj) {
		this.srdj = srdj;
	}
	public DZFDouble getSrje() {
		return srje;
	}
	public void setSrje(DZFDouble srje) {
		this.srje = srje;
	}
	public DZFDouble getFcsl() {
		return fcsl;
	}
	public void setFcsl(DZFDouble fcsl) {
		this.fcsl = fcsl;
	}
	public DZFDouble getFcdj() {
		return fcdj;
	}
	public void setFcdj(DZFDouble fcdj) {
		this.fcdj = fcdj;
	}
	public DZFDouble getFcje() {
		return fcje;
	}
	public void setFcje(DZFDouble fcje) {
		this.fcje = fcje;
	}
	public DZFDouble getJcsl() {
		return jcsl;
	}
	public void setJcsl(DZFDouble jcsl) {
		this.jcsl = jcsl;
	}
	public DZFDouble getJcdj() {
		return jcdj;
	}
	public void setJcdj(DZFDouble jcdj) {
		this.jcdj = jcdj;
	}
	public DZFDouble getJcje() {
		return jcje;
	}
	public void setJcje(DZFDouble jcje) {
		this.jcje = jcje;
	}
	
	public DZFDouble getZgdj() {
		return zgdj;
	}
	public void setZgdj(DZFDouble zgdj) {
		this.zgdj = zgdj;
	}
	public String getPk_ictrade_h() {
		return pk_ictrade_h;
	}
	public void setPk_ictrade_h(String pk_ictrade_h) {
		this.pk_ictrade_h = pk_ictrade_h;
	}
	public DZFDate getDbilldate() {
		return dbilldate;
	}
	public void setDbilldate(DZFDate dbilldate) {
		this.dbilldate = dbilldate;
	}
	public String getDbillid() {
		return dbillid;
	}
	public void setDbillid(String dbillid) {
		this.dbillid = dbillid;
	}
	public String getCbilltype() {
		return cbilltype;
	}
	public void setCbilltype(String cbilltype) {
		this.cbilltype = cbilltype;
	}
	public String getPk_ictrade_b() {
		return pk_ictrade_b;
	}
	public void setPk_ictrade_b(String pk_ictrade_b) {
		this.pk_ictrade_b = pk_ictrade_b;
	}
	public String getPk_inventory() {
		return pk_inventory;
	}
	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	public DZFDouble getNnum() {
		return nnum;
	}
	public void setNnum(DZFDouble nnum) {
		this.nnum = nnum;
	}
	public DZFDouble getNymny() {
		return nymny;
	}
	public void setNymny(DZFDouble nymny) {
		this.nymny = nymny;
	}
	public DZFDouble getNcost() {
		return ncost;
	}
	public void setNcost(DZFDouble ncost) {
		this.ncost = ncost;
	}
	
	public DZFDateTime getTs() {
		return ts;
	}
	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}
	
	public String getIsPaging() {
		return isPaging;
	}
	public void setIsPaging(String isPaging) {
		this.isPaging = isPaging;
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
}

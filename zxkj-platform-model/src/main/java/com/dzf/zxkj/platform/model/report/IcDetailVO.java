package com.dzf.zxkj.platform.model.report;

/**
 * 库存明细
 * @author wangzhn
 *
 */
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;

public class IcDetailVO extends SuperVO {

//	private String spid;//商品
//	private DZFBoolean isqry;//是否重新查询
	
	//查询结果
	private String pk_ictrade_h;
	private DZFDate dbilldate;// 单据日期
	private String dbillid;// 单据编号
	private String cbilltype;// 单据类型
	private String pk_ictrade_b;//子
	private String pk_inventory;
	private DZFDouble nnum;
	private DZFDouble nymny;
	private DZFDouble ncost;
	private DZFDateTime ts;
	
	//展示结果
	private String pk_accsubj;
	private String kmbm;
	private String km;
	private String spfl;//商品分类
	private String spflid;//商品分类id
	private String spflcode;//商品分类编码
	private String spgg;
	private String spxh;
	private String pk_sp;
	private String spmc;
	private String spbm;
	private String jldw;
	private String rq;
	private String djbh;
	private String zy;
	
	private DZFDouble qcsl;
	private DZFDouble qcdj;
	private DZFDouble qcje;
	
	private DZFDouble srsl;
	private DZFDouble srdj;
	private DZFDouble srje;
	
	private DZFDouble fcsl;
	private DZFDouble fcdj;
	private DZFDouble fcje;
	
	private DZFDouble jcsl;
	private DZFDouble jcdj;
	private DZFDouble jcje;
	
	//打印相关
	public String titlePeriod;
    public String gs;
    public String isPaging;
    
    private String pk_corp;
    
    private DZFBoolean bsyszy;
    
	public DZFBoolean getBsyszy() {
		return bsyszy;
	}
	public void setBsyszy(DZFBoolean bsyszy) {
		this.bsyszy = bsyszy;
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
	public String getSpflid() {
		return spflid;
	}
	public String getSpflcode() {
		return spflcode;
	}
	public void setSpflid(String spflid) {
		this.spflid = spflid;
	}
	
	public void setSpflcode(String spflcode) {
		this.spflcode = spflcode;
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
	//	public String getSpid() {
//		return spid;
//	}
//	public void setSpid(String spid) {
//		this.spid = spid;
//	}
//	public DZFBoolean getIsqry() {
//		return isqry;
//	}
//	public void setIsqry(DZFBoolean isqry) {
//		this.isqry = isqry;
//	}
	
	
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

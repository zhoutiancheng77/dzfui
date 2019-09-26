package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

public class ImageGroupBAKVO extends SuperVO {
	
	private String pk_image_group_bak;
	private String pk_corp;
    private DZFDateTime ts;
    private String sessionflag;
    private String coperatorid;
    private Integer dr;
    private DZFDate doperatedate;
    private String pk_image_group;
    private String groupcode;
    private DZFBoolean iscliped;
    private String clipedby;
    private DZFDateTime clipedon;
    private DZFBoolean isskiped;
    private String skipedby;
    private DZFDateTime skipedon;
    private String settlemode;
    private DZFBoolean cert;
    private String certtx;
    private DZFBoolean isuer;
    private String zdrmc;
    private DZFDouble mny;
    private String memo;
    private String certbusitype;
    private String certctnum;
    private String certmsg;
    private Integer imagecounts;
    private String otcorp;
    private DZFBoolean ishd;
    private DZFBoolean isfj;
    private DZFBoolean isdb;
    private Integer fpstyle;
    private String fpstylecode;
    private String fpstylename;
    private DZFDate fjdate;
    private String fjr;
    private DZFDate dbdate;
    private String cdbcorpid;
    private String szstylecode;
    private String szstylename;
    private Integer istate;
    private DZFDate cvoucherdate;
    private Integer sourcemode;

	public String getPk_image_group_bak() {
		return pk_image_group_bak;
	}

	public void setPk_image_group_bak(String pk_image_group_bak) {
		this.pk_image_group_bak = pk_image_group_bak;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getSessionflag() {
		return sessionflag;
	}

	public void setSessionflag(String sessionflag) {
		this.sessionflag = sessionflag;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	public String getGroupcode() {
		return groupcode;
	}

	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}

	public DZFBoolean getIscliped() {
		return iscliped;
	}

	public void setIscliped(DZFBoolean iscliped) {
		this.iscliped = iscliped;
	}

	public String getClipedby() {
		return clipedby;
	}

	public void setClipedby(String clipedby) {
		this.clipedby = clipedby;
	}

	public DZFDateTime getClipedon() {
		return clipedon;
	}

	public void setClipedon(DZFDateTime clipedon) {
		this.clipedon = clipedon;
	}

	public DZFBoolean getIsskiped() {
		return isskiped;
	}

	public void setIsskiped(DZFBoolean isskiped) {
		this.isskiped = isskiped;
	}

	public String getSkipedby() {
		return skipedby;
	}

	public void setSkipedby(String skipedby) {
		this.skipedby = skipedby;
	}

	public DZFDateTime getSkipedon() {
		return skipedon;
	}

	public void setSkipedon(DZFDateTime skipedon) {
		this.skipedon = skipedon;
	}

	public String getSettlemode() {
		return settlemode;
	}

	public void setSettlemode(String settlemode) {
		this.settlemode = settlemode;
	}

	public DZFBoolean getCert() {
		return cert;
	}

	public void setCert(DZFBoolean cert) {
		this.cert = cert;
	}

	public String getCerttx() {
		return certtx;
	}

	public void setCerttx(String certtx) {
		this.certtx = certtx;
	}

	public DZFBoolean getIsuer() {
		return isuer;
	}

	public void setIsuer(DZFBoolean isuer) {
		this.isuer = isuer;
	}

	public String getZdrmc() {
		return zdrmc;
	}

	public void setZdrmc(String zdrmc) {
		this.zdrmc = zdrmc;
	}

	public DZFDouble getMny() {
		return mny;
	}

	public void setMny(DZFDouble mny) {
		this.mny = mny;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getCertbusitype() {
		return certbusitype;
	}

	public void setCertbusitype(String certbusitype) {
		this.certbusitype = certbusitype;
	}

	public String getCertctnum() {
		return certctnum;
	}

	public void setCertctnum(String certctnum) {
		this.certctnum = certctnum;
	}

	public String getCertmsg() {
		return certmsg;
	}

	public void setCertmsg(String certmsg) {
		this.certmsg = certmsg;
	}

	public Integer getImagecounts() {
		return imagecounts;
	}

	public void setImagecounts(Integer imagecounts) {
		this.imagecounts = imagecounts;
	}

	public String getOtcorp() {
		return otcorp;
	}

	public void setOtcorp(String otcorp) {
		this.otcorp = otcorp;
	}

	public DZFBoolean getIshd() {
		return ishd;
	}

	public void setIshd(DZFBoolean ishd) {
		this.ishd = ishd;
	}

	public DZFBoolean getIsfj() {
		return isfj;
	}

	public void setIsfj(DZFBoolean isfj) {
		this.isfj = isfj;
	}

	public DZFBoolean getIsdb() {
		return isdb;
	}

	public void setIsdb(DZFBoolean isdb) {
		this.isdb = isdb;
	}

	public Integer getFpstyle() {
		return fpstyle;
	}

	public void setFpstyle(Integer fpstyle) {
		this.fpstyle = fpstyle;
	}

	public String getFpstylecode() {
		return fpstylecode;
	}

	public void setFpstylecode(String fpstylecode) {
		this.fpstylecode = fpstylecode;
	}

	public String getFpstylename() {
		return fpstylename;
	}

	public void setFpstylename(String fpstylename) {
		this.fpstylename = fpstylename;
	}

	public DZFDate getFjdate() {
		return fjdate;
	}

	public void setFjdate(DZFDate fjdate) {
		this.fjdate = fjdate;
	}

	public String getFjr() {
		return fjr;
	}

	public void setFjr(String fjr) {
		this.fjr = fjr;
	}

	public DZFDate getDbdate() {
		return dbdate;
	}

	public void setDbdate(DZFDate dbdate) {
		this.dbdate = dbdate;
	}

	public String getCdbcorpid() {
		return cdbcorpid;
	}

	public void setCdbcorpid(String cdbcorpid) {
		this.cdbcorpid = cdbcorpid;
	}

	public String getSzstylecode() {
		return szstylecode;
	}

	public void setSzstylecode(String szstylecode) {
		this.szstylecode = szstylecode;
	}

	public String getSzstylename() {
		return szstylename;
	}

	public void setSzstylename(String szstylename) {
		this.szstylename = szstylename;
	}

	public Integer getIstate() {
		return istate;
	}

	public void setIstate(Integer istate) {
		this.istate = istate;
	}

	public DZFDate getCvoucherdate() {
		return cvoucherdate;
	}

	public void setCvoucherdate(DZFDate cvoucherdate) {
		this.cvoucherdate = cvoucherdate;
	}

	public Integer getSourcemode() {
		return sourcemode;
	}

	public void setSourcemode(Integer sourcemode) {
		this.sourcemode = sourcemode;
	}

	@Override
	public String getPKFieldName() {
		return "pk_image_group_bak";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_image_group_bak";
	}

}

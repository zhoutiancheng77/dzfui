package com.dzf.zxkj.platform.model.secret;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;

public class SecretKeyVo extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String TABLE_NAME = "ynt_secret_key";

	public static final String PK_FIELD = "pk_secret_key";

	private String pk_secret_key;// 主键
	private String pk_corp;// 公司id
	private String vversionno;// 版本号
	private Integer isourcesys;// 加密对象 (0 数据备份，不同的对象处理的方式可以不一样)
	private Integer isecrettype;// 加密类型(0 RSA加密)
	private String rsafilename;//rsa加密文件
	private String vsecretkey;//秘钥
	private DZFDateTime dversionbegdate;//版本启用时间
	private DZFDateTime dversionenddate;//版本结束时间
	private Integer dr;//
	private DZFDateTime ts;//
	

	public DZFDateTime getDversionbegdate() {
		return dversionbegdate;
	}

	public void setDversionbegdate(DZFDateTime dversionbegdate) {
		this.dversionbegdate = dversionbegdate;
	}

	public DZFDateTime getDversionenddate() {
		return dversionenddate;
	}

	public void setDversionenddate(DZFDateTime dversionenddate) {
		this.dversionenddate = dversionenddate;
	}

	public String getRsafilename() {
		return rsafilename;
	}

	public void setRsafilename(String rsafilename) {
		this.rsafilename = rsafilename;
	}

	public String getPk_secret_key() {
		return pk_secret_key;
	}

	public void setPk_secret_key(String pk_secret_key) {
		this.pk_secret_key = pk_secret_key;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVversionno() {
		return vversionno;
	}

	public void setVversionno(String vversionno) {
		this.vversionno = vversionno;
	}

	public Integer getIsourcesys() {
		return isourcesys;
	}

	public void setIsourcesys(Integer isourcesys) {
		this.isourcesys = isourcesys;
	}

	public Integer getIsecrettype() {
		return isecrettype;
	}

	public void setIsecrettype(Integer isecrettype) {
		this.isecrettype = isecrettype;
	}

	public String getVsecretkey() {
		return vsecretkey;
	}

	public void setVsecretkey(String vsecretkey) {
		this.vsecretkey = vsecretkey;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELD;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return TABLE_NAME;
	}

}

package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 自定义短信签名vo信息
 * 
 *
 */
public class SmsSignatureVO extends SuperVO {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String pk_smssignature;// 主键
    
    private String vdomain;// 域名
    
    private String pk_corp;// 代账机构ID
    
    private String unitcode;// 代账机构编码
    
    private String vsigname;// 签名
    
    private String vsigncode;//
    
    private DZFBoolean isms;// 是否启用短信
    
    private String vdef1;
    
    private String vdef2;

    private Integer dr;// 删除标记
    
    private DZFDateTime ts;// 时间戳
    
    private String wxappid;//微信appid
    
    private String wxsecret;//微信密钥

    public String getVsigncode() {
        return vsigncode;
    }

    public void setVsigncode(String vsigncode) {
        this.vsigncode = vsigncode;
    }

    public String getPk_smssignature() {
        return pk_smssignature;
    }

    public void setPk_smssignature(String pk_smssignature) {
        this.pk_smssignature = pk_smssignature;
    }

    public String getVdomain() {
        return vdomain;
    }

    public void setVdomain(String vdomain) {
        this.vdomain = vdomain;
    }

    public String getUnitcode() {
        return unitcode;
    }

    public void setUnitcode(String unitcode) {
        this.unitcode = unitcode;
    }

    public String getVsigname() {
        return vsigname;
    }

    public void setVsigname(String vsigname) {
        this.vsigname = vsigname;
    }

    public DZFBoolean getIsms() {
        return isms;
    }

    public void setIsms(DZFBoolean isms) {
        this.isms = isms;
    }

    public String getVdef1() {
        return vdef1;
    }

    public void setVdef1(String vdef1) {
        this.vdef1 = vdef1;
    }

    public String getVdef2() {
        return vdef2;
    }

    public void setVdef2(String vdef2) {
        this.vdef2 = vdef2;
    }

    public String getPk_corp() {
        return pk_corp;
    }

    public void setPk_corp(String pk_corp) {
        this.pk_corp = pk_corp;
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

    public String getWxappid() {
		return wxappid;
	}

	public void setWxappid(String wxappid) {
		this.wxappid = wxappid;
	}

	public String getWxsecret() {
		return wxsecret;
	}

	public void setWxsecret(String wxsecret) {
		this.wxsecret = wxsecret;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
    public String getPKFieldName() {
        return "pk_smssignature";
    }

    @Override
    public String getParentPKFieldName() {
        return null;
    }

    @Override
    public String getTableName() {
        return "ynt_smssignature";
    }

}

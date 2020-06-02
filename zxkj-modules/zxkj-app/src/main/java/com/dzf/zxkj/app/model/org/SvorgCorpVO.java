package com.dzf.zxkj.app.model.org;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;

/**
 * 公司签约vo
 * @author ge
 *
 */
public class SvorgCorpVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	private String pk_corp;
	private String orgcode;
	private String orgname;
	private String pk_temp_corp;
	private String pk_temp_svorg;
	private String corpcode;//公司编码
	private String corpname;//公司名称
	private String corpaddr;
	private String contactman;//负责人
	private String tel;//电话
	private String username;//用户名称
	private String usercode;//用户编码
	private String pk_svorg;
	private DZFBoolean bsign;
	
	private String industry;
	private String corptype;
	
	private String phone;
	
	private Integer signtype;//签约类型 5：拒签；0：待签约；1：签约；
	
	public String chargedeptname;//公司性质
	
	private String pk_corpk;//小企业公司id(已经存在了默认代账机构，更改代账机构)
	private String qysbh;//企业识别号
	
	
	public String getPk_corpk() {
        return pk_corpk;
    }

    public void setPk_corpk(String pk_corpk) {
        this.pk_corpk = pk_corpk;
    }

    public String getChargedeptname() {
        return chargedeptname;
    }

    public void setChargedeptname(String chargedeptname) {
        this.chargedeptname = chargedeptname;
    }

    public Integer getSigntype() {
		return signtype;
	}

	public void setSigntype(Integer signtype) {
		this.signtype = signtype;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getPk_temp_svorg() {
		return pk_temp_svorg;
	}

	public void setPk_temp_svorg(String pk_temp_svorg) {
		this.pk_temp_svorg = pk_temp_svorg;
	}

	public String getOrgcode() {
		return orgcode;
	}

	public void setOrgcode(String orgcode) {
		this.orgcode = orgcode;
	}

	public String getOrgname() {
		return orgname;
	}

	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}

	public String getIndustry() {
		return industry;
	}

	public void setIndustry(String industry) {
		this.industry = industry;
	}

	public String getCorptype() {
		return corptype;
	}

	public void setCorptype(String corptype) {
		this.corptype = corptype;
	}

	public String getPk_svorg() {
		return pk_svorg;
	}

	public void setPk_svorg(String pk_svorg) {
		this.pk_svorg = pk_svorg;
	}

	public DZFBoolean getBsign() {
		return bsign;
	}

	public void setBsign(DZFBoolean bsign) {
		this.bsign = bsign;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	public String getCorpcode() {
		return corpcode;
	}

	public void setCorpcode(String corpcode) {
		this.corpcode = corpcode;
	}

	public String getCorpname() {
		return corpname;
	}

	public void setCorpname(String corpname) {
		this.corpname = corpname;
	}

	public String getCorpaddr() {
		return corpaddr;
	}

	public void setCorpaddr(String corpaddr) {
		this.corpaddr = corpaddr;
	}

	public String getContactman() {
		return contactman;
	}

	public void setContactman(String contactman) {
		this.contactman = contactman;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	@Override
	public String getPKFieldName() {
		return "pk_temp_svorg";
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return "app_temp_svorg";
	}
	
	
	public String getQysbh() {
		return qysbh;
	}

	public void setQysbh(String qysbh) {
		this.qysbh = qysbh;
	}

	public DZFBoolean getDZFBoolean(String attributeName) {
		Object obj = getAttributeValue(attributeName);
		DZFBoolean dzfboolean = DZFBoolean.FALSE;
		if (obj != null) {
			if (obj instanceof DZFBoolean)
				dzfboolean = (DZFBoolean) obj;
			else
				dzfboolean = new DZFBoolean(obj.toString());
		}
		return dzfboolean;
	}
	
}

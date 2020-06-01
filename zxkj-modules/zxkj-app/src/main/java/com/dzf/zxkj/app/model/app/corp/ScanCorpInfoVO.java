package com.dzf.zxkj.app.model.app.corp;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 扫描营业执照二维码
 * @author gejw
 * @time 2018年3月26日 上午10:10:56
 *
 */
public class ScanCorpInfoVO extends SuperVO {

	private static final long serialVersionUID = 7933010104193269235L;
	
	private String innercode;
	
	@JsonProperty("uname")
	private String unitname; // 客户名称
	
    @JsonProperty("corprhone")
    private String vcorporatephone;// 法人电话
    
    @JsonProperty("ccrecode")
    public String vsoccrecode;// 社会信用代码
    
//	  1  有限责任公司  
//	  2  个人独资企业  
//	  3  有限合伙企业  
//	  4  有限责任公司(自然人独资)  
//	  5  有限责任公司(国内合资)  
//	  6  自然人有限责任公司   
//	  7  有限责任公司(自然人投资或控股)  
//	  8  有限责任公司(外商投资企业法人独资)  
//	  9  有限责任公司(台港澳法人独资)  
//	  10  有限责任公司(中外合资)   
//	  11  有限责任公司(国有独资)   
//	  12  有限责任公司(非自然人投资或控股的法人独资)  
//	  13  其他股份有限公司(非上市)   
//	  14  其他股份有限公司分公司(非上市)  
//	  15  一人有限责任公司  
//	  16  其他有限责任公司  
//	  17  其他有限责任公司(分公司)  
//	  18  全民所有制  
//    19 其他
    @JsonProperty("comptype")
    private Integer icompanytype;
    
    @JsonProperty("bodycode")
    public String legalbodycode;// 法人代表
    
    @JsonProperty("d9")
    public String def9;//注册资本
    
    @JsonProperty("dcldate")
    public DZFDate destablishdate;// 成立日期
    
    @JsonProperty("buscope")
    private String vbusinescope;// 经营范围

    @JsonProperty("approdate")
    private DZFDate dapprovaldate;// 核准日期（发证日期）

    @JsonProperty("regans")
    private String vregistorgans;// 登记机关
    
    @JsonProperty("saleaddr")
    public String saleaddr;//住所

    @JsonProperty("comptypenm")
    private String vcompanytypenm;// 公司类型名称 
	
	public String getVcompanytypenm() {
		return vcompanytypenm;
	}

	public void setVcompanytypenm(String vcompanytypenm) {
		this.vcompanytypenm = vcompanytypenm;
	}

	public String getInnercode() {
        return innercode;
    }

    public void setInnercode(String innercode) {
        this.innercode = innercode;
    }

    public String getUnitname() {
        return unitname;
    }

    public void setUnitname(String unitname) {
        this.unitname = unitname;
    }

    public String getVcorporatephone() {
        return vcorporatephone;
    }

    public void setVcorporatephone(String vcorporatephone) {
        this.vcorporatephone = vcorporatephone;
    }

    public String getVsoccrecode() {
        return vsoccrecode;
    }

    public void setVsoccrecode(String vsoccrecode) {
        this.vsoccrecode = vsoccrecode;
    }

    public Integer getIcompanytype() {
        return icompanytype;
    }

    public void setIcompanytype(Integer icompanytype) {
        this.icompanytype = icompanytype;
    }

    public String getLegalbodycode() {
        return legalbodycode;
    }

    public void setLegalbodycode(String legalbodycode) {
        this.legalbodycode = legalbodycode;
    }

    public String getDef9() {
        return def9;
    }

    public void setDef9(String def9) {
        this.def9 = def9;
    }

    public DZFDate getDestablishdate() {
        return destablishdate;
    }

    public void setDestablishdate(DZFDate destablishdate) {
        this.destablishdate = destablishdate;
    }

    public String getVbusinescope() {
        return vbusinescope;
    }

    public void setVbusinescope(String vbusinescope) {
        this.vbusinescope = vbusinescope;
    }

    public DZFDate getDapprovaldate() {
        return dapprovaldate;
    }

    public void setDapprovaldate(DZFDate dapprovaldate) {
        this.dapprovaldate = dapprovaldate;
    }

    public String getVregistorgans() {
        return vregistorgans;
    }

    public void setVregistorgans(String vregistorgans) {
        this.vregistorgans = vregistorgans;
    }

    public String getSaleaddr() {
        return saleaddr;
    }

    public void setSaleaddr(String saleaddr) {
        this.saleaddr = saleaddr;
    }

    @Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}

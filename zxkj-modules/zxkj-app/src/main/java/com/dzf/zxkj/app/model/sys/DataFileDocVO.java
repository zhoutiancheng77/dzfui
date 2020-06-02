package com.dzf.zxkj.app.model.sys;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * <b> 资料档案(公司) </b>
 * <p>
 *     (公司)
 * </p>
 	 * 创建日期:2016-05-11 17:25:49
 * @author Administrator
 * @version DZFPrj 1.0
 */
public class DataFileDocVO extends SuperVO<DataFileDocVO> {

	private static final long serialVersionUID = 8028816853501551229L;

	@JsonProperty("id")
	private String pk_kfiledoc;//主键
	
	@JsonProperty("pk_source")
	private String pk_source;//数据来源
	
	@JsonProperty("corp")
	private String pk_corp;//公司	
	
	@JsonProperty("code")
	private String vcode;//编码
	
	@JsonProperty("name")
	private String vname;//名称
	
	@JsonProperty("memo")
	private String vmemo;//备注
	
	@JsonProperty("isseal")
	private DZFBoolean issealed;//是否封存
	
	@JsonProperty("isleaf")
	private DZFBoolean isleaf;//是否末级
	
	@JsonProperty("filelevel")
	private Integer filelevel;//层级
	
	private Integer dr;//删除标志
	
	private DZFDateTime ts;//时间戳
	
	@JsonProperty("vtypeid")
	private String pk_filetype;//资料类型主键
	
	@JsonProperty("vtypenm")
	private String filetypename;//资料类型名称
	
	@JsonProperty("itype")
	private Integer ifiletype; // ：0：原件；1：复印件；2：电子资料；3：打印资料
	
	@JsonProperty("proper")
    private Integer iproperty;//资料属性  0：普通类；1：财务类（记账凭证、财务账簿、财务报表）；
	
	@JsonProperty("only")
    private DZFBoolean isonly;//每个客户都只会有一件
	
	public static final String PK_KFILEDOC = "pk_kfiledoc";
	
	public static final String PK_SOURCE = "pk_source";
	
	public static final String PK_CORP = "pk_corp";
	
	public static final String VCODE = "vcode";
	
	public static final String VNAME = "vname";
	
	public static final String VMEMO = "vmemo";
	
	public static final String ISSEALED = "issealed";
	
	public static final String ISLEAF = "isleaf";
	
	public static final String FILELEVEL = "filelevel";
	
	public static final String DR = "dr";
	
	public static final String TS = "ts"; 
	
	public static final String PK_FILETYPE = "pk_filetype";
	
	public static final String FILETYPENAME = "filetypename";
	
	public DZFBoolean getIsonly() {
        return isonly;
    }

    public void setIsonly(DZFBoolean isonly) {
        this.isonly = isonly;
    }

    public Integer getIproperty() {
        return iproperty;
    }

    public void setIproperty(Integer iproperty) {
        this.iproperty = iproperty;
    }

    public Integer getIfiletype() {
        return ifiletype;
    }

    public void setIfiletype(Integer ifiletype) {
        this.ifiletype = ifiletype;
    }

    public String getFiletypename() {
		return filetypename;
	}

	public void setFiletypename(String filetypename) {
		this.filetypename = filetypename;
	}

	public String getPk_filetype() {
		return pk_filetype;
	}

	public void setPk_filetype(String pk_filetype) {
		this.pk_filetype = pk_filetype;
	}

	public String getPk_kfiledoc() {
		return pk_kfiledoc;
	}

	public void setPk_kfiledoc(String pk_kfiledoc) {
		this.pk_kfiledoc = pk_kfiledoc;
	}

	public String getPk_source() {
		return pk_source;
	}

	public void setPk_source(String pk_source) {
		this.pk_source = pk_source;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public DZFBoolean getIssealed() {
		return issealed;
	}

	public void setIssealed(DZFBoolean issealed) {
		this.issealed = issealed;
	}

	public DZFBoolean getIsleaf() {
		return isleaf;
	}

	public void setIsleaf(DZFBoolean isleaf) {
		this.isleaf = isleaf;
	}

	public Integer getFilelevel() {
		return filelevel;
	}

	public void setFilelevel(Integer filelevel) {
		this.filelevel = filelevel;
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

	/**
	  * <p>取得父VO主键字段.
	  * <p>
	  * 创建日期:2016-05-11 17:25:49
	  * @return java.lang.String
	  */
	public String getParentPKFieldName() {
	    return null;
	} 
	
	/**
	  * <p>取得表主键.
	  * <p>
	  * 创建日期:2016-05-11 17:25:49
	  * @return java.lang.String
	  */
	public String getPKFieldName() {
	  return "pk_kfiledoc";
	}
    
	/**
	 * <p>返回表名称.
	 * <p>
	 * 创建日期:2016-05-11 17:25:49
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "ynt_kfiledoc";
	}    
    
    /**
	  * 按照默认方式创建构造子.
	  *
	  * 创建日期:2016-05-11 17:25:49
	  */
     public DataFileDocVO() {
		super();	
	}
     
} 

package com.dzf.zxkj.platform.model.sys;

import com.dzf.zxkj.common.model.SuperVO;

/**
 * 单据编码规则参数VO
 * @author gejw
 * @time 2018年7月9日 下午2:17:17
 *
 */
@SuppressWarnings("rawtypes")
public class MaxCodeVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String pk_corp;// 会计机构--不能为空

	private String billType;//单据类型--不能为空
	
	private String fieldName;//字段名--不能为空
	
	private String tbName;//表名--不能为空
	
	private String newCode;
	
	private String vcode;
	
	private Integer diflen;
	
	private String corpIdField;//公司ID名称
	
	private String busiType;//业务大类
	
	private String pk_contcodeset; // 编号规则主键
	
	private String entryCode;//手工录入编码
	
	private String returnCode;//获取到的编码
    
    private String uuid;
    
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(String returnCode) {
        this.returnCode = returnCode;
    }
	
	public String getEntryCode() {
        return entryCode;
    }

    public void setEntryCode(String entryCode) {
        this.entryCode = entryCode;
    }

    public String getPk_contcodeset() {
        return pk_contcodeset;
    }

    public void setPk_contcodeset(String pk_contcodeset) {
        this.pk_contcodeset = pk_contcodeset;
    }

    public String getBusiType() {
        return busiType;
    }

    public void setBusiType(String busiType) {
        this.busiType = busiType;
    }

    public String getCorpIdField() {
        return corpIdField;
    }

    public void setCorpIdField(String corpIdField) {
        this.corpIdField = corpIdField;
    }

    public String getTbName() {
        return tbName;
    }

    public void setTbName(String tbName) {
        this.tbName = tbName;
    }

    public String getBillType() {
        return billType;
    }

    public void setBillType(String billType) {
        this.billType = billType;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public String getNewCode() {
        return newCode;
    }

    public void setNewCode(String newCode) {
        this.newCode = newCode;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public Integer getDiflen() {
        return diflen;
    }

    public void setDiflen(Integer diflen) {
        this.diflen = diflen;
    }

    public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

package com.dzf.zxkj.platform.model.bdset;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 数据备份VO
 * 
 * @author liubj
 *
 */
public class BackupVO extends SuperVO {
	private static final long serialVersionUID = 1L;
	@JsonProperty("id")
	private String pk_backup;
	@JsonProperty("corpid")
	private String pk_corp;
	@JsonProperty("userid")
	private String coperatorid;
	@JsonProperty("bfmc")
	private String fileName;
	@JsonProperty("path")
	private String filePath;
	@JsonProperty("size")
	private String fileSize;
	@JsonProperty("time")
	private DZFDateTime backupTime;
	private Integer dr;
	@JsonProperty("bz")
	private String vmemo;//备注

	private DZFDateTime ts;
	
	private String secretno;//加密版本号
	
	public String getSecretno() {
		return secretno;
	}

	public void setSecretno(String secretno) {
		this.secretno = secretno;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public String getPk_backup() {
		return pk_backup;
	}

	public void setPk_backup(String pk_backup) {
		this.pk_backup = pk_backup;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public DZFDateTime getBackupTime() {
		return backupTime;
	}

	public void setBackupTime(DZFDateTime backupTime) {
		this.backupTime = backupTime;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return "pk_backup";
	}

	@Override
	public String getTableName() {
		return "sys_backup";
	}

}

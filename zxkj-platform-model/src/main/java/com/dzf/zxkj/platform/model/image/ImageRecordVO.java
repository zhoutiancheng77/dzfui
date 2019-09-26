package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 审批记录信息
 * @author zhangj
 *
 */
public class ImageRecordVO extends SuperVO {

	public static final String TABLE_NAME = "ynt_image_record";
	public static final String PK_FIELDNAME = "pk_image_record";
	
	private String pk_image_record;// 主键
	private String pk_temp_corp;// 临时公司
	private String pk_corp;// 公司
	private String coperatorid;//制单人
	private String vcurapprovetor;// 当前审批人
	private String vcurrole;// 当前提交角色
	private String vnexapprovetor;// 下一审批人
	private String vnextrole;// 下一审批角色
	private DZFDateTime doperatedate;// 审批时间
	private String vapprovemsg;// 审批信息
	private String vmemo;// 备注
	private String vapproveope;// 审批操作
	private DZFDateTime ts;//
	private Integer dr;//
	private String pk_image_group;// 图片组信息
	private String pk_source_id;//来源id
	
	public String getPk_source_id() {
		return pk_source_id;
	}

	public void setPk_source_id(String pk_source_id) {
		this.pk_source_id = pk_source_id;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getPk_image_record() {
		return pk_image_record;
	}

	public void setPk_image_record(String pk_image_record) {
		this.pk_image_record = pk_image_record;
	}

	public String getPk_temp_corp() {
		return pk_temp_corp;
	}

	public void setPk_temp_corp(String pk_temp_corp) {
		this.pk_temp_corp = pk_temp_corp;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVcurapprovetor() {
		return vcurapprovetor;
	}

	public void setVcurapprovetor(String vcurapprovetor) {
		this.vcurapprovetor = vcurapprovetor;
	}

	public String getVcurrole() {
		return vcurrole;
	}

	public void setVcurrole(String vcurrole) {
		this.vcurrole = vcurrole;
	}

	public String getVnexapprovetor() {
		return vnexapprovetor;
	}

	public void setVnexapprovetor(String vnexapprovetor) {
		this.vnexapprovetor = vnexapprovetor;
	}

	public String getVnextrole() {
		return vnextrole;
	}

	public void setVnextrole(String vnextrole) {
		this.vnextrole = vnextrole;
	}

	public DZFDateTime getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDateTime doperatedate) {
		this.doperatedate = doperatedate;
	}

	public String getVapprovemsg() {
		return vapprovemsg;
	}

	public void setVapprovemsg(String vapprovemsg) {
		this.vapprovemsg = vapprovemsg;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public String getVapproveope() {
		return vapproveope;
	}

	public void setVapproveope(String vapproveope) {
		this.vapproveope = vapproveope;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public String getPk_image_group() {
		return pk_image_group;
	}

	public void setPk_image_group(String pk_image_group) {
		this.pk_image_group = pk_image_group;
	}

	@Override
	public String getPKFieldName() {
		return PK_FIELDNAME;
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

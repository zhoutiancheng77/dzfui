package com.dzf.zxkj.platform.model.image;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class OcrImageGroupVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = -288573809773095785L;
	@JsonProperty("corp")
	private String pk_corp;// 会计公司
	private DZFDateTime ts;// 时间戳
	private String coperatorid;// 制单人
	private Integer dr;// 删除标志
	@JsonProperty("ddate")
	private DZFDate doperatedate;// 制单日期
	@JsonProperty("pid")
	private String pk_image_ocrgroup;// 主键
	@JsonProperty("gcode")
	// 图片组编码（也是目录 /ImageUpload/公司编码/图片组编码/uuid.后缀）
	private String groupcode;// 组号
	private Integer imagecounts;// 组数
	private Integer istate;// 单据状态【0----初始态 1------整组失败】
	// private String custcorp;// 小微客户公司(隔板识别出)
	@JsonProperty("selcorp")
	private String pk_selectcorp;// 图片组选中所属的小微客户公司
	private DZFBoolean iscomplete; // 上传完成
	@JsonProperty("pjlxzt")
	private Integer pjlxstatus;//票据类型状态

	public String getPk_selectcorp() {
		return pk_selectcorp;
	}

	public void setPk_selectcorp(String pk_selectcorp) {
		this.pk_selectcorp = pk_selectcorp;
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

	public String getPk_image_ocrgroup() {
		return pk_image_ocrgroup;
	}

	public void setPk_image_ocrgroup(String pk_image_ocrgroup) {
		this.pk_image_ocrgroup = pk_image_ocrgroup;
	}

	public String getGroupcode() {
		return groupcode;
	}

	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}

	public Integer getImagecounts() {
		return imagecounts;
	}

	public void setImagecounts(Integer imagecounts) {
		this.imagecounts = imagecounts;
	}

	public Integer getIstate() {
		return istate;
	}

	public void setIstate(Integer istate) {
		this.istate = istate;
	}

	public DZFBoolean getIscomplete() {
		return iscomplete;
	}

	public void setIscomplete(DZFBoolean iscomplete) {
		this.iscomplete = iscomplete;
	}

	public Integer getPjlxstatus() {
		return pjlxstatus;
	}

	public void setPjlxstatus(Integer pjlxstatus) {
		this.pjlxstatus = pjlxstatus;
	}

	@Override
	public String getPKFieldName() {
		return "pk_image_ocrgroup";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_image_ocrgroup";
	}
}

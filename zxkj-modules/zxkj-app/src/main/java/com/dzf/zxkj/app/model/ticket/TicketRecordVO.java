package com.dzf.zxkj.app.model.ticket;

import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;


/**
 * 发票扫码记录
 * @author zhangj
 *
 */
public class TicketRecordVO extends SuperVO {

	public static final String TABLE_NAME = "app_zzstiket_record";
	public static final String PK_FIELD = "pk_zzstiket_record";

	private String pk_zzstiket_record;// 主键
	private String pk_zzstiket;// 票据主键
	private String user_code;// 用户编码
	private String cuserid;//用户id
	private String pk_corp;// 公司id
	private String vgfmc;// 购方名称
	private String vxfmc;// 销方名称
	private DZFDateTime dsmdate;// 扫描日期
	private String ts;
	private String dr;
	

	public String getCuserid() {
		return cuserid;
	}

	public void setCuserid(String cuserid) {
		this.cuserid = cuserid;
	}

	public String getPk_zzstiket_record() {
		return pk_zzstiket_record;
	}

	public void setPk_zzstiket_record(String pk_zzstiket_record) {
		this.pk_zzstiket_record = pk_zzstiket_record;
	}

	public String getPk_zzstiket() {
		return pk_zzstiket;
	}

	public void setPk_zzstiket(String pk_zzstiket) {
		this.pk_zzstiket = pk_zzstiket;
	}

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String user_code) {
		this.user_code = user_code;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getVgfmc() {
		return vgfmc;
	}

	public void setVgfmc(String vgfmc) {
		this.vgfmc = vgfmc;
	}

	public String getVxfmc() {
		return vxfmc;
	}

	public void setVxfmc(String vxfmc) {
		this.vxfmc = vxfmc;
	}

	public DZFDateTime getDsmdate() {
		return dsmdate;
	}

	public void setDsmdate(DZFDateTime dsmdate) {
		this.dsmdate = dsmdate;
	}

	public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getDr() {
		return dr;
	}

	public void setDr(String dr) {
		this.dr = dr;
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

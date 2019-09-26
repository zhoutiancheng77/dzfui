package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;

/**
 * 凭证合并规则
 * 
 * @author liubj
 *
 */
public class VoucherMergeSettingVO extends SuperVO {
	/**
	 * 不分组，合并为一张凭证
	 * 
	 */
	public static int GROUP_NONE = 1;
	/**
	 * 按往来单位分组合并
	 * 
	 */
	public static int GROUP_CONTACT = 2;
	/**
	 * 无往来单位时合并
	 *
	 */
	public static int NO_CONTACT_MERGE = 1;
	/**
	 * 无往来单位时不合并
	 *
	 */
	public static int NO_CONTACT_IGNORE = 2;
	/**
	 * 同方向分录合并
	 * 
	 */
	public static int ENTRY_SAME_DIRECT = 1;
	/**
	 * 同方向， 不同方向分录均合并
	 * 
	 */
	public static int ENTRY_ALL_DIRECT = 2;
	/**
	 * 自动截取摘要
	 * 
	 */
	public static int ABSTRACT_AUTO = 1;
	/**
	 * 手工填写摘要
	 * 
	 */
	public static int ABSTRACT_MANUAL = 2;

	private String pk_merge_setting;
	// 分组规则
	private Integer group_type;
	// 无往来单位凭证的合并规则
	private Integer no_contact_rule;
	// 不合并银行科目
	private DZFBoolean not_merge_bank;
	// 分录合并规则
	private Integer entry_type;
	// 摘要合并规则
	private Integer abstract_type;
	// 截断摘要
	private String cut_zy;
	// 摘要
	private String zy;

	private String pk_corp;
	private String coperatorid;
	private Integer dr;
	private DZFDateTime ts;

	public String getPk_merge_setting() {
		return pk_merge_setting;
	}

	public void setPk_merge_setting(String pk_merge_setting) {
		this.pk_merge_setting = pk_merge_setting;
	}

	public Integer getGroup_type() {
		return group_type;
	}

	public void setGroup_type(Integer group_type) {
		this.group_type = group_type;
	}

	public Integer getNo_contact_rule() {
		return no_contact_rule;
	}

	public void setNo_contact_rule(Integer no_contact_rule) {
		this.no_contact_rule = no_contact_rule;
	}

	public DZFBoolean getNot_merge_bank() {
		return not_merge_bank;
	}

	public void setNot_merge_bank(DZFBoolean not_merge_bank) {
		this.not_merge_bank = not_merge_bank;
	}

	public Integer getEntry_type() {
		return entry_type;
	}

	public void setEntry_type(Integer entry_type) {
		this.entry_type = entry_type;
	}

	public Integer getAbstract_type() {
		return abstract_type;
	}

	public void setAbstract_type(Integer abstract_type) {
		this.abstract_type = abstract_type;
	}

	public String getCut_zy() {
		return cut_zy;
	}

	public void setCut_zy(String cut_zy) {
		this.cut_zy = cut_zy;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
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
		return "pk_merge_setting";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_settings_mergepz";
	}
}

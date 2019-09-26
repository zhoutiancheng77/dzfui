package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

public class VoucherPrintParam extends SuperVO {
	// 打印模板
	private Integer type;
	// 要打印的凭证id
	private String ids;
	// 左边距
	private DZFDouble left;
	// 上边距
	private DZFDouble top;
	// 制单人类型
	private Integer zdr;
	// 制单人
	private String user_name;
	// 科目汇总级次
	private Integer account_level;
	// 汇总打印
	private DZFBoolean collect;
	// 按借贷方向分别汇总
	private DZFBoolean deb_cred;
	// 汇总金额为零不显示
	private DZFBoolean hide_zero;
	// 显示辅助核算项目
	private DZFBoolean show_auxiliary;
	// 显示辅助核算项目(不打印存货)
	private DZFBoolean show_auxiliary_noinv;
	// 显示记账人
	private DZFBoolean show_vjz;
	// 显示审核人
	private DZFBoolean show_vappr;
	// 字体
	private String font_name;
	// 打印分割线
	private DZFBoolean print_splitline;
	// 打印行次
	private Integer print_rows;
	// 辅助项设置
	private VoucherPrintAssitSetVO[] assistSetting;

	public DZFBoolean getShow_auxiliary_noinv() {
		return show_auxiliary_noinv;
	}

	public void setShow_auxiliary_noinv(DZFBoolean show_auxiliary_noinv) {
		this.show_auxiliary_noinv = show_auxiliary_noinv;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public DZFDouble getLeft() {
		return left;
	}

	public void setLeft(DZFDouble left) {
		this.left = left;
	}

	public DZFDouble getTop() {
		return top;
	}

	public void setTop(DZFDouble top) {
		this.top = top;
	}

	public Integer getZdr() {
		return zdr;
	}

	public void setZdr(Integer zdr) {
		this.zdr = zdr;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public Integer getAccount_level() {
		return account_level;
	}

	public void setAccount_level(Integer account_level) {
		this.account_level = account_level;
	}

	public DZFBoolean getCollect() {
		return collect;
	}

	public void setCollect(DZFBoolean collect) {
		this.collect = collect;
	}

	public DZFBoolean getDeb_cred() {
		return deb_cred;
	}

	public void setDeb_cred(DZFBoolean deb_cred) {
		this.deb_cred = deb_cred;
	}

	public DZFBoolean getHide_zero() {
		return hide_zero;
	}

	public void setHide_zero(DZFBoolean hide_zero) {
		this.hide_zero = hide_zero;
	}

	public DZFBoolean getShow_auxiliary() {
		return show_auxiliary;
	}

	public void setShow_auxiliary(DZFBoolean show_auxiliary) {
		this.show_auxiliary = show_auxiliary;
	}

	public DZFBoolean getShow_vjz() {
		return show_vjz;
	}

	public void setShow_vjz(DZFBoolean show_vjz) {
		this.show_vjz = show_vjz;
	}

	public DZFBoolean getShow_vappr() {
		return show_vappr;
	}

	public void setShow_vappr(DZFBoolean show_vappr) {
		this.show_vappr = show_vappr;
	}

	public String getFont_name() {
		return font_name;
	}

	public void setFont_name(String font_name) {
		this.font_name = font_name;
	}

	public DZFBoolean getPrint_splitline() {
		return print_splitline;
	}

	public void setPrint_splitline(DZFBoolean print_splitline) {
		this.print_splitline = print_splitline;
	}

	public Integer getPrint_rows() {
		return print_rows;
	}

	public void setPrint_rows(Integer print_rows) {
		this.print_rows = print_rows;
	}

	public VoucherPrintAssitSetVO[] getAssistSetting() {
		return assistSetting;
	}

	public void setAssistSetting(VoucherPrintAssitSetVO[] assistSetting) {
		this.assistSetting = assistSetting;
	}

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}

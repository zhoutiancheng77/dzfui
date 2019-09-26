package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 存货别名VO
 *  aliasname +  invtype + unit  + pk_corp 全数据库唯一索引
 *  pk_inventory  数据库not null
 */
public class InventoryAliasVO extends SuperVO {
	
	private String pk_alias;//别名id
	private String pk_inventory;//存货档案主键
	private String pk_corp;
	private String aliasname;
	private String spec;// 规格(型号)
	private String invtype;//---数据库有这个字段，界面隐藏。
	private String unit;// 计量单位
	private String unitname;// 计量单位(名称展示)
	
	private int calcmode=0;
	//计算模式 0----代表 别名单位 * 换算率 ＝ 原单位
	//。。 1------代表  别名单位 / 换算率  ＝ 原单位
	private DZFDouble hsl;//换算率
	private Integer dr;
	private DZFDateTime ts;
	private String def1;
	private String def2;
	private String def3;
	private String def4;
	private String def5;
	
	/***********数据关系展示字段 不存库*********/
	private String kmclassify;// 类别科目
    private String chukukmid;//出库科目
    private String name;//存货名称
    private String kmmc_sale;//类别科目
    private String kmmc_invcl;//销售科目
    private String kmclasscode;//类别科目编码
    private String chukukmcode;//类别科目编码
    private DZFBoolean isAdd;//是否新增存货
    private DZFBoolean isMatch;//是否已经匹配
    private String fphm;//存货匹配界面 仅展示用 不存库
    private String mid;
    
	
	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getPk_alias() {
		return pk_alias;
	}

	public void setPk_alias(String pk_alias) {
		this.pk_alias = pk_alias;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getAliasname() {
		return aliasname;
	}

	public void setAliasname(String aliasname) {
		this.aliasname = aliasname;
	}

	public String getSpec() {
		return spec;
	}

	public void setSpec(String spec) {
		this.spec = spec;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public DZFDouble getHsl() {
		return hsl;
	}

	public void setHsl(DZFDouble hsl) {
		this.hsl = hsl;
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

	public String getDef1() {
		return def1;
	}

	public void setDef1(String def1) {
		this.def1 = def1;
	}

	public String getDef2() {
		return def2;
	}

	public void setDef2(String def2) {
		this.def2 = def2;
	}

	public String getDef3() {
		return def3;
	}

	public void setDef3(String def3) {
		this.def3 = def3;
	}

	public String getDef4() {
		return def4;
	}

	public void setDef4(String def4) {
		this.def4 = def4;
	}

	public String getDef5() {
		return def5;
	}

	public void setDef5(String def5) {
		this.def5 = def5;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}
	
	public String getKmclassify() {
		return kmclassify;
	}

	public String getChukukmid() {
		return chukukmid;
	}

	public void setKmclassify(String kmclassify) {
		this.kmclassify = kmclassify;
	}

	public void setChukukmid(String chukukmid) {
		this.chukukmid = chukukmid;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getKmmc_sale() {
		return kmmc_sale;
	}

	public String getKmmc_invcl() {
		return kmmc_invcl;
	}

	public void setKmmc_sale(String kmmc_sale) {
		this.kmmc_sale = kmmc_sale;
	}

	public void setKmmc_invcl(String kmmc_invcl) {
		this.kmmc_invcl = kmmc_invcl;
	}
	
	public String getKmclasscode() {
		return kmclasscode;
	}

	public String getChukukmcode() {
		return chukukmcode;
	}

	public void setKmclasscode(String kmclasscode) {
		this.kmclasscode = kmclasscode;
	}

	public void setChukukmcode(String chukukmcode) {
		this.chukukmcode = chukukmcode;
	}

	public int getCalcmode() {
		return calcmode;
	}

	public void setCalcmode(int calcmode) {
		this.calcmode = calcmode;
	}
	
	public DZFBoolean getIsAdd() {
		return isAdd;
	}

	public DZFBoolean getIsMatch() {
		return isMatch;
	}

	public void setIsAdd(DZFBoolean isAdd) {
		this.isAdd = isAdd;
	}

	public void setIsMatch(DZFBoolean isMatch) {
		this.isMatch = isMatch;
	}
	
	public String getUnitname() {
		return unitname;
	}

	public void setUnitname(String unitname) {
		this.unitname = unitname;
	}

	@Override
	public String getPKFieldName() {
		return "pk_alias";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_icalias";
	}

}

package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.entity.ICodeName;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InventoryVO extends SuperVO implements ICodeName {
	@JsonProperty("id")
	private String pk_inventory;
	@JsonProperty("splxid")
	private String pk_invclassify;
	@JsonProperty("splxmc")
	private String invclassname;
	@JsonProperty("jldwid")
	private String pk_measure;
	@JsonProperty("jldw")
	private String measurename;
	@JsonProperty("gs")
	private String pk_corp;
	@JsonProperty("cpsn")
	private String creator;
	@JsonProperty("ctime")
	private DZFDateTime createtime;
	@JsonProperty("spbm")
	private String code;
	@JsonProperty("spmc")
	private String name;
	@JsonProperty("jc")
	private String shortname;
	@JsonProperty("gg")
	private String invspec;
//	@JsonProperty("xh")
//	private String invtype;
	@JsonProperty("bz")
	private String memo;
	@JsonProperty("fc")
	private DZFBoolean sealflag;
	@JsonProperty("extime")
	private DZFDateTime ts;
	private Integer dr;
	@JsonProperty("v1")
	private String vdef1;
	@JsonProperty("v2")
	private String vdef2;
	@JsonProperty("v3")
	private String vdef3;
	@JsonProperty("v4")
	private String vdef4;
	@JsonProperty("v5")
	private String vdef5;
	@JsonProperty("v6")
	private String vdef6;
	@JsonProperty("v7")
	private String vdef7;
	@JsonProperty("v8")
	private String vdef8;
	@JsonProperty("v9")
	private String vdef9;
	@JsonProperty("v10")
	private String vdef10;
	@JsonProperty("kmid")
	private String pk_subject;// 科目主键[必输项目]
	@JsonProperty("kmmc")
	private String kmname;// 科目名称
	@JsonProperty("kmbm")
	private String kmcode;// 科目编码
	private DZFBoolean ispage;
	@JsonProperty("jsjg")
	private DZFDouble jsprice;// 结算价格
	@JsonProperty("xslx")
	private Integer xslx;// 销售类型

	private String taxcode;// 税收编码
	private String taxname;// 货物或应税劳务、服务名称
	private String taxclassify;// 税收品目
	private DZFDouble taxratio;// 税率

	private DZFBoolean isshow;// 是否显示结存
	private DZFDouble njznum;// 结存数量
	private DZFDouble njzmny;// 结存金额
	private DZFDouble ncbprice;//成本单价
	private int calcmode=0;
	private DZFDouble hsl;//换算率
	
	public int getCalcmode() {
		return calcmode;
	}

	public void setCalcmode(int calcmode) {
		this.calcmode = calcmode;
	}

	public DZFDouble getHsl() {
		return hsl;
	}

	public void setHsl(DZFDouble hsl) {
		this.hsl = hsl;
	}

	public static String pkFieldName = "pk_inventory";

	public String getCreator() {
		return creator;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	public DZFDateTime getCreatetime() {
		return createtime;
	}

	public void setCreatetime(DZFDateTime createtime) {
		this.createtime = createtime;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public String getPk_invclassify() {
		return pk_invclassify;
	}

	public void setPk_invclassify(String pk_invclassify) {
		this.pk_invclassify = pk_invclassify;
	}

	public String getPk_measure() {
		return pk_measure;
	}

	public void setPk_measure(String pk_measure) {
		this.pk_measure = pk_measure;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getShortname() {
		return shortname;
	}

	public void setShortname(String shortname) {
		this.shortname = shortname;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

//	public String getInvtype() {
//		return invtype;
//	}
//
//	public void setInvtype(String invtype) {
//		this.invtype = invtype;
//	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public DZFBoolean getSealflag() {
		return sealflag;
	}

	public void setSealflag(DZFBoolean sealflag) {
		this.sealflag = sealflag;
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

	public String getVdef1() {
		return vdef1;
	}

	public void setVdef1(String vdef1) {
		this.vdef1 = vdef1;
	}

	public String getVdef2() {
		return vdef2;
	}

	public void setVdef2(String vdef2) {
		this.vdef2 = vdef2;
	}

	public String getVdef3() {
		return vdef3;
	}

	public void setVdef3(String vdef3) {
		this.vdef3 = vdef3;
	}

	public String getVdef4() {
		return vdef4;
	}

	public void setVdef4(String vdef4) {
		this.vdef4 = vdef4;
	}

	public String getVdef5() {
		return vdef5;
	}

	public void setVdef5(String vdef5) {
		this.vdef5 = vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public String getVdef9() {
		return vdef9;
	}

	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}

	public String getVdef10() {
		return vdef10;
	}

	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}

	public String getInvclassname() {
		return invclassname;
	}

	public void setInvclassname(String invclassname) {
		this.invclassname = invclassname;
	}

	public String getMeasurename() {
		return measurename;
	}

	public void setMeasurename(String measurename) {
		this.measurename = measurename;
	}

	public String getPk_subject() {
		return pk_subject;
	}

	public void setPk_subject(String pk_subject) {
		this.pk_subject = pk_subject;
	}

	public DZFBoolean getIspage() {
		return ispage;
	}

	public void setIspage(DZFBoolean ispage) {
		this.ispage = ispage;
	}

	public Integer getXslx() {
		return xslx;
	}

	public void setXslx(Integer xslx) {
		this.xslx = xslx;
	}

	@Override
	public String getTableName() {
		return "ynt_inventory";
	}

	@Override
	public String getPKFieldName() {
		return "pk_inventory";
	}

	@Override
	public String getParentPKFieldName() {
		return null;

	}

	public String getKmname() {
		return kmname;
	}

	public void setKmname(String kmname) {
		this.kmname = kmname;
	}

	public String getKmcode() {
		return kmcode;
	}

	public void setKmcode(String kmcode) {
		this.kmcode = kmcode;
	}

	public DZFDouble getJsprice() {
		return jsprice;
	}

	public void setJsprice(DZFDouble jsprice) {
		this.jsprice = jsprice;
	}

	public String getTaxcode() {
		return taxcode;
	}

	public void setTaxcode(String taxcode) {
		this.taxcode = taxcode;
	}

	public String getTaxname() {
		return taxname;
	}

	public void setTaxname(String taxname) {
		this.taxname = taxname;
	}

	public String getTaxclassify() {
		return taxclassify;
	}

	public void setTaxclassify(String taxclassify) {
		this.taxclassify = taxclassify;
	}

	public DZFDouble getTaxratio() {
		return taxratio;
	}

	public void setTaxratio(DZFDouble taxratio) {
		this.taxratio = taxratio;
	}

	public DZFDouble getNjznum() {
		return njznum;
	}

	public DZFDouble getNjzmny() {
		return njzmny;
	}

	public void setNjznum(DZFDouble njznum) {
		this.njznum = njznum;
	}

	public void setNjzmny(DZFDouble njzmny) {
		this.njzmny = njzmny;
	}

	public DZFBoolean getIsshow() {
		return isshow;
	}

	public void setIsshow(DZFBoolean isshow) {
		this.isshow = isshow;
	}

	public DZFDouble getNcbprice() {
		return ncbprice;
	}

	public void setNcbprice(DZFDouble ncbprice) {
		this.ncbprice = ncbprice;
	}
	
}

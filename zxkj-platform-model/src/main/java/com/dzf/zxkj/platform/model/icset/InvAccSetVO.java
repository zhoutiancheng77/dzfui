package com.dzf.zxkj.platform.model.icset;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.fasterxml.jackson.annotation.JsonProperty;

public class InvAccSetVO extends SuperVO {

	/**
	 * 存货科目关系设置表
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private String pk_invaccset;// 主键
	@JsonProperty("ztwz")
	private String cg_ztwzkm;// 在途物资科目
	@JsonProperty("yjjxs")
	private String cg_yjjxskm;// 应交进项税科目
	@JsonProperty("yfzk")
	private String cg_yfzkkm;// 应付账款科目
	@JsonProperty("xjfk")
	private String cg_xjfkkm;// 现金付款科目
	@JsonProperty("xjsk")
	private String xs_xjskkm;// 现金收款科目
	@JsonProperty("yszk")
	private String xs_yszkkm;// 应收账款科目
	@JsonProperty("yysr")
	private String xs_yysrkm;// 商品销售收入科目
	@JsonProperty("clsr")
	private String xs_clsrkm;// 材料销售收入科目
	@JsonProperty("yjxxs")
	private String xs_yjxxskm;// 应交销项税科目
	@JsonProperty("clcb")
	private String ll_clcbkm;// 材料成本科目
	@JsonProperty("ycl")
	private String ll_yclkm;// 原材料科目
	@JsonProperty("v1")
	private String vdef1;// 自定义项1
	@JsonProperty("v2")
	private String vdef2;// 自定义项2
	@JsonProperty("v3")
	private String vdef3;// 自定义项3
	@JsonProperty("v4")
	private String vdef4;// 自定义项4
	@JsonProperty("v5")
	private String vdef5;// 自定义项5
	@JsonProperty("v6")
	private String vdef6;// 自定义项6
	@JsonProperty("v7")
	private String vdef7;// 自定义项7
	@JsonProperty("v8")
	private String vdef8;// 自定义项8
	@JsonProperty("v9")
	private String vdef9;// 自定义项9
	@JsonProperty("v10")
	private String vdef10;// 自定义项10

	@JsonProperty("gsid")
	private String pk_corp;// 公司
	private Integer dr; // 删除标志
	private DZFDateTime ts; // 时间戳
	
	private int xsfkfs=1; //销售付款方式  默认往来  应收账款科目
	private int cgfkfs=1; //采购付款方式  默认往来  应付账款科目
	
	private String zgrkdfkm;//暂估入库贷方科目;
	private String zgkhfz;//供应商辅助;

	public String getZgrkdfkm() {
		return zgrkdfkm;
	}

	public String getZgkhfz() {
		return zgkhfz;
	}

	public void setZgrkdfkm(String zgrkdfkm) {
		this.zgrkdfkm = zgrkdfkm;
	}

	public void setZgkhfz(String zgkhfz) {
		this.zgkhfz = zgkhfz;
	}

	public String getPk_invaccset() {
		return pk_invaccset;
	}

	public void setPk_invaccset(String pk_invaccset) {
		this.pk_invaccset = pk_invaccset;
	}

	public String getCg_ztwzkm() {
		return cg_ztwzkm;
	}

	public void setCg_ztwzkm(String cg_ztwzkm) {
		this.cg_ztwzkm = cg_ztwzkm;
	}

	public String getCg_yjjxskm() {
		return cg_yjjxskm;
	}

	public void setCg_yjjxskm(String cg_yjjxskm) {
		this.cg_yjjxskm = cg_yjjxskm;
	}

	public String getCg_yfzkkm() {
		return cg_yfzkkm;
	}

	public void setCg_yfzkkm(String cg_yfzkkm) {
		this.cg_yfzkkm = cg_yfzkkm;
	}

	public String getCg_xjfkkm() {
		return cg_xjfkkm;
	}

	public void setCg_xjfkkm(String cg_xjfkkm) {
		this.cg_xjfkkm = cg_xjfkkm;
	}

	public String getXs_xjskkm() {
		return xs_xjskkm;
	}

	public void setXs_xjskkm(String xs_xjskkm) {
		this.xs_xjskkm = xs_xjskkm;
	}

	public String getXs_yszkkm() {
		return xs_yszkkm;
	}

	public void setXs_yszkkm(String xs_yszkkm) {
		this.xs_yszkkm = xs_yszkkm;
	}

	public String getXs_yysrkm() {
		return xs_yysrkm;
	}

	public void setXs_yysrkm(String xs_yysrkm) {
		this.xs_yysrkm = xs_yysrkm;
	}

	public String getXs_yjxxskm() {
		return xs_yjxxskm;
	}

	public void setXs_yjxxskm(String xs_yjxxskm) {
		this.xs_yjxxskm = xs_yjxxskm;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
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

	public String getLl_clcbkm() {
		return ll_clcbkm;
	}

	public void setLl_clcbkm(String ll_clcbkm) {
		this.ll_clcbkm = ll_clcbkm;
	}

	public String getLl_yclkm() {
		return ll_yclkm;
	}

	public void setLl_yclkm(String ll_yclkm) {
		this.ll_yclkm = ll_yclkm;
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
	
	public int getXsfkfs() {
		return xsfkfs;
	}

	public int getCgfkfs() {
		return cgfkfs;
	}

	public void setXsfkfs(int xsfkfs) {
		this.xsfkfs = xsfkfs;
	}

	public void setCgfkfs(int cgfkfs) {
		this.cgfkfs = cgfkfs;
	}
	
	public String getXs_clsrkm() {
		return xs_clsrkm;
	}

	public void setXs_clsrkm(String xs_clsrkm) {
		this.xs_clsrkm = xs_clsrkm;
	}

	@Override
	public String getPKFieldName() {
		return "pk_invaccset";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "bd_invaccset";
	}

}

package com.dzf.zxkj.platform.model.pzgl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.PZTaxItemRadioVO;
import com.dzf.zxkj.platform.model.sys.AddInfo;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

/**
 * <b> 填制凭证字表 </b> 创建日期:2014-09-26 12:14:28
 * 
 * @author Administrator
 * @version NCPrj 1.0
 */
@SuppressWarnings("serial")
public class TzpzBVO extends SuperVO {
	@JsonProperty("zy")
	private String zy;
	private DZFDateTime ts;
	private String vdef9;
	@JsonProperty("bid")
	private String pk_tzpz_b;
	private String vdef1;
	@JsonProperty("fx")
	private Integer vdirect;
	private String vdef8;
	@JsonProperty("kmid")
	private String pk_accsubj;
	private String vdef10;
	private String vdef2;
	private String vdef5;
	@JsonProperty("dfmny")
	// 贷方金额
	private DZFDouble dfmny;
	@JsonProperty("memo")
	private String memo;
	@JsonProperty("pid")
	private String pk_tzpz_h;
	private String vdef3;
	private String vdef6;
	@JsonProperty("rowno")
	private Integer rowno;
	private Integer dr;
	@JsonProperty("jfmny")
	// 借方金额
	private DZFDouble jfmny;
	@JsonProperty("bzid")
	// 币种ID
	private String pk_currency;
	@JsonProperty("bzcode")
	// 币种编码
	private String cur_code;
	private String vdef4;
	private String vdef7;
	// 操作日期
	@JsonProperty("optdate")
	private DZFDate doperatedate;
	// 凭证类别
	@JsonProperty("pzlb")
	private Integer pzlb;
	@JsonProperty("pzh")
	private String pzh;

	// 税目信息
	private String pk_taxitemsetting;
	private String pk_taxitem;
	private String taxcode;
	private String taxname;
	private DZFDouble taxratio;
	// 税额反算金额
	private DZFDouble taxcalmny;

	// zpm增加
	private String ppresult;
	private String dpz_date;
	private String pz_no;

	private String df_mny;
	private String jf_mny;

	private String subj_name;
	private String period;// 期间 不存库

	private String subj_code;
	private AddInfo addinfo;
	private String year;// 年度
	private String jf_qcmny;// 借方期初金额
	private String df_qcmny;// 贷方期初金额
	private String jf_bqmny;// 借方发生金额
	private String df_bqmny;// 贷方发生金额
	private String jf_qmmny;// 借方期末金额
	private String df_qmmny;// 贷方期末金额
	private String pk_corp;

	@JsonProperty("chid")
	// 存货ID
	private String pk_inventory;
	// 存货数量
	@JsonProperty("chnum")
	private DZFDouble nnumber;
	@JsonProperty("chdj")
	// 存货单价
	private DZFDouble nprice;
	@JsonProperty("nrate")
	// 汇率
	private DZFDouble nrate;
	@JsonProperty("ybjfmny")
	private DZFDouble ybjfmny;// 原币借方金额
	@JsonProperty("ybdfmny")
	private DZFDouble ybdfmny;// 原币贷方金额

	@JsonProperty("fzhsid")
	private String pk_fzhs;// 辅助核算id

	private String fzhsxm;// 辅助核算项目
	private String fzhsmc;// 辅助核算名称

	// 增加新冗余字段
	@JsonProperty("kmcode")
	private String vcode;
	@JsonProperty("kmname")
	private String vname;
	@JsonProperty("fullname")
	private String kmmchie;
	private Integer direct;
	private Integer vlevel;
	@JsonProperty("isnum")
	private DZFBoolean isnum;// 是否是数量
	// 是否外汇
	private DZFBoolean isCur;
	@JsonProperty("kmAllName")
	private String subj_allname;// 科目整合名称
	@JsonProperty("chbm")
	private String invcode;// 存货编码
	@JsonProperty("chName")
	private String invname;// 存货名称
	// 存货规格
	private String invspec;
	// 存货型号
	private String invtype;
	@JsonProperty("jldwbm")
	private String meacode;// 计量单位编码
	@JsonProperty("jldw")
	private String meaname;// 计量单位名称
	@JsonProperty("whhsid")
	private String exc_pk_currency;// 外汇核算币种主键
	@JsonProperty("whhsbm")
	private String exc_crycode;// 外汇核算币种编码
	@JsonProperty("whhslist")
	private List<BdCurrencyVO> exc_cur_array;

	// 辅助核算项改为fzhsx1(客户)～fzhsx10(自定义项4)共10个字段，分别保存各辅助核算项的具体档案(ynt_fzhs_b)的key
	@JsonProperty("fzhslist")
	private List<AuxiliaryAccountBVO> fzhs_list;
	@JsonProperty("fzhs1")
	private String fzhsx1;// 辅助核算客户
	@JsonProperty("fzhs2")
	private String fzhsx2;// 辅助核算供应商
	@JsonProperty("fzhs3")
	private String fzhsx3;// 辅助核算职员
	@JsonProperty("fzhs4")
	private String fzhsx4;// 辅助核算供项目
	@JsonProperty("fzhs5")
	private String fzhsx5;// 辅助核算部门
	@JsonProperty("fzhs6")
	private String fzhsx6;// 辅助核算存货
	@JsonProperty("fzhs7")
	private String fzhsx7;// 辅助核算自定义1
	@JsonProperty("fzhs8")
	private String fzhsx8;// 辅助核算自定义2
	@JsonProperty("fzhs9")
	private String fzhsx9;// 辅助核算自定义3
	@JsonProperty("fzhs10")
	private String fzhsx10;// 辅助核算自定义4

	private Integer fp_style;// 发票类型
	
	private String vicbillcodetype;
	private DZFDouble xsjzcb; //销售结转成本
	private DZFDouble glchhsnum;//总账存货核算数量
	private DZFDouble glcgmny; //总账采购金额

	private String fullcode;
	// 1 普票（开具的普通发票）
	// 2 专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
	// 3 未开票（指一般人未开票的收入）

	// 税表表项
	private List<PZTaxItemRadioVO> tax_items;

	private String tmpzy;//智能列表显示用
	private String tmpfullname;//智能列表显示用
	
	private DZFBoolean istaxsubj;//是否税行科目
	
	public DZFBoolean getIstaxsubj() {
		return istaxsubj;
	}

	public void setIstaxsubj(DZFBoolean istaxsubj) {
		this.istaxsubj = istaxsubj;
	}

	public String getTmpzy() {
		return tmpzy;
	}

	public void setTmpzy(String tmpzy) {
		this.tmpzy = tmpzy;
	}

	public String getTmpfullname() {
		return tmpfullname;
	}

	public void setTmpfullname(String tmpfullname) {
		this.tmpfullname = tmpfullname;
	}

	public List<PZTaxItemRadioVO> getTax_items() {
		return tax_items;
	}

	public void setTax_items(List<PZTaxItemRadioVO> tax_items) {
		this.tax_items = tax_items;
	}

	public List<AuxiliaryAccountBVO> getFzhs_list() {
		return fzhs_list;
	}

	public void setFzhs_list(List<AuxiliaryAccountBVO> fzhs_list) {
		this.fzhs_list = fzhs_list;
	}

	public String getFzhsx1() {
		return fzhsx1;
	}

	public void setFzhsx1(String fzhsx1) {
		this.fzhsx1 = fzhsx1;
	}

	public String getFzhsx2() {
		return fzhsx2;
	}

	public void setFzhsx2(String fzhsx2) {
		this.fzhsx2 = fzhsx2;
	}

	public String getFzhsx3() {
		return fzhsx3;
	}

	public void setFzhsx3(String fzhsx3) {
		this.fzhsx3 = fzhsx3;
	}

	public String getFzhsx4() {
		return fzhsx4;
	}

	public void setFzhsx4(String fzhsx4) {
		this.fzhsx4 = fzhsx4;
	}

	public String getFzhsx5() {
		return fzhsx5;
	}

	public void setFzhsx5(String fzhsx5) {
		this.fzhsx5 = fzhsx5;
	}

	public String getFzhsx6() {
		return fzhsx6;
	}

	public void setFzhsx6(String fzhsx6) {
		this.fzhsx6 = fzhsx6;
	}

	public String getFzhsx7() {
		return fzhsx7;
	}

	public void setFzhsx7(String fzhsx7) {
		this.fzhsx7 = fzhsx7;
	}

	public String getFzhsx8() {
		return fzhsx8;
	}

	public void setFzhsx8(String fzhsx8) {
		this.fzhsx8 = fzhsx8;
	}

	public String getFzhsx9() {
		return fzhsx9;
	}

	public void setFzhsx9(String fzhsx9) {
		this.fzhsx9 = fzhsx9;
	}

	public String getFzhsx10() {
		return fzhsx10;
	}

	public void setFzhsx10(String fzhsx10) {
		this.fzhsx10 = fzhsx10;
	}

	public List<BdCurrencyVO> getExc_cur_array() {
		String[] pk_cur = getExc_pk_currency() != null ? getExc_pk_currency()
				.split(",") : null;
		String[] code_cur = getExc_crycode() != null ? getExc_crycode().split(
				",") : null;
		if (code_cur != null && pk_cur != null && code_cur.length > 0
				&& code_cur.length == pk_cur.length) {
			exc_cur_array = new ArrayList<BdCurrencyVO>();
			for (int i = 0; i < pk_cur.length; i++) {
				BdCurrencyVO v = new BdCurrencyVO();
				v.setPk_currency(pk_cur[i]);
				v.setCurrencycode(code_cur[i]);
				exc_cur_array.add(v);
			}
		}
		return exc_cur_array;
	}

	public void setExc_cur_array(List<BdCurrencyVO> exc_cur_array) {
		this.exc_cur_array = exc_cur_array;
	}

	public static final String ZY = "zy";
	public static final String VDEF9 = "vdef9";
	public static final String PK_TZPZ_B = "pk_tzpz_b";
	public static final String VDEF1 = "vdef1";
	public static final String VDIRECT = "vdirect";
	public static final String VDEF8 = "vdef8";
	public static final String PK_ACCSUBJ = "pk_accsubj";
	public static final String VDEF10 = "vdef10";
	public static final String VDEF2 = "vdef2";
	public static final String VDEF5 = "vdef5";
	public static final String DFMNY = "dfmny";
	public static final String MEMO = "memo";
	public static final String PK_TZPZ_H = "pk_tzpz_h";
	public static final String VDEF3 = "vdef3";
	public static final String VDEF6 = "vdef6";
	public static final String ROWNO = "rowno";
	public static final String JFMNY = "jfmny";
	public static final String PK_CURRENCY = "pk_currency";
	public static final String VDEF4 = "vdef4";
	public static final String VDEF7 = "vdef7";

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	/**
	 * 属性zy的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getZy() {
		return zy;
	}

	/**
	 * 属性zy的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newZy
	 *            String
	 */
	public void setZy(String newZy) {
		this.zy = newZy;
	}

	/**
	 * 属性ts的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDateTime
	 */
	public DZFDateTime getTs() {
		return ts;
	}

	/**
	 * 属性ts的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newTs
	 *            DZFDateTime
	 */
	public void setTs(DZFDateTime newTs) {
		this.ts = newTs;
	}

	/**
	 * 属性vdef9的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef9() {
		return vdef9;
	}

	/**
	 * 属性vdef9的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef9
	 *            String
	 */
	public void setVdef9(String newVdef9) {
		this.vdef9 = newVdef9;
	}

	/**
	 * 属性pk_tzpz_b的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_tzpz_b() {
		return pk_tzpz_b;
	}

	/**
	 * 属性pk_tzpz_b的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_tzpz_b
	 *            String
	 */
	public void setPk_tzpz_b(String newPk_tzpz_b) {
		this.pk_tzpz_b = newPk_tzpz_b;
	}

	/**
	 * 属性vdef1的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef1() {
		return vdef1;
	}

	/**
	 * 属性vdef1的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef1
	 *            String
	 */
	public void setVdef1(String newVdef1) {
		this.vdef1 = newVdef1;
	}

	/**
	 * 属性vdirect的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public Integer getVdirect() {
		return vdirect;
	}

	/**
	 * 属性vdirect的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdirect
	 *            DZFDouble
	 */
	public void setVdirect(Integer newVdirect) {
		this.vdirect = newVdirect;
	}

	/**
	 * 属性vdef8的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef8() {
		return vdef8;
	}

	/**
	 * 属性vdef8的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef8
	 *            String
	 */
	public void setVdef8(String newVdef8) {
		this.vdef8 = newVdef8;
	}

	/**
	 * 属性pk_accsubj的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_accsubj() {
		return pk_accsubj;
	}

	/**
	 * 属性pk_accsubj的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_accsubj
	 *            String
	 */
	public void setPk_accsubj(String newPk_accsubj) {
		this.pk_accsubj = newPk_accsubj;
	}

	/**
	 * 属性vdef10的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef10() {
		return vdef10;
	}

	/**
	 * 属性vdef10的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef10
	 *            String
	 */
	public void setVdef10(String newVdef10) {
		this.vdef10 = newVdef10;
	}

	/**
	 * 属性vdef2的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef2() {
		return vdef2;
	}

	/**
	 * 属性vdef2的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef2
	 *            String
	 */
	public void setVdef2(String newVdef2) {
		this.vdef2 = newVdef2;
	}

	/**
	 * 属性vdef5的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef5() {
		return vdef5;
	}

	/**
	 * 属性vdef5的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef5
	 *            String
	 */
	public void setVdef5(String newVdef5) {
		this.vdef5 = newVdef5;
	}

	/**
	 * 属性dfmny的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getDfmny() {
		return dfmny;
	}

	/**
	 * 属性dfmny的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newDfmny
	 *            DZFDouble
	 */
	public void setDfmny(DZFDouble newDfmny) {
		this.dfmny = newDfmny;
	}

	/**
	 * 属性memo的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getMemo() {
		return memo;
	}

	/**
	 * 属性memo的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newMemo
	 *            String
	 */
	public void setMemo(String newMemo) {
		this.memo = newMemo;
	}

	/**
	 * 属性pk_tzpz_h的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}

	/**
	 * 属性pk_tzpz_h的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_tzpz_h
	 *            String
	 */
	public void setPk_tzpz_h(String newPk_tzpz_h) {
		this.pk_tzpz_h = newPk_tzpz_h;
	}

	/**
	 * 属性vdef3的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef3() {
		return vdef3;
	}

	/**
	 * 属性vdef3的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef3
	 *            String
	 */
	public void setVdef3(String newVdef3) {
		this.vdef3 = newVdef3;
	}

	/**
	 * 属性vdef6的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef6() {
		return vdef6;
	}

	/**
	 * 属性vdef6的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef6
	 *            String
	 */
	public void setVdef6(String newVdef6) {
		this.vdef6 = newVdef6;
	}

	/**
	 * 属性rowno的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public Integer getRowno() {
		return rowno;
	}

	/**
	 * 属性rowno的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newRowno
	 *            DZFDouble
	 */
	public void setRowno(Integer newRowno) {
		this.rowno = newRowno;
	}

	/**
	 * 属性dr的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public Integer getDr() {
		return dr;
	}

	/**
	 * 属性dr的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newDr
	 *            DZFDouble
	 */
	public void setDr(Integer newDr) {
		this.dr = newDr;
	}

	/**
	 * 属性jfmny的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return DZFDouble
	 */
	public DZFDouble getJfmny() {
		return jfmny;
	}

	/**
	 * 属性jfmny的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newJfmny
	 *            DZFDouble
	 */
	public void setJfmny(DZFDouble newJfmny) {
		this.jfmny = newJfmny;
	}

	/**
	 * 属性pk_currency的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getPk_currency() {
		return pk_currency;
	}

	/**
	 * 属性pk_currency的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newPk_currency
	 *            String
	 */
	public void setPk_currency(String newPk_currency) {
		this.pk_currency = newPk_currency;
	}

	/**
	 * 属性vdef4的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef4() {
		return vdef4;
	}

	/**
	 * 属性vdef4的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef4
	 *            String
	 */
	public void setVdef4(String newVdef4) {
		this.vdef4 = newVdef4;
	}

	/**
	 * 属性vdef7的Getter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @return String
	 */
	public String getVdef7() {
		return vdef7;
	}

	/**
	 * 属性vdef7的Setter方法. 创建日期:2014-09-26 12:14:28
	 * 
	 * @param newVdef7
	 *            String
	 */
	public void setVdef7(String newVdef7) {
		this.vdef7 = newVdef7;
	}

	/**
	 * <p>
	 * 取得父VO主键字段.
	 * <p>
	 * 创建日期:2014-09-26 12:14:28
	 * 
	 * @return java.lang.String
	 */
	public String getParentPKFieldName() {
		return "pk_tzpz_h";
	}

	/**
	 * <p>
	 * 取得表主键.
	 * <p>
	 * 创建日期:2014-09-26 12:14:28
	 *
	 * @return java.lang.String
	 */
	public String getPKFieldName() {
		return "pk_tzpz_b";
	}

	/**
	 * <p>
	 * 返回表名称.
	 * <p>
	 * 创建日期:2014-09-26 12:14:28
	 *
	 * @return java.lang.String
	 */
	public String getTableName() {
		return "YNT_TZPZ_B";
	}

	public String getPpresult() {
		return ppresult;
	}

	public void setPpresult(String ppresult) {
		this.ppresult = ppresult;
	}

	public String getDpz_date() {
		return dpz_date;
	}

	public void setDpz_date(String dpz_date) {
		this.dpz_date = dpz_date;
	}

	public String getPz_no() {
		return pz_no;
	}

	public void setPz_no(String pz_no) {
		this.pz_no = pz_no;
	}

	public String getDf_mny() {
		return df_mny;
	}

	public void setDf_mny(String df_mny) {
		this.df_mny = df_mny;
	}

	public String getJf_mny() {
		return jf_mny;
	}

	public void setJf_mny(String jf_mny) {
		this.jf_mny = jf_mny;
	}

	public String getSubj_name() {
		return subj_name;
	}

	public void setSubj_name(String subj_name) {
		this.subj_name = subj_name;
	}

	public String getSubj_code() {
		return subj_code;
	}

	public void setSubj_code(String subj_code) {
		this.subj_code = subj_code;
	}

	public DZFDate getDoperatedate() {
		return doperatedate;
	}

	public void setDoperatedate(DZFDate doperatedate) {
		this.doperatedate = doperatedate;
	}

	public Integer getPzlb() {
		return pzlb;
	}

	public void setPzlb(Integer pzlb) {
		this.pzlb = pzlb;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getPk_taxitemsetting() {
		return pk_taxitemsetting;
	}

	public void setPk_taxitemsetting(String pk_taxitemsetting) {
		this.pk_taxitemsetting = pk_taxitemsetting;
	}

	public String getPk_taxitem() {
		return pk_taxitem;
	}

	public void setPk_taxitem(String pk_taxitem) {
		this.pk_taxitem = pk_taxitem;
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

	public DZFDouble getTaxratio() {
		return taxratio;
	}

	public void setTaxratio(DZFDouble taxratio) {
		this.taxratio = taxratio;
	}

	public DZFDouble getTaxcalmny() {
		return taxcalmny;
	}

	public void setTaxcalmny(DZFDouble taxcalmny) {
		this.taxcalmny = taxcalmny;
	}

	public AddInfo getAddinfo() {
		return addinfo;
	}

	public void setAddinfo(AddInfo addinfo) {
		this.addinfo = addinfo;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getJf_qcmny() {
		return jf_qcmny;
	}

	public void setJf_qcmny(String jf_qcmny) {
		this.jf_qcmny = jf_qcmny;
	}

	public String getDf_qcmny() {
		return df_qcmny;
	}

	public void setDf_qcmny(String df_qcmny) {
		this.df_qcmny = df_qcmny;
	}

	public String getJf_bqmny() {
		return jf_bqmny;
	}

	public void setJf_bqmny(String jf_bqmny) {
		this.jf_bqmny = jf_bqmny;
	}

	public String getDf_bqmny() {
		return df_bqmny;
	}

	public void setDf_bqmny(String df_bqmny) {
		this.df_bqmny = df_bqmny;
	}

	public String getJf_qmmny() {
		return jf_qmmny;
	}

	public void setJf_qmmny(String jf_qmmny) {
		this.jf_qmmny = jf_qmmny;
	}

	public String getDf_qmmny() {
		return df_qmmny;
	}

	public void setDf_qmmny(String df_qmmny) {
		this.df_qmmny = df_qmmny;
	}

	/**
	 * 按照默认方式创建构造子.
	 * 
	 * 创建日期:2014-09-26 12:14:28
	 */
	public TzpzBVO() {
		super();
	}

	public String getVcode() {
		return vcode;
	}

	public void setVcode(String vcode) {
		this.vcode = vcode;
	}

	public String getVname() {
		return vname;
	}

	public void setVname(String vname) {
		this.vname = vname;
	}

	public Integer getDirect() {
		return direct;
	}

	public void setDirect(Integer direct) {
		this.direct = direct;
	}

	public Integer getVlevel() {
		return vlevel;
	}

	public void setVlevel(Integer vlevel) {
		this.vlevel = vlevel;
	}

	public DZFDouble getNnumber() {
		return nnumber;
	}

	public void setNnumber(DZFDouble nnumber) {
		this.nnumber = nnumber;
	}

	public DZFDouble getNprice() {
		return nprice;
	}

	public void setNprice(DZFDouble nprice) {
		this.nprice = nprice;
	}

	public DZFDouble getNrate() {
		return nrate;
	}

	public void setNrate(DZFDouble nrate) {
		this.nrate = nrate;
	}

	public DZFDouble getYbjfmny() {
		return ybjfmny;
	}

	public void setYbjfmny(DZFDouble ybjfmny) {
		this.ybjfmny = ybjfmny;
	}

	public DZFDouble getYbdfmny() {
		return ybdfmny;
	}

	public void setYbdfmny(DZFDouble ybdfmny) {
		this.ybdfmny = ybdfmny;
	}

	public String getPk_inventory() {
		return pk_inventory;
	}

	public void setPk_inventory(String pk_inventory) {
		this.pk_inventory = pk_inventory;
	}

	public DZFBoolean getIsnum() {
		return isnum;
	}

	public void setIsnum(DZFBoolean isnum) {
		this.isnum = isnum;
	}

	public String getSubj_allname() {
		return subj_allname;
	}

	public void setSubj_allname(String subj_allname) {
		this.subj_allname = subj_allname;
	}

	public String getInvcode() {
		return invcode;
	}

	public void setInvcode(String invcode) {
		this.invcode = invcode;
	}

	public String getInvname() {
		return invname;
	}

	public void setInvname(String invname) {
		this.invname = invname;
	}

	public String getInvspec() {
		return invspec;
	}

	public void setInvspec(String invspec) {
		this.invspec = invspec;
	}

	public String getInvtype() {
		return invtype;
	}

	public void setInvtype(String invtype) {
		this.invtype = invtype;
	}

	public String getMeacode() {
		return meacode;
	}

	public void setMeacode(String meacode) {
		this.meacode = meacode;
	}

	public String getMeaname() {
		return meaname;
	}

	public void setMeaname(String meaname) {
		this.meaname = meaname;
	}

	public String getCur_code() {
		// if(getPk_currency() == null)
		// setPk_currency(IGlobalConstants.RMB_currency_id);
		// BdCurrencyVO cvo = CurrencyCache.getInstance().get(null,
		// getPk_corp(), getPk_currency());
		// if(cvo != null) cur_code = cvo.getCurrencycode();
		return cur_code;
	}

	public void setCur_code(String cur_code) {
		this.cur_code = cur_code;
	}

	public String getExc_pk_currency() {
		return exc_pk_currency;
	}

	public void setExc_pk_currency(String exc_pk_currency) {
		this.exc_pk_currency = exc_pk_currency;
	}

	public String getExc_crycode() {
		return exc_crycode;
	}

	public void setExc_crycode(String exc_crycode) {
		this.exc_crycode = exc_crycode;
	}

	public DZFBoolean getIsCur() {
		isCur = pk_currency != null
				&& !pk_currency.equals(IGlobalConstants.RMB_currency_id) ? DZFBoolean.TRUE
				: DZFBoolean.FALSE;
		return isCur;
	}

	public void setIsCur(DZFBoolean isCur) {
		this.isCur = isCur;
	}

	public String getKmmchie() {
		return kmmchie;
	}

	public void setKmmchie(String kmmchie) {
		this.kmmchie = kmmchie;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_fzhs() {
		return pk_fzhs;
	}

	public void setPk_fzhs(String pk_fzhs) {
		this.pk_fzhs = pk_fzhs;
	}

	public String getFzhsxm() {
		return fzhsxm;
	}

	public void setFzhsxm(String fzhsxm) {
		this.fzhsxm = fzhsxm;
	}

	public String getFzhsmc() {
		return fzhsmc;
	}

	public void setFzhsmc(String fzhsmc) {
		this.fzhsmc = fzhsmc;
	}

	public Integer getFp_style() {
		return fp_style;
	}

	public void setFp_style(Integer fp_style) {
		this.fp_style = fp_style;
	}

	public String getVicbillcodetype() {
		return vicbillcodetype;
	}

	public void setVicbillcodetype(String vicbillcodetype) {
		this.vicbillcodetype = vicbillcodetype;
	}

	public DZFDouble getXsjzcb() {
		return xsjzcb;
	}

	public void setXsjzcb(DZFDouble xsjzcb) {
		this.xsjzcb = xsjzcb;
	}

	public DZFDouble getGlchhsnum() {
		return glchhsnum;
	}

	public void setGlchhsnum(DZFDouble glchhsnum) {
		this.glchhsnum = glchhsnum;
	}

	public DZFDouble getGlcgmny() {
		return glcgmny;
	}

	public void setGlcgmny(DZFDouble glcgmny) {
		this.glcgmny = glcgmny;
	}

	public String getFullcode() {
		return fullcode;
	}

	public void setFullcode(String fullcode) {
		this.fullcode = fullcode;
	}
	
}
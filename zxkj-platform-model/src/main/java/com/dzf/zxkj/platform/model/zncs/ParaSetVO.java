package com.dzf.zxkj.platform.model.zncs;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 智能财税
 * 公司参数设置表
 * @author mfz
 *
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class ParaSetVO extends SuperVO {
	
	@JsonProperty("id")
	private String pk_para_set;//主键
	@JsonProperty("corpid")
	private String pk_corp;	
	@JsonProperty("srfl")
	private Integer incomeclass;//收入分类0无2按客户分类
	@JsonProperty("isyh")
	private DZFBoolean bankbillbyacc;//银行票据按账户分组
	@JsonProperty("isrz")
	private DZFBoolean invidentify;//增值税发票进项票据标为认证
	@JsonProperty("iszpb")
	private DZFBoolean voucherqfzpp;//凭证区分专普票
	@JsonProperty("pzrq")
	private Integer voucherdate;//凭证日期0票据实际日期1当前账期最后一天
	@JsonProperty("iscwvhr")
	private DZFBoolean errorvoucher;//错误票据是否生成凭证
	@JsonProperty("ishbfl")
	private DZFBoolean ismergedetail;//凭证是否合并分录
	@JsonProperty("flpx")
	private Integer orderdetail;//分录排序 0：不排序 1 先借 
	@JsonProperty("pjhbsl")
	private Integer mergebillnum;//票据合并数量
	@JsonProperty("ishbsr")
	private DZFBoolean ismergeincome;//收入分类是否合并
	@JsonProperty("ishbkc")
	private DZFBoolean ismergeic;//库存采购分类是否合并
	@JsonProperty("ishbyh")
	private DZFBoolean ismergebank;//银行票据分类是否合并
	@JsonProperty("cgfz")
	private DZFBoolean purchclass;//采购按往来客户分组
	@JsonProperty("cbfz")
	private DZFBoolean costclass;//成本按往来客户分组
	@JsonProperty("yhhb")
	private DZFBoolean bankinoutclass;//银行转入转出分类合并凭证时按往来客户分组
	@JsonProperty("ncpsl")
	private String ncpsl;//农产品税率
	
	
	public String getNcpsl() {
		return ncpsl;
	}

	public void setNcpsl(String ncpsl) {
		this.ncpsl = ncpsl;
	}

	public DZFBoolean getPurchclass() {
		return purchclass;
	}

	public void setPurchclass(DZFBoolean purchclass) {
		this.purchclass = purchclass;
	}

	public DZFBoolean getCostclass() {
		return costclass;
	}

	public void setCostclass(DZFBoolean costclass) {
		this.costclass = costclass;
	}

	public DZFBoolean getBankinoutclass() {
		return bankinoutclass;
	}

	public void setBankinoutclass(DZFBoolean bankinoutclass) {
		this.bankinoutclass = bankinoutclass;
	}

	public Integer getMergebillnum() {
		return mergebillnum;
	}

	public void setMergebillnum(Integer mergebillnum) {
		this.mergebillnum = mergebillnum;
	}

	public DZFBoolean getIsmergeincome() {
		return ismergeincome;
	}

	public void setIsmergeincome(DZFBoolean ismergeincome) {
		this.ismergeincome = ismergeincome;
	}

	public DZFBoolean getIsmergeic() {
		return ismergeic;
	}

	public void setIsmergeic(DZFBoolean ismergeic) {
		this.ismergeic = ismergeic;
	}

	public DZFBoolean getIsmergebank() {
		return ismergebank;
	}

	public void setIsmergebank(DZFBoolean ismergebank) {
		this.ismergebank = ismergebank;
	}

	public DZFBoolean getIsmergedetail() {
		return ismergedetail;
	}

	public void setIsmergedetail(DZFBoolean ismergedetail) {
		this.ismergedetail = ismergedetail;
	}

	public Integer getOrderdetail() {
		return orderdetail;
	}

	public void setOrderdetail(Integer orderdetail) {
		this.orderdetail = orderdetail;
	}

	public DZFBoolean getErrorvoucher() {
		return errorvoucher;
	}

	public void setErrorvoucher(DZFBoolean errorvoucher) {
		this.errorvoucher = errorvoucher;
	}

	private Integer dr;
	private DZFDateTime ts;
	

	public Integer getVoucherdate() {
		return voucherdate;
	}

	public void setVoucherdate(Integer voucherdate) {
		this.voucherdate = voucherdate;
	}

	public String getPk_para_set() {
		return pk_para_set;
	}

	public void setPk_para_set(String pk_para_set) {
		this.pk_para_set = pk_para_set;
	}

	public Integer getIncomeclass() {
		return incomeclass;
	}

	public void setIncomeclass(Integer incomeclass) {
		this.incomeclass = incomeclass;
	}

	public DZFBoolean getBankbillbyacc() {
		return bankbillbyacc;
	}

	public void setBankbillbyacc(DZFBoolean bankbillbyacc) {
		this.bankbillbyacc = bankbillbyacc;
	}

	public DZFBoolean getInvidentify() {
		return invidentify;
	}

	public void setInvidentify(DZFBoolean invidentify) {
		this.invidentify = invidentify;
	}

	public DZFBoolean getVoucherqfzpp() {
		return voucherqfzpp;
	}

	public void setVoucherqfzpp(DZFBoolean voucherqfzpp) {
		this.voucherqfzpp = voucherqfzpp;
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

	@Override
	public String getPKFieldName() {
		return "pk_para_set";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_para_set";
	}
	
	
}

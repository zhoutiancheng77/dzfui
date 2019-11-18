package com.dzf.zxkj.platform.model.tax.workbench;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

@SuppressWarnings("rawtypes")
public class BsWorkbenchVO extends SuperVO {

	private static final long serialVersionUID = 8683527125269132368L;

	@JsonProperty("id")
	private String pk_workbench;//主键

	@JsonProperty("fcorp")
	public String fathercorp;//会计公司主键
	
	@JsonProperty("khid")
	private String pk_corp;//客户主键
	
	private String period;//期间
	
	@JsonProperty("khbm")
	private String khCode;// 客户编码（仅作展示）

	@JsonProperty("khmc")
	private String khName;// 客户名称（仅作展示）
	
	@JsonProperty("chname")
	private String chargedeptname;// 纳税人资格
	
	@JsonProperty("pcount")
	public String pcountname;// 主办会计名称
	
	@JsonProperty("pcountid")
	public String vsuperaccount;// 主办会计主键
	
	@JsonProperty("spzt")
	private Integer isptx;//送票： 0：未勾选；  1勾选；
	
	@JsonProperty("accheck")
	private Integer iacctcheck;//记账完成： 0：未勾选；  1勾选；
	
	@JsonProperty("cszt")
	private Integer taxStateCopy;//抄税： 0：未勾选；  1勾选；
	
	@JsonProperty("taxconsta")
	private Integer itaxconfstate;//税款确认状态 ：   没发送的为空，0：待确认（等待小企业主回复）；
	//1：已确认（企业主已确认并账户有余额）:2：有异议（企业对税款有异议，不管余额有没有）；
	
	@JsonProperty("wczt")
	private Integer taxStateFinish;//申报完成： 0：未勾选；  1勾选；
	
	@JsonProperty("qkzt")
	private Integer taxStateClean;//可清卡： 0：未勾选；  1勾选；

	@JsonProperty("pzjjzt")
	private Integer ipzjjzt;// （报税状态）凭证交接： 0：未勾选；  1勾选；

	@JsonProperty("iszerodec")
	private DZFBoolean isZeroDeclare;//零申报

	private DZFDouble income;// 收入
	
	@JsonProperty("erstat")
	private Integer erningStatus;//财报
	
	@JsonProperty("paymny")
	private DZFDouble npaymny;//应缴合计
	
	@JsonProperty("paidmny")
	private DZFDouble npaidmny;//实缴合计
	
	private String memo;//备注
	
	//状态常量：0：已提交；1：受理失败；2：受理成功；3：申报失败；4：申报成功；5：作废；6：缴款失败；7：缴款成功；101：未提交；98：未填写；99：已填写；

	@JsonProperty("zzs")
	private DZFDouble addTax;// 增值税（应缴）
	
	@JsonProperty("zzspaid")
	private DZFDouble addpaidTax;// 增值税（实缴）
	
	@JsonProperty("zzsstat")
	private Integer addStatus;// 增值税（实缴）状态
	
	@JsonProperty("addittax")
	private DZFDouble additionalTax;//附加税合计（应缴）
	
	@JsonProperty("addittaxpaid")
	private DZFDouble additionalpaidTax;//附加税合计（实缴）
	
	@JsonProperty("addittaxstat")
	private Integer additionalStatus;//附加税合计（实缴）状态
	
	@JsonProperty("cjs")
	private DZFDouble cityTax;// 城建税（应缴）
	
	@JsonProperty("cjspaid")
	private DZFDouble citypaidTax;// 城建税（实缴）
	
	@JsonProperty("cjsstat")
	private Integer cityStatus;// 城建税（实缴）状态
	
	@JsonProperty("jyffj")
	private DZFDouble educaTax;// 教育费附加（应缴）
	
	@JsonProperty("jyffjpaid")
	private DZFDouble educapaidTax;// 教育费附加（实缴）
	
	@JsonProperty("jyffjstat")
	private Integer educaStatus;// 教育费附加（实缴）状态
	
	@JsonProperty("dfjyf")
	private DZFDouble localEducaTax;// 地方教育费附加（应缴）
	
	@JsonProperty("dfjyfpaid")
	private DZFDouble localEducapaidTax;// 地方教育费附加（实缴）
	
	@JsonProperty("dfjyfstat")
	private Integer localEducaStatus;// 地方教育费附加（实缴）状态
	
	@JsonProperty("sds")
	private DZFDouble incomeTax;// 企业所得税（应缴）
	
	@JsonProperty("sdspaid")
	private DZFDouble incomepaidTax;// 企业所得税（实缴）
	
	@JsonProperty("sdsstat")
	private Integer incomeStatus;// 企业所得税（实缴）状态
	
	@JsonProperty("xfs")
	private DZFDouble exciseTax;// 消费税（应缴）
	
	@JsonProperty("xfspaid")
	private DZFDouble excisepaidTax;// 消费税（实缴）
	
	@JsonProperty("xfsstat")
	private Integer exciseStatus;// 消费税（实缴）状态
	
	@JsonProperty("cultax")
	private DZFDouble culturalTax;//文化事业建设费（应缴）
	
	@JsonProperty("cultaxpaid")
	private DZFDouble culturalpaidTax;//文化事业建设费（实缴）
	
	@JsonProperty("cultaxstat")
	private Integer culturalStatus;//文化事业建设费（实缴）状态
	
	@JsonProperty("stamptax")
	private DZFDouble stampTax;//印花税（应缴）

	@JsonProperty("stamppaidtax")
	private DZFDouble stamppaidTax;//印花税（实缴）
	
	@JsonProperty("stampstat")
	private Integer stampStatus;//印花税（实缴）状态
	
	@JsonProperty("grsds")
	private DZFDouble personTax;// 个人所得税（应缴）
	
	@JsonProperty("grsdspaid")
	private DZFDouble personpaidTax;// 个人所得税（实缴）

	@JsonProperty("grsdsstat")
	private Integer personStatus;// 个人所得税（实缴）状态
	
	@JsonProperty("yys")
	private DZFDouble salesTax;// 营业税（暂未使用）
	
	@JsonProperty("operatorid")
	private String coperatorid; // 制单人
	
	private Integer dr;
	
	@JsonProperty("jzrq")
	private DZFDate begindate;//（不存库）
	
	@JsonProperty("rule")
	private String coderule;//（不存库）

	@JsonProperty("rbday")
	private Integer rembday;// 提醒开始日期（不存库）
	
    @JsonProperty("ovince")
    public Integer vprovince;// 省（不存库）
	
	@JsonProperty("msg")
	private String vmsg;//接口返回信息（不存库）
	
	public DZFDouble getNpaymny() {
		return npaymny;
	}

	public void setNpaymny(DZFDouble npaymny) {
		this.npaymny = npaymny;
	}

	public DZFDouble getNpaidmny() {
		return npaidmny;
	}

	public void setNpaidmny(DZFDouble npaidmny) {
		this.npaidmny = npaidmny;
	}

	public String getVmsg() {
		return vmsg;
	}

	public DZFDouble getStampTax() {
		return stampTax;
	}

	public void setStampTax(DZFDouble stampTax) {
		this.stampTax = stampTax;
	}

	public DZFDouble getStamppaidTax() {
		return stamppaidTax;
	}

	public void setStamppaidTax(DZFDouble stamppaidTax) {
		this.stamppaidTax = stamppaidTax;
	}

	public Integer getStampStatus() {
		return stampStatus;
	}

	public void setStampStatus(Integer stampStatus) {
		this.stampStatus = stampStatus;
	}

	public Integer getVprovince() {
		return vprovince;
	}

	public void setVprovince(Integer vprovince) {
		this.vprovince = vprovince;
	}

	public Integer getIacctcheck() {
		return iacctcheck;
	}

	public void setIacctcheck(Integer iacctcheck) {
		this.iacctcheck = iacctcheck;
	}

	public String getFathercorp() {
		return fathercorp;
	}

	public void setFathercorp(String fathercorp) {
		this.fathercorp = fathercorp;
	}

	public Integer getItaxconfstate() {
		return itaxconfstate;
	}

	public void setItaxconfstate(Integer itaxconfstate) {
		this.itaxconfstate = itaxconfstate;
	}

	public void setVmsg(String vmsg) {
		this.vmsg = vmsg;
	}

	public Integer getErningStatus() {
		return erningStatus;
	}

	public void setErningStatus(Integer erningStatus) {
		this.erningStatus = erningStatus;
	}

	public DZFBoolean getIsZeroDeclare() {
		return isZeroDeclare;
	}

	public void setIsZeroDeclare(DZFBoolean isZeroDeclare) {
		this.isZeroDeclare = isZeroDeclare;
	}

	public DZFDouble getCulturalTax() {
		return culturalTax;
	}

	public void setCulturalTax(DZFDouble culturalTax) {
		this.culturalTax = culturalTax;
	}

	public DZFDouble getAdditionalTax() {
		return additionalTax;
	}

	public void setAdditionalTax(DZFDouble additionalTax) {
		this.additionalTax = additionalTax;
	}

	public DZFDouble getAddpaidTax() {
		return addpaidTax;
	}

	public void setAddpaidTax(DZFDouble addpaidTax) {
		this.addpaidTax = addpaidTax;
	}

	public DZFDouble getExcisepaidTax() {
		return excisepaidTax;
	}

	public void setExcisepaidTax(DZFDouble excisepaidTax) {
		this.excisepaidTax = excisepaidTax;
	}

	public DZFDouble getIncomepaidTax() {
		return incomepaidTax;
	}

	public void setIncomepaidTax(DZFDouble incomepaidTax) {
		this.incomepaidTax = incomepaidTax;
	}

	public DZFDouble getCitypaidTax() {
		return citypaidTax;
	}

	public void setCitypaidTax(DZFDouble citypaidTax) {
		this.citypaidTax = citypaidTax;
	}

	public DZFDouble getEducapaidTax() {
		return educapaidTax;
	}

	public void setEducapaidTax(DZFDouble educapaidTax) {
		this.educapaidTax = educapaidTax;
	}

	public DZFDouble getLocalEducapaidTax() {
		return localEducapaidTax;
	}

	public void setLocalEducapaidTax(DZFDouble localEducapaidTax) {
		this.localEducapaidTax = localEducapaidTax;
	}

	public DZFDouble getPersonpaidTax() {
		return personpaidTax;
	}

	public void setPersonpaidTax(DZFDouble personpaidTax) {
		this.personpaidTax = personpaidTax;
	}

	public DZFDouble getCulturalpaidTax() {
		return culturalpaidTax;
	}

	public void setCulturalpaidTax(DZFDouble culturalpaidTax) {
		this.culturalpaidTax = culturalpaidTax;
	}

	public DZFDouble getAdditionalpaidTax() {
		return additionalpaidTax;
	}

	public void setAdditionalpaidTax(DZFDouble additionalpaidTax) {
		this.additionalpaidTax = additionalpaidTax;
	}

	public Integer getAddStatus() {
		return addStatus;
	}

	public void setAddStatus(Integer addStatus) {
		this.addStatus = addStatus;
	}

	public Integer getExciseStatus() {
		return exciseStatus;
	}

	public void setExciseStatus(Integer exciseStatus) {
		this.exciseStatus = exciseStatus;
	}

	public Integer getIncomeStatus() {
		return incomeStatus;
	}

	public void setIncomeStatus(Integer incomeStatus) {
		this.incomeStatus = incomeStatus;
	}

	public Integer getCityStatus() {
		return cityStatus;
	}

	public void setCityStatus(Integer cityStatus) {
		this.cityStatus = cityStatus;
	}

	public Integer getEducaStatus() {
		return educaStatus;
	}

	public void setEducaStatus(Integer educaStatus) {
		this.educaStatus = educaStatus;
	}

	public Integer getLocalEducaStatus() {
		return localEducaStatus;
	}

	public void setLocalEducaStatus(Integer localEducaStatus) {
		this.localEducaStatus = localEducaStatus;
	}

	public Integer getPersonStatus() {
		return personStatus;
	}

	public void setPersonStatus(Integer personStatus) {
		this.personStatus = personStatus;
	}

	public Integer getCulturalStatus() {
		return culturalStatus;
	}

	public void setCulturalStatus(Integer culturalStatus) {
		this.culturalStatus = culturalStatus;
	}

	public Integer getAdditionalStatus() {
		return additionalStatus;
	}

	public void setAdditionalStatus(Integer additionalStatus) {
		this.additionalStatus = additionalStatus;
	}

	public Integer getRembday() {
		return rembday;
	}

	public void setRembday(Integer rembday) {
		this.rembday = rembday;
	}

	public String getChargedeptname() {
		return chargedeptname;
	}

	public void setChargedeptname(String chargedeptname) {
		this.chargedeptname = chargedeptname;
	}

	public Integer getIsptx() {
		return isptx;
	}

	public void setIsptx(Integer isptx) {
		this.isptx = isptx;
	}

	public Integer getIpzjjzt() {
		return ipzjjzt;
	}

	public void setIpzjjzt(Integer ipzjjzt) {
		this.ipzjjzt = ipzjjzt;
	}

	public String getCoperatorid() {
		return coperatorid;
	}

	public void setCoperatorid(String coperatorid) {
		this.coperatorid = coperatorid;
	}

	public String getPk_workbench() {
		return pk_workbench;
	}

	public void setPk_workbench(String pk_workbench) {
		this.pk_workbench = pk_workbench;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public String getKhCode() {
		return khCode;
	}

	public void setKhCode(String khCode) {
		this.khCode = khCode;
	}

	public String getKhName() {
		return khName;
	}

	public void setKhName(String khName) {
		this.khName = khName;
	}

	public DZFDouble getIncome() {
		return income;
	}

	public void setIncome(DZFDouble income) {
		this.income = income;
	}

	public DZFDouble getAddTax() {
		return addTax;
	}

	public void setAddTax(DZFDouble addTax) {
		this.addTax = addTax;
	}

	public DZFDouble getSalesTax() {
		return salesTax;
	}

	public void setSalesTax(DZFDouble salesTax) {
		this.salesTax = salesTax;
	}

	public DZFDouble getExciseTax() {
		return exciseTax;
	}

	public void setExciseTax(DZFDouble exciseTax) {
		this.exciseTax = exciseTax;
	}

	public DZFDouble getCityTax() {
		return cityTax;
	}

	public void setCityTax(DZFDouble cityTax) {
		this.cityTax = cityTax;
	}

	public DZFDouble getEducaTax() {
		return educaTax;
	}

	public void setEducaTax(DZFDouble educaTax) {
		this.educaTax = educaTax;
	}

	public DZFDouble getLocalEducaTax() {
		return localEducaTax;
	}

	public void setLocalEducaTax(DZFDouble localEducaTax) {
		this.localEducaTax = localEducaTax;
	}

	public DZFDouble getPersonTax() {
		return personTax;
	}

	public void setPersonTax(DZFDouble personTax) {
		this.personTax = personTax;
	}

	public DZFDouble getIncomeTax() {
		return incomeTax;
	}

	public void setIncomeTax(DZFDouble incomeTax) {
		this.incomeTax = incomeTax;
	}

	public Integer getTaxStateCopy() {
		return taxStateCopy;
	}

	public void setTaxStateCopy(Integer taxStateCopy) {
		this.taxStateCopy = taxStateCopy;
	}

	public Integer getTaxStateClean() {
		return taxStateClean;
	}

	public void setTaxStateClean(Integer taxStateClean) {
		this.taxStateClean = taxStateClean;
	}

	public Integer getTaxStateFinish() {
		return taxStateFinish;
	}

	public void setTaxStateFinish(Integer taxStateFinish) {
		this.taxStateFinish = taxStateFinish;
	}

	public String getMemo() {
		return memo;
	}

	public void setMemo(String memo) {
		this.memo = memo;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Integer getDr() {
		return dr;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public DZFDate getBegindate() {
		return begindate;
	}

	public void setBegindate(DZFDate begindate) {
		this.begindate = begindate;
	}

	public String getCoderule() {
		return coderule;
	}

	public void setCoderule(String coderule) {
		this.coderule = coderule;
	}

	public String getPcountname() {
		return pcountname;
	}

	public void setPcountname(String pcountname) {
		this.pcountname = pcountname;
	}

	public String getVsuperaccount() {
		return vsuperaccount;
	}

	public void setVsuperaccount(String vsuperaccount) {
		this.vsuperaccount = vsuperaccount;
	}

	@Override
	public String getPKFieldName() {
		return "pk_workbench";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "nsworkbench";
	}

	public boolean isEmpty() {
		// 收入、增值税、消费税、城建税、教育费附加、地方教育费附加、个人所得税、企业所得税、抄税、清卡、完成、备注
		if ((income == null || income.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (addTax == null || addTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (exciseTax == null || exciseTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (cityTax == null || cityTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (educaTax == null || educaTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (localEducaTax == null || localEducaTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (personTax == null || personTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (incomeTax == null || incomeTax.compareTo(DZFDouble.ZERO_DBL) == 0)
				&& (taxStateCopy == null || taxStateCopy.intValue() == 0)
				&& (taxStateClean == null || taxStateClean.intValue() == 0)
				&& (taxStateFinish == null || taxStateFinish.intValue() == 0)
				&& (memo == null || memo.trim().equals(""))) {
			return true;
		}
		return false;
	}

}

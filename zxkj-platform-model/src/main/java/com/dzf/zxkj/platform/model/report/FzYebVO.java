package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.fasterxml.jackson.annotation.JsonProperty;

public class FzYebVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

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

	@Override
	public FzYebVO clone() {
		FzYebVO newvo = new FzYebVO();
		newvo.pk_acc = this.pk_acc;
		newvo.pk_fzlb = this.pk_fzlb;
		newvo.accCode = this.accCode;
		newvo.accName = this.accName;
		newvo.accLevel = this.accLevel;
		newvo.accDirection = this.accDirection;
		newvo.fzhsx1 = this.fzhsx1;
		newvo.fzhsx2 = this.fzhsx2;
		newvo.fzhsx3 = this.fzhsx3;
		newvo.fzhsx4 = this.fzhsx4;
		newvo.fzhsx5 = this.fzhsx5;
		newvo.fzhsx6 = this.fzhsx6;
		newvo.fzhsx7 = this.fzhsx7;
		newvo.fzhsx8 = this.fzhsx8;
		newvo.fzhsx9 = this.fzhsx9;
		newvo.fzhsx10 = this.fzhsx10;
		newvo.fzhsx1Code = this.fzhsx1Code;
		newvo.fzhsx2Code = this.fzhsx2Code;
		newvo.fzhsx3Code = this.fzhsx3Code;
		newvo.fzhsx4Code = this.fzhsx4Code;
		newvo.fzhsx5Code = this.fzhsx5Code;
		newvo.fzhsx6Code = this.fzhsx6Code;
		newvo.fzhsx7Code = this.fzhsx7Code;
		newvo.fzhsx8Code = this.fzhsx8Code;
		newvo.fzhsx9Code = this.fzhsx9Code;
		newvo.fzhsx10Code = this.fzhsx10Code;
		newvo.fzhsx1Name = this.fzhsx1Name;
		newvo.fzhsx2Name = this.fzhsx2Name;
		newvo.fzhsx3Name = this.fzhsx3Name;
		newvo.fzhsx4Name = this.fzhsx4Name;
		newvo.fzhsx5Name = this.fzhsx5Name;
		newvo.fzhsx6Name = this.fzhsx6Name;
		newvo.fzhsx7Name = this.fzhsx7Name;
		newvo.fzhsx8Name = this.fzhsx8Name;
		newvo.fzhsx9Name = this.fzhsx9Name;
		newvo.fzhsx10Name = this.fzhsx10Name;
		newvo.qcye = this.qcye;
		newvo.bqfsjf = this.bqfsjf;
		newvo.bqfsdf = this.bqfsdf;
		newvo.bnljjf = this.bnljjf;
		newvo.bnljdf = this.bnljdf;
		newvo.qmye = this.qmye;
		newvo.bsjf = this.bsjf;
		newvo.bsdf = this.bsdf;
		
		//------------原币数据------
		newvo.ybqcye = this.ybqcye;
		newvo.ybbqfsjf = this.ybbqfsjf;
		newvo.ybbqfsdf = this.ybbqfsdf;
		newvo.ybbnljjf = this.ybbnljjf;
		newvo.ybbnljdf = this.ybbnljdf;
		newvo.ybqmye = this.ybqmye;
		newvo.ybbsjf = this.ybbsjf;
		newvo.ybbsdf = this.ybbsdf;

		return newvo;
	}

	/**
	 * 最轻量复制，只复制必要的属性
	 */
	public FzYebVO clone(int fzlb) {
		FzYebVO newvo = new FzYebVO();
		newvo.pk_acc = this.pk_acc;
		newvo.accCode = this.accCode;
		newvo.accName = this.accName;
		newvo.accLevel = this.accLevel;
		newvo.accDirection = this.accDirection;
		newvo.pk_fzlb = this.pk_fzlb;

		String fzhsx = "fzhsx" + fzlb;
		String fzhsxCode = fzhsx + "Code";
		String fzhsxName = fzhsx + "Name";
		newvo.setAttributeValue(fzhsx, this.getAttributeValue(fzhsx));
		newvo.setAttributeValue(fzhsxCode, this.getAttributeValue(fzhsxCode));
		newvo.setAttributeValue(fzhsxName, this.getAttributeValue(fzhsxName));

		newvo.qcye = this.qcye;
		newvo.bqfsjf = this.bqfsjf;
		newvo.bqfsdf = this.bqfsdf;
		newvo.bnljjf = this.bnljjf;
		newvo.bnljdf = this.bnljdf;
		newvo.qmye = this.qmye;
		newvo.bsjf = this.bsjf;
		newvo.bsdf = this.bsdf;
		
		//------------------原币----------
		newvo.ybqcye = this.ybqcye;
		newvo.ybbqfsjf = this.ybbqfsjf;
		newvo.ybbqfsdf = this.ybbqfsdf;
		newvo.ybbnljjf = this.ybbnljjf;
		newvo.ybbnljdf = this.ybbnljdf;
		newvo.ybqmye = this.ybqmye;
		newvo.ybbsjf = this.ybbsjf;
		newvo.ybbsdf = this.ybbsdf;

		return newvo;
	}

	//打印时的抬头标签
	public String titlePeriod;
	public String gs;

	//期初余额的期间，给明细账取期初时用。余额表结果本身不需要期间。
	private String rq; //日期
	private String period;

	//当前行科目，当前行没有科目时显示为空（如快速切换点选辅助项目不点下面的科目(明细账才有快速切换)，或者参数不显示科目）
	private String pk_acc;
	public String accCode;
	private String accName;
	private Integer accLevel; //科目层级

	//当前科目方向（明细行取明细科目方向，汇总行取上级科目方向，辅助项汇总行(无科目级)方向取空）
	private Integer accDirection;

	//暂不支持按币种和原币金额展示
	private String pk_currency;
	@JsonProperty("fzlbid")
	private String pk_fzlb;//辅助类别
	//private String currCode;
	//private String currName;

	private String fzhsx1;
	private String fzhsx2;
	private String fzhsx3;
	private String fzhsx4;
	private String fzhsx5;
	private String fzhsx6;
	private String fzhsx7;
	private String fzhsx8;
	private String fzhsx9;
	private String fzhsx10;

	private String fzhsx1Code;
	private String fzhsx2Code;
	private String fzhsx3Code;
	private String fzhsx4Code;
	private String fzhsx5Code;
	private String fzhsx6Code;
	private String fzhsx7Code;
	private String fzhsx8Code;
	private String fzhsx9Code;
	private String fzhsx10Code;

	private String fzhsx1Name;
	private String fzhsx2Name;
	private String fzhsx3Name;
	private String fzhsx4Name;
	private String fzhsx5Name;
	private String fzhsx6Name;
	private String fzhsx7Name;
	private String fzhsx8Name;
	private String fzhsx9Name;
	private String fzhsx10Name;

	//辅助项、科目合并显示列
	public String fzhsxCode;
	public String fzhsxName;

	public DZFDouble qcye;
	public DZFDouble bqfsjf;
	public DZFDouble bqfsdf;
	public DZFDouble bnljjf;
	public DZFDouble bnljdf;
	public DZFDouble qmye;
	public Integer bsjf; //笔数
	public Integer bsdf;

	//余额表最终结果余额按借贷两栏展示
	public DZFDouble qcyejf;
	public DZFDouble qcyedf;
	public DZFDouble qmyejf;
	public DZFDouble qmyedf;
	
	
	//---------------------原币数据 -------------
	public DZFDouble ybqcye;
	public DZFDouble ybbqfsjf;
	public DZFDouble ybbqfsdf;
	public DZFDouble ybbnljjf;
	public DZFDouble ybbnljdf;
	public DZFDouble ybqmye;
	public Integer ybbsjf; //笔数
	public Integer ybbsdf;

	//余额表最终结果余额按借贷两栏展示
	public DZFDouble ybqcyejf;
	public DZFDouble ybqcyedf;
	public DZFDouble ybqmyejf;
	public DZFDouble ybqmyedf;

	public String getTitlePeriod() {
		return titlePeriod;
	}

	public void setTitlePeriod(String titlePeriod) {
		this.titlePeriod = titlePeriod;
	}

	public String getGs() {
		return gs;
	}

	public void setGs(String gs) {
		this.gs = gs;
	}

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getPk_currency() {
		return pk_currency;
	}

	public void setPk_currency(String pk_currency) {
		this.pk_currency = pk_currency;
	}

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public String getPk_acc() {
		return pk_acc;
	}

	public void setPk_acc(String pk_acc) {
		this.pk_acc = pk_acc;
	}

	public String getAccCode() {
		return accCode;
	}

	public void setAccCode(String accCode) {
		this.accCode = accCode;
	}

	public String getAccName() {
		return accName;
	}

	public void setAccName(String accName) {
		this.accName = accName;
	}

	public Integer getAccLevel() {
		return accLevel;
	}

	public void setAccLevel(Integer accLevel) {
		this.accLevel = accLevel;
	}

	public Integer getAccDirection() {
		return accDirection;
	}

	public void setAccDirection(Integer accDirection) {
		this.accDirection = accDirection;
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

	public String getFzhsx1Code() {
		return fzhsx1Code;
	}

	public void setFzhsx1Code(String fzhsx1Code) {
		this.fzhsx1Code = fzhsx1Code;
	}

	public String getFzhsx2Code() {
		return fzhsx2Code;
	}

	public void setFzhsx2Code(String fzhsx2Code) {
		this.fzhsx2Code = fzhsx2Code;
	}

	public String getFzhsx3Code() {
		return fzhsx3Code;
	}

	public void setFzhsx3Code(String fzhsx3Code) {
		this.fzhsx3Code = fzhsx3Code;
	}

	public String getFzhsx4Code() {
		return fzhsx4Code;
	}

	public void setFzhsx4Code(String fzhsx4Code) {
		this.fzhsx4Code = fzhsx4Code;
	}

	public String getFzhsx5Code() {
		return fzhsx5Code;
	}

	public void setFzhsx5Code(String fzhsx5Code) {
		this.fzhsx5Code = fzhsx5Code;
	}

	public String getFzhsx6Code() {
		return fzhsx6Code;
	}

	public void setFzhsx6Code(String fzhsx6Code) {
		this.fzhsx6Code = fzhsx6Code;
	}

	public String getFzhsx7Code() {
		return fzhsx7Code;
	}

	public void setFzhsx7Code(String fzhsx7Code) {
		this.fzhsx7Code = fzhsx7Code;
	}

	public String getFzhsx8Code() {
		return fzhsx8Code;
	}

	public void setFzhsx8Code(String fzhsx8Code) {
		this.fzhsx8Code = fzhsx8Code;
	}

	public String getFzhsx9Code() {
		return fzhsx9Code;
	}

	public void setFzhsx9Code(String fzhsx9Code) {
		this.fzhsx9Code = fzhsx9Code;
	}

	public String getFzhsx10Code() {
		return fzhsx10Code;
	}

	public void setFzhsx10Code(String fzhsx10Code) {
		this.fzhsx10Code = fzhsx10Code;
	}

	public String getFzhsx1Name() {
		return fzhsx1Name;
	}

	public void setFzhsx1Name(String fzhsx1Name) {
		this.fzhsx1Name = fzhsx1Name;
	}

	public String getFzhsx2Name() {
		return fzhsx2Name;
	}

	public void setFzhsx2Name(String fzhsx2Name) {
		this.fzhsx2Name = fzhsx2Name;
	}

	public String getFzhsx3Name() {
		return fzhsx3Name;
	}

	public void setFzhsx3Name(String fzhsx3Name) {
		this.fzhsx3Name = fzhsx3Name;
	}

	public String getFzhsx4Name() {
		return fzhsx4Name;
	}

	public void setFzhsx4Name(String fzhsx4Name) {
		this.fzhsx4Name = fzhsx4Name;
	}

	public String getFzhsx5Name() {
		return fzhsx5Name;
	}

	public void setFzhsx5Name(String fzhsx5Name) {
		this.fzhsx5Name = fzhsx5Name;
	}

	public String getFzhsx6Name() {
		return fzhsx6Name;
	}

	public void setFzhsx6Name(String fzhsx6Name) {
		this.fzhsx6Name = fzhsx6Name;
	}

	public String getFzhsx7Name() {
		return fzhsx7Name;
	}

	public void setFzhsx7Name(String fzhsx7Name) {
		this.fzhsx7Name = fzhsx7Name;
	}

	public String getFzhsx8Name() {
		return fzhsx8Name;
	}

	public void setFzhsx8Name(String fzhsx8Name) {
		this.fzhsx8Name = fzhsx8Name;
	}

	public String getFzhsx9Name() {
		return fzhsx9Name;
	}

	public void setFzhsx9Name(String fzhsx9Name) {
		this.fzhsx9Name = fzhsx9Name;
	}

	public String getFzhsx10Name() {
		return fzhsx10Name;
	}

	public void setFzhsx10Name(String fzhsx10Name) {
		this.fzhsx10Name = fzhsx10Name;
	}

	public String getFzhsxCode() {
		return fzhsxCode;
	}

	public void setFzhsxCode(String fzhsxCode) {
		this.fzhsxCode = fzhsxCode;
	}

	public String getFzhsxName() {
		return fzhsxName;
	}

	public void setFzhsxName(String fzhsxName) {
		this.fzhsxName = fzhsxName;
	}

	public DZFDouble getQcye() {
		return qcye;
	}

	public void setQcye(DZFDouble qcye) {
		this.qcye = qcye;
	}

	public DZFDouble getBqfsjf() {
		return bqfsjf;
	}

	public void setBqfsjf(DZFDouble bqfsjf) {
		this.bqfsjf = bqfsjf;
	}

	public DZFDouble getBqfsdf() {
		return bqfsdf;
	}

	public void setBqfsdf(DZFDouble bqfsdf) {
		this.bqfsdf = bqfsdf;
	}

	public DZFDouble getBnljjf() {
		return bnljjf;
	}

	public void setBnljjf(DZFDouble bnljjf) {
		this.bnljjf = bnljjf;
	}

	public DZFDouble getBnljdf() {
		return bnljdf;
	}

	public void setBnljdf(DZFDouble bnljdf) {
		this.bnljdf = bnljdf;
	}

	public DZFDouble getQmye() {
		return qmye;
	}

	public void setQmye(DZFDouble qmye) {
		this.qmye = qmye;
	}

	public Integer getBsjf() {
		return bsjf;
	}

	public void setBsjf(Integer bsjf) {
		this.bsjf = bsjf;
	}

	public Integer getBsdf() {
		return bsdf;
	}

	public void setBsdf(Integer bsdf) {
		this.bsdf = bsdf;
	}

	public DZFDouble getQcyejf() {
		return qcyejf;
	}

	public void setQcyejf(DZFDouble qcyejf) {
		this.qcyejf = qcyejf;
	}

	public DZFDouble getQcyedf() {
		return qcyedf;
	}

	public void setQcyedf(DZFDouble qcyedf) {
		this.qcyedf = qcyedf;
	}

	public DZFDouble getQmyejf() {
		return qmyejf;
	}

	public void setQmyejf(DZFDouble qmyejf) {
		this.qmyejf = qmyejf;
	}

	public DZFDouble getQmyedf() {
		return qmyedf;
	}

	public void setQmyedf(DZFDouble qmyedf) {
		this.qmyedf = qmyedf;
	}

	public DZFDouble getYbqcye() {
		return ybqcye;
	}

	public void setYbqcye(DZFDouble ybqcye) {
		this.ybqcye = ybqcye;
	}

	public DZFDouble getYbbqfsjf() {
		return ybbqfsjf;
	}

	public void setYbbqfsjf(DZFDouble ybbqfsjf) {
		this.ybbqfsjf = ybbqfsjf;
	}

	public DZFDouble getYbbqfsdf() {
		return ybbqfsdf;
	}

	public void setYbbqfsdf(DZFDouble ybbqfsdf) {
		this.ybbqfsdf = ybbqfsdf;
	}

	public DZFDouble getYbbnljjf() {
		return ybbnljjf;
	}

	public void setYbbnljjf(DZFDouble ybbnljjf) {
		this.ybbnljjf = ybbnljjf;
	}

	public DZFDouble getYbbnljdf() {
		return ybbnljdf;
	}

	public void setYbbnljdf(DZFDouble ybbnljdf) {
		this.ybbnljdf = ybbnljdf;
	}

	public DZFDouble getYbqmye() {
		return ybqmye;
	}

	public void setYbqmye(DZFDouble ybqmye) {
		this.ybqmye = ybqmye;
	}

	public Integer getYbbsjf() {
		return ybbsjf;
	}

	public void setYbbsjf(Integer ybbsjf) {
		this.ybbsjf = ybbsjf;
	}

	public Integer getYbbsdf() {
		return ybbsdf;
	}

	public void setYbbsdf(Integer ybbsdf) {
		this.ybbsdf = ybbsdf;
	}

	public DZFDouble getYbqcyejf() {
		return ybqcyejf;
	}

	public void setYbqcyejf(DZFDouble ybqcyejf) {
		this.ybqcyejf = ybqcyejf;
	}

	public DZFDouble getYbqcyedf() {
		return ybqcyedf;
	}

	public void setYbqcyedf(DZFDouble ybqcyedf) {
		this.ybqcyedf = ybqcyedf;
	}

	public DZFDouble getYbqmyejf() {
		return ybqmyejf;
	}

	public void setYbqmyejf(DZFDouble ybqmyejf) {
		this.ybqmyejf = ybqmyejf;
	}

	public DZFDouble getYbqmyedf() {
		return ybqmyedf;
	}

	public void setYbqmyedf(DZFDouble ybqmyedf) {
		this.ybqmyedf = ybqmyedf;
	}

	public String getPk_fzlb() {
		return pk_fzlb;
	}

	public void setPk_fzlb(String pk_fzlb) {
		this.pk_fzlb = pk_fzlb;
	}
	
	

}

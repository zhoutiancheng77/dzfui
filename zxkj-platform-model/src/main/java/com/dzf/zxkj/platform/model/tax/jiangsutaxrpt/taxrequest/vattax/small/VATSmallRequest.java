package com.dzf.zxkj.platform.model.tax.jiangsutaxrpt.taxrequest.vattax.small;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * 增值税小规模纳税人申报数据
 *
 * @author liubj
 *
 */
public class VATSmallRequest {

	@JsonProperty("Data1_01")
	private TaxReturn data1_01;

	@JsonProperty("Data1_02")
	private AcceptanceInfo data1_02;

	@JsonProperty("Data2_01")
	private TaxReturnAttach data2_01;

	@JsonProperty("Data4_01")
	private TaxCut[] data4_01;

	@JsonProperty("Data4_02")
	private TaxFree[] data4_02;

	@JsonProperty("Data4_03")
	private TaxReliefSum data4_03;

	public TaxReturn getData1_01() {
		return data1_01;
	}

	public void setData1_01(TaxReturn data1_01) {
		this.data1_01 = data1_01;
	}

	public TaxReturnAttach getData2_01() {
		return data2_01;
	}

	public void setData2_01(TaxReturnAttach data2_01) {
		this.data2_01 = data2_01;
	}

	public TaxCut[] getData4_01() {
		return data4_01;
	}

	public void setData4_01(TaxCut[] data4_01) {
		this.data4_01 = data4_01;
	}

	public TaxFree[] getData4_02() {
		return data4_02;
	}

	public void setData4_02(TaxFree[] data4_02) {
		this.data4_02 = data4_02;
	}

	public TaxReliefSum getData4_03() {
		return data4_03;
	}

	public void setData4_03(TaxReliefSum data4_03) {
		this.data4_03 = data4_03;
	}

	public AcceptanceInfo getData1_02() {
		return data1_02;
	}

	public void setData1_02(AcceptanceInfo data1_02) {
		this.data1_02 = data1_02;
	}
}

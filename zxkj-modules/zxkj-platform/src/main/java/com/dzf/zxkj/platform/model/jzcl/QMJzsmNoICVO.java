package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 商贸企业 期末成本结转 不启用库存
 *
 */
public class QMJzsmNoICVO extends SuperVO {
	public String getKmid() {
		return kmid;
	}

	public void setKmid(String kmid) {
		this.kmid = kmid;
	}

	@JsonProperty("id")
	private String id;
	@JsonProperty("kmid")
	private String kmid;
	@JsonProperty("kmbm")
	private String kmbm;
	@JsonProperty("kmmc")
	private String kmmc;
	@JsonProperty("qcnum")
	private DZFDouble qcnum;
	@JsonProperty("qcmny")
	private DZFDouble qcmny;
	@JsonProperty("bqsrnum")
	private DZFDouble bqsrnum;
	@JsonProperty("bqsrmny")
	private DZFDouble bqsrmny;
	@JsonProperty("bqprice")
	private DZFDouble bqprice;
	@JsonProperty("bqfcnum")
	private DZFDouble bqfcnum;
	@JsonProperty("shws")
	private Integer  shws;
	@JsonProperty("fzid")
	private String  fzid;
	private String kmfzid;
	//zpm 新加
	private DZFDouble xsprice;//销售单价
	private DZFDouble xsnum;//销售数量
	private DZFDouble xsmny;//销售金额
	
	
	private DZFDouble zgxsnum;//暂估销售数量(用于成本结转暂估单价取数计算)
	private DZFDouble zgxsmny;//暂估销售金额(用于成本结转暂估单价取数计算)
	
	private DZFDouble zgcgnum;//暂估采购数量(用于成本结转暂估单价取数计算)
	private DZFDouble zgcgmny;//暂估采购金额(用于成本结转暂估单价取数计算)
	
	public String getKmfzid() {
		return kmfzid;
	}

	public void setKmfzid(String kmfzid) {
		this.kmfzid = kmfzid;
	}

	public Integer getShws() {
		return shws;
	}

	public void setShws(Integer shws) {
		this.shws = shws;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKmbm() {
		return kmbm;
	}

	public void setKmbm(String kmbm) {
		this.kmbm = kmbm;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public DZFDouble getQcnum() {
		return qcnum;
	}

	public void setQcnum(DZFDouble qcnum) {
		this.qcnum = qcnum;
	}

	public DZFDouble getQcmny() {
		return qcmny;
	}

	public void setQcmny(DZFDouble qcmny) {
		this.qcmny = qcmny;
	}

	public DZFDouble getBqsrnum() {
		return bqsrnum;
	}

	public void setBqsrnum(DZFDouble bqsrnum) {
		this.bqsrnum = bqsrnum;
	}

	public DZFDouble getBqsrmny() {
		return bqsrmny;
	}

	public void setBqsrmny(DZFDouble bqsrmny) {
		this.bqsrmny = bqsrmny;
	}

	public DZFDouble getBqprice() {
		return bqprice;
	}

	public void setBqprice(DZFDouble bqprice) {
		this.bqprice = bqprice;
	}

	public DZFDouble getBqfcnum() {
		return bqfcnum;
	}

	public void setBqfcnum(DZFDouble bqfcnum) {
		this.bqfcnum = bqfcnum;
	}
	

	public String getFzid() {
		return fzid;
	}

	public void setFzid(String fzid) {
		this.fzid = fzid;
	}
	
	public DZFDouble getXsprice() {
		return xsprice;
	}

	public void setXsprice(DZFDouble xsprice) {
		this.xsprice = xsprice;
	}

	public DZFDouble getXsnum() {
		return xsnum;
	}

	public void setXsnum(DZFDouble xsnum) {
		this.xsnum = xsnum;
	}

	public DZFDouble getXsmny() {
		return xsmny;
	}

	public void setXsmny(DZFDouble xsmny) {
		this.xsmny = xsmny;
	}
	

	public DZFDouble getZgxsnum() {
		return zgxsnum;
	}

	public DZFDouble getZgxsmny() {
		return zgxsmny;
	}


	public DZFDouble getZgcgnum() {
		return zgcgnum;
	}

	public void setZgxsnum(DZFDouble zgxsnum) {
		this.zgxsnum = zgxsnum;
	}

	public void setZgxsmny(DZFDouble zgxsmny) {
		this.zgxsmny = zgxsmny;
	}

	public void setZgcgnum(DZFDouble zgcgnum) {
		this.zgcgnum = zgcgnum;
	}

	public DZFDouble getZgcgmny() {
		return zgcgmny;
	}

	public void setZgcgmny(DZFDouble zgcgmny) {
		this.zgcgmny = zgcgmny;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}
	
}

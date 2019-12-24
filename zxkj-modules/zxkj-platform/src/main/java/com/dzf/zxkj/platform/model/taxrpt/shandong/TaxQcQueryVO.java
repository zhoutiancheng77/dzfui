package com.dzf.zxkj.platform.model.taxrpt.shandong;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;

@SuppressWarnings("rawtypes")
public class TaxQcQueryVO extends SuperVO {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String djxh;// 登记序号
	private String gdslxDm;//
	private String sssqQ;// 所属期起
	private String sssqZ;// 所属期止
	private String dzbzdszlDm;//
	private String nsrsbh;// 纳税人识别号
	private String nsrMc;// 纳税人名称
	private String zgswjDm;//
	private String swjgDm;//
	private String impl;//
	private String yzpzzlDm;
	private String yzpzzldm_and;
	private String yzpzzldm_ayjd;
	private String yzpzzldm_bnd;
	private String yzpzzldm_byjd;
	private String sfyjdndsbDm;
	private String zgswkfjDm;
	
	private String ywlx;// 业务类型
	private String xmlType;// xml的type类型
	private DZFBoolean isConQc;// 是否包含期初
	private DZFBoolean isSend;// 是否支持上报
	private String noSendReason;//不支持上报原因

	private String type;
	private String schemaLocation;
	private InitFiledMapParse intParse;// 期初数据解析器

	public String getDjxh() {
		return djxh;
	}

	public void setDjxh(String djxh) {
		this.djxh = djxh;
	}

	public String getGdslxDm() {
		return gdslxDm;
	}

	public void setGdslxDm(String gdslxDm) {
		this.gdslxDm = gdslxDm;
	}

	public String getSssqQ() {
		return sssqQ;
	}

	public void setSssqQ(String sssqQ) {
		this.sssqQ = sssqQ;
	}

	public String getSssqZ() {
		return sssqZ;
	}

	public void setSssqZ(String sssqZ) {
		this.sssqZ = sssqZ;
	}

	public String getDzbzdszlDm() {
		return dzbzdszlDm;
	}

	public void setDzbzdszlDm(String dzbzdszlDm) {
		this.dzbzdszlDm = dzbzdszlDm;
	}

	public String getNsrsbh() {
		return nsrsbh;
	}

	public void setNsrsbh(String nsrsbh) {
		this.nsrsbh = nsrsbh;
	}

	public String getNsrMc() {
		return nsrMc;
	}

	public void setNsrMc(String nsrMc) {
		this.nsrMc = nsrMc;
	}

	public String getZgswjDm() {
		return zgswjDm;
	}

	public void setZgswjDm(String zgswjDm) {
		this.zgswjDm = zgswjDm;
	}

	public String getSwjgDm() {
		return swjgDm;
	}

	public void setSwjgDm(String swjgDm) {
		this.swjgDm = swjgDm;
	}

	public String getImpl() {
		return impl;
	}

	public String getYwlx() {
		return ywlx;
	}

	public String getXmlType() {
		return xmlType;
	}

	public void setImpl(String impl) {
		this.impl = impl;
	}

	public void setYwlx(String ywlx) {
		this.ywlx = ywlx;
	}

	public void setXmlType(String xmlType) {
		this.xmlType = xmlType;
	}

	public InitFiledMapParse getIntParse() {
		return intParse;
	}

	public void setIntParse(InitFiledMapParse intParse) {
		this.intParse = intParse;
	}

	public DZFBoolean getIsConQc() {
		return isConQc;
	}

	public void setIsConQc(DZFBoolean isConQc) {
		this.isConQc = isConQc;
	}

	public DZFBoolean getIsSend() {
		return isSend;
	}

	public void setIsSend(DZFBoolean isSend) {
		this.isSend = isSend;
	}

	public String getYzpzzlDm() {
		return yzpzzlDm;
	}

	public void setYzpzzlDm(String yzpzzlDm) {
		this.yzpzzlDm = yzpzzlDm;
	}
	
	public String getYzpzzldm_and() {
		return yzpzzldm_and;
	}

	public String getYzpzzldm_ayjd() {
		return yzpzzldm_ayjd;
	}

	public String getYzpzzldm_bnd() {
		return yzpzzldm_bnd;
	}

	public String getYzpzzldm_byjd() {
		return yzpzzldm_byjd;
	}

	public String getSfyjdndsbDm() {
		return sfyjdndsbDm;
	}

	public String getZgswkfjDm() {
		return zgswkfjDm;
	}

	public void setYzpzzldm_and(String yzpzzldm_and) {
		this.yzpzzldm_and = yzpzzldm_and;
	}

	public void setYzpzzldm_ayjd(String yzpzzldm_ayjd) {
		this.yzpzzldm_ayjd = yzpzzldm_ayjd;
	}

	public void setYzpzzldm_bnd(String yzpzzldm_bnd) {
		this.yzpzzldm_bnd = yzpzzldm_bnd;
	}

	public void setYzpzzldm_byjd(String yzpzzldm_byjd) {
		this.yzpzzldm_byjd = yzpzzldm_byjd;
	}

	public void setSfyjdndsbDm(String sfyjdndsbDm) {
		this.sfyjdndsbDm = sfyjdndsbDm;
	}

	public void setZgswkfjDm(String zgswkfjDm) {
		this.zgswkfjDm = zgswkfjDm;
	}

	public String getType() {
		return type;
	}

	public String getSchemaLocation() {
		return schemaLocation;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setSchemaLocation(String schemaLocation) {
		this.schemaLocation = schemaLocation;
	}

	public String getNoSendReason() {
		return noSendReason;
	}

	public void setNoSendReason(String noSendReason) {
		this.noSendReason = noSendReason;
	}

	@Override
	public String getPKFieldName() {
		return null;
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return null;
	}

}

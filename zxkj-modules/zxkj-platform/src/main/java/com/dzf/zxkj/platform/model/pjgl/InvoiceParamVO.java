package com.dzf.zxkj.platform.model.pjgl;

import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFDate;

/**
 * 票据查询条件VO
 * @author wangzhn
 *
 */
public class InvoiceParamVO extends SuperVO {
	
	private DZFDate begindate;//认证日期开始
	
	private DZFDate enddate;//认证日期结束
	
	private DZFDate begindate2;//开票日期开始
	
	private DZFDate enddate2;//开票日期结束
	
	private String  fphm;//发票号码
	
	private String  fpdm;//发票代码
	
	private String kpxm;//开票项目
	
	private String pk_corp;
	
	private String serdate;//查询方式
	private String startYear2;//入账年
	private String startMonth2;//入账月
	
	private String ioperatetype;//操作类型

	private String iszh; //是否专票
	private String ispz; //是否生成凭证
	
	
	
	

	public String getKpxm() {
		return kpxm;
	}

	public void setKpxm(String kpxm) {
		this.kpxm = kpxm;
	}

	public String getIspz() {
		return ispz;
	}

	public void setIspz(String ispz) {
		this.ispz = ispz;
	}

	public String getIszh() {
		return iszh;
	}

	public void setIszh(String iszh) {
		this.iszh = iszh;
	}

	public String getStartYear2() {
		return startYear2;
	}

	public String getStartMonth2() {
		return startMonth2;
	}

	public void setStartYear2(String startYear2) {
		this.startYear2 = startYear2;
	}

	public void setStartMonth2(String startMonth2) {
		this.startMonth2 = startMonth2;
	}

	public String getIoperatetype() {
		return ioperatetype;
	}

	public void setIoperatetype(String ioperatetype) {
		this.ioperatetype = ioperatetype;
	}

	public String getSerdate() {
		return serdate;
	}

	public void setSerdate(String serdate) {
		this.serdate = serdate;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public DZFDate getBegindate() {
		return begindate;
	}

	public void setBegindate(DZFDate begindate) {
		this.begindate = begindate;
	}

	public DZFDate getEnddate() {
		return enddate;
	}

	public void setEnddate(DZFDate enddate) {
		this.enddate = enddate;
	}

	public DZFDate getBegindate2() {
		return begindate2;
	}

	public void setBegindate2(DZFDate begindate2) {
		this.begindate2 = begindate2;
	}

	public DZFDate getEnddate2() {
		return enddate2;
	}

	public void setEnddate2(DZFDate enddate2) {
		this.enddate2 = enddate2;
	}

	public String getFphm() {
		return fphm;
	}

	public void setFphm(String fphm) {
		this.fphm = fphm;
	}

	public String getFpdm() {
		return fpdm;
	}

	public void setFpdm(String fpdm) {
		this.fpdm = fpdm;
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

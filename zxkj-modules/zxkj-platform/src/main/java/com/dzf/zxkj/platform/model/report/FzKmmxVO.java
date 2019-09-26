package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;

/**
 * 辅助科目明细vo
 * 
 * @author zhangj
 *
 */
@SuppressWarnings("serial")
public class FzKmmxVO extends KmMxZVO {

	private String rq;
	private String pzh;
	private String vmemo;
	private DZFDouble ybjf;// 原币借方
	private DZFDouble jf;
	private DZFDouble ybdf;// 原币贷方
	private DZFDouble df;
	private String fx;
	private DZFDouble ybye;// 原币余额
	private DZFDouble ye;
	private DZFDouble jfnum;// 借方数量
	private DZFDouble dfnum;// 贷方数量
	private DZFDouble yenum;// 数量
	private String fzxm;
	private String fzxmname;
	private String kmmx;
	private String kmmc;
	private String zy;
	private String pk_accsubj;
	private String fzcode;// 辅助项目编码
	private String fzname;// 辅助项目名称
	private String kmcode;// 科目编码
	private String kmname;// 科目名称
	private String fzlb;//辅助类别
	// 树状显示
	private String id;
	private String text;
	private String code;
	private String state;// 是否展开
	private DZFBoolean iskmid;// 是否是科目
	private String bdefault;//是否默认选中
	private String checked;//是否被选中
	public String bz;
	
	private String sourcebilltype;// 来源类型
	
	
	public String getFzlb() {
		return fzlb;
	}

	public void setFzlb(String fzlb) {
		this.fzlb = fzlb;
	}

	public String getChecked() {
		return checked;
	}

	public void setChecked(String checked) {
		this.checked = checked;
	}

	public String getBdefault() {
		return bdefault;
	}

	public void setBdefault(String bdefault) {
		this.bdefault = bdefault;
	}

	public String getSourcebilltype() {
		return sourcebilltype;
	}

	public void setSourcebilltype(String sourcebilltype) {
		this.sourcebilltype = sourcebilltype;
	}

	public DZFDouble getJfnum() {
		return jfnum;
	}

	public void setJfnum(DZFDouble jfnum) {
		this.jfnum = jfnum;
	}

	public DZFDouble getDfnum() {
		return dfnum;
	}

	public void setDfnum(DZFDouble dfnum) {
		this.dfnum = dfnum;
	}

	public DZFDouble getYenum() {
		return yenum;
	}

	public void setYenum(DZFDouble yenum) {
		this.yenum = yenum;
	}

//	private FzKmmxVO[] children;

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

	public String getBz() {
		return bz;
	}

	public void setBz(String bz) {
		this.bz = bz;
	}

	public DZFDouble getYbjf() {
		return ybjf;
	}

	public void setYbjf(DZFDouble ybjf) {
		this.ybjf = ybjf;
	}

	public DZFDouble getYbdf() {
		return ybdf;
	}

	public void setYbdf(DZFDouble ybdf) {
		this.ybdf = ybdf;
	}

	public DZFDouble getYbye() {
		return ybye;
	}

	public void setYbye(DZFDouble ybye) {
		this.ybye = ybye;
	}

	public String getFzcode() {
		return fzcode;
	}

	public void setFzcode(String fzcode) {
		this.fzcode = fzcode;
	}

	public String getFzname() {
		return fzname;
	}

	public void setFzname(String fzname) {
		this.fzname = fzname;
	}

	public String getKmcode() {
		return kmcode;
	}

	public void setKmcode(String kmcode) {
		this.kmcode = kmcode;
	}

	public String getKmname() {
		return kmname;
	}

	public void setKmname(String kmname) {
		this.kmname = kmname;
	}

	public String getPk_accsubj() {
		return pk_accsubj;
	}

	public void setPk_accsubj(String pk_accsubj) {
		this.pk_accsubj = pk_accsubj;
	}

	public DZFBoolean getIskmid() {
		return iskmid;
	}

	public void setIskmid(DZFBoolean iskmid) {
		this.iskmid = iskmid;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getFzxmname() {
		return fzxmname;
	}

	public void setFzxmname(String fzxmname) {
		this.fzxmname = fzxmname;
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
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

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public String getPzh() {
		return pzh;
	}

	public void setPzh(String pzh) {
		this.pzh = pzh;
	}

	public String getVmemo() {
		return vmemo;
	}

	public void setVmemo(String vmemo) {
		this.vmemo = vmemo;
	}

	public DZFDouble getJf() {
		return jf;
	}

	public void setJf(DZFDouble jf) {
		this.jf = jf;
	}

	public DZFDouble getDf() {
		return df;
	}

	public void setDf(DZFDouble df) {
		this.df = df;
	}

	public String getFx() {
		return fx;
	}

	public void setFx(String fx) {
		this.fx = fx;
	}

	public DZFDouble getYe() {
		return ye;
	}

	public void setYe(DZFDouble ye) {
		this.ye = ye;
	}

	public String getFzxm() {
		return fzxm;
	}

	public void setFzxm(String fzxm) {
		this.fzxm = fzxm;
	}

	public String getKmmx() {
		return kmmx;
	}

	public void setKmmx(String kmmx) {
		this.kmmx = kmmx;
	}

	public String getKmmc() {
		return kmmc;
	}

	public void setKmmc(String kmmc) {
		this.kmmc = kmmc;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

//	public FzKmmxVO[] getChildren() {
//		if(children == null ||  children.length ==0){
//			FzKmmxVO[] bodyvos = (FzKmmxVO[]) DZfcommonTools.convertToSuperVO( super.getChildren());
//			return bodyvos;
//		}
//		return children;
//	}
//	
//	public SuperVO[] getchildrenSuper(){
//		return super.getChildren();
//	}
//
//	public void setChildren(FzKmmxVO[] children) {
//		this.children = children;
//	}
}

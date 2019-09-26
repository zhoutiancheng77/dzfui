package com.dzf.zxkj.platform.model.jzcl;

import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;

@SuppressWarnings("serial")
public class QmLossesVO extends SuperVO {

	private String pk_annuallosses;// 主键
	private DZFDouble nlossmny;// 弥补金额
	private String pk_corp;// 公司
	private String period;//年度
	private DZFDateTime ts;// 时间戳
	private Integer dr;// 删除标志
	private DZFDouble vdef1;// 自定义项1
	private DZFDouble vdef2;// 自定义项2
	private DZFDouble vdef3;// 自定义项3
	private DZFDouble vdef4;// 自定义项4
	private DZFDouble vdef5;// 自定义项5
	private String vdef6;// 自定义项6
	private String vdef7;// 自定义项7
	private String vdef8;// 自定义项8
	private String vdef9;// 自定义项9
	private String vdef10;// 自定义项10
	private Integer vdef11;// 自定义项11
	private Integer vdef12;// 自定义项12
	private Integer vdef13;// 自定义项13
	private DZFBoolean vdef14;// 自定义项14
	private DZFBoolean vdef15;// 自定义项15

	public String getPk_annuallosses() {
		return pk_annuallosses;
	}

	public DZFDouble getNlossmny() {
		return nlossmny;
	}

	public String getPk_corp() {
		return pk_corp;
	}

	public DZFDateTime getTs() {
		return ts;
	}

	public Integer getDr() {
		return dr;
	}

	public DZFDouble getVdef1() {
		return vdef1;
	}

	public DZFDouble getVdef2() {
		return vdef2;
	}

	public DZFDouble getVdef3() {
		return vdef3;
	}

	public DZFDouble getVdef4() {
		return vdef4;
	}

	public DZFDouble getVdef5() {
		return vdef5;
	}

	public String getVdef6() {
		return vdef6;
	}

	public String getVdef7() {
		return vdef7;
	}

	public String getVdef8() {
		return vdef8;
	}

	public String getVdef9() {
		return vdef9;
	}

	public String getVdef10() {
		return vdef10;
	}

	public Integer getVdef11() {
		return vdef11;
	}

	public Integer getVdef12() {
		return vdef12;
	}

	public Integer getVdef13() {
		return vdef13;
	}

	public DZFBoolean getVdef14() {
		return vdef14;
	}

	public DZFBoolean getVdef15() {
		return vdef15;
	}

	public void setPk_annuallosses(String pk_annuallosses) {
		this.pk_annuallosses = pk_annuallosses;
	}

	public void setNlossmny(DZFDouble nlossmny) {
		this.nlossmny = nlossmny;
	}

	public void setPk_corp(String pk_corp) {
		this.pk_corp = pk_corp;
	}

	public void setTs(DZFDateTime ts) {
		this.ts = ts;
	}

	public void setDr(Integer dr) {
		this.dr = dr;
	}

	public void setVdef1(DZFDouble vdef1) {
		this.vdef1 = vdef1;
	}

	public void setVdef2(DZFDouble vdef2) {
		this.vdef2 = vdef2;
	}

	public void setVdef3(DZFDouble vdef3) {
		this.vdef3 = vdef3;
	}

	public void setVdef4(DZFDouble vdef4) {
		this.vdef4 = vdef4;
	}

	public void setVdef5(DZFDouble vdef5) {
		this.vdef5 = vdef5;
	}

	public void setVdef6(String vdef6) {
		this.vdef6 = vdef6;
	}

	public void setVdef7(String vdef7) {
		this.vdef7 = vdef7;
	}

	public void setVdef8(String vdef8) {
		this.vdef8 = vdef8;
	}

	public void setVdef9(String vdef9) {
		this.vdef9 = vdef9;
	}

	public void setVdef10(String vdef10) {
		this.vdef10 = vdef10;
	}

	public void setVdef11(Integer vdef11) {
		this.vdef11 = vdef11;
	}

	public void setVdef12(Integer vdef12) {
		this.vdef12 = vdef12;
	}

	public void setVdef13(Integer vdef13) {
		this.vdef13 = vdef13;
	}

	public void setVdef14(DZFBoolean vdef14) {
		this.vdef14 = vdef14;
	}

	public void setVdef15(DZFBoolean vdef15) {
		this.vdef15 = vdef15;
	}
	
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	@Override
	public String getPKFieldName() {
		return "pk_annuallosses";
	}

	@Override
	public String getParentPKFieldName() {
		return null;
	}

	@Override
	public String getTableName() {
		return "ynt_annuallosses";
	}

}

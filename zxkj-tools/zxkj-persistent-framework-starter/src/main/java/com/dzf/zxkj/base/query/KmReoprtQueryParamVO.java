package com.dzf.zxkj.base.query;


import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.query.QueryParamVO;

/**
 * 科目查询公共的vo参数
 * 
 * @author zhangj
 *
 */
public class KmReoprtQueryParamVO extends QueryParamVO {


	// 辅助明细账使用
	private DZFBoolean isxsxj;// 只是显示下级
	private DZFBoolean isxsmx;// 显示最明细科目
	private String corpIds1;
	private String fzlb;
	private String fzxm;
	private String selectkmid;// 辅助核算选中科目的id
	private String selectxmid;// 选中的项目id
	private DZFBoolean xskm;// 是否显示科目
	private DZFBoolean bswitch;//是否切换
	private int report_rows;//报表显示页数

	// 科目明细账使用
	private DZFBoolean isqry;// 是否是重新查询或者第一次查询(空默认就是重新查询)
	private String currkmbm;// 当前科目编码,也可以是,号分割

	private String minmny;// 最小金额
	private String maxmny;//最大金额
	private String zy;// 摘要

	private String list;//list字符串

	public int getReport_rows() {
		return report_rows;
	}

	public void setReport_rows(int report_rows) {
		this.report_rows = report_rows;
	}

	public String getList() {
		return list;
	}

	public void setList(String list) {
		this.list = list;
	}

	public DZFBoolean getBswitch() {
		return bswitch;
	}

	public void setBswitch(DZFBoolean bswitch) {
		this.bswitch = bswitch;
	}

	public String getMinmny() {
		return minmny;
	}

	public void setMinmny(String minmny) {
		this.minmny = minmny;
	}

	public String getMaxmny() {
		return maxmny;
	}

	public void setMaxmny(String maxmny) {
		this.maxmny = maxmny;
	}

	public String getZy() {
		return zy;
	}

	public void setZy(String zy) {
		this.zy = zy;
	}

	public String getCurrkmbm() {
		return currkmbm;
	}

	public void setCurrkmbm(String currkmbm) {
		this.currkmbm = currkmbm;
	}

	public DZFBoolean getIsqry() {
		return isqry;
	}

	public void setIsqry(DZFBoolean isqry) {
		this.isqry = isqry;
	}

	public DZFBoolean getXskm() {
		return xskm;
	}

	public void setXskm(DZFBoolean xskm) {
		this.xskm = xskm;
	}

	public String getSelectxmid() {
		return selectxmid;
	}

	public void setSelectxmid(String selectxmid) {
		this.selectxmid = selectxmid;
	}

	public String getFzxm() {
		return fzxm;
	}

	public void setFzxm(String fzxm) {
		this.fzxm = fzxm;
	}

	public String getSelectkmid() {
		return selectkmid;
	}

	public void setSelectkmid(String selectkmid) {
		this.selectkmid = selectkmid;
	}

	public String getFzlb() {
		return fzlb;
	}

	public void setFzlb(String fzlb) {
		this.fzlb = fzlb;
	}

	public String getCorpIds1() {
		return corpIds1;
	}

	public void setCorpIds1(String corpIds1) {
		this.corpIds1 = corpIds1;
	}

	public DZFBoolean getIsxsxj() {
		return isxsxj;
	}

	public void setIsxsxj(DZFBoolean isxsxj) {
		this.isxsxj = isxsxj;
	}

	public DZFBoolean getIsxsmx() {
		return isxsmx;
	}

	public void setIsxsmx(DZFBoolean isxsmx) {
		this.isxsmx = isxsmx;
	}

}

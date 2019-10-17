package com.dzf.zxkj.platform.model.report;


import com.dzf.zxkj.common.model.SuperVO;

/**
 * 风控体检报告
 * 
 * @author zhangj
 *
 */
public class FkTjBgVo extends SuperVO {

	private String zbmc;//指标名称
	private Integer fx;// 风险0 极低 1 爆表 2 未知
	private String fxstr;// 风险显示
	private Integer jy;// 建议 0 赞一个 1 及时调账 2 调整取数方式
	private String jystr;// 建议内容
	private String fxjg;//分析结果
	private FktjBgBvo[] fktjbgbvos;//风控提交报告子表
	
	public static  class FktjBgBvo {
		private String name;
		private String bq;//本期
		private String sq;//上期
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getBq() {
			return bq;
		}
		public void setBq(String bq) {
			this.bq = bq;
		}
		public String getSq() {
			return sq;
		}
		public void setSq(String sq) {
			this.sq = sq;
		}
	}
	
	public String getFxjg() {
		return fxjg;
	}

	public void setFxjg(String fxjg) {
		this.fxjg = fxjg;
	}

	public FktjBgBvo[] getFktjbgbvos() {
		return fktjbgbvos;
	}

	public void setFktjbgbvos(FktjBgBvo[] fktjbgbvos) {
		this.fktjbgbvos = fktjbgbvos;
	}

	public String getZbmc() {
		return zbmc;
	}

	public void setZbmc(String zbmc) {
		this.zbmc = zbmc;
	}

	public Integer getFx() {
		return fx;
	}

	public void setFx(Integer fx) {
		this.fx = fx;
	}

	public String getFxstr() {
		return fxstr;
	}

	public void setFxstr(String fxstr) {
		this.fxstr = fxstr;
	}

	public Integer getJy() {
		return jy;
	}

	public void setJy(Integer jy) {
		this.jy = jy;
	}

	public String getJystr() {
		return jystr;
	}

	public void setJystr(String jystr) {
		this.jystr = jystr;
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

package com.dzf.zxkj.app.model.resp.bean;


import com.dzf.zxkj.app.model.report.AppFzChVo;
import com.dzf.zxkj.app.model.report.AppFzYeVo;
import com.dzf.zxkj.app.model.report.ZqVo;
import com.dzf.zxkj.platform.model.report.ZzsBgVo;

import java.util.List;
import java.util.Map;


/**
 * 报表响应类
 * @author zhangj
 *
 */
public class ReportResBean {

	//--------辅助余额---------
	private String totalmny;//合计金额
	private String totalcount;//合计数量
	private List<AppFzYeVo> fzyelist;//辅助余额信息
	private List<AppFzChVo.AppFzMx1> fzmxvos1;//辅助存货信息
	//--------辅助余额---------
	
	//-----------征期日历------
	private Map<String, String> zqmap ;
	
	private List<ZqVo> zqlist;//征期list
	
	//-----------征期日历-------
	
	//---------税负预警-------
	private String sfyjx;//税负预警线
	
	private ZzsBgVo[] zzsbg;//增值税报告
	//-----------税负预警---------
	
	public Map<String, String> getZqmap() {
		return zqmap;
	}

	public List<AppFzChVo.AppFzMx1> getFzmxvos1() {
		return fzmxvos1;
	}

	public void setFzmxvos1(List<AppFzChVo.AppFzMx1> fzmxvos1) {
		this.fzmxvos1 = fzmxvos1;
	}

	public String getTotalmny() {
		return totalmny;
	}

	public void setTotalmny(String totalmny) {
		this.totalmny = totalmny;
	}

	public String getTotalcount() {
		return totalcount;
	}

	public void setTotalcount(String totalcount) {
		this.totalcount = totalcount;
	}

	public List<AppFzYeVo> getFzyelist() {
		return fzyelist;
	}

	public void setFzyelist(List<AppFzYeVo> fzyelist) {
		this.fzyelist = fzyelist;
	}

	public void setZqmap(Map<String, String> zqmap) {
		this.zqmap = zqmap;
	}
	
	public List<ZqVo> getZqlist() {
		return zqlist;
	}

	public void setZqlist(List<ZqVo> zqlist) {
		this.zqlist = zqlist;
	}

	public String getSfyjx() {
		return sfyjx;
	}

	public ZzsBgVo[] getZzsbg() {
		return zzsbg;
	}

	public void setZzsbg(ZzsBgVo[] zzsbg) {
		this.zzsbg = zzsbg;
	}

	public void setSfyjx(String sfyjx) {
		this.sfyjx = sfyjx;
	}
	
}

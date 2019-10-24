package com.dzf.zxkj.report.excel.cwbb;

import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.report.QyBdVO;
import com.dzf.zxkj.report.utils.ReportUtil;

import java.util.Date;

/**
 * 权益变动表
 * @author zhangj
 *
 */
public class QyBdExcelField implements IExceport<QyBdVO> {

	private QyBdVO[] ywhdvos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("xm", "项目",false,0,false),
			new Fieldelement("hc", "行次",false,0,false),
			new Fieldelement("sq_je", "上年数",true,2,true),
			new Fieldelement("bn_je", "本年数",true,2,true),
	};
	
	@Override
	public String getExcelport2007Name() {
		return "权益变动表-"+ ReportUtil.formatQj(qj)+".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "权益变动表-"+ReportUtil.formatQj(qj)+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "权益变动表";
	}

	@Override
	public String getSheetName() {
		return "权益变动表";
	}

	@Override
	public QyBdVO[] getData() {
		return ywhdvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}
	
	public void setYwhdvos(QyBdVO[] ywhdvos) {
		this.ywhdvos = ywhdvos;
	}

	@Override
	public String getQj() {
		return qj;
	}

	@Override
	public String getCreateSheetDate() {
		return now;
	}

	@Override
	public String getCreateor() {
		return creator;
	}
	
	public void setQj(String qj) {
		this.qj = qj;
	}

	public void setCreator(String creator) {
		this.creator = creator;
	}

	@Override
	public String getCorpName() {
		return corpname;
	}
	
	public void setCorpName(String corpname){
		this.corpname = corpname;
	}

	@Override
	public boolean[] isShowTitDetail() {
		return new boolean[]{true,true,false};
	}

}

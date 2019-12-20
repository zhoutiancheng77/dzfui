package com.dzf.zxkj.platform.excel;

import com.dzf.zxkj.common.base.LogRecordVo;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;

import java.util.Date;


/**
 * 日志导出excel
 * @author zhangj
 *
 */
public class LogRecordExcelField   implements IExceport<LogRecordVo> {

	private LogRecordVo[] logrecordvos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("doperatedate", "操作时间",false,0,false,20,true),
			new Fieldelement("vuser", "操作用户",false,0,false),
			new Fieldelement("vuserip", "ip地址",false,0,false),
			new Fieldelement("opetypestr", "操作类型",false,0,false),
			new Fieldelement("vopemsg","操作说明",false,0,false,50,true),
	};

	@Override
	public String getExcelport2007Name() {
		return "操作日志_"+now+".xlsx";
	}
	
	@Override
	public String getExcelport2003Name() {
		return "操作日志_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "操作日志";
	}

	@Override
	public String getSheetName() {
		return "操作日志";
	}

	@Override
	public LogRecordVo[] getData() {
		return logrecordvos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public LogRecordVo[] getLogrecordvos() {
		return logrecordvos;
	}

	public void setLogrecordvos(LogRecordVo[] logrecordvos) {
		this.logrecordvos = logrecordvos;
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

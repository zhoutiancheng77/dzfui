package com.dzf.zxkj.platform.excel.zcgl;


import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.excel.param.Fieldelement;
import com.dzf.zxkj.excel.param.IExceport;
import com.dzf.zxkj.platform.model.zcgl.AssetDepreciaTionVO;

import java.util.Date;

/**
 * 折旧明细账账
 * @author wangzhn
 *
 */
public class ZczjmxExcelField implements IExceport<AssetDepreciaTionVO> {

	private AssetDepreciaTionVO[] assdetivos = null;
	
	private String qj = null;
	
	private String now = DZFDate.getDate(new Date()).toString();
	
	private String creator = null;
	
	private String corpname = null;
	
	private Fieldelement[] fields = new Fieldelement[]{
			new Fieldelement("catename", "类别",false,0,false),
			new Fieldelement("assetcode", "资产编码",false,0,false),
			new Fieldelement("assetname", "资产名称",false,0,false),
			new Fieldelement("accountdate", "入账日期",false,0,false),
			new Fieldelement("assetmny", "资产原值",true,2,true),
			new Fieldelement("uselimit", "预计使用期间数",false,0,false),
			new Fieldelement("salvageratio", "净残值率(%)",true,2,true,15,true),
			new Fieldelement("businessdate", "折旧日期",false,0,false),
			new Fieldelement("originalvalue", "本期折旧",true,2,true),
			new Fieldelement("depreciationmny", "累计折旧",true,2,true),
			new Fieldelement("assetnetmny", "期末净值",true,2,true),
			new Fieldelement("istogl", "转总账",false,0,false),
			new Fieldelement("pzh", "凭证号",false,0,false),
			new Fieldelement("issettle", "已结账",false,0,false)
		};

	@Override
	public String getExcelport2007Name() {
		return "折旧明细账_"+now+".xlsx";
	}

	@Override
	public String getExcelport2003Name() {
		return "折旧明细账_"+now+".xls";
	}

	@Override
	public String getExceportHeadName() {
		return "折旧明细账";
	}

	@Override
	public String getSheetName() {
		return "折旧明细账";
	}

	@Override
	public AssetDepreciaTionVO[] getData() {
		return assdetivos;
	}

	@Override
	public Fieldelement[] getFieldInfo() {
		return fields;
	}

	public void setAssdetivos(AssetDepreciaTionVO[] assdetivos) {
		this.assdetivos = assdetivos;
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

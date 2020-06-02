package com.dzf.zxkj.app.model.nssb;


import com.dzf.zxkj.common.model.SuperVO;

public class NssbBean implements java.io.Serializable {

	public String rq;
	
	public String hj;//合计
	
	public String bqld;//本期留底
	
	public String confirm;//是否确认 0 确认，1 没确认

	public NssbContent[] content;

	public String getRq() {
		return rq;
	}

	public void setRq(String rq) {
		this.rq = rq;
	}

	public NssbContent[] getContent() {
		return content;
	}

	public void setContent(NssbContent[] content) {
		this.content = content;
	}
	
	public String getHj() {
		return hj;
	}

	public void setHj(String hj) {
		this.hj = hj;
	}

	public String getBqld() {
		return bqld;
	}

	public void setBqld(String bqld) {
		this.bqld = bqld;
	}



	public static class NssbContent extends SuperVO {

		public String name;

		public String value;
		
		private String code;//编码
		
		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
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


	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

}

package com.dzf.zxkj.platform.enums;

import com.dzf.zxkj.common.utils.DzfUtil;

/**
 * 来源系统名字
 * @author zhangj
 *
 */
public enum SourceSysEnum {

	/**
	 * 总后台操作产生消息类型
	 */
	SOURCE_SYS_DZF("dzf", "大账房","大账房财务在线", DzfUtil.MASTERSECRET,DzfUtil.JPUSHAPPKEY),
	SOURCE_SYS_CST("cst", "移动账务","移动账务","0fd42b51349771d8655a0c78","cccacbdbbb8750ba71cced15"),
	SOURCE_SYS_KCDR("kcdr", "卡车达人","卡车达人","",""),
	SOURCE_SYS_WX_APPLET("wx_applet", "大账房","大账房财务在线","",""),//微信小程序
	SOURCE_SYS_PST_APP("pst", "票税通","大账房财务在线","",""),
	SOURCE_SYS_CSDR("csdr", "财税达人", "大账房财务在线", "086a0163c87f837ea46da903", "0805a2424889ae344f9dbd71");
	

	private String value;
	private String name;
	private String smsdefaultvalue;//默认短信信息
	private String jgsecret;//极光秘钥
	private String jgkey;//极光key
	

	SourceSysEnum(String value, String name, String smsvalue, String jgsecret, String jgkey) {
		this.value = value;
		this.name = name;
		this.smsdefaultvalue = smsvalue;
		this.jgsecret = jgsecret;
		this.jgkey = jgkey;
	}


	public String getJgsecret() {
		return jgsecret;
	}


	public void setJgsecret(String jgsecret) {
		this.jgsecret = jgsecret;
	}


	public String getJgkey() {
		return jgkey;
	}





	public void setJgkey(String jgkey) {
		this.jgkey = jgkey;
	}





	public String getSmsdefaultvalue() {
		return smsdefaultvalue;
	}

	public void setSmsdefaultvalue(String smsdefaultvalue) {
		this.smsdefaultvalue = smsdefaultvalue;
	}


	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}

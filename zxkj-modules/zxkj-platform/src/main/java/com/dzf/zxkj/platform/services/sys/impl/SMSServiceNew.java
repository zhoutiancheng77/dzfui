package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.platform.model.sys.SMSBVO;
import com.dzf.zxkj.platform.model.sys.SMSHVO;
import com.dzf.zxkj.platform.model.sys.SMSResVO;
import com.dzf.zxkj.platform.util.SMSPropertiesUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

@Slf4j
public class SMSServiceNew {

	private HttpClient client;

	private String reqsys;

	private String ipaddr;

	private String authkey;

	private String url;

	private String[] phone;

	private String templatecode;

	private Map<String, String> params;

	private String dxqm;

	public SMSServiceNew(SMSBVO smsVO) {
		init(smsVO);
	}

	private void init(SMSBVO smsVO) {
		Properties prop = SMSPropertiesUtils.getProperties();
		reqsys = prop.getProperty("appid");
		authkey = prop.getProperty("appkey");
		url = prop.getProperty("smsurl");
		this.params = smsVO.getParams();
		this.phone = smsVO.getPhone();
		this.templatecode = smsVO.getTemplatecode();
		this.dxqm = smsVO.getDxqm();
		this.ipaddr = smsVO.getSmsip();
	}

	public SMSResVO sendPostData() {

		SMSResVO headvo = null;

		return headvo;
	}


	private SMSHVO buildHeadVO() {
		SMSHVO head = new SMSHVO();
		head.setReqsys(reqsys);
		head.setReqtime(getTimeNow());
		head.setReqno(reqsys + "_" + String.valueOf(System.currentTimeMillis()));
		head.setAuthkey(authkey);
		encodeAuthorkey(head);
		return head;
	}

	private void encodeAuthorkey(SMSHVO head) {
		String time = head.getReqtime();
		String sys = head.getReqsys();
		String no = head.getReqno();
		String unionkey = time + sys + no;
		String jm64 = "";
		try {
			jm64 = Base64CodeUtils.encode(unionkey.getBytes());
		} catch (Exception e) {
			log.error("短信通道接口，base64加密失败！", e);
		}
		String authkey = head.getAuthkey();
		String unionkey1 = authkey + jm64;
		String md516 = mD5ECode(unionkey1);
		head.setReqauthkey(md516);
	}

	private String getTimeNow() {
		SimpleDateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		String date = dateFormater.format(new Date());
		return date;
	}

	private SMSBVO buildBodyVO() {
		SMSBVO body = new SMSBVO();
		body.setPhone(phone);
		body.setAppid(reqsys);
		body.setTemplatecode(templatecode);
		body.setDxqm(dxqm);
		body.setParams(params);
		body.setSmsip(ipaddr);
		return body;
	}

	private String mD5ECode(String text) {
		StringBuffer buf = new StringBuffer("");
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(text.getBytes());
			byte b[] = md.digest();
			int i;
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return buf.toString();
	}
}

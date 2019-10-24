package com.dzf.zxkj.platform.model.secret;

import java.io.Serializable;

public class RsaVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String prikey;
	private String pubkey;

	public String getPrikey() {
		return prikey;
	}

	public void setPrikey(String prikey) {
		this.prikey = prikey;
	}

	public String getPubkey() {
		return pubkey;
	}

	public void setPubkey(String pubkey) {
		this.pubkey = pubkey;
	}

}

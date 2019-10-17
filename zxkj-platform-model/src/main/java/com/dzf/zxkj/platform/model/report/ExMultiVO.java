package com.dzf.zxkj.platform.model.report;

import com.dzf.zxkj.common.constant.PowerConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 * 多栏账vo数据
 * 
 * @author zhangj
 *
 */
public class ExMultiVO extends SuperVO {

	private static final long serialVersionUID = 1L;
	
	private HashMap<String, Object> hash = new HashMap();
	
	private String pk_tzpz_h;
	private String gs;
	// 日期
	private String rq;
	// 凭证号
	private String pzh;
	// 摘要
	private String zy;
	// 借方
	private String jf;
	// 贷方
	private String df;
	// 方向
	private String fx;
	// 余额
	private String ye;
	private String kmbm;
	private String pk_accsubj;
	// 科目
	private String km;

	private String bz;

	private String pk_corp;//

	private String pzpk;
	// 币种
	private String pk_currency;

	public String getPk_currency() {
		return (String)hash.get("pk_currency");
	}

	public void setPk_currency(String pk_currency) {
		hash.put("pk_currency",pk_currency);
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


	public String[] getAttributeNames() {
		Set s = hash.keySet();

		if (s == null)
			return null;

		Object[] obs = (Object[]) s.toArray();

		if (obs == null || obs.length == 0)
			return null;

		String[] attrs = new String[obs.length];
		for (int i = 0; i < obs.length; i++)
			attrs[i] = (String) obs[i];
		return attrs;
	}

	public Object getAttributeValue(String attributeName) {
		if (attributeName == null)
			return null;
		return hash.get(attributeName);
	}

	/**
	 * 清空数据
	 */
	public void ClearData() {
		Iterator it = hash.keySet().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (!key.equals(PowerConstant.attribute[0]) && !key.equals(PowerConstant.attribute[1])
					&& !key.equals(PowerConstant.attribute[2]) && !key.equals(PowerConstant.attribute_user[0])
					&& !key.equals(PowerConstant.attribute_user[1]) && !key.equals(PowerConstant.attribute_user[2])) {
				hash.put(key, new DZFBoolean("N"));
			}
		}
	}

	public void setAttributeValue(String name, Object value) {
		if (name == null)
			return;
		Object ob = hash.get(name);
		if (ob != null)
			hash.remove(name);
		if (value == null) {
			hash.put(name, "");
		} else {
			hash.put(name, value);
		}
	}

	public HashMap<String, Object> getHash() {
		return hash;
	}

	public String getPk_tzpz_h() {
		return pk_tzpz_h;
	}
	
	public void setPk_tzpz_h(String pk_tzpz_h) {
		this.pk_tzpz_h = pk_tzpz_h;
	}

	public String getGs() {
		return (String)hash.get("gs");
	}

	public String getRq() {
		return (String) hash.get("rq");
	}

	public String getPzh() {
		return (String) hash.get("pzh");
	}

	public String getZy() {
		return (String) hash.get("zy");
	}

	public String getJf() {
		return (String) hash.get("jf");
	}

	public String getDf() {
		return (String) hash.get("df");
	}

	public String getFx() {
		return (String) hash.get("fx");
	}

	public String getYe() {
		return (String) hash.get("ye");
	}

	public String getKmbm() {
		return (String) hash.get("kmbm");
	}

	public String getPk_accsubj() {
		return (String) hash.get("pk_accsubj");
	}

	public String getKm() {
		return (String) hash.get("km");
	}

	public String getBz() {
		return (String) hash.get("bz");
	}

	public String getPk_corp() {
		return (String) hash.get("pk_corp");
	}

	public String getPzpk() {
		return (String) hash.get("pzpk");
	}

}

package com.dzf.zxkj.common.entity;

import com.dzf.zxkj.common.model.SuperVO;

import java.util.LinkedHashMap;
import java.util.Set;

public class DynamicAttributeVO extends SuperVO {

	public LinkedHashMap<String, Object> hash;

	public DynamicAttributeVO(LinkedHashMap<String, Object> hash) {
		this.hash = hash;
	}

	public String[] getAttributeNames() {
		Set<String> s = hash.keySet();

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

	@Override
	public String getParentPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPKFieldName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTableName() {
		// TODO Auto-generated method stub
		return null;
	}

}

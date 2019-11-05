package com.dzf.zxkj.platform.service.qcset.impl;

import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.platform.model.qcset.QcYeCurrency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QcYeCommon {

	
	
	/**
	 * 分组
	 */
	public  Map<String, QcYeCurrency> hashlizeObject(List<QcYeCurrency> list)
			throws BusinessException {
		Map<String, QcYeCurrency> result = new HashMap<String, QcYeCurrency>();
		if (list == null || list.isEmpty())
			return result;
		for (int i = 0; i < list.size(); i++) {
			String key = list.get(i).getPk_currency();
			result.put(key, list.get(i));
		}
		return result;
	}
	
	public QcYeCurrency[] outRepeat(List<QcYeCurrency> list){
		Map<String,QcYeCurrency> map = new HashMap<String,QcYeCurrency>();
		List<QcYeCurrency> zlist = new ArrayList<QcYeCurrency>();
		for(QcYeCurrency v : list){
			String key = v.getPk_currency();
			if(!map.containsKey(key)){
				map.put(key, v);//使用map,需要实现排序接口
				zlist.add(v);
			}
		}
		return zlist.toArray(new QcYeCurrency[0]);
	}
	
	/**
	 * 返回in('xxx','xxx'...'xxx')形式
	 */
	public String getInWhereClauseVO(String[] args){
		if(args == null || args.length == 0)
			return null;
		StringBuffer sf = new StringBuffer();
		sf.append(" in (");
		for(int i = 0 ; i < args.length ; i++){
			if(i == 0 ){
				sf.append(" '"+args[i]+"'");
			}else{
				sf.append(",'"+args[i]+"'");
			}
		}
		sf.append(")");
		return sf.toString();
	}
}

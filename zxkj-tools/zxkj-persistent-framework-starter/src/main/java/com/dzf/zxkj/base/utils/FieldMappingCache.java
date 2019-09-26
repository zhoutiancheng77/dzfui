package com.dzf.zxkj.base.utils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FieldMappingCache {
private static FieldMappingCache fc=new FieldMappingCache();
private SoftReferenceMap<String, String[][]> map=new SoftReferenceMap<String,String[][]>();
//private Map<String, SoftReference<Map<String,String>>> map=new ConcurrentHashMap<String, SoftReference<Map<String, String>>>();//<String, SoftReference<Map<String,String>>>();


public void add(String key,Map<String,String> m){
	int len=m.size();
	String[][] strs=new String[len][2];
	String[] ss=m.keySet().toArray(new String[0]);
	for (int i = 0; i < len; i++) {
		strs[i][0]=ss[i];
		strs[i][1]=m.get(ss[i]);
	}
	map.put(key, strs);
}
public Map<String,String> get(String key){
	String[][] strs= map.get(key);
	Map<String,String> mm=new ConcurrentHashMap<String, String>();
	int len=strs==null?0:strs.length;
	if(strs==null)return null;
	for (int i = 0; i < len; i++) {
		mm.put(strs[i][0],strs[i][1]);
	}
	return mm;
}

	private FieldMappingCache() {

	}
public static FieldMappingCache getInstance(){
	return fc;
}
}

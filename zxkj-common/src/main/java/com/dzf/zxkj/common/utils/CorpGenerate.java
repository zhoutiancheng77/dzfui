package com.dzf.zxkj.common.utils;

import java.util.Arrays;
import java.util.concurrent.locks.ReentrantLock;

public class CorpGenerate {

	private CorpGenerate() {
		// TODO Auto-generated constructor stub
	}
	private static ReentrantLock lock=new ReentrantLock();

//	private static final char[] IDKEYS={'0','1','2','3','4','5','6','7','8','9',
//		'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
//		'Q','R','S','T','U','V','W','X','Y','Z','a','b',
//		'c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r',
//		's','t','u','v','w','x','y','z'};
	private static final int IDKEYSLen=IDefaultValue.IDKEYS.length;
	private static CorpGenerate idGenerate=null;
	public static CorpGenerate getInstance(){
		if(idGenerate==null){
			synchronized (CorpGenerate.class) {
				if(idGenerate==null)
					idGenerate=new CorpGenerate();
			}
		}
		return idGenerate;
	}
	
	public  String getNextID(String corp){
		String[] s=getNextIDS(corp,1);
		return s!=null&&s.length>0?s[0]:null;
	}
public  String[] getNextIDS(String corp,int len){
	
	lock.lock();
	String[] strs=new String[len];
	try{
	char[] ct=corp.toCharArray();
	
	
	for(int j=0;j<len;j++){
	getNextBit(ct, 5);

	strs[j]=String.valueOf(ct);
	}
	}finally{
	lock.unlock();
	}
	return strs;
}
private static void getNextBit(char[] ct,int index){
	
	int nindex=getIndex(ct[index]);// list.indexOf(ct[index]);
	nindex=nindex+1;
	if(nindex<IDKEYSLen){
		ct[index]=IDefaultValue.IDKEYS[nindex];
	}else{
		ct[index]=IDefaultValue.IDKEYS[0];
		getNextBit(ct, index-1);
	}
	
	
}
private static int getIndex(char c){
	return Arrays.binarySearch(IDefaultValue.IDKEYS, c);
}
}

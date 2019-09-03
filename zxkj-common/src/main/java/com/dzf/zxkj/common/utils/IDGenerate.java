package com.dzf.zxkj.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class IDGenerate {
//	String[] servers = new String[] {"0", "1","2","3","4","5","6","7","8","9"};
private static	List<String> list=new ArrayList<String>();
private static List<ReentrantLock> llock=new ArrayList<ReentrantLock>();
static{
	for (int i = 0; i < 10; i++) {
		llock.add(new ReentrantLock());
		list.add(String.valueOf(i));
	}
};

private static	ConsistentHash<String> consHash = new ConsistentHash<String>(100,list);
//private static final char[] IDKEYS={'0','1','2','3','4','5','6','7','8','9',
//	'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P',
//	'Q','R','S','T','U','V','W','X','Y','Z','a','b',
//	'c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r',
//	's','t','u','v','w','x','y','z'};
private static final int IDKEYSLen=IDefaultValue.IDKEYS.length;
	private static ConcurrentHashMap<String, char[]> chm=new ConcurrentHashMap<String, char[]>();
	private IDGenerate() {
		
		
		
	}
	private static IDGenerate idGenerate=null;
	public static IDGenerate getInstance(){
		if(idGenerate==null){
			synchronized (IDGenerate.class) {
				if(idGenerate==null)
					idGenerate=new IDGenerate();
			}
		}
		return idGenerate;
	}
	protected static void init(String corp) {
		if(chm.contains(corp))return;
		long time=System.currentTimeMillis();
		time=time-1440209716717l;
		char[] cs=new char[24];
		Arrays.fill(cs,IDefaultValue.IDKEYS[0]);
		int index=4;
		int y=1;
		while(time>0){
			y=(int) (time%IDKEYSLen);
			cs[index]=IDefaultValue.IDKEYS[y];
			index++;
			time=time/IDKEYSLen;
		}
		y=index;
		char[] css=new char[y];
		System.arraycopy(cs,0, css, 0,index);
		chm.put(corp, css);
	}
	public  String getNextID(String corp){
		String[] s=getNextIDS(corp,1);
		return s!=null&&s.length>0?s[0]:null;
	}
public  String[] getNextIDS(String corp,int len){
	String key=consHash.getByData(corp);
	ReentrantLock lock=llock.get(Integer.valueOf(key));
	lock.lock();
	String[] strs=new String[len];
	try{
	char[] ct=chm.get(corp);
	if(ct==null){
		init(corp);
		ct=chm.get(corp);
	}
	//List<char> list=Arrays.asList(IDKEYS);
	
	char[] cs=new char[24];
	Arrays.fill(cs, IDefaultValue.IDKEYS[0]);
//	for(int i=0;i<16;i++){
//		cs[i]=IDKEYS[0];
//	}
	int len0=0;
	for(int j=0;j<len;j++){
	getNextBit(ct, 0);

	System.arraycopy(corp.toCharArray(), 0, cs, 0, 6);
	len0=ct.length;
	StringBuffer sb=new StringBuffer(len0);
	char[] css=new char[len0];
	for (int i = len0-1; i>=0; i--) {
		sb.append(ct[i]);
	}
	
	System.arraycopy(sb.toString().toCharArray(), 0, cs, 24-len0, len0);
	strs[j]=String.valueOf(cs);
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
		getNextBit(ct, index+1);
	}
	
	
}
private static int getIndex(char c){
//	int len=IDKEYSLen;
//	for (int i = 0; i < len; i++) {
//		if(IDKEYS[i]==c)
//			return i;
//	}
//	return -1;
	return Arrays.binarySearch(IDefaultValue.IDKEYS, c);
}
public static void main(String[] s){
	IDGenerate id=IDGenerate.getInstance();
	String[] ss=		id.getNextIDS("000002", 5);
	String[] ss1=		id.getNextIDS("000005", 5);
//	IDGenerate id=new IDGenerate();
//	new Thread(){
//
//		@Override
//		public void run() {
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//			}
//			String[] ss=	new IDGenerate().getNextIDS("0002", 5);
//		}
//		
//	}.start();
//	new Thread(){
//
//		@Override
//		public void run() {
//			try {
//				Thread.sleep(100);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				//e.printStackTrace();
//			}
//			String[] ss=	new IDGenerate().getNextIDS("0002", 5);
//		}
//		
//	}.start();
//String[] ss=	new IDGenerate().getNextIDS("0002", 5);
////System.out.print(ss[0]);
}
}

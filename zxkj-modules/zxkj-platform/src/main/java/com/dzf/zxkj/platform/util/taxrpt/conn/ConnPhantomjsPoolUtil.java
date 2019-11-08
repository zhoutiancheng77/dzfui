package com.dzf.zxkj.platform.util.taxrpt.conn;

public class ConnPhantomjsPoolUtil {

	private ConnPhantomjsPool connectionPool = null;
	
	private static ConnPhantomjsPoolUtil phantomjsutil ;
	
	private ConnPhantomjsPoolUtil(){
		init();
	}
	
	public void init() {
		connectionPool = new ConnPhantomjsPool(3, 10, 1);
	}
	
	public static synchronized ConnPhantomjsPoolUtil getInstance(){
		if(phantomjsutil == null){
			phantomjsutil = new ConnPhantomjsPoolUtil();
		}
		return phantomjsutil;
	}
	
	public String checkout(){
		String id = null;
		try{
			id = connectionPool.checkout();
		}catch(Exception e){
			
		}
		return id;
	}
	
	public void checkin(String id){
		connectionPool.checkin(id);
	}
	
}
package com.dzf.zxkj.app.utils;

import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;


public class BaseTimerCache<K,V> {
	public static void main(String[] s){
		BaseTimerCache<String,String> bt=new BaseTimerCache<String, String>();
		bt.put("1", "2");
		bt.put("w1", "2");
		bt.put("we1", "2");
		bt.put("1", "2");
		try {
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		System.out.println(0);
	}
	private int remain=2000;//存在2000秒
	 public int getRemain() {
		return remain;
	}
	public void setRemain(int remain) {
		this.remain = remain;
	}
	private int FreshTimerIntervalSeconds = 200;//每隔200秒刷新一次。
	public int getFreshTimerIntervalSeconds() {
		return FreshTimerIntervalSeconds;
	}
	public void setFreshTimerIntervalSeconds(int freshTimerIntervalSeconds) {
		FreshTimerIntervalSeconds = freshTimerIntervalSeconds;
	}

	private class TimeVO<K,V>{
	private long ltime;
	private K k;
	private V v;
		public TimeVO(K k,V v) {
			super();
			this.k=k;
			this.v=v;
			ltime=System.currentTimeMillis();
		}
		public long getLtime() {
			return ltime;
		}
		public void setLtime(long ltime) {
			this.ltime = ltime;
		}
		public K getK() {
			return k;
		}
		public V getV() {
			return v;
		}
		
	}
private ConcurrentHashMap<K, TimeVO<K,V>> hm=new ConcurrentHashMap<K, TimeVO<K,V>>();
	public BaseTimerCache() {
		TimerTask task = new TimerTask(){

			@Override
			public void run() {
			Collection<TimeVO<K,V>>	ction=hm.values();
			long l;
			long cl=System.currentTimeMillis();
			long rl=getRemain()*1000;
			for (TimeVO<K, V> timeVO : ction) {
				l=timeVO.getLtime();
		        l=cl-l;
		        if(l>rl){
		        	hm.remove(timeVO.getK());
		        }
		        	
			}
				
			}
			
		};
		Timer timer = new Timer("SimpleCache_Timer", true);
        timer.scheduleAtFixedRate(task, 1000, getFreshTimerIntervalSeconds() * 1000);//每格一秒刷新一次(缓存中
	}
	public V getValue(K k){
		TimeVO<K,V> t=hm.get(k);
		V v=null;
		if(t!=null){
			t.setLtime(System.currentTimeMillis());
			v=t.getV();
		}

		return v;
	}
	public void put(K k,V v){
		TimeVO<K,V> t=new TimeVO<K, V>(k, v);
		hm.put(k, t);
	}
	
	public void remove(K k){
		hm.remove(k);
	}
	
	public boolean containsKey(K k){
		return hm.containsKey(k);
	}
}

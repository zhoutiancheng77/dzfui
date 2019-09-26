package com.dzf.zxkj.platform.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ThreadImgPoolExecutorFactory {

	//创建图片处理线程池
	private static  ExecutorService fixedThreadPool = Executors.newFixedThreadPool(20);
	
	public static ExecutorService getInstance(){
//		if(fixedThreadPool == null)
//			fixedThreadPool = Executors.newScheduledThreadPool(20);
		return fixedThreadPool;
	}
}

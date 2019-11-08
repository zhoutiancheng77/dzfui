package com.dzf.zxkj.platform.util.taxrpt.conn;

import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.dzf.pub.StringUtil;

/**
 * 
 * phantomjs连接池
 * 
 */
public class ConnPhantomjsPool {
	private static final Logger LOGGER =Logger.getLogger(ConnPhantomjsPool.class);
	/** 空闲的连接池 */
	private LinkedBlockingQueue<String> idleConnectionPool = null;
	/** 连接池默认最小连接数 */
	private long minPoolSize = 3;
	/** 连接池默认最大连接数 */
	private long maxPoolSize = 10;
	/** 当前创建的连接数 */
	private volatile int nowPoolSize = 0;
	/** 默认等待时间（单位：秒） */
	private long waitTimes = 3;
	/** 创建连接默认1次 */
	private static final int COUNT = 1;

	public ConnPhantomjsPool(long minPoolSize, long maxPoolSize, long waitTimes) {
		this.minPoolSize = minPoolSize;
		this.maxPoolSize = maxPoolSize;
		this.waitTimes = waitTimes;
		poolInit();
	}

	private void poolInit() {
		try {
			idleConnectionPool = new LinkedBlockingQueue<String>();
			for (int i = 0; i < minPoolSize; i++) {
				String logId = UUID.randomUUID().toString();
				createTrackerServer(logId, COUNT);
			}
		} catch (Exception e) {
			LOGGER.error("初始化访问phantomjs连接池错误!", e);
		}
	}

	private void createTrackerServer(String logId, int flag) {
		try {
			if(!StringUtil.isEmpty(logId)){
				idleConnectionPool.add(logId);
				/** 同一时间只允许一个线程对nowPoolSize操作 **/
				synchronized (this) {
					nowPoolSize++;
				}
			}
		} catch (Exception e) {
			LOGGER.error("初始化访问phantomjs队列错误", e);
		}
	}


	public String checkout() throws Exception {
		String requestid = idleConnectionPool.poll();
		if (StringUtil.isEmpty(requestid)) {
			if (nowPoolSize < maxPoolSize) {
				String logId = UUID.randomUUID().toString();
				createTrackerServer(logId, COUNT);
				try {
					requestid = idleConnectionPool.poll(waitTimes,TimeUnit.SECONDS);
				} catch (Exception e) {
					LOGGER.error("新增访问phantomjs队列连接失败",e);
				}
			}
			if (StringUtil.isEmpty(requestid)) {
				LOGGER.error("获取访问phantomjs队列连接失败，请稍候");
			}
		}
		return requestid;
	}

	/**
	 * 
	 * 
	 */

	public void checkin(String requestid) {
		if (!StringUtil.isEmpty(requestid)) {
			if (idleConnectionPool.size() < minPoolSize) {
				idleConnectionPool.add(requestid);
			} else {
				synchronized (this) {
					if (nowPoolSize != 0) {
						nowPoolSize--;
					}
				}
			}
		}
	}

	public LinkedBlockingQueue<String> getIdleConnectionPool() {
		return idleConnectionPool;
	}

	public long getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(long minPoolSize) {
		if (minPoolSize != 0) {
			this.minPoolSize = minPoolSize;
		}
	}

	public long getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(long maxPoolSize) {
		if (maxPoolSize != 0) {
			this.maxPoolSize = maxPoolSize;
		}
	}

	public long getWaitTimes() {
		return waitTimes;
	}

	public void setWaitTimes(int waitTimes) {
		if (waitTimes != 0) {
			this.waitTimes = waitTimes;
		}
	}
}
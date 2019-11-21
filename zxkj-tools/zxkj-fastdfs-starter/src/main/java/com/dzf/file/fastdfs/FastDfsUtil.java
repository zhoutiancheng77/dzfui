package com.dzf.file.fastdfs;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.StorageClient1;
import org.csource.fastdfs.StorageServer;
import org.csource.fastdfs.TrackerServer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.SocketTimeoutException;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 *
 * @ClassName: FastDfsUtil
 * @Description: fastdfs文件操作工具类 1).初始化连接池； 2).实现文件的上传与下载;
 * @author mr_smile2014 mr_smile2014@xxxx.com
 * @date 2015年11月25日 上午10:21:46
 *
 */
@Slf4j
public class FastDfsUtil {
	/** 连接池 */
	private ConnectionPool connectionPool = null;
	/** 连接池默认最小连接数 */
	private long minPoolSize = 10;
	/** 连接池默认最大连接数 */
	private long maxPoolSize = 30;
	/** 当前创建的连接数 */
	private volatile long nowPoolSize = 0;
	/** 默认等待时间（单位：秒） */
	private long waitTimes = 200;

	private static FastDfsUtil fastutil ;

	private FastDfsUtil(){
		init();
	}

	public static synchronized FastDfsUtil getInstance(){
		if(fastutil == null){
			fastutil = new FastDfsUtil() ;// (FastDfsUtil) SpringUtils.getBean("connectionPool");
		}
		return fastutil;
	}

	/**
	 * 初始化线程池
	 *
	 * @Description:
	 *
	 */
	public void init() {
//		String logId = UUID.randomUUID().toString();
//		LOGGER.info("[初始化线程池(Init)][" + logId + "][默认参数：minPoolSize="
//				+ minPoolSize + ",maxPoolSize=" + maxPoolSize + ",waitTimes="
//				+ waitTimes + "]");
		connectionPool = new ConnectionPool(minPoolSize, maxPoolSize, waitTimes);
	}


	/**
	 * 通过字节流上传文件
	 * @param file
	 * @param filename
	 * @param metaList
	 * @return
	 * @throws AppException
	 */
	public String upload(File file,String filename, Map<String,String> metaList) throws AppException{
	    if(!file.exists()){
	    	throw ERRORS.NOT_EXIST_FILE.ERROR();
	    }
	    FileInputStream is = null;
	    byte[] filebytes = null;
		try {
			is =new FileInputStream(file);
			filebytes = IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw ERRORS.READ_FILE_ERROR.ERROR();
		}finally{
			if(is != null){
				IOUtils.closeQuietly(is);
			}
		}

		return upload(filebytes, filename, metaList);
	}

	/**
	 * 通过图片bufferedimage上传
	 * @param buffer
	 * @param filename
	 * @param metaList
	 * @return
	 * @throws AppException
	 */
	public String upload(BufferedImage buffer,String filename, Map<String,String> metaList) throws AppException{
	    if(buffer == null){
	    	throw ERRORS.NOT_EXIST_FILE.ERROR();
	    }

	    byte[] filebytes = null;
	    ByteArrayOutputStream os = null ;
	    InputStream is = null ;
		try {
			String type = FilenameUtils.getExtension(filename);
			os = new ByteArrayOutputStream();
			ImageIO.write(buffer, type, os);
			 is = new ByteArrayInputStream(os.toByteArray());
			filebytes = IOUtils.toByteArray(is);
		} catch (IOException e) {
			throw ERRORS.READ_FILE_ERROR.ERROR();
		}finally {
			if(is!=null){
				try {
					is.close();
				} catch (IOException e) {
					throw ERRORS.READ_FILE_ERROR.ERROR();
				}
			}

			if(os!=null){
				try {
					os.close();
				} catch (IOException e) {
					throw ERRORS.READ_FILE_ERROR.ERROR();
				}
			}

		}

		return upload(filebytes, filename, metaList);
	}

	/**
	 * 通过字节流上传文件
	 * @param filebytes
	 * @param fileName
	 * @param metaList
	 * @return
	 * @throws AppException
	 */
	public String upload(byte[] filebytes,String fileName, Map<String,String> metaList) throws AppException{
		NameValuePair[] nameValuePairs = null;
        if (metaList != null) {
            nameValuePairs = new NameValuePair[metaList.size()];
            int index = 0;
            for (Iterator<Map.Entry<String,String>> iterator = metaList.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,String> entry = iterator.next();
                String name = entry.getKey();
                String value = entry.getValue();
                nameValuePairs[index++] = new NameValuePair(name,value);
            }
        }
		return upload("",filebytes, FilenameUtils.getExtension(fileName) ,"",  nameValuePairs);
	}


	public byte[] downFile(String file_id) throws AppException{
		String logId = UUID.randomUUID().toString();
		TrackerServer trackerServer = null;
		byte[] results= null;
		try {

			/** 获取fastdfs服务器连接 */
			trackerServer = connectionPool.checkout(logId);
			StorageServer storageServer = null;
			StorageClient1 client1 = new StorageClient1(trackerServer,
					storageServer);

			/** 以文件字节的方式上传 */
			  results = client1.download_file1(file_id);

			/** 上传完毕及时释放连接 */
			connectionPool.checkin(trackerServer, logId);


		} catch (AppException e) {
			log.error("[下载文件（upload)][" + logId + "][异常：" + e + "]");
			throw e;
		} catch (SocketTimeoutException e) {
			log.error("[下载文件（upload)][" + logId + "][异常：" + e + "]");
			throw ERRORS.WAIT_IDLECONNECTION_TIMEOUT.ERROR();
		} catch (Exception e) {
			log.error("[下载文件（upload)][" + logId + "][异常：" + e + "]");
			connectionPool.drop(trackerServer, logId);
			throw ERRORS.SYS_ERROR.ERROR();
		}

		return results;
	}

	/**
	 *
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param groupName
	 *            组名如group0
	 * @param fileBytes
	 *            文件字节数组
	 * @param extName
	 *            文件扩展名：如png
	 * @param linkUrl
	 *            访问地址：http://image.xxx.com
	 * @return 图片上传成功后地址
	 * @throws AppException
	 *
	 */
	public String upload(String groupName, byte[] fileBytes, String extName,
			String linkUrl,NameValuePair[] nameValuePairs) throws AppException {
		String logId = UUID.randomUUID().toString();
//		/** 封装文件信息参数 */
//		NameValuePair[] metaList = new NameValuePair[] { new NameValuePair(
//				"fileName", "") };
		TrackerServer trackerServer = null;
		try {

			/** 获取fastdfs服务器连接 */
			trackerServer = connectionPool.checkout(logId);
			StorageServer storageServer = null;
			StorageClient1 client1 = new StorageClient1(trackerServer,
					storageServer);

			/** 以文件字节的方式上传 */
			String[] results = client1.upload_file(groupName, fileBytes,
					extName, nameValuePairs);

			/** 上传完毕及时释放连接 */
			connectionPool.checkin(trackerServer, logId);

			if (results != null && results.length >0){
				log.info("[上传文件（upload）-fastdfs服务器相应结果][" + logId
						+ "][result：results=" + results.length + "]");
			}else{
				log.info("[上传文件（upload）-fastdfs服务器相应结果][" + logId
						+ "][result：results= null]");
			}


			/** results[0]:组名，results[1]:远程文件名 */
			if (results != null && results.length == 2) {
				return linkUrl + "/" + results[0] + "/" + results[1];
			} else {
				/** 文件系统上传返回结果错误 */
				throw ERRORS.UPLOAD_RESULT_ERROR.ERROR();
			}
		} catch (AppException e) {
			log.error("[上传文件（upload)][" + logId + "][异常：" + e + "]");
			throw e;
		} catch (SocketTimeoutException e) {
			log.error("[上传文件（upload)][" + logId + "][异常：" + e + "]");
			throw ERRORS.WAIT_IDLECONNECTION_TIMEOUT.ERROR();
		} catch (Exception e) {
			log.error("[上传文件（upload)][" + logId + "][异常：" + e + "]");
			connectionPool.drop(trackerServer, logId);
			throw ERRORS.SYS_ERROR.ERROR();
		}

	}

	/**
	 *
	 * @Description: 删除fastdfs服务器中文件
	 * @param file_Id
	 *            组名
	 * remote_filename
	 *            远程文件名称
	 * @throws AppException
	 *
	 */
	public void deleteFile(String file_Id)
			throws AppException {
/*
		String logId = UUID.randomUUID().toString();
		LOGGER.info("[ 删除文件（deleteFile）][" + logId + "][parms：file_id="
				+ file_Id + "]");
		TrackerServer trackerServer = null;

		try {
			*//** 获取可用的tracker,并创建存储server *//*
			trackerServer = connectionPool.checkout(logId);
			StorageServer storageServer = null;
			StorageClient1 client1 = new StorageClient1(trackerServer,
					storageServer);
			*//** 删除文件,并释放 trackerServer *//*
			int result = client1.delete_file1(file_Id);

			*//** 上传完毕及时释放连接 *//*
			connectionPool.checkin(trackerServer, logId);

			LOGGER.info("[ 删除文件（deleteFile）--调用fastdfs客户端返回结果][" + logId
					+ "][results：result=" + result + "]");

			*//** 0:文件删除成功，2：文件不存在 ，其它：文件删除出错 *//*
			if (result == 2) {

				throw ERRORS.NOT_EXIST_FILE.ERROR();

			} else if (result != 0) {

				throw ERRORS.DELETE_RESULT_ERROR.ERROR();

			}

		} catch (AppException e) {

			LOGGER.error("[ 删除文件（deleteFile）][" + logId + "][异常：" + e + "]");
			throw e;

		} catch (SocketTimeoutException e) {
			LOGGER.error("[ 删除文件（deleteFile）][" + logId + "][异常：" + e + "]");
			throw ERRORS.WAIT_IDLECONNECTION_TIMEOUT.ERROR();
		} catch (Exception e) {

			LOGGER.error("[ 删除文件（deleteFile）][" + logId + "][异常：" + e + "]");
			connectionPool.drop(trackerServer, logId);
			throw ERRORS.SYS_ERROR.ERROR();

		}*/
	}

	public ConnectionPool getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool) {
		this.connectionPool = connectionPool;
	}

	public long getMinPoolSize() {
		return minPoolSize;
	}

	public void setMinPoolSize(long minPoolSize) {
		this.minPoolSize = minPoolSize;
	}

	public long getMaxPoolSize() {
		return maxPoolSize;
	}

	public void setMaxPoolSize(long maxPoolSize) {
		this.maxPoolSize = maxPoolSize;
	}

	public long getNowPoolSize() {
		return nowPoolSize;
	}

	public void setNowPoolSize(long nowPoolSize) {
		this.nowPoolSize = nowPoolSize;
	}

	public long getWaitTimes() {
		return waitTimes;
	}

	public void setWaitTimes(long waitTimes) {
		this.waitTimes = waitTimes;
	}

	public int modifyFile(String file_id,byte[] buffers) throws AppException{
		String logId = UUID.randomUUID().toString();
		TrackerServer trackerServer = null;

		int modifyres =0;
		try {

			/** 获取fastdfs服务器连接 */
			trackerServer = connectionPool.checkout(logId);
			StorageServer storageServer = null;
			StorageClient1 client1 = new StorageClient1(trackerServer,
					storageServer);

			/** 以文件字节的方式上传 */
			modifyres =  client1.modify_file1(file_id, -1000, buffers);

			/** 上传完毕及时释放连接 */
			connectionPool.checkin(trackerServer, logId);


		} catch (AppException e) {
			log.error("[修改文件（modify)][" + logId + "][异常：" + e + "]");
			throw e;
		} catch (SocketTimeoutException e) {
			log.error("[修改文件（modify)][" + logId + "][异常：" + e + "]");
			throw ERRORS.WAIT_IDLECONNECTION_TIMEOUT.ERROR();
		} catch (Exception e) {
			log.error("[修改文件（modify)][" + logId + "][异常：" + e + "]");
			connectionPool.drop(trackerServer, logId);
			throw ERRORS.SYS_ERROR.ERROR();
		}

		return modifyres;
	}
}
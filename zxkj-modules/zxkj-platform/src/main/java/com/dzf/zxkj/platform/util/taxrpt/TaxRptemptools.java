package com.dzf.zxkj.platform.util.taxrpt;

import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class TaxRptemptools {

	
	public static  String readFileString(String filepath) throws DZFWarpException {
		
		String sReturn = null;
		if(filepath.startsWith("*")){
			try {
				byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(filepath.substring(1));
				
				if(bytes!=null && bytes.length>0){
					sReturn = new String(bytes, "utf-8");
				}
				return sReturn;
			} catch (Exception e) {
				throw new WiseRunException(e);
			}
			
		}
		
		File f = new File(filepath);
		if (f.exists() && f.isFile()) {
			int byteread = 0;
			int bytesum = 0;

			FileInputStream inStream = null;
			ByteOutputStream bos = null;
			try {
				inStream = new FileInputStream(f);

				bos = new ByteOutputStream();
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					bos.write(buffer, 0, byteread);
				}
				bos.flush();
				byte[] bs = bos.toByteArray();
				sReturn = new String(bs, "utf-8");
			} catch (Exception e) {
				throw new WiseRunException(e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException ioe) {
					}
				}
				if (bos != null)
					bos.close();
			}
		}

		return sReturn;
	}
	
	public static   byte[] readFileBytes(String filepath) throws DZFWarpException {
		byte[] btsReturn = null;
		
		if(filepath.startsWith("*")){
			try {
				byte[] bytes = ((FastDfsUtil)SpringUtils.getBean("connectionPool")).downFile(filepath.substring(1));
				
				return bytes;
				
			} catch (Exception e) {
				throw new WiseRunException(e);
			}
			
		}
		
		File f = new File(filepath);
		if (f.exists() && f.isFile()) {
			int byteread = 0;
			int bytesum = 0;

			FileInputStream inStream = null;
			ByteOutputStream bos = null;
			try {
				inStream = new FileInputStream(f);

				bos = new ByteOutputStream();
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					bos.write(buffer, 0, byteread);
				}
				bos.flush();
				btsReturn = bos.toByteArray();

			} catch (Exception e) {
				throw new WiseRunException(e);
			} finally {
				if (inStream != null) {
					try {
						inStream.close();
					} catch (IOException ioe) {
					}
				}
				if (bos != null)
					bos.close();
			}
		}

		return btsReturn;
	}
	
}

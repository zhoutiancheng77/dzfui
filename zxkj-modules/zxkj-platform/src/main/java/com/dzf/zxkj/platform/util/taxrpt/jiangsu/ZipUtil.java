package com.dzf.zxkj.platform.util.taxrpt.jiangsu;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * 
 *  服务支撑平台
 * <p>net.chnbs.comm.utils
 * <p>File: zipUtil.java 创建时间:2016-6-21上午11:16:19</p> 
 * <p>Title:字符串压缩加解密</p>
 * <p>Description: 描述（简要描述类的职责、实现方式、使用注意事项等）</p>
 * <p>Copyright: Copyright (c) 2016 江苏中润四方科技有限公司</p>
 * <p>Company: 江苏中润四方科技有限公司</p>
 * <p>模块: 公共模块</p>
 * @author  Administrator
 * @version 1.0
 * @history 修订历史（历次修订内容、修订人、修订时间等）
 */
@Slf4j
public class ZipUtil {
//	private static final Logger logger=Logger.getLogger(ZipUtil.class);
	/**
	 * 压缩加密(zip压缩，base64加密)
	 * （先压缩、在base64加密）
	 * @param str
	 * @param iszip	判断是否base64加密， false表示只做base64操作
	 * @return
	 */
	public static String zipEncode(String str,boolean iszip){
		String encodeStr="";
		try{
		
			if(iszip){
				encodeStr=new sun.misc.BASE64Encoder().encodeBuffer(compress(str));
			}
			else{
				encodeStr=new sun.misc.BASE64Encoder().encodeBuffer(str.getBytes("UTF-8")).trim();
			}
			if(null!=encodeStr){
				encodeStr = encodeStr.replaceAll("\r\n", "").replaceAll("\n","");
			}
		}catch(Exception e){
			log.error("压缩加密失败:"+str,e);
//			e.printStackTrace();
		}
		return encodeStr;
	}
	
	/**
	 * 压缩
	 * 
	 * 这里需要特别注意的是，如果你想把压缩后的byte[]保存到字符串中，
	 * 不能直接使用new String(byte)或者byte.toString()， 因为这样转换之后容量是增加的。
	 * 同样的道理，如果是字符串的话，也不能直接使用new String().getBytes()获取byte[]传入到decompress中进行解压缩。
	 * 如果保存压缩后的二进制，可以使用new sun.misc.BASE64Encoder().encodeBuffer(byte[] b)将其转换为字符串。
	 * 同样解压缩的时候首先使用new BASE64Decoder().decodeBuffer 方法将字符串转换为字节，然后解压就可以了。
     *
	 * @param str
	 * @return
	 */
	private static byte[] compress(String str) {
		if (str == null)
			return null;

		byte[] compressed;
		ByteArrayOutputStream out = null;
		ZipOutputStream zout = null;

		try {
			out = new ByteArrayOutputStream();
			zout = new ZipOutputStream(out);
			zout.putNextEntry(new ZipEntry("0"));
			zout.write(str.getBytes("UTF-8"));
			zout.closeEntry();
			compressed = out.toByteArray();
		} catch (IOException e) {
			compressed = null;
		} finally {
			if (zout != null) {
				try {
					zout.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return compressed;
	}
	/**
	 * 解密(压缩解密)
	 * （先base64解密，在解压）
	 * @param zipStr
	 * @param iszip	是否是压缩文件， false表示只做base64操作
	 * @return
	 */
	public static String unzipDecode(String zipStr,boolean iszip) {	
		String unzipStr="";
		try{
			 byte[] unzip=new sun.misc.BASE64Decoder().decodeBuffer(zipStr);
		     if(iszip){			    
		        unzipStr=decompress(unzip);
		     }
		     else{
			 unzipStr= new String(unzip,"UTF-8");
		    }
		}
		catch(Exception e){
			log.error("压缩解密失败:"+zipStr,e);
//			e.printStackTrace();
		}
		
		return unzipStr;
	}
	/**
	 * 解压
	 * @param compressed
	 * @return
	 */
	private static String decompress(byte[] compressed) {
		if (compressed == null)
			return null;

		ByteArrayOutputStream out = null;
		ByteArrayInputStream in = null;
		ZipInputStream zin = null;
		String decompressed;
		try {
			out = new ByteArrayOutputStream();
			in = new ByteArrayInputStream(compressed);
			zin = new ZipInputStream(in);
			zin.getNextEntry();
			byte[] buffer = new byte[1024];
			int offset = -1;
			while ((offset = zin.read(buffer)) != -1) {
				out.write(buffer, 0, offset);
			}
			out.flush();
			decompressed = out.toString("UTF-8");
		} catch (IOException e) {
			decompressed = null;
		} finally {
			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}

		return decompressed;
	}

}

package com.dzf.zxkj.app.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IOUtils {

	public IOUtils() {
	}


	public static byte[] getBytes(String[] obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		NetObjectOutputStream nos = new NetObjectOutputStream(bout);
		try{
			int len = obj == null ? 0 : obj.length;
			nos.write(len);

			for (int i = 0; i < len; i++) {
				if (StringUtil.isEmptyWithTrim(obj[i])) {
					nos.writeByte(0);
					// nos.write(obj[i].getBytes());
				} else {
					nos.writeByte(obj[i].length());
					nos.write(obj[i].getBytes());
				}
			}
			// nos.writeObject(obj);
			nos.flush();
			
		}catch(Exception e){
			log.error("错误",e);
		}finally{
			if(bout!=null){
				bout.close();
			}
			if(nos!=null){
				nos.close();
			}
		}
		return bout.toByteArray();
	}

	public static byte[] getBytes(Object obj) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		NetObjectOutputStream nos = new NetObjectOutputStream(bout);
		try{
			nos.writeObject(obj);
			// int len=obj==null?0:obj.length;
			// nos.write(len);
			// for(int i=0;i<len;i++){
			// nos.writeUTF(obj[i]);
			// }
			// nos.writeObject(obj);
			nos.flush();
		}catch(Exception e){
			log.error("错误",e);
		}finally{
			if(bout!=null){
				bout.close();
			}
			if(nos!=null){
				nos.close();
			}
		}
		return bout.toByteArray();
	}

	public static byte[] getBytes(SuperVO svo, IDzfSerializable iser) throws Exception {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		NetObjectOutputStream nos = new NetObjectOutputStream(bout);
		try{
			iser.setSerializable(svo, nos);

			nos.flush();
		}catch(Exception e){
			log.error("错误",e);
		}finally{
			if(bout!=null){
				bout.close();
			}
			if(nos!=null){
				nos.close();
			}
		}
		return bout.toByteArray();
	}

	public static SuperVO getObject(byte[] bs, IDzfSerializable iser) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(bs);
		NetObjectInputStream is = new NetObjectInputStream(bin);
		SuperVO svo =  null;
		try{
			svo = (SuperVO) iser.getSerializable(is);
		}catch(Exception e){
			log.error("错误",e);
		}finally{
			if(bin!=null){
				bin.close();
			}
			if(is!=null){
				is.close();
			}
		}
		return svo;
	}

	public static String[] getObject(byte[] bs) throws Exception {
		ByteArrayInputStream bin = new ByteArrayInputStream(bs);
		NetObjectInputStream is = new NetObjectInputStream(bin);
		String[] strs = null;
		try{
			int len = is.read();
			strs = new String[len];
			int len1 = 0;
			byte[] bs1 = null;
			for (int i = 0; i < len; i++) {
				len1 = is.readByte();
				if (len1 > 0) {
					bs1 = new byte[len1];
					is.read(bs1);
					strs[i] = new String(bs1);// is.readUTF();
				}
			}
			// Object obj= is.readObject();
		}catch(Exception e){
			log.error("错误",e);
		}finally{
			if(bin!=null){
				bin.close();
			}
			if(is!=null){
				is.close();
			}
		}
		return strs;
	}

	public static Object getBytetoObj(byte[] bs) throws Exception {
		Object obj = null;
		ByteArrayInputStream bin = new ByteArrayInputStream(bs);
		NetObjectInputStream is = new NetObjectInputStream(bin);
		try {
			obj = is.readObject();
		} catch (Exception e) {
			log.error("错误",e);
		} finally {
			if(bin!=null){
				bin.close();
			}
			if(is!=null){
				is.close();
			}
		}
		return obj;
	}
}
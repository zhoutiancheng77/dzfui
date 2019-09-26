package com.dzf.zxkj.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * 对象序列化的字节大小
 * 
 */
@Slf4j
public class ObjectSerialByteTool {

	/**
	 * 显示字符串信息的最大长度
	 */
	private static final int MAX = 20;

	/**
	 * 打印一个对象序列化后的字节数
	 * 
	 * @param obj
	 *            要测试的对象
	 */
	public void printByte(Object obj) {
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		try {
			ObjectOutputStream o = new ObjectOutputStream(buf);
			o.writeObject(obj);
			byte[] bytes = buf.toByteArray();
			String value = obj.toString();
			if (obj.toString().length() > ObjectSerialByteTool.MAX) {
				value = value.substring(0, ObjectSerialByteTool.MAX) + ".....";
			}
			log.info("值为【" + value + "】,"); /*-=notranslate=-*/
			log.info("长度为【" + bytes.length + "】,"); /*-=notranslate=-*/
		} catch (IOException ex) {
			ExceptionUtils.wrappException(ex);
		}
	}

}

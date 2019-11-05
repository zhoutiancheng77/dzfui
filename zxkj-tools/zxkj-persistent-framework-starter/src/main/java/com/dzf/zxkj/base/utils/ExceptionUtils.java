package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常处理工具类
 * 
 */
@Slf4j
public class ExceptionUtils {

	private ExceptionUtils() {
		// 缺省构造方法
	}

	/**
	 * 异常处理工具类的工厂方法。
	 * 
	 * @return 返回异常处理工具类的实例
	 * @deprecated 用具体的static方法替代
	 */
	@Deprecated
	public static ExceptionUtils getInstance() {
		return new ExceptionUtils();
	}

	/**
	 * 抛出未实现异常
	 */
	public static void notImplement() {
		String message = "还没有实现此功能"; /*-=notranslate=-*/
		BusinessException ex = new BusinessException(message);
		log.error("错误",ex);

		throw ex;
	}

	/**
	 * 将最底层的异常解析出来
	 * 
	 * @param ex
	 *            要处理的异养
	 * @return 最底层的异常
	 */
	public static Throwable unmarsh(Throwable ex) {
		Throwable cause = ex.getCause();
		if (cause != null) {
			cause = ExceptionUtils.unmarsh(cause);
		} else {
			cause = ex;
		}
		return cause;
	}

	/**
	 * 抛出不支持异常
	 */
	public static void unSupported() {
		String message = "不支持此种业务，请检查"; /*-=notranslate=-*/
		BusinessException ex = new BusinessException(message);
		log.error("错误",ex);

		throw ex;
	}

	/**
	 * 抛出业务异常
	 * 
	 * @param message
	 *            异常信息
	 */
	public static void wrappBusinessException(String message) {
		BusinessException ex = new BusinessException(message);
		log.error("错误",ex);

		throw ex;
	}

	/**
	 * 抛出业务异常
	 * 
	 * @param message
	 *            异常信息
	 * @param location
	 *            出现异常的位置
	 */
	public static void wrappBusinessException(String message, String location) {
		BusinessException ex = new BusinessException(message, location);
		log.error("错误",ex);

		throw ex;
	}

	public static void wrappException(Exception ex) {
		if (ex instanceof BusinessException) {
			throw (BusinessException) ex;
		} else {
			wrappBusinessException(ex.getMessage(), ex.getLocalizedMessage());
		}
	}

}

package com.dzf.zxkj.common.exception;

/**
 * 
 * 数据访问对象异常类
 */
public class DAOException extends DZFWarpException {


	/**
	 * @param s
	 */
	public DAOException(String s) {
		super(s);
		// TODO 自动生成构造函数存根
	}

	/**
	 * @param message
	 * @param cause
	 */
	public DAOException(String message, Throwable cause) {
		super(message, cause);
		// TODO 自动生成构造函数存根
	}

	/**
	 * @param cause
	 */
	public DAOException(Throwable cause) {
		super("DAO异常",cause);
		// TODO 自动生成构造函数存根
	}

}

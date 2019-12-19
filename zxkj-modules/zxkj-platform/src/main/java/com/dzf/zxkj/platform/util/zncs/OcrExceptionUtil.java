package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import org.jboss.logging.Logger;

public class OcrExceptionUtil {

	private static Logger log = Logger.getLogger(OcrExceptionUtil.class);

	public static void dealOcrException(Exception e, String errorCode, String message) {
		if (e instanceof BusinessException) {
			log.error("错误",e);
			throw new BusinessException(e.getMessage(), errorCode);
		} else if (e instanceof DAOException) {
			log.error("错误",e);
			throw new BusinessException("数据库操作异常：" + message, errorCode);
		} else {
			log.error("错误",e);
			throw new BusinessException("异常：" + message, errorCode);
		}
	}
}

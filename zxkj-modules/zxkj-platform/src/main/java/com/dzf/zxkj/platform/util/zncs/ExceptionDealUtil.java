package com.dzf.zxkj.platform.util.zncs;

import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;
import org.jboss.logging.Logger;

public class ExceptionDealUtil {

	public static void dealException(OcrImageLibraryVO vo, Logger log, String moduleName, String message, Throwable t) {
		String msg = moduleName + "识别，发票代码" + vo.getVinvoicecode() + "，发票号码" + vo.getVinvoiceno() + "，图片原名"
				+ vo.getOldfilename() + "图片名称" + vo.getImgname() + "，主键" + vo.getPk_image_ocrlibrary() + message;
		loggerError(log, moduleName, msg, t);
	}

	public static void dealLog(OcrImageLibraryVO vo, Logger log, String moduleName, String message) {
		String msg = moduleName + "识别，发票代码" + vo.getVinvoicecode() + "，发票号码" + vo.getVinvoiceno() + "，图片原名"
				+ vo.getOldfilename() + "图片名称" + vo.getImgname() + "，主键" + vo.getPk_image_ocrlibrary() + message;
		loggerInfo(log, moduleName, msg);
	}

	public static void loggerInfo(Logger log, String moduleName, String message) {
		log.info("Thread: " + Thread.currentThread().getName() + "[" + moduleName + "]" + message);
	}

	public static void loggerError(Logger log, String moduleName, String message, Throwable t) {
		log.error("Thread: " + Thread.currentThread().getName() + "[" + moduleName + "]" + message, t);
	}

}

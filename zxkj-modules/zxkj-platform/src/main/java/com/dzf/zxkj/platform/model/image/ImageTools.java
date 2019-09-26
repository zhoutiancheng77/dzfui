package com.dzf.zxkj.platform.model.image;

import net.coobird.thumbnailator.Thumbnails;

import java.io.File;
import java.io.IOException;

/**
 * 图片处理工具类
 */
public class ImageTools {

	/**
	 * 指定大小进行缩放
	 * 若图片横比200小，高比300小，不变
	 * 若图片横比200小，高比300大，高缩小到300，图片比例不变 
	 * 若图片横比200大，高比300小，横缩小到200，图片比例不变 
     * 若图片横比200大，高比300大，图片按比例缩小，横为200或高为300 
	 * @param width
	 * @param height
	 * @param srcName
	 * @param dstName
	 * @throws IOException 
	 */
	public static void compImgBySize(int width, 
			int height,
			String srcName,
			String dstName) throws IOException {
		
		Thumbnails.of(srcName)
			.size(width, height)
			.toFile(dstName);
	}
	
	public static void compImgBySize(int width, 
			int height,
			File srcFile,
			File dstFile) throws IOException{
		
		Thumbnails.of(srcFile)
			.size(width, height)
			.toFile(dstFile);
	}
	
	/**
	 * 按照比例进行缩放
	 * @param width
	 * @param height
	 * @param srcName
	 * @param dstName
	 * @throws IOException
	 */
	public static void compImgByScale(float d,
			String srcName,
			String dstName) throws IOException{
		Thumbnails.of(srcName)
			.scale(d)
			.toFile(dstName);
	}
	
	public static void compImgByScale(float d,
			File srcFile,
			File dstFile) throws IOException{
		Thumbnails.of(srcFile)
			.scale(d)
			.toFile(dstFile);
	}
	
	/**
	 * 转化图像格式（按大小）
	 * @param width
	 * @param height
	 * @param srcName
	 * @param dstName
	 * @param imgType
	 * @throws IOException
	 */
	public static void convertImgFormatBySize(int width, 
			int height,
			String srcName,
			String dstName,
			String imgType) throws IOException{
		
		Thumbnails.of(srcName)
			.size(width, height)
			.outputFormat(imgType)//图像格式
			.toFile(dstName);
	}
	
	public static void convertImgFormatBySize(int width, 
			int height,
			File srcFile,
			File dstFile,
			String imgType) throws IOException{
		Thumbnails.of(srcFile)
			.size(width, height)
			.outputFormat(imgType)//图像格式
			.toFile(dstFile);
	}
	
	/**
	 * 转化图片格式（按比例）
	 * @param d
	 * @param srcName
	 * @param dstName
	 * @param imgType
	 * @throws IOException
	 */
	public static void convertImgFormatByScale(float d,
			String srcName,
			String dstName,
			String imgType) throws IOException{
		Thumbnails.of(srcName)
			.scale(d)
			.outputFormat(imgType)//图像格式
			.toFile(dstName);
	}
	
	public static void convertImgFormatByScale(float d,
			File srcFile,
			File dstFile,
			String imgType) throws IOException{
		Thumbnails.of(srcFile)
			.scale(d)
			.outputFormat(imgType)//图像格式
			.toFile(dstFile);
	}
}

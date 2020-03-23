package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.platform.model.image.ImageTools;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ImageCopyUtil {
	private static final int BUFFER_SIZE = 16 * 1024;
	private static final String CONVERTIMAGETYPE = "jpg";
	public static void compressCoye(File src, File dst) {
		FileOutputStream out = null;
		try {
			int outputWidth = 1024;
			int outputHeight = 768;
			Image img = ImageIO.read(src);
			// 判断图片格式是否正确
			if (img.getWidth(null) == -1) {
//				System.out.println(" can't read,retry!" + "<BR>");
//				return "no";
			} else {
				int newWidth;
				int newHeight;
				// 为等比缩放计算输出的图片宽度及高度
				double rate1 = ((double) img.getWidth(null)) / (double) outputWidth + 0.1;
				double rate2 = ((double) img.getHeight(null)) / (double) outputHeight + 0.1;
				// 根据缩放比率大的进行缩放控制
				double rate = rate1 > rate2 ? rate1 : rate2;
				newWidth = (int) (((double) img.getWidth(null)) / rate);
				newHeight = (int) (((double) img.getHeight(null)) / rate);
				BufferedImage tag = new BufferedImage((int) newWidth, (int) newHeight, BufferedImage.TYPE_INT_RGB);
				/*
				 * Image.SCALE_SMOOTH 的缩略算法 生成缩略图片的平滑度的 优先级比速度高 生成的图片质量比较好 但速度慢
				 */
				tag.getGraphics().drawImage(img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);
				out = new FileOutputStream(dst);
				// JPEGImageEncoder可适用于其他图片类型的转换
				JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(out);
				encoder.encode(tag);
				out.close();
			}
		} catch (IOException ex) {
		}finally {
			if(out != null){
				try {
					out.close();
				} catch (Exception e) {
					//e.printStackTrace();
				}
			}
		}
	}
	
	public static void copy(MultipartFile src, File dst) {
        InputStream in = null;
        OutputStream out = null;
        InputStream is = null;
        FileOutputStream os = null;
        try {
        	is = src.getInputStream();
            in = new BufferedInputStream(is, BUFFER_SIZE);
            os = new FileOutputStream(dst);
            out = new BufferedOutputStream(os,
                    BUFFER_SIZE);
            byte[] buffer = new byte[BUFFER_SIZE];
            int len = 0;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            out.flush();
        } catch (Exception e) {
            //e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    //e.printStackTrace();
                }
            }
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                	
                }
            }
            
            if (null != os) {
                try {
                    os.close();
                } catch (IOException e) {
                	
                }
            }
        }
    }
	public static void copy(InputStream is, File dst) {
		InputStream in = null;
		OutputStream out = null;
		FileOutputStream os = null;
		try {
			in = new BufferedInputStream(is, BUFFER_SIZE);
			os = new FileOutputStream(dst);
			out = new BufferedOutputStream(os,
					BUFFER_SIZE);
			byte[] buffer = new byte[BUFFER_SIZE];
			int len = 0;
			while ((len = in.read(buffer)) > 0) {
				out.write(buffer, 0, len);
			}
			out.flush();
		} catch (Exception e) {
			//e.printStackTrace();
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}

			if (null != is) {
				try {
					is.close();
				} catch (IOException e) {
					//e.printStackTrace();
				}
			}
			if (null != out) {
				try {
					out.close();
				} catch (IOException e) {

				}
			}

			if (null != os) {
				try {
					os.close();
				} catch (IOException e) {

				}
			}
		}
	}
	public static void transPdfToJpg(MultipartFile src,
									 File dir,
									 String pdfName,
									 File imgDst){
		
		try {
			File pdfDst = new File(dir, pdfName);
//			File imgDst = new File(dir, imgName);
			
			copy(src, pdfDst);
			
			loadPdfDoc(pdfDst, imgDst);
		} catch (Exception e) {
			log.error("错误",e);
			throw new BusinessException("转化pdf失败");
		}
	}
	
	public static void transPdfToJpg(MultipartFile src,
			File pdfDst,
			File jpgDst){
		try {
			copy(src, pdfDst);
			loadPdfDoc(pdfDst, jpgDst);
		} catch (Exception e) {
			log.error("错误",e);
			throw new BusinessException("重传:转化pdf失败");
		}
	}
	public static void transPdfToJpg(InputStream is,
									 File pdfDst,
									 File jpgDst){
		try {
			copy(is, pdfDst);
			loadPdfDoc(pdfDst, jpgDst);
		} catch (Exception e) {
			log.error("错误",e);
			throw new BusinessException("重传:转化pdf失败");
		}
	}
	private static void loadPdfDoc(File pdfDst, 
			File imgDst) throws Exception{
		PDDocument document =  PDDocument.load(pdfDst, (String)null);
		 int size = document.getNumberOfPages();
	     List<BufferedImage> piclist = new ArrayList();
	     BufferedImage image = null;
	     for(int i = 0; i < size; i++){
	    	 image = new PDFRenderer(document).renderImageWithDPI(i,100, ImageType.RGB);
	    	 piclist.add(image);
	     }
	     
	     document.close();
	     
	     yPic(piclist, imgDst);
	        
	}
	
	/** 
     * 将宽度相同的图片，竖向追加在一起 ##注意：宽度必须相同 
     *  
     * @param piclist 
     *            文件流数组 
     * @param imgDst 
     *            输出文件
     */  
     private static void yPic(List<BufferedImage> piclist, File imgDst) {// 纵向处理图片  
        if (piclist == null || piclist.size() <= 0) {  
        	throw new BusinessException("图片数组为空!");
        }
        
        ByteArrayOutputStream out = null;
        FileOutputStream output = null;
        try {  
            int height = 0, // 总高度  
            width = 0, // 总宽度  
            _height = 0, // 临时的高度 , 或保存偏移高度  
            __height = 0, // 临时的高度，主要保存每个高度  
            picNum = piclist.size();// 图片的数量  
            File fileImg = null; // 保存读取出的图片  
            int[] heightArray = new int[picNum]; // 保存每个文件的高度  
            BufferedImage buffer = null; // 保存图片流  
            List<int[]> imgRGB = new ArrayList<int[]>(); // 保存所有的图片的RGB  
            int[] _imgRGB; // 保存一张图片中的RGB数据  
            for (int i = 0; i < picNum; i++) {  
                buffer = piclist.get(i);  
                heightArray[i] = _height = buffer.getHeight();// 图片高度  
                if (i == 0) {  
                    width = buffer.getWidth();// 图片宽度  
                }  
                height += _height; // 获取总高度  
                _imgRGB = new int[width * _height];// 从图片中读取RGB  
                _imgRGB = buffer  
                        .getRGB(0, 0, width, _height, _imgRGB, 0, width);  
                imgRGB.add(_imgRGB);  
            }  
            _height = 0; // 设置偏移高度为0  
            // 生成新图片  
            BufferedImage imageResult = new BufferedImage(width, height,  
                    BufferedImage.TYPE_INT_BGR);  
            for (int i = 0; i < picNum; i++) {  
                __height = heightArray[i];  
                if (i != 0)  
                    _height += __height; // 计算偏移高度  
                imageResult.setRGB(0, _height, width, __height, imgRGB.get(i),  
                        0, width); // 写入流中  
            }  
            out = new ByteArrayOutputStream();  
            ImageIO.write(imageResult, "jpg", out);// 写图片  
            byte[] b = out.toByteArray();  
            output = new FileOutputStream(imgDst);  
            output.write(b);  
            out.close();  
            output.close();  
        } catch (Exception e) {  
           log.error("错误",e);
           throw new BusinessException("PDF转化图片失败!");
        }  finally {
			if(out != null){
				try {
					out.close();
				} catch (Exception e2) {
				}
			}
			if(output != null){
				try {
					output.close();
				} catch (Exception e2) {
				}
			}
		}
    }  

	public static void storageImgFile(File srcFile, 
			String nameSuffixTemp, 
			File dir,
			String nameSuffix,
			String imgFileNm,
			String simgFileNm,
			String mimgFileNm,
			boolean isComBySale){
		try{
			File destSmallFile = new File(dir, simgFileNm);
			
			if(isComBySale){
				File destMiddlFile = new File(dir, mimgFileNm);
				ImageTools.convertImgFormatByScale(0.25f, srcFile, destMiddlFile, CONVERTIMAGETYPE);
			}
			
			if (".bmp".equalsIgnoreCase(nameSuffixTemp) || ".png".equalsIgnoreCase(nameSuffixTemp)) {
				ImageTools.convertImgFormatBySize(260, 170, srcFile, destSmallFile, CONVERTIMAGETYPE);
			}else{
				ImageTools.compImgBySize(260, 170, srcFile, destSmallFile);
			}
			
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw new BusinessException("保存图片失败");
		}
	}
}

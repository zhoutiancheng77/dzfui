package com.dzf.zxkj.platform.services.image.impl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.platform.model.image.DecodeOption;
import com.sun.imageio.plugins.jpeg.JPEGImageWriter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Externalizable;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Iterator;

public class ClientFileSerializable implements Externalizable {

	private Image file;



	public Image getFile() {
		return file;
	}

	public void setFile(Image file) {
		this.file = file;
	}



	private void writeString(ObjectOutput out, String str) throws DZFWarpException {
		try{
			if (str != null) {
				byte[] bytes = str.getBytes("UTF-8");
				out.writeByte(bytes.length);
				out.write(bytes);

			} else {
				out.writeByte(-1);
			}
		}catch(Exception e){
			throw new WiseRunException(e);
		}
	}

	public void readExternal(ObjectInput arg0) throws DZFWarpException {
		try {
		ImageObject ioj=	ImageObject.decodeStream(arg0, new DecodeOption(), "jpg");
		file=ioj.getImage();
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		
//		String fileName = arg0.readUTF();// input.readUTF();
//		int fileLength = (int) arg0.readLong(); // number of total bytes
//		BufferedOutputStream output = null;
//		try {
//			file = new File("C:\\Users\\temp" + File.separator + fileName);
//
//			byte[] content = new byte[20480];
//			int offset = 0;
//			int numReadBytes = 0;
//			while (offset < fileLength
//					&& (numReadBytes = arg0.read(content)) > 0) {
//				output.write(content, 0, numReadBytes);
//
//				offset += numReadBytes;
//			}
//		} finally {
//			if (output != null)
//				output.close();
//		}
	}
	public BufferedImage getBufferedImage(){
		if(file == null) return null;
		return (BufferedImage) file;
	}
	public void writeExternal(ObjectOutput arg0) throws DZFWarpException {
		// DataOutputStream dout = new DataOutputStream(arg0);
		// now we start to send the file meta info.
	//	arg0.writeUTF(file.getName());
		ImageOutputStream imageStream = null;
		try{
			Iterator<ImageWriter> iter = ImageIO.getImageWritersByMIMEType("image/jpeg");
			ImageWriter writer = iter.next();
			imageStream = ImageIO.createImageOutputStream(arg0);
			writer.setOutput(imageStream);
			
			if(writer instanceof JPEGImageWriter){
				ImageWriteParam writeParam = writer.getDefaultWriteParam();
				// 设置压缩模式
				writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
				// 将压缩质量设置为 0 和 1 之间的某个值
				writeParam.setCompressionQuality(1.0f);
				writer.write(null, new IIOImage(getBufferedImage(), null, null), writeParam);
			} else {

				writer.write(null, new IIOImage(getBufferedImage(), null, null), null);
			}
			writer.dispose();
			imageStream.flush();
		}catch(Exception e){
			throw new WiseRunException(e);
		}finally{
			try{
				if(imageStream != null){
					imageStream.close();
				}
			}catch(Exception e){
				throw new WiseRunException(e);
			}
		}

//		
//		arg0.writeLong(file.);
//		arg0.flush();
//		// end comment
//		// FileDataPackage pData = new FileDataPackage();
//		DataInputStream is = null;
//		try {
//			is = new DataInputStream(new FileInputStream(file));
//			byte[] bytes = new byte[20480];
//
//			// Read in the bytes
//			int offset = 0;
//			int numRead = 0;
//			int fsize = (int) file.length();
//			while (offset < fsize
//					&& (numRead = is.read(bytes, 0, bytes.length)) >= 0) {
//				arg0.write(bytes, 0, numRead);
//
//				arg0.flush();
//				offset += numRead;
//
//			}
//		} finally {
//			if (is != null)
//				is.close();
//		}

	}

}

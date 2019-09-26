package com.dzf.zxkj.platform.model.image;

import com.dzf.zxkj.common.lang.DZFDate;

import java.io.*;
import java.text.SimpleDateFormat;

public class ServerFileSerializable extends FileSerializable {

	private File file;

	private String pk_corp;
	private static String imageBasePath = "";
	static {
//		imageBasePath = RuntimeEnv.getInstance().getNCHome().trim().replace('\\', '/');
		imageBasePath = "";
        if (!imageBasePath.endsWith("/"))
        	imageBasePath += "/";
        imageBasePath += "ImageUpload/";
        new File(imageBasePath).mkdir();
    }		
	public ServerFileSerializable() {
	}
	public ServerFileSerializable(String pk_corp) {
		this.pk_corp = pk_corp;
	}
	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}



	public void readExternal(ObjectInput arg0) throws IOException,
			ClassNotFoundException {
		DZFDate date = new DZFDate();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	
		
		String fileName = arg0.readUTF();// input.readUTF();
		
		int fileLength = (int) arg0.readLong(); // number of total bytes
		pk_corp=arg0.readUTF();
		BufferedOutputStream output = null;
		try {
			file=new File(imageBasePath +pk_corp + "/" + sdf.format(date.toDate()) + "/"+fileName);
			if(file.getParentFile().exists()==false)
				file.getParentFile().mkdirs();
			if(file.exists()==false){
				
				file.createNewFile();
			}
			output = new BufferedOutputStream(new FileOutputStream(file));
			//System.out.println("Received File Name = " + fileName);
			//System.out.println("Received File size = " + fileLength / 1024
			//		+ "KB");

			byte[] content = new byte[20480];
			int offset = 0;
			int numReadBytes = 0;
			while (offset < fileLength
					&& (numReadBytes = arg0.read(content)) > 0) {
				output.write(content, 0, numReadBytes);

				offset += numReadBytes;
			}
		}catch (Exception e) {
			e.printStackTrace();
		} 
		finally {
			if (output != null)
				output.close();
		}

	}

	public void writeExternal(ObjectOutput arg0) throws IOException {
		// DataOutputStream dout = new DataOutputStream(arg0);
		// now we start to send the file meta info.
		arg0.writeUTF(file.getName());
		arg0.writeLong(file.length());
		arg0.writeUTF(pk_corp);
		arg0.flush();
		// end comment
		// FileDataPackage pData = new FileDataPackage();
		DataInputStream is = null;
		try {
			is = new DataInputStream(new FileInputStream(file));
			byte[] bytes = new byte[20480];

			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			int fsize = (int) file.length();
			while (offset < fsize
					&& (numRead = is.read(bytes, 0, bytes.length)) >= 0) {
				arg0.write(bytes, 0, numRead);

				arg0.flush();
				offset += numRead;

			}
		} finally {
			if (is != null)
				is.close();
			if(arg0!=null){
				arg0.close();
			}
		}

	}

}

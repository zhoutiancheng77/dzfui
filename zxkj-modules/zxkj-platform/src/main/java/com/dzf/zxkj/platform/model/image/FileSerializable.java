package com.dzf.zxkj.platform.model.image;

import java.io.*;

public class FileSerializable implements Externalizable {

	public FileSerializable() {
	}
	private File file;

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		
	}

}

package com.dzf.zxkj.platform.model.image;

import java.util.Comparator;

public class ImageLibrarySort implements Comparator<ImageLibraryVO> {

	@Override
	public int compare(ImageLibraryVO arg0, ImageLibraryVO arg1) {
		int i = arg0.getImgname().compareTo(arg1.getImgname());
		return i;
	}

}

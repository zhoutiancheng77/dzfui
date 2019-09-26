package com.dzf.zxkj.platform.model.image;

import java.util.Comparator;

public class ImageGroupSort implements Comparator<ImageGroupVO> {

	@Override
	public int compare(ImageGroupVO o1, ImageGroupVO o2) {
		int i = o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
		return i;
	}

}

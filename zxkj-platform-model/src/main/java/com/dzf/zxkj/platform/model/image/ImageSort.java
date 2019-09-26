package com.dzf.zxkj.platform.model.image;

import java.util.Comparator;
import java.util.zip.ZipEntry;

public class ImageSort implements Comparator<ZipEntry> {
	public int compare(ZipEntry v1, ZipEntry v2) {
		String name1 = v1.getName();
		String name2 = v2.getName();
		if(name1 != null && name2 != null){
			return name1.compareTo(name2);
		}else{
			return 0;
		}
//		if(v1.getPhotots() != null && v2.getPhotots() != null){
//			int i=v1.getPhotots().compareTo(v2.getPhotots());
//			if(0==i){
//				i=
//			}
			
//			if (v1.getPhotots().before(v2.getPhotots())) {
//				return 0;
//			} else if(v1.getPhotots().equals(v2.getPhotots())){
//				if(photoname1>photoname2){
//					
//				}else{
//					
//				}
//			} else {
//				return 1;
//			}
//		}else{
//			return 1;
//		}
	}
}
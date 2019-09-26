package com.dzf.zxkj.platform.util;

import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;

import java.util.Comparator;

/**
 * 这个是最老的识图、切图做出来的东西 ，已经没有什么用了。
 * @author zpm
 *
 */
public class TZPZHVOSort implements Comparator<TzpzHVO> {
	public int compare(TzpzHVO v1, TzpzHVO v2) {
		String photoname1 = v1.getPhototname();
		String photoname2 = v2.getPhototname();
		String pzh1 = v1.getPzh();
		String pzh2 = v2.getPzh();
		if(photoname1 != null && photoname2 != null){
			return photoname1.compareTo(photoname2);
		}else{
			return pzh1.compareTo(pzh2);
		}
	}
}
package com.dzf.zxkj.app.utils;

import java.util.ArrayList;
import com.dzf.zxkj.app.model.image.ImageObject;
import com.dzf.zxkj.base.exception.DZFWarpException;

public class ImageCompositeProcessor implements ImageProcessor {
	private ArrayList<ImageProcessor> processors = new ArrayList<ImageProcessor>();
	
	public void add(ImageProcessor processor){
		processors.add(processor);
	}
	
	public void remove(ImageProcessor processor){
		if(processors.contains(processor))
			processors.remove(processor);
	}
	
	public void clear(){
		processors.clear();
	}
	
	public void ProcessImage(ImageObject image) throws DZFWarpException {
		for(ImageProcessor processor: processors){
			processor.ProcessImage(image);
		}
	}

}

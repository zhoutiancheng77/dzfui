package com.dzf.zxkj.platform.model.zncs;

import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrImageGroupVO;
import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;

public class ImageInfoVO {

	
	private OcrImageLibraryVO ocrImageLbVO;
	private OcrImageGroupVO ocrImageGVO;
	private ImageGroupVO imageGVO;
	private ImageLibraryVO imageLbVO;
	
	
	public OcrImageLibraryVO getOcrImageLbVO() {
		return ocrImageLbVO;
	}
	public void setOcrImageLbVO(OcrImageLibraryVO ocrImageLbVO) {
		this.ocrImageLbVO = ocrImageLbVO;
	}
	public OcrImageGroupVO getOcrImageGVO() {
		return ocrImageGVO;
	}
	public void setOcrImageGVO(OcrImageGroupVO ocrImageGVO) {
		this.ocrImageGVO = ocrImageGVO;
	}
	public ImageGroupVO getImageGVO() {
		return imageGVO;
	}
	public void setImageGVO(ImageGroupVO imageGVO) {
		this.imageGVO = imageGVO;
	}
	public ImageLibraryVO getImageLbVO() {
		return imageLbVO;
	}
	public void setImageLbVO(ImageLibraryVO imageLbVO) {
		this.imageLbVO = imageLbVO;
	}
	
	
}

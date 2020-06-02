package com.dzf.zxkj.app.service.app.act;

import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

public interface IImageService {
//	public ResponseBaseBeanVO deleteImage(List<ImageBeanVO> imageBeanLs) throws DZFWarpException;

	public void downLoadByget(ImageBeanVO imageBean, HttpServletResponse httpServletResponse) throws DZFWarpException;

//	public void downLoadByFastFile(ImageBeanVO imageBean, HttpServletResponse httpServletResponse) throws DZFWarpException;
//
//	public ResponseBaseBeanVO getRephotoMark(ImageBeanVO imageBean) throws DZFWarpException;
//
//	public ResponseBaseBeanVO getRephotoMsg(ImageBeanVO imageBean) throws DZFWarpException;

}

package com.dzf.zxkj.app.service.photo;

import com.dzf.zxkj.app.model.image.ImageUploadRecordVO;
import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ImageReqVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.model.sys.OcrInvoiceVOForApp;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.platform.model.image.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Map;

/**
 * 同时存在两个地方，为了出盘不报错(4个，4)
 *
 */
public interface IImageProviderPhoto {


	public ImageQueryBean[] saveUploadImages(UserBeanVO uBean, MultipartFile file, String filenames, InputStream file_in) throws DZFWarpException;


	//图片重传
	public int saveReuploadImage(UserBeanVO uBean, ImageBeanVO[] imgbeanvos, MultipartFile file, InputStream file_in) throws DZFWarpException;

	public ImageUploadRecordVO[] queryImages(ImageBeanVO beanvo)throws DZFWarpException;

	/**
	 * 获取用户上传记录
	 */
	public ImageUploadRecordVO[] queryUploadRecord(ImageReqVO uBean)throws DZFWarpException;
	/**
	 * 根据文件名下载文件
	 * @param filePath
	 * @param out
	 * @param paramBeans
	 * @return
	 * @
	 */
	public void downloadImage(String filePath, OutputStream out, ImageParamBeanVO[] paramBeans) throws DZFWarpException;//void

	/**
	 * 查询识别历史
	 * @param pk_group
	 * @return
	 * @throws DZFWarpException
	 */
	public OcrInvoiceVOForApp[] querySbVos(String pk_group) throws DZFWarpException;
	/**
	 * 查询识别详情
	 * @param sbid
	 * @return
	 * @throws DZFWarpException
	 */
	public OcrInvoiceVOForApp querySbDetail(String sbid) throws DZFWarpException;




//	/**
//	 * 上传图片
//	 * @param uBean 手机端封装的bean
//	 * @param fileItem
//	 * @return
//	 * @
//	 */
////	public ImageQueryBean[] uploadImages(UserBean uBean,FileItem fileItem) throws DZFWarpException;//ImageQueryBean[]
//	/**
//	 * 上传图片
//	 * @param pk_corp 公司PK值
//	 * @param uploadlot 上传批次，必须全局唯一
//	 * @param uploader 上传用户PK值
//	 * @param zipfile 上传的压缩包
//	 * @param isCompress 是否需要压缩
//	 * @
//	 */
//	public ImageQueryBean[] uploadImagesFile(Integer sel, String pk_corp, String uploadlot, String uploader, ServerFileSerializable fs, String qj, boolean isCompress) throws DZFWarpException;//ImageQueryBean[]
//
//	/**
//	 * 查询图片
//	 * @param strWhere 查询条件
//	 * @return 查询出的图片VO
//	 * @
//	 */
//	//public ImageQueryBean[] queryImages(String strWhere) throws DZFWarpException;//ImageQueryBean[]
//
//	/**
//	 * PC端调用下载功能
//	 * @param filePath
//	 * @param out
//	 * @param paramBeans
//	 * @return
//	 * @
//	 */
//	public ClientFileSerializable downloadImageByPc(String filePath, ImageParamBeanVO[] paramBeans) throws DZFWarpException;//ClientFileSerializable
//
//	/**
//	 * 删除图片
//	 * @param imageKeys
//	 * @
//	 */
//	//public void deleteImage(String[] imageKeys) throws DZFWarpException;//void
//	/**
//	 * 旋转图片
//	 * @param filePath
//	 * @param degree
//	 * @
//	 */
//	public void rotateImage(String filePath, int degree) throws DZFWarpException;//void
//	/**
//	 * 保存切图数据
//	 * @return
//	 * @
//	 */
//	public void saveClipedImage(String pk_image_group, DZFDateTime ts, ImageMetaVO[] metaVOs) throws DZFWarpException;//void
//	/**
//	 * 退回重拍
//	 * @param pk_image_group
//	 * @param ts
//	 * @param skipedBy
//	 * @param skipedOn
//	 * @
//	 */
//	public void skipClipedImage(String pk_image_group, DZFDateTime ts, String skipedBy, DZFDateTime skipedOn) throws DZFWarpException;//void
//
//	/**
//	 * 查询需要切图的图片组VO
//	 * @param count
//	 * @param excludeImageGroupKeys
//	 * @return
//	 * @
//	 */
//	public ClipingImageBean queryNewClipingImages(String pk_corp, int count, ArrayList<String> excludeImageGroupKeys) throws DZFWarpException;//ClipingImageBean
//	/**
//	 * 释放未完成切图的图片组VO
//	 * @param imageGroupVOs
//	 * @
//	 */
//	public void releaseNewClipingImages(ImageGroupVO[] imageGroupVOs) throws DZFWarpException;//void
//	/**
//	 * 查询需要识别的图元信息VO
//	 * @param user
//	 * @param count
//	 * @param excludeMetaKeys
//	 * @return
//	 * @
//	 */
//	public IndentImageBean queryNewIndentImages(String pk_corp, String user, int count, ArrayList<String> excludeMetaKeys) throws DZFWarpException;//IndentImageBean
//	/**
//	 * 释放未完成识图的图元信息VO
//	 * @param metaVOs
//	 * @
//	 */
//	public void releaseNewIndentImages(ImageMetaVO[] metaVOs) throws DZFWarpException;//void
//	/**
//	 * 保存识图数据
//	 * @param pk_image_meta
//	 * @param indentValue
//	 * @param indentBy
//	 * @param indentOn
//	 * @
//	 */
//	public void SaveIndentInfo(String pk_image_meta, DZFDateTime ts, String indentValue, String indentBy, DZFDateTime indentOn)  throws DZFWarpException;//void
//	/**
//	 * 批量生成预凭证
//	 * @param imageVOs
//	 * @param user
//	 * @param date
//	 * @return
//	 * @
//	 */
//	public IMageVoucherVO[]  batchCreatePreVoucher(ImageLibraryVO[] imageVOs, String user, DZFDate date) throws DZFWarpException;//IMageVoucherVO[]
//	/**
//	 * 预凭证转总账
//	 * @param voucherVOs
//	 * @param user
//	 * @param date
//	 * @
//	 */
//	public void PreVoucherToGL(IMageVoucherVO[] voucherVOs, String user, DZFDate date) throws DZFWarpException;//void
//	/**
//	 * 更新预凭证转总账状态
//	 * @param pk_image_voucher
//	 * @param istogl
//	 * @param voucherno
//	 * @
//	 */
//	public void updatePreVoucherGLState(String pk_image_voucher, boolean istogl, String voucherno) throws DZFWarpException;//void
//	/**
//	 * 切图/预凭证直接转会计
//	 * @param pk_image_voucher
//	 * @param pk_image_group
//	 * @param user
//	 * @param date
//	 * @param sourcebilltype
//	 */
//	public void ClipDirectToAccount(String pk_image_voucher, String pk_image_group, String user, DZFDate date, String sourcebilltype) throws DZFWarpException;//void
//	/**
//	 * 保存退回重拍消息
//	 * @param vo
//	 * @
//	 */
//	public ImageTurnMsgVO saveReturnMsg(ImageTurnMsgVO vo) throws DZFWarpException;//ImageTurnMsgVO
//	/**
//	 * 退回重拍消息设置
//	 */
//	public void readReturnMsg(String pk_corp, ImageBeanVO imageBean) throws DZFWarpException;//void
//
//	/**
//	 * 查询需要编辑处理的预凭证
//	 * @param user
//	 * @param count
//	 * @return
//	 * @
//	 */
//	public IMageVoucherVO[] queryHandlingVouchers(String pk_datacorp, int count) throws DZFWarpException;//IMageVoucherVO[]
//	/**
//	 * 释放未完成编辑处理的预凭证VO
//	 * @param metaVOs
//	 * @
//	 */
//	public void releaseHandlingVouchers(IMageVoucherVO[] voucherVOs) throws DZFWarpException;//void
//
//	//public void readReturnMsg(String pk_corp,String[] msgKeys, String user, DZFDateTime readTime) throws DZFWarpException ;
//	public ImageQueryBean[] queryImages(String pk_corp, String strWhere, SQLParameter sp) throws DZFWarpException;
//	public void deleteImage(String pk_corp, String[] imageKeys) throws DZFWarpException;
////	public ImageQueryBean[] uploadImages(UserBeanVO uBean,FileItem fileItem) throws DZFWarpException;
	  



//
//	/**
//	 * 根据条件查询图片组信息
//	 * @param uBean
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public List<ImageGroupVO> queryImgGroupvo(ReportBeanVO uBean)throws DZFWarpException;
//
//	/**
//	 * 删除上传记录
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public Integer deleteRecord(String sessionflag, String pk_corp) throws DZFWarpException;
//
//	/**
//	 * 华道生成预凭证
//	 * @param imageVOs
//	 * @param user
//	 * @param date
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public IMageVoucherVO[]  batchCreateHDVoucher(ImageHuadaoHVO[] imageVOs, String user, DZFDate date) throws DZFWarpException;//IMageVoucherVO[]
//
//	/**
//	 * 直接生单
//	 * @param user
//	 * @param date
//	 * @param map
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public IMageVoucherVO[] batchCreateDirVoucher(String user, String pk_datacorp, DZFDate date, Map<String, String> map)throws DZFWarpException;
//
//	/**
//	 * 图片上传
//	 */
//	public ImageLibraryVO uploadSingFile(UserBeanVO uBean, CorpVO corpvo, String account_id, File[] infiles, String[] filenames, String g_id, String kprq, String memo, String memo1, String payment, DZFDouble mny) throws DZFWarpException;
//
//
//	//zpm zpm
//
//	public ImageQueryBean[] queryImageWithNonDr(String pk_image_group) throws Exception;
//
//	public ImageTurnMsgVO[] queryReturnmsgHeadVOs(String pk_corp, String strWhere) throws Exception;
//


}

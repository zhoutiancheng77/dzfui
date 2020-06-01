package com.dzf.zxkj.app.service.app.act.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.service.app.act.IImageService;
import com.dzf.zxkj.app.service.photo.IImageProviderPhoto;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageParamBeanVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSONArray;


/**
 * 
 * @author liangjy
 * 
 */
@Slf4j
@Service("imservice")
public class ImageServiceImpl implements IImageService {


	/**
	 *
	 * @param request
	 * @param response
	 * @param m_provider
	 * @throws ServletException
	 * @throws IOException
	 */
	public void downLoadByget(ImageBeanVO imageBean,
							  HttpServletResponse response) throws DZFWarpException {

		String filePath = imageBean.getFilepath();
		OutputStream output = null;

		if (StringUtil.isEmptyWithTrim(filePath)) {
			throw new BusinessException("下载文件传入参数不能为空！");
		}
		try {
			ImageParamBeanVO[] paramBeans = null;
			IImageProviderPhoto provide = (IImageProviderPhoto) SpringUtils
					.getBean("poimp_imagepro");

			if (imageBean.getImageparams() != null) {
				JSONArray jsonArray = JSONArray.parseArray(imageBean
						.getImageparams());
				paramBeans = (ImageParamBeanVO[]) ((List) jsonArray)
						.toArray(new ImageParamBeanVO[0]);
			}

			output = response.getOutputStream();
			provide.downloadImage(filePath, response.getOutputStream(),
					paramBeans);
			output.flush();

		} catch (Exception e) {
			log.error(e.getMessage(),e);
		} finally {
			try {
				if (output != null) {
					output.close();
				}
			} catch (IOException e) {
				log.error(e.getMessage(),e);
			}
		}
	}

//
//	public ResponseBaseBeanVO deleteImage(List<ImageBeanVO> imageBeanLs)
//			throws DZFWarpException {
//		ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
//		try {
//			String[] imageKeys = new String[imageBeanLs.size()];
//			for (int i = 0; i < imageBeanLs.size(); i++) {
//				imageKeys[i] = imageBeanLs.get(i).getImagekey();
//			}
//			IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils
//					.getBean("poimp_imagepro");
//			ip.deleteImage(null, imageKeys);
//
//			respBean.setRescode(IConstant.DEFAULT);
//			respBean.setResmsg("删除文件成功");
//		} catch (Exception e) {
//			log.error(e,e);
//			respBean.setRescode(IConstant.FIRDES);
//			if(e instanceof BusinessException){
//				respBean.setResmsg(e.getMessage());
//			}else{
//				respBean.setResmsg("删除文件失败");
//			}
//		}
//		return respBean;
//	}
//
//
//
//	/**
//	 * post��������
//	 *
//	 * @param request
//	 * @param response
//	 * @param m_provider
//	 * @throws ServletException
//	 * @throws IOException
//	 */
//	protected void downLoadByPost(HttpServletRequest request,
//			HttpServletResponse response, ImageBeanVO imageBean)
//			throws ServletException, IOException {
//
//	}
//
//	/**
//	 * ��ѯͼƬ
//	 *
//	 * @param request
//	 * @param response
//	 * @param imageBean
//	 * @param m_provider
//	 * @throws ServletException
//	 * @throws IOException
//	 */
//	protected void qryImages(HttpServletRequest request,
//			HttpServletResponse response, ImageBeanVO imageBean)
//			throws ServletException, IOException {
//
//		ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
//		try {
//
//			// 公司PK
//			if (imageBean.getPk_corp() == null) {
//				throw new BusinessException("没有包含pk_corp参数");
//			}
//			// 查询开始日期
//			if (imageBean.getBegindate() == null) {
//				throw new BusinessException("没有包含beginDate参数");
//			}
//			// 查询结束日期
//			if (imageBean.getEnddate() == null) {
//				throw new BusinessException("没有包含endDate参数");
//			}
//
//			String strWhere = "pk_corp=? and doperatedate>=? and doperatedate<=?";//
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(imageBean.getPk_corp());
//			sp.addParam(imageBean.getBegindate());
//			sp.addParam(imageBean.getEnddate());
//			IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils
//					.getBean("poimp_imagepro");
//			ip.queryImages(imageBean.getPk_corp(), strWhere, sp);
//			respBean.setRescode(IConstant.DEFAULT);
//		} catch (Exception e) {
//			log.error(e,e);
//			respBean.setRescode(IConstant.FIRDES);
//			if(e instanceof BusinessException){
//				respBean.setResmsg(e.getMessage());
//			}else{
//				respBean.setResmsg("失败");
//			}
//		}
//		CommonServ.writeJsonByFilter(request, response, respBean, null, null);
//	}
//
//	public ResponseBaseBeanVO getRephotoMark(ImageBeanVO imageBean)
//			throws DZFWarpException {
//		ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
//		try {
//			if (StringUtil.isEmptyWithTrim(imageBean.getImagemsgkeys())) {
//				throw new BusinessException("传入的重拍信息主键不能为空");
//			}
//			if (StringUtil.isEmptyWithTrim(imageBean.getAccount_id())) {
//				throw new BusinessException("传入的用户名不能为空");
//			}
//
//			IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");
//			ip.readReturnMsg(imageBean.getPk_corp(), imageBean);
//
//			respBean.setRescode(IConstant.DEFAULT);
//			respBean.setResmsg("标记重拍消息已读成功");
//		} catch (Exception e) {
//			log.error(e,e);
//			respBean.setRescode(IConstant.FIRDES);
//			if(e instanceof BusinessException){
//				respBean.setResmsg("标记重拍消息已读失败：" + e.getMessage());
//			}else{
//				respBean.setResmsg("标记重拍消息已读失败");
//			}
//		}
//
//		return respBean;
//	}
//
//	public ResponseBaseBeanVO getRephotoMsg(ImageBeanVO imageBean)
//			throws DZFWarpException {
//
//		ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
//		try {
//			if (StringUtil.isEmptyWithTrim(imageBean.getPk_corp())) {
//				throw new BusinessException("公司名称不能为空");
//			}
//			if (StringUtil.isEmptyWithTrim(imageBean.getAccount_id())) {
//				throw new BusinessException("用户名称不能为空");
//			}
//			SingleObjectBO sbo = new SingleObjectBO(
//					DataSourceFactory.getDataSource(null,
//							imageBean.getPk_corp()));
//			StringBuffer sf = new StringBuffer();
//			sf.append("select distinct a.*,g.settlemode settle,NVL(b.bread,'N') bread ,g.doperatedate as updatetime,g.memo as zy  from ynt_imgreturnmsg a ");
//			sf.append(" inner join ynt_image_group g on a.pk_image_group=g.pk_image_group ");
//			sf.append(" left join ynt_imgmsgreader b on a.pk_image_returnmsg=b.pk_image_returnmsg ");
//			sf.append(" where nvl(a.dr,0)=0 and a.pk_corp=? and nvl(b.dr,0)=0 and g.coperatorid=? order by a.ts desc,bread desc");
//
//
//			SQLParameter sp = new SQLParameter();
//			sp.addParam(imageBean.getPk_corp());
//			sp.addParam(imageBean.getAccount_id());
//			List<ImageTurnMsgVO> msgVOs = (List<ImageTurnMsgVO>) sbo
//					.executeQuery(sf.toString(), sp, new BeanListProcessor(ImageTurnMsgVO.class));
//			ArrayList<ImageRephotoMsgBean> msgBeans = new ArrayList<ImageRephotoMsgBean>();
//			if (msgVOs != null && msgVOs.size() > 0) {
//				ImageRephotoMsgBean msgBean = null;
//				String[] badImagePaths = null;
//				LinkedHashMap<String, ImageRephotoMsgBean> beanMap = new LinkedHashMap<String, ImageRephotoMsgBean>();
//				for (int i = 0; i < msgVOs.size(); i++) {
//					msgBean = new ImageRephotoMsgBean();
//					msgBean.setKey(msgVOs.get(i).getPrimaryKey());
//					msgBean.setMessage(msgVOs.get(i).getMessage());
//					msgBean.setZy(msgVOs.get(i).getZy());
//					msgBean.setGroupImagePaths(getGroupImagePaths(imageBean.getPk_corp(), msgVOs.get(i).getPk_image_group()));
//					badImagePaths = getBadImagePaths(imageBean.getPk_corp(), msgVOs.get(i).getPk_image_librarys());
//					msgBean.setBadImagePaths(badImagePaths);
//					msgBean.setSettle(msgVOs.get(i).getSettle() == null ? "" : msgVOs.get(i).getSettle());
//					msgBean.setBread(msgVOs.get(i).getBread());
//					msgBean.setDealtime(msgVOs.get(i).getDoperatedate().toString());// 图片退回时间
//					msgBean.setUpdatetime(msgVOs.get(i).getUpdatetime());// 图片上传时间
//					beanMap.put(msgVOs.get(i).getPrimaryKey(), msgBean);
//				}
//				Iterator iter = beanMap.entrySet().iterator();
//				Entry entry = null;
//				while (iter.hasNext()) {
//					entry = (Entry) iter.next();
//					msgBeans.add((ImageRephotoMsgBean) entry.getValue());
//				}
//			}
//			respBean.setRescode(IConstant.DEFAULT);
//			respBean.setResmsg(msgBeans.toArray(new ImageRephotoMsgBean[msgBeans.size()]));
//		} catch (Exception e) {
//			log.error(e,e);
//			respBean.setRescode(IConstant.FIRDES);
//			if(e instanceof BusinessException){
//				respBean.setResmsg("获取重拍消息失败：" + e.getMessage());
//			}else{
//				respBean.setResmsg("获取重拍消息失败");
//			}
//
//		}
//		return respBean;
//	}
//
//	private String[] getGroupImagePaths(String pk_corp, String pk_image_group)
//			throws DZFWarpException {
//		SingleObjectBO sbo = new SingleObjectBO(
//				DataSourceFactory.getDataSource(null, pk_corp));
//		String sql = "select * from ynt_image_library where pk_image_group=?";// ,
//																				// pk_image_group);
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_image_group);
//		List<ImageLibraryVO> imageVOs = (List<ImageLibraryVO>) sbo
//				.executeQuery(sql, sp, new BeanListProcessor(
//						ImageLibraryVO.class));
//		if (imageVOs == null || imageVOs.size() == 0)
//			return null;
//		String[] imagePaths = new String[imageVOs.size()];
//		for (int i = 0; i < imageVOs.size(); i++)
//			imagePaths[i] = imageVOs.get(i).getImgpath();
//		return imagePaths;
//	}
//
//	private String[] getBadImagePaths(String pk_corp, String pk_image_librarys)
//			throws DZFWarpException {
//
//		if (StringUtil.isEmptyWithTrim(pk_image_librarys))
//			return null;
//		String[] array = pk_image_librarys.split(",");
//		if (array == null || array.length == 0)
//			return null;
//		SingleObjectBO sbo = new SingleObjectBO(
//				DataSourceFactory.getDataSource(null, pk_corp));
//		String[] imagePaths = new String[array.length];
//		ImageLibraryVO imageVO = null;
//		for (int i = 0; i < array.length; i++) {
//			imageVO = (ImageLibraryVO) sbo.queryVOByID(array[i],
//					ImageLibraryVO.class);// .queryByPrimaryKey(ImageLibraryVO.class,
//											// array[i]);
//			if (imageVO == null)
//				throw new BusinessException("没有找到pk值为" + array[i]
//						+ "的图片记录，请确认！");
//			imagePaths[i] = imageVO.getImgpath();
//		}
//		return imagePaths;
//	}
//
//	@Override
//	public void downLoadByFastFile(ImageBeanVO imageBean,
//			HttpServletResponse response)
//			throws DZFWarpException {
//		//从文件服务器下载图片
//		String filePath = imageBean.getFilepath();
//		OutputStream output = null;
//
//		if (StringUtil.isEmptyWithTrim(filePath)) {
//			throw new BusinessException("下载文件传入参数不能为空！");
//		}
//
//		filePath = CryptUtil.getInstance().decryptAES(filePath);
//
//		if (StringUtil.isEmptyWithTrim(filePath)) {
//			throw new BusinessException("下载文件传入参数不能为空！");
//		}
//		if(filePath.startsWith("/")){
//			filePath = filePath.substring(1);
//		}
//		try {
//			output = response.getOutputStream();
//			byte[] bytes = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).downFile(filePath);
//			output.write(bytes);
//			output.flush();
//		} catch (Exception e) {
//			log.error(e,e);
//		} finally {
//			try {
//				if (output != null) {
//					output.close();
//				}
//			} catch (IOException e) {
//				log.error(e,e);
//			}
//		}
//	}

}

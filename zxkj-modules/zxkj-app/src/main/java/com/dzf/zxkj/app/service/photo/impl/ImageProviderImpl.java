package com.dzf.zxkj.app.service.photo.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.dzf.zxkj.app.model.image.ImageObject;
import com.dzf.zxkj.app.model.image.ImageUploadRecordVO;
import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ImageReqVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.model.sys.OcrInvoiceVOForApp;
import com.dzf.zxkj.app.pub.ImageBeanConvert;
import com.dzf.zxkj.app.pub.UploadRule;
import com.dzf.zxkj.app.service.app.act.IAppApproveService;
import com.dzf.zxkj.app.service.app.act.IAppBusinessService;
import com.dzf.zxkj.app.service.photo.IImageProviderPhoto;
import com.dzf.zxkj.app.utils.BeanUtils;
import com.dzf.zxkj.app.utils.CryptUtil;
import com.dzf.zxkj.app.utils.ImageHelper;
import com.dzf.zxkj.app.utils.ImageProcessor;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.util.Streams;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import org.springframework.web.multipart.MultipartFile;


@Slf4j
@Service("poimp_imagepro")
public class ImageProviderImpl implements IImageProviderPhoto {

	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;
	@Autowired
	private SingleObjectBO singleObjectBO;

//	private IReferenceCheck getRefCheck(){
//		return refchecksrv;
//	}
//
//	private IReferenceCheck refchecksrv;

//	private static int BATCH_ONLINE = 100;//直接生单查询公司上线
//

//
//	private ISysMessageJPush sysmsgsrv;//推送
//
//	private YntBoPubUtil yntBoPubUtil = null;
//

//
//	private IPhotoManage poimp_manage;
//
//	private IVoucherService voucher;
//
//	private ICbComconstant gl_cbconstant;
//	@Autowired
//	private IParameterSetService paramterService;
//
//	@Autowired
//	private IUserService userServiceImpl;
//
//	private static Hashtable<String, Integer> groupcodeTable = new Hashtable<String, Integer>();
//	private static Hashtable<String, Integer> preVoucherBillcodeMap = new Hashtable<String, Integer>();
//	private static Hashtable<String, String> subBetSubjectsMap = new Hashtable<String, String>();  // 公司往来款替代科目表
//
//
//	@Autowired
//	public void setPoimp_manage(IPhotoManage poimp_manage) {
//		this.poimp_manage = poimp_manage;
//	}
//
//	@Autowired
//	public void setRefchecksrv(IReferenceCheck refchecksrv) {
//		this.refchecksrv = refchecksrv;
//	}
//
//	@Autowired
//	public void setYntBoPubUtil(YntBoPubUtil yntBoPubUtil) {
//		this.yntBoPubUtil = yntBoPubUtil;
//	}
//
//	@Autowired
//	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
//		this.singleObjectBO = singleObjectBO;
//	}
//

//
//	public IVoucherService getVoucher() {
//		return voucher;
//	}
//	@Autowired
//	public void setVoucher(IVoucherService voucher) {
//		this.voucher = voucher;
//	}
//
//	@Autowired
//	public void setGl_cbconstant(ICbComconstant gl_cbconstant) {
//		this.gl_cbconstant = gl_cbconstant;
//	}
//

	public ImageQueryBean[] saveUploadImages(UserBeanVO uBean, MultipartFile file, String filenames, InputStream file_in) throws DZFWarpException{
		ImageQueryBean[] beans = null;

		if(StringUtil.isEmptyWithTrim(uBean.getPk_corp())){
			throw new BusinessException("传入的pk_corp参数不能为空！");
		}
		if(StringUtil.isEmptyWithTrim(uBean.getAccount_id())){
			throw new BusinessException("传入的uploader参数不能为空！");
		}
		if(StringUtil.isEmptyWithTrim(uBean.getCorpname())){
			throw new BusinessException("传入的uploadlot参数不能为空！");
		}

		String uploadlot = uBean.getCorpname().toLowerCase();

		//业务合作 证照上传
		if(uBean.getCert() != null && DZFBoolean.valueOf(uBean.getCert()).booleanValue()){
//			return uploadCorpeateInfo(uBean, files, filenames);//这个证照取消，则不处理
		} else{

			if((file == null||file.getSize()<=0) && !ISysConstants.SYS_ADMIN.equals(uBean.getSourcesys()) ){
				throw new BusinessException("传入的fileItem参数不能为空！");
			}

			checkCorp(uBean);
			// 根据上传批次查找之前的记录，并删除
			ArrayList<String> delfiles = delImageLibs(findVOBySessionFlag(uBean.getPk_corp(), uploadlot,uBean.getCert()));
			// 保存压缩包到imageBasePath中，以上传批次作为zip文件名
			File imgzipFile = SaveZipFile(file,file_in, uploadlot+File.separator+filenames,uBean.getSourcesys(),uBean.getCert(),uBean.getAccount(),"","");
			delImgFiles(delfiles);
			// 解压缩文件
			HashMap<String, String> groupSettle = new HashMap<String, String>();
			Map<String, String> groupLibMap = new HashMap<String, String>();
			HashMap<String, String> smallGroupLibMap = new HashMap<String, String>();
			Map<String, String> midGroupLibMap = new HashMap<String, String>();
			TreeMap<String, ArrayList<String>> groupfileMap = unzipFile(imgzipFile, uBean.getPk_corp(), DZFBoolean.valueOf(uBean.getBdata()).booleanValue(),groupSettle,groupLibMap,uBean.getCert(),uBean.getAccount_id(), smallGroupLibMap, midGroupLibMap );
			ImageGroupVO[] aggVOs = buildGroupRecords(groupfileMap,groupSettle, groupLibMap,uBean,filenames, smallGroupLibMap, midGroupLibMap);
			ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
			beans = ct.fromVO(aggVOs);

			if(!ISysConstants.SYS_ADMIN.equals(uBean.getSourcesys())){
				IAppApproveService appapprovehand = (IAppApproveService) SpringUtils.getBean("appapprovehand");
				for(ImageGroupVO tempvo:aggVOs){
					BusiReqBeanVo busibean = new BusiReqBeanVo();
					BeanUtils.copyNotNullProperties(uBean, busibean);
					busibean.setPk_image_group(tempvo.getPk_image_group());
					appapprovehand.updateApprove(busibean,singleObjectBO);
				}
			}

			//根据状态判断是否需要走ocr或 套固定模板生成凭证
			Integer state = null;
			SQLParameter sp = new SQLParameter();
			if(ISysConstants.SYS_ADMIN.equals(uBean.getSourcesys())){
				for(ImageGroupVO tempvo : aggVOs){
					sp.clearParams();
					sp.addParam(tempvo.getPrimaryKey());
					List<ImageGroupVO> gpvolist =  (List<ImageGroupVO>) singleObjectBO.executeQuery(" pk_image_group = ? ", sp, new Class[]{ImageGroupVO.class,ImageLibraryVO.class});//.queryObject( beanvo.getGroupkey(), new Class[]{ImageGroupVO.class,ImageLibraryVO.class});
					tempvo =  gpvolist.get(0);
					state = tempvo.getIstate();
					if(state == null || state == PhotoState.state0){
						IAppBusinessService appbusihand = (IAppBusinessService) SpringUtils.getBean("appbusihand");
						appbusihand.saveVoucherFromPic(tempvo.getPrimaryKey(), tempvo.getPk_corp(),singleObjectBO);
					}
				}
			}


		}

		return beans;
	}


	/**
	 * 公司业务校验
	 * @param uBean
	 */
	private void checkCorp(UserBeanVO uBean) {

		List<String> kpdates =  null;
		if(!StringUtil.isEmpty(uBean.getKpdate())){
			kpdates =  (ArrayList<String>)JSONUtils.parse(uBean.getKpdate());
		}
		List<String> dates =  new ArrayList<String>();;

		if(kpdates == null || kpdates.size() == 0){
			dates.add(DateUtils.getPeriod(new DZFDate()));
		}else{
			for(String str:kpdates){
				if(!dates.contains(str.substring(0,7))){
					dates.add(str.substring(0,7));
				}
			}
		}

		for(String period:dates){
			iZxkjRemoteAppService.isQjSyJz(uBean.getPk_corp(), period);

			iZxkjRemoteAppService.isGz(uBean.getPk_corp(), period);
		}
	}

	/**
	 * 删除图片组下的所有图片记录
	 * @param groupVOs
	 * @return
	 * @throws BusinessException
	 */
	protected ArrayList<String> delImageLibs(ImageGroupVO[] groupVOs) throws DZFWarpException {
		if(groupVOs== null || groupVOs.length == 0) return null;

		ArrayList<String> delfiles = new ArrayList<String>();
		List<String> imggppkList = new ArrayList<String>();
		for(ImageGroupVO groupVO : groupVOs){
			// 校验是否被档案引用
			if (iZxkjRemoteAppService.isReferenced(groupVO.getTableName(), groupVO.getPrimaryKey())) {
				throw new BusinessException("已经被引用，不能删除！");
			}
			imggppkList.add(groupVO.getPk_image_group());
		}
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0)=0 and pk_corp = '")
			.append(groupVOs[0].getPk_corp())
				.append("' and ")
					.append(SqlUtil.buildSqlForIn("pk_image_group", imggppkList.toArray(new String[0])));

		ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, sf.toString(),new SQLParameter());

		if(imageVOs == null || imageVOs.length == 0)
			throw new BusinessException("未找到相应的图片信息，请检查");

		for(ImageLibraryVO imageVO: imageVOs){
			// 校验是否被档案引用
			if (iZxkjRemoteAppService.isReferenced(imageVO.getTableName(), imageVO.getPrimaryKey())) {
				throw new BusinessException("已经被引用，不能删除！");
			}
			delfiles.add(imageVO.getImgpath());
		}

		singleObjectBO.deleteVOArray(imageVOs);
		singleObjectBO.deleteVOArray(groupVOs);
//		for(ImageGroupVO groupVO: groupVOs){
//			String where = String.format("nvl(dr,0)=0 and pk_image_group='%s'", groupVO.getPrimaryKey());
//			ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, where,new SQLParameter());
//			if(imageVOs == null || imageVOs.length == 0) continue;
//
//			for(ImageLibraryVO imageVO: imageVOs){
//				// 校验是否被档案引用
//				if (getRefCheck().isReferenced(imageVO.getTableName(), imageVO.getPrimaryKey())) {
//					throw new BusinessException("已经被引用，不能删除！");
//				}
//				delfiles.add(imageVO.getImgpath());
//				singleObjectBO.deleteObject(imageVO);
//			}
//
//			singleObjectBO.deleteObject(groupVO);
//		}
		return delfiles;
	}

	/**
	 * 根据会话标识查找图片组记录
	 * @param pk_corp
	 * @param sessionflag
	 * @return
	 * @throws DZFWarpException
	 */
	protected ImageGroupVO[] findVOBySessionFlag(String pk_corp, String sessionflag,String cert) throws DZFWarpException{
//		String certWhere = "";
//		if(){
//			certWhere = "nvl(cert,'N')='Y'  and ";
//		}
//		String where = String.format(certWhere+"", pk_corp, sessionflag);
		StringBuffer sf = new StringBuffer();
		if(cert != null && DZFBoolean.valueOf(cert).booleanValue()){
			sf.append("nvl(cert,'N')='Y'  and ");
		}
		sf.append(" nvl(dr,0)=0 and pk_corp= ? and sessionflag= ? ");
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		param.addParam(sessionflag);
		return (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class, sf.toString(), param);
	}

	public ImageQueryBean[] queryImages(String pk_corp,String strWhere,SQLParameter sp) throws DZFWarpException
	  {
//	    String where ="nvl(dr,0)=0 and "+strWhere+" order by groupcode desc";//, new Object[] { strWhere };
//	    //SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
//	    ImageGroupVO[] ivo = (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class, where, sp);
//	    ImageQueryBean[] beans = null;
//	    if(ivo != null){
//	    	ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
//	    	beans = ct.fromVO(ivo);
//	    }
//		return beans;
		ImageQueryBean[] beans = null;
		try{
//			String where = String.format("nvl(dr,0)=0 and nvl(cert,'N')='N' and %s order by groupcode desc", strWhere);
//			ImageGroupVO[] groupVOs = (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class, where,new SQLParameter());
//			if(groupVOs == null || groupVOs.length == 0) return beans;
//			for(ImageGroupVO groupVO: groupVOs){
//				where = String.format("nvl(dr,0)=0 and pk_image_group='%s' order by imgname", groupVO.getPrimaryKey());
//				groupVO.setChildren(singleObjectBO.queryByCondition(ImageLibraryVO.class, where,new SQLParameter()));
//			}
			StringBuffer sf = new StringBuffer();
			sf.append(" nvl(dr,0)=0 and nvl(cert,'N')='N' and ")
				.append(strWhere);
			List<ImageGroupVO> groupVOList = (List<ImageGroupVO>) singleObjectBO.executeQuery(sf.toString(),
					new SQLParameter(), new Class[]{ImageGroupVO.class, ImageLibraryVO.class});
			Collections.sort(groupVOList, new Comparator<ImageGroupVO>() {
				@Override
				public int compare(ImageGroupVO o1, ImageGroupVO o2) {
					int i = o2.getGroupcode().compareTo(o1.getGroupcode());
					return i;
				}
			});
			for(ImageGroupVO imageGroupVO : groupVOList){
				ImageLibraryVO[] imgLibVOs = (ImageLibraryVO[]) imageGroupVO.getChildren();
				java.util.Arrays.sort(imgLibVOs, new Comparator<ImageLibraryVO>() {
					@Override
					public int compare(ImageLibraryVO o1, ImageLibraryVO o2) {
						int i = o1.getImgname().compareTo(o2.getImgname());
						return i;
					}
				});
			}

			ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
			beans = ct.fromVO(groupVOList.toArray(new ImageGroupVO[0]));
//			beans = ct.fromVO(groupVOs);
//			beans = ImageQueryBean.fromVO(groupVOs);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			throw new WiseRunException(e);
		}
		return beans;

	  }

	/**
	 * 保存压缩文件
	 * @param fileItem
	 * @param filename
	 * @throws DZFWarpException
	 */
	public File SaveZipFile(FileItem fileItem, String filename,String cert,String account) throws DZFWarpException{
		try{
			/**liangyi*/
			String filePath =	ImageCommonPath.getZipPath(filename);

			if(cert != null && DZFBoolean.valueOf(cert).booleanValue()){
				//证件保存目录，账号+文件名
				filePath = ImageCommonPath.getCertZipPath(account, filename);
			}
			File imgzipFile = new File(filePath);
			if(!imgzipFile.getParentFile().exists())
				imgzipFile.getParentFile().mkdirs();
			fileItem.write(imgzipFile);
			return imgzipFile;
		} catch(Exception e){
			log.error(e.getMessage(), e);
			throw new WiseRunException(e);
		}
	}

	/**
	 * 保存压缩文件
	 * @param f
	 * @param filename
	 * @throws DZFWarpException
	 */
	public File SaveZipFile(MultipartFile f,InputStream file_in,String filename,String sys_source,
			String cert,String account,String logo,String permit) throws DZFWarpException{
		InputStream in = null;
		OutputStream out = null;
		try{
			/**liangyi*/
			String filePath =	ImageCommonPath.getZipPath(filename);

			if(cert != null && DZFBoolean.valueOf(cert).booleanValue()){
				//证件保存目录，账号+文件名
				filePath = ImageCommonPath.getCertZipPath(account, filename);
			}
			if(logo!=null && DZFBoolean.valueOf(logo).booleanValue()){
				//logo保存目录，账号+文件名
				filePath = ImageCommonPath.getLogoUnZipPath(account, filename);
			}
			if(permit!=null && DZFBoolean.valueOf(permit).booleanValue()){
				//营业执照保存目录，账号+文件名
				filePath = ImageCommonPath.getPermitUnZipPath(account, filename);
			}


			File imgzipFile = new File(filePath);
			if(!imgzipFile.getParentFile().exists())
				imgzipFile.getParentFile().mkdirs();


			if(!StringUtil.isEmpty(sys_source) && ISysConstants.SYS_ADMIN.equals(sys_source)){//管理端传递流
				in = file_in;
			}else{
				in = f.getInputStream();
			}
			out = new FileOutputStream(imgzipFile);

			Streams.copy(in, out, true);
			return imgzipFile;
		} catch(Exception e){
			log.error(e.getMessage(), e);
			throw new WiseRunException(e);
		}finally{
			try{
				if(in != null){
					in.close();
				}
				if(out != null){
					out.close();
				}
			}catch(Exception e){
				log.error(e.getMessage(), e);
			}

		}
	}

	/**
	 * 删除图片文件
	 * @param delfiles
	 */
	protected void delImgFiles(ArrayList<String> delfiles){
		if(delfiles != null && delfiles.size() > 0){
			File imgFile = null;
			for(String delfile: delfiles){
				imgFile = new File(Common.imageBasePath + delfile);
				if(imgFile.exists())
					imgFile.delete();
			}
		}
	}

	/**
	 * 解压缩文件
	 * @param zipfile
	 * @param pk_corp1
	 * @param isCompress
	 * @return
	 * @throws DZFWarpException
	 */
	protected TreeMap<String, ArrayList<String>> unzipFile(File zipfile, String pk_corp1, boolean isCompress,HashMap<String, String> groupSettle, Map<String, String> groupLibMap, String cert,String uploader,
			Map<String, String> smallGroupLibMap, Map<String, String> midGroupLibMap) throws DZFWarpException{
		try{
			DZFDate date = new DZFDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String unitcode;
			if(Common.tempidcreate.equalsIgnoreCase(pk_corp1)){
				unitcode=pk_corp1;
			}else{
//				CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp1);
				CorpVO corpvo = iZxkjRemoteAppService.queryByPk(pk_corp1);
				unitcode = corpvo.getUnitcode();
			}
			String filePath = ImageCommonPath.getUnZipPath(unitcode, sdf.format(date.toDate()));
			if(cert != null && DZFBoolean.valueOf(cert).booleanValue()){
				filePath = ImageCommonPath.getCertUnZipPath(unitcode, sdf.format(date.toDate()));
			}
			File unzipDir = new File(filePath);
			unzipDir.mkdirs();

			ZipFile zip = new ZipFile(zipfile);
			Enumeration enumeration = zip.entries();
			HashMap<String, String> groupmap = new HashMap<String, String>();
			TreeMap<String, ArrayList<String>> resultmap = new TreeMap<String, ArrayList<String>>();
			//
			List<ZipEntry> list  = new ArrayList<ZipEntry>();
			ZipEntry entrye = null;
			while(enumeration.hasMoreElements()){
				entrye = (ZipEntry) enumeration.nextElement();
				list.add(entrye);
			}
//			GroupMaxCodeCache.getInstance().remove(pk_corp1);
			//zpm
			ImageSort sort = new ImageSort();//排序
    		Collections.sort(list, sort);
    		String[] nameArray = null;
    		String groupKey = null;
    		String groupname = null;
    		String imagename = null;

    		String imgvirname = null;
    		String imgrealname = null;
    		String imgsuffix = null;
    		File imagefile = null;
    		BufferedInputStream in = null;
    		FileOutputStream fileout = null;
    		BufferedOutputStream out = null;
			//排序
			for(ZipEntry entry :list){
				nameArray = entry.getName().split("-");

				if(nameArray.length != 2){
					throw new BusinessException("压缩包中文件的文件名必须是[图片组名-图片文件名]格式的字符串");
				}
				groupKey = nameArray[0];
				if(groupKey.contains("/")){
					groupKey=groupKey.substring(groupKey.indexOf("/")+1);
				}
				if(!groupmap.containsKey(groupKey))
					groupmap.put(groupKey, getGroupCode(uploader, pk_corp1));
				// 图片组名
				groupname = groupmap.get(groupKey);
				if(!resultmap.containsKey(groupname)){//付款方式-----已由新接口走，已不从这里走了。数据中心没有付款方式。
					resultmap.put(groupname, new ArrayList<String>());
					groupSettle.put(groupname, groupKey);
				}

				// 文件名
				imgsuffix = ImageHelper.getFileExt(entry.getName());
				imgvirname = getImageName(groupname, resultmap.get(groupname).size() + 1) + imgsuffix;
				imgrealname = UUID.randomUUID().toString();
				imagename = unzipDir.getAbsolutePath()  + File.separator + imgrealname + imgsuffix;

				imagefile = new File(imagename);
				resultmap.get(groupname).add(imagefile.getAbsolutePath());

				groupLibMap.put(imagefile.getAbsolutePath(), imgvirname);//图片真实名称与虚拟名称映射

				in = new BufferedInputStream(zip.getInputStream(entry));
				fileout = new FileOutputStream(imagefile);
				out = new BufferedOutputStream(fileout);
				try {
					byte[] buffer = new byte[1024];
					int count = 0;
					while((count = in.read(buffer, 0, buffer.length)) > 0 ){
						out.write(buffer, 0, count);
					}
					out.flush();

					//小图与压缩图
					storageImgFile(imagefile, unzipDir, imgsuffix, smallGroupLibMap, midGroupLibMap);

				} finally {
					try{
						if(in != null)
							in.close();
						if(out != null)
							out.close();
						if(fileout != null)
							fileout.close();
					} catch(Exception e){
						log.error(e.getMessage(), e);
					}
				}
				// 是否需要压缩
				if(isCompress){
					ImageHelper.compressImage(imagename).writeToFile(new File(imagename), 0.5f);
				}
			}
			if(zip != null){
				zip.close();
			}
			return resultmap;
		} catch(Exception e){
			log.error(e.getMessage(), e);
			throw new WiseRunException(e);
		}
	}

	/**
	 * 获取图片组号
	 * @param userid
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private String getGroupCode(String userid, String pk_corp) throws DZFWarpException {
//		String groupCode = GroupMaxCodeCache.getInstance().get(userid, pk_corp);
		long maxCode = iZxkjRemoteAppService.getNowMaxImageGroupCode(pk_corp);
		String groupCode = "";
		if(maxCode > 0){
			groupCode = maxCode + 1 + "";
		}else{
			groupCode = getCurDate() + "0001";
		}
		return groupCode;
	}
	//	获取当前年月日
	private String getCurDate(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(Calendar.getInstance().getTime());
	}
	/**
	 * 获取图片名称
	 * @param groupcode
	 * @param index
	 * @return
	 */
	private String getImageName(String groupcode, int index){
		// 图片名称由图片组号+3位流水组成
		return String.format("%s-%03d", groupcode, index);
	}

	/**
	 * WJX 创建图片上传记录信息
	 * */
	protected ImageGroupVO[] buildGroupRecords(TreeMap<String, ArrayList<String>> groupfileMap, HashMap<String, String> groupSettle, Map<String,String> groupLibMap, UserBeanVO uBean,String groupfilename,
			Map<String, String> smallGroupLibMap, Map<String, String> midGroupLibMap) throws DZFWarpException{

		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(uBean.getPk_corp());
		if(groupfileMap == null || groupfileMap.size() == 0) return null;
		ImageGroupVO[] aggVOs = new ImageGroupVO[groupfileMap.size()];
		ImageGroupVO groupVO = null;

		List<String> settles = null;
		if(!StringUtil.isEmpty(uBean.getPaymethod())){
			settles = (ArrayList<String>)JSONUtils.parse(uBean.getPaymethod());
		}
		List<String> memos =  null;
		if(!StringUtil.isEmpty(uBean.getMemo())){
			memos =  (ArrayList<String>)JSONUtils.parse(uBean.getMemo());
		}
		List<String> memos1 = null;
		if(!StringUtil.isEmpty(uBean.getMemo1())){
			memos1 =  (ArrayList<String>)JSONUtils.parse(uBean.getMemo1());
		}
		List<String> mnys =  null;
		if(!StringUtil.isEmpty(uBean.getMny())){
			mnys =  (ArrayList<String>)JSONUtils.parse(uBean.getMny());
		}
		List<String> kpdates =  null;
		if(!StringUtil.isEmpty(uBean.getKpdate())){
			kpdates =  (ArrayList<String>)JSONUtils.parse(uBean.getKpdate());
		}

		int index=0;

		String kpdate = "";

		for(String group: groupfileMap.keySet()){

			Integer i = new Integer(groupSettle.get(group))-1;

			kpdate = (kpdates == null) ? new DZFDate().toString():kpdates.get(i);

			groupVO = createGroupVO(null,kpdate,uBean.getPk_corp(), uBean.getCorpname().toLowerCase(), uBean.getAccount_id(), group);

			groupVO.setSourcemode(PhotoState.SOURCEMODE_01);//手机上传

			if(settles!=null&&settles.get(i)!=null){//结算方式
				groupVO.setSettlemode(settles.get(i));
			}
			if(memos!=null&&memos.get(i)!=null){//摘要
				groupVO.setMemo(memos.get(i));
			}
			if(mnys!=null&&mnys.get(i)!=null){//金额
				groupVO.setMny(new DZFDouble(mnys.get(i)));
			}
			if(memos1!=null&&memos1.get(i)!=null){//备注
				groupVO.setMemo1(memos1.get(i));
			}

			groupVO.setOtcorp(uBean.getOtcorp());//重新上传时写入对方单位

			if(uBean.getCert() != null){
				groupVO.setCert(DZFBoolean.valueOf(uBean.getCert()));
			}
			groupVO.setSessionflag(groupfilename+".zip");//分组ZIP包名
			groupVO.setCerttx(uBean.getCerttx());
			groupVO.setChildren(createImageVO(groupVO, groupfileMap.get(group), groupLibMap, smallGroupLibMap, midGroupLibMap));
			groupVO.setPicflowid(cpvo.getDef4());//走自动化
			groupVO.setImagecounts(groupVO.getChildren().length);//图片数量
			aggVOs[index++] = groupVO;
		}
		String[] headpks = singleObjectBO.insertVOArr(uBean.getPk_corp(), aggVOs);//更新主表
		List<ImageLibraryVO> childrenList = new ArrayList<ImageLibraryVO>();
		for(int i = 0; i < aggVOs.length; i++){
			ImageLibraryVO[] imglibvos = (ImageLibraryVO[]) aggVOs[i].getChildren();
			for(int j = 0; j < imglibvos.length; j++){
				imglibvos[j].setPk_image_group(aggVOs[i].getPrimaryKey());
				childrenList.add(imglibvos[j]);
			}
		}
		if(childrenList.size() > 0){
			singleObjectBO.insertVOArr(uBean.getPk_corp(), childrenList.toArray(new ImageLibraryVO[0]));
		}

		return aggVOs;
	}
	/**
	 * 创建图片组VO
	 * @param pk_corp
	 * @param sessionflag
	 * @param uploader
	 * @param groupcode
	 * @return
	 * @throws DZFWarpException
	 */
	private ImageGroupVO createGroupVO(Integer sel,String qj,String pk_corp, String sessionflag, String uploader, String groupcode) throws DZFWarpException{
		ImageGroupVO groupVO = new ImageGroupVO();
		groupVO.setCoperatorid(uploader);
		groupVO.setDoperatedate(new DZFDate());
		groupVO.setPk_corp(pk_corp);
		groupVO.setSessionflag(sessionflag);
		groupVO.setGroupcode(groupcode);
		if(sel == null)
			sel = 1;
		if(UploadRule.sel1.intValue() == sel.intValue()){//直接生单
			//不启用华道 、不启用切图、识图，上传图片状态为自由态
			groupVO.setIstate(PhotoState.state0);
		}else if(UploadRule.sel2.intValue() ==sel.intValue()){//识图切图
			//不用做任何操作
			groupVO.setIstate(PhotoState.state10);//
		}else if(UploadRule.sel3.intValue() == sel.intValue()){//华道生单
			groupVO.setIshd(DZFBoolean.TRUE);
		}

		//TODO 测试期间，默认全部图片走华道处理接口，后期需要修改
//		int count = poimp_manage.newClipingCount(pk_corp,null);
//		int count = getNewClipingCount(pk_corp);
//		int hd_imagesCount = readXMLImagesSet();
//		if(count > hd_imagesCount){
//			groupVO.setIshd(DZFBoolean.TRUE);
//		}

		if(qj != null && qj.length() > 0){
			//凭证日期:::
			if(qj.length() == 10){//qj格式满足格式为 2016-01-01
				groupVO.setCvoucherdate(new DZFDate(qj));//生成凭证时间
			}else{
				int year = Integer.valueOf(qj.substring(0, 4));
				int month = Integer.valueOf(qj.substring(5, 7));
				DZFDate cdate = getMaxDayByYearMonth(year,month);
				groupVO.setCvoucherdate(cdate);//生成凭证时间
			}

		}
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		groupVO.setPicflowid(cpvo.getDef4());

		return groupVO;
	}
	//根据期间生成当月最后一天
	public DZFDate getMaxDayByYearMonth(int year,int month) {
		String period = year + "-" + month;
		return DateUtils.getPeriodEndDate(period);
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(year,month-1,1);
//        calendar.set(Calendar.DATE, calendar.getActualMaximum(Calendar.DATE));
//        Date cd = calendar.getTime();
//        SimpleDateFormat sf = new SimpleDateFormat ("yyyy-MM-dd");
//        String date = sf.format(cd);
//        return new DZFDate(date);
	}

	/**
	 * 创建图片VO
	 * @param groupVO
	 * @param imgfiles
	 * @return
	 */
	private ImageLibraryVO[] createImageVO(ImageGroupVO groupVO, ArrayList<String> imgfiles, Map<String, String> groupLibMap,
			Map<String, String> smallGroupLibMap, Map<String, String> midGroupLibMap){
		if(imgfiles == null || imgfiles.size() == 0) return null;
		ImageLibraryVO[] imageVOs = new ImageLibraryVO[imgfiles.size()];
		ImageLibraryVO imageVO = null;
		File file = null;
		File basedir = new File(Common.imageBasePath);
		String smallImgePath = null;
		String middleImagePath = null;
		for(int i = 0; i<imgfiles.size(); i++){
			file = new File(imgfiles.get(i));

			smallImgePath = smallGroupLibMap.get(file.getAbsolutePath());
			middleImagePath = midGroupLibMap.get(file.getAbsolutePath());

			imageVO = new ImageLibraryVO();
			imageVO.setPk_corp(groupVO.getPk_corp());
			imageVO.setCoperatorid(groupVO.getCoperatorid());
			imageVO.setDoperatedate(groupVO.getDoperatedate());
			imageVO.setCvoucherdate(groupVO.getCvoucherdate());
//			imageVO.setImgname(file.getName());
			imageVO.setImgname(groupLibMap.get(file.getAbsolutePath()));//名称
			imageVO.setImgpath(file.getAbsolutePath().replace(basedir.getAbsolutePath() + File.separator, ""));   // 去掉根目录路径
			imageVO.setSmallimgpath(StringUtil.isEmpty(smallImgePath)? "" : smallImgePath.replace(basedir.getAbsolutePath() + File.separator, ""));

			imageVO.setMiddleimgpath(StringUtil.isEmpty(middleImagePath)? "" : middleImagePath.replace(basedir.getAbsolutePath() + File.separator, ""));
//			imageVO.setStatus(nc.vo.pub.VOStatus.NEW);
			imageVOs[i] = imageVO;
		}

		return imageVOs;
	}

	private void storageImgFile(File srcFile,
			File unzipDir,
			String imgsuffix,
			Map<String, String> smallGroupLibMap,
			Map<String, String> midGroupLibMap){
		try {

			if(srcFile.length() > 100 * 1024){//100k
				String midImgname = UUID.randomUUID().toString() + imgsuffix;
				midImgname =  unzipDir.getAbsolutePath()  + File.separator + midImgname;
				File destMidFile = new File(midImgname);
				ImageTools.compImgByScale(0.25f, srcFile, destMidFile);
				midGroupLibMap.put(srcFile.getAbsolutePath(), destMidFile.getAbsolutePath());
			}

			String smallImgname = UUID.randomUUID().toString() + imgsuffix;
			smallImgname =  unzipDir.getAbsolutePath()  + File.separator + smallImgname;
			File dstSmallFile = new File(smallImgname);
			ImageTools.compImgBySize(260, 170, srcFile, dstSmallFile);
			smallGroupLibMap.put(srcFile.getAbsolutePath(), dstSmallFile.getAbsolutePath());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("保存图片失败");
		}
	}
	/**
	 * 重传图片
	 * @param uBean
	 * @param imgbeanvos
	 * @param file
	 * @param filename
	 * @return
	 * @throws DZFWarpException
	 */
	public int saveReuploadImage(UserBeanVO uBean, ImageBeanVO[] imgbeanvos, MultipartFile file,String filename, InputStream file_in) throws DZFWarpException{
		ImageQueryBean[] beans = null;
		int count = 0;
		if(imgbeanvos == null || imgbeanvos.length == 0){
			throw new BusinessException("未选择重传图片，请检查");
		}
		List libvalueList = new ArrayList<String>();
		Map<String, ImageBeanVO> imgbeanMap = new HashMap<String, ImageBeanVO>();
		for(ImageBeanVO imgbeanvo : imgbeanvos){
			libvalueList.add(imgbeanvo.getImagekey());
			imgbeanMap.put(imgbeanvo.getImagekey(), imgbeanvo);
		}

		StringBuffer sfwhere = new StringBuffer();
		sfwhere.append(" nvl(dr,0) = 0 and  ")
				.append(SqlUtil.buildSqlForIn("pk_image_library", (String[]) libvalueList.toArray(new String[0])));
		ImageLibraryVO[] imglibvos = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, sfwhere.toString(), null);
		String pkgrp = "";
		if(imglibvos == null || imglibvos.length == 0){
			throw new BusinessException("重传图片信息不存在，请检查！");
		}
		ImageBeanVO imgbeanvo = null;
		String pk_image_library = null;
		String imgPath = null;
		String grpcheckpk = "";
		for(int i = 0; i < imglibvos.length; i++){
			imgbeanvo = imgbeanMap.get(imglibvos[i].getPk_image_library());
			pk_image_library = imgbeanvo.getImagekey();
			imgPath = imgbeanvo.getFilepath();
			DZFBoolean isback = imglibvos[i].getIsback();
			if(isback == null || isback == DZFBoolean.FALSE){
				throw new BusinessException(String.format("%s图片状态不是已退回，请检查", imglibvos[i].getImgname()));
			}
			if (StringUtil.isEmpty(pk_image_library) || StringUtil.isEmpty(imgPath)) {
				throw new BusinessException("重传图片信息不详细，请重新上传！");
			}
			if (!imgPath.equals(imglibvos[i].getImgpath())) {
				throw new BusinessException("重传图片信息检验失败，请重新上传！");
			}
			if(i == 0){
				pkgrp = imglibvos[i].getPk_image_group();
			}else{
				grpcheckpk = imglibvos[i].getPk_image_group();
				if(!pkgrp.equals(grpcheckpk)){
					throw new BusinessException("上传的系列图片不是同一组，请检查！");
				}
			}

		}

		try{
			ZipInputStream zs = new ZipInputStream(file.getInputStream());
//			ZipFile zip = new ZipFile(file);
//			Enumeration enumeration = zip.entries();
			List<ZipEntry> list  = new ArrayList<ZipEntry>();
			ZipEntry ze = null;
			while((ze = zs.getNextEntry()) != null){
//				entrye = (ZipEntry) enumeration.nextElement();
				list.add(ze);
			}
			ImageSort sort = new ImageSort();//排序
			Collections.sort(list, sort);
			java.util.Arrays.sort(imgbeanvos, new Comparator<ImageBeanVO>() {
				@Override
				public int compare(ImageBeanVO arg0, ImageBeanVO arg1) {
					int i = arg1.getFilepath().compareTo(arg0.getFilepath());
					return 0;
				}
			});
			ZipEntry entryy = null;
			String entryname = null;
			String imgname = null;
			String filesuff = null;
			String entrysuff= null;
			for(int k = 0; k < list.size(); k++){
				entryy = list.get(k);
				entryname = entryy.getName();
				imgname = Common.imageBasePath + imgbeanvos[0].getFilepath();
				filesuff = imgname.substring(imgname.lastIndexOf(".") + 1).trim().toLowerCase();
				entrysuff= entryname.substring(entryname.lastIndexOf(".") + 1).trim().toLowerCase();
				if(!filesuff.equals(entrysuff)){
					throw new BusinessException("本次上传图片类型与之前不一致，请检查！");
				}
			}
			ZipEntry entry = null;
			String imagename = null;
			File imagefile = null;
//    		BufferedInputStream in = null;
			FileOutputStream fileout = null;
			BufferedOutputStream out = null;
			for(int j = 0; j < list.size(); j++){
				entry = list.get(j);
				imagename = Common.imageBasePath + imgbeanvos[0].getFilepath();
				imagefile = new File(imagename);

//    			in = new BufferedInputStream(zip.getInputStream(entry));
				fileout = new FileOutputStream(imagefile);
				out = new BufferedOutputStream(fileout);

				try {
					byte[] buffer = new byte[1024];
					int coun = 0;
					while((coun = zs.read(buffer, 0, buffer.length)) > 0 ){
						out.write(buffer, 0, coun);
					}
					out.flush();
				} finally {
					try{
//						if(in != null)
//							in.close();
						if(out != null)
							out.close();
						if(fileout != null)
							fileout.close();
					} catch(Exception e){
						log.error(e.getMessage(), e);
					}
				}
			}
			zs.close();

			String wherepart = " pk_image_group = ? and nvl(dr,0) = 0 " ;
			SQLParameter sp = new SQLParameter();
			sp.addParam(pkgrp);
			List<ImageGroupVO> ivo = (List<ImageGroupVO>) singleObjectBO.executeQuery(wherepart, sp, new Class[]{ImageGroupVO.class,ImageLibraryVO.class});
			ImageGroupVO imggrpVO = ivo.get(0);


			ImageLibraryVO[] imglibVOs = (ImageLibraryVO[]) imggrpVO.getChildren();
			Integer dr = null;
			int imagecount = 0;
			for(int k = 0; k < imglibVOs.length; k++){
				dr = imglibVOs[k].getDr();
				if(dr != null && dr == 1){
					continue;
				}
				if(libvalueList.contains(imglibVOs[k].getPk_image_library())){
					imglibVOs[k].setAttributeValue("isback", DZFBoolean.FALSE);//自由态
					imagecount++;
				}else if(imglibVOs[k].getIsback() != null
						&& imglibVOs[k].getIsback().booleanValue() == DZFBoolean.TRUE.booleanValue()){
					imglibVOs[k].setAttributeValue("dr", 1);//自由态
				}

			}

			imggrpVO.setImagecounts(imagecount);
			imggrpVO.setIsskiped(DZFBoolean.FALSE);
			imggrpVO.setIsuer(DZFBoolean.FALSE);//设置为未使用
			imggrpVO.setIstate(PhotoState.state0);//自由态
			singleObjectBO.update(imggrpVO);

			//消息
			iZxkjRemoteAppService.deleteMsg(imggrpVO);

			count = singleObjectBO.updateAry(imglibVOs);
		}catch(Exception e){
			log.error(e.getMessage(), e);
			if(e instanceof BusinessException){
				throw new BusinessException(e.getMessage());
			}else{
				throw new WiseRunException(e);
			}

		}finally{

		}
		return count;
	}
	/**
	 *WJX  根据group sessionflag 获取历史图片记录
	 * */
	public ImageUploadRecordVO[] queryImages(ImageBeanVO beanvo) throws DZFWarpException
	  {

		if(StringUtil.isEmpty(beanvo.getImageparams()) && StringUtil.isEmpty(beanvo.getGroupkey())){
			throw new BusinessException("获取列表失败，请重新刷新获取!");
		}
		SQLParameter sp = new SQLParameter();
		StringBuffer wherepart = new StringBuffer();

		wherepart.append("   pk_corp = ? " );
		sp.addParam(beanvo.getPk_corp());
		if(!StringUtil.isEmpty(beanvo.getImageparams())){
			wherepart.append(" and  SESSIONFLAG = ?  ");
			sp.addParam(beanvo.getImageparams());//照片组sessionflag
		}
		if(!StringUtil.isEmpty(beanvo.getGroupkey())){
			wherepart.append(" and pk_image_group = ? ");
			sp.addParam(beanvo.getGroupkey());
		}
		wherepart.append(" AND NVL(DR,0)=0 ORDER BY GROUPCODE DESC");

	    @SuppressWarnings("unchecked")
		List<ImageGroupVO> ivo = (List<ImageGroupVO>) singleObjectBO.executeQuery(wherepart.toString(), sp, new Class[]{ImageGroupVO.class,ImageLibraryVO.class});


	    ImgGroupRsBean[] beans = null;
	    if(ivo != null){
	    	List<String> imggrpList = new ArrayList<String>();
	    	//针对主子表查询  查询dr=1问题修改，singleObjectBO后期如修改， 该代码可删除
	    	for(ImageGroupVO igvo : ivo){
	    		ImageLibraryVO[] ilibvos = (ImageLibraryVO[]) igvo.getChildren();
	    		imggrpList.add(igvo.getPrimaryKey());
	    		if(ilibvos != null){
	    			List<ImageLibraryVO> ilibvoList = new ArrayList<ImageLibraryVO>();
	    			for(ImageLibraryVO ilibvo : ilibvos){
	    				Integer dr = ilibvo.getDr();
	    				if(dr != null && dr == 1){

	    				}else{
	    					ilibvoList.add(ilibvo);
	    				}

	    			}
	    			igvo.setChildren( ilibvoList.toArray(new ImageLibraryVO[0]));
	    		}
	    	}

	    	ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
	    	beans = ct.fromImgHisVO(ivo.toArray(new ImageGroupVO[0]));

	    	//追加退回原因 begin
	    	Map<String, ImgGroupRsBean> beanMap = new HashMap<String, ImgGroupRsBean>();
	    	for(ImgGroupRsBean bean : beans){
	    		beanMap.put(bean.getGroupKey(), bean);
	    	}
	    	String turnmsgWhe = SqlUtil.buildSqlForIn("pk_image_group", imggrpList.toArray(new String[0]));
	    	StringBuffer tmsgbf = new StringBuffer();
	    	tmsgbf.append(" nvl(dr,0) = 0 and ").append(turnmsgWhe);
	    	ImageTurnMsgVO[] imageTurnVOs = (ImageTurnMsgVO[]) singleObjectBO.queryByCondition(ImageTurnMsgVO.class, tmsgbf.toString(), null);
	    	if(imageTurnVOs != null && imageTurnVOs.length != 0){
	    		java.util.Arrays.sort(imageTurnVOs, new Comparator<ImageTurnMsgVO>() {
					@Override
					public int compare(ImageTurnMsgVO o1, ImageTurnMsgVO o2) {
						int i = 0;
						if(o2.getPk_image_group().compareTo(o1.getPk_image_group()) == 0){
							i = o2.getPrimaryKey().compareTo(o1.getPrimaryKey());
						}else{
							i = o2.getPk_image_group().compareTo(o1.getPk_image_group());
						}
						return i;
					}
				});
	    		Map<String,ImageTurnMsgVO> checkMap = new HashMap<String,ImageTurnMsgVO>();
	    		for(int i = 0; i < imageTurnVOs.length; i++){
	    			if(checkMap.containsKey(imageTurnVOs[i].getPk_image_group())){
	    				continue;
	    			}else{
	    				ImgGroupRsBean msgbean = beanMap.get(imageTurnVOs[i].getPk_image_group());
	    				msgbean.setBackReason(imageTurnVOs[i].getMessage());//设置退回原因
	    				checkMap.put(imageTurnVOs[i].getPk_image_group(), imageTurnVOs[i]);
	    			}
	    		}
	    	}
	    	//追加退回原因 end
	    }

	    ImageUploadRecordVO[] upbeans = null ;
	    if(beans!=null && beans.length>0){
	    	upbeans = new ImageUploadRecordVO[beans.length];
	    	ImageUploadRecordVO gpvo = null;
	    	for(int i=0;i<beans.length;i++){
	    		gpvo = new ImageUploadRecordVO();
	    		BeanUtils.copyNotNullProperties(beans[i], gpvo);
	    		upbeans[i]= gpvo;
	    	}
	    }

		return upbeans;
	  }
	/**
	 * 获取用户上传记录
	 * @author Administrator accountid
	 * */
	@Override
	public ImageUploadRecordVO[] queryUploadRecord(ImageReqVO uBean ) throws DZFWarpException {

		String gpsql =" SELECT * FROM YNT_IMAGE_GROUP WHERE COPERATORID = ? AND DOPERATEDATE>=? AND  DOPERATEDATE<=? AND PK_CORP = ? AND NVL(DR,0)=0 ORDER BY ts desc,SESSIONFLAG";
		SQLParameter sp = new SQLParameter();
		sp.addParam(uBean.getAccount_id());
		sp.addParam(DateUtils.getPeriodStartDate(uBean.getStartdate()));
		sp.addParam(DateUtils.getPeriodEndDate(uBean.getEnddate()));
		sp.addParam(uBean.getPk_corp());
		List<ImageGroupVO> groups = (ArrayList<ImageGroupVO>)singleObjectBO.executeQuery(gpsql, sp, new BeanListProcessor(ImageGroupVO.class));

		if(groups==null||groups.size()<=0){
			return null;
		}

		Map<String,ImageUploadRecordVO> rm = new HashMap<String,ImageUploadRecordVO>();

		for (ImageGroupVO gp : groups){
			ImageUploadRecordVO rdvo=rm.get(gp.getSessionflag());
			if (rdvo == null){
				rdvo = new ImageUploadRecordVO();
				rdvo.setImagegroupid(gp.getSessionflag());
				rdvo.setImgcounts(gp.getImagecounts()==null?"0":gp.getImagecounts().toString());//供旧版本使用
				rdvo.setMemo("摘要: " + ( gp.getMemo() == null ? "无" : gp.getMemo() ));//供旧版本使用
				rdvo.setPaymethod("结算方式: " + ( gp.getSettlemode() == null ? "无" : gp.getSettlemode() ));//结算方式  //供旧版本使用
				String uploadTime = (new SimpleDateFormat("yyyy-MM-dd")).format(gp.getDoperatedate().toDate());//取上传时间
				rdvo.setUploadtime(gp.getTs().toString());
				rdvo.setCount(1);

				//图片状态设置
				int imgState = PhotoState.state2;//未知状态
				if(gp.getIstate() != null){
					imgState = gp.getIstate();
				}
				int normalCount = 0;
				if(imgState == PhotoState.state80){
					normalCount = 0;
				}else{
					normalCount = 1;
				}
				rdvo.setNormalCount(normalCount);
				rdvo.setImgtime(gp.getTs());//最后更新时间
				//概览图片URL
				String sql = "SELECT * FROM YNT_IMAGE_LIBRARY WHERE PK_IMAGE_GROUP =? AND ROWNUM=1 and nvl(dr,0)=0 ORDER BY IMGNAME";
				SQLParameter psp = new SQLParameter();
				psp.addParam(gp.getPk_image_group());
				ImageLibraryVO lib =(ImageLibraryVO)singleObjectBO.executeQuery(sql, psp, new BeanProcessor(ImageLibraryVO.class));
				if(lib!=null){
					rdvo.setReviewpath(lib.getImgpath());
				}
			}else{
				if(gp.getImagecounts() == null){
					gp.setImagecounts(0);
				}
				Integer c = new Integer(rdvo.getImgcounts())+gp.getImagecounts();
				rdvo.setImgcounts(c.toString());
				rdvo.setCount(rdvo.getCount()+1);
				rdvo.setMemo("摘要: 共"+rdvo.getCount()+"组上传摘要");//供旧版本使用
				rdvo.setPaymethod("结算方式: 共"+rdvo.getCount()+"组");//供旧版本使用
				//图片状态设置
				int imgState = PhotoState.state2;//未知状态
				if(gp.getIstate() != null){
					imgState = gp.getIstate();
				}
				int normalCount = rdvo.getNormalCount();
				if(imgState == PhotoState.state80){

				}else{
					normalCount = normalCount + 1;
				}
				rdvo.setNormalCount(normalCount);
			}
			rm.put(gp.getSessionflag(), rdvo);
		}
		Iterator<String> it =rm.keySet().iterator();
		ImageUploadRecordVO[] rds = new ImageUploadRecordVO[rm.keySet().size()];
		int index=0;
		while(it.hasNext()){
			String key = it.next();
			rds[index++]=rm.get(key);
		}

		//数组按照上传时间排序
		java.util.Arrays.sort(rds, new Comparator<ImageUploadRecordVO>() {
			public int compare(ImageUploadRecordVO o1, ImageUploadRecordVO o2) {
				int i = o2.getUploadtime().compareTo(o1.getUploadtime());
				return i;
			}
		});

		return rds;
	}

	public void downloadImage(String filePath, OutputStream out, ImageParamBeanVO[] paramBeans)throws DZFWarpException {
		try{
//			String absolutePath = Common.imageBasePath + filePath;
			if("(null)".equalsIgnoreCase(filePath))
				return ;
			String absolutePath = filePath;
			if(!filePath.contains("ImageUpload")){
				absolutePath=Common.imageBasePath+filePath;
			}

			ImageObject imageObject = ImageObject.decodeFile(absolutePath);
			ImageProcessor processor = ImageHelper.getImageProcesssor(paramBeans);
			if(processor != null)
				processor.ProcessImage(imageObject);
			imageObject.writeToStream(out, 1.0f);
		}catch(Exception e){
			log.error("错误",e);
			throw new WiseRunException(e);
		}
	}

	/**
	 * 查询识别记录
	 */
	public OcrInvoiceVOForApp[] querySbVos(String pk_group) throws DZFWarpException {

		if(StringUtil.isEmpty(pk_group)){
			throw new BusinessException("图片信息为空");
		}

		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select b.* ,a.imgpath as filepath,nvl(a.isinterface,'N') res,nvl(a.iszd,'N') zdres  ");
		qrysql.append(" from  ynt_image_ocrlibrary a ");
		qrysql.append(" left join ynt_interface_invoice b on a.pk_image_ocrlibrary = b.ocr_id  ");
		qrysql.append(" where nvl(a.dr,0) =0 and nvl(b.dr,0)=0   ");
		qrysql.append(" and  a.crelationid in (  ");
		qrysql.append(" select tt.pk_image_library from  ynt_image_library tt  where nvl(tt.dr,0)=0 and tt.pk_image_group = ? )");

		SQLParameter  sp = new SQLParameter();
		sp.addParam(pk_group);

		List<OcrInvoiceVOForApp> ocrlist1 =  (List<OcrInvoiceVOForApp>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(OcrInvoiceVOForApp.class));

		if(ocrlist1==null || ocrlist1.size() == 0){
			throw new BusinessException("识别信息为空!");
		}

		for(OcrInvoiceVOForApp votemp:ocrlist1){

			if(!StringUtil.isEmpty(votemp.getFilepath())){
				votemp.setFilepath(CryptUtil.getInstance().encryptAES(votemp.getFilepath()));
			}
			if(votemp.getIstate()!=null && ("已被识别".equals(votemp.getIstate())
					||"识别正确".equals(votemp.getIstate())) ){
				votemp.setSb_status(0);
			}else if(StringUtil.isEmpty(votemp.getPk_invoice())){
				// res是否的，制单，识别失败
				if(!votemp.getRes().booleanValue() && votemp.getZdres().booleanValue()){
					votemp.setSb_status(1);//识别失败
				}else{
					votemp.setSb_status(2);//识别中
				}
			}else{
				votemp.setSb_status(1);//识别失败
			}
			//增值税普通发票、增值税专用发票、增值税电子普通发票
			if(votemp.getInvoicetype()!=null){
				if(votemp.getInvoicetype().indexOf("增值税普通发票")>=0){
					votemp.setInvoicetype("1");
				}else if(votemp.getInvoicetype().indexOf("增值税专用发票")>=0){
					votemp.setInvoicetype("0");
				}else if(votemp.getInvoicetype().indexOf("增值税电子普通发票")>=0){
					votemp.setInvoicetype("2");
				}
			}
		}

		return ocrlist1.toArray(new OcrInvoiceVOForApp[0]);
	}
	@Override
	public OcrInvoiceVOForApp querySbDetail(String sbid) throws DZFWarpException {
		if (StringUtil.isEmpty(sbid)) {
			OcrInvoiceVOForApp sbvo = new OcrInvoiceVOForApp();
			sbvo.setSb_status(2);//识别中
			return sbvo;
		}

		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql = new StringBuffer();
//		qrysql.append(" select vinvoicecode ,vinvoiceno  ,dinvoicedate ,  ");
//		qrysql.append(" jym , nmny  ,ntaxnmny ,ntotaltax  ");
//		qrysql.append(" ,vpurchname ,vpurchtaxno  ,vsalename ,vsaletaxno   ");
		qrysql.append(" select *  ");
		qrysql.append("  from  ynt_interface_invoice ");
		qrysql.append(" where  pk_invoice = ? ");
		sp.addParam(sbid);

		List<OcrInvoiceVOForApp> lists = (List<OcrInvoiceVOForApp>) singleObjectBO.executeQuery(qrysql.toString(), sp,
				new BeanListProcessor(OcrInvoiceVOForApp.class));

		if (lists == null || lists.size() == 0) {
			throw new BusinessException("识别信息不存在!");
		}


		//增值税普通发票、增值税专用发票、增值税电子普通发票
		if(lists.get(0).getInvoicetype()!=null){
			if(lists.get(0).getInvoicetype().indexOf("增值税普通发票")>=0){
				lists.get(0).setInvoicetype("1");
			}else if(lists.get(0).getInvoicetype().indexOf("增值税专用发票")>=0){
				lists.get(0).setInvoicetype("0");
			}else if(lists.get(0).getInvoicetype().indexOf("增值税电子普通发票")>=0){
				lists.get(0).setInvoicetype("2");
			}
		}

		if(lists.get(0).getIstate()!=null && ("已被识别".equals(lists.get(0).getIstate())
				||"识别正确".equals(lists.get(0).getIstate())) ){
			lists.get(0).setSb_status(0);
		}else{
			lists.get(0).setSb_status(1);//识别失败
		}

		return lists.get(0);
	}






////	public ImageQueryBean[] uploadImages(UserBeanVO uBean,FileItem fileItem) throws BusinessException{
////		ImageQueryBean[] beans = null;
////		try{
////			if(StringUtil.isEmptyWithTrim(uBean.getPk_corp())){
////				throw new BusinessException("传入的pk_corp参数不能为空！");
////			}
////			if(StringUtil.isEmptyWithTrim(uBean.getAccount_id())){
////				throw new BusinessException("传入的uploader参数不能为空！");
////			}
////			if(StringUtil.isEmptyWithTrim(uBean.getCorpname())){
////				throw new BusinessException("传入的uploadlot参数不能为空！");
////			}
////			if(fileItem == null){
////				throw new BusinessException("传入的fileItem参数不能为空！");
////			}
////
////			String uploadlot = uBean.getCorpname().toLowerCase();
////
////			if(uBean.getCert() != null && DZFBoolean.valueOf(uBean.getCert()).booleanValue()){
////
////				File file = SaveZipFile(fileItem, uploadlot,uBean.getCert(),uBean.getAccount());
////
////				//String filePath = "zipfile/cert/"+uBean.getAccount()+"-"+  forceFileExt(uploadlot, ".zip");//zpm修改
////				String filePath = file.getPath();
////				CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, uBean.getPk_corp());
////				String fathercorp = corpvo.getFathercorp();
////				ImageGroupVO groupVO = createGroupVO(fathercorp, filePath, uBean.getAccount_id(), uBean.getAccount());
////				if(groupVO != null){
////					groupVO.setCert(DZFBoolean.valueOf(uBean.getCert()));
////					groupVO.setCerttx(uBean.getCerttx());
////					singleObjectBO.saveObject(groupVO.getPk_corp(), groupVO);
////				}
////				//
////				ImageQueryBean bean = new ImageQueryBean();
////				bean.setGroupKey(groupVO.getPrimaryKey());
////				bean.setGroupCode(groupVO.getGroupcode());
////				bean.setCreatedBy(uBean.getAccount());
////				bean.setCreatedOn(groupVO.getDoperatedate().toString());
////				bean.setImagePath(filePath);
////				bean.setPk_corp(uBean.getPk_corp());
////				//
////				return new ImageQueryBean[]{bean};
////			}
////
////			// 根据上传批次查找之前的记录，并删除
////			ArrayList<String> delfiles = delImageLibs(findVOBySessionFlag(uBean.getPk_corp(), uploadlot,uBean.getCert()));
////			// 保存压缩包到imageBasePath中，以上传批次作为zip文件名
////			File imgzipFile = SaveZipFile(fileItem, uploadlot,uBean.getCert(),uBean.getAccount());
////			delImgFiles(delfiles);
////			// 解压缩文件
////			HashMap<String, String> groupSettle = new HashMap<String, String>();
////			HashMap<String, ArrayList<String>> groupfileMap = unzipFile(imgzipFile, uBean.getPk_corp(), DZFBoolean.valueOf(uBean.getBdata()).booleanValue(),groupSettle,uBean.getCert());
////			ImageGroupVO[] aggVOs = buildGroupRecords(groupfileMap, uBean.getPk_corp(), uploadlot, uBean.getAccount_id(),groupSettle,uBean.getCert(),uBean.getCerttx());
////			//
////			ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
////			beans = ct.fromVO(aggVOs);
////			//beans = ImageQueryBean.fromVO(aggVOs);
////
////		}catch(Exception e){
////			throw new BusinessException(e.getMessage());
////		}
////		return beans;
////	}
//
//
//
//	/**
//	 * 业务合作，保存证照
//	 * */
//	public ImageQueryBean[] uploadCorpeateInfo(UserBeanVO uBean,File[] files,String[] filenames){
//
//		File file = SaveZipFile(files[0], null,filenames[0],"",uBean.getCert(),uBean.getAccount(),"","");
//
//		String filePath = file.getPath();
//		String fathercorp;
//		if(uBean.getPk_corp().equalsIgnoreCase(Common.tempidcreate)){
//			fathercorp=Common.tempidcreate;
//		}else{
//			CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, uBean.getPk_corp());
//			fathercorp = corpvo.getFathercorp();
//		}
//		ImageGroupVO groupVO = createGroupVO(null,null,fathercorp, filePath, uBean.getAccount_id(), uBean.getAccount());
//		if(groupVO != null){
//			groupVO.setCert(DZFBoolean.valueOf(uBean.getCert()));
//			groupVO.setCerttx(uBean.getCerttx());
//			groupVO.setCertbusitype(uBean.getCertbusitype());//新业务
//			groupVO.setCertctnum(uBean.getCertctnum());//联系方式
//			groupVO.setCertmsg(uBean.getCertmsg());//留言
//			groupVO.setSessionflag(filenames[0]+".zip");
//			singleObjectBO.saveObject(groupVO.getPk_corp(), groupVO);
//		}
//		ImageQueryBean bean = new ImageQueryBean();
//		bean.setGroupKey(groupVO.getPrimaryKey());
//		bean.setGroupCode(groupVO.getGroupcode());
//		bean.setCreatedBy(uBean.getAccount());
//		bean.setCreatedOn(groupVO.getDoperatedate().toString());
//		bean.setImagePath(filePath);
//		bean.setPk_corp(uBean.getPk_corp());
//
//		uBean.setGroupkey(groupVO.getPrimaryKey());
//		return new ImageQueryBean[]{bean};
//
//	}
//
//
////	public ResponseBaseBeanVO processcollabtevalt(UserBeanVO userBean) {
////		CollabtevaltVO vo = new CollabtevaltVO();
////		vo.setBusitype(userBean.getBusitype());//
////		vo.setDes(userBean.getDes());
////		if(userBean.getItems() != null)
////			vo.setItem(userBean.getItems().toString());
////		vo.setMessage(userBean.getMessage());
////
////		vo.setPk_user(userBean.getAccount_id());
////		vo.setPk_corp(userBean.getPk_corp());
////		vo.setBusidate(new DZFDate());
////		ResponseBaseBeanVO bean = new ResponseBaseBeanVO() ;
////		try {
////			/**
////			 * 会计公司主键
////			 */
////			CorpVO cvo=CorpCache.getInstance().get(null, userBean.getPk_corp());
////			Object pk_parent =cvo.getFathercorp();//
////			if(pk_parent != null)
////				vo.setPk_parent(pk_parent.toString());
////
////			singleObjectBO.saveObject(cvo.getPk_corp(),vo);
////
////			bean.setRescode(IConstant.DEFAULT) ;
////			bean.setResmsg("提交成功") ;
////		} catch (Exception e) {
////			Logger.error(this,"操作异常："+e.getMessage(),e);
////			bean.setRescode(IConstant.DEFAULT) ;
////			bean.setResmsg("操作异常："+e.getMessage()) ;
////		}
////		//new CommonServ().writeJsonByFilter(request, response, bean, null,null);
////		return bean;
////	}
//
//
//








//	/**
//	 * 获取图片组号，废弃不用
//	 * @param pk_corp
//	 * @param currDate
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private String getGroupCode(String pk_corp,  DZFDate currDate) throws DZFWarpException {
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//		String prefix =  sdf.format(currDate.toDate());
//		String key = pk_corp + prefix;
//		int flow = 0;
//		if(!groupcodeTable.containsKey(key)){
//			String sql = String.format("select max(groupcode) groupcode from ynt_image_group where groupcode like '%s%%' and nvl(dr,0)=0 and pk_corp='%s'", prefix, pk_corp);
//			Object[] result = (Object[]) singleObjectBO.executeQuery(sql, new SQLParameter(),new ArrayProcessor());
//			String maxcode = (result != null && result.length>0 && result[0] != null?result[0].toString(): "");
//			if(!maxcode.equals(""))
//				flow = Integer.parseInt(maxcode.substring(prefix.length()));
//			groupcodeTable.put(key, flow + 1);
//		} else {
//			flow = groupcodeTable.get(key) + 1;
//			groupcodeTable.put(key, flow);
//		}
//		// 图片组号由日期+4位流水组成
//		return String.format("%s%04d", prefix, groupcodeTable.get(key));
//	}
//


//	public ClientFileSerializable downloadImageByPc(String filePath, ImageParamBeanVO[] paramBeans) throws DZFWarpException{
//		ClientFileSerializable cs=null;
//		try{
//			String absolutePath =Common.imageBasePath + filePath;//.replace('\\', '/');//zpm修改增加
//			ImageObject imageObject = null;
//
//			imageObject = ImageObject.decodeFile(absolutePath);
//			ImageProcessor processor = ImageHelper.getImageProcesssor(paramBeans);
//			if(processor != null)
//				processor.ProcessImage(imageObject);
//			cs=new ClientFileSerializable();
//			cs.setFile(imageObject.getImage());
////				String tempdir = System.getProperty("java.io.tmpdir");  // 系统临时目录
////				fs.setFile(new File("C:\\Users\\temp" + File.separator +"xx.zip"));
////			imageObject.writeToStream(output, 1.0f);
//		}catch(Exception e){
//			log.error("图片下载失败！",e);
////			throw new WiseRunException(e);
//			throw new BusinessException("图片下载失败！图片不存在或者已被损坏！");
//		}
//		return cs;
//	}
//
//
//	public void deleteImage(String pk_corp,String[] imageKeys) throws DZFWarpException {
//		try{
//			if(imageKeys == null || imageKeys.length == 0)
//				return;
//			ArrayList<String> delfiles = new ArrayList<String>();
//			// 删除图片库和图片组表的记录
//			String where = String.format("nvl(dr,0)=0 and (%s)", yntBoPubUtil.getSqlStrByArrays(imageKeys, 1000, "pk_image_library"));
//			ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, where,new SQLParameter());
//			if(imageVOs != null && imageVOs.length > 0){
//				ArrayList<String> imgGroupPks = new ArrayList<String>();
//				ArrayList<ImageGroupVO> groupVOs = new ArrayList<ImageGroupVO>();
//				for(ImageLibraryVO imageVO: imageVOs){
//					delfiles.add(imageVO.getImgpath());
//					// 校验是否被档案引用
//					if (getRefCheck().isReferenced(imageVO.getTableName(), imageVO.getPrimaryKey())) {
//						throw new BusinessException("已经被引用 ，不能删除！");
//					}
//
//					if(!imgGroupPks.contains(imageVO.getPk_image_group())){
//						imgGroupPks.add(imageVO.getPk_image_group());
//
//						ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, imageVO.getPk_image_group());
//
//						//校验是否是手机上传的图片
//						if(groupVO != null){
//							Integer sourcemode = groupVO.getSourcemode();
//							if(sourcemode != null && groupVO.getSourcemode() == PhotoState.SOURCEMODE_01){
//								String warnstr = String.format("图片%s是手机端上传的图片，不能删除！", imageVO.getImgname());
//								throw new BusinessException(warnstr);
//							}
//							//校验
//							Integer istate = groupVO.getIstate();
//							if(istate != null && istate == PhotoState.state8){
//								throw new BusinessException("图片已被引用，不能删除！");
//							}
//
//							groupVOs.add(groupVO);
//						}
//					}
//				}
//				singleObjectBO.deleteVOArray(imageVOs);
//				// 判断图片组是否还有相关的图片文件，如果没有也要删除
//				for(ImageGroupVO groupVO: groupVOs){
//					where = String.format("nvl(dr,0)=0 and pk_image_group='%s'", groupVO.getPrimaryKey());
//					ImageLibraryVO[] childVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, where,new SQLParameter());
//					if(childVOs == null || childVOs.length == 0){
//						// 校验是否被档案引用
//						if (getRefCheck().isReferenced(groupVO.getTableName(), groupVO.getPrimaryKey())) {
//							throw new BusinessException("已经被引用 ，不能删除！");
//						}
//
//						singleObjectBO.deleteObject(groupVO);
//					}
//				}
//			}
//			// 删除图片文件
//			delImgFiles(delfiles);
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//
//		}
//	}
//	public void rotateImage(String filePath, int degree)throws DZFWarpException{
//		try {
//			String absolutePath = Common.imageBasePath+ filePath;
//			ImageObject imageObject = ImageObject.decodeFile(absolutePath);
//			new ImageRotateProcessor(degree).ProcessImage(imageObject);
//			imageObject.writeToFile(new File(absolutePath), 1.0f);
//		}catch(Exception e){
//			throw new WiseRunException(e);
//		}
//	}
//	public void saveClipedImage(String pk_image_group, DZFDateTime ts,
//			ImageMetaVO[] metaVOs)throws DZFWarpException{
//		try{
//			// 校验图片组是否合法
//			ImageGroupVO groupVO = checkImageGroupValid(pk_image_group);
//
//			//zpm注销，新版ts ，已经不用
//			//if(!groupVO.getTs().equals(ts))
//				//throw new BusinessException(String.format("图片组 %s 已经被他人修改，请刷新后重试！", groupVO.getGroupcode()));
//
//			// 校验传入的metaVO是同一个图片的
//			if(metaVOs != null && metaVOs.length > 0){
//				String pk_image_library = "";
//				for(ImageMetaVO metaVO: metaVOs){
//					if(pk_image_library.equals("")){
//						pk_image_library=metaVO.getPk_image_library();
//					} else if(!pk_image_library.equals(metaVO.getPk_image_library())){
//						throw new BusinessException("不允许传入的图元数据数组属于不同的图片！");
//					}
//				}
//			}
//			// 对于之前设置为切图状态的图片，先设置为非切图状态
//			ImageLibraryVO[] clipImageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, "nvl(dr,0)=0 and nvl(iscliped,'N')='Y' and pk_image_group='" + pk_image_group + "'",new SQLParameter());
//			if(clipImageVOs != null && clipImageVOs.length > 0){
//				for(ImageLibraryVO clipImageVO: clipImageVOs){
//					clipImageVO.setIscliped(new DZFBoolean(false));
//					clipImageVO.setClipedby(null);
//					clipImageVO.setClipedon(null);
//					singleObjectBO.update(clipImageVO);
//				}
//			}
//
//			// 需要把本图片组其他图片的切图数据删除
//			ImageMetaVO[] oldMetaVOs = (ImageMetaVO[]) singleObjectBO.queryByCondition(ImageMetaVO.class, "nvl(dr,0)=0 and pk_image_library in (select pk_image_library from ynt_image_library where pk_image_group='" + groupVO.getPrimaryKey() + "')",new SQLParameter());
//			if(oldMetaVOs != null && oldMetaVOs.length > 0){
//				for(ImageMetaVO oldMetaVO: oldMetaVOs){
//					// 查找新的VO中是否还存在，不存在则删除
//					boolean isfinded = false;
//
//					if(metaVOs != null && metaVOs.length > 0){
//						for(ImageMetaVO metaVO: metaVOs){
//							if(StringUtil.isEmptyWithTrim(metaVO.getPrimaryKey())) continue;
//							if(metaVO.getPrimaryKey().equals(oldMetaVO.getPrimaryKey())){
//								isfinded = true;
//								break;
//							}
//						}
//					}
//
//					if(!isfinded) {
//						// 校验是否被档案引用
//						if (getRefCheck().isReferenced(oldMetaVO.getTableName(), oldMetaVO.getPrimaryKey())) {
//							throw new BusinessException("已经被引用，不能删除！");
//						}
//
//						singleObjectBO.deleteObject(oldMetaVO);
//					}
//				}
//			}
//
//			//备份图片组信息
//			Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
//			imageGroupMap.put(groupVO.getPrimaryKey(), groupVO);//切图
//			saveImageGroupBackUp(imageGroupMap, PhotoState.state20);
//
//			if(metaVOs != null && metaVOs.length > 0){
//				for(ImageMetaVO metaVO: metaVOs){
//					if(StringUtil.isEmptyWithTrim(metaVO.getPrimaryKey()))
//						singleObjectBO.insertVOArr(metaVO.getPk_corp(), new ImageMetaVO[]{metaVO});
//					else {
//						singleObjectBO.update(metaVO);
//					}
//				}
//				ImageLibraryVO imageVO = (ImageLibraryVO) singleObjectBO.queryByPrimaryKey(ImageLibraryVO.class, metaVOs[0].getPk_image_library());
//				// 更新图片的时间戳，防止并发问题
//				imageVO.setIscliped(new DZFBoolean(true));
//				imageVO.setClipedby(metaVOs[0].getCoperatorid());
//				imageVO.setClipedon(new DZFDateTime(new Date()));
//				singleObjectBO.update(imageVO);
//
//				groupVO.setIscliped(new DZFBoolean(true));
//				groupVO.setClipedby(metaVOs[0].getCoperatorid());
//				groupVO.setClipedon(new DZFDateTime(new Date()));
//				groupVO.setIsskiped(new DZFBoolean(false));
//				groupVO.setSkipedby(null);
//				groupVO.setSkipedon(null);
//				groupVO.setIsuer(DZFBoolean.TRUE);
//				groupVO.setIstate(PhotoState.state20);//已切图
//				singleObjectBO.update(groupVO);
//			} else {
//				groupVO.setIsuer(DZFBoolean.FALSE);
//				groupVO.setIscliped(new DZFBoolean(false));
//				groupVO.setClipedby(null);
//				groupVO.setClipedon(null);
//				singleObjectBO.update(groupVO);
//			}
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//	}
//
//	public void skipClipedImage(String pk_image_group, DZFDateTime ts,
//			String skipedBy, DZFDateTime skipedOn) throws DZFWarpException{
//
//		ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
//		if(groupVO == null)
//			throw new BusinessException(String.format("图片组ID %s 对应的图片组记录不存在，请确认是否已删除！", pk_image_group));
//
////		if(!groupVO.getTs().equals(ts))
////			throw new BusinessException(String.format("图片组 %s 已经被他人修改，请刷新后重试！", groupVO.getGroupcode()));
//
//		innerSkipClipedImage(groupVO, skipedBy, skipedOn);
//
//	}
//
//
//
//	private void innerSkipClipedImage(ImageGroupVO groupVO, String skipedBy, DZFDateTime skipedOn) throws DZFWarpException{
//		if(groupVO == null) return;
//		// 更新图片的时间戳，防止并发问题
//		groupVO.setIsskiped(new DZFBoolean(true));
//		groupVO.setSkipedby(skipedBy);
//		groupVO.setSkipedon(skipedOn);
////		groupVO.setDr(1);   // 置删除状态
//		singleObjectBO.update(groupVO);
//		TzpzHVO[] hvos = queryVoucherByImageGroup(groupVO);
//		//查询临时凭证状态是否发生变化
//		if(hvos!=null && hvos.length>0){
//			for(TzpzHVO hvo : hvos){
//				if(hvo.getVbillstatus() != IVoucherConstants.TEMPORARY){
//					throw new BusinessException("当前凭证已发生变化，请重新查询后操作！");
//				}
//			}
//		}
//		// 删除凭证信息
//		deleteBD(hvos);
//		// 删除预凭证信息
//		deleteBD(queryPreVoucherByImageGroup(groupVO));
//		// 删除识图信息
//		deleteBD(queryIdentByImageGroup(groupVO));
//		// 删除切图信息
//		deleteBD(queryClipingByImageGroup(groupVO));
//		// 删除图片信息
////		deleteBD(queryImageByImageGroup(groupVO));
//	}
//
//	/**
//	 * 根据图片组pk获取所有该图片组对应的凭证记录
//	 * @param groupVO
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private TzpzHVO[] queryVoucherByImageGroup(ImageGroupVO groupVO) throws DZFWarpException{
//		if(groupVO == null) return null;
//		SQLParameter param = new SQLParameter();
//		param.addParam(groupVO.getPrimaryKey());
//		List<TzpzHVO> headVOs = (List<TzpzHVO>) singleObjectBO.executeQuery("nvl(dr,0)=0 and pk_image_group= ? ",
//				param, new Class[]{TzpzHVO.class, TzpzBVO.class});
//
//		if(headVOs == null || headVOs.size() == 0) return null;
//
////		TzpzHVO[] headVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, "nvl(dr,0)=0 and pk_image_group='" + groupVO.getPrimaryKey() + "'",new SQLParameter());
////		if(headVOs == null || headVOs.length == 0) return null;
////
//////		HYBillVO[] billVOs = new HYBillVO[headVOs.length];
////		for(int i = 0; i<headVOs.length; i++){
//////			billVOs[i] = new HYBillVO();
//////			billVOs[i].setParentVO(headVOs[i]);
////			TzpzBVO[] bodyVOs = (TzpzBVO[]) singleObjectBO.queryByCondition(TzpzBVO.class, "nvl(dr,0)=0 and pk_tzpz_h='" + headVOs[i].getPrimaryKey() + "'",new SQLParameter());
////			headVOs[i].setChildren(bodyVOs);
////		}
//		return headVOs.toArray(new TzpzHVO[0]);
//	}
//
//	/**
//	 * 根据图片组pk获取所有该图片组对应的预凭证记录
//	 * @param groupVO
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private IMageVoucherVO[] queryPreVoucherByImageGroup(ImageGroupVO groupVO) throws DZFWarpException{
//		if(groupVO == null) return null;
//
//		StringBuffer sf = new StringBuffer();
//		sf.append("nvl(dr,0)=0 and pk_image_library in (select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group= ? )");
//
//		SQLParameter param = new SQLParameter();
//		param.addParam(groupVO.getPrimaryKey());
//
//		List<IMageVoucherVO> headVOs = (List<IMageVoucherVO>) singleObjectBO.executeQuery(sf.toString(),
//				param, new Class[]{IMageVoucherVO.class, IMageVoucherBVO.class});
//
//		if(headVOs == null || headVOs.size() == 0) return null;
//
////		String where = String.format("nvl(dr,0)=0 and pk_image_library in (select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group='%s')", groupVO.getPrimaryKey());
////		IMageVoucherVO[] headVOs = (IMageVoucherVO[]) singleObjectBO.queryByCondition(IMageVoucherVO.class, where,new SQLParameter());
////		if(headVOs == null || headVOs.length == 0) return null;
////
//////		HYBillVO[] billVOs = new HYBillVO[headVOs.length];
////		for(int i = 0; i<headVOs.length; i++){
//////			billVOs[i] = new HYBillVO();
//////			billVOs[i].setParentVO(headVOs[i]);
////			IMageVoucherBVO[] bodyVOs = (IMageVoucherBVO[]) singleObjectBO.queryByCondition(IMageVoucherBVO.class, "nvl(dr,0)=0 and pk_image_voucher='" + headVOs[i].getPrimaryKey() + "'",new SQLParameter());
//////			billVOs[i].setChildrenVO(bodyVOs);
////			headVOs[i].setChildren(bodyVOs);
////		}
//		return headVOs.toArray(new IMageVoucherVO[0]);
//	}
//
//	/**
//	 * 根据图片组pk获取所有该图片组对应的识图记录
//	 * @param groupVO
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private ImageIdentVO[] queryIdentByImageGroup(ImageGroupVO groupVO) throws DZFWarpException{
//		if(groupVO == null) return null;
//		String where = String.format("nvl(dr,0)=0 and pk_image_meta in (select a.pk_image_meta from ynt_image_meta a inner join  ynt_image_library b on a.pk_image_library=b.pk_image_library where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.pk_image_group='%s')", groupVO.getPrimaryKey());
//		ImageIdentVO[] identVOs = (ImageIdentVO[])singleObjectBO.queryByCondition(ImageIdentVO.class, where,new SQLParameter());
////		if(identVOs == null || identVOs.length == 0) return null;
////		HYBillVO[] billVOs = new HYBillVO[identVOs.length];
////		for(int i = 0; i<identVOs.length; i++){
////			billVOs[i] = new HYBillVO();
////			billVOs[i].setParentVO(identVOs[i]);
////		}
//		return identVOs;
//	}
//
//	/**
//	 * 根据图片组pk获取所有该图片组对应的切图记录
//	 * @param groupVO
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private ImageMetaVO[] queryClipingByImageGroup(ImageGroupVO groupVO) throws DZFWarpException{
//		if(groupVO == null) return null;
//		String where = String.format("nvl(dr,0)=0 and pk_image_library in (select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group='%s')", groupVO.getPrimaryKey());
//		ImageMetaVO[] metaVOs = (ImageMetaVO[])singleObjectBO.queryByCondition(ImageMetaVO.class, where,new SQLParameter());
////		if(metaVOs == null || metaVOs.length == 0) return null;
////		HYBillVO[] billVOs = new HYBillVO[metaVOs.length];
////		for(int i = 0; i<metaVOs.length; i++){
////			billVOs[i] = new HYBillVO();
////			billVOs[i].setParentVO(metaVOs[i]);
////		}
//		return metaVOs;
//	}
//
//	/**
//	 * 根据图片组pk获取所有该图片组对应的切图记录
//	 * @param groupVO
//	 * @return
//	 * @throws BusinessException
//	 */
//	private ImageLibraryVO[] queryImageByImageGroup(ImageGroupVO groupVO) throws BusinessException{
//		if(groupVO == null) return null;
//		ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, "nvl(dr,0)=0 and pk_image_group='" + groupVO.getPrimaryKey() + "'",new SQLParameter());
////		if(imageVOs == null || imageVOs.length == 0) return null;
////		HYBillVO[] billVOs = new HYBillVO[imageVOs.length];
////		for(int i = 0; i<imageVOs.length; i++){
////			billVOs[i] = new HYBillVO();
////			billVOs[i].setParentVO(imageVOs[i]);
////		}
//		return imageVOs;
//	}
//
//	private void deleteBD(SuperVO[] billVOs) throws DZFWarpException{
//		if(billVOs == null || billVOs.length == 0) return;
//		for(int i=0; i<billVOs.length; i++){
//			billVOs[i].setAttributeValue("dr", 1);//zpm
//			//存在子表，子表行dr置为删除态
//			SuperVO[] childrenVOs = billVOs[i].getChildren();
//			if(childrenVOs != null && childrenVOs.length != 0){
//				for(SuperVO child : childrenVOs){
//					child.setAttributeValue("dr", 1);//删除态
//				}
//				//singleObjectBO目前不支持主子表一同更新，所以采用此方法，后续singleObjectBO支持主子表更新，该代码可删除
//				singleObjectBO.updateAry(childrenVOs);
//			}
//			singleObjectBO.update(billVOs[i]);//只是把dr置为删除态，而delete是真正删除记录
//			//singleObjectBO.deleteObject(billVOs[i]);   // deleteBD
//		}
//	}
//
//	public ClipingImageBean queryNewClipingImages(String pk_corp,int count, ArrayList<String> excludeImageGroupKeys)throws DZFWarpException{
//		ClipingImageBean bean = null;
//		try{
//			bean = new ClipingImageBean();
//			bean.setTotalCount(poimp_manage.newClipingCount(pk_corp,excludeImageGroupKeys));
//			bean.setClipingVOs(poimp_manage.dispatchClipingImages(pk_corp,count, excludeImageGroupKeys));
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//		return bean;
//	}
//
//	public void releaseNewClipingImages(ImageGroupVO[] imageGroupVOs) throws DZFWarpException{
//		try{
//			poimp_manage.releaseDispatchedImages(imageGroupVOs);
////			ClipingImageManager.getInstance().releaseDispatchedImages(imageGroupVOs);
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//	}
//	public IndentImageBean queryNewIndentImages(String pk_corp,String user, int count, ArrayList<String> excludeMetaKeys) throws DZFWarpException{
//		IndentImageBean bean = null;
//		try{
//			bean = new IndentImageBean();
//			bean.setTotalCount(poimp_manage.getNewIndentCount(pk_corp,user, excludeMetaKeys));
////			IndentMetaManager.getInstance()
//			bean.setMetaVOs(poimp_manage.dispatchIndentMetas(pk_corp,user, count, excludeMetaKeys));
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//		return bean;
//	}
//	public void releaseNewIndentImages(ImageMetaVO[] metaVOs) throws DZFWarpException{
//		try{
////			IPhotoManage manage = NCLocator.getInstance().lookup(IPhotoManage.class);
//			poimp_manage.releaseDispatchedMetas(metaVOs);
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//	}
//
//	public void SaveIndentInfo(String pk_image_meta, DZFDateTime ts, String indentValue, String indentBy,
//			DZFDateTime indentOn) throws DZFWarpException{
//		try{
//			if(StringUtil.isEmptyWithTrim(pk_image_meta)) return;
//			// 重新从数据库再查一遍，保证数据是最新的
//			ImageMetaVO metaVO = (ImageMetaVO) singleObjectBO.queryByPrimaryKey(ImageMetaVO.class, pk_image_meta);
//			if(metaVO == null){
//				throw new BusinessException(String.format("图元信息ID %s 对应的图元信息记录不存在，请确认是否已删除！", pk_image_meta));
//			}
//
//			//if(!metaVO.getTs().equals(ts)) //////////////zpm注销，新版走新的版本校验
//				//throw new BusinessException(String.format("图片%s的切图图元%s 已经被他人修改，请刷新后重试！", metaVO.getImgname(), metaVO.getMetacode()));
//
//			if(metaVO.getIsindent() != null && metaVO.getIsindent().booleanValue()){
//				throw new BusinessException(String.format("图片%s的切图图元%s已经成功识图或者识图次数超过了3次，不允许重复识图！", metaVO.getImgname(), metaVO.getMetacode()));
//			}
//
//			ImageLibraryVO imageVO = (ImageLibraryVO) singleObjectBO.queryByPrimaryKey(ImageLibraryVO.class, metaVO.getPk_image_library());
//			// 校验图片组是否合法
//			ImageGroupVO imageGroupVO = checkImageGroupValid(imageVO.getPk_image_group());
//
//			//更新及备份图片组信息
//			Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
//			imageGroupMap.put(imageGroupVO.getPrimaryKey(), imageGroupVO);
//			saveImageGroupBackUp(imageGroupMap, PhotoState.state30);//已识图
//
//			ImageIdentVO identVO = queryIdentVO(metaVO.getPrimaryKey());
//			if(identVO == null){
//				identVO = new ImageIdentVO();
//				identVO.setPk_corp(metaVO.getPk_corp());
//				identVO.setPk_image_meta(metaVO.getPrimaryKey());
//				identVO.setCoperatorid(indentBy);
//				identVO.setDoperatedate(new DZFDate());////---------------------------???
//				identVO.setIdentdate1(indentOn);
//				identVO.setIdenterid1(indentBy);
//				identVO.setIdentvalue1(indentValue);
//
//				singleObjectBO.insertVOArr(metaVO.getPk_corp(), new ImageIdentVO[]{identVO});
//			} else {
//				if(StringUtil.isEmptyWithTrim(identVO.getIdenterid1()) || identVO.getIdenterid1().equals(indentBy)){
//					identVO.setIdentdate1(indentOn);
//					identVO.setIdenterid1(indentBy);
//					identVO.setIdentvalue1(indentValue);
//				} else if(StringUtil.isEmptyWithTrim(identVO.getIdenterid2()) || identVO.getIdenterid2().equals(indentBy)){
//					identVO.setIdentdate2(indentOn);
//					identVO.setIdenterid2(indentBy);
//					identVO.setIdentvalue2(indentValue);
//				} else if(StringUtil.isEmptyWithTrim(identVO.getIdenterid3()) || identVO.getIdenterid3().equals(indentBy)){
//					identVO.setIdentdate3(indentOn);
//					identVO.setIdenterid3(indentBy);
//					identVO.setIdentvalue3(indentValue);
//				} else {
//					throw new BusinessException(String.format("图片%s的切图图元%s识别次数超过3次异常", metaVO.getImgname(), metaVO.getMetacode()));
//				}
//
//				if(identValueEqual(identVO)){  // 成功识图
//					identVO.setIdentvalue(indentValue);
//					metaVO.setIsindent(new DZFBoolean(true));
//					singleObjectBO.update(metaVO);
//				} else if(identCountOverLimit(identVO)){    // 识别次数等于3次
//					metaVO.setIsindent(new DZFBoolean(true));
//					singleObjectBO.update(metaVO);
//				}
//
//				singleObjectBO.update(identVO);
//			}
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//	}
//
//	/**
//	 * 判断识别次数是否超过限制
//	 * @param identVO
//	 * @return
//	 */
//	private boolean identCountOverLimit(ImageIdentVO identVO){
//		String identerid1 = identVO.getIdenterid1();
//		String identerid2 = identVO.getIdenterid2();
//		String identerid3 = identVO.getIdenterid3();
//		return !StringUtil.isEmptyWithTrim(identerid1) &&
//				!StringUtil.isEmptyWithTrim(identerid2) &&
//				!StringUtil.isEmptyWithTrim(identerid3);
//	}
//
//	/**
//	 * 判断识别值是否相等
//	 * @param identVO
//	 * @return
//	 */
//	private boolean identValueEqual(ImageIdentVO identVO){
//		String identValue1 = identVO.getIdentvalue1();
//		String identValue2 = identVO.getIdentvalue2();
//		String identValue3 = identVO.getIdentvalue3();
//		if(identValue1 == null)
//			identValue1 = "";
//		if(identValue2 == null)
//			identValue2 = "";
//		if(identValue3 == null)
//			identValue3 = "";
//
//		if((identValue1.equals("") && identValue2.equals(""))) return false;
//		if((identValue1.equals("") && identValue3.equals(""))) return false;
//		if((identValue2.equals("") && identValue3.equals(""))) return false;
//
//		if(identValue1.equals(identValue2)) return true;
//		if(identValue1.equals(identValue3)) return true;
//		if(identValue2.equals(identValue3)) return true;
//
//		return false;
//	}
//
//	private ImageIdentVO queryIdentVO(String pk_image_meta) throws DZFWarpException{
//		String where = "nvl(dr,0)=0 and pk_image_meta='" + pk_image_meta + "'";
//		ImageIdentVO[] identVOs = (ImageIdentVO[]) singleObjectBO.queryByCondition(ImageIdentVO.class, where,new SQLParameter());
//		return identVOs == null || identVOs.length == 0? null:identVOs[0];
//	}
//
//	public IMageVoucherVO[] batchCreatePreVoucher(ImageLibraryVO[] imageVOs,String user, DZFDate date) throws DZFWarpException{
//		IMageVoucherVO[] resultBillVOs = null;
//		try{
//			if(imageVOs == null || imageVOs.length == 0) return resultBillVOs;
//
//			resultBillVOs = new IMageVoucherVO[imageVOs.length];
//			int index = 0;
//			IMageVoucherVO headVO = null;
//			IMageVoucherBVO[] bodyVOs = null;
//			for(ImageLibraryVO imageVO: imageVOs){
//				// 根据图片VO创建预凭证表头
//				headVO = createPreVoucherHeadVO(imageVO, user);
////				billVO.setParentVO(headVO);
//				// 根据图片VO创建预凭证表体
//				bodyVOs = createPreVoucherBodyVOs(imageVO, user, date);
////				billVO.setChildrenVO(bodyVOs);
//				headVO.setChildren(bodyVOs);
//
//				if(bodyVOs != null && bodyVOs.length > 0){
//					double creditmny = 0, debitmny=0;
//					for(IMageVoucherBVO bodyVO: bodyVOs){
//						creditmny += bodyVO.getCreditmny() == null?0:bodyVO.getCreditmny().doubleValue();
//						debitmny += bodyVO.getDebitmny() == null?0:bodyVO.getDebitmny().doubleValue();
//					}
//					headVO.setCreditmny(new DZFDouble(creditmny));
//					headVO.setDebitmny(new DZFDouble(debitmny));
//				}
//
//				resultBillVOs[index++] = (IMageVoucherVO) singleObjectBO.saveObject(headVO.getPk_corp(), headVO);
//			}
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//		return resultBillVOs;
//	}
//
//	/**
//	 * 根据图片VO，创建预凭证行VO
//	 * @param imageVO
//	 * @param user
//	 * @param date
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private IMageVoucherBVO[] createPreVoucherBodyVOs(ImageLibraryVO imageVO, String user, DZFDate date) throws DZFWarpException{
//		// 查找识图信息
//		List<ImageIdentVO> identVOs = queryImageIdentVOs(imageVO.getPrimaryKey());
//		if(identVOs == null || identVOs.size() == 0) return null;
//
//		String zy = "";
//		//本币
//		DZFDouble mny = DZFDouble.ZERO_DBL;
//		DZFDouble ybmny = DZFDouble.ZERO_DBL;
//		DZFDouble bbmny = DZFDouble.ZERO_DBL;
//
//		String pk_currency = null;
//		DZFDouble exrate = DZFDouble.ZERO_DBL;
//		String currcode = null;
//		HashMap<String, BdCurrencyVO> map = queryCurrencyInfo();
//		for(ImageIdentVO identVO: identVOs){
//			if(identVO.getClipkind().intValue() == 0){  // 金额
//				if(identVO.getIdentvalue() == null){
//					bbmny = DZFDouble.ZERO_DBL;
//					pk_currency = yntBoPubUtil.getCNYPk();
//					exrate = new DZFDouble(1);
//				}else{
//					String[] strs = identVO.getIdentvalue().split("_");
//					mny = new DZFDouble(safeAsDouble(strs[0]));
//					bbmny = new DZFDouble(safeAsDouble(strs[0]));
//					if(strs != null && strs.length == 1){
//						currcode = "CNY";
//					}else if(strs != null && strs.length == 2){
//						currcode = strs[1];
//					}
//					ExrateVO vo = queryRate(currcode , imageVO.getPk_corp());
//
//					if(vo != null){
//						ybmny = mny;
//						if(vo.getConvmode() == 0){
//							bbmny = ybmny.multiply(vo.getExrate());
//						}else{
//							bbmny = ybmny.div(vo.getExrate());
//						}
//						pk_currency = vo.getPk_currency();
//						exrate = vo.getExrate();
//					}else{
//						bbmny = mny;
//						pk_currency = map.get(currcode)==null?null: map.get(currcode).getPk_currency();
//						exrate = new DZFDouble(1);
//					}
//				}
////				mny = new DZFDouble(safeAsDouble(identVO.getIdentvalue()));
////				bbmny = new DZFDouble(safeAsDouble(identVO.getIdentvalue()));
//			} else if(identVO.getClipkind().intValue() == 1) {  // 摘要
//				zy = identVO.getIdentvalue();
//			}
//		}
//
//		if(StringUtil.isEmptyWithTrim(zy) && mny.doubleValue() == 0) return null;
//
//		IMageVoucherBVO[] bodyVOs = new IMageVoucherBVO[2];
//		// 借方行
//		bodyVOs[0] = new IMageVoucherBVO();
//		bodyVOs[0].setCoperatorid(user);
//		bodyVOs[0].setDoperatedate(date);
//		bodyVOs[0].setAbstracts(zy);
//		bodyVOs[0].setDebitmny(bbmny);
//		bodyVOs[0].setYbdebitmny(ybmny);
//		bodyVOs[0].setPk_currency(pk_currency);
//		bodyVOs[0].setNrate(exrate);
//		// 贷方行
//		bodyVOs[1] = new IMageVoucherBVO();
//		bodyVOs[1].setCoperatorid(user);
//		bodyVOs[1].setDoperatedate(date);
//		bodyVOs[1].setAbstracts(zy);
//		bodyVOs[1].setCreditmny(bbmny);
//		bodyVOs[1].setYbcreditmny(ybmny);
//		bodyVOs[1].setPk_currency(pk_currency);
//		bodyVOs[1].setNrate(exrate);
//
//		return bodyVOs;
//	}
//
//	private double safeAsDouble(String s){
//		try{
//			return StringUtil.isEmptyWithTrim(s)?0:Double.parseDouble(s);
//		} catch(Exception e){
//			return 0;
//		}
//	}
//
//	/**
//	 * 根据图片VO创建预凭证表头
//	 * @param imageVO
//	 * @param user
//	 * @param date
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private IMageVoucherVO createPreVoucherHeadVO(ImageLibraryVO imageVO, String user) throws DZFWarpException{
//		IMageVoucherVO headVO = new IMageVoucherVO();
//		headVO.setPk_corp(imageVO.getPk_corp());
//		headVO.setCoperatorid(user);
//		DZFDate date = null;
//		if(imageVO.getCvoucherdate() != null){//取制单日期
//			date = imageVO.getCvoucherdate();
//		}else{
//			date = imageVO.getDoperatedate();//取上传日期
//		}
//		headVO.setDoperatedate(date);
//		headVO.setPk_image_library(imageVO.getPrimaryKey());
//		headVO.setVbillcode(getPreVoucherBillcode(imageVO.getPk_corp(), date));
//		return headVO;
//	}
//
//	/**
//	 * 根据公司+日期，获取预凭证号
//	 * @param pk_corp
//	 * @param date
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private String getPreVoucherBillcode(String pk_corp, DZFDate date) throws DZFWarpException{
//		String key = pk_corp + "|" + date.toString();
//		if(!preVoucherBillcodeMap.containsKey(key)){
//			String where = " doperatedate like '"+date.getYear()+"%' and vbillcode like '"+date.getStrMonth()+""+date.getStrDay()+"%' and nvl(dr,0)=0 and pk_corp='"+pk_corp+"'  order by  vbillcode  desc ";
//			IMageVoucherVO[] voucherVOs = (IMageVoucherVO[]) singleObjectBO.queryByCondition(IMageVoucherVO.class, where,new SQLParameter());
//			int maxNo = 0;
//			if(voucherVOs != null && voucherVOs.length > 0){
//				String flowno = voucherVOs[0].getVbillcode().substring(4, voucherVOs[0].getVbillcode().length()) ;
//				maxNo = Integer.valueOf(flowno) ;
//			}
//			preVoucherBillcodeMap.put(key, new Integer(maxNo));
//		}
//
//		Integer currFlowNo = preVoucherBillcodeMap.get(key);
//		currFlowNo++;
//		preVoucherBillcodeMap.put(key, currFlowNo);
//
//		return String.format("%s%04d", date.getStrMonth()+""+date.getStrDay(), currFlowNo.intValue());
//	}
//
//	private List<ImageIdentVO> queryImageIdentVOs(String pk_image_library) throws DZFWarpException{
//		String sql = String.format("select a.*, b.clipkind from ynt_image_ident a inner join ynt_image_meta b on a.pk_image_meta=b.pk_image_meta where nvl(a.dr,0)=0 and b.pk_image_library='%s'", pk_image_library);
//		return (List<ImageIdentVO>) singleObjectBO.executeQuery(sql,new SQLParameter() ,new BeanListProcessor(ImageIdentVO.class));
//	}
//
//	/**
//	 * 判断是否往来款替代科目
//	 * @param voucherBodyVO
//	 * @param pk_corp
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private boolean isSubBetweenSubjects(IMageVoucherBVO voucherBodyVO, String pk_corp) throws DZFWarpException{
//		if(StringUtil.isEmptyWithTrim(voucherBodyVO.getPk_account())) return false;
//		if(!subBetSubjectsMap.containsKey(pk_corp)){
//			YntCpaccountVO[] accountVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, "nvl(dr,0)=0 and accountcode='" + Common.subBetSubjectcode + "' and pk_corp='" + pk_corp + "'",new SQLParameter());
//			if(accountVOs == null || accountVOs.length == 0)
//				if(gl_cbconstant.getFangan_2007().equals(voucherBodyVO.getPk_account()) || gl_cbconstant.getFangan_2013().equals(voucherBodyVO.getPk_account())){//2003 与2007走此判断
//					throw new BusinessException(String.format("公司%s下没有定义科目编码为%s的往来款科目，请检查！", pk_corp, Common.subBetSubjectcode));
//				}else{
//					return false;
//				}
//
//			subBetSubjectsMap.put(pk_corp, accountVOs[0].getPrimaryKey());
//		}
//
//		String betweenSubjects = subBetSubjectsMap.get(pk_corp);
//		return voucherBodyVO.getPk_account().equals(betweenSubjects);
//	}
//
//	/**
//	 * 获取有余额的公司往来科目
//	 * @param pk_corp
//	 * @param period
//	 * @param subjectName
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private String getCorpBetweenSubjects(String pk_corp, String period, String subjectName) throws DZFWarpException{
//		if(StringUtil.isEmptyWithTrim(subjectName)) return "";
//		for(String betSubjectcode: Common.betSubjectcodes){
//			String where = String.format("nvl(dr,0)=0 and pk_corp='%s' and accountname='%s' and accountcode like '%s%%'", pk_corp, subjectName, betSubjectcode);
//			YntCpaccountVO[] accountVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class, where,new SQLParameter());
//			if(accountVOs == null || accountVOs.length == 0) continue;
//			// 判断科目是否有余额
//			DZFDouble mny = yntBoPubUtil.getQmMny(pk_corp,period, accountVOs[0].getPrimaryKey());
//			if(mny != null && mny.getDouble()!=0){
//				return accountVOs[0].getPrimaryKey();
//			}
//		}
//		return "";
//	}
//
//	/**
//	 * 根据预凭证头获取往来单位
//	 * @param voucherVO
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private String getBetweenUnit(IMageVoucherVO voucherVO) throws DZFWarpException{
//		String sql = String.format("select a.* from ynt_image_ident a " +
//				"inner join ynt_image_meta b on a.pk_image_meta=b.pk_image_meta " +
//				"where b.pk_image_library='%s' and nvl(a.dr,0)=0 and b.clipkind=2", voucherVO.getPk_image_library());
//		ImageIdentVO identVO =(ImageIdentVO)singleObjectBO.executeQuery(sql, new SQLParameter(),new BeanProcessor(ImageIdentVO.class));
//		return (identVO == null?"":identVO.getIdentvalue());
//	}
//	/**
//	 * 根据日期获取期间
//	 * @param date
//	 * @return
//	 */
//	private String getPeriod(DZFDate date){
//		return date.toString().substring(0, 7);
//	}
//
//	public void PreVoucherToGL(IMageVoucherVO[] voucherVOs, String user,
//			DZFDate date) throws DZFWarpException{
//		try{
//			if(voucherVOs == null || voucherVOs.length == 0)
//				return;
//
//			for(IMageVoucherVO voucherVO: voucherVOs){
//				PreVoucherToGL(voucherVO, user, date);
//			}
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//	}
//
//	private void PreVoucherToGL(IMageVoucherVO voucherVO, String user,DZFDate date) throws DZFWarpException{
//		IMageVoucherVO dbVO = (IMageVoucherVO) singleObjectBO.queryByPrimaryKey(IMageVoucherVO.class, voucherVO.getPrimaryKey());
//		if(dbVO == null){
//			throw new BusinessException(String.format("预凭证[%s]已经找不到，请确认是否已经删除！", voucherVO.getVbillcode()));
//		}
//
//		//if(!dbVO.getTs().equals(voucherVO.getTs())){
//		//	throw new BusinessException(String.format("预凭证[%s] 已经被他人修改，请刷新后重试！", voucherVO.getVbillcode()));
//		//}
//
//		if(voucherVO.getIstogl() != null && voucherVO.getIstogl().booleanValue()){
//			throw new BusinessException(String.format("预凭证[%s] 已经转总账，不允许重复转总账！", voucherVO.getVbillcode()));
//		}
//
//		if(voucherVO.getGlvoucherno() != null && voucherVO.getGlvoucherno().length() > 0){
//			String hid = voucherVO.getGlvoucherno();
//			String sql = " select count(0) z1 from ynt_tzpz_h where nvl(dr,0)=0 and pk_tzpz_h = '"+hid+"'    ";
//			boolean zz = (boolean)singleObjectBO.executeQuery(sql, null, new ResultSetProcessor(){
//
//				@Override
//				public Object handleResultSet(ResultSet rs) throws SQLException {
//					boolean falg = false;
//					if(rs.next()){
//						Integer z1 = rs.getInt("z1");
//						if(z1 > 0){
//							falg = true;
//						}
//					}
//					return falg;
//				}
//
//			});
//			if(zz){
//				throw new BusinessException("当前预凭证已经转总账，不允许重复转总账！");
//			}
//
//		}
//
//		IMageVoucherBVO[] voucherBodyVOs = (IMageVoucherBVO[]) singleObjectBO.queryByCondition(IMageVoucherBVO.class, "nvl(dr,0)=0 and pk_image_voucher='" + voucherVO.getPrimaryKey() + "'",new SQLParameter());
////		if(voucherBodyVOs == null || voucherBodyVOs.length == 0){
////			throw new BusinessException(String.format("预凭证[%s]表体行为空，不允许转总账！", voucherVO.getVbillcode()));
////		}
//
//		ImageLibraryVO imageVO = (ImageLibraryVO) singleObjectBO.queryByPrimaryKey(ImageLibraryVO.class, voucherVO.getPk_image_library());
//		if(imageVO == null)
//			throw new BusinessException("图片已经被他人删除，请检查！");
//		// 校验图片组是否合法
//		ImageGroupVO imageGoupVO = checkImageGroupValid(imageVO.getPk_image_group());
//
//		int billCount = singleObjectBO.getTotalRow("ynt_image_library", "nvl(dr,0)=0 and pk_image_group= '" + imageVO.getPk_image_group() + "' ", null);
//
//		date = voucherVO.getDoperatedate();
//		TzpzHVO headVO = new TzpzHVO() ;
//		headVO.setPk_corp(voucherVO.getPk_corp()) ;
//		headVO.setPzlb(0) ;//凭证类别：记账
//		headVO.setIshasjz(DZFBoolean.FALSE) ;
//		headVO.setCoperatorid(user) ;
//		headVO.setDoperatedate(date) ;
//		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(voucherVO.getPk_corp() , headVO.getDoperatedate())) ;
//		headVO.setSourcebillid(voucherVO.getPrimaryKey());   // 来源单据ID
//		headVO.setSourcebilltype(IBillTypeCode.HP26);   // 来源单据类别
//		headVO.setPk_image_group(imageVO.getPk_image_group());
//		headVO.setPk_image_library(imageVO.getPrimaryKey());
//		headVO.setVbillstatus(8);
////		headVO.setVoucherstatus(2);   // 状态为暂存态-正常
//		headVO.setPeriod(date.toString().substring(0,7));
//		headVO.setVyear(Integer.valueOf(date.toString().substring(0,4)));
//		headVO.setIsfpxjxm(new DZFBoolean("N"));
//		headVO.setNbills(billCount);//设置单据张数
//
//		TzpzBVO[] bodyVOs = null;
//		if(voucherBodyVOs != null && voucherBodyVOs.length > 0){
//			bodyVOs = new TzpzBVO[voucherBodyVOs.length];
//			TzpzBVO bodyVO;
//			int index=0;
//			double debit=0, credit = 0, totalDebit=0, totalCredit=0;
//			for(IMageVoucherBVO voucherBodyVO: voucherBodyVOs){
////				if(StringUtil.isEmptyWithTrim(voucherBodyVO.getPk_account())){
////					throw new BusinessException(String.format("预凭证[%s]表体行科目为空，不允许转总账！", voucherVO.getVbillcode()));
////				}
////				if(StringUtil.isEmptyWithTrim(voucherBodyVO.getAbstracts())){
////					throw new BusinessException(String.format("预凭证[%s]表体行摘要为空，不允许转总账！", voucherVO.getVbillcode()));
////				}
//
//				debit = voucherBodyVO.getDebitmny() == null?0:voucherBodyVO.getDebitmny().doubleValue();
//				credit = voucherBodyVO.getCreditmny() == null?0:voucherBodyVO.getCreditmny().doubleValue();
//				totalCredit +=credit;
//				totalDebit += debit;
//
////				if(debit == 0 && credit == 0){
////					throw new BusinessException(String.format("预凭证[%s]表体行借方和贷方金额都为0，不允许转总账！", voucherVO.getVbillcode()));
////				}
//
//				String pk_account = voucherBodyVO.getPk_account();
//				if(isSubBetweenSubjects(voucherBodyVO, voucherVO.getPk_corp())){   // 如果是往来科目，需要替换往来科目
//					String betweenUnit = getBetweenUnit(voucherVO);   // 获取识别出来的往来单位
//					String betweenSubject = getCorpBetweenSubjects(voucherVO.getPk_corp(),  getPeriod(date), betweenUnit);  // 查找有余额的往来科目
//					//System.out.println(String.format("查找到公司[%s]期间[%s]往来单位[%s]所对应的往来科目为[%s]", voucherVO.getPk_corp(),
//					//		getPeriod(date), betweenUnit, betweenSubject));
//					if(!StringUtil.isEmptyWithTrim(betweenSubject))
//						pk_account = betweenSubject;
//
////					headVO.setVoucherstatus(3);   // 状态为暂存态-往来款
//				}
//
//				bodyVO = new TzpzBVO() ;
//				bodyVO.setPk_accsubj(pk_account) ; // 科目
//				bodyVO.setZy(voucherBodyVO.getAbstracts()) ;//摘要
////				bodyVO.setPk_currency(pub_utilserv.getCNYPk());   // 币种
//				bodyVO.setPk_currency(voucherBodyVO.getPk_currency());
//
//				bodyVO.setDfmny(voucherBodyVO.getCreditmny()) ;   // 贷方金额
//				bodyVO.setJfmny(voucherBodyVO.getDebitmny()) ;    // 借方金额
//				bodyVO.setYbdfmny(voucherBodyVO.getYbcreditmny());
//				bodyVO.setYbjfmny(voucherBodyVO.getYbdebitmny());
//				bodyVO.setNrate(voucherBodyVO.getNrate());
//				bodyVO.setPk_corp(headVO.getPk_corp());
//
//				bodyVOs[index++] = bodyVO;
//			}
//			// 贷方合计
//			headVO.setDfmny(new DZFDouble(totalCredit));
//			// 借方合计
//			headVO.setJfmny(new DZFDouble(totalDebit));
//		}
//
////		if(totalCredit != totalDebit){
////			throw new BusinessException(String.format("预凭证[%s]生成的凭证，借方合计%f不等于贷方合计%f", voucherVO.getVbillcode(), totalDebit,totalCredit));
////		}
//
////		HYBillVO billVO = new HYBillVO() ;
////		billVO.setParentVO(headVO) ;
////		billVO.setChildrenVO(bodyVOs) ;
//		headVO.setChildren(bodyVOs);
//		CorpVO covp = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, headVO.getPk_corp());
//		headVO = voucher.saveVoucher(covp, headVO);
//		//headVO = (TzpzHVO)singleObjectBO.saveObject(headVO.getPk_corp(), headVO);
//
//		// 回写预凭证
//		voucherVO.setIstogl(new DZFBoolean(true));
//		voucherVO.setGlvoucherno(headVO.getPrimaryKey());
//		singleObjectBO.update(voucherVO);
//
//		//备份图片组表
//		Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
//		imageGroupMap.put(imageGoupVO.getPrimaryKey(), imageGoupVO);
//		saveImageGroupBackUp(imageGroupMap, PhotoState.state100);
//	}
//	public void updatePreVoucherGLState(String pk_image_voucher,
//			boolean istogl, String voucherno) throws DZFWarpException{
//		try{
//			IMageVoucherVO voucherVO = (IMageVoucherVO) singleObjectBO.queryByPrimaryKey(IMageVoucherVO.class, pk_image_voucher);
//			if(voucherVO == null) return;
//			voucherVO.setIstogl(new DZFBoolean(istogl));
//			voucherVO.setGlvoucherno(voucherno);
//			singleObjectBO.update(voucherVO);
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//	}
//
//	/**
//	 * 校验图片组是否合法
//	 * @param pk_image_group
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private ImageGroupVO  checkImageGroupValid(String pk_image_group) throws DZFWarpException{
//		ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, pk_image_group);
//		if(groupVO == null){
//			throw new BusinessException("没有找到PK值为" + pk_image_group + "的图片组，请确认该图片组是否已经被删除！");
//		}
//
//		if(groupVO.getIsskiped() != null && groupVO.getIsskiped().booleanValue()){
//			throw new BusinessException("图片组" + groupVO.getGroupcode() + "已经被退回重拍，不允许进行任何操作！");
//		}
//
//		//新版数据中心，2017.5.18去掉此校验，因为目前的临时凭证就是正式凭证的表
////		TzpzHVO[] tzpzVOs =(TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, "nvl(dr,0)=0 and  pk_image_group='" + pk_image_group + "'",new SQLParameter());
////		if(tzpzVOs != null && tzpzVOs.length>0)
////			throw new BusinessException("总账凭证中已经存在图片组[" + pk_image_group + "]对应的凭证，不允许进行任何图片操作，凭证号为：" + tzpzVOs[0].getPzh() + ",请刷新后重试");
//
//		return groupVO;
//	}
//	private IMageVoucherVO checkImageVoucherValid(String pk_image_voucher){
//		IMageVoucherVO voucherVO = (IMageVoucherVO) singleObjectBO.queryByPrimaryKey(IMageVoucherVO.class, pk_image_voucher);
//		//校验预凭证信息是否合法
//		if(voucherVO != null){
//			Integer dr = (Integer) voucherVO.getAttributeValue("dr");
//			if(dr != null && dr == 1){//已删除标志
//				throw new BusinessException("没有找到PK值为" + pk_image_voucher + "的预凭证，请确认该预凭证是否已经被删除！");
//			}
//		}
//		return voucherVO;
//	}
//	/**
//	 * 转会计
//	 * @param pk_image_group
//	 * @param user
//	 * @param date
//	 * @param sourcebilltype 切图工作台:IBillTypeCode.HP23,预凭证:IBillTypeCode.HP26
//	 * @throws DZFWarpException
//	 */
//	public void ClipDirectToAccount(String pk_image_voucher, String pk_image_group, String user, DZFDate date, String sourcebilltype)throws DZFWarpException {
//
//		// 校验图片组是否合法
//		ImageGroupVO groupVO = checkImageGroupValid(pk_image_group);
//		IMageVoucherVO voucherVO = checkImageVoucherValid(pk_image_voucher);
//		try{
//			//单据张数
//			int billCount = singleObjectBO.getTotalRow("ynt_image_library", "nvl(dr,0)=0 and pk_image_group= '" + pk_image_group + "' ", null);
//			DZFDate cvoucher = groupVO.getCvoucherdate();
//			// 生成暂存态的凭证
//			TzpzHVO headVO = new TzpzHVO() ;
//			headVO.setPk_corp(groupVO.getPk_corp()) ;
//			headVO.setPzlb(0) ;//凭证类别：记账
//			headVO.setIshasjz(DZFBoolean.FALSE) ;
//			headVO.setCoperatorid(user) ;
//			headVO.setDoperatedate(groupVO.getCvoucherdate()) ;//制单日期
//			headVO.setPzh(yntBoPubUtil.getNewVoucherNo(groupVO.getPk_corp() , headVO.getDoperatedate())) ;//改值
//			headVO.setSourcebillid(pk_image_voucher);   // 来源单据ID 预凭证
//			headVO.setSourcebilltype(sourcebilltype);   // 来源单据类别
//			headVO.setPk_image_group(pk_image_group);
//			headVO.setVbillstatus(-1); //8为自由态，-1定为转会计状态
////			headVO.setVoucherstatus(4);   // 状态为暂存态-跳过
//			headVO.setDfmny(new DZFDouble(0));
//			headVO.setJfmny(new DZFDouble(0));
//			headVO.setPeriod(cvoucher == null ? null : cvoucher.toString().substring(0,7));
//			headVO.setVyear(Integer.valueOf(date.toString().substring(0,4)));
//			headVO.setIsfpxjxm(new DZFBoolean("N"));
//			headVO.setNbills(billCount);//设置单据张数
//
////			HYBillVO billVO = new HYBillVO() ;
////			billVO.setParentVO(headVO) ;
//			singleObjectBO.saveObject(headVO.getPk_corp(), headVO);
//
//			//回写预凭证，目前需求是：转会计的单据，预凭证istogl为'Y'
//			if(voucherVO != null){
//				voucherVO.setIstogl(DZFBoolean.TRUE);
//				voucherVO.setGlvoucherno(headVO.getPrimaryKey());
//				singleObjectBO.update(voucherVO);
//			}
//
//			//更新备份图片组信息
//			Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
//			imageGroupMap.put(groupVO.getPrimaryKey(), groupVO);
//			saveImageGroupBackUp(imageGroupMap, PhotoState.state4);//转会计
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//	}
//	public ImageTurnMsgVO saveReturnMsg(ImageTurnMsgVO vo)throws DZFWarpException{
//		try{
//			if(vo == null) return vo;
//			// 退回重拍
////			ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, vo.getPk_image_group());
//
//			ImageGroupVO groupVO = checkImageGroupValid(vo.getPk_image_group());
//
//			//备份图片组信息，有两次提交，需优化
//			Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
//			imageGroupMap.put(groupVO.getPrimaryKey(), groupVO);
//
//			innerSkipClipedImage(groupVO, vo.getCoperatorid(), vo.getTs());
//			// 保存
//			vo = (ImageTurnMsgVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
//
//			if(groupVO.getIstate() ==  PhotoState.state80){
//				throw new BusinessException("图片已退回,不能重复提交!");
//			}
//			//备份图片组
//			saveImageGroupBackUp(imageGroupMap, PhotoState.state80);
//
//			String pk_image_lib = vo.getPk_image_librarys();
//			StringBuffer badimagepath = new StringBuffer();//图片退回信息(按照组退回)
//			String[] libs = null;
//			if(pk_image_lib!=null && pk_image_lib.trim().length()>0){
//				libs = pk_image_lib.split(",");
//			}
//			if(libs!=null && libs.length>0){
//				StringBuffer wherepklib = new StringBuffer();
//				for(String  str:libs){
//					wherepklib.append("'"+str+"',");
//				}
//				ImageLibraryVO[] libvos = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, "   pk_image_library in("+wherepklib.substring(0, wherepklib.length()-1)+")", new SQLParameter());
//
//				if(libvos != null && libvos.length>0){
//					badimagepath.append(libvos[0].getImgpath().replace("\\\\","/")+",");
//
//					//更新退回图片状态
//					for(ImageLibraryVO libvo : libvos){
//						libvo.setAttributeValue("isback", DZFBoolean.TRUE);
//					}
//					singleObjectBO.updateAry(libvos, new String[]{"isback"});
//				}
//
//				//  2017-09-08  zhw  修改了图片之间的关联关系  回写退回标记
//				if(groupVO != null && groupVO.getSourcemode() != null && groupVO.getSourcemode().intValue() ==PhotoState.SOURCEMODE_10){
//
//					SQLParameter sp = new SQLParameter();
//					sp.addParam(StateEnum.HAND_BACK.getValue());
//					UserVO  user =userServiceImpl.queryUserJmVOByID(vo.getCoperatorid());//UserCache.getInstance().get(vo.getCoperatorid(), null);
//					sp.addParam("图片被"+user.getUser_name()+"退回");
//					StringBuffer strb = new StringBuffer();
//					strb.append(" update ynt_image_ocrlibrary set istate =? ,reason = ?  where  crelationid in("+wherepklib.substring(0, wherepklib.length()-1)+")");
//					singleObjectBO.executeUpdate(strb.toString(),sp);
//				}
//
//			}
//
//
//			//极光推送图片消息 add by zhangj
//
//			IMsgService sys_msgtzserv = (IMsgService) SpringUtils.getBean("sys_msgtzserv");
//
//			ImageGroupVO  ztgroupvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, groupVO.getPrimaryKey());
//
//			sys_msgtzserv.saveMsgVoFromImage(vo.getPk_corp(),vo.getCoperatorid(), vo,ztgroupvo);//图片处理
//
//			//end 2015.12.30
//
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//		return vo;
//	}
//	public void readReturnMsg(String pk_corp,ImageBeanVO imageBean)throws DZFWarpException {
//		try{
//			String[] msgKeys = imageBean.getImagemsgkeys().split(",");
//			if(msgKeys == null || msgKeys.length == 0) return;
//			ImageMsgreaderVO[] readVOs = new ImageMsgreaderVO[msgKeys.length];
//			int index = 0;
//			for(String msgKey: msgKeys){
//				ImageTurnMsgVO msgVO = (ImageTurnMsgVO) singleObjectBO.queryByPrimaryKey(ImageTurnMsgVO.class, msgKey);
//				if(msgVO == null){
//					throw new BusinessException("系统中不存在主键为" + msgKey + "的退回重拍消息，请检查！");
//				}
//				ImageMsgreaderVO[] sysReadVOs = (ImageMsgreaderVO[]) singleObjectBO.queryByCondition(ImageMsgreaderVO.class, "nvl(dr,0)=0 and pk_image_returnmsg='" + msgVO.getPrimaryKey() + "' and cuserid='" + imageBean.getAccount_id() +"'",new SQLParameter());
//				if(sysReadVOs != null && sysReadVOs.length > 0){
//					if("0".equals(imageBean.getOp_msg())){
//
//						for(ImageMsgreaderVO sysVO : sysReadVOs){
//							sysVO.setBread(DZFBoolean.TRUE);
//						}
//						singleObjectBO.updateAry(sysReadVOs, new String[]{"bread"});
//						return;
//					}else if("1".equals(imageBean.getOp_msg())){
//						singleObjectBO.deleteVOArray(sysReadVOs);
//					}
//				}
//
//				ImageMsgreaderVO readVO = new ImageMsgreaderVO();
//				readVO.setPk_image_returnmsg(msgVO.getPrimaryKey());
//				readVO.setCoperatorid(imageBean.getAccount_id());
//				readVO.setCuserid(imageBean.getAccount_id());
//				readVO.setDoperatedate(new DZFDate());
//				readVO.setBread(DZFBoolean.TRUE);
//				readVO.setReadtime(new DZFDateTime());
//				readVOs[index++] = readVO;
//			};
//			singleObjectBO.insertVOArr("123456",readVOs);
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//	}
//
//	public IMageVoucherVO[] queryHandlingVouchers(String pk_datacorp, int count)throws DZFWarpException{
//		IMageVoucherVO[] vos = null;
//		try{
//			vos = poimp_manage.dispatchEditingVouchers(pk_datacorp,count, null);
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
////		IPhotoManage manage = NCLocator.getInstance().lookup(IPhotoManage.class);
//		return vos;
//	}
//
//	public void releaseHandlingVouchers(IMageVoucherVO[] voucherVOs)throws DZFWarpException{
//		try{
////			IPhotoManage manage = NCLocator.getInstance().lookup(IPhotoManage.class);
//			poimp_manage.releaseDispatchedVouchers(voucherVOs);
//		}catch(Exception e){
//			log.error("错误",e);
//			throw new WiseRunException(e);
//		}
//	}
//
//	public ImageQueryBean[] uploadImagesFile(Integer sel,String pk_corp, String uploadlot,
//			String uploader, ServerFileSerializable fs,String qj, boolean isCompress)throws DZFWarpException{
//		ImageQueryBean[] beans = null;
//
//		if(StringUtil.isEmptyWithTrim(pk_corp)){
//			throw new BusinessException("传入的pk_corp参数不能为空！");
//		}
//		if(StringUtil.isEmptyWithTrim(uploader)){
//			throw new BusinessException("传入的uploader参数不能为空！");
//		}
//		if(StringUtil.isEmptyWithTrim(uploadlot)){
//			throw new BusinessException("传入的uploadlot参数不能为空！");
//		}
//		uploadlot = uploadlot.toLowerCase();
//
//		//检查上传图片期间是否在建账前
//		checkIsAccount(pk_corp, qj, uploader);
//		//检查上传图片期间是否期间损益结转
//		checkIsQjSyJz(pk_corp, qj);
//		try{
//			// 根据上传批次查找之前的记录，并删除
//			ArrayList<String> delfiles = delImageLibs(findVOBySessionFlag(pk_corp, uploadlot,null));
//			// 保存压缩包到imageBasePath中，以上传批次作为zip文件名
//			delImgFiles(delfiles);
//			// 解压缩文件
//			HashMap<String, String> groupSettle = new HashMap<String, String>();
//			Map<String, String> groupLibMap = new HashMap<String, String>();
//			HashMap<String, String> smallGroupLibMap = new HashMap<String, String>();
//			Map<String, String> midGroupLibMap = new HashMap<String, String>();
//			TreeMap<String, ArrayList<String>> groupfileMap = unzipFile(fs.getFile(), pk_corp, isCompress,groupSettle, groupLibMap, null,uploader,smallGroupLibMap,midGroupLibMap);
//			ImageGroupVO[] aggVOs = buildGroupRecords(groupfileMap, pk_corp, uploadlot, uploader,qj,sel,groupSettle,groupLibMap,null,null);
////			beans = ImageQueryBean.fromVO(aggVOs);
//			ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
//			beans = ct.fromVO(aggVOs);
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//		return beans;
//	}
//
//	/**
//	 * 汇率信息
//	 */
//	HashMap<String, ExrateVO> map = new HashMap<String, ExrateVO>();
//	public ExrateVO queryRate(String currcode,String pk_corp) throws DZFWarpException{
//		if(map.containsKey(currcode+"_"+pk_corp)){
//			return map.get(currcode+"_"+pk_corp);
//		}else{
//			StringBuffer sb = new StringBuffer();
//			sb.append(" select ynt_exrate.* from ynt_exrate ynt_exrate" );
//			sb.append(" left join ynt_bd_currency ynt_bd_currency on ynt_exrate.pk_currency = ynt_bd_currency.pk_currency");
//			sb.append(" where ynt_exrate.pk_corp = '"+pk_corp+"' and lower(ynt_bd_currency.currencycode) = '"+currcode.toLowerCase()+"'");
//			sb.append(" and nvl(ynt_exrate.dr,0) = 0");
//			ArrayList<ExrateVO> result = (ArrayList<ExrateVO>)singleObjectBO.executeQuery(sb.toString(), new SQLParameter(),new BeanListProcessor(ExrateVO.class));
//			if (result != null && result.size() > 0) {
//				ExrateVO vo = (ExrateVO)result.get(0);
//				map.put(currcode+"_"+pk_corp, vo);
//				return vo;
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * 取所有币种信息
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private HashMap<String, BdCurrencyVO> queryCurrencyInfo() throws DZFWarpException{
//		Collection col = singleObjectBO.retrieveByClause(BdCurrencyVO.class, "nvl(dr,0)=0",new SQLParameter());
//		HashMap<String, BdCurrencyVO> map = new HashMap<String, BdCurrencyVO>();
//		if(col != null && col.size() > 0){
//			BdCurrencyVO[] vos = (BdCurrencyVO[])col.toArray(new BdCurrencyVO[0]);
//			for(BdCurrencyVO vo : vos){
//				map.put(vo.getCurrencycode(), vo);
//			}
//		}
//		return map;
//	}
//
//	protected ImageGroupVO[] buildGroupRecords(TreeMap<String, ArrayList<String>> groupfileMap, String pk_corp, String uploadlot, String uploader,String qj,Integer sel,HashMap<String, String> groupSettle, Map<String, String> groupLibMap, String cert,String certtx) throws DZFWarpException{
//		if(groupfileMap == null || groupfileMap.size() == 0) return null;
//		ImageGroupVO[] aggVOs = new ImageGroupVO[groupfileMap.size()];
//		ImageGroupVO groupVO = null;
//		int index = 0;
//		for(String group: groupfileMap.keySet()){
//			//liangjy,增加付款方式//付款方式-----已由新接口走，已不从这里走了。数据中心没有付款方式。
////			String settle = groupSettle.get(group);
////			if(settle != null && !settle.equals("")){
////				if(settle.indexOf("[") != -1 && settle.indexOf("]") != -1){
////					settle = settle.substring(settle.indexOf("[")+1, settle.indexOf("]"));
////				}
////			}
//			String settle = null;//zpm
//			groupVO = createGroupVO(sel,qj,pk_corp, uploadlot, uploader, group);
//			//liangjy
//			groupVO.setSettlemode(settle);
//			if(cert != null){
//				groupVO.setCert(DZFBoolean.valueOf(cert));
//			}
//			groupVO.setCerttx(certtx);
//			groupVO.setChildren(createImageVO(groupVO, groupfileMap.get(group), groupLibMap, null, null));
////			aggVOs[index++] = (ImageGroupVO) singleObjectBO.saveObject(groupVO.getPk_corp(),groupVO);
//			aggVOs[index++] = groupVO;
//		}
//		String[] headpks = singleObjectBO.insertVOArr(pk_corp, aggVOs);//更新主表
//		List<ImageLibraryVO> childrenList = new ArrayList<ImageLibraryVO>();
//		for(int i = 0; i < aggVOs.length; i++){
//			ImageLibraryVO[] imglibvos = (ImageLibraryVO[]) aggVOs[i].getChildren();
//			for(int j = 0; j < imglibvos.length; j++){
//				imglibvos[j].setPk_image_group(aggVOs[i].getPrimaryKey());
//				childrenList.add(imglibvos[j]);
//			}
//		}
//		if(childrenList.size() > 0){
//			singleObjectBO.insertVOArr(pk_corp, childrenList.toArray(new ImageLibraryVO[0]));
//		}
//		return aggVOs;
//	}
//


//
//	/**
//	 * 删除上传记录
//	 */
//	@Override
//	public Integer deleteRecord(String sessionflag,String pk_corp) throws DZFWarpException {
//
//		StringBuffer groupsql  = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//
//		groupsql.append("SELECT * FROM YNT_IMAGE_GROUP  ");
//		groupsql.append("   where nvl(dr,0)=0 and  ");
//		groupsql.append("    (sessionflag =? or pk_image_group = ? )");
//		groupsql.append("   and pk_corp = ? ");
//		sp.addParam(sessionflag);
//		sp.addParam(sessionflag);
//		sp.addParam(pk_corp);
//
//		List<ImageGroupVO> listgroup = 	(List<ImageGroupVO>) singleObjectBO.executeQuery(groupsql.toString(), sp, new BeanListProcessor(ImageGroupVO.class));
//
//		if(listgroup!=null && listgroup.size()>0){
//			StringBuffer groupid = new StringBuffer();
//			for(ImageGroupVO groupvo:listgroup){
//				groupid.append("'"+groupvo.getPrimaryKey()+"',");
//			}
//			//凭证是否引用
//			String pzsql = "select pk_image_group from ynt_tzpz_h h where nvl(dr,0)=0 and  h.pk_image_group in("+groupid.subSequence(0, groupid.length()-1)+")";
//			List<String> grouplist = (List<String>) singleObjectBO.executeQuery(pzsql, new SQLParameter(), new ColumnListProcessor());
//			StringBuffer imgpzsql = new StringBuffer();
//			imgpzsql.append(" select b.pk_image_group from YNT_IMAGE_VOUCHER a ");
//			imgpzsql.append("  INNER JOIN  YNT_IMAGE_LIBRARY b ON a.PK_IMAGE_LIBRARY = b.PK_IMAGE_LIBRARY ");
//			imgpzsql.append(" where nvl(a.dr,0)=0 and nvl(b.dr,0)=0  and  b.pk_image_group  in("+groupid.subSequence(0, groupid.length()-1)+")");
//			List<String> imggrouplist = (List<String>) singleObjectBO.executeQuery(imgpzsql.toString(), new SQLParameter(), new ColumnListProcessor());
//
//			//数据中心是否引用
//			StringBuffer  metasql = new StringBuffer();
//			metasql.append("select l.pk_image_group from ynt_image_meta m inner join ynt_image_library l on m.pk_image_library = l.pk_image_library " );
//			metasql.append(" where  l.pk_image_group  in("+groupid.subSequence(0, groupid.length()-1)+")");
//			metasql.append(" and nvl(l.dr,0)=0 and nvl(m.dr,0)=0");
//			List<String> grouplist1 = (List<String>) singleObjectBO.executeQuery(metasql.toString(), new SQLParameter(), new ColumnListProcessor());
//			//判断当前图像是不是被引用
//			if(grouplist!=null &&  grouplist.size()>0){
//				throw new BusinessException("凭证已引用不能删除!");
//			}
//
//			if(imggrouplist!=null && imggrouplist.size()>0){
//				throw new BusinessException("预凭证已引用不能删除!");
//			}
//			if(grouplist1!=null && grouplist1.size()>0){
//				throw new BusinessException("数据中心已引用，不能删除!");
//			}
//
//			for(int i=0;i<listgroup.size();i++){
//				listgroup.get(i).setDr(1);
//			}
//			int res = singleObjectBO.updateAry(listgroup.toArray(new ImageGroupVO[0]));
//
//			return res;
//		}
//		return 0;
//	}
//
//	public IMageVoucherVO[] batchCreateHDVoucher(ImageHuadaoHVO[] imageVOs,String user, DZFDate date) throws DZFWarpException{
//		IMageVoucherVO[] resultBillVOs = null;
//		try{
//			if(imageVOs == null || imageVOs.length == 0) return resultBillVOs;
//
//			resultBillVOs = new IMageVoucherVO[imageVOs.length];
//			int index = 0;
//			IMageVoucherVO headVO = null;
//			IMageVoucherBVO[] bodyVOs = null;
//			HashMap<String, DcModelHVO> map = queryDcModelVO();
//			String pk_curr = yntBoPubUtil.getCNYPk();
//			HashMap<String, String> fpMap = getMapFP();
//			for(ImageHuadaoHVO imageVO: imageVOs){
//				// 根据图片VO创建预凭证表头
//				headVO = createHDVoucherHeadVO(imageVO, user, date);
//				if(imageVO.getErrortag().equals("N")){
//					// 根据图片VO创建预凭证表体
//					String[] ptype = imageVO.getPtype().split("-");
//					String fpstylecode = ptype[1];//发票类型
//					String szstylecode = ptype[0];//收支类型
//					String pk_corp = headVO.getPk_corp();
//					CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//					String corptype = corpvo.getCorptype();//科目方案
//					String chargedeptname = corpvo.getChargedeptname();
//					String key = corptype +"_"+chargedeptname+"_"+fpstylecode+"_"+szstylecode;
//					if(fpMap.containsKey(fpstylecode)){
//						bodyVOs = ConvertVouchBVO.createHDVoucherBodyVOs(headVO, imageVO, map, user, date, pk_curr,key);
//					}else if(fpstylecode.equals(FieldConstant.FPSTYLE_01)){
//						bodyVOs = ConvertVouchBVO.createHDVoucherBodyVOs_01(headVO,imageVO,map, user, date, pk_curr,key);
//					}else if(fpstylecode.equals(FieldConstant.FPSTYLE_02)){
//						bodyVOs = ConvertVouchBVO.createHDVoucherBodyVOs_02(headVO, imageVO, map, user, date, pk_curr,key);
//					}else if(fpstylecode.equals(FieldConstant.FPSTYLE_04)
//							|| fpstylecode.equals(FieldConstant.FPSTYLE_11)){
//						bodyVOs = ConvertVouchBVO.createHDVoucherBodyVOs_KMMC(headVO, imageVO, map, user, date, pk_curr,key);
//					}
//					headVO.setChildren(bodyVOs);
//					if(bodyVOs != null && bodyVOs.length > 0){
//						double creditmny = 0, debitmny=0;
//						for(IMageVoucherBVO bodyVO: bodyVOs){
//							creditmny += bodyVO.getCreditmny() == null?0:bodyVO.getCreditmny().doubleValue();
//							debitmny += bodyVO.getDebitmny() == null?0:bodyVO.getDebitmny().doubleValue();
//						}
//						headVO.setCreditmny(new DZFDouble(creditmny));
//						headVO.setDebitmny(new DZFDouble(debitmny));
//					}
//				}
//				resultBillVOs[index++] = (IMageVoucherVO) singleObjectBO.saveObject(headVO.getPk_corp(), headVO);
//			}
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//		return resultBillVOs;
//	}
//
//	/**
//	 * 以下几种票据类型，通过createHDVoucherBodyVOs方法组织凭证表体数据
//	 * @return
//	 */
//	private HashMap<String, String> getMapFP(){
//		HashMap<String, String> map = new HashMap<>();
//		map.put(FieldConstant.FPSTYLE_03, FieldConstant.FPSTYLE_03);
//		map.put(FieldConstant.FPSTYLE_05, FieldConstant.FPSTYLE_05);
//		map.put(FieldConstant.FPSTYLE_06, FieldConstant.FPSTYLE_06);
//		map.put(FieldConstant.FPSTYLE_07, FieldConstant.FPSTYLE_07);
//		map.put(FieldConstant.FPSTYLE_08, FieldConstant.FPSTYLE_08);
//		map.put(FieldConstant.FPSTYLE_09, FieldConstant.FPSTYLE_09);
//		map.put(FieldConstant.FPSTYLE_10, FieldConstant.FPSTYLE_10);
//		map.put(FieldConstant.FPSTYLE_12, FieldConstant.FPSTYLE_12);
//		map.put(FieldConstant.FPSTYLE_13, FieldConstant.FPSTYLE_13);
//		map.put(FieldConstant.FPSTYLE_14, FieldConstant.FPSTYLE_14);
//		return map;
//	}
//
//	private IMageVoucherVO createHDVoucherHeadVO(ImageHuadaoHVO imageVO, String user, DZFDate date) throws DZFWarpException{
//		IMageVoucherVO headVO = new IMageVoucherVO();
//		String[] strs = imageVO.getName().split("\\.");
//		String pk_image_library = strs[0];
//		ImageLibraryVO libraryVO = queryImageLibraryVO(pk_image_library);
//		if(libraryVO == null){
//			return null;
//		}
//		headVO.setPk_corp(libraryVO.getPk_corp());
//		headVO.setVbillcode(getPreVoucherBillcode(libraryVO.getPk_corp(), date));
//		headVO.setCoperatorid(user);
//		headVO.setDoperatedate(date);
//		headVO.setPk_image_library(pk_image_library);
//		return headVO;
//	}
//
//	public HashMap<String, YntCpaccountVO> queryCpacountMap(String pk_corp){
//		YntCpaccountVO[] vos = AccountCache.getInstance().get(null, pk_corp);
//		HashMap<String, YntCpaccountVO> mapCpa = new HashMap<>();
//		for(YntCpaccountVO vo : vos){
//			mapCpa.put(vo.getAccountcode(), vo);
//		}
//		return mapCpa;
//	}
//	/**
//	 * 查询数据中心凭证模版
//	 * @return
//	 */
//	public HashMap<String, DcModelHVO> queryDcModelVO(){
//		HashMap<String, DcModelHVO> map = new HashMap<>();
//		SQLParameter params = new SQLParameter();
//		params.addParam(IDefaultValue.DefaultGroup);
//		List<DcModelHVO> mhvos = (List<DcModelHVO>) singleObjectBO.executeQuery("pk_corp=? and nvl(dr,0)=0 ", params,
//				new Class[]{DcModelHVO.class, DcModelBVO.class});
////		DcModelHVO[] mhvos = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class, "pk_corp=? and nvl(dr,0)=0 ", params);
//		String key = null;
//		for(DcModelHVO hvo : mhvos){
////			DcModelBVO[] mbvos = (DcModelBVO[]) singleObjectBO.queryByCondition(DcModelBVO.class, "nvl(dr,0)=0 and pk_model_h='"+hvo.getPk_model_h()+"'", null);
////			hvo.setChildren(mbvos);
//			key = hvo.getPk_trade_accountschema()+"_"+hvo.getChargedeptname()+"_"+hvo.getVspstylecode()+"_"+hvo.getSzstylecode() +"_"+hvo.getBusitypetempname();
//			map.put(key, hvo);
//		}
//		return map;
//	}
//
//
//	private ImageLibraryVO queryImageLibraryVO(String pk_image_library) throws DZFWarpException{
//		return (ImageLibraryVO) singleObjectBO.queryByPrimaryKey(ImageLibraryVO.class, pk_image_library);
//	}
//
//
//	public ISysMessageJPush getSysmsgsrv() {
//		return sysmsgsrv;
//	}
//
//	@Autowired
//	public void setSysmsgsrv(ISysMessageJPush sysmsgsrv) {
//		this.sysmsgsrv = sysmsgsrv;
//	}
//
//	public int readXMLImagesSet(){
//		Properties prop = new Properties();
//		InputStream in =  this.getClass().getResourceAsStream("/config.properties");
//	    try {
//			prop.load(in);
//			String hd_imagesCount = prop.getProperty("hd_imagesCount");
//			return hd_imagesCount==null?8000:Integer.parseInt(hd_imagesCount);
//		} catch (IOException e) {
//			log.error(e.getMessage(), e);
//		}finally{
//			if(in!=null){
//				try {
//					in.close();
//				} catch (IOException e) {
//				}
//			}
//
//		}
//
//	    ///加载属性列表
//		return 8000;
//	}
//
//	public int getNewClipingCount(String loginCorp) throws DZFWarpException{
//		String where = getWhere(loginCorp);///zpm 新版这里，有问题，因为会计公司分有分部 了   。。。。。
//		String sql = "select count(*) from " + getFrom() + " where " + where;
//		Object [] result = (Object[])singleObjectBO.executeQuery(sql,new SQLParameter(), new ArrayProcessor());
//		return (result == null || result.length == 0?0: Integer.parseInt(result[0].toString()));
//	}
//
//	private String getWhere(String loginCorp){
//		//isuer-----------这个条件是宝库那边的数据上传造成///增加是否华道
//		String where = "nvl(a.cert,'N')='N' and nvl(a.dr,0)=0 and (nvl(a.iscliped,'N')='N' and nvl(a.isskiped,'N')='N') and nvl(a.isuer,'N')='N' and nvl(a.ishd,'N') = 'N' ";//cert liangyi //and nvl(b.pk_tzpz_h,'0')='0' zpm去掉
//		return where;
//	}
//
//	private String getFrom(){///zpm 新版这里，有问题，因为会计公司分有分部 了   。。。。。
//		// 由于切图中增加了转会计功能，因此这里要把已经转会计的图片组中的所有图片过滤掉
////		return "ynt_image_group a left join ynt_tzpz_h b on a.pk_image_group=b.pk_image_group and nvl(b.dr,0)=0 ";
//		StringBuffer sb = new StringBuffer();
//		sb.append(" ynt_image_group a  ");
//		//sb.append("  join ynt_image_library ry on a.pk_image_group = ry.pk_image_group and nvl(ry.dr,0) = 0  ");//zpm增加过滤
//		//sb.append("  left join ynt_tzpz_h b on a.pk_image_group=b.pk_image_group and nvl(b.dr,0)=0 ");
//		sb.append(" left join bd_corp bd_corp on bd_corp.pk_corp = a.pk_corp ");
//		return sb.toString();
//	}
//
//	//直接生单
//	@Override
//	public IMageVoucherVO[] batchCreateDirVoucher(String user,String pk_datacorp, DZFDate date, Map<String, String> map)
//			throws DZFWarpException {
//		IMageVoucherVO[] resultBillVOs = null;
//		try{
//			ImageLibraryVO[]  imageVOs = queryLibraryVO(user,pk_datacorp, map);
//			if(imageVOs == null || imageVOs.length == 0)
//				return resultBillVOs;
//
//			imageVOs = filterImgLibResult(imageVOs);
//
//			if(imageVOs == null || imageVOs.length == 0)//参数过滤后再次检验
//				return resultBillVOs;
//			//根据条数再次调整
//			imageVOs = filterImgLibByNum(imageVOs, map);
//
//			//数据中心与在线会计平台针对直接生单方式使用抢占模式,所以保存之前先检验
//			Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
//			checkIsCreated(imageVOs,imageGroupMap);
//			resultBillVOs = new IMageVoucherVO[imageVOs.length];
//			int index = 0;
//			//排序
//			java.util.Arrays.sort(imageVOs, new Comparator<ImageLibraryVO>() {
//
//				@Override
//				public int compare(ImageLibraryVO o1, ImageLibraryVO o2) {
//					int i = o1.getImgname().compareTo(o2.getImgname());
//					return i;
//				}
//
//			});
//            Map<String, IMageVoucherVO> imageVoucherMap = queryImageVoucher(imageVOs);
//
//            HashMap<String, DcModelHVO> modelmap = queryDcModelVO();
//            String pk_curr = yntBoPubUtil.getCNYPk();
//
//			IMageVoucherVO headVO = null;
//			for(ImageLibraryVO imageVO: imageVOs){
//				headVO = createPreVoucherHeadVO(imageVO, user);
//				//根据上传图片摘要信息自动生成凭证分录
//				createPreVoucherBodyVO(headVO, imageGroupMap, imageVO, modelmap, pk_curr, user, date);
//
//				if(imageVoucherMap.containsKey(imageVO.getPk_image_library())){
//					headVO.setAttributeValue("pk_image_voucher", imageVoucherMap.get(imageVO.getPk_image_library()).getAttributeValue("pk_image_voucher"));
//					headVO.setAttributeValue("dr", 0);//表因为Pk_image_library而采用此种方式
//					headVO.setAttributeValue("istogl", DZFBoolean.FALSE);
//					singleObjectBO.update(headVO);
//					resultBillVOs[index++] = headVO;
//				}else{
//					resultBillVOs[index++] = (IMageVoucherVO) singleObjectBO.saveObject(headVO.getPk_corp(), headVO);
//				}
//
//			}
//			//备份图片组表<ynt_image_group_bak>
//			saveImageGroupBackUp(imageGroupMap, PhotoState.state1);
//		}catch(Exception e){
//			if(e instanceof BusinessException){
//				throw new BusinessException(e.getMessage());
//			}else{
//				log.error("错误",e);
//				throw new WiseRunException(e);
//			}
//		}
//		return resultBillVOs;
//	}
//
//	private ImageLibraryVO[] filterImgLibByNum(ImageLibraryVO[] imageVOs, Map<String, String> map){
//		if(StringUtil.isEmptyWithTrim(map.get("num"))){
//			return imageVOs;
//		}else{
//			int num = Integer.parseInt(map.get("num"));
//			num = num >= imageVOs.length ? imageVOs.length : num;
//			ImageLibraryVO[] libvos = new ImageLibraryVO[num];
//			System.arraycopy(imageVOs, 0, libvos, 0, num);
//			return libvos;
//		}
//	}
//
//	private ImageLibraryVO[] filterImgLibResult(ImageLibraryVO[]  imageVOs){
//		Map<String, List<ImageLibraryVO>> imglibVOMap = new HashMap<String, List<ImageLibraryVO>>();
//		Set<String> corpPKSet = new HashSet<String>();
//		int count = 0;
//		for(ImageLibraryVO imgvo : imageVOs){
////			if(count >= BATCH_ONLINE)
////				break;
//
//			String pcorp = imgvo.getPk_corp();
//			if(imglibVOMap.containsKey(pcorp)){
//				imglibVOMap.get(pcorp).add(imgvo);
//			}else{
////				count ++;
//				corpPKSet.add(pcorp);
//				List<ImageLibraryVO> tempList = new ArrayList<ImageLibraryVO>();
//				tempList.add(imgvo);
//				imglibVOMap.put(pcorp, tempList);
//			}
//		}
//		List<ImageLibraryVO> imglibvoList = new ArrayList<ImageLibraryVO>();
//		List<String> corptemp = new ArrayList<String>();
//		for(String corppk : corpPKSet){
//			count++;
//			corptemp.add(corppk);
//			if(count % BATCH_ONLINE == 0 && count != corpPKSet.size()){
//				YntParameterSet[] paramterSets = paramterService.queryParamterbyCodes(corptemp.toArray(new String[0]), PhotoParaCtlState.PhotoParaCtlCode);
//				for(YntParameterSet paramterSet : paramterSets){
//					Integer pardetail = paramterSet.getPardetailvalue();
//					if(pardetail != null && pardetail == PhotoParaCtlState.PhotoParaCtlValue_Sys
//							&& imglibVOMap.containsKey(paramterSet.getPk_corp())){
//						imglibvoList.addAll(imglibVOMap.get(paramterSet.getPk_corp()));
//					}
//				}
//				corptemp = new ArrayList<String>();
//
//			}
//		}
//
//		if(count == corpPKSet.size()){
//			YntParameterSet[] paramterSets = paramterService.queryParamterbyCodes(corptemp.toArray(new String[0]), PhotoParaCtlState.PhotoParaCtlCode);
//			for(YntParameterSet paramterSet : paramterSets){
//				Integer pardetail = paramterSet.getPardetailvalue();
//				if(pardetail != null && pardetail == PhotoParaCtlState.PhotoParaCtlValue_Sys
//						&& imglibVOMap.containsKey(paramterSet.getPk_corp())){
//					imglibvoList.addAll(imglibVOMap.get(paramterSet.getPk_corp()));
//				}
//			}
//		}
//
//		return imglibvoList.toArray(new ImageLibraryVO[0]);
//	}
//
//	private final static String SMALL_TAX = "小规模纳税人";
//
//	public static IMageVoucherVO createPreVoucherBodyVO(IMageVoucherVO headVO, Map<String,
//			ImageGroupVO> imageGroupMap, ImageLibraryVO imageLibVO,
//			HashMap<String, DcModelHVO> map, String pk_currency,
//			String user, DZFDate date){
//		String pk_image_group = imageLibVO.getPk_image_group();
//		if(imageGroupMap.containsKey(pk_image_group)){
//			String pk_corp = headVO.getPk_corp();
//			YntBoPubUtil yntBoPubUtil1 = (YntBoPubUtil) SpringUtils.getBean("yntBoPubUtil");
//			Integer corpschema = yntBoPubUtil1.getAccountSchema(pk_corp);
//			CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//			ImageGroupVO imageGroupVO = imageGroupMap.get(pk_image_group);
//			String fpstylecode = TransVspstyleModel.transVspstyle(imageGroupVO.getMemo(),corpvo.getChargedeptname());//票据类型
//			String szstylecode = TransVspstyleModel.transSzstyle(imageGroupVO.getMemo(),imageGroupVO.getSettlemode());//收支类型
//			String vmemocode = TransVspstyleModel.transVsMemotyle(imageGroupVO.getMemo(),corpschema,corpvo.getChargedeptname());
//			if(!StringUtil.isEmpty(fpstylecode)
//					&& !StringUtil.isEmpty(szstylecode)){
//				String corptype = corpvo.getCorptype();//科目方案
////				String key = corptype + "_" + SMALL_TAX + "_" + fpstylecode + "_" + szstylecode+"_"+vmemocode;//规则为匹配小纳税人模板
//				String key = corptype + "_" + corpvo.getChargedeptname() + "_" + fpstylecode + "_" + szstylecode+"_"+vmemocode;//规则为匹配小纳税人模板
//				IMageVoucherBVO[] bodyVOs = ConvertVouchBVO.createPreVoucherBodyVO(headVO, map, pk_currency,
//						imageGroupVO, user, date, key);
//
//				if(bodyVOs != null && bodyVOs.length > 0){
//					headVO.setChildren(bodyVOs);
//					double creditmny = 0;
//					double debitmny=0;
//					for(IMageVoucherBVO bodyVO: bodyVOs){
//						creditmny += bodyVO.getCreditmny() == null?0:bodyVO.getCreditmny().doubleValue();
//						debitmny += bodyVO.getDebitmny() == null?0:bodyVO.getDebitmny().doubleValue();
//					}
//					headVO.setCreditmny(new DZFDouble(creditmny));
//					headVO.setDebitmny(new DZFDouble(debitmny));
//				}
//
//			}
//
//		}
//		return headVO;
//	}
//	public Map<String, IMageVoucherVO> queryImageVoucher(ImageLibraryVO[] imageVOs){
//		Map<String, IMageVoucherVO> tImageGroupMap = new HashMap<String, IMageVoucherVO>();
//		if(imageVOs == null || imageVOs.length == 0){
//			return tImageGroupMap;
//		}
//		List<String> libpkList = new ArrayList<String>();
//		for(ImageLibraryVO imageVO : imageVOs){
//			libpkList.add(imageVO.getPk_image_library());
//		}
//		String where = SqlUtil.buildSqlForIn("pk_image_library", libpkList.toArray(new String[0]));
//
//		IMageVoucherVO[] headVOs = (IMageVoucherVO[]) singleObjectBO.queryByCondition(IMageVoucherVO.class, where,new SQLParameter());
//		for(IMageVoucherVO tImageVoucher : headVOs){
//			tImageGroupMap.put(tImageVoucher.getPk_image_library(), tImageVoucher);
//		}
//		return tImageGroupMap;
//	}
//	/**
//	 * 备份图片组表
//	 * @param imageGroupMap
//	 * @return
//	 */
//	private void saveImageGroupBackUp(Map<String,ImageGroupVO> imageGroupMap, int type) throws DZFWarpException{
//		SQLParameter params = new SQLParameter();
//		ImageGroupVO imageGroupVO = null;
//		ImageGroupBAKVO imageGroupBAKVO = null;
//		List<ImageGroupBAKVO> imggrpbakList = new ArrayList<ImageGroupBAKVO>();
//		for(Map.Entry<String, ImageGroupVO> entry : imageGroupMap.entrySet()){
//			imageGroupVO = entry.getValue();
//			imageGroupBAKVO = new ImageGroupBAKVO();
//			BeanUtils.copyNotNullProperties(imageGroupVO, imageGroupBAKVO);//赋值操作
//			imageGroupBAKVO.setPrimaryKey("");//赋值操作时,imageGroupVO的主键赋值给了imageGroupBAKVO的主键
//			imggrpbakList.add(imageGroupBAKVO);
//
//			params.clearParams();
//			String sql = "";
//			if(PhotoState.state1 == type){
//				sql =  " update ynt_image_group set istate = ?,isuer = ? where pk_image_group = ? ";
//				params.addParam(PhotoState.state1);//直接生单,已占用
//				params.addParam("Y");
//				params.addParam(imageGroupVO.getPrimaryKey());
//			}else if(PhotoState.state4 == type){//转会计
//				sql =  " update ynt_image_group set istate = ? where pk_image_group = ? ";
//				params.addParam(PhotoState.state4);//转会计
//				params.addParam(imageGroupVO.getPrimaryKey());
//			}else if(PhotoState.state30 == type){
//				sql =  " update ynt_image_group set istate = ? where pk_image_group = ? ";
//				params.addParam(PhotoState.state30);//已识图
//				params.addParam(imageGroupVO.getPrimaryKey());
//			}else if(PhotoState.state80 == type){
//				sql =  " update ynt_image_group set istate = ?,isuer = ?  where pk_image_group = ? ";
//				params.addParam(PhotoState.state80);//已退回
//				params.addParam("N");
//				params.addParam(imageGroupVO.getPrimaryKey());
//			}else if(PhotoState.state100 == type){
//				sql =  " update ynt_image_group set istate = ?  where pk_image_group = ? ";
//				params.addParam(PhotoState.state100);//已生成凭证
//				params.addParam(imageGroupVO.getPrimaryKey());
//			}else{}
//			if(!"".equals(sql)){
//				singleObjectBO.executeUpdate(sql, params);
//			}
//
//		}
//		ImageGroupBAKVO[] imggrpbakVOs = imggrpbakList.toArray(new ImageGroupBAKVO[0]);
//		singleObjectBO.insertVOArr(imggrpbakVOs[0].getPk_corp(), imggrpbakVOs);
//
//	}
//	/**
//	 * 校验上传图片是否已生单
//	 * @param imageVOs
//	 * @return
//	 *
//	 */
//	private void checkIsCreated(ImageLibraryVO[]  imageVOs,Map<String, ImageGroupVO> imageGroupMap){
//		List<String> imggrppks = new ArrayList<String>();
//		for(ImageLibraryVO imglibvo : imageVOs){
//			imggrppks.add(imglibvo.getPk_image_group());
//		}
//		String grppks = SqlUtil.buildSqlForIn("pk_image_group", imggrppks.toArray(new String[0]));
//		StringBuffer sfwhere = new StringBuffer();
//		sfwhere.append(" nvl(dr,0) = 0 and ")
//				.append(grppks);
//		ImageGroupVO[] imggrpvos = (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class, sfwhere.toString(), null);
//		for(ImageGroupVO groupVO : imggrpvos){
//			imageGroupMap.put(groupVO.getPk_image_group(), groupVO);
//			if(DZFBoolean.TRUE == groupVO.getIsuer()){
//				throw new BusinessException("图片组"+ groupVO.getGroupcode() + "已直接生单,不能再次生单!");
//			}
//		}
//
//	}
//
//	public ImageLibraryVO[] queryLibraryVO(String user,String pk_datacorp, Map<String, String> dirmap)throws DZFWarpException {
//		SQLParameter params = new SQLParameter();
//		StringBuffer sf = new StringBuffer();
//		String concatf = getConWhereSql(dirmap, params);
//		sf.append(" select ry.* from ynt_image_group ep ");
//		sf.append(" join ynt_image_library ry on ep.pk_image_group = ry.pk_image_group ");
//		sf.append(" left join bd_corp bd on bd.pk_corp = ep.pk_corp  ");
//		sf.append(" where ");
//		sf.append(concatf);
//		sf.append(" ep.istate = ? and nvl(ep.sourcemode,0) != ? and nvl(ep.dr,0) = 0 and nvl(ry.dr,0) = 0 ");
//		sf.append(" and ep.pk_corp in ( ");
//		sf.append(getDirvouchersql());
//		sf.append(" ) ");// order by ep.pk_image_group,ry.imgname
//
//		params.addParam(PhotoState.state0);
//		params.addParam(PhotoState.SOURCEMODE_05);//过滤来源票通
//		params.addParam(pk_datacorp);
//		List<ImageLibraryVO> list = (List<ImageLibraryVO>)singleObjectBO.executeQuery(sf.toString(), params, new BeanListProcessor(ImageLibraryVO.class));
//		if(list == null || list.size() == 0)
//			return null;
//		Map<String,ImageLibraryVO> map =new HashMap<String,ImageLibraryVO>();
//		String imagegroup = null;
//		for(ImageLibraryVO z : list){
//			imagegroup = z.getPk_image_group();
//			if(!map.containsKey(imagegroup)){
//				map.put(imagegroup, z);
//			}
//		}
//		return map.values().toArray(new ImageLibraryVO[0]);
//	}
//
//	private String getConWhereSql(Map<String, String> map, SQLParameter sp){
//		StringBuffer sff = new StringBuffer();
//		if(!StringUtil.isEmptyWithTrim(map.get("unitcode"))){
//			sff.append("bd.unitcode = ? and ");
//			sp.addParam(map.get("unitcode"));
//		}
//		if(!StringUtil.isEmptyWithTrim(map.get("from"))){
//			sff.append("ep.cvoucherdate >= ? and ");
//			sp.addParam(map.get("from"));
//		}
//		if(!StringUtil.isEmptyWithTrim(map.get("to"))){
//			sff.append("ep.cvoucherdate <= ? and ");
//			sp.addParam(map.get("to"));
//		}
//		return sff.toString();
//	}
//
//  private String getDirvouchersql(){
//	  StringBuffer sf = new StringBuffer();
//	  sf.append(" select t1.pk_corp ");
//	  sf.append("   from bd_corp t1 ");
//	  sf.append("   join sm_datacc t2 ");
//	  sf.append("     on t1.fathercorp = t2.pk_acccorp ");
////	  sf.append("   join ynt_parameter yp ");
////	  sf.append("     on t1.pk_corp = yp.pk_corp ");
//	  sf.append("  where t2.pk_datacorp = ? ");
//	  sf.append("    and nvl(t1.dr, 0) = 0 ");
//	  sf.append("    and nvl(t2.dr, 0) = 0 ");
////	  sf.append("    and yp.parameterbm = '");
////	  sf.append(PhotoParaCtlState.PhotoParaCtlCode);
////	  sf.append("'");
////	  sf.append("    and yp.issync = 0 ");
////	  sf.append("    and nvl(yp.dr, 0) = 0 ");
////	  sf.append("    and yp.pardetailvalue = ");
////	  sf.append(PhotoParaCtlState.PhotoParaCtlValue_Sys);
//	  return sf.toString();
//  }
//  	/**
//	 * 判断上传的期间是否损益结转
//	 */
//	private void checkIsQjSyJz(String pk_corp,String cvoucherdate) throws DZFWarpException{
//		SQLParameter sp = new SQLParameter();
//		if(!StringUtil.isEmpty(pk_corp) && !StringUtil.isEmpty(cvoucherdate)){
//			StringBuilder sbCode = new StringBuilder( "select count(1) from  ynt_qmcl where pk_corp=? and period=? and nvl(dr,0) = 0 and nvl(isqjsyjz,'N')='Y'");
//			sp.addParam(pk_corp);
//			sp.addParam(cvoucherdate);
//			BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp, new ColumnProcessor());
//
//			if (repeatCodeNum.intValue() > 0) {
//				throw new BusinessException("该月份已经结转损益，请修改上传月份！");
//			}
//		}else{
//			throw new BusinessException("上传图片失败！");
//		}
//
//	}
//	/**
//	 * 检查上传图片期间是否在建账前
//	 */
//	private void checkIsAccount(String pk_corp, String qj, String uploader) throws DZFWarpException{
//		if(!StringUtil.isEmpty(pk_corp) && !StringUtil.isEmpty(qj)){
////			CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
//			CorpVO corpVO = CorpCache.getInstance().get(uploader, pk_corp);
//			DZFDate begindate = corpVO.getBegindate();//建账日期
////			int beginYear = begindate.getYear();
////			int beginMonth = begindate.getMonth();
////			int year = Integer.valueOf(qj.substring(0, 4));//期间截取
////			int month = Integer.valueOf(qj.substring(5, 7));
//			DZFDate qjdate = DateUtils.getPeriodEndDate(qj);
//			int compareInt = qjdate.compareTo(begindate);
////			if(year > beginYear || (year == beginYear && month >= beginMonth)){
//			if(compareInt >= 0){
//				log.info("上传凭证期间大于正式签约日期");
//			}else{
//				throw new BusinessException("上传凭证期间不可小于正式签约日期！");
//			}
//		}else{
//			throw new BusinessException("上传图片失败！");
//		}
//	}
//
//	@Override
//	public List<ImageGroupVO> queryImgGroupvo(ReportBeanVO uBean) throws DZFWarpException {
//		StringBuffer gpsql = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		gpsql.append(" SELECT * FROM YNT_IMAGE_GROUP ");
//		gpsql.append("  WHERE  1=1 ");
//		if(!StringUtil.isEmpty(uBean.getAccount_id())){
//			gpsql.append("   and (COPERATORID = ?  or vapprovetor = ?)    ");
//			sp.addParam(uBean.getAccount_id());
//			sp.addParam(uBean.getAccount_id());
//		}
//		if(uBean.getStartdate()!=null){
//			gpsql.append(" AND DOPERATEDATE>=? ");
//			sp.addParam(DateUtils.getPeriodStartDate(uBean.getStartdate()));
//		}
//		if(uBean.getEnddate()!=null){
//			gpsql.append(" AND DOPERATEDATE<=? ");
//			sp.addParam(DateUtils.getPeriodEndDate(uBean.getEnddate()));
//		}
//		gpsql.append("   AND PK_CORP = ? AND NVL(DR,0)=0  ");
//		sp.addParam(uBean.getPk_corp());
//		if(!StringUtil.isEmpty(uBean.getGroupkey())){
//			gpsql.append(" and pk_image_group = ?  ");
//			sp.addParam(uBean.getGroupkey());
//		}
//		gpsql.append("   ORDER BY ts desc ");
//		List<ImageGroupVO> groups = (ArrayList<ImageGroupVO>)singleObjectBO.executeQuery(gpsql.toString(), sp, new BeanListProcessor(ImageGroupVO.class));
//		return groups;
//	}
//
//	@Override
//	public ImageQueryBean[] queryImageWithNonDr(String pk_image_group) throws Exception {
//		// 不能带isnull(dr,0)=0的条件，因为退回重拍的图片，图片已经设置为删除状态，dr已经不为0了
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_image_group);
//		String sql = "select * from ynt_image_group where pk_image_group= ? ";
//		List<ImageGroupVO> groupVOs = (List<ImageGroupVO>)singleObjectBO.executeQuery(sql, sp,new BeanListProcessor(ImageGroupVO.class));
//		if(groupVOs == null || groupVOs.size() == 0) return null;
//
//		int index = 0;
//		for(SuperVO svo : groupVOs){
//			ImageGroupVO groupVO = (ImageGroupVO)svo;
//			sp.clearParams();
//			sp.addParam(groupVO.getPrimaryKey());
//			sql = "select * from ynt_image_library where pk_image_group= ? and nvl(dr,0) = 0 order by imgname";
//			List<ImageLibraryVO> imageVOs =  (List<ImageLibraryVO>)singleObjectBO.executeQuery(sql, sp,new BeanListProcessor(ImageLibraryVO.class));
//			groupVO.setChildren(imageVOs == null || imageVOs.size() == 0 ? null: imageVOs.toArray(new ImageLibraryVO[imageVOs.size()]));
//
//		}
//
//		ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);
//		ImageQueryBean[] vos = ct.fromVO(groupVOs.toArray(new ImageGroupVO[0]));
//		return vos;
//	}
//
//	@Override
//	public ImageTurnMsgVO[] queryReturnmsgHeadVOs(String loginCorp, String strWhere) throws Exception {
//		StringBuffer qrysql =new StringBuffer();
//		if (StringUtil.isEmpty(strWhere)) strWhere = "1=0";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(loginCorp);
//		qrysql.append(" select ynt_imgreturnmsg.* from ynt_imgreturnmsg   ");
//		qrysql.append( " left join bd_corp bd_corp on bd_corp.pk_corp = ynt_imgreturnmsg.pk_corp ");
//		qrysql.append( " left join sm_datacc sm_datacc on sm_datacc.pk_acccorp = bd_corp.fathercorp ");
//		qrysql.append( "  where   sm_datacc.pk_datacorp = ? and  "+strWhere );
//
//		List<ImageTurnMsgVO> listvos = (List<ImageTurnMsgVO>)singleObjectBO.executeQuery(qrysql.toString(), sp,new BeanListProcessor(ImageTurnMsgVO.class));
//
//		return listvos.toArray(new ImageTurnMsgVO[0]);
//	}
//


}

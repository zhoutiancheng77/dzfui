package com.dzf.zxkj.app.service.photo.impl;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.dzf.zxkj.app.model.app.remote.AppCorpCtrlVO;
import com.dzf.zxkj.app.model.image.ImageUploadRecordVO;
import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ImageReqVO;
import com.dzf.zxkj.app.model.resp.bean.ReportBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.model.sys.OcrInvoiceVOForApp;
import com.dzf.zxkj.app.pub.ImageBeanConvert;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.MarkConstant;
import com.dzf.zxkj.app.service.app.act.IAppApproveService;
import com.dzf.zxkj.app.service.app.act.IAppBusinessService;
import com.dzf.zxkj.app.service.photo.IImageProviderPhoto;
import com.dzf.zxkj.app.service.photo.IImagePubQryService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.*;
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
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * 322 图片处理
 * @author zhangj
 *
 */
@Slf4j
@Service("poimp_imagepro322")
public class ImageProvider322Impl implements IImageProviderPhoto {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAppPubservice apppubservice;
	@Autowired
	private IImagePubQryService imageqry_pub;


	@Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
	private IZxkjRemoteAppService iZxkjRemoteAppService;



//	@Autowired
//	private IImageGroupService gl_pzimageserv;
//
//	@Autowired
//	private IQmgzService qmgzService;
//
//	@Autowired
//	private IUserService userServiceImpl;

	@Override
	public ImageQueryBean[] saveUploadImages(UserBeanVO uBean, MultipartFile file, String filenames, InputStream file_in) throws DZFWarpException {
		return null;
	}

	/**
	 * 重传图片
	 * @param uBean
	 * @param imgbeanvos
	 * @param files
	 * @param filenames
	 * @return
	 * @throws DZFWarpException
	 */
	public int saveReuploadImage(UserBeanVO uBean, ImageBeanVO[] imgbeanvos, MultipartFile file,String filename, InputStream file_in)
			throws DZFWarpException {

		if(StringUtil.isEmpty(uBean.getGroupkey())){
			throw new BusinessException("图片组信息不存在!");
		}

//		ImageGroupVO groupvo = (ImageGroupVO) singleObjectBO.queryObject(uBean.getGroupkey(), new Class[]{ImageGroupVO.class,ImageLibraryVO.class});

		SQLParameter sp = new SQLParameter();

		sp.addParam(uBean.getGroupkey());

		List<ImageGroupVO> gpvolist =  (List<ImageGroupVO>) singleObjectBO.executeQuery(" pk_image_group = ? ", sp, new Class[]{ImageGroupVO.class, ImageLibraryVO.class});//.queryObject( beanvo.getGroupkey(), new Class[]{ImageGroupVO.class,ImageLibraryVO.class});

		if(gpvolist == null  || gpvolist.size() == 0){
			throw new BusinessException("图片信息不存在!");
		}


		ImageGroupVO groupvo = gpvolist.get(0);

		ImageLibraryVO[] olibvos = (ImageLibraryVO[]) groupvo.getChildren();

		ImageProviderImpl poimp_imagepro = new ImageProviderImpl();

		String uploadlot = uBean.getCorpname().toLowerCase();

		List<String> items = new ArrayList<String>();


			//先删除再重新的新增
			File imgzipFile = poimp_imagepro.SaveZipFile(file,file_in, uploadlot+File.separator+filename,uBean.getSourcesys(),uBean.getCert(),uBean.getAccount(),"","");
			// 解压缩文件
			HashMap<String, String> groupSettle = new HashMap<String, String>();
			Map<String, String> groupLibMap = new HashMap<String, String>();
			//解压文件生成lib数据
			ImageLibraryVO[] libvos =  unzipFileGenLib(groupvo,imgzipFile, uBean.getPk_corp(), DZFBoolean.valueOf(uBean.getBdata()).booleanValue(),groupSettle,groupLibMap,uBean.getCert(),uBean.getAccount_id());
			//查询对应的数组信息
			if (libvos != null && libvos.length > 0){
				String[] itemkeys = singleObjectBO.insertVOArr(groupvo.getPrimaryKey(), libvos);
				for(String str:itemkeys){
					items.add(str);
				}
			}

		String delimagekeys = (String) uBean.getItems();//不需要删除的keyid
		sp.clearParams();
		if(!StringUtil.isEmpty(delimagekeys)){
			String[] libkeys= delimagekeys.split(",");
			for(String str : libkeys){
				items.add(str);
			}
		}

		for(ImageLibraryVO libvo:olibvos){
			if(!StringUtil.isEmpty(uBean.getSourcesys()) && uBean.getSourcesys().equals(ISysConstants.SYS_ADMIN)){
				if(items.contains(libvo.getPrimaryKey())){//删除
					libvo.setDr(1);
				}else{
					libvo.setIsback(DZFBoolean.FALSE);//保留
				}
			}else{
				if(items.contains(libvo.getPrimaryKey())){//保留
					libvo.setIsback(DZFBoolean.FALSE);
				}else{
					libvo.setDr(1);//删除
				}
			}
		}

		singleObjectBO.updateAry(olibvos, new String[]{"isback","dr"});


		groupvo.setIsskiped(DZFBoolean.FALSE);
		groupvo.setIsuer(DZFBoolean.FALSE);//设置为未使用
		groupvo.setIstate(PhotoState.state0);
		groupvo.setVapprovetor(null);//图片重传(重新走审批流)

		groupvo.setSettlemode(uBean.getPaymethod());
		groupvo.setMemo(uBean.getMemo());//摘要
		groupvo.setMny(new DZFDouble(uBean.getMny()));
		groupvo.setMemo1(uBean.getMemo1());//备注
		if(uBean.getKpdate()!=null){
			groupvo.setCvoucherdate(new DZFDate(uBean.getKpdate()));//开票日期
		}

		singleObjectBO.update(groupvo);

		// 消息处理
		iZxkjRemoteAppService.deleteMsg(groupvo);

		// 判断是否需要走审批流
		if(!ISysConstants.SYS_ADMIN.equals(uBean.getSourcesys())){
			IAppApproveService appapprovehand = (IAppApproveService) SpringUtils.getBean("appapprovehand");
			BusiReqBeanVo busibean = new BusiReqBeanVo();
			BeanUtils.copyNotNullProperties(uBean, busibean);
			busibean.setPk_image_group(groupvo.getPk_image_group());
			appapprovehand.updateApprove(busibean,singleObjectBO);
		}

		sp.clearParams();
		sp.addParam(groupvo.getPrimaryKey());
		List<ImageGroupVO> gpvolist1 =  (List<ImageGroupVO>) singleObjectBO.executeQuery(" pk_image_group = ? ", sp, new Class[]{ImageGroupVO.class,ImageLibraryVO.class});//.queryObject( beanvo.getGroupkey(), new Class[]{ImageGroupVO.class,ImageLibraryVO.class});
		groupvo =  gpvolist.get(0);
		if(groupvo.getIstate() == null || groupvo.getIstate() == PhotoState.state0){
			IAppBusinessService appbusihand = (IAppBusinessService) SpringUtils.getBean("appbusihand");
			appbusihand.saveVoucherFromPic(groupvo.getPrimaryKey(), groupvo.getPk_corp(),singleObjectBO);
		}

		return 1;
	}


	/**
	 * 解压缩文件
	 * @param zipfile
	 * @param pk_corp
	 * @param isCompress
	 * @return
	 * @throws DZFWarpException
	 */
	protected ImageLibraryVO[] unzipFileGenLib(ImageGroupVO groupvo ,File zipfile, String pk_corp1, boolean isCompress,HashMap<String, String> groupSettle, Map<String, String> groupLibMap, String cert,String uploader) throws DZFWarpException{
		List<ImageLibraryVO> libvos = new ArrayList<ImageLibraryVO>();
		try{
			int childcont = groupvo.getChildren().length;
			List<Integer> imgnames = new ArrayList<Integer>();
			for(int i = 0;i<childcont;i++){
				ImageLibraryVO tempvp = (ImageLibraryVO) groupvo.getChildren()[i];
				String tempstr = tempvp.getImgname().split("-")[1];
				imgnames.add(Integer.parseInt(tempstr.substring(0, tempstr.indexOf("."))));
			}
			Collections.sort(imgnames);

			DZFDate date = new DZFDate();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
			String unitcode;
			if(Common.tempidcreate.equalsIgnoreCase(pk_corp1)){
				unitcode=pk_corp1;
			}else{
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
			HashMap<String, String> smallGroupLibMap = new HashMap<String, String>();
			HashMap<String, String> midGroupLibMap = new HashMap<String, String>();
			List<ZipEntry> list  = new ArrayList<ZipEntry>();
			ZipEntry entrye = null;
			while(enumeration.hasMoreElements()){
				entrye = (ZipEntry) enumeration.nextElement();
				list.add(entrye);
			}
			ImageLibraryVO imageVO= null;
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

    		String smallImgePath = null;
    		String middleImagePath = null;

    		String imgsuffix = null;
    		File imagefile = null;

    		BufferedInputStream in = null;
    		FileOutputStream fileout = null;
    		BufferedOutputStream out = null;
			//排序
    		File basedir = new File(Common.imageBasePath);
			for(ZipEntry entry :list){
				imageVO = new ImageLibraryVO();
				nameArray = entry.getName().split("-");
				if(nameArray.length != 2){
					throw new BusinessException("压缩包中文件的文件名必须是[图片组名-图片文件名]格式的字符串");
				}
				groupKey = nameArray[0];
				if(groupKey.contains("/")){
					groupKey=groupKey.substring(groupKey.indexOf("/")+1);
				}
				if(!groupmap.containsKey(groupKey))
					groupmap.put(groupKey,groupvo.getGroupcode());
				// 图片组名
				groupname = groupmap.get(groupKey);
				if(!resultmap.containsKey(groupname)){//付款方式-----已由新接口走，已不从这里走了。数据中心没有付款方式。
					resultmap.put(groupname, new ArrayList<String>());
					groupSettle.put(groupname, groupKey);
				}
				// 文件名
				imgsuffix = ImageHelper.getFileExt(entry.getName());
				imgvirname = getImageName(groupname,GetNextNumber(imgnames.toArray(new Integer[0]))) + imgsuffix;
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
					}
				}
				// 是否需要压缩
				if(isCompress){
					ImageHelper.compressImage(imagename).writeToFile(new File(imagename), 0.5f);
				}
				smallImgePath = smallGroupLibMap.get(imagefile.getAbsolutePath());
				middleImagePath = midGroupLibMap.get(imagefile.getAbsolutePath());

				imageVO.setPk_corp(groupvo.getPk_corp());
				imageVO.setCoperatorid(groupvo.getCoperatorid());
				imageVO.setDoperatedate(groupvo.getDoperatedate());
				imageVO.setCvoucherdate(groupvo.getCvoucherdate());
				imageVO.setImgname(imgvirname);//名称
				imageVO.setImgpath(imagefile.getAbsolutePath().replace(basedir.getAbsolutePath() + File.separator, ""));   // 去掉根目录路径
				imageVO.setSmallimgpath(StringUtil.isEmpty(smallImgePath)? "" : smallImgePath.replace(basedir.getAbsolutePath() + File.separator, ""));

				imageVO.setMiddleimgpath(StringUtil.isEmpty(middleImagePath)? "" : middleImagePath.replace(basedir.getAbsolutePath() + File.separator, ""));

				imageVO.setPk_image_group(groupvo.getPrimaryKey());
				imgnames.add(Integer.parseInt(imageVO.getImgname().split("-")[1].replace(imgsuffix, "")) );
				libvos.add(imageVO);
			}
			if(zip != null){
				zip.close();
			}
		} catch(Exception e){
			throw new WiseRunException(e);
		}
		return libvos.toArray(new ImageLibraryVO[0]);
	}
		 public static int GetNextNumber(Integer[] iNumList)
     {
         for (int i = 0, j = 1; j < iNumList.length - 1; i++, j++)
         {
             //如果出现断号，则补齐断号
             if ((iNumList[j] - iNumList[i]) > 1)
             {
                 return iNumList[i] + 1;
             }
         }
         return iNumList[iNumList.length - 1] + 1;
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
	@Override
	public ImageUploadRecordVO[] queryImages(ImageBeanVO beanvo) throws DZFWarpException {

		ImgGroupRsBean[] beans = new ImgGroupRsBean[1];
		//查询当前的图片id
		if(StringUtil.isEmpty(beanvo.getGroupkey())){
			throw new BusinessException("图片信息不存在!");
		}

		SQLParameter sp = new SQLParameter();
		sp.addParam(beanvo.getGroupkey());
		List<ImageGroupVO> gpvolist =  (List<ImageGroupVO>) singleObjectBO.executeQuery(" pk_image_group = ? ", sp, new Class[]{ImageGroupVO.class,ImageLibraryVO.class});//.queryObject( beanvo.getGroupkey(), new Class[]{ImageGroupVO.class,ImageLibraryVO.class});

		if(gpvolist == null  || gpvolist.size() == 0){
			throw new BusinessException("图片信息不存在!");
		}

		ImageGroupVO gpvo = gpvolist.get(0);

		ImageLibraryVO[] libvos = new ImageLibraryVO[gpvo.getChildren().length];

		for(int i =0;i<gpvo.getChildren().length;i++){
			libvos[i] = (ImageLibraryVO) gpvo.getChildren()[i];
		}

		gpvo.setChildren(libvos);

		ImageBeanConvert ct = new ImageBeanConvert(singleObjectBO);

    	beans = ct.fromImgHisVO(new ImageGroupVO[]{gpvo});

    	if(beans[0].getMny() == null){
    		beans[0].setMny("");
    	}

    	beans[0].setMny(new DZFDouble(beans[0].getMny()).setScale(2, DZFDouble.ROUND_HALF_UP).toString());

		StringBuffer recordsql = new StringBuffer();
		recordsql.append(" select sm.user_name as vcurapprovetor ,ar.doperatedate,ar.vapprovemsg, ");
		recordsql.append(" case  ar.vapproveope when 'commit' then '上传发票' ");
		recordsql.append(" when 'approve' then '审批通过' ");
		recordsql.append(" when 'appreturn' then '驳回'   when 'return' then '退回' when 'voucher' then '制单' else '未知'    end vapproveope ");
		recordsql.append(" from "+ImageRecordVO.TABLE_NAME+"  ar  ");
		recordsql.append(" inner join sm_user sm on ar.vcurapprovetor =  sm.cuserid ");
		recordsql.append(" where nvl(ar.dr,0)=0 and nvl(sm.dr,0)=0");
		recordsql.append("  and ar.pk_image_group = ?  " );
		recordsql.append(" order by ar.doperatedate desc,ar.vapproveope desc  ");

		List<ImageRecordVO> records = (List<ImageRecordVO>) singleObjectBO.executeQuery(recordsql.toString(), sp, new BeanListProcessor(ImageRecordVO.class));

		records =  (List<ImageRecordVO>) QueryDeCodeUtils.decKeyUtils(new String[]{"vcurapprovetor"}, records, 1);

		if(records!=null && records.size()>0){
			beans[0].setImgrecord(records.toArray(new ImageRecordVO[0]));
		}

		ReportBeanVO reportbean = new ReportBeanVO();

		BeanUtils.copyNotNullProperties(beanvo, reportbean);

		reportbean.setGroupkey(beanvo.getGroupkey());

		//获取权限
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(gpvo.getPk_corp());

		List<ImageGroupVO> gplist = new ArrayList<ImageGroupVO>();

		gplist.add(gpvo);

		Map<String, AppCorpCtrlVO> mapcorp= apppubservice.queryCorpCtrl(cpvo.getFathercorp(), reportbean.getAccount_id());

		Map<String,Integer> imgmap =  imageqry_pub.queryUserImgPower(mapcorp, gplist);

		ImageUploadRecordVO recodevo = new ImageUploadRecordVO();

		BeanUtils.copyNotNullProperties(beans[0], recodevo);

		recodevo = createImageRecord(recodevo,beanvo.getAccount_id(), gpvo,imgmap);

		if(recodevo.getChildren()!=null && recodevo.getChildren().length>0){
			String key = "";
			for(SuperVO vo:recodevo.getChildren()){
				key = (String) vo.getAttributeValue("imagePath");
				if(!StringUtil.isEmpty(key)){
					vo.setAttributeValue("imagePath", CryptUtil.getInstance().encryptAES(key));
				}
			}
		}

		if(recodevo!=null ){
			beans[0].setApprovemsg(recodevo.getApprovemsg());
			beans[0].setBhand(recodevo.getBhand());
		}

		return new ImageUploadRecordVO[]{recodevo};

	}
	private ImageUploadRecordVO createImageRecord(ImageUploadRecordVO rdvo,String account_id , ImageGroupVO gp,Map<String,Integer> imgmap){
		CorpVO cpvo = iZxkjRemoteAppService.queryByPk(gp.getPk_corp());
		UserVO ucvo = iZxkjRemoteAppService.queryUserJmVOByID(gp.getCoperatorid()) ;//UserCache.getInstance().get(gp.getCoperatorid(), "");
		rdvo.setImgcounts(gp.getImagecounts()+"");
		rdvo.setMemo("摘要"+MarkConstant.MH_ZH + AppStringUtils.getNullValue(gp.getMemo()));//
		rdvo.setMemo1("备注"+MarkConstant.MH_ZH+AppStringUtils.getNullValue(gp.getMemo1()));//备注
		rdvo.setPaymethod("结算方式"+MarkConstant.MH_ZH+AppStringUtils.getNullValue(gp.getSettlemode()));//
		rdvo.setL_uploadtime(gp.getCvoucherdate()== null ? gp.getDoperatedate().getMillis():gp.getCvoucherdate().getMillis());//毫秒
		rdvo.setUploadtime(gp.getCvoucherdate() == null ? gp.getDoperatedate().toString():gp.getCvoucherdate().toString());//上传时间
		rdvo.setL_uploadtime_ser(gp.getTs().getMillis());//服务器上传时间
		rdvo.setImagegroupid(gp.getPrimaryKey());
		rdvo.setUp_cname(cpvo.getUnitname());
		rdvo.setUp_corpid(gp.getPk_corp());//上传公司id
		rdvo.setUp_ope("上传人"+MarkConstant.MH_ZH+(ucvo == null? "空":ucvo.getUser_name()));
		gp.setMny(gp.getMny() == null ? DZFDouble.ZERO_DBL:gp.getMny());
		rdvo.setMny("金额"+MarkConstant.MH_ZH+gp.getMny().setScale(2, DZFDouble.ROUND_HALF_UP).toString());
		//图片状态设置
		rdvo.setImgState(gp.getIstate()+"");
		rdvo.setImagesource(!StringUtil.isEmpty(gp.getPk_ticket_h()) ? 0 : 1);
		//概览图片URL
		String sql = "SELECT * FROM YNT_IMAGE_LIBRARY WHERE PK_IMAGE_GROUP =? AND ROWNUM=1 and nvl(dr,0)=0 ORDER BY IMGNAME";
		SQLParameter psp = new SQLParameter();
		psp.addParam(gp.getPk_image_group());
		ImageLibraryVO lib =(ImageLibraryVO)singleObjectBO.executeQuery(sql, psp, new BeanProcessor(ImageLibraryVO.class));
		if(lib!=null){
			rdvo.setReviewpath(CryptUtil.getInstance().encryptAES(lib.getImgpath()) );
		}
		rdvo.setBhand(IConstant.FIRDES);//是否需要本人处理
		UserVO uvo = null;
	    if (PhotoState.state200 == gp.getIstate().intValue()){//非本人的需要展示
	    	if(!StringUtil.isEmpty(gp.getVapprovetor()) && account_id.equals(gp.getVapprovetor())){
	    		rdvo.setBhand(IConstant.DEFAULT);
	    		uvo = iZxkjRemoteAppService.queryUserJmVOByID(gp.getCoperatorid());// UserCache.getInstance().get(gp.getCoperatorid(), "");
	    		rdvo.setApprovemsg("申请人"+ MarkConstant.MH_ZH+ (uvo == null ? "空":uvo.getUser_name()));
	    	}else if(!StringUtil.isEmpty(gp.getVapprovetor()) && gp.getCoperatorid().equals(account_id)){
	    		rdvo.setBhand(IConstant.FIRDES);
	    		uvo = iZxkjRemoteAppService.queryUserJmVOByID(gp.getVapprovetor()); //UserCache.getInstance().get(gp.getVapprovetor(), "");
	    		rdvo.setApprovemsg("审核人"+MarkConstant.MH_ZH+ (uvo == null ? "空" :uvo.getUser_name()));
	    	}else{
	    		rdvo.setBhand(IConstant.FIRDES);//是否需要操作
	    		uvo = iZxkjRemoteAppService.queryUserJmVOByID(gp.getCoperatorid()); //UserCache.getInstance().get(gp.getCoperatorid(), "");
	    		rdvo.setApprovemsg("申请人"+MarkConstant.MH_ZH+ (uvo == null ? "空":uvo.getUser_name()));
	    	}
		}else if(PhotoState.state0 == gp.getIstate().intValue()){
			rdvo.setApprovemsg("未处理");
		}else if(PhotoState.state80 == gp.getIstate().intValue()){
			rdvo.setApprovemsg("已退回");
			if(!StringUtil.isEmpty(gp.getCoperatorid())  && gp.getCoperatorid().equals(account_id)){
				rdvo.setBhand(IConstant.DEFAULT);//需要重新编辑
			}
			//查询退回原因
			String returnsql = "select * from ynt_imgreturnmsg where nvl(dr,0)=0 and pk_image_group=? order by ts desc ";
			psp.clearParams();
			psp.addParam(gp.getPk_image_group());
			List<ImageTurnMsgVO> turnlist =  (List<ImageTurnMsgVO>) singleObjectBO.executeQuery(returnsql, psp, new BeanListProcessor(ImageTurnMsgVO.class));
		    if(turnlist!=null && turnlist.size()>0 ){
		    	rdvo.setBackReason(turnlist.get(0).getMessage());
		    }
		}else if(PhotoState.state201 == gp.getIstate().intValue()){
			rdvo.setApprovemsg("已驳回");
			if(gp.getCoperatorid().equals(account_id)){
				rdvo.setBhand(IConstant.DEFAULT);//需要重新编辑
			}
		}else if(PhotoState.state100 == gp.getIstate().intValue()){
			rdvo.setApprovemsg("已制证");
		}else if(PhotoState.state101 == gp.getIstate().intValue()){
			rdvo.setApprovemsg("暂存中");
		}else{
			rdvo.setApprovemsg("处理中");
		}

	    if(imgmap.containsKey(gp.getPrimaryKey())){
	    	rdvo.setOpe_power(imgmap.get(gp.getPrimaryKey()));
	    }else{
	    	rdvo.setOpe_power(0);
	    }
	    return rdvo;
	}


	@Override
	public ImageUploadRecordVO[] queryUploadRecord(ImageReqVO uBean) throws DZFWarpException {
		DZFDate startdate = handlerdate(uBean.getStartdate(),DZFBoolean.TRUE);
		DZFDate enddate = handlerdate(uBean.getEnddate(),DZFBoolean.FALSE);
		String[] corpids = AppCheckValidUtils.isEmptyCorp(uBean.getPk_corp()) ? uBean.getCorpids()
				: new String[] { uBean.getPk_corp() };//公司ids
		StringBuffer wherepart = new StringBuffer();
		if (!StringUtil.isEmpty(uBean.getSourcesys()) && uBean.getSourcesys().equals(ISysConstants.SYS_ADMIN)) {//不显示审核，退回的图片
			if(!StringUtil.isEmpty(uBean.getImg_state())){
				wherepart.append(" and istate = " + uBean.getImg_state());
			}else{
				wherepart.append(" and istate in ( "+PhotoState.state0 + " ,"+PhotoState.state80+","+PhotoState.state100+ ","+PhotoState.state101+ ") ");
			}
		}else{
			wherepart.append(" and  (coperatorid = '"+uBean.getAccount_id()+"' or vapprovetor = '"+uBean.getAccount_id()+"')");
		}
		if (corpids == null || corpids.length == 0) {
			throw new BusinessException("您公司没签约，查询数据为空");
		}
		List<ImageGroupVO> groups = imageqry_pub.queryImgGroupvo(corpids, null,
				startdate, enddate, uBean.getGroupkey(),wherepart.toString());

		if (groups == null || groups.size() <= 0) {
			throw new BusinessException("暂无记录!");
		}

		List<ImageUploadRecordVO> appvos = new ArrayList<ImageUploadRecordVO>();// 需要您审核的人

		List<ImageUploadRecordVO> recordvos = new ArrayList<ImageUploadRecordVO>();// 待处理的人

		ImageUploadRecordVO rdvo = null;

		//获取权限
		Map<String,AppCorpCtrlVO> mapcorp= apppubservice.queryCorpCtrl(uBean.getFathercorpid(), uBean.getAccount_id());

		Map<String,Integer> imgmap =  imageqry_pub.queryUserImgPower(mapcorp, groups);

		for (ImageGroupVO gp : groups) {

			if (gp.getImagecounts() == null) {
				gp.setImagecounts(0);
			}

			rdvo = new ImageUploadRecordVO();

			rdvo = createImageRecord(rdvo,uBean.getAccount_id(), gp,imgmap);

			if (PhotoState.state200 == gp.getIstate().intValue()) {
				appvos.add(rdvo);
			} else {
				recordvos.add(rdvo);
			}

		}
		List<ImageUploadRecordVO> resvos = new ArrayList<ImageUploadRecordVO>();

		// 待审核的信息
		if (appvos.size() > 0) {
			for (ImageUploadRecordVO temp : appvos) {
				resvos.add(temp);
			}
		}

		if (recordvos.size() > 0) {
			for (ImageUploadRecordVO temp : recordvos) {
				resvos.add(temp);
			}
		}
		return resvos.toArray(new ImageUploadRecordVO[0]);
	}
	private DZFDate handlerdate(String datestr,DZFBoolean isstart) {
		DZFDate date = null;
		if(!StringUtil.isEmpty(datestr)){
			if(datestr.length()==10){
				date = new DZFDate(datestr);
			}else{
				if(isstart!=null && isstart.booleanValue()){
					date = DateUtils.getPeriodStartDate(datestr);
				}else{
					date = DateUtils.getPeriodEndDate(datestr);
				}
			}
		}
		return date;
	}
	public void downloadImage(String filePath, OutputStream out, ImageParamBeanVO[] paramBeans)
			throws DZFWarpException {

	}
	public OcrInvoiceVOForApp[] querySbVos(String pk_group) throws DZFWarpException {
		return null;
	}

	@Override
	public OcrInvoiceVOForApp querySbDetail(String sbid) throws DZFWarpException {
		return null;
	}
	public Integer deleteRecord(String sessionflag, String pk_corp) throws DZFWarpException {
		return null;
	}

//	 public static int GetNextNumber(Integer[] iNumList)
//     {
//         for (int i = 0, j = 1; j < iNumList.length - 1; i++, j++)
//         {
//             //如果出现断号，则补齐断号
//             if ((iNumList[j] - iNumList[i]) > 1)
//             {
//                 return iNumList[i] + 1;
//             }
//         }
//         return iNumList[iNumList.length - 1] + 1;
//     }
//

//
//	//\\
//	@Override
//	public ImageQueryBean[] queryImageWithNonDr(String pk_image_group) throws Exception {
//		return null;
//	}
//
//	@Override
//	public ImageTurnMsgVO[] queryReturnmsgHeadVOs(String pk_corp, String strWhere) throws Exception {
//		return null;
//	}
//
//	@Override
//	public ImageLibraryVO uploadSingFile(UserBeanVO uBean,CorpVO corpvo, String account_id, File[] infiles, String[] filenames, String g_id,
//			String kprq, String memo, String memo1, String payment, DZFDouble mny)
//			throws DZFWarpException {
//
//		if(StringUtil.isEmpty(kprq)){
//			throw new BusinessException("上传日期不能为空!");
//		}
//
//		// 检查该图片是否已上传
//		//String imgMD = gl_pzimageserv.getUploadImgMD(infiles[0], corpvo);
//
//		// 检查是否关账
//		if (qmgzService.isGz(corpvo.getPk_corp(), kprq.substring(0, 7))) {
//			throw new BusinessException("图片所属的月份已关账，不允许上传图片哦");
//		}
//		// 检查上传图片期间是否期间损益结转
//		gl_pzimageserv.isQjSyJz(corpvo.getPk_corp(), kprq.substring(0, 7));
//
//		ImageGroupVO ig = null;
//		Long imgLibNum = 1L;
//		if (!StringUtil.isEmpty(g_id)) {
//			ig = gl_pzimageserv.queryGrpByID(corpvo.getPk_corp(), g_id);
//			if (ig != null) {
//				imgLibNum = gl_pzimageserv.queryLibCountByGID(corpvo.getPk_corp(), ig.getPk_image_group());
//				imgLibNum++;
//			}
//		} else {
//			ig = new ImageGroupVO();
//			ig.setImagecounts(1);
//			ig.setPk_corp(corpvo.getPk_corp());
//			ig.setCoperatorid(account_id);
//			ig.setDoperatedate(new DZFDate());
//			ig.setMemo(memo);
//			ig.setMemo1(memo1);
//			ig.setMny(mny);
//			ig.setSettlemode(payment);
//			// istate=0 标识对应直接生单,不启用切图、识图，以后用PhotoState常量类
//			ig.setIstate(PhotoState.state0);// 0
//			// 保存为凭证日期
//			ig.setCvoucherdate(new DZFDate(kprq));
//			ig.setPicflowid(corpvo.getDef4());
//			long maxCode = gl_pzimageserv.getNowMaxImageGroupCode(corpvo.getPk_corp());
//			if (maxCode > 0) {
//				ig.setGroupcode(maxCode + 1 + "");
//			} else {
//				ig.setGroupcode(AppQueryUtil.getCurDate() + "0001");
//			}
//		}
//		if (ig == null) {
//			throw new BusinessException("未找到图片组！");
//		}
//		String nameSuffix = filenames[0].substring(filenames[0].lastIndexOf("."));
//		String ds = "";
//		File dir = ImageUtil.getImageFolder("vchImg", corpvo, ds);
//
//		String nameSuffixTemp = "";
//		if (nameSuffix != null && (".bmp".equalsIgnoreCase(nameSuffix) || ".png".equalsIgnoreCase(nameSuffix))) {
//			nameSuffixTemp = nameSuffix;// 赋值前记录下后缀
//			nameSuffix = ImageUtil.COMPRESSIMAGESUffIX;// 设置固定值
//		}
//
//		String imgname = ig.getGroupcode() + "-" + ImageUtil.getLibraryNum(imgLibNum.intValue()) + nameSuffix;
//		String imgFileNm = UUID.randomUUID().toString() + nameSuffix;
//		File destFile = new File(dir, imgFileNm);
//		try {
//			if (".bmp".equalsIgnoreCase(nameSuffixTemp) || ".png".equalsIgnoreCase(nameSuffixTemp)) {
//				ImageCopyUtil.compressCoye(infiles[0], destFile);
//			} else {
//				ImageCopyUtil.copy(infiles[0], destFile);
//			}
//
//		} catch (Exception e) {
//			throw new BusinessException("保存图片失败");
//		}
//
//		ImageLibraryVO il = new ImageLibraryVO();
//		il.setImgpath(corpvo.getUnitcode() + "/" + AppQueryUtil.getCurDate() + "/" + imgFileNm);
//		il.setImgname(imgname);
//		il.setPk_corp(corpvo.getPk_corp());
//		il.setCoperatorid(account_id);
//		il.setDoperatedate(new DZFDate());
//		il.setCvoucherdate(new DZFDate(kprq));
////		il.setImgmd(imgMD);// 图片MD5码值
//		if (g_id != null && !StringUtil.isEmpty(ig.getPk_image_group())) {
//			il.setPk_image_group(g_id);
//			gl_pzimageserv.save(il);
//		} else {
//			ig.addChildren(il);
//			gl_pzimageserv.save(ig);
//
//			//第一张图片处理
//			IAppApproveService appapprovehand = (IAppApproveService) SpringUtils.getBean("appapprovehand");
//			BusiReqBeanVo busibean = new BusiReqBeanVo();
//			BeanUtils.copyNotNullProperties(uBean, busibean);
//			busibean.setPk_image_group(ig.getPk_image_group());
//			appapprovehand.updateApprove(busibean,singleObjectBO);
//
//			ImageGroupVO groutvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, ig.getPrimaryKey());
//			if(groutvo.getIstate()== null || groutvo.getIstate() == PhotoState.state0){
//				IAppBusinessService appbusihand = (IAppBusinessService) SpringUtils.getBean("appbusihand");
//				appbusihand.saveVoucherFromPic(groutvo.getPrimaryKey(), ig.getPk_corp(),singleObjectBO);
//			}
//		}
//		//更新张数
//		ig = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, ig.getPrimaryKey());
//		ig.setImagecounts(imgLibNum.intValue());
//		singleObjectBO.update(ig);
//
//		return il;
//	}

	
}
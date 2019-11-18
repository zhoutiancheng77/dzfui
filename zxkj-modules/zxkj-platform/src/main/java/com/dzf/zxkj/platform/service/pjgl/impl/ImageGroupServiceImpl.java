package com.dzf.zxkj.platform.service.pjgl.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.*;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.constant.ImageTypeConst;
import com.dzf.zxkj.common.constant.InvoiceColumns;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.FastOcrStateInfoVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.MsgAdminVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.image.ICreatePZByFixedService;
import com.dzf.zxkj.platform.service.image.IOcrAutoParseService;
import com.dzf.zxkj.platform.service.image.IPjsjManageService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IBankStatementService;
import com.dzf.zxkj.platform.service.pjgl.ICreatePZByRecogService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pjgl.IOcrImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.IBillcategory;
import com.dzf.zxkj.platform.service.zncs.IPrebillService;
import com.dzf.zxkj.platform.service.zncs.IZncsService;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.ImageCopyUtil;
import com.dzf.zxkj.platform.util.ThreadImgPoolExecutorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service("gl_pzimageserv")
@Slf4j
public class ImageGroupServiceImpl implements IImageGroupService {
	private static final String COMPRESSIMAGESUffIX = ".jpg";//
	private static final int THRESHOLD_FILE_SIZE = 100 * 1024;// 100k

	private SingleObjectBO singleObjectBO;
	private IReferenceCheck refchecksrv;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired(required = false)
	private ICreatePZByFixedService fixedservice;
	@Autowired(required = false)
	private IOcrAutoParseService gl_ocrauparseserv;
	@Autowired(required = false)
	private IPjsjManageService pjsj_serv;
	@Autowired(required = false)
	private IOcrImageGroupService imgOcrGropuserv;
	@Autowired(required = false)
	private ICreatePZByRecogService recogservice;
	@Autowired(required = false)
	private IBankStatementService bankStateservice;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired(required = false)
	IPrebillService iprebillservice;
	@Autowired(required = false)
	IBillcategory ibillcategory;
	@Autowired(required = false)
	IZncsService iZncsService;

	@Autowired
	private ICorpService corpService;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	public IReferenceCheck getRefchecksrv() {
		return refchecksrv;
	}

	@Autowired
	public void setRefchecksrv(IReferenceCheck refchecksrv) {
		this.refchecksrv = refchecksrv;
	}

	@Override
	public List<ImageLibraryVO> queryLibrary(String period, String pk_corp) throws DZFWarpException {
		return null;
	}

	@Override
	public List<ImageGroupVO> queryGroup(String period, String pk_corp) throws DZFWarpException {
		return null;
	}

	@Override
	public ImageGroupVO queryGrpByID(String pk_corp, String id) throws DZFWarpException {
		ImageGroupVO vo = (ImageGroupVO) singleObjectBO.queryVOByID(id, ImageGroupVO.class);
		return vo;
	}

	@Override
	public long queryLibCountByGID(String pk_corp, String gid) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(gid);
		String sql = "select count(*) from ynt_image_library where pk_corp = ? and pk_image_group = ?";
		long count = 0;

		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
		if (array != null && array.length > 0) {
			if (array[0] != null)
				count = Long.parseLong(array[0].toString());
		}
		return count;
	}

	@Override
	public ImageLibraryVO queryLibByID(String pk_corp, String id) throws DZFWarpException {
		ImageLibraryVO vo = (ImageLibraryVO) singleObjectBO.queryVOByID(id, ImageLibraryVO.class);
		return vo;
	}

	@Override
	public void update(ImageLibraryVO vo) throws DZFWarpException {
		singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}

	@Override
	public void save(ImageLibraryVO vo) throws DZFWarpException {
		singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}

	@Override
	public long getNowMaxImageGroupCode(String pk_corp) {
		// SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		// params.addParam(format.format(Calendar.getInstance().getTime()));
		params.addParam(new DZFDate().toString());

		String sql = "select max(groupcode) from ynt_image_group where pk_corp = ? and doperatedate = ? ";
		long maxcode = 0;

		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
		if (array != null && array.length > 0) {
			if (array[0] != null)
				maxcode = Long.parseLong(array[0].toString());
		}
		return maxcode;
	}

	@Override
	public void save(ImageGroupVO vo) throws DZFWarpException {
		singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}

	@Override
	public String queryLibByName(String pk_corp, String imgName) throws DZFWarpException {
		String a = null;
		// ImageLibraryVO vo = new ImageLibraryVO() ;
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(imgName);
		String sql = "select * from ynt_image_library where pk_corp = ? and imgname = ? ";
		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
		if (array != null & array.length > 0) {
			if (array[7] != null) {
				a = (String) array[7];
			}
		}
		return a;
	}

	@Override
	public void deleteImg(ImageLibraryVO vo) throws DZFWarpException {
		// String imgGroup = vo.getPk_image_group();
		singleObjectBO.deleteObject(vo);
	}

	@Override
	public void saveImageGroupBackUp(Map<String, ImageGroupVO> imageGroupMap, int type) {
		List<ImageGroupBAKVO> imgGrpBAKList = new ArrayList<ImageGroupBAKVO>();
		ImageGroupVO imageGroupVO = null;
		ImageGroupBAKVO imageGroupBAKVO = null;
		for (Map.Entry<String, ImageGroupVO> entry : imageGroupMap.entrySet()) {
			imageGroupVO = entry.getValue();
			imageGroupBAKVO = new ImageGroupBAKVO();
			BeanUtils.copyNotNullProperties(imageGroupVO, imageGroupBAKVO);// 赋值操作
			imageGroupBAKVO.setPrimaryKey("");// 赋值操作时,imageGroupVO的主键赋值给了imageGroupBAKVO的主键
			// singleObjectBO.saveObject(imageGroupBAKVO.getPk_corp(),
			// imageGroupBAKVO);
			imgGrpBAKList.add(imageGroupBAKVO);
		}
		ImageGroupBAKVO[] imgGrpBAKVOs = imgGrpBAKList.toArray(new ImageGroupBAKVO[0]);
		singleObjectBO.insertVOArr(imgGrpBAKVOs[0].getPk_corp(), imgGrpBAKVOs);

	}

	@Override
	public void saveImageGroupBackUp(ImageGroupVO imageGroupVO, int type) {

		if (imageGroupVO != null) {
			ImageGroupVO groupVO = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
					imageGroupVO.getPrimaryKey());
			Map<String, ImageGroupVO> imageGroupMap = new HashMap<String, ImageGroupVO>();
			imageGroupMap.put(groupVO.getPrimaryKey(), groupVO);
			saveImageGroupBackUp(imageGroupMap, type);
			groupVO.setIsskiped(DZFBoolean.FALSE);
			groupVO.setIsuer(DZFBoolean.FALSE);// 设置为未使用
			groupVO.setIstate(PhotoState.state0);// 自由态
			singleObjectBO.update(groupVO);
		}
	}

	/**
	 * 判断上传的期间是否损益结转
	 */
	public void isQjSyJz(String pk_corp, String cvoucherdate) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		if (pk_corp != null && !"".equals(pk_corp) && cvoucherdate != null && !"".equals(cvoucherdate)) {
			StringBuilder sbCode = new StringBuilder(
					"select count(1) from  ynt_qmcl where pk_corp=? and period=? and nvl(dr,0) = 0 and nvl(isqjsyjz,'N')='Y'");
			sp.addParam(pk_corp);
			sp.addParam(cvoucherdate);
			BigDecimal repeatCodeNum = (BigDecimal) singleObjectBO.executeQuery(sbCode.toString(), sp,
					new ColumnProcessor());

			if (repeatCodeNum.intValue() > 0) {
				// String errCodeStr = "-150";
				throw new BusinessException("该月份已经结转损益，请修改上传月份！");
			}
		} else {
			throw new BusinessException("上传图片失败！");
		}

	}

	@Override
	public List<ImageGroupVO> queryImageGroupByCondition(CorpVO corpvo, String value, String Condition) {
		StringBuffer sf = new StringBuffer();
		sf.append(" select ep.* from ynt_image_group ep       ");
		sf.append("   where ep.pk_corp = ? and ep.pk_image_group in(             ");
		sf.append("       select ry.pk_image_group from ynt_image_library ry  ");
		sf.append("         where 1=1  ");
		SQLParameter searchParams = new SQLParameter();
		searchParams.addParam(corpvo.getPk_corp());
		if ("ImageName".equals(Condition)) {
			sf.append("  and ry.imgname = ?  ");
			searchParams.addParam(value.trim());
		}
		sf.append(" ) ");
		List<ImageGroupVO> groupVOs = (List<ImageGroupVO>) singleObjectBO.executeQuery(sf.toString(), searchParams,
				new BeanListProcessor(ImageGroupVO.class));

		return groupVOs;
	}

	@Override
	public void deleteImgFromTpll(String pk_corp, String userid, String desc, String[] delTelGrpData, 
			String[] delOthIds) throws DZFWarpException{
		if(delTelGrpData != null && delTelGrpData.length > 0){
			refuseImg(pk_corp, userid, desc, delTelGrpData);
		}
		
		if(delOthIds != null && delOthIds.length > 0){
			deleteKJImg(pk_corp, delOthIds);
		}
	}
	
	private void refuseImg(String pk_corp, String userid, String des, String[] delTelGrpData) throws DZFWarpException{
		if (delTelGrpData == null || delTelGrpData.length == 0)
			return;
		
		Set<String> set = new HashSet<>();
		
		ImageTurnMsgVO imageTurnMsgVO;
		for(String s : delTelGrpData){
			if(set.contains(s)){
				continue;
			}
			
			imageTurnMsgVO = new ImageTurnMsgVO();
			imageTurnMsgVO.setCoperatorid(userid);
			imageTurnMsgVO.setDoperatedate(new DZFDate(new Date()));
			imageTurnMsgVO.setMessage(des);
			imageTurnMsgVO.setPk_corp(pk_corp);
			imageTurnMsgVO.setPk_image_group(s);
			TzpzHVO hvo = new TzpzHVO();
			hvo.setCoperatorid(userid);
			
			gl_tzpzserv.updateImageVo(pk_corp, s, null, "1", imageTurnMsgVO, hvo, false, userid, false);
		}
		
		
	}
	
	@Override
	public void deleteKJImg(String pk_corp, String[] imageKeys) throws DZFWarpException {
		try {
			if (imageKeys == null || imageKeys.length == 0)
				return;
			// ArrayList<String> delfiles = new ArrayList<String>();
			// 删除图片库和图片组表的记录
			YntBoPubUtil yntBoPubUtil = new YntBoPubUtil();
			String where = String.format("nvl(dr,0)=0 and (%s)",
					yntBoPubUtil.getSqlStrByArrays(imageKeys, 1000, "pk_image_library"));
			ImageLibraryVO[] imageVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, where,
					new SQLParameter());
			if (imageVOs != null && imageVOs.length > 0) {
				ArrayList<String> imgGroupPks = new ArrayList<String>();
				// 图片组信息
				for (ImageLibraryVO imageVO : imageVOs) {
					imgGroupPks.add(imageVO.getPk_image_group());
				}
				String grpwhere = String.format("nvl(dr,0)=0 and (%s)",
						yntBoPubUtil.getSqlStrByArrays(imgGroupPks.toArray(new String[0]), 1000, "pk_image_group"));
				ImageGroupVO[] imggrpvos = (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class,
						grpwhere, new SQLParameter());
				Map<String, ImageGroupVO> imgGrpMap = new HashMap<String, ImageGroupVO>();
				for (ImageGroupVO imggrpvo : imggrpvos) {
					imgGrpMap.put(imggrpvo.getPk_image_group(), imggrpvo);
				}
				// 凭证信息
				TzpzHVO[] tzpzVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(TzpzHVO.class, grpwhere,
						new SQLParameter());

				Map<String, TzpzHVO> pzmap = new LinkedHashMap<>();
				if (tzpzVOs != null && tzpzVOs.length > 0) {
					for (TzpzHVO tzpzvo : tzpzVOs) {
						pzmap.put(tzpzvo.getPk_image_group(), tzpzvo);
					}
				}
				for (ImageLibraryVO imageVO : imageVOs) {
					// delfiles.add(imageVO.getImgpath());

					if (!pk_corp.equals(imageVO.getPk_corp())) {
						throw new BusinessException("无权删除该图片");
					}

					String pk_image_group = imageVO.getPk_image_group();
					ImageGroupVO groupVO = imgGrpMap.get(pk_image_group);
					// 校验图片组
					if (groupVO == null) {
						throw new BusinessException("没有找到PK值为" + pk_image_group + "的图片组，请确认该图片组是否已经被作废！");
					}
					// 校验是否是手机上传的图片
					Integer sourcemode = groupVO.getSourcemode();
					if (sourcemode != null && groupVO.getSourcemode() == PhotoState.SOURCEMODE_01) {
						String warnstr = String.format("图片%s是手机端上传的图片，不能作废！", imageVO.getImgname());
						throw new BusinessException(warnstr);
					}
					// 2017-05-11 管理端批量上传的图片不能删除
					// if (sourcemode != null && groupVO.getSourcemode() ==
					// PhotoState.SOURCEMODE_10) {
					// String warnstr = String.format("图片%s是管理端上传的图片，不能删除！",
					// imageVO.getImgname());
					// throw new BusinessException(warnstr);
					// }

					int status = groupVO.getIstate();
					// 重复状态图片 没有被引用
					// if (status != PhotoState.state0 && status !=
					// PhotoState.state80 && status != PhotoState.state102) {
					// throw new BusinessException("已经被引用 ，不能删除！");
					// }
					
//					if (status == PhotoState.state1 ) {
//						throw new BusinessException("已经被占用 ，不能删除！");
//					}
					if (pzmap.get(pk_image_group) != null) {
						if (status != PhotoState.state0 && status != PhotoState.state80
								&& status != PhotoState.state102) {
							throw new BusinessException("已经被引用 ，不能作废！");
						}
					}
					if (status == PhotoState.state205 ) {
					throw new BusinessException("已经被作废！");
				}
					// 校验是否被档案引用
					if (getRefchecksrv().isReferenced(imageVO.getTableName(), imageVO.getPrimaryKey())) {
						throw new BusinessException("已经被引用 ，不能作废！");
					}
					//imageVO.setIstate(PhotoState.state205);
				}
				//add by mfz检查图片是否生成了资产或者出入库
				checkZckpAndKc(pk_corp,imgGroupPks);
				
				if (tzpzVOs != null && tzpzVOs.length > 0) {
					throw new BusinessException("总账凭证中已经存在图片组[" + tzpzVOs[0].getPk_image_group()
							+ "]对应的凭证，不允许进行任何图片操作，凭证号为：" + tzpzVOs[0].getPzh() + ",请刷新后重试");
				}
				//singleObjectBO.deleteVOArray(imageVOs);
				//singleObjectBO.updateAry(imageVOs,new String []{"istate"});
				// 判断图片组是否还有相关的图片文件，如果没有也要删除
//				ImageLibraryVO[] childVOs = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class,
//						grpwhere, new SQLParameter());
//				if (childVOs == null || childVOs.length == 0) {
//					singleObjectBO.deleteVOArray(imggrpvos);
//				} else {
//					Map<String, ImageLibraryVO> imgLibMap = new HashMap<String, ImageLibraryVO>();
//					for (ImageLibraryVO childVO : childVOs) {
//						imgLibMap.put(childVO.getPk_image_group(), childVO);
//					}
//					List<ImageGroupVO> delImgGrpList = new ArrayList<ImageGroupVO>();
//					for (ImageGroupVO groupVO : imggrpvos) {
//						if (imgLibMap.containsKey(groupVO.getPk_image_group())) {
//							continue;
//						} else {
//							//groupVO.setIstate(PhotoState.state205);
//							delImgGrpList.add(groupVO);
//						}
//					}
//					if (delImgGrpList.size() > 0) {
//						//singleObjectBO.deleteVOArray(delImgGrpList.toArray(new ImageGroupVO[0]));
//						singleObjectBO.updateAry(delImgGrpList.toArray(new ImageGroupVO[0]),new String []{"istate"});
//					}
//				}
				for (ImageGroupVO groupvo : imggrpvos) {
					groupvo.setIstate(PhotoState.state205);
				}
				singleObjectBO.updateAry(imggrpvos,new String []{"istate"});
				// 删除图片文件
				// delImgFiles(delfiles);
				
				delDetailsList(imageVOs, pk_corp);
				// 删除ocr上传的图片
				//delOcrImage(imageVOs);
				// 删除回单中间表数据
				delBankBill(imageVOs, pk_corp);
			} else {
				throw new BusinessException("获取图片组信息失败，请确认图片是否已被删除");
			}

		} catch (Exception e) {
			log.error("错误", e);
			throw new BusinessException(e.getMessage());
		}
	}
	private void checkZckpAndKc(String pk_corp,ArrayList<String> imgGroupPks)throws DZFWarpException{
		if(imgGroupPks==null||imgGroupPks.size()==0)return;
		String sql="select pk_invoice from ynt_interface_invoice where nvl(dr,0)=0 and pk_corp=? and "+ SqlUtil.buildSqlForIn("pk_image_group", imgGroupPks.toArray(new String[0]));
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<Object[]> invoiceList=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		if(invoiceList!=null&&invoiceList.size()>0){
			List<String> billidList=new ArrayList<>();
			for (int i = 0; i < invoiceList.size(); i++) {
				billidList.add(invoiceList.get(i)[0].toString());
			}
			sql = "nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_invoice", billidList.toArray(new String[0])) + "";
			OcrInvoiceVO vos[] = (OcrInvoiceVO[]) singleObjectBO.queryByCondition(OcrInvoiceVO.class, sql,
					new SQLParameter());
			if (vos == null || vos.length == 0)
				throw new BusinessException("没有查到对应的票据信息!");
			List<OcrInvoiceVO> list = Arrays.asList(vos);

			List<OcrInvoiceDetailVO> listdetail = iprebillservice.queryDetailByInvList(list);

			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(listdetail,
					new String[] { "pk_invoice" });

			for (OcrInvoiceVO invoicevo : list) {
				if(detailMap.get(invoicevo.getPk_invoice())!=null){
					invoicevo.setChildren(detailMap.get(invoicevo.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
				}
			}

			if (ibillcategory.checkHaveZckp(vos).equals(DZFBoolean.TRUE)) {
				throw new BusinessException("所选票据已生成后续单据，请检查！");
			}
			if (ibillcategory.checkHaveIctrade(vos).equals(DZFBoolean.TRUE)) {
				throw new BusinessException("所选票据已生成后续单据，请检查！");
			}
		}
	}
	// 删除图片 更新清单 
	private void delDetailsList(ImageLibraryVO[] imageVOs, String pk_corp) {
		
		if(imageVOs == null || imageVOs.length==0)
			return;
		
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		SQLParameter sp = null;
		for (ImageLibraryVO image : imageVOs) {
			sp = new SQLParameter();
			sp.addParam(image.getPk_image_library());
			sp.addParam(pk_corp);
			list.add(sp);
		}
		
		String sql = "update ynt_vatsaleinvoice y set y.imgpath = null, y.sourcebillid =null,y.pk_image_group=null,y.pk_image_library=null,vdef13=null Where y.sourcebillid = ? and pk_corp = ? and sourcetype !="+ IBillManageConstants.OCR;
		singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
		
		sql = "update ynt_vatsaleinvoice y set dr=1 Where y.sourcebillid = ? and pk_corp = ? and sourcetype ="+IBillManageConstants.OCR;
		singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
	
		sql = "update ynt_vatincominvoice y set  y.imgpath = null,y.sourcebillid =null,y.pk_image_group=null,y.pk_image_library=null,vdef13=null Where y.sourcebillid = ? and pk_corp = ?  and sourcetype !="+IBillManageConstants.OCR;
		singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
		
		sql = "update ynt_vatincominvoice y set  dr=1 Where y.sourcebillid = ? and pk_corp = ?  and sourcetype ="+IBillManageConstants.OCR;
		singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
		
		sql = "update ynt_ictrade_h y set y.pk_image_group=null,y.pk_image_library=null Where y.pk_image_library = ? and pk_corp = ? ";
		singleObjectBO.executeBatchUpdate(sql.toString(), list.toArray(new SQLParameter[list.size()]));
	}
	
	//删除回单中间表数据
	private void delBankBill(ImageLibraryVO[] imageVOs, String pk_corp) {

		if (imageVOs == null || imageVOs.length == 0) {
			return;
		}
		List<String> list = new ArrayList<>();
		for (ImageLibraryVO imagevo : imageVOs) {
			list.add(imagevo.getPk_image_library());
		}
		
		bankStateservice.deleteBankBillByLibrary(list, pk_corp);

	}

	// 删除ocr上传的图片
	private void delOcrImage(ImageLibraryVO[] imageVOs) {

		if (imageVOs == null || imageVOs.length == 0) {
			return;
		}
		List<String> list = new ArrayList<>();
		for (ImageLibraryVO imagevo : imageVOs) {
			list.add(imagevo.getPk_image_library());
		}

		String wheresql = SqlUtil.buildSqlForIn("crelationid", list.toArray(new String[0]));
		StringBuffer strb = new StringBuffer();
		strb.append(" update ynt_image_ocrlibrary set dr=1  where " + wheresql);
		singleObjectBO.executeUpdate(strb.toString(), null);

		strb.setLength(0);
		strb.append(
				" update ynt_image_ocrgroup set dr=1   where pk_image_ocrgroup in (select pk_image_ocrgroup  from ynt_image_ocrlibrary where"
						+ wheresql + ")");
		singleObjectBO.executeUpdate(strb.toString(), null);
	}

	/**
	 * 删除图片文件
	 * 
	 * @param delfiles
	 */
	protected void delImgFiles(ArrayList<String> delfiles) {
		if (delfiles != null && delfiles.size() > 0) {
			File imgFile = null;
			for (String delfile : delfiles) {
				imgFile = new File(Common.imageBasePath + delfile);
				if (imgFile.exists())
					imgFile.delete();
			}
		}
	}

	@Override
	public ImageLibraryVO[] queryLibByMD(String pk_corp, String md) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(md.trim());

		String wherePart = " nvl(dr,0) = 0 and pk_corp = ? and trim(imgmd) = ? ";
		ImageLibraryVO[] imglibvos = (ImageLibraryVO[]) singleObjectBO.queryByCondition(ImageLibraryVO.class, wherePart,
				params);
		if(imglibvos!=null &&imglibvos.length>0){
			for (int i = 0; i < imglibvos.length; i++) {
				ImageGroupVO vo = (ImageGroupVO)singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, imglibvos[i].getPk_image_group());
				if(vo.getIstate()!=PhotoState.state205){
					return imglibvos;
				}
			}
			return null;
		}
		return imglibvos;

	}

	@Override
	public ImageLibraryVO uploadSingFile(CorpVO corpvo, UserVO userVo, MultipartFile[] infiles, String g_id,
										 String period, String pjlxType) throws DZFWarpException {

		// 2017-09-08 去掉自动识别 在ocr服务统一处理
		// processImg(g_id, ig, il);
		ImageLibraryVO il = null;
		try {
			il = uploadSingFile1(corpvo, userVo, infiles, g_id, period, null, pjlxType);
			// 在线端上传图片
			imgOcrGropuserv.saveData(corpvo, il, pjlxType, 0, infiles[0].getOriginalFilename());
			pjsj_serv.updateCountByPjlx(corpvo.getPrimaryKey(), period, pjlxType, null, null, userVo, corpvo, 1);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}

		return il;
	}

	private ImageLibraryVO uploadSingFile1(CorpVO corpvo, UserVO userVo, MultipartFile[] infiles,
			String g_id, String period, String invoicedata, String pjlxType) {
		// 检查该图片是否已上传
		String imgMD = getUploadImgMD(infiles[0], corpvo);

		// 检查是否关账
		if (qmgzService.isGz(corpvo.getPk_corp(), period)) {
//			throw new BusinessException("图片所属的月份已关账，不允许上传图片哦");
			throw new BusinessException("当前期间已关账，请选择其他期间上传或取消关账");
		}

		// 检查上传图片期间是否损益结转
//		if (StringUtil.isEmpty(invoicedata))
//			isQjSyJz(corpvo.getPk_corp(), period);
		
		/*
		 * if(!"no".equals(isQjsyjz)){//"no"为未期间结转损益或无数据，其他情况这不能上传图片
		 * json.setRows(new ImageLibraryVO()); throw new
		 * Exception("该月份已经结转损益，请修改上传日期！"); }
		 */
		ImageGroupVO ig = null;
		Long imgLibNum = 1L;
		if (g_id != null && g_id.trim().length() > 0) {
			ig = queryGrpByID(corpvo.getPk_corp(), g_id);
			if (ig != null) {
				imgLibNum = queryLibCountByGID(corpvo.getPk_corp(), ig.getPk_image_group());
				imgLibNum++;
			}
		} else {
			ig = new ImageGroupVO();
			ig.setPk_corp(corpvo.getPk_corp());
			ig.setCoperatorid(userVo.getCuserid());
			ig.setDoperatedate(new DZFDate());
			// istate=0 标识对应直接生单,不启用切图、识图，以后用PhotoState常量类
			ig.setIstate(PhotoState.state0);// 0
			// *********************************************************
			// 根据前台传过来的年份和月份，等到当前月的最大日期
			// String requetDate = selYear + "-" + selMon + "-" + new
			// DZFDate().getDay();
			// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			// Date nowDate = sdf.parse(requetDate);
			// Calendar cDay1 = Calendar.getInstance();
			// cDay1.setTime(nowDate);
			// int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
			// DZFDate selDate = DZFDate.getDate(selYear + "-" + selMon + "-" +
			// lastDay );
			// 保存为凭证日期
			ig.setCvoucherdate(TrunLastDay(period));
			ig.setPicflowid(corpvo.getDef4());
			ig.setPjlxstatus(StringUtil.isEmpty(pjlxType) ? null : Integer.parseInt(pjlxType));
			long maxCode = getNowMaxImageGroupCode(corpvo.getPk_corp());
			if (maxCode > 0) {
				ig.setGroupcode(maxCode + 1 + "");
			} else {
				ig.setGroupcode(getCurDate() + "0001");
			}
			if (!StringUtil.isEmpty(invoicedata)) {
				ig.setSourcemode(PhotoState.SOURCEMODE_20);
			}
		}
		if (ig == null) {
			throw new BusinessException("未找到图片组！");
		}
		String nameSuffix = infiles[0].getOriginalFilename().substring(infiles[0].getOriginalFilename().lastIndexOf("."));
		String ds = ""; // getRequest().getRealPath("/").replaceAll("\\\\","/");
		// ds = ds.substring(0,ds.length() -1);
		File dir = getImageFolder("vchImg", corpvo, ds);
		String imgFileNm = UUID.randomUUID().toString() + nameSuffix;
		String imgname = ig.getGroupcode() + "-" + getLibraryNum(imgLibNum.intValue()) + nameSuffix;
		String pdfFileNm = null;
		boolean isPdf = false;

		String nameSuffixTemp = "";
		if (nameSuffix != null && (".bmp".equalsIgnoreCase(nameSuffix) || ".png".equalsIgnoreCase(nameSuffix)
				|| ".pdf".equalsIgnoreCase(nameSuffix))) {
			nameSuffixTemp = nameSuffix;// 赋值前记录下后缀
			nameSuffix = COMPRESSIMAGESUffIX;// 设置固定值

			if (".pdf".equalsIgnoreCase(nameSuffixTemp)) {
				isPdf = true;
			}
		}

		// File destFile = new File(dir,ig.getGroupcode() + "-" +
		// getLibraryNum(imgLibNum.intValue()) + nameSuffix);
		/*
		 * 注：ynt_image_library 中imgname与imgpath名称不一致的原因是保证数据备份
		 */
		String simgFileNm = UUID.randomUUID().toString() + nameSuffix;
		String mimgFileNm = UUID.randomUUID().toString() + nameSuffix;
		boolean isComBySale = false;
		File destFile = null;

		if (isPdf) {
			imgFileNm = UUID.randomUUID().toString() + nameSuffix;
			destFile = new File(dir, imgFileNm);

			if (infiles[0].getSize() > THRESHOLD_FILE_SIZE * 1024 / 20)// 5M
				throw new BusinessException("上传pdf文件大小超过5M，请检查");

			pdfFileNm = UUID.randomUUID().toString() + nameSuffixTemp;
			ImageCopyUtil.transPdfToJpg(infiles[0], dir, pdfFileNm, destFile);
		} else {
			destFile = new File(dir, imgFileNm);
			try {
				isComBySale = infiles[0].getSize() > THRESHOLD_FILE_SIZE;
				ImageCopyUtil.copy(infiles[0], destFile);

			} catch (Exception e) {
				throw new BusinessException("保存图片失败");
			}
		}

		ImageLibraryVO il = new ImageLibraryVO();
		il.setImgpath(corpvo.getUnitcode() + "/" + getCurDate() + "/" + imgFileNm);
		il.setPdfpath(isPdf ? corpvo.getUnitcode() + "/" + getCurDate() + "/" + pdfFileNm : null);
		il.setSmallimgpath(corpvo.getUnitcode() + "/" + getCurDate() + "/" + simgFileNm);
		il.setMiddleimgpath(isComBySale ? (corpvo.getUnitcode() + "/" + getCurDate() + "/" + mimgFileNm) : null);
		il.setImgname(imgname);
		// il.setImgpath(corpvo.getUnitcode() + "/" + getCurDate() + "/" +
		// destFile.getName());
		// il.setImgname(destFile.getName());
		il.setPk_corp(corpvo.getPk_corp());
		il.setPk_uploadcorp(corpvo.getPk_corp());
		il.setCoperatorid(userVo.getCuserid());
		il.setDoperatedate(new DZFDate());
		il.setCvoucherdate(TrunLastDay(period));
		il.setImgmd(imgMD);// 图片MD5码值
		if (g_id != null && !StringUtil.isEmpty(ig.getPk_image_group())) {
			il.setPk_image_group(g_id);
			save(il);
		} else {
			ig.addChildren(il);
			save(ig);
		}
		storageImgFile(destFile, nameSuffixTemp, dir, nameSuffix, imgFileNm, simgFileNm, mimgFileNm, isComBySale);
		return il;
	}

	private long getMaxOcrImageGroupCode(String pk_corp) {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(new DZFDate().toString());

		String sql = "select max(groupcode) from ynt_image_ocrgroup where pk_corp = ? and doperatedate = ? ";
		long maxcode = 0;

		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
		if (array != null && array.length > 0) {
			if (array[0] != null)
				maxcode = Long.parseLong(array[0].toString());
		}
		return maxcode;
	}

	public void processImg(String gid, ImageGroupVO grpvo, ImageLibraryVO libvo) throws DZFWarpException {
		String flowid = grpvo.getPicflowid();

		if (StringUtil.isEmpty(flowid))
			return;

		int iflow = Integer.parseInt(flowid);
//		if (PhotoState.TREAT_TYPE_1 == iflow || PhotoState.TREAT_TYPE_3 == iflow || PhotoState.TREAT_TYPE_5 == iflow|| PhotoState.TREAT_TYPE_6 == iflow|| PhotoState.TREAT_TYPE_7 == iflow) {
		if (PhotoState.TREAT_TYPE_7 == iflow) {
			grpvo.setChildren(null);
			grpvo.addChildren(libvo);
			final ImageGroupVO groupvo = grpvo;
			ThreadImgPoolExecutorFactory.getInstance().execute(new Runnable() {

				@Override
				public void run() {
					try {
						// 先失眠1秒
						Thread.sleep(1000);
						gl_ocrauparseserv.processOcrPase(groupvo);
					} catch (InterruptedException e) {
						log.error("错误",e);
					}
				}
			});
		}
		// else if((PhotoState.TREAT_TYPE_2 == iflow || PhotoState.TREAT_TYPE_4
		// == iflow)
		// && StringUtil.isEmpty(gid)){
		//// picflowservice.execFlow(grpvo, grpvo.getPk_corp());
		// ICreatePZByFixedService fixedservice = (ICreatePZByFixedService)
		// SpringUtils.getBean("createpzbyfixedservice");
		// fixedservice.newSaveVoucherFromPic(grpvo, false);
		// }
	}

	/**
	 * 存储图片
	 * 
	 * @param srcFile
	 * @param nameSuffixTemp
	 * @param dir
	 * @param nameSuffix
	 * @param imgFileNm
	 * @param simgFileNm
	 */
	public void storageImgFile(final File srcFile, final String nameSuffixTemp, final File dir, final String nameSuffix,
			final String imgFileNm, final String simgFileNm, final String mimgFileNm, final boolean isComBySale) {

		ThreadImgPoolExecutorFactory.getInstance().execute(new Runnable() {
			@Override
			public void run() {
				try {
					// 先失眠1秒
					Thread.sleep(1000);
					ImageCopyUtil.storageImgFile(srcFile, nameSuffixTemp, dir, nameSuffix, imgFileNm, simgFileNm,
							mimgFileNm, isComBySale);
				} catch (InterruptedException e) {
					log.error("错误",e);
				}
			}
		});
	}

	/**
	 * 获取图片md5码值
	 * 
	 * @param file
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public String getUploadImgMD(MultipartFile file, CorpVO corpvo) throws DZFWarpException {
		String value = null;
		FileInputStream in = null;
		try {
			in = (FileInputStream)file.getInputStream();
			MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.getSize());
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(byteBuffer);
			BigInteger bi = new BigInteger(1, md5.digest());
			value = bi.toString(16);

			ImageLibraryVO[] imglibVOs = queryLibByMD(corpvo.getPk_corp(), value);
			if (imglibVOs != null && imglibVOs.length != 0) {
				throw new BusinessException("图片已存在，不可重复上传");
			}
			return value;
		} catch (Exception e) {
			log.error("图片已存在", e);
			throw new BusinessException("图片已存在，不可重复上传");
		} finally {
			if (null != in) {
				try {
					in.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}

	// 日期转换
	private DZFDate TrunLastDay(String period) {
		DZFDate selDate = DateUtils.getPeriodEndDate(period);
		// String requetDate = selYear + "-" + selMon + "-" + "03";
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		// Date nowDate;
		// DZFDate selDate = null;
		// try {
		// nowDate = sdf.parse(requetDate);
		// Calendar cDay1 = Calendar.getInstance();
		// cDay1.setTime(nowDate);
		// int lastDay = cDay1.getActualMaximum(Calendar.DAY_OF_MONTH);
		// selDate = DZFDate.getDate(selYear + "-" + selMon + "-" + lastDay );
		// } catch (ParseException e) {
		// //e.printStackTrace();
		// }
		return selDate;
	}

	// 获取当前年月日
	private static String getCurDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(Calendar.getInstance().getTime());
	}

	private static File getImageFolder(String type, CorpVO corpvo, String directory) {
		File dir = null;
		if ("vchImg".equals(type)) {
			String imgfolder = ImageCommonPath.getDataCenterPhotoPath() + File.separator + corpvo.getUnitcode()
					+ File.separator + getCurDate();
			String folder = directory + imgfolder; // DZFConstant.DZF_KJ_UPLOAD_BASE
													// + imgfolder;
			dir = new File(folder);
			if (!dir.exists()) {
				dir.mkdirs();
			}
		}
		return dir;
	}

	private static String getLibraryNum(int n) {
		String tmp = n + "";
		while (tmp.length() < 3) {
			tmp = "0" + tmp;
		}
		return tmp;
	}

	@Override
	public void saveCreatePz(ImageLibraryVO libvo, CorpVO corpVO, String gid, String pjlxType) throws DZFWarpException {
		if (libvo == null || StringUtil.isEmpty(libvo.getPk_image_group())) {
			return;
		}

		String ipicflow = corpVO.getDef4();

		if (StringUtil.isEmpty(ipicflow))
			return;

		int iflow = Integer.parseInt(ipicflow);
		if ((PhotoState.TREAT_TYPE_0 == iflow || (PhotoState.TREAT_TYPE_7 == iflow && !StringUtil.isEmpty(pjlxType)))
				&& StringUtil.isEmpty(gid)) {
			ImageGroupVO grpvo = (ImageGroupVO) singleObjectBO.queryByPrimaryKey(ImageGroupVO.class,
					libvo.getPk_image_group());
			fixedservice.newSaveVoucherFromPic(grpvo, false);
		}

	}

	@Override
	public long queryBackLibCount(String pk_corp, String gid) throws DZFWarpException {
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(gid);
		params.addParam(DZFBoolean.TRUE.toString());
		String sql = "select count(pk_image_library) from ynt_image_library where pk_corp = ? and pk_image_group = ? and nvl(dr,0) = 0 and nvl(isback,'N') = ? ";
		long count = 0;

		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());

		if (array != null && array.length > 0) {
			if (array[0] != null)
				count = Long.parseLong(array[0].toString());
		}

		return count;
	}

	@Override
	public Map<String, Object> getPicStatistics(String pk_corp, String beginDate, 
			String endDate, String serdate) throws DZFWarpException {
		String sqlin = "";
		if("serSc".equals(serdate)){
			sqlin = " and lb.cvoucherdate >= ? and lb.cvoucherdate <= ? ";
		}else if("serRz".equals(serdate)){
			sqlin = " and lb.doperatedate >= ? and lb.doperatedate <= ? ";
		}
		//case when syje is null then zcje else syje end as mony
		StringBuilder sql = new StringBuilder();//gp.istate
		sql.append("select count(1) total, bu.ioperatetype btype, pz.vbillstatus, pz.iautorecognize,")
				.append("  case when ( (yi.pk_invoice is null or yi.pk_billcategory is null) and  gp.istate not in (205,100,101) ) then 18 else gp.istate end as istate,")
				.append(" bk.pk_bankstatement bank, sl.pk_vatsaleinvoice sale,bu.pk_vatincominvoice buy")
				.append("  from ynt_image_library lb")
				.append("  left join ynt_image_group gp")
				.append("    on gp.pk_corp = ? and gp.pk_image_group = lb.pk_image_group")
				.append("  left join ynt_tzpz_h pz")
				.append("    on pz.pk_corp = ? and pz.pk_image_group = lb.pk_image_group")
				.append("     and nvl(pz.dr, 0) = 0")
				.append("  left join ynt_bankstatement bk on bk.sourcebillid = lb.pk_image_library")
				.append("  and nvl(bk.dr, 0) = 0")
				.append("  left join ynt_vatsaleinvoice sl on sl.pk_image_library = lb.pk_image_library")
				.append("  and nvl(sl.dr, 0) = 0")
				.append("  left join ynt_vatincominvoice bu on bu.pk_image_library = lb.pk_image_library")
				.append("  and nvl(bu.dr, 0) = 0")
				.append(" left join ynt_image_ocrlibrary yc on lb.pk_image_library = yc.crelationid")
				.append(" left join ynt_interface_invoice yi on yi.ocr_id = yc.pk_image_ocrlibrary and nvl(yi.dr,0)=0 ")
				.append(" where lb.pk_corp = ?").append("   and nvl(lb.dr, 0) = 0")
				.append(sqlin)//.append("   and lb.cvoucherdate >= ? ").append("   and lb.cvoucherdate <= ? ")
				.append("   and nvl(gp.istate,10) <> ? ")
				.append("   and nvl(gp.istate,10) <> ? ")
				.append(" group by gp.istate, bu.ioperatetype, pz.vbillstatus, pz.iautorecognize,")
				.append(" bk.pk_bankstatement, sl.pk_vatsaleinvoice,bu.pk_vatincominvoice,yi.pk_invoice,yi.pk_billcategory");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		if("serSc".equals(serdate)){
			sp.addParam(beginDate);
			sp.addParam(endDate);
		}else if("serRz".equals(serdate)){
			sp.addParam(beginDate);
			sp.addParam(endDate);
		}
		
		sp.addParam(PhotoState.state10);
		sp.addParam(PhotoState.state80);
		List<Map<String, Object>> rsList = (List<Map<String, Object>>) singleObjectBO.executeQuery(sql.toString(), sp,
				new MapListProcessor());
		return processPicGroupData(pk_corp, rsList);
	}

	private Map<String, Object> processPicGroupData(String pk_corp, List<Map<String, Object>> data) {
		Map<String, Object> statistics = new HashMap<String, Object>();
//		Map<String, Integer> pjlxCountMap = new LinkedHashMap<String, Integer>();
//		pjlxCountMap.put("未分类", 0);
//		List<PjCheckBVO> pjList = pjsj_serv.queryPjlxTypes(pk_corp);
//		if (pjList != null) {
//			for (PjCheckBVO pjCheckBVO : pjList) {
//				pjlxCountMap.put(pjCheckBVO.getMemo(), 0);
//			}
//		}
		
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		//boolean isAutoRecognize = isAutoRecognize(corpvo.getDef4());
		int fullTotal = 0;
		int recognizedTotal = 0;
		int unusedTotal = 0;
		int repeatTotal = 0;
		int invalidTotal = 0;
		int vouchertotal = 0;
		int processTotal = 0;//识别中
		int unvoucher=0;
		for (Map<String, Object> map : data) {
			BigDecimal btype = (BigDecimal) map.get("btype");
			String type =  null;
			if (map.get("sale") != null) {
				type = "销项发票";
			} else if (map.get("buy") != null && btype != null && btype.intValue() == 21) {
				type = "入库发票";
			} else if (map.get("bank") != null) {
				type = "银行票据";
			} else {
				type = "其他票据";
			}
			
			
			// 总图片数
			int totalPic = 0;
			
			BigDecimal totalDc = (BigDecimal) map.get("total");
			int total = totalDc.intValue();
			fullTotal += total;
			BigDecimal state = (BigDecimal) map.get("istate");
			if (state != null ) {//&& (!isAutoRecognize || state.intValue() != PhotoState.state0)
				recognizedTotal += total;
				Map<String, Integer> countMap = (Map<String, Integer>) statistics.get(type);
				if (countMap == null) {
					countMap = new HashMap<String, Integer>();
					statistics.put(type, countMap);
				}
				if (countMap.containsKey("total")) {
					totalPic = countMap.get("total");
				}
				totalPic += total;
				switch (state.intValue()) {
				// case PhotoState.state0:
				// unusedPic += total;
				// break;
//				case PhotoState.state80:
//					sbackPic += total;
//					recognizedTotal += total;
//					break;
				case PhotoState.state100:
				case PhotoState.state101:
					BigDecimal pzStatus = (BigDecimal) map.get("vbillstatus");
					BigDecimal recognized = (BigDecimal) map.get("iautorecognize");
					if (pzStatus != null) {
						int formalVoucher = 0;
						if (countMap.containsKey("formal")) {
							formalVoucher = countMap.get("formal");
						}
						if (pzStatus.intValue() == -1) {
							if (recognized == null || recognized.intValue() != 1) {
								int tempVoucher = 0;
								if (countMap.containsKey("temp")) {
									tempVoucher = countMap.get("temp");
								}
								tempVoucher += total;
								countMap.put("temp", tempVoucher);
							} else {
								formalVoucher += total;
							}

						} else if (pzStatus.intValue() > 0) {
							formalVoucher += total;
						}
						countMap.put("formal", formalVoucher);
					}
					vouchertotal  +=total;
					break;
				case PhotoState.state205:
					invalidTotal +=total;
					break;
				case PhotoState.state80:
					break;
				case PhotoState.state102:
					repeatTotal += total;
					break;
				case 18:
					processTotal += total;
					break;
				default:
//					if (state.intValue() == PhotoState.state102) {
//						repeatTotal += total;
////						recognizedTotal += total;
//					}
					// 未处理或处理中
					int unusedPic = 0;
					if (countMap.containsKey("unused")) {
						unusedPic = countMap.get("unused");
					}
					unusedPic += total;
					countMap.put("unused", unusedPic);
					unvoucher+=total;
					break;
				}
				countMap.put("total", totalPic);
			} else {
				unusedTotal += total;
			}
		}
		statistics.put("total", fullTotal);
		statistics.put("processTotal", processTotal);
		statistics.put("invalidTotal", invalidTotal);
		statistics.put("recognized", recognizedTotal);
		statistics.put("unused", unusedTotal);
		statistics.put("repeat", repeatTotal);
		statistics.put("vouchertotal", vouchertotal);
		statistics.put("unvoucher", unvoucher);
		return statistics;
	}

	private void saveOcrData(ImageLibraryVO ilib, String invoicedata, String filename) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(ilib.getPk_corp());
		String ipicflow = corpvo.getDef4();
		if (StringUtil.isEmpty(ipicflow))
			return;
		int iflow = Integer.parseInt(ipicflow);
		if (PhotoState.TREAT_TYPE_7 == iflow) {

			OcrImageGroupVO ig = new OcrImageGroupVO();
			ig.setPk_corp(ilib.getPk_corp());
			ig.setCoperatorid(ilib.getCoperatorid());
			ig.setDoperatedate(new DZFDate());
			ig.setIstate(Integer.valueOf(0));
			ig.setPk_selectcorp(ilib.getPk_corp());
			ig.setImagecounts(Integer.valueOf(1));
			ig.setIscomplete(DZFBoolean.TRUE);
			ig.setDr(Integer.valueOf(0));
			long maxCode = getMaxOcrImageGroupCode(ilib.getPk_corp());
			if (maxCode > 0L)
				ig.setGroupcode((new StringBuilder(String.valueOf(maxCode + 1L))).toString());
			else
				ig.setGroupcode((new StringBuilder(String.valueOf(getCurDate()))).append("0001").toString());
			OcrImageLibraryVO il = new OcrImageLibraryVO();
			il.setImgname(ilib.getImgname());
			il.setPk_corp(ilib.getPk_corp());
			il.setPk_custcorp(ilib.getPk_corp());
			il.setCoperatorid(ilib.getCoperatorid());
			il.setCvoucherdate(ilib.getCvoucherdate());
			il.setDoperatedate(new DZFDate());
			il.setDr(Integer.valueOf(0));
			il.setIstate(Integer.valueOf(StateEnum.INIT.getValue()));
			il.setIszd(DZFBoolean.FALSE);
			il.setIsinterface(DZFBoolean.FALSE);
			il.setIbusinesstype(Integer.valueOf(1));
			il.setIspartition(DZFBoolean.FALSE);
			il.setIorder(Integer.valueOf(getOrderNo(il)));
			il.setReason("上传成功");
			il.setImgmd(ilib.getImgmd());
			il.setImgpath(ilib.getImgpath());
			il.setPdfpath(ilib.getPdfpath());
			il.setSmallimgpath(ilib.getSmallimgpath());
			il.setMiddleimgpath(ilib.getMiddleimgpath());
			il.setCrelationid(ilib.getPk_image_library());
			il.setOldfilename(filename);
			il.setSystem(Integer.toString(PhotoState.SOURCEMODE_20));
			addScannerInfo(il, invoicedata);
			ig.addChildren(il);
			singleObjectBO.saveObject(ig.getPk_corp(), ig);
		}
	}

	private synchronized int getOrderNo(OcrImageLibraryVO vo) {
		String sql = (new StringBuilder(" select  max(iorder)  from ynt_image_ocrlibrary where  pk_custcorp = '"))
				.append(vo.getPk_custcorp()).append("' and cvoucherdate= '").append(vo.getCvoucherdate()).append("'")
				.toString();
		BigDecimal maxDocNo = (BigDecimal) singleObjectBO.executeQuery(sql, null, new ObjectProcessor());
		if (maxDocNo != null) {
			int maxNo = maxDocNo.intValue() + 1;
			return maxNo;
		} else {
			return 0;
		}
	}

	private void addScannerInfo(OcrImageLibraryVO vo, String invoicedata) {

		if (StringUtil.isEmpty(invoicedata)) {
			return;
		}
		JSONObject object = JSON.parseObject(invoicedata);

		if (object == null || object.size() == 0) {
			return;
		}

		String[] hcodes = InvoiceColumns.OCR_CODES;
		String[] hnames = InvoiceColumns.SCANNER_CODES;

		int hlen = hcodes.length;

		for (int m = 0; m < hlen; m++) {
			vo.setAttributeValue(hcodes[m], object.get(hnames[m]));
		}
		vo.setVinvoicecode(substring(vo.getVinvoicecode(), 100));
		vo.setVinvoiceno(substring(vo.getVinvoiceno(), 100));
		vo.setVpurchname(substring(vo.getVpurchname(), 200));
		vo.setVpurchtaxno(substring(vo.getVpurchtaxno(), 100));
		vo.setVsalename(substring(vo.getVsalename(), 200));
		vo.setVsaletaxno(substring(vo.getVsaletaxno(), 100));
		vo.setVtotaltaxcapital(substring(vo.getVtotaltaxcapital(), 100));

		if (!StringUtil.isEmpty(vo.getDinvoicedate())) {
			if (vo.getDinvoicedate().length() == 6) {
				vo.setDinvoicedate("20" + vo.getDinvoicedate());
			} else {
				vo.setDinvoicedate(vo.getDinvoicedate());
			}
		}
		vo.setNmny(subnmny(vo.getNmny(), 16));
		// vo.setNtax(subnmny(vo.getNtax(), 16));
		vo.setNtaxnmny(subnmny(vo.getNtaxnmny(), 16));
		vo.setNtotaltax(subnmny(vo.getNtotaltax(), 16));
		if (StringUtil.isEmpty(vo.getInvoicetype())) {
			if (StringUtil.isEmpty(vo.getCheckcode())) {
				vo.setInvoicetype(ImageTypeConst.SPECIA_INVOICE_NAME);
			} else {
				vo.setInvoicetype(ImageTypeConst.ORDINARY_INVOICE_NAME);
			}
		} else {
			vo.setInvoicetype(substring(vo.getInvoicetype(), 30));
		}

		vo.setCheckcode(vo.getCheckcode());
		vo.setVocrpurchname(vo.getVpurchname());
		vo.setVocrsalename(vo.getVsalename());
		vo.setIway(3);// 扫描仪识别
	}

	private String subnmny(String str, int len) {
		if (StringUtil.isEmpty(str)) {
			return null;
		} else {
			int len1 = str.length();
			if (len1 > len) {
				if (str.indexOf("￥") >= 0) {
					str = str.substring(1, len);
				} else {
					str = str.substring(0, len);
				}
			} else {
				if (str.indexOf("￥") >= 0) {
					str = str.substring(1, str.length());
				}
			}
			return str;
		}
	}

	@Override
	public ImageLibraryVO uploadSingFileByFastTax(CorpVO corpvo, UserVO uservo, MultipartFile[] infiles, String[] filenames,
			String g_id, String period, String invoicedata,String pjlxType) throws DZFWarpException {
		ImageLibraryVO il = null;
		try {
			// 来源扫描仪上传的图片 保存结果信息
			if (StringUtil.isEmpty(invoicedata)) {
				invoicedata = "{}";
			}
			il = uploadSingFile1(corpvo, uservo, infiles, g_id, period, invoicedata, pjlxType);

			// 来源扫描仪上传的图片 保存结果信息
			saveOcrData(il, invoicedata, filenames[0]);
		} catch (Exception e) {
			throw new BusinessException(e.getMessage());
		}
		return il;
	}

	@Override
	public void processSplitGroup(String pk_corp, String pk_image_group) throws DZFWarpException {
		ImageGroupVO group = queryGrpByID(pk_corp, pk_image_group);
		
		if(group == null)
			return;
		
		if (group.getIsuer() != null && group.getIsuer().booleanValue()) {
			throw new BusinessException("已制证不能拆分");
		} else if (group.getIstate() != null && PhotoState.state80 == group.getIstate()) {
			throw new BusinessException("已退回不能拆分");
		}
		String sql = " select pk_image_library from ynt_image_library "
				+ "where nvl(dr,0) = 0 and pk_corp = ? and pk_image_group = ? order by pk_image_library";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_image_group);
		List<String> libs = (List<String>) singleObjectBO.executeQuery(sql, sp, new ColumnListProcessor());
		int len = libs.size();
		if (len == 1) {
			throw new BusinessException("当前图片组不需要拆分");
		}
		List<ImageLibraryVO> libvos = new ArrayList<ImageLibraryVO>();
		List<ImageGroupVO> groups = new ArrayList<ImageGroupVO>();
		String[] ids = IDGenerate.getInstance().getNextIDS(pk_corp, len - 1);
		long maxCode = getNowMaxImageGroupCode(pk_corp);
		for (int i = 1; i < len; i++) {
			String groupId = ids[i - 1];
			ImageGroupVO clone = (ImageGroupVO) group.clone();
			if (maxCode > 0) {
				clone.setGroupcode(maxCode + 1 + "");
			} else {
				clone.setGroupcode(getCurDate() + "0001");
			}
			clone.setPk_image_group(groupId);
			groups.add(clone);

			ImageLibraryVO libvo = new ImageLibraryVO();
			libvo.setPk_image_group(groupId);
			libvo.setPk_corp(pk_corp);
			libvo.setPk_image_library(libs.get(i));
			libvos.add(libvo);
			maxCode++;
		}
		singleObjectBO.insertVOWithPK(pk_corp, groups.toArray(new ImageGroupVO[0]));
		singleObjectBO.updateAry(libvos.toArray(new ImageLibraryVO[0]), new String[] { "pk_image_group" });
		
		// 更新清单的图片组id
		updateRefGroupId(libvos);
	}

	@Override
	public void saveMsg(String msgkey, String pk_corp, String selYear, String selMon, UserVO uservo, ImageLibraryVO il)
			throws DZFWarpException {
		String newkey = pk_corp + "_" + selYear + "_" + selMon;
		if (!StringUtil.isEmpty(msgkey) && msgkey.equals(newkey)) {// 直接返回，不产生消息
			return;
		}

		StringBuffer sf = new StringBuffer();
		sf.append(" Select distinct re.cuserid ");
		sf.append(" From bd_corp cp ");
		sf.append(" join sm_user_role re on re.pk_corp = cp.pk_corp ");
		sf.append(" Where re.pk_corp = ? and nvl(re.dr, 0) = 0 ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);

		List<String> list = (List<String>) singleObjectBO.executeQuery(sf.toString(), sp,
				new ColumnListProcessor("cuserid"));

		if (list == null || list.size() == 0) {
			return;
		}

		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String content = String.format("%s 公司 %s 会计人员在 %s 年 %s 月上传票据，请尽快制单",
				new String[] { corpvo.getUnitname(), uservo.getUser_name(), selYear, selMon });

		MsgAdminVO msgvo = null;
		List<MsgAdminVO> msgList = new ArrayList<MsgAdminVO>();
		for (String ss : list) {
			msgvo = new MsgAdminVO();
			msgvo.setCuserid(ss);
			msgvo.setVcontent(content);
			msgvo.setSendman(uservo.getPrimaryKey());
			msgvo.setVsenddate(new DZFDateTime().toString());
			msgvo.setSys_send(ISysConstants.SYS_KJ);
			msgvo.setVtitle(null);
			msgvo.setIsread(DZFBoolean.FALSE);
			msgvo.setPk_corpk(pk_corp);
			msgvo.setDr(0);
			msgvo.setPk_bill(il.getPk_image_group());
			msgvo.setMsgtype(MsgtypeEnum.MSG_TYPE_APPLY_VOUCHER.getValue());
			msgvo.setMsgtypename(MsgtypeEnum.MSG_TYPE_APPLY_VOUCHER.getName());

			msgList.add(msgvo);
		}

		singleObjectBO.insertVOArr(pk_corp, msgList.toArray(new MsgAdminVO[0]));
	}

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public List<FastOcrStateInfoVO> getOcrStateInfoVOS(String pk_corp, String sourceids) throws DZFWarpException {

		if (StringUtil.isEmpty(sourceids)) {
			return null;
		}
		StringBuffer sf = new StringBuffer();
		sf.append(
				" select y.imgname,to_char(y.ts,'yyyy-mm-dd HH24:mi:ss:ff') dlastrowtime,g.istate,y.sourceid,y.pk_image_ocrlibrary, y.pk_corp,g.dr,");
		sf.append(" h.pk_tzpz_h,h.pzh, h.doperatedate, h.iautorecognize, ");
		sf.append(
				" h1.pk_tzpz_h pk_tzpz_h1,h1.pzh pzh1, h1.doperatedate doperatedate1, h1.iautorecognize iautorecognize1");
		sf.append(" from ynt_image_ocrlibrary y ");
		sf.append(" left join ynt_image_library p on y.crelationid = p.pk_image_library ");
		sf.append(" left join ynt_image_group g on p.pk_image_group = g.pk_image_group ");
		sf.append(" left join ynt_tzpz_h h on g.pk_image_group = h.pk_image_group ");
		sf.append(" left join ynt_tzpz_h h1 on y.def10 = h1.pk_tzpz_h ");
		sf.append(" where y.system = 20   ");
		String srcids = SqlUtil.buildSqlForIn("y.sourceid", sourceids.split(","));
		sf.append(" and " + srcids);
		SQLParameter sp = new SQLParameter();
		
		List<CorpVO> corpList=iZncsService.queryWhiteListCorpVOs(pk_corp);
		if(corpList==null||corpList.size()==0){
			sf.append(" and y.pk_corp = ?   ");
			sp.addParam(pk_corp);
		}else{
			List<String> corpKeyList=new ArrayList<String>();
			for(int i=0;i<corpList.size();i++){
				corpKeyList.add(corpList.get(i).getPk_corp());
			}
			sf.append(" and "+SqlUtil.buildSqlForIn("y.pk_corp", corpKeyList.toArray(new String[0])));
		}
		List<FastOcrStateInfoVO> list = (List<FastOcrStateInfoVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new ResultSetProcessor() {
					@Override
					public Object handleResultSet(ResultSet resultset) throws SQLException {
						List<FastOcrStateInfoVO> result = new ArrayList<FastOcrStateInfoVO>();

						while (resultset.next()) {
							FastOcrStateInfoVO infovo = new FastOcrStateInfoVO();
							int istate = resultset.getInt("istate");
							infovo.setImgname(resultset.getString("imgname"));
							infovo.setIstate(istate);
							infovo.setPk_image_ocrlibrary(resultset.getString("pk_image_ocrlibrary"));
							infovo.setSourceid(resultset.getString("sourceid"));
							infovo.setPk_corp(resultset.getString("pk_corp"));
							infovo.setDr(resultset.getInt("dr"));
							String time = resultset.getString("dlastrowtime");
							if (!StringUtil.isEmpty(time)) {
								infovo.setDlastrowtime(time);
							}
							if (istate == PhotoState.state102) {
								infovo.setPk_tzpz_h(resultset.getString("pk_tzpz_h1"));
								infovo.setPzh(resultset.getString("pzh1"));
								infovo.setDoperatedate(resultset.getString("doperatedate1"));
								infovo.setIautorecognize(resultset.getInt("iautorecognize1"));
							} else {
								infovo.setPk_tzpz_h(resultset.getString("pk_tzpz_h"));
								infovo.setPzh(resultset.getString("pzh"));
								infovo.setDoperatedate(resultset.getString("doperatedate"));
								infovo.setIautorecognize(resultset.getInt("iautorecognize"));
							}
							result.add(infovo);
						}
						return result;
					}
				});

		if (list == null || list.size() == 0) {
			return null;
		}
		// 分组
		Map<String, List<FastOcrStateInfoVO>> maps = DZfcommonTools.hashlizeObject(list, new String[] { "sourceid" });
		List<FastOcrStateInfoVO> volist = new ArrayList<FastOcrStateInfoVO>();
		for (String key : maps.keySet()) {
			List<FastOcrStateInfoVO> zlist = maps.get(key);
			// 取这个zlist中dlastrowtime 最新的。
			FastOcrStateInfoVO vo = getLastNewRow(zlist);
			if (vo != null) {
				volist.add(vo);
			}

		}
		String vstate = null;
		for (FastOcrStateInfoVO vo : volist) {
			int istate = vo.getIstate();
			int iautorecognize = vo.getIautorecognize();

			// 凭证 状态 已制单 -- 1 暂存凭证 ---0
			if (iautorecognize != 1) {
				vo.setIautorecognize(0);
			}
			if (istate == PhotoState.state102) {
				// 重复
				vstate = "0";// 重复
			} else if (istate == PhotoState.state101 || istate == PhotoState.state100) {
				// 制单
				if(istate == PhotoState.state100){//手动修改过的凭证 未走识别的认为手动制证
					if (iautorecognize != 1) {
						vo.setIautorecognize(1);
					}
				}
				vstate = "1";// 制单
			} else if (istate == PhotoState.state205 || istate == PhotoState.state0 || istate == PhotoState.state1) {
				if (vo.getDr() == 1 || istate == PhotoState.state205) {
					vstate = "2";// 删除
				} else {
					vstate = "3";// 已上传
				}
			} else {
				// 处理中
				vstate = "3";// 已上传
			}
			vo.setVstate(vstate);
			if (!StringUtil.isEmpty(vo.getPzh())) {
				vo.setPzh("记" + vo.getPzh());
			}
		}
		return volist;
	}

	private FastOcrStateInfoVO getLastNewRow(List<FastOcrStateInfoVO> zlist) {
		if (zlist == null || zlist.size() == 0)
			return null;
		if (zlist.size() == 1) {
			return zlist.get(0);
		}
		FastOcrStateInfoVO avo = null;
		String atime = null;
		for (FastOcrStateInfoVO vo : zlist) {
			if (avo == null) {
				avo = vo;
			}
			if (atime == null) {
				atime = vo.getDlastrowtime();
			} else {
				if (atime.compareTo(vo.getDlastrowtime()) < 0) {
					atime = vo.getDlastrowtime();
					avo = vo;
				}
			}
		}
		return avo;
	}

	private String substring(String str, int len) {
		if (StringUtil.isEmpty(str)) {
			return null;
		} else {
			int len1 = str.length();
			if (len1 > len) {
				str = str.substring(0, len);
			}
			return str;
		}
	}

	@Override
	public void recogImage(String pk_corp, String[] imageKeys) throws DZFWarpException {
		try {
			if (imageKeys == null || imageKeys.length == 0)
				return;
			recogservice.creatPZ(pk_corp, imageKeys);

		} catch (Exception e) {
			log.error("错误", e);
			throw new BusinessException(e.getMessage());
		}
	}
	
	// 是否是智能识别
	private boolean isAutoRecognize(String flowid) {
		boolean isAuto = false;
		if (!StringUtil.isEmpty(flowid)) {
			int iflow = Integer.parseInt(flowid);
			if (PhotoState.TREAT_TYPE_7 == iflow) {
				isAuto = true;
			}
		}
		return isAuto;
	}

	@Override
	public String processMergeGroup(String pk_corp, String destGroup, List<String> groups)
			throws DZFWarpException {
		if (groups == null || groups.size() == 0) {
			return destGroup;
		}
		if (StringUtil.isEmpty(destGroup)) {
			destGroup = groups.remove(0);
		} else {
			groups.remove(destGroup);
		}
		if (groups.size() == 0) {
			return destGroup;
		}

		String inSQL = SQLHelper.getInSQL(groups);
		// 记录原图片组SQL的参数
		SQLParameter recordGroupSp = new SQLParameter();
		SQLParameter sp = new SQLParameter();
		sp.addParam(destGroup);
		sp.addParam(pk_corp);
		recordGroupSp.addParam(pk_corp);
		for (String pk_group : groups) {
			sp.addParam(pk_group);
			recordGroupSp.addParam(pk_group);
		}
		// 记录原图片组
		singleObjectBO.executeUpdate(
				"update ynt_image_library set old_pk_image_group = pk_image_group where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 and old_pk_image_group is null ",
						recordGroupSp);
		// 更新图片组
		singleObjectBO.executeUpdate(
				"update ynt_image_library set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.deleteByPKs(ImageGroupVO.class, groups.toArray(new String[0]));

		
		singleObjectBO.executeUpdate(
				"update ynt_vatsaleinvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.executeUpdate(
				"update ynt_vatincominvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.executeUpdate(
				" update ynt_bankstatement set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_image_group in  " + inSQL
				, sp);
		
		singleObjectBO.executeUpdate(
				" update ynt_bankbilltostatement set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_image_group in  " + inSQL
				, sp);
		
		singleObjectBO.executeUpdate(
				" update ynt_ictrade_h set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_image_group in  " + inSQL
				, sp);
		
		singleObjectBO.executeUpdate(
				" update ynt_interface_invoice set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_image_group in  " + inSQL
				, sp);
		
		singleObjectBO.executeUpdate(
				" update ynt_assetcard set pk_image_group = ? where pk_corp = ? and nvl(dr,0) = 0 and pk_image_group in  " + inSQL
				, sp);
		return destGroup;
	}

	@Override
	public void processSplitMergedGroup(String pk_corp, String pk_image_group)
			throws DZFWarpException {
		ImageGroupVO group = queryGrpByID(pk_corp, pk_image_group);
		if(group == null)
			return;
		
		if (group.getIsuer() != null && group.getIsuer().booleanValue()
				|| group.getIstate() != null && PhotoState.state80 == group.getIstate()) {
			return;
		}
		String sql = " select pk_image_library,pk_corp, old_pk_image_group from ynt_image_library "
				+ "where nvl(dr,0) = 0 and pk_corp = ? and pk_image_group = ? order by pk_image_library";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_image_group);
		List<ImageLibraryVO> libs = (List<ImageLibraryVO>) singleObjectBO
				.executeQuery(sql, sp, new BeanListProcessor(ImageLibraryVO.class));
		if (libs.size() <= 1) {
			// 不需要拆分
			return;
		}
		Map<String, List<ImageLibraryVO>> groupMap = new LinkedHashMap<String, List<ImageLibraryVO>>();
		for (ImageLibraryVO imageLibraryVO : libs) {
			String key = imageLibraryVO.getOld_pk_image_group();
			List<ImageLibraryVO> splitList = groupMap.get(key);
			if (splitList == null) {
				splitList = new ArrayList<ImageLibraryVO>();
				groupMap.put(key, splitList);
			}
			splitList.add(imageLibraryVO);
		}
		if (groupMap.size() <= 1) {
			// 不需要拆分
			return;
		}
		List<ImageLibraryVO> libvos = new ArrayList<ImageLibraryVO>();
		List<ImageGroupVO> groups = new ArrayList<ImageGroupVO>();
		String[] ids = IDGenerate.getInstance().getNextIDS(pk_corp, groupMap.size() - 1);
		long maxCode = getNowMaxImageGroupCode(pk_corp);
		int i = 0;
		for (List<ImageLibraryVO> imgList : groupMap.values()) {
			// 第一组延用原来图片组，后面的生成新组
			if (i > 0) {
				String groupId = ids[i - 1];
				ImageGroupVO clone = (ImageGroupVO) group.clone();
				if (maxCode > 0) {
					clone.setGroupcode(maxCode + 1 + "");
				} else {
					clone.setGroupcode(getCurDate() + "0001");
				}
				clone.setPk_image_group(groupId);
				groups.add(clone);
				for (ImageLibraryVO imageLibraryVO : imgList) {
					imageLibraryVO.setPk_image_group(groupId);
					imageLibraryVO.setOld_pk_image_group(null);
				}
				ImageLibraryVO libvo = new ImageLibraryVO();
				libvo.setPk_image_group(groupId);
				libvos.addAll(imgList);
				maxCode++;
			}
			i++;
		}

		if (groups.size() > 0) {
			singleObjectBO.insertVOWithPK(pk_corp, groups.toArray(new ImageGroupVO[0]));
			singleObjectBO.updateAry(libvos.toArray(new ImageLibraryVO[0]),
					new String[] { "pk_image_group", "old_pk_image_group" });
			updateRefGroupId(libvos);
		}
	}
	
	// 更新清单的图片组id
	private void updateRefGroupId(List<ImageLibraryVO> libvos) {
		if (libvos == null || libvos.size() == 0) {
			return;
		}
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		for (ImageLibraryVO libvo:libvos) {
			SQLParameter sp = new SQLParameter();
			sp.addParam(libvo.getPk_image_group());
			sp.addParam(libvo.getPk_image_library());
			sp.addParam(libvo.getPk_corp());
			list.add(sp);
		}
		String sql1 =" update ynt_vatincominvoice set pk_image_group=? where pk_image_library =?  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
		sql1 =" update ynt_vatsaleinvoice set pk_image_group=? where pk_image_library =?  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
		sql1 =" update ynt_bankstatement set pk_image_group=? where sourcebillid =?  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
		sql1 =" update ynt_bankbilltostatement set pk_image_group=? where sourcebillid =?  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
		sql1 =" update ynt_ictrade_h set pk_image_group=? where pk_image_library =?  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
		sql1 =" update ynt_interface_invoice set pk_image_group=? where ocr_id =(select y.pk_image_ocrlibrary from  ynt_image_ocrlibrary y where y.crelationid =? )  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
		sql1 =" update ynt_assetcard set pk_image_group=? where pk_image_library =?  and pk_corp=? and nvl(dr,0)=0";
		singleObjectBO.executeBatchUpdate(sql1, list.toArray(new SQLParameter[list.size()]));
	}
}
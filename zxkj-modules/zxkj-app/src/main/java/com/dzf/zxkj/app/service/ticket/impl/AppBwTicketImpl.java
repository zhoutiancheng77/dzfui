package com.dzf.zxkj.app.service.ticket.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpSession;

import com.dzf.zxkj.app.service.ticket.IAppBwTicket;
import com.dzf.zxkj.app.utils.AppQueryUtil;
import com.dzf.zxkj.base.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
@Slf4j
@Service("appbwbusihand")
public class AppBwTicketImpl implements IAppBwTicket {

//	@Autowired
//	private IAppCorpService corpservice ;
//
//	@Autowired
//	private SingleObjectBO singleObjectBO;
//
//	@Autowired
//	private IVoucherService gl_tzpzserv;
//
//	@Autowired
//	private IAppPubservice apppubservice;
//
//	@Autowired
//	private ICorpSign corpsign;
//
//	@Autowired
//	private IUserService userServiceImpl;

	

//		private String getDzfAdminUrl(String name) {
//		String value = AppQueryUtil.getProperValue("dzfadmin.properties", name);
//		return value;
//	}
//
//	/**
//	 * 获取大账房管理端，收票的接口
//	 * @return
//	 */
//	public IDzfCollTicketService getAdminRemoteCollTicketService(){
//		HessianProxyFactory factory = new HessianProxyFactory();
//		factory.setOverloadEnabled(true);
//		try {
//			//创建IService接口的实例对象
//			return (IDzfCollTicketService) factory.create(IDzfCollTicketService.class, getDzfAdminUrl("dzfadmincollticket"));
//		} catch (MalformedURLException e) {
//			log.error(e.getMessage());
//			throw new BusinessException(e.getMessage());
//		} catch (BusinessException be){
//			log.error(be.getMessage());
//			throw new BusinessException(be.getMessage());
//		}
//	};
//

//
//
//	@Override
//	public InvoiceHVO genInvoiceFromBwTicket(String pk_corp, String account_id, InvoiceBean invoicevo_req)
//			throws DZFWarpException {
//
//		if (invoicevo_req == null || invoicevo_req.getInvoiceInfo() == null) {
//			throw new BusinessException("您发票信息为空!");
//		}
//
//		String invoicecode_req = invoicevo_req.getInvoiceInfo().getInvoiceCode();// 发票代码
//
//		String invoicenum_req = invoicevo_req.getInvoiceInfo().getInvoiceNumber();// 发票号码
//		// 转换成自己vo
//		if (StringUtil.isEmpty(invoicecode_req)) {
//			throw new BusinessException("您发票代码为空!");
//		}
//
//		InvoiceHVO hvo = getInvoiceVO(invoicecode_req, invoicenum_req);// 获取现有系统的数据
//
//		// 如果没有先存发票信息
//		if (hvo == null) {
//			hvo = new InvoiceHVO();
//			hvo.setPk_corp(pk_corp);
//			// 发票基本信息
//			InvoiceInfoBean infobean = invoicevo_req.getInvoiceInfo();
//			copyBeanToVO(infobean, hvo);
//
//			// 销售方信息
//			com.dzf.model.app.bwticket.InvoiceResultBean.ResultBean.InvoiceBean.SalesUnitInfoBean saleunitbean
//			   = invoicevo_req.getSalesUnitInfo();
//			copyBeanToVO(saleunitbean, hvo);
//
//			// 购买方信息
//			PurchaserUnitInfoBean purbean = invoicevo_req.getPurchaserUnitInfo();
//			copyBeanToVO(purbean, hvo);
//
//			// 金额信息
//			CommodityInfoBean commoditybean = invoicevo_req.getCommodityInfo();
//			List<CommodityListBean> beanvos = commoditybean.getCommodityList();
//			copyBeanToVO(commoditybean, hvo);
//
//			// 子表信息
//			List<CommdityInfoBVO> bvos = new ArrayList<CommdityInfoBVO>();
//
//			if (beanvos == null || beanvos.size() == 0) {
//				throw new BusinessException("发票子信息不全!");
//			}
//			for (CommodityListBean beanvo : beanvos) {
//				CommdityInfoBVO bvo = new CommdityInfoBVO();
//				bvo.setPk_corp(pk_corp);
//				copyBeanToVO(beanvo, bvo);
//				bvos.add(bvo);
//			}
//			hvo.setChildren(bvos.toArray(new CommdityInfoBVO[0]));
//			hvo = (InvoiceHVO) singleObjectBO.saveObject(pk_corp, hvo);
//		}
//		if(StringUtil.isEmpty(hvo.getInvoicetype()) || "2".equals(hvo.getInvoicetype())){
//			throw new BusinessException("您票已作废的不能生成凭证!");
//		}
//		if("3".equals(hvo.getInvoicetype())){
//			throw new BusinessException("空白票暂不生成凭证!");
//		}
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(hvo.getPrimaryKey());
//		List<InvoiceHVO> invoicevolist =  (List<InvoiceHVO>) singleObjectBO.executeQuery(" pk_bw_invoice_h = ? ", sp, new Class[]{InvoiceHVO.class,CommdityInfoBVO.class});//
//		hvo = invoicevolist.get(0);
//		// 生成凭证信息
//		if (hvo.getInvoicedate() == null) {
//			throw new BusinessException("开票日期不能为空!");
//		}
//
//		return hvo;
//	}
//
//	public void copyBeanToVO(Object objsour, SuperVO hvo) {
//		try {
//			if(objsour == null){
//				return;
//			}
//			Field[] fields = objsour.getClass().getFields();
//
//			if (fields != null && fields.length > 0) {
//
//				for (Field field : fields) {
//					if (field.get(objsour) == null) {
//						continue;
//					}
//					String type = field.getType().toString();//得到此属性的类型
//					if(type.endsWith("String") && StringUtil.isEmpty((String)field.get(objsour))){
//						continue;
//					}
//					hvo.setAttributeValue(field.getName(), field.get(objsour));
//				}
//			}
//		} catch (SecurityException e) {
//			log.error("错误",e);
//		} catch (IllegalArgumentException e) {
//			log.error("错误",e);
//		} catch (IllegalAccessException e) {
//			log.error("错误",e);
//		}
//
//	}
//
//	private InvoiceHVO getInvoiceVO(String invoicecode_req,String invoicenum_req) {
//		SQLParameter sp = new SQLParameter();
//		// 查询票据信息
//		StringBuffer qrysql = new StringBuffer();
//		qrysql.append(" select * from ");
//		qrysql.append(" " + InvoiceHVO.TABLE_NAME);
//		qrysql.append(" where nvl(dr,0)=0");
//		qrysql.append("  and invoicecode = ? ");
//		qrysql.append("  and  invoicenumber = ? ");
//		sp.addParam(invoicecode_req);
//		sp.addParam(invoicenum_req);
//		List<InvoiceHVO> invoicelisthvo = (List<InvoiceHVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
//				new BeanListProcessor(InvoiceHVO.class));
//		if (invoicelisthvo != null && invoicelisthvo.size() > 0) {
//			InvoiceHVO hvo = invoicelisthvo.get(0);
//			return hvo;
//		}
//		return null;
//	}
//
//	@Override
//	public void saveDelVoucher(String pk_corp, String account_id,
//			InvalidInfoList invalidbean,SalesUnitInfoBean salebean ) throws DZFWarpException {
//
//		String invoicecode = invalidbean.getInvoiceCode();
//		String invoicenum = invalidbean.getInvoiceNumber();
//		String InvoicetypeCode = invalidbean.getInvoicetypeCode();
//
//		//作数据库保存的时候，不区分大小写，写入数据，需要区分
//		InvalidInvoiceVO delinvoicevo = new InvalidInvoiceVO();
//		delinvoicevo.setPk_corp(pk_corp);
//		copyBeanToVO(salebean, delinvoicevo);
//		copyBeanToVO(invalidbean, delinvoicevo);
//		singleObjectBO.saveObject(pk_corp, delinvoicevo);
//
//		//查找对应的发票信息
//		InvoiceHVO hvo = getInvoiceVO(invoicecode,invoicenum);
//
//		if(hvo == null){
//			throw new BusinessException("发票还未生成凭证!");
//		}
//
//		hvo.setInvoicetype(InvoicetypeCode);//作废
//
//		singleObjectBO.update(hvo, new String[]{"invoicetype"});
//
//		BwCreateVoucherPTImpl impl = new BwCreateVoucherPTImpl();
//
//		TzpzHVO tzpzhvo = impl.getPzFromCode(hvo.getInvoicecode(), hvo.getInvoicenumber(), pk_corp);
//
//		gl_tzpzserv.deleteVoucher(tzpzhvo);
//	}
//
//	private String Filename(String nameSuffix) {
//		String imgFileNm = UUID.randomUUID().toString() + nameSuffix;
//		return imgFileNm;
//	}
//
//	/**
//	 * 获取生成pdf文件的路径
//	 */
//	private String getPdfPath(CorpVO corpvo) {
//		return Common.imageBasePath + File.separator + corpvo.getUnitcode() + File.separator + CommonXml.getCurDate() ;
//	}
//
//	/**
//	 * 获取生成jpg文件的路径
//	 */
//	private String getjpgPath(CorpVO corpvo) {
//		return Common.imageBasePath + File.separator + corpvo.getUnitcode() +  File.separator + CommonXml.getCurDate() ;
//	}
//
//	@Override
//	public ImageGroupVO imageGenFromBwTicket(String url, String pk_corp,String account_id ,InvoiceHVO hvo) throws DZFWarpException {
//		ImageGroupVO temp_groupvo = new ImageGroupVO();
//		if(StringUtil.isEmpty(url)){
//			return temp_groupvo;
//		}
//		CorpVO cpvo = CorpCache.getInstance().get("", pk_corp);
//		//下载pdf文件
//		DownUrlFileUtil downfile = new DownUrlFileUtil();
//		String pdfpath = "";
//		try {
//			String filename =  Filename(".pdf");
//			pdfpath = getPdfPath(cpvo) + File.separator +filename;
//			downfile.downLoadFromUrl(url,filename,  getPdfPath(cpvo));
//		} catch (IOException e) {
//			log.error("错误",e);
//			return temp_groupvo;
//		}
//		//转换成jgp文件
//		String imgpath = convertoJpg(pdfpath, cpvo);
//		if(StringUtil.isEmpty(pdfpath)){
//			return temp_groupvo;
//		}
//		//生成图片组信息
//		ImageGroupVO groupvo = genImageGroup(cpvo, hvo, account_id, imgpath);
//		//生成图片组信息
////		hvo.setImagepth(imgpath);
//		hvo.setPdfpath(pdfpath);
//		hvo.setPk_image_group(groupvo.getPrimaryKey());
//		singleObjectBO.update(hvo, new String[]{ "pdfpath"});
//
//		return groupvo;
//	}
//
//	private ImageGroupVO genImageGroup(CorpVO corpvo,InvoiceHVO hvo,String account_id,String path) {
//		ImageGroupVO groupvo = new ImageGroupVO();
//		groupvo.setPk_corp(corpvo.getPk_corp());
//		groupvo.setCoperatorid(account_id);
//		groupvo.setDoperatedate(new DZFDate());
//		groupvo.setMemo("");
//		groupvo.setMemo1("");//备注
//		groupvo.setSettlemode("");
//		groupvo.setImagecounts(1);// 图片张数
//		groupvo.setMny(hvo.getTotaltaxamount());//取合计
//		groupvo.setPk_ticket_h(hvo.getPrimaryKey());//票通的信息主键
//		// istate=0 标识对应直接生单,不启用切图、识图，以后用PhotoState常量类
//		groupvo.setIstate(PhotoState.state0);// 0
//		// 保存为凭证日期
//		if(!StringUtil.isEmpty(hvo.getInvoicedate())){
//			groupvo.setCvoucherdate(new DZFDate(hvo.getInvoicedate()));
//		}
//		long maxCode = getNowMaxImageGroupCode(corpvo.getPk_corp());
//		if (maxCode > 0) {
//			groupvo.setGroupcode(maxCode + 1 + "");
//		} else {
//			groupvo.setGroupcode(getCurDate() + "0001");
//		}
//		groupvo.setSessionflag(groupvo.getGroupcode());
//
//
//		ImageLibraryVO il = new ImageLibraryVO();
//		il.setImgpath(path);
//		il.setImgname(groupvo.getGroupcode() + "-001.jpg");
//		il.setPk_corp(corpvo.getPk_corp());
//		il.setCoperatorid(account_id);
//		il.setDoperatedate(new DZFDate());
//		if(!StringUtil.isEmpty(hvo.getInvoicedate())){
//			il.setCvoucherdate(new DZFDate(hvo.getInvoicedate()));//生成凭证时间
//		}
//
//		groupvo.addChildren(il);
//
//		groupvo = (ImageGroupVO) singleObjectBO.saveObject(corpvo.getPk_corp(), groupvo);
//
//		// 生成imagelibvo
//		return groupvo;
//	}
//
//	// 获取当前年月日
//	private static String getCurDate() {
//		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//		return format.format(Calendar.getInstance().getTime());
//	}
//
//	public long getNowMaxImageGroupCode(String pk_corp) {
//		SQLParameter params = new SQLParameter();
//		params.addParam(pk_corp);
//		params.addParam(new DZFDate().toString());
//
//		String sql = "select max(groupcode) from ynt_image_group where pk_corp = ? and doperatedate = ? ";
//		long maxcode = 0;
//
//		Object[] array = (Object[]) singleObjectBO.executeQuery(sql, params, new ArrayProcessor());
//		if (array != null && array.length > 0) {
//			if (array[0] != null)
//				maxcode = Long.parseLong(array[0].toString());
//		}
//		return maxcode;
//	}
//
//	private String convertoJpg(String pdfpath,CorpVO corpvo){
//		String jpgpath = getjpgPath(corpvo) +File.separator +Filename(".jpg");
//		try{
//			File file = new File(pdfpath);
//			log.error(file.getAbsolutePath());
//			PDDocument document = PDDocument.load(file);
//			PDFRenderer renderer = new PDFRenderer(document);
//			BufferedImage bufferedImage = renderer.renderImageWithDPI(0, 100, ImageType.RGB);
//			ImageIOUtil.writeImage(bufferedImage, jpgpath, 100);
//
//		}catch(Exception e){
//			log.error("错误",e);
//		}
//		return jpgpath;
//	}
//
//	@Override
//	public void genVoucherFromBwTicket(String pk_corp, InvoiceHVO vo, String userid, String pk_image_group)
//			throws DZFWarpException {
//		BwCreateVoucherPTImpl voucherptimpl = new BwCreateVoucherPTImpl();
//		voucherptimpl.createVoucher(pk_corp, vo, userid, pk_image_group);
//	}
//
//	@Override
//	public LoginResponseBeanVO genCorpFromBwCorp(String account, String iot,String bw_login, String isautologin,
//			String pk_corp_father, String vdevicemsg, String sourcesys) throws DZFWarpException {
//		if (StringUtil.isEmpty(account)) {
//			throw new BusinessException("帐号不存在!");
//		}
//
//		if (StringUtil.isEmpty(bw_login)) {
//			throw new BusinessException("公司信息不存在!");
//		}
//
//		JSONObject array = (JSONObject) JSON.parse(bw_login);
//		Map<String, String> bodymapping = FieldMapping.getFieldMapping(new LoginResultBean());
//		LoginResultBean resbean = DzfTypeUtils.cast(array, bodymapping, LoginResultBean.class,
//				JSONConvtoJAVA.getParserConfig());
//
//		if (resbean.getResult() == null || !"200".equals(resbean.getResult().getReturnCode())) {
//			if (resbean.getResult() != null) {
//				throw new BusinessException(resbean.getResult().getReturnMessages());
//			} else {
//				throw new BusinessException("注册公司有误!");
//			}
//		}
//		// 默认的代账公司是否已经
////		String corpcode = getCorpCode(pk_corp_father);
//		com.dzf.model.app.bwticket.LoginResultBean.ResultBean.SalesUnitInfoBean infobean = resbean.getResult()
//				.getSalesUnitInfo();
//		String corpname = infobean.getSalesUnitName();// 名称
//		String gsdz = infobean.getSalesUnitAddress();// 公司地址
//		String taxcode = infobean.getSalesUnitTaxId();// 税号
//		String invoicetype = resbean.getResult().getInvoiceType();//
//		String phone = infobean.getSalesUnitPhone();// 电话
//		String khhname = infobean.getSalesUnitBankName();// 开户行名称
//		String khhcode = infobean.getSalesUnitBankAcount();// 开户行编码
//
//		// 公司建账
//		String pk_corp = corpsign.saveCorp(account, pk_corp_father, corpname, gsdz, taxcode, phone, khhname,
//				khhcode, iot, invoicetype,"");
//
//		// 更新关联关系
//		UserBeanVO userBean = new UserBeanVO();
//		userBean.setPhone(account);
//		userBean.setAccount(account);
//		userBean.setPk_corp(pk_corp);
//		userBean.setCorpname(corpname);
//		corpservice.genTempUser(userBean, singleObjectBO, "", pk_corp);
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(account);
//		UserVO[] uvos = (UserVO[]) singleObjectBO.queryByCondition(UserVO.class, "nvl(dr,0)=0 and user_code = ? ", sp);
//		if (uvos == null || uvos.length == 0) {
//			throw new BusinessException("用户不存在!");
//		}
//
//		// 自动登录
//		LoginResponseBeanVO logbean = new LoginResponseBeanVO();
//		userBean.setPk_corp(pk_corp);
//		userBean.setUsercode(account);
//		userBean.setAccount_id(uvos[0].getCuserid());
//		userBean.setVdevicdmsg(vdevicemsg);
//		userBean.setSourcesys(sourcesys);
//
//		IAppLoginCorpService user320service = (IAppLoginCorpService) SpringUtils.getBean("user320service");
//
//		logbean = user320service.loginFromTel(userBean, logbean);
//
//		logbean.setIsautologin(IConstant.DEFAULT);
//
//		return logbean;
//	}
//
//	@Override
//	public void saveManageInfo(String account_id, String pk_corp, TaxManageInfoVO[] manageInfo)
//			throws DZFWarpException {
//		singleObjectBO.insertVOArr(pk_corp, manageInfo);
////		String period = manageInfo[0].getDataSendStartDate();
////		period = period.substring(0, 4) + "-" + period.substring(4, 6);
////		MsgAdminVO msg = new MsgAdminVO();
////		msg.setMsgtype(MsgtypeEnum.MSG_TYPE_CSTX.getValue());
////		msg.setPk_corpk(pk_corp);
////		msg.setVperiod(period);
////		sys_appmsgserv.saveAdminNsMsg(pk_corp, account_id, period, msg);
//	}
//
//	@Override
//	public BwBusiRespBean loginFromIot(String iot, String pwd,String vdevicemsg,String systype) throws DZFWarpException {
//
//		BwBusiRespBean bean = null;
//
//		if (StringUtil.isEmpty(iot)) {
//			throw new BusinessException("iot不能为空!");
//		}
//		if (StringUtil.isEmpty(pwd)) {
//			throw new BusinessException("密码不能为空!");
//		}
//
//		String descpwd = apppubservice.decryptPwd(systype, pwd);
//
//		// 通过iot获取公司的信息，同时获取用户的信息
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(iot);
//		sp.addParam(new Encode().encode(descpwd));
//		NsCorpVO[] nscorps = (NsCorpVO[]) singleObjectBO.queryByCondition(NsCorpVO.class, "nvl(dr,0)=0 and iot = ? and iotpassword = ?",
//				sp);
//
//		if (nscorps != null && nscorps.length > 0) {
//			//查询用户
//			sp.clearParams();
//			sp.addParam(nscorps[0].getPk_corp());
//			UserToCorp[] utcorp = (UserToCorp[]) singleObjectBO.queryByCondition(UserToCorp.class, " nvl(dr,0)=0 and pk_corp = ? ", sp);
//			bean = new BwBusiRespBean();
//			if(utcorp!=null && utcorp.length>0){//临时意义不大
//				UserVO uvo =userServiceImpl.queryUserJmVOByID(utcorp[0].getPk_user());// UserCache.getInstance().get(utcorp[0].getPk_user(), "");
//				bean.setAccount(uvo.getUser_code());
//				bean.setAccount_id(utcorp[0].getPk_user());
//			}
//			CorpVO cpvo = CorpCache.getInstance().get("", nscorps[0].getPk_corp());
//			bean.setCorpname(cpvo.getUnitname());
//			if(nscorps!=null && nscorps.length>0){
//				bean.setPk_corp(nscorps[0].getPk_corp());
//			}
//			if(utcorp!=null && utcorp.length>0){
//				bean.setPriid(utcorp[0].getPrimaryKey());
//			}
//			//生成token
//			// 生成token信息
//			HttpSession hs = SessionUtil.getSession();
//			if (hs != null){
//				hs.setAttribute(IGlobalConstants.login_user, bean.getAccount_id());
//				hs.setAttribute(IGlobalConstants.login_corp, nscorps[0].getPk_corp());
//			}
//			String tokenid = genTokenValue(vdevicemsg,bean.getPk_corp(), bean.getAccount_id(),"");
//			bean.setToken(tokenid);
//			bean.setRescode(IConstant.DEFAULT);
//			bean.setResmsg("登录成功!");
//		}else{
//			throw new BusinessException("帐号或密码错误!");
//		}
//
//		return bean;
//	}
//
//
//	private String genTokenValue(String vdevicdmsg ,String pk_corp,String account_id,String pk_temp_corp) {
//
//		String tokenid = null;
//
//		if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {
//			tokenid = GenTokenUtil.getToken(vdevicdmsg, account_id, pk_corp);
//		} else {
//			tokenid = GenTokenUtil.getToken(vdevicdmsg, account_id, pk_temp_corp);
//		}
//
//		return tokenid;
//	}
//
//	@Override
//	public BwBusiRespBean saveBwIotRegisterCorp(BwBusiReqBeanVo bwreqvo) throws DZFWarpException {
//
//		//注册校验
//		validateRegister(bwreqvo);
//
//		String account = bwreqvo.getPhone();
//
//		String pk_corp_father =  AppQueryUtil.getDeafultFatherid();
//
//		String descpwd = apppubservice.decryptPwd(bwreqvo.getSystype(), bwreqvo.getPassword());
//
//		//公司建账
//		String pk_corp = corpsign.saveCorp(account, pk_corp_father,
//				bwreqvo.getCorpname(), bwreqvo.getCorpaddr(), bwreqvo.getSh(), bwreqvo.getLxdh(), bwreqvo.getKhh(), bwreqvo.getKhzh(),
//				bwreqvo.getIot(), bwreqvo.getKplx(),descpwd);
//
//		boolean isexist = apppubservice.isExistUser(account);
//
//		if (!isexist) {
//			// 开始用户注册
//			TempUserRegVO tempuservo = new TempUserRegVO();
//			tempuservo.setUser_code(account);
//			tempuservo.setUser_password(new Encode().encode(descpwd));
//			tempuservo.setUser_name(account);
//			tempuservo.setApp_user_qq(account);
//			tempuservo.setPhone(account);
//			tempuservo.setIstate(IConstant.TWO);
//			SuperVO vo = singleObjectBO.saveObject(Common.tempidcreate, tempuservo);
//		}
//
//		//生成用户/公司关联关系
//		UserBeanVO userBean = new UserBeanVO();
//		userBean.setPhone(account);
//		userBean.setAccount(account);
//		userBean.setPk_corp(pk_corp);
//		userBean.setCorpname(bwreqvo.getCorpname());
//		corpservice.genTempUser(userBean, singleObjectBO, "", pk_corp);
//
//		//登录
//		BwBusiRespBean bwrespvo = loginFromIot(bwreqvo.getIot(),  bwreqvo.getPassword(),  bwreqvo.getVdevicdmsg(),bwreqvo.getSystype());
//
//		bwrespvo.setResmsg("注册成功!");
//
//		return bwrespvo;
//	}
//
//	private void validateRegister(BwBusiReqBeanVo bwreqvo) {
//		// 判断公司名称是否存在
//		if (StringUtil.isEmpty(bwreqvo.getCorpname())) {
//			throw new BusinessException("公司名称不能为空!");
//		}
//
//		if (StringUtil.isEmpty(bwreqvo.getPhone())) {
//			throw new BusinessException("手机号不能为空!");
//		}
//
//		if (StringUtil.isEmpty(bwreqvo.getIot())) {
//			throw new BusinessException("iot号不能为空!");
//		}
//
//		if (StringUtil.isEmpty(bwreqvo.getPassword())) {
//			throw new BusinessException("密码不能为空!");
//		}
//
//		// 查看验证码是否存在
//		String str = CommonServ.resetUserIdentify(bwreqvo, bwreqvo.getIdentify(), true);
//
//		if (!StringUtil.isEmpty(str)) {
//			throw new BusinessException(str);
//		}
//
//		// 判断公司名称是否已经存在
//		UserBeanVO ubean = new UserBeanVO();
//		ubean.setCorpname(bwreqvo.getCorpname());
//		String[] strs = corpservice.getPk_corpByName(ubean);
//		if (strs != null && strs.length > 0) {
//			throw new BusinessException("公司名称已存在,请联系客服处理!");
//		}
//		// iot 是否已经存在
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(bwreqvo.getIot());
//		NsCorpVO[] nscorpvos = (NsCorpVO[]) singleObjectBO.queryByCondition(NsCorpVO.class, "nvl(dr,0)=0 and iot = ? ",
//				sp);
//		if (nscorpvos != null && nscorpvos.length > 0) {
//			throw new BusinessException("IOT 已经存在!");
//		}
//	}
//
//	@Override
//	public BwBusiRespBean updatePwd(BwBusiReqBeanVo bwreqvo) throws DZFWarpException {
//
//		BwBusiRespBean bean = new BwBusiRespBean();
//
//		if (StringUtil.isEmpty(bwreqvo.getPassword())) {
//			throw new BusinessException("旧密码不能为空!");
//		}
//		if (StringUtil.isEmpty(bwreqvo.getNewpwd())) {
//			throw new BusinessException("新密码不能为空!");
//		}
//
//		if(StringUtil.isEmpty(bwreqvo.getPk_corp())){
//			throw new BusinessException("公司不能为空!");
//		}
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(bwreqvo.getPk_corp());
//		NsCorpVO[] nscorpvos  = (NsCorpVO[]) singleObjectBO.queryByCondition(NsCorpVO.class, "nvl(dr,0)=0 and pk_corp = ? ", sp);
//
//		if(nscorpvos == null || nscorpvos.length ==0){
//			throw new BusinessException("公司尚未注册!");
//		}
//		if(nscorpvos.length>1){
//			throw new BusinessException("公司查询出错!");
//		}
//
//		String de_old_pwd = apppubservice.decryptPwd(bwreqvo.getSystype(), bwreqvo.getPassword());//旧密码
//
//		String de_new_pwd = apppubservice.decryptPwd(bwreqvo.getSystype(), bwreqvo.getNewpwd());//新密码
//
//		if(new Encode().encode(de_old_pwd).equals(nscorpvos[0].getIotpassword())){
//			nscorpvos[0].setIotpassword(new Encode().encode(de_new_pwd));
//			singleObjectBO.update(nscorpvos[0], new String[]{"iotpassword"});
//		}else{
//			throw new BusinessException("修改密码失败,原始密码错误!");
//		}
//
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg("修改成功!");
//		return bean;
//	}
//
//	@Override
//	public BwBusiRespBean updateBwCorp(BwBusiReqBeanVo bwreqvo) throws DZFWarpException {
//
//		BwBusiRespBean bean = new BwBusiRespBean();
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(bwreqvo.getPk_corp());
//		NsCorpVO[] nscorpvos  = (NsCorpVO[]) singleObjectBO.queryByCondition(NsCorpVO.class, "nvl(dr,0)=0 and pk_corp = ? ", sp);
//
//		if(nscorpvos == null || nscorpvos.length ==0){
//			throw new BusinessException("公司尚未注册!");
//		}
//
//		if(nscorpvos.length>1){//查询过多
//			throw new BusinessException("公司查询出错!");
//		}
//
//		List<String> columnlist = new ArrayList<>();
//
//		if(!StringUtil.isEmpty(bwreqvo.getSh())){
//			nscorpvos[0].setTaxcode(bwreqvo.getSh());
//			columnlist.add("taxcode");
//		}
//
//		if(!StringUtil.isEmpty(bwreqvo.getCorpname())){
//			nscorpvos[0].setUnitname(bwreqvo.getCorpname());
//			columnlist.add("unitname");
//		}
//
//		if (!StringUtil.isEmpty(bwreqvo.getIot())) {
//			nscorpvos[0].setIot(bwreqvo.getIot());
//			columnlist.add("iot");
//			sp.clearParams();
//			sp.addParam(bwreqvo.getIot());
//			sp.addParam(bwreqvo.getPk_corp());
//			NsCorpVO[] nscorpvos_check = (NsCorpVO[]) singleObjectBO.queryByCondition(NsCorpVO.class,
//					"nvl(dr,0)=0 and iot = ? and pk_corp !=? ", sp );
//
//			if (nscorpvos_check != null && nscorpvos_check.length > 0) {
//				throw new BusinessException("IOT 公司已存在,不能更改!");
//			}
//		}
//
//		if(columnlist.size()==0){
//			throw new BusinessException("暂无更新数据!");
//		}
//
//		singleObjectBO.update(nscorpvos[0], columnlist.toArray(new String[0]));
//
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg("修改成功");
//		return bean;
//	}
//
}

package com.dzf.zxkj.app.service.user.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

import com.dzf.zxkj.app.model.app.CollabtevaltVO;
import com.dzf.zxkj.app.model.app.OpinionVO;
import com.dzf.zxkj.app.model.app.corp.CorpBean;
import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.corp.UserToCorp;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpPhoto;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.message.IMessageSendService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.service.user.IAppUserActService;
import com.dzf.zxkj.app.service.user.IAppUserService;
import com.dzf.zxkj.app.utils.*;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.*;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("userservice")
@Slf4j
public class UserServiceImpl implements IAppUserService {
	public static Map<String, String[]> wxmap = new HashMap<String, String[]>();
	
	private SingleObjectBO sbo;
	
	@Autowired
	private IAppCorpService corpservice ;
	
	@Autowired
	private IAppCorpPhoto corpPhotoservice;
	
	@Autowired
	private IAppPubservice apppubservice ;
	
	@Autowired
	private IMessageSendService sys_appmsgserv ;
	
	@Autowired
	private IAppLoginCorpService user320service;
	
	public UserServiceImpl() {
	}

	private void insertCollabtevalt(String pk_corp, Integer btype, SuperVO svo) throws DZFWarpException {
		CollabtevaltVO vo = (CollabtevaltVO) svo;
		if(btype == IConstant.ZORE){//业务合作，
			vo.setPk_collabtevalt(btype + vo.getPk_user().substring(1, 24));
			String[] pks = sbo.insertVOArr(pk_corp, new SuperVO[] { vo });
			CorpVO cpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
			UserVO uvo = (UserVO) sbo.queryByPrimaryKey(UserVO.class, vo.getPk_user());
			String content =cpvo.getUnitname() +"公司,"+ CodeUtils1.deCode(uvo.getUser_name())+"申请"+getMsgtypevalue(vo.getNewType())
			  +"请尽快联系，电话:"+uvo.getPhone()+"！";
			sys_appmsgserv.saveTypeMsg(vo.getPk_user(), cpvo.getFathercorp(),content, 
					pk_corp, null, pks[0], MsgtypeEnum.MSG_TYPE_SJYWHZYXTZ,"");
		} else if (btype == IConstant.ONE) {// 服务评价
			vo.setPk_collabtevalt(btype + vo.getPk_user().substring(1, 24));
			sbo.insertVOArr(pk_corp, new SuperVO[] { vo });
		} else {// 意见反馈
			IAppPubservice apppubservice = (IAppPubservice) SpringUtils.getBean("apppubservice");
			// vo变化，改用OpinionVO 
			OpinionVO  opinvo = new OpinionVO();
			opinvo.setPk_corp(vo.getPk_corp());
			opinvo.setPk_temp_corp(vo.getPk_tempcorp());
			opinvo.setSendman(vo.getPk_user());
			opinvo.setSendmanname(apppubservice.getUserName(vo.getPk_user()));
			opinvo.setVcontact(vo.getCertctnum());
			opinvo.setSys_send(ISysConstants.DZF_APP);
			opinvo.setVcontent(vo.getMessage());
			opinvo.setDsenddate(vo.getBusidate().toString());
			sbo.insertVOArr(pk_corp, new SuperVO[] { opinvo });
		}
	}
	
	private String getMsgtypevalue(String itemstr){
		if(!StringUtil.isEmpty(itemstr)){
			String[] items = itemstr.split(",");
			StringBuffer ress = new StringBuffer();
			for(int i = 0 ;i<items.length;i++){
				if(items[i].equals("0")){
					ress.append("新户设立,");
				}else if(items[i].equals("1")){
					ress.append("审计评估,");
				}else if(items[i].equals("2")){
					ress.append("税务变更,");
				}else if(items[i].equals("3")){
					ress.append("产权代理,");
				}else if(items[i].equals("4")){
					ress.append("工商变更,");
				}else if(items[i].equals("5")){
					ress.append("其他业务,");
				}
			}
			return ress.substring(0, ress.length()-1);
		}
		return null;
	}

	// 验证公司是否存在
	public RegisterRespBeanVO CheckExistCorp(UserBeanVO userBean) {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		SQLParameter sp = new SQLParameter();
		String qryCorp = "select pk_corp from bd_corp where unitname=? union all select pk_temp_corp from app_temp_corp where corpname=?";
		sp.clearParams();
		sp.addParam(CodeUtils1.deCode(userBean.getCorpname()));
		sp.addParam(userBean.getCorpname());
		ArrayList al = (ArrayList) sbo.executeQuery(qryCorp, sp, new ArrayListProcessor());
		if (al != null && al.size() > 0) {// 校验公司是否已存在
			String qryphone = "select phone2 from bd_corp where unitname=? union all select tel from app_temp_corp where corpname=?";
			List<String> phones =   (List<String>) sbo.executeQuery(qryphone, sp, new ColumnListProcessor());
			if(phones !=null && phones.size()>1 ){
				throw new BusinessException("您输入的公司名称对应多家公司!");
			}
			bean.setExitcorp(IConstant.DEFAULT);
			String phone = null;
			
			if(phones!=null && phones.size()>0){
				phone = phones.get(0);
			}
			if (phone == null || phone.length() <= 0) {
				throw new BusinessException("公司已存在，但未注册管理员手机号");
			}
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg(phone);
			return bean;
		} else {
			bean.setExitcorp(IConstant.FIRDES);
		}
		bean.setRescode(IConstant.FIRDES);
		bean.setResmsg("N");

		return bean;
	}

	
	/**
	 * 保存用户，同时记录关联关系
	 * 
	 * @param userBean
	 * @param ope
	 *            是否需要校验用户重复信息
	 * @return
	 * @throws DZFWarpException
	 */
	public List<UserVO> saveUser(UserBeanVO userBean, String pk_tempcorp, String pk_corp, DZFBoolean ismanage,
			Integer ope) throws DZFWarpException {
		List<UserVO> usercount;
		try {
			IAppUserActService ias = (IAppUserActService) SpringUtils.getBean("auaservice");
			String userObjKey = IDGenerate.getInstance().getNextID(Common.tempidcreate);
			SQLParameter sp = new SQLParameter();
			// 判断该公司对应的用户是否已经注册过
			String smsql = "select * from sm_user  where user_code= ? and nvl(dr,0)=0";
			sp.clearParams();
			sp.addParam(userBean.getPhone());
			usercount = (List<UserVO>) sbo.executeQuery(smsql, sp, new BeanListProcessor(UserVO.class));
			// 当前用户没注册过
			if (usercount.size() == 0) {
				if (StringUtil.isEmpty(userBean.getAccount())) {
					userBean.setAccount(userBean.getPhone());
				}
				AppUserVO userVO = createUserVO(userBean, true);
				userVO.setPrimaryKey(userObjKey);
				AppUserVO[] appuservos = (AppUserVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" },
						new AppUserVO[] { userVO }, 0);
				String cuserid = ias.saveUser(appuservos[0]);
				appuservos[0].setCuserid(cuserid);
				usercount.add(appuservos[0]);
				
				//更新临时用户的信息
				String uptempusersql = " update app_temp_user set pk_user = ? where user_code = ? and nvl(dr,0)=0 ";
				sp.clearParams();
				sp.addParam(cuserid);
				sp.addParam(userBean.getPhone());
				sbo.executeUpdate(uptempusersql, sp);
			}
		    List<UserToCorp> reslit = null;
			int rescount = 0;
			DZFBoolean ismangevalue = DZFBoolean.TRUE;
			if (!AppCheckValidUtils.isEmptyCorp(pk_corp)) {
				String corpsql = " select * from ynt_corp_user  where pk_user= ? and pk_corp=? and nvl(dr,0)=0";
				sp.clearParams();
				sp.addParam(usercount.get(0).getCuserid());
				sp.addParam(pk_corp);
				reslit=  (List<UserToCorp>) sbo.executeQuery(corpsql, sp, new BeanListProcessor(UserToCorp.class));
				rescount = reslit.size();//(BigDecimal) sbo.executeQuery(corpsql, sp, new ColumnProcessor());
				
				if(ismangevalue.booleanValue()){
					CorpVO cpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, pk_corp);// CorpCache.getInstance().get("", pk_corp);
					cpvo = CorpUtil.getCorpvo(cpvo);
					if(!userBean.getPhone().equals(cpvo.getPhone2())){//手机号不相等则不添加
						ismangevalue = DZFBoolean.FALSE;
					}
				}
			} else if (!StringUtil.isEmpty(pk_tempcorp)) {
				String corpsql = "select * from ynt_corp_user  where pk_user= ? and pk_tempcorp=? and nvl(dr,0)=0";
				sp.clearParams();
				sp.addParam(usercount.get(0).getCuserid());
				sp.addParam(pk_tempcorp);
				reslit=  (List<UserToCorp>) sbo.executeQuery(corpsql, sp, new BeanListProcessor(UserToCorp.class));
				rescount = reslit.size();//

				String corpsqlism = "select count(1) from ynt_corp_user  where pk_tempcorp=? and nvl(dr,0)=0";
				sp.clearParams();
				sp.addParam(pk_tempcorp);
				BigDecimal rescountism = (BigDecimal) sbo.executeQuery(corpsqlism, sp, new ColumnProcessor());
				if (rescountism != null && rescountism.intValue() > 0) {
					ismangevalue = DZFBoolean.FALSE;
				}
			}
			// 更新关联关系
			if (rescount == 0) {
				UserToCorp userToCorp = new UserToCorp();
				if(StringUtil.isEmpty(pk_tempcorp)){//生成一个新的app_temp_corpvo
					pk_tempcorp = corpservice.genTempCorpMsg252(userBean,pk_corp, sbo);
				}
				userToCorp.setPk_tempcorp(pk_tempcorp);//临时公司主键
				userToCorp.setPk_user(usercount.get(0).getCuserid());
				if (userBean.getBdata() != null) {
					userToCorp.setBdata(DZFBoolean.valueOf(userBean.getBdata()));
				}
				if (userBean.getBaccount() != null) {
					userToCorp.setBaccount(DZFBoolean.valueOf(userBean.getBaccount()));
				}
				if(userBean.getBbillapply() != null ){
					userToCorp.setBbillapply(DZFBoolean.valueOf(userBean.getBbillapply()));
				}
				userToCorp.setIstate(userBean.getIstates() == null?IConstant.TWO:Integer.parseInt(userBean.getIstates()));
				// 是否manage需要是否是第一次
				userToCorp.setIsmanage(ismangevalue);
				if (AppCheckValidUtils.isEmptyCorp(pk_corp)) {
					pk_corp = Common.tempidcreate;
				}else{
					CorpVO corpcachevo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, pk_corp);
					corpcachevo = CorpUtil.getCorpvo(corpcachevo);
					userToCorp.setFathercorp(corpcachevo.getFathercorp());
				}
				userToCorp.setPk_corp(pk_corp);// 签约公司主键
				userToCorp.setIaudituser(userBean.getIaudituser());
				sbo.insertVOArr(Common.tempidcreate, new UserToCorp[] { userToCorp });
			} else {
				//更改现有管理员sql
				if(!AppCheckValidUtils.isEmptyCorp(reslit.get(0).getPk_corp())
						&& ismangevalue!=null && ismangevalue.booleanValue() ){
					//更改老数据
					String upoldmansql = "update ynt_corp_user set dr =1 where ismanage = 'Y' and pk_corp = ?  and nvl(dr,0)=0";
					sp.clearParams();
					sp.addParam(reslit.get(0).getPk_corp());
					int res= sbo.executeUpdate(upoldmansql, sp);
					if(res >1 ){
						throw new BusinessException("业务出错，请联系管理员");
					}
					//赋值新数据
					reslit.get(0).setIsmanage(DZFBoolean.TRUE);
					reslit.get(0).setDr(0);
					sbo.update(reslit.get(0), new String[]{"ismanage"});
				}else if(0 == ope){
					throw new BusinessException("该用户已经在您公司存在!");
				}
			}
		} catch (Exception e) {
			if(e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			else
				throw new WiseRunException(e);
		}
		return usercount;
	}

	/**
	 * 注册用户
	 * 
	 * @return
	 * @throws Exception
	 */
	public RegisterRespBeanVO saveRegisterUser(UserBeanVO userBean) throws DZFWarpException {
		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
			return saveRegisterUser1(userBean);
		}else{
			throw new BusinessException("版本信息不对!");
		}
	}
	
	private RegisterRespBeanVO saveRegisterUser1(UserBeanVO userBean) {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		
		//校验
		validateSaveRegisterUser1(userBean);
		
		// 登录用的公司
		String loginaccount = userBean.getAccount();
		String corpid = userBean.getCorpid();
		userBean.setPk_signcorp(corpid);
		userBean.setCorpid(corpid);
		userBean.setFathercorpid(corpid);
		userBean.setAccount(userBean.getPhone());//注册帐号，就是传递过来的手机号，传递过来的account其实是登录的account
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getPhone());
		TempUserRegVO[] tempvos =  (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class, " nvl(dr,0)=0 and user_code =?", sp);
		
		TempUserRegVO tempuservo = null;
		if(tempvos==null || tempvos.length==0){
			tempuservo = new TempUserRegVO();
			tempuservo.setUser_code(userBean.getPhone());
			tempuservo.setUser_name(userBean.getUsername());
			tempuservo.setApp_user_qq(userBean.getPhone());
			tempuservo.setPhone(userBean.getPhone());
			tempuservo.setIstate(IConstant.TWO);
			tempuservo = (TempUserRegVO) sbo.saveObject(Common.tempidcreate, tempuservo);
		}else{
			tempuservo = tempvos[0];
		}
		
		List<UserVO> usercount = saveUser(userBean, userBean.getPk_tempcorp(), corpid, DZFBoolean.FALSE, 0);
		
		//发送短信
//		msservice.sendSaveRegisterUser1(userBean, loginaccount);
		
		bean.setCuserid(usercount.get(0).getCuserid());
		bean.setResmsg("操作成功!");
		bean.setRescode(IConstant.DEFAULT);
		return bean;
	}


	private void validateSaveRegisterUser1(UserBeanVO userBean) {
		if(StringUtil.isEmpty(userBean.getPhone())){
			throw new BusinessException("手机号不能为空!");
		}
		AppCheckValidUtils.validatePhone(userBean.getPhone());
		
		if(!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())){
			CorpVO[] cpvodemo = AppQueryUtil.getInstance().getDemoCorpMsg();
			if(cpvodemo[0].getPk_corp().equals(userBean.getPk_corp())){
				throw new BusinessException("请创建属于您自己的公司添加人员!");
			}
		}
		
		AppCheckValidUtils.isEmptyWithCorp(userBean.getPk_corp(), userBean.getPk_tempcorp(),null);//公司不能为空
	}

	public RegisterRespBeanVO saveregister(UserBeanVO userBean) throws DZFWarpException {
		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1 
				&& userBean.getVersionno().intValue()<IVersionConstant.VERSIONNO310) {// 第一个版本走的东西
			return saveRegisterSwitch1(userBean);// 保存
		}else if(userBean.getVersionno()>=IVersionConstant.VERSIONNO310){
			return saveRegisterAndConnectUser(userBean);//
		}else{
			throw new BusinessException("版本信息有误!");
		}
	}
	
	private RegisterRespBeanVO saveRegisterAndConnectUser(UserBeanVO userBean) {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		// 登录用的公司
		// 信息预制
		String corpidtemp = userBean.getPk_corp();
		
		UserToCorp ucorp = (UserToCorp) sbo.queryByPrimaryKey(UserToCorp.class, AppEncryPubUtil.decryParam(corpidtemp));
		
		if(ucorp == null){
			throw new BusinessException("该信息不存在");
		}
		
		//手机号是否合法
		AppCheckValidUtils.validatePhone(userBean.getPhone());
		
		String corpid = ucorp.getPk_corp();
		userBean.setPk_corp(corpid);
		userBean.setPk_tempcorp(ucorp.getPk_tempcorp());
		userBean.setPk_signcorp(corpid);
		userBean.setCorpid(corpid);
		userBean.setFathercorpid(corpid);
		userBean.setAccount(userBean.getPhone());
		userBean.setUsername(userBean.getUsername());
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getPhone());
		TempUserRegVO[] tempvos =  (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class, " nvl(dr,0)=0 and user_code =?", sp);
		if(tempvos==null || tempvos.length==0){
			TempUserRegVO tempuservo = new TempUserRegVO();
			tempuservo.setUser_code(userBean.getPhone());
			tempuservo.setUser_name(StringUtil.isEmpty(userBean.getUsername())?userBean.getPhone():userBean.getUsername());
			tempuservo.setApp_user_qq(userBean.getPhone());
			tempuservo.setPhone(userBean.getPhone());
			tempuservo.setIstate(IConstant.TWO);
			sbo.saveObject(Common.tempidcreate, tempuservo);
		}
		List<UserVO> usercount = saveUser(userBean, userBean.getPk_tempcorp(), corpid, DZFBoolean.FALSE, 0);
		bean.setCuserid(usercount.get(0).getCuserid());
		bean.setResmsg("操作成功!");
		bean.setRescode(IConstant.DEFAULT);
		return bean;
	}
	
	private RegisterRespBeanVO saveRegisterSwitch1(UserBeanVO userBean) {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		if (StringUtil.isEmptyWithTrim(userBean.getIdentify())) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("验证码不能为空");
			return bean;
		}
		if (userBean.getCorpname() == null) {
			userBean.setCorpname(userBean.getAccount());
		}
		String phone = CommonServ.getExistCorp(userBean.getCorpname());
		if (phone != null) {
			userBean.setPhone(phone);
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getAccount());

		String qryAccount = "select cuserid from sm_user where user_code=?";

		ArrayList accountLs = (ArrayList) sbo.executeQuery(qryAccount, sp, new ArrayListProcessor());

		if (accountLs != null && accountLs.size() > 0) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("此账号系统已经存在");
			return bean;
		}

		String qryCorp = "select pk_corp from bd_corp where unitname=? union all select pk_temp_corp from app_temp_corp where corpname=?";
		sp.clearParams();
		sp.addParam(userBean.getCorpname());
		sp.addParam(userBean.getCorpname());
		ArrayList al = (ArrayList) sbo.executeQuery(qryCorp, sp, new ArrayListProcessor());
		bean.setExitcorp(IConstant.DEFAULT);
		boolean newCorpAsMng = false;

		String userObjKey = IDGenerate.getInstance().getNextID(Common.tempidcreate);// userBean.getAccount()+userBean.getPhone()+mills.toString()+"000000000000000";
		IAppUserActService ias = (IAppUserActService) SpringUtils.getBean("auaservice");

		if (al == null || al.size() == 0) {
			TempCorpVO corpVO = new TempCorpVO();
			corpVO.setPk_temp_corp(userObjKey);
			corpVO.setCorpname(userBean.getCorpname());
			corpVO.setUsercode(userBean.getAccount());
			corpVO.setUsername(userBean.getUsername());
			corpVO.setContactman(userBean.getUsername());
			corpVO.setCustnature(2);//法人
			corpVO.setTel(userBean.getPhone());
			corpVO.setPk_corp(Common.tempidcreate);// 获取ID用，常量

			ias.saveTempCorp(corpVO);
			bean.setCorpid(userObjKey);
			bean.setExitcorp(IConstant.FIRDES);
			newCorpAsMng = true;

			userBean.setPk_tempcorp(userObjKey);// 临时公司的主键
			userBean.setCorpid(Common.tempidcreate);
			userBean.setFathercorpid(Common.tempidcreate);
			bean.setIsdemo("1");
		} else {
			Object[] dataObj = (Object[]) al.get(0);
			for (Object data : dataObj) {
				if (data.toString().length() == 6) {
					// 如果该公司已经创建过了，则isdemo为0
					if (!data.toString().equals(Common.tempidcreate)) {
						bean.setIsdemo("0");
					} else {
						bean.setIsdemo("1");
					}
					userBean.setPk_signcorp(data.toString());
					userBean.setCorpid(data.toString());
					userBean.setFathercorpid(data.toString());
				} else {
					bean.setIsdemo("1");
					userBean.setPk_tempcorp(data.toString());
				}
			}
		}
		if (userBean.getCorpid() == null) {
			userBean.setCorpid(Common.tempidcreate);
			userBean.setFathercorpid(Common.tempidcreate);
		}
		AppUserVO userVO =null;
		try {
			userVO = createUserVO(userBean, newCorpAsMng);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		userVO.setPrimaryKey(userObjKey);
		AppUserVO[] appuservos = (AppUserVO[]) QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" },
				new AppUserVO[] { userVO }, 0);
		ias.saveUser(appuservos[0]);

		// 再查询一次根据公司名字查询公司地址
		String tempqrycorp = "select * from app_temp_corp where corpname=? ";
		sp.clearParams();
		sp.addParam(userBean.getCorpname());
		List<TempCorpVO> templistvo = (ArrayList<TempCorpVO>) sbo.executeQuery(tempqrycorp, sp,
				new BeanListProcessor(TempCorpVO.class));

		if (templistvo != null && templistvo.size() > 0) {
			bean.setCorpaddr(templistvo.get(0).getCorpaddr());
			bean.setLatitude(templistvo.get(0).getLatitude());
			bean.setLongitude(templistvo.get(0).getLongitude());
		}
		if (userVO.getIsmanager() != null && userVO.getIsmanager().booleanValue()) {
			bean.setIsmanage("Y");
		} else {
			bean.setIsmanage("N");
		}

		bean.setCuserid(userObjKey);
		bean.setCusercode(userVO.getUser_code());
		if (bean.getRescode() == null) {
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg("注册成功!");
		}
		return bean;
	}

	private AppUserVO createUserVO(UserBeanVO bean, boolean newCorpAsMng) throws Exception {
		AppUserVO userVO = new AppUserVO();
		if (StringUtil.isEmpty(bean.getCorpid())) {
			userVO.setPk_corp(Common.tempidcreate);
		} else {
			userVO.setPk_corp(bean.getCorpid());
		}
		userVO.setPk_creatcorp(bean.getFathercorpid());
		userVO.setUser_code(bean.getAccount());
		if (StringUtil.isEmpty(bean.getUsername())) {
			userVO.setUser_name(bean.getAccount());
		} else {
			userVO.setUser_name(bean.getUsername());//后面有加密
		}
		userVO.setPwdlevelcode("junior");
		userVO.setAble_time(new DZFDate());
		userVO.setLangcode("simpchn");
		userVO.setAuthen_type("staticpwd");
		// 数据权限
		if (bean.getBdata() != null) {
			userVO.setBdata(DZFBoolean.valueOf(bean.getBdata()));
		}
		if (bean.getBaccount() != null) {
			userVO.setBaccount(DZFBoolean.valueOf(bean.getBaccount()));
		}

		if (newCorpAsMng) {
			userVO.setIsmanager(DZFBoolean.TRUE);
		} else {
			String qryUserByCorp = "select cuserid from sm_user where pk_corp=? and nvl(dr,0)=0";
			SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, bean.getCorpid()));
			SQLParameter sp = new SQLParameter();
			sp.addParam(bean.getCorpid());
			ArrayList exitUser = (ArrayList) sbo.executeQuery(qryUserByCorp, sp, new ArrayListProcessor());
			if (exitUser == null || !(exitUser.size() > 0)) {
				userVO.setIsmanager(DZFBoolean.TRUE);
			}
		}
		userVO.setBappuser(DZFBoolean.TRUE);
		userVO.setIstate(IConstant.TWO);
		userVO.setPwdparam(new DZFDate().toString());
		userVO.setCheckcode(String.valueOf(System.currentTimeMillis()));
		userVO.setIsca(DZFBoolean.FALSE);
		userVO.setLocked_tag(DZFBoolean.FALSE);
		userVO.setUser_note(bean.getPhone());
		userVO.setPk_tempcorp(bean.getPk_tempcorp());
		userVO.setPk_signcorp(bean.getPk_signcorp());
		userVO.setApp_user_tel(bean.getAccount());
		// 260版本以后
		String passwordsql = " select user_password  from app_temp_user  where  nvl(dr,0) = 0 and user_code = ? ";
		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, bean.getCorpid()));
		SQLParameter sp = new SQLParameter();
		sp.addParam(bean.getAccount());
		Object o = sbo.executeQuery(passwordsql, sp, new ColumnProcessor());
		if(o == null || StringUtil.isEmpty((String)o)){
			o = new Encode().encode(bean.getAccount());
		}
		userVO.setUser_password((String)o);
		return userVO;
	}

	// 完善用户信息
	public ResponseBaseBeanVO completeUserInfo(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		if (StringUtil.isEmptyWithTrim(userBean.getAccount()) || StringUtil.isEmptyWithTrim(userBean.getPassword())) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("用户编码为：" + userBean.getAccount().trim() + "，在系统中不存在");
			return bean;
		}
		try {
			String sql = "select user_password from sm_user where cuserid=?";
			SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getCorpid()));
			SQLParameter sp = new SQLParameter();
			sp.addParam(userBean.getAccount_id());
			List existUser = (ArrayList) sbo.executeQuery(sql, sp, new ArrayListProcessor());

			if (existUser == null || existUser.size() == 0) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("用户编码为：" + userBean.getAccount().trim() + "，在系统中不存在");
				return bean;
			}

			if (userBean.getUsername() == null || userBean.getUsername().length() <= 0 || userBean.getCorpname() == null
					|| userBean.getCorpname().length() <= 0 || userBean.getCorpaddr() == null
					|| userBean.getCorpaddr().length() <= 0) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("信息填写不完善");
				return bean;
			}

			AppUserVO uservo = new AppUserVO();
			uservo.setCuserid(userBean.getAccount_id());
			uservo.setJob(userBean.getJob());
			uservo.setApp_corpadd(userBean.getCorpaddr());
			uservo.setApp_corpname(userBean.getCorpname());
			uservo.setApp_user_mail(userBean.getApp_user_mail());
			uservo.setApp_user_memo(userBean.getApp_user_memo());
			uservo.setApp_user_qq(userBean.getApp_user_qq());
			uservo.setApp_user_tel(userBean.getApp_user_tel());
			sbo.update(uservo, new String[] { "job", "app_corpadd", "app_corpname", "app_user_mail", "app_user_memo",
					"app_user_qq", "app_user_tel" });
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg("个人信息维护成功");
		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("操作异常：" + e.getMessage());
			log.error("错误",e);
		}
		return bean;
	}

	public ResponseBaseBeanVO qryCorpList(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		try {
			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
				bean = qryCorpList1(userBean);
			}
		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			if(e instanceof BusinessException){
				bean.setResmsg(e.getMessage());
			}else{
				bean.setResmsg("查询公司信息出错！");
			}
			log.error("错误",e);
		}
		return bean;

	}

	private ResponseBaseBeanVO qryCorpList1(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		if (StringUtil.isEmptyWithTrim(userBean.getAccount_id())) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("账户不能为空");
		}
		String whereStr = "fathercorp='10}n'";
		SQLParameter sp = new SQLParameter();
		List<CorpVO> corps = (List<CorpVO>) sbo.retrieveByClause(CorpVO.class, whereStr, sp);
		bean.setRescode(IConstant.DEFAULT);

		if (corps != null && corps.size() > 0) {
			List<CorpBean> corpBeanLs = new ArrayList<CorpBean>(corps.size());
			CorpBean corpbean = null;
			for (CorpVO corp : corps) {
				corpbean = new CorpBean();
				corpbean.setCorpid(corp.getPrimaryKey());
				corpbean.setCorpnm(CodeUtils1.deCode(corp.getUnitname()));
				corpBeanLs.add(corpbean);
			}
			bean.setResmsg(corpBeanLs.toArray(new CorpBean[corpBeanLs.size()]));
		} else {
			bean.setResmsg("查询公司为空");
		}
		return bean;
	}

	public ResponseBaseBeanVO qryCorpService(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		try {
			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
				bean =  qryCorpService1(userBean);
			}
		} catch (Exception e) {
			if( e instanceof BusinessException){
				bean.setResmsg(e.getMessage());
			}else {
				bean.setResmsg("查询公司信息出错!");
			}
			bean.setRescode(IConstant.FIRDES);
			
		}
		return bean;
	}

	private ResponseBaseBeanVO qryCorpService1(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getPk_corp());
		List<CorpVO> corps = (List<CorpVO>) sbo.retrieveByClause(CorpVO.class, "pk_corp=?", sp);// "pk_corp='"+userBean.getPk_corp()+"'");
		bean.setRescode(IConstant.DEFAULT);
		if (corps != null && corps.size() > 0) {
			CorpBean corpbean = new CorpBean();
			corpbean.setPk_org(userBean.getPk_corp());
			corpbean.setOrgname(corps.get(0).getDef1());
			corpbean.setTel(corps.get(0).getDef2());// 客服电话
			corpbean.setCustomservice(corps.get(0).getForeignname());
			corpbean.setPhone(corps.get(0).getDef3());// 客户经理电话
			corpbean.setCustomintrodu(corps.get(0).getBriefintro());
			bean.setResmsg(corpbean);
		} else {
			bean.setResmsg("没有查到服务机构");
		}
		return bean;
	}

	/**
	 * 意见反馈
	 */
	public ResponseBaseBeanVO saveprocesscollabtevalt(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
			bean = saveprocesscollabtevalt1(userBean);
		}
		return bean;
	}

	private ResponseBaseBeanVO saveprocesscollabtevalt1(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		CollabtevaltVO vo = new CollabtevaltVO();
		vo.setBusitype(userBean.getBusitype());
		vo.setDes(userBean.getDes());
		vo.setCertctnum(userBean.getCertctnum());
		if (userBean.getItems() != null)
			vo.setItem(userBean.getItems().toString());
		vo.setMessage(userBean.getMessage());
		vo.setPk_tempcorp(userBean.getPk_tempcorp());
		vo.setPk_user(userBean.getAccount_id());
		vo.setPk_corp(userBean.getPk_corp());
		vo.setBusidate(new DZFDate());

		// 业务合作
		vo.setNewType(userBean.getCertbusitype());
		if (userBean.getBusitype() != null && userBean.getBusitype().intValue() == 0) {
			vo.setMessage(userBean.getCertmsg());
			vo.setPk_image_group(userBean.getGroupkey());
		}else if(userBean.getBusitype() != null && userBean.getBusitype().intValue() == 1){//服务评价
			vo.setSatisfaction(userBean.getSatisfaction());//总体评价
			vo.setAppraisalnames(userBean.getAppraisalnames());//印象名称
			vo.setSeverbearing(userBean.getSeverbearing());//服务态度
			vo.setSpecialty(userBean.getSpecialty());//专业水平
			vo.setBetimes(userBean.getBetimes());//及时性
			if(!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())){
				CorpVO cpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, userBean.getPk_corp());// CorpCache.getInstance().get("", userBean.getPk_corp());
				cpvo = CorpUtil.getCorpvo(cpvo);
				if(cpvo.getBegindate() !=null){
					if(DateUtils.getPeriodEndDate(userBean.getBusidate()).before(cpvo.getBegindate())){
						throw new BusinessException("评价日期早于正式签约日期("+DateUtils.getPeriod(cpvo.getBegindate())+")！");
					}
					if(DateUtils.getPeriodStartDate(userBean.getBusidate()).after(new DZFDate())){
						throw new BusinessException("评价日期晚于当前日期！");
					}
				}else{
					throw new BusinessException("评价失败，您公司尚未正式签约!");
				}
			}
			vo.setBusidate(DateUtils.getPeriodStartDate(userBean.getBusidate()));//操作日期
			vo.setServicexm("代理记账");
		}
		/**
		 * 会计公司主键
		 */
		CorpVO cvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, userBean.getPk_corp());// CorpCache.getInstance().get(null, userBean.getPk_corp());
		cvo = CorpUtil.getCorpvo(cvo);
		if (cvo != null) {
			Object pk_parent = cvo.getFathercorp();
			if (pk_parent != null)
				vo.setPk_parent(pk_parent.toString());
		}
		insertCollabtevalt(userBean.getPk_corp(), userBean.getBusitype(), vo);

		bean.setRescode(IConstant.DEFAULT);
		bean.setResmsg("提交成功");
		return bean;
	}

	public ResponseBaseBeanVO qrySelfAndChdCorps(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		try {
			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
				bean =  qrySelfAndChdCorps1(userBean);
			}
		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			if(e instanceof BusinessException){
				bean.setResmsg(e.getMessage());
			}else{
				bean.setResmsg("查询公司信息出错！");
			}
			log.error("错误",e);
		}
		return bean;
	}

	public CorpVO[] getValidateCorpByUserId(String dsName,
											String userID) throws DZFWarpException {
		Vector v = new Vector();
		try {
			String whereSql = " (isseal is null or isseal<>'Y') and ishasaccount='Y' and nvl(dr,0) =0 ";
			whereSql += " and pk_corp in (select b.pk_corp from sm_user a  , sm_user_role b  where a.cuserid = b.cuserid and a.cUserId = ? ) order by unitcode";
			SQLParameter sp=new SQLParameter();
			sp.addParam(userID);
			CorpVO[] corps=(CorpVO[]) sbo.queryByCondition(CorpVO.class, whereSql, sp);
//          nc.vo.bd.CorpVO[] corps = new nc.bs.bd.CorpDMO().queryByWhereSQL(whereSql);
			int n = corps == null ? 0 : corps.length;
			for (int i = 0; i < n; i++) {
				v.add(corps[i]);
			}
		} catch (DAOException e) {
			throw e;
		}
		CorpVO[] retrs = new CorpVO[v.size()];
		if (v.size() > 0) {
			v.copyInto(retrs);
		}
		return retrs;
	}

	private ResponseBaseBeanVO qrySelfAndChdCorps1(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		CorpVO[] corps = getValidateCorpByUserId("", userBean.getAccount_id());
		bean.setRescode(IConstant.DEFAULT);
		// 至少有一公司
		if (corps != null && corps.length > 0) {
			List<CorpBean> corpBeanLs = new ArrayList<CorpBean>(corps.length);
			for (CorpVO corp : corps) {
				CorpBean corpbean = new CorpBean();
				corpbean.setCorpid(corp.getPrimaryKey());
				corpbean.setCorpnm(CodeUtils1.deCode(corp.getUnitname()));
				corpBeanLs.add(corpbean);
			}
			bean.setResmsg(corpBeanLs.toArray(new CorpBean[corpBeanLs.size()]));
		}
		return bean;
	}

	/**
	 * 
	 * @param power 0 是否有报表的权限，1是否有上传票据， 2 全部的权限 null的话不判断
	 * @param account
	 * @param pk_corp
	 * @param pk_temp_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean getPrivilege(String power,String account,String pk_account_id,String pk_corp,String pk_temp_corp) throws DZFWarpException {
		
		if(power == null){
			return false;
		}
		//判断用户是否已经停用
		//根据用户account获取用户主键
		SQLParameter sp = new SQLParameter();
		String pk_user ; 
		if(!StringUtil.isEmpty(pk_account_id)){
			pk_user = pk_account_id;
		}else{
			sp.addParam(account);
			String usersql = " select cuserid from sm_user where user_code =? and nvl(dr,0)=0 ";
			List<String> cusridlist =  (List<String>) sbo.executeQuery(usersql, sp, new ColumnListProcessor());
			if(cusridlist!=null && cusridlist.size()>0){
				pk_user = cusridlist.get(0);
			}else{
				String usertempsql = " select pk_temp_user from app_temp_user where user_code =? and nvl(dr,0)=0  ";
				List<String> templist = (List<String>) sbo.executeQuery(usertempsql, sp, new ColumnListProcessor());
				if(templist!=null && templist.size()>0){
					pk_user = templist.get(0);
				}else{
					pk_user = "";
				}
			}
		}
		
		sp.clearParams();
		StringBuffer sql = new StringBuffer();
		sql.append(" select istate,nvl(baccount,'N') as baccount,nvl(bdata,'N') as bdata, ");
		sql.append("  nvl(bbillapply ,'N') as bbillapply , ismanage  ");
		sql.append(" from ynt_corp_user where nvl(dr,0)=0 ");
		sql.append( " and pk_user = ?" );
		sp.addParam(pk_user);
		if(!StringUtil.isEmpty(pk_corp) && !Common.tempidcreate.equals(pk_corp)){
			sql.append( " and pk_corp =?" );
			sp.addParam(pk_corp);
		}else{
			if(!StringUtil.isEmpty(pk_temp_corp)){
				sql.append( " and pk_tempcorp =?" );
				sp.addParam( pk_temp_corp);
			}
		}
		List<UserToCorp> obj = (List<UserToCorp>) sbo.executeQuery(sql.toString(), sp, new BeanListProcessor(UserToCorp.class));
		// 如果是空则也提示这个
		if (obj == null || obj.size() == 0) {
			return true;
		}
		
		if(!StringUtil.isEmpty(power)){
			if(obj.get(0).getIsmanage()!=null && obj.get(0).getIsmanage().booleanValue()){
				return false;
			}
			if(power.equals(IConstant.REPORT)){//报表权限
				return !(obj.get(0).getBaccount().booleanValue());
			}else if(power.equals(IConstant.IMAGE)){//上传图片权限
				return !(obj.get(0).getBdata().booleanValue());
			}else if(power.equals(IConstant.BILLAPPLY)){
				return !(obj.get(0).getBbillapply().booleanValue());
			}else if(power.equals(IConstant.ALLPOW)){
				if( obj.get(0).getBdata().booleanValue() && obj.get(0).getBaccount().booleanValue()){
					return false;
				}else{
					return true;
				}
			}
		}
		return Integer.parseInt(obj.get(0).getIstate().toString()) == IConstant.THREE;
	}

	@Override
	public ResponseBaseBeanVO updatecompletInfo(UserBeanVO userBean) {

		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
			return updatecompletInfo1(userBean);
		}
		return bean;

	}

	private ResponseBaseBeanVO updatecompletInfo1(UserBeanVO userBean) {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		try {
			if (StringUtil.isEmpty(userBean.getAccount())) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("更改失败:用户帐号信息为空!");
				return bean;
			}
			SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getCorpid()));
			StringBuffer usersql = new StringBuffer();
			usersql.append(" select user_code from sm_user ");
			usersql.append(" where nvl(dr,0)=0 and user_code=? ");
			usersql.append(" union all  ");
			usersql.append(" select user_code from app_temp_user ");
			usersql.append("  where user_code =? and nvl(dr,0)=0  ");
			SQLParameter sp = new SQLParameter();
			sp.addParam(userBean.getAccount());
			sp.addParam(userBean.getAccount());

			List<String> codelist = (List<String>) sbo.executeQuery(usersql.toString(), sp, new ColumnListProcessor());

			if (codelist == null || codelist.size() == 0) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("更改失败:用户帐号信息为空!");
				return bean;
			}

			if (userBean.getPhoto() != null && userBean.getPhoto().length() > 0) {//
				// 文件保存
				String photostr = userBean.getPhoto().replaceAll("<", "");
				photostr = photostr.replaceAll(">", "");
				InputStream inputStream = new ByteArrayInputStream(Hex.decode(photostr));
				// 文件名
				String imagename = ImageCommonPath.getUserHeadPhotoPath(userBean.getAccount(), userBean.getPhototype());
				corpPhotoservice.saveCorpFilemsg(inputStream, imagename);
				bean.setPhotopath(CryptUtil.getInstance().encryptAES(imagename));
			}
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg("个人信息维护成功");

		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("操作异常：" + e.getMessage());
			log.error("错误",e);
		}
		return bean;
	}

	public static String byte2hex(byte[] b) // 二进制转字符串
	{
		StringBuffer sb = new StringBuffer();
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1) {
				sb.append("0" + stmp);
			} else {
				sb.append(stmp);
			}

		}
		return sb.toString();
	}

	public static byte[] hex2byte(String str) { // 字符串转二进制
		if (str == null)
			return null;
		str = str.trim();
		int len = str.length();
		if (len == 0  || len % 2 != 0)
			return null;
		byte[] b = new byte[len / 2];
		try {
			for (int i = 0; i < str.length(); i += 2) {
				b[i / 2] = (byte) Integer.decode("0X" + str.substring(i, i + 2)).intValue();
			}
			return b;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 分配聊天对象
	 */
	@Override
	public ResponseBaseBeanVO fpHxkfAccount(UserBeanVO userBean) throws DZFWarpException {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
			return fpHxkfAccount1(userBean);
		}
		return bean;
	}

	private ResponseBaseBeanVO fpHxkfAccount1(UserBeanVO userBean) {
		String qrysql = new String(" select acc.hxaccountid,acc.hxaccountname, nvl(scount.ncount, 0) ncount"
				+ "  from ynt_hximaccount acc" + " inner join sm_user_role role on acc.userid = role.cuserid"
				+ "  left join (select count(1) ncount, sacc.srvcustid" + "  from ynt_hximaccount sacc"
				+ "  where sacc.userid = ?" + "   group by sacc.srvcustid) scount on acc.hxaccountid ="
				+ "  scount.srvcustid" + " where role.pk_corp = ?" + " and role.pk_role = '"
				+ IGlobalConstants.zxkfroleid + "' " + " and nvl(role.dr,0)=0 and nvl(acc.dr,0)=0 "
				+ " order by ncount ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getAccount_id());
		sp.addParam("002MP9");
		List<Object[]> rslsit = (ArrayList<Object[]>) getSbo().executeQuery(qrysql, sp, new ArrayListProcessor());
		if (rslsit == null || rslsit.size() == 0) {
			ResponseBaseBeanVO rs = new ResponseBaseBeanVO();
			rs.setRescode("1");
			rs.setResmsg("会计公司尚未设置客服，请电话咨询沟通！");
			return rs;
		}
		String upsql = new String("UPDATE YNT_HXIMACCOUNT SET SRVCUSTID =?  WHERE USERID =?");
		SQLParameter usp = new SQLParameter();
		Object[] obj = rslsit.get(0);
		usp.addParam((String) obj[0]);
		usp.addParam(userBean.getAccount_id());

		getSbo().executeUpdate(upsql, usp);// 更新客服对象

		ResponseBaseBeanVO rsvo = new ResponseBaseBeanVO();
		rsvo.setRescode("0");
		rsvo.setResmsg(new String[] { (String) obj[0], (String) obj[1] });// 环信ID,名称

		return rsvo;
	}

	@Override
	public ResponseBaseBeanVO savePassword(UserBeanVO userBean) throws DZFWarpException {
		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
		if (StringUtil.isEmptyWithTrim(userBean.getAccount()) || StringUtil.isEmptyWithTrim(userBean.getPassword())) {
			throw new BusinessException("用户编码为：" + userBean.getAccount().trim() + "，在系统中不存在");
		}

		if (StringUtil.isEmptyWithTrim(userBean.getPassword())) {
			throw new BusinessException("密码不能为空");
		}
		
		if(!StringUtil.isEmpty(userBean.getIdentify())){
			userBean.setPhone(userBean.getAccount());
			String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
			if(tips.length()>0){
				throw new BusinessException(tips);
			}
		}

		// 判断当前的用户编码是否有密码
		String checksql = " select user_password,user_code from app_temp_user where user_code=? and  nvl(dr,0)= 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getAccount());
		Object o = sbo.executeQuery(checksql, sp, new ArrayProcessor());
		if (o == null) {
			throw new BusinessException("当前用户不存在，请重新注册!");
		} else {
			Object[] os = (Object[]) o;
			if (os[0] != null) {
				throw new BusinessException("当前用户已经存在密码，不能设置!");
			}
		}
		try {
			sp.clearParams();
			String pwd  = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());
			sp.addParam(pwd);
			sp.addParam(userBean.getAccount());
			String sqltemp = "update app_temp_user set user_password=? where user_code=?";
			String sql = "update sm_user set user_password=? where user_code=?";
			sbo.executeUpdate(sqltemp, sp);
			sbo.executeUpdate(sql, sp);
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg("设置密码成功!");
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		return bean;
	}

	
	
	/**
	 * 更改帐号信息
	 */
	@Override
	public RegisterRespBeanVO updateUserMsg(UserBeanVO userBean) throws DZFWarpException {
		if(userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1 ){//第一版走的代码
			return updateUserMsg1(userBean);
		}else{
			throw new BusinessException("版本信息有误!");
		}
	}

	private RegisterRespBeanVO updateUserMsg1(UserBeanVO userBean) {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		// 手机号已经存在
		String phone = userBean.getPhone();
		if (StringUtil.isEmpty(phone) || StringUtil.isEmpty(userBean.getUsername()) || StringUtil.isEmpty(userBean.getAccount())) {
			throw new BusinessException("更改失败：当前帐号信息为空!");
		} else {
			// 手机号已经注册过了
			StringBuffer checksql = new StringBuffer();
			checksql.append(" select app_temp_user.user_code  ");
			checksql.append(" from app_temp_user ");
			checksql.append(" where app_temp_user.user_code=?  and nvl(app_temp_user.dr,0)=0  ");
			checksql.append(" union all  ");
			checksql.append(" select sm_user.user_code  ");
			checksql.append(" from sm_user ");
			checksql.append(" where sm_user.user_code =? and nvl(sm_user.dr,0)=0  ");
			SQLParameter sp = new SQLParameter();
			sp.addParam(userBean.getPhone());// 当前帐号
			sp.addParam(userBean.getPhone());

			List<String> listres = (List<String>) sbo.executeQuery(checksql.toString(), sp, new ColumnListProcessor());

			if (!phone.equals(userBean.getAccount()) && listres != null && listres.size() > 0) {
				throw new BusinessException("当前更改手机号已经注册过，请重新登录更改!");
			}
			// 如果确定了 ，那就可以更改帐号信息
			String updatesql1 = "update sm_user set user_code = ? ,user_name =?,app_user_qq=? ,phone=?   where user_code=?";
			sp.clearParams();
			sp.addParam(userBean.getPhone());
			sp.addParam(CodeUtils1.enCode(userBean.getUsername()));
			sp.addParam(userBean.getPhone());
			sp.addParam(userBean.getPhone());
			sp.addParam(userBean.getAccount());// 登录的account
			sbo.executeUpdate(updatesql1, sp);
			String updatesql2 = "update app_temp_user set  user_code = ? ,user_name =?,app_user_qq=? ,phone=?   where user_code=?";
			sp.clearParams();
			sp.addParam(userBean.getPhone());
			sp.addParam(userBean.getUsername());
			sp.addParam(userBean.getPhone());
			sp.addParam(userBean.getPhone());
			sp.addParam(userBean.getAccount());
			sbo.executeUpdate(updatesql2, sp);
			
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg("更改成功!");
			return bean;
		}
	}

	@Override
	public void updateBackPassword(UserBeanVO userBean) throws DZFWarpException {
		if (StringUtil.isEmptyWithTrim(userBean.getPassword())) {
			throw new BusinessException("密码不能为空!");
		}
		if (StringUtil.isEmptyWithTrim(userBean.getAccount())) {
			throw new BusinessException("帐号信息不能为空!");
		}
		
		userBean.setPhone(userBean.getAccount());
		String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
		if(tips.length()>0){
			throw new BusinessException(tips);
		}
		
		// 查询临时用户
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getAccount());
		TempUserRegVO[] tempuservos = (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class," nvl(dr,0)=0 and user_code = ?", sp);
		// 查询当前登录用户
		UserVO[] uservos = (UserVO[]) sbo.queryByCondition(UserVO.class, " nvl(dr,0)=0 and user_code = ?",sp);
		if ((tempuservos == null || tempuservos.length == 0) && (uservos == null || uservos.length == 0)) {
			throw new BusinessException("您帐号信息不存在!");
		}
		String password = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());
		
		if (tempuservos != null && tempuservos.length > 0) {
			for(TempUserRegVO vo:tempuservos){
				vo.setUser_password(password);
			}
			int res = sbo.updateAry(tempuservos, new String[] { "user_password" });
			log.info("当前用户"+userBean.getAccount()+"更改密码的用户数:"+res);
		}

		if (uservos != null && uservos.length > 0) {
			for(UserVO vo:uservos){
				vo.setUser_password(password);
			}
			int res = sbo.updateAry(uservos, new String[] { "user_password" });
			log.info("当前用户"+userBean.getAccount()+"更改密码的用户数:"+res);
		}

	}
	
	@Override
	public LoginResponseBeanVO qryLgBeanvo(UserBeanVO userBean) throws DZFWarpException {
		LoginResponseBeanVO lgbean = new LoginResponseBeanVO();
		String idtemp = userBean.getCorpid();
		if(StringUtil.isEmpty(idtemp)){
			lgbean.setRescode(IConstant.FIRDES);
			lgbean.setResmsg("信息不能为空!");
			return lgbean;
		}
		String id = AppEncryPubUtil.decryParam(idtemp);
		UserToCorp utocorp =  (UserToCorp) sbo.queryByPrimaryKey(UserToCorp.class, id);
		if(utocorp == null ){
			lgbean.setRescode(IConstant.FIRDES);
			lgbean.setResmsg("信息不能为空!");
			return lgbean;
		}
		//获取用户信息
		String userid = utocorp.getPk_user();
		UserVO uvo = (UserVO) sbo.queryByPrimaryKey(UserVO.class, userid);
		if(uvo == null ){
			lgbean.setRescode(IConstant.FIRDES);
			lgbean.setResmsg("人员信息不能为空!");
			return lgbean;
		}
		
		if(uvo != null && !StringUtil.isEmpty(uvo.getUser_name())){//邀请人信息
			lgbean.setUser_name(CodeUtils1.deCode(uvo.getUser_name()));
		}else {
			lgbean.setUser_name(uvo.getUser_code());
		}
		//获取公司信息
		String fathercorp = "";
		if(!AppCheckValidUtils.isEmptyCorp(utocorp.getPk_corp())){
			CorpVO cpvo = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, utocorp.getPk_corp());
			lgbean.setCorpname(CodeUtils1.deCode(cpvo.getUnitname()));
			fathercorp = cpvo.getFathercorp();
		}else if(!StringUtil.isEmpty(utocorp.getPk_tempcorp())){
			TempCorpVO tempvo = (TempCorpVO) sbo.queryByPrimaryKey(TempCorpVO.class, utocorp.getPk_tempcorp());
			lgbean.setCorpname(tempvo.getCorpname());
			fathercorp = tempvo.getPk_svorg();
		}
		//代账公司logo
		if(!StringUtil.isEmpty(fathercorp)){
			CorpVO fcorp = (CorpVO) sbo.queryByPrimaryKey(CorpVO.class, fathercorp);// CorpCache.getInstance().get("", fathercorp);
			if(fcorp!=null){
				lgbean.setFname(StringUtil.isEmpty(fcorp.getUnitshortname())?CodeUtils1.deCode(fcorp.getUnitname())  : CodeUtils1.deCode(fcorp.getUnitshortname()));
				lgbean.setFlogo(CryptUtil.getInstance().encryptAES(fcorp.getUrl()) );//logo地址
				lgbean.setQysbh(fcorp.getDef12());//企业识别号
			}
		}
		
		lgbean.setPk_corp(utocorp.getPk_corp());
		lgbean.setPk_temp_corp(utocorp.getPk_tempcorp());
		lgbean.setPhotopath(UserUtil.getHeadPhotoPath(uvo.getUser_code(), 0));
		lgbean.setRescode(IConstant.DEFAULT);
		
		return lgbean;
	}
	
	@Override
	public RegisterRespBeanVO updateModifyPasswordSWtch320(UserBeanVO userBean) throws DZFWarpException {
		RegisterRespBeanVO bean = new RegisterRespBeanVO();
		if (StringUtil.isEmptyWithTrim(userBean.getNewpwd()) || StringUtil.isEmptyWithTrim(userBean.getPassword())) {
			throw new BusinessException("密码不能为空!");
		}
		if (StringUtil.isEmptyWithTrim(userBean.getAccount())) {
			throw new BusinessException("帐号信息不能为空!");
		}
		// 查询临时用户
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getAccount());
		TempUserRegVO[] tempuservos = (TempUserRegVO[]) sbo.queryByCondition(TempUserRegVO.class,
				" nvl(dr,0)=0 and user_code = ?", sp);
		// 查询当前登录用户
		UserVO[] uservos = (UserVO[]) sbo.queryByCondition(UserVO.class, " nvl(dr,0)=0 and user_code = ?",
				sp);
		if ((tempuservos == null || tempuservos.length == 0) && (uservos == null || uservos.length == 0)) {
			throw new BusinessException("您帐号信息不存在!");
		}
		
		String oldpassword = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());
		
		String newpassword = apppubservice.decryptPwd(userBean.getSystype(), userBean.getNewpwd());
		
		if ( (tempuservos != null && tempuservos.length > 0) ||  (uservos != null && uservos.length > 0)) {
			if(!StringUtil.isEmpty(tempuservos[0].getUser_password()) 
					&& !oldpassword.equals(tempuservos[0].getUser_password())){
				throw new BusinessException("原密码不正确!");
			}
			if(!StringUtil.isEmpty(uservos[0].getUser_password()) 
					&& !oldpassword.equals(uservos[0].getUser_password())){
				throw new BusinessException("原密码不正确!");
			}
		}
		
		
		if (tempuservos != null && tempuservos.length > 0) {
			tempuservos[0].setUser_password(newpassword);
			if(SourceSysEnum.SOURCE_SYS_PST_APP.getValue().equals(userBean.getSourcesys()) ){
				tempuservos[0].setBbwdefaultpwd(DZFBoolean.FALSE);
			}
			sbo.update(tempuservos[0], new String[] { "user_password", "bbwdefaultpwd"});
		}
		if (uservos != null && uservos.length > 0) {
			uservos[0].setUser_password(newpassword);
			sbo.update(uservos[0], new String[] { "user_password" });
		}

		bean.setRescode(IConstant.DEFAULT);
		bean.setResmsg("修改密码成功!");
		return bean;
	}
	
	public SingleObjectBO getSbo() {
		return sbo;
	}

	@Autowired
	public void setSbo(SingleObjectBO sbo) {
		this.sbo = sbo;
	}

	@Override
	public LoginResponseBeanVO qryLgBeanvoFromWxApplet(UserBeanVO userBean) throws DZFWarpException {
		LoginResponseBeanVO lgbean = new LoginResponseBeanVO();
		return lgbean;
	}
	
}

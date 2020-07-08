package com.dzf.zxkj.app.service.login.impl;

import com.dzf.zxkj.app.model.app.LoginQRVO;
import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.SubCopMsgBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.ISysSourceConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.pub.constant.LoginQRConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.login.IAppDemoCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginOrRegService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.utils.*;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 用户260版本接口实现类
 * 
 * @author zhangj
 *
 */
@Service("user300service")
@Slf4j
public class AppLoginOrRegImpl implements IAppLoginOrRegService {
	
	public static BaseTimerCache<String, Object> log_identify = new BaseTimerCache<String, Object>();
	
	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAppPubservice apppubservice;
	
	@Autowired
	private IAppDemoCorpService democorpser;

	@Autowired
	private IAppCorpService corpservice;
	
	@Autowired
	private IAppLoginCorpService user320service;
	

	@Override
	public LoginResponseBeanVO saveRegisterCorpSWtch260(UserBeanVO userBean) throws DZFWarpException {

		LoginResponseBeanVO bean = new LoginResponseBeanVO();

		if (StringUtil.isEmptyWithTrim(userBean.getIdentify())) {
			throw new BusinessException("验证码不能为空");
		}

		if (StringUtil.isEmptyWithTrim(userBean.getPassword())) {
			throw new BusinessException("密码不能为空!");
		}

		String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
		
		if(tips.length()>0){
			throw new BusinessException(tips);
		}
		
		boolean isexist = apppubservice.isExistUser(userBean.getPhone());
		
		if (isexist) {
			throw new BusinessException("当前用户已经存在，不能重复注册!");
		}

		//校验手机号
		AppCheckValidUtils.validatePhone(userBean.getPhone());
		
		// 开始用户注册
		String password = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());
		TempUserRegVO tempuservo = new TempUserRegVO();
		tempuservo.setUser_code(userBean.getPhone());
		tempuservo.setUser_password(password);
		tempuservo.setUser_name(userBean.getPhone());
		tempuservo.setApp_user_qq(userBean.getPhone());
		tempuservo.setPhone(userBean.getPhone());
		tempuservo.setIstate(IConstant.TWO);
		SuperVO vo = singleObjectBO.saveObject(Common.tempidcreate, tempuservo);
		// 添加公司信息
		CorpVO[] cpvos = apppubservice.getNoLinkCorp(userBean.getPhone(),userBean.getSourcesys());
		if (cpvos != null && cpvos.length > 0) {
			bean.setCpvos(cpvos);
		}

		// 查询demo公司
		CorpVO[] corpvo = AppQueryUtil.getInstance().getDemoCorpMsg();
		String usercode = userBean.getPhone();
		String username = userBean.getPhone();
		String accountid = vo.getPrimaryKey();
		democorpser.sendDemoCorp(bean, usercode, username, accountid, userBean.getVdevicdmsg(), corpvo,userBean.getSourcesys(),"");
		bean.setRescode(IConstant.DEFAULT);
		bean.setResmsg("注册成功!");
		return bean;
	}


	@Override
	public LoginResponseBeanVO logingGetCorpVOs260(UserBeanVO userBean) throws DZFWarpException {

//		if (StringUtil.isEmptyWithTrim(userBean.getUsercode())) {
//			LoginResponseBeanVO bean = new LoginResponseBeanVO();
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("账号不能为空!");
//			return bean;
//		}
		
//		boolean flag = false;
//		//密码为空则为验证码登录
//		if (StringUtil.isEmptyWithTrim(userBean.getPassword())) {
//			flag = false;
//		}else{
//			flag = true;
//		}
		LoginResponseBeanVO bean = localLogingGetCorpVOs260(userBean);
		return bean;
	}

	public LoginResponseBeanVO localLogingGetCorpVOs260(UserBeanVO userBean) throws DZFWarpException {
		LoginResponseBeanVO bean = new LoginResponseBeanVO();
		try {
//			List<TempUserRegVO> templistapp = getTempList(userBean);
//			ArrayList<AppUserVO> alres = getUserList(userBean);
//			if ((templistapp == null || templistapp.size() == 0)  && (alres == null || alres.size() == 0)) {
//				throw new BusinessException("您用户不存在，请注册登录!");
//			} else {
//				checkSource(userBean,templistapp);
//				checkPassword(userBean, ispwd, bean,alres,templistapp);
//				if(bean.getRescode()!=null && bean.getRescode().equals("1") && ispwd){
//					return bean;
//				}else if(!StringUtil.isEmpty(userBean.getIdentify()) && !ispwd ){
//					userBean.setPhone(userBean.getAccount());
//					String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
//					if(tips.length()>0){
//						bean.setRescode(IConstant.FIRDES);
//						bean.setResmsg(tips);
//						return bean;
//					}
//				}
				return getCorpVos(userBean, bean);
//			}
		} catch (Exception e) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg(e.getMessage());
			log.error(e.getMessage(),e);
		}
		return bean;
	}

	
	/**
	 * 检查来源
	 * @param userBean
	 */
	private void checkSource(UserBeanVO userBean,List<TempUserRegVO> templistapp) {
		if(!StringUtil.isEmpty(userBean.getSourcesys())
				&& SourceSysEnum.SOURCE_SYS_PST_APP.getValue().equals(userBean.getSourcesys())
				&& StringUtil.isEmpty(userBean.getBw_login())
				){
//			throw new BusinessException("百望登录信息为空!");
		}
		
		
		if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(userBean.getSourcesys())){
			if(StringUtil.isEmpty(templistapp.get(0).getPk_svorg())
					&& userBean.getVersionno()<= IVersionConstant.VERSIONNO326){//老版本进行校验
				throw new BusinessException("您用户尚未注册机构!");
			}
		}
		
		
	}

	private void checkPassword(UserBeanVO userBean, boolean ispwd, LoginResponseBeanVO bean,
			ArrayList<AppUserVO> alres,List<TempUserRegVO> templistapp) throws Exception {
		String pwd = null;
		// 只有临时用户
		if ((alres == null || alres.size() == 0)) {
			pwd = templistapp.get(0).getUser_password();
		} else {
			pwd = alres.get(0).getUser_password();
		}
		
		if (!ispwd) {
			if (pwd == null || "".equals(pwd)) {
				bean.setPwdcode("0");
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("未设置密码,请用短信登录!");
			} else {
				bean.setPwdcode("1");
			}
		} else {
			String longinpwd  = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());
			if (StringUtil.isEmpty(pwd)) {
				bean.setPwdcode("0");
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("未设置密码,请用短信登录!");
			}
			if (!longinpwd.equalsIgnoreCase(pwd)) {
				bean.setRescode(IConstant.FIRDES);
				bean.setResmsg("密码不正确!");
			} else {
				bean.setPwdcode("1");
			}
		}
	}

	private DZFBoolean isTempCorp(List<TempUserRegVO> templistapp, ArrayList<AppUserVO> alres) {

		DZFBoolean istemp = DZFBoolean.FALSE;
		if (alres == null || alres.size() == 0) {
			istemp = DZFBoolean.TRUE;
		} else if (alres != null && alres.size() == 1 
				&& StringUtil.isEmpty(alres.get(0).getPk_tempcorp())
				&& StringUtil.isEmpty(alres.get(0).getPk_corp())) {
			istemp = DZFBoolean.TRUE;
		}
		return istemp;

	}

	// 临时用户
	private List<TempUserRegVO> getTempList(UserBeanVO userBean) {
		SQLParameter sp = new SQLParameter();
		String appsql = "select * from app_temp_user where user_code=? and  nvl(isaccount,'N') ='N' and nvl(dr,0)=0 ";
		sp.addParam(userBean.getUsercode());
		List<TempUserRegVO> templistapp = (List<TempUserRegVO>) singleObjectBO.executeQuery(appsql, sp,
				new BeanListProcessor(TempUserRegVO.class));

		return templistapp;

	}

	// 获取公司信息
	private void getSubCopMsgBeanVO(UserBeanVO userBean, ArrayList<AppUserVO> alres, LoginResponseBeanVO bean,
			String pk_svorg  ) throws DZFWarpException {
		Vector<SubCopMsgBeanVO> sublist = new Vector<SubCopMsgBeanVO>();

		String account = "";
		String account_id = "";
		if (alres != null && alres.size() > 0) {
			// 一个公司对应一个list数据
			bean.setIsautologin("1");
			DZFBoolean isconsigncorp = DZFBoolean.FALSE;
			
			Set<String> tempcorps = new HashSet<String>();
			Map<String, TempCorpVO> t_map_corp = new HashMap<String,TempCorpVO>();
			Set<String> corps = new HashSet<String>();
			Map<String,CorpVO> map_corp = new HashMap<String,CorpVO>();

			for(AppUserVO uservo : alres){
				if(!StringUtil.isEmpty(uservo.getPk_tempcorp())){
					tempcorps.add(uservo.getPk_tempcorp());
				}
				if(!AppCheckValidUtils.isEmptyCorp(uservo.getPk_corp())){
					corps.add(uservo.getPk_corp());
				}
			}
			
			getMapVOFromKey(corps, map_corp);
			
			getMapTempVOFromKey(tempcorps, t_map_corp);
			
			Map<String,String> umap =  apppubservice.getManageUserFromCorp(corps.toArray(new String[0]), tempcorps.toArray(new String[0]));
			
			
			for (AppUserVO uservo : alres) {
				account_id  = uservo.getPrimaryKey();
				account=uservo.getUser_code();
				// 帐号停用
				if (!StringUtil.isEmptyWithTrim(uservo.getPk_corp())) {
					if (uservo.getPk_corp().trim().equals(IGlobalConstants.DefaultGroup)) {// 该用户不能为集团用户
						continue;
					}

					if (uservo.getLocked() != null && uservo.getLocked().booleanValue()) {// 已被锁定,请联系管理员
						continue;
					}

					if (uservo.getBappuser() != null && uservo.getBappuser().booleanValue()) {
						continue;
					}
					if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(userBean.getSourcesys())){
						if(!StringUtil.isEmpty(pk_svorg) ){
							if(!AppCheckValidUtils.isEmptyCorp(uservo.getPk_corp())){
								if(!pk_svorg.equals(map_corp.get(uservo.getPk_corp()).getFathercorp())){//正式公司信息
									continue;
								}
							}else if(!StringUtil.isEmpty(uservo.getPk_tempcorp())){
								if(!pk_svorg.equals(t_map_corp.get(uservo.getPk_tempcorp()).getPk_svorg())){//临时公司信息
									continue;
								}
							}
						}
					}
					
					if(SourceSysEnum.SOURCE_SYS_WX_APPLET.getValue().equals(userBean.getSourcesys())){//微信小程序，不显示管理员
						if((uservo.getIsmanager() == null  ||  
								!uservo.getIsmanager().booleanValue()) && uservo.getIstate() != 2){//待审核的不显示
							continue;
						}
					}

					if (!AppCheckValidUtils.isEmptyCorp(uservo.getPk_corp()) && uservo.getIstate() == 2) {
						isconsigncorp = DZFBoolean.TRUE;
					}
					SubCopMsgBeanVO subbean = new SubCopMsgBeanVO();
					subbean.setPk_tempcorp(uservo.getPk_tempcorp());
					subbean.setCorpname(AppCheckValidUtils.isEmptyCorp(uservo.getPk_corp()) ? uservo.getCorpnm()
							: CodeUtils1.deCode(uservo.getApp_corpname()));
					subbean.setPk_corp(uservo.getPk_corp());
					subbean.setAccount_id(uservo.getPrimaryKey());
					subbean.setIsys(IConstant.FIRDES);
					subbean.setAccount(uservo.getUser_code());
					subbean.setBdata(uservo.getBdata().toString());
					subbean.setBaccount(uservo.getBaccount().toString());
					subbean.setIstate(String.valueOf(uservo.getIstate()));
					subbean.setCorpaddr(uservo.getApp_corpadd() == null ? "" : uservo.getApp_corpadd());
					subbean.setPriid(uservo.getPriid() == null ? "" : uservo.getPriid());
					subbean.setUser_name(StringUtil.isEmpty(uservo.getUser_name()) ? uservo.getUser_code()
							: CodeUtils1.deCode(uservo.getUser_name()));
					if (StringUtil.isEmpty(subbean.getCorpname())) {
						subbean.setCorpname(uservo.getApp_corpname() == null ? CodeUtils1.deCode(uservo.getUnitname())
								: uservo.getApp_corpname());
					}
					if (uservo.getBisgn() != null && uservo.getBisgn().booleanValue()) {
						subbean.setCorpname(CodeUtils1.deCode(uservo.getUnitname()));
					}

					if (uservo.getUser_password() == null || "".equals(uservo.getUser_password())) {
						bean.setPwdcode("0");
					} else {
						bean.setPwdcode("1");
					}
					
					if(!"2".equals(subbean.getIstate())){
						String name = "";
						if(!StringUtil.isEmpty(subbean.getPk_tempcorp())
								&& umap.get(subbean.getPk_tempcorp()) != null){
							name = umap.get(subbean.getPk_tempcorp());
						}
						if(!AppCheckValidUtils.isEmptyCorp(subbean.getPk_corp())
								&& umap.get(subbean.getPk_corp()) != null){
							name = umap.get(subbean.getPk_corp());
						}
						subbean.setTips("该公司正在审核中,请联系管理员"+name+"审核");
					}

					subbean.setAccname(CodeUtils1.deCode(uservo.getAccname()));
					subbean.setAccfc(getAccFc( uservo.getSealeddate()));
					//添加不封存的会计公司
					if(IConstant.FIRDES.equals(subbean.getAccfc())){
						sublist.add(subbean);
					}
				}
			}
			
			//不包含签约公司+全部代账公司被封存 则显示临时公司
			if(!isconsigncorp.booleanValue() || sublist.size() == 0){
				SubCopMsgBeanVO subbeandemo = AppQueryUtil.getInstance().addDemoCorp(userBean, alres.get(0).getUser_code(),CodeUtils1.deCode(alres.get(0).getUser_name()),
						alres.get(0).getCuserid());
				sublist.add(0, subbeandemo);
			}
		}

		if (sublist.size() == 0) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("您用户对应的公司权限已取消，请联系客服！");
		} else {
			bean.setAccount(account);
			bean.setAccount_id(account_id);
			bean.setRescode(IConstant.DEFAULT);
			bean.setResmsg("获取数据成功!");
			bean.setSubcorpvos(sublist.toArray(new SubCopMsgBeanVO[0]));
		}
	}


	private boolean bAllFc(Vector<SubCopMsgBeanVO> sublist) {
		if (sublist != null && sublist.size() > 0){
			for(SubCopMsgBeanVO bean :sublist){
				if(IConstant.FIRDES.equals(bean.getAccfc())){//没有被封存
					return false;
				}
			}
		}
		return true;
	}


	private String getAccFc(DZFDate sealeddate) {
		if(sealeddate!=null){
			if(new DZFDate().compareTo(sealeddate)>=0){
				return IConstant.DEFAULT;//服务器日期在封存日期后则说明已经封存
			}
		}
		return IConstant.FIRDES;
	}


	private void getMapVOFromKey(Set<String> corps, Map<String, CorpVO> map_corp) {
		if(corps.size()>0){
			CorpVO[] cpvos = (CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class, "nvl(dr,0)=0 and "+ SqlUtil.buildSqlForIn("pk_corp", corps.toArray(new String[0])), new SQLParameter());
			if(cpvos!=null && cpvos.length>0){
				for(CorpVO cpvo:cpvos){
					map_corp.put(cpvo.getPk_corp(), cpvo);
				}
			}
		}
	}
	
	private void getMapTempVOFromKey(Set<String> corps, Map<String, TempCorpVO> map_corp) {
		if(corps.size()>0){
			TempCorpVO[] cpvos = (TempCorpVO[]) singleObjectBO.queryByCondition(TempCorpVO.class, "nvl(dr,0)=0 and "+ SqlUtil.buildSqlForIn("pk_temp_corp", corps.toArray(new String[0])), new SQLParameter());
			if(cpvos!=null && cpvos.length>0){
				for(TempCorpVO cpvo:cpvos){
					map_corp.put(cpvo.getPk_temp_corp(), cpvo);
				}
			}
		}
	}

	// 有权限的公司用户
	private ArrayList<AppUserVO> getUserList(UserBeanVO userBean) {
		
		// 登录sql
		StringBuffer loginsql = new StringBuffer();
		// 新用户
		loginsql.append("   select t.pk_corp_user as priid, a.cuserid cuserid,a.user_code user_code, a.user_name user_name,t.pk_tempcorp ,");
		loginsql.append("   a.job, a.app_corpname  as app_corpname ,a.app_user_tel,a.app_user_qq,a.app_user_mail,a.app_user_memo, ");
		loginsql.append("   t.pk_corp pk_corp,a.locked_tag locked,t.ismanage ismanager,nvl(a.bdata,'N') bdata,nvl(a.baccount,'N') baccount, ");
		loginsql.append("   nvl(bconfirmsign,'N') as bconfirmsign,  temp.corpname corpnm,c.isdatacorp bappuser,c.isaccountcorp isCa ,c.unitname ,nvl(t.istate,'2')  as istate, ");// 上面的公司地址应该去app_temp_corp里面取
		loginsql.append("   case nvl(temp.longitude,'0') when '0' then c.def10 else temp.longitude end  longitude, case nvl(temp.latitude,'0') when '0' then c.def11 else temp.latitude end  latitude ,   ");
		loginsql.append("   case nvl(temp.corpaddr,'')  when '' then c.postaddr else  temp.corpaddr  end as  app_corpadd ,   ");
		loginsql.append("   a.user_password , ba.unitcode as acccode,ba.unitname as accname ,ba.pk_corp as accid , ");
		loginsql.append("   ba.sealeddate as  sealeddate " );
		loginsql.append("    from sm_user a     ");
		loginsql.append("   left join ynt_corp_user t on a.cuserid= t.pk_user and nvl(a.dr,0)=0   and nvl(t.dr,0)=0 ");
		loginsql.append("   left join bd_corp c on t.pk_corp=c.pk_corp ");
		loginsql.append("   left join app_temp_corp temp on t.pk_tempcorp = temp.pk_temp_corp   ");
		loginsql.append("   left join bd_account ba on c.fathercorp = ba.pk_corp  ");
		loginsql.append("   where a.user_code = ?  and nvl(a.dr,0)=0  and nvl(t.dr,0)=0  and nvl(c.isseal , 'N') ='N'   ");
		userBean.setAccount(userBean.getUsercode());
		SQLParameter sp = new SQLParameter();
		sp.addParam(userBean.getAccount());
		
//		if(!StringUtil.isEmpty(userBean.getBw_login())){
//			JSONObject array = (JSONObject) JSON.parse(userBean.getBw_login());
//			Map<String, String> bodymapping = FieldMapping.getFieldMapping(new LoginResultBean());
//			LoginResultBean resbean = DzfTypeUtils.cast(array, bodymapping, LoginResultBean.class,
//					JSONConvtoJAVA.getParserConfig());
//
//			if (resbean.getResult() == null || !"200".equals(resbean.getResult().getReturnCode())) {
//				if (resbean.getResult() != null) {
//					throw new BusinessException(resbean.getResult().getReturnMessages());
//				} else {
//					throw new BusinessException("登录公司有误!");
//				}
//			}
//			com.dzf.model.app.bwticket.LoginResultBean.ResultBean.SalesUnitInfoBean infobean = resbean.getResult()
//					.getSalesUnitInfo();
//
//			if(!StringUtil.isEmpty(infobean.getSalesUnitName())){
//				loginsql.append(" and  c.unitname = ? ");
//				sp.addParam(CodeUtils1.enCode(infobean.getSalesUnitName()));
//			}
//		}
		ArrayList<AppUserVO> alres = (ArrayList<AppUserVO>) singleObjectBO.executeQuery(loginsql.toString(), sp, new BeanListProcessor(AppUserVO.class));
		return alres;
	}

	private LoginResponseBeanVO getCorpVos(UserBeanVO userBean, LoginResponseBeanVO bean) throws DZFWarpException {
		
		List<TempUserRegVO> templistapp = getTempList(userBean);

		ArrayList<AppUserVO> alres = getUserList(userBean);

		if ((templistapp == null || templistapp.size() == 0) && (alres == null || alres.size() == 0)) {
			bean.setRescode(IConstant.FIRDES);
			bean.setResmsg("您用户不存在，请注册登录!");
			return bean;
		} else {
			String pk_svorg_cst = "";
			if(templistapp != null && templistapp.size() > 0){
				pk_svorg_cst = templistapp.get(0).getPk_svorg();
			}
//			//1:判断是否只有临时用户
//			DZFBoolean istemp = isTempCorp(templistapp, alres);
//			if (istemp.booleanValue()) {//临时公司，直接弹演示公司
//				CorpVO[] corpvo = AppQueryUtil.getInstance().getDemoCorpMsg();
//				String usercode = userBean.getAccount();
//				String username = templistapp.get(0).getUser_name();
//				String accountid = templistapp.get(0).getPk_temp_user();
//				String pk_svorg= templistapp.get(0).getPk_svorg();
//				//登录成功数据
//				democorpser.sendDemoCorp(bean, usercode, username, accountid,
//						userBean.getVdevicdmsg(), corpvo,userBean.getSourcesys(),pk_svorg);
//				//公司列表数据
//				List<SubCopMsgBeanVO> sublist = new ArrayList<SubCopMsgBeanVO>();
//			    SubCopMsgBeanVO subbeandemo = AppQueryUtil.getInstance().addDemoCorp(userBean, usercode,usercode,accountid);
//				sublist.add(0, subbeandemo);
//				bean.setSubcorpvos(sublist.toArray(new SubCopMsgBeanVO[0]));
//			} else {//2：如果存在公司取出公司列表
				getSubCopMsgBeanVO(userBean, alres, bean,pk_svorg_cst);
			}
			
			//3：如果只有一家公司则自动登录
			if (bean.getRescode().equals(IConstant.DEFAULT)) {
				if (bean.getSubcorpvos() != null && bean.getSubcorpvos().length == 1
						&& !ISysSourceConstant.SYSTYPE_IOS.equals(userBean.getSystype())
						&& !IConstant.DEFAULT.equals(bean.getIsys())) {//只有一家公司时自动登录
					userBean.setPk_corp(bean.getSubcorpvos()[0].getPk_corp());
					userBean.setCorpname(bean.getSubcorpvos()[0].getCorpname());
					userBean.setPk_tempcorp(bean.getSubcorpvos()[0].getPk_tempcorp());
					userBean.setUsercode(userBean.getAccount());
					userBean.setAccount_id(bean.getSubcorpvos()[0].getAccount_id());

					IAppLoginCorpService user320service = (IAppLoginCorpService) SpringUtils.getBean("user320service");

					bean = user320service.loginFromTel(userBean,bean);
					
					bean.setIsautologin(IConstant.DEFAULT);
				}
			}
			
			//4:获取待激活公司信息
			CorpVO[] cpvos = apppubservice.getNoLinkCorp(userBean.getAccount(),userBean.getSourcesys());
			
			if (cpvos != null && cpvos.length > 0) {
				bean.setCpvos(cpvos);
			}
			
			bean.setIbind(IConstant.DEFAULT);
			return bean;
//		}
	}



	@Override
	public LoginResponseBeanVO saveRegisterAndLinkCorp(UserBeanVO userBean,DZFBoolean blogin) throws DZFWarpException {
		LoginResponseBeanVO bean = new LoginResponseBeanVO();
		//1：校验手机号
		AppCheckValidUtils.validatePhone(userBean.getPhone());
		//2：没登录 需要验证密码，验证码，等信息
		if(blogin == null || !blogin.booleanValue()){
			if (StringUtil.isEmptyWithTrim(userBean.getIdentify())) {
				throw new BusinessException("验证码不能为空");
			}

			if (StringUtil.isEmptyWithTrim(userBean.getPassword())) {
				throw new BusinessException("密码不能为空!");
			}

			String tips = CommonServ.resetUserIdentify(userBean, userBean.getIdentify(), true);
			
			if(tips.length()>0){
				throw new BusinessException(tips);
			}
		}
		//3：判断用户是否存在,如果存在 则需要更新密码等信息，如果存在，注册用户
		boolean isexist = apppubservice.isExistUser(userBean.getPhone());
		
		String password = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());
		
		if (!isexist) {
			// 开始用户注册
			TempUserRegVO tempuservo = new TempUserRegVO();
			tempuservo.setUser_code(userBean.getPhone());
			tempuservo.setUser_password(password);
			tempuservo.setUser_name(userBean.getPhone());
			tempuservo.setApp_user_qq(userBean.getPhone());
			tempuservo.setPhone(userBean.getPhone());
			tempuservo.setIstate(IConstant.TWO);
			SuperVO vo = singleObjectBO.saveObject(Common.tempidcreate, tempuservo);
		}else{//如果存在 不更新用户的密码
		
		}

		//4:记录关联关系
		if(!StringUtil.isEmpty(userBean.getCorpid())){
			 corpservice.saveUserFromInvite(userBean,0);
		}
		
		// 注册成功登录公司
		userBean.setUsercode(userBean.getPhone());

		return getCorpVos(userBean, bean);
	}
		
	private String getPwd(){
		
		StringBuffer pwd = new StringBuffer();
		
		Random random = new Random();
		for(int i =0;i<6;i++){
			pwd.append(random.nextInt(10));
		}
		
		return pwd.toString();
		
	}
	@Override
	public void saveScanQrCode(UserBeanVO userBean,String requrl) throws DZFWarpException {
		LoginQRVO loginQRVO = new LoginQRVO();
		loginQRVO.setUuid(requrl);
		loginQRVO.setCoperatorid(userBean.getAccount_id());
		loginQRVO.setPk_corp(userBean.getPk_corp());
		loginQRVO.setIstatus(LoginQRConstant.QR_SCAN);
		updateQRCode(loginQRVO);
	}
	public void updateQRCode(LoginQRVO loginQRVO) throws DZFWarpException {
		if(StringUtil.isEmpty(loginQRVO.getUuid())){
			throw new BusinessException("二维码请求串不能为空！");
		}
		if(StringUtil.isEmpty(loginQRVO.getPk_corp())){
			throw new BusinessException("会计公司信息不能为空！");
		}
		if(StringUtil.isEmpty(loginQRVO.getCoperatorid())){
			throw new BusinessException("登录人员信息不能为空！");
		}

		String strUUID = loginQRVO.getUuid();
		SQLParameter sp = new SQLParameter();
		sp.addParam(strUUID);
		String sql="select * from ynt_login_qrcode where nvl(dr,0) = 0 and trim(uuid) = ?";
		List<LoginQRVO> list = (ArrayList<LoginQRVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(LoginQRVO.class));

		if (list== null || list.size()<=0) {
			String msg = "";
			if (loginQRVO.getIstatus() == LoginQRConstant.QR_SCAN) {
				msg = "扫码失败";
			} else if (loginQRVO.getIstatus() == LoginQRConstant.QR_AUTH) {
				msg = "授权失败";
			}
			throw new BusinessException(msg);
		}
		LoginQRVO qrvo = list.get(0);
		if (loginQRVO.getIstatus() == LoginQRConstant.QR_AUTH) {
			checkDataValid(loginQRVO, qrvo);
		}
		qrvo.setIstatus(loginQRVO.getIstatus());
		qrvo.setCoperatorid(loginQRVO.getCoperatorid());
		qrvo.setPk_corp(loginQRVO.getPk_corp());
		singleObjectBO.saveObject(qrvo.getPk_corp(),qrvo);
	}
	/**
	 * loginQRVO 为前台传递来 loginQROldVO为数据库来
	 */
	private void checkDataValid(LoginQRVO loginQRVO, LoginQRVO loginQROldVO) throws DZFWarpException {
		if (loginQRVO.getPk_corp() == null || loginQRVO.getCoperatorid() == null) {
			throw new BusinessException("用户名或公司不能为空，请检查");
		}
		if (!loginQRVO.getPk_corp().equals(loginQROldVO.getPk_corp())) {
			throw new BusinessException("授权公司与扫码公司不一致，请检查");
		}
		if (!loginQRVO.getCoperatorid().equals(loginQROldVO.getCoperatorid())) {
			throw new BusinessException("授权人与扫码处理人不一致，请检查");
		}
	}
	@Override
	public void saveConfirmQrCode(UserBeanVO userBean,String requrl) throws DZFWarpException {
		LoginQRVO auloginQRVO = new LoginQRVO();
		auloginQRVO.setUuid(requrl);
		auloginQRVO.setCoperatorid(userBean.getAccount_id());
		auloginQRVO.setPk_corp(userBean.getPk_corp());
		auloginQRVO.setIstatus(LoginQRConstant.QR_AUTH);
		updateQRCode(auloginQRVO);
	}
}

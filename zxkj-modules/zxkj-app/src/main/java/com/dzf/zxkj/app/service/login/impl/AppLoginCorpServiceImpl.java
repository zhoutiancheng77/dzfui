package com.dzf.zxkj.app.service.login.impl;

import com.dzf.zxkj.app.model.app.corp.UserToCorp;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.login.IAppDemoCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.message.IMessageQryService;
import com.dzf.zxkj.app.service.user.IAppUserService;
import com.dzf.zxkj.app.utils.*;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpDocVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 320版本登录
 * @author zhangj
 *
 */
@Service("user320service")
@Slf4j
public class AppLoginCorpServiceImpl implements IAppLoginCorpService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private IAppDemoCorpService democorpser;
	
	@Override
	public LoginResponseBeanVO loginFromTel(UserBeanVO userBean, LoginResponseBeanVO bean) throws DZFWarpException {
		if (StringUtil.isEmptyWithTrim(userBean.getUsercode()) && StringUtil.isEmpty(userBean.getAccount())) {
			throw new BusinessException("登录出错:账号不能为空!");
		}

		if (StringUtil.isEmpty(userBean.getUsercode())) {// account == usercode
			userBean.setUsercode(userBean.getAccount());
		}

		String corpname = userBean.getCorpname();
		CorpVO[] corpvo = AppQueryUtil.getInstance().getDemoCorpMsg();
		if (corpvo != null && corpvo.length > 0) {
			String deunitname = CodeUtils1.deCode(corpvo[0].getUnitname());
			if (deunitname.equals(corpname) || corpvo[0].getPk_corp().equals(userBean.getPk_corp())) {
				democorpser.sendDemoCorp(bean, userBean.getUsercode(), userBean.getUsername(), userBean.getAccount_id(),
						userBean.getVdevicdmsg(), corpvo, userBean.getSourcesys(),"");
				return bean;
			}
		}
		//判断当前账号是否存在在正式表存在，如果不存在，则从临时表查询
		UserVO uvo =  (UserVO) singleObjectBO.queryByPrimaryKey(UserVO.class, userBean.getAccount_id());
		if(uvo == null){
			TempUserRegVO tuvo = (TempUserRegVO) singleObjectBO.queryByPrimaryKey(TempUserRegVO.class, userBean.getAccount_id());
			if(tuvo!=null &&  !StringUtil.isEmpty(tuvo.getPk_user())){
				userBean.setAccount_id(tuvo.getPk_user());
			}
		}
		// 登录sql
		StringBuffer loginsql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		DZFBoolean isdecode = DZFBoolean.FALSE;// 是否需要解密
		if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
			getLoginCorpSQL(userBean, sp, userBean.getPk_corp(), loginsql);
			isdecode = DZFBoolean.TRUE;
		} else {
			getLoginTempCorpSQL(userBean, sp, null, loginsql);// 如果当前公司还没签约的时候
		}

		ArrayList<AppUserVO> alres = (ArrayList<AppUserVO>) singleObjectBO.executeQuery(loginsql.toString(), sp,
				new BeanListProcessor(AppUserVO.class));
		if (alres != null && alres.size() > 0) {
			IAppUserService iaus = (IAppUserService) SpringUtils.getBean("userservice");
			AppUserVO uservo = alres.get(0);
			// 帐号停用
			if (iaus.getPrivilege(null, uservo.getUser_code(), "", uservo.getPk_corp(), uservo.getPk_tempcorp())) {
				throw new BusinessException("您账号待管理员审核，请联系管理员!");
			} else {
				if (!StringUtil.isEmptyWithTrim(uservo.getPk_corp())) {
					
					checkUserPower(uservo, bean, userBean);

					// 返回登录基本信息
					putResultBean(userBean, bean, isdecode, userBean.getSourcesys(), alres, uservo, getTempList(userBean));

					// 如果公司已经签约则出现会计公司广告
					putFatherCorp(uservo.getPk_corp(), bean, userBean.getSourcesys());

					// 消息未读数信息
					IMessageQryService ms321service = (IMessageQryService) SpringUtils.getBean("ms321service");
					
					bean.setUnrcount(ms321service.getAllMessageUnRead(bean.getPk_corp(), bean.getPk_temp_corp(),
							bean.getAccount_id()));

					bean.setRescode(IConstant.DEFAULT);
					bean.setResmsg("登录成功");

				}
			}
		} else {
			throw new BusinessException("登录失败:用户名不存在");
		}
		return bean;
	}
	
	// 临时用户
	private List<TempUserRegVO> getTempList(UserBeanVO userBean) {
		SQLParameter sp = new SQLParameter();
		String appsql = "select * from app_temp_user where user_code=? and  nvl(isaccount,'N') ='N' ";
		sp.addParam(userBean.getUsercode());
		List<TempUserRegVO> templistapp = (List<TempUserRegVO>) singleObjectBO.executeQuery(appsql, sp,
				new BeanListProcessor(TempUserRegVO.class));

		return templistapp;

	}

	private void putResultBean(UserBeanVO userBean, LoginResponseBeanVO bean, DZFBoolean isdecode, String soucesys,
			ArrayList<AppUserVO> alres, AppUserVO uservo,List<TempUserRegVO> tempuservolist) {
		List<String> pk_svorg = new ArrayList<String>();
		
		for (int i = 0; i < alres.size(); i++) {
			if (!StringUtil.isEmpty(alres.get(i).getPk_svorg())) {
				pk_svorg.add(alres.get(i).getPk_svorg());
			}
		}
		
//		bean.setLguuid(handStr(uservo.getPriid(), 0));
		bean.setLguuid(AppEncryPubUtil.enctyParam(uservo.getPriid()));
		String inviteurl = "";
		bean.setInvite_url(inviteurl);//邀请人员地址
		bean.setPk_temp_corp(uservo.getPk_tempcorp());
		bean.setCorpname(uservo.getCorpnm());
		bean.setPriid(uservo.getPriid());// 用户在公司的唯一标识
		bean.setPk_corp(StringUtil.isEmpty(uservo.getPk_corp())? Common.tempidcreate:uservo.getPk_corp());//公司主键
		bean.setUsergrade(uservo.getIsmanager().booleanValue() ?IConstant.FIRDES:IConstant.DEFAULT);//是否是管理员
		
		bean.setBegdate(uservo.getBegdate());//建账日期
		bean.setIsys(IConstant.FIRDES);//是否是演示公司
		bean.setAccount_id(uservo.getPrimaryKey());//用户id
		bean.setAccount(uservo.getUser_code());//用户编码
		bean.setBdata(uservo.getIsmanager().booleanValue() ? "Y" : uservo.getBdata().toString());//是否有报表权限
		bean.setBaccount(uservo.getIsmanager().booleanValue() ? "Y" : uservo.getBaccount().toString());//是否有上传图片权限
		bean.setIsdemo(IConstant.FIRDES);
		bean.setUser_name(uservo.getUser_name() == null ? uservo.getUser_code() : CodeUtils1.deCode(uservo.getUser_name()));
		bean.setBbillapply(uservo.getIsmanager().booleanValue() ? "Y" : uservo.getBbillapply());
		bean.setInnercode(uservo.getInnercode());
		
		bean.setCorpaddr(StringUtil.isEmpty(uservo.getApp_corpadd()) ? "" :uservo.getApp_corpadd());//公司地址
		if(SourceSysEnum.SOURCE_SYS_KCDR.getValue().equals(soucesys) || 
				SourceSysEnum.SOURCE_SYS_CSDR.getValue().equals(soucesys)
				){//卡车达人app,财税达人
			if("小规模纳税人".equals(uservo.getChargedeptname())){
				bean.setChargedeptname("0");
			}else if("一般纳税人".equals(uservo.getChargedeptname())){
				bean.setChargedeptname("1");
			}
		}else{
			bean.setChargedeptname(uservo.getChargedeptname());
			if("小规模纳税人".equals(uservo.getChargedeptname())){
				bean.setChargedept_num("0");
			}else if("一般纳税人".equals(uservo.getChargedeptname())){
				bean.setChargedept_num("1");
			}
		}
		if(StringUtil.isEmpty(uservo.getChargedeptname())){
			bean.setChargedeptname("");
		}
		//-----税务信息-----------
		bean.setSh(StringUtil.isEmpty(uservo.getSh()) ? "" : uservo.getSh());
		bean.setKhh(StringUtil.isEmpty(uservo.getKhh()) ? "" : uservo.getKhh());
		bean.setKhzh(StringUtil.isEmpty(uservo.getKhzh()) ? "" : uservo.getKhzh());
		if(!StringUtil.isEmpty(uservo.getKpdh())){
			bean.setKpdh(isdecode.booleanValue() ? CodeUtils1.deCode(uservo.getKpdh()):uservo.getKpdh());
		}else{
			bean.setKpdh("");
		}
		
		if (uservo.getIsca() != null && uservo.getIsca().booleanValue()) {
			bean.setIsaccorp("Y");
			bean.setUsergrade(IConstant.FIRDES);
		} else {
			bean.setIsaccorp("N");
		}
		// 如果改公司存在则也是isdemo也是0
		if (!AppCheckValidUtils.isEmptyCorp(uservo.getPk_corp())) {
			bean.setIsdemo(IConstant.DEFAULT);//是否已签约的公司
		}
		// 公司经纬度
		if (!StringUtil.isEmpty(uservo.getLatitude()) && !"~".equals(uservo.getLatitude())) {
			bean.setCorplatitude(uservo.getLatitude());
		} else {
			bean.setCorplatitude("");
		}
		if (!StringUtil.isEmpty(uservo.getLongitude()) && !"~".equals(uservo.getLongitude())) {
			bean.setCorplongitude(uservo.getLongitude());
		} else {
			bean.setCorplongitude("");
		}
		if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(soucesys)){//财税通来源
			if(StringUtil.isEmpty(tempuservolist.get(0).getPk_svorg()) 
					|| tempuservolist.get(0)==null
					|| StringUtil.isEmpty(tempuservolist.get(0).getPk_svorg()) 
					|| !tempuservolist.get(0).getPk_svorg().equals(tempuservolist.get(0).getPk_svorg())
					){
				throw new BusinessException("非当前代账机构不能登录");
			} 
		} else{
			bean.setPk_svorg(pk_svorg.toArray(new String[0]));
		}
		if (bean.getCorpname() == null) {
			bean.setCorpname(uservo.getApp_corpname() == null ?  CodeUtils1.deCode(uservo.getUnitname()) : uservo.getApp_corpname());
		}
		if (uservo.getBisgn() != null && uservo.getBisgn().booleanValue()) {
			bean.setCorpname(CodeUtils1.deCode(uservo.getUnitname()));
		}
		if (StringUtil.isEmpty(uservo.getUser_password())) {
			bean.setPwdcode(IConstant.DEFAULT);
		} else {
			bean.setPwdcode(IConstant.FIRDES);
		}
		
		bean.setPhotopath(UserUtil.getHeadPhotoPath(userBean.getAccount(),0));
	}
	
	private boolean getAccFc(DZFDate sealeddate) {
		if(sealeddate!=null){
			if(new DZFDate().compareTo(sealeddate)>=0){
				return true;//服务器日期在封存日期后则说明已经封存
			}
		}
		return false;
	}
	
	
	/**
	 * 公司信息赋值
	 * @param pk_corp
	 * @param bean
	 */
	public void putFatherCorp(String pk_corp, LoginResponseBeanVO bean,String soucesys) throws DZFWarpException {
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		String fathercorp = null;
		if (cpvo != null) {
			fathercorp = cpvo.getFathercorp();
			//如果代账公司被封存则不能登录
			CorpVO fcpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, fathercorp);
			fcpvo = CorpUtil.getCorpvo(fcpvo);
			boolean fc = getAccFc(fcpvo.getSealeddate());
			if(fc){
				throw new BusinessException("登录失败:该代账公司被封存");
			}
		} else if (bean.getPk_svorg() != null && bean.getPk_svorg().length > 0 
				&& SourceSysEnum.SOURCE_SYS_CST.getValue().equals(soucesys)) {
			fathercorp = bean.getPk_svorg()[0];
		} else {
			fathercorp = IGlobalConstants.currency_corp;
		}
		
		if (!StringUtil.isEmpty(fathercorp)) {
			SQLParameter sp = new SQLParameter();
			StringBuffer cxsql = new StringBuffer();
			cxsql.append(" select vfilepath,vurllink from ynt_corpdoc ");
			cxsql.append(" where nvl(dr,0)=0 and pk_corp = ? ");
			sp.addParam(fathercorp);
			if(fathercorp.equals(IGlobalConstants.currency_corp)){//集团的图片
				cxsql.append(" and (sys_type is null or sys_type = 'dzf_app') ");
			}else {
				cxsql.append(" and (sys_type = 'dzf_kj') ");//管理端的图片
			}
			List<CorpDocVO> filelistvo =  (List<CorpDocVO>) singleObjectBO.executeQuery(cxsql.toString(), sp,
					new BeanListProcessor(CorpDocVO.class));
			if (filelistvo != null && filelistvo.size() > 0) {
				String[] liststrs = new String[filelistvo.size()];
				for(int i =0;i<filelistvo.size();i++){
					filelistvo.get(i).setVfilepath(CryptUtil.getInstance().encryptAES(filelistvo.get(i).getVfilepath()));
					liststrs[i] = CryptUtil.getInstance().encryptAES(filelistvo.get(i).getVfilepath());
				}
				bean.setAdverts(liststrs);
				bean.setAdvertvos(filelistvo.toArray(new CorpDocVO[0]));
			}
			if(!fathercorp.equals(IGlobalConstants.currency_corp)){
				AccountVO fcorp = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, fathercorp);
				bean.setFname(CodeUtils1.deCode(fcorp.getUnitname()));
				bean.setFtel(CodeUtils1.deCode(fcorp.getPhone1()));
				bean.setFcode(fcorp.getInnercode());
				bean.setFurl(CryptUtil.getInstance().encryptAES(fcorp.getDef8()) );//网址
				bean.setFlogo(CryptUtil.getInstance().encryptAES(fcorp.getUrl()) );//logo地址
				bean.setQysbh(fcorp.getDef12());//企业识别号
			}
		}
	} 
	
	
//	/**
//	 * 
//	 * @param strvalue
//	 * @param mode 0 是手机端展示，1是解析后台使用
//	 * @return
//	 */
//	private String handStr(String strvalue,int mode){
//		if(StringUtil.isEmpty(strvalue)){
//			return "";
//		}
//		String resvalue = null;
//		if(mode == 0){
//			if(strvalue.startsWith("appuse")){
//				resvalue = strvalue.substring(6);
//			}else{
//				resvalue = strvalue;
//			}
//		}else{
//			if(strvalue.length() == 18){//去掉特殊字符的处理
//				resvalue = "appuse"+strvalue;
//			}else{
//				resvalue = strvalue;
//			}
//		}
//		return resvalue;
//	}
	
	private void checkUserPower(AppUserVO uservo,LoginResponseBeanVO bean,UserBeanVO userBean) {
		
		if(uservo.getIstate().intValue() !=2){
			throw new BusinessException("您账号待管理员审核，请联系管理员!");
		}
		
		if (uservo.getPk_corp().trim().equals(IGlobalConstants.DefaultGroup)) {// 该用户不能为集团用户
			throw new BusinessException("此账号已停用，请联系管理员!");
		}
		
		if (uservo.getLocked() != null && uservo.getLocked().booleanValue()) {// 已被锁定,请联系管理员
			throw new BusinessException("账户【" + userBean.getAccount() + "】已被锁定,请联系管理员");
		}
		
		if (uservo.getBappuser() != null && uservo.getBappuser().booleanValue()) {
			throw new BusinessException("数据中心用户不能登录!");
		}
	}
	
	/**
	 * 获取登录没签约的公司
	 * 
	 * @param userBean
	 * @param sp
	 * @param pk_corp
	 * @param loginsql
	 */
	private void getLoginCorpSQL(UserBeanVO userBean, SQLParameter sp, String pk_corp, StringBuffer loginsql) {
		// 新用户
		loginsql.append("  select a.cuserid cuserid,a.user_code user_code, ");
		loginsql.append("   a.user_name user_name, a.app_corpname  as app_corpname ,");
		loginsql.append("   t.pk_tempcorp, t.pk_corp_user as priid,");
		loginsql.append("   c.chargedeptname, c.def10   longitude, c.def11   latitude ,c.begindate as begdate, ");
		loginsql.append("   t.pk_corp pk_corp,a.locked_tag locked,nvl(t.ismanage,'N') ismanager,nvl(t.istate,'2')  as istate, ");
		loginsql.append("   nvl(t.bdata,'N') bdata,nvl(t.baccount,'N') baccount,nvl(t.bbillapply,'N') bbillapply, a.user_password  , ");
		loginsql.append("   c.unitcode as unitcode, c.isdatacorp bappuser,");
		loginsql.append("   c.isaccountcorp isCa , c.unitname , c.postaddr as app_corpadd,");// 上面的公司地址应该去app_temp_corp里面取
		loginsql.append("   'Y' as bisgn ,'Y' as bconfirmsign,  '' as corpnm,ba.pk_corp as pk_svorg , "); // 服务机构
//		loginsql.append("   hxinfo.hxaccoutnpwd  hximpwd, hxinfo.hxaccountid hximaccountid, hxinfo.uuid hxuuid, ");// 环信账户信息
		loginsql.append("   c.taxcode as sh, c.vbankname khh,c.vbankcode khzh,c.phone1  as kpdh , ");//税务信息
		loginsql.append("   c.innercode ");
		loginsql.append("   from sm_user a ");
		loginsql.append("   left join ynt_corp_user t on a.cuserid= t.pk_user ");
		loginsql.append("   left join bd_corp c on t.pk_corp=c.pk_corp  ");
		loginsql.append("   left join bd_account ba on ba.pk_corp = c.fathercorp ");// 服务机构的经纬度
//		loginsql.append("   left join ynt_hximaccount hxinfo on a.cuserid = hxinfo.userid ");
		loginsql.append("   where 1=1 and nvl(t.dr,0)=0 and nvl(c.dr,0)=0  ");
		sp.clearParams();
		if (!StringUtil.isEmpty(pk_corp)) {
			loginsql.append("   and t.pk_corp = ? ");
			sp.addParam(pk_corp);
		}
		loginsql.append("   and a.cuserid=?  ");
		sp.addParam(userBean.getAccount_id());
		userBean.setAccount(userBean.getUsercode());
	}
	
	/**
	 * 获取已经签约的公司信息
	 * 
	 * @param userBean
	 * @param sp
	 * @param pk_corp
	 * @param loginsql
	 */
	private void getLoginTempCorpSQL(UserBeanVO userBean, SQLParameter sp, String pk_corp, StringBuffer loginsql) {
		// 新用户
		loginsql.append("  select a.cuserid cuserid,a.user_code user_code, a.user_name user_name,t.pk_tempcorp, ");
		loginsql.append("  temp.usercode as unitcode, t.pk_corp_user as priid, ");
		loginsql.append("  t.pk_corp pk_corp,a.locked_tag locked,t.ismanage ismanager,nvl(t.bdata,'N') bdata,nvl(t.baccount,'N') baccount,nvl(t.bbillapply,'N') bbillapply, ");
		loginsql.append("  case nvl(temp.longitude,'0') when '0' then c.def10 else temp.longitude end  longitude, case nvl(temp.latitude,'0') when '0' then c.def11 else temp.latitude end  latitude ,  ");
		loginsql.append("  nvl(bconfirmsign,'N') as bconfirmsign,  temp.corpname corpnm,c.isdatacorp bappuser,c.isaccountcorp isCa ,");
		loginsql.append("  temp.chargedeptname, temp.bconfirmsign bisgn ,c.unitname, nvl(t.istate,'2')  as istate, a.user_password  ,ba.pk_corp as pk_svorg ,"); 
		loginsql.append("  temp.vtaxcode as sh, temp.vbillbank khh,temp.vbillbankcode khzh,temp.vbillphone kpdh,  ");//开户信息
		loginsql.append("  temp.corpaddr as app_corpadd ");
		loginsql.append("  from sm_user a  ");
		loginsql.append("  left join ynt_corp_user t on a.cuserid= t.pk_user  ");
		loginsql.append("  left join bd_corp c on t.pk_corp=c.pk_corp  " );
		loginsql.append("  left join app_temp_corp temp on t.pk_tempcorp = temp.pk_temp_corp");
		loginsql.append("  left join app_temp_svorg temps on temps.pk_temp_corp =  temp.pk_temp_corp   ");
		loginsql.append(" left join bd_account ba on ba.pk_corp = temps.pk_svorg ");// 服务机构的经纬度
		loginsql.append("  where 1=1  and nvl(t.dr,0)=0 and nvl(temp.dr,0)=0 and nvl(temps.dr,0)=0 and nvl(c.dr,0)=0  ");
		loginsql.append("    and nvl(t.pk_corp ,'appuse') ='appuse' ");
		sp.clearParams();
		if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
			loginsql.append("   and t.pk_tempcorp = ? ");
			sp.addParam(userBean.getPk_tempcorp());
		}
		if (!StringUtil.isEmpty(pk_corp)) {
			loginsql.append("   and t.pk_corp = ? ");
			sp.addParam(pk_corp);
		}
		loginsql.append("   and a.cuserid=?  ");
		sp.addParam(userBean.getAccount_id());
		userBean.setAccount(userBean.getUsercode());
	}
	
	/**
	 * 获取自动登录的信息
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO getAutoLogin(UserBeanVO ubean , String account_id, String pk_corp , String pk_temp_corp) throws DZFWarpException{

		if(ubean.getVersionno()< IVersionConstant.VERSIONNO321){
			return null;
		}
		SQLParameter sp = new SQLParameter();
		StringBuffer qrysql =  new StringBuffer();
		qrysql.append(" select * from ynt_corp_user  " );
		qrysql.append(" where pk_user = ? and  ");
		qrysql.append(" nvl(dr,0)=0 and nvl(istate,2)=2 ");
		sp.addParam(account_id);
		if(!StringUtil.isEmpty(pk_corp)){
			qrysql.append("   and pk_corp = ?   ");
			sp.addParam(pk_corp);
		}else if(!StringUtil.isEmpty(pk_temp_corp)){
			qrysql.append("   and pk_tempcorp= ?  ");
			sp.addParam(pk_temp_corp);
		}else{
			return null;
		}
		qrysql.append(" union all ");
		qrysql.append(" select distinct yu.* from ynt_corp_user yu ");
		qrysql.append(" inner join app_temp_user tu  on yu.pk_user = tu.pk_user ");
		qrysql.append("  where tu.pk_temp_user = ? ");
		qrysql.append("  and nvl(yu.dr,0)=0 and nvl(tu.dr,0)=0  ");
		qrysql.append("  and nvl(yu.istate,2)=2 ");
		sp.addParam(account_id);
		if(!StringUtil.isEmpty(pk_corp)){
			qrysql.append("   and yu.pk_corp = ?   ");
			sp.addParam(pk_corp);
		}else if(!StringUtil.isEmpty(pk_temp_corp)){
			qrysql.append("    and yu.pk_tempcorp= ?  ");
			sp.addParam(pk_temp_corp);
		}else{
			return null;
		}
		
		List<UserToCorp> corplist=  (List<UserToCorp>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(UserToCorp.class));
		
		if(corplist == null || corplist.size() == 0){
			return null;
		}
		ubean.setAccount_id(corplist.get(0).getPk_user());
		ubean.setPk_corp(pk_corp);
		ubean.setPk_tempcorp(pk_temp_corp);
		ubean.setUsercode(ubean.getAccount());
		
		ResponseBaseBeanVO bean;
		try {
			bean = loginFromTel(ubean,new LoginResponseBeanVO());
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			return null;
		}
		
		return bean;
		
	}

	
}

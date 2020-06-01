package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.app.config.AppConfig;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.SubCopMsgBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.AccountVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * app 公共类
 * 
 * @author zhangj
 *
 */
public class AppQueryUtil {

	public static AppQueryUtil appQueryutil = null;

	public static AppQueryUtil getInstance() {
		if (appQueryutil == null) {
			appQueryutil = new AppQueryUtil();
		}
		return appQueryutil;
	}

	private AppQueryUtil() {
		super();
	}

	/**
	 * 获取demo公司信息
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO[] getDemoCorpMsg() throws DZFWarpException {
		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		AppConfig appConfig = SpringUtils.getBean(AppConfig.class);
		CorpVO[] corpvo;
		try {
			SQLParameter sp = new SQLParameter();
			String democorp =appConfig.democorpcode;// getProperValue("config.properties", "democorpcode");
			// 自动返回公司信息
			sp.clearParams();
			if (StringUtil.isEmpty(democorp)) {
				return new CorpVO[0];
			}
			sp.addParam(democorp.trim());
			corpvo = (CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class, " nvl(dr,0) =0 and unitcode = ?", sp);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
		return corpvo;
	}
	
	
	public boolean isDemoCorp(String unitcode,String pk_corp){
		
		CorpVO[] cpvos = getDemoCorpMsg();
		
		if(cpvos!=null && cpvos.length>0){
			
			if(cpvos[0].getUnitcode().equals(unitcode)
					|| cpvos[0].getPk_corp().equals(pk_corp)
					){
				return true;
			}
			
		}
		
		return false;
	}

//	public static String getProperValue(String filename, String column) {
//		InputStream in = null;
//		try {
//			Properties prop = new Properties();
//			in = AppQueryUtil.class.getResourceAsStream("/" + filename);
//			prop.load(in); /// 加载属性列表
//			String columnvalue = prop.getProperty(column);
//			return columnvalue;
//		} catch (Exception e) {
//			throw new WiseRunException(e);
//		}finally{
//			if(in!=null){
//				try {
//					in.close();
//				} catch (IOException e) {
//				}
//			}
//
//		}
//	}



	/**
	 * 获取客服信息
	 * 
	 * @return
	 * @throws DZFWarpException
	 */
	public List<UserVO> getKfUsercode() throws DZFWarpException {
		List<UserVO> kfuservos = null;
		AppConfig appConfig = SpringUtils.getBean(AppConfig.class);
		try {
			String user_code_kf = appConfig.chat_user_code_kf;//  prop.getProperty("user_code_kf");
			String corp_code_kf = appConfig.chat_corp_code_kf;// prop.getProperty("corp_code_kf");
			if (!StringUtil.isEmpty(user_code_kf) && !StringUtil.isEmpty(corp_code_kf)) {
				SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
				String[] usercodes = user_code_kf.split(",");
				StringBuffer qrysql = new StringBuffer();
				qrysql.append("  select sm.*,ue.imagepath as user_note from sm_user  sm ");
				qrysql.append("  inner join bd_corp bp on sm.pk_corp = bp.pk_corp ");
				qrysql.append("  left join ynt_user_ext ue on sm.cuserid = ue.cuserid  ");
				qrysql.append("    where nvl(sm.dr,0)=0  and nvl(ue.dr,0)=0 and "
						+ SqlUtil.buildSqlForIn("sm.user_code", usercodes));
				qrysql.append("  and bp.unitcode = ? ");
				SQLParameter sp = new SQLParameter();
				sp.addParam(corp_code_kf);
				kfuservos = (List<UserVO>) singleObjectBO.executeQuery(qrysql.toString(), sp,
						new BeanListProcessor(UserVO.class));
			}
		} catch (Exception e) {
			throw new WiseRunException(e);
		} finally{
		}
		return kfuservos;
	}

	public SubCopMsgBeanVO addDemoCorp(UserBeanVO userBean, String usercode, String user_name, String user_id)
			throws DZFWarpException {
		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		// 添加一个临时demo演示公司
		SubCopMsgBeanVO subbeandemo = new SubCopMsgBeanVO();
		CorpVO[] corpvo = AppQueryUtil.getInstance().getDemoCorpMsg();
		if (corpvo == null || corpvo.length == 0) {
			throw new BusinessException("演示公司为空!");
		}
		subbeandemo.setAccount(usercode);
		subbeandemo.setUser_name(user_name);
		subbeandemo.setIsys(IConstant.DEFAULT);
		subbeandemo.setCorpname(CodeUtils1.deCode(corpvo[0].getUnitname()));
		subbeandemo.setPk_corp(corpvo[0].getPk_corp());
		subbeandemo.setAccount_id(user_id);
		subbeandemo.setIsaccorp("N");
		subbeandemo.setUsergrade(IConstant.DEFAULT);
		subbeandemo.setBdata("Y");
		subbeandemo.setBaccount("Y");
		subbeandemo.setPriid(IConstant.PRIID_COM);// 临时主键信息
		subbeandemo.setIsdemo(IConstant.DEFAULT);
		subbeandemo.setRescode(IConstant.DEFAULT);
		subbeandemo.setIstate(String.valueOf(IConstant.TWO));
		subbeandemo.setAccfc(IConstant.FIRDES);//服务机构未封存
		if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(userBean.getSourcesys())){
			IAppPubservice apppubservice = (IAppPubservice) SpringUtils.getBean("apppubservice");
			List<TempUserRegVO> tlist = apppubservice.getTempList(usercode);
			if(tlist!=null && tlist.size()>0 && !StringUtil.isEmpty(tlist.get(0).getPk_svorg())){
				CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, tlist.get(0).getPk_svorg());
				subbeandemo.setAccname(CodeUtils1.deCode(cpvo.getUnitname()));// 会计公司名称
			}
		}else{
			CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, corpvo[0].getFathercorp());
			subbeandemo.setAccname(CodeUtils1.deCode( cpvo.getUnitname()));// 会计公司名称
		}
		return subbeandemo;
	}
	

	/**
	 * 获取服务机构的电话号码--短信验证码使用
	 * @param sourcesys
	 * @param account
	 * @return
	 */
	public static String getFwjgPhone(String sourcesys,String account,String qysbh,String msgcount){
		if(SourceSysEnum.SOURCE_SYS_CST.getValue().equals(sourcesys)){
			IAppPubservice apppubservice = (IAppPubservice) SpringUtils.getBean("apppubservice");
			List<TempUserRegVO> tlist = apppubservice.getTempList(account);
			SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
			AccountVO accountvo = null;
			if(tlist != null && tlist.size()>0){
				  accountvo = (AccountVO) singleObjectBO.queryByPrimaryKey(AccountVO.class, tlist.get(0).getPk_svorg());
			}else if(!StringUtil.isEmpty(qysbh)){
				SQLParameter sp = new SQLParameter();
				sp.addParam(qysbh.toUpperCase());
				AccountVO[] accountvos = (AccountVO[]) singleObjectBO.queryByCondition(AccountVO.class,
						"nvl(dr,0)=0 and def12 = ? ", sp);
				if (accountvos != null && accountvos.length > 0) {
					accountvo = accountvos[0];
				}
			}
			if(accountvo !=null && !StringUtil.isEmpty(accountvo.getPhone1())){
				return msgcount+CodeUtils1.deCode(accountvo.getPhone1());
			}else{
				return "";
			}
		}else if(SourceSysEnum.SOURCE_SYS_WX_APPLET.getValue().equals(sourcesys)
				&& !StringUtil.isEmpty(qysbh)){ //企业识别号
			SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
			SQLParameter sp = new SQLParameter();
			sp.addParam(qysbh.toUpperCase());
			AccountVO[] accountvos = (AccountVO[]) singleObjectBO.queryByCondition(AccountVO.class,
					"nvl(dr,0)=0 and def12 = ? ", sp);
			if (accountvos != null && accountvos.length > 0) {
				if(!StringUtil.isEmpty(accountvos[0].getDef2())){
					return msgcount+accountvos[0].getDef2();
				}
			}
			
		}
		return msgcount + IConstant.DEFAULTPHONE;
		
	}
	
	public static String getDefaultAppValue(String sourcesys){//获取系统名称
		String value ="";
		if(StringUtil.isEmpty(sourcesys)){
			return SourceSysEnum.SOURCE_SYS_DZF.getName();
		}
		for(SourceSysEnum valueenum:SourceSysEnum.values()){
			if(sourcesys.equals(valueenum.getValue())){
				value = valueenum.getName();
			}
		}
		return value;
	}
	
	public static String getCurDate() {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		return format.format(Calendar.getInstance().getTime());
	}
}

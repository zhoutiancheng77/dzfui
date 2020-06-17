package com.dzf.zxkj.app.controller;

import com.dzf.auth.api.result.Result;
import com.dzf.auth.api.service.IUserCenterService;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import org.apache.dubbo.config.annotation.Reference;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 大账房app基础
 * 
 */

public class BaseAppController  {
    @Autowired
    private IUserPubService userPubService;
	@Reference(version = "1.0.1", protocol = "dubbo", timeout = 1000)
	private  IUserCenterService userCenterService;


    public UserVO queryUserVOId(String account_id){
		UserVO userVO = userPubService.queryUserVOId(account_id);
		//用户存在则查询出来不存在则新建
		if(userVO!=null) return userVO;
		//1:查出用户中心账户信息
		Result<com.dzf.auth.api.model.user.UserVO>  result =userCenterService.getUserDetailById("zxkj", new Long(account_id));
        //2:查出是否存在相同用户
         userVO  = userPubService.queryUserVObyCode(result.getData().getLoginName());
         if(userVO !=null){
             userVO.setUnifiedid(account_id);
             userPubService.updateUserUnifiedid(userVO);
             return userVO;
         }
		com.dzf.auth.api.model.user.UserVO uvo = result.getData();
         //3:新建用户
		UserBeanVO beanVO = new UserBeanVO();
		beanVO.setUsercode(uvo.getLoginName());
		beanVO.setPhone(uvo.getMobile());
		beanVO.setUsername(uvo.getUserName());
        beanVO.setPassword("qwe123!@#");
		return userPubService.saveRegisterCorpSWtch(beanVO,account_id);
    }

	public void printErrorJson(ResponseBaseBeanVO bean, Throwable e, Logger log, String errormsg) {
		if(bean == null){ 
			bean = new ResponseBaseBeanVO();
		}
		log.error(errormsg, log);
		bean.setRescode(IConstant.FIRDES);
		if (e instanceof BusinessException) {
			bean.setResmsg(e.getMessage());
		} else {
			if (StringUtil.isEmpty(errormsg)) {
				bean.setResmsg("处理失败!");
			} else {
				bean.setResmsg(errormsg);
			}
		}
	}

//	/**
//	 * 生成图片http路径
//	 */
//	protected String createImageHttpath() {
//		HttpServletRequest request = getRequest();
//		String path = request.getContextPath();
//		String servername = request.getServerName();
//		if ("localhost".equals(servername) || "127.0.0.1".equals(servername)) {
//			servername = getLocalIP();
//		}
//		String basePath = request.getScheme() + "://" + servername + ":" + request.getServerPort() + path;
//		return basePath;
//	}
//
//	// 获取服务器地址(即本地)
//	protected String getLocalIP() {
//		InetAddress addr = null;
//		try {
//			addr = InetAddress.getLocalHost();
//		} catch (UnknownHostException e) {
//		}
//		byte[] ipAddr = addr != null ? addr.getAddress() : null;
//		String ipAddrStr = "";
//		if(ipAddr!=null && ipAddr.length>0){
//			for (int i = 0; i < ipAddr.length; i++) {
//				if (i > 0) {
//					ipAddrStr += ".";
//				}
//				ipAddrStr += ipAddr[i] & 0xFF;
//			}
//		}
//		return ipAddrStr;
//	}
//
//	/**
//	 * 必须要继承userbeanvo
//	 * @param power
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public  SuperVO getUserBeanAndValidate(String power,Class classvalue) throws DZFWarpException {
//
//		SuperVO userbean = null;
//
//		IAppPubservice apppubservice  = (IAppPubservice) SpringUtils.getBean("apppubservice");
//
//		try {
//			userbean = (SuperVO) DzfTypeUtils.cast(getRequest(), (SuperVO)classvalue.newInstance());
//		} catch (Exception e) {
//			throw new WiseRunException(e);
//		}
//
//		IAppUserService iaus = (IAppUserService) SpringUtils.getBean("userservice");
//
//		CorpVO[] corpvos = AppQueryUtil.getInstance().getDemoCorpMsg();
//		// 判断是否demo公司
//		String demoname = null;
//		if (corpvos != null && corpvos.length > 0) {
//			if (!StringUtil.isEmpty(corpvos[0].getUnitname())) {
//				demoname = CodeUtils1.deCode(corpvos[0].getUnitname());
//			}
//		}
//		String account = (String) userbean.getAttributeValue("account");
//		String account_id = (String) userbean.getAttributeValue("account_id");
//		String pk_corp = (String) userbean.getAttributeValue("pk_corp");
//		String pk_tempcorp = (String) userbean.getAttributeValue("pk_tempcorp");
//		String corpname  = apppubservice.getCorpName(pk_corp, pk_tempcorp);
//		// 非演示公司 才有账号停用校验
//		if (demoname == null
//				|| (!StringUtil.isEmpty(corpname) && !demoname.equals(corpname))) {
//			if (iaus.getPrivilege(power, account,account_id, pk_corp, pk_tempcorp)) {
//				throw new BusinessException("您没该权限，请联系管理员!");
//			}
//		}
//
//
//		Integer versionno = (Integer) userbean.getAttributeValue("versionno");
//		if (versionno == null || versionno.intValue() == 0) {
//			throw new BusinessException("您当前版本出问题，请更新最新版本!");
//		}
//
//		return userbean;
//	}
//
//	public void saveLog(UserBeanVO userBean) {
//		userBean.setOptype("0-" + userBean.getSystype() + "-" + userBean.getOperate());
//		ILog lo = (ILog) SpringUtils.getBean("applog");
//		lo.savelog(userBean);
//	}
}

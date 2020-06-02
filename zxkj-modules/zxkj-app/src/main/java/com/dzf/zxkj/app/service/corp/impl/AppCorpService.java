package com.dzf.zxkj.app.service.corp.impl;


import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

public abstract class AppCorpService  {

	public LoginResponseBeanVO logingGetCorpVOs(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public ResponseBaseBeanVO updateuserAndCorpRelation(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public RegisterRespBeanVO corpAddMsg(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public ResponseBaseBeanVO userAddCorpExamine(UserBeanVO userBean) {
		return null;
	}

	public String[] getPk_temp_corpByName(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public String[] getPk_corpByName(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public CorpVO[] getPk_corpVoByName(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public String genTempCorpMsg252(UserBeanVO userBean, String pk_corp, SingleObjectBO sbo) throws DZFWarpException {
		return null;
	}

	public void genTempUser(UserBeanVO userBean, SingleObjectBO sbo, String pk_tempcorp, String pk_corp)
			throws DZFWarpException {
	}

	public CorpVO[] getPk_corpandAccountByName(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public UserVO isExistManage(String pk_corp, String pk_tempcorp, String account_id) throws DZFWarpException {
		return null;
	}

	public String isLinkCorp(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public ResponseBaseBeanVO getFwPjValues() throws DZFWarpException {
		return null;
	}

	public RegisterRespBeanVO updateUserTel(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public ResponseBaseBeanVO updateuserAddCorp(UserBeanVO userBean) throws DZFWarpException {
		return null;
	}

	public void updateAddCorpFromActiveCode(UserBeanVO userBean) throws DZFWarpException {

	}
	
	public void saveKpmsg(String pk_corp,String pk_temp_corp,String account_id,
			String corpname, String sh,  String gsdz, String kpdh, String khh, String khzh,String grdh,String gryx)
			throws DZFWarpException {
		
	}
	
	public void saveConfirmApply(String id, 
			String confirm, 
			String msgtype) throws DZFWarpException {
	}
	
	public ResponseBaseBeanVO qrykpmsg(String pk_corp, String pk_temp_corp, String account_id,String account)
			throws DZFWarpException {
		return null;
	}
	
	public UserBeanVO saveUserFromInvite(UserBeanVO userbean,Integer repeat_tips) throws DZFWarpException {
		return null;
	} 


}

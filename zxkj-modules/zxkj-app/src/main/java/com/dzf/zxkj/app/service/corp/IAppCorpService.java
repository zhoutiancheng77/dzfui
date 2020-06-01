package com.dzf.zxkj.app.service.corp;


import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;

/**
 * app公司维护 
 * 
 * @author zhangj
 *
 */
public interface IAppCorpService {

	/**
	 * 分配公司与用户关系
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO updateuserAndCorpRelation(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 公司信息上传
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public RegisterRespBeanVO corpAddMsg(UserBeanVO userBean) throws DZFWarpException;


	
	/**
	 * 添加公司版本检查
	 * @param userBean
	 * @return
	 */
	public ResponseBaseBeanVO userAddCorpExamine(UserBeanVO userBean);

	
	/**
	 * 根据名称获取临时公司id信息
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] getPk_temp_corpByName(UserBeanVO userBean) throws DZFWarpException;

	
	/**
	 * 根据名称获取正式公司id信息
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public String[] getPk_corpByName(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 根据公司名称获取正式公司vo
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO[] getPk_corpVoByName(UserBeanVO userBean) throws DZFWarpException;

	
	/**
	 * 生成临时公司(根据加密的公司名称)
	 * @param userBean
	 * @param sbo
	 * @return
	 * @throws DZFWarpException
	 */
	public String genTempCorpMsg252(UserBeanVO userBean, String pk_corp, SingleObjectBO sbo) throws DZFWarpException;
	
	/**
	 * 生成临时用户信息
	 * @param userBean
	 * @param sbo
	 * @param pk_tempcorp
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public void genTempUser(UserBeanVO userBean, SingleObjectBO sbo, String pk_tempcorp, String pk_corp)
			throws DZFWarpException;

	
	/**
	 * 获取尚未签约的代账公司
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO[] getPk_corpandAccountByName(UserBeanVO userBean) throws DZFWarpException;

	/**
	 * 当前公司是否已经存在管理员
	 * 
	 * @param pk_corp
	 * @param pk_tempcorp
	 * @param account_id
	 * @return
	 * @throws DZFWarpException
	 */
	public UserVO isExistManage(String pk_corp, String pk_tempcorp, String account_id) throws DZFWarpException;

	/**
	 * 判断当前公司是否已经关联过
	 * 
	 * @param userBean
	 * @throws DZFWarpException
	 */
	public String isLinkCorp(UserBeanVO userBean) throws DZFWarpException;

	
	/**
	 * 获取服务评价信息
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO getFwPjValues() throws DZFWarpException;
	
	
	/**
	 * 修改手机号码
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public RegisterRespBeanVO updateUserTel(UserBeanVO userBean) throws DZFWarpException;// 
	

	/**
	 * 用户添加公司
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO updateuserAddCorp(UserBeanVO userBean) throws DZFWarpException; 

	
	/**
	 * 通过激活码关联公司
	 * @param userBean
	 * @throws DZFWarpException
	 */
	public void updateAddCorpFromActiveCode(UserBeanVO userBean) throws DZFWarpException;
	
	
	/**
	 * 保存公司开票信息数据
	 * @throws DZFWarpException
	 */
	public void saveKpmsg(String pk_corp,String pk_temp_corp,String account_id,
			String corpname, String sh,  String gsdz, String kpdh, String khh, String khzh,String grdh,String gryx) throws DZFWarpException;
	
	/**
	 * 申请开通服务
	 * @throws DZFWarpException
	 */
	public void saveConfirmApply(String id, String confirm, String msgtype) throws DZFWarpException;

	
	/**
	 * 获取公司开票信息数据
	 * @param pk_corp
	 * @param pk_temp_corp
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qrykpmsg(String pk_corp,String pk_temp_corp,String account_id,String account) throws DZFWarpException;
	
	
	/**
	 * 通过邀请短信获取信息
	 * @param userbean
	 * @param repeat_tips 重复提醒0 提醒，1 不提醒
	 * @return
	 * @throws DZFWarpException
	 */
	public UserBeanVO saveUserFromInvite(UserBeanVO userbean,Integer repeat_tips) throws DZFWarpException;
}

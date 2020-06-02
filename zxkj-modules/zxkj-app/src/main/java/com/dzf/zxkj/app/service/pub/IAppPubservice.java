package com.dzf.zxkj.app.service.pub;

import com.dzf.zxkj.app.model.app.corp.UserToCorp;
import com.dzf.zxkj.app.model.app.remote.AppCorpCtrlVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;


/**
 * app公共接口
 * @author zhangj
 *
 */
public interface IAppPubservice {

	/**
	 * 用户登录密码的加密和解密过程
	 * 
	 * @param sysType
	 * @param password
	 * @return
	 * @throws Exception
	 */
	public String decryptPwd(String sysType, String password) throws DZFWarpException;

	/**
	 * 查询区域信息
	 * 
	 * @param keyvalue
	 *            0 代码key为主键 1代表name为主键
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> queryArea(String keyvalue) throws DZFWarpException;

	/**
	 * 获取用户名字(根据用户id)
	 * 
	 * @param account_id
	 * @return
	 * @throws DZFWarpException
	 */
	public String getUserName(String account_id) throws DZFWarpException;

	/**
	 * 获取公司名称(根据公司id)
	 * 
	 * @param pk_corp
	 * @param pk_temp_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public String getCorpName(String pk_corp, String pk_temp_corp) throws DZFWarpException;

	/**
	 * 获取当前用户所有的公司(根据用户手机号)
	 * 
	 * @param account
	 * @return
	 * @throws DZFWarpException
	 */
	public List<UserToCorp> getUserCorp(String account, String account_id) throws DZFWarpException;

	/**
	 * 根据代账公司id+公司名称，获取公司id信息
	 * 
	 * @param corpname
	 * @param admincorpid
	 * @param power
	 *            0 全部权限 1上传图片，2 填制凭证， null 代表不需要权限
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> getCorpid(String[] corpname, String admincorpid, String account_id, Integer power)
			throws DZFWarpException;

	/**
	 * 获取当前用户尚未关联的公司
	 * 
	 * @param account
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpVO[] getNoLinkCorp(String account, String sourcesys) throws DZFWarpException;

	/**
	 * 获取有权限的用户信息
	 * 
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<String> getUserForPowerCorp(String pk_corp, String funpk) throws DZFWarpException;

	/**
	 * 获取参数设置(是否是会计公司生成)
	 */
	public boolean isParamSysCreatePZ(String pk_corp) throws DZFWarpException;

	/**
	 * 获取参数
	 * 
	 * @param pk_corp
	 * @param paramcode
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer qryParamValue(String pk_corp, String paramcode) throws DZFWarpException;

	/**
	 * 查询小客户的权限
	 * 
	 * @param pk_corp
	 * @param cuserid
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, AppCorpCtrlVO> queryCorpCtrl(String pk_corp, String cuserid) throws DZFWarpException;

	/**
	 * 该用户在公司是否是管理员
	 * 
	 * @param pk_corp
	 * @param pk_temp_corp
	 * @param account_id
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean isManageUserInCorp(String pk_corp, String pk_temp_corp, String account_id) throws DZFWarpException;
	
	
	/**
	 * 查找公司管理员
	 * @param pk_corps
	 * @param pk_temp_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,String> getManageUserFromCorp(String[] pk_corps,String[] pk_temp_corp) throws DZFWarpException;

	
	/**
	 * 当前用户是否存在
	 * @param user_code
	 * @return
	 * @throws DZFWarpException
	 */
	public boolean isExistUser(String user_code) throws DZFWarpException;

	
	/**
	 * 获取未签约的用户
	 * @param user_code
	 * @return
	 * @throws DZFWarpException
	 */
	public List<TempUserRegVO> getTempList(String user_code) throws DZFWarpException;

}

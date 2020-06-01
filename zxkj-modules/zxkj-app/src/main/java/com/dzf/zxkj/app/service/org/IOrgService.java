package com.dzf.zxkj.app.service.org;


import com.dzf.zxkj.app.model.resp.bean.OrgRespBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;

public interface IOrgService {

	
	/**
	 * 获取服务机构
	 * @param userBean
	 * @param bfwwd
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qrySvorgLs(UserBeanVO userBean, DZFBoolean bfwwd) throws DZFWarpException;

	/**
	 * 保存签约机构
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO saveSvOrgSetting(UserBeanVO userBean, OrgRespBean[] userBeanLs) throws DZFWarpException;


	/**
	 * 查询选择的服务机构列表
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO qrySignOrg(UserBeanVO userBean) throws DZFWarpException;


	/**
	 * 确认签约
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO updateconfirmSignOrg(UserBeanVO userBean) throws DZFWarpException;
}

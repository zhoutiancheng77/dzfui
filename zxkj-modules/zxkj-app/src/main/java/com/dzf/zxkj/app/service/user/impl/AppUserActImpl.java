package com.dzf.zxkj.app.service.user.impl;

import java.util.ArrayList;

import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.app.user.TempUserVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.user.IAppUserActService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.platform.model.sys.RoleVO;
import com.dzf.zxkj.platform.model.sys.UserRoleVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.springframework.stereotype.Service;


@Service("auaservice")
public class AppUserActImpl implements IAppUserActService {



	public String saveTempCorp(TempCorpVO corpVO) throws DZFWarpException {
		String pk=null;
		try{
			SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, IGlobalConstants.DefaultGroup));
			pk=sbo.insertVOWithPK(corpVO);
		}catch (Exception e) {
			throw new WiseRunException(e);
		}
		return pk;
	}


	public String saveTempUser(TempUserVO userVO) throws DZFWarpException {
		String pk=null;
		try{
			SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null,userVO.getPk_corp()));
			sbo.insertVOWithPK(userVO);
		}catch (Exception e) {
			throw new WiseRunException(e);
		}
		return pk;
	}


	public int approveUser(String userID,String pk_corp,String pk_temp_corp,String bdata,String baccount,String bbillapply) throws DZFWarpException {
		UserVO userVO = new UserVO();
		userVO.setPk_corp(pk_corp);
		userVO.setPrimaryKey(userID);
		SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null,pk_corp));
		if(!Common.tempidcreate.equals(pk_corp)){
			String sql = " update ynt_corp_user set istate=?,bdata=?,baccount = ?,bbillapply = ?  where pk_user=? and pk_corp = ? ";
			SQLParameter sp=new SQLParameter();
			sp.addParam(IConstant.TWO);
			sp.addParam(bdata==null?"Y":bdata);
			sp.addParam(baccount==null?"Y":baccount);
			sp.addParam(bbillapply ==null? "Y":bbillapply);
			sp.addParam(userID);
			sp.addParam(pk_corp);
			int dor = sbo.executeUpdate(sql,sp);
			return dor;
		}else{
			String sql = " update ynt_corp_user set istate=?, bdata=?,baccount = ?,bbillapply = ? where pk_user=? and pk_tempcorp = ? ";
			SQLParameter sp=new SQLParameter();
			sp.addParam(IConstant.TWO);
			sp.addParam(bdata==null?"Y":bdata);
			sp.addParam(baccount==null?"Y":baccount);
			sp.addParam(bbillapply == null ? "Y":bbillapply);
			sp.addParam(userID);
			sp.addParam(pk_temp_corp);
			int dor = sbo.executeUpdate(sql,sp);
			return dor;
		}
	}

	public int cancelApproveUser(UserBeanVO userBean) throws DZFWarpException {
		SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null,userBean.getPk_corp()));
		String sql = "delete from sm_user where cuserid=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(userBean.getUserid());
		int dor = sbo.executeUpdate(sql,sp);
		if(dor>0){
			return 0;
		}
		return 1;
	}
	/**
	 * ���û���ӽ�ɫ
	 * @param userVO
	 * @throws BusinessException
	 */
	private void addRole(UserVO userVO) throws DZFWarpException {
		RoleVO defaultRoleVO = new RoleVO();
		defaultRoleVO.setRole_code(userVO.getPk_corp());   // Ĭ�Ͻ�ɫ�ı��Ϊpk_corp
		SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null,userVO.getPk_corp()));
		SQLParameter sp=new SQLParameter();
		sp.addParam(userVO.getPk_corp());
		RoleVO[] roleVOs = (RoleVO[])sbo.queryByCondition(RoleVO.class, "role_code=?",sp);
		if(roleVOs != null && roleVOs.length > 0){
			defaultRoleVO = roleVOs[0];
			UserRoleVO[] addRoles = castToUserRoleVOs(new RoleVO[] { defaultRoleVO }, userVO.getPrimaryKey());
			sbo.insertVOWithPK(addRoles[0]);
		}
	}

	public UserRoleVO[] castToUserRoleVOs(RoleVO[] roles, String userPK) throws DZFWarpException {
		try {
			ArrayList list = new ArrayList();
			for (int i = 0; i < roles.length; i++) {
				UserRoleVO urVO = new UserRoleVO();
				urVO.setPk_role(roles[i].getPrimaryKey());
				urVO.setPk_corp(roles[i].getPk_corp());
				urVO.setCuserid(userPK);
				list.add(urVO);
			}
			return (UserRoleVO[]) list.toArray(new UserRoleVO[list.size()]);
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}



	public int saveSvOrg(UserBeanVO userBean) throws DZFWarpException{

		return IConstant.ONE;
	}

	public String saveUser(AppUserVO userVO) throws DZFWarpException {
		validateUserVO(userVO); 
		SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null,userVO.getPk_corp()));
		String sql="update sm_user set dr=0 where (UPPER(checkcode)=? or UPPER(user_code)=?) and nvl(dr,0)=0";
		SQLParameter sp=new SQLParameter();
		sp.addParam(userVO.getCheckcode());
		sp.addParam(userVO.getUser_code());
		int r=sbo.executeUpdate(sql, sp);
		if (r>0)
			throw new BusinessException("更新失败,请联系客服查询!");
		
		return sbo.insertVOWithPK(userVO);
	}
	private void validateUserVO(AppUserVO vo) throws DZFWarpException {
		boolean isInValid = false;
		StringBuffer eInfo = new StringBuffer();
		if (vo.getUser_code() == null || vo.getUser_code().trim().length() == 0) {
			eInfo.append("用户编码不能为空\n");
			isInValid = true;
		}
		if (vo.getCheckcode() == null || vo.getCheckcode().trim().length() == 0) {
			eInfo.append("��֤�벻��Ϊ��\n");
			isInValid = true;
		}else if(vo.getCheckcode().length() < 6){
			eInfo.append("��֤�볤�Ȳ���С��6λ\n");
			isInValid = true;
		}
		if (vo.getUser_name() == null || vo.getUser_name().trim().length() == 0) {
			eInfo.append("�û�����Ϊ��\n");
			isInValid = true;
		}
		if (vo.getAble_time() == null) {
			eInfo.append("��Ч���ڲ���Ϊ��\n");
			isInValid = true;
		}
		if (vo.getAble_time() != null && vo.getDisable_time() != null) {
			if (vo.getAble_time().after(vo.getDisable_time())) {
				eInfo.append("ʧЧ���ڲ�������Ч����֮ǰ\n");
				isInValid = true;
			}
		}
		if (isInValid)
			throw new BusinessException(eInfo.toString());

	}
}

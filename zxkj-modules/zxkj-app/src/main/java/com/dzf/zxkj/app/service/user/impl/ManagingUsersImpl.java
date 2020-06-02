package com.dzf.zxkj.app.service.user.impl;

import java.util.List;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.service.user.IManagingUsers;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
/**
 * 人员管理
 * @author zhangj
 *
 */
@Slf4j
@Service("mguser")
public class ManagingUsersImpl implements IManagingUsers {

	private SingleObjectBO sbo;

	
//	@Autowired
//	private IAppPubservice apppubservice;

	public List<AppUserVO> getUserlist(UserBeanVO userBean, DZFBoolean isself) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.user_name,a.user_code,a.cuserid,a.app_user_mail,a.app_user_qq");
		sql.append(" ,a.app_user_tel,b.istate ,nvl(b.bdata,'N') as bdata,nvl(b.baccount,'N') as baccount,nvl(b.bbillapply,'N') as bbillapply ,b.ismanage as ismanager,b.iaudituser ");
		sql.append(" from sm_user a inner join ynt_corp_user b  on a.cuserid = b.pk_user ");
		sql.append("   where nvl(a.dr,0)=0 and nvl(b.dr,0)=0  ");

		SQLParameter sp = new SQLParameter();
		if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
			sql.append("  and b.pk_corp =?");
			sp.addParam(userBean.getPk_corp());
		} else {
			if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
				sql.append(" and b.pk_tempcorp=?   ");
				sp.addParam(userBean.getPk_tempcorp());
			}
		}
		if(userBean.getIstates()!=null){
			if(userBean.getIaudituser()!=null && userBean.getIaudituser().booleanValue()){
				sql.append(" and ( b.istate= ?  or b.iaudituser= 'Y'  )");
			}else{
				sql.append(" and b.istate= ? ");
			}
			sp.addParam(userBean.getIstates());
		}
		List<AppUserVO> userVOslisttemp = (List<AppUserVO>) sbo.executeQuery(sql.toString(), sp, new BeanListProcessor(AppUserVO.class));

		return userVOslisttemp;
	}

//	public ResponseBaseBeanVO qryUserLs(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		try {
//			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1 && userBean.getVersionno()<IVersionConstant.VERSIONNO320) {// 第一版走的代码
//				bean =  qryUserLs1(userBean,DZFBoolean.TRUE);
//			}else if(userBean.getVersionno().intValue()>= IVersionConstant.VERSIONNO320){
//				userBean.setIstates("2");
//				bean =  qryUserLs1(userBean, DZFBoolean.TRUE);
//			}
//		} catch (Exception e) {
//			bean.setRescode(IConstant.FIRDES);
//			if( e instanceof BusinessException){
//				bean.setResmsg(e.getMessage());
//			}else{
//				bean.setResmsg("查询用户信息出错！");
//			}
//		}
//		return bean;
//
//	}
//
//	/**
//	 *
//	 * @param userBean
//	 * @param isself 是否包含自己 Y 不包含自己，N 包含自己
//	 * @return
//	 */
//	private ResponseBaseBeanVO qryUserLs1(UserBeanVO userBean,DZFBoolean isself) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		SQLParameter sp  = new SQLParameter();
//
//		AppCheckValidUtils.isEmptyWithCorp(userBean.getPk_corp(), userBean.getPk_tempcorp(),null);
//
//		List<AppUserVO> userVOslisttemp = getUserlist(userBean,isself);
//
//
//		bean.setRescode(IConstant.DEFAULT);
//		//不是管理员的不让显示，待审核状态的用户
//		List<AppUserVO> userVOslist = filterUserVO(userVOslisttemp,userBean.getAccount_id(),isself);
//
//		AppUserVO[] userVOs = userVOslist.toArray(new AppUserVO[0]);
//		if (userVOs != null && userVOs.length > 0) {
//			UserLsBean[] ubs=UserUtil.setUserLsBean( userVOs);
//			bean.setResmsg(ubs);
//		} else {
//			//如果没公司信息，也能查询用户信息
//			if( ( !AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp()))
//					|| !StringUtil.isEmpty(userBean.getPk_tempcorp())){
//				bean.setRescode(IConstant.FIRDES);
//				bean.setResmsg("暂无人员");
//			}else{
//				StringBuffer usersql =new StringBuffer();
//				usersql.append("select * from app_temp_user where nvl(dr,0)=0 and user_code= ? ");
//				sp.clearParams();
//				sp.addParam(userBean.getAccount());
//				if(isself!=null && !isself.booleanValue()){//不包含自己
//					if(!StringUtil.isEmpty(userBean.getAccount_id())){
//						usersql.append(" and pk_user != ?");
//						sp.addParam(userBean.getAccount_id());
//					}
//				}
//				List<AppUserVO> usertempVOslist = (List<AppUserVO>) sbo.executeQuery(usersql.toString(), sp,
//						new BeanListProcessor(AppUserVO.class));
//				if (usertempVOslist != null && usertempVOslist.size() > 0) {
//					UserLsBean[] ubs = new UserLsBean[1];
//					ubs[0] = new UserLsBean();
//					ubs[0].setUsername(usertempVOslist.get(0).getUser_name());
//					ubs[0].setUsercode(usertempVOslist.get(0).getUser_code());
//					ubs[0].setPhotopath(UserUtil.getHeadPhotoPath(usertempVOslist.get(0).getUser_code(),0));
//					bean.setResmsg(ubs);
//				} else {
//					bean.setRescode(IConstant.FIRDES);
//					bean.setResmsg("暂无人员");
//				}
//			}
//		}
//		return bean;
//	}
//
//
//
//	/**
//	 * 过滤
//	 * @param userVOslisttemp
//	 * @param account_id
//	 * @param isself
//	 */
//	private List<AppUserVO>  filterUserVO(List<AppUserVO> userVOslisttemp, String account_id, DZFBoolean isself) {
//		List<AppUserVO> userVOslist = new ArrayList<AppUserVO>();
//
//		DZFBoolean ismanage = DZFBoolean.FALSE;
//
//		for(AppUserVO uvo:userVOslisttemp){
//			if(uvo.getCuserid().equals(account_id)){
//				if(uvo.getIsmanager()!=null && uvo.getIsmanager().booleanValue()){
//					ismanage = DZFBoolean.TRUE;
//					break;
//				}
//			}
//		}
//
//		for(AppUserVO uvo:userVOslisttemp){
//			if(uvo.getCuserid().equals(account_id) && isself!=null && !isself.booleanValue()){//isself是501走的代码，如果没审核通过不显示
//				continue;
//			}
//
//			if(isself!=null && !isself.booleanValue() && uvo.getIstate() !=null &&uvo.getIstate().intValue() !=2){//isself是501走的代码，如果没审核通过不显示
//				continue;
//			}
//
//			if(!ismanage.booleanValue() && uvo.getIstate() != 2){
//				continue;
//			}
//			userVOslist.add(uvo);
//		}
//		return userVOslist;
//	}
//
//	public ResponseBaseBeanVO saveapprove(UserBeanVO userBean) {
//
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		if (userBean.getVersionno().intValue()>= IVersionConstant.VERSIONNO1 && userBean.getVersionno().intValue() < IVersionConstant.VERSIONNO311) {// 第一版走的代码
//			bean = saveApproveSwitch1(userBean);
//		}else if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO311) {//参数不一样userid ~ accountid
//			bean = saveApprove311(userBean);
//		}
//		return bean;
//
//	}
//
//	private ResponseBaseBeanVO saveApproveSwitch1(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		IAppUserActService iaas = (IAppUserActService) SpringUtils.getBean("auaservice");
//		int approve = iaas.approveUser(userBean.getAccount_id(), userBean.getPk_corp(),
//				userBean.getPk_tempcorp(),userBean.getBdata(),userBean.getBaccount(),userBean.getBbillapply());
//		if (approve > 0) {
//			bean.setRescode(IConstant.DEFAULT);
//			bean.setResmsg("审核成功");
//		} else {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("审核失败");
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO saveApprove311(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		IAppUserActService iaas = (IAppUserActService) SpringUtils.getBean("auaservice");
//		int approve = iaas.approveUser(userBean.getUserid(), userBean.getPk_corp(), userBean.getPk_tempcorp(),
//				userBean.getBdata(),userBean.getBaccount(),userBean.getBbillapply());
//		if (approve > 0) {
//			bean.setRescode(IConstant.DEFAULT);
//			bean.setResmsg("审核成功!");
//		} else {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("审核失败!");
//		}
//
//		return bean;
//	}
//
//	public ResponseBaseBeanVO adjustPrivilege(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		try {
//			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
//				bean = adjustPrivilege1(userBean);
//			}
//		} catch (Exception e) {
//			bean.setRescode(IConstant.FIRDES);
//			if(e instanceof BusinessException){
//				bean.setResmsg(e.getMessage());
//			}else{
//				bean.setResmsg("获取权限用户出错！");
//			}
//			log.error("错误",e);
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO adjustPrivilege1(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//
//		AppUserVO userVO = (AppUserVO) sbo.queryVOByID(userBean.getUserid(), AppUserVO.class);
//
//		bean.setBdata("N");
//		bean.setBaccount("N");
//
//		if (userVO.getBdata() != null && userVO.getBdata().booleanValue()) {
//			bean.setBdata("Y");
//		}
//		if (userVO.getBaccount() != null && userVO.getBaccount().booleanValue()) {
//			bean.setBaccount("Y");
//		}
//
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg("获取数据成功");
//
//		return bean;
//	}
//
//	public ResponseBaseBeanVO updateassignPrivilege(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1 && userBean.getVersionno().intValue() < IVersionConstant.VERSIONNO311) {// 第一版走的代码
//			bean = updateassignPrivilege1(userBean);
//		} else if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO311) {
//			bean = updateassignPrivilege311(userBean);
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO updateassignPrivilege311(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		// 管理员角色
//		StringBuffer upsql;
//		String ismanage = userBean.getIsmanage();
//		SQLParameter sp = new SQLParameter();
//		if (ismanage != null && "Y".equals(ismanage)) {
//			StringBuffer qrysql = new StringBuffer();
//			qrysql.append("select * from ynt_corp_user ");
//			qrysql.append(" where  nvl(ismanage,'N')='Y'  and nvl(dr,0)=0 ");
//			if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
//				qrysql.append(" and pk_corp =?");
//				sp.addParam(userBean.getPk_corp());
//			} else {
//				if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//					qrysql.append(" and pk_tempcorp =?");
//					sp.addParam(userBean.getPk_tempcorp());
//				}
//			}
//
//			List<UserToCorp> userlist = (List<UserToCorp>) sbo.executeQuery(qrysql.toString(), sp,
//					new BeanListProcessor(UserToCorp.class));
//			if (userlist != null && userlist.size() > 0) {
//				userlist.get(0).setIsmanage(DZFBoolean.FALSE);
//				sbo.update(userlist.get(0));
//			}
//			upsql = new StringBuffer();
//			upsql.append("update  ynt_corp_user set ismanage='Y' , bdata = ? , baccount = ?,bbillapply = ? ");
//			upsql.append(" WHERE pk_user=? and nvl(dr,0)=0");
//			if (!StringUtil.isEmpty(userBean.getPk_corp()) && !"appuse".equals(userBean.getPk_corp())) {
//				upsql.append(" and pk_corp =? ");
//			} else {
//				if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//					upsql.append(" and pk_tempcorp =?  ");
//				}
//			}
//		} else {
//			upsql = new StringBuffer();
//			upsql.append("update  ynt_corp_user set  bdata = ? , baccount = ?,bbillapply = ?  WHERE  pk_user=?  and nvl(dr,0)=0");
//			if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
//				upsql.append(" and pk_corp =? ");
//			} else {
//				if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//					upsql.append(" and pk_tempcorp =?  ");
//				}
//			}
//		}
//		// 普通角色
//		sp.clearParams();
//		if (userBean.getBdata() != null && "Y".equals(userBean.getBdata())) {
//			sp.addParam("Y");
//		} else {
//			sp.addParam("N");
//		}
//		if (userBean.getBaccount() != null && "Y".equals(userBean.getBaccount())) {
//			sp.addParam("Y");
//		} else {
//			sp.addParam("N");
//		}
//		if (userBean.getBbillapply() != null && "Y".equals(userBean.getBbillapply())) {
//			sp.addParam("Y");
//		} else {
//			sp.addParam("N");
//		}
//		sp.addParam(userBean.getUserid());
//		if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
//			sp.addParam(userBean.getPk_corp());
//		} else {
//			if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//				sp.addParam(userBean.getPk_tempcorp());
//			}
//		}
//		sbo.executeUpdate(upsql.toString(), sp);
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg("分配权限成功");
//		return bean;
//	}
//
//	private ResponseBaseBeanVO updateassignPrivilege1(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		AppCheckValidUtils.isEmptyWithCorp(userBean.getPk_corp(), userBean.getPk_tempcorp(),null);
//		// 管理员角色
//		StringBuffer upsql;
//		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_corp()));
//		String ismanage = userBean.getIsmanage();
//		SQLParameter sp = new SQLParameter();
//		if (ismanage != null && "Y".equals(ismanage)) {
//			StringBuffer qrysql = new StringBuffer();
//			qrysql.append("select * from ynt_corp_user ");
//			qrysql.append(" where  nvl(ismanage,'N')='Y'");
//			if (!StringUtil.isEmpty(userBean.getPk_corp()) && !Common.tempidcreate.equals(userBean.getPk_corp())) {
//				qrysql.append(" and pk_corp =?");
//				sp.addParam(userBean.getPk_corp());
//			} else {
//				if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//					qrysql.append(" and pk_tempcorp =?");
//					sp.addParam(userBean.getPk_tempcorp());
//				}
//			}
//
//			List<UserToCorp> userlist = (List<UserToCorp>) sbo.executeQuery(qrysql.toString(), sp,
//					new BeanListProcessor(UserToCorp.class));
//			if (userlist != null && userlist.size() > 0) {
//				userlist.get(0).setIsmanage(DZFBoolean.FALSE);
//				sbo.update(userlist.get(0));
//			}
//			upsql = new StringBuffer();
//			upsql.append("update  ynt_corp_user set ismanage='Y' , bdata = ? , baccount = ? ");
//			upsql.append(" WHERE pk_user=?");
//			if (!StringUtil.isEmpty(userBean.getPk_corp()) && !"appuse".equals(userBean.getPk_corp())) {
//				upsql.append(" and pk_corp =? ");
//			} else {
//				if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//					upsql.append(" and pk_tempcorp =?  ");
//				}
//			}
//		} else {
//			upsql = new StringBuffer();
//			upsql.append("update  ynt_corp_user set  bdata = ? , baccount = ? WHERE  pk_user=?");
//			if (!StringUtil.isEmpty(userBean.getPk_corp()) && !"appuse".equals(userBean.getPk_corp())) {
//				upsql.append(" and pk_corp =? ");
//			} else {
//				if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//					upsql.append(" and pk_tempcorp =?  ");
//				}
//			}
//		}
//		// 普通角色
//		sp.clearParams();
//		if (userBean.getBdata() != null && "Y".equals(userBean.getBdata())) {
//			sp.addParam("Y");
//		} else {
//			sp.addParam("N");
//		}
//		if (userBean.getBaccount() != null && "Y".equals(userBean.getBaccount())) {
//			sp.addParam("Y");
//		} else {
//			sp.addParam("N");
//		}
//		sp.addParam(userBean.getAccount_id());
//		if (!StringUtil.isEmpty(userBean.getPk_corp()) && !"appuse".equals(userBean.getPk_corp())) {
//			sp.addParam(userBean.getPk_corp());
//		} else {
//			if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//				sp.addParam(userBean.getPk_tempcorp());
//			}
//		}
//		sbo.executeUpdate(upsql.toString(), sp);
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setResmsg("分配权限成功");
//		return bean;
//	}
//
//	public ResponseBaseBeanVO qryPrivilege(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		try {
//			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
//				return qryPrivilege1(userBean);
//			}
//		} catch (Exception e) {
//			bean.setRescode(IConstant.FIRDES);
//			if( e instanceof BusinessException){
//				bean.setResmsg(e.getMessage());
//			}else{
//				bean.setResmsg("查询权限出错！");
//			}
//			log.error("错误",e);
//
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO qryPrivilege1(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//
//		AppUserVO userVO = (AppUserVO) sbo.queryVOByID(userBean.getAccount_id(), AppUserVO.class);
//		bean.setRescode(IConstant.DEFAULT);
//		bean.setBdata("N");
//		bean.setBaccount("N");
//		bean.setResmsg("查询权限成功");
//		if (userBean.getType() != null) {
//			if ("0".equals(userBean.getType())) {
//				if (userVO.getBdata() != null && userVO.getBdata().booleanValue()) {
//					bean.setBdata("Y");
//				} else {
//					bean.setResmsg("无权限");
//				}
//
//			} else if ("1".equals(userBean.getType())) {
//				if (userVO.getBaccount() != null && userVO.getBaccount().booleanValue()) {
//					bean.setBaccount("Y");
//				} else {
//					bean.setResmsg("无权限");
//				}
//			}
//		}
//		return bean;
//	}
//
//	public ResponseBaseBeanVO updatestop(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
//			bean =  updatestop1(userBean);
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO updatestop1(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_corp()));
//
//		AppCheckValidUtils.isEmptyWithCorp(userBean.getPk_corp(), userBean.getPk_tempcorp(),"停用用户状态失败，当前公司不存在！");
//
//		StringBuffer sql = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sql.append(" update ynt_corp_user set istate=? where pk_user=? ");
//		sp.addParam(IConstant.THREE);
//		sp.addParam(userBean.getUserid());
//		if (!StringUtil.isEmpty(userBean.getPk_corp()) && !Common.tempidcreate.equals(userBean.getPk_corp())) {
//			sql.append(" and pk_corp =?");
//			sp.addParam(userBean.getPk_corp());
//		} else {
//			if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//				sql.append(" and pk_tempcorp = ?");
//				sp.addParam(userBean.getPk_tempcorp());
//			}
//		}
//		int dor = sbo.executeUpdate(sql.toString(), sp);
//		if (dor > 0) {
//			bean.setRescode(IConstant.DEFAULT);
//			bean.setResmsg("操作成功");
//		} else {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("操作失败");
//		}
//
//		return bean;
//	}
//
//	public ResponseBaseBeanVO updaterenew(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
//			return updaterenew1(userBean);
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO updaterenew1(UserBeanVO userBean) throws DZFWarpException {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, userBean.getPk_corp()));
//		String sql = "update sm_user set istate=?,locked_tag='N' where cuserid=?";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(IConstant.TWO);
//		sp.addParam(userBean.getUserid());
//		int dor = sbo.executeUpdate(sql, sp);// dao.executeUpdate(sql);
//		if (dor > 0) {
//			bean.setRescode(IConstant.DEFAULT);
//			bean.setResmsg("操作成功");
//		} else {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("操作失败");
//		}
//		return bean;
//	}
//
//	public ResponseBaseBeanVO delete(UserBeanVO userBean) {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1) {// 第一版走的代码
//			bean = delete1(userBean);
//		}
//		return bean;
//	}
//
//	private ResponseBaseBeanVO delete1(UserBeanVO userBean) {
//
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		if (AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
//			userBean.setPk_corp(null);
//		}
//
//		boolean ismanage = apppubservice.isManageUserInCorp(userBean.getPk_corp(),userBean.getPk_tempcorp(), userBean.getAccount_id());
//
//		if(!ismanage){
//			throw new BusinessException("权限已变更，您无该权限!");
//		}
//
//		AppCheckValidUtils.isEmptyWithCorp(userBean.getPk_corp(), userBean.getPk_tempcorp(), "停用用户状态失败，当前公司不存在！");
//
//		if (StringUtil.isEmpty(userBean.getUserid())) {
//			throw new BusinessException("删除用户信息失败，当前用户信息为空！");
//		}
//
//		if (userBean.getUserid().equals(userBean.getAccount_id())) {
//			throw new BusinessException("删除失败，请勿删除当前账号!");
//		}
//
//		StringBuffer sql = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sql.append(" update ynt_corp_user set dr=1 where pk_user=? ");
//		sp.addParam(userBean.getUserid());
//		if (!AppCheckValidUtils.isEmptyCorp(userBean.getPk_corp())) {
//			sql.append(" and pk_corp =?");
//			sp.addParam(userBean.getPk_corp());
//		} else {
//			if (!StringUtil.isEmpty(userBean.getPk_tempcorp())) {
//				sql.append(" and pk_tempcorp = ?");
//				sp.addParam(userBean.getPk_tempcorp());
//			}
//		}
//		int dor = sbo.executeUpdate(sql.toString(), sp);
//		if (dor > 0) {
//			bean.setRescode(IConstant.DEFAULT);
//			bean.setResmsg("操作成功");
//		} else {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("操作失败");
//		}
//		return bean;
//	}
//
//	/**
//	 * 环信，信息查询
//	 */
//	@Override
//	public ResponseBaseBeanVO qryhxUser(UserBeanVO userbean) throws BusinessException {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//
//		if ((StringUtil.isEmpty(userbean.getPk_corp()) || Common.tempidcreate.equals(userbean.getPk_corp()))
//				&& StringUtil.isEmpty(userbean.getPk_tempcorp())) {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("公司信息为空!");
//			return bean;
//		}
//
//		List<HxUserVo> hxvolist = new ArrayList<HxUserVo>();
//		// 客服sql
//		StringBuffer hxsqlkf = new StringBuffer();
//		hxsqlkf.append(" select  t.hxaccountid as hximaccountid ,t.uuid as  hxuuid ,t.pk_corp,t.hxaccountname as hxaccountname from ynt_hximaccount t   ");
//		hxsqlkf.append(" where nvl(dr,0)=0  and t.pk_corp  is null and t.pk_tempcorp  is null ");
//		List<AppUserVO> hxlist1 = (List<AppUserVO>) sbo.executeQuery(hxsqlkf.toString(), new SQLParameter(),
//				new BeanListProcessor(AppUserVO.class));
//		if (hxlist1 != null && hxlist1.size() > 0) {
//			HxUserVo hxuservo1 = new HxUserVo();
//			hxuservo1.setTitle("大账房团队");
//			hxuservo1.setContent(hxlist1.toArray(new AppUserVO[0]));
//			hxvolist.add(hxuservo1);
//		}
//
//		// 当前公司人员sql
//		StringBuffer hxsql = new StringBuffer();
//		hxsql.append(" select  t.hxaccountid as hximaccountid ,t.uuid as  hxuuid ,t.pk_corp,sm_user.user_name as hxaccountname ,t.pk_tempcorp ,sm_user.user_code  from ynt_hximaccount t   ");
//		hxsql.append(" inner join sm_user on sm_user.cuserid = t.userid ");
//		hxsql.append(" where nvl(t.dr,0)=0  and t.userid != ?");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(userbean.getUserid());
//		if (!StringUtil.isEmpty(userbean.getPk_corp()) && !Common.tempidcreate.equals(userbean.getPk_tempcorp())) {
//			hxsql.append(" and t.pk_corp = ?  ");
//			sp.addParam(userbean.getPk_corp());
//		}
//		if (!StringUtil.isEmpty(userbean.getPk_tempcorp())) {
//			hxsql.append(" and t.pk_tempcorp = ?  ");
//			sp.addParam(userbean.getPk_tempcorp());
//		}
//
//		List<AppUserVO> hxlist = (List<AppUserVO>) sbo.executeQuery(hxsql.toString(), sp,
//				new BeanListProcessor(AppUserVO.class));
//		if ((hxlist == null || hxlist.size() == 0) && (hxlist1 == null || hxlist1.size() == 0)) {
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("人员列表为空!");
//		} else {
//			bean.setRescode(IConstant.DEFAULT);
//			if (hxlist != null && hxlist.size() > 0) {
//				String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
//				String imagename = null;
//				File imagefile = null;
//				for (AppUserVO vo : hxlist) {
//					try {
//						vo.setHximaccountid(vo.getHximaccountid().toLowerCase());
//						vo.setHxaccountname(CodeUtils1.deCode(vo.getHxaccountname()));
//
//						for (String type : types) {
//							// 文件名
//							imagename = ImageCommonPath.getUserHeadPhotoPath(vo.getUser_code(), type);
//							imagefile = new File(imagename);
//							if (imagefile.exists()) {
//								vo.setPhotopath(imagefile.getPath());
//								break;
//							}
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//				HxUserVo hxuservo2 = new HxUserVo();
//				hxuservo2.setTitle(userbean.getCorpname());
//				hxuservo2.setContent(hxlist.toArray(new AppUserVO[0]));
//				hxvolist.add(hxuservo2);
//
//
//			}
//			bean.setResmsg(hxvolist);
//		}
//		return bean;
//	}
//
//	private DZFBoolean isPhoneNode(String code) {
//		// 移动
//		Pattern pyd = Pattern.compile("(^1(3[4-9]|4[7]|5[0-27-9]|7[8]|8[2-478])\\d{8}$)|(^1705\\d{7}$)");
//		// 联通
//		Pattern plt = Pattern.compile("(^1(3[0-2]|4[5]|5[56]|7[6]|8[56])\\d{8}$)|(^1709\\d{7}$)");
//		// 电信
//		Pattern pdx = Pattern.compile("(^1(33|53|77|73|8[019])\\d{8}$)|(^1700\\d{7}$)");
//
//		Matcher myd = pyd.matcher(code);
//		Matcher mlt = plt.matcher(code);
//		Matcher mdx = pdx.matcher(code);
//		if (myd.matches() || mlt.matches() || mdx.matches()) {
//			return DZFBoolean.TRUE;
//		}
//		return DZFBoolean.FALSE;
//	}
//
//	public SingleObjectBO getSbo() {
//		return sbo;
//	}
//
//	@Autowired
//	public void setSbo(SingleObjectBO sbo) {
//		this.sbo = sbo;
//	}
//
//	@Override
//	public ResponseBaseBeanVO qryUserLsFromCon(UserBeanVO userBean) throws BusinessException {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//		try {
//			if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO310 && userBean.getVersionno().intValue()<IVersionConstant.VERSIONNO320) {// 第一版走的代码
//				return qryUserLs1(userBean,DZFBoolean.FALSE);
//			}else if(userBean.getVersionno().intValue()>= IVersionConstant.VERSIONNO320){
//				userBean.setIstates("2");
//				return qryUserLs1(userBean, DZFBoolean.FALSE);
//			}
//		} catch (Exception e) {
//			bean.setRescode(IConstant.FIRDES);
//			if(e instanceof BusinessException){
//				bean.setResmsg(e.getMessage());
//			}else{
//				bean.setResmsg("获取人员信息出错！");
//			}
//		}
//		return bean;
//	}
//
//	@Override
//	public ResponseBaseBeanVO qryAuditUserLs(UserBeanVO userBean) throws DZFWarpException {
//		ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
//
//		userBean.setIstates("3");
//		userBean.setIaudituser(DZFBoolean.TRUE);
//		List<AppUserVO> listvos = getUserlist(userBean, DZFBoolean.FALSE);
//
//		if(listvos == null || listvos.size() == 0){
//			bean.setRescode(IConstant.FIRDES);
//			bean.setResmsg("暂无人员");
//		}else{
//			bean.setRescode(IConstant.DEFAULT);
//			UserLsBean[] ubs  = UserUtil.setUserLsBean( listvos.toArray(new AppUserVO[0]));
//			bean.setResmsg(ubs);
//		}
//		return bean;
//	}
}

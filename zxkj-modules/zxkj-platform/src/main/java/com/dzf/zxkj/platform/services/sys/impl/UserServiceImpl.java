package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.ISmsConst;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.sys.IRoleMngService;
import com.dzf.zxkj.platform.services.sys.IUserService;
import com.dzf.zxkj.platform.util.FunnodetreeCreate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@SuppressWarnings("all")
@Service("userServiceImpl")
@Slf4j
public class UserServiceImpl implements IUserService {

	@Autowired
	private ICorpService corpService;

	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}	
	
	@Override
	public UserVO loginByCode(String usercode)  throws DZFWarpException {
		UserVO userVo = null;
		if(StringUtil.isEmpty(usercode))
			return userVo;
		try{
			SQLParameter sp=new SQLParameter();
			sp.addParam(usercode);
			String where = " nvl(dr,0) = 0  and  user_code=? ";
			List<UserVO> listVo =  (List<UserVO>)singleObjectBO.retrieveByClause(UserVO.class,where, sp);
			if(listVo == null || listVo.size() == 0 ){
				return null;
			}
			userVo = listVo.get(0);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return userVo;
	}
	
	@Override
	public UserVO save(UserVO uservo) throws DZFWarpException{//新增保存
//		chkDate(uservo);
		validateUserVO(uservo);
		if (isUserCodeExist(uservo.getUser_code(),null))
			throw new BusinessException("用户编码"+ uservo.getUser_code()+ "已被使用，请使用别的用户编码\n");

		if(StringUtil.isEmpty(uservo.getPrimaryKey())){
		    uservo.setDpwddate(new DZFDate());
			uservo.setUser_password(new Encode().encode(uservo.getUser_password()));
			if(StringUtil.isEmpty(uservo.getPk_department())){
                String pk_dept = queryTopDept(uservo.getPk_corp());
                uservo.setPk_department(pk_dept);
            }
			CorpVO cvo = corpService.queryByPk(uservo.getPk_corp());
			if(cvo != null && cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
			    if(uservo.getPk_creatcorp().equals(IDefaultValue.DefaultGroup)){
			        saveEmployee(uservo);
			    }
			}
			uservo = (UserVO) singleObjectBO.saveObject(uservo.getPk_creatcorp(),uservo);
			if(cvo != null && cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
                if(uservo.getPk_creatcorp().equals(IDefaultValue.DefaultGroup)){
                    saveUserCorp(uservo);
                    
                    String sql = "update ynt_employee set cuserid = ? where pk_employee = ? and pk_corp = ?";
                    SQLParameter params = new SQLParameter();
                    params.addParam(uservo.getCuserid());
                    params.addParam(uservo.getPk_employee());
                    params.addParam(uservo.getPk_corp());
                    singleObjectBO.executeUpdate(sql, params);
                }
            }
			if(uservo.getIsmanager() != null && uservo.getIsmanager().booleanValue()){
			    if(cvo != null && cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
			        if(uservo.getPk_creatcorp().equals(IDefaultValue.DefaultGroup)){
			            insertUserAdminRole(uservo);
			        }
			    }else if(cvo != null && cvo.getIsfactory() != null && cvo.getIsfactory().booleanValue()){
			    	insertUserAdminRole(uservo);
			    }else{
			        insertUserAdminRole(uservo);
			    }
			}
			updateEmployeeDept(uservo);
		}else{
			throw new BusinessException("非法操作");
		}
		return uservo;
	}
	
	/**
	 * 加盟商--新增用户是，同时拥有当前机构的权限
	 * @author gejw
	 * @time 上午10:10:54
	 */
	private void saveUserCorp(UserVO uservo){
	    UserCorpVO ucvo = new UserCorpVO();
        ucvo.setPk_corp(uservo.getPk_corp());
        ucvo.setPk_corpk(uservo.getPk_corp());
        ucvo.setCuserid(uservo.getCuserid());
        ucvo.setDr(0);
        singleObjectBO.saveObject(uservo.getPk_corp(), ucvo);
	}
	
	/**
     * 加盟商--保存用户时同时生成员工信息
     * @author gejw
     * @time 下午5:53:44
     * @param uvo
     */
    private void saveEmployee(UserVO uvo){
        EmployeeVO evo = new EmployeeVO();
        evo.setPk_corp(uvo.getPk_corp());
        evo.setVemcode(uvo.getUser_code());
        evo.setVemname(SecretCodeUtils.deCode(uvo.getUser_name()));
        evo.setPk_department(uvo.getPk_department());
        evo.setDeptname("公司");
        evo.setPhone(uvo.getPhone());
        evo.setIstatus(1);
        evo.setDindate(uvo.getAble_time());
        evo.setDoperatedate(new DZFDate());
        evo = (EmployeeVO) singleObjectBO.saveObject(uvo.getPk_corp(), evo);
        uvo.setPk_employee(evo.getPk_employee());
    }
	
	/**
	 * 同步更新员工信息部门
	 * @author gejw
	 * @time 上午10:00:23
	 * @param uservo
	 */
	private void updateEmployeeDept(UserVO uservo){
	    if(!StringUtil.isEmpty(uservo.getPk_employee())){
	        String uSql = "update ynt_employee set pk_department = ?,deptname = ? where pk_corp = ? and pk_employee = ?";
            SQLParameter params = new SQLParameter();
            params.addParam(uservo.getPk_department());
            params.addParam(uservo.getDeptname());
            params.addParam(uservo.getPk_corp());
            params.addParam(uservo.getPk_employee());
            singleObjectBO.executeUpdate(uSql, params);
	    }
	}
	private String queryTopDept(String pk_corp){
	    SQLParameter params = new SQLParameter();
	    params.addParam(pk_corp);
	    DepartmentVO[] vos =(DepartmentVO[]) singleObjectBO.queryByCondition(DepartmentVO.class, " nvl(dr,0) = 0 and deptcode = '01' and pk_corp = ?", params);
	    if(vos != null && vos.length > 0){
	        return vos[0].getPk_department();
	    }
	    return null;
	}
	@Override
	public void update(UserVO vo)  throws DZFWarpException{//修改保存
//		chkDate(vo);
		validateUserVO(vo);
		if (isUserCodeExist(vo.getUser_code(),vo.getCuserid()))
            throw new BusinessException("用户编码"+ vo.getUser_code()+ "已被使用，请使用别的用户编码\n");
		UserVO oldUservo = queryUserById(vo.getCuserid());
		if(StringUtil.isEmpty(vo.getPk_employee()) && !StringUtil.isEmpty(oldUservo.getPk_employee())){
		    vo.setPk_employee(oldUservo.getPk_employee());
		}
		String phone = vo.getPhone();
		if(vo.getUser_password().equalsIgnoreCase("******") 
				|| vo.getUser_password()==null || vo.getUser_password().equals("")){
			singleObjectBO.update(vo, new String[]{"user_code","checkcode","ismanager","user_note","user_name","able_time","disable_time","user_mail","phone","pk_department","pk_employee","emname"});
		}else{
			vo.setUser_password(new Encode().encode(vo.getUser_password()));
			if(!vo.getUser_password().equals(oldUservo.getUser_password())){
			    vo.setDpwddate(new DZFDate());
			}else{
			    vo.setDpwddate(oldUservo.getDpwddate());
			}
			singleObjectBO.update(vo, new String[]{"user_code","checkcode","ismanager","user_note","user_name","user_password","able_time","disable_time","user_mail","phone","pk_department","pk_employee","emname","dpwddate"});
		}
		
		CorpVO cvo = corpService.queryByPk(vo.getPk_corp());
        if(cvo != null && cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
            updateEmployee(vo);
        }
        if(oldUservo.getIsmanager() != null && oldUservo.getIsmanager().booleanValue()){
            if(vo.getIsmanager() == null || !vo.getIsmanager().booleanValue()){
                deleteUserRole(vo.getCuserid(),vo.getPk_corp());
            }
        }else{
            if(vo.getIsmanager() != null && vo.getIsmanager().booleanValue()){
                insertUserAdminRole(vo);
            }
        }
		
		String pwd = vo.getUser_password();
		String sql = "update app_temp_user set user_password = ?,phone = ? where pk_user = ?";
		SQLParameter params = new SQLParameter();
		params.addParam(pwd);
		params.addParam(phone);
		params.addParam(vo.getCuserid());
		singleObjectBO.executeUpdate(sql, params);
		updateEmployeeDept(vo);
	}
	
	/**
	 * 如果是加盟商，修改用户的同时，修改员工信息
	 * @author gejw
	 * @time 下午1:50:17
	 */
	private void updateEmployee(UserVO vo){
	    EmployeeVO evo = new EmployeeVO();
	    evo.setPk_employee(vo.getPk_employee());
	    evo.setPk_corp(vo.getPk_corp());
	    evo.setVemcode(vo.getUser_code());
	    evo.setVemname(SecretCodeUtils.deCode(vo.getUser_name()));
	    evo.setDindate(vo.getAble_time());
	    singleObjectBO.update(evo, new String[]{"vemcode","vemname","dindate"});
	}
	
	//修改密码
	public int updatePsw(UserVO uservo,String psw) throws DZFWarpException{
		String yzsql = "select user_password from sm_user where user_name='" 
						+ uservo.getUser_name() + "' and nvl(dr,0)=0";
		String oldpsw = (String) singleObjectBO.executeQuery(yzsql, new SQLParameter(),new ColumnProcessor());
		int flag = -1;
		if(oldpsw != null){
			if( uservo.getUser_password().equals(new Encode().decode( oldpsw )) ){
				String ENpsw = new Encode().encode(psw);
				String sql = "update sm_user set user_password ='" + ENpsw +",dpwddate='"+new DZFDate()+"'"
						+ "' where user_name='" + uservo.getUser_name() + "'";
				flag = singleObjectBO.executeUpdate(sql, new SQLParameter());
			}else{
				flag = -2;
				throw new BusinessException("输入初始密码错误！");
			}
		}else{
			flag = -2;
			throw new BusinessException("修改密码失败，请检查！");
		}
		return flag;
	}
	
	public void insertUserAdminRole(UserVO uservo) throws DZFWarpException{
	    CorpVO cvo = corpService.queryByPk(uservo.getPk_corp());
	    if(cvo != null){
	        String role_code = RoleVO.COMPANY_ADMIN_ROLE_CODE;
	        if(cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
	            role_code = RoleVO.JMS01_ROLE_CODE;
	        }
	        if(cvo.getIsfactory() != null && cvo.getIsfactory().booleanValue()){
	            role_code = "kjgc007";
	        }
	        RoleVO rolevo = getRoles(role_code);
	        if(rolevo != null){
	            UserRoleVO[] urvos = castToUserRoleVOs(new RoleVO[]{ rolevo},uservo.getPk_corp(),
	                    uservo.getCuserid());
	            singleObjectBO.insertVOArr(uservo.getPk_corp(), urvos);
	            if(cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
	                 insertUserRole(rolevo,uservo);
	            }
	        }
	    }
	}
	
	/**
	 * 加盟商--增加用户同时委派机构负责人角色
	 * @author gejw
	 * @time 上午9:56:56
	 * @param rolevo
	 * @param uservo
	 */
	private void insertUserRole(RoleVO rolevo,UserVO uservo){
	    JMUserRoleVO urvo = new JMUserRoleVO();
	    urvo.setPk_corp(uservo.getPk_corp());
	    urvo.setPk_role(rolevo.getPk_role());
	    urvo.setCuserid(uservo.getCuserid());
	    singleObjectBO.saveObject(uservo.getPk_corp(), urvo);
	}
	
	
	public boolean isUserCodeExist(String userCode,String cuserid) throws DZFWarpException {
		String sql = "select user_code from sm_user where UPPER(user_code)=? ";// 由于用户编码不区分大小写，所以UPPER
		SQLParameter param = new SQLParameter();
		param.addParam(userCode.toUpperCase());
		if(!StringUtil.isEmpty(cuserid)){
            sql = sql + " and cuserid <> ?";
            param.addParam(cuserid);
        }
		Object obje = singleObjectBO.executeQuery(sql,param, new ColumnProcessor(1));
		if (obje != null)
			return true;
		else
			return false;
	}
	
//	public boolean isCheckCodeExist(UserVO uservo) throws DZFWarpException {//zpm删除，没有地方调用。2019.5.17
//		String sql = "select checkcode from sm_user where UPPER(checkcode)=? and nvl(dr,0)=0" ;//
//		SQLParameter param = new SQLParameter();
//		param.addParam(uservo.getCheckcode().toUpperCase());
//		if(uservo.getCuserid() != null && !uservo.getCuserid().equals("")){
//			sql = sql + " and cuserid <> ?";
//			param.addParam(uservo.getCuserid());
//		}
//		Object obje = singleObjectBO.executeQuery(sql,param, new ColumnProcessor(1));
//		if (obje != null)
//			return true;
//		else
//			return false;
//	}
	
	@Override
	public UserVO login(UserVO vo)  throws DZFWarpException{
		UserVO userVo = null;
		try{
			SQLParameter sp=new SQLParameter();
			sp.addParam(vo.getUser_name());
			sp.addParam(new Encode().encode(vo.getUser_password()));
			String where = " nvl(dr,0) = 0  and  user_code=? and user_password= ? ";
			List<UserVO> listVo =  (List<UserVO>)singleObjectBO.retrieveByClause(UserVO.class,where, sp);
			if(listVo == null || listVo.size() == 0 ){
				return null;
			}
			userVo = listVo.get(0);
		}catch(Exception e){
			log.error(e.getMessage(), e);
		}
		return userVo;
	}

	@Override
	public void delete(UserVO vo)  throws DZFWarpException{
		checkUserRef(vo.getCuserid());
//		String delsql = " delete from sm_user where nvl(dr,0) = 0 and cuserid = ? ";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(vo.getCuserid());
//		singleObjectBO.executeUpdate(delsql, sp);
//		UserCache.getInstance().remove(vo.getCuserid());
		
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmm");
        String time = df.format(new Date()); 
        String delsql = " update sm_user set user_code = CONCAT(user_code,'_" + time + "'),dr = 1 where pk_corp = ? and cuserid = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getCuserid());
        singleObjectBO.executeUpdate(delsql, sp);
//        UserCache.getInstance().remove(vo.getCuserid());
        
        deleteAuditSet(vo);
        
        if(!StringUtil.isEmpty(vo.getJcuid())){
            updateBusiness(vo);
        }else{
            deleteUserPower(vo.getCuserid());
        }
	}
	
	/**
	 * 删除审批流设置用户
	 * @author gejw
	 * @time 下午1:46:00
	 */
	private void deleteAuditSet(UserVO vo){
	    String sql = "delete from ynt_approveset_b where pk_corp = ? and vauditpsnid = ?";
	    SQLParameter parameter = new SQLParameter();
	    parameter.addParam(vo.getPk_corp());
	    parameter.addParam(vo.getCuserid());
	    singleObjectBO.executeUpdate(sql, parameter);
	}
	
	/**
	 * 交接任务
	 * @author gejw
	 * @time 下午1:47:46
	 * @param vo
	 */
	private void updateBusiness(UserVO vo){
	    SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getJcuid());
        sp.addParam(vo.getPk_corp());
        sp.addParam(vo.getCuserid());
        String sql = "update ynt_workflow set dealman = ? where pk_corp = ? and dealman = ? and isdeal = 'N'";
        singleObjectBO.executeUpdate(sql, sp);
        
        sql = "update ynt_contract set preauditpsnid = ? where pk_corp = ? and preauditpsnid = ? and vstatus = 6";
        singleObjectBO.executeUpdate(sql, sp);
 
        sql = "update ynt_charge set preauditpsnid = ? where pk_corp = ? and preauditpsnid = ? and istatus = 2";
        singleObjectBO.executeUpdate(sql, sp);
 
        sql = "update ynt_task_b set vdealman = ? where pk_corp = ? and vdealman = ? and (bstatus = 1 or bstatus = 2)";
        singleObjectBO.executeUpdate(sql, sp);
        updateCorpPower(vo);
//        sql = "update sm_user_role set cuserid = ? where pk_corp != ? and cuserid = ? ";
//        singleObjectBO.executeUpdate(sql, sp);
	}
	
	/**
	 * 交接客户权限
	 * @author gejw
	 * @time 下午2:21:58
	 * @param vo
	 */
	private void updateCorpPower(UserVO vo){
	    String condition = " cuserid = ? and nvl(dr,0) = 0";
	    SQLParameter params = new SQLParameter();
	    params.addParam(vo.getJcuid());
	    UserRoleVO[] vos = (UserRoleVO[]) singleObjectBO.queryByCondition(UserRoleVO.class, condition, params);
	    ArrayList<String> list = new ArrayList<>();
	    if(vos != null && vos.length > 0){
	        for(UserRoleVO rvo : vos){
	            list.add(rvo.getPk_corp()+"_"+rvo.getPk_role());
	        }
	    }
	    
	    params.clearParams();;
        params.addParam(vo.getCuserid());
        UserRoleVO[] vosdel = (UserRoleVO[]) singleObjectBO.queryByCondition(UserRoleVO.class, condition, params);
        ArrayList<UserRoleVO> listvo = new ArrayList<>();
        ArrayList<UserRoleVO> listdelvo = new ArrayList<>();
        for(UserRoleVO rvo : vosdel){
            if(!list.contains(rvo.getPk_corp()+"_"+rvo.getPk_role())){
                rvo.setCuserid(vo.getJcuid());
                listvo.add(rvo);
            }else{
                listdelvo.add(rvo);
            }
        }
        if(listvo != null && listvo.size() > 0){
            singleObjectBO.updateAry(listvo.toArray(new UserRoleVO[0]), new String[]{"cuserid"});
        }
        if(listdelvo != null && listdelvo.size() > 0){
            singleObjectBO.deleteVOArray(listdelvo.toArray(new UserRoleVO[0]));
        }
	}
	
	/**
     * 删除用户客户权限数据
     * @author gejw
     * @time 下午3:40:05
     * @param vo
     */
    private void deleteUserPower(String userid){
        SQLParameter sp = new SQLParameter();
        //删除用户、客户、角色关系表
        sp.addParam(userid);
        singleObjectBO.executeUpdate("delete from sm_user_role where cuserid = ? ", sp);
    }


	@Override
	public List<CorpVO> querypowercorp(String userid) throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(userid);
		StringBuffer sf  = new StringBuffer();
		sf.append(" select distinct tax.tax_area,cp.* from bd_corp cp ");
		sf.append(" join sm_user_role re on re.pk_corp = cp.pk_corp ");
		sf.append(" left join bd_corp_tax tax on tax.pk_corp = cp.pk_corp and nvl(tax.dr,0) = 0 ");
		sf.append(" where re.cuserid = ? and nvl(re.dr,0) = 0 and nvl(cp.isaccountcorp,'N') = 'N' ");
		sf.append(" and nvl(cp.isseal,'N') = 'N'   and nvl(cp.ishasaccount,'N') = 'Y' ");
		sf.append(" order by cp.innercode  ");
		List<CorpVO> list = (List<CorpVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(CorpVO.class));
		return list;
	}
	
	@Override
	public List<CorpVO> queryPowerCorpKj(String userid) throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(userid);
		StringBuffer sf  = new StringBuffer();
		sf.append(" select distinct tax.tax_area, cp.* from bd_corp cp ");
		sf.append(" join sm_user_role re on re.pk_corp = cp.pk_corp ");
		sf.append(" join sm_role role on re.pk_role = role.pk_role ");
		sf.append(" left join bd_corp_tax tax on tax.pk_corp = cp.pk_corp and nvl(tax.dr,0) = 0 ");
		sf.append(" where re.cuserid = ? and nvl(re.dr,0) = 0 and nvl(cp.isaccountcorp,'N') = 'N' ");
		sf.append(" and nvl(cp.isseal,'N') = 'N' and nvl(cp.ishasaccount,'N') = 'Y' ");
		sf.append(" and role.roletype in(4,6,8)");
		sf.append(" order by cp.innercode  ");
		List<CorpVO> list = (List<CorpVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(CorpVO.class));
		return list;
	}
	
	public List<CorpVO> queryPowerCorpAdmin(String userid) throws DZFWarpException{
		 SQLParameter sp = new SQLParameter();
		 sp.addParam(userid);
		 sp.addParam(new DZFDate());
		 StringBuffer sf = new StringBuffer();
		 sf.append(" select distinct cp.* from bd_corp cp ");
		 sf.append(" join sm_user_role re on re.pk_corp = cp.pk_corp ");
		 sf.append(" join BD_ACCOUNT t on cp.PK_CORP = t.PK_CORP ");
		 sf.append(" where re.cuserid = ? and nvl(re.dr,0) = 0 ");
//		 sf.append(" and nvl(cp.isseal,'N') = 'N'  and nvl(cp.ISACCOUNTCORP,'N') = 'Y'");
		 sf.append(" and (cp.sealeddate > ? or cp.sealeddate is null)  and nvl(cp.ISACCOUNTCORP,'N') = 'Y'");
		 sf.append(" and nvl(cp.dr,0) = 0 and nvl(t.dr,0) = 0 order by cp.pk_corp ");// cp.FATHERCORP
		 List list = (List) singleObjectBO.executeQuery(sf.toString(), sp, new
		 BeanListProcessor(CorpVO.class));
		return list;
	}
	
	@Override
	public Set<String> queryPowerCorpAdSet(String userid) throws DZFWarpException{
		List<CorpVO> list = queryPowerCorpAdmin(userid);
		Set<String> corpSet = null;
		if(list != null && list.size() > 0){
			corpSet = new HashSet<String>();
			for(CorpVO cvo : list){
				corpSet.add(cvo.getPk_corp());
			}
		}
		return corpSet;
	}

	@Override
	public Set<String> querypowercorpSet(String userid) throws DZFWarpException{
		if(StringUtil.isEmpty(userid))
			return new HashSet<String>();
		List<CorpVO> list = querypowercorp(userid);
		Set<String> corpSet = null;
		if(list != null && list.size() > 0){
			corpSet = new HashSet<String>();
			for(CorpVO cvo : list){
				corpSet.add(cvo.getPk_corp());
			}
		}
		return corpSet;
	}

	@Override
	public CorpVO querypowercorpById(String id) throws DZFWarpException {
		return (CorpVO)singleObjectBO.queryVOByID(id, CorpVO.class);
	}
	
	//验证的时查询
	@Override
	public UserVO queryUserById(String id) throws DZFWarpException {
		return (UserVO)singleObjectBO.queryVOByID(id, UserVO.class);
	}
	
	public CorpVO[] getValidateCorpByUserId(String dsName,
			String userID) throws DZFWarpException {
		java.util.Vector v = new java.util.Vector();
		try {
			String whereSql = " (isseal is null or isseal<>'Y') and ishasaccount='Y' and nvl(dr,0) =0 ";
			whereSql += " and pk_corp in (select b.pk_corp from sm_user a  , sm_user_role b  where a.cuserid = b.cuserid and a.cUserId = ? ) order by unitcode";
			SingleObjectBO sbo=new SingleObjectBO(DataSourceFactory.getDataSource(null, IGlobalConstants.DefaultGroup));
			SQLParameter sp=new SQLParameter();
			sp.addParam(userID);
			CorpVO[] corps=(CorpVO[]) sbo.queryByCondition(CorpVO.class, whereSql, sp);
//			nc.vo.bd.CorpVO[] corps = new nc.bs.bd.CorpDMO().queryByWhereSQL(whereSql);
			int n = corps == null ? 0 : corps.length;
			for (int i = 0; i < n; i++) {
				v.add(corps[i]);
			}
		} catch (DAOException e) {
			throw e;
		}
		CorpVO[] retrs = new CorpVO[v.size()];
		if (v.size() > 0)
			v.copyInto(retrs);
		return retrs;
	}
	@Override
	public List<UserVO> query(String pk_corp, SysPowerConditVO qryVO)  throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sql.append("select u.*,d.deptname as deptname from sm_user u");
		sql.append(" left join ynt_department d on u.pk_department = d.pk_department");
//		StringBuffer where = new StringBuffer();
		sql.append(" where u.pk_corp=? and nvl(u.dr,0) = 0 ");//各公司显示各公司的用户zpm
		sp.addParam(pk_corp);
		if(qryVO != null){
		    if(!StringUtil.isEmpty(qryVO.getPk_dept())){
                sql.append(" and u.pk_department = ? ");
                sp.addParam(qryVO.getPk_dept());
            }else{
                if(!StringUtil.isEmpty(qryVO.getUcode())){
                    sql.append(" and u.user_code like ? ");
                    sp.addParam("%"+qryVO.getUcode()+"%");
                }
                if(!StringUtil.isEmpty(qryVO.getUname())){
                    String corpname = qryVO.getUname();
                    sql.append(" and u.user_name = ? ");
                    sp.addParam(corpname);
                }
                if(!StringUtil.isEmpty(qryVO.getEntime())){
                    sql.append(" and u.able_time >= ? ");
                    sp.addParam(qryVO.getEntime());
                }
                if(!StringUtil.isEmpty(qryVO.getIlock()) && qryVO.getIlock().equals("Y")){
                    sql.append(" and u.locked_tag = 'Y' ");
                }else if(!StringUtil.isEmpty(qryVO.getIlock()) && qryVO.getIlock().equals("N")){
                    sql.append(" and nvl(u.locked_tag,'N')= 'N' ");
                }
                
                if(!StringUtil.isEmpty(qryVO.getUtype())){
                    sql.append(" and u.xsstyle = ?");
                    sp.addParam(qryVO.getUtype());
                }
            }
		}
		sql.append(" order by user_code asc");
		List<UserVO> listVo = (ArrayList<UserVO>)singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(UserVO.class));
//		List<UserVO> listVo =  (List<UserVO>)singleObjectBO.retrieveByClause(UserVO.class,where.toString(), sp);
		if(listVo != null && listVo.size()>0){
			UserVO uvo = new UserVO();
			for(int i=0;i<listVo.size();i++){
				uvo = listVo.get(i);
				UserVO vo=new UserVO();
				vo.setUser_code(uvo.getUser_code());
				vo.setUser_name(uvo.getUser_name());
				vo.setCheckcode(uvo.getCheckcode());
				vo.setAble_time(uvo.getAble_time());
				vo.setDisable_time(uvo.getDisable_time());
				vo.setCorpnm(corpService.queryByPk(uvo.getPk_corp()).getUnitname());
				if(uvo.getPk_creatcorp()!=null)
					vo.setCrtcorp(corpService.queryByPk(uvo.getPk_creatcorp()).getUnitname());
				vo.setPk_corp(uvo.getPk_corp());
				vo.setCuserid(uvo.getCuserid());
				vo.setIsmanager(uvo.getIsmanager()==null? DZFBoolean.FALSE:uvo.getIsmanager());
				vo.setLocked_tag(uvo.getLocked_tag()==null?DZFBoolean.FALSE:uvo.getLocked_tag());
				vo.setUser_password(new Encode().decode(uvo.getUser_password()));
				vo.setUser_note(uvo.getUser_note());
				vo.setPhone(uvo.getPhone());
				vo.setUser_mail(uvo.getUser_mail());
				vo.setPk_department(uvo.getPk_department());
				vo.setDeptname(uvo.getDeptname());
				vo.setPk_employee(uvo.getPk_employee());
				vo.setEmname(uvo.getEmname());
				vo.setDpwddate(uvo.getDpwddate());
				vo.setStrategy(uvo.getStrategy());
				vo.setPwdeffectiveday(uvo.getPwdeffectiveday());
				vo.setPwdremindday(uvo.getPwdremindday());
				listVo.set(i, vo);
			}
		}
		return listVo;
	}

	@Override
	public boolean exist(UserVO vo) throws DZFWarpException {//
		StringBuffer where = new StringBuffer(" nvl(dr,0) = 0  ");
		SQLParameter sp=new SQLParameter();
		if(vo.getCheckcode().equals("ucode")){
			where.append(" and user_code=?");
			sp.addParam(vo.getCuserid());
		}
//		else if(vo.getCheckcode().equals("uname")){
//			where.append(" and user_name=?");
//			sp.addParam(vo.getCuserid());
//		}
		List<UserVO> listVo =  (List<UserVO>)singleObjectBO.retrieveByClause(UserVO.class,where.toString(), sp);
		if(listVo != null && listVo.size()>0)
			return true;
		return false;
	}
	
	@Override
	public SysFunNodeVO[] getAuthAccessPage(UserVO user, CorpVO corp,String path) throws DZFWarpException {
		String sql = new String("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER "+
								" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE "+
								" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE "+
								" WHERE ROL.CUSERID =? AND ROL.PK_CORP=?  AND MODULE = ? AND NVL(POWER.DR,0)=0 AND NVL(ROL.DR,0)=0 ORDER BY SHOW_ORDER");
		SQLParameter sp = new SQLParameter();
		sp.addParam(user.getCuserid());
		sp.addParam(corp.getPk_corp());
		sp.addParam(path);
		List<SysFunNodeVO> list = (ArrayList<SysFunNodeVO>)getSingleObjectBO().executeQuery(sql, sp, new BeanListProcessor(SysFunNodeVO.class));
		SysFunNodeVO vo = (SysFunNodeVO) BDTreeCreator.createTree(
				list.toArray(new SysFunNodeVO[0]), new FunnodetreeCreate());
		SysFunNodeVO[] bodyvos = (SysFunNodeVO[]) DZfcommonTools.convertToSuperVO(vo
				.getChildren());
		if(bodyvos==null){
			return null;
		}
		return bodyvos;
	}
	
	@Override
	public void updateLock(UserVO vo) throws DZFWarpException {
		singleObjectBO.update(vo, new String[]{"locked_tag"});
	}
	
	/**
	 * 检查新建用户或更新用户时的VO的数据正确性
	 *
	 * @param vo 欲检查的UserVO
	 * @throws BusinessException
	 */
	private void validateUserVO(UserVO vo) throws DZFWarpException {
		boolean ret = false;
		boolean isInValid = false;
		StringBuffer eInfo = new StringBuffer();
		if (StringUtil.isEmpty(vo.getUser_code())) {
			eInfo.append("用户编码不能为空<br>");
			isInValid = true;
		}
		if (vo.getUser_code().contains(" ")) {
			eInfo.append("用户编码不能有空格<br>");
			isInValid = true;
		}
		Pattern pu = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher mu = pu.matcher(vo.getUser_code());
        if (mu.find()) {
            throw new BusinessException("用户编码不能包含汉字。");
        }
		if (vo.getUser_code().length()< 6 ) {
			eInfo.append("用户编码长度不能小于6位<br>");
			isInValid = true;
		}
		if (StringUtil.isEmpty(vo.getUser_password())){
			eInfo.append("密码不能为空<br>");
			isInValid = true;
		}
		if (vo.getUser_password().contains(" ")) {
			eInfo.append("密码不能有空格<br>");
			isInValid = true;
		}
		if (vo.getUser_password().length() < 8){
			eInfo.append("密码长度不能小于8<br>");
			isInValid = true;
		}
		String pwd = vo.getUser_password();
		if (!pwd.matches(".*([0-9]+.*[A-Za-z]+|[A-Za-z]+.*[0-9]+).*")){
			eInfo.append("密码必须含有数字、字母<br>");
			isInValid = true;
		}
		
		String regEx = "[~!@#$%^&*()<>?+=]";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(pwd);
		if (m.find()){
			ret = true;
		}
		if(!ret){
			eInfo.append("密码必须含有特殊字符<br>");
			isInValid = true;
		}
		if (StringUtil.isEmpty(vo.getUser_name())) {
			eInfo.append("用户名不能为空<br>");
			isInValid = true;
		}
		if (vo.getUser_name().contains(" ")) {
			eInfo.append("用户名不能有空格<br>");
			isInValid = true;
		}
		
		if (StringUtil.isEmpty(vo.getPk_corp())) {
			eInfo.append("公司不能为空<br>");
			isInValid = true;
		}
		if (vo.getAble_time() == null) {
			eInfo.append("生效日期不能为空<br>");
			isInValid = true;
		}
		if (vo.getAble_time() != null && vo.getDisable_time() != null) {
			if (vo.getAble_time().after(vo.getDisable_time())) {
				eInfo.append("失效日期不能在生效日期之前<br>");
				isInValid = true;
			}
		}
		if (!StringUtil.isEmpty(vo.getUser_note()) && vo.getUser_note().length() >35 ) {
            eInfo.append("用户描述长度不能大于35位<br>");
            isInValid = true;
        }
		if(!StringUtil.isEmpty(vo.getUser_mail())){
		    ret = DZFValidator.isEmail(vo.getUser_mail());
	        if (!ret){
	            eInfo.append("邮箱格式不正确<br>");
	            isInValid = true;
	        }
		}
		if(!StringUtil.isEmpty(vo.getPhone())){
		    ret = DZFValidator.isMobile(vo.getPhone());
		    if (!ret){
                eInfo.append("手机号格式不正确<br>");
                isInValid = true;
            }
		}
		//增加安全检查
		StringBuffer sf = new StringBuffer(); 
		CheckPwdSecurity.checkUserPWD2(vo.getUser_code(), pwd,sf);
		if(sf.length()>0){
			eInfo.append(sf.toString());
			isInValid = true;
		}
		if (isInValid)
			throw new BusinessException(eInfo.toString());
	}

	@Override
	public UserVO queryByCode(String pkCorp,String usercode) throws DZFWarpException {
		SQLParameter sp=new SQLParameter();
		
		StringBuffer where = new StringBuffer();
		where.append(" nvl(dr,0) = 0 ");
		if(!StringUtil.isEmpty(usercode)){
			where.append(" and user_code = ? ");
			sp.addParam(usercode);
		}else{
			return null;
		}
		Collection collect =  singleObjectBO.retrieveByClause(UserVO.class,where.toString(), sp);
		if (collect == null || collect.size() == 0)
			return null;
		else {
			UserVO voUser = (UserVO) collect.iterator().next();
			if (pkCorp.equals(voUser.getPk_corp()))//为创建公司，直接返回
				return voUser;
			else {
				throw new BusinessException("用户编码已存在，但不属于当前登录公司。");
			}
		}
	}
	
	/**
	 * 将RoleVO转换为UserRoleVO,UserRoleVO中的pk_corp取Role_VO中的pk_corp.
	 * 
	 * @param roles
	 * @param userPK
	 * @return
	 */
	public UserRoleVO[] castToUserRoleVOs(RoleVO[] roles, String userPK) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < roles.length; i++) {
			UserRoleVO urVO = new UserRoleVO();
			urVO.setPk_role(roles[i].getPrimaryKey());
			urVO.setPk_corp(roles[i].getPk_corp());
			urVO.setCuserid(userPK);
			list.add(urVO);
		}
		return (UserRoleVO[]) list.toArray(new UserRoleVO[list.size()]);
	}
	
	/**
	 * 将RoleVO转换为UserRoleVO,其中只取用RoleVO的pk_Role.
	 * 
	 * @param roles
	 * @param corpPK
	 * @param userPK
	 * @return
	 */
	public UserRoleVO[] castToUserRoleVOs(RoleVO[] roles, String corpPK,String userPK) {
		ArrayList list = new ArrayList();
		for (int i = 0; i < roles.length; i++) {
			UserRoleVO urVO = new UserRoleVO();
			urVO.setPk_role(roles[i].getPrimaryKey());
			urVO.setPk_corp(corpPK);
			urVO.setCuserid(userPK);
			list.add(urVO);
		}
		return (UserRoleVO[]) list.toArray(new UserRoleVO[list.size()]);
	}
	
	public RoleVO getRoles(String rolecode){
		IRoleMngService service = (IRoleMngService) SpringUtils.getBean("sys_roleadminserv");
		return service.queryRoleBycode(rolecode);
	}
	
	public void deleteUserRole(String userPK,String pk_corp) throws DZFWarpException {
	    String role_code = RoleVO.COMPANY_ADMIN_ROLE_CODE;
	    CorpVO cvo = corpService.queryByPk(pk_corp);
        if(cvo != null && cvo.getIschannel() != null && cvo.getIschannel().booleanValue()){
            role_code = RoleVO.JMS01_ROLE_CODE;
        }else if(cvo != null && cvo.getIsfactory() != null && cvo.getIsfactory().booleanValue()){
            role_code = "kjgc007";
        }
		RoleVO rolevo = getRoles(role_code);
		if(rolevo != null){
			String sql = "delete from sm_user_role where cuserid=? and pk_role=? and pk_corp=?";
			SQLParameter param = new SQLParameter();
			param.addParam(userPK);
			param.addParam(rolevo.getPrimaryKey());
			param.addParam(pk_corp);
			singleObjectBO.executeUpdate(sql,param);
			
			sql = "delete from sm_userole where cuserid=? and pk_role=? and pk_corp=?";
            singleObjectBO.executeUpdate(sql,param);
		}
	}

	@Override
	public void loginLog(LoginLogVo loginLogVo) throws DZFWarpException {
		try{
			singleObjectBO.saveObject(loginLogVo.getPk_corp() != null ? loginLogVo.getPk_corp() : IDefaultValue.DefaultGroup, loginLogVo);			
		}catch(Exception e){
			throw new WiseRunException(e);
		}
	}

	//zpm 注掉。不去更新数据库了。
//	@Override
//	public void logoutLog(LoginLogVo loginLogVo) throws DZFWarpException {
//		try{
//			SQLParameter sp=new SQLParameter();
//			sp.addParam(loginLogVo.getPk_user());
//			sp.addParam(loginLogVo.getLoginsession());
//			String where = " pk_user = ? and trim(loginsession) = ? and nvl(dr,0)=0 order by logindate desc";
//			List<LoginLogVo> listVo =  (List<LoginLogVo>)singleObjectBO.retrieveByClause(LoginLogVo.class,where, sp);
//			if(listVo != null && listVo.size() > 0 ){
//				LoginLogVo lvo = listVo.get(0);
//				lvo.setLogoutdate(loginLogVo.getLogoutdate());
//				lvo.setLogouttype(loginLogVo.getLogouttype());
//				singleObjectBO.update(lvo, new String[]{"logoutdate","logouttype"});
//			}
//		}catch(Exception e){
//			throw new WiseRunException(e);
//		}
//	}
	
	private void checkUserRef (String userID) {
		SQLParameter sp = new SQLParameter();
//		sp.addParam(userID);
//		String sql = "select count(1) from sm_user_role where cuserid = ? and exists(select 1 from bd_corp cp where cp.pk_corp=sm_user_role.pk_corp and nvl(cp.dr,0)=0)";
//		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
//		if (count != null && count.intValue() > 0)
//			throw new BusinessException("用户已被引用，不允许删除！");
		
		String sql = "select count(1) from ynt_tzpz_h where nvl(dr,0) = 0 and (coperatorid = ? or vapproveid = ? or vjzoperatorid = ? or vcashid = ?)";
		sp.addParam(userID);
		sp.addParam(userID);
		sp.addParam(userID);
		sp.addParam(userID);
		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
        if (count != null && count.intValue() > 0)
            throw new BusinessException("该用户已有凭证数据不能删除！");
	}
	
	@Override
	public List<UserVO> queryOwner(String pk_corp, SysPowerConditVO qryVO)  throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		//sp.addParam(qryVO.getCrtcorp_id());
		StringBuffer where = new StringBuffer();
		where.append(" pk_corp=? and nvl(dr,0) = 0 and isowner='Y' ");//各公司显示各公司的用户zpm
		//where.append(" (pk_corp=? or pk_creatcorp =?) and  nvl(dr,0) = 0 ");
//		where.append(" pk_corp=? and pk_creatcorp =? and  nvl(dr,0) = 0 ");
		if(qryVO != null){
			if(!StringUtil.isEmpty(qryVO.getUcode())){
				where.append(" and user_code like ? ");
				sp.addParam("%"+qryVO.getUcode()+"%");
			}
			if(!StringUtil.isEmpty(qryVO.getUname())){
				 String corpname = qryVO.getUname();
				 where.append(" and user_name = ? ");
				 sp.addParam(corpname);
			}
			if(!StringUtil.isEmpty(qryVO.getEntime())){
				where.append(" and able_time>=? ");
				sp.addParam(qryVO.getEntime());
			}
			if(qryVO.getIlock() != null && !qryVO.getIlock().equals("")){
				where.append(" and nvl(locked_tag,'N')=? ");
				sp.addParam(qryVO.getIlock());
			}
		}
		List<UserVO> listVo =  (List<UserVO>)singleObjectBO.retrieveByClause(UserVO.class,where.toString(), sp);
		if(listVo != null && listVo.size()>0){
			for(int i=0;i<listVo.size();i++){
				UserVO vo=new UserVO();
				vo.setUser_code(listVo.get(i).getUser_code());
				vo.setIsOwner(listVo.get(i).getIsOwner());
				vo.setUser_name(listVo.get(i).getUser_name());
				vo.setCheckcode(listVo.get(i).getCheckcode());
				vo.setAble_time(listVo.get(i).getAble_time());
				vo.setDisable_time(listVo.get(i).getDisable_time());
				vo.setCorpnm(corpService.queryByPk(listVo.get(i).getPk_corp()).getUnitname());
				if(listVo.get(i).getPk_creatcorp()!=null)
					vo.setCrtcorp(corpService.queryByPk(listVo.get(i).getPk_creatcorp()).getUnitname());
				vo.setPk_corp(listVo.get(i).getPk_corp());
				vo.setCuserid(listVo.get(i).getCuserid());
				vo.setIsmanager(listVo.get(i).getIsmanager()==null?DZFBoolean.FALSE:listVo.get(i).getIsmanager());
				vo.setLocked_tag(listVo.get(i).getLocked_tag()==null?DZFBoolean.FALSE:listVo.get(i).getLocked_tag());
				vo.setUser_password(new Encode().decode(listVo.get(i).getUser_password()));
				vo.setUser_note(listVo.get(i).getUser_note());
				vo.setPhone(listVo.get(i).getPhone());
				vo.setUser_mail(listVo.get(i).getUser_mail());
				vo.setPk_department(listVo.get(i).getPk_department());
                vo.setDeptname(listVo.get(i).getDeptname());
                vo.setPk_employee(listVo.get(i).getPk_employee());
                vo.setEmname(listVo.get(i).getEmname());
                vo.setDpwddate(listVo.get(i).getDpwddate());
				listVo.set(i, vo);
			}
		}
		return listVo;
	}

	@Override
	public void updateDept(UserVO[] vos) throws DZFWarpException {
		singleObjectBO.updateAry(vos, new String[]{"pk_department"});
		for(UserVO vo : vos){
		    updateEmployeeDept(vo);
		}
	}
	
	@Override
	public UserVO queryOwnerByTempUser(String pk_temp_user)throws DZFWarpException{
		UserVO userVo = null;
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		
		sb.append("select distinct u.cuserid,u.user_code,u.user_password,u.pk_corp ");
		sb.append(" from sm_user u  ");
		sb.append(" where nvl(u.dr,0)=0 and u.cuserid=( ");
		sb.append(" select pk_user from app_temp_user where nvl(dr,0)=0 and pk_temp_user=? )");
		sb.append(" and u.bappuser= ?");
		
		sp.addParam(pk_temp_user);
		sp.addParam("Y");
		List<UserVO> listVo =  (List<UserVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(UserVO.class));
		if(listVo == null || listVo.size() == 0 ){
			return null;
		}
		userVo = listVo.get(0);
		return userVo;
	}
	
	@Override
	public UserVO loginByOwner(String usercode) throws DZFWarpException {
		UserVO userVo = null;
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		
		sb.append("select distinct u.cuserid,u.user_code,u.user_password,u.pk_corp ");
		sb.append(" from sm_user u  ");
		sb.append(" where nvl(u.dr,0)=0 and u.user_code=? ");
		sb.append(" and u.bappuser= ?");
		
		sp.addParam(usercode);
		sp.addParam("Y");
		List<UserVO> listVo =  (List<UserVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(UserVO.class));
		if(listVo == null || listVo.size() == 0 ){
			return null;
		}
		//如果该企业主有多个公司，反回第一个做登录校验
		userVo = listVo.get(0);
		return userVo;
	}

	@Override
	public List<CorpVO> queryPowerCorpByOwner(String userid) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb  = new StringBuffer();
		
		sb.append(" select * from bd_corp where nvl(dr,0)=0 and begindate is not null and pk_corp in( ");
		sb.append("select cu.pk_corp ");
		sb.append(" from sm_user u inner join ynt_corp_user cu on u.cuserid=cu.pk_user ");
		sb.append(" where nvl(u.dr,0)=0 and nvl(cu.dr,0)=0 and u.cuserid =? ");
		sb.append(" and cu.pk_corp!=? and u.bappuser= ? and cu.istate!=3 and (cu.ismanage='Y'))  order by innercode");
		
		sp.addParam(userid);
		sp.addParam("appuse");
		sp.addParam("Y");
		
		List<CorpVO> list = (List<CorpVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CorpVO.class));
		//如果能登录，但是没有公司，看看是不是有只有上传票据的公司，如果是提示
		if(list==null||list.size()==0){
			throw new BusinessException("请使用管理员账号登录");
		}
		return list;
	}

	@Override
	public Set<String> queryPowerCorpOwnerSet(String userid) throws DZFWarpException {
		List<CorpVO> list = queryPowerCorpByOwner(userid);
		Set<String> corpSet = null;
		if(list != null && list.size() > 0){
			corpSet = new HashSet<String>();
			for(CorpVO cvo : list){
				corpSet.add(cvo.getPk_corp());
			}
		}
		return corpSet;
	}

	@Override
	public int sendVerify(String phone,String url) throws DZFWarpException {
//		UserVO user=new UserVO();
//		user.setUser_code(phone);
		UserVO user=loginByOwner(phone);
		String name = getCorpName(url);
		if(user==null){
			throw new BusinessException("您输入的手机号，不是"+name+"手机用户");
		}
		Random random = new Random();
		int verify = random.nextInt(899999) + 100000;
		Map<String, String> params = new HashMap<String, String>();
		params.put("verify", String.valueOf(verify));
		SMSBVO smsVO = new SMSBVO();
		if(name.equals("大账房")){
			smsVO.setDxqm(ISmsConst.SIGN_01);
		}else{
			smsVO.setDxqm(ISmsConst.SIGN_03);
		}
		smsVO.setParams(params);
		smsVO.setPhone(new String[] { phone});
		smsVO.setTemplatecode(ISmsConst.TEMPLATECODE_0001);
		
		//验证码:${verify}，（大账房绝对不会索取此验证码，切勿告知他人），请您10分钟内在页面中输入以完成验证。
		SMSServiceNew smsServ = new SMSServiceNew(smsVO);
		SMSResVO headvo = smsServ.sendPostData();
		if(!headvo.isSuccess()){
			throw new BusinessException("短信发送失败，请稍后重试");
		}
		return verify;
	}
	
	private String getCorpName(String url){
		String name= "大账房";
		SmsSignatureVO [] smsvos = (SmsSignatureVO[])singleObjectBO.queryByCondition(SmsSignatureVO.class, "nvl(dr,0)=0", new SQLParameter());
		if(smsvos==null)
			return name; 
		for (int i = 0; i < smsvos.length; i++) {
			if(smsvos[i].getVdomain()!=null&&url.contains(smsvos[i].getVdomain())){
				return smsvos[i].getVsigname();
			}
		}
		
		return name;
	}
	
	@Override
	public void updateLoginFlag(UserVO vo) throws DZFWarpException {
		vo.setIslogin(DZFBoolean.TRUE);
		singleObjectBO.update(vo, new String[]{"islogin"});
	}

	@Override
	public void updateOwnerPass(UserVO vo) throws DZFWarpException {
		if(vo.getUser_password().length()<6){
			throw new BusinessException("密码长度不能小于6位");
		}
		String sql="update sm_user set user_password=? where cuserid=? and nvl(dr,0)=0";
    	SQLParameter sp=new SQLParameter();
    	sp.addParam(new Encode().encode(vo.getUser_password()));
    	sp.addParam(vo.getCuserid());
    	//修改正式用户
    	singleObjectBO.executeUpdate(sql, sp);	
    	//修改临时用户表
    	sql="update app_temp_user set user_password=? where pk_user=? and nvl(dr,0)=0";
    	singleObjectBO.executeUpdate(sql, sp);	
	}
	
	@Override
	public UserToCorp[] queryCustMngUsers(String pk_corp) throws DZFWarpException {
		String pk_corpk = pk_corp;
		if(!StringUtil.isEmpty(pk_corpk)){
			String condition = " nvl(dr,0) = 0 and pk_corp = ? and nvl(ismanage,'N') = 'Y'";
			SQLParameter params = new SQLParameter();
			params.addParam(pk_corpk);
			return (UserToCorp[])singleObjectBO.queryByCondition(UserToCorp.class, condition, params);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Map<String, String> queryCorpSyByUser(String userid, List<CorpVO> list)
			throws DZFWarpException {
		//首先区分 普通 还是加盟商客户
		List<String> ptpks = new ArrayList<String>();//普通
		List<String> jmspks= new ArrayList<String>();//加盟商
		
		DZFBoolean ischannel;
		for(CorpVO corpvo : list){
			ischannel = corpvo.getIschannel();
			ischannel = ischannel == null ? DZFBoolean.FALSE : ischannel;
//			if(ischannel.booleanValue()){
//				jmspks.add(corpvo.getPk_corp());
//			}else{
				ptpks.add(corpvo.getPk_corp());
//			}
		}
		
		//构造sql
		SQLParameter sp = new SQLParameter();
		StringBuffer sf  = new StringBuffer();
		if(ptpks.size() > 0){
			sf.append("select max(period) period, pk_corp from ynt_qmcl where ");//pk_corp in (
			sf.append(SqlUtil.buildSqlForIn("pk_corp", ptpks.toArray(new String[0])));
			sf.append(" and isqjsyjz = 'Y' and nvl(dr,0) = 0 group by pk_corp ");
			
			if(jmspks.size() > 0){
				sf.append(" union ");
			}
		}
		
		if(jmspks.size() > 0){
			sf.append("select max(period) period, pk_corp from ynt_qmcl where ");//pk_corp in (
			sf.append(SqlUtil.buildSqlForIn("pk_corp", jmspks.toArray(new String[0])));
			sf.append(" and isgz = 'Y' and nvl(dr,0) = 0 group by pk_corp ");
		}

		Map<String, String> periodMap = (Map<String, String>) singleObjectBO.executeQuery(sf.toString(), sp, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				Map<String, String> periodMap = new HashMap<String, String>();
				while(rs.next()){
					periodMap.put(rs.getString("pk_corp"), rs.getString("period"));
				}
				return periodMap;
			}
		});
		return periodMap;
	}

    @Override
    public void saveErrorInfo(ImpErrorVO[] uvos,String pk_corp) throws DZFWarpException {
        singleObjectBO.insertVOArr(pk_corp, uvos);
    }

    /* (non-Javadoc)
     * @see com.dzf.service.sys.sys_power.IUserService#onImportSave(com.dzf.model.sys.sys_power.UserVO[], com.dzf.model.sys.sys_power.CorpVO, java.lang.String)
     */
    @Override
    public List<UserVO> onImportSave(UserVO[] uvos,CorpVO cvo,String loginUserid) throws DZFWarpException {
        List<UserVO> list = new ArrayList<UserVO>();
        List<ImpErrorVO> listError = new ArrayList<ImpErrorVO>();
        String pk_corp = cvo.getPk_corp();
        if(uvos != null && uvos.length > 0){
            HashMap<String, EmployeeVO> mapEmp = queryEmployee(pk_corp);
            HashMap<String, DepartmentVO> mapDept = queryDept(pk_corp);
            ImpErrorVO evo = null;
            for(UserVO uvo : uvos){
                try {
                    uvo.setPk_corp(pk_corp);
                    uvo.setCorpnm(cvo.getUnitname());
                    uvo.setPk_creatcorp(pk_corp);
                    uvo.setCrtcorp(cvo.getUnitname());
                    uvo.setUser_password("123abc!@#");// 默认密码为123abc(1: jlehfdffcfmohiag)
                    uvo.setAuthen_type("staticpwd");
                    uvo.setLangcode("simpchn");
                    uvo.setPwdlevelcode("update");
                    uvo.setPwdparam(new DZFDate().toString());
                    uvo.setLocked_tag(DZFBoolean.FALSE);
                    uvo.setDpwddate(new DZFDate());
                    if(!StringUtil.isEmpty(uvo.getEmcode())){
                        if(mapEmp != null){
                            EmployeeVO emvo = mapEmp.get(uvo.getEmcode());
                            if(emvo != null){
                                uvo.setPk_employee(emvo.getPk_employee());
                            }else{
                                throw new BusinessException("员工信息不存在");
                            }
                        }else {
                            throw new BusinessException("员工信息不存在");
                        }
                    }
                    
                    if(!StringUtil.isEmpty(uvo.getDeptname())){
                        if(mapDept != null){
                            DepartmentVO deptvo = mapDept.get(uvo.getDeptname());
                            if(deptvo != null){
                                uvo.setPk_department(deptvo.getPk_department());
                            }else{
                                throw new BusinessException("所属部门不存在");
                            }
                        }else {
                            throw new BusinessException("所属部门不存在");
                        }
                    }
                    save(uvo);
                    uvo.setUser_password(new Encode().decode(uvo.getUser_password()));
                    list.add(uvo);
                } catch (BusinessException e) {
                    evo = new ImpErrorVO();
                    evo.setVcode(uvo.getUser_code());
                    evo.setVname(SecretCodeUtils.deCode(uvo.getUser_name()));
                    evo.setDeptname(uvo.getDeptname());
                    evo.setCoperatorid(loginUserid);
                    evo.setDoperatedate(new DZFDate());
                    evo.setDimptime(new DZFDateTime());
                    evo.setVerror(e.getMessage());
                    evo.setVstatus("导入失败");
                    evo.setPk_corp(pk_corp);
                    evo.setVtype(uvo.getTableName());
                    listError.add(evo);
                } catch (Exception e) {
                    evo = new ImpErrorVO();
                    evo.setVcode(uvo.getUser_code());
                    evo.setVname(SecretCodeUtils.deCode(uvo.getUser_name()));
                    evo.setDeptname(uvo.getDeptname());
                    evo.setCoperatorid(loginUserid);
                    evo.setDoperatedate(new DZFDate());
                    evo.setDimptime(new DZFDateTime());
                    evo.setVerror(e.getMessage());
                    evo.setVstatus("导入失败");
                    evo.setPk_corp(pk_corp);
                    evo.setVtype(uvo.getTableName());
                    listError.add(evo);
                }
            }
            if(listError != null && listError.size() > 0){
                singleObjectBO.insertVOArr(pk_corp, listError.toArray(new ImpErrorVO[0]));
            }
        }
        return list;
    }
    
    private HashMap<String, EmployeeVO> queryEmployee(String pk_corp){
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        EmployeeVO[] vos = (EmployeeVO[]) singleObjectBO.queryByCondition(EmployeeVO.class, "pk_corp = ? ", params);
        HashMap<String, EmployeeVO> map = new HashMap<>();
        if(vos != null && vos.length > 0){
            for(EmployeeVO evo : vos){
                map.put(evo.getVemcode(), evo);
            }
            return map;
        }
        return null;
    }
    
    private HashMap<String, DepartmentVO> queryDept(String pk_corp){
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        DepartmentVO[] vos = (DepartmentVO[]) singleObjectBO.queryByCondition(DepartmentVO.class, "pk_corp = ? ", params);
        HashMap<String, DepartmentVO> map = new HashMap<>();
        if(vos != null && vos.length > 0){
            for(DepartmentVO dvo : vos){
                map.put(dvo.getDeptname(), dvo);
            }
            return map;
        }
        return null;
    }

    @Override
    public ImpErrorVO[] queryErrorInfo(String pk_corp) throws DZFWarpException {
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        String condition = "pk_corp = ? and vtype = 'sm_user' order by dimptime desc"; 
        ImpErrorVO[] vos = (ImpErrorVO[]) singleObjectBO.queryByCondition(ImpErrorVO.class,condition, params);
        return vos;
    }

    @Override
    public MsgremindsetVO queryPwdStrategy(String pk_corp) throws DZFWarpException {
        String condition = " nvl(dr,0) = 0 and pk_corp = ?";
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        MsgremindsetVO[] vos = (MsgremindsetVO[]) singleObjectBO.queryByCondition(MsgremindsetVO.class, condition, params);
        if(vos != null && vos.length > 0){
//            MsgremindsetVO vo = vos[0];
//            if(vo.getPwdstrategy() != null && vo.getPwdstrategy().booleanValue()){
//                return vo.getStrategynum();
//            }
            return vos[0];
        }
        return null;
    }


	@Override
	public KryDZFRelationVO queryKryDZFRelationVO(String kryuserid, String kryshopid) throws DZFWarpException {
		if(StringUtil.isEmpty(kryuserid) || StringUtil.isEmpty(kryshopid))
			return null;
		KryDZFRelationVO vo = null;
		String where = " nvl(dr,0) = 0 and  kryuserid = ? and kryshopid = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(kryuserid);
		sp.addParam(kryshopid);
		KryDZFRelationVO[] vos = (KryDZFRelationVO[])singleObjectBO.queryByCondition(KryDZFRelationVO.class, where, sp);
		if(vos!= null && vos.length>0)
			vo = vos[0];
		return vo;
	}

    @Override
    public void updateLoginPwd(UserVO vo) throws DZFWarpException {
        validateUserVO(vo);
        if (isUserCodeExist(vo.getUser_code(),vo.getCuserid()))
            throw new BusinessException("用户编码"+ vo.getUser_code()+ "已被使用，请使用别的用户编码\n");
        String phone = vo.getPhone();
        vo.setUser_password(new Encode().encode(vo.getUser_password()));
        singleObjectBO.update(vo, new String[]{"user_password"});
        
        String pwd = vo.getUser_password();
        String sql = "update app_temp_user set user_password = ?,phone = ? where pk_user = ?";
        SQLParameter params = new SQLParameter();
        params.addParam(pwd);
        params.addParam(phone);
        params.addParam(vo.getCuserid());
        singleObjectBO.executeUpdate(sql, params);
        updateEmployeeDept(vo);
    }

    @Override
    public boolean checkBusiness(String cuid,String pk_corp) throws DZFWarpException {
        SQLParameter sp = new SQLParameter();
        sp.addParam(cuid);
        sp.addParam(cuid);
        sp.addParam(pk_corp);
        StringBuffer sql = new StringBuffer();
        sql.append(" select pk_corp from (");
        sql.append(" select pk_corp from ynt_workflow where dealman = ? and isdeal = 'N' ");
        sql.append(" union all select pk_corp from ynt_task_b where vdealman = ? and (bstatus = 1 or bstatus = 2) ");
        sql.append(" )    ");
        sql.append(" where pk_corp = ?  ");
        return singleObjectBO.isExists(pk_corp, sql.toString(), sp);
    }

	@Override
	public UserVO queryUserJmVOByID(String userid) throws DZFWarpException {
		if(StringUtil.isEmpty(userid))
			return new UserVO();
		UserVO uservo = (UserVO)singleObjectBO.queryByPrimaryKey(UserVO.class, userid);
		if(uservo != null){
			uservo.setUser_password(null);
			uservo.setUser_name(SecretCodeUtils.deCode(uservo.getUser_name()));
		}else{
			uservo = new UserVO();
		}
		return uservo;
	}

    @Override
    public HashMap<String, UserVO> queryUserMap(String pk_corp, boolean isDel) throws DZFWarpException {
        if(StringUtil.isEmpty(pk_corp)){
            return new HashMap<>();
        }
        StringBuffer sql = new StringBuffer();
        SQLParameter params = new SQLParameter();
        sql.append(" pk_corp = ? ");
        params.addParam(pk_corp);
        if(!isDel){
            sql.append(" and nvl(dr,0) = 0");
        }
        UserVO[] vos = (UserVO[]) singleObjectBO.queryByCondition(UserVO.class, sql.toString(), params);
        HashMap<String, UserVO> map = new HashMap<>();
        if(vos != null && vos.length > 0){
            for(UserVO uvo : vos){
                uvo.setUser_password(null);
                uvo.setUser_name(SecretCodeUtils.deCode(uvo.getUser_name()));
                map.put(uvo.getCuserid(), uvo);
            }
        }
        return map;
    }
}
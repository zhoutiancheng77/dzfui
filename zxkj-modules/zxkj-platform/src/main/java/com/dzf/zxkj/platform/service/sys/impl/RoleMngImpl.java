package com.dzf.zxkj.platform.service.sys.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IRoleConstants;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.service.sys.IRoleMngService;
import com.dzf.zxkj.platform.service.sys.IVersionMngService;
import com.dzf.zxkj.platform.util.FunnodetreeCreate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("sys_roleadminserv")
@SuppressWarnings("all")
public class RoleMngImpl implements IRoleMngService {

    private SingleObjectBO singleObjectBO;
    
    @Autowired
    private IVersionMngService sys_funnodeversionserv;
    
    public SingleObjectBO getSingleObjectBO() {
        return singleObjectBO;
    }
    @Autowired
    public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
        this.singleObjectBO = singleObjectBO;
    }
    @Override
    public void delete(RoleVO vo, UserVO userVo) throws DZFWarpException {
        if(userVo == null) 
            throw new BusinessException("请登录!");
        if(! IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())){
            if(vo.getPk_corp().equalsIgnoreCase(IGlobalConstants.DefaultGroup)){
                throw new BusinessException("预置角色,不能删除!");
            }
            checkBeforeUpdata(vo,"删除");
        }
        if (isRoleHasUsers(vo.getPk_role(), null))
            throw new BusinessException("此角色已分配用户,不能删除.");
        if (isRoleHasFun(vo.getPk_role(), null))
            throw new BusinessException("此角色已分配权限,不能删除.");
        getSingleObjectBO().deleteObject(vo);
    }
    
    public DZFBoolean checkBeforeUpdata(RoleVO vo, String action) throws DZFWarpException {
        if(vo.getPk_corp().equalsIgnoreCase(IGlobalConstants.DefaultGroup)){
            throw new BusinessException("预置角色,不能"+action+"!");
        }
        return DZFBoolean.TRUE;
    }
    
    /**
     * 判断角色中是否有用户,如果corpPK为空则查询角色中所有用户
     */
    public boolean isRoleHasUsers(String rolePK, String corpPK) throws BusinessException {
        String sql = "select cuserid from sm_user_role where pk_role=? and nvl(dr,0) = 0 ";
        SQLParameter param = new SQLParameter();
        param.addParam(rolePK);
        if (corpPK != null && corpPK.length() > 0) {
            sql += "and pk_corp=?";
            param.addParam(corpPK);
        }
        Object obje = singleObjectBO.executeQuery(sql, param, new ColumnProcessor(1));
        if (obje != null)
            return true;
        else
            return false;
    }
    
    /**
     * 判断角色是否分配权限
     * @param rolePK
     * @param corpPK
     * @return
     * @throws BusinessException
     */
    public boolean isRoleHasFun(String rolePK, String corpPK) throws DZFWarpException {
        String sql = "select resource_data_id from sm_power_func where pk_role=? and nvl(dr,0) = 0 ";
        SQLParameter param = new SQLParameter();
        param.addParam(rolePK);
        if (corpPK != null && corpPK.length() > 0) {
            sql += "and pk_corp=?";
            param.addParam(corpPK);
        }
        Object obje = singleObjectBO.executeQuery(sql, param, new ColumnProcessor(1));
        if (obje != null)
            return true;
        else
            return false;
    }

    @Override
    public boolean exist(RoleVO vo) throws DZFWarpException {
        StringBuffer where = new StringBuffer(" nvl(dr,0) = 0  ");
        SQLParameter sp=new SQLParameter();
        if(vo.getRole_code() != null && vo.getRole_code().trim().length() > 0){
            where.append(" and role_code=?");
            sp.addParam(vo.getRole_code());
        }else if(vo.getRole_name() != null && vo.getRole_name().trim().length() > 0){
            where.append(" and role_name=? and roletype = ? and pk_corp = ?");
            sp.addParam(vo.getRole_name());
            sp.addParam(vo.getRoletype());
            sp.addParam(vo.getPk_corp());
        }
        if(vo.getPk_role() != null && vo.getPk_role().trim().length() > 0){
            where.append(" and pk_role !=?");
            sp.addParam(vo.getPk_role());
        }
        List<RoleVO> listVo =  (List<RoleVO>)getSingleObjectBO().retrieveByClause(RoleVO.class,where.toString(), sp);
        if(listVo != null && listVo.size()>0)
            return true;
        return false;
    }

    @Override
    public SysFunNodeVO[] queryFunNode(String role_id) throws DZFWarpException {
        String sql = "select f.*,s.pk_power from sm_funnode f"
                +" left join sm_power_func s on f.pk_funnode=s.resource_data_id ";
        SQLParameter sp = new SQLParameter();
        if(role_id != null){
            sql = sql+" and s.pk_role=?";
            sp.addParam(role_id);
        }
        String[] modules = new String[]{"sys_dzf","admin_kj","/DZF_KJ"};
        SysFunNodeVO[] bodyvos = new SysFunNodeVO[3];
        for(int i=0;i<3;i++){
            bodyvos[i] = new SysFunNodeVO();
            List<SysFunNodeVO> listVo = (List<SysFunNodeVO>) getSingleObjectBO().executeQuery(sql+" where nvl(f.dr,0)=0 and module='"+modules[i]+"'", sp, new BeanListProcessor(SysFunNodeVO.class));
            if(listVo!=null&&listVo.size()>0){
                SysFunNodeVO vo = (SysFunNodeVO) BDTreeCreator.createTree(
                        listVo.toArray(new SysFunNodeVO[0]), new FunnodetreeCreate());
                bodyvos[i].setChildren((SysFunNodeVO[]) DZfcommonTools.convertToSuperVO(vo
                        .getChildren()));
            }
        }
        return bodyvos;
    }

    @Override
    public SysFunNodeVO[] queryFunNode(String role_id, int roletype, CorpVO cvo) throws DZFWarpException {
        if(roletype < 1){
            throw new BusinessException("角色类型错误!");
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select f.*,s.pk_power from sm_funnode f");
        sql.append(" left join sm_power_func s on f.pk_funnode=s.resource_data_id ");
        SQLParameter sp = new SQLParameter();
        if(role_id != null){
            sql.append(" and s.pk_role=?");
            sp.addParam(role_id);
        }
        
        
//        String[] modules = new String[]{"sys_dzf","dzf_photo","admin_kj","dzf_kj","dzf_factory","dzf_kj","dzf_channel","dzf_fat"};
        String[] modules = new String[]{"sys_dzf","dzf_photo","admin_kj","dzf_kj","dzf_factory","dzf_kj"};
        sql.append(" where nvl(f.dr,0)=0 and module='").append(modules[roletype -1]).append("' ");
        if(roletype == IRoleConstants.ROLE_3){
            sql.append(" and f.funtype in (1,2)");
        }
        ///在线端和会计端，增加收费过滤 zpm
        if(cvo.getIschannel() == null || !cvo.getIschannel().booleanValue()){
            if(cvo.getIsaccountcorp() != null && cvo.getIsaccountcorp().booleanValue()){
                String[] funcodes = sys_funnodeversionserv.queryCorpVersion(cvo.getPk_corp());
                if(funcodes != null && funcodes.length>0){
                    String data = SqlUtil.buildSqlConditionForIn(funcodes);
                    sql.append(" AND f.pk_funnode in("+data+") ");
                }
            }
        }
        sql.append(" order by f.show_order");
//      for(int i=0;i<3;i++){
            List<SysFunNodeVO> listVo = (List<SysFunNodeVO>) getSingleObjectBO().executeQuery(sql.toString(), sp, new BeanListProcessor(SysFunNodeVO.class));
            if(listVo!=null&&listVo.size()>0){
                SysFunNodeVO vo = (SysFunNodeVO) BDTreeCreator.createTree(
                        listVo.toArray(new SysFunNodeVO[0]), new FunnodetreeCreate());
                return (SysFunNodeVO[]) DZfcommonTools.convertToSuperVO(vo.getChildren());
            }
//      }
            return null;
    }

    @Override
    public void sign(String pk_corp,RoleVO rolevo) throws DZFWarpException {
        String sql = "delete from sm_power_func where pk_role=? and pk_corp=?";
        SQLParameter sp = new SQLParameter();
        sp.addParam(rolevo.getPk_role());
        sp.addParam(pk_corp);
        getSingleObjectBO().executeUpdate(sql, sp);
        if(rolevo.getCkfnarr() != null && rolevo.getCkfnarr().length>0){
            List<SmPowerFuncVO> saveVOs = new ArrayList<SmPowerFuncVO>(rolevo.getCkfnarr().length);
            for(String fnId : rolevo.getCkfnarr()){
                SmPowerFuncVO vo = new SmPowerFuncVO();
                vo.setPk_role(rolevo.getPk_role());
                vo.setResource_data_id(fnId);
                vo.setIscommon_power(DZFBoolean.FALSE);
                vo.setOrgtypecode(1);//TODO
                vo.setPk_corp(pk_corp);
                saveVOs.add(vo);
            }
            getSingleObjectBO().insertVOArr(pk_corp, saveVOs.toArray(new SmPowerFuncVO[rolevo.getCkfnarr().length]));
        }
    }

    @Override
    public List<RoleVO> query(SysPowerConditVO conditvo)throws DZFWarpException {
        StringBuffer sb = new StringBuffer();
        sb.append("select sm_role.*,bd_corp.unitname as corpnm from sm_role sm_role");
        sb.append(" left join bd_corp bd_corp on bd_corp.pk_corp=sm_role.pk_corp");
        sb.append(" where nvl(sm_role.dr,0)=0");
        sb.append(" and (sm_role.pk_corp=? or sm_role.pk_corp=?)");
//      if(conditvo.getRoletype() == 3){
//          sb.append(" and (sm_role.roletype = 3 or sm_role.roletype = 4)");
//      }else if(conditvo.getRoletype() == 2){
//          sb.append(" and (sm_role.roletype =  2)");
//      }
        SQLParameter params = new SQLParameter();
        params.addParam(conditvo.getCorp_id());
        params.addParam(conditvo.getCrtcorp_id());
        if(conditvo.getIaccount() != null){
//          sb.append(" and sm_role.roletype in ("+IRoleConstants.ROLE_3+","+IRoleConstants.ROLE_4+")");
            if(conditvo.getRoletype()==IRoleConstants.ROLE_3){
                sb.append(" and sm_role.roletype = "+IRoleConstants.ROLE_3);
            }else if(conditvo.getRoletype()==IRoleConstants.ROLE_4){
                sb.append(" and sm_role.roletype = "+IRoleConstants.ROLE_4);
            }else{
                sb.append(" and sm_role.roletype in ("+IRoleConstants.ROLE_3+","+IRoleConstants.ROLE_4+")");
            }
            
            if(!StringUtil.isEmpty(conditvo.getRolenm())){
                sb.append(" and sm_role.role_name like '%"+conditvo.getRolenm()+"%'");
            }
            
            if(!StringUtil.isEmpty(conditvo.getRolecd())){
                sb.append(" and sm_role.role_code like '%"+conditvo.getRolecd()+"%'");
            }
        }
        if(conditvo.getIdata() != null){
            sb.append(" and sm_role.roletype in ("+IRoleConstants.ROLE_1+","+IRoleConstants.ROLE_2+")");
        }
        if(conditvo.getCompanyproperty() != null&&conditvo.getCompanyproperty()==1){//会计工厂
            sb.append(" and sm_role.roletype in ('"+IRoleConstants.ROLE_5+"')");
        }
        sb.append(" order by sm_role.roletype,sm_role.role_code asc");
        List<RoleVO> listVo = (List<RoleVO>) singleObjectBO.executeQuery(sb.toString(), params, new BeanListProcessor(RoleVO.class));
//      List<RoleVO> listVo =  (List<RoleVO>)singleObjectBO.retrieveByClause(RoleVO.class, sb.toString(), params);
        return listVo;
    }
    
    @Override
    public List<RoleVO> queryPowerRole(SysPowerConditVO conditvo)throws DZFWarpException {
        StringBuffer sb = new StringBuffer();
        sb.append("select sm_role.*,bd_corp.unitname as corpnm from sm_role sm_role");
        sb.append(" left join bd_corp bd_corp on bd_corp.pk_corp=sm_role.pk_corp");
        sb.append(" where nvl(sm_role.dr,0)=0");
//      sb.append(" and sm_role.pk_corp=? ");
        sb.append(" and (sm_role.pk_corp=? or sm_role.pk_corp=?)");
        SQLParameter params = new SQLParameter();
        params.addParam(conditvo.getCorp_id());
        params.addParam(conditvo.getCrtcorp_id());
        if(!conditvo.getCorp_id().equals(IGlobalConstants.DefaultGroup)){
            if(conditvo.getIaccount() != null){
//              sb.append(" and sm_role.roletype in ("+IRoleConstants.ROLE_3+","+IRoleConstants.ROLE_4+")");
                if(conditvo.getRoletype()==IRoleConstants.ROLE_3){
                    sb.append(" and sm_role.roletype = "+IRoleConstants.ROLE_3);
                }else if(conditvo.getRoletype()==IRoleConstants.ROLE_4){
                    sb.append(" and sm_role.roletype = "+IRoleConstants.ROLE_4);
                }else{
                    sb.append(" and sm_role.roletype in ("+IRoleConstants.ROLE_3+","+IRoleConstants.ROLE_4+")");
                }
                
                if(!StringUtil.isEmpty(conditvo.getRolenm())){
                    sb.append(" and sm_role.role_name like '%"+conditvo.getRolenm()+"%'");
                }
                
                if(!StringUtil.isEmpty(conditvo.getRolecd())){
                    sb.append(" and sm_role.role_code like '%"+conditvo.getRolecd()+"%'");
                }
            }
            if(conditvo.getIdata() != null){
                sb.append(" and sm_role.roletype in ("+IRoleConstants.ROLE_2+")");
            }
            if(conditvo.getCompanyproperty() != null&&conditvo.getCompanyproperty()==1){//会计工厂
                sb.append(" and sm_role.roletype in ('"+IRoleConstants.ROLE_5+"')");
            }
        }else{
//            sb.append(" and sm_role.roletype != '"+IRoleConstants.ROLE_8+"' and sm_role.roletype != '"+IRoleConstants.ROLE_6+"'");
//            sb.append(" and sm_role.roletype not in (?,?,?)");
//            params.addParam(IRoleConstants.ROLE_6);
//            params.addParam(IRoleConstants.ROLE_8);
//            params.addParam(IRoleConstants.ROLE_9);
            sb.append(" and sm_role.roletype in (?,?,?,?,?)");
            params.addParam(IRoleConstants.ROLE_1);
            params.addParam(IRoleConstants.ROLE_2);
            params.addParam(IRoleConstants.ROLE_3);
            params.addParam(IRoleConstants.ROLE_4);
            params.addParam(IRoleConstants.ROLE_5);
        }
        sb.append(" order by sm_role.role_code asc");
        List<RoleVO> listVo = (List<RoleVO>) singleObjectBO.executeQuery(sb.toString(), params, new BeanListProcessor(RoleVO.class));
//      List<RoleVO> listVo =  (List<RoleVO>)singleObjectBO.retrieveByClause(RoleVO.class, sb.toString(), params);
        return listVo;
    }


    @Override
    public RoleVO save(RoleVO vo)throws DZFWarpException {
        if (isRoleCodeExist(vo.getRole_code()))
            throw new BusinessException("角色编码已被使用!");
        if (isRoleNameExist(vo.getRole_name(), vo.getPk_corp(),vo.getRoletype()))
            throw new BusinessException("角色名称已被使用!");
        vo = (RoleVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
        return vo;
    }

    @Override
    public void update(RoleVO vo,UserVO userVo)throws DZFWarpException {
        if(userVo == null) 
            throw new BusinessException("请登录!");
        if(! IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())){
            checkBeforeUpdata(vo,"修改");
        }
        RoleVO oldRoleVO = (RoleVO) singleObjectBO.queryByPrimaryKey(RoleVO.class, vo.getPrimaryKey());
        if(!oldRoleVO.getRole_code().equals(vo.getRole_code())){
            if (isRoleCodeExist(vo.getRole_code()))
                throw new BusinessException("角色编码已被使用!");
        }
        if(!oldRoleVO.getRole_name().equals(vo.getRole_name())){
            if (isRoleNameExist(vo.getRole_name(), vo.getPk_corp(),vo.getRoletype()))
                throw new BusinessException("角色名称已被使用!");
        }else if(!oldRoleVO.getRoletype().equals(vo.getRoletype())){
            if (isRoleNameExist(vo.getRole_name(), vo.getPk_corp(),vo.getRoletype()))
                throw new BusinessException("角色名称已被使用!");
        }
        singleObjectBO.update(vo, new String[]{"role_code","role_name","role_memo","roletype","ischat"});
    }
    
    public boolean isRoleCodeExist(String roleCode) throws DZFWarpException {
        String sql = "select role_code from sm_role where role_code=? and nvl(dr,0)=0";
        SQLParameter param = new SQLParameter();
        param.addParam(roleCode);
        Object obje = singleObjectBO.executeQuery(sql,param, new ColumnProcessor(1));
        if (obje != null)
            return true;
        else
            return false;
    }

    public boolean isRoleNameExist(String roleName, String corpPK,Integer roletype) throws DZFWarpException {
        String sql = "select role_code from sm_role where role_name=? and pk_corp = ? and roletype = ? and nvl(dr,0)=0";
        SQLParameter param = new SQLParameter();
        param.addParam(roleName);
        param.addParam(corpPK);
        param.addParam(roletype);
        Object obje = singleObjectBO.executeQuery(sql,param, new ColumnProcessor(1));
        if (obje != null)
            return true;
        else
            return false;
    }
    @Override
    public RoleVO[] queryKeyRole() throws DZFWarpException {
        String condition = " nvl(dr,0)=0 and nvl(keyrole,'N')='Y' and roletype = "+IRoleConstants.ROLE_4;
        RoleVO[] vos = (RoleVO[]) singleObjectBO.queryByCondition(RoleVO.class, condition, new SQLParameter());
        return vos;
    }
    @Override
    public RoleVO queryRoleBycode(String rolecode) throws DZFWarpException {
        String condition = " nvl(dr,0)=0 and role_code =? ";
        SQLParameter param = new SQLParameter();
        param.addParam(rolecode);
        RoleVO[] roles = (RoleVO[]) singleObjectBO.queryByCondition(RoleVO.class, condition, param);
        if(roles != null && roles.length > 0){
            return roles[0];
        }
        return null;
    }
    @Override
    public RoleVO[] queryByCondition(String condition)
            throws DZFWarpException {
        RoleVO[] roles = (RoleVO[]) singleObjectBO.queryByCondition(RoleVO.class, condition, null);
        if(roles != null && roles.length > 0){
            return roles;
        }
        return null;
    }
    @Override
    public RoleVO queryRoleByID(String pk_role) throws DZFWarpException {
        String condition = " nvl(dr,0)=0 and  pk_role =? ";
        SQLParameter param = new SQLParameter();
        param.addParam(pk_role);
        RoleVO[] roles = (RoleVO[]) singleObjectBO.queryByCondition(RoleVO.class, condition, param);
        if(roles != null && roles.length > 0){
            return roles[0];
        }
        return null;
    }
    /**
     * 会计工厂角色权限调用
     * 如果roleCorp是000001:走原理的逻辑
     * 如果roleCorp不是000001：看userCreateCorp
     * 
     * 如果userCreateCorp是000001：能读取到的功能节点是集团预制的角色分配的权限集合
     * 如果userCreateCorp不是000001，说明是会计工厂自制用户，能读取到公司=userCreateCorp的角色权限
     * @param userCreateCorp 当前操作用户的创建公司
     * @return
     * @throws DZFWarpException
     */
    @Override
    public SysFunNodeVO[] queryFunNode(String role_id, int roletype,String roleCorp, String userCreateCorp) throws DZFWarpException {
        if (roletype < 1) {
            throw new BusinessException("角色类型错误!");
        }
        StringBuffer sb=new StringBuffer(" select f.*,s.pk_power from sm_funnode f left join sm_power_func s on f.pk_funnode=s.resource_data_id ");
        SQLParameter sp = new SQLParameter();
        if (role_id != null) {
            sb.append(" and s.pk_role=? ");
            sp.addParam(role_id);
        }
        String[] modules = new String[] { "sys_dzf", "dzf_photo", "admin_kj", "dzf_kj", "dzf_factory" };
        
        sb.append(" where nvl(f.dr,0)=0 and module='" + modules[roletype - 1] + "' ");
        if(!IDefaultValue.DefaultGroup.equals(roleCorp)){
            sb.append(" and f.pk_funnode in (select distinct resource_data_id from sm_power_func where nvl(dr,0)=0  ");
            sb.append(" and pk_role in(select pk_role from sm_role where roletype=? and nvl(dr,0)=0 and pk_corp=?)  and pk_corp = ?) ");
            sp.addParam(5);
            sp.addParam(userCreateCorp);
            sp.addParam(userCreateCorp);
        }
        
        List<SysFunNodeVO> listVo = (List<SysFunNodeVO>) getSingleObjectBO().executeQuery(sb.toString(), sp, new BeanListProcessor(SysFunNodeVO.class));
        if (listVo != null && listVo.size() > 0) {
            SysFunNodeVO vo = (SysFunNodeVO) BDTreeCreator.createTree(listVo.toArray(new SysFunNodeVO[0]), new FunnodetreeCreate());
            return (SysFunNodeVO[]) DZfcommonTools.convertToSuperVO(vo.getChildren());
        }
        return null;
    }
    
    //角色封存-解封
        public void updateSeal(RoleVO vo,UserVO userVo){
            if(userVo == null) 
                throw new BusinessException("请登录!");
            if(vo.getSeal().booleanValue()){
                
                if(! IDefaultValue.DefaultGroup.equals(userVo.getPk_corp())){
                    checkBeforeUpdata(vo,"封存");
                }
                
                if (isRoleHasUsers(vo.getPk_role(), null))
                    throw new BusinessException("此角色已分配用户,不能封存.");
                if (isRoleHasFun(vo.getPk_role(), null))
                    throw new BusinessException("此角色已分配权限,不能封存.");
            }
            singleObjectBO.update(vo, new String[]{"seal"});
        }
}

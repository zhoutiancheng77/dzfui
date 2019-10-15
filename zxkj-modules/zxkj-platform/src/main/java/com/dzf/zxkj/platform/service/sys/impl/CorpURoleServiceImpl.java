package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IRoleCodeCont;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpRoleVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserRoleVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.ICorpURoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 我的客户-权限分配（派工）
 * 
 * @author
 * 
 */
@Service("corpUserRoleImpl")
public class CorpURoleServiceImpl implements ICorpURoleService {

    @Autowired
    private SingleObjectBO singleObjectBO = null;

    @Autowired
    private ICorpService corpService;

    @Override
    public void saveRoleUser(String loginCorp, CorpRoleVO[] listR) throws DZFWarpException {
        ArrayList<UserRoleVO> list = new ArrayList<>();
        UserRoleVO uRoleVO = null;
        HashMap<String, UserRoleVO> map = queryRoleUser(null, listR);
        StringBuffer key = null;
        for (CorpRoleVO cRoleVO : listR) {
            key = new StringBuffer();
            uRoleVO = new UserRoleVO();
            key.append(cRoleVO.getPk_corp()).append("_").append(cRoleVO.getCuserid()).append("_")
                    .append(cRoleVO.getPk_role());
            if (!map.containsKey(key.toString())) {
                uRoleVO.setPk_corp(cRoleVO.getPk_corp());
                uRoleVO.setCuserid(cRoleVO.getCuserid());
                uRoleVO.setPk_role(cRoleVO.getPk_role());
                uRoleVO.setRole_code(cRoleVO.getRole_code());
                list.add(uRoleVO);
            }
        }
        if (list != null && list.size() > 0) {
            singleObjectBO.insertVOArr(loginCorp, list.toArray(new UserRoleVO[0]));
            updateCorpUser(loginCorp, list.toArray(new UserRoleVO[0]));
        }
    }

    /**
     * 查询用户客户已分配的角色
     * 
     * @param pk_corp
     * @param crvos
     * @return
     */
    private HashMap<String, UserRoleVO> queryRoleUser(String pk_corp, CorpRoleVO[] crvos) {
        String sqlin = buildSqlForIn("pk_corp", crvos);
        String condition = "nvl(dr,0) = 0 and " + sqlin;
        UserRoleVO[] urvos = (UserRoleVO[]) singleObjectBO.queryByCondition(UserRoleVO.class, condition, null);
        HashMap<String, UserRoleVO> map = new HashMap<>();
        if (urvos != null && urvos.length > 0) {
            StringBuffer key = null;
            for (UserRoleVO urvo : urvos) {
                key = new StringBuffer();
                key.append(urvo.getPk_corp()).append("_").append(urvo.getCuserid()).append("_")
                        .append(urvo.getPk_role());
                if (!map.containsKey(key.toString())) {
                    map.put(key.toString(), urvo);
                }
            }
        }
        return map;
    }

    @Override
    public ArrayList<CorpRoleVO> queryUserPower(String loginCorp, String[] corpids) throws DZFWarpException {
        String sqlIn = SqlUtil.buildSqlForIn("ur.pk_corp", corpids);
        StringBuffer sql = new StringBuffer();
        SQLParameter param = new SQLParameter();
        sql.append(" select ur.cuserid,ur.pk_corp,ur.pk_role,ur.pk_user_role,su.user_code,su.user_name,");
        sql.append(" corp.innercode,corp.unitname,role.role_name,role.role_code,dept.deptname");
        sql.append(" from sm_user_role ur");
        sql.append(" left join sm_user su on ur.cuserid = su.cuserid");
        sql.append(" left join ynt_department dept on dept.pk_department = su.pk_department");
        sql.append(" left join bd_corp corp on ur.pk_corp = corp.pk_corp");
        sql.append(" left join sm_role role on ur.pk_role = role.pk_role");
        sql.append(" where nvl(ur.dr,0) = 0 and nvl(su.dr,0) = 0 and nvl(corp.dr,0) = 0 ");
        sql.append(" and ").append(sqlIn);
        sql.append(" and ur.cuserid in (select cuserid from sm_user where pk_corp = ? and pk_creatcorp = ? ) ");
        param.addParam(loginCorp);
        param.addParam(loginCorp);
        sql.append(" order by corp.innercode");
        ArrayList<CorpRoleVO> list = (ArrayList<CorpRoleVO>) singleObjectBO.executeQuery(sql.toString(), param,
                new BeanListProcessor(CorpRoleVO.class));
        return list;
    }

    private static String buildSqlForIn(final String fieldname, final CorpRoleVO[] crvos) {
        StringBuffer sbSQL = new StringBuffer();
        sbSQL.append("(" + fieldname + " IN ( ");
        int len = crvos.length;
        // 循环写入条件
        for (int i = 0; i < len; i++) {
            String pk_corp = crvos[i].getPk_corp();
            if (!StringUtil.isEmpty(pk_corp)) {
                sbSQL.append("'").append(pk_corp).append("'");
                // 单独处理 每个取值后面的",", 对于最后一个取值后面不能添加"," 并且兼容 oracle 的 IN 254 限制。每
                // 200 个 数据 or 一次。时也不能添加","
                if (i != (len - 1) && !(i > 0 && (i + 1) % 200 == 0)) {
                    sbSQL.append(",");
                }
            } else {
                return null;
            }

            // 兼容 oracle 的 IN 254 限制。每 200 个 数据 or 一次。
            if (i > 0 && (i + 1) % 200 == 0 && i != (len - 1)) {
                sbSQL.append(" ) OR ").append(fieldname).append(" IN ( ");
            }
        }
        sbSQL.append(" )) ");
        return sbSQL.toString();
    }

    @Override
    public void delRoleUser(CorpRoleVO paramvo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        sql.append(" delete from sm_user_role");
        sql.append(" where pk_corp = ? and pk_role = ? and cuserid = ? and pk_user_role = ? ");
        SQLParameter params = new SQLParameter();
        params.addParam(paramvo.getPk_corp());
        params.addParam(paramvo.getPk_role());
        params.addParam(paramvo.getCuserid());
        params.addParam(paramvo.getPk_user_role());
        singleObjectBO.executeUpdate(sql.toString(), params);
    }

    @Override
    public void updateRoleUser(String loginCorp,CorpRoleVO crvo) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        sql.append(" update sm_user_role set cuserid = ? ");
        sql.append(" where pk_corp = ? and pk_role = ? and cuserid = ? and pk_user_role = ? ");
        SQLParameter params = new SQLParameter();
        params.addParam(crvo.getCduserid());
        params.addParam(crvo.getPk_corp());
        params.addParam(crvo.getPk_role());
        params.addParam(crvo.getCuserid());
        params.addParam(crvo.getPk_user_role());
        singleObjectBO.executeUpdate(sql.toString(), params);
        UserRoleVO[] urvos = new UserRoleVO[1];
        UserRoleVO urvo = new UserRoleVO();
        urvo.setPk_corp(crvo.getPk_corp());
        urvo.setCuserid(crvo.getCduserid());
        urvo.setRole_code(crvo.getRole_code());
        urvos[0] = urvo;
        updateCorpUser(loginCorp, urvos);
    }

    @Override
    public void updateCorpUser(String loginCorp, UserRoleVO[] urvos) throws DZFWarpException {
        CorpVO lcvo = corpService.queryByPk(loginCorp);
        if(lcvo != null && lcvo.getIschannel() != null && lcvo.getIschannel().booleanValue()){
            if(urvos != null && urvos.length > 0){
                String sqls = "update bd_corp set vsuperaccount = ? where pk_corp = ? and fathercorp = ?";
                List<SQLParameter> list=new ArrayList<SQLParameter>();
                for(UserRoleVO urvo : urvos){
                    SQLParameter parameter = new SQLParameter();
                    if(!StringUtil.isEmpty(urvo.getRole_code()) && urvo.getRole_code().equals(IRoleCodeCont.jms07)){
                        parameter.addParam(urvo.getCuserid());
                        parameter.addParam(urvo.getPk_corp());
                        parameter.addParam(loginCorp);
                        list.add(parameter);
                    }
                }
                if(list != null && list.size() > 0){
                    singleObjectBO.executeBatchUpdate(sqls, list.toArray(new SQLParameter[0]));
                }
            }
        }
    }

    @Override
    public List<UserRoleVO> queryUserRoleVO(String pk, String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk) || StringUtil.isEmpty(pk_corp))
            return null;
        SQLParameter sp = new SQLParameter();
        StringBuffer sf = new StringBuffer();
        sf.append(" select ur.* from sm_user_role ur ");
        sf.append(" left join sm_role sr on sr.pk_role = ur.pk_role and nvl(sr.dr,0) = 0");
        sf.append(" where ur.cuserid = ? and ur.pk_corp = ? and nvl(ur.dr,0) = 0 ");
        sp.addParam(pk);
        sp.addParam(pk_corp);
        List<UserRoleVO> list = (List<UserRoleVO>) singleObjectBO.executeQuery(sf.toString(), sp,
                new BeanListProcessor(UserRoleVO.class));
        return list;
    }

}
package com.dzf.zxkj.platform.service.common.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.util.SystemUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Service("ic_securityserv")

public class SecurityServiceImpl implements ISecurityService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IUserService userService;

    public void checkSecurityForSave(String aa,String aa1, String pk_corp, String logincorp, String cuserid) throws DZFWarpException{

    }
	private void checkSecurity(Class className, String primaryKey, String pk_corp, String logincorp, String cuserid) {

		if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(logincorp) || !pk_corp.equals(logincorp)) {
			throw new BusinessException("出现数据无权问题，无权操作！");
		}

		if (!StringUtil.isEmpty(cuserid)) {
			Set<String> powerCorpSet = userService.querypowercorpSet(cuserid);
			if (powerCorpSet == null || !powerCorpSet.contains(logincorp)) {
				throw new BusinessException("出现数据无权问题，无权操作！");
			}
		}

		if (!StringUtil.isEmpty(primaryKey) && className != null) {
			SuperVO svo1 = null;
			try {
				svo1 = (SuperVO) className.newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			String sql = " select " + svo1.getPKFieldName() + ",pk_corp from " + svo1.getTableName()
					+ " where nvl(dr,0) = 0 and  " + svo1.getPKFieldName() + " = ?";
			SQLParameter sp = new SQLParameter();
			sp.addParam(primaryKey);
			List<SuperVO> list = (List<SuperVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(className));
			if (list == null || list.size() == 0) {
				throw new BusinessException("数据被删除，请刷新后操作！");
			} else {
				if (!logincorp.equals(list.get(0).getAttributeValue("pk_corp"))) {
					throw new BusinessException("出现数据无权问题，无权操作！");
				}
			}
		}
	}

	public boolean isExists(String pk_corp, SuperVO supervo) throws DAOException {

		if (supervo == null || StringUtil.isEmpty(supervo.getPrimaryKey()))
			return false;

		SQLParameter sp = new SQLParameter();
		sp.addParam(supervo.getPrimaryKey());
		String sql = " select " + supervo.getPKFieldName() + " from " + supervo.getTableName()
				+ " where nvl(dr,0) = 0 and  " + supervo.getPKFieldName() + " =?";
		return singleObjectBO.isExists(pk_corp, sql, sp);
	}


	@Override
	public void checkSecurityForSave(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurity(null, null, pk_corp, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForDelete(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurity(null, null, pk_corp, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForOther(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurity(null, null, pk_corp, logincorp, cuserid);
	}

    public void checkSecurityData(SuperVO[] vos,String[] corps, String cuserid,boolean isCheckData) {
        List<String> corpList = new ArrayList<>();
	    if(corps != null && corps.length >0){
            corpList = Arrays.asList(corps);
        }else{
            if(vos != null && vos.length >0){
                if(isCheckData){
                    checkData(vos);
                }
                corpList = new ArrayList<>();
                String pk_corp = "";
                for(SuperVO vo:vos){
                    pk_corp = (String)vo.getAttributeValue("pk_corp");
                    if(!corpList.contains(pk_corp)){
                        corpList.add(pk_corp);
                    }
                }
            }
        }
        checkSecurityForCorp(corpList,cuserid);
    }

    // 校验数据是否存在
    private void checkData(SuperVO[] vos){
        if(vos == null || vos.length==0){
            return ;
        }
        List<String> pkList = new ArrayList<>();
        String pk = null;
        for(SuperVO vo:vos){
            pk = (String)vo.getAttributeValue(vos[0].getPKFieldName());
            if(!StringUtil.isEmpty(pk)){
                pkList.add(pk);
            }
        }
        if(pkList.size() ==0){
            return;
        }
        StringBuffer sql = new StringBuffer(" select count(0) from ");
        sql.append( vos[0].getTableName());
        sql.append(" where nvl(dr,0) = 0 and " );
        sql.append(SqlUtil.buildSqlForIn(vos[0].getPKFieldName(), pkList.toArray(new String[pkList.size()])));

        int count =  new Integer(singleObjectBO.executeQuery(sql.toString(), null, new ColumnProcessor()).toString()).intValue();
       if(count != vos.length){
           throw new BusinessException("出现数据问题，请重新操作！");
       }
    }
    private void checkSecurityForCorp(List<String> corpList,String cuserid){

	    if(corpList == null || corpList.size()==0){
            throw new BusinessException("出现数据无权问题，无权操作！");
        }
        for(String corp :corpList){
            if(StringUtil.isEmpty(corp)){
                throw new BusinessException("出现数据无权问题，无权操作！");
            }
        }
        if (StringUtil.isEmpty(cuserid)) {
            cuserid = SystemUtil.getLoginUserId();
        } else {
            //校验用户是否存在
            UserVO uservo =userService.queryUserById(cuserid);
            if(uservo == null){
                throw new BusinessException("出现用户无权问题，无权操作！");
            }
        }
        boolean isHav =userService.isExistCorpPower(corpList,cuserid);
        if(!isHav){
            throw new BusinessException("出现数据无权问题，无权操作！");
        }
    }
}

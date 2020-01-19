package com.dzf.zxkj.platform.service.common.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
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

@Service("ic_securityserv")

public class SecurityServiceImpl implements ISecurityService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IUserService userService;

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

package com.dzf.zxkj.platform.service.common.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
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
import java.util.Map;
import java.util.stream.Collectors;

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
                corpList = Arrays.stream(vos).filter(item -> !StringUtil.isEmpty((String)item.getAttributeValue("pk_corp")))
                        .map(item -> {
                            return (String)item.getAttributeValue("pk_corp");
                        }).collect(Collectors.toList());
            }
        }
        checkSecurityForCorp(corpList,cuserid);
    }

    // 校验数据是否存在
    private void checkData(SuperVO[] vos){
        if(vos == null || vos.length==0){
            return ;
        }
        Map<String,List<SuperVO>> map = DZfcommonTools.hashlizeObject( Arrays.asList(vos), new String[]{"pk_corp"});
        String pk_corp = null;
        List<SuperVO> list= null;
        for (Map.Entry<String, List<SuperVO>> entry : map.entrySet()) {
            pk_corp = entry.getKey();
            if(StringUtil.isEmpty(pk_corp))
                throw new BusinessException("出现数据中公司不存在问题！");
            list = entry.getValue();
            if(list == null || list.isEmpty())
                continue;
            String pkFieldName =list.get(0).getPKFieldName();
            List<String> pkList =list.stream().filter(item -> !StringUtil.isEmpty((String)item.getAttributeValue(pkFieldName)))
                    .map(item -> {
                        return (String)item.getAttributeValue(pkFieldName);
                    }).collect(Collectors.toList());
//        pkList = pkList.stream().distinct().filter(v -> !StringUtil.isEmpty(v)).collect(Collectors.toList());
            if(pkList.size() ==0){
                return;
            }
            StringBuffer sql = new StringBuffer(" select count(0) from ");
            SQLParameter sp = new SQLParameter();
            sql.append(list.get(0).getTableName());
            sp.addParam(pk_corp);
            sql.append(" where nvl(dr,0) = 0 and pk_corp = ? and " );
            sql.append(SqlUtil.buildSqlForIn(pkFieldName, pkList.toArray(new String[pkList.size()])));

            int count =  new Integer(singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor()).toString()).intValue();
            if(count != list.size()){
                throw new BusinessException("出现数据问题，请重新操作！");
            }
        }
    }
    private void checkSecurityForCorp(List<String> corpList,String cuserid){

        corpList = corpList.stream().distinct().filter(v -> !StringUtil.isEmpty(v)).collect(Collectors.toList());
        if(corpList == null || corpList.size()==0){
            return;
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

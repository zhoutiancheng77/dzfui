package com.dzf.zxkj.app.service.pub.impl;

import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.workbench.BsWorkbenchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service("userPubService")
public class UserPubServiceImpl implements IUserPubService {
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Override
    public UserVO queryUserVOId(String account_id) {
        if(StringUtil.isEmpty(account_id)){
            return  null;
        }
        String sql = " select * from sm_user where nvl(dr,0)=0 and unifiedid = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(account_id);
        List<UserVO> list = (ArrayList<UserVO>)singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(UserVO.class));
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }else {
            throw new BusinessException("您的用户不存在，请注册登录!");
        }


    }
}

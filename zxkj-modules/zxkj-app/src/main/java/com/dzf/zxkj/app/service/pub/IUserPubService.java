package com.dzf.zxkj.app.service.pub;

import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;

public interface IUserPubService {
    //用过uuid查用户
    public UserVO queryUserVOId(String account_id);
    //通过账号查用户
    public UserVO queryUserVObyCode(String usercode);
    //更新用户uuid
    public UserVO updateUserUnifiedid(UserVO uservo);
    //查询临时用户
    public TempUserRegVO queryTempUser(String pk_user);

    public UserVO saveRegisterCorpSWtch(UserBeanVO userBean,String unifiedid,UserVO olduserVO);
}

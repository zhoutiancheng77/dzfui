package com.dzf.zxkj.app.service.pub.impl;

import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.user.TempUserRegVO;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.login.IAppDemoCorpService;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.AppQueryUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.Encode;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service("userPubService")
public class UserPubServiceImpl implements IUserPubService {
    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IAppPubservice apppubservice;

    @Autowired
    private IAppDemoCorpService democorpser;

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
//            throw new BusinessException("您的用户不存在，请注册登录!");
            return null;
        }


    }


    public UserVO queryUserVObyCode(String usercode){
        if(StringUtil.isEmpty(usercode)){
            return  null;
        }
        String sql = " select * from sm_user where nvl(dr,0)=0 and user_code = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(usercode);
        List<UserVO> list = (ArrayList<UserVO>)singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(UserVO.class));
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }else {
           return null;
        }
    }

    public UserVO updateUserUnifiedid(UserVO uservo){

        singleObjectBO.update(uservo,new String[]{"unifiedid"});
        return uservo;
    }

    @Override
    public TempUserRegVO queryTempUser(String pk_user) {
        String sql = " select * from app_temp_user where nvl(dr,0)=0 and pk_user = ? ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_user);
        List<TempUserRegVO> list = (ArrayList<TempUserRegVO>)singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(TempUserRegVO.class));
        if(list!=null&&list.size()>0){
            return  list.get(0);
        }else {
            return null;
        }
    }


    @Override
    public UserVO saveRegisterCorpSWtch(UserBeanVO userBean,String unifiedid,UserVO olduserVO)  {
        LoginResponseBeanVO bean = new LoginResponseBeanVO();
        TempUserRegVO tempuservo = null;
        List<TempUserRegVO>  tempList = apppubservice.getTempList(userBean.getUsercode());

        if(tempList == null || tempList.size()  == 0){
            // 开始用户注册
            tempuservo = new TempUserRegVO();
            String password = apppubservice.decryptPwd(userBean.getSystype(), userBean.getPassword());

            tempuservo.setUser_code(userBean.getUsercode());
            tempuservo.setUser_password(password);
            tempuservo.setUser_name(userBean.getUsername());
            tempuservo.setApp_user_qq(userBean.getPhone());
            tempuservo.setPhone(userBean.getPhone());
            tempuservo.setIstate(IConstant.TWO);
           tempuservo.setUser_password(new Encode().encode(userBean.getPassword()));
            SuperVO vo = singleObjectBO.saveObject(Common.tempidcreate, tempuservo);
            // 添加公司信息
            CorpVO[] cpvos = apppubservice.getNoLinkCorp(userBean.getPhone(),userBean.getSourcesys());
            if (cpvos != null && cpvos.length > 0) {
                bean.setCpvos(cpvos);
            }

            // 查询demo公司
            CorpVO[] corpvo = AppQueryUtil.getInstance().getDemoCorpMsg();
            String usercode = userBean.getUsercode();
            String username = userBean.getUsername();
            String accountid = vo.getPrimaryKey();
            democorpser.sendDemoCorp(bean, usercode, username, accountid, userBean.getVdevicdmsg(), corpvo,userBean.getSourcesys(),"");
        }else{//临时公司存在的时候
            tempuservo = tempList.get(0);
        }

        return create(tempuservo,null,unifiedid,olduserVO);
    }


    private UserVO create(TempUserRegVO tempUserRegVO,TempCorpVO corpVO,String unifiedid, UserVO olduserVO) throws DZFWarpException {
            String pk_user = null;
            UserVO userVO = olduserVO == null ?createUserVO( tempUserRegVO,corpVO,unifiedid) : olduserVO;
            pk_user = userVO.getPrimaryKey();

            tempUserRegVO.setPk_user(pk_user);
            singleObjectBO.update(tempUserRegVO);
            return userVO;
    }

    private UserVO createUserVO(TempUserRegVO uservo,TempCorpVO tempCorpVO11,String unifiedid) {
        UserVO userVO = new UserVO();
        userVO.setAble_time(new DZFDate());
        userVO.setAuthen_type("staticpwd");
        userVO.setIsca(DZFBoolean.FALSE);
        userVO.setLangcode("simpchn");
        userVO.setLocked_tag(DZFBoolean.FALSE);
        userVO.setPk_corp(Common.tempidcreate);
        userVO.setPwdlevelcode("junior");
        userVO.setPwdparam(new DZFDate().toString());
        userVO.setPwdtype(0);
        userVO.setUser_code(uservo.getUser_code());
        userVO.setUser_name(CodeUtils1.enCode(uservo.getUser_name()));
        userVO.setUser_note(userVO.getUser_code());
        userVO.setUser_password(uservo.getUser_password());
        //userVO.setCheckcode(uservo.getUser_code());
        userVO.setBappuser(DZFBoolean.TRUE);
        userVO.setIstate(2);
        userVO.setPk_creatcorp(Common.tempidcreate);
        userVO.setIsmanager(DZFBoolean.TRUE);
        userVO.setUnifiedid(unifiedid);
        //userVO.setPk_tempcorp(vo.getPrimaryKey());
        userVO = (UserVO) singleObjectBO.saveObject("webuse", userVO);
        return userVO;
    }
}

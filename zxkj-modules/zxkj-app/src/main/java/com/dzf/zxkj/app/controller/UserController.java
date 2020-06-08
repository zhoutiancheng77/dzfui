package com.dzf.zxkj.app.controller;

import com.dzf.zxkj.app.model.resp.bean.RegisterRespBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.org.IOrgService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app/usersvlt")
public class UserController extends  BaseAppController{


    @Autowired
    private IUserPubService userPubService;
    @Autowired
    private IAppCorpService corpservice;

    @RequestMapping("/doAction")
    public ReturnData<ResponseBaseBeanVO> doAction(UserBeanVO userBean,String corp,String tcorp,String cname,String icmsg,String acode,String hcorp) {
//        CommonServ common = new CommonServ();
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        UserVO uservo = userPubService.queryUserVOId(userBean.getAccount_id());
        userBean.setAccount_id(uservo.getCuserid());
        userBean.setPk_corp(corp);
        userBean.setPk_tempcorp(tcorp);
        userBean.setCorpname(cname);
        userBean.setIsconfirmsg(icmsg);
        userBean.setActivecode(acode);
        userBean.setHandcorp(hcorp);
//        UserBeanVO userBean = (UserBeanVO) DzfTypeUtils.cast(getRequest(), new UserBeanVO());
//        if (userBean.getOperate() != null && Integer.parseInt(userBean.getOperate()) == IConstant.ELEVEN) {
//            userBean.setAccount("test001");
//        }
//        applog.savelog(userBean);
        Integer operate = Integer.parseInt(userBean.getOperate());

//        try {
//            checkUser(common, bean, userBean, operate);
//        } catch (Exception e) {
//            printErrorJson(bean, e, log, "业务处理失败!");
//            common.writeJsonByFilter(getRequest(), getResponse(), bean, null, null);
//            return;
//        }

        switch (operate) {
//            case IConstant.TWO:
//            case IConstant.THREE:
//            case IConstant.THIRTY_ONE:
//            case IConstant.THIRTY_TWO:
//            case IConstant.SEVEN:
//            case IConstant.NINE:
//            case IConstant.TEN:
//            case IConstant.ELEVEN:
//            case IConstant.TWO_TWO:// WJX 完善用户信息
//            case IConstant.VERSION:
//                bean = doAppUserAction(operate, userBean);
//                break;
//            case IConstant.FIVE:
//            case IConstant.FIFTY_ONE:
//                bean = doManagingAction(operate, userBean);
//                break;
//            case IConstant.FIFTY_TWO:
//                bean = doManagingAction(operate, userBean);
//                break;
//            case IConstant.FIFTY_THREE:
//            case IConstant.FIFTY_FOUR:
//                bean = doManagingAction(operate, userBean);
//                break;
//            case IConstant.FIFTY_FIVE:
//                bean = doManagingAction(operate, userBean);
//                break;
//            case IConstant.FIFTY_SIX:
//            case IConstant.FIFTY_EIGHT:
//            case IConstant.FIFTY_SEVEN:
//                bean = doManagingAction(operate, userBean);
//                break;
//            case  IConstant.FIFTY_ZERO_ONE:
//            case  IConstant.FIFTY_ZERO_TWO:
//                bean = doManagingAction(operate, userBean);
//                break;
//            case IConstant.EIGTH:
//                bean = doMessageAction(operate, userBean);
//                break;
            case IConstant.SIX:
            case IConstant.SIXTY:
            case IConstant.SIXTY_ONE:
            case IConstant.SIXTY_TWO:
            case IConstant.SIXTY_THREE://签约
            case IConstant.SIXTY_FOUR:
            case IConstant.SIXTY_FIVE:
                bean = doOrgAction(operate, userBean);
                break;
//            case IConstant.TWO_FIVE://分配聊天对象，并返回
//                bean = doHxkffpAction(operate, userBean);
//                break;
//            case IConstant.TWO_ZERO_ONE://添加人员
//                bean = saveRegisterUser(userBean) ;
//                break;
//            case IConstant.TWO_ZERO_FOUR:// 更改帐号信息
//                bean = updateUserMsg(userBean);
//                break;
//            case IConstant.TWO_ZERO_FOUR_ONE:// 更改手机号码
//                bean = updateUserTel(userBean);
//                break;
            case IConstant.TWO_ZERO_FIVE:// 用户添加公司
            case IConstant.TWO_ONE_FIVE://财税通用户添加公司
                bean =  userAddcorp(userBean);
                break;
//            case IConstant.TWO_ZERO_FIVE_ONE:// 确定提交
//                bean = corpservice.updateuserAndCorpRelation(userBean);
//                break;
//            case IConstant.TWO_ZERO_FIVE_TWO:// 审核提交(300版本以后已经不使用)
//                bean = corpservice.userAddCorpExamine(userBean);
//                break;
            case IConstant.TWO_ONE_ONE://激活公司
                bean = activeCorp(userBean);
                break;
            case IConstant.TWO_ZERO_SIX:// 公司上传信息
                bean = corpservice.corpAddMsg(userBean);
                break;
//            case IConstant.TAXMSGQRY:  //申报信息查询
//                bean = doTaxQuery(operate, userBean);
//                break;
//            case IConstant.TAXSTAEDIT: //申报申报状态修改
//                bean = doTaxStatusEdit(operate, userBean);
//                break;
//            case IConstant.TWO_ONE_SERVEN://解除绑定
//                bean = doRemoveBindWx(userBean);
//                break;
//            case IConstant.TWO_ONE_EIGHT://大账房登录成功绑定微信
//                bean = loginBingWx(userBean);
//                break;
//            case IConstant.TWO_ONE_NINE://获取昵称
//                bean = doObtainNickName(userBean);
//                break;
//            case IConstant.TWO_TWO_FOUR://扫描公司二维码(加入公司)
//                bean = doScanCorpDrcode(userBean);
//                break;
//            case IConstant.TWO_TWO_FIVE://下载注册关联公司
//                bean = addUserFromRegister(userBean);
//                break;
        }
        return  ReturnData.ok().data(bean);
    }

    private ResponseBaseBeanVO doOrgAction(Integer operate, UserBeanVO userBean) throws BusinessException {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        IOrgService iorg = (IOrgService) SpringUtils.getBean("orgservice");
        try {
            switch (operate) {
//                case IConstant.SIX:
////                    bean = iorg.qrySvorgLs(userBean,null);
////                    break;
////                case IConstant.SIXTY:
////                    bean = iorg.qrySettingOrgLs(userBean);
////                    break;
                case IConstant.SIXTY_TWO:
                    bean = iorg.qrySignOrg(userBean);
                    break;
//                case IConstant.SIXTY_FOUR:
//                    bean = iorg.qryCstSignOrg(userBean);
//                    break;
                case IConstant.SIXTY_THREE:
                    bean = confirmSignOrg(iorg,userBean);
                    break;
//                case IConstant.SIXTY_FIVE:
//                    bean =iorg.saveCstSvOrgSetting(userBean);
//                    break;
            }
        } catch (Exception e) {
//            bean.setRescode(IConstant.FIRDES);
//            bean.setResmsg(e.getMessage());
//            log.error("失败!", log);
            printErrorJson(bean, e, log, "失败!");
        }
        return bean;
    }
    public ResponseBaseBeanVO confirmSignOrg(IOrgService iorg,UserBeanVO userBean) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        try {
            if(userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO1){//第一版走的代码
                bean = iorg.updateconfirmSignOrg(userBean);
            }
        } catch (Exception e) {
//            bean.setRescode(IConstant.FIRDES);
//            bean.setResmsg(e.getMessage());
//            log.error("确认签约出错！", log);
            printErrorJson(bean, e, log, "确认签约出错！");
        }
        return bean;
    }

    private ResponseBaseBeanVO userAddcorp(UserBeanVO userBean ){
        int versionno = userBean.getVersionno().intValue();

        ResponseBaseBeanVO bean1 = new RegisterRespBeanVO();

        IAppCorpService corpservice = null;

        try {
            if (versionno < IVersionConstant.VERSIONNO2 && versionno >= IVersionConstant.VERSIONNO1) {// 第一个版本走的东西
                corpservice = (IAppCorpService) SpringUtils.getBean("corpservice");
            } else if (versionno >= IVersionConstant.VERSIONNO2 && versionno < IVersionConstant.VERSIONNO300) {// 第二个版本走的东西（252以后）
                corpservice = (IAppCorpService) SpringUtils.getBean("corp252service");
            } else if (versionno >= IVersionConstant.VERSIONNO300 &&  versionno < IVersionConstant.VERSIONNO311 ) {// 存在多个公司的情况
                corpservice = (IAppCorpService) SpringUtils.getBean("corp300service");
            } else if(versionno>= IVersionConstant.VERSIONNO311 && versionno<IVersionConstant.VERSIONNO322){
                corpservice = (IAppCorpService) SpringUtils.getBean("corp311service");
            } else if (versionno >= IVersionConstant.VERSIONNO322){
                corpservice = (IAppCorpService) SpringUtils.getBean("corp322service");
            }
            if(corpservice!=null){
                bean1 = corpservice.updateuserAddCorp(userBean);
            }
        } catch (Exception e) {
//            bean1.setRescode(IConstant.FIRDES);
//            bean1.setResmsg(e.getMessage());
//            log.error("创建公司失败，公司可能已存在，请联系客服!", log);
            printErrorJson(bean1, e, log, "创建公司失败，公司可能已存在，请联系客服!");
        }
        return bean1;
    }
    /**
     * 激活公司
     * @param userBean
     * @return
     */
    private ResponseBaseBeanVO activeCorp(UserBeanVO userBean) {
        RegisterRespBeanVO bean = new RegisterRespBeanVO();
        try {
            IAppCorpService corpservice = null;
            if (userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO322) {// 第一版走的代码
                corpservice = (IAppCorpService) SpringUtils.getBean("corpservice");
            }
            if(corpservice!=null){
                corpservice.updateAddCorpFromActiveCode(userBean);
            }

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("激活公司成功!");
        } catch (Exception e) {
//            log.error( "激活公司出错！", log);
//            bean.setRescode(IConstant.FIRDES);
//            bean.setResmsg(e.getMessage());
            printErrorJson(bean, e, log, "激活公司出错！");
        }
        return bean;
    }
}

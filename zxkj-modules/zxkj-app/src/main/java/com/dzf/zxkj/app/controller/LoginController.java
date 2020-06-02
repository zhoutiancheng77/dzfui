package com.dzf.zxkj.app.controller;

import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.corp.IAppCorp320Service;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginOrRegService;
import com.dzf.zxkj.app.service.login.impl.AppLoginOrRegImpl;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.SourceSysEnum;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 登录
 */
@RestController
@RequestMapping("/app/login")
@Slf4j
public class LoginController {

    @Autowired
    private IAppLoginCorpService user320service;


    @Autowired
    private IAppLoginOrRegService user300service;

    @Autowired
    private IAppCorp320Service corp320service;
    @Autowired
    private IUserPubService userPubService;

    /**
     * 查询科目明细数据
     */
    @RequestMapping("/doLogin")
    public ReturnData<ResponseBaseBeanVO> doLogin(UserBeanVO userBean,String uuid,String cname,String uname,String corp,String tcorp,String ucode) {
        UserVO uservo = userPubService.queryUserVOId(userBean.getAccount_id());
        userBean.setUsercode(uservo.getUser_code());
        userBean.setAccount_id(uservo.getCuserid());
        userBean.setCorpname(cname);
        userBean.setUsername(uname);
        userBean.setPk_corp(corp);
        userBean.setPk_tempcorp(tcorp);
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        Integer versionno = userBean.getVersionno();
        if(versionno == null || versionno.intValue() ==0){
            bean.setRescode(IConstant.FIRDES);
            bean.setResmsg("您当前版本出问题，请更新最新版本!");
        }else{
            Integer operate =Integer.parseInt(userBean.getOperate());
            switch(operate){
                case IConstant.TWO_ZERO_TWO://不选公司自动登录
                    userBean.setAccount(userBean.getPhone());
                    bean= loginFromTel(userBean);
                    break;
                case IConstant.TWO_ZERO_THREE://公司列表
                    userBean.setAccount(userBean.getPhone());
                    bean = logingGetCorpVOs(userBean);
                    break;
                case IConstant.TWO_ZERO_NINE://获取公司信息(token)
                    bean = corp320service.getCorpMsg(userBean);
                    break;
                case IConstant.TWO_TWO_ZERO://扫码二维码(扫码登录)
                    bean = doScanQrcode(userBean,uuid);
                    break;
                case IConstant.TWO_TWO_ONE://确认二维码(扫码登录)
                    bean =  doconfirmQrCode(userBean,uuid);
                    break;
            }
        }
        return ReturnData.ok().data(bean);
    }

    private ResponseBaseBeanVO doconfirmQrCode(UserBeanVO userBean,String requrl) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        try {
            user300service.saveConfirmQrCode(userBean, requrl);
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("授权成功");
        } catch (Exception e) {
            log.error("授权失败", log);
        }
        return bean;
    }

    private ResponseBaseBeanVO doScanQrcode(UserBeanVO userbean,String requrl) {
        ResponseBaseBeanVO bean = new ResponseBaseBeanVO();
        try {
            user300service.saveScanQrCode(userbean, requrl);
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("扫描成功");
        } catch (Exception e) {
            log.error("扫描失败", log);
        }
        return bean;
    }

    public LoginResponseBeanVO logingGetCorpVOs(UserBeanVO userBean) throws DZFWarpException {
        LoginResponseBeanVO bean = new LoginResponseBeanVO();
        int versionno = userBean.getVersionno().intValue();
        try {
            bean = user300service.logingGetCorpVOs260(userBean);
            AppLoginOrRegImpl.log_identify.put(userBean.getAccount()+"@"+IConstant.TWO_ZERO_THREE, DZFBoolean.TRUE);
            return bean;//
        } catch (Exception e) {
            printErrorLog(bean, e, "获取公司信息出错！");
        }
        return bean;
    }


    /**
     * 通过电话号码+验证码登录
     */
    public LoginResponseBeanVO loginFromTel(UserBeanVO userBean) throws DZFWarpException {
        LoginResponseBeanVO bean = new LoginResponseBeanVO();
        try {
            if(userBean.getVersionno().intValue() >= IVersionConstant.VERSIONNO320){
                return user320service.loginFromTel(userBean,bean);
            }
        } catch (Exception e) {
            printErrorLog(bean, e, "登录出错!");
        }
        return bean;
    }

    public void printErrorLog(ResponseBaseBeanVO bean, Throwable e, String errormsg) {
        if(bean == null){
            bean = new ResponseBaseBeanVO();
        }
        log.error(e.getMessage(),e);
        bean.setRescode(IConstant.FIRDES);
        if (e instanceof BusinessException) {
            bean.setResmsg(e.getMessage());
        } else {
            if (StringUtil.isEmpty(errormsg)) {
                bean.setResmsg("处理失败!");
            } else {
                bean.setResmsg(errormsg);
            }
        }
    }
}

package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.login.IAppLoginService;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.login.IAppLoginOrRegService;
import com.dzf.zxkj.app.service.login.impl.AppLoginOrRegImpl;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service("appLoginService")
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class AppLoginServiceImpl implements IAppLoginService {
    @Autowired
    private IAppLoginOrRegService user300service;
    @Override
    public ReturnData<LoginResponseBeanVO> logingGetCorpVOs(String operate,String account_id,Integer versionno) throws DZFWarpException {
        UserBeanVO userBean = new UserBeanVO();
        userBean.setOperate(operate);
        userBean.setAccount_id(account_id);
        userBean.setVersionno(versionno);
        LoginResponseBeanVO bean = new LoginResponseBeanVO();
        try {
            bean = user300service.logingGetCorpVOs260(userBean);
            AppLoginOrRegImpl.log_identify.put(userBean.getAccount()+"@"+ IConstant.TWO_ZERO_THREE, DZFBoolean.TRUE);
            return ReturnData.ok().data(bean);//
        } catch (Exception e) {
            log.error("获取公司信息出错！", e);
        }
        return ReturnData.ok().data(bean);
    }
}

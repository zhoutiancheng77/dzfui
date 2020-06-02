package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.corp.IRemoteAppCorpService;
import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.service.login.IAppLoginCorpService;
import com.dzf.zxkj.app.service.login.IAppLoginOrRegService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class RemoteAppCorpDubbo implements IRemoteAppCorpService {
    @Autowired
    private IAppLoginOrRegService user300service;
    @Autowired
    private IAppLoginCorpService user320service;
    @Override
    public LoginResponseBeanVO logingGetCorpVOs260(String ucode, String p) {
        LoginResponseBeanVO bean = new LoginResponseBeanVO();
        UserBeanVO userBeanVO = new UserBeanVO();
        userBeanVO.setUsercode(ucode);
        userBeanVO.setPassword(p);
        try {
            bean = user300service.logingGetCorpVOs260(userBeanVO);
            return bean;//
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return bean;
    }

    @Override
    public LoginResponseBeanVO loginFromTel(String tcorp, String corp, String cname, String ucode, String account, String account_id, String uname) {
        LoginResponseBeanVO bean = new LoginResponseBeanVO();
        UserBeanVO userBeanVO = new UserBeanVO();
        userBeanVO.setPk_tempcorp(tcorp);
        userBeanVO.setPk_corp(corp);
        userBeanVO.setCorpname(cname);
        userBeanVO.setUsercode(ucode);
        userBeanVO.setAccount_id(account_id);
        userBeanVO.setAccount(account);
        userBeanVO.setUsername(uname);
        try {
                return user320service.loginFromTel(userBeanVO,bean);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return bean;
    }
}

package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.eleverify.IAppVerifyService;
import com.dzf.zxkj.app.service.sms.SMSServiceNew;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.constant.ISmsConst;
import com.dzf.zxkj.platform.model.sys.SMSBVO;
import com.dzf.zxkj.platform.model.sys.SMSResVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service("appVerifyService")
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class AppVerifyServiceImpl implements IAppVerifyService {
    @Override
    public String sendPhoneVerverify(String phone,String verify){
        Map<String, String> params = new HashMap<String, String>();
        params.put("verify", String.valueOf(verify));
        SMSBVO smsVO = new SMSBVO();
       // if(name.equals("大账房")){
            smsVO.setDxqm(ISmsConst.SIGN_01);
//        }else{
//            smsVO.setDxqm(ISmsConst.SIGN_03);
//        }
        smsVO.setParams(params);
        smsVO.setPhone(new String[] { phone});
        smsVO.setTemplatecode(ISmsConst.TEMPLATECODE_4101);//ISmsConst.TEMPLATECODE_4101

        //验证码:${verify}，（大账房绝对不会索取此验证码，切勿告知他人），请您10分钟内在页面中输入以完成验证。
        //【大帐房】 您正在进行短信验证码登录操作，验证码${verify}。(验证码不要告知他人，否则可能导致账号被盗，请勿泄露)
        SMSServiceNew smsServ = new SMSServiceNew(smsVO);
        SMSResVO headvo = smsServ.sendPostData();
        if(!headvo.isSuccess()){
            throw new BusinessException("短信发送失败，请稍后重试");
        }
        return "短信发送成功";
    }
}

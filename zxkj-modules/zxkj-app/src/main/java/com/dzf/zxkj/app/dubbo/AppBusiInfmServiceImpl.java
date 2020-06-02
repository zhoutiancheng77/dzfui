package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.file.AppBusiInfmService;
import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.service.app.act.IImageService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.CryptUtil;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletResponse;
@Slf4j
@Service("appBusiInfmService")
@org.apache.dubbo.config.annotation.Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class AppBusiInfmServiceImpl implements AppBusiInfmService {

    @Autowired
    private IUserPubService userPubService;



    public void downLoadImage(ImageBeanVO imageBean, HttpServletResponse response)  {//图片下载

        UserVO uservo = userPubService.queryUserVOId(imageBean.getAccount_id());
        imageBean.setUsercode(uservo.getUser_code());
        imageBean.setAccount_id(uservo.getCuserid());
        if(!StringUtil.isEmpty(imageBean.getFilepath())){
//			if(imageBean.getFilepath().indexOf("ImageUpload")<0){
            imageBean.setFilepath(CryptUtil.getInstance().decryptAES(imageBean.getFilepath()));
//			}
            if(StringUtil.isEmpty(imageBean.getFilepath()) || imageBean.getFilepath().indexOf("..")!=-1){
                return;
            }
        }
        IImageService iis=	(IImageService) SpringUtils.getBean("imservice");
        iis.downLoadByget(imageBean, response);
    }
}

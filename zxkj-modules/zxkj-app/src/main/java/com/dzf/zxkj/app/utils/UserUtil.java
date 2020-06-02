package com.dzf.zxkj.app.utils;

import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.resp.bean.UserLsBean;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.io.File;

public class UserUtil {

    public static UserVO getUservo (UserVO uvo) {
        uvo.setUser_name(CodeUtils1.deCode(uvo.getUser_name()));
        return uvo;
    }

    /**
     * 包含用户头像的返回值
     * @param bean
     * @param userVOs
     */
    public static UserLsBean[] setUserLsBean(AppUserVO[] userVOs) {
        UserLsBean[] ubs = new UserLsBean[userVOs.length];
        String tempname = null;
        String username = null;
        for (int i = 0; i < userVOs.length; i++) {
            ubs[i] = new UserLsBean();
            tempname = CodeUtils1.deCode(userVOs[i].getUser_name());

            username = tempname;
            ubs[i].setUsername(username);
            ubs[i].setUsercode(userVOs[i].getUser_code());
            ubs[i].setBdata(userVOs[i].getBdata() == null ? "N" : userVOs[i].getBdata().toString());
            ubs[i].setBaccount(userVOs[i].getBaccount() == null ? "N" : userVOs[i].getBaccount().toString());
            ubs[i].setUserid(userVOs[i].getCuserid());
            ubs[i].setEmail(userVOs[i].getApp_user_mail());
            ubs[i].setBbillapply(userVOs[i].getBbillapply());
            if (userVOs[i].getIsmanager() != null && userVOs[i].getIsmanager().booleanValue()) {
                ubs[i].setUsergrade(IConstant.FIRDES);
            } else {
                ubs[i].setUsergrade(IConstant.DEFAULT);
            }
            ubs[i].setQq(userVOs[i].getApp_user_qq());
            ubs[i].setTel(userVOs[i].getApp_user_tel());
            ubs[i].setUserstate(userVOs[i].getIstate() == null ? "0" : userVOs[i].getIstate().toString());
            if (userVOs[i].getJob() == null) {
                ubs[i].setJob("");
            } else {
                ubs[i].setJob(userVOs[i].getJob());
            }

            ubs[i].setPhotopath(UserUtil.getHeadPhotoPath(userVOs[i].getUser_code(),0));
        }
        return ubs;
    }

    /**
     * 获取用户头像
     * @param en 是否加密 0 加密，1 不加密
     * @return
     * @throws DZFWarpException
     */
    public static String getHeadPhotoPath(String account,Integer en) throws DZFWarpException {
        String headpath = "";
        String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
        String imagename = null;
        File imagefile = null;
        for (String type : types) {
            // 文件名
            imagename = ImageCommonPath.getUserHeadPhotoPath(account, type);
            imagefile = new File(imagename);
            if (imagefile.exists()) {
                headpath = imagefile.getPath();
                break;
            }
        }
        if(en.intValue() == 0){
            return CryptUtil.getInstance().encryptAES(headpath);
        }else{
            return headpath;
        }
    }

    /**
     * 获取接单人信息
     * @param account
     * @return
     * @throws DZFWarpException
     */
    public static String getJdrHeadPhotoPath(String account) throws DZFWarpException {
        String headpath = "";

        String[] types = new String[] { ".jpg", ".png", ".jpeg", ".bmp", ".gif" };
        String imagename = null;
        File imagefile = null;
        for (String type : types) {
            // 文件名
            imagename = ImageCommonPath.getBillpersonLogoPath(account, type);
            imagefile = new File(imagename);
            if (imagefile.exists()) {
                headpath = imagefile.getPath();
                break;
            }
        }
        return headpath;
    }
}

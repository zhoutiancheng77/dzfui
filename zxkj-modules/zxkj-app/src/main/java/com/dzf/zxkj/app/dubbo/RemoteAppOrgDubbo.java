package com.dzf.zxkj.app.dubbo;

import com.dzf.zxkj.app.model.resp.bean.OrgRespBean;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.org.IAppOrgService;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.org.IOrgService;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Slf4j
@Service(version = "1.0.0", timeout = Integer.MAX_VALUE)
public class RemoteAppOrgDubbo implements IAppOrgService {

    @Autowired
    private IOrgService orgService;

    @Override
    public ResponseBaseBeanVO qrySvorgLs(double longitude, double latitude, String pk_corp) {
        try {
            UserBeanVO userBeanVO = new UserBeanVO();
            userBeanVO.setLongitude(longitude);
            userBeanVO.setLatitude(latitude);
            userBeanVO.setPk_corp(pk_corp);
            return  orgService.qrySvorgLs(userBeanVO, DZFBoolean.FALSE);
        } catch (DZFWarpException e) {
            log.error(e.getMessage(),e);
            ResponseBaseBeanVO  beanvo = new ResponseBaseBeanVO();
            beanvo.setRescode(IConstant.FIRDES);
            beanvo.setResmsg("操作失败!");
            return beanvo;
        }
    }

    @Override
    public ResponseBaseBeanVO saveSvOrgSetting(double longitude, double latitude, String pk_corp, String pk_tempcorp, String corpname, String phone, String username, OrgRespBean[] userBeanLs) {

        try {
            UserBeanVO userBeanVO = new UserBeanVO();
            userBeanVO.setLongitude(longitude);
            userBeanVO.setLatitude(latitude);
            userBeanVO.setPk_corp(pk_corp);
            userBeanVO.setPk_tempcorp(pk_tempcorp);
            userBeanVO.setPhone(phone);
            userBeanVO.setCorpname(corpname);
            userBeanVO.setUsername(username);
            return orgService.saveSvOrgSetting(userBeanVO, userBeanLs);
        } catch (DZFWarpException e) {
            log.error(e.getMessage(),e);
            ResponseBaseBeanVO  baseBeanVO = new ResponseBaseBeanVO();
            baseBeanVO.setRescode(IConstant.FIRDES);
            baseBeanVO.setResmsg("操作失败");
        }
        return null;
    }

    @Override
    public ResponseBaseBeanVO qrySignOrg(String pk_corp, String account_id, String pk_temp_corp) {
        try {
            UserBeanVO userBeanVO = new UserBeanVO();
            userBeanVO.setPk_tempcorp(pk_temp_corp);
            userBeanVO.setPk_corp(pk_corp);
            userBeanVO.setAccount_id(account_id);
            return orgService.qrySignOrg(userBeanVO);
        } catch (DZFWarpException e) {
            log.error(e.getMessage(),e);
            ResponseBaseBeanVO baseBeanVO = new ResponseBaseBeanVO();
            baseBeanVO.setResmsg("操作失败!");
            baseBeanVO.setRescode(IConstant.FIRDES);
            return baseBeanVO;
        }
    }

    @Override
    public ResponseBaseBeanVO updateconfirmSignOrg(String account, String sourcesys, String account_id, String pk_signcorp, String pk_temp_corp) {

        UserBeanVO userBeanVO = new UserBeanVO();
        try {
            userBeanVO.setSourcesys("dzfapp");
            userBeanVO.setAccount_id(account_id);
            userBeanVO.setPk_signcorp(pk_signcorp);
            userBeanVO.setPk_tempcorp(pk_temp_corp);
            return  orgService.updateconfirmSignOrg(userBeanVO);
        } catch (DZFWarpException e) {
            log.error(e.getMessage(), e);
            ResponseBaseBeanVO baseBeanVO = new ResponseBaseBeanVO();
            baseBeanVO.setRescode(IConstant.FIRDES);
            baseBeanVO.setResmsg("操作失败");
            return baseBeanVO;
        }
    }
}

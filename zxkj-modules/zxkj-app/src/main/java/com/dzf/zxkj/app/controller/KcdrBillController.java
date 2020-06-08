package com.dzf.zxkj.app.controller;

import com.dzf.zxkj.app.model.req.BillCustomerBean;
import com.dzf.zxkj.app.model.resp.bean.BillResonseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.pub.constant.IBillConstant;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.app.act.ILog;
import com.dzf.zxkj.app.service.corp.IAppCorpService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.BeanUtils;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/app/dzfbillhandlesvlt")
public class KcdrBillController {

    @Autowired
    private IUserPubService userPubService;

    @RequestMapping("/doAction")
    public ReturnData<BillResonseBeanVO> doAction(BillCustomerBean userbean,String corp,String tcorp,String caddr) {

        BillResonseBeanVO beanvo = new  BillResonseBeanVO();
        UserVO uservo = userPubService.queryUserVOId(userbean.getAccount_id());
        userbean.setAccount_id(uservo.getCuserid());
        userbean.setPk_corp(corp);
        userbean.setPk_tempcorp(tcorp);
        userbean.setCorpaddr(caddr);
        Integer operate = Integer.parseInt(userbean.getOperate());
        switch (operate) {
            case IBillConstant.CORP_BILL_QRY://开票信息(大账房app也需要)
                beanvo = doQueryCorpBill(userbean);
                break;
            case IBillConstant.CORP_BILL_UPDATE://开票信息保存(大账房app也需要)
                beanvo = doSaveCorpBill(userbean);
                break;
            default:
                break;
        }
        return ReturnData.ok().data(beanvo);
    }

    private BillResonseBeanVO doQueryCorpBill(BillCustomerBean userbean) {

        BillResonseBeanVO beanres = new BillResonseBeanVO();
        IAppCorpService corpservice =   (IAppCorpService) SpringUtils.getBean("corpservice");
        try {
            ResponseBaseBeanVO basevo = corpservice.qrykpmsg(userbean.getPk_corp(), userbean.getPk_tempcorp(), userbean.getAccount_id(),userbean.getAccount());
            BeanUtils.copyNotNullProperties(basevo, beanres);
            beanres.setRescode(IConstant.DEFAULT);
        } catch (Exception e) {
            beanres.setResmsg(e.getMessage());
            beanres.setRescode(IConstant.FIRDES);
            log.error("获取开票信息失败!", e);
        }
        return (BillResonseBeanVO) beanres;
    }

    private BillResonseBeanVO doSaveCorpBill(BillCustomerBean userbean) {
        BillResonseBeanVO beanvo = new BillResonseBeanVO();

        IAppCorpService corpservice =   (IAppCorpService) SpringUtils.getBean("corpservice");

        try {
            corpservice.saveKpmsg(userbean.getPk_corp(), userbean.getPk_tempcorp(), userbean.getAccount_id(),
                    userbean.getCorpname(), userbean.getSh(),  userbean.getCorpaddr(),
                    userbean.getKpdh(), userbean.getKhh(), userbean.getKhzh(),userbean.getGrdh(),userbean.getGryx());

            beanvo.setRescode(IConstant.DEFAULT);

            beanvo.setResmsg("保存开票信息成功!");

        } catch (Exception e) {
            beanvo.setResmsg(e.getMessage());
            beanvo.setRescode(IConstant.FIRDES);
            log.error("保存开票信息失败!", e);
        }

        return beanvo;
    }
}

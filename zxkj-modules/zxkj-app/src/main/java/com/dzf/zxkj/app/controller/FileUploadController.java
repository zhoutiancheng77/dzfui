package com.dzf.zxkj.app.controller;

import com.dzf.zxkj.app.model.image.ImageUploadRecordVO;
import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ImageReqVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.pub.constant.MarkConstant;
import com.dzf.zxkj.app.service.app.act.ILog;
import com.dzf.zxkj.app.service.message.IMessageSendService;
import com.dzf.zxkj.app.service.photo.IImageProviderPhoto;
import com.dzf.zxkj.app.service.pub.IAppPubservice;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.service.user.IAppUserService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.AppQueryUtil;
import com.dzf.zxkj.app.utils.BeanUtils;
import com.dzf.zxkj.app.utils.CommonServ;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.CodeUtils1;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageQueryBean;
import com.dzf.zxkj.platform.model.image.ImageRecordVO;
import com.dzf.zxkj.platform.model.image.ImgGroupRsBean;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.report.service.IZxkjRemoteAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
@Slf4j
@RestController
@RequestMapping("/app/uploadServlet")
public class FileUploadController {

    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IZxkjRemoteAppService iZxkjRemoteAppService;

    @Autowired
    private IAppPubservice apppubservice ;
    @Autowired
    private IUserPubService userPubService;

    @RequestMapping("/doUpLoad")
    public ReturnData<ResponseBaseBeanVO> doUpLoad(UserBeanVO uBean, String cname, String method, String corp, String tcorp,
                                                   MultipartFile ynt) {

        UserVO uservo = userPubService.queryUserVOId(uBean.getAccount_id());
        uBean.setUsercode(uservo.getUser_code());
        uBean.setAccount_id(uservo.getCuserid());
        uBean.setAccount(StringUtil.isEmpty(uBean.getAccount())?uservo.getUser_code():uBean.getAccount());
        uBean.setCorpname(cname);
        uBean.setPaymethod(method);
        uBean.setPk_corp(corp);
        uBean.setPk_tempcorp(tcorp);
        ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
        try {
            validateForUpload(uBean);
            CommonServ.initUser(uBean);
            /**
             * 启动日志线程
             */
            uBean.setJson(uBean.getCorpname());// 文件路径
            uBean.setOptype("3-文件上传" + uBean.getSystype() + "-" + uBean.getOperate());
//            ILog lo = (ILog) SpringUtils.getBean("applog");
//            lo.savelog(uBean);

            // 区分业务合作
            // 上传日志（业务合作）
            if (uBean.getBusitype() != null && uBean.getBusitype().intValue() == 0) {
                IAppUserService iaus = (IAppUserService) SpringUtils.getBean("userservice");
                iaus.saveprocesscollabtevalt(uBean);
                respBean.setRescode(IConstant.DEFAULT);
                respBean.setResmsg("提交成功");
            } else {//图片上传
                if (uBean.getCorpname() == null) {
                    uBean.setCorpname(uBean.getAccount());
                }

                IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");

                ImageQueryBean[] resultBeans = ip.saveUploadImages(uBean, ynt,
                        ynt.getOriginalFilename(), null);

                if (resultBeans != null && resultBeans.length > 0) {
                    respBean.setRescode(IConstant.DEFAULT);
                    respBean.setResmsg("上传成功");
                } else {
                    respBean.setRescode(IConstant.FIRDES);
                    respBean.setResmsg("上传图片失败，没有生成任何图片记录");
                }

                // 是否匹配模板生成凭证(发送会计端，是否制单)
                genImgMsgToKj(uBean, resultBeans);

                // 赋值图片张数
                putImgeNumber(respBean, uBean);
            }
            // 删除临时文件
//			delTempFile();

        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return ReturnData.ok().data(null);
    }

    /**
     * 图片重传
     *
     */
    @RequestMapping("/doReImageUpload")
    public ReturnData<ResponseBaseBeanVO> doReImageUpload(UserBeanVO uBean, String imgmsg,String cname,String method,
                                                          MultipartFile ynt) {
        UserVO uservo = userPubService.queryUserVOId(uBean.getAccount_id());
        uBean.setUsercode(uservo.getUser_code());
        uBean.setAccount_id(uservo.getCuserid());
        uBean.setCorpname(cname);
        uBean.setPaymethod(method);
        ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
        try {
            // 重传的对象数据
            ImageBeanVO[] imgbeanvos = JsonUtils.deserialize(imgmsg,ImageBeanVO[].class);
            // 重传校验
            validateForUpload(uBean);

            // 启动日志线程
            uBean.setJson(uBean.getCorpname());// 文件路径
            uBean.setOptype("3-文件上传" + uBean.getSystype() + "-" + uBean.getOperate());
            ILog lo = (ILog) SpringUtils.getBean("applog");
            lo.savelog(uBean);

            if (uBean.getCorpname() == null) {
                uBean.setCorpname(uBean.getAccount());
            }

            IImageProviderPhoto ip = null;

            Integer versionno = uBean.getVersionno();

            if (versionno.intValue() < IVersionConstant.VERSIONNO322) {
                ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");
            } else {
                ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro322");
            }


            int sussCount = ip.saveReuploadImage(uBean, imgbeanvos, ynt,ynt.getOriginalFilename(),null);

            // 上传日志
            if (uBean.getBusitype() != null && uBean.getBusitype().intValue() == 0) {
                IAppUserService iaus = (IAppUserService) SpringUtils.getBean("userservice");
                iaus.saveprocesscollabtevalt(uBean);
            }

            if (sussCount < 1) {
                throw new BusinessException("重传图片失败，没有生成任何图片记录");
            }
            if (sussCount >= 1) {// 更新一条信息
                respBean.setRescode(IConstant.DEFAULT);
                if (versionno.intValue() < IVersionConstant.VERSIONNO322) {// 重新获取图片信息
                    respBean.setResmsg("上传成功");
                } else {
                    ImageBeanVO beanvo = new ImageBeanVO();

                    BeanUtils.copyNotNullProperties(uBean, beanvo);

                    beanvo.setImageparams(beanvo.getGroupkey());

                    ImgGroupRsBean[] rsbean = ip.queryImages(beanvo);

                    if (rsbean != null && rsbean.length > 0) {
                        respBean.setResmsg(rsbean[0]);
                    } else {
                        respBean.setRescode(IConstant.DEFAULT);
                        respBean.setResmsg("获取信息失败:请重新请求数据!");
                    }
                }
            }
        } catch (Exception e) {
            log.error("上传失败!",e );
        }
        return  ReturnData.ok().data(respBean);
    }

    @RequestMapping("/doImageRecord")
    public ReturnData<ResponseBaseBeanVO> doImageRecord(ImageReqVO uBean,String corp,String tcorp) {
        UserVO uservo = userPubService.queryUserVOId(uBean.getAccount_id());
        uBean.setUsercode(uservo.getUser_code());
        uBean.setAccount_id(uservo.getCuserid());
        uBean.setPk_corp(corp);
        uBean.setPk_tempcorp(tcorp);
        ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
        try {
            validatePower(respBean, uBean);
            IImageProviderPhoto ip = null;
            if (uBean.getVersionno().intValue() < IVersionConstant.VERSIONNO322) {
                ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");
            } else {
                ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro322");
            }
            uBean.setIsshowapprove(DZFBoolean.TRUE);//显示审核的发票
            ImageUploadRecordVO[] rcds = ip.queryUploadRecord(uBean);// 获取用户上传记录

            if (rcds != null && rcds.length > 0) {
                Integer fkqy = apppubservice.qryParamValue(uBean.getPk_corp(), "fkqy001");
                if(fkqy==null || fkqy.intValue() ==1){
                    for(ImageUploadRecordVO record:rcds){
                        if("未处理".equals(record.getApprovemsg()) ||
                                "处理中".equals(record.getApprovemsg()) || "已制证".equals(record.getApprovemsg())
                                || "暂存中".equals(record.getApprovemsg())){
                            record.setApprovemsg("已传票");
                        }
                    }
                }
                respBean.setRescode(IConstant.DEFAULT);
                respBean.setResmsg(rcds);
            } else {
                respBean.setRescode(IConstant.FIRDES);
                respBean.setResmsg("暂无记录!");
            }

        } catch (Exception e) {
            log.error( "\"获取用户上传记录失败!\"",log );
            if(e.getMessage().indexOf("暂无记录")>=0){
                respBean.setResmsg(e.getMessage());
            }
        }
        return ReturnData.ok().data(respBean);
    }

    @RequestMapping("/doImageHis")
    public ReturnData<ResponseBaseBeanVO> doImageHis(ImageBeanVO beanvo,String corp) {
        UserVO uservo = userPubService.queryUserVOId(beanvo.getAccount_id());
        beanvo.setUsercode(uservo.getUser_code());
        beanvo.setAccount_id(uservo.getCuserid());
        ResponseBaseBeanVO respBean = new ResponseBaseBeanVO();
//		getResponse().setContentType("text/json;charset=UTF-8");
        Integer versionno = beanvo.getVersionno();
        if (versionno == null || versionno.intValue() == 0) {
            respBean.setRescode(IConstant.FIRDES);
            respBean.setResmsg("您当前版本出问题，请更新最新版本!");
            return ReturnData.error().data(respBean) ;
        }

        try {

            IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");
            respBean.setRescode(IConstant.DEFAULT);
            respBean.setResmsg("查询上传图片历史成功");

            if (versionno.intValue() < IVersionConstant.VERSIONNO322) {
                ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");
                ImageUploadRecordVO[] iqbean = ip.queryImages(beanvo);
                respBean.setResmsg(iqbean);
            } else {
                ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro322");
                ImageUploadRecordVO[] iqbean = ip.queryImages(beanvo);
                handleUprecord(corp,iqbean);
                respBean.setResmsg(iqbean[0]);
            }
        } catch (Exception e) {
            log.error("查询上传图片历史失败", log);
        }
        return ReturnData.ok().data(respBean);
    }



    private void validatePower(ResponseBaseBeanVO respBean, UserBeanVO uBean) throws DZFWarpException {

        CorpVO[] corpvos = AppQueryUtil.getInstance().getDemoCorpMsg();
        // 判断是否demo公司
        String demoname = null;
        if (corpvos != null && corpvos.length > 0) {
            if (!StringUtil.isEmpty(corpvos[0].getUnitname())) {
                demoname = CodeUtils1.deCode(corpvos[0].getUnitname());
            }
        }

        if (demoname == null || (!StringUtil.isEmpty(uBean.getCorpname()) && !demoname.equals(uBean.getCorpname()))) {
            IAppUserService iaus = (IAppUserService) SpringUtils.getBean("userservice");
            if (iaus.getPrivilege(IConstant.IMAGE, uBean.getAccount(), "", uBean.getPk_corp(),
                    uBean.getPk_tempcorp())) {
                throw new BusinessException("您没该权限，请联系管理员!");
            }
        }
    }



    /**
     * 处理上传记录
     * @param iqbean
     */
    private void handleUprecord(String pk_corp,ImageUploadRecordVO[] iqbean) {

        if(iqbean == null || iqbean.length == 0){
            return;
        }
        for(ImageUploadRecordVO record:iqbean){
            // 处理金额+结算方式+摘要+备注
            if (!StringUtil.isEmpty(record.getMny())) {
                if (record.getMny().split(MarkConstant.MH_ZH).length > 1) {
                    record.setMny(record.getMny().split(MarkConstant.MH_ZH)[1]);
                } else {
                    record.setMny("");
                }
            }
            if (!StringUtil.isEmpty(record.getPaymethod())) {
                if (record.getPaymethod().split(MarkConstant.MH_ZH).length > 1) {
                    record.setPaymethod(record.getPaymethod().split(MarkConstant.MH_ZH)[1]);
                } else {
                    record.setPaymethod("");
                }
            }

            if (!StringUtil.isEmpty(record.getMemo())) {
                if (record.getMemo().split(MarkConstant.MH_ZH).length > 1) {
                    record.setMemo(record.getMemo().split(MarkConstant.MH_ZH)[1]);
                } else {
                    record.setMemo("");
                }
            }
            if (!StringUtil.isEmpty(record.getMemo1())) {
                if (record.getMemo1().split(MarkConstant.MH_ZH).length > 1) {
                    record.setMemo1(record.getMemo1().split(MarkConstant.MH_ZH)[1]);
                } else {
                    record.setMemo1("");
                }
            }

            ImageRecordVO[] imgrecordvos =  record.getImgrecord();
            Integer fkqy = apppubservice.qryParamValue(pk_corp, "fkqy001");
            if(fkqy==null || fkqy.intValue() ==1){
                if("未处理".equals(record.getApprovemsg()) ||
                        "处理中".equals(record.getApprovemsg()) || "已制证".equals(record.getApprovemsg())
                        || "暂存中".equals(record.getApprovemsg())){
                    record.setApprovemsg("已传票");
                }

                if (imgrecordvos != null && imgrecordvos.length > 0) {
                    List<ImageRecordVO> list = new ArrayList<ImageRecordVO>();
                    for(ImageRecordVO vo:imgrecordvos){
                        if("制单".equals(vo.getVapproveope()) || "未知".equals(vo.getVapproveope())){
                            continue;
                        }
                        list.add(vo);
                    }
                    record.setImgrecord(list.toArray(new ImageRecordVO[0]));
                }
            }
        }
    }




    private void putImgeNumber(ResponseBaseBeanVO respBean, UserBeanVO uBean) {
        //查询当前上传的图片
        SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
        SQLParameter sp = new SQLParameter();
        sp.addParam(uBean.getPk_corp());
        sp.addParam(uBean.getAccount_id());
        respBean.setImg_pj_tips(IConstant.FIRDES);
        ImageGroupVO[] groupvos = (ImageGroupVO[]) singleObjectBO.queryByCondition(ImageGroupVO.class, "nvl(dr,0)=0 and pk_corp = ? and coperatorid = ? ", sp);
        if (groupvos != null && groupvos.length > 0) {
            if (groupvos.length == 20 || groupvos.length == 100 || groupvos.length == 200) {
                respBean.setImg_pj_tips(IConstant.DEFAULT);
            }
        }
    }

    private void genImgMsgToKj(UserBeanVO uBean, ImageQueryBean[] resultBeans) {
        if(!((uBean.getCert() != null && DZFBoolean.valueOf(uBean.getCert()).booleanValue())
                || (uBean.getLogo()!=null && DZFBoolean.valueOf(uBean.getLogo()).booleanValue())
                || (uBean.getPermit()!=null && DZFBoolean.valueOf(uBean.getPermit()).booleanValue()))){
            if(resultBeans != null && resultBeans.length > 0){
                IMessageSendService sys_appmsgserv = (IMessageSendService) SpringUtils.getBean("sys_appmsgserv");
                Set<String> groupkeyset = new HashSet<String>();
                for(ImageQueryBean bean : resultBeans){
                    if(!groupkeyset.contains(bean.getGroupKey())){
                        //生成消息
                        sys_appmsgserv.saveMsgVoFromImage(uBean.getPk_corp(), bean.getGroupKey());
                        groupkeyset.add(bean.getGroupKey());
                    }
                }

            }
        }
    }

    private void validateForUpload(UserBeanVO uBean) {
        CorpVO[] corpvos = AppQueryUtil.getInstance().getDemoCorpMsg();
        // 判断是否demo公司
        String demoname = null;
        if (corpvos != null && corpvos.length > 0) {
            if (!StringUtil.isEmpty(corpvos[0].getUnitname())) {
                demoname = CodeUtils1.deCode(corpvos[0].getUnitname());
            }
        }
        if (demoname == null || (!StringUtil.isEmpty(uBean.getCorpname()) && !demoname.equals(uBean.getCorpname()))) {
            IAppUserService iaus = (IAppUserService) SpringUtils.getBean("userservice");
            if (iaus.getPrivilege(IConstant.IMAGE, uBean.getAccount(), "", uBean.getPk_corp(),
                    uBean.getPk_tempcorp())) {
                throw new BusinessException("您没该权限，请联系管理员!");
            }
        }

        if (AppCheckValidUtils.isEmptyCorp(uBean.getPk_corp())) {
            throw new BusinessException("您公司尚未正式签约，不能上传图片!");
        } else {
            CorpVO cpvo = iZxkjRemoteAppService.queryByPk(uBean.getPk_corp());
            if (cpvo == null) {
                throw new BusinessException("您公司尚未正式签约，不能上传图片!");
            }
        }
    }


}

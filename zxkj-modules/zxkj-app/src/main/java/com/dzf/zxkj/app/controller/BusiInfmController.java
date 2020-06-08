package com.dzf.zxkj.app.controller;

import com.dzf.zxkj.app.model.resp.bean.ImageBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.sys.OcrInvoiceVOForApp;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.service.app.act.IImageService;
import com.dzf.zxkj.app.service.photo.IImageProviderPhoto;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.CryptUtil;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RestController
@RequestMapping("/app/busiServlet")
public class BusiInfmController {

    @Autowired
    private IUserPubService userPubService;


    @RequestMapping("/downLoadImage")
    public void downLoadImage(ImageBeanVO imageBean, HttpServletResponse response)  {//图片下载
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
    /**
     * 获取识别信息
     */
    @RequestMapping("/imageIdentify")
    public ReturnData<ResponseBaseBeanVO> imageIdentify(String groupkey) {

        ResponseBaseBeanVO beanvo = new ResponseBaseBeanVO();

        IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");

        try {
            OcrInvoiceVOForApp[] orcvos = ip.querySbVos(groupkey);
            beanvo.setRescode(IConstant.DEFAULT);
            beanvo.setResmsg(orcvos);
        } catch (Exception e) {
            log.error("查询识别历史失败!", log);
            beanvo.setResmsg(e.getMessage());
            beanvo.setRescode(IConstant.FIRDES);

        }
        return  ReturnData.ok().data(beanvo);
    }
    @RequestMapping("/imageIdentifyDetail")
    public ReturnData<ResponseBaseBeanVO> imageIdentifyDetail(String ocrid,String sb_status) {
        ResponseBaseBeanVO beanvo = new ResponseBaseBeanVO();

        IImageProviderPhoto ip = (IImageProviderPhoto) SpringUtils.getBean("poimp_imagepro");

        try {
            OcrInvoiceVOForApp orcvo = null;
            if (StringUtil.isEmpty(ocrid)) {
                orcvo= new OcrInvoiceVOForApp();
                if(StringUtil.isEmpty(sb_status)){
                    orcvo.setSb_status( 2);//识别中
                }else{
                    orcvo.setSb_status(Integer.parseInt(sb_status));//识别中
                }
                orcvo.setInvoicetype("1");//默认普票
            }else{
                orcvo = ip.querySbDetail(ocrid);
            }
            beanvo.setRescode(IConstant.DEFAULT);
            beanvo.setResmsg(orcvo);
        } catch (Exception e) {
            log.error("查询识别详情失败!", log);
            beanvo.setResmsg(e.getMessage());
            beanvo.setRescode(IConstant.FIRDES);
        }
        return ReturnData.ok().data(beanvo);
    }
}

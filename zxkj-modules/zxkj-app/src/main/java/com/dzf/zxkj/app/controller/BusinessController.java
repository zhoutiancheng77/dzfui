package com.dzf.zxkj.app.controller;

import com.dzf.admin.dzfapp.model.filetrans.*;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.econtract.IDzfAppEcontractService;
import com.dzf.admin.dzfapp.service.filetrans.IDzfAppFiletransService;
import com.dzf.zxkj.app.model.report.ZqVo;
import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.BusinessResonseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportResBean;
import com.dzf.zxkj.app.model.sys.*;
import com.dzf.zxkj.app.pub.constant.IBusiConstant;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.app.act.IQryReport1Service;
import com.dzf.zxkj.app.service.bill.IAppInvoiceService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.service.ticket.impl.AppBwTicketImpl;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/app/busihandlesvlt")
public class BusinessController {

    @Autowired
    private IQryReport1Service orgreport1;
    @Autowired
    private IAppInvoiceService invoiceservice;
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IUserPubService userPubService;
    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppFiletransService iDzfAppFiletransService;


    @RequestMapping("/doBusiAction")
    public ReturnData<BusinessResonseBeanVO> doBusiAction(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        UserVO uservo = userPubService.queryUserVOId(userbean.getAccount_id());
        userbean.setAccount_id(uservo.getCuserid());
        Integer operate = Integer.parseInt(userbean.getOperate());
        switch (operate) {
//            case IBusiConstant.SEVENTY_THREE:
//                bean = appbusihand.getWorkTips(userbean);
//                break;
//            case IBusiConstant.NINE_TWO:
//                bean = doSaveTickmsg(userbean);
//                break;
//            case IBusiConstant.NINE_THREE:// 图片生成凭证
//                bean = dobusiVoucher(getRequest(), bean);
//                break;
//            //订单
//            case IBusiConstant.NINE_FIVE:
//            case IBusiConstant.NINE_SIX:
//            case IBusiConstant.NINE_SEVEN:
//            case IBusiConstant.NINE_EIGTH:
//            case IBusiConstant.NINE_NINE:
//            case IBusiConstant.NINE_ZERO_ONE:
//            case IBusiConstant.NINE_ZERO_TWO:
//                bean = doOrder(operate,getRequest(), userbean);
//                break;
//            //审批流
//            case IBusiConstant.NINE_ZERO_THREE:
//            case IBusiConstant.NINE_ZERO_FIVE:
//            case IBusiConstant.NINE_ZERO_FOUR:
//            case IBusiConstant.NINE_ZERO_SIX:
//                bean = doApprove(operate,getRequest(), userbean);
//                break;
//            case IBusiConstant.NINE_ZERO_SEVEN://百旺信息生成凭证
//                bean = dobwbusiVoucher(getRequest(), bean);
//                break;
//            case IBusiConstant.NINE_ZERO_EIGTH://作废信息
//                bean = dobwdelbusiVoucher(getRequest(), bean);
//                break;
//            case IBusiConstant.NINE_TEN://网络抄报
//                bean = copyDeclareTax(getRequest(), bean);
//                break;
            //资料交接
            case IBusiConstant.ZERO://资料
            case IBusiConstant.ONE:
            case IBusiConstant.TWO:
            case IBusiConstant.THREE:
            case IBusiConstant.FOUR:
            case IBusiConstant.FIVE:
            case IBusiConstant.SIX:
            case IBusiConstant.SEVEN:
            case IBusiConstant.EIGTH:
            case IBusiConstant.NINE:
            case IBusiConstant.TEN:
            case IBusiConstant.ELEVEN:
            case IBusiConstant.EIGHTEEN:
                bean = doMaterial(operate,userbean);
                break;
//            //进项销项发票查询
//            case IBusiConstant.TWELVE://票据
//            case IBusiConstant.THIRTEEN:
//                bean = doCollticket(operate,userbean);
//                break;
//            case IBusiConstant.FOURTEEN://常见问题
//                bean = doProblem();
//                break;
//            case IBusiConstant.TRADE_QRY://行业档案查询
//                bean = doTradeQry(userbean);
//                break;
//            //---------扫描营业执照----------
//            case IBusiConstant.SCAN_YYZZ:
//                bean = doScanYyzz(userbean);
//                break;
            //-----获取首页信息(目前有 营业执照链接，征期信息)
            case IBusiConstant.INDEX_MSG:
                bean = doIndexMsg(userbean);
                break;
//            case IBusiConstant.MORE_SERVICE://更多服务
//                bean = doMoreService(userbean);
//                break;
//            case IBusiConstant.MORE_SERVICE_DETAIL://服务详情
//                bean = doMoreServiceDetail(userbean);
//                break;
            default:
                break;
        }
        return ReturnData.ok().data(bean);
    }

    private BusinessResonseBeanVO doIndexMsg(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        Integer versionno =  userbean.getVersionno();

        if(versionno.intValue()<= IVersionConstant.VERSIONNO327){
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(new String[]{"http://qyxy.baic.gov.cn","http://www.jsgsj.gov.cn:58888"});
        }else if(versionno.intValue() >=IVersionConstant.VERSIONNO328){//
            try {
                Map<String, Object> indexmsg = new HashMap<String, Object>();
                indexmsg.put("yyzzurl", new String[]{"http://qyxy.baic.gov.cn","http://www.jsgsj.gov.cn:58888"});//营业执照信息
                List<String> msglist = new ArrayList<String>();
                if(!AppCheckValidUtils.isEmptyCorp(userbean.getPk_corp())){
                    ReportResBean rptbean = orgreport1.qryZqrl(userbean.getPk_corp(), DateUtils.getPeriod(new DZFDate()));
                    //rptbean 转换成信息
                    List<ZqVo> zqvos =  rptbean.getZqlist();
                    if(zqvos!=null && zqvos.size()>0){
                        for(ZqVo vo:zqvos){
                            if(!"0".equals(vo.getZqlx())){
                                msglist.add(vo.getTitle());
                            }
                        }
                    }
                }
                indexmsg.put("zqmsg", msglist.toArray(new String[0]));
                indexmsg.put("zbmsg", getZbMap(userbean));//指标信息
                bean.setIndexmsg(indexmsg);
                bean.setRescode(IConstant.DEFAULT);
            } catch (Exception e) {
                log.error( "首页信息获取失败", log);
            }
        }
        return bean;
    }

    private Map<String, String> getZbMap(BusiReqBeanVo userbean) {
        Map<String, String> zbmap = new LinkedHashMap<String, String>();
        String period = DateUtils.getPeriod(new DZFDate());
        DZFDate begindate = DateUtils.getPeriodStartDate(period);
        DZFDate enddate = DateUtils.getPeriodEndDate(period);
        //财务进度
        zbmap.put("cwjd_zt", orgreport1.queryNsgzt(userbean.getPk_corp(),period ));
        zbmap.put("cwjd_scdj", orgreport1.queryLibnum(userbean.getPk_corp(), begindate, enddate)+"张");
        zbmap.put("cwjd_kjpz", orgreport1.queryPzNum(userbean.getPk_corp(), begindate, enddate)+"张");

        //财务指标
        Map<String, DZFDouble> cwzbmap = orgreport1.queryCwzb(userbean.getPk_corp(), period);
        zbmap.put("cwzb_jlr", Common.format( cwzbmap.get("jlr")));//净利润
        zbmap.put("cwzb_sr", Common.format(cwzbmap.get("sr")));//收入
        zbmap.put("cwzb_zc", Common.format(cwzbmap.get("zc")));//支出
        zbmap.put("cwzb_fy", Common.format(cwzbmap.get("fy")));//费用

        //发票清单
        Object[] xxobj = invoiceservice.queryXXTotal(userbean.getPk_corp(), period);
        Object[] jxobj = invoiceservice.queryJxTotal(userbean.getPk_corp(), period);
        zbmap.put("fpqd_xx_zs", xxobj[0]+"张");//销项发票张数
        zbmap.put("fpqd_xx_je", Common.format((DZFDouble)xxobj[1]));//销项发票金额
        zbmap.put("fpqd_jx_zs", jxobj[0]+"张");//进项发票张数
        zbmap.put("fpqd_jx_je",  Common.format((DZFDouble)jxobj[1]));//进项发票金额

        //资金状况
        Map<String, CwgyInfoVO> cwgymap = orgreport1.getCwgyMap(userbean.getPk_corp(), period);
        zbmap.put("zjzk_zjsr_bq", getValueFromCwgy(cwgymap,"资金状况_资金收入",0));//资金收入
        zbmap.put("zjzk_zjsr_lj",  getValueFromCwgy(cwgymap,"资金状况_资金收入",1));
        zbmap.put("zjzk_zjzf_bq",  getValueFromCwgy(cwgymap,"资金状况_资金支付",0));//资金支付
        zbmap.put("zjzk_zjzf_lj", getValueFromCwgy(cwgymap,"资金状况_资金支付",1));

        //应收应付
        zbmap.put("ysyf_yszk_bq", getValueFromCwgy(cwgymap,"应收应付_应收账款",0));//应收账款 本期
        zbmap.put("ysyf_yszk_lj", getValueFromCwgy(cwgymap,"应收应付_应收账款",1));//应收账款 累计

        zbmap.put("ysyf_yfzk_bq", getValueFromCwgy(cwgymap,"应收应付_应付账款",0));//应付账款 本期
        zbmap.put("ysyf_yfzk_lj", getValueFromCwgy(cwgymap,"应收应付_应付账款",1));//应付账款 累计

        return zbmap;
    }
    /**
     * 根据项目信息，获取财务概要信息
     * @param cwgymap
     * @param key
     */
    private String getValueFromCwgy(Map<String, CwgyInfoVO> cwgymap, String key,Integer lx) {
        if(cwgymap.containsKey(key)
                && cwgymap.get(key)!=null){
            if(lx == 0){
                return Common.format(cwgymap.get(key).getByje());
            }else{
                return Common.format(cwgymap.get(key).getBnljje());
            }
        }
        return "0.00";
    }
    private BusinessResonseBeanVO doMaterial(Integer operate, BusiReqBeanVo userbean) {

        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();

        try {
            if(AppCheckValidUtils.isEmptyCorp(userbean.getPk_corp())){
                throw new BusinessException("公司尚未签约!");
            }

            CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, userbean.getPk_corp());

            userbean.setFathercorpid(cpvo.getFathercorp());

            switch (operate) {
                case IBusiConstant.ZERO://资料交接列表
                    doMaterList(userbean,bean);
                    break;
                case IBusiConstant.ONE://资料交接列表-确认
                    doMaterListConfirm(userbean,bean);
                    break;
                case IBusiConstant.TWO://查询资料档案
                    doQueryFiledoc(userbean,bean);
                    break;
                case IBusiConstant.THREE://保存添加资料
                    doSaveFiletrans(userbean,bean);
                    break;
                case IBusiConstant.FOUR://当面交(生成二维码数据)
                    doSaveHandin(userbean,bean);
                    break;
                case IBusiConstant.FIVE://当面收(根据二维码生成清单)
                    doQueryHandin(userbean,bean);
                    break;
                case IBusiConstant.SIX://当面收-确认
                    doSaveHandinConf(userbean,bean);
                    break;
                case IBusiConstant.SEVEN://转交（查询接收人信息）
                    doQueryCatcher(userbean,bean);
                    break;
                case IBusiConstant.EIGTH://转交确认发送
                    doSaveSurrConf(userbean,bean);
                    break;
                case IBusiConstant.NINE://查询资料消息详情
                    doQueryAppFiletrans(userbean,bean);
                    break;
                case IBusiConstant.TEN://整单确认
                    doUpdateAllConfirm(userbean,bean);
                    break;
                case IBusiConstant.ELEVEN://获取二维码状态
                    doQueryQrStatus(userbean,bean);
                    break;
                case IBusiConstant.EIGHTEEN://查询资料类型
                    doQueryZllx(userbean,bean);
                    break;
                default:
                    break;
            }
        }  catch (Exception e) {
            log.error( "操作失败!", log);
        }

        return bean;
    }
    private void doMaterList(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setPk_corpk(userbean.getPk_corp());
            List<FiletransBVO> bvos = (List<FiletransBVO>)iDzfAppFiletransService.queryFileList(pramvo).getData();
            if(bvos == null || bvos.size() == 0){
                throw new BusinessException("暂无数据!");
            }
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(bvos);
        } catch (Exception e) {
            log.error("查询列表出错!", log);
        }

    }
    private void doMaterListConfirm(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {
        try {
            if(StringUtil.isEmpty(userbean.getId())){
                throw new BusinessException("资料信息为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setPk_zj(userbean.getId());
            iDzfAppFiletransService.updateConfirm(pramvo);
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("操作成功!");
        } catch (Exception e) {
            log.error("操作失败!", log);
        }
    }
    private void doQueryFiledoc(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            List<DataFileDocVO> docvos = (List<DataFileDocVO>)iDzfAppFiletransService.queryFiledoc(pramvo).getData();
            if(docvos == null || docvos.size() == 0){
                throw new BusinessException("暂无记录!");
            }
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(docvos);
        } catch (Exception e) {
            log.error("查询失败!", log);
        }
    }
    private void doSaveFiletrans(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setBodys(userbean.getBodys());
            if(userbean.getVersionno()!=null &&
                    userbean.getVersionno().intValue()<=IVersionConstant.VERSIONNO326 ){
                pramvo.setFileid(userbean.getFileid());
                pramvo.setVbegperiod(userbean.getVbegperiod());
                pramvo.setNum(userbean.getNum());
                pramvo.setVendperiod(userbean.getVendperiod());
                pramvo.setVmemo(userbean.getMemo());
                iDzfAppFiletransService.saveFiletrans(pramvo);
            }else{
                iDzfAppFiletransService.saveFiles(pramvo );
            }

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("操作成功!");
        } catch (Exception e) {
            log.error( "操作失败!", log);
        }
    }
    private void doSaveHandin(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            if(StringUtil.isEmpty(userbean.getFileids())){
                throw new BusinessException("交接信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setFileids(userbean.getFileids());
            pramvo.setPk_corp(userbean.getFathercorpid());
            QueryBeanVO querybeanvo = (QueryBeanVO)iDzfAppFiletransService.saveHandin(pramvo).getData();

            if(querybeanvo == null || StringUtil.isEmpty(querybeanvo.getQrid())){
                throw new BusinessException("获取信息失败!");
            }

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("操作成功!");
            bean.setQrid(querybeanvo.getQrid());
        } catch (Exception e) {
            log.error( "操作失败!", log);
        }
    }
    private void doQueryHandin(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            if(StringUtil.isEmpty(userbean.getQrid())){
                throw new BusinessException("二维码信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setQrid(userbean.getQrid());
            pramvo.setPk_corp(userbean.getFathercorpid());
            FileHandinVO filehandinvo = (FileHandinVO)iDzfAppFiletransService.queryHandin(pramvo).getData();

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(filehandinvo);
        } catch (Exception e) {
            log.error("获取信息失败!", log);
        }
    }
    private void doSaveHandinConf(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            if(StringUtil.isEmpty(userbean.getQrid())){
                throw new BusinessException("二维码信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setQrid(userbean.getQrid());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setSysytpe(2);
            iDzfAppFiletransService.doHandinConf(pramvo);

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("操作成功!");
        } catch (Exception e) {
            log.error( "操作失败!", log);
        }
    }
    /**
     * 转交（查询接收人信息）
     * @param userbean
     * @param bean
     */
    private void doQueryCatcher(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setPk_corp(userbean.getFathercorpid());
            AppQunGroup[] groups = (AppQunGroup[])iDzfAppFiletransService.queryCatcher(pramvo,null).getData();

            if (groups == null || groups.length == 0) {
                throw new BusinessException("暂无数据!");
            }

            bean.setRescode(IConstant.DEFAULT);

            bean.setResmsg(groups);

        } catch (Exception e) {
            log.error( "获取信息失败!", log);
        }
    }
    /**
     * 转交确认发送
     * @param userbean
     * @param bean
     */
    private void doSaveSurrConf(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {


        try {

            if(StringUtil.isEmpty(userbean.getFileids())){
                throw new BusinessException("转交资料信息不能为空!");
            }

            if(StringUtil.isEmpty(userbean.getPk_zj())){
                throw new BusinessException("转交人信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setFileids(userbean.getFileids());
            pramvo.setPk_zj(userbean.getPk_zj());
            iDzfAppFiletransService.saveSurrConf(pramvo);

            bean.setRescode(IConstant.DEFAULT);

            bean.setResmsg("操作成功!");

        } catch (Exception e) {
            log.error("操作失败!", log);
        }

    }
    /**
     * 查询资料消息详情
     * @param userbean
     * @param bean
     */
    private void doQueryAppFiletrans(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {

            if(StringUtil.isEmpty(userbean.getPk_jjid())){
                throw new BusinessException("交接单信息不能为空!");
            }
            if(StringUtil.isEmpty(userbean.getPk_msgid())){
                throw new BusinessException("消息信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setPk_jjid(userbean.getPk_jjid());
            pramvo.setPk_msgid(userbean.getPk_msgid());
            AppFiletransHVO appfilethvo = (AppFiletransHVO)iDzfAppFiletransService.queryAppFiletrans(pramvo).getData();

            if(appfilethvo == null){
                throw new BusinessException("获取信息失败!");
            }

            bean.setRescode(IConstant.DEFAULT);

            bean.setResmsg(appfilethvo);

        } catch (Exception e) {
            log.error("获取信息失败!", log);
        }

    }
    /**
     * 整单确认
     * @param userbean
     * @param bean
     */
    private void doUpdateAllConfirm(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {

            if(StringUtil.isEmpty(userbean.getPk_jjid())){
                throw new BusinessException("交接单信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setPk_jjid(userbean.getPk_jjid());
            iDzfAppFiletransService.updateAllConfirm(pramvo);

            bean.setRescode(IConstant.DEFAULT);

            bean.setResmsg("操作成功!");

        } catch (Exception e) {
            log.error("操作失败!", log );
        }

    }
    private void doQueryQrStatus(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            if(StringUtil.isEmpty(userbean.getQrid())){
                throw new BusinessException("二维码信息不能为空!");
            }
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setQrid(userbean.getQrid());
            pramvo.setPk_corpk(userbean.getPk_corp());
            Integer qrstatus = iDzfAppFiletransService.queryQrStatus(pramvo).getData();

            if(qrstatus == null){
                throw new BusinessException("获取状态信息失败!");
            }

            bean.setRescode(IConstant.DEFAULT);

            if(qrstatus == 0){
                bean.setResmsg("您二维码信息已经失效，请重新生成!");
            }else if(qrstatus == 1){
                bean.setResmsg("二维码信息待确认!");
            }else if(qrstatus == 2){
                bean.setResmsg("资料已经交接成功!");
            }
            bean.setQrstatus(String.valueOf(qrstatus));

        } catch (Exception e) {
            log.error( "获取状态信息失败!", log);
        }
    }
    private void doQueryZllx(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {
        FiletransVO vo =null;
        List<FiletransVO>  list =new ArrayList<FiletransVO>();
        String[] name={"原件","复印件","电子资料","打印资料"};
        for(int i=0;i<name.length;i++){
            vo =new FiletransVO();
            vo.setFiletypename(name[i]);
            vo.setIfiletype(i);
            list.add(vo);
        }
        bean.setRescode(IConstant.DEFAULT);
        bean.setResmsg(list);
    }
}

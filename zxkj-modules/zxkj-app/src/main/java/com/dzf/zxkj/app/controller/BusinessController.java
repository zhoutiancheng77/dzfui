package com.dzf.zxkj.app.controller;

import com.dzf.admin.dzfapp.model.filetrans.AppFiletransQryVO;
import com.dzf.admin.dzfapp.model.result.AppResult;
import com.dzf.admin.dzfapp.service.filetrans.IDzfAppFiletransService;
import com.dzf.admin.model.app.transfer.filetrans.RetAppFiletransBVO;
import com.dzf.admin.model.app.transfer.filetrans.RetAppQunGroup;
import com.dzf.admin.model.app.transfer.filetrans.RetDataFileDocVO;
import com.dzf.zxkj.app.model.app.corp.ScanCorpInfoVO;
import com.dzf.zxkj.app.model.approve.ApproveSetVo;
import com.dzf.zxkj.app.model.report.ZqVo;
import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.*;
import com.dzf.zxkj.app.model.sys.DataFileDocVO;
import com.dzf.zxkj.app.model.sys.FiletransVO;
import com.dzf.zxkj.app.model.sys.ProblemVo;
import com.dzf.zxkj.app.pub.constant.IBusiConstant;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.IScanCorpInfo;
import com.dzf.zxkj.app.service.app.act.IAppApproveService;
import com.dzf.zxkj.app.service.app.act.IAppBusinessService;
import com.dzf.zxkj.app.service.app.act.IQryReport1Service;
import com.dzf.zxkj.app.service.bill.IAppInvoiceService;
import com.dzf.zxkj.app.service.pub.IUserPubService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.app.utils.AppkeyUtil;
import com.dzf.zxkj.app.utils.BaseTimerCache;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@Slf4j
@RestController
@RequestMapping("/app/busihandlesvlt")
public class BusinessController extends  BaseAppController{

    @Autowired
    private IQryReport1Service orgreport1;
    @Autowired
    private IAppInvoiceService invoiceservice;
    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IAppBusinessService appbusihand;
    @Reference(version = "1.0.0", protocol = "dubbo", timeout = Integer.MAX_VALUE, retries = 0)
    private IDzfAppFiletransService iDzfAppFiletransService;

    @Autowired
    private IScanCorpInfo scancorpSer;

    public static BaseTimerCache<String, Object> busi_identify = new BaseTimerCache<String, Object>();

    @RequestMapping("/doBusiAction")
    public ReturnData<BusinessResonseBeanVO> doBusiAction(@RequestParam Map<String,Object> param) {
        BusiReqBeanVo userbean = new BusiReqBeanVo();ApproveSetVo approveset = new ApproveSetVo();
        changeParamvo(param,userbean,approveset);
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
//        UserVO uservo = userPubService.queryUserVOId(userbean.getAccount_id());
//        userbean.setAccount_id(uservo.getCuserid());
        Integer operate = Integer.parseInt(userbean.getOperate());
        switch (operate) {
//            case IBusiConstant.SEVENTY_THREE:
//                bean = appbusihand.getWorkTips(userbean);
//                break;
            case IBusiConstant.NINE_TWO:
                bean = doSaveTickmsg(userbean);
                break;
            case IBusiConstant.NINE_THREE:// 图片生成凭证
                bean = dobusiVoucher(userbean, bean);
                break;
            //审批流
            case IBusiConstant.NINE_ZERO_THREE:
            case IBusiConstant.NINE_ZERO_FIVE:
            case IBusiConstant.NINE_ZERO_FOUR:
            case IBusiConstant.NINE_ZERO_SIX:
                bean = doApprove(operate,approveset, userbean);
                break;
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
             case IBusiConstant.FOURTEEN://常见问题
                bean = doProblem();
                break;
//            //进项销项发票查询
//            case IBusiConstant.TWELVE://票据
//            case IBusiConstant.THIRTEEN:
//                bean = doCollticket(operate,userbean);
//                break;

//            case IBusiConstant.TRADE_QRY://行业档案查询
//                bean = doTradeQry(userbean);
//                break;
//            //---------扫描营业执照----------
            case IBusiConstant.SCAN_YYZZ:
                bean = doScanYyzz(userbean);
                break;
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

    private void changeParamvo(Map<String,Object> param ,BusiReqBeanVo busireqvo,ApproveSetVo approveset){
        AppkeyUtil.setMulAppValue(param,busireqvo,new Class[]{BusiReqBeanVo.class, UserBeanVO.class, RequestBaseBeanVO.class} );
        AppkeyUtil.setAppValue(param,approveset );
        UserVO uservo = queryUserVOId((String)param.get("account_id"));
        busireqvo.setAccount_id(uservo.getCuserid());
        busireqvo.setUserid(uservo.getCuserid());
        return ;
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
                printErrorJson(bean,e,log,"首页信息获取失败");
               // log.error( "首页信息获取失败", log);
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
            printErrorJson(bean,e,log,"操作失败!");
            //log.error( "操作失败!", log);
        }

        return bean;
    }
    private void doMaterList(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            pramvo.setPk_corpk(userbean.getPk_corp());
            pramvo.setQrid(userbean.getQrid());
            pramvo.setQrytype(userbean.getQrytype());
            List<RetAppFiletransBVO> bvos = (List<RetAppFiletransBVO>)iDzfAppFiletransService.queryFileList(pramvo).getData();
            if(bvos == null || bvos.size() == 0){
                throw new BusinessException("暂无数据!");
            }
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(bvos);
        } catch (Exception e) {
            printErrorJson(bean,e,log,"操作失败!");
//            bean.setRescode(IConstant.FIRDES);
//            bean.setResmsg(e.getMessage());
//            log.error(e.getMessage(), log);
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
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("操作失败!", log);
            printErrorJson(bean,e,log,"操作失败!");
        }
    }
    private void doQueryFiledoc(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppFiletransQryVO pramvo = new AppFiletransQryVO();
            pramvo.setCuserid(userbean.getAccount_id());
            pramvo.setPk_corp(userbean.getFathercorpid());
            List<RetDataFileDocVO> docvos = (List<RetDataFileDocVO>)iDzfAppFiletransService.queryFiledoc(pramvo).getData();
            if(docvos == null || docvos.size() == 0){
                throw new BusinessException("暂无记录!");
            }
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(docvos);
        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("查询失败!", log);
            printErrorJson(bean,e,log,"查询失败!");
        }
    }
    private void doSaveFiletrans(BusiReqBeanVo userbean, BusinessResonseBeanVO bean) {

        try {
            AppResult result = null;
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
                result =  iDzfAppFiletransService.saveFiletrans(pramvo);
            }else{
                result =  iDzfAppFiletransService.saveFiles(pramvo );
            }
            if(result.getCode() != 200){
                bean.setRescode(IConstant.FIRDES);
                bean.setResmsg(result.getMsg());
            }else{
                bean.setRescode(IConstant.DEFAULT);
                bean.setResmsg("操作成功!");
            }

        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
          //  log.error( "操作失败!", log);
            printErrorJson(bean,e,log,"操作失败!");
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
            AppResult querybeanvo = iDzfAppFiletransService.saveHandin(pramvo);

            if(querybeanvo == null || StringUtil.isEmpty((String)querybeanvo.getData())){
                throw new BusinessException("获取信息失败!");
            }

            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg("操作成功!");
            bean.setQrid((String)querybeanvo.getData());
        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error( "操作失败!", log);
            printErrorJson(bean,e,log,"操作失败!");
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
            AppResult filehandinvo = (AppResult)iDzfAppFiletransService.queryHandin(pramvo);
            if(filehandinvo.getCode() != 200){
                throw new BusinessException(filehandinvo.getMsg());
            }
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(filehandinvo.getData());
        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("获取信息失败!", log);
            printErrorJson(bean,e,log,"获取信息失败!");
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
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error( "操作失败!", log);
            printErrorJson(bean,e,log,"操作失败!");
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
            AppResult<RetAppQunGroup> groups = (AppResult<RetAppQunGroup>)iDzfAppFiletransService.queryCatcher(pramvo,null);

            if (groups.getData() == null)  {
                throw new BusinessException("暂无数据!");
            }

            bean.setRescode(IConstant.DEFAULT);

            bean.setResmsg(groups.getData());

        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error( "获取信息失败!", log);
            printErrorJson(bean,e,log,"获取信息失败!");
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
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("操作失败!", log);
            printErrorJson(bean,e,log,"操作失败!");
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
            AppResult appfilethvo = iDzfAppFiletransService.queryAppFiletrans(pramvo);

            if(appfilethvo == null || appfilethvo.getData() == null){
                throw new BusinessException("获取信息失败!");
            }

            bean.setRescode(IConstant.DEFAULT);

            bean.setResmsg(appfilethvo.getData());

        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("获取信息失败!", log);
            printErrorJson(bean,e,log,"获取信息失败!");
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
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("操作失败!", log );
            printErrorJson(bean,e,log,"操作失败!");
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
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error( "获取状态信息失败!", log);
            printErrorJson(bean,e,log,"获取状态信息失败!");
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
    private BusinessResonseBeanVO doSaveTickmsg(BusiReqBeanVo userbean){
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        try {

            // 如果该公司没签约则返回是空的
            if (AppCheckValidUtils.isEmptyCorp(userbean.getPk_corp())) {
                throw new BusinessException("您公司没签约，不能做相关业务!");
            }

            bean = appbusihand.saveTickMsg(userbean);
        } catch (Exception e) {
//            bean.setRescode(IConstant.FIRDES);
//            bean.setResmsg(e.getMessage());
//            log.error("获取票据信息失败!",log);
            printErrorJson(bean,e,log,"获取票据信息失败!");
        }
        return bean;
    }
    private BusinessResonseBeanVO dobusiVoucher(BusiReqBeanVo uBean, ResponseBaseBeanVO bean) {

        BusinessResonseBeanVO beanvo = new BusinessResonseBeanVO();
        try {

            // 如果该公司没签约则返回是空的
            if (AppCheckValidUtils.isEmptyCorp(uBean.getPk_corp())) {
                throw new BusinessException("您公司没签约，不能做相关业务!");
            }

            ImageGroupVO groupvo =  appbusihand.saveImgFromTicket(uBean);

            try {
                appbusihand.saveVoucherFromTicket(uBean,groupvo);
            } catch (Exception e) {
                if(groupvo!=null){
                    singleObjectBO.deleteObject(groupvo);
                }
//                bean.setResmsg(e.getMessage());
//                bean.setRescode(IConstant.FIRDES);
//                log.error("生成凭证失败!", log);
                printErrorJson(beanvo,e,log,"生成凭证失败!");
                return beanvo;
            }

            beanvo.setRescode(IConstant.DEFAULT);

            beanvo.setResmsg("操作成功!");

        } catch (Exception e) {
//            beanvo.setResmsg(e.getMessage());
//            beanvo.setRescode(IConstant.FIRDES);
//            log.error("生成凭证失败!", log);
            printErrorJson(beanvo,e,log,"生成凭证失败!");
        }

        return beanvo;
    }
    private BusinessResonseBeanVO doProblem() {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        try {
            List<ProblemVo> list = appbusihand.getProblems();
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(list);
        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("获取数据失败", log);
            printErrorJson(bean,e,log,"获取数据失败!");
        }
        return bean;
    }
    /**
     *
     * @param request
     * @param userbean
     * @return
     */
    private BusinessResonseBeanVO doApprove(Integer operate, ApproveSetVo approveset, BusiReqBeanVo userbean) {

        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();

        try {
            switch (operate) {
                case IBusiConstant.NINE_ZERO_THREE://审批流保存
                    bean =  doApproveSave(approveset,userbean);
                    break;
                case IBusiConstant.NINE_ZERO_FIVE://审批流查询
                    bean = doApproveQry(userbean);
                    break;
                case IBusiConstant.NINE_ZERO_FOUR://审批流审核
                    bean = doApprove(userbean);
                    break;
                case IBusiConstant.NINE_ZERO_SIX://审批流驳回
                    bean = doReturn(userbean);
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("审批操作失败!", log);
            printErrorJson(bean,e,log,"审批操作失败!");
        }

        return bean;
    }
    private BusinessResonseBeanVO doReturn(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        try {
            // 如果该公司没签约则返回是空的
            if (AppCheckValidUtils.isEmptyCorp(userbean.getPk_corp())) {
                throw new BusinessException("您公司没签约，不能做相关业务!");
            }

            IAppApproveService approveser = (IAppApproveService) SpringUtils.getBean("appapprovehand");
            bean =  approveser.updateReject(userbean);
        } catch (DZFWarpException e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error( "驳回失败!", log);
            printErrorJson(bean,e,log,"驳回失败!");
        }

        return bean;
    }
    private BusinessResonseBeanVO doApprove(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        try {
            // 如果该公司没签约则返回是空的
            if (AppCheckValidUtils.isEmptyCorp(userbean.getPk_corp())) {
                throw new BusinessException("您公司没签约，不能做相关业务!");
            }
            IAppApproveService approveser = (IAppApproveService) SpringUtils.getBean("appapprovehand");
            bean =  approveser.updateApprove(userbean,singleObjectBO);

            //审批成功后是否匹配模板生成凭证
//			try {
//				IAppBusinessService iappbusihand = (IAppBusinessService) SpringUtils.getBean("appbusihand");
//				iappbusihand.saveVoucherFromPic(userbean.getPk_image_group(),
//						userbean.getPk_corp());
//			} catch (Exception e) {
//
//			}
        } catch (DZFWarpException e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("审批失败!", log);
            printErrorJson(bean,e,log,"审批失败!");
        }

        return bean;
    }
    private BusinessResonseBeanVO doApproveSave(ApproveSetVo approveset, BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();
        try {

            IAppApproveService approveser = (IAppApproveService) SpringUtils.getBean("appapprovehand");

            bean =  approveser.saveApproveSet(approveset);
        } catch (DZFWarpException e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("保存审批流设置失败!", log);
            printErrorJson(bean,e,log,"保存审批流设置失败!");
        }

        return bean;

    }
    private BusinessResonseBeanVO doApproveQry(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean= new BusinessResonseBeanVO();
        try {
            IAppApproveService approveser = (IAppApproveService) SpringUtils.getBean("appapprovehand");
            bean = approveser.queryApprovSet(userbean);
        } catch (DZFWarpException e) {
//            bean.setResmsg(e.getMessage());
//            bean.setRescode(IConstant.FIRDES);
//            log.error("查询审批流设置失败!", log);
            printErrorJson(bean,e,log,"查询审批流设置失败!");
        }
        return bean;
    }


    /**
     * 扫描营业执照
     * @param userbean
     * @return
     */
    private BusinessResonseBeanVO doScanYyzz(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO  bean= new BusinessResonseBeanVO();
        try {
            ScanCorpInfoVO infovo = scancorpSer.scanPermit(userbean.getDrcode());
            infovo.setInnercode(infovo.getVsoccrecode());
            bean.setRescode(IConstant.DEFAULT);
            bean.setResmsg(infovo.getUnitname());
            busi_identify.put(infovo.getUnitname(), infovo);
        } catch (Exception e) {
            printErrorJson(bean, e, log, "信息不存在");
        }

        return bean;
    }
}

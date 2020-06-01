package com.dzf.zxkj.app.controller;

import com.dzf.zxkj.app.model.report.ZqVo;
import com.dzf.zxkj.app.model.req.BusiReqBeanVo;
import com.dzf.zxkj.app.model.resp.bean.BusinessResonseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.ReportResBean;
import com.dzf.zxkj.app.pub.constant.IBusiConstant;
import com.dzf.zxkj.app.pub.constant.IConstant;
import com.dzf.zxkj.app.pub.constant.IVersionConstant;
import com.dzf.zxkj.app.service.app.act.IQryReport1Service;
import com.dzf.zxkj.app.service.bill.IAppInvoiceService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.common.entity.ReturnData;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Common;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.platform.model.report.CwgyInfoVO;
import lombok.extern.slf4j.Slf4j;
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

    @RequestMapping("/doBusiAction")
    public ReturnData<BusinessResonseBeanVO> doBusiAction(BusiReqBeanVo userbean) {
        BusinessResonseBeanVO bean = new BusinessResonseBeanVO();

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
//            //资料交接
//            case IBusiConstant.ZERO://资料
//            case IBusiConstant.ONE:
//            case IBusiConstant.TWO:
//            case IBusiConstant.THREE:
//            case IBusiConstant.FOUR:
//            case IBusiConstant.FIVE:
//            case IBusiConstant.SIX:
//            case IBusiConstant.SEVEN:
//            case IBusiConstant.EIGTH:
//            case IBusiConstant.NINE:
//            case IBusiConstant.TEN:
//            case IBusiConstant.ELEVEN:
//            case IBusiConstant.EIGHTEEN:
//                bean = doMaterial(operate,userbean);
//                break;
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
     * @param string
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
}

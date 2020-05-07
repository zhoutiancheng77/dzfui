package com.dzf.zxkj.platform.service.http.impl;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.Base64CodeUtils;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.bill.InvoiceApplyVO;
import com.dzf.zxkj.platform.model.piaotong.*;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO2;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceVO2;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.service.bill.IInvoiceApplyService;
import com.dzf.zxkj.platform.service.http.IVptInvService;
import com.dzf.zxkj.platform.service.zncs.IVATSaleInvoice2Service;
import com.dzf.zxkj.platform.util.zncs.CommonXml;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.PiaoTongUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("vpt_invserv")
@Slf4j
public class VptInvServiceImpl implements IVptInvService {

    @Autowired
    private SingleObjectBO singleObjectBO;
    @Autowired
    private IInvoiceApplyService invservice;
    @Autowired
    private IBillingProcessService iBillingProcessService;
    @Autowired
    private IVATSaleInvoice2Service gl_vatsalinvserv2;

    @Override
    public void saveRegis(PiaoTongReqVO reqvo) throws DZFWarpException {
        //验签

        //报文内容
        String content = analyzContent(reqvo);
        InvoiceApplyVO vo = JSON.parseObject(content, InvoiceApplyVO.class);

        //先找到对应的公司信息
//		vo.setEnterpriseName("test2");
//		vo.setTaxpayerNum("9111XXXXXXXXXXXXXX");
        List<InvoiceApplyVO> appList = invservice.queryInviceByCode(vo.getEnterpriseName(), vo.getTaxpayerNum());

        List<InvoiceApplyVO> result = new ArrayList<InvoiceApplyVO>();
        if(appList != null && appList.size() > 0){
            Integer status;
            for(InvoiceApplyVO appvo : appList){
                status = appvo.getIstatus();
                if(status != null
                        && status == IInvoiceApplyConstant.APPLY_STATUS_3){//申请中
                    result.add(appvo);
                }
            }
        }

        if(result.size() == 0)
            throw new BusinessException("未找到对应开通企业信息");

        List<String> fields = new ArrayList<String>();
        for(InvoiceApplyVO avo : result){

            if(CommonXml.rtnsucccode.equals(vo.getCode())){
                avo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_5);
                avo.setPtbm(vo.getPlatformCode());
                avo.setZcm(vo.getRegistrationCode());
                avo.setSqm(vo.getAuthorizationCode());

                fields.add("ptbm");
                fields.add("zcm");
                fields.add("sqm");
            }else{
                avo.setIstatus(IInvoiceApplyConstant.APPLY_STATUS_4);
            }
            fields.add("istatus");
            avo.setMemo(vo.getMsg());
            avo.setModifydatetime(new DZFDateTime());

            fields.add("memo");
            fields.add("modifydatetime");
        }

        singleObjectBO.updateAry(result.toArray(new InvoiceApplyVO[0]),
                fields.toArray(new String[0]));

    }

    private String analyzContent(PiaoTongReqVO reqVO){
        String content = reqVO.getContent();
        //解析
        content = PiaoTongUtil.decrypt(content);

        return content;
    }

    @Override
    public boolean updateInvoice(PiaoTongReqVO reqVO) throws DZFWarpException {
        String content = analyzContent(reqVO);
        PiaoTongResInvVO invVO = JSON.parseObject(content, PiaoTongResInvVO.class);

        BillApplyVO applyVO = iBillingProcessService.queryBySerialNo(invVO.getInvoiceReqSerialNo());

        if(applyVO == null){
            throw new BusinessException("未找到对应流水号对应的单据");
        }

        String code = invVO.getInvoiceIssueResultCode();//返回编码
        if(CommonXml.rtnsucccode.equals(code)){
            //判断是蓝票 还是红票
            String type = invVO.getInvoiceIssueType();

            //中间表
            CaiFangTongHVO hvo = transfer(invVO, applyVO);
            singleObjectBO.saveObject(hvo.getPk_corp(), hvo);

            if(IInvoiceApplyConstant.ISSUETYPE_1.equals(type)){
                applyVO.setFpdm(hvo.getFpdm());
                applyVO.setFphm(hvo.getFphm());
                applyVO.setDdate(StringUtil.isEmpty(hvo.getKprq())
                        ? null : new DZFDateTime(hvo.getKprq()));
                applyVO.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_1);
                setDownUrl(applyVO, hvo);
                singleObjectBO.update(applyVO, new String[]{ "fpdm", "fphm", "ddate", "ibillstatus", "downurl" });

                //后续调用 销项接口
                callXXQD(hvo, applyVO.getPk_corp());
            }else if(IInvoiceApplyConstant.ISSUETYPE_2.equals(type)){//红票
                applyVO.setFpdm(hvo.getFpdm());
                applyVO.setFphm(hvo.getFphm());
                applyVO.setDdate(StringUtil.isEmpty(hvo.getKprq())
                        ? null : new DZFDateTime(hvo.getKprq()));
                applyVO.setRedflag(IInvoiceApplyConstant.RED_FLAG_1);
                applyVO.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_1);
                setDownUrl(applyVO, hvo);
                singleObjectBO.update(applyVO, new String[]{ "fpdm", "fphm", "redflag", "ddate", "ibillstatus", "downurl" });

//        		//找红冲的蓝票
//        		BillApplyVO bluevo = iBillingProcessService.queryByFPDMHM(applyVO.getFpdm(),
//        				applyVO.getFphm(), applyVO.getPk_corp());
//        		if(bluevo == null){
//        			throw new BusinessException("未找到红票对应的蓝票单据");
//        		}

                //后续调用 销项接口
                callXXQD(hvo, applyVO.getPk_corp());
            }

        }else {
            //判断是蓝票 还是红票
            String type = invVO.getInvoiceIssueType();
            if(IInvoiceApplyConstant.ISSUETYPE_1.equals(type)){
                applyVO.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_6);
                applyVO.setMemo(invVO.getInvoiceIssueResultMsg());
                singleObjectBO.update(applyVO, new String[]{ "ibillstatus", "memo" });
            }else if(IInvoiceApplyConstant.ISSUETYPE_2.equals(type)){//红票
                applyVO.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_6);
                applyVO.setMemo(invVO.getInvoiceIssueResultMsg());
                applyVO.setRedflag(IInvoiceApplyConstant.RED_FLAG_2);
                singleObjectBO.update(applyVO, new String[]{ "ibillstatus", "redflag", "memo" });
            }

        }
        return true;
    }

    private void setDownUrl(BillApplyVO applyVO, CaiFangTongHVO hvo){
        String url = hvo.getVdef1();
        if(!StringUtil.isEmpty(url)){
            byte[] bytes;
            try {
                bytes = Base64CodeUtils.decode(url);// 先base64解码
                url = new String(bytes,"UTF-8");
                applyVO.setDownurl(url);//下载地址
            } catch (Exception e) {

            }
        }

    }

    private void callXXQD(CaiFangTongHVO hvo, String pk_corp){
        Map<String, VATSaleInvoiceVO2[]> sendData = new HashMap<String, VATSaleInvoiceVO2[]>();
        VATSaleInvoiceVO2 vo = buildXjVOs(hvo, null, 0);
        sendData.put("adddocvos", new VATSaleInvoiceVO2[]{ vo });
        VATSaleInvoiceVO2[] addvos = gl_vatsalinvserv2.updateVOArr(pk_corp, sendData);
    }

    private VATSaleInvoiceVO2 buildXjVOs(CaiFangTongHVO hvo, String userid, int dr){
        VATSaleInvoiceVO2 svo = new VATSaleInvoiceVO2();
        svo.setXhfmc(hvo.getXsf_nsrmc());
        svo.setXhfsbh(hvo.getXsf_nsrsbh());
        svo.setXhfdzdh(transNullValue(hvo.getXsf_dz()) + transNullValue(hvo.getXsf_dh()));
        svo.setXhfyhzh(transNullValue(hvo.getXsf_yh()) + transNullValue(hvo.getXsf_yhzh()));
        svo.setFp_dm(hvo.getFpdm());
        svo.setFp_hm(hvo.getFphm());

        svo.setKprj(new DZFDateTime(hvo.getKprq()).getDate());
        svo.setJshj(new DZFDouble(hvo.getKphjje()));
        svo.setHjje(new DZFDouble(hvo.getHjbhsje()));
        svo.setSpse(new DZFDouble(hvo.getKphjse()));
        svo.setKhmc(hvo.getGmf_nsrmc());
        svo.setCustidentno(hvo.getGmf_nsrsbh());
        svo.setGhfdzdh(transNullValue(hvo.getGmf_dz()) + transNullValue(hvo.getGmf_dh()));
        svo.setGhfyhzh(transNullValue(hvo.getGmf_yh()) + transNullValue(hvo.getGmf_yhzh()));

        svo.setPeriod(DateUtils.getPeriod(svo.getKprj()));
        svo.setInperiod(svo.getPeriod());//入账期间
//		svo.setIszhuan(ICaiFangTongConstant.FPZLDM_Z0.equals(
//				hvo.getFp_zldm()) ? DZFBoolean.TRUE : DZFBoolean.FALSE);
        svo.setIszhuan(DZFBoolean.FALSE);//目前只有普票

        svo.setCoperatorid(userid);
        svo.setDoperatedate(new DZFDate());
        svo.setPk_corp(hvo.getPk_corp());
        svo.setSourcetype(IBillManageConstants.PIAOTONGKP_CALLBACK);
        svo.setSourcebillid(hvo.getPk_caifangtong_h());
        svo.setSourcebilltype(ICaiFangTongConstant.LYDJLX_KP);
        svo.setKplx(hvo.getKplx());

        CaiFangTongBVO[] bvos = hvo.getChildren();
        CaiFangTongBVO bvo = null;
        VATSaleInvoiceBVO2[] sbvos = new VATSaleInvoiceBVO2[bvos.length];
        VATSaleInvoiceBVO2 sbvo = null;
        String spmcstr = null;
        for(int i = 0; i < bvos.length; i++){
            bvo = bvos[i];

            sbvo = new VATSaleInvoiceBVO2();

            if(!StringUtil.isEmpty(bvo.getSpmc())
                    && StringUtil.isEmpty(spmcstr)){
                spmcstr = bvo.getSpmc();
            }

            sbvo.setRowno(Integer.parseInt(bvo.getSphxh()));//商品行序号

            sbvo.setBspmc(bvo.getSpmc());

            sbvo.setBnum(new DZFDouble(bvo.getSpsl()));
            sbvo.setBhjje(new DZFDouble(bvo.getSpje()));
            sbvo.setBprice(new DZFDouble(bvo.getSpdj()));
            sbvo.setMeasurename(bvo.getDw());
            sbvo.setInvspec(bvo.getGgxh());
            sbvo.setBspse(new DZFDouble(bvo.getSe()));
            sbvo.setBspsl(SafeCompute.multiply(new DZFDouble(bvo.getSl()), new DZFDouble(100)));//税率

            sbvo.setPk_corp(hvo.getPk_corp());
            sbvo.setDr(dr);

            sbvos[i] = sbvo;
        }

        svo.setSpmc(spmcstr);
//		svo.setSpsl(spsl);
        svo.setChildren(sbvos);
        svo.setDr(dr);
        return svo;
    }

    private String transNullValue(String value){

        return StringUtil.isEmpty(value) ? "" : value;
    }

    private CaiFangTongHVO transfer(PiaoTongResInvVO invVO, BillApplyVO applyVO){

        PiaoTongResInvHVO hvo = invVO.getInvoiceInfo();
        String pk_corp = applyVO.getPk_corp();

        Map<String, String> hmap = getCFTHMapping();
        Map<String, String> bmap = getCFTBMapping();
        CaiFangTongHVO cftvo = new CaiFangTongHVO();
        Object value = null;
        for(Map.Entry<String, String> entry : hmap.entrySet()){
            value = hvo.getAttributeValue(entry.getKey());

            if(value != null){
                cftvo.setAttributeValue(entry.getValue(), value);
            }
        }

        cftvo.setLy(ICaiFangTongConstant._LYLX_HXP);
        cftvo.setDoperatedate(new DZFDate());
        cftvo.setPk_corp(pk_corp);

        List<PiaoTongResInvBVO> ptbvos = hvo.getItemList();
        List<CaiFangTongBVO> cftbvos = new ArrayList<CaiFangTongBVO>();
        CaiFangTongBVO cftbvo;
        if(ptbvos != null && ptbvos.size() > 0){
            for(PiaoTongResInvBVO ptbvo : ptbvos){
                cftbvo = new CaiFangTongBVO();
                for(Map.Entry<String, String> entry1 : bmap.entrySet()){
                    value = ptbvo.getAttributeValue(entry1.getKey());

                    if(value != null){
                        cftbvo.setAttributeValue(entry1.getValue(), value);
                    }
                }
                cftbvo.setPk_corp(pk_corp);
                cftbvos.add(cftbvo);
            }
        }else{
            cftbvo = new CaiFangTongBVO();
            cftbvo.setPk_corp(pk_corp);;
            cftbvo.setSpje(cftvo.getHjbhsje());
            cftbvo.setSe(cftvo.getKphjse());
        }

        cftvo.setChildren(cftbvos.toArray(
                new CaiFangTongBVO[0]));

        return cftvo;
    }

    private Map<String, String> getCFTHMapping(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("invoiceReqSerialNo", "fpqqlsh");//发票请求流水号
        map.put("sellerName", "xsf_nsrmc");//销方名称
        map.put("sellerTaxpayerNum", "xsf_nsrsbh");//销方纳税人识别号
        map.put("invoiceCode", "fpdm");//发票代码
        map.put("invoiceNo", "fphm");//发票号码
        map.put("invoiceDate", "kprq");//开票日期
        map.put("invoiceType", "kplx");//开票类型
        map.put("amountWithTax", "kphjje");//开票合计金额
        map.put("noTaxAmount", "hjbhsje");//合计不含税金额
        map.put("taxAmount", "kphjse");//开票合计税额
        map.put("buyerTaxpayerNum", "gmf_nsrsbh");//购货方纳税人识别号
        map.put("buyerName", "gmf_nsrmc");//购货方纳税人名称
        map.put("buyerBankName", "gmf_yh");//购货方银行
        map.put("buyerBankAccount", "gmf_yhzh");//购货方银行账号
        map.put("buyerAddress", "gmf_dz");//购货方地址
        map.put("buyerTel", "gmf_dh");//购货方电话
//        map.put("buyerProvince", "gmf_sf");//购货方省份
//        map.put("buyerPhone", "gmf_sj");//购货方手机
//        map.put("buyerEmail", "gmf_email");//购货方邮箱
        map.put("oldInvoiceCode", "yfphm");//原发票号码
        map.put("oldInvoiceNo", "yfpdm");//原发票代码
        map.put("machineCode", "jqbh");//机器编号
        map.put("drawerName", "kpy");//开票员
        map.put("casherName", "sky");//收款员
        map.put("reviewerName", "fhr");//复核人
        map.put("invoiceKindCode", "fp_zldm");//发票种类代码
        map.put("sellerAddress", "xsf_dz");//销售方地址
        map.put("sellerTel", "xsf_dh");//销售方电话
        map.put("sellerBankName", "xsf_yh");//销售方银行
        map.put("sellerBankAccount", "xsf_yhzh");//销售方银行账号
        map.put("extensionNum", "fjh");//分机号
//        map.put("businessPlatformCode", "dsptbm");//电商平台编码
//        map.put("agentInvoiceFlag", "dkbz");//代开标志
        map.put("redFlag", "tschbz");//特殊冲红标志
//        map.put("redReason", "chyy");//冲红原因
//        map.put("taxClassificationCodeVersion", "bmbbbh");//编码表版本号
//        map.put("taxControlCode", "skm");//税控码
        map.put("qrCode", "ewm");//二维码
        map.put("remark", "bz");//备注
//        map.put("cipherText", "fp_mw");//防伪密文
        map.put("checkCode", "jym");//校验码
        map.put("specialInvoiceKind", "tspz");//特殊票种
//        map.put("includeTaxValueFlag", "slbz");//含税税率标识
//        map.put("buyFlag", "sgbz");//收购标志
        map.put("downloadUrl", "vdef1");//发票下载 Url
        return map;
    }

    private Map<String, String> getCFTBMapping(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("itemNo", "sphxh");//商品行序号
        map.put("goodsName", "spmc");//商品名称
        map.put("quantity", "spsl");//商品数量
        map.put("itemAmount", "spje");//商品金额
        map.put("unitPrice", "spdj");//商品单价
        map.put("meteringUnit", "dw");//单位
        map.put("specificationModel", "ggxh");//规格型号
//        map.put("includeTaxFlag", "hsjbz");//含税价标志
        map.put("deduction", "kce");//扣除额
        map.put("taxRateAmount", "se");//税额
        map.put("taxRateValue", "sl");//税率
        map.put("taxClassificationCode", "spbm");//税商品编码
//        map.put("customCode", "zxbm");//自行编码
        map.put("preferentialPolicyFlag", "yhzcbs");//优惠政策标识
        map.put("zeroTaxFlag", "lslbs");//零税率标识
        map.put("vatSpecialManage", "zzstsgl");//增值税特殊管理
        map.put("itemProperty", "fphxz");//发票行性质

        return map;
    }
}

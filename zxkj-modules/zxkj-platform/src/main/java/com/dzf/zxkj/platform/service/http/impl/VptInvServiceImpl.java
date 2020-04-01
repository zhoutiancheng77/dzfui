package com.dzf.zxkj.platform.service.http.impl;

import com.alibaba.fastjson.JSON;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.constant.IInvoiceApplyConstant;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.bill.BillApplyVO;
import com.dzf.zxkj.platform.model.piaotong.*;
import com.dzf.zxkj.platform.service.bill.IBillingProcessService;
import com.dzf.zxkj.platform.service.http.IVptInvService;
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
    private IBillingProcessService iBillingProcessService;

    @Override
    public boolean updateInvoice(PiaoTongReqVO reqVO) throws DZFWarpException {
        String content = analyzContent(reqVO);
        PiaoTongResInvVO invVO = JSON.parseObject(content, PiaoTongResInvVO.class);

        BillApplyVO applyVO = iBillingProcessService.queryBySerialNo(invVO.getInvoiceReqSerialNo());

        CaiFangTongHVO hvo = transfer(invVO, applyVO);

        //
        applyVO.setFpdm(hvo.getFpdm());
        applyVO.setFphm(hvo.getFphm());
        applyVO.setIbillstatus(IInvoiceApplyConstant.INV_STATUS_1);
        singleObjectBO.update(applyVO, new String[]{ "fpdm", "fphm", "ibillstatus" });

        singleObjectBO.saveObject(hvo.getPk_corp(), hvo);
        //后续调用 销项接口

        return true;
    }

    private CaiFangTongHVO transfer(PiaoTongResInvVO invVO, BillApplyVO applyVO){

        PiaoTongResHVO hvo = invVO.getInvoiceInfo();
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

        List<PiaoTongResBVO> ptbvos = hvo.getItemList();
        List<CaiFangTongBVO> cftbvos = new ArrayList<CaiFangTongBVO>();
        CaiFangTongBVO cftbvo;
        if(ptbvos != null && ptbvos.size() > 0){
            for(PiaoTongResBVO ptbvo : ptbvos){
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
        map.put("sellerEnterpriseName", "xsf_nsrmc");//销方名称
        map.put("sellerTaxpayerNum", "xsf_nsrsbh");//销方纳税人识别号
        map.put("invoiceCode", "fpdm");//发票代码
        map.put("invoiceNo", "fphm");//发票号码
        map.put("invoiceTime", "kprq");//开票日期
        map.put("invoiceType", "kplx");//开票类型
        map.put("amount", "kphjje");//开票合计金额
        map.put("noTaxAmount", "hjbhsje");//合计不含税金额
        map.put("taxAmount", "kphjse");//开票合计税额
        map.put("buyerTaxpayerNum", "gmf_nsrsbh");//购货方纳税人识别号
        map.put("buyerName", "gmf_nsrmc");//购货方纳税人名称
        map.put("buyerBankName", "gmf_yh");//购货方银行
        map.put("buyerBankAccount", "gmf_yhzh");//购货方银行账号
        map.put("buyerAddress", "gmf_dz");//购货方地址
        map.put("buyerTel", "gmf_dh");//购货方电话
        map.put("buyerProvince", "gmf_sf");//购货方省份
        map.put("buyerPhone", "gmf_sj");//购货方手机
        map.put("buyerEmail", "gmf_email");//购货方邮箱
        map.put("originalInvoiceNo", "yfphm");//原发票号码
        map.put("originalInvoiceCode", "yfpdm");//原发票代码
        map.put("machineCode", "jqbh");//机器编号
        map.put("drawerName", "kpy");//开票员
        map.put("takerName", "sky");//收款员
        map.put("reviewerName", "fhr");//复核人
        map.put("invoiceKindCode", "fp_zldm");//发票种类代码
        map.put("sellerAddress", "xsf_dz");//销售方地址
        map.put("sellerTel", "xsf_dh");//销售方电话
        map.put("sellerBankName", "xsf_yh");//销售方银行
        map.put("sellerBankAccount", "xsf_yhzh");//销售方银行账号
        map.put("extensionNum", "fjh");//分机号
        map.put("businessPlatformCode", "dsptbm");//电商平台编码
        map.put("agentInvoiceFlag", "dkbz");//代开标志
        map.put("specialRedFlag", "tschbz");//特殊冲红标志
        map.put("redReason", "chyy");//冲红原因
        map.put("taxClassificationCodeVersion", "bmbbbh");//编码表版本号
        map.put("taxControlCode", "skm");//税控码
        map.put("qrCode", "ewm");//二维码
        map.put("remark", "bz");//备注
        map.put("cipherText", "fp_mw");//防伪密文
        map.put("securityCode", "jym");//校验码
        map.put("specialInvoiceKind", "tspz");//特殊票种
        map.put("includeTaxValueFlag", "slbz");//含税税率标识
        map.put("buyFlag", "sgbz");//收购标志

        return map;
    }

    private Map<String, String> getCFTBMapping(){
        Map<String, String> map = new HashMap<String, String>();

        map.put("goodsSerialNo", "sphxh");//商品行序号
        map.put("goodsName", "spmc");//商品名称
        map.put("quantity", "spsl");//商品数量
        map.put("invoiceAmount", "spje");//商品金额
        map.put("unitPrice", "spdj");//商品单价
        map.put("meteringUnit", "dw");//单位
        map.put("specificationModel", "ggxh");//规格型号
        map.put("includeTaxFlag", "hsjbz");//含税价标志
        map.put("deductionAmount", "kce");//扣除额
        map.put("taxRateAmount", "se");//税额
        map.put("taxRateValue", "sl");//税率
        map.put("taxClassificationCode", "spbm");//税商品编码
        map.put("customCode", "zxbm");//自行编码
        map.put("preferentialPolicyFlag", "yhzcbs");//优惠政策标识
        map.put("zeroTaxFla", "lslbs");//零税率标识
        map.put("vatSpecialManage", "zzstsgl");//增值税特殊管理
        map.put("itemType", "fphxz");//发票行性质

        return map;
    }

    private String analyzContent(PiaoTongReqVO reqVO){
        String content = reqVO.getContent();
        //解析
        content = PiaoTongUtil.decrypt(content);

        return content;
    }
}

package com.dzf.zxkj.platform.service.zncs.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.FieldConstant;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.lang.*;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongBVO;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongHVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResBVO;
import com.dzf.zxkj.platform.model.piaotong.PiaoTongResHVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO2;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceVO2;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.zncs.IBankStatement2Service;
import com.dzf.zxkj.platform.service.zncs.IPiaoTongKp2Service;
import com.dzf.zxkj.platform.service.zncs.IVATSaleInvoice2Service;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.PiaotongKp1;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("piaotongkpservice2")
public class PiaoTongKp2ServiceImpl implements IPiaoTongKp2Service {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired
	private IBankStatement2Service gl_yhdzdserv2;
	@Autowired
	private IDcpzService dcpzjmbserv;
	@Autowired
	private IVATSaleInvoice2Service gl_vatsalinvserv;
	
	@Override
	public Map<String, VATSaleInvoiceVO2> saveKp(CorpVO corpvo, String userid, VATSaleInvoiceVO2 paramvo) throws DZFWarpException {
		CaiFangTongHVO[] hvos = getCaiFangTongBillInfo(corpvo, userid, paramvo);
		
		if(hvos == null 
				|| hvos.length == 0)
			return null;
		
		String pk_corp = corpvo.getPk_corp();
		VATSaleInvoiceVO2 salevo = null;
		VATSaleInvoiceVO2 oldSalevo = null;
		String kplx = null;
		String demo = null;
		String hm = null;
//		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
//		Map<String, DcModelHVO> dcMap = hashlizeDcModel(dcList);
		Map<String, VATSaleInvoiceVO2> repMap = new LinkedHashMap<String, VATSaleInvoiceVO2>();
		for(CaiFangTongHVO hvo : hvos){
			
			hm = hvo.getFphm();
			kplx = hvo.getKplx();
			oldSalevo = queryByCGTId(hvo.getFphm(), hvo.getFpdm(), pk_corp);//查询库里是否存在此票
			
			if(!StringUtil.isEmpty(kplx)
					&& (ICaiFangTongConstant.FPLX_1.equals(kplx)
							|| ICaiFangTongConstant.FPLX_2.equals(kplx)
							|| ICaiFangTongConstant.FPLX_3.equals(kplx))){
				int dr = 0;
				if(oldSalevo != null){//判断是增加  还是删除
					dr = 1;
					setVatCFTDelVO(hvo, dr, "新增前校验到该号码代码数据库中已有数据");
				}

				hvo = (CaiFangTongHVO) singleObjectBO.saveObject(pk_corp, hvo);
				
				salevo = buildXjVOs(hvo, userid, dr);
				salevo.setVersion(new DZFDouble(1.0));
				//设置结算方式，结算科目，入账科目
//				if(!StringUtil.isEmpty(salevo.getPk_model_h())){
//					CategorysetVO setVO = gl_yhdzdserv2.queryCategorySetVO(salevo.getPk_model_h());
//					salevo.setSettlement(setVO.getSettlement()==null?0:setVO.getSettlement());
//					salevo.setPk_subject(setVO.getPk_accsubj());
//					salevo.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
//				}
				
				
				salevo = (VATSaleInvoiceVO2) singleObjectBO.saveObject(pk_corp, salevo);
				
				if(dr == 0){
					repMap.put(hm, salevo);
				}
			}else if(ICaiFangTongConstant.FPLX_4.equals(kplx)
					|| ICaiFangTongConstant.FPLX_5.equals(kplx)){
				int dr = 0;
				if(oldSalevo == null){
					dr = 0;
					demo = "新增前校验到该号码代码(废票)数据库中没有数据";
				}else{
					updateSaleVOStatus(oldSalevo, hvo, kplx, pk_corp);
					dr = 1;
					demo = "新增前校验到该号码代码(废票)数据库中有数据";
				}
				
				setVatCFTDelVO(hvo, dr, demo);
				hvo = (CaiFangTongHVO) singleObjectBO.saveObject(pk_corp, hvo);
				
				salevo = buildXjVOs(hvo, userid, dr);
				
				salevo = (VATSaleInvoiceVO2) singleObjectBO.saveObject(pk_corp, salevo);
				
				if(dr == 0){
					repMap.put(hm, salevo);
				}
			}
			
		}
		
		return repMap;
	}

private Map<String, DcModelHVO> hashlizeDcModel(List<DcModelHVO> list){
		
		if(list == null || list.size() == 0)
			return null;
		
		Map<String, DcModelHVO> map = new HashMap<String, DcModelHVO>();
		String key = null;
		String szcode = null;
		String vscode = null;
		String businame = null;
		for(DcModelHVO hvo : list){
			szcode = hvo.getSzstylecode();
			vscode = hvo.getVspstylecode();
			businame = hvo.getBusitypetempname();
			if(FieldConstant.SZSTYLE_05.equals(szcode)
					&& (FieldConstant.FPSTYLE_01.equals(vscode)
							|| FieldConstant.FPSTYLE_02.equals(vscode))
					&& (FieldConstant.YWSTYLE_22.equals(businame)
							|| FieldConstant.YWSTYLE_15.equals(businame))){//只过滤出 增值税 销售收入、服务收入 其他收入  业务类型模板
				
				key = hvo.getBusitypetempname()
						+ "_" + hvo.getVspstylecode()
						+ "_" + hvo.getSzstylecode();//业务类型名称+票据类型+结算方式
				
				if(!map.containsKey(key)){
					map.put(key, hvo);
				}
			}
		}
		
		return map;
	}
	
	private void setVatCFTDelVO(CaiFangTongHVO hvo, int dr, String demo){
		hvo.setDr(dr);//删除标识
		hvo.setDemo1(demo);
		CaiFangTongBVO[] cbvos = hvo.getChildren();
		if(cbvos != null && cbvos.length > 0){
			for(CaiFangTongBVO bvo : cbvos){
				bvo.setDr(dr);
			}
		}
	}
	
	private void updateSaleVOStatus(VATSaleInvoiceVO2 salevo, CaiFangTongHVO hvo, String kplx, String pk_corp){
		
		salevo.setKplx(kplx);
		singleObjectBO.update(salevo, 
				new String[]{"kplx"});
		
		if(!StringUtil.isEmpty(salevo.getPk_tzpz_h())){
			VoucherParamVO paramvo = new VoucherParamVO();
			paramvo.setPk_tzpz_h(salevo.getPk_tzpz_h());
			TzpzHVO tzpzhvo = gl_tzpzserv.queryHeadVoById(paramvo);
			if(tzpzhvo != null){
				gl_tzpzserv.deleteVoucher(tzpzhvo);
			}
		}
		
	}
	
	private VATSaleInvoiceVO2 queryByCGTId(String fphm, String fpdm, String pk_corp){
		String sql = "nvl(dr,0) = 0 and pk_corp = ? and fp_hm = ? and fp_dm = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(fphm);
		sp.addParam(fpdm);
		
		List<VATSaleInvoiceVO2> list = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sql, sp,
				new Class[]{VATSaleInvoiceVO2.class, VATSaleInvoiceBVO2.class});
		
		return list == null || list.size() == 0 ? null : list.get(0);
	}
	
	private VATSaleInvoiceVO2 buildXjVOs(CaiFangTongHVO hvo, String userid, int dr){
		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
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
		svo.setIszhuan(ICaiFangTongConstant.FPZLDM_Z0.equals(
				hvo.getFp_zldm()) ? DZFBoolean.TRUE : DZFBoolean.FALSE);
//		DZFDouble bsl = DZFDouble.ZERO_DBL;
//		bsl = SafeCompute.multiply(SafeCompute.div(svo.getSpse(), svo.getHjje()), new DZFDouble(100));
//		bsl = bsl.setScale(0, DZFDouble.ROUND_HALF_UP);
//		svo.setSpsl(bsl);
		
		
		svo.setCoperatorid(userid);
		svo.setDoperatedate(new DZFDate());
		svo.setPk_corp(hvo.getPk_corp());
		svo.setSourcetype(IBillManageConstants.CAIFANGTONG);
		svo.setSourcebillid(hvo.getPk_caifangtong_h());
		svo.setSourcebilltype(ICaiFangTongConstant.LYDJLX);
		svo.setKplx(hvo.getKplx());
		
		CaiFangTongBVO[] bvos = hvo.getChildren();
		CaiFangTongBVO bvo = null;
		VATSaleInvoiceBVO2[] sbvos = new VATSaleInvoiceBVO2[bvos.length];
		VATSaleInvoiceBVO2 sbvo = null;
		DZFDouble spsl = null;
		String spmcstr = null;
		DZFDouble bbsl = DZFDouble.ZERO_DBL;
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
			
//			bslstr = bvo.getSl();
//			bslstr = StringUtil.isEmpty(bslstr) ? "" : (bslstr.endsWith("%") ? bslstr.substring(0, bslstr.length()-1) : bslstr);
			bbsl = SafeCompute.multiply(SafeCompute.div(sbvo.getBspse(), sbvo.getBhjje()), new DZFDouble(100));
			bbsl = bbsl.setScale(0, DZFDouble.ROUND_HALF_UP);
			
			sbvo.setBspsl(bbsl);
			if(bbsl != null && spsl == null){
				spsl = bbsl;
			}
			
			sbvo.setPk_corp(hvo.getPk_corp());
			sbvo.setDr(dr);
			
			sbvos[i] = sbvo;
		}
		
		svo.setSpmc(spmcstr);
		svo.setSpsl(spsl);
		//gl_vatsalinvserv.scanMatchBusiName(svo, dcMap);//匹配模板
		svo.setChildren(sbvos);
		svo.setDr(dr);
		list.add(svo);
		List<VATSaleInvoiceVO2> saleList = gl_vatsalinvserv.changeToSale(list, list.get(0).getPk_corp());//分类
		return saleList.get(0);
	}
	
//	private void setBusiName(Map<String, DcModelHVO> map, String spmc, VATSaleInvoiceVO2 vo){
//		if(StringUtil.isEmpty(spmc))
//			return;
//		
//		String key = null;
//		String busiName = null;
//		DZFBoolean iszh = vo.getIsZhuan();
//		String zp = iszh != null && iszh.booleanValue() 
//				? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
//		if(spmc.contains("费")
//				|| spmc.contains("劳务")){
//			busiName = FieldConstant.YWSTYLE_15;
//		}else{
//			busiName = FieldConstant.YWSTYLE_22;
//		}
//		
//		key = busiName
//				+ "_" + zp
//				+ "_" + FieldConstant.SZSTYLE_05;
//		
//		DcModelHVO hvo = map.get(key);
//		if(hvo != null && !StringUtil.isEmpty(hvo.getPk_model_h())){
//			vo.setPk_model_h(hvo.getPk_model_h());
//			vo.setBusitypetempname(hvo.getBusitypetempname());
//		}
//		
//	}
	
	private String transNullValue(String value){
		
		return StringUtil.isEmpty(value) ? "" : value;
	}
	
	private CaiFangTongHVO[] getCaiFangTongBillInfo(CorpVO corpvo, String userid, VATSaleInvoiceVO2 paramvo){
		CaiFangTongHVO[] hvos = null;
		
		if(corpvo == null)
			return hvos;
		
		String taxpayerNum = corpvo.getVsoccrecode();
		String unitname = corpvo.getUnitname();
		
		DZFDate kprq = paramvo.getKprj();
		String period = DateUtils.getPeriod(kprq);
		DZFDate d1 = DateUtils.getPeriodStartDate(period);
		DZFDate d2 = DateUtils.getPeriodEndDate(period);
				
		String startTime = new DZFDateTime(d1, new DZFTime("00:00:00")).toString();
		String endTime = new DZFDateTime(d2, new DZFTime("24:59:59")).toString();
		
		PiaotongKp1 kp = new PiaotongKp1(taxpayerNum, unitname, startTime, endTime, userid,
				corpvo.getPk_corp(), ICaiFangTongConstant.LYLX_XX_KP);
		
		int pageSize = Integer.parseInt(PiaotongKp1.xxsize);
		int pageIndex = 1;
		
		PiaoTongResHVO[] resHVOs = kp.getVOs(pageSize, pageIndex);
		
		Integer totalCount = kp.getTotalCount();
		if(totalCount == null
				|| resHVOs == null
				|| resHVOs.length == 0){
			return null;
		}
		
		List<PiaoTongResHVO> resList = new ArrayList<PiaoTongResHVO>();
		resList.addAll(Arrays.asList(resHVOs));
		
		while(totalCount > pageSize * pageIndex){
			pageIndex++;
			
			resHVOs = kp.getVOs(pageSize, pageIndex);
			resList.addAll(Arrays.asList(resHVOs));
		}
		
		hvos = transCFTData(resList, corpvo, userid);
		
		return hvos;
	}
	
	private CaiFangTongHVO[] transCFTData(List<PiaoTongResHVO> resList,
			CorpVO corpvo, String userid){
		
		if(resList == null || resList.size() == 0)
			return null;
		
		String pk_corp = corpvo.getPk_corp();
		List<CaiFangTongHVO> cftList = new ArrayList<CaiFangTongHVO>();
		Map<String, String> hmap = getCFTHMapping();
		Map<String, String> bmap = getCFTBMapping();
		
		CaiFangTongHVO cftvo = null;
		CaiFangTongBVO cftbvo = null;
		List<CaiFangTongBVO> cftbvos = null;
		List <PiaoTongResBVO> ptbvos = null;
		Object value = null;
		for(PiaoTongResHVO hvo : resList){
			
			cftvo = new CaiFangTongHVO();
			for(Map.Entry<String, String> entry : hmap.entrySet()){
				value = hvo.getAttributeValue(entry.getKey());
				
				if(value != null){
					cftvo.setAttributeValue(entry.getValue(), value);
				}
			}
			
			cftvo.setLy(ICaiFangTongConstant.LYLX_XX_KP);
			cftvo.setDoperatedate(new DZFDate());
			cftvo.setPk_corp(pk_corp);
			cftvo.setCoperatorid(userid);
			
			ptbvos = hvo.getItemList();
			cftbvos = new ArrayList<CaiFangTongBVO>();
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
			
			cftList.add(cftvo);
		}
		
		return cftList.toArray(new CaiFangTongHVO[0]);
		
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
	
//	private String getLastTime(TicketNssbhVO nssbvo, int sjly){
//		
//		String pk_corp = nssbvo.getPk_corp();
//		String where = "Select max(y.maxkprq) From ynt_caifangtong_h y Where nvl(y.dr,0) = 0 and pk_corp = ? and ly = ?";
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		sp.addParam(sjly);
//		
//		String maxTimestamp = (String) singleObjectBO.executeQuery(where, sp, new ObjectProcessor());
//		DZFDouble dzfMaxTime = new DZFDouble(maxTimestamp);
//		DZFDouble dzfCurTime = new DZFDouble(nssbvo.getCurtimestamp());
//		if(dzfMaxTime.compareTo(dzfCurTime) >= 0){
//			return dzfMaxTime.toString();
//		}else{
//			return dzfCurTime.toString();
//		}
//		
//	}

}

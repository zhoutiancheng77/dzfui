package com.dzf.zxkj.platform.service.zncs.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.constant.ImageTypeConst;
import com.dzf.zxkj.common.constant.InvoiceColumns;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.PhotoState;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.zncs.IOcrBillCreate;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service("ocr_billcreate")
public class OcrBillCreateImpl implements IOcrBillCreate {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	protected IDcpzService dcpzjmbserv;
	@Autowired
	private ICorpService corpService;
	// @Autowired
	// private IVoucherService gl_tzpzserv;

	private static final String moduleName = "WEB_AUTOCREATEBILL";
	@Override
	public String changBillCorpInfo(List<CorpVO> corpvos, OcrImageLibraryVO imagevo, OcrInvoiceVO invvo, ImageLibraryVO imglibs, ImageGroupVO groupvo, boolean isequal)throws DZFWarpException {
		log.info ( moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo开始匹配公司",log);
		if(corpvos==null||corpvos.size()==0) {
			log.info(moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo不需要匹配,结束",log);
			return null;
		}
		CorpVO corpvo = corpService.queryByPk(imagevo.getPk_custcorp());
		if(isMarch(corpvo, invvo)){
			log.info(moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo与导入一致,结束",log);
			return null;
		}
		
		String pk_corp = null;
		for (CorpVO corpVO : corpvos) {
			if(isMarch(corpVO, invvo)){
				pk_corp = corpVO.getPk_corp();
				log.info( moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo已匹配"+corpVO.getUnitname(),log);
				break;
			}
		}
		
		if(!StringUtil.isEmpty(pk_corp)){
			
			imglibs.setPk_corp(pk_corp);
			singleObjectBO.update(imglibs, new String[]{"pk_corp"});
			groupvo.setPk_corp(pk_corp);
			singleObjectBO.update(groupvo, new String[]{"pk_corp"});
			if(!isequal){
				StringBuffer buffer = new StringBuffer();
				buffer.append(" update ynt_image_ocrgroup set pk_selectcorp=? where pk_image_ocrgroup = ?");
				SQLParameter param = new SQLParameter();
				param.addParam(pk_corp);
				param.addParam(imagevo.getPk_image_ocrgroup());
				singleObjectBO.executeUpdate(buffer.toString(), param);
				imagevo.setPk_custcorp(pk_corp);
				singleObjectBO.update(imagevo, new String[]{"pk_custcorp"});
			}else{
				StringBuffer buffer = new StringBuffer();
				buffer.append(" update ynt_image_ocrgroup set pk_selectcorp=?,pk_corp =? where pk_image_ocrgroup = ?");
				SQLParameter param = new SQLParameter();
				param.addParam(pk_corp);
				param.addParam(pk_corp);
				param.addParam(imagevo.getPk_image_ocrgroup());
				singleObjectBO.executeUpdate(buffer.toString(), param);
				imagevo.setPk_custcorp(pk_corp);
				imagevo.setPk_corp(pk_corp);
				singleObjectBO.update(imagevo, new String[]{"pk_custcorp","pk_corp"});
			}
			
			
		}
		log.info( moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo匹配结束!",log);
		return pk_corp;
	}
	
	private boolean isMarch(CorpVO corpvo, OcrInvoiceVO invvo){
		
		if(!StringUtil.isEmpty(invvo.getVsalename()) && invvo.getVsalename().length()>2&& OcrUtil.isSameCompany(corpvo.getUnitname(), invvo.getVsalename())){
			log.info( moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo匹配的公司名称："+corpvo.getUnitname()+","+invvo.getVsalename()+","+invvo.getVpurchname(),log);

			log.info(moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfoVsalename匹配成功",log);
			return true;
		}
		if(!StringUtil.isEmpty(invvo.getVpurchname()) && invvo.getVpurchname().length()>2&&OcrUtil.isSameCompany(corpvo.getUnitname(), invvo.getVpurchname())){
			log.info(moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfo匹配的公司名称："+corpvo.getUnitname()+","+invvo.getVsalename()+","+invvo.getVpurchname(),log);

			log.info(moduleName, "WebID:" + invvo.getWebid() + "changBillCorpInfoVpurchname匹配成功",log);
			return true;
		}
		return false;
	}
	
	@Override
	public void createBill(OcrInvoiceVO invvo, ImageGroupVO grpvo,ImageLibraryVO imglibvo,VATInComInvoiceVO2 incomvos[]) throws DZFWarpException {
		log.info(moduleName, "WebID:" + invvo.getWebid() + "开始生成业务数据",log);
		CorpVO corpvo = corpService.queryByPk(invvo.getPk_corp());
		String tradesource=null;//回写出库入库单
		log.info(moduleName, "WebID:" + invvo.getWebid() + "公司名称:"+corpvo.getUnitname(),log);
		log.info(moduleName, "WebID:" + invvo.getWebid() + "购方名称:"+invvo.getVpurchname(),log);
		log.info(moduleName, "WebID:" + invvo.getWebid() + "销方名称:"+invvo.getVsalename(),log);
		if (!StringUtil.isEmpty(invvo.getIstate()) && (invvo.getIstate().equals("增值税发票"))) {

			String  pk_tzpz_h = null;
			//invvo.setVpurchname(corpvo.getUnitname());//测试用 jinxiang
			//invvo.setVsalename(corpvo.getUnitname());//销项
			int ifptype = matchImageType(invvo, corpvo, grpvo);
			if (VATInvoiceTypeConst.VAT_INCOM_INVOICE == ifptype) {// 进项
				log.info(moduleName, "WebID:" + invvo.getWebid() + "开始生成进项数据",log);
				VATInComInvoiceVO2 incomvo = new VATInComInvoiceVO2();
				VATInComInvoiceVO2 svo = (VATInComInvoiceVO2)checkIsExist(incomvo.getTableName(), invvo, imglibvo, VATInComInvoiceVO2.class);
				if(svo==null){
					incomvo = createVATInComInvoiceVO2(invvo, grpvo,imglibvo,incomvos);
					singleObjectBO.insertVO(invvo.getPk_corp(), incomvo);
				}else if(StringUtil.isEmpty(svo.getPk_image_library())){//IBillManageConstants
					tradesource = svo.getPrimaryKey();
					pk_tzpz_h = ((VATInComInvoiceVO2)svo).getPk_tzpz_h();
					incomvo.setPk_vatincominvoice(svo.getPrimaryKey());
					incomvo.setImgpath(getImgpath(imglibvo));
					incomvo.setPk_image_group(grpvo.getPk_image_group());
					incomvo.setPk_image_library(imglibvo.getPk_image_library());
					incomvo.setSourcebillid(imglibvo.getPk_image_library());
					incomvo.setVdef13(invvo.getPk_invoice());
//					incomvo.setVersion(IBillManageConstants.pjversion);			//进项发票绑图片，即使绑上图，进项还是不会更换版本，因为没有更换老业务类型。
					
					singleObjectBO.update(incomvo, new String[]{"vdef13","imgpath","pk_image_group","pk_image_library","sourcebillid"});//,"version"});
					//设置分类
					if(!StringUtil.isEmpty(svo.getPk_model_h())){
						invvo.setPk_billcategory(svo.getPk_model_h());
					}
				}
			} else if (VATInvoiceTypeConst.VAT_SALE_INVOICE == ifptype) {// 销项
				log.info( moduleName, "WebID:" + invvo.getWebid() + "开始生成销项数据",log);
				VATSaleInvoiceVO2 salevo = new VATSaleInvoiceVO2();
				VATSaleInvoiceVO2 svo = (VATSaleInvoiceVO2)checkIsExist(salevo.getTableName(), invvo, imglibvo, VATSaleInvoiceVO2.class);
				if(svo==null){
					salevo = createVATSaleInvoiceVO2(invvo, grpvo,imglibvo);
					singleObjectBO.insertVO( invvo.getPk_corp(), salevo);
				}else if(StringUtil.isEmpty(svo.getPk_image_library())){// !(((VATSaleInvoiceVO2)svo).getSourcetype()==IBillManageConstants.OCR) 
					tradesource = svo.getPrimaryKey();
					pk_tzpz_h = ((VATSaleInvoiceVO2)svo).getPk_tzpz_h();
					salevo.setPk_vatsaleinvoice(svo.getPrimaryKey());
					salevo.setImgpath(getImgpath(imglibvo));
					salevo.setPk_image_group(grpvo.getPk_image_group());
					salevo.setPk_image_library(imglibvo.getPk_image_library());
					salevo.setSourcebillid(imglibvo.getPk_image_library());
					salevo.setVdef13(invvo.getPk_invoice());
//					salevo.setVersion(IBillManageConstants.pjversion);	//销项发票绑图片，即使绑上图，销项还是不会变换版本，因为没有更换老业务类型。
					singleObjectBO.update(salevo, new String[]{"vdef13","imgpath","pk_image_group","pk_image_library","sourcebillid"});//,"version"});
					//设置分类
					if(!StringUtil.isEmpty(svo.getPk_model_h())){
						invvo.setPk_billcategory(svo.getPk_model_h());
					}
				}
			}
			if(!StringUtil.isEmpty(pk_tzpz_h)){//凭证绑定图片
				TzpzHVO tzpzvo = (TzpzHVO)singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk_tzpz_h);
				if(StringUtil.isEmpty(tzpzvo.getPk_image_group())){
					if(tzpzvo!=null){
						tzpzvo.setPk_image_group(grpvo.getPk_image_group());
						tzpzvo.setPk_image_library(imglibvo.getPk_image_library());
						singleObjectBO.update(tzpzvo, new String[]{"pk_image_group","pk_image_library"});
					}
					//有凭证设置图片状态
					if(tzpzvo.getVbillstatus()!= IVoucherConstants.TEMPORARY){
						grpvo.setIstate(PhotoState.state100);
					}else{
						grpvo.setIstate(PhotoState.state101);
					}
					singleObjectBO.update(grpvo, new String[]{"istate"});//更新图片状态
					//更新票据分类
				}
				
				
				
			}
		}else if(!StringUtil.isEmpty(invvo.getIstate()) && (invvo.getIstate().equals("b银行票据"))){
			log.info(moduleName, "WebID:" + invvo.getWebid() + "开始生成银行对账单数据",log);
			saveBankInfo(corpvo, invvo, grpvo, imglibvo);
		}
		if(!StringUtil.isEmpty(invvo.getPk_billcategory())){
			BillCategoryVO categoryvo = (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, invvo.getPk_billcategory());
			if (categoryvo != null)
			{
				invvo.setPeriod(categoryvo.getPeriod());
				singleObjectBO.update(invvo,new String[]{"pk_billcategory"});
				OcrInvoiceDetailVO childvos []= (OcrInvoiceDetailVO[])invvo.getChildren();
				for (OcrInvoiceDetailVO detailvo : childvos)
				{
					if(StringUtil.isEmpty(detailvo.getPk_billcategory())){
						detailvo.setPk_billcategory(invvo.getPk_billcategory());
					}
				}
				singleObjectBO.updateAry(childvos,new String[]{"pk_billcategory"});
			}
			
		}
		
		if(!StringUtil.isEmpty(tradesource)){
			IntradeHVO tradevos[] = (IntradeHVO[])singleObjectBO.queryByCondition(IntradeHVO.class, "nvl(dr,0)=0 and sourcebillid='"+tradesource+"'", new SQLParameter());
			if(tradevos!=null&&tradevos.length>0){
				for (IntradeHVO intradeHVO : tradevos) {
					intradeHVO.setPk_image_group(grpvo.getPk_image_group());
					intradeHVO.setPk_image_library(imglibvo.getPk_image_library());
				}
				singleObjectBO.updateAry(tradevos, new String[]{"pk_image_group","pk_image_library"});
			}
		}
		log.info(moduleName, "WebID:" + invvo.getWebid() + "结束生成业务数据",log);
	}


	// ---银行对账单
	private void saveBankInfo(CorpVO corpvo, OcrInvoiceVO invvo, ImageGroupVO grpvo, ImageLibraryVO libvo) {
	
		// 对应的银行对账单是否生成凭证 中间表 怎么关联银行对账单 根据字段关联对账单 对账单 跟 回单关联关系
		int imageType = matchYHImageType(invvo, corpvo);
		if (imageType == VATInvoiceTypeConst.VAT_RECE_INVOICE || imageType == VATInvoiceTypeConst.VAT_PAY_INVOICE) {
			BankBillToStatementVO bankvo = createBankStatementVO2(corpvo, invvo, grpvo, libvo);
			BankStatementVO2 banksvo = queryByBankBill(corpvo.getPk_corp(), bankvo);


			saveBankBill(corpvo.getPk_corp(), bankvo, banksvo, invvo);
		} else {
			// hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
		}
		// }
	}

	private int matchYHImageType(OcrInvoiceVO vo, CorpVO corpvo) {

		String saleName =vo.getVsalename();// filterName(vo.getVsalename());
		String purName = vo.getVpurchname();//filterName(vo.getVpurchname());
		String corpName = corpvo.getUnitname();//filterName(corpvo.getUnitname());

		if (OcrUtil.isSameCompany(corpName, purName)) {//!StringUtil.isEmpty(purName) && (purName.startsWith(corpName) || corpName.startsWith(purName))
			return VATInvoiceTypeConst.VAT_PAY_INVOICE;
		} else if (OcrUtil.isSameCompany(corpName, saleName)) {//!StringUtil.isEmpty(saleName) && ((saleName.startsWith(corpName) || corpName.startsWith(saleName)))
			return VATInvoiceTypeConst.VAT_RECE_INVOICE;
		} else {
			return VATInvoiceTypeConst.UNDETERMINED_INVOICE;
		}
	}

	private BankBillToStatementVO createBankStatementVO2(CorpVO corpvo, OcrInvoiceVO invvo, ImageGroupVO grpvo,
			ImageLibraryVO libvo) throws BusinessException {
		BankBillToStatementVO headvo = new BankBillToStatementVO();

		if (!StringUtil.isEmpty(invvo.getDinvoicedate())) {
			DZFDate date;
			if (invvo.getDinvoicedate().contains("年")) {
				String das = invvo.getDinvoicedate().replace("年", "").replace("月", "").replace("日", "");
				date = new DZFDate(das);
			} else {
				date = new DZFDate(invvo.getDinvoicedate());
			}
			headvo.setTradingdate(date);// 交易日期
		} else {
			// DZFDate ddate = getNewDate(invvo.getDinvoicedate(),
			// hvo.getDoperatedate(),hvo);
			headvo.setTradingdate(grpvo.getDoperatedate());// 交易日期
		}
		headvo.setPeriod(invvo.getPeriod());//入账期间
		String zy = getBankKeyWord(invvo);

		if (!StringUtil.isEmpty(zy)) {
			headvo.setZy(zy);// 摘要
		}

		headvo.setPk_image_ocrlibrary(invvo.getOcr_id());// 图片主键
		headvo.setPk_image_group(grpvo.getPk_image_group());
		int imageType = matchYHImageType(invvo, corpvo);
		if (imageType == VATInvoiceTypeConst.VAT_RECE_INVOICE) {// 收款
			headvo.setOthaccountcode(invvo.getVpurchtaxno());// 对方账户为空
			headvo.setOthaccountname(invvo.getVpurchname());// 对方账户名称
			headvo.setSyje(getDZFDouble(invvo.getNtotaltax()));// 收款方金额
			headvo.setMyaccountcode(invvo.getVsaletaxno());// 本方账户
			headvo.setMyaccountname(invvo.getVsalename());// 本方账户名称
			headvo.setAccountname(invvo.getVsalebankname());
		} else if (imageType == VATInvoiceTypeConst.VAT_PAY_INVOICE) {// 付款
			headvo.setZcje(getDZFDouble(invvo.getNtotaltax()));// 付款方金额
			headvo.setOthaccountcode(invvo.getVsaletaxno());// 对方账户为空
			headvo.setOthaccountname(invvo.getVsalename());// 对方账户名称
			headvo.setMyaccountcode(invvo.getVpurchtaxno());// 本方账户
			headvo.setMyaccountname(invvo.getVpurchname());// 本方账户名称
			headvo.setAccountname(invvo.getVpurbankname());
		}

		// ImageLibraryVO libvo = getImageLibraryVO(hvo.getPk_corp(), invvo);
		// if (libvo != null) {
		headvo.setSourcebillid(libvo.getPk_image_library());
		headvo.setPk_image_group(libvo.getPk_image_group());
		headvo.setPk_image_library(libvo.getPk_image_library());
		String imagepath = getImgpath(libvo);
		if (!StringUtil.isEmpty(imagepath)) {
			headvo.setImgpath(imagepath);
		}
		// }
		// setDefaultValue(headvo, hvo, grpvo);
		headvo.setPk_corp(grpvo.getPk_corp());
		headvo.setDoperatedate(new DZFDate());
		return headvo;
	}

	private String getBankKeyWord(OcrInvoiceVO vo) {
		// 获取摘要信息
		if (StringUtil.isEmpty(vo.getVsalephoneaddr()))
			return null;

		String vsalephoneaddr = vo.getVsalephoneaddr();
		vsalephoneaddr = vsalephoneaddr.replaceAll("”", "'");

		JSONObject rowobject = JSON.parseObject(vsalephoneaddr);
		if (rowobject == null || rowobject.size() == 0)
			return null;

		String[] keys = { "摘要", "用途", "备注", "附言" };
		for (String key : keys) {
			String zy = (String) rowobject.get(key);
			if (!StringUtil.isEmpty(zy)) {
				return zy;
			}
		}

		Set<String> set = rowobject.keySet();
		for (String key : set) {
			String zy = (String) rowobject.get(key);
			if (!StringUtil.isEmpty(zy)) {
				return zy;
			}
		}
		return null;
	}

	// -----end 银行对账单
	public SuperVO checkIsExist(String table, OcrInvoiceVO invvo, ImageLibraryVO imglibvo, Class className) {
		StringBuffer sqlbuff = new StringBuffer();
		// sqlbuff.append(" select *from ").append(table);
		sqlbuff.append("  nvl(dr,0)=0 and pk_corp = ? and fp_hm=? and fp_dm=? ");
		SQLParameter param = new SQLParameter();
		param.addParam(invvo.getPk_corp());
		param.addParam(invvo.getVinvoiceno());
		param.addParam(invvo.getVinvoicecode());
		SuperVO svo[] = singleObjectBO.queryByCondition(className, sqlbuff.toString(), param);
		// getImgpath
		if (svo != null && svo.length > 0) {
			return svo[0];
		}
		return null;
	}

	protected int matchImageType(OcrInvoiceVO vo, CorpVO corpvo, ImageGroupVO grpvo) {

		String saleName = vo.getVsalename();//filterName(vo.getVsalename());
		String purName = vo.getVpurchname();//filterName(vo.getVpurchname());
		String corpName = corpvo.getUnitname();//filterName(corpvo.getUnitname());

		if (OcrUtil.isSameCompany(corpName, purName)) {//!StringUtil.isEmpty(purName) && (purName.startsWith(corpName) || corpName.startsWith(purName))
//			if (!StringUtil.isEmpty(saleName) && isDk(saleName, vo.getVmemo())) {
//				// 截取销方名称
//				// setSaleInfo(vo);
//			}
			return VATInvoiceTypeConst.VAT_INCOM_INVOICE;
		} else if (OcrUtil.isSameCompany(corpName, saleName)){//(!StringUtil.isEmpty(saleName)&& ((saleName.startsWith(corpName) || corpName.startsWith(saleName)) )) {//|| isDk(saleName, vo.getVmemo())
//			if (isDk(saleName, vo.getVmemo())) {
//				// 截取销方名称
//				// setSaleInfo(vo);
//			}
//			saleName = filterName(vo.getVsalename());
//			if (!StringUtil.isEmpty(saleName) && (saleName.startsWith(corpName) || corpName.startsWith(saleName))) {
//				
//			} else {
//				return VATInvoiceTypeConst.UNDETERMINED_INVOICE;
//			}
			return VATInvoiceTypeConst.VAT_SALE_INVOICE;
		} else {
			return VATInvoiceTypeConst.UNDETERMINED_INVOICE;
		}
	}



	/// -----银行对账单处理

	// ------进项发票处理---
	private VATInComInvoiceVO2 createVATInComInvoiceVO2(OcrInvoiceVO invvo, ImageGroupVO grpvo, ImageLibraryVO imglibvo,VATInComInvoiceVO2 incomvos[])
			throws BusinessException {
		List<VATInComInvoiceBVO> blist = new ArrayList<VATInComInvoiceBVO>();
		VATInComInvoiceVO2 headvo = new VATInComInvoiceVO2();

		String[] hcodes = InvoiceColumns.INVOICE_HCODES;
		String[] hnames = InvoiceColumns.INCOM_HCODES;

		int hlen = hcodes.length;

		for (int m = 0; m < hlen; m++) {
			headvo.setAttributeValue(hnames[m], invvo.getAttributeValue(hcodes[m]));
		}

		setJXDefaultValue(invvo, headvo, grpvo, imglibvo);
		OcrInvoiceDetailVO[] detailvos = (OcrInvoiceDetailVO[]) invvo.getChildren();

		String[] bcodes = InvoiceColumns.INVOICE_BCODES;
		String[] bnames = InvoiceColumns.INCOM_BCODES;
		int blen = bcodes.length;
		int i = 0;
		for (OcrInvoiceDetailVO detail : detailvos) {
			VATInComInvoiceBVO bvo = new VATInComInvoiceBVO();
			for (int m = 0; m < blen; m++) {
				if ("itemtaxrate".equals(bcodes[m]) || "itemmny".equals(bcodes[m]) || "itemtaxmny".equals(bcodes[m])
						|| "itemamount".equals(bcodes[m]) || "itemprice".equals(bcodes[m])) {
					bvo.setAttributeValue(bnames[m], getDZFDouble((String) detail.getAttributeValue(bcodes[m])));
				} else {
					bvo.setAttributeValue(bnames[m], StringUtil.isEmpty((String)detail.getAttributeValue(bcodes[m]))?"":((String)detail.getAttributeValue(bcodes[m])).trim());
				}
			}
			bvo.setRowno(i);
			bvo.setPk_corp(invvo.getPk_corp());
			bvo.setDr(0);
			i++;
			blist.add(bvo);
		}

		if (blist != null && blist.size() > 0) {
			headvo.setChildren(blist.toArray(new VATInComInvoiceBVO[blist.size()]));
		}
//		if(incomvos!=null && incomvos.length>0){
//			headvo.setRzjg(incomvos[0].getRzjg());
//			headvo.setRzrj(incomvos[0].getRzrj());
//		}
		return headvo;
	}

	private void setJXDefaultValue(OcrInvoiceVO invvo, VATInComInvoiceVO2 vo, ImageGroupVO grpvo, ImageLibraryVO libvo) {
		vo.setPk_corp(invvo.getPk_corp());
		// vo.setCoperatorid(hvo.getCoperatorid());
		vo.setDoperatedate(new DZFDate());
		vo.setVdef13(invvo.getPk_invoice());
		String date = getStrFormateDate(invvo.getDinvoicedate());
		vo.setKprj(StringUtil.isEmpty(date)?null:new DZFDate(date));
		// 设置税率
		DZFDouble sl = vo.getSpsl();
		if (sl == null || sl.doubleValue() == DZFDouble.ZERO_DBL.doubleValue()) {
			vo.setSpsl(SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100)));
			vo.setSpsl(vo.getSpsl().setScale(0, DZFDouble.ROUND_HALF_UP));
		}

		if (grpvo.getCvoucherdate() != null) {
			vo.setInperiod(DateUtils.getPeriod(grpvo.getCvoucherdate()));
			vo.setUploadperiod(DateUtils.getPeriod(grpvo.getCvoucherdate()));
		}

		// 设置期间
//		String period = null;
//		if (vo.getRzjg() != null && vo.getRzjg() == 1 && vo.getRzrj() != null) {
//			period = DateUtils.getPeriod(vo.getRzrj());
//		} else if (vo.getKprj() != null) {
//			period = DateUtils.getPeriod(vo.getKprj());
//		}
//		vo.setPeriod(period);
		vo.setPeriod(vo.getInperiod());
		CorpVO corpvo = corpService.queryByPk(invvo.getPk_corp());
		int fply = getFp_style(invvo.getPk_corp(), invvo);
		List<ParaSetVO> paravo = queryParaSet(invvo.getPk_corp());
		
		if (fply == VATInvoiceTypeConst.VAT_SPECIA_INVOICE) {
			// 公司性质为“一般纳税人”，上传票据生成进项清单，票据为增值税专用发票，认证状态默认勾选，认证日期取上传期间
			if ("一般纳税人".equals(corpvo.getChargedeptname())) {
				if(paravo.get(0).getInvidentify().equals(DZFBoolean.TRUE)){
					vo.setRzrj(grpvo.getCvoucherdate());
					vo.setRzjg(1);// 勾选
				}
				
			}
			vo.setIszhuan(DZFBoolean.TRUE);
		} else {
			vo.setIszhuan(DZFBoolean.FALSE);
		}
		
		
		if("机动车销售统一发票".equals(invvo.getInvoicetype())||"通行费增值税电子普通发票".equals(invvo.getInvoicetype())){
			vo.setIszhuan(DZFBoolean.TRUE);
			
		}
		vo.setVersion(IBillManageConstants.pjversion);
		vo.setSourcetype(IBillManageConstants.OCR);
		// 设置来源
		vo.setSourcebilltype(ICaiFangTongConstant.LYDJLX_OCR);
		if (grpvo.getPjlxstatus() != null && grpvo.getPjlxstatus().intValue() > 20) {
			vo.setIuploadtype(grpvo.getPjlxstatus());
			vo.setIoperatetype(grpvo.getPjlxstatus());
		}

		// ImageLibraryVO libvo = getImageLibraryVO(invvo.getPk_corp(), invvo);
		if (libvo != null) {
			vo.setSourcebillid(libvo.getPk_image_library());
			vo.setPk_image_group(libvo.getPk_image_group());
			vo.setPk_image_library(libvo.getPk_image_library());
			String imagepath = getImgpath(libvo);
			if (!StringUtil.isEmpty(imagepath)) {
				vo.setImgpath(imagepath);
			}
		}
		// 非总账核算存货
		// DZFBoolean icinv = new
		// DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));//
		// 启用总账库存
		// if (icinv == null || !icinv.booleanValue()) {
		// // 非存货上传
		// if (grpvo.getPjlxstatus() != null && grpvo.getPjlxstatus().intValue()
		// != PjTypeEnum.INV.getValue()) {
		// DcModelHVO hmodelvo = getMatchModel(invvo, corpvo,
		// VATInvoiceTypeConst.VAT_INCOM_INVOICE, grpvo);
		// if (hmodelvo != null &&
		// !StringUtil.isEmpty(hmodelvo.getPk_model_h())) {
		// vo.setPk_model_h(hmodelvo.getPk_model_h());
		// vo.setBusitypetempname(hmodelvo.getBusitypetempname());
		// }
		// }
		// }
	}

	// public DcModelHVO getMatchModel(OcrInvoiceVO invvo, CorpVO corpvo, int
	// imagetype, ImageGroupVO grpvo) {
	// if (invvo == null)
	// return null;
	// List<DcModelBVO> lista = null;
	// String billtype = invvo.getInvoicetype();
	// String type = null;
	// //int imagetype = hvo1.getIfptype();
	// if (imagetype == VATInvoiceTypeConst.VAT_INCOM_INVOICE) {
	// type = "采购";
	// if (grpvo != null && grpvo.getPjlxstatus() != null
	// && grpvo.getPjlxstatus().intValue() == PjTypeEnum.OTHER.getValue()) {
	// type = "其他采购";
	// }
	// } else if (VATInvoiceTypeConst.VAT_SALE_INVOICE == imagetype) {
	// type = "销售";
	// if (grpvo != null && grpvo.getPjlxstatus() != null
	// && grpvo.getPjlxstatus().intValue() == PjTypeEnum.OTHER.getValue()) {
	// type = "其他销售";
	// }
	// }
	// String pk_corp = corpvo.getPk_corp();
	// List<DcModelHVO> list = queryDcModelHVOs(pk_corp, billtype, type);
	// DcModelHVO hvo = filterModelDataByKeyWords(list, corpvo, invvo);
	// if (hvo != null) {
	// lista = dcpzjmbserv.queryByPId(hvo.getPk_model_h(), pk_corp);
	// if (lista != null && lista.size() > 0) {
	// hvo.setChildren(lista.toArray(new DcModelBVO[0]));
	// }
	// }
	// return hvo;
	// }

	// 通过关键字过滤合适的模板数据
	// private DcModelHVO filterModelDataByKeyWords(List<DcModelHVO> list,
	// CorpVO vo, OcrInvoiceVO invvo) {
	//
	// if (list == null || list.size() == 0)
	// return null;
	// if (list.size() == 1)
	// return list.get(0);
	//
	// String vmome = invvo.getVmemo();
	// String pipeistyle = "";
	// List<ModelSelectVO> zmselectlist = new ArrayList<ModelSelectVO>();
	// List<DcModelHVO> filterlist = new ArrayList<>();
	//
	// for (DcModelHVO dc : list) {
	// String keywords = dc.getKeywords();// 这个字段不可能为空，但还是判断一下
	// if (StringUtil.isEmpty(keywords))
	// continue;
	//
	// keywords = keywords.replace("*", "&");
	// String[] kds = keywords.split("&");
	//
	// // 收购 代开
	// if (!StringUtil.isEmpty(vmome)
	// && (vmome.contains("收购(左上角标志)") || (vmome.contains("代开企业税号") &&
	// vmome.contains("代开企业名称")))) {
	// if (kds != null && kds.length > 1) {
	// if (kds[1].equals("收购") || kds[1].equals("代开")) {
	// filterlist.add(dc);
	// continue;
	// }
	// }
	// }
	// }
	//
	// // 非 收购 代开
	// if (filterlist == null || filterlist.size() == 0) {
	// // 3关键字 4默认 采购 销售
	// for (DcModelHVO dc : list) {
	// String keywords = dc.getKeywords();// 这个字段不可能为空，但还是判断一下
	// if (StringUtil.isEmpty(keywords))
	// continue;
	// keywords = keywords.replace("*", "&");
	// String[] kds = keywords.split("&");
	// if (kds != null && kds.length == 1) {
	// pipeistyle = ModelSelectVO.pipeistyle_5;
	// zmselectlist.add(buildSelectVO(dc, pipeistyle));
	// continue;
	// }
	// // 按以下顺序识别，优先级
	// boolean isexist = isExistKeyWords(kds, invvo.getVfirsrinvname());
	// pipeistyle = ModelSelectVO.pipeistyle_3;
	//
	// if (isexist) {
	// zmselectlist.add(buildSelectVO(dc, pipeistyle));
	// }
	// }
	// } else {
	//
	// // 0收购 代开 关键字 1收购 代开 4默认 采购 销售
	// for (DcModelHVO dc : list) {
	// String keywords = dc.getKeywords();
	// if (StringUtil.isEmpty(keywords))
	// continue;
	// keywords = keywords.replace("*", "&");
	// String[] kds = keywords.split("&");
	// if (kds != null && kds.length == 1) {
	// // 采购销售
	// pipeistyle = ModelSelectVO.pipeistyle_5;
	// zmselectlist.add(buildSelectVO(dc, pipeistyle));
	// continue;
	// }
	// }
	//
	// for (DcModelHVO dc : filterlist) {
	// String keywords = dc.getKeywords();
	// if (StringUtil.isEmpty(keywords))
	// continue;
	// keywords = keywords.replace("*", "&");
	// String[] kds = keywords.split("&");
	// if (kds != null && kds.length == 2) {
	// if (kds[1].equals("收购") || kds[1].equals("代开")) {
	// pipeistyle = ModelSelectVO.pipeistyle_1;
	// zmselectlist.add(buildSelectVO(dc, pipeistyle));
	// }
	// continue;
	// }
	// String[] kds1 = new String[kds.length - 2];
	// System.arraycopy(kds, 2, kds1, 0, kds.length - 2);
	// // 按以下顺序识别，优先级
	// boolean isexist = isExistKeyWords(kds1, invvo.getVfirsrinvname());
	// pipeistyle = ModelSelectVO.pipeistyle_0;
	// if (isexist) {
	// zmselectlist.add(buildSelectVO(dc, pipeistyle));
	// }
	// }
	// }
	//
	// DcModelHVO defaultmodel = null;
	// // 选择其中级别最高的
	// if (zmselectlist != null && zmselectlist.size() > 0) {
	// Collections.sort(zmselectlist);
	// defaultmodel = zmselectlist.get(zmselectlist.size() -
	// 1).getDefaultmodel();
	// }
	// return defaultmodel;
	// }

	// private ModelSelectVO buildSelectVO(DcModelHVO defaultmodel, String
	// pipeistyle) {
	// ModelSelectVO vo = new ModelSelectVO();
	// vo.setDefaultmodel(defaultmodel);
	// vo.setPipeistyle(pipeistyle);
	// return vo;
	// }
	//
	private boolean isExistKeyWords(String[] kds, String value) {
		boolean flag = false;
		if (kds == null || kds.length == 0)
			return flag;
		if (StringUtil.isEmpty(value))
			return flag;
		for (int i = 0; i < kds.length; i++) {
			if (value.contains(kds[i])) {
				flag = true;
				break;
			}
		}
		return flag;
	}

	// // 查询符合条件的业务类型数据
	// private List<DcModelHVO> queryDcModelHVOs(String pk_corp, String
	// billtype, String type) {
	// if (StringUtil.isEmpty(billtype) || StringUtil.isEmpty(type))
	// return null;
	// List<DcModelHVO> list = dcpzjmbserv.queryAccordBankModel(pk_corp, new
	// String[] { billtype, type });
	// return list;
	// }

	/// -------------销项发票处理-----------
	private VATSaleInvoiceVO2 createVATSaleInvoiceVO2(OcrInvoiceVO invvo, ImageGroupVO grpvo, ImageLibraryVO imglibvo)
			throws BusinessException {
		String pk_corp = invvo.getPk_corp();
		List<VATSaleInvoiceBVO2> blist = new ArrayList<VATSaleInvoiceBVO2>();
		VATSaleInvoiceVO2 headvo = new VATSaleInvoiceVO2();

		String[] hcodes = InvoiceColumns.INVOICE_HCODES;
		String[] hnames = InvoiceColumns.INCOM_HCODES1;

		int hlen = hcodes.length;

		for (int m = 0; m < hlen; m++) {
			headvo.setAttributeValue(hnames[m], invvo.getAttributeValue(hcodes[m]));
		}
		setXXDefaultValue(invvo, headvo, grpvo, imglibvo);
		OcrInvoiceDetailVO[] detailvos = (OcrInvoiceDetailVO[]) invvo.getChildren();

		String[] bcodes = InvoiceColumns.INVOICE_BCODES;
		String[] bnames = InvoiceColumns.INCOM_BCODES;
		int blen = bcodes.length;
		int i = 0;
		for (OcrInvoiceDetailVO detail : detailvos) {
			VATSaleInvoiceBVO2 bvo = new VATSaleInvoiceBVO2();
			for (int m = 0; m < blen; m++) {
				if ("itemtaxrate".equals(bcodes[m]) || "itemmny".equals(bcodes[m]) || "itemtaxmny".equals(bcodes[m])
						|| "itemamount".equals(bcodes[m]) || "itemprice".equals(bcodes[m])) {
					bvo.setAttributeValue(bnames[m], getDZFDouble((String) detail.getAttributeValue(bcodes[m])));
				} else {
				//	bvo.setAttributeValue(bnames[m], replaceBlank((String) detail.getAttributeValue(bcodes[m])));
					bvo.setAttributeValue(bnames[m], StringUtil.isEmpty((String)detail.getAttributeValue(bcodes[m]))?"":((String)detail.getAttributeValue(bcodes[m])).trim());
				}
			}
			bvo.setRowno(i);
			bvo.setPk_corp(pk_corp);
			bvo.setDr(0);
			i++;
			blist.add(bvo);
		}

		if (blist != null && blist.size() > 0) {
			headvo.setChildren(blist.toArray(new VATSaleInvoiceBVO2[blist.size()]));
		}
		return headvo;
	}

	protected DZFDouble getDZFDouble(String smny) {
		DZFDouble mny = DZFDouble.ZERO_DBL;
		try {

			if (StringUtil.isEmpty(smny)) {
				mny = DZFDouble.ZERO_DBL;
			} else {
				smny = smny.replaceAll("[￥%$*免税]", "");
				smny = replaceBlank(smny);
			}
			mny = new DZFDouble(smny);
		} catch (Exception e) {
			if (e instanceof NumberFormatException)
				throw new BusinessException("数字识别出错");
			else
				throw new BusinessException(e.getMessage());
		}
		return mny;
	}

	protected String replaceBlank(String str) {
		String dest = "";
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	/**
	 * 设置默认值
	 * 
	 * @param invvo
	 * @param grpvo
	 * @param vo
	 */
	private void setXXDefaultValue(OcrInvoiceVO invvo, VATSaleInvoiceVO2 vo, ImageGroupVO grpvo, ImageLibraryVO libvo) {
		vo.setPk_corp(invvo.getPk_corp());
		// vo.setCoperatorid(hvo.getCoperatorid());
		vo.setDoperatedate(new DZFDate());
		vo.setVdef13(invvo.getPk_invoice());
		//vo.setKprj(new DZFDate(getStrFormateDate(invvo.getDinvoicedate())));
		String date = getStrFormateDate(invvo.getDinvoicedate());
		vo.setKprj(StringUtil.isEmpty(date)?null:new DZFDate(date));
		// 设置期间
		if (vo.getKprj() != null) {
			//vo.setPeriod(DateUtils.getPeriod(vo.getKprj()));
			vo.setInperiod(vo.getPeriod());// 不走凭证所在期间
											// DateUtils.getPeriod(grpvo.getCvoucherdate())
		}
		vo.setVersion(IBillManageConstants.pjversion);
		if (grpvo.getCvoucherdate() != null) {
			vo.setInperiod(DateUtils.getPeriod(grpvo.getCvoucherdate()));
			vo.setUploadperiod(DateUtils.getPeriod(grpvo.getCvoucherdate()));
		}
		vo.setPeriod(vo.getInperiod());
		DZFDouble sl = vo.getSpsl();
		if (sl == null || sl.doubleValue() == DZFDouble.ZERO_DBL.doubleValue()) {
			vo.setSpsl(SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100)));
			vo.setSpsl(vo.getSpsl().setScale(0, DZFDouble.ROUND_HALF_UP));
		}
		int fply = getFp_style(invvo.getPk_corp(), invvo);
		// 设置来源
		if (fply == VATInvoiceTypeConst.VAT_SPECIA_INVOICE) {
			vo.setIszhuan(DZFBoolean.TRUE);
		} else {
			vo.setIszhuan(DZFBoolean.FALSE);
		}
		if("机动车销售统一发票".equals(invvo.getInvoicetype())){
			vo.setIszhuan(DZFBoolean.TRUE);
		}
		vo.setSourcetype(IBillManageConstants.OCR);
		// 设置来源
		vo.setSourcebilltype(ICaiFangTongConstant.LYDJLX_OCR);
		if (grpvo.getPjlxstatus() != null && grpvo.getPjlxstatus().intValue() == PjTypeEnum.INV.getValue()) {
			vo.setIuploadtype(grpvo.getPjlxstatus());
			vo.setIoperatetype(grpvo.getPjlxstatus());
		}
		// ImageLibraryVO libvo = getImageLibraryVO(invvo.getPk_corp(), invvo);
		if (libvo != null) {
			vo.setSourcebillid(libvo.getPk_image_library());
			vo.setPk_image_group(libvo.getPk_image_group());
			vo.setPk_image_library(libvo.getPk_image_library());
			String imagepath = getImgpath(libvo);
			if (!StringUtil.isEmpty(imagepath)) {
				vo.setImgpath(imagepath);
			}
		}

	}

	protected String getStrFormateDate(String str) {

		if (StringUtil.isEmpty(str)) {

			return null;

		}
		StringBuffer strb = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			try {
				char ch = str.charAt(i);
				Integer.parseInt(String.valueOf(ch));
				strb.append(ch);
			} catch (NumberFormatException e) {
				// log.error("日期转换错误");
			} catch (Exception e) {
				log.error("日期转换未知错误", e);
			}
		}
		if (strb.length() > 0) {
			return strb.toString();
		} else {
			return null;
		}
	}

	// StringBuffer strb = new StringBuffer();
	// protected ImageLibraryVO getImageLibraryVO(String pk_corp, OcrInvoiceVO
	// invvo) {
	// strb.append(" select iy.* from ynt_image_library iy ");
	// strb.append(" join ynt_image_ocrlibrary oy on iy.pk_image_library =
	// oy.crelationid ");
	// strb.append("where nvl(oy.dr,0)=0 and nvl(iy.dr,0)=0 ");
	// strb.append(" and oy.pk_image_ocrlibrary = ? and iy.pk_corp = ? ");
	//
	// SQLParameter sp = new SQLParameter();
	// sp.addParam(invvo.getOcr_id());
	// sp.addParam(pk_corp);
	//
	// List<ImageLibraryVO> list = (List<ImageLibraryVO>)
	// singleObjectBO.executeQuery(strb.toString(), sp,
	// new BeanListProcessor(ImageLibraryVO.class));
	// if (list != null && list.size() > 0) {
	// return list.get(0);
	// }
	// return null;
	//
	// }

	protected String getImgpath(ImageLibraryVO libvo) {
		String imagepath = null;
		if (libvo != null) {
			imagepath = "/gl/gl_imgview!search.action?id=" + libvo.getPk_image_library() + "&name=" + libvo.getImgname()
					+ "&pk_corp=" + libvo.getPk_corp() + "";

		}
		return imagepath;

	}

	protected Integer getFp_style(String pk_corp, OcrInvoiceVO vo) {
		// 发票类型 如果 无发票类型
		String invoicetype = vo.getInvoicetype();
		if (StringUtil.isEmpty(invoicetype)) {
			CorpVO corpvo = corpService.queryByPk(pk_corp);

			String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人"
					: corpvo.getChargedeptname();

			if ("小规模纳税人".equals(chargedeptname)) {
				// 默认普票
				return VATInvoiceTypeConst.VAT_ORDINARY_INVOICE;
			} else {
				// 默认专票
				return VATInvoiceTypeConst.VAT_SPECIA_INVOICE;
			}
		} else {
			if (invoicetype.indexOf(ImageTypeConst.ORDINARY_INVOICE_SHORTNAME) > -1
					|| invoicetype.indexOf(ImageTypeConst.ORDINARY_INVOICE_NAME) > -1
					|| invoicetype.equals(ImageTypeConst.ORDINARY_INVOICE_CODE)) {// 增值税普通发票：04
				return VATInvoiceTypeConst.VAT_ORDINARY_INVOICE;
			} else if (invoicetype.indexOf(ImageTypeConst.SPECIA_INVOICE_SHORTNAME) > -1
					|| invoicetype.indexOf(ImageTypeConst.SPECIA_INVOICE_NAME) > -1
					|| invoicetype.equals(ImageTypeConst.SPECIA_INVOICE_CODE)) {// 增值税专用发票：01
				return VATInvoiceTypeConst.VAT_SPECIA_INVOICE;
			} else {
				CorpVO corpvo = corpService.queryByPk(pk_corp);

				String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人"
						: corpvo.getChargedeptname();

				if ("小规模纳税人".equals(chargedeptname)) {
					// 默认普票
					return VATInvoiceTypeConst.VAT_ORDINARY_INVOICE;
				} else {
					// 默认专票
					return VATInvoiceTypeConst.VAT_SPECIA_INVOICE;
				}
			}
		}
	}

	protected String filterName(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		name = getHanzi(name);
		return name;
	}

	private String getHanzi(String string) {
		if (StringUtil.isEmpty(string))
			return null;
		String reg1 = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(reg1);
		Matcher m = p.matcher(string);

		String lasrChar = "";
		while (m.find()) {
			lasrChar = lasrChar + m.group();
		}
		return lasrChar;
	}







	
	public List<ParaSetVO> queryParaSet(String pk_corp) throws DZFWarpException {
		String sql="select * from ynt_para_set where nvl(dr,0)=0 and pk_corp=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<ParaSetVO> list=(List<ParaSetVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(ParaSetVO.class));
		if(list!=null&&list.size()>0){
			return list;
		}else{
			List<ParaSetVO> l=new ArrayList<ParaSetVO>();
			l.add(insertParaSetVO(pk_corp));
			return l;
		}
	}
	
	private ParaSetVO insertParaSetVO(String pk_corp)throws DZFWarpException {
		ParaSetVO paraVO=new ParaSetVO();
		paraVO.setPk_corp(pk_corp);
		paraVO.setDr(0);
		paraVO.setIncomeclass(ZncsConst.SRFL_0);
		paraVO.setBankbillbyacc(DZFBoolean.FALSE);
		paraVO.setInvidentify(DZFBoolean.TRUE);
		paraVO.setVoucherqfzpp(DZFBoolean.FALSE);
		paraVO.setVoucherdate(ZncsConst.PZRQ_1);
		paraVO.setErrorvoucher(DZFBoolean.FALSE);
		paraVO.setOrderdetail(1);
		paraVO.setIsmergedetail(DZFBoolean.TRUE);
		paraVO.setMergebillnum(2);
		paraVO.setIsmergeincome(DZFBoolean.FALSE);
		paraVO.setIsmergeic(DZFBoolean.FALSE);
		paraVO.setIsmergebank(DZFBoolean.FALSE);
		paraVO.setPurchclass(DZFBoolean.FALSE);
		paraVO.setCostclass(DZFBoolean.FALSE);
		paraVO.setBankinoutclass(DZFBoolean.FALSE);
		paraVO.setNcpsl("0%");
		paraVO=(ParaSetVO)singleObjectBO.insertVO(pk_corp, paraVO);
		return paraVO;
	}
	
	
	
	public boolean saveBankBill(String pk_corp, BankBillToStatementVO billvo, BankStatementVO2 bankvo,OcrInvoiceVO invvo)
			throws DZFWarpException {
		//先转换值 souceid 传回来的是 pk_image_ocrlibrary，
		//更新成 pk_image_library 主键
		//pk_bankaccount 重新赋值
		transBankBillValue(pk_corp, billvo, bankvo);
		
		if(bankvo != null){//回单绑定上了

			boolean isSave = !StringUtil.isEmpty(bankvo.getSourcebillid()) 
					&& bankvo.getSourcebillid().equals(billvo.getSourcebillid()) ? true : false;
			
			List<String> filedList = new ArrayList<String>();
			if(!StringUtil.isEmpty(billvo.getPk_tzpz_h())
					&& StringUtil.isEmpty(bankvo.getPk_tzpz_h())){//对账单没有生成凭证，将回单的凭证号回写
				bankvo.setPk_tzpz_h(billvo.getPk_tzpz_h());
				bankvo.setPzh(billvo.getPzh());
				
				filedList.add("pk_tzpz_h");
				filedList.add("pzh");
			}
			
			bankvo.setSourcebillid(billvo.getSourcebillid());
			bankvo.setImgpath(billvo.getImgpath());
			bankvo.setPk_image_group(billvo.getPk_image_group());
			bankvo.setPk_image_library(billvo.getPk_image_library());
			bankvo.setBillstatus(BankStatementVO2.STATUS_2);//绑定关系
			bankvo.setInperiod(billvo.getPeriod());//入账期间
			bankvo.setVdef13(invvo.getPk_invoice());
			bankvo.setPk_bankaccount(billvo.getPk_bankaccount());
//			bankvo.setVersion(IBillManageConstants.pjversion);		//绑图时，不能改变原单据的版本号。
			filedList.add("sourcebillid");
			filedList.add("imgpath");
			filedList.add("pk_image_group");
			filedList.add("billstatus");
			filedList.add("inperiod");//更新入账期间
			filedList.add("vdef13");
			filedList.add("pk_bankaccount");
//			filedList.add("version");		//版本号保存原状
			singleObjectBO.update(bankvo, 
					filedList.toArray(new String[0]));
			
			
			billvo.setPk_bankstatement(bankvo.getPrimaryKey());//对账单主键
			billvo.setMemo("回单匹配上对账单<br>");
			
			invvo.setPk_billcategory(bankvo.getPk_model_h());
			invvo.setPk_category_keyword(bankvo.getPk_category_keyword());
			invvo.setBilltitle(StringUtil.isEmpty(bankvo.getZy())?"往来款":bankvo.getZy());
			if(!isSave){
				singleObjectBO.insertVOArr(pk_corp, 
						new BankBillToStatementVO[]{ billvo });
			}
			//如果已制证，继续处理凭证和图片相关信息
			String pk_tzpz_h = bankvo.getPk_tzpz_h();
			if(!StringUtil.isEmpty(pk_tzpz_h)){//凭证绑定图片
				TzpzHVO tzpzvo = (TzpzHVO)singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk_tzpz_h);
				if(tzpzvo!=null){
					if(StringUtil.isEmpty(tzpzvo.getPk_image_group())){
	
						ImageGroupVO grpvo = (ImageGroupVO)singleObjectBO.queryByPrimaryKey(ImageGroupVO.class, bankvo.getPk_image_group());
						if (grpvo != null)
						{
							tzpzvo.setPk_image_group(grpvo.getPk_image_group());
							tzpzvo.setPk_image_library(bankvo.getPk_image_library());
							singleObjectBO.update(tzpzvo, new String[]{"pk_image_group","pk_image_library"});
		
							//有凭证设置图片状态
							if(tzpzvo.getVbillstatus()!=IVoucherConstants.TEMPORARY){
								grpvo.setIstate(PhotoState.state100);
							}else{
								grpvo.setIstate(PhotoState.state101);
							}
							singleObjectBO.update(grpvo, new String[]{"istate"});//更新图片状态
							//更新票据分类
						}
					}
				}
				
				
			}
		}else{//未绑定
			BankStatementVO2 vo = new BankStatementVO2();
			BeanUtils.copyNotNullProperties(billvo, vo);
			vo.setInperiod(billvo.getPeriod());// 更新入账期间
			vo.setBillstatus(BankStatementVO2.STATUS_1);//来源回单
			vo.setSourcetem(BankStatementVO2.SOURCE_100);
			vo.setSourcetype(BankStatementVO2.SOURCE_100);
			vo.setVdef13(invvo.getPk_invoice());
			vo.setPk_bankaccount(billvo.getPk_bankaccount());
			vo.setVersion(IBillManageConstants.pjversion);
			String pk = singleObjectBO.insertVOWithPK(vo);
			
			billvo.setPk_bankstatement(pk);//对账单主键
			billvo.setMemo("回单未匹配上,新增对账单<br>");
			
			singleObjectBO.insertVOArr(pk_corp, new BankBillToStatementVO[]{ billvo });
		}
		
		
		return true;
	}
	
	
	private void transBankBillValue(String pk_corp, BankBillToStatementVO billvo, BankStatementVO2 bankvo){
		if(bankvo == null){
			String myBankAcc = billvo.getMyaccountcode();//约定的  已方银行账号
			String pk_bankaccount = getYhzh(myBankAcc, pk_corp);//银行账户主键
			billvo.setPk_bankaccount(pk_bankaccount);
		}
		
		String pk_image_ocrlibrary = billvo.getPk_image_ocrlibrary();//
		if(!StringUtil.isEmpty(pk_image_ocrlibrary)){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk_image_ocrlibrary);
			OcrImageLibraryVO[] libvos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class, 
					" nvl(dr,0)=0 and pk_corp = ? and pk_image_ocrlibrary = ? ", sp);
			if(libvos != null && libvos.length > 0){
				billvo.setSourcebillid(libvos[0].getCrelationid());//更新成图片信息表主键
			}
		}
	}
	
	private String getYhzh(String myBankAcc, String pk_corp){
		
		if(!StringUtil.isEmpty(myBankAcc)){
			BankAccountVO[] vos = queryByCode(myBankAcc, pk_corp);
			
			if(vos != null && vos.length > 0)
				return vos[0].getPk_bankaccount();
		}
		
		return null;
	}
	public BankAccountVO[] queryByCode(String code, String pk_corp) throws DZFWarpException {
		
		SQLParameter sp = new SQLParameter();
		String condition =" nvl(dr,0) = 0 and pk_corp = ? and state != ? ";
		sp.addParam(pk_corp);
		sp.addParam(IBillManageConstants.TINGY_STATUS);
		if(!StringUtil.isEmpty(code)){
			condition = condition +"and bankaccount = ? ";
			sp.addParam(code);
		}
		
		BankAccountVO[] vos = (BankAccountVO[]) singleObjectBO.queryByCondition(BankAccountVO.class,
				condition, sp);
		
		return vos;
	}


	public BankStatementVO2 queryByBankBill(String pk_corp, BankBillToStatementVO vo) throws DZFWarpException {
		
		DZFDate tradingdate = vo.getTradingdate();
		if(tradingdate == null){
			return null;//交易日期为空，不在查询
		}
		
		BankStatementVO2 bankvo = queryBankVOByImgPk(vo, pk_corp);
		if(bankvo != null){
			return bankvo;
		}
		
		String myBankAcc = vo.getMyaccountcode();//约定的  已方银行账号
		String pk_bankaccount = getYhzh(myBankAcc, pk_corp);//银行账户主键
		vo.setPk_bankaccount(pk_bankaccount);
		List<BankStatementVO2> bankStateList = queryDataByTradingdate(vo, pk_bankaccount, pk_corp, true);
		
		if(bankStateList == null || bankStateList.size() == 0){
			return null;
		}
		
		Map<String, BankStatementVO2> bankStateMap = hashliseBankStatementMap(bankStateList);
		
		String key = buildKey(new String[]{
				vo.getTradingdate().toString(),
				pk_bankaccount,
				getDefaultMnyValue(vo.getSyje()),
				getDefaultMnyValue(vo.getZcje()),
				vo.getOthaccountname()
		});
		
		if(bankStateMap.containsKey(key)){
			return bankStateMap.get(key);
		}
		
		return null;
	}
	
	private Map<String, BankStatementVO2> hashliseBankStatementMap(List<BankStatementVO2> list){
		Map<String, BankStatementVO2> map = new HashMap<String, BankStatementVO2>();
		
		if(list == null || list.size() == 0){
			return map;
		}
		
		String key;
		for(BankStatementVO2 vo : list){

			key = buildKey(new String[]{
					vo.getTradingdate().toString(),
					vo.getPk_bankaccount(),
					getDefaultMnyValue(vo.getSyje()),
					getDefaultMnyValue(vo.getZcje()),
					vo.getOthaccountname()
			});
			
			if(!map.containsKey(key)){
				map.put(key, vo);
			}
		}
		
		return map;
	}

	private String buildKey(String[] keys){
		StringBuffer sf = new StringBuffer();
		int length = keys.length;
		for(int i = 0; i < length; i++){
			if(StringUtil.isEmpty(keys[i])){
				sf.append("");
			}else{
				sf.append(keys[i]);
			}
			
			if(i != length - 1){
				sf.append(",");
			}
		}
		
		return sf.toString();
	}
	
	
	private String getDefaultMnyValue(DZFDouble je){
		return je == null ? DZFDouble.ZERO_DBL.doubleValue() + "" : je.doubleValue() + "";
	}
	
	private BankStatementVO2 queryBankVOByImgPk(BankBillToStatementVO vo, String pk_corp){
		String pk = vo.getPk_image_ocrlibrary();
		if(StringUtil.isEmpty(pk)){
			return null;
		}
		if(!StringUtil.isEmpty(pk)){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk);
			OcrImageLibraryVO[] libvos = (OcrImageLibraryVO[]) singleObjectBO.queryByCondition(OcrImageLibraryVO.class, 
					" nvl(dr,0)=0 and pk_corp = ? and pk_image_ocrlibrary = ? ", sp);
			if(libvos != null && libvos.length > 0){
				pk = libvos[0].getCrelationid();//更新成图片信息表主键
			}
		}
		
		StringBuffer sf = new StringBuffer();
//		sf.append(" Select * From ynt_bankbilltostatement t Where ");
		sf.append(" nvl(dr,0) = 0 and pk_corp = ? and sourcebillid = ? ");
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk);
		
		BankStatementVO2[] vos = (BankStatementVO2[]) singleObjectBO.queryByCondition(BankStatementVO2.class, 
				sf.toString(), sp);
		
		return vos == null || vos.length == 0 
				? null :vos[0];
	}
	private List<BankStatementVO2> queryDataByTradingdate(BankBillToStatementVO vo,
			String pk_bankaccount,
			String pk_corp,
			boolean isY) throws DZFWarpException{
		
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" Select * From ynt_bankstatement y Where y.pk_corp = ? ");
		sf.append(" and nvl(y.dr,0) = 0 and y.sourcebillid is null ");
		sp.addParam(pk_corp);
		
		if(StringUtil.isEmpty(pk_bankaccount)){//pk_bankaccount
			sf.append(" and y.pk_bankaccount is null ");
		}else{
			sf.append(" and y.pk_bankaccount = ? ");
			sp.addParam(pk_bankaccount);
		}
		
		sf.append(" and y.tradingdate = ? ");
		sp.addParam(vo.getTradingdate());
		
		if(isY){
			sf.append(" and y.billstatus = ? ");
			sp.addParam(BankStatementVO2.STATUS_0);//只匹配银行对账单未绑定的数据
		}
		
		sf.append(" order by ts desc ");
		
		List<BankStatementVO2> list = (List<BankStatementVO2>) singleObjectBO.executeQuery(sf.toString(), 
				sp, new BeanListProcessor(BankStatementVO2.class));
		
		return list;
	}

}

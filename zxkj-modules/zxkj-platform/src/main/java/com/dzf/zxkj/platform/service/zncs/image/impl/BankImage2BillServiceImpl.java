package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.util.Set;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.BankBillToStatementVO;
import com.dzf.zxkj.platform.model.pjgl.BankStatementVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.zncs.IBankStatementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


@Service("image2bill_bank")
public class BankImage2BillServiceImpl extends DefaultImage2BillServiceImpl {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired
	private IBankStatementService gl_yhdzdserv;

	@Override
	public TzpzHVO saveBill(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo, boolean isRecog)
			throws DZFWarpException {
		if (isRecog) {
			saveBankInfoModel2(corpvo, hvo, invvo, grpvo);
		} else {
			saveBankInfo(corpvo, hvo, invvo, grpvo);
		}

		return hvo;
	}

	// 需要判断是否存在银行回单 如果存在 没有生成凭证 不生成凭证 已经生成凭证 关联凭证
	private void saveBankInfo(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo) {
		// 校验是否重复 如果重复更新重复标识 不生成凭证
		checkValidImgGrpData(grpvo);

		// 对应的银行对账单是否生成凭证 中间表 怎么关联银行对账单 根据字段关联对账单 对账单 跟 回单关联关系
		String pk_tzpz_h = getRepeatedPz(corpvo, invvo);
		if (!StringUtil.isEmpty(pk_tzpz_h)) {
			updateRepeatedInfo(pk_tzpz_h, invvo);
		} else {

			int imageType = matchImageType(invvo, corpvo);
			if (imageType == VATInvoiceTypeConst.VAT_RECE_INVOICE || imageType == VATInvoiceTypeConst.VAT_PAY_INVOICE) {
				BankBillToStatementVO bankvo = createBankStatementVO(corpvo, invvo, hvo, grpvo);
				BankStatementVO banksvo = gl_yhdzdserv.queryByBankBill(corpvo.getPk_corp(), bankvo);

				// 如果中间没有数据生成凭证则 ocr识别生成凭证
				if (banksvo == null) {
					hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
					bankvo.setPk_tzpz_h(hvo.getPk_tzpz_h());
					bankvo.setPzh(hvo.getPzh());
				} else {
					if (StringUtil.isEmpty(banksvo.getPk_tzpz_h())) {
						// 行对账单未生成凭证 ocr识别生成凭证
						hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
						bankvo.setPk_tzpz_h(hvo.getPk_tzpz_h());
						bankvo.setPzh(hvo.getPzh());
					} else {
						// 银行对账单生成凭证 更新图片
						updatePzImageGroup(grpvo.getPk_image_group(), banksvo.getPk_tzpz_h());
					}
				}
				gl_yhdzdserv.saveBankBill(corpvo.getPk_corp(), bankvo, banksvo);
			} else {
				hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
			}
		}
	}

	private void saveBankInfoModel2(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo) {

		int imageType = matchImageType(invvo, corpvo);
		if (imageType == VATInvoiceTypeConst.VAT_RECE_INVOICE || imageType == VATInvoiceTypeConst.VAT_PAY_INVOICE) {
			BankBillToStatementVO bankvo = createBankStatementVO(corpvo, invvo, hvo, grpvo);
			BankStatementVO banksvo = gl_yhdzdserv.queryByBankBill(corpvo.getPk_corp(), bankvo);

			// 如果中间没有数据生成凭证则 ocr识别生成凭证
			if (banksvo == null) {
				hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
				bankvo.setPk_tzpz_h(hvo.getPk_tzpz_h());
				bankvo.setPzh(hvo.getPzh());
			} else {
				if (StringUtil.isEmpty(banksvo.getPk_tzpz_h())) {
					// 行对账单未生成凭证 ocr识别生成凭证
					hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
					bankvo.setPk_tzpz_h(hvo.getPk_tzpz_h());
					bankvo.setPzh(hvo.getPzh());
				} else {
					// 银行对账单生成凭证 更新图片
					updatePzImageGroup(grpvo.getPk_image_group(), banksvo.getPk_tzpz_h());
				}
			}
			gl_yhdzdserv.saveBankBill(corpvo.getPk_corp(), bankvo, banksvo);
		} else {
			hvo = gl_tzpzserv.saveVoucher(corpvo, hvo);
		}
	}

	protected void updatePzImageGroup(String pk_image_group, String pk_tzpz_h) {
		String sql = " update  ynt_tzpz_h set  pk_image_group = ?,iautorecognize = ? where  pk_tzpz_h = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_image_group);
		sp.addParam(1);
		sp.addParam(pk_tzpz_h);
		int row = singleObjectBO.executeUpdate(sql, sp);

	}

	private String getRepeatedPz(CorpVO corpvo, OcrInvoiceVO invvo) {

		if (StringUtil.isEmpty(invvo.getVkeywordinfo()))
			return null;

		StringBuffer strb = new StringBuffer();
		strb.append(" select h.pk_tzpz_h from   ynt_interface_invoice  e ");
		strb.append(" join ynt_tzpz_h h on e.pk_image_group  = h.pk_image_group ");
		strb.append("  where nvl(h.dr,0) = 0  and e.vkeywordinfo = ? and e.pk_corp =? and iautorecognize = 1 ");
		SQLParameter parameter = new SQLParameter();
		parameter.addParam(invvo.getVkeywordinfo());
		parameter.addParam(corpvo.getPk_corp());
		Object o = singleObjectBO.executeQuery(strb.toString(), parameter, new ColumnProcessor());
		return o == null ? null : (String) o;
	}

	private BankBillToStatementVO createBankStatementVO(CorpVO corpvo, OcrInvoiceVO invvo, TzpzHVO hvo,
			ImageGroupVO grpvo) throws BusinessException {
		BankBillToStatementVO headvo = new BankBillToStatementVO();

		if (StringUtil.isEmpty(invvo.getDinvoicedate())) {
			headvo.setTradingdate(hvo.getDoperatedate());// 交易日期
		} else {
			// DZFDate ddate = getNewDate(invvo.getDinvoicedate(),
			// hvo.getDoperatedate(),hvo);
			headvo.setTradingdate(new DZFDate(invvo.getDinvoicedate()));// 交易日期
		}
		headvo.setPeriod(DateUtils.getPeriod(hvo.getDoperatedate()));//入账期间
		String zy = getBankKeyWord(invvo);

		if (!StringUtil.isEmpty(zy)) {
			headvo.setZy(zy);// 摘要
		}

		headvo.setPk_image_ocrlibrary(invvo.getOcr_id());// 图片主键
		headvo.setPk_image_group(grpvo.getPk_image_group());
		int imageType = matchImageType(invvo, corpvo);
		if (imageType == VATInvoiceTypeConst.VAT_RECE_INVOICE) {// 收款
			headvo.setOthaccountcode(invvo.getVpurchtaxno());// 对方账户为空
			headvo.setOthaccountname(invvo.getVpurchname());// 对方账户名称
			headvo.setSyje(getDZFDouble(invvo.getNtotaltax()));// 收款方金额
			headvo.setMyaccountcode(invvo.getVsaletaxno());// 本方账户
			headvo.setMyaccountname(invvo.getVsalename());// 本方账户名称
		} else if (imageType == VATInvoiceTypeConst.VAT_PAY_INVOICE) {// 付款
			headvo.setZcje(getDZFDouble(invvo.getNtotaltax()));// 付款方金额
			headvo.setOthaccountcode(invvo.getVsaletaxno());// 对方账户为空
			headvo.setOthaccountname(invvo.getVsalename());// 对方账户名称
			headvo.setMyaccountcode(invvo.getVpurchtaxno());// 本方账户
			headvo.setMyaccountname(invvo.getVpurchname());// 本方账户名称
		}

		ImageLibraryVO libvo = getImageLibraryVO(hvo.getPk_corp(), invvo);
		if (libvo != null) {
			headvo.setSourcebillid(libvo.getPk_image_library());
			headvo.setPk_image_group(libvo.getPk_image_group());
			headvo.setPk_image_library(libvo.getPk_image_library());
			String imagepath = getImgpath(libvo);
			if (!StringUtil.isEmpty(imagepath)) {
				headvo.setImgpath(imagepath);
			}
		}
		setDefaultValue(headvo, hvo, grpvo);

		return headvo;
	}

	private DZFDate getNewDate(String dinvoicedate, DZFDate doperatedate, TzpzHVO hvo) {

		if (StringUtil.isEmpty(dinvoicedate)) {
			hvo.setIautorecognize(0);
			return doperatedate;
		}

		DZFDate tdate = getDzfDateData(dinvoicedate);
		// 年度不在上传年的 按照上传年记录 月份不在上传季度的 按照上传月份 日期不在当月日期中的 按照上传月的最后一天

		int tyear = tdate.getYear();
		int tmonth = tdate.getMonth();
		int tquarter = getQuarter(tmonth);
		int tday = tdate.getDay();

		int dyear = doperatedate.getYear();
		int dmonth = doperatedate.getMonth();
		int dquarter = getQuarter(dmonth);
		int dday = doperatedate.getDay();

		if (tyear != dyear) {
			hvo.setIautorecognize(0);
			tyear = dyear;
		}

		if (tquarter != dquarter) {
			hvo.setIautorecognize(0);
			tmonth = dmonth;
		}
		String sDate = Integer.toString(tyear);
		sDate = sDate + (tmonth < 10 ? "0" + tmonth : tmonth);
		if (tday > dday) {
			tday = dday;
		}
		sDate = sDate + (tday < 10 ? "0" + tday : tday);
		return new DZFDate(sDate);
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

	private void setDefaultValue(BankBillToStatementVO vo, TzpzHVO hvo, ImageGroupVO grpvo) {
		vo.setCoperatorid(grpvo.getCoperatorid());
		vo.setPk_corp(grpvo.getPk_corp());
		vo.setDoperatedate(new DZFDate());
//		vo.setPeriod(DateUtils.getPeriod(vo.getTradingdate()));

		if (!StringUtil.isEmpty(hvo.getPk_model_h())) {
			vo.setPk_model_h(hvo.getPk_model_h());
			DcModelHVO dchvo = (DcModelHVO) singleObjectBO.queryByPrimaryKey(DcModelHVO.class, hvo.getPk_model_h());
			if (dchvo != null) {
				vo.setBusitypetempname(dchvo.getBusitypetempname());
			}
		}
	}

	private int matchImageType(OcrInvoiceVO vo, CorpVO corpvo) {

		String saleName = filterName(vo.getVsalename());
		String purName = filterName(vo.getVpurchname());
		String corpName = filterName(corpvo.getUnitname());

		if (!StringUtil.isEmpty(purName) && (purName.contains(corpName) || corpName.contains(purName))) {
			return VATInvoiceTypeConst.VAT_PAY_INVOICE;
		} else if (!StringUtil.isEmpty(saleName) && ((saleName.contains(corpName) || corpName.contains(saleName)))) {
			return VATInvoiceTypeConst.VAT_RECE_INVOICE;
		} else {
			return VATInvoiceTypeConst.UNDETERMINED_INVOICE;
		}
	}

}

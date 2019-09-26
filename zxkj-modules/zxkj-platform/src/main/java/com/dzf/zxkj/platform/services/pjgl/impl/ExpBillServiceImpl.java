package com.dzf.zxkj.platform.services.pjgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.exception.WiseRunException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pzgl.*;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.EAModelBVO;
import com.dzf.zxkj.platform.model.sys.EAModelHVO;
import com.dzf.zxkj.platform.services.pjgl.IExpBillService;
import com.dzf.zxkj.platform.services.pzgl.IVoucherService;
import com.dzf.zxkj.platform.services.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.util.AmountUtil;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("gl_bxdserv")
public class ExpBillServiceImpl implements IExpBillService {
//	public static void main(String[] args){
//		try {
//			
//			saveImpWbx("","");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//		}
//	}

	private SingleObjectBO singleObjectBO = null;
	private IVoucherService gl_tzpzserv;
	private IYntBoPubUtil yntBoPubUtil = null;

	public IYntBoPubUtil getYntBoPubUtil() {
		return yntBoPubUtil;
	}
	@Autowired
	public void setYntBoPubUtil(IYntBoPubUtil yntBoPubUtil) {
		this.yntBoPubUtil = yntBoPubUtil;
	}
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	public IVoucherService getGl_tzpzserv() {
		return gl_tzpzserv;
	}
	@Autowired
	public void setGl_tzpzserv(IVoucherService gl_tzpzserv) {
		this.gl_tzpzserv = gl_tzpzserv;
	}


	@Override
	public List<ExpBillHVO> query(ExpBillParamVO param)
			throws DZFWarpException {
		List<ExpBillHVO> listres = new ArrayList<ExpBillHVO>();
		SQLParameter sp = new SQLParameter();
		StringBuffer condition = new StringBuffer();
		condition.append(" pk_corp = ? and nvl(dr,0) = 0 ");
		sp.addParam(param.getPk_corp());
		if (param.getBxbdate() != null) {
			condition.append("and reimburseDate >= ? ");
			sp.addParam(param.getBxbdate());
		}
		if (param.getBxedate() != null) {
			condition.append("and reimburseDate <= ? ");
			sp.addParam(param.getBxedate());
		}
		if (param.getScbdate() != null) {
			condition.append("and submitDate >= ? ");
			sp.addParam(param.getScbdate());
		}
		if (param.getScedate() != null) {
			condition.append("and submitDate <= ? ");
			sp.addParam(param.getScedate());
		}
		if (!StringUtil.isEmpty(param.getBxr())) {
			condition.append("and applicant = ? ");
			sp.addParam(param.getBxr());
		}
		if (param.getBxdStatus() != null && param.getBxdStatus() != 8) {
			condition.append("and billStatus = ? ");
			sp.addParam(param.getBxdStatus());
		}
		if (!StringUtil.isEmpty(param.getZy())){
			condition.append("and summary = ? ");
			sp.addParam(param.getZy());
		}
		
		ExpBillHVO[] result = (ExpBillHVO[]) singleObjectBO.queryByCondition(ExpBillHVO.class, condition.toString(), sp);
		if (result != null && result.length > 0) {
			for (ExpBillHVO expBillHVO : result) {
				expBillHVO.setChinesemny(AmountUtil.toChinese(expBillHVO.getMny().toString()));
				expBillHVO.setChildren(queryB(expBillHVO.getPk_expbill_h(), param.getPk_corp()));
			}
			listres = Arrays.asList(result);
		}

		return listres;
	}
	@Override
	public ExpBillHVO queryHeadByID(String id, String pk_corp)
			throws DZFWarpException {
		ExpBillHVO hvo = new ExpBillHVO();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(id);
		String condition = " pk_corp = ? and pk_expbill_h = ? and nvl(dr,0) = 0 ";
		ExpBillHVO[] result = (ExpBillHVO[]) singleObjectBO.queryByCondition(ExpBillHVO.class, condition, sp);
		if (result != null && result.length > 0) {
			hvo = result[0];
		}
		return hvo;
	}
	@Override
	public ExpBillHVO queryByID(String id, String pk_corp)
			throws DZFWarpException {
		ExpBillHVO hvo = new ExpBillHVO();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(id);
		String condition = " pk_corp = ? and pk_expbill_h = ? and nvl(dr,0) = 0 ";
		ExpBillHVO[] result = (ExpBillHVO[]) singleObjectBO.queryByCondition(ExpBillHVO.class, condition, sp);
		if (result != null && result.length > 0) {
			hvo = result[0];
			hvo.setChildren(queryB(hvo.getPk_expbill_h(), pk_corp));
		}
		return hvo;
	}
	private ExpBillBVO[] queryB(String hid, String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(hid);
		String condition = " pk_corp = ? and pk_expbill_h = ? and nvl(dr,0) = 0 ";
		ExpBillBVO[] result = (ExpBillBVO[]) singleObjectBO.queryByCondition(ExpBillBVO.class, condition, sp);
		return result;
	}

	@Override
	public ExpBillHVO save(ExpBillHVO vo)
			throws DZFWarpException {
		return (ExpBillHVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
	}
	public List<ExpBillHVO> saveImpWbx(String xmlStr,String pk_corp) throws DZFWarpException{
		List<ExpBillHVO> hvoList = new ArrayList<ExpBillHVO>();
		List<List<ExpBillBVO>> bvoLists = new ArrayList<List<ExpBillBVO>>();
//		SAXReader reader = new SAXReader();
//        String filePath = "C:/Users/Administrator/Desktop/xml.xml";
//        File file = new File(filePath);
//        Document document = reader.read(file);// 读取XML文件
		try {
			Document document = DocumentHelper.parseText(xmlStr);
			Element root = document.getRootElement();
			Element payments = root.element("PAYMENTS");
			Iterator eleIter = payments.elements().iterator();
			while (eleIter.hasNext()) {
				ExpBillHVO hvo = new ExpBillHVO();
				hvo.setSubmitdate(new DZFDate());//上传时间
				hvo.setPk_corp(pk_corp);
				hvo.setBillstatus(0);//未转凭证
				Element ele = (Element) eleIter.next();
				
				hvo.setBill_id(ele.element("pk_bxd_h").getText());
				hvo.setPk_applicant(ele.element("pk_user").getText());
				Element user_name = ele.element("pk_user_name");
				if (user_name != null)
					hvo.setApplicant(user_name.getText());
				DZFDate vDate = new DZFDate(ele.element("vdate").getText());
				hvo.setReimbursedate(vDate);
				hvo.setSummary(ele.element("title").getText());
				DZFDouble vTotal = new DZFDouble(ele.element("vtotal").getText());
				hvo.setMny(vTotal);
				hvo.setMemo(ele.element("vmemo").getText());
				Element payType = ele.element("payType");
				if (payType != null)
					hvo.setPaytype(StringUtil.isEmpty(payType.getText()) ? 0 : Integer.valueOf(payType.getText()));
				Element payTime = ele.element("payTime");
				if (payTime != null)
					hvo.setPaydate(new DZFDateTime(payTime.getText()));
				
				Element invoices = ele.element("INVOICES");
				List<ExpBillBVO> bvoList = new ArrayList<ExpBillBVO>();
				for (Element invoice : (List<Element>)invoices.elements()) {
					ExpBillBVO bvo = new ExpBillBVO();
					bvo.setPk_corp(pk_corp);
					Element vcostcode = invoice.element("vcostcode");
					if (vcostcode != null)
						bvo.setVtypecode(vcostcode.getText());
					Element vcostname = invoice.element("vcostname");
					if (vcostname != null)
						bvo.setVtypename(vcostname.getText());
					Element pk_costtype = invoice.element("pk_costtype");
					if (pk_costtype != null)
						bvo.setPk_vtype(pk_costtype.getText());
					Element total_amount = invoice.element("total_amount");
					if (total_amount != null)
						bvo.setTotalmny(new DZFDouble(total_amount.getText()));
					Element vbuildtime = invoice.element("vbuildtime");
					if (vbuildtime != null)
						bvo.setInvoice_date(new DZFDate(vbuildtime.getText().substring(0,10)));
					Element picpath = invoice.element("picpath");
					if (picpath != null)
						bvo.setPicPath(picpath.getText());
					Element buildtime = invoice.element("buildtime");
					if (buildtime != null)
						bvo.setCreatedate(new DZFDate(buildtime.getText()));
					Element pk_bxd_h = invoice.element("pk_bxd_h");
					if (pk_bxd_h != null)
						bvo.setBill_id(pk_bxd_h.getText());
					Element pk_xfyw = invoice.element("pk_xfyw");
					if (pk_xfyw != null)
						bvo.setInvoice_id(pk_xfyw.getText());
					bvoList.add(bvo);
				}
				bvoLists.add(bvoList);
				hvo.setNbills(bvoList.size());
				hvoList.add(hvo);
//				save(hvo);
			}
			String[] hids = singleObjectBO.insertVOArr(pk_corp, hvoList.toArray(new ExpBillHVO[0]));
			List<ExpBillBVO> insertBvos = new ArrayList<ExpBillBVO>();
			for (int i = 0; i < hvoList.size(); i++) {
				List<ExpBillBVO>  children = bvoLists.get(i);
				for (ExpBillBVO child : children) {
					child.setPk_expbill_h(hids[i]);
				}
				insertBvos.addAll(children);
			}
			singleObjectBO.insertVOArr(pk_corp, insertBvos.toArray(new ExpBillBVO[0]));
		}  catch ( Exception e) {
			throw new WiseRunException(e);
		}
		return hvoList;
	}
	
	@Override
	public ExpBillHVO update(ExpBillHVO vo)
			throws DZFWarpException {
		singleObjectBO.update(vo);
		return vo;
	}
	@Override
	public void updateFromVch(TzpzHVO pzvo)
			throws DZFWarpException {
		String sql = " update ynt_expbill_h set pk_voucher = '', billStatus = 0 where pk_expbill_h = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pzvo.getSourcebillid());
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public ExpBillHVO saveVoucher(String[] bills, CorpVO corpVo, String userid)
			throws DZFWarpException {
		ExpBillHVO billvo = new ExpBillHVO();
		for (String id : bills) {
			ExpBillHVO vo = queryByID(id, corpVo.getPk_corp());
			if (vo.getBillstatus() == 1) {
				throw new BusinessException("已生成凭证，请勿重复提交！");
			}
			if (!corpVo.getPk_corp().equals(vo.getPk_corp())) {
				throw new BusinessException("无权操作！");
			}
			billvo = createVoucher(vo,corpVo,userid);
		}
		return billvo;
	}
	
	private ExpBillHVO createVoucher(ExpBillHVO vo, CorpVO corpVo, String userid)
			throws DZFWarpException {
		String payType = vo.getPaytype().toString();
		String pk_corp = corpVo.getPk_corp();
		
		ExpBillBVO[] bvos = (ExpBillBVO[]) vo.getChildren();
		HashMap<String, TzpzBVO> jfMap = new HashMap<String, TzpzBVO>();
		HashMap<String, TzpzBVO> dfMap = new HashMap<String, TzpzBVO>();
		DZFDouble jf = DZFDouble.ZERO_DBL;
		DZFDouble df = DZFDouble.ZERO_DBL;
		
		String bill_summary = vo.getSummary();
		for (ExpBillBVO expBillBVO : bvos) {
			String vtypecode = expBillBVO.getVtypecode();
			EAModelBVO[] models = queryModel(payType,vtypecode,corpVo);
			for (EAModelBVO model : models) {
				DZFDouble mny = new DZFDouble();
				
				if (model.getVfield().equals("mny"))
					mny = expBillBVO.getTotalmny();
				else
					throw new BusinessException("找不到金额！");
				TzpzBVO bvo = new TzpzBVO();
				bvo.setPk_accsubj(model.getPk_accsubj()) ;
				bvo.setDr(0);
				bvo.setZy(model.getZy());//摘要
				//币种，默认人民币
				bvo.setPk_currency(yntBoPubUtil.getCNYPk());//(pub_utilserv.getCNYPk()) ;
				bvo.setNrate(new DZFDouble(1));
				bvo.setPk_corp(vo.getPk_corp());
				if(model.getDirection() == 0){//借方
					if (!StringUtil.isEmpty(expBillBVO.getMemo())) {
						bvo.setZy(expBillBVO.getMemo());
					} else if (!StringUtil.isEmpty(expBillBVO.getVtypename())) {
						bvo.setZy(expBillBVO.getVtypename());
					}
					if (jfMap.get(model.getPk_accsubj()) != null) {
						bvo = jfMap.get(model.getPk_accsubj());
						DZFDouble jfmny = bvo.getJfmny();
						if (jfmny != null) {
							bvo.setJfmny(jfmny.add(mny));
						}
					} else {
						bvo.setJfmny(mny) ;
					}
					jfMap.put(model.getPk_accsubj(), bvo);
					jf = jf.add(mny);
				} else {//贷方
					if (!StringUtil.isEmpty(bill_summary)) {
						bvo.setZy(bill_summary);
					}
					if (dfMap.get(model.getPk_accsubj()) != null) {
						bvo = dfMap.get(model.getPk_accsubj());
						DZFDouble dfmny = bvo.getDfmny();
						if (dfmny != null) {
							bvo.setDfmny(dfmny.add(mny));
						}
					} else {
						bvo.setDfmny(mny);
					}
					dfMap.put(model.getPk_accsubj(), bvo);
					df = df.add(mny);
				}
			}
		}
		TzpzHVO headVO = new TzpzHVO() ;
		DZFDate date = new DZFDate();
		headVO.setDr(0);
		headVO.setPk_corp(vo.getPk_corp());
		headVO.setPzlb(0) ;//凭证类别：记账
		headVO.setJfmny(jf) ;
		headVO.setDfmny(df) ;
		headVO.setCoperatorid(userid) ;
		headVO.setIshasjz(DZFBoolean.FALSE) ;
		headVO.setDoperatedate(date) ;
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, date));//(pub_utilserv.getNewVoucherNo(pk_corp, doperatedate)) ;
		headVO.setVbillstatus(8) ;//默认自由态	
		headVO.setSourcebillid(vo.getPk_expbill_h()) ;
		headVO.setSourcebilltype(IBillTypeCode.HP50) ;//来源
		headVO.setPeriod(date.toString().substring(0, 7));
		headVO.setVyear(Integer.valueOf(date.toString().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		List<TzpzBVO> jbodyList = new ArrayList<>(jfMap.values());
		List<TzpzBVO> dbodyList = new ArrayList<>(dfMap.values());
		jbodyList.addAll(dbodyList);
		TzpzBVO[] bodyArray = new TzpzBVO[jbodyList.size()];
		jbodyList.toArray(bodyArray);
		headVO.setChildren(bodyArray);
		gl_tzpzserv.saveVoucher(corpVo, headVO);
		vo.setPk_voucher(headVO.getPk_tzpz_h());
		vo.setBillstatus(1);//已生成凭证
		vo = (ExpBillHVO) singleObjectBO.saveObject(vo.getPk_corp(), vo);
		return vo;
	}
	//根据公司，结算方式查找模板
	private EAModelBVO[] queryModel(String payType, String vspstylecode, CorpVO corp){
		SQLParameter hparams = new SQLParameter();
		hparams.addParam(corp.getPk_corp());
		hparams.addParam(vspstylecode);
		hparams.addParam(payType);
		String hcondition = " pk_corp = ? and vspstylecode=? and szstylecode=?  and nvl(dr,0)=0 ";
		EAModelHVO[] hmodels = (EAModelHVO[]) singleObjectBO.queryByCondition(EAModelHVO.class, hcondition, hparams);
		if(hmodels == null || hmodels.length == 0){
			return queryModelGroup(payType, vspstylecode, corp);
		}
		EAModelHVO hmodel = hmodels[0];
		SQLParameter params = new SQLParameter();
		params.addParam(corp.getPk_corp());
		params.addParam(hmodel.getPk_model_h());
		String condition = " pk_corp = ? and pk_model_h = ? and nvl(dr,0)=0 ";
		EAModelBVO[] models = (EAModelBVO[]) singleObjectBO.queryByCondition(EAModelBVO.class, condition, params);
		return models;
	}
	//根据科目方案，结算方式查找模板
	private EAModelBVO[] queryModelGroup(String payType, String vspstylecode, CorpVO corp){
		SQLParameter hparams = new SQLParameter();
		hparams.addParam(IDefaultValue.DefaultGroup);
		hparams.addParam(corp.getCorptype());
		hparams.addParam(vspstylecode);
		hparams.addParam(payType);
		String hcondition = " pk_corp = ? and pk_trade_accountschema=? and vspstylecode=? and szstylecode=?  and nvl(dr,0)=0 ";
		EAModelHVO[] hmodels = (EAModelHVO[]) singleObjectBO.queryByCondition(EAModelHVO.class, hcondition, hparams);
		if(hmodels == null || hmodels.length == 0){
			throw new BusinessException("找不到对应模板！");
		}
		EAModelHVO hmodel = hmodels[0];
		SQLParameter params = new SQLParameter();
		params.addParam(IDefaultValue.DefaultGroup);
		params.addParam(hmodel.getPk_model_h());
		String condition = " pk_corp = ? and pk_model_h = ? and nvl(dr,0)=0 ";
		EAModelBVO[] models = (EAModelBVO[]) singleObjectBO.queryByCondition(EAModelBVO.class, condition, params);
		//将模板翻译成公司级模板
		for(int i = 0; i < models.length; i++){
			//借方科目
			String kmid = models[i].getPk_accsubj();
			//根据行业会计科目主键找到公司会计科目主键						
			kmid = yntBoPubUtil.getCorpAccountPkByTradeAccountPk(kmid, corp.getPk_corp());
			models[i].setPk_accsubj(kmid);
		}
		return models;
	}

	@Override
	public void updateCwdj(String appSecret, String pk_corp) throws DZFWarpException {
		String sql = " update bd_corp set def10 = ? where pk_corp = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(appSecret);
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public void deleteVoucher(String[] bills, String pk_corp, String isqjsy)
			throws DZFWarpException {
		VoucherParamVO paramVO = new VoucherParamVO();
		paramVO.setPk_corp(pk_corp);
		for (String bill : bills) {
			ExpBillHVO billvo = queryHeadByID(bill, pk_corp);
			if (billvo == null)
				throw new BusinessException("报销单不存在！");
			paramVO.setPk_tzpz_h(billvo.getPk_voucher());
			TzpzHVO pzvo = gl_tzpzserv.queryHeadVoById(paramVO);
			if(pzvo == null)
				throw new BusinessException("报销单未生成凭证！");
			if(pzvo.getIshasjz() != null && pzvo.getIshasjz().booleanValue()){
				throw new BusinessException("凭证已记账！");
			}
			if(pzvo.getVbillstatus() != 8){
				throw new BusinessException("凭证已审核！");
			}
			
			if (isqjsy == null || isqjsy.equals("N")) {
				gl_tzpzserv.checkQjsy(pzvo);
			} else {
				pzvo.setIsqxsy(new DZFBoolean(isqjsy));
			}
			gl_tzpzserv.deleteVoucher(pzvo);
		}
		
	}

}

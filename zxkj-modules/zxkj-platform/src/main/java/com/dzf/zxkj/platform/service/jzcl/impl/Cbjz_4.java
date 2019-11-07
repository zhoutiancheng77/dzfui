package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IcConst;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.jzcl.CostForwardVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TransFerVOInfo;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IQueryLastNum;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;

import java.util.ArrayList;
import java.util.List;

/**
 * 工业结转
 * @author zpm
 *
 */
public class Cbjz_4 {

	private SingleObjectBO singleObjectBO;
	
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private IVoucherService voucher;
	
	private IQueryLastNum ic_rep_cbbserv;
	
	private ICbComconstant gl_cbconstant;
	
	private IPurchInService ic_purchinserv;
	
	private IParameterSetService parameterserv;
	
	public Cbjz_4(ICbComconstant gl_cbconstant,IYntBoPubUtil yntBoPubUtil,SingleObjectBO singleObjectBO,
			IVoucherService voucher,IQueryLastNum ic_rep_cbbserv,IPurchInService ic_purchinserv,IParameterSetService parameterserv){
		this.singleObjectBO = singleObjectBO;
		this.yntBoPubUtil = yntBoPubUtil;
		this.voucher = voucher;
		this.ic_rep_cbbserv = ic_rep_cbbserv;
		this.gl_cbconstant = gl_cbconstant;
		this.ic_purchinserv = ic_purchinserv;
		this.parameterserv = parameterserv;
	}

	public QmclVO save(CorpVO corpVo, TransFerVOInfo fervos, String userid)
			throws BusinessException {
		QmclVO vo = fervos.getQmvo();
		// 转凭证
		IndustrySave save = new IndustrySave(yntBoPubUtil,singleObjectBO,voucher);
		List<List<CostForwardVO>> list = new ArrayList<List<CostForwardVO>>();
		list.add(fervos.getCostforwardvolist2());
		list.add(fervos.getCostforwardvolist3());
		list.add(fervos.getCostforwardvolist5());
		// 存储未完工    工业企业库存   未完工数据取自发生额及余额表
//		NwgSave nwgsave = new NwgSave();
//		List<Industinvtory_qcvo> insertlist = nwgsave.createInv_qc(fervos);
//		if(insertlist != null && insertlist.size() > 0){
//			singleObjectBO.insertVOArr(insertlist.get(0).getPk_corp(),
//					insertlist.toArray(new Industinvtory_qcvo[0]));
//		}
		//
		TzpzHVO billvo =null;
		for (int i = 0 ;i< list.size();i++) {
			List<CostForwardVO> list1 = list.get(i);
			if (list1 != null && list1.size() > 0) {
				 billvo = save.createVoucher(fervos, list1,userid);
				 //存凭证
				 billvo = voucher.saveVoucher(corpVo, billvo);
				 if(i == list.size()-1){//最后一步
					//完工产品入库单
					saveWGrkbill(corpVo,billvo,vo);
				 }
			}
		}
		// 成本销售结转
		Cbjz_3 c3 = new Cbjz_3(gl_cbconstant,yntBoPubUtil,singleObjectBO,voucher,ic_rep_cbbserv,parameterserv);
		if (corpVo.getIbuildicstyle() == null || corpVo.getIbuildicstyle() == 0) {//库存老模式
			vo = c3.save(corpVo, fervos.getQmvo(),userid);
		} else if(corpVo.getIbuildicstyle()!=null && corpVo.getIbuildicstyle() ==1){//库存新模式
			vo = c3.savemode2(corpVo, fervos.getQmvo(),userid);
		}
		//更改状态
		Cbjz_1 cb = new Cbjz_1(yntBoPubUtil,singleObjectBO,voucher);
		cb.save(corpVo,vo);
		return vo;
	}
	
	/**
	 * 完工产品入库单
	 */
	public void saveWGrkbill(CorpVO corpVo, TzpzHVO billvo, QmclVO vo){
		if(IcCostStyle.IC_ON.equals(corpVo.getBbuildic()) &&//启用库存
				corpVo.getIbuildicstyle()!=null && 
				corpVo.getIbuildicstyle() ==1){//库存新模式
			IntradeHVO tradevo = createIntradeHVO(billvo,vo);
			ic_purchinserv.save(tradevo,false);
		}
	}
	
	public IntradeHVO createIntradeHVO(TzpzHVO billvo,QmclVO vo){
		String pk_corp = billvo.getPk_corp();
		DZFDate billdate = billvo.getDoperatedate();
		IntradeHVO hvo = new IntradeHVO();
		//保存生成单据号
//		String dbillid = ic_purchinserv.getNewBillNo(pk_corp,billdate,IcConst.WGTYPE);
//		hvo.setDbillid(dbillid);
		hvo.setDbilldate(billdate);
//		hvo.setIpayway(ipayway);//付款方式
//		hvo.setIarristatus(iarristatus);//到货状态 
		hvo.setCbusitype(IcConst.WGTYPE);//业务类型
		hvo.setCreator(billvo.getCoperatorid());
		hvo.setPk_corp(pk_corp);
		hvo.setSourcebilltype(IBillTypeCode.HP34);
		hvo.setSourcebillid(vo.getPk_qmcl());
		hvo.setPzh(billvo.getPzh());//填制凭证信息
		hvo.setPzid(billvo.getPk_tzpz_h());
		hvo.setIsjz(DZFBoolean.TRUE);
		//
		TzpzBVO[] pzbvos = (TzpzBVO[])billvo.getChildren();
		List<IctradeinVO> invo = new ArrayList<IctradeinVO>();
		for(int i = 0 ;i < pzbvos.length;i++){
			if(pzbvos[i].getNnumber()!=null && pzbvos[i].getNnumber().doubleValue()>0){
				IctradeinVO chvo = new IctradeinVO();
				chvo.setPk_inventory(pzbvos[i].getPk_inventory());
				chvo.setNprice(pzbvos[i].getNprice());
				chvo.setNnum(pzbvos[i].getNnumber());
				chvo.setNtotaltaxmny(pzbvos[i].getJfmny());
				chvo.setPk_subject(pzbvos[i].getPk_accsubj());
				chvo.setNymny(pzbvos[i].getJfmny());
				chvo.setPk_corp(pk_corp);
				chvo.setPk_voucher(pzbvos[i].getPk_tzpz_h());
				chvo.setPk_voucher_b(pzbvos[i].getPk_tzpz_b());
				chvo.setPzh(billvo.getPzh());
				chvo.setZy(pzbvos[i].getZy());
				invo.add(chvo);
			}
		}
		hvo.setChildren(invo.toArray(new IctradeinVO[0]));
		return hvo;
	}
}
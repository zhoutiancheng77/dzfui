package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.CostForwardVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.TransFerVOInfo;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 工业结转
 *
 */
public class IndustrySave {
	private IYntBoPubUtil yntBoPubUtil = null;
	
	private SingleObjectBO singleObjectBO = null;
	
	private IVoucherService voucher;
	
	public IndustrySave(IYntBoPubUtil yntBoPubUtil, SingleObjectBO singleObjectBO, IVoucherService voucher){
		this.yntBoPubUtil = yntBoPubUtil;
		this.singleObjectBO = singleObjectBO;
		this.voucher = voucher;
	}
	
	
	public TzpzHVO createVoucher(TransFerVOInfo fervos, List<CostForwardVO> list1, String userid)throws BusinessException {
		 QmclVO vo = fervos.getQmvo();
		 TzpzHVO bill1 =  createPzvos(vo,list1,"",userid);
		 return bill1;
	}
	
	public TzpzHVO createVoucherByqmclVO(QmclVO qmclvo,List<CostForwardVO> list1,String cbjzCount,String userid)throws BusinessException {
//		 QmclVO vo = fervos.getQmvo();
		 TzpzHVO bill1 =  createPzvos(qmclvo,list1,cbjzCount,userid);
		 return bill1;
	}
	
	private TzpzHVO createPzvos(QmclVO vo,List<CostForwardVO> list,String cbjzCount,String userid)throws BusinessException {
		if(list == null || list.size() == 0){
			return null;
		}
		DZFDouble jf = DZFDouble.ZERO_DBL;
		DZFDouble df = DZFDouble.ZERO_DBL;
		TzpzBVO[] bodyvos = new TzpzBVO[list.size()];
		for(int i = 0 ;i < list.size(); i++){
			CostForwardVO cvo = list.get(i);
			bodyvos[i] = new TzpzBVO() ;
			bodyvos[i].setPk_inventory(cvo.getPk_inventory());
			bodyvos[i].setNnumber(cvo.getNnum());
			bodyvos[i].setVcode(cvo.getVcode());
			bodyvos[i].setVname(cvo.getVname());
			bodyvos[i].setPk_accsubj(cvo.getPk_accsubj()) ;
			if(cvo.getNnum() != null && cvo.getNnum().doubleValue() > 0){
				bodyvos[i].setNprice(SafeCompute.div(cvo.getJfmny(), cvo.getNnum()));
			}
			bodyvos[i].setJfmny(cvo.getJfmny()) ;
			bodyvos[i].setDfmny(cvo.getDfmny());
			bodyvos[i].setZy(cvo.getZy()) ;
			bodyvos[i].setPk_currency(yntBoPubUtil.getCNYPk()) ;
			bodyvos[i].setNrate(new DZFDouble(1));
			bodyvos[i].setPk_corp(vo.getPk_corp());
			jf = SafeCompute.add(jf, cvo.getJfmny());
			df = SafeCompute.add(df, cvo.getDfmny());
		}
		
		// 汇总vo

		Map<String, TzpzBVO> map = new LinkedHashMap();
		for (TzpzBVO bvo : bodyvos) {
			String inv = constructTzpzKey(bvo);
			if (StringUtil.isEmpty(inv)) {
				inv = "aaaaa";
			}
			TzpzBVO temp = null;
			if (!map.containsKey(inv)) {
				temp = bvo;
			} else {
				temp = map.get(inv);
				temp.setNnumber(SafeCompute.add(temp.getNnumber(), bvo.getNnumber()));
				temp.setDfmny(SafeCompute.add(temp.getDfmny(), bvo.getDfmny()));
				temp.setYbdfmny(SafeCompute.add(temp.getYbdfmny(), bvo.getYbdfmny()));
				temp.setJfmny(SafeCompute.add(temp.getJfmny(), bvo.getJfmny()));
				temp.setYbjfmny(SafeCompute.add(temp.getYbjfmny(), bvo.getYbjfmny()));
			}
			if (temp.getNnumber() != null && DZFDouble.ZERO_DBL.compareTo(temp.getNnumber()) != 0) {
				temp.setNprice(SafeCompute.div(temp.getJfmny(), temp.getNnumber()));
			}
			map.put(inv, temp);
		}
		
		TzpzHVO headVO = new TzpzHVO() ;
		headVO.setPk_corp(vo.getPk_corp()) ;
		headVO.setPzlb(0) ;//凭证类别：记账
		headVO.setJfmny(jf) ;
		headVO.setDfmny(df) ;
		headVO.setCoperatorid(userid) ;
		headVO.setIshasjz(DZFBoolean.FALSE) ;
//		DZFDate nowDate = DZFDate.getDate(new Long(InvocationInfoProxy.getInstance().getDate())) ;
//		headVO.setDoperatedate(nowDate) ;
		DZFDate nowDatevalue =  getPeroidDZFDate(vo) ;
		headVO.setDoperatedate(nowDatevalue) ;
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(vo.getPk_corp(), nowDatevalue)) ;
		headVO.setVbillstatus(8) ;//默认自由态					
		//记录单据来源
		headVO.setSourcebillid(vo.getPk_qmcl()) ;
		headVO.setCbjzCount(cbjzCount);
		headVO.setSourcebilltype(IBillTypeCode.HP34) ;
		headVO.setPeriod(vo.getPeriod());
		headVO.setVyear(Integer.valueOf(vo.getPeriod().substring(0, 4)));
		headVO.setIsfpxjxm(new DZFBoolean("N"));
		
		headVO.setChildren(map.values().toArray(new TzpzBVO[map.size()]));
		
		return headVO;
	}


	
	private String constructTzpzKey(TzpzBVO bvo) {
		StringBuffer sf = new StringBuffer();
		sf.append("&").append(bvo.getPk_accsubj()).append("&").append(bvo.getPk_inventory()).append("&")
				.append(bvo.getPk_taxitem()).append("&");

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();

	}
	/**
	 * 取期间所属月的最后一天
	 * @param vo
	 * @return
	 */
	public DZFDate getPeroidDZFDate(QmclVO vo){
		DZFDate period = new DZFDate(vo.getPeriod() + "-01");
		period = new DZFDate(vo.getPeriod() + "-" + period.getDaysMonth()) ;
		return period;
	}
	
}
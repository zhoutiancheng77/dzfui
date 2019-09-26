package com.dzf.zxkj.platform.services.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IQmclConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.platform.model.icset.InventorySetVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.icset.IInventoryAccSetService;
import com.dzf.zxkj.platform.services.jzcl.ISMcbftService;
import com.dzf.zxkj.platform.util.Kmschema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 商贸的成本分摊
 * 总账存货使用
 *
 */
@Service("gl_smcbftserv")
public class SMcbftServiceImpl implements ISMcbftService {
	
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv = null;
	
	/**
	 * 是否允许使用成本分摊
	 */
	/**
	 * 参与条件 
	 * 1、启用总账存货。(并且是启用明细核算和大类核算)
	 * 2、工业或者商贸结转。
	 * 3、当修改期末处理，成本结转的凭证，并且带有com.dzf.pub.IBillTypeCode.HP34结转模板。需要重新参与分摊。
	 */
	private boolean isallowCBft(CorpVO corpVo){
		boolean falg = false;
		if(IcCostStyle.IC_INVTENTORY.equals(corpVo.getBbuildic())){
			//
			InventorySetVO vo = gl_ic_invtorysetserv.query(corpVo.getPk_corp());
			int chcbjzfs = InventoryConstant.IC_NO_MXHS;
			if(vo != null){
				chcbjzfs = vo.getChcbjzfs();
			}
			//大类或明细
			if(chcbjzfs == InventoryConstant.IC_CHDLHS 
					|| chcbjzfs == InventoryConstant.IC_FZMXHS){
				//工业和商贸
				Integer cost = corpVo.getIcostforwardstyle();
				if(cost != null && (cost ==  IQmclConstant.z2 || cost ==  IQmclConstant.z3) ){
					falg = true;
				}
			}
		}
		return falg;
	}

	/**
	 * 结转成本分摊
	 * @param headVO
	 * @param qmclvo
	 * @param corpVo
	 */
	@Override
	public void saveCBFt(TzpzHVO headVO, CorpVO corpVo) throws DZFWarpException {
		if(headVO == null)
			return;
		//判断是不是期末处理成本结转来的凭证
		if(!IBillTypeCode.HP34.equals(headVO.getSourcebilltype()))
			return;
		//是否是成本结转 的凭证
		if(!Kmschema.ischengbenpz(corpVo, (TzpzBVO[])headVO.getChildren()))
			return;
		//总账存货等判断
		boolean cbft = isallowCBft(corpVo);
		if(!cbft)
			return;
		String period = headVO.getPeriod();
		String pk_corp = corpVo.getPk_corp();
		List<TzpzBVO> list = queryShouRupzBVOs(pk_corp,period,corpVo);
		if(list == null || list.size() ==0)
			return;
		TzpzBVO[] bodys = (TzpzBVO[])headVO.getChildren();
		if(bodys == null || bodys.length == 0)
			return;
		List<TzpzBVO> list2 = new ArrayList<TzpzBVO>();
		for(TzpzBVO bvo : bodys){
			if(bvo.getVdirect()!=null){
				if(bvo.getVdirect().intValue() == 1){//贷方
					list2.add(bvo);
				}
			}else{
				if(bvo.getDfmny()!= null 
						&& bvo.getDfmny().doubleValue()!=0){//贷方
					list2.add(bvo);
				}
			}
		}
		Map<String,TzpzBVO> map1 = DZfcommonTools.hashlizeObjectByPk(list2, new String[]{"fzhsx6"});
		Map<String,List<TzpzBVO>> map2 = DZfcommonTools.hashlizeObject(list, new String[]{"fzhsx6"});
		Iterator<String> it = map2.keySet().iterator();
		List<TzpzBVO> listall = new ArrayList<TzpzBVO>();
		while(it.hasNext()){
			String key = it.next();
			TzpzBVO bvo1 = map1.get(key);
			List<TzpzBVO> list1 = map2.get(key);
			calcCbFT(bvo1,list1);
			listall.addAll(list1);
		}
		if(listall !=null && listall.size()>0){
			singleObjectBO.updateAry(listall.toArray(new TzpzBVO[0]), new String[]{"xsjzcb"});
		}
	}
	
	private void calcCbFT(TzpzBVO bvo1,List<TzpzBVO> list1){
		if(bvo1 == null || bvo1.getDfmny() == null || bvo1.getDfmny().doubleValue() == 0
				|| bvo1.getNnumber() == null || bvo1.getNnumber().doubleValue() ==0
				|| list1 == null || list1.size() == 0)
			return;
		//收入的  数量为负，，考虑这种情况。因为数量为负，贷方金额为负。是冲收入凭证
		//先不考虑为负的情况。为负的情况，不好弄。有负数的情况下，也是没有问题的。
		DZFDouble sum = null;
		for(int i = 0 ;i<list1.size();i++){//TzpzBVO vo: list1
			if(i==list1.size()-1){//最后一笔倒济
				list1.get(i).setXsjzcb(SafeCompute.sub(bvo1.getDfmny(), sum));
			}else{
				DZFDouble jzmny = SafeCompute.div(SafeCompute.multiply(list1.get(i).getNnumber(), bvo1.getDfmny()), bvo1.getNnumber());
				jzmny = jzmny.setScale(2,DZFDouble.ROUND_HALF_UP);//金额
				list1.get(i).setXsjzcb(jzmny);
				sum = SafeCompute.add(sum, jzmny);
			}
		}
	}
	
	private List<TzpzBVO> queryShouRupzBVOs(String pk_corp,String period,CorpVO corpVo){
		StringBuffer sf = new StringBuffer();
		sf.append(" select b.* from ynt_tzpz_b b");
		sf.append(" join ynt_tzpz_h h on h.pk_tzpz_h = b.pk_tzpz_h");
		sf.append(" where h.pk_corp = ? and nvl(h.dr,0)= 0 and nvl(b.dr,0) = 0 ");
		sf.append(" and h.period = ? and b.vicbillcodetype = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(InventoryConstant.IC_STYLE_OUT);
		List<TzpzBVO> list = (List<TzpzBVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(TzpzBVO.class));
		if(list == null || list.size() == 0)
			return null;
		List<TzpzBVO> listn = new ArrayList<TzpzBVO>();
		//过滤出来收入类的发货
		for(TzpzBVO vo : list){
			if(Kmschema.isshouru(corpVo.getCorptype(), vo.getVcode())){
				listn.add(vo);
			}
		}
		return listn;
	}
}
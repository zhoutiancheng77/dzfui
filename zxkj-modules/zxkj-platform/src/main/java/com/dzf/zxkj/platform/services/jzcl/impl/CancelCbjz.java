package com.dzf.zxkj.platform.services.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IcConst;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.constant.InventoryConstant;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DZFValueCheck;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.report.XjllVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.util.Kmschema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * 取消成本结转
 *
 */
public class CancelCbjz {
	
	private SingleObjectBO singleObjectBO;
	
	public CancelCbjz(SingleObjectBO singleObjectBO){
		this.singleObjectBO = singleObjectBO;
	}
	
	/**
	 * 反成本结转 zpm
	 */
	public List<QmclVO> rollbackCbjz(QmclVO[] vos) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new BusinessException("没有需要反成本结转的数据！");
		}
		List<QmclVO> list = new ArrayList<QmclVO>();
		String pk_corp = null;
		// 检查公司是否需要成本结转
		CorpVO corpVO = null;
		SQLParameter sp=null;
		String str;
		for(QmclVO vo : vos){
			// 去掉勾上成本结转
			vo.setIscbjz(DZFBoolean.FALSE);
			vo.setCbjz1(DZFBoolean.FALSE);
//			vo.setCbjz2(DZFBoolean.FALSE);
			vo.setCbjz3(DZFBoolean.FALSE);
			vo.setCbjz4(DZFBoolean.FALSE);
			vo.setCbjz5(DZFBoolean.FALSE);
			vo.setCbjz6(DZFBoolean.FALSE);
			// 公司
			 pk_corp = vo.getPk_corp();
			// 检查公司是否需要成本结转
			 corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class,pk_corp);
			if (corpVO == null) {
				throw new BusinessException("公司主键为" + pk_corp + "的公司已被删除！");
			}
			if (vo.getIsqjsyjz() != null && vo.getIsqjsyjz().booleanValue()) {
				throw new BusinessException("期间:"+vo.getPeriod()+"，已经做损益结转，请先反损益结转！");
			}
			check(vo);
			sp=new SQLParameter();
			// 查找已经生成过的成本结转凭证，并删除之
			String pk_qmcl = vo.getPrimaryKey();
			sp.addParam(pk_qmcl);
			sp.addParam(IBillTypeCode.HP34);
			
			TzpzHVO[] pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(
					TzpzHVO.class, " sourcebillid=? and sourcebilltype=? and nvl(dr,0)=0 ", sp);
			if (pzHeadVOs != null && pzHeadVOs.length > 0) {
				for (TzpzHVO headVO : pzHeadVOs) {
					if (headVO.getIshasjz() != null
							&& headVO.getIshasjz().booleanValue()) {
						// 已有凭证记账
						throw new BusinessException("凭证号：" + headVO.getPzh()
								+ "已记账，不能反操作！");
					}
					if (headVO.getVbillstatus() == 1) {
						// 已有凭证审核通过
						throw new BusinessException("凭证号：" + headVO.getPzh()
								+ "已审核，不能反操作！");
					}
					sp.clearParams();
					sp.addParam(headVO.getPrimaryKey());
					
					if(corpVO.getIbuildicstyle() ==null || corpVO.getIbuildicstyle()!=1){
						// 删除入库单
						str = "update  ynt_ictradein set dr =1 where pk_voucher=? ";
						singleObjectBO.executeUpdate(str, sp);
						// 删除出库单
						str = " update  ynt_ictradeout set dr =1 where pk_voucher=?  ";
						singleObjectBO.executeUpdate(str, sp);
					}
				
					// 先删除表体
					str = " update ynt_tzpz_b set dr =1 where pk_tzpz_h=?  ";
					singleObjectBO.executeUpdate(str, sp);
					// 再删除表头
					str = " update ynt_tzpz_h set dr =1 where pk_tzpz_h=? ";
					singleObjectBO.executeUpdate(str, sp);
					sp.clearParams();
					sp.addParam(vo.getPeriod());
					sp.addParam(headVO.getPk_corp());
					//删除未完工数据
					str = " delete from ynt_industinvtory_qc  where period = ? and pk_corp = ? ";
					singleObjectBO.executeUpdate(str, sp);
					//删除---生成的销售数据
					sp.clearParams();
					sp.addParam(headVO.getPk_tzpz_h());
					sp.addParam(headVO.getPk_corp());
					str = " delete from ynt_subinvtory  where pk_tzpz_h = ? and pk_corp = ? ";
					singleObjectBO.executeUpdate(str, sp);
					
					// 删除现金流量
					XjllVO[] xjllList = queryCashFlow(headVO.getPk_tzpz_h(), headVO.getPk_corp());
					if (xjllList != null && xjllList.length > 0) {
						for (XjllVO xjll : xjllList) {
							singleObjectBO.deleteObject(xjll);
						}
					}
					// 删除税目
					SQLParameter param1 = new SQLParameter();
					param1.addParam(headVO.getPk_corp());
					param1.addParam(headVO.getPk_tzpz_h());
					singleObjectBO.executeUpdate(" delete from ynt_pztaxitem where pk_corp = ? and pk_tzpz_h = ? ", param1);
				}
			}
			// 更新期末处理
			singleObjectBO.update(vo,new String[]{"iscbjz","cbjz1","cbjz3","cbjz4","cbjz5","cbjz6"});
			list.add(vo);
			if(corpVO.getIbuildicstyle() !=null && corpVO.getIbuildicstyle()==1){
				
				//  清除ncost库存成本、vdef1成本单价
				str = " update  ynt_ictradeout set vdef1 = null ,ncost = null where pk_ictrade_h in( select pk_ictrade_h from ynt_ictrade_h  where nvl(isback,'N')='N' and  pk_corp = ? and  dbilldate >=? and dbilldate <=? and nvl(dr,0)=0 and nvl(cbusitype,'46') = ? )";
				sp.clearParams();
				DZFDate speriod = new DZFDate(vo.getPeriod() + "-01");
				DZFDate eperiod = new DZFDate(vo.getPeriod() + "-" +speriod.getDaysMonth()) ;
				sp.addParam(corpVO.getPk_corp());
				sp.addParam(speriod.toString());
				sp.addParam(eperiod.toString());
				sp.addParam(IcConst.XSTYPE);
				singleObjectBO.executeUpdate(str, sp);
				
				//删除产成品完工入库单------启用库存，库存模式为新模式
				sp.clearParams();
				sp.addParam(corpVO.getPk_corp());
				sp.addParam(vo.getPk_qmcl());
				sp.addParam(IBillTypeCode.HP34);
				sp.addParam(IcConst.WGTYPE);//完工入库
				str = " delete from ynt_ictradein where pk_ictrade_h in (select  pk_ictrade_h from ynt_ictrade_h where pk_corp = ? and sourcebillid = ? and sourcebilltype = ? and cbusitype = ? ) ";
				singleObjectBO.executeUpdate(str, sp);
				str = " delete from ynt_ictrade_h where pk_corp = ? and sourcebillid = ? and sourcebilltype = ? and cbusitype = ? ";
				singleObjectBO.executeUpdate(str, sp);
			}
			
			cancelFentan(vo, corpVO);//取消分摊成本
		}
		return list;
	}
	
	private XjllVO[] queryCashFlow(String pk_tzpz_h, String pk_corp) throws DZFWarpException {
		StringBuilder sb = new StringBuilder();
		sb.append("select a.pk_xjll, a.coperatorid, a.doperatedate, a.pk_corp, a.pk_tzpz_h, a.pk_xjllxm, a.vdirect,")
				.append(" a.nmny, b.pzh from YNT_XJLL a ")
				// .append("left join ynt_tzpz_h b on a.pk_tzpz_h = b.pk_tzpz_h
				// and a.pk_corp = b.pk_corp ")
				.append(" join ynt_tzpz_h b on a.pk_tzpz_h = b.pk_tzpz_h   ")
				.append("where a.pk_tzpz_h = ? and a.pk_corp = ? and nvl(a.dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);
		List<XjllVO> rs = (List<XjllVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(XjllVO.class));
		return rs.toArray(new XjllVO[0]);
	}
	
	//取消分摊成本
	private void cancelFentan(QmclVO vo, CorpVO corpvo){
		String bic = corpvo.getBbuildic();
		if(IcCostStyle.IC_INVTENTORY.equals(bic)){
			List<TzpzBVO> list = queryShouRupzBVOs(vo,corpvo);
			if(list == null || list.size() == 0)
				return;
			
			for(TzpzBVO bvo : list){
				bvo.setXsjzcb(null);
			}
			
			singleObjectBO.updateAry(list.toArray(new TzpzBVO[0]),
					new String[]{"xsjzcb"} );
		}
	}
	
	private List<TzpzBVO> queryShouRupzBVOs(QmclVO qmclvo,CorpVO corpVo){
		String pk_corp = qmclvo.getPk_corp();
		String period = qmclvo.getPeriod();
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
	
	/**
	 * 不启用库存的情况下
	 * 反成本结转
	 */
	public List<QmclVO> rollbackCbjzNoic(QmclVO[] vos,String cbjzCount) throws BusinessException {
		if (vos == null || vos.length == 0) {
			throw new BusinessException("没有需要反成本结转的数据！");
		}
		List<QmclVO> list = new ArrayList<QmclVO>();
		String pk_corp =null;
		CorpVO corpVO = null;
		String pk_qmcl = null;
		TzpzHVO[] pzHeadVOs = null;
		SQLParameter sp=new SQLParameter();
		String str;
		for(QmclVO vo : vos){
			
			// 公司
			 pk_corp = vo.getPk_corp();
			// 检查公司是否需要成本结转
			 corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class,pk_corp);
			if (corpVO == null) {
				throw new BusinessException("公司主键为" + pk_corp + "的公司已被删除！");
			}
			if (vo.getIsqjsyjz() != null && vo.getIsqjsyjz().booleanValue()) {
				throw new BusinessException("期间:"+vo.getPeriod()+"，已经做损益结转，请先反损益结转！");
			}
			// 去掉勾上成本结转
			setDefaultValue(cbjzCount, vo);
			check(vo);
			// 查找已经生成过的成本结转凭证，并删除之
			 pk_qmcl = vo.getPrimaryKey();
			
//			TzpzHVO[] pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(
//					TzpzHVO.class, " sourcebillid='" + pk_qmcl
//							+ "' and sourcebilltype='" + IBillTypeCode.HP34
//							+ "' and cbjzCount = '"+ cbjzCount +"' and nvl(dr,0)=0 ", new SQLParameter());
			sp.clearParams();
			if("0".equals(cbjzCount)){
				sp.addParam(pk_qmcl);
				sp.addParam(IBillTypeCode.HP34);
				pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(
						TzpzHVO.class, " sourcebillid=? and sourcebilltype=? and cbjzCount in('1','2','3','4','5','6') and nvl(dr,0)=0 ", sp);
			}else{
				sp.addParam(pk_qmcl);
				sp.addParam(IBillTypeCode.HP34);
				sp.addParam(cbjzCount);
				pzHeadVOs = (TzpzHVO[]) singleObjectBO.queryByCondition(
						TzpzHVO.class, " sourcebillid=? and sourcebilltype=? and cbjzCount = ? and nvl(dr,0)=0 ", sp);
			}
			if (pzHeadVOs != null && pzHeadVOs.length > 0) {
				
				for (TzpzHVO headVO : pzHeadVOs) {
					if (headVO.getIshasjz() != null
							&& headVO.getIshasjz().booleanValue()) {
						// 已有凭证记账
						throw new BusinessException("凭证号：" + headVO.getPzh()
								+ "已记账，不能反操作！");
					}
					if (headVO.getVbillstatus() == 1) {
						// 已有凭证审核通过
						throw new BusinessException("凭证号：" + headVO.getPzh()
								+ "已审核，不能反操作！");
					}
					// 删除入库单
					sp.clearParams();
					sp.addParam(headVO.getPrimaryKey());
					str = "update  ynt_ictradein set dr =1 where pk_voucher=? ";
					singleObjectBO.executeUpdate(str, sp);
					// 删除出库单
					str= " update  ynt_ictradeout set dr =1 where pk_voucher=? ";
					singleObjectBO.executeUpdate(str,sp);
					// 先删除表体
					str= " update ynt_tzpz_b set dr =1 where pk_tzpz_h=?  ";
					singleObjectBO.executeUpdate(str, sp);
					// 再删除表头
					str = " update ynt_tzpz_h set dr =1 where pk_tzpz_h=? ";
					singleObjectBO.executeUpdate(str,sp);
					
					//删除未完工数据
					sp.clearParams();
					sp.addParam(vo.getPeriod());
					sp.addParam(headVO.getPk_corp());
					str = " delete from ynt_industinvtory_qc  where period = ? and pk_corp = ? ";
					singleObjectBO.executeUpdate(str, sp);
					
					//删除---生成的销售数据
					sp.clearParams();
					sp.addParam(headVO.getPk_tzpz_h());
					sp.addParam(headVO.getPk_corp());
					str = " delete from ynt_subinvtory  where pk_tzpz_h = ? and pk_corp = ? ";
					singleObjectBO.executeUpdate(str, sp);
					
					// 删除现金流量
					XjllVO[] xjllList = queryCashFlow(headVO.getPk_tzpz_h(), headVO.getPk_corp());
					if (xjllList != null && xjllList.length > 0) {
						for (XjllVO xjll : xjllList) {
							singleObjectBO.deleteObject(xjll);
						}
					}
					// 删除税目
					SQLParameter param1 = new SQLParameter();
					param1.addParam(headVO.getPk_corp());
					param1.addParam(headVO.getPk_tzpz_h());
					singleObjectBO.executeUpdate(" delete from ynt_pztaxitem where pk_corp = ? and pk_tzpz_h = ? ", param1);
				}
			}
			//取消分摊成本
			cancelFentan(vo, corpVO);
			// 更新期末处理
			String[] columns = null;
			if("1".equals(cbjzCount)){
				columns = new String[]{"cbjz1","iscbjz"};
			}
//			else if("2".equals(cbjzCount)){
//				vo.setCbjz2(DZFBoolean.FALSE);
//				vo.setIscbjz(DZFBoolean.FALSE);
//			}
			else if("3".equals(cbjzCount)){
				columns = new String[]{"cbjz3","iscbjz"};
			}else
			if("4".equals(cbjzCount)){
				columns = new String[]{"cbjz4","iscbjz"};
			}else
			if("5".equals(cbjzCount)){
				columns = new String[]{"cbjz5","iscbjz"};
			}else
			if("6".equals(cbjzCount)){
				columns = new String[]{"cbjz6","iscbjz"};
			}else
			if("0".equals(cbjzCount)){
				vo.setIscbjz(DZFBoolean.FALSE);
				vo.setCbjz1(DZFBoolean.FALSE);
//				vo.setCbjz2(DZFBoolean.FALSE);
				vo.setCbjz3(DZFBoolean.FALSE);
				vo.setCbjz4(DZFBoolean.FALSE);
				vo.setCbjz5(DZFBoolean.FALSE);
				vo.setCbjz6(DZFBoolean.FALSE);
				columns = new String[]{"cbjz1","cbjz3","cbjz4","cbjz5","cbjz6","iscbjz"};
			}
			if(!DZFValueCheck.isEmpty(columns))
				singleObjectBO.update(vo,columns);
			list.add(vo);
		}
		return list;
	}

	private void setDefaultValue(String cbjzCount, QmclVO vo) {
		if("1".equals(cbjzCount)){
			vo.setCbjz1(DZFBoolean.FALSE);
			vo.setIscbjz(DZFBoolean.FALSE);
		}
//		else if("2".equals(cbjzCount)){
//			vo.setCbjz2(DZFBoolean.FALSE);
//			vo.setIscbjz(DZFBoolean.FALSE);
//		}
		else if("3".equals(cbjzCount)){
			vo.setCbjz3(DZFBoolean.FALSE);
			vo.setIscbjz(DZFBoolean.FALSE);
		}else
		if("4".equals(cbjzCount)){
			vo.setCbjz4(DZFBoolean.FALSE);
			vo.setIscbjz(DZFBoolean.FALSE);
		}else
		if("5".equals(cbjzCount)){
			vo.setCbjz5(DZFBoolean.FALSE);
			vo.setIscbjz(DZFBoolean.FALSE);
		}else
		if("6".equals(cbjzCount)){
			vo.setCbjz6(DZFBoolean.FALSE);
			vo.setIscbjz(DZFBoolean.FALSE);
		}else
		if("0".equals(cbjzCount)){
			vo.setIscbjz(DZFBoolean.FALSE);
			vo.setCbjz1(DZFBoolean.FALSE);
//			vo.setCbjz2(DZFBoolean.FALSE);
			vo.setCbjz3(DZFBoolean.FALSE);
			vo.setCbjz4(DZFBoolean.FALSE);
			vo.setCbjz5(DZFBoolean.FALSE);
			vo.setCbjz6(DZFBoolean.FALSE);
		}
	}
	private void check(QmclVO vo) throws BusinessException{
		SQLParameter sp = new  SQLParameter();
		sp.addParam(vo.getPk_corp());
		String sql = " select max(period) period from ynt_qmcl cl where cl.pk_corp = ? and cl.iscbjz ='Y'  ";
		String period = (String)singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String period = null;
				if(rs.next()){
					period = rs.getString("period");
				}
				return period;
			}
		});
		if(period!=null&&period.length()>0){
			if(vo.getPeriod().compareTo(period) < 0 ){
				throw new BusinessException("不能跨期间取消成本结转！");
			}
		}
	}

}

package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IQmclConstant;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.YJJZSetVO;
import com.dzf.zxkj.platform.model.jzcl.YjjzVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.jzcl.IYJJZService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 一键结转
 *
 */
@Service("gl_yjjzserv")
public class YJJZServiceImpl implements IYJJZService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private ICorpService corpService;

	
	@Override
	public YJJZSetVO queryset(String userid, String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		YJJZSetVO[] vos = (YJJZSetVO[])singleObjectBO.queryByCondition(YJJZSetVO.class, 
				" nvl(dr,0) = 0 and pk_corp = ? ", sp);
		YJJZSetVO vo = null;
		if(vos!=null&& vos.length>0){
			vo = vos[0];
		}
		return vo;
	}


	@Override
	public void savejzset(List<YJJZSetVO> list) throws DZFWarpException {
		if(list != null && list.size() > 0){
			StringBuffer sf = new StringBuffer();
			SQLParameter sp = new SQLParameter();
			sf.append(" delete from ynt_yjjzset where pk_corp in (");
			for(int i = 0 ;i<list.size();i++){
				if(i == 0){
					sf.append(" ? ");
				}else{
					sf.append(" ,? ");
				}
				sp.addParam(list.get(i).getPk_corp());
			}
			sf.append(" ) ");
			singleObjectBO.executeUpdate(sf.toString(), sp);
			singleObjectBO.insertVOArr("YJJZBC", list.toArray(new YJJZSetVO[0]));
		}
	}

	@Override
	public List<YjjzVO> query(List<String> corppks, DZFDate starte, DZFDate enddate, String userid, String logindate) throws DZFWarpException {
		// 处理结账逻辑
		// 1、检查所选公司、期间的所有凭证是否全部都记账；
		if (corppks == null || corppks.size() == 0)
			return null;
		String[] pk_corps = corppks.toArray(new String[0]);
		if (pk_corps == null || pk_corps.length < 1) {
			throw new BusinessException("公司不能为空！");
		}
		// 期间起
		if (starte == null) {
			throw new BusinessException("期间起不能为空！");
		}
		if (enddate == null) {
			throw new BusinessException("期间至不能为空！");
		}
		if (enddate.before(starte)) {
			throw new BusinessException("期间至不能在期间起之前！");
		}
		
		String sql = " select t.*,pp.unitname corpname from ynt_yjjz t  ";
		sql = sql + " join bd_corp pp on t.pk_corp = pp.pk_corp where "+ SqlUtil.buildSqlForIn("t.pk_corp", corppks.toArray(new String[0]));
		sql = sql + " and t.period >= ? and t.period <= ? and nvl(t.dr,0) = 0 order by t.period ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(DateUtils.getPeriod(starte));
		sp.addParam(DateUtils.getPeriod(enddate));
		List<YjjzVO> list = (List<YjjzVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(YjjzVO.class));
		Map<String,YjjzVO> maps = DZfcommonTools.hashlizeObjectByPk(list, new String[]{"pk_corp","period"});
		List<YjjzVO> list2 = new ArrayList<YjjzVO>();

		CorpVO pvo = null;
		for(String pk_corp : corppks){
			List<YjjzVO> list1 = new ArrayList<YjjzVO>();
			pvo = corpService.queryByPk(pk_corp);
			DZFDate begdate = pvo.getBegindate();//建账日期
			DZFDate startdate = new DZFDate(DateUtils.getPeriod(starte)+"-01");//开始查询日期
			if(startdate.before(begdate)){
				startdate = new DZFDate(DateUtils.getPeriod(begdate)+"-01");
			}
			DZFDate enddate1 = new DZFDate(DateUtils.getPeriod(enddate)+"-01");
			while(startdate.before(enddate1) || startdate.equals(enddate1)){
				String period = DateUtils.getPeriod(startdate);
				String key = pk_corp+","+period;
				YjjzVO v1 = maps.get(key);
				if(v1 == null){
					v1 = createYjjzvo(pk_corp,period,userid);
					list1.add(v1);
				}
				//赋默认值
				setDefaultValue(v1,pvo);
				list2.add(v1);
				//循环
				long start = startdate.toDate().getTime();
				startdate = new DZFDate(DateUtils.getNextMonth(start));
			}
			if(list1.size()>0){
				singleObjectBO.insertVOArr(pk_corp, list1.toArray(new YjjzVO[0]));
			}
		}
		return list2;
	}
	
	public YjjzVO createYjjzvo(String pk_corp,String period,String usrid){
		YjjzVO vo = new YjjzVO();
		vo.setPk_corp(pk_corp);
		vo.setPeriod(period);

		CorpVO cvo = corpService.queryByPk(pk_corp);
		vo.setCorpname(cvo.getUnitname());
		vo.setIsjz(new DZFBoolean("N"));
		return vo;
	}
	
	
	public void setDefaultValue(YjjzVO yjjzvo,CorpVO corpVO){
		yjjzvo.setIkc(new DZFBoolean(IcCostStyle.IC_ON.equals(corpVO.getBbuildic())));// 库存
		if (corpVO.getIcostforwardstyle() != null
				&& corpVO.getIcostforwardstyle().intValue() == IQmclConstant.z0) {// 如果是直接成本结转也不提示
			yjjzvo.setIkc(DZFBoolean.FALSE);
		}
		yjjzvo.setIgdzc(corpVO.getHoldflag() == null ? DZFBoolean.FALSE : corpVO.getHoldflag());// 是否启用固定资产
		if("一般纳税人".equals(corpVO.getChargedeptname())){
			yjjzvo.setIsybr(DZFBoolean.TRUE);
		}else{
			yjjzvo.setIsybr(DZFBoolean.FALSE);
		}
		yjjzvo.setJzdate(corpVO.getBegindate());
	}


	@Override
	public void updateYjjz(YjjzVO[] vos, String userid, String pk_corp) throws DZFWarpException {
		if(vos == null || vos.length == 0)
			return;
		for(YjjzVO v : vos){
			v.setIsjz(new DZFBoolean(true));
		}
		singleObjectBO.updateAry(vos,new String[]{"isjz"});
	}


	@Override
	public QmclVO queryqmcl(String qmid, String pk_corp) throws DZFWarpException {
		QmclVO vo = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(qmid);
		String where = " pk_corp = ? and pk_qmcl = ? and nvl(dr,0) = 0 ";
		QmclVO[] vos = (QmclVO[])singleObjectBO.queryByCondition(QmclVO.class, where, sp);
		if(vos!=null&&vos.length>0){
			vo = vos[0];
		}
		return vo;
	}
}
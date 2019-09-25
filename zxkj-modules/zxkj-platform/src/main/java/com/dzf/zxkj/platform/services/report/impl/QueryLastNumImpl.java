package com.dzf.zxkj.platform.services.report.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.common.constant.IcConst;
import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.icset.IcbalanceVO;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.report.IQueryLastNum;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("ic_rep_cbbserv")
@SuppressWarnings("all")
public class QueryLastNumImpl implements IQueryLastNum {
	
	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Autowired
	private ICorpService corpService;

	public Map<String, IcbalanceVO> queryLastBanlanceVOs_byMap1(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn)throws DZFWarpException {
		String lastdate = getBalanceDate(pk_corp,currentenddate);
		Map<String,IcbalanceVO> map = queryLastBanlanceVOs_byMap2(lastdate, currentenddate, pk_corp,pk_invtory,isafternonn);
		return map;
	}
	
	public Map<String, QcYeVO> queryLastBanlanceVOs_byMap1NoIC(String currentenddate, String pk_corp, String pk_invtory, boolean isafternonn)throws  DZFWarpException{
		String lastdate = getBalanceDateNoIC(pk_corp,currentenddate);
		//String lastdate  = currentenddate.substring(0,4)+"-01-01";
		Map<String,QcYeVO> map = queryLastBanlanceVOs_byMap2NoIC(lastdate, currentenddate, pk_corp,pk_invtory,isafternonn);
		return map;
	}
	
	/**
	 * 查询截止日期最新库存
	 */
	private Map<String,IcbalanceVO> queryLastBanlanceVOs_byMap2(String lastdate,String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn)throws  DZFWarpException{
		Map<String,IcbalanceVO> map = new java.util.HashMap<String, IcbalanceVO>();
		List<IcbalanceVO> ancevos =queryLastBanlanceVOs_byList2(lastdate,currentenddate,pk_corp,pk_invtory,null,isafternonn);
		if(ancevos != null && ancevos.size() > 0){
			for(IcbalanceVO v : ancevos){
				map.put(v.getPk_inventory(), v);
			}
		}
		return map;
	}
	
	/**
	 * 查询截止日期最新pz  不启用库存
	 */
	private Map<String,QcYeVO> queryLastBanlanceVOs_byMap2NoIC(String lastdate,String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn)throws  DZFWarpException{
		Map<String,QcYeVO> map = new java.util.HashMap<String, QcYeVO>();
		List<QcYeVO> ancevos =queryLastBanlanceVOs_byList2NoIC(lastdate,currentenddate,pk_corp,pk_invtory,isafternonn);
		if(ancevos != null && ancevos.size() > 0){
			for(QcYeVO v : ancevos){
				map.put(v.getPk_accsubj(), v);
			}
		}
		return map;
	}
	public List<IcbalanceVO> queryLastBanlanceVOs_byList1(String currentenddate,String pk_corp,String pk_invtory,String pk_invclass,boolean isafternonn)throws  DZFWarpException{
		String lastdate = getBalanceDate(pk_corp,currentenddate);
		List<IcbalanceVO> ancevos = queryLastBanlanceVOs_byList2(lastdate,currentenddate,pk_corp,pk_invtory,pk_invclass,isafternonn);
		return ancevos;
	}
	
	
	private List<IcbalanceVO> queryLastBanlanceVOs_byList2(String lastdate,String currentenddate,String pk_corp,String pk_invtory,String pk_invclass,boolean isafternonn)throws  DZFWarpException{
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(ncost) ncost,sum(nymny) nymny,pk_inventory,inventorycode,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname,pk_subjectcode from ( ");
		sf.append("  select yy.pk_inventory,yy.code inventorycode,yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,ct.accountcode pk_subjectcode,nnum,ncost,nymny from ynt_icbalance bal ");
		sf.append("   join ynt_inventory yy on bal.pk_inventory = yy.pk_inventory   ");
		sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
		sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
		sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
		sf.append("  where bal.dbilldate = ? and bal.pk_corp = ? and nvl(bal.dr,0)=0");
		sp.addParam(lastdate);
		sp.addParam(pk_corp);
		String wheInv = null;
		if (!StringUtil.isEmptyWithTrim(pk_invtory)) {
			String[] spris = pk_invtory.split(",");
			wheInv = SqlUtil.buildSqlConditionForIn(spris);
			sf.append(" and bal.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}
		String wheInvcl = null;
		if (!StringUtil.isEmptyWithTrim(pk_invclass)) {
			String[] spris =pk_invclass.split(",");
			wheInvcl = SqlUtil.buildSqlConditionForIn(spris);
			sf.append(" and cl.pk_invclassify in ( ");
			sf.append(wheInvcl);
			sf.append(" ) ");
		}
		
		sf.append("  union all ");
		sf.append("  (select yy.pk_inventory,yy.code inventorycode, yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,ct.accountcode pk_subjectcode,nnum,ncost,nymny from ynt_icbalance_view viw ");
		sf.append("   join ynt_inventory yy on viw.pk_inventory = yy.pk_inventory   ");
		sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
		sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
		sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
		if(isafternonn){//截止当天晚上
			sf.append("   where viw.dbilldate >= ? and viw.dbilldate <=? and viw.pk_corp =  ? ");
		}else{//截止当天早上
			sf.append("   where viw.dbilldate >= ? and viw.dbilldate < ? and viw.pk_corp = ? ");
		}
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		
		if (!StringUtil.isEmptyWithTrim(wheInv)) {
			sf.append(" and viw.pk_inventory in ( ");
			sf.append(wheInv);
			sf.append(" ) ");
		}
		if (!StringUtil.isEmptyWithTrim(wheInvcl)) {
			sf.append(" and cl.pk_invclassify in ( ");
			sf.append(wheInvcl);
			sf.append(" ) ");
		}
		sf.append("  )) group by pk_inventory,inventorycode,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname,pk_subjectcode ");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
	}
	
	private List<QcYeVO> queryLastBanlanceVOs_byList2NoIC(String lastdate,String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn)throws  DZFWarpException{
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select ct.pk_corp_account pk_accsubj, ct.measurename  measurename,");
		sf.append(" ct.accountname  vname, ");
		sf.append(" qc.bnqcnum, ");
		sf.append(" qc.yearqc ");
		sf.append(" from ynt_qcye qc ");
		sf.append(" join ynt_cpaccount ct ");
		sf.append("  on ct.pk_corp_account =qc.pk_accsubj ");
		sf.append("  where  qc.doperatedate = ? and qc.pk_corp = ? and nvl(qc.dr,0)=0 and ( ct.accountcode like '1403%' or ct.accountcode like '1405%') ");
		sp.addParam(lastdate);
		//sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		if(!StringUtil.isEmpty(pk_invtory)){//pk_invtory != null && !"".equals(pk_invtory)
			sf.append(" and qc.pk_accsubj = ? ");
			sp.addParam(pk_invtory);
		}
		//sf.append("  )) group by pk_inventory,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname ");
		List<QcYeVO> ancevos = (List<QcYeVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(QcYeVO.class));
		return ancevos;
	}
	
	private String getBalanceDate(String pk_corp,String currentenddate)throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append(" select max(dbilldate) dbilldate  from  ynt_icbalance where  dbilldate <= ? and pk_corp = ? and nvl(dr,0)=0 ");
		String dbilldate = (String)singleObjectBO.executeQuery(sf.toString(), sp, new ObjectProcessor());
		if(StringUtil.isEmptyWithTrim(dbilldate))
			dbilldate="1900-01-01";
		return dbilldate;
	}
	
	private String getBalanceDateNoIC(String pk_corp,String currentenddate)throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		sf.append(" select max(doperatedate) doperatedate  from  ynt_qcye qc where  doperatedate <= ? and pk_corp = ? and nvl(dr,0)=0 ");
		String dbilldate = (String)singleObjectBO.executeQuery(sf.toString(), sp, new ObjectProcessor());
		if(StringUtil.isEmptyWithTrim(dbilldate))
			dbilldate="1900-01-01";
		return dbilldate;
	}

	// 取本月的销售成本以及数量        期初+截止本月月末的入库-截止上月月末的出库  （用于月末一次加权平均结转）
	@Override
	public List<IcbalanceVO> queryLastBanlanceVOs_byList3(String currentenddate, String pk_corp, String pk_invtory,
			boolean isafternonn) throws DZFWarpException {
		String lastdate = getBalanceDate(pk_corp,currentenddate);
		List<IcbalanceVO> ancevos = queryLastBanlanceVOs_byList3(lastdate,currentenddate,pk_corp,pk_invtory,isafternonn,false);
		return ancevos;
		
	}
	
	/**
	 * 查询截止日期最新销售成本以及数量   期初+截止本月月末的入库-截止上月月末的出库  （用于月末一次加权平均结转）
	 */
	public Map<String,IcbalanceVO> queryLastBanlanceVOs_byMap3(String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn)throws  DZFWarpException{
		Map<String,IcbalanceVO> map = new java.util.HashMap<String, IcbalanceVO>();
		String lastdate = getBalanceDate(pk_corp,currentenddate);
		List<IcbalanceVO> ancevos =queryLastBanlanceVOs_byList3(lastdate,currentenddate,pk_corp,pk_invtory,isafternonn,false);
		if(ancevos != null && ancevos.size() > 0){
			for(IcbalanceVO v : ancevos){
				map.put(v.getPk_inventory(), v);
			}
		}
		return map;
	}
	
	/**
	 * 查询截止日期最新销售成本以及数量  
	 * 成本结转前：（期初结存成本+本期购入成本）/（期初结存数量+本期购入数量）；
 	 * 成本结转后：（期初结存成本+本期购入成本-本期销售结转成本）/（期初结存数量+本期购入数量-本期销售数量）；
	 */
	public Map<String,IcbalanceVO> queryLastBanlanceVOs_byMap4(String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn)throws  DZFWarpException{
		Map<String,IcbalanceVO> map = new java.util.HashMap<String, IcbalanceVO>();
		String lastdate = getBalanceDate(pk_corp,currentenddate);
		List<IcbalanceVO> ancevos =queryLastBanlanceVOs_byList3(lastdate,currentenddate,pk_corp,pk_invtory,isafternonn,true);
		if(ancevos != null && ancevos.size() > 0){
			for(IcbalanceVO v : ancevos){
				map.put(v.getPk_inventory(), v);
			}
		}
		return map;
	}
	
	private List<IcbalanceVO> queryLastBanlanceVOs_byList3(String lastdate,String currentenddate,String pk_corp,String pk_invtory,boolean isafternonn,boolean isqu)throws  DZFWarpException{
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select sum(nnum) nnum ,sum(ncost) ncost,sum(nymny) nymny,pk_inventory,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname from ( ");
		//  期初
		sf.append("  select yy.pk_inventory,yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,nnum,ncost,nymny from ynt_icbalance bal ");
		sf.append("   join ynt_inventory yy on bal.pk_inventory = yy.pk_inventory   ");
		sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
		sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
		sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
		sf.append("  where bal.dbilldate = ? and bal.pk_corp = ? and nvl(bal.dr,0)=0");
		sp.addParam(lastdate);
		sp.addParam(pk_corp);
		if(!StringUtil.isEmpty(pk_invtory)){//pk_invtory != null && !"".equals(pk_invtory)
			sf.append(" and bal.pk_inventory = ? ");
			sp.addParam(pk_invtory);
		}
		// 截止到本月底的入库
		sf.append("  union all ");
		CorpVO corp = corpService.queryByPk(pk_corp);
		boolean isChargedept = !StringUtil.isEmpty(corp.getChargedeptname()) && corp.getChargedeptname().equals("一般纳税人")
				? true : false;
		sf.append("  (select yy.pk_inventory,yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,nnum,");
		if(isChargedept){
			sf.append("case  when nvl(in1.ncost1,0)<>0 then in1.ncost1 else (CASE nvl(h1.fp_style,2) WHEN 2 THEN  in1.nymny ELSE in1.ntotaltaxmny END) end ncost,");
		}else{
			sf.append("case  when nvl(in1.ncost1,0)<>0 then in1.ncost1 else (in1.ntotaltaxmny) end ncost,");
		}
		sf.append(" nymny from ynt_ictradein in1 ");
		sf.append("  join ynt_ictrade_h h1 ");
		sf.append("   on h1.pk_ictrade_h = in1.pk_ictrade_h ");
		sf.append("   join ynt_inventory yy on in1.pk_inventory = yy.pk_inventory   ");
		sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
		sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
		sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
		if(isafternonn){//截止当天晚上
			sf.append("   where in1.dbilldate >= ? and in1.dbilldate <=? and in1.pk_corp =  ? and nvl(in1.dr,0)=0");
		}else{//截止当天早上
			sf.append("   where in1.dbilldate >= ? and in1.dbilldate < ? and in1.pk_corp = ? and nvl(in1.dr,0)=0");
		}
		sp.addParam(lastdate);
		sp.addParam(currentenddate);
		sp.addParam(pk_corp);
		if(!StringUtil.isEmpty(pk_invtory)){//pk_invtory != null && !"".equals(pk_invtory)
			sf.append(" and in.pk_inventory = ? ");
			sp.addParam(pk_invtory);
		}
		sf.append("  )");
		
		sf.append("  union all ");
		sf.append("  (select yy.pk_inventory,yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,nnum*-1 as nnum ,ncost*-1 as ncost,nymny*-1 as nymny from ynt_ictradeout out1 ");
		sf.append("   join ynt_inventory yy on out1.pk_inventory = yy.pk_inventory   ");
		sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
		sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
		sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
		sf.append("   where out1.dbilldate >= ? and out1.pk_corp =  ? and nvl(out1.dr,0)=0 ");
		sp.addParam(lastdate);
		sp.addParam(pk_corp);
		DZFDate date1 =new DZFDate(currentenddate);
		String period = DateUtils.getPeriod(date1);
		SQLParameter sp1 = new SQLParameter();
		String qmclsqlwhere = "select * from YNT_QMCL  where nvl(dr,0)=0 and pk_corp  = ? and period =  ? ";
		sp1.addParam(pk_corp);
		sp1.addParam(period);
		boolean iscbjz =false;
		List<QmclVO> qmcllist = (List<QmclVO>) singleObjectBO.executeQuery(qmclsqlwhere, sp1,
				new BeanListProcessor(QmclVO.class));
		if (qmcllist != null && qmcllist.size() > 0) {
			QmclVO clvo = qmcllist.get(0);
			if (clvo.getIscbjz() != null && clvo.getIscbjz().booleanValue()) {
				iscbjz =true;
			}
		}
		
		if(!isqu){//成本结转取数
			// 未经期末结转取截止到上月底的出库数据
			sf.append("  and out1.dbilldate < ? ");
			DZFDate startdate =DateUtils.getPeriodStartDate(period);
			sp.addParam(startdate);
		}else{//报表取成本数据
			// 已经期末结转取截止到当月出库数据
			if(iscbjz){
				sf.append("  and out1.dbilldate <= ? ");
				sp.addParam(currentenddate);
			}else{
				// 未期末结转取截止到上月底的出库数据
				sf.append("  and out1.dbilldate < ? ");
				DZFDate startdate =DateUtils.getPeriodStartDate(period);
				sp.addParam(startdate);
			}
		}
		if(!StringUtil.isEmpty(pk_invtory)){//pk_invtory != null && !"".equals(pk_invtory)
			sf.append(" and out1.pk_inventory = ? ");
			sp.addParam(pk_invtory);
		}
		sf.append("  )");
		// 本月其他出库 领料出库 以及退库
		if(isqu&&iscbjz){//报表取成本数据    因为上面已经取过 不在取本月的其他  领料  销售退回
		}else{
			sf.append("  union all ");
			sf.append("  (select yy.pk_inventory,yy.name inventoryname,yy.invspec,yy.invtype,ms.name measurename,cl.name inventorytype ,ct.accountname pk_subjectname,nnum*-1 as nnum ,ncost*-1 as ncost,nymny*-1 as nymny from ynt_ictradeout out1 ");
			sf.append("   join ynt_inventory yy on out1.pk_inventory = yy.pk_inventory   ");
			sf.append("  join ynt_ictrade_h h on out1.pk_ictrade_h = h.pk_ictrade_h ");
			sf.append(" left join ynt_measure ms on ms.pk_measure  = yy.pk_measure ");
			sf.append(" left join ynt_invclassify cl on cl.pk_invclassify =  yy.pk_invclassify ");
			sf.append(" left join ynt_cpaccount ct on ct.pk_corp_account = yy.pk_subject  ");
			sf.append("   where out1.dbilldate >= ? and out1.dbilldate <= ? and out1.pk_corp =  ? and nvl(out1.dr,0)=0 ");
			sf.append(" and nvl(h.dr,0)=0 ");
			sf.append(" and (nvl(out1.cbusitype,'46') in( ?,?,?) or  nvl(h.isback,'N')='Y') ");
			DZFDate startdate2 =DateUtils.getPeriodStartDate(DateUtils.getPeriod(date1));
			sp.addParam(startdate2);
			sp.addParam(currentenddate);
			sp.addParam(pk_corp);
			sp.addParam(IcConst.QTCTYPE);
			sp.addParam(IcConst.LLTYPE);
			sp.addParam(IcConst.CBTZTYPE);
			if(!StringUtil.isEmpty(pk_invtory)){//pk_invtory != null && !"".equals(pk_invtory)
				sf.append(" and out1.pk_inventory = ? ");
				sp.addParam(pk_invtory);
			}
			sf.append("  )");
		}
		
		sf.append(" ) group by pk_inventory,inventoryname,invspec,invtype,measurename,inventorytype,pk_subjectname ");
		List<IcbalanceVO> ancevos = (List<IcbalanceVO>)singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(IcbalanceVO.class));
		return ancevos;
	}
}

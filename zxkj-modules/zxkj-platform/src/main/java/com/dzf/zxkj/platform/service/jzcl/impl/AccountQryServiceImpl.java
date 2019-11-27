package com.dzf.zxkj.platform.service.jzcl.impl;

import com.dzf.zxkj.base.dao.MultBodyObjectBO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZFNumberUtil;
import com.dzf.zxkj.common.constant.IRoleCodeCont;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.IncomeHistoryVo;
import com.dzf.zxkj.platform.model.bdset.IncomeWarningVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.AccountQryVO;
import com.dzf.zxkj.platform.model.report.VoucherFseQryVO;
import com.dzf.zxkj.platform.model.sys.JMUserRoleVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.bdset.IIncomeWarningService;
import com.dzf.zxkj.platform.service.jzcl.IAccountQryService;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.vo.QrySqlSpmVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service("accountqryser")
@Slf4j
@SuppressWarnings("all")
public class AccountQryServiceImpl implements IAccountQryService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private MultBodyObjectBO multBodyObjectBO;
	
	@Autowired
	private IIncomeWarningService incomewarningserv;

	@Override
	public Integer queryTotalRow(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException {
		QrySqlSpmVO qryvo = getCorpInfo(pamvo, uvo, true, ischannel);
		return multBodyObjectBO.getDataTotal(qryvo.getSql(), qryvo.getSpm());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccountQryVO> query(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException {
		QrySqlSpmVO qryvo = getCorpInfo(pamvo, uvo, false, ischannel);
		List<AccountQryVO> list = (List<AccountQryVO>) multBodyObjectBO.queryDataPage(AccountQryVO.class, qryvo.getSql(), qryvo.getSpm(), 
				pamvo.getPage(), pamvo.getRows(), null);
		if(list != null && list.size() > 0){
			List<String> pklist = new ArrayList<String>();
			for(AccountQryVO avo : list){
				pklist.add(avo.getPk_corp());
			}
			String[] pks = null;
			if(pklist != null && pklist.size() > 0){
				pks = pklist.toArray(new String[0]);
			}else{
				return null;
			}
			return getReturnData(pamvo, pks, ischannel, uvo);
		}
		return list;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AccountQryVO> queryAllData(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		int qrytype = -1;
		//本节点查询为100，外节点联查为非100
		if(pamvo.getQrytype() != null && pamvo.getQrytype() != 100){
			qrytype = 1;//查询月与当前月不相等
		}else{
			qrytype = 1;//查询月与当前月不相等
			String nperiod = DateUtils.getPeriod(new DZFDate());
			if (nperiod.equals(pamvo.getQjq())) {
				qrytype = 2;//查询月与当前月相等
			}
		}
		
		//0、获取全部数据
		getAllColumn(sql, ischannel);
		//1、获取查询列
		getQryColumn(sql, spm, qrytype, pamvo, ischannel);
		//2、查询客户相关信息
		getCorpSqlSpm(pamvo, null, sql, spm, ischannel, uvo);
		//3、查询记账状态
		getJzztSqlSpm(pamvo, null, sql, spm, qrytype, uvo, ischannel);
		//4、账务检查状态
		getZwjcSqlSpm(pamvo, null, sql, spm, uvo, ischannel);
		//5、凭证审核状态
		getPzshSqlSpm(pamvo, null, sql, spm, uvo, ischannel);
		//6、获取全部数据过滤条件
		getAllFilter(sql, spm, pamvo, ischannel);
		List<AccountQryVO> rlist = (List<AccountQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountQryVO.class));
		if(rlist != null && rlist.size() > 0){
			return getRetAllData(rlist, pamvo, uvo, ischannel);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private List<AccountQryVO> getReturnData(QueryParamVO pamvo, String[] pks, boolean ischannel, UserVO uvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		
		int qrytype = 1;//查询月与当前月不相等
		String nperiod = DateUtils.getPeriod(new DZFDate());
		if (nperiod.equals(pamvo.getQjq())) {
			qrytype = 2;//查询月与当前月相等
		}
		
		//1、获取查询列
		getQryColumn(sql, spm, qrytype, pamvo, ischannel);
		//2、查询客户相关信息
		getCorpSqlSpm(pamvo, pks, sql, spm, ischannel, uvo);
		//3、查询记账状态
		getJzztSqlSpm(pamvo, pks, sql, spm, qrytype, uvo, ischannel);
		//4、账务检查状态
		getZwjcSqlSpm(pamvo, pks, sql, spm, uvo, ischannel);
		//5、凭证审核状态
		getPzshSqlSpm(pamvo, pks, sql, spm, uvo, ischannel);
		sql.append(" ORDER BY qcorp.khcode ");
		List<AccountQryVO> rlist = (List<AccountQryVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(AccountQryVO.class));
		if(rlist != null && rlist.size() > 0){
			setShowName(rlist, pamvo, ischannel, uvo);
		}
		return rlist;
	}
	
	/**
	 * 获取客户对应的记账会计
	 * @param pamvo
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, StringBuffer> queryJzkjMap(QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException {
		Map<String, StringBuffer> map = new HashMap<String, StringBuffer>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("select distinct corp.pk_corp,  ");
		sql.append("                us.cuserid,  ");
		sql.append("                us.user_name,  ");
		sql.append("                sr.role_code as rolecode  ");
		sql.append("  from bd_corp corp  ");
		sql.append("  join sm_user_corp uc on uc.pk_corpk = corp.pk_corp  ");
		sql.append("  join sm_user us on us.cuserid = uc.cuserid  ");
		sql.append("  join sm_userole ur on ur.cuserid = us.cuserid  ");
		sql.append("  join sm_role sr on sr.pk_role = ur.pk_role and sr.role_code = ? ");
		spm.addParam(IRoleCodeCont.jms08);
		sql.append(" where corp.fathercorp = ?  ");
		spm.addParam(pamvo.getFathercorp());

		addSqlParam(pamvo, sql, spm, uvo, ischannel);

		sql.append("   and nvl(us.dr, 0) = 0  ");
		sql.append("   and nvl(us.locked_tag, 'N') = 'N'  ");
		sql.append("   and nvl(ur.dr, 0) = 0  ");
		List<JMUserRoleVO> list = (List<JMUserRoleVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(JMUserRoleVO.class));
		StringBuffer value = null;
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[] { "user_name" }, list, 1);
			for (JMUserRoleVO urvo : list) {
				if (!map.containsKey(urvo.getPk_corp())) {
					value = new StringBuffer();
					value.append(urvo.getUser_name());
					map.put(urvo.getPk_corp(), value);
				} else {
					value = map.get(urvo.getPk_corp());
					value.append("，").append(urvo.getUser_name());
					map.put(urvo.getPk_corp(), value);
				}
			}
		}
		return map;
	}
	
	/**
	 * 追加查询条件
	 * @param pamvo
	 * @param sql
	 * @param spm
	 * @param uvo
	 * @throws DZFWarpException
	 */
	private void addSqlParam(QueryParamVO pamvo, StringBuffer sql, SQLParameter spm, UserVO uvo, boolean ischannel) throws DZFWarpException {
        sql.append(" and nvl(corp.dr,0) = 0   ");
        sql.append(" and nvl(corp.isaccountcorp, 'N') = 'N'   ");
        sql.append(" and nvl(corp.isseal,'N') = 'N' ");
        sql.append(" and corp.fathercorp = ?   ");
        spm.addParam(pamvo.getFathercorp());
        //建账日期
        if(pamvo.getBegindate1() != null && pamvo.getEnddate() != null){
            sql.append(" and corp.begindate >= ? and corp.begindate <= ? ");
            spm.addParam(pamvo.getBegindate1());
            spm.addParam(pamvo.getEnddate());
        }else if(pamvo.getBegindate1() != null){
            sql.append(" and corp.begindate >= ?  ");
            spm.addParam(pamvo.getBegindate1());
        }else if(pamvo.getEnddate() != null){
            sql.append(" and corp.begindate <= ?  ");
            spm.addParam(pamvo.getEnddate());
        }
        //纳税人资格
        if (pamvo.getLevelq() == 0) {
            sql.append(" and corp.chargedeptname= '一般纳税人' ");
        } else if (pamvo.getLevelq() == 1) {
            sql.append(" and corp.chargedeptname= '小规模纳税人' ");
        }
        sql.append(" and  exists (select pk_corp from sm_user_role ur ");
		sql.append("               where corp.pk_corp = ur.pk_corp and nvl(ur.dr,0) = 0 and ur.cuserid = ? ) ");
		spm.addParam(uvo.getCuserid());
		//记账会计查询
		if (!StringUtil.isEmpty(pamvo.getUserid())) {
			if(ischannel){//是否加盟商
				sql.append(" and  exists (select pk_corp from sm_user_role ur ");
				sql.append("               where p.pk_corp = ur.pk_corp and nvl(ur.dr,0) = 0  ");
				sql.append("                 and ur.cuserid = ? and ur.pk_role = ? )");
				spm.addParam(uvo.getCuserid());
				spm.addParam(pamvo.getUserid());
				spm.addParam(IRoleCodeCont.jms08_ID);
			}else{
				sql.append(" and corp.vsuperaccount = ? ");
				spm.addParam(pamvo.getUserid());
			}
		}
	}
	
	/**
	 * 设置显示名称
	 * @param rlist
	 * @param pamvo
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void setShowName(List<AccountQryVO> rlist, QueryParamVO pamvo, boolean ischannel, UserVO uvo) throws DZFWarpException {
		Map<String, StringBuffer> umap = null;
		if(ischannel){
			umap = queryJzkjMap(pamvo, uvo, ischannel);
		}
		if(ischannel){
			StringBuffer accer = null;
			for(AccountQryVO avo : rlist){
				QueryDeCodeUtils.decKeyUtil(new String[]{"khName"}, avo, 1);
				if(umap != null && !umap.isEmpty()){
					accer = umap.get(avo.getPk_corp());
					if(accer != null && accer.length() > 0){
						avo.setPcountname(accer.toString());// 记账会计
					}
				}
			}
		}else{
			QueryDeCodeUtils.decKeyUtils(new String[]{"khName", "pcountname"}, rlist, 1);
		}
	}
	
	/**
	 * 凭证审核状态
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getPzshSqlSpm(QueryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm, UserVO uvo, boolean ischannel)
			throws DZFWarpException {
		sql.append("LEFT JOIN ( ");
		sql.append("SELECT h.pk_corp AS pk_corp,  ");
		sql.append("       COUNT(h.pk_tzpz_h) AS snum,  ");
		sql.append("       SUM(CASE h.vbillstatus  ");
		sql.append("             WHEN 8 THEN  ");
		sql.append("              1  ");
		sql.append("             ELSE  ");
		sql.append("              0  ");
		sql.append("           END) AS whnum  ");
		sql.append("  FROM ynt_tzpz_h h  ");
		sql.append("  JOIN bd_corp corp ON h.pk_corp = corp.pk_corp  ");
		sql.append(" WHERE nvl(h.dr, 0) = 0  ");
		sql.append("   AND h.vbillstatus IN (1, 8)  ");
		sql.append("   AND h.period = ?  ");
		spm.addParam(pamvo.getQjq());
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" h.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm, uvo, ischannel);
		}
		sql.append(" GROUP BY h.pk_corp  ) pzsh ");
		sql.append(" ON qcorp.pk_corp = pzsh.pk_corp ");
	}
	
	/**
	 * 查询账务检查状态
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @throws DZFWarpException
	 */
	private void getZwjcSqlSpm(QueryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm, UserVO uvo, boolean ischannel)
			throws DZFWarpException {
		sql.append(" LEFT JOIN ( ");
		sql.append("SELECT q.pk_corp, q.period  ");
		sql.append("  FROM ynt_qmcl q  ");
		sql.append("  JOIN bd_corp corp on corp.pk_corp = q.pk_corp  ");
		sql.append(" WHERE q.isgz = 'Y'  ");
		sql.append("   AND q.period = ?  ");
		spm.addParam(pamvo.getQjq());
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" q.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm, uvo, ischannel);
		}
		sql.append(" )  zwjc ");
		sql.append(" ON qcorp.pk_corp = zwjc.pk_corp ");
	}
	
	/**
	 * 查询记账状态
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @param period
	 * @param qrytype
	 * @throws DZFWarpException
	 */
	private void getJzztSqlSpm(QueryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm, int qrytype,
			UserVO uvo, boolean ischannel) throws DZFWarpException {
		sql.append(" LEFT JOIN ( ");
		sql.append("SELECT q.pk_corp,   ");
		if (qrytype == 2) {
			sql.append(" max(q.period) as period ");
		} else {
			sql.append(" q.period as period ");
		}
		sql.append("  FROM ynt_qmcl q  ");
		sql.append("  JOIN bd_corp corp ON corp.pk_corp = q.pk_corp  ");
		sql.append(" WHERE q.isqjsyjz = 'Y' ");
		if (qrytype == 2) {
			sql.append(" AND q.period <= ? ");
		} else {
			sql.append(" AND q.period = ? ");
		}
		spm.addParam(pamvo.getQjq());
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" q.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm, uvo, ischannel);
		}
		if (qrytype == 2) {
			sql.append(" GROUP BY q.pk_corp ");
		}
		sql.append("  ) jzzt ");
		sql.append(" ON qcorp.pk_corp = jzzt.pk_corp  ");
	}
	
	/**
	 * 查询客户相关信息
	 * @param pamvo
	 * @param pks
	 * @param sql
	 * @param spm
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void getCorpSqlSpm(QueryParamVO pamvo, String[] pks, StringBuffer sql, SQLParameter spm, boolean ischannel,
			UserVO uvo) throws DZFWarpException {
		sql.append("(SELECT corp.pk_corp        as pk_corp,  ");
		sql.append("       corp.innercode      as khcode,  ");
		sql.append("       corp.unitname       as khname,  ");
		sql.append("       corp.chargedeptname as khTaxType,  ");
		sql.append("       corp.ishasaccount   as ishasaccount,  ");
		if (!ischannel) {
			sql.append("       r.user_name   as pcountname,  ");
		}
		sql.append("       corp.begindate      as period  ");
		sql.append("  FROM bd_corp corp  ");
		if (!ischannel) {
			sql.append(" LEFT JOIN sm_user r ON corp.vsuperaccount = r.cuserid ");
		}
		sql.append(" WHERE corp.fathercorp = ?  ");
		spm.addParam(pamvo.getFathercorp());
		if (pks != null && pks.length > 0) {
			String where = SqlUtil.buildSqlForIn(" corp.pk_corp ", pks);
			sql.append(" AND ").append(where);
		} else {
			// 添加过滤查询条件
			addSqlParam(pamvo, sql, spm, uvo, ischannel);
		}
		sql.append(" ) qcorp ");
	}
	
	/**
	 * 获取查询列
	 * @param sql
	 * @param spm
	 * @param qrytype
	 * @param pamvo
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void getQryColumn(StringBuffer sql, SQLParameter spm, int qrytype, QueryParamVO pamvo, boolean ischannel) throws DZFWarpException {
		sql.append("SELECT qcorp.pk_corp,  ");
		sql.append("       qcorp.khcode,  ");
		sql.append("       qcorp.khname,  ");
		sql.append("       qcorp.khTaxType,  ");
		sql.append("       qcorp.period,  ");
		if(!ischannel){
			sql.append("       qcorp.pcountname,  ");
		}
		//记账状态
		sql.append("       CASE WHEN jzzt.period IS NOT NULL THEN  ");
		sql.append("            jzzt.period || '已完成' ");
		if(qrytype == 2){
			//查询月与服务器月份相同，查询最早未损益结转月份
			sql.append("            WHEN jzzt.period IS NULL AND qcorp.period IS NOT NULL THEN ");
			sql.append("            substr(qcorp.period, 0, 7) || '未完成' ");
		}else if(qrytype == 1){
			sql.append("            WHEN jzzt.period IS NULL AND substr(qcorp.period,0,7) <= ? THEN ");
			spm.addParam(pamvo.getQjq());
			sql.append("'").append(pamvo.getQjq()).append("'").append(" || '未完成'");
		}
		sql.append("       END AS jzstatus, ");
		//账务检查
		sql.append("       CASE WHEN zwjc.period IS NOT NULL THEN  ");
		sql.append("            zwjc.period || '已关账' ");
		sql.append("            ELSE '' END AS vcheckstatus ");
		//凭证审核
		sql.append("       ,CASE WHEN pzsh.snum IS NULL THEN '' ");
		sql.append("            WHEN pzsh.snum IS NOT NULL AND nvl(pzsh.whnum,0) != 0 THEN '未完成' ");
		sql.append("            ELSE '已完成'  ");
		sql.append("       END AS vauditstatus ");
		
		sql.append(" FROM ");
	}
	
	/**
	 * 获取客户过滤条件
	 * @param pamvo
	 * @param uvo
	 * @param isqrynum
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getCorpInfo(QueryParamVO pamvo, UserVO uvo, boolean isqrynum, boolean ischannel) throws DZFWarpException {
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if(isqrynum){
			sql.append("SELECT COUNT(p.pk_corp) ");
		}else{
			sql.append("SELECT p.pk_corp as pk_corp  ") ; 
		}
		sql.append("  FROM bd_corp p  ");
		sql.append(" WHERE nvl(p.dr,0) = 0   ");
		sql.append("   AND nvl(p.isaccountcorp, 'N') = 'N'   ");
		sql.append("   AND nvl(p.isseal,'N') = 'N' ");
		sql.append("   AND p.fathercorp = ? ");
		spm.addParam(pamvo.getFathercorp());
		//记账日期查询
		if(pamvo.getBegindate1() != null && pamvo.getEnddate() != null){
            sql.append(" and p.begindate >= ? and p.begindate <= ? ");
            spm.addParam(pamvo.getBegindate1());
            spm.addParam(pamvo.getEnddate());
        }else if(pamvo.getBegindate1() != null){
		    sql.append(" AND p.begindate >= ?  ");
            spm.addParam(pamvo.getBegindate1());
		}else if(pamvo.getEnddate() != null){
            sql.append(" AND p.begindate <= ?  ");
            spm.addParam(pamvo.getEnddate());
        }
		//纳税人资格
		if (pamvo.getLevelq() != null && pamvo.getLevelq() == 0) {
			sql.append(" AND p.chargedeptname= '一般纳税人' ");
		} else if (pamvo.getLevelq() != null && pamvo.getLevelq() == 1) {
			sql.append(" AND p.chargedeptname= '小规模纳税人' ");
		}
		//记账会计查询
		if (!StringUtil.isEmpty(pamvo.getUserid())) {
			if(ischannel){//是否加盟商
				sql.append(" and  exists (select pk_corp from sm_user_role ur ");
				sql.append("               where p.pk_corp = ur.pk_corp and nvl(ur.dr,0) = 0  ");
				sql.append("                 and ur.cuserid = ? and ur.pk_role = ? )");
				spm.addParam(pamvo.getUserid());
				spm.addParam(IRoleCodeCont.jms08_ID);
			}else{
				sql.append(" and p.vsuperaccount = ? ");
				spm.addParam(pamvo.getUserid());
			}
		}
		sql.append(" and  exists (select pk_corp from sm_user_role ur ");
		sql.append("               where p.pk_corp = ur.pk_corp and nvl(ur.dr,0) = 0 and ur.cuserid = ? ) ");
		spm.addParam(uvo.getCuserid());
		sql.append(" order by p.innercode ");
		qryvo.setSpm(spm);
		qryvo.setSql(sql.toString());
		return qryvo;
	}
	
	/**
	 * 设置显示名称
	 * @param rlist
	 * @throws DZFWarpException
	 */
	private List<AccountQryVO> getRetAllData(List<AccountQryVO> rlist,QueryParamVO pamvo, UserVO uvo, boolean ischannel) throws DZFWarpException {
		Map<String, StringBuffer> umap = queryJzkjMap(pamvo, uvo, ischannel);
		List<AccountQryVO> retlist = new ArrayList<AccountQryVO>();
		StringBuffer accer = null;
		for(AccountQryVO avo : rlist){
			if(ischannel){
				QueryDeCodeUtils.decKeyUtil(new String[]{"khName"}, avo, 1);
			}else{
				QueryDeCodeUtils.decKeyUtil(new String[]{"khName", "pcountname"}, avo, 1);
			}
			if(!StringUtil.isEmpty(pamvo.getCorpname())){
				if((!StringUtil.isEmpty(avo.getKhCode()) && avo.getKhCode().indexOf(pamvo.getCorpname()) != -1) 
						|| (!StringUtil.isEmpty(avo.getKhName()) && avo.getKhName().indexOf(pamvo.getCorpname()) != -1)){
					retlist.add(avo);
				}else{
					continue;
				}
			}
			if(umap != null && !umap.isEmpty()){
				accer = umap.get(avo.getPk_corp());
				if(accer != null && accer.length() > 0){
					avo.setPcountname(accer.toString());// 记账会计
				}
			}
		}
		if(!StringUtil.isEmpty(pamvo.getCorpname())){
			return retlist;
		}
		return rlist;
	}
	
	/**
	 * 获取全部数据
	 * @param sql
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void getAllColumn(StringBuffer sql, boolean ischannel) throws DZFWarpException {
		sql.append("SELECT d_all.pk_corp,  ");
		sql.append("       d_all.khcode,  ");
		sql.append("       d_all.khname,  ");
		sql.append("       d_all.khTaxType,  ");
		sql.append("       d_all.period,  ");
		if(!ischannel){
			sql.append("       d_all.pcountname,  ");
		}
		//记账状态
		sql.append("       d_all.jzstatus, ");
		//账务检查
		sql.append("       d_all.vcheckstatus ");
		sql.append("       ,d_all.vauditstatus ");
		//凭证审核
		sql.append(" FROM  ( ");
	}
	
	/**
	 * 获取全部数据过滤条件
	 * @param sql
	 * @param spm
	 * @param pamvo
	 * @param ischannel
	 * @throws DZFWarpException
	 */
	private void getAllFilter(StringBuffer sql, SQLParameter spm, QueryParamVO pamvo, boolean ischannel) throws DZFWarpException {
		sql.append(" ) d_all ");
		sql.append(" WHERE d_all.pk_corp is not null ");
//		//记账状态
//		if(!"全部".equals(pamvo.getAsname())){
//			sql.append(" AND d_all.jzstatus like ? ");
//			spm.addParam("%"+pamvo.getAsname()+"%");
//		}
//		//账务检查
//		if(!"全部".equals(pamvo.getZccode())){
//			if("已关账".equals(pamvo.getZccode())){
//				sql.append(" AND d_all.vcheckstatus like ? ");
//				spm.addParam("%"+pamvo.getZccode()+"%");
//			}else{
//				sql.append(" AND d_all.vcheckstatus IS NULL ");
//			}
//		}
//		if(ischannel){
//			//凭证审核
//			if(!"全部".equals(pamvo.getZcsx())){
//				if("已完成".equals(pamvo.getZcsx())){
//					sql.append(" AND d_all.vauditstatus = ? ");
//					spm.addParam(pamvo.getZcsx());
//				}else{
//					sql.append(" AND d_all.vauditstatus IS NULL ");
//				}
//			}
//		}
		sql.append(" order by d_all.khcode ");
	}

	@Override
	public List<AccountQryVO> queryYjxxByMulti(List<AccountQryVO> accountQryVOList, String period) throws DZFWarpException {
		accountQryVOList = queryYjxx(accountQryVOList, period);
		if(accountQryVOList == null || accountQryVOList.size() == 0)
			return null;
		
		ExecutorService pool = null;
		try {
			int maxcount = 100;
			if (accountQryVOList.size() <= 100) {
				maxcount = accountQryVOList.size();
			}
			DZFDate endDate = new DZFDate(period + "-01");
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(endDate.toDate());
			int mon = calendar.get(Calendar.MONTH);
			int quarter = mon / 3 + 1;
			int endMon = 3 * quarter - 1;
			int beginMon = endMon - 2;
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, beginMon);
			DZFDate beginDate = new DZFDate(calendar.getTime());
			calendar.set(Calendar.MONTH, endMon);
			endDate = new DZFDate(calendar.getTime());
			String beginperiod = DateUtils.getPeriod(beginDate);
			String endperiod = DateUtils.getPeriod(endDate);
			
			pool = Executors.newFixedThreadPool(maxcount);
			List<Future<List<AccountQryVO>>> vc = new Vector<Future<List<AccountQryVO>>>();
			for (AccountQryVO vo : accountQryVOList) {
				if(StringUtil.isEmptyWithTrim(vo.getPeriod())){
					continue;
				}
				Future<List<AccountQryVO>> future = pool.submit(new AccountQryData(vo, beginperiod, endperiod));
				vc.add(future);
			}
			// 默认执行 线程池操作结果，等待本组数据执行完成
			for (Future<List<AccountQryVO>> fu : vc) {
				fu.get();//不需要往list重新设值了
			}
			pool.shutdown();
		} catch (Exception e) {
			log.error("错误",e);
		} finally {
			try {
				if (pool != null) {
					pool.shutdown();
				}
			} catch (Exception e) {
			}
		}
		
		return accountQryVOList;
		
	}
	
	private class AccountQryData implements Callable<List<AccountQryVO>>{
		private AccountQryVO data;
		private String beginperiod;
		private String endperiod;
		public AccountQryData(AccountQryVO data, String beginperiod, String endperiod){
			this.data = data;
			this.beginperiod = beginperiod;
			this.endperiod = endperiod;
		}
		@Override
		public List<AccountQryVO> call() throws Exception {
			queryYjBySpe(data, beginperiod, endperiod);
			return null;
		}
		
		
	}
	
	private void queryYjBySpe(AccountQryVO vo, String beginperiod, String endperiod){
		IncomeWarningVO[] warnvos = incomewarningserv.query(vo.getPk_corp());
		if(warnvos == null || warnvos.length == 0)
			return;
		List<IncomeWarningVO> warnList = new ArrayList<IncomeWarningVO>();
		for(IncomeWarningVO warnvo : warnvos){
			if(warnvo.getSpeflg() != null && warnvo.getSpeflg().booleanValue()){
				warnList.add(warnvo);
			}
		}
		
		if(warnList.size() == 0)
			return;
		
		String xinxi = vo.getYjxx();
		for(IncomeWarningVO incomeWarningVO : warnList){
			DZFDouble fs = incomewarningserv.getSpecFsValue(beginperiod, endperiod, vo.getPk_corp(), incomeWarningVO);
			
			if(fs != null && fs.doubleValue() >= incomeWarningVO.getSrsx().doubleValue()){
				if(StringUtil.isEmpty(xinxi)){
					xinxi = incomeWarningVO.getXmmc()+"("+translate(incomeWarningVO.getPeriod_type())+")已达预警上线";
				}else{
					xinxi += "#" + incomeWarningVO.getXmmc()+"("+translate(incomeWarningVO.getPeriod_type())+")已达预警上线";
				}
				continue;
			}
			
			if(fs != null && fs.doubleValue() > incomeWarningVO.getYjz().doubleValue()){
				if(StringUtil.isEmpty(xinxi)){
					xinxi = incomeWarningVO.getXmmc()+"("+translate(incomeWarningVO.getPeriod_type())+")已达预警上线";
				}else{
					xinxi += "#" + incomeWarningVO.getXmmc()+"("+translate(incomeWarningVO.getPeriod_type())+")已达预警上线";
				}
				continue;
			}
		}

		vo.setYjxx(xinxi);
	}
	
	@Override
	public List<AccountQryVO> queryYjxx(List<AccountQryVO> accountQryVOList, String period) throws DZFWarpException {
		//需要查询的公司
		String[] pk_corpArr = new String[accountQryVOList.size()];
		//查询区间  例如：period为 2018-03 查询2017-04至2018-12月份的数据
		String previousYearPeriod = DateUtils.getPreviousYearPeriod(period);
		String endPeriod = Integer.toString(new DZFDate(period + "-01").getYear())+"-12"; ///1
		//过滤历史发生额 增加预警值的维护历史发生额数据
		List<String> pkCorpIncomeHistory = new ArrayList<>();
		for(int i = 0; i < accountQryVOList.size(); i++){
			pk_corpArr[i] = accountQryVOList.get(i).getPk_corp();
			//做账日期如果不再查询时间区间内 就剔除
			if(StringUtil.isEmpty(accountQryVOList.get(i).getPeriod())  || new DZFDate(previousYearPeriod+"-01").before(new DZFDate(accountQryVOList.get(i).getPeriod()))){
				pkCorpIncomeHistory.add(pk_corpArr[i]);
			}
		}
		//查询公司预警条目
		IncomeWarningVO[] incomeWarningVOS = (IncomeWarningVO[]) singleObjectBO.queryByCondition(IncomeWarningVO.class,
				" pk_corp in ( " + SqlUtil.buildSqlConditionForIn(pk_corpArr) + " ) and nvl(speflg,'N')='N' and nvl(dr,0) = 0 order by period_type desc", null);
		//无预警条目直接返回
		if(incomeWarningVOS.length == 0){
			return accountQryVOList;
		}
		//进一步过滤数据集 不在预警条目中的 不查询发生额
		Set<String> accsubjSet = splitAccsubj(incomeWarningVOS);

		//查询科目主键对应的科目编码 注：预警条目里的kmbm不能使用 存在空值
		YntCpaccountVO[] yntCpaccountVOS = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				" PK_CORP_ACCOUNT in ( " + SqlUtil.buildSqlConditionForIn(accsubjSet.toArray(new String[accsubjSet.size()])) + " )  and nvl(dr,0) = 0", null);
		//构建科目主键和编码的映射关系
		Map<String,String> codeMap = new HashMap();
		for(YntCpaccountVO yntCpaccountVO : yntCpaccountVOS){
			codeMap.put(yntCpaccountVO.getPk_corp_account(), yntCpaccountVO.getAccountcode());
		}
		//构建sql where条件 REGEXP_LIKE
		String codeLikeStr = createRegexpLikeCondition(new ArrayList(codeMap.values()));
		//查询发生额
		List<VoucherFseQryVO> voucherFseQryVOS = queryFseByCorpAndKmbmAndPeriod(previousYearPeriod, endPeriod, pk_corpArr, codeLikeStr);
		//预警信息中的历史发生金额
		Map<String, DZFDouble> incomeHistoryVoMap = queryIncomeHistoryMap(pkCorpIncomeHistory);
		//计算发生额
		Map<String,Map<String, DZFDouble>> fseMap = calculateFse(incomeWarningVOS, voucherFseQryVOS, codeMap);
		//遍历预警信息 生成预警提示
		Map<String,String> yjxxMap = createYjxx(period, incomeWarningVOS, incomeHistoryVoMap, fseMap, codeMap);
		return fillData(yjxxMap, accountQryVOList);
	}
	/**
	 *
	 * @param yjxxMap 预警信息集合
	 * @param accountQryVOList 客户公司集合
	 * @return
	 */
	private List<AccountQryVO>  fillData(Map<String, String> yjxxMap, List<AccountQryVO> accountQryVOList) {
		//赋给返回集合
		for(AccountQryVO accountQryVO : accountQryVOList){
			if(yjxxMap.containsKey(accountQryVO.getPk_corp())){
				accountQryVO.setYjxx(yjxxMap.get(accountQryVO.getPk_corp()));
			}
		}
		return accountQryVOList;
	}

	/**
	 * @param period
	 * @param incomeWarningVOS
	 * @param incomeHistoryVoMap
	 * @param fseMap
	 * @param codeMap
	 * @return
	 * 生成预警信息
	 */
	private Map<String, String> createYjxx(String period, IncomeWarningVO[] incomeWarningVOS, Map<String, DZFDouble> incomeHistoryVoMap, Map<String, Map<String, DZFDouble>> fseMap, Map<String, String> codeMap) {
		Map<String,String> yjxxMap = new HashMap();
		for(IncomeWarningVO incomeWarningVO : incomeWarningVOS){
			Integer period_type = incomeWarningVO.getPeriod_type() == null ? 3 : incomeWarningVO.getPeriod_type();
			DZFDouble fs = DZFDouble.ZERO_DBL;
			String[] periods = getPeriodRange(period, period_type);
			String pk_corp = incomeWarningVO.getPk_corp();


			for(String thisPriod : periods){
				String k = incomeWarningVO.getPk_sryj()+"_"+thisPriod;
				if(incomeHistoryVoMap.containsKey(k)){
					fs = SafeCompute.add(fs, incomeHistoryVoMap.get(k));
				}
			}
			//科目主键
			String pk_accsubj = incomeWarningVO.getPk_accsubj();
			if(!StringUtil.isEmpty(pk_accsubj)){
				for(String accsubj : pk_accsubj.split(",")){
					if(codeMap.containsKey(accsubj)){
						Map<String, DZFDouble> fse = fseMap.get(pk_corp+"_"+codeMap.get(accsubj));
						if(fse == null || fse.size() == 0){
							continue;
						}
						for(String thisPriod : periods){
							if(DZFNumberUtil.isNotNullAndNotZero(fse.get(thisPriod))){
								fs = SafeCompute.add(fs, fse.get(thisPriod));
							}
						}
					}
				}
			}

			if(fs.doubleValue() >= incomeWarningVO.getSrsx().doubleValue()){
				if(yjxxMap.containsKey(incomeWarningVO.getPk_corp())){
					yjxxMap.put(pk_corp,yjxxMap.get(pk_corp) +"#"+ incomeWarningVO.getXmmc()+"("+translate(period_type)+")已达预警上线");
				}else{
					yjxxMap.put(incomeWarningVO.getPk_corp(),incomeWarningVO.getXmmc()+"("+translate(period_type)+")已达预警上线");
				}
				continue;
			}

			if(fs.doubleValue() > incomeWarningVO.getYjz().doubleValue()){
				if(yjxxMap.containsKey(incomeWarningVO.getPk_corp())){
					yjxxMap.put(pk_corp,yjxxMap.get(pk_corp) +"#"+ incomeWarningVO.getXmmc()+"("+translate(period_type)+")超过预警值"+subZeroAndDot(fs.sub(incomeWarningVO.getYjz()).toString())+"元,请留意");
				}else{
					yjxxMap.put(incomeWarningVO.getPk_corp(),incomeWarningVO.getXmmc()+"("+translate(period_type)+")超过预警值"+subZeroAndDot(fs.sub(incomeWarningVO.getYjz()).toString())+"元");
				}
				continue;
			}
		}
		return yjxxMap;
	}
	/**
	 *
	 * @param incomeWarningVOS 预警项集合
	 * @param voucherFseQryVOS 凭证发生额集合
	 * @param kmbmMap  科目主键和编码映射
	 * @return  [公司编码+科目编码 : [期间 : 发生额]]
	 * @description 计算发生额
	 */
	private Map<String, Map<String, DZFDouble>> calculateFse(IncomeWarningVO[] incomeWarningVOS, List<VoucherFseQryVO> voucherFseQryVOS, Map kmbmMap) {

		Map<String,Map<String, DZFDouble>> fseMap = new HashMap<>();
		//判断空集合
		if(DZFArrayUtil.isEmpty(incomeWarningVOS) || DZFCollectionUtil.isEmpty(voucherFseQryVOS) || DZFMapUtil.isEmpty(kmbmMap)){
			return fseMap;
		}

		//初始化发生额信息   -> period : 发生额
		String accs;
		for(IncomeWarningVO incomeWarningVO : incomeWarningVOS){
			String pk_corp = incomeWarningVO.getPk_corp();
			accs = incomeWarningVO.getPk_accsubj();
			if(StringUtil.isEmpty(accs))
				continue;
			for(String pk_accsubj : accs.split(",")){
				if(kmbmMap.containsKey(pk_accsubj)){
					fseMap.put(pk_corp+"_"+ kmbmMap.get(pk_accsubj), new HashMap<String, DZFDouble>());
				}
			}
		}
		//遍历各科目的发生额填充fseMap
		for (Map.Entry<String, Map<String, DZFDouble>> entry : fseMap.entrySet()) {
			String key = entry.getKey();
			Map<String, DZFDouble> value = entry.getValue();
			for(VoucherFseQryVO voucherFseQryVO : voucherFseQryVOS){
				//模糊匹配key
				if((voucherFseQryVO.getPk_corp() +"_"+voucherFseQryVO.getVcode()).startsWith(key)){
					String period_fse = voucherFseQryVO.getPeriod();
					if(value.containsKey(period_fse)){
						value.get(period_fse).add(voucherFseQryVO.getMny());
					}else{
						value.put(period_fse, voucherFseQryVO.getMny());
					}
				}
			}
		}
		return fseMap;
	}

	/**
	 * @param pkCorpIncomeHistory 公司编码集合
	 * @return [主键+期间 : 发生额]
	 * @description 查询历史发生额
	 */
	private Map<String, DZFDouble> queryIncomeHistoryMap(List<String> pkCorpIncomeHistory) {

		//构建主键+period和发生额的映射
		Map<String, DZFDouble> incomeHistoryVoMap = new HashMap();

		if(DZFCollectionUtil.isEmpty(pkCorpIncomeHistory)){
			return incomeHistoryVoMap;
		}

		//预警信息中的历史发生金额
		IncomeHistoryVo[] incomeHistoryVos = (IncomeHistoryVo[]) singleObjectBO.queryByCondition(IncomeHistoryVo.class,
				" PK_CORP in ( " + SqlUtil.buildSqlConditionForIn(pkCorpIncomeHistory.toArray(new String[pkCorpIncomeHistory.size()])) + " )  and nvl(dr,0) = 0", null);

		for(IncomeHistoryVo incomeHistoryVo : incomeHistoryVos){
			incomeHistoryVoMap.put(incomeHistoryVo.getPk_sryj()+"_"+incomeHistoryVo.getPeriod(), incomeHistoryVo.getOccur_mny());
		}
		return incomeHistoryVoMap;
	}

	/**
	 * @param incomeWarningVOS 预警设置集合
	 * @return { 科目主键 }
	 * @description 拆分科目主键
	 */
	private Set<String> splitAccsubj(IncomeWarningVO[] incomeWarningVOS) {

		if(DZFArrayUtil.isEmpty(incomeWarningVOS)){
			return new HashSet<>();
		}

		Set<String> pkAccsubjSet = new HashSet<>();

		for(int i = 0; i < incomeWarningVOS.length; i++){
			String pk_accsubj = incomeWarningVOS[i].getPk_accsubj();
			//取数科目多个处理
			if(StringUtil.isEmpty(pk_accsubj)){
				continue;
			} else if(pk_accsubj.indexOf(",") != -1){
				String[] pk_accsubjArr = pk_accsubj.split(",");
				for(String pk_accsubj1 : pk_accsubjArr){
					pkAccsubjSet.add(pk_accsubj1);
				}
			}else{
				pkAccsubjSet.add(pk_accsubj);
			}
		}
		return pkAccsubjSet;
	}

	/**
	 *
	 * @param kmbmList 科目编码集合
	 * @return 编码过滤条件
	 * @description 创建sql where RegexpLike查询条件
	 */
	private String createRegexpLikeCondition(List<String> kmbmList) {

		if(DZFCollectionUtil.isEmpty(kmbmList)){
			return null;
		}
		kmbmList.sort((s1, s2) -> s1.compareTo(s2));
		Set<String> codeSet = new HashSet<>();
		int parentIndex = 0;
		codeSet.add(kmbmList.get(0));
		for (int i = 1; i < kmbmList.size(); i++) {
			String kmbm = kmbmList.get(i);
			if (kmbm.startsWith(kmbmList.get(parentIndex))) {
				continue;
			} else {
				codeSet.add(kmbm);
				parentIndex = i;
			}
		}
		StringBuilder sb = new StringBuilder("'(^")
				.append(StringUtils.join(codeSet, "|"))
				.append(")'");
		return sb.toString();
	}

	/**
	 * @param previousYearPeriod 起始区间
	 * @param endPeriod 结束区间
	 * @param pk_corpArr 公司编码数组
	 * @param codeLikeStr 编码过滤条件
	 * @return { 凭证发生额 }
	 * @description 根据时间区间、公司编码、科目编码查询发生额
	 */
	private List<VoucherFseQryVO> queryFseByCorpAndKmbmAndPeriod(String previousYearPeriod, String endPeriod, String[] pk_corpArr, String codeLikeStr) {
		SQLParameter sp = new SQLParameter();

		StringBuffer sf = new StringBuffer();
		sf.append("select b.pk_corp, sum(decode(c.direction, '0', b.jfmny, '1', b.dfmny, b.jfmny)) as mny, b.vcode, h.period, c.direction");
		sf.append(" from ynt_tzpz_b b  join ynt_tzpz_h h on b.pk_tzpz_h = h.pk_tzpz_h  join ynt_cpaccount c on b.pk_accsubj = c.pk_corp_account ");
		sf.append(" where 1 = 1 ");
		if(!StringUtil.isEmpty(codeLikeStr)){
			sf.append(" and  regexp_like(b.vcode,"+codeLikeStr+")");
		}
		if(!StringUtil.isEmpty(previousYearPeriod)){
			sp.addParam(previousYearPeriod);
			sf.append(" and h.period > ?");
		}
		if(!StringUtil.isEmpty(endPeriod)){
			sp.addParam(endPeriod);
			sf.append(" and h.period <= ?");
		}
		sf.append(" and nvl(h.dr, 0) = 0");
		sf.append(" and nvl(b.dr, 0) = 0");
		sf.append("	and nvl(c.dr, 0) = 0");
		if(DZFArrayUtil.isNotEmpty(pk_corpArr)){
			sf.append(" and h.pk_corp in (" + SqlUtil.buildSqlConditionForIn(pk_corpArr) + ")");
		}
		sf.append("group by b.pk_corp, b.vcode, h.period, c.direction");
		return (List<VoucherFseQryVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(VoucherFseQryVO.class));
	}

	/**
	 * @param periodType 预警周期
	 * @return 国际化
	 */
	private String translate(int periodType){
		switch (periodType) {
			case 0:
				return "月度";
			case 1:
				return "季度";
			case 2:
				return "年度";
			default:
				return "连续12月";
		}
	}

	/**
	 * @param period 期间
	 * @param periodType 预警周期
	 * @return [期间]
	 * @description 根据预警周期和区间获取期间结合
	 */
	private String[] getPeriodRange(String period, Integer periodType){
		periodType = periodType == null ? 3 : periodType;
		switch (periodType){
			case 0 :
				return new String[]{ period };
			case 1 :
				DZFDate date = DateUtils.getPeriodStartDate(period);
				int month = date.getMonth();
				int year = date.getYear();
				if(month >= 1 && month <= 3){
					return new String[]{ year+"-01",year+"-02",year+"-03"};
				}else if(month >= 4 && month <= 6){
					return new String[]{ year+"-04",year+"-05",year+"-06"};
				}else if(month >= 7 && month <= 9){
					return new String[]{ year+"-07",year+"-08",year+"-09"};
				}else if(month >= 10 && month <= 12){
					return new String[]{ year+"-10",year+"-11",year+"-12"};
				}
			case 2 :
				int thisYear = DateUtils.getPeriodStartDate(period).getYear();
				return new String[]{ thisYear+"-01",thisYear+"-02",thisYear+"-03", thisYear+"-04",thisYear+"-05",thisYear+"-06", thisYear+"-07",thisYear+"-08",thisYear+"-09", thisYear+"-10",thisYear+"-11",thisYear+"-12"};
			default:
				String[] result = new String[12];
				result[0] = period;
				for(int i = 1; i < 12; i++){
					result[i] = DateUtils.getPreviousPeriod(result[i-1]);
				}
				return result;
		}
	}

	/**
	 * @param s
	 * @return
	 * @description 金额去零
	 */
	private String subZeroAndDot(String s){
		if(s.indexOf(".") > 0){
			s = s.replaceAll("0+?$", "");//去掉多余的0
			s = s.replaceAll("[.]$", "");//如最后一位是.则去掉
		}
		return s;
	}

}


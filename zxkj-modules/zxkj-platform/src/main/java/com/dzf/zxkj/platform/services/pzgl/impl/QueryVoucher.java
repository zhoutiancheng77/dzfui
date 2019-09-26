package com.dzf.zxkj.platform.services.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.base.vo.QueryPageVO;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.PZTaxItemRadioVO;
import com.dzf.zxkj.platform.model.pzgl.PzSourceRelationVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.pzgl.VoucherParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.bdset.IAuxiliaryAccountService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 凭证查询
 *
 */
public class QueryVoucher {
	
	private SingleObjectBO singleObjectBO = null;
	
	private IAuxiliaryAccountService gl_fzhsserv = null;
	
	public QueryVoucher(SingleObjectBO singleObjectBO, IAuxiliaryAccountService gl_fzhsserv){
		this.singleObjectBO = singleObjectBO;
		this.gl_fzhsserv = gl_fzhsserv;
	}
	
	public List<TzpzHVO> queryVoucher(VoucherParamVO paramvo) throws BusinessException {
		String id = paramvo.getPk_tzpz_h();
		String pk_corp = paramvo.getPk_corp();
		DZFDate bdate = paramvo.getBegindate();
		DZFDate edate = paramvo.getEnddate();
		String pzh = paramvo.getPzh();
		String pzh2 = paramvo.getPzh2();
		Integer pz_status = paramvo.getPz_status();
		Integer iautorecognize = paramvo.getIautorecognize();
		String cn_status = paramvo.getCn_status();
		String zy = paramvo.getZy();
		String vname = paramvo.getVname();
		String fzhsxm = paramvo.getFzhsxm();
		String fzhslb = paramvo.getFzhslb();
		String fzcode = paramvo.getFzcode();
		String join = "";
		DZFDouble mny1 = paramvo.getMny1();
		DZFDouble mny2 = paramvo.getMny2();
		if(StringUtil.isEmpty(id) && (StringUtil.isEmpty(pk_corp) || bdate == null || edate == null)){
			throw new BusinessException("传入查询条件为空！");
		}
		StringBuffer sf = new StringBuffer();
		StringBuffer wheresql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sf.append("select distinct ynt_tzpz_h.pk_tzpz_h, ynt_tzpz_h.*,  zd.user_name as zd_user,sh.user_name as sh_user, ");	
		sf.append("jz.user_name as jz_user ,cn.user_name as cn_user");
		sf.append(" from ynt_tzpz_h   ");
		sf.append("left join sm_user zd on ynt_tzpz_h.coperatorid = zd.cuserid ");
		sf.append("left join sm_user sh on ynt_tzpz_h.vapproveid = sh.cuserid ");
		sf.append("left join sm_user jz on ynt_tzpz_h.vjzoperatorid = jz.cuserid ");
		sf.append("left join sm_user cn on ynt_tzpz_h.vcashid = cn.cuserid ");//出纳
		if(!StringUtil.isEmptyWithTrim(vname) || !StringUtil.isEmptyWithTrim(zy) || !StringUtil.isEmptyWithTrim(fzhslb)
				|| mny1 != null && mny1.toDouble() != 0 || mny2 != null && mny2.toDouble() != 0){
			join = " left join ynt_tzpz_b tb on ynt_tzpz_h.pk_tzpz_h = tb.pk_tzpz_h and ynt_tzpz_h.pk_corp = tb.pk_corp and nvl(tb.dr,0)=0 ";
			sf.append(join);
		}
		wheresql.append(" where ynt_tzpz_h.pk_corp = ? ");
		if (!StringUtil.isEmpty(id)) {
			wheresql.append(" and ynt_tzpz_h.pk_tzpz_h = ? ");
			sp.addParam(id);
		}
		wheresql.append(" and nvl(ynt_tzpz_h.dr,0)=0 ");
		if (bdate != null) {
			wheresql.append(" and ynt_tzpz_h.doperatedate >= ? ");
			sp.addParam(bdate);
		}
		if (edate != null) {
			wheresql.append(" and ynt_tzpz_h.doperatedate <= ?");
			sp.addParam(edate);
		}
		if (!StringUtil.isEmptyWithTrim(pzh)) {
			wheresql.append(" and ynt_tzpz_h.pzh >= ? ");
			sp.addParam(pzh.trim());
		}
		if (!StringUtil.isEmptyWithTrim(pzh2)){
			wheresql.append(" and ynt_tzpz_h.pzh <= ? ");
			sp.addParam(pzh2.trim());
		}
		if(pz_status != null && pz_status != 0){//修改：pz_status == 0 代表全部
			wheresql.append(" and (ynt_tzpz_h.vbillstatus = ? ");
			if (pz_status == -1) {
				// 暂存不包含已识别图片
				wheresql.append(" and ynt_tzpz_h.iautorecognize <> 1");				
			} else if (pz_status == 8) {
				// 未审核包含已识别图片
				wheresql.append(" or ynt_tzpz_h.vbillstatus = -1 and iautorecognize = 1");				
			}
			wheresql.append(")");
			sp.addParam(pz_status);
		}

		if(iautorecognize != null && iautorecognize != -111){
			wheresql.append("and nvl(ynt_tzpz_h.iautorecognize,0) = ? ");
			sp.addParam(iautorecognize);
		}
		
		if(!StringUtil.isEmpty(cn_status)){
			wheresql.append(" and nvl(ynt_tzpz_h.bsign,'N') = ? ");
			sp.addParam(cn_status);
		}
		
		if (vname != null && vname.trim().length() != 0){
			vname = "%" + vname + "%";
//			vcode = vcode + "%";
			wheresql.append("and (tb.kmmchie like ? or tb.vcode like ?) ");
			sp.addParam(vname.trim());
			sp.addParam(vname.trim());
		}
		
		if (!StringUtil.isEmptyWithTrim(fzhslb)) {
			if (!StringUtil.isEmptyWithTrim(fzhsxm)) {
				fzhsxm = fzhsxm.trim();
				wheresql.append("and( tb.fzhsx");
				wheresql.append(fzcode);
				wheresql.append(" = ? ");
				wheresql.append(" or tb.pk_inventory = ? ) ");
				sp.addParam(fzhsxm);
				sp.addParam(fzhsxm);
			} else {
				wheresql.append("and (tb.fzhsx");
				wheresql.append(fzcode);
				wheresql.append(" is not null ");
				wheresql.append(" or tb.pk_inventory is not null ) ");
			}
		}
		if (zy != null && zy.trim().length() != 0){
			zy = "%" + zy + "%";
			wheresql.append("and tb.zy like ? ");
			sp.addParam(zy.trim());
		}
		if(mny1 != null && mny1.toDouble() != 0){
			if(mny2 != null && mny2.toDouble() != 0){
				wheresql.append("and ((tb.jfmny >= ? and tb.jfmny <= ?) or (tb.dfmny >= ? and tb.dfmny <= ?)) ");
				sp.addParam(mny1);
				sp.addParam(mny2);
				sp.addParam(mny1);
				sp.addParam(mny2);
			}else{
				wheresql.append("and (tb.jfmny >= ? or tb.dfmny >= ?) ");
				sp.addParam(mny1);
				sp.addParam(mny1);
			}
		}else if(mny2 != null && mny2.toDouble() != 0){
			wheresql.append("and (tb.jfmny <= ? or tb.dfmny <= ?) ");
			sp.addParam(mny2);
			sp.addParam(mny2);
		}
		List<TzpzHVO> hvo = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString() + wheresql.toString()
				+ " order by ynt_tzpz_h.period, ynt_tzpz_h.pzh ",sp, new BeanListProcessor(TzpzHVO.class));
		StringBuffer sql = new StringBuffer();
		sql.append("select ynt_inventory.name as invname, tb1.*, taxitem.shortname as taxname ");
		sql.append(" from ynt_tzpz_b tb1  join  ");
		sql.append(" (select distinct ynt_tzpz_h.pk_tzpz_h,ynt_tzpz_h.pk_corp   ");
		sql.append("   from ynt_tzpz_h " + join + wheresql.toString() +  ") th ");
		sql.append("    on th.pk_tzpz_h = tb1.pk_tzpz_h and th.pk_corp=tb1.pk_corp  ");
		sql.append("  left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = tb1.pk_inventory");
		sql.append(" left join ynt_pztaxitem pztax on pztax.pk_tzpz_b = tb1.pk_tzpz_b");
		sql.append(" left join ynt_taxitem taxitem on taxitem.pk_taxitem = pztax.pk_taxitem");
		sql.append("   where nvl(tb1.dr,0) = 0  order by tb1.pk_tzpz_h,tb1.rowno ");
		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sql.toString(),sp, new BeanListProcessor(TzpzBVO.class));
		//System.out.println("sql=" + sql);
		Map<String, List<TzpzBVO>> map = DZfcommonTools.hashlizeObject(bvos, new String[]{"pk_tzpz_h"});//返回MAP信息
		bvos = null;
		List<TzpzHVO> reshvo = new ArrayList<TzpzHVO>();//出纳签字过滤 银行存款，库存现金，其他货币资金
		if(hvo != null && hvo.size() > 0){
			Map<String, AuxiliaryAccountBVO> fzhsMap = gl_fzhsserv.queryMap(pk_corp);
			for(TzpzHVO th : hvo){
				List<TzpzBVO> liz = map.get(th.getPrimaryKey());
				DZFBoolean ishashb = DZFBoolean.FALSE;
				if(liz != null && !liz.isEmpty()){
					for (TzpzBVO tzpzBVO : liz) {
						tzpzBVO.setFzhs_list(getFzhs(tzpzBVO, fzhsMap));
						if(tzpzBVO.getVcode() != null &&
								(tzpzBVO.getVcode().startsWith("1001")|| tzpzBVO.getVcode().startsWith("1002")
										|| tzpzBVO.getVcode().startsWith("1012"))){
							ishashb = DZFBoolean.TRUE;
						}
					}
					th.setChildren( DZfcommonTools.convertToSuperVO(liz.toArray(new TzpzBVO[0])));;
				}
				if(paramvo.getCnqz()!=null && paramvo.getCnqz().booleanValue()  && !ishashb.booleanValue()){//不包含货币资金的不显示
					continue;
				}
				reshvo.add(th);
			}
		}
		map=null;
			
		return reshvo;
	}
	
	public QueryPageVO queryVoucherPaged(VoucherParamVO paramvo) throws  BusinessException{
		String id = paramvo.getPk_tzpz_h();
		String pk_corp = paramvo.getPk_corp();
		DZFDate bdate = paramvo.getBegindate();
		DZFDate edate = paramvo.getEnddate();
		String pzh = paramvo.getPzh();
		String pzh2 = paramvo.getPzh2();
		Integer pz_status = paramvo.getPz_status();
		Integer iautorecognize = paramvo.getIautorecognize();
		String cn_status = paramvo.getCn_status();
		String zy = paramvo.getZy();
		String vname = paramvo.getVname();
		String fzhsxm = paramvo.getFzhsxm();
		String fzhslb = paramvo.getFzhslb();
		String fzcode = paramvo.getFzcode();
		String sourcebilltype = paramvo.getSourcebilltype();
		String join = "";
		DZFDouble mny1 = paramvo.getMny1();
		DZFDouble mny2 = paramvo.getMny2();
		if(StringUtil.isEmpty(id) && (StringUtil.isEmpty(pk_corp) || bdate == null || edate == null)){
			throw new BusinessException("传入查询条件为空！");
		}
		StringBuilder wheresql = new StringBuilder();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		if(!StringUtil.isEmptyWithTrim(vname) || !StringUtil.isEmptyWithTrim(zy) || !StringUtil.isEmptyWithTrim(fzhslb)
				|| mny1 != null && mny1.toDouble() != 0 || mny2 != null && mny2.toDouble() != 0){
			join = "  join ynt_tzpz_b tb on ynt_tzpz_h.pk_tzpz_h = tb.pk_tzpz_h  ";
		}
		wheresql.append(" where ynt_tzpz_h.pk_corp = ? ");
		if (!StringUtil.isEmpty(id)) {
			wheresql.append(" and ynt_tzpz_h.pk_tzpz_h = ? ");
			sp.addParam(id);
		}
		/**
		 *    改成支持多种来源组合查询
		 *    原因： 一键结转联查凭证  gzx  (2019-08/1.3.6 一键结转联查凭证功能优化)
		 */
		if(!StringUtil.isEmpty(sourcebilltype)){
			if(sourcebilltype.contains(",")){
				String[] sourcebilltypeArr = sourcebilltype.split(",");
				wheresql.append(" and ");
				wheresql.append(SqlUtil.buildSqlForIn("ynt_tzpz_h.sourcebilltype", sourcebilltypeArr));
			}else{
				wheresql.append(" and ynt_tzpz_h.sourcebilltype = ? ");
				sp.addParam(sourcebilltype);
			}
		}

		wheresql.append(" and nvl(ynt_tzpz_h.dr,0)=0 ");
		if (bdate != null) {
			wheresql.append(" and ynt_tzpz_h.doperatedate >= ? ");
			sp.addParam(bdate);
		}
		if (edate != null) {
			wheresql.append(" and ynt_tzpz_h.doperatedate <= ?");
			sp.addParam(edate);
		}
		if (!StringUtil.isEmptyWithTrim(pzh)) {
			wheresql.append(" and ynt_tzpz_h.pzh >= ? ");
			sp.addParam(pzh.trim());
		}
		if (!StringUtil.isEmptyWithTrim(pzh2)){
			wheresql.append(" and ynt_tzpz_h.pzh <= ? ");
			sp.addParam(pzh2.trim());
		}
		if(pz_status != null){
			//修改：pz_status == 0 代表全部
			if (pz_status == 11 || pz_status == 12) {
				wheresql.append(" and nvl(ynt_tzpz_h.ishasjz, 'N') = ? ");
				sp.addParam(pz_status == 11 ? DZFBoolean.FALSE : DZFBoolean.TRUE);
			} else if (pz_status != 0) {
				wheresql.append(" and (ynt_tzpz_h.vbillstatus = ? ");
				if (pz_status == -1) {
					// 暂存不包含已识别图片
					wheresql.append(" and ynt_tzpz_h.iautorecognize <> 1");
				} else if (pz_status == 8) {
					// 未审核包含已识别图片
					wheresql.append(" or ynt_tzpz_h.vbillstatus = -1 and iautorecognize = 1");
				}
				wheresql.append(")");
				sp.addParam(pz_status);
			}
		}

		if(iautorecognize != null && iautorecognize != -111){
			wheresql.append("and nvl(ynt_tzpz_h.iautorecognize,0) = ? ");
			sp.addParam(iautorecognize);
		}
		
		if(!StringUtil.isEmpty(cn_status)){
			wheresql.append(" and nvl(ynt_tzpz_h.bsign,'N') = ? ");
			sp.addParam(cn_status);
		}
		if(paramvo.getIs_error_cash() != null
				&& paramvo.getIs_error_cash()){
			wheresql.append(" and error_cash_analyse = ? ");
			sp.addParam(paramvo.getIs_error_cash());
		}
		if(paramvo.getIs_error_tax() != null
				&& paramvo.getIs_error_tax()){
			wheresql.append(" and error_tax_analyse = ? ");
			sp.addParam(paramvo.getIs_error_tax());
		}
		if (vname != null && vname.trim().length() != 0){
			vname = "%" + vname + "%";
//			vcode = vcode + "%";
			wheresql.append("and (tb.kmmchie like ? or tb.vcode like ?) ");
			sp.addParam(vname.trim());
			sp.addParam(vname.trim());
		}
		
		if (!StringUtil.isEmptyWithTrim(fzhslb)) {
			if (!StringUtil.isEmptyWithTrim(fzhsxm)) {
				fzhsxm = fzhsxm.trim();
				wheresql.append("and( tb.fzhsx");
				wheresql.append(fzcode);
				wheresql.append(" = ? ");
				wheresql.append(" or tb.pk_inventory = ? ) ");
				sp.addParam(fzhsxm);
				sp.addParam(fzhsxm);
			} else {
				wheresql.append("and (tb.fzhsx");
				wheresql.append(fzcode);
				wheresql.append(" is not null ");
				wheresql.append(" or tb.pk_inventory is not null ) ");
			}
		}
		if (zy != null && zy.trim().length() != 0){
			zy = "%" + zy + "%";
			wheresql.append("and tb.zy like ? ");
			sp.addParam(zy.trim());
		}
		if(mny1 != null && mny1.toDouble() != 0){
			if(mny2 != null && mny2.toDouble() != 0){
				wheresql.append("and ((tb.jfmny >= ? and tb.jfmny <= ?) or (tb.dfmny >= ? and tb.dfmny <= ?)) ");
				sp.addParam(mny1);
				sp.addParam(mny2);
				sp.addParam(mny1);
				sp.addParam(mny2);
			}else{
				wheresql.append("and (tb.jfmny >= ? or tb.dfmny >= ?) ");
				sp.addParam(mny1);
				sp.addParam(mny1);
			}
		}else if(mny2 != null && mny2.toDouble() != 0){
			wheresql.append("and (tb.jfmny <= ? or tb.dfmny <= ?) ");
			sp.addParam(mny2);
			sp.addParam(mny2);
		}
		if(!StringUtil.isEmptyWithTrim(join)){
			wheresql.append(" and nvl(tb.dr,0) = 0 ");
		}
		String where = wheresql.toString();
		String orderField = "ynt_tzpz_h.period, ynt_tzpz_h.pzh";
		SQLParameter emptyParam = new SQLParameter();
		// 把凭证主表rowid放入临时表
		prepareVoucherToTempTable(join, where, orderField, sp, paramvo.getPage(), paramvo.getRows());
		int total = getTotal(join, where, sp);

		StringBuilder headSql = new StringBuilder();
		headSql.append("select ynt_tzpz_h.*,  zd.user_name as zd_user,sh.user_name as sh_user, ")
		.append("jz.user_name as jz_user ,cn.user_name as cn_user")
		.append(" from ynt_tzpz_h ")
		.append("left join sm_user zd on ynt_tzpz_h.coperatorid = zd.cuserid ")
		.append("left join sm_user sh on ynt_tzpz_h.vapproveid = sh.cuserid ")
		.append("left join sm_user jz on ynt_tzpz_h.vjzoperatorid = jz.cuserid ")
		.append("left join sm_user cn on ynt_tzpz_h.vcashid = cn.cuserid ")
		.append(" where ynt_tzpz_h.rowid in (select a from dzf_tmp_insql) order by ")
		.append(orderField);

		List<TzpzHVO> hvo = (List<TzpzHVO>) singleObjectBO
				.executeQuery(headSql.toString(),
						emptyParam, new BeanListProcessor(TzpzHVO.class));
		List<TzpzHVO> reshvo = new ArrayList<TzpzHVO>();//出纳签字过滤 银行存款，库存现金，其他货币资金
		if(hvo != null && hvo.size() > 0){
			StringBuilder sql = new StringBuilder();
			sql.append("select ynt_inventory.name as invname, tb1.*, ")
					.append(" subj.accountcode as vcode,subj.accountname as vname, ")
					.append(" ynt_inventory.invspec, ynt_inventory.invtype, ")
					.append(" subj.fullname as kmmchie, case when ynt_measure.name = null")
					.append(" then subj.measurename else ynt_measure.name end as meaname ");
//		sql.append(", taxitem.shortname as taxname ");
			sql.append(" from ynt_tzpz_b tb1  join  ");
			sql.append(" (select pk_tzpz_h, pk_corp   ");
			sql.append("   from ynt_tzpz_h where rowid in (select a from dzf_tmp_insql) ) th ");
			sql.append("    on th.pk_tzpz_h = tb1.pk_tzpz_h and th.pk_corp=tb1.pk_corp  ");
			sql.append("  left join ynt_cpaccount subj on subj.pk_corp_account = tb1.pk_accsubj");
			sql.append("  left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = tb1.pk_inventory");
			sql.append(" left join ynt_measure ynt_measure on ynt_inventory.pk_measure = ynt_measure.pk_measure");
//		sql.append(" left join ynt_pztaxitem pztax on pztax.pk_tzpz_b = tb1.pk_tzpz_b");
//		sql.append(" left join ynt_taxitem taxitem on taxitem.pk_taxitem = pztax.pk_taxitem");
			sql.append("   where nvl(tb1.dr,0) = 0  order by tb1.pk_tzpz_h,tb1.rowno ");
			List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sql.toString(), emptyParam, new BeanListProcessor(TzpzBVO.class));
			Map<String, List<TzpzBVO>> map = DZfcommonTools.hashlizeObject(bvos, new String[]{"pk_tzpz_h"});//返回MAP信息
			bvos = null;
			Map<String, AuxiliaryAccountBVO> fzhsMap = gl_fzhsserv.queryMap(pk_corp);

			for(TzpzHVO th : hvo){
				List<TzpzBVO> liz = map.get(th.getPrimaryKey());
				DZFBoolean ishashb = DZFBoolean.FALSE;
				if(liz != null && !liz.isEmpty()){
					for (TzpzBVO tzpzBVO : liz) {
						tzpzBVO.setFzhs_list(getFzhs(tzpzBVO, fzhsMap));
						if(tzpzBVO.getVcode() != null &&
								(tzpzBVO.getVcode().startsWith("1001")|| tzpzBVO.getVcode().startsWith("1002")
										|| tzpzBVO.getVcode().startsWith("1012"))){
							ishashb = DZFBoolean.TRUE;
						}
					}
					th.setChildren( DZfcommonTools.convertToSuperVO(liz.toArray(new TzpzBVO[0])));;
				}
				if(paramvo.getCnqz()!=null && paramvo.getCnqz().booleanValue()  && !ishashb.booleanValue()){//不包含货币资金的不显示
					continue;
				}
				reshvo.add(th);
			}
		}
		QueryPageVO pagedVO = new QueryPageVO();
		pagedVO.setTotal(total);
		pagedVO.setPagevos(reshvo.toArray(new TzpzHVO[0]));
		return pagedVO;
	}

	private void prepareVoucherToTempTable(String join, String where, String orderFields,SQLParameter sp, int pageNo, int pageSize) {
		StringBuilder pagedSql = new StringBuilder();
		pagedSql.append("insert into dzf_tmp_insql(a) select rid from (select rownum rn, tem.rid from (")
		.append(" select ");
		if (!StringUtil.isEmpty(join)) {
			pagedSql.append(" distinct ");
		}
		pagedSql.append(" ynt_tzpz_h.pk_tzpz_h, ynt_tzpz_h.rowid rid,")
		.append(orderFields)
		.append(" from ynt_tzpz_h ")
		.append(join)
		.append(where)
		.append(" order by ")
		.append(orderFields)
		.append(") tem ")
		.append(" where rownum <= " + pageNo * pageSize)
		.append(" ) where rn > " + (pageNo - 1) * pageSize);
		singleObjectBO.executeUpdate(pagedSql.toString(), sp);
	}

	private int getTotal(String join, String where, SQLParameter sp) {
		StringBuilder countSql = new StringBuilder();
		countSql.append("select count(1) from (")
		.append(" select ");
		if (!StringUtil.isEmpty(join)) {
			countSql.append(" distinct ");
		}
		countSql.append("  ynt_tzpz_h.pk_tzpz_h from ynt_tzpz_h ")
		.append(join)
		.append(" ")
		.append(where)
		.append(" ")
		.append(")");
		BigDecimal big = (BigDecimal) singleObjectBO.executeQuery(countSql.toString(), sp, new ColumnProcessor());
		return big.intValue();
	}

	public TzpzHVO queryVoucherById(VoucherParamVO paramvo) throws  BusinessException{
		String pk = paramvo.getPk_tzpz_h();
		if(pk == null){
			throw new BusinessException("传入查询条件为空！");
		}
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk);
		sf.append(" pk_tzpz_h = ? and nvl(dr,0)=0 ");	
		List<TzpzHVO> ancevos = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString(),sp, new Class[]{TzpzHVO.class,TzpzBVO.class});
		if(ancevos != null && ancevos.size() > 0){
			return ancevos.get(0);
		}
		return null;
	}

	public List<TzpzHVO> queryVoucherByids(List<String> pklist,
										   boolean containsChildren, boolean containsRelation,
										   boolean containsTaxItem) throws DZFWarpException {
		if(pklist == null || pklist.size()==0){
			return null;
		}
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select * from ynt_tzpz_h where  nvl(dr,0)=0  and ")
				.append(SqlUtil.buildSqlForIn("pk_tzpz_h", pklist.toArray(new String[0])));
		List<TzpzHVO> hvos = (List<TzpzHVO>) singleObjectBO.executeQuery(sf.toString(),sp, new BeanListProcessor(TzpzHVO.class));
		VOUtil.ascSort(hvos, new String[]{"period", "pzh"});
		if (containsChildren) {
			addChildren(hvos, pklist);
			if (containsTaxItem) {
				addTaxItems(hvos, pklist);
			}
		}
		if (containsRelation) {
			addSourceRelations(hvos, pklist);
		}
		return hvos;
	}
	public List<TzpzHVO> queryVoucherByids(List<String> pklist)throws DZFWarpException{
		return queryVoucherByids(pklist, true, true, true);
		
	}
	
	/**
	 * 查询主表
	 */
	@SuppressWarnings("unchecked")
	public TzpzHVO queryHeadVOById(String pk_tzpz_h)throws  BusinessException{
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sb.append(" select  ynt_tzpz_h.*,zd.user_name as zd_user,sh.user_name as sh_user,cn.user_name as cn_user, ");
		sb.append("jz.user_name as jz_user from ynt_tzpz_h ");
		sb.append("left join sm_user zd on ynt_tzpz_h.coperatorid = zd.cuserid ");
		sb.append("left join sm_user sh on ynt_tzpz_h.vapproveid = sh.cuserid ");
		sb.append("left join sm_user jz on ynt_tzpz_h.vjzoperatorid = jz.cuserid ");
		sb.append("left join sm_user cn on ynt_tzpz_h.vcashid = cn.cuserid ");
		sb.append("where ynt_tzpz_h.pk_tzpz_h = ? and nvl(ynt_tzpz_h.dr,0) = 0 ");
		
		TzpzHVO hvo = null;	//(TzpzHVO) singleObjectBO.queryByPrimaryKey(TzpzHVO.class, pk);

		List<TzpzHVO> hvos = (List<TzpzHVO>) singleObjectBO.executeQuery(sb.toString(),sp, new BeanListProcessor(TzpzHVO.class));
		if(hvos != null && hvos.size() > 0){
			hvo = hvos.get(0);
			TzpzBVO[] bodyvos = queryFullBodyVos(pk_tzpz_h, hvo.getPk_corp());
			hvo.setChildren(bodyvos);
		}
		return hvo;
	}
	
	public List<TzpzHVO> queryByPeriod(String Period, String pk_corp)throws  BusinessException{
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(Period);
		sp.addParam(pk_corp);
		sb.append(" select * from ynt_tzpz_h ");
		sb.append("where period = ? and pk_corp = ? and nvl(dr,0)=0 ");

		List<TzpzHVO> hvos = (List<TzpzHVO>) singleObjectBO.executeQuery(sb.toString(),sp, new BeanListProcessor(TzpzHVO.class));
		VOUtil.ascSort(hvos, new String[]{"pzh"});
		List<String> ids = new ArrayList<>();
		for (TzpzHVO hvo :hvos) {
			ids.add(hvo.getPk_tzpz_h());
		}
		addChildren(hvos, ids);
		addTaxItems(hvos, ids);
		addSourceRelations(hvos, ids);
		return hvos;
	}
	public TzpzBVO[] queryFullBodyVos(String pk_tzpz_h, String pk_corp){
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);
		CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		String[] joinFields = null;
		if(!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
			joinFields = getJoinFullFieldsNOic();
		}else{
			joinFields = getJoinFullFields();
		}
		sb.append(" select ");
		for(String field : joinFields){
			sb.append(field + ",");
		}
		sb.append(" ynt_tzpz_b.* ");
		sb.append(" from ynt_tzpz_b ynt_tzpz_b");
		sb.append(" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = ynt_tzpz_b.pk_inventory");
		sb.append(" left join ynt_measure ynt_measure on ynt_inventory.pk_measure = ynt_measure.pk_measure");
		sb.append(" left join ynt_cpaccount ynt_cpaccount on ynt_cpaccount.pk_corp_account = ynt_tzpz_b.pk_accsubj");
		sb.append(" where nvl(ynt_tzpz_b.dr,0)=0");
		sb.append(" and ynt_tzpz_b.pk_tzpz_h = ? and ynt_tzpz_b.pk_corp = ? order by ynt_tzpz_b.pk_tzpz_h,ynt_tzpz_b.rowno ");
		List<TzpzBVO> listVO = (List<TzpzBVO>) singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(TzpzBVO.class));
		if(listVO != null && listVO.size() > 0){
			Map<String, AuxiliaryAccountBVO> fzhsMap = gl_fzhsserv.queryMap(pk_corp);
			for (TzpzBVO tzpzBVO : listVO) {
				tzpzBVO.setFzhs_list(getFzhs(tzpzBVO, fzhsMap));
			}
			return listVO.toArray(new TzpzBVO[0]);
		}
		return null;
	}
	
	public String[] getJoinFullFields(){
		String[] strs = new String[]{
				"ynt_inventory.code as invcode",
				"ynt_inventory.name as invname",
				"ynt_measure.code as meacode",
				"ynt_cpaccount.exc_crycode as exc_crycode",
				"ynt_cpaccount.exc_pk_currency as exc_pk_currency",
				"ynt_measure.name as meaname"
		};
		return strs;
	}
	public String[] getJoinFullFieldsNOic(){
		String[] strs = new String[]{
				"ynt_cpaccount.exc_crycode as exc_crycode",
				"ynt_cpaccount.exc_pk_currency as exc_pk_currency",
				"ynt_cpaccount.measurename as meaname"
		};
		return strs;
	}
	public TzpzBVO[] queryBodyVos(String pk_tzpz_h){
		StringBuffer sb = new StringBuffer();
		String[] joinFields = getJoinFields();
		sb.append(" select ");
		for(String field : joinFields){
			sb.append(field + ",");
		}
		sb.append(" ynt_tzpz_b.* ");
		sb.append(" from ynt_tzpz_b ynt_tzpz_b");
		sb.append(" left join ynt_inventory ynt_inventory on ynt_inventory.pk_inventory = ynt_tzpz_b.pk_inventory");
		sb.append(" left join ynt_measure ynt_measure on ynt_inventory.pk_measure = ynt_measure.pk_measure");
		sb.append(" where nvl(ynt_tzpz_b.dr,0)=0");
		sb.append(" and ynt_tzpz_b.pk_tzpz_h = '"+ pk_tzpz_h +"'");
		List<TzpzBVO> listVO = (List<TzpzBVO>) singleObjectBO.executeQuery(sb.toString(), null, new BeanListProcessor(TzpzBVO.class));
		if(listVO != null && listVO.size() > 0){
			return listVO.toArray(new TzpzBVO[0]);
		}
		return null;
	}
	
	public String[] getJoinFields(){
		String[] strs = new String[]{
				"ynt_inventory.code as invcode",
				"ynt_inventory.name as invname",
				"ynt_measure.code as meacode",
				"ynt_measure.name as meaname"
		};
		return strs;
	}
	
	private List<AuxiliaryAccountBVO> getFzhs(TzpzBVO tzpzBVO, Map<String, AuxiliaryAccountBVO> fzhsMap) {
		List<AuxiliaryAccountBVO> fzhsList = null;
		List<String> pk_fzhs = new ArrayList<String>();
		if (tzpzBVO.getFzhsx1() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx1());
		}
		if (tzpzBVO.getFzhsx2() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx2());
		}
		if (tzpzBVO.getFzhsx3() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx3());
		}
		if (tzpzBVO.getFzhsx4() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx4());
		}
		if (tzpzBVO.getFzhsx5() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx5());
		}
		if (tzpzBVO.getFzhsx6() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx6());
		}
		if (tzpzBVO.getFzhsx7() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx7());
		}
		if (tzpzBVO.getFzhsx8() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx8());
		}
		if (tzpzBVO.getFzhsx9() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx9());
		}
		if (tzpzBVO.getFzhsx10() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx10());
		}
        if(pk_fzhs != null  && pk_fzhs.size() > 0){
        	fzhsList =  new ArrayList<AuxiliaryAccountBVO>();
        	for (String fzhsid : pk_fzhs) {
        		fzhsList.add(fzhsMap.get(fzhsid));
			}
        }
		return fzhsList;
	}

	private List<TzpzBVO> queryBodyDos(List<String> hidList){
		StringBuilder sql = new StringBuilder();
		SQLParameter sp = new SQLParameter();
		String inSql = SqlUtil.buildSqlForIn("tb.pk_tzpz_h", hidList.toArray(new String[0]));
		sql.append("select tb.* from ynt_tzpz_b tb")
		.append(" where ").append(inSql)
		.append(" and nvl(tb.dr,0) = 0 ");
		List<TzpzBVO> bvos = (List<TzpzBVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(TzpzBVO.class));
		return bvos;
		
	}
	private List<PZTaxItemRadioVO> queryTaxItems(List<String> hidList){
		StringBuilder sql = new StringBuilder();
		SQLParameter sp = new SQLParameter();
		String inSql = SqlUtil.buildSqlForIn("pk_tzpz_h", hidList.toArray(new String[0]));
		sql.append("select * from ynt_pztaxitem where ").append(inSql)
				.append(" and nvl(dr,0)=0 ");
		List<PZTaxItemRadioVO> taxItems = (List<PZTaxItemRadioVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(PZTaxItemRadioVO.class));
		return taxItems;

	}
	private void addChildren(List<TzpzHVO> hvos, List<String> hidList) {
		if (hidList == null || hidList.size() == 0) {
			return;
		}
		List<TzpzBVO> bvos = queryBodyDos(hidList);
		Map<String, List<TzpzBVO>> bvoMap = DZfcommonTools.hashlizeObject(bvos, new String[] { "pk_tzpz_h" });
		for (TzpzHVO hvo: hvos) {
			List<TzpzBVO> list = bvoMap.get(hvo.getPrimaryKey());
			hvo.setChildren(list.toArray(new TzpzBVO[0]));
		}
	}
	private void addSourceRelations(List<TzpzHVO> hvos, List<String> hidList) {
		if (hidList == null || hidList.size() == 0) {
			return;
		}
		List<PzSourceRelationVO> relations = querySourceRelations(hidList);
		Map<String, List<PzSourceRelationVO>> relationMap = DZfcommonTools.hashlizeObject(relations, new String[] { "pk_tzpz_h" });
		for (TzpzHVO hvo: hvos) {
			List<PzSourceRelationVO> relationList = relationMap.get(hvo.getPrimaryKey());
			if (relationList != null && relationList.size() > 0) {
				hvo.setSource_relation(relationList.toArray(new PzSourceRelationVO[0]));
			}
		}
	}
	private void addTaxItems(List<TzpzHVO> hvos, List<String> hidList) {
		if (hidList == null || hidList.size() == 0) {
			return;
		}
		List<PZTaxItemRadioVO> taxItems = queryTaxItems(hidList);
		Map<String, List<PZTaxItemRadioVO>> taxItemMap = DZfcommonTools.hashlizeObject(taxItems,
				new String[] { "pk_tzpz_h", "pk_tzpz_b" });
		for (TzpzHVO hvo: hvos) {
			TzpzBVO[] bvos = (TzpzBVO[]) hvo.getChildren();
			if (bvos != null) {
				for (TzpzBVO bvo: bvos) {
					List<PZTaxItemRadioVO> taxList = taxItemMap.get(hvo.getPk_tzpz_h() + "," + bvo.getPk_tzpz_b());
					if (taxList != null && taxList.size() > 0) {
						bvo.setTax_items(taxList);
					}
				}
			}
		}
	}
	private List<PzSourceRelationVO> querySourceRelations(List<String> hidList) {
		StringBuilder sql = new StringBuilder();
		SQLParameter sp = new SQLParameter();
		String inSql = SqlUtil.buildSqlForIn("pk_tzpz_h", hidList.toArray(new String[0]));
		sql.append("select * from ynt_pz_sourcerelation where ").append(inSql)
		.append(" and nvl(dr,0)=0");
		List<PzSourceRelationVO> relations = (List<PzSourceRelationVO>) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(PzSourceRelationVO.class));
		return relations;
	}
}

package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.TaxRptConstPub;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IZtszService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

@Service("sys_ztsz_serv")
public class ZtszServiceImpl implements IZtszService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private ICorpTaxService corpTaxact;
	
	@Autowired
	private IBDCorpTaxService bdcorptaxserv;

	@Autowired
	private ICorpService corpService;

	@Override
	public void updateCorpTaxVo(CorpTaxVo corptaxvo, String selTaxReportIds, String unselTaxReportIds)
			throws DZFWarpException {
		//更新 征收方式
		saveCharge(corptaxvo);
		
		// 如果当前表void是空则新增
		corptaxvo.setIsmaintainedtax(new DZFBoolean(true));//是否维护了纳税信息
		if (StringUtil.isEmpty(corptaxvo.getPrimaryKey())) {
			saveNew(corptaxvo);
		} else {
			update(corptaxvo);
		}
		// 回写bd_corp表的数据，并且清除公司缓存
		writeBackCorp(corptaxvo);

		// 更新税务信息
		HashMap<String, SuperVO[]> sendData = new HashMap<String, SuperVO[]>();
		String[] taxRptids = null;
		if (!StringUtil.isEmpty(selTaxReportIds)) {
			taxRptids = selTaxReportIds.split(",");
		}
		String[] taxUnRptids = null;
		if (!StringUtil.isEmpty(unselTaxReportIds)) {
			taxUnRptids = unselTaxReportIds.split(",");
		}
		// 只是复制corptaxvo的字段
		// 后台更新的字段也只是纳税的字段，不会有问题
		CorpVO cpvo = new CorpVO();
		BeanUtils.copyProperties(corptaxvo, cpvo);
		cpvo.setPk_corp(corptaxvo.getPk_corp());
		corpTaxact.updateCorp(cpvo, sendData, taxRptids, taxUnRptids);
	}

	private void saveCharge(CorpTaxVo vo){
		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());
		if(corpvo == null 
				|| !"00000100AA10000000000BMD".equals(corpvo.getCorptype())){//不是13小企业 不设默认值
			return;
		}
		bdcorptaxserv.saveCharge(vo, vo.getPk_corp(), null, new StringBuffer());
	}
	
	private void saveNew(CorpTaxVo corptaxvo) {
		singleObjectBO.saveObject(corptaxvo.getPk_corp(), corptaxvo);
	}
	
	private void update(CorpTaxVo corptaxvo) {
		singleObjectBO.update(corptaxvo);
	}	
	/**
	 * 回写bd_corp表的数据
	 * @param corptaxvo
	 */
	private void writeBackCorp(CorpTaxVo corptaxvo) {
		String[] upcolumns = new String[] { "vsoccrecode", "isxrq", "drdsj", "legalbodycode",
				"vcorporatephone", "unitname", "unitshortname",  "industry", "chargedeptname", "icostforwardstyle"};
		String pk_corp = corptaxvo.getPk_corp();
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("公司不能为空");
		}
		CorpVO cpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		if (cpvo == null) {
			throw new BusinessException("公司不能为空");
		}
		for (String column : upcolumns) {
			// 处理字段加密
			if ("legalbodycode".equals(column) || "vcorporatephone".equals(column) || "unitname".equals(column)) {
				String value = (String) corptaxvo.getAttributeValue(column);
//				if (!StringUtil.isEmpty(value)) {
//					value = CodeUtils1.enCode(value);
//				}
				cpvo.setAttributeValue(column, value);
			}else if("isxrq".equals(column)){//Integer 类型有问题，单独处理
				cpvo.setIsxrq((Integer)corptaxvo.getAttributeValue(column));
			}
			else if("unitshortname".equals(column)){
				cpvo.setUnitshortname(cpvo.getUnitname());//公司简称默认等于公司名称
			}
			else {// 处理非加密的字段
				cpvo.setAttributeValue(column, corptaxvo.getAttributeValue(column));
			}
		}
		
		//TODO 这个没考虑bd_corp的加锁
		singleObjectBO.update(cpvo, upcolumns);

	}

	@Override
	public List<CorpTaxVo> query(QueryParamVO queryvo, UserVO uservo, Set<String> clist) throws DZFWarpException {
		List<CorpTaxVo> list = new ArrayList<>();

		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		buildQuerySql(queryvo, clist, sf, sp);

		if(sf.length() == 0){
			return list;
		}

		List<CorpTaxVo> vos = (List<CorpTaxVo>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(CorpTaxVo.class));

		if(vos == null || vos.size() == 0){
			return list;
		}

		boolean flag = StringUtil.isEmpty(queryvo.getCorpname()) ? false : true;
		String filter = queryvo.getCorpname();
		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname",
				"vtaxofficernm", "vcorporatephone" }, vos, 1);

		for(CorpTaxVo vo : vos){
			if(flag){
				if(filter.equals(vo.getUnitname())){
					setCorpTaxDefaultValue(vo);
					list.add(vo);
				}
			}else{
				setCorpTaxDefaultValue(vo);
				list.add(vo);
			}

		}

		VOUtil.sort(list, new String[]{"innercode"}, new int[]{VOUtil.ASC});

		return list;
	}

	//默认值
	private void setCorpTaxDefaultValue(CorpTaxVo vo){
//		CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
//				CorpVO.class);

		String yhzc = vo.getVyhzc();//优惠政策
		Integer comtype = vo.getIcompanytype();
		if(StringUtil.isEmpty(yhzc)
				&& (comtype == null ||
				(comtype != 2 && comtype != 20 && comtype != 21))){
			vo.setVyhzc("0");//默认为小型微利
		}

		//设置附加税税目默认税率
		DZFDouble citytax = vo.getCitybuildtax();//城建税
		if(citytax == null){
			vo.setCitybuildtax(new DZFDouble(0.07));
		}
		DZFDouble localtax = vo.getLocaleducaddtax();
		if(localtax == null){
			vo.setLocaleducaddtax(new DZFDouble(0.02));
		}
		DZFDouble educaddtax = vo.getEducaddtax();//教育费附加
		if(educaddtax == null){
			vo.setEducaddtax(new DZFDouble(0.03));
		}

		//设置报税地区默认值
		if (vo.getTax_area() == null) {
			Integer city = vo.getVcity();
			if (city != null && (city == 151 || city == 171 || city == 234)) { //3个单独申报的地区/市：厦门、青岛、深圳
				vo.setDefTaxArea(city); //默认值仅在tax_area为空时才有值和起作用
			} else if (vo.getVprovince() != null) { //省
				vo.setDefTaxArea(vo.getVprovince());
			}
		}


		String corptype = vo.getCorptype();
		if(!"00000100AA10000000000BMD".equals(corptype)){//不是13小企业 不设默认值
			return;
		}

		Integer intype = vo.getIncomtaxtype();
		if(intype == null){
			if(comtype == null ||
					( comtype != 2 && comtype != 20 && comtype != 21)){
				vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_QY);
			}else{
				vo.setIncomtaxtype(TaxRptConstPub.INCOMTAXTYPE_GR);
			}
		}

		Integer taxletype = vo.getTaxlevytype();
		if(taxletype == null){
			vo.setTaxlevytype(TaxRptConstPub.TAXLEVYTYPE_CZZS);//查账征收
		}

		taxletype = vo.getTaxlevytype();
		if(taxletype == TaxRptConstPub.TAXLEVYTYPE_HDZS
				&& vo.getVerimethod() == null){
			vo.setVerimethod(0);//核定应税所得率(能核算收入总额的)
		}

	}

	private void buildQuerySql(QueryParamVO queryvo, Set<String> clist, StringBuffer sql, SQLParameter sp){

		if(clist == null || clist.size() == 0){
			return;
		}

		sql.append("select b1.*,a.innercode,a.begindate,a.createdate,a.vsuperaccount, ");
		sql.append(" a.pk_corp,a.vsoccrecode,a.isxrq,a.drdsj, ");
		sql.append(" a.legalbodycode,a.vcorporatephone, t.tradename as indusname,a.industry,a.unitname,a.chargedeptname, ");
		sql.append(" a.icostforwardstyle, a.bbuildic, a.ishasaccount, a.holdflag, a.busibegindate, a.icbegindate, a.corptype, ");
		sql.append(" a.vcustsource, a.vprovince, a.vcity, a.varea, a.vbankname, a.fathercorp, a.isseal, a.icompanytype, ");
//		sql.append(" b.unitname as def1 ,");
//		sql.append(" b.def3      		,");
//		sql.append(" b.def2     		,");
		sql.append(" y.accname as ctypename      ,");
		sql.append(" t.tradename as indusname    ,");
		sql.append(" u.user_name as vtaxofficernm  ,");//
		sql.append(" t.tradecode as vtradecode	");   //国家标准行业编码
		sql.append(" from bd_corp a ");
		sql.append(" left join bd_corp_tax b1 on a.pk_corp = b1.pk_corp ");
		sql.append(" left join ynt_tdaccschema y on a.corptype = y.pk_trade_accountschema");// 查询科目方案的名称
		sql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade");
		sql.append(" left join sm_user u on b1.vtaxofficer = u.cuserid");

		sql.append(" where nvl(a.dr,0) = 0 and nvl(a.isseal,'N') = 'N' and nvl(a.ishasaccount,'N') = 'Y' ");
		sql.append(" and  ").append(SqlUtil.buildSqlForIn("a.pk_corp", clist.toArray(new String[0])));

		if(!StringUtil.isEmpty(queryvo.getPk_corp())){
			sql.append(" and a.pk_corp = ? ");
			sp.addParam(queryvo.getPk_corp());
			return;
		}

		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			sql.append(" and a.innercode like ? ");
		}

		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
				sql.append(" and a.createdate = ? ");
				sp.addParam(queryvo.getBegindate1());
			} else {
				sql.append(" and (a.createdate >= ? and a.createdate <= ? )");
				sp.addParam(queryvo.getBegindate1());
				sp.addParam(queryvo.getEnddate());
			}
		}else if(queryvo.getBegindate1() != null){
			sql.append(" and a.createdate >= ? ");
			sp.addParam(queryvo.getBegindate1());
		}else if(queryvo.getEnddate() != null){
			sql.append(" and a.createdate <= ? ");
			sp.addParam(queryvo.getEnddate());
		}
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
				sql.append(" and a.begindate = ? ");
				sp.addParam(queryvo.getBcreatedate());
			} else {
				sql.append(" and (a.begindate >= ? and a.begindate <= ? )");
				sql.append(queryvo.getBcreatedate());
				sql.append(queryvo.getEcreatedate());
			}
		} else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
			sql.append(" and a.begindate >= ? ");
			sql.append(queryvo.getBcreatedate());
		} else if (queryvo.getEcreatedate() != null) {
			sql.append(" and a.begindate <= ? ");
			sql.append(queryvo.getEcreatedate());
		}

		sql.append(" and  nvl(a.isaccountcorp,'N') = 'N' ");

		if(!StringUtil.isEmpty(queryvo.getVprovince())){//报税地区
			sql.append(" and b1.tax_area = ? ");
			sp.addParam(queryvo.getVprovince());
		}
		if(queryvo.getMaintainedtax() != null){
			if(queryvo.getMaintainedtax() == 2){
				sql.append(" and b1.ismaintainedtax = 'Y' ");
			}else if(queryvo.getMaintainedtax() == 3){
				sql.append(" and nvl(b1.ismaintainedtax,'N')='N' ");
			}
		}
	}


}

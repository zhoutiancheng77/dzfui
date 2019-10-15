package com.dzf.zxkj.platform.service.sys.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.platform.util.SecretCodeUtils;
import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IRoleCodeCont;
import com.dzf.zxkj.common.constant.TaxRptConstPub;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.sys.BDTradeVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.tax.SpecDeductHistVO;
import com.dzf.zxkj.platform.model.tax.TaxEffeHistVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.util.BeanUtils;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.vo.sys.QueryParamVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("sys_corp_tax_serv")
public class BDCorpTaxServiceImpl implements IBDCorpTaxService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICorpTaxService corpTaxact;
	@Autowired
	private ICorpService corpService;
	
//	@Autowired
//	private IUserService userServiceImpl;

	@Override
	public List<CorpTaxVo> queryTaxVoByIds(String[] ids) throws DZFWarpException {
		if (ids == null || ids.length == 0) {
			throw new BusinessException("公司参数不能为空");
		}
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select b.*,a.pk_corp,a.innercode,a.begindate,a.createdate,a.vsuperaccount,");
		qrysql.append(" a.vsoccrecode,a.isxrq,a.drdsj, ");
		qrysql.append(" a.legalbodycode,a.vcorporatephone,a.indusname,a.industry,a.unitname,a.chargedeptname, ");
		qrysql.append(" a.vcustsource,a.vcity,a.varea,a.vbankname,a.fathercorp,a.isseal, a.icompanytype ");
		qrysql.append(" from bd_corp a ");
		qrysql.append(" left join bd_corp_tax b on a.pk_corp = b.pk_corp");
		qrysql.append(" where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 ");
		qrysql.append("  and " + SqlUtil.buildSqlForIn("pk_corp", ids));

		List<CorpTaxVo> cptaxvos = (List<CorpTaxVo>) singleObjectBO.executeQuery(qrysql.toString(), new SQLParameter(),
				new BeanListProcessor(CorpTaxVo.class));

		// 数据解密
		cptaxvos = (List<CorpTaxVo>) QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2",
				"unitname", "unitshortname", "def1", "def3", "pcountname", "wqcountname", "vcorporatephone" }, cptaxvos,
				1);
		
		//赋值行业
		Map<String, BDTradeVO> hymap= getHyMap();
		
		if(cptaxvos!=null && cptaxvos.size()>0){
			String industry = "";
			Set<String> uids = new HashSet<String>();
			Set<String> corpids = new HashSet<String>();
			for(CorpTaxVo vo : cptaxvos){
				if(!StringUtil.isEmpty(vo.getVtaxofficer())){
					uids.add(vo.getVtaxofficer());
				}
				
				if(!StringUtil.isEmpty(vo.getPk_corp())){
					corpids.add(vo.getPk_corp());
				}
				
				industry = vo.getIndustry();
				if(!StringUtil.isEmpty(industry) && hymap.containsKey(industry)){
					hymap.get(industry).getTradename();
					vo.setIndusname(hymap.get(industry).getTradename());// 国家标准行业名称
					vo.setVtradecode(hymap.get(industry).getTradecode());//国家标准行业编码
				}
			}
			
			if(uids.size() > 0){
				UserVO[] uvos = (UserVO[]) singleObjectBO.queryByCondition(UserVO.class, " nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("cuserid", uids.toArray(new String[0])), new SQLParameter());
				Map<String, String> namemap = new HashMap<String,String>();
				if(uvos!=null && uvos.length>0){
					for(UserVO uvo:uvos){
						namemap.put(uvo.getCuserid(), SecretCodeUtils.deCode(uvo.getUser_name()));
					}
					
					for(CorpTaxVo vo : cptaxvos){
						//赋值办税人员名册
						if(!StringUtil.isEmpty(vo.getVtaxofficer())){
							vo.setVtaxofficernm(namemap.get(vo.getVtaxofficer()));
						}
					}
				}
			}
			if(corpids.size() > 0){
				List<CorpVO> cvos = (List<CorpVO>) singleObjectBO.executeQuery(" select * from bd_corp where nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_corp", corpids.toArray(new String[0])),
						new SQLParameter(), new BeanListProcessor(CorpVO.class));
				Map<String, CorpVO> corpmap = new HashMap<String, CorpVO>();
				if(cvos != null && cvos.size() > 0){
					corpmap = DZfcommonTools.hashlizeObjectByPk(cvos, new String[]{"pk_corp"});
					CorpVO corpvo;
					//赋值所得税类型
					for(CorpTaxVo vo : cptaxvos){
						corpvo = corpmap.get(vo.getPk_corp());
						setCorpTaxDefaultValue(vo, corpvo);
					}
				}
			}
			
		}
		return cptaxvos;
	}

	private Map<String, BDTradeVO> getHyMap() {
		Map<String, BDTradeVO> map = new HashMap<String, BDTradeVO>();
		// 获取所有的行业id
		SQLParameter sp = new SQLParameter();
		sp.addParam(IGlobalConstants.currency_corp);
		BDTradeVO[] tradevos = (BDTradeVO[]) singleObjectBO.queryByCondition(BDTradeVO.class,
				"nvl(dr,0)=0 and pk_corp = '000001'", sp);
		if (tradevos != null && tradevos.length > 0) {
			for(BDTradeVO vo:tradevos){
				map.put(vo.getPrimaryKey(), vo);
			}
		}
		return map;
	}

	@Override
	public List<CorpTaxVo> queryTaxVoByParam(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		String sql = getQuerySql(queryvo, uservo);
		SQLParameter param = getQueryParam(queryvo, uservo);
		List<CorpTaxVo> vos = (List<CorpTaxVo>) singleObjectBO.executeQuery(sql.toString(), param,
				new BeanListProcessor(CorpTaxVo.class));
		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname",
				"pcountname", "wqcountname", "vcorporatephone", "unitdistinction" }, vos, 1);
		//赋值办税人员
		if(vos !=null && vos.size()>0){
			Set<String> uids = new HashSet<String>();
			Set<String> corpids = new HashSet<String>();
			for(CorpTaxVo vo:vos){
				if(!StringUtil.isEmpty(vo.getVtaxofficer())){
					uids.add(vo.getVtaxofficer());
				}
				
				if(!StringUtil.isEmpty(vo.getPk_corp())){
					corpids.add(vo.getPk_corp());
				}
			}
			if(uids!=null && uids.size()>0){
				UserVO[] uvos = (UserVO[]) singleObjectBO.queryByCondition(UserVO.class, "nvl(dr,0)=0 and "+ SqlUtil.buildSqlForIn("cuserid", uids.toArray(new String[0])), new SQLParameter());
				Map<String, String> namemap = new HashMap<String,String>();
				if(uvos!=null && uvos.length>0){
					for(UserVO uvo:uvos){
						namemap.put(uvo.getCuserid(), SecretCodeUtils.deCode(uvo.getUser_name()));
					}
				}
				for(CorpTaxVo vo:vos){
					if(!StringUtil.isEmpty(vo.getVtaxofficer())
							&& namemap.containsKey(vo.getVtaxofficer())){
						vo.setVtaxofficernm(namemap.get(vo.getVtaxofficer()));
					}
				}
			}
			
			if(corpids != null && corpids.size() > 0){
				List<CorpVO> cvos = (List<CorpVO>) singleObjectBO.executeQuery(" select * from bd_corp where nvl(dr,0)=0 and " + SqlUtil.buildSqlForIn("pk_corp", corpids.toArray(new String[0])),
						new SQLParameter(), new BeanListProcessor(CorpVO.class));
				Map<String, CorpVO> corpmap = new HashMap<String, CorpVO>();
				if(cvos != null && cvos.size() > 0){
					corpmap = DZfcommonTools.hashlizeObjectByPk(cvos, new String[]{"pk_corp"});
					CorpVO corpvo;
					//赋值所得税类型
					for(CorpTaxVo vo : vos){
						corpvo = corpmap.get(vo.getPk_corp());
						setCorpTaxDefaultValue(vo, corpvo);
					}
				}
			}
			
		}
		
		return vos;
	}

	@Override
	public void updateCorpTaxVo(CorpTaxVo corptaxvo, String selTaxReportIds, String unselTaxReportIds)
			throws DZFWarpException {
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

	/**
	 * 回写bd_corp表的数据
	 * @param corptaxvo
	 */
	private void writeBackCorp(CorpTaxVo corptaxvo) {
		String[] upcolumns = new String[] { "vsoccrecode", "isxrq", "drdsj", "legalbodycode",
				"vcorporatephone", "unitname", "unitshortname",  "industry", "chargedeptname", };
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
//					value = SecretCodeUtils.enCode(value);
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

	/**
	 * 更新表的数据
	 * @param corptaxvo
	 */
	private void update(CorpTaxVo corptaxvo) {
		singleObjectBO.update(corptaxvo);
	}	

	/**
	 * 新增表的数据
	 * @param corptaxvo
	 */
	private void saveNew(CorpTaxVo corptaxvo) {
		singleObjectBO.saveObject(corptaxvo.getPk_corp(), corptaxvo);
	}
	
	
	/**
	 * 拼装我的客户查询信息
	 * 
	 * @param queryvo
	 * @return
	 */
	private String getQuerySql(QueryParamVO queryvo, UserVO uservo) {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select b1.*,a.innercode,a.begindate,a.createdate,a.vsuperaccount, ");
		corpsql.append(" a.pk_corp,a.vsoccrecode,a.isxrq,a.drdsj, ");
		corpsql.append(" a.legalbodycode,a.vcorporatephone, t.tradename as indusname,a.industry,a.unitname,a.chargedeptname, ");
//		corpsql.append(" a.icostforwardstyle, a.bbuildic, a.ishasaccount, a.holdflag, a.busibegindate, a.icbegindate, a.corptype, ");
		corpsql.append(" a.vcustsource,a.vprovince,a.vcity,a.varea,a.vbankname,a.fathercorp,a.isseal,a.icompanytype, ");
		corpsql.append(" b.unitname as def1 ,");
		corpsql.append(" b.def3      		,");
		corpsql.append(" b.def2     		,");
		corpsql.append(" y.accname as ctypename      ,");
		corpsql.append(" t.tradename as indusname    ,");
		corpsql.append(" u.user_name as pcountname   ,");//
		corpsql.append(" us.user_name as wqcountname , ");//
		corpsql.append(" t.tradecode as vtradecode	");   //国家标准行业编码
		corpsql.append(" from bd_corp a ");
		corpsql.append(" left join bd_account b on a.fathercorp = b.pk_corp");
		corpsql.append(" left join bd_corp_tax b1 on a.pk_corp = b1.pk_corp ");
		corpsql.append(" left join ynt_tdaccschema y on a.corptype = y.pk_trade_accountschema");// 查询科目方案的名称
		corpsql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade");
		corpsql.append(" left join sm_user u on a.vsuperaccount = u.cuserid");// 关联用户表--主管会计
		corpsql.append(" left join sm_user us on a.vwqaccount = us.cuserid");// 关联用户表--外勤会计
		if(!StringUtil.isEmpty(queryvo.getAsname())){
            corpsql.append(" join ynt_corpimpress cip on cip.pk_corp = a.pk_corp and cip.vname = '"+queryvo.getAsname()+"'");
        }
		// 添加权限过滤
//		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
//		corpsql.append(" where nvl(a.dr,0) =0 and a.fathercorp = ?");
		corpsql.append(" where nvl(a.dr,0) =0 ");

		if(queryvo.getIshassh()==null||!queryvo.getIshassh().booleanValue()){
			corpsql.append(" and nvl(a.isseal,'N') = 'N' ");
		}
		if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'Y' ");
        }
//		if(!StringUtil.isEmpty(queryvo.getCorpid())){
//			corpsql.append(" and a.pk_corp = ? ");
//		}
		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			corpsql.append(" and a.innercode like ? ");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
				corpsql.append(" and a.createdate = ? ");
			} else {
				corpsql.append(" and (a.createdate >= ? and a.createdate <= ? )");
			}
		}else if(queryvo.getBegindate1() != null){
            corpsql.append(" and a.createdate >= ? ");
        }else if(queryvo.getEnddate() != null){
            corpsql.append(" and a.createdate <= ? ");
        }
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
				corpsql.append(" and a.begindate = ? ");
			} else {
				corpsql.append(" and (a.begindate >= ? and a.begindate <= ? )");
			}
		} else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
			corpsql.append(" and a.begindate >= ? ");
		} else if (queryvo.getEcreatedate() != null) {
			corpsql.append(" and a.begindate <= ? ");
		}
		corpsql.append(" and  nvl(a.isaccountcorp,'N') = 'N' ");
		if (queryvo.getXswyewfs() != null) {
			corpsql.append(" and nvl(a.ispersonal,'N') = ? ");
		}
		// 添加权限过滤
//		if (!ismanager.booleanValue()) {
			corpsql.append(" and (a.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
			corpsql.append(" or a.vsuperaccount = ? or a.coperatorid = ?)");
//		}

		if(!StringUtil.isEmpty(queryvo.getVprovince())){//报税地区
			corpsql.append(" and b1.tax_area=? ");
		}
		if(queryvo.getMaintainedtax() != null){
            if(queryvo.getMaintainedtax() == 2){
                corpsql.append(" and b1.ismaintainedtax = 'Y' ");
            }else if(queryvo.getMaintainedtax() == 3){
                corpsql.append(" and nvl(b1.ismaintainedtax,'N')='N' ");
            }       
        }
		if(!StringUtil.isEmpty(queryvo.getKms_id())){//公司主键
			corpsql.append(" and a.pk_corp=? ");
		} 
		//是否激活dzfAPP
        if(queryvo.getIsdzfapp() != null ){
            if(queryvo.getIsdzfapp() == 0){
                corpsql.append(" and exists (select * from ynt_corp_user where a.pk_corp = pk_corp and nvl(dr,0) = 0 and nvl(istate,2) = 2 and fathercorp = ?) ");
            }else if(queryvo.getIsdzfapp() == 1){
                corpsql.append(" and not exists (select * from ynt_corp_user where a.pk_corp = pk_corp and nvl(dr,0) = 0 and nvl(istate,2) = 2 and fathercorp = ?) ");
            }
        }
		if(queryvo.getIsywskp() != null){
            if(queryvo.getIsywskp() == 0){
                corpsql.append(" and nvl(a.isywskp,'N')= 'Y' ");
            }else if(queryvo.getIsywskp() == 1){
                corpsql.append(" and nvl(a.isywskp,'N')= 'N' ");
            }
        }
		if(queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1){
			corpsql.append(" and nvl(a.ifwgs,0)= ? ");
		}
		if(queryvo.getIsformal() != null){
		    corpsql.append(" and nvl(a.isformal,'N') = ?");
		}
		Integer type = queryvo.getLevelq() == null ? 0 : queryvo.getLevelq();
        if(type == 0){
            
        }else if(type == 1){//代账客户
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'Y'");
        }else if(type == 2){//非代账客户
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'N'");
        }else if(type == 3){//潜在客户
            corpsql.append(" and nvl(a.isformal,'N') = 'N'");
        }else if(type == 4){//一般纳税人
            corpsql.append(" and a.chargedeptname = '一般纳税人'");
        }else if(type == 5){//小规模纳税人
            corpsql.append(" and a.chargedeptname = '小规模纳税人'");
        }else if(type == 6){//未核定纳税人性质
            corpsql.append(" and a.chargedeptname is null");
        }else if(type == 7){//个人客户
            corpsql.append(" and nvl(a.ispersonal,'N') = 'Y'");
        }else if(type == 8){//待分配客户
            corpsql.append(" and (a.pk_corp not in (select pk_corp from sm_user_role where pk_role = '"+ IRoleCodeCont.jms07_ID+"') ");
            corpsql.append(" or a.pk_corp not in (select pk_corp from sm_user_role where pk_role = '"+IRoleCodeCont.jms08_ID+"'))");
        }
		corpsql.append(" order by a.innercode");
		return corpsql.toString();
	}
	
	/**
	 * 获取我的客户查询参数
	 * 
	 * @param queryvo
	 * @param uservo
	 * @return
	 */
	private SQLParameter getQueryParam(QueryParamVO queryvo, UserVO uservo) {
		SQLParameter param = new SQLParameter();
//		param.addParam(queryvo.getPk_corp());

//		if (!StringUtil.isEmpty(queryvo.getCorpid())){
//			param.addParam(queryvo.getCorpid());
//		}
		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			param.addParam("%" + queryvo.getCorpcode() + "%");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			if (queryvo.getBegindate1().compareTo(queryvo.getEnddate()) == 0) {
				param.addParam(queryvo.getBegindate1());
			} else {
				param.addParam(queryvo.getBegindate1());
				param.addParam(queryvo.getEnddate());
			}
		}else if(queryvo.getBegindate1() != null){
            param.addParam(queryvo.getBegindate1());
        }else if(queryvo.getEnddate() != null){
            param.addParam(queryvo.getEnddate());
        }
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			if (queryvo.getBcreatedate().compareTo(queryvo.getEcreatedate()) == 0) {
				param.addParam(queryvo.getBcreatedate());
			} else {
				param.addParam(queryvo.getBcreatedate());
				param.addParam(queryvo.getEcreatedate());
			}
		} else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
			param.addParam(queryvo.getBcreatedate());
		} else if (queryvo.getEcreatedate() != null) {
			param.addParam(queryvo.getEcreatedate());
		}
		if (queryvo.getXswyewfs() != null) {
			param.addParam(queryvo.getXswyewfs());
		}
//		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
//		if (!ismanager.booleanValue()) {
			param.addParam(uservo.getCuserid());
			param.addParam(uservo.getCuserid());
			param.addParam(uservo.getCuserid());
//		}
		if(!StringUtil.isEmpty(queryvo.getVprovince())){
			param.addParam(Integer.parseInt(queryvo.getVprovince()));
		}
		if(!StringUtil.isEmpty(queryvo.getKms_id())){//公司主键
			param.addParam(queryvo.getKms_id());
		}
		//是否激活dzfAPP
        if(queryvo.getIsdzfapp() != null){
            if(queryvo.getIsdzfapp() == 0){
                param.addParam(queryvo.getPk_corp());
            }else if(queryvo.getIsdzfapp() == 1){
                param.addParam(queryvo.getPk_corp());
            }
        }
		if(queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1){
		    param.addParam(queryvo.getIfwgs());
		}
		if(queryvo.getIsformal() != null){
		    param.addParam(queryvo.getIsformal());
		}
		if(queryvo.getCjz() != null && queryvo.getCjz() != -1){
			param.addParam(queryvo.getCjz());
		}
		return param;
	}

	@Override
	public CorpTaxVo queryCorpTaxVO(String pk_corp) throws DZFWarpException {
		CorpTaxVo vo = new CorpTaxVo();
		vo.setPk_corp(pk_corp);
		
		if(StringUtil.isEmpty(pk_corp))
			return vo;
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		CorpTaxVo[] bodyvos = (CorpTaxVo[])singleObjectBO.queryByCondition(CorpTaxVo.class, " pk_corp = ? and nvl(dr,0) = 0  ", sp);
		if(bodyvos != null && bodyvos.length > 0){
			vo = bodyvos[0];
		}
		
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		setCorpTaxDefaultValue(vo, corpvo);
		return vo;
	}
	
	//默认值
	private void setCorpTaxDefaultValue(CorpTaxVo vo, CorpVO corpVO){
//		CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
//				CorpVO.class);
		if(corpVO == null)
			return;
		vo.setChargedeptname(corpVO.getChargedeptname());
		String yhzc = vo.getVyhzc();//优惠政策
		Integer comtype = corpVO.getIcompanytype();
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
//		DZFDouble educaddtax = vo.getEducaddtax();//教育费附加
//		if(educaddtax == null){
//			vo.setEducaddtax(new DZFDouble(0.03));
//		}

		//设置报税地区默认值
		if (vo.getTax_area() == null) {
			Integer city = corpVO.getVcity();
			if (city != null && (city == 151 || city == 171 || city == 234)) { //3个单独申报地区(市)（厦门、青岛、深圳）
				vo.setTax_area(city);
			} else if (corpVO.getVprovince() != null) { //省
				vo.setTax_area(corpVO.getVprovince());
			}
		}

		String corptype = corpVO.getCorptype();
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
	
	private void dealChargeHis(CorpTaxVo vo, 
			CorpTaxVo oldvo, 
			List<TaxEffeHistVO> hiss,
			String pk_corp,
			StringBuffer msg){
		Integer taxtype = vo.getTaxlevytype();//征收方式
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFDate begindate = corpvo.getBegindate();
		String jzper = DateUtils.getPeriod(begindate);
		
		//旧值
		Integer oldcomtype = corpvo.getIcompanytype();
		Integer oldTaxType = oldvo.getTaxlevytype();
		
		//新值
		Integer newcomtype = vo.getIcompanytype();
		Integer newTaxType = vo.getTaxlevytype();
		Integer newComTaxtype = vo.getIncomtaxtype();
		
		if(oldcomtype == null || oldcomtype.intValue() != newcomtype){
			msg.append("公司类型;");
		}
		if(oldTaxType == null || oldTaxType.intValue() != newTaxType){
			msg.append("征收方式;");
		}
		
		if(taxtype != null && taxtype == 0){//核定征收
			//重新设值
			vo.setBegprodate(oldvo.getBegprodate());//开始经营生产时间为空
			//旧值
			String oldBeginPer = oldvo.getSxbegperiod();
			String oldEndPer = oldvo.getSxendperiod();
			DZFDouble oldtaxrate = oldvo.getIncometaxrate();
			Integer oldVerIme = oldvo.getVerimethod();
			//新值
			String beginPer = vo.getSxbegperiod();
			String endPer = vo.getSxendperiod();
			DZFDouble taxrate = vo.getIncometaxrate();
			Integer newVerIme = vo.getVerimethod();
			
			if(!beginPer.equals(oldBeginPer)){
				msg.append("生效开始期间;");
			}
			if(!endPer.equals(oldEndPer)){
				msg.append("生效结束期间;");
			}
			
//			if(beginPer.equals(oldBeginPer) && endPer.equals(oldEndPer)){
//				return;
//			}
			
			if(oldtaxrate == null 
					|| SafeCompute.div(oldtaxrate, taxrate).doubleValue() != 0){
				msg.append("应税所得率;");
			}
			
			if(oldVerIme == null 
					|| oldVerIme.intValue() != newVerIme){
				msg.append("核定征收方式;");
			}
			
			if(StringUtil.isEmpty(beginPer)
					|| StringUtil.isEmpty(endPer)){
				throw new BusinessException("核定征收时，生效期间不允许为空");
			}else if(jzper.compareTo(beginPer) > 0 ){
				throw new BusinessException("核定征收时，生效开始期间不允许早于建账期间");
			}else if(beginPer.compareTo(endPer) > 0){
				throw new BusinessException("核定征收时，生效开始期间不允许晚于生效结束期间");
			}
			
			DZFDouble sdsl = SafeCompute.add(taxrate, DZFDouble.ZERO_DBL);
			if(sdsl.doubleValue() <=0 || sdsl.doubleValue() >=100){
				throw new BusinessException("应税所得率请录入0-100之间的数值");
			}
//			else if(!endPer.endsWith("12")){
//				throw new BusinessException("核定征收时，生效结束期间必须是年末最后期间");
//			}
			
			checkValidBefSave(pk_corp, beginPer, endPer);
			
			if(hiss == null || hiss.size() == 0){
				if(beginPer.equals(jzper)){
					TaxEffeHistVO vo1 = new TaxEffeHistVO();
					BeanUtils.copyNotNullProperties(vo, vo1);
					vo1.setPrimaryKey(null);
					singleObjectBO.saveObject(pk_corp, vo1);
				}else{
					TaxEffeHistVO vo2 = new TaxEffeHistVO();
					vo2.setPk_corp(pk_corp);
					vo2.setIncomtaxtype(0);
					vo2.setTaxlevytype(1);
					vo2.setSxbegperiod(jzper);
					vo2.setSxendperiod(DateUtils.getPreviousPeriod(beginPer));//往前一个月
					
					TaxEffeHistVO vo3 = new TaxEffeHistVO();
					BeanUtils.copyNotNullProperties(vo, vo3);
					vo3.setPrimaryKey(null);
					TaxEffeHistVO[] vos = {vo2, vo3};
					singleObjectBO.insertVOArr(pk_corp, vos);
				}
			}else{
				TaxEffeHistVO vo4 = hiss.get(0);
				Integer taxType4 = vo4.getTaxlevytype();
				String effendPer = vo4.getSxendperiod();
				if(taxType4 == 0){//核定征收
					if(effendPer.compareTo(beginPer) >= 0 ){
						throw new BusinessException("修改后的生效开始期间必须晚于历史记录中最大结束期间");
					}
					String newp = DateUtils.getPreviousPeriod(beginPer);
					if(!newp.equals(effendPer)){
						throw new BusinessException("修改后的生效开始期间必须与历史记录中最大结束期间连续");
					}
					
				}
				
				String effBegPer = vo4.getSxbegperiod();
				if(taxType4 == 1 && effBegPer.compareTo(beginPer) >= 0){//查账征收
					throw new BusinessException("修改后的生效开始期间必须晚于历史记录中最大开始期间");
				}
				
				TaxEffeHistVO vo5 = new TaxEffeHistVO();
				BeanUtils.copyNotNullProperties(vo, vo5);
				vo5.setPrimaryKey(null);
				singleObjectBO.saveObject(pk_corp, vo5);
				
				if(taxType4 == 1){
					DZFDate d = DateUtils.getPeriodStartDate(beginPer);
					d = d.getDateBefore(1);
					vo4.setSxendperiod(DateUtils.getPeriod(d));
					singleObjectBO.update(vo4, new String[]{ "sxendperiod" });
				}
				
			}
			
		}else{
			DZFDate oldBegpro = oldvo.getBegprodate();
			
			vo.setVerimethod(null);
			vo.setIncometaxrate(null);
			
			DZFDate begPro = vo.getBegprodate();
			if(newComTaxtype != null && newComTaxtype == TaxRptConstPub.INCOMTAXTYPE_GR
//					&& vo.getIsbegincom() != null && vo.getIsbegincom().booleanValue()
					)
			{
				if(begPro == null){
					throw new BusinessException("查账征收时，开始生产经营日期不允许为空");
				}
				if(begPro.after(begindate)){
					throw new BusinessException("查账征收时，开始生产经营日期不能晚于建账日期");
				}
			}
			
			
			if(newComTaxtype == TaxRptConstPub.INCOMTAXTYPE_GR &&
					(oldBegpro == null || !oldBegpro.equals(begPro))){
				msg.append("开始生产经营日期;");
			}
			
			if(hiss == null || hiss.size() == 0){
				vo.setSxbegperiod(jzper);//生效开始期间 
				
				TaxEffeHistVO vo1 = new TaxEffeHistVO();
				BeanUtils.copyNotNullProperties(vo, vo1);
				vo1.setSxbegperiod(jzper);
				vo1.setSxendperiod(null);
				vo1.setPrimaryKey(null);
				singleObjectBO.saveObject(pk_corp, vo1);
			}else{
				TaxEffeHistVO vo2 = hiss.get(0);
				Integer taxType4 = vo2.getTaxlevytype();
				String effendPer = vo2.getSxendperiod();
				if(taxType4 != null && taxType4 == 0){//核定征收
					TaxEffeHistVO vo3 = new TaxEffeHistVO();
					BeanUtils.copyNotNullProperties(vo, vo3);
					DZFDate d = DateUtils.getPeriodEndDate(effendPer);
					d = d.getDateAfter(1);
					String per = DateUtils.getPeriod(d);
					vo3.setSxbegperiod(per);
					vo3.setPrimaryKey(null);
					singleObjectBO.saveObject(pk_corp, vo3);
					
					vo.setSxbegperiod(per);
				}
				
			}
		}
	}
	
	private void dealSpecialDeductionHis(CorpTaxVo vo, 
			CorpTaxVo oldvo, 
			List<SpecDeductHistVO> hiss,
			String pk_corp,
			StringBuffer msg) {
		if(DZFValueCheck.isEmpty(vo.getBgperiod())){
			throw new BusinessException("变更日期不能为空");
		}
		
		Integer taxtype = vo.getTaxlevytype();// 征收方式
		msg.append("专项扣除;");
		if (taxtype != null && taxtype == 0) {//核定征收

		} else {
			checkValidBefSave(pk_corp, vo.getBgperiod(), "2099-12");
			if (hiss == null || hiss.size() == 0) {
				SpecDeductHistVO vo1 = new SpecDeductHistVO();
				BeanUtils.copyNotNullProperties(vo, vo1);
				vo1.setPrimaryKey(null);
				singleObjectBO.saveObject(pk_corp, vo1);
			} else {
				SpecDeductHistVO soldvo = null;
				for(SpecDeductHistVO svo :hiss){
					if(vo.getBgperiod().equals(svo.getBgperiod())){
						soldvo = svo;
						break;
					}
				}
				SpecDeductHistVO vo3 = new SpecDeductHistVO();
				BeanUtils.copyNotNullProperties(vo, vo3);
				if(soldvo !=null){
					vo3.setPrimaryKey(soldvo.getPk_specdeduct_his());
				}else{
					vo3.setPrimaryKey(null);
				}
				singleObjectBO.saveObject(pk_corp, vo3);
			}
		}
	}

	@Override
	public void saveCharge(CorpTaxVo vo, 
			String pk_corp, 
			String userid,
			StringBuffer msg) throws DZFWarpException {
		CorpTaxVo oldvo = queryCorpTaxVO(pk_corp);
		
		List<TaxEffeHistVO> hiss = corpTaxact.queryChargeHis(pk_corp);
		dealChargeHis(vo, oldvo, hiss, pk_corp, msg);
		//选中变更期间  增加专项扣除记录
		if(DZFValueCheck.isNotEmpty(vo.getBgperiod())){
			List<SpecDeductHistVO> hisss = querySpecChargeHis(pk_corp);
			dealSpecialDeductionHis(vo, oldvo, hisss, pk_corp, msg);
		}
		
		if(oldvo != null && !StringUtil.isEmpty(oldvo.getPrimaryKey())){
			vo.setPrimaryKey(oldvo.getPrimaryKey());
			List<String> upfs = new ArrayList<String>();
			upfs.add("incomtaxtype");//所得税类型
			upfs.add("taxlevytype");//征收方式
			upfs.add("sxbegperiod");
			upfs.add("sxendperiod");
			upfs.add("incometaxrate");//所得税税率
			upfs.add("begprodate");
			upfs.add("verimethod");
			
			singleObjectBO.update(vo, upfs.toArray(new String[0]));
		}else{
			vo.setPrimaryKey(null);
			vo.setPk_corp(pk_corp);
			saveNew(vo);
		}
		
		CorpVO corpvo = new CorpVO();
		corpvo.setPk_corp(pk_corp);
		corpvo.setIcompanytype(vo.getIcompanytype());
		singleObjectBO.update(corpvo, new String[]{"icompanytype"});
	}
	
	private void checkValidBefSave(String pk_corp, String beginPer, String endPer){
		//校验期末处理计提所得税
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = "select max(period) from  ynt_qmcl where nvl(dr,0) = 0 and pk_corp = ? and nvl(qysdsjz,'N') = 'Y' ";
		String maxPer = (String) singleObjectBO.executeQuery(sql, 
				sp, new ColumnProcessor());
		
		if(!StringUtil.isEmpty(maxPer) 
				&& (maxPer.compareTo(beginPer) >= 0
						|| maxPer.compareTo(endPer) >= 0)){
			throw new BusinessException("期间:" + maxPer + "已计提所得税,请检查");
		}
		
		//校验税表
//		List<TaxReportVO> taxlist = taxDeclarationService.queryTaxReprotVOs(
//				begPer, endPer, pk_corp, null);
//		if(taxlist != null && taxlist.size() > 0){
//			throw new BusinessException("期间:" + taxlist.get(0).getPeriod() + "存在申报报表,请检查");
//		}
	}

	private void cascadeUpHis(String pk_corp, TaxEffeHistVO effvo){
		CorpTaxVo taxvo = queryCorpTaxVO(pk_corp);
		if(StringUtil.isEmpty(taxvo.getPrimaryKey())){
			return;
		}
		
		String[] fields = { "incomtaxtype", "taxlevytype", 
				"sxbegperiod", "sxendperiod", 
				"verimethod", "incometaxrate"};
		taxvo.setIncomtaxtype(effvo.getIncomtaxtype());
		taxvo.setTaxlevytype(effvo.getTaxlevytype());
		taxvo.setSxbegperiod(effvo.getSxbegperiod());
		taxvo.setSxendperiod(effvo.getSxendperiod());
		taxvo.setVerimethod(effvo.getVerimethod());
		taxvo.setIncometaxrate(effvo.getIncometaxrate());
		
		
		singleObjectBO.update(taxvo, fields);
	}

	@Override
	public void deletechargHis(String pk_corp, String pk) throws DZFWarpException {
		//先查询  后删除
		List<TaxEffeHistVO> efflist = corpTaxact.queryChargeHis(pk_corp);
		if(efflist == null || efflist.size() == 0){
			cascadeUpHis(pk_corp, new TaxEffeHistVO());
		}

		TaxEffeHistVO vo = efflist.get(0);
		String key = vo.getPrimaryKey();
		if(!key.equals(pk)){
			throw new BusinessException("历史记录请按顺序删除");
		}
		
		TaxEffeHistVO firstVO = new TaxEffeHistVO();
		TaxEffeHistVO secondVO = null;
		if(efflist.size() > 1){
			firstVO = efflist.get(1);
			secondVO = efflist.get(1);
			
			Integer taxtype = secondVO.getTaxlevytype();
			if(taxtype == null || taxtype == 0){//核定征收
				secondVO = null;
			}
			
		}
		
		String begPer = vo.getSxbegperiod();
		String endPer = vo.getSxendperiod();
		if(!StringUtil.isEmpty(begPer)){
			if(StringUtil.isEmpty(endPer)){
				endPer = "2099-12";
			}
			checkValidBefSave(pk_corp, begPer, endPer);
		}
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk);
		String sql = "delete from ynt_taxeff_his y where y.pk_corp = ? and y.pk_taxeff_his = ? ";
		singleObjectBO.executeUpdate(sql, sp);
		
		cascadeUpHis(pk_corp, firstVO);
		
		if(secondVO != null){
			secondVO.setSxendperiod(null);
			singleObjectBO.update(secondVO, new String[]{ "sxendperiod" });
		}
	}
	
	 public List querySpecChargeHis(String pk_corp)
		        throws DZFWarpException
	{
		String sql = " select * from ynt_specdeduct_his y where nvl(dr,0)=0 and pk_corp = ? order by bgperiod desc ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List list = (List) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(SpecDeductHistVO.class));
		return list;
	}

	@Override
	public void deleteSpecChargHis(String pk_corp, String pk) throws DZFWarpException {
		// 先查询 后删除
		List<SpecDeductHistVO> efflist = querySpecChargeHis(pk_corp);
		if (efflist == null || efflist.size() == 0) {
			return;
		}

		SpecDeductHistVO vo = efflist.get(0);
		String key = vo.getPrimaryKey();
		if (!key.equals(pk)) {
			throw new BusinessException("历史记录请按顺序删除");
		}
		
		String begPer = vo.getBgperiod();
		if (!StringUtil.isEmpty(begPer)) {
				String endPer = "2099-12";
			checkValidBefSave(pk_corp, begPer, endPer);
		}

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk);
		String sql = "delete from ynt_specdeduct_his y where y.pk_corp = ? and y.pk_specdeduct_his = ? ";
		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public List<SpecDeductHistVO> querySpecChargeHis(String pk_corp, String period) throws DZFWarpException {
		String year = period.substring(0, 4);
		StringBuilder sql = new StringBuilder();
		sql.append("select * from ynt_specdeduct_his  where nvl(dr,0)=0 and pk_corp = ? ")
				.append("  and bgperiod like ? and bgperiod <= ? ")
				.append(" union ")
				.append("select * from (select * from ynt_specdeduct_his where nvl(dr,0)=0 and pk_corp = ? ")
				.append("and  bgperiod < ?  order by bgperiod desc )")
				.append(" where rownum = 1 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(year + "%");
		sp.addParam(period);
		sp.addParam(pk_corp);
		sp.addParam(year + "-01");
		List<SpecDeductHistVO> list = (List<SpecDeductHistVO>) singleObjectBO
				.executeQuery(sql.toString(), sp, new BeanListProcessor(SpecDeductHistVO.class));
		list.sort((o1, o2) -> (o2.getBgperiod().compareTo(o1.getBgperiod())));
		return list;
	}

	@Override
	public CorpTaxVo queryCorpTaxVOByType(String pk_corp, String type) throws DZFWarpException {
		CorpTaxVo vo =queryCorpTaxVO(pk_corp);
		if("specHis".equals(type)){
			setZxKc(vo, pk_corp);
		}else{
			if(vo.getTaxlevytype() == 1 && vo.getIncomtaxtype()==1){
				setZxKc(vo, pk_corp);
			}
		}
		return vo;
	}
	
	private void setZxKc(CorpTaxVo vo, String pk_corp) {
		List list = querySpecChargeHis(pk_corp);
		if (list == null || list.size() == 0)
			return;
		SpecDeductHistVO svo = (SpecDeductHistVO) list.get(0);
		vo.setYanglaobx(svo.getYanglaobx());
		vo.setYiliaobx(svo.getYiliaobx());
		vo.setShiyebx(svo.getShiyebx());
		vo.setZfgjj(svo.getZfgjj());
		vo.setBgperiod(svo.getBgperiod());
		DZFDouble zxkcxj = SafeCompute.add(vo.getYanglaobx(), vo.getYiliaobx());
		zxkcxj = SafeCompute.add(zxkcxj, vo.getShiyebx());
		zxkcxj = SafeCompute.add(zxkcxj, vo.getZfgjj());
		vo.setZxkcxj(zxkcxj);

	}

}

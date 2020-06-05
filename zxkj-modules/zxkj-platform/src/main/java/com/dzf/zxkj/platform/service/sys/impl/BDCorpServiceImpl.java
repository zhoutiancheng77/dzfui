package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.*;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IRoleCodeCont;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.entity.ICodeName;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.icset.InvAccSetVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.icset.MeasureVO;
import com.dzf.zxkj.platform.model.image.ImageCommonPath;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.model.tax.CorpTaxInfoVO;
import com.dzf.zxkj.platform.service.common.IReferenceCheck;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.util.CryptCodeUtil;
import com.dzf.zxkj.platform.util.PinyinUtil;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.secret.CorpSecretUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公司目录
 * 
 * @author zhangj
 * 
 */
@Slf4j
@Service("sys_corpserv")
@SuppressWarnings("all")
public class BDCorpServiceImpl implements IBDCorpService {
	@Autowired
	private SingleObjectBO singleObjectBO = null;

	@Autowired
	private ICorp corpImpl = null;
	
	@Autowired
	private IUserService userServiceImpl;
	
	@Autowired
	private ICorpURoleService corpUserRoleImpl;
	
	@Autowired
	private IBillCodeService billCodeServiceImpl;
	
	@Autowired
    private IReferenceCheck refchecksrv;

	@Autowired
	private ICorpService corpService;

    @Autowired
    IInventoryAccSetService  accSetService;

    @Autowired
    private IInventoryAccSetService gl_ic_invtorysetserv;
	@SuppressWarnings("unchecked")
	public CorpVO[] queryCorp(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		// 根据查询条件查询公司的信息
		// StringBuffer corpsql = new StringBuffer();
		String sql = getQuerySql(queryvo, uservo,1);
		SQLParameter param = getQueryParam(queryvo, uservo);
//		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), param,
//				new BeanListProcessor(CorpVO.class));
		List<CorpVO> vos = (List<CorpVO>)queryDataPage(CorpVO.class,sql.toString(),param,queryvo.getPage(), queryvo.getRows(),null);
		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname",
		        "pcountname", "wqcountname", "vcorporatephone","unitdistinction","editusname" }, vos, 1);
//		String corpname = queryvo.getCorpname();
//		if (!StringUtil.isEmpty(corpname)) {
//			corpname = corpname.trim();
//			if (vos != null && vos.size() > 0) {
//				CorpVO[] cvos = vos.toArray(new CorpVO[0]);
//				List<CorpVO> list = new ArrayList<>();
//				String enf_name = null;
//				String en_name = null;
//				String unitname = null;
//				for (CorpVO cvo : cvos) {
//					unitname = cvo.getUnitname();
//					if (!StringUtil.isEmpty(unitname)) {
//						en_name = PinyinUtil.getFullSpell(unitname);
//						enf_name = PinyinUtil.getFirstSpell(unitname);
//					}
//					if (unitname.contains(corpname) || en_name.contains(corpname) || enf_name.contains(corpname)) {
//						list.add(cvo);
//					}
//				}
//				return list.toArray(new CorpVO[0]);
//			}
//		}
		return vos.toArray(new CorpVO[0]);
	}
	
	/** 分页查询
     * @param className
     * @param sql 查询语句
     * @param params 条件参数
     * @param pageNo 页数
     * @param pageSize 每页记录条数
     * @param order 排序字段
      */
    public List<?> queryDataPage(Class className, String sql, SQLParameter params, int pageNo, int pageSize, String order) throws DZFWarpException {
        StringBuffer qrysql = new StringBuffer();
        qrysql.append(" select * from ( SELECT ROWNUM AS ROWNO, tt.* FROM ( ");
        qrysql.append(sql);
        if (order != null) {
            qrysql.append(" order by " + order + " desc) tt WHERE ROWNUM<="
                    + pageNo * pageSize);
        } else {
            qrysql.append(" ) tt WHERE ROWNUM<=" + pageNo * pageSize + " ");
        }
        qrysql.append(" ) WHERE ROWNO> " + (pageNo - 1) * pageSize + " ");
        return (List<?>)singleObjectBO.executeQuery(qrysql.toString(), params, new BeanListProcessor(className));
    }
    /**
     * 根据条件 获取总条数
     * @param sql ： select count(*) from table
     * @param params
     * @return
     * @throws DAOException
     */
    public int getDataTotal(String sql,SQLParameter params) throws DZFWarpException{
        Object obj = singleObjectBO.executeQuery(sql, params, new ColumnProcessor());
        if(obj == null){
            return 0;
        }
        return Integer.parseInt(obj.toString());
//      return new Integer((int)(obj == null ? 0 :obj.toString()));
    }

	/**
	 * 拼装我的客户查询信息
	 * 
	 * @param queryvo
	 * @return
	 */
	private String getQuerySql(QueryParamVO queryvo, UserVO uservo,int total) {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		if(total == 1){
		    corpsql.append("select a.*, ");
//		corpsql.append(" b.unitname as def1 ,");
//		corpsql.append(" b.def3      		,");
//		corpsql.append(" b.def2     		,");
		    corpsql.append(" y.accname as ctypename      ,");
//		corpsql.append(" t.tradecode || t.tradename as indusname    ,");
		    corpsql.append(" t.tradename as indusname    ,");
		    corpsql.append(" u.user_name as pcountname   ,");//
		    corpsql.append(" us.user_name as wqcountname , ");//
		    corpsql.append(" su.user_name as editusname , ");//
		    corpsql.append(" ctax.dcoachbdate ,");
            corpsql.append(" ctax.dcoachedate ,");
		    corpsql.append(" t.tradecode as vtradecode	");   //国家标准行业编码
		}else if(total == 2){
		    corpsql.append("select count(a.pk_corp) ");
		}
		corpsql.append(" from bd_corp a");

		corpsql.append(" join bd_account b on a.fathercorp = b.pk_corp");
		corpsql.append(" left join bd_corp_tax ctax on ctax.pk_corp = a.pk_corp");
		corpsql.append(" left join ynt_tdaccschema y on a.corptype = y.pk_trade_accountschema");// 查询科目方案的名称
		corpsql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade");
		corpsql.append(" left join sm_user u on a.vsuperaccount = u.cuserid");// 关联用户表--主管会计
		corpsql.append(" left join sm_user us on a.vwqaccount = us.cuserid");// 关联用户表--外勤会计
		corpsql.append(" left join sm_user su on a.def18 = su.cuserid");// 关联用户表--最后修改人
		if(!StringUtil.isEmpty(queryvo.getAsname())){
            corpsql.append(" join ynt_corpimpress cip on cip.pk_corp = a.pk_corp and cip.vname = '"+queryvo.getAsname()+"'");
        }
		// 添加权限过滤
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		corpsql.append(" where nvl(a.dr,0) =0 and a.fathercorp = ? and b.pk_corp = ?");

		if(queryvo.getIshassh()==null||!queryvo.getIshassh().booleanValue()){
			corpsql.append(" and nvl(a.isseal,'N') = 'N' ");
		}
		if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
            corpsql.append(" and nvl(a.ishasaccount,'N') = 'Y' ");
        }
		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			corpsql.append(" and a.innercode like ? ");
		}
		if(!StringUtil.isEmptyWithTrim(queryvo.getCorpname())){
		    corpsql.append(" and instr(a.rcunitname,?) > 0 ");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			corpsql.append(" and (a.createdate >= ? and a.createdate <= ? )");
		}else if(queryvo.getBegindate1() != null){
            corpsql.append(" and a.createdate >= ? ");
        }else if(queryvo.getEnddate() != null){
            corpsql.append(" and a.createdate <= ? ");
        }
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			corpsql.append(" and (a.begindate >= ? and a.begindate <= ? )");
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
		if (!ismanager.booleanValue()) {
			corpsql.append(" and (a.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
			corpsql.append(" or a.vsuperaccount = ? or a.coperatorid = ?)");
		}

		if(!StringUtil.isEmpty(queryvo.getVprovince())){
			corpsql.append(" and a.vprovince=? ");
		}
        if(queryvo.getMaintainedtax() != null && queryvo.getMaintainedtax() == 2){
            corpsql.append(" and a.ismaintainedtax = 'Y' ");
        }else if(queryvo.getMaintainedtax() != null && queryvo.getMaintainedtax() == 3){
            corpsql.append(" and nvl(a.ismaintainedtax,'N')='N' ");
        }       
		if(!StringUtil.isEmpty(queryvo.getKms_id())){//公司主键
			corpsql.append(" and a.pk_corp=? ");
		} 
		//是否激活dzfAPP
        if(queryvo.getIsdzfapp() != null && queryvo.getIsdzfapp() == 0){
            corpsql.append(" and exists (select * from ynt_corp_user where a.pk_corp = pk_corp and nvl(dr,0) = 0 and nvl(istate,2) = 2 and fathercorp = ?) ");
        }else if(queryvo.getIsdzfapp() != null && queryvo.getIsdzfapp() == 1){
            corpsql.append(" and not exists (select * from ynt_corp_user where a.pk_corp = pk_corp and nvl(dr,0) = 0 and nvl(istate,2) = 2 and fathercorp = ?) ");
        }
        if(queryvo.getIsywskp() != null && queryvo.getIsywskp() == 0){
            corpsql.append(" and nvl(ctax.isywskp,'N')= 'Y' ");
        }else if(queryvo.getIsywskp() != null && queryvo.getIsywskp() == 1){
            corpsql.append(" and nvl(ctax.isywskp,'N')= 'N' ");
        }
		if(queryvo.getIfwgs() != null && queryvo.getIfwgs() != -1){
			corpsql.append(" and nvl(a.ifwgs,0)= ? ");
		}
		if(queryvo.getIsformal() != null){
		    corpsql.append(" and nvl(a.isformal,'N') = ?");
		}
		Integer type = queryvo.getLevelq() == null ? 0 : queryvo.getLevelq();
        if(type == 1){//代账客户
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
        }else if(type == 9){//代理记账
            corpsql.append(" and instr(a.ecotype,1) > 0 ");
        }else if(type == 10){//增值服务
            corpsql.append(" and instr(a.ecotype,2) > 0 ");
        }
        if(!StringUtil.isEmpty(queryvo.getForeignname())){
            corpsql.append(" and a.foreignname like ? ");
        }
        if(!StringUtil.isEmpty(queryvo.getUserid())){
            corpsql.append(" and a.vsuperaccount = ? ");
        }
        if(!StringUtil.isEmpty(queryvo.getHc())){
            corpsql.append(" and EXISTS  ");
            corpsql.append("(SELECT DISTINCT ur.pk_corp  ") ;
            corpsql.append("  FROM sm_user_role ur  ") ; 
            corpsql.append("  LEFT JOIN sm_user r ON ur.cuserid = r.cuserid  ") ; 
            corpsql.append(" WHERE nvl(r.dr, 0) = 0  ") ; 
            corpsql.append("   AND nvl(ur.dr, 0) = 0  ") ; 
            corpsql.append("   AND nvl(r.locked_tag, 'N') = 'N'  ") ; 
            corpsql.append("   AND r.pk_corp = ? ");
            corpsql.append("   AND r.pk_department = ? ");
            corpsql.append("   AND a.pk_corp = ur.pk_corp ) ");
        }
		corpsql.append(" order by a.innercode");
		return corpsql.toString();
	}

	@Override
	public int queryCorpTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		String sql = getQuerySql(queryvo, uservo,2);
		SQLParameter param = getQueryParam(queryvo, uservo);
		return getDataTotal(sql,param);
//		corpsql.append(sql);
//		corpsql.append("  order by a.begindate desc");
//		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(corpsql.toString(), param,
//				new BeanListProcessor(CorpVO.class));
//		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname",
//				"def1", "def2", "def3", "pcountname", "wqcountname" }, vos, 1);
//		if (!StringUtil.isEmpty(queryvo.getCorpname())) {
//			if (vos != null && vos.size() > 0) {
//				CorpVO[] cvos = vos.toArray(new CorpVO[0]);
//				List<CorpVO> list = new ArrayList<>();
//				for (CorpVO cvo : cvos) {
//					if (cvo.getUnitname().contains(queryvo.getCorpname())) {
//						list.add(cvo);
//					}
//				}
//				return list.toArray(new CorpVO[0]);
//			}
//		}
//		return vos.toArray(new CorpVO[0]);
	}

	/**
	 * 根据公司主键查询 新增后列表显示只显示新增公司
	 */
	@Override
	public List<CorpVO> queryByPk(String[] pk_corp) throws DZFWarpException {
		List<CorpVO> corpList = new ArrayList<CorpVO>();
		for (int i = 0; i < pk_corp.length; i++) {
			CorpVO corp = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp[i]);
			
			if (corp != null) {
				StringBuffer hyquery = new StringBuffer();
				SQLParameter hypm = new SQLParameter();
				StringBuffer kmquery = new StringBuffer();
				SQLParameter kmpm = new SQLParameter();
				hyquery.append(" nvl(dr,0) = 0 and pk_trade in (select industry from bd_corp where pk_corp = ?) "); 
				hypm.addParam(pk_corp[i]);
				BDTradeVO[] tradeVOs = (BDTradeVO[]) singleObjectBO.queryByCondition(BDTradeVO.class, hyquery.toString(), hypm);
				if(tradeVOs != null && tradeVOs.length > 0){
					corp.setIndusname(tradeVOs[0].getTradename());// 国家标准行业名称
					corp.setVtradecode(tradeVOs[0].getTradecode());//国家标准行业编码
				}
				
				// 给科目方案名称赋值
				kmquery.append("select accname\n");
				kmquery.append("  from ynt_tdaccschema\n");
				kmquery.append(" where nvl(dr, 0) = 0\n");
				kmquery.append("   and pk_trade_accountschema in\n");
				kmquery.append("       (select corptype from bd_corp where pk_corp = ? )");
				kmpm.addParam(pk_corp[i]);
				String ctypeName = (String) singleObjectBO.executeQuery(kmquery.toString(), kmpm, new ColumnProcessor());
				corp.setCtypename(ctypeName);// 行业科目方案名称
				
				// 给账户相关信息赋值
				StringBuffer sql = new StringBuffer();
				SQLParameter sp = new SQLParameter();
				sql.append("nvl(dr,0) = 0 ");
				if (corp.getFathercorp() != null && !"".equals(corp.getFathercorp())) {
					sql.append(" and pk_corp = ?");
					sp.addParam(corp.getFathercorp());
				}
				AccountVO[] AccountVOs = (AccountVO[]) singleObjectBO.queryByCondition(AccountVO.class, sql.toString(),
						sp);
				if (AccountVOs != null && AccountVOs.length > 0) {
					corp.setDef1(AccountVOs[0].getUnitname());
					corp.setDef2(AccountVOs[0].getDef2());
					corp.setDef3(AccountVOs[0].getDef3());
//					corp.setForeignname(AccountVOs[0].getForeignname());
				}
				
				UserVO uservo = null;
				// 给主管会计名称赋值
				if (!StringUtil.isEmpty(corp.getVsuperaccount())) {
					uservo = userServiceImpl.queryUserJmVOByID(corp.getVsuperaccount());
					if(uservo != null){
						corp.setPcountname(uservo.getUser_name());
					}
				}

				// 给外勤会计名称赋值
				if (!StringUtil.isEmpty(corp.getVwqaccount())) {
					uservo = userServiceImpl.queryUserJmVOByID(corp.getVwqaccount());
					if(uservo != null){
						corp.setWqcountname(uservo.getUser_name());
					}
				}
				UserVO uvo = userServiceImpl.queryUserJmVOByID(corp.getDef6());
				if(uvo != null){
					corp.setDef7(uvo.getUser_name());
				}
				uvo = userServiceImpl.queryUserJmVOByID(corp.getDef18());//最后修改人
				if(uvo != null){
					corp.setEditusname(uvo.getUser_name());
				}
				
				//zpm
//				uvo = UserCache.getInstance().get(corp.getVtaxofficer(), null); //办税人员
//				if(uvo != null){
//					corp.setVtaxofficercode(uvo.getUser_code());
//					corp.setVtaxofficernm(uvo.getUser_name());
//				}
			}

			corpList.add(corp);
		}
		return corpList;
	}

	@Override
	public CorpVO queryByID(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String condition = " nvl(dr,0)=0 and pk_corp= ? ";
		CorpVO[] corp = (CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class, condition, sp);
		if (corp == null || corp.length == 0) {
			return null;
		}
		return corp[0];

	}

	@SuppressWarnings("unchecked")
	@Override
	public CorpVO[] queryCorpAndSelf(QueryParamVO queryvo) throws DZFWarpException {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		SQLParameter params = new SQLParameter();
		corpsql.append(" select * from bd_corp  ");
		corpsql.append(" where nvl(dr,0) = 0 and nvl(isdatacorp,'N') = 'N' and isseal = 'N' ");
		corpsql.append(" and (fathercorp = ? or pk_corp = ?)");
		params.addParam(queryvo.getPk_corp());
		params.addParam(queryvo.getPk_corp());
		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			corpsql.append(" and innercode like ? ");
			params.addParam("%" + queryvo.getCorpcode() + "%");
		}
		if (queryvo.getCorpname() != null && queryvo.getCorpname().trim().length() > 0) {
			corpsql.append(" and unitname like ? ");
			params.addParam("%" + queryvo.getCorpname() + "%");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			corpsql.append(" and (createdate >= ? and createdate <= ?)");
			params.addParam(queryvo.getBegindate1());
			params.addParam(queryvo.getEnddate());
		}
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			corpsql.append(" and (a.begindate >= ? and a.begindate <= ?)");
			params.addParam(queryvo.getBcreatedate());
			params.addParam(queryvo.getEcreatedate());
		}

		corpsql.append(" order by innercode ");
		List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(corpsql.toString(), params,
				new BeanListProcessor(CorpVO.class));
		CorpVO[] vos = null;
		if (list != null && list.size() > 0)
			vos = list.toArray(new CorpVO[0]);
		return vos;
	}

	/**
	 * 保存公司目录表
	 * 
	 * @param accountvo
	 * @throws BusinessException
	 */
	@Override
	public CorpVO insertCorpVO(CorpVO corpVO) throws DZFWarpException {
		checkBeforeSave(corpVO);
		corpVO.setUnitshortname(corpVO.getUnitname());
		if (corpVO.getIcostforwardstyle() != null) {//!"".equals(corpVO.getIcostforwardstyle()) && 
			corpVO.setIsworkingunit(DZFBoolean.TRUE);
		}
		if(corpVO.getDbirthday() != null){
            corpVO.setVmonthday(corpVO.getDbirthday().getStrMonth()+"-"+corpVO.getDbirthday().getStrDay());
        }
		String pk = corpImpl.insertCorp(corpVO, null);
		corpVO.setPk_corp(pk);

		// 新增附件VO数组拼装
        String uploadPath = ImageCommonPath.getCorpFilePath(corpVO.getUnitcode(), null);
		CorpDocVO[] vos = (CorpDocVO[]) corpVO.getChildren();
		if (vos != null && vos.length > 0) {
			for (CorpDocVO corpDocVO : vos) {
				corpDocVO.setPk_corp(pk);
				String vfilepath = uploadPath + File.separator + corpDocVO.getDocTemp();
				corpDocVO.setVfilepath(vfilepath);
				// singleObjectBO.saveObject(pk, corpDocVO);
			}
			singleObjectBO.insertVOArr(pk, vos);
		}
		if (corpVO.getPk_source() != null && !corpVO.getPk_source().equals("")) {
			updatePortoReal(corpVO.getPk_source(), corpVO.getPk_corp());
		}
		updateCorpTaxVO(corpVO);
		CorpVO fcorp = corpService.queryByPk(corpVO.getFathercorp());
		if(fcorp != null && fcorp.getIschannel() != null && fcorp.getIschannel().booleanValue()){
		    saveManagerPower(corpVO);
		}
		return corpVO;
	}
	
	/**
     * 新增客户，分配会计经理
     * @author gejw
     * @time 上午9:35:06
     * @param corpvo
     */
    private void saveManagerPower(CorpVO corpvo){
        ArrayList<String> list = queryManageUsers(corpvo.getFathercorp());
        if(list != null && list.size() > 0){
            ArrayList<UserRoleVO> listUR = new ArrayList<>();
            ArrayList<UserCorpVO> listUC = new ArrayList<>();
            UserCorpVO ucvo = null;
            UserRoleVO urvo = null;
            for(String str : list){
                ucvo = new UserCorpVO();
                ucvo.setPk_corp(corpvo.getFathercorp());
                ucvo.setPk_corpk(corpvo.getPk_corp());
                ucvo.setCuserid(str);
                listUC.add(ucvo);
                
                urvo = new UserRoleVO();
                urvo.setPk_corp(corpvo.getPk_corp());
                urvo.setCuserid(str);
                urvo.setPk_role(IRoleCodeCont.jms02_ID);
                listUR.add(urvo);
            }
            if(listUR != null && listUR.size() > 0){
                singleObjectBO.insertVOArr(corpvo.getFathercorp(), listUR.toArray(new UserRoleVO[0]));
            }
            
            if(listUC != null && listUC.size() > 0){
                singleObjectBO.insertVOArr(corpvo.getFathercorp(), listUC.toArray(new UserCorpVO[0]));
            }
        }
    }
    /**
     * 查询会计经理角色用户
     * @author gejw
     * @time 上午9:37:30
     * @param loginCorpID
     * @param uid
     * @return
     */
    private ArrayList<String> queryManageUsers(String loginCorpID){
        String sql = "select distinct cuserid from sm_userole where nvl(dr,0) = 0 and pk_corp = ? and pk_role = ? ";
        SQLParameter params = new SQLParameter();
        params.addParam(loginCorpID);
        params.addParam(IRoleCodeCont.jms02_ID);
        List<JMUserRoleVO> vos = (List<JMUserRoleVO>) singleObjectBO.executeQuery(sql, params, new BeanListProcessor(JMUserRoleVO.class));
        ArrayList<String> list = new ArrayList<>();
        if(vos != null && vos.size() > 0){
            for(JMUserRoleVO vo :vos){
                list.add(vo.getCuserid());
            }
        }
        return list;
     }
	
	
	/**
     * 保存纳税信息
     * @author gejw
     * @time 上午11:01:09
     * @throws DZFWarpException
     */
    private void saveCorpTaxVO(CorpVO corpVO) throws DZFWarpException{
        CorpTaxVo tvo = new CorpTaxVo();
        tvo.setPk_corp(corpVO.getPk_corp());
//        tvo.setDef16(corpVO.getDef16());
//        tvo.setVstateuname(corpVO.getVstateuname());
//        tvo.setVstatetaxpwd(corpVO.getVstatetaxpwd());
//        tvo.setIsywskp(corpVO.getIsywskp());
        tvo.setDcoachbdate(corpVO.getDcoachbdate());
        tvo.setDcoachedate(corpVO.getDcoachedate());
        singleObjectBO.saveObject(tvo.getPk_corp(), tvo);
    }

	public void updatePortoReal(String pk_customno, String pk_corpk) throws DZFWarpException {
		SQLParameter sq = new SQLParameter();
		StringBuffer sql = new StringBuffer();
		sql.append("update YNT_POTCUS set pk_corpk=?,FLWSTATUS=1 where PK_CUSTOMNO=?");
		sq.addParam(pk_corpk);
		sq.addParam(pk_customno);
		singleObjectBO.executeUpdate(sql.toString(), sq);
	}

	/**
	 * 保存增值客户
	 * 
	 * @param accountvo
	 * @throws BusinessException
	 */
	@Override
	public CorpVO insertZzCorpVO(CorpVO corpVO) throws DZFWarpException {
		checkBeforeZzSave(corpVO);
		corpVO.setUnitshortname(corpVO.getUnitname());
		if (corpVO.getIcostforwardstyle() != null) {//!"".equals(corpVO.getIcostforwardstyle()) &&
			corpVO.setIsworkingunit(DZFBoolean.TRUE);
		}
		String pk = corpImpl.insertCorp(corpVO, null);
		corpVO.setPk_corp(pk);
		return corpVO;
	}

	@Override
	public void updateCorpVO(CorpVO corpVO) throws DZFWarpException {
		if (StringUtil.isEmpty(corpVO.getUnitshortname())) {
			corpVO.setUnitshortname(corpVO.getUnitname());
		}
		if(corpVO.getDbirthday() != null){
            corpVO.setVmonthday(corpVO.getDbirthday().getStrMonth()+"-"+corpVO.getDbirthday().getStrDay());
        }
		checkBeforeSave(corpVO);
		checkIsOnly(corpVO, false);
//		DZFBoolean ischange = isChangeKJRY(corpVO);
//		CorpVO oldvo = queryByID(corpVO.getPk_corp());
		if(!StringUtil.isEmpty(corpVO.getChargedeptname()) && corpVO.getChargedeptname().equals("小规模纳税人")){
		    corpVO.setDrdsj(null);
		    corpVO.setIsxrq(null);
		}
		String unitname = CorpSecretUtil.deCode(corpVO.getUnitname());
        corpVO.setRcunitname(CryptCodeUtil.enCode(unitname));
		singleObjectBO.update(corpVO, getUpdateFields());

		updateNcompany(corpVO);

		updateCorpTaxVO(corpVO);
	}
	
	/**
     * 修改纳税信息
     * @author gejw
     * @time 上午11:01:09
     * @throws DZFWarpException
     */
    private void updateCorpTaxVO(CorpVO corpVO) throws DZFWarpException{
        String condition = " nvl(dr,0) = 0 and pk_corp = ?";
        SQLParameter params = new SQLParameter();
        params.addParam(corpVO.getPk_corp());
        CorpTaxVo[] tvos = (CorpTaxVo[]) singleObjectBO.queryByCondition(CorpTaxVo.class, condition, params);
        if(tvos != null && tvos.length > 0){
            CorpTaxVo tvo = tvos[0];
//            tvo.setDef16(corpVO.getDef16());
//            tvo.setVstateuname(corpVO.getVstateuname());
//            tvo.setVstatetaxpwd(corpVO.getVstatetaxpwd());
//            tvo.setIsywskp(corpVO.getIsywskp());
            tvo.setDcoachbdate(corpVO.getDcoachbdate());
            tvo.setDcoachedate(corpVO.getDcoachedate());
            singleObjectBO.update(tvo, new String[]{"dcoachbdate","dcoachedate"});
        }else{
            saveCorpTaxVO(corpVO);
        }
        
    }
	
	/**
	 * 同步创业公司
	 * @author gejw
	 * @time 下午1:56:15
	 */
	private void updateNcompany(CorpVO corpVO){
	    String sql = " update fat_ncompany set vnname = ? ,vbankname=?,vbankcode=?,vaddr=?,phone=? where pk_corp = ?";
	    SQLParameter params = new SQLParameter();
	    params.addParam(CryptCodeUtil.enCode(CorpSecretUtil.deCode(corpVO.getUnitname())));
	    params.addParam(corpVO.getVbankname());
	    params.addParam(CryptCodeUtil.enCode(corpVO.getVbankcode()));
	    params.addParam(corpVO.getPostaddr());
	    params.addParam(CryptCodeUtil.enCode(CorpSecretUtil.deCode(corpVO.getVcorporatephone())));
	    params.addParam(corpVO.getPk_corp());
	    singleObjectBO.executeUpdate(sql, params);
	}
	
	
	
	
	private boolean checkIsOnly(CorpVO condCorpVO, boolean isInsert)
			throws DZFWarpException {
		StringBuilder sbCode = new StringBuilder(
				"select count(1) from bd_corp where innercode='").append(
				condCorpVO.getInnercode()).append("' and fathercorp = '"+condCorpVO.getFathercorp()+"'");
		StringBuilder sbName = new StringBuilder(
				"select count(1) from bd_corp where unitname='").append(
				condCorpVO.getUnitname()).append("' and fathercorp = '"+condCorpVO.getFathercorp()+"'");
		if (!isInsert) {
			sbCode.append(" and pk_corp!='").append(condCorpVO.getPrimaryKey())
					.append("'");
			sbName.append(" and pk_corp!='").append(condCorpVO.getPrimaryKey())
					.append("'");
		}
		BigDecimal ojbCodeNum = (BigDecimal) singleObjectBO.executeQuery(
				sbCode.toString(), null, new ColumnProcessor());
		Integer repeatCodeNum = ojbCodeNum.intValue();
		BigDecimal objNameNum = (BigDecimal) singleObjectBO.executeQuery(
				sbName.toString(), null, new ColumnProcessor());
		Integer repeatNameNum = objNameNum.intValue();

		if (repeatCodeNum > 0) {
			throw new BusinessException("客户编码不能重复。");
		} else if (repeatNameNum > 0) {
			throw new BusinessException("客户名称不能重复。");
		}
		return false;
	}
    /**
     * 益世  业务已停，暂时废弃
     * @author gejw
     * @time 上午9:46:31
     * @param corpVO
     * @param ischange
     */
//	private void sendServePsnInfo(CorpVO corpVO, DZFBoolean ischange) {// WJX
//
//		// 保存后，判断是否调用益世接口（返回会计人员信息）---begin
//		try {
//			if ("益世财税".equals(corpVO.getVcustsource()) && !ischange.booleanValue()) { // 公司来源是益世推送,且主管会计或外勤会计改变了，调用接口
//				Object servePsgInfoImpl = BeanUtils.getBean("yspsnsrv");
//				Method m = servePsgInfoImpl.getClass().getMethod("servePsnInfoUpload", CorpVO.class);
//				m.invoke(servePsgInfoImpl, corpVO);
//				// servePsgInfoImpl.servePsnInfoUpload(corpVO);
//			}
//
//		} catch (Exception e) {
//			log.error("调用益世接口报错", e);// 调用益世接口，如有异常，此处不向上层抛出
//			String msg = "调用益世接口异常";
//			if(e instanceof InvocationTargetException){
//				InvocationTargetException ee = (InvocationTargetException)e;
//				if(ee.getTargetException() instanceof BusinessException){
//					msg = ee.getTargetException().getMessage();
//				}			
//			}			
//			throw new BusinessException(msg);
//		}
//
//		// ---end
//	}
//
//	private DZFBoolean isChangeKJRY(CorpVO vo) {// WJX
//		StringBuffer sql = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(vo.getPk_corp());
//		sql.append(" select count(1) ");
//		sql.append(" from bd_corp b ");
//		sql.append(" where  b.pk_corp = ? and nvl(b.dr,0)=0");
//		if (!StringUtil.isEmpty(vo.getVsuperaccount())) {
//			sp.addParam(vo.getVsuperaccount());
//			sql.append(" and b.vsuperaccount = ?");
//		}
//		if (!StringUtil.isEmpty(vo.getVwqaccount())) {
//			sp.addParam(vo.getVwqaccount());
//			sql.append("  and b.vwqaccount = ? ");
//		}
//		BigDecimal count = (BigDecimal) singleObjectBO.executeQuery(sql.toString(), sp, new ColumnProcessor());
//		return count.intValue() == 0 ? DZFBoolean.FALSE : DZFBoolean.TRUE;
//	}

	/**
	 * 界面更新字段
	 * 
	 * @return
	 */
    private String[] getUpdateFields() {
        String[] updateFields = new String[] {
                //客户编码、客户名称、单位地址、个人用户
                "innercode","unitname","unitshortname", "postaddr","ispersonal",
                //省、市、区、客户来源、客户来源说明、销售人员
                 "vprovince", "vcity", "varea","vcustsource", "vsourcenote","foreignname",
                // 开户银行、账号、银行地址、银行位置、
                 "vbankname", "vbankcode", "vbankaddr","vbankpos", 
                // 客户简介、申请科委小巨人基金、授权企业主打印导出、原客户名称、客户联系人
                 "briefintro","def8","unitdistinction","linkman2",//"iskwxjr",
                //联系人手机、老板手机号、法人电话、 微信号、电子邮件、客户其他联系方式、
                 "phone1", "phone2","vcorporatephone","linkman3","email1","vcustothertel",
                 //行业、会计制度、成本结转类型、图片处理方式、主管会计、行业
                 "industry","corptype", "icostforwardstyle", "def4","vsuperaccount","def20",
                 //社会信用代码、法人代表、公司类型、成立日期、核准日期、证件类型
                 "vsoccrecode","legalbodycode","icompanytype","destablishdate","dapprovaldate", "def15",
                 //法人身份证号、注册资本、登记机关、 住所、经营范围、
                 "vcorporationid","def9","vregistorgans","saleaddr","vbusinescope", 
                 // 公司性质、一般人生效日期、一般人认定日期、最后修改人、最后修改时间
                 "chargedeptname","isxrq","drdsj","def18","lastmodifytime",
                //是否成本结转、客户pk、联系方式 、外勤会计
                "isworkingunit",  "foreignid", "def3","vwqaccount", "def6","def7"
                // url、是否正式客户
                ,  "accbooks","url","isformal","rcunitname","dbirthday","rembday","vmonthday","ecotype",
              //营业期限自、营业期限至、登记状态
                "busienddate","dlicexpdate","remeday"
                };
        return updateFields;
    }

	/**
	 * 公司目录保存前校验 1：由于存在个人客户，所以科目方案与行业不做非空校验
	 * 
	 * @param corpVO
	 */
	private void checkBeforeSave(CorpVO corpVO) {
		if (StringUtil.isEmptyWithTrim(corpVO.getInnercode())) {
			throw new BusinessException("客户编码不能为空,保存失败!");
		}
		Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
		Matcher m = p.matcher(corpVO.getInnercode());
		if (m.find()) {
		    throw new BusinessException("客户编码不能包含汉字。");
		}
		if (StringUtil.isEmptyWithTrim(corpVO.getUnitname())) {
			throw new BusinessException("客户名称不能为空,保存失败!");
		}

		if (StringUtil.isEmptyWithTrim(corpVO.getPhone1())) {
			throw new BusinessException("联系人手机不能为空,保存失败!");
		}
		if (StringUtil.isEmptyWithTrim(corpVO.getPhone2())) {
			throw new BusinessException("老板手机号不能为空,保存失败!");
		}
		try {
            boolean mobile2 = DZFValidator.isMobile(CorpSecretUtil.deCode(corpVO.getPhone2()));
            if (!mobile2) {
                throw new BusinessException("老板手机号格式非法,保存失败!");
            }
        } catch (Exception e) {
            throw new BusinessException("老板手机号格式非法,保存失败!");
        }
		try {
			boolean mobile1 = DZFValidator.isMobile(CorpSecretUtil.deCode(corpVO.getPhone1()));
			if (!mobile1) {
				throw new BusinessException("联系人手机号格式非法，保存失败!");
			}
		} catch (Exception e) {
			throw new BusinessException("联系人手机号格式非法，保存失败!");
		}
		
		if (!StringUtil.isEmptyWithTrim(corpVO.getEmail1())) {
			boolean email1 = DZFValidator.isEmail(corpVO.getEmail1());
			if (!email1) {
				throw new BusinessException("电子邮箱格式非法,保存失败!");
			}
		}
		if (!StringUtil.isEmpty(corpVO.getDef15())&&"身份证".equals(corpVO.getDef15())&&corpVO.getVcorporationid() != null && !"".equals(corpVO.getVcorporationid())) {
			// boolean idcard =
			// DZFValidator.isIDCard(corpVO.getVcorporationid());
			boolean idcard = Pattern.matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)", corpVO.getVcorporationid());
			if (!idcard) {
				throw new BusinessException("证件号码格式非法,保存失败!");
			}
		}
		if(corpVO.getBusienddate() != null && corpVO.getDlicexpdate() != null){
		    if(corpVO.getDlicexpdate().before(corpVO.getBusienddate())){
		        throw new BusinessException("营业期限截止日期必须大于开始日期!");
		    }
		}
	}

	/**
	 * 增值客户保存前校验
	 * 
	 * @param corpVO
	 */
	private void checkBeforeZzSave(CorpVO corpVO) {
		if (corpVO.getInnercode() == null) {
			throw new BusinessException("客户编码不能为空,保存失败!");
		}
	    Pattern p = Pattern.compile("[\u4e00-\u9fa5]");
        Matcher m = p.matcher(corpVO.getInnercode());
        if (m.find()) {
            throw new BusinessException("客户编码不能包含汉字。");
        }

		if (corpVO.getUnitname() == null) {
			throw new BusinessException("客户名称不能为空,保存失败!");
		}
		if (corpVO.getPhone1() == null) {
			throw new BusinessException("联系电话不能为空,保存失败!");
		}
		if (corpVO.getPhone2() == null) {
			throw new BusinessException("验证码电话不能为空,保存失败!");
		}
		try {
			boolean mobile1 = DZFValidator.isMobile(CorpSecretUtil.deCode(corpVO.getPhone1()));
			if (!mobile1) {
				throw new BusinessException("联系人电话手机号格式非法,保存失败!");
			}
		} catch (Exception e) {
			throw new BusinessException("联系人电话手机号格式非法,保存失败!");
		}
		try {
			boolean mobile2 = DZFValidator.isMobile(CorpSecretUtil.deCode(corpVO.getPhone2()));
			if (!mobile2) {
				throw new BusinessException("验证码电话手机号格式非法,保存失败!");
			}
		} catch (Exception e) {
			throw new BusinessException("验证码电话手机号格式非法,保存失败!");
		}
		if (corpVO.getVcorporationid() != null && !"".equals(corpVO.getVcorporationid())) {
			// boolean idcard =
			// DZFValidator.isIDCard(corpVO.getVcorporationid());
			boolean idcard = Pattern.matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)", corpVO.getVcorporationid());
			if (!idcard) {
				throw new BusinessException("法人身份证号格式非法,保存失败!");
			}
		}
	}

	/**
	 * 查询公司列表--一直到最上级
	 */
//	@Override
//	public CorpVO[] queryCorp(String pk_corp) throws BusinessException {
//
//		// String corpsql = "select * from bd_corp where pk_corp='"++"'";
//
//		return null;
//	}

	/**
	 * 小企业建账前校验科目方案,并初始化会计科目
	 * 
	 * @param corpVO
	 * @param fvo  代账机构
	 * @throws BusinessException
	 * @throws UifException
	 */
	public void saveCorpAccount(CorpVO corpVO) throws BusinessException {
		if (corpVO.getCorptype() == null) {
			throw new BusinessException("没有会计科目方案，不允许建账");
		}
		// 根据当前公司的科目方案
		BdtradeAccountSchemaVO schmeVO = (BdtradeAccountSchemaVO) singleObjectBO
				.queryByPrimaryKey(BdtradeAccountSchemaVO.class, corpVO.getCorptype());
		CorpVO fvo = corpService.queryByPk(corpVO.getFathercorp());
		if (schmeVO != null) {
			String pk_schema = schmeVO.getPrimaryKey();
			// 根据科目方案，找到行业会计科目
			String sql = " pk_trade_accountschema=? and nvl(dr,0)=0 ";
			SQLParameter param = new SQLParameter();
			param.addParam(pk_schema);
			BdTradeAccountVO[] vo2s = (BdTradeAccountVO[]) singleObjectBO.queryByCondition(BdTradeAccountVO.class, sql,
					param);
			if (vo2s != null && vo2s.length > 0) {
				// 转换成公司科目
				Vector<YntCpaccountVO> vec = new Vector<YntCpaccountVO>();
				for (BdTradeAccountVO vo2 : vo2s) {
					String[] attrNames = vo2.getAttributeNames();
					YntCpaccountVO acorpVO = new YntCpaccountVO();
					for (String attName : attrNames) {
						if ("children".equals(attName) || "dr".equals(attName))
							continue;
						if ("pk_trade_account".equals(attName) || "".equals(attName) || attName == null)
							continue;
						if (attName.equals("pk_trade_accountschema")) {
							acorpVO.setPk_corp_accountschema(vo2.getPk_trade_accountschema());
						} else {
							acorpVO.setAttributeValue(attName, vo2.getAttributeValue(attName));
						}
					}
					acorpVO.setIsfzhs("0000000000");
					if ("1122".equals(acorpVO.getAccountcode())
							|| "2202".equals(acorpVO.getAccountcode())) {
						acorpVO.setIsverification(DZFBoolean.TRUE);
						
					}
					if(fvo != null && fvo.getIschannel() != null && fvo.getIschannel().booleanValue()){
					    if ("1122".equals(acorpVO.getAccountcode()) || "2203".equals(acorpVO.getAccountcode())) {
					        acorpVO.setIsfzhs("1000000000");
	                    }else if ("1123".equals(acorpVO.getAccountcode()) || "2202".equals(acorpVO.getAccountcode())) {
                            acorpVO.setIsfzhs("0100000000");
                        }
                    }
					acorpVO.setPk_corp(corpVO.getPk_corp());
					acorpVO.setIssyscode(DZFBoolean.TRUE);// 建账时，预制科目默认值
					vec.add(acorpVO);
				}
				if (!vec.isEmpty()) {
					String selDel = "delete from ynt_cpaccount where pk_corp=?";
					param.clearParams();
					param.addParam(corpVO.getPk_corp());
					singleObjectBO.executeUpdate(selDel, param);
					// singleObjectBO.deleteByWhereClause(YntCpaccountVO.class,
					// " pk_corp='"+corpVO.getPk_corp()+"' ") ;
					singleObjectBO.insertVOArr(corpVO.getPk_corp(), vec.toArray(new YntCpaccountVO[0]));					
				}
				
				addIncomeWarning(corpVO.getPk_corp(), schmeVO.getAccountstandard(), corpVO.getChargedeptname());
			} else {
				throw new BusinessException("没有找到行业会计科目，不允许建账");
			}
		} else {
			throw new BusinessException("该公司没有会计科目方案，不允许建账");
		}
	}

	@Override
	public void deleteCorp(CorpVO corpVO,String fathercorp) throws DZFWarpException {
		String pk_corp = corpVO.getPk_corp();
		String ucode = corpVO.getInnercode();
		//手机注册用户和网站注册用户不允许删除
		if(isAppOrWzCorp(corpVO.getFathercorp(), pk_corp)){
			throw new BusinessException("手机或网站注册客户，不允许删除！");
		}
		// 1.已建账客户不允许被删除(建账之后才允许停用，不需要重复校验)
		if (corpVO.getIshasaccount() != null && corpVO.getIshasaccount().booleanValue()) {
			throw new BusinessException("客户已建账，不允许删除！");
		}
		// 2.被引用客户不允许被删除
        refchecksrv.isReferencedRefmsg(corpVO.getTableName(), corpVO.getPk_corp());
//		// 2.被引用客户不允许被删除
//		if (isBeUsed(pk_corp)) {
//			throw new BusinessException("客户已被引用，不允许删除！");
//		}
//		if (isRoleCorp(pk_corp)) {
//            throw new BusinessException("客户已分配权限，不允许删除！");
//        }
		// 删除提醒信息
		CorpCredenVO[] credenVOs = queryCorpCreden(corpVO);
		singleObjectBO.deleteVOArray(credenVOs);

		// 删除上传附件VO信息
		CorpDocVO[] docVOs = queryCorpDoc(corpVO);
		String vfilepath = null;
		if (docVOs != null && docVOs.length > 0) {
			vfilepath = docVOs[0].getVfilepath();
			vfilepath = vfilepath.substring(0, vfilepath.indexOf(ucode));
			vfilepath = vfilepath + ucode;
		}
		singleObjectBO.deleteVOArray(docVOs);
		
//		CorpRoleVO[] cRoleVOs = queryCorpRole(corpVO);
//		if(cRoleVOs != null && cRoleVOs.length > 0){
//		    String delRole = "update sm_user_role set dr = 1 where pk_user_role = ?";
//		    SQLParameter param = new SQLParameter();
//		    for(CorpRoleVO cRoleVO : cRoleVOs){
//		        param.clearParams();
//		        param.addParam(cRoleVO.getPk_user_role());
//		        singleObjectBO.executeUpdate(delRole, param);
//		    }
//		    singleObjectBO.deleteVOArray(cRoleVOs);
//		}
		//删除权限信息//zpm修改
		String delpower = " delete from  sm_user_role where pk_corp= ? ";
		SQLParameter spd = new SQLParameter();
		spd.addParam(pk_corp);
		singleObjectBO.executeUpdate(delpower, spd);
		
		// 删除客户签约信息
		String updatesql = "update ynt_corp_user set pk_corp='appuse' where pk_corp=?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		singleObjectBO.executeUpdate(updatesql, sp);

		// 删除主表信息
		String delsql = " delete from bd_corp where nvl(dr,0) = 0 and pk_corp = ? and fathercorp = ? ";
		sp.clearParams();
		sp.addParam(pk_corp);
		sp.addParam(fathercorp);
		singleObjectBO.executeUpdate(delsql, sp);
		
		String shorlersql = " delete from ynt_corpsholder where nvl(dr,0) = 0 and pk_corp = ? ";
        sp.clearParams();
        sp.addParam(pk_corp);
        singleObjectBO.executeUpdate(shorlersql, sp);
        
        //删除客户印象
        delsql = " delete from ynt_corpimpress where nvl(dr,0) = 0 and pk_corp = ? ";
        sp.clearParams();
        sp.addParam(pk_corp);
        singleObjectBO.executeUpdate(delsql, sp);
        
        //删除纳税信息
        delsql = " delete from bd_corp_tax where nvl(dr,0) = 0 and pk_corp = ? ";
        sp.clearParams();
        sp.addParam(pk_corp);
        singleObjectBO.executeUpdate(delsql, sp);

		// 删除上传附件
		if (!StringUtil.isEmpty(vfilepath)) {
			File folder = new File(vfilepath);
			File[] files = folder.listFiles();
			if(files != null && files.length > 0){
			    for (File file : files) {
			        file.delete();
			    }
			}
			folder.delete();
		}
	}

	/**
	 * 更新固定资产
	 */
	@Override
	public void updateHflagSer(CorpVO corpVO) throws DZFWarpException {
		if (corpVO.getIshasaccount() == null || !corpVO.getIshasaccount().booleanValue()) {
			throw new BusinessException("未建账，不允许启用固定资产。");
		}
		if(corpVO.getBusibegindate().before(corpVO.getBegindate())){
            throw new BusinessException("固定资产启用日期不能早于建账日期");
        }
		corpVO.setHoldflag(DZFBoolean.TRUE);
		singleObjectBO.update(corpVO, new String[] { "holdflag", "busibegindate" });
	}

	/**
	 * 更新是否建账
	 */
	@Override
	public void updateHasaccountSer(CorpVO corpvo) throws DZFWarpException {
		if (StringUtil.isEmpty(corpvo.getCorptype())) {
			throw new BusinessException("科目方案为空，不能建账。");
		}
		if (corpvo.getIcostforwardstyle() == null) {
			throw new BusinessException("成本结转类型为空，不能建账。");
		}
		CorpVO avo = corpService.queryByPk(corpvo.getFathercorp());

		if (StringUtil.isEmpty(corpvo.getChargedeptname())){
		    throw new BusinessException("纳税人资格为空，不能建账。");
		}
		if(!"00000100AA10000000000BMD".equals(corpvo.getCorptype()) && !"00000100AA10000000000BMF".equals(corpvo.getCorptype())){
		    if(corpvo.getBbuildic() != null && IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){//启用库存
                throw new BusinessException("只有【企业会计准则】或【小企业会计准则】才能启用库存模块。");
            }else if(corpvo.getBbuildic() != null && IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())){
                throw new BusinessException("只有【企业会计准则】或【小企业会计准则】才能启用总账核算存货。");
            }
        }
		corpvo.setIshasaccount(DZFBoolean.TRUE);
		//建账同时启用固定资产
//		corpvo.setHoldflag(DZFBoolean.TRUE);
//		corpvo.setBusibegindate(corpvo.getBegindate());
		String[] strs = new String[] {"ishasaccount","begindate","corptype","icostforwardstyle","def4","industry","chargedeptname","def20","bbuildic","ecotype"};
//		if(avo.getIschannel() != null && avo.getIschannel().booleanValue()){
//		    strs = new String[] {"ishasaccount","begindate","corptype","icostforwardstyle","def4","industry","def20","bbuildic"};
//        }
		singleObjectBO.update(corpvo, strs);
		saveCorpAccount(corpvo);
		if(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
		    if(corpvo.getIcbegindate().before(corpvo.getBegindate())){
		        throw new BusinessException("库存启用日期不能早于总账启用日期");
		    }
		    updateBuildicSer(corpvo);
		}
		if(corpvo.getHoldflag() != null && corpvo.getHoldflag().booleanValue()){
		    updateHflagSer(corpvo);
		}
		addVoucherTemplate(corpvo);
	}
	
	/**
	 * 更新是否启用库存
	 */
	@Override
	public void updateBuildicSer(CorpVO corpvo) throws DZFWarpException {
		if (corpvo.getIshasaccount() == null || !corpvo.getIshasaccount().booleanValue()) {
			throw new BusinessException("未建账，不允许启用存货模块。");
		}
		if(!corpvo.getIcbegindate().getStrDay().equals("01")){
            throw new BusinessException("启用库存模块必须为每月1号。");
        }
        if(corpvo.getBegindate().compareTo(corpvo.getIcbegindate()) > 0){
            throw new BusinessException("库存启用日期不能早于总账启用日期");
        }
        if(!"00000100AA10000000000BMD".equals(corpvo.getCorptype()) && !"00000100AA10000000000BMF".equals(corpvo.getCorptype())){
            throw new BusinessException("只有【企业会计准则】或【小企业会计准则】才能启用库存模块");
        }
		corpvo.setBbuildic(IcCostStyle.IC_ON);
		corpvo.setIbuildicstyle(1);//存货单据生成总账凭证
		singleObjectBO.update(corpvo, new String[] { "bbuildic", "icbegindate", "ibuildicstyle" });
		
		saveModelTOInvAccSetVO(corpvo.getPk_corp());
		
		updateIcData(corpvo);
	}
	
	// 更新存货辅助的数据到库存存货 并更新对应的凭证
	private void updateIcData(CorpVO corpvo) {

		// 查询辅助
		String condition = " pk_auacount_h = ? and pk_corp = ? and nvl(dr,0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam("000001000000000000000006");
		sp.addParam(corpvo.getPk_corp());
		AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
				.queryByCondition(AuxiliaryAccountBVO.class, condition, sp);

		if (results == null || results.length == 0) {
			return;
		}

		// 计量单位
		Map<String, MeasureVO> jldwmap = new HashMap<>();

		// 查询1405科目
		StringBuffer sb = new StringBuffer();
		sb.append(" pk_corp=? and nvl(dr,0)=0  and accountcode =?");
		sp.clearParams();
		sp.addParam(corpvo.getPk_corp());
		sp.addParam("1405");
		YntCpaccountVO[] accvos = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
				sb.toString(), sp);

		String pk_subject = accvos[0].getPk_corp_account();
		DZFDateTime date = new DZFDateTime();

		// 转换存货vo
		List<InventoryVO> invlist = new ArrayList<>();
		for (AuxiliaryAccountBVO vo : results) {
			InventoryVO invvo = new InventoryVO();
			invvo.setCode(vo.getCode());
			invvo.setName(vo.getName());
			invvo.setInvspec(vo.getSpec());
			//invvo.setInvtype(vo.getInvtype());
			invvo.setXslx(vo.getXslx());

			String jldw = vo.getUnit();
			MeasureVO jldwvo = jldwmap.get(jldw);
			if (jldwvo == null && !StringUtil.isEmpty(jldw)) {
				jldwvo = buildMeasureVO(jldw, corpvo.getPk_corp(), null);
				if (!jldwmap.containsKey(jldw)) {
					jldwmap.put(jldw, jldwvo);
				}
			}
			if (jldwvo != null)
				invvo.setPk_measure(jldwvo.getPk_measure());
			invvo.setPk_corp(corpvo.getPk_corp());
			if(StringUtil.isEmpty(vo.getPk_subject())){
				invvo.setPk_subject(pk_subject);
			}else{
				invvo.setPk_subject(vo.getPk_subject());
			}
			invvo.setCreatetime(date);
			invvo.setDr(0);
			invlist.add(invvo);
		}

		// 保存存货vo
		String[] pks = singleObjectBO.insertVOArr(corpvo.getPk_corp(),
				invlist.toArray(new InventoryVO[invlist.size()]));

		// 记录pk对照关系
		int len = results.length;

		List<SQLParameter> list = new ArrayList<SQLParameter>();
		for (int i = 0; i < len; i++) {
			sp = new SQLParameter();
			sp.addParam(pks[i]);
			sp.addParam("");
			sp.addParam(results[i].getPk_auacount_b());
			sp.addParam(corpvo.getPk_corp());
			list.add(sp);
		}
		// 更新公司所有凭证
		sb.setLength(0);
		sb.append(" update ynt_tzpz_b set pk_inventory=?,fzhsx6 =? where fzhsx6 =?  and pk_corp=? and nvl(dr,0)=0");
		singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
		
		sb.setLength(0);
		list.clear();
		for (int i = 0; i < len; i++) {
			sp = new SQLParameter();
			sp.addParam(pks[i]);
			sp.addParam(results[i].getPk_auacount_b());
			sp.addParam(corpvo.getPk_corp());
			list.add(sp);
		}
		sb.append(" update ynt_fzhsqc set fzhsx6=? where fzhsx6 =?  and pk_corp=? and nvl(dr,0)=0");
		singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
		
		// 删除存货辅助记录
		singleObjectBO.deleteVOArray(results);
		//删除别名
//		SQLParameter sp1 = new SQLParameter();
//		sp1.addParam(corpvo.getPk_corp());
//		singleObjectBO.executeUpdate("delete from ynt_icalias where pk_corp = ? ", sp1);
		//更新别名
		sb.setLength(0);
		sb.append(" update ynt_icalias set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
	}
		
	private MeasureVO buildMeasureVO(String jldw, String pk_corp, String creator) {
		MeasureVO vo = new MeasureVO();
		vo.setCode(getMeasureCode(pk_corp));
		vo.setName(jldw);
		vo.setPk_corp(pk_corp);
		vo.setCreator(creator);
		vo.setCreatetime(new DZFDateTime());
		singleObjectBO.saveObject(vo.getPk_corp(), vo);
		return vo;
	}

	private String getMeasureCode(String pk_corp) throws DZFWarpException {
		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("获取计量单位编码失败!");
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql = " select * from ynt_measure where pk_corp=? and nvl(dr,0) = 0  ";
		List<MeasureVO> listbvo = (List<MeasureVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(MeasureVO.class));
		List<ICodeName> list = new ArrayList<ICodeName>();
		if (listbvo != null && listbvo.size() > 0) {
			Collections.addAll(list, listbvo.toArray(new MeasureVO[0]));
		}
		Long maxcode = getMaxCode(list);
		return getFinalcode(maxcode);
	}

	private String getFinalcode(Long code) {
		String str = "";
		if (code > 0 && code < 10) {
			str = "00" + String.valueOf(code);
		} else if (code > 9 && code < 100) {
			str = "0" + String.valueOf(code);
		} else {
			str = String.valueOf(code);
		}
		return str;
	}
		
	private Long getMaxCode(List<ICodeName> listbvo) {
		if (listbvo == null || listbvo.size() == 0)
			return 1L;
		Long result = 1L;
		Long maxNum = 1L;
		int size = listbvo.size();
		ICodeName bvo = null;
		for (int i = 0; i < size; i++) {
			bvo = listbvo.get(i);
			String code = bvo.getCode();
			if (StringUtil.isEmpty(code))
				continue;
			try {
				result = Long.parseLong(code.trim()) + 1;
				if (result > maxNum) {
					maxNum = result;
				}
			} catch (Exception e) {
				// 吃掉异常
			}
		}
		return maxNum;
	}

	private void saveModelTOInvAccSetVO(String pk_corp) {

        CorpVO corpVO = corpService.queryByPk(pk_corp);
        String corpType = corpVO.getCorptype();
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDefaultValue.DefaultGroup);
        sp.addParam(corpType);
        InvAccModelVO[] vos = (InvAccModelVO[]) singleObjectBO.queryByCondition(InvAccModelVO.class,
                " pk_corp = ? and pk_trade_accountschema = ? and nvl(dr,0) = 0", sp);

        if (vos == null || vos.length == 0)
            return ;

        InvAccSetVO setvo = new InvAccSetVO();
        setvo.setPk_corp(pk_corp);
        setvo.setDr(0);
        for (InvAccModelVO vo : vos) {
            String corp_account = getCorpAccountPkByTradeAccountPk(vo.getPk_accsubj(), pk_corp);
            switch (vo.getIcolumntype().intValue()) {
            case 0:
                setvo.setCg_yjjxskm(corp_account);
                break;
            case 1:
                setvo.setCg_yfzkkm(corp_account);
                break;
            case 2:
                setvo.setCg_xjfkkm(corp_account);
                break;
            case 3:
                setvo.setXs_xjskkm(corp_account);
                break;
            case 4:
                setvo.setXs_yszkkm(corp_account);
                break;
            case 5:
                setvo.setXs_yysrkm(corp_account);
                break;
            case 6:
                setvo.setXs_yjxxskm(corp_account);
                break;
            case 7:
                setvo.setLl_clcbkm(corp_account);
                break;
            case 8:
                setvo.setLl_yclkm(corp_account);
                break;
            case 9:
                setvo.setVdef1(corp_account);
                break;
            case 10:
                setvo.setVdef2(corp_account);
                break;
            case 11:
                setvo.setVdef3(corp_account);
                break;
            case 12:
                setvo.setVdef4(corp_account);
                break;
            case 13:
                setvo.setVdef5(corp_account);
                break;
            case 14:
                setvo.setVdef6(corp_account);
                break;
            case 15:
                setvo.setZgrkdfkm(corp_account);
                break;
            case 16:
                setvo.setXs_clsrkm(corp_account);
                break;
            default:
                break;
            }
        }
        sp.clearParams();
        sp.addParam(pk_corp);
        InvAccSetVO[] sets = (InvAccSetVO[]) singleObjectBO.queryByCondition(InvAccSetVO.class,
                " pk_corp = ? and nvl(dr,0) = 0", sp);
        
        if(sets == null || sets.length==0){
            singleObjectBO.insertVOArr(pk_corp, new InvAccSetVO[]{setvo});
        }else{
            setvo.setPk_invaccset(sets[0].getPk_invaccset());
            singleObjectBO.update(setvo);
        }
        
    }
	
	public String getCorpAccountPkByTradeAccountPk(String pk_trade_account,
			String pk_corp) throws DZFWarpException {
		BdTradeAccountVO jfkmVO = (BdTradeAccountVO) singleObjectBO.queryVOByID(pk_trade_account, BdTradeAccountVO.class);
		if (jfkmVO == null) {
			return null;
		}
		SQLParameter sp=new SQLParameter();
		String newrule = queryAccountRule(pk_corp);
		String olerule = DZFConstant.ACCOUNTCODERULE;
		String newaccount = getNewRuleCode(jfkmVO.getAccountcode(), olerule, newrule);
		sp.addParam(newaccount);
		sp.addParam(pk_corp);
		
		String condition = "  isleaf='Y' and  accountcode=? and pk_corp=? and nvl(dr,0)=0 ";
		YntCpaccountVO[] gsjfkmVOs = (YntCpaccountVO[]) singleObjectBO
				.queryByCondition(YntCpaccountVO.class,condition,sp);
		if (gsjfkmVOs == null || gsjfkmVOs.length < 1) {
			YntCpaccountVO vo =	queryAccount(pk_corp, newaccount);
			if(vo == null)
				return null;
			return vo.getPk_corp_account();
		}

		return gsjfkmVOs[0].getPrimaryKey();

	}
	
//  查询第一分支的最末级科目
	private YntCpaccountVO queryAccount(String pk_corp, String code) throws BusinessException {

		StringBuffer strb = new StringBuffer();

		strb.append(" SELECT * FROM  ynt_cpaccount t ");
		strb.append("  where t.accountcode like ? ");
		strb.append("  and pk_corp = ? and  nvl(isleaf,'N')='Y'  ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(code+"%");
		sp.addParam(pk_corp);

		List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO.executeQuery(strb.toString(), sp,
				new BeanListProcessor(YntCpaccountVO.class));
		if(list == null || list.size()==0){
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}
	
	/**
	 * 查询对应的科目编码规则
	 */
	
	private String queryAccountRule(String pk_corp) throws DZFWarpException {

		if (StringUtil.isEmpty(pk_corp)) {
			throw new BusinessException("查询科目编码时:公司信息不能为空!");
		}
		String kmrulesql = "select accountcoderule from bd_corp where pk_corp = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<String> kmrulelist = (List<String>) singleObjectBO.executeQuery(
				kmrulesql, sp, new ColumnListProcessor());
		if (kmrulelist == null || kmrulelist.size() == 0) {
			return DZFConstant.ACCOUNTCODERULE;
		} else {
			if (StringUtil.isEmpty(kmrulelist.get(0))) {
				return DZFConstant.ACCOUNTCODERULE;
			}
			return kmrulelist.get(0);
		}
	}
	
	/****
	 * 获取新编码
	 * @param oldcode  旧编码
	 * @param oldrule  旧编码规则
	 * @param newrule  新编码规则
	 * 
	 * @return 新编码
	 */
	private String getNewRuleCode(String oldcode,String oldrule,String newrule)throws DZFWarpException {
		
		try {
			String[] odru = oldrule.split("/");
			String[] newru = newrule.split("/");
			
			String newcode = "";
			int startIndex = 0;		
			
		    for(int i=0;i<odru.length;i++){
		    	int codelen = new BigInteger(String.valueOf(odru[i])).intValue();
		    	String oldpartCode = oldcode.substring(startIndex, startIndex+codelen);
		    	startIndex+=codelen;
		    	String newpartCode = getNewPartCode(newru[i],oldpartCode);
		    	newcode+=newpartCode;
		    	if(startIndex==oldcode.trim().length()){
		    		break;
		    	}
		    }
		    
		    return newcode;
		} catch (Exception e) {
//			logger.error("获取新编码异常", e);
			throw new BusinessException("获取新的科目编码异常");
		}

	}
	
	
	/****
	 * 获取某级次上的新编码  如就编码为100101  第一级为1001 第二级为01  
	 *     原来的编码规则为4/2/2 现在要变为  4/3/3;现在要获取第二级（01）的新编码
	 *     则返回001 ，在原来编码左边补0指导满足第二级的位数（3）
	 *     
	 * @param newcodeRulePart 新编码规则对应级次位数字符串  如4/3/3 第二级传3
	 * @param oldpartCode     旧编码规则对应级次编码字符串  如100101 第二级传 01
	 * @return  对应级次的新编码
	 */
	public String getNewPartCode(String newcodeRulePart,String oldpartCode){
		
		String newPartCode = oldpartCode;
		int newPartLen = Integer.parseInt(newcodeRulePart);
		int oldPartLen = oldpartCode.trim().length();
		if(oldPartLen==newPartLen){
			return newPartCode;
		}
	    
		for(int i=0;i<(newPartLen-oldPartLen);i++){
			newPartCode = "0" + newPartCode;
		}
		
		return newPartCode;
	}
	@Override
	public void updateService(CorpVO corpvo) throws DZFWarpException {
		checkBeforeStop(corpvo);
		singleObjectBO.update(corpvo, new String[] { "isseal","sealeddate","def17","approve_user" });
	}
	
	private void checkBeforeStop(CorpVO corpvo){
		//1、合同已结束或终止
		checkContract(corpvo);
		//2、收款单已审核
		checkPayee(corpvo);
		//3、代收款、预收款无余额
		checkBalance(corpvo);
	}
	
	/***
	 * 合同已结束或终止检查
	 * @param corpvo
	 */
	@SuppressWarnings("unchecked")
	private void checkContract(CorpVO corpvo){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select vcontcode from ynt_contract where nvl(dr,0)=0 and pk_corpk=? and nvl(isflag,'N') ='Y' and icosttype in(0,1) and vstatus in(")
		.append(0).append(",")
		.append(1).append(",")
		.append(2).append(")");
		
		SQLParameter params = new SQLParameter();
		params.addParam(corpvo.getPk_corp());

/*		boolean isexist = singleObjectBO.isExists(corpvo.getPk_corp(), sql.toString(), params);
		if(isexist){
			throw new BusinessException("存在未结束或停止的合同不能停止服务");
		}*/
		List<Map<String, String>> list = (List<Map<String, String>>)singleObjectBO.executeQuery(sql.toString(), params, new MapListProcessor());
		if(list!=null&&list.size()>0){
			StringBuffer msg = new StringBuffer();
			msg.append("该客户的如下业务还未结束，还不能停止服务，请处理完这些业务后再停止客户:");
			for(Map<String, String> map : list){
				msg.append(map.get("vcontcode")).append(",");
			}
			msg.deleteCharAt(msg.length()-1);
			msg.append(" 合同未结束;");
			throw new BusinessException(msg.toString());
		}
		
		sql = new StringBuffer();
		sql.append("select vcontcode from ynt_contract where nvl(dr,0)=0 and pk_corpk=? and icosttype in(2) and balancesum>0");

	    list = (List<Map<String, String>>)singleObjectBO.executeQuery(sql.toString(), params, new MapListProcessor());
		if(list!=null&&list.size()>0){
			StringBuffer msg = new StringBuffer();
			msg.append("该客户的如下业务还未结束，还不能停止服务，请处理完这些业务后再停止客户:");
			for(Map<String, String> map : list){
				msg.append(map.get("vcontcode")).append(",");
			}
			msg.deleteCharAt(msg.length()-1);
			msg.append(" 合同存在余额;");
			throw new BusinessException(msg.toString());
		}
	}
	
	
	/***
	 * 收款单已审核检查
	 * @param corpvo
	 */
	@SuppressWarnings("unchecked")
	private void checkPayee(CorpVO corpvo){
		
		StringBuffer sql = new StringBuffer();
		sql.append("select vbillcode from ynt_charge where nvl(dr,0)=0 and pk_corpk=? and istatus in(")
		.append(0).append(")");
		
		SQLParameter params = new SQLParameter();
		params.addParam(corpvo.getPk_corp());

/*		boolean isexist = singleObjectBO.isExists(corpvo.getPk_corp(), sql.toString(), params);
		if(isexist){
			throw new BusinessException("存在没有审核的收款单不能停止服务");
		}*/
		
		List<Map<String, String>> list = (List<Map<String, String>>)singleObjectBO.executeQuery(sql.toString(), params,  new MapListProcessor());
		if(list!=null&&list.size()>0){
			StringBuffer msg = new StringBuffer();
			msg.append("该客户的如下业务还未结束，还不能停止服务，请处理完这些业务后再停止客户:");
			for(Map<String, String> map : list){
				msg.append(map.get("vbillcode")).append(",");
			}
			msg.deleteCharAt(msg.length()-1);
			msg.append(" 收款单未审核;");
			throw new BusinessException(msg.toString());
		}
	}
	
	
	/***
	 * 代收款、预收款无余额检查
	 * @param corpvo
	 */
	private void checkBalance(CorpVO corpvo){
		
		StringBuffer msg = new StringBuffer();
		msg.append("该客户的如下业务还未结束，还不能停止服务，请处理完这些业务后再停止客户:");
		
		StringBuffer sql = new StringBuffer();
		sql.append("select sum(nyuchargemny)- sum(nyhxmny)  ys from ynt_charge where nvl(dr,0)=0 and pk_corpk=?  ");
		
		SQLParameter params = new SQLParameter();
		params.addParam(corpvo.getPk_corp());

		Object[] obj = (Object[])singleObjectBO.executeQuery(sql.toString(), params, new ArrayProcessor());
		if(obj!=null&&obj.length>0&&obj[0]!=null){
			BigDecimal i = (BigDecimal) obj[0];
			if(i.doubleValue()>0){
				msg.append("预收款存在余额为"+i+"元;");
				throw new BusinessException(msg.toString());
			}
		}
		
		sql = new StringBuffer();
		sql.append("select sum(totalamount)- sum(usedamount)  ds from ynt_collect_b where nvl(dr,0)=0 and corpb=?  ");
		obj = (Object[])singleObjectBO.executeQuery(sql.toString(), params, new ArrayProcessor());
		if(obj!=null&&obj.length>0&&obj[0]!=null){
			BigDecimal i = (BigDecimal) obj[0];
			if(i.doubleValue()>0){
				msg.append("代收款存在余额为"+i+"元;");
				throw new BusinessException(msg.toString());
			}
		}
	}

	@Override
	public CorpDocVO[] queryChild(CorpDocVO vo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("nvl(dr,0) = 0 ");
		if (vo.getPk_corp() != null && !"".equals(vo.getPk_corp())) {
			sql.append(" and pk_corp = ?");
			sp.addParam(vo.getPk_corp());
		}
		return (CorpDocVO[]) singleObjectBO.queryByCondition(CorpDocVO.class, sql.toString(), sp);
	}

	@Override
	public int countCorp(QueryParamVO queryvo) throws DZFWarpException {
		StringBuffer corpsql = new StringBuffer();
		SQLParameter params = new SQLParameter();
		corpsql.append("select count(*) from bd_corp a");
		corpsql.append(" left join bd_account b on a.fathercorp = b.pk_corp");
		corpsql.append(" left join ynt_tdaccschema y on a.corptype = y.pk_trade_accountschema");// 查询科目方案的名称
		corpsql.append(" left join ynt_bd_trade t on a.industry = t.pk_trade");
		corpsql.append(" where nvl(a.dr,0) =0 and a.fathercorp = ?");
		params.addParam(queryvo.getPk_corp());
		if (!StringUtil.isEmptyWithTrim(queryvo.getCorpcode())) {
			corpsql.append(" and a.innercode like ? ");
			params.addParam("%" + queryvo.getCorpcode() + "%");
		}
		if (!StringUtil.isEmptyWithTrim(queryvo.getCorpname())) {
			corpsql.append(" and a.unitname like ? ");
			params.addParam("%" + queryvo.getCorpname() + "%");
		}
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			corpsql.append(" and (a.createdate >= ? and a.createdate <= ?)");
			params.addParam(queryvo.getBegindate1());
			params.addParam(queryvo.getEnddate());
		}
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			corpsql.append(" and (a.begindate >= ? and a.begindate <= ? )");
			params.addParam(queryvo.getBcreatedate());
			params.addParam(queryvo.getEcreatedate());
		}
		if (queryvo.getXswyewfs() != null) {
			corpsql.append(" and nvl(a.ispersonal,'N') = ? ");
			params.addParam(queryvo.getXswyewfs());
		}
		corpsql.append(" and  nvl(a.isaccountcorp,'N') = 'N' ");
		return Integer.parseInt(
				singleObjectBO.executeQuery(corpsql.toString(), params, new ColumnProcessor()).toString());
	}

	@Override
	public CorpCredenVO[] queryCorpCreden(CorpVO corpVO) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("nvl(dr,0) = 0 ");
		String pk_corp = corpVO.getPk_corp();
		sql.append(" and pk_corp = ?");
		sp.addParam(pk_corp);
		return (CorpCredenVO[]) singleObjectBO.queryByCondition(CorpCredenVO.class, sql.toString(), sp);
	}

	@Override
	public String[] insertCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException {
		if (corpCredenVOs != null && corpCredenVOs.length > 0) {
			checkBeforeCredenSave(corpCredenVOs);
			String[] keys = singleObjectBO.insertVOWithPK(corpCredenVOs[0].getPk_corp(), corpCredenVOs);
			return keys;
		}
		return null;
	}

	// WJX
	@Override
	public CorpTaxInfoVO[] queryCorpTaxInfo(CorpVO corpVO) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("nvl(dr,0) = 0 ");
		String pk_corp = corpVO.getPk_corp();
		if (!StringUtil.isEmpty(pk_corp)) {
			sql.append(" and pk_corp = ?");
			sp.addParam(pk_corp);
		}
		return (CorpTaxInfoVO[]) singleObjectBO.queryByCondition(CorpTaxInfoVO.class, sql.toString(), sp);
	}

	// WJX
//	@Override
//	public String[] insertCorpTaxinfo(CorpTaxInfoVO[] corpTaxinfoVOs) throws DZFWarpException {
//		if (corpTaxinfoVOs != null && corpTaxinfoVOs.length > 0) {
//			checkBeforeTaxSave(corpTaxinfoVOs);
//			String[] keys = singleObjectBO.insertVOWithPK(corpTaxinfoVOs[0].getPk_corp(), corpTaxinfoVOs);
//			return keys;
//		}
//		return null;
//	}

	/**
	 * 税率信息保存前校验
	 * 
	 * @param CorpTaxInfoVO
	 *            WJX
	 */
//	private void checkBeforeTaxSave(CorpTaxInfoVO[] corpTaxVOs) {
//		for (CorpTaxInfoVO vo : corpTaxVOs) {
//			if (vo.getTaxcode() == null) {
//				throw new BusinessException("税种不能为空,保存失败!");
//			}
//			if (vo.getTaxrate() == null) {
//				throw new BusinessException("税率不能为空,保存失败!");
//			}
//		}
//	}

	@Override
	public void updateCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException {
		for (CorpCredenVO vo : corpCredenVOs) {
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		}
	}

	@Override
	public void deleteCorpCreden(CorpCredenVO[] corpCredenVOs) throws DZFWarpException {
		singleObjectBO.deleteVOArray(corpCredenVOs);

	}

	/**
	 * 提醒信息保存前校验
	 * 
	 * @param corpCredenVOs
	 */
	private void checkBeforeCredenSave(CorpCredenVO[] corpCredenVOs) {
		for (CorpCredenVO vo : corpCredenVOs) {
			if (vo.getVcredcode() == null) {
				throw new BusinessException("证书/提醒名称不能为空,保存失败!");
			}
			if (vo.getDexpiredate() == null) {
				throw new BusinessException("证件到期日不能为空,保存失败!");
			}
			if (vo.getIexpireday() == null) {
				throw new BusinessException("提前提醒天数不能为空,保存失败!");
			}
		}
	}

	@SuppressWarnings("unchecked")
	public CorpVO[] queryCorpRef(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		// 根据查询条件查询公司的信息
		// StringBuffer corpsql = new StringBuffer();
		String sql = getRefQuerySql(queryvo, uservo);
		SQLParameter param = getRefParam(queryvo, uservo);
		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(sql.toString(), param,
				new BeanListProcessor(CorpVO.class));
		QueryDeCodeUtils.decKeyUtils(new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname" },
				vos, 1);
		String corpname = queryvo.getCorpname();
		String corpcode = queryvo.getCorpcode();
		if (!StringUtil.isEmpty(corpname)) {
			if (vos != null && vos.size() > 0) {
				CorpVO[] cvos = vos.toArray(new CorpVO[0]);
				List<CorpVO> list = new ArrayList<>();
				String enf_name = null;
				String en_name = null;
				String unitname = null;
				for (CorpVO cvo : cvos) {
					unitname = cvo.getUnitname();
					if (!StringUtil.isEmpty(unitname)) {
						en_name = PinyinUtil.getFullSpell(unitname);
						enf_name = PinyinUtil.getFirstSpell(unitname);
					}
					if (unitname.contains(corpname) || en_name.contains(corpname) || enf_name.contains(corpname)) {
						list.add(cvo);
					} else if (cvo.getInnercode().contains(corpcode)) {
						list.add(cvo);
					}
				}
				return list.toArray(new CorpVO[0]);
			}
		}
		return vos.toArray(new CorpVO[0]);
	}

	/**
	 * 拼装我的客户查询信息
	 * 
	 * @param queryvo
	 * @return
	 */
	private String getRefQuerySql(QueryParamVO queryvo, UserVO uservo) {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select bd_corp.* ");
		corpsql.append(" from bd_corp bd_corp");
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		corpsql.append(" where nvl(bd_corp.dr,0) =0 and bd_corp.fathercorp = ? ");
		if (!ismanager.booleanValue()) {
			// corpsql.append(" left join sm_user_role sm_user_role on
			// sm_user_role.pk_corp = bd_corp.pk_corp");
			corpsql.append("  and bd_corp.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
		}
		// if (queryvo.getCorpcode() != null
		// && queryvo.getCorpcode().trim().length() > 0) {
		// corpsql.append(" and (bd_corp.unitcode like ? ");
		// }
		// if (queryvo.getCorpname() != null
		// && queryvo.getCorpname().trim().length() > 0) {
		// corpsql.append(" or bd_corp.unitname like ? )");
		// }
		if(!StringUtil.isEmpty(queryvo.getCorpid())){
            corpsql.append("  and bd_corp.pk_corp = ? ");
        }
		corpsql.append(" and nvl(bd_corp.isaccountcorp,'N') = 'N' ");
		corpsql.append(" and nvl(bd_corp.isseal,'N') = 'N'");
		if(queryvo.getQrytype() != null && !queryvo.getQrytype().equals("300") ){
		    corpsql.append(" and nvl(bd_corp.isformal,'N') = 'Y'");
		}
//		if (queryvo.getXswyewfs() != null) {
//			corpsql.append(" and nvl(bd_corp.ispersonal,'N') = ? ");
//		}
		if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
			corpsql.append(" and nvl(bd_corp.ishasaccount,'N') = ? ");
		}
		// if(!ismanager.booleanValue()){
		// corpsql.append(" and sm_user_role.cuserid = ?");
		// }
		corpsql.append("order by bd_corp.innercode");
		return corpsql.toString();
	}

	private SQLParameter getRefParam(QueryParamVO queryvo, UserVO uservo) {
		SQLParameter param = new SQLParameter();
		param.addParam(queryvo.getPk_corp());
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		if (!ismanager.booleanValue()) {
			param.addParam(uservo.getCuserid());
		}
		if(!StringUtil.isEmpty(queryvo.getCorpid())){
            param.addParam(queryvo.getCorpid());
        }
		// if (queryvo.getCorpcode() != null
		// && queryvo.getCorpcode().trim().length() > 0) {
		// param.addParam("%"+queryvo.getCorpcode()+"%");
		// }
		// if (queryvo.getCorpname() != null
		// && queryvo.getCorpname().trim().length() > 0) {
		// param.addParam("%"+queryvo.getCorpname()+"%");
		// }
//		if (queryvo.getXswyewfs() != null) {
//			param.addParam(queryvo.getXswyewfs());
//		}
		if(queryvo.getIshasjz() != null && queryvo.getIshasjz().booleanValue()){
			param.addParam(queryvo.getIshasjz());
		}
		return param;
	}

	public CorpVO[] queryCorpRefTotal(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException {
		// 根据查询条件查询公司的信息
		StringBuffer corpsql = new StringBuffer();
		String sql = getRefQuerySql(queryvo, uservo);
		SQLParameter param = getRefParam(queryvo, uservo);
		corpsql.append(sql);
		corpsql.append("  order by bd_corp.begindate desc");
		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(corpsql.toString(), param,
				new BeanListProcessor(CorpVO.class));
		return vos.toArray(new CorpVO[0]);
	}

	@Override
	public CorpDocVO[] queryCorpDoc(CorpVO corpVO) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("nvl(dr,0) = 0 ");
		String pk_corp = corpVO.getPk_corp();
		if (!StringUtil.isEmpty(pk_corp)) {
			sql.append(" and pk_corp = ?");
			sp.addParam(pk_corp);
		}
		return (CorpDocVO[]) singleObjectBO.queryByCondition(CorpDocVO.class, sql.toString(), sp);
	}

	@Override
	public void deleteCorpDoc(CorpDocVO[] corpDocVOs, String delFilePath) throws DZFWarpException {
		for (CorpDocVO vo : corpDocVOs) {
			// 删除上传附件信息
			singleObjectBO.deleteObject(vo);
			// 删除上传附件
			File folder = new File(delFilePath);
			File[] files = folder.listFiles();
			for (File file : files) {
				if (file.getName().equals(vo.getDocTemp())) {
					file.delete();
				}
			}
		}
	}

	@Override
	public CorpDocVO queryCorpDocByID(String pk_doc) throws DZFWarpException {
		return (CorpDocVO) singleObjectBO.queryVOByID(pk_doc, CorpDocVO.class);
	}

	@Override
	public List queryArea(String parenter_id) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sql.append("select region_id, region_name\n");
		sql.append("  from ynt_area\n");
		sql.append(" where nvl(dr, 0) = 0\n");
		sql.append("   and parenter_id = ? order by region_id");
		sp.addParam(Integer.parseInt(parenter_id));
		ArrayList list = (ArrayList) singleObjectBO.executeQuery(sql.toString(), sp,
				new BeanListProcessor(ResponAreaVO.class));
		return list;
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
		param.addParam(queryvo.getPk_corp());
		param.addParam(queryvo.getPk_corp());

		if (queryvo.getCorpcode() != null && queryvo.getCorpcode().trim().length() > 0) {
			param.addParam("%" + queryvo.getCorpcode() + "%");
		}
		if(!StringUtil.isEmptyWithTrim(queryvo.getCorpname())){//客户名称模糊查询
            param.addParam(CryptCodeUtil.enCode(queryvo.getCorpname()));
        }
		if (queryvo.getBegindate1() != null && queryvo.getEnddate() != null) {
			param.addParam(queryvo.getBegindate1());
			param.addParam(queryvo.getEnddate());
		}else if(queryvo.getBegindate1() != null){
            param.addParam(queryvo.getBegindate1());
        }else if(queryvo.getEnddate() != null){
            param.addParam(queryvo.getEnddate());
        }
		if (queryvo.getBcreatedate() != null && queryvo.getEcreatedate() != null) {
			param.addParam(queryvo.getBcreatedate());
			param.addParam(queryvo.getEcreatedate());
		} else if (queryvo.getBcreatedate() != null) {// 建账日期只录入开始日期或结束日期
			param.addParam(queryvo.getBcreatedate());
		} else if (queryvo.getEcreatedate() != null) {
			param.addParam(queryvo.getEcreatedate());
		}
		if (queryvo.getXswyewfs() != null) {
			param.addParam(queryvo.getXswyewfs());
		}
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE : uservo.getIsmanager();
		if (!ismanager.booleanValue()) {
			param.addParam(uservo.getCuserid());
			param.addParam(uservo.getCuserid());
			param.addParam(uservo.getCuserid());
		}
		if(!StringUtil.isEmpty(queryvo.getVprovince())){
			param.addParam(Integer.parseInt(queryvo.getVprovince()));
		}
		if(!StringUtil.isEmpty(queryvo.getKms_id())){//公司主键
			param.addParam(queryvo.getKms_id());
		}
		//是否激活dzfAPP
        if(queryvo.getIsdzfapp() != null && queryvo.getIsdzfapp() == 0){
            param.addParam(queryvo.getPk_corp());
        }else if(queryvo.getIsdzfapp() != null && queryvo.getIsdzfapp() == 1){
            param.addParam(queryvo.getPk_corp());
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
		if (!StringUtil.isEmpty(queryvo.getForeignname())) {
            param.addParam(queryvo.getForeignname() + "%");
        }
		if(!StringUtil.isEmpty(queryvo.getUserid())){
            param.addParam(queryvo.getUserid());
        }
		if(!StringUtil.isEmpty(queryvo.getHc())){
            param.addParam(queryvo.getPk_corp());
            param.addParam(queryvo.getHc());
        }
		return param;
	}

	@SuppressWarnings("unchecked")
	@Override
	// WJX MODIFIED
	public void saveCorp(CorpVO corpvo,File[] files, String uploadPath) throws DZFWarpException {
		boolean isNew = StringUtil.isEmpty(corpvo.getPk_corp());
		String tempcorpid = corpvo.getDef5(); //def5: 服务主体PK
		if (!StringUtil.isEmpty(tempcorpid) && isNew) {
			//判断是否已签约客户（服务主体已经生成客户的，不能重复生成）
			SQLParameter sp = new SQLParameter();
			sp.addParam(tempcorpid);
			sp.addParam(corpvo.getFathercorp());
			StringBuilder sb = new StringBuilder();
//			sb.append("select 1 from ynt_corp_user a");
//			sb.append(" inner join app_temp_corp t on a.pk_tempcorp=t.pk_temp_corp");
//			sb.append(" inner join bd_corp c on a.pk_corp=c.pk_corp");
//			sb.append(" where a.pk_tempcorp=? and c.fathercorp=?");
			sb.append("select 1 from bd_corp where def5 = ? and fathercorp = ? ");
			if (singleObjectBO.isExists(null, sb.toString(), sp))
				throw new BusinessException("服务主体已经签约，不能重复生成客户！");
		}
		// 1.主表VO+附件VO数组
		CorpDocVO[] docvos = (CorpDocVO[]) corpvo.getCorpDocVos();
		corpvo.setChildren(docvos);
		MaxCodeVO mcvo = null;
		try{
		    mcvo = getBillCode(corpvo);
	        corpvo.setInnercode(mcvo.getReturnCode());
	        CorpVO corpVO = insertCorpVO(corpvo);
	        String pk_corp = corpVO.getPk_corp();
	        // 2.提醒信息VO数组
	        CorpCredenVO[] addvos = (CorpCredenVO[]) corpvo.getCorpCredenVos();
	        if (addvos != null && addvos.length > 0) {
	            for (CorpCredenVO vo : addvos) {
	                vo.setPk_corp(corpVO.getPk_corp());
	            }
	            insertCorpCreden(addvos);
	        }
	        
	        CorpRoleVO[] roleVos = (CorpRoleVO[]) corpvo.getCorpRoleVos();
	        if(roleVos != null && roleVos.length > 0){
	            insertRoleUser(corpvo,roleVos);
	        }
	        
	        CorpSholderVO[] sholderVos = (CorpSholderVO[]) corpvo.getCorpSholderVos();
	        if(sholderVos != null && sholderVos.length > 0){
	            saveSholderVO(corpvo,sholderVos);
	        }
	        
	        // 3.上传附件数组
	        if(!StringUtil.isEmpty(corpVO.getUnitcode())){
	            uploadPath = ImageCommonPath.getCorpFilePath(corpVO.getUnitcode(), null);
	            if (docvos != null && docvos.length > 0) {
	                for (int i = 0; i < docvos.length; i++) {
	                    InputStream is=null;
	                    OutputStream os=null;
	                    try {
	                        is = new FileInputStream(files[i]);
	                        File toFile = new File(uploadPath, docvos[i].getDocTemp());
	                        if (!toFile.getParentFile().exists()) {
	                            toFile.getParentFile().mkdirs();
	                        }
	                         os= new FileOutputStream(toFile);
	                        byte[] buffer = new byte[1024];
	                        int length = 0;
	                        while ((length = is.read(buffer)) > 0) {
	                            os.write(buffer, 0, length);
	                        }
	                        
	                    } catch (FileNotFoundException e) {
	                        throw new BusinessException("查找文件出错" + e.getMessage());
	                    } catch (IOException e) {
	                        throw new BusinessException("文件读取出错" + e.getMessage());
	                    } catch (Exception ep){
	                        log.error("错误",ep);
	                        throw new BusinessException("文件出错" + ep.getMessage());
	                    }finally {
	                        try {
	                            if(is!=null)
	                                is.close();
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                        try {
	                            if(os!=null)
	                                os.close();
	                        } catch (IOException e) {
	                            e.printStackTrace();
	                        }
	                    }
	                }
	            }
	        }
	        //二、进行签约操作（服务主体签约代账公司，成为签约客户）
	        
	        if (!StringUtil.isEmpty(tempcorpid) && isNew) {
	            //判断是否已签约客户（服务主体已经生成客户的，不能重复生成）
	            SQLParameter sp = new SQLParameter();
	            StringBuilder sb = new StringBuilder();
	            
	            //这时可能存在该主体在其他代账公司下生成的客户的签约数据，但肯定不存在当前代账公司下的客户，可以放心增行（如果有尚未关联客户的空白行，可以用起来，直接更新，不必增行）

	            //是否存在未签约的空白行
	            sp.clearParams();
	            sp.addParam(tempcorpid);
	            boolean isExistCorpUser = singleObjectBO.isExists(null, "select 1 from ynt_corp_user where pk_tempcorp=? and pk_corp='appuse'", sp);
	            if (isExistCorpUser) {
	                sb = new StringBuilder();
	                sb.append("update ynt_corp_user ");
	                sb.append("set pk_corp=? ");
	                sb.append("where pk_tempcorp=? and pk_corp='appuse'");
	                sp = new SQLParameter();
	                sp.addParam(pk_corp); //客户公司
	                sp.addParam(tempcorpid); //服务主体
	                singleObjectBO.executeUpdate(sb.toString(), sp); //用update语句批量更新
	            } else {
	                
	            }
	        }
	        //saveDefaultUserRole(corpVO,null);
		}catch(Exception e){
		    if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		}finally{
		    billCodeServiceImpl.unLockCode(mcvo);
		}
	}
	
	private MaxCodeVO getBillCode(CorpVO corpvo){
        MaxCodeVO mcvo = new MaxCodeVO();
        mcvo.setBillType(IBillTypeCode.EP11);
        mcvo.setFieldName("innercode");
        mcvo.setTbName("bd_corp");
        mcvo.setCorpIdField("fathercorp");
        mcvo.setPk_corp(corpvo.getFathercorp());
        mcvo.setEntryCode(corpvo.getInnercode().replaceAll(" ", ""));
        mcvo.setUuid(UUID.randomUUID().toString());
        String code = billCodeServiceImpl.getBillCode(mcvo);
        mcvo.setReturnCode(code);
        return mcvo;
    }
	
	/**
	 * 保存角色权限信息
	 * @param corpvo
	 * @param roleVos
	 */
	private void insertRoleUser(CorpVO corpvo,CorpRoleVO[] roleVos) {
	    for(CorpRoleVO crvo : roleVos ){
            crvo.setPk_corp(corpvo.getPk_corp());
        }
	    corpUserRoleImpl.saveRoleUser(corpvo.getFathercorp(), roleVos);
//	    UserRoleVO uRoleVO = null;
//        for(CorpRoleVO cRoleVO : roleVos){
//            uRoleVO = new UserRoleVO();
//            uRoleVO.setPk_corp(corpvo.getPk_corp());
//            uRoleVO.setCuserid(cRoleVO.getCuserid());
//            uRoleVO.setPk_role(cRoleVO.getPk_role());
//            uRoleVO = (UserRoleVO) singleObjectBO.saveObject(corpvo.getFathercorp(), uRoleVO);
//            cRoleVO.setPk_user_role(uRoleVO.getPrimaryKey());
//            cRoleVO.setDr(0);
//            cRoleVO.setPk_corp(corpvo.getPk_corp());
//        }
//        singleObjectBO.insertVOArr(corpvo.getPk_corp(), roleVos);
    }
	
	/**
	 * 股东信息
	 * @param corpvo
	 * @param sholderVos
	 */
	private void saveSholderVO(CorpVO corpvo,CorpSholderVO[] sholderVos){
	    for(CorpSholderVO vo : sholderVos){
	        vo.setDr(0);
	        vo.setPk_corp(corpvo.getPk_corp());
	    }
	    singleObjectBO.insertVOArr(corpvo.getPk_corp(), sholderVos);
	}

    @Override
	public void updateCorp(CorpVO corpVO, HashMap sendData, File[] files, String uploadPath) throws DZFWarpException {
		String pk_corp = corpVO.getPk_corp();

//		CorpVO oldvo = null;
//		if ("益世财税".equalsIgnoreCase(corpVO.getVcustsource())) {
//			oldvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
//		}
		
		if(corpVO.getUpdatets() != null){
		    checkData(corpVO.getUpdatets(), pk_corp);
		}

		updateCorpVO(corpVO);
		updateCredenInfo(sendData,pk_corp);

		execTaxinfoUpate(sendData, pk_corp);// 更新税率税种信息 WJX
		
//		updateRoleUser(sendData, corpVO);
		
		updateSholder(sendData, corpVO);

		if(!StringUtil.isEmpty(corpVO.getUnitcode())){
		    
		    uploadPath = ImageCommonPath.getCorpFilePath(corpVO.getUnitcode(), null);
		 // 附件-新增
		    CorpDocVO[] adddocvos = (CorpDocVO[]) sendData.get("adddocvos");
		    if (adddocvos != null && adddocvos.length > 0) {
		        for (CorpDocVO docvo : adddocvos) {
		            docvo.setPk_corp(pk_corp);
		            String vfilepath = uploadPath + File.separator + docvo.getDocTemp();
		            docvo.setVfilepath(vfilepath);
		        }
		        singleObjectBO.insertVOArr(pk_corp, adddocvos);
		        for (int i = 0; i < adddocvos.length; i++) {
		        	InputStream is=null;
		        	OutputStream os=null;
		            try {
		                is= new FileInputStream(files[i]);
		                File toFile = new File(uploadPath, adddocvos[i].getDocTemp());
		                if (!toFile.getParentFile().exists()) {
		                    toFile.getParentFile().mkdirs();
		                }
		                 os= new FileOutputStream(toFile);
		                byte[] buffer = new byte[1024];
		                int length = 0;
		                while ((length = is.read(buffer)) > 0) {
		                    os.write(buffer, 0, length);
		                }
		            } catch (FileNotFoundException e) {
		                throw new BusinessException("查找文件出错" + e.getMessage());
		            } catch (IOException e) {
		                throw new BusinessException("文件读取出错" + e.getMessage());
		            } catch (Exception ep){
		                log.error("错误",ep);
		                throw new BusinessException("文件出错" + ep.getMessage());
		            }finally {
		            	try {
		            		if(is!=null)
		            			is.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
		                try {
		                	if(os!=null)
		                		os.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
		        }
		    }
		    // 附件-删除
		    CorpDocVO[] deldocvos = (CorpDocVO[]) sendData.get("deldocvos");
		    if (deldocvos != null && deldocvos.length > 0) {
		        for (CorpDocVO docvo : deldocvos) {
		            docvo.setPk_corp(pk_corp);
		        }
		        singleObjectBO.deleteVOArray(deldocvos);
		        for (CorpDocVO vo : deldocvos) {
		            // 删除上传附件
		            File folder = new File(uploadPath);
		            File[] delfiles = folder.listFiles();
		            if(delfiles != null && delfiles.length > 0){
		                for (File file : delfiles) {
		                    if (file.getName().equals(vo.getDocTemp())) {
		                        file.delete();
		                    }
		                }
		            }
		        }
		    }
		}
		// 附件-修改
		CorpDocVO[] uptdocvos = (CorpDocVO[]) sendData.get("upddocvos");
		if (uptdocvos != null && uptdocvos.length > 0) {
			for (CorpDocVO docvo : uptdocvos) {
				docvo.setPk_corp(pk_corp);
			}
			singleObjectBO.updateAry(uptdocvos, new String[]{"isscan","isdownload","filetype"});
		}


		// WJX 如果为益世客户，推送更新的税务税率信息
//		if (oldvo != null) {
//			final String fpk_corp = pk_corp;
//			@SuppressWarnings("rawtypes")
//			final HashMap fsendData = sendData;
//			final CorpVO foldvo = oldvo;
//			final CorpVO fcorpVO = corpVO;
//			pushUpdateTaxInfo(fpk_corp, fsendData, foldvo, fcorpVO);
//		}
	}
    
    /**
     * 时间戳校验
     * @param tstamp
     * @param pk_corp
     */
    private void checkData(DZFDateTime tstamp, String pk_corp) {
        CorpVO cvo =(CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
        if(cvo != null && cvo.getUpdatets() != null && !tstamp.equals(cvo.getUpdatets())){
            throw new BusinessException("数据已发生变化，请刷新后重新操作。");
        }
    }
    
    /**
     * 提醒信息
     * @param sendData
     * @param pk_corp
     */
    private void updateCredenInfo(HashMap sendData,String pk_corp){
     // 提醒信息-新增
        CorpCredenVO[] addvos = (CorpCredenVO[]) sendData.get("addvos");
        if (addvos != null && addvos.length > 0) {
            for (CorpCredenVO addvo : addvos) {
                addvo.setPk_corp(pk_corp);
            }
            singleObjectBO.insertVOArr(pk_corp, addvos);
        }

        // 提醒信息-修改
        CorpCredenVO[] updvos = (CorpCredenVO[]) sendData.get("updvos");
        if (updvos != null && updvos.length > 0) {
            for (CorpCredenVO updvo : updvos) {
                updvo.setPk_corp(pk_corp);
            }
            singleObjectBO.updateAry(updvos);
        }

        // 提醒信息-删除
        CorpCredenVO[] delvos = (CorpCredenVO[]) sendData.get("delvos");
        if (delvos != null && delvos.length > 0) {
            for (CorpCredenVO delvo : delvos) {
                delvo.setPk_corp(pk_corp);
            }
            singleObjectBO.deleteVOArray(delvos);
        }

    }

//	private void updateRoleUser(HashMap sendData, CorpVO corpVO) {
//        CorpRoleVO[] addvos = (CorpRoleVO[]) sendData.get("addRoleVos");
//        if (addvos != null && addvos.length > 0) {
//            insertRoleUser(corpVO,addvos);
//        }
//
//        CorpRoleVO[] updvos = (CorpRoleVO[]) sendData.get("updRoleVos");
//        if (updvos != null && updvos.length > 0) {
//            UserRoleVO uRoleVO = null;
//            for (CorpRoleVO updvo : updvos) {
//                uRoleVO = new UserRoleVO();
//                updvo.setPk_corp(corpVO.getPk_corp());
//                uRoleVO.setPk_role(updvo.getPk_role());
//                uRoleVO.setCuserid(updvo.getCuserid());
//                uRoleVO.setPrimaryKey(updvo.getPk_user_role());
//                singleObjectBO.update(uRoleVO, new String[]{"pk_role","cuserid"});
//            }
//            singleObjectBO.updateAry(updvos);
//        }
//
//        CorpRoleVO[] delvos = (CorpRoleVO[]) sendData.get("delRoleVos");
//        if (delvos != null && delvos.length > 0) {
//            String sqlDel = "delete from sm_user_role where pk_user_role = ? ";
//            SQLParameter param = new SQLParameter();
//            for (CorpRoleVO delvo : delvos) {
//                delvo.setPk_corp(corpVO.getPk_corp());
//                param.clearParams();
//                param.addParam(delvo.getPk_user_role());
//                singleObjectBO.executeUpdate(sqlDel, param);
//            }
//            singleObjectBO.deleteVOArray(delvos);
//        }
//    }
	
	private void updateSholder(HashMap sendData, CorpVO corpVO) {
	    String sqlDel = "delete from ynt_corpsholder where pk_corp = ? ";
	    SQLParameter param = new SQLParameter();
	    param.addParam(corpVO.getPk_corp());
	    singleObjectBO.executeUpdate(sqlDel, param);
	    CorpSholderVO[] sholderVos = (CorpSholderVO[]) sendData.get("addSholder");
        if (sholderVos != null && sholderVos.length > 0) {
            for (CorpSholderVO vo : sholderVos) {
                vo.setPk_corp(corpVO.getPk_corp());
                vo.setPk_corpsholder(null);
            }
            saveSholderVO(corpVO, sholderVos);
        }
    }

//    @SuppressWarnings("rawtypes")
//	private void pushUpdateTaxInfo(String pk_corp, HashMap sendData, CorpVO oldvo, CorpVO corpVO) {
//
//		String cd = new String("  pk_corp=? and nvl(dr,0)=0");
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk_corp);
//		CorpTaxInfoVO[] taxinfovo = null;
//		if (sendData.get("addTaxvos") != null || sendData.get("updTaxvos") != null
//				|| sendData.get("delTaxvos") != null) {
//			taxinfovo = (CorpTaxInfoVO[]) singleObjectBO.queryByCondition(CorpTaxInfoVO.class, cd, sp);
//		}
//
//		Object taxupdate = BeanUtils.getBean("ystaxsrv");
//
//		try {
//			Method m = taxupdate.getClass().getMethod("pushTaxinfoUpdate", CorpVO.class, CorpVO.class,
//					CorpTaxInfoVO[].class);
//			m.invoke(taxupdate, oldvo, corpVO, taxinfovo);
//		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException
//				| InvocationTargetException e) {
//			
//			log.error("推送更新的税务税率信息异常", e);
//			String msg = "调用益世接口异常";
//			if(e instanceof InvocationTargetException){
//				InvocationTargetException ee = (InvocationTargetException)e;
//				if(ee.getTargetException() instanceof BusinessException){
//					msg = ee.getTargetException().getMessage();
//				}			
//			}			
//			throw new BusinessException(msg);
//		}
//	}

	/**
	 * WJX 税率信息更新
	 */
	private void execTaxinfoUpate(Map sendData, String pk_corp) {
		// 提醒信息-新增
		CorpTaxInfoVO[] addvos = (CorpTaxInfoVO[]) sendData.get("addTaxvos");
		if (addvos != null && addvos.length > 0) {
			for (CorpTaxInfoVO addvo : addvos) {
				addvo.setPk_corp(pk_corp);
			}
			singleObjectBO.insertVOArr(pk_corp, addvos);
		}

		// 提醒信息-修改
		CorpTaxInfoVO[] updvos = (CorpTaxInfoVO[]) sendData.get("updTaxvos");
		if (updvos != null && updvos.length > 0) {
			for (CorpTaxInfoVO updvo : updvos) {
				updvo.setPk_corp(pk_corp);
			}
			singleObjectBO.updateAry(updvos);
		}

		// 提醒信息-删除
		CorpTaxInfoVO[] delvos = (CorpTaxInfoVO[]) sendData.get("delTaxvos");
		if (delvos != null && delvos.length > 0) {
			for (CorpTaxInfoVO delvo : delvos) {
				delvo.setPk_corp(pk_corp);
			}
			singleObjectBO.deleteVOArray(delvos);
		}
	}

	/**
	 * 反建账
	 */
	@Override
	public void updateJzCancel(CorpVO corpvo,String topCorpID) throws DZFWarpException {
		if (isExistsVO(corpvo.getPk_corp())) {
			throw new BusinessException("该公司已有期初、凭证、库存、资产、工资表、图片数据，不能反建账");
		}

		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());
		String sql = " delete from ynt_parameter where nvl(dr,0) = 0 and pk_corp = ? and issync = 0 ";
		singleObjectBO.executeUpdate(sql, sp);
		singleObjectBO.executeUpdate("delete from Ynt_Bd_Abstracts where nvl(dr,0) = 0 and pk_corp = ? ", sp);
		singleObjectBO.executeUpdate("delete from ynt_cpaccount where nvl(dr,0) = 0 and pk_corp = ? ", sp);
		//收入预警
		singleObjectBO.executeUpdate("delete from ynt_IncomeWarning where pk_corp = ? ", sp);
		singleObjectBO.executeUpdate("delete from ynt_salaryaccset where pk_corp = ? ", sp);
		singleObjectBO.executeUpdate("delete from ynt_sjwh_dataupgrade where pk_corp = ? ", sp);
		singleObjectBO.executeUpdate("delete from ynt_personalset where pk_corp = ? ", sp);
		//
		singleObjectBO.executeUpdate("delete from ynt_glicset where pk_corp = ? ", sp);
		
	      //销项--主子表数据
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from ynt_vatsaleinvoice_b where pk_corp = ? and pk_vatsaleinvoice in (select pk_vatsaleinvoice from ynt_vatsaleinvoice where pk_corp = ?)", sp);
        sp.clearParams();
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from ynt_vatsaleinvoice where pk_corp = ? ", sp);
        //进项--主子表数据
        sp.clearParams();
        sp.addParam(corpvo.getPk_corp());
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from ynt_vatincominvoice_b where pk_corp = ? and pk_vatincominvoice in (select pk_vatincominvoice from ynt_vatincominvoice where pk_corp = ?)", sp);
        sp.clearParams();
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from ynt_vatincominvoice where pk_corp = ? ", sp);
        
        //期末处理
        sp.clearParams();
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from ynt_qmcl where pk_corp = ?", sp);
        
        //年结
        sp.clearParams();
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from ynt_qmjz where pk_corp = ?", sp);
        
        //年末结账处理
        sp.clearParams();
        sp.addParam(corpvo.getPk_corp());
        singleObjectBO.executeUpdate("delete from YNT_KMQMJZ where pk_corp = ?", sp);
        
        //智能财税需要删除的数据
        singleObjectBO.executeUpdate("delete from YNT_BASECATEGORY where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from YNT_BILLCATEGORY where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_category_keyword where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_categoryset where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_categoryset_fzhs where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_blacklist where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_vouchertemplet_h where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_vouchertemplet_b where pk_corp = ?", sp);
        singleObjectBO.executeUpdate("delete from ynt_para_set where pk_corp = ?", sp);
        
        
		corpvo.setBbuildic(IcCostStyle.IC_OFF);
		corpvo.setIshasaccount(DZFBoolean.FALSE);
		corpvo.setHoldflag(DZFBoolean.FALSE);
		corpvo.setBegindate(null);
		corpvo.setIcbegindate(null);
		corpvo.setBusibegindate(null);
		corpvo.setAccountcoderule(null);
		singleObjectBO.update(corpvo);
	}

	/**
	 * 反建账时的校验
	 * 
	 * @param pk_corp
	 * @return
	 */
	private boolean isExistsVO(String pk_corp) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" select pk_corp from ( select a.pk_corp from ynt_qcye a   ");
		sql.append(
				" where  (nvl(a.thismonthqc,0) <> 0  or nvl( a.yearjffse,0) <> 0 or nvl(a.yeardffse,0) <> 0  or  nvl(a.yearqc,0) <> 0  )  and nvl(a.dr,0) = 0 ");
		sql.append(" union all select b.pk_corp  from ynt_tzpz_h b where nvl(b.dr,0) = 0 ");
		sql.append(" union all select c.pk_corp  from ynt_assetcard c where nvl(c.dr,0) = 0    ");
		sql.append(" union all select d.pk_corp from ynt_inventory d  where nvl(d.dr,0) = 0  ");
		sql.append(" union all select f.pk_corp from ynt_salaryreport f  where nvl(f.dr,0) = 0  ");
		sql.append(" union all select e.pk_corp from ynt_image_group e   where nvl(e.dr,0) = 0 and nvl(e.istate,0) != 205 and nvl(e.istate,0) != 80  )    ");
		sql.append(" where pk_corp = ?  ");
		return singleObjectBO.isExists(pk_corp, sql.toString(), sp);
	}

	@Override
	public void updateHflagTy(CorpVO corpvo) throws DZFWarpException {
		CorpVO corp = queryByID(corpvo.getPk_corp());
		if (corp == null) {
			throw new BusinessException("该数据不存在，或已被删除！");
		}
		if (!corp.getHoldflag().booleanValue()) {
			throw new BusinessException("不是启用状态，不能停用");
		}
		if (checkHflagUsed(corpvo)) {
			throw new BusinessException("固定资产已被使用，不能停用");
		}
		singleObjectBO.update(corpvo, new String[] { "holdflag", "busibegindate" });
	}

	private boolean checkHflagUsed(CorpVO corpvo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());
		StringBuffer sql = new StringBuffer();
		sql.append(" select pk_corp from ynt_assetcard where nvl(dr,0) = 0  ");
		sql.append(" and pk_corp = ?");
		return singleObjectBO.isExists(corpvo.getPk_corp(), sql.toString(), sp);
	}

	@Override
	public void updateBuildicTy(CorpVO corpvo) throws DZFWarpException {
		CorpVO corp = queryByID(corpvo.getPk_corp());
		if (corp == null) {
			throw new BusinessException("该数据不存在，或已被删除！");
		}
		if (!IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
			throw new BusinessException("不是启用状态，不能停用");
		}
		// 2018-06-14  取消停用校验控制 停用后删除库存数据
//		if (checkBuildicUsed(corpvo)) {
//			throw new BusinessException("库存已被使用，不能停用");
//		}
		singleObjectBO.update(corpvo, new String[] { "bbuildic", "icbegindate", "ibuildicstyle" });
		updateFzData(corpvo);
	}
	// 更新库存存货的数据到存货辅助  并删除库存数据 并更新对应的凭证
	private void updateFzData(CorpVO corpvo) {

		// 查询库存存货
		List<InventoryVO> invlist = query(corpvo.getPk_corp());
		if (invlist == null || invlist.size() == 0) {
			return;
		}
		// 转换存货vo
		List<AuxiliaryAccountBVO> fzlist = new ArrayList<>();
		for (InventoryVO vo : invlist) {
			AuxiliaryAccountBVO invvo = new AuxiliaryAccountBVO();
			invvo.setCode(vo.getCode());
			invvo.setName(vo.getName());
			invvo.setSpec(vo.getInvspec());
			//invvo.setInvtype(vo.getInvtype());
			invvo.setPk_subject(vo.getPk_subject());
			invvo.setXslx(vo.getXslx());
			invvo.setUnit(vo.getMeasurename());
			invvo.setPk_corp(corpvo.getPk_corp());
			invvo.setDr(0);
			invvo.setPk_auacount_h("000001000000000000000006");
			fzlist.add(invvo);
		}

		// 保存存货vo
		String[] pks = singleObjectBO.insertVOArr(corpvo.getPk_corp(),
				fzlist.toArray(new AuxiliaryAccountBVO[fzlist.size()]));

		// 记录pk对照关系
		SQLParameter sp = new SQLParameter();
		List<SQLParameter> list = new ArrayList<SQLParameter>();
		int size = invlist.size();
		for (int i = 0; i < size; i++) {
			sp = new SQLParameter();
			sp.addParam("");
			sp.addParam(pks[i]);
			sp.addParam(invlist.get(i).getPk_inventory());
			sp.addParam(corpvo.getPk_corp());
			list.add(sp);
		}
		// 更新公司所有凭证
		StringBuffer sb = new StringBuffer();
		sb.append(" update ynt_tzpz_b set pk_inventory=?,fzhsx6 =? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
		
		updateTzpzHVO(corpvo);
		
		sb.setLength(0);
		list.clear();
		for (int i = 0; i < size; i++) {
			sp = new SQLParameter();
			sp.addParam(pks[i]);
			sp.addParam(invlist.get(i).getPk_inventory());
			sp.addParam(corpvo.getPk_corp());
			list.add(sp);
		}
		sb.append(" update ynt_fzhsqc set fzhsx6=? where fzhsx6 =?  and pk_corp=? and nvl(dr,0)=0");
		singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
		
		deleteIcBill(corpvo, invlist);
		
		//删除别名
//		SQLParameter sp1 = new SQLParameter();
//		sp1.addParam(corpvo.getPk_corp());
//		singleObjectBO.executeUpdate("delete from ynt_icalias where pk_corp = ? ", sp1);
		
		//更新别名
		sb.setLength(0);
		sb.append(" update ynt_icalias set pk_inventory=? where pk_inventory =?  and pk_corp=? and nvl(dr,0)=0");
		singleObjectBO.executeBatchUpdate(sb.toString(), list.toArray(new SQLParameter[list.size()]));
	}
	
	private void updateTzpzHVO(CorpVO corpvo){
		String pk_corp = corpvo.getPk_corp();
		
		StringBuffer inner = buildInnerSql();
		
		SQLParameter sp = new SQLParameter();
		//更新凭证主表来源 'HP75'-> 'HP90'
		StringBuffer tzpzhsf = new StringBuffer();
		tzpzhsf.append(" update ynt_tzpz_h h ");//凭证表
		tzpzhsf.append(inner.toString());
		
		StringBuffer pzrelsf = new StringBuffer();
		pzrelsf.append(" update ynt_pz_sourcerelation h ");//关联表
		pzrelsf.append(inner.toString());
		
		sp.addParam(IBillTypeCode.HP90);
		sp.addParam(IBillTypeCode.HP90);
		sp.addParam(IBillTypeCode.HP75);
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		sp.addParam(IBillTypeCode.HP90);
		sp.addParam(IBillTypeCode.HP75);
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		
		int count = singleObjectBO.executeUpdate(tzpzhsf.toString(), sp);
		String msg = String.format("公司(%s),停用库存,销项发票,更新%d条凭证信息,param:%s,sql:%s", 
				pk_corp, count, sp.toString(),tzpzhsf.toString());
		log.info(msg);
		
		count = singleObjectBO.executeUpdate(pzrelsf.toString(), sp);
		msg = String.format("公司(%s),停用库存,销项发票,更新%d条凭证关联信息,param:%s,sql:%s", 
				pk_corp, count, sp.toString(), pzrelsf.toString());
		log.info(msg);
		
		sp.clearParams();
		sp.addParam(IBillTypeCode.HP95);
		sp.addParam(IBillTypeCode.HP95);
		sp.addParam(IBillTypeCode.HP70);
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		sp.addParam(IBillTypeCode.HP95);
		sp.addParam(IBillTypeCode.HP70);
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		
		count = singleObjectBO.executeUpdate(tzpzhsf.toString(), sp);
		msg = String.format("公司(%s),停用库存,进项发票,更新%d条凭证信息,param:%s,sql:%s", 
				pk_corp, count, sp.toString(), tzpzhsf.toString());
		log.info(msg);
		
		count = singleObjectBO.executeUpdate(pzrelsf.toString(), sp);
		msg = String.format("公司(%s),停用库存,进项发票,更新%d条凭证关联信息,param:%s, sql:%s", 
				pk_corp, count, sp.toString(), pzrelsf.toString());
		log.info(msg);
	}
	
	private StringBuffer buildInnerSql(){
		StringBuffer inner = new StringBuffer();
		inner.append("    set h.sourcebilltype = ?, ");//'HP90'
		inner.append("        h.sourcebillid  = ");
		inner.append("        (select y.sourcebillid ");
		inner.append("           from ynt_ictrade_h y ");
		inner.append("          Where nvl(y.dr, 0) = 0 ");
		inner.append("            and nvl(h.dr, 0) = 0 ");
		inner.append("            and y.sourcebilltype = ? ");//'HP90'
		inner.append("            and h.sourcebilltype = ? ");//'HP75'
		inner.append("            and y.pk_corp = ? ");
		inner.append("            and h.pk_corp = ? ");
		inner.append("            and h.sourcebillid = y.pk_ictrade_h) ");

		inner.append("  Where exists (select 1 ");
		inner.append("           from ynt_ictrade_h y ");
		inner.append("          Where nvl(y.dr, 0) = 0 ");
		inner.append("            and nvl(h.dr, 0) = 0 ");
		inner.append("            and y.sourcebilltype = ? ");//'HP90'
		inner.append("            and h.sourcebilltype = ? ");//'HP75'
		inner.append("            and y.pk_corp = ? ");
		inner.append("            and h.pk_corp = ? ");
		inner.append("            and h.sourcebillid = y.pk_ictrade_h) ");
		
		return inner;
	}
		
		// 删除库存节点数据
	private void deleteIcBill(CorpVO corpvo, List<InventoryVO> invlist) {

		// 删除存货信息
		singleObjectBO.deleteVOArray(invlist.toArray(new InventoryVO[invlist.size()]));

		String sql = null;
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());

		// 删除库存期初
		sql = " delete from ynt_icbalance where pk_corp =? ";
		singleObjectBO.executeUpdate(sql, sp);

		// 删除计量单位
		sql = " delete from ynt_measure where pk_corp =? ";
		singleObjectBO.executeUpdate(sql, sp);

		// 删除存货分类
		sql = " delete from ynt_invclassify where pk_corp =? ";
		singleObjectBO.executeUpdate(sql, sp);

		// 删除出入库单
		sql = " delete from ynt_ictradein where pk_corp =?  ";
		singleObjectBO.executeUpdate(sql, sp);
		sql = " delete from ynt_ictradeout where pk_corp =? ";
		singleObjectBO.executeUpdate(sql, sp);

//		if (corpvo.getIbuildicstyle() == null || corpvo.getIbuildicstyle().intValue() != 1) {
			// 删除销售单据（老模式）
			sql = " delete from ynt_subinvtory where pk_corp = ?  ";
			singleObjectBO.executeUpdate(sql, sp);
//		} else {
			// 删除出入库单主表（新模式）
			sql = " delete from ynt_ictrade_h where pk_corp = ?  ";
			singleObjectBO.executeUpdate(sql, sp);
			
			// 删除入账设置
			sql = " delete from bd_invaccset where pk_corp =? ";
			singleObjectBO.executeUpdate(sql, sp);

//		}
		
		// 更新销进项清单的出入库标志
		sql = " update ynt_vatsaleinvoice set pk_ictrade_h = null where pk_corp =? ";
		singleObjectBO.executeUpdate(sql, sp);
		
		sql = " update ynt_vatincominvoice set pk_ictrade_h = null where pk_corp =? ";
		singleObjectBO.executeUpdate(sql, sp);
	}
		
	public List<InventoryVO> query(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer sf = new StringBuffer();
		sf.append(" select  ry.*,re.name measurename from ynt_inventory  ry  ");
		sf.append(" left join ynt_measure re on re.pk_measure = ry.pk_measure ");
		sf.append(" where nvl(ry.dr,0) = 0 and ry.pk_corp = ? ");
		List<InventoryVO> ancevos = (List<InventoryVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(InventoryVO.class));
		if (ancevos == null || ancevos.size() == 0)
			return null;
		return ancevos;
	}

//	private boolean checkBuildicUsed(CorpVO corpvo) {
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(corpvo.getPk_corp());
//		StringBuffer sql = new StringBuffer();
//		sql.append(" select pk_corp from ynt_ictradein where nvl(dr,0) = 0  ");
//		sql.append(" and pk_corp = ?");
//		boolean in = singleObjectBO.isExists(corpvo.getPk_corp(), sql.toString(), sp);
//		if (in)
//			return true;
//
//		sql = new StringBuffer();
//		sql.append(" select pk_corp from ynt_ictradeout where nvl(dr,0) = 0  ");
//		sql.append(" and pk_corp = ?");
//		return singleObjectBO.isExists(corpvo.getPk_corp(), sql.toString(), sp);
//	}
	
	@Override
	public CorpVO[] queryCorpRefByYS(QueryParamVO queryvo, UserVO uservo) throws DZFWarpException{
		// 根据查询条件查询公司的信息
//		StringBuffer corpsql = new StringBuffer();
		String sql = getRefQuerySqlByYS(queryvo,uservo);
		SQLParameter param = getRefParam(queryvo,uservo);
//		corpsql.append(" select * from ( SELECT ROWNUM AS ROWNO, tt.* FROM ( ");
//		corpsql.append(sql);
//		queryvo.setOrder("bd_corp.unitcode");
//		String order = queryvo.getOrder();
//		int pageNo = queryvo.getPage();
//		int pageSize = queryvo.getRows();
//		if (order != null) {
//			corpsql.append(" order by " + order + " desc) tt WHERE ROWNUM<="
//					+ pageNo * pageSize);
//		} else {
//			corpsql.append("AND ROWNUM<=" + pageNo * pageSize + " ");
//		}
//		
//		if(queryvo.getXswyewfs()!=null){
//			corpsql.append(" and nvl(tt.ispersonal,'N') = '" +queryvo.getXswyewfs()+"'");
//		}
//		
//		corpsql.append(" ) WHERE ROWNO> " + (pageNo - 1) * pageSize + " ");
		List<CorpVO> vos = (List<CorpVO>) singleObjectBO.executeQuery(sql
				.toString(), param, new BeanListProcessor(
				CorpVO.class));
		QueryDeCodeUtils.decKeyUtils( new String[] { "legalbodycode", "phone1", "phone2", "unitname", "unitshortname" }, vos, 1);
		String corpname = queryvo.getCorpname();
		String corpcode = queryvo.getCorpcode();
		if(!StringUtil.isEmpty(corpname)){
			if(vos != null && vos.size() > 0){
				CorpVO[] cvos = vos.toArray(new CorpVO[0]);
				List<CorpVO> list = new ArrayList<>();
				String enf_name = null;
				String en_name = null;
				String unitname = null;
				for(CorpVO cvo : cvos){
					unitname = cvo.getUnitname();
					if(!StringUtil.isEmpty(unitname)){
						en_name = PinyinUtil.getFullSpell(unitname);
						enf_name = PinyinUtil.getFirstSpell(unitname);
					}
					if(unitname.contains(corpname) || en_name.contains(corpname)
							|| enf_name.contains(corpname)){
						list.add(cvo);
					}else if(cvo.getInnercode().contains(corpcode)){
						list.add(cvo);
					}
				}
				return list.toArray(new CorpVO[0]);
			}
		}
		return vos.toArray(new CorpVO[0]);
	}

	private String getRefQuerySqlByYS(QueryParamVO queryvo,UserVO uservo) {
		// 根据查询条件查询公司的信息s
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select bd_corp.* ");
		corpsql.append(" from bd_corp bd_corp");
		DZFBoolean ismanager = uservo.getIsmanager() == null ? DZFBoolean.FALSE:uservo.getIsmanager();
		corpsql.append(" where nvl(bd_corp.dr,0) =0 and bd_corp.fathercorp = ? and bd_corp.vcustsource = '益世财税' ");
		if(!ismanager.booleanValue()){
//			corpsql.append(" left join sm_user_role sm_user_role on sm_user_role.pk_corp = bd_corp.pk_corp");
			corpsql.append("  and bd_corp.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
		}
//		if (queryvo.getCorpcode() != null
//				&& queryvo.getCorpcode().trim().length() > 0) {
//			corpsql.append(" and (bd_corp.unitcode like ? ");
//		}
//		if (queryvo.getCorpname() != null
//				&& queryvo.getCorpname().trim().length() > 0) {
//			corpsql.append(" or bd_corp.unitname like ? )");
//		}
		
		corpsql.append(" and nvl(bd_corp.isaccountcorp,'N') = 'N' ");
		if(queryvo.getXswyewfs()!=null){
			corpsql.append(" and nvl(bd_corp.ispersonal,'N') = ? ");
		}
//		if(!ismanager.booleanValue()){
//			corpsql.append(" and sm_user_role.cuserid = ?");
//		}
		return corpsql.toString();
	}
	/**
	 * 是否为手机和网站注册用户
	 * @return
	 * @throws DZFWarpException
	 */
	private boolean isAppOrWzCorp(String pk_corp,String pk_corpk) throws DZFWarpException{
		StringBuffer sql = new StringBuffer();
		SQLParameter pam = new SQLParameter();
		sql.append("SELECT * FROM ynt_corp_user ");
		sql.append(" WHERE nvl(dr,0) = 0 and pk_corp = ? ");
		if(!StringUtil.isEmpty(pk_corp)){
			pam.addParam(pk_corpk);
		}else{
			throw new BusinessException("查询客户信息不能为空!");
		}
		return singleObjectBO.isExists(pk_corp, sql.toString(), pam);
	}

	@Override
	public void updateAccountType(CorpVO corpVO) throws DZFWarpException {
		singleObjectBO.update(corpVO, new String[]{"corptype"});
	}
	private void addVoucherTemplate (CorpVO corpvo) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(corpvo.getCorptype());
		List<SalaryModelHVO> group_models = (List<SalaryModelHVO>) singleObjectBO.executeQuery(
				"pk_corp = ? and pk_trade_accountschema = ? and nvl(dr,0) = 0",
						sp, new Class[]{SalaryModelHVO.class, SalaryModelBVO.class});
		if (group_models != null && group_models.size() > 0) {
			SalaryAccSetVO salaryTemp = new SalaryAccSetVO();
			salaryTemp.setPk_corp(corpvo.getPk_corp());
			SQLParameter param = new SQLParameter();
			for (SalaryModelHVO salaryModelHVO : group_models) {
				List<SalaryModelBVO> bvos = Arrays.asList(salaryModelHVO.getChildren());
				for (SalaryModelBVO salaryModelBVO : bvos) {
					param.clearParams();
					param.addParam(corpvo.getPk_corp());
					param.addParam(salaryModelBVO.getKmbm());
					String corp_account = (String) singleObjectBO
							.executeQuery("select pk_corp_account from ynt_cpaccount where pk_corp = ? and accountcode = ? and nvl(dr, 0) = 0 ",
									param, new ColumnProcessor());
					salaryModelBVO.setPk_accsubj(corp_account);
				}
				if (salaryModelHVO.getTemp_type() == 0) {
					for (SalaryModelBVO salaryModelBVO : bvos) {
						switch (salaryModelBVO.getZy()) {
						case "工资费用科目":
							salaryTemp.setJtgz_gzfykm(salaryModelBVO.getPk_accsubj());
							break;
						case "应付工资科目":
							salaryTemp.setJtgz_yfgzkm(salaryModelBVO.getPk_accsubj());
							break;
						case "应付社保科目":
							salaryTemp.setJtgz_yfsbkm(salaryModelBVO.getPk_accsubj());
							break;
						case "社保费用科目":
							salaryTemp.setJtgz_sbfykm(salaryModelBVO.getPk_accsubj());
							break;
						}
					}
				} else {
					for (SalaryModelBVO salaryModelBVO : bvos) {
						switch (salaryModelBVO.getZy()) {
						case "应付工资科目":
							salaryTemp.setFfgz_yfgzkm(salaryModelBVO.getPk_accsubj());
							break;
						case "公积金个人部分":
							salaryTemp.setFfgz_gjjgrbf(salaryModelBVO.getPk_accsubj());
							break;
						case "应缴个税科目":
							salaryTemp.setFfgz_grsds(salaryModelBVO.getPk_accsubj());
							break;
						case "工资发放科目":
							salaryTemp.setFfgz_xjlkm(salaryModelBVO.getPk_accsubj());
							break;
						case "个人养老保险":
							salaryTemp.setFfgz_sbgrbf(salaryModelBVO.getPk_accsubj());
							break;
						case "个人医疗保险":
							salaryTemp.setFfgz_yilbxbf(salaryModelBVO.getPk_accsubj());
							break;
						case "个人失业保险":
							salaryTemp.setFfgz_sybxbf(salaryModelBVO.getPk_accsubj());
							break;
						case "工伤保险科目":
							salaryTemp.setFfgz_gsbxkm(salaryModelBVO.getPk_accsubj());
							break;
						case "生育保险科目":
							salaryTemp.setFfgz_shybxkm(salaryModelBVO.getPk_accsubj());
							break;
						default:
							break;
						}
					}
				}
			}
			singleObjectBO.saveObject(corpvo.getPk_corp(), salaryTemp);
		}
	}
	

	@SuppressWarnings({ "unchecked", "serial" })
	@Override
	public List<String> queryStopReason(String pk_corp) throws DZFWarpException {
		
		if(StringUtil.isEmpty(pk_corp)){
			throw new BusinessException("公司信息不能为空");
		}
		
		String sql = "select distinct def17 from bd_corp where pk_corp=? or fathercorp=? and def17 is not null";
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(pk_corp);
		List<String> list = (List<String>)singleObjectBO.executeQuery(sql, params, new BaseProcessor(){

			@Override
			public Object processResultSet(ResultSet rs) throws SQLException {
				List<String> list = new ArrayList<String>();
				list.add("公司注销");
                list.add("公司迁移");
                list.add("合同到期不续签");
                list.add("请专职会计");
				while(rs.next()){
					if(!StringUtil.isEmpty(rs.getString("def17"))){
					    if(!list.contains(rs.getString("def17"))){
                            list.add(rs.getString("def17"));
                        }
					}					
				}
				return list;
			}
			
		});
		return list;
	}
	
	@Override
	public int updateChargeAccount(String[] pk_corps, String chargeAcount)
			throws DZFWarpException {

		if(pk_corps==null||pk_corps.length<=0){
			return 0;
		}
		String insql = SqlUtil.buildSqlForIn("pk_corp", pk_corps);
		String sql = "update bd_corp set vsuperaccount=? where nvl(dr,0)=0  and " + insql;
		SQLParameter params = new SQLParameter();
		params.addParam(chargeAcount);
		for(String pk_corp : pk_corps){
		    CorpVO oldvo = queryByID(pk_corp);
		    CorpVO cvo = new CorpVO();
		    cvo.setPk_corp(pk_corp);
		    cvo.setVsuperaccount(chargeAcount);
		    cvo.setFathercorp(oldvo.getFathercorp());
		    //saveDefaultUserRole(cvo, oldvo);
		}
		
		return singleObjectBO.executeUpdate(sql, params);
	}
	private void addIncomeWarning (String pk_corp, Integer accountstandard, String chargedeptname) {
        boolean isSmall = StringUtil.isEmpty(chargedeptname) || "小规模纳税人".equals(chargedeptname);
        if (!isSmall) {
            return;
        }

        String accountCode1 = null;
        // 主营业务收入
        String accountCode2 = null;
        // 其他业务收入
        String accountCode3 = null;
        if (accountstandard == 0) {
            accountCode1 = "500101";
            accountCode2 = "5001";
            accountCode3 = "5051";
        } else if (accountstandard == 1) {
            accountCode1 = "600101";
            accountCode2 = "6001";
            accountCode3 = "6051";
        } else {
            return;
        }
        SQLParameter param = new SQLParameter();
        param.addParam(pk_corp);
        param.addParam(accountCode1);
        param.addParam(accountCode2);
        param.addParam(accountCode3);
        List<YntCpaccountVO> accounts = (List<YntCpaccountVO>) singleObjectBO
                .executeQuery("select pk_corp_account,accountcode, accountname from ynt_cpaccount where pk_corp = ?" +
                                " and accountcode in (?, ?, ?) and nvl(dr, 0) = 0 ",
                        param, new BeanListProcessor(YntCpaccountVO.class));
        if (accounts.size() > 0) {
            String pk_account1 = null;
            String pk_account2 = null;
            String pk_account3 = null;
            for (YntCpaccountVO account : accounts) {
                if (accountCode1.equals(account.getAccountcode())) {
                    pk_account1 = account.getPk_corp_account();
                } else if (accountCode2.equals(account.getAccountcode())) {
                    pk_account2 = account.getPk_corp_account();
                } else if (accountCode3.equals(account.getAccountcode())) {
                    pk_account3 = account.getPk_corp_account();
                }
            }
            StringBuilder sb = null;
            if (isSmall) {
                // 小规模收入预警 商品销售收入
                sb = new StringBuilder();
                sb.append("insert into ynt_IncomeWarning (pk_sryj, pk_corp, ")
                .append("dr, xmmc, srsx, yjz, isloginremind, isinputremind, pk_accsubj) ")
                .append(" values (?, ?, 0, '收入预警', 5000000, 4900000, 'Y', 'Y', ?) ");
                param.clearParams();
                param.addParam(IDGenerate.getInstance().getNextID(pk_corp));
                param.addParam(pk_corp);
//	              param.addParam(account.getAccountname());
//	              param.addParam(account.getAccountcode());
                param.addParam(pk_account1);
                singleObjectBO.executeUpdate(sb.toString(), param);
                
                // 免交增值税预警 主营业务收入+其他业务收入
                sb = new StringBuilder();
                sb.append("insert into ynt_IncomeWarning (pk_sryj, pk_corp, ")
                .append("dr, xmmc, srsx, yjz, isloginremind, isinputremind, pk_accsubj,period_type) ")
                .append(" values (?, ?, 0, '免交增值税', ?, ?, 'Y', 'Y', ?, ?) ");
                param.clearParams();
                param.addParam(IDGenerate.getInstance().getNextID(pk_corp));
                param.addParam(pk_corp);
//	              param.addParam(account.getAccountname());
                // 上限
                param.addParam(300000);
                // 预警值
                param.addParam(290000);
//	              param.addParam(account.getAccountcode());
                param.addParam(pk_account2 + "," + pk_account3);
                // 期间类型
                param.addParam(1);
                singleObjectBO.executeUpdate(sb.toString(), param);
            }
        }
	}	
	@Override
	public int updateActiveCode(String pk_gs, String code) throws DZFWarpException {
		String sql = "update bd_corp set def11 = ? where pk_corp = ?";
		SQLParameter params = new SQLParameter();
		params.addParam(code);
		params.addParam(pk_gs);
		return singleObjectBO.executeUpdate(sql, params);
	}
	
	@Override
	public void updateTicRemin(CorpVO corpvo) throws DZFWarpException {
		singleObjectBO.update(corpvo, new String[]{"rembday","remeday"});
	}

	//更新缓存
	@Override
	public void updateTaxradio(String pk_corp, String radio) throws DZFWarpException {
		String sql = "update bd_corp set def19 = ? where pk_corp = ?";
		SQLParameter params = new SQLParameter();
		params.addParam(radio);
		params.addParam(pk_corp);
		singleObjectBO.executeUpdate(sql, params);
	}

	@Override
	public DZFDouble getTaxWarningRate(String pk_corp) throws DZFWarpException {
		String sql = "select hy.warning_rate from bd_corp corp " +
				" left join ynt_bd_trade hy on corp.industry = hy.pk_trade " +
				" where corp.pk_corp = ? and nvl(corp.dr,0)=0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		BigDecimal rate = (BigDecimal) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
		if (rate != null) {
			return new DZFDouble(rate);
		} else {
            return new DZFDouble(1);
		}
	}
	
	@Override
	public void processPicMode(CorpVO[] corps, CorpVO corpvo) throws DZFWarpException {
		
		transPicProcMode(corps, corpvo);
		
		singleObjectBO.updateAry(corps, new String[]{"def4"});
	}
	
	private void transPicProcMode(CorpVO[] corps, CorpVO corpvo){
//		int value = 0;
//		
//		if(corpvo.getIsneedocr() != null 
//				&& corpvo.getIsneedocr().booleanValue()){
//			value = PhotoState.TREAT_TYPE_1;
//		}
//
//		if(corpvo.getIsneedappro() != null
//				&& corpvo.getIsneedappro().booleanValue()){
//			if(String.valueOf(PhotoState.TREAT_TYPE_2).equals(corpvo.getDjshfs())){//平台审单
//				
//				value += PhotoState.TREAT_TYPE_2;
//			}else if(String.valueOf(PhotoState.TREAT_TYPE_4).equals(corpvo.getDjshfs())){//自行审单
//				
//				value += PhotoState.TREAT_TYPE_4;
//			}
//		}

//		String defValue = String.valueOf(value);
	    String defValue = corpvo.getDef4();
		//批量复制
		for(CorpVO vo : corps){
			vo.setDef4(defValue);
		}
	}
	
//	private void saveDefaultUserRole(CorpVO cvo,CorpVO oldvo){
//	    if(!StringUtil.isEmpty(cvo.getVsuperaccount())){
//	        String corpkid = cvo.getPk_corp();
//	        RoleVO[] roles = getRolePk();
//	        if(oldvo != null){
//	            if(!StringUtil.isEmpty(oldvo.getVsuperaccount()) && !oldvo.getVsuperaccount().equals(cvo.getVsuperaccount())){
//	                String sqlDel = null;
//	                SQLParameter param = new SQLParameter();
//	                for (RoleVO role : roles) {
//	                    param.clearParams();
//	                    sqlDel = "delete from sm_user_role where cuserid = ? and pk_corp = ? and pk_role = ?";
//	                    param.addParam(oldvo.getVsuperaccount());
//	                    param.addParam(corpkid);
//	                    param.addParam(role.getPk_role());
//	                    singleObjectBO.executeUpdate(sqlDel, param);
//	                }
//	                sys_kjryqxfpserv.saveDefaultUserRole(cvo.getVsuperaccount(),cvo.getFathercorp(), corpkid, roles);
//	            }
//	        }else{
//	            sys_kjryqxfpserv.saveDefaultUserRole(cvo.getVsuperaccount(),cvo.getFathercorp(), corpkid, roles);
//	        }
//	    }
//	}
	
	   /**
     * 角色 暂时先直接固定主键
     * 
     * @return
     */
    private RoleVO[] getRolePk() {
        IRoleMngService sys_roleadminserv = (IRoleMngService) SpringUtils.getBean("sys_roleadminserv");
        RoleVO[] vos = sys_roleadminserv.queryKeyRole();
        return vos;
    }

    @Override
    public CorpVO queryHBodys(CorpVO corpvo) throws DZFWarpException {
        CorpVO cvo = new CorpVO();
        CorpDocVO vo = new CorpDocVO();
        vo.setPk_corp(corpvo.getPk_corp());
        CorpDocVO[] list = queryChild(vo);
        cvo.setCorpDocVos(list);
        CorpCredenVO[] cCredenVOs = queryCorpCreden(corpvo);
        cvo.setCorpCredenVos(cCredenVOs);
        CorpRoleVO[] cRoleVos = queryCorpRole(corpvo);
        cvo.setCorpRoleVos(cRoleVos);
        CorpTaxInfoVO[]cTaxInfoVos = queryCorpTaxInfo(corpvo);
        cvo.setCorpTaxInfoVos(cTaxInfoVos);
        CorpSholderVO[] sholderVos = queryCorpSholder(corpvo);
        cvo.setCorpSholderVos(sholderVos);
        return cvo;
    }
    
    public CorpRoleVO[] queryCorpRole(CorpVO corpVO) throws DZFWarpException {
//        StringBuffer sql = new StringBuffer();
//        SQLParameter sp = new SQLParameter();
//        sql.append("nvl(dr,0) = 0 ");
//        String pk_corp = corpVO.getPk_corp();
//        sql.append(" and pk_corp = ?");
//        sp.addParam(pk_corp);
//        return (CorpRoleVO[]) singleObjectBO.queryByCondition(CorpRoleVO.class, sql.toString(), sp);
        ArrayList<CorpRoleVO> list = corpUserRoleImpl.queryUserPower(corpVO.getFathercorp(), new String[]{corpVO.getPk_corp()});
        if(list != null && list.size() > 0){
            QueryDeCodeUtils.decKeyUtils(new String[] { "user_name"}, list, 1);
            return list.toArray(new CorpRoleVO[0]);
        }
        return new CorpRoleVO[0];
    }
    
    private CorpSholderVO[] queryCorpSholder(CorpVO corpVO) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sql.append("nvl(dr,0) = 0 ");
        String pk_corp = corpVO.getPk_corp();
        sql.append(" and pk_corp = ?");
        sp.addParam(pk_corp);
        return (CorpSholderVO[]) singleObjectBO.queryByCondition(CorpSholderVO.class, sql.toString(), sp);
    }

    @Override
    public CorpVO queryCorpByName(String fathercorp,String uname) throws DZFWarpException {
        String condition = "nvl(dr,0) = 0 and fathercorp = ? and unitname = ?";
        SQLParameter params = new SQLParameter();
        params.addParam(fathercorp);
        params.addParam(uname);
        CorpVO[] cvos = (CorpVO[]) singleObjectBO.queryByCondition(CorpVO.class, condition, params);
        if(cvos != null && cvos.length > 0){
            return cvos[0];
        }
        return null;
    }
    
    private Integer queryAllAccountCorp(String pk_corp,DZFBoolean ishasaccount) throws DZFWarpException {
    	if(IDefaultValue.DefaultGroup.equals(pk_corp))
    		return -1;
        StringBuffer strb = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        strb.append(" select count(1) num from bd_corp bc where nvl(bc.dr,0)=0 and nvl(bc.isseal,'N')='N' start with bc.pk_corp = ? connect by prior bc.pk_corp = fathercorp ");
        sp.addParam(pk_corp);
        strb.append(" and nvl(bc.isaccountcorp,'N') = 'N'");
        if(ishasaccount != null && ishasaccount.booleanValue()){
            strb.append(" and nvl(bc.ishasaccount,'N') = ? ");
            sp.addParam(ishasaccount);
        }
        Integer num = (Integer)singleObjectBO.executeQuery(strb.toString(), sp ,new ResultSetProcessor(){

			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				Integer num = -1;
				if(rs.next()){
					num = rs.getInt("num")-1;
				}
				return num;
			}
        	
        });
        return num;
    }

    @Override
    public Integer queryAcountCorps(String pk_corp) throws DZFWarpException {
    	if(IDefaultValue.DefaultGroup.equals(pk_corp))
    		return -1;
    	Integer num = queryAllAccountCorp(pk_corp,DZFBoolean.TRUE);
        return num;
    }
    @Override
	public void updateStGenledic(CorpVO corpvo) throws DZFWarpException {
		if (corpvo.getIshasaccount() == null || !corpvo.getIshasaccount().booleanValue()) {
			throw new BusinessException("未建账，不允许启用总账核算存货。");
		}
		if(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
			throw new BusinessException("已启用存货模块。");
		}
		if(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())){
			throw new BusinessException("已启用总账核算存货。");
		}
		if(!"00000100AA10000000000BMD".equals(corpvo.getCorptype()) && !"00000100AA10000000000BMF".equals(corpvo.getCorptype())){
            throw new BusinessException("只有【企业会计准则】或【小企业会计准则】才能启用总账核算存货。");
        }
		corpvo.setBbuildic(IcCostStyle.IC_INVTENTORY);
		singleObjectBO.update(corpvo, new String[] { "bbuildic"});
        accSetService.saveDefaultValue(null,corpvo,true);
	}

	@Override
	public void updateSpGenledic(CorpVO corpvo) throws DZFWarpException {
		if (!IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())) {
			throw new BusinessException("不是启用状态，不能停用");
		}
		corpvo.setBbuildic(IcCostStyle.IC_OFF);
		singleObjectBO.update(corpvo, new String[] { "bbuildic"});
        gl_ic_invtorysetserv.deleteDefaultValue(corpvo.getPk_corp());
	}

}
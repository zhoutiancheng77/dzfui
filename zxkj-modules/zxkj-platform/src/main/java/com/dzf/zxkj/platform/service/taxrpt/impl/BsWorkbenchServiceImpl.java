package com.dzf.zxkj.platform.service.taxrpt.impl;

import com.dzf.file.fastdfs.AppException;
import com.dzf.file.fastdfs.FastDfsUtil;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.IMsgConstant;
import com.dzf.zxkj.common.constant.IRoleCodeCont;
import com.dzf.zxkj.common.constant.ISysConstants;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;
import com.dzf.zxkj.platform.model.report.KmQmJzExtVO;
import com.dzf.zxkj.platform.model.sys.*;
import com.dzf.zxkj.platform.model.tax.workbench.*;
import com.dzf.zxkj.platform.service.sys.IBDCorpTaxService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.ISysMessageJPush;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.platform.service.taxrpt.IKmQryService;
import com.dzf.zxkj.platform.service.taxrpt.ITaxRptCalCellService;
import com.dzf.zxkj.platform.service.taxrpt.IbsWorkbenchService;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellBVO;
import com.dzf.zxkj.platform.model.tax.workbench.TaxRptCalCellVO;
import com.dzf.zxkj.platform.util.QueryDeCodeUtils;
import com.dzf.zxkj.platform.vo.QrySqlSpmVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.math.BigDecimal;
import java.util.*;

/**
 * 纳税工作台实现类
 * @author zy
 *
 */
@Service("bs_workbenchserv")
@SuppressWarnings("all")
@Slf4j
public class BsWorkbenchServiceImpl implements IbsWorkbenchService {

    
	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IKmQryService kmQryService;
	
	@Autowired
	private ISysMessageJPush sysMessagePush;
	
	@Autowired
	private IUserService userService;
	
	@Autowired
	private ITaxRptCalCellService taxrptService;
	
	@Autowired
	private IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private ICorpService corpService;
	
	/**
	 * 纳税工作台取数（从客户档案取客户或工作台取数据）
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<BsWorkbenchVO> query(QueryParamVO paramvo, UserVO uservo, String[] corpks, CorpVO fcorpvo) throws DZFWarpException {
		QrySqlSpmVO qryvo = getQrySqlSpm(paramvo, uservo, corpks, fcorpvo);
		List<BsWorkbenchVO> list = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(qryvo.getSql(), qryvo.getSpm(),
				new BeanListProcessor(BsWorkbenchVO.class));
		if (list != null && list.size() > 0) {
			QueryDeCodeUtils.decKeyUtils(new String[]{"khname","pcountname"}, list, 1);
		}
		return list;
	}
	
	/**
	 * 获取查询条件
	 * @param pk_corp
	 * @param paramvo
	 * @param uservo
	 * @return
	 * @throws DZFWarpException
	 */
	private QrySqlSpmVO getQrySqlSpm(QueryParamVO paramvo, UserVO uservo, String[] corpks, CorpVO fcorpvo) throws DZFWarpException{
		String period = paramvo.getQjq();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		spm.addParam(period);
		spm.addParam(period);
		sql.append("select a.begindate, ") ;
		sql.append("       a.accountcoderule as coderule, ") ; 
		sql.append("       a.innercode as khcode, ") ; 
		sql.append("       a.unitname as khname, ") ; 
		sql.append("       a.pk_corp as pk_corp, ") ; 
		sql.append("       t.ibeginday AS rembday, ") ; 
		sql.append("       a.chargedeptname, ") ; 
		sql.append("       tax.tax_area as vprovince, ") ; 
		//查询纳税工作台字段 begin
		sql.append("       b.pk_workbench,   ") ;
		sql.append("       b.fathercorp,   ") ;
		sql.append("       nvl(b.isptx,0) AS isptx, ") ;
		sql.append("       nvl(b.iacctcheck,0) AS iacctcheck, ") ;
		sql.append("       nvl(b.taxStateCopy,0) AS taxStateCopy, ") ;
		sql.append("       b.itaxconfstate, ") ;
		sql.append("       nvl(b.taxStateFinish,0) AS taxStateFinish, ") ;
		sql.append("       nvl(b.taxStateClean,0) AS taxStateClean, ") ;
		sql.append("       nvl(b.ipzjjzt,0) AS ipzjjzt, ") ;
		sql.append("       nvl(b.isZeroDeclare,'N') AS isZeroDeclare, ") ;
		sql.append("       b.income, ") ;
		sql.append("       b.addTax, ") ;
		sql.append("       b.addpaidTax, ") ;
		sql.append("       nvl(b.addStatus,0) AS addStatus, ") ;
		sql.append("       b.exciseTax, ") ;
		sql.append("       b.excisepaidTax, ") ;
		sql.append("       nvl(b.exciseStatus,0) AS exciseStatus, ") ;
		sql.append("       b.incomeTax, ") ;
		sql.append("       b.incomepaidTax, ") ;
		sql.append("       nvl(b.incomeStatus,0) AS incomeStatus, ") ;
		sql.append("       nvl(b.erningStatus,0) AS erningStatus, ") ;//财报
		sql.append("       b.culturalTax, ") ;
		sql.append("       b.culturalpaidTax, ") ;
		sql.append("       nvl(b.culturalStatus,0) AS culturalStatus, ") ;
		sql.append("       b.additionalTax, ") ;
		sql.append("       b.additionalpaidTax, ") ;
		sql.append("       nvl(b.additionalStatus,0) AS additionalStatus, ") ;
		sql.append("       b.cityTax, ") ;
		sql.append("       b.citypaidTax, ") ;
		sql.append("       nvl(b.cityStatus,0) AS cityStatus, ") ;
		sql.append("       b.educaTax, ") ;
		sql.append("       b.educapaidTax, ") ;
		sql.append("       nvl(b.educaStatus,0) AS educaStatus, ") ;
		sql.append("       b.localEducaTax, ") ;
		sql.append("       b.localEducapaidTax, ") ;
		sql.append("       nvl(b.localEducaStatus,0) AS localEducaStatus, ") ;
		sql.append("       b.personTax, ") ;
		sql.append("       b.personpaidTax, ") ;
		sql.append("       nvl(b.personStatus,0) AS personStatus, ") ;
		sql.append("       b.stampTax, ") ;
		sql.append("       b.stamppaidTax, ") ;
		sql.append("       nvl(b.stampStatus,0) AS stampStatus, ") ;
		sql.append("       b.npaymny, ") ;
		sql.append("       b.npaidmny, ") ;
		sql.append("       b.memo, ") ;
		//查询纳税工作台字段 begin
		if(fcorpvo.getIschannel() == null || !fcorpvo.getIschannel().booleanValue()){
			sql.append("       a.vsuperaccount, ") ; 
			sql.append("       r.user_name AS pcountname, ");
		}
		sql.append("       ? as period ") ; 
		sql.append("  from bd_corp a ") ; 
		sql.append("  left join bd_corp_tax tax on tax.pk_corp = a.pk_corp ") ; 
		sql.append("  left join ynt_remindset t on a.pk_corp = t.pk_corpk and t.iremindtype = 38 ");
		if(fcorpvo.getIschannel() == null || !fcorpvo.getIschannel().booleanValue()){
			sql.append("  left join sm_user r on a.vsuperaccount = r.cuserid ");
		}
		if((paramvo.getIsywskp() != null && paramvo.getIsywskp() == -1 && 
				paramvo.getIfwgs() != null && paramvo.getIfwgs() == -1) 
				|| paramvo.getIfwgs() != null && paramvo.getIfwgs() == -1
				|| (paramvo.getIsywskp() == null && paramvo.getIfwgs() == null)){
			//1、税种或申报状态都不为空，且都为全部；2、申报状态为全部；3、税种或申报状态都为空
			sql.append("  left join ") ;
		}else{
			sql.append("  inner join ") ;
		}
		sql.append("   nsworkbench b on a.pk_corp = b.pk_corp ") ; 
		sql.append("                         and b.period = ? ") ; 
		sql.append("                         and nvl(b.dr, 0) = 0");
		sql.append(" where nvl(a.dr,0) = 0  ");   
		sql.append("   and a.fathercorp = ?  "); 
		sql.append("   and nvl(a.isaccountcorp, 'N') = 'N'  ");    
		spm.addParam(paramvo.getFathercorp());
		if(!StringUtil.isEmpty(paramvo.getPk_corp())){
			sql.append(" and a.pk_corp = ?  ");
			spm.addParam(paramvo.getPk_corp());
		}
		if(paramvo.getLevelq() != null && paramvo.getLevelq() == 0){
			sql.append(" and a.chargedeptname= '一般纳税人' ");
		}else if(paramvo.getLevelq() != null && paramvo.getLevelq() == 1){
			sql.append(" and a.chargedeptname= '小规模纳税人' ");
		}
		sql.append(" and nvl(a.isformal,'N') = 'Y'");
		if(paramvo.getIsleaf()!=null&&!paramvo.getIsleaf().booleanValue()){
			sql.append(" and nvl(a.isseal,'N') = 'N'");
		}
		//主办会计查询（旗舰版与加盟商有不同的查询逻辑）
		if(!StringUtil.isEmpty(paramvo.getUserid())){
			if(fcorpvo.getIschannel() != null && fcorpvo.getIschannel().booleanValue()){
				sql.append(" and a.pk_corp in (select pk_corp from sm_user_role where cuserid = ?  ");
				sql.append(" and pk_role = ? and nvl(dr,0) = 0) ");
				spm.addParam(paramvo.getUserid());
				spm.addParam(IRoleCodeCont.jms07_ID);
			}else{
				sql.append(" and a.vsuperaccount = ? ");
				spm.addParam(paramvo.getUserid());
			}
		}
		if(paramvo.getIsywskp() != null && paramvo.getIsywskp() != -1 && 
				paramvo.getIfwgs() != null && paramvo.getIfwgs() != -1){
			//1、税种、申报状态，当两者都不为空时：
			switch(paramvo.getIsywskp()){
				case 1://增值税
					sql.append(" and nvl(b.addStatus, 0) = ? ");//税种状态
					break;
				case 2://消费税
					sql.append(" and nvl(b.exciseStatus, 0) = ? ");
					break;
				case 3://企业所得税
					sql.append(" and nvl(b.incomeStatus, 0) = ? ");
					break;
				case 4://文化事业建设费
					sql.append(" and nvl(b.culturalStatus, 0) = ? ");
					break;
				case 5://附加税合计
					sql.append(" and nvl(b.additionalStatus = ? ");
					break;
				case 6://城建税
					sql.append(" and nvl(b.cityStatus, 0) = ? ");
					break;
				case 7://教育费附加
					sql.append(" and nvl(b.educaStatus, 0) = ? ");
					break;
				case 8://地方教育费附加
					sql.append(" and nvl(b.localEducaStatus, 0) = ? ");
					break;
				case 9://个人所得税
					sql.append(" and nvl(b.personStatus, 0) = ? ");
					break;
				case 10://印花税
					sql.append(" and nvl(b.stampStatus, 0) = ? ");
					break;
			}
			spm.addParam(paramvo.getIfwgs());//税种状态
		}else if(paramvo.getIsywskp() != null && paramvo.getIsywskp() == -1 && 
				paramvo.getIfwgs() != null && paramvo.getIfwgs() != -1){
			//2、税种为空、申报状态不为空时：
			sql.append(" and ( nvl(b.addStatus, 0) = ? ");//税种状态
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.exciseStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.incomeStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.culturalStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.additionalStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.cityStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.educaStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.localEducaStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.personStatus, 0) = ? ");
			spm.addParam(paramvo.getIfwgs());//税种状态
			sql.append(" or nvl(b.stampStatus, 0) = ? )");
			spm.addParam(paramvo.getIfwgs());//税种状态
		}
		if(paramvo.getCjq() != null && paramvo.getCjq() != -1){//省
			sql.append(" AND a.vprovince = ? ");
			spm.addParam(paramvo.getCjq());
		}
		if(paramvo.getCjz() != null && paramvo.getCjz() != -1){//市
			sql.append(" AND a.vcity = ? ");
			spm.addParam(paramvo.getCjz());
		}
		if(paramvo.getLevelz() != null && paramvo.getLevelz() != -1){//区
			sql.append(" AND a.varea = ? ");
			spm.addParam(paramvo.getLevelz());
		}
		//取数专用查询条件：
		if(corpks != null && corpks.length > 0){
			String where = SqlUtil.buildSqlForIn("a.pk_corp", corpks);
			sql.append(" and ").append(where);
			sql.append(" and nvl(a.ishasaccount,'N') = 'Y' ");
		}else{
			//普通查询，只查询已建账客户
			sql.append(" and nvl(a.ishasaccount,'N') = 'Y' ");
		}
		sql.append(" and a.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
		spm.addParam(uservo.getCuserid());
		sql.append(" order by khcode ");
		QrySqlSpmVO qryvo = new QrySqlSpmVO();
		qryvo.setSql(sql.toString());
		qryvo.setSpm(spm);
		return qryvo;
	}

	@Override
	public BsWorkbenchVO save(BsWorkbenchVO vo, Integer msgtype, UserVO uservo) throws DZFWarpException {
	    String uuid = UUID.randomUUID().toString();
		try {
			boolean issbtx = false;
			String sql = " nvl(dr,0) = 0 and period = ? and pk_corp = ? and pk_workbench is not null";
			SQLParameter spm = new SQLParameter();
			spm.addParam(vo.getPeriod());
			spm.addParam(vo.getPk_corp());
			BsWorkbenchVO[] oldVOs = (BsWorkbenchVO[]) singleObjectBO.queryByCondition(BsWorkbenchVO.class, sql, spm);
			String unitname = "";
			CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());
			if(corpvo != null){
				unitname = corpvo.getUnitname();
			}
//			if(oldVOs != null && oldVOs.length > 0){
//				CorpVO corpvo = CorpCache.getInstance().get(null, oldVOs[0].getPk_corp());
//				if(corpvo != null){
//					unitname = corpvo.getUnitname();
//				}
//			}
			//唯一性校验
			if(StringUtil.isEmpty(vo.getPk_workbench())){
				if(oldVOs != null && oldVOs.length > 0){
					throw new BusinessException("客户"+unitname+"已经在区间"+vo.getPeriod()+"存在，请刷新界面数据后，再次尝试");
				}
				if (vo.getTaxStateFinish() != null && vo.getTaxStateFinish() == 1) {// 申报完成
					issbtx = true;
				}
			} else {
				if (vo.getTaxStateFinish() != null && vo.getTaxStateFinish() == 1
						&& (oldVOs[0].getTaxStateFinish() == null
								|| (oldVOs[0].getTaxStateFinish() != null && oldVOs[0].getTaxStateFinish() == 0))) {// 申报完成
					issbtx = true;
				}
			}
//			UserVO uservo = userService.queryUserJmVOByID(vo.getCoperatorid());
			//存储操作信息
			if(msgtype == null){
				List<CorpMsgVO> msglist = queryCorpMsg(vo,uservo);
				if(msglist != null && msglist.size() > 0){
					singleObjectBO.insertVOArr(vo.getFathercorp(), msglist.toArray(new CorpMsgVO[0]));
				}
			}else{
				String msgtname = "";
				if(msgtype == 42){
					msgtname = "已送票";
				}else if(msgtype == 43){
					msgtname = "已抄税";
				}else if(msgtype == 44){
					msgtname = "已清卡";
				}else if(msgtype == 45){
					msgtname = "凭证收到";
				}
				CorpMsgVO msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				singleObjectBO.saveObject(vo.getFathercorp(), msgvo);
			}
			vo = (BsWorkbenchVO) singleObjectBO.saveObject(vo.getFathercorp(), vo);
			if(issbtx){
				saveSbwcMsg(vo, uservo, unitname);
			}
		}catch (Exception e) {
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		}
		return vo;
	}
	
	/**
	 * 申报完成，发送提醒信息
	 * @param vo
	 * @param uservo
	 * @param unitname
	 * @throws DZFWarpException
	 */
	private void saveSbwcMsg(BsWorkbenchVO vo, UserVO uservo, String unitname) throws DZFWarpException {
		StringBuffer msg = new StringBuffer();
		// 尊敬的[公司名称]，贵公司xxx(年-月)纳税申报已完成，请知悉。
		msg.append("尊敬的").append(unitname).append("，贵公司");
		msg.append(vo.getPeriod()).append("纳税申报已完成，请知悉。");

		saveSpRemind(vo.getFathercorp(), vo.getPk_corp(), msg.toString(), MsgtypeEnum.MSG_TYPE_SBWCTX.getValue(),
				MsgtypeEnum.MSG_TYPE_SBWCTX.getName(), uservo, vo.getPeriod(), vo.getPk_workbench(), null, false);
	}

	@Override
	public BsWorkbenchVO queryById(String id) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(id);
		String condition = " pk_workbench = ? and nvl(dr,0) = 0 ";
		BsWorkbenchVO[] rs = (BsWorkbenchVO[]) singleObjectBO.queryByCondition(BsWorkbenchVO.class, condition, sp);
		if (rs != null && rs.length > 0) {
			return rs[0];
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, BsWorkbenchVO> queryByCorp(QueryParamVO pamvo) throws DZFWarpException {
		Map<String, BsWorkbenchVO> map = new HashMap<String, BsWorkbenchVO>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT ");
		// 查询纳税工作台字段 begin
		sql.append("       b.pk_workbench,   ");
		sql.append("       b.fathercorp,   ");
		sql.append("       b.pk_corp,   ");
		sql.append("       b.period, ");
		sql.append("       nvl(b.isptx,0) AS isptx, ");
		sql.append("       nvl(b.iacctcheck,0) AS iacctcheck, ");
		sql.append("       nvl(b.taxStateCopy,0) AS taxStateCopy, ");
		sql.append("       b.itaxconfstate, ");
		sql.append("       nvl(b.taxStateFinish,0) AS taxStateFinish, ");
		sql.append("       nvl(b.taxStateClean,0) AS taxStateClean, ");
		sql.append("       nvl(b.ipzjjzt,0) AS ipzjjzt, ");
		sql.append("       nvl(b.isZeroDeclare,'N') AS isZeroDeclare, ");
		sql.append("       b.income, ");
		sql.append("       b.addTax, ");
		sql.append("       b.addpaidTax, ");
		sql.append("       nvl(b.addStatus,0) AS addStatus, ");
		sql.append("       b.exciseTax, ");
		sql.append("       b.excisepaidTax, ");
		sql.append("       nvl(b.exciseStatus,0) AS exciseStatus, ");
		sql.append("       b.incomeTax, ");
		sql.append("       b.incomepaidTax, ");
		sql.append("       nvl(b.incomeStatus,0) AS incomeStatus, ");
		sql.append("       nvl(b.erningStatus,0) AS erningStatus, ");// 财报
		sql.append("       b.culturalTax, ");
		sql.append("       b.culturalpaidTax, ");
		sql.append("       nvl(b.culturalStatus,0) AS culturalStatus, ");
		sql.append("       b.additionalTax, ");
		sql.append("       b.additionalpaidTax, ");
		sql.append("       nvl(b.additionalStatus,0) AS additionalStatus, ");
		sql.append("       b.cityTax, ");
		sql.append("       b.citypaidTax, ");
		sql.append("       nvl(b.cityStatus,0) AS cityStatus, ");
		sql.append("       b.educaTax, ");
		sql.append("       b.educapaidTax, ");
		sql.append("       nvl(b.educaStatus,0) AS educaStatus, ");
		sql.append("       b.localEducaTax, ");
		sql.append("       b.localEducapaidTax, ");
		sql.append("       nvl(b.localEducaStatus,0) AS localEducaStatus, ");
		sql.append("       b.personTax, ");
		sql.append("       b.personpaidTax, ");
		sql.append("       nvl(b.personStatus,0) AS personStatus, ");
		sql.append("       b.stampTax, ");
		sql.append("       b.stamppaidTax, ");
		sql.append("       nvl(b.stampStatus,0) AS stampStatus, ");
		sql.append("       b.npaymny, ");
		sql.append("       b.npaidmny, ");
		sql.append("       b.memo ");
		sql.append("  FROM nsworkbench b");
		sql.append(" WHERE nvl(b.dr,0) = 0 ");
		sql.append("   AND b.pk_corp = ? ");
		spm.addParam(pamvo.getPk_corp());
		sql.append("   AND b.period like ? ");
		spm.addParam(pamvo.getQjq() + "%");
		sql.append("   ORDER BY b.period ");
		// 查询纳税工作台字段 begin
		List<BsWorkbenchVO> list = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BsWorkbenchVO.class));
		if (list != null && list.size() > 0) {
			for (BsWorkbenchVO vo : list) {
				map.put(vo.getPeriod(), vo);
			}
		}
		return map;
	}

	@Override
	public List<BsWorkbenchVO> saveFetchData(String pk_corp, UserVO uservo, String period, String[] corpks) throws DZFWarpException {
		//1、取客户集合（包含纳税信息<如果有值>）：
		Map<String, BsWorkbenchVO> queryVoMap = queryBsVOMap(pk_corp, uservo, period, corpks);
		String[] pk_corpks = null;//客户主键数组
		if(queryVoMap != null){
			pk_corpks = queryVoMap.keySet().toArray(new String[0]);
		}
		List<BsWorkbenchVO> bslist = getTaxInfo(pk_corpks, queryVoMap, pk_corp, period);
		List<BsWorkbenchVO> retlist = new ArrayList<BsWorkbenchVO>();
		if (bslist != null && bslist.size() > 0) {
			Map<String, String> gzmap = isGz(pk_corpks, period);
			Map<String, DZFDouble> map = queryCulturalTax(period, pk_corpks);
			BsWorkbenchVO retvo = null;
			for (BsWorkbenchVO bsvo : bslist) {
				if(map != null && !map.isEmpty()){
					bsvo.setCulturalTax(getDZFDouble(map.get(bsvo.getPk_corp())));
				}
				countAdditional(bsvo);//计算附加税合计
				
				// 记账完成以查询月是否关账来统计：
				if (gzmap != null && !gzmap.isEmpty()) {
					if (!StringUtil.isEmpty(gzmap.get(bsvo.getPk_corp()))) {
						bsvo.setIacctcheck(1);
					}else{
						bsvo.setIacctcheck(0);
					}
				} else {
					bsvo.setIacctcheck(0);
				}
				
				String uuid = UUID.randomUUID().toString();
				try {
					retvo = (BsWorkbenchVO) singleObjectBO.saveObject(bsvo.getPk_corp(), bsvo);
				}catch (Exception e) {
		            if (e instanceof BusinessException)
		                throw new BusinessException(e.getMessage());
		            else
		                throw new WiseRunException(e);
				}
				retlist.add(retvo);
			}
		}
		return retlist;
	}
	
	/**
	 * 查询是否关账
	 * @param pk_corpks
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, String> isGz(String[] pk_corpks, String period) throws DZFWarpException {
		Map<String, String> map = new HashMap<String, String>();
		String where = "";
		if (pk_corpks != null && pk_corpks.length > 0) {
			where = SqlUtil.buildSqlForIn("pk_corp", pk_corpks);
		} else {
			return map;
		}
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT MAX(period) AS period, pk_corp  ");
		sql.append("  FROM ynt_qmcl  ");
		sql.append(" WHERE nvl(isgz, 'N') = 'Y'  ");
		// 只关注查询月是否关账
		sql.append("   AND period = ?  ");
		spm.addParam(period);
		sql.append(" AND ").append(where);
		sql.append(" GROUP BY pk_corp  ");

		List<BsWorkbenchVO> list = (List<BsWorkbenchVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BsWorkbenchVO.class));
		if (list != null && list.size() > 0) {
			for (BsWorkbenchVO vo : list) {
				String key = vo.getPk_corp();
				map.put(key, vo.getPeriod());
			}
		}
		return map;
	}
	
	/**
	 * 获取实缴金额
	 * 
	 * @param pk_corpks
	 * @param queryVoMap
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
	private List<BsWorkbenchVO> getTaxInfo(String[] pk_corpks, Map<String, BsWorkbenchVO> queryVoMap, String pk_corp,
			String period) throws DZFWarpException {
		List<BsWorkbenchVO> bslist = new ArrayList<BsWorkbenchVO>();
		if (pk_corpks != null && pk_corpks.length > 0) {
			for (String pk_corpk : pk_corpks) {
				BsWorkbenchVO bsvo = queryVoMap.get(pk_corpk);
				String[] kms = getAccountCodes(bsvo.getCoderule());
				Map<String, List<KmQmJzExtVO>> resMap = kmQryService.resmapvos(new String[] { pk_corpk }, kms, period);
				BsWorkbenchVO vo = null;
				if (!StringUtil.isEmpty(bsvo.getPk_workbench())) {
					vo = bsvo;
					// 原有数据，重新取数，需要先清除原有数据
					restoreData(vo);
				} else {
					vo = new BsWorkbenchVO();
				}
				vo.setFathercorp(pk_corp);
				vo.setPk_corp(pk_corpk);
				vo.setPeriod(period);
				List<KmQmJzExtVO> resList = resMap.get(pk_corpk);
				if (resList != null && resList.size() > 0) {
					for (KmQmJzExtVO resVO : resList) {
						String kmbm = resVO.getKmbm();
						DZFDouble res = resVO.getThismonthqm();
						DZFDouble fs = resVO.getDffse();
						if (kms[0].equals(kmbm) || kms[1].equals(kmbm)) {
							vo.setIncome(SafeCompute.add(fs, vo.getIncome()));
							continue;
						}
						if (kms[2].equals(kmbm) || kms[3].equals(kmbm)) {
							vo.setIncome(SafeCompute.add(fs, vo.getIncome()));
							continue;
						}
						if (kms[4].equals(kmbm)) {
							if (res != null) {
								vo.setAddTax(res);
							} else {
								vo.setAddTax(DZFDouble.ZERO_DBL);
							}
							continue;
						}
						if (kms[5].equals(kmbm)) {
							vo.setExciseTax(res);
							continue;
						}
						if (kms[6].equals(kmbm)) {
							vo.setCityTax(res);
							continue;
						}
						if (kms[7].equals(kmbm)) {
							vo.setEducaTax(res);
							continue;
						}
						if (kms[8].equals(kmbm)) {
							vo.setLocalEducaTax(res);
							continue;
						}
						if (kms[9].equals(kmbm)) {
							vo.setPersonTax(res);
							continue;
						}
						if (kms[10].equals(kmbm)) {
							vo.setIncomeTax(res);
							continue;
						}
						/*
						 * 印花税 科目编码
							4/2/2   22211106 
							4/3/2   222101106
							4/3/3   2221011006
						 */
						if ("22211106".equals(kmbm) || "222101106".equals(kmbm)
								|| "2221011006".equals(kmbm)) {
							vo.setStampTax(res);
							continue;
						}
					}
				}
				bslist.add(vo);
			}
		}
		return bslist;
	}
	
	/**
	 * 整数相减
	 * @param num1
	 * @param num2
	 * @return
	 */
	public static Integer subInteger(Integer num1, Integer num2){
		num1 = num1 == null ? 0 : num1;
		num2 = num2 == null ? 0 : num2;
		return num1 - num2;
	}
	
	/**
	 * 对已经取过数的客户再次取数，需要先将应缴、实缴、实缴状态数据清空
	 * @param vo
	 */
	private void restoreData(BsWorkbenchVO vo){
		vo.setIncome(null);//收入
		vo.setAddTax(null);// 增值税（应缴）
		vo.setExciseTax(null);// 消费税（应缴）
		vo.setIncomeTax(null);// 企业所得税（应缴）
		vo.setCityTax(null);// 城建税（应缴）
		vo.setEducaTax(null);// 教育费附加（应缴）
		vo.setLocalEducaTax(null);// 地方教育费附加（应缴）
		vo.setPersonTax(null);// 个人所得税（应缴）
		vo.setCulturalTax(null);//文化事业建设费（应缴）
		vo.setAdditionalTax(null);//附加税合计（应缴）
		vo.setStampTax(null);//印花税（应缴）
		
		vo.setAddpaidTax(null);// 增值税（实缴）
		vo.setExcisepaidTax(null);// 消费税（实缴）
		vo.setIncomepaidTax(null);// 企业所得税（实缴）
		vo.setCitypaidTax(null);// 城建税（实缴）
		vo.setEducapaidTax(null);// 教育费附加（实缴）
		vo.setLocalEducapaidTax(null);// 地方教育费附加（实缴）
		vo.setPersonpaidTax(null);// 个人所得税（实缴）
		vo.setCulturalpaidTax(null);//文化事业建设费（实缴）
		vo.setAdditionalpaidTax(null);//附加税合计（实缴）
		vo.setStamppaidTax(null);//印花税（实缴）
		
		vo.setAddStatus(null);// 增值税（实缴）状态
		vo.setExciseStatus(null);// 消费税（实缴）状态
		vo.setIncomeStatus(null);// 企业所得税（实缴）状态
		vo.setCityStatus(null);// 城建税（实缴）状态
		vo.setEducaStatus(null);// 教育费附加（实缴）状态
		vo.setLocalEducaStatus(null);// 地方教育费附加（实缴）状态
		vo.setPersonStatus(null);// 个人所得税（实缴）状态
		vo.setCulturalStatus(null);//文化事业建设费（实缴）状态
		vo.setAdditionalStatus(null);//附加税合计（实缴）状态
		vo.setStampStatus(null);//印花税（实缴）状态
	}
	
	/**
	 * 计算附加税合计
	 * @param bsvo
	 * @throws DZFWarpException
	 */
	private void countAdditional(BsWorkbenchVO bsvo) throws DZFWarpException {
		DZFDouble sum = DZFDouble.ZERO_DBL;
		if (bsvo.getCityTax() != null) {
			sum = SafeCompute.add(sum, bsvo.getCityTax());
		}
		if (bsvo.getEducaTax() != null) {
			sum = SafeCompute.add(sum, bsvo.getEducaTax());
		}
		if (bsvo.getLocalEducaTax() != null) {
			sum = SafeCompute.add(sum, bsvo.getLocalEducaTax());
		}
		if (bsvo.getCityTax() != null || bsvo.getEducaTax() != null 
				|| bsvo.getLocalEducaTax() != null) {
			bsvo.setAdditionalTax(sum);
		}
	}
	
	/**
	 * 查询文化事业建设费
	 * @param period
	 * @param corpks
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, DZFDouble> queryCulturalTax(String period, String[] corpks) throws DZFWarpException {
		Map<String, DZFDouble> map = new HashMap<String, DZFDouble>();
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT h.pk_corp, sum(nvl(b.dfmny,0)) AS mny ") ;
		sql.append("  FROM ynt_tzpz_b b ") ; 
		sql.append("  LEFT JOIN ynt_tzpz_h h ON b.pk_tzpz_h = h.pk_tzpz_h ") ; 
		sql.append(" WHERE nvl(b.dr, 0) = 0 ") ; 
		sql.append("   AND nvl(h.dr, 0) = 0 ") ; 
		sql.append("   AND b.pk_accsubj in ") ; 
		sql.append("       (SELECT t.pk_corp_account ") ; 
		sql.append("          FROM ynt_cpaccount t ") ; 
		sql.append("         where t.accountname like '%文化事业建设费%') ") ; 
		sql.append("   AND h.period = ? ") ; 
		spm.addParam(period);
		if(corpks != null && corpks.length > 0){
			String where = SqlUtil.buildSqlForIn("h.pk_corp", corpks);
			sql.append(" AND ").append(where);
		}
		sql.append(" GROUP BY h.pk_corp, b.pk_accsubj, h.period");
		ArrayList<Object> result = (ArrayList<Object>) singleObjectBO.executeQuery(sql.toString(), spm, new ArrayListProcessor());
		if(result != null && !result.isEmpty()){	
			for(int i=0;i<result.size();i++){
				Object[] obj = (Object[]) result.get(i); 
				map.put((String)obj[0], getDZFDouble(obj[1]));
			}
		}
		return map;
	}
	
	/**
	 * 获取科目编码
	 * @param codeRule
	 * @return
	 */
	private String[] getAccountCodes(String codeRule) {
		String pCode = "2221";
		if (!StringUtil.isEmpty(codeRule)) {
			String[] ruleArray = codeRule.split("/");
			if (ruleArray != null && ruleArray.length > 1) {
				int length = Integer.valueOf(ruleArray[1]);
				//二级科目编码补0
				for (int i = 2; i < length; i++) {
					pCode += "0";
				}
			}
		}
		String[] kms = new String[14];
		//13主营业务收入
		kms[0] = "5001";
		//07主营业务收入
		kms[1] = "6001";
		//13其他业务收入
		kms[2] = "5051";
		//07其他业务收入
		kms[3] = "6051";
		//增值税
		kms[4] = pCode + "09";
		//消费税
		kms[5] = pCode + "08";
		//城建税
		kms[6] = pCode + "02";
		//教育费附加
		kms[7] = pCode + "03";
		//地方教育费附加
		kms[8] = pCode + "04";
		//个人所得税
		kms[9] = pCode + "05";
		//所得税
		kms[10] = pCode + "06";
		//印花税
		/*
		 * 印花税 科目编码
			4/2/2   22211106 
			4/3/2   222101106
			4/3/3   2221011006
		 */
		kms[11] = "22211106";
		kms[12] = "222101106";
		kms[13] = "2221011006";
		return kms;
	}
	
	/**
	 * 取客户集合（包含纳税信息<如果有值>）
	 * @param pk_corp
	 * @param user
	 * @param period
	 * @param corpks 界面传递客户主键
	 * @return
	 */
	private Map<String, BsWorkbenchVO> queryBsVOMap (String pk_corp, UserVO user, String period, String[] corpks) {
		Map<String, BsWorkbenchVO> voMap = null;
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setQjq(period);
		paramvo.setFathercorp(pk_corp);
		CorpVO fcorpvo = corpService.queryByPk(pk_corp);
		List<BsWorkbenchVO> list = query(paramvo, user, corpks, fcorpvo);
		if(list != null && list.size() > 0){
			voMap = new HashMap<String, BsWorkbenchVO>();
			for(BsWorkbenchVO vo : list){
				voMap.put(vo.getPk_corp(), vo);
			}
		}
		return voMap;
	}
	
	/**
	 * 权限公司过滤（管理员：看到该会计公司所有客户和委托客户；普通会计：看到该公司分配的客户和委托客户；）
	 * @param uservo
	 * @param pk_corp
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Map<String, CorpVO> queryPowerCorp (UserVO uservo, String pk_corp) {
		Map<String, CorpVO> corpMap = new HashMap<String, CorpVO>();;
		SQLParameter spm = new SQLParameter();
		StringBuffer sql  = new StringBuffer();
		sql.append("select cp.pk_corp, cp.accountcoderule as def11, cp.begindate  ") ;
		sql.append("  from bd_corp cp  ") ; 
		sql.append(" where nvl(cp.dr, 0) = 0  ") ; 
		sql.append("   and nvl(cp.isaccountcorp, 'N') = 'N'") ; 
		sql.append("   and cp.pk_corp in (select pk_corp from sm_user_role where cuserid = ?)");
		spm.addParam(uservo.getCuserid());
		List<CorpVO> list = (List<CorpVO>)singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(CorpVO.class));
		if(list != null && list.size() > 0){
			corpMap = new HashMap<String, CorpVO>();
			for(CorpVO cvo : list){
				corpMap.put(cvo.getPk_corp(), cvo);
			}
		}
		return corpMap;
	}

	@Override
	public Set<String> queryPowerCorpSet(UserVO user, String pk_corp)
			throws DZFWarpException {
		Map<String, CorpVO> corpMap = queryPowerCorp(user, pk_corp);
		return corpMap.keySet();
	}

	@Override
	public void saveRemindMsg(String pk_corp, String pk_corpk, String msgtype, UserVO uvo, String qj)
			throws DZFWarpException {
	    String uuid = UUID.randomUUID().toString();
		try {
			BsWorkbenchVO bsvo = new BsWorkbenchVO();
			bsvo.setPk_corp(pk_corpk);
			bsvo.setPeriod(qj);
			bsvo.setCoperatorid(uvo.getCuserid());
			String sql = " nvl(dr,0) = 0 and period = ? and pk_corp = ? and pk_workbench is not null";
			SQLParameter spm = new SQLParameter();
			spm.addParam(qj);
			spm.addParam(pk_corpk);
			BsWorkbenchVO[] bsVOs = (BsWorkbenchVO[]) singleObjectBO.queryByCondition(BsWorkbenchVO.class, sql, spm);
			// 应缴、实缴发送消息前校验
			if (msgtype.equals(IMsgConstant.ZZJD_TYPE_5) || msgtype.equals(IMsgConstant.ZZJD_TYPE_6)) {
				checkPayRemind(bsVOs, pk_corp, pk_corpk, msgtype, uvo, qj);
			}
			if (bsVOs != null && bsVOs.length > 0) {
				bsvo = bsVOs[0];
			} else {
				bsvo.setFathercorp(pk_corp);
				bsvo = (BsWorkbenchVO) singleObjectBO.saveObject(pk_corp, bsvo);
			}
			StringBuffer msg = null;
			String txmsg = "";
			DZFDouble totalmny = DZFDouble.ZERO_DBL;
			String remdate = getRemindDate(qj);
			if (msgtype.equals(IMsgConstant.ZZJD_TYPE_1)) {
				msg = new StringBuffer();
				msg.append("您好！征期将至，");
				msg.append("请尽快把").append(remdate).append("的各类发票（银行回单、银行对账单、销售发票、购货发票、认证清单、发票汇总表、费用单据等）交接给记账会计!");
				saveSpRemind(pk_corp, pk_corpk, msg.toString(), MsgtypeEnum.MSG_TYPE_SPTX.getValue(),
						MsgtypeEnum.MSG_TYPE_SPTX.getName(), uvo, qj, bsvo.getPk_workbench(), null, true);
			} else if (msgtype.equals(IMsgConstant.ZZJD_TYPE_2)) {
				msg = new StringBuffer();
				msg.append("您好！").append(remdate).append("报税期已经开始了，需要贵公司协助税控进行抄报，");
				msg.append("抄报后代账会计将为您进行报税，如不抄报税控器的情况下将影响报税，会导致其他罚款等事件。谢谢合作！ ");
				saveSpRemind(pk_corp, pk_corpk, msg.toString(), MsgtypeEnum.MSG_TYPE_CSTX.getValue(),
						MsgtypeEnum.MSG_TYPE_CSTX.getName(), uvo, qj, bsvo.getPk_workbench(), null, true);
			} else if (msgtype.equals(IMsgConstant.ZZJD_TYPE_3)) {
				msg = new StringBuffer();
				msg.append("感谢您的配合！").append(remdate).append("的缴税已经全部申报完，若您有税控器， ");
				msg.append("请您于15号以前清卡或反写，以免税控被锁死。 为了避免给您造成不必要的麻烦，请一定要及时清卡！已经操作的客户，可忽略此条信息提醒。");
				saveSpRemind(pk_corp, pk_corpk, msg.toString(), MsgtypeEnum.MSG_TYPE_QKTX.getValue(),
						MsgtypeEnum.MSG_TYPE_QKTX.getName(), uvo, qj, bsvo.getPk_workbench(), null, true);
			} else if (msgtype.equals(IMsgConstant.ZZJD_TYPE_4)) {
				msg = new StringBuffer();
				msg.append("您好，").append(remdate).append("的凭证已寄出（已交接）请注意查收。");
				saveSpRemind(pk_corp, pk_corpk, msg.toString(), MsgtypeEnum.MSG_TYPE_JJPZTX.getValue(),
						MsgtypeEnum.MSG_TYPE_JJPZTX.getName(), uvo, qj, bsvo.getPk_workbench(), null, true);
			} else if (msgtype.equals(IMsgConstant.ZZJD_TYPE_5)) {
				Map<String,Object> retmap = getYjPayRemindMsg(bsvo, pk_corp, pk_corpk);
				if(retmap != null && !retmap.isEmpty()){
					if(retmap.get("msg") != null){
						txmsg = String.valueOf(retmap.get("msg"));
					}
					if(retmap.get("mny") != null){
						totalmny = getDZFDouble(retmap.get("mny"));
					}
				}
				saveSpRemind(pk_corp, pk_corpk, txmsg, MsgtypeEnum.MSG_TYPE_YJTX.getValue(),
						MsgtypeEnum.MSG_TYPE_YJTX.getName(), uvo, qj, bsvo.getPk_workbench(), totalmny, true);
				updateTaxStatus(bsvo);
			} else if (msgtype.equals(IMsgConstant.ZZJD_TYPE_6)) {
				Map<String,Object> retmap = getSjPayRemindMsg(bsvo, pk_corp, pk_corpk);
				if(retmap != null && !retmap.isEmpty()){
					if(retmap.get("msg") != null){
						txmsg = String.valueOf(retmap.get("msg"));
					}
					if(retmap.get("mny") != null){
						totalmny = getDZFDouble(retmap.get("mny"));
					}
				}
				saveSpRemind(pk_corp, pk_corpk, txmsg, MsgtypeEnum.MSG_TYPE_SJTX.getValue(),
						MsgtypeEnum.MSG_TYPE_SJTX.getName(), uvo, qj, bsvo.getPk_workbench(), totalmny, true);
			}
		}catch (Exception e) {
            if (e instanceof BusinessException)
                throw new BusinessException(e.getMessage());
            else
                throw new WiseRunException(e);
		}
	}
	
	/**
	 * 获取提醒日期
	 * @param qj
	 * @return
	 * @throws DZFWarpException
	 */
	private String getRemindDate(String qj) throws DZFWarpException {
		StringBuffer str = new StringBuffer();
		String[] strs = qj.split("-");
		str.append(strs[0]).append("年");
		str.append(Integer.parseInt(strs[1])).append("月");
		return str.toString();
	}
	
	/**
	 * 更新税款状态
	 * @param bsvo
	 * @throws DZFWarpException
	 */
	private void updateTaxStatus(BsWorkbenchVO bsvo) throws DZFWarpException {
		if (bsvo.getItaxconfstate() == null || (bsvo.getItaxconfstate() != null 
				&& bsvo.getItaxconfstate() == 2)) {
			bsvo.setItaxconfstate(0);
			singleObjectBO.update(bsvo, new String[]{"itaxconfstate"});
		}
	}
	
	/**
	 * 获取应缴提醒信息
	 * @param bvo
	 * @param pk_corp
	 * @param pk_corpk
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,Object> getYjPayRemindMsg(BsWorkbenchVO bvo,String pk_corp,String pk_corpk) throws DZFWarpException {
		Map<String,Object> retmap = new HashMap<String,Object>();
		StringBuffer msg = new StringBuffer();
		msg.append("应缴提醒：您好，尊敬的 ");
		CorpVO corpvo = corpService.queryByPk(pk_corpk);
		if(corpvo != null){
			msg.append(corpvo.getUnitname()).append("公司，");
		}
	    DZFDouble sum = DZFDouble.ZERO_DBL;
		if(bvo.getAddTax() != null){
			sum = SafeCompute.add(sum, bvo.getAddTax());
		}
		if(bvo.getExciseTax() != null){
			sum = SafeCompute.add(sum, bvo.getExciseTax());
		}
		if(bvo.getIncomeTax() != null){
			sum = SafeCompute.add(sum, bvo.getIncomeTax());
		}
		if(bvo.getPersonTax() != null){
			sum = SafeCompute.add(sum, bvo.getPersonTax());
		}
		if(bvo.getCulturalTax() != null){
			sum = SafeCompute.add(sum, bvo.getCulturalTax());
		}
		if(bvo.getAdditionalTax() != null){
			sum = SafeCompute.add(sum, bvo.getAdditionalTax());
		}
		String period = getReminDate(bvo.getPeriod());
		msg.append("贵公司").append(period);
		msg.append("应缴纳税金是").append(sum.setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，其中");
		if(bvo.getAddTax() != null){// 增值税（应缴）
			msg.append("增值税").append("应缴").append(bvo.getAddTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getExciseTax() != null){//消费税（应缴）
			msg.append("消费税").append("应缴").append(bvo.getExciseTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getIncomeTax() != null){//企业所得税（应缴）
			msg.append("企业所得税").append("应缴").append(bvo.getIncomeTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getPersonTax() != null){//个人所得税（应缴）
			msg.append("个人所得税").append("应缴").append(bvo.getPersonTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getCulturalTax() != null){//文化事业建设费（应缴）
			msg.append("文化事业建设费").append("应缴").append(bvo.getCulturalTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getAdditionalTax() != null){//附加税合计（应缴）
			msg.append("附加税合计").append("应缴").append(bvo.getAdditionalTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		msg.append("请知晓！如有问题，请联系您的专属会计或致电公司电话");
		corpvo = corpService.queryByPk(pk_corp);
		if(corpvo != null){
			msg.append(corpvo.getPhone1());
		}
		retmap.put("mny", sum);
		retmap.put("msg", msg);
		return retmap;
	}
	
	/**
	 * 获取应缴提醒信息
	 * @param bvo
	 * @param pk_corp
	 * @param pk_corpk
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String,Object> getSjPayRemindMsg(BsWorkbenchVO bvo,String pk_corp,String pk_corpk) throws DZFWarpException{
		Map<String,Object> retmap = new HashMap<String,Object>();
		StringBuffer msg = new StringBuffer();
		msg.append("实缴提醒：您好，尊敬的 ");
		CorpVO corpvo = corpService.queryByPk(pk_corpk);
		if(corpvo != null){
			msg.append(corpvo.getUnitname()).append("公司，");
		}
		DZFDouble sum = DZFDouble.ZERO_DBL;
		if(bvo.getAddpaidTax() != null){
			sum = SafeCompute.add(sum, bvo.getAddpaidTax());
		}
		if(bvo.getExcisepaidTax() != null){
			sum = SafeCompute.add(sum, bvo.getExcisepaidTax());
		}
		if(bvo.getIncomepaidTax() != null){
			sum = SafeCompute.add(sum, bvo.getIncomepaidTax());
		}
		if(bvo.getPersonpaidTax() != null){
			sum = SafeCompute.add(sum, bvo.getPersonpaidTax());
		}
		if(bvo.getCulturalpaidTax() != null){
			sum = SafeCompute.add(sum, bvo.getCulturalpaidTax());
		}
		if(bvo.getAdditionalpaidTax() != null){
			sum = SafeCompute.add(sum, bvo.getAdditionalpaidTax());
		}
		String period = getReminDate(bvo.getPeriod());
		msg.append("贵公司").append(period);
		msg.append("实缴纳税金是").append(sum.setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，其中");
		if(bvo.getAddpaidTax() != null){// 增值税（实缴）
			msg.append("增值税").append("实缴").append(bvo.getAddpaidTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getExcisepaidTax() != null){//消费税（实缴）
			msg.append("消费税").append("实缴").append(bvo.getExcisepaidTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getIncomepaidTax() != null){//企业所得税（实缴）
			msg.append("企业所得税").append("实缴").append(bvo.getIncomepaidTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getPersonpaidTax() != null){//个人所得税（实缴）
			msg.append("个人所得税").append("实缴").append(bvo.getPersonpaidTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getCulturalpaidTax() != null){//文化事业建设费（实缴）
			msg.append("文化事业建设费").append("实缴").append(bvo.getCulturalpaidTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		if(bvo.getAdditionalpaidTax() != null){//附加税合计（实缴）
			msg.append("附加税合计").append("实缴").append(bvo.getAdditionalpaidTax().setScale(2, DZFDouble.ROUND_HALF_UP)).append("元，");
		}
		msg.append("以上税种均已申报成功，请知晓！如有问题，请联系您的专属会计或致电公司电话");
		corpvo = corpService.queryByPk(pk_corp);
		if(corpvo != null){
			msg.append(corpvo.getPhone1());
		}
		retmap.put("mny", sum);
		retmap.put("msg", msg);
		return retmap;
	}
	
	/**
	 * 应缴、实缴提醒消息 期间格式化
	 * @param period
	 * @return
	 */
	private String getReminDate(String period){
		if(!StringUtil.isEmpty(period)){
			String year = period.substring(0, 4);
			String month = period.substring(5);
			return year + "年" + month + "月";
		}
		return null;
	}
	
	/**
	 * 发送应缴、实缴提醒前校验
	 * @param bsVOs
	 * @param pk_corp
	 * @param pk_corpk
	 * @param msgtype
	 * @param uvo
	 * @param qj
	 */
	private void checkPayRemind(BsWorkbenchVO[] bsVOs,String pk_corp,String pk_corpk,String msgtype,UserVO uvo,String qj){
		if(bsVOs == null || bsVOs.length == 0){
			if(msgtype.equals(IMsgConstant.ZZJD_TYPE_5)){
				throw new BusinessException("您好，该公司本月的应缴税额为空，请您确认凭证是否处理正确。");
			}else if(msgtype.equals(IMsgConstant.ZZJD_TYPE_6)){
				throw new BusinessException("该公司本月的实缴税额为空，请您确认报表是否正确。");
			}
		}
		BsWorkbenchVO bvo = bsVOs[0];
		if(msgtype.equals(IMsgConstant.ZZJD_TYPE_5)){
			if(bvo.getAddTax() == null && bvo.getExciseTax() == null && bvo.getIncomeTax() == null 
//					&& bvo.getCityTax() == null && bvo.getEducaTax() == null && bvo.getLocalEducaTax() == null
					&& bvo.getPersonTax() == null && bvo.getCulturalTax() == null && bvo.getAdditionalTax() == null){
				throw new BusinessException("您好，该公司本月的应缴税额为空，请您确认凭证是否处理正确。");
			}
		}else if(msgtype.equals(IMsgConstant.ZZJD_TYPE_6)){
			if(bvo.getAddpaidTax() == null && bvo.getExcisepaidTax() == null && bvo.getIncomepaidTax() == null 
//					&& bvo.getCitypaidTax() == null && bvo.getEducapaidTax() == null && bvo.getLocalEducapaidTax() == null
					&& bvo.getPersonpaidTax() == null && bvo.getCulturalpaidTax() == null && bvo.getAdditionalpaidTax() == null){
				throw new BusinessException("该公司本月的实缴税额为空，请您确认报表是否正确。");
			}
		}
		
	}
	
	/**
	 * 保存提醒信息
	 * @param pk_corp
	 * @param pk_corpk
	 * @param msg
	 * @param msgtypevalue
	 * @param msgtypename
	 * @param uvo
	 * @param qj
	 * @param pk_bill
	 * @param totalmny
	 * @param checkUser
	 */
	private void saveSpRemind(String pk_corp, String pk_corpk, String msg, Integer msgtypevalue, String msgtypename,
			UserVO uvo, String qj, String pk_bill, DZFDouble totalmny, boolean checkUser) {
		UserToCorp[] users = userService.queryCustMngUsers(pk_corpk);
		if(checkUser){
			if(users == null || users.length == 0){
				// 提示文案修改：未找到消息接收人，发送消息失败
				throw new BusinessException("请及时联系客户注册使用APP接收消息");
			}
		}
		if (users != null && users.length > 0) {
			JPMessageBean messageBean = new JPMessageBean();
			String[] userids = new String[users.length];
			MsgAdminVO[] mvos = new MsgAdminVO[userids.length];
			List<CorpMsgVO> cmlist = null;
			CorpMsgVO cmvo = null;
			if (msgtypevalue == MsgtypeEnum.MSG_TYPE_SPTX.getValue()
					|| msgtypevalue == MsgtypeEnum.MSG_TYPE_CSTX.getValue()
					|| msgtypevalue == MsgtypeEnum.MSG_TYPE_QKTX.getValue()
					|| msgtypevalue == MsgtypeEnum.MSG_TYPE_JJPZTX.getValue()
					|| msgtypevalue == MsgtypeEnum.MSG_TYPE_YJTX.getValue()
					|| msgtypevalue == MsgtypeEnum.MSG_TYPE_SJTX.getValue()) {
				cmlist = new ArrayList<CorpMsgVO>();
			}
			for (int i = 0; i < users.length; i++) {
				userids[i] = users[i].getPk_user();
				mvos[i] = convertVO(pk_corp, pk_corpk, msg, users[i].getPk_user(), msgtypevalue, msgtypename,
						uvo, qj, pk_bill, totalmny);
				if(msgtypevalue == MsgtypeEnum.MSG_TYPE_SPTX.getValue()
						|| msgtypevalue == MsgtypeEnum.MSG_TYPE_CSTX.getValue()
						|| msgtypevalue == MsgtypeEnum.MSG_TYPE_QKTX.getValue()
						|| msgtypevalue == MsgtypeEnum.MSG_TYPE_JJPZTX.getValue()
						|| msgtypevalue == MsgtypeEnum.MSG_TYPE_YJTX.getValue()
						|| msgtypevalue == MsgtypeEnum.MSG_TYPE_SJTX.getValue()){
					cmvo = convertCorpmsgVO(pk_corp, pk_corpk, msg, users[i].getPk_user(), msgtypevalue,
							msgtypename, uvo, qj);
					cmlist.add(cmvo);
				}
			}
			messageBean.setUserids(userids);
			messageBean.setMessage(msg.toString());
			messageBean.setSourcesys("dzf");
			sysMessagePush.sendSysMessage(messageBean);//大账房推送
			messageBean.setSourcesys("cst");
			sysMessagePush.sendSysMessage(messageBean);//移动账务推送
			singleObjectBO.insertVOArr(pk_corp, mvos);
			if(cmlist != null && cmlist.size() > 0){
				singleObjectBO.insertVOArr(pk_corp, cmlist.toArray(new CorpMsgVO[0]));
			}
		}
	}
	
	/**
	 * 构造消息内容
	 * @param pk_corp
	 * @param pk_corpk
	 * @param msg
	 * @param vreceiveid
	 * @param msgtype
	 * @param msgtname
	 * @param uvo
	 * @param qj
	 * @param pk_bill
	 * @return
	 */
	private MsgAdminVO convertVO(String pk_corp, String pk_corpk, String msg, String vreceiveid, Integer msgtype,
			String msgtname, UserVO uvo, String qj, String pk_bill, DZFDouble totalmny) {
		MsgAdminVO msgvo = new MsgAdminVO();
		msgvo.setPk_corp(pk_corp);
		msgvo.setPk_corpk(pk_corpk);
		msgvo.setCuserid(vreceiveid);
		msgvo.setMsgtype(msgtype);
		msgvo.setVcontent(msg);
		msgvo.setVsenddate(new DZFDateTime().toString());
		msgvo.setSys_send(ISysConstants.SYS_ADMIN);
		msgvo.setMsgtypename(msgtname);
		msgvo.setIsread(DZFBoolean.FALSE);
		msgvo.setSendman(uvo.getCuserid());
		msgvo.setVperiod(qj);
		msgvo.setPk_bill(pk_bill);
		// msgvo.setNodecode(INodeConstant.ADMIN_WDKH);
		msgvo.setDr(0);
		//应缴提醒、实缴提醒记录总金额
		if(msgtype.equals(MsgtypeEnum.MSG_TYPE_YJTX.getValue()) || msgtype .equals(MsgtypeEnum.MSG_TYPE_SJTX.getValue())){
			msgvo.setNtotalmny(totalmny);
		}
		return msgvo;
	}
	
	/**
	 * 构造提醒历史数据
	 * @param pk_corp
	 * @param pk_corpk
	 * @param msg
	 * @param vreceiveid
	 * @param msgtype
	 * @param msgtname
	 * @param uvo
	 * @param qj
	 * @return
	 * @throws DZFWarpException
	 */
	public CorpMsgVO convertCorpmsgVO(String pk_corp,String pk_corpk,String msg,String vreceiveid,Integer msgtype,
			String msgtname,UserVO uvo,String qj) throws DZFWarpException{
		CorpMsgVO msgvo = new CorpMsgVO();
		msgvo.setPk_corp(pk_corp);
		msgvo.setPk_corpk(pk_corpk);
		msgvo.setCuserid(vreceiveid);
		msgvo.setMsgtype(msgtype);
		msgvo.setVcontent(msg);
		msgvo.setVsenddate(new DZFDateTime().toString());
		msgvo.setVdate(new DZFDate().toString());
		msgvo.setMsgtypename(msgtname);
		msgvo.setIsread(DZFBoolean.FALSE);
		if(uvo != null){
			msgvo.setSendman(uvo.getCuserid());
		}
		msgvo.setVperiod(qj);
		msgvo.setDr(0);
		return msgvo;
	}

	@Override
	public CorpMsgVO[] queryMsgAdminVO(String pk_corp, String pk_corpk,String qj) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT g.*, u.user_name AS user_name  ") ;
		sql.append("  FROM ynt_corpmsg g  ") ; 
		sql.append("  LEFT JOIN sm_user u ON g.sendman = u.cuserid  ") ; 
		sql.append(" WHERE nvl(g.dr, 0) = 0  ") ; 
		sql.append("   and nvl(u.dr, 0) = 0  ") ; 
		sql.append("   and g.pk_corp = ?") ; 
		sql.append("   and g.pk_corpk = ?") ; 
		sql.append("   and g.vperiod = ?") ; 
		sql.append("   and g.msgtype in (38, 39, 40, 41, 51, 52)") ; 
		sql.append(" order by g.vsenddate desc  ");
		spm.addParam(pk_corp);
		spm.addParam(pk_corpk);
		spm.addParam(qj);
		List<CorpMsgVO> list = (List<CorpMsgVO>) singleObjectBO.executeQuery(sql.toString(), spm, new BeanListProcessor(CorpMsgVO.class));
		if(list != null && list.size() > 0){
			QueryDeCodeUtils.decKeyUtils(new String[]{"user_name"}, list, 1);
			return list.toArray(new CorpMsgVO[0]);
		}
		return null;
	}
	
	/**
	 * 构造操作信息
	 * @param vo
	 * @return
	 */
	private List<CorpMsgVO> queryCorpMsg(BsWorkbenchVO vo,UserVO uservo) throws DZFWarpException{
		Integer msgtype = null;
		String msgtname = null;
		List<CorpMsgVO> msglist = new ArrayList<CorpMsgVO>();
		CorpMsgVO msgvo = null;
		//1、新增
		if(StringUtil.isEmpty(vo.getPk_workbench())){
			if(vo.getIsptx() != null && vo.getIsptx() == 1){
				msgtype = 42;
				msgtname = "已送票";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			} 
			if(vo.getTaxStateCopy() != null && vo.getTaxStateCopy() == 1){
				msgtype = 43;
				msgtname = "已抄税";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			}
			if(vo.getTaxStateClean() != null && vo.getTaxStateClean() == 1){
				msgtype = 44;
				msgtname = "已清卡";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			}
			if(vo.getIpzjjzt() != null && vo.getIpzjjzt() == 1){
				msgtype = 45;
				msgtname = "凭证收到";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			}
		}else{
			//2、修改
			BsWorkbenchVO bsvo = (BsWorkbenchVO) singleObjectBO.queryByPrimaryKey(BsWorkbenchVO.class, vo.getPk_workbench());
			if(vo.getIsptx() != null && vo.getIsptx() == 1 && !vo.getIsptx().equals(bsvo.getIsptx())){
				msgtype = 42;
				msgtname = "已送票";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			} 
			if(vo.getTaxStateCopy() != null && vo.getTaxStateCopy() == 1 && !vo.getTaxStateCopy().equals(bsvo.getTaxStateCopy())){
				msgtype = 43;
				msgtname = "已抄税";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			}
			if(vo.getTaxStateClean() != null && vo.getTaxStateClean() == 1 && !vo.getTaxStateClean().equals(bsvo.getTaxStateClean())){
				msgtype = 44;
				msgtname = "已清卡";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			}
			if(vo.getIpzjjzt() != null && vo.getIpzjjzt() == 1 && !vo.getIpzjjzt().equals(bsvo.getIpzjjzt())){
				msgtype = 45;
				msgtname = "凭证收到";
				msgvo = convertCorpmsgVO(vo.getFathercorp(), vo.getPk_corp(), null, null, msgtype, msgtname, uservo, vo.getPeriod());
				msglist.add(msgvo);
			}
		}
		return msglist;
	}
	
	@Override
	public List<BsWorkbenchVO> getTaxDeclare(String pk_corp, UserVO uservo, String period, String[] corpks)
			throws DZFWarpException {
		QueryParamVO paramvo = new QueryParamVO();
		paramvo.setQjq(period);
		paramvo.setFathercorp(pk_corp);
		CorpVO fcorpvo = corpService.queryByPk(pk_corp);
		List<BsWorkbenchVO> list = query(paramvo, uservo, corpks, fcorpvo);
		if(list != null && list.size() > 0){
			try {
				Map<String, List<TaxRptCalCellBVO>> pamap = getPamMap();//查询参数集合
				Map<String, String> schemap = querySchemaMap();//科目方案集合
				List<BsWorkbenchVO> uplist = new ArrayList<BsWorkbenchVO>();
				Integer provice = null;
				for(BsWorkbenchVO bsvo : list){
					provice = bsvo.getVprovince();//报税地区
					if(provice != null){
						if(bsvo.getBegindate() != null 
								&& bsvo.getBegindate().compareTo(new DZFDate(period+"-01")) <= 0){//已建账
							//获取纳税申报实缴税额和申报状态
							getRealpayData(bsvo, pamap, schemap);
						}
					}
					countTotalMny(bsvo);
					uplist.add(bsvo);
				}
				return uplist;
			} catch (Exception e) {
				throw new BusinessException(e.getMessage());
			}
		}
		return list;
	}

	@Override
	public void updateTaxDeclare(List<BsWorkbenchVO> uplist) throws DZFWarpException {
		String[] upstr = new String[] { "addpaidTax", "addStatus", "incomepaidTax", "incomeStatus", "culturalpaidTax",
				"culturalStatus", "additionalpaidTax", "additionalStatus", "citypaidTax", "cityStatus", "educapaidTax",
				"educaStatus", "localEducapaidTax", "localEducaStatus", "personpaidTax", "personStatus", "erningStatus",
				"stampTax", "stamppaidTax", "stampStatus", "npaymny", "npaidmny" };
		singleObjectBO.updateAry(uplist.toArray(new BsWorkbenchVO[0]), upstr);
	}
	/**
	 * 计算应缴合计和实缴合计
	 * @param bvo
	 * @throws DZFWarpException
	 */
	private void countTotalMny(BsWorkbenchVO bvo) throws DZFWarpException {
		DZFDouble npaymny = DZFDouble.ZERO_DBL;//应缴金额
		DZFDouble npaidmny = DZFDouble.ZERO_DBL;//实缴金额
		
		//增值税
		npaymny = SafeCompute.add(npaymny, bvo.getAddTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getAddpaidTax());
		//消费税
		npaymny = SafeCompute.add(npaymny, bvo.getExciseTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getExcisepaidTax());
		//企业所得税
		npaymny = SafeCompute.add(npaymny, bvo.getIncomeTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getIncomepaidTax());
		//文化事业建设费
		npaymny = SafeCompute.add(npaymny, bvo.getCulturalTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getCulturalpaidTax());
		//附加税合计（包含城建税、教育费附加、地方教育费附加）
		npaymny = SafeCompute.add(npaymny, bvo.getAdditionalTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getAdditionalpaidTax());
		//个人所得税
		npaymny = SafeCompute.add(npaymny, bvo.getPersonTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getPersonpaidTax());
		//印花税
		npaymny = SafeCompute.add(npaymny, bvo.getStampTax());
		npaidmny = SafeCompute.add(npaidmny, bvo.getStamppaidTax());
		bvo.setNpaymny(npaymny);
		bvo.setNpaidmny(npaidmny);
	}
	
	/**
	 * 获取实缴税额和申报状态
	 * @param bsvo  
	 * @param pamap  查询参数集合
	 * @param schemap 科目方案集合
	 * @throws DZFWarpException
	 */
	private void getRealpayData(BsWorkbenchVO bsvo, Map<String, List<TaxRptCalCellBVO>> pamap, Map<String, String> schemap
			) throws DZFWarpException {
		//纳税申报税种：1：增值税；2：消费税；3：企业所得税；4：文化事业建设费；5：附加税合计；6：城建税；7：教育费附加；8：地方教育费附加；9：个人所得税；10：财报；
		List<TaxRptCalCellBVO> list = getTaxProp(bsvo, pamap, schemap);
		TaxRptCalCellVO retvo = null;
		TaxRptCalCellBVO[] detVOs = null;
		if(list != null && list.size() > 0){
			for(TaxRptCalCellBVO tvo : list){
				tvo.setPk_corp(bsvo.getPk_corp());//客户主键
				tvo.setPeriod(bsvo.getPeriod());//期间
				tvo.setMny(null);
				tvo.setTxstatus(null);
			}
			retvo = taxrptService.getTaxRptCalCell(list.toArray(new TaxRptCalCellBVO[0]));
			if(retvo != null && retvo.getSuccess() != null && retvo.getSuccess().booleanValue()){
				detVOs = (TaxRptCalCellBVO[]) retvo.getChildren();
				if(detVOs != null && detVOs.length > 0){
					setRetValue(bsvo, detVOs);
				}
			}
		}
	}
	
	/**
	 * 赋值更新
	 * @param bsvo
	 * @param detVOs
	 * @throws DZFWarpException
	 */
	private void setRetValue(BsWorkbenchVO bsvo, TaxRptCalCellBVO[] detVOs) throws DZFWarpException {
		Integer sbzt = null;
		for(TaxRptCalCellBVO dvo : detVOs){
			sbzt = getSbStatues(dvo);
			switch(dvo.getIuptaxcode()){
			case 1://1：增值税；
				if(dvo.getMny() != null){
					bsvo.setAddpaidTax(SafeCompute.add(bsvo.getAddpaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("addStatus", sbzt);
				}
				break;
			case 2://2：消费税；
				break;
			case 3://3：企业所得税；
				if(dvo.getMny() != null){
					bsvo.setIncomepaidTax(SafeCompute.add(bsvo.getIncomepaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("incomeStatus", sbzt);
				}
				break;
			case 4://4：文化事业建设费；
				if(dvo.getMny() != null){
					bsvo.setCulturalpaidTax(SafeCompute.add(bsvo.getCulturalpaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("culturalStatus", sbzt);
				}
				break;
			case 5://5：附加税合计；
				if(dvo.getMny() != null){
					bsvo.setAdditionalpaidTax(SafeCompute.add(bsvo.getAdditionalpaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("additionalStatus", sbzt);
				}
				break;
			case 6://6：城建税；
				if(dvo.getMny() != null){
					bsvo.setCitypaidTax(SafeCompute.add(bsvo.getCitypaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("cityStatus", sbzt);
				}
				break;
			case 7://7：教育费附加；
				if(dvo.getMny() != null){
					bsvo.setEducapaidTax(SafeCompute.add(bsvo.getEducapaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("educaStatus", sbzt);
				}
				break;
			case 8://8：地方教育费附加；
				if(dvo.getMny() != null){
					bsvo.setLocalEducapaidTax(SafeCompute.add(bsvo.getLocalEducapaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("localEducaStatus", sbzt);
				}
				break;
			case 9://9：个人所得税；
				if(dvo.getMny() != null){
					bsvo.setPersonpaidTax(SafeCompute.add(bsvo.getPersonpaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("personStatus", sbzt);
				}
				break;
			case 10://10：财报；
				if(sbzt != null){
					bsvo.setAttributeValue("erningStatus", sbzt);
				}
				break;
			case 11://11：印花税；
				if(dvo.getMny() != null){
					bsvo.setStamppaidTax(SafeCompute.add(bsvo.getStamppaidTax(), dvo.getMny()));
				}
				if(sbzt != null){
					bsvo.setAttributeValue("stampStatus", sbzt);
				}
				break;
			}
		}
	}
	
	/**
	 * 获取申报状态
	 * @param bvo
	 * @return
	 * @throws DZFWarpException
	 */
	private Integer getSbStatues(TaxRptCalCellBVO bvo) throws DZFWarpException {
		//状态常量：0：已提交；1：受理失败；2：受理成功；3：申报失败；4：申报成功；5：作废；6：缴款失败；7：缴款成功；101：未提交；98：未填写；99：已填写；
		if("4".equals(bvo.getSbzt_dm()) || "7".equals(bvo.getSbzt_dm())){
			return 1;
		}else{
			return 0;
		}
	}
	
	/**
	 * 获取纳税申报税种的相关信息
	 * @param pk_corp
	 * @param pamap
	 * @param schemap
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TaxRptCalCellBVO> getTaxProp(BsWorkbenchVO bsvo, Map<String, List<TaxRptCalCellBVO>> pamap,
                                              Map<String, String> schemap) throws DZFWarpException {
		List<TaxRptCalCellBVO> retlist = new ArrayList<TaxRptCalCellBVO>();
		// 纳税申报税种：1：增值税；2：消费税；3：企业所得税；4：文化事业建设费；5：附加税合计；6：城建税；7：教育费附加；8：地方教育费附加；9：个人所得税；10：财报；
		Integer[] taxcodes = new Integer[]{1,2,3,4,5,6,7,8,9,10,11};
		List<TaxRptCalCellBVO> list = null;
		Integer provice = bsvo.getVprovince();
		if(provice == null){
			return null;
		}
		String chname = "";
		String corptype = "";
		CorpVO corpvo = corpService.queryByPk(bsvo.getPk_corp());
		if(corpvo != null){
			chname = corpvo.getChargedeptname();
			corptype = corpvo.getCorptype();
		}
		String key = "";
		TaxRptCalCellBVO rptvo = null;
		for(Integer tax : taxcodes){
			key = "";
			list = new ArrayList<TaxRptCalCellBVO>();
			//9：个人所得税；10：财报；不调用接口查询实缴信息
			if(tax == 2 || tax == 9 || tax == 10){
				if(tax == 2){
					continue;
				}
				rptvo = new TaxRptCalCellBVO();
//				rptvo.setPk_corp(bsvo.getPk_corp());
				switch(tax){
					case 9://9：个人所得税；
						rptvo.setSbzlbh("G");//税种编码
						rptvo.setReportname("");//税表名称
						rptvo.setX("");//税表从坐标
						rptvo.setY("");//税表横坐标
						rptvo.setPeriodtype("0");//申报周期     0：月报；1：季报；2：年报；
						rptvo.setIuptaxcode(9);//更新税种编码
						retlist.add(rptvo);
						break;
					case 10://10：财报；
						if(schemap != null && !schemap.isEmpty()){
							//小企业会计准则-小企业财报，企业会计准则-一般企业财报
							if(schemap.get("小企业会计准则").equals(corpvo.getCorptype())){
								rptvo.setSbzlbh("C1");//税种编码
							}else if(schemap.get("企业会计准则").equals(corpvo.getCorptype())){
								rptvo.setSbzlbh("C2");//税种编码
							}
						}
						rptvo.setReportname("");//税表名称
						rptvo.setX("");//税表从坐标
						rptvo.setY("");//税表横坐标
						rptvo.setPeriodtype("");//申报周期     0：月报；1：季报；2：年报；
						rptvo.setIuptaxcode(10);//更新税种编码
						retlist.add(rptvo);
						break;
				}
			}else if(tax == 3 || tax == 11){//不区分纳税人资格
				//1：中国；2：北京市；11：江苏省；16：山东省；23：重庆市；
				if(provice == 2 || provice == 11 || provice == 16 || provice == 23){
					key = provice + ";" + tax + ";";
					list = pamap.get(key);
				}else{
					key = 0 + ";" + tax + ";";
					list = pamap.get(key);
				}
				retlist.addAll(list);
			}else{//区分纳税人资格
				//1：中国；2：北京市；11：江苏省；16：山东省；23：重庆市；
				if(provice == 2 || provice == 11 || provice == 16 || provice == 23){
					key = provice + ";" + tax + ";" +chname;
					list = pamap.get(key);
				}else{
					key = 0 + ";" + tax + ";" +chname;
					list = pamap.get(key);
				}
				retlist.addAll(list);
			}
		}
		return retlist;
	}
	
	/**
	 * 获取实缴金额查询参数
	 * @return
	 * @throws DZFWarpException
	 */
	@SuppressWarnings("unchecked")
	private Map<String, List<TaxRptCalCellBVO>> getPamMap() throws DZFWarpException {
		Map<String, List<TaxRptCalCellBVO>> pamap = new HashMap<String, List<TaxRptCalCellBVO>>();
		String sql = " SELECT * FROM ynt_taxqrypam WHERE nvl(dr,0) = 0 ";
		List<TaxQryPamVO> list = (List<TaxQryPamVO>) singleObjectBO.executeQuery(sql, null,
				new BeanListProcessor(TaxQryPamVO.class));
		if(list != null && list.size() > 0){
			String key = "";
			List<TaxRptCalCellBVO> newlist = null;
			List<TaxRptCalCellBVO> oldlist = null;
			TaxRptCalCellBVO taxvo = null;
			String chname = "";
			for(TaxQryPamVO pamvo : list){
				if(!StringUtil.isEmptyWithTrim(pamvo.getChargedeptname())){
					chname = pamvo.getChargedeptname();
				}else{
					chname = "";
				}
				key = pamvo.getVprovince() + ";" + pamvo.getIcode() + ";" + chname;
				if(!pamap.containsKey(key)){
					newlist = new ArrayList<TaxRptCalCellBVO>();
					taxvo = new TaxRptCalCellBVO();
					taxvo.setSbzlbh(pamvo.getSbzlbh());//申报种类编号
					taxvo.setReportname(pamvo.getReportname());//报表名称
					taxvo.setX(pamvo.getX());//坐标x
					taxvo.setY(pamvo.getY());//坐标y
					taxvo.setIuptaxcode(pamvo.getIcode());//更新税种编码
					newlist.add(taxvo);
					pamap.put(key, newlist);
				}else{
					oldlist = pamap.get(key);
					taxvo = new TaxRptCalCellBVO();
					taxvo.setSbzlbh(pamvo.getSbzlbh());//申报种类编号
					taxvo.setReportname(pamvo.getReportname());//报表名称
					taxvo.setX(pamvo.getX());//坐标x
					taxvo.setY(pamvo.getY());//坐标y
					taxvo.setIuptaxcode(pamvo.getIcode());//更新税种编码
					oldlist.add(taxvo);
					pamap.put(key, oldlist);
				}
			}
		}
		return pamap;
	}
	
	/**
	 * 获取科目方案信息
	 * @return
	 */
	private Map<String, String> querySchemaMap() {
		String sql = " nvl(dr,0) = 0 ";
		BdtradeAccountSchemaVO[] schemaVOs = (BdtradeAccountSchemaVO[]) singleObjectBO
				.queryByCondition(BdtradeAccountSchemaVO.class, sql, null);
		Map<String, String> map = new HashMap<String, String>();
		if(schemaVOs != null && schemaVOs.length > 0){
			for(BdtradeAccountSchemaVO vo : schemaVOs){
				map.put(vo.getAccname(), vo.getPk_trade_accountschema());
			}
		}
		return map;
	}

	@Override
	public void saveRemindSet(BsWorkbenchVO bsvo, RemindSetVO[] remVOs, Map<Integer, RemindSetVO> remap,
							  String pk_corp, String cuserid) throws DZFWarpException {
		RemindSetVO[] setVOs = qryRemSet(bsvo, pk_corp);
		if(setVOs != null && setVOs.length > 0){
			RemindSetVO remvo = null;
			for(RemindSetVO setvo : setVOs){
				remvo = remap.get(setvo.getIremindtype());
				if(remvo != null){
					setvo.setIbeginday(remvo.getIbeginday());
					setvo.setIendday(remvo.getIendday());
					setvo.setUpdatets(new DZFDateTime());
				}
			}
			singleObjectBO.updateAry(setVOs, new String[]{"ibeginday","iendday"});
		}else{
			for(RemindSetVO remvo : remVOs){
				remvo.setPk_corp(pk_corp);
				remvo.setPk_corpk(bsvo.getPk_corp());
				remvo.setCoperatorid(cuserid);
				remvo.setDoperatedate(new DZFDate());
				remvo.setUpdatets(new DZFDateTime());
			}
			singleObjectBO.insertVOArr(pk_corp, remVOs);
		}
	}
	
	/**
	 * 查询客户提醒设置信息
	 * @param bsvo
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public RemindSetVO[] qryRemSet(BsWorkbenchVO paramvo,String pk_corp) throws DZFWarpException {
		String sql = " nvl(dr,0) = 0 AND pk_corp = ? AND pk_corpk = ? ";
		SQLParameter spm = new SQLParameter();
		spm.addParam(pk_corp);//会计公司主键
		spm.addParam(paramvo.getPk_corp());//客户主键
		return (RemindSetVO[]) singleObjectBO.queryByCondition(RemindSetVO.class, sql, spm);
	}
	
	public DZFDouble getDZFDouble(Object value) {
		if (value == null || value.toString().trim().equals("")) {
			return DZFDouble.ZERO_DBL;
		} else if (value instanceof DZFDouble) {
			return (DZFDouble) value;
		} else if (value instanceof BigDecimal) {
			return new DZFDouble((BigDecimal) value);
		} else {
			return new DZFDouble(value.toString().trim());
		}
	}
	
	@Override
	public void uploadFile(String fathercorp, String pk_corpperiod, String[] filenames, File[] files, UserVO uservo,
			String pk_corp, String period) throws DZFWarpException {
		List<BsWorkDocVO> list = new ArrayList<BsWorkDocVO>();
		BsWorkDocVO docvo = null;
		int i = 0;
		String vfilepath = "";
		for (File file : files) {
			i = 0;
			docvo = new BsWorkDocVO();
			docvo.setPk_corpperiod(pk_corpperiod);
			docvo.setFathercorp(fathercorp);
			docvo.setPk_corp(pk_corp);
			docvo.setPeriod(period);
			docvo.setDocname(filenames[i]);
			vfilepath = upload(file, filenames[i]);
			docvo.setVfilepath(vfilepath);
			docvo.setCoperatorid(uservo.getCuserid());
			docvo.setDoperatetime(new DZFDateTime());
			docvo.setDr(0);
			list.add(docvo);
			i++;
		}
		if (list != null && list.size() > 0) {
			singleObjectBO.insertVOArr(fathercorp, list.toArray(new BsWorkDocVO[0]));
		} else {
			throw new BusinessException("附件上传失败");
		}
	}
	
	/**
	 * 上传附件到文件服务器
	 * @param file
	 * @param filename
	 * @return
	 * @throws DZFWarpException
	 */
	private String upload(File file, String filename) throws DZFWarpException {
		String filepath = "";
		try {
			filepath = ((FastDfsUtil) SpringUtils.getBean("connectionPool")).upload(file, filename, null);
		} catch (AppException e) {
			throw new BusinessException("附件上传错误");
		}
		if (StringUtil.isEmpty(filepath)) {
			throw new BusinessException("附件上传错误");
		}
		return filepath.substring(1);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<BsWorkDocVO> getAttatches(BsWorkDocVO qvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  ");
		sql.append("  FROM ynt_bsworkdoc  ");
		sql.append(" WHERE nvl(dr, 0) = 0  ");
		sql.append("   AND fathercorp = ?  ");
		spm.addParam(qvo.getFathercorp());
		sql.append("   AND pk_corp = ?  ");
		spm.addParam(qvo.getPk_corp());
		sql.append("   AND pk_corpperiod = ? ");
		spm.addParam(qvo.getPk_corpperiod());
		if(!StringUtil.isEmpty(qvo.getPk_bsworkdoc())){
			sql.append(" AND pk_bsworkdoc = ? ");
			spm.addParam(qvo.getPk_bsworkdoc());
		}
		return (List<BsWorkDocVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BsWorkDocVO.class));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void delAttaches(String fathercorp, String[] ids) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  ");
		sql.append("  FROM ynt_bsworkdoc  ");
		sql.append(" WHERE nvl(dr, 0) = 0  ");
		sql.append("   AND fathercorp = ?  ");
		spm.addParam(fathercorp);
		String where = SqlUtil.buildSqlForIn("pk_bsworkdoc", ids);
		sql.append(" AND ").append(where);
		List<BsWorkDocVO> list = (List<BsWorkDocVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(BsWorkDocVO.class));
		if(list != null && list.size() > 0){
			List<String> pklist = new ArrayList<String>();
			for(BsWorkDocVO docvo : list){
				try {
    				((FastDfsUtil) SpringUtils.getBean("connectionPool")).deleteFile(docvo.getVfilepath());
    				pklist.add(docvo.getPk_bsworkdoc());
				} catch (Exception e) {
					log.error("文件删除失败", e);
				}  
			}
			if(pklist != null && pklist.size() > 0){
				sql = new StringBuffer();
				spm = new SQLParameter();
				sql.append(" DELETE FROM ynt_bsworkdoc  ");
				sql.append(" WHERE fathercorp = ? ");
				spm.addParam(fathercorp);
				where = SqlUtil.buildSqlForIn("pk_bsworkdoc", pklist.toArray(new String[0]));
				sql.append(" AND ").append(where);
				singleObjectBO.executeUpdate(sql.toString(), spm);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public UserVO[] queryUser(String fathercorp, String rolecode) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		if(StringUtil.isEmpty(rolecode)){
			sql.append("SELECT *  ") ;
			sql.append("  FROM sm_user  ") ; 
			sql.append(" WHERE pk_corp = ?  ") ; 
			sql.append("   AND nvl(dr, 0) = 0  ") ; 
			sql.append("   AND nvl(locked_tag, 'N') = 'N' ");
			spm.addParam(fathercorp);
		}else if(!StringUtil.isEmpty(rolecode)){
			sql.append("SELECT DISTINCT u.*  ") ;
			sql.append("  FROM sm_user u  ") ; 
			sql.append("  JOIN sm_user_role ur ON u.cuserid = ur.cuserid  ") ; 
			sql.append("  JOIN sm_role r ON ur.pk_role = r.pk_role  ") ; 
			sql.append(" WHERE nvl(u.dr, 0) = 0  ") ; 
			sql.append("   AND nvl(ur.dr, 0) = 0  ") ; 
			sql.append("   AND nvl(r.dr, 0) = 0  ") ; 
			sql.append("   AND u.pk_corp = ?  ") ; 
			sql.append("   AND r.role_code = ? ");
			spm.addParam(fathercorp);
			spm.addParam(rolecode);
		}
		List<UserVO> list = (List<UserVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(UserVO.class));
		if(list != null && list.size() > 0){
			return list.toArray(new UserVO[0]);
		}
		return new UserVO[0];
	}

	@Override
	public BsWorkbenchVO getFinanceProgress(String pk_corp, String period) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		BsWorkbenchVO[] rs = (BsWorkbenchVO[]) singleObjectBO.queryByCondition(BsWorkbenchVO.class,
						" pk_corp = ? and period = ? and nvl(dr,0)=0 ", sp);
		BsWorkbenchVO vo = null;
		if (rs != null && rs.length > 0) {
			vo = rs[0];
		} else {
			vo = new BsWorkbenchVO();
			vo.setPk_corp(pk_corp);
			vo.setPk_corp(period);
			// 送票
			vo.setIsptx(0);
			// 抄税
			vo.setTaxStateCopy(0);
			// 申报完成
			vo.setTaxStateFinish(0);
			// 清卡
			vo.setTaxStateClean(0);
		}
		String gzSql = " select isgz from ynt_qmcl where pk_corp = ? and period = ? and nvl(dr,0)=0 ";
		String isgz = (String) singleObjectBO.executeQuery(gzSql, sp, new ColumnProcessor());
		// 记账
		vo.setIacctcheck("Y".equals(isgz) ? 1 : 0);
		return vo;
	}

	@Override
	public void updateFinanceProgress(String pk_corp, String period,
											   String field, Integer status) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		String querySql = "select pk_workbench from nsworkbench where pk_corp = ? and period = ? and nvl(dr,0)=0 ";
		String id = (String) singleObjectBO.executeQuery(querySql, sp, new ColumnProcessor());
		BsWorkbenchVO vo = new BsWorkbenchVO();
		vo.setPk_corp(pk_corp);
		vo.setPeriod(period);
		String updateField = null;
		switch (field) {
			case "spzt":
				updateField = "isptx";
				vo.setIsptx(status);
				break;
			case "cszt":
				updateField = "taxstatecopy";
				vo.setTaxStateCopy(status);
				break;
			case "wczt":
				updateField = "taxstatefinish";
				vo.setTaxStateFinish(status);
				break;
			case "qkzt":
				updateField = "taxstateclean";
				vo.setTaxStateClean(status);
				break;
			default:
				break;
		}
		if (updateField == null) {
			return;
		}
		if (id == null) {
			singleObjectBO.saveObject(pk_corp, vo);
		} else {
			SQLParameter updateParam = new SQLParameter();
			updateParam.addParam(status);
			updateParam.addParam(id);
			String updateSql = "update nsworkbench set "+ updateField + " = ? where pk_workbench = ? ";
			singleObjectBO.executeUpdate(updateSql, updateParam);
		}
	}

	@Override
	public void saveCol(ColumnSetupVO pamvo) throws DZFWarpException {
		ColumnSetupVO oldvo = queryCol(pamvo);
		if(oldvo != null){
			pamvo.setPk_col_setup(oldvo.getPk_col_setup());
			singleObjectBO.update(pamvo, new String[]{"vcolsetup"});
		}else{
			singleObjectBO.saveObject(pamvo.getPk_corp(), pamvo);
		}
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public ColumnSetupVO queryCol(ColumnSetupVO pamvo) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter spm = new SQLParameter();
		sql.append("SELECT *  ");
		sql.append("  FROM YNT_COL_SETUP  ");
		sql.append(" WHERE nvl(dr, 0) = 0  ");
		sql.append("   AND pk_corp = ?  ");
		spm.addParam(pamvo.getPk_corp());
		sql.append("   AND vnodecode = ?  ");
		spm.addParam(pamvo.getVnodecode());
		sql.append("   AND coperatorid = ?  ");
		spm.addParam(pamvo.getCoperatorid());
		sql.append("   AND isettype = ? ");
		spm.addParam(pamvo.getIsettype());
		List<ColumnSetupVO> list = (List<ColumnSetupVO>) singleObjectBO.executeQuery(sql.toString(), spm,
				new BeanListProcessor(ColumnSetupVO.class));
		if(list != null && list.size() > 0){
			return list.get(0);
		}
		return null;
	}

	@Override
	public void uploadFile(String fathercorp, String pk_corpperiod, String[] filenames, List<byte[]>  files, UserVO uservo,
						   String pk_corp, String period) throws DZFWarpException {
		List<BsWorkDocVO> list = new ArrayList<BsWorkDocVO>();
		BsWorkDocVO docvo = null;
		int i = 0;
		String vfilepath = "";
		for (byte[] bytes : files) {
			i = 0;
			docvo = new BsWorkDocVO();
			docvo.setPk_corpperiod(pk_corpperiod);
			docvo.setFathercorp(fathercorp);
			docvo.setPk_corp(pk_corp);
			docvo.setPeriod(period);
			docvo.setDocname(filenames[i]);
			vfilepath = upload(bytes, filenames[i]);
			docvo.setVfilepath(vfilepath);
			docvo.setCoperatorid(uservo.getCuserid());
			docvo.setDoperatetime(new DZFDateTime());
			docvo.setDr(0);
			list.add(docvo);
			i++;
		}
		if (list != null && list.size() > 0) {
			singleObjectBO.insertVOArr(fathercorp, list.toArray(new BsWorkDocVO[0]));
		} else {
			throw new BusinessException("附件上传失败");
		}
	}

	/**
	 * 上传附件到文件服务器
	 * @param filename
	 * @return
	 * @throws DZFWarpException
	 */
	private String upload(byte[] bytes, String filename) throws DZFWarpException {
		String filepath = "";
		try {
			filepath = FastDfsUtil.getInstance().upload(bytes, filename, null);
		} catch (AppException e) {
			throw new BusinessException("附件上传错误");
		}
		if (StringUtil.isEmpty(filepath)) {
			throw new BusinessException("附件上传错误");
		}
		return filepath.substring(1);
	}

}

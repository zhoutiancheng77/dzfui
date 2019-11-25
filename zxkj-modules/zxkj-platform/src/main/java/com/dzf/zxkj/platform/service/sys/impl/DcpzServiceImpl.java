package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.image.DcModelBVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.image.DcModelImpVO;
import com.dzf.zxkj.platform.model.image.KMCommonVO;
import com.dzf.zxkj.platform.model.sys.BDTradeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.sys.IJtsjTemService;
import com.dzf.zxkj.platform.util.KmbmUpgrade;
import com.dzf.zxkj.platform.util.PinyinUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service("dcpzjmbserv")
@Slf4j
@SuppressWarnings("all")
public class DcpzServiceImpl implements IDcpzService {

	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private IJtsjTemService gl_jtsjtemserv;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;
	
	//导入时合并的分组字段
	private String[]  impgroupkey = new String[]{"busitypetempname",
			"accountschemaname","chargedeptname","vspstylename","szstylename","tradename","keywords"};
	
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	
	//在公司内唯一的判断标准。
	public String[] getGroupkey(String pk_corp){
		String[] groupkey = null;
		//这里行业先不管了。后续在说
		if(IDefaultValue.DefaultGroup.equals(pk_corp)){
			groupkey =  new String[]{"vspstylename","szstylename","busitypetempname","accountschemaname","chargedeptname","keywords"};
		}else{
			groupkey =  new String[]{"vspstylename","szstylename","busitypetempname","keywords"};
		}
		return groupkey;
	}

	@Override
	public DcModelHVO save(DcModelHVO vo) throws DZFWarpException {
		String pk_model_h = vo.getPk_model_h();
		if(!StringUtil.isEmpty(pk_model_h)){
			//删除子表delete
			String sql =" delete from  ynt_dcmodel_b  where pk_model_h = ? and pk_corp = ?  ";
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_model_h);
			sp.addParam(vo.getPk_corp());
			singleObjectBO.executeUpdate(sql, sp);
		}
		
		DcModelBVO[] newbvos = vo.getChildren();
		for(DcModelBVO newbvo : newbvos){
			newbvo.setPk_model_h(null);
			newbvo.setPk_model_b(null);
		}
		//在公司操作，企业性质及 科目方案不用存。
		if(!StringUtil.isEmpty(vo.getPk_corp())
				&& !IDefaultValue.DefaultGroup.equals(vo.getPk_corp())){
			vo.setAccountschemaname(null);
			vo.setPk_trade_accountschema(null);
			vo.setChargedeptname(null);
		}
		//
		DcModelHVO savevo = (DcModelHVO)singleObjectBO.saveObject(vo.getPk_corp(), vo);
		return savevo;
	}
	
	
	public String check(String pk_corp,DcModelHVO headvo){
		String error = "";
		if(headvo == null){
			error = "保存数据为空！";
			return error;
		}
		
//		CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
//		if(corpvo != null 
//				&& StringUtil.isEmpty(corpvo.getChargedeptname())
//				&& !IDefaultValue.DefaultGroup.equals(pk_corp)){
//			error = "公司性质没有维护，不允许保存！";
//			return error;
//		}
		
		DcModelBVO[] bodyvos = headvo.getChildren();
		if(bodyvos == null || bodyvos.length<2){
			error = "表体数据至少为两行以上！";
			return error;
		}
		error = checkOnly(pk_corp,headvo);
		return error;
	}
	
	public String checkOnly(String pk_corp,DcModelHVO headvo){
		String condition =  null;
		String pk_trade_accountschema = headvo.getPk_trade_accountschema();
		String vspstylecode = headvo.getVspstylecode();
		String szstylecode = headvo.getSzstylecode();
		String chargedeptname = headvo.getChargedeptname();
		String busitypename = headvo.getBusitypetempname();
		String keywords = headvo.getKeywords();
		//
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		if(IDefaultValue.DefaultGroup.equals(pk_corp)){
			params.addParam(pk_trade_accountschema);
		}
		params.addParam(vspstylecode);
		params.addParam(szstylecode);
		params.addParam(busitypename);
		params.addParam(keywords);
		if(IDefaultValue.DefaultGroup.equals(pk_corp) && !StringUtil.isEmpty(chargedeptname)){//集团
			params.addParam(chargedeptname);
		}
		//
		if(IDefaultValue.DefaultGroup.equals(pk_corp)){
			StringBuffer sf = new StringBuffer();
			sf.append(" nvl(dr,0) = 0 and pk_corp = ? and pk_trade_accountschema=?  ");
			sf.append(" and vspstylecode=? and szstylecode=? and busitypetempname = ? ");
			sf.append(" and keywords = ?  ");
			if(!StringUtil.isEmpty(chargedeptname)){
				sf.append("  and chargedeptname=? ");
			}
			condition = sf.toString();
		}else{
			condition = " nvl(dr,0) = 0 and pk_corp = ? and vspstylecode=? and szstylecode=? and busitypetempname = ? and keywords = ?";
		}
		if(!StringUtil.isEmpty(headvo.getPk_model_h())){
			condition = condition + " and pk_model_h <> ?";
			params.addParam(headvo.getPk_model_h());;
		}
		DcModelHVO[] hvos = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class, condition, params);
		if(hvos != null && hvos.length > 0){
			return "业务类型、科目方案、发票类型、结算方式、关键字必须唯一";
		}
		return "";
	}

//	@Override
//	public void update(DcModelHVO vo) throws DZFWarpException {
//		DcModelHVO tvo = new DcModelHVO();
//		BeanUtils.copyProperties(vo, tvo);
//		singleObjectBO.saveObject(tvo.getPk_corp(), tvo);
//	}

	/**
	 * 查询主表，即节点查询，返回数据
	 */
	@Override
	public List<DcModelHVO> queryself(String pk_corp, String quickcreate) throws DZFWarpException {
		List<DcModelHVO> list = null;
		if("Y".equals(quickcreate)){//是否是快速制单请求过来的
			list = queryModelHVOs(pk_corp,quickcreate,null);
		}else{//非快速制单，比如节点查询
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			
			
			DcModelHVO[] ancevos = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, " nvl(dr,0) = 0 and pk_corp = ? order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode", sp);
			if(ancevos != null && ancevos.length > 0){
				list = new ArrayList<DcModelHVO>(Arrays.asList(ancevos));
			}
		}
		return list;
	}
	
	public List<DcModelHVO> queryself(String pk_corp,String quickcreate,DcModelHVO hvo) throws DZFWarpException{
		List<DcModelHVO> list = null;
		if(hvo == null){
			list =queryself(pk_corp, quickcreate);
		}else{
			if("Y".equals(quickcreate)){//是否是快速制单请求过来的
				list = queryModelHVOs(pk_corp,quickcreate,null);
			}else{//非快速制单，比如节点查询
				SQLParameter sp = new SQLParameter();
				sp.addParam(pk_corp);
				
				StringBuffer sf = new StringBuffer();
				sf.append("nvl(dr,0) = 0 and pk_corp = ? ");
				addCondition(sf, hvo, sp);
				sf.append(" order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode");
				DcModelHVO[] ancevos = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
				if(ancevos != null && ancevos.length > 0){
					list = new ArrayList<DcModelHVO>(Arrays.asList(ancevos));
				}
			}
		}
		return list;
	}

	private void addCondition(StringBuffer buffer, DcModelHVO paramvo, SQLParameter sp) {

		if (StringUtils.isNotEmpty(paramvo.getBusitypetempname())) {
			buffer.append(" and busitypetempname like ? ");
			sp.addParam("%" + paramvo.getBusitypetempname() + "%");
		}

		if (StringUtils.isNotEmpty(paramvo.getPk_trade_accountschema())) {
			buffer.append(" and pk_trade_accountschema = ? ");
			sp.addParam(paramvo.getPk_trade_accountschema());
		}

		if (StringUtils.isNotEmpty(paramvo.getChargedeptname())) {
			buffer.append(" and chargedeptname = ? ");
			sp.addParam(paramvo.getChargedeptname());
		}

		if (StringUtils.isNotEmpty(paramvo.getVspstylecode())) {
			buffer.append(" and vspstylecode = ? ");
			sp.addParam(paramvo.getVspstylecode());
		}

		if (StringUtils.isNotEmpty(paramvo.getSzstylecode())) {
			buffer.append(" and szstylecode = ? ");
			sp.addParam(paramvo.getSzstylecode());
		}

		if (StringUtils.isNotEmpty(paramvo.getKeywords())) {
			buffer.append(" and keywords like ? ");
			sp.addParam("%" + paramvo.getKeywords() + "%");
		}
		
		if (paramvo.getIsdefault()!= null) {
			buffer.append(" and nvl(isdefault,'N') = ? ");
			if(paramvo.getIsdefault().booleanValue()){
				sp.addParam("Y");
			}else{
				sp.addParam("N");
			}
		}

	}
	/**
	 * 查询子表，带翻译
	 */
	@Override
	public List<DcModelBVO> queryByPId(String PId,String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(PId) || StringUtil.isEmpty(pk_corp))
			return null;
		DcModelHVO headvo  = (DcModelHVO)singleObjectBO.queryByPrimaryKey(DcModelHVO.class, PId);
		if(headvo == null)
			return null;
		Collection<DcModelBVO> ancevos = null;
		if(pk_corp.equals(headvo.getPk_corp())){
			SQLParameter sp = new SQLParameter();
			sp.addParam(PId);
			ancevos = singleObjectBO.retrieveByClause(DcModelBVO.class, " nvl(dr,0) = 0 and pk_model_h = ?  ", " pk_model_b ", sp);
		}else if(IDefaultValue.DefaultGroup.equals(headvo.getPk_corp())
				&& !pk_corp.equals(headvo.getPk_corp())){
			//查询出来集团的，翻译成公司的科目pk
			SQLParameter sp = new SQLParameter();
			sp.addParam(PId);
			sp.addParam(IDefaultValue.DefaultGroup);
			ancevos = singleObjectBO.retrieveByClause(DcModelBVO.class, " nvl(dr,0) = 0 and pk_model_h = ? and pk_corp = ? ", " pk_model_b ", sp);
			Map<String, YntCpaccountVO> cpmap = accountService.queryMapByPk(pk_corp);
			transKM(cpmap,ancevos,pk_corp,PId);
		}
		List<DcModelBVO> list = null;
		if(ancevos != null && ancevos.size() > 0){
			list = new ArrayList<DcModelBVO>(ancevos);
		}
		return list;
	}
	/**
	 * 翻译
	 */
	private void transKM(Map<String, YntCpaccountVO> cpmap,Collection<DcModelBVO> ancevos,String pk_corp,String PId){
		if(ancevos == null || ancevos.size() == 0)
			return;
		for(DcModelBVO bvo : ancevos){
			String key = gl_jtsjtemserv.getCpidFromTd(bvo.getPk_accsubj(), pk_corp, null);
			YntCpaccountVO cpavo = cpmap.get(key);
			if(cpavo == null)
				continue;
			bvo.setPk_accsubj(key);//设置科目
			bvo.setPk_corp(pk_corp);
			bvo.setPk_model_h(PId);
			bvo.setKmmc(cpavo.getAccountname());
			bvo.setKmbm(cpavo.getAccountcode());
		}
	}

	@Override
	public void delete(String pid, String pk_corp) throws DZFWarpException {
		
		String[] arr = pid.split(",");
		String whereSql = constrDelSQL(arr);
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sf.append(" update ynt_dcmodel_h h set dr = 1 Where pk_corp = ? and h.pk_model_h in (  ");
		sf.append(whereSql);
		sf.append(" )");
		singleObjectBO.executeUpdate(sf.toString(), sp);
		
		sf = new StringBuffer();
		sf.append("update ynt_dcmodel_b b set dr = 1 Where pk_corp = ? and b.pk_model_h in ( ");
		sf.append(whereSql);
		sf.append(" )");
		singleObjectBO.executeUpdate(sf.toString(), sp);
		//singleObjectBO.deleteObjectByID(pid, new Class[]{DcModelHVO.class,DcModelBVO.class});
	}
	
	private String constrDelSQL(String[] arr){
		if(arr == null || arr.length == 0)
			throw new BusinessException("请选择相应数据进行删除");
		String whsql = SqlUtil.buildSqlConditionForIn(arr);
		
		return whsql;
		
	}

	/**
	 * 查询自己的模板。在加上集团的模板。合并成自己的一套模板，查询主表
	 */
	@Override
	public List<DcModelHVO> query(String pk_corp) throws DZFWarpException {
		List<DcModelHVO> list  = queryModelHVOs(pk_corp,"N",null);//不是快速制单的
		return list;
	}
	
	private List<DcModelHVO> queryModelHVOs(String pk_corp,String isquickcreate,String[] keyword)throws DZFWarpException {
		//查询集团数据
		DcModelHVO[]  jtdata = queryGroupData(pk_corp,isquickcreate,keyword);
		//查询公司数据
		DcModelHVO[] gsdaga = queryGsData(pk_corp,isquickcreate,keyword);
		if(jtdata == null || jtdata.length ==0)
			return gsdaga== null ||gsdaga.length ==0 ? null: new ArrayList<DcModelHVO>(Arrays.asList(gsdaga));
		if(gsdaga == null || gsdaga.length ==0)
			return jtdata== null ||jtdata.length ==0 ? null: new ArrayList<DcModelHVO>(Arrays.asList(jtdata));
		//数据合并，公司有模板以公司为准
		List<DcModelHVO> list = new ArrayList<DcModelHVO>();
		list.addAll(new ArrayList<DcModelHVO>(Arrays.asList(gsdaga)));
		Map<String,List<DcModelHVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(gsdaga), getGroupkey(pk_corp));
		for(DcModelHVO vo : jtdata){
			 String key = DZfcommonTools.getCombinesKey(vo, getGroupkey(pk_corp));
			 if(!map.containsKey(key)){
				 list.add(vo);
			 }
		}
		return list;
	}
	
	private DcModelHVO[]  queryGsData(String pk_corp,String isquickcreate,String[] keywords){
		DcModelHVO[] gsdaga = null;
		if(!IDefaultValue.DefaultGroup.equals(pk_corp)){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			if("Y".equals(isquickcreate)){
				sp.addParam("Y");
			}
			StringBuffer sf = new StringBuffer();
			sf.append(" nvl(dr,0) = 0 and pk_corp = ? ");
			if("Y".equals(isquickcreate)){
				sf.append(" and isdefault = ?   ");
			}
			//查询关键字
			if(keywords != null && keywords.length > 0){
				sf.append(" and (   ");
				for(int i = 0 ;i <keywords.length;i++){
					if(i==0){
						sf.append(" keywords like ?  ");
						sp.addParam("%"+keywords[i]+"%");
					}else{
						sf.append(" and keywords like ?  escape '#' ");
						sp.addParam("%#_"+keywords[i]+"%");
					}
				}
				sf.append(" ) ");
			}
			sf.append("  order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode ");
			gsdaga = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		}
		return gsdaga;
	}
	
	private DcModelHVO[] queryGroupData(String pk_corp,String isquickcreate,String[] keywords){
		CorpVO vo = corpService.queryByPk(pk_corp);
		String qyxz = null;
		if("一般纳税人".equals(vo.getChargedeptname())){
			qyxz = "一般纳税人";
		}else{
			qyxz = "小规模纳税人";
		}
		SQLParameter sp = new SQLParameter();
		if("Y".equals(isquickcreate)){
			sp.addParam("Y");
		}
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(vo.getCorptype());
		sp.addParam(qyxz);
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0     ");
		if("Y".equals(isquickcreate)){
			sf.append(" and isdefault = ?   ");
		}
		sf.append(" and (pk_corp = ? and pk_trade_accountschema = ? and (chargedeptname = ? or chargedeptname is null))   ");
		//查询关键字
		if(keywords != null && keywords.length > 0){
			sf.append(" and (   ");
			for(int i = 0 ;i <keywords.length;i++){
				if(i==0){
					sf.append(" keywords like ?  ");
					sp.addParam("%"+keywords[i]+"%");
				}else{
					sf.append(" and keywords like ?  escape '#' ");
					sp.addParam("%#_"+keywords[i]+"%");
				}
			}
			sf.append(" ) ");
		}
		sf.append(" order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode ");
		DcModelHVO[] ancevos = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		return ancevos;
	}

	@Override
	public String copyCorpToCorp(String[] gs, String[] ids, String userid,String loginpk) throws DZFWarpException {
		if(gs == null || gs.length ==0
				|| ids == null || ids.length == 0)
			return "<font color = 'red'>参数不正确</font><br>";
		if(gs.length>500){
			throw new BusinessException("复制公司数超限，请控制到500以内");
		}
		if(ids.length>50){
			throw new BusinessException("复制模板数超限，请控制到50以内");
		}
		//查询需要复制的模板
		DcModelHVO[] sources = queryModelHVObypks(ids,loginpk);
		DcModelHVO[] headcpvos = queryModelHVObycorps(gs,loginpk);
		Map<String,List<DcModelHVO>> map = DZfcommonTools.hashlizeObject(Arrays.asList(headcpvos), new String[]{"pk_corp"});
		if(map == null)
			map = new HashMap<String,List<DcModelHVO>>();
		CorpVO sourcepvo = corpService.queryByPk(loginpk);
		StringBuffer sbff = new StringBuffer();
		for(String tocorppk : gs){
			if(StringUtil.isEmpty(tocorppk))
				continue;
			if(tocorppk.equals(loginpk)){
				sbff.append("<font color = 'red'>不允许复制到当前登录公司</font><br>");
				continue;
			}
			//判断两家公司的科目方案是否一样。
			CorpVO topvo = corpService.queryByPk(tocorppk);
			if(!StringUtil.isEmpty(sourcepvo.getCorptype()) 
					&& sourcepvo.getCorptype().equals(topvo.getCorptype())){
				List<DcModelHVO> tolist = map.get(tocorppk);
				docopytocorp(tolist,sources,userid,topvo,sourcepvo.getAccountcoderule(),topvo.getAccountcoderule(),sbff);
			}else{
				//科目方案不一样
				sbff.append("<font color = 'red'>公司："+topvo.getUnitname()+"科目方案不一致，复制失败</font><br>");
			}
		}
		return sbff.toString();
	}
	
	
	private void docopytocorp(List<DcModelHVO> tolist,DcModelHVO[] sources,String userid,CorpVO topvo,String sourcerule,String torule,StringBuffer sbff){
		if(sources == null || sources.length ==0)
			return;
		String tocorppk = topvo.getPk_corp();
		Map<String,DcModelHVO> map = DZfcommonTools.hashlizeObjectByPk(tolist, getGroupkey(tocorppk));
		if(map == null)
			map = new HashMap<String,DcModelHVO>();
		YntCpaccountVO[] kmvos = accountService.queryByPk(tocorppk);
		//生成科目map
		Map<String, YntCpaccountVO> kmmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(kmvos), new String[]{"accountcode"});
		for(DcModelHVO source : sources){
			String key = DZfcommonTools.getCombinesKey(source, getGroupkey(source.getPk_corp()));
			if(map.containsKey(key)){
				sbff.append("<font color = 'red'>公司："+topvo.getUnitname()+"已存在业务模板，"+source.getBusitypetempname()+"</font><br>");
				continue;
			}
			String result = transDcModelHVO(kmmap,source,tocorppk,sourcerule,torule);
			if(StringUtil.isEmpty(result)){
				sbff.append("公司："+topvo.getUnitname()+"已复制模板，"+source.getBusitypetempname()+"<br>");
			}else{
				sbff.append("<font color = 'red'>公司："+topvo.getUnitname()+"，业务模板："+source.getBusitypetempname()+"，"+result+"</font><br>");
			}
		}
	}
	
	private String transDcModelHVO(Map<String, YntCpaccountVO> kmmap,DcModelHVO source,String tocorppk,String sourcerule,String torule){
		String result = null;
		if(source == null)
			return result;
		//查询子表
		SQLParameter sp = new SQLParameter();
		sp.addParam(source.getPk_model_h());
		DcModelBVO[] sourcebodys = (DcModelBVO[])singleObjectBO.queryByCondition(DcModelBVO.class, " pk_model_h = ? and nvl(dr,0) = 0 ", sp);
		DcModelHVO headvo  = buildHeadvo(source,tocorppk);
		List<DcModelBVO> list = buildBodyvos(kmmap,sourcebodys,tocorppk,sourcerule,torule);
		if(list!=null && list.size() > 0){
			saveDCmodelHVO(headvo,list);
		}else{
			result = "表体找不到对应科目，复制失败";
		}
		return result;
	}
	
	private void saveDCmodelHVO(DcModelHVO headvo,List<DcModelBVO> list){
		if(headvo==null || list==null || list.size()==0)
			return;
		headvo.setChildren(list.toArray(new DcModelBVO[0]));
		singleObjectBO.saveObject(headvo.getPk_corp(), headvo);
	}
	
	private DcModelHVO buildHeadvo(DcModelHVO other,String tocorppk){
		DcModelHVO headvo = (DcModelHVO)other.clone();
		headvo.setPk_corp(tocorppk);
		headvo.setPk_model_h(null);
		headvo.setPk_trade_accountschema(null);
		headvo.setChargedeptname(null);
		return headvo;
	}
	
	private List<DcModelBVO> buildBodyvos(Map<String, YntCpaccountVO> kmmap,DcModelBVO[] sourcebodys,String tocorppk,String sourcerule,String torule){
		if(sourcebodys == null || sourcebodys.length == 0)
			return null;
		List<DcModelBVO> list = new ArrayList<DcModelBVO>();
		for(DcModelBVO bb : sourcebodys){
			String bm =  KmbmUpgrade.getOriginalCode(bb.getKmbm(), sourcerule);
			if(StringUtil.isEmpty(bm))
				continue;
			String newbm = KmbmUpgrade.getNewCode(bm, "4/2/2/2/2", torule);
			YntCpaccountVO kmvo = kmmap.get(newbm);
			if(kmvo == null)
				continue;
			DcModelBVO bvo = (DcModelBVO)bb.clone();
			bvo.setPk_model_b(null);
			bvo.setPk_model_h(null);
			bvo.setPk_accsubj(kmvo.getPk_corp_account());
			bvo.setPk_corp(tocorppk);
			bvo.setKmbm(kmvo.getAccountcode());
			bvo.setKmmc(kmvo.getAccountname());
			list.add(bvo);
		}
		return list;
	}
	
	private DcModelHVO[] queryModelHVObycorps(String[] gs,String loginpk){
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0   ");
		sf.append(" and pk_corp in (");		
		SQLParameter sp = new SQLParameter();
		for(int i = 0 ;i<gs.length;i++){
			if(StringUtil.isEmpty(gs[i]))
				continue;
//			if(gs[i].equals(loginpk))//在后面有方法中有过滤
//				continue;
			sp.addParam(gs[i]);
			if(i==0){
				sf.append("?");
			}else{
				sf.append(",?");
			}
		}
		sf.append(") ");
		DcModelHVO[] vos = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		return vos;
	}
	
	private DcModelHVO[] queryModelHVObypks(String[] ids, String loginpk){
		StringBuffer sf = new StringBuffer();
		sf.append(" pk_corp = ? and nvl(dr,0) = 0   ");
		sf.append(" and pk_model_h in (");		
		SQLParameter sp = new SQLParameter();
		sp.addParam(loginpk);
		for(int i = 0 ;i<ids.length;i++){
			if(StringUtil.isEmpty(ids[i]))
				continue;
			sp.addParam(ids[i]);
			if(i==0){
				sf.append("?");
			}else{
				sf.append(",?");
			}
		}
		sf.append(") ");
		DcModelHVO[] vos = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		return vos;
	}

	@Override
	public String saveImp(InputStream is, String fileType, String userid, String pk_corp) throws DZFWarpException {
		String msg = "";
//		FileInputStream is = null;
		try {
//			is = new FileInputStream(inss);
			Workbook impBook = null;
			if("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				  impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}
			
			int sheetno = impBook.getNumberOfSheets();
			if (sheetno == 0) {
				throw new Exception("需要导入的数据为空。");
			}
			Sheet sheet1 = impBook.getSheetAt(0);
			List<DcModelImpVO> list = buildImport(sheet1,pk_corp);
			//保存主子表
			msg = saveImpdata(list,userid,pk_corp);
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("导入文件格式错误");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if(e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			if(e instanceof DAOException)
				throw new BusinessException("数据库错误");
			throw new BusinessException("导入失败，请检查！");
		}finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
		return msg;
	}
	
	private Map<String,List<KMCommonVO>> getKMAggVO(String pk_corp) {
		List<KMCommonVO> list = null;
		if(IDefaultValue.DefaultGroup.equals(pk_corp)){
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			String sql = " select accountcode code,accountname name,t.pk_trade_account id,t.pk_trade_accountschema pk_accschema,t.pk_corp from ynt_tdacc t where nvl(pk_corp,'000001') = ? and nvl(dr,0) = 0  ";
			list = (List<KMCommonVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(KMCommonVO.class));
		}else{
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			String sql = " select accountcode code,accountname name,t.pk_corp_account id,t.pk_corp_accountschema pk_accschema,t.pk_corp from ynt_cpaccount t where pk_corp = ? and nvl(dr,0) = 0   ";
			list = (List<KMCommonVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(KMCommonVO.class));
		}
		Map<String,List<KMCommonVO>> map = DZfcommonTools.hashlizeObject(list, new String[]{"code"});
		return map;
	}
	
	private String saveImpdata(List<DcModelImpVO> list,String userid, String pk_corp)throws DZFWarpException {
		if(list == null || list.size() ==0)
			return "数据为空，保存失败";
		//保存数据分组,,,,[accountschemaname] 字段在集团数据导入时有值 ，，在公司数据导入时 为空。
		Map<String,List<DcModelImpVO>> map = DZfcommonTools.hashlizeObject(list, impgroupkey);
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		DcModelHVO[] ancevos = (DcModelHVO[])singleObjectBO.queryByCondition(DcModelHVO.class, " nvl(dr,0) = 0 and pk_corp = ? ", sp);
		Map<String,DcModelHVO> map1 = null;
		if(ancevos != null && ancevos.length >0){
			map1 = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(ancevos), getGroupkey(pk_corp));
		}
		Map<String,List<KMCommonVO>> accountmap = getKMAggVO(pk_corp);
		List<DcModelHVO> headlist = new ArrayList<DcModelHVO>();
		
		StringBuffer sf = new StringBuffer();
		Map<String,String> codemap = getTransCodeMap();
		//查询 行业表
		Map<String, BDTradeVO> trademap =  queryTradeVO();
		//取当前公司最大编码
		Long max = getMaxBussinessCode(pk_corp);
		for(String key : map.keySet()){
			List<DcModelImpVO> listz = map.get(key);
			Collections.sort(listz);
			DcModelHVO hvo = build(max,trademap,codemap,accountmap,listz,map1,sf,pk_corp);
			if(hvo != null){
				headlist.add(hvo);
				//这里不批量了
				singleObjectBO.saveObject(pk_corp, hvo);
				sf.append("业务类型："+hvo.getBusitypetempname()+"，导入成功<br>");
			}
			max++;
		}
		return sf.toString();
	}
	
	private Map<String,BDTradeVO> queryTradeVO(){
		SQLParameter sp = new SQLParameter();
		sp.addParam("Z%");
		sp.addParam(IDefaultValue.DefaultGroup);
		BDTradeVO[] tradevos = (BDTradeVO[])singleObjectBO.queryByCondition(BDTradeVO.class, " tradecode like ? and pk_corp = ?  and nvl(dr,0) = 0 ", sp);
		if(tradevos == null || tradevos.length == 0)
			return null;
		Map<String,BDTradeVO> map = new HashMap<String,BDTradeVO>();
		for(BDTradeVO vo : tradevos){
			map.put(vo.getTradename(), vo);
		}
		return map;
	}
	
	private DcModelHVO build(Long max,Map<String,BDTradeVO> trademap,Map<String,String> codemap ,Map<String,List<KMCommonVO>> accountmap ,List<DcModelImpVO> listz,Map<String,DcModelHVO> map1,StringBuffer sf,String pk_corp){
		if(listz == null || listz.size() == 0)
			return null;
		String key = DZfcommonTools.getCombinesKey(listz.get(0), getGroupkey(pk_corp));
		if(map1!=null && map1.containsKey(key)){
			sf.append("<font color ='red'>当前业务类型："+listz.get(0).getBusitypetempname()+"已存在，导入失败</font><br>");
			return null;
		}
		DcModelImpVO vo = listz.get(0);
		DcModelHVO headvo = createHeadvo(max,trademap,codemap,vo,pk_corp);
		DcModelBVO[] bodyvos = createBodyvos(codemap,accountmap,listz,pk_corp,headvo.getPk_trade_accountschema());
		if(bodyvos==null||bodyvos.length == 0){
			sf.append("<font color ='red'>当前业务类型："+listz.get(0).getBusitypetempname()+"子表数据为空，导入失败</font><br>");
			return null;
		}
		headvo.setChildren(bodyvos);
		return headvo;
	}

	private DcModelHVO createHeadvo(Long max,Map<String,BDTradeVO> trademap,Map<String,String> codemap ,DcModelImpVO vo,String pk_corp){
		if(vo == null)
			return null;
		DcModelHVO headvo = new DcModelHVO();
		headvo.setBusitypetempcode(String.valueOf(max));
		headvo.setBusitypetempname(vo.getBusitypetempname());
		headvo.setVspstylecode(codemap.get(vo.getVspstylename()));
		headvo.setVspstylename(vo.getVspstylename());
		headvo.setSzstylecode(codemap.get(vo.getSzstylename()));
		headvo.setSzstylename(vo.getSzstylename());
		headvo.setChargedeptname(vo.getChargedeptname());
		if(!StringUtil.isEmpty(vo.getAccountschemaname())){
			headvo.setAccountschemaname(vo.getAccountschemaname());
			headvo.setPk_trade_accountschema(codemap.get(vo.getAccountschemaname()));
		}
		headvo.setVnote(vo.getVnote());
		//拼音简写
		String bustypename = vo.getBusitypetempname();
		if(!StringUtil.isEmpty(bustypename)){
			String res = PinyinUtil.getFirstSpell(bustypename);
			if(StringUtil.isEmpty(res)){
				headvo.setShortpinyin(bustypename);
			}else{
				headvo.setShortpinyin(res);
			}
		}
		headvo.setPk_corp(pk_corp);
		headvo.setDr(0);
		headvo.setIsdefault("是".equals(vo.getIsdefault()) ? DZFBoolean.TRUE:DZFBoolean.FALSE);
		if(trademap!=null){
			BDTradeVO advo = trademap.get(vo.getTradename());
			if(advo != null){
				headvo.setTradename(advo.getTradename());
				headvo.setPk_trade(advo.getPk_trade());
			}
		}
		headvo.setKeywords(vo.getKeywords());
		return headvo;
	}
	
	
	private DcModelBVO[] createBodyvos(Map<String,String> codemap ,Map<String,List<KMCommonVO>> accountmap ,List<DcModelImpVO> listz,String pk_corp,String pk_accschema){
		if(listz == null || listz.size() == 0)
			return null;
		List<DcModelBVO> list = new ArrayList<DcModelBVO>();
		for(DcModelImpVO body : listz){
			if(StringUtil.isEmpty(body.getKmbm()))
				continue;
			List<KMCommonVO> kmvos = accountmap.get(body.getKmbm());
			if(kmvos == null || kmvos.size() == 0)
				continue;
			DcModelBVO b = new DcModelBVO();
			if(IDefaultValue.DefaultGroup.equals(pk_corp)){
				for(KMCommonVO v : kmvos){
					if(!StringUtil.isEmpty(pk_accschema) && pk_accschema.equals(v.getPk_accschema())){
						b.setPk_accsubj(v.getId());
						b.setKmbm(v.getCode());
						b.setKmmc(v.getName());
						break;
					}
				}
			}else{
				b.setPk_accsubj(kmvos.get(0).getId());
				b.setKmbm(kmvos.get(0).getCode());
				b.setKmmc(kmvos.get(0).getName());
			}
			b.setPk_corp(pk_corp);
			b.setZy(body.getZy());
			b.setDirection("借方".equals(body.getDirection()) || "借".equals(body.getDirection()) ? 0:1);
			b.setVfield(codemap.get(body.getVfield()));
			b.setDr(0);
			list.add(b);
		}
		return list.toArray(new DcModelBVO[0]);
	}
	
	private List<DcModelImpVO> buildImport(Sheet sheet1, String pk_corp) throws DZFWarpException {
		List<DcModelImpVO> list = new ArrayList<DcModelImpVO>();
		try{
			String[][] data = getImpdatacom(pk_corp);
			for(int iBegin = 1;iBegin < sheet1.getLastRowNum()+1;iBegin++){
				DcModelImpVO vo = new DcModelImpVO();
				for(int i = 0;i<data.length;i++){
					Cell cell  = sheet1.getRow(iBegin).getCell(Integer.valueOf(data[i][0]));
					if(cell != null){
						String res = cell.getStringCellValue();
						if(!StringUtil.isEmpty(res)){
							vo.setAttributeValue(data[i][1], res.trim());
						}
					}
				}
				//校验
				if(IDefaultValue.DefaultGroup.equals(pk_corp)){
					if(StringUtil.isEmpty(vo.getKeywords())
							||StringUtil.isEmpty(vo.getBusitypetempname())
							||StringUtil.isEmpty(vo.getAccountschemaname())
							||StringUtil.isEmpty(vo.getKmbm())){
						throw new BusinessException("业务类型名称、科目方案、科目编码、关键字不能为空");
					}
				}else{
					if(StringUtil.isEmpty(vo.getKeywords())
							||StringUtil.isEmpty(vo.getBusitypetempname())
							||StringUtil.isEmpty(vo.getKmbm())){
						throw new BusinessException("业务类型名称、科目编码、关键字不能为空");
					}
				}
				list.add(vo);
			}
		}catch(Exception e){
			if(e instanceof BusinessException){
				throw e;
			}else{
				throw new BusinessException("导入转换数据错误，请确认excel为文本格式，主要检查业务编码和科目编码是否为文本格式");
			}	
		}
		return list;
	}
	
	private String[][] getImpdatacom(String pk_corp){
		if(IDefaultValue.DefaultGroup.equals(pk_corp)){
			return data2;
		}else{
			return data1;
		}
	}
	
	private String[][] data1 = new String[][]{
		{"0","busitypetempcode"},
		{"1","busitypetempname"},
		{"2","vspstylename"},
		{"3","szstylename"},
		{"4","zy"},
		{"5","kmbm"},
		{"6","kmmc"},
		{"7","direction"},
		{"8","vfield"},
		{"9","isdefault"},
		{"10","tradename"},
		{"11","keywords"}
	};
	
	
	private String[][] data2 = new String[][]{
		{"0","busitypetempcode"},
		{"1","busitypetempname"},
		{"2","accountschemaname"},
		{"3","chargedeptname"},
		{"4","vspstylename"},
		{"5","szstylename"},
		{"6","tradename"},
		{"7","keywords"},
		{"8","zy"},
		{"9","kmbm"},
		{"10","kmmc"},
		{"11","direction"},
		{"12","vfield"},
		{"13","isdefault"},
		{"14","vnote"}
	};
	
	private Map<String,String> getTransCodeMap(){
		Map<String,String> codemap = new HashMap<String,String>();
		codemap.put("现金收入", "01");
		codemap.put("现金支出", "02");
		codemap.put("银行收入", "03");
		codemap.put("银行支出", "04");
		codemap.put("其他收入", "05");
		codemap.put("其他支出", "06");
		//
		codemap.put("增值税专用发票", "01");
		codemap.put("增值税普通发票", "02");
		codemap.put("银行收付款回单", "20");
		codemap.put("其他票据", "21");
		//
		codemap.put("总金额", "totalmny");
		codemap.put("金额", "mny");
		codemap.put("无税金额", "wsmny");
		codemap.put("税额", "smny");
		codemap.put("含税金额", "hsmny");
		//
		codemap.put("小企业2013小会计", "00000100AA10000000000BMD");
		codemap.put("小企业2007新会计", "00000100AA10000000000BMF");
		codemap.put("小企业会计准则", "00000100AA10000000000BMD");
		codemap.put("企业会计准则", "00000100AA10000000000BMF");
		codemap.put("企业会计制度", "00000100000000Ig4yfE0005");
		return codemap;
	}

	/**
	 * 给智能识别返回结果
	 */
	@Override
	public List<DcModelHVO> queryAccordBankModel(String pk_corp, String[] keywords) throws DZFWarpException {
		List<DcModelHVO> list  = null;
		if(keywords != null && keywords.length > 0){
			list =  queryModelHVOs(pk_corp,"N",keywords);
		}
		return list;
	}
	
	public Long getMaxBussinessCode(String pk_corp) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String sql =" select max(h.busitypetempcode) billcode from ynt_dcmodel_h h where pk_corp=? and nvl(dr,0) = 0  ";
		String maxcode = (String)singleObjectBO.executeQuery(sql,sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String maxcode = null;
				if(rs.next()){
					maxcode =  rs.getString("billcode");
				}
				return maxcode;
			}
		});
		Long max = 2000l;
		try{
			if(!StringUtil.isEmpty(maxcode)){
				Long lastv = Long.valueOf(maxcode);
				max = lastv+1;
			}
		}catch(Exception e){
		}
		return max;
	}
}
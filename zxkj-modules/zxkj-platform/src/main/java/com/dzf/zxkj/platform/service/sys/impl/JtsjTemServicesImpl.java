package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BdTradeAccountVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.JtsjVO;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IJtsjTemService;
import com.dzf.zxkj.platform.service.tax.ICorpTaxService;
import com.dzf.zxkj.platform.util.KmbmUpgrade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service("sys_jtsjtemserv")
@SuppressWarnings("all")
public class JtsjTemServicesImpl implements IJtsjTemService {
	
	private SingleObjectBO singleObjectBO = null;
	@Autowired
	private ICorpTaxService corptaxserv = null;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	public List<JtsjVO> queryByKmmethod(String prjid, String pk_corp, boolean isgroup) throws DZFWarpException {
		StringBuffer sql = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		List<JtsjVO> list1= null;
		sp.addParam(pk_corp);
		if(IDefaultValue.DefaultGroup.equals(pk_corp)){//集团
			sp.addParam(prjid);
			sql.append(" select a.pk_jtsjtemplate,a.pk_corp,a.kmmethod,a.jfkm_id,a.dfkm_id, ");
			sql.append(" a.tax,a.memo,c.accountname jfkmmc ,d.accountname dfkmmc,a.pk_group ");
			sql.append("  from ynt_jtsj a ");
			sql.append(" left join ynt_tdacc c on a.jfkm_id = c.pk_trade_account ");
			sql.append(" left join ynt_tdacc d on a.dfkm_id = d.pk_trade_account ");
			sql.append("  where nvl(a.dr, 0) = 0 ");
			sql.append(" and a.pk_corp = ? ");
			sql.append(" and a.kmmethod = ? ");
			list1 = (List<JtsjVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(JtsjVO.class));
		}else{//公司级别
			sql.append(" select a.pk_jtsjtemplate,a.pk_corp,a.kmmethod,a.jfkm_id,a.dfkm_id, ");
			sql.append("  a.tax,a.memo,c.accountname jfkmmc ,d.accountname dfkmmc,a.pk_group,a.vdef1,a.vdef2 ");
			sql.append("    from ynt_jtsj a ");
			sql.append("  left join ynt_cpaccount c on a.jfkm_id = c.pk_corp_account and nvl(c.dr,0)=0 ");
			sql.append("  left join ynt_cpaccount d on a.dfkm_id = d.pk_corp_account and nvl(d.dr,0)=0 ");
			sql.append("  where nvl(a.dr, 0) = 0 and a.pk_corp = ? ");
			List<JtsjVO> list2 = (List<JtsjVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(JtsjVO.class));
			list1 = queryNotexistself(prjid,IDefaultValue.DefaultGroup,isgroup);
			if(list1==null)
				list1 = new ArrayList<JtsjVO>();
			if(list2!=null&&list2.size()>0)
				list1.addAll(list2);
		}
		
		dealSpelByQuery(list1, pk_corp);
		return list1;
	}
	

	//针对税率做些调整
	private void dealSpelByQuery(List<JtsjVO> list, String pk_corp){
		if(list == null || list.size() == 0)
			return;
		
		if(!IDefaultValue.DefaultGroup.equals(pk_corp)){//不是集团
			CorpTaxVo taxvo = corptaxserv.queryCorpTaxVO(pk_corp);
			DZFDouble citybuildtax = taxvo.getCitybuildtax();//城建税税率
			DZFDouble localeducaddtax = taxvo.getLocaleducaddtax();//地方教育费附加
//			DZFDouble educaddtax = taxvo.getEducaddtax();//教育费附加
			String dfmc;
			for(JtsjVO vo : list){
				dfmc = vo.getDfkmmc();
				if("应交城建税".equals(dfmc)){
					vo.setTax(SafeCompute.multiply(citybuildtax, new DZFDouble(100)));
				}
//				else if("应交教育费附加".equals(dfmc)){
//					vo.setTax(SafeCompute.multiply(educaddtax, new DZFDouble(100)));
//				}
				else if("应交地方教育附加".equals(dfmc)){
					vo.setTax(SafeCompute.multiply(localeducaddtax, new DZFDouble(100)));
				}
			}
		}
	}
	//针对税率做些处理
	private void dealSpelBySave(JtsjVO vo, String pk_corp, String dfmc){
		if(IDefaultValue.DefaultGroup.equals(pk_corp))
			return;
		
		DZFDouble tax = vo.getTax();
		String field = null;
		if("应交城建税".equals(dfmc)){
			field = "citybuildtax";
		}
//		else if("应交教育费附加".equals(dfmc)){
//			field = "educaddtax";
//		}
		else if("应交地方教育附加".equals(dfmc)){
			field = "localeducaddtax";
		}
		
		if(!StringUtil.isEmpty(field)){
			CorpTaxVo taxvo = corptaxserv.queryCorpTaxVO(pk_corp);
			taxvo.setAttributeValue(field, SafeCompute.div(tax, new DZFDouble(100)));
			singleObjectBO.update(taxvo, new String[]{field});
		}
	}
	
	private List<JtsjVO> queryNotexistself(String prjid,String pk_corp,boolean isgroup) throws DZFWarpException{
		if(!isgroup){
			if(IDefaultValue.DefaultGroup.equals(pk_corp))
				return null;
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(prjid);
//		
		//查询集团数据
		StringBuffer sql = new StringBuffer();
		sql.append(" select a.pk_jtsjtemplate,a.pk_corp,a.kmmethod,a.jfkm_id,a.dfkm_id, ");
		sql.append(" a.tax,a.memo,c.accountname jfkmmc ,d.accountname dfkmmc,a.pk_group ");
		sql.append("  from ynt_jtsj a ");
		sql.append(" left join ynt_tdacc c on a.jfkm_id = c.pk_trade_account ");
		sql.append(" left join ynt_tdacc d on a.dfkm_id = d.pk_trade_account ");
		sql.append("  where nvl(a.dr, 0) = 0 ");
		sql.append(" and a.pk_corp = ? ");
		sql.append(" and a.kmmethod = ? ");
		if(!isgroup){
			sp.addParam(pk_corp);
			sql.append(" and not exists(select 1 from ynt_jtsj e where nvl(e.dr,0) = 0 ");
			sql.append(" and e.pk_corp=? and e.pk_group = a.pk_jtsjtemplate)  ");
		}
		List<JtsjVO> list = (List<JtsjVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(JtsjVO.class));
		return list;
	}
	
	private void savedata(JtsjVO vo,String logincorppk,String loginuserid)throws DZFWarpException {
		if(vo==null)
			return;
		check(vo,logincorppk,loginuserid);
		vo.setJfkmmc(null);
		vo.setDfkmmc(null);
		if (StringUtil.isEmpty(vo.getPk_jtsjtemplate())){
			singleObjectBO.saveObject(vo.getPk_corp(), vo);
		}else{
			singleObjectBO.update(vo);
		}
	}
	
	private void check(JtsjVO vo,String logincorppk,String loginuserid)throws DZFWarpException{
		if(IDefaultValue.DefaultGroup.equals(logincorppk)){
			boolean isexist = check(vo);
			if(isexist)
				throw new BusinessException("当前公司存在重复的数据！保存失败！");
		}else{
			//1、判断是公司内是否重复
			boolean isexist = checkCorpExist(vo);
			if(isexist)
				throw new BusinessException("当前公司存在重复的数据！");
			//2、判断在集团内是否重复
//			boolean isexist1 = checkgroup(vo,logincorppk,loginuserid);
//			if(isexist1)
//				throw new BusinessException("存在与集团重复的数据！保存失败！");
		}
		//{集团级数据
			//1、如果新增、判断集团内是否重复
			//2、如果修改、判断集团内是否重复
		
		//}else{公司级数据
		
		//1、如果新增的
		     //1、直接新增、判断在公司内和集团内是否重复。
		     //2、如果修改集团的数据新增、则判断在公司内和集团内是否重复。
		//2、如果修改的
			 //1、判断在公司内和集团内是否重复。
		//}
	}
	
	/**
	 * 校验集团数据
	 */
	private boolean checkgroup(JtsjVO vo,String logincorppk,String loginuserid)throws DZFWarpException{
		String jfid=vo.getJfkm_id();
		String dfid=vo.getDfkm_id();
		String pk_group = vo.getPk_group();
		CorpVO cvo = corpService.queryByPk(logincorppk);
		String[] vos = getTradeAccountcodes(cvo.getCorptype());
		Map<String,String> codemap = KmbmUpgrade.getKmUpgradeinfo(cvo, vos);
		jfid = getJiTuanKmid(jfid,vo.getPk_corp(),loginuserid,codemap, cvo.getCorptype());
		dfid = getJiTuanKmid(dfid,vo.getPk_corp(),loginuserid,codemap, cvo.getCorptype());
		SQLParameter sp = new SQLParameter();
		sp.addParam(jfid);
		sp.addParam(dfid);
		sp.addParam(IDefaultValue.DefaultGroup);
		StringBuffer sf = new StringBuffer();
		sf.append(" select 1 from YNT_JTSJ where jfkm_id =? and dfkm_id = ? and pk_corp = ? and nvl(dr,0) = 0 ");
		if(!StringUtil.isEmpty(pk_group)){
			sf.append(" and pk_jtsjtemplate <> ? ");
			sp.addParam(pk_group);
		}
		boolean isexist = singleObjectBO.isExists(vo.getPk_corp(), sf.toString(), sp);
		return isexist;
	}
	
	public String[] getTradeAccountcodes(String pk_trade_accountschema){
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_trade_accountschema);
		BdTradeAccountVO[] vos =  (BdTradeAccountVO[])singleObjectBO.queryByCondition(BdTradeAccountVO.class,
				" pk_trade_accountschema=? and nvl(dr,0) = 0", sp);
		if(vos == null ||vos.length == 0)
			return null;
		List<String> list = new ArrayList<String>();
		for(BdTradeAccountVO s : vos){
			list.add(s.getAccountcode());
		}
		return list.toArray(new String[0]);
	}
	
	public String getJiTuanKmid(String kmid,String logincorp,String loginuserid,Map<String,String> codemap, String corptype)throws DZFWarpException{
		YntCpaccountVO[] vos = accountService.queryByPk(logincorp);
		YntCpaccountVO crrentvo = null;
		for(YntCpaccountVO s : vos){
			if(s.getPrimaryKey().equals(kmid)){
				crrentvo = s;
				break;
			}
		}
		if(crrentvo == null)
			return null;
		String acccode = crrentvo.getAccountcode();
		if(StringUtil.isEmpty(acccode))
			return null;
		acccode = codemap.get(acccode);
		String tdid = getTradepk(acccode, corptype);
		return tdid;
	}
	
	private String getTradepk(String tradecode, String corptype)throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(tradecode);
		sp.addParam(corptype);
		String sql = "select aw.pk_trade_account from ynt_tdacc aw where aw.accountcode = ? and nvl(aw.dr,0) = 0 and aw.pk_trade_accountschema = ? ";
		String pk_trade_account = (String)singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String pk_trade_account = null;
				if(rs.next()){
					pk_trade_account = rs.getString("pk_trade_account");
				}
				return pk_trade_account;
			}
		});
		return pk_trade_account;
	}
	
	/**
	 * 判断在一个公司内是否重复
	 * @param vo
	 * @throws DZFWarpException
	 */
	private boolean check(JtsjVO vo)throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getJfkm_id());
		sp.addParam(vo.getDfkm_id());
		sp.addParam(vo.getPk_corp());
		StringBuffer sf = new StringBuffer();
		sf.append(" select 1 from YNT_JTSJ where jfkm_id =? and dfkm_id = ? and pk_corp = ? and nvl(dr,0) = 0 ");
		if(!StringUtil.isEmpty(vo.getPrimaryKey())){
			sf.append(" and pk_jtsjtemplate <> ? ");
			sp.addParam(vo.getPrimaryKey());
		}
		boolean isexist = singleObjectBO.isExists(vo.getPk_corp(), sf.toString(), sp);
		return isexist;
	}
	
	private boolean checkCorpExist(JtsjVO vo)throws DZFWarpException{
		// 停用时不校验重复
		if ("N".equals(vo.getVdef1())) {
			return false;
		}
		String jfCode = null;
		String dfCode = null;
		if (!StringUtil.isEmpty(vo.getPk_group())) {
			BdTradeAccountVO jf = (BdTradeAccountVO) singleObjectBO.queryByPrimaryKey(BdTradeAccountVO.class, vo.getJfkm_id());
			if (jf != null) {
				jfCode = jf.getAccountcode();
			}
			BdTradeAccountVO df = (BdTradeAccountVO) singleObjectBO.queryByPrimaryKey(BdTradeAccountVO.class, vo.getDfkm_id());
			if (df != null) {
				dfCode = df.getAccountcode();
			}
		} else {
			YntCpaccountVO jf = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, vo.getJfkm_id());
			if (jf != null) {
				jfCode = jf.getAccountcode();
			}
			YntCpaccountVO df = (YntCpaccountVO) singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, vo.getDfkm_id());
			if (df != null) {
				dfCode = df.getAccountcode();
			}
		}
		StringBuilder sb = new StringBuilder();
		sb.append("select 1 from (select jt.pk_jtsjtemplate, ")
		.append(" coalesce(tdjf.accountcode, cpjf.accountcode)  as jfcode, ")
		.append(" coalesce(tddf.accountcode, cpdf.accountcode)  as dfcode ").append("from ynt_jtsj jt")
		.append(" left join ynt_tdacc tdjf").append(" on jt.pk_corp = '000001' and jt.jfkm_id = tdjf.pk_trade_account ")
		.append(" left join ynt_tdacc tddf on jt.pk_corp = '000001' and jt.dfkm_id = tddf.pk_trade_account")
		.append(" left join ynt_cpaccount cpjf on jt.pk_corp = ? and jt.jfkm_id = cpjf.pk_corp_account")
		.append(" left join ynt_cpaccount cpdf on jt.pk_corp = ? and jt.dfkm_id = cpdf.pk_corp_account")
		.append(" where (jt.pk_corp = ? and jt.pk_group is null and nvl(jt.vdef1, 'Y') = 'Y' or")
		.append(" jt.pk_corp = '000001' and jt.kmmethod = ? and jt.pk_jtsjtemplate not in")
		.append(" (select pk_group from ynt_jtsj where pk_corp = ? and vdef1 = 'N' and pk_group is not null and nvl(dr, 0) = 0))")
		.append(" and nvl(jt.dr, 0) = 0) where jfcode = ? and dfcode = ?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getPk_corp());
		CorpVO cvo = corpService.queryByPk(vo.getPk_corp());
		sp.addParam(cvo.getCorptype());
		sp.addParam(vo.getPk_corp());
		sp.addParam(jfCode);
		sp.addParam(dfCode);
		if(!StringUtil.isEmpty(vo.getPrimaryKey())){
			sb.append(" and pk_jtsjtemplate <> ? ");
			sp.addParam(StringUtil.isEmpty(vo.getPk_group()) ? vo.getPrimaryKey() : vo.getPk_group());
		}
		boolean isexist = singleObjectBO.isExists(vo.getPk_corp(), sb.toString(), sp);
		return isexist;
	}

	@Override
	public void save(JtsjVO vo,String logincorppk,String loginuser) throws DZFWarpException {
		if(vo==null)
			return;
		String jfkm_id=vo.getJfkm_id();
		String dfkm_id=vo.getDfkm_id();
		String dfmc = vo.getDfkmmc();
		if(StringUtil.isEmpty(jfkm_id) || StringUtil.isEmpty(dfkm_id)){
			throw new BusinessException("请输入或选择正确的科目编码");
		}
		if(vo.getPk_corp().equals(logincorppk)){
			savedata(vo,logincorppk,loginuser);
		}
		if(IDefaultValue.DefaultGroup.equals(vo.getPk_corp())
				&& !StringUtil.isEmpty(vo.getPk_jtsjtemplate())
				&& !vo.getPk_corp().equals(logincorppk)){
			//转换VO
			bulidvo(vo,logincorppk,loginuser);
			savedata(vo,logincorppk,loginuser);
		}
		
		dealSpelBySave(vo, logincorppk, dfmc);
	}
	
	private void bulidvo(JtsjVO vo,String logincorppk,String loginuser){
		if(vo==null)
			return;
		vo.setPk_group(vo.getPk_jtsjtemplate());
		vo.setPk_jtsjtemplate(null);
		vo.setPk_corp(logincorppk);
		String jfid = getCpidFromTd(vo.getJfkm_id(),logincorppk,loginuser);
		String dfid = getCpidFromTd(vo.getDfkm_id(),logincorppk,loginuser);
		vo.setJfkm_id(jfid);
		vo.setDfkm_id(dfid);
		vo.setDr(0);
	}
	
	public String getCpidFromTd(String id,String logincorppk,String loginuser){
		YntCpaccountVO[] vos= accountService.queryByPk(logincorppk);
		boolean exist = isExists(vos,id);
		if(exist)
			return id;
		CorpVO cvo =  (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, logincorppk);
		String code = getNewCode(id,cvo.getAccountcoderule());
		SQLParameter sp = new SQLParameter();
		sp.addParam(code);
		sp.addParam(logincorppk);
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_cpaccount bb  ");
		sf.append("   where bb.accountcode = ? ");
		sf.append(" and bb.pk_corp = ? and nvl(bb.dr,0)=0 ");
		List<YntCpaccountVO> list = (List<YntCpaccountVO>) singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(YntCpaccountVO.class));
		return list!=null&&list.size()>0?list.get(0).getPk_corp_account():null;
	}
	
	private String getNewCode(String id,String newrule){
		String oldcode = getTradeCode(id);
		if(StringUtil.isEmpty(newrule)
				|| "~".equals(newrule)
				|| newrule.startsWith(DZFConstant.ACCOUNTCODERULE)){
			return oldcode;
		}
		return KmbmUpgrade.getNewCode(oldcode, DZFConstant.ACCOUNTCODERULE, newrule);
	}
	
	private String getTradeCode(String id){
		SQLParameter sp = new SQLParameter();
		sp.addParam(id);
		String sql = "select aw.accountcode from ynt_tdacc aw where aw.pk_trade_account = ? and nvl(aw.dr,0) = 0 ";
		String accountcode = (String)singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor(){
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String accountcode = null;
				if(rs.next()){
					accountcode = rs.getString("accountcode");
				}
				return accountcode;
			}
		});
		return accountcode;
	}

	private boolean isExists(YntCpaccountVO[] vos,String id){
		if(vos == null || vos.length == 0 || StringUtil.isEmpty(id))
			return false;
		for(YntCpaccountVO s :vos){
			if(s.getPrimaryKey().equals(id)){
				return true;
			}
		}
		return false;
	}
	
	
	@Override
	public void delete(JtsjVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
	}
	@Override
	public JtsjVO queryById(String id) throws DZFWarpException {
		return (JtsjVO) singleObjectBO.queryByPrimaryKey(JtsjVO.class, id);
	}

}
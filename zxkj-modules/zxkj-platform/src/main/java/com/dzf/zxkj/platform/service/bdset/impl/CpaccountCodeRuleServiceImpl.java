package com.dzf.zxkj.platform.service.bdset.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountChangeVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.base.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("gl_accountcoderule")
@Slf4j
public class CpaccountCodeRuleServiceImpl implements ICpaccountCodeRuleService {

	private final String SPLIT = "/";
	
	//数据已改变
	private final String CHANGED = "1";
	
	private SingleObjectBO sbo;

	@Override
	public YntCpaccountChangeVO[] updateCodeRule(String pk_corp, String oldrule,
												 String newrule, Object ...Otherparams) throws DZFWarpException {
	
		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(oldrule)
				||StringUtil.isEmpty(newrule)){
			
			throw new BusinessException("参数不合法请检查参数");
		}
		
		if(!codeRuleValidate(oldrule,newrule)){
			throw new BusinessException("请检查新旧规则参数是否正确");
		}
		
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		
		YntCpaccountVO[] vos =(YntCpaccountVO[]) getSbo().queryByCondition(YntCpaccountVO.class,"pk_corp=? and nvl(dr,0)=0 order by accountcode ", sp);// AccountCache.getInstance().get(null, pk_corp);

		List<String> repeatcolumns = (List<String>) getSbo().executeQuery("select accountcode from ynt_cpaccount where pk_corp=? and nvl(dr,0)=0 group by accountcode having count(1)>1", sp, new ColumnListProcessor());
		
		if(repeatcolumns!=null && repeatcolumns.size()>0){
			log.info("存在重复的会计科目，请联系客服。");
			throw new BusinessException("存在重复的会计科目，请联系客服。");
		}
		
		if(vos==null||vos.length==0){
			log.info("从缓存中没有找到公式对应的会计科目数据 开始从数据库中加载");
			sp.clearParams();
			vos=(YntCpaccountVO[]) getSbo().queryByCondition(YntCpaccountVO.class,"pk_corp=? and nvl(dr,0)=0 order by accountcode ", sp);
		}
		
		if(vos==null||vos.length==0){
			log.info("数据库中加载找不到公司对应的科目信息");
			throw new BusinessException("数据库中加载找不到公司对应的科目信息");
		}
		
		String accountmemo = "";
		if(Otherparams!=null&&Otherparams.length>0){
			accountmemo = Otherparams[0].toString();
		}
		
		List<YntCpaccountChangeVO> listvos = new ArrayList<YntCpaccountChangeVO>();
		//需要更新的数据
		List<YntCpaccountVO> needupdatevos =  new ArrayList<YntCpaccountVO>();
		for(YntCpaccountVO vo : vos){
		
			YntCpaccountChangeVO changevo = new YntCpaccountChangeVO();
			changevo.setPk_corp(pk_corp);
			changevo.setChangeversion(1);
			String oldcode = vo.getAccountcode();
			String newcode = getNewCode(oldcode,oldrule,newrule);
			if(!newcode.equals(oldcode)){
				
				vo.setAccountcode(newcode);
				vo.setVdef1(CHANGED);
				vo.setVdef2(oldcode);//暂存
				needupdatevos.add(vo);
				changevo.setIschanged(new DZFBoolean(true));
			}

			changevo.setOldcode(oldcode);
			changevo.setOldname(vo.getAccountname());
			changevo.setNewname(vo.getAccountname());
			changevo.setNewcode(newcode);
			changevo.setMemo(accountmemo);
			listvos.add(changevo);
		}

		if(needupdatevos.size()>0){
			log.info("科目编码规则变化需要更新的行数为："+needupdatevos.size());
			updateRefDatas(pk_corp,needupdatevos.toArray(new YntCpaccountVO[0]));
		}
		
		saveChangInfo(pk_corp,listvos);
		updateCorpAccountCodeRule(pk_corp,newrule,accountmemo);
		
		return listvos.toArray(new YntCpaccountChangeVO[0]);
	}
	
	
	public void saveChangInfo(String pk_corp,List<YntCpaccountChangeVO> listvos){
		
		try {
			String updatesql = "update ynt_sjwh_dataupgrade a set a.changeversion = a.changeversion+1 where a.pk_corp=?";
			SQLParameter param = new SQLParameter();
			param.addParam(pk_corp);
			int rows = getSbo().executeUpdate(updatesql, param);
			log.info("更新ynt_sjwh_dataupgrade历史版本影响行数为："+rows);
			
			String[] pks = getSbo().insertVOArr(pk_corp, listvos.toArray(new YntCpaccountChangeVO[0]));
			log.info("保存新编码规则对应的明细为："+pks.length+"行");
		} catch (Exception e) {
			log.error("保存新编码规则明细异常", e);
			throw new  BusinessException("保存新编码规则明细异常");
		}
		
	}
	
	
	@Override
	public String getNewRuleCode(String oldCode, String oldrule, String newrule)
			throws DZFWarpException {
		
		return getNewCode(oldCode,oldrule,newrule);
	}
	
	
	/***
	 * 更新公司对应的科目编码规则
	 *    目前科目编码规则保存在公司的自定义项4中 可能需要修改
	 * 
	 * @param pk_corp  公司
	 * @param newrule  要更新的科目编码规则
	 */
	public void updateCorpAccountCodeRule(String pk_corp,String newrule,String accountmemo)throws DZFWarpException{
		
		try {
			String updatesql = "update bd_corp set accountcoderule=?,accountcoderulememo=? where pk_corp=?";
			SQLParameter sp = new SQLParameter();
			sp.addParam(newrule);
			sp.addParam(accountmemo);
			sp.addParam(pk_corp);
			getSbo().executeUpdate(updatesql, sp);
		} catch (Exception e) {
			log.error("更新公司对应的科目编码规则发生异常", e);
			throw new  BusinessException("更新公司对应的科目编码规则发生异常");
		}
		log.info("更新公司["+pk_corp+"]对应的科目编码规则["+newrule+"]成功");

	}
	
	
	/****
	 * 更新引用数据
	 * 
	 * @param pk_corp 公司
	 * @param vos  需要更新的会计科目vo
	 * @throws BusinessException
	 */
	public void updateRefDatas(String pk_corp,YntCpaccountVO[] vos)throws DZFWarpException{
		
		if(vos==null||vos.length<=0||StringUtil.isEmpty(pk_corp)){
			return ;
		}
		
		try {
			getSbo().updateAry(vos,new String[]{"accountcode","vdef1","vdef2"});
			
			//期间损益模板
			String  qjsyupdate = getUpdateSql("ynt_cptransmb","accountcode");
			SQLParameter param = new SQLParameter();
			param.addParam(CHANGED);
			param.addParam(pk_corp);
			param.addParam(CHANGED);
			int effectrows = getSbo().executeUpdate(qjsyupdate, param);
			log.info("期间损益模板 科目编码更新"+effectrows+"行");
			
			//成本结转模板  贷方
			String  cbjzdfupdate =  getUpdateSql("ynt_cpcosttrans","dvcode");
			effectrows = getSbo().executeUpdate(cbjzdfupdate, param);
			log.info("成本结转模板 贷方 科目编码更新"+effectrows+"行");
			
			//成本结转模板   借方
			String  cbjzjfupdate = getUpdateSql("ynt_cpcosttrans","jvcode");
			effectrows = getSbo().executeUpdate(cbjzjfupdate, param);
			log.info("成本结转模板 借方 科目编码更新"+effectrows+"行");
			
			//汇兑损益模板  收益
			String  hdsysyupdate = getUpdateSql("ynt_remittance","accountcode");
			effectrows = getSbo().executeUpdate(hdsysyupdate, param);
			log.info("汇兑损益模板 收益 科目编码更新"+effectrows+"行");
			
			//汇兑损益模板板    损失
			String  hdsyssupdate = getUpdateSql("ynt_remittance","outatcode");
			effectrows = getSbo().executeUpdate(hdsyssupdate, param);
			log.info("汇兑损益模板 损失 科目编码更新"+effectrows+"行");
			
			//期初开账 科目期初余额
			String  qckzkmyeupdate = getUpdateSql("ynt_qcye","vcode");
			effectrows = getSbo().executeUpdate(qckzkmyeupdate, param);
			log.info("期初开账 科目期初余额 科目编码更新"+effectrows+"行");
			
			//凭证
			String  pzupdate = getUpdateSql("ynt_tzpz_b","vcode");
			effectrows = getSbo().executeUpdate(pzupdate, param);
			log.info("凭证 科目编码更新"+effectrows+"行");
			
			//常用凭证模板
			String  cypzmbupdate = getUpdateSql("ynt_cppztemmb_b","vcode");
			effectrows = getSbo().executeUpdate(cypzmbupdate, param);
			log.info("常用凭证模板更新"+effectrows+"行");
			
			//收入预警
			String  sryjupdate = getUpdateSql("ynt_incomewarning","kmbm");
			effectrows = getSbo().executeUpdate(sryjupdate, param);
			log.info("收入预警更新"+effectrows+"行");
			
			
			//利润结转模板主表
			String  lrjzhupdate = getUpdateSql("ynt_cptranslr","accountcode");
			effectrows = getSbo().executeUpdate(lrjzhupdate, param);
			log.info("利润结转模板表"+effectrows+"行");
			
			String  lrjzhupdate1 = getUpdateSql("ynt_cptranslr","vcode");
			effectrows = getSbo().executeUpdate(lrjzhupdate1, param);
			log.info("利润结转模板表"+effectrows+"行");
			
			//计提税金模板
			String  jtsjupdate1 = getUpdateSql("ynt_jtsj","jfkm_id");
			effectrows = getSbo().executeUpdate(jtsjupdate1, param);
			log.info("计提税金模板表"+effectrows+"行");
			
			String  jtsjupdate2 = getUpdateSql("ynt_jtsj","dfkm_id");
			effectrows = getSbo().executeUpdate(jtsjupdate2, param);
			log.info("计提税金模板表"+effectrows+"行");
			
			//科目与税目关联关系
			String  taxrelationupdate = getUpdateSql("ynt_taxrelation","subj_code");
			effectrows = getSbo().executeUpdate(taxrelationupdate, param);
			log.info("科目与税目关联关系"+effectrows+"行");
			
			//业务类型模板
			String  dcmodelupdate = getUpdateSql("ynt_dcmodel_b","kmbm");
			effectrows = getSbo().executeUpdate(dcmodelupdate, param);
			log.info("业务类型模板"+effectrows+"行");
			
			//库存模板
			String  invaccmodel = getUpdateSql("ynt_invaccmodel","kmbm");
			effectrows = getSbo().executeUpdate(invaccmodel, param);
			log.info("库存模板"+effectrows+"行");
			
			
			for(YntCpaccountVO vo : vos){
				vo.setVdef1("");
				vo.setVdef2("");
			}
			//清空临时值
			getSbo().updateAry(vos,new String[]{"vdef1","vdef2"});

			log.info("科目编码规则变化更行引用数据完毕");
			
		} catch (Exception e) {
			log.error("变更科目编码规则更新引用数据时发生异常", e);
			throw new BusinessException("变更科目编码规则更新引用数据时发生异常");
		}
	}
	
	
	/****
	 * 获取更新语句
	 * @param tablename  需要更新的表
	 * @param codename  更新表中科目编码对应的字段
	 * @return
	 */
	public String getUpdateSql(String tablename,String codename){
		
		StringBuilder sb = new StringBuilder();
		sb.append("update ")
		.append(tablename).append(" b " )
		.append(" set b.").append(codename).append("=(")
		.append("select a.accountcode from ynt_cpaccount a where a.vdef1=? and a.pk_corp=b.pk_corp and a.vdef2=b.")
		.append(codename).append(") where b.pk_corp=?")
		.append(" and exists (select 1 from ynt_cpaccount c where  c.pk_corp=b.pk_corp  and c.vdef2 =b.").append(codename)
		.append(" and c.vdef1=?) ");
		
		return sb.toString();
	}
	
	
	/****
	 * 获取新编码
	 * @param oldcode  旧编码
	 * @param oldrule  旧编码规则
	 * @param newrule  新编码规则
	 * 
	 * @return 新编码
	 */
	public String getNewCode(String oldcode,String oldrule,String newrule)throws DZFWarpException {
		
		try {
			String[] odru = oldrule.split(SPLIT);
			String[] newru = newrule.split(SPLIT);
			
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
			log.error("获取新的科目编码异常", e);
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
	
	/**
	 * 获取一个oldcode数组推出来一个map集合
	 * @param oldcode
	 * @param oldrule
	 * @param newrule
	 * @return
	 * @throws BusinessException
	 */
	public String[] getNewCodes(String[] oldcode,String oldrule,String newrule) throws DZFWarpException{
		
		if(oldcode== null || oldcode.length ==0){
			throw new BusinessException("转换编码不能为空!");
		}
		
		if(StringUtil.isEmpty(oldrule) || StringUtil.isEmpty(newrule)){
			throw new BusinessException("编码规则不能为空!");
		}
//		Map<String, String> resmap = new HashMap<String, String>();
		String[] reslist = new String[oldcode.length];
		for(int i=0;i<oldcode.length;i++){
			String restemp = getNewCode(oldcode[i], oldrule, newrule);
			reslist[i]= restemp;
		}
		return reslist;
		
	}
	
	
/*	@SuppressWarnings("rawtypes")
	public boolean validateOldCodeRule(String pk_corp,String oldrule)throws BusinessException {
		
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		String sql = "select coderule from bd_corp where pk_corp=?";
		Map map = (Map)getSbo().executeQuery(sql, param, new MapProcessor());
		if(map==null||map.size()<=0){
			throw new BusinessException("找不到公司请确认公司参数传递正确性");
		}
		
		Object obj = map.get("coderule");
		if(obj==null||obj.toString().trim().length()<0){
			throw new BusinessException("找不到公司对应的科目编码规则");
		}
		
		if(!oldrule.equals(obj.toString().trim())){
			throw new BusinessException("请检查旧科目编码规则传递的正确性");
		}
		
		return true;

	}*/
		
	
	/****
	 * 编码规则校验
	 *     失败情况
	 *        新编码规则等于旧编码规则
	 *        旧编码规则任何级次位数小于2
	 *        旧编码规则任意级次位数大于新编码规则对应级次位数
	 * 
	 * @param oldrule  旧编码规则
	 * @param newrule  新编码规则
	 * @return  true 校验通过  ；false 校验失败
	 */
	private boolean codeRuleValidate(String oldrule,String newrule){
				
		try {
			if(oldrule.trim().equals(newrule.trim())){
				return false;
			}
			
			String[] odru = oldrule.split(SPLIT);
			String[] newru = newrule.split(SPLIT);
			
			for(int i=0,len=Math.min(odru.length, newru.length);i<len;i++){
				if(Integer.parseInt(odru[i])<2||(Integer.parseInt(odru[i])>Integer.parseInt(newru[i]))){
					
					return false;
				}
			}
		} catch (Exception e) {
			
			return false;
		}
		
	    return true;
		
	}
	
	
	public SingleObjectBO getSbo() {
		if(sbo==null){
			sbo=(SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		}
		return sbo;
	}


	@SuppressWarnings("unchecked")
	@Override
	public YntCpaccountChangeVO[] loadData(String pk_corp)
			throws DZFWarpException {
		
		try {
			String condition = " changeversion=1 and pk_corp=?  order by oldcode";
			SQLParameter param = new SQLParameter();
			param.addParam(pk_corp);
			List<YntCpaccountChangeVO> res = (List<YntCpaccountChangeVO>)getSbo().retrieveByClause(YntCpaccountChangeVO.class, condition, param);
			
			if(res!=null&&res.size()>0){
				
				log.info("获取数据成功条数为："+res.size());
				return res.toArray(new YntCpaccountChangeVO[0]);
			}
			log.info("获取数据为空");
		
		} catch (Exception e) {
			log.error("获取数据异常"+e.getMessage(),e);
			throw new BusinessException("获取数据异常");
		}

		return null;
	}


	@Override
	public Map<String, String> getNewCodeMap(String[] oldcode, String oldrule, String newrule) throws DZFWarpException {
		
		Map<String,String> resmap = new HashMap<String,String>();
		if(oldcode== null || oldcode.length ==0){
			throw new BusinessException("转换编码不能为空!");
		}
		
		if(StringUtil.isEmpty(oldrule) || StringUtil.isEmpty(newrule)){
			throw new BusinessException("编码规则不能为空!");
		}
//		String[] reslist = new String[oldcode.length];
		for(int i=0;i<oldcode.length;i++){
			String restemp = getNewCode(oldcode[i], oldrule, newrule);
//			reslist[i]= restemp;
			resmap.put(oldcode[i], restemp);
		}
		return resmap;
		
	}




}

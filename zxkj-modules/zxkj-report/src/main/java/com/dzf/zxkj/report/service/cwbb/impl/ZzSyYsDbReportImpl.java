package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.ZzSyYsDBVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IZzSyYsDbReport;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 增值税和营业税月度申报对比表

 * @author zhangj
 *
 */
@Service("gl_rep_yyssbbserv")
public class ZzSyYsDbReportImpl implements IZzSyYsDbReport {

	@Reference
	private IZxkjPlatformService zxkjPlatformService;

	@Autowired
	private IFsYeReport gl_rep_fsyebserv;

	@Autowired
	private SingleObjectBO singleObjectBO = null;

	
	/**
	 * 增值税和营业税月度申报对比表取数
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	public ZzSyYsDBVO[] getZZSYYSDBVOs(String period, String pk_corp) throws DZFWarpException {
		
		if(!StringUtil.isEmptyWithTrim(pk_corp)){
			if(pk_corp.indexOf(",")>0){
				//多个公司
				String[] pk_corps = pk_corp.split(",") ;
				ZzSyYsDBVO[] vos = new ZzSyYsDBVO[pk_corps.length] ;
				for(int i=0 ;i<pk_corps.length;i++){
					Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corps[i]);
					if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
						ZzSyYsDBVO[] reslist = getZZSYYSDB2007VOs(new String[]{period} , pk_corps[i]) ;
						//2007会计准则
						vos[i] = reslist[0];
					}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
						ZzSyYsDBVO[] reslist = getZZSYYSDB2013VOs(new String[]{period} , pk_corps[i]) ;
						//2013会计准则
						vos[i] = reslist[0];
					}else{
						throw new BusinessException("该制度暂不支持利润表,敬请期待!");
					}
				}
				return vos ;
			}else{
				Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
				//单个公司
				if(corpschema == DzfUtil.SEVENSCHEMA.intValue()){
					ZzSyYsDBVO[] reslist = getZZSYYSDB2007VOs(new String[]{period} , pk_corp) ;
					//2007会计准则
					return  reslist;
				}else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()){
					ZzSyYsDBVO[] reslist = getZZSYYSDB2013VOs(new String[]{period} , pk_corp) ;
					//2013会计准则
					return reslist;
				}else{
					throw new BusinessException("该制度暂不支持利润表,敬请期待!");
				}
			}
		}else{
			return null ;
		}
		
	}
	
	/**
	 * 根据传入的科目编码找到科目当期的发生额，并根据方向计算发生额方向
	 * @param accountcode
	 * @param pk_corp
	 * @param period
	 * @param fx 代表方向：0：借方；1：贷方
	 * @return
	 * @throws BusinessException
	 */
	private DZFDouble getBQFSEByKm(String accountcode , String pk_corp , String period , int fx) throws DZFWarpException{
		DZFDouble bqfseMny = DZFDouble.ZERO_DBL ;
		//根据科目编码找到科目主键
		//则查询本期的发生额，自动包含下级 //zpm修改
		SQLParameter sp  = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select sum(b.jfmny) as jf, sum(b.dfmny) as df from ynt_tzpz_b b  ");
		sf.append(" join ynt_tzpz_h h    on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf.append(" join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
		sf.append(" where  h.doperatedate like ? ");
		sp.addParam(period+"%");
		sf.append(" and h.pk_corp = ? and t.accountcode like  ? ");
		sp.addParam(pk_corp);
		sp.addParam(accountcode+"%");
		sf.append(" and t.pk_corp =  ? and nvl(h.dr,0) = 0 and nvl(b.dr,0) = 0 and nvl(t.dr,0) = 0  ");
		sp.addParam(pk_corp);
		ArrayList result = (ArrayList) singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(KmZzVO.class));
		
		if(result!=null&&result.size()>0){
			KmZzVO kmzzVO = (KmZzVO)result.get(0) ;
			//截止本期初本科目累计借方发生额
			DZFDouble jf = kmzzVO.getJf()==null?DZFDouble.ZERO_DBL:kmzzVO.getJf() ;
			//截止本期初本科目累计贷方发生额
			DZFDouble df = kmzzVO.getDf()==null?DZFDouble.ZERO_DBL:kmzzVO.getDf();
			if(0==fx){
				//借方=借方累计
				bqfseMny = jf;//.sub(df) ;
			}else{
				//贷方=贷方累计
				bqfseMny = df;//.sub(jf) ;
			}
		}
	
		return bqfseMny ;
	}
	
	
	/**
	 * 根据传入的科目编码找到科目当期的发生额，并根据方向计算发生额方向
	 * @param accountcode
	 * @param pk_corp
	 * @param period
	 * @param fx 代表方向：0：借方；1：贷方
	 * @return
	 * @throws BusinessException
	 */
	private DZFDouble getBQFSEByKmMap(String accountcode , String pk_corp , String period ,int fx,Map<String,KmZzVO> mapzzvos) throws DZFWarpException{
		DZFDouble bqfseMny = DZFDouble.ZERO_DBL ;
		//则查询本期的发生额，自动包含下级
		KmZzVO result = mapzzvos.get(period);
		if(result!=null ){
			KmZzVO kmzzVO = (KmZzVO)result ;
			//截止本期初本科目累计借方发生额
			DZFDouble jf = kmzzVO.getJf()==null?DZFDouble.ZERO_DBL:kmzzVO.getJf() ;
			//截止本期初本科目累计贷方发生额
			DZFDouble df = kmzzVO.getDf()==null?DZFDouble.ZERO_DBL:kmzzVO.getDf();
			if(0==fx){
				//借方=借方累计
				bqfseMny = jf;//.sub(df) ;
			}else{
				//贷方=贷方累计
				bqfseMny = df;//.sub(jf) ;
			}
		}
	
		return bqfseMny ;
	}
	
	
	/**
	 * 根据传入的科目编码找到科目当期的发生额，并根据方向计算发生额方向
	 * @param accountcode
	 * @param pk_corp
	 * @return
	 * @throws BusinessException
	 */
	private List<KmZzVO>  getBQFSEByKmPeriod(String accountcode , String pk_corp , String year) throws DZFWarpException{
		//则查询本期的发生额，自动包含下级 //zpm修改
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" select period,sum(b.jfmny) as jf, sum(b.dfmny) as df from ynt_tzpz_b b  ");
		sf.append(" join ynt_tzpz_h h    on b.pk_tzpz_h = h.pk_tzpz_h ");
		sf.append(" join ynt_cpaccount t on b.pk_accsubj = t.pk_corp_account ");
		sf.append(" where  nvl(t.dr,0) = 0  and nvl(b.dr,0) = 0  and nvl(h.dr,0) = 0  ");
		sf.append(" and h.pk_corp = ? and t.accountcode like  ? ");
		sp.addParam(pk_corp);
		sp.addParam(accountcode+"%");
		sf.append(" and t.pk_corp = ?  and substr(h.period,0,4) = ?  group by period ");
		sp.addParam(pk_corp);
		sp.addParam(year);
		ArrayList result = (ArrayList) singleObjectBO.executeQuery(sf.toString(), sp,new BeanListProcessor(KmZzVO.class));
		return result ;
	}
	
	
	private ZzSyYsDBVO[] getZZSYYSDB2007VOs(String[] periods , String pk_corp) throws DZFWarpException{
		
		CorpVO corpVO = (CorpVO) singleObjectBO.queryVOByID(pk_corp,
				CorpVO.class);
		CorpTaxVo taxvo = zxkjPlatformService.queryCorpTaxVO(pk_corp);
		
		List<ZzSyYsDBVO> listzzsy = new ArrayList<ZzSyYsDBVO>();

		Map<String, List<FseJyeVO>> monthmap = null;

		if (periods != null && periods.length > 0) {
			String year = periods[0].substring(0, 4);
			Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp,null,"zzsyys");
			monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		}
		String newrule = zxkjPlatformService.queryAccountRule(pk_corp);
		
		String[] oldcode = new String[]{"600101","600102",
				"22210102","22210101","22210104","222107","222108","222102","222103","222104",
				"222109","22210102","22210104" };
		Map<String,String> codemap = zxkjPlatformService.getNewCodeMap(oldcode, DZFConstant.ACCOUNTCODERULE, newrule);
		
		if(periods!=null && periods.length>0){
			for(String period : periods){
				ZzSyYsDBVO vo0 = new ZzSyYsDBVO() ;		
				if(corpVO!=null){
					// 公司名称
					vo0.setDw(corpVO.getUnitname());
					// 税务代码
					vo0.setSwdm(taxvo.getTaxcode());
					// 税率
					vo0.setSl(corpVO.getOwnersharerate());
					// （1）货物收入=dffs（600101）
					DZFDouble hwsr = getBQFSEByKm(codemap.get("600101"), pk_corp, period, 1);
					vo0.setWhsr(hwsr);
					// （2）劳务收入=dffs（600102）
					DZFDouble lwsr = getBQFSEByKm(codemap.get("600102"), pk_corp, period, 1);
					vo0.setLwsr(lwsr);
					// 主营业务收入=(1)+(2)
					vo0.setZysr(vo0.getWhsr().add(vo0.getLwsr()));
					// 其他业务收入=dffs（6051）
					DZFDouble qtywsr = getBQFSEByKm("6051", pk_corp, period, 1);
					vo0.setQtywsr(qtywsr);
					// 营业税金及其他=jffs（6403）
					DZFDouble yysjjqt = getBQFSEByKm("6403", pk_corp, period, 0);
					vo0.setYysjjfj(yysjjqt);
					// 主税=主营收入*税率
					vo0.setZs(vo0.getZysr().multiply(vo0.getSl()));
					// 附加=主税*0.6%
					vo0.setFj(vo0.getZs().multiply(0.006));
					// 合计=主税+附加
					vo0.setHj(vo0.getZs().add(vo0.getFj()));
					
					vo0.setInsum(vo0.getZysr().add(qtywsr));// 收入合计
					
					DZFDouble zztax1 = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("22210102"),monthmap); // 增值税期末余额
					DZFDouble zztax2 = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("22210101"),monthmap);
					DZFDouble zztax3 = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("22210104"),monthmap);
					DZFDouble zztax = zztax1.sub(zztax2).add(zztax3);
					vo0.setZztax(zztax);
					
					DZFDouble spendtax = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222108"),monthmap);// 消费税期末余额
					spendtax = spendtax== null?DZFDouble.ZERO_DBL:spendtax;
					vo0.setSpendtax(spendtax);
					
					DZFDouble csmaintax = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222102"),monthmap);//  城市维护建设税期末余额
					csmaintax = csmaintax== null?DZFDouble.ZERO_DBL:csmaintax;
					vo0.setCsmaintax(csmaintax);
					
					DZFDouble studytax =  getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222103"),monthmap);// 教育费附加期末余额
					studytax = studytax== null?DZFDouble.ZERO_DBL:studytax;
					vo0.setStudytax(studytax);
					
					DZFDouble partstudytax =   getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222104"),monthmap);// 地方教育费附加期末余额
					partstudytax = partstudytax== null?DZFDouble.ZERO_DBL:partstudytax;
					vo0.setPartstudytax(partstudytax);
					
					
					DZFDouble taxsum = zztax.add(spendtax).add(csmaintax).add(studytax).add(partstudytax);// 税金合计
					vo0.setTaxsum(taxsum);
					
					vo0.setPeriod(period);//期间
					
					listzzsy.add(vo0);
				}else{
					throw new BusinessException("公司主键："+pk_corp+"，在系统中不存在") ;
					
				}
			}
		}
		return listzzsy.toArray(new ZzSyYsDBVO[0]);
	}
	
	private ZzSyYsDBVO[] getZZSYYSDB2013VOs(String[] periods , String pk_corp) throws DZFWarpException{
		Map<String, List<FseJyeVO>> monthmap  = null; 
		if(periods!=null && periods.length>0){
			String year = periods[0].substring(0, 4);
			Object[] obj = gl_rep_fsyebserv.getYearFsJyeVOs(year, pk_corp,null,"zzsyys");
			monthmap = (Map<String, List<FseJyeVO>>) obj[0];
		}else{
			throw new BusinessException("查询区间为空");
		}

		String newrule = zxkjPlatformService.queryAccountRule(pk_corp);
		
		String[] oldcode = new String[]{ "500101","500102","500101",
				"22210102","22210101","22210104","222107","222108","222102","222103","222104",
				"222109","22210102","22210104" };
		Map<String,String> codemap = zxkjPlatformService.getNewCodeMap(oldcode, DZFConstant.ACCOUNTCODERULE, newrule);
		
		List<ZzSyYsDBVO> reslist = new ArrayList<ZzSyYsDBVO>();
		
		List<KmZzVO> reshwzzvos = getBQFSEByKmPeriod(codemap.get("500101"), pk_corp, periods[0].substring(0, 4));//货物收入
		Map<String,KmZzVO> reshwmap = 	changeMap(reshwzzvos);
		
		List<KmZzVO> reslwvos = getBQFSEByKmPeriod(codemap.get("500102"), pk_corp, periods[0].substring(0, 4));//劳务收入
		Map<String,KmZzVO> reslwmap = 	changeMap(reslwvos);
		
		List<KmZzVO> qtywvos =  getBQFSEByKmPeriod("5051", pk_corp, periods[0].substring(0, 4));//其他业务收入
		Map<String,KmZzVO> resqtmap = 	changeMap(qtywvos);
		
		List<KmZzVO> yysjjqtvos = getBQFSEByKmPeriod("5403", pk_corp, periods[0].substring(0, 4));//营业税金及其他
		Map<String,KmZzVO> resjjmap = 	changeMap(yysjjqtvos);
		
		CorpVO corpVO = (CorpVO)singleObjectBO.queryVOByID(pk_corp, CorpVO.class);;
		CorpTaxVo taxvo = zxkjPlatformService.queryCorpTaxVO(pk_corp);
		for(String period:periods){
			if (corpVO != null) {
				ZzSyYsDBVO vo0 = new ZzSyYsDBVO() ;		

				// 公司名称
				vo0.setDw(corpVO.getUnitname());

				// 税务代码
				vo0.setSwdm(taxvo.getTaxcode());
				// 税率
				vo0.setSl(corpVO.getOwnersharerate());

				// （1）货物收入=dffs（500101）
				DZFDouble hwsr =getBQFSEByKmMap(codemap.get("500101"), pk_corp, period, 1, reshwmap); //getBQFSEByKmMap("500101", pk_corp, period, 1,);// getBQFSEByKm("500101", pk_corp, period, 1);
				vo0.setWhsr(hwsr);

				// （2）劳务收入=dffs（500102）
				DZFDouble lwsr =getBQFSEByKmMap(codemap.get("500101"), pk_corp, period, 1, reslwmap);// getBQFSEByKm("500102", pk_corp, period, 1);
				vo0.setLwsr(lwsr);

				// 主营业务收入=(1)+(2)
				vo0.setZysr(vo0.getWhsr().add(vo0.getLwsr()));

				// 其他业务收入=dffs（5051）
				DZFDouble qtywsr =getBQFSEByKmMap("500101", pk_corp, period, 1, resqtmap); // getBQFSEByKm("5051", pk_corp, period, 1);
				vo0.setQtywsr(qtywsr);
				// 营业税金及其他=jffs（5403）
				DZFDouble yysjjqt =getBQFSEByKmMap("500101", pk_corp, period, 1, resjjmap); // getBQFSEByKm("5403", pk_corp, period, 0);
				vo0.setYysjjfj(yysjjqt);
				// 主税=主营收入*税率
				vo0.setZs(vo0.getZysr().multiply(vo0.getSl()));
				// 附加=主税*0.6%
				vo0.setFj(vo0.getZs().multiply(0.006));
				// 合计=主税+附加
				vo0.setHj(vo0.getZs().add(vo0.getFj()));

				vo0.setInsum(vo0.getZysr().add(qtywsr));// 收入合计
				
				

				DZFDouble zztax1 = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("22210102"),monthmap); //getBQFSEByKm("222109", pk_corp, period, 1);// 增值税期末余额
				DZFDouble zztax2 = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("22210101"),monthmap);
				DZFDouble zztax3 = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("22210104"),monthmap);
				DZFDouble zztax = zztax1.sub(zztax2).add(zztax3);
				vo0.setZztax(zztax);

//				DZFDouble busitax =  getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222107"),monthmap);// getBQFSEByKm("222107", pk_corp, period, 1);// 增值税期末余额
//				busitax = busitax== null?DZFDouble.ZERO_DBL:busitax;
//				vo0.setBusitax(busitax);

				DZFDouble spendtax = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222108"),monthmap);//  getBQFSEByKm("222108", pk_corp, period, 1);// 消费税期末余额
				spendtax = spendtax== null?DZFDouble.ZERO_DBL:spendtax;
				vo0.setSpendtax(spendtax);

				DZFDouble csmaintax = getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222102"),monthmap);// getBQFSEByKm("540303", pk_corp, period, 1);// 城市维护建设税期末余额
				csmaintax = csmaintax== null?DZFDouble.ZERO_DBL:csmaintax;
				vo0.setCsmaintax(csmaintax);

				DZFDouble studytax =  getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222103"),monthmap);// getBQFSEByKm("222103", pk_corp, period, 1);// 教育费附加期末余额
				studytax = studytax== null?DZFDouble.ZERO_DBL:studytax;
				vo0.setStudytax(studytax);

				DZFDouble partstudytax =   getQmyeForPeriod(codemap,period,pk_corp,codemap.get("222104"),monthmap);// getBQFSEByKm("222104", pk_corp, period, 1);// 地方教育费附加期末余额
				partstudytax = partstudytax== null?DZFDouble.ZERO_DBL:partstudytax;
				vo0.setPartstudytax(partstudytax);

				DZFDouble taxsum = zztax/*.add(busitax)*/.add(spendtax).add(csmaintax).add(studytax).add(partstudytax);// 税金合计
				vo0.setTaxsum(taxsum);

				vo0.setPeriod(period);//期间
				
				reslist.add(vo0);

			}else{
				throw new BusinessException("公司主键："+pk_corp+"，在系统中不存在") ;
			}
		}
		
		return reslist.toArray(new ZzSyYsDBVO[0]);
	}
	
	
	
	private Map<String, KmZzVO> changeMap(List<KmZzVO> vos){
		Map<String, KmZzVO> resmap = new HashMap<String, KmZzVO>();
		for(KmZzVO zzvo:vos){
			resmap.put(zzvo.getPeriod(), zzvo);
		}
		return resmap;
	}
	
	
	/**
	 * 取某个期间的期末余额
	 * @return
	 * @throws BusinessException
	 */
	private DZFDouble getQmyeForPeriod(Map<String,String> codemap, String period ,String pk_corp ,String kmbm,Map<String, List<FseJyeVO>> monthmap)throws DZFWarpException{
		
		DZFDouble mnyvalue = DZFDouble.ZERO_DBL;
		
		List<FseJyeVO> listfs = monthmap.get(period);
		
		if(listfs == null || listfs.size() ==0){
			return mnyvalue;
		}
		for(FseJyeVO fsvo:listfs){
			if(fsvo.getKmbm().equals(kmbm)){
				if(codemap.get("222109").equals(kmbm) || (codemap.get("222107")).equals(kmbm)  
						|| (codemap.get("22210102")).equals(kmbm) 
						|| (codemap.get("22210104")).equals(kmbm)
						|| (codemap.get("222108")).equals(kmbm)  || (codemap.get("222102")).equals(kmbm)
						|| (codemap.get("222103")).equals(kmbm) || (codemap.get("222104")).equals(kmbm) ){
					mnyvalue = fsvo.getEndfsdf();//贷方
				}else if( (codemap.get("22210101")).equals(kmbm) ){
					mnyvalue = fsvo.getEndfsjf();//贷方
				}else{
					//mnyvalue =  fsvo.getQmjf();//借方
				}
			}
		}
		mnyvalue = mnyvalue == null ?DZFDouble.ZERO_DBL:mnyvalue;
		
		return mnyvalue;
		
	}

	/**
	 * 一个公司对应多个期间
	 */
	@Override
	public ZzSyYsDBVO[] getZZSYYSDBVOsForPeriod(String[] periods, String pk_corp) throws DZFWarpException {
		if (!StringUtil.isEmptyWithTrim(pk_corp)) {
			// 单个公司
			Integer corpschema = zxkjPlatformService.getAccountSchema(pk_corp);
			if (corpschema == DzfUtil.SEVENSCHEMA.intValue()) {
				// 2007会计准则
				return getZZSYYSDB2007VOs(periods, pk_corp) ;
			} else if(corpschema == DzfUtil.THIRTEENSCHEMA.intValue()) {
				// 2013会计准则
				return getZZSYYSDB2013VOs(periods, pk_corp) ;
			}else{
				throw new BusinessException("该制度暂不支持利润表,敬请期待!");
			}
		}
		return new ZzSyYsDBVO[0];
	}
}

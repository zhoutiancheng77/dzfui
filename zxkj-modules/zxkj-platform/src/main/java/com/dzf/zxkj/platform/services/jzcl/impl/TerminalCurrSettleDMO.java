package com.dzf.zxkj.platform.services.jzcl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.jzcl.KMQMJZVO;
import com.dzf.zxkj.platform.model.jzcl.KmZzVO;
import com.dzf.zxkj.platform.model.jzcl.QmJzVO;
import com.dzf.zxkj.platform.model.qcset.FzhsqcVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * 期末结账后台处理，支持多币种
 * @author Administrator
 *
 */
@Slf4j
public class TerminalCurrSettleDMO {
	private SingleObjectBO singleObjectBO = null;

	public TerminalCurrSettleDMO(SingleObjectBO singleObjectBO) {
		super();
		this.singleObjectBO = singleObjectBO;
	}
	
	
	/**
	 * 先删除年结数据
	 * @param pk_corp
	 * @param kmcodes
	 */
	public void deleteTerminalSettleData(String pk_corp,String[] kmcodes){
		if (kmcodes == null || kmcodes.length == 0) {
			return;
		}
		// 查询当前科目的所有下级科目
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		StringBuffer wherepart = new StringBuffer();
		for (String str : kmcodes) {
			wherepart.append("or accountcode like '" + str + "%' ");
		}
		List<String> kmids = (List<String>) singleObjectBO
				.executeQuery("select pk_corp_account from ynt_cpaccount where nvl(dr,0)=0 and pk_corp = ? and "
						+ wherepart.toString().substring(2), sp, new ColumnListProcessor());

		if (kmids == null || kmids.size() == 0) {
			return;
		}
		
		//删除年结数据
		KMQMJZVO[] kmqmjzvos = (KMQMJZVO[]) singleObjectBO.queryByCondition(KMQMJZVO.class, "nvl(dr,0)=0 and pk_corp = ? and "+ SqlUtil.buildSqlForIn("pk_accsubj", kmids.toArray(new String[0])), sp);
		
		if (kmqmjzvos != null && kmqmjzvos.length > 0){
			singleObjectBO.deleteVOArray(kmqmjzvos);
		}
	}

	/**
	 * 单个科目的年结，反年结数据
	 * @param pk_corp
	 * @param logdate
	 * @param userid
	 * @param kmids 科目id
	 * @throws DZFWarpException
	 */
	public void saveSomeKmTerminalSettleDataFromPZ(String pk_corp, DZFDate logdate, String userid, String[] kmids)
			throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		// 根据公司查询已经年结数据
		sp.clearParams();
		sp.addParam(pk_corp);
		QmJzVO[] jzvos = (QmJzVO[]) singleObjectBO.queryByCondition(QmJzVO.class,
				"nvl(dr,0)=0 and nvl(jzfinish,'N')='Y' and pk_corp =? order by period ", sp);
		if (jzvos != null && jzvos.length > 0) {
			saveTerminalSettleDataFromPZ(jzvos, logdate, userid, Arrays.asList(kmids));
		}
	}
	
	/**
	 * 结账按照凭证进行结账---同时按照年进行结账
	 */
    public QmJzVO[] saveTerminalSettleDataFromPZ(QmJzVO[] vos,DZFDate logdate,String userid,List<String> kmids) throws DZFWarpException {
		
		if(vos==null||vos.length<1){
			throw new BusinessException("没有需要结账检查的数据") ;
		}
		//查询公司科目 以公司作为key
		HashMap<String, YntCpaccountVO[]> map = queryCorpAccountVOS(vos,kmids);
		
		String pk_corp = vos[0].getPk_corp();
		
		String periodYear = null;
		
		String period = null;
		//公司科目，以科目id为key
		Map<String,YntCpaccountVO> kmmap=null;
		//期末结账数据
		HashMap<String, KMQMJZVO> mapKmqmjz = null;
		//当前公司数据
		HashMap<String, KmZzVO> mapKmzzCurrent =null;
		
		HashMap<String, FzhsqcVO> mapQcye = null;
		
		Set<String> sumpk_cpacount = null;
		
		KMQMJZVO dbKmqmVO = null;
		
		for(QmJzVO qmjzvo:vos){
			
			 pk_corp = qmjzvo.getPk_corp();
			 
			 period = qmjzvo.getPeriod();
			 
			 periodYear = qmjzvo.getPeriod().substring(0, 4);
		
			 //更新结账标识
			 String pk_qmjz = updateQmjzVo(pk_corp, periodYear, qmjzvo);
			 
			 //查找当前所有科目
			 kmmap = queryCorpKmMap(map, pk_corp);
			
			//查询科目结账数据
			 mapKmqmjz = queryKmqmjzVOFromPz(pk_corp,period);
			
			//获取结账当年的发生
			 mapKmzzCurrent = queryKmzzVO(pk_corp, periodYear + "-01", periodYear + "-12",kmids);//queryKmzzVO2FromPz(qmjzvo,qjDate);//从这个凭证开始循环写km总账表
			
			//科目期初
			mapQcye = queryKmqcyeVOFromPz(qmjzvo,kmids);
			
			//获取结账科目PK
			sumpk_cpacount = getJzPks(kmmap, mapKmqmjz, mapKmzzCurrent, mapQcye);
			
			//生成期末结账数据
			dbKmqmVO = genKmqmJzVo(userid, map, kmmap, mapKmqmjz, mapKmzzCurrent, mapQcye, sumpk_cpacount, dbKmqmVO,
					qmjzvo, pk_qmjz);
		   
		}
		return vos ;
	}

    /**
     * 生成科目期末结账数据
     * @param userid
     * @param map
     * @param kmmap
     * @param mapKmqmjz
     * @param mapKmzzCurrent
     * @param mapQcye
     * @param sumpk_cpacount
     * @param dbKmqmVO
     * @param qmjzvo
     * @param pk_qmjz
     * @return
     */
	private KMQMJZVO genKmqmJzVo(String userid, HashMap<String, YntCpaccountVO[]> map,
			Map<String, YntCpaccountVO> kmmap, HashMap<String, KMQMJZVO> mapKmqmjz,
			HashMap<String, KmZzVO> mapKmzzCurrent, HashMap<String, FzhsqcVO> mapQcye, Set<String> sumpk_cpacount,
			KMQMJZVO dbKmqmVO, QmJzVO qmjzvo, String pk_qmjz) {
		KMQMJZVO nextKmqmjzVO = null;
		//期间,为下一期间
		DZFDate nextDate =null;
		String nextPeriod = null;
		String pk_account = null;
		String pk_currency = null;
		YntCpaccountVO corpaccvo = null;
		DZFDouble jf = DZFDouble.ZERO_DBL;
		DZFDouble df = DZFDouble.ZERO_DBL;
		DZFDouble ybjf = DZFDouble.ZERO_DBL;
		DZFDouble ybdf = DZFDouble.ZERO_DBL;
		DZFDouble ye = DZFDouble.ZERO_DBL;
		DZFDouble ybye = DZFDouble.ZERO_DBL;
		DZFDouble thismonthqc = null;
		DZFDouble ybthismonthqc = null;
		KmZzVO kmzzVO = null;
		if (sumpk_cpacount.size() > 0) {
			for (String key : sumpk_cpacount) {
				//取科目信息
		    	 pk_account = key.substring(0, 24);
		    	 pk_currency = key.substring(24, 48);
		    	 corpaccvo = kmmap.get(pk_account);
		    	
		    	if(corpaccvo == null){
		    		continue;
		    	}
		    	
		    	Integer fx = corpaccvo.getDirection();
		    	
		    	 dbKmqmVO = mapKmqmjz.get(key) ;
		    	
		    	if(dbKmqmVO != null){
					//已经存在期初数，只需要写入期末数、及本期累计发生
					dbKmqmVO.setPk_qmjz(pk_qmjz) ;//记录期末结账的PK
					
					 jf = DZFDouble.ZERO_DBL ;
					 df = DZFDouble.ZERO_DBL ;
					 ybjf = DZFDouble.ZERO_DBL ;
					 ybdf = DZFDouble.ZERO_DBL ;
					
					if(mapKmzzCurrent!=null && mapKmzzCurrent.size()>0){
						 kmzzVO = (KmZzVO)mapKmzzCurrent.get(key);
						if(kmzzVO != null){
							//本期本科目累计借方发生额
							jf = kmzzVO.getJf()==null?DZFDouble.ZERO_DBL:kmzzVO.getJf() ;
							//本期本科目累计贷方发生额
							df = kmzzVO.getDf()==null?DZFDouble.ZERO_DBL:kmzzVO.getDf();
							//本期本科目累计借方发生额
							ybjf = kmzzVO.getYbjf()==null?DZFDouble.ZERO_DBL:kmzzVO.getYbjf() ;
							//本期本科目累计贷方发生额
							ybdf = kmzzVO.getYbdf()==null?DZFDouble.ZERO_DBL:kmzzVO.getYbdf();
						}
					}
					
					//本期借方发生累计
					dbKmqmVO.setJffse(jf) ;
					//本期贷方发生累计
					dbKmqmVO.setDffse(df) ;
					//本期借方发生累计
					dbKmqmVO.setYbjfmny(ybjf);
					//本期贷方发生累计
					dbKmqmVO.setYbdfmny(ybdf) ;
					//结账日期
					dbKmqmVO.setCoperatorid(userid) ;
					dbKmqmVO.setDoperatedate(qmjzvo.getDoperatedate()) ;
					//余额
					 ye = DZFDouble.ZERO_DBL ;
					 ybye =DZFDouble.ZERO_DBL ;
					 thismonthqc = dbKmqmVO.getThismonthqc()==null?DZFDouble.ZERO_DBL:dbKmqmVO.getThismonthqc() ;
					 ybthismonthqc = dbKmqmVO.getYbthismonthqc()==null?DZFDouble.ZERO_DBL:dbKmqmVO.getYbthismonthqc() ;
					
					if(0==fx.intValue()){
						//借方
						ye = jf.sub(df) ;	
						ybye = ybjf.sub(ybdf);
						dbKmqmVO.setThismonthqm(thismonthqc.add(ye)) ;
						dbKmqmVO.setYbthismonthqm(ybthismonthqc.add(ybye));
					}else{
						//贷方
						ye = df.sub(jf) ;	
						ybye = ybdf.sub(ybjf);
						dbKmqmVO.setThismonthqm(thismonthqc.add(ye)) ;
						dbKmqmVO.setYbthismonthqm(ybthismonthqc.add(ybye)) ;
					}
				}else{
					//本科目没有结过账的
					//科目的期初、期末表
					dbKmqmVO  = new KMQMJZVO() ;
					dbKmqmVO.setPk_qmjz(pk_qmjz) ;//记录期末结账的PK
					dbKmqmVO.setPk_corp(qmjzvo.getPk_corp()) ;
					dbKmqmVO.setPk_currency(pk_currency);
					//科目主键
					dbKmqmVO.setPk_accsubj(pk_account) ;
					//结账日期
					dbKmqmVO.setDoperatedate(qmjzvo.getDoperatedate()) ;
					//结账人
					dbKmqmVO.setCoperatorid(userid) ;
					//期间
					dbKmqmVO.setPeriod(qmjzvo.getPeriod());
					
					 jf = DZFDouble.ZERO_DBL ;
					 df = DZFDouble.ZERO_DBL ;
					 ybjf = DZFDouble.ZERO_DBL ;
					 ybdf = DZFDouble.ZERO_DBL ;
					if(map != null && map.size() > 0){
						 kmzzVO = (KmZzVO)mapKmzzCurrent.get(key) ;
						if(kmzzVO != null){
							//是否辅助核算
							if (key.length() > 48) {
								dbKmqmVO.setFzhsx1(kmzzVO.getFzhsx1());
								dbKmqmVO.setFzhsx2(kmzzVO.getFzhsx2());
								dbKmqmVO.setFzhsx3(kmzzVO.getFzhsx3());
								dbKmqmVO.setFzhsx4(kmzzVO.getFzhsx4());
								dbKmqmVO.setFzhsx5(kmzzVO.getFzhsx5());
								dbKmqmVO.setFzhsx6(kmzzVO.getFzhsx6());
								dbKmqmVO.setFzhsx7(kmzzVO.getFzhsx7());
								dbKmqmVO.setFzhsx8(kmzzVO.getFzhsx8());
								dbKmqmVO.setFzhsx9(kmzzVO.getFzhsx9());
								dbKmqmVO.setFzhsx10(kmzzVO.getFzhsx10());
							}
							//本期本科目累计借方发生额
							jf = kmzzVO.getJf()==null?DZFDouble.ZERO_DBL:kmzzVO.getJf() ;
							//本期本科目累计贷方发生额
							df = kmzzVO.getDf()==null?DZFDouble.ZERO_DBL:kmzzVO.getDf();
							//本期本科目累计借方发生额
							ybjf = kmzzVO.getYbjf()==null?DZFDouble.ZERO_DBL:kmzzVO.getYbjf() ;
							//本期本科目累计贷方发生额
							ybdf = kmzzVO.getYbdf()==null?DZFDouble.ZERO_DBL:kmzzVO.getYbdf();
						}
					}
					//本期借方发生累计
					dbKmqmVO.setJffse(jf) ;
					dbKmqmVO.setYbjfmny(ybjf);
					//本期贷方发生累计
					dbKmqmVO.setDffse(df) ;
					dbKmqmVO.setYbdfmny(ybdf);
					
					//没有结果账，则查期初表，期初数
					FzhsqcVO qcyeVO = mapQcye.get(key);
					
					DZFDouble qcyeMny = DZFDouble.ZERO_DBL ;
					DZFDouble ybqcyeMny = DZFDouble.ZERO_DBL ;
					if(qcyeVO != null){
						dbKmqmVO.setFzhsx1(qcyeVO.getFzhsx1());
						dbKmqmVO.setFzhsx2(qcyeVO.getFzhsx2());
						dbKmqmVO.setFzhsx3(qcyeVO.getFzhsx3());
						dbKmqmVO.setFzhsx4(qcyeVO.getFzhsx4());
						dbKmqmVO.setFzhsx5(qcyeVO.getFzhsx5());
						dbKmqmVO.setFzhsx6(qcyeVO.getFzhsx6());
						dbKmqmVO.setFzhsx7(qcyeVO.getFzhsx7());
						dbKmqmVO.setFzhsx8(qcyeVO.getFzhsx8());
						dbKmqmVO.setFzhsx9(qcyeVO.getFzhsx9());
						dbKmqmVO.setFzhsx10(qcyeVO.getFzhsx10());
						qcyeMny = qcyeVO.getThismonthqc()==null?DZFDouble.ZERO_DBL:qcyeVO.getThismonthqc() ;
						ybqcyeMny = qcyeVO.getYbthismonthqc()==null?DZFDouble.ZERO_DBL:qcyeVO.getYbthismonthqc() ;
					}
					dbKmqmVO.setThismonthqc(qcyeMny) ;
					dbKmqmVO.setYbthismonthqc(ybqcyeMny);
					
					//期末数据
					 ye = DZFDouble.ZERO_DBL ;
					 ybye = DZFDouble.ZERO_DBL ;
					if(0==fx.intValue()){
						//借方期末余额=期初+借方-贷方
						ye = qcyeMny.add(jf).sub(df) ;	
						ybye = ybqcyeMny.add(ybjf).sub(ybdf) ;
						dbKmqmVO.setThismonthqm(ye) ;
						dbKmqmVO.setYbthismonthqm(ybye);
					}else{
						//贷方期末余额=期初+贷方—贷方
						ye = qcyeMny.add(df).sub(jf) ;		
						ybye =ybqcyeMny.add(ybdf).sub(ybjf) ;	
						dbKmqmVO.setThismonthqm(ye) ;
						dbKmqmVO.setYbthismonthqm(ybye);
					}
				}
				
				if(dbKmqmVO!=null){
					//保存本期期末数据
					if(StringUtil.isEmptyWithTrim(dbKmqmVO.getPrimaryKey())){
						//新增
						singleObjectBO.saveObject(dbKmqmVO.getPk_corp(), dbKmqmVO) ;
					}else{
						//修改
						singleObjectBO.update(dbKmqmVO) ;
					}
					
					//下一步，写下一期的期初
					 nextKmqmjzVO = (KMQMJZVO) dbKmqmVO.clone();
					//期间,为下一期间
					 nextDate = new DZFDate(qmjzvo.getPeriod() + "-01");
					 nextPeriod = nextDate.getYear() + 1 + "-12" ;
					nextKmqmjzVO.setPk_kmqmjz(null);
					nextKmqmjzVO.setJffse(null);
					nextKmqmjzVO.setYbjfmny(null);
					nextKmqmjzVO.setDffse(null);
					nextKmqmjzVO.setYbdfmny(null);
					nextKmqmjzVO.setThismonthqm(null);
					nextKmqmjzVO.setYbthismonthqm(null);
					nextKmqmjzVO.setPeriod(nextPeriod);
					//期初数= 上期的期末数
					nextKmqmjzVO.setThismonthqc(dbKmqmVO.getThismonthqm());
					nextKmqmjzVO.setYbthismonthqc(dbKmqmVO.getYbthismonthqm());
					//保存下一期的期初
					singleObjectBO.saveObject(nextKmqmjzVO.getPk_corp(), nextKmqmjzVO) ;
				}
			}
		}
		return dbKmqmVO;
	}

	private Set<String> getJzPks(Map<String, YntCpaccountVO> kmmap, HashMap<String, KMQMJZVO> mapKmqmjz,
			HashMap<String, KmZzVO> mapKmzzCurrent, HashMap<String, FzhsqcVO> mapQcye) {
		Set<String> sumpk_cpacount;
		Set<String> mapQcyeKey;
		YntCpaccountVO accountvo;
		sumpk_cpacount = new HashSet<String>();
		sumpk_cpacount.addAll(mapKmzzCurrent.keySet());
		
		//期初
		 mapQcyeKey = mapQcye.keySet();
		if (mapQcyeKey.size() > 0) {
			for (String key : mapQcyeKey) {
				 accountvo = kmmap.get(key.substring(0, 24));
				FzhsqcVO qcyevo = mapQcye.get(key);
				if(accountvo ==null){
					throw new BusinessException("未找到科目！");
				}
				if(accountvo.getIsleaf()!=null && accountvo.getIsleaf().booleanValue()){
					if(qcyevo.getThismonthqc()!=null && qcyevo.getThismonthqc().doubleValue()!=0){
						sumpk_cpacount.add(key);
					}
				}
			}
		}
		
		//没进行发生的科目
		sumpk_cpacount.addAll(mapKmqmjz.keySet());
		return sumpk_cpacount;
	}

	private Map<String, YntCpaccountVO> queryCorpKmMap(HashMap<String, YntCpaccountVO[]> map, String pk_corp) {
		YntCpaccountVO[] corpaccountvos;
		Map<String, YntCpaccountVO> kmmap;
		corpaccountvos =map.get(pk_corp);
		
		kmmap= new HashMap<String,YntCpaccountVO>();
		
		for(YntCpaccountVO corpaccountvo:corpaccountvos){
			kmmap.put(corpaccountvo.getPrimaryKey(), corpaccountvo);
		}
		return kmmap;
	}

	private String updateQmjzVo(String pk_corp,   String periodYear,   QmJzVO qmjzvo) {
		//已经结账了，不需要更新结账标识
		if(!StringUtil.isEmpty(qmjzvo.getPrimaryKey())
				&& qmjzvo.getJzfinish()!=null && qmjzvo.getJzfinish().booleanValue()){
			return qmjzvo.getPrimaryKey();
		}
		
		CorpVO currcorp = SpringUtils.getBean(ICorpService.class).queryByPk(pk_corp);
		//公司建账年份
		Integer currperiodYear = currcorp.getBegindate().getYear();
		//最近结账sql
		String jzsql ="select max(period) from ynt_qmjz where pk_corp =? and nvl(dr,0) = 0 and nvl(jzfinish,'N') ='Y'";
		SQLParameter jzsp=  new SQLParameter();
		if(!periodYear.equals(currperiodYear.toString())){
			//判断最大的结账日期
			jzsp.clearParams();
			jzsp.addParam(qmjzvo.getPk_corp());
			String resbig =  (String) singleObjectBO.executeQuery(jzsql, jzsp, new ColumnProcessor());
			if(resbig != null && resbig.length()>0){
				if(!resbig.substring(0, 4).equals(String.valueOf(Integer.parseInt(periodYear)-1))){
					throw new BusinessException("公司:"+currcorp.getUnitname()+"当前结账日期为："+ qmjzvo.getPeriod()+"，最近的结账日期为："+resbig+"不能跨年结账!");
				}
			}
			if(resbig == null || resbig.length() ==0){
				throw new BusinessException("公司:"+currcorp.getUnitname()+"当前结账日期为："+ qmjzvo.getPeriod()+"，最近的开账日期为："+currperiodYear+"年，不能跨年结账!");
			}
		}
		
		//插入结账表，勾选结账标识
		String pk_qmjz = "" ;
		if(StringUtil.isEmptyWithTrim(qmjzvo.getPrimaryKey())){
			qmjzvo.setJzfinish(DZFBoolean.TRUE) ;
			pk_qmjz= singleObjectBO.saveObject(qmjzvo.getPk_corp(), qmjzvo).getPrimaryKey();
		}else{
			pk_qmjz = qmjzvo.getPrimaryKey();
			qmjzvo.setJzfinish(DZFBoolean.TRUE) ;
			singleObjectBO.update(qmjzvo) ;
		}
		return pk_qmjz;
	}
    
	/**
	 * 查询公司科目
	 */
	private HashMap<String, YntCpaccountVO[]> queryCorpAccountVOS(QmJzVO[] vos,List<String> kmids) throws DZFWarpException {
		HashMap<String, YntCpaccountVO[]> map = new HashMap<String, YntCpaccountVO[]>();
		YntCpaccountVO[] kmVOs = null;
		String wherepart = "";
		if(kmids!=null && kmids.size()>0){
			wherepart = " and "+ SqlUtil.buildSqlForIn("pk_corp_account", kmids.toArray(new String[0]));
		}
		for (QmJzVO qmjzvo : vos) {
			if (map.containsKey(qmjzvo.getPk_corp())) {
				continue;
			}
			kmVOs = (YntCpaccountVO[]) singleObjectBO.queryByCondition(YntCpaccountVO.class,
					" pk_corp='" + qmjzvo.getPk_corp() + "' and nvl(dr,0)=0 "+wherepart+"order by accountcode asc ",
					new SQLParameter());
			map.put(qmjzvo.getPk_corp(), kmVOs);
		}
		return map;
	}
	
	/**
	 * 查科目期末
	 * @param qmjzvo
	 * @return
	 * @throws BusinessException
	 */
	private HashMap<String, KMQMJZVO>  queryKmqmjzVOFromPz(String pk_corp,String period) throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		HashMap<String, KMQMJZVO> map = new HashMap<String, KMQMJZVO>();
		StringBuffer sb = new StringBuffer();
		sb.append(" pk_corp=? ");
		sb.append(" and period=? ");
		sb.append(" and nvl(dr,0)=0 ");
		KMQMJZVO[] dbKmqmVOs = (KMQMJZVO[])singleObjectBO.queryByCondition(KMQMJZVO.class, sb.toString(),sp) ;
		if(dbKmqmVOs != null && dbKmqmVOs.length > 0){
			for(KMQMJZVO dbKmqmVO : dbKmqmVOs){
				map.put(getFzKey(dbKmqmVO), dbKmqmVO);
			}
		}
		return map;
	}
	

	/**
	 * 查询期初
	 * @param qmjzvo
	 * @return
	 * @throws BusinessException
	 */
	private HashMap<String, FzhsqcVO> queryKmqcyeVOFromPz(QmJzVO qmjzvo,List<String> kmids) throws DZFWarpException{
		SQLParameter sp = new SQLParameter();
		sp.addParam(qmjzvo.getPk_corp());
		HashMap<String, FzhsqcVO> map = new HashMap<String, FzhsqcVO>();
		StringBuffer sql = new StringBuffer();
		sql.append(" select * from YNT_QCYE qc ");
		sql.append(" left join YNT_CPACCOUNT acc on (qc.pk_accsubj = acc.pk_corp_account and qc.pk_corp = acc.pk_corp) ");
		sql.append(" where qc.pk_corp= ? and nvl(qc.dr,0) = 0   ");
		if (kmids != null && kmids.size() > 0) {
			sql.append(" and  " + SqlUtil.buildSqlForIn("qc.pk_accsubj", kmids.toArray(new String[0])));
		}
		List<FzhsqcVO> qcyeVOs = (List<FzhsqcVO>) singleObjectBO.executeQuery(sql.toString(), sp, new BeanListProcessor(FzhsqcVO.class));
		//辅助核算期初
		StringBuffer fzhssql = new StringBuffer();
		fzhssql.append(" pk_corp= ? and nvl(dr,0)=0 ");
		if (kmids != null && kmids.size() > 0) {
			fzhssql.append(" and  " + SqlUtil.buildSqlForIn("pk_accsubj", kmids.toArray(new String[0])));
		}
		FzhsqcVO[] fzqcVOs = (FzhsqcVO[])singleObjectBO.queryByCondition(FzhsqcVO.class,fzhssql.toString() , sp);
		if(qcyeVOs != null && qcyeVOs.size() > 0){
			for(FzhsqcVO qcyevo : qcyeVOs){
				if(isNotQcData(fzqcVOs,qcyevo)){
					map.put(qcyevo.getPk_accsubj()+qcyevo.getPk_currency(), qcyevo);
				}
			}
		}
		if (fzqcVOs != null && fzqcVOs.length > 0) {
			for (FzhsqcVO fzqcVO : fzqcVOs) {
				map.put(getFzKey(fzqcVO), fzqcVO);
			}
		}
		
		return map;
	}
	/**
	 * 存在，有科目挂辅助核算，但是期初挂在科目上，没在辅助中的
	 * @param fzqcVOs
	 * @param qcyevo
	 * @return
	 */
	private boolean isNotQcData(FzhsqcVO[] fzqcVOs, FzhsqcVO qcyevo) {
//		if(!StringUtil.isEmpty(qcyevo.getIsfzhs())
//				&& !qcyevo.getIsfzhs().equals("0000000000") ){
			if(fzqcVOs == null || fzqcVOs.length ==0){
				return true;
			}
			for(FzhsqcVO vo:fzqcVOs){
				if(vo.getPk_accsubj().equals(qcyevo.getPk_accsubj())){
					return false;//说明存在该科目
				}
			}
			return true;
//		}
//		
//		return false;
	}

	/**
	 * 查询科目发生额
	 * @param pk_corp
	 * @param bDate
	 * @param eDate
	 * @return
	 * @throws BusinessException
	 */
	@SuppressWarnings("unchecked")
	private HashMap<String, KmZzVO> queryKmzzVO(String pk_corp,String bPeriod, String ePeriod,List<String> kmids) throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(bPeriod);
		sp.addParam(ePeriod);
		sp.addParam(pk_corp);
		StringBuffer sql = new StringBuffer();
		sql.append(" select b.jfmny as jf,b.dfmny as df,b.ybdfmny as ybdf,b.ybjfmny as ybjf, b.pk_accsubj,b.pk_currency," );
		sql.append(" b.fzhsx1 as fzhsx1, b.fzhsx2 as fzhsx2,b.fzhsx3 as fzhsx3,b.fzhsx4 as fzhsx4,b.fzhsx5 as fzhsx5," );
		sql.append("case when b.fzhsx6 is null then b.pk_inventory else b.fzhsx6 end fzhsx6,");
		sql.append(" b.fzhsx7 as fzhsx7,b.fzhsx8 as fzhsx8,b.fzhsx9 as fzhsx9,b.fzhsx10 as fzhsx10 " );
		sql.append(" from ynt_tzpz_b b inner join ynt_tzpz_h h on b.pk_tzpz_h=h.pk_tzpz_h " );
		sql.append(" where  ( h.period between ? and  ? )" );
		sql.append(" and h.pk_corp=? and h.pk_corp=b.pk_corp and nvl(b.dr,0)=0 " );
		if(kmids!=null && kmids.size()>0){
			sql.append(" and  "+SqlUtil.buildSqlForIn("b.pk_accsubj", kmids.toArray(new String[0])));
		}
		
		List<KmZzVO> result = (List<KmZzVO>)singleObjectBO.executeQuery(sql.toString(), sp,new BeanListProcessor(KmZzVO.class)) ;
		HashMap<String, KmZzVO> map = new HashMap<String, KmZzVO>();
		if(result != null && result.size() > 0){
			for (KmZzVO vo : result) {
				String fzKey = getFzKey(vo);//包含币种主键和辅助核算主键
//				String cuKey = vo.getPk_accsubj() + vo.getPk_currency();//包含币种主键
				calMny(fzKey, map, vo);
				//是否有辅助核算项
//				if (fzKey.equals(cuKey)) {
//					calMny(fzKey, map, vo);
//				} else {
//					calMny(fzKey, map, vo);
//					calMny(cuKey, map, vo);
//				}
			}
		}
		return map;
	}
	/**
	 * 计算凭证金额
	 * @param key
	 * @param map
	 * @param vo
	 */
	private void calMny (String key, HashMap<String, KmZzVO> map, KmZzVO vo) {
		KmZzVO zzVO = map.get(key);
		if (zzVO == null) {
			map.put(key, vo);
		} else {
			zzVO.setDf(SafeCompute.add(zzVO.getDf(), vo.getDf()));
			zzVO.setJf(SafeCompute.add(zzVO.getJf(), vo.getJf()));
			zzVO.setYbdf(SafeCompute.add(zzVO.getYbdf(), vo.getYbdf()));
			zzVO.setYbjf(SafeCompute.add(zzVO.getYbjf(), vo.getYbjf()));
		}
	}
	/**
	 * 获取科目+币种+辅助核算项组成的key
	 * @param obj
	 * @return
	 */
	private String getFzKey (SuperVO obj) {
		StringBuffer key = new StringBuffer();
		String pk_accsubj = (String) obj.getAttributeValue("pk_accsubj");
		String pk_currency =  obj.getAttributeValue("pk_currency") == null ? IGlobalConstants.RMB_currency_id:(String)obj.getAttributeValue("pk_currency");
		key.append(pk_accsubj);
		key.append(pk_currency);
		//10个辅助核算项
		for (int i = 1; i <= 10; i++) {
			String fzhs = (String) obj.getAttributeValue("fzhsx" + i);
			if (!StringUtil.isEmpty(fzhs)) {
				key.append(fzhs);
			}
		}
		return key.toString();
	}
	
}

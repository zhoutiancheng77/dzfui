package com.dzf.zxkj.platform.service.sys.impl;

import com.alicp.jetcache.anno.CacheInvalidate;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.IParameterConstants;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.ObjectUtils;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.dao.ParameterSetDao;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service("sys_parameteract")
public class ParameterSetServiceImpl implements IParameterSetService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICorpService corpService;

	@Autowired
	private ParameterSetDao parameterSetDao;

	@Autowired
	private IAccountService accountService;

	@Override
	@CacheInvalidate(name = "zxkj:parameter-set", key = "#pk_corp")
	public void saveParamter(String pk_corp, YntParameterSet vo)throws DZFWarpException {
		check(pk_corp,vo);
		boolean isexists = isExists(pk_corp,vo.getParameterbm());
		if(isexists){//判断如果存在
			checkParam(pk_corp, vo);
			singleObjectBO.update(vo, new String[]{"pardetailvalue"});//更新明细值
		}else{
			checkParam(pk_corp, vo);
			vo.setPk_parameter(null);
			vo.setPk_corp(pk_corp);//将登录公司赋值
			singleObjectBO.saveObject(pk_corp, vo);
		}
	}
	
	private void checkParam(String pk_corp, YntParameterSet vo) {

		if (IParameterConstants.DZF009.equals(vo.getParameterbm())
				|| IParameterConstants.DZF010.equals(vo.getParameterbm())) {
			YntParameterSet oldvo = (YntParameterSet) queryParamterbyCode(pk_corp, vo.getParameterbm());
			if (oldvo != null) {
				if (oldvo.getPardetailvalue() != null && vo.getPardetailvalue() != null) {
					if (oldvo.getPardetailvalue().intValue() > vo.getPardetailvalue().intValue()) {
						// 校验是否发生 如果已有发生 不允许调小精度值
						checkRef(pk_corp, "ynt_ictradein", "入库单", null);

						checkRef(pk_corp, "ynt_ictradeout", "出库单", null);

						checkRef(pk_corp, "ynt_icbalance", "库存期初", null);

						checkRef(pk_corp, "ynt_glicqc", "存货期初", null);

						List<String> list = getWherePart(pk_corp, 2);
						String wherePart  = null;
						if(list != null && list.size()>0){
							wherePart = SqlUtil.buildSqlForIn("pk_accsubj", list.toArray(new String[0]));
							checkRef(pk_corp, "YNT_TZPZ_B", "凭证", " and " + wherePart);
							checkRef(pk_corp, "ynt_fzhsqc", "科目辅助期初", " and " + wherePart);

							if(!StringUtil.isEmpty(wherePart)){
								wherePart = wherePart
										+ " and (nvl(bnqcnum,0) <> 0 or nvl(bnfsnum,0) <> 0 or nvl(bndffsnum,0) <> 0 or nvl(monthqmnum,0) <> 0 )";
							}
							
							checkRef(pk_corp, "ynt_qcye", "科目期初", " and " + wherePart);
						}
						checkRef(pk_corp, "ynt_vatincominvoice_b", "进项清单", null);
						checkRef(pk_corp, "ynt_vatsaleinvoice_b", "销项清单", null);

					}
				}
			}
		}
		
		if (IParameterConstants.DZF011.equals(vo.getParameterbm())) {
			YntParameterSet oldvo = (YntParameterSet) singleObjectBO.queryByPrimaryKey(YntParameterSet.class,
					vo.getPk_parameter());
			if (oldvo != null) {
				if (oldvo.getPardetailvalue() != null && vo.getPardetailvalue() != null) {
					if (oldvo.getPardetailvalue().intValue() > vo.getPardetailvalue().intValue()) {
						checkRef(pk_corp, "ynt_exrate", "汇率档案",null);
						List<String> list = getWherePart(pk_corp, 1);
						String wherePart = null;
						if(list != null && list.size()>0){
							wherePart = SqlUtil.buildSqlForIn("pk_accsubj", list.toArray(new String[0]));
							checkRef(pk_corp, "YNT_TZPZ_B", "凭证"," and " + wherePart);
							
							if(!StringUtil.isEmpty(wherePart)){
								wherePart = wherePart
										+ " and (nvl(ybyearjffse,0) <> 0 or nvl(ybyeardffse,0) <> 0 or nvl(ybyearqc,0) <> 0 or nvl(ybthismonthqc,0) <> 0 "
										+ " or nvl(thismonthqc, 0) <> 0 or nvl(yearjffse, 0) <> 0 or nvl(yearqc, 0) <> 0 or nvl(yeardffse, 0) <> 0 )";
							}
							checkRef(pk_corp, "ynt_qcye", "科目期初", " and " + wherePart);
						}
					}
				}
			}
		}
	}
	
	// 1 ------外币核算  2----数量核算
	private List<String> getWherePart(String pk_corp, int type) {
		YntCpaccountVO[] vos = accountService.queryByPk(pk_corp);
		List<String> list = new ArrayList<>();
		for (YntCpaccountVO vo : vos) {
			if (type == 1) {
				if (vo.getIswhhs() != null && vo.getIswhhs().booleanValue()) {
					list.add(vo.getPk_corp_account());
				}
				continue;
			}

			if (type == 2) {
				if (vo.getIsnum() != null && vo.getIsnum().booleanValue()) {
					list.add(vo.getPk_corp_account());
				}
				continue;
			}
		}
		return list;

	}
	
	private void checkRef(String pk_corp, String tablename, String errname,String wherePart ) {

		StringBuffer sf = new StringBuffer();
		sf.append("select count(1) x  from " + tablename + " where pk_corp=? and nvl(dr,0) = 0 ");
		if(!StringUtil.isEmpty(wherePart)){
			sf.append(wherePart);
		}
		SQLParameter param = new SQLParameter();
		param.addParam(pk_corp);
		BigDecimal codeNum = (BigDecimal) singleObjectBO.executeQuery(sf.toString(), param, new ColumnProcessor());
		boolean flag = new DZFDouble(codeNum).doubleValue() > 0 ? true : false;
		if (flag)
			throw new BusinessException(errname + "已存在发生数据,不能调小精度值!");
	}
	
	private void check(String pk_corp, YntParameterSet vo)throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			throw new BusinessException("公司为空！");
		if(vo == null)
			throw new BusinessException("保存数据为空！");
	}
	
	private boolean isExists(String pk_corp,String paramcode) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(paramcode);
		String sql = "select count(1) x  from ynt_parameter where  pk_corp = ? and parameterbm = ?  and nvl(dr,0) = 0 ";
		BigDecimal codeNum = (BigDecimal) singleObjectBO.executeQuery(sql, sp, new ColumnProcessor());
		return new DZFDouble(codeNum).doubleValue() > 0 ? true:false;
	}

	@Override
	public List<YntParameterSet> queryParamter(String pk_corp)throws DZFWarpException {
		List<YntParameterSet> ancevos = queryParamters(pk_corp,"");
		List<String> lista = querycorpsByorder(pk_corp);
		if(ancevos == null || ancevos.size() == 0 )
			return null;
		
		List<YntParameterSet> z1 = setGroup(ancevos,lista);
		return z1;
	}
	
	private List<YntParameterSet> setGroup(List<YntParameterSet> ancevos,List<String> lista){
		Map<String,List<YntParameterSet>> map = (Map<String,List<YntParameterSet>>)
				DZfcommonTools.hashlizeObject(ancevos, new String[]{"parameterbm"});
		Iterator<String> it = map.keySet().iterator();
		List<YntParameterSet> z1 = new ArrayList<YntParameterSet>();
//		sortParameter sort = new sortParameter();
		while(it.hasNext()){
			String key = it.next();
			List<YntParameterSet> z = map.get(key);
			if(z!=null && z.size()>0){
				//排序，取pk_corp 最大的
//				Collections.sort(z,sort);
				YntParameterSet set = getYntParameterSet(z,lista);
				if(set!=null){
					z1.add(set);
				}
			}
		}
		return z1;
	}
	
	private YntParameterSet getYntParameterSet(List<YntParameterSet> z1,List<String> lista){
		if(z1 == null || z1.size() == 0){
			return null;
		}
		YntParameterSet set = null;
		for(String s :lista){
			for(YntParameterSet s1:z1){
				if(s.equals(s1.getPk_corp())){
					set = s1;
					break;
				}
			}
			if(set!=null){
				break;
			}
		}
		return set;
	}
	
	//倒序排序
//	class sortParameter implements Comparator<YntParameterSet>{
//		@Override
//		public int compare(YntParameterSet o1, YntParameterSet o2) {
//			int z = o1.getPk_corp().compareTo(o2.getPk_corp());
//			return z>0?-1:1;
//		}
//	}
	
	private List<String> querycorpsByorder(String pk_corp)throws DZFWarpException {
		String sql = " select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior fathercorp and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<String> ancevos = (List<String>)
				singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor(){
					
					@Override
					public Object handleResultSet(ResultSet resultset) throws SQLException {
						List<String> list = new ArrayList<String>();
						while(resultset.next()){
							list.add(resultset.getString("pk_corp"));
						}
						return list;
					}
					
				});
		return ancevos;
	}
	
	private List<YntParameterSet> queryParamters(String pk_corp, String parameterbm)throws DZFWarpException {

		List<YntParameterSet> parameterSetList = parameterSetDao.queryParamters(pk_corp);

		if(!StringUtil.isEmpty(parameterbm)){
			return ObjectUtils.notEmpty(parameterSetList) ? parameterSetList.stream().filter(v -> parameterbm.equalsIgnoreCase(v.getParameterbm())).collect(Collectors.toList()) : new ArrayList<YntParameterSet>();
		}
		return parameterSetList;
	}

	@Override
	public YntParameterSet queryParamterbyCode(String pk_corp, String paramcode) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(paramcode)){
			throw new BusinessException("参数为空，请确认参数!");
		}
		YntParameterSet set = null;
		List<YntParameterSet> ancevos = queryParamters(pk_corp,paramcode);
		List<String> lista = querycorpsByorder(pk_corp);
		if(ancevos == null || ancevos.size() == 0)
			return set;
		List<YntParameterSet> z1 = setGroup(ancevos,lista);
		if(z1 != null && z1.size() > 0){
			set = z1.get(0);
			set.setPk_corp(pk_corp);
			set.setPk_parameter(null);
		}
		return set;
	}
	
	@Override
	public String queryParamterValueByCode(String pk_corp, String paramcode) throws DZFWarpException{
		YntParameterSet setvo = queryParamterbyCode(pk_corp, paramcode);
		
		String value = null;
		if(setvo != null){
			String paravalue = setvo.getParametervalue();
			Integer detailvalue = setvo.getPardetailvalue();
			String[] arr = StringUtil.isEmpty(paravalue) ? null : paravalue.split(";");
			if(arr != null && arr.length > 0 && detailvalue != null){
				value = arr[detailvalue];
			}
		}
		
		return value;
	}

	@Override
	public YntParameterSet[] queryParamterbyCodes(String[] pk_corps, String paramcode) throws DZFWarpException {
		if(StringUtil.isEmpty(paramcode) || pk_corps == null || pk_corps.length == 0)
			throw new BusinessException("参数为空，请确认参数!");
		if(pk_corps.length > 100)
			throw new BusinessException("参数批量查询最多支持100家客户!");
		Map<String,List<String>> z1 = queryCorps(pk_corps);
		List<String> list1 = mergeList(z1);
		Map<String,YntParameterSet> z2 = queryParameterMaps(list1.toArray(new String[0]), paramcode);
		int len = pk_corps.length;
		YntParameterSet[] z3 = new YntParameterSet[len];
		for(int i = 0 ;i < len;i++){
			List<String> list = z1.get(pk_corps[i]);
			if(list != null && list.size() > 0){
				for(String s : list){
					YntParameterSet set = z2.get(s);
					if(set != null){
						set.setPk_parameter(null);
						set.setPk_corp(pk_corps[i]);
						z3[i] = (YntParameterSet)set.clone();
						break;
					}
				}
			}
		}
		return z3;
	}
	
	private List<String> mergeList(Map<String,List<String>> z1) {
		List<String> list = new ArrayList<String>();
		if(z1!=null && z1.size()>0){
			Collection<List<String>> c = z1.values();
			for(List<String> z : c){
				list.addAll(z);
			}
		}
		return list;
	}
	
	private Map<String,YntParameterSet> queryParameterMaps(String[] corps, String paramcode)throws DZFWarpException {
		Map<String,YntParameterSet> map = new HashMap<String,YntParameterSet>();
		if(corps == null || corps.length == 0)
			return map;
		SQLParameter sp = new SQLParameter();
		sp.addParam(paramcode);
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_parameter where nvl(dr,0) = 0 and parameterbm = ? and pk_corp in ");
		sf.append(" ("+SqlUtil.buildSqlConditionForIn(corps)+") ");
		List<YntParameterSet> yps = (List<YntParameterSet>)
				singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(YntParameterSet.class));
		if(yps == null || yps.size() == 0)
			return map;
		for(YntParameterSet  s : yps)
			map.put(s.getPk_corp(), s);
		return map;
	}
	
	private Map<String,List<String>> queryCorps(String[] pk_corps)throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		sf.append(" select pk_corp,level from bd_corp start with pk_corp in ");
		sf.append(" ("+SqlUtil.buildSqlConditionForIn(pk_corps)+") ");
		sf.append(" connect by pk_corp = prior fathercorp  and nvl(dr, 0) = 0 ");
		List<Object[]> keys = (List<Object[]>)
				singleObjectBO.executeQuery(sf.toString(), null, new ArrayListProcessor());
		Map<String,List<String>> map = hashlize(keys);
		return map;
	}
	
	private Map<String,List<String>> hashlize(List<Object[]> keys){
		Map<String,List<String>> map = new HashMap<String,List<String>>();
		if(keys == null || keys.size() == 0)
			return map;
		List<String> list =  null;
		String key = null;
		int level = -1;
		for(Object[] k : keys){
			level = Integer.parseInt(k[1].toString());
			key = k[0].toString();
			if(level == 1){
				list = new ArrayList<String>();
				list.add(key);
				map.put(key, list);
			}else{
				list.add(key);
			}
		}
		return map;
	}
	
	class keylevel {
		private String pk_corp;
		private int level;
		public String getPk_corp() {
			return pk_corp;
		}
		public void setPk_corp(String pk_corp) {
			this.pk_corp = pk_corp;
		}
		public int getLevel() {
			return level;
		}
		public void setLevel(int level) {
			this.level = level;
		}
	}
}

package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.zncs.CategoryKeywordVO;
import com.dzf.zxkj.platform.service.zncs.IBaseCategoryService;
import com.dzf.zxkj.platform.service.zncs.ICategoryKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class CategoryKeywordServiceImpl implements ICategoryKeywordService {

	@Autowired
	SingleObjectBO singleObjectBO;
	@Autowired
	IBaseCategoryService baseCategoryService;
	
	public Map<String, String> queryKeyWordMap()throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		Map<String, String> returnMap=new HashMap<String, String>();
		sb.append("select pk_keyword,keyword from ynt_keyword where nvl(dr,0)=0 and pk_corp=?");
		sp.addParam(IDefaultValue.DefaultGroup);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(), obj[1].toString());
		}
		return returnMap;
	}


	@Override
	public List<CategoryKeywordVO> queryCateKey(String pk_corp) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_category_keyword where nvl(dr,0)=0 and pk_corp='000001' ");
		if(!StringUtils.isEmpty(pk_corp)){
			sb.append(" pk_corp = ?");
			sp.addParam(pk_corp);
		}
		List<CategoryKeywordVO> list=(List<CategoryKeywordVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CategoryKeywordVO.class));

		return list;
	}
	
	public List<CategoryKeywordVO> queryCateKeyByAll(Map<String, String> map,String pk_corp,String period) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from (select a.*, b.categorycode, d.tradecode from ynt_category_keyword a inner join ynt_basecategory b ");
		sb.append(" on a.pk_basecategory = b.pk_basecategory left outer join ynt_bd_trade d on a.pk_trade = d.pk_trade where nvl(a.dr, 0) = 0 and nvl(b.dr, 0) = 0 and nvl(a.useflag, 'N') = 'Y' and nvl(b.useflag, 'N') = 'Y' ");
		sb.append("  and (a.pk_corp = '000001' or a.pk_corp = ?) union all ");
		sb.append(" select a.*, b.categorycode, d.tradecode from ynt_category_keyword a inner join ynt_billcategory b on a.pk_category = b.pk_category left outer join ynt_bd_trade d on a.pk_trade = d.pk_trade ");
		sb.append("  where nvl(a.dr, 0) = 0 and nvl(b.dr, 0) = 0 and nvl(a.useflag, 'N') = 'Y' and a.pk_corp = ? and b.pk_corp = ? ");
		sb.append(" and b.period = ? and nvl(b.isaccount, 'N') = 'N') order by case when pk_trade is null then 0 else length(tradecode) end desc, priority ");
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<CategoryKeywordVO> list=(List<CategoryKeywordVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CategoryKeywordVO.class));
		for (CategoryKeywordVO categoryKeywordVO : list) {
			if(!StringUtil.isEmpty(categoryKeywordVO.getPk_keywords())){
				String[] pk_keywords = categoryKeywordVO.getPk_keywords().split(",");
//				String keyWord = queryVOByPks(pk_keywords);
				String keyWordName="";
				for(int i=0;i<pk_keywords.length;i++){
					keyWordName+=map.get(pk_keywords[i])+",";
				}
				keyWordName=keyWordName.substring(0, keyWordName.length()-1);
				categoryKeywordVO.setKeywordnames(keyWordName);
			}
		}
		
		return list;
	}
	
	public String buildSqlForNotIn(final String fieldname,
			final String[] fieldvalue) {
		StringBuffer sbSQL = new StringBuffer();
		sbSQL.append("(" + fieldname + " NOT IN ( ");
		int len = fieldvalue.length;
		// 循环写入条件
		for (int i = 0; i < len; i++) {
			if (fieldvalue[i] != null && fieldvalue[i].trim().length() > 0) {
				sbSQL.append("'").append(fieldvalue[i].toString()).append("'");
				// 单独处理 每个取值后面的",", 对于最后一个取值后面不能添加"," 并且兼容 oracle 的 IN 254 限制。每
				// 200 个 数据 or 一次。时也不能添加","
				if (i != (fieldvalue.length - 1)
						&& !(i > 0 && (i + 1) % 100 == 0)) {
					sbSQL.append(",");
				}
			} else {
				return null;
			}

			// 兼容 oracle 的 IN 254 限制。每 200 个 数据 or 一次。
			if (i > 0
					&& (i + 1) % 100 == 0
					&& i != (fieldvalue.length - 1)) {
				sbSQL.append(" ) OR ").append(fieldname).append(" NOT IN ( ");
			}
		}
		sbSQL.append(" )) ");

		return sbSQL.toString();
	}
}

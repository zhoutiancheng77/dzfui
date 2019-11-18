package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.platform.model.zncs.BaseCategoryVO;
import com.dzf.zxkj.platform.service.zncs.IBaseCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BaseCategoryServiceImpl implements IBaseCategoryService {

	@Autowired
	SingleObjectBO singleObjectBO ;
	@Override
	public List<BaseCategoryVO> queryBaseCategory(String pk_corp) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yb1.*,yb2.categoryname as parentname from ynt_basecategory yb1 left join ynt_basecategory yb2 on yb1.pk_parentbasecategory=yb2.pk_basecategory where nvl(yb1.useflag, 'N') = 'Y' and nvl(yb1.dr,0)=0 and yb1.pk_corp = '000001' and (yb1.categorycode not like '1510%' and yb1.categorycode not like '1512%' or yb1.categorycode ='1510') ");
		if(!StringUtils.isEmpty(pk_corp)){
			sb.append(" or yb1.pk_corp = ? and nvl(yb1.useflag, 'N') = 'Y'");
			sp.addParam(pk_corp);
		}
		sb.append(" order by yb1.categorylevel");
		List<BaseCategoryVO> list=(List<BaseCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BaseCategoryVO.class));
		
		return list;
	}
	//固定资产科目体系专用, 事业单位和民间非盈利
	public List<BaseCategoryVO> queryBaseCategoryByGdzcNotNull(String pk_accountschema) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yb1.*,yb2.categoryname as parentname from ynt_basecategory yb1 left join ynt_basecategory yb2 on yb1.pk_parentbasecategory=yb2.pk_basecategory where nvl(yb1.useflag, 'N') = 'Y' and nvl(yb1.dr,0)=0 and yb1.categorycode like '1510%'  ");
		if(!StringUtils.isEmpty(pk_accountschema)){
			sb.append(" and yb1.pk_accountschema = ? ");
			sp.addParam(pk_accountschema);
		}
		sb.append(" order by yb1.categorylevel");
		List<BaseCategoryVO> list=(List<BaseCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BaseCategoryVO.class));
		
		return list;
	}
	//固定资产科目体系通用
	public List<BaseCategoryVO> queryBaseCategoryByGdzcIsNull() throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yb1.*,yb2.categoryname as parentname from ynt_basecategory yb1 left join ynt_basecategory yb2 on yb1.pk_parentbasecategory=yb2.pk_basecategory where nvl(yb1.useflag, 'N') = 'Y' and nvl(yb1.dr,0)=0 and (yb1.categorycode like '1510%' or yb1.categorycode like '1512%') and yb1.categorycode != '1510' ");
		sb.append(" and yb1.pk_accountschema is null ");
		sb.append(" order by yb1.categorylevel");
		List<BaseCategoryVO> list=(List<BaseCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BaseCategoryVO.class));
		
		return list;
	}
	
	@Override
	public BaseCategoryVO queryVOById(String pk_basecategory) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select yb1.*,yb2.categoryname as parentname from ynt_basecategory yb1 left join ynt_basecategory yb2 on yb1.pk_parentbasecategory=yb2.pk_basecategory where nvl(yb1.dr,0)=0 and pk_basecategory = ? ");
		sp.addParam(pk_basecategory);
		List<BaseCategoryVO> list=(List<BaseCategoryVO>)singleObjectBO.executeQuery(sb.toString(), null, new BeanListProcessor(BaseCategoryVO.class));
		return list.get(0);
	}
	@Override
	public String queryCategoryName(String pk_billcategory) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select ba.catalogname from ynt_billcategory bi left join ynt_basecategory ba "
				+ " on bi.pk_basecategory=ba.pk_basecategory where nvl(bi.dr,0)=0 and nvl(ba.dr,0)=0 and bi.pk_category=? ");
		sp.addParam(pk_billcategory);
		List<Object[]> list = (List<Object[]>) singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		return (String)list.get(0)[0];
	}
}

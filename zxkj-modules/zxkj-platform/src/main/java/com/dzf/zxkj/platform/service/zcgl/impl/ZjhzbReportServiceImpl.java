package com.dzf.zxkj.platform.service.zcgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.ZjhzbReportVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zcgl.IZjhzbReportSerice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("zjhzbRepSer")
public class ZjhzbReportServiceImpl implements IZjhzbReportSerice {

	private SingleObjectBO singleObjectBO = null;
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	private ICorpService corpService;

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public List<ZjhzbReportVO> queryZjhzb(QueryParamVO vo)
			throws DZFWarpException {
		
		StringBuffer qrysql = new StringBuffer();
		if(vo.getBegindate1()==null){
			throw new BusinessException("查询失败:开始日期不能为空!");
		}
		
		if(vo.getBegindate1()!=null && vo.getEnddate()!=null){
			if(vo.getBegindate1().after(vo.getEnddate())){
				throw new BusinessException("查询开始日期，应在结束日期之前!");
			}
		}
		
		String zxlb = vo.getPk_assetcategory();
		String zcsx = vo.getZcsx();
		Integer lbjc_start = vo.getLevelq();//类别级次开始
		Integer lbjc_end = vo.getLevelz();//结束
		SQLParameter sp = new SQLParameter();
		/**
		 * assetmny 资产原值  depreciation 累计折旧
		 * assetnetvalue 资产净值 originalvalue 本期折旧 assetproperty 资产属性
		 * pk_assetcategory 资产类别主键
		 * 
		 * assetmny 资产原值 depreciationmny 累计折旧
		 * assetnetmny 资产净值 originalvalue 本期折旧 assetproperty 资产属性
		 * pk_assetcategory 资产类别主键
		 * ynt_category资产类别表
		 */
		//qrysql.append("select sum(b.assetmny) as assetmny, sum(b.depreciation) as depreciation, sum(b.assetnetvalue) as assetnetvalue, ");
		qrysql.append("select sum(b.assetmny) as assetmny, sum(a.depreciationmny) as depreciationmny, sum(a.assetnetmny) as assetnetmny, ");
		qrysql.append(" sum(a.originalvalue) as originalvalue, ");
		qrysql.append(" d.assetproperty,d.catecode,d.catename ");
		qrysql.append(" from ynt_depreciation a  left join ynt_assetcard b on a.pk_assetcard = b.pk_assetcard  ");
		qrysql.append(" left join ynt_category d on b.assetcategory=d.pk_assetcategory where ");
		
		
		if(DzfUtil.POPULARSCHEMA.intValue() == getAccountSchema(vo.getPk_corp()) || DzfUtil.CAUSESCHEMA.intValue() == getAccountSchema(vo.getPk_corp())){
			qrysql.append(" a.pk_corp = ? and nvl(a.dr,0)=0  and nvl(b.dr,0)=0 and ( d.catecode like'03%'  or  d.catecode like'02%' ) ");
		}else{
			qrysql.append(" a.pk_corp = ? and nvl(a.dr,0)=0  and nvl(b.dr,0)=0  and (d.catecode like'01%' or  d.catecode like'02%' or  d.catecode like'04%' ) ");
		}
		
		qrysql.append(" and a.businessdate >=? ");
		
		sp.addParam(vo.getPk_corp());
		sp.addParam(vo.getBegindate1());
		if(vo.getEnddate()!=null){
			qrysql.append(" and a.businessdate <=? ");
			sp.addParam(vo.getEnddate());
		}
		
		if(!StringUtil.isEmpty(zxlb)){
			qrysql.append("  and b.assetcategory = ?");
			sp.addParam(zxlb);
		}
		if(!StringUtil.isEmpty(zcsx)){
			if(!"2".equals(zcsx)){//2位显示全部
				qrysql.append("  and d.assetproperty = ?");
				sp.addParam(zcsx);
			}
			
		}
		//类别级次
		//通过代码过滤
		/*if(lbjc_start != null && lbjc_end != null ){
			qrysql.append(" and d.catelevel between ? and ? ");
			sp.addParam(lbjc_start);
			sp.addParam((lbjc_end));
		}*/
		
		qrysql.append(" group by d.assetproperty,d.catecode,d.catename  order by d.assetproperty, d.catecode ");//排序
		
		/*qrysql.append("select * from ynt_category ");*/
	  	List<ZjhzbReportVO> zjhzbList = (List<ZjhzbReportVO>) singleObjectBO.executeQuery(qrysql.toString(), sp, new BeanListProcessor(ZjhzbReportVO.class));
	  	//ZjhzbReportVO[]  zczjarray= result1.toArray(new ZjhzbReportVO[0]);
	  	
	  	/*List<String> codeList_old = new ArrayList<String>();
	  	codeList_old = getCateCode(zjhzbList);
	  	Boolean flag = true;
	  	int size = zjhzbList.size();
	  	for(int i=0 ; i <size  ; i++){
	  		//每条数据循环，没有此数据的二级类别，则需要调用searSuperLeave()方法
	  		if(zjhzbList.get(i).getCatecode().length() > 4){
	  			for(int j=0 ; j < size ; j++){
	  				if(zjhzbList.get(i).getCatecode().substring(0, 4).equals(zjhzbList.get(j).getCatecode())){
		  				flag = true;
		  				break;
		  			}else{
		  				flag = false;
		  				if(i == 0){
		  					zjhzbList = searSuperLeave(zjhzbList,codeList_old);
		  					i = i+2 > size ? size : i+2;
		  					flag = true;
		  				}
		  			}
	  			}
	  		}
	  		if(!flag){//第 i 条数据没有二级类别
	  			zjhzbList = searSuperLeave(zjhzbList,codeList_old);
	  			i = i+1 > size ? size : i+1;
	  		}
	  	}
	  	*/
	  	
	  	
	  	zjhzbList = searSuperLeave(zjhzbList,vo);
	  	List<ZjhzbReportVO> hzbList = new ArrayList<ZjhzbReportVO>();
	  	
	  	for(int i=0 ; i<zjhzbList.size() ; i++){
	  		if(lbjc_start != null && lbjc_end != null ){
				if(zjhzbList.get(i).getCatecode().length()/2 >= lbjc_start
						&& zjhzbList.get(i).getCatecode().length()/2 <= lbjc_end){
					hzbList.add(zjhzbList.get(i));
				}
			}
	  	}
	  	
	  	return hzbList;
	}
	/**
	 * 递归算出每一类别级次的上级类别
	 * 
	*/
	@SuppressWarnings("unchecked")
	private List<ZjhzbReportVO> searSuperLeave(List<ZjhzbReportVO> zList,QueryParamVO vo){//,List<String> codeList_old
		//先查询出有数据的资产类别，根据有数据的资产类别查出其上级资产类别，
		List<String> codeList = new ArrayList<String>();
		codeList = getCateCode(zList);
		
		//先查询出每个级别的上级类别
		for(int i=0 ; i<zList.size() ; i++){
			//类别级次小于2，则不必查询上级
			if(zList.get(i).getCatecode().length()>4){
				if(codeList.contains(zList.get(i).getCatecode().substring(0, zList.get(i).getCatecode().length()-2))){
					continue;
				}else{
  					ZjhzbReportVO newVo = new ZjhzbReportVO();
  					newVo = searchLb(zList.get(i).getCatecode().substring(0, zList.get(i).getCatecode().length()-2),vo.getPk_corp());
  					zList.add(i,newVo);
  					if(i == 0){
  						i = -1;
  					}else{
  						i = i-1 <= 0 ? 0 : i-1;
  					}
  					//System.out.println(zList.get(i));;
  					codeList = getCateCode(zList);
  				}
			}
		}
		
		//每一层级的数据往上汇总
		for(int i=0 ; i<zList.size() ; i++){
			int length = zList.get(i).getCatecode().length();
			for(int j=i+1 ; j<zList.size() ; j++){
				if(zList.get(i).getCatecode().length()<=zList.get(j).getCatecode().length() &&
						zList.get(i).getCatecode().equals(zList.get(j).getCatecode().substring(0, length))){
					DZFDouble Assetmny = addCal(zList.get(i).getAssetmny(), zList.get(j).getAssetmny());
  					zList.get(i).setAssetmny(String.valueOf(Assetmny));//资产原值
  					
  					DZFDouble Originalvalue =addCal(zList.get(i).getOriginalvalue(), zList.get(j).getOriginalvalue());
  					zList.get(i).setOriginalvalue(String.valueOf(Originalvalue));//本期折旧额
  					
  					DZFDouble Depreciation =addCal(zList.get(i).getDepreciationmny(), zList.get(j).getDepreciationmny()); 
  					zList.get(i).setDepreciationmny(String.valueOf(Depreciation));//累计折旧额
  					
  					DZFDouble Assetnetmny =addCal(zList.get(i).getAssetnetmny(), zList.get(j).getAssetnetmny());
  					zList.get(i).setAssetnetmny(String.valueOf(Assetnetmny));//资产净值
				}else{
					break;
				}
			}
			
		}
		
//	  	for(int i=0 ; i<zList.size() ; i++){
//	  		//类别级次大于2，则不必查询上级
//	  		if(zList.get(i).getCatecode().length()>4){
//	  			if(codeList.contains(zList.get(i).getCatecode().substring(0, zList.get(i).getCatecode().length()-2))){
//	  				if(!codeList_old.contains(zList.get(i).getCatecode().substring(0, zList.get(i).getCatecode().length()-2))){
//	  					continue;
//	  				}else{
//	  					for(int j = (i-1) < 0 ? 0 :i-1; j >= 0 ; j--){
//			  				//往上循环查询，如果有比i级更上一级的数据，对比类别编码，是一样则吧数据加入，不一样则把i级的上级数据插入
//			  				if( zList.get(j).getCatecode().length() == zList.get(i).getCatecode().length()-2 
//			  						&& zList.get(i).getCatecode().substring(0, zList.get(i).getCatecode().length()-2).
//			  							equals(zList.get(j).getCatecode()) && i != 0){//如果已经存在其上级类别，则直接增加数据
//			  					int Assetmny = Integer.valueOf(zList.get(i).getAssetmny() == null ? "0":zList.get(i).getAssetmny()) + Integer.valueOf(zList.get(j).getAssetmny() == null ? "0":zList.get(j).getAssetmny());
//			  					zList.get(j).setAssetmny(String.valueOf(Assetmny));//资产原值
//			  					
//			  					int Originalvalue = Integer.valueOf(zList.get(i).getOriginalvalue() == null ? "0":zList.get(i).getOriginalvalue()) + Integer.valueOf(zList.get(j).getOriginalvalue() == null ? "0":zList.get(j).getOriginalvalue());
//			  					zList.get(j).setOriginalvalue(String.valueOf(Originalvalue));//本期折旧额
//			  					
//			  					int Depreciation = Integer.valueOf(zList.get(i).getDepreciationmny() == null ? "0":zList.get(i).getDepreciationmny()) + Integer.valueOf(zList.get(j).getDepreciationmny() == null ? "0":zList.get(j).getDepreciationmny());
//			  					zList.get(j).setDepreciationmny(String.valueOf(Depreciation));//累计折旧额
//			  					
//			  					int Assetnetmny = Integer.valueOf(zList.get(i).getAssetnetmny()) + Integer.valueOf(zList.get(j).getAssetnetmny());
//			  					zList.get(j).setAssetnetmny(String.valueOf(Assetnetmny));//资产净值
//			  				}
//		  				}
//	  				}
//		  			
//	  			}else{
//  					ZjhzbReportVO newVo = new ZjhzbReportVO();
//  					newVo = searchLb(zList.get(i).getCatecode().substring(0, zList.get(i).getCatecode().length()-2));
//  					newVo.setAssetmny(zList.get(i).getAssetmny());//资产原值
//  					newVo.setOriginalvalue(zList.get(i).getOriginalvalue());//本期折旧额
//  					newVo.setDepreciationmny(zList.get(i).getDepreciationmny());//累计折旧额
//  					newVo.setAssetnetmny(zList.get(i).getAssetnetmny());//资产净值
//  					
//  					zList.add(i,newVo);
//  					i = i-1 <= 0 ? 0 : i-1;
//  					codeList = getCateCode(zList);
//  					//break;
//  				}
//	  		}
//	  		
//	  	}
	  	return zList;
	}
	
	public DZFDouble addCal(String s1,String s2){
		return SafeCompute.add(getdzfDouble(s1), getdzfDouble(s2));
	}
	
	public DZFDouble getdzfDouble(String str){
		if(StringUtil.isEmpty(str)){
			return DZFDouble.ZERO_DBL;
		}
		return  new DZFDouble(str);
	}
	
	@SuppressWarnings("rawtypes")
	private List getCateCode(List<ZjhzbReportVO> zList){
		List<String> codeList = new ArrayList<String>();
		for(int i=0 ; i < zList.size() ; i++){
			codeList.add(zList.get(i).getCatecode());
		}
		return codeList;
	}
	
	/**
	 * 根据类别编码查询资产类别
	*/
	private ZjhzbReportVO searchLb(String code,String pk_corp){
		
		StringBuffer sql = new StringBuffer();
	  	SQLParameter spBysql = new SQLParameter();
	  	sql.append("select * from ynt_category where (pk_corp = ? or pk_corp = ?) "); 
	  	spBysql.addParam(pk_corp);
	  	spBysql.addParam("000001");
	    spBysql.addParam(code);
		sql.append(" and catecode = ?");
		
		List<ZjhzbReportVO> zjhzL = (List<ZjhzbReportVO>) singleObjectBO.executeQuery(sql.toString(), spBysql, new BeanListProcessor(ZjhzbReportVO.class));
		
		return zjhzL.get(0);
	}
		
	/**
	 * 判断所使用的科目方案
	 * 
	 * @return
	 */
	public Integer getAccountSchema(String pk_corp) throws DZFWarpException {
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		if (corpVO != null) {
			if (!StringUtil.isEmptyWithTrim(corpVO.getCorptype())) {
				BdtradeAccountSchemaVO schemaVO = (BdtradeAccountSchemaVO) singleObjectBO.queryVOByID(corpVO.getCorptype(), BdtradeAccountSchemaVO.class);
				if (schemaVO != null) {
					return schemaVO.getAccountstandard();
				}
			}
		}
		return -1;
	}

}

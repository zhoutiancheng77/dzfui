package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.zncs.IBillcategory;
import com.dzf.zxkj.platform.service.zncs.IEditDirectory;
import com.dzf.zxkj.platform.util.zncs.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class EditDirectoryImpl implements IEditDirectory {
	@Autowired
	SingleObjectBO singleObjectBO ;
	@Autowired
	private IBillcategory iBillcategory;
	@Override
	public List<AuxiliaryAccountHVO> queryAuxiliaryAccountHVOs(String pk_corp) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_fzhs_h where nvl(dr,0)=0 and (pk_corp=? or pk_corp=?) order by code");
		sp.addParam(pk_corp);
		sp.addParam(IDefaultValue.DefaultGroup);
		List<AuxiliaryAccountHVO> list=(List<AuxiliaryAccountHVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AuxiliaryAccountHVO.class));
		return list;
	}

	@Override
	public List<AuxiliaryAccountBVO> queryAuxiliaryAccountBVOs(String pk_corp, String pk_head) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_fzhs_b where nvl(dr,0)=0 and pk_corp=? and pk_auacount_h=? order by code");
		sp.addParam(pk_corp);
		sp.addParam(pk_head);
		List<AuxiliaryAccountBVO> list=(List<AuxiliaryAccountBVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AuxiliaryAccountBVO.class));
		return list;
	}
	private void checkCategoryName(String pk_corp,String pk_parentcategory,String pk_category,String categoryname)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and pk_parentcategory=? and pk_category!=? and categoryname=?");
		sp.addParam(pk_corp);
		sp.addParam(pk_parentcategory);
		sp.addParam(pk_category);
		sp.addParam(categoryname);
		List<BillCategoryVO> list=(List<BillCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillCategoryVO.class));
		if(list!=null&&list.size()>0){
			throw new BusinessException("分类名称重复");
		}
	}
	@Override
	public void saveAuxiliaryAccountVO(String pk_corp, CategorysetVO headVO, CategorysetBVO[] bodyVOs) throws DZFWarpException {
		//查base
		String pk_category=headVO.getPk_category();
		BillCategoryVO categoryVO=(BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_category);
		if (categoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_WT))
		{
			throw new BusinessException("问题票据不能编辑分类");
		}
		if (categoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_WSB))
		{
			throw new BusinessException("未识别票据不能编辑分类");
		}
		//保存主表
		headVO.setDr(0);
		headVO.setPk_corp(pk_corp);
		headVO.setPk_basecategory(StringUtil.isEmpty(categoryVO.getPk_basecategory())?null:categoryVO.getPk_basecategory());
		headVO=(CategorysetVO)singleObjectBO.saveObject(pk_corp, headVO);
		//更新分类名称
		if(categoryVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_1)||categoryVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_2)){
			//检查名称是否重复
			checkCategoryName(pk_corp, categoryVO.getPk_parentcategory(), pk_category,headVO.getCategoryname());
			if(categoryVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_1)&&categoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)){
				//要更新base
				categoryVO.setCategoryname(headVO.getCategoryname());
				singleObjectBO.update(categoryVO, new String[]{"categoryname"});
				BaseCategoryVO baseVO=(BaseCategoryVO)singleObjectBO.queryByPrimaryKey(BaseCategoryVO.class, categoryVO.getPk_basecategory());
				baseVO.setCategoryname(headVO.getCategoryname());
				singleObjectBO.update(baseVO, new String[]{"categoryname"});
			}else if(categoryVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_2)){
				categoryVO.setCategoryname(headVO.getCategoryname());
				singleObjectBO.update(categoryVO, new String[]{"categoryname"});
			}
		}
		//删除子表
		SQLParameter sp=new SQLParameter();
		sp.addParam(headVO.getPk_categoryset());
		singleObjectBO.executeUpdate("delete from ynt_categoryset_fzhs where pk_categoryset=?", sp);
		//插入新子表
		if(bodyVOs!=null&&bodyVOs.length!=0){
			for(int i=0;i<bodyVOs.length;i++){
				bodyVOs[i].setDr(0);
				bodyVOs[i].setPk_corp(pk_corp);
				bodyVOs[i].setPk_categoryset(headVO.getPk_categoryset());
				bodyVOs[i].setPk_categoryset_fzhs(null);
			}
			singleObjectBO.insertVOArr(pk_corp, bodyVOs);
			headVO.setChildren(bodyVOs);
		}
		//所有下级都改规则
		ArrayList<Object[]> childList=getChildKeys(pk_category, pk_corp);
		saveCopyParent(headVO, childList, pk_corp);
		//修改分类目录名称
		iBillcategory.updateCategoryName(headVO.getPk_category(), headVO.getCategoryname());
	}
	@Override
	public void saveCopyParent(CategorysetVO headVO,ArrayList<Object[]> childList,String pk_corp)throws DZFWarpException{
		for(int i=0;i<childList.size();i++){
			String pk_cate=childList.get(i)[0].toString();
			String pk_base=childList.get(i)[1]==null?null:childList.get(i)[1].toString();
			Integer categorytype=childList.get(i)[2]==null?0:Integer.parseInt(childList.get(i)[2].toString());
			CategorysetVO vo=queryCategorysetVO(pk_cate, pk_corp);
			if(vo==null||vo.getPk_corp()!=null&&!vo.getPk_corp().equals(pk_corp)){
				vo=new CategorysetVO();
			}
			vo.setDepreciationmonth(headVO.getDepreciationmonth());
			vo.setDr(0);
			vo.setMergemode(headVO.getMergemode());
			vo.setPk_basecategory(pk_base);
			vo.setPk_category(pk_cate);
			vo.setPk_corp(pk_corp);
			vo.setSalvagerate(headVO.getSalvagerate());
			vo.setSettlement(headVO.getSettlement());
			vo.setZdyzy(headVO.getZdyzy());
			if(categorytype==ZncsConst.CATEGORYTYPE_3||categorytype==ZncsConst.CATEGORYTYPE_4){
				vo.setPk_accsubj(headVO.getPk_accsubj());
				vo.setPk_settlementaccsubj(headVO.getPk_settlementaccsubj());
				vo.setPk_taxaccsubj(headVO.getPk_taxaccsubj());
			}
			CategorysetBVO[] BVOs=(CategorysetBVO[])headVO.getChildren();
			String[] fzhsKey = null;
			if(BVOs!=null&&BVOs.length>0){
				fzhsKey = new String[BVOs.length];
				for (int j = 0; j < BVOs.length; j++) {
					BVOs[j].setPk_categoryset(null);
					BVOs[j].setPk_categoryset_fzhs(null);
					fzhsKey[j]=BVOs[j].getPk_auacount_h();
				}
			}
			vo.setChildren(headVO.getChildren());
			//先删除子表
			SQLParameter sp=new SQLParameter();
			sp.addParam(vo.getPk_categoryset());
			if (fzhsKey != null && fzhsKey.length > 0)
			{
				singleObjectBO.executeUpdate("delete from ynt_categoryset_fzhs where pk_categoryset=? and "+ SqlUtil.buildSqlForIn("pk_auacount_h", fzhsKey), sp);
			}
			singleObjectBO.saveObject(pk_corp, vo);
		}
	}
	/**
	 * 找下级分类树主键
	 * @param pk_category
	 * @return
	 * @throws DZFWarpException 
	 */
	private ArrayList<Object[]> getChildKeys(String pk_category, String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,pk_basecategory,case when pk_parentcategory=? then categorytype else 0 end categorytype from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and pk_corp=? and pk_category!=? start with pk_category=? connect by prior pk_category=pk_parentcategory order by categorylevel");
		sp.addParam(pk_category);
		sp.addParam(pk_corp);
		sp.addParam(pk_category);
		sp.addParam(pk_category);
		ArrayList<Object[]> list=(ArrayList<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		return list;
	}
	@Override
	public CategorysetVO queryCategorysetVO(String pk_category, String pk_corp) throws DZFWarpException {
		CategorysetVO headVO=null;
		String sql="select * from ynt_categoryset where nvl(dr,0)=0 and pk_corp=? and pk_category=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_category);
		//当前期间的
		List<CategorysetVO> list=(List<CategorysetVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CategorysetVO.class));
		if(list!=null&&list.size()>0){
			headVO=list.get(0);
			sql="select ynt_categoryset_fzhs.* from ynt_categoryset_fzhs inner join ynt_fzhs_h on(ynt_categoryset_fzhs.pk_auacount_h=ynt_fzhs_h.pk_auacount_h) where nvl(ynt_categoryset_fzhs.dr,0)=0 and pk_categoryset=? order by ynt_fzhs_h.code";
			sp=new SQLParameter();
			sp.addParam(headVO.getPrimaryKey());
			List<CategorysetBVO> listb=(List<CategorysetBVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CategorysetBVO.class));
			headVO.setChildren(listb.toArray(new CategorysetBVO[0]));
		}else{
			//公司没有，看集团是否有
			sql="select * from ynt_categoryset where nvl(dr,0)=0 and pk_corp=? and pk_basecategory=(select pk_basecategory from ynt_billcategory where nvl(dr,0)=0 and pk_category=?)";
			sp=new SQLParameter();
			sp.addParam(IDefaultValue.DefaultGroup);
			sp.addParam(pk_category);
			list=(List<CategorysetVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CategorysetVO.class));
			if(list!=null&&list.size()>0){
				headVO=list.get(0);
				headVO.setPk_corp(pk_corp);
				headVO.setPk_categoryset(null);
				headVO.setPk_category(pk_category);
			}
		}
		if(headVO!=null){
			if(!StringUtil.isEmpty(headVO.getPk_basecategory())){
				headVO.setDefaultZy(queryDefaultZy(headVO.getPk_basecategory(), pk_corp));
			}
		}else{
			headVO=new CategorysetVO();
			headVO.setPk_category(pk_category);
			headVO.setSettlement(0);
			headVO.setMergemode(0);
			sql="select pk_basecategory from ynt_billcategory where nvl(dr,0)=0 and pk_category=?";
			sp=new SQLParameter();
			sp.addParam(pk_category);
			List<BillCategoryVO> list1=(List<BillCategoryVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BillCategoryVO.class));
			if(list1!=null&&list1.size()>0){
				headVO.setDefaultZy(queryDefaultZy(list1.get(0).getPk_basecategory(), pk_corp));
			}
		}
		return headVO;
	}

	/**
	 * 取分类入账规则的摘要
	 * @param pk_basecategory
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private String queryDefaultZy(String pk_basecategory,String pk_corp)throws DZFWarpException{
		CorpVO corp= SystemUtil.queryCorp(pk_corp);
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_accset where nvl(dr,0)=0 and pk_corp=? and pk_basecategory=? and pk_accountschema=? and (pk_trade=? or pk_trade is null) and nvl(useflag,'N')='Y' order by pk_trade");
		SQLParameter sp=new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(pk_basecategory);
		sp.addParam(corp.getCorptype());
		sp.addParam(corp.getIndustry());
		List<AccsetVO> accsetVOList=(List<AccsetVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AccsetVO.class));
		if(accsetVOList!=null&&accsetVOList.size()>0){
			return accsetVOList.get(0).getZy();
		}else{
			return null;
		}
	}
}

package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.zncs.BaseCategoryVO;
import com.dzf.zxkj.platform.model.zncs.BillCategoryVO;
import com.dzf.zxkj.platform.model.zncs.BillcategoryQueryVO;
import com.dzf.zxkj.platform.model.zncs.CategorysetVO;
import com.dzf.zxkj.platform.service.zncs.IDirectory;
import com.dzf.zxkj.platform.service.zncs.IEditDirectory;
import com.dzf.zxkj.platform.service.zncs.IInterfaceBill;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class DirectoryImpl implements IDirectory {

	@Autowired
	SingleObjectBO singleObjectBO;
	@Autowired
	private IInterfaceBill iInterfaceBill;
	@Autowired
	private IEditDirectory iEditDirectory;
	@Override
	public void saveNewDirectory(String dirName, String pk_parent, String pk_corp, String period,String pk_user) throws DZFWarpException {
		if (StringUtil.isEmpty(dirName))
		{
			throw new BusinessException("分类名称不能为空!");
		}
		else
		{
			if (dirName.contains("~"))
			{
				throw new BusinessException("分类名称不能含有特殊字符");
			}
			else if (dirName.equals("其他") || dirName.equals("其他往来"))
			{
				throw new BusinessException("分类名称不适用，请修改");
			}
		}
		//这个是要创建的目录的爸爸
		BillCategoryVO parentVO=queryByID(pk_parent);
		if(parentVO==null|| DZFBoolean.TRUE.equals(parentVO.getIsaccount())||DZFBoolean.FALSE.equals(parentVO.getAllowchild())
				||parentVO.getChildlevel()<1||!parentVO.getPk_corp().equals(pk_corp)){
			throw new BusinessException("不允许在该分类下创建下级。");
		}
		//这些都是要创建的目录的兄弟
		BillCategoryVO[] childrenTreeVOs=queryChildCategoryVOs(pk_parent, DZFBoolean.FALSE);
		if(childrenTreeVOs!=null&&childrenTreeVOs.length>0){
			for (int i = 0; i < childrenTreeVOs.length; i++) {
				if(childrenTreeVOs[i].getCategoryname().equals(dirName)){
					throw new BusinessException("分类名称重复!");
				}
			}
			if (childrenTreeVOs.length >= 80)
			{
//				throw new BusinessException("当前层次新建分类数量达到上限");
			}
		}
		//目标基础分类
		BaseCategoryVO baseTreeVO=null;
		if(!StringUtil.isEmpty(parentVO.getPk_basecategory())){
			baseTreeVO=(BaseCategoryVO)singleObjectBO.queryByPrimaryKey(BaseCategoryVO.class, parentVO.getPk_basecategory());
		}
		BillCategoryVO curVO=insertCategoryVOByName(pk_corp, dirName, parentVO, childrenTreeVOs, period, baseTreeVO,pk_user);
		//复制上级的编辑目录属性
		ArrayList<Object[]> childList=new ArrayList<Object[]>();
		Object[] obj=new Object[3];
		obj[0]=curVO.getPk_category();
		obj[1]=curVO.getPk_basecategory();
		obj[2]=curVO.getCategorytype();
		childList.add(obj);
		CategorysetVO headVO=iEditDirectory.queryCategorysetVO(parentVO.getPk_category(), pk_corp);
		if(headVO!=null&& headVO.getPk_corp() != null && headVO.getPk_corp().equals(pk_corp)){
			iEditDirectory.saveCopyParent(headVO, childList, pk_corp);
		}
	}

	/**
	 * 检查目录是否存在
	 * @param dirName
	 * @param pk_parent
	 * @param pk_corp
	 * @param period
	 * @throws DZFWarpException
	 */
	private void checkSameDirName(String dirName, String pk_parent, String pk_corp, String period) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and pk_parentcategory=? and categoryname?");
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(pk_parent);
		sp.addParam(dirName);
		List<BillCategoryVO> list=(List<BillCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillCategoryVO.class));
		if(list!=null&&list.size()>0){
			throw new BusinessException("目录已经存在");
		}
	}
	private BillCategoryVO insertCategoryVOByName(String pk_corp,String name,BillCategoryVO treeVO,BillCategoryVO[] childrenTreeVOs,String period,BaseCategoryVO baseTreeVO,String pk_user)throws DZFWarpException{
		BaseCategoryVO baseVO=null;
		if(treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)){//自定义要显存base，在存公司的
			//看集团有没有，没有新增，有查出来
			BaseCategoryVO[] baseChildrenTreeVOs=queryBaseChildCategoryVOs(baseTreeVO.getPk_basecategory(),pk_corp);
			if(baseChildrenTreeVOs!=null&&baseChildrenTreeVOs.length>0){
				for (int i = 0; i < baseChildrenTreeVOs.length; i++) {
					if(baseChildrenTreeVOs[i].getCategoryname().equals(name)){
						baseVO=baseChildrenTreeVOs[i];
					}
				}
			}
			if(baseVO==null){
				baseVO=new BaseCategoryVO();
				baseVO.setAllowchild(treeVO.getAllowchild());
				baseVO.setCatalogname(name);
				if(baseChildrenTreeVOs != null && baseChildrenTreeVOs.length > 0){
					baseVO.setCategorycode(String.valueOf(Integer.parseInt(baseChildrenTreeVOs[baseChildrenTreeVOs.length-1].getCategorycode())+1));
				}else{
					baseVO.setCategorycode(treeVO.getCategorycode()+"01");
				}
				baseVO.setCategorylevel(treeVO.getCategorylevel()+1);
				baseVO.setCategoryname(name);
				baseVO.setCategorytype(ZncsConst.CATEGORYTYPE_1);
				baseVO.setChildlevel(treeVO.getChildlevel()-1);
				baseVO.setDr(0);
				baseVO.setIsleaf(DZFBoolean.TRUE);
				baseVO.setPk_corp(pk_corp);
				baseVO.setPk_parentbasecategory(baseTreeVO.getPk_basecategory());
				baseVO.setSettype(baseTreeVO.getSettype());
				if(baseChildrenTreeVOs!=null&&baseChildrenTreeVOs.length>0){
					baseVO.setShoworder(baseChildrenTreeVOs[baseChildrenTreeVOs.length-1].getShoworder()+1);
				}else{
					baseVO.setShoworder(1);
				}
				baseVO.setUseflag(DZFBoolean.TRUE);
				baseVO.setCenableid(pk_user);
				baseVO.setDenabledate(new DZFDate());
				baseVO.setCoperatorid(pk_user);
				baseVO.setDoperatedate(new DZFDate());
				baseVO.setInoutflag(treeVO.getInoutflag());
				baseVO=(BaseCategoryVO)singleObjectBO.insertVO(pk_corp, baseVO);
			}else{
				if(DZFBoolean.FALSE.equals(baseVO.getUseflag())){
					baseVO.setUseflag(DZFBoolean.TRUE);
					singleObjectBO.update(baseVO,new String[]{"useflag"});
				}
			}
		}
		BillCategoryVO VO=new BillCategoryVO();
		VO.setAllowchild(treeVO.getAllowchild());
		if (baseVO != null)
		{
			VO.setPk_basecategory(baseVO.getPrimaryKey());
			VO.setCategorycode(baseVO.getCategorycode());
			VO.setShoworder(baseVO.getShoworder());
		}
		else
		{
			if(childrenTreeVOs != null && childrenTreeVOs.length > 0){
				
				//寻找合适的编号和计算显示顺序
				Set<String> codeSet = new HashSet<String>();
				int maxshoworder = 0;
				for (BillCategoryVO childvo : childrenTreeVOs)
				{
					codeSet.add(childvo.getCategorycode());
					if (childvo.getShoworder() != null && childvo.getShoworder().intValue() > maxshoworder)
					{
						maxshoworder = childvo.getShoworder();
					}
				}
				String categorycode = childrenTreeVOs[0].getCategorycode().substring(0, childrenTreeVOs[0].getCategorycode().length() - 2);
				String thisCode = categorycode + "01";
				int i = 0;
				while (codeSet.contains(thisCode))
				{
					i++;
					thisCode = categorycode + (i < 10 ? "0" : "") + i;
				}
				if (i >= 99)
				{
					throw new BusinessException("当前层次新建分类数量达到上限");
				}
				
				VO.setCategorycode(thisCode);
				VO.setShoworder(maxshoworder + 1);
			}else{
				VO.setCategorycode(treeVO.getCategorycode()+"01");
				VO.setShoworder(1);
			}
		}
		VO.setCategorylevel(treeVO.getCategorylevel()+1);
		VO.setCategoryname(name);
		VO.setCategorytype(VO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)?ZncsConst.CATEGORYTYPE_1:ZncsConst.CATEGORYTYPE_2);
		VO.setChildlevel(treeVO.getChildlevel()-1);
		VO.setDr(0);
		VO.setIsaccount(DZFBoolean.FALSE);
		VO.setIsleaf(DZFBoolean.TRUE);
		VO.setPeriod(period);
		VO.setPk_corp(pk_corp);
		VO.setPk_parentcategory(treeVO.getPk_category());
		VO.setSettype(treeVO.getSettype());
		
		VO.setInoutflag(treeVO.getInoutflag());
		VO=(BillCategoryVO)singleObjectBO.insertVO(pk_corp, VO);
		treeVO.setIsleaf(DZFBoolean.FALSE);
		singleObjectBO.update(treeVO,new String[]{"isleaf"});
		return VO;
	}
	private BillCategoryVO queryByID(String pk_category)throws DZFWarpException{
		BillCategoryVO vo=(BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_category);
		return vo;
	}
	
	/**
	 * 查询公司树的下级树
	 * @param pk_tree
	 * @return
	 * @throws DZFWarpException
	 */
	private BillCategoryVO[] queryChildCategoryVOs(String pk_tree,DZFBoolean isaccout)throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_tree);
		sp.addParam(isaccout);
		BillCategoryVO[] vos=( BillCategoryVO[])singleObjectBO.queryByCondition(BillCategoryVO.class, "nvl(dr,0)=0 and pk_parentcategory=? and isaccount=? order by categorycode", sp);
		return vos;
	}
	/**
	 * 查询集团树的下级树
	 * @param pk_tree
	 * @return
	 * @throws DZFWarpException
	 */
	private BaseCategoryVO[] queryBaseChildCategoryVOs(String pk_tree,String pk_corp)throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_tree);
		sp.addParam(pk_corp);
		BaseCategoryVO[] vos=( BaseCategoryVO[])singleObjectBO.queryByCondition(BaseCategoryVO.class, "nvl(dr,0)=0 and pk_parentbasecategory=? and pk_corp=? order by categorycode", sp);
		return vos;
	}
	@Override
	public void deleteDirectory(String primaryKey, String pk_parent, String pk_corp, String period) throws DZFWarpException {
		// 这个自己
		BillCategoryVO selfVO = queryByID(primaryKey);
		if (selfVO == null || selfVO.getIsaccount() == DZFBoolean.TRUE || (selfVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_1)&&selfVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_2))||!selfVO.getPk_corp().equals(pk_corp)) {
			throw new BusinessException("不允许删除该目录。");
		}
		//作废票据
		BillcategoryQueryVO paraVO=new BillcategoryQueryVO();
		paraVO.setPeriod(period);
		paraVO.setPk_corp(pk_corp);
		paraVO.setPk_category(primaryKey);
		paraVO.setPk_parentcategory(pk_parent);
		iInterfaceBill.updateInvalidBatchBill(paraVO);
		// 删自己和所有的儿子 孙子
		deleteAllLowerLevel(primaryKey, pk_corp, period);
		//如果是自定义目录删除base
		if(selfVO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)){
			deleteAllBaseLowerLevel(selfVO.getPk_basecategory(), pk_corp);
		}
		BillCategoryVO[] childrenTreeVOs=queryChildCategoryVOs(selfVO.getPk_parentcategory(), DZFBoolean.FALSE);
		if(childrenTreeVOs==null||childrenTreeVOs.length==0){
			BillCategoryVO parentVO = queryByID(selfVO.getPk_parentcategory());
			parentVO.setIsleaf(DZFBoolean.TRUE);
			singleObjectBO.update(parentVO, new String[]{"isleaf"});
		}
	}
	private void deleteAllBaseLowerLevel(String pk_basecategory,String pk_corp){
//		检查这个base关联的公司分类(包括这个公司所有期间的分类)，如果挂着票，不管已制证还是未制证都不能删base
		//0、查所有下级ID,1、更新invoice2、删入账规则3、更新base//4、删自定义模板//5、还要删其他期间base关联的公司分类
		String sql1="select pk_category";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_basecategory);
		String sql="select pk_basecategory from ynt_basecategory where nvl(dr,0)=0 and pk_corp=? start with pk_basecategory=? connect by prior pk_basecategory=pk_parentbasecategory";
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		List<String> delList=new ArrayList<String>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				delList.add(list.get(i)[0].toString());
			}
		}
		if (delList.size() > 0)
		{
			sql="update ynt_interface_invoice set pk_category_keyword=null where nvl(dr,0)=0 and pk_corp=? and "
					+ "pk_category_keyword in(select pk_category_keyword from ynt_category_keyword where nvl(dr,0)=0 and pk_corp=? and  "+SqlUtil.buildSqlForIn("pk_basecategory", delList.toArray(new String[0]))+")";
			sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk_corp);
			singleObjectBO.executeUpdate(sql, sp);
			sql="delete from ynt_category_keyword where nvl(dr,0)=0 and pk_corp=? and  "+ SqlUtil.buildSqlForIn("pk_basecategory", delList.toArray(new String[0]));
			sp=new SQLParameter();
			sp.addParam(pk_corp);
			singleObjectBO.executeUpdate(sql, sp);
			
			sql="update ynt_basecategory set useflag = 'N' where nvl(dr,0)=0 and pk_corp=? and "+SqlUtil.buildSqlForIn("pk_basecategory", delList.toArray(new String[0]));
			singleObjectBO.executeUpdate(sql, sp);
			
//			sql="delete from ynt_vouchertemplet_h where nvl(dr,0)=0 and pk_corp=? and "+SqlUtil.buildSqlForIn("pk_basecategory", delList.toArray(new String[0]));
//			singleObjectBO.executeUpdate(sql, sp);
		}
	}
	private void deleteAllLowerLevel(String pk_category,String pk_corp, String period){
		String sql="delete from ynt_billcategory  where pk_category in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and nvl(isaccount,'N')='N' start with pk_category=? connect by prior pk_category=pk_parentcategory)";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(pk_category);
		singleObjectBO.executeUpdate(sql, sp);
	}
}

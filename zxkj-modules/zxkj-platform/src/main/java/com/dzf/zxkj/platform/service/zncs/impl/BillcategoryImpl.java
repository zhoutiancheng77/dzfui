package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.tree.AccTreeCreateStrategyByID;
import com.dzf.zxkj.common.tree.BDTreeCreator;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.zncs.IBillcategory;
import com.dzf.zxkj.platform.service.zncs.IParaSet;
import com.dzf.zxkj.platform.service.zncs.IPrebillService;
import com.dzf.zxkj.platform.service.zncs.ISchedulCategoryService;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.SystemUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class BillcategoryImpl implements IBillcategory {
	@Autowired
	SingleObjectBO singleObjectBO ;
	@Autowired
	IParaSet iParaSet;
	@Autowired
	IPrebillService iPrebillService;
	@Autowired
	ISchedulCategoryService iSchedulCategoryService;
	@Override
	public List<BillCategoryVO> queryCategoryTree(BillcategoryQueryVO paramVO) throws DZFWarpException {
		String categorycode=paramVO.getCategorycode();
		List<ParaSetVO> listParas=iParaSet.queryParaSet(paramVO.getPk_corp());
		DZFBoolean isyh=listParas.get(0).getBankbillbyacc();
		//1、如果点中的是银行票据分类且启用了银行账号参数
		if(!StringUtil.isEmpty(categorycode)&&categorycode.equals(ZncsConst.FLCODE_YHPJ)){
			if(DZFBoolean.TRUE.equals(isyh)){
				List<BillCategoryVO> list=queryBankGroup(paramVO);
				setBankCodeShowName(list);
				return sortList(list);
			}
		}
		//2、选中的是一个银行账号，取父主键下的分类，把账号做为条件取票据
		else if(!StringUtil.isEmpty(paramVO.getPk_category())&&paramVO.getPk_category().startsWith("bank_")){
			BillCategoryVO vo=queryByCode(ZncsConst.FLCODE_YHPJ, paramVO.getPk_corp(),paramVO.getBillstate(),paramVO.getPeriod());
			String pk_category=paramVO.getPk_category();
			paramVO.setPk_category(vo.getPk_category()); 
			paramVO.setIsBank(DZFBoolean.TRUE);
			List<BillCategoryVO> list=queryCategory(paramVO);
			for(int i=0;i<list.size();i++){
				if(list.get(i).getItype()==0){
					list.get(i).setPk_parentcategory(pk_category);
				}
			}
			return sortList(list);
		}
		if(DZFBoolean.TRUE.equals(isyh)&&!StringUtil.isEmpty(categorycode)&&categorycode.startsWith(ZncsConst.FLCODE_YHPJ)){
			paramVO.setIsBank(DZFBoolean.TRUE);
			paramVO.setCategorycode(paramVO.getPk_bankcode());//这里存的是bank_
		}
		//3、选中的是分类(包括普通分类和税率客户设置)
		return sortList(queryCategory(paramVO));
	}
	
	private void setBankCodeShowName(List<BillCategoryVO> list){
		 if(list!=null&&list.size()>0){
			 for (int i = 0; i < list.size(); i++) {
				 String categoryname=list.get(i).getCategoryname();
				if(categoryname!=null&&categoryname.indexOf("-")>-1){
					String[] names=categoryname.split("-");
					String name1=names[0]+"-";
					String name2=null;
					if(names[1].length()>4){
						name2=names[1].substring(names[1].length()-4,names[1].length());
					}else{
						name2=names[1];
					}
					list.get(i).setCategoryname(name1+name2);
				}
			}
		 }
	}
	/**
	 * 排个序
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<BillCategoryVO> sortList(List<BillCategoryVO> list)throws DZFWarpException{
		Collections.sort(list,new Comparator<Object>(){
			@Override
			public int compare(Object o1, Object o2) {
				BillCategoryVO vo1=(BillCategoryVO)o1;
				BillCategoryVO vo2=(BillCategoryVO)o2;
				int v1,v2;
				if(vo1.getBillcount()==null){
					v1=0;
				}else{
					v1=vo1.getBillcount();
				}
				if(vo2.getBillcount()==null){
					v2=0;
				}else{
					v2=vo2.getBillcount();
				}
				return v2-v1;
			}
		});
		//问题票据和未识别票据这两个分类下如果有票放到有票分类的最后
		List<BillCategoryVO> youpiaoList=new ArrayList<BillCategoryVO>();
		List<BillCategoryVO> meipiaoList=new ArrayList<BillCategoryVO>();
		List<BillCategoryVO> returnList=new ArrayList<BillCategoryVO>();
		BillCategoryVO wsb=null;
		BillCategoryVO wt=null;
		for(int i=0;i<list.size();i++){
			if(StringUtil.isEmpty(list.get(i).getCategorycode())){
				return list;
			}
			if(!list.get(i).getCategorycode().equals(ZncsConst.FLCODE_WSB)&&!list.get(i).getCategorycode().equals(ZncsConst.FLCODE_WT)){
				if(list.get(i).getBillcount()!=null&&list.get(i).getBillcount()>0){
					youpiaoList.add(list.get(i));
				}else{
					meipiaoList.add(list.get(i));
				}
			}else{
				if(list.get(i).getCategorycode().equals(ZncsConst.FLCODE_WSB)){
					wsb=list.get(i);
				}else{
					wt=list.get(i);
				}
			}
		}
		if(wsb==null&&wt==null){
			return list;
		}
		if(youpiaoList.size()==0){
			if(wsb!=null||wt!=null){
				if(wt.getBillcount()!=null&&wt.getBillcount()>0){
					returnList.add(wt);
				}
				if(wsb.getBillcount()!=null&&wsb.getBillcount()>0){
					returnList.add(wsb);
				}
				returnList.addAll(meipiaoList);
				if(wt.getBillcount()==null||wt.getBillcount()==0){
					returnList.add(wt);
				}
				if(wsb.getBillcount()==null||wsb.getBillcount()==0){
					returnList.add(wsb);
				}
				return returnList;
			}else{
				return list;
			}
		}
		returnList.addAll(youpiaoList);
		if(wt.getBillcount()!=null&&wt.getBillcount()>0){
			returnList.add(wt);
		}
		if(wsb.getBillcount()!=null&&wsb.getBillcount()>0){
			returnList.add(wsb);
		}
		returnList.addAll(meipiaoList);
		if(wt.getBillcount()==null||wt.getBillcount()==0){
			returnList.add(wt);
		}
		if(wsb.getBillcount()==null||wsb.getBillcount()==0){
			returnList.add(wsb);
		}
		return returnList;
	}
	private BillCategoryVO queryByCode(String categorycode,String pk_corp,Integer billstate,String period)throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(categorycode);
		sp.addParam(pk_corp);
		sp.addParam(billstate==0?DZFBoolean.FALSE:DZFBoolean.TRUE);
		sp.addParam(period);
		BillCategoryVO[] vo=(BillCategoryVO[])singleObjectBO.queryByCondition(BillCategoryVO.class, "nvl(dr,0)=0 and categorycode=? and pk_corp=? and isaccount=? and period=?", sp);
		return vo[0];
	}
	private List<BillCategoryVO> queryBankGroup(BillcategoryQueryVO paramVO)throws DZFWarpException{
		//返回银行账号
		Map<String, Object[]> groupBanks=queryBankInvoiceCategoryVOs(paramVO);
		List<BillCategoryVO> returnList=new ArrayList<BillCategoryVO>();
		BillCategoryVO categoryVO=queryByID(paramVO.getPk_category());
		Iterator<String> itor=groupBanks.keySet().iterator();
		while(itor.hasNext()){
			String showName=itor.next();
			Object[] obj=groupBanks.get(showName);
			BillCategoryVO bcVO=new BillCategoryVO();
			bcVO.setAllowchild(categoryVO.getAllowchild());
			bcVO.setBillcount(Integer.parseInt(obj[0].toString()));
			bcVO.setCategorycode(obj[1]==null?null:obj[1].toString());
//			bcVO.setCategoryname(showName+"("+obj[0].toString()+")");
			bcVO.setErrorcount(Integer.parseInt(obj[2].toString()));
			bcVO.setCategoryname(showName);
			bcVO.setCategorylevel(categoryVO.getCategorylevel()+1);
			bcVO.setCategorytype(ZncsConst.CATEGORYTYPE_5);
			bcVO.setChildlevel(categoryVO.getCategorylevel());
			bcVO.setDr(0);
			bcVO.setIsaccount(paramVO.getBillstate()==0?DZFBoolean.FALSE:DZFBoolean.TRUE);
			bcVO.setIsleaf(DZFBoolean.FALSE);
			bcVO.setItype(0);
			bcVO.setPk_parentcategory(categoryVO.getPk_category());
			bcVO.setPeriod(paramVO.getPeriod());
			bcVO.setPk_category("bank_"+(obj[1]==null?null:obj[1].toString()));
			bcVO.setPk_bankcode("bank_"+(obj[1]==null?null:obj[1].toString()));
			bcVO.setPk_corp(paramVO.getPk_corp());
			bcVO.setSettype(99);
			returnList.add(bcVO);
		}
		return returnList;
	}
	private BillCategoryVO queryByID(String pk_category)throws DZFWarpException{
		BillCategoryVO vo=(BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_category);
		return vo;
	}
	private List<BillCategoryVO> queryCategory(BillcategoryQueryVO paramVO)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.*,0 as itype from ynt_billcategory a where nvl(a.dr,0)=0 and a.period=? and a.pk_corp=? ");
		sp.addParam(paramVO.getPeriod());
		sp.addParam(paramVO.getPk_corp());
		if(StringUtil.isEmpty(paramVO.getPk_category())){//查1级树
			sb.append(" and a.pk_parentcategory is null");
		}else{
			sb.append(" and a.pk_parentcategory = ?");
			sp.addParam(paramVO.getPk_category());
		}
		if(paramVO.getBillstate()==0){//未制证
			sb.append(" and nvl(a.isaccount,'N') = 'N'");
		}else{
			sb.append(" and nvl(a.isaccount,'N') = 'Y'");
		}
		sb.append(" order by a.showorder");
		List<BillCategoryVO> list=(List<BillCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillCategoryVO.class));
		if(list!=null&&list.size()>0){
			Set<String> codeSet=queryBaseCategoryNotEdit();
			for(int i=0;i<list.size();i++){
				if(codeSet.contains(list.get(i).getCategorycode())){
					list.get(i).setIseditacc(DZFBoolean.FALSE);
				}else{
					list.get(i).setIseditacc(DZFBoolean.TRUE);
				}
			}
		}
		fillBillCount(paramVO, list);
		return list;
	}
	
	/**
	 * 查询集团预制基础分类是否末级，末级可以编辑3个科目，其他不能
	 * @return
	 * @throws DZFWarpException
	 */
	private Set<String> queryBaseCategoryNotEdit()throws DZFWarpException{
		Set<String> returnSet=new HashSet<String>();
		String sql="select categorycode from ynt_basecategory where nvl(dr,0)=0 and pk_corp='000001' and nvl(isleaf,'N')='N' order by categorycode ";
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, new SQLParameter(), new ArrayListProcessor());
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnSet.add(obj[0].toString());
		}
		return returnSet;
	}
	/**
	 * 查出该公司的分类后，给每个分类统计当期的票据数据
	 * @param list
	 * @throws DZFWarpException
	 */
	private void fillBillCount(BillcategoryQueryVO paramVO,List<BillCategoryVO> list)throws DZFWarpException{
		List<InvoiceCategoryVO> invoiceList=queryInvoiceCategoryVOs(paramVO);//所有票据
		List<BillCategoryVO> addBillList=new ArrayList<BillCategoryVO>();//直接挂在所选分类下的票据，需要返回前台
		Set<String> billSet=new HashSet<String>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				//循环查
				String categoryname=list.get(i).getCategoryname();//分类名字，最后要扩展出数量
				String categorycode=list.get(i).getCategorycode();//分类编码，统计数量用
				if(!StringUtil.isEmpty(paramVO.getPk_bankcode())){
					list.get(i).setPk_bankcode(paramVO.getPk_bankcode());//这个分类是属于那个银行账号下的
				}
				int errorCount=0;//这个分类是否有错误，前端是否显示感叹号
				if(invoiceList.size()==0){
//					categoryname=categoryname+"(0)";
					list.get(i).setCategoryname(categoryname);
					list.get(i).setBillcount(0);
				}else{
					int billCount=0;
					for(int j=0;j<invoiceList.size();j++){
						if(invoiceList.get(j).getCategorycode().startsWith(categorycode)){//这个票属于这个分类的子集
							billCount++;
							if(!StringUtil.isEmpty(invoiceList.get(j).getErrordesc())||!StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
								errorCount++;
							}
						}
						if(!StringUtil.isEmpty(paramVO.getPk_category())&&paramVO.getPk_category().equals(invoiceList.get(j).getPk_billcategory())){//这个票直接属于该分类下，把这个票也返回前端，展示票据title
							BillCategoryVO billVO=new BillCategoryVO();
							billVO.setItype(1);
							billVO.setIsaccount(paramVO.getBillstate()==0?DZFBoolean.FALSE:DZFBoolean.TRUE);
							billVO.setBilltitle(invoiceList.get(j).getBilltitle());
							billVO.setPk_category(invoiceList.get(j).getPk_invoice());
							billVO.setNmny(invoiceList.get(j).getNmny());
							billVO.setNtaxnmny(invoiceList.get(j).getNtaxnmny());
							billVO.setNtotaltax(invoiceList.get(j).getNtotaltax());
							billVO.setTaxrate(invoiceList.get(j).getTaxrate());
							billVO.setDinvoicedate(invoiceList.get(j).getDinvoicedate());//日期
							billVO.setVpurchname(invoiceList.get(j).getVpurchname());//付款方
							billVO.setVsalename(invoiceList.get(j).getVsalename());//收款方
							billVO.setDinvoicedate(invoiceList.get(j).getDinvoicedate());
							billVO.setRowcount(invoiceList.get(j).getRowcount());
							billVO.setPk_image_group(invoiceList.get(j).getPk_image_group());
							billVO.setPk_image_library(invoiceList.get(j).getPk_image_library());
							billVO.setIstate(invoiceList.get(j).getIstate());
							billVO.setTruthindent(invoiceList.get(j).getTruthindent());
							if(StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
								billVO.setErrordesc(null);
							}else if(!StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
								billVO.setErrordesc(invoiceList.get(j).getErrordesc());
							}else if(StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&!StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
								billVO.setErrordesc(invoiceList.get(j).getErrordesc2());
							}else if(!StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&!StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
								billVO.setErrordesc(invoiceList.get(j).getErrordesc()+","+invoiceList.get(j).getErrordesc2());
							}
							if(!billSet.contains(billVO.getPk_category())){
								billSet.add(billVO.getPk_category());
								addBillList.add(billVO);
							}
						}
					}
//					categoryname=categoryname+"("+billCount+")";
					list.get(i).setCategoryname(categoryname);
					list.get(i).setBillcount(billCount);
					list.get(i).setErrorcount(errorCount);
				}
			}
		}else{//如果没有分类，看看票是否有这个分类的
			for(int j=0;j<invoiceList.size();j++){
				if(!StringUtil.isEmpty(paramVO.getPk_category())&&paramVO.getPk_category().equals(invoiceList.get(j).getPk_billcategory())){//这个票直接属于该分类下，把这个票也返回前端，展示票据title
					BillCategoryVO billVO=new BillCategoryVO();
					billVO.setItype(1);
					billVO.setIsaccount(paramVO.getBillstate()==0?DZFBoolean.FALSE:DZFBoolean.TRUE);
					billVO.setBilltitle(invoiceList.get(j).getBilltitle());
					billVO.setPk_category(invoiceList.get(j).getPk_invoice());
					billVO.setNmny(invoiceList.get(j).getNmny());
					billVO.setNtaxnmny(invoiceList.get(j).getNtaxnmny());
					billVO.setNtotaltax(invoiceList.get(j).getNtotaltax());
					billVO.setTaxrate(invoiceList.get(j).getTaxrate());
					billVO.setRowcount(invoiceList.get(j).getRowcount());
					billVO.setDinvoicedate(invoiceList.get(j).getDinvoicedate());//日期
					billVO.setVpurchname(invoiceList.get(j).getVpurchname());//付款方
					billVO.setVsalename(invoiceList.get(j).getVsalename());//收款方
					billVO.setPk_image_group(invoiceList.get(j).getPk_image_group());
					billVO.setPk_image_library(invoiceList.get(j).getPk_image_library());
					billVO.setIstate(invoiceList.get(j).getIstate());
					billVO.setTruthindent(invoiceList.get(j).getTruthindent());
					if(StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
						billVO.setErrordesc(null);
					}else if(!StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
						billVO.setErrordesc(invoiceList.get(j).getErrordesc());
					}else if(StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&!StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
						billVO.setErrordesc(invoiceList.get(j).getErrordesc2());
					}else if(!StringUtil.isEmpty(invoiceList.get(j).getErrordesc())&&!StringUtil.isEmpty(invoiceList.get(j).getErrordesc2())){
						billVO.setErrordesc(invoiceList.get(j).getErrordesc()+","+invoiceList.get(j).getErrordesc2());
					}
					addBillList.add(billVO);
				}
			}
		}
		list.addAll(addBillList);//把分类和票组合
	}
	
	/**
	 * 查询票据
	 * @param paramVO
	 * @return
	 * @throws DZFWarpException
	 */
	public List<InvoiceCategoryVO> queryInvoiceCategoryVOs(BillcategoryQueryVO paramVO)throws DZFWarpException{
		String unitName= SystemUtil.queryCorp(paramVO.getPk_corp()).getUnitname();
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.truthindent,a.istate,d.crelationid as pk_image_library,a.ntotaltax,a.nmny,a.ntaxnmny,a.dinvoicedate,a.taxrate,a.pk_invoice,a.vpurchname,a.vpurchtaxno,a.vsalename,a.vsaletaxno,a.pk_billcategory,c.categoryname,c.categorycode,a.billtitle,a.errordesc,a.errordesc2,a.rowcount,b.pk_image_group from ynt_interface_invoice a,ynt_image_group b,ynt_billcategory c");
		sb.append(",ynt_image_ocrlibrary d");
		sb.append(" where  a.pk_image_group=b.pk_image_group and a.pk_billcategory=c.pk_category and a.ocr_id=d.pk_image_ocrlibrary and nvl(d.dr,0)=0  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(c.dr,0)=0 and b.istate !=205 ");
		if(paramVO.getBillstate()==0){
			sb.append(" and b.istate !=100 and b.istate !=101 and nvl(c.isaccount,'N')='N' ");
		}else if(paramVO.getBillstate()==1){
			sb.append(" and (b.istate =100 or b.istate =101) and nvl(c.isaccount,'N')='Y'");
		}
		sb.append(" and a.pk_corp=? ");
		sp.addParam(paramVO.getPk_corp());
		sb.append(" and a.period=?");
		sp.addParam(paramVO.getPeriod());
		if(DZFBoolean.TRUE.equals(paramVO.getIsBank())){
			sb.append("and a.pk_billcategory in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode like '"+ZncsConst.FLCODE_YHPJ+"%')");
			sp.addParam(paramVO.getPk_corp());
			sp.addParam(paramVO.getPeriod());
			if(StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null")){
//				sb.append("and (case when '"+unitName+"' like a.vpurchname ||'%' and length('"+unitName+"') - length(a.vpurchname) < 2 then  a.vpurchtaxno  when '"+unitName+"' like a.vsalename || '%' and  length('"+unitName+"') - length(a.vsalename) < 2 then a.vsaletaxno end) is null");
			}else{
//				sb.append(" and (a.vpurchtaxno=? or a.vsaletaxno=?)");
//				String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
//				sp.addParam(code);
//				sp.addParam(code);
			}
		}
		if(!StringUtil.isEmpty(paramVO.getBdate())){
			sb.append(" and a.dinvoicedate>=?");
			sp.addParam(paramVO.getBdate());
		}
		if(!StringUtil.isEmpty(paramVO.getEdate())){
			sb.append(" and a.dinvoicedate<=?");
			sp.addParam(paramVO.getBdate());
		}
		if(!StringUtil.isEmpty(paramVO.getBilltype())){//增值税，银行票据，其他票据
			String type ="";
			switch (paramVO.getBilltype()) {
			case "增值税":
				sb.append(" and invoicetype like '%增值税%' ");
				break;
			case "银行票据":
				sb.append(" and istate like 'b%'");
				break;
			case "其他票据":
				sb.append(" and istate like 'c%'");
				break;
			default:
				break;
			}
		}
		if(!StringUtil.isEmpty(paramVO.getRemark())){
			sb.append(" and pk_invoice in (");
			sb.append(" select pk_invoice From ynt_interface_invoice_detail where invname like ? and nvl(dr,0)=0 and pk_corp =?");
			sb.append("  )");
			sp.addParam("%"+paramVO.getRemark() + "%");
			sp.addParam(paramVO.getPk_corp());
		}
		sb.append(" order by d.iorder ");
		List<InvoiceCategoryVO> list=(List<InvoiceCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InvoiceCategoryVO.class));
		if(list!=null&&list.size()>0&&DZFBoolean.TRUE.equals(paramVO.getIsBank())){
			//过滤出错的票
			List<InvoiceCategoryVO> returnList=new ArrayList<InvoiceCategoryVO>();
			for(int i=0;i<list.size();i++){
				InvoiceCategoryVO invVO=list.get(i);
				String vpurchname=invVO.getVpurchname();//购方名称
				String vpurchtaxno=invVO.getVpurchtaxno();//购方账号
				String vsalename=invVO.getVsalename();//销方名称
				String vsaletaxno=invVO.getVsaletaxno();//销方账号
				if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vsaletaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vsaletaxno)&&vsaletaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else{//出问题了
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))){
						returnList.add(invVO);
					}
				}
			}
			list=returnList;
		}
		return list;
	}
	
	public List<CheckOcrInvoiceVO> queryErrorInvoiceCategoryVOs(BillcategoryQueryVO paramVO)throws DZFWarpException{
		String unitName=SystemUtil.queryCorp(paramVO.getPk_corp()).getUnitname();
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.webid,a.ntotaltax,a.dinvoicedate,a.nmny,a.ntaxnmny,a.taxrate,a.pk_invoice,a.vpurchname,a.vpurchtaxno,a.vsalename,a.vsaletaxno,a.pk_billcategory,c.categoryname,c.categorycode,a.billtitle,a.errordesc,a.errordesc2,a.rowcount,b.pk_image_group from ynt_interface_invoice a,ynt_image_group b,ynt_billcategory c");
		sb.append(" where a.pk_image_group=b.pk_image_group and a.pk_billcategory=c.pk_category and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(c.dr,0)=0 and b.istate !=205 ");
		if(paramVO.getBillstate()==0){
			sb.append(" and b.istate !=100 and b.istate !=101 and nvl(c.isaccount,'N')='N' ");
		}else if(paramVO.getBillstate()==1){
			sb.append(" and (b.istate =100 or b.istate =101) and nvl(c.isaccount,'N')='Y'");
		}
		sb.append(" and a.pk_corp=? ");
		sp.addParam(paramVO.getPk_corp());
		sb.append(" and a.period=?");
		sp.addParam(paramVO.getPeriod());
		if(DZFBoolean.TRUE.equals(paramVO.getIsBank())){
			sb.append("and a.pk_billcategory in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode like '"+ZncsConst.FLCODE_YHPJ+"%')");
		}else{
			sb.append("and a.pk_billcategory in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode like '"+paramVO.getCategorycode()+"%')");
		}
		sp.addParam(paramVO.getPk_corp());
		sp.addParam(paramVO.getPeriod());
		sb.append(" and (a.errordesc is not null or a.errordesc2 is not null)");
		List<InvoiceCategoryVO> list=(List<InvoiceCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InvoiceCategoryVO.class));
		if(list!=null&&list.size()>0&&DZFBoolean.TRUE.equals(paramVO.getIsBank())&&!StringUtil.isEmpty(paramVO.getCategorycode())){
			//过滤出错的票
			List<InvoiceCategoryVO> returnList=new ArrayList<InvoiceCategoryVO>();
			for(int i=0;i<list.size();i++){
				InvoiceCategoryVO invVO=list.get(i);
				String vpurchname=invVO.getVpurchname();//购方名称
				String vpurchtaxno=invVO.getVpurchtaxno();//购方账号
				String vsalename=invVO.getVsalename();//销方名称
				String vsaletaxno=invVO.getVsaletaxno();//销方账号
				if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vsaletaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vsaletaxno)&&vsaletaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else{//出问题了
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))){
						returnList.add(invVO);
					}
				}
			}
			list=returnList;
		}
		List<CheckOcrInvoiceVO> returnList=new ArrayList<CheckOcrInvoiceVO>();
		if(list!=null&&list.size()>0){
			Map<String, String> categoryMap=queryCategoryMap(paramVO.getPeriod(), paramVO.getPk_corp());
			
			for(int i=0;i<list.size();i++){
				CheckOcrInvoiceVO error=new CheckOcrInvoiceVO();
				error.setPk_invoice(list.get(i).getPk_invoice());//主键
				error.setCategoryname(list.get(i).getCategoryname());//分类名称
				error.setBilltitle(list.get(i).getBilltitle());//标题
				error.setWebid(list.get(i).getWebid());
				if(!StringUtil.isEmpty(list.get(i).getErrordesc())&&StringUtil.isEmpty(list.get(i).getErrordesc2())){
					error.setErrordesc(list.get(i).getErrordesc());
				}else if(StringUtil.isEmpty(list.get(i).getErrordesc())&&!StringUtil.isEmpty(list.get(i).getErrordesc2())){
					error.setErrordesc(list.get(i).getErrordesc2());
				}else if(!StringUtil.isEmpty(list.get(i).getErrordesc())&&!StringUtil.isEmpty(list.get(i).getErrordesc2())){
					error.setErrordesc(list.get(i).getErrordesc()+","+list.get(i).getErrordesc2());
				}
				if(StringUtil.isEmpty(paramVO.getCategorycode())){
					String vpurchname=list.get(i).getVpurchname();//购方名称
					String vpurchtaxno=list.get(i).getVpurchtaxno();//购方账号
					String vsalename=list.get(i).getVsalename();
					String vsaletaxno=list.get(i).getVsaletaxno();//销方账号
					//采购方和付款方是一个字段，销售方和收款方是一个字段
					String bankAccountNo=null;//生效的账号
					if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
						bankAccountNo=vpurchtaxno;
					}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
						bankAccountNo=vsaletaxno;
					}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
						bankAccountNo=vpurchtaxno;
					}else{//出问题了
						bankAccountNo=null;
					}
					error.setOcraddress(getAllParentCategoryKey(categoryMap, list.get(i).getPk_billcategory(),"bank_"+bankAccountNo));
				}else{
					error.setOcraddress(getAllParentCategoryKey(categoryMap, list.get(i).getPk_billcategory(),paramVO.getCategorycode()));
				}
				
				returnList.add(error);
			}
		}
		return returnList;
	}
	
	private String getAllParentCategoryKey(Map<String, String> categoryMap,String curkey,String categorycode){
		String retuenKey=curkey;
		while (true) {
			String parentKey=categoryMap.get(curkey);
			if(!StringUtil.isEmpty(parentKey)){
				retuenKey=parentKey+","+retuenKey;
				curkey=parentKey;
			}else{
				break;
			}
		}
		if(!StringUtil.isEmpty(categorycode)&&categorycode.startsWith("bank_")){
			retuenKey=retuenKey.substring(0, 25)+categorycode+","+retuenKey.substring(25,retuenKey.length());
		}
		return retuenKey;
	}
	private Map<String, String> queryCategoryMap(String period, String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,pk_parentcategory from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp=? and nvl(isaccount,'N')='N' order by categorylevel desc");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		Map<String, String> returnMap=new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(),obj[1]==null?null:obj[1].toString());
		}
		return returnMap;
	}
	private Map<String, String> queryCategoryMap2(String period, String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,pk_parentcategory from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp=?  order by categorylevel desc");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		Map<String, String> returnMap=new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(),obj[1]==null?null:obj[1].toString());
		}
		return returnMap;
	}
	private Map<String, String> queryCategoryCodeMap(String period, String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,categorycode from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp=? and nvl(isaccount,'N')='N' order by categorylevel desc");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		Map<String, String> returnMap=new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(),obj[1]==null?null:obj[1].toString());
		}
		return returnMap;
	}
	private Map<String, String> queryCategoryCodeMap2(String period, String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,categorycode from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp=?  order by categorylevel desc");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		Map<String, String> returnMap=new HashMap<String, String>();
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(),obj[1]==null?null:obj[1].toString());
		}
		return returnMap;
	}
	@Override
	public List<OcrInvoiceVO> queryBankInvoiceVOs(BillcategoryQueryVO paramVO)throws DZFWarpException{
		String unitName= SystemUtil.queryCorp(paramVO.getPk_corp()).getUnitname();
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b,ynt_billcategory c");
		sb.append(" where a.pk_image_group=b.pk_image_group and a.pk_billcategory=c.pk_category and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(c.dr,0)=0 and b.istate !=205 ");
		if(paramVO.getBillstate()==0){
			sb.append(" and b.istate !=100 and b.istate !=101 and nvl(c.isaccount,'N')='N' ");
		}else if(paramVO.getBillstate()==1){
			sb.append(" and (b.istate =100 or b.istate =101) and nvl(c.isaccount,'N')='Y'");
		}
		sb.append(" and a.pk_corp=? ");
		sp.addParam(paramVO.getPk_corp());
		sb.append(" and a.period=?");
		sp.addParam(paramVO.getPeriod());
		if(DZFBoolean.TRUE.equals(paramVO.getIsBank())){
			sb.append("and a.pk_billcategory in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode like '"+ZncsConst.FLCODE_YHPJ+"%')");
			sp.addParam(paramVO.getPk_corp());
			sp.addParam(paramVO.getPeriod());
		}
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		if(list != null && list.size() > 0 && DZFBoolean.TRUE.equals(paramVO.getIsBank())){
			//过滤出错的票
			List<OcrInvoiceVO> returnList=new ArrayList<OcrInvoiceVO>();
			for(int i=0;i<list.size();i++){
				OcrInvoiceVO invVO=list.get(i);
				String vpurchname=invVO.getVpurchname();//购方名称
				String vpurchtaxno=invVO.getVpurchtaxno();//购方账号
				String vsalename=invVO.getVsalename();//销方名称
				String vsaletaxno=invVO.getVsaletaxno();//销方账号
				if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vsaletaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vsaletaxno)&&vsaletaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}
					if(!StringUtil.isEmpty(paramVO.getCategorycode())){
						String code=paramVO.getCategorycode().startsWith("bank_")?paramVO.getCategorycode().substring(5, paramVO.getCategorycode().length()):paramVO.getCategorycode();
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else{//出问题了
					if((StringUtil.isEmpty(paramVO.getCategorycode())||paramVO.getCategorycode().equals("bank_null"))){
						returnList.add(invVO);
					}
				}
			}
			list=returnList;
		}
		return list;
	}
	
	/**
	 * 查询按银行分组的票据
	 * @param paramVO
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, Object[]> queryBankInvoiceCategoryVOs(BillcategoryQueryVO paramVO)throws DZFWarpException{
		String unitName=SystemUtil.queryCorp(paramVO.getPk_corp()).getUnitname();
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where a.pk_image_group=b.pk_image_group  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.istate !=205");//and a.invoicetype like 'b%'
		if(paramVO.getBillstate()==0){
			sb.append(" and b.istate !=100 and b.istate !=101");
		}else if(paramVO.getBillstate()==1){
			sb.append(" and (b.istate =100 or b.istate =101)");
		}
		sb.append(" and a.pk_corp=? ");
		sp.addParam(paramVO.getPk_corp());
		sb.append(" and a.period=?");
		sp.addParam(paramVO.getPeriod());
		sb.append("and a.pk_billcategory in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode like '"+ZncsConst.FLCODE_YHPJ+"%')");
		sp.addParam(paramVO.getPk_corp());
		sp.addParam(paramVO.getPeriod());
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		Map<String, Object[]> returnMap=new HashMap<String, Object[]>();//key显示名称,value[0]数量value[1]账号
		if(list!=null&&list.size()>0){
			Map<String, String> accountMap=queryBankAccountVOs(paramVO.getPk_corp());
			for(int i=0;i<list.size();i++){
				OcrInvoiceVO invVO=list.get(i);
				String vpurchname=invVO.getVpurchname();//购方名称
				String vpurchtaxno=invVO.getVpurchtaxno();//购方账号
				String vsalename=StringUtil.isEmpty(invVO.getVsalename())?"":invVO.getVsalename();//销方名称
				String vsaletaxno=invVO.getVsaletaxno();//销方账号
				//采购方和付款方是一个字段，销售方和收款方是一个字段
				String showName=null;
				String bankAccountNo=null;//生效的账号
				if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
					if(accountMap.containsKey(vpurchtaxno)){
						showName=accountMap.get(vpurchtaxno);
					}else{
						showName="银行账号-"+ (StringUtil.isEmpty(vpurchtaxno) ? "空" : vpurchtaxno.trim());//(StringUtil.isEmpty(vpurchtaxno)||vpurchtaxno.length()<=4?vpurchtaxno:vpurchtaxno.substring(vpurchtaxno.length()-4, vpurchtaxno.length()));
					}
					bankAccountNo=vpurchtaxno;
				}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
					if(accountMap.containsKey(vsaletaxno)){
						showName=accountMap.get(vsaletaxno);
					}else{
						showName="银行账号-"+ (StringUtil.isEmpty(vsaletaxno) ? "空" : vsaletaxno.trim());//(StringUtil.isEmpty(vsaletaxno)||vsaletaxno.length()<=4?vsaletaxno:vsaletaxno.substring(vsaletaxno.length()-4, vsaletaxno.length()));
					}
					bankAccountNo=vsaletaxno;
				}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
					if(accountMap.containsKey(vpurchtaxno)){
						showName=accountMap.get(vpurchtaxno);
					}else{
						showName="银行账号-"+ (StringUtil.isEmpty(vpurchtaxno) ? "空" : vpurchtaxno.trim());//(StringUtil.isEmpty(vpurchtaxno)||vpurchtaxno.length()<=4?vpurchtaxno:vpurchtaxno.substring(vpurchtaxno.length()-4, vpurchtaxno.length()));
					}
					bankAccountNo=vpurchtaxno;
				}else{//出问题了
					bankAccountNo=null;
				}
				if(bankAccountNo==null){
					showName="其他账号";
				}
				if(returnMap.containsKey(showName)){//统计数量
					Object[] obj=returnMap.get(showName);
					if(!StringUtil.isEmpty(invVO.getErrordesc())||!StringUtil.isEmpty(invVO.getErrordesc2())){
						obj[2]=Integer.parseInt(obj[2].toString())+1;
					}
					obj[0]=Integer.parseInt(obj[0].toString())+1;
					returnMap.put(showName, obj);
				}else{
					Integer errorCount=0;
					if(!StringUtil.isEmpty(invVO.getErrordesc())||!StringUtil.isEmpty(invVO.getErrordesc2())){
						errorCount++;
					}
					returnMap.put(showName, new Object[]{1,bankAccountNo,errorCount});
				}
				
			}
		}
		return returnMap;
	}
	
	/**
	 * 取银行账号
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, String> queryBankAccountVOs(String pk_corp)throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		BankAccountVO[] vos=(BankAccountVO[])singleObjectBO.queryByCondition(BankAccountVO.class, "nvl(dr,0)=0 and pk_corp=?", sp);
		Map<String, String> returnMap=new HashMap<String, String>();
		if(vos!=null&&vos.length!=0){
			for(int i=0;i<vos.length;i++){
				String bankaccount=vos[i].getBankaccount();
//				String tmpaccount=bankaccount.length()<=4?bankaccount:bankaccount.substring(bankaccount.length()-4, bankaccount.length());
				returnMap.put(bankaccount,vos[i].getBankname()+"-"+bankaccount);
			}
		}
		return returnMap;
	}

	@Override
	public List<CategoryTreeVO> queryCategoryTree(String pk_corp,String period) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,pk_basecategory,pk_parentcategory,categoryname,isleaf from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and nvl(isaccount,'N')='N' and categorytype in(0,1,2)");
		sp.addParam(pk_corp);
		sp.addParam(period);
		sb.append("order by showorder");
		List<CategoryTreeVO> list = (List<CategoryTreeVO>) singleObjectBO.executeQuery(sb.toString(), sp,new BeanListProcessor(CategoryTreeVO.class));
		if(list!=null&&list.size()>0){
			CategoryTreeVO vo  = buildTree(list);
			CategoryTreeVO[] bodyvos = (CategoryTreeVO[])vo.getChildren();
			list = new ArrayList<CategoryTreeVO>(Arrays.asList(bodyvos));
		}
		return list;
	}
	
	private CategoryTreeVO buildTree(List<CategoryTreeVO> list){
		CategoryTreeVO vo = (CategoryTreeVO) BDTreeCreator.createTree(
				list.toArray(new CategoryTreeVO[0]), new AccTreeCreateStrategyByID(){
					@Override
					public SuperVO getRootVO() {
						return new CategoryTreeVO();
					}
					@Override
					public Object getNodeId(Object obj) {
						CategoryTreeVO vo = (CategoryTreeVO)obj;
						return vo.getPk_category();
					}
					@Override
					public Object getParentNodeId(Object obj) {
						CategoryTreeVO vo = (CategoryTreeVO)obj;
						return vo.getPk_parentcategory();
					}
				});
		return vo;
	}

	@Override
	public void saveNewCategory(String[] pk_bills, String pk_tree, String pk_corp,String period) throws DZFWarpException {
		//0、查票
		OcrInvoiceVO[] vos=queryOcrInvoiceVOs(pk_bills);
		saveNewCategroy(vos, pk_tree, pk_corp, period);
	}
	
	/**
	 * @param vos
	 * @throws DZFWarpException
	 */
	@Override
	public DZFBoolean checkHaveZckp(OcrInvoiceVO[] vos)throws DZFWarpException{
		Set<String> detailSet=new HashSet<String>();
		for (int i = 0; i < vos.length; i++) {
			OcrInvoiceVO ocrInvoiceVO = vos[i];
			OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren();
			for (int j = 0; detailVOs!=null&&j < detailVOs.length; j++) {
				OcrInvoiceDetailVO ocrInvoiceDetailVO = detailVOs[j];
				if(!StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_invoice_detail())){
					detailSet.add(ocrInvoiceDetailVO.getPk_invoice_detail());
				}
				
			}
		}
		if(detailSet.size()>0){
			String sql="select * from ynt_assetcard where nvl(dr,0)=0 and  "+ SqlUtil.buildSqlForIn("pk_invoice_detail", detailSet.toArray(new String[0]));
			List<AssetcardVO> list=(List<AssetcardVO>)singleObjectBO.executeQuery(sql, new SQLParameter(),new BeanListProcessor(AssetcardVO.class));
			if(list!=null&&list.size()>0){
				return DZFBoolean.TRUE;
			}else{
				return DZFBoolean.FALSE;
			}
		}else{
			return DZFBoolean.FALSE;
		}
		
	}
	
	/**
	 * @param vos
	 * @throws DZFWarpException
	 */
	@Override
	public DZFBoolean checkHaveIctrade(OcrInvoiceVO[] vos)throws DZFWarpException{
		Set<String> imageGroupSet=new HashSet<String>();
		for (int i = 0; i < vos.length; i++) {
			OcrInvoiceVO ocrInvoiceVO = vos[i];
			if(!StringUtil.isEmpty(ocrInvoiceVO.getPk_image_group())){
				imageGroupSet.add(ocrInvoiceVO.getPk_image_group());
			}
			
		}
		if(imageGroupSet.size()>0){
			String sql="select * from ynt_ictrade_h where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_image_group", imageGroupSet.toArray(new String[0]));
			List<IntradeHVO> list=(List<IntradeHVO>)singleObjectBO.executeQuery(sql, new SQLParameter(),new BeanListProcessor(IntradeHVO.class));
			if(list!=null&&list.size()>0){
				return DZFBoolean.TRUE;
			}else{
				return DZFBoolean.FALSE;
			}
		}else{
			return DZFBoolean.FALSE;
		}
	}
	@Override
	public void saveNewCategroy(OcrInvoiceVO[] vos, String pk_tree, String pk_corp, String period) throws DZFWarpException {
		//0、检查票据下的表体是否有生成的资产卡片，并且没有生成凭证
		if(DZFBoolean.TRUE.equals(checkHaveZckp(vos))){
			throw new BusinessException("所选票据已生成资产卡片，不能移动。");
		}
		if(DZFBoolean.TRUE.equals(checkHaveIctrade(vos))){
			throw new BusinessException("所选票据已生成出入库单，不能移动。");
		}
		// 1、目标分类
		BillCategoryVO treeVO = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_tree);
		if (treeVO.getIsaccount() != null && treeVO.getIsaccount().booleanValue())
		{
			throw new BusinessException("目标分类 ‘" + treeVO.getCategoryname() + "’ 是已做账类型，操作非法。");
		}
		// 2、目标分类的孩子
		List<BillCategoryVO> childrenTreeVOs = queryChildCategoryVOs(pk_tree, DZFBoolean.FALSE);
		// 3、目标基础分类
		BaseCategoryVO baseTreeVO = (BaseCategoryVO) singleObjectBO.queryByPrimaryKey(BaseCategoryVO.class, treeVO.getPk_basecategory());
		if (treeVO != null) {
			if (treeVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_0) || treeVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_1)) {
				if (baseTreeVO.getIsleaf() == DZFBoolean.FALSE) {
					throw new BusinessException("只能移动到末级分类！");
				}
			} else {
				if (treeVO.getIsleaf() == DZFBoolean.FALSE) {// 如果不是末级要看看下级挂的是不是分组目录
					// 如果是分组就可以放到这个目录，否则报错
					if (childrenTreeVOs != null && childrenTreeVOs.size() > 0) {
						for (int i = 0; i < childrenTreeVOs.size(); i++) {
							if (!childrenTreeVOs.get(i).getCategorytype().equals(ZncsConst.CATEGORYTYPE_3) && !childrenTreeVOs.get(i).getCategorytype().equals(ZncsConst.CATEGORYTYPE_4)) {
								throw new BusinessException("只能移动到末级分类！");
							}
						}
					}
				}
			}
		} else {
			throw new BusinessException("查找树节点失败！");
		}
		// 4、更新pk_billcategory
		List<ParaSetVO> listParas = iParaSet.queryParaSet(pk_corp);

		boolean srfz = listParas.get(0).getIncomeclass() == 2;					//收入按往来客户分组
		boolean kccgfz = (listParas.get(0).getPurchclass() == null ? false : listParas.get(0).getPurchclass().booleanValue());		//库存采购按往来客户分组
		boolean cbfz = (listParas.get(0).getCostclass() == null ? false : listParas.get(0).getCostclass().booleanValue());				//按成本分组
		boolean bankinoutfz = (listParas.get(0).getBankinoutclass() == null ? false : listParas.get(0).getBankinoutclass().booleanValue());//银行转入转出按往来分组

		// 判断是否是未识别票据
		BillCategoryVO BillParentVO = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, vos[0].getPk_billcategory());
		if (BillParentVO == null && (vos[0].getUpdateflag() == null || vos[0].getUpdateflag().booleanValue() == false))
		{
			return;
		}
		if (BillParentVO.getCategorycode().startsWith(ZncsConst.FLCODE_WSB)) {
			throw new BusinessException("不能从未识别票据移出");
		}
		if (BillParentVO.getIsaccount() != null && BillParentVO.getIsaccount().booleanValue()) {
//			throw new BusinessException("源分类 ‘" + BillParentVO.getCategoryname() + "’ 是已做账类型，操作非法。");
			return;
		}
		if (treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_WSB)) 
		{
			throw new BusinessException("不能移至未识别票据");
		}
		if (treeVO.getCategorycode().equals(BillParentVO.getCategorycode()))
		{
//			throw new BusinessException("所移动分类与原分类相同，请重新选择");
			return;
		}
		if (!srfz && !kccgfz && !cbfz && !bankinoutfz) {
			for (int i = 0; i < vos.length; i++) {
				OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
				for (int k = 0;detailVos!=null&& k < detailVos.length; k++) {
					if (StringUtil.isEmpty(detailVos[k].getPk_billcategory())||vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
						detailVos[k].setPk_billcategory(pk_tree);
					}
				}
				vos[i].setPk_billcategory(pk_tree);// 移动到新目录
				vos[i].setHandflag(ZncsConst.HANDFLAG_2);
			}
		} else {// 用了参数，看是否是收入或库存或成本或银行转入转出
			if (treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_SR) && srfz
					|| treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_KC) && kccgfz
					|| treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_CB) && cbfz
					|| (treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHZR)
							|| treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHZC)) && bankinoutfz) {// 收入,库存,成本，银行转入转出
//				if (srfz == ZncsConst.SRFL_1) {// 按税率分组
//					// 税率找票据的表体第一行
//					for (int i = 0; i < vos.length; i++) {
////						OcrInvoiceDetailVO detailVO = getFirstOcrInvoiceDetailVO(vos[i].getPk_invoice());
//						OcrInvoiceDetailVO detailVO = (OcrInvoiceDetailVO)vos[i].getChildren()[0];
//						BillCategoryVO newVO = null;
//						if (detailVO != null) {
//							DZFDouble sl = OcrUtil.getInvoiceSL(detailVO.getItemtaxrate());
//							if (sl.compareTo(DZFDouble.ZERO_DBL)==0) {
//								// 创建其他税率目录
//								newVO = createBillCategoryVOByName(pk_corp, "其他税率", ZncsConst.CATEGORYTYPE_3, treeVO, childrenTreeVOs, DZFBoolean.FALSE, period);
//							} else {// 创建税率目录
//								newVO = createBillCategoryVOByName(pk_corp, sl.toString() + "%", ZncsConst.CATEGORYTYPE_3, treeVO, childrenTreeVOs, DZFBoolean.FALSE, period);
//							}
//						} else {// 创建其他税率目录
//							newVO = createBillCategoryVOByName(pk_corp, "其他税率", ZncsConst.CATEGORYTYPE_3, treeVO, childrenTreeVOs, DZFBoolean.FALSE, period);
//						}
//						OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
//						for (int k = 0;detailVos!=null&& k < detailVos.length; k++) {
//							if (StringUtil.isEmpty(detailVos[k].getPk_billcategory())||vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
//								detailVos[k].setPk_billcategory(newVO.getPk_category());
//							}
//						}
//						vos[i].setPk_billcategory(newVO.getPk_category());// 移动到新目录
//						vos[i].setHandflag(ZncsConst.HANDFLAG_2);
//					}
//				} else {// 按客户
					// 客户找不是自己的另一方
				String unitName = SystemUtil.queryCorp(pk_corp).getUnitname();// 自己的名字
				BillCategoryVO newVO = null;
				for (int i = 0; i < vos.length; i++) {
					String vpurchname = vos[i].getVpurchname();
					String vsalename = StringUtil.isEmpty(vos[i].getVsalename())?"":vos[i].getVsalename();
					// 都是自己，都不是自己，任意一方名字为空都进其他往来
					if (StringUtil.isEmpty(vpurchname) || StringUtil.isEmpty(vsalename) || OcrUtil.isSameCompany(unitName, vpurchname) && OcrUtil.isSameCompany(unitName, vsalename) || !OcrUtil.isSameCompany(unitName, vpurchname) && !OcrUtil.isSameCompany(unitName, vsalename)) {
						newVO = createBillCategoryVOByName(pk_corp, "其他往来", ZncsConst.CATEGORYTYPE_4, treeVO, childrenTreeVOs, DZFBoolean.FALSE, period);
					} else {
						if ( OcrUtil.isSameCompany(unitName, vpurchname)) {
							newVO = createBillCategoryVOByName(pk_corp, vsalename, ZncsConst.CATEGORYTYPE_4, treeVO, childrenTreeVOs, DZFBoolean.FALSE, period);
						} else {
							newVO = createBillCategoryVOByName(pk_corp, vpurchname, ZncsConst.CATEGORYTYPE_4, treeVO, childrenTreeVOs, DZFBoolean.FALSE, period);
						}
					}
					OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
					for (int k = 0;detailVos!=null&& k < detailVos.length; k++) {
						if (StringUtil.isEmpty(detailVos[k].getPk_billcategory())||vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
							detailVos[k].setPk_billcategory(newVO.getPk_category());
						}
					}
					vos[i].setPk_billcategory(newVO.getPk_category());// 移动到新目录
					vos[i].setHandflag(ZncsConst.HANDFLAG_2);
				}

			} else {
				for (int i = 0; i < vos.length; i++) {
					OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
					for (int k = 0;detailVos!=null&& k < detailVos.length; k++) {
						if (StringUtil.isEmpty(detailVos[k].getPk_billcategory())||vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
							detailVos[k].setPk_billcategory(pk_tree);
						}
					}
					vos[i].setPk_billcategory(pk_tree);// 移动到新目录
					vos[i].setHandflag(ZncsConst.HANDFLAG_2);
				}
			}
		}
		// 4、处理pk_category_keyword
		Set<String> keySet = new HashSet<String>();
		for (int i = 0; i < vos.length; i++) {
			if (!StringUtil.isEmpty(vos[i].getPk_category_keyword())) {
				if(DZFBoolean.FALSE.equals(isNeedStudy(pk_corp, vos[i], treeVO)))continue;
				if (BillParentVO.getCategorycode().substring(0, 2).equals(treeVO.getCategorycode().substring(0, 2)) == false // 移动后不是一个大类别
						&& vos[i].getPjlxstatus() != null && (vos[i].getPjlxstatus().intValue() == 21 || vos[i].getPjlxstatus().intValue() == 22)) {
					vos[i].setPjlxstatus(20); // 一旦移动，清除是存货或费用上传的标志，否则重新整理会失效
				}
				if (!keySet.contains(vos[i].getPk_category_keyword())) {
					
					CategoryKeywordVO[] ckVOs = queryCategoryKeywordVOs(new String[] { vos[i].getPk_category_keyword() });
					if (ckVOs != null && ckVOs.length > 0)
					{
						keySet.add(vos[i].getPk_category_keyword());
						
						boolean oldRecordExist = false;
						if (ckVOs[0].getPk_corp().equals(IDefaultValue.DefaultGroup)) {// 集团预制
							//查找当前集团预制规则行已经被学习到哪个分类
							CategoryKeywordVO[] oldstudiedVOs = (CategoryKeywordVO[])singleObjectBO.queryByCondition(CategoryKeywordVO.class, "pk_corp='" + pk_corp + "' and pk_category_keyword_ori='" + ckVOs[0].getPk_category_keyword() + "' and nvl(dr,0)=0", new SQLParameter());
							if (oldstudiedVOs != null && oldstudiedVOs.length > 0)
							{
								ckVOs = oldstudiedVOs;
								oldRecordExist = true;
							}
						}
						else
						{
							oldRecordExist = true;
						}
						if (oldRecordExist == false)// 集团预制
						{
							CategoryKeywordVO tmpVO = (CategoryKeywordVO) ckVOs[0].clone();
							tmpVO.setPk_corp(pk_corp);
							tmpVO.setPk_category_keyword_ori(ckVOs[0].getPk_category_keyword());
							tmpVO.setPk_category_keyword(null);
							if (StringUtil.isEmpty(treeVO.getPk_basecategory())) {
								tmpVO.setPk_category(treeVO.getPk_category());
								tmpVO.setPk_basecategory(null);
							} else {
								tmpVO.setPk_category(null);
								tmpVO.setPk_basecategory(treeVO.getPk_basecategory());
							}
							//如果当前移动的规则没有关键字内容，是个默认的低级别规则，则不提升至最高，只比预制规则提升1 , 2019-10-09, ztc
							if (StringUtil.isEmptyWithTrim(tmpVO.getPk_keywords()))
							{
								tmpVO.setPriority(tmpVO.getPriority() - 1);
							}
							else
							{
								tmpVO.setPriority(9);
							}
							tmpVO = (CategoryKeywordVO) singleObjectBO.insertVO(pk_corp, tmpVO);
							vos[i].setPk_category_keyword(tmpVO.getPk_category_keyword());
							OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
							for (int k = 0; k < detailVos.length; k++) {
								if (vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
									detailVos[k].setPk_category_keyword(tmpVO.getPk_category_keyword());
								}
							}
						} else {
							if (StringUtil.isEmpty(treeVO.getPk_basecategory())) {
								ckVOs[0].setPk_category(treeVO.getPk_category());
								ckVOs[0].setPk_basecategory(null);
							} else {
								ckVOs[0].setPk_category(null);
								ckVOs[0].setPk_basecategory(treeVO.getPk_basecategory());
							}
							//如果当前移动的规则没有关键字内容，是个默认的低级别规则，则不改变优先级 , 2019-10-09, ztc
							if (StringUtil.isEmptyWithTrim(ckVOs[0].getPk_keywords()) == false)
							{
								ckVOs[0].setPriority(9);
							}
							
							singleObjectBO.update(ckVOs[0]);
							vos[i].setPk_category_keyword(ckVOs[0].getPk_category_keyword());
							OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
							for (int k = 0; detailVos!=null&&k < detailVos.length; k++) {
								if (vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
									detailVos[k].setPk_category_keyword(ckVOs[0].getPk_category_keyword());
								}
							}
						}
					}
					else
					{
						//分类匹配规则丢失，可能是无用规则，被删除了
						vos[i].setPk_category_keyword(null);
						OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
						for (int k = 0; detailVos!=null && k < detailVos.length; k++) {
							if (vos[i].getPk_billcategory().equals(detailVos[k].getPk_billcategory())) {
								detailVos[k].setPk_category_keyword(null);
							}
						}
					}

				}
			}
		}
		if(!StringUtil.isEmpty(vos[0].getPrimaryKey())){
			// 更新表头2值
			singleObjectBO.updateAry(vos, new String[] { "pk_billcategory", "pk_category_keyword", "handflag", "pjlxstatus" });
			// 更新表体1个值
			for (int i = 0; i < vos.length; i++) {
				OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
				if(detailVos!=null){
					singleObjectBO.updateAry(detailVos, new String[] { "pk_billcategory", "pk_category_keyword" });
				}
			}
		}
	}
	
	/**
	 * 判断是否需要学习规则
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFBoolean isNeedStudy(String pk_corp,OcrInvoiceVO invoiceVO,BillCategoryVO treeVO)throws DZFWarpException{
		String unitName =SystemUtil.queryCorp(pk_corp).getUnitname();// 自己的名字
		String vpurchname = invoiceVO.getVpurchname();
		String vsalename = StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
		int flag=treeVO.getInoutflag()==null?0:treeVO.getInoutflag();
		DZFBoolean needStudy = DZFBoolean.TRUE;
		if (treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_WT))
		{
			needStudy = DZFBoolean.FALSE;	//移入问题票据无需学习
		}
		//先根据收支方向判断是否需要学习
		if(flag==1){//票是销方。目标目录也是销方
			if(StringUtil.isEmpty(vsalename) || unitName.startsWith(vsalename) == false){
				needStudy = DZFBoolean.FALSE;
			}
		}else if(flag==2){//票是购方。目标目录也是购方
			if(StringUtil.isEmpty(vpurchname) || unitName.startsWith(vpurchname) == false){
				needStudy = DZFBoolean.FALSE;
			}
		}
		//银行票移至非银行类别，非银行票移至银行类别，不用学习
		if (ZncsConst.SBZT_1.equals(invoiceVO.getIstate()) && treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHPJ) == false 
				|| ZncsConst.SBZT_1.equals(invoiceVO.getIstate()) == false && treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHPJ))
		{
			needStudy = DZFBoolean.FALSE;
		}

		return needStudy;
	}
	/**
	 * 查询分类匹配规则
	 * @param pk_category_keywords
	 * @return
	 * @throws DZFWarpException
	 */
	private CategoryKeywordVO[] queryCategoryKeywordVOs(String[] pk_category_keywords)throws DZFWarpException{
		CategoryKeywordVO[] vos=(CategoryKeywordVO[])singleObjectBO.queryByCondition(CategoryKeywordVO.class, "nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_category_keyword", pk_category_keywords), new SQLParameter());
		return vos;
	}
	/**
	 * 创建目录
	 * @param pk_corp
	 * @param name
	 * @param categorytype 类型2自定义 3税率 4客户
	 * @param isaccount
	 * @return
	 * @throws DZFWarpException
	 */
	private BillCategoryVO createBillCategoryVOByName(String pk_corp,String name,int categorytype,BillCategoryVO treeVO, List<BillCategoryVO> childrenTreeVOs,DZFBoolean isaccount,String period)throws DZFWarpException{

		BillCategoryVO VO = queryBillCategoryVOByName(pk_corp, name, categorytype, treeVO, isaccount,period);

		if(VO==null){
			if (categorytype == ZncsConst.CATEGORYTYPE_4 && childrenTreeVOs != null && childrenTreeVOs.size() >= 98)//销项数据按客户分组
			{
				VO = queryBillCategoryVOByName(pk_corp, "其他", categorytype, treeVO, isaccount,period);
			}
			if (VO == null)
			{
				VO=insertBillCategoryVOByName(pk_corp, name, categorytype, treeVO, childrenTreeVOs, isaccount, period);
			}
		}
		return VO;
	}
	
	private BillCategoryVO insertBillCategoryVOByName(String pk_corp,String name,int categorytype,BillCategoryVO treeVO, List<BillCategoryVO> childrenTreeVOs,DZFBoolean isaccount,String period)throws DZFWarpException{
		
		
		BillCategoryVO VO=new BillCategoryVO();
		VO.setAllowchild(DZFBoolean.FALSE);
		
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
		String categorycode = treeVO.getCategorycode();
		String thisCode = categorycode + "01";
		int i = 0;
		while (codeSet.contains(thisCode))
		{
			i++;
			thisCode = categorycode + (i < 10 ? "0" : "") + i;
		}
		if (categorytype == ZncsConst.CATEGORYTYPE_4 && i >= 99)//销项数据按客户分组
		{
			
			name = "其他";
		}
		
//		if(childrenTreeVOs!=null &&childrenTreeVOs.size() > 0){
		VO.setCategorycode(thisCode);
		VO.setShoworder(maxshoworder + 1);
//		}else{
//			VO.setCategorycode(treeVO.getCategorycode()+"01");
//			VO.setShoworder(1);
//		}
		VO.setCategorylevel(treeVO.getCategorylevel()+1);
		VO.setCategoryname(name);
		VO.setCategorytype(categorytype);
		VO.setChildlevel(treeVO.getChildlevel()-1);
		VO.setDescription("按分组条件系统自动生成");
		VO.setDr(0);
		VO.setIsaccount(isaccount);
		VO.setIsleaf(DZFBoolean.TRUE);
		VO.setPeriod(period);
		VO.setPk_corp(pk_corp);
		VO.setPk_parentcategory(treeVO.getPk_category());
		VO.setSettype(0);
		VO.setInoutflag(treeVO.getInoutflag());
		VO=(BillCategoryVO)singleObjectBO.insertVO(pk_corp, VO);
		childrenTreeVOs.add(VO);	//新建的分组目录要加入到列表中
		treeVO.setIsleaf(DZFBoolean.FALSE);
		singleObjectBO.update(treeVO,new String[]{"isleaf"});
		return VO;
	}
	
	/**
	 * 按名称查目录
	 * @param pk_corp
	 * @param name
	 * @param categorytype
	 * @param treeVO
	 * @param isaccount
	 * @return
	 * @throws DZFWarpException
	 */
	private BillCategoryVO queryBillCategoryVOByName(String pk_corp,String name,int categorytype,BillCategoryVO treeVO,DZFBoolean isaccount,String period)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and pk_parentcategory=? and categoryname=? and nvl(isaccount, 'N')=? and categorytype=? and period=?");
		sp.addParam(pk_corp);
		sp.addParam(treeVO.getPk_category());
		sp.addParam(name);
		sp.addParam(isaccount);
		sp.addParam(categorytype);
		sp.addParam(period);
		List<BillCategoryVO> list=(List<BillCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillCategoryVO.class));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 获得表体第一行
	 * @param pk_parent
	 * @return
	 * @throws DZFWarpException
	 */
	private OcrInvoiceDetailVO getFirstOcrInvoiceDetailVO(String pk_parent)throws DZFWarpException{
		String sql="select * from ynt_interface_invoice_detail where nvl(dr,0)=0 and pk_invoice=? and rowno=1";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_parent);
		List<OcrInvoiceDetailVO> list=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 查票据
	 * @param pk_bills ids
	 * @return
	 * @throws DZFWarpException
	 */
	private OcrInvoiceVO[] queryOcrInvoiceVOs(String[] pk_bills)throws DZFWarpException{
		String sql="select * from ynt_interface_invoice where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_invoice", pk_bills);
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sql, new SQLParameter(), new BeanListProcessor(OcrInvoiceVO.class));
		if(list!=null&&list.size()>0){
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(list.toArray(new OcrInvoiceVO[0]));
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
			for(int i=0;i<list.size();i++){
				if(detailMap.get(list.get(i).getPk_invoice())!=null){
					list.get(i).setChildren(detailMap.get(list.get(i).getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
				}
			}
		}
		return list.toArray(new OcrInvoiceVO[0]);
	}
	
	private List<OcrInvoiceDetailVO> queryInvoiceDetail(OcrInvoiceVO[] list) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select a.*,case when  b.categorytype in(3,4) then parentname else b.categoryname end categoryname from ynt_interface_invoice_detail a left outer join (select c.*,d.categoryname as parentname from ynt_billcategory c left outer join ynt_billcategory d on(c.pk_parentcategory=d.pk_category)) b on(a.pk_billcategory=pk_category) where nvl(a.dr,0)=0 and nvl(b.dr,0)=0");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> returnList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		return returnList;
	}
	/**
	 * 查询公司树的下级树
	 * @param pk_tree
	 * @return
	 * @throws DZFWarpException
	 */
	private List<BillCategoryVO> queryChildCategoryVOs(String pk_tree,DZFBoolean isaccout)throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_tree);
		sp.addParam(isaccout);
		BillCategoryVO[] vos=( BillCategoryVO[])singleObjectBO.queryByCondition(BillCategoryVO.class, "nvl(dr,0)=0 and pk_parentcategory=? and isaccount=? order by categorycode", sp);
		List<BillCategoryVO> list = new ArrayList<BillCategoryVO>();
		if (vos != null)
		{
			for (BillCategoryVO vo : vos)
			{
				list.add(vo);
			}
		}
		return list;
	}

	@Override
	public void updateCategoryName(String pk_category, String categoryname) throws DZFWarpException {
		String sql="update ynt_billcategory set categoryname=? where nvl(dr,0)=0 and pk_category=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_category);
		sp.addParam(categoryname);
		singleObjectBO.executeUpdate(sql, sp);
	}
	/*
	 * 重新整理目录
	 * 
	 */
	@Override
	public void updateCategoryAgain(String pk_category, String pk_parent, String pk_corp, String period) throws DZFWarpException {
		List<OcrInvoiceVO> ocrlist = null;

		if (pk_category != null && pk_category.startsWith("bank_") || pk_parent != null && pk_parent.startsWith("bank_"))
		{
			String bankcode = (pk_category.startsWith("bank_") ? pk_category.substring(5) : pk_parent.substring(5));

			BillcategoryQueryVO queryvo = new BillcategoryQueryVO();
			queryvo.setPk_corp(pk_corp);
			queryvo.setPeriod(period);
			queryvo.setIsBank(new DZFBoolean(true));
			queryvo.setBillstate(0);
			if (pk_category.startsWith("bank_"))
			{
				queryvo.setCategorycode(bankcode.equals("null") ? null : bankcode);
				
			}
			else
			{
				queryvo.setCategorycode(pk_parent);
				queryvo.setPk_category(pk_category);
				queryvo.setPk_parentcategory(pk_parent);
			}
			ocrlist = queryBankInvoiceVOs(queryvo);
		}
		else
		{
			//根据当前目录主键查询出该目录及其子目录下的所有票据
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			List<String> arrayList = new ArrayList<String>();
//			List<String> taxrateList = new ArrayList<String>();
			List<String> srList = new ArrayList<String>();
			List<String> kccgList = new ArrayList<String>();
			List<String> cbList = new ArrayList<String>();
			List<String> bankinoutList = new ArrayList<String>();
			if(!StringUtils.isEmpty(pk_category)){
				sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' start with pk_category=? connect by prior pk_category=pk_parentcategory ");
				sp.addParam(pk_category);
			}else{
				sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and pk_corp = ? and period = ? ");
				sp.addParam(pk_corp);
				sp.addParam(period);
			}
			List<BillCategoryVO> billList=(List<BillCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BillCategoryVO.class));
			if(billList !=null && billList.size() > 0){
				for (BillCategoryVO billCategoryVO : billList) {
					arrayList.add(billCategoryVO.getPk_category());
//					if(billCategoryVO.getCategorytype()==3){//税率类别
//						taxrateList.add(billCategoryVO.getPk_category());
//					}else 
					if(billCategoryVO.getCategorytype()==4){//客户类别
						if (billCategoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_SR))
						{
							srList.add(billCategoryVO.getPk_category());
						}
						else if (billCategoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_KC))
						{
							kccgList.add(billCategoryVO.getPk_category());
						}
						else if (billCategoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_CB))
						{
							cbList.add(billCategoryVO.getPk_category());
						}
						else if (billCategoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHZR) 
								|| billCategoryVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHZC))
						{
							bankinoutList.add(billCategoryVO.getPk_category());
						}
					}
				}	
			}else{
				throw new BusinessException("参数异常");
			}
			ocrlist = iPrebillService.queryOcrInvoiceVOByBillId(arrayList, pk_corp, period);
			
			//查询有没有开启参数设置（税率、客户）  开启就删除原目录
			List<ParaSetVO> paraSetList = iParaSet.queryParaSet(pk_corp);
			boolean srfz = paraSetList.get(0).getIncomeclass() == 2;
			boolean kccgfz = (paraSetList.get(0).getPurchclass() == null ? false : paraSetList.get(0).getPurchclass().booleanValue());
			boolean cbfz = (paraSetList.get(0).getCostclass() == null ? false : paraSetList.get(0).getCostclass().booleanValue());
			boolean bankinoutfz = (paraSetList.get(0).getBankinoutclass() == null ? false : paraSetList.get(0).getBankinoutclass().booleanValue());
//			if(srfl != ZncsConst.SRFL_1 && taxrateList != null && taxrateList.size() > 0){
//				updateParentIsleaf(taxrateList);//修改父级节点isleaf
//				deleteCategory(taxrateList);//删除节点
//				
//			}
			if(!srfz && srList.size() > 0){
				updateParentIsleaf(srList);
				deleteCategory(srList);
			}
			if (!kccgfz && kccgList.size() > 0)
			{
				updateParentIsleaf(kccgList);
				deleteCategory(kccgList);
			}
			if (!cbfz && cbList.size() > 0)
			{
				updateParentIsleaf(cbList);
				deleteCategory(cbList);
			}
			if (!bankinoutfz && bankinoutList.size() > 0)
			{
				updateParentIsleaf(bankinoutList);
				deleteCategory(bankinoutList);
			}
		}
		CorpVO corpVO = SystemUtil.queryCorp(pk_corp);//当前公司信息
		//删除票头和票体上的pk_category_keyword
		iPrebillService.updateInvoiceById(ocrlist);
		iPrebillService.updateInvoiceDetailByInvId(ocrlist);
		
		for (OcrInvoiceVO ocrvo : ocrlist)
		{
			ocrvo.setUpdateflag(new DZFBoolean(true));
			ocrvo.setDatasource(ZncsConst.SJLY_1);
		}
		iSchedulCategoryService.updateInvCategory(ocrlist, pk_corp, period, corpVO);
		
	}
	
	private void updateParentIsleaf(List<String> list){
		StringBuffer sb=new StringBuffer();
		sb.append("update ynt_billcategory set isleaf = 'Y' where nvl(dr,0)=0 and pk_category in ( ");
		sb.append(" select pk_parentcategory from ynt_billcategory where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_category", list.toArray(new String[0]))+" ) ");
		singleObjectBO.executeUpdate(sb.toString(), null);
	}
	
	/*
	 * 删除目录
	 * param list 类别主键集合 
	 */
	public void deleteCategory(List<String> list){
		StringBuffer sb=new StringBuffer();
		
		sb.append("delete from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and "+SqlUtil.buildSqlForIn("pk_category", list.toArray(new String[0])));
		singleObjectBO.executeUpdate(sb.toString(), null);
	}
	
	@Override
	public List<CheckOcrInvoiceVO> modifyCheckCategory(String pk_corp, String period) throws DZFWarpException {
		List<OcrInvoiceVO> arrayList = new ArrayList<OcrInvoiceVO>();
		//查出当前公司在该期间的所有票据
		List<OcrInvoiceVO> list = iPrebillService.queryOcrVOByPkcorpAndPeriod(pk_corp, period);
		//过滤掉未识别, 检测问题票据，自定义类别中的票据
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			if (!ocrInvoiceVO.getCategorycode().startsWith(ZncsConst.FLCODE_WSB)) {
//					&&! ocrInvoiceVO.getCategorycode().startsWith(ZncsConst.FLCODE_WT)
//					&&! ocrInvoiceVO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)) {
				arrayList.add(ocrInvoiceVO);
			}
	
		}
		
		//检测分类
		List<CheckOcrInvoiceVO> checkvoList = iSchedulCategoryService.testingCategory(arrayList,pk_corp);
		return checkvoList;
	}

	@Override
	public String queryParentPK(DZFBoolean isyh,OcrInvoiceVO ocrVO,String categorycode) throws DZFWarpException {
//		StringBuffer sb=new StringBuffer();
//		StringBuffer pk_categorys=new StringBuffer();
//		SQLParameter sp=new SQLParameter();
//		sb.append(" select pk_category from ynt_billcategory where nvl(dr,0)=0 "
//				+ " start with pk_category=?  connect by prior pk_parentcategory=pk_category order by categorylevel ");
//		sp.addParam(pk_billcategory);
//		List<Object[]> list = (List<Object[]>) singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
//		if(list!=null&&list.size()>0){
//			for (int i = 0; i < list.size(); i++) {
//				pk_categorys.append(list.get(i)[0]+",");
//			}
//		}
//		return pk_categorys.substring(0,pk_categorys.length()-1);
		String unitName=SystemUtil.queryCorp(ocrVO.getPk_corp()).getUnitname();
		Map<String, String> categoryMap=queryCategoryMap(ocrVO.getPeriod(), ocrVO.getPk_corp());
		if(DZFBoolean.TRUE.equals(isyh)&&categorycode.startsWith(ZncsConst.FLCODE_YHPJ)){
			String vpurchname=ocrVO.getVpurchname();//购方名称
			String vpurchtaxno=ocrVO.getVpurchtaxno();//购方账号
			String vsalename=StringUtil.isEmpty(ocrVO.getVsalename())?"":ocrVO.getVsalename();
			String vsaletaxno=ocrVO.getVsaletaxno();//销方账号
			//采购方和付款方是一个字段，销售方和收款方是一个字段
			String bankAccountNo=null;//生效的账号
			if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
				bankAccountNo=vpurchtaxno;
			}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
				bankAccountNo=vsaletaxno;
			}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
				bankAccountNo=vpurchtaxno;
			}else{//出问题了
				bankAccountNo=null;
			}
			return getAllParentCategoryKey(categoryMap, ocrVO.getPk_billcategory(),"bank_"+bankAccountNo);
		}else{
			return getAllParentCategoryKey(categoryMap, ocrVO.getPk_billcategory(),null);
		}
	}

	@Override
	public List<OcrInvoiceDetailVO> queryDetailVOs(String pk_invoice) throws DZFWarpException {
		OcrInvoiceVO[] invoices=queryOcrInvoiceVOs(new String[]{pk_invoice});
		List<OcrInvoiceDetailVO> details=queryInvoiceDetail(invoices);
		return details;
	}
	/**
	 * 查票据
	 * @param pk_bills ids
	 * @return
	 * @throws DZFWarpException
	 */
	private OcrInvoiceVO[] queryOcrInvoiceVOsByBody(String[] pk_bills)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_interface_invoice_detail where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice_detail", pk_bills));
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> bodyList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		//表体按表投主键分组
		Map<String, List<OcrInvoiceDetailVO>> detailMap=new HashMap<String, List<OcrInvoiceDetailVO>>();
		Set<String> invoiceKeys=new HashSet<String>();
		if(bodyList!=null&&bodyList.size()>0){
			for (int i = 0; i < bodyList.size(); i++) {
				invoiceKeys.add(bodyList.get(i).getPk_invoice());
				if(detailMap.containsKey(bodyList.get(i).getPk_invoice())){
					detailMap.get(bodyList.get(i).getPk_invoice()).add(bodyList.get(i));
				}else{
					List<OcrInvoiceDetailVO> tmpList=new ArrayList<OcrInvoiceDetailVO>();
					tmpList.add(bodyList.get(i));
					detailMap.put(bodyList.get(i).getPk_invoice(), tmpList);
				}
			}
		}
		String sql="select * from ynt_interface_invoice where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_invoice", invoiceKeys.toArray(new String[0]));
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sql, new SQLParameter(), new BeanListProcessor(OcrInvoiceVO.class));
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				list.get(i).setChildren(detailMap.get(list.get(i).getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
		}
		return list.toArray(new OcrInvoiceVO[0]);
	}
	/**
	 * 表体行移动分类时，如果是与表头相同的最后一条分类，则不允许移动，通过移动表头来实现表体分类的改变。
	 * @param vos
	 * @param pk_bodybills
	 * @return
	 */
	private boolean allowMoveBody(OcrInvoiceVO[] vos, String[] pk_bodybills)
	{
		
		for (OcrInvoiceVO headvo : vos)
		{
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sp.addParam(headvo.getPk_invoice());
			sp.addParam(headvo.getPk_billcategory());
			sb.append("select * from ynt_interface_invoice_detail where pk_invoice = ? and nvl(dr,0)=0 ");
			sb.append(" and not "+SqlUtil.buildSqlForIn("pk_invoice_detail", pk_bodybills));
			sb.append(" and pk_billcategory = ?");
			sb.append(" order by rowno");
			List<OcrInvoiceDetailVO> bodyList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
			if (bodyList != null && bodyList.size() > 0)
			{
				continue;
			}
			else
			{
				return false;
			}
		}
		return true;
	}
	@Override
	public void saveNewCategoryBody(String[] pk_bills, String pk_tree, String pk_corp, String period) throws DZFWarpException {
		OcrInvoiceVO[] vos = queryOcrInvoiceVOsByBody(pk_bills);
		//0、检查票据下的表体是否有生成的资产卡片，并且没有生成凭证
		if(DZFBoolean.TRUE.equals(checkHaveZckp(vos))){
			throw new BusinessException("所选票据已生成资产卡片，不能移动。");
		}
		if(DZFBoolean.TRUE.equals(checkHaveIctrade(vos))){
			throw new BusinessException("所选票据已生成出入库单，不能移动。");
		}
		Set<String> setPk_category = new HashSet<String>();
		
		//判断移动的表体是否包含着与表头相同的最后一条表体
		for (OcrInvoiceVO headvo : vos)
		{
			boolean bodyHasSameCategory = false;
			String pk_headcategory = headvo.getPk_billcategory();
			for (OcrInvoiceDetailVO detailvo : (OcrInvoiceDetailVO[])headvo.getChildren())
			{
				if (pk_headcategory.equals(detailvo.getPk_billcategory()))	//还存在未移动的与表头相同的类别
				{
					bodyHasSameCategory = true;
					break;
					
				}
			}
			if (bodyHasSameCategory && !allowMoveBody(vos, pk_bills))
			{
				throw new BusinessException("明细行不能继续移动，请您选择票据头移动分类");
			}
			if (!setPk_category.contains(pk_headcategory))
			{
				setPk_category.add(pk_headcategory);
			}
			
		}
		//判断是否是已做账票据
		BillCategoryVO[] categoryvos = (BillCategoryVO[]) singleObjectBO.queryByCondition(BillCategoryVO.class, SqlUtil.buildSqlForIn("pk_category", setPk_category.toArray(new String[0])) + " and isaccount='Y' and nvl(dr,0)=0", null);
		if (categoryvos != null && categoryvos.length > 0)
		{
			throw new BusinessException("已做账票据不能移动分类。");
		}
		//判断是否是自定义分类下的票
		BillCategoryVO[] zdyBillCategoryVOs = (BillCategoryVO[]) singleObjectBO.queryByCondition(BillCategoryVO.class, SqlUtil.buildSqlForIn("pk_category", setPk_category.toArray(new String[0])) + " and nvl(isaccount,'N')='N' and nvl(dr,0)=0", null);
		if (zdyBillCategoryVOs != null && zdyBillCategoryVOs.length > 0)
		{
			if(zdyBillCategoryVOs[0].getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)){
				throw new BusinessException("不能对自定义分类票据移动表体行");
			}
		}
		// 1、目标分类
		BillCategoryVO treeVO = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_tree);
		if (treeVO.getIsaccount() != null && treeVO.getIsaccount().booleanValue())
		{
			throw new BusinessException("目标分类 ‘" + treeVO.getCategoryname() + "’ 是已做账类型，操作非法。");
		}
		
		// 2、目标分类的孩子
		List<BillCategoryVO> childrenTreeVOs = queryChildCategoryVOs(pk_tree, DZFBoolean.FALSE);
		// 3、目标基础分类
		BaseCategoryVO baseTreeVO = (BaseCategoryVO) singleObjectBO.queryByPrimaryKey(BaseCategoryVO.class, treeVO.getPk_basecategory());
		if (treeVO != null) {
			if (treeVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_0) || treeVO.getCategorytype().equals(ZncsConst.CATEGORYTYPE_1)) {
				if (baseTreeVO.getIsleaf() == DZFBoolean.FALSE) {
					throw new BusinessException("只能移动到末级分类！");
				}
			} else {
				if (treeVO.getIsleaf() == DZFBoolean.FALSE) {// 如果不是末级要看看下级挂的是不是分组目录
					// 如果是分组就可以放到这个目录，否则报错
					if (childrenTreeVOs != null && childrenTreeVOs.size() > 0) {
						for (int i = 0; i < childrenTreeVOs.size(); i++) {
							if (!childrenTreeVOs.get(i).getCategorytype().equals(ZncsConst.CATEGORYTYPE_3) && !childrenTreeVOs.get(i).getCategorytype().equals(ZncsConst.CATEGORYTYPE_4)) {
								throw new BusinessException("只能移动到末级分类！");
							}
						}
					}
				}
			}
		} else {
			throw new BusinessException("查找树节点失败！");
		}
//		// 4、更新pk_billcategory
//		List<ParaSetVO> listParas = iParaSet.queryParaSet(pk_corp);
//		int srfl = listParas.get(0).getIncomeclass();

		// 判断是否是未识别票据
		if (treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_WSB)||treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_WT)||treeVO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)) {
			throw new BusinessException("不能移至未识别、问题票据和自定义分类");
		}
		//批量选择，如果目标分类和原分类一致，不做处理
//		if (treeVO.getCategorycode().equals(BillParentVO.getCategorycode())) {
//			throw new BusinessException("请重新选择");
//		}
		Set<String> changeSet=new HashSet<String>();
		for (int i = 0; i < vos.length; i++) {
			OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
			for (int k = 0; k < detailVos.length; k++) {
				if (!pk_tree.equals(detailVos[k].getPk_billcategory())) {
					detailVos[k].setPk_billcategory(pk_tree);
					changeSet.add(detailVos[k].getPk_category_keyword());
				}
			}
		}
		// 4、处理pk_category_keyword
		Set<String> keySet = new HashSet<String>();
		for (int i = 0; i < vos.length; i++) {
			if (!StringUtil.isEmpty(vos[i].getPk_category_keyword())) {
				if (isNeedStudy(pk_corp, vos[i], treeVO) == DZFBoolean.FALSE)
					continue;
				OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
				for (int k = 0; k < detailVos.length; k++) {
					if (changeSet.contains(detailVos[k].getPk_category_keyword())&&!keySet.contains(detailVos[k].getPk_category_keyword())) {
						
						CategoryKeywordVO[] ckVOs = queryCategoryKeywordVOs(new String[] { detailVos[k].getPk_category_keyword() });
						if (ckVOs != null && ckVOs.length > 0)
						{
							keySet.add(detailVos[k].getPk_category_keyword());
							boolean oldRecordExist = false;
							if (ckVOs[0].getPk_corp().equals(IDefaultValue.DefaultGroup)) {// 集团预制
								//查找当前集团预制规则行已经被学习到哪个分类
								CategoryKeywordVO[] oldstudiedVOs = (CategoryKeywordVO[])singleObjectBO.queryByCondition(CategoryKeywordVO.class, "pk_corp='" + pk_corp + "' and pk_category_keyword_ori='" + ckVOs[0].getPk_category_keyword() + "' and nvl(dr,0)=0", new SQLParameter());
								if (oldstudiedVOs != null && oldstudiedVOs.length > 0)
								{
									ckVOs = oldstudiedVOs;
									oldRecordExist = true;
								}
							}
							else
							{
								oldRecordExist = true;
							}
							if (oldRecordExist == false)
							{
								CategoryKeywordVO tmpVO = (CategoryKeywordVO) ckVOs[0].clone();
								tmpVO.setPk_corp(pk_corp);
								tmpVO.setPk_category_keyword_ori(ckVOs[0].getPk_category_keyword());
								tmpVO.setPk_category_keyword(null);
								if (StringUtil.isEmpty(tmpVO.getPk_basecategory())) {
									tmpVO.setPk_category(treeVO.getPk_category());
									tmpVO.setPk_basecategory(null);
								} else {
									tmpVO.setPk_category(null);
									tmpVO.setPk_basecategory(treeVO.getPk_basecategory());
								}
								tmpVO.setPriority(9);
								tmpVO = (CategoryKeywordVO) singleObjectBO.insertVO(pk_corp, tmpVO);
								detailVos[k].setPk_category_keyword(tmpVO.getPk_category_keyword());
							} else {
								if (StringUtil.isEmpty(ckVOs[0].getPk_basecategory())) {
									ckVOs[0].setPk_category(treeVO.getPk_category());
									ckVOs[0].setPk_basecategory(null);
								} else {
									ckVOs[0].setPk_category(null);
									ckVOs[0].setPk_basecategory(treeVO.getPk_basecategory());
								}
								ckVOs[0].setPriority(9);
								singleObjectBO.update(ckVOs[0]);
								detailVos[k].setPk_category_keyword(ckVOs[0].getPk_category_keyword());
							}
						}
						else
						{
							//分类匹配规则丢失，可能是无用规则，被删除了
							detailVos[k].setPk_category_keyword(null);
						}
					}
				}
			}
		}
		if (!StringUtil.isEmpty(vos[0].getPrimaryKey())) {
			// 更新表体1个值
			for (int i = 0; i < vos.length; i++) {
				OcrInvoiceDetailVO[] detailVos = (OcrInvoiceDetailVO[]) vos[i].getChildren();
				singleObjectBO.updateAry(detailVos, new String[] { "pk_billcategory", "pk_category_keyword" });
			}
		}
	}
	
	@Override
	public List<CheckOcrInvoiceVO> queryErrorDetailVOs(BillcategoryQueryVO paramVO) throws DZFWarpException {
		String categorycode=paramVO.getCategorycode();
		List<ParaSetVO> listParas=iParaSet.queryParaSet(paramVO.getPk_corp());
		DZFBoolean isyh=listParas.get(0).getBankbillbyacc();
		if(!StringUtil.isEmpty(paramVO.getPk_category())&&paramVO.getPk_category().startsWith("bank_")){
			BillCategoryVO vo=queryByCode(ZncsConst.FLCODE_YHPJ, paramVO.getPk_corp(),paramVO.getBillstate(),paramVO.getPeriod());
			paramVO.setPk_category(vo.getPk_category()); 
			paramVO.setCategorycode("bank_"+paramVO.getCategorycode());
			paramVO.setIsBank(DZFBoolean.TRUE);
		}
		if(DZFBoolean.TRUE.equals(isyh)&&!StringUtil.isEmpty(categorycode)&&categorycode.startsWith(ZncsConst.FLCODE_YHPJ)){
			paramVO.setIsBank(DZFBoolean.TRUE);
			paramVO.setCategorycode(paramVO.getPk_parentcategory());//这里存的是bank_
		}
		List<CheckOcrInvoiceVO> errorList=queryErrorInvoiceCategoryVOs(paramVO);
		return errorList;
	}

	@Override
	public int checkInvoiceForGz(String pk_corp, String period) throws DZFWarpException {
		BillcategoryQueryVO paramVO=new BillcategoryQueryVO();
 		paramVO.setBillstate(0);
		paramVO.setPk_corp(pk_corp);
		paramVO.setPeriod(period);
		int errorNum=0;
		List<InvoiceCategoryVO> invoiceList=queryInvoiceCategoryVOs(paramVO);
		if(invoiceList!=null&&invoiceList.size()>0){
			List<String> invoiceKeyList=new ArrayList<String>();
			for(int i=0;i<invoiceList.size();i++){
				if(!invoiceList.get(i).getCategorycode().startsWith(ZncsConst.FLCODE_WSB)&&!invoiceList.get(i).getCategorycode().startsWith(ZncsConst.FLCODE_WT)){
					if(invoiceList.get(i).getCategorycode().startsWith(ZncsConst.FLCODE_ZC)){
						invoiceKeyList.add(invoiceList.get(i).getPk_invoice());
					}else{
						errorNum++;
					}
				}
			}
			if(invoiceKeyList.size()>0){
				//检查资产卡片类的票据是否生成了已制证的卡片
				OcrInvoiceVO[] invoiceVOs=queryOcrInvoiceVOs(invoiceKeyList.toArray(new String[0]));
				Set<String> detailSet=new HashSet<String>();
				for (int i = 0; i < invoiceVOs.length; i++) {
					OcrInvoiceVO ocrInvoiceVO = invoiceVOs[i];
					OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren();
					for (int j = 0; detailVOs!=null&&j < detailVOs.length; j++) {
						OcrInvoiceDetailVO ocrInvoiceDetailVO = detailVOs[j];
						if(!StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_invoice_detail())){
							detailSet.add(ocrInvoiceDetailVO.getPk_invoice_detail());
						}
					}
				}
				if(detailSet.size()>0){
					String sql="select * from ynt_assetcard where nvl(dr,0)=0 and pk_voucher is not null and "+SqlUtil.buildSqlForIn("pk_invoice_detail", detailSet.toArray(new String[0]));
					List<AssetcardVO> cardList=(List<AssetcardVO>)singleObjectBO.executeQuery(sql, new SQLParameter(),new BeanListProcessor(AssetcardVO.class));
					if(cardList!=null&&cardList.size()>0){
						Set<String> voucherInvoiceList=new HashSet<String>();
						for (int i = 0; i < cardList.size(); i++) {
							AssetcardVO card = cardList.get(i);
							voucherInvoiceList.add(card.getPk_invoice());
						}
						errorNum=errorNum+invoiceKeyList.size()-voucherInvoiceList.size();
					}else{
						errorNum=errorNum+invoiceKeyList.size();
					}
				}else{
					errorNum=errorNum+invoiceKeyList.size();
				}
			}
		}
		return errorNum;
	}

	@Override
	public List<OcrInvoiceVO> queryBillsByWhere(BillcategoryQueryVO paramVO) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.*,b.pk_image_group from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where  a.pk_image_group=b.pk_image_group and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.istate !=205 ");
		sb.append(" and a.pk_corp=? ");
		sp.addParam(paramVO.getPk_corp());
		sb.append(" and a.period=?");
		sp.addParam(paramVO.getPeriod());
		if(!StringUtil.isEmpty(paramVO.getBilltype())&&!paramVO.getBilltype().equals("全部")){//票据类别
			sb.append(" and a.istate=? ");
			sp.addParam(paramVO.getBilltype());
		}
		if(paramVO.getBillstate()!=null){
			if(paramVO.getBillstate()==0){
				sb.append(" and b.istate !=100 and b.istate !=101 ");
			}else if(paramVO.getBillstate()==1){
				sb.append(" and (b.istate =100 or b.istate =101) ");
			}
		}
		if(!StringUtil.isEmpty(paramVO.getInvoicetype())){//单据类型
			sb.append(" and a.invoicetype like '%"+paramVO.getInvoicetype()+"%' ");
		}
		if(!StringUtil.isEmpty(paramVO.getBilltitle())){//票据名称
			sb.append(" and a.billtitle like '%"+paramVO.getBilltitle()+"%' ");
		}
		if(!StringUtil.isEmpty(paramVO.getVpurchname())){//付款方
			sb.append(" and a.vpurchname like '%"+paramVO.getVpurchname()+"%' ");
		}
		if(!StringUtil.isEmpty(paramVO.getVsalename())){//收款方
			sb.append(" and a.vsalename like '%"+paramVO.getVsalename()+"%' ");
		}
		if(!StringUtil.isEmpty(paramVO.getBntotaltax())){//总金额开始
			sb.append(" and to_number(a.ntotaltax)>=? ");
			sp.addParam(paramVO.getBntotaltax());
		}
		if(!StringUtil.isEmpty(paramVO.getEntotaltax())){//总金额结束
			sb.append(" and to_number(a.ntotaltax)<=? ");
			sp.addParam(paramVO.getEntotaltax());
		}
		if(!StringUtil.isEmpty(paramVO.getBdate())){//开票日期开始
			sb.append(" and (a.dinvoicedate is not null and (replace(replace(replace(a.dinvoicedate,'年',''),'月',''),'日','')>=?))");
			sp.addParam(paramVO.getBdate());
		}
		if(!StringUtil.isEmpty(paramVO.getEdate())){//开票日期结束
			sb.append(" and (a.dinvoicedate is not null and (replace(replace(replace(a.dinvoicedate,'年',''),'月',''),'日','')<=?))");
			sp.addParam(paramVO.getEdate());
		}
		if(!StringUtil.isEmpty(paramVO.getTruthindent())&&!paramVO.getTruthindent().equals("全部")){//真伪
			sb.append(" and a.truthindent=? ");
			sp.addParam(paramVO.getTruthindent());
		}
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		if(list!=null&&list.size()>0){
			String unitName=SystemUtil.queryCorp(paramVO.getPk_corp()).getUnitname();
			Map<String, String> categoryMap=queryCategoryMap2(paramVO.getPeriod(), paramVO.getPk_corp());
			List<ParaSetVO> listParas=iParaSet.queryParaSet(paramVO.getPk_corp());
			DZFBoolean isyh=listParas.get(0).getBankbillbyacc();
			Map<String, String> categoryCodeMap=queryCategoryCodeMap2(paramVO.getPeriod(), paramVO.getPk_corp());
			for(int i=0;i<list.size();i++){
				if(DZFBoolean.TRUE.equals(isyh)&&categoryCodeMap.get(list.get(i).getPk_billcategory()).startsWith(ZncsConst.FLCODE_YHPJ)){
					String vpurchname=list.get(i).getVpurchname();//购方名称
					String vpurchtaxno=list.get(i).getVpurchtaxno();//购方账号
					String vsalename=list.get(i).getVsalename();
					String vsaletaxno=list.get(i).getVsaletaxno();//销方账号
					//采购方和付款方是一个字段，销售方和收款方是一个字段
					String bankAccountNo=null;//生效的账号
					if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&(StringUtil.isEmpty(vsalename)||!unitName.startsWith(vsalename))){//购方
						bankAccountNo=vpurchtaxno;
					}else if((StringUtil.isEmpty(vpurchname)||!unitName.startsWith(vpurchname))&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//销方
						bankAccountNo=vsaletaxno;
					}else if(!StringUtil.isEmpty(vpurchname)&&unitName.startsWith(vpurchname)&&!StringUtil.isEmpty(vsalename)&&unitName.startsWith(vsalename)){//都有可能是户间转账
						bankAccountNo=vpurchtaxno;
					}else{//出问题了
						bankAccountNo=null;
					}
					list.get(i).setOcraddress(getAllParentCategoryKey(categoryMap, list.get(i).getPk_billcategory(),"bank_"+bankAccountNo));
				}else{
					list.get(i).setOcraddress(getAllParentCategoryKey(categoryMap, list.get(i).getPk_billcategory(),paramVO.getCategorycode()));
				}
			}
		}
		return list;
	}
}

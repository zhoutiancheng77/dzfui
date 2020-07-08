package com.dzf.zxkj.platform.service.zncs.impl;

import com.alibaba.fastjson.JSONObject;
import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.BDTradeVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.BdtradeAccountSchemaVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
public class SchedulCategoryServiceImpl implements ISchedulCategoryService {
	@Autowired
	SingleObjectBO singleObjectBO;
	@Autowired
	IParaSet iParaSet;
	@Autowired
	IBaseCategoryService baseCategoryService;
	@Autowired
	ICategoryKeywordService categoryKeywordService;
	@Autowired
	IPrebillService prebillService;
	@Autowired
	private IBlackList iBlackList;
	@Autowired
	private IBillcategory iBillcategory;
	@Autowired
	private IEditDirectory iEditDirectory;
	@Autowired
	private IAuxiliaryAccountService iAuxiliaryAccountService;
	@Autowired
	private IZncsNewTransService iZncsNewTransService;
	@Autowired
	private ICorpService corpService;
	@Autowired
	private RedissonDistributedLock redissonDistributedLock;

	@Override
	public List<BillCategoryVO> queryTree(BillcategoryQueryVO paramVO) throws DZFWarpException {
		String categorycode = paramVO.getCategorycode();
		// 1、如果点中的是银行票据分类且启用了银行账号参数
		if (!StringUtil.isEmpty(categorycode) && categorycode.equals(ZncsConst.FLCODE_YHPJ)) {
			List<ParaSetVO> listParas = iParaSet.queryParaSet(paramVO.getPk_corp());
			DZFBoolean isyh = listParas.get(0).getBankbillbyacc();
			if (isyh == DZFBoolean.TRUE) {
				// 返回银行账号
				List<InvoiceCategoryVO> invoiceList = queryInvoiceCategoryVOs(paramVO);// 所有票据
			}
		}
		return queryCategory(paramVO);
	}

	private List<BillCategoryVO> queryCategory(BillcategoryQueryVO paramVO) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("select a.*,0 as itype from ynt_billcategory a where nvl(a.dr,0)=0 and a.period=? and a.pk_corp=? ");
		sp.addParam(paramVO.getPeriod());
		sp.addParam(paramVO.getPk_corp());
		if (StringUtil.isEmpty(paramVO.getPk_category())) {// 查1级树
			sb.append(" and a.pk_parentcategory is null");
		} else {
			sb.append(" and a.pk_parentcategory = ?");
			sp.addParam(paramVO.getPk_category());
		}
		sb.append(" order by a.showorder");
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		fillBillCount(paramVO, list);
		return list;
	}

	/**
	 * 查出该公司的分类后，给每个分类统计当期的票据数据
	 * 
	 * @param list
	 * @throws
	 */
	private void fillBillCount(BillcategoryQueryVO paramVO, List<BillCategoryVO> list) throws DZFWarpException {
		List<InvoiceCategoryVO> invoiceList = queryInvoiceCategoryVOs(paramVO);// 所有票据
		List<BillCategoryVO> addBillList = new ArrayList<BillCategoryVO>();// 直接挂在所选分类下的票据，需要返回前台
		if (list != null && list.size() > 0) {
			for (int i = 0; i < list.size(); i++) {
				// 循环查
				String categoryname = list.get(i).getCategoryname();// 分类名字，最后要扩展出数量
				String categorycode = list.get(i).getCategorycode();// 分类编码，统计数量用
				if (invoiceList.size() == 0) {
					categoryname = categoryname + "(0)";
					list.get(i).setCategoryname(categoryname);
					list.get(i).setBillcount(0);
				} else {
					int billCount = 0;
					for (int j = 0; j < invoiceList.size(); j++) {
						if (invoiceList.get(j).getCategorycode().startsWith(categorycode)) {// 这个票属于这个分类的子集
							billCount++;
						}
						if (!StringUtil.isEmpty(paramVO.getPk_category())
								&& paramVO.getPk_category().equals(invoiceList.get(j).getPk_billcategory())) {// 这个票直接属于该分类下，把这个票也返回前端，展示票据title
							BillCategoryVO billVO = new BillCategoryVO();
							billVO.setItype(1);
							billVO.setBilltitle(invoiceList.get(j).getBilltitle());
							billVO.setPk_category(invoiceList.get(j).getPk_invoice());
							addBillList.add(billVO);
						}
					}
					categoryname = categoryname + "(" + billCount + ")";
					list.get(i).setCategoryname(categoryname);
					list.get(i).setBillcount(billCount);
				}
			}
		} else {// 如果没有分类，看看票是否有这个分类的
			for (int j = 0; j < invoiceList.size(); j++) {
				if (!StringUtil.isEmpty(paramVO.getPk_category())
						&& paramVO.getPk_category().equals(invoiceList.get(j).getPk_billcategory())) {// 这个票直接属于该分类下，把这个票也返回前端，展示票据title
					BillCategoryVO billVO = new BillCategoryVO();
					billVO.setItype(1);
					billVO.setBilltitle(invoiceList.get(j).getBilltitle());
					billVO.setPk_category(invoiceList.get(j).getPk_invoice());
					addBillList.add(billVO);
				}
			}
		}
		list.addAll(addBillList);// 把分类和票组合
	}

	/**
	 * 查询票据
	 * 
	 * @param paramVO
	 * @return
	 * @throws
	 */
	private List<InvoiceCategoryVO> queryInvoiceCategoryVOs(BillcategoryQueryVO paramVO) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(
				"select a.pk_invoice,a.pk_billcategory,c.categoryname,c.categorycode,a.billtitle from ynt_interface_invoice a,ynt_image_group b,ynt_billcategory c");
		sb.append(
				" where a.pk_image_group=b.pk_image_group and a.pk_billcategory=c.pk_category and nvl(a.dr,0)=0 and nvl(c.dr,0)=0 ");// and
																																		// nvl(b.dr,0)=0
		if (paramVO.getBillstate() == 0) {
			sb.append(" and b.istate !=100 and b.istate !=101");
		} else if (paramVO.getBillstate() == 1) {
			sb.append(" and (b.istate ==100 or b.istate ==101)");
		}
		sb.append(" and a.pk_corp=? ");
		sp.addParam(paramVO.getPk_corp());
		sb.append(" and a.period=?");
		sp.addParam(paramVO.getPeriod());
		List<InvoiceCategoryVO> list = (List<InvoiceCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(InvoiceCategoryVO.class));
		return list;
	}
	/**
	 * 判断所使用的科目方案
	 * 
	 * @return
	 */
	private Integer getAccountSchema(String pk_corp) throws DZFWarpException {
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
	/*
	 * param list 分组后的票据头集合 创建公司级分类树
	 */
	public void newSaveCorpCategory(List<OcrInvoiceVO> list, String pk_corp, String period, CorpVO corpVO)
			throws DZFWarpException {
		String requestid = UUID.randomUUID().toString();
		boolean lock = false;
		try {
			long now = System.currentTimeMillis();
			lock = redissonDistributedLock.tryGetDistributedFairLock("zncscreateTree_"+pk_corp+period);
			while (!lock && System.currentTimeMillis() - now < 5000)
			{
				Thread.sleep(499);
				lock = redissonDistributedLock.tryGetDistributedFairLock("zncscreateTree_"+pk_corp+period);
			}
			if (lock) {

				// 查询公司+期间是否有节点树
				List<BillCategoryVO> list2 = queryBillCategoryByCorpAndPeriod(pk_corp, period);
				if (list2 == null || list2.size() == 0) {// 没有公司分类树
		
					// 查询所有基础类分表中pk_corp=000001的节点并按级次排序，不包括固定资产及子类
					List<BaseCategoryVO> baseCategoryList = baseCategoryService.queryBaseCategory(pk_corp);
		
					for (BaseCategoryVO baseCategoryVO : baseCategoryList) {
		
						createBillCategory(baseCategoryVO, pk_corp, period);
					}
					// 固定资产根据科目不同新建类别不同
					List<BaseCategoryVO> gdzcList = new ArrayList<BaseCategoryVO>();
					Integer iAccountSchema = getAccountSchema(pk_corp);
					if (DzfUtil.POPULARSCHEMA.equals(iAccountSchema) || DzfUtil.CAUSESCHEMA.equals(iAccountSchema))
					{
						gdzcList = baseCategoryService.queryBaseCategoryByGdzcNotNull("00000100AA10000000000BMQ");
					}
					else
					{
						gdzcList = baseCategoryService.queryBaseCategoryByGdzcIsNull();
					}
//					if (gdzcList==null || gdzcList.size() == 0) {
//						gdzcList = baseCategoryService.queryBaseCategoryByGdzcIsNull();
//					}
					for (BaseCategoryVO baseCategoryVO : gdzcList) {
						createBillCategory(baseCategoryVO, pk_corp, period);
					}
					//复制设置信息
					copyCategorySet(pk_corp, period);
				}
			}
			else
			{
				throw new BusinessException("服务器忙，请稍后再试！");
			}
		}
		catch (Exception e)
		{
			log.error(e.getMessage(), e);
			throw new BusinessException("操作失败，请稍后再试！");
		}
		finally {
			if (lock) {
				redissonDistributedLock.releaseDistributedFairLock("zncscreateTree_"+pk_corp+period);
			}
		}
	}
	private void copyCategorySet(String pk_corp, String period) throws DZFWarpException
	{
		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(period);
		BillCategoryVO[] billcategoryvos = (BillCategoryVO[])singleObjectBO.queryByCondition(BillCategoryVO.class, "pk_corp=? and period=? and nvl(dr,0)=0", params);

		Map<String, BillCategoryVO> map = new HashMap<String, BillCategoryVO>();
		for (BillCategoryVO vo : billcategoryvos)
		{
			if(!StringUtil.isEmpty(vo.getPk_basecategory())){
				map.put(vo.getPk_basecategory(), vo);
			}
		}
		
		//复制系统预制设置, 和上个期间的类别设置属性
		params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(pk_corp);
		params.addParam(pk_corp);
		params.addParam(period);
		HashSet<String> setKeys = new HashSet<String>();
		List<CategorysetVO> listSetVOs = new ArrayList<CategorysetVO>();
		HashSet<String> headKeys = new HashSet<String>();
		CategorysetVO[] setvos = (CategorysetVO[])singleObjectBO.queryByCondition(CategorysetVO.class, "(pk_corp=? and pk_category in (select pk_category from ynt_billcategory where pk_corp=? and period=(select max(period) from ynt_billcategory where pk_corp=? and period<? and nvl(dr,0)=0) ) and nvl(dr,0)=0 and pk_basecategory is not null) and nvl(dr,0)=0", params);
		if (setvos != null && setvos.length > 0)
		{
			for (CategorysetVO setvo : setvos)
			{
				if (!StringUtil.isEmpty(setvo.getPk_basecategory())&&map.containsKey(setvo.getPk_basecategory()))
				{
					setvo.setPk_corp(pk_corp);
					headKeys.add(setvo.getPk_categoryset());
//					setvo.setPk_categoryset(null);
					setvo.setTs(new DZFDateTime());
					setvo.setPk_category(map.get(setvo.getPk_basecategory()).getPk_category());
					listSetVOs.add(setvo);
					setKeys.add(setvo.getPk_basecategory());
				}
			}
			//查表体辅助
			if(headKeys.size()>0){
				String sql="select * from ynt_categoryset_fzhs where nvl(dr,0)=0 and "+ SqlUtil.buildSqlForIn("pk_categoryset", headKeys.toArray(new String[0]));
				List<CategorysetBVO> listb=(List<CategorysetBVO>)singleObjectBO.executeQuery(sql, new SQLParameter(), new BeanListProcessor(CategorysetBVO.class));
				Map<String, List<CategorysetBVO>> detailMap = DZfcommonTools.hashlizeObject(listb, new String[] { "pk_categoryset" });
				for(int i=0;i<listSetVOs.size();i++){
					if(detailMap.get(listSetVOs.get(i).getPk_categoryset())!=null){
						CategorysetBVO[] detailVOs=detailMap.get(listSetVOs.get(i).getPk_categoryset()).toArray(new CategorysetBVO[0]);
						for (int j = 0; j < detailVOs.length; j++) {
							CategorysetBVO categorysetBVO = detailVOs[j];
							categorysetBVO.setPk_categoryset(null);
							categorysetBVO.setPk_categoryset_fzhs(null);
						}
						listSetVOs.get(i).setChildren(detailVOs);
					}
					listSetVOs.get(i).setPk_categoryset(null);
				}
			}
		}
		params = new SQLParameter();
		params.addParam(IDefaultValue.DefaultGroup);
		setvos = (CategorysetVO[])singleObjectBO.queryByCondition(CategorysetVO.class, "pk_corp=? and pk_category is null and nvl(dr,0)=0 and pk_basecategory is not null", params);
		if (setvos != null && setvos.length > 0)
		{
			for (CategorysetVO setvo : setvos)
			{

				setvo.setPk_corp(pk_corp);
				setvo.setPk_categoryset(null);
				setvo.setTs(new DZFDateTime());
				if (!setKeys.contains(setvo.getPk_basecategory()) && map.containsKey(setvo.getPk_basecategory()))
				{
					setvo.setPk_category(map.get(setvo.getPk_basecategory()).getPk_category());
					listSetVOs.add(setvo);
					setKeys.add(setvo.getPk_basecategory());
				}
			}

		}
		if (listSetVOs.size() > 0)
		{
			for(int i=0;i<listSetVOs.size();i++){
				singleObjectBO.saveObject(pk_corp, listSetVOs.get(i));
			}
//			singleObjectBO.insertVOArr(pk_corp, listSetVOs.toArray(new CategorysetVO[0]));
		}
		
	}
	/*
	 * 查询公司+期间是否存在分类树
	 * 
	 */
	public List<BillCategoryVO> queryBillCategoryByCorpAndPeriod(String pk_corp, String period) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();

		sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' ");
		if (!StringUtil.isEmpty(pk_corp)) {
			sb.append(" and pk_corp = ?");
			sp.addParam(pk_corp);
		}
		if (!StringUtil.isEmpty(period)) {
			sb.append(" and period = ? order by categorycode");
			sp.addParam(period);
		}
		List<BillCategoryVO> volist = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		return volist;
	}

	/*
	 * 根据分类编码查询主键
	 */
	private BillCategoryVO queryByCode(String categoryCode, String pk_corp, String period) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(
				"select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and categorycode = ? and pk_corp = ? and period = ? ");
		sp.addParam(categoryCode);
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> volist = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));

		return volist.size() == 0 ? null : volist.get(0);

	}

	/*
	 * 根据分类名称查询主键
	 */
	private String queryByName(String name, String pk_corp, String period) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(
				"select * from ynt_billcategory where nvl(dr,0)=0 and  categoryname = ? and pk_corp = ? and period = ? ");
		sp.addParam(name);
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> volist = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));

		return volist.size() == 0 ? null : volist.get(0).getPk_category();

	}

//	private String queryByNameAndPk(String name, String pk_corp, String period, String pk_category) {
//		StringBuffer sb = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sb.append(
//				"select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and categoryname = ? and pk_corp = ? and period = ? and pk_parentcategory=?");
//		sp.addParam(name);
//		sp.addParam(pk_corp);
//		sp.addParam(period);
//		sp.addParam(pk_category);
//		List<BillCategoryVO> volist = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
//				new BeanListProcessor(BillCategoryVO.class));
//
//		return volist.size() == 0 ? null : volist.get(0).getPk_category();
//
//	}
	private String queryPkByGroupName(String name, Map<String, List<BillCategoryVO>> mapChildBillcategoryVO, String pk_category) {
		
		
		if (mapChildBillcategoryVO.containsKey(pk_category) == false)
		{
			return null;
		}
		else
		{
			List<BillCategoryVO> list = mapChildBillcategoryVO.get(pk_category);
			for (BillCategoryVO vo : list)
			{
				if (vo.getCategoryname().equals(name))
				{
					return vo.getPk_category();
				}
			}
			return null;
		}


	}
	/**
	 * 置单据的标题名称，优先顺序 
	 * 1，firstBillTitle  
	 * 2，OcrInvoiceVO.getInvoicetype
	 * 3，OcrInvoiceVO.istate
	 * 4,lastBillTitle
	 * 5,未命名
	 * @param firstBillTitle
	 * @param invoiceVO
	 * @param lastBillTitle
	 */
	private void setBillTitle(String firstBillTitle, OcrInvoiceVO invoiceVO, String lastBillTitle)
	{
		if (StringUtil.isEmptyWithTrim(invoiceVO.getBilltitle()))
		{
			if (ZncsConst.SBZT_1.equals(invoiceVO.getIstate()))	   //无名的银行标题，用往来款
			{
				if (firstBillTitle == null)
				{
					if (StringUtil.isEmpty(invoiceVO.getVsalephoneaddr()) == false)
					{
						try {
							String[] sa = new String[] {
					    		"摘要",
					    		"用途",
					    		"附言",
					    		"备注",
					    		"费用名称",
					    		"附加信息",
					    		"操作类型",
					    		"收付标志",
					    		"业务名称",
					    		"业务种类",
					    		"业务类型",
					    		"回单类型",
					    		"交易类型",
					    		"交易名称",
					    		"凭证种类",
					    		"商户名称"};


					    	
					
							Map<String, Object> oMap = JSONObject.parseObject(invoiceVO.getVsalephoneaddr().replace("“", "\"").replace("”", "\""), Map.class);
							if (oMap != null)
							{
	label1:						for (String s : sa)
								{
									if (oMap.containsKey(s) && oMap.get(s) != null)
									{
										firstBillTitle = oMap.get(s).toString();
										break label1;
									} 
	
								}
							}
						}
						catch (Exception ex)
						{
							//不用写异常
						}
					}
				}
				
				firstBillTitle = (firstBillTitle == null ? "往来款" : firstBillTitle);
				
				if (invoiceVO.getUpdateflag() != null && invoiceVO.getUpdateflag().booleanValue())
				{
					if (invoiceVO.getChildren() != null && invoiceVO.getChildren().length == 1)
					{  
						OcrInvoiceDetailVO ocrInvoiceDetailVO = (OcrInvoiceDetailVO)invoiceVO.getChildren()[0];
						if (StringUtil.isEmptyWithTrim(ocrInvoiceDetailVO.getInvname()))
						{
							ocrInvoiceDetailVO.setInvname(firstBillTitle);
							ocrInvoiceDetailVO.setItemmny(invoiceVO.getNtotaltax());
						}
					}
					
				}
			}
			else if (ZncsConst.SBZT_3.equals(invoiceVO.getIstate()))	//发票，取*分隔后的最后部分
			{
				if (firstBillTitle == null)
				{
					firstBillTitle = invoiceVO.getVfirsrinvname();
				}
				if (StringUtil.isEmpty(firstBillTitle) == false)
				{
					firstBillTitle = OcrUtil.execInvname(firstBillTitle);
				}

			}
			invoiceVO.setBilltitle(StringUtil.isEmptyWithTrim(firstBillTitle) == false ? firstBillTitle
					: (StringUtil.isEmptyWithTrim(invoiceVO.getInvoicetype()) == false ? invoiceVO.getInvoicetype()
							: (StringUtil.isEmptyWithTrim(invoiceVO.getIstate()) == false ? invoiceVO.getIstate()
									: StringUtil.isEmptyWithTrim(lastBillTitle) == false ? lastBillTitle : "未命名")));
		}
	}
	private CategoryKeywordVO selectCategoryKeywords(int count, CategoryKeywordVO userDefineKeywords, CategoryKeywordVO systemKeywords, OcrInvoiceVO ocrInvoiceVO
			, OcrInvoiceDetailVO ocrInvoiceDetailVO, Map<String, BillCategoryVO> mapBillCategoryVO, Map<String, BillCategoryVO> mapBillCategoryVOByBaseCategoryPK)
	{
		CategoryKeywordVO categoryKeywordVO = null;
		if (userDefineKeywords != null && systemKeywords != null)
		{
			//判断系统分类匹配规则是否完全包含用户自定义匹配规则
			String userkwds = StringUtil.isEmpty(userDefineKeywords.getKeywordnames()) ? "" : userDefineKeywords.getKeywordnames();
			String systemkwds = StringUtil.isEmpty(systemKeywords.getKeywordnames()) ? "" : systemKeywords.getKeywordnames();
			String[] userwords = (!StringUtil.isEmpty(userDefineKeywords.getKeywordnames()) ? userDefineKeywords.getKeywordnames().split(",") : new String[0]);
			String[] systemwords = (!StringUtil.isEmpty(systemKeywords.getKeywordnames()) ? systemKeywords.getKeywordnames().split(",") : new String[0]);
			
			boolean contains = true;
			for (String userw : userwords)
			{
				boolean onecontains = false;
				for (String systemw : systemwords)
				{
					if (systemw.contains(userw) && systemw.length() > userw.length())
					{
						onecontains = true;
						break;
					}
				}
				if (!onecontains)
				{
					contains = false;
					break;
				}
			}
			if (!userkwds.equals(systemkwds) && contains && systemKeywords.getPriority() >= userDefineKeywords.getPriority())
			{
				//把用户自定义规则的优先级降至这行系统规则之后
				SQLParameter param = new SQLParameter();
				param.addParam(systemKeywords.getPriority() + 1);
				param.addParam(userDefineKeywords.getPk_category_keyword());
				singleObjectBO.executeUpdate("update ynt_category_keyword set priority=? where pk_category_keyword=?", param);
				categoryKeywordVO = systemKeywords;
			}
			else
			{
				categoryKeywordVO = (userDefineKeywords.getPriority() < systemKeywords.getPriority() ? userDefineKeywords : systemKeywords);
			}
			
	
		}
		else
		{
			categoryKeywordVO = (systemKeywords != null ? systemKeywords  : userDefineKeywords);
		}
		if (categoryKeywordVO != null)
		{
			String keywordnames = categoryKeywordVO.getKeywordnames();// 关键字
			
			if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3)) {
				// if(count==0){
				if (ocrInvoiceDetailVO.getRowno() == null || ocrInvoiceDetailVO.getRowno() == 1) { // 只有第一行分录匹配上才不禁问题票据
					// 查询类别表主键
					if (!StringUtil.isEmpty(categoryKeywordVO.getPk_category())) {
						ocrInvoiceVO.setPk_billcategory(categoryKeywordVO.getPk_category());
						ocrInvoiceVO.setBillcategoryname(mapBillCategoryVO.get(categoryKeywordVO.getPk_category()).getCategoryname());
						ocrInvoiceDetailVO.setPk_billcategory(categoryKeywordVO.getPk_category());
					} else {
						BillCategoryVO billCategoryVO = mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory());
						ocrInvoiceVO.setPk_billcategory(billCategoryVO.getPk_category());
						ocrInvoiceVO.setBillcategoryname(mapBillCategoryVO.get(billCategoryVO.getPk_category()).getCategoryname());
						ocrInvoiceDetailVO.setPk_billcategory(billCategoryVO.getPk_category());
					}
					ocrInvoiceVO.setPk_category_keyword(categoryKeywordVO.getPk_category_keyword());
					ocrInvoiceVO.setUpdatets(new DZFDateTime());
					if (!ZncsConst.YCXX_8.equals(ocrInvoiceVO.getErrordesc())
							&& !ZncsConst.YCXX_10.equals(ocrInvoiceVO.getErrordesc())
							&& !(ZncsConst.YCXX_8 + "," + ZncsConst.YCXX_10).equals(ocrInvoiceVO.getErrordesc()))					{
						ocrInvoiceVO.setErrordesc(null);
					}
					//置票据头名称
					setBillTitle(ocrInvoiceDetailVO.getInvname(), ocrInvoiceVO, null);	//第一行货物名称

					ocrInvoiceDetailVO.setPk_category_keyword(categoryKeywordVO.getPk_category_keyword());
					ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());
//					if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
//					{
//						// 更新主子表数据库
//						prebillService.updateOcrInv(ocrInvoiceVO);
//						prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
//						
//						
//					}
				} else {
					// 查询类别表主键
					if (!StringUtil.isEmpty(categoryKeywordVO.getPk_category())) {
						ocrInvoiceDetailVO.setPk_billcategory(categoryKeywordVO.getPk_category());
					} else {
						BillCategoryVO billCategoryVO = mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory());
						ocrInvoiceDetailVO.setPk_billcategory(billCategoryVO.getPk_category());
					}
					ocrInvoiceDetailVO.setPk_category_keyword(categoryKeywordVO.getPk_category_keyword());
					ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());
//					if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
//					{
//						prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
//						
//					}
				}
			} else {
				if (count == 0) {
					// 查询类别表主键
					if (!StringUtil.isEmpty(categoryKeywordVO.getPk_category())) {
						ocrInvoiceVO.setPk_billcategory(categoryKeywordVO.getPk_category());
						ocrInvoiceVO.setBillcategoryname(mapBillCategoryVO.get(categoryKeywordVO.getPk_category()).getCategoryname());
						ocrInvoiceDetailVO.setPk_billcategory(categoryKeywordVO.getPk_category());
					} else {
						BillCategoryVO billCategoryVO = mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory());
						ocrInvoiceVO.setPk_billcategory(billCategoryVO.getPk_category());
						ocrInvoiceVO.setBillcategoryname(mapBillCategoryVO.get(billCategoryVO.getPk_category()).getCategoryname());
						ocrInvoiceDetailVO.setPk_billcategory(billCategoryVO.getPk_category());
					}
					ocrInvoiceVO.setPk_category_keyword(categoryKeywordVO.getPk_category_keyword());
					ocrInvoiceVO.setUpdatets(new DZFDateTime());
					if (!ZncsConst.YCXX_8.equals(ocrInvoiceVO.getErrordesc())
							&& !ZncsConst.YCXX_10.equals(ocrInvoiceVO.getErrordesc())
							&& !(ZncsConst.YCXX_8 + "," + ZncsConst.YCXX_10).equals(ocrInvoiceVO.getErrordesc()))
					{
						ocrInvoiceVO.setErrordesc(null);
					}
					//置票据头名称
					setBillTitle(keywordnames, ocrInvoiceVO, null);		 //关键字名，或者单据类型或者票据类别

					ocrInvoiceDetailVO.setPk_category_keyword(categoryKeywordVO.getPk_category_keyword());
					ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());
					
					if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
					{
						if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1) && (ocrInvoiceVO.getChildren() != null && ocrInvoiceVO.getChildren().length == 1) && StringUtil.isEmptyWithTrim(ocrInvoiceDetailVO.getInvname()))//银行票据只有一行，也放到表体去
						{
							ocrInvoiceDetailVO.setInvname(ocrInvoiceVO.getBilltitle());
							ocrInvoiceDetailVO.setItemmny(ocrInvoiceVO.getNtotaltax());
						}
						// 更新主子表数据库
//						prebillService.updateOcrInv(ocrInvoiceVO);
//						prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
						
					}
				} else {
					// 查询类别表主键
					if (!StringUtil.isEmpty(categoryKeywordVO.getPk_category())) {
						ocrInvoiceDetailVO.setPk_billcategory(categoryKeywordVO.getPk_category());
					} else {
						BillCategoryVO billCategoryVO = mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory());
						ocrInvoiceDetailVO.setPk_billcategory(billCategoryVO.getPk_category());
					}
					ocrInvoiceDetailVO.setPk_category_keyword(categoryKeywordVO.getPk_category_keyword());
					ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());

				}
			}
		}
		return categoryKeywordVO;
	}
	/*
	 * 根据票据信息分类
	 */
	public List<OcrInvoiceVO> updateInvCategory(List<OcrInvoiceVO> list, String pk_corp, String period, CorpVO corpVO) {
//		long l = System.currentTimeMillis();
		List<OcrInvoiceVO> ocrList = new ArrayList<OcrInvoiceVO>();
		Map<String, String> map = categoryKeywordService.queryKeyWordMap();
		ArrayList<CategoryKeywordVO> bankList = new ArrayList<CategoryKeywordVO>();//银行规则
		ArrayList<CategoryKeywordVO> stockList = new ArrayList<CategoryKeywordVO>();//收入   库存
		ArrayList<CategoryKeywordVO> costList = new ArrayList<CategoryKeywordVO>();//费用规则
		ArrayList<CategoryKeywordVO> notBankList = new ArrayList<CategoryKeywordVO>();//非银行规则
		List<CategoryKeywordVO> allList = categoryKeywordService.queryCateKeyByAll(map, corpVO.getPk_corp(),
				period);//该公司的全部匹配规则
		for (CategoryKeywordVO categoryKeywordVO : allList) {
			if(!StringUtil.isEmpty(categoryKeywordVO.getCategorycode())){
				if(categoryKeywordVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHPJ)){
					bankList.add(categoryKeywordVO);
				}else{
					notBankList.add(categoryKeywordVO);
				}
				if(categoryKeywordVO.getCategorycode().startsWith(ZncsConst.FLCODE_FY)){
					costList.add(categoryKeywordVO);
				}
				if(categoryKeywordVO.getCategorycode().startsWith(ZncsConst.FLCODE_SR)
						||categoryKeywordVO.getCategorycode().startsWith(ZncsConst.FLCODE_KC)){
					stockList.add(categoryKeywordVO);
				}
			}
		}
		List<BlackListVO> blackListVOs = iBlackList.queryBlackListVOs(pk_corp);// 黑名单
//		Map<String, String> basepkcodeMap = queryBaseCategoryMap();
		// 公司+期间+其他 查对应的主键
		BillCategoryVO problem = queryByCode(ZncsConst.FLCODE_WT, pk_corp, period);// 问题票据
		BillCategoryVO distinguish = queryByCode(ZncsConst.FLCODE_WSB, pk_corp, period);// 未识别票据
		// 查询公司是否启用了参数设置
		List<ParaSetVO> paraSetList = iParaSet.queryParaSet(pk_corp);
		boolean srfz = paraSetList.get(0).getIncomeclass() == 2;					//收入按往来分组
		boolean kccgfz = (paraSetList.get(0).getPurchclass() == null ? false : paraSetList.get(0).getPurchclass().booleanValue());				//库存采购按往来分组
		boolean cbfz = (paraSetList.get(0).getCostclass() == null ? false : paraSetList.get(0).getCostclass().booleanValue());					//成本按往来分组
		
		boolean bankinoutfz = (paraSetList.get(0).getBankinoutclass() == null ? false : paraSetList.get(0).getBankinoutclass().booleanValue());	//银行转入转出按往来分组
		
		
		//判断票据来源是否是智能识别
		Map<String, List<OcrInvoiceDetailVO>> detailMap = new HashMap<String, List<OcrInvoiceDetailVO>>();
		if(list.get(0).getUpdateflag().equals(DZFBoolean.TRUE)){
			// 查询该票据的所有详细信息
			List<OcrInvoiceDetailVO> invDetailList = prebillService.queryDetailByInvList(list);
			detailMap = DZfcommonTools.hashlizeObject(invDetailList,
					new String[] { "pk_invoice" });
		}
		//生成公司票据类别表map
		List<BillCategoryVO> listBillCategoryVO = queryBillCategoryByCorpAndPeriod(pk_corp, period);
		Map<String, BillCategoryVO> mapBillCategoryVO = new HashMap<String, BillCategoryVO>();
		Map<String, List<BillCategoryVO>> mapChildBillcategoryVO = new HashMap<String, List<BillCategoryVO>>();
		Map<String, BillCategoryVO> mapBillCategoryVOByBaseCategoryPK = new HashMap<String, BillCategoryVO>();
		for (BillCategoryVO vo : listBillCategoryVO)
		{
			mapBillCategoryVO.put(vo.getPk_category(), vo);
			if (StringUtil.isEmpty(vo.getPk_basecategory()) == false)
			{
				mapBillCategoryVOByBaseCategoryPK.put(vo.getPk_basecategory(), vo);
			}
			if (StringUtil.isEmpty(vo.getPk_parentcategory()) == false)
			{
				if (mapChildBillcategoryVO.containsKey(vo.getPk_parentcategory()))
				{
					mapChildBillcategoryVO.get(vo.getPk_parentcategory()).add(vo);
				}
				else
				{
					List<BillCategoryVO> childlist =  new ArrayList<BillCategoryVO>();
					childlist.add(vo);
					mapChildBillcategoryVO.put(vo.getPk_parentcategory(), childlist);
				}
				
				
			}
		}
		//生成行业map
		BDTradeVO[] tradevos = (BDTradeVO[])singleObjectBO.queryByCondition(BDTradeVO.class, "pk_corp='000001' and nvl(dr,0)=0", null);
		Map<String, BDTradeVO> mapTradeVO = new HashMap<String, BDTradeVO>();
		for (BDTradeVO vo : tradevos)
		{
			mapTradeVO.put(vo.getPk_trade(), vo);
		}
		//查询职员辅助核算
		AuxiliaryAccountBVO[] staffvos = iAuxiliaryAccountService.queryB(AuxiliaryConstant.ITEM_STAFF, pk_corp, null);
		Map<String, AuxiliaryAccountBVO> mapStaff = new HashMap<String, AuxiliaryAccountBVO>();
		if (staffvos != null)
		{
			for (AuxiliaryAccountBVO vo : staffvos)
			{
				if (StringUtil.isEmpty(vo.getName()) == false)
				{
					mapStaff.put(vo.getName(), vo);
				}
			}
		}
		List<OcrInvoiceDetailVO> detailList = null;
		// 循环所有主票据
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			
			try {
				detailList = new ArrayList<OcrInvoiceDetailVO>();
				if(ocrInvoiceVO.getUpdateflag().equals(DZFBoolean.TRUE)){
					detailList = detailMap.get(ocrInvoiceVO.getPk_invoice());
					if (detailList != null && detailList.size() > 0)
					{
						ocrInvoiceVO.setChildren(detailList.toArray(new OcrInvoiceDetailVO[0]));
					}
					if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_4))
					{
						ocrInvoiceVO.setPk_billcategory(distinguish.getPk_category());
						//置票据头名称
						setBillTitle(null, ocrInvoiceVO, ZncsConst.SBZT_4);
						if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
						{
							// 修改主子表数据库
							prebillService.updateOcrInv(ocrInvoiceVO);
						}
						continue;
					}
					
					//票据异常信息查验（收付款方与公司名称不一致、开票日期晚于当前账期、过期票据）
					if(!checkNoError(ocrInvoiceVO, corpVO, problem.getPk_category())){
						continue;
					}
					
					// 检查重复票据
					if(checkIsRepeat(ocrInvoiceVO, pk_corp, problem.getPk_category())){
						continue;
					}
					
				}else{
					//处理进销项银行对账单数据
					if(ocrInvoiceVO.getChildren()!=null&&ocrInvoiceVO.getChildren().length>0){
						detailList.addAll(Arrays.asList((OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren()));
					}else{
						detailList.add(new OcrInvoiceDetailVO());
					}
				}
				
				int count = 0;
				if (detailList != null) {

					ocrInvoiceVO.setChildren(detailList.toArray(new OcrInvoiceDetailVO[0]));
					for (OcrInvoiceDetailVO ocrInvoiceDetailVO : detailList) {
						CategoryKeywordVO userDefineKeywordVO = null;	//先匹配上的用户移动学习出的自定义匹配规则vo
						CategoryKeywordVO systemKeywordVO = null;		//系统预制的匹配规则vo
						
						// 处理黑名单
						if (blackListVOs != null && blackListVOs.size() > 0) {
							boolean isBlacklist = IsBlacklist(ocrInvoiceVO, ocrInvoiceDetailVO, blackListVOs, corpVO,
									problem);
							if (isBlacklist) {
								if(ocrInvoiceVO.getUpdateflag().equals(DZFBoolean.FALSE)){
									ocrList.add(ocrInvoiceVO);
								}
								break;
							}
						}

						// 根据匹配规则分类
						if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1)) {// 银行单据
							for (CategoryKeywordVO categoryKeywordVO : bankList) {

								boolean thismatching = isMatching(ocrInvoiceVO, ocrInvoiceDetailVO, categoryKeywordVO,
										corpVO, count, mapTradeVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK, mapStaff);
								if (thismatching)
								{
									if (categoryKeywordVO.getPk_corp().equals(IDefaultValue.DefaultGroup) == false)
									{
										if (userDefineKeywordVO == null)
										{
											userDefineKeywordVO = categoryKeywordVO;
										}
									}
									else// 匹配成功,并且是预制分类匹配规则
									{
										systemKeywordVO = categoryKeywordVO;
										break;
									}
								}
							}
							if (userDefineKeywordVO != null || systemKeywordVO != null)	//匹配到规则了
							{
								//选择合适的分类匹配规则，如果预制规则包含用户自定义匹配规则，则换回预制规则
								CategoryKeywordVO categoryKeywordVO = selectCategoryKeywords(count, userDefineKeywordVO, systemKeywordVO, ocrInvoiceVO, ocrInvoiceDetailVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK);
								if(ocrInvoiceVO.getUpdateflag().equals(DZFBoolean.FALSE)){

									ocrList.add(ocrInvoiceVO);
								}
								BillCategoryVO categoryvo = (StringUtil.isEmpty(categoryKeywordVO.getPk_basecategory()) == false ? mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory()) : mapBillCategoryVO.get(categoryKeywordVO.getPk_category()));

								String categoryCode = categoryvo.getCategorycode();
								if (bankinoutfz && (categoryCode.startsWith("1210") || categoryCode.startsWith("1211"))) {	//银行转入转出按往来分组					
									SaleCategoryByCustomer(ocrInvoiceVO, ocrInvoiceDetailVO,
											categoryvo, pk_corp, period, corpVO.getUnitname(), mapBillCategoryVO, mapChildBillcategoryVO);

								}
								count++;
							}
						} else if (ocrInvoiceVO.getPjlxstatus() != null && ocrInvoiceVO.getPjlxstatus() == 21	// 库存
								&& ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3)) {// 库存
							for (CategoryKeywordVO categoryKeywordVO : stockList) {
								boolean thismatching = isMatching(ocrInvoiceVO, ocrInvoiceDetailVO, categoryKeywordVO,
										corpVO, count, mapTradeVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK, mapStaff);
								if (thismatching)
								{
									if (categoryKeywordVO.getPk_corp().equals(IDefaultValue.DefaultGroup) == false)
									{
										if (userDefineKeywordVO == null)
										{
											userDefineKeywordVO = categoryKeywordVO;
										}
									}
									else// 匹配成功,并且是预制分类匹配规则
									{

										systemKeywordVO = categoryKeywordVO;
										break;
									}
								}
							}
							if (userDefineKeywordVO != null || systemKeywordVO != null)	//匹配到规则了
							{
								//选择合适的分类匹配规则，如果预制规则包含用户自定义匹配规则，则换回预制规则
								CategoryKeywordVO categoryKeywordVO = selectCategoryKeywords(count, userDefineKeywordVO, systemKeywordVO, ocrInvoiceVO, ocrInvoiceDetailVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK);
//								if (srfl.equals(ZncsConst.SRFL_0)) {
//									
//									
//								} else if (srfl.equals(ZncsConst.SRFL_1)) {// 按税率分组
//									BillCategoryVO categoryvo = (StringUtil.isEmpty(categoryKeywordVO.getPk_basecategory()) == false ? mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory()) : mapBillCategoryVO.get(categoryKeywordVO.getPk_category()));
//									
//									SaleCategoryByTaxrate(ocrInvoiceVO, ocrInvoiceDetailVO,
//											categoryvo, pk_corp, period, mapBillCategoryVO, mapChildBillcategoryVO);
//
//								} else 
								if (kccgfz) {//库存采购按往来客户分组
									
									BillCategoryVO categoryvo = (StringUtil.isEmpty(categoryKeywordVO.getPk_basecategory()) == false ? mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory()) : mapBillCategoryVO.get(categoryKeywordVO.getPk_category()));
									
									SaleCategoryByCustomer(ocrInvoiceVO, ocrInvoiceDetailVO,
											categoryvo, pk_corp, period, corpVO.getUnitname(), mapBillCategoryVO, mapChildBillcategoryVO);

								}
								count++;
								break; // 没有按客户税率分组
							}
						} else if (ocrInvoiceVO.getPjlxstatus() != null && ocrInvoiceVO.getPjlxstatus() == 22	// 费用
								&& !ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1)
								&& !OcrUtil.isSameCompany(corpVO.getUnitname(), ocrInvoiceVO.getVsalename())) {// 费用
							// 若选择“费用”上传票据（仅针对增值税发票及c其它发票中收款方不是本公司的票据）仅在费用范围内根据费用分类下的分类匹配规则进行分类；(代开)
							for (CategoryKeywordVO categoryKeywordVO : costList) {
								boolean thismatching = isMatching(ocrInvoiceVO, ocrInvoiceDetailVO, categoryKeywordVO,
										corpVO, count, mapTradeVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK, mapStaff);
								if (thismatching)// 匹配成功
								{
									if (categoryKeywordVO.getPk_corp().equals(IDefaultValue.DefaultGroup) == false)
									{
										if (userDefineKeywordVO == null)
										{
											userDefineKeywordVO = categoryKeywordVO;
										}
									}
									else// 匹配成功,并且是预制分类匹配规则
									{

										systemKeywordVO = categoryKeywordVO;
										break;
									}
								}
							}
							if (userDefineKeywordVO != null || systemKeywordVO != null)	//匹配到规则了
							{
								//选择合适的分类匹配规则，如果预制规则包含用户自定义匹配规则，则换回预制规则
								selectCategoryKeywords(count, userDefineKeywordVO, systemKeywordVO, ocrInvoiceVO, ocrInvoiceDetailVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK);

								count++;
							}
						} else {// 非银行
							for (CategoryKeywordVO categoryKeywordVO : notBankList) {


								boolean thismatching = isMatching(ocrInvoiceVO, ocrInvoiceDetailVO, categoryKeywordVO,
										corpVO, count, mapTradeVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK, mapStaff);
								if (thismatching)// 匹配成功
								{
									if (categoryKeywordVO.getPk_corp().equals(IDefaultValue.DefaultGroup) == false)
									{
										if (userDefineKeywordVO == null)
										{
											userDefineKeywordVO = categoryKeywordVO;
										}
									}
									else// 匹配成功,并且是预制分类匹配规则
									{

										systemKeywordVO = categoryKeywordVO;
										break;
									}
									
									
								}
							}
							if (userDefineKeywordVO != null || systemKeywordVO != null)	//匹配到规则了
							{
								//选择合适的分类匹配规则，如果预制规则包含用户自定义匹配规则，则换回预制规则
								CategoryKeywordVO categoryKeywordVO = selectCategoryKeywords(count, userDefineKeywordVO, systemKeywordVO, ocrInvoiceVO, ocrInvoiceDetailVO, mapBillCategoryVO, mapBillCategoryVOByBaseCategoryPK);
								
								BillCategoryVO categoryvo = (StringUtil.isEmpty(categoryKeywordVO.getPk_basecategory()) == false ? mapBillCategoryVOByBaseCategoryPK.get(categoryKeywordVO.getPk_basecategory()) : mapBillCategoryVO.get(categoryKeywordVO.getPk_category()));
								String categoryCode = categoryvo.getCategorycode();
								
//								if (srfl.equals(ZncsConst.SRFL_0)) { // 没有按客户税率分组
//
//								} else if (srfl.equals(ZncsConst.SRFL_1)
//										&& (categoryCode.startsWith("10") || categoryCode.startsWith("11"))) {// 按税率分组
//									SaleCategoryByTaxrate(ocrInvoiceVO, ocrInvoiceDetailVO,
//											categoryvo, pk_corp, period, mapBillCategoryVO, mapChildBillcategoryVO);
//
//								} else 
								if (srfz && categoryCode.startsWith("10") 															//收入按往来客户分组
										|| kccgfz && categoryCode.startsWith("11")													//库存采购按往来客户分组
										|| cbfz && categoryCode.startsWith("14")) {													//成本按往来客户分组
									SaleCategoryByCustomer(ocrInvoiceVO, ocrInvoiceDetailVO,
											categoryvo, pk_corp, period, corpVO.getUnitname(), mapBillCategoryVO, mapChildBillcategoryVO);

								}
								if(ocrInvoiceVO.getUpdateflag().equals(DZFBoolean.FALSE)){
									if(ocrInvoiceVO.getChildren()!=null&&ocrInvoiceVO.getChildren().length>0){
										OcrInvoiceDetailVO[] ocrDetailArray=(OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren();
										for (int i = 0; i < ocrDetailArray.length; i++) {
											if(StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_invoice_detail()) == false && StringUtil.isEmpty(ocrDetailArray[i].getPk_invoice_detail()) == false && ocrInvoiceDetailVO.getPk_invoice_detail().equals(ocrDetailArray[i].getPk_invoice_detail())){
												ocrDetailArray[i].setPk_billcategory(ocrInvoiceDetailVO.getPk_billcategory());
												ocrDetailArray[i].setPk_category_keyword(ocrInvoiceDetailVO.getPk_category_keyword());
											}
										}
									}
									ocrList.add(ocrInvoiceVO);
								}
								count++;
							}

						}
						// 到这里都没有匹配上 问题票据 无法分类
						if (StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_billcategory())) {
							ocrInvoiceDetailVO.setPk_billcategory(problem.getPk_category());
							ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());
//							if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
//							{
//								prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
//							}else{//不是智能识别数据
//								if(ocrInvoiceVO.getChildren()!=null&&ocrInvoiceVO.getChildren().length>0){
//									OcrInvoiceDetailVO[] ocrDetailArray=(OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren();
//									for (int i = 0; i < ocrDetailArray.length; i++) {
//										if(ocrInvoiceDetailVO.getPk_invoice_detail().equals(ocrDetailArray[i].getPk_invoice_detail())){
//											ocrDetailArray[i].setPk_billcategory(ocrInvoiceDetailVO.getPk_billcategory());
//											ocrDetailArray[i].setPk_category_keyword(ocrInvoiceDetailVO.getPk_category_keyword());
//										}
//									}
//								}
//							}
						}
					}

				}
				

				// 到这里都没有匹配上 问题票据 无法分类
				if (StringUtil.isEmpty(ocrInvoiceVO.getPk_billcategory())) {

					ocrInvoiceVO.setPk_billcategory(problem.getPk_category());
					ocrInvoiceVO.setErrordesc(ZncsConst.YCXX_2);
					ocrInvoiceVO.setUpdatets(new DZFDateTime());
					ocrInvoiceVO.setBillcategoryname(problem.getCategoryname());
					if(ocrInvoiceVO.getUpdateflag().equals(DZFBoolean.TRUE)){//智能识别数据
						//置票据头名称
						setBillTitle(null, ocrInvoiceVO, ZncsConst.YCXX_2);
					}
					if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
					{
//						prebillService.updateOcrInv(ocrInvoiceVO);
						
					}else{//不是智能识别数据
						ocrList.add(ocrInvoiceVO);
					}
				}

			} catch (Exception e) {
				log.error(e.getMessage(), e);
				ocrInvoiceVO.setPk_billcategory(problem.getPk_category());
				ocrInvoiceVO.setErrordesc(ZncsConst.YCXX_3);
				ocrInvoiceVO.setUpdatets(new DZFDateTime());
				ocrInvoiceVO.setBillcategoryname(problem.getCategoryname());
				//置票据头名称
				if (StringUtil.isEmpty(ocrInvoiceVO.getBilltitle()))
				{
					setBillTitle(null, ocrInvoiceVO, ZncsConst.YCXX_2);
				}
				if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
				{
//					prebillService.updateOcrInv(ocrInvoiceVO);
					
				}else{//不是智能识别数据
					ocrList.add(ocrInvoiceVO);
				}
			}
			//表头不是未识别和问题票据，表体中的问题票据行和未分类行更新为表头类型
			if (detailList != null && detailList.size() > 1 && ocrInvoiceVO.getPk_billcategory().equals(problem.getPk_category()) == false &&  ocrInvoiceVO.getPk_billcategory().equals(distinguish.getPk_category()) == false) 
			{
//				List<OcrInvoiceDetailVO> updatelist = new ArrayList<OcrInvoiceDetailVO>();
				for (OcrInvoiceDetailVO detailvo : detailList)
				{
					if (detailvo.getPk_billcategory() == null || problem.getPk_category().equals(detailvo.getPk_billcategory()))
					{
						detailvo.setPk_billcategory(ocrInvoiceVO.getPk_billcategory());
//						updatelist.add(detailvo);
					}
				}
//				if (updatelist.size() > 0 && ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
//				{
//					singleObjectBO.updateAry(updatelist.toArray(new OcrInvoiceDetailVO[0]));
//				}
			}
			//改为批量更新 ztc 2019-03-13
			if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
			{
				prebillService.updateOcrInv(ocrInvoiceVO);
				if(detailList!=null&&detailList.size()>0){
					singleObjectBO.updateAry(detailList.toArray(new OcrInvoiceDetailVO[0]));
				}
			}
			
//			System.out.println("\nwebid:" + ocrInvoiceVO.getWebid() + ",现在运行了" + ((System.currentTimeMillis() - l) / 1000) + "秒");
		}
		if (StringUtil.isEmpty(corpVO.getZipcode()) == false && "autocategory".equals(corpVO.getZipcode()))
		{
			SQLParameter param = new SQLParameter();
			param = new SQLParameter();
			param.addParam(pk_corp);
			param.addParam(period);
			param.addParam(pk_corp);
			String strCondition = "pk_invoice in (select a.pk_invoice " +
                    " from ynt_interface_invoice a, ynt_image_group b " +
                    " where a.pk_corp = ? " +
                    " and a.period = ? " +
                    " and a.pk_billcategory is null " +
                    " and nvl(a.dr, 0) = 0 " +
                    " and b.pk_corp = ? " +
                    " and a.pk_image_group = b.pk_image_group" +
                    " and nvl(b.dr, 0) = 0" +
                    " and b.istate in (100, 101))";
			
			OcrInvoiceVO[] vos = (OcrInvoiceVO[])singleObjectBO.queryByCondition(OcrInvoiceVO.class, strCondition, param);
			if (vos != null && vos.length > 0)
			{
				List<String> ids = new ArrayList<String>();
//				List<String> imggrpids = new ArrayList<String>();
				for (OcrInvoiceVO vo : vos)
				{
					ids.add(vo.getPk_invoice());
//					imggrpids.add(vo.getPk_image_group());
				}
				//更新老业务类型已制证，又在新工作台上传票，无法分类，显示到识别中的票据
			 	String sql = "update ynt_interface_invoice " +
					" set pk_billcategory = ?, billtitle=case when billtitle is null then '已制证历史票据' else billtitle end, errordesc='该张图片对应的' || case when istate='增值税发票' then '进/销项发票' when istate='b银行票据' then '银行对账单' else '票据' end || '可能已用旧业务类型生成凭证, 不能执行删除凭证操作。' " +
					" where " + SqlUtil.buildSqlForIn("pk_invoice", ids.toArray(new String[0]));
			 	
				//2、未制证分类
				Map<String, BillCategoryVO> falseMap=iZncsNewTransService.queryCategoryVOs_IsAccount(pk_corp, period, "N");
				//3、已制证分类
				Map<String, BillCategoryVO> trueMap=iZncsNewTransService.queryCategoryVOs_IsAccount(pk_corp, period, "Y");
				//4、创建差异已制证分类树，并得到最新的已制证分类树
				trueMap= iZncsNewTransService.newInsertCategoryVOs(falseMap, trueMap, pk_corp + period);
			 	String pk_voucheredProblem = trueMap.get("问题票据").getPk_category();// 问题票据
			 	
			 	param = new SQLParameter();
				param.addParam(pk_voucheredProblem);

			 	singleObjectBO.executeUpdate(sql, param);
			

			}
		}
//		System.out.println("耗时" + ((System.currentTimeMillis() - l) / 1000) + "秒");
		return ocrList;
	}

	private boolean inOutFlagCheck(OcrInvoiceVO ocrInvoiceVO, Integer iflag, String corpname, String checkNameInBill, Map<String, AuxiliaryAccountBVO> mapStaff) {
		if (iflag.equals(ZncsConst.SFBZ_3)) { // 必须为空
			return StringUtil.isEmpty(checkNameInBill);
		} else if (iflag.equals(ZncsConst.SFBZ_1)) { // 非本公司
			return (StringUtil.isEmptyWithTrim(checkNameInBill) == false && !mapStaff.containsKey(checkNameInBill) && checkNameInBill.length() > 5 && !OcrUtil.isSameCompany(corpname, checkNameInBill));

		} else if (iflag.equals(ZncsConst.SFBZ_2)) { // 个人
			String staffname = ocrInvoiceVO.getStaffname();
			return (!StringUtil.isEmptyWithTrim(staffname) || !StringUtil.isEmptyWithTrim(checkNameInBill) && mapStaff.containsKey(checkNameInBill));
		} else if (iflag.equals(ZncsConst.SFBZ_0)) { // 本公司
			return (OcrUtil.isSameCompany(corpname, checkNameInBill));

		}
		// SFBZ_4 _忽略
		return true;
	}
	/*
	 * 重复票据查验
	 */
	private boolean checkIsRepeat(OcrInvoiceVO ocrInvoiceVO, String pk_corp, String pk_Problem) {
		// 重复票据 增值税发票用发票号码+发票代码校验唯一 银行票据用uniquecode。。。。。
		if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1) || ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3)) {
			//查询除当前票以外的信息相同的票
			List<OcrInvoiceVO> isOnlyList = prebillService.queryOcrVOIsOnly(ocrInvoiceVO, pk_corp);
			if (isOnlyList != null && isOnlyList.size()>0) {
				List<OcrInvoiceVO> ocrList = new ArrayList<OcrInvoiceVO>();
				for (OcrInvoiceVO ocr : isOnlyList) {
					if (ocr.getPk_billcategory() != null) {
						ocrList.add(ocr);
					}
				}
				if (ocrList != null || ocrList.size() != 0) {
					if (StringUtil.isEmpty(ocrInvoiceVO.getBilltitle()))
					{
						setBillTitle(null, ocrInvoiceVO, ocrInvoiceVO.getIstate());
					}
					ocrInvoiceVO.setErrordesc(ZncsConst.YCXX_4);
					if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
					{
						ocrInvoiceVO.setPk_billcategory(pk_Problem);// 问题票据
						ocrInvoiceVO.setUpdatets(new DZFDateTime());

						prebillService.updateOcrInv(ocrInvoiceVO);
						
						if(ocrInvoiceVO.getChildren() != null && ocrInvoiceVO.getChildren().length > 0){
							singleObjectBO.updateAry(ocrInvoiceVO.getChildren());
						}
					}
					return true;
				}
			}
		}
		return false;
	}
	/*
	 * 异常信息查验
	 * true : 没有错误
	 * false：包含错误
	 */
	private boolean checkNoError(OcrInvoiceVO ocrInvoiceVO,CorpVO corpVO,String pk_Problem){
		//过期票据
		/*if(!StringUtil.isEmpty(ocrInvoiceVO.getErrordesc2())&&ocrInvoiceVO.getErrordesc2().equals("过期发票")){
			ocrInvoiceVO.setPk_billcategory(pk_isBlacklist);// 问题票据
			ocrInvoiceVO.setUpdatets(new DZFDateTime());
			prebillService.updateOcrInv(ocrInvoiceVO);
			return false;
		}*/
		//清理errdesc2的"金额为空"描述
		if (ZncsConst.YCXX_7.equals(ocrInvoiceVO.getErrordesc2()))
		{
			ocrInvoiceVO.setErrordesc2(null);
		}
		
		String errormsg = null;
		//金额为空
		if (StringUtil.isEmptyWithTrim(ocrInvoiceVO.getNtotaltax())&&!StringUtil.isEmptyWithTrim(ocrInvoiceVO.getErrordesc2())&&ocrInvoiceVO.getErrordesc2().indexOf(ZncsConst.YCXX_7)==-1)
		{
			errormsg = ZncsConst.YCXX_7;
			

		}
		//开票日期晚于当前账期
		// 取消税收完税证明  202007
		if(!StringUtil.isEmpty(ocrInvoiceVO.getPeriod())&&!StringUtil.isEmpty(ocrInvoiceVO.getDinvoicedate())&&
		!StringUtil.isEmpty(ocrInvoiceVO.getInvoicetype())&&!ocrInvoiceVO.getInvoicetype().equals("b税收完税证明")){
			//检查日期是否合法，转换试验
//			DZFDate trueDate = null;
			try {
//				trueDate = new DZFDate(ocrInvoiceVO.getDinvoicedate().replace("年", "-").replace("月", "-").replace("日", ""));
				String dinvoicedate = ocrInvoiceVO.getDinvoicedate().replace("年", "").substring(0, 6);
				String period = ocrInvoiceVO.getPeriod().replace("-", "");
				if(dinvoicedate.compareTo(period)>0){
					errormsg = (errormsg == null ? ZncsConst.YCXX_6 : errormsg + "," + ZncsConst.YCXX_6);

					
				}
			}
			catch (Exception ex)
			{
				errormsg = (errormsg == null ? ZncsConst.YCXX_9 : errormsg + "," + ZncsConst.YCXX_9);
			}
			
		}
		// 收付款方与公司名称不一致
		if (!StringUtil.isEmpty(ocrInvoiceVO.getIstate())
				&& (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3)
						|| ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_2)	//暂时所有票种都判断是否本公司票据，
						|| ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1))
				&& !OcrUtil.isSameCompany(corpVO.getUnitname(), ocrInvoiceVO.getVsalename())
				&& !OcrUtil.isSameCompany(corpVO.getUnitname(), ocrInvoiceVO.getVpurchname())) {
			errormsg = (errormsg == null ? ZncsConst.YCXX_5 : errormsg + "," + ZncsConst.YCXX_5);
		}
		//销项发票应在开票期间入账， 2017年7月1日后增值税发票购买方必须填写纳税人识别号
		if (!StringUtil.isEmpty(ocrInvoiceVO.getIstate())
				&& ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3) 
				&& StringUtil.isEmpty(ocrInvoiceVO.getDinvoicedate()) == false)
		{
			try {
				DZFDate date = new DZFDate(ocrInvoiceVO.getDinvoicedate().replace("年", "-").replace("月", "-").replace("日", ""));
				
				//销项发票应在开票期间入账
				if (OcrUtil.isSameCompany(corpVO.getUnitname(), ocrInvoiceVO.getVsalename()))
				{
					if (date.toString().startsWith(ocrInvoiceVO.getPeriod()) == false)
						errormsg = (errormsg == null ? ZncsConst.YCXX_8 : errormsg + "," + ZncsConst.YCXX_8);
				}
				//2017年7月1日后增值税发票购买方必须填写纳税人识别号
				if (date.compareTo(new DZFDate("2017-07-01")) >= 0
						&& StringUtil.isEmptyWithTrim(ocrInvoiceVO.getVpurchtaxno()) 
						&& !StringUtil.isEmpty(ocrInvoiceVO.getVpurchname())
						&& ocrInvoiceVO.getVpurchname().trim().length() > 5){	//5个字以内算个人姓名，大于5个字默认算公司
					errormsg = (errormsg == null ? ZncsConst.YCXX_10 : errormsg + "," + ZncsConst.YCXX_10);
				}
			}
			catch (Exception ex)
			{
				log.error(ex.getMessage(), ex);
			}
		}


		ocrInvoiceVO.setErrordesc(errormsg);

		String checkerrormsg = (errormsg == null ? "" : errormsg.replace(ZncsConst.YCXX_8, "").replace(ZncsConst.YCXX_10, "").replace("",  "").replace(",", "")) + (!StringUtil.isEmpty(ocrInvoiceVO.getErrordesc2()) && ocrInvoiceVO.getErrordesc2().contains(ZncsConst.YCXX_11)? ocrInvoiceVO.getErrordesc2() : "");
		//设置表头
		if (!StringUtil.isEmptyWithTrim(checkerrormsg) && StringUtil.isEmpty(ocrInvoiceVO.getBilltitle()))
		{
			setBillTitle(null, ocrInvoiceVO, null);  //设置问题票据的表头

		}
		if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
		{
			if (!StringUtil.isEmptyWithTrim(checkerrormsg) && ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_4) == false) //不是未识别, 仅有销项票入账期间不对，不归入问题票据
			{	
				ocrInvoiceVO.setPk_billcategory(pk_Problem);// 问题票据
			}
			ocrInvoiceVO.setUpdatets(new DZFDateTime());
			prebillService.updateOcrInv(ocrInvoiceVO);
			
			if(ocrInvoiceVO.getChildren() != null && ocrInvoiceVO.getChildren().length > 0){
				singleObjectBO.updateAry(ocrInvoiceVO.getChildren());
			}

		}

		return StringUtil.isEmptyWithTrim(checkerrormsg);
	}

	/*
	 * 根据票据主表信息，详细信息，判断与匹配规则是否符合00000100000001rQxNK7002B
	 */
	private boolean isMatching(OcrInvoiceVO ocrInvoiceVO, OcrInvoiceDetailVO ocrInvoiceDetailVO,
			CategoryKeywordVO categoryKeywordVO, CorpVO corpVO, int count, Map<String, BDTradeVO> mapTradeVO, Map<String, BillCategoryVO> mapBillCategoryVO, Map<String, BillCategoryVO> mapBillCategoryVOByBaseCategoryPK, Map<String, AuxiliaryAccountBVO> mapStaff) {
		boolean billsourceFlag = false;
		boolean aicategoryFlag = false;
		boolean aibilltypeFlag = false;
		boolean keywordnamesFlag = true;
		Integer billsource = categoryKeywordVO.getBillsource();// 数据来源
		Integer aicategory = categoryKeywordVO.getAicategory();// 票据类别
		String aibilltype = categoryKeywordVO.getAibilltype();// 单据类型
		Integer inflag = categoryKeywordVO.getInflag();// 收方标志
		Integer outflag = categoryKeywordVO.getOutflag();// 付方标志
		String keywordnames = categoryKeywordVO.getKeywordnames();// 关键字
		// 数据来源
		if (billsource.equals(ZncsConst.SJLY_0)||billsource.equals(ocrInvoiceVO.getDatasource())) {
			billsourceFlag = true;
		}
		if (billsourceFlag == false) {
			return false;
		}
		// 票据类别
		if (aicategory.equals(ZncsConst.PJLB_1)) {
			aicategoryFlag = ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1);
		} else if (aicategory.equals(ZncsConst.PJLB_2)) {
			aicategoryFlag = ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_2);
		} else if (aicategory.equals(ZncsConst.PJLB_3)) {
			aicategoryFlag = ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3);
		} else {
			aicategoryFlag = true;
		}
		if (aicategoryFlag == false) {
			return false;
		}
		//行业
		if (StringUtil.isEmpty(categoryKeywordVO.getPk_trade()) == false)
		{
			//规则行行业
			BDTradeVO keywordtrade = mapTradeVO.get(categoryKeywordVO.getPk_trade());
			//公司行业
			if (StringUtil.isEmpty(corpVO.getIndustry()) == false)
			{
				BDTradeVO corptrade = mapTradeVO.get(corpVO.getIndustry());
				if (corptrade == null)
				{
					return false;
				}
				else
				{
					 if (corptrade.getTradecode().startsWith("Z"))	//用户选择了大账房自定义的行业，这个必须用全等比较
					 {
						 if (corptrade.getTradecode().equals(keywordtrade.getTradecode()) == false)
						 {
							 return false;
						 }
					 }
					 else
					 {
						 //行业编码开头即可
						 if (corptrade.getTradecode().startsWith(keywordtrade.getTradecode()) == false)
						 {
							 return false;
						 }
					 }
				}
			}
			
		}
		// 单据类型
		if (StringUtil.isEmpty(aibilltype)) {
			aibilltypeFlag = true;
		} else if (ocrInvoiceVO.getInvoicetype() != null && ocrInvoiceVO.getInvoicetype().indexOf(aibilltype) > -1) {
			aibilltypeFlag = true;
		} else {
			aibilltypeFlag = false;
		}
		if (aibilltypeFlag == false) {
			return false;
		}
		// 收方标志（销售方）
		if (inOutFlagCheck(ocrInvoiceVO, inflag, corpVO.getUnitname(), ocrInvoiceVO.getVsalename(), mapStaff) == false) {
			return false;
		}
		// 付方标志
		if (inOutFlagCheck(ocrInvoiceVO, outflag, corpVO.getUnitname(), ocrInvoiceVO.getVpurchname(), mapStaff) == false) {
			return false;
		}

		// 关键字
		if (!StringUtil.isEmpty(keywordnames)) {
			StringBuffer buffer = new StringBuffer();
			if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_3)) {// 增值税发票只匹配分录名称
				buffer.append(ocrInvoiceDetailVO.getInvname());
			} else {
				boolean bodyhasSXF = false;	//银行单据表体出现手续费，特殊处理 20191213 ztc
				OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren();
				if (detailVOs.length > 1)
				{

					for (OcrInvoiceDetailVO detailvo : detailVOs)
					{
						if (ocrInvoiceVO.getIstate().equals(ZncsConst.SBZT_1) && detailvo.getInvname() != null && detailvo.getInvname().contains("手续费"))
						{
							bodyhasSXF = true;
							if ("b手续费".equals(ocrInvoiceVO.getInvoicetype()))	//银行票据，多表体行，有一行名称含有手续费时，表头的单据类型不需要用手续费，防止其他金额误入手续费
							{
								ocrInvoiceVO.setInvoicetype("b普通回单");
							}
						}
						if (!StringUtil.isEmpty(ocrInvoiceVO.getVmemo()))
						{
							ocrInvoiceVO.setVfirsrinvname(null);
							if (ocrInvoiceVO.getVmemo().contains(detailvo.getInvname()))
							{
								ocrInvoiceVO.setVmemo(ocrInvoiceVO.getVmemo().replace(detailvo.getInvname(), ""));
							}
						}
					}
				}

				if (bodyhasSXF == false
						|| ocrInvoiceDetailVO.getInvname() == null
						|| ocrInvoiceDetailVO.getInvname().contains("手续费") == false)	//银行票据多行表体的中手续费行，只用含手续费的表体项匹配 20191213 ztc
				{
					buffer.append(ocrInvoiceVO.getInvoicetype());


					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVsalename());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVpurchname());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVinvoicecode());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVinvoiceno());
					buffer.append(",");

					buffer.append(ocrInvoiceVO.getBilltitle());

					buffer.append(",");
					buffer.append(ocrInvoiceVO.getDinvoicedate());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVsaleopenacc());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVsalephoneaddr());

					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVpuropenacc());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVpurphoneaddr());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVpurbankname());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVsalebankname());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVfirsrinvname());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getIstate());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getVmemo());
					buffer.append(",");
					buffer.append(ocrInvoiceVO.getKeywords());

					buffer.append(",");
					buffer.append(ocrInvoiceDetailVO.getInvtype());
					buffer.append(",");
					buffer.append(ocrInvoiceDetailVO.getItemunit());
					buffer.append(",");
                    if (bodyhasSXF &&
                            (ocrInvoiceDetailVO.getInvname() == null
                            || ocrInvoiceDetailVO.getInvname().contains("手续费") == false))		//表体含有手续费，但当前表体行没有手续费，替换全部表头中的手续费字样为空。
                    {
                        String s = buffer.toString().replace("手续费", "");
                        buffer = new StringBuffer();
                        buffer.append(s);
                    }
				}
                buffer.append(ocrInvoiceDetailVO.getInvname());
                buffer.append(",");
			}
			String[] keywords = keywordnames.split(",");
			for (int i = 0; i < keywords.length; i++) {
				Pattern p = Pattern.compile(keywords[i]);
				Matcher m = p.matcher(buffer);
				if (!m.find()) {
					keywordnamesFlag = false;
					break;
				}
			}
			if (keywordnamesFlag == false) {
				return false;
			}
		}

		
		return true;

	}

	/*
	 * 组装公司级类别对象
	 */
	public void createBillCategory(BaseCategoryVO baseCategoryVO, String pk_corp, String period) {
		BillCategoryVO billCategoryVO = new BillCategoryVO();
		billCategoryVO.setPk_basecategory(baseCategoryVO.getPk_basecategory());// 基础票据类别主键
		billCategoryVO.setPk_corp(pk_corp);// 公司主键
		billCategoryVO.setPeriod(period);// 期间
		if (StringUtil.isEmpty(baseCategoryVO.getPk_parentbasecategory())) {
			billCategoryVO.setPk_parentcategory(null);// 父节点主键
		} else {
			/*
			 * String pk_category =
			 * queryByName(baseCategoryVO.getParentname(),pk_corp,period);
			 */
			String pk_category = queryParentId(baseCategoryVO.getPk_parentbasecategory(), pk_corp, period);
			billCategoryVO.setPk_parentcategory(pk_category);// 父节点主键
		}
		billCategoryVO.setInoutflag(baseCategoryVO.getInoutflag());//方向
		billCategoryVO.setCategorylevel(baseCategoryVO.getCategorylevel());// 类别级次
		billCategoryVO.setIsleaf(baseCategoryVO.getIsleaf());// 是否末级
		billCategoryVO.setShoworder(baseCategoryVO.getShoworder());
		billCategoryVO.setCategorycode(baseCategoryVO.getCategorycode());// 类别编码
		billCategoryVO.setCategoryname(baseCategoryVO.getCategoryname());// 类别名称
		billCategoryVO.setSettype(baseCategoryVO.getSettype());// 设置类型
		billCategoryVO.setAllowchild(baseCategoryVO.getAllowchild());// 是否允许创建下级
		billCategoryVO.setChildlevel(baseCategoryVO.getChildlevel());// 允许建下级的层次数量
		billCategoryVO.setDescription(baseCategoryVO.getDescription());// 说明
		billCategoryVO.setCategorytype(baseCategoryVO.getCategorytype());// 节点类型0：基础票据类别节点1：自定义目录
		billCategoryVO.setDr(0);// 是否删除
		billCategoryVO.setIsaccount(DZFBoolean.FALSE);
		singleObjectBO.saveObject(pk_corp, billCategoryVO);
	}

	private String queryParentId(String pk_parentCategory, String pk_corp, String period) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(
				"select * from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and pk_basecategory = ? and pk_corp=? and period = ?");
		sp.addParam(pk_parentCategory);
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> volist = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));

		return volist.size() == 0 ? null : volist.get(0).getPk_category();
	}

	/*
	 * 匹配黑名单 param ocrInvoiceVO 已识别票头 param ocrInvoiceDetailVO 已识别票体 param
	 * blackListVOs 黑名单关键字
	 */
	public boolean IsBlacklist(OcrInvoiceVO ocrInvoiceVO, OcrInvoiceDetailVO ocrInvoiceDetailVO,
			List<BlackListVO> blackListVOs, CorpVO corpVO, BillCategoryVO problem) {
		String otherSide = null;
		String otherSideno = null;
		String unitname = corpVO.getUnitname();// 公司名称
		// 除本公司名称匹配全票面信息
		String salename=StringUtil.isEmpty(ocrInvoiceVO.getVsalename())?"":ocrInvoiceVO.getVsalename();
		if (unitname.equals(salename)) {
			otherSide = ocrInvoiceVO.getVpurchname();
			otherSideno = ocrInvoiceVO.getVpurchtaxno();
		} else {
			otherSide = salename;
			otherSideno = ocrInvoiceVO.getVsaletaxno();
		}
		
		setBillTitle(null, ocrInvoiceVO, null);
		
		StringBuffer buffer = new StringBuffer();
		buffer.append(ocrInvoiceVO.getInvoicetype() + "," + otherSide + "," + ocrInvoiceVO.getVinvoicecode() + ","
				+ ocrInvoiceVO.getVinvoiceno() + "," + ocrInvoiceVO.getBilltitle());
		buffer.append("," + ocrInvoiceVO.getDinvoicedate() + "," + otherSideno + "," + ocrInvoiceVO.getVsaleopenacc()
				+ "," + ocrInvoiceVO.getVsalephoneaddr());
		buffer.append("," + ocrInvoiceVO.getVpuropenacc() + "," + ocrInvoiceVO.getVpurphoneaddr() + ","
				+ ocrInvoiceVO.getVpurbankname() + "," + ocrInvoiceVO.getVsalebankname());
		buffer.append("," + ocrInvoiceVO.getVfirsrinvname() + "," + ocrInvoiceVO.getIstate() + ","
				+ ocrInvoiceVO.getVmemo() + "," + ocrInvoiceVO.getKeywords());
		buffer.append("," + ocrInvoiceDetailVO.getInvname() + "," + ocrInvoiceDetailVO.getInvtype() + ","
				+ ocrInvoiceDetailVO.getItemunit());
		for (BlackListVO blackListVO : blackListVOs) {
//			Pattern p = Pattern.compile(blackListVO.getBlacklistname());
//			Matcher m = p.matcher(buffer.toString());
			if (buffer.toString().contains(blackListVO.getBlacklistname())) {
				ocrInvoiceVO.setPk_billcategory(problem.getPk_category());
				ocrInvoiceVO.setErrordesc(ZncsConst.YCXX_1);
				ocrInvoiceVO.setUpdatets(new DZFDateTime());
				ocrInvoiceVO.setBillcategoryname(problem.getCategoryname());
				ocrInvoiceDetailVO.setPk_billcategory(problem.getPk_category());
				if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
				{
					// 更新主子表数据库
					prebillService.updateOcrInv(ocrInvoiceVO);
					prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
					
				}
				return true;
			}
		}
		return false;
	}

	/**
	 * 查询公司树的下级树
	 * 
	 * @param pk_tree
	 * @return
	 * @throws DZFWarpException
	 */
	private BillCategoryVO[] queryChildCategoryVOs(String pk_tree, DZFBoolean isaccout) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tree);
		sp.addParam(isaccout);
		BillCategoryVO[] vos = (BillCategoryVO[]) singleObjectBO.queryByCondition(BillCategoryVO.class,
				"nvl(dr,0)=0 and pk_parentcategory=? and isaccount=? order by categorycode", sp);
		return vos;
	}

	/*
	 * 按税率分组 销项发票是否按税率分组 param pk_corp 公司主键 param period 期间 param
	 */
	private void SaleCategoryByTaxrate(OcrInvoiceVO ocrInvoiceVO, OcrInvoiceDetailVO ocrInvoiceDetailVO,
			BillCategoryVO billcategoryvo, String pk_corp, String period, Map<String, BillCategoryVO> mapBillCategoryVO, Map<String, List<BillCategoryVO>> mapChildBillcategoryVO) {

//		BillCategoryVO[] childrenTreeVOs = queryChildCategoryVOs(billcategoryvo.getPk_category(), DZFBoolean.FALSE);
		List<BillCategoryVO> childrenTreeVOs = mapChildBillcategoryVO.get(billcategoryvo.getPk_category());
		
//		String pk_billVO = queryByNameAndPk(ocrInvoiceVO.getTaxrate(), pk_corp, period, billcategoryvo.getPk_category());
		String pk_billVO = queryPkByGroupName(ocrInvoiceVO.getTaxrate(), mapChildBillcategoryVO, billcategoryvo.getPk_category());
		if (pk_billVO == null) {
			BillCategoryVO vo = new BillCategoryVO();
			vo.setPk_corp(pk_corp);// 公司主键
			vo.setPeriod(period);// 期间
			vo.setInoutflag(billcategoryvo.getInoutflag());//方向
			vo.setPk_parentcategory(billcategoryvo.getPk_category());// 父节点主键
			vo.setCategorylevel(billcategoryvo.getCategorylevel() + 1);// 类别级次
			vo.setIsleaf(DZFBoolean.TRUE);// 是否末级
			// vo.setShoworder(11);//记得改
			if (childrenTreeVOs != null && childrenTreeVOs.size() > 0) {
				vo.setCategorycode(String
						.valueOf(Integer.parseInt(childrenTreeVOs.get(childrenTreeVOs.size() - 1).getCategorycode()) + 1));
				vo.setShoworder(childrenTreeVOs.get(childrenTreeVOs.size() - 1).getShoworder() + 1);
			} else {
				vo.setCategorycode(billcategoryvo.getCategorycode() + "01");
				vo.setShoworder(1);
			}
			String sl = ocrInvoiceVO.getTaxrate();
			String name = null;
			if (StringUtil.isEmpty(sl)) {
				name = "其他税率";
			}
			sl = sl.replaceAll("%", "");
			try {
				DZFDouble slDbl = new DZFDouble(sl, 2);
				name = slDbl.toString() + "%";
			} catch (Exception e) {
				name = "其他税率";
			}
			vo.setCategoryname(name);// 类别名称 税率记得改
			vo.setSettype(billcategoryvo.getSettype());// 设置类型
			vo.setAllowchild(DZFBoolean.FALSE);// 是否允许创建下级
			vo.setChildlevel(0);// 允许建下级的层次数量
			vo.setDescription(null);// 说明
			vo.setCategorytype(ZncsConst.CATEGORYTYPE_3);// 节点类型0：基础票据类别节点1：自定义目录
			vo.setDr(0);// 是否删除
			vo.setIsaccount(DZFBoolean.FALSE);
			if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
			{
				BillCategoryVO newBillvo = (BillCategoryVO) singleObjectBO.saveObject(pk_corp, vo);
				
				pk_billVO = newBillvo.getPk_category();
				
				//map中补充新节点
				mapBillCategoryVO.put(pk_billVO, vo);
				
				if (mapChildBillcategoryVO.containsKey(vo.getPk_parentcategory()))
				{
					mapChildBillcategoryVO.get(vo.getPk_parentcategory()).add(vo);
				}
				else
				{
					List<BillCategoryVO> childlist =  new ArrayList<BillCategoryVO>();
					childlist.add(vo);
					mapChildBillcategoryVO.put(vo.getPk_parentcategory(), childlist);
				}
				
				//复制编辑目录属性
				ArrayList<Object[]> childList=new ArrayList<Object[]>();
				Object[] obj=new Object[3];
				obj[0]=newBillvo.getPk_category();
				obj[1]=newBillvo.getPk_basecategory();
				obj[2]=newBillvo.getCategorytype();
				childList.add(obj);
				CategorysetVO headVO=iEditDirectory.queryCategorysetVO(newBillvo.getPk_parentcategory(), pk_corp);
				if(headVO!=null&&headVO.getPk_corp()!=null&&headVO.getPk_corp().equals(pk_corp)){
					iEditDirectory.saveCopyParent(headVO, childList, pk_corp);
				}
				// 修改父类是否末级
				updateIsLeaf(billcategoryvo.getPk_category());
			}
		}
		ocrInvoiceVO.setPk_billcategory(ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue() ? pk_billVO : billcategoryvo.getPk_category());
		ocrInvoiceVO.setUpdatets(new DZFDateTime());
		ocrInvoiceDetailVO.setPk_billcategory(pk_billVO);
		ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());
//		if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
//		{
//			// 修改票头分类
//			prebillService.updateOcrInv(ocrInvoiceVO);
//			prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
//		}
	}

	/*
	 * 按客户分组 销项发票按客户分组 param pk_corp 公司主键 param period 期间 param
	 */
	private void SaleCategoryByCustomer(OcrInvoiceVO ocrInvoiceVO, OcrInvoiceDetailVO ocrInvoiceDetailVO,
			BillCategoryVO categoryvo, String pk_corp, String period, String corpname, Map<String, BillCategoryVO> mapBillCategoryVO, Map<String, List<BillCategoryVO>> mapChildBillcategoryVO) {

//		BillCategoryVO[] childrenTreeVOs = queryChildCategoryVOs(categoryvo.getPk_category(), DZFBoolean.FALSE);
		List<BillCategoryVO> childrenTreeVOs = mapChildBillcategoryVO.get(categoryvo.getPk_category());

		
		String salename=StringUtil.isEmpty(ocrInvoiceVO.getVsalename())?"":ocrInvoiceVO.getVsalename();
		String customername = (!StringUtil.isEmpty(salename)&& OcrUtil.isSameCompany(corpname, salename) ? ocrInvoiceVO.getVpurchname() : salename);
		if (StringUtil.isEmptyWithTrim(customername))
		{
			customername = "其他往来";
		}
		  
//		String pk_billVO = queryByNameAndPk(customername, pk_corp, period, categoryvo.getPk_category());
		String pk_billVO = queryPkByGroupName(customername, mapChildBillcategoryVO, categoryvo.getPk_category());
		if (pk_billVO == null)
		{
			//如果已存在“其他"目录，有无此客户的目录，则此客户一定进入其他分类，
			pk_billVO = queryPkByGroupName("其他", mapChildBillcategoryVO, categoryvo.getPk_category());
		}
		if (pk_billVO == null) {
			BillCategoryVO vo = new BillCategoryVO();
			vo.setPk_corp(pk_corp);// 公司主键
			vo.setPeriod(period);// 期间
			vo.setInoutflag(categoryvo.getInoutflag());//方向
			vo.setPk_parentcategory(categoryvo.getPk_category());// 父节点主键
			vo.setCategorylevel(categoryvo.getCategorylevel() + 1);// 类别级次
			vo.setIsleaf(DZFBoolean.TRUE);// 是否末级
			// vo.setShoworder(11);//记得改
			if (childrenTreeVOs != null && childrenTreeVOs.size() > 0) {
				
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
				String categorycode = categoryvo.getCategorycode();
				String thisCode = categorycode + "01";
				int i = 0;
				while (codeSet.contains(thisCode))
				{
					i++;
					thisCode = categorycode + (i < 10 ? "0" : "") + i;
				}
				if (i >= 99)
				{
					thisCode = categorycode + "99";
					customername = "其他";	//仅支持98个分组名，第99个分组命名为"其他"，
				}

				vo.setCategorycode(thisCode);
				vo.setShoworder(maxshoworder + 1);

			} else {
				vo.setCategorycode(categoryvo.getCategorycode() + "01");
				vo.setShoworder(1);
			}

			vo.setCategoryname(customername);// 类别名称 购方
			vo.setSettype(categoryvo.getSettype());// 设置类型
			vo.setAllowchild(DZFBoolean.FALSE);// 是否允许创建下级
			vo.setChildlevel(0);// 允许建下级的层次数量
			vo.setDescription(null);// 说明
			vo.setCategorytype(ZncsConst.CATEGORYTYPE_4);// 节点类型0：基础票据类别节点1：自定义目录
			vo.setDr(0);// 是否删除
			vo.setIsaccount(DZFBoolean.FALSE);
			if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
			{
				BillCategoryVO newBillvo = (BillCategoryVO) singleObjectBO.saveObject(pk_corp, vo);
				pk_billVO = newBillvo.getPk_category();
				
				//map中补充新节点
				mapBillCategoryVO.put(pk_billVO, vo);
				
				if (mapChildBillcategoryVO.containsKey(vo.getPk_parentcategory()))
				{
					mapChildBillcategoryVO.get(vo.getPk_parentcategory()).add(vo);
				}
				else
				{
					List<BillCategoryVO> childlist =  new ArrayList<BillCategoryVO>();
					childlist.add(vo);
					mapChildBillcategoryVO.put(vo.getPk_parentcategory(), childlist);
				}
				
				//复制编辑目录属性
				ArrayList<Object[]> childList=new ArrayList<Object[]>();
				Object[] obj=new Object[3];
				obj[0]=newBillvo.getPk_category();
				obj[1]=newBillvo.getPk_basecategory();
				obj[2]=newBillvo.getCategorytype();
				childList.add(obj);
				CategorysetVO headVO=iEditDirectory.queryCategorysetVO(newBillvo.getPk_parentcategory(), pk_corp);
				if(headVO!=null&&headVO.getPk_corp()!=null&&headVO.getPk_corp().equals(pk_corp)){
					iEditDirectory.saveCopyParent(headVO, childList, pk_corp);
				}
				// 修改父类是否末级
				updateIsLeaf(categoryvo.getPk_category());
			}
		}
		ocrInvoiceVO.setPk_billcategory(ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue() ? pk_billVO : categoryvo.getPk_category());
		ocrInvoiceVO.setUpdatets(new DZFDateTime());
		ocrInvoiceDetailVO.setPk_billcategory(ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue() ? pk_billVO : categoryvo.getPk_category());
		ocrInvoiceDetailVO.setUpdatets(new DZFDateTime());
//		if (ocrInvoiceVO.getUpdateflag() != null && ocrInvoiceVO.getUpdateflag().booleanValue())
//		{
//			// 修改票头分类
//			prebillService.updateOcrInv(ocrInvoiceVO);
//			prebillService.updateOcrInvDetail(ocrInvoiceDetailVO);
//		}

	}


	public void updateIsLeaf(String pk_category) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("update ynt_billcategory set isleaf = 'N' where nvl(dr,0) = 0 and pk_category = ?");
		sp.addParam(pk_category);
		singleObjectBO.executeUpdate(sb.toString(), sp);
	}

	public BillCategoryVO queryVOByBaseId(String pk_baseCategory, String pk_corp, String period) {
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(
				"select * from ynt_billcategory where nvl(dr,0)=0 and pk_basecategory = ? and pk_corp=? and period=?");
		sp.addParam(pk_baseCategory);
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		return list.size() == 0 ? null : list.get(0);

	}

	private Map<String, String> queryBaseCategoryMap() throws DZFWarpException {
		String sql = "select pk_basecategory,categorycode from ynt_basecategory where nvl(dr,0)=0 and pk_corp=? and nvl(useflag,'N')='Y'";
		SQLParameter sp = new SQLParameter();
		Map<String, String> returnMap = new HashMap<String, String>();
		sp.addParam(IDefaultValue.DefaultGroup);
		List<Object[]> list = (List<Object[]>) singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		for (int i = 0; i < list.size(); i++) {
			Object[] obj = list.get(i);
			returnMap.put(obj[0].toString(), obj[1].toString());
		}
		return returnMap;
	}

//	private BillCategoryVO queryVOByBasePk(String pk_basecategory, String pk_corp, String period, String isAccount) {
//		StringBuffer sb = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sb.append(
//				"select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp = ? and period = ? and pk_basecategory = ? and nvl(isaccount, 'N')=?");
//		sp.addParam(pk_corp);
//		sp.addParam(period);
//		sp.addParam(pk_basecategory);
//		sp.addParam(isAccount);
//		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
//				new BeanListProcessor(BillCategoryVO.class));
//		return list.size() == 0 ? null : list.get(0);
//	}
	
//	public String queryVOByPk(String pk_category) {
//		StringBuffer sb = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sb.append(
//				"select bi.categoryname as categoryname from ynt_billcategory bi where nvl(bi.dr,0)=0 and bi.pk_category = ?");
//		sp.addParam(pk_category);
//		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
//				new BeanListProcessor(BillCategoryVO.class));
//		return list.size() == 0 ? null : list.get(0).getCategoryname();
//	}

	@Override
	public List<CheckOcrInvoiceVO> testingCategory(List<OcrInvoiceVO> list,String pk_corp) throws DZFWarpException {
		

		
		List<CheckOcrInvoiceVO> checkvoList = new ArrayList<CheckOcrInvoiceVO>();
		if(list!=null&&list.size()>0){
			CorpVO corpVO = corpService.queryByPk(pk_corp);
			//
			List<ParaSetVO> listParas=iParaSet.queryParaSet(pk_corp);
			DZFBoolean isyh=listParas.get(0).getBankbillbyacc();
			//查询当前期间未做账的类别
			String period = list.get(0).getPeriod();
			SQLParameter sp = new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(period);
			BillCategoryVO[] billcategoryvos = (BillCategoryVO[])singleObjectBO.queryByCondition(BillCategoryVO.class, "pk_corp=? and period=? and  nvl(isaccount,'N')='N' and nvl(dr,0)=0", sp);
			HashMap<String, BillCategoryVO> hmBillCategory = new HashMap<String, BillCategoryVO>();
			for (BillCategoryVO vo : billcategoryvos)
			{
				hmBillCategory.put(vo.getPk_category(), vo);
			}
			
			BillCategoryVO problem = queryByCode(ZncsConst.FLCODE_WT, pk_corp, list.get(0).getPeriod());// 问题票据

			// 查询票据的所有详细信息
			List<OcrInvoiceDetailVO> invDetailList = prebillService.queryDetailByInvList(list);
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(invDetailList, new String[] { "pk_invoice" });
			
			for (OcrInvoiceVO ocrVO : list) {
				String errordesc = null;
				String vSaleName = ocrVO.getVsalename();
				//根据类别方向，收、付方是不是本公司
				if (ZncsConst.LBFX_1.equals(ocrVO.getInoutflag())){//收入(销售) 
					
					if (vSaleName == null || !corpVO.getUnitname().startsWith(vSaleName.trim())){					
						errordesc = "收方（销售方）标志不是本公司";		
					}
				}else if(ZncsConst.LBFX_2.equals(ocrVO.getInoutflag())){//支出(采购)
					if(ocrVO.getVpurchname() == null || !corpVO.getUnitname().startsWith(ocrVO.getVpurchname().trim())){
						errordesc = "付方（采购方）标志不是本公司";
					}
				}
				//银行票在非银行类别中/非银行票在银行类别中
				if (ocrVO.getIstate().equals(ZncsConst.SBZT_1)
						&& !ocrVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHPJ)
						&& !ocrVO.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY)
						&& problem.getPk_category().equals(ocrVO.getPk_billcategory()) == false) {
					errordesc = (errordesc == null ? "" : errordesc + "; ") + "银行票在非银行类别中";
				}else if(!ocrVO.getIstate().equals(ZncsConst.SBZT_1) && ocrVO.getCategorycode().startsWith(ZncsConst.FLCODE_YHPJ)){
					errordesc = (errordesc == null ? "" : errordesc + "; ") + "非银行票在银行类别中";
				}
				//自定义目录中的票，是否定义自定义规则
				BillCategoryVO categoryvo = hmBillCategory.get(ocrVO.getPk_billcategory());
				if (categoryvo.getCategorycode().startsWith(ZncsConst.FLCODE_ZDY))
				{
					//检查当前目录是否定义了规则
					sp = new SQLParameter();
					sp.addParam(pk_corp);
					sp.addParam(categoryvo.getPk_basecategory());
					SuperVO[] vos =singleObjectBO.queryByCondition(VouchertempletHVO.class, "pk_corp=? and pk_basecategory=? and nvl(dr,0)=0", sp);
					if (vos == null || vos.length == 0)
					{
						errordesc = (errordesc == null ? "'" : errordesc + ", '") + categoryvo.getCategoryname() + "' 没有定义规则";
					}
				}
				else if (categoryvo.getCategorycode().startsWith(ZncsConst.FLCODE_ZC))
				{
					//资产目录的名称不在资产类别名称中
					sp = new SQLParameter();
					sp.addParam(pk_corp);
					sp.addParam(categoryvo.getCategoryname());
					BdAssetCategoryVO[] assetcategoryvos = (BdAssetCategoryVO[])singleObjectBO.queryByCondition(BdAssetCategoryVO.class, "(pk_corp = '000001' or pk_corp = ?) and catename = ? and nvl(dr,0)=0", sp);
					if (assetcategoryvos == null || assetcategoryvos.length == 0)
					{
						errordesc = (errordesc == null ? "" : errordesc + "; ") + "没有资产类别'" + categoryvo.getCategoryname() + "'，请先录入";
					}
				}

				
				//继续检测
				ocrVO.setUpdateflag(new DZFBoolean(false));
				ocrVO.setErrordesc(null);
				//重复票据检测
				if (checkIsRepeat(ocrVO, pk_corp, problem.getPk_category()))
				{
					errordesc = (errordesc == null ? "" : errordesc + "; ") + "重复票据建议归为问题票据";
				}
				

				if (checkNoError(ocrVO, corpVO, problem.getPk_category()) == false
						|| ZncsConst.YCXX_8.equals(ocrVO.getErrordesc())
						|| ZncsConst.YCXX_10.equals(ocrVO.getErrordesc())
						|| (ZncsConst.YCXX_8 + "," + ZncsConst.YCXX_10).equals(ocrVO.getErrordesc()))				{
					String thisError = StringUtil.isEmpty(ocrVO.getErrordesc()) ? "" : ocrVO.getErrordesc();
					//金额为空
					if (thisError.contains(ZncsConst.YCXX_7))
					{
						errordesc = (errordesc == null ? "" : errordesc + "; ") + ZncsConst.YCXX_7;
					}
					//收付款方与公司名称不一致
					if (thisError.contains( ZncsConst.YCXX_5))		
					{
						errordesc = (errordesc == null ? "" : errordesc + "; ") + "收付款方与公司名称不一致建议归为问题票据";
					}
					//开票日期晚于当前账期
					if (thisError.contains(ZncsConst.YCXX_6)) {
						errordesc = (errordesc == null ? "" : errordesc + "; ") + "开票日期晚于当前账期应属于问题票据分类";
					}
					//开票日期晚于当前账期
					if (thisError.contains(ZncsConst.YCXX_8)) {
						errordesc = (errordesc == null ? "" : errordesc + "; ") + ZncsConst.YCXX_8 + ",应属于问题票据分类";
					}
					//日期格式非法
					if (thisError.contains(ZncsConst.YCXX_9)) {
						errordesc = (errordesc == null ? "" : errordesc + "; ") + ZncsConst.YCXX_9 + ",应属于问题票据分类";
					}
					//日期格式非法
					if (thisError.contains(ZncsConst.YCXX_10)) {
						errordesc = (errordesc == null ? "" : errordesc + "; ") + ZncsConst.YCXX_10 + ",应属于问题票据分类";
					}

				}
				//黑名单
				List<BlackListVO> blackListVOs = iBlackList.queryBlackListVOs(pk_corp);// 黑名单
				if (ocrVO.getPk_invoice() != null && detailMap.containsKey(ocrVO.getPk_invoice()))
				{
					List<OcrInvoiceDetailVO> detailvos = detailMap.get(ocrVO.getPk_invoice());
					for (OcrInvoiceDetailVO ocrInvoiceDetailVO : detailvos)
					{
						if (IsBlacklist(ocrVO, ocrInvoiceDetailVO, blackListVOs, corpVO, problem))
						{
							errordesc = (errordesc == null ? "" : errordesc + "; ") + "含有黑名单关键字";
							break;
						}
					}
				}
				if (errordesc != null || ocrVO.getErrordesc2() != null)
				{
					CheckOcrInvoiceVO checkvo = new CheckOcrInvoiceVO();
					checkvo.setPk_invoice(ocrVO.getPk_invoice());
					checkvo.setId(ocrVO.getPk_invoice());
					checkvo.setErrordesc((errordesc == null ? "" : errordesc) + (ocrVO.getErrordesc2() == null ? "" : ocrVO.getErrordesc2()));
					checkvo.setBilltitle(ocrVO.getBilltitle());
					checkvo.setWebid(ocrVO.getWebid());
					checkvo.setOcraddress(iBillcategory.queryParentPK(isyh, ocrVO,hmBillCategory.get(ocrVO.getPk_billcategory()).getCategorycode()));
					checkvo.setCategoryname(categoryvo.getCategoryname());//( baseCategoryService.queryCategoryName(ocrVO.getPk_billcategory());
					checkvoList.add(checkvo);
				}
				ocrVO.setErrordesc(errordesc == null ? null : errordesc);
				prebillService.updateErrorDesc(ocrVO);
				
			}
			
		}
		return checkvoList;
	}
	
}

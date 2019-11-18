package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.IctradeinVO;
import com.dzf.zxkj.platform.model.icset.IntradeHVO;
import com.dzf.zxkj.platform.model.icset.IntradeoutVO;
import com.dzf.zxkj.platform.model.icset.InventoryVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.VatInvoiceSetVO;
import com.dzf.zxkj.platform.model.pzgl.PzSourceRelationVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.BdAssetCategoryVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zcgl.AssetcardVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountCodeRuleService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.bdset.IHLService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icbill.ISaleoutService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IPzglService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.pzgl.impl.CaclTaxMny;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.zcgl.IKpglService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("zncsVoucherImpl")
public class ZncsVoucherImpl implements IZncsVoucher {
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IEditDirectory iEditDirectory;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	protected IYntBoPubUtil yntBoPubUtil;
	@Autowired
	protected ICorpService corpService;
	@Autowired
	private IParaSet iParaSet;
	@Autowired
	private IVoucherService iVoucherService;
	@Autowired
	private IImageGroupService iImageGroupService;
	@Autowired
	private IBDCurrencyService iBDCurrencyService;
	@Autowired
	private IHLService iHLService;
	@Autowired
	private IZncsNewTransService iZncsNewTransService;
	@Autowired
	private ISchedulCategoryService iSchedulCategoryService;
	@Autowired
	private IInventoryAccSetService iInventoryAccSetService;
	@Autowired
	private IKpglService iKpglService;
	@Autowired
	private IAutoMatchName ocr_atuomatch;
	@Autowired
	private ISaleoutService ic_saleoutserv;
	@Autowired
	private IPurchInService ic_purchinserv;
	@Autowired
	private IPzglService iPzglService;
	@Autowired
	private IParameterSetService sys_parameteract;
	@Autowired
	private ICpaccountService cpaccountService;
	@Autowired
	private IBDCurrencyService sys_currentserv;
	@Autowired
	private IUserService userServiceImpl;
	@Autowired
	private IBillcategory iBillcategory;
	@Autowired
	private IAccountService accountService;

	/**
	 * 分类或票据生成凭证
	 */
	@Override
	public List<TzpzHVO> processGeneralTzpzVOs(String pk_category, String pk_bills, String period, String pk_corp, String pk_parent, Map<String, Map<String, Object>> checkMsgMap, String pk_user) throws DZFWarpException {
		//0、要返回的凭证数据
		List<TzpzHVO> returnList=new ArrayList<TzpzHVO>();
		//1、查公司参数
		List<Object> paramList=queryParams(pk_corp);
		//2、得到要做账的票(不包括未识别和问题的)
		List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOs(pk_category, pk_bills, period, pk_corp,pk_parent,((ParaSetVO)paramList.get(0)).getErrorvoucher());
		if(invoiceList==null||invoiceList.size()==0){
			throw new BusinessException("没有符合制证条件的数据");
		}
		//3、缓存公司分类数据 3.5、按级次倒序排序的分类集合，合并用
		List<List<Object[]>> levelList=new ArrayList<List<Object[]>>();
		Map<String, Object[]> categoryMap=queryCategoryMap(period, pk_corp,levelList, DZFBoolean.FALSE);
		if(categoryMap==null||categoryMap.size()==0){
			throw new BusinessException("没有符合制证条件的数据");
		}
		//4、查票据表体
		List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
		//5、表体按表头主键分组
		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
		//6、装表体
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			if(detailMap.get(invoiceVO.getPk_invoice())!=null){
				invoiceVO.setChildren(detailMap.get(invoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
		}
		//7、辅助核算头
		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=queryFzhsHeadMap(pk_corp);
		//8、辅助核算表体
		Set<String> zyFzhsList=new HashSet<String>();
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=queryFzhsBodyMap(pk_corp,zyFzhsList);
		//9、InventorySetVO
		InventorySetVO inventorySetVO=iInventoryAccSetService.query(pk_corp);
		CorpVO corp= corpService.queryByPk(pk_corp);
		if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO==null){
			throw new BusinessException("启用总账核算存货，请先设置存货成本核算方式！");
		}
		//10、辅助核算别名
		Map<String, InventoryAliasVO> fzhsBMMap =getFzhsBMMap(pk_corp,inventorySetVO);
		//11、检查12456791011
		checkCategory(checkMsgMap, categoryMap, invoiceList, period, pk_corp,fzhsBodyMap.get("000001000000000000000006"),fzhsBMMap);
		if(invoiceList.size()==0)return returnList;
		//12、把票分组，组1是自定义分类下的票(走自定义模板)，组2是非自定义的票(走入账规则)
		Map<String, List<OcrInvoiceVO>> invoiceMap=groupByCategoryType(invoiceList, categoryMap);
		//13、取币种
		Map<String, BdCurrencyVO> currMap=queryCurrencyVOMap();
		//14、汇率
		Map<String, Object[]> rateMap=queryExateVOMap(pk_corp);
		//15、查票据关联的ImageGroup
		Map<String, ImageGroupVO> groupMap=queryImageGroupVO(invoiceList);
		//16、查票据上的分类对应的编辑目录(当前公司的不包括集团预制)
		Map<String, CategorysetVO> categorysetMap=queryCategorysetVO(invoiceList,pk_corp);
		//17、取银行账号
		Map<String, String> bankAccountMap=queryBankAccountVOs(pk_corp);
		//18、取科目map
		Map<String, YntCpaccountVO> accountMap = accountService.queryMapByPk(pk_corp);
		//19、要汇总的分类
		String collectCategory= StringUtil.isEmpty(pk_category)?StringUtil.isEmpty(pk_bills)?null:invoiceList.get(0).getPk_billcategory():pk_category;
		Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(pk_corp);
		//20、银行存款科目是否有下级
		DZFBoolean isBankSubjLeaf=isBankSubjLeaf(pk_corp);
		//21、处理自定义票据
		if(invoiceMap.get("zdy").size()>0){
			returnList.addAll(createZdyVoucher(invoiceMap.get("zdy"),pk_corp,categoryMap,paramList,levelList,pk_user,currMap,rateMap,groupMap,categorysetMap,bankAccountMap,accountMap,checkMsgMap,inventorySetVO,collectCategory,fzhsHeadMap,fzhsBodyMap,assistMap,fzhsBMMap,isBankSubjLeaf));
		}
		//22、处理非自定义票据
		if(invoiceMap.get("fzdy").size()>0){
			returnList.addAll(createFZdyVoucher(invoiceMap.get("fzdy"),pk_corp,categoryMap,paramList,levelList,pk_user,currMap,rateMap,groupMap,categorysetMap,bankAccountMap,accountMap,checkMsgMap,inventorySetVO,collectCategory,fzhsHeadMap,fzhsBodyMap,assistMap,zyFzhsList,fzhsBMMap,isBankSubjLeaf));
		}
		//23、处理问题和未识别票据
		if(invoiceMap.get("error").size()>0){
			returnList.addAll(createErrorVoucher(invoiceMap.get("error"), pk_corp, categoryMap, paramList, pk_user, groupMap));
		}
		//24、设置名称、辅助核算、金额2位
		setShowTzpzBVO(returnList);
		//25、按ynt_image_ocrlibrary的iorder排序,生成凭证号
		sortTzpzHVOList(returnList, invoiceList);
		return returnList;
	}
	
	/**
	 * 检查分类
	 * 职能做账检查
	 * 1、票据未匹配存货（强制）
	 * 2、资产分类名称与资产类别是否一致（强制）
	 * 3、银行账号不存在（半强制，银行存款科目下有明细科目强制）
	 * 4、存在未签收进项发票
	 * 5、存在未签收销项发票
	 * 6、存在未勾对银行对账单数据
	 * 7、检测分类存在问题
	 * 8、自定义分类创建规则检测（强制）
	 * 9、检查是否有票生成的资产卡片
	 * 10、检查是否有出库单
	 * 11、检查是否有入库单
	 * @throws DZFWarpException
	 */
	private void checkCategory(Map<String, Map<String, Object>> checkMsgMap,Map<String, Object[]> categoryMap,List<OcrInvoiceVO> invoiceList,String period, String pk_corp,List<AuxiliaryAccountBVO> fzhsBodyVOs,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		initCheckMsgMap(checkMsgMap);
		if(DZFBoolean.TRUE.equals(DZFBoolean.TRUE)){//1存货匹配(所有票判断表体行)
			checkChpp(checkMsgMap,pk_corp, period,invoiceList,categoryMap,fzhsBodyVOs,fzhsBMMap);
		}
		List<String[]> checkList=new ArrayList<String[]>();
		CorpVO corp=corpService.queryByPk(pk_corp);
		DZFBoolean isZc=isCheck(invoiceList, categoryMap, "zc",checkList,corp);
		if(DZFBoolean.TRUE.equals(isZc)){//2资产类别
			List<Map<String, Object>> zzflList=checkZcfl(pk_corp, period,checkList);
			checkMsgMap.get("2").put("count", zzflList.size());
			checkMsgMap.get("2").put("body", zzflList);
		}
		if(DZFBoolean.TRUE.equals(isCheck(invoiceList, categoryMap, "jxfp",null,corp))){//4检查进项发票
			List<Map<String, Object>> jxfpList=checkJxfp(pk_corp, period);
			checkMsgMap.get("4").put("count", jxfpList.size());
			checkMsgMap.get("4").put("body", jxfpList);
		}
		if(DZFBoolean.TRUE.equals(isCheck(invoiceList, categoryMap, "xxfp",null,corp))){//5检查销项发票
			List<Map<String, Object>> xxfpList=checkXxfp(pk_corp, period);
			checkMsgMap.get("5").put("count", xxfpList.size());
			checkMsgMap.get("5").put("body", xxfpList);
		}
		if(DZFBoolean.TRUE.equals(isCheck(invoiceList, categoryMap, "yhpj",null,corp))){//6检查银行票据
			List<Map<String, Object>> yhpjList=checkYhpj(pk_corp, period,invoiceList);
			checkMsgMap.get("6").put("count", yhpjList.size());
			checkMsgMap.get("6").put("body", yhpjList);
		}
		List<OcrInvoiceVO> tmpInvoiceList=(List<OcrInvoiceVO>) OcrUtil.clonAll(invoiceList);
		List<CheckOcrInvoiceVO> list7=iSchedulCategoryService.testingCategory(tmpInvoiceList, pk_corp);//7检测分类
		if(list7!=null&&list7.size()>0){
			checkMsgMap.get("7").put("count", list7.size());
			checkMsgMap.get("7").put("body", list7);
		}
		if(DZFBoolean.TRUE.equals(DZFBoolean.TRUE)){//9资产卡品(所有票判断表体行，不知固定资产分类下的)
			List<Map<String, Object>> zzcpList=checkZccp(pk_corp, period,invoiceList);
			checkMsgMap.get("9").put("count", zzcpList.size());
			checkMsgMap.get("9").put("body", zzcpList);
		}
		
		if(DZFBoolean.TRUE.equals(DZFBoolean.TRUE)){//10出库单(所有票判断表体行)
			List<Map<String, Object>> ckdList=checkIctrade(pk_corp, period,invoiceList, IBillTypeCode.HP75);
			checkMsgMap.get("10").put("count", ckdList.size());
			checkMsgMap.get("10").put("body", ckdList);
		}
		
		if(DZFBoolean.TRUE.equals(DZFBoolean.TRUE)){//11入库单(所有票判断表体行)
			List<Map<String, Object>> rkdList=checkIctrade(pk_corp, period,invoiceList,IBillTypeCode.HP70);
			checkMsgMap.get("11").put("count", rkdList.size());
			checkMsgMap.get("11").put("body", rkdList);
		}
	}
	/**
	 * 初始化智能做账检查结果map
	 * @param checkMsgMap
	 * @throws DZFWarpException
	 */
	private void initCheckMsgMap(Map<String, Map<String, Object>> checkMsgMap)throws DZFWarpException {
		Map<String, Object> map1=new HashMap<String, Object>();
		map1.put("count", 0);
		map1.put("ismust", "Y");
		map1.put("body", null);
		checkMsgMap.put("1", map1);
		
		Map<String, Object> map2=new HashMap<String, Object>();
		map2.put("count", 0);
		map2.put("ismust", "Y");
		map2.put("body", null);
		checkMsgMap.put("2", map2);
		
		Map<String, Object> map3=new HashMap<String, Object>();
		map3.put("count", 0);
		map3.put("ismust", "N");
		map3.put("body", null);
		checkMsgMap.put("3", map3);
		
		Map<String, Object> map4=new HashMap<String, Object>();
		map4.put("count", 0);
		map4.put("ismust", "N");
		map4.put("body", null);
		checkMsgMap.put("4", map4);
		
		Map<String, Object> map5=new HashMap<String, Object>();
		map5.put("count", 0);
		map5.put("ismust", "N");
		map5.put("body", null);
		checkMsgMap.put("5", map5);
		
		Map<String, Object> map6=new HashMap<String, Object>();
		map6.put("count", 0);
		map6.put("ismust", "N");
		map6.put("body", null);
		checkMsgMap.put("6", map6);
		
		Map<String, Object> map7=new HashMap<String, Object>();
		map7.put("count", 0);
		map7.put("ismust", "N");
		map7.put("body", null);
		checkMsgMap.put("7", map7);
		
		Map<String, Object> map8=new HashMap<String, Object>();
		map8.put("count", 0);
		map8.put("ismust", "Y");
		map8.put("body", null);
		checkMsgMap.put("8", map8);
		
		Map<String, Object> map9=new HashMap<String, Object>();
		map9.put("count", 0);
		map9.put("ismust", "N");
		map9.put("body", null);
		checkMsgMap.put("9", map9);
		
		Map<String, Object> map10=new HashMap<String, Object>();
		map10.put("count", 0);
		map10.put("ismust", "N");
		map10.put("body", null);
		checkMsgMap.put("10", map10);
		
		Map<String, Object> map11=new HashMap<String, Object>();
		map11.put("count", 0);
		map11.put("ismust", "N");
		map11.put("body", null);
		checkMsgMap.put("11", map11);
	}
	/**
	 * 判断职能做账是否检查这一项
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFBoolean isCheck(List<OcrInvoiceVO> invoiceList,Map<String, Object[]> categoryMap,String checkType,List<String[]> checkList,CorpVO corp)throws DZFWarpException{
		DZFBoolean flag=DZFBoolean.FALSE;
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invVO=invoiceList.get(i);
			String pk_category1=invVO.getPk_billcategory();
			Object[] obj1=categoryMap.get(pk_category1);
			String categorycode1=obj1[0].toString();
			invVO.setCategorycode(categorycode1);
			if(checkType.equals("zc")&&DZFBoolean.TRUE.equals(corp.getHoldflag())){//2资产
				OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])invVO.getChildren();
				for (int j = 0;detailVOs!=null&& j < detailVOs.length; j++) {
					if(!StringUtil.isEmpty(detailVOs[j].getPk_billcategory())){
						String pk_category=detailVOs[j].getPk_billcategory();
						Object[] obj=categoryMap.get(pk_category);
						String categorycode=obj[0].toString();
						String categoryname=obj[4].toString();
						if(categorycode.startsWith(ZncsConst.FLCODE_ZC)){
							flag=DZFBoolean.TRUE;
							String[] stringObj=new String[]{pk_category,categoryname,invVO.getPk_invoice()};
							checkList.add(stringObj);
						}
					}
				}
			}
			if(checkType.equals("jxfp")&&invVO.getIstate().equals(ZncsConst.SBZT_3)&&(categorycode1.startsWith(ZncsConst.FLCODE_KC)||categorycode1.startsWith(ZncsConst.FLCODE_FY)||categorycode1.startsWith(ZncsConst.FLCODE_CB)||categorycode1.startsWith(ZncsConst.FLCODE_ZC))){//4进项发票
				flag=DZFBoolean.TRUE;
				break;
			}
			if(checkType.equals("xxfp")&&invVO.getIstate().equals(ZncsConst.SBZT_3)&&categorycode1.startsWith(ZncsConst.FLCODE_SR)){//5销项发票
				flag=DZFBoolean.TRUE;
				break;
			}
			if(checkType.equals("yhpj")&&invVO.getIstate().equals(ZncsConst.SBZT_1)){//6银行
				flag=DZFBoolean.TRUE;
				break;
			}
		}
		return flag;
	}
	
	/**
	 * 检查资产卡片
	 * @return
	 * @throws DZFWarpException
	 */
	private List<Map<String, Object>> checkZccp(String pk_corp,String period,List<OcrInvoiceVO> invoiceList)throws DZFWarpException{
		List<String> invoiceKeyList=new ArrayList<String>();
		for(int i=0;i<invoiceList.size();i++){
			invoiceKeyList.add(invoiceList.get(i).getPk_invoice());
		}
		String unitName=corpService.queryByPk(pk_corp).getUnitname();
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_assetcard where  nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_invoice", invoiceKeyList.toArray(new String[0])));
		SQLParameter sp=new SQLParameter();
		List<AssetcardVO> VOList=(List<AssetcardVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AssetcardVO.class));
		List<OcrInvoiceVO> deleteList=new ArrayList<OcrInvoiceVO>();
		if(VOList!=null&&VOList.size()>0){
			Map<String, List<AssetcardVO>> invoiceMap = DZfcommonTools.hashlizeObject(VOList, new String[] { "pk_invoice" });
			Iterator<String> itor=invoiceMap.keySet().iterator();
			while(itor.hasNext()){
				String pk_invoice=itor.next();
				for(int i=0;i<invoiceList.size();i++){
					String invoiceKey=invoiceList.get(i).getPk_invoice();
					if(pk_invoice.equals(invoiceKey)){
						Map<String, Object> tmpMap=new HashMap<String, Object>();
						tmpMap.put("id", pk_invoice);//主键
						tmpMap.put("webid", invoiceList.get(i).getWebid());//付款方名称
						tmpMap.put("fkf", unitName);//付款方名称
						tmpMap.put("skf", invoiceList.get(i).getVsalename());//收款方名称
						tmpMap.put("je", invoiceList.get(i).getNmny());//金额
						tmpMap.put("se", invoiceList.get(i).getNtaxnmny());//税额
						tmpMap.put("jshj", invoiceList.get(i).getNtotaltax());//价税合计
						tmpMap.put("kprq", invoiceList.get(i).getDinvoicedate());//开票日期
						returnList.add(tmpMap);
						deleteList.add(invoiceList.get(i));
						break;
					}
				}
			}
		}
		for(int i=0;i<deleteList.size();i++){
			invoiceList.remove(deleteList.get(i));
		}
		return returnList;
	}
	
	/**
	 * 查存货匹配
	 * @return
	 * @throws DZFWarpException
	 */
	private void checkChpp(Map<String, Map<String, Object>> checkMsgMap,String pk_corp,String period,List<OcrInvoiceVO> invoiceList,Map<String, Object[]> categoryMap,List<AuxiliaryAccountBVO> fzhsBodyVOs,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		CorpVO corp=corpService.queryByPk(pk_corp);
		InventorySetVO inventorySetVO=iInventoryAccSetService.query(pk_corp);
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			if(!invoiceVO.getIstate().equals(ZncsConst.SBZT_3))continue;
			OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])invoiceVO.getChildren();
			for (int j = 0; detailVOs!=null&&j < detailVOs.length; j++) {
				OcrInvoiceDetailVO detailVO = detailVOs[j];
				if(!StringUtil.isEmpty(detailVO.getPk_billcategory())){
					String catecode=categoryMap.get(detailVO.getPk_billcategory())[0].toString();
					String invName=OcrUtil.execInvname(detailVO.getInvname());
					if(catecode.startsWith("101015")||catecode.startsWith("101110")||catecode.startsWith("11")){
						// 1、启用库存，去存货档案找
						if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
							//先循环查，后面可以缓存起来
							InventoryVO invVO=getInventoryVOByName(invName, detailVO.getInvtype(),detailVO.getItemunit(), pk_corp);
							if(invVO==null){
								checkInvtentory(checkMsgMap, invoiceVO, corp.getUnitname());
							}
						} else {// 2、没启用去辅助核算找
							if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO!=null&&inventorySetVO.getChcbjzfs()!=InventoryConstant.IC_NO_MXHS){
								DZFBoolean isPp=DZFBoolean.FALSE;
								String strName1="";
								String strName2="";
								if(inventorySetVO.getChppjscgz()==0){
									strName2=invName+(StringUtil.isEmpty(detailVO.getInvtype())?"":detailVO.getInvtype())+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
								}else{
									strName2=invName+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
								}
								//先找别名表
								if(fzhsBMMap.get(strName2)!=null){
									isPp=DZFBoolean.TRUE;
								}
								if(DZFBoolean.FALSE.equals(isPp)&&fzhsBodyVOs!=null&&fzhsBodyVOs.size()>0){
									for(AuxiliaryAccountBVO fzhsBodyVO:fzhsBodyVOs){
										if(inventorySetVO.getChppjscgz()==0){
											strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getSpec())?"":fzhsBodyVO.getSpec())+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
										}else{
											strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
										}
										if(strName1.equals(strName2)){
											isPp=DZFBoolean.TRUE;
											break;
										}
									}
								}
								if(DZFBoolean.FALSE.equals(isPp)){
									checkInvtentory(checkMsgMap, invoiceVO,  corp.getUnitname());
								}
							}
						}
					}
				}
			}
		}
	}
	
	/**
	 * 总账核算模式 存货别名
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, InventoryAliasVO> getFzhsBMMap(String pk_corp,InventorySetVO inventorySetVO)throws DZFWarpException{
		Map<String, InventoryAliasVO> returnMap=new HashMap<String, InventoryAliasVO>();
		CorpVO corp=corpService.queryByPk(pk_corp);
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.*,b.kmclassify,b.chukukmid from ynt_icalias a left outer join ynt_fzhs_b b on(a.pk_inventory=b.pk_auacount_b)  where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and a.pk_corp=?");
		sp.addParam(pk_corp);
		List<InventoryAliasVO> list=(List<InventoryAliasVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InventoryAliasVO.class));
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size();i++) {
				String keyName=null;
				if(IcCostStyle.IC_ON.equals(corp.getBbuildic())||(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO.getChppjscgz()==0)){
					keyName=list.get(i).getAliasname()+(StringUtil.isEmpty(list.get(i).getSpec())?"":list.get(i).getSpec())+(StringUtil.isEmpty(list.get(i).getUnit())?"":list.get(i).getUnit());
				}else{
					keyName=list.get(i).getAliasname()+(StringUtil.isEmpty(list.get(i).getUnit())?"":list.get(i).getUnit());
				}
				returnMap.put(keyName, list.get(i));
			}
		}
		return returnMap;
	}
	/**
	 * 检查出库单
	 * @throws DZFWarpException
	 */
	private List<Map<String, Object>> checkIctrade(String pk_corp,String period,List<OcrInvoiceVO> invoiceList,String cbilltype)throws DZFWarpException{
		List<String> imageGroupKeyList=new ArrayList<String>();
		for(int i=0;i<invoiceList.size();i++){
			if(!StringUtil.isEmpty(invoiceList.get(i).getPk_image_group())){
				imageGroupKeyList.add(invoiceList.get(i).getPk_image_group());
			}
		}
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		if(imageGroupKeyList.size()>0){
			String unitName=corpService.queryByPk(pk_corp).getUnitname();
			StringBuffer sb=new StringBuffer();
			sb.append("select * from ynt_ictrade_h where  nvl(dr,0)=0 and cbilltype=? and "+SqlUtil.buildSqlForIn("pk_image_group", imageGroupKeyList.toArray(new String[0])));
			SQLParameter sp=new SQLParameter();
			sp.addParam(cbilltype);
			List<IntradeHVO> VOList=(List<IntradeHVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(IntradeHVO.class));
			List<OcrInvoiceVO> deleteList=new ArrayList<OcrInvoiceVO>();
			if(VOList!=null&&VOList.size()>0){
				Map<String, List<IntradeHVO>> invoiceMap = DZfcommonTools.hashlizeObject(VOList, new String[] { "pk_image_group" });
				Iterator<String> itor=invoiceMap.keySet().iterator();
				while(itor.hasNext()){
					String pk_image_group=itor.next();
					for(int i=0;i<invoiceList.size();i++){
						String imageGroupKey=invoiceList.get(i).getPk_image_group();
						if(pk_image_group.equals(imageGroupKey)){
							Map<String, Object> tmpMap=new HashMap<String, Object>();
							tmpMap.put("id", invoiceList.get(i).getPk_invoice());//主键
							tmpMap.put("webid", invoiceList.get(i).getWebid());//付款方名称
							if(cbilltype.equals(IBillTypeCode.HP70)){//入库
								tmpMap.put("fkf", unitName);//付款方名称
								tmpMap.put("skf", invoiceList.get(i).getVsalename());//收款方名称
							}else{
								tmpMap.put("fkf", invoiceList.get(i).getVpurchname());//付款方名称
								tmpMap.put("skf", unitName);//收款方名称
							}
							tmpMap.put("je", invoiceList.get(i).getNmny());//金额
							tmpMap.put("se", invoiceList.get(i).getNtaxnmny());//税额
							tmpMap.put("jshj", invoiceList.get(i).getNtotaltax());//价税合计
							tmpMap.put("kprq", invoiceList.get(i).getDinvoicedate());//开票日期
							returnList.add(tmpMap);
							deleteList.add(invoiceList.get(i));
							break;
						}
					}
				}
			}
			for(int i=0;i<deleteList.size();i++){
				invoiceList.remove(deleteList.get(i));
			}
		}
		return returnList;
	}
	
	/**
	 * 检查资产分类名称
	 * @return
	 * @throws DZFWarpException
	 */
	private List<Map<String, Object>> checkZcfl(String pk_corp,String period,List<String[]> checkList)throws DZFWarpException{
		List<String> keyList=new ArrayList<String>();
		List<String> nameList=new ArrayList<String>();
		for(int i=0;i<checkList.size();i++){
			String[] stringObj=checkList.get(i);
			keyList.add(stringObj[0]);
			nameList.add(stringObj[1]);
		}
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_category where (pk_corp='000001' or pk_corp=?)  and nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("catename", nameList.toArray(new String[0])));
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<BdAssetCategoryVO> VOList=(List<BdAssetCategoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BdAssetCategoryVO.class));
		for(int i=0;i<nameList.size();i++){
			String key=keyList.get(i);
			String name=nameList.get(i);
			boolean flag=true;
			for(int j=0;j<VOList.size();j++){
				if(VOList.get(j).getCatename().equals(name)){
					flag=false;
					break;
				}
			}
			if(flag){
				Map<String, Object> tmpMap=new HashMap<String, Object>();
				tmpMap.put("id", key);//分类主键
				tmpMap.put("zcflmc", name);//资产分类名称
				returnList.add(tmpMap);
			}
		}
		return returnList;
	}
	/**
	 * 检查进项发票
	 * @return
	 * @throws DZFWarpException
	 */
	private List<Map<String, Object>> checkJxfp(String pk_corp,String period)throws DZFWarpException{
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_vatincominvoice where pk_corp=? and period=? and nvl(dr,0)=0 and pk_image_group is null");
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<VATInComInvoiceVO2> VOList=(List<VATInComInvoiceVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VATInComInvoiceVO2.class));
		if(VOList!=null&&VOList.size()>0){
			String unitName=corpService.queryByPk(pk_corp).getUnitname();
			for(int i=0;i<VOList.size();i++){
				Map<String, Object> tmpMap=new HashMap<String, Object>();
				VATInComInvoiceVO2 vo=VOList.get(i);
				tmpMap.put("id", vo.getPk_vatincominvoice());//主键
				tmpMap.put("fkf", unitName);//付款方名称
				tmpMap.put("skf", vo.getXhfmc());//收款方名称
				tmpMap.put("je", vo.getHjje());//金额
				tmpMap.put("se", vo.getSpse());//税额
				tmpMap.put("jshj", vo.getJshj());//价税合计
				tmpMap.put("kprq", vo.getKprj());//开票日期
				returnList.add(tmpMap);
			}
		}
		return returnList;
	}
	/**
	 * 检查银行票据
	 * @return
	 * @throws DZFWarpException
	 */
	private List<Map<String, Object>> checkYhpj(String pk_corp,String period,List<OcrInvoiceVO> invoiceList)throws DZFWarpException{
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_bankstatement where pk_corp=? and period=? and nvl(dr,0)=0 and ((pk_image_group is null and sourcetype!=15) or (sourcetype=15))");
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BankStatementVO2> VOList=(List<BankStatementVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BankStatementVO2.class));
		if(VOList!=null&&VOList.size()>0){
			String unitName=corpService.queryByPk(pk_corp).getUnitname();
			Map<String, OcrInvoiceVO> invoiceMap=new HashMap<String, OcrInvoiceVO>();
			for(int i=0;i<invoiceList.size();i++){
				invoiceMap.put(invoiceList.get(i).getPk_invoice(), invoiceList.get(i));
			}
			for(int i=0;i<VOList.size();i++){
				Map<String, Object> tmpMap=new HashMap<String, Object>();
				BankStatementVO2 vo=VOList.get(i);
				if("15".equals(vo.getSourcetype())){//来源ocr
					String pk_invoice=vo.getVdef13();
					OcrInvoiceVO invoiceVO=invoiceMap.get(pk_invoice);
					if(invoiceVO!=null){
						String bankCode=null;//银行账号
						String bankName=null;//银行名称
						//3处理银行账号暂时都是非强制
						String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
						if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName, saleName)){
							bankCode=invoiceVO.getVsaletaxno();
							bankName=invoiceVO.getVsalebankname();
						}else if(OcrUtil.isSameCompany(unitName, invoiceVO.getVpurchname())){
							bankCode=invoiceVO.getVpurchtaxno();
							bankName=invoiceVO.getVpurbankname();
						}
						tmpMap.put("id", pk_invoice);//主键
						tmpMap.put("type", 0);
						tmpMap.put("webid", invoiceVO.getWebid());//图片ID
						tmpMap.put("bankname", bankName);//本方银行名称
						tmpMap.put("bankcode", bankCode);//本方银行账号
						tmpMap.put("fkfmc", invoiceVO.getVpurchname());//付款方名称
						tmpMap.put("skfmc", saleName);//收款方名称
						tmpMap.put("je", invoiceVO.getNtotaltax());//金额
						tmpMap.put("kprq", invoiceVO.getDinvoicedate());//开票日期
						returnList.add(tmpMap);
					}
				}else{//来源其他
					tmpMap.put("id", vo.getPk_bankstatement());//主键
					tmpMap.put("type", 1);
					tmpMap.put("dfzhmc", vo.getOthaccountname());//对方账户名称
					tmpMap.put("dfzh", vo.getOthaccountcode());//对方账户
					tmpMap.put("je", vo.getSyje()==null||vo.getSyje().compareTo(DZFDouble.ZERO_DBL)==0?vo.getZcje():vo.getSyje());//金额
					tmpMap.put("rq", vo.getTradingdate());//日期
					returnList.add(tmpMap);
				}
				
			}
		}
		return returnList;
	}
	/**
	 * 检查销项发票
	 * @return
	 * @throws DZFWarpException
	 */
	private List<Map<String, Object>> checkXxfp(String pk_corp,String period)throws DZFWarpException{
		List<Map<String, Object>> returnList=new ArrayList<Map<String, Object>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_vatsaleinvoice where pk_corp=? and period=? and nvl(dr,0)=0 and pk_image_group is null");
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<VATSaleInvoiceVO2> VOList=(List<VATSaleInvoiceVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VATSaleInvoiceVO2.class));
		if(VOList!=null&&VOList.size()>0){
			String unitName=corpService.queryByPk(pk_corp).getUnitname();
			for(int i=0;i<VOList.size();i++){
				Map<String, Object> tmpMap=new HashMap<String, Object>();
				VATSaleInvoiceVO2 vo=VOList.get(i);
				tmpMap.put("id", vo.getPk_vatsaleinvoice());//主键
				tmpMap.put("fkf", vo.getXhfmc());//付款方名称
				tmpMap.put("skf", unitName);//收款方名称
				tmpMap.put("je", vo.getHjje());//金额
				tmpMap.put("se", vo.getSpse());//税额
				tmpMap.put("jshj", vo.getJshj());//价税合计
				tmpMap.put("kprq", vo.getKprj());//开票日期
				returnList.add(tmpMap);
			}
		}
		return returnList;
	}
	/**
	 * 币种
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, BdCurrencyVO> queryCurrencyVOMap()throws DZFWarpException{
	 	BdCurrencyVO[] vos=iBDCurrencyService.queryCurrency();
	 	Map<String, BdCurrencyVO> currMap=new HashMap<String, BdCurrencyVO>();
	 	for (int i = 0; i < vos.length; i++) {
			BdCurrencyVO bdCurrencyVO = vos[i];
			currMap.put(bdCurrencyVO.getCurrencycode(), bdCurrencyVO);
		}
	 	return currMap;
	}
	/**
	 * 汇率
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, Object[]> queryExateVOMap(String pk_corp)throws DZFWarpException{
		List<ExrateVO> list=iHLService.query(pk_corp);
		Map<String, Object[]> rateMap=new HashMap<String, Object[]>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				rateMap.put(list.get(i).getPk_currency(), new Object[]{list.get(i).getExrate(),list.get(i).getConvmode()});
			}
		}
		return rateMap;
	}
	
	private void setShowTzpzBVO(List<TzpzHVO> returnList){
		if(returnList==null||returnList.size()==0){
			return;
		}
		BdCurrencyVO[] cvos = sys_currentserv.queryCurrency();
		Map<String, BdCurrencyVO> currmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(cvos), new String[]{"pk_currency"});
		Map<String, AuxiliaryAccountBVO> fzhsMap = gl_fzhsserv.queryMap(returnList.get(0).getPk_corp());
		int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(returnList.get(0).getPk_corp(), "dzf009"));
		int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(returnList.get(0).getPk_corp(), "dzf010"));
		int ratePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(returnList.get(0).getPk_corp(), "dzf011"));
		for (int i = 0; i < returnList.size(); i++) {
			returnList.get(i).setPreserveCode(Boolean.TRUE);
			returnList.get(i).setZd_user(userServiceImpl.queryUserJmVOByID(returnList.get(i).getCoperatorid()).getUser_name());
			returnList.get(i).setJfmny(returnList.get(i).getJfmny()==null?DZFDouble.ZERO_DBL:returnList.get(i).getJfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
			returnList.get(i).setDfmny(returnList.get(i).getDfmny()==null?DZFDouble.ZERO_DBL:returnList.get(i).getDfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
			TzpzBVO[] tzpzBVOs=(TzpzBVO[])returnList.get(i).getChildren();
			if(tzpzBVOs!=null&&tzpzBVOs.length>0){
				for (int j = 0; j < tzpzBVOs.length; j++) {
					if(tzpzBVOs[j].getJfmny()!=null){
						tzpzBVOs[j].setJfmny(tzpzBVOs[j].getJfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
					}else{
						tzpzBVOs[j].setJfmny(DZFDouble.ZERO_DBL);
					}
					if(tzpzBVOs[j].getDfmny()!=null){
						tzpzBVOs[j].setDfmny(tzpzBVOs[j].getDfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
					}else{
						tzpzBVOs[j].setDfmny(DZFDouble.ZERO_DBL);
					}
					if(tzpzBVOs[j].getYbjfmny()!=null){
						tzpzBVOs[j].setYbjfmny(tzpzBVOs[j].getYbjfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
					}else{
						tzpzBVOs[j].setYbjfmny(DZFDouble.ZERO_DBL);
					}
					if(tzpzBVOs[j].getYbdfmny()!=null){
						tzpzBVOs[j].setYbdfmny(tzpzBVOs[j].getYbdfmny().setScale(2, DZFDouble.ROUND_HALF_UP));
					}else{
						tzpzBVOs[j].setYbdfmny(DZFDouble.ZERO_DBL);
					}
					if(tzpzBVOs[j].getNnumber()!=null){
						tzpzBVOs[j].setNnumber(tzpzBVOs[j].getNnumber().setScale(numPrecision, DZFDouble.ROUND_HALF_UP));
					}
					if(tzpzBVOs[j].getNprice()!=null){
						tzpzBVOs[j].setNprice(tzpzBVOs[j].getNprice().setScale(pricePrecision, DZFDouble.ROUND_HALF_UP));
					}
					if(tzpzBVOs[j].getNrate()!=null){
						tzpzBVOs[j].setNrate(tzpzBVOs[j].getNrate().setScale(ratePrecision, DZFDouble.ROUND_HALF_UP));
					}
					tzpzBVOs[j].setFzhs_list(getFzhs(tzpzBVOs[j], fzhsMap));
					String[] fzhsStr = getFzhsStr(tzpzBVOs[j].getFzhs_list());
					
					String curcode= null;
					if(StringUtil.isEmpty(tzpzBVOs[j].getPk_currency())){
						curcode = "CNY";//人民币
					}else{
						if(IGlobalConstants.RMB_currency_id.equals(tzpzBVOs[j].getPk_currency())){
							curcode = "CNY";//人民币
						}else{
							BdCurrencyVO cyvo = currmap.get(tzpzBVOs[j].getPk_currency());
							curcode = cyvo.getCurrencycode();
						}
					}
					
					StringBuilder showZy = new StringBuilder(tzpzBVOs[j].getZy() == null ? "" : tzpzBVOs[j].getZy());
					boolean hasForeignCur = curcode != null && !curcode.equals("CNY");
					boolean hasNumber = tzpzBVOs[j].getNnumber() != null && tzpzBVOs[j].getNnumber().doubleValue() != 0;
					if (hasForeignCur || hasNumber) {
						showZy.append("(");
						if (hasForeignCur) {
							showZy.append(curcode).append(":");
							if (tzpzBVOs[j].getYbjfmny() != null && tzpzBVOs[j].getYbjfmny().doubleValue() != 0) {
								showZy.append(tzpzBVOs[j].getYbjfmny().toString().replaceAll("\\.0+$", ""));
							} else {
								showZy.append(tzpzBVOs[j].getYbdfmny() == null ? ""
										: tzpzBVOs[j].getYbdfmny().toString().replaceAll("\\.0+$", ""));
							}
							showZy.append(",汇率:")
									.append(tzpzBVOs[j].getNrate() == null ? ""
											: tzpzBVOs[j].getNrate().toString().replaceAll("\\.0+$", ""));
						}
						if (hasNumber) {
							if (hasForeignCur) {
								showZy.append("; ");
							}
							showZy.append("数量:")
									.append(tzpzBVOs[j].getNnumber().toString().replaceAll("\\.0+$", ""));
							String measureName = getMeasureName(tzpzBVOs[j]);
							showZy.append(measureName == null ? "" : measureName);
							showZy.append(",单价:")
									.append(tzpzBVOs[j].getNprice() == null ? ""
											: tzpzBVOs[j].getNprice().toString().replaceAll("\\.0+$", ""));
						}
						showZy.append(")");
					}
					// 科目显示字段
					StringBuilder showKm = new StringBuilder();
					showKm.append(tzpzBVOs[j].getVcode() == null ? "" : tzpzBVOs[j].getVcode()).append(fzhsStr[0]).append(" ")
					.append(tzpzBVOs[j].getKmmchie() == null ? "" : tzpzBVOs[j].getKmmchie()).append(fzhsStr[1]);
					if (tzpzBVOs[j].getInvname() != null) {
						showKm.append("_").append(tzpzBVOs[j].getInvname());
						if (!StringUtil.isEmpty(tzpzBVOs[j].getMeaname())) {
							showKm.append("(").append(tzpzBVOs[j].getMeaname()).append(")");
						}
					}
//					tzpzBVOs[j].setKmmchie(showKm.toString());
					tzpzBVOs[j].setTmpzy(showZy.toString());
					tzpzBVOs[j].setTmpfullname(showKm.toString());
				}
			}
			returnList.get(i).setChildren(DZfcommonTools.convertToSuperVO(tzpzBVOs));
		}
	}
	
	private String getMeasureName(TzpzBVO bvo) {
		String measureName = bvo.getMeaname();
		List<AuxiliaryAccountBVO> fzvos = bvo.getFzhs_list();
		if (fzvos != null) {
			for (AuxiliaryAccountBVO fzvo : fzvos) {
				if (fzvo != null && AuxiliaryConstant.ITEM_INVENTORY.equals(fzvo.getPk_auacount_h())
				&& fzvo.getUnit() != null) {
					measureName = fzvo.getUnit();
				}
			}
		}
		return measureName;
	}
	private String[] getFzhsStr(List<AuxiliaryAccountBVO> fzhsvos) {
		String[] fzhsStr = new String[2];
		String code = "";
		String name = "";
		if (fzhsvos != null && fzhsvos.size() > 0) {
			for (AuxiliaryAccountBVO fzhs : fzhsvos) {
				if (fzhs != null) {
					code += "_" + fzhs.getCode();
					name += "_" + fzhs.getName();
					if (AuxiliaryConstant.ITEM_INVENTORY.equals(fzhs.getPk_auacount_h())) {
						if (!StringUtil.isEmpty(fzhs.getSpec())) {
							name=name+"("+fzhs.getSpec()+")";
						}
					}
				}
			}
		}
		fzhsStr[0]  = code;
		fzhsStr[1]  = name;
		return fzhsStr;
	}
	
	private List<AuxiliaryAccountBVO> getFzhs(TzpzBVO tzpzBVO, Map<String, AuxiliaryAccountBVO> fzhsMap) {
		List<AuxiliaryAccountBVO> fzhsList = null;
		List<String> pk_fzhs = new ArrayList<String>();
		if (tzpzBVO.getFzhsx1() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx1());
		}
		if (tzpzBVO.getFzhsx2() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx2());
		}
		if (tzpzBVO.getFzhsx3() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx3());
		}
		if (tzpzBVO.getFzhsx4() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx4());
		}
		if (tzpzBVO.getFzhsx5() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx5());
		}
		if (tzpzBVO.getFzhsx6() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx6());
		}
		if (tzpzBVO.getFzhsx7() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx7());
		}
		if (tzpzBVO.getFzhsx8() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx8());
		}
		if (tzpzBVO.getFzhsx9() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx9());
		}
		if (tzpzBVO.getFzhsx10() != null) {
			pk_fzhs.add(tzpzBVO.getFzhsx10());
		}
        if(pk_fzhs != null  && pk_fzhs.size() > 0){
        	fzhsList =  new ArrayList<AuxiliaryAccountBVO>();
        	for (String fzhsid : pk_fzhs) {
        		if (fzhsMap.containsKey(fzhsid))
        		{
        			fzhsList.add(fzhsMap.get(fzhsid));
        		}
			}
        	if (fzhsList.size() == 0)
        	{
        		fzhsList = null;
        	}
        }
       
		return fzhsList;
	}
	/**
	 * 进项、销项、银行对账单转票据VO后生成凭证
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public List<TzpzHVO> processGeneralTzpzVOsByInvoice(List<OcrInvoiceVO> invoiceList, String period, String pk_corp,String pk_user,Map<String, Map<String, Object>> checkMsgMap
			,List<List<Object[]>> levelList,Map<String, Object[]> categoryMap,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Set<String> zyFzhsList
			,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,InventorySetVO inventorySetVO,CorpVO corp,Map<String, InventoryAliasVO> fzhsBMMap
			,List<Object> paramList,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap
			,Map<String, AuxiliaryAccountBVO> assistMap,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, String> jituanSubMap,YntCpaccountVO[] accVOs
			,String tradeCode,String newrule,List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException {
		//0、要返回的凭证数据
		List<TzpzHVO> returnList = new ArrayList<TzpzHVO>();
		//1、得到要做账的票(不包括未识别和问题的)
		if (invoiceList == null || invoiceList.size() == 0) {
			throw new BusinessException("没有符合制证条件的数据");
		}
		//2、缓存公司分类数据 2.5、按级次倒序排序的分类集合，合并用
//		List<List<Object[]>> levelList = new ArrayList<List<Object[]>>();
//		Map<String, Object[]> categoryMap = queryCategoryMap(period, pk_corp, levelList,DZFBoolean.FALSE);
//		if (categoryMap == null || categoryMap.size() == 0) {
//			throw new BusinessException("没有符合制证条件的数据");
//		}
//		//3、辅助核算头
//		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=queryFzhsHeadMap(pk_corp);
//		//4、辅助核算表体
//		Set<String> zyFzhsList=new HashSet<String>();
//		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=queryFzhsBodyMap(pk_corp,zyFzhsList);
//		//5、InventorySetVO
//		InventorySetVO inventorySetVO=iInventoryAccSetService.query(pk_corp);
//		CorpVO corp=CorpCache.getInstance().get(null, pk_corp);
//		if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO==null){
//			throw new BusinessException("启用总账核算存货，请先设置存货成本核算方式！");
//		}
//		//6、辅助核算别名
//		Map<String, InventoryAliasVO> fzhsBMMap=getFzhsBMMap(pk_corp, inventorySetVO);
		//7、检查245679
		initCheckMsgMap(checkMsgMap);
//		checkCategory(checkMsgMap, categoryMap, invoiceList, period, pk_corp,fzhsBodyMap.get("000001000000000000000006"),fzhsBMMap);
		//8、把票分组，组1是自定义分类下的票(走自定义模板)，组2是非自定义的票(走入账规则)
		Map<String, List<OcrInvoiceVO>> invoiceMap = groupByCategoryType(invoiceList, categoryMap);
		//9、查公司参数
//		List<Object> paramList = queryParams(pk_corp);
//		//10、处理自定义票据
//		//11、取币种
//		Map<String, BdCurrencyVO> currMap=queryCurrencyVOMap();
//		//12、汇率
//		Map<String, Object[]> rateMap=queryExateVOMap(pk_corp);
		//13、查票据关联的ImageGroup
		Map<String, ImageGroupVO> groupMap=queryImageGroupVO(invoiceList);
		//14、查票据上的分类对应的编辑目录(当前公司的不包括集团预制)
		Map<String, CategorysetVO> categorysetMap=queryCategorysetVO(invoiceList,pk_corp);
//		//15、取银行账号
//		Map<String, String> bankAccountMap=queryBankAccountVOs(pk_corp);
//		//16、取科目map
//		Map<String,YntCpaccountVO> accountMap = AccountCache.getInstance().getMap(null, pk_corp);
//		Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(pk_corp);
		//20、银行存款科目是否有下级
		DZFBoolean isBankSubjLeaf=isBankSubjLeaf(pk_corp);
		if (invoiceMap.get("zdy").size() > 0) {
			returnList.addAll(createZdyVoucher(invoiceMap.get("zdy"), pk_corp, categoryMap, paramList, levelList,pk_user,currMap,rateMap,groupMap,categorysetMap,bankAccountMap,accountMap,checkMsgMap,inventorySetVO,null,fzhsHeadMap,fzhsBodyMap,assistMap,fzhsBMMap,isBankSubjLeaf));
		}
		//17、处理非自定义票据
		if (invoiceMap.get("fzdy").size() > 0) {
			returnList.addAll(createFZdyVoucherInvoice(invoiceMap.get("fzdy"), pk_corp, categoryMap, paramList, levelList, pk_user, currMap, rateMap, groupMap, categorysetMap, bankAccountMap, accountMap, checkMsgMap, inventorySetVO, null, fzhsHeadMap, fzhsBodyMap, assistMap, zyFzhsList, fzhsBMMap, corp, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs,isBankSubjLeaf));
		}
		//18、错误凭证
		if(invoiceMap.get("error").size()>0){
			returnList.addAll(createErrorVoucher(invoiceList, pk_corp, categoryMap, paramList, null, groupMap));
		}
		setShowTzpzBVO(returnList);
		if(returnList.size()>0){
			String pzh=yntBoPubUtil.getNewVoucherNo(returnList.get(0).getPk_corp(), returnList.get(0).getDoperatedate());
			for (int i = 0; i < returnList.size(); i++) {
				TzpzHVO tzpzVO=returnList.get(i);
				tzpzVO.setPzh(pzh);//凭证号
				pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
			}
		}
		return returnList;
	}
	
	/**
	 * 查公司参数
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private List<Object> queryParams(String pk_corp)throws DZFWarpException{
		List<ParaSetVO> paramList1=iParaSet.queryParaSet(pk_corp);
		String sql="select * from ynt_invoiceset where nvl(dr,0)=0 and pk_corp=? order by style";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<VatInvoiceSetVO> paramList2=(List<VatInvoiceSetVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VatInvoiceSetVO.class));
		List<Object> returnList=new ArrayList<Object>();
		returnList.add(paramList1.get(0));
		List<VatInvoiceSetVO> param2=new ArrayList<VatInvoiceSetVO>();
		for(int i=0;i<3;i++){
			VatInvoiceSetVO setVO=null;
			for(int j=0;j<paramList2.size();j++){
				if(paramList2.get(j).getStyle().equals(String.valueOf(i+1))){
					setVO=paramList2.get(j);
					break;
				}
			}
			param2.add(setVO);
		}
		returnList.add(param2);
		return returnList;
	}
	
	/**
	 * 查科目入账规则
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, List<AccsetVO>> queryAccsetVOMap(String pk_accountschema,String[] pk_basecategory)throws DZFWarpException{
		//key：分类+科目体系+行业
		Map<String, List<AccsetVO>> returnMap=new HashMap<String, List<AccsetVO>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select * from ynt_accset where nvl(dr,0)=0 and pk_corp=? and pk_accountschema=? and nvl(useflag,'N')='Y' ");
		if(pk_basecategory!=null){
			sb.append(" and "+ SqlUtil.buildSqlForIn("pk_basecategory", pk_basecategory));
		}
		sb.append(" order by pk_basecategory,pk_trade ");
		SQLParameter sp=new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(pk_accountschema);
		List<AccsetVO> accsetVOList=(List<AccsetVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AccsetVO.class));
		for(int i=0;i<accsetVOList.size();i++){
			String pk_trade=StringUtil.isEmpty(accsetVOList.get(i).getPk_trade())?"":accsetVOList.get(i).getPk_trade();
			String key=accsetVOList.get(i).getPk_basecategory()+accsetVOList.get(i).getPk_accountschema()+pk_trade;
			if(returnMap.containsKey(key)){
				returnMap.get(key).add(accsetVOList.get(i));
			}else{
				List<AccsetVO> tmpList=new ArrayList<AccsetVO>();
				tmpList.add(accsetVOList.get(i));
				returnMap.put(key, tmpList);
			}
		}
		return returnMap;
	}
	
	/**
	 * 查关键字入账规则
	 * @param pk_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, List<AccsetKeywordBVO2>> queryAccsetKeywordBVO2Map(String pk_accountschema,String[] pk_basecategory)throws DZFWarpException{
		//key：分类+科目体系+行业
		Map<String, List<AccsetKeywordBVO2>> returnMap=new HashMap<String, List<AccsetKeywordBVO2>>();
		StringBuffer sb=new StringBuffer();
		sb.append("select a.pk_basecategory,b.*,c.tradecode from ynt_accset_keyword a inner join ynt_accset_keyword_b2 b on(a.pk_accset_keyword=b.pk_accset_keyword)  left outer join ynt_bd_trade c on (c.pk_trade = b.pk_trade) ");
		sb.append(" where nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and a.pk_corp=? and b.pk_accountschema=? and nvl(b.useflag,'N')='Y' ");
		if(pk_basecategory!=null){
			sb.append(" and "+SqlUtil.buildSqlForIn("a.pk_basecategory", pk_basecategory));
		}
		sb.append(" order by a.pk_basecategory,case when  b.pk_trade is null then 0 else length(c.tradecode) end desc ");
		SQLParameter sp=new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(pk_accountschema);
		List<AccsetKeywordBVO2> accsetKeywordBVO2List=(List<AccsetKeywordBVO2>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AccsetKeywordBVO2.class));
		if(accsetKeywordBVO2List!=null&&accsetKeywordBVO2List.size()>0){
			fillKeyWords(accsetKeywordBVO2List);
			for(int i=0;i<accsetKeywordBVO2List.size();i++){
				String tradeCode=StringUtil.isEmpty(accsetKeywordBVO2List.get(i).getPk_trade())?"":accsetKeywordBVO2List.get(i).getTradecode();
				String key=accsetKeywordBVO2List.get(i).getPk_basecategory()+accsetKeywordBVO2List.get(i).getPk_accountschema()+tradeCode;
				if(returnMap.containsKey(key)){
					returnMap.get(key).add(accsetKeywordBVO2List.get(i));
				}else{
					List<AccsetKeywordBVO2> tmpList=new ArrayList<AccsetKeywordBVO2>();
					tmpList.add(accsetKeywordBVO2List.get(i));
					returnMap.put(key, tmpList);
				}
				
			}
		}
		return returnMap;
	}
	
	private Map<String, List<AccsetKeywordBVO1>> queryBVO1(List<AccsetKeywordBVO2> list)throws DZFWarpException{
		Set<String> keySet=new HashSet<String>();
		for(int i=0;i<list.size();i++){
			keySet.add(list.get(i).getPk_accset_keyword());
		}
		String sql="select pk_accset_keyword,pk_keywords from ynt_accset_keyword_b1 where nvl(dr,0)=0 and pk_corp=? and "+SqlUtil.buildSqlForIn("pk_accset_keyword", keySet.toArray(new String[0]));
		SQLParameter sp=new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		List<AccsetKeywordBVO1> list1=(List<AccsetKeywordBVO1>)singleObjectBO.executeQuery(sql, sp,new BeanListProcessor(AccsetKeywordBVO1.class));
		Map<String, List<AccsetKeywordBVO1>> map=new HashMap<String, List<AccsetKeywordBVO1>>();
		if(list1!=null&&list1.size()>0){
			for(int i=0;i<list1.size();i++){
				String key=list1.get(i).getPk_accset_keyword();
				if(map.containsKey(key)){
					map.get(key).add(list1.get(i));
				}else{
					List<AccsetKeywordBVO1> tmpList=new ArrayList<AccsetKeywordBVO1>();
					tmpList.add(list1.get(i));
					map.put(key, tmpList);
				}
			}
		}
		return map;
	}
	
	private void fillKeyWords(List<AccsetKeywordBVO2> list)throws DZFWarpException{
		Map<String, List<AccsetKeywordBVO1>> bVO1Map=queryBVO1(list);
		if(bVO1Map==null||bVO1Map.size()==0){
			return;
		}
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_keyword,keyword from ynt_keyword where nvl(dr,0)=0 and pk_corp=?");
		sp.addParam(IDefaultValue.DefaultGroup);
		List<KeywordVO> KeywordList = (List<KeywordVO>) singleObjectBO.executeQuery(sb.toString(), sp,new BeanListProcessor(KeywordVO.class));
		Map<String, String> keyWordMap=new HashMap<String, String>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<KeywordList.size();i++){
				keyWordMap.put(KeywordList.get(i).getPk_keyword(), KeywordList.get(i).getKeyword());
			}
			for(int i=0;i<list.size();i++){
				AccsetKeywordBVO2 vo=list.get(i);
				List<AccsetKeywordBVO1> tmpList=bVO1Map.get(vo.getPk_accset_keyword());
				if(tmpList==null)continue;
				for (int j = 0; j < tmpList.size(); j++) {
					String pk_keywors = tmpList.get(j).getPk_keywords();
					if (!StringUtil.isEmpty(pk_keywors)) {
						String[] words = pk_keywors.split(",");
						String wordNames = "";
						for (int k = 0; k < words.length; k++) {
							wordNames += keyWordMap.get(words[k]) + ",";
						}
						wordNames = wordNames.substring(0, wordNames.length() - 1);
						tmpList.get(j).setKeywordnames(wordNames);
					}
				}
				vo.setList1(tmpList);
			}
		}
	}
	
	/**
	 * 检查自定义模板
	 * @throws DZFWarpException
	 */
	private void checkZdyTemplte(Map<String, Map<String, Object>> checkMsgMap,OcrInvoiceVO invoiceVO,Map<String, Object[]> categoryMap)throws DZFWarpException{
		if(checkMsgMap.get("8").get("body")!=null){
			List<Map<String, Object>> tmpList=((List<Map<String, Object>>)checkMsgMap.get("8").get("body"));
			for (int j = 0; j < tmpList.size(); j++) {
				if(tmpList.get(j).get("id").toString().equals(invoiceVO.getPk_billcategory())){
					return;
				}
			}
		}
		checkMsgMap.get("8").put("count", Integer.parseInt(checkMsgMap.get("8").get("count").toString())+1);
		Map<String, Object> zdymbMap=new HashMap<String, Object>();
		zdymbMap.put("id", invoiceVO.getPk_billcategory());
		Object[] obj=categoryMap.get(invoiceVO.getPk_billcategory());
		zdymbMap.put("categoryname", obj[4].toString());
		if(checkMsgMap.get("8").get("body")==null){
			List<Map<String, Object>> zdymbList=new ArrayList<Map<String, Object>>();
			zdymbList.add(zdymbMap);
			checkMsgMap.get("8").put("body", zdymbList);
		}else{
			((List<Map<String, Object>>)checkMsgMap.get("8").get("body")).add(zdymbMap);
		}
	}
	/**
	 * 检查银行账号是否存在
	 * @throws DZFWarpException
	 */
	private void checkBankCode(CorpVO corp,OcrInvoiceVO invoiceVO,Map<String, String> bankAccountMap,Map<String, Map<String, Object>> checkMsgMap,DZFBoolean isBankSubjLeaf)throws DZFWarpException{
		if(StringUtil.isEmpty(invoiceVO.getIstate())||!invoiceVO.getIstate().equals(ZncsConst.SBZT_1)){
			return;
		}
		if(DZFBoolean.TRUE.equals(isBankSubjLeaf)){
			return;
		}
		String unitName=corp.getUnitname();
		String bankCode=null;//银行账号
		String bankName=null;//银行名称
		//3处理银行账号暂时都是非强制
		String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
		if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName, saleName)){
			bankCode=invoiceVO.getVsaletaxno();
			bankName=invoiceVO.getVsalebankname();
		}
		if(bankCode!=null&&bankAccountMap.get(bankCode)==null&&checkMsgMap!=null){
			checkMsgMap.get("3").put("count", Integer.parseInt(checkMsgMap.get("3").get("count").toString())+1);
			Map<String, Object> yhzhMap=new HashMap<String, Object>();
			yhzhMap.put("id", invoiceVO.getPk_invoice());//主键
			yhzhMap.put("webid", invoiceVO.getWebid());//图片ID
			yhzhMap.put("bankname", bankName);//本方银行名称
			yhzhMap.put("bankcode", bankCode);//本方银行账号
			yhzhMap.put("fkfmc", invoiceVO.getVpurchname());//付款方名称
			yhzhMap.put("skfmc", saleName);//收款方名称
			yhzhMap.put("je", invoiceVO.getNtotaltax());//金额
			yhzhMap.put("kprq", invoiceVO.getDinvoicedate());//开票日期
			if(checkMsgMap.get("3").get("body")==null){
				List<Map<String, Object>> yhzhList=new ArrayList<Map<String, Object>>();
				yhzhList.add(yhzhMap);
				checkMsgMap.get("3").put("body", yhzhList);
			}else{
				((List<Map<String, Object>>)checkMsgMap.get("3").get("body")).add(yhzhMap);
			}
		}
		bankCode=null;//银行账号
		bankName=null;//银行名称
		if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName, invoiceVO.getVpurchname())){
			bankCode=invoiceVO.getVpurchtaxno();
			bankName=invoiceVO.getVpurbankname();
		}
		if(bankCode!=null&&bankAccountMap.get(bankCode)==null&&checkMsgMap!=null){
			checkMsgMap.get("3").put("count", Integer.parseInt(checkMsgMap.get("3").get("count").toString())+1);
			Map<String, Object> yhzhMap=new HashMap<String, Object>();
			yhzhMap.put("id", invoiceVO.getPk_invoice());//主键
			yhzhMap.put("webid", invoiceVO.getWebid());//图片ID
			yhzhMap.put("bankname", bankName);//本方银行名称
			yhzhMap.put("bankcode", bankCode);//本方银行账号
			yhzhMap.put("fkfmc", invoiceVO.getVpurchname());//付款方名称
			yhzhMap.put("skfmc", saleName);//收款方名称
			yhzhMap.put("je", invoiceVO.getNtotaltax());//金额
			yhzhMap.put("kprq", invoiceVO.getDinvoicedate());//开票日期
			if(checkMsgMap.get("3").get("body")==null){
				List<Map<String, Object>> yhzhList=new ArrayList<Map<String, Object>>();
				yhzhList.add(yhzhMap);
				checkMsgMap.get("3").put("body", yhzhList);
			}else{
				((List<Map<String, Object>>)checkMsgMap.get("3").get("body")).add(yhzhMap);
			}
		}
	}
	
	/**
	 * 查当前公司的行业
	 * @param pk_trade
	 * @return
	 * @throws DZFWarpException
	 */
	private String getCurTradeCode(String pk_trade)throws DZFWarpException{
		if(StringUtil.isEmpty(pk_trade)){
			return null;
		}
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select tradecode from ynt_bd_trade where nvl(dr,0)=0 and pk_trade=?");
		sp.addParam(pk_trade);
		List<Object[]> tradeList=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		if(tradeList!=null&&tradeList.size()>0&&tradeList.get(0)[0]!=null){
			return tradeList.get(0)[0].toString();
		}else{
			return null;
		}
	}
	/**
	 * 处理问题和未识别票据
	 * @param invoiceList
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> createErrorVoucher(List<OcrInvoiceVO> invoiceList,String pk_corp,Map<String, Object[]> categoryMap,List<Object> paramList,String pk_user,Map<String, ImageGroupVO> groupMap)throws DZFWarpException{
		List<TzpzHVO> returnLists=new ArrayList<TzpzHVO>();
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			TzpzHVO tzpzVO=creatTzpzVOByError(invoiceVO, StringUtil.isEmpty(invoiceVO.getPk_image_group())?null:groupMap.get(invoiceVO.getPk_image_group()), categoryMap, paramList, pk_user);
			returnLists.add(tzpzVO);
		}
		return returnLists;
	}
	
	private void groupTempBankCode(OcrInvoiceVO invVO,String pk_corp,Map<String, Object[]> categoryMap,Map<String, List<OcrInvoiceVO>> tmpInvouceMap){
		String bankCode=null;
		String unitName=corpService.queryByPk(pk_corp).getUnitname();
		String categorycode=categoryMap.get(invVO.getPk_billcategory())[0].toString();
		if(categorycode.startsWith(ZncsConst.FLCODE_YHPJ)){
			String vpurchname=invVO.getVpurchname();//购方名称
			String vpurchtaxno=invVO.getVpurchtaxno();//购方账号
			String vsalename=StringUtil.isEmpty(invVO.getVsalename())?"":invVO.getVsalename();//销方名称
			String vsaletaxno=invVO.getVsaletaxno();//销方账号
			if(!StringUtil.isEmpty(vpurchname)&&OcrUtil.isSameCompany(unitName,vpurchname)&&(StringUtil.isEmpty(vsalename)||!OcrUtil.isSameCompany(unitName,vsalename))){//购方
				if(StringUtil.isEmpty(vpurchtaxno)){
					bankCode="empty";
				}else{
					bankCode=vpurchtaxno;
				}
			}else if((StringUtil.isEmpty(vpurchname)||!OcrUtil.isSameCompany(unitName,vpurchname))&&!StringUtil.isEmpty(vsalename)&&OcrUtil.isSameCompany(unitName,vsalename)){//销方
				if(StringUtil.isEmpty(vsaletaxno)){
					bankCode="empty";
				}else{
					bankCode=vsaletaxno;
				}
			}else if(!StringUtil.isEmpty(vpurchname)&&OcrUtil.isSameCompany(unitName,vpurchname)&&!StringUtil.isEmpty(vsalename)&&OcrUtil.isSameCompany(unitName,vsalename)){//都有可能是户间转账
				if(StringUtil.isEmpty(vpurchtaxno)&&StringUtil.isEmpty(vsaletaxno)){
					bankCode="empty";
				}else{
					if(!StringUtil.isEmpty(vpurchtaxno)){
						bankCode=vpurchtaxno;
					}else{
						bankCode=vsaletaxno;
					}
				}
			}else{//出问题了
				bankCode="empty";
			}
			if(!tmpInvouceMap.containsKey(bankCode)){
				List<OcrInvoiceVO> tmpList=new ArrayList<OcrInvoiceVO>();
				tmpList.add(invVO);
				tmpInvouceMap.put(bankCode, tmpList);
			}else{
				tmpInvouceMap.get(bankCode).add(invVO);
			}
		}else{
			if(!tmpInvouceMap.containsKey("notbank")){
				List<OcrInvoiceVO> tmpList=new ArrayList<OcrInvoiceVO>();
				tmpList.add(invVO);
				tmpInvouceMap.put("notbank", tmpList);
			}else{
				tmpInvouceMap.get("notbank").add(invVO);
			}
		}
	}
	/**
	 * 处理非自定义票据
	 * @param invoiceList
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> createFZdyVoucher(List<OcrInvoiceVO> invoiceList,String pk_corp,Map<String, Object[]> categoryMap,List<Object> paramList,List<List<Object[]>> levelList,String pk_user,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, ImageGroupVO> groupMap,Map<String, CategorysetVO> categorysetMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,String collectCategory,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, AuxiliaryAccountBVO> assistMap,Set<String> zyFzhsList,Map<String, InventoryAliasVO> fzhsBMMap,DZFBoolean isBankSubjLeaf)throws DZFWarpException{
		//1、要返回的凭证集合
		List<TzpzHVO> returnLists=new ArrayList<TzpzHVO>();
		CorpVO corp=corpService.queryByPk(pk_corp);
		//2、查分类入账规则
		Map<String, List<AccsetVO>> accsetMap=queryAccsetVOMap(corp.getCorptype(),null);
		//3、查关键字入账规则
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=queryAccsetKeywordBVO2Map(corp.getCorptype(),null);
		//4、把表体装到表头
		//5、得到凭证Map
		Map<String, List<TzpzHVO>> tzpzMap=new HashMap<String, List<TzpzHVO>>();
		//6、得到票据Map
		Map<String, OcrInvoiceVO> invoiceVOMap=new HashMap<String, OcrInvoiceVO>();
		//7、查集团科目
		Map<String, String> jituanSubMap=queryJituanSubj(corp.getCorptype());
		//8、编码规则
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(corp.getPk_corp());
		YntCpaccountVO[] accVOs=accountService.queryByPk(corp.getPk_corp());
		//9、获得当前公司行业编码，关键字规则用
		String tradeCode=getCurTradeCode(corp.getIndustry());
		//10、存货辅助核算
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
		if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO!=null&&inventorySetVO.getChcbjzfs()==InventoryConstant.IC_CHDLHS){
			chFzhsBodyVOs=fzhsBodyMap.get("000001000000000000000006");//存货辅助核算值
		}
		//11、如果开了银行分组要把转入、转出的票分开
		DZFBoolean isyh=((ParaSetVO)paramList.get(0)).getBankbillbyacc();
		//是否选择的是银行票据分类或者总的智能做账
		DZFBoolean isSelectyh=(StringUtil.isEmpty(collectCategory)||categoryMap.get(collectCategory)!=null&&categoryMap.get(collectCategory)[0].toString().equals(ZncsConst.FLCODE_YHPJ))?DZFBoolean.TRUE:DZFBoolean.FALSE;
		if(DZFBoolean.TRUE.equals(isyh)&&DZFBoolean.TRUE.equals(isSelectyh)){
			//开启了银行账户，要把非银一组，银行下有几个账号再加几组，最后把银行下的几组，再根据12合并或者不合并
			Map<String, List<OcrInvoiceVO>> tmpInvouceMap=new HashMap<String, List<OcrInvoiceVO>>();
			List<Map<String, List<TzpzHVO>>> allTzpzList=new ArrayList<Map<String, List<TzpzHVO>>>();
			for(int i=0;i<invoiceList.size();i++){
				OcrInvoiceVO invoiceVO=invoiceList.get(i);
				//3处理银行账号暂时都是非强制
				checkBankCode(corp, invoiceVO, bankAccountMap, checkMsgMap,isBankSubjLeaf);
				groupTempBankCode(invoiceVO, pk_corp,categoryMap,tmpInvouceMap);
			}
			Iterator<String> itor=tmpInvouceMap.keySet().iterator();
			while(itor.hasNext()){
				String key=itor.next();
				List<OcrInvoiceVO> curInvoiceList=tmpInvouceMap.get(key);
				tzpzMap=new HashMap<String, List<TzpzHVO>>();
				for(int i=0;i<curInvoiceList.size();i++){
					OcrInvoiceVO invoiceVO=curInvoiceList.get(i);
					TzpzHVO tzpzVO=creatTzpzVOByAccset(invoiceVO, StringUtil.isEmpty(invoiceVO.getPk_image_group())?null:groupMap.get(invoiceVO.getPk_image_group()), accsetMap, accsetKeywordBVO2Map, categorysetMap, categoryMap, paramList,corp,pk_user,bankAccountMap,jituanSubMap,newrule,accountMap,accVOs,currMap,rateMap,checkMsgMap,inventorySetVO,tradeCode,chFzhsBodyVOs,fzhsHeadMap,fzhsBodyMap,zyFzhsList,fzhsBMMap);
					if(tzpzMap.containsKey(invoiceVO.getPk_billcategory())){
						tzpzMap.get(invoiceVO.getPk_billcategory()).add(tzpzVO);
					}else{
						List<TzpzHVO> tmpList=new ArrayList<TzpzHVO>();
						tmpList.add(tzpzVO);
						tzpzMap.put(invoiceVO.getPk_billcategory(), tmpList);
					}
					invoiceVOMap.put(invoiceVO.getPk_invoice(), invoiceVO);
				}
				//12、合并凭证及分录 categorysetMap tzpzMap categoryMap paramList pk_category pk_bills
				List<VatInvoiceSetVO> vatSetVOList=(List<VatInvoiceSetVO>)paramList.get(1);//这里面有用的是摘要
				for(int i=0;i<levelList.size();i++){//一个有多少个level
					List<Object[]> categoryList=levelList.get(i);//得到这个level的所以分类
					for(int j=0;j<categoryList.size();j++){
						Object[] categoryObj=categoryList.get(j);//得到一个分类
						List<TzpzHVO> tmpTzpzList=tzpzMap.get(categoryObj[0]);//这个分类的凭证
						if(tmpTzpzList!=null&&tmpTzpzList.size()>0){
							List<TzpzHVO> newTmpTzpzList=(List<TzpzHVO>)OcrUtil.clonAll(tmpTzpzList);
							CategorysetVO categorySetVO=categorysetMap.get(categoryObj[0]);//分类对应的编辑目录
							DZFBoolean isleaf=new DZFBoolean(categoryObj[6]==null?"N":categoryObj[6].toString());
							int categorytype=categoryObj[7]==null?0:Integer.parseInt(categoryObj[7].toString());
							String categoryname=categoryObj[5]==null?"":categoryObj[5].toString();
							newTmpTzpzList=mergeTzpz(newTmpTzpzList, categorySetVO, vatSetVOList,invoiceVOMap,categoryMap,currMap,assistMap,categoryObj[1].toString(),isleaf,(ParaSetVO)paramList.get(0),categorytype,categoryname);
							//当前级次合并完，如果有上级把结果put到父级
							String pk_parentcategory=(String)categoryMap.get(categoryObj[0])[2];
							tzpzMap.put((String)categoryObj[0], newTmpTzpzList);
							if(!StringUtil.isEmpty(pk_parentcategory)){
								if(tzpzMap.get(pk_parentcategory)==null){
									tzpzMap.put(pk_parentcategory, (List<TzpzHVO>)OcrUtil.clonAll(newTmpTzpzList));
								}else{
									tzpzMap.get(pk_parentcategory).addAll(newTmpTzpzList);
								}
							}
						}
					}
				}
				allTzpzList.add(tzpzMap);
			}
			if(StringUtil.isEmpty(collectCategory)){
				List<Object[]> returnListObj=levelList.get(levelList.size()-1);
				for(int i=0;i<returnListObj.size();i++){
					for(int j=0;j<allTzpzList.size();j++){
						Map<String, List<TzpzHVO>> tmpTzpzMap=allTzpzList.get(j);
						if(tmpTzpzMap.get(returnListObj.get(i)[0])!=null){
							returnLists.addAll(tmpTzpzMap.get(returnListObj.get(i)[0]));
						}
					}
				}
			}else{
				for (int p = 0; p < levelList.size(); p++) {
					List<Object[]> returnListObj=levelList.get(p);
					for(int i=0;i<returnListObj.size();i++){
						for(int j=0;j<allTzpzList.size();j++){
							Map<String, List<TzpzHVO>> tmpTzpzMap=allTzpzList.get(j);
							if(collectCategory.equals(returnListObj.get(i)[0])&&tmpTzpzMap.get(returnListObj.get(i)[0])!=null){
								returnLists.addAll(tmpTzpzMap.get(returnListObj.get(i)[0]));
							}
						}
					}
					if(returnLists.size()>0)break;
				}
			}
		}else{
			for(int i=0;i<invoiceList.size();i++){
				OcrInvoiceVO invoiceVO=invoiceList.get(i);
				//3处理银行账号暂时都是非强制
				checkBankCode(corp, invoiceVO, bankAccountMap, checkMsgMap,isBankSubjLeaf);
				//10、生成凭证VO
				TzpzHVO tzpzVO=creatTzpzVOByAccset(invoiceVO, StringUtil.isEmpty(invoiceVO.getPk_image_group())?null:groupMap.get(invoiceVO.getPk_image_group()), accsetMap, accsetKeywordBVO2Map, categorysetMap, categoryMap, paramList,corp,pk_user,bankAccountMap,jituanSubMap,newrule,accountMap,accVOs,currMap,rateMap,checkMsgMap,inventorySetVO,tradeCode,chFzhsBodyVOs,fzhsHeadMap,fzhsBodyMap,zyFzhsList,fzhsBMMap);
				if(tzpzMap.containsKey(invoiceVO.getPk_billcategory())){
					tzpzMap.get(invoiceVO.getPk_billcategory()).add(tzpzVO);
				}else{
					List<TzpzHVO> tmpList=new ArrayList<TzpzHVO>();
					tmpList.add(tzpzVO);
					tzpzMap.put(invoiceVO.getPk_billcategory(), tmpList);
				}
				invoiceVOMap.put(invoiceVO.getPk_invoice(), invoiceVO);
			}
			//12、合并凭证及分录 categorysetMap tzpzMap categoryMap paramList pk_category pk_bills
			List<VatInvoiceSetVO> vatSetVOList=(List<VatInvoiceSetVO>)paramList.get(1);//这里面有用的是摘要
			for(int i=0;i<levelList.size();i++){//一个有多少个level
				List<Object[]> categoryList=levelList.get(i);//得到这个level的所以分类
				for(int j=0;j<categoryList.size();j++){
					Object[] categoryObj=categoryList.get(j);//得到一个分类
					List<TzpzHVO> tmpTzpzList=tzpzMap.get(categoryObj[0]);//这个分类的凭证
					if(tmpTzpzList!=null&&tmpTzpzList.size()>0){
						List<TzpzHVO> newTmpTzpzList=(List<TzpzHVO>)OcrUtil.clonAll(tmpTzpzList);
						CategorysetVO categorySetVO=categorysetMap.get(categoryObj[0]);//分类对应的编辑目录
						DZFBoolean isleaf=new DZFBoolean(categoryObj[6]==null?"N":categoryObj[6].toString());
						int categorytype=categoryObj[7]==null?0:Integer.parseInt(categoryObj[7].toString());
						String categoryname=categoryObj[5]==null?"":categoryObj[5].toString();
						newTmpTzpzList=mergeTzpz(newTmpTzpzList, categorySetVO, vatSetVOList,invoiceVOMap,categoryMap,currMap,assistMap,categoryObj[1].toString(),isleaf,(ParaSetVO)paramList.get(0),categorytype,categoryname);
						//当前级次合并完，如果有上级把结果put到父级
						String pk_parentcategory=(String)categoryMap.get(categoryObj[0])[2];
						tzpzMap.put((String)categoryObj[0], newTmpTzpzList);
						if(!StringUtil.isEmpty(pk_parentcategory)){
							if(tzpzMap.get(pk_parentcategory)==null){
								tzpzMap.put(pk_parentcategory, (List<TzpzHVO>)OcrUtil.clonAll(newTmpTzpzList));
							}else{
								tzpzMap.get(pk_parentcategory).addAll(newTmpTzpzList);
							}
						}
					}
				}
			}
			//至此已经所有的level都合并完，取levelList的最后一个结果返回
			if(StringUtil.isEmpty(collectCategory)){
				List<Object[]> returnListObj=levelList.get(levelList.size()-1);
				for(int i=0;i<returnListObj.size();i++){
					if(tzpzMap.get(returnListObj.get(i)[0])!=null){
						returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
					}
				}
			}else{
				for (int p = 0; p < levelList.size(); p++) {
					List<Object[]> returnListObj=levelList.get(p);
					for(int i=0;i<returnListObj.size();i++){
						if(collectCategory.startsWith("bank_")&&tzpzMap.get(returnListObj.get(i)[0])!=null){
							if(p==levelList.size()-2){//点中银行票据分组下的账号分组生成，把第一级的分类都加起来再返回
								returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
							}
						}
						if(collectCategory.equals(returnListObj.get(i)[0])&&tzpzMap.get(returnListObj.get(i)[0])!=null){
							returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
							break;
						}
					}
					if(returnLists.size()>0)break;
				}
			}
		}
		//13、参数：是否合并分录
//		if(DZFBoolean.TRUE.equals(((ParaSetVO)paramList.get(0)).getIsmergedetail())){
		if(true){//永远合并
			mergeTzpzDetail(returnLists, currMap, assistMap, null);
		}else{
			if(((ParaSetVO)paramList.get(0)).getOrderdetail()==1){//先借后贷
				sortTzpzDetail(returnLists);
			}
		}
		//14、处理凭证号，设置表头余额
		returnLists=setTzpzOtherInfo(returnLists,categoryMap,invoiceList);
		//15、返回凭证
		return returnLists;
	}
	
	/**
	 * 排序
	 * @param returnLists
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> sortTzpzHVOList(List<TzpzHVO> returnLists,List<OcrInvoiceVO> invoiceList)throws DZFWarpException{
		Map<String, Integer> iOrderMap=new HashMap<>();
		for (int i = 0; i < invoiceList.size(); i++) {
			iOrderMap.put(invoiceList.get(i).getPk_invoice(), invoiceList.get(i).getIorder());
		}
		for (int i = 0; i < returnLists.size(); i++) {
			TzpzHVO tzpzVO=returnLists.get(i);
			String pk_invoices=tzpzVO.getUserObject().toString();
			String[] strPk_invoice=pk_invoices.split(",");
			Integer minOrder=0;
			for (int j = 0; j < strPk_invoice.length; j++) {
				Integer iOrder = iOrderMap.get(strPk_invoice[j])==null?999:iOrderMap.get(strPk_invoice[j]);
				if(minOrder.intValue()==0||iOrder.intValue()<minOrder){
					minOrder=iOrder;
				}
			}
			tzpzVO.setVdef7(minOrder.toString());
		}
		Collections.sort(returnLists,new Comparator<Object>(){
			@Override
			public int compare(Object o1, Object o2) {
				TzpzHVO vo1=(TzpzHVO)o1;
				TzpzHVO vo2=(TzpzHVO)o2;
				return Integer.parseInt(vo1.getVdef7())-Integer.parseInt(vo2.getVdef7());
			}
		});
		if(returnLists.size()>0){
			String pzh=yntBoPubUtil.getNewVoucherNo(returnLists.get(0).getPk_corp(), returnLists.get(0).getDoperatedate());
			for (int i = 0; i < returnLists.size(); i++) {
				TzpzHVO tzpzVO=returnLists.get(i);
				tzpzVO.setPzh(pzh);//凭证号
				tzpzVO.setVdef7(null);
				pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
			}
		}
		return returnLists;
	}
	/**
	 * 处理自定义票据
	 * @param invoiceList
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> createZdyVoucher(List<OcrInvoiceVO> invoiceList,String pk_corp,Map<String, Object[]> categoryMap,List<Object> paramList,List<List<Object[]>> levelList,String pk_user,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, ImageGroupVO> groupMap,Map<String, CategorysetVO> categorysetMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,String collectCategory,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, AuxiliaryAccountBVO> assistMap,Map<String, InventoryAliasVO> fzhsBMMap,DZFBoolean isBankSubjLeaf)throws DZFWarpException{
		//1、要返回的凭证集合
		List<TzpzHVO> returnLists=new ArrayList<TzpzHVO>();
		//2、查该公司自定义凭证模板
		Map<String, List<VouchertempletHVO>> templetMap=groupVouchertempletVO(pk_corp);
		//3、得到凭证Map
		Map<String, List<TzpzHVO>> tzpzMap=new HashMap<String, List<TzpzHVO>>();
		//4、得到票据Map
		Map<String, OcrInvoiceVO> invoiceVOMap=new HashMap<String, OcrInvoiceVO>();
		CorpVO corp=corpService.queryByPk(pk_corp);
		
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			//3处理银行账号暂时都是非强制
			checkBankCode(corp, invoiceVO, bankAccountMap, checkMsgMap,isBankSubjLeaf);
			//5、判断该票据走哪个自定义凭证模板，现找所在分类
			List<VouchertempletHVO> templetList=searchVouchertempletHVO(invoiceVO, templetMap, categoryMap, null);
			if(templetList==null){
				checkZdyTemplte(checkMsgMap, invoiceVO, categoryMap);
			}else{
				//6、生成凭证VO
				TzpzHVO tzpzVO=creatTzpzVOByTemplet(invoiceVO, StringUtil.isEmpty(invoiceVO.getPk_image_group())?null:groupMap.get(invoiceVO.getPk_image_group()), templetList,categorysetMap,categoryMap,paramList,pk_user,bankAccountMap,currMap,accountMap,rateMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
				if(tzpzMap.containsKey(invoiceVO.getPk_billcategory())){
					tzpzMap.get(invoiceVO.getPk_billcategory()).add(tzpzVO);
				}else{
					List<TzpzHVO> tmpList=new ArrayList<TzpzHVO>();
					tmpList.add(tzpzVO);
					tzpzMap.put(invoiceVO.getPk_billcategory(), tmpList);
				}
				invoiceVOMap.put(invoiceVO.getPk_invoice(), invoiceVO);
			}
		}
		//7、合并凭证及分录 categorysetMap tzpzMap categoryMap paramList pk_category pk_bills
		List<VatInvoiceSetVO> vatSetVOList=(List<VatInvoiceSetVO>)paramList.get(1);//这里面有用的是摘要
		for(int i=0;i<levelList.size();i++){//一个有多少个level
			List<Object[]> categoryList=levelList.get(i);//得到这个level的所以分类
			for(int j=0;j<categoryList.size();j++){
				Object[] categoryObj=categoryList.get(j);//得到一个分类
				List<TzpzHVO> tmpTzpzList=tzpzMap.get(categoryObj[0]);//这个分类的凭证
				if(tmpTzpzList!=null&&tmpTzpzList.size()>0){
					List<TzpzHVO> newTmpTzpzList=(List<TzpzHVO>)OcrUtil.clonAll(tmpTzpzList);
					CategorysetVO categorySetVO=categorysetMap.get(categoryObj[0]);//分类对应的编辑目录
					DZFBoolean isleaf=new DZFBoolean(categoryObj[6]==null?"N":categoryObj[6].toString());
					int categorytype=categoryObj[7]==null?0:Integer.parseInt(categoryObj[7].toString());
					String categoryname=categoryObj[5]==null?"":categoryObj[5].toString();
					newTmpTzpzList=mergeTzpz(newTmpTzpzList, categorySetVO, vatSetVOList,invoiceVOMap,categoryMap,currMap,assistMap,categoryObj[1].toString(),isleaf,(ParaSetVO)paramList.get(0),categorytype,categoryname);
					//当前级次合并完，如果有上级把结果put到父级
					String pk_parentcategory=(String)categoryMap.get(categoryObj[0])[2];
					tzpzMap.put((String)categoryObj[0], newTmpTzpzList);
					if(!StringUtil.isEmpty(pk_parentcategory)){
						if(tzpzMap.get(pk_parentcategory)==null){
							tzpzMap.put(pk_parentcategory,(List<TzpzHVO>)OcrUtil.clonAll(newTmpTzpzList));
						}else{
							tzpzMap.get(pk_parentcategory).addAll(newTmpTzpzList);
						}
					}
				}
			}
		}
		//至此已经所有的level都合并完，取levelList的最后一个结果返回
		if(StringUtil.isEmpty(collectCategory)){
			List<Object[]> returnListObj=levelList.get(levelList.size()-1);
			for(int i=0;i<returnListObj.size();i++){
				if(tzpzMap.get(returnListObj.get(i)[0])!=null){
					returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
				}
			}
		}else{
			for (int p = 0; p < levelList.size(); p++) {
				List<Object[]> returnListObj=levelList.get(p);
				for(int i=0;i<returnListObj.size();i++){
					if(collectCategory.startsWith("bank_")&&tzpzMap.get(returnListObj.get(i)[0])!=null){
						if(p==levelList.size()-2){//点中银行票据分组下的账号分组生成，把第一级的分类都加起来再返回
							returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
						}
					}
					if(collectCategory.equals(returnListObj.get(i)[0])&&tzpzMap.get(returnListObj.get(i)[0])!=null){
						returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
						break;
					}
				}
				if(returnLists.size()>0)break;
			}
		}
		//8、参数：是否合并分录
//		if(DZFBoolean.TRUE.equals(((ParaSetVO)paramList.get(0)).getIsmergedetail())){
		if(true){//永远合并
			mergeTzpzDetail(returnLists, currMap, assistMap, null);
		}else{
			if(((ParaSetVO)paramList.get(0)).getOrderdetail()==1){//先借后贷
				sortTzpzDetail(returnLists);
			}
		}
		//9、处理凭证号，设置表头余额
		returnLists=setTzpzOtherInfo(returnLists,categoryMap,invoiceList);
		//10、返回凭证
		return returnLists;
	}
	
	/**
	 * 分录排序
	 * @param returnLists
	 */
	private void sortTzpzDetail(List<TzpzHVO> returnLists){
		for(int i=0;i<returnLists.size();i++){
			TzpzBVO[] details=(TzpzBVO[])returnLists.get(i).getChildren();
			List<TzpzBVO> jfList=new ArrayList<TzpzBVO>();
			List<TzpzBVO> dfList=new ArrayList<TzpzBVO>();
			for (int j = 0; j < details.length; j++) {
				TzpzBVO tzpzBVO = details[j];
				if(tzpzBVO.getJfmny().compareTo(DZFDouble.ZERO_DBL)>0){//借方
					jfList.add(tzpzBVO);
				}else{//贷方
					dfList.add(tzpzBVO);
				}
			}
			jfList.addAll(dfList);
			returnLists.get(i).setChildren(jfList.toArray(new TzpzBVO[0]));
		}
	}
	/**
	 * 设置凭照，余额等
	 * @param returnLists
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> setTzpzOtherInfo(List<TzpzHVO> returnLists,Map<String, Object[]> categoryMap,List<OcrInvoiceVO> invoiceList)throws DZFWarpException{
		if(returnLists==null||returnLists.size()==0){
			return returnLists;
		}
		Map<String, String> invoiceCodeMap=getInvoiceKeyCategoryCode(categoryMap, invoiceList);
		for(int i=0;i<returnLists.size();i++){
			TzpzHVO headVO=returnLists.get(i);
			TzpzBVO[] bodyVOs=(TzpzBVO[])headVO.getChildren();
			List<TzpzBVO> nullLineBodyList=new ArrayList<TzpzBVO>();
			
			DZFDouble jfmny = DZFDouble.ZERO_DBL;
			DZFDouble dfmny = DZFDouble.ZERO_DBL;
			DZFBoolean isErrorNumPrice=DZFBoolean.FALSE;
			DZFBoolean isErrorZyFz=DZFBoolean.FALSE;
			int isErrorWbCount=0;
			DZFBoolean isErrorWb2=DZFBoolean.FALSE;
			for (TzpzBVO bvo : bodyVOs) {
				jfmny = SafeCompute.add(jfmny, bvo.getJfmny());
				dfmny = SafeCompute.add(dfmny, bvo.getDfmny());
				if(bvo.getNnumber()!=null||bvo.getNprice()!=null){
					if(bvo.getJfmny().compareTo(DZFDouble.ZERO_DBL)!=0){
						if(bvo.getNnumber().multiply(bvo.getNprice()).setScale(2, DZFDouble.ROUND_HALF_UP).compareTo(bvo.getJfmny())!=0){
							if(bvo.getJfmny().div(bvo.getNnumber()).sub(bvo.getNprice()).abs().compareTo(new DZFDouble(0.02))>0){
								isErrorNumPrice=DZFBoolean.TRUE;
							}else{
								bvo.setNprice(bvo.getNprice());
							}
						}
					}else{
						if(bvo.getNnumber().multiply(bvo.getNprice()).setScale(2, DZFDouble.ROUND_HALF_UP).compareTo(bvo.getDfmny())!=0){
							if(bvo.getDfmny().div(bvo.getNnumber()).sub(bvo.getNprice()).abs().compareTo(new DZFDouble(0.02))>0){
								isErrorNumPrice=DZFBoolean.TRUE;
							}else{
								bvo.setNprice(bvo.getNprice());
							}
						}
					}
				}
				if(bvo.getJfmny()!=null&&bvo.getJfmny().compareTo(DZFDouble.ZERO_DBL)!=0||bvo.getDfmny()!=null&&bvo.getDfmny().compareTo(DZFDouble.ZERO_DBL)!=0){
					nullLineBodyList.add(bvo);
				}
				if(!StringUtil.isEmpty(bvo.getVdef5())&&bvo.getVdef5().equals("error")){
					isErrorZyFz=DZFBoolean.TRUE;
					bvo.setVdef5(null);
				}
				if(!StringUtil.isEmpty(bvo.getVdef6())&&bvo.getVdef6().equals("error")){
					isErrorWbCount++;
					bvo.setVdef6(null);
				}
				if(!StringUtil.isEmpty(bvo.getVdef6())&&bvo.getVdef6().equals("error2")){
					isErrorWb2=DZFBoolean.TRUE;
					bvo.setVdef6(null);
				}
				if(!StringUtil.isEmpty(bvo.getVdef2())&&bvo.getVdef2().equals("bsc")){
					bvo.setVdef2(null);
					headVO.setIsbsctaxitem(DZFBoolean.TRUE);
				}
			}
			headVO.setJfmny(jfmny);
			headVO.setDfmny(dfmny);
			if(nullLineBodyList.size()>0){
				headVO.setChildren(nullLineBodyList.toArray(new TzpzBVO[0]));
			}else{
				headVO.setVbillstatus(255);//分录金额都为空
			}
			if(jfmny.compareTo(dfmny)!=0){
				headVO.setVbillstatus(251);//借贷不相等
			}
			if(DZFBoolean.TRUE.equals(isErrorNumPrice)){
				headVO.setVbillstatus(253);//金额、数量、单价不等
			}
			if(DZFBoolean.TRUE.equals(isErrorZyFz)){
				headVO.setVbillstatus(254);//职员辅助有问题
			}
			if(isErrorWbCount==bodyVOs.length){
				headVO.setVbillstatus(256);//票是外币，所以科目都没启用这个外币
			}
			if(DZFBoolean.TRUE.equals(isErrorWb2)){
				headVO.setVbillstatus(257);//票是外币，没设置汇率
			}
			if(headVO.getUserObject()!=null){
				String[] invoiceKeys=headVO.getUserObject().toString().split(",");
				for (int j = 0; j < invoiceKeys.length; j++) {
					String key = invoiceKeys[j];
					if(invoiceCodeMap.get(key).equals(ZncsConst.FLCODE_LXZC)||invoiceCodeMap.get(key).equals(ZncsConst.FLCODE_SB)){
						headVO.setVbillstatus(258);//利息支出\社保
					}
				}
			}
		}
		return returnLists;
	}
	
	/**
	 * 返回票主键和它所在分类的编码的map
	 * @param categoryMap
	 * @param invoiceList
	 * @return
	 */
	private Map<String, String> getInvoiceKeyCategoryCode(Map<String, Object[]> categoryMap,List<OcrInvoiceVO> invoiceList){
		Map<String, String> returnMap=new HashMap<String, String>();
		if(invoiceList!=null&&invoiceList.size()>0){
			for(int i=0;i<invoiceList.size();i++){
				returnMap.put(invoiceList.get(i).getPk_invoice(), categoryMap.get(invoiceList.get(i).getPk_billcategory())[0].toString());
			}
		}
		return returnMap;
	}
	/**
	 * 合并凭证或分录
	 * @param tmpTzpzList 待合并的凭证
	 * @param categorySetVO 编辑目录 上面的合并方式有用
	 * @param vatSetVOList 参数设置 合并分录的方式有用
	 * @param invoiceVOMap 发票map
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> mergeTzpz(List<TzpzHVO> tmpTzpzList,CategorysetVO categorySetVO,List<VatInvoiceSetVO> vatSetVOList,Map<String, OcrInvoiceVO> invoiceVOMap,Map<String, Object[]> categoryMap,Map<String, BdCurrencyVO> currMap,Map<String, AuxiliaryAccountBVO> assistMap,String categorycode,DZFBoolean isleaf,ParaSetVO paramVO,int categorytype,String categoryname)throws DZFWarpException{
		//默认规则一个分类下，专票和普票不能在一个凭证上，普票和其他票可以在一张凭证上
		if(categorytype==ZncsConst.CATEGORYTYPE_4||categorySetVO!=null&&!ZncsConst.HBFS_0.equals(categorySetVO.getMergemode())){//合并凭证_下面不会走了
			tmpTzpzList=mergeTzpzHead(tmpTzpzList, invoiceVOMap,categoryMap,categorycode,isleaf,paramVO);
		}
//		else if(categorytype==ZncsConst.CATEGORYTYPE_4||categorySetVO!=null&&!ZncsConst.HBFS_0.equals(categorySetVO.getMergemode())){//合并分录
//			tmpTzpzList=mergeTzpzHead(tmpTzpzList, invoiceVOMap,categoryMap,categorycode,isleaf,paramVO);
////			OcrInvoiceVO invoiceVO=invoiceVOMap.get(tmpTzpzList.get(0).getUserObject().toString().split(",")[0]);
////			String zy=vatSetVO.getZy();//设置里的摘要规则
//			String zdyzy=null;
//			if(categorytype==4&&(categorySetVO==null||StringUtil.isEmpty(categorySetVO.getZdyzy()))){
//				if(categorycode.startsWith(ZncsConst.FLCODE_SR)){
//					zdyzy="向"+categoryname+"销售";
//				}else if(categorycode.startsWith(ZncsConst.FLCODE_KC)){
//					zdyzy="向"+categoryname+"采购";
//				}else if(categorycode.startsWith(ZncsConst.FLCODE_CB)){
//					zdyzy="向"+categoryname+"购入";
//				}else if(categorycode.startsWith(ZncsConst.FLCODE_YHZR)){
//					zdyzy="从"+categoryname+"转入";
//				}else if(categorycode.startsWith(ZncsConst.FLCODE_YHZC)){
//					zdyzy="向"+categoryname+"转出";
//				}
//			}else{
//				zdyzy=categorySetVO==null?null:categorySetVO.getZdyzy();
//			}
//			mergeTzpzDetail(tmpTzpzList, currMap, assistMap, zdyzy);
//		}
		else{//不合并
		}
		return tmpTzpzList;
	}
	
	/**
	 * 合并凭证分录
	 * @throws DZFWarpException
	 */
	private void mergeTzpzDetail(List<TzpzHVO> tmpTzpzList,Map<String, BdCurrencyVO> currMap,Map<String, AuxiliaryAccountBVO> assistMap,String zdyzy)throws DZFWarpException{
		//调用zpm
		for (int i = 0; i < tmpTzpzList.size(); i++) {
			TzpzBVO[] oldTzpzVOs=(TzpzBVO[])tmpTzpzList.get(i).getChildren();
			for (int j = 0; j < oldTzpzVOs.length; j++) {
				TzpzBVO tzpzBVO = oldTzpzVOs[j];
				StringBuilder fullcode = new StringBuilder();
				if (tzpzBVO.getVcode() != null) {
					fullcode.append(tzpzBVO.getVcode());
				}
				for (int k = 1; k <= 10; k++) {
					String fzhsID = (String) tzpzBVO.getAttributeValue("fzhsx" +k);
					if (i == 6 && StringUtil.isEmpty(fzhsID)) {
						fzhsID = tzpzBVO.getPk_inventory();
					}
					if (!StringUtil.isEmpty(fzhsID)) {
						AuxiliaryAccountBVO assist = assistMap.get(fzhsID);
						if (assist != null) {
							fullcode.append("_").append(assist.getCode());
						}
					}
				}
				if (tzpzBVO.getPk_currency() != null
						&& !IGlobalConstants.RMB_currency_id
								.equals(tzpzBVO.getPk_currency())) {
					if (currMap.containsKey(tzpzBVO.getPk_currency())) {
						fullcode.append("_").append(
								currMap.get(tzpzBVO.getPk_currency()).getCurrencycode());
					}
				}
				tzpzBVO.setFullcode(fullcode.toString());
			}
			TzpzBVO[] newTzpzVOs=iPzglService.mergeVoucherEntries(tmpTzpzList.get(i).getPk_corp(), (TzpzBVO[])OcrUtil.clonAll(oldTzpzVOs), zdyzy);
			tmpTzpzList.get(i).setChildren(newTzpzVOs==null||newTzpzVOs.length==0?oldTzpzVOs:newTzpzVOs);
		}
	}
	
	private DZFBoolean isNeedNewTzpzVO_bankCustmoer(String categorycode,DZFBoolean isleaf,ParaSetVO paramVO)throws DZFWarpException{
		if((categorycode.startsWith(ZncsConst.FLCODE_YHZR)||categorycode.startsWith(ZncsConst.FLCODE_YHZC))&&DZFBoolean.TRUE.equals(paramVO.getBankinoutclass())&&
				DZFBoolean.TRUE.equals(isleaf)){
			return DZFBoolean.TRUE;
		}else{
			return DZFBoolean.FALSE;
		}
	}
	
	/**
	 * 判断是否需要重新生成一个新的凭证
	 * @return
	 * @throws DZFWarpException
	 */
	private DZFBoolean isNeedNewTzpzVO_billAmount(String categorycode,DZFBoolean isleaf,ParaSetVO paramVO,TzpzHVO tzpzHVO)throws DZFWarpException{
		if(!categorycode.startsWith(ZncsConst.FLCODE_SR)&&!categorycode.startsWith(ZncsConst.FLCODE_KC)&&!categorycode.startsWith(ZncsConst.FLCODE_YHPJ)){
			return DZFBoolean.FALSE;
		}
		if(DZFBoolean.FALSE.equals(isleaf)){
			return DZFBoolean.FALSE;
		}
		if(paramVO.getMergebillnum()==1){
			return DZFBoolean.FALSE;
		}
		if(tzpzHVO.getNbills()!=null&&tzpzHVO.getNbills().equals(paramVO.getMergebillnum())){
			if(categorycode.startsWith(ZncsConst.FLCODE_SR)&&DZFBoolean.TRUE.equals(paramVO.getIsmergeincome())
					||categorycode.startsWith(ZncsConst.FLCODE_KC)&&DZFBoolean.TRUE.equals(paramVO.getIsmergeic())
					||categorycode.startsWith(ZncsConst.FLCODE_YHPJ)&&DZFBoolean.TRUE.equals(paramVO.getIsmergebank())){
				return DZFBoolean.TRUE;
			}
		}
		
		return DZFBoolean.FALSE;
	}
	/**
	 * 合并凭证头
	 * @param tmpTzpzList
	 * @param invoiceVOMap
	 * @return
	 * @throws DZFWarpException
	 */
	private List<TzpzHVO> mergeTzpzHead(List<TzpzHVO> tmpTzpzList,Map<String, OcrInvoiceVO> invoiceVOMap,Map<String, Object[]> categoryMap,String categorycode,DZFBoolean isleaf,ParaSetVO paramVO)throws DZFWarpException{
		List<TzpzHVO> returnList=new ArrayList<TzpzHVO>();
		TzpzHVO TzpzHVO1=new TzpzHVO();//银行
		TzpzHVO TzpzHVO2=new TzpzHVO();//资产(专)
		TzpzHVO TzpzHVO3=new TzpzHVO();//专进项(或专入库)
		TzpzHVO TzpzHVO4=new TzpzHVO();//普进项(或普入库)+其他
		TzpzHVO TzpzHVO5=new TzpzHVO();//专销项(或专出库)
		TzpzHVO TzpzHVO6=new TzpzHVO();//普销项(或普出库)
		TzpzHVO TzpzHVO7=new TzpzHVO();//未开其他
		TzpzHVO TzpzHVO8=new TzpzHVO();//null其他
		TzpzHVO TzpzHVO9=new TzpzHVO();//资产(普)
		
		List<TzpzBVO> tzpzBVOList1=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList2=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList3=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList4=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList5=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList6=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList7=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList8=new ArrayList<TzpzBVO>();
		List<TzpzBVO> tzpzBVOList9=new ArrayList<TzpzBVO>();
		
		List<TzpzHVO> ListTzpzHVO1=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO2=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO3=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO4=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO5=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO6=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO7=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO8=new ArrayList<TzpzHVO>();
		List<TzpzHVO> ListTzpzHVO9=new ArrayList<TzpzHVO>();
		
		Map<String, TzpzHVO> bankCustomerMap=new HashMap<String, TzpzHVO>();
		CorpVO corp=corpService.queryByPk(paramVO.getPk_corp());
		for(int i=0;i<tmpTzpzList.size();i++){
			Integer otherTicketDirection=null;//其他票据的方向，决定和进项合还是销项合，没方向就和进项合 0:进项 1销项
			OcrInvoiceVO invoiceVO=invoiceVOMap.get(tmpTzpzList.get(i).getUserObject().toString().split(",")[0]);//取出凭证对应的票据
			String istate=invoiceVO.getIstate();//发票大类
			String invoicetype=invoiceVO.getInvoicetype();//发票小类
			
			String customer1=StringUtil.isEmpty(invoiceVO.getVpurchname())?"~":invoiceVO.getVpurchname();//购方名称
			String customer2=StringUtil.isEmpty(invoiceVO.getVsalename())?"~":invoiceVO.getVsalename();//销方名称
			
			if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP110)){
				if(OcrUtil.isSameCompany(corp.getUnitname(), customer2)){
					otherTicketDirection=1;
				}else{
					otherTicketDirection=0;
				}
			}
			
			if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP85)){//银行
//				if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_bankCustmoer(categorycode, isleaf, paramVO))){
//					if(OcrUtil.isSameCompany(corp.getUnitname(), customer1)){
//						if(bankCustomerMap.containsKey(customer2)){
//							TzpzHVO1=bankCustomerMap.get(customer2);
//							tzpzBVOList1=new ArrayList<TzpzBVO>();
//							TzpzBVO[] bodyVOs=(TzpzBVO[])TzpzHVO1.getChildren();
//							for (int j = 0; j < bodyVOs.length; j++) {
//								tzpzBVOList1.add(bodyVOs[j]);
//							}
//						}else{
//							TzpzHVO1=new TzpzHVO();
//							tzpzBVOList1=new ArrayList<TzpzBVO>(); 
//						}
//						bankCustomerMap.put(customer2,TzpzHVO1);
//					}else if(OcrUtil.isSameCompany(corp.getUnitname(), customer2)){
//						if(bankCustomerMap.containsKey(customer1)){
//							TzpzHVO1=bankCustomerMap.get(customer1);
//							tzpzBVOList1=new ArrayList<TzpzBVO>();
//							TzpzBVO[] bodyVOs=(TzpzBVO[])TzpzHVO1.getChildren();
//							for (int j = 0; j < bodyVOs.length; j++) {
//								tzpzBVOList1.add(bodyVOs[j]);
//							}
//						}else{
//							TzpzHVO1=new TzpzHVO();
//							tzpzBVOList1=new ArrayList<TzpzBVO>();
//						}
//						bankCustomerMap.put(customer1,TzpzHVO1);
//					}
//				}else 
				if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO1))){
					TzpzHVO1.setChildren(tzpzBVOList1.toArray(new TzpzBVO[0]));
					ListTzpzHVO1.add(TzpzHVO1);
					TzpzHVO1=new TzpzHVO();
					tzpzBVOList1=new ArrayList<TzpzBVO>(); 
				}
				if(StringUtil.isEmpty(TzpzHVO1.getPk_corp())){
					TzpzHVO1.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
					TzpzHVO1.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
					TzpzHVO1.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
					TzpzHVO1.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
					TzpzHVO1.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
				}else{
					TzpzHVO1.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO1.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO1.getDoperatedate());//取大的
					TzpzHVO1.setSourcebillid(TzpzHVO1.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
					TzpzHVO1.setNbills(TzpzHVO1.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
					TzpzHVO1.setPk_image_group(TzpzHVO1.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
					TzpzHVO1.setUserObject(TzpzHVO1.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
				}
				TzpzHVO1.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
				TzpzHVO1.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
				TzpzHVO1.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
				TzpzHVO1.setIshasjz(DZFBoolean.FALSE);//是否记账
				TzpzHVO1.setDr(0);
				TzpzHVO1.setPzlb(0);// 凭证类别：记账
				TzpzHVO1.setSourcebilltype(IBillTypeCode.HP85);// 来源单据类型_销项
				TzpzHVO1.setIsfpxjxm(DZFBoolean.FALSE);
				TzpzHVO1.setVyear(tmpTzpzList.get(i).getVyear());//会计年
				TzpzHVO1.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
				TzpzHVO1.setIsocr(DZFBoolean.TRUE);
				TzpzHVO1.setIautorecognize(1);//0-- 非识别 1----识别
				TzpzHVO1.setFp_style(null);
				tzpzBVOList1.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				if(bankCustomerMap.size()>0){
					TzpzHVO1.setChildren(tzpzBVOList1.toArray(new TzpzBVO[0]));
				}
			}else if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP59)){//资产
				if(!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_3)&&!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){//专资产
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO2))){
						TzpzHVO2.setChildren(tzpzBVOList2.toArray(new TzpzBVO[0]));
						ListTzpzHVO2.add(TzpzHVO2);
						TzpzHVO2=new TzpzHVO();
						tzpzBVOList2=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO2.getPk_corp())){
						TzpzHVO2.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO2.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO2.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO2.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO2.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO2.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO2.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO2.getDoperatedate());//取大的
						TzpzHVO2.setSourcebillid(TzpzHVO2.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO2.setNbills(TzpzHVO2.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO2.setPk_image_group(TzpzHVO2.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO2.setUserObject(TzpzHVO2.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO2.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO2.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO2.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO2.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO2.setDr(0);
					TzpzHVO2.setPzlb(0);// 凭证类别：记账
					TzpzHVO2.setSourcebilltype(IBillTypeCode.HP59);// 来源单据类型_销项
					TzpzHVO2.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO2.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO2.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO2.setIsocr(DZFBoolean.TRUE);
					TzpzHVO2.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO2.setFp_style(2);
					tzpzBVOList2.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}else{
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO9))){
						TzpzHVO9.setChildren(tzpzBVOList9.toArray(new TzpzBVO[0]));
						ListTzpzHVO9.add(TzpzHVO9);
						TzpzHVO9=new TzpzHVO();
						tzpzBVOList9=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO9.getPk_corp())){
						TzpzHVO9.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO9.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO9.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO9.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO9.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO9.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO9.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO9.getDoperatedate());//取大的
						TzpzHVO9.setSourcebillid(TzpzHVO9.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO9.setNbills(TzpzHVO9.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO9.setPk_image_group(TzpzHVO9.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO9.setUserObject(TzpzHVO9.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO9.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO9.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO9.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO9.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO9.setDr(0);
					TzpzHVO9.setPzlb(0);// 凭证类别：记账
					TzpzHVO9.setSourcebilltype(IBillTypeCode.HP59);// 来源单据类型_销项
					TzpzHVO9.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO9.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO9.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO9.setIsocr(DZFBoolean.TRUE);
					TzpzHVO9.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO9.setFp_style(1);
					tzpzBVOList9.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}
			}else if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP70)||tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP95)||(otherTicketDirection!=null&&otherTicketDirection==0)){//进项或者入库或其他
				if(!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_3)&&!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){//专进项或专入库
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO3))){
						TzpzHVO3.setChildren(tzpzBVOList3.toArray(new TzpzBVO[0]));
						ListTzpzHVO3.add(TzpzHVO3);
						TzpzHVO3=new TzpzHVO();
						tzpzBVOList3=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO3.getPk_corp())){
						TzpzHVO3.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO3.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO3.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO3.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO3.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO3.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO3.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO3.getDoperatedate());//取大的
						TzpzHVO3.setSourcebillid(TzpzHVO3.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO3.setNbills(TzpzHVO3.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO3.setPk_image_group(TzpzHVO3.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO3.setUserObject(TzpzHVO3.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO3.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO3.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO3.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO3.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO3.setDr(0);
					TzpzHVO3.setPzlb(0);// 凭证类别：记账
					if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP70)||!StringUtil.isEmpty(TzpzHVO3.getSourcebilltype())&&TzpzHVO3.getSourcebilltype().equals(IBillTypeCode.HP70)){
						TzpzHVO3.setSourcebilltype(IBillTypeCode.HP70);// 来源单据类型_入库
					}else{
						TzpzHVO3.setSourcebilltype(IBillTypeCode.HP95);// 来源单据类型_进项
					}
					TzpzHVO3.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO3.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO3.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO3.setIsocr(DZFBoolean.TRUE);
					TzpzHVO3.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO3.setFp_style(2);
					tzpzBVOList3.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}else{//普进项或普入库
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO4))){
						TzpzHVO4.setChildren(tzpzBVOList4.toArray(new TzpzBVO[0]));
						ListTzpzHVO4.add(TzpzHVO4);
						TzpzHVO4=new TzpzHVO();
						tzpzBVOList4=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO4.getPk_corp())){
						TzpzHVO4.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO4.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO4.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO4.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO4.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO4.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO4.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO4.getDoperatedate());//取大的
						TzpzHVO4.setSourcebillid(TzpzHVO4.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO4.setNbills(TzpzHVO4.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO4.setPk_image_group(TzpzHVO4.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO4.setUserObject(TzpzHVO4.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO4.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO4.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO4.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO4.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO4.setDr(0);
					TzpzHVO4.setPzlb(0);// 凭证类别：记账
					if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP70)||!StringUtil.isEmpty(TzpzHVO4.getSourcebilltype())&&TzpzHVO4.getSourcebilltype().equals(IBillTypeCode.HP70)){
						TzpzHVO4.setSourcebilltype(IBillTypeCode.HP70);// 来源单据类型_入库
					}else{
						TzpzHVO4.setSourcebilltype(IBillTypeCode.HP95);// 来源单据类型_进项
					}
					TzpzHVO4.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO4.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO4.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO4.setIsocr(DZFBoolean.TRUE);
					TzpzHVO4.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO4.setFp_style(1);
					tzpzBVOList4.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}
			}else if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP75)||tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP90)||(otherTicketDirection!=null&&otherTicketDirection==1)){//销项或者出库
				if(tmpTzpzList.get(i).getFp_style()!=null&&tmpTzpzList.get(i).getFp_style()==2){//专销项或专出库
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO5))){
						TzpzHVO5.setChildren(tzpzBVOList5.toArray(new TzpzBVO[0]));
						ListTzpzHVO5.add(TzpzHVO5);
						TzpzHVO5=new TzpzHVO();
						tzpzBVOList5=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO5.getPk_corp())){
						TzpzHVO5.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO5.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO5.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO5.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO5.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO5.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO5.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO5.getDoperatedate());//取大的
						TzpzHVO5.setSourcebillid(TzpzHVO5.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO5.setNbills(TzpzHVO5.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO5.setPk_image_group(TzpzHVO5.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO5.setUserObject(TzpzHVO5.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO5.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO5.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO5.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO5.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO5.setDr(0);
					TzpzHVO5.setPzlb(0);// 凭证类别：记账
					if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP75)||!StringUtil.isEmpty(TzpzHVO5.getSourcebilltype())&&TzpzHVO5.getSourcebilltype().equals(IBillTypeCode.HP75)){
						TzpzHVO5.setSourcebilltype(IBillTypeCode.HP75);// 来源单据类型_出库
					}else{
						TzpzHVO5.setSourcebilltype(IBillTypeCode.HP90);// 来源单据类型_销项
					}
					TzpzHVO5.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO5.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO5.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO5.setIsocr(DZFBoolean.TRUE);
					TzpzHVO5.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO5.setFp_style(2);
					tzpzBVOList5.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}else if(tmpTzpzList.get(i).getFp_style()!=null&&tmpTzpzList.get(i).getFp_style()==1){//普销项或普出库
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO6))){
						TzpzHVO6.setChildren(tzpzBVOList6.toArray(new TzpzBVO[0]));
						ListTzpzHVO6.add(TzpzHVO6);
						TzpzHVO6=new TzpzHVO();
						tzpzBVOList6=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO6.getPk_corp())){
						TzpzHVO6.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO6.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO6.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO6.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO6.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO6.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO6.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO6.getDoperatedate());//取大的
						TzpzHVO6.setSourcebillid(TzpzHVO6.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO6.setNbills(TzpzHVO6.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO6.setPk_image_group(TzpzHVO6.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO6.setUserObject(TzpzHVO6.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO6.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO6.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO6.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO6.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO6.setDr(0);
					TzpzHVO6.setPzlb(0);// 凭证类别：记账
					if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP75)||!StringUtil.isEmpty(TzpzHVO6.getSourcebilltype())&&TzpzHVO6.getSourcebilltype().equals(IBillTypeCode.HP75)){
						TzpzHVO6.setSourcebilltype(IBillTypeCode.HP75);// 来源单据类型_出库
					}else{
						TzpzHVO6.setSourcebilltype(IBillTypeCode.HP90);// 来源单据类型_销项
					}
					TzpzHVO6.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO6.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO6.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO6.setIsocr(DZFBoolean.TRUE);
					TzpzHVO6.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO6.setFp_style(1);
					tzpzBVOList6.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}else if(tmpTzpzList.get(i).getFp_style()!=null&&tmpTzpzList.get(i).getFp_style()==3){//未开其他
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO7))){
						TzpzHVO7.setChildren(tzpzBVOList7.toArray(new TzpzBVO[0]));
						ListTzpzHVO7.add(TzpzHVO7);
						TzpzHVO7=new TzpzHVO();
						tzpzBVOList7=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO7.getPk_corp())){
						TzpzHVO7.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO7.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO7.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO7.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO7.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO7.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO7.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO7.getDoperatedate());//取大的
						TzpzHVO7.setSourcebillid(TzpzHVO7.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO7.setNbills(TzpzHVO7.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO7.setPk_image_group(TzpzHVO7.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO7.setUserObject(TzpzHVO7.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO7.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO7.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO7.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO7.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO7.setDr(0);
					TzpzHVO7.setPzlb(0);// 凭证类别：记账
					if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP75)||!StringUtil.isEmpty(TzpzHVO7.getSourcebilltype())&&TzpzHVO7.getSourcebilltype().equals(IBillTypeCode.HP75)){
						TzpzHVO7.setSourcebilltype(IBillTypeCode.HP75);// 来源单据类型_出库
					}else{
						TzpzHVO7.setSourcebilltype(IBillTypeCode.HP110);// 来源单据类型_销项
					}
					TzpzHVO7.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO7.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO7.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO7.setIsocr(DZFBoolean.TRUE);
					TzpzHVO7.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO7.setFp_style(3);
					tzpzBVOList7.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}else if(tmpTzpzList.get(i).getFp_style()==null){//null其他
					if(DZFBoolean.TRUE.equals(isNeedNewTzpzVO_billAmount(categorycode, isleaf, paramVO, TzpzHVO8))){
						TzpzHVO8.setChildren(tzpzBVOList8.toArray(new TzpzBVO[0]));
						ListTzpzHVO8.add(TzpzHVO8);
						TzpzHVO8=new TzpzHVO();
						tzpzBVOList8=new ArrayList<TzpzBVO>(); 
					}
					if(StringUtil.isEmpty(TzpzHVO8.getPk_corp())){
						TzpzHVO8.setDoperatedate(tmpTzpzList.get(i).getDoperatedate());//开票日期
						TzpzHVO8.setSourcebillid(tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO8.setNbills(tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO8.setPk_image_group(tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO8.setUserObject(tmpTzpzList.get(i).getUserObject());//凭证上放入票的主键
					}else{
						TzpzHVO8.setDoperatedate(tmpTzpzList.get(i).getDoperatedate().after(TzpzHVO8.getDoperatedate())?tmpTzpzList.get(i).getDoperatedate():TzpzHVO8.getDoperatedate());//取大的
						TzpzHVO8.setSourcebillid(TzpzHVO8.getSourcebillid()+","+tmpTzpzList.get(i).getSourcebillid());//来源ID
						TzpzHVO8.setNbills(TzpzHVO8.getNbills()+tmpTzpzList.get(i).getNbills());// 设置单据张数
						TzpzHVO8.setPk_image_group(TzpzHVO8.getPk_image_group()+","+tmpTzpzList.get(i).getPk_image_group());
						TzpzHVO8.setUserObject(TzpzHVO8.getUserObject().toString()+","+tmpTzpzList.get(i).getUserObject().toString());//凭证上放入票的主键
					}
					TzpzHVO8.setPk_corp(tmpTzpzList.get(i).getPk_corp());//公司
					TzpzHVO8.setCoperatorid(tmpTzpzList.get(i).getCoperatorid());//制单人
					TzpzHVO8.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
					TzpzHVO8.setIshasjz(DZFBoolean.FALSE);//是否记账
					TzpzHVO8.setDr(0);
					TzpzHVO8.setPzlb(0);// 凭证类别：记账
					if(tmpTzpzList.get(i).getSourcebilltype().equals(IBillTypeCode.HP85)||!StringUtil.isEmpty(TzpzHVO8.getSourcebilltype())&&TzpzHVO8.getSourcebilltype().equals(IBillTypeCode.HP85)){
						TzpzHVO8.setSourcebilltype(IBillTypeCode.HP85);// 来源单据类型_出库
					}else{
						TzpzHVO8.setSourcebilltype(IBillTypeCode.HP110);// 来源单据类型_销项
					}
					TzpzHVO8.setIsfpxjxm(DZFBoolean.FALSE);
					TzpzHVO8.setVyear(tmpTzpzList.get(i).getVyear());//会计年
					TzpzHVO8.setPeriod(tmpTzpzList.get(i).getPeriod());//会计期间
					TzpzHVO8.setIsocr(DZFBoolean.TRUE);
					TzpzHVO8.setIautorecognize(1);//0-- 非识别 1----识别
					TzpzHVO8.setFp_style(null);
					tzpzBVOList8.addAll(Arrays.asList(((TzpzBVO[])tmpTzpzList.get(i).getChildren())));
				}
			}
		}
		if(bankCustomerMap.size()>0){
			Iterator<String> itor=bankCustomerMap.keySet().iterator();
			while(itor.hasNext()){
				ListTzpzHVO1.add(bankCustomerMap.get(itor.next()));
			}
		}else{
			if(!StringUtil.isEmpty(TzpzHVO1.getPk_corp())){
				TzpzHVO1.setChildren(tzpzBVOList1.toArray(new TzpzBVO[0]));
				ListTzpzHVO1.add(TzpzHVO1);
			}
		}
		if(!StringUtil.isEmpty(TzpzHVO2.getPk_corp())){
			TzpzHVO2.setChildren(tzpzBVOList2.toArray(new TzpzBVO[0]));
			ListTzpzHVO2.add(TzpzHVO2);
		}
		if(!StringUtil.isEmpty(TzpzHVO9.getPk_corp())){
			TzpzHVO9.setChildren(tzpzBVOList9.toArray(new TzpzBVO[0]));
			ListTzpzHVO9.add(TzpzHVO9);
		}
		if(!StringUtil.isEmpty(TzpzHVO3.getPk_corp())){
			TzpzHVO3.setChildren(tzpzBVOList3.toArray(new TzpzBVO[0]));
			ListTzpzHVO3.add(TzpzHVO3);
		}
		if(!StringUtil.isEmpty(TzpzHVO4.getPk_corp())){
			TzpzHVO4.setChildren(tzpzBVOList4.toArray(new TzpzBVO[0]));
			ListTzpzHVO4.add(TzpzHVO4);
		}
		if(!StringUtil.isEmpty(TzpzHVO5.getPk_corp())){
			TzpzHVO5.setChildren(tzpzBVOList5.toArray(new TzpzBVO[0]));
			ListTzpzHVO5.add(TzpzHVO5);
		}
		if(!StringUtil.isEmpty(TzpzHVO6.getPk_corp())){
			TzpzHVO6.setChildren(tzpzBVOList6.toArray(new TzpzBVO[0]));
			ListTzpzHVO6.add(TzpzHVO6);
		}
		if(!StringUtil.isEmpty(TzpzHVO7.getPk_corp())){
			TzpzHVO7.setChildren(tzpzBVOList7.toArray(new TzpzBVO[0]));
			ListTzpzHVO7.add(TzpzHVO7);
		}
		if(!StringUtil.isEmpty(TzpzHVO8.getPk_corp())){
			TzpzHVO8.setChildren(tzpzBVOList8.toArray(new TzpzBVO[0]));
			ListTzpzHVO8.add(TzpzHVO8);
		}
		if(ListTzpzHVO1.size()>0){
			returnList.addAll(ListTzpzHVO1);
		}
		if(ListTzpzHVO2.size()>0){
			returnList.addAll(ListTzpzHVO2);
		}
		if(ListTzpzHVO3.size()>0){
			returnList.addAll(ListTzpzHVO3);
		}
		if(ListTzpzHVO4.size()>0){
			returnList.addAll(ListTzpzHVO4);
		}
		if(ListTzpzHVO5.size()>0){
			returnList.addAll(ListTzpzHVO5);
		}
		if(ListTzpzHVO6.size()>0){
			returnList.addAll(ListTzpzHVO6);
		}
		if(ListTzpzHVO7.size()>0){
			returnList.addAll(ListTzpzHVO7);
		}
		if(ListTzpzHVO8.size()>0){
			returnList.addAll(ListTzpzHVO8);
		}
		if(ListTzpzHVO9.size()>0){
			returnList.addAll(ListTzpzHVO9);
		}
		return returnList;
	}
	/**
	 * 通过自定义模板生成凭证VO
	 * @param invoiceVO
	 * @param groupVO
	 * @param templetList
	 * @return
	 * @throws DZFWarpException
	 */
	private TzpzHVO creatTzpzVOByTemplet(OcrInvoiceVO invoiceVO, ImageGroupVO groupVO,List<VouchertempletHVO> templetList,Map<String, CategorysetVO> categorysetMap,Map<String, Object[]> categoryMap,List<Object> paramList,String pk_user,Map<String, String> bankAccountMap,Map<String, BdCurrencyVO> currMap,Map<String,YntCpaccountVO> accountMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, InventoryAliasVO> fzhsBMMap) throws DZFWarpException {
		TzpzHVO headVO=new TzpzHVO();
		//表头
		headVO=createTzpzHVO(headVO, groupVO,paramList,invoiceVO,pk_user,categoryMap);
		if(headVO.getSourcebilltype().equals(IBillTypeCode.HP59)){
			headVO.setSourcebilltype(IBillTypeCode.HP95);
		}
		//表体
		TzpzBVO[] bodyVOs=createTzpzBVOByTemplet(invoiceVO, groupVO, templetList,categorysetMap,categoryMap,bankAccountMap,accountMap,currMap,rateMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
		//合并
		headVO.setChildren(bodyVOs);
		return headVO;
	}
	
	/**
	 * 通过分类匹配规则和关键字规则生成凭证VO
	 * @param paramList
	 * @return
	 * @throws DZFWarpException
	 */
	private TzpzHVO creatTzpzVOByAccset(OcrInvoiceVO invoiceVO, ImageGroupVO groupVO,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, CategorysetVO> categorysetMap,Map<String, Object[]> categoryMap,List<Object> paramList,CorpVO corp,String pk_user,Map<String, String> bankAccountMap,Map<String, String> jituanSubMap,String newrule,Map<String,YntCpaccountVO> accountMap,YntCpaccountVO[] accVOs,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,String tradeCode,List<AuxiliaryAccountBVO> chFzhsBodyVOs,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Set<String> zyFzhsList,Map<String, InventoryAliasVO> fzhsBMMap) throws DZFWarpException {
		TzpzHVO headVO=new TzpzHVO();
		//表头
		headVO=createTzpzHVO(headVO, groupVO,paramList,invoiceVO,pk_user,categoryMap);
		//表体
		TzpzBVO[] bodyVOs=createTzpzBVOByAccset(invoiceVO, groupVO, accsetMap,accsetKeywordBVO2Map,categorysetMap,categoryMap,corp,paramList,bankAccountMap,jituanSubMap,newrule,accountMap,accVOs,currMap,rateMap,checkMsgMap,inventorySetVO,tradeCode,chFzhsBodyVOs,fzhsHeadMap,fzhsBodyMap,zyFzhsList,fzhsBMMap);
		//合并
		headVO.setChildren(bodyVOs);
		return headVO;
	}
	
	private TzpzHVO creatTzpzVOByError(OcrInvoiceVO invoiceVO, ImageGroupVO groupVO,Map<String, Object[]> categoryMap,List<Object> paramList,String pk_user) throws DZFWarpException {
		TzpzHVO headVO=new TzpzHVO();
		//表头
		headVO=createTzpzHVO(headVO, groupVO,paramList,invoiceVO,pk_user,categoryMap);
		String pk_category=invoiceVO.getPk_billcategory();
		if(categoryMap.get(pk_category)[0].toString().startsWith(ZncsConst.FLCODE_WSB)){
			headVO.setIautorecognize(0);
		}
		if(headVO.getSourcebilltype().equals(IBillTypeCode.HP59)){
			headVO.setSourcebilltype(IBillTypeCode.HP95);
		}
		headVO.setVbillstatus(252);//问题或未识别
		TzpzBVO[] tzpzBVOs=new TzpzBVO[2];

		TzpzBVO tzpzBVO1=new TzpzBVO();
		TzpzBVO tzpzBVO2=new TzpzBVO();
		
		tzpzBVOs[0]=tzpzBVO1;
		tzpzBVOs[1]=tzpzBVO2;
		headVO.setChildren(tzpzBVOs);
		return headVO;
	}
	
	private DZFBoolean isHasSH(String fplx,CorpVO corp,OcrInvoiceVO invoiceVO,OcrInvoiceDetailVO detailVO){
		DZFBoolean isHasSH=DZFBoolean.FALSE;
		String chargedeptname=corp.getChargedeptname();//公司性质
		String invoiceytype=StringUtil.isEmpty(invoiceVO.getInvoicetype())?"":invoiceVO.getInvoicetype();
		//一般、小规模的普票、专票的销项都有税行
		//一般人、专票、进项有税行
		if(!StringUtil.isEmpty(fplx) && (fplx.equals(ZncsConst.SBZT_3)||(fplx.equals(ZncsConst.SBZT_2)&&(!StringUtil.isEmpty(invoiceVO.getStaffname()) || invoiceytype.equals("c火车票") || invoiceytype.equals("c航空运输电子客票行程单"))))) {//增值税发票
			String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
			if (!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(corp.getUnitname(),saleName)|| OcrUtil.isSameCompany(saleName,corp.getUnitname())) {	//是销项票
				isHasSH=DZFBoolean.TRUE;
			} else {	//进项
				if (!StringUtil.isEmpty(invoiceVO.getInvoicetype()) && (invoiceVO.getInvoicetype().indexOf("增值税专用发票")>-1||invoiceVO.getInvoicetype().indexOf("机动车销售统一发票")>-1||invoiceVO.getInvoicetype().indexOf("通行费增值税电子普通发票")>-1) && !"小规模纳税人".equals(chargedeptname)){ //进项、一般人、专票

					isHasSH=DZFBoolean.TRUE;
				} 
				else { 	//增值税电子普通发票，税率是3%和9%， 货物名称包含"客运服务"和“”
					String date=StringUtil.isEmpty(invoiceVO.getDinvoicedate())?null:invoiceVO.getDinvoicedate().replace("年", "-").replace("月", "-").replace("日", "");
					if(date!=null){
						try{
							new DZFDate(date); 
						}catch(Exception e1){
							date=null;
						}
					}
					if (!"小规模纳税人".equals(chargedeptname)&&!StringUtil.isEmpty(invoiceVO.getInvoicetype()) && invoiceVO.getInvoicetype().indexOf("增值税电子普通发票")>-1&&date!=null&&new DZFDate(date).after(new DZFDate("2019-03-31"))
							&& (detailVO.getInvname().indexOf("*运输服务*")>-1||((OcrUtil.getInvoiceSL(detailVO.getItemtaxrate()).div(100).compareTo(new DZFDouble(0.03))==0
							||OcrUtil.getInvoiceSL(detailVO.getItemtaxrate()).div(100).compareTo(new DZFDouble(0.09))==0)&&(detailVO.getInvname().indexOf("*旅客运输*")>-1))
							)){
						isHasSH=DZFBoolean.TRUE;
					}
					//水路运输客会走下面
					if(!"小规模纳税人".equals(chargedeptname)&&date!=null&&new DZFDate(date).after(new DZFDate("2019-03-31"))&&(fplx.equals(ZncsConst.SBZT_2)&&(!StringUtil.isEmpty(invoiceVO.getStaffname()) || invoiceytype.equals("c火车票")|| invoiceytype.equals("c航空运输电子客票行程单")))){
						isHasSH=DZFBoolean.TRUE;
					}
					//收购(左上角标志)
					if(!"小规模纳税人".equals(chargedeptname)&&!StringUtil.isEmpty(invoiceVO.getVmemo())&&invoiceVO.getVmemo().equals("收购(左上角标志)")){
						isHasSH=DZFBoolean.TRUE;
					}
				}
				
			}
		}
		return isHasSH;
	}
	/**
	 * 通过入账规则生成凭证体
	 * @return
	 * @throws DZFWarpException
	 */
	private TzpzBVO[] createTzpzBVOByAccset(OcrInvoiceVO invoiceVO, ImageGroupVO groupVO,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, CategorysetVO> categorysetMap,Map<String, Object[]> categoryMap,CorpVO corp,List<Object> paramList,Map<String, String> bankAccountMap,Map<String, String> jituanSubMap,String newrule,Map<String,YntCpaccountVO> accountMap,YntCpaccountVO[] accVOs,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,String tradeCode,List<AuxiliaryAccountBVO> chFzhsBodyVOs,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Set<String> zyFzhsList,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		//1、取票据表体
		OcrInvoiceDetailVO[] detailVOs= (OcrInvoiceDetailVO[])invoiceVO.getChildren();
		String fplx=invoiceVO.getIstate();//发票类型
		List<TzpzBVO> returnList=new ArrayList<TzpzBVO>();
		//2、凭证分录
		for(int i=0;i<detailVOs.length;i++){
			//2、判断是否有税行
			DZFBoolean isHasSH=isHasSH(fplx, corp, invoiceVO,detailVOs[i]);
			//分类
			String pk_category=null;
			if(!StringUtil.isEmpty(detailVOs[i].getPk_billcategory())&&!categoryMap.get(detailVOs[i].getPk_billcategory())[0].toString().startsWith("18")){
				pk_category=detailVOs[i].getPk_billcategory();
			}else{
				pk_category=invoiceVO.getPk_billcategory();
			}
			AccsetVO accsetVO=getAccsetVOByCategory(pk_category, accsetMap, categoryMap, corp,invoiceVO,zyFzhsList);
			AccsetKeywordBVO2 accsetKeywordBVO2=getAccsetKeywordBVO2ByCategory(accsetVO.getPk_basecategory(), accsetKeywordBVO2Map, categoryMap, corp,invoiceVO,detailVOs[i],tradeCode);//有可能是null
			TzpzBVO[] bodyVOs=buildTzpzBVOsByAccset(accsetVO, accsetKeywordBVO2, invoiceVO, corp, accountMap, categorysetMap, bankAccountMap, categoryMap, isHasSH, paramList,detailVOs[i],jituanSubMap,newrule,accVOs,currMap,rateMap,checkMsgMap,inventorySetVO,chFzhsBodyVOs,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
			returnList.addAll(Arrays.asList(bodyVOs));
		}
		return returnList.toArray(new TzpzBVO[0]);
	}
	
	/**
	 * 根据设置、编辑目录、入账规则、关键字规则获得摘要
	 * @param invoiceVO
	 * @param corp
	 * @param paramList
	 * @return
	 */
	private String getZyByRule(OcrInvoiceVO invoiceVO,CorpVO corp,List<Object> paramList,Map<String, CategorysetVO> categorysetMap,AccsetVO accsetVO,AccsetKeywordBVO2 accsetKeywordBVO2,Map<String, Object[]> categoryMap,OcrInvoiceDetailVO detailVO){
		List<VatInvoiceSetVO> vatSetVOList=(List<VatInvoiceSetVO>)paramList.get(1);
		VatInvoiceSetVO vatSetVO=null;
		if(!StringUtil.isEmpty(invoiceVO.getIstate())&&invoiceVO.getIstate().equals(ZncsConst.SBZT_1)){
			vatSetVO=vatSetVOList.get(0);
		}else if(!StringUtil.isEmpty(invoiceVO.getIstate())&&invoiceVO.getIstate().equals(ZncsConst.SBZT_3)){
			if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){//购方
				vatSetVO=vatSetVOList.get(2);
			}else{
				vatSetVO=vatSetVOList.get(1);
			}
			if(vatSetVO==null){
				vatSetVO=new VatInvoiceSetVO();
				vatSetVO.setZy("$selectWlZy$selectLxZy$selectXmZy$$");
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){//购方
					vatSetVO.setStyle("3");
				}else{
					vatSetVO.setStyle("2");
				}
				
			}
		}
		String zy="";
		if(vatSetVO!=null){
			//设置
			if(!StringUtil.isEmpty(vatSetVO.getZy())){
				String pk_billcategory=StringUtil.isEmpty(detailVO.getPk_billcategory())?invoiceVO.getPk_billcategory():detailVO.getPk_billcategory();
				String categoryName=categoryMap.get(pk_billcategory)[4].toString();//分类名称
				//selectQjZy$selectWlZy$selectLxZy$selectXmZy$selectHmZy$selectZdyZy:w222$
				if(vatSetVO.getZy().indexOf("selectQjZy")!=-1){//日期
					zy+=StringUtil.isEmpty(invoiceVO.getDinvoicedate())?"":invoiceVO.getDinvoicedate();
				}
				String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
				if(vatSetVO.getZy().indexOf("selectWlZy")!=-1){//往来单位
					if(vatSetVO.getStyle().equals("1")){
						if(categoryName.equals("转入")){
							if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(corp.getUnitname(),saleName)){
								zy+=StringUtil.isEmpty(invoiceVO.getVpurchname())?"":"从"+invoiceVO.getVpurchname();
							}else if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){
								zy+=StringUtil.isEmpty(saleName)?"":"从"+saleName;
							}
						}else if(categoryName.equals("转出")){
							if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){
								zy+=StringUtil.isEmpty(saleName)?"":"向"+saleName;
							}else if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(corp.getUnitname(),saleName)){
								zy+=StringUtil.isEmpty(invoiceVO.getVpurchname())?"":"向"+invoiceVO.getVpurchname();
							}
						}else if(categoryName.equals("工资")||categoryName.equals("手续费")||categoryName.equals("利息支出")||categoryName.equals("工会经费")||categoryName.equals("公积金")
								||categoryName.equals("残保金")){
							zy+="付";
						}else if(categoryName.equals("利息收入")||categoryName.equals("退款")||categoryName.equals("实收资本")||categoryName.equals("分红")||categoryName.equals("理财收入")||categoryName.equals("补贴")){
							zy+="收";
						}else if(categoryName.equals("社保")||categoryName.equals("罚款")){
							zy+="缴";
						}else{
							//户间转账等
						}
					}else{
						if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){
							zy+="向"+(StringUtil.isEmpty(saleName)?"":saleName);
						}else if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(corp.getUnitname(),saleName)){
							zy+="向"+(StringUtil.isEmpty(invoiceVO.getVpurchname())?"":invoiceVO.getVpurchname());
						}
					}
				}
				if(vatSetVO.getZy().indexOf("selectLxZy")!=-1){//业务类型
					if(vatSetVO.getStyle().equals("1")){//银行
						zy+=categoryName;
					}else if(vatSetVO.getStyle().equals("2")){//2销项发票  
						zy+="销售";
					}if(vatSetVO.getStyle().equals("3")){//3进项发票 
						zy+="采购";
					}
				}
				if(vatSetVO.getZy().indexOf("selectXmZy")!=-1){//开票项目
					String xm=detailVO.getInvname();
					zy+=StringUtil.isEmpty(xm)?"":xm;
				}
				if(vatSetVO.getZy().indexOf("selectHmZy")!=-1){//发票号码
					zy+=StringUtil.isEmpty(invoiceVO.getVinvoiceno())?"":invoiceVO.getVinvoiceno();
				}
			}
		}
		//自定义
		CategorysetVO setVO=categorysetMap.get(invoiceVO.getPk_billcategory());
		if(setVO!=null&&!StringUtil.isEmpty(setVO.getZdyzy())){
			zy+=setVO.getZdyzy();
		}
		//关键字
		if(StringUtil.isEmpty(zy)&&accsetKeywordBVO2!=null){
			zy=accsetKeywordBVO2.getZy();
		}
		//分类
		if(StringUtil.isEmpty(zy)&&accsetVO!=null){
			zy=accsetVO.getZy();
		}
		return zy;
	}
	
	/**
	 * 通过规则去科目
	 * @return
	 * @throws DZFWarpException
	 */
	private String getPk_accsubjByRule(Integer i,AccsetVO accsetVO,AccsetKeywordBVO2 accsetKeywordBVO2,CorpVO corp,CategorysetVO categorysetVO,List<Object> paramList,Map<String, String> jituanSubMap,String newrule,YntCpaccountVO[] accVOs,OcrInvoiceVO invoiceVO,OcrInvoiceDetailVO detailVO,List<AuxiliaryAccountBVO> chFzhsBodyVOs,Map<String, Object[]> categoryMap,InventorySetVO inventorySetVO,Map<String, InventoryAliasVO> fzhsBMMap,TzpzBVO tzpzBVO)throws DZFWarpException{
		String pk_accsubj=null;//编辑目录入账科目
		String pk_settlementaccsubj=null;//编辑目录结算科目
		String pk_taxaccsubj=null;//税行科目
		int settlement=ZncsConst.JSFS_0;//结算方式
		if(detailVO!=null&&categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith(ZncsConst.FLCODE_YHPJ)){
			settlement=ZncsConst.JSFS_2;
		}
		DZFBoolean isTurnAccsubj=DZFBoolean.FALSE;//入账科目是否需要转公司科目
		DZFBoolean isTurnSetsubj=DZFBoolean.FALSE;//结算科目是否需要转公司科目
		DZFBoolean isTurnTaxsubj=DZFBoolean.FALSE;//税行科目是否需要转公司科目
		if(detailVO!=null){
			String invName=OcrUtil.execInvname(detailVO.getInvname());
			if(chFzhsBodyVOs!=null){//总账核算-大类
				String strName2="";
				if(inventorySetVO.getChppjscgz()==0){
					strName2=invName+(StringUtil.isEmpty(detailVO.getInvtype())?"":detailVO.getInvtype())+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
				}else{
					strName2=invName+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
				}
				if(fzhsBMMap.get(strName2)!=null){
					InventoryAliasVO bmVO=fzhsBMMap.get(strName2);
					if(categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("11")){//找入库科目
						pk_accsubj=bmVO.getKmclassify();//入库科目
					}else if(categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("101015")||
							categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("101110")){//找出库科目
						pk_accsubj=bmVO.getChukukmid();//出库科目
					}
				}else{
					if(categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("11")){//找入库科目
						for(AuxiliaryAccountBVO fzhsBodyVO:chFzhsBodyVOs){
							String strName1="";
							if(inventorySetVO.getChppjscgz()==0){
								strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getSpec())?"":fzhsBodyVO.getSpec())+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
							}else{
								strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
							}
							if(strName1.equals(strName2)){
								pk_accsubj=fzhsBodyVO.getKmclassify();//入库科目
							}
						}
					}else if(categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("101015")||
							categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("101110")){//找出库科目
						for(AuxiliaryAccountBVO fzhsBodyVO:chFzhsBodyVOs){
							String strName1="";
							if(inventorySetVO.getChppjscgz()==0){
								strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getSpec())?"":fzhsBodyVO.getSpec())+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
							}else{
								strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
							}
							if(strName1.equals(strName2)){
								pk_accsubj=fzhsBodyVO.getChukukmid();//出库科目
							}
						}
					}
				}
			}
			if(IcCostStyle.IC_ON.equals(corp.getBbuildic())){//启用库存去存货找-只找入库科目
				if(categoryMap.get(detailVO.getPk_billcategory())[0].toString().startsWith("11")){//找入库科目
					InventoryVO invVO=getInventoryVOByName(invName, detailVO.getInvtype(),detailVO.getItemunit(), corp.getPk_corp());
					if(invVO!=null){
						pk_accsubj=invVO.getPk_subject();
					}
				}
			}
		}
		if(categorysetVO!=null){
			if(StringUtil.isEmpty(pk_accsubj)){
				pk_accsubj=categorysetVO.getPk_accsubj();
			}
			pk_settlementaccsubj=categorysetVO.getPk_settlementaccsubj();
			if(categorysetVO.getSettlement()!=null){
				settlement=categorysetVO.getSettlement();
			}
			pk_taxaccsubj=categorysetVO.getPk_taxaccsubj();
		}
		//如果进项、销项、银行传过来有结算方式和科目按传过来的走
 		if(invoiceVO!=null&&invoiceVO.getSettlement()!=null){
			settlement=invoiceVO.getSettlement();
		}
		if(invoiceVO!=null&&!StringUtil.isEmpty(invoiceVO.getPk_subject())){
			pk_accsubj=invoiceVO.getPk_subject();
		}
		if(invoiceVO!=null&&!StringUtil.isEmpty(invoiceVO.getPk_settlementaccsubj())){
			pk_settlementaccsubj=invoiceVO.getPk_settlementaccsubj();
		}
		if(invoiceVO!=null&&!StringUtil.isEmpty(invoiceVO.getPk_taxaccsubj())){
			pk_taxaccsubj=invoiceVO.getPk_taxaccsubj();
		}
		if(StringUtil.isEmpty(pk_accsubj)){
			isTurnAccsubj=DZFBoolean.TRUE;
		}
		if(StringUtil.isEmpty(pk_settlementaccsubj)){
			isTurnSetsubj=DZFBoolean.TRUE;
		}
		if(StringUtil.isEmpty(pk_taxaccsubj)){
			isTurnTaxsubj=DZFBoolean.TRUE;
		}
		DZFBoolean rzjg=DZFBoolean.FALSE;
		if(invoiceVO==null||StringUtil.isEmpty(invoiceVO.getRzjg())){
			rzjg=((ParaSetVO)paramList.get(0)).getInvidentify();
		}else{
			rzjg=new DZFBoolean(invoiceVO.getRzjg().equals("0")?false:true);
		}
		if(i==2&&DZFBoolean.FALSE.equals(rzjg)){
			if(!StringUtil.isEmpty(invoiceVO.getIstate())&&invoiceVO.getIstate().equals(ZncsConst.SBZT_3)){
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){
					String invname=detailVO.getInvname();//名称
					String sl=detailVO.getItemtaxrate();
					DZFDouble slDbl=DZFDouble.ZERO_DBL;//税率
					if (!StringUtil.isEmpty(sl)) {
						sl = sl.replaceAll("%", "");
						try {
							slDbl = new DZFDouble(sl, 2).div(100);
						} catch (Exception e) {
						}
					}
					String invoicetype=invoiceVO.getInvoicetype();//发票小类
					String date=StringUtil.isEmpty(invoiceVO.getDinvoicedate())?null:invoiceVO.getDinvoicedate().replace("年", "-").replace("月", "-").replace("日", "");
					if(date!=null){
						try{
							new DZFDate(date); 
						}catch(Exception e1){
							date=null;
						}
					}
					if(((invname.indexOf("*旅客运输*")>-1&&(slDbl.compareTo(new DZFDouble(0.09))==0||slDbl.compareTo(new DZFDouble(0.03))==0))||invname.indexOf("*运输服务*")>-1)
							&&date!=null&&new DZFDate(date).after(new DZFDate("2019-03-31"))){
						if(!StringUtil.isEmpty(invoicetype)&&invoicetype.indexOf("增值税电子普通发票")>-1){
							rzjg=DZFBoolean.TRUE;
						}
					}
				}
			}
		}
		if(accsetKeywordBVO2!=null){
			if(StringUtil.isEmpty(pk_accsubj)){
				pk_accsubj=accsetKeywordBVO2.getPk_accsubj();
			}
			if(StringUtil.isEmpty(pk_taxaccsubj)){
				if(DZFBoolean.TRUE.equals(rzjg)){
					pk_taxaccsubj=accsetKeywordBVO2.getPk_taxaccsubj1();
				}else{
					pk_taxaccsubj=accsetKeywordBVO2.getPk_taxaccsubj2();
				}
			}
		}else{
			if(StringUtil.isEmpty(pk_accsubj)){
				pk_accsubj=accsetVO.getPk_accsubj();
			}
			if(StringUtil.isEmpty(pk_taxaccsubj)){
				if(DZFBoolean.TRUE.equals(rzjg)){
					pk_taxaccsubj=accsetVO.getPk_taxaccsubj1();
				}else{
					pk_taxaccsubj=accsetVO.getPk_taxaccsubj2();
				}
			}
		}
		if(StringUtil.isEmpty(pk_settlementaccsubj)){
			if(settlement==ZncsConst.JSFS_0){
				pk_settlementaccsubj=accsetVO.getPk_settlementaccsubj_exg();//往来
			}else if(settlement==ZncsConst.JSFS_1){
				pk_settlementaccsubj=accsetVO.getPk_settlementaccsubj_cash();//现金
			}else{
				pk_settlementaccsubj=accsetVO.getPk_settlementaccsubj_bank();//银行
			}
		}
		ICpaccountCodeRuleService gl_accountcoderule = (ICpaccountCodeRuleService) SpringUtils.getBean("gl_accountcoderule");
		String kmnew_accsubj=null;
		String kmnew_setsubj=null;
		String kmnew_taxsubj=null;
		if(DZFBoolean.TRUE.equals(isTurnAccsubj)){
			kmnew_accsubj = gl_accountcoderule.getNewRuleCode(jituanSubMap.get(pk_accsubj), DZFConstant.ACCOUNTCODERULE, newrule);
		}
		if(DZFBoolean.TRUE.equals(isTurnSetsubj)){
			kmnew_setsubj = gl_accountcoderule.getNewRuleCode(jituanSubMap.get(pk_settlementaccsubj), DZFConstant.ACCOUNTCODERULE, newrule);
		}
		if(DZFBoolean.TRUE.equals(isTurnTaxsubj)&&!StringUtil.isEmpty(pk_taxaccsubj)){
			kmnew_taxsubj = gl_accountcoderule.getNewRuleCode(jituanSubMap.get(pk_taxaccsubj), DZFConstant.ACCOUNTCODERULE, newrule);
		}
		int ipk_sccsubjCodeLength = 0;
		int ipk_settlementaccsubjCodeLength = 0;
		int ipk_taxsubjCodeLength = 0;
		boolean foundpk_sccsubj = false;
		boolean foundpk_settlementaccsubj = false;
		boolean foundpk_taxsubj = false;
		for (int j = 0; j < accVOs.length; j++) {
			YntCpaccountVO yntCpaccountVO = accVOs[j];
			if(!StringUtil.isEmpty(kmnew_accsubj)) {
				if (yntCpaccountVO.getAccountcode().equals(kmnew_accsubj)){
					pk_accsubj=yntCpaccountVO.getPk_corp_account();
					foundpk_sccsubj = true;
				}
				else if (!foundpk_sccsubj && (yntCpaccountVO.getAccountcode().startsWith(kmnew_accsubj) || kmnew_accsubj.startsWith(yntCpaccountVO.getAccountcode())))
				{
					if (yntCpaccountVO.getAccountcode().length() > ipk_sccsubjCodeLength)
					{
						ipk_sccsubjCodeLength = yntCpaccountVO.getAccountcode().length();
						pk_accsubj=yntCpaccountVO.getPk_corp_account();
					}
				}
			}
			if(!StringUtil.isEmpty(kmnew_setsubj)) {
				if (yntCpaccountVO.getAccountcode().equals(kmnew_setsubj)){
					pk_settlementaccsubj=yntCpaccountVO.getPk_corp_account();
					foundpk_settlementaccsubj = true;
				}
				else if (!foundpk_settlementaccsubj && (yntCpaccountVO.getAccountcode().startsWith(kmnew_setsubj) || kmnew_setsubj.startsWith(yntCpaccountVO.getAccountcode())))
				{
					if (yntCpaccountVO.getAccountcode().length() > ipk_settlementaccsubjCodeLength)
					{
						ipk_settlementaccsubjCodeLength = yntCpaccountVO.getAccountcode().length();
						pk_settlementaccsubj=yntCpaccountVO.getPk_corp_account();
					}
				}
			}
			if(!StringUtil.isEmpty(kmnew_taxsubj)) {
				if (yntCpaccountVO.getAccountcode().equals(kmnew_taxsubj)){
					pk_taxaccsubj=yntCpaccountVO.getPk_corp_account();
					foundpk_taxsubj = true;
				}
				else if (!foundpk_taxsubj && (yntCpaccountVO.getAccountcode().startsWith(kmnew_taxsubj) || kmnew_taxsubj.startsWith(yntCpaccountVO.getAccountcode())))
				{
					if (yntCpaccountVO.getAccountcode().length() > ipk_taxsubjCodeLength)
					{
						ipk_taxsubjCodeLength = yntCpaccountVO.getAccountcode().length();
						pk_taxaccsubj=yntCpaccountVO.getPk_corp_account();
					}
				}
			}
		}
		if(i==0)return pk_accsubj;
		if(i==1)return pk_settlementaccsubj;
		if(i==2){
			if(tzpzBVO!=null){
				tzpzBVO.setIstaxsubj(DZFBoolean.TRUE);
			}
			return pk_taxaccsubj;
		}
		return null;
	}
	
	/**
	 * 查集团科目主键、编码对照表
	 * @param pk_accountschema
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, String> queryJituanSubj(String pk_accountschema)throws DZFWarpException{
		String sql="select pk_trade_account,accountcode from ynt_tdacc where nvl(dr,0)=0 and pk_trade_accountschema=? and nvl(pk_corp,'000001')=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_accountschema);
		sp.addParam(IDefaultValue.DefaultGroup);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		Map<String, String> returnMap=new HashMap<String, String>();
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				returnMap.put(list.get(i)[0].toString(), list.get(i)[1].toString());
			}
		}
		return returnMap;
	}
	/**
	 * 判断票是否是外币
	 * @return
	 * @throws DZFWarpException
	 */
	private String getCurrency(OcrInvoiceVO invoiceVO,OcrInvoiceDetailVO detailVO,Map<String, BdCurrencyVO> currMap,TzpzBVO tzpzBVO,YntCpaccountVO accountVO,Map<String, Object[]> rateMap)throws DZFWarpException{
		String jine=(detailVO==null||StringUtil.isEmpty(detailVO.getItemmny()))?invoiceVO.getNmny():detailVO.getItemmny();
		String shuie=(detailVO==null||StringUtil.isEmpty(detailVO.getItemtaxmny()))?invoiceVO.getNtaxnmny():OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny());
		String headhj=invoiceVO.getNtotaltax();
		Iterator<String> itor=currMap.keySet().iterator();
		while(itor.hasNext()){
			String key=itor.next();
			if(!StringUtil.isEmpty(jine)&&jine.startsWith(key)||!StringUtil.isEmpty(shuie)&&shuie.startsWith(key)||!StringUtil.isEmpty(headhj)&&headhj.startsWith(key)){
				String pk_currency=currMap.get(key).getPk_currency();
				if(DZFBoolean.TRUE.equals(accountVO.getIswhhs())&&!StringUtil.isEmpty(accountVO.getExc_pk_currency())&&accountVO.getExc_pk_currency().contains(pk_currency)){
					tzpzBVO.setCur_code(key);
					return pk_currency;
				}else{
					Object[] rateObj=rateMap.get(pk_currency);//0汇率1模式(1是除else乘)
					if(rateObj!=null){
						tzpzBVO.setNrate(new DZFDouble(rateObj[0].toString()));
						int model=Integer.parseInt(rateObj[1].toString());
						tzpzBVO.setVdef9(String.valueOf(model));
						tzpzBVO.setVdef6("error");
					}else{
						tzpzBVO.setVdef6("error2");
						tzpzBVO.setNrate(new DZFDouble(1));
						tzpzBVO.setVdef9(String.valueOf(1));
					}
				}
			}
		}
		tzpzBVO.setCur_code("CNY");
		return DzfUtil.PK_CNY;
	}
	private boolean isMatchAccountCode(YntCpaccountVO account,CorpVO corp) {
		String[] accCodes = null;
		if ("00000100AA10000000000BMD".equals(corp.getCorptype())) {//小企业会计准则
			accCodes=new String[] { "1122", "2202","1221", "224103", "2241003","2203","1123"};
		} else if ("00000100AA10000000000BMF".equals(corp.getCorptype())) {//企业会计准则
			accCodes=new String[] { "1122", "2202","1221", "224103", "2241003","2203","1123"};
		} else if("00000100000000Ig4yfE0005".equals(corp.getCorptype())){//企业会计制度
			accCodes=new String[] { "1131", "2121","1133", "218103", "2181003","2131","1151"};
		}else if ("00000100AA10000000000BMQ".equals(corp.getCorptype())) {//民间非营利组织会计制度
			accCodes=new String[] { "1121", "2202","1122", "2209","2203","1141"};
		}
		boolean isMatch = false;
		for (String accCode : accCodes) {
			if (account.getAccountcode().startsWith(accCode)) {
				isMatch = true;
				break;
			}
		}
		return isMatch;
	}
	
	// 匹配科目 匹配不上新建
	private YntCpaccountVO matchAccount(String pk_corp, YntCpaccountVO account, String name,
			YntCpaccountVO[] accounts, String newrule) {

		String pcode = account.getAccountcode();
		YntCpaccountVO nextvo = getXJAccountVOByName(name, pcode, pk_corp, accounts);

		if (nextvo == null) {
			if (StringUtil.isEmpty(name)) {
				return account;
			}
			nextvo = getnext(account, pcode, accounts, name, pk_corp, newrule);
			//保存前查下数据库
			YntCpaccountVO saveEd=querySubjByName(nextvo);
			if(saveEd!=null)return saveEd;
			cpaccountService.saveNew(nextvo);
		}
		return nextvo;

	}
	private YntCpaccountVO querySubjByName(YntCpaccountVO nextvo){
		String accountCode=nextvo.getAccountcode();
		String accountname=nextvo.getAccountname();
		String condtion = new String(
				"(dr=0 or dr is null) and accountcode like ? and accountname = ? and pk_corp=? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(accountCode.substring(0, accountCode.length() - 2) + "%");
		sp.addParam(accountname);
		sp.addParam(nextvo.getPk_corp());
		List<YntCpaccountVO> coList = (List<YntCpaccountVO>)singleObjectBO.retrieveByClause(YntCpaccountVO.class,
				condtion, sp);
		if(coList != null && coList.size() > 0){
			return coList.get(0);
		}
		return null;
	}
	private YntCpaccountVO getXJAccountVOByName(String name, String pcode, String pk_corp, YntCpaccountVO[] accounts) {
		if (accounts == null || accounts.length == 0)
			return null;
		String accname = null;
		name = filterName(name);
		for (YntCpaccountVO accvo : accounts) {
			boolean ifleaf = accvo.getIsleaf() == null ? false : accvo.getIsleaf().booleanValue();
			if (ifleaf && accvo.getAccountcode().startsWith(pcode)) {
				accname = filterName(accvo.getAccountname());
				if (accname.equals(name)) {
					return accvo;
				}
			}
		}
		return null;
	}
	private String filterName(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		name = getHanzi(name);
		return name;
	}
	// 如果达到了最大
	private boolean isMaxCode(String code, String newrule) {
		if (StringUtil.isEmpty(newrule))
			return false;

		int len = code.length();
		String nowru[] = newrule.split("/");
		int startIndex = 0;
		int endIndex = 0;
		for (int i = 0; i < nowru.length; i++) {
			int inx = Integer.valueOf(nowru[i]).intValue();
			String maxcode = getMaxLevelCode(inx);
			endIndex = endIndex + inx;
			if (len >= endIndex) {
				String scode = code.substring(startIndex, endIndex);
				if (scode.compareTo(maxcode) >= 0) {
					if (len == endIndex) {
						return true;
					}
				}
			}
			startIndex = endIndex;
		}
		return false;
	}
	private String getMaxLevelCode(int levelRule) {
		StringBuffer maxvalue = new StringBuffer();
		for (int i = 0; i < levelRule; i++) {
			maxvalue.append("9");
		}
		return maxvalue.toString();
	}
	private String getFirstCode(String parentCode, String rule) {
		int levelLength = getCurrentLevelLength(parentCode, rule);
		String childCode = null;
		childCode = parentCode;
		for (int i = 0; i < levelLength - 1; i++) {
			childCode += "0";
		}
		childCode += "1";
		return childCode;
	}
	private int getCurrentLevelLength(String parentCode, String codeRule) {
		String[] codeRuleArray = codeRule.split("/");
		int totallen = 0;
		for (int i = 0; i < codeRuleArray.length - 1; i++) {
			totallen += Integer.valueOf(codeRuleArray[i]).intValue();
			if (parentCode.length() == totallen) {
				return Integer.valueOf(codeRuleArray[i + 1]).intValue();
			}
		}
		return 0;
	}
	private String getNextCode(String maxaccountcode, String pcode, String newrule) {
		if (StringUtil.isEmpty(pcode) || StringUtil.isEmpty(newrule))
			return maxaccountcode;

		String parentcode = DZfcommonTools.getParentCode(maxaccountcode, newrule);
		if (pcode.equals(parentcode)) {
			return maxaccountcode;
		} else {
			int plen = pcode.length();
			String nowru[] = newrule.split("/");
			int inxlen = 0;
			for (int i = 0; i < nowru.length; i++) {
				int inx = Integer.valueOf(nowru[i]).intValue();
				inxlen = inxlen + inx;
				if (inxlen > plen) {
					break;
				}
			}
			if (StringUtil.isEmpty(maxaccountcode) || maxaccountcode.length() < inxlen) {
				return pcode;
			} else {
				String accoutcode = maxaccountcode.substring(0, inxlen);
				return accoutcode;
			}
		}
	}
	
	private YntCpaccountVO getnext(YntCpaccountVO account, String pcode, YntCpaccountVO[] accounts, String name,
			String pk_corp, String newrule) {
		YntCpaccountVO nextvo = (YntCpaccountVO) account.clone();
		String maxcode = getMaxAccouontCode(pk_corp, pcode);

		if (isMaxCode(maxcode, newrule)) {
			nextvo.setAccountcode(getFirstCode(maxcode, newrule));
			nextvo.set__parentId(maxcode);
		} else {
			String accountcode = getNextCode(maxcode, pcode, newrule);
			if (isMaxCode(accountcode, newrule)) {
				DZFDouble code = new DZFDouble(maxcode);
				code = SafeCompute.add(code, DZFDouble.ONE_DBL);
				accountcode = code.setScale(0, 0).toString();
				nextvo.setAccountcode(accountcode);
				nextvo.set__parentId(DZfcommonTools.getParentCode(accountcode, newrule));
			} else {
				DZFDouble code = new DZFDouble(accountcode);
				code = SafeCompute.add(code, DZFDouble.ONE_DBL);
				accountcode = code.setScale(0, 0).toString();
				nextvo.setAccountcode(accountcode);
				nextvo.set__parentId(pcode);
			}
		}
		nextvo.setFullname(null);
		nextvo.setAccountname(name);
		nextvo.setDoperatedate(new DZFDate().toString().substring(0, 10));
		nextvo.setIssyscode(DZFBoolean.FALSE);

		nextvo.setPk_corp_account(null);
		return nextvo;
	}
	private String getMaxAccouontCode(String pk_corp, String code) throws DZFWarpException {

		if (pk_corp == null) {
			throw new BusinessException("获取科目编码失败!");
		}

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(code + "%");
		String sql = "select max(accountcode)  from ynt_cpaccount where pk_corp=?  and accountcode like  ? and nvl(dr,0) = 0 ";
		String maxDocNo = (String) singleObjectBO.executeQuery(sql, sp, new ObjectProcessor());

		return maxDocNo;
	}
	private String getHanzi(String string) {
		if (StringUtil.isEmpty(string))
			return null;
		String reg1 = "[\\u4e00-\\u9fa5]";
		Pattern p = Pattern.compile(reg1);
		Matcher m = p.matcher(string);

		String lasrChar = "";
		while (m.find()) {
			lasrChar = lasrChar + m.group();
		}
		return lasrChar;
	}
	
	private YntCpaccountVO queryDelAccountVO(String pk_subject)throws DZFWarpException{
		String sql="select * from ynt_cpaccount a where a.pk_corp_account=? and dr=1";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_subject);
		List<YntCpaccountVO> list=(List<YntCpaccountVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(YntCpaccountVO.class));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	private TzpzBVO[] buildTzpzBVOsByAccset(AccsetVO accsetVO,AccsetKeywordBVO2 accsetKeywordBVO2,OcrInvoiceVO invoiceVO,CorpVO corp,Map<String,YntCpaccountVO> accountMap,Map<String, CategorysetVO> categorysetMap,Map<String, String> bankAccountMap,Map<String, Object[]> categoryMap,DZFBoolean isHasSH,List<Object> paramList,OcrInvoiceDetailVO detailVO,Map<String, String> jituanSubMap,String newrule,YntCpaccountVO[] accVOs,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,List<AuxiliaryAccountBVO> chFzhsBodyVOs,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		int bodyCount=invoiceVO.getChildren().length;
		TzpzBVO[] tzpzBVOs=new TzpzBVO[DZFBoolean.TRUE.equals(isHasSH)?3:2];
		for (int j = 0; j < tzpzBVOs.length; j++) {
			TzpzBVO tzpzBVO=new TzpzBVO();
			tzpzBVO.setDr(0);
			tzpzBVO.setZy(getZyByRule(invoiceVO, corp, paramList, categorysetMap, accsetVO, accsetKeywordBVO2, categoryMap,detailVO));//摘要
			if(accsetKeywordBVO2!=null){
				tzpzBVO.setVdirect(accsetKeywordBVO2.getVdirect());//科目方向：借方是0贷方是1
			}else{
				tzpzBVO.setVdirect(accsetVO.getVdirect());//科目方向：借方是0贷方是1
			}
			if(tzpzBVO.getVdirect()==0){
				if(j==0){//入账行
					tzpzBVO.setPk_accsubj(getPk_accsubjByRule(0, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO));//科目
				}else if(j==1){
					if(DZFBoolean.TRUE.equals(isHasSH)){
						String pk_km=getPk_accsubjByRule(2, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO);
						if(StringUtil.isEmpty(pk_km)){
							pk_km=tzpzBVOs[0].getPk_accsubj();
						}
						tzpzBVO.setPk_accsubj(pk_km);//科目
					}else{
						tzpzBVO.setPk_accsubj(getPk_accsubjByRule(1, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO));//科目
					}
				}else{
					tzpzBVO.setPk_accsubj(getPk_accsubjByRule(1, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO));//科目
				}
			}else{
				if(j==0){//结算
					tzpzBVO.setPk_accsubj(getPk_accsubjByRule(1, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO));//科目
				}else if(j==1){
					tzpzBVO.setPk_accsubj(getPk_accsubjByRule(0, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO));//科目
				}else{
					String pk_km=getPk_accsubjByRule(2, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(detailVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs,invoiceVO,detailVO,chFzhsBodyVOs,categoryMap,inventorySetVO,fzhsBMMap,tzpzBVO);
					if(StringUtil.isEmpty(pk_km)){
						pk_km=tzpzBVOs[1].getPk_accsubj();
					}
					tzpzBVO.setPk_accsubj(pk_km);//科目
				}
			}
			if(StringUtil.isEmpty(tzpzBVO.getPk_accsubj())){
				throw new BusinessException("没有匹配的科目，请确认是否需要升级标准科目");
			}
			YntCpaccountVO accountVO=accountMap.get(tzpzBVO.getPk_accsubj());//科目
			if(accountVO==null){
				YntCpaccountVO delAccountVO=queryDelAccountVO(tzpzBVO.getPk_accsubj());
				if(delAccountVO!=null){
					throw new BusinessException("会计科目"+delAccountVO.getAccountcode()+" "+delAccountVO.getAccountname()+"已删除，请检查！");
				}else{
					throw new BusinessException("会计科目不存在，请确认设置的会计科目是否已删除！");//按理不会走到这里
				}
			}
			String unitName=corp.getUnitname();
			String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
			//如果科目是1002，如果1002有下级用下级替换1002，如果没有银行辅助核算(自定义7)
			if(accountVO.getAccountcode().equals("1002")&&!StringUtil.isEmpty(invoiceVO.getIstate())&&invoiceVO.getIstate().equals(ZncsConst.SBZT_1)){
				if(DZFBoolean.TRUE.equals(accountVO.getIsleaf())){
					//走银行辅助核算
					if(!StringUtil.isEmpty(accountVO.getIsfzhs())&&accountVO.getIsfzhs().charAt(6)=='1'){
					}
				}else{
					//替换科目
					String bankCode=null;
					if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)&&!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
						if(j==0){
							bankCode=invoiceVO.getVsaletaxno();
						}else{
							bankCode=invoiceVO.getVpurchtaxno();
						}
					}else{
						if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
							bankCode=invoiceVO.getVsaletaxno();
						}else if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
							bankCode=invoiceVO.getVpurchtaxno();
						}
					}
					if(bankAccountMap.get(bankCode)!=null){
						//找到了
						tzpzBVO.setPk_accsubj(bankAccountMap.get(bankCode));
						accountVO=accountMap.get(bankAccountMap.get(bankCode));//科目100201
					}else{
						checkMsgMap.get("3").put("ismust","Y");
					}
				}
			}
			if(accountVO==null){
				YntCpaccountVO delAccountVO=queryDelAccountVO(tzpzBVO.getPk_accsubj());
				if(delAccountVO!=null){
					throw new BusinessException("会计科目"+delAccountVO.getAccountcode()+" "+delAccountVO.getAccountname()+"不存在，请检查银行账户关联的科目！");
				}else{
					throw new BusinessException("匹配会计科目失败！");//按理不会走到这里
				}
			}
			//往来科目匹配下级科目
			if(DZFBoolean.FALSE.equals(accountVO.getIsleaf())&&isMatchAccountCode(accountVO,corp)){
				String khGysName=null;
				if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)&&!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
				}else{
					if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
						khGysName=invoiceVO.getVpurchname();
					}else if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
						khGysName=invoiceVO.getVsalename();
					}
				}
				if(!StringUtil.isEmpty(khGysName)){
					// 供应商 客户类科目（应付账款 应收账款）
					YntCpaccountVO account1 = matchAccount(corp.getPk_corp(), accountVO, khGysName, accVOs, newrule);
					if (account1 != null) {
						accountVO = account1;
						tzpzBVO.setPk_accsubj(accountVO.getPk_corp_account());
					}
				}
			}
			tzpzBVO.setPk_currency(getCurrency(invoiceVO, detailVO, currMap,tzpzBVO,accountVO,rateMap));//币种
			//设置借方、贷方金额
			if(!tzpzBVO.getPk_currency().equals(DzfUtil.PK_CNY)){
				Object[] rateObj=rateMap.get(tzpzBVO.getPk_currency());//0汇率1模式(1是除else乘)
				if(rateObj==null){//出错了
					tzpzBVO.setVdef6("error2");
					tzpzBVO.setNrate(new DZFDouble(1));
					tzpzBVO.setVdef9(String.valueOf(1));
					if(tzpzBVO.getVdirect()==0){
						//方向是借，先入账和税行，后结算，
						if(j==0){//入账行
							DZFDouble jine=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemmny()));
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							if(DZFBoolean.TRUE.equals(isHasSH)){//还要拼一个税行
								tzpzBVO.setYbjfmny(jine);
							}else{
								if(jine.add(shuie).compareTo(DZFDouble.ZERO_DBL)==0&&bodyCount==1){
									tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
								}else{
									tzpzBVO.setYbjfmny(jine.add(shuie));
								}
							}
							tzpzBVO.setJfmny(tzpzBVO.getYbjfmny());
							tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
						}else if(j==1){
							if(DZFBoolean.TRUE.equals(isHasSH)){
								DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
								tzpzBVO.setYbjfmny(shuie);
								tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
								tzpzBVO.setJfmny(tzpzBVO.getYbjfmny());
								tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
							}else{
								DZFDouble jshj=DZFDouble.ZERO_DBL;
								if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
									jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
								}else{
									jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
								}
								tzpzBVO.setYbdfmny(jshj);
								tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
								tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
								tzpzBVO.setDfmny(tzpzBVO.getYbdfmny());
							}
						}else{
							DZFDouble jshj=DZFDouble.ZERO_DBL;
							if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
							}else{
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
							}
							tzpzBVO.setYbdfmny(jshj);
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(tzpzBVO.getYbdfmny());
						}
					}else{
						//方向是贷方，先结算，后入账和税行
						if(j==0){//结算
							DZFDouble jshj=DZFDouble.ZERO_DBL;
							if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
							}else{
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
							}
							tzpzBVO.setYbjfmny(jshj);
							tzpzBVO.setJfmny(tzpzBVO.getYbjfmny());
							tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
						}else if(j==1){
							DZFDouble jine=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemmny()));
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							if(DZFBoolean.TRUE.equals(isHasSH)){//还要拼一个税行
								tzpzBVO.setYbdfmny(jine);
							}else{
								if(jine.add(shuie).compareTo(DZFDouble.ZERO_DBL)==0&&bodyCount==1){
									tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
								}else{
									tzpzBVO.setYbdfmny(jine.add(shuie));
								}
							}
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(tzpzBVO.getYbdfmny());
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
						}else{
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							tzpzBVO.setYbdfmny(shuie);
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(tzpzBVO.getYbdfmny());
						}
					}
				}else{
					tzpzBVO.setNrate(new DZFDouble(rateObj[0].toString()));
					int model=Integer.parseInt(rateObj[1].toString());
					if(tzpzBVO.getVdirect()==0){
						//方向是借，先入账和税行，后结算，
						if(j==0){//入账行
							DZFDouble jine=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemmny()));
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							if(DZFBoolean.TRUE.equals(isHasSH)){//还要拼一个税行
								tzpzBVO.setYbjfmny(jine);
							}else{
								if(jine.add(shuie).compareTo(DZFDouble.ZERO_DBL)==0&&bodyCount==1){
									tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
								}else{
									tzpzBVO.setYbjfmny(jine.add(shuie));
								}
								
							}
							tzpzBVO.setJfmny(model==1?tzpzBVO.getYbjfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbjfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
						}else if(j==1){
							if(DZFBoolean.TRUE.equals(isHasSH)){
								DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
								tzpzBVO.setYbjfmny(shuie);
								tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
//								tzpzBVO.setPk_accsubj(getPk_accsubjByRule(2, accsetVO, accsetKeywordBVO2, corp, categorysetMap.get(invoiceVO.getPk_billcategory()), paramList,jituanSubMap,newrule,accVOs));//科目
							}else{
								DZFDouble jshj=DZFDouble.ZERO_DBL;
								if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
									jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
								}else{
									jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
								}
								tzpzBVO.setYbdfmny(jshj);
								tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
								tzpzBVO.setDfmny(model==1?tzpzBVO.getYbdfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbdfmny().multiply(tzpzBVO.getNrate()));
								tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
							}
						}else{
							DZFDouble jshj=DZFDouble.ZERO_DBL;
							if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
							}else{
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
							}
							tzpzBVO.setYbdfmny(jshj);
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(model==1?tzpzBVO.getYbdfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbdfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
						}
					}else{
						//方向是贷方，先结算，后入账和税行
						if(j==0){//结算
							DZFDouble jshj=DZFDouble.ZERO_DBL;
							if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
							}else{
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
							}
							tzpzBVO.setYbjfmny(jshj);
							tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setJfmny(model==1?tzpzBVO.getYbjfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbjfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
						}else if(j==1){
							DZFDouble jine=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemmny()));
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							if(DZFBoolean.TRUE.equals(isHasSH)){//还要拼一个税行
								tzpzBVO.setYbdfmny(jine);
							}else{
								if(jine.add(shuie).compareTo(DZFDouble.ZERO_DBL)==0&&invoiceVO.getChildren().length==1){
									tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
								}else{
									tzpzBVO.setYbdfmny(jine.add(shuie));
								}
							}
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(model==1?tzpzBVO.getYbdfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbdfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
						}else{
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							tzpzBVO.setYbdfmny(shuie);
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(model==1?tzpzBVO.getYbdfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbdfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
						}
					}
				}
			}else{
				if(tzpzBVO.getVdirect()==0){
					//方向是借，先入账和税行，后结算，
					if(j==0){//入账行
						DZFDouble jine=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemmny()));
						DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
						if(DZFBoolean.TRUE.equals(isHasSH)){//还要拼一个税行
							tzpzBVO.setJfmny(jine);
						}else{
							if(jine.add(shuie).compareTo(DZFDouble.ZERO_DBL)==0&&bodyCount==1){
								tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
							}else{
								tzpzBVO.setJfmny(jine.add(shuie));
							}
							
						}
						tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
					}else if(j==1){
						if(DZFBoolean.TRUE.equals(isHasSH)){
							DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
							tzpzBVO.setJfmny(shuie);
							tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
						}else{
							DZFDouble jshj=DZFDouble.ZERO_DBL;
							if(bodyCount>1){
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
							}else{
								jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
							}
							tzpzBVO.setDfmny(jshj);
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
						}
					}else{
						DZFDouble jshj=DZFDouble.ZERO_DBL;
						if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
							jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
						}else{
							jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
						}
						tzpzBVO.setDfmny(jshj);
						tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
					}
				}else{
					//方向是贷方，先结算，后入账和税行
					if(j==0){//结算
						DZFDouble jshj=DZFDouble.ZERO_DBL;
						if((!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))||!StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())))&&bodyCount>1){
							jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));
						}else{
							jshj=new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax()));
						}
						tzpzBVO.setJfmny(jshj);
						tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
					}else if(j==1){
						DZFDouble jine=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemmny()));
						DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
						if(DZFBoolean.TRUE.equals(isHasSH)){//还要拼一个税行
							tzpzBVO.setDfmny(jine);
						}else{
							if(jine.add(shuie).compareTo(DZFDouble.ZERO_DBL)==0&&bodyCount==1){
								tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
							}else{
								tzpzBVO.setDfmny(jine.add(shuie));
							}
						}
						tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
					}else{
						DZFDouble shuie=new DZFDouble(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))&&bodyCount==1?OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()):OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()));
						tzpzBVO.setDfmny(shuie);
						tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
					}
				}
				if(tzpzBVO.getNrate()!=null&&tzpzBVO.getNrate().compareTo(DZFDouble.ZERO_DBL)>0){
					int model=Integer.parseInt(tzpzBVO.getVdef9());
					tzpzBVO.setJfmny(model==1?tzpzBVO.getJfmny().div(tzpzBVO.getNrate()):tzpzBVO.getJfmny().multiply(tzpzBVO.getNrate()));
					tzpzBVO.setDfmny(model==1?tzpzBVO.getDfmny().div(tzpzBVO.getNrate()):tzpzBVO.getDfmny().multiply(tzpzBVO.getNrate()));
					tzpzBVO.setVdef9(null);
					tzpzBVO.setNrate(null);
				}
			}
			tzpzBVO.setPk_corp(corp.getPk_corp());
			//如果不是下级科目，找第一个下级科目
			if(!DZFBoolean.TRUE.equals(accountVO.getIsleaf())){
				for (int k = 0; k < accVOs.length; k++) {
					YntCpaccountVO yntCpaccountVO = accVOs[k];
					if((yntCpaccountVO.getBisseal()==null||DZFBoolean.FALSE.equals(yntCpaccountVO.getBisseal()))&&yntCpaccountVO.getAccountcode().startsWith(accountVO.getAccountcode())&&DZFBoolean.TRUE.equals(yntCpaccountVO.getIsleaf())){
						accountVO=yntCpaccountVO;
						tzpzBVO.setPk_accsubj(accountVO.getPk_corp_account());
						break;
					}
				}
			}
			tzpzBVO.setVcode(accountVO.getAccountcode());//科目编码
			tzpzBVO.setVname(accountVO.getAccountname());//科目名称
			tzpzBVO.setKmmchie(accountVO.getFullname());//科目全称
			//判断科目是否启用了辅助核算
			if(!StringUtil.isEmpty(accountVO.getIsfzhs())&&accountVO.getIsfzhs().indexOf("1")>-1){
				setFzhsValue(accountVO, categorysetMap, corp.getPk_corp(), invoiceVO, tzpzBVO,detailVO,categoryMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
			}
			if(DZFBoolean.TRUE.equals(accountVO.getIsnum())){
				setFzhsBMVO(tzpzBVO, invoiceVO, categoryMap, detailVO, fzhsBMMap, corp, inventorySetVO);
				DZFDouble tMny=tzpzBVO.getDfmny().compareTo(DZFDouble.ZERO_DBL) !=0 ? tzpzBVO.getDfmny() : tzpzBVO.getJfmny();
				String calcmode=tzpzBVO.getVdef3();
				String hsl=tzpzBVO.getVdef4();
				if(StringUtil.isEmpty(detailVO.getItemamount())&&StringUtil.isEmpty(detailVO.getItemprice())){
					if(StringUtil.isEmpty(calcmode)){
						tzpzBVO.setNnumber(new DZFDouble(1));//数量
					}else{
						if(calcmode.equals("1")){
							tzpzBVO.setNnumber(new DZFDouble(1).div(new DZFDouble(hsl)));//数量
						}else{
							tzpzBVO.setNnumber(new DZFDouble(1).multiply(new DZFDouble(hsl)));//数量
						}
					}
					tzpzBVO.setNprice(tMny.div(tzpzBVO.getNnumber()));//单价
				}else{
					if(!StringUtil.isEmpty(detailVO.getItemamount())&&!StringUtil.isEmpty(detailVO.getItemprice())){
						if(StringUtil.isEmpty(calcmode)){
							tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemamount())));//数量
						}else{
							if(calcmode.equals("1")){
								tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemamount())).div(new DZFDouble(hsl)));//数量
							}else{
								tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemamount())).multiply(new DZFDouble(hsl)));//数量
							}
						}
						if(DZFBoolean.TRUE.equals(isHasSH)){
							tzpzBVO.setNprice(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemprice())));//单价
						}else{
							if(tzpzBVO.getNnumber().compareTo(DZFDouble.ZERO_DBL)==0){
								if(StringUtil.isEmpty(calcmode)){
									tzpzBVO.setNnumber(new DZFDouble(1));//数量
								}else{
									if(calcmode.equals("1")){
										tzpzBVO.setNnumber(new DZFDouble(1).div(new DZFDouble(hsl)));//数量
									}else{
										tzpzBVO.setNnumber(new DZFDouble(1).multiply(new DZFDouble(hsl)));//数量
									}
								}
							}
							tzpzBVO.setNprice(tMny.div(tzpzBVO.getNnumber()));//单价
						}
					}else{
						if(StringUtil.isEmpty(detailVO.getItemamount())){
							if(StringUtil.isEmpty(calcmode)){
								tzpzBVO.setNnumber(new DZFDouble(1));//数量
							}else{
								if(calcmode.equals("1")){
									tzpzBVO.setNnumber(new DZFDouble(1).div(new DZFDouble(hsl)));//数量
								}else{
									tzpzBVO.setNnumber(new DZFDouble(1).multiply(new DZFDouble(hsl)));//数量
								}
							}
						}else{
							if(StringUtil.isEmpty(calcmode)){
								tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemamount())));//数量
							}else{
								if(calcmode.equals("1")){
									tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemamount())).div(new DZFDouble(hsl)));//数量
								}else{
									tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemamount())).multiply(new DZFDouble(hsl)));//数量
								}
							}
						}
						tzpzBVO.setNprice(tMny.div(tzpzBVO.getNnumber()));//单价
					}
				}
				tzpzBVO.setVdef3(null);
				tzpzBVO.setVdef4(null);
			}
			setTaxItem(invoiceVO, detailVO, tzpzBVO, corp);
			tzpzBVOs[j]=tzpzBVO;
		}
		if(tzpzBVOs.length==3){
			int vdirect=-1;
			if(accsetKeywordBVO2!=null){
				vdirect=accsetKeywordBVO2.getVdirect();//科目方向：借方是0贷方是1
			}else{
				vdirect=accsetVO.getVdirect();//科目方向：借方是0贷方是1
			}
			if(vdirect==0){
				if(tzpzBVOs[0].getPk_accsubj().equals(tzpzBVOs[1].getPk_accsubj())){
					tzpzBVOs[0].setJfmny(tzpzBVOs[0].getJfmny().add(tzpzBVOs[1].getJfmny()));
					tzpzBVOs[0].setDfmny(tzpzBVOs[0].getDfmny().add(tzpzBVOs[1].getDfmny()));
					List<TzpzBVO> returnList=new ArrayList<TzpzBVO>();
					returnList.add(tzpzBVOs[0]);
					returnList.add(tzpzBVOs[2]);
					tzpzBVOs=returnList.toArray(new TzpzBVO[0]);
				}
			}else{
				if(tzpzBVOs[1].getPk_accsubj().equals(tzpzBVOs[2].getPk_accsubj())){
					tzpzBVOs[1].setJfmny(tzpzBVOs[1].getJfmny().add(tzpzBVOs[2].getJfmny()));
					tzpzBVOs[1].setDfmny(tzpzBVOs[1].getDfmny().add(tzpzBVOs[2].getDfmny()));
					List<TzpzBVO> returnList=new ArrayList<TzpzBVO>();
					returnList.add(tzpzBVOs[0]);
					returnList.add(tzpzBVOs[1]);
					tzpzBVOs=returnList.toArray(new TzpzBVO[0]);
				}
			}
		}
		
		return tzpzBVOs;
	}
	
	private void setFzhsBMVO(TzpzBVO tzpzBVO,OcrInvoiceVO invoiceVO,Map<String, Object[]> categoryMap,OcrInvoiceDetailVO detailVO,Map<String, InventoryAliasVO> fzhsBMMap,CorpVO corp,InventorySetVO inventorySetVO){
		if(!invoiceVO.getIstate().equals(ZncsConst.SBZT_3))return;
		String catecode=categoryMap.get(detailVO.getPk_billcategory())[0].toString();
		if(catecode.startsWith("101015")||catecode.startsWith("101110")||catecode.startsWith("11")){
			String invName=OcrUtil.execInvname(detailVO.getInvname());
			if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO!=null&&inventorySetVO.getChcbjzfs()!=InventoryConstant.IC_NO_MXHS){
				String strName2="";
				if(inventorySetVO.getChppjscgz()==0){
					strName2=invName+(StringUtil.isEmpty(detailVO.getInvtype())?"":detailVO.getInvtype())+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
				}else{
					strName2=invName+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
				}
				//先找别名表
				if(fzhsBMMap.get(strName2)!=null){
					tzpzBVO.setVdef3(String.valueOf(fzhsBMMap.get(strName2).getCalcmode()));
					tzpzBVO.setVdef4(fzhsBMMap.get(strName2).getHsl().toString());
				}
			}else if(IcCostStyle.IC_ON.equals(corp.getBbuildic())){
				String strName2=invName+(StringUtil.isEmpty(detailVO.getInvtype())?"":detailVO.getInvtype())+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
				//先找别名表
				if(fzhsBMMap.get(strName2)!=null){
					tzpzBVO.setVdef3(String.valueOf(fzhsBMMap.get(strName2).getCalcmode()));
					tzpzBVO.setVdef4(fzhsBMMap.get(strName2).getHsl().toString());
				}
			}
		}
	}
	/**
	 * 按分类取关键字入账规则
	 * @return
	 * @throws DZFWarpException
	 */
	private AccsetKeywordBVO2 getAccsetKeywordBVO2ByCategory(String pk_basecategory,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, Object[]> categoryMap,CorpVO corp,OcrInvoiceVO invoiceVO,OcrInvoiceDetailVO detailVO,String tradeCode)throws DZFWarpException{
		String pk_accountschema=corp.getCorptype();
		AccsetKeywordBVO2 accsetKeywordBVO2=null;
		String key=null;
		List<AccsetKeywordBVO2> accsetKeywordBVO2List=null;
		
		if(!StringUtil.isEmpty(tradeCode)){
			//3级
			key=pk_basecategory+pk_accountschema+tradeCode;//有行业的优先
			accsetKeywordBVO2List=accsetKeywordBVO2Map.get(key);
			if(accsetKeywordBVO2List!=null&&accsetKeywordBVO2List.size()>0){
				accsetKeywordBVO2=getWordMate(invoiceVO, detailVO, accsetKeywordBVO2List);
			}
			//2级
			if(accsetKeywordBVO2==null&&!tradeCode.startsWith("Z")&&tradeCode.length()>3){
				key=pk_basecategory+pk_accountschema+tradeCode.substring(0,3);
				accsetKeywordBVO2List=accsetKeywordBVO2Map.get(key);
				if(accsetKeywordBVO2List!=null&&accsetKeywordBVO2List.size()>0){
					accsetKeywordBVO2=getWordMate(invoiceVO, detailVO, accsetKeywordBVO2List);
				}
				//1级
				if(accsetKeywordBVO2==null&&tradeCode.length()>2){
					key=pk_basecategory+pk_accountschema+tradeCode.substring(0,2);
					accsetKeywordBVO2List=accsetKeywordBVO2Map.get(key);
					if(accsetKeywordBVO2List!=null&&accsetKeywordBVO2List.size()>0){
						accsetKeywordBVO2=getWordMate(invoiceVO, detailVO, accsetKeywordBVO2List);
					}
				}
			}
		}
		if(accsetKeywordBVO2==null){
			key=pk_basecategory+pk_accountschema;
			accsetKeywordBVO2List=accsetKeywordBVO2Map.get(key);
			if(accsetKeywordBVO2List!=null&&accsetKeywordBVO2List.size()>0){
				accsetKeywordBVO2=getWordMate(invoiceVO, detailVO, accsetKeywordBVO2List);
			}
		}
		return accsetKeywordBVO2;
	}
	
	/**
	 * 发票匹配关键字入账规则
	 * @return
	 */
	private AccsetKeywordBVO2 getWordMate(OcrInvoiceVO invoiceVO,OcrInvoiceDetailVO detailVO,List<AccsetKeywordBVO2> accsetKeywordBVO2List)throws DZFWarpException{
		OcrInvoiceDetailVO[] detailVOs= (OcrInvoiceDetailVO[])invoiceVO.getChildren();
		StringBuffer sb=new StringBuffer();
		if(invoiceVO.getIstate().equals(ZncsConst.SBZT_3)){//增值税发票匹配表体
			sb.append(detailVO.getInvname());
		}else{
			if (detailVOs.length > 1 && !StringUtil.isEmpty(invoiceVO.getVmemo()))
			{
				invoiceVO.setVfirsrinvname(null);
				for (OcrInvoiceDetailVO detailvo : detailVOs)
				{
					if (invoiceVO.getVmemo().contains(detailvo.getInvname()))
					{
						invoiceVO.setVmemo(invoiceVO.getVmemo().replace(detailvo.getInvname(), ""));
					}
				}
			}
			sb.append(invoiceVO.getInvoicetype() + "," + invoiceVO.getVsalename() + ","+invoiceVO.getVpurchname() + "," + invoiceVO.getVinvoicecode() + ","+ invoiceVO.getVinvoiceno());//+","+invoiceVO.getBilltitle());
			sb.append("," + invoiceVO.getDinvoicedate() + "," + invoiceVO.getVsaleopenacc() + ","+ invoiceVO.getVsalephoneaddr());
			sb.append("," + invoiceVO.getVpuropenacc() + "," + invoiceVO.getVpurphoneaddr() + ","+ invoiceVO.getVpurbankname() + "," + invoiceVO.getVsalebankname());
			sb.append("," + invoiceVO.getIstate() + ","+ invoiceVO.getVmemo() + "," + invoiceVO.getKeywords());
			sb.append("," + detailVO.getInvname() + "," + detailVO.getInvtype() + ","+ detailVO.getItemunit());
		}
		for(int i=0;i<accsetKeywordBVO2List.size();i++){//关键字规则
			//格式:12,34  |  56,78  |  999
			List<AccsetKeywordBVO1> listVO1=accsetKeywordBVO2List.get(i).getList1();
			if(listVO1!=null){
				for(int j=0;j<listVO1.size();j++){//一个规则下的关键字list
					String[] keywords=listVO1.get(j).getKeywordnames().split(",");
					DZFBoolean keyWordFlag=DZFBoolean.TRUE;
					for (int k = 0; k < keywords.length; k++) {//一个list里的关键字","分割
						Pattern p = Pattern.compile(keywords[k]);
						Matcher m = p.matcher(sb);
						if (!m.find()) {
							keyWordFlag = DZFBoolean.FALSE;
							break;
						}
					}
					if (keyWordFlag == DZFBoolean.TRUE) {
						return accsetKeywordBVO2List.get(i);
					}
				}
			}
		}
		return null;
	}
	/**
	 * 按分类取分类入账规则
	 * @return
	 * @throws DZFWarpException
	 */
	private AccsetVO getAccsetVOByCategory(String pk_category,Map<String, List<AccsetVO>> accsetMap,Map<String, Object[]> categoryMap,CorpVO corp,OcrInvoiceVO invoiceVO, Set<String> zyFzhsList)throws DZFWarpException{
		Object[] obj=categoryMap.get(pk_category);//分类详情
		String pk_basecategory=obj[1]==null?null:obj[1].toString();
		if(StringUtil.isEmpty(pk_basecategory)){//自己创建的目录，往上找
			if(!StringUtil.isEmpty((String)obj[2])){
				return getAccsetVOByCategory(obj[2].toString(), accsetMap, categoryMap, corp,invoiceVO,zyFzhsList);
			}else{
				throw new BusinessException("没有找到分类入账规则");
			}
		}
		String pk_accountschema=corp.getCorptype();
		String key=pk_basecategory+pk_accountschema+corp.getIndustry();//有行业的优先
		List<AccsetVO> accsetVOList=accsetMap.get(key);
		if(accsetVOList==null){
			key=pk_basecategory+pk_accountschema;
			accsetVOList=accsetMap.get(key);
			if(accsetVOList==null){
				if(!StringUtil.isEmpty((String)obj[2])){
					return getAccsetVOByCategory(obj[2].toString(), accsetMap, categoryMap, corp,invoiceVO,zyFzhsList);
				}else{
					throw new BusinessException("没有找到分类入账规则");
				}
			}
		}
		//如果accsetVOList的size有多个县看是否个人，返回个人
		AccsetVO accsetVO=null;//最后返回的
		AccsetVO nullAccsetVO=null;//都忽略的VO
		String staffname=invoiceVO.getStaffname();//个人标注
		for(int i=0;i<accsetVOList.size();i++){
			AccsetVO tmpAccsetVO=accsetVOList.get(i);
			if(!StringUtil.isEmpty(invoiceVO.getIstate()) && !invoiceVO.getIstate().equals(ZncsConst.SBZT_2) ){ //银行票和增值税发票
				if(tmpAccsetVO.getInflag() == 1){
					String salename=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
					if(!StringUtil.isEmpty(staffname) || !StringUtil.isEmptyWithTrim(salename)  && zyFzhsList.contains(salename)){
						accsetVO=tmpAccsetVO;
					}
				}else if(tmpAccsetVO.getOutflag()==1){
					String purname=invoiceVO.getVpurchname();
					if(!StringUtil.isEmpty(staffname) || !StringUtil.isEmptyWithTrim(purname) && zyFzhsList.contains(purname)){
						accsetVO=tmpAccsetVO;
					}
				}else{//不会配置成都是个人的，所以这里是都忽略
					nullAccsetVO=tmpAccsetVO;
				}
			}else {
				if(tmpAccsetVO.getOutflag()==1){
					if(!StringUtil.isEmpty(staffname)){
						accsetVO=tmpAccsetVO;
					}
				}else{
					nullAccsetVO=tmpAccsetVO;
				}
			}
		}
		return accsetVO==null?nullAccsetVO:accsetVO;
	}
	/**
	 * 通过模板生成凭证体
	 * @return
	 * @throws DZFWarpException
	 */
	private TzpzBVO[] createTzpzBVOByTemplet(OcrInvoiceVO invoiceVO, ImageGroupVO groupVO,List<VouchertempletHVO> templetList,Map<String, CategorysetVO> categorysetMap,Map<String, Object[]> categoryMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		//1、匹配一个自定义模板
		VouchertempletHVO templetHVO=mateTemplet(invoiceVO, templetList);
		//2、取模板表体
		VouchertempletBVO[] templetBVOs=(VouchertempletBVO[])templetHVO.getChildren();
		//3、取票据表体
		OcrInvoiceDetailVO[] detailVOs= (OcrInvoiceDetailVO[])invoiceVO.getChildren();
		//4、凭证分录
		TzpzBVO[] tzpzBVOs=null;
		if(StringUtil.isEmpty(detailVOs[0].getInvname())){
			//5.1、生成表体 生成的分录数=模板表体数
			tzpzBVOs=buildTzpzBVOsByTemplet(templetBVOs, invoiceVO, groupVO.getPk_corp(), accountMap,categorysetMap,bankAccountMap,categoryMap,currMap,rateMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
		}else{
			//5.2、生成表体 模板表体数*票据表体数=生成的分录数
			tzpzBVOs=buildTzpzBVOsByTemplet(templetBVOs, detailVOs, groupVO.getPk_corp(), accountMap,categorysetMap,invoiceVO,bankAccountMap,categoryMap,currMap,rateMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
		}
		return tzpzBVOs;
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
				String relatedsubj=vos[i].getRelatedsubj();
				returnMap.put(bankaccount,relatedsubj);
			}
		}
		return returnMap;
	}
	/**
	 * 通过自定义模板表体+票据头拼凭证表体
	 * @param templetBVOs
	 * @param invoiceVO
	 * @param pk_corp
	 * @param accountMap
	 * @return
	 * @throws DZFWarpException
	 */
	private TzpzBVO[] buildTzpzBVOsByTemplet(VouchertempletBVO[] templetBVOs,OcrInvoiceVO invoiceVO,String pk_corp,Map<String,YntCpaccountVO> accountMap,Map<String, CategorysetVO> categorysetMap,Map<String, String> bankAccountMap,Map<String, Object[]> categoryMap,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		TzpzBVO[] tzpzBVOs=new TzpzBVO[templetBVOs.length];
		CorpVO corp= corpService.queryByPk(pk_corp);
		for (int j = 0; j < templetBVOs.length; j++) {
			TzpzBVO tzpzBVO=new TzpzBVO();
			tzpzBVO.setDr(0);
			tzpzBVO.setZy(templetBVOs[j].getZy());//摘要
			tzpzBVO.setPk_accsubj(templetBVOs[j].getPk_accsubj());//科目
			tzpzBVO.setVdirect(templetBVOs[j].getDebitmny()!=null?0:1);//科目方向：借方是0贷方是1
			YntCpaccountVO accountVO=accountMap.get(tzpzBVO.getPk_accsubj());//科目
			if(accountVO==null){
				YntCpaccountVO delAccountVO=queryDelAccountVO(tzpzBVO.getPk_accsubj());
				if(delAccountVO!=null){
					throw new BusinessException("会计科目"+delAccountVO.getAccountcode()+" "+delAccountVO.getAccountname()+"不存在，请检查！");
				}else{
					throw new BusinessException("匹配会计科目失败！");//按理不会走到这里
				}
			}
			//如果科目是1002，如果1002有下级用下级替换1002，如果没有银行辅助核算(自定义7)
			tzpzBVO.setPk_currency(getCurrency(invoiceVO, null, currMap,tzpzBVO,accountVO,rateMap));//币种写死人民币
			if(!tzpzBVO.getPk_currency().equals(DzfUtil.PK_CNY)){
				//外币汇率
				Object[] rateObj=rateMap.get(tzpzBVO.getPk_currency());//0汇率1模式(1是除else乘)
				if(rateObj==null){//出错了
					if(tzpzBVO.getVdirect()==0){
						if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_0){
							tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_1){
							tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_2){
							tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_3){
							tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_4){
							tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else{
							tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}
						tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
					}else{
						if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_0){
							tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_1){
							tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_2){
							tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_3){
							tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_4){
							tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else{
							tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}
						tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
						if(rateObj!=null){
							tzpzBVO.setNrate(new DZFDouble(rateObj[0].toString()));
						}
					}
				}else{
					tzpzBVO.setNrate(new DZFDouble(rateObj[0].toString()));
					int model=Integer.parseInt(rateObj[1].toString());
					if(tzpzBVO.getVdirect()==0){
						if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_0){
							tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_1){
							tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_2){
							tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_3){
							tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_4){
							tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else{
							tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}
						tzpzBVO.setJfmny(model==1?tzpzBVO.getYbjfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbjfmny().multiply(tzpzBVO.getNrate()));
						tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
						tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
					}else{
						if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_0){
							tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_1){
							tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_2){
							tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_3){
							tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_4){
							tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
						}else{
							tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
						}
						tzpzBVO.setDfmny(model==1?tzpzBVO.getYbdfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbdfmny().multiply(tzpzBVO.getNrate()));
						tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
						tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
					}
				}
			}else{
				//设置借方、贷方金额
				if(tzpzBVO.getVdirect()==0){
					if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_0){
						tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
					}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_1){
						tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
					}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_2){
						tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
					}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_3){
						tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
					}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_4){
						tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
					}else{
						tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
					}
					tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
				}else{
					if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_0){
						tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
					}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_1){
						tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
					}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_2){
						tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
					}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_3){
						tzpzBVO.setDfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));
					}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_4){
						tzpzBVO.setDfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));
					}else{
						tzpzBVO.setDfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(invoiceVO.getNtotaltax())));
					}
					tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
				}
				if(tzpzBVO.getNrate()!=null&&tzpzBVO.getNrate().compareTo(DZFDouble.ZERO_DBL)>0){
					int model=Integer.parseInt(tzpzBVO.getVdef9());
					tzpzBVO.setJfmny(model==1?tzpzBVO.getJfmny().div(tzpzBVO.getNrate()):tzpzBVO.getJfmny().multiply(tzpzBVO.getNrate()));
					tzpzBVO.setDfmny(model==1?tzpzBVO.getDfmny().div(tzpzBVO.getNrate()):tzpzBVO.getDfmny().multiply(tzpzBVO.getNrate()));
					tzpzBVO.setVdef9(null);
					tzpzBVO.setNrate(null);
				}
			}
			tzpzBVO.setPk_corp(pk_corp);
			tzpzBVO.setVcode(accountVO.getAccountcode());//科目编码
			tzpzBVO.setVname(accountVO.getAccountname());//科目名称
			tzpzBVO.setKmmchie(accountVO.getFullname());//科目全称
			//判断科目是否启用了辅助核算
			if(!StringUtil.isEmpty(accountVO.getIsfzhs())&&accountVO.getIsfzhs().indexOf("1")>-1){
				setFzhsValue(accountVO, categorysetMap, pk_corp, invoiceVO, tzpzBVO,null,categoryMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
			}
			setTaxItem(invoiceVO, null, tzpzBVO, corp);
			tzpzBVOs[j]=tzpzBVO;
		}
		return tzpzBVOs;
	}
	
	/**
	 * 设置辅助核算项值
	 * @throws DZFWarpException
	 */
	private void setFzhsValue(YntCpaccountVO accountVO,Map<String, CategorysetVO> categorysetMap,String pk_corp,OcrInvoiceVO invoiceVO,TzpzBVO tzpzBVO,OcrInvoiceDetailVO detailVO,Map<String, Object[]> categoryMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		String fzhs=accountVO.getIsfzhs();//科目辅助核算
		CorpVO corp=corpService.queryByPk(pk_corp);
		CategorysetVO setHeadVO=categorysetMap.get(invoiceVO.getPk_billcategory());
		CategorysetBVO[] setBodyVOs=null;
		if(setHeadVO!=null){
			setBodyVOs=(CategorysetBVO[])setHeadVO.getChildren();//编辑目录辅助核算表体
		}
		String fplx=invoiceVO.getIstate();//发票类型(未识别票据,增值税发票,b银行票据,c其它票据四类)
		DZFBoolean isSpecial=DZFBoolean.FALSE;//如果客户和供应商只启用了一个，那都另一个没启用的也要入辅助核算
		if(fzhs.charAt(0)=='1'&&fzhs.charAt(1)=='0'||fzhs.charAt(0)=='0'&&fzhs.charAt(1)=='1'){
			isSpecial=DZFBoolean.TRUE;
		}
		for(int i=0;i<fzhs.length();i++){
//			if(i==6)continue;//银行的上面单独处理了			//ztc 2019-10-15注释掉
			char flag=fzhs.charAt(i);
			if(flag=='1'){//启用
				AuxiliaryAccountHVO fzhsHeadVO=fzhsHeadMap.get(i+1);//辅助核算项
				List<AuxiliaryAccountBVO> fzhsBodyVOs=null;
				if(i+1==1||i+1==2||i+1==3||i+1==6){
					fzhsBodyVOs=fzhsBodyMap.get(fzhsHeadVO.getPk_auacount_h());
				}else{
					fzhsBodyVOs=iEditDirectory.queryAuxiliaryAccountBVOs(pk_corp, fzhsHeadVO.getPk_auacount_h());//辅助核算值
				}
				Set<String> fzhsKeyMap=getfzhsKeyMap(fzhsBodyVOs);
				if(setBodyVOs!=null&&setBodyVOs.length>0){//取两边交集
					for(CategorysetBVO setBodyVO:setBodyVOs){
						if(setBodyVO.getPk_auacount_h().equals(fzhsHeadVO.getPk_auacount_h())){//找到了一样的辅助核算项，赋值
							String pk_auacount_b=setBodyVO.getPk_auacount_b();//得到辅助核算值
							if(!fzhsKeyMap.contains(pk_auacount_b)){
								AuxiliaryAccountBVO delFzhsVO=queryDelAuxiliaryAccountBVO(pk_auacount_b);
								if(delFzhsVO!=null){
									throw new BusinessException("辅助核算"+delFzhsVO.getCode()+" "+delFzhsVO.getName()+"已删除，请检查！");
								}else{
									throw new BusinessException("辅助核算不存在，请确认设置的辅助核算是否已删除！");//按理不会走到这里
								}
							}
							tzpzBVO.setAttributeValue("fzhsx"+(i+1), pk_auacount_b);
							break;
						}
					}
					//如果编辑目录上没有科目需要的辅助核算值，看是否是增值税发票或者银行
					if(StringUtil.isEmpty((String)tzpzBVO.getAttributeValue("fzhsx"+(i+1)))){
						setFzhsValue_Fplx(corp, fplx, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO, pk_corp, invoiceVO,isSpecial,detailVO,categoryMap,checkMsgMap,inventorySetVO,fzhsBMMap);
					}
				}else{//如果是发票和银行 取客户 供应商值到辅助核算项里，没有就增加
					setFzhsValue_Fplx(corp, fplx, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO, pk_corp, invoiceVO,isSpecial,detailVO,categoryMap,checkMsgMap,inventorySetVO,fzhsBMMap);
				}
				//20191012 ztc 未赋值辅助核算，加提示
				if (i != 5)	//非存货辅助
				{
					if (tzpzBVO.getAttributeValue("fzhsx"+(i+1)) == null)
					{
						tzpzBVO.setVdef5("error");
					}
				}
				else	//存货辅助
				{
					if (tzpzBVO.getAttributeValue("fzhsx"+(i+1)) == null && tzpzBVO.getPk_inventory() == null)
					{
						tzpzBVO.setVdef5("error");
					}
				}
			}
			else
			{
				if (i == 4)	//未启用部门辅助，清除职员辅助赋值的部门辅助核算值
				{
					tzpzBVO.setAttributeValue("fzhsx5", null);
				}
			}
		}
	}
	private AuxiliaryAccountBVO queryDelAuxiliaryAccountBVO(String pk_auacount_b)throws DZFWarpException{
		String sql="select * from ynt_fzhs_b where nvl(dr,0)=1 and pk_auacount_b=? ";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_auacount_b);
		List<AuxiliaryAccountBVO> list=(List<AuxiliaryAccountBVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(AuxiliaryAccountBVO.class));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}else{
			return null;
		}
	}
	/**
	 * 辅助核算keymap校验编辑目录的辅助核算值是否被删除
	 * @param fzhsBodyVOs
	 * @return
	 * @throws DZFWarpException
	 */
	private Set<String> getfzhsKeyMap(List<AuxiliaryAccountBVO> fzhsBodyVOs)throws DZFWarpException{
		Set<String> fzhsKeyMap=new HashSet<String>();
		if(fzhsBodyVOs!=null){
			for(int i=0;i<fzhsBodyVOs.size();i++){
				fzhsKeyMap.add(fzhsBodyVOs.get(i).getPk_auacount_b());
			}
		}
		return fzhsKeyMap;
	}
	/**
	 * 拼存货没有匹配的错误
	 * @throws DZFWarpException
	 */
	private void checkInvtentory(Map<String, Map<String, Object>> checkMsgMap,OcrInvoiceVO invoiceVO,String unitName)throws DZFWarpException{
		if(checkMsgMap.get("1").get("body")!=null){
			List<Map<String, Object>> tmpList=((List<Map<String, Object>>)checkMsgMap.get("1").get("body"));
			for (int j = 0; j < tmpList.size(); j++) {
				if(tmpList.get(j).get("id").toString().equals(invoiceVO.getPk_invoice())){
					return;
				}
			}
		}
		checkMsgMap.get("1").put("count", Integer.parseInt(checkMsgMap.get("1").get("count").toString())+1);
		Map<String, Object> chfzhsMap=new HashMap<String, Object>();
		chfzhsMap.put("id", invoiceVO.getPk_invoice());//主键
		chfzhsMap.put("webid", invoiceVO.getWebid());//图片ID
		String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
		if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
			chfzhsMap.put("fkfmc", saleName);//付款方名称
			chfzhsMap.put("skfmc", invoiceVO.getVpurchname());//收款方名称
		}else if(OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
			chfzhsMap.put("fkfmc", saleName);//付款方名称
			chfzhsMap.put("skfmc",invoiceVO.getVpurchname());//收款方名称
		}
		chfzhsMap.put("je", invoiceVO.getNmny());//金额
		chfzhsMap.put("se", invoiceVO.getNtaxnmny());//税额
		chfzhsMap.put("jshj", invoiceVO.getNtotaltax());//价税合计
		chfzhsMap.put("kprq", invoiceVO.getDinvoicedate());//开票日期
		if(checkMsgMap.get("1").get("body")==null){
			List<Map<String, Object>> chfzhsList=new ArrayList<Map<String, Object>>();
			chfzhsList.add(chfzhsMap);
			checkMsgMap.get("1").put("body", chfzhsList);
		}else{
			((List<Map<String, Object>>)checkMsgMap.get("1").get("body")).add(chfzhsMap);
		}
	}
	/**
	 * 按发票类型设置辅助核算值
	 * @throws DZFWarpException
	 */
	private void setFzhsValue_Fplx(CorpVO corp,String fplx,List<AuxiliaryAccountBVO> fzhsBodyVOs,int i,TzpzBVO tzpzBVO,AuxiliaryAccountHVO fzhsHeadVO,String pk_corp,OcrInvoiceVO invoiceVO,DZFBoolean isSpecial,OcrInvoiceDetailVO detailVO,Map<String, Object[]> categoryMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		String unitName=corp.getUnitname();
		String saleName=StringUtil.isEmpty(invoiceVO.getVsalename())?"":invoiceVO.getVsalename();
		if(!StringUtil.isEmpty(fplx)&&fplx.equals(ZncsConst.SBZT_3)){
			if(i==0){//0 是客户
				if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}else if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())&&DZFBoolean.TRUE.equals(isSpecial)){
					setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}
			}else if(i==1){//1是供应商
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
					setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}else if(OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}
			}else if(i==2){//2是职员
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
					if(!StringUtil.isEmpty(saleName)){
						setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
					}else{
						tzpzBVO.setVdef5("error");
					}
				}else if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					if(!StringUtil.isEmpty(buyName)){
						setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
					}else{
						tzpzBVO.setVdef5("error");
					}
				}else{
					tzpzBVO.setVdef5("error");
				}
			} else if (i == 5) {// 5是存货
				if(!invoiceVO.getIstate().equals(ZncsConst.SBZT_3))return;
				
				String catecode=categoryMap.get(detailVO.getPk_billcategory())[0].toString();
				if(detailVO==null)return;
				String invName=OcrUtil.execInvname(detailVO.getInvname());
				if(!catecode.startsWith("101015")&&!catecode.startsWith("101110")&&!catecode.startsWith("11")){
					if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
						//插存货
						InventoryVO invVO=getInventoryVOByName(invName, detailVO.getInvtype(),detailVO.getItemunit(), pk_corp);
						if(invVO!=null){
							tzpzBVO.setPk_inventory(invVO.getPk_inventory());
							tzpzBVO.setInvcode(invVO.getCode());
							tzpzBVO.setInvname(invVO.getName());
						}else{
							tzpzBVO.setVdef5("error");
						}
					}
					if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO!=null&&inventorySetVO.getChcbjzfs()!=InventoryConstant.IC_NO_MXHS){
						setFzhsValue_Create(invName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO, detailVO.getInvtype(), detailVO.getItemunit(),fplx,false);
					}
				}else{
					// 1、启用库存，去存货档案找
					if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {
						InventoryVO invVO=getInventoryVOByName(invName, detailVO.getInvtype(),detailVO.getItemunit(), pk_corp);
						if(invVO!=null){
							tzpzBVO.setPk_inventory(invVO.getPk_inventory());
							tzpzBVO.setInvcode(invVO.getCode());
							tzpzBVO.setInvname(invVO.getName());
						}else{
//							throw new BusinessException("存货未匹配");
						}
					} else {// 2、没启用去辅助核算找
						if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO!=null&&inventorySetVO.getChcbjzfs()!=InventoryConstant.IC_NO_MXHS){
							String strName1="";
							String strName2="";
							if(inventorySetVO.getChppjscgz()==0){
								strName2=invName+(StringUtil.isEmpty(detailVO.getInvtype())?"":detailVO.getInvtype())+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
							}else{
								strName2=invName+(StringUtil.isEmpty(detailVO.getItemunit())?"":detailVO.getItemunit());
							}
							//先找别名表
							if(fzhsBMMap.get(strName2)!=null){
								tzpzBVO.setAttributeValue("fzhsx"+(i+1), fzhsBMMap.get(strName2).getPk_inventory());
							}
							if(tzpzBVO.getAttributeValue("fzhsx"+(i+1))==null&&fzhsBodyVOs!=null&&fzhsBodyVOs.size()>0){
								for(AuxiliaryAccountBVO fzhsBodyVO:fzhsBodyVOs){
									if(inventorySetVO.getChppjscgz()==0){
										strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getSpec())?"":fzhsBodyVO.getSpec())+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
									}else{
										strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
									}
									if(strName1.equals(strName2)){
										tzpzBVO.setAttributeValue("fzhsx"+(i+1), fzhsBodyVO.getPk_auacount_b());
										break;
									}
								}
							}
							if(tzpzBVO.getAttributeValue("fzhsx"+(i+1))==null);{
//								throw new BusinessException("存货未匹配");
							}
						}else{
							//插入辅助核算
							setFzhsValue_Create(detailVO.getInvname(), pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO, detailVO.getInvtype(), detailVO.getItemunit(),fplx,false);
						}
					}
				}
			}
		}else if(!StringUtil.isEmpty(fplx)&&fplx.equals(ZncsConst.SBZT_1)){
			if(i==0){//0 是客户
				if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}else if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname()) && DZFBoolean.TRUE.equals(isSpecial)){
					setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}
			}else if(i==1){//1是供应商
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
					setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}else if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName) && DZFBoolean.TRUE.equals(isSpecial)){
					String buyName=invoiceVO.getVpurchname();
					setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}
			}else if(i==2){//2是职员
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
					if(!StringUtil.isEmpty(saleName)){
						setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
					}else{
						tzpzBVO.setVdef5("error");
					}
				}else if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					if(!StringUtil.isEmpty(buyName)){
						setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
					}else{
						tzpzBVO.setVdef5("error");
					}
				}else{
					tzpzBVO.setVdef5("error");
				}
			}
		}else{
			if(i==0){//0 是客户
				if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}else if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())&&DZFBoolean.TRUE.equals(isSpecial)){
					setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}
			}else if(i==1){//1是供应商
				if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
					setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}else if(OcrUtil.isSameCompany(unitName,saleName)){
					String buyName=invoiceVO.getVpurchname();
					setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
				}
			}
			else if(i==2){//2是职员
				String staffname=invoiceVO.getStaffname();//个人标注
				if(!StringUtil.isEmpty(staffname)){
					setFzhsValue_Create(staffname, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,true);
				}else{
					if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceVO.getVpurchname())){
						if(!StringUtil.isEmpty(saleName)){
							setFzhsValue_Create(saleName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
						}else{
							tzpzBVO.setVdef5("error");
						}
					}else if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
						String buyName=invoiceVO.getVpurchname();
						if(!StringUtil.isEmpty(buyName)){
							setFzhsValue_Create(buyName, pk_corp, fzhsBodyVOs, i, tzpzBVO, fzhsHeadVO,fplx,false);
						}else{
							tzpzBVO.setVdef5("error");
						}
					}else{
						tzpzBVO.setVdef5("error");
					}
				}
			}
		}
	}
	/**
	 * 通过存货名称查存货主键
	 * @return
	 */
	private InventoryVO getInventoryVOByName(String name,String ggxh,String unit, String pk_corp) {
		//再去别名表找
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select b.*,a.calcmode,a.hsl from ynt_icalias a left outer join ynt_inventory b on(a.pk_inventory=b.pk_inventory)  where nvl(a.dr,0)=0 and nvl(b.dr,0)=0");
		sb.append(" and a.pk_corp=? and a.aliasname=? ");
		sp.addParam(pk_corp);
		sp.addParam(name);
		if(!StringUtil.isEmpty(ggxh)){
			sb.append(" and a.spec=? ");
			sp.addParam(ggxh);
		}else{
			sb.append(" and a.spec is null ");
		}
		if(!StringUtil.isEmpty(unit)){
			sb.append(" and a.unit=? ");
			sp.addParam(unit);
		}else{
			sb.append(" and a.unit is null ");
		}
		List<InventoryVO> list=(List<InventoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InventoryVO.class));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		//先去存货表找
		sb=new StringBuffer();
		sp=new SQLParameter();
		sb.append("select * from ynt_inventory a ");
		sb.append(" where nvl(a.dr,0)=0  and a.pk_corp=? and a.name=?  ");
		sp.addParam(pk_corp);
		sp.addParam(name);
		if(!StringUtil.isEmpty(ggxh)){
			sb.append(" and a.invspec=? ");
			sp.addParam(ggxh);
		}else{
			sb.append(" and a.invspec is null ");
		}
		if(!StringUtil.isEmpty(unit)){
			sb.append(" and a.pk_measure=(select pk_measure from ynt_measure where nvl(dr,0)=0 and pk_corp=? and name=?) ");
			sp.addParam(pk_corp);
			sp.addParam(unit);
		}else{
			sb.append(" and a.pk_measure is null ");
		}
		list=(List<InventoryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(InventoryVO.class));
		if(list!=null&&list.size()>0){
			return list.get(0);
		}
		return null;
	}
	/**
	 * 发票的值赋值给凭证赋值核算，没有新增
	 * @throws DZFWarpException
	 */
	private void setFzhsValue_Create(String name,String pk_corp,List<AuxiliaryAccountBVO> fzhsBodyVOs,int i,TzpzBVO tzpzBVO,AuxiliaryAccountHVO fzhsHeadVO,String fplx,boolean isTrueZy)throws DZFWarpException{
		if(!StringUtil.isEmpty(name)){
			if(fzhsBodyVOs!=null&&fzhsBodyVOs.size()>0){
				for(AuxiliaryAccountBVO fzhsBodyVO:fzhsBodyVOs){
					if(i==0||i==1){
						String name1=filterName(fzhsBodyVO.getName());
						String name2=filterName(name);
						if(name1.equals(name2)){
							tzpzBVO.setAttributeValue("fzhsx"+(i+1), fzhsBodyVO.getPk_auacount_b());
							break;
						}
					}else{
						if(fzhsBodyVO.getName().equals(name)){
							tzpzBVO.setAttributeValue("fzhsx"+(i+1), fzhsBodyVO.getPk_auacount_b());
							//如果是职员辅助，查找所属部门，如果科目启用了部门辅助，则自动赋值部门辅助核算值
							if (i == 2)
							{
								tzpzBVO.setAttributeValue("fzhsx5", fzhsBodyVO.getCdeptid());
							}
							break;
						}
					}
				}
			}
			//职员辅助只有其他票create
			if(StringUtil.isEmpty((String)tzpzBVO.getAttributeValue("fzhsx"+(i+1)))){
				if(i==2&&!StringUtil.isEmpty(fplx)&&!fplx.equals(ZncsConst.SBZT_2)){
					tzpzBVO.setVdef5("error");
					return;
				}
				if(i==2&&!StringUtil.isEmpty(fplx)&&fplx.equals(ZncsConst.SBZT_2)&&name.length()>5&&!isTrueZy){
					tzpzBVO.setVdef5("error");
					return;
				}
			}
			if(StringUtil.isEmpty((String)tzpzBVO.getAttributeValue("fzhsx"+(i+1)))){
				//如果现在的辅助核算表里没有这个值，先增加
				AuxiliaryAccountBVO tmpBodyVO=new AuxiliaryAccountBVO();
				tmpBodyVO.setPk_auacount_h(fzhsHeadVO.getPk_auacount_h());//主表主键
				tmpBodyVO.setCode(yntBoPubUtil.getFZHsCode(pk_corp, fzhsHeadVO.getPk_auacount_h()));
				tmpBodyVO.setName(name);
				tmpBodyVO.setPk_corp(pk_corp);
				tmpBodyVO.setDr(0);
				if(i==2&&!StringUtil.isEmpty(fplx)&&fplx.equals(ZncsConst.SBZT_2)){
					tmpBodyVO.setBilltype("05");
				}
				tmpBodyVO=gl_fzhsserv.saveB(tmpBodyVO);
				tzpzBVO.setAttributeValue("fzhsx"+(i+1), tmpBodyVO.getPk_auacount_b());
				fzhsBodyVOs.add(tmpBodyVO);
			}
		}
	}
	
	/**
	 * 发票的值赋值给凭证赋值核算，没有新增
	 * 存货
	 * @throws DZFWarpException
	 */
	private void setFzhsValue_Create(String name,String pk_corp,List<AuxiliaryAccountBVO> fzhsBodyVOs,int i,TzpzBVO tzpzBVO,AuxiliaryAccountHVO fzhsHeadVO,String ggxh,String unit,String fplx,boolean isTrueZy)throws DZFWarpException{
		if(!StringUtil.isEmpty(name)){
			if(fzhsBodyVOs!=null&&fzhsBodyVOs.size()>0){
				for(AuxiliaryAccountBVO fzhsBodyVO:fzhsBodyVOs){
					String strName1=fzhsBodyVO.getName()+(StringUtil.isEmpty(fzhsBodyVO.getSpec())?"":fzhsBodyVO.getSpec())+(StringUtil.isEmpty(fzhsBodyVO.getUnit())?"":fzhsBodyVO.getUnit());
					String strName2=name+(StringUtil.isEmpty(ggxh)?"":ggxh)+(StringUtil.isEmpty(unit)?"":unit);
					if(strName1.equals(strName2)){
						tzpzBVO.setAttributeValue("fzhsx"+(i+1), fzhsBodyVO.getPk_auacount_b());
						break;
					}
				}
			}
			if(StringUtil.isEmpty((String)tzpzBVO.getAttributeValue("fzhsx"+(i+1)))){
				//职员辅助只有其他票create
				if(i==2&&!StringUtil.isEmpty(fplx)&&!fplx.equals(ZncsConst.SBZT_2)){
					tzpzBVO.setVdef5("error");
					return;
				}
				if(i==2&&!StringUtil.isEmpty(fplx)&&fplx.equals(ZncsConst.SBZT_2)&&name.length()>5&&!isTrueZy){
					tzpzBVO.setVdef5("error");
					return;
				}
			}
			if(StringUtil.isEmpty((String)tzpzBVO.getAttributeValue("fzhsx"+(i+1)))){
				//如果现在的辅助核算表里没有这个值，先增加
				AuxiliaryAccountBVO tmpBodyVO=new AuxiliaryAccountBVO();
				tmpBodyVO.setPk_auacount_h(fzhsHeadVO.getPk_auacount_h());//主表主键
				tmpBodyVO.setCode(yntBoPubUtil.getFZHsCode(pk_corp, fzhsHeadVO.getPk_auacount_h()));
				tmpBodyVO.setName(name);
				tmpBodyVO.setPk_corp(pk_corp);
				tmpBodyVO.setSpec(ggxh);
				tmpBodyVO.setUnit(unit);
				tmpBodyVO.setDr(0);
				if(i==2&&!StringUtil.isEmpty(fplx)&&fplx.equals(ZncsConst.SBZT_2)){
					tmpBodyVO.setBilltype("05");
				}
				tmpBodyVO=gl_fzhsserv.saveB(tmpBodyVO);
				tzpzBVO.setAttributeValue("fzhsx"+(i+1), tmpBodyVO.getPk_auacount_b());
				fzhsBodyVOs.add(tmpBodyVO);
			}
		}
	}
	
	/**
	 * 查辅助核算表体
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, List<AuxiliaryAccountBVO>> queryFzhsBodyMap(String pk_corp,Set<String> zyFzhsList)throws DZFWarpException{
		List<AuxiliaryAccountBVO> khFzhsBodyVOs=iEditDirectory.queryAuxiliaryAccountBVOs(pk_corp, "000001000000000000000001");//客户辅助核算值
		List<AuxiliaryAccountBVO> gysFzhsBodyVOs=iEditDirectory.queryAuxiliaryAccountBVOs(pk_corp,"000001000000000000000002");//供应商辅助核算值
		List<AuxiliaryAccountBVO> zyFzhsBodyVOs=iEditDirectory.queryAuxiliaryAccountBVOs(pk_corp, "000001000000000000000003");//职员辅助核算值
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=iEditDirectory.queryAuxiliaryAccountBVOs(pk_corp, "000001000000000000000006");//存货辅助核算值
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=new HashMap<String, List<AuxiliaryAccountBVO>>();
		fzhsBodyMap.put("000001000000000000000001", khFzhsBodyVOs);
		fzhsBodyMap.put("000001000000000000000002", gysFzhsBodyVOs);
		fzhsBodyMap.put("000001000000000000000003", zyFzhsBodyVOs);
		fzhsBodyMap.put("000001000000000000000006", chFzhsBodyVOs);
		for (int i = 0; i < zyFzhsBodyVOs.size(); i++) {
			zyFzhsList.add(zyFzhsBodyVOs.get(i).getName());
		}
		return fzhsBodyMap;
	}
	/**
	 * 查询该公司辅助核算项
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<Integer, AuxiliaryAccountHVO> queryFzhsHeadMap(String pk_corp)throws DZFWarpException{
		List<AuxiliaryAccountHVO> list=iEditDirectory.queryAuxiliaryAccountHVOs(pk_corp);
		Map<Integer, AuxiliaryAccountHVO> returnMap=new HashMap<Integer, AuxiliaryAccountHVO>();
		for(int i=0;i<list.size();i++){
			returnMap.put(list.get(i).getCode(), list.get(i));
		}
		return returnMap;
	}
	/**
	 * 通过自定义模板表体+票据表体拼凭证表体
	 * @param templetBVOs
	 * @param detailVOs
	 * @param pk_corp
	 * @param accountMap
	 * @return
	 * @throws DZFWarpException
	 */
	private TzpzBVO[] buildTzpzBVOsByTemplet(VouchertempletBVO[] templetBVOs,OcrInvoiceDetailVO[] detailVOs,String pk_corp,Map<String,YntCpaccountVO> accountMap,Map<String, CategorysetVO> categorysetMap,OcrInvoiceVO invoiceVO,Map<String, String> bankAccountMap,Map<String, Object[]> categoryMap,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, InventoryAliasVO> fzhsBMMap)throws DZFWarpException{
		CorpVO corp=corpService.queryByPk(pk_corp);
		TzpzBVO[] tzpzBVOs=new TzpzBVO[templetBVOs.length*detailVOs.length];
		int rowno=1;
		for (int i = 0; i < detailVOs.length; i++) {
			for (int j = 0; j < templetBVOs.length; j++) {
				TzpzBVO tzpzBVO=new TzpzBVO();
				tzpzBVO.setDr(0);
				tzpzBVO.setZy(templetBVOs[j].getZy());//摘要
				tzpzBVO.setPk_accsubj(templetBVOs[j].getPk_accsubj());//科目
				tzpzBVO.setVdirect(templetBVOs[j].getDebitmny()!=null?0:1);//科目方向：借方是0贷方是1
				YntCpaccountVO accountVO=accountMap.get(tzpzBVO.getPk_accsubj());//科目
				if(accountVO==null){
					YntCpaccountVO delAccountVO=queryDelAccountVO(tzpzBVO.getPk_accsubj());
					if(delAccountVO!=null){
						throw new BusinessException("会计科目"+delAccountVO.getAccountcode()+" "+delAccountVO.getAccountname()+"不存在，请检查！");
					}else{
						throw new BusinessException("匹配会计科目失败！");//按理不会走到这里
					}
				}
				tzpzBVO.setPk_currency(getCurrency(invoiceVO, detailVOs[i], currMap,tzpzBVO,accountVO,rateMap));//币种写死人民币
				//设置借方、贷方金额
				if(!tzpzBVO.getPk_currency().equals(DzfUtil.PK_CNY)){
					//外币汇率
					Object[] rateObj=rateMap.get(tzpzBVO.getPk_currency());//0汇率1模式(1是除else乘)
					if(rateObj==null){//出错了
						if(tzpzBVO.getVdirect()==0){
							if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_0){
								tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_1){
								tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_2){
								tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_3){
								tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_4){
								tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else{
								tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}
							tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
						}else{
							if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_0){
								tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_1){
								tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_2){
								tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_3){
								tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_4){
								tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else{
								tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
						}
						if(rateObj!=null){
							tzpzBVO.setNrate(new DZFDouble(rateObj[0].toString()));
						}
					}else{
						tzpzBVO.setNrate(new DZFDouble(rateObj[0].toString()));
						int model=Integer.parseInt(rateObj[1].toString());
						if(tzpzBVO.getVdirect()==0){
							if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_0){
								tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_1){
								tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_2){
								tzpzBVO.setYbjfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_3){
								tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_4){
								tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else{
								tzpzBVO.setYbjfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}
							tzpzBVO.setJfmny(model==1?tzpzBVO.getYbjfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbjfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setYbdfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
						}else{
							if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_0){
								tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_1){
								tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_2){
								tzpzBVO.setYbdfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_3){
								tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
							}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_4){
								tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
							}else{
								tzpzBVO.setYbdfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
							}
							tzpzBVO.setDfmny(model==1?tzpzBVO.getYbdfmny().div(tzpzBVO.getNrate()):tzpzBVO.getYbdfmny().multiply(tzpzBVO.getNrate()));
							tzpzBVO.setYbjfmny(DZFDouble.ZERO_DBL);
							tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
						}
					}
				}else{
					if(tzpzBVO.getVdirect()==0){
						if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_0){
							tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_1){
							tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_2){
							tzpzBVO.setJfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_3){
							tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
						}else if(templetBVOs[j].getDebitmny().intValue()==ZncsConst.TMPMNY_4){
							tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
						}else{
							tzpzBVO.setJfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
						}
						tzpzBVO.setDfmny(DZFDouble.ZERO_DBL);
					}else{
						if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_0){
							tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_1){
							tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_2){
							tzpzBVO.setDfmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_3){
							tzpzBVO.setDfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())));
						}else if(templetBVOs[j].getCreditmny().intValue()==ZncsConst.TMPMNY_4){
							tzpzBVO.setDfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny())));
						}else{
							tzpzBVO.setDfmny(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemmny())).add(new DZFDouble("-"+OcrUtil.turnMnyByCurrency(detailVOs[i].getItemtaxmny()))));
						}
						tzpzBVO.setJfmny(DZFDouble.ZERO_DBL);
					}
					if(tzpzBVO.getNrate()!=null&&tzpzBVO.getNrate().compareTo(DZFDouble.ZERO_DBL)>0){
						int model=Integer.parseInt(tzpzBVO.getVdef9());
						tzpzBVO.setJfmny(model==1?tzpzBVO.getJfmny().div(tzpzBVO.getNrate()):tzpzBVO.getJfmny().multiply(tzpzBVO.getNrate()));
						tzpzBVO.setDfmny(model==1?tzpzBVO.getDfmny().div(tzpzBVO.getNrate()):tzpzBVO.getDfmny().multiply(tzpzBVO.getNrate()));
						tzpzBVO.setVdef9(null);
						tzpzBVO.setNrate(null);
					}
				}
				tzpzBVO.setPk_corp(pk_corp);
				tzpzBVO.setVcode(accountVO.getAccountcode());//科目编码
				tzpzBVO.setVname(accountVO.getAccountname());//科目名称
				tzpzBVO.setKmmchie(accountVO.getFullname());//科目全称
				//判断科目是否启用了辅助核算
				if(!StringUtil.isEmpty(accountVO.getIsfzhs())&&accountVO.getIsfzhs().indexOf("1")>-1){
					setFzhsValue(accountVO, categorysetMap, pk_corp, invoiceVO, tzpzBVO,detailVOs[i],categoryMap,checkMsgMap,inventorySetVO,fzhsHeadMap,fzhsBodyMap,fzhsBMMap);
				}
				if(DZFBoolean.TRUE.equals(accountVO.getIsnum())){
					setFzhsBMVO(tzpzBVO, invoiceVO, categoryMap, detailVOs[i], fzhsBMMap, corp, inventorySetVO);
					DZFDouble tMny=tzpzBVO.getDfmny().compareTo(DZFDouble.ZERO_DBL) !=0 ? tzpzBVO.getDfmny() : tzpzBVO.getJfmny();
					String calcmode=tzpzBVO.getVdef3();
					String hsl=tzpzBVO.getVdef4();
					if(StringUtil.isEmpty(detailVOs[i].getItemamount())&&StringUtil.isEmpty(detailVOs[i].getItemprice())){
						if(StringUtil.isEmpty(calcmode)){
							tzpzBVO.setNnumber(new DZFDouble(1));//数量
						}else{
							if(calcmode.equals("1")){
								tzpzBVO.setNnumber(new DZFDouble(1).div(new DZFDouble(hsl)));//数量
							}else{
								tzpzBVO.setNnumber(new DZFDouble(1).multiply(new DZFDouble(hsl)));//数量
							}
						}
						tzpzBVO.setNprice(tMny.div(tzpzBVO.getNnumber()));//单价
					}else{
						if(!StringUtil.isEmpty(detailVOs[i].getItemamount())&&!StringUtil.isEmpty(detailVOs[i].getItemprice())){
							if(StringUtil.isEmpty(calcmode)){
								tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemamount())));//数量
							}else{
								if(calcmode.equals("1")){
									tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemamount())).div(new DZFDouble(hsl)));//数量
								}else{
									tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemamount())).multiply(new DZFDouble(hsl)));//数量
								}
							}
							if(tzpzBVO.getNnumber().compareTo(DZFDouble.ZERO_DBL)==0){
								if(StringUtil.isEmpty(calcmode)){
									tzpzBVO.setNnumber(new DZFDouble(1));//数量
								}else{
									if(calcmode.equals("1")){
										tzpzBVO.setNnumber(new DZFDouble(1).div(new DZFDouble(hsl)));//数量
									}else{
										tzpzBVO.setNnumber(new DZFDouble(1).multiply(new DZFDouble(hsl)));//数量
									}
								}
							}
							tzpzBVO.setNprice(tMny.div(tzpzBVO.getNnumber()));//单价
						}else{
							if(StringUtil.isEmpty(detailVOs[i].getItemamount())){
								if(StringUtil.isEmpty(calcmode)){
									tzpzBVO.setNnumber(new DZFDouble(1));//数量
								}else{
									if(calcmode.equals("1")){
										tzpzBVO.setNnumber(new DZFDouble(1).div(new DZFDouble(hsl)));//数量
									}else{
										tzpzBVO.setNnumber(new DZFDouble(1).multiply(new DZFDouble(hsl)));//数量
									}
								}
							}else{
								if(StringUtil.isEmpty(calcmode)){
									tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemamount())));//数量
								}else{
									if(calcmode.equals("1")){
										tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemamount())).div(new DZFDouble(hsl)));//数量
									}else{
										tzpzBVO.setNnumber(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVOs[i].getItemamount())).multiply(new DZFDouble(hsl)));//数量
									}
								}
							}
							tzpzBVO.setNprice(tMny.div(tzpzBVO.getNnumber()));//单价
						}
					}
					tzpzBVO.setVdef3(null);
					tzpzBVO.setVdef4(null);
				}
				setTaxItem(invoiceVO, detailVOs[i], tzpzBVO, corp);
				tzpzBVOs[rowno-1]=tzpzBVO;
				rowno++;
			}
		}
		return tzpzBVOs;
	}
	/**
	 * 返回匹配的自定义模板
	 * @param invoiceVO
	 * @param templetList
	 * @return
	 * @throws DZFWarpException
	 */
	private VouchertempletHVO mateTemplet(OcrInvoiceVO invoiceVO,List<VouchertempletHVO> templetList)throws DZFWarpException{
		if(templetList.size()==1){
			return templetList.get(0);
		}
		//从第二个开始找
		for(int i=1;i<templetList.size();i++){
			OcrInvoiceDetailVO[] detailVOs= (OcrInvoiceDetailVO[])invoiceVO.getChildren();
			StringBuffer sb=new StringBuffer();
			if(invoiceVO.getIstate().equals(ZncsConst.SBZT_3)){//增值税发票匹配表体
			 	for (int j = 0; j < detailVOs.length; j++) {
					sb.append(detailVOs[j].getInvname()+",");
				}
			}else{
				sb.append(invoiceVO.getInvoicetype() + "," + invoiceVO.getVsalename() + ","+invoiceVO.getVpurchname() + "," + invoiceVO.getVinvoicecode() + ","+ invoiceVO.getVinvoiceno());//+","+invoiceVO.getBilltitle());
				sb.append("," + invoiceVO.getDinvoicedate() + "," + invoiceVO.getVsaleopenacc() + ","+ invoiceVO.getVsalephoneaddr());
				sb.append("," + invoiceVO.getVpuropenacc() + "," + invoiceVO.getVpurphoneaddr() + ","+ invoiceVO.getVpurbankname() + "," + invoiceVO.getVsalebankname());
				sb.append("," + invoiceVO.getIstate() + ","+ invoiceVO.getVmemo() + "," + invoiceVO.getKeywords());
				for (int j = 0; j < detailVOs.length; j++) {
					sb.append("," + detailVOs[j].getInvname() + "," + detailVOs[j].getInvtype() + ","+ detailVOs[j].getItemunit());
				}
			}
			//格式:12&34,56,78,99&aaa&bb(,是或&是且)
			if(!StringUtil.isEmpty(templetList.get(i).getKeywords())){
				String[] keywords = templetList.get(i).getKeywords().split(",");
				for (int j = 0; j < keywords.length; j++) {
					String[] words=keywords[j].split("&");
					DZFBoolean keyWordFlag=DZFBoolean.FALSE;
					for(int k=0;k<words.length;k++){
						if (sb.toString().contains(words[k])) {
							keyWordFlag = DZFBoolean.TRUE;
							break;
						}
					}
					if (DZFBoolean.TRUE.equals(keyWordFlag)) {
						return templetList.get(i);
					}
				}
			}
		}
		//没找到就返回第一个默认的模板
		return templetList.get(0);
	}
	
	private DZFDate getInvoiceDate(OcrInvoiceVO invoiceVO){
		String date=invoiceVO.getDinvoicedate().replace("年", "-").replace("月", "-").replace("日", "");
		DZFDate trueDate=null;
		try{
			trueDate=new DZFDate(date);
		}catch(Exception e){
			throw new BusinessException(invoiceVO.getBilltitle()+" 票据日期非法");
		}
		if(new DZFDate(date).before(new DZFDate(invoiceVO.getPeriod()+"-01"))){
			trueDate=new DZFDate(invoiceVO.getPeriod()+"-01");
		}else if(new DZFDate(date).after(new DZFDate(invoiceVO.getPeriod()+"-"+new DZFDate(invoiceVO.getPeriod()+"-01").getDaysMonth()))){
			trueDate=new DZFDate(invoiceVO.getPeriod()+"-"+new DZFDate(invoiceVO.getPeriod()+"-01").getDaysMonth());
		}
		return trueDate;
	}
	/**
	 * 获得凭证日期
	 * @param paramList
	 * @param invoiceVO
	 */
	private DZFDate getVoucherDate(List<Object> paramList,OcrInvoiceVO invoiceVO){
		if(((ParaSetVO)paramList.get(0)).getVoucherdate()==0&&!StringUtil.isEmpty(invoiceVO.getDinvoicedate())){
			return getInvoiceDate(invoiceVO);
		}else{
			return new DZFDate(invoiceVO.getPeriod()+"-"+new DZFDate(invoiceVO.getPeriod()+"-01").getDaysMonth());
		}
	}
	/**
	 * 生成凭证头
	 * @param headVO
	 * @param grpvo
	 * @return
	 */
	private TzpzHVO createTzpzHVO(TzpzHVO headVO, ImageGroupVO grpvo,List<Object> paramList,OcrInvoiceVO invoiceVO,String pk_user,Map<String, Object[]> categoryMap)throws DZFWarpException {
		headVO.setPk_corp(invoiceVO.getPk_corp());//公司
		CorpVO corp=corpService.queryByPk(headVO.getPk_corp());
		headVO.setCoperatorid(StringUtil.isEmpty(pk_user)?grpvo.getCoperatorid():pk_user);//制单人
		headVO.setDoperatedate(getVoucherDate(paramList, invoiceVO));
		headVO.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
		headVO.setIshasjz(DZFBoolean.FALSE);//是否记账
		headVO.setDr(0);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setSourcebillid(grpvo==null?null:grpvo.getPrimaryKey());//来源ID
		String istate=invoiceVO.getIstate();//发票大类
		String invoicetype=invoiceVO.getInvoicetype();//发票小类
		if(!StringUtil.isEmpty(invoiceVO.getIstate())&&invoiceVO.getIstate().equals(ZncsConst.SBZT_3)){
			OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])invoiceVO.getChildren();
			if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){
				DZFBoolean isKc=DZFBoolean.FALSE;
				DZFBoolean isZc=DZFBoolean.FALSE;
				for (int i = 0; i < detailVOs.length; i++) {
					OcrInvoiceDetailVO ocrInvoiceDetailVO = detailVOs[i];
					if(!StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_billcategory())){
						if(categoryMap.get(ocrInvoiceDetailVO.getPk_billcategory())[0].toString().startsWith("11")){
							isKc=DZFBoolean.TRUE;
						}
						if(categoryMap.get(ocrInvoiceDetailVO.getPk_billcategory())[0].toString().startsWith(ZncsConst.FLCODE_ZC)){
							isZc=DZFBoolean.TRUE;
						}
					}
				}
				if(IcCostStyle.IC_ON.equals(corp.getBbuildic())&&DZFBoolean.TRUE.equals(isKc)){
					headVO.setSourcebilltype(IBillTypeCode.HP70);// 来源单据类型_销项
				}else if(DZFBoolean.TRUE.equals(corp.getHoldflag())&&DZFBoolean.TRUE.equals(isZc)){
					headVO.setSourcebilltype(IBillTypeCode.HP59);// 来源单据类型_资产卡片
				}else{
					headVO.setSourcebilltype(IBillTypeCode.HP95);// 来源单据类型_进项
				}
			}else{
				DZFBoolean isKc=DZFBoolean.FALSE;
				for (int i = 0; i < detailVOs.length; i++) {
					OcrInvoiceDetailVO ocrInvoiceDetailVO = detailVOs[i];
					if(!StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_billcategory())){
						if(categoryMap.get(ocrInvoiceDetailVO.getPk_billcategory())[0].toString().startsWith("101015")||
								categoryMap.get(ocrInvoiceDetailVO.getPk_billcategory())[0].toString().startsWith("101110")){
							isKc=DZFBoolean.TRUE;
						}
					}
				}
				if(IcCostStyle.IC_ON.equals(corp.getBbuildic())&&DZFBoolean.TRUE.equals(isKc)){
					headVO.setSourcebilltype(IBillTypeCode.HP75);// 来源单据类型_销项
				}else{
					headVO.setSourcebilltype(IBillTypeCode.HP90);// 来源单据类型_销项
				}
			}
			if(!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_3)&&!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){
				headVO.setFp_style(2);
			}else{
				headVO.setFp_style(1);
			}
		}else if(!StringUtil.isEmpty(invoiceVO.getIstate())&&invoiceVO.getIstate().equals(ZncsConst.SBZT_1)){
			headVO.setSourcebilltype(IBillTypeCode.HP85);// 银行对账单
			headVO.setFp_style(null);
		}else{
			headVO.setSourcebilltype(IBillTypeCode.HP110);// 来源单据类型
			if(!StringUtil.isEmpty(invoiceVO.getVpurchname())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVpurchname())){
				headVO.setFp_style(1);
			}else if(!StringUtil.isEmpty(invoiceVO.getVsalename())&&OcrUtil.isSameCompany(corp.getUnitname(),invoiceVO.getVsalename())){
				if(!"小规模纳税人".equals(corp.getChargedeptname())){
					headVO.setFp_style(1);
				}else{
					headVO.setFp_style(3);
				}
			}else{
				headVO.setFp_style(null);
			}
		}
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		headVO.setVyear(StringUtil.isEmpty(invoiceVO.getPeriod())?new DZFDate().getYear():Integer.parseInt(invoiceVO.getPeriod().substring(0, 4)));//会计年
		headVO.setPeriod(StringUtil.isEmpty(invoiceVO.getPeriod())?new DZFDate().toString():invoiceVO.getPeriod());//会计期间
		headVO.setNbills(1);// 设置单据张数
		headVO.setPk_image_group(grpvo==null?null:grpvo.getPrimaryKey());
		headVO.setIsocr(DZFBoolean.TRUE);
		headVO.setIautorecognize(1);//0-- 非识别 1----识别
		headVO.setUserObject(invoiceVO.getPk_invoice());//凭证上放入票的主键
		return headVO;
	}
	/**
	 * 匹配该票据走哪个凭证模板
	 * @param invoiceVO 票
	 * @param templetMap 所有凭证模板
	 * @param categoryMap 分类 
	 * @return
	 * @throws DZFWarpException
	 */
	private List<VouchertempletHVO> searchVouchertempletHVO(OcrInvoiceVO invoiceVO,Map<String, List<VouchertempletHVO>> templetMap,Map<String, Object[]> categoryMap,Object[] obj)throws DZFWarpException{
		if(obj==null){
			String pk_category=invoiceVO.getPk_billcategory();//公司分类主键
			obj=categoryMap.get(pk_category);
		}
		if(templetMap.containsKey(obj[1])){//集团分裂主键，自定义里obj[2]都有值
			return templetMap.get(obj[1]);
		}else{
//			if(StringUtil.isEmpty((String)obj[2])){//已经没有上级了
//				return null;
//			}else{
//				return searchVouchertempletHVO(invoiceVO, templetMap, categoryMap,categoryMap.get(obj[2]));
//			}
			return null;
		}
	}
	
	/**
	 * 查公司自定义模板
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, List<VouchertempletHVO>> groupVouchertempletVO(String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_vouchertemplet_h where nvl(dr,0)=0 and pk_corp=? order by pk_basecategory,orderno");
		sp.addParam(pk_corp);
		//查表头
		List<VouchertempletHVO> headList=(List<VouchertempletHVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VouchertempletHVO.class));
		Map<String, List<VouchertempletHVO>> returnMap=new HashMap<String, List<VouchertempletHVO>>();
		if(headList!=null&&headList.size()>0){
			//查表体
			List<VouchertempletBVO> bodyList=queryVouchertempletBVO(pk_corp);
			//把表体按表头主键分组
			Map<String, List<VouchertempletBVO>> detailMap = DZfcommonTools.hashlizeObject(bodyList, new String[] { "pk_vouchertemplet_h" });
			//把表体装到表头、按pk_basecategory分组
			for(int i=0;i<headList.size();i++){
				VouchertempletHVO headVO=headList.get(i);
				headVO.setChildren(detailMap.get(headVO.getPk_vouchertemplet_h()).toArray(new VouchertempletBVO[0]));
				if(returnMap.get(headVO.getPk_basecategory())==null){
					List<VouchertempletHVO> tmpList=new ArrayList<VouchertempletHVO>();
					tmpList.add(headVO);
					returnMap.put(headVO.getPk_basecategory(), tmpList);
				}else{
					returnMap.get(headVO.getPk_basecategory()).add(headVO);
				}
			}
		}
		return returnMap;
	}
	/**
	 * 查自定义模板表体
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	private List<VouchertempletBVO> queryVouchertempletBVO(String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_vouchertemplet_b where nvl(dr,0)=0 and pk_corp=? order by pk_vouchertemplet_h");
		sp.addParam(pk_corp);
		List<VouchertempletBVO> list=(List<VouchertempletBVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(VouchertempletBVO.class));
		return list;
	}
	/**
	 * 查票据表体
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<OcrInvoiceDetailVO> queryInvoiceDetail(List<OcrInvoiceVO> list) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_invoice());
		}
		sb.append("select * from ynt_interface_invoice_detail where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_invoice", pkList.toArray(new String[0])));
		sb.append(" order by rowno");
		List<OcrInvoiceDetailVO> returnList=(List<OcrInvoiceDetailVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceDetailVO.class));
		return returnList;
	}
	/**
	 * 查票据所属分类的编辑目录属性
	 * @param list
	 * @return key:图片组主键，图片组VO
	 * @throws DZFWarpException
	 */
	private Map<String, CategorysetVO> queryCategorysetVO(List<OcrInvoiceVO> list,String pk_corp) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		Set<String> pkList = new HashSet<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getPk_billcategory());
			OcrInvoiceDetailVO[] vos=(OcrInvoiceDetailVO[])ocrInvoiceVO.getChildren();
			for (int i = 0;vos!=null&&i < vos.length; i++) {
				OcrInvoiceDetailVO ocrInvoiceDetailVO = vos[i];
				if(!StringUtil.isEmpty(ocrInvoiceDetailVO.getPk_billcategory())){
					pkList.add(ocrInvoiceDetailVO.getPk_billcategory());
				}
			}
		}
		Map<String, CategorysetVO> returnMap=new HashMap<String, CategorysetVO>();
		if(pkList.size()>0){
			sb.append("select * from ynt_categoryset where nvl(dr,0)=0 ");
			sb.append(" and pk_corp=? ");
			sb.append(" and pk_category in(select pk_category from ynt_billcategory where nvl(dr,0)=0  start with "+SqlUtil.buildSqlForIn("pk_category", pkList.toArray(new String[0])));
			sb.append("  connect by prior pk_parentcategory=pk_category) ");
			sb.append(" order by pk_category");
			sp.addParam(pk_corp);
			List<CategorysetVO> returnList=(List<CategorysetVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CategorysetVO.class));
			if(returnList!=null&&returnList.size()>0){
				returnList=buildCategorysetBVO(returnList);
			}
			for (int i = 0; i < returnList.size(); i++) {
				returnMap.put(returnList.get(i).getPk_category(), returnList.get(i));
			}
		}
		return returnMap;
	}
	
	/**
	 * 查ImageGroup,现在都是一组一张，所以一张票据对应一个ImageGroup
	 * @param list
	 * @return key:图片组主键，图片组VO
	 * @throws DZFWarpException
	 */
	private Map<String, ImageGroupVO> queryImageGroupVO(List<OcrInvoiceVO> list) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		Map<String, ImageGroupVO> returnMap=new HashMap<String, ImageGroupVO>();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			if(!StringUtil.isEmpty(ocrInvoiceVO.getPk_image_group())){
				pkList.add(ocrInvoiceVO.getPk_image_group());
			}
		}
		if(pkList.size()>0){
			sb.append("select * from ynt_image_group where nvl(dr,0)=0 ");
			sb.append(" and "+SqlUtil.buildSqlForIn("pk_image_group", pkList.toArray(new String[0])));
			sb.append(" order by pk_image_group");
			List<ImageGroupVO> returnList=(List<ImageGroupVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(ImageGroupVO.class));
			returnList=buildImageLibraryVO(returnList);
			for (int i = 0; i < returnList.size(); i++) {
				returnMap.put(returnList.get(i).getPk_image_group(), returnList.get(i));
			}
		}
		return returnMap;
	}
	/**
	 * 查辅助核算设置
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<CategorysetVO> buildCategorysetBVO(List<CategorysetVO> list) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (CategorysetVO categorysetVO : list) {
			pkList.add(categorysetVO.getPk_categoryset());
		}
		sb.append("select * from ynt_categoryset_fzhs where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_categoryset", pkList.toArray(new String[0])));
		sb.append(" order by pk_categoryset");
		List<CategorysetBVO> BVOList=(List<CategorysetBVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CategorysetBVO.class));
		if(BVOList!=null&&BVOList.size()>0){
			Map<String, List<CategorysetBVO>> detailMap = DZfcommonTools.hashlizeObject(BVOList, new String[] { "pk_categoryset" });
			for (int i = 0; i < list.size(); i++) {
				if(detailMap.get(list.get(i).getPk_categoryset())!=null){
					list.get(i).setChildren(detailMap.get(list.get(i).getPk_categoryset()).toArray(new CategorysetBVO[0]));
				}
			}
		}
		return list;
	}
	/**
	 * 查出图片组表体，放到表头里
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<ImageGroupVO> buildImageLibraryVO(List<ImageGroupVO> list) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (ImageGroupVO imageGroupVO : list) {
			pkList.add(imageGroupVO.getPk_image_group());
		}
		sb.append("select * from ynt_image_library where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_image_group", pkList.toArray(new String[0])));
		sb.append(" order by pk_image_group");
		List<ImageLibraryVO> libraryList=(List<ImageLibraryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(ImageLibraryVO.class));
		Map<String, List<ImageLibraryVO>> detailMap = DZfcommonTools.hashlizeObject(libraryList, new String[] { "pk_image_group" });
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setChildren(detailMap.get(list.get(i).getPk_image_group()).toArray(new ImageLibraryVO[0]));
		}
		return list;
	}
	/**
	 * 把票据分组
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, List<OcrInvoiceVO>> groupByCategoryType(List<OcrInvoiceVO> invoiceList,Map<String, Object[]> categoryMap)throws DZFWarpException{
		Map<String, List<OcrInvoiceVO>> returnMap=new HashMap<String, List<OcrInvoiceVO>>();
		List<OcrInvoiceVO> zdyList=new ArrayList<OcrInvoiceVO>();
		List<OcrInvoiceVO> fZdyList=new ArrayList<OcrInvoiceVO>();
		List<OcrInvoiceVO> errorList=new ArrayList<OcrInvoiceVO>();
		for (int i = 0; i < invoiceList.size(); i++) {
			String pk_category=invoiceList.get(i).getPk_billcategory();
			if(categoryMap.get(pk_category)[0].toString().startsWith(ZncsConst.FLCODE_ZDY)){
				zdyList.add(invoiceList.get(i));
			}else if(categoryMap.get(pk_category)[0].toString().startsWith(ZncsConst.FLCODE_WSB)||categoryMap.get(pk_category)[0].toString().startsWith(ZncsConst.FLCODE_WT)){
				errorList.add(invoiceList.get(i));
			}else{
				fZdyList.add(invoiceList.get(i));
			}
		}
		returnMap.put("zdy", zdyList);
		returnMap.put("fzdy", fZdyList);
		returnMap.put("error", errorList);
		return returnMap;
	}
	/**
	 * 查询公司分类树
	 * @param period
	 * @param pk_corp
	 * @return key:分类主键value[]{编码，集团分类主键(有可能空),父主键,级次,名称}
	 * @throws DZFWarpException
	 */
	private Map<String, Object[]> queryCategoryMap(String period, String pk_corp,List<List<Object[]>> levelList,DZFBoolean flag)throws DZFWarpException{
		Map<String, Object[]> returnMap=new HashMap<String, Object[]>();
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category,categorycode,pk_basecategory,pk_parentcategory,categorylevel,categoryname,isleaf,categorytype from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp=?  ");
		if(flag!=null){
			if(DZFBoolean.TRUE.equals(flag)){
				sb.append(" and nvl(isaccount,'N')='Y' ");
			}else{
				sb.append(" and nvl(isaccount,'N')='N' ");
			}
		}
		sb.append(" order by categorylevel desc ");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		Map<Object, List<Object[]>> levelMap=new LinkedHashMap<Object, List<Object[]>>();
		for(int i=0;i<list.size();i++){
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(), new Object[]{obj[1],obj[2],obj[3],obj[4],obj[5],obj[6],obj[7]});
			if(levelMap.get(obj[4])==null){
				List<Object[]> tmpList=new ArrayList<Object[]>();
				tmpList.add(obj);
				levelMap.put(obj[4], tmpList);
			}else{
				levelMap.get(obj[4]).add(obj);
			}
		}
		if(levelList!=null){
			Iterator<Object> itor=levelMap.keySet().iterator();
			while(itor.hasNext()){
				levelList.add(levelMap.get(itor.next()));
			}
		}
		return returnMap;
	}
	
	/**
	 * 返回分类全名称
	 * @return
	 * @throws DZFWarpException
	 */
	@Override
	public Map<String, String> queryCategoryFullName(List<String> pk_categoryList,String period,String pk_corp)throws DZFWarpException{
		Map<String, String> returnMap=new HashMap<String, String>();
		if(pk_categoryList!=null&&pk_categoryList.size()>0){
			 Map<String, Object[]> categoryMap=queryCategoryMap(period, pk_corp, null, null);
			 if(categoryMap!=null&&categoryMap.size()>0){
				 StringBuffer fullName=new StringBuffer();
				 for (int i = 0; i < pk_categoryList.size(); i++) {
					fullName=new StringBuffer();
					String pk_category=pk_categoryList.get(i);//末级分类
					addParentCatgoryName(fullName, pk_category, categoryMap);
					if(fullName.length()>0){
						returnMap.put(pk_category, fullName.substring(1, fullName.length()));
					}
				}
				
			 }
		}
		return returnMap;
	}
	
	private void addParentCatgoryName(StringBuffer fullName,Object pk_parent,Map<String, Object[]> categoryMap)throws DZFWarpException{
		if(pk_parent!=null&&!StringUtil.isEmpty(pk_parent.toString())){
			Object[] obj=categoryMap.get(pk_parent.toString());
			if(obj!=null){
				fullName.insert(0, "-"+obj[4].toString());
				addParentCatgoryName(fullName, obj[2], categoryMap);
			}
		}
	}
	/**
	 * 查询票据
	 * @return
	 */
	public List<OcrInvoiceVO> queryOcrInvoiceVOs(String pk_category, String pk_bills, String period, String pk_corp,String pk_parent,DZFBoolean isErrorVoucher){
		List<OcrInvoiceVO> invoiceList=null;
		if(StringUtil.isEmpty(pk_category)&&StringUtil.isEmpty(pk_bills)){
			//全部
			invoiceList=queryOcrInvoiceVOsByWhere(null,null, period, pk_corp,isErrorVoucher);
		}else if(StringUtil.isEmpty(pk_category)&&!StringUtil.isEmpty(pk_bills)){
			//按一个分类下的票据
			invoiceList=queryOcrInvoiceVOsByWhere(null,pk_bills.split(","), period, pk_corp,isErrorVoucher);
		}else if(!StringUtil.isEmpty(pk_category)&&StringUtil.isEmpty(pk_bills)){
			//按分类
			//1、得到分类树的所有下级分类
			String[] childTreeKeys=getChildKeys(pk_category, period, pk_corp);
			//2、得到要做账的票
			invoiceList=queryOcrInvoiceVOsByWhere(childTreeKeys,null, period, pk_corp,isErrorVoucher);
		}else{
			throw new BusinessException("请求参数错误");
		}
		if(!StringUtil.isEmpty(pk_parent)&&pk_parent.startsWith("bank_")||!StringUtil.isEmpty(pk_category)&&pk_category.startsWith("bank_")){
			String unitName=corpService.queryByPk(pk_corp).getUnitname();
			List<OcrInvoiceVO> returnList=new ArrayList<OcrInvoiceVO>();
			for(int i=0;i<invoiceList.size();i++){
				OcrInvoiceVO invVO=invoiceList.get(i);
				if(StringUtil.isEmpty(invVO.getIstate())||!invVO.getIstate().equals(ZncsConst.SBZT_1)){
					continue;
				}
				String vpurchname=invVO.getVpurchname();//购方名称
				String vpurchtaxno=invVO.getVpurchtaxno();//购方账号
				String vsalename=StringUtil.isEmpty(invVO.getVsalename())?"":invVO.getVsalename();//销方名称
				String vsaletaxno=invVO.getVsaletaxno();//销方账号
				if(!StringUtil.isEmpty(vpurchname)&&OcrUtil.isSameCompany(unitName,vpurchname)&&(StringUtil.isEmpty(vsalename)||!OcrUtil.isSameCompany(unitName,vsalename))){//购方
					if((!StringUtil.isEmpty(pk_parent)&&pk_parent.equals("bank_null")||!StringUtil.isEmpty(pk_category)&&pk_category.equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)){
						returnList.add(invVO);
					}else{
						String code=null;
						if(!StringUtil.isEmpty(pk_parent)&&pk_parent.startsWith("bank_")){
							code=pk_parent.substring(5, pk_parent.length());
						}else{
							code=pk_category.substring(5, pk_category.length());
						}
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if((StringUtil.isEmpty(vpurchname)||!OcrUtil.isSameCompany(unitName,vpurchname))&&!StringUtil.isEmpty(vsalename)&&OcrUtil.isSameCompany(unitName,vsalename)){//销方
					if((!StringUtil.isEmpty(pk_parent)&&pk_parent.equals("bank_null")||!StringUtil.isEmpty(pk_category)&&pk_category.equals("bank_null"))&&StringUtil.isEmpty(vsaletaxno)){
						returnList.add(invVO);
					}else{
						String code=null;
						if(!StringUtil.isEmpty(pk_parent)&&pk_parent.startsWith("bank_")){
							code=pk_parent.substring(5, pk_parent.length());
						}else{
							code=pk_category.substring(5, pk_category.length());
						}
						if(!StringUtil.isEmpty(vsaletaxno)&&vsaletaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else if(!StringUtil.isEmpty(vpurchname)&&OcrUtil.isSameCompany(unitName,vpurchname)&&!StringUtil.isEmpty(vsalename)&&OcrUtil.isSameCompany(unitName,vsalename)){//都有可能是户间转账
					if((!StringUtil.isEmpty(pk_parent)&&pk_parent.equals("bank_null")||!StringUtil.isEmpty(pk_category)&&pk_category.equals("bank_null"))&&StringUtil.isEmpty(vpurchtaxno)&&StringUtil.isEmpty(vsaletaxno)){
						returnList.add(invVO);
					}else{
						String code=null;
						if(!StringUtil.isEmpty(pk_parent)&&pk_parent.startsWith("bank_")){
							code=pk_parent.substring(5, pk_parent.length());
						}else{
							code=pk_category.substring(5, pk_category.length());
						}
						if(!StringUtil.isEmpty(vpurchtaxno)&&vpurchtaxno.equals(code)||!StringUtil.isEmpty(vsaletaxno)&&vsaletaxno.equals(code)){
							returnList.add(invVO);
						}
					}
				}else{//出问题了
					if(!StringUtil.isEmpty(pk_parent)&&pk_parent.equals("bank_null")||!StringUtil.isEmpty(pk_category)&&pk_category.equals("bank_null")){
						returnList.add(invVO);
					}
				}
			}
			invoiceList=returnList;
		}
		return invoiceList;
	}
	/**
	 * 查询票据
	 * @param categoryKeys
	 * @return
	 * @throws DZFWarpException
	 */
	private List<OcrInvoiceVO> queryOcrInvoiceVOsByWhere(String[] categoryKeys, String[] pk_invoices,String period, String pk_corp,DZFBoolean isErrorVoucher)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.*,d.iorder,c.inoutflag from ynt_interface_invoice a,ynt_image_group b,ynt_billcategory c,ynt_image_ocrlibrary d");
		sb.append(" where a.pk_image_group=b.pk_image_group  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and nvl(c.dr,0)=0 and nvl(d.dr,0)=0 and nvl(c.isaccount,'N')='N' and a.ocr_id=d.pk_image_ocrlibrary and a.pk_billcategory=c.pk_category and b.istate !=205 ");
		sb.append(" and b.istate !=100 and b.istate !=101 ");
		if(categoryKeys!=null&&categoryKeys.length>0){
			sb.append(" and  "+SqlUtil.buildSqlForIn("a.pk_billcategory", categoryKeys));
		}else if(pk_invoices!=null&&pk_invoices.length>0){
			sb.append(" and  "+SqlUtil.buildSqlForIn("a.pk_invoice", pk_invoices));
		}
		sb.append(" and a.pk_corp=? ");
		sp.addParam(pk_corp);
		if(!StringUtil.isEmpty(period)){
			sb.append(" and a.period=?");
			sp.addParam(period);
		}
		if(isErrorVoucher==null||DZFBoolean.FALSE.equals(isErrorVoucher)){
			sb.append(" and pk_billcategory not in(select pk_category from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and categorycode in("+ZncsConst.FLCODE_WT+","+ZncsConst.FLCODE_WSB+"))");
			sp.addParam(pk_corp);
			sp.addParam(period);
		}
		sb.append(" order by categorylevel desc,d.iorder ");
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		return list;
	}
	/**
	 * 找下级分类树主键
	 * @param pk_category
	 * @return
	 * @throws DZFWarpException
	 */
	private String[] getChildKeys(String pk_category,String period, String pk_corp)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select pk_category from ynt_billcategory where nvl(dr,0)=0 and nvl(isaccount,'N')='N' and pk_corp=? and period=? start with pk_category=? connect by prior pk_category=pk_parentcategory order by categorylevel");
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(pk_category);
		ArrayList<Object[]> list=(ArrayList<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
		List<String> returnList=new ArrayList<String>();
		for(int i=0;i<list.size();i++){
			returnList.add(list.get(i)[0].toString());
		}
		return returnList.toArray(new String[0]);
	}
	private Map<String, BillCategoryVO> getFalseMap(Map falsemap, String pk_billcategory)
	{
		if (falsemap == null && !StringUtil.isEmpty(pk_billcategory))
		{
			falsemap = new LinkedHashMap<String, BillCategoryVO>();
			BillCategoryVO categoryvo = (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_billcategory);
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and nvl(isaccount,'N')='N' ");
			sb.append(" order by categorylevel ");
			sp.addParam(categoryvo.getPk_corp());
			sp.addParam(categoryvo.getPeriod());
			List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,new BeanListProcessor(BillCategoryVO.class));
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					falsemap.put(list.get(i).getPk_category(), list.get(i));
					updateFullName(falsemap, list.get(i));
					falsemap.put(list.get(i).getFullcategoryname(), list.get(i));
				}
			}
		}
		return falsemap;
	}
	private void updateFullName(Map map, BillCategoryVO categoryvo)
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(categoryvo.getCategoryname());
		BillCategoryVO vo = categoryvo;
		while (!StringUtil.isEmpty(vo.getPk_parentcategory()))
		{
			vo = (BillCategoryVO)map.get(vo.getPk_parentcategory());
			if (vo != null)
			{
				sbuf.insert(0, "~");
				sbuf.insert(0, vo.getCategoryname());

			}
			else
			{
				break;
			}
		}
		categoryvo.setFullcategoryname(sbuf.toString());

	}
	private Map<String, BillCategoryVO> getTrueMap(Map falsemap, Map truemap, String pk_billcategory)
	{
		if (truemap == null && !StringUtil.isEmpty(pk_billcategory))
		{
			truemap = new LinkedHashMap<String, BillCategoryVO>();
			BillCategoryVO categoryvo = (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, pk_billcategory);
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and nvl(isaccount,'N')='Y' ");
			sb.append(" order by categorylevel ");
			sp.addParam(categoryvo.getPk_corp());
			sp.addParam(categoryvo.getPeriod());
			List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,new BeanListProcessor(BillCategoryVO.class));
			if(list!=null&&list.size()>0){
				for(int i=0;i<list.size();i++){
					truemap.put(list.get(i).getPk_category(), list.get(i));
					updateFullName(truemap, list.get(i));
					truemap.put(list.get(i).getFullcategoryname(), list.get(i));
				}
			}
			truemap= iZncsNewTransService.newInsertCategoryVOs(falsemap, truemap, categoryvo.getPk_corp() + categoryvo.getPeriod());
//			turnBillCategoryMap(falsemap, truemap);
		}
		return truemap;
	}
	
	private void updateSourceBillType(TzpzHVO tzpzHVO)throws DZFWarpException{
		if(tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP110)&&tzpzHVO.getIautorecognize()==2&&!StringUtil.isEmpty(tzpzHVO.getPk_image_group())){
			tzpzHVO.setIautorecognize(1);
			SQLParameter sp=new SQLParameter();
			sp.addParam(tzpzHVO.getPk_image_group().split(",")[0]);
			sp.addParam(tzpzHVO.getPk_corp());
			String sql="select * from ynt_bankstatement where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			List<BankStatementVO2> list1=(List<BankStatementVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BankStatementVO2.class));
			if(list1!=null&&list1.size()>0){
				tzpzHVO.setSourcebilltype(IBillTypeCode.HP85);
				return;
			}
			sql="select * from ynt_vatsaleinvoice where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			List<VATSaleInvoiceVO2> list2=(List<VATSaleInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATSaleInvoiceVO2.class));
			if(list2!=null&&list2.size()>0){
				tzpzHVO.setSourcebilltype(IBillTypeCode.HP90);
				return;
			}
			sql="select * from ynt_vatincominvoice where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			List<VATInComInvoiceVO2> list3=(List<VATInComInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATInComInvoiceVO2.class));
			if(list3!=null&&list3.size()>0){
				tzpzHVO.setSourcebilltype(IBillTypeCode.HP95);
				return;
			}
		}
	}
	/**
	 * 保存凭证
	 */
	@Override
	public void saveVouchersBefore(List<TzpzHVO> tzpzHVOs) throws DZFWarpException {
		DZFBoolean voucherFlag = getVoucherFlag();
		if(DZFBoolean.FALSE.equals(voucherFlag))return;
		
		if(!StringUtil.isEmpty(tzpzHVOs.get(0).getIsMerge())&&tzpzHVOs.get(0).getIsMerge().equals("Y"))return;
		
		boolean isTmpVoucher=false;
		if(!StringUtil.isEmpty(tzpzHVOs.get(0).getPrimaryKey())){
			TzpzHVO tzpzHVO=(TzpzHVO)singleObjectBO.queryByPrimaryKey(TzpzHVO.class, tzpzHVOs.get(0).getPrimaryKey());
			if(tzpzHVO == null || tzpzHVO!=null&&tzpzHVO.getVbillstatus()!= IVoucherConstants.TEMPORARY)return;
			if(StringUtil.isEmpty(tzpzHVO.getSourcebilltype())
					|| (!tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP85) 
							&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP90) 
							&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP95) 
							&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP59)
							&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP70)
							&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP75)
							&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP110))){
				return;
			}
			isTmpVoucher=true;
			SQLParameter sp=new SQLParameter();
			sp.addParam(tzpzHVOs.get(0).getPrimaryKey());
			String sourceids="";
			if(tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP85)){
				BankStatementVO2[] vos=(BankStatementVO2[])singleObjectBO.queryByCondition(BankStatementVO2.class, "nvl(dr,0)=0 and pk_tzpz_h=?", sp);
				if(vos!=null&&vos.length>0){
					for (int i = 0; i < vos.length; i++) {
						sourceids=sourceids+","+vos[i].getPrimaryKey();
					}
					if(!StringUtil.isEmpty(sourceids)){
						tzpzHVOs.get(0).setSourcebillid(sourceids.substring(1, sourceids.length()));
						tzpzHVOs.get(0).setSourcebilltype(tzpzHVO.getSourcebilltype());
					}
				}
			}else if(tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP90) || tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP75)){
				VATSaleInvoiceVO2[] vos=(VATSaleInvoiceVO2[])singleObjectBO.queryByCondition(VATSaleInvoiceVO2.class, "nvl(dr,0)=0 and pk_tzpz_h=?", sp);
				if(vos!=null&&vos.length>0){
					for (int i = 0; i < vos.length; i++) {
						sourceids=sourceids+","+vos[i].getPrimaryKey();
					}
					if(!StringUtil.isEmpty(sourceids)){
						tzpzHVOs.get(0).setSourcebillid(sourceids.substring(1, sourceids.length()));
						tzpzHVOs.get(0).setSourcebilltype(tzpzHVO.getSourcebilltype());
					}
				}
			}else if(tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP95) || tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP70)){	//增加入库单(可能来源进项发票）
				VATInComInvoiceVO2[] vos=(VATInComInvoiceVO2[])singleObjectBO.queryByCondition(VATInComInvoiceVO2.class, "nvl(dr,0)=0 and pk_tzpz_h=?", sp);
				if(vos!=null&&vos.length>0){
					for (int i = 0; i < vos.length; i++) {
						sourceids=sourceids+","+vos[i].getPrimaryKey();
					}
					if(!StringUtil.isEmpty(sourceids)){
						tzpzHVOs.get(0).setSourcebillid(sourceids.substring(1, sourceids.length()));
						tzpzHVOs.get(0).setSourcebilltype(tzpzHVO.getSourcebilltype());
					}
				}
			}else{
				return;
			}
		}else{
			if(StringUtil.isEmpty(tzpzHVOs.get(0).getSourcebilltype())
					|| (!tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP85) 
							&& !tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP90) 
							&& !tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP95) 
							&& !tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP59)
							&& !tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP70)
							&& !tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP75)
							&& !tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP110))){
				if(StringUtil.isEmpty(tzpzHVOs.get(0).getSourcebilltype())&&!StringUtil.isEmpty(tzpzHVOs.get(0).getPk_image_group())){
					List<OcrInvoiceVO> ocrList=queryOcrInvoiceVOsByImageGroup(tzpzHVOs.get(0).getPk_image_group());
					if(ocrList==null||ocrList.size()==0)return;
					if(DZFBoolean.TRUE.equals(iBillcategory.checkHaveZckp(ocrList.toArray(new OcrInvoiceVO[0])))){
						throw new BusinessException("所选票据已生成后续单据，请在资产卡片节点处理！");
					}
					if(DZFBoolean.TRUE.equals(iBillcategory.checkHaveIctrade(ocrList.toArray(new OcrInvoiceVO[0])))){
						throw new BusinessException("所选票据已生成后续单据，请在出入库单节点处理！");
					}
					StringBuffer invoiceKey=new StringBuffer();;
					for(int i=0;i<ocrList.size();i++){
						invoiceKey.append(ocrList.get(i).getPk_invoice()+",");
					}
					tzpzHVOs.get(0).setUserObject(invoiceKey.toString().substring(0, invoiceKey.length()-1));
					tzpzHVOs.get(0).setSourcebilltype(IBillTypeCode.HP110);
					tzpzHVOs.get(0).setIautorecognize(2);
				}else{
					return;
				}
			}
		}
		
		CorpVO corp=corpService.queryByPk(tzpzHVOs.get(0).getPk_corp());
		//1、查公司参数
		List<Object> paramList=queryParams(tzpzHVOs.get(0).getPk_corp());
		//2、未制证分类
//		Map<String, BillCategoryVO> falseMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corp.getPk_corp(), tzpzHVOs.get(0).getPeriod(),"N");
		//3、已制证分类
//		Map<String, BillCategoryVO> trueMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corp.getPk_corp(), tzpzHVOs.get(0).getPeriod(),"Y");
		//4、创建差异已制证分类树，并得到最新的已制证分类树
//		trueMap= iZncsNewTransService.newInsertCategoryVOs(falseMap, trueMap,tzpzHVOs.get(0).getPk_corp()+tzpzHVOs.get(0).getPeriod());
		//5、把已制证分类树和未制证分类map，转换成主键为key的map,增加到原map里
//		turnBillCategoryMap(falseMap, trueMap);
		updateSourceBillType(tzpzHVOs.get(0));//手动凭证的保存来源都是110，这里改来源HP85 HP90 HP95
		if(tzpzHVOs.get(0).getUserObject()!=null&&!tzpzHVOs.get(0).getUserObject().toString().equals("")){
			//6、取所有凭证上的票据主键
			List<String> invoiceKeyList=getInvoiceKey(tzpzHVOs);
			//7、查票据invoice
			List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOsByWhere(null, invoiceKeyList.toArray(new String[0]), null, corp.getPk_corp(),DZFBoolean.TRUE);// tzpzHVOs.get(0).getPeriod(), corp.getPk_corp(),DZFBoolean.TRUE);
			//8、查票据表体
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
			//9、表体按表头主键分组
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
			for(int i=0;i<invoiceList.size();i++){
				OcrInvoiceDetailVO[] detailVOs=detailMap.get(invoiceList.get(i).getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]);
				invoiceList.get(i).setChildren(detailVOs);
			}
			//10、编辑目录
			Map<String, CategorysetVO> categorysetMap=queryCategorysetVO(invoiceList,corp.getPk_corp());
//			//11、查票据关联的ImageGroup
//			Map<String, ImageGroupVO> groupMap=queryImageGroupVO(invoiceList);
			//12、更新原票上表头表体的pk_billcategory为已制证的pk_category
			updateNewPk_billcategory(invoiceList, detailMap, null, null);//falseMap, trueMap);
//			//13、发票map
//			Map<String, OcrInvoiceVO> invoiceMap=getOcrInvoiceMap(invoiceList);
			if (tzpzHVOs != null && tzpzHVOs.size() > 0) {
				for (int i = 0; i < tzpzHVOs.size(); i++) {
					String pk_image_groups=tzpzHVOs.get(i).getPk_image_group();//这里是多个imageGroup
					String pk_invoices=tzpzHVOs.get(i).getUserObject().toString();//这里是多个pk_invoice
					List<String> groupList=new ArrayList<String>();
					String[] groups=pk_image_groups.split(",");
					String[] invoices=pk_invoices.split(",");
					for (int j = 0; j < groups.length; j++) {
						groupList.add(groups[j]);
						//更新进项、销项、银行对账单业务类型
						updateSourceModel(tzpzHVOs.get(i), invoices[j], groups[j],DZFBoolean.TRUE, null, null,null);//falseMap, trueMap);
					}
					String pk_image_group=iImageGroupService.processMergeGroup(tzpzHVOs.get(i).getPk_corp(), null, groupList);
					for(OcrInvoiceVO invVO:invoiceList){
						invVO.setPk_image_group(pk_image_group);
					}
					tzpzHVOs.get(i).setPk_image_group(pk_image_group);
					tzpzHVOs.get(i).setSourcebillid(pk_image_group);
					//14、通过pk_image_group找到85 90 95的来源,创建PzSourceRelationVO
					PzSourceRelationVO[] relVOs=buildPzSourceRelationVOs(tzpzHVOs.get(i),invoiceList,null,categorysetMap,corp,null);
					if(relVOs!=null&&relVOs.length>0){
						tzpzHVOs.get(i).setSource_relation(relVOs);
						String sourceid="";
						for (int j = 0; j < relVOs.length; j++) {
							PzSourceRelationVO pzSourceRelationVO = relVOs[j];
							sourceid=sourceid+pzSourceRelationVO.getSourcebillid()+",";
							tzpzHVOs.get(i).setSourcebillid(relVOs[0].getSourcebillid());
						}
						if(tzpzHVOs.get(i).getSourcebilltype().equals(IBillTypeCode.HP85)
								||tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP90)
								||tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP95)){
							tzpzHVOs.get(i).setSourcebillid(sourceid.substring(0, sourceid.length()-1));
						}
						
					}
					tzpzHVOs.get(i).setVbillstatus(IVoucherConstants.FREE);
//					iVoucherService.saveVoucher(corp, tzpzHVOs.get(i));
				}
			}
		}else{//进项、销项、资产卡片直接保存
			if(!StringUtil.isEmpty(tzpzHVOs.get(0).getSourcebillid())){
				updateSourceModel2(tzpzHVOs.get(0), tzpzHVOs.get(0).getSourcebillid().split(","), null, DZFBoolean.TRUE, null, null,null);//falseMap,trueMap);
			}
			//卡片保存凭证看来源，如果这张票全是固定资产并且全制证了，那票变成已制证，否则一直是未制证
			if(tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP59)){
				updateInvoiceByCard(tzpzHVOs.get(0));
			}
			if(tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP75)||tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP70)){//出库-把票由未做账变成已做账
				updateInvoiceByCrk(tzpzHVOs.get(0));
			}
		}
		// 判断银行对账饭 销进项 合并生成凭证
		if (isTmpVoucher&&(IBillTypeCode.HP85.equals(tzpzHVOs.get(0).getSourcebilltype())
				|| IBillTypeCode.HP90.equals(tzpzHVOs.get(0).getSourcebilltype())
				|| IBillTypeCode.HP95.equals(tzpzHVOs.get(0).getSourcebilltype())
				|| IBillTypeCode.HP70.equals(tzpzHVOs.get(0).getSourcebilltype())
				|| IBillTypeCode.HP75.equals(tzpzHVOs.get(0).getSourcebilltype())
				)) {
			if (!StringUtil.isEmpty(tzpzHVOs.get(0).getSourcebillid())) {
				String[] sourcebillids = tzpzHVOs.get(0).getSourcebillid().split(",");
				tzpzHVOs.get(0).setSourcebillid(sourcebillids[0]);
			}
		}
	}
	
	/**
	 * 过滤掉低版本的数据，低版本不需要换业务类型
	 * @param invoiceList
	 * @return
	 */
	private List<OcrInvoiceVO> filterVersionInvoiceVO(List<OcrInvoiceVO> invoiceList){
		List<OcrInvoiceVO> returnList=new ArrayList<OcrInvoiceVO>();
		for(int i=0;i<invoiceList.size();i++){
			if(invoiceList.get(i).getVersion()!=null&&invoiceList.get(i).getVersion().compareTo(new DZFDouble(1.0))>-1){
				returnList.add(invoiceList.get(i));
			}
		}
		return returnList;
	}
	/**
	 * 出入库单直接总总账调用
	 * @param tzpzHVO
	 * @throws DZFWarpException
	 */
	private void updateInvoiceByCrk(TzpzHVO tzpzHVO)throws DZFWarpException{
		String pk_image_group=tzpzHVO.getPk_image_group();
//		Map<String, BillCategoryVO> falseMap=iZncsNewTransService.queryCategoryVOs_IsAccount(tzpzHVO.getPk_corp(), tzpzHVO.getPeriod(),"N");
//		//已制证分类
//		Map<String, BillCategoryVO> trueMap=iZncsNewTransService.queryCategoryVOs_IsAccount(tzpzHVO.getPk_corp(), tzpzHVO.getPeriod(),"Y");
//		turnBillCategoryMap(falseMap, trueMap);
		if(!StringUtil.isEmpty(pk_image_group)){
			List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOsByImageGroup(pk_image_group);
			invoiceList=filterVersionInvoiceVO(invoiceList);
			if(invoiceList.size()==0)return;
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
			//更新票上的分类
			updateNewPk_billcategory(invoiceList, detailMap, null, null);//falseMap, trueMap);
			//更新进项发票的业务类型
			for (int i = 0; i < invoiceList.size(); i++) {
				updateSourceModel(tzpzHVO, invoiceList.get(i).getPk_invoice(), invoiceList.get(i).getPk_image_group(),DZFBoolean.TRUE, null, null,null);// falseMap, trueMap);
			}
		}else{
			List<String> pk_categoryNewList=new ArrayList<String>();
			if(tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP75)){//销项
				String sql="select * from ynt_vatsaleinvoice where pk_vatsaleinvoice in (select sourcebillid from ynt_ictrade_h where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("PK_ICTRADE_H", tzpzHVO.getSourcebillid().split(","))+") and pk_corp = ? and nvl(dr,0) = 0";
				SQLParameter sp=new SQLParameter();
				sp.addParam(tzpzHVO.getPk_corp());
				List<VATSaleInvoiceVO2> list=(List<VATSaleInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATSaleInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					Map<String, BillCategoryVO> falseMap = getFalseMap(null, (String)list.get(0).getAttributeValue("pk_model_h"));
					Map<String, BillCategoryVO> trueMap = getTrueMap(falseMap, null, (String)list.get(0).getAttributeValue("pk_model_h"));
					
					for (int i = 0; i < list.size(); i++) {
						BillCategoryVO falseVO=falseMap.get((String)list.get(i).getAttributeValue("pk_model_h"));
						BillCategoryVO trueVO=trueMap.get(falseVO.getFullcategoryname());
						String pk_category_new=null;
						if(trueVO.getCategorytype()==3||trueVO.getCategorytype()==4){
							pk_category_new=trueVO.getPk_parentcategory();
						}else{
							pk_category_new=trueVO.getPk_category();
						}
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
						pk_categoryNewList.add(pk_category_new);
					}
					
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, tzpzHVO.getPeriod(), tzpzHVO.getPk_corp());
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
					}
					singleObjectBO.updateAry(list.toArray(new VATSaleInvoiceVO2[0]), new String[]{"pk_model_h","busitypetempname"});
				}
			}else{//进项或者资产
				String sql="select * from ynt_vatincominvoice where pk_vatincominvoice in(select sourcebillid from ynt_ictrade_h where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("PK_ICTRADE_H", tzpzHVO.getSourcebillid().split(","))+") and pk_corp = ? and nvl(dr,0) = 0";
				SQLParameter sp=new SQLParameter();
				sp.addParam(tzpzHVO.getPk_corp());
				List<VATInComInvoiceVO2> list=(List<VATInComInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATInComInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					Map<String, BillCategoryVO> falseMap = getFalseMap(null, (String)list.get(0).getAttributeValue("pk_model_h"));
					Map<String, BillCategoryVO> trueMap = getTrueMap(falseMap, null, (String)list.get(0).getAttributeValue("pk_model_h"));
					
					for(int i=0;i<list.size();i++){
						BillCategoryVO falseVO=falseMap.get((String)list.get(i).getAttributeValue("pk_model_h"));
						BillCategoryVO trueVO=trueMap.get(falseVO.getFullcategoryname());
						String pk_category_new=null;
						if(trueVO.getCategorytype()==3||trueVO.getCategorytype()==4){
							pk_category_new=trueVO.getPk_parentcategory();
						}else{
							pk_category_new=trueVO.getPk_category();
						}
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
						pk_categoryNewList.add(pk_category_new);
					}
					
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, tzpzHVO.getPeriod(), tzpzHVO.getPk_corp());
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
					}
					singleObjectBO.updateAry(list.toArray(new VATInComInvoiceVO2[0]), new String[]{"pk_model_h","busitypetempname"});
				}
			}
		}
	}
	/**
	 * 更新发票状态
	 * @param tzpzHVO
	 * @throws DZFWarpException
	 */
	private void updateInvoiceByCard(TzpzHVO tzpzHVO)throws DZFWarpException{
		String pk_image_group=tzpzHVO.getPk_image_group();
		if(!StringUtil.isEmpty(pk_image_group)){
			List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOsByImageGroup(pk_image_group);
			invoiceList=filterVersionInvoiceVO(invoiceList);
			if(invoiceList.size()==0)return;
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
			//更新票上的分类
			updateNewPk_billcategory(invoiceList, detailMap, null, null);//falseMap, trueMap);
			//更新进项发票的业务类型
			for (int i = 0; i < invoiceList.size(); i++) {
				updateSourceModel(tzpzHVO, invoiceList.get(i).getPk_invoice(), invoiceList.get(i).getPk_image_group(),DZFBoolean.TRUE, null, null,null);// falseMap, trueMap);
			}
		}
		
//		PzSourceRelationVO[] relVOs=tzpzHVO.getSource_relation();
//		if(relVOs==null)return;
//		List<String> zcPks=new ArrayList<String>();
//		for (int i = 0; i < relVOs.length; i++) {
//			PzSourceRelationVO pzSourceRelationVO = relVOs[i];
//			if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP59)){
//				zcPks.add(pzSourceRelationVO.getSourcebillid());
//			}
//		}
//		if(zcPks.size()>0){
//			//查生成凭证的卡片
//			String sql="select * from ynt_assetcard where nvl(dr,0)=0 and pk_image_group is not null and "+SqlUtil.buildSqlForIn("pk_assetcard", zcPks.toArray(new String[0]));
//			List<AssetcardVO> accsetList=(List<AssetcardVO>)singleObjectBO.executeQuery(sql, new SQLParameter(), new BeanListProcessor(AssetcardVO.class));
//			if(accsetList==null||accsetList.size()==0)return;
//			Set<String> invoiceSet=new HashSet<String>();
//			Map<String, List<AssetcardVO>> cardMap=new HashMap<String, List<AssetcardVO>>();
//			//把卡片上的票找出来
//			for(int i=0;i<accsetList.size();i++){
//				String pk_invoice=accsetList.get(i).getPk_invoice();
//				invoiceSet.add(pk_invoice);
//				if(cardMap.containsKey(pk_invoice)){
//					cardMap.get(pk_invoice).add(accsetList.get(i));
//				}else{
//					List<AssetcardVO> tmpList=new ArrayList<AssetcardVO>();
//					tmpList.add(accsetList.get(i));
//					cardMap.put(pk_invoice, tmpList);
//				}
//			}
//			//查票
//			List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOsByWhere(null, invoiceSet.toArray(new String[0]),  tzpzHVO.getPeriod(), tzpzHVO.getPk_corp(),DZFBoolean.TRUE);
//			invoiceList=filterVersionInvoiceVO(invoiceList);
//			if(invoiceList.size()==0)return;
//			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
//			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
//			for(int i=0;i<invoiceList.size();i++){
//				String pk_invoice=invoiceList.get(i).getPk_invoice();
//				List<OcrInvoiceDetailVO> details=detailMap.get(pk_invoice);
//				invoiceList.get(i).setChildren(details.toArray(new OcrInvoiceDetailVO[0]));
//			}
//			List<OcrInvoiceVO> zzInvoiceList=new ArrayList<OcrInvoiceVO>();
//			//比较票的行和卡片状态
//			if(invoiceList.get(0).getVersion()==null||invoiceList.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
//			Map<String, BillCategoryVO> falseMap = getFalseMap(null, invoiceList.get(0).getPk_invoice());
//			Map<String, BillCategoryVO> trueMap = getTrueMap(falseMap, null, invoiceList.get(0).getPk_invoice());
//			for(int i=0;i<invoiceList.size();i++){
//				
//				String pk_invoice=invoiceList.get(i).getPk_invoice();
//				OcrInvoiceDetailVO[] details=(OcrInvoiceDetailVO[])invoiceList.get(i).getChildren();
//				
//				List<AssetcardVO> cards=cardMap.get(pk_invoice);
//				DZFBoolean isZz=DZFBoolean.TRUE;
//				for (int j = 0; j < details.length; j++) {
//					if(falseMap.get(details[j].getPk_billcategory()).getCategorycode().startsWith(ZncsConst.FLCODE_ZC)){
//						String pk_invoice_detail=details[j].getPk_invoice_detail();
//						for(int k=0;k<cards.size();k++){
//							//是资产的行生成的卡片已制证或者，这次生成的凭证的来源里有就是已制证
//							if((pk_invoice_detail.equals(cards.get(k).getPk_invoice_detail())&&!StringUtil.isEmpty(cards.get(k).getPk_voucher()))
//									||(pk_invoice_detail.equals(cards.get(k).getPk_invoice_detail())&&StringUtil.isEmpty(cards.get(k).getPk_voucher())&&zcPks.contains(cards.get(k).getPk_assetcard()))){
//							}else{
//								isZz=DZFBoolean.FALSE;
//								break;
//							}
//						}
//					}else{
//						//票上如果有非资产的管不了了
//						isZz=DZFBoolean.FALSE;
//						break;
//					}
//				}
//				if(DZFBoolean.TRUE.equals(isZz)){
//					zzInvoiceList.add(invoiceList.get(i));
//				}
//			}
//			//更新票上的分类
//			updateNewPk_billcategory(zzInvoiceList, detailMap, falseMap, trueMap);
//			//更新进项
//			for (int i = 0; i < zzInvoiceList.size(); i++) {
//				updateSourceModel(tzpzHVO, zzInvoiceList.get(i).getPk_invoice(), zzInvoiceList.get(i).getPk_image_group(),DZFBoolean.TRUE, falseMap, trueMap);
//			}
//		}
	}
	/**
	 * 得到发票map
	 * @param invoiceList
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, OcrInvoiceVO> getOcrInvoiceMap(List<OcrInvoiceVO> invoiceList)throws DZFWarpException{
		Map<String, OcrInvoiceVO> returnMap=new HashMap<String, OcrInvoiceVO>();
		if(invoiceList!=null&&invoiceList.size()>0){
			for(int i=0;i<invoiceList.size();i++){
				returnMap.put(invoiceList.get(i).getPk_invoice(), invoiceList.get(i));
			}
		}
		return returnMap;
	}
	private void updateSourceModel2(TzpzHVO tzpzHVO,String[] sourceids,String pk_tzpz,DZFBoolean flag,Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap,DZFBoolean isHaveBank)throws DZFWarpException{
		if(tzpzHVO.getSourcebilltype()==null||tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP110)){
			return;
		}
		String billtype=tzpzHVO.getSourcebilltype();//来源
		String sql=null;
		SQLParameter sp=new SQLParameter();
		if(DZFBoolean.TRUE.equals(flag)){
			if(billtype.equals(IBillTypeCode.HP85)){//银行
				sql="select * from ynt_bankstatement where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_bankstatement", sourceids);
				List<BankStatementVO2> list=(List<BankStatementVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BankStatementVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					falseMap = getFalseMap(falseMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					trueMap = getTrueMap(falseMap, trueMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					
					for (int i = 0; i < list.size(); i++) {
						String pk_category_old=(String)list.get(i).getAttributeValue("pk_model_h");
						BillCategoryVO falseVO=falseMap.get(pk_category_old);
						if(falseVO==null)return;
						BillCategoryVO trueVO=trueMap.get(falseVO.getFullcategoryname());
						String pk_category_new=trueVO.getPk_category();
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
//						list.get(i).setAttributeValue("busitypetempname", trueVO.getCategoryname());
						singleObjectBO.update(list.get(i), new String[]{"pk_model_h"});
					}
				}
			}else if(billtype.equals(IBillTypeCode.HP90)||billtype.equals(IBillTypeCode.HP75)){//销项
				sql="select * from ynt_vatsaleinvoice where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_vatsaleinvoice", sourceids);
				List<VATSaleInvoiceVO2> list=(List<VATSaleInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATSaleInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					falseMap = getFalseMap(falseMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					trueMap = getTrueMap(falseMap, trueMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					for (int i = 0; i < list.size(); i++) {
						String pk_category_old=(String)list.get(i).getAttributeValue("pk_model_h");
						BillCategoryVO falseVO=falseMap.get(pk_category_old);
						if(falseVO==null)return;
						BillCategoryVO trueVO=trueMap.get(falseVO.getFullcategoryname());
						String pk_category_new=trueVO.getPk_category();
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
//						list.get(i).setAttributeValue("busitypetempname", trueVO.getCategoryname());
						singleObjectBO.update(list.get(i), new String[]{"pk_model_h"});
					}
				}
			}else if(billtype.equals(IBillTypeCode.HP95)||billtype.equals(IBillTypeCode.HP59)||billtype.equals(IBillTypeCode.HP70)){//进项或资产
				sql="select * from ynt_vatincominvoice where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_vatincominvoice", sourceids);
				List<VATInComInvoiceVO2> list=(List<VATInComInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATInComInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					falseMap = getFalseMap(falseMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					trueMap = getTrueMap(falseMap, trueMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					for (int i = 0; i < list.size(); i++) {
						String pk_category_old=(String)list.get(i).getAttributeValue("pk_model_h");
						BillCategoryVO falseVO=falseMap.get(pk_category_old);
						if(falseVO==null)return;
						BillCategoryVO trueVO=trueMap.get(falseVO.getFullcategoryname());
						String pk_category_new=trueVO.getPk_category();
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
//						list.get(i).setAttributeValue("busitypetempname", trueVO.getCategoryname());
						singleObjectBO.update(list.get(i), new String[]{"pk_model_h"});
					}
				}
			}
		}else{
			sp.addParam(pk_tzpz);
			List<String> pk_categoryNewList=new ArrayList<String>();
			if(isHaveBank!=null&&DZFBoolean.TRUE.equals(isHaveBank)||billtype.equals(IBillTypeCode.HP85)){//银行
				sql="select * from ynt_bankstatement where nvl(dr,0)=0 and pk_tzpz_h=? and pk_image_group is null order by period";
				List<BankStatementVO2> list=(List<BankStatementVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BankStatementVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					falseMap = getFalseMap(falseMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					trueMap = getTrueMap(falseMap, trueMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					String period=tzpzHVO.getPeriod();
					for (int i = 0; i < list.size(); i++) {
						String pk_category_old=(String)list.get(i).getAttributeValue("pk_model_h");
						BillCategoryVO trueVO = trueMap.get(pk_category_old);
						if(trueVO==null){
							falseMap=null;
							trueMap=null;
							falseMap = getFalseMap(falseMap,pk_category_old);
							trueMap = getTrueMap(falseMap, trueMap, pk_category_old);
							trueVO = trueMap.get(pk_category_old);
						}
						period=trueVO.getPeriod();
						BillCategoryVO falseVO = getParentBillCategoryVO(falseMap, trueMap,trueVO);
						if(falseVO!=null){
							String pk_category_new = falseVO.getPk_category();
							list.get(i).setAttributeValue("pk_model_h", pk_category_new);
							pk_categoryNewList.add(pk_category_new);
							singleObjectBO.update(list.get(i), new String[]{"pk_model_h"});
						}
					}
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, period, tzpzHVO.getPk_corp());
					for (int i = 0; i < list.size(); i++) {
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
						singleObjectBO.update(list.get(i), new String[]{"busitypetempname"});
					}
				}
			}
			if(billtype.equals(IBillTypeCode.HP90)||billtype.equals(IBillTypeCode.HP75)){//销项
				sql="select * from ynt_vatsaleinvoice where nvl(dr,0)=0 and pk_tzpz_h=? and pk_image_group is null  order by period";
				List<VATSaleInvoiceVO2> list=(List<VATSaleInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATSaleInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					falseMap = getFalseMap(falseMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					trueMap = getTrueMap(falseMap, trueMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					String period=tzpzHVO.getPeriod();
					for (int i = 0; i < list.size(); i++) {
						String pk_category_old=(String)list.get(i).getAttributeValue("pk_model_h");
						BillCategoryVO trueVO = trueMap.get(pk_category_old);
						if(trueVO==null){
							falseMap=null;
							trueMap=null;
							falseMap = getFalseMap(falseMap,pk_category_old);
							trueMap = getTrueMap(falseMap, trueMap, pk_category_old);
							trueVO = trueMap.get(pk_category_old);
						}
						period=trueVO.getPeriod();
						BillCategoryVO falseVO = getParentBillCategoryVO(falseMap, trueMap,trueVO);
						if(falseVO!=null){
							String pk_category_new = falseVO.getPk_category();
							list.get(i).setAttributeValue("pk_model_h", pk_category_new);
							pk_categoryNewList.add(pk_category_new);
							singleObjectBO.update(list.get(i), new String[]{"pk_model_h"});
						}
					}
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, period, tzpzHVO.getPk_corp());
					for (int i = 0; i < list.size(); i++) {
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
						singleObjectBO.update(list.get(i), new String[]{"busitypetempname"});
					}
				}
			}
			if(billtype.equals(IBillTypeCode.HP95)||billtype.equals(IBillTypeCode.HP59)||billtype.equals(IBillTypeCode.HP70)){//进项
				sql="select * from ynt_vatincominvoice where nvl(dr,0)=0 and pk_tzpz_h=? and pk_image_group is null  order by period";
				List<VATInComInvoiceVO2> list=(List<VATInComInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATInComInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					if(list.get(0).getVersion()==null||list.get(0).getVersion().compareTo(new DZFDouble(1.0))<0)return;
					falseMap = getFalseMap(falseMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					trueMap = getTrueMap(falseMap, trueMap, (String)list.get(0).getAttributeValue("pk_model_h"));
					String period=tzpzHVO.getPeriod();
					for (int i = 0; i < list.size(); i++) {
						String pk_category_old=(String)list.get(i).getAttributeValue("pk_model_h");
						BillCategoryVO trueVO = trueMap.get(pk_category_old);
						if(trueVO==null){
							falseMap=null;
							trueMap=null;
							falseMap = getFalseMap(falseMap,pk_category_old);
							trueMap = getTrueMap(falseMap, trueMap, pk_category_old);
							trueVO = trueMap.get(pk_category_old);
						}
						period=trueVO.getPeriod();
						BillCategoryVO falseVO = getParentBillCategoryVO(falseMap, trueMap,trueVO);
						if(falseVO!=null){
							String pk_category_new = falseVO.getPk_category();
							list.get(i).setAttributeValue("pk_model_h", pk_category_new);
							pk_categoryNewList.add(pk_category_new);
							singleObjectBO.update(list.get(i), new String[]{"pk_model_h"});
						}
					}
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList,period, tzpzHVO.getPk_corp());
					for (int i = 0; i < list.size(); i++) {
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
						singleObjectBO.update(list.get(i), new String[]{"busitypetempname"});
					}
				}
			}
		}
	}
	
	/**
	 * 更新来源的业务类型(分类)
	 * @param tzpzHVO
	 * @throws DZFWarpException
	 */
	private void updateSourceModel(TzpzHVO tzpzHVO,String pk_invoice,String pk_image_group,DZFBoolean flag,Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap,DZFBoolean isHaveBank)throws DZFWarpException{
		if(tzpzHVO.getSourcebilltype()==null||tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP110)){
			return;
		}
		String billtype=tzpzHVO.getSourcebilltype();//来源
		String sql=null;
		SQLParameter sp=new SQLParameter();
		if(DZFBoolean.TRUE.equals(flag)){
			OcrInvoiceVO invoiceVO=(OcrInvoiceVO)singleObjectBO.queryByPrimaryKey(OcrInvoiceVO.class, pk_invoice);
			
			falseMap = getFalseMap(falseMap, invoiceVO.getPk_billcategory());
			trueMap = getTrueMap(falseMap, trueMap, invoiceVO.getPk_billcategory());
			
			sp.addParam(pk_image_group);
			sp.addParam(tzpzHVO.getPk_corp());
			List<String> pk_categoryNewList=new ArrayList<String>();
			if(billtype.equals(IBillTypeCode.HP85)){//银行
				sql="select * from ynt_bankstatement where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
				List<BankStatementVO2> list=(List<BankStatementVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BankStatementVO2.class));
				if(list!=null&&list.size()>0){
					BillCategoryVO trueVO=trueMap.get(invoiceVO.getPk_billcategory());
					String pk_category_new=null;
					String busitypetempname=null;
					if(trueVO.getCategorytype()==3||trueVO.getCategorytype()==4){
						pk_category_new=trueVO.getPk_parentcategory();
						busitypetempname=trueMap.get(trueVO.getPk_parentcategory()).getCategoryname();
					}else{
						pk_category_new=trueVO.getPk_category();
						busitypetempname=trueVO.getCategoryname();
					}
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
						pk_categoryNewList.add(pk_category_new);
					}
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, tzpzHVO.getPeriod(), tzpzHVO.getPk_corp());
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
					}
					singleObjectBO.updateAry(list.toArray(new BankStatementVO2[0]), new String[]{"pk_model_h","busitypetempname"});
				}
			}else if(billtype.equals(IBillTypeCode.HP90)||billtype.equals(IBillTypeCode.HP75)){//销项
				sql="select * from ynt_vatsaleinvoice where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
				List<VATSaleInvoiceVO2> list=(List<VATSaleInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATSaleInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					BillCategoryVO trueVO=trueMap.get(invoiceVO.getPk_billcategory());
					String pk_category_new=null;
					String busitypetempname=null;
					if(trueVO.getCategorytype()==3||trueVO.getCategorytype()==4){
						pk_category_new=trueVO.getPk_parentcategory();
						busitypetempname=trueMap.get(trueVO.getPk_parentcategory()).getCategoryname();
					}else{
						pk_category_new=trueVO.getPk_category();
						busitypetempname=trueVO.getCategoryname();
					}
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
						pk_categoryNewList.add(pk_category_new);
					}
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, tzpzHVO.getPeriod(), tzpzHVO.getPk_corp());
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
					}
					singleObjectBO.updateAry(list.toArray(new VATSaleInvoiceVO2[0]), new String[]{"pk_model_h","busitypetempname"});
				}
			}else if(billtype.equals(IBillTypeCode.HP95)||billtype.equals(IBillTypeCode.HP59)||billtype.equals(IBillTypeCode.HP70)){//进项或者资产
				sql="select * from ynt_vatincominvoice where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
				List<VATInComInvoiceVO2> list=(List<VATInComInvoiceVO2>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VATInComInvoiceVO2.class));
				if(list!=null&&list.size()>0){
					BillCategoryVO trueVO=trueMap.get(invoiceVO.getPk_billcategory());
					String pk_category_new=null;
					String busitypetempname=null;
					if(trueVO.getCategorytype()==3||trueVO.getCategorytype()==4){
						pk_category_new=trueVO.getPk_parentcategory();
						busitypetempname=trueMap.get(trueVO.getPk_parentcategory()).getCategoryname();
					}else{
						pk_category_new=trueVO.getPk_category();
						busitypetempname=trueVO.getCategoryname();
					}
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("pk_model_h", pk_category_new);
						pk_categoryNewList.add(pk_category_new);
					}
					Map<String, String> fullNameMap=queryCategoryFullName(pk_categoryNewList, tzpzHVO.getPeriod(), tzpzHVO.getPk_corp());
					for(int i=0;i<list.size();i++){
						list.get(i).setAttributeValue("busitypetempname", fullNameMap.get(list.get(i).getAttributeValue("pk_model_h")));
					}
					singleObjectBO.updateAry(list.toArray(new VATInComInvoiceVO2[0]), new String[]{"pk_model_h","busitypetempname"});
				}
			}
		}else{
			sp.addParam(pk_image_group);
			sp.addParam(tzpzHVO.getPk_corp());
			if(isHaveBank!=null&&DZFBoolean.TRUE.equals(isHaveBank)||billtype.equals(IBillTypeCode.HP85)){//银行
				sql="update ynt_bankstatement set pk_model_h=null,busitypetempname=null where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			}
			if(billtype.equals(IBillTypeCode.HP90)||billtype.equals(IBillTypeCode.HP75)){//销项
				sql="update ynt_vatsaleinvoice set pk_model_h=null,busitypetempname=null  where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			}
			if(billtype.equals(IBillTypeCode.HP95)||billtype.equals(IBillTypeCode.HP59)||billtype.equals(IBillTypeCode.HP70)){//进项
				sql="update ynt_vatincominvoice set pk_model_h=null,busitypetempname=null where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			}
			singleObjectBO.executeUpdate(sql, sp);
		}
		
	}
	
	/**
	 * 资产分类名称和主键map
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, String> queryZcflMap(String pk_corp,Set<String> zcflKey)throws DZFWarpException{
		Map<String, String> returnMap=new HashMap<String, String>();
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		String sql="select catename,pk_assetcategory from ynt_category where (pk_corp='000001' or pk_corp=?)  and nvl(dr,0)=0 and (";
		String[] zcflKeys=zcflKey.toArray(new String[0]);
		for (int i = 0; i < zcflKeys.length; i++) {
			String[] keys=zcflKeys[i].split(",");
			sql+=" (catename='"+keys[0]+"' and catecode like '"+keys[1]+"%') or";
		}
		sql=sql.substring(0, sql.length()-2)+" )";
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		for (int i = 0; i < list.size(); i++) {
			Object[] obj=list.get(i);
			returnMap.put(obj[0].toString(), obj[1].toString());
		}
		return returnMap;
	}
	
	private String getFalseCategory(Map<String, BillCategoryVO> trueMap,Map<String, BillCategoryVO> falseMap,String pk_billcategory){
		String fullname=trueMap.get(pk_billcategory).getFullcategoryname();
		if(falseMap.get(fullname)!=null){
			String pk_billcategory1=falseMap.get(fullname).getPk_category();
			return pk_billcategory1;
		}else{
			return falseMap.get(trueMap.get(trueMap.get(pk_billcategory).getPk_parentcategory()).getFullcategoryname()).getPk_category();
		}
	}
	
	/**
	 * 通过银行账号找主键
	 * @param pk_corp
	 * @param bankCode
	 * @return
	 * @throws DZFWarpException
	 */
	private String queryBankPrimaryKey(String pk_corp,String bankCode)throws DZFWarpException{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(bankCode);
		BankAccountVO[] vos=(BankAccountVO[])singleObjectBO.queryByCondition(BankAccountVO.class, "nvl(dr,0)=0 and pk_corp=? and bankaccount=?", sp);
		if(vos!=null&&vos.length!=0){
			return vos[0].getPk_bankaccount();
		}
		return null;
	}
	/**
	 * 保存出入库单
	 * @return
	 * @throws DZFWarpException
	 */
	private List<PzSourceRelationVO> saveIctrade(TzpzHVO tzpzHVO,List<OcrInvoiceVO> kcInvoice,String pk_image_group,String billtypecode,Map<String, CategorysetVO> categorysetMap,Map<String, BillCategoryVO> trueMap,Map<String, BillCategoryVO> falseMap)throws DZFWarpException{
		List<PzSourceRelationVO> returnList=new ArrayList<PzSourceRelationVO>();
		int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(tzpzHVO.getPk_corp(), "dzf009"));
		int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(tzpzHVO.getPk_corp(), "dzf010"));
		//1、取进项、销项发票
		Map<String, SuperVO> jxxxMap=queryJxxxfpByGroup(pk_image_group, billtypecode);
		for (int i = 0; i < kcInvoice.size(); i++) {
			IntradeHVO icHeadVO=new IntradeHVO();
			SuperVO headVO=jxxxMap.get(kcInvoice.get(i).getPk_invoice());//取出进项或者销项VO
			if(headVO==null)continue;
			String khmc=null;//客户或者供应商
			if(billtypecode.equals(IBillTypeCode.HP70)){//进项
				icHeadVO.setCbusitype(IcConst.CGTYPE);//采购入库
				icHeadVO.setSourcebilltype(IBillTypeCode.HP95);//来源进项票
				icHeadVO.setIszg(DZFBoolean.FALSE);// 不是暂估
				khmc=kcInvoice.get(i).getVsalename();//销售方
				if (!StringUtil.isEmpty(khmc)) {
					AuxiliaryAccountBVO custvo = matchCustomer(headVO, khmc, tzpzHVO.getPk_corp(), tzpzHVO.getCoperatorid(), AuxiliaryConstant.ITEM_SUPPLIER);
					icHeadVO.setPk_cust(custvo.getPk_auacount_b());//供应商
				}
			}else{
				icHeadVO.setCbusitype(IcConst.XSTYPE);//销售出库
				icHeadVO.setSourcebilltype(IBillTypeCode.HP90);//来源销项票
				khmc=kcInvoice.get(i).getVpurchname();//购买方
				if (!StringUtil.isEmpty(khmc)) {
					AuxiliaryAccountBVO custvo = matchCustomer(headVO, khmc, tzpzHVO.getPk_corp(),  tzpzHVO.getCoperatorid(), AuxiliaryConstant.ITEM_CUSTOMER);
					icHeadVO.setPk_cust(custvo.getPk_auacount_b());//客户
				}
			}
			if(StringUtil.isEmpty(kcInvoice.get(i).getDinvoicedate())){
				icHeadVO.setDinvdate(DateUtils.getPeriodEndDate(tzpzHVO.getPeriod()));//单据日期-开票日期
			}else{
				DZFDate trueDate=getInvoiceDate(kcInvoice.get(i));
				icHeadVO.setDinvdate(trueDate);//发票日期-开票日期
			}
			
			icHeadVO.setDbillid(null);// 单据编号-设置为空，调用入库接口会重新生成
			
			icHeadVO.setPk_corp(tzpzHVO.getPk_corp());
			icHeadVO.setIarristatus(1);//到货状态

			icHeadVO.setDbilldate(tzpzHVO.getDoperatedate());//单据日期-凭证日期
			if(icHeadVO.getDinvdate().after(icHeadVO.getDbilldate())){//如果开票日期晚于单据日期取单据日期
				icHeadVO.setDinvdate(icHeadVO.getDbilldate());
			}
			icHeadVO.setDinvid(kcInvoice.get(i).getVinvoiceno());//发票号码
			icHeadVO.setCreator(tzpzHVO.getCoperatorid());//制单人

			icHeadVO.setSourcebillid(headVO.getPrimaryKey());//单据来源ID
			
			icHeadVO.setPk_image_group((String)headVO.getAttributeValue("pk_image_group"));//图片组
			icHeadVO.setPk_image_library((String)headVO.getAttributeValue("pk_image_library"));//图片
			icHeadVO.setFp_style(tzpzHVO.getFp_style());// 1普票 2专票3未开票
			
			icHeadVO.setIsjz(DZFBoolean.TRUE);//转总账
			icHeadVO.setDjzdate(new DZFDate());
			icHeadVO.setIsinterface(DZFBoolean.TRUE);
			if(icHeadVO.getSourcebilltype().equals(IBillTypeCode.HP95)){
				icHeadVO.setIsrz(headVO.getAttributeValue("rzrj")==null?DZFBoolean.FALSE:DZFBoolean.TRUE);//是否认证
			}
			OcrInvoiceDetailVO[] details=(OcrInvoiceDetailVO[])kcInvoice.get(i).getChildren();
			CategorysetVO setVO=categorysetMap.get(getFalseCategory(trueMap, falseMap, details[0].getPk_billcategory()));//编辑目录
			icHeadVO.setIpayway((setVO==null||setVO.getSettlement()==0)?1:setVO.getSettlement()==1?0:2);// 付款方式
			if(icHeadVO.getIpayway()==2){
				String bankCode="";
				if(billtypecode.equals(IBillTypeCode.HP70)){//进项
					bankCode=kcInvoice.get(i).getVpuropenacc();
				}else{
					bankCode=kcInvoice.get(i).getVsaleopenacc();
				}
				if(!StringUtil.isEmpty(bankCode)){
					String[] str=bankCode.trim().split(" ");
					icHeadVO.setPk_bankaccount(queryBankPrimaryKey(tzpzHVO.getPk_corp(), str[1]));
				}
			}
			List<SuperVO> icList = new ArrayList<SuperVO>();
			SuperVO icbvo = null;
			for (int j = 0; j < details.length; j++) {
				
				if(StringUtil.isEmpty(details[j].getItemmny())||new DZFDouble(OcrUtil.turnMnyByCurrency(OcrUtil.turnMnyByCurrency(details[j].getItemmny()))).compareTo(DZFDouble.ZERO_DBL)==0){
					continue;
				}
				String spmc = OcrUtil.execInvname(details[j].getInvname());//商品名称

				if (StringUtil.isEmpty(spmc)) {
					continue;
				}

				InventoryVO inventoryvo = getInventoryVOByName(spmc, details[j].getInvtype(),details[j].getItemunit(),tzpzHVO.getPk_corp());
				if(inventoryvo==null){
					throw new BusinessException("存货未匹配！");
				}
				if(billtypecode.equals(IBillTypeCode.HP70)){
					icbvo = new IctradeinVO();
				}else{
					icbvo = new IntradeoutVO();
				}
				
				icbvo.setAttributeValue("pk_inventory", inventoryvo.getPk_inventory());
				icbvo.setAttributeValue("pk_subject", inventoryvo.getPk_subject());
				
				int calcmode=inventoryvo.getCalcmode();
				DZFDouble hsl=inventoryvo.getHsl();
				
				if(StringUtil.isEmpty(details[j].getItemamount())){
					if(hsl!=null){
						if(calcmode==1){
							icbvo.setAttributeValue("nnum", new DZFDouble(1).div(new DZFDouble(hsl)));
						}else{
							icbvo.setAttributeValue("nnum",new DZFDouble(1).multiply(new DZFDouble(hsl)));
						}
					}else{
						icbvo.setAttributeValue("nnum", new DZFDouble(1));
					}
				}else{
					if(hsl!=null){
						if(calcmode==1){
							icbvo.setAttributeValue("nnum", new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemamount())).div(new DZFDouble(hsl)).setScale(numPrecision, DZFDouble.ROUND_HALF_UP));//数量
						}else{
							icbvo.setAttributeValue("nnum", new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemamount())).multiply(new DZFDouble(hsl)).setScale(numPrecision, DZFDouble.ROUND_HALF_UP));//数量
						}
					}else{
						icbvo.setAttributeValue("nnum", new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemamount())).setScale(numPrecision, DZFDouble.ROUND_HALF_UP));
					}
				}
				icbvo.setAttributeValue("nymny", new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemmny())).setScale(2, DZFDouble.ROUND_HALF_UP));
				
				icbvo.setAttributeValue("nprice", ((DZFDouble)icbvo.getAttributeValue("nymny")).div((DZFDouble)icbvo.getAttributeValue("nnum")).setScale(pricePrecision, DZFDouble.ROUND_HALF_UP));
				
				icbvo.setAttributeValue("ntax",OcrUtil.getInvoiceSL(details[j].getItemtaxrate()).div(100));
				icbvo.setAttributeValue("ntaxmny", new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemtaxmny())));
				icbvo.setAttributeValue("ntotaltaxmny", new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(details[j].getItemtaxmny()))));
				icbvo.setAttributeValue("ncost", null);

				icList.add(icbvo);
			}
			PzSourceRelationVO relVO=new PzSourceRelationVO();
			String cbillid=null;
			if(billtypecode.equals(IBillTypeCode.HP70)){
				icHeadVO.setChildren(icList.toArray(new IctradeinVO[0]));
				cbillid=ic_purchinserv.save(icHeadVO, false).getPrimaryKey();// 保存
			}else{
				icHeadVO.setChildren(icList.toArray(new IntradeoutVO[0]));
				cbillid=ic_saleoutserv.saveSale(icHeadVO, false, false).getPrimaryKey();// 保存
			}
			relVO.setSourcebillid(cbillid);
			relVO.setSourcebilltype(billtypecode);
			relVO.setPk_corp(tzpzHVO.getPk_corp());
			returnList.add(relVO);
		}
		return returnList;
	}
	
	private AuxiliaryAccountBVO matchCustomer(SuperVO vo, String gfmc, String pk_corp, String userid,
			String pk_auacount_h) {
		AuxiliaryAccountBVO suppliervo = null;
		String payer=null;
		String name=null;
		String address=null;
		String bank=null;
		if(AuxiliaryConstant.ITEM_CUSTOMER.equals(pk_auacount_h)){
			payer = (String)vo.getAttributeValue("custidentno");// 购方识别号
			name = (String)vo.getAttributeValue("khmc");// 购方名称
			address = (String)vo.getAttributeValue("ghfdzdh");// 购方方地址电话
			bank = (String)vo.getAttributeValue("ghfyhzh");// 购方方开户账号
		}else{
			payer = (String)vo.getAttributeValue("xhfsbh");// 销方识别号
			name = (String)vo.getAttributeValue("xhfmc");// 销货方名称
			address = (String)vo.getAttributeValue("xhfdzdh");// 销货方地址电话
			bank = (String)vo.getAttributeValue("xhfyhzh()");// 销货方开户账号
		}
		suppliervo = ocr_atuomatch.getAuxiliaryAccountBVOByName(name, pk_corp, pk_auacount_h);

		if (suppliervo == null) {
			if (!StringUtil.isEmpty(payer) && !"000000000000000".equalsIgnoreCase(payer)) {
				suppliervo = ocr_atuomatch.getAuxiliaryAccountBVOByTaxNo(payer, pk_corp, pk_auacount_h);
			}
		}

		if (suppliervo == null && !StringUtil.isEmpty(name)) {
			suppliervo = new AuxiliaryAccountBVO();
			suppliervo.setCode(yntBoPubUtil.getFZHsCode(pk_corp, pk_auacount_h));
			suppliervo.setName(gfmc);
			suppliervo.setTaxpayer(payer);
			suppliervo.setDr(0);
			suppliervo.setPk_corp(pk_corp);
			suppliervo.setPk_auacount_h(pk_auacount_h);
			setFzInfo(suppliervo, address, bank);
			suppliervo = gl_fzhsserv.saveB(suppliervo);
		}
		return suppliervo;
	}
	private void setFzInfo(AuxiliaryAccountBVO bvo, String address, String bank) throws DZFWarpException {
		if (!StringUtil.isEmpty(address)) {
			String ss = getLastHanzi(address);
			if (!StringUtil.isEmpty(ss)) {
				String ss1 = address.substring(ss.length(), address.length());
				bvo.setAddress(ss.trim());
				if (!StringUtil.isEmpty(ss1))
					bvo.setPhone_num(ss1.trim());
			}
		}

		if (!StringUtil.isEmpty(bank)) {
			String ss = getLastHanzi(bank);
			if (!StringUtil.isEmpty(ss)) {
				String ss1 = bank.substring(ss.length(), bank.length());
				bvo.setBank(ss.trim());
				if (!StringUtil.isEmpty(ss1))
					bvo.setAccount_num(ss1.trim());
			}
		}
	}
	private String getLastHanzi(String string) {

		if (string.indexOf(" ") > -1) {
			string = string.split(" ").length > 0 ? string.split(" ")[0] : null;
		} else {
			String reg1 = "[\\u4e00-\\u9fa5]";
			Pattern p = Pattern.compile(reg1);
			Matcher m = p.matcher(string);

			String lasrChar = null;
			while (m.find()) {
				lasrChar = m.group();
			}
			if (!StringUtil.isEmpty(lasrChar)) {
				int last = string.lastIndexOf(lasrChar);
				string = string.substring(0, last + 1);
			}
		}
		return string;
	}
	/**
	 * 通过imagegroup去进项或者销项发票
	 * @param pk_image_group
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, SuperVO> queryJxxxfpByGroup(String pk_image_group,String billtypecode)throws DZFWarpException{
		Map<String, SuperVO> returnMap=new HashMap<String, SuperVO>();
		String tableName=billtypecode.equals(IBillTypeCode.HP70)?"ynt_vatincominvoice":"ynt_vatsaleinvoice";
		String bodyTableName=billtypecode.equals(IBillTypeCode.HP70)?"ynt_vatincominvoice_b":"ynt_vatsaleinvoice_b";
		String pk_head=billtypecode.equals(IBillTypeCode.HP70)?"pk_vatincominvoice":"pk_vatsaleinvoice";
		Class className=billtypecode.equals(IBillTypeCode.HP70)?VATInComInvoiceVO2.class:VATSaleInvoiceVO2.class;
		Class bodyClassName=billtypecode.equals(IBillTypeCode.HP70)?VATInComInvoiceBVO2.class:VATSaleInvoiceBVO2.class;
		String sql="select * from "+tableName+" where nvl(dr,0)=0 and pk_image_group=? ";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_image_group);
		List<SuperVO> headList=(List<SuperVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(className));
		if(headList!=null&&headList.size()>0){
			List<String> headKeys=new ArrayList<String>();
			for(int i=0;i<headList.size();i++){
				headKeys.add((String)headList.get(i).getAttributeValue(pk_head));
			}
			sql="select * from "+bodyTableName+" where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn(pk_head, headKeys.toArray(new String[0]));
			List<SuperVO> bodyList=(List<SuperVO>)singleObjectBO.executeQuery(sql, new SQLParameter(), new BeanListProcessor(bodyClassName));
			Map<String, List<SuperVO>> bodyMap = DZfcommonTools.hashlizeObject(bodyList, new String[] { pk_head });
			//6、装表体
			for(int i=0;i<headList.size();i++){
				SuperVO headVO=headList.get(i);
				headVO.setChildren(bodyMap.get(headVO.getPrimaryKey()).toArray(new SuperVO[0]));
				returnMap.put(headVO.getAttributeValue("vdef13").toString(), headVO);
			}
		}
		return returnMap;
	}
	
	/**
	 * 如果资产卡片的结算科目也启用了辅助，设置辅助值
	 * @param tzpzHVO
	 * @param card
	 * @throws DZFWarpException
	 */
	private void setZckpFzhs(TzpzHVO tzpzHVO,AssetcardVO card)throws DZFWarpException{
		String pk_jskm=card.getPk_jskm();
		TzpzBVO[] tzpzBVOs=(TzpzBVO[])tzpzHVO.getChildren();
		for (int i = 0; i < tzpzBVOs.length; i++) {
			if(tzpzBVOs[i].getPk_accsubj().equals(pk_jskm)){
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx1())){
					card.setJsfzhsx1(tzpzBVOs[i].getFzhsx1());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx2())){
					card.setJsfzhsx2(tzpzBVOs[i].getFzhsx2());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx3())){
					card.setJsfzhsx3(tzpzBVOs[i].getFzhsx3());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx4())){
					card.setJsfzhsx4(tzpzBVOs[i].getFzhsx4());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx5())){
					card.setJsfzhsx5(tzpzBVOs[i].getFzhsx5());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx6())){
					card.setJsfzhsx6(tzpzBVOs[i].getFzhsx6());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx7())){
					card.setJsfzhsx7(tzpzBVOs[i].getFzhsx7());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx8())){
					card.setJsfzhsx8(tzpzBVOs[i].getFzhsx8());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx9())){
					card.setJsfzhsx9(tzpzBVOs[i].getFzhsx9());
				}
				if(!StringUtil.isEmpty(tzpzBVOs[i].getFzhsx10())){
					card.setJsfzhsx10(tzpzBVOs[i].getFzhsx10());
				}
				break;
			}
		}
	}
	
	private String queryLibraryID(String pk_image_group)throws DZFWarpException{
		String sql="select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group=? ";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_image_group);
		String pk_image_library=null;
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		if(list!=null&&list.size()>0){
			Object[] obj=list.get(0);
			pk_image_library=obj[0].toString();
		}
		return pk_image_library;
	}
	/**
	 * 保存资产卡片
	 * @return
	 * @throws DZFWarpException
	 */
	private List<PzSourceRelationVO> saveZckp(TzpzHVO tzpzHVO,Set<String> zcflKey,List<OcrInvoiceDetailVO> zckpList,String pk_image_group,Map<String, BillCategoryVO> trueMap,Map<String, CategorysetVO> categorysetMap,CorpVO corp,Map<String, OcrInvoiceVO> invoiceMap,Map<String, BillCategoryVO> falseMap)throws DZFWarpException{
		List<PzSourceRelationVO> returnList=new ArrayList<PzSourceRelationVO>();
		//0、获得当前公司行业编码，关键字规则用
		String tradeCode=getCurTradeCode(corp.getIndustry());
		//1、查公司参数
		List<Object> paramList=queryParams(corp.getPk_corp());
		//2、得到资产类别名称和类别主键关系map
		Map<String, String> zcflMap=queryZcflMap(tzpzHVO.getPk_corp(), zcflKey);
		//3、取公司分类关系map
		Map<String, Object[]> categoryMap=queryCategoryMap(tzpzHVO.getPeriod(), corp.getPk_corp(),null,DZFBoolean.FALSE);
		//4、查集团科目
		Map<String, String> jituanSubMap=queryJituanSubj(corp.getCorptype());
		//5、编码规则
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(corp.getPk_corp());
		YntCpaccountVO[] accVOs=accountService.queryByPk(corp.getPk_corp());
		//6、得到所有表体行的公司分类、对应base分类、以及对应map
		Set<String> baseKeySet=new HashSet<String>();//这个变量要去数据库查入账规则和关键字规则
		Map<String, String> categoryAndBaseMap=new HashMap<String, String>();//记录公司分类和对应base
		for (int i = 0; i < zckpList.size(); i++) {
			String pk_billcategory=zckpList.get(i).getPk_billcategory();
			String pk_basecategory=getCategoryBaseKey(getFalseCategory(trueMap, falseMap, pk_billcategory),categoryMap);
			categoryAndBaseMap.put(pk_billcategory, pk_basecategory);
			baseKeySet.add(pk_basecategory);
		}
		Set<String> zyFzhsList=new HashSet<String>();
		queryFzhsBodyMap(corp.getPk_corp(),zyFzhsList);
		//7、查分类入账规则
		Map<String, List<AccsetVO>> accsetMap=queryAccsetVOMap(corp.getCorptype(),baseKeySet.toArray(new String[0]));
		//8、查关键字入账规则
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=queryAccsetKeywordBVO2Map(corp.getCorptype(),baseKeySet.toArray(new String[0]));
		String pk_image_library=queryLibraryID(tzpzHVO.getPk_image_group());
		for (int i = 0; i < zckpList.size(); i++) {
			OcrInvoiceDetailVO detailVO=zckpList.get(i);
			int len=1;
			try{
				len=new DZFDouble(detailVO.getItemamount()).setScale(0, DZFDouble.ROUND_HALF_UP).intValue();
			}catch(Exception ex){
			}
			if(len==0){
				len=1;
			}
			AssetcardVO card=new AssetcardVO();
			String pk_category=detailVO.getPk_billcategory();//新分类主键
			card.setPk_voucher(tzpzHVO.getPk_tzpz_h());//凭证主键
			card.setSourcetype(IBillTypeCode.HP110);//来源
			card.setPk_image_group(tzpzHVO.getPk_image_group());//图片组主键
			card.setPk_image_library(pk_image_library);//图片主键
			card.setFp_hm(invoiceMap.get(detailVO.getPk_invoice()).getVinvoiceno());//发票号码
			card.setPk_invoice(detailVO.getPk_invoice());//票主键
			card.setPk_invoice_detail(detailVO.getPk_invoice_detail());//票体主键
			card.setAccountdate(tzpzHVO.getDoperatedate());//日期
			card.setPeriod(tzpzHVO.getDoperatedate());//期间
			card.setAssetname(OcrUtil.execInvname(detailVO.getInvname()));//资产名称
			String categoryName=trueMap.get(pk_category).getCategoryname();//分类名称
			CategorysetVO setVO=categorysetMap.get(getFalseCategory(trueMap, falseMap, pk_category));//编辑目录
			card.setAssetcategory(zcflMap.get(categoryName));//资产类别主键
			card.setUselimit(categorysetMap.get(getFalseCategory(trueMap, falseMap, pk_category)).getDepreciationmonth());//使用年限
			//分类入账规则
			AccsetVO accsetVO=getAccsetVOByCategory(getFalseCategory(trueMap, falseMap, pk_category), accsetMap, categoryMap, corp,invoiceMap.get(detailVO.getPk_invoice()),zyFzhsList);
			//关键字入账规则
			AccsetKeywordBVO2 accsetKeywordBVO2=getAccsetKeywordBVO2ByCategory(accsetVO.getPk_basecategory(), accsetKeywordBVO2Map, categoryMap, corp,invoiceMap.get(detailVO.getPk_invoice()),detailVO,tradeCode);//有可能是null
			Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corp.getPk_corp());
			String unitName=corp.getUnitname();
			String saleName=StringUtil.isEmpty(invoiceMap.get(detailVO.getPk_invoice()).getVsalename())?"":invoiceMap.get(detailVO.getPk_invoice()).getVsalename();
			if(StringUtil.isEmpty(setVO.getPk_accsubj())){//从编辑目录找
				card.setPk_zckm(getPk_accsubjByRule(0, accsetVO, accsetKeywordBVO2, corp, setVO, paramList,jituanSubMap,newrule,accVOs,null,null,null,null,null,null,null));
			}else{
				card.setPk_zckm(setVO.getPk_accsubj());//资产科目
			}
			//找下级科目开始
			YntCpaccountVO accountVO1=accountMap.get(card.getPk_zckm());//科目
			//往来科目匹配下级科目
			if(DZFBoolean.FALSE.equals(accountVO1.getIsleaf())&&isMatchAccountCode(accountVO1,corp)){
				String khGysName=null;
				if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)&&!StringUtil.isEmpty(invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())){
				}else{
					if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
						khGysName=invoiceMap.get(detailVO.getPk_invoice()).getVpurchname();
					}else if(!StringUtil.isEmpty(invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())){
						khGysName=invoiceMap.get(detailVO.getPk_invoice()).getVsalename();
					}
				}
				if(!StringUtil.isEmpty(khGysName)){
					// 供应商 客户类科目（应付账款 应收账款）
					YntCpaccountVO account1 = matchAccount(corp.getPk_corp(), accountVO1, khGysName, accVOs, newrule);
					if (account1 != null) {
						accountVO1 = account1;
						card.setPk_zckm(accountVO1.getPk_corp_account());
					}
				}
			}
			//如果不是下级科目，找第一个下级科目
			if(!DZFBoolean.TRUE.equals(accountVO1.getIsleaf())){
				for (int k = 0; k < accVOs.length; k++) {
					YntCpaccountVO yntCpaccountVO = accVOs[k];
					if((yntCpaccountVO.getBisseal()==null||DZFBoolean.FALSE.equals(yntCpaccountVO.getBisseal()))&&yntCpaccountVO.getAccountcode().startsWith(accountVO1.getAccountcode())&&DZFBoolean.TRUE.equals(yntCpaccountVO.getIsleaf())){
						accountVO1=yntCpaccountVO;
						card.setPk_zckm(accountVO1.getPk_corp_account());
					}
				}
			}
			if(StringUtil.isEmpty(setVO.getPk_settlementaccsubj())){
				card.setPk_jskm(getPk_accsubjByRule(1, accsetVO, accsetKeywordBVO2, corp, setVO, paramList,jituanSubMap,newrule,accVOs,null,null,null,null,null,null,null));
			}else{
				card.setPk_jskm(setVO.getPk_settlementaccsubj());//结算科目
			}
			//找下级科目开始
			YntCpaccountVO accountVO2=accountMap.get(card.getPk_jskm());//科目
			//往来科目匹配下级科目
			if(DZFBoolean.FALSE.equals(accountVO2.getIsleaf())&&isMatchAccountCode(accountVO2,corp)){
				String khGysName=null;
				if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)&&!StringUtil.isEmpty(invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())){
				}else{
					if(!StringUtil.isEmpty(saleName)&&OcrUtil.isSameCompany(unitName,saleName)){
						khGysName=invoiceMap.get(detailVO.getPk_invoice()).getVpurchname();
					}else if(!StringUtil.isEmpty(invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())&&OcrUtil.isSameCompany(unitName,invoiceMap.get(detailVO.getPk_invoice()).getVpurchname())){
						khGysName=invoiceMap.get(detailVO.getPk_invoice()).getVsalename();
					}
				}
				if(!StringUtil.isEmpty(khGysName)){
					// 供应商 客户类科目（应付账款 应收账款）
					YntCpaccountVO account1 = matchAccount(corp.getPk_corp(), accountVO2, khGysName, accVOs, newrule);
					if (account1 != null) {
						accountVO2 = account1;
						card.setPk_jskm(accountVO2.getPk_corp_account());
					}
				}
			}
			//如果不是下级科目，找第一个下级科目
			if(!DZFBoolean.TRUE.equals(accountVO2.getIsleaf())){
				for (int k = 0; k < accVOs.length; k++) {
					YntCpaccountVO yntCpaccountVO = accVOs[k];
					if((yntCpaccountVO.getBisseal()==null||DZFBoolean.FALSE.equals(yntCpaccountVO.getBisseal()))&&yntCpaccountVO.getAccountcode().startsWith(accountVO2.getAccountcode())&&DZFBoolean.TRUE.equals(yntCpaccountVO.getIsleaf())){
						accountVO2=yntCpaccountVO;
						card.setPk_jskm(accountVO2.getPk_corp_account());
					}
				}
			}
			setZckpFzhs(tzpzHVO, card);
			OcrInvoiceVO invoiceVO=invoiceMap.get(detailVO.getPk_invoice());
			String istate=invoiceVO.getIstate();//发票大类
			String invoicetype=invoiceVO.getInvoicetype();//发票小类
			if(!"小规模纳税人".equals(corp.getChargedeptname()) &&!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_3)&&!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1)){
				card.setAssetmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())));////资产原值
				card.setNjxsf(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));//进行税额
			}else{
				//一般
				card.setAssetmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())).add(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))));//金额+税额
			}
			card.setNsl(OcrUtil.getInvoiceSL(detailVO.getItemtaxrate()).div(100));//税率
			if(setVO.getSalvagerate()!=null){
				card.setSalvageratio(setVO.getSalvagerate().div(100));//残值率
			}else{
				card.setSalvageratio(DZFDouble.ZERO_DBL);//残值率
			}
			card.setZjtype(0);//折旧类型
			List<AssetcardVO> assetcardList=new ArrayList<AssetcardVO>();
			if(len>1){
				DZFDouble assetmny=card.getAssetmny().div(len).setScale(2, DZFDouble.ROUND_HALF_UP);
				DZFDouble njxsf=null;
				if(card.getNjxsf()!=null){
					njxsf=card.getNjxsf().div(len).setScale(2, DZFDouble.ROUND_HALF_UP);
				}
				for(int j=1;j<len;j++){
					AssetcardVO card1=(AssetcardVO)card.clone();
					card1.setAssetmny(assetmny);
					card1.setNjxsf(njxsf);
					assetcardList.add(card1);
				}
				card.setAssetmny(card.getAssetmny().sub(assetmny.multiply(len-1)));
				if(card.getNjxsf()!=null){
					card.setNjxsf(card.getNjxsf().sub(njxsf.multiply(len-1)));
				}
			}
			assetcardList.add(card);
			for(int j=0;j<assetcardList.size();j++){
				//生成卡片
				PzSourceRelationVO relVO=new PzSourceRelationVO();
				relVO.setSourcebillid(iKpglService.saveCard(corp.getPk_corp(), assetcardList.get(j)).getPk_assetcard());
				relVO.setSourcebilltype(IBillTypeCode.HP59);
				relVO.setPk_corp(tzpzHVO.getPk_corp());
				returnList.add(relVO);
			}
			
		}
		return returnList;
	}
	
	/**
	 * 如果公司分类没有对应base会往上级找
	 * @return
	 * @throws DZFWarpException
	 */
	private String getCategoryBaseKey(String pk_category,Map<String, Object[]> categoryMap) throws DZFWarpException{
		Object[] obj=categoryMap.get(pk_category);//分类详情
		String pk_basecategory=obj[1]==null?null:obj[1].toString();
		if(StringUtil.isEmpty(pk_basecategory)){//自己创建的目录，往上找
			if(!StringUtil.isEmpty((String)obj[2])){
				return getCategoryBaseKey(obj[2].toString(), categoryMap);
			}else{
				throw new BusinessException("没有找到分类的上级分类");
			}
		}else{
			return pk_basecategory;
		}
	}
	/**
	 * 分类编码转资产类别编码
	 * @param categorycode
	 * @return
	 */
	private String getZcLbCode(String categorycode) {
		String parentCode = categorycode.substring(0, 6);
		switch (parentCode) {
		//普通固定资产
		case "151010":
			return "0101";
		case "151011":
			return "0102";
		case "151012":
			return "0103";
		case "151013":
			return "0104";
		case "151014":
			return "0105";
		case "151015":
			return "0106";
		//民间固定资产
		case "151020":
			return "0301";
		case "151021":
			return "0302";
		case "151022":
			return "0303";
		case "151023":
			return "0304";
		case "151024":
			return "0305";
		case "151025":
			return "0306";
		case "151026":
			return "0307";
		//无形资产
		case "151110":
			return "0201";
		case "151111":
			return "0202";
		case "151112":
			return "0203";
		case "151113":
			return "0204";
		case "151114":
			return "0205";
		case "151115":
			return "0206";
		case "151116":
			return "0207";
		//长期待摊费用
		case "151210":
			return "0401";
		case "151211":
			return "0402";
		case "151212":
			return "0403";
		case "151213":
			return "0404";
		case "151214":
			return "0405";
		default:
			return null;
		}
	}
	/**
	 * 创建凭证来源关系
	 * @return
	 * @throws DZFWarpException
	 */
	private PzSourceRelationVO[] buildPzSourceRelationVOs(TzpzHVO tzpzHVO,List<OcrInvoiceVO> invoiceList,Map<String, BillCategoryVO> trueMap,Map<String, CategorysetVO> categorysetMap,CorpVO corp,Map<String, BillCategoryVO> falseMap)throws DZFWarpException{
		
		falseMap = getFalseMap(falseMap, invoiceList.get(0).getPk_billcategory());
		trueMap = getTrueMap(falseMap, trueMap, invoiceList.get(0).getPk_billcategory());
		
		List<PzSourceRelationVO> returnList=new ArrayList<PzSourceRelationVO>();
		String billtype=tzpzHVO.getSourcebilltype();//来源
		String pk_image_group=tzpzHVO.getPk_image_group();
		String sql=null;
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_image_group);
		sp.addParam(tzpzHVO.getPk_corp());
		if(billtype.equals(IBillTypeCode.HP85)){//银行
			sql="select pk_bankstatement from ynt_bankstatement where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
		}else if(billtype.equals(IBillTypeCode.HP90)||billtype.equals(IBillTypeCode.HP75)){//销项或销售出库
			sql="select pk_vatsaleinvoice from ynt_vatsaleinvoice where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			if(IcCostStyle.IC_ON.equals(corp.getBbuildic())&&billtype.equals(IBillTypeCode.HP75)){
				List<OcrInvoiceVO> kcInvoice=new ArrayList<OcrInvoiceVO>();//需要生成库存的票
				Map<String, OcrInvoiceVO> invoiceMap=new HashMap<String, OcrInvoiceVO>();//票的主键和票的map
				//把票上不需要生成库存的行，去掉
				for(int i=0;i<invoiceList.size();i++){
					OcrInvoiceVO invoiceVO=invoiceList.get(i);
					OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])invoiceVO.getChildren();
					List<OcrInvoiceDetailVO> kcList=new ArrayList<OcrInvoiceDetailVO>();
					for (int j = 0; j < detailVOs.length; j++) {
						String categoryCode=trueMap.get(detailVOs[j].getPk_billcategory()).getCategorycode();//分类编码
						if(categoryCode.startsWith("101015")||categoryCode.startsWith("101110")){
							kcList.add(detailVOs[j]);
							if(!invoiceMap.containsKey(invoiceVO.getPk_invoice())){
								invoiceMap.put(invoiceVO.getPk_invoice(), invoiceVO);
							}
						}
					}
					invoiceVO.setChildren(kcList.toArray(new OcrInvoiceDetailVO[0]));
					if(kcList.size()>0){
						kcInvoice.add(invoiceVO);
					}
				}
				if(kcInvoice.size()>0){
					returnList.addAll(saveIctrade(tzpzHVO,kcInvoice,pk_image_group,IBillTypeCode.HP75,categorysetMap,trueMap,falseMap));
				}
			}
			billtype=IBillTypeCode.HP90;
		}else if(billtype.equals(IBillTypeCode.HP95)||billtype.equals(IBillTypeCode.HP59)||billtype.equals(IBillTypeCode.HP70)){//进项或资产或入库
			sql="select pk_vatincominvoice from ynt_vatincominvoice where pk_image_group = ? and pk_corp = ? and nvl(dr,0) = 0";
			if(IcCostStyle.IC_ON.equals(corp.getBbuildic())&&billtype.equals(IBillTypeCode.HP70)){
				List<OcrInvoiceVO> kcInvoice=new ArrayList<OcrInvoiceVO>();//需要生成库存的票
				Map<String, OcrInvoiceVO> invoiceMap=new HashMap<String, OcrInvoiceVO>();//票的主键和票的map
				//把票上不需要生成库存的行，去掉
				for(int i=0;i<invoiceList.size();i++){
					OcrInvoiceVO invoiceVO=invoiceList.get(i);
					OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])invoiceVO.getChildren();
					List<OcrInvoiceDetailVO> kcList=new ArrayList<OcrInvoiceDetailVO>();
					for (int j = 0; j < detailVOs.length; j++) {
						String categoryCode=trueMap.get(detailVOs[j].getPk_billcategory()).getCategorycode();//分类编码
						if(categoryCode.startsWith("11")){
							kcList.add(detailVOs[j]);
							if(!invoiceMap.containsKey(invoiceVO.getPk_invoice())){
								invoiceMap.put(invoiceVO.getPk_invoice(), invoiceVO);
							}
						}
					}
					invoiceVO.setChildren(kcList.toArray(new OcrInvoiceDetailVO[0]));
					if(kcList.size()>0){
						kcInvoice.add(invoiceVO);
					}
				}
				if(kcInvoice.size()>0){
					returnList.addAll(saveIctrade(tzpzHVO,kcInvoice,pk_image_group,IBillTypeCode.HP70,categorysetMap,trueMap,falseMap));
				}
			}else if(DZFBoolean.TRUE.equals(corp.getHoldflag())&&billtype.equals(IBillTypeCode.HP59)){
				Set<String> zcflKey=new HashSet<String>();//需要查资产分类的数据
				List<OcrInvoiceDetailVO> zckpList=new ArrayList<OcrInvoiceDetailVO>();//要生成资产卡片的发票体
				Map<String, OcrInvoiceVO> invoiceMap=new HashMap<String, OcrInvoiceVO>();//票的主键和票的map
				for(int i=0;i<invoiceList.size();i++){
					OcrInvoiceVO invoiceVO=invoiceList.get(i);
					OcrInvoiceDetailVO[] detailVOs=(OcrInvoiceDetailVO[])invoiceVO.getChildren();
					for (int j = 0; j < detailVOs.length; j++) {
						String categoryCode=trueMap.get(detailVOs[j].getPk_billcategory()).getCategorycode();//分类编码
						String categoryName=trueMap.get(detailVOs[j].getPk_billcategory()).getCategoryname();//分类名称
						if(categoryCode.startsWith(ZncsConst.FLCODE_ZC)){
							String zcLbCode=getZcLbCode(categoryCode);//资产类别编码
							zcflKey.add(categoryName+","+zcLbCode);
							zckpList.add(detailVOs[j]);
							if(!invoiceMap.containsKey(invoiceVO.getPk_invoice())){
								invoiceMap.put(invoiceVO.getPk_invoice(), invoiceVO);
							}
						}
					}
				}
				if(zckpList.size()>0){
					returnList.addAll(saveZckp(tzpzHVO,zcflKey, zckpList, pk_image_group,trueMap,categorysetMap,corp,invoiceMap,falseMap));
				}
			}
			billtype=IBillTypeCode.HP95;
		}else{
			return returnList.toArray(new PzSourceRelationVO[0]);
		}
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		if(list!=null&&list.size()>0){
			for (int i = 0; i < list.size(); i++) {
				PzSourceRelationVO relVO=new PzSourceRelationVO();
				relVO.setSourcebillid(list.get(i)[0].toString());
				relVO.setSourcebilltype(billtype);
				relVO.setPk_corp(tzpzHVO.getPk_corp());
				returnList.add(relVO);
			}
//			singleObjectBO.insertVOArr(tzpzHVO.getPk_corp(), returnList.toArray(new PzSourceRelationVO[0]));
		}
		return returnList.toArray(new PzSourceRelationVO[0]);
	}
	
	@Override
	public void saveVoucherBefore(TzpzHVO tzpzHVO) throws DZFWarpException {
		if(tzpzHVO == null){
			return ;
		}
		List<TzpzHVO> tzpzHVOs=new ArrayList<TzpzHVO>();
		tzpzHVOs.add(tzpzHVO);
		saveVouchersBefore(tzpzHVOs);
	}
	/**
	 * 设置新的分类id到票据上
	 * @throws DZFWarpException
	 */
	private void updateNewPk_billcategory(List<OcrInvoiceVO> invoiceList,Map<String, List<OcrInvoiceDetailVO>> detailMap, Map<String, BillCategoryVO> falseMap, Map<String, BillCategoryVO> trueMap)throws DZFWarpException{
		
		falseMap = getFalseMap(falseMap, invoiceList.get(0).getPk_billcategory());
		trueMap = getTrueMap(falseMap, trueMap, invoiceList.get(0).getPk_billcategory());
		
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			String pk_category_old=invoiceVO.getPk_billcategory();//isaccount=N的分类id

			BillCategoryVO falseVO= falseMap.get(pk_category_old);
			if(falseVO==null){
				falseMap=null;
				trueMap=null;
				falseMap = getFalseMap(falseMap,pk_category_old);
				trueMap = getTrueMap(falseMap, trueMap, pk_category_old);
				falseVO= falseMap.get(pk_category_old);
			}
			BillCategoryVO trueVO= trueMap.get(falseVO.getFullcategoryname());
			if(trueVO==null){
				trueVO=trueMap.get(falseVO.getFullcategoryname().substring(0, (falseVO.getFullcategoryname().lastIndexOf("~")+1))+"其他");
			}
			String pk_category_new=trueVO.getPk_category();//isaccount=Y的分类id
			invoiceVO.setPk_billcategory(pk_category_new);
			OcrInvoiceDetailVO[] detailVOs=detailMap.get(invoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]);
			for (int j = 0; j < detailVOs.length; j++) {
				OcrInvoiceDetailVO ocrInvoiceDetailVO = detailVOs[j];
				if(pk_category_old.equals(ocrInvoiceDetailVO.getPk_billcategory())){
					ocrInvoiceDetailVO.setPk_billcategory(pk_category_new);//表体的也换成新的
				}else{
					String pk_category_old1=ocrInvoiceDetailVO.getPk_billcategory();//isaccount=N的分类id
					if(StringUtil.isEmpty(pk_category_old1))continue;
					BillCategoryVO falseVO1=falseMap.get(pk_category_old1);
					BillCategoryVO trueVO1=trueMap.get(falseVO1.getFullcategoryname());
					if(trueVO1==null){
						trueVO1=trueMap.get(falseVO1.getFullcategoryname().substring(0, (falseVO1.getFullcategoryname().lastIndexOf("~")+1))+"其他");
					}
					String pk_category_new1=trueVO1.getPk_category();//isaccount=Y的分类id
					ocrInvoiceDetailVO.setPk_billcategory(pk_category_new1);//表体的也换成新的
				}
			}
			invoiceVO.setChildren(detailVOs);
			singleObjectBO.update(invoiceVO, new String[]{"pk_billcategory"});
			singleObjectBO.updateAry(detailVOs, new String[]{"pk_billcategory"});
		}
	}
	private List<String> getInvoiceKey(List<TzpzHVO> tzpzHVOs)throws DZFWarpException{
		List<String> returnList=new ArrayList<String>();
		for(int i=0;i<tzpzHVOs.size();i++){
			String userObject=tzpzHVOs.get(i).getUserObject().toString();//这里存的是票的ids
			String[] userObjects=userObject.split(",");
			for (int j = 0; j < userObjects.length; j++) {
				returnList.add(userObjects[j]);
			}
		}
		return returnList;
	}

	/**
	 * 编码map转主键map
	 * @param falseMap
	 * @param trueMap
	 * @throws DZFWarpException
	 */
	private void turnBillCategoryMap(Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap)throws DZFWarpException{
		Iterator<String> itor=falseMap.keySet().iterator();
		List<BillCategoryVO> addfalseVOList=new ArrayList<BillCategoryVO>();
		while(itor.hasNext()){
			String fullcategoryName =itor.next(); //未制证全名称
			BillCategoryVO falseVO=falseMap.get(fullcategoryName);//未制证VO
			addfalseVOList.add(falseVO);
			BillCategoryVO trueVO=trueMap.get(fullcategoryName);//已制证VO
			if(trueVO!=null){
				trueMap.put(trueVO.getPk_category(),trueVO);
			}
		}
		for(int i=0;i<addfalseVOList.size();i++){
			falseMap.put(addfalseVOList.get(i).getPk_category(),addfalseVOList.get(i));
		}
		List<BillCategoryVO> addTrueVOList=new ArrayList<BillCategoryVO>();
		itor = trueMap.keySet().iterator();
		while(itor.hasNext()){
			BillCategoryVO trueVO = trueMap.get(itor.next());
			if (trueMap.containsKey(trueVO.getPk_category()) == false)
			{
				addTrueVOList.add(trueVO);
			}
		}
		for(int i=0;i<addTrueVOList.size();i++){
			trueMap.put(addTrueVOList.get(i).getPk_category(),addTrueVOList.get(i));
		}
	}
//	/**
//	 * 按是否制作查公司分类
//	 * @param pk_corp
//	 * @param period
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	private Map<String, BillCategoryVO> queryCategoryVOs_IsAccount(String pk_corp,String period,String flag)throws DZFWarpException{
//		StringBuffer sb=new StringBuffer();
//		SQLParameter sp=new SQLParameter();
//		sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and nvl(isaccount,'N')=? ");
//		sb.append(" order by categorylevel ");
//		sp.addParam(pk_corp);
//		sp.addParam(period);
//		sp.addParam(flag);
//		Map<String, BillCategoryVO> returnMap=new LinkedHashMap<String, BillCategoryVO>();
//		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,new BeanListProcessor(BillCategoryVO.class));
//		if(list!=null&&list.size()>0){
//			for(int i=0;i<list.size();i++){
//				returnMap.put(list.get(i).getCategorycode(), list.get(i));
//			}
//		}
//		return returnMap;
//	}
	/**
	 * 查询公司isaccount的false的树
	 * @param pk_corp
	 * @param period
	 * @return
	 * @throws DZFWarpException
	 */
//	private Map<String, BillCategoryVO> newInsertCategoryVOs(Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap,String key) throws DZFWarpException {
//		Iterator<String> itor=falseMap.keySet().iterator();
//		List<BillCategoryVO> addList=new ArrayList<BillCategoryVO>();
//		while(itor.hasNext()){//遍历未制证树
//			String categoryCode=itor.next();//未制证编码
//			BillCategoryVO falseVO=falseMap.get(categoryCode);//未制证VO
//			BillCategoryVO trueVO=trueMap.get(categoryCode);//已制证VO
//			if(trueVO==null){//如果没有这个，就增加
//				BillCategoryVO newVO=(BillCategoryVO)falseVO.clone();//复制未制证VO
//				addList.add(newVO);
//			}
//		}
//		
//		if(addList.size()>0){
//			String requestid=null;
//			boolean lock=false;
//			try {
//				requestid = UUID.randomUUID().toString();
//				lock = LockUtil.getInstance().addLockKey("zncs_accounttree", key, requestid, 60);// 设置60秒
//				long starttime = System.currentTimeMillis();
//				while (!lock)
//				{
//					if (System.currentTimeMillis() - starttime > 10000)
//					{
//						throw new BusinessException("操作失败，请稍后再试");
//					}
//					Thread.sleep(100);
//					lock = LockUtil.getInstance().addLockKey("zncs_accounttree", key, requestid, 60);// 设置60秒
//				}
//				
//				for (int i = 0; i < addList.size(); i++) {
//					BillCategoryVO newVO=addList.get(i);
//					newVO.setIsaccount(DZFBoolean.TRUE);//变成已制证
//					newVO.setPk_category(null);//清空主键
//					BillCategoryVO falseVO =falseMap.get(newVO.getCategorycode());
//					if(!StringUtil.isEmpty(falseVO.getPk_parentcategory())){//如果有上级，要设置上级主键
//						String parentCategoryCode=newVO.getCategorycode().substring(0, newVO.getCategorycode().length()-2);//找到上级的编码
//						BillCategoryVO parentVO=trueMap.get(parentCategoryCode);//从已制证map找VO,肯定找得到
//						newVO.setPk_parentcategory(parentVO.getPk_category());//设置已制证父主键
//					}
//					newVO=(BillCategoryVO)singleObjectBO.insertVO(falseVO.getPk_corp(), newVO);//保存
//					trueMap.put(newVO.getCategorycode(), newVO);//缓存到已制证map
//				}
//			} catch (Exception e) {
//			} finally {
//				if(lock){
//					LockUtil.getInstance().unLock_Key("zncs_accounttree", key, requestid);
//				}
//			}
//		}
//		return trueMap;
//	}

	/**
	 * 手动凭证
	 */
	@Override
	public Map<String, Object> generalHandTzpzVOs(String pk_category, String pk_bills, String period, String pk_corp,String pk_parent) throws DZFWarpException {
		//1、得到要做账的票(不包括未识别和问题的)
		//查公司参数
		List<Object> paramList=queryParams(pk_corp);
		List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOs(pk_category, pk_bills, period, pk_corp,pk_parent,DZFBoolean.TRUE);
		if(invoiceList==null||invoiceList.size()==0){
			throw new BusinessException("没有符合制证条件的数据");
		}
		//2、查票据表体
		List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
		//3、表体按表头主键分组
		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			if(detailMap.get(invoiceVO.getPk_invoice())!=null){
				invoiceVO.setChildren(detailMap.get(invoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
			}
		}
		if(DZFBoolean.TRUE.equals(iBillcategory.checkHaveZckp(invoiceList.toArray(new OcrInvoiceVO[0])))){
			throw new BusinessException("所选票据已生成后续单据，请在资产卡片节点处理！");
		}
		if(DZFBoolean.TRUE.equals(iBillcategory.checkHaveIctrade(invoiceList.toArray(new OcrInvoiceVO[0])))){
			throw new BusinessException("所选票据已生成后续单据，请在出入库单节点处理！");
		}
		//4、按专普票分组
		Map<String, List<OcrInvoiceVO>> groupMap=groupByZpp(invoiceList);
		//5、封装返回值
//		Map<String, List<BillInfoVO>> returnMap=new HashMap<String, List<BillInfoVO>>();
		Map<String, Object> returnMap=new HashMap<String,Object>();
		//凭证号
		String pzh=yntBoPubUtil.getNewVoucherNo(pk_corp, new DZFDate(new DZFDate(period+"-01").toString().substring(0, 8)+new DZFDate(period+"-01").getDaysMonth()));
		if(groupMap.get("yh")!=null&&groupMap.get("yh").size()>0){
			//6、封装票据的图片信息
			List<BillInfoVO> billInfos=queryBillInfos(groupMap.get("yh"));
			returnMap.put("yh", billInfos);
			returnMap.put("yhvoucherno",pzh);
			if(groupMap.get("yh").size()==1){
				returnMap.put("yhvoucherdate",getVoucherDate(paramList, groupMap.get("yh").get(0)));
			}else{
				returnMap.put("yhvoucherdate",new DZFDate(period+"-"+new DZFDate(period+"-01").getDaysMonth()));
			}
			pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
		}
		if(groupMap.get("zjx")!=null&&groupMap.get("zjx").size()>0){
			//6、封装票据的图片信息
			List<BillInfoVO> billInfos=queryBillInfos(groupMap.get("zjx"));
			returnMap.put("zjx", billInfos);
			returnMap.put("zjxvoucherno",pzh);
			if(groupMap.get("zjx").size()==1){
				returnMap.put("zjxvoucherdate",getVoucherDate(paramList, groupMap.get("zjx").get(0)));
			}else{
				returnMap.put("zjxvoucherdate",new DZFDate(period+"-"+new DZFDate(period+"-01").getDaysMonth()));
			}
			pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
		}
		if(groupMap.get("pjx")!=null&&groupMap.get("pjx").size()>0){
			//6、封装票据的图片信息
			List<BillInfoVO> billInfos=queryBillInfos(groupMap.get("pjx"));
			returnMap.put("pjx", billInfos);
			returnMap.put("pjxvoucherno",pzh);
			if(groupMap.get("pjx").size()==1){
				returnMap.put("pjxvoucherdate",getVoucherDate(paramList, groupMap.get("pjx").get(0)));
			}else{
				returnMap.put("pjxvoucherdate",new DZFDate(period+"-"+new DZFDate(period+"-01").getDaysMonth()));
			}
			pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
		}
		if(groupMap.get("zxx")!=null&&groupMap.get("zxx").size()>0){
			//6、封装票据的图片信息
			List<BillInfoVO> billInfos=queryBillInfos(groupMap.get("zxx"));
			returnMap.put("zxx", billInfos);
			returnMap.put("zxxvoucherno",pzh);
			if(groupMap.get("zxx").size()==1){
				returnMap.put("zxxvoucherdate",getVoucherDate(paramList, groupMap.get("zxx").get(0)));
			}else{
				returnMap.put("zxxvoucherdate",new DZFDate(period+"-"+new DZFDate(period+"-01").getDaysMonth()));
			}
			pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
		}
		if(groupMap.get("pxx")!=null&&groupMap.get("pxx").size()>0){
			//6、封装票据的图片信息
			List<BillInfoVO> billInfos=queryBillInfos(groupMap.get("pxx"));
			returnMap.put("pxx", billInfos);
			returnMap.put("pxxvoucherno",pzh);
			if(groupMap.get("pxx").size()==1){
				returnMap.put("pxxvoucherdate",getVoucherDate(paramList, groupMap.get("pxx").get(0)));
			}else{
				returnMap.put("pxxvoucherdate",new DZFDate(period+"-"+new DZFDate(period+"-01").getDaysMonth()));
			}
			pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
		}
		return returnMap;
	}
	/**
	 * 把票据分组——按专普票
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, List<OcrInvoiceVO>> groupByZpp(List<OcrInvoiceVO> invoiceList)throws DZFWarpException{
		Map<String, List<OcrInvoiceVO>> returnMap=new HashMap<String, List<OcrInvoiceVO>>();
		List<OcrInvoiceVO> yhList=new ArrayList<OcrInvoiceVO>();//银行
		List<OcrInvoiceVO> zjxList=new ArrayList<OcrInvoiceVO>();//专进项
		List<OcrInvoiceVO> pjxList=new ArrayList<OcrInvoiceVO>();//普进项+其他
		List<OcrInvoiceVO> zxxList=new ArrayList<OcrInvoiceVO>();//专销项
		List<OcrInvoiceVO> pxxList=new ArrayList<OcrInvoiceVO>();//普销项
		CorpVO corp=corpService.queryByPk(invoiceList.get(0).getPk_corp());
		for (int i = 0; i < invoiceList.size(); i++) {
			String istate=invoiceList.get(i).getIstate();//发票大类
			String invoicetype=invoiceList.get(i).getInvoicetype();//发票小类
			if(!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_1)){
				yhList.add(invoiceList.get(i));
			}else{
				//是增值税发票
				if(!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_3)){
					if(OcrUtil.isSameCompany(corp.getUnitname(),invoiceList.get(i).getVpurchname())){//进项
						if(!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){//专进项
							zjxList.add(invoiceList.get(i));
						}else{//普进项
							pjxList.add(invoiceList.get(i));
						}
					}else{//销项
						if(!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){//专销项
							zxxList.add(invoiceList.get(i));
						}else{//普销项
							pxxList.add(invoiceList.get(i));
						}
					}
				}else{//其他票
					pjxList.add(invoiceList.get(i));
				}
			}
		}
		returnMap.put("yh", yhList);
		returnMap.put("zjx", zjxList);
		returnMap.put("pjx", pjxList);
		returnMap.put("zxx", zxxList);
		returnMap.put("pxx", pxxList);
		return returnMap;
	}
	/**
	 * 查票据关联的图片
	 * @param invoiceList
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BillInfoVO> queryBillInfos(List<OcrInvoiceVO> invoiceList) throws DZFWarpException {
		List<BillInfoVO> returnList=new ArrayList<BillInfoVO>();
		List<OcrImageLibraryVO> libraryList=queryOcrImageLibraryVOs(invoiceList);
		Map<String, OcrImageLibraryVO> libraryMap=getOcrLibraryMap(libraryList);
		for(int i=0;i<invoiceList.size();i++){
			BillInfoVO billinfovo = new BillInfoVO();
			OcrInvoiceVO vo = invoiceList.get(i);
			OcrInvoiceDetailVO[] vos =(OcrInvoiceDetailVO[])vo.getChildren();
			vo.setChildren(vos);
			billinfovo.setInvoicvo(vo);
			OcrImageLibraryVO librayrvo=libraryMap.get(vo.getOcr_id());
			billinfovo.setImgsourid(librayrvo.getCrelationid());
			billinfovo.setImgname(librayrvo.getImgname());
			billinfovo.setCorpId(librayrvo.getPk_corp());
			vo.setCorpName(CodeUtils1.deCode(corpService.queryByPk(librayrvo.getPk_corp()).getUnitname()));
			vo.setCorpCode(corpService.queryByPk(librayrvo.getPk_corp()).getUnitcode());
			returnList.add(billinfovo);
		}
		return returnList;
	}
	
	/**
	 * 查OcrImageLibraryVO
	 * @param list
	 * @return
	 * @throws DZFWarpException
	 */
	private List<OcrImageLibraryVO> queryOcrImageLibraryVOs(List<OcrInvoiceVO> list)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		List<String> pkList = new ArrayList<String>();
		for (OcrInvoiceVO ocrInvoiceVO : list) {
			pkList.add(ocrInvoiceVO.getOcr_id());
		}
		sb.append("select * from ynt_image_ocrlibrary where nvl(dr,0)=0 ");
		sb.append(" and "+SqlUtil.buildSqlForIn("pk_image_ocrlibrary", pkList.toArray(new String[0])));
		List<OcrImageLibraryVO> returnList=(List<OcrImageLibraryVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrImageLibraryVO.class));
		return returnList;
	}
	
	/**
	 * 返回libraryMap
	 * @param libraryList
	 * @return
	 * @throws DZFWarpException
	 */
	private Map<String, OcrImageLibraryVO> getOcrLibraryMap(List<OcrImageLibraryVO> libraryList)throws DZFWarpException{
		Map<String, OcrImageLibraryVO> returnMap=new HashMap<String, OcrImageLibraryVO>();
		for(int i=0;i<libraryList.size();i++){
			returnMap.put(libraryList.get(i).getPk_image_ocrlibrary(), libraryList.get(i));
		}
		return returnMap;
	}

	private void setTzpzHVODefaultValue(List<TzpzHVO> tzpzHVOs,Map<String, OcrInvoiceVO> invoiceVOMap,CorpVO corp)throws DZFWarpException{
		String pzh=yntBoPubUtil.getNewVoucherNo(tzpzHVOs.get(0).getPk_corp(), tzpzHVOs.get(0).getDoperatedate());
		for(int i=0;i<tzpzHVOs.size();i++){
			TzpzHVO headVO=tzpzHVOs.get(i);
			TzpzBVO[] bodyVOs=(TzpzBVO[])headVO.getChildren();
			
			headVO.setVbillstatus(IVoucherConstants.FREE);// 凭证状态暂存
			headVO.setIshasjz(DZFBoolean.FALSE);//是否记账
			headVO.setPzlb(0);// 凭证类别：记账
			headVO.setSourcebillid(headVO.getPk_image_group());//来源ID
			headVO.setSourcebilltype(IBillTypeCode.HP110);// 来源单据类型
			headVO.setIsfpxjxm(DZFBoolean.FALSE);
			headVO.setVyear(Integer.valueOf(headVO.getPeriod().toString().substring(0, 4)));//会计年
			headVO.setIsocr(DZFBoolean.TRUE);
			headVO.setIautorecognize(1);//0-- 非识别 1----识别
			String[] invoicesKey=headVO.getUserObject().toString().split(",");
			for(int w=0;w<invoicesKey.length;w++){
				OcrInvoiceVO invoiceVOTmp=invoiceVOMap.get(invoicesKey[w]);//取出凭证对应的票据
				String istateTmp=invoiceVOTmp.getIstate();//发票大类
				if(!StringUtil.isEmpty(istateTmp)&&istateTmp.equals(ZncsConst.SBZT_3)){
					if(OcrUtil.isSameCompany(corp.getUnitname(),invoiceVOTmp.getVpurchname())){
						headVO.setSourcebilltype(IBillTypeCode.HP95);// 来源单据类型_进项
					}else{
						headVO.setSourcebilltype(IBillTypeCode.HP90);// 来源单据类型_销项
					}
					break;
				}else if(!StringUtil.isEmpty(istateTmp)&&istateTmp.equals(ZncsConst.SBZT_1)){
					headVO.setSourcebilltype(IBillTypeCode.HP85);// 银行对账单
					break;
				}
			}
			if(StringUtil.isEmpty(headVO.getSourcebilltype())){
				headVO.setSourcebilltype(IBillTypeCode.HP110);
			}
			DZFDouble jfmny = DZFDouble.ZERO_DBL;
			DZFDouble dfmny = DZFDouble.ZERO_DBL;
			
			for (TzpzBVO bvo : bodyVOs) {
				jfmny = SafeCompute.add(jfmny, bvo.getJfmny());
				dfmny = SafeCompute.add(dfmny, bvo.getDfmny());
			}
			headVO.setJfmny(jfmny);
			headVO.setDfmny(dfmny);
			
			pzh=OcrUtil.addZeroForNum(String.valueOf(Integer.parseInt(pzh)+1), 4);
		}
	}
	
	@Override
	public void saveHandVouchers(List<TzpzHVO> tzpzHVOs) throws DZFWarpException {
		if(tzpzHVOs == null || tzpzHVOs.size() == 0){
			return ;
		}
		CorpVO corp=corpService.queryByPk(tzpzHVOs.get(0).getPk_corp());
		//0、查公司参数
		List<Object> paramList=queryParams(tzpzHVOs.get(0).getPk_corp());
		//1、取所有凭证上的票据主键
		List<String> invoiceKeyList=getInvoiceKey(tzpzHVOs);
		//2、查票据invoice
		List<OcrInvoiceVO> invoiceList=queryOcrInvoiceVOsByWhere(null, invoiceKeyList.toArray(new String[0]),  tzpzHVOs.get(0).getPeriod(), corp.getPk_corp(),((ParaSetVO)paramList.get(0)).getErrorvoucher());
		//3、查票据表体
		List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
		//4、表体按表头主键分组
		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
		//5、查票据关联的ImageGroup
//		Map<String, ImageGroupVO> groupMap=queryImageGroupVO(invoiceList);
//		//6、未制证分类
//		Map<String, BillCategoryVO> falseMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corp.getPk_corp(), tzpzHVOs.get(0).getPeriod(),"N");
//		//7、已制证分类
//		Map<String, BillCategoryVO> trueMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corp.getPk_corp(), tzpzHVOs.get(0).getPeriod(),"Y");
//		//8、创建差异已制证分类树，并得到最新的已制证分类树
//		trueMap = iZncsNewTransService.newInsertCategoryVOs(falseMap, trueMap,tzpzHVOs.get(0).getPk_corp()+tzpzHVOs.get(0).getPeriod());
//		//9、把已制证分类树和未制证分类map，转换成主键为key的map,增加到原map里
//		turnBillCategoryMap(falseMap, trueMap);
		//10、更新原票上表头表体的pk_billcategory为已制证的pk_category
		updateNewPk_billcategory(invoiceList, detailMap, null, null);// falseMap, trueMap);
		//11、得到发票map
		Map<String, OcrInvoiceVO> invoiceVOMap=new HashMap<String, OcrInvoiceVO>();
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			invoiceVOMap.put(invoiceVO.getPk_invoice(), invoiceVO);
		}
		if (tzpzHVOs != null && tzpzHVOs.size() > 0) {
			setTzpzHVODefaultValue(tzpzHVOs, invoiceVOMap, corp);
			for (int i = 0; i < tzpzHVOs.size(); i++) {
				String pk_image_librarys=tzpzHVOs.get(i).getPk_image_group();//这里是多个pk_image_library
				String pk_image_groups=queryGroupKeyByLibrarykey(pk_image_librarys);
				List<String> groupList=new ArrayList<String>();
				String[] groups=pk_image_groups.split(",");
				for (int j = 0; j < groups.length; j++) {
					groupList.add(groups[j]);
				}
				String pk_image_group=iImageGroupService.processMergeGroup(tzpzHVOs.get(i).getPk_corp(), null, groupList);
				tzpzHVOs.get(i).setPk_image_group(pk_image_group);
				tzpzHVOs.get(i).setSourcebillid(pk_image_group);
				iVoucherService.saveVoucher(corp, tzpzHVOs.get(i));
			}
		}
	}
	
	/**
	 * @param pk_image_librarys
	 * @return
	 * @throws DZFWarpException
	 */
	private String queryGroupKeyByLibrarykey(String pk_image_librarys)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		sb.append("select pk_image_library,pk_image_group from ynt_image_library where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_image_library", pk_image_librarys.split(",")));
		List<Object[]> libraryList=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), new SQLParameter(), new ArrayListProcessor());
		Map<String, String> keyMap=new HashMap<>();
		for(int i=0;i<libraryList.size();i++){
			Object[] obj=libraryList.get(i);
			keyMap.put(obj[0].toString(), obj[1].toString());
		}
		String[] pk_image_library=pk_image_librarys.split(",");
		String pk_image_group="";
		for (int i = 0; i < pk_image_library.length; i++) {
			String string = pk_image_library[i];
			pk_image_group=keyMap.get(string)+",";
		}
		pk_image_group=pk_image_group.substring(0,pk_image_group.length()-1);
		return pk_image_group;
	}

	private PzSourceRelationVO[] getSourceRelations(String pk_tzpz_h,
			String pk_corp) {
		String condition = " pk_tzpz_h = ? and pk_corp = ? and nvl(dr, 0) = 0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);
		PzSourceRelationVO[] rs = (PzSourceRelationVO[]) singleObjectBO
				.queryByCondition(PzSourceRelationVO.class, condition, sp);
		return rs;
	}
	
	@Override
	public void deleteVoucherBefore(TzpzHVO tzpzHVO) throws DZFWarpException {
		DZFBoolean isHaveBank=DZFBoolean.FALSE;//合并的凭证是否有银行票
//		IZncsVoucher iZncsVoucher = (IZncsVoucher) SpringUtils.getBean("zncsVoucherImpl");
//		DZFBoolean voucherFlag=iZncsVoucher.getVoucherFlag();
		DZFBoolean voucherFlag = getVoucherFlag();
		if (DZFBoolean.TRUE.equals(voucherFlag)&&tzpzHVO.getVbillstatus()!=IVoucherConstants.TEMPORARY){
			PzSourceRelationVO[] sources=tzpzHVO.getSource_relation();
			if (sources == null) {
				sources = getSourceRelations(tzpzHVO.getPk_tzpz_h(), tzpzHVO.getPk_corp());
			}
			if(sources==null||sources.length==0){
				if(StringUtil.isEmpty(tzpzHVO.getSourcebilltype())
						|| (!tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP85) 
								&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP90) 
								&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP95) 
								&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP59)
								&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP70) 
								&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP75)
								&& !tzpzHVO.getSourcebilltype().equals(IBillTypeCode.HP110))){
					return;
				}
			}else{
				DZFBoolean isReturn=DZFBoolean.TRUE;
				for (int i = 0; i < sources.length; i++) {
					PzSourceRelationVO pzSourceRelationVO = sources[i];
					if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP85)){
						isHaveBank=DZFBoolean.TRUE;
					}
					if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP85)||
					   pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP90)||
					   pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP95)||
					   pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP59)||
					   pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP70)||
					   pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP75)||
					   pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP110)){
							isReturn=DZFBoolean.FALSE;
							//合并的凭证删除，没有来源，把表体来源赋给表头，前提是表体来源都一样,目前没有发现不一样的来源可以合并的
							if(StringUtil.isEmpty(tzpzHVO.getSourcebilltype())&&!pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP85)){
								tzpzHVO.setSourcebilltype(pzSourceRelationVO.getSourcebilltype());
							}
					}
				}
				if(DZFBoolean.TRUE.equals(isReturn))return;
			}
		}else{
			return;
		}
		if(isHaveBank!=null&&DZFBoolean.TRUE.equals(isHaveBank)&&StringUtil.isEmpty(tzpzHVO.getSourcebilltype())){
			tzpzHVO.setSourcebilltype(IBillTypeCode.HP85);
		}
		if(!StringUtil.isEmpty(tzpzHVO.getIsMerge())&&tzpzHVO.getIsMerge().equals("Y"))return;
//		// 未制证分类
//		Map<String, BillCategoryVO> falseMap = iZncsNewTransService.queryCategoryVOs_IsAccount(tzpzHVO.getPk_corp(), tzpzHVO.getPeriod(), "N");
//		// 已制证分类
//		Map<String, BillCategoryVO> trueMap = iZncsNewTransService.queryCategoryVOs_IsAccount(tzpzHVO.getPk_corp(), tzpzHVO.getPeriod(), "Y");
//		// 转PK树
//		turnBillCategoryMap(falseMap, trueMap);
		if(!StringUtil.isEmpty(tzpzHVO.getPk_image_group())){
			// 查这个凭证关联的票据
			List<OcrInvoiceVO> invoiceList = queryOcrInvoiceVOsForZZ(tzpzHVO.getPk_corp(), tzpzHVO.getPk_image_group());
			invoiceList=filterVersionInvoiceVO(invoiceList);
			if (invoiceList == null || invoiceList.size() == 0)
				return;
			
			Map<String, BillCategoryVO> falseMap = getFalseMap(null, invoiceList.get(0).getPk_billcategory());
			Map<String, BillCategoryVO> trueMap = getTrueMap(falseMap, null, invoiceList.get(0).getPk_billcategory());
			
			// 查票据体
			List<OcrInvoiceDetailVO> detailList = queryInvoiceDetail(invoiceList);
			// 表体按表头主键分组
			Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
			// 更新会未制证分类树
			for (int i = 0; i < invoiceList.size(); i++) {
				OcrInvoiceVO invoiceVO = invoiceList.get(i);
				String pk_category_old = invoiceVO.getPk_billcategory();// isaccount=Y的分类id
				BillCategoryVO trueVO = trueMap.get(pk_category_old);
				if(trueVO==null){
					falseMap=null;
					trueMap=null;
					falseMap = getFalseMap(falseMap,pk_category_old);
					trueMap = getTrueMap(falseMap, trueMap, pk_category_old);
					trueVO = trueMap.get(pk_category_old);
				}
				BillCategoryVO falseVO = getParentBillCategoryVO(falseMap, trueMap,trueVO);
				if (falseVO != null) {
					String pk_category_new = falseVO.getPk_category();// isaccount=N的分类id
					invoiceVO.setPk_billcategory(pk_category_new);
					OcrInvoiceDetailVO[] detailVOs = detailMap.get(invoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]);
					for (int j = 0; j < detailVOs.length; j++) {
						OcrInvoiceDetailVO ocrInvoiceDetailVO = detailVOs[j];
						if (pk_category_old.equals(ocrInvoiceDetailVO.getPk_billcategory())) {
							ocrInvoiceDetailVO.setPk_billcategory(pk_category_new);// 表体的也换成新的
						}else{
							String pk_category_old1 = ocrInvoiceDetailVO.getPk_billcategory();// isaccount=Y的分类id
							if(StringUtil.isEmpty(pk_category_old1))continue;
							BillCategoryVO trueVO1 = trueMap.get(pk_category_old1);
							BillCategoryVO falseVO1 = getParentBillCategoryVO(falseMap, trueMap,trueVO1);
							if (falseVO1 != null) {
								String pk_category_new1 = falseVO1.getPk_category();// isaccount=N的分类id
								ocrInvoiceDetailVO.setPk_billcategory(pk_category_new1);// 表体的也换成新的
							}
						}
					}
					invoiceVO.setChildren(detailVOs);
					singleObjectBO.update(invoiceVO, new String[] { "pk_billcategory" });
					singleObjectBO.updateAry(detailVOs, new String[] { "pk_billcategory" });
				}
			}
			//更新业务类型
			updateSourceModel(tzpzHVO, null, tzpzHVO.getPk_image_group(),DZFBoolean.FALSE, falseMap, trueMap,isHaveBank);
			updateSourceModel2(tzpzHVO, null, tzpzHVO.getPk_tzpz_h(), DZFBoolean.FALSE, null, null,isHaveBank);
		}else{
			updateSourceModel2(tzpzHVO, null, tzpzHVO.getPk_tzpz_h(), DZFBoolean.FALSE, null, null,isHaveBank);//falseMap, trueMap);
		}
	}
	
	/**
	 * 找到一个有效的BillCategoryVO，一直往上找
	 * @return
	 * @throws DZFWarpException
	 */
	private BillCategoryVO getParentBillCategoryVO(Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap,BillCategoryVO trueVO)throws DZFWarpException{
		BillCategoryVO falseVO=falseMap.get(trueVO.getFullcategoryname());
		if(falseVO==null){
			return getParentBillCategoryVO(falseMap,trueMap, trueMap.get(trueVO.getPk_parentcategory()));
		}else{
			return falseVO;
		}
	}
	/**
	 * 查已制证的票据
	 * @throws DZFWarpException
	 */
	private List<OcrInvoiceVO> queryOcrInvoiceVOsForZZ(String pk_corp,String pk_image_group)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select a.* from ynt_interface_invoice a,ynt_image_group b");
		sb.append(" where a.pk_image_group=b.pk_image_group  and nvl(a.dr,0)=0 and nvl(b.dr,0)=0 and b.istate !=205 ");
		sb.append(" and (b.istate =100 or b.istate =101) ");
		sb.append(" and a.pk_corp=? ");
		sb.append(" and a.pk_image_group=? order by a.period");
		sp.addParam(pk_corp);
		sp.addParam(pk_image_group);
		List<OcrInvoiceVO> list=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		return list;
	}
	@Override
	public DZFBoolean getVoucherFlag() throws DZFWarpException {
		String sql="select zncsflag from ynt_zncsflag";
		try{
			List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, new SQLParameter(), new ArrayListProcessor());
			if(list!=null&&list.size()>0){
				String flag=list.get(0)[0].toString();
				return new DZFBoolean(flag);
			}
		}catch(Exception e){
			return DZFBoolean.FALSE;
		}
		return DZFBoolean.FALSE;
	}
	
	/**
	 * 设置税目
	 * @throws DZFWarpException
	 *  abcd12345678efaabd000100	100	一般计税方法-17%税率的货物及加工修理修配劳务 	一般货物17%	一般纳税人	0.17000000
		abcd12345678efaabd000101	101	一般计税方法-17%税率的服务、不动产和无形资产	一般服务17%	一般纳税人	0.17000000
		abcd12345678efaabd000102	102	一般计税方法-13%税率 	一般13%	一般纳税人	0.13000000
		abcd12345678efaabd000103	103	一般计税方法-11%税率的货物及加工修理修配劳务	一般货物11%	一般纳税人	0.11000000
		abcd12345678efaabd000104	104	一般计税方法-11%税率的服务、不动产和无形资产 	一般服务11%	一般纳税人	0.11000000
		abcd12345678efaabd000105	105	一般计税方法-6%税率 	一般6%	一般纳税人	0.06000000
		abcd12345678efaabd000106	106	简易计税方法-6%征收率	简易6%	一般纳税人	0.06000000
		abcd12345678efaabd000107	107	简易计税方法-5%征收率的货物及加工修理修配劳务	简易货物5%	一般纳税人	0.05000000
		abcd12345678efaabd000108	108	简易计税方法-5%征收率的服务、不动产和无形资产	简易服务5%	一般纳税人	0.05000000
		abcd12345678efaabd000109	109	简易计税方法-4%征收率	简易4%	一般纳税人	0.04000000
		abcd12345678efaabd000110	110	简易计税方法-3%征收率的货物及加工修理修配劳务	简易货物3%	一般纳税人	0.03000000
		abcd12345678efaabd000111	111	简易计税方法-3%征收率的服务、不动产和无形资产	简易服务3%	一般纳税人	0.03000000
		abcd12345678efaabd000112	112	免抵退税-货物及加工修理修配劳务	免抵退货物0%	一般纳税人	0.00000000
		abcd12345678efaabd000113	113	免抵退税-服务、不动产和无形资产 	免抵退服务0%	一般纳税人	0.00000000
		abcd12345678efaabd000114	114	免税-货物及加工修理修配劳务	免税货物0%	一般纳税人	0.00000000
		abcd12345678efaabd000115	115	免税-服务、不动产和无形资产 	免税服务0%	一般纳税人	0.00000000
		abcd12345678efaabd000123	123	自开或税务机关代开的增值税专用发票不含税销售额3%征收率-货物及劳务	货物专票3%	小规模纳税人	0.03000000
		abcd12345678efaabd000124	124	自开或税务机关代开的增值税专用发票不含税销售额3%征收率-服务、不动产和无形资产	服务专票3%	小规模纳税人	0.03000000
		abcd12345678efaabd000125	125	税控器具开具的普通发票不含税销售额3%征收率-货物及劳务	货物普票3%	小规模纳税人	0.03000000
		abcd12345678efaabd000126	126	税控器具开具的普通发票不含税销售额3%征收率-服务、不动产和无形资产	服务普票3%	小规模纳税人	0.03000000
		abcd12345678efaabd000127	127	自开或税务机关代开的增值税专用发票不含税销售额5%征收率-服务、不动产和无形资产	服务专票5%	小规模纳税人	0.05000000
		abcd12345678efaabd000128	128	税控器具开具的普通发票不含税销售额5%征收率-服务、不动产和无形资产	服务普票5%	小规模纳税人	0.05000000
		abcd12345678efaabd000129	129	11%农产品收购发票	进项农产品11%	一般纳税人	0.11000000
		abcd12345678efaabd000130	130	一般计税方法-16%税率的货物及加工修理修配劳务 	一般货物16%	一般纳税人	0.16000000
		abcd12345678efaabd000131	131	一般计税方法-16%税率的服务、不动产和无形资产	一般服务16%	一般纳税人	0.16000000
		abcd12345678efaabd000132	132	一般计税方法-10%税率的货物及加工修理修配劳务	一般货物10%	一般纳税人	0.10000000
		abcd12345678efaabd000133	133	一般计税方法-10%税率的服务、不动产和无形资产 	一般服务10%	一般纳税人	0.10000000
		abcd12345678efaabd000136	136	10%农产品收购发票	进项10%农产品收购	一般纳税人	0.10000000
		abcd12345678efaabd000137	137	12%农产品收购发票委托加工、生产销售	进项12%农产品委托加工、生产销售	一般纳税人	0.12000000
		abcd12345678efaabd000138  138 一般计税方法-13%税率的货物及加工修理修配劳务  一般货物13% 一般纳税人 0.13000000
		abcd12345678efaabd000139  139 一般计税方法-13%税率的服务、不动产和无形资产  一般服务13% 一般纳税人 0.13000000
		abcd12345678efaabd000140  140 一般计税方法-9%税率的货物及加工修理修配劳务 一般货物9%  一般纳税人 0.09000000
		abcd12345678efaabd000141  141 一般计税方法-9%税率的服务、不动产和无形资产 一般服务9%  一般纳税人 0.09000000
		abcd12345678efaabd000142  142 9%农产品收购发票 进项9%农产品收购 一般纳税人 0.09000000
		abcd12345678efaabd000143  143	旅客运输服务进项税（非专票）	旅客运输服务进项税（非专票）	一般纳税人
		abcd12345678efaabd000144  144	旅客运输服务进项税（专票）	旅客运输服务进项税（专票）	一般纳税人

	 */
	private void setTaxItem(OcrInvoiceVO invoiceVO,OcrInvoiceDetailVO detailVO,TzpzBVO tzpzBVO,CorpVO corp)throws DZFWarpException{
		if(detailVO==null)return;
		if(!"00000100AA10000000000BMQ".equals(corp.getCorptype())&&!"00000100AA10000000000BMD".equals(corp.getCorptype())&&!"00000100AA10000000000BMF".equals(corp.getCorptype())&&!"00000100000000Ig4yfE0005".equals(corp.getCorptype())){
			return;
		}
		if ("00000100AA10000000000BMD".equals(corp.getCorptype())) {//小企业会计准则
			if(!tzpzBVO.getVcode().startsWith("5001")&&!tzpzBVO.getVcode().startsWith("5051")&&!tzpzBVO.getVcode().startsWith("560113")&&!tzpzBVO.getVcode().startsWith("560214")&&!tzpzBVO.getVcode().startsWith("56022102")
					&&!tzpzBVO.getVcode().startsWith("5601013")&&!tzpzBVO.getVcode().startsWith("5602014")&&!tzpzBVO.getVcode().startsWith("560202102")&&!tzpzBVO.getVcode().startsWith("5602021002")){
				return;
			}
		} else if ("00000100AA10000000000BMF".equals(corp.getCorptype())) {//企业会计准则
			if(!tzpzBVO.getVcode().startsWith("6001")&&!tzpzBVO.getVcode().startsWith("6051")&&!tzpzBVO.getVcode().startsWith("660113")&&!tzpzBVO.getVcode().startsWith("660213")&&!tzpzBVO.getVcode().startsWith("66022001")
					&&!tzpzBVO.getVcode().startsWith("6601013")&&!tzpzBVO.getVcode().startsWith("6602013")&&!tzpzBVO.getVcode().startsWith("660202001")&&!tzpzBVO.getVcode().startsWith("6602020001")){
				return;
			}
		} else if("00000100000000Ig4yfE0005".equals(corp.getCorptype())){//企业会计制度
			if(!tzpzBVO.getVcode().startsWith("5101")&&!tzpzBVO.getVcode().startsWith("5102")&&!tzpzBVO.getVcode().startsWith("550113")&&!tzpzBVO.getVcode().startsWith("550213")&&!tzpzBVO.getVcode().startsWith("55022001")
					&&!tzpzBVO.getVcode().startsWith("5501013")&&!tzpzBVO.getVcode().startsWith("5502013")&&!tzpzBVO.getVcode().startsWith("550202001")&&!tzpzBVO.getVcode().startsWith("5502020001")){
				return;
			}
		}else if ("00000100AA10000000000BMQ".equals(corp.getCorptype())) {//民间非营利组织会计制度
			if(!tzpzBVO.getVcode().startsWith("5201")){
				return;
			}
		}
		String istate=invoiceVO.getIstate();//发票大类
		String invoicetype=StringUtil.isEmpty(invoiceVO.getInvoicetype())?"":invoiceVO.getInvoicetype();//发票小类
		String date=StringUtil.isEmpty(invoiceVO.getDinvoicedate())?null:invoiceVO.getDinvoicedate().replace("年", "-").replace("月", "-").replace("日", "");
		if(date!=null){
			try{
				new DZFDate(date); 
			}catch(Exception e1){
				date=null;
			}
		}
		String chargedeptname=corp.getChargedeptname();//公司性质
		PZTaxItemRadioVO taxItemVO=new PZTaxItemRadioVO();
		if(!StringUtil.isEmpty(istate)&&istate.equals(ZncsConst.SBZT_3)){
			String invname=detailVO.getInvname();//名称
			String sl=detailVO.getItemtaxrate();
			DZFDouble slDbl=DZFDouble.ZERO_DBL;//税率
			if (!StringUtil.isEmpty(sl)) {
				sl = sl.replaceAll("%", "");
				try {
					slDbl = new DZFDouble(sl, 2).div(100);
				} catch (Exception e) {
				}
			}
			taxItemVO.setTaxratio(slDbl);//税率
			taxItemVO.setTaxmny(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny()))?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));//税额
			taxItemVO.setMny(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(detailVO.getItemmny()))?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())));//金额
			if(tzpzBVO.getVcode().startsWith("5001")||tzpzBVO.getVcode().startsWith("5051")||tzpzBVO.getVcode().startsWith("6001")||tzpzBVO.getVcode().startsWith("6051")||tzpzBVO.getVcode().startsWith("5101")||tzpzBVO.getVcode().startsWith("5102")){
				//这些都是销项
				if("小规模纳税人".equals(chargedeptname)){
					if(!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){
						if(slDbl.compareTo(new DZFDouble(0.03))==0){//3%
							if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
								taxItemVO.setPk_taxitem("abcd12345678efaabd000124");
								taxItemVO.setTaxcode("124");
								taxItemVO.setTaxname("自开或税务机关代开的增值税专用发票不含税销售额3%征收率-服务、不动产和无形资产");
							}else{
								taxItemVO.setPk_taxitem("abcd12345678efaabd000123");
								taxItemVO.setTaxcode("123");
								taxItemVO.setTaxname("自开或税务机关代开的增值税专用发票不含税销售额3%征收率-货物及劳务");
							}
						}else if(slDbl.compareTo(new DZFDouble(0.05))==0){//5%
							taxItemVO.setPk_taxitem("abcd12345678efaabd000127");
							taxItemVO.setTaxcode("127");
							taxItemVO.setTaxname("自开或税务机关代开的增值税专用发票不含税销售额5%征收率-服务、不动产和无形资产");
						}
					}else{
						if(slDbl.compareTo(new DZFDouble(0.03))==0){//3%
							if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
								taxItemVO.setPk_taxitem("abcd12345678efaabd000126");
								taxItemVO.setTaxcode("126");
								taxItemVO.setTaxname("税控器具开具的普通发票不含税销售额3%征收率-服务、不动产和无形资产");
							}else{
								taxItemVO.setPk_taxitem("abcd12345678efaabd000125");
								taxItemVO.setTaxcode("125");
								taxItemVO.setTaxname("税控器具开具的普通发票不含税销售额3%征收率-货物及劳务");
							}
						}else if(slDbl.compareTo(new DZFDouble(0.05))==0){//5%
							taxItemVO.setPk_taxitem("abcd12345678efaabd000128");
							taxItemVO.setTaxcode("128");
							taxItemVO.setTaxname("税控器具开具的普通发票不含税销售额5%征收率-服务、不动产和无形资产");
						}
					}
				}else{
					if(slDbl.compareTo(new DZFDouble(0.16))==0){//16%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000131");
							taxItemVO.setTaxcode("131");
							taxItemVO.setTaxname("一般计税方法-16%税率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000130");
							taxItemVO.setTaxcode("130");
							taxItemVO.setTaxname("一般计税方法-16%税率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.10))==0){//10%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000133");
							taxItemVO.setTaxcode("133");
							taxItemVO.setTaxname("一般计税方法-10%税率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000132");
							taxItemVO.setTaxcode("132");
							taxItemVO.setTaxname("一般计税方法-10%税率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.06))==0){//6%
						if(CaclTaxMny.getTaxItemPercent6(corp.getPk_corp()).equals("abcd12345678efaabd000105")){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000105");
							taxItemVO.setTaxcode("105");
							taxItemVO.setTaxname("一般计税方法-6%税率");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000106");
							taxItemVO.setTaxcode("106");
							taxItemVO.setTaxname("简易计税方法-6%征收率");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.17))==0){//17%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000101");
							taxItemVO.setTaxcode("101");
							taxItemVO.setTaxname("一般计税方法-17%税率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000100");
							taxItemVO.setTaxcode("100");
							taxItemVO.setTaxname("一般计税方法-17%税率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.13))==0){//13%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000139");
							taxItemVO.setTaxcode("139");
							taxItemVO.setTaxname("一般计税方法-13%税率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000138");
							taxItemVO.setTaxcode("138");
							taxItemVO.setTaxname("一般计税方法-13%税率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.11))==0){//11%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000104");
							taxItemVO.setTaxcode("104");
							taxItemVO.setTaxname("一般计税方法-11%税率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000103");
							taxItemVO.setTaxcode("103");
							taxItemVO.setTaxname("一般计税方法-11%税率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.05))==0){//5%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000108");
							taxItemVO.setTaxcode("108");
							taxItemVO.setTaxname("简易计税方法-5%征收率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000107");
							taxItemVO.setTaxcode("107");
							taxItemVO.setTaxname("简易计税方法-5%征收率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.04))==0){//4%
						taxItemVO.setPk_taxitem("abcd12345678efaabd000109");
						taxItemVO.setTaxcode("109");
						taxItemVO.setTaxname("简易计税方法-4%征收率");
					}else if(slDbl.compareTo(new DZFDouble(0.03))==0){//3%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000111");
							taxItemVO.setTaxcode("111");
							taxItemVO.setTaxname("简易计税方法-3%征收率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000110");
							taxItemVO.setTaxcode("110");
							taxItemVO.setTaxname("简易计税方法-3%征收率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0.09))==0){//9%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000141");
							taxItemVO.setTaxcode("141");
							taxItemVO.setTaxname("一般计税方法-9%税率的服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000140");
							taxItemVO.setTaxcode("140");
							taxItemVO.setTaxname("一般计税方法-9%税率的货物及加工修理修配劳务");
						}
					}else if(slDbl.compareTo(new DZFDouble(0))==0){//0%
						if(invname.indexOf("服务")>-1||invname.indexOf("劳务")>-1||invname.indexOf("不动产")>-1||invname.indexOf("无形资产")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000115");
							taxItemVO.setTaxcode("115");
							taxItemVO.setTaxname("免税-服务、不动产和无形资产");
						}else{
							taxItemVO.setPk_taxitem("abcd12345678efaabd000114");
							taxItemVO.setTaxcode("114");
							taxItemVO.setTaxname("免税-货物及加工修理修配劳务");
						}
					}
				}
			}else{
				tzpzBVO.setVdef2("bsc");
				//这些都是进项
				if(!"小规模纳税人".equals(chargedeptname)){
					if(((invname.indexOf("*旅客运输*")>-1&&(slDbl.compareTo(new DZFDouble(0.09))==0||slDbl.compareTo(new DZFDouble(0.03))==0))||invname.indexOf("*运输服务*")>-1)
							&&date!=null&&new DZFDate(date).after(new DZFDate("2019-03-31"))){
						if(!StringUtil.isEmpty(invoicetype)&&(invoicetype.indexOf("增值税专用发票")>-1||invoicetype.indexOf("机动车销售统一发票")>-1||invoicetype.indexOf("通行费增值税电子普通发票")>-1)){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000144");
							taxItemVO.setTaxcode("144");
							taxItemVO.setTaxname("旅客运输服务进项税（专票）");
						}else if(!StringUtil.isEmpty(invoicetype)&&invoicetype.indexOf("增值税电子普通发票")>-1){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000143");
							taxItemVO.setTaxcode("143");
							taxItemVO.setTaxname("旅客运输服务进项税（非专票）");
						}
					}else if(!StringUtil.isEmpty(invoiceVO.getVmemo())&&invoiceVO.getVmemo().equals("收购(左上角标志)")){
						if(slDbl.compareTo(new DZFDouble(0.09))==0){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000142");
							taxItemVO.setTaxcode("142");
							taxItemVO.setTaxname("9%农产品收购发票");
						}else if(slDbl.compareTo(new DZFDouble(0.1))==0){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000136");
							taxItemVO.setTaxcode("136");
							taxItemVO.setTaxname("10%农产品收购发票");
						}else if(slDbl.compareTo(new DZFDouble(0.11))==0){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000129");
							taxItemVO.setTaxcode("129");
							taxItemVO.setTaxname("11%农产品收购发票");
						}else if(slDbl.compareTo(new DZFDouble(0.2))==0){
							taxItemVO.setPk_taxitem("abcd12345678efaabd000137");
							taxItemVO.setTaxcode("137");
							taxItemVO.setTaxname("12%农产品收购发票委托加工、生产销售");
						}
					}
				}
			}
			if(!StringUtil.isEmpty(taxItemVO.getPk_taxitem())){
				taxItemVO.setMny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemmny())));//金额
				taxItemVO.setTaxratio(new DZFDouble(OcrUtil.getInvoiceSL(detailVO.getItemtaxrate())).div(100));//税率
				taxItemVO.setTaxmny(new DZFDouble(OcrUtil.turnMnyByCurrency(detailVO.getItemtaxmny())));//税额
				List<PZTaxItemRadioVO> tax_items=new ArrayList<PZTaxItemRadioVO>();
				tax_items.add(taxItemVO);
				tzpzBVO.setTax_items(tax_items);
			}
		}else if(!"小规模纳税人".equals(chargedeptname)&&date!=null&&new DZFDate(date).after(new DZFDate("2019-03-31"))&&!StringUtil.isEmpty(istate) &&(istate.equals(ZncsConst.SBZT_2)&&(!StringUtil.isEmpty(invoiceVO.getStaffname()) || invoicetype.equals("c火车票") || invoicetype.equals("c航空运输电子客票行程单")))){
			tzpzBVO.setVdef2("bsc");
			
			String sl=invoiceVO.getTaxrate();
			DZFDouble slDbl=DZFDouble.ZERO_DBL;//税率
			if (!StringUtil.isEmpty(sl)) {
				sl = sl.replaceAll("%", "");
				try {
					slDbl = new DZFDouble(sl, 2).div(100);
				} catch (Exception e) {
				}
			}
			taxItemVO.setTaxratio(slDbl);//税率
			taxItemVO.setTaxmny(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny()))?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNtaxnmny())));//税额
			taxItemVO.setMny(StringUtil.isEmpty(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny()))?DZFDouble.ZERO_DBL:new DZFDouble(OcrUtil.turnMnyByCurrency(invoiceVO.getNmny())));//金额
			taxItemVO.setPk_taxitem("abcd12345678efaabd000143");
			taxItemVO.setTaxcode("143");
			taxItemVO.setTaxname("旅客运输服务进项税（非专票）");
			List<PZTaxItemRadioVO> tax_items=new ArrayList<PZTaxItemRadioVO>();
			tax_items.add(taxItemVO);
			tzpzBVO.setTax_items(tax_items);
		}
	}

	@Override
	public void saveVoucherAfter(TzpzHVO tzpzHVO) throws DZFWarpException {
		if(tzpzHVO == null){
			return ;
		}
		List<TzpzHVO> tzpzHVOs=new ArrayList<TzpzHVO>();
		tzpzHVOs.add(tzpzHVO);
		saveVouchersAfter(tzpzHVOs);
	}
	
	private void saveVouchersAfter(List<TzpzHVO> tzpzHVOs) throws DZFWarpException {
		DZFBoolean voucherFlag = getVoucherFlag();
		if(DZFBoolean.FALSE.equals(voucherFlag)||tzpzHVOs == null || tzpzHVOs.size() == 0||StringUtil.isEmpty(tzpzHVOs.get(0).getSourcebilltype())
				|| (!tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP59)
						&&!tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP70)
						&&!tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP75))
				){
			return ;
		}
		//59处理资产卡片
		CorpVO corp=corpService.queryByPk(tzpzHVOs.get(0).getPk_corp());
		Object userObject=tzpzHVOs.get(0).getUserObject();
		if(userObject==null)return;
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		if(tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP59)||tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP70)||tzpzHVOs.get(0).getSourcebilltype().equals(IBillTypeCode.HP75)){
			PzSourceRelationVO[] relVOs=tzpzHVOs.get(0).getSource_relation();
			if(relVOs==null||relVOs.length==0)return;
			List<String> jxPks=new ArrayList<String>();
			List<String> xxPks=new ArrayList<String>();
			List<String> zcPks=new ArrayList<String>();
			List<String> rkPks=new ArrayList<String>();
			List<String> ckPks=new ArrayList<String>();
			for (int i = 0; i < relVOs.length; i++) {
				PzSourceRelationVO pzSourceRelationVO = relVOs[i];
				if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP95)){
					jxPks.add(pzSourceRelationVO.getSourcebillid());
				}
				if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP90)){
					xxPks.add(pzSourceRelationVO.getSourcebillid());
				}
				if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP59)){
					zcPks.add(pzSourceRelationVO.getSourcebillid());
				}
				if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP70)){
					rkPks.add(pzSourceRelationVO.getSourcebillid());
				}
				if(pzSourceRelationVO.getSourcebilltype().equals(IBillTypeCode.HP75)){
					ckPks.add(pzSourceRelationVO.getSourcebillid());
				}
			}
			//来源是进项，如果也生成了进项，回写进项的状态
			if(jxPks.size()>0){
				sb.append("  update ynt_vatincominvoice y set y.pk_tzpz_h = ?,y.pzh = ? where ");
				sb.append(SqlUtil.buildSqlForIn("pk_vatincominvoice", jxPks.toArray(new String[0])));
				sp.addParam(tzpzHVOs.get(0).getPrimaryKey());
				sp.addParam(tzpzHVOs.get(0).getPzh());
				singleObjectBO.executeUpdate(sb.toString(), sp);
			}
			//来源是销项，如果也生成了销项，回写销项的状态
			if(xxPks.size()>0){
				sb=new StringBuffer();
				sp=new SQLParameter();
				sb.append("  update ynt_vatsaleinvoice y set y.pk_tzpz_h = ?,y.pzh = ? where ");
				sb.append(SqlUtil.buildSqlForIn("pk_vatsaleinvoice", xxPks.toArray(new String[0])));
				sp.addParam(tzpzHVOs.get(0).getPrimaryKey());
				sp.addParam(tzpzHVOs.get(0).getPzh());
				singleObjectBO.executeUpdate(sb.toString(), sp);
			}
//			//来源是资产卡片，看卡片有没有图
//			if(zcPks.size()>0){
//				sb=new StringBuffer();
//				sp=new SQLParameter();
//				sb.append("select * from ynt_assetcard where nvl(dr,0)=0 and pk_image_group is not null and "+SqlUtil.buildSqlForIn("pk_assetcard", zcPks.toArray(new String[0])));
//				List<AssetcardVO> cardList=(List<AssetcardVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(AssetcardVO.class));
//				Set<String> groupSet=new HashSet<String>();
//				if(cardList!=null&&cardList.size()>0){
//					for(int i=0;i<cardList.size();i++){
//						groupSet.add(cardList.get(i).getPk_image_group());
//					}
//					if(groupSet.size()>0){
//						//和图片组
//						String pk_image_group=iImageGroupService.processMergeGroup(tzpzHVOs.get(0).getPk_corp(), null, new ArrayList<String>(groupSet));
//					}
//				}
//			}
			//来源入库单的回写入库单主子表、进项
			if(rkPks.size()>0){
				sb=new StringBuffer();
				sp=new SQLParameter();
				sp.addParam(tzpzHVOs.get(0).getPrimaryKey());
				sp.addParam(tzpzHVOs.get(0).getPzh());
				sb.append("  update ynt_ictrade_h y set y.pzid = ?,y.pzh = ? where ");
				sb.append(SqlUtil.buildSqlForIn("pk_ictrade_h", rkPks.toArray(new String[0])));
				singleObjectBO.executeUpdate(sb.toString(), sp);//主表
				sb=new StringBuffer();
				sb.append("  update ynt_ictradein y set y.pk_voucher = ?,y.pzh = ? Where ");
				sb.append(SqlUtil.buildSqlForIn("pk_ictrade_h", rkPks.toArray(new String[0])));
				singleObjectBO.executeUpdate(sb.toString(), sp);//子表
				for(int i=0;i<rkPks.size();i++){
					sb=new StringBuffer();
					sp=new SQLParameter();
					sp.addParam(rkPks.get(i));
					sp.addParam(rkPks.get(i));
					sp.addParam(rkPks.get(i));
					sb.append("  update ynt_vatincominvoice y set y.pk_ictrade_h = ?,y.vicbillno = (select dbillid from ynt_ictrade_h where pk_ictrade_h=?) where ");
					sb.append("pk_vatincominvoice=(select sourcebillid from ynt_ictrade_h where pk_ictrade_h=? and sourcebilltype='HP95')");
					singleObjectBO.executeUpdate(sb.toString(), sp);//进项
				}
			}
			//来源出库单的回写出库单主子表、销项
			if(ckPks.size()>0){
				sb=new StringBuffer();
				sp=new SQLParameter();
				sp.addParam(tzpzHVOs.get(0).getPrimaryKey());
				sp.addParam(tzpzHVOs.get(0).getPzh());
				sb.append("  update ynt_ictrade_h y set y.pzid = ?,y.pzh = ? where ");
				sb.append(SqlUtil.buildSqlForIn("pk_ictrade_h", ckPks.toArray(new String[0])));
				singleObjectBO.executeUpdate(sb.toString(), sp);//主表
				sb=new StringBuffer();
				sb.append("  update ynt_ictradeout y set y.pk_voucher = ?,y.pzh = ? Where ");
				sb.append(SqlUtil.buildSqlForIn("pk_ictrade_h", ckPks.toArray(new String[0])));
				singleObjectBO.executeUpdate(sb.toString(), sp);//子表
				for(int i=0;i<ckPks.size();i++){
					sb=new StringBuffer();
					sp=new SQLParameter();
					sp.addParam(ckPks.get(i));
					sp.addParam(ckPks.get(i));
					sp.addParam(ckPks.get(i));
					sb.append("  update ynt_vatsaleinvoice y set y.pk_ictrade_h = ?,y.vicbillno = (select dbillid from ynt_ictrade_h where pk_ictrade_h=?) where ");
					sb.append("pk_vatsaleinvoice=(select sourcebillid from ynt_ictrade_h where pk_ictrade_h=? and sourcebilltype='HP90')");
					singleObjectBO.executeUpdate(sb.toString(), sp);//销项
				}
			}
		}
	}
	
	private List<OcrInvoiceVO> queryOcrInvoiceVOsByImageGroup(String pk_image_group)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_interface_invoice ");
		sb.append(" where nvl(dr,0)=0 and pk_image_group=? order by period");
		sp.addParam(pk_image_group);
		List<OcrInvoiceVO> invoiceList=(List<OcrInvoiceVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(OcrInvoiceVO.class));
		if(invoiceList.size()==0){
			String sql="select * from ynt_image_ocrlibrary where crelationid in(select pk_image_library from ynt_image_library where nvl(dr,0)=0 and pk_image_group=?)";
			List<OcrImageLibraryVO> ocrlibVO=(List<OcrImageLibraryVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(OcrImageLibraryVO.class));
			if(ocrlibVO!=null&&ocrlibVO.size()>0&&(ocrlibVO.get(0).getIstate()==10||ocrlibVO.get(0).getIstate()==0)){
				throw new BusinessException("票据正在识别中，请稍后再试！");
			}
			return null;
		}
		List<OcrInvoiceDetailVO> detailList=queryInvoiceDetail(invoiceList);
		Map<String, List<OcrInvoiceDetailVO>> detailMap = DZfcommonTools.hashlizeObject(detailList, new String[] { "pk_invoice" });
		for (int i = 0; i < invoiceList.size(); i++) {
			OcrInvoiceVO invoiceVO = invoiceList.get(i);
			invoiceVO.setChildren(detailMap.get(invoiceVO.getPk_invoice()).toArray(new OcrInvoiceDetailVO[0]));
		}
		return invoiceList;
	}

	@Override
	public void deleteVoucherAfter(TzpzHVO tzpzHVOs) throws DZFWarpException {
	}

	@Override
	public Map<String, Object> initVoucherParam(CorpVO corp,String period,boolean isBank) throws DZFWarpException {
		List<List<Object[]>> levelList = new ArrayList<List<Object[]>>();
		Set<String> zyFzhsList=new HashSet<String>();
		
		//1、缓存公司分类数据 2.5、按级次倒序排序的分类集合，合并用
		Map<String, Object[]> categoryMap = queryCategoryMap(period, corp.getPk_corp(), levelList,DZFBoolean.FALSE);
		if (categoryMap == null || categoryMap.size() == 0) {
			throw new BusinessException("没有符合制证条件的数据");
		}
		//2、辅助核算头
		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=queryFzhsHeadMap(corp.getPk_corp());
		//3、辅助核算表体
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=queryFzhsBodyMap(corp.getPk_corp(),zyFzhsList);
		//4、InventorySetVO
		InventorySetVO inventorySetVO=iInventoryAccSetService.query(corp.getPk_corp());
		if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO==null&&!isBank){
			throw new BusinessException("启用总账核算存货，请先设置存货成本核算方式！");
		}
		//5、辅助核算别名
		Map<String, InventoryAliasVO> fzhsBMMap=getFzhsBMMap(corp.getPk_corp(), inventorySetVO);
		//6、查公司参数
		List<Object> paramList = queryParams(corp.getPk_corp());
		//7、处理自定义票据
		//8、取币种
		Map<String, BdCurrencyVO> currMap=queryCurrencyVOMap();
		//9、汇率
		Map<String, Object[]> rateMap=queryExateVOMap(corp.getPk_corp());
		//10、取银行账号
		Map<String, String> bankAccountMap=queryBankAccountVOs(corp.getPk_corp());
		//11、取科目map
		Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(corp.getPk_corp());
		//12、查分类入账规则
		Map<String, List<AccsetVO>> accsetMap=queryAccsetVOMap(corp.getCorptype(),null);
		//13、查关键字入账规则
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=queryAccsetKeywordBVO2Map(corp.getCorptype(),null);
		//14、查集团科目
		Map<String, String> jituanSubMap=queryJituanSubj(corp.getCorptype());
		//15、获得当前公司行业编码，关键字规则用
		String tradeCode=getCurTradeCode(corp.getIndustry());
		//16、存货辅助核算
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=null;
		if(IcCostStyle.IC_INVTENTORY.equals(corp.getBbuildic())&&inventorySetVO!=null&&inventorySetVO.getChcbjzfs()==InventoryConstant.IC_CHDLHS){
			chFzhsBodyVOs=fzhsBodyMap.get("000001000000000000000006");//存货辅助核算值
		}
		//17、编码规则
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String newrule = gl_cpacckmserv.queryAccountRule(corp.getPk_corp());
		
		Map<String, Object> returnMap=new HashMap<>();
		returnMap.put("levelList", levelList);
		returnMap.put("categoryMap", categoryMap);
		returnMap.put("fzhsHeadMap", fzhsHeadMap);
		returnMap.put("zyFzhsList", zyFzhsList);
		returnMap.put("fzhsBodyMap", fzhsBodyMap);
		returnMap.put("inventorySetVO", inventorySetVO);
		returnMap.put("fzhsBMMap", fzhsBMMap);
		returnMap.put("paramList", paramList);
		returnMap.put("currMap", currMap);
		returnMap.put("rateMap", rateMap);
		returnMap.put("bankAccountMap", bankAccountMap);
		returnMap.put("assistMap", assistMap);
		
		returnMap.put("accsetMap", accsetMap);
		returnMap.put("accsetKeywordBVO2Map", accsetKeywordBVO2Map);
		returnMap.put("jituanSubMap", jituanSubMap);
		returnMap.put("tradeCode", tradeCode);
		returnMap.put("newrule", newrule);
		returnMap.put("chFzhsBodyVOs", chFzhsBodyVOs);
		return returnMap;
	}
	private List<TzpzHVO> createFZdyVoucherInvoice(List<OcrInvoiceVO> invoiceList,String pk_corp,Map<String, Object[]> categoryMap,List<Object> paramList,List<List<Object[]>> levelList,String pk_user,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, ImageGroupVO> groupMap,Map<String, CategorysetVO> categorysetMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap,Map<String, Map<String, Object>> checkMsgMap,InventorySetVO inventorySetVO,String collectCategory,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,Map<String, AuxiliaryAccountBVO> assistMap,Set<String> zyFzhsList,Map<String, InventoryAliasVO> fzhsBMMap
			,CorpVO corp,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, String> jituanSubMap,YntCpaccountVO[] accVOs
			,String tradeCode,String newrule,List<AuxiliaryAccountBVO> chFzhsBodyVOs,DZFBoolean isBankSubjLeaf)throws DZFWarpException{
		//1、要返回的凭证集合
		List<TzpzHVO> returnLists=new ArrayList<TzpzHVO>();
		//2、得到凭证Map
		Map<String, List<TzpzHVO>> tzpzMap=new HashMap<String, List<TzpzHVO>>();
		//3、得到票据Map
		Map<String, OcrInvoiceVO> invoiceVOMap=new HashMap<String, OcrInvoiceVO>();
		for(int i=0;i<invoiceList.size();i++){
			OcrInvoiceVO invoiceVO=invoiceList.get(i);
			//3处理银行账号暂时都是非强制
			checkBankCode(corp, invoiceVO, bankAccountMap, checkMsgMap,isBankSubjLeaf);
			//10、生成凭证VO
			TzpzHVO tzpzVO=creatTzpzVOByAccset(invoiceVO, StringUtil.isEmpty(invoiceVO.getPk_image_group())?null:groupMap.get(invoiceVO.getPk_image_group()), accsetMap, accsetKeywordBVO2Map, categorysetMap, categoryMap, paramList,corp,pk_user,bankAccountMap,jituanSubMap,newrule,accountMap,accVOs,currMap,rateMap,checkMsgMap,inventorySetVO,tradeCode,chFzhsBodyVOs,fzhsHeadMap,fzhsBodyMap,zyFzhsList,fzhsBMMap);
			if(tzpzMap.containsKey(invoiceVO.getPk_billcategory())){
				tzpzMap.get(invoiceVO.getPk_billcategory()).add(tzpzVO);
			}else{
				List<TzpzHVO> tmpList=new ArrayList<TzpzHVO>();
				tmpList.add(tzpzVO);
				tzpzMap.put(invoiceVO.getPk_billcategory(), tmpList);
			}
			invoiceVOMap.put(invoiceVO.getPk_invoice(), invoiceVO);
		}
		//11、合并凭证及分录 categorysetMap tzpzMap categoryMap paramList pk_category pk_bills
		List<VatInvoiceSetVO> vatSetVOList=(List<VatInvoiceSetVO>)paramList.get(1);//这里面有用的是摘要
		for(int i=0;i<levelList.size();i++){//一个有多少个level
			List<Object[]> categoryList=levelList.get(i);//得到这个level的所以分类
			for(int j=0;j<categoryList.size();j++){
				Object[] categoryObj=categoryList.get(j);//得到一个分类
				List<TzpzHVO> tmpTzpzList=tzpzMap.get(categoryObj[0]);//这个分类的凭证
				if(tmpTzpzList!=null&&tmpTzpzList.size()>0){
					List<TzpzHVO> newTmpTzpzList=(List<TzpzHVO>)OcrUtil.clonAll(tmpTzpzList);
					CategorysetVO categorySetVO=categorysetMap.get(categoryObj[0]);//分类对应的编辑目录
					DZFBoolean isleaf=new DZFBoolean(categoryObj[6]==null?"N":categoryObj[6].toString());
					int categorytype=categoryObj[7]==null?0:Integer.parseInt(categoryObj[7].toString());
					String categoryname=categoryObj[5]==null?"":categoryObj[5].toString();
					newTmpTzpzList=mergeTzpz(newTmpTzpzList, categorySetVO, vatSetVOList,invoiceVOMap,categoryMap,currMap,assistMap,categoryObj[1].toString(),isleaf,(ParaSetVO)paramList.get(0),categorytype,categoryname);
					//当前级次合并完，如果有上级把结果put到父级
					String pk_parentcategory=(String)categoryMap.get(categoryObj[0])[2];
					tzpzMap.put((String)categoryObj[0], newTmpTzpzList);
					if(!StringUtil.isEmpty(pk_parentcategory)){
						if(tzpzMap.get(pk_parentcategory)==null){
							tzpzMap.put(pk_parentcategory, (List<TzpzHVO>)OcrUtil.clonAll(newTmpTzpzList));
						}else{
							tzpzMap.get(pk_parentcategory).addAll(newTmpTzpzList);
						}
					}
				}
			}
		}
		//至此已经所有的level都合并完，取levelList的最后一个结果返回
		if(StringUtil.isEmpty(collectCategory)){
			List<Object[]> returnListObj=levelList.get(levelList.size()-1);
			for(int i=0;i<returnListObj.size();i++){
				if(tzpzMap.get(returnListObj.get(i)[0])!=null){
					returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
				}
			}
		}else{
			for (int p = 0; p < levelList.size(); p++) {
				List<Object[]> returnListObj=levelList.get(p);
				for(int i=0;i<returnListObj.size();i++){
					if(collectCategory.startsWith("bank_")&&tzpzMap.get(returnListObj.get(i)[0])!=null){
						if(p==levelList.size()-2){//点中银行票据分组下的账号分组生成，把第一级的分类都加起来再返回
							returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
						}
					}
					if(collectCategory.equals(returnListObj.get(i)[0])&&tzpzMap.get(returnListObj.get(i)[0])!=null){
						returnLists.addAll(tzpzMap.get(returnListObj.get(i)[0]));
						break;
					}
				}
				if(returnLists.size()>0)break;
			}
		}
		//12、参数：是否合并分录
//		if(DZFBoolean.TRUE.equals(((ParaSetVO)paramList.get(0)).getIsmergedetail())){
		if(true){//永远合并
			mergeTzpzDetail(returnLists, currMap, assistMap, null);
		}else{
			if(((ParaSetVO)paramList.get(0)).getOrderdetail()==1){//先借后贷
				sortTzpzDetail(returnLists);
			}
		}
		//13、处理凭证号，设置表头余额
		returnLists=setTzpzOtherInfo(returnLists,categoryMap,invoiceList);
		//14、返回凭证
		return returnLists;
	}
	
	/**
	 * 判断银行存款科目是否是末级科目
	 * @return
	 */
	private DZFBoolean isBankSubjLeaf(String pk_corp){
		YntCpaccountVO[] accVOs=accountService.queryByPk(pk_corp);
		DZFBoolean isLeaf=DZFBoolean.FALSE;
		for (int i = 0; i < accVOs.length; i++) {
			YntCpaccountVO yntCpaccountVO = accVOs[i];
			if(yntCpaccountVO.getAccountcode().equals("1002")){
				if(yntCpaccountVO.getIsleaf()!=null&&DZFBoolean.TRUE.equals(yntCpaccountVO.getIsleaf())){
					isLeaf=DZFBoolean.TRUE;
				}
				break;
			}
		}
		return isLeaf;
	}
}

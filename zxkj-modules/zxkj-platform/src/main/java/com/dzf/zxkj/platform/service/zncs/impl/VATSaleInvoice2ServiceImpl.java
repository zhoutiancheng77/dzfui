package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.*;
import com.dzf.zxkj.common.entity.Grid;
import com.dzf.zxkj.common.enums.IFpStyleEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.*;
import com.dzf.zxkj.platform.model.bdset.*;
import com.dzf.zxkj.platform.model.glic.InventoryAliasVO;
import com.dzf.zxkj.platform.model.glic.InventorySetVO;
import com.dzf.zxkj.platform.model.icset.*;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongBVO;
import com.dzf.zxkj.platform.model.piaotong.CaiFangTongHVO;
import com.dzf.zxkj.platform.model.pjgl.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.tax.TaxitemParamVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.*;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccAliasService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.icbill.ISaleoutService;
import com.dzf.zxkj.platform.service.icset.IInvAccSetService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.IAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import com.dzf.zxkj.platform.service.sys.IParameterSetService;
import com.dzf.zxkj.platform.service.tax.ITaxitemsetService;
import com.dzf.zxkj.platform.service.zncs.*;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.OcrUtil;
import com.dzf.zxkj.platform.util.zncs.ZncsConst;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

@Slf4j
@Service("gl_vatsalinvact2")
public class VATSaleInvoice2ServiceImpl implements IVATSaleInvoice2Service {


	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private YntBoPubUtil yntBoPubUtil;
	@Autowired
	private IVoucherService voucher;
	@Autowired
	private IBillcategory iBillcategory;
	// @Autowired
	// private ICbComconstant gl_cbconstant;//科目方案
	// @Autowired
	// private ICpaccountService gl_cpacckmserv;
	// @Autowired
	// private ICpaccountService cpaccountService;
	@Autowired
	private IBankStatement2Service gl_yhdzdserv2;
	@Autowired
	IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IDcpzService dcpzjmbserv;
	@Autowired
	private IZncsVoucher zncsVoucher;
	@Autowired
	private ICaiFangTong2Service caifangtongserv2;
	@Autowired
	private IPiaoTongKp2Service piaotongkpserv2;
	@Autowired
	private ITaxitemsetService taxitemserv;
	// @Autowired
	// private ITaxitemsetService sys_taxsetserv;
	@Autowired
	private ISaleoutService ic_saleoutserv;
	@Autowired
	private IInventoryService invservice;
	@Autowired
	private IAutoMatchName ocr_atuomatch;
	@Autowired
	private IMeasureService measervice;
	@Autowired
	private IInventoryAccAliasService gl_ic_invtoryaliasserv;
	@Autowired
	private IParameterSetService parameterserv;
	@Autowired
	private IInvAccSetService ic_chkmszserv;
	@Autowired
	private IQmgzService qmgzService;
	@Autowired
	private CheckInventorySet inventory_setcheck;
	@Autowired
	private IImageGroupService img_groupserv;
	@Autowired
	private IQmclService gl_qmclserv;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv;
	@Autowired
	private ISchedulCategoryService schedulCategoryService;
	@Autowired
	private IInventoryService inventoryservice;
	@Autowired
	private IParameterSetService sys_parameteract;
	private static final String SMALL_TAX = "小规模纳税人";
	@Autowired
	private IInterfaceBill ocrinterface;
	@Autowired
	ICpaccountService gl_cpacckmserv;
	@Autowired
    private ICorpService corpService;
	@Autowired
	private IAccountService accountService;
	@Autowired
	private RedissonDistributedLock redissonDistributedLock;
	// private static final String SALE = "SALE";
	// private static final String LABOUR = "LABOUR";
	// private static final String TAX = "TAX";
	// private static final String SALEACCOUNTCODE_07 = "600101";//商品销售收入编码
	// private static final String LABOURACCOUNTCODE_07 = "600102";//提供劳务收入编码
	// private static final String SALEACCOUNTCODE_13 = "500101";//商品销售收入编码
	// private static final String LABOURACCOUNTCODE_13 = "500102";//提供劳务收入编码
	// private static final String OUTPUTTAXACCOUNTCODE = "22210102";//销项税额编码

	// private static final String ACCOUNTRECEIVABLENAME = "应收账款";//1122

	// private static int[] oldRule = new int[] { 4, 2, 2, 2, 2, 2 };

	// 对照关系
	private static final Integer BAI_WANG_A = 0;
	private static final Integer BAI_WANG_B = 1;
	private static final Integer HANG_TIAN = 2;
	private static final Integer NO_IMP = 3;
	// public static Map<String, String> BW_STYLE = null;
	// public static Map<String, String> HT_STYLE = null;
	// private static List<Map<String,String>> STYLE_LIST = null;

//	private static String STR_DANJU = "单据号";
	// excel对照关系
	private static final Integer TONGYONG_EXCEL = 0;// 通用excel
	private static final Integer BAIWANG_EXCEL = 1;// 百旺excel
	private static final Integer BAIWANG_JDC_EXCEL = 12;// 百旺机动车excel
	private static final Integer BAIWANG_another_EXCEL = 11;// 百旺其他的excel
	private static final Integer BAIWANG_NEW_EXCEL = 13;// 百旺新版销项导入excel				20191204
	private static final Integer BAIWANG_NEW_another_EXCEL = 14;// 百旺新版其他的excel		20191204

	private static final Integer XINLONG_EXCEL = 15;// 新龙excel		20191211

	private static final Integer HANG_TIAN_EXCEL = 2;// 航信
	private static final Integer HANG_JDC_EXCEL = 22;// 航信机动车
	// private static int[][] fp_dm_arr = {//匹配发票代码 前两个格子是坐标，后一个格子是文件来源
	// {5, 7, BAIWANG_EXCEL},//百旺excel导入
	// {5, 8, HANG_TIAN_EXCEL}//航信excel导入
	// };

	private static final Integer ifptype = 0;//// 0 销项 1进项

	private static String VAT_ZHUAN = "专票";// xml导入使用
	private static String VAT_PU = "普票";// xml导入使用
	private static String VAT_EXC_ZHUAN = "专用发票";// excel导入使用
	private static String VAT_EXC_PU = "普通发票";// excel导入使用
	private static String VAT_EXC_DIANZI = "电子发票";// excel导入使用		20191204增加

	@Override
	public List<VATSaleInvoiceVO2> quyerByPkcorp(InvoiceParamVO paramvo, String sort, String order)
			throws DZFWarpException {
		// try {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append(
				" select pk_vatsaleinvoice,y.coperatorid,y.doperatedate,y.pk_corp,y.batchflag,y.invmodel,y.invstatus,y.iszhuan,y.fp_hm,y.fp_dm,y.khmc,y.spmc,y.spsl, y.inperiod, ");
		sb.append(
				" y.spse,y.hjje,y.jshj,y.yfp_hm,y.yfp_dm,y.noticebillno,y.kprname,y.kprid,y.kprj,y.zfrname,y.zfrid,y.zfrj,y.custidentno,y.pk_tzpz_h, nvl(d.pk_category,e.pk_model_h) as pk_model_h, y.pk_image_group,y.ioperatetype,y.isettleway, h.vicbillcode vicbillno, ");//y.isic, 
		sb.append(
				" y.pk_subject,y.period,y.billstatus,y.sourcetype,y.modifyoperid,y.modifydatetime,y.dr,y.ts, y.imgpath, y.kplx, h.pzh,  nvl(y.busitypetempname,e.busitypetempname) as busitypetempname, ic.dbillid, ic.pk_ictrade_h ");
		sb.append(" from ynt_vatsaleinvoice y ");
		sb.append("   left join ynt_billcategory d ");
		sb.append("     on y.pk_model_h = d.pk_category ");
		sb.append(" left join ynt_dcmodel_h e on y.pk_model_h = e.pk_model_h ");
		sb.append("   left join ynt_ictrade_h ic ");
		sb.append("     on y.pk_ictrade_h = ic.pk_ictrade_h ");
		sb.append("   left join ynt_tzpz_h h ");
		sb.append("     on y.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append(" where y.pk_corp=? and nvl(y.dr,0)=0");
		sp.addParam(paramvo.getPk_corp());

		if (StringUtils.isNotEmpty(paramvo.getIszh())) {
			sb.append(" and y.iszhuan = ? ");
			sp.addParam(paramvo.getIszh());
		}
		
		if(!StringUtil.isEmpty(paramvo.getIspz())){
			if("Y".equals(paramvo.getIspz())){
				sb.append(" and y.pk_tzpz_h is not null ");
			}else{
				sb.append(" and y.pk_tzpz_h is null ");
			}
		}

//		if (paramvo.getBegindate() != null) {
//			sb.append(" and y.kprj >= ? ");
//			sp.addParam(paramvo.getBegindate());
//		}
//
//		if (paramvo.getEnddate() != null) {
//			sb.append(" and y.kprj <= ? ");
//			sp.addParam(paramvo.getEnddate());
//		}
		
		if ("serDay".equals(paramvo.getSerdate())) {
			if (paramvo.getBegindate() != null) {
				sb.append(" and y.kprj >= ? ");
				sp.addParam(paramvo.getBegindate());
			}

			if (paramvo.getEnddate() != null) {
				sb.append(" and y.kprj <= ? ");
				sp.addParam(paramvo.getEnddate());
			}
			if ("id".equals(sort)) {
				sort = "skprj";// 开票日期
			}
		} else if ("serPer".equals(paramvo.getSerdate()) && !StringUtil.isEmpty(paramvo.getStartYear2())
				&& !StringUtil.isEmpty(paramvo.getStartMonth2())) {// 入账期间
			sb.append(" and inperiod = ? ");
			String inperiod = paramvo.getStartYear2() + "-" + paramvo.getStartMonth2();
			sp.addParam(inperiod);
		}

		if (paramvo.getIoperatetype() != null) {// 操作类型
			sb.append(" and y.ioperatetype = ? ");
			sp.addParam(paramvo.getIoperatetype());
		}

		if (!StringUtil.isEmpty(paramvo.getKpxm())) {
			sb.append(" and spmc like ").append("'%").append(paramvo.getKpxm()).append("%'");
		}
		if (!StringUtil.isEmpty(paramvo.getFphm())) {
			sb.append(" and fp_hm like ").append("'%").append(paramvo.getFphm()).append("%'");
		}

		if (!StringUtil.isEmpty(paramvo.getFpdm())) {
			sb.append(" and fp_dm like ").append("'%").append(paramvo.getFpdm()).append("%'");
		}

		if (!StringUtil.isEmpty(sort)) {// sort != null && !"".equals(sort)
			String sortb = FieldMapping.getFieldNameByAlias(new VATSaleInvoiceVO2(), sort);
			order = " order by " + (sortb == null ? sort : sortb) + " " + order + ", y.rowid " + order;
			sb.append(order);
		}
		List<VATSaleInvoiceVO2> listVo = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));// retrieveByClause(VATSaleInvoiceVO2.class,
																// sb.toString(),
																// sp);

		// filterData(listVo);
		return listVo;

	}

	// private void filterData(List<VATSaleInvoiceVO2> listVo){
	// if(listVo == null || listVo.size() == 0)
	// return;
	//
	// for(VATSaleInvoiceVO2 vo : listVo){
	// if(!StringUtil.isEmpty(vo.getAccountname())
	// && "_".equals(vo.getAccountname())){
	// vo.setAccountname("");
	// }
	// }
	// }



	@Override
	public void delete(VATSaleInvoiceVO2 vo, String pk_corp) throws DZFWarpException {
		// 删除前数据安全验证
		VATSaleInvoiceVO2 getmsvo = (VATSaleInvoiceVO2)singleObjectBO.queryByPrimaryKey(VATSaleInvoiceVO2.class, vo.getPrimaryKey());

		if (getmsvo == null || vo.getPk_corp() == null
				|| !pk_corp.equals(vo.getPk_corp())) {
			throw new BusinessException("正在处理中，请刷新重试！");
		}
		if(!StringUtils.isEmpty(getmsvo.getImgpath())){
			throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
		}
//		DZFBoolean isic = vo.getIsic();
//		if (isic != null && isic.booleanValue()) {
//			throw new BusinessException("单据已生成出库单，请检查");
//		}
		String pk_ictrade_h = vo.getPk_ictrade_h();
		if(!StringUtil.isEmpty(pk_ictrade_h)){
			throw new BusinessException("单据已生成出库单，请检查");
		}

		if (!StringUtil.isEmpty(vo.getPzh()) || !StringUtil.isEmpty(vo.getPk_tzpz_h()))
			throw new BusinessException("单据已生成凭证，请检查。");

		singleObjectBO.deleteObjectByID(vo.getPrimaryKey(),
				new Class[] { VATSaleInvoiceVO2.class, VATSaleInvoiceBVO2.class });

//		StringBuffer strb = new StringBuffer();
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(PhotoState.state0);
//		sp.addParam(getmsvo.getSourcebillid());
//		strb.append(" update  ynt_image_group p  set istate=? where p.pk_image_group in ");
//		strb.append(" (select pk_image_group from  ynt_image_library y where y.pk_image_library =? and nvl(dr,0)=0 ) ");
//		singleObjectBO.executeUpdate(strb.toString(), sp);

		deleteRelationVO(getmsvo);
	}

	private void deleteRelationVO(VATSaleInvoiceVO2 vo) {
		if ((ICaiFangTongConstant.LYDJLX.equals(vo.getSourcebilltype())
				|| ICaiFangTongConstant.LYDJLX_PTKP.equals(vo.getSourcebilltype()))
				&& !StringUtil.isEmpty(vo.getSourcebillid())) {
			singleObjectBO.deleteObjectByID(vo.getSourcebillid(),
					new Class[] { CaiFangTongHVO.class, CaiFangTongBVO.class });// 财房通主子表dr=1
		}
	}
	@Override
	public VATSaleInvoiceVO2[] updateVOArr(String pk_corp, Map<String, VATSaleInvoiceVO2[]> sendData)
			throws DZFWarpException {
		List<VATSaleInvoiceVO2> ll = new ArrayList<VATSaleInvoiceVO2>();
		VATSaleInvoiceVO2[] addvos = sendData.get("adddocvos");
		VATSaleInvoiceVO2[] updvos = sendData.get("upddocvos");
		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();


		SQLParameter params = new SQLParameter();
		params.addParam(pk_corp);
		params.addParam(addvos!=null && addvos.length>0 ? addvos[0].getInperiod() : updvos[0].getInperiod());

		BillCategoryVO[] categoryvos = (BillCategoryVO[])singleObjectBO.queryByCondition(BillCategoryVO.class, "pk_corp=? and period=? and nvl(dr,0)=0 and isaccount = 'N'", params);
		Map<String, BillCategoryVO> mapcategory = new HashMap<String, BillCategoryVO>();
		for (BillCategoryVO vo : categoryvos)
		{
			mapcategory.put(vo.getPrimaryKey(), vo);
		}

		//新增修改的合在一起
		if(addvos!=null && addvos.length>0){
			for (VATSaleInvoiceVO2 VATSaleInvoiceVO2 : addvos) {
				VATSaleInvoiceVO2.setVersion(new DZFDouble(1.0));
				//查询业务类型所属期间是否是入账期间
				if (StringUtil.isEmpty(VATSaleInvoiceVO2.getPk_model_h()) == false)
				{
					BillCategoryVO categoryvo = mapcategory.get(VATSaleInvoiceVO2.getPk_model_h());//(BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, VATSaleInvoiceVO2.getPk_model_h());
					if (categoryvo == null)
					{
						throw new BusinessException("业务类型不正确，请重新选择");
					}
					if (VATSaleInvoiceVO2.getInperiod().equals(categoryvo.getPeriod()) == false)
					{
						throw new BusinessException("业务类型所属期间与入账期间不一致，请重新选择一下业务类型");
					}
				}
				//解决导入时销方名称为空的bug
				if(StringUtils.isEmpty(VATSaleInvoiceVO2.getXhfmc())){
					CorpVO corpVO = corpService.queryByPk(pk_corp);
					VATSaleInvoiceVO2.setXhfmc(corpVO.getUnitname());
				}
				
				VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[])VATSaleInvoiceVO2.getChildren();
				if(bvos!=null&&bvos.length>0){
					for (int i = 0; i < bvos.length; i++) {

						VATSaleInvoiceBVO2 bvo = bvos[i];
						if (StringUtil.isEmptyWithTrim(bvo.getPk_billcategory()) == false && mapcategory.containsKey(bvo.getPk_billcategory()) == false)
						{
							throw new BusinessException("第 " + (i + 1) + " 行业务类型不正确，请重新选择");
						}

						//录入表体业务类型后保存，表头取表体第一行业务类型
						if(bvo.getRowno().equals(1)&&StringUtils.isEmpty(VATSaleInvoiceVO2.getPk_model_h())&&!StringUtils.isEmpty(bvo.getPk_billcategory())){
							VATSaleInvoiceVO2.setPk_model_h(bvo.getPk_billcategory());
							VATSaleInvoiceVO2.setBusitypetempname(bvo.getBillcategoryname());
						}
						//处理表头有业务类型，表体没有
						if(!StringUtils.isEmpty(VATSaleInvoiceVO2.getPk_model_h())&&StringUtils.isEmpty(bvo.getPk_billcategory())){
							bvo.setPk_billcategory(VATSaleInvoiceVO2.getPk_model_h());
						}
					}
				}
				if(StringUtils.isEmpty(VATSaleInvoiceVO2.getPk_model_h())){//导入的时候为空
					ll.add(VATSaleInvoiceVO2);
					
				}else{
					list.add(VATSaleInvoiceVO2);
				}
				
			}
		}
		if(updvos!=null && updvos.length>0){
			for (VATSaleInvoiceVO2 VATSaleInvoiceVO2 : updvos) {
				VATSaleInvoiceVO2.setVersion(new DZFDouble(1.0));
				
				//查询业务类型所属期间是否是入账期间
				if (StringUtil.isEmpty(VATSaleInvoiceVO2.getPk_model_h()) == false)
				{
					BillCategoryVO categoryvo = mapcategory.get(VATSaleInvoiceVO2.getPk_model_h());// (BillCategoryVO)singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, VATSaleInvoiceVO2.getPk_model_h());
					if (categoryvo == null)
					{
						throw new BusinessException("业务类型不正确，请重新设置");
					}
					if (VATSaleInvoiceVO2.getInperiod().equals(categoryvo.getPeriod()) == false)
					{
						throw new BusinessException("业务类型所属期间与入账期间不一致，请重新选择一下业务类型");
					}
					//修改时重新设置结算方式，结算科目，入账科目
					CategorysetVO setVO = gl_yhdzdserv2.queryCategorySetVO(VATSaleInvoiceVO2.getPk_model_h());
					if(StringUtils.isEmpty(VATSaleInvoiceVO2.getBusisztypecode())){
						VATSaleInvoiceVO2.setSettlement(setVO.getSettlement()==null?0:setVO.getSettlement());
					}else{
						VATSaleInvoiceVO2.setSettlement(Integer.parseInt(VATSaleInvoiceVO2.getBusisztypecode()));
					}
					VATSaleInvoiceVO2.setPk_subject(setVO.getPk_accsubj());
					VATSaleInvoiceVO2.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
					VATSaleInvoiceVO2.setPk_taxaccsubj(setVO.getPk_taxaccsubj());
					
				}

				if (VATSaleInvoiceVO2.getChildren() != null)
				{
					for (int i = 0; i < VATSaleInvoiceVO2.getChildren().length; i++)
					{
						VATSaleInvoiceBVO2 bvo = (VATSaleInvoiceBVO2)VATSaleInvoiceVO2.getChildren()[i];
						if (StringUtil.isEmpty(bvo.getPk_billcategory()) == false && mapcategory.containsKey(bvo.getPk_billcategory()) == false)
						{
							throw new BusinessException("第 " + (i + 1) + " 行业务类型不正确，请重新选择");
						}
					}
				}

				if(StringUtils.isEmpty(VATSaleInvoiceVO2.getPk_model_h())){
					VATSaleInvoiceVO2.setPk_category_keyword(null);
					VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[])VATSaleInvoiceVO2.getChildren(); 
					if (bvos != null)
					{
						for (VATSaleInvoiceBVO2 bvo : bvos)
						{
							bvo.setPk_billcategory(null);
							bvo.setPk_category_keyword(null);
						}
					}
					ll.add(VATSaleInvoiceVO2);
				}else{
					VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[])VATSaleInvoiceVO2.getChildren(); 
					if (bvos != null)
					{
						for (VATSaleInvoiceBVO2 bvo : bvos)
						{
							if (StringUtil.isEmpty(bvo.getPk_billcategory()))
							{
								bvo.setPk_billcategory(VATSaleInvoiceVO2.getPk_model_h());
								bvo.setPk_category_keyword(null);
							}
						}
					}
					list.add(VATSaleInvoiceVO2);
				}
				
			}
		}
		
		List<VATSaleInvoiceVO2> list2= new ArrayList<>();
		//设置业务类型
		if(ll!=null&&ll.size()>0){
			List<VATSaleInvoiceVO2> toSale = changeToSale(ll, pk_corp);
			list.addAll(toSale);
		}
		resetSpsl(list.toArray(new VATSaleInvoiceVO2[0]));//设置税率
		for (VATSaleInvoiceVO2 salevo : list) {
			salevo.setVersion(new DZFDouble(1.0));
			
			
			checkOcrIsHasRepeation(pk_corp, salevo);
			List<VATSaleInvoiceVO2> changeList = new ArrayList<VATSaleInvoiceVO2>();
			VATSaleInvoiceVO2 oldvo=null;
			if(!StringUtils.isEmpty(salevo.getPk_vatsaleinvoice())){
				oldvo = queryByID(salevo.getPk_vatsaleinvoice());
				salevo.setSourcetype(oldvo.getSourcetype());	
			}
			//修改才能自学习
			if (!StringUtil.isEmpty(salevo.getPrimaryKey())&&!StringUtils.isEmpty(salevo.getPk_category_keyword()) && !StringUtils.isEmpty(salevo.getPk_model_h())) {// 有没有修改分类
				//查询修改前的分类主键
				
				if(StringUtils.isEmpty(oldvo.getPk_model_h()) == false && !salevo.getPk_model_h().equals(oldvo.getPk_model_h()) 
						&& !StringUtils.isEmpty(oldvo.getPk_category_keyword())){
					changeList.add(oldvo);
					List<OcrInvoiceVO> OcrInvoiceVOList = changeToOcr(changeList, pk_corp);
					for (OcrInvoiceVO ocrInvoiceVO : OcrInvoiceVOList) {
						ocrInvoiceVO.setPk_invoice(null);
					}
					iBillcategory.saveNewCategroy(OcrInvoiceVOList.toArray(new OcrInvoiceVO[0]),salevo.getPk_model_h(), pk_corp, OcrInvoiceVOList.get(0).getPeriod());
				}
			}
			salevo.setPk_corp(pk_corp);
			if(!StringUtil.isEmpty(salevo.getPrimaryKey())){
				singleObjectBO.executeUpdate("update ynt_vatsaleinvoice_b set dr=1 where pk_vatsaleinvoice='"+salevo.getPrimaryKey()+"'", new SQLParameter());
			}

			VATSaleInvoiceVO2 vo = (VATSaleInvoiceVO2)singleObjectBO.saveObject(pk_corp, salevo);
			list2.add(vo);

		}
		
		return list2.toArray(new VATSaleInvoiceVO2[0]);
	}
		
	public VATSaleInvoiceVO2[] updateVOArr2(String pk_corp, Map<String, VATSaleInvoiceVO2[]> sendData)
			throws DZFWarpException {

		VATSaleInvoiceVO2[] rtvos = null;
		List<VATSaleInvoiceVO2> nolist = new ArrayList<VATSaleInvoiceVO2>();
		List<VATSaleInvoiceVO2> yeslist = new ArrayList<VATSaleInvoiceVO2>();
		Map<String, String> repMap = new HashMap<String, String>();
		String hm = null;
		VATSaleInvoiceVO2[] addvos = sendData.get("adddocvos");

		if (addvos != null && addvos.length > 0) {
			// 如是新增， 需返回新增vos
			String[] addpks = null;
			SuperVO[] bvos = null;

			resetSpsl(addvos);
			List<SuperVO> bvoList = new ArrayList<SuperVO>();
			for (int i = 0; i < addvos.length; i++) {
				if(StringUtils.isEmpty(addvos[i].getPk_model_h())){
					nolist.add(addvos[i]);
				}else{
					yeslist.add(addvos[i]);
				}
			}
			if(nolist!=null&&nolist.size()>0){
				List<VATSaleInvoiceVO2> toInCom = changeToSale(nolist, pk_corp);
				yeslist.addAll(toInCom);
			}
			addpks = singleObjectBO.insertVOArr(pk_corp,yeslist.toArray(new VATSaleInvoiceVO2[0]));
			for (int i = 0; i < addvos.length; i++) {
				hm = addvos[i].getFp_hm();
				repMap.put(hm, hm);

				bvos = addvos[i].getChildren();
				if (bvos != null && bvos.length > 0) {
					for (SuperVO bvo : bvos) {
						bvo.setAttributeValue(bvo.getParentPKFieldName(), addpks[i]);
						bvoList.add(bvo);
					}
				}
			}

			if (bvoList.size() > 0) {
				singleObjectBO.insertVOArr(pk_corp, bvoList.toArray(new SuperVO[0]));
			}

			rtvos = addvos;
		}

		VATSaleInvoiceVO2[] updvos = sendData.get("upddocvos");

		if (updvos != null && updvos.length > 0) {// 单行更新

			resetSpsl(updvos);

			String pk = null;
			VATSaleInvoiceVO2 oldvo = null;
			SuperVO[] oldbvos = null;
			SuperVO[] newbvos = null;
			for (VATSaleInvoiceVO2 vo : updvos) {
				pk = vo.getPrimaryKey();

				hm = vo.getFp_hm();
				repMap.put(hm, hm);

				newbvos = vo.getChildren();
				for (SuperVO bvo : newbvos) {
					bvo.setAttributeValue(bvo.getParentPKFieldName(), pk);
				}
				List<VATSaleInvoiceVO2> newList = new ArrayList<VATSaleInvoiceVO2>();
				List<VATSaleInvoiceVO2> bList = new ArrayList<VATSaleInvoiceVO2>();
				List<VATSaleInvoiceVO2> changeList = new ArrayList<VATSaleInvoiceVO2>();
				bList.add(vo);
				if(StringUtils.isEmpty(vo.getPk_model_h())){//没有分类走自动分类
					//转换分类
					List<VATSaleInvoiceVO2> inComList = changeToSale(bList, pk_corp);
					newList.addAll(inComList);
				}else{
					newList.add(vo);
				}
				//自学习
				if (!StringUtils.isEmpty(vo.getPk_category_keyword()) && !StringUtils.isEmpty(vo.getPk_model_h())) {// 有没有修改分类
					//查询修改前的分类主键
					VATSaleInvoiceVO2 oldVO = queryByID(vo.getPk_vatsaleinvoice());
					if(!vo.getPk_model_h().equals(oldVO.getPk_model_h())){
						changeList.add(oldVO);
						List<OcrInvoiceVO> OcrInvoiceVOList = changeToOcr(changeList, pk_corp);
						for (OcrInvoiceVO ocrInvoiceVO : OcrInvoiceVOList) {
							ocrInvoiceVO.setPk_invoice(null);
						}
						iBillcategory.saveNewCategroy(OcrInvoiceVOList.toArray(new OcrInvoiceVO[0]),
								vo.getPk_model_h(), pk_corp, OcrInvoiceVOList.get(0).getPeriod());
					}
					
				}
				singleObjectBO.update(vo,
						new String[] { "iszhuan", "fp_hm", "fp_dm", "khmc", "spmc", "spsl", "spse", "hjje", "jshj",
								"kprj", "period", "modifyoperid", "modifydatetime", "demo", "pk_model_h", "xhfmc",
								"xhfsbh", "xhfdzdh", "xhfyhzh", "ghfdzdh", "ghfyhzh", "custidentno", "inperiod" });
				if (!StringUtil.isEmpty(pk)) {
					oldvo = queryByID(pk);
					oldbvos = oldvo.getChildren();
					if (oldbvos != null && oldbvos.length > 0) {
						singleObjectBO.deleteVOArray(oldbvos);
					}

					// 赋值
					vo.setPk_corp(oldvo.getPk_corp());
				}
				singleObjectBO.insertVOArr(vo.getPk_corp(), newbvos);
			}

			rtvos = updvos;
		}

		checkIsHasRepeation(pk_corp, repMap);

		return rtvos;
	}

	// 重新设置税率
	private void resetSpsl(VATSaleInvoiceVO2[] vos) {
		if (vos == null || vos.length == 0)
			return;

		VATSaleInvoiceBVO2[] bvos;
		for (VATSaleInvoiceVO2 vo : vos) {
			bvos = (VATSaleInvoiceBVO2[]) vo.getChildren();
			if (bvos != null && bvos.length > 0) {
				vo.setSpsl(bvos[0].getBspsl());// 设置税率
			}
		}
	}

	/**
	 * 校验数据是否重复
	 * 
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void checkIsHasRepeation(String pk_corp, Map<String, String> repMap) throws DZFWarpException {

		if (repMap == null || repMap.size() == 0)
			return;

		int i = 0;
		String[] fieldValue = new String[repMap.size()];
		for (Map.Entry<String, String> entry : repMap.entrySet()) {
			fieldValue[i++] = entry.getValue();
		}
		String wherePart = SqlUtil.buildSqlForIn("fp_hm", fieldValue);

		StringBuffer sf = new StringBuffer();
		sf.append(" Select fp_hm ");
		sf.append("   From ynt_vatsaleinvoice y ");
		sf.append("  Where y.pk_corp = ? and nvl(y.dr,0) = 0 and ");
		sf.append(wherePart);
		sf.append("  group by y.fp_hm ");
		sf.append(" having count(1) > 1  ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);

		List<VATSaleInvoiceVO2> list = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));
		sf = new StringBuffer();
		if (list != null && list.size() > 0) {
			sf.append("<p>发票号码:");
			for (VATSaleInvoiceVO2 vo : list) {
				sf.append(vo.getFp_hm());
				sf.append("  ");
			}
			sf.append("重复，请检查！</p>");

			throw new BusinessException(sf.toString());
		}
	}
	/**
	 * 校验数据是否重复
	 * 
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private VATSaleInvoiceVO2 checkOcrIsHasRepeation(String pk_corp,VATSaleInvoiceVO2 salevo) throws DZFWarpException {
		VATSaleInvoiceVO2 invo2= null;
		StringBuffer sf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sf.append(" Select * ");
		sf.append("   From ynt_vatsaleinvoice y ");
		sf.append("  Where y.pk_corp = ? and nvl(y.dr,0) = 0 and fp_hm=? and fp_dm=?");//and sourcetype !=?
		//vo.setSourcetype(IBillManageConstants.OCR);
		sp.addParam(pk_corp);
		sp.addParam(salevo.getFp_hm());
		sp.addParam(salevo.getFp_dm());
		//sp.addParam(IBillManageConstants.OCR);
		if(!StringUtil.isEmpty(salevo.getPrimaryKey())){
			sf.append("   and pk_vatsaleinvoice !=? ");
			sp.addParam(salevo.getPrimaryKey());
		}
		List<VATSaleInvoiceVO2> list = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));

		if (list != null && list.size() > 0) {
//			if(list.size()==1&&list.get(0).getSourcetype() == IBillManageConstants.OCR){
//				return list.get(0);
//			}
			
			sf = new StringBuffer();
			sf.append("<p>发票号码:");
			for (VATSaleInvoiceVO2 vo : list) {
				sf.append(vo.getFp_hm());
				sf.append("  ");
			}
			sf.append("重复，请检查！</p>");

			throw new BusinessException(sf.toString());
		}
		return invo2;
	}
	//设置摘要
	private void setPzZy(List<TzpzBVO> tblist, VatInvoiceSetVO setvo, VATSaleInvoiceVO2 vo){
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String zy = buildZy(setvo, vo, map);
		Boolean isaddZy = map.get("flag");
		if(isaddZy.booleanValue()){
			for(TzpzBVO bvo : tblist){
				bvo.setZy(zy);
			}
		}
	}
	// 其他按钮生成凭证
	@Override
	public void createPZ(VATSaleInvoiceVO2 vo, String pk_corp, String userid, String period,
						 VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT
			, List<List<Object[]>> levelList, Map<String, Object[]> categoryMap, Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap, Set<String> zyFzhsList
			, Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap, InventorySetVO inventorySetVO, CorpVO corp, Map<String, InventoryAliasVO> fzhsBMMap
			, List<Object> paramList, Map<String, BdCurrencyVO> currMap, Map<String, Object[]> rateMap, Map<String, String> bankAccountMap, Map<String, YntCpaccountVO> accountMap
			, Map<String, AuxiliaryAccountBVO> assistMap, Map<String, List<AccsetVO>> accsetMap, Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map, Map<String, String> jituanSubMap, YntCpaccountVO[] accVOs
			, String tradeCode, String newrule, List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException {

		CorpVO corpvo = corpService.queryByPk(pk_corp);

		List<VATSaleInvoiceVO2> ll = new ArrayList<VATSaleInvoiceVO2>();
		ll.add(vo);
		checkisGroup(ll, pk_corp);// 校验
		IntradeHVO ichvo = createIH(vo, corpvo, userid,fzhsBodyMap);//生成出库单
		// 首先先判断pk_model_h是否存在
		List<VATSaleInvoiceVO2> vatModelList = new ArrayList<VATSaleInvoiceVO2>();
		if (StringUtil.isEmpty(vo.getPk_model_h())) {
			/*setModelValue(vo, corpvo, vatModelList);
			if (StringUtil.isEmpty(vo.getPk_model_h())) {
				DcModelHVO defaultModel = getDefaultModelHVO(map, vo, corpvo);
				if (defaultModel != null) {
					vo.setPk_model_h(defaultModel.getPk_model_h());
					vo.setBusitypetempname(defaultModel.getBusitypetempname());
					vatModelList.add(vo);
					isT = true;
				}
			}*/
			throw new BusinessException("销项发票:业务类型为空,请重新选择业务类型");
		}


		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		int fp_style = getFpStyle(vo);
		headVO.setFp_style(fp_style);
//生成凭证
		/*aitovoucherserv_vatinvoice.getTzpzBVOList(headVO, models, lib, detailvos, tblist, accounts);
		aitovoucherserv_vatinvoice.setSpeaclTzpzBVO1(headVO, lib, tblist);*/
		Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
		List<OcrInvoiceVO> invoiceList = changeToOcr(ll, pk_corp);
		List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, period, pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
		//取出凭证子类
		List<TzpzBVO> tblist = Arrays.asList((TzpzBVO[])tzpzhvoList.get(0).getChildren());
		//取出凭证主类//清空子类
		//tzpzhvoList.get(0).setChildren(null);
		//根据规则设置摘要q
		//setPzZy(tblist, setvo, vo);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		vo.setCount(1);
		createTzpzHVO(headVO, ll, pk_corp, userid, null, null, vo.getJshj(), accway,ichvo,setvo);
		headVO.setPk_image_group(vo.getPk_image_group());
		updateImageGroup(vo.getPk_image_group());

		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));

		if (isT) {
			createTempPZ(headVO, new VATSaleInvoiceVO2[] { vo }, pk_corp,ichvo);
		} else {
			headVO = voucher.saveVoucher(corpvo, headVO);
		}

		if (lwflag != null && lwflag.booleanValue()) {
			List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
			list.add(vo);
			updateOtherType(list);// 其他
		}
		if(ichvo != null){
			writeBackSale(ichvo, headVO);
		}
		// 更新业务类型标识
		/*if (vatModelList != null && vatModelList.size() > 0) {
			singleObjectBO.updateAry(vatModelList.toArray(new VATSaleInvoiceVO2[0]), new String[] { "pk_model_h" });
		}*/
	}

	/*private void setModelValue(VATSaleInvoiceVO2 vo, CorpVO corpvo, List<VATSaleInvoiceVO2> vatModelList) {

		scanMatchBusiName(vo, null);

		if (!StringUtil.isEmpty(vo.getPk_model_h())) {
			vatModelList.add(vo);
		}
	}*/

	/*private DcModelHVO getDefaultModelHVO(Map<String, DcModelHVO> map, VATSaleInvoiceVO2 vo, CorpVO corpvo) {
		if (map == null || map.size() == 0)
			return null;

		String charname = corpvo.getChargedeptname();
		DZFBoolean iszhuan = vo.getIsZhuan();
		
		String vscode = iszhuan!=null && iszhuan.booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
		String szcode = FieldConstant.SZSTYLE_05;// 其他收入
		String businame = "销售收入";
		String corptype = corpvo.getCorptype();
		DcModelHVO model = null;
		DcModelHVO defaultModel;
		for (Map.Entry<String, DcModelHVO> entry : map.entrySet()) {
			defaultModel = entry.getValue();
			if ((StringUtil.isEmpty(defaultModel.getChargedeptname())
					|| charname.equals(defaultModel.getChargedeptname()))
					&& corptype.equals(defaultModel.getPk_trade_accountschema())
					&& vscode.equals(defaultModel.getVspstylecode()) && szcode.equals(defaultModel.getSzstylecode())
					&& businame.equals(defaultModel.getBusitypetempname())) {
				model = defaultModel;
				break;
			}
		}

		return model;
	}*/

	private OcrInvoiceDetailVO[] changeOcrInvoiceDetailVO(VATSaleInvoiceVO2 vo, String pk_corp) {

		VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[]) vo.getChildren();

		int len = bvos == null ? 0 : bvos.length;

		List<OcrInvoiceDetailVO> detailList = new ArrayList<OcrInvoiceDetailVO>();

		OcrInvoiceDetailVO detailvo = null;
		VATSaleInvoiceBVO2 bvo = null;
		for (int i = 0; i < len; i++) {
			bvo = bvos[i];
			detailvo = new OcrInvoiceDetailVO();

			detailvo.setInvname(bvo.getBspmc());
			detailvo.setInvtype(bvo.getInvspec());
			detailvo.setItemunit(bvo.getMeasurename());
			detailvo.setItemamount(getDefaultMny(bvo.getBnum()));
			detailvo.setItemprice(getDefaultMny(bvo.getBprice()));
			detailvo.setItemmny(getDefaultMny(bvo.getBhjje()));
			detailvo.setItemtaxrate(getDefaultMny(bvo.getBspsl()));
			detailvo.setItemtaxmny(getDefaultMny(bvo.getBspse()));
			detailvo.setPk_corp(pk_corp);

			detailvo.setPk_inventory(bvo.getPk_inventory());// 存货

			detailList.add(detailvo);
		}

		return detailList.toArray(new OcrInvoiceDetailVO[0]);
	}

	private String getDefaultMny(DZFDouble mny) {
		return mny != null ? mny.toString() : DZFDouble.ZERO_DBL.toString();
	}

	private OcrImageLibraryVO changeOcrInvoiceVO(VATSaleInvoiceVO2 vo, String pk_corp, int imagetype, int ifpkind) {

		OcrImageLibraryVO invvo = new OcrImageLibraryVO();
		invvo.setVinvoicecode(vo.getFp_dm());
		invvo.setVinvoiceno(vo.getFp_hm());
		invvo.setDinvoicedate(vo.getKprj().toString());
		invvo.setInvoicetype(ifpkind == 1 ? "04" : "01");
		invvo.setItype(imagetype);
		invvo.setNmny(getDefaultMny(vo.getHjje()));
		invvo.setNtaxnmny(getDefaultMny(vo.getSpse()));
		invvo.setNtotaltax(getDefaultMny(vo.getJshj()));
		invvo.setVpurchname(vo.getKhmc());
		invvo.setVpurchtaxno(vo.getCustidentno());
		invvo.setVpuropenacc(vo.getGhfyhzh());
		invvo.setVpurphoneaddr(vo.getGhfdzdh());
		invvo.setVsalename(vo.getXhfmc());
		invvo.setVsaletaxno(vo.getXhfsbh());
		invvo.setVsaleopenacc(vo.getXhfyhzh());
		invvo.setVsalephoneaddr(vo.getXhfdzdh());
		invvo.setInvname(vo.getSpmc());
		invvo.setVmemo(vo.getDemo());
		invvo.setDkbs(null);
		invvo.setUniquecode(null);
		invvo.setPk_corp(pk_corp);
		invvo.setIfpkind(ifpkind);
		invvo.setIinvoicetype(null);

		invvo.setIsinterface(DZFBoolean.TRUE);

		return invvo;
	}

	private static String ISTEMP = "ISTEMP";// 暂存标识
	// private List<TzpzBVO> createTzpzBVO(VATSaleInvoiceVO2 vo,
	// DcModelHVO mHVO,
	// String pk_curr,
	// Map<String, YntCpaccountVO> ccountMap,
	// Map<String, Boolean> isTempMap,
	// Map<String, TaxitemVO> taxItemMap,
	// String pk_corp,
	// String userid,
	// boolean isNewFz){
	// List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
	// Boolean isTemp = null;
	// DcModelBVO[] mBVOs = mHVO.getChildren();
	// CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
	// YntCpaccountVO[] accounts = AccountCache.getInstance().get(null,
	// vo.getPk_corp());
	//
	// DZFDouble mny = null;
	// TzpzBVO tzpzbvo = null;
	// List<TzpzBVO> tzpzList = null;
	// YntCpaccountVO cvo = null;
	//// YntCpaccountVO[] accounts = null;
	// int rowno = 1;//分录行排序
	// for(DcModelBVO bvo : mBVOs){
	//
	// cvo = ccountMap.get(bvo.getPk_accsubj());
	// tzpzbvo = new TzpzBVO();
	// isTemp = isTempMap.get(ISTEMP);
	// if(isTemp || StringUtil.isEmpty(vo.getKhmc())){
	// isTemp = true;
	// isTempMap.put(ISTEMP, Boolean.TRUE);
	// }else{
	// tzpzList = new ArrayList<TzpzBVO>();
	// cvo = buildTzpzBVoByAccount(cvo, tzpzbvo, tzpzList, vo, corpvo, bvo,
	// accounts, isNewFz, isTempMap, 1);
	// }
	//
	// if(tzpzList == null || tzpzList.size() == 0){
	// mny = (DZFDouble) vo.getAttributeValue(bvo.getVfield());
	// tzpzbvo.setRowno(rowno++);//设置分录行编号
	//
	// if(bvo.getDirection() == 0){
	// tzpzbvo.setJfmny(mny);
	// tzpzbvo.setYbjfmny(mny);
	// tzpzbvo.setVdirect(0);
	// }else{
	// tzpzbvo.setDfmny(mny);
	// tzpzbvo.setYbdfmny(mny);
	// tzpzbvo.setVdirect(1);
	// }
	//
	// setTzpzBVOValue(tzpzbvo, pk_curr, cvo, bvo, vo);
	//
	// dealTaxItem(tzpzbvo, null, vo, vo.getSpsl(), cvo);
	// bodyList.add(tzpzbvo);
	// }else{
	// for(TzpzBVO tzpzchild : tzpzList){
	// tzpzchild.setRowno(rowno++);//设置分录行编号
	// setTzpzBVOValue(tzpzchild, pk_curr, cvo, bvo, vo);
	//
	// dealTaxItem(tzpzchild, null, vo, vo.getSpsl(), cvo);
	// bodyList.add(tzpzchild);
	// }
	// }
	//
	// cvo = null;
	// }
	// return bodyList;
	// }

	// private void setTzpzBVOValue(TzpzBVO tzpzbvo, String pk_curr,
	// YntCpaccountVO cvo, DcModelBVO bvo, VATSaleInvoiceVO2 vo){
	// tzpzbvo.setPk_currency(pk_curr);
	// tzpzbvo.setPk_accsubj(cvo.getPk_corp_account());
	// tzpzbvo.setVcode(cvo.getAccountcode());
	// tzpzbvo.setVname(cvo.getAccountname());
	//
	// tzpzbvo.setKmmchie(cvo.getFullname());
	// tzpzbvo.setSubj_code(cvo.getAccountcode());
	// tzpzbvo.setSubj_name(cvo.getAccountname());
	//
	// tzpzbvo.setZy(bvo.getZy());
	// tzpzbvo.setNrate(DZFDouble.ONE_DBL);
	// tzpzbvo.setPk_corp(vo.getPk_corp());
	// }

	// private void dealTaxItem(TzpzBVO depvo, String pk_inventory,
	// VATSaleInvoiceVO2 vo,
	// DZFDouble taxratio,
	// YntCpaccountVO cvo){
	//
	// taxratio = SafeCompute.div(taxratio, new DZFDouble(100));
	//
	// TaxitemVO itemvo = TaxItemUtil.getTaxitemVO(vo.getCoperatorid(),
	// depvo.getPk_corp(),
	// taxratio, pk_inventory, cvo);
	// if (itemvo != null) {
	// depvo.setPk_taxitem(itemvo.getPk_taxitem());
	// depvo.setTaxcode(itemvo.getTaxcode());
	// depvo.setTaxname(itemvo.getTaxname());
	// depvo.setTaxratio(itemvo.getTaxratio());
	// }
	// }

	// private List<TaxitemVO> filterTaxItem(List<TaxitemVO> list){
	// List<TaxitemVO> after = new ArrayList<TaxitemVO>();
	//
	// if(list != null && list.size() > 0){
	// for(TaxitemVO vo : list){
	// if(vo.getIsselect() != null && vo.getIsselect().booleanValue()){}
	// after.add(vo);
	// }
	// }
	//
	// return after;
	// }

	// private YntCpaccountVO buildTzpzBVoByAccount(YntCpaccountVO cvo,
	// TzpzBVO bvo,
	// List<TzpzBVO> tzpzList,
	// VATSaleInvoiceVO2 vo,
	// CorpVO corpvo,
	// DcModelBVO modelbvo,
	// YntCpaccountVO[] accounts,
	// boolean isNewFz,
	// Map<String,Boolean> isTempMap,
	// int level){
	//
	// if(level > 4){
	// isTempMap.put(ISTEMP, true);
	// return cvo;
	// }
	//
	// boolean isleaf = cvo.getIsleaf().booleanValue();//是否是末级
	// boolean iskhfz = cvo.getIsfzhs().charAt(0) == '1';//是否是客户辅助核算
	// boolean ischfz = cvo.getIsfzhs().charAt(5) == '1';//是否是存货辅助核算
	//
	// if(iskhfz || ischfz){
	// constructFzhs(cvo, bvo, tzpzList, vo, corpvo, accounts, modelbvo, iskhfz,
	// ischfz, isNewFz);
	// }else if(!isleaf){
	//
	// cvo = getXJAccount(cvo, vo.getKhmc(), accounts, isTempMap, bvo, tzpzList,
	// vo, corpvo, modelbvo, isNewFz, ++level);
	// }
	//
	// return cvo;
	// }

	// private TzpzBVO constructFzhs(YntCpaccountVO cvo,
	// TzpzBVO tzpzbvo,
	// List<TzpzBVO> tzpzList,
	// VATSaleInvoiceVO2 vo,
	// CorpVO corpvo,
	// YntCpaccountVO[] accounts,
	// DcModelBVO modelbvo,
	// boolean iskhfz,
	// boolean ischfz,
	// boolean isNewFz){
	// List<AuxiliaryAccountBVO> fzhslist = new
	// ArrayList<AuxiliaryAccountBVO>();
	// AuxiliaryAccountBVO fzhsx = null;
	//
	// VATSaleInvoiceBVO2[] salechildren = (VATSaleInvoiceBVO2[])
	// vo.getChildren();
	//
	// if(ischfz){
	// if(salechildren == null || salechildren.length == 0){
	// getInvFz(vo, null, corpvo, tzpzbvo, accounts);
	// }else{
	// TzpzBVO tzpzchild = null;
	// DZFDouble mny = null;
	// for(VATSaleInvoiceBVO2 salechild : salechildren){
	// tzpzchild = new TzpzBVO();
	// mny = (DZFDouble) salechild.getAttributeValue(modelbvo.getVfield());
	//
	// if(modelbvo.getDirection() == 0){
	// tzpzchild.setJfmny(mny);
	// tzpzchild.setYbjfmny(mny);
	// tzpzchild.setVdirect(0);
	// }else{
	// tzpzchild.setDfmny(mny);
	// tzpzchild.setYbdfmny(mny);
	// tzpzchild.setVdirect(1);
	// }
	//
	// getInvFz(vo, salechild, corpvo, tzpzchild, accounts);
	//
	// if(cvo != null && cvo.getIsnum() != null &&
	// cvo.getIsnum().booleanValue()){
	// tzpzchild.setNnumber(salechild.getBnum());
	// tzpzchild.setNprice(salechild.getBprice());//单价
	// }
	//
	// tzpzList.add(tzpzchild);
	// }
	// }
	// }
	//
	// if(iskhfz){
	// fzhsx = getfzhsx(AuxiliaryConstant.ITEM_CUSTOMER, vo.getPk_corp(),
	// vo.getKhmc(), null, null, isNewFz);
	//
	// fzhslist.add(fzhsx);
	//
	// if(tzpzList != null && tzpzList.size() > 0){
	// for(TzpzBVO tzpzchild : tzpzList){
	// tzpzchild.setFzhsx1(fzhsx.getPk_auacount_b());
	//
	// if(fzhslist.size() > 0){
	// tzpzchild.setFzhs_list(fzhslist);
	// }
	// }
	// }else{
	// tzpzbvo.setFzhsx1(fzhsx.getPk_auacount_b());
	//
	// if(fzhslist.size() > 0){
	// tzpzbvo.setFzhs_list(fzhslist);
	// }
	// }
	// }
	//
	// return tzpzbvo;
	// }

	// private void getInvFz(VATSaleInvoiceVO2 salevo, VATSaleInvoiceBVO2
	// salechild, CorpVO corpvo,
	// TzpzBVO entry, YntCpaccountVO[] accounts){
	// // 如果启用库存
	// if (corpvo.getBbuildic() != null && corpvo.getBbuildic().booleanValue())
	// {
	// InventoryVO bvo = matchInvtoryIC(salevo, salechild, corpvo, accounts);
	// if (bvo != null) {
	// entry.setPk_inventory(bvo.getPk_inventory());
	// }
	// } else {
	// // 不启用库存
	// AuxiliaryAccountBVO bvo = matchInvtoryNoIC(salevo, salechild, corpvo);
	// if (bvo != null) {
	// entry.setFzhsx6(bvo.getPk_auacount_b());
	// }
	// }
	// }

	// private AuxiliaryAccountBVO matchInvtoryNoIC(VATSaleInvoiceVO2 salevo,
	// VATSaleInvoiceBVO2 salechild, CorpVO corpvo) {
	// if (corpvo == null || salechild == null)
	// return null;
	// AuxiliaryAccountBVO bvo = null;
	// String pk_auacount_h = AuxiliaryConstant.ITEM_INVENTORY;
	// bvo = ocr_atuomatch.getAuxiliaryAccountBVOByInfo(salechild.getBspmc(),
	// salechild.getInvspec(), salechild.getMeasurename(),corpvo.getPk_corp(),
	// pk_auacount_h);
	//
	// // 如果 匹配存货辅助 匹配不上新建
	//
	// if (bvo == null
	// && ((salechild != null && StringUtil.isEmpty(salechild.getBspmc())))) {
	// bvo = new AuxiliaryAccountBVO();
	// bvo.setCode(yntBoPubUtil.getFZHsCode(corpvo.getPk_corp(),
	// pk_auacount_h));
	//
	// bvo.setUnit(salechild.getMeasurename());
	// bvo.setSpec(salechild.getInvspec());
	// bvo.setName(salechild.getBspmc());
	//
	// bvo.setPk_corp(corpvo.getPk_corp());
	// bvo.setDr(0);
	// bvo.setPk_auacount_h(AuxiliaryConstant.ITEM_INVENTORY);
	// bvo = gl_fzhsserv.saveB(bvo);
	// }
	// return bvo;
	// }

	/**
	 * 生成暂存态凭证
	 * 
	 * @param headVO
	 * @param vos
	 * @param pk_corp
	 */
	private void createTempPZ(TzpzHVO headVO, VATSaleInvoiceVO2[] vos, String pk_corp,IntradeHVO ichvo) {
		gl_yhdzdserv2.checkCreatePZ(pk_corp, headVO);
		headVO.setVbillstatus(IVoucherConstants.TEMPORARY);// 暂存态
		TzpzBVO[] bvos = (TzpzBVO[]) headVO.getChildren();
		if (bvos != null && bvos.length > 0) {
			for (int i = 0; i < bvos.length; i++) {
				if (bvos[i].getRowno() == null) {
					bvos[i].setRowno(i + 1);
				}
			}
		}

		// 凭证号
		headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp, headVO.getDoperatedate()));// 改值
		if(ichvo==null){
			headVO.setSourcebillid(headVO.getPrimaryKey());
			headVO.setSourcebilltype(IBillTypeCode.HP90);
		}else{
			headVO.setSourcebillid(ichvo.getPrimaryKey());
			headVO.setSourcebilltype(IBillTypeCode.HP75);
		}
//		headVO.setSourcebilltype(IBillTypeCode.HP90);
		headVO = (TzpzHVO) singleObjectBO.saveObject(headVO.getPk_corp(), headVO);

		// 更新税目
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		voucher.saveTaxItem(headVO, corpvo);
		// 暂存态回写
		for (VATSaleInvoiceVO2 vo : vos) {
			vo.setPk_tzpz_h(headVO.getPrimaryKey());
			vo.setPzh(headVO.getPzh());
		}

		singleObjectBO.updateAry(vos, new String[] { "pk_tzpz_h", "pzh" });
		
		//暂存态回写业务类型模板
		singleObjectBO.updateAry(vos,new String[] { "pk_model_h" });//, "busitypetempname"
	}

	/**
	 * 生成凭证分录
	 *
	 * @return
	 */
	// private List<TzpzBVO> createTzpzBVO(VATSaleInvoiceVO2 vo,
	// Map<String, YntCpaccountVO> map,
	// String pk_corp,
	// String pk_curr,
	// Map<String, YntCpaccountVO> accmap,
	// Map<String, Boolean> isTempMap,
	// boolean isNewFz){
	//
	// String zy = buildZy(vo);
	// List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
	// if(StringUtil.isEmptyWithTrim(vo.getPk_subject()))
	// throw new BusinessException("销项发票清单:未关联相关科目，请检查");
	//
	// int rowno = 1;//分录行排序
	// YntCpaccountVO cpavo = accmap.get(vo.getPk_subject());
	// if(cpavo == null){
	// cpavo = getParentCpaVO(vo.getPk_subject(), pk_corp, isTempMap);
	// }
	// TzpzBVO bvo = new TzpzBVO();//借方科目
	// cpavo = transCpaVO(cpavo, bvo, vo, isTempMap, isNewFz, 1);
	// bvo.setPk_currency(pk_curr);
	// bvo.setPk_accsubj(cpavo.getPk_corp_account());
	// bvo.setVcode(cpavo.getAccountcode());
	// bvo.setVname(cpavo.getAccountname());
	//
	// bvo.setKmmchie(cpavo.getFullname());
	// bvo.setSubj_code(cpavo.getAccountcode());
	// bvo.setSubj_name(cpavo.getAccountname());
	//
	// bvo.setRowno(rowno++);
	// bvo.setZy(zy);
	// bvo.setNrate(DZFDouble.ONE_DBL);
	// bvo.setPk_corp(pk_corp);
	//
	// bvo.setJfmny(vo.getJshj());
	// bvo.setYbjfmny(vo.getJshj());
	// bodyList.add(bvo);
	//
	// DZFDouble sl = vo.getSpsl();
	// String spmc = vo.getSpmc();
	//
	// bvo = new TzpzBVO();//贷方科目
	// if (
	// (new DZFDouble("11").compareTo(sl) == 0
	// || new DZFDouble("6").compareTo(sl) == 0
	// || new DZFDouble("3").compareTo(sl) == 0)
	// &&
	// (!StringUtil.isEmpty(spmc)
	// && spmc.indexOf("费") != -1)
	//
	// ) {//判断是否是提供劳务收入
	//
	// cpavo = map.get(LABOUR);
	// }else{
	// cpavo = map.get(SALE);
	// }
	// Boolean b = false;
	// cpavo = transCpaVO(cpavo, bvo, vo, isTempMap, isNewFz, 1);
	// bvo.setPk_currency(pk_curr);
	// bvo.setPk_accsubj(cpavo.getPk_corp_account());
	// bvo.setVcode(cpavo.getAccountcode());
	// bvo.setVname(cpavo.getAccountname());
	//
	// bvo.setKmmchie(cpavo.getFullname());
	// bvo.setSubj_code(cpavo.getAccountcode());
	// bvo.setSubj_name(cpavo.getAccountname());
	//
	// bvo.setRowno(rowno++);
	// bvo.setZy(zy);
	// bvo.setNrate(DZFDouble.ONE_DBL);
	// bvo.setPk_corp(pk_corp);
	//
	// bvo.setDfmny(vo.getHjje());
	// bvo.setYbdfmny(vo.getHjje());
	// bodyList.add(bvo);
	//
	// bvo = new TzpzBVO();//贷方科目
	// cpavo = map.get(TAX);
	// cpavo = transCpaVO(cpavo, bvo, vo, isTempMap, isNewFz, 1);
	// bvo.setPk_currency(pk_curr);
	// bvo.setPk_accsubj(cpavo.getPk_corp_account());
	// bvo.setVcode(cpavo.getAccountcode());
	// bvo.setVname(cpavo.getAccountname());
	//
	// bvo.setKmmchie(cpavo.getFullname());
	// bvo.setSubj_code(cpavo.getAccountcode());
	// bvo.setSubj_name(cpavo.getAccountname());
	//
	// bvo.setRowno(rowno++);
	// bvo.setZy(zy);
	// bvo.setNrate(DZFDouble.ONE_DBL);
	// bvo.setPk_corp(pk_corp);
	//
	// bvo.setDfmny(vo.getSpse());
	// bvo.setYbdfmny(vo.getSpse());
	// bodyList.add(bvo);
	//
	// cpavo = null;
	//
	// return bodyList;
	// }

	// private String buildZy(VATSaleInvoiceVO2 vo){
	// StringBuffer sf = new StringBuffer();
	// boolean isKhmcNoEmp = !StringUtil.isEmpty(vo.getKhmc());
	// boolean isSpmcNoEmp = !StringUtil.isEmpty(vo.getSpmc());
	//
	// if(isKhmcNoEmp){
	// sf.append("向")
	// .append(vo.getKhmc())
	// .append("销售");
	// if(isSpmcNoEmp){
	// sf.append(vo.getSpmc());
	// }
	// }else{
	// if(isSpmcNoEmp)
	// sf.append("销售")
	// .append(vo.getSpmc());
	// }
	//
	// return sf.toString();
	// }

	// private YntCpaccountVO getParentCpaVO(String pk_subject, String pk_corp,
	// Map<String,Boolean> isTempMap){
	// YntCpaccountVO cpavo = (YntCpaccountVO)
	// singleObjectBO.queryByPrimaryKey(YntCpaccountVO.class, pk_subject);
	// if(cpavo != null){
	// String code = cpavo.getAccountcode().substring(0, 4);
	// YntCpaccountVO[] accounts = AccountCache.getInstance().get(null,
	// pk_corp);
	// List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();
	// for(YntCpaccountVO accvo : accounts){
	// if(accvo.getIsleaf().booleanValue() && accvo.get__parentId() != null &&
	// accvo.get__parentId().startsWith(code)){
	// list.add(accvo);
	// }
	// }
	//
	// if(list.size() > 0){//存在值即排序
	// Collections.sort(list, new Comparator<YntCpaccountVO>() {
	//
	// @Override
	// public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
	// int i = o1.getAccountcode().compareTo(o2.getAccountcode());
	// return i;
	// }
	// });
	// for(YntCpaccountVO vo : list){
	// if(vo.getIsleaf().booleanValue()){
	// isTempMap.put(ISTEMP, true);
	// return vo;
	// }
	// }
	// }
	// }
	//
	// if(cpavo == null){
	// throw new BusinessException("入账科目未找到，请检查");
	// }
	// return cpavo;
	// }

	// private YntCpaccountVO transCpaVO(YntCpaccountVO cvo,
	// TzpzBVO bvo,
	// VATSaleInvoiceVO2 vo,
	// Map<String, Boolean> isTempMap,
	// boolean isNewFz,
	// int level){
	// if(StringUtil.isEmptyWithTrim(vo.getKhmc()) || level > 4){
	// isTempMap.put(ISTEMP, true);
	// return cvo;
	// }
	//
	// boolean isleaf = cvo.getIsleaf().booleanValue();//是否是末级
	// boolean iskhfz = cvo.getIsfzhs().charAt(0) == '1';//是否是客户辅助核算
	//
	// if(iskhfz){
	// List<AuxiliaryAccountBVO> fzhslist = new
	// ArrayList<AuxiliaryAccountBVO>();
	// AuxiliaryAccountBVO fzhsx = getfzhsx(AuxiliaryConstant.ITEM_CUSTOMER,
	// vo.getPk_corp(), vo.getKhmc(), null, null, isNewFz);
	// bvo.setFzhsx1(fzhsx.getPk_auacount_b());//getPK_fzhsx(AuxiliaryConstant.ITEM_CUSTOMER,
	// vo.getPk_corp(),vo.getKhmc(), null, null, isNewFz)
	// fzhslist.add(fzhsx);
	// bvo.setFzhs_list(fzhslist);
	// }else if(!isleaf){
	// YntCpaccountVO[] accounts = AccountCache.getInstance().get(null,
	// vo.getPk_corp());
	// cvo = getXJAccount(cvo, vo.getKhmc(), accounts, isTempMap, bvo, vo,
	// isNewFz, ++level);
	// }
	//
	// return cvo;
	// }

	// 获取下级科目
	// private YntCpaccountVO getXJAccount(YntCpaccountVO account,
	// String ghfmc,
	// YntCpaccountVO[] accounts,
	// Map<String,Boolean> isTempMap,
	// TzpzBVO tzpzbvo,
	// List<TzpzBVO> tzpzList,
	// VATSaleInvoiceVO2 vo,
	// CorpVO corpvo,
	// DcModelBVO dcmodelvo,
	// boolean isNewFz,
	// int level){
	// List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();//存储下级科目
	//
	// boolean iskhfz;
	// boolean ischfz;
	// for(YntCpaccountVO accvo : accounts){
	// if(accvo.getIsleaf().booleanValue() && accvo.get__parentId() != null &&
	// accvo.get__parentId().startsWith(account.getAccountcode())){
	// if(accvo.getAccountname().equals(ghfmc)){
	// return accvo;
	// }else{
	// iskhfz = accvo.getIsfzhs().charAt(0) == '1';//是否是客户辅助核算
	// ischfz = accvo.getIsfzhs().charAt(5) == '1';//是否是存货辅助核算
	// if(iskhfz || ischfz){
	// accvo = buildTzpzBVoByAccount(accvo, tzpzbvo, tzpzList, vo, corpvo,
	// dcmodelvo, accounts, isNewFz, isTempMap, ++level);
	// return accvo;
	// }
	// list.add(accvo);
	// }
	//
	// }
	// }
	// YntCpaccountVO nextvo = null;
	// if(list.size() > 0){//存在值即排序
	// Collections.sort(list, new Comparator<YntCpaccountVO>() {
	//
	// @Override
	// public int compare(YntCpaccountVO o1, YntCpaccountVO o2) {
	// String code1 = o1.getAccountcode();
	// String code2 = o2.getAccountcode();
	// int i = code1.length() - code2.length() == 0 ?
	// o1.getAccountcode().compareTo(o2.getAccountcode()) : code1.length() -
	// code2.length();
	// return i;
	// }
	// });
	//
	// String firaccountcode = account.getAccountcode();
	// String[] specarrcode = IBillManageConstants.SPEC_ACC_CODE;
	// if(firaccountcode.startsWith(specarrcode[0])
	// || firaccountcode.startsWith(specarrcode[1])
	// || firaccountcode.startsWith(specarrcode[2])
	// || firaccountcode.startsWith(specarrcode[3])){
	// nextvo = (YntCpaccountVO) list.get(list.size() - 1).clone();
	// int accountcode = Integer.parseInt(nextvo.getAccountcode()) + 1;
	// nextvo.setPk_corp_account(null);
	// nextvo.setFullname(null);
	// nextvo.setAccountcode(accountcode + "");
	// nextvo.setAccountname(ghfmc);
	// nextvo.setDoperatedate(new DZFDate().toString().substring(0,
	// 10).substring(0, 10));
	// nextvo.setIssyscode(DZFBoolean.FALSE);
	// nextvo.setBisseal(DZFBoolean.FALSE);
	// cpaccountService.saveNew(nextvo);
	// }else{
	// for(YntCpaccountVO yntvo : list){
	// if(yntvo.getIsleaf().booleanValue()){
	// isTempMap.put(ISTEMP, true);
	// return yntvo;
	// }
	// }
	// }
	//
	// }
	//
	// return nextvo;
	// }

	// private String getPK_fzhsx (String pk_fzhslb, String pk_corp, String
	// name, String dw, String gg, boolean isNewFz) {
	//
	// return getfzhsx(pk_fzhslb, pk_corp, name, dw, gg,
	// isNewFz).getPk_auacount_b();
	// }

	// private AuxiliaryAccountBVO getfzhsx (String pk_fzhslb, String pk_corp,
	// String name, String dw, String gg, boolean isNewFz) {
	//
	// AuxiliaryAccountBVO fzhs = queryBByName(pk_corp, name, dw, gg,
	// pk_fzhslb);
	//
	// if (fzhs == null) {
	// fzhs = new AuxiliaryAccountBVO();
	// fzhs.setPk_auacount_h(pk_fzhslb);
	// fzhs.setCode(getRandomCode());
	// fzhs.setName(name);
	// fzhs.setUnit(dw);
	// fzhs.setSpec(gg);
	// fzhs.setPk_corp(pk_corp);
	// fzhs = (AuxiliaryAccountBVO) singleObjectBO.saveObject(pk_corp, fzhs);
	// }
	// return fzhs;
	// }

	private String getRandomCode() {
		StringBuilder sb = new StringBuilder();
		Random rand = new Random();
		for (int i = 0; i < 11; i++) {
			sb.append(rand.nextInt(10));
		}
		char letter = (char) (rand.nextInt(26) + 'a');
		sb.append(letter);
		return sb.toString();
	}

	private AuxiliaryAccountBVO queryBByName(String pk_corp, String name, String dw, String gg, String hid)
			throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		sf.append(" pk_auacount_h = ? and name = ? and pk_corp = ? and nvl(dr,0) = 0 ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(hid);
		sp.addParam(name);
		sp.addParam(pk_corp);
		if (AuxiliaryConstant.ITEM_INVENTORY.equals(hid)) {
			if (!StringUtil.isEmpty(dw)) {
				sf.append(" and unit = ? ");
				sp.addParam(dw);
			}
			if (!StringUtil.isEmpty(gg)) {
				sf.append(" and spec = ? ");
				sp.addParam(gg);
			}
		}
		AuxiliaryAccountBVO[] results = (AuxiliaryAccountBVO[]) singleObjectBO
				.queryByCondition(AuxiliaryAccountBVO.class, sf.toString(), sp);
		if (results != null && results.length > 0) {
			return results[0];
		}
		return null;
	}

	/**
	 * 生成凭证
	 * 
	 * @param headVO
	 * @param pk_corp
	 * @param userid
	 * @param pk_curr
	 * @return
	 */
	private TzpzHVO createTzpzHVO(TzpzHVO headVO, List<VATSaleInvoiceVO2> list, String pk_corp, String userid, String pk_curr,
			Map<String, Boolean> isTempMap, DZFDouble mny, boolean accway,IntradeHVO ichvo,VatInvoiceSetVO setvo) {

		DZFDate voucherDate = null;
		if(setvo!=null&&(setvo.getPzrq()==null||setvo.getPzrq()==1)){
			//凭证日期是当前账期最后一天
			if (accway) {
				voucherDate = DateUtils.getPeriodEndDate(DateUtils.getPeriod(list.get(0).getKprj()));// 取开票日期所在期间的最后一天
			} else {
				voucherDate = list.get(0).getKprj();
			}

			if (!StringUtil.isEmpty(list.get(0).getInperiod())) {
				voucherDate = DateUtils.getPeriodEndDate(list.get(0).getInperiod());
			}
		}else{
			//凭证日期是票据实际日期
			DZFDate kprq=null;
			if(list!=null&&list.size()>1){
				
				for (VATSaleInvoiceVO2 vo : list) {
					if(kprq==null){
						kprq = vo.getKprj();
					}else{
						if(kprq.before(vo.getKprj())){
							kprq=vo.getKprj();
						}
					}
					 
				}
			}else{	
				kprq=list.get(0).getKprj();
			}
			
			if(DateUtils.getPeriod(kprq).compareTo(list.get(0).getInperiod())==0){
			
				voucherDate = kprq ;
			}else if(DateUtils.getPeriod(kprq).compareTo(list.get(0).getInperiod())<0){
				
				voucherDate = DateUtils.getPeriodStartDate(list.get(0).getInperiod());
			}else if(DateUtils.getPeriod(kprq).compareTo(list.get(0).getInperiod())>0){
				
				voucherDate = DateUtils.getPeriodEndDate(list.get(0).getInperiod());
			}else{
				
				voucherDate = DateUtils.getPeriodEndDate(list.get(0).getInperiod());
			}
	
		}
		

		headVO.setPk_corp(pk_corp);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(mny);
		headVO.setDfmny(mny);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setDoperatedate(voucherDate);// vo.getKprj()
		// headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp,
		// vo.getTradingdate()));
		// headVO.setVbillstatus(isTemp ? IVoucherConstants.TEMPORARY :
		// IVoucherConstants.FREE);// 默认自由态
		headVO.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
		// 记录单据来源
//		headVO.setSourcebillid(vo.getPrimaryKey());
//		headVO.setSourcebilltype(IBillTypeCode.HP90);
		if(ichvo==null){
			headVO.setSourcebillid(list.get(0).getPrimaryKey());
			headVO.setSourcebilltype(IBillTypeCode.HP90);
		}else{
			headVO.setSourcebillid(ichvo.getPk_ictrade_h());
			headVO.setSourcebilltype(IBillTypeCode.HP75);
		}
		
		headVO.setFp_style(getFpStyle(list.get(0)));// 1/2/3 普票/专票/未开票 空：不处理改字段
		String period = list.get(0).getInperiod();
		headVO.setPeriod(period);
		headVO.setVyear(Integer.valueOf(period.substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		
		int count = list.get(0).getCount();
		if(count == 0){
			count = 1;
		}
		headVO.setNbills(count);// 
		
		headVO.setMemo(null);
		
		headVO.setIsqxsy(DZFBoolean.TRUE);//不在校验期间损益是否结转

		return headVO;
	}

	/**
	 * 此方法会过滤掉折扣行 调用时慎用
	 */
	public List<VATSaleInvoiceVO2> constructVatSale(VATSaleInvoiceVO2[] vos, String pk_corp) {
		List<String> pks = new ArrayList<String>();
		for (VATSaleInvoiceVO2 vo : vos) {
			pks.add(vo.getPrimaryKey());
		}

		String wherePart = SqlUtil.buildSqlForIn("pk_vatsaleinvoice", pks.toArray(new String[0]));
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_vatsaleinvoice where ");
		sf.append(wherePart);
		sf.append(" and pk_corp = ? and nvl(dr,0) = 0 ");
		sf.append(" order by kprj asc ,rowid asc ");

		sp.addParam(pk_corp);
		List<VATSaleInvoiceVO2> list = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));

		// 查询子表
		sf.delete(0, sf.length());
		sf.append(" Select * From ynt_vatsaleinvoice_b Where ");
		sf.append(wherePart);
		sf.append(" and pk_corp = ? and nvl(dr,0) = 0 order by pk_vatsaleinvoice asc, rowno asc, rowid asc ");

		List<VATSaleInvoiceBVO2> bList = (List<VATSaleInvoiceBVO2>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceBVO2.class));

		if (bList != null && bList.size() > 0) {
			Map<String, List<VATSaleInvoiceBVO2>> bMap = hashlizeBodyMap(bList);//
			if (list != null && list.size() > 0) {
				String key = null;
				List<VATSaleInvoiceBVO2> tempb = null;
				for (VATSaleInvoiceVO2 vo : list) {
					key = vo.getPk_vatsaleinvoice();
					if (bMap.containsKey(key)) {
						tempb = bMap.get(key);
						vo.setChildren(tempb.toArray(new VATSaleInvoiceBVO2[0]));
					}
				}
			}
		}

		// 过滤掉折口行（也就是金额存在负数的行）
		list = filterZkRow(list, pk_corp);

		return list;
	}

	private List<VATSaleInvoiceVO2> filterZkRow(List<VATSaleInvoiceVO2> stoList, String pk_corp) {

		if (stoList == null || stoList.size() == 0)
			return null;

		for (VATSaleInvoiceVO2 vo : stoList) {
			if (vo == null)
				continue;
			
			boolean iszkh = false;
			VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[]) vo.getChildren();

			if (bvos == null || bvos.length < 2)
				continue;

			List<VATSaleInvoiceBVO2> nlist = new ArrayList<>();
			nlist.add(bvos[0]);
			int len = bvos.length;

			for (int j = 1; j < len; j++) {
				VATSaleInvoiceBVO2 tvo = bvos[j];
				VATSaleInvoiceBVO2 bvo = bvos[j-1];
				if (tvo.getBhjje() != null && tvo.getBhjje().doubleValue() < 0 && bvo.getBhjje() != null
						&& bvo.getBhjje().doubleValue() > 0 && !StringUtils.isEmpty(tvo.getBspmc())
						&& !StringUtils.isEmpty(bvo.getBspmc())
						&& tvo.getBspmc().equals(bvo.getBspmc())) {
					iszkh = true;
					bvo.setBhjje(SafeCompute.add(tvo.getBhjje(),bvo.getBhjje()));
					bvo.setBspse(SafeCompute.add(tvo.getBspse(),bvo.getBspse()));
					bvo.setBnum(SafeCompute.add(tvo.getBnum(), bvo.getBnum()));
					bvo.setBprice(SafeCompute.div(tvo.getBhjje(), bvo.getBnum()));
					continue;
				}else{
					nlist.add(tvo);
				}
				
			}
			int size = nlist.size() - 1;
			for (int i = size; i >= 0; i--) {
				if (nlist.get(i) == null) {
					continue;
				} else {
					if (nlist.get(i).getBhjje() == null || nlist.get(i).getBhjje().doubleValue() == 0) {
						nlist.remove(i);
					}
				}
			}
			if(iszkh){
				for (int j = 0; j < nlist.size(); j++) {
					nlist.get(j).setRowno(j);
				}
			}
			vo.setChildren(nlist.toArray(new VATSaleInvoiceBVO2[nlist.size()]));
		}
		return stoList;
	}

	private Map<String, List<VATSaleInvoiceBVO2>> hashlizeBodyMap(List<VATSaleInvoiceBVO2> bList) {
		Map<String, List<VATSaleInvoiceBVO2>> map = new HashMap<String, List<VATSaleInvoiceBVO2>>();
		List<VATSaleInvoiceBVO2> tempList = null;
		String key = null;
		for (VATSaleInvoiceBVO2 bvo : bList) {
			key = bvo.getPk_vatsaleinvoice();
			bvo.setBspmc(OcrUtil.execInvname(bvo.getBspmc()));
			if (key != null) {
				if (!map.containsKey(key)) {
					tempList = new ArrayList<VATSaleInvoiceBVO2>();
					tempList.add(bvo);
					map.put(key, tempList);
				} else {
					tempList = map.get(key);
					tempList.add(bvo);
				}
			}
		}

		return map;
	}

	/**
	 * 构造模板
	 * 
	 * @param corpvo
	 * @return
	 */
	// public Map<String, YntCpaccountVO> construcTemKm(CorpVO corpvo){
	// /*
	// * 生成凭证规则。
	// 摘要：主营业务收入
	// 借：对应科目栏次的科目 金额为价税合计数。
	// 贷： 500101/600101 商品销售收入 金额为金额
	// （500102/600102 提供劳务收入 金额为金额 ）
	// 22210102 销项税额（与小规模共用） 金额为税额数
	//
	// 如何区分销售收入和提供劳务的科目
	// 销售收入：税率为 0.17、0.13、0.03 且开票项目不含有“费”字的。
	// 提供劳务：税率为 0.11、0.06、0.03 且开票项目含有“费”字的。
	// 如果不在以上行列的税率默认销售收入。
	// */
	// String pk_corp = corpvo.getPk_corp();
	// Map<String, YntCpaccountVO> map = null;
	// String[][] arr = new String[3][3];
	// if(gl_cbconstant.getFangan_2007().equals(corpvo.getCorptype())){
	// arr[0][0] = SALEACCOUNTCODE_07;
	// arr[0][1] = LABOURACCOUNTCODE_07;
	//
	// }else if(gl_cbconstant.getFangan_2013().equals(corpvo.getCorptype())){
	// arr[0][0] = SALEACCOUNTCODE_13;
	// arr[0][1] = LABOURACCOUNTCODE_13;
	// }
	//
	// arr[0][2] = OUTPUTTAXACCOUNTCODE;
	// arr[1][0] = "2";
	// arr[1][1] = "2";
	// arr[1][2] = "3";
	// if(StringUtil.isEmpty(arr[0][0])
	// || StringUtil.isEmpty(arr[0][1])){
	// return map;
	// }
	//
	// String ruleStr = gl_cpacckmserv.queryAccountRule(pk_corp);
	//
	// String[] newRule = ruleStr.split("/");
	// String qskm = null;
	// for(int i = 0; i < arr.length; i++){
	// arr[2][i] = getNewCode(newRule, arr[0][i],
	// Integer.parseInt(arr[1][i]));//获取当前公司科目编码，考虑升级
	// }
	//
	//
	// StringBuffer sf = new StringBuffer();
	// sf.append(" select * from ynt_cpaccount bb ");
	// sf.append(" where ");
	// sf.append( SqlUtil.buildSqlForIn("bb.accountcode", arr[2]));
	// sf.append(" and bb.pk_corp = ? and nvl(bb.dr,0)=0 ");//查询三个科目
	// SQLParameter sp = new SQLParameter();
	// sp.addParam(pk_corp);
	//
	// List<YntCpaccountVO> cpaList = (List<YntCpaccountVO>)
	// singleObjectBO.executeQuery(sf.toString(),
	// sp, new BeanListProcessor(YntCpaccountVO.class));
	// if(cpaList == null
	// || cpaList.size() != arr[2].length)
	// return map;
	//
	// //
	// map = new HashMap<String, YntCpaccountVO>();
	// for(YntCpaccountVO cpa : cpaList){
	// if(arr[2][0].equals(cpa.getAccountcode())){
	// map.put(SALE, cpa);
	// }else if(arr[2][1].equals(cpa.getAccountcode())){
	// map.put(LABOUR, cpa);
	// }else if(arr[2][2].equals(cpa.getAccountcode())){
	// map.put(TAX, cpa);
	// }
	// }
	//
	// return map;
	// }

	// private String getNewCode(String[] newRule, String code, int level) {
	// int beginIndex = 0;
	// String newCode = "";
	// for (int i = 0; i < level; i++) {
	// int codelen = oldRule[i];
	// String oldpartCode = code.substring(beginIndex, beginIndex + codelen);
	// beginIndex += codelen;
	// String newPartCode = getNewPartCode(newRule[i], oldpartCode);
	// newCode += newPartCode;
	// }
	// return newCode;
	// }

	// private String getNewPartCode(String newcodeRulePart, String oldpartCode)
	// {
	//
	// String newPartCode = oldpartCode;
	// int newPartLen = Integer.parseInt(newcodeRulePart);
	// int oldPartLen = oldpartCode.trim().length();
	// if (oldPartLen == newPartLen) {
	// return newPartCode;
	// }
	//
	// for (int i = 0; i < (newPartLen - oldPartLen); i++) {
	// newPartCode = "0" + newPartCode;
	// }
	//
	// return newPartCode;
	// }

	private static final String suff_xml = "xml";
	private static final String suff_zip = "zip";
	private static final String suff_xls = "xls";
	private static final String suff_xlsx = "xlsx";

	@Override
	public void saveImp(MultipartFile file, String filename, VATSaleInvoiceVO2 paramvo, String pk_corp, String fileType,
						String userid, StringBuffer msg) throws DZFWarpException {

		DZFBoolean isFlag = paramvo.getIsFlag();
		List<VATSaleInvoiceVO2> list = null;
		//List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);

		if (isXml(fileType)) {
			Integer flag = calcName(file);
			BufferedReader breader = null;
			InputStreamReader isreader = null;
			InputStream is = null;
			try {
				is = file.getInputStream();
				isreader = new InputStreamReader(is, "GBK");
				breader = new BufferedReader(isreader);
				list = new ArrayList<VATSaleInvoiceVO2>();
				if (flag != null) {
					if (flag.intValue() == BAI_WANG_A.intValue()) {
						getSXDataByXml(breader, pk_corp, userid, flag, list, msg);
					} else if (flag.intValue() == BAI_WANG_B.intValue()) {
						getDataByXml(breader, pk_corp, userid, flag, list, msg);
					}
				}
			} catch (Exception e) {
				log.error("解析文件错误", e);
				if (e instanceof BusinessException)
					throw new BusinessException(e.getMessage());
			} finally {
				if (breader != null) {
					try {
						breader.close();
					} catch (IOException e) {
					}
				}
				if (isreader != null) {
					try {
						isreader.close();
					} catch (IOException e) {
					}
				}

				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
					}
				}
			}
		} else if (isExcel(fileType)) {
			list = importExcel(file, fileType, pk_corp, userid, msg);
		} else if (isZip(fileType)) {
			list = readZipFile(file, pk_corp, userid, msg);
		} else {
			throw new BusinessException("上传文件格式不符合规范，请检查");
		}

		if (list == null || list.size() == 0) {
			String frag = "<p>导入文件数据为空，请检查。</p>";

			if (msg.length() == 0) {
				msg.append(frag);
			}

			throw new BusinessException(msg.toString());
		}

		judgeInOneCompany(list, pk_corp, isFlag);

		list = filterRepeationData(list, pk_corp, msg);// 过滤重复数据
		if(list.size()==0&&!StringUtils.isEmpty(msg.toString())){
			throw new BusinessException(msg.toString());
		}
		list = specialVatBVO(list, pk_corp);
		//设置数量 ,单价精度
		int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
		int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
		Map<String,String> map = new HashMap<String,String>();
		for (VATSaleInvoiceVO2 vo : list) {
			VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[]) vo.getChildren();
			if (bvos != null && bvos.length > 0) {
				for (VATSaleInvoiceBVO2 bvo : bvos) {
					if (bvo.getBnum() != null) {
						bvo.setBnum(bvo.getBnum().setScale(numPrecision, DZFDouble.ROUND_HALF_UP));
					}
					if (bvo.getBprice() != null) {
						bvo.setBprice(bvo.getBprice().setScale(pricePrecision, DZFDouble.ROUND_HALF_UP));
					}
				}
				
			}
			//根据业务类型名称找业务类型主键
			if(!StringUtils.isEmpty(vo.getBusitypetempname())){
				String pk_category = map.get(vo.getBusitypetempname()+vo.getInperiod());
				if(StringUtils.isEmpty(pk_category)){
					List<BillCategoryVO> categoryList = queryCategoryList(vo.getInperiod(), pk_corp);
					if(categoryList==null||categoryList.size()==0){
						CorpVO corpVO = corpService.queryByPk(pk_corp);
						schedulCategoryService.newSaveCorpCategory(null, pk_corp, vo.getInperiod(), corpVO);
					}
					
					pk_category = gl_yhdzdserv2.queryBillCategoryId(vo.getBusitypetempname(), pk_corp, vo.getInperiod());
					map.put(vo.getBusitypetempname()+vo.getInperiod(), pk_category);
				}
				vo.setPk_model_h(pk_category);
				
					
			}
		}
		
		Map<String, VATSaleInvoiceVO2[]> sendData = new HashMap<String, VATSaleInvoiceVO2[]>();
		
		//matchBusiName(list, dcList, pk_corp);

		sendData.put("adddocvos", list.toArray(new VATSaleInvoiceVO2[0]));
		VATSaleInvoiceVO2[] vos = updateVOArr(pk_corp, sendData);

		if (vos != null && vos.length > 0) {
			paramvo.setCount(vos.length);// 供action提示使用
			msg.append("<p>文件导入成功！</p>");
		}

		setAfterImportPeriod(vos, paramvo);
	}

	private List<BillCategoryVO> queryCategoryList(String period,String pk_corp){
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from ynt_billcategory where nvl(dr,0)=0 and period=? and pk_corp = ? ");
		sp.addParam(period);
		sp.addParam(pk_corp);
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(BillCategoryVO.class));
		return list;
	}
	private void judgeInOneCompany(List<VATSaleInvoiceVO2> list, String pk_corp, DZFBoolean isFlag) {
		if (isFlag == null || !isFlag.booleanValue()) {
			CorpVO corpvo = corpService.queryByPk(pk_corp);
			String corpname = corpvo.getUnitname();
			String xhfmc = null;
			for (VATSaleInvoiceVO2 vo : list) {
				xhfmc = vo.getXhfmc();

				if (!StringUtil.isEmpty(xhfmc) && !xhfmc.equals(corpname)) {
					throw new BusinessException(
							String.format("导入数据开票公司<font color='blue'>%s</font>与当前登录公司<font color='blue'>%s</font>不一致，是否导入?", new String[] { xhfmc, corpname }),
							IBillManageConstants.ERROR_FLAG);
				}
			}
		}
	}

	private List<VATSaleInvoiceVO2> filterRepeationData(List<VATSaleInvoiceVO2> list, String pk_corp, StringBuffer msg) {
		Map<String, List<VATSaleInvoiceVO2>> map = new LinkedHashMap<String, List<VATSaleInvoiceVO2>>();
		List<VATSaleInvoiceVO2> result = new ArrayList<VATSaleInvoiceVO2>();
		String key;
		List<VATSaleInvoiceVO2> tempList;
		StringBuffer sqlf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sqlf.append(" select fp_hm, fp_dm from ynt_vatsaleinvoice where ( ");
		for (VATSaleInvoiceVO2 vo : list) {
			key = vo.getFp_dm() + "_" + vo.getFp_hm();
			if (!map.containsKey(key)) {
				//sqlf.append(" ( fp_hm = ? and fp_dm = ? ) or");
				sqlf.append(" ( fp_hm = ? and fp_dm = ? ) or");//and sourcetype !="+IBillManageConstants.OCR+"
				
				sp.addParam(vo.getFp_hm());
				sp.addParam(vo.getFp_dm());

				tempList = new ArrayList<VATSaleInvoiceVO2>();
				tempList.add(vo);
				map.put(key, tempList);

			} else {
				tempList = map.get(key);
				tempList.add(vo);
			}
		}

		sqlf.delete(sqlf.length() - 2, sqlf.length());
		sqlf.append(" ) and pk_corp = ? and nvl(dr,0) = 0 ");
		sp.addParam(pk_corp);

		List<VATSaleInvoiceVO2> oldList = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sqlf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));

		if (oldList != null && oldList.size() > 0) {
			for (VATSaleInvoiceVO2 oldvo : oldList) {
				key = oldvo.getFp_dm() + "_" + oldvo.getFp_hm();

				if (map.containsKey(key)) {
					msg.append("<p>发票号码:");
					msg.append(oldvo.getFp_hm());
					msg.append("重复,请检查</p>");

					map.remove(key);
				}

			}
		}

		for (Map.Entry<String, List<VATSaleInvoiceVO2>> entry : map.entrySet()) {
			result.addAll(entry.getValue());
		}

		return result;
	}

	/*private void matchBusiName(List<VATSaleInvoiceVO2> list, List<DcModelHVO> dcList, String pk_corp) {

		if (list == null || list.size() == 0) {
			return;
		}

		Map<String, DcModelHVO> dcMap = new HashMap<String, DcModelHVO>();
		hashlizeDcModel(dcList, dcMap, pk_corp);
		String businame;
		DcModelHVO dchvo;
		String zpflag;
		DZFBoolean zhuan;
		for (VATSaleInvoiceVO2 vo : list) {
			businame = vo.getBusitypetempname();// 业务类型
			if (!StringUtil.isEmpty(businame)) {
				zhuan = vo.getIsZhuan();
				zpflag = zhuan != null && zhuan.booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
				zpflag = businame + "_" + zpflag;
				dchvo = dcMap.get(zpflag);

				if (dchvo == null) {
					vo.setBusitypetempname(null);
				} else {
					vo.setBusitypetempname(dchvo.getBusitypetempname());
					vo.setPk_model_h(dchvo.getPk_model_h());
					continue;
				}
			}

			if (StringUtil.isEmpty(vo.getSpmc()) || !StringUtil.isEmpty(vo.getPk_model_h())) {
				continue;
			}

			scanMatchBusiName(vo, dcMap);
		}
	}*/

	private void setAfterImportPeriod(VATSaleInvoiceVO2[] vos, VATSaleInvoiceVO2 paramvo) {
		if (vos != null && vos.length > 0) {
			DZFDate beginrq = vos[0].getKprj();
			DZFDate endrq = vos[0].getKprj();
			DZFDate rq = null;

			for (int i = 1; i < vos.length; i++) {

				rq = vos[i].getKprj();
				if (rq == null) {
					continue;
				}

				if (beginrq != null) {
					beginrq = beginrq.before(rq) ? beginrq : rq;
				}
				// else{
				// beginrq = beginrq;
				// }

				if (endrq != null) {
					endrq = endrq.before(rq) ? rq : endrq;
				} else {
					endrq = rq;
				}
			}

			paramvo.setBeginrq(beginrq != null ? DateUtils.getPeriodStartDate(DateUtils.getPeriod(beginrq)) : null);
			paramvo.setEndrq(endrq != null ? DateUtils.getPeriodEndDate(DateUtils.getPeriod(endrq)) : null);
		}
	}

	private List<VATSaleInvoiceVO2> specialVatBVO(List<VATSaleInvoiceVO2> blist, String pk_corp) {
		String[][] STYLE_2 = getBStyleMap();

		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		Map<String, List<VATSaleInvoiceBVO2>> bmap = new HashMap<String, List<VATSaleInvoiceBVO2>>();

		// List<TaxitemVO> taxItems = queryTaxItems(pk_corp);

		List<VATSaleInvoiceBVO2> btempList = null;
		VATSaleInvoiceBVO2 bvo = null;
		Object value = null;
		String key = null;
		for (VATSaleInvoiceVO2 vo : blist) {
			key = vo.getFp_dm() + "_" + vo.getFp_hm();

			bvo = new VATSaleInvoiceBVO2();
			for (String[] arr : STYLE_2) {
				value = vo.getAttributeValue(arr[0]);
				bvo.setAttributeValue(arr[1], value);
			}

			// 设置税目
			// setBodyVOShuimu(bvo, taxItems);

			if (bmap.containsKey(key)) {
				btempList = bmap.get(key);

				bvo.setRowno(btempList.size() + 1);

				btempList.add(bvo);
			} else {
				list.add(vo);

				btempList = new ArrayList<VATSaleInvoiceBVO2>();

				bvo.setRowno(1);

				btempList.add(bvo);
				bmap.put(key, btempList);
			}

		}

		for (VATSaleInvoiceVO2 hvo : list) {
			key = hvo.getFp_dm() + "_" + hvo.getFp_hm();
			if (bmap.containsKey(key)) {
				btempList = bmap.get(key);

				// 重新设置金额税额
				if (btempList.size() > 1) {
					DZFDouble je = DZFDouble.ZERO_DBL;
					DZFDouble se = DZFDouble.ZERO_DBL;
					DZFDouble sl = DZFDouble.ZERO_DBL;
					for (VATSaleInvoiceBVO2 bvo2 : btempList) {
						je = SafeCompute.add(je, bvo2.getBhjje());
						se = SafeCompute.add(se, bvo2.getBspse());
					}

					hvo.setHjje(je);
					hvo.setSpse(se);
					hvo.setJshj(SafeCompute.add(je, se));

					sl = SafeCompute.multiply(SafeCompute.div(se, je), new DZFDouble(100));
					sl = sl.setScale(0, DZFDouble.ROUND_HALF_UP);
					hvo.setSpsl(sl);

				}

				hvo.setChildren(btempList.toArray(new VATSaleInvoiceBVO2[0]));
			}
		}

		return list;
	}

	private List<VATSaleInvoiceVO2> importExcel(MultipartFile file, String fileType, String pk_corp, String userid,
		    StringBuffer msg) throws DZFWarpException {
		InputStream is = null;
		try {
			is = file.getInputStream();
			Workbook impBook = null;
			try {
				if (suff_xls.equals(fileType)) {
					impBook = new HSSFWorkbook(is);
				} else if (suff_xlsx.equals(fileType)) {
					impBook = new XSSFWorkbook(is);
				} else {
					throw new BusinessException("不支持的文件格式");
				}
			} catch (Exception e) {
				log.error("错误",e);
				if (e instanceof BusinessException) {
					throw new BusinessException(e.getMessage());
				} else {
					is = file.getInputStream();
					impBook = new XSSFWorkbook(is);
				}
			}

			// 判断excel文件来源
			int sourceType = getExcelSourceType(impBook);

			int sheetno = impBook.getNumberOfSheets();
			if (sheetno == 0) {
				throw new Exception("需要导入的数据为空。");
			}
			Sheet sheet1 = impBook.getSheetAt(0);
			;
			List<VATSaleInvoiceVO2> list = getDataByExcel(sheet1, pk_corp, userid, fileType, sourceType, msg);
			return list;
		} catch (FileNotFoundException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("导入文件未找到");
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			throw new BusinessException("导入文件格式错误");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			throw new BusinessException("导入文件格式错误");
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
		}
	}

	private int getExcelSourceType(Workbook book) {
		Sheet sheet = book.getSheetAt(0);
		int iBegin = 0;
		Cell aCell = null;
		String sTmp = null;

		Map<Integer, Object[][]> style = getStyleByExcel();
		Integer maxCount = getMaxCountByStyleMap();

		Map<String, String> nameMap = null;
		Object[][] objarr = null;
		int flagCount = 0;

		for (; iBegin < (sheet.getLastRowNum() + 1); iBegin++) {
			if (iBegin == sheet.getLastRowNum())
				throw new BusinessException("导入失败,导入文件抬头格式不正确");

			if (sheet.getRow(iBegin) == null && iBegin != sheet.getLastRowNum())
				continue;

			nameMap = new HashMap<String, String>();
			for (int k = 0; k < maxCount; k++) {
				aCell = sheet.getRow(iBegin).getCell(k);
				sTmp = getExcelCellValue(aCell);

				if (!StringUtil.isEmpty(sTmp)) {
					nameMap.put(sTmp, sTmp);
				}
			}

			if (nameMap.size() == 0) {
				continue;
			}

			for (Map.Entry<Integer, Object[][]> entry1 : style.entrySet()) {
				objarr = entry1.getValue();

				flagCount = 0;
				for (Object[] objs : objarr) {
					if (nameMap.containsKey((String) objs[1])) {
						flagCount++;
					}

				}

				if (objarr.length == flagCount) {
					return entry1.getKey();// 返回对应的模板类型
				}

			}

		}

		return TONGYONG_EXCEL;
	}

	private List<VATSaleInvoiceVO2> getDataByExcel(Sheet sheet, String pk_corp, String userid, String fileType,
			int sourceType, StringBuffer msg) throws DZFWarpException {
		List<VATSaleInvoiceVO2> blist = new ArrayList<VATSaleInvoiceVO2>();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFBoolean ISZHUAN = null;
		//DZFBoolean ISZHUAN = isZhuan(corpvo);
		// YntCpaccountVO[] cpavos = AccountCache.getInstance().get(null,
		// pk_corp);
		int iBegin = 0;
		Cell aCell = null;
		String sTmp = "";
		VATSaleInvoiceVO2 excelvo = null;
		// StringBuffer sf = new StringBuffer();

		String corpnameStr = null;// 导入文件中第一个是公司名称
		Object[][] STYLE_1 = getStyleByExcel().get(sourceType);
		Set<String> matchSet = null;
		Map<String, Integer> cursorMap = buildImpStypeMap(STYLE_1);

		Integer maxCCount = getStyleCellCount().get(sourceType);// 遍历的最大列数
		// int loopCount = 0;

		for (; iBegin < (sheet.getLastRowNum() + 1); iBegin++) {
			if (sheet.getRow(iBegin) == null && iBegin != sheet.getLastRowNum())
				continue;
			if (iBegin == sheet.getLastRowNum())
				throw new BusinessException("导入失败,导入文件抬头格式不正确 !");

			matchSet = new HashSet<String>();
			for (int k = 0; k < maxCCount; k++) {

				aCell = sheet.getRow(iBegin).getCell(k);
				sTmp = getExcelCellValue(aCell);

				// 校准前先确定公司名称，而确定发票类别
				if (sTmp != null) {
					if (StringUtil.isEmpty(corpnameStr) && sTmp.endsWith("发票数据")) {
						corpnameStr = sTmp.substring(0, sTmp.length() - 4);// 截取发票数据前的字符
						break;
					} else if (sourceType != XINLONG_EXCEL && sTmp.startsWith("发票类别")) {
						ISZHUAN = transPjlb(sTmp, ISZHUAN);
						break;
					}

				}

				if (sTmp != null && cursorMap.containsKey(sTmp)) {
					matchSet.add(sTmp);
					cursorMap.put(sTmp, k);// 重新设置列行号
					// loopCount++;
				}
			}
			if (cursorMap.size() == matchSet.size()) {
				iBegin++;

				resetStyleCursor(STYLE_1, cursorMap);
				break;
			}

			if (iBegin == sheet.getLastRowNum())
				throw new BusinessException("文件格式不正确，请检查");

		}

		int count;// 计数器的作用判断该行是不是空行，如count == STYLE_1.length 则为空行
		boolean isNullFlag;
		VATSaleInvoiceVO2 firstvo = null;
		StringBuffer innermsg = new StringBuffer();
		for (; iBegin < (sheet.getLastRowNum() + 1); iBegin++) {
			excelvo = new VATSaleInvoiceVO2();
			if (sourceType == XINLONG_EXCEL)
			{
				ISZHUAN = null;		//新龙的专票标志在行的“发票类型”上，每行需要重新初始化
			}
			count = 0;
			isNullFlag = false;
			for (int j = 0; j < STYLE_1.length; j++) {
				if (sheet.getRow(iBegin) == null) {
					isNullFlag = true;
					break;
				}

				aCell = sheet.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
				sTmp = getExcelCellValue(aCell);
				
				sTmp = specialTreatCellValue(sourceType, j, sTmp,aCell);
				
				if (sTmp != null && !StringUtil.isEmpty(sTmp.trim())) {
					if(sTmp.startsWith("发票类别") ){
						ISZHUAN = transPjlb(sTmp, ISZHUAN);
					}
					if (XINLONG_EXCEL == sourceType && STYLE_1[j][2].toString().equals("iszhuan"))	//
					{
						ISZHUAN = new DZFBoolean(sTmp.endsWith("专用发票") || sTmp.contains("机动车"));
					}
					else {
						excelvo.setAttributeValue(STYLE_1[j][2].toString(), sTmp.trim());// sTmp.replace("
					}																		// ",
																						// "")
				} else {
					count++;
				}
			}

			if (excelvo != null && count != STYLE_1.length && !isNullFlag) {

				// 特殊处理 航信、百旺最后一行 带有的提示性语言
				if (BAIWANG_EXCEL == sourceType || BAIWANG_another_EXCEL == sourceType || BAIWANG_NEW_EXCEL == sourceType || BAIWANG_NEW_another_EXCEL == sourceType
						|| HANG_TIAN_EXCEL == sourceType|| HANG_JDC_EXCEL == sourceType
						|| BAIWANG_JDC_EXCEL == sourceType) {

					String fpdm = excelvo.getFp_dm();// 发票代码列
					if (!StringUtil.isEmpty(fpdm)
							&& (fpdm.startsWith("份数") || fpdm.startsWith("发票类别") || fpdm.startsWith("发票代码"))) {// 针对专普票在一个excel中所做的特殊处理
						continue;
					} else if (!StringUtil.isEmpty(excelvo.getBspmc())// 多行明细中
																		// 小计行不存
							&& "小计".equals(excelvo.getBspmc()) && excelvo.getBspsl() == null) {
						continue;
					} else if (firstvo == null && (BAIWANG_EXCEL == sourceType || BAIWANG_NEW_EXCEL == sourceType) && StringUtil.isEmpty(fpdm)
							&& StringUtil.isEmpty(excelvo.getFp_hm()) && excelvo.getBhjje() != null) {// 百旺废票的分录行过滤掉
						continue;
					}

					dealSpecialValue(excelvo, firstvo);//

					if (BAIWANG_EXCEL == sourceType  || BAIWANG_NEW_EXCEL == sourceType || BAIWANG_JDC_EXCEL == sourceType) {// 发票状态含废票的过滤掉
						String status = excelvo.getInvstatus();
						if (!StringUtil.isEmpty(status)) {
							dealStatusValue(excelvo, status);
						}
					}
				}
				if (sourceType == XINLONG_EXCEL)	//新龙，处理发票状态	20191211
				{
					String status = excelvo.getInvstatus();
					if (!StringUtil.isEmpty(status)) {
						dealStatusValue(excelvo, status);
					}
				}

				if (TONGYONG_EXCEL == sourceType) {
					if (excelvo.getBnum() != null && excelvo.getHjje() != null) {
						excelvo.setBprice(SafeCompute.div(excelvo.getHjje(), excelvo.getBnum()));
					}
				}
				if(HANG_JDC_EXCEL == sourceType){//航信机动车
					excelvo.setMeasurename("辆");
					excelvo.setBnum(new DZFDouble(1));
					excelvo.setXhfdzdh(isNull(excelvo.getXfdz())+" "+isNull(excelvo.getXfdh()));
					excelvo.setXhfyhzh(isNull(excelvo.getXfyh())+" "+isNull(excelvo.getXfzh()));
				}
				if(BAIWANG_JDC_EXCEL == sourceType){//百旺机动车
					excelvo.setMeasurename("辆");
					excelvo.setBnum(new DZFDouble(1));
					if(excelvo.getSpsl().doubleValue()<1){
						excelvo.setSpsl(excelvo.getSpsl().multiply(new DZFDouble(100)));
						excelvo.setBspsl(excelvo.getSpsl().multiply(new DZFDouble(100)));
					}
				}
				
				innermsg.setLength(0);
				checkDataValid(excelvo, innermsg, iBegin, pk_corp, fileType,sourceType);

				if (innermsg.length() != 0) {
					msg.append(innermsg);
					continue;
				}

				setDefaultValue(pk_corp, userid, excelvo, ISZHUAN, corpnameStr,sourceType);
				blist.add(excelvo);

				firstvo = excelvo;// 如果后续有空值， 用firstvo的字段值
			}

		}

		// 构造业务类型
		// Map<String, DcModelHVO> dcMap1 = new HashMap<String, DcModelHVO>();
		// Map<String, DcModelHVO> dcMap2 = new HashMap<String, DcModelHVO>();
		// buildDcModelMap(dcMap1, dcList);
		//
		// specialBusiNameValue(blist, pk_corp, dcMap1);

		return blist;
	}
	private String specialTreatCellValue(int sourceType, int j, String sTmp,Cell cell) {
		//暂时处理导入模板金额保留两位
		if (sTmp == null || StringUtil.isEmpty(sTmp.trim()))
			return sTmp;
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期字符串
		java.text.DecimalFormat formatter = new java.text.DecimalFormat("#########.##");
		if(sourceType==BAIWANG_EXCEL||sourceType==BAIWANG_another_EXCEL || sourceType==BAIWANG_NEW_EXCEL || sourceType==BAIWANG_NEW_another_EXCEL){
			if(j==9||j==10||j==15||j==16||j==18){
				if(isNumber(sTmp)){
					sTmp = formatter.format(new DZFDouble(sTmp));
				}
				
			}
		}else if(sourceType==BAIWANG_JDC_EXCEL){
			if(j==6||j==8||j==12||j==13||j==15){
				if(isNumber(sTmp)){
					sTmp = formatter.format(new DZFDouble(sTmp));
				}
			}else if(j==5){//百旺机动车模板开票日期yyyy-MM-dd hh:mm:ss格式需要处理
				sTmp=sTmp.trim().substring(0,10);
			}
		}else if(sourceType==HANG_TIAN_EXCEL){
			if(j==9||j==10||j==15||j==16||j==18){
				if(isNumber(sTmp)){
					sTmp = formatter.format(new DZFDouble(sTmp));
				}
			}
		}else if(sourceType==HANG_JDC_EXCEL){
			if(j==6||j==12||j==13||j==16||j==17||j==19){
				if(isNumber(sTmp)){
					sTmp = formatter.format(new DZFDouble(sTmp));
				}
			}
		}else if(sourceType==TONGYONG_EXCEL){
			if(j==7||j==8){
				if(isNumber(sTmp)){
					sTmp = formatter.format(new DZFDouble(sTmp));
				}
			}//else if(j==2&&cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC){//开票日期	通用模板不用再特殊处理开票日期
			//	sTmp = sdf.format(HSSFDateUtil.getJavaDate(Double.parseDouble(sTmp)));
			//}
		}
		else if (sourceType == XINLONG_EXCEL)
		{
			if (j == 3 && sTmp != null && sTmp.trim().length() > 8)	//开票日期
			{
				sTmp = sTmp.substring(0, 4) + "-" + sTmp.substring(4, 6) + "-" + sTmp.substring(6, 8);
			}
		}
		
		return sTmp;
	}
	private boolean isNumber(String sTmp){
		try {
			new DZFDouble(sTmp);
			return true;
		} catch (Exception e) {
			return false;
		}
		
	}
	private String isNull(String string){
		return StringUtils.isEmpty(string)?"":string;
	}
	private void dealStatusValue(VATSaleInvoiceVO2 excelvo, String status) {
		String kplx = null;//ICaiFangTongConstant.FPLX_1;// 正票
		DZFDouble je = excelvo.getBhjje();
		if (je != null) {
			if (excelvo.INVMODEL_1.equals(status) || excelvo.INVMODEL_11.equals(status)) {
				kplx = ICaiFangTongConstant.FPLX_1;
			} else if (excelvo.INVMODEL_4.equals(status) || excelvo.INVMODEL_14.equals(status)) {
				kplx = ICaiFangTongConstant.FPLX_2;
			} else if (excelvo.INVMODEL_5.equals(status) || excelvo.INVMODEL_15.equals(status)) {
				kplx = ICaiFangTongConstant.FPLX_5;
			} else if (excelvo.INVMODEL_2.equals(status) || excelvo.INVMODEL_12.equals(status)) {
				kplx = ICaiFangTongConstant.FPLX_3;
			} else if (excelvo.INVMODEL_3.equals(status) || excelvo.INVMODEL_13.equals(status)) {
				kplx = ICaiFangTongConstant.FPLX_4;
			} 
//			else if (excelvo.INVMODEL_3.equals(status)
//					&& SafeCompute.add(je, DZFDouble.ZERO_DBL).doubleValue() >= 0) {
//				kplx = ICaiFangTongConstant.FPLX_4;
//			} 
//			else if (excelvo.INVMODEL_3.equals(status) && SafeCompute.add(je, DZFDouble.ZERO_DBL).doubleValue() < 0) {
//				kplx = ICaiFangTongConstant.FPLX_5;
//			}
		}

		excelvo.setKplx(kplx);
	}

	private DZFBoolean transPjlb(String str, DZFBoolean zhuan) {
		if (!StringUtil.isEmpty(str)) {
			if (str.endsWith(VAT_EXC_ZHUAN)) {
				zhuan = DZFBoolean.TRUE;
			} else if (str.endsWith(VAT_EXC_PU) || str.endsWith(VAT_EXC_DIANZI)) {
				zhuan = DZFBoolean.FALSE;
			}
		}

		return zhuan;
	}

	private void resetStyleCursor(Object[][] style, Map<String, Integer> cursorMap) {
		if (style != null && style.length > 0) {
			String name = null;
			Integer acursor = null;
			Integer mcursor = null;
			for (Object[] arr : style) {
				acursor = (Integer) arr[0];
				name = (String) arr[1];

				mcursor = cursorMap.get(name);
				if (mcursor == null) {
					throw new BusinessException("解析文件格式失败，请检查");
				}

				if (acursor != null && mcursor != null && acursor.intValue() != mcursor.intValue()) {// 重新设置游标
					arr[0] = mcursor;
				}

			}
		}
	}

	private Map<String, Integer> buildImpStypeMap(Object[][] style) {
		Map<String, Integer> map = new HashMap<String, Integer>();
		if (style != null && style.length > 0) {
			String name = null;
			Integer cursor = null;
			for (Object[] arr : style) {
				cursor = (Integer) arr[0];
				name = (String) arr[1];
				if (!map.containsKey(name)) {
					map.put(name, cursor);
				}
			}
		}

		return map;
	}

	private Integer getMaxCountByStyleMap() {// 获取最大行号
		Integer count = null;

		Map<Integer, Integer> map = getStyleCellCount();

		Integer i = null;
		for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
			i = entry.getValue();

			if (count == null || count < i) {
				count = i;
			}
		}

		return count;
	}

	public Map<Integer, Integer> getStyleCellCount() {
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		countMap.put(TONGYONG_EXCEL, 15);
		countMap.put(BAIWANG_EXCEL, 25);
		//新百旺
		countMap.put(BAIWANG_NEW_EXCEL, 25);

		countMap.put(BAIWANG_another_EXCEL, 25);

		countMap.put(BAIWANG_NEW_another_EXCEL, 25);

		//新龙
		countMap.put(XINLONG_EXCEL, 30);

		countMap.put(HANG_TIAN_EXCEL, 25);
		countMap.put(HANG_JDC_EXCEL, 26);//航信机动车  
		countMap.put(BAIWANG_JDC_EXCEL, 18);//百旺机动车  

		return countMap;
	}

	// private void buildDcModelMap(Map<String, DcModelHVO> dcMap1,
	// List<DcModelHVO> dcList) {
	// dcMap1 = new HashMap<String, DcModelHVO>();
	//
	// hashlizeDcModel(dcList, dcMap1);
	// }

	private void dealSpecialValue(VATSaleInvoiceVO2 vo, VATSaleInvoiceVO2 firstvo) {

		String value = vo.getTempvalue();
		if (!StringUtil.isEmpty(value)) {// 处理开票日期 && value.length() > 10
			try {
				DZFDate date = null;
				int index = value.indexOf(" ");
				if (index == -1) {
					date = new DZFDate(value);
				} else {
					value = value.substring(0, index);// 取日期
					date = new DZFDate(value);
				}
				vo.setKprj(date);
			} catch (Exception e) {
				log.error("错误",e);
			}
		}

		String fpdm = vo.getFp_dm();
		String fphm = vo.getFp_hm();
		DZFDouble je = vo.getBhjje();

		if (firstvo != null && StringUtil.isEmpty(fpdm) && StringUtil.isEmpty(fphm) && je != null) {// 只针对航信、百旺excel导入的模板
																									// 单独
			vo.setFp_dm(firstvo.getFp_dm());
			vo.setFp_hm(firstvo.getFp_hm());
			vo.setKhmc(firstvo.getKhmc());
			vo.setCustidentno(firstvo.getCustidentno());
			vo.setGhfyhzh(firstvo.getGhfyhzh());
			vo.setGhfdzdh(firstvo.getGhfdzdh());
			vo.setKprj(firstvo.getKprj());

			vo.setInvstatus(firstvo.getInvstatus());
		}

	}

	/**
	 * 业务类型字段特殊处理
	 *
	 */
	// private void specialBusiNameValue(List<VATSaleInvoiceVO2> list, String
	// pk_corp, Map<String, DcModelHVO> dcMap1) {
	//
	// String busiName = null;
	// DcModelHVO tempDcVo = null;
	// for (VATSaleInvoiceVO2 vo : list) {
	// busiName = vo.getBusitypetempname();
	// if (!StringUtil.isEmpty(busiName)) {
	//
	// tempDcVo = dcMap1.get(busiName);
	//
	// if (tempDcVo == null) {
	// vo.setBusitypetempname(null);
	// } else {
	// vo.setBusitypetempname(tempDcVo.getBusitypetempname());
	// vo.setPk_model_h(tempDcVo.getPk_model_h());
	// }
	// } else if (!StringUtil.isEmpty(vo.getSpmc())) {
	// scanMatchBusiName(vo, dcMap1);
	// }
	//
	// }
	//
	// }

	// private String matchBusiValue(String busiName, String pk_corp) {
	// StringBuffer keysf = new StringBuffer();
	// keysf.append(busiName);
	// if (busiName.endsWith("收入")) {
	// CorpVO corp = CorpCache.getInstance().get(null, pk_corp);
	// String fpcode = isZhuan(corp).booleanValue() ? FieldConstant.FPSTYLE_01 :
	// FieldConstant.FPSTYLE_02;
	//
	// keysf.append("_");
	// keysf.append(fpcode);// 增值税专用发票、增值税普通发票
	// keysf.append("_");
	// keysf.append(FieldConstant.SZSTYLE_05);// 其他收入
	// }
	// return keysf.toString();
	// }

	/*private void hashlizeDcModel(List<DcModelHVO> dcModelList, Map<String, DcModelHVO> dcmap, String pk_gs) {

		dcModelList = new DcPzmb().filterDataCommon(dcModelList, pk_gs, null, null, "Y", null);
		if (dcModelList == null || dcModelList.size() == 0) {
			return;
		}
		String key2 = null;
		String pk_corp = null;
		String szcode = null;
		String vscode = null;
		for (DcModelHVO hvo : dcModelList) {
			szcode = hvo.getSzstylecode();
			vscode = hvo.getVspstylecode();
			// 只过滤出 增值税 其他收入
			if (FieldConstant.FPSTYLE_01.equals(vscode) || FieldConstant.FPSTYLE_02.equals(vscode)) {
				key2 = hvo.getBusitypetempname() + "_" + vscode;
				pk_corp = hvo.getPk_corp();
				if (!IDefaultValue.DefaultGroup.equals(pk_corp)) {
					dcmap.put(key2, hvo);
				} else if (FieldConstant.SZSTYLE_05.equals(szcode)) {
					if (!dcmap.containsKey(key2)) {
						dcmap.put(key2, hvo);
					}
				}

			}
		}

	}*/

	private String getExcelCellValue(Cell cell) {
		String ret = "";
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期字符串
			if (cell == null) {
				ret = null;
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				ret = cell.getRichStringCellValue().getString();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				ret = "" + Double.valueOf(cell.getNumericCellValue()).doubleValue();
				// 小数不可用这样格式，只为了凭证编码格式
				java.text.DecimalFormat formatter = new java.text.DecimalFormat("#########.########");
				if ("General".equals(cell.getCellStyle().getDataFormatString())) {
					ret = formatter.format(cell.getNumericCellValue());
				} else if (cell.getCellStyle().getDataFormatString().indexOf(".") >= 0) {
					ret = formatter.format(cell.getNumericCellValue());
				} else {
					ret = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
				}
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
				String value1 = null;
				try {
					java.text.DecimalFormat formatter = new java.text.DecimalFormat("#############.##");
					value1 = formatter.format(cell.getNumericCellValue());
					ret = value1;
				}
				catch (Exception e)
				{}
				if (StringUtil.isEmpty(value1) || "0.00".equals(ret))
				{
					try {
						FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();  
					    CellValue cellValue = evaluator.evaluate(cell);
					    ret = String.valueOf(cellValue.getNumberValue());
					}
					catch (Exception e)
					{}
				}
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
				ret = "" + cell.getErrorCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
				ret = "" + cell.getBooleanCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
				ret = null;
			}
		} catch (Exception ex) {
			log.error("错误",ex);
			ret = null;
		}
		return OcrUtil.filterCorpName(ret);
	}

	private String[][] getBStyleMap() {
		return new String[][] { { "spmc", "bspmc" }, { "spsl", "bspsl" }, { "spse", "bspse" }, { "hjje", "bhjje" },
				{ "pk_corp", "pk_corp" },

				// 后续添加的子表
				{ "invspec", "invspec" }, { "measurename", "measurename" }, { "bnum", "bnum" }, { "bprice", "bprice" }
				// { "bspsl", "bspsl" }
		};
	}

	private Map<Integer, Object[][]> getStyleByExcel() {// int sourceType
		Map<Integer, Object[][]> STYLE = new HashMap<Integer, Object[][]>();
		Object[][] obj1 = new Object[][] { // 百旺导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "购方企业名称", "khmc" }, { 3, "购方税号", "custidentno" },
				{ 4, "银行帐号", "ghfyhzh" }, { 5, "地址电话", "ghfdzdh" }, { 6, "开票日期", "tempvalue" }, // 20170901
																								// 17:37:04
																								// 格式需要二次转换，故临时存放，后续会放到
																								// kprj
																								// 字段
				{ 8, "备注", "demo" }, // 暂时将单据号放在备注栏中
				{ 9, "商品名称", "spmc" }, { 14, "金额", "hjje" }, // 合计金额
				{ 16, "税额", "spse" }, // 合计税额

				{ 9, "商品名称", "bspmc" }, { 10, "规格", "invspec" }, { 11, "单位", "measurename" }, { 12, "数量", "bnum" },
				{ 13, "单价", "bprice" }, { 14, "金额", "bhjje" }, { 15, "税率", "bspsl" }, { 16, "税额", "bspse" },
				{ 17, "发票状态", "invstatus" } };

		Object[][] obj11 = new Object[][] { // 百旺 其他类型模板导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "购方企业名称", "khmc" }, { 3, "购方税号", "custidentno" },
				{ 4, "银行帐号", "ghfyhzh" }, { 5, "地址电话", "ghfdzdh" }, { 6, "开票日期", "kprj" }, { 8, "单据号", "demo" }, // 暂时将单据号放在备注栏中
				{ 9, "商品名称", "spmc" }, { 14, "金额", "hjje" }, // 合计金额
				{ 16, "税额", "spse" }, // 合计税额
				{ 9, "商品名称", "bspmc" }, { 10, "规格", "invspec" }, { 11, "单位", "measurename" }, { 12, "数量", "bnum" },
				{ 13, "单价", "bprice" }, { 14, "金额", "bhjje" }, { 15, "税率", "bspsl" }, { 16, "税额", "bspse" } };
		Object[][] obj12 = new Object[][] { // 百旺 机动车导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "购货单位", "khmc" }, { 3, "购方税号", "custidentno" },
				{ 5, "车辆类型", "spmc" }, { 13, "开票日期", "kprj" }, { 14, "金额", "hjje" }, 
				{ 15, "税率", "spsl" }, { 16, "税额", "spse" }, { 17, "发票状态", "invstatus" },
				{ 5, "车辆类型", "bspmc" }, { 6, "厂牌型号", "invspec" }, { 14, "金额", "bprice" }, { 14, "金额", "bhjje" },
				{ 15, "税率", "bspsl" }, { 16, "税额", "bspse" } };

		//新增新百旺导入 20191204
		Object[][] obj13 = new Object[][] { // 百旺 新销项模板导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "购方企业名称", "khmc" }, { 3, "购方税号", "custidentno" },
				{ 4, "银行账户", "ghfyhzh" }, { 5, "地址电话", "ghfdzdh" }, { 6, "开票日期", "kprj" },

				{ 8, "备注", "demo" }, // 暂时将单据号放在备注栏中
				{ 9, "商品名称", "spmc" }, { 14, "金额", "hjje" }, // 合计金额
				{ 16, "税额", "spse" }, // 合计税额

				{ 9, "商品名称", "bspmc" }, { 10, "规格", "invspec" }, { 11, "单位", "measurename" }, { 12, "数量", "bnum" },
				{ 13, "单价", "bprice" }, { 14, "金额", "bhjje" }, { 15, "税率", "bspsl" }, { 16, "税额", "bspse" },
				{ 17, "发票状态", "invstatus" } };


		Object[][] obj14 = new Object[][] { // 百旺 其他类型模板导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "购方企业名称", "khmc" }, { 3, "购方税号", "custidentno" },
				{ 4, "银行账户", "ghfyhzh" }, { 5, "地址电话", "ghfdzdh" }, { 6, "开票日期", "kprj" }, { 8, "单据号", "demo" }, // 暂时将单据号放在备注栏中
				{ 9, "商品名称", "spmc" }, { 14, "金额", "hjje" }, // 合计金额
				{ 16, "税额", "spse" }, // 合计税额
				{ 9, "商品名称", "bspmc" }, { 10, "规格", "invspec" }, { 11, "单位", "measurename" }, { 12, "数量", "bnum" },
				{ 13, "单价", "bprice" }, { 14, "金额", "bhjje" }, { 15, "税率", "bspsl" }, { 16, "税额", "bspse" } };

		//新增新龙（百旺)导入 20191211
		Object[][] obj15 = new Object[][] { // 新龙销项模板导入
				{ 0, "发票类型", "iszhuan" },{ 2, "发票代码", "fp_dm" }, { 3, "发票号码", "fp_hm" }, { 4, "开票日期", "kprj" }, { 6, "购方纳税人识别号", "custidentno" },{ 7, "购方单位名称", "khmc" },
				{ 8, "购方单位地址电话", "ghfdzdh" },  { 9, "购方银行账号", "ghfyhzh" },

				{ 29, "备注", "demo" }, // 暂时将单据号放在备注栏中
				{ 11, "货物或应税劳务名称", "spmc" }, { 20, "金额", "hjje" }, // 合计金额
				{ 21, "税额", "spse" }, // 合计税额

				{ 11, "货物或应税劳务名称", "bspmc" }, { 12, "规格型号", "invspec" }, { 13, "单位", "measurename" }, { 14, "数量", "bnum" },
				{ 15, "单价", "bprice" }, { 16, "金额", "bhjje" }, { 17, "税率", "bspsl" }, { 18, "税额", "bspse" },
				{ 1, "发票状态", "invstatus" } };



		Object[][] obj2 = new Object[][] { // 航信导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "购方企业名称", "khmc" }, { 3, "购方税号", "custidentno" },
				{ 4, "银行账号", "ghfyhzh" }, { 5, "地址电话", "ghfdzdh" }, { 6, "开票日期", "kprj" }, { 8, "单据号", "demo" }, // 暂时将单据号放在备注栏中
				{ 9, "商品名称", "spmc" }, { 14, "金额", "hjje" }, // 合计金额
				{ 16, "税额", "spse" }, // 合计税额

				{ 9, "商品名称", "bspmc" }, { 10, "规格", "invspec" }, { 11, "单位", "measurename" }, { 12, "数量", "bnum" },
				{ 13, "单价", "bprice" }, { 14, "金额", "bhjje" }, { 15, "税率", "bspsl" }, { 16, "税额", "bspse" } };
		Object[][] obj22 = new Object[][] { // 航信机动车导入
				{ 0, "发票代码", "fp_dm" }, { 1, "发票号码", "fp_hm" }, { 2, "开票日期", "kprj" }, { 3, "购方单位名称", "khmc" },
				{ 5, "购方单位识别号", "custidentno" }, { 6, "车辆类型", "spmc" },  
				{ 14, "价税合计", "jshj" }, { 15, "电话", "xfdh" },{ 16, "账号", "xfzh" }, { 17, "地址", "xfdz" },{ 18, "开户银行", "xfyh" },
				{ 19, "增值税税率", "spsl" }, { 20, "增值税税额", "spse" },{ 21, "不含税价", "hjje" },
						
				{ 6, "车辆类型", "bspmc" }, { 7, "厂牌型号", "invspec" },
				{ 21, "不含税价", "bprice" }, {21, "不含税价", "bhjje" }, { 19, "增值税税率", "bspsl" }, { 20, "增值税税额", "bspse" } };
		Object[][] obj0 = new Object[][] { // 通用导入
				{ 1, "发票代码", "fp_dm" }, { 2, "发票号码", "fp_hm" }, { 3, "开票日期", "kprj" }, { 4, "开票项目", "spmc" },
				{5,"规格型号","invspec"},{6,"计量单位","measurename"},
				{ 7, "数量", "bnum" }, {8, "金额", "hjje" }, { 9, "税额", "spse" }, {10, "业务类型", "busitypetempname" },
				{ 11, "客户名称", "khmc" } };

		STYLE.put(BAIWANG_EXCEL, obj1);
		STYLE.put(BAIWANG_another_EXCEL, obj11);
		STYLE.put(BAIWANG_JDC_EXCEL, obj12);
		//新增新百旺导入 20191204
		STYLE.put(BAIWANG_NEW_EXCEL, obj13);
		STYLE.put(BAIWANG_NEW_another_EXCEL, obj14);

		//新龙导入 20191211
		STYLE.put(XINLONG_EXCEL, obj15);

		STYLE.put(HANG_TIAN_EXCEL, obj2);
		STYLE.put(HANG_JDC_EXCEL, obj22);
		STYLE.put(TONGYONG_EXCEL, obj0);
		return STYLE;
	}

	private DZFBoolean isZhuan(CorpVO corpvo) {
		return (StringUtil.isEmpty(corpvo.getChargedeptname()) || SMALL_TAX.equals(corpvo.getChargedeptname()))
				? DZFBoolean.FALSE : DZFBoolean.TRUE;
	}

	private boolean isXml(String fileType) throws DZFWarpException {
		boolean result = false;
		if (StringUtil.isEmptyWithTrim(fileType))
			throw new BusinessException("未能解析上传文件格式，请检查");

		if (suff_xml.equals(fileType))
			result = true;

		return result;
	}

	private boolean isExcel(String fileType) throws DZFWarpException {
		boolean result = false;
		if (suff_xls.equals(fileType) || suff_xlsx.equals(fileType)) {
			result = true;
		}

		return result;
	}

	private boolean isZip(String fileType) throws DZFWarpException {
		boolean result = false;
		if (suff_zip.equals(fileType)) {
			result = true;
		}

		return result;
	}

	private List<VATSaleInvoiceVO2> readZipFile(MultipartFile file, String pk_corp, String userid, StringBuffer msg)
			throws DZFWarpException {
		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		BufferedReader br = null;
		InputStreamReader is = null;
		InputStream in = null;
		ZipInputStream zin = null;
		InputStream fis = null;
		Integer flag = null;
		ZipFile zf = null;
		try {

			zf = new ZipFile((File) file);
			fis = file.getInputStream();
			in = new BufferedInputStream(fis);
			zin = new ZipInputStream(in);

			ZipEntry ze;
			while ((ze = zin.getNextEntry()) != null) {
				if (ze.isDirectory()) {
				} else {
					long size = ze.getSize();
					flag = calcName(file);
					if (size > 0 && flag != NO_IMP) {
						is = new InputStreamReader(zf.getInputStream(ze));
						br = new BufferedReader(is);
						getDataByXml(br, pk_corp, userid, flag, list, msg);
						br.close();
					}
				}
			}
			zin.closeEntry();
		} catch (IOException e) {

		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
				}
			}

			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
				}
			}
			if (zf != null) {
				try {
					zf.close();
				} catch (IOException e) {
				}
			}

			if (zin != null) {
				try {
					zin.close();
				} catch (IOException e) {
				}
			}
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
				}
			}
		}

		return list;
	}

	private Integer calcName(MultipartFile file) {
		
		try {
			SAXReader reader = new SAXReader();
			Document document = reader.read((File) file);
			Element bookStore = document.getRootElement();
			if (bookStore.element("YKFP")!=null)
				return BAI_WANG_A;
			else if (bookStore.element("sbbZzsfpkjmx")!=null)
				return BAI_WANG_B;
			else{
				return NO_IMP;
			}	
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			throw new BusinessException("文件格式错误，请检查");
		}
		// return flag;
	}

	private void getDataByXml(BufferedReader file, String pk_corp, String userid, Integer flag,
			List<VATSaleInvoiceVO2> list, StringBuffer msg) throws DZFWarpException {

		// List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFBoolean ISZHUAN = (StringUtil.isEmpty(corpvo.getChargedeptname())
				|| SMALL_TAX.equals(corpvo.getChargedeptname())) ? DZFBoolean.FALSE : DZFBoolean.TRUE;
		SAXReader reader = new SAXReader();
		StringBuffer sf = new StringBuffer();
		try {
			Document document = reader.read(file);

			Element bookStore = document.getRootElement().element("sbbZzsfpkjmx");

			Iterator firstIt = bookStore.elementIterator("body");

			Element secondEl = null;
			Iterator secondIt = null;

			Element threeEl = null;
			Iterator threeIt = null;

			Element fourEl = null;
			Iterator fourIt = null;
			VATSaleInvoiceVO2 vo = null;
			String zfbz = null;
			String kprj = null;
			int count = 0;
			StringBuffer innermsg = new StringBuffer();
			// 遍历body节点
			while (firstIt.hasNext()) {
				secondEl = (Element) firstIt.next();

				secondIt = secondEl.elementIterator("zyfpkjmx");
				ISZHUAN = DZFBoolean.TRUE;
				while (secondIt.hasNext()) {
					threeEl = (Element) secondIt.next();

					threeIt = threeEl.elementIterator("mxxx");

					while (threeIt.hasNext()) {
						fourEl = (Element) threeIt.next();
						zfbz = fourEl.elementTextTrim("zfbz");
						count++;
						if (!("Y".equals(zfbz) || "y".equals(zfbz))) {
							vo = new VATSaleInvoiceVO2();
							vo.setFp_dm(fourEl.elementTextTrim("fpdm"));
							vo.setFp_hm(fourEl.elementTextTrim("fphm"));
							kprj = fourEl.elementTextTrim("kprq");
							vo.setKprj(new DZFDate(StringUtil.isEmpty(kprj) ? ""
									: kprj.substring(0, 4) + "-" + kprj.substring(4, 6) + "-" + kprj.substring(6, 8)));
							vo.setHjje(new DZFDouble(fourEl.elementTextTrim("je")));
							vo.setSpse(new DZFDouble(fourEl.elementTextTrim("se")));
							vo.setInvstatus(vo.INVMODEL_1);// 正常发票

							innermsg.setLength(0);

							checkDataValid(vo, innermsg, count, pk_corp, null,null);

							if (innermsg.length() != 0) {
								msg.append(innermsg);
								continue;
							}

							setDefaultValue(pk_corp, userid, vo, ISZHUAN, null,null);
							list.add(vo);
						}

					}
				}

				secondIt = secondEl.elementIterator("ptfpkjmx");
				ISZHUAN = DZFBoolean.FALSE;
				while (secondIt.hasNext()) {
					threeEl = (Element) secondIt.next();

					threeIt = threeEl.elementIterator("mxxx");

					while (threeIt.hasNext()) {
						fourEl = (Element) threeIt.next();
						zfbz = fourEl.elementTextTrim("zfbz");
						count++;
						if (!("Y".equals(zfbz) || "y".equals(zfbz))) {
							vo = new VATSaleInvoiceVO2();
							vo.setFp_dm(fourEl.elementTextTrim("fpdm"));
							vo.setFp_hm(fourEl.elementTextTrim("fphm"));
							kprj = fourEl.elementTextTrim("kprq");
							vo.setKprj(new DZFDate(StringUtil.isEmpty(kprj) ? ""
									: kprj.substring(0, 4) + "-" + kprj.substring(4, 6) + "-" + kprj.substring(6, 8)));
							vo.setHjje(new DZFDouble(fourEl.elementTextTrim("je")));
							vo.setSpse(new DZFDouble(fourEl.elementTextTrim("se")));
							vo.setInvstatus(vo.INVMODEL_1);

							innermsg.setLength(0);

							checkDataValid(vo, sf, count, pk_corp, null,null);

							if (innermsg.length() != 0) {
								msg.append(innermsg);
								continue;
							}

							setDefaultValue(pk_corp, userid, vo, ISZHUAN, null,null);
							list.add(vo);
						}

					}
				}
			}
			if (!StringUtil.isEmpty(sf.toString())) {
				throw new BusinessException(sf.toString());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			throw new BusinessException("文件格式错误，请检查");
		}

	}

	private void getSXDataByXml(BufferedReader file, String pk_corp, String userid, Integer flag,
			List<VATSaleInvoiceVO2> list, StringBuffer msg) throws DZFWarpException {

		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFBoolean ISZHUAN = (StringUtil.isEmpty(corpvo.getChargedeptname())
				|| SMALL_TAX.equals(corpvo.getChargedeptname())) ? DZFBoolean.FALSE : DZFBoolean.TRUE;
		SAXReader reader = new SAXReader();
		// StringBuffer sf = new StringBuffer();
		try {
			Document document = reader.read(file);

			Element bookStore = document.getRootElement();

			Iterator it = bookStore.elementIterator();

			VATSaleInvoiceVO2 vo = null;

			Element row = null;
			Element child = null;
			Iterator itt = null;
			List<Attribute> attrs = null;
			String name = null;
			int count = 0;
			StringBuffer innermsg = new StringBuffer();
			while (it.hasNext()) {
				row = (Element) it.next();
				itt = row.elementIterator();
				while (itt.hasNext()) {
					count++;
					vo = new VATSaleInvoiceVO2();
					child = (Element) itt.next();
					attrs = child.attributes();
					for (Attribute attr : attrs) {
						name = attr.getName();
						vo.setAttributeValue(getInstance().get(flag).get(name), attr.getValue());
					}
					innermsg.setLength(0);
					checkDataValid(vo, innermsg, count, pk_corp, null,null);

					if (innermsg.length() != 0) {
						msg.append(innermsg);
					} else if (vo.INVMODEL_1.equals(vo.getInvstatus()) || vo.INVMODEL_4.equals(vo.getInvstatus())) {
						setDefaultValue(pk_corp, userid, vo, ISZHUAN, null,null);
						list.add(vo);
					}

				}
			}
			// if(!StringUtil.isEmpty(sf.toString())){
			// throw new BusinessException(sf.toString());
			// }
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			if (e instanceof BusinessException)
				throw new BusinessException(e.getMessage());
			throw new BusinessException("文件格式错误，请检查");
		}

	}

	// private YntCpaccountVO getAccountReceivable(String pk_corp){
	// YntCpaccountVO[] cpavos = AccountCache.getInstance().get(null, pk_corp);
	// YntCpaccountVO vo = null;
	// if(cpavos != null || cpavos.length > 0){
	// for(YntCpaccountVO cpavo : cpavos){
	// if(ACCOUNTRECEIVABLENAME.equals(cpavo.getAccountcode())){
	// vo = cpavo;
	// break;
	// }
	// }
	// }
	//
	// return vo;
	// }

	private void checkDataValid(VATSaleInvoiceVO2 vo, StringBuffer sf, int index, String pk_corp, String fileType,Integer sourceType) {
		StringBuffer msg = new StringBuffer();
		if (StringUtil.isEmpty(vo.getInvstatus()) && !isExcel(fileType)) {
			msg.append(" 发票状态不允许为空,请检查！ ");
		}
		if (StringUtil.isEmpty(vo.getFp_hm())) {
			msg.append(" 发票号码不允许为空,请检查！ ");
		}
		if (StringUtil.isEmpty(vo.getFp_dm())) {
			msg.append(" 发票代码不允许为空,请检查！ ");
		}
		 if(sourceType==TONGYONG_EXCEL&&StringUtil.isEmpty(vo.getSpmc())){
		 msg.append(" 开票项目不允许为空,请检查！ ");
		 }
		if (vo.getKprj() == null) {
			msg.append(" 开票日期不允许为空,请检查！ ");
		}
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		if (vo.getKprj() != null && vo.getKprj().before(corpVO.getBegindate())) {
			msg.append(" 开票日期不允许在建账日期前，请检查！ ");
		}
		if (vo.getSpse() == null) {
			msg.append(" 税额不允许为空,请检查！ ");
		}
		if (vo.getHjje() == null) {
			msg.append(" 合计金额不允许为空,请检查！ ");
		}
		// if(StringUtil.isEmpty(vo.getKhmc())){
		// msg.append(" 客户名称不允许为空,请检查！ ");
		// }

		if (!StringUtil.isEmpty(msg.toString())) {
			sf.append("<p>第").append(index + 1).append("行  ").append(msg.toString()).append(" </p> ");
		}
	}

	/**
	 * 设置默认值
	 * 
	 * @param pk_corp
	 * @param userid
	 * @param vo
	 */
	private void setDefaultValue(String pk_corp, String userid, VATSaleInvoiceVO2 vo, DZFBoolean iszhuan,
			String unitName,Integer sourceType) {
		vo.setPk_corp(pk_corp);
		vo.setCoperatorid(userid);
		vo.setDoperatedate(new DZFDate());

		if (!StringUtil.isEmpty(unitName)) {
			vo.setXhfmc(unitName);
		}

		// 设置税率
		DZFDouble sl = vo.getSpsl();
		if (sl == null || sl.doubleValue() == DZFDouble.ZERO_DBL.doubleValue()) {
			vo.setSpsl(SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100)));
			vo.setSpsl(vo.getSpsl().setScale(0, DZFDouble.ROUND_HALF_UP));
		}

		// 设置价税合计
		vo.setJshj(SafeCompute.add(vo.getSpse(), vo.getHjje()));
		// 设置期间
		vo.setPeriod(DateUtils.getPeriod(vo.getKprj()));
		vo.setInperiod(vo.getPeriod());
		// 设置来源
		vo.setSourcetype(IBillManageConstants.AUTO);

		// 专普票标识
		//sourceType只有导入excel文件时此字段才有值
		if(sourceType!=null){
			if(sourceType==TONGYONG_EXCEL){
				iszhuan = isZhuan(corpService.queryByPk(pk_corp));
				vo.setIszhuan(iszhuan);
			}else if(sourceType==BAIWANG_EXCEL||sourceType==BAIWANG_another_EXCEL||sourceType==HANG_TIAN_EXCEL || sourceType==BAIWANG_NEW_EXCEL  || sourceType==BAIWANG_NEW_another_EXCEL || sourceType == XINLONG_EXCEL){
				if(iszhuan!=null){
					vo.setIszhuan(iszhuan);
				}else{
					vo.setIszhuan(DZFBoolean.FALSE);
				}
			}else if(sourceType==HANG_JDC_EXCEL||sourceType==BAIWANG_JDC_EXCEL){
				vo.setIszhuan(DZFBoolean.TRUE);
			}else{
				iszhuan = isZhuan(corpService.queryByPk(pk_corp));
				vo.setIszhuan(iszhuan);
			}
		}else{
			if (!StringUtil.isEmpty(vo.getInvmodel())) {
				if (vo.getInvmodel().endsWith(VAT_ZHUAN)) {
					vo.setIszhuan(DZFBoolean.TRUE);
				} else if (vo.getInvmodel().endsWith(VAT_PU)) {
					vo.setIszhuan(DZFBoolean.FALSE);
				} else {
					vo.setIszhuan(iszhuan);
				}
			}else if (!StringUtil.isEmpty(vo.getFp_dm()) && vo.getFp_dm().length() >= 8) {// 判断专普票的规则
				// 第8位代表发票种类，1代表专票、3代表普票
				char lb = vo.getFp_dm().charAt(7);
				iszhuan = lb == '1' ? DZFBoolean.TRUE : DZFBoolean.FALSE;
				vo.setIszhuan(iszhuan);
				} else {
				vo.setIszhuan(iszhuan);
				}
		}
		
	}

	/**
	 * 字段对照关系
	 * 
	 * @return
	 */
	public static List<Map<String, String>> getInstance() {
		// if(STYLE_LIST == null || STYLE_LIST.size() == 0){
		List<Map<String, String>> STYLE_LIST = new ArrayList<Map<String, String>>();
		Map<String, String> style = new HashMap<String, String>();

		style.put("发票类型", "invmodel");
		style.put("发票状态", "invstatus");
		style.put("发票代码", "fp_dm");
		style.put("发票号码", "fp_hm");
		style.put("客户名称", "khmc");
		style.put("主要商品名称", "spmc");
		style.put("税额", "spse");
		style.put("合计金额", "hjje");
		style.put("价税合计", "jshj");
		style.put("原发票代码", "yfp_dm");
		style.put("原发票号码", "yfp_hm");
		style.put("通知单编号", "noticebillno");
		style.put("开票人", "kprname");
		style.put("开票日期", "kprj");
		style.put("作废人", "zfrname");
		style.put("作废日期", "zfrj");
		style.put("客户识别号", "custidentno");

		STYLE_LIST.add(BAI_WANG_A, style);

		style = new HashMap<String, String>();
		style.put("fpdm", "fp_dm");
		style.put("fphm", "fp_hm");
		style.put("kprq", "kprj");
		style.put("je", "hjje");
		style.put("se", "spse");
		style.put("zfbz", "invstatus");
		STYLE_LIST.add(BAI_WANG_B, style);

		style = new HashMap<String, String>();
		style.put("fpdm", "fp_dm");
		style.put("fphm", "fp_hm");
		style.put("kprq", "kprj");
		style.put("je", "hjje");
		style.put("se", "spse");
		style.put("zfbz", "invstatus");
		STYLE_LIST.add(HANG_TIAN, style);
		// }
		return STYLE_LIST;
	}
	
	/*private Map<String, DcModelHVO> hashliseBusiTypeMap(List<DcModelHVO> dcList){
		Map<String, DcModelHVO> map = new LinkedHashMap<String, DcModelHVO>();
		if(dcList != null && dcList.size() > 0){
			String key;
			for(DcModelHVO hvo : dcList){
				key = hvo.getBusitypetempname()
						+ "," + hvo.getVspstylecode()
						+ "," + hvo.getSzstylecode();
				if(!map.containsKey(key)){
					map.put(key, hvo);
				}
						
			}
		}
		
		return map;
	}*/

	/*@Override
	public String saveBusiType(VATSaleInvoiceVO2[] vos, String busiid, 
			String businame, 
			String selvalue, 
			String userid, 
			String pk_corp) throws DZFWarpException {
		VATSaleInvoiceVO2 newVO = null;
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		//过滤数据
//		dcList = new DcPzmb().filterDataCommon(dcList, pk_corp, null, null, "Y", null);

//		Map<String, DcModelHVO> dcmap = DZfcommonTools.hashlizeObjectByPk(dcList, 
//				new String[]{"busitypetempname", "vspstylecode", "szstylecode"});
		Map<String, DcModelHVO> dcmap = hashliseBusiTypeMap(dcList);
		int upCount = 0;
		int npCount = 0;
		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		String stylecode;
		String key;
		DcModelHVO dchvo;
		StringBuffer innermsg = new StringBuffer();
		String ims;
		for (VATSaleInvoiceVO2 vo : vos) {

			if (!StringUtil.isEmptyWithTrim(vo.getPk_tzpz_h())) {
				ims = String.format("<font color='red'><p>销项发票[%s_%s]已生成凭证</p></font>", 
						vo.getFp_dm(), vo.getFp_hm());
				innermsg.append(ims);
				npCount++;
				continue;
			}

			stylecode = vo.getIsZhuan() != null && 
					vo.getIsZhuan().booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
			key = businame + "," + stylecode + "," + selvalue;
			dchvo = dcmap.get(key);
			if(dchvo == null){
				String zhflag = stylecode.equals(FieldConstant.FPSTYLE_01) ? "专票" : "普票";
				ims = String.format("<font color='red'><p>销项发票[%s_%s]为%s与入账模板票据类型不一致，请检查</p></font>",
						vo.getFp_dm(), vo.getFp_hm(), zhflag);
				innermsg.append(ims);
				npCount++;
				continue;
			}
			
			newVO = new VATSaleInvoiceVO2();
			newVO.setPk_vatsaleinvoice(vo.getPk_vatsaleinvoice());
			newVO.setPk_model_h(dchvo.getPk_model_h());
			upCount++;
			list.add(newVO);
		}

		if (list != null && list.size() > 0) {
			singleObjectBO.updateAry(list.toArray(new VATSaleInvoiceVO2[0]), new String[] { "pk_model_h" });
		}
		
		VatBusinessTypeVO typevo = new VatBusinessTypeVO();
		if(StringUtil.isEmpty(busiid)){
			typevo.setBusiname(businame);
			typevo.setSelectvalue(selvalue);
			typevo.setCoperatorid(userid);
			typevo.setDoperatedate(new DZFDate());
			typevo.setPk_corp(pk_corp);
			typevo.setStype(IBillManageConstants.HEBING_JXFP);
			singleObjectBO.saveObject(pk_corp, typevo);
		}else{
			typevo.setSelectvalue(selvalue);
			typevo.setPk_vatbusitype(busiid);
			singleObjectBO.update(typevo, new String[]{"selectvalue"});
		}

//		return new StringBuffer().append("入账设置更新成功 ").append(upCount).append(" 条")
//				.append(npCount > 0 ? ",未更新 " + npCount + " 条" : "").toString();
		StringBuffer msg = new StringBuffer();
		msg.append("业务类型设置更新成功 ").append(upCount).append(" 条");
		msg.append(npCount > 0 ? ",未更新 " + npCount + " 条,详细原因如下:" + innermsg.toString() : "");
		return msg.toString();
	}*/

	@Override
	public TzpzHVO getTzpzHVOByID(VATSaleInvoiceVO2[] vos, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway)
			throws DZFWarpException {
		List<VATSaleInvoiceVO2> sencodevos = new ArrayList<VATSaleInvoiceVO2>();

		DZFDate maxDate = null;
		List<String> pks = new ArrayList<String>();
		for (VATSaleInvoiceVO2 vo : vos) {
			if ((!StringUtil.isEmpty(vo.getPrimaryKey()) && StringUtil.isEmpty(vo.getPk_tzpz_h()))
			// && (vo.getIsic() == null || vo.getIsic().booleanValue())
			) {
				sencodevos.add(vo);
				pks.add(vo.getPrimaryKey());
			}

			// 找最大的期间
			if (maxDate == null) {
				maxDate = vo.getKprj();
			} else if (maxDate.before(vo.getKprj())) {
				maxDate = vo.getKprj();
			}
		}

		if (sencodevos.size() == 0)
			return null;

		List<VATSaleInvoiceVO2> afterList = constructVatSale(sencodevos.toArray(new VATSaleInvoiceVO2[0]), pk_corp);

		if (afterList == null || afterList.size() == 0)
			throw new BusinessException("查询销项发票清单信息不存在，请检查");

		checkisGroup(afterList, pk_corp);

		CorpVO corpVO = corpService.queryByPk(pk_corp);

		/*YntCpaccountVO[] accounts = AccountCache.getInstance().get(null, pk_corp);
		DcModelHVO mHVO = null;
		Map<String, DcModelHVO> dcmap = gl_yhdzdserv2.queryDcModelVO(pk_corp);

		mHVO = dcmap.get(afterList.get(0).getPk_model_h());

		if (mHVO == null)
			throw new BusinessException("销项发票:未能找到对应的业务类型模板，请检查");

		DcModelBVO[] models = mHVO.getChildren();
		OcrImageLibraryVO lib = null;
		OcrInvoiceDetailVO[] detailvos = null;*/
		int fp_style;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		List<TzpzBVO> inlist = null;
		Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpVO.getPk_corp());
		YntCpaccountVO[] accVOs=accountService.queryByPk(corpVO.getPk_corp());
		
		Map<String, Object> paramMap=zncsVoucher.initVoucherParam(corpVO, afterList.get(0).getInperiod(),false);
		List<List<Object[]>> levelList=(List<List<Object[]>>) paramMap.get("levelList");
		Map<String, Object[]> categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
		Set<String> zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
		InventorySetVO inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
		Map<String, InventoryAliasVO> fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
		List<Object> paramList = (List<Object>) paramMap.get("paramList");
		Map<String, BdCurrencyVO> currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
		Map<String, Object[]> rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
		Map<String, String> bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
		Map<String, AuxiliaryAccountBVO> assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
		Map<String, List<AccsetVO>> accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
		Map<String, String> jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
		String tradeCode=(String) paramMap.get("tradeCode");
		String newrule = (String) paramMap.get("newrule");
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");
		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);

		for (VATSaleInvoiceVO2 vo : afterList) {
			if(StringUtils.isEmpty(vo.getPk_model_h())){
				throw new BusinessException("销项发票:业务类型为空,请重新选择业务类型");
			}
			fp_style = getFpStyle(vo);
			headVO.setFp_style(fp_style);
			/*lib = changeOcrInvoiceVO(vo, pk_corp, ifptype, fp_style);
			detailvos = changeOcrInvoiceDetailVO(vo, pk_corp);*/
			inlist = new ArrayList<TzpzBVO>();
			ArrayList<VATSaleInvoiceVO2> voList = new ArrayList<VATSaleInvoiceVO2>();
			voList.add(vo);
			Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
			List<OcrInvoiceVO> invoiceList = changeToOcr(voList, pk_corp);
			List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, vo.getInperiod(), pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpVO, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
			TzpzBVO[] pzbvoArr = (TzpzBVO[])tzpzhvoList.get(0).getChildren();
			inlist.addAll(Arrays.asList(pzbvoArr));
			//aitovoucherserv_vatinvoice.getTzpzBVOList(headVO, models, lib, detailvos, inlist, accounts);
			//根据规则设置摘要q
			setPzZy(inlist, setvo, vo);
			tblist.addAll(inlist);
		}
		
		//aitovoucherserv_vatinvoice.setSpeaclTzpzBVO1(headVO, lib, tblist);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		buildFzhs(tblist, pk_corp);

		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : tblist) {
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}
		// 排序
		if (tblist != null && tblist.size() > 0) {
			Collections.sort(tblist, new Comparator<TzpzBVO>() {
				@Override
				public int compare(TzpzBVO o1, TzpzBVO o2) {
					int i = 0;
					if (o1.getRowno() != null && o2.getRowno() != null) {
						i = o1.getRowno().compareTo(o2.getRowno());
					} else if (o1.getRowno() != null) {
						i = 1;
					} else if (o2.getRowno() != null) {
						i = -1;
					}
					return i;
				}
			});
		}

		afterList.get(0).setKprj(maxDate);
		afterList.get(0).setCount(afterList.size());
		createTzpzHVO(headVO, afterList, pk_corp, userid, null, null, totalMny, accway,null,setvo);

		// headvo sourcebillid重新赋值
		pks = new ArrayList<String>();
		for (VATSaleInvoiceVO2 svo : afterList) {
			pks.add(svo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));
		headVO.setSourcebillid(sourcebillid);
		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));

		return headVO;
	}

	private void buildFzhs(List<TzpzBVO> tblist, String pk_corp) {
		List<InventoryVO> invenList = null;
		Map<String, InventoryVO> invenMap = null;

		List<AuxiliaryAccountBVO> fzhsList;
		AuxiliaryAccountBVO aabvo;
		InventoryVO invenvo = null;
		String value;
		String pk_inventory;
		for (TzpzBVO bvo : tblist) {
			fzhsList = new ArrayList<AuxiliaryAccountBVO>();
			for (int i = 1; i < 11; i++) {// 辅助核算
				value = (String) bvo.getAttributeValue("fzhsx" + i);

				if (!StringUtil.isEmpty(value)) {
					aabvo = gl_fzhsserv.queryBByID(value, pk_corp);

					if (aabvo != null) {
						fzhsList.add(aabvo);
					}
				}
			}

			if (fzhsList.size() > 0) {
				bvo.setFzhs_list(fzhsList);
			}

			pk_inventory = bvo.getPk_inventory();// 存货
			if (!StringUtil.isEmpty(pk_inventory)) {
				if (invenList == null) {
					invenList = invservice.query(pk_corp);
					if (invenList != null && invenList.size() > 0) {
						invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "pk_inventory" });
					}
				}

				if (invenMap != null) {
					invenvo = invenMap.get(pk_inventory);
				}

				if (invenvo != null) {
					bvo.setInvcode(invenvo.getCode());
					bvo.setInvname(invenvo.getName());
					bvo.setMeaname(invenvo.getMeasurename());
				}
			}
		}
	}

	// private List<TzpzBVO> createCombinTzpzBVO(List<VATSaleInvoiceVO2> vos,
	// Map<String, VATSaleInvoiceVO2> bkmap,
	// DcModelHVO mHVO,
	// String pk_curr,
	// Map<String, YntCpaccountVO> ccountMap,
	// Map<String, Boolean> isTempMap,
	// Map<String, TaxitemVO> taxItemMap,
	// String userid,
	// String pk_corp,
	// boolean isNewFz){
	// List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
	// Boolean isTemp = null;
	// DcModelBVO[] mBVOs = mHVO.getChildren();
	// DZFDouble mny = null;
	// TzpzBVO tzpzbvo = null;
	// YntCpaccountVO cvo = null;
	// VATSaleInvoiceVO2 vo = null;
	// String key = null;
	// Map<String, TzpzBVO> tzpzbmap = new HashMap<String, TzpzBVO>();
	// CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
	// YntCpaccountVO[] account = AccountCache.getInstance().get(null, pk_corp);
	//
	//// TzpzBVO temptzpz = null;
	// List<TzpzBVO> tzpzList = null;
	//
	// int rowno = 1;//分录行排序
	// for(DcModelBVO bvo : mBVOs){
	// for(Map.Entry<String, VATSaleInvoiceVO2> entry : bkmap.entrySet()){
	// key = entry.getKey();
	// vo = entry.getValue();
	// if("$$$".equals(key)){
	// vo.setKhmc("");
	// }
	// tzpzbvo = new TzpzBVO();
	// cvo = ccountMap.get(bvo.getPk_accsubj());
	// if(cvo == null){
	// log.error("业务类型模板涉及主表："+
	// mHVO.getPrimaryKey()+",子表："+bvo.getPrimaryKey());
	// throw new BusinessException(mHVO.getBusitypetempname() +
	// "业务类型科目未找到，请检查!");
	// }
	//
	// isTemp = isTempMap.get(ISTEMP);
	// if(StringUtil.isEmpty(vo.getKhmc())){
	// isTemp = true;
	// isTempMap.put(ISTEMP, Boolean.TRUE);
	// }else{
	// tzpzList = new ArrayList<TzpzBVO>();
	// cvo = buildTzpzBVoByAccount(cvo, tzpzbvo, tzpzList, vo, corpvo, bvo,
	// account, isNewFz, isTempMap, 1);
	// }
	//
	// if(tzpzList == null || tzpzList.size() == 0){
	// mny = (DZFDouble) vo.getAttributeValue(bvo.getVfield());
	//
	// tzpzbvo.setRowno(rowno++);//设置分录行编号
	//
	// if(bvo.getDirection() == 0){
	// tzpzbvo.setJfmny(mny);
	// tzpzbvo.setYbjfmny(mny);
	// tzpzbvo.setVdirect(0);
	// }else{
	// tzpzbvo.setDfmny(mny);
	// tzpzbvo.setYbdfmny(mny);
	// tzpzbvo.setVdirect(1);
	// }
	//
	// setTzpzBVOValue(tzpzbvo, pk_curr, cvo, bvo, vo);
	// dealTaxItem(tzpzbvo, null, vo, vo.getSpsl(), cvo);
	// buildTzpzBMap(tzpzbvo, tzpzbmap, bodyList);
	// }else{
	// for(TzpzBVO tzpzchild : tzpzList){
	// tzpzchild.setRowno(rowno++);
	//
	// setTzpzBVOValue(tzpzchild, pk_curr, cvo, bvo, vo);
	// dealTaxItem(tzpzchild, null, vo, vo.getSpsl(), cvo);
	// buildTzpzBMap(tzpzchild, tzpzbmap, bodyList);
	// }
	// }
	//
	//// dealTaxItem(taxItemMap, cvo, vo, tzpzbvo, pk_corp, userid);
	// cvo = null;
	// }
	// }
	// return bodyList;
	// }

	// private void buildTzpzBMap(TzpzBVO tzpzbvo, Map<String, TzpzBVO>
	// tzpzbmap, List<TzpzBVO> bodyList){
	// String key = new StringBuffer().append(tzpzbvo.getPk_accsubj())
	// .append("_")
	// .append(tzpzbvo.getFzhsx1())
	// .append("_")
	// .append(tzpzbvo.getFzhsx6())
	// .append("_")
	// .append(tzpzbvo.getPk_inventory())
	// .toString();
	// TzpzBVO temptzpz = null;
	//
	// if(tzpzbmap.containsKey(key)){
	// temptzpz = tzpzbmap.get(key);
	// temptzpz.setJfmny(SafeCompute.add(temptzpz.getJfmny(),
	// tzpzbvo.getJfmny()));
	// temptzpz.setYbjfmny(SafeCompute.add(temptzpz.getYbjfmny(),
	// tzpzbvo.getYbjfmny()));
	// temptzpz.setDfmny(SafeCompute.add(temptzpz.getDfmny(),
	// tzpzbvo.getDfmny()));
	// temptzpz.setYbdfmny(SafeCompute.add(temptzpz.getYbdfmny(),
	// tzpzbvo.getYbdfmny()));
	// }else{
	// tzpzbmap.put(key, tzpzbvo);
	// bodyList.add(tzpzbvo);
	// }
	// }

	// private List<TzpzBVO> createCombinTzpzBVO(List<VATSaleInvoiceVO2> vos,
	// Map<String, VATSaleInvoiceVO2> bkmap,
	// DcModelHVO mHVO,
	// String pk_curr,
	// Map<String, YntCpaccountVO> accmap,
	// Map<String, Boolean> isTempMap,
	// boolean isNewFz){
	// Map<String, TzpzBVO> tzpzMap = new HashMap<String, TzpzBVO>();
	// List<TzpzBVO> tzpzList = null;
	// String key = null;
	// TzpzBVO innervo = null;
	// for(VATSaleInvoiceVO2 vo : vos){
	// tzpzList = null;//createTzpzBVO(vo, bkmap, pk_curr, accmap, isTempMap,
	// isNewFz);
	// if(tzpzList == null || tzpzList.size() == 0){
	// continue;
	// }
	//
	// for(TzpzBVO tzpzbvo : tzpzList){
	// key = "&" + tzpzbvo.getPk_accsubj() + "&" + tzpzbvo.getFzhsx1();
	// if(tzpzMap.containsKey(key)){
	// innervo = tzpzMap.get(key);
	// innervo.setJfmny(SafeCompute.add(innervo.getJfmny(),
	// tzpzbvo.getJfmny()));
	// innervo.setYbjfmny(SafeCompute.add(innervo.getYbjfmny(),
	// tzpzbvo.getYbjfmny()));
	//
	// innervo.setDfmny(SafeCompute.add(innervo.getDfmny(),
	// tzpzbvo.getDfmny()));
	// innervo.setYbdfmny(SafeCompute.add(innervo.getYbdfmny(),
	// tzpzbvo.getYbdfmny()));
	// }else{
	// tzpzMap.put(key, tzpzbvo);
	// }
	// }
	// }
	//
	// tzpzList = new ArrayList<TzpzBVO>();
	// for(Map.Entry<String, TzpzBVO> entry : tzpzMap.entrySet()){
	// innervo = entry.getValue();
	// tzpzList.add(innervo);
	// }
	//
	// return tzpzList;
	// }

	@Override
	public void checkBeforeCombine(VATSaleInvoiceVO2[] vos) throws DZFWarpException {
		List<String> pks = new ArrayList<String>();
		for (VATSaleInvoiceVO2 vo : vos) {
			if (!StringUtil.isEmpty(vo.getPrimaryKey())) {
				pks.add(vo.getPrimaryKey());
			}
		}

		StringBuffer sf = new StringBuffer();
		sf.append("select * from ynt_vatsaleinvoice  where nvl(dr,0) = 0 and ")
				.append(SqlUtil.buildSqlForIn("pk_vatsaleinvoice", pks.toArray(new String[pks.size()])));

		List<VATSaleInvoiceVO2> afterList = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sf.toString(), null,
				new BeanListProcessor(VATSaleInvoiceVO2.class));

		if (afterList == null || afterList.size() == 0)
			throw new BusinessException("查询销项发票清单信息不存在,请检查");

		// String period = null;
		String busipk = null;
		for (VATSaleInvoiceVO2 vo : afterList) {

			/*if (!StringUtil.isEmpty(busipk) && !busipk.equals(vo.getPk_model_h())) {
				throw new BusinessException("业务类型不一致,请检查");
			}*/
			busipk = vo.getPk_model_h();

			// if(!StringUtil.isEmpty(period)
			// && !period.equals(vo.getPeriod())){
			// throw new BusinessException("开票日期所在期间不一致,请检查");
			// }
			//
			// period = vo.getPeriod();
		}

		BillCategoryVO modelhvo = (BillCategoryVO) singleObjectBO.queryByPrimaryKey(BillCategoryVO.class, busipk);

		if (modelhvo == null || modelhvo.getDr() == 1) {
			throw new BusinessException("业务类型不存在或已被删除,请重新选择业务类型");
		}

	}

	@Override
	public List<String> getCustNames(String pk_corp, VATSaleInvoiceVO2[] vos) throws DZFWarpException {
		AuxiliaryAccountBVO[] bvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_CUSTOMER, pk_corp, null);
		List<String> custList = new ArrayList<String>();
		Map<String, String> map = new HashMap<String, String>();
		String busiName = null;

		if (vos != null && vos.length > 0) {
			for (VATSaleInvoiceVO2 vo : vos) {
				busiName = vo.getKhmc();
				if (!StringUtil.isEmpty(busiName) && !map.containsKey(busiName)) {
					custList.add(busiName);
					map.put(busiName, busiName);
				}
			}
		}

		if (bvos != null && bvos.length > 0) {
			for (AuxiliaryAccountBVO bvo : bvos) {
				busiName = bvo.getName();
				if (!StringUtil.isEmpty(busiName) && !map.containsKey(busiName)) {
					custList.add(busiName);
					map.put(busiName, busiName);
				}
			}
		}
		return custList;
	}
	
	/*@Override
	public List<String> getBusiTypes(String pk_corp) throws DZFWarpException {
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		List<String> busiList = new LinkedList<String>();
		if (dcList != null && dcList.size() > 0) {
			dcList = new DcPzmb().filterDataCommon(dcList, pk_corp, 
					null, null, "Y", null);
			String businame;
			Map<String, DcModelHVO> map = new HashMap<String, DcModelHVO>();
			for(DcModelHVO hvo : dcList){
				businame = hvo.getBusitypetempname();
				if(!map.containsKey(businame)){
					map.put(businame, hvo);
					busiList.add(businame);
				}
			}
		}
		return busiList;
	}*/

	@Override
	public VATSaleInvoiceVO2 queryByID(String pk) throws DZFWarpException {
		ArrayList<String> pk_categoryList = new ArrayList<String>();
		if (StringUtil.isEmpty(pk))
			throw new BusinessException("参数为空，请检查");
		StringBuffer sf = new StringBuffer();
		sf.append(" Select y.*,y.busitypetempname as busitypetempname,  c.settlement as busisztypecode ");
		sf.append(" From ynt_vatsaleinvoice y left join ynt_billcategory h on y.pk_model_h = h.pk_category ");
		//sf.append(" left join ynt_basecategory d on h.pk_basecategory=d.pk_basecategory ");
		sf.append(" left join ynt_categoryset c on h.pk_category=c.pk_category ");
		sf.append(" Where nvl(y.dr,0) = 0 and nvl(h.dr,0)=0 and nvl(c.dr,0)=0 and y.pk_vatsaleinvoice = ? ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk);

		VATSaleInvoiceVO2 vo = (VATSaleInvoiceVO2) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanProcessor(VATSaleInvoiceVO2.class));

		sf.setLength(0);
		sf.append(
				" select b.*,y.categoryname as billcategoryname from ynt_vatsaleinvoice_b b "
				+ " left join ynt_billcategory y on b.pk_billcategory = y.pk_category "
				+ " where nvl(b.dr,0) = 0 and nvl(y.dr,0)=0 and b.pk_vatsaleinvoice = ? order by b.rowno asc, b.rowid asc ");
		List<VATSaleInvoiceBVO2> blsit = (List<VATSaleInvoiceBVO2>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceBVO2.class));

		if (blsit != null && blsit.size() > 0) {
			for (VATSaleInvoiceBVO2 bvo : blsit) {
				pk_categoryList.add(bvo.getPk_billcategory());
			}
			//查询全名称
			Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList, vo.getInperiod(), vo.getPk_corp());
			for (VATSaleInvoiceBVO2 bvo : blsit) {
				bvo.setBillcategoryname(map.get(bvo.getPk_billcategory()));
			}
			vo.setChildren(blsit.toArray(new VATSaleInvoiceBVO2[0]));
		}

		return vo;
	}

	@Override
	public void saveCombinePZ(List<VATSaleInvoiceVO2> list, String pk_corp, String userid, String period,
			DZFBoolean lwflag, VatInvoiceSetVO setvo, boolean accway, boolean isT,List<List<Object[]>> levelList,Map<String, Object[]> categoryMap,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Set<String> zyFzhsList
			,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,InventorySetVO inventorySetVO,CorpVO corp,Map<String, InventoryAliasVO> fzhsBMMap
			,List<Object> paramList,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap
			,Map<String, AuxiliaryAccountBVO> assistMap,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, String> jituanSubMap,YntCpaccountVO[] accVOs
			,String tradeCode,String newrule,List<AuxiliaryAccountBVO> chFzhsBodyVOs) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);

		checkisGroup(list, pk_corp);// 校验

		/*YntCpaccountVO[] accounts = AccountCache.getInstance().get(null, pk_corp);
		OcrImageLibraryVO lib = null;
		OcrInvoiceDetailVO[] detailvos = null;*/
		int fp_style;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		List<TzpzBVO> inlist  = null;
		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		String maxPeriod = null;
		List<String> imageGroupList = new ArrayList<>();
		
		for (VATSaleInvoiceVO2 vo : list) {

			if (StringUtil.isEmpty(vo.getPk_model_h())) {
				throw new BusinessException("销项发票:业务类型为空,请重新选择业务类型");
			}

			fp_style = getFpStyle(vo);
			headVO.setFp_style(fp_style);
			inlist = new ArrayList<TzpzBVO>();
			ArrayList<VATSaleInvoiceVO2> voList = new ArrayList<VATSaleInvoiceVO2>();
			voList.add(vo);
			Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
			List<OcrInvoiceVO> invoiceList = changeToOcr(voList, pk_corp);
			List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, period, pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
			TzpzBVO[] pzbvoArr = (TzpzBVO[])tzpzhvoList.get(0).getChildren();
			inlist.addAll(Arrays.asList(pzbvoArr));
			// 找最大交易日期
			if (maxPeriod == null) {
				maxPeriod = vo.getInperiod();
			} else if (!StringUtil.isEmpty(vo.getInperiod()) && maxPeriod.compareTo(vo.getInperiod()) < 0) {
				maxPeriod = vo.getInperiod();
			}
			if (!StringUtil.isEmpty(vo.getPk_image_group()) && !imageGroupList.contains(vo.getPk_image_group())) {
				imageGroupList.add(vo.getPk_image_group());
			}
			//根据规则设置摘要q
			setPzZy(inlist, setvo, vo);
			tblist.addAll(inlist);
		}
		
		//aitovoucherserv_vatinvoice.setSpeaclTzpzBVO1(headVO, lib, tblist);

		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : tblist) {
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}

		list.get(0).setInperiod(maxPeriod);
		list.get(0).setCount(list.size());
		createTzpzHVO(headVO, list, pk_corp, userid, null, null, totalMny, accway,null,setvo);

		// headvo sourcebillid 重新赋值
		List<String> pks = new ArrayList<String>();
		for (VATSaleInvoiceVO2 svo : list) {
			pks.add(svo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));

		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));
		if (imageGroupList != null && imageGroupList.size() > 0) {
			// 合并图片组
//			String groupId = mergeImage(pk_corp, imageGroupList);
			String groupId = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			headVO.setPk_image_group(groupId);
			updateImageGroup(groupId);
		}

		if (isT) {
			createTempPZ(headVO, list.toArray(new VATSaleInvoiceVO2[list.size()]), pk_corp,null);
		} else {
			headVO.setSourcebillid(sourcebillid);
			headVO = voucher.saveVoucher(corpvo, headVO);
		}

		if (lwflag != null && lwflag.booleanValue()) {
			updateOtherType(list);// 劳务
		}

		// 更新业务类型标识
		/*if (vatModelList != null && vatModelList.size() > 0) {
			singleObjectBO.updateAry(vatModelList.toArray(new VATSaleInvoiceVO2[0]), new String[] { "pk_model_h" });
		}*/
	}

	// private DZFBoolean getCheckZhuanFlag(VATSaleInvoiceVO2 vo, DZFBoolean
	// iszhFlag){
	// DZFBoolean iszh = vo.getIsZhuan();
	//
	// iszh = iszh == null ? DZFBoolean.FALSE : iszh;
	// if(iszhFlag == null){
	// iszhFlag = iszh;
	// }else if(!iszh.equals(iszhFlag)){
	// throw new BusinessException("票据性质不同，专普票不允许合并，请检查");
	// }
	//
	// return iszhFlag;
	// }

	private void updateImageGroup(String pk_image_group) {
		// 图片生成凭证
		String sql = " update  ynt_image_group set  istate=?,isuer='Y' where  pk_image_group = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state100);
		sp.addParam(pk_image_group);
		singleObjectBO.executeUpdate(sql, sp);
	}

	/**
	 * 合并凭证图片组
	 * 
	 * @param pk_corp
	 * @param imageGroupList
	 * @return
	 */
	private String mergeImage(String pk_corp, List<String> imageGroupList) {
		int count = imageGroupList.size();
		String groupId = imageGroupList.get(0);
		if (count == 1) {
			return groupId;
		}
		imageGroupList.remove(0);
		String inSQL = SQLHelper.getInSQL(imageGroupList);
		SQLParameter sp = new SQLParameter();
		sp.addParam(groupId);
		sp.addParam(pk_corp);
		for (String pk_group : imageGroupList) {
			sp.addParam(pk_group);
		}
		singleObjectBO.executeUpdate(
				"update ynt_image_library set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		singleObjectBO.deleteByPKs(ImageGroupVO.class, imageGroupList.toArray(new String[0]));
		singleObjectBO.executeUpdate(
				"update ynt_vatsaleinvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		return groupId;
	}

	private void checkisGroup(List<VATSaleInvoiceVO2> list, String pk_corp) {
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();

		sf.append(" select y.fp_dm, y.fp_hm ");
		sf.append(" 	From ynt_vatsaleinvoice y where y.pk_corp = ? and nvl(dr, 0) = 0 ");
		sf.append(" and ( 1 <> 1 ");

		sp.addParam(pk_corp);
		boolean flag = true;
		List<String> libs = new ArrayList<String>();
		for (VATSaleInvoiceVO2 vo : list) {
			if (!StringUtil.isEmpty(vo.getPk_image_library()) && !StringUtil.isEmpty(vo.getPk_image_group())) {
				sf.append(" or pk_image_group = ? ");

				sp.addParam(vo.getPk_image_group());
				libs.add(vo.getPk_image_library());

				flag = false;
			}
		}

		sf.append(" ) ");

		if (flag) {
			return;
		}

		for (String s : libs) {
			sf.append(" and y.pk_image_library != ? ");
			sp.addParam(s);
		}

		List<VATSaleInvoiceVO2> vos = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));

		if (vos != null && vos.size() > 0) {
			sf.setLength(0);
			sf.append("<br>发票号码:");
			for (int i = 0; i < vos.size(); i++) {
				sf.append(vos.get(i).getFp_hm());

				if (i != vos.size() - 1) {
					sf.append(", ");
				}
			}

			sf.append("归属于合并生单的一组图片，需要勾选涉及该组图片一起生单");

			throw new BusinessException(sf.toString());
		}

	}
	
	private String buildZy(VatInvoiceSetVO setvo,
			VATSaleInvoiceVO2 vo,
			Map<String, Boolean> map){
		Boolean flag = false;
		String zy = null;
		if(StringUtil.isEmpty(setvo.getPrimaryKey())){
			zy = "$selectWlZy$selectLxZy$selectXmZy$$$";//默认
		}else{
			zy = setvo.getZy();
		}
		StringBuffer sf = new StringBuffer();
		
		if(!StringUtil.isEmpty(zy) 
				&& zy.contains("$")){
				String[] arr = zy.split("\\$");
				for(String ss : arr){
					if("selectQjZy".equals(ss) && !StringUtil.isEmpty(vo.getInperiod())){
						sf.append("期间:").append(vo.getInperiod());
						flag = true;
					}else if("selectWlZy".equals(ss) && !StringUtil.isEmpty(vo.getKhmc())){
						sf.append("向").append(vo.getKhmc());
						flag = true;
					}else if("selectLxZy".equals(ss)){
						sf.append("销售");
						flag = true;
					}else if("selectXmZy".equals(ss) && !StringUtil.isEmpty(vo.getSpmc())){
						sf.append(vo.getSpmc()).append("等");
						flag = true;
					}else if("selectHmZy".equals(ss)){
						sf.append("发票号码:").append(vo.getFp_hm());
						flag = true;
					}else if(!StringUtil.isEmpty(ss) && ss.contains("selectZdyZy:")){
						int beginindex = zy.lastIndexOf("selectZdyZy:");
						int endindex = zy.lastIndexOf("$");
						String sss = zy.substring(beginindex + 12, endindex);
						sf.append(sss);
						flag = true;
					}
				}
			
		}
		
		map.put("flag", flag);
		String str = sf.toString();
		str = str.length() > 200 ? str.substring(0, 200) : str;
		return str;
	}
	
	private void sortEntryByDirection(List<TzpzBVO> list, String pk_corp){
		if(list == null || list.size() == 0)
			return;
		Map<String, AuxiliaryAccountBVO> assistMap = gl_fzhsserv.queryMap(pk_corp);
		BdCurrencyVO[] currencyvos = gl_qmclserv.queryCurrency();
		Map<String, BdCurrencyVO> currencyMap = new HashMap<String, BdCurrencyVO>();
		if(currencyvos != null && currencyvos.length > 0){
			for (BdCurrencyVO bdCurrencyVO : currencyvos) {
				currencyMap.put(bdCurrencyVO.getPk_currency(), bdCurrencyVO);
			}
		}
		
		for(TzpzBVO bvo : list){
			setEntryFullcode(bvo, assistMap, currencyMap);
		}
		
		sortVoucherEntry(list, pk_corp);
	}
	
	private void sortVoucherEntry(List<TzpzBVO> bvos, String pk_corp) {
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		String taxCode = null;
		if ("00000100AA10000000000BMD".equals(corpVO.getCorptype())
				|| "00000100AA10000000000BMF".equals(corpVO.getCorptype())) {
			// 进项税额，待认证进项税额，销项税额
			taxCode = "^(22210?13|22210+10+(1|2))";
		} else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
			// 企业会计制度 进项税额，销项税额
			taxCode = "^21710+10+(1|5)";
		} else if ("00000100AA10000000000BMQ".equals(corpVO.getCorptype())) {
			// 民间非盈利 进项税额，待抵扣进项税，销项税额
			taxCode = "^2206(0?10|0+10+(1|2))";
		}
		final String taxMatch = taxCode;
	    Collections.sort(bvos, new Comparator<TzpzBVO>() {
	        @Override
	        public int compare(TzpzBVO o1, TzpzBVO o2) {
	            int jf1 = o1.getJfmny() == null
	                    || o1.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int jf2 = o2.getJfmny() == null
	                    || o2.getJfmny().doubleValue() == 0 ? 0 : 1;
	            int cp = jf2 - jf1;
	            if (cp == 0) {
	            	String code1 = o1.getFullcode() == null ? o1.getVcode() : o1.getFullcode();
	            	String code2 = o2.getFullcode() == null ? o2.getVcode() : o2.getFullcode();
	            	boolean isTax1 = o1.getIstaxsubj() != null && o1.getIstaxsubj().booleanValue()
                            || taxMatch != null && code1.matches(taxMatch);
	            	boolean isTax2 = o2.getIstaxsubj() != null && o2.getIstaxsubj().booleanValue()
                            || taxMatch != null && code2.matches(taxMatch);
	            	if (isTax1 || isTax2) {
	            		if (isTax1 && !isTax2) {
		            		code1 = "999";
						} else if (isTax2 && !isTax1) {
							code2 = "999";
						}
					}
	                cp = code1.compareTo(code2);
	            }
	            return cp;
	        }
	    });
	}
	
	private void setEntryFullcode(TzpzBVO vo,
			Map<String, AuxiliaryAccountBVO> assistMap,
			Map<String, BdCurrencyVO> currencyMap) {
		StringBuilder fullcode = new StringBuilder();
		if (vo.getVcode() != null) {
			fullcode.append(vo.getVcode());
		}
		for (int i = 1; i <= 10; i++) {
			String fzhsID = (String) vo.getAttributeValue("fzhsx" + i);
			if (i == 6 && StringUtil.isEmpty(fzhsID)) {
				fzhsID = vo.getPk_inventory();
			}
			if (!StringUtil.isEmpty(fzhsID)) {
				AuxiliaryAccountBVO assist = assistMap.get(fzhsID);
				if (assist != null) {
					fullcode.append("_").append(assist.getCode());
				}
			}
		}
		if (vo.getPk_currency() != null
				&& !IGlobalConstants.RMB_currency_id
						.equals(vo.getPk_currency())) {
			if (currencyMap.containsKey(vo.getPk_currency())) {
				fullcode.append("_").append(
						currencyMap.get(vo.getPk_currency()).getCurrencycode());
			}
		}
		vo.setFullcode(fullcode.toString());
	}

	/**
	 * @param tzpzlist
	 * @param setvo
	 * @param pk_corp
	 * @return
	 */
	private List<TzpzBVO> constructItems(List<TzpzBVO> tzpzlist, 
			VatInvoiceSetVO setvo,
//			VATSaleInvoiceVO vo,
			String pk_corp) {
		String key = null;
		TzpzBVO tempbvo = null;

		// 合并同类项
		DZFBoolean isbk = setvo.getIsbank();// 是否合并银行科目
		isbk = isbk == null ? DZFBoolean.FALSE : DZFBoolean.TRUE;
		Integer type = setvo.getEntry_type();//凭证分录合并规则
		type = type == null ? IBillManageConstants.HEBING_FL_02 : type;//默认 同方向分录合并
		
		Integer vvvalue = setvo.getValue();//凭证合并的规则
		vvvalue = vvvalue == null ? IBillManageConstants.HEBING_GZ_01 : vvvalue;//默认不合并

		if (vvvalue != IBillManageConstants.HEBING_GZ_01
				&& type != IBillManageConstants.HEBING_FL_03) {
			sortEntryByDirection(tzpzlist, pk_corp);
		}
		
		Map<String, TzpzBVO> tzpzmap = new HashMap<String, TzpzBVO>();
		List<TzpzBVO> finalList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> afterList = new ArrayList<TzpzBVO>();
		int rowno = 1;
		for (TzpzBVO bvo : tzpzlist) {
			
			if (type == IBillManageConstants.HEBING_FL_02 && isbk.booleanValue()
					&& !StringUtil.isEmpty(bvo.getVcode()) && bvo.getVcode().startsWith("1002")) {
				bvo.setRowno(rowno++);
				finalList.add(bvo);
				continue;
			}

			if (type == IBillManageConstants.HEBING_FL_02) {
				key = constructTzpzKey(bvo);
				if (tzpzmap.containsKey(key)) {
					tempbvo = tzpzmap.get(key);
					if(bvo.getTax_items()!=null&&bvo.getTax_items().size()>0){
						if(tempbvo.getTax_items()!=null&&tempbvo.getTax_items().size()>0){
							ArrayList<PZTaxItemRadioVO> list = new ArrayList<PZTaxItemRadioVO>();
							list.addAll(tempbvo.getTax_items());
							
							for (PZTaxItemRadioVO pvo2 : bvo.getTax_items()) {
								boolean flag=false;
								for (PZTaxItemRadioVO pvo1 : list) {
									//科目、税目、税率均一致的两条税表表项需合并
									if ( pvo1.getTaxratio().equals(pvo2.getTaxratio())) {
										pvo1.setMny(pvo1.getMny().add(pvo2.getMny()));
										pvo1.setTaxmny(pvo1.getTaxmny().add(pvo2.getTaxmny()));
										flag=true;
										break;
									}
								}
								if(flag==false){
									tempbvo.getTax_items().add(pvo2);
								}
							}
						}else{
							tempbvo.setTax_items(bvo.getTax_items());
							
							
						}
					}
					
					tempbvo.setZy(bvo.getZy());
					tempbvo.setJfmny(SafeCompute.add(tempbvo.getJfmny(), bvo.getJfmny()));
					tempbvo.setYbjfmny(SafeCompute.add(tempbvo.getYbjfmny(), bvo.getYbjfmny()));
					tempbvo.setDfmny(SafeCompute.add(tempbvo.getDfmny(), bvo.getDfmny()));
					tempbvo.setYbdfmny(SafeCompute.add(tempbvo.getYbdfmny(), bvo.getYbdfmny()));
					if (bvo.getNnumber() != null) {
						tempbvo.setNnumber(SafeCompute.add(tempbvo.getNnumber(), bvo.getNnumber()));
						if (tempbvo.getDfmny() != null && tempbvo.getDfmny().doubleValue() > 0) {
							tempbvo.setNprice(SafeCompute.div(tempbvo.getDfmny(), tempbvo.getNnumber()));
						}
						if (tempbvo.getJfmny() != null && tempbvo.getJfmny().doubleValue() > 0) {
							tempbvo.setNprice(SafeCompute.div(tempbvo.getJfmny(), tempbvo.getNnumber()));
						}
					}
				} else {
					tzpzmap.put(key, bvo);
					bvo.setRowno(rowno++);
					finalList.add(bvo);
				}

			} else {
				bvo.setRowno(rowno++);
				finalList.add(bvo);
			}
		}
		for (TzpzBVO bvo : finalList) {
			if(bvo.getJfmny()!=null&&bvo.getJfmny().doubleValue()!=0||(bvo.getDfmny()!=null&&bvo.getDfmny().doubleValue()!=0)){
				afterList.add(bvo);
			}
		}
		if(afterList!=null&&afterList.size()>0){
			return afterList;
		}else{
			return finalList;
		}
		
	}

	protected String constructTzpzKey(TzpzBVO bvo) {
		StringBuffer sf = new StringBuffer();
		sf.append("&").append(bvo.getVdirect()).append("&").append(bvo.getPk_accsubj()).append("&")
				.append(bvo.getPk_inventory()).append("&").append(bvo.getPk_taxitem()).append("&");

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();
	}

	@Override
	
	public Map<String, VATSaleInvoiceVO2> saveCft(String pk_corp, String userid, String ccrecode, String fptqm,
			VATSaleInvoiceVO2 paramvo, StringBuffer msg) throws DZFWarpException {
		CorpVO corpvo = getCorpVO(pk_corp);
		// 为后续更新税号、发票提取码做准备
		corpvo.setVsoccrecode(ccrecode);// 纳税识别号
		corpvo.setFax2(fptqm);// 发票提取码

		String nsrsbh = corpvo.getVsoccrecode();
		String unitname = corpvo.getUnitname();
		DZFDate loginDate = paramvo.getKprj();

		beforeCallCheck(nsrsbh, unitname, loginDate);

		Map<String, VATSaleInvoiceVO2> dataMap = caifangtongserv2.saveCft(corpvo, userid, loginDate, msg);

		Map<String, String> repMap = new HashMap<String, String>();
		VATSaleInvoiceVO2[] vos = buildBusiData(repMap, dataMap);

		setAfterImportPeriod(vos, paramvo);

		checkIsHasRepeation(pk_corp, repMap);

		updateCcRecodeAfterTicket(corpvo);

		return dataMap;
	}

	private void updateCcRecodeAfterTicket(CorpVO corpvo) {
		List<String> fieldList = new ArrayList<String>();
		fieldList.add("fax2");
		fieldList.add("vsoccrecode");

		singleObjectBO.update(corpvo, fieldList.toArray(new String[0]));
	}

	private VATSaleInvoiceVO2[] buildBusiData(Map<String, String> repmap, Map<String, VATSaleInvoiceVO2> datamap) {
		if (datamap == null || datamap.size() == 0)
			return null;

		String value = null;
		VATSaleInvoiceVO2 salevo = null;
		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		for (Map.Entry<String, VATSaleInvoiceVO2> entry : datamap.entrySet()) {

			value = entry.getKey();
			salevo = entry.getValue();

			repmap.put(value, value);
			list.add(salevo);

		}

		return list.size() == 0 ? null : list.toArray(new VATSaleInvoiceVO2[0]);
	}

	private CorpVO getCorpVO(String pk_corp) {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		corpvo.setUnitname(CodeUtils1.deCode(corpvo.getUnitname()));

		return corpvo;
	}

	private void beforeCallCheck(String nsrsbh, String unitname, DZFDate loginDate) {
		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号为空,请检查");
		}
		if (StringUtil.isEmpty(unitname)) {
			throw new BusinessException("公司名称为空,请检查");
		}
		if (loginDate == null) {
			throw new BusinessException("登录期间解析错误，请联系管理员");
		}
	}

	/*@Override
	public void scanMatchBusiName(VATSaleInvoiceVO2 salevo, Map<String, DcModelHVO> dcmap) throws DZFWarpException {
		String pk_corp = salevo.getPk_corp();// 公司
		if (StringUtil.isEmpty(pk_corp))
			return;

		CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);

		DZFBoolean iszh = salevo.getIsZhuan();
		iszh = iszh == null ? DZFBoolean.FALSE : iszh;
		TzpzHVO hvo = new TzpzHVO();
		hvo.setIfptype(0);// 销项
		hvo.setFp_style(getFpStyle(salevo));// 1-----普票 2----专票 3---
											// 未开票

		OcrInvoiceVO invo = buildOcrInvoiceVO(salevo, corpvo, iszh);
		ImageGroupVO grpvo = new ImageGroupVO();
		// 总账核算存货
		DZFBoolean icinv = new DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));// 启用总账库存
		if (icinv != null && icinv.booleanValue()) {
			grpvo.setPjlxstatus(PjTypeEnum.OTHER.getValue());
		}
		DcModelHVO dcvo = aitovoucherserv_extend.getMatchModel(invo, corpvo, hvo, grpvo);

		if (dcvo != null) {
			salevo.setPk_model_h(dcvo.getPk_model_h());
			salevo.setBusitypetempname(dcvo.getBusitypetempname());
		}

	}*/

	private OcrInvoiceVO buildOcrInvoiceVO(VATSaleInvoiceVO2 salevo, CorpVO corpvo, DZFBoolean iszh) {

		String invoicetype = iszh.booleanValue() ? "增值税专用发票" : "增值税普通发票";

		OcrInvoiceVO invo = new OcrInvoiceVO();
		invo.setPk_corp(salevo.getPk_corp());
		invo.setInvoicetype(invoicetype);
		invo.setVmemo(salevo.getDemo());// 备注
		invo.setVfirsrinvname(salevo.getSpmc());

		return invo;
	}

	// public void scanMatchBusiName(VATSaleInvoiceVO2 salevo, Map<String,
	// DcModelHVO> dcmap) throws DZFWarpException {
	// if(dcmap == null || dcmap.size() == 0)
	// return;
	//
	// String spmc = salevo.getSpmc();
	//
	// if(StringUtil.isEmpty(spmc))
	// return;
	//
	// String key = null;
	// String busiName = null;
	// DZFBoolean iszh = salevo.getIsZhuan();
	//
	// String zp = iszh != null && iszh.booleanValue()
	// ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
	//
	// if(spmc.contains("费")
	// || spmc.contains("劳务")){
	// busiName = FieldConstant.YWSTYLE_15;
	// }else{
	// busiName = FieldConstant.YWSTYLE_22;
	// }
	//
	// key = busiName
	// + "_" + zp
	// + "_" + FieldConstant.SZSTYLE_05;
	//
	// DcModelHVO hvo = dcmap.get(key);
	//
	// if(hvo != null && !StringUtil.isEmpty(hvo.getPk_model_h())){
	// salevo.setPk_model_h(hvo.getPk_model_h());
	// salevo.setBusitypetempname(hvo.getBusitypetempname());
	// }
	//
	// }

	@Override
	public List<VATSaleInvoiceVO2> queryByPks(String[] pks, String pk_corp) throws DZFWarpException {
		List<String> pk_categoryList = new ArrayList<String>(); 
		String wherePart = SqlUtil.buildSqlForIn("pk_vatsaleinvoice", pks);

		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append(
				" select pk_vatsaleinvoice,y.coperatorid,y.doperatedate,y.pk_corp,y.batchflag,y.invmodel,y.invstatus,y.iszhuan,y.fp_hm,y.fp_dm,y.khmc,y.spmc,y.spsl, y.inperiod, ");
		sb.append(
				" y.spse,y.hjje,y.jshj,y.yfp_hm,y.yfp_dm,y.noticebillno,y.kprname,y.kprid,y.kprj,y.zfrname,y.zfrid,y.zfrj,y.custidentno,y.pk_tzpz_h,d.pk_category as pk_model_h, y.pk_image_group,y.ioperatetype,y.isettleway, h.vicbillcode vicbillno, ");//y.isic, 
		sb.append(
				" y.pk_subject,y.period,y.billstatus,y.sourcetype,y.modifyoperid,y.modifydatetime,y.dr,y.ts, y.imgpath, y.kplx, h.pzh,ic.dbillid, ic.pk_ictrade_h ");
		sb.append(" from ynt_vatsaleinvoice y ");
		sb.append("   left join ynt_billcategory d ");
		sb.append("     on y.pk_model_h = d.pk_category ");
		sb.append("   left join ynt_ictrade_h ic ");
		sb.append("     on y.pk_ictrade_h = ic.pk_ictrade_h ");
		sb.append("   left join ynt_tzpz_h h ");
		sb.append("     on y.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append(" where y.pk_corp=? and nvl(y.dr,0)=0 and " +wherePart);
		sp.addParam(pk_corp);
		List<VATSaleInvoiceVO2> listvo = (List<VATSaleInvoiceVO2>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO2.class));
		for (VATSaleInvoiceVO2 vo : listvo) {
			pk_categoryList.add(vo.getPk_model_h());
		}
		//查询全名称
		Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList, listvo.get(0).getInperiod(), listvo.get(0).getPk_corp());
		for (VATSaleInvoiceVO2 vo : listvo) {
			vo.setBusitypetempname(map.get(vo.getPk_model_h()));
		}
		return listvo;
	}

	@Override
	public Map<String, VATSaleInvoiceVO2> saveKp(String pk_corp, String userid, VATSaleInvoiceVO2 paramvo,
			TicketNssbhVO nssbvo) throws DZFWarpException {

		CorpVO corpvo = getCorpVO(pk_corp);

		if (nssbvo == null)
			throw new BusinessException("票通认证不成功，请确认");

		Map<String, VATSaleInvoiceVO2> dataMap = piaotongkpserv2.saveKp(corpvo, userid, paramvo);

		Map<String, String> repMap = new HashMap<String, String>();
		VATSaleInvoiceVO2[] vos = buildBusiData(repMap, dataMap);

		setAfterImportPeriod(vos, paramvo);

		checkIsHasRepeation(pk_corp, repMap);

		return dataMap;
	}

	@Override
	public TicketNssbhVO getNssbvo(CorpVO corpvo) throws DZFWarpException {
		TicketNssbhVO nssbvo = null;
		if (corpvo == null || StringUtil.isEmptyWithTrim(corpvo.getPk_corp()))
			return nssbvo;
		String sql = "Select * From ynt_ticket_nssbh y Where nvl(y.dr,0) = 0 and y.pk_corp = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());
		nssbvo = (TicketNssbhVO) singleObjectBO.executeQuery(sql, sp, new BeanProcessor(TicketNssbhVO.class));

		return nssbvo;
	}

	@Override
	public List<TaxitemVO> queryTaxItems(String pk_corp) throws DZFWarpException {
		List<TaxitemVO> vos = taxitemserv.queryAllTaxitems();// 查询税目档案

		vos = filterTaxItem(vos, pk_corp);

		return vos;
	}

	private List<TaxitemVO> filterTaxItem(List<TaxitemVO> vos, String pk_corp) {
		if (vos == null || vos.size() == 0) {
			return null;
		}

		List<TaxitemVO> aftervos = new ArrayList<TaxitemVO>();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String chargename = corpvo.getChargedeptname();
		chargename = StringUtil.isEmpty(chargename) ? SMALL_TAX : chargename;

		for (TaxitemVO vo : vos) {
			if (chargename.equals(vo.getChargedeptname()) && "1".equals(vo.getTaxstyle())) {// 损益类：收入
				aftervos.add(vo);
			}
		}

		return aftervos;
	}
	@Autowired
	private IZncsNewTransService iZncsNewTransService;
	@Autowired
	private IVATSaleInvoiceService gl_vatsalinvserv;
	@Override
	public IntradeHVO createIC(VATSaleInvoiceVO2 vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid,List<List<Object[]>> levelList,Map<String, Object[]> categoryMap,Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap,Set<String> zyFzhsList
			,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap,InventorySetVO inventorySetVO,CorpVO corp,Map<String, InventoryAliasVO> fzhsBMMap
			,List<Object> paramList,Map<String, BdCurrencyVO> currMap,Map<String, Object[]> rateMap,Map<String, String> bankAccountMap,Map<String,YntCpaccountVO> accountMap
			,Map<String, AuxiliaryAccountBVO> assistMap,Map<String, List<AccsetVO>> accsetMap,Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map,Map<String, String> jituanSubMap,YntCpaccountVO[] accVOs
			,String tradeCode,String newrule,List<AuxiliaryAccountBVO> chFzhsBodyVOs)
			throws DZFWarpException {
		String pk_corp = corpvo.getPk_corp();
		String pk_ictrade_h = vo.getPk_ictrade_h();
		if(!StringUtil.isEmpty(pk_ictrade_h)){
			throw new BusinessException("已生成出库单，无需再次生成");
		}

		String pk_model_h = vo.getPk_model_h();
		if (StringUtil.isEmpty(pk_model_h)) {
			throw new BusinessException("业务类型不能为空");
		}
		Map<String, BillCategoryVO> falseMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corpvo.getPk_corp(), vo.getInperiod(),"N");
		Map<String, BillCategoryVO> trueMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corpvo.getPk_corp(), vo.getInperiod(),"Y");
		trueMap= iZncsNewTransService.newInsertCategoryVOs(falseMap, trueMap,corpvo.getPk_corp()+vo.getInperiod());
		turnBillCategoryMap(falseMap, trueMap);
		//过滤掉不需要生成出入库单的行
		VATSaleInvoiceVO2 oldVO=(VATSaleInvoiceVO2)OcrUtil.clonAll(vo);
		vo=filterBodyVOs(vo, falseMap);
		if(vo.getChildren()==null||vo.getChildren().length==0){
			throw new BusinessException("请重新选择业务类型");
		}
		Map<String, CategorysetVO> categorysetMap=queryCategorysetVO(vo,corpvo.getPk_corp());
		
		IntradeHVO ichvo = buildIctrade(vo, userid, categorysetMap, trueMap, falseMap,fzhsBodyMap);

		ic_saleoutserv.saveSale(ichvo, false, false);// 保存
		// 更新状态
		updateICStatus(vo.getPk_vatsaleinvoice(), corpvo.getPk_corp(), ichvo.getPk_ictrade_h());
		//生成凭证
		List<VATSaleInvoiceVO2> ll = new ArrayList<VATSaleInvoiceVO2>();
		ll.add(oldVO);
		Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
		List<OcrInvoiceVO> invoiceList = changeToOcr(ll, pk_corp);
		List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, vo.getInperiod(), pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
		tzpzhvoList.get(0).setUserObject(null);
		tzpzhvoList.get(0).setSourcebillid(ichvo.getPk_ictrade_h());
		tzpzhvoList.get(0).setSourcebilltype(IBillTypeCode.HP75);
		TzpzHVO headvo = voucher.saveVoucher(corpvo, tzpzhvoList.get(0));
		writeBackSale(ichvo, headvo);
		return ichvo;
	}
	private void writeBackSale(IntradeHVO ivo, TzpzHVO headvo) {
		ivo.setPzid(headvo.getPrimaryKey());
		ivo.setPzh(headvo.getPzh());
		ivo.setDjzdate(new DZFDate());
		ivo.setIsjz(DZFBoolean.TRUE);
		List<IntradeoutVO> list = new ArrayList<IntradeoutVO>();

		TzpzBVO[] bvos = (TzpzBVO[]) headvo.getChildren();
		SuperVO[] ibodyvos = ivo.getChildren();

		for (SuperVO ibvo : ibodyvos) {
			for (TzpzBVO bvo : bvos) {
				IntradeoutVO ibody = (IntradeoutVO) ibvo;
				if (ibody.getPk_inventory().equals(bvo.getPk_inventory())) {
					ibody.setPzh(headvo.getPzh());
					ibody.setPk_voucher(headvo.getPk_tzpz_h());
					ibody.setPk_voucher_b(bvo.getPk_tzpz_b());
					ibody.setZy(bvo.getZy());
					// ibody.setPk_subject(bvo.getPk_accsubj());
					list.add(ibody);
					break;
				}
			}
		}

		singleObjectBO.updateAry(list.toArray(new IntradeoutVO[list.size()]),
				new String[] { "pk_voucher", "pzh", "pk_voucher_b", "zy" });

		singleObjectBO.update(ivo);

		// 如果来源于销项
		gl_vatsalinvserv.updatePZH(headvo);
	}
	
	private int getSettlement(VATSaleInvoiceVO2 vo,CategorysetVO setVO){
		if(vo.getSettlement()!=null){
			if(vo.getSettlement()==0){
				return 1;
			}else if(vo.getSettlement()==1){
				return 0;
			}else{
				return 2;
			}
		}
		return (setVO==null||setVO.getSettlement()==0)?1:setVO.getSettlement()==1?0:2;
	}
	private IntradeHVO buildIctrade(VATSaleInvoiceVO2 vo,String userid,Map<String, CategorysetVO> categorysetMap,Map<String, BillCategoryVO> trueMap,Map<String, BillCategoryVO> falseMap
			,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap)throws DZFWarpException{
		IntradeHVO icHeadVO=new IntradeHVO();
		String khmc=null;//客户或者供应商
		icHeadVO.setCbusitype(IcConst.XSTYPE);//销售出库
		icHeadVO.setSourcebilltype(IBillTypeCode.HP90);//来源销项票
		khmc=vo.getKhmc();//购买方
		if (!StringUtil.isEmpty(khmc)) {
			AuxiliaryAccountBVO custvo = matchCustomer(vo, khmc, vo.getPk_corp(),  userid, AuxiliaryConstant.ITEM_CUSTOMER);
			icHeadVO.setPk_cust(custvo.getPk_auacount_b());//客户
			fzhsBodyMap.get("000001000000000000000001").add(custvo);
		}
		icHeadVO.setDinvdate(vo.getKprj());//发票日期-开票日期
		
		icHeadVO.setDbillid(null);// 单据编号-设置为空，调用入库接口会重新生成
		
		icHeadVO.setPk_corp(vo.getPk_corp());
		icHeadVO.setIarristatus(1);//到货状态

		icHeadVO.setDbilldate(vo.getKprj());//单据日期-凭证日期
		icHeadVO.setDinvid(vo.getFp_hm());//发票号码
		icHeadVO.setCreator(userid);//制单人

		icHeadVO.setSourcebillid(vo.getPrimaryKey());//单据来源ID
		
		icHeadVO.setPk_image_group(vo.getPk_image_group());//图片组
		icHeadVO.setPk_image_library(vo.getPk_image_library());//图片
		icHeadVO.setFp_style(getFpStyle(vo));// 1普票 2专票3未开票
		
//		icHeadVO.setIsjz(DZFBoolean.TRUE);//转总账
//		icHeadVO.setDjzdate(new DZFDate());
		icHeadVO.setIsinterface(DZFBoolean.FALSE);
		VATSaleInvoiceBVO2[] details=(VATSaleInvoiceBVO2[])vo.getChildren();
		CategorysetVO setVO=categorysetMap.get(falseMap.get(details[0].getPk_billcategory()));//编辑目录
		icHeadVO.setIpayway(getSettlement(vo, setVO));// 付款方式
		if(icHeadVO.getIpayway()==2){
			String bankCode="";
			bankCode=vo.getXhfyhzh();
			if(!StringUtil.isEmpty(bankCode)){
				String code=null;
				String[] str=bankCode.trim().split(" ");
				if(str!=null&&str.length<2){
					StringBuffer buffer = new StringBuffer();
					StringBuffer bufferCode = new StringBuffer();
					char[] charCode = bankCode.toCharArray();
					for (int i = charCode.length-1 ; i >= 0; i--) {
						if(Character.isDigit(charCode[i])){
							buffer.append(charCode[i]);
						}else{
							break;
						}
					}
					char[] charNum = buffer.toString().toCharArray();
					for (int i = charNum.length-1 ; i >= 0; i--) {
						bufferCode.append(charNum[i]);
					}
					code = bufferCode.toString().trim();
				}else{
					code = str[1].trim();
				}
				if(!StringUtils.isEmpty(code)){
					icHeadVO.setPk_bankaccount(queryBankPrimaryKey(vo.getPk_corp(), code));
				}
			}
		}
		List<SuperVO> icList = new ArrayList<SuperVO>();
		SuperVO icbvo = null;
		for (int j = 0; j < details.length; j++) {
			
			String spmc = OcrUtil.execInvname(details[j].getBspmc());//商品名称

			if (StringUtil.isEmpty(spmc)) {
				continue;
			}

			InventoryVO inventoryvo = getInventoryVOByName(spmc, details[j].getInvspec(),details[j].getMeasurename(),vo.getPk_corp());
			if(StringUtil.isEmpty(spmc)){
				throw new BusinessException("货物或劳务名称为空，不能生成出入库单");
			}
			if(inventoryvo==null){
				throw new BusinessException("存货未匹配！");
			}
			icbvo = new IntradeoutVO();
			
			icbvo.setAttributeValue("pk_inventory", inventoryvo.getPk_inventory());
			icbvo.setAttributeValue("pk_subject", inventoryvo.getPk_subject());

			int calcmode=inventoryvo.getCalcmode();
			DZFDouble hsl=inventoryvo.getHsl();
			
			if(details[j].getBnum()==null){
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
						icbvo.setAttributeValue("nnum", new DZFDouble(details[j].getBnum()).div(new DZFDouble(hsl)));//数量
					}else{
						icbvo.setAttributeValue("nnum", new DZFDouble(details[j].getBnum()).multiply(new DZFDouble(hsl)));//数量
					}
				}else{
					icbvo.setAttributeValue("nnum", details[j].getBnum());
				}
			}
			icbvo.setAttributeValue("nymny", details[j].getBhjje());
			
			icbvo.setAttributeValue("nprice", ((DZFDouble)icbvo.getAttributeValue("nymny")).div((DZFDouble)icbvo.getAttributeValue("nnum")));
			
			icbvo.setAttributeValue("ntax",details[j].getBspsl());
			icbvo.setAttributeValue("ntaxmny", details[j].getBspse());
			icbvo.setAttributeValue("ntotaltaxmny", details[j].getBhjje().add(details[j].getBspse()));
			icbvo.setAttributeValue("ncost", null);

			icList.add(icbvo);
		}
		icHeadVO.setChildren(icList.toArray(new IntradeoutVO[0]));
		return icHeadVO;
	}
	
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
	private Map<String, CategorysetVO> queryCategorysetVO(VATSaleInvoiceVO2 vo,String pk_corp) throws DZFWarpException {
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		Set<String> pkList = new HashSet<String>();
		pkList.add(vo.getPk_model_h());
		VATSaleInvoiceBVO2[] vos=(VATSaleInvoiceBVO2[])vo.getChildren();
		for (int i = 0; i < vos.length; i++) {
			VATSaleInvoiceBVO2 bodyVO = vos[i];
			if(!StringUtil.isEmpty(bodyVO.getPk_billcategory())){
				pkList.add(bodyVO.getPk_billcategory());
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
	private VATSaleInvoiceVO2 filterBodyVOs(VATSaleInvoiceVO2 vo,Map<String, BillCategoryVO> falseMap)throws DZFWarpException{
		List<VATSaleInvoiceBVO2> bodyList=new ArrayList<VATSaleInvoiceBVO2>();
		VATSaleInvoiceBVO2[] bodyVOs=(VATSaleInvoiceBVO2[])vo.getChildren();
		if(bodyVOs==null || bodyVOs.length==0) return vo;
		for (int i = 0; i < bodyVOs.length; i++) {
			if(!StringUtil.isEmpty(bodyVOs[i].getPk_billcategory())){
				String categoryCode=falseMap.get(bodyVOs[i].getPk_billcategory()).getCategorycode();//分类编码
				if(categoryCode.startsWith("101015")||categoryCode.startsWith("101110")){
					bodyList.add(bodyVOs[i]);
				}
			}
		}
		vo.setChildren(bodyList.toArray(new VATSaleInvoiceBVO2[0]));
		return vo;
	}
	
	private void turnBillCategoryMap(Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap)throws DZFWarpException{
		Iterator<String> itor=falseMap.keySet().iterator();
		List<BillCategoryVO> addfalseVOList=new ArrayList<BillCategoryVO>();
		while(itor.hasNext()){
			String categoryCode=itor.next();//未制证编码
			BillCategoryVO falseVO=falseMap.get(categoryCode);//未制证VO
			addfalseVOList.add(falseVO);
			BillCategoryVO trueVO=trueMap.get(categoryCode);//已制证VO
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
	private IntradeHVO buildIntradeHVO(VATSaleInvoiceVO2 vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid)
			throws DZFWarpException {

		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(corpvo.getPk_corp());
		if (setvo == null) {
			throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
		}

		VATSaleInvoiceBVO2[] saleBodyvos = (VATSaleInvoiceBVO2[]) vo.getChildren();

		if (saleBodyvos == null || saleBodyvos.length == 0) {
			throw new BusinessException("销项表体行为空，不能生成出库单");
		}

		List<IntradeoutVO> icList = new ArrayList<IntradeoutVO>();
		String pk_corp = corpvo.getPk_corp();
		IntradeoutVO icbvo = null;

		AuxiliaryAccountBVO custvo = null;// 客户辅助
		InventoryVO inventoryvo = null;// 存货档案

		String gfmc = null;
		String spmc = null;

		for (VATSaleInvoiceBVO2 salebvo : saleBodyvos) {
			spmc = salebvo.getBspmc();

			if (StringUtil.isEmpty(spmc)) {
				continue;
			}

			inventoryvo = matchInvtoryIC(vo, salebvo, corpvo, accounts);

			icbvo = new IntradeoutVO();
			icbvo.setPk_inventory(inventoryvo.getPk_inventory());
			icbvo.setPk_subject(inventoryvo.getPk_subject());

			icbvo.setNnum(salebvo.getBnum());
			icbvo.setNprice(salebvo.getBprice());
			icbvo.setNymny(salebvo.getBhjje());
			icbvo.setNtax(salebvo.getBspsl());
			icbvo.setNtaxmny(salebvo.getBspse());
			icbvo.setNtotaltaxmny(SafeCompute.add(icbvo.getNymny(), icbvo.getNtaxmny()));// 价税合计
			icbvo.setNcost(null);

			icList.add(icbvo);
		}

		if (icList.size() == 0) {
			throw new BusinessException("销项表体行货物或应税劳务名称为空，不能生成出库单");
		}

		IntradeHVO ichvo = new IntradeHVO();
		gfmc = vo.getKhmc();

		if (!StringUtil.isEmpty(gfmc)) {
			custvo = matchCustomer(vo, gfmc, pk_corp, userid, AuxiliaryConstant.ITEM_CUSTOMER);
			ichvo.setPk_cust(custvo.getPk_auacount_b());
		}

		DZFDate date = null;
		if (StringUtil.isEmpty(vo.getInperiod())) {
			date = vo.getKprj();
		} else {
			date = DateUtils.getPeriodEndDate(vo.getInperiod());
		}

		ichvo.setDbilldate(date);
		ichvo.setDbillid(null);// 设置为空，调用入库接口会重新生成
		ichvo.setCbusitype(IcConst.XSTYPE);// 销售出库
		ichvo.setIpayway(setvo.getXsfkfs());// 取自库存入账设置节点
		ichvo.setPk_corp(pk_corp);
		ichvo.setIarristatus(1);

		// ichvo.setPk_bankaccount();
		ichvo.setDinvdate(vo.getKprj());
		ichvo.setDinvid(vo.getFp_hm());
		ichvo.setCreator(userid);

		// 单据来源
		ichvo.setSourcebilltype(IBillTypeCode.HP90);
		ichvo.setSourcebillid(vo.getPrimaryKey());
		ichvo.setPk_image_group(vo.getPk_image_group());
		ichvo.setPk_image_library(vo.getPk_image_library());
		ichvo.setFp_style(getFpStyle(vo));// 1普票 2专票3未开票
		// DZFBoolean iszh = getIsZhuan(corpvo.getChargedeptname(),
		// vo.getIsZhuan());
		// if(iszh == null){
		// ichvo.setFp_style(3);
		// }else{
		//
		// }
		ichvo.setChildren(icList.toArray(new IntradeoutVO[0]));

		return ichvo;
	}

	/*
	 * 专普票标识 当前公司是一般纳税人，如果销项发票是专票则生成凭证时，不带任何标识；如果销项发票是普票则生成凭证时默认勾选“普票”标识；
	 * 当前公司是小规模纳税人，如果销项发票是专票时则生成凭证时，默认勾选“专票”标识；如果销项发票是普票则生成凭证时不带任何标识
	 */
	private DZFBoolean getIsZhuan(String chargename, DZFBoolean iszh) {
		DZFBoolean flag = null;
		iszh = iszh == null ? DZFBoolean.FALSE : iszh;
		if ("一般纳税人".equals(chargename)) {
			if (!iszh.booleanValue()) {
				flag = DZFBoolean.FALSE;
			}
		} else {
			if (iszh.booleanValue()) {
				flag = DZFBoolean.TRUE;
			}

		}

		return flag;
	}

	private AuxiliaryAccountBVO matchCustomer(VATSaleInvoiceVO2 vo, String gfmc, String pk_corp, String userid,
			String pk_auacount_h) {
		AuxiliaryAccountBVO suppliervo = null;

		String payer = vo.getCustidentno();// 购方识别号
		String name = vo.getKhmc();// 购方名称
		String address = vo.getGhfdzdh();// 购方方地址电话
		String bank = vo.getGhfyhzh();// 购方方开户账号

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

	private void setFzInfo(AuxiliaryAccountBVO bvo, String address, String bank) {
		try {
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

		} catch (Exception e) {
			log.error(e.getMessage(), e);
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

	private InventoryVO matchInvtoryIC(VATSaleInvoiceVO2 salevo, VATSaleInvoiceBVO2 salechild, CorpVO corpvo,
			YntCpaccountVO[] accounts) {

		if (corpvo == null || salechild == null)
			return null;

		MeasureVO meavo = null;
		InventoryVO invvo = null;
		String pk_measure = null;

		String pk_inventory = salechild != null ? salechild.getPk_inventory() : "";

		if (!StringUtil.isEmpty(pk_inventory)) {
			invvo = invservice.queryByPrimaryKey(pk_inventory);
		} else {
			meavo = getMeasureVO(salevo, salechild, corpvo);

			if (meavo != null) {
				pk_measure = meavo.getPk_measure();
			}

			invvo = ocr_atuomatch.getInventoryVOByName(salechild.getBspmc(), salechild.getInvspec(), pk_measure,
					corpvo.getPk_corp());
		}

		if (invvo == null && ((salechild != null && !StringUtil.isEmpty(salechild.getBspmc())))) {
			invvo = new InventoryVO();
			invvo.setPk_corp(corpvo.getPk_corp());
			invvo.setCreatetime(new DZFDateTime());
			if (meavo != null)
				invvo.setPk_measure(pk_measure);
			invvo.setCreator(salevo.getCoperatorid());

			invvo.setInvspec(salechild.getInvspec());
			invvo.setName(salechild.getBspmc());

			invvo.setCode(yntBoPubUtil.getInventoryCode(corpvo.getPk_corp()));
			// invvo.setPk_measure(pk_measure);
			for (YntCpaccountVO acc : accounts) {
				if ("1405".equalsIgnoreCase(acc.getAccountcode()))
					invvo.setPk_subject(acc.getPk_corp_account());
			}
			invservice.save(corpvo.getPk_corp(), new InventoryVO[] { invvo });
		}
		return invvo;
	}

	private MeasureVO getMeasureVO(VATSaleInvoiceVO2 salevo, VATSaleInvoiceBVO2 salechild, CorpVO corpvo) {
		// 查找计量单位

		if (salechild == null || StringUtil.isEmpty(salechild.getMeasurename())) {
			return new MeasureVO();
		}

		StringBuffer sb = new StringBuffer();
		sb.append(" pk_corp=? and nvl(dr,0)=0 and name = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());
		sp.addParam(salechild.getMeasurename());
		List<MeasureVO> listVo = (List<MeasureVO>) singleObjectBO.retrieveByClause(MeasureVO.class, sb.toString(), sp);
		MeasureVO meavo = null;
		if (listVo == null || listVo.size() == 0) {
			meavo = new MeasureVO();
			meavo.setPk_corp(corpvo.getPk_corp());
			meavo.setCreatetime(new DZFDateTime());
			meavo.setCreator(salevo.getCoperatorid());
			meavo.setName(salechild.getMeasurename());
			meavo.setCode(yntBoPubUtil.getMeasureCode(corpvo.getPk_corp()));
			listVo = new ArrayList<>();
			listVo.add(meavo);
			measervice.updateVOArr(corpvo.getPk_corp(), salevo.getCoperatorid(), listVo);
		} else {
			meavo = listVo.get(0);
		}
		return meavo;
	}

	// private InventoryVO matchInvtoryIC(Map<String, InventoryVO> invMap,
	// VATSaleInvoiceBVO2 salebvo,
	// YntCpaccountVO[] accounts,
	// String spmc,
	// String pk_corp,
	// String userid){
	// InventoryVO invvo = null;
	// if(invMap.containsKey(spmc)){
	// invvo = invMap.get(spmc);
	// }
	//
	// if(invvo == null){
	// //存货
	// invvo = new InventoryVO();
	// invvo.setPk_corp(pk_corp);
	// invvo.setCreatetime(new DZFDateTime());
	//
	// invvo.setName(spmc);
	//
	// invvo.setCode(yntBoPubUtil.getInventoryCode(pk_corp));
	//
	// for(YntCpaccountVO acc : accounts){
	// if("1405".equalsIgnoreCase(acc.getAccountcode())){
	// invvo.setPk_subject(acc.getPk_corp_account());
	// }
	// }
	//
	// invservice.save(pk_corp, new InventoryVO[]{ invvo });
	// }
	//
	// return invvo;
	// }

	@Override
	public void deletePZH(String pk_corp, String pk_tzpz_h) throws DZFWarpException {
		String sql = "update ynt_vatsaleinvoice y set y.pk_tzpz_h = null,y.pzh = null Where y.pk_tzpz_h = ? and pk_corp = ? ";

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);

		singleObjectBO.executeUpdate(sql, sp);

	}

	@Override
	public void updatePZH(TzpzHVO headvo) throws DZFWarpException {
		String condition = " nvl(dr,0)=0 and  cbilltype = ? and pzid = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillTypeCode.HP75);
		sp.addParam(headvo.getPrimaryKey());
		sp.addParam(headvo.getPk_corp());
		IntradeHVO[] hvos = (IntradeHVO[]) singleObjectBO.queryByCondition(IntradeHVO.class, condition, sp);

		if (hvos == null || hvos.length == 0) {
			return;
		}

		List<String> pks = new ArrayList<String>();

		String type = null;
		for (IntradeHVO hvo : hvos) {
			type = hvo.getSourcebilltype();
			if (IBillTypeCode.HP90.equals(type)) {
				pks.add(hvo.getSourcebillid());
			}
		}

		if (pks.size() == 0)
			return;

		StringBuffer sf = new StringBuffer();
		sf.append("  update ynt_vatsaleinvoice y set y.pk_tzpz_h = ?,y.pzh = ? Where ");// y.pk_vatincominvoice
																						// =
																						// ?
		sf.append(SqlUtil.buildSqlForIn("pk_vatsaleinvoice", pks.toArray(new String[0])));
		sp = new SQLParameter();
		sp.addParam(headvo.getPrimaryKey());
		sp.addParam(headvo.getPzh());

		singleObjectBO.executeUpdate(sf.toString(), sp);
	}

	@Override
	public void updateICStatus(String pk_vatsaleinvoice, String pk_corp, String pk_ictrade_h) throws DZFWarpException {
		String sql = " update ynt_vatsaleinvoice set pk_ictrade_h = ? where pk_vatsaleinvoice = ? and pk_corp = ? and nvl(dr,0) = 0 ";

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_ictrade_h);
		sp.addParam(pk_vatsaleinvoice);
		sp.addParam(pk_corp);

		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public void saveGL(IntradeHVO hvo, String pk_corp, String userid) throws DZFWarpException {
		 CorpVO cvo = corpService.queryByPk(pk_corp);
		ic_saleoutserv.saveToGL(hvo, cvo, userid, "销售商品");// 转总账
	}

	@Override
	public void saveTotalGL(IntradeHVO[] vos, String pk_corp, String userid) throws DZFWarpException {
		String zy = "销售商品";
		ic_saleoutserv.saveToGL(vos, pk_corp, userid, zy);
	}

	@Override
	public List<VatGoosInventoryRelationVO> getGoodsInvenRela(List<VATSaleInvoiceVO2> saleList, String pk_corp) throws DZFWarpException {
		CorpVO corp = corpService.queryByPk(pk_corp);
		
		AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);
		List<InventoryVO> intorylist = inventoryservice.querySpecialKM(pk_corp);
		Map<String, AuxiliaryAccountBVO> invenMap = new HashMap<>();
		Map<String, AuxiliaryAccountBVO> invenMap1 = new HashMap<>();

		if (invenvos != null && invenvos.length > 0) {
			List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
			if(IcCostStyle.IC_ON.equals(corp.getBbuildic())){
				invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec","unit" });//加上计量单位
			}else{

				invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec" });
			}
			Map<String, AuxiliaryAccountBVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invenList,
					new String[] { "pk_auacount_b" });
			invenMap1 = buildInvenMap(tempMap, invenMap, pk_corp);
		}

		Map<String, VATSaleInvoiceBVO2> bvoMap = buildGoodsInvenRelaMap(saleList);

		List<VatGoosInventoryRelationVO> list = null;
		if (bvoMap != null && bvoMap.size() > 0) {
			list = new ArrayList<VatGoosInventoryRelationVO>();

			String key;
			AuxiliaryAccountBVO invenvo;
			VATSaleInvoiceBVO2 bvo;
			VatGoosInventoryRelationVO relvo;
			for (Map.Entry<String, VATSaleInvoiceBVO2> entry : bvoMap.entrySet()) {
				key = entry.getKey();
				bvo = entry.getValue();
				invenvo = invenMap1.get(key);//先匹配别名
				relvo = new VatGoosInventoryRelationVO();
				if (invenvo == null && invenMap.containsKey(key)) {
					invenvo = invenMap.get(key);
				}
				if(invenMap.containsKey(key)){
					relvo.setPk_inventory_old(invenMap.get(key).getPrimaryKey());
				}
				
				if (invenvo != null) {
					relvo.setPk_inventory(invenvo.getPrimaryKey());
//					relvo.setPk_inventory_old(invenvo.getPrimaryKey());
					relvo.setCalcmode(invenvo.getCalcmode());
					relvo.setHsl(invenvo.getHsl());
					relvo.setCode(invenvo.getCode());
					relvo.setName(invenvo.getName());
					relvo.setPk_subj(invenvo.getPk_accsubj());
					relvo.setSubjname(invenvo.getSubjname());
				}else{
					relvo.setPk_subj(intorylist.get(1).getPk_subject());//pk_subject
					relvo.setSubjname(intorylist.get(1).getKmname());//kmname
				}
				
				if(relvo.getHsl()==null){
					relvo.setHsl(DZFDouble.ONE_DBL);
					relvo.setCalcmode(0);
				}
				relvo.setFphm(bvo.getFphm());
				relvo.setUnit(bvo.getMeasurename());
				relvo.setSpmc(bvo.getBspmc());
				relvo.setInvspec(bvo.getInvspec());

				list.add(relvo);
			}
		}
		
		if(list!=null && list.size()>0){
			List<String> slist = new ArrayList<>();
			for (VATSaleInvoiceVO2 svo : saleList) {
				slist.add(svo.getInperiod());
			}
			Map<String,List<VATSaleInvoiceBVO2>> salemap = ocrinterface.querySaleInvoiceInfo(pk_corp, slist, InventoryConstant.IC_RULE_0);
			int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
			int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
			for (VatGoosInventoryRelationVO rvo : list) {
				InventorySaleInfoVO saleinfo = new InventorySaleInfoVO();
				saleinfo.setName(rvo.getSpmc());
				saleinfo.setSpec(rvo.getInvspec());
				saleinfo.setUnit(rvo.getUnit());
				saleinfo.setPk_corp(pk_corp);
				//InventorySaleInfoVO infovo = ocrinterface.querySaleBillInfo(slist, InventoryConstant.IC_RULE_0, saleinfo,null,0,0);
				InventorySaleInfoVO infovo = ocrinterface.querySaleBillInfo(slist, InventoryConstant.IC_RULE_0, saleinfo,salemap,numPrecision,pricePrecision);
				if(infovo!=null){
					rvo.setSaleNumber(infovo.getSaleNumber());
					rvo.setSalePrice(infovo.getSalePrice());
				}
				
			}
		}
		return list;
	}

	// 将来有可能会放开
	private List<InventoryAliasVO> queryVatGoosInvenRela(String pk_corp) throws DZFWarpException {

		String sql = "Select * From ynt_icalias y Where y.pk_corp = ? and nvl(dr,0) = 0 order by ts desc";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InventoryAliasVO> list = (List<InventoryAliasVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(InventoryAliasVO.class));

		return list;

	}

	private Map<String, AuxiliaryAccountBVO> buildInvenMap(Map<String, AuxiliaryAccountBVO> tempinMap,
			Map<String, AuxiliaryAccountBVO> invenMap, String pk_corp) {

		Map<String, AuxiliaryAccountBVO> invenMap1 = new HashMap<String, AuxiliaryAccountBVO>();

		List<InventoryAliasVO> list = queryVatGoosInvenRela(pk_corp);

		if (list != null && list.size() > 0) {
			String key;
			String pk_inventory;
			for (InventoryAliasVO vo : list) {
				//key = vo.getSpmc() + "," + vo.getInvspec();
				key = vo.getAliasname() + "," + vo.getSpec()+ ","+vo.getUnit();//名称规格计量单位
				
				if (invenMap1.containsKey(key)) {
					continue;
				}

				pk_inventory = vo.getPk_inventory();
				if (!StringUtil.isEmpty(pk_inventory) && tempinMap.containsKey(pk_inventory)) {
					AuxiliaryAccountBVO svo = (AuxiliaryAccountBVO)tempinMap.get(pk_inventory).clone();
					svo.setHsl(vo.getHsl());
					svo.setCalcmode(vo.getCalcmode());
					svo.setUnit(vo.getUnit());
					invenMap1.put(key, svo);
				}

			}
		}
		return invenMap1;
	}

	private Map<String, VATSaleInvoiceBVO2> buildGoodsInvenRelaMap(List<VATSaleInvoiceVO2> saleList) {

		Map<String, VATSaleInvoiceBVO2> map = new HashMap<>();

		String key;
		VATSaleInvoiceBVO2[] bvos = null;
		Map<String, BillCategoryVO> catemap = new HashMap<String, BillCategoryVO>();
		for (VATSaleInvoiceVO2 vo : saleList) {
			bvos = (VATSaleInvoiceBVO2[]) vo.getChildren();

			if (bvos != null && bvos.length > 0) {
				for (VATSaleInvoiceBVO2 bvo : bvos) {
					if(bvos == null || bvos.length==0){
						VATSaleInvoiceBVO2 mvo = new VATSaleInvoiceBVO2();
						mvo.setPk_billcategory(vo.getPk_model_h());
						mvo.setBspmc(OcrUtil.execInvname(vo.getSpmc()));
						bvos = new VATSaleInvoiceBVO2[]{mvo};
					}
					if(!ocrinterface.checkIsMatchCategroy(bvo.getPk_billcategory(), catemap)){
						continue;
					}
					bvo.setBspmc(OcrUtil.execInvname(bvo.getBspmc()));
					key = bvo.getBspmc() + "," + bvo.getInvspec()+"," +bvo.getMeasurename() ;
					bvo.setFphm(vo.getFp_hm());
					if (!map.containsKey(key)) {
						map.put(key, bvo);
					}

				}
			}

		}

		return map;
	}

	@Override
	public void saveGoodsRela(Map<String, List<VatGoosInventoryRelationVO>> newRelMap, String pk_corp, String userid)
			throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		// DZFBoolean bbuildic = corpvo.getBbuildic();

		if (newRelMap == null || newRelMap.size() == 0) {
			return;
		}

		String[] pks = newRelMap.keySet().toArray(new String[0]);

		if (!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())) {
			dealFzGoodsRela(pk_corp, userid, pks, newRelMap);
		} else {
			dealInvenGoodsRela(pk_corp, userid, pks, newRelMap);

		}

	}

	private void dealFzGoodsRela(String pk_corp, String userid, String[] pks,
			Map<String, List<VatGoosInventoryRelationVO>> newRelMap) {
		AuxiliaryAccountBVO[] vos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);

		if (vos != null && vos.length > 0) {
			Map<String, AuxiliaryAccountBVO> fzmap = DZfcommonTools.hashlizeObjectByPk(Arrays.asList(vos),
					new String[] { "pk_auacount_b" });

			String key;
			String demo;
			boolean flag;
			String spec;
			AuxiliaryAccountBVO bvo;
			List<AuxiliaryAccountBVO> fzList = new ArrayList<AuxiliaryAccountBVO>();
			List<VatGoosInventoryRelationVO> relList;
			List<VatGoosInventoryRelationVO> newRelList = new ArrayList<VatGoosInventoryRelationVO>();
			for (Map.Entry<String, List<VatGoosInventoryRelationVO>> entry : newRelMap.entrySet()) {
				flag = false;
				key = entry.getKey();

				bvo = fzmap.get(key);

				if (bvo == null) {
					continue;
				}

				demo = bvo.getVmemo();

				if (StringUtil.isEmpty(demo)) {
					demo = "";
				}

				relList = entry.getValue();

				for (VatGoosInventoryRelationVO relvo : relList) {
					spec = relvo.getSpmc();
					if (!StringUtil.isEmpty(spec)) {
						if (!demo.contains(spec)) {
							flag = true;

							if (StringUtil.isEmpty(demo)) {
								demo = spec;
							} else {
								demo += "," + spec;
							}
						}

						newRelList.add(relvo);
					}
				}

				if (flag) {
					bvo.setVmemo(demo);
					fzList.add(bvo);
				}

			}

			if (fzList.size() > 0) {
				singleObjectBO.updateAry(fzList.toArray(new AuxiliaryAccountBVO[0]), new String[] { "vmemo" });
			}

			if (newRelList.size() > 0) {
				for (VatGoosInventoryRelationVO vo : newRelList) {
					vo.setCoperatorid(userid);
					vo.setDoperatedate(new DZFDate());
					vo.setPk_corp(pk_corp);
				}

				singleObjectBO.insertVOArr(pk_corp, newRelList.toArray(new VatGoosInventoryRelationVO[0]));
			}

		}

	}

	private void dealInvenGoodsRela(String pk_corp, String userid, String[] pks,
			Map<String, List<VatGoosInventoryRelationVO>> newRelMap) {

		//Map<String, InventoryVO> invenmap = invservice.queryInventoryVOs(pk_corp, pks);
		List<InventoryAliasVO> list = queryInventoryAliasVO(pk_corp);
		Map<String,InventoryAliasVO>invenMap = DZfcommonTools.hashlizeObjectByPk(list, new String[] { "aliasname", "spec","unit" });
		//if (invenmap != null && invenmap.size() > 0) {
			String key;
			List<VatGoosInventoryRelationVO> relList;
			List<VatGoosInventoryRelationVO> newRelList = new ArrayList<VatGoosInventoryRelationVO>();
			List<InventoryVO> invenList = new ArrayList<InventoryVO>();

			for (Map.Entry<String, List<VatGoosInventoryRelationVO>> entry : newRelMap.entrySet()) {
				key = entry.getKey();
				relList = entry.getValue();
				for (VatGoosInventoryRelationVO vo : relList) {
					InventoryAliasVO alvo = new InventoryAliasVO();
					alvo.setAliasname(vo.getSpmc());
					alvo.setSpec(vo.getInvspec());
					alvo.setPk_corp(pk_corp);
					alvo.setPk_inventory(vo.getPk_inventory());
					alvo.setUnit(vo.getUnit());
					alvo.setCalcmode(vo.getCalcmode());
					alvo.setHsl(vo.getHsl());
					key = alvo.getAliasname()+","+alvo.getSpec()+","+alvo.getUnit();
					if(invenMap.containsKey(key)){
						InventoryAliasVO ivo = invenMap.get(key);
						ivo.setPk_inventory(vo.getPk_inventory());
						ivo.setCalcmode(vo.getCalcmode());
						ivo.setHsl(vo.getHsl());
						singleObjectBO.update(ivo);
					}else{
						singleObjectBO.insertVO(pk_corp, alvo);
					}
					
				}

//				if (invenvo == null) {
//					continue;
//				}
//
//				demo = invenvo.getMemo();
//				if (StringUtil.isEmpty(demo)) {
//					demo = "";
//				}
//
//				relList = entry.getValue();
//
//				for (VatGoosInventoryRelationVO relvo : relList) {
//					spec = relvo.getSpmc();
//					if (!StringUtil.isEmpty(spec)) {
//						if (!demo.contains(spec)) {
//							flag = true;
//
//							if (StringUtil.isEmpty(demo)) {
//								demo = spec;
//							} else {
//								demo += "," + spec;
//							}
//						}
//
//						newRelList.add(relvo);
//					}
//				}
//
//				if (flag) {
//					invenvo.setMemo(demo);
//					invenList.add(invenvo);
//				}
//
//			}

//			if (invenList.size() > 0) {
//				singleObjectBO.updateAry(invenList.toArray(new InventoryVO[0]), new String[] { "memo" });
//			}
//			List<InventoryAliasVO> aliaslist = new ArrayList<InventoryAliasVO>();
//			if (newRelList.size() > 0) {
//				for (VatGoosInventoryRelationVO vo : newRelList) {
//					InventoryAliasVO alvo = new InventoryAliasVO();
//					alvo.setAliasname(vo.getSpmc());
//					alvo.setSpec(vo.getInvspec());
//					alvo.setPk_corp(pk_corp);
//					alvo.setPk_inventory(vo.getPk_inventory());
//					alvo.setPk_alias(vo.getPk_goodsinvenrela());
//					
//					
//					vo.setCoperatorid(userid);
//					vo.setDoperatedate(new DZFDate());
//					vo.setPk_corp(pk_corp);
//
//				}
//
//				singleObjectBO.insertVOArr(pk_corp, newRelList.toArray(new VatGoosInventoryRelationVO[0]));
			}

	//	}

	}

	@Override
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, VATSaleInvoiceVO2[] vos, InventorySetVO invsetvo)
			throws DZFWarpException {

		String newrule = gl_cpacckmserv.queryAccountRule(pk_corp);
		List<VATSaleInvoiceVO2> saleList = constructVatSale(vos, pk_corp);
		int pprule = invsetvo.getChppjscgz();//匹配规则
		if (saleList == null || saleList.size() == 0)
			throw new BusinessException("未找销项发票数据，请检查");

		AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);

		Map<String, AuxiliaryAccountBVO> invenMap = new LinkedHashMap<>();
		Map<String, InventoryAliasVO> invenMap1 = new LinkedHashMap<>();

		if (invenvos != null && invenvos.length > 0) {
			List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
			if(pprule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位
				invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", null, "unit" });
			}else{
				invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec", "unit" });
			}
			//invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec", "unit" });
			Map<String, AuxiliaryAccountBVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invenList,
					new String[] { "pk_auacount_b" });
			invenMap1 = buildInvenMapModel7(tempMap, invenMap, pk_corp,pprule);
		}

		Map<String, VATSaleInvoiceBVO2> bvoMap = buildGoodsInvenRelaMapModel7(saleList, invsetvo);
		List<InventoryAliasVO> list = null;
		if (bvoMap != null && bvoMap.size() > 0) {
			list = new ArrayList<InventoryAliasVO>();
			Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
			String key;
			AuxiliaryAccountBVO invenvo;
			VATSaleInvoiceBVO2 bvo;
			InventoryAliasVO relvo = null;
			InventoryAliasVO relvo1 = null;
			for (Map.Entry<String, VATSaleInvoiceBVO2> entry : bvoMap.entrySet()) {
				relvo = null;
				key = entry.getKey();
				bvo = entry.getValue();
				invenvo = invenMap.get(key);

				// 先匹配别名 别名不存在在匹配存货
				if (invenMap1.containsKey(key)) {
					relvo = invenMap1.get(key);
				}
				if (relvo == null) {
					relvo = new InventoryAliasVO();
					relvo.setAliasname(bvo.getBspmc());
					relvo.setSpec(bvo.getInvspec());
					relvo.setUnit(bvo.getMeasurename());
					if (invenvo != null) {
						relvo.setPk_inventory(invenvo.getPk_auacount_b());
						relvo.setChukukmid(invenvo.getChukukmid());
						relvo.setKmclassify(invenvo.getKmclassify());
						YntCpaccountVO accvo = getAccountVO(accmap, invenvo.getChukukmid());
						if (accvo != null) {
							relvo.setKmmc_sale(accvo.getAccountname());
							relvo.setChukukmcode(accvo.getAccountcode());
						}
						accvo = getAccountVO(accmap, invenvo.getKmclassify());
						if (accvo != null) {
							relvo.setKmmc_invcl(accvo.getAccountname());
							relvo.setKmclasscode(accvo.getAccountcode());
						}
						if(StringUtil.isEmpty(relvo.getChukukmid())){
							accvo = getAccountVO(accmap, bvo.getPk_accsubj());
							if(accvo == null){
								accvo = ocrinterface.queryCategorSubj(bvo.getPk_billcategory(), new String[] { "101015", "101110" }, 1,
										pk_corp, accmap, newrule);
							}
							if(accvo==null){
								accvo = getFisrtNextLeafAccount("600101",accmap);
							}
							if(accvo==null){
								accvo = getFisrtNextLeafAccount("500101",accmap);
							}
							if (accvo != null) {
								relvo.setKmmc_sale(accvo.getAccountname());
								relvo.setChukukmcode(accvo.getAccountcode());
								relvo.setChukukmid(accvo.getPk_corp_account());
							}
						}
						if(StringUtil.isEmpty(relvo.getKmclassify())){
							
							accvo = ocrinterface.queryCategorSubj(bvo.getPk_billcategory(), new String[] { "11" }, 2, pk_corp, accmap,
									newrule);
							if(accvo==null){
								accvo = getFisrtNextLeafAccount("1405",accmap);
							}
						
							if (accvo != null) {
								relvo.setKmmc_invcl(accvo.getAccountname());
								relvo.setKmclasscode(accvo.getAccountcode());
								relvo.setKmclassify(accvo.getPk_corp_account());
							}
							
						}
						
						String name = invenvo.getName();

						if (!StringUtil.isEmpty(invenvo.getSpec())) {
							name = name + " (" + invenvo.getSpec() + ")";
						}

						if (!StringUtil.isEmpty(invenvo.getUnit())) {
							name = name + " " + invenvo.getUnit();
						}
						relvo.setName(name);

						relvo1 = invenMap1.get(key);
						if (relvo1 != null)
							relvo.setPk_alias(relvo1.getPk_alias());
					} else {
						YntCpaccountVO accvo = null;
						String pk_accsubj = null;
						relvo.setChukukmid(invsetvo.getKcspckkm());
						relvo.setKmclassify(invsetvo.getKcsprkkm());
						
						accvo = getAccountVO(accmap, bvo.getPk_accsubj());
						if(accvo == null){
							accvo = ocrinterface.queryCategorSubj(bvo.getPk_billcategory(), new String[] { "101015", "101110" }, 1,
								pk_corp, accmap, newrule);
						}
						if(accvo==null){
							accvo = getFisrtNextLeafAccount("600101",accmap);
						}
						if(accvo==null){
							accvo = getFisrtNextLeafAccount("500101",accmap);
						}
					
						if (accvo != null) {
							relvo.setKmmc_sale(accvo.getAccountname());
							relvo.setChukukmcode(accvo.getAccountcode());
							relvo.setChukukmid(accvo.getPk_corp_account());
						}
						
						accvo = ocrinterface.queryCategorSubj(bvo.getPk_billcategory(), new String[] { "11" }, 2, pk_corp, accmap,
								newrule);
						if(accvo==null){
							accvo = getFisrtNextLeafAccount("1405",accmap);
						}
					
						if (accvo != null) {
							relvo.setKmmc_invcl(accvo.getAccountname());
							relvo.setKmclasscode(accvo.getAccountcode());
							relvo.setKmclassify(accvo.getPk_corp_account());
						}
					}
					if (relvo.getHsl() == null || DZFDouble.ZERO_DBL.compareTo(relvo.getHsl()) == 0) {
						relvo.setHsl(DZFDouble.ONE_DBL);
					}
				}
				relvo.setFphm(bvo.getFphm());//发票号
				list.add(relvo);
			}
		}
		
		
		if(list!=null && list.size()>0){
			List<String> slist = new ArrayList<>();
			for (VATSaleInvoiceVO2 svo : vos) {
				slist.add(svo.getInperiod());
			}
			Map<String,List<VATSaleInvoiceBVO2>> salemap = ocrinterface.querySaleInvoiceInfo(pk_corp, slist, pprule);
			int numPrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf009"));
			int pricePrecision = Integer.valueOf(sys_parameteract.queryParamterValueByCode(pk_corp, "dzf010"));
			for (InventoryAliasVO rvo : list) {
				InventorySaleInfoVO saleinfo = new InventorySaleInfoVO();
				saleinfo.setName(rvo.getAliasname());
				saleinfo.setSpec(rvo.getSpec());
				saleinfo.setUnit(rvo.getUnit());
				saleinfo.setPk_corp(pk_corp);
				InventorySaleInfoVO infovo = ocrinterface.querySaleBillInfo(slist, pprule, saleinfo,salemap,numPrecision,pricePrecision);
				if(infovo!=null){
					rvo.setSaleNumber(infovo.getSaleNumber());
					rvo.setSalePrice(infovo.getSalePrice());
				}
				
			}
		}
		return list;
	}

	private YntCpaccountVO getAccountVO(Map<String, YntCpaccountVO> accmap, String accid) {
		if (accmap == null || accmap.size() == 0)
			return null;
		if(StringUtil.isEmpty(accid)){
			return null;
		}
		return accmap.get(accid);
	}

	private Map<String, InventoryAliasVO> buildInvenMapModel7(Map<String, AuxiliaryAccountBVO> tempinMap,
			Map<String, AuxiliaryAccountBVO> invenMap, String pk_corp,int pprule) {

		Map<String, InventoryAliasVO> invenMap1 = new LinkedHashMap<String, InventoryAliasVO>();

		List<InventoryAliasVO> list = queryInventoryAliasVO(pk_corp);
		Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);

		if (list != null && list.size() > 0) {
			String key;
			String pk_inventory;
			AuxiliaryAccountBVO avo = null;
			for (InventoryAliasVO vo : list) {
				//key = vo.getAliasname() + "," + vo.getSpec() + "," + vo.getUnit();
				if(pprule == InventoryConstant.IC_RULE_1 ){//存货名称+计量单位
					key = vo.getAliasname() + ",null," + vo.getUnit();
				}else{
					key = vo.getAliasname() + "," + vo.getSpec() + "," + vo.getUnit();
				}
				if (invenMap1.containsKey(key)) {
					continue;
				}

				pk_inventory = vo.getPk_inventory();
				if (!StringUtil.isEmpty(pk_inventory) && tempinMap.containsKey(pk_inventory)) {
					avo = tempinMap.get(pk_inventory);
					vo.setChukukmid(avo.getChukukmid());
					vo.setKmclassify(avo.getKmclassify());
					YntCpaccountVO accvo = getAccountVO(accmap, vo.getChukukmid());
					if (accvo != null) {
						vo.setKmmc_sale(accvo.getAccountname());
					}
					accvo = getAccountVO(accmap, vo.getKmclassify());
					if (accvo != null) {
						vo.setKmmc_invcl(accvo.getAccountname());
					}
					vo.setName(avo.getName());
					invenMap1.put(key, vo);
				}
			}
		}
		return invenMap1;
	}

	private List<InventoryAliasVO> queryInventoryAliasVO(String pk_corp) throws DZFWarpException {

		String sql = "Select * From ynt_icalias y Where y.pk_corp = ? and nvl(dr,0) = 0 order by ts desc";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		List<InventoryAliasVO> list = (List<InventoryAliasVO>) singleObjectBO.executeQuery(sql, sp,
				new BeanListProcessor(InventoryAliasVO.class));

		return list;

	}
	
	private String buildByRule(String name, String gg, String unit, int rule){
		String key = null;
		if(rule == InventoryConstant.IC_RULE_1){//存货名称+计量单位
			key = name + ",null," + unit;
		}else{
			key = name + "," + gg + "," + unit;
		}
		
		return key;
	}

	private Map<String, VATSaleInvoiceBVO2> buildGoodsInvenRelaMapModel7(List<VATSaleInvoiceVO2> saleList,
			InventorySetVO invsetvo) {

		int pprule = invsetvo.getChppjscgz();//匹配规则
		Map<String, VATSaleInvoiceBVO2> map = new LinkedHashMap<>();
		Map<String, BillCategoryVO> catemap = new HashMap<String, BillCategoryVO>();
		String key;
		VATSaleInvoiceBVO2[] bvos = null;

		for (VATSaleInvoiceVO2 vo : saleList) {
			if (!StringUtil.isEmpty(vo.getPk_tzpz_h())) {
				continue;
			}
			bvos = (VATSaleInvoiceBVO2[]) vo.getChildren();
			if(bvos == null || bvos.length==0){
				VATSaleInvoiceBVO2 mvo = new VATSaleInvoiceBVO2();
				mvo.setPk_billcategory(vo.getPk_model_h());
				mvo.setBspmc(OcrUtil.execInvname(vo.getSpmc()));
				bvos = new VATSaleInvoiceBVO2[]{mvo};
			}
			
			if (bvos != null && bvos.length > 0) {
				for (VATSaleInvoiceBVO2 bvo : bvos) {
					if(!ocrinterface.checkIsMatchCategroy(bvo.getPk_billcategory(), catemap)){
						continue;
					}
					bvo.setPk_accsubj(vo.getPk_subject());
					bvo.setBspmc(OcrUtil.execInvname(bvo.getBspmc()));
//					key = bvo.getBspmc() + "," + bvo.getInvspec() + "," + bvo.getMeasurename();
					key = buildByRule(bvo.getBspmc(), 
							bvo.getInvspec(), bvo.getMeasurename(), pprule);

					if (!map.containsKey(key)) {
						bvo.setFphm(vo.getFp_hm());
						map.put(key, bvo);
					}

				}
			}
		}
		return map;
	}

	@Override
	public InventoryAliasVO[] saveInventoryData(String pk_corp, InventoryAliasVO[] vos, List<Grid> logList) throws DZFWarpException {

		if (vos == null || vos.length == 0)
			return vos;

		// 如果存货名称为空 按照别名新增存货
		InventorySetVO invsetvo = gl_ic_invtorysetserv.query(pk_corp);
		int chcbjzfs = invsetvo.getChcbjzfs();
		for (InventoryAliasVO vo : vos) {
			if (StringUtil.isEmpty(vo.getName()))
				vo.setName(vo.getAliasname());
			if(chcbjzfs==InventoryConstant.IC_FZMXHS){
				vo.setKmclassify(null);
				vo.setChukukmid(null);
			}
		}

		// 新增存货
		List<InventoryAliasVO> nlista = new ArrayList<>();
		List<InventoryAliasVO> ulista = new ArrayList<>();
		List<InventoryAliasVO> dlista = new ArrayList<>();
		List<AuxiliaryAccountBVO> nlistb = new ArrayList<>();
		List<AuxiliaryAccountBVO> ulistb = new ArrayList<>();
		//存货日志
		Grid nlistaGrid = new Grid();
		Grid ulistaGrid = new Grid();
		Grid nlistbGrid = new Grid();
		Grid ulistbGrid = new Grid();

		List<String> listkey = new ArrayList<>();
		AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);

		Map<String, AuxiliaryAccountBVO> invenMap = new LinkedHashMap<>();
		if (invenvos != null && invenvos.length > 0) {
			List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
			invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "pk_auacount_b" });
		}
		
		//InventorySetVO invsetvo = gl_ic_invtorysetserv.query(pk_corp);
		int pprule = invsetvo.getChppjscgz();
		// Map<String, InventoryAliasVO> alimap = new LinkedHashMap<>();
		String key = null;
		for (InventoryAliasVO vo : vos) {
			//预处理
//			if(pprule == InventoryConstant.IC_RULE_1){
//				vo.setSpec(null);
//				vo.setInvtype(null);
//			}
			
			// 存货存在 是否新增别名
			if (!StringUtil.isEmpty(vo.getPk_inventory())) {
				AuxiliaryAccountBVO invvo = invenMap.get(vo.getPk_inventory());
				if (invvo == null) {
					throw new BusinessException("存货[" + vo.getName() + "]不存在或者被删除");
				}
				String invkey = getNameInvInfoKey(invvo,pprule);
				String aliaskey = getNameAliasInfoKey(vo,pprule);
				if (!StringUtil.isEmpty(vo.getPk_alias())) {
					// 如果别名也存在 更新存货 新的别名
					if (!invkey.equals(aliaskey)) {
						ulista.add(vo);
					} else {
						dlista.add(vo);
					}
				} else {
					// 如果别名不存在 并且存货中也不包含该别名 新增别名
					if (!invkey.equals(aliaskey)) {
						if (vo.getHsl() == null || DZFDouble.ZERO_DBL.compareTo(vo.getHsl()) == 0) {
							vo.setHsl(DZFDouble.ONE_DBL);
						}
						vo.setPk_corp(pk_corp);
						vo.setDr(0);
						nlista.add(vo);
					}
				}
				AuxiliaryAccountBVO bvo = new AuxiliaryAccountBVO();
				bvo.setPk_auacount_b(vo.getPk_inventory());
				bvo.setChukukmid(vo.getChukukmid());
				bvo.setKmclassify(vo.getKmclassify());
				ulistb.add(bvo);
			} else {
				if(pprule == InventoryConstant.IC_RULE_1){
					vo.setSpec(null);
					vo.setInvtype(null);
				}
				// 新增存货
				AuxiliaryAccountBVO bvo = buildAuxiliaryByMatch(vo, pk_corp, pprule);

				String invkey = getNameInvInfoKey(bvo,pprule);

				if (!listkey.contains(invkey)) {
					listkey.add(invkey);
					nlistb.add(bvo);
				}
			}
		}

		if (nlistb != null && nlistb.size() > 0) {
			gl_fzhsserv.saveBs(nlistb, true);
			nlistbGrid.setMsg("新增存货 : 编码："+nlistb.get(0).getCode()+"， 名称："+nlistb.get(0).getName()+"， 等"+nlistb.size()+"条；");
			logList.add(nlistbGrid);
			// 存货新增 判断是否需要新增别名
			for (AuxiliaryAccountBVO vo2 : nlistb) {
//				String key1 = vo2.getName() + "," + vo2.getSpec() + "," + vo2.getUnit();
				String key1 = buildByRule(vo2.getName(), vo2.getSpec(), vo2.getUnit(), pprule);
				for (InventoryAliasVO vo1 : vos) {
//					key = vo1.getName() + "," + vo1.getSpec() + "," + vo1.getUnit();
					key = buildByRule(vo1.getName(), vo1.getSpec(), vo2.getUnit(), pprule);
					if (key1.equals(key)) {
						if (!vo1.getAliasname().equals(vo1.getName())) {
							// 如果别名和存货名不相等
							vo1.setPk_inventory(vo2.getPk_auacount_b());
							if (!StringUtil.isEmpty(vo1.getPk_alias())) {
								// 如果别名也存在 更新存货 新的别名
								ulista.add(vo1);
							} else {
								// 如果别名不存在 并且存货中也不包含该别名 新增别名
								if (vo1.getHsl() == null || DZFDouble.ZERO_DBL.compareTo(vo1.getHsl()) == 0) {
									vo1.setHsl(DZFDouble.ONE_DBL);
								}
								vo1.setPk_corp(pk_corp);
								vo1.setDr(0);
								nlista.add(vo1);
							}
						}
					}
				}
			}
		}

		if (ulistb != null && ulistb.size() > 0) {
			singleObjectBO.updateAry(ulistb.toArray(new AuxiliaryAccountBVO[ulistb.size()]),
					new String[] { "kmclassify", "chukukmid" });
			AuxiliaryAccountBVO updateVO = (AuxiliaryAccountBVO) singleObjectBO.queryByPrimaryKey(AuxiliaryAccountBVO.class, ulistb.get(0).getPk_auacount_b());
			//添加修改存货日志
			ulistbGrid.setMsg("修改存货 : 编码："+updateVO.getCode()+"， 名称："+updateVO.getName()+"， 等"+ulistb.size()+"条；");
			logList.add(ulistbGrid);
		}

		if (nlista != null && nlista.size() > 0) {
			gl_ic_invtoryaliasserv.insertAliasVOS(nlista.toArray(new InventoryAliasVO[nlista.size()]), pk_corp);
			//添加新增别名日志
			nlistaGrid.setMsg("新增别名 : 存货："+nlista.get(0).getName()+"， 别名："+nlista.get(0).getAliasname()+"， 等"+nlista.size()+"条；");
			logList.add(nlistaGrid);
		}

		if (ulista != null && ulista.size() > 0) {
			gl_ic_invtoryaliasserv.updateAliasVOS(ulista.toArray(new InventoryAliasVO[ulista.size()]), pk_corp,
					new String[] { "pk_inventory", "hsl", "calcmode" });
			//添加修改别名日志
			InventoryAliasVO updateAliasVo = (InventoryAliasVO) singleObjectBO.queryByPrimaryKey(InventoryAliasVO.class, ulista.get(0).getPk_alias());
			ulistaGrid.setMsg("修改别名 : 存货："+updateAliasVo.getName()+"， 别名："+updateAliasVo.getAliasname()+"， 等"+ulista.size()+"条；");
			logList.add(ulistaGrid);
		}

		if (dlista != null && dlista.size() > 0) {
			List<String> list = new ArrayList<>();
			for (InventoryAliasVO vo : dlista) {
				list.add(vo.getPk_alias());
			}
			gl_ic_invtoryaliasserv.deleteByPks(list.toArray(new String[list.size()]), pk_corp);
		}

		checkInventorySet(pk_corp, nlistb, ulistb);

		return vos;
	}
	
	private AuxiliaryAccountBVO buildAuxiliaryByMatch(InventoryAliasVO vo,
			String pk_corp, int pprule){
		AuxiliaryAccountBVO bvo = new AuxiliaryAccountBVO();
		// bvo.setCode(yntBoPubUtil.getFZHsCode(pk_corp,
		// AuxiliaryConstant.ITEM_INVENTORY));
		bvo.setUnit(vo.getUnit());
		
		bvo.setSpec(vo.getSpec());
		bvo.setName(vo.getName());
		bvo.setChukukmid(vo.getChukukmid());
		bvo.setKmclassify(vo.getKmclassify());
		bvo.setPk_corp(pk_corp);
		bvo.setDr(0);
		bvo.setPk_auacount_h(AuxiliaryConstant.ITEM_INVENTORY);
		
		return bvo;
	}

	private void checkInventorySet(String pk_corp, List<AuxiliaryAccountBVO> nlistb, List<AuxiliaryAccountBVO> ulistb) {

		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		for (AuxiliaryAccountBVO vo2 : nlistb) {
			set1.add(vo2.getChukukmid());
			set2.add(vo2.getPk_auacount_b());
		}

		for (AuxiliaryAccountBVO vo2 : ulistb) {
			set1.add(vo2.getChukukmid());
			set2.add(vo2.getPk_auacount_b());
		}

		Map<String, Set<String>> pzkmidmap = new HashMap<String, Set<String>>();
		pzkmidmap.put("KMID", set1);
		pzkmidmap.put("CHID", set2);
		CorpVO cpvo = corpService.queryByPk(pk_corp);
		String err = inventory_setcheck.checkInventorySetCommon("", cpvo, pzkmidmap);
		if (!StringUtil.isEmpty(err)) {
			throw new BusinessException(err);
		}

	}

	private String getNameInvInfoKey(AuxiliaryAccountBVO invo,int pprule) {
		StringBuffer strb = new StringBuffer();
		strb.append(appendIsNull(invo.getName()));
		if(pprule == InventoryConstant.IC_RULE_1){//存货名称+计量单位
			strb.append(appendIsNull(null));
			strb.append(appendIsNull(null));
		}else{
			strb.append(appendIsNull(invo.getSpec()));
			strb.append(appendIsNull(invo.getInvtype()));
		}
		strb.append(appendIsNull(invo.getUnit()));
		return strb.toString();

	}

	private String getNameAliasInfoKey(InventoryAliasVO invo,int pprule) {
		StringBuffer strb = new StringBuffer();
		strb.append(appendIsNull(invo.getAliasname()));
		
		if(pprule == InventoryConstant.IC_RULE_1){//存货名称+计量单位
			strb.append(appendIsNull(null));
			strb.append(appendIsNull(null));
		}else{
			strb.append(appendIsNull(invo.getSpec()));
			strb.append(appendIsNull(invo.getInvtype()));
		}
		strb.append(appendIsNull(invo.getUnit()));
		return strb.toString();

	}

	private String appendIsNull(String info) {
		StringBuffer strb = new StringBuffer();
		if (StringUtil.isEmpty(info)) {
			strb.append("null");
		} else {
			strb.append(info);
		}
		return strb.toString();
	}

	@Override
	public void createPZ(VATSaleInvoiceVO2 vo, String pk_corp, String userid, boolean accway, boolean isT,
			VatInvoiceSetVO setvo, InventorySetVO invsetvo, String jsfs) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		if(StringUtils.isEmpty(vo.getPk_model_h())){
			throw new BusinessException("销项发票:业务类型为空,请重新选择业务类型");
		}
		//YntCpaccountVO[] accounts = AccountCache.getInstance().get(null, pk_corp);
		// 1-----普票 2----专票 3--- 未开票
		int fp_style = getFpStyle(vo);
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();

		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		headVO.setFp_style(fp_style);

		Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
		YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
		
		Map<String, Object> paramMap=zncsVoucher.initVoucherParam(corpvo,vo.getInperiod(),false);
		List<List<Object[]>> levelList=(List<List<Object[]>>) paramMap.get("levelList");
		Map<String, Object[]> categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
		Set<String> zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
		InventorySetVO inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
		Map<String, InventoryAliasVO> fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
		List<Object> paramList = (List<Object>) paramMap.get("paramList");
		Map<String, BdCurrencyVO> currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
		Map<String, Object[]> rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
		Map<String, String> bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
		Map<String, AuxiliaryAccountBVO> assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
		Map<String, List<AccsetVO>> accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
		Map<String, String> jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
		String tradeCode=(String) paramMap.get("tradeCode");
		String newrule = (String) paramMap.get("newrule");
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");
		
		//Map<String, YntCpaccountVO> ccountMap = AccountCache.getInstance().getMap(userid, pk_corp);
		//生成凭证
		//tblist = createTzpzBVOXx(vo, userid, invsetvo, ccountMap, jsfs, fp_style);
		List<VATSaleInvoiceVO2> ll = new ArrayList<VATSaleInvoiceVO2>();
		ll.add(vo);
		Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
		List<OcrInvoiceVO> invoiceList = changeToOcr(ll, pk_corp);
		List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, vo.getInperiod(), pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
		//取出凭证子类
		tblist = Arrays.asList((TzpzBVO[])tzpzhvoList.get(0).getChildren());
		//取出凭证主类//清空子类
		//tzpzhvoList.get(0).setChildren(null);
		//sortTzpz(tblist);
		//根据规则设置摘要q
		//setPzZy(tblist, setvo, vo);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		vo.setCount(1);
		createTzpzHVO(headVO, ll, pk_corp, userid, null, null, vo.getJshj(), accway,null,setvo);
		if (vo.getPzstatus() != null) {
			headVO.setVbillstatus(vo.getPzstatus());
		}

		headVO.setPk_image_group(vo.getPk_image_group());
		updateImageGroup(vo.getPk_image_group());

		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));

		if (isT) {
			createTempPZ(headVO, new VATSaleInvoiceVO2[] { vo }, pk_corp,null);
		} else {
			headVO = voucher.saveVoucher(corpvo, headVO);
		}
		List<VATSaleInvoiceVO2> list = new ArrayList<>();
		list.add(vo);
		updateOperate(list, jsfs, pk_corp);// 存货
	}

	@Override
	public void saveCombinePZ(List<VATSaleInvoiceVO2> list, String pk_corp, String userid, VatInvoiceSetVO setvo,
			boolean accway, boolean isT, InventorySetVO invsetvo, String jsfs) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		int fp_style;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		List<TzpzBVO> inlist=null;
		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);

		String maxPeriod = null;
		List<String> imageGroupList = new ArrayList<>();
		//Map<String, YntCpaccountVO> ccountMap = AccountCache.getInstance().getMap(userid, pk_corp);
		Integer pzstatus = null;
		
		Map<String,YntCpaccountVO> accountMap = accountService.queryMapByPk(corpvo.getPk_corp());
		YntCpaccountVO[] accVOs=accountService.queryByPk(corpvo.getPk_corp());
		
		Map<String, Object> paramMap=zncsVoucher.initVoucherParam(corpvo,list.get(0).getInperiod(),false);
		List<List<Object[]>> levelList=(List<List<Object[]>>) paramMap.get("levelList");
		Map<String, Object[]> categoryMap =(Map<String, Object[]>) paramMap.get("categoryMap");
		Map<Integer, AuxiliaryAccountHVO> fzhsHeadMap=(Map<Integer, AuxiliaryAccountHVO>) paramMap.get("fzhsHeadMap");
		Set<String> zyFzhsList=(Set<String>) paramMap.get("zyFzhsList");
		Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap=(Map<String, List<AuxiliaryAccountBVO>>) paramMap.get("fzhsBodyMap");
		InventorySetVO inventorySetVO=(InventorySetVO) paramMap.get("inventorySetVO");
		Map<String, InventoryAliasVO> fzhsBMMap=(Map<String, InventoryAliasVO>) paramMap.get("fzhsBMMap");
		List<Object> paramList = (List<Object>) paramMap.get("paramList");
		Map<String, BdCurrencyVO> currMap=(Map<String, BdCurrencyVO>) paramMap.get("currMap");
		Map<String, Object[]> rateMap=(Map<String, Object[]>) paramMap.get("rateMap");
		Map<String, String> bankAccountMap=(Map<String, String>) paramMap.get("bankAccountMap");
		Map<String, AuxiliaryAccountBVO> assistMap=(Map<String, AuxiliaryAccountBVO>) paramMap.get("assistMap");
		Map<String, List<AccsetVO>> accsetMap=(Map<String, List<AccsetVO>>) paramMap.get("accsetMap");
		Map<String, List<AccsetKeywordBVO2>> accsetKeywordBVO2Map=(Map<String, List<AccsetKeywordBVO2>>) paramMap.get("accsetKeywordBVO2Map");
		Map<String, String> jituanSubMap=(Map<String, String>) paramMap.get("jituanSubMap");
		String tradeCode=(String) paramMap.get("tradeCode");
		String newrule = (String) paramMap.get("newrule");
		List<AuxiliaryAccountBVO> chFzhsBodyVOs=(List<AuxiliaryAccountBVO>) paramMap.get("chFzhsBodyVOs");
		
		// DZFBoolean iszhFlag = null;
		for (VATSaleInvoiceVO2 vo : list) {
			// iszhFlag = getCheckZhuanFlag(vo, iszhFlag);
			if(StringUtils.isEmpty(vo.getPk_model_h())){
				throw new BusinessException("销项发票:业务类型为空,请重新选择业务类型");
			}
			fp_style = getFpStyle(vo);
			//生成凭证
			//List<TzpzBVO> pzlist = createTzpzBVOXx(vo, userid, invsetvo, ccountMap, jsfs, fp_style);
			inlist = new ArrayList<TzpzBVO>();
			ArrayList<VATSaleInvoiceVO2> voList = new ArrayList<VATSaleInvoiceVO2>();
			voList.add(vo);
			Map<String, Map<String, Object>> checkMsgMap=new HashMap<String, Map<String, Object>>();
			List<OcrInvoiceVO> invoiceList = changeToOcr(voList, pk_corp);
			List<TzpzHVO> tzpzhvoList = zncsVoucher.processGeneralTzpzVOsByInvoice(invoiceList, vo.getInperiod(), pk_corp, userid, checkMsgMap, levelList, categoryMap, fzhsHeadMap, zyFzhsList, fzhsBodyMap, inventorySetVO, corpvo, fzhsBMMap, paramList, currMap, rateMap, bankAccountMap, accountMap, assistMap, accsetMap, accsetKeywordBVO2Map, jituanSubMap, accVOs, tradeCode, newrule, chFzhsBodyVOs);
			TzpzBVO[] pzbvoArr = (TzpzBVO[])tzpzhvoList.get(0).getChildren();
			inlist.addAll(Arrays.asList(pzbvoArr));			
			if (pzstatus == null) {
				if (vo.getPzstatus() != null) {
					pzstatus = vo.getPzstatus();
				}
			}
			//根据规则设置摘要q
			setPzZy(inlist, setvo, vo);
			tblist.addAll(inlist);
			// 找最大交易日期
			if (maxPeriod == null) {
				maxPeriod = vo.getInperiod();
			} else if (!StringUtil.isEmpty(vo.getInperiod()) && maxPeriod.compareTo(vo.getInperiod()) < 0) {
				maxPeriod = vo.getInperiod();
			}
			if (!StringUtil.isEmpty(vo.getPk_image_group())) {
				imageGroupList.add(vo.getPk_image_group());
			}
		}
		//sortTzpz(tblist);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : tblist) {
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}

		list.get(0).setInperiod(maxPeriod);
		list.get(0).setCount(list.size());
		createTzpzHVO(headVO, list, pk_corp, userid, null, null, totalMny, accway,null,setvo);
		if (pzstatus != null) {
			headVO.setVbillstatus(pzstatus);
		}
		// headvo sourcebillid 重新赋值
		List<String> pks = new ArrayList<String>();
		for (VATSaleInvoiceVO2 svo : list) {
			pks.add(svo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));

		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));
		if (imageGroupList != null && imageGroupList.size() > 0) {
			// 合并图片组
//			String groupId = mergeImage(pk_corp, imageGroupList);
			String groupId = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			headVO.setPk_image_group(groupId);
			updateImageGroup(groupId);
		}

		if (isT) {
			createTempPZ(headVO, list.toArray(new VATSaleInvoiceVO2[list.size()]), pk_corp,null);
		} else {
			headVO.setSourcebillid(sourcebillid);
			headVO = voucher.saveVoucher(corpvo, headVO);
		}

		updateOperate(list, jsfs, pk_corp);// 存货
	}

	private List<TzpzBVO> createTzpzBVOXx(VATSaleInvoiceVO2 vo, String userid, InventorySetVO invsetvo,
			Map<String, YntCpaccountVO> ccountMap, String jsfs, int fp_style) {
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> bodyList = null;

		String pk_accsunj = null;

		if (invsetvo == null)
			throw new BusinessException("存货设置未设置!");
		int chcbjzfs = invsetvo.getChcbjzfs();

		if ("01".equals(jsfs)) {// 往来科目
			pk_accsunj = invsetvo.getYingshoukm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("往来科目未设置!");
			}
		} else if ("02".equals(jsfs)) {// 银行科目
			pk_accsunj = invsetvo.getYinhangkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("银行科目未设置!");
			}
		} else if ("03".equals(jsfs)) {// 现金结算
			pk_accsunj = invsetvo.getXianjinkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("现金结算科目未设置!");
			}
		} else {
			throw new BusinessException("结算方式出错!");
		}

		String zy = null;
		if (StringUtil.isEmpty(vo.getKhmc())) {
			zy = "向客户销售商品";
		} else {
			zy = "向" + vo.getKhmc() + "公司销售商品";
		}
		bodyList = createCommonTzpzBVO(vo, pk_accsunj, userid, zy, "bhjje&bspse", 0, ccountMap, chcbjzfs, 0, false);
		pk_accsunj = null;
		finBodyList.addAll(bodyList);//

		if (chcbjzfs == 0) {// 明细
			pk_accsunj = invsetvo.getKcspckkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("出库科目未设置!");
			}
		} else if (chcbjzfs == 1) {// 大类

		} else if (chcbjzfs == 2) {// 不核算明细
			pk_accsunj = invsetvo.getKcspckkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("出库科目未设置!");
			}
		} else {
			throw new BusinessException("存货成本结转方式出错!");
		}
		bodyList = createCommonTzpzBVO(vo, pk_accsunj, userid, zy, "bhjje", 1, ccountMap, chcbjzfs, 1, true);
		pk_accsunj = null;
		finBodyList.addAll(bodyList);//
		// 销项税额
		String jxsekm = invsetvo.getXxshuiekm();
		if (StringUtil.isEmpty(jxsekm)) {
			throw new BusinessException("销项税额科目未设置!");
		}
		bodyList = createCommonTzpzBVO(vo, jxsekm, userid, zy, "bspse", 1, ccountMap, chcbjzfs, 2, false);
		finBodyList.addAll(bodyList);//
		return finBodyList;
	}

	private List<TzpzBVO> createCommonTzpzBVO(VATSaleInvoiceVO2 vo, String pk_accsubj, String userid, String zy,
			String column1, int vdirect, Map<String, YntCpaccountVO> ccountMap, int chcbjzfs, int newRowno,
			boolean checkinv) {
		List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
		String priceStr = parameterserv.queryParamterValueByCode(vo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		SuperVO[] ibodyvos = (SuperVO[]) vo.getChildren();
		if (ibodyvos == null || ibodyvos.length == 0)
			return bodyList;

		String column2 = null;
		String column3 = null;

		List<TzpzBVO> list = new ArrayList<>();
		// 转换成凭证vo
		DZFDouble taxratio = (DZFDouble) ibodyvos[0].getAttributeValue("bspsl");
		for (SuperVO body : ibodyvos) {
			VATSaleInvoiceBVO2 ibody = (VATSaleInvoiceBVO2) body;
			String inv = ibody.getPk_inventory();
			if (chcbjzfs == 1) {// 大类
				if (checkinv) {
					// 获取存货科目 入库科目
					pk_accsubj = ibody.getPk_accsubj();
					if (StringUtil.isEmpty(pk_accsubj)) {
						throw new BusinessException("存货销售科目为空!");
					}
				}
			}

			YntCpaccountVO cvo = ccountMap.get(pk_accsubj);
			if (cvo == null)
				throw new BusinessException("科目不存在，或已经被删除");

			boolean isleaf = cvo.getIsleaf() == null ? false : cvo.getIsleaf().booleanValue();

			if (!isleaf) {//
				// 第一个下级的最末级
				cvo = getFisrtNextLeafAccount(cvo.getAccountcode(), ccountMap);
			}

			DZFDouble nmny = DZFDouble.ZERO_DBL;
			if (column1.indexOf("&") > 0) {
				column2 = column1.split("&")[0];
				column3 = column1.split("&")[1];
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column3), nmny);
			} else {
				column2 = column1;
				nmny = SafeCompute.add((DZFDouble) ibody.getAttributeValue(column2), nmny);
			}
			if (chcbjzfs != 2) {// 不合算明细 的不生成存货辅助明细
				if (cvo.getIsfzhs().charAt(5) == '1') {
					String spmc = null;
					if (StringUtil.isEmpty(ibody.getBspmc())) {
						spmc = "商品";
					} else {
						spmc = ibody.getBspmc();
					}

					if (StringUtil.isEmpty(vo.getKhmc())) {
						zy = "向客户销售" + spmc;
					} else {
						zy = "向" + vo.getKhmc() + "公司销售" + spmc;
					}
				}
			}
			// 金额为零的 不记录凭证行
			TzpzBVO bvo = createSingleTzpzBVO(cvo, zy, vo, vdirect, nmny, ibody, chcbjzfs, newRowno, userid, checkinv);
			list.add(bvo);
		}

		// 汇总vo

		Map<String, TzpzBVO> map = new LinkedHashMap<String, TzpzBVO>();
		for (TzpzBVO bvo : list) {
			String inv = constructTzpzKey(bvo);
			if (StringUtil.isEmpty(inv)) {
				inv = "aaaaa";
			}
			TzpzBVO temp = null;
			if (!map.containsKey(inv)) {
				temp = bvo;
			} else {
				temp = map.get(inv);
				temp.setNnumber(SafeCompute.add(temp.getNnumber(), bvo.getNnumber()));
				temp.setDfmny(SafeCompute.add(temp.getDfmny(), bvo.getDfmny()));
				temp.setYbdfmny(SafeCompute.add(temp.getYbdfmny(), bvo.getYbdfmny()));
				temp.setJfmny(SafeCompute.add(temp.getJfmny(), bvo.getJfmny()));
				temp.setYbjfmny(SafeCompute.add(temp.getYbjfmny(), bvo.getYbjfmny()));
			}
			if (temp.getNnumber() != null && DZFDouble.ZERO_DBL.compareTo(temp.getNnumber()) != 0) {
				if (vdirect == 1) {
					DZFDouble price = SafeCompute.div(temp.getDfmny(), temp.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
				} else {
					DZFDouble price = SafeCompute.div(temp.getJfmny(), temp.getNnumber());
					price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
					temp.setNprice(price);
				}
			}
			map.put(inv, temp);
		}

		for (TzpzBVO value : map.values()) {
			if (vdirect == 1) {
				if (value.getDfmny() == null || value.getDfmny().doubleValue() == 0) {
					continue;
				}
			} else {
				if (value.getJfmny() == null || value.getJfmny().doubleValue() == 0) {
					continue;
				}
			}
			bodyList.add(value);
		}
		return bodyList;
	}

	// 查询第一分支的最末级科目
	private YntCpaccountVO getFisrtNextLeafAccount(String accountcode, Map<String, YntCpaccountVO> ccountMap) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目
		for (YntCpaccountVO accvo : ccountMap.values()) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(accountcode)) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return null;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	private TzpzBVO createSingleTzpzBVO(YntCpaccountVO cvo, String zy, VATSaleInvoiceVO2 vo, int vdirect,
			DZFDouble totalDebit, VATSaleInvoiceBVO2 ibody, int chcbjzfs, int newRowno, String userid,
			boolean checkinv) {
		String priceStr = parameterserv.queryParamterValueByCode(vo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		TzpzBVO depvo = new TzpzBVO();
		depvo.setPk_accsubj(cvo.getPk_corp_account());
		depvo.setVcode(cvo.getAccountcode());
		depvo.setVname(cvo.getAccountname());
		depvo.setZy(zy);// 摘要
		depvo.setRowno(newRowno);
		if (cvo.getIsfzhs().charAt(0) == '1') {
			AuxiliaryAccountBVO bvo = matchCustomer(vo, vo.getKhmc(), vo.getPk_corp(), userid,
					AuxiliaryConstant.ITEM_CUSTOMER);
			if (bvo == null || StringUtil.isEmpty(bvo.getPk_auacount_b())) {
				// throw new BusinessException("科目【" + cvo.getAccountname() +
				// "】启用客户辅助核算,发票号"+vo.getFp_hm()+"购方名称必须录入!");
				vo.setPzstatus(IVoucherConstants.TEMPORARY);
			}
			if (bvo != null)
				depvo.setFzhsx1(bvo.getPk_auacount_b());
		}

		if (cvo.getIsfzhs().charAt(1) == '1') {
			AuxiliaryAccountBVO bvo = matchCustomer(vo, vo.getKhmc(), vo.getPk_corp(), userid,
					AuxiliaryConstant.ITEM_SUPPLIER);
			if (bvo == null || StringUtil.isEmpty(bvo.getPk_auacount_b())) {
				// throw new BusinessException("科目【" + cvo.getAccountname() +
				// "】启用供应商辅助核算,发票号"+vo.getFp_hm()+"销货方名称必须录入!");
				vo.setPzstatus(IVoucherConstants.TEMPORARY);
			}
			if (bvo != null)
				depvo.setFzhsx2(bvo.getPk_auacount_b());
		}

		if (chcbjzfs != 2) {// 不合算明细 的不生成存货辅助明细
			if (checkinv) {
				if (cvo.getIsfzhs().charAt(5) == '0') {
					throw new BusinessException("科目【" + cvo.getAccountname() + "】未启用存货辅助,请调整存货设置或启用科目辅助!");
				}
			}
			if (cvo.getIsfzhs().charAt(5) == '1') {
				depvo.setFzhsx6(ibody.getPk_inventory());
			}
		}

		depvo.setVdirect(vdirect);
		if (vdirect == 1) {
			depvo.setDfmny(totalDebit);
			depvo.setYbdfmny(totalDebit);
			// 启用数量核算
			if (cvo.getIsnum() != null && cvo.getIsnum().booleanValue()) {
				depvo.setNnumber(ibody.getBnum());
				DZFDouble price = SafeCompute.div(depvo.getDfmny(), depvo.getNnumber());
				price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
				depvo.setNprice(price);
			}
		} else {
			depvo.setJfmny(totalDebit);
			depvo.setYbjfmny(totalDebit);
			// 启用数量核算
			if (cvo.getIsnum() != null && cvo.getIsnum().booleanValue()) {
				depvo.setNnumber(ibody.getBnum());
				DZFDouble price = SafeCompute.div(depvo.getJfmny(), depvo.getNnumber());
				price = price.setScale(iprice, DZFDouble.ROUND_HALF_UP);
				depvo.setNprice(price);
			}
		}

		depvo.setPk_currency(DzfUtil.PK_CNY);
		depvo.setNrate(DZFDouble.ONE_DBL);
		depvo.setPk_corp(vo.getPk_corp());
		// 1-----普票 2----专票 3--- 未开票
		int fp_style = getFpStyle(vo);
		DZFDouble taxratio = SafeCompute.div(ibody.getBspsl(), new DZFDouble(100));
		TaxitemParamVO taxparam = new TaxitemParamVO.Builder(depvo.getPk_corp(), taxratio).UserId(vo.getCoperatorid())
				.InvName(ibody.getBspmc()).Fp_style(fp_style).build();

		return depvo;
	}

	private void updateOtherType(List<VATSaleInvoiceVO2> list) {
		if (list == null || list.size() == 0)
			return;

		for (VATSaleInvoiceVO2 vo : list) {
			vo.setIoperatetype(23);// 劳务
		}
		singleObjectBO.updateAry(list.toArray(new VATSaleInvoiceVO2[list.size()]), new String[] { "ioperatetype" });
	}

	private void updateOperate(List<VATSaleInvoiceVO2> list, String jsfs, String pk_corp) {//
		if (list == null || list.size() == 0)
			return;

		//uglyDeal(list, jsfs, pk_corp, 21);
		
		for (VATSaleInvoiceVO2 vo : list) {
			vo.setIoperatetype(21);
		}
		singleObjectBO.updateAry(list.toArray(new VATSaleInvoiceVO2[list.size()]),
				new String[] { "ioperatetype"});
	}
	
	private void uglyDeal(List<VATSaleInvoiceVO2> list, String jsfs, String pk_corp, int opeType){
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		if(dcList == null || dcList.size() == 0){
			return;
		}
//		dcList = new DcPzmb().filterDataCommon(dcList, 
//				pk_corp, null, null, "Y", null);
		
		Map<String, DcModelHVO> dcmap = DZfcommonTools.hashlizeObjectByPk(dcList, 
				new String[]{"busitypetempname", "vspstylecode", "szstylecode"});
		
		String key;
		String spname = "销售收入";
		String vscode;//类型
		DcModelHVO dcvo;
		//01 往来科目  02 银行科目 03现金结算
		String szcode = "01".equals(jsfs) ? 
					FieldConstant.SZSTYLE_05 : "02".equals(jsfs) 
						? FieldConstant.SZSTYLE_03 : FieldConstant.SZSTYLE_01;//结算方式
		for(VATSaleInvoiceVO2 vo : list){
			
			vscode = vo.getIszhuan() != null && vo.getIszhuan().booleanValue()
					? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
			key = spname + "," + vscode + "," + szcode;
			
			dcvo = dcmap.get(key);
			if(dcvo == null){
				vo.setPk_model_h(null);
				vo.setBusitypetempname(null);
			}else{
				vo.setPk_model_h(dcvo.getPk_model_h());
				vo.setBusitypetempname(dcvo.getBusitypetempname());
			}
			
			vo.setIoperatetype(opeType);
		}
	}

	@Override
	public String saveBusiPeriod(VATSaleInvoiceVO2[] vos, String pk_corp, String period) throws DZFWarpException {
		// 期间关账检查
		boolean isgz = qmgzService.isGz(pk_corp, period);
		if (isgz) {
			throw new BusinessException("所选入账期间" + period + "已关账，请检查");
		}

		StringBuffer msg = new StringBuffer();
		BigDecimal repeatCodeNum = gl_yhdzdserv2.checkIsQjsyjz(pk_corp, period);
		if (repeatCodeNum != null && repeatCodeNum.intValue() > 0) {
			msg.append("<p>所选入账期间" + period + "已损益结转。</p>");
		}

		VATSaleInvoiceVO2 newVO = null;
		int upCount = 0;
		int npCount = 0;

		StringBuffer part = new StringBuffer();
		List<VATSaleInvoiceVO2> list = new ArrayList<VATSaleInvoiceVO2>();
		for (VATSaleInvoiceVO2 vo : vos) {
			vo.setVersion(new DZFDouble(1.0));
			if (!StringUtil.isEmptyWithTrim(vo.getPk_tzpz_h())) {
				part.append("<p>发票号[" + vo.getFp_dm() + "]已生成凭证。</p>");
				npCount++;
				continue;
//			} else if (vo.getIsic() != null && vo.getIsic().booleanValue()) {
			} else if (!StringUtil.isEmpty(vo.getPk_ictrade_h())) {
				part.append("<p>发票号[" + vo.getFp_dm() + "]已生成入库单。</p>");
				npCount++;
				continue;
			}
			if(!StringUtils.isEmpty(vo.getPk_model_h())){
				Map<String, BillCategoryVO> map = gl_yhdzdserv2.queryNewPkcategory(vo.getPk_model_h(), period, pk_corp);
				vo.setPk_category_keyword(null);
				BillCategoryVO billvo = map.get(vo.getPk_model_h());
				if (billvo == null)
				{
					vo.setPk_model_h(null);
					vo.setPk_category_keyword(null);
					List<VATSaleInvoiceVO2> bList = new ArrayList<VATSaleInvoiceVO2>();
					bList.add(vo);
					bList = changeToSale(bList, pk_corp);
					vo = bList.get(0);
					
				}
				else
				{
					vo.setPk_model_h(billvo.getPk_category());
				}
				vo.setPk_model_h(billvo.getPk_category());
				vo.setInperiod(period);
				vo.setPeriod(period);
				List<VATSaleInvoiceBVO2> bvolist = queryBvoByPk(vo.getPk_vatsaleinvoice());
				if(bvolist!=null&&bvolist.size()>0){
					for (VATSaleInvoiceBVO2 bvo : bvolist) {
						bvo.setPk_billcategory(billvo.getPk_category());
						bvo.setPk_category_keyword(null);
					}
					vo.setChildren(bvolist.toArray(new VATSaleInvoiceBVO2[0]));
				}
			}else{
				vo.setInperiod(period);
				vo.setPeriod(period);
			}
			
			
			upCount++;
			list.add(vo);
		}

		if (list != null && list.size() > 0) {
			for (VATSaleInvoiceVO2 vo : list) {
				//设置结算方式，结算科目，入账科目
				if(!StringUtils.isEmpty(vo.getPk_model_h())){
					CategorysetVO setVO = gl_yhdzdserv2.queryCategorySetVO(vo.getPk_model_h());
					vo.setSettlement(setVO.getSettlement()==null?0:setVO.getSettlement());
					vo.setPk_subject(setVO.getPk_accsubj());
					vo.setPk_settlementaccsubj(setVO.getPk_settlementaccsubj());
				}
			}
			singleObjectBO.updateAry(list.toArray(new VATSaleInvoiceVO2[0]),
					new String[] { "inperiod", "pk_model_h", "pk_category_keyword", "busitypetempname", "version",
							"settlement", "pk_subject", "pk_settlementaccsubj","period" });
			for (VATSaleInvoiceVO2 vo : list) {
				SuperVO[] superVOs = vo.getChildren();
				if(superVOs!=null&&superVOs.length>0){
					singleObjectBO.updateAry(superVOs,new String[]{"pk_billcategory","pk_category_keyword"});
				}
				
			}
		}

		msg.append("<p>入账期间更新成功 ").append(upCount).append(" 条")
				.append(npCount > 0 ? ",未更新 " + npCount + " 条。未更新详细原因如下:</p>" + part.toString() : "</p>");

		return msg.toString();
	}

	private Integer getFpStyle(VATSaleInvoiceVO2 vo) {
		Integer fp_style = vo.getIszhuan() != null && vo.getIszhuan().booleanValue()
				? IFpStyleEnum.SPECINVOICE.getValue() : IFpStyleEnum.COMMINVOICE.getValue();
		return fp_style;
	}

	@Override
	public CorpVO chooseTicketWay(String pk_corp) throws DZFWarpException {
		CorpVO corpVO = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);
		TicketNssbhVO nssbvo = getNssbvo(corpVO);// 取当前公司
		if (nssbvo == null) {
			corpVO.setDef12("");
		} else {
			corpVO.setDef12("Y");
		}

		return corpVO;
	}

	private void sortTzpz(List<TzpzBVO> tblist) {
		if (tblist == null || tblist.size() == 0)
			return;

		Collections.sort(tblist, new Comparator<TzpzBVO>() {
			@Override
			public int compare(TzpzBVO o1, TzpzBVO o2) {
				int i = o1.getRowno().compareTo(o2.getRowno());
				return i;
			}
		});

		int rowno = 1;
		for (TzpzBVO bvo : tblist) {
			bvo.setRowno(rowno++);
		}
	}

	/*@Override
	public List<VatBusinessTypeVO> getBusiType(String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			return null;
		
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		
		if(dcList == null || dcList.size() == 0){
			return null;
		}
		//过滤数据
		DcPzmb mb = new DcPzmb();
		mb.sortH(dcList, null, null, "Y");
		dcList = mb.filterDataCommon(dcList, pk_corp, null, null, "Y", null);
		
		List<VatBusinessTypeVO> list = new ArrayList<VatBusinessTypeVO>();
		Map<String, VatBusinessTypeVO> map = new HashMap<String, VatBusinessTypeVO>();
		
		//先组装dcmodel
		String businame;
		VatBusinessTypeVO typevo;
		for(DcModelHVO hvo : dcList){
			businame = hvo.getBusitypetempname();
			if(map.containsKey(businame)){
				typevo = map.get(businame);
				typevo.setShowvalue(typevo.getShowvalue() + "," + hvo.getSzstylecode());
			}else{
				typevo = new VatBusinessTypeVO();
				typevo.setPk_corp(hvo.getPk_corp());//pk_corp
				typevo.setBusiname(businame);
				typevo.setShowvalue(hvo.getSzstylecode());
				list.add(typevo);
				map.put(businame, typevo);
			}
		}
		//再组装历史勾选记录
		String sql = " select * from ynt_vatbusitype y where y.pk_corp = ? and nvl(dr,0) = 0 and y.stype = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(IBillManageConstants.HEBING_JXFP);
		List<VatBusinessTypeVO> typeList = (List<VatBusinessTypeVO>) singleObjectBO.executeQuery(sql, 
				sp, new BeanListProcessor(VatBusinessTypeVO.class));
		
		businame = null;
		typevo = null;
		if(typeList != null && typeList.size() > 0){
			for(VatBusinessTypeVO tvo : typeList){
				businame = tvo.getBusiname();
				if(map.containsKey(businame)){
					typevo = map.get(businame);
					typevo.setPrimaryKey(tvo.getPrimaryKey());
					typevo.setSelectvalue(tvo.getSelectvalue());
				}
			}
		}
		
		return list;
	}*/
	/*
	 *  将销项实体类转换成ocr类
	 */
	private List<OcrInvoiceVO> changeToOcr(List<VATSaleInvoiceVO2> sList,String pk_corp){
		List<OcrInvoiceVO> list = new ArrayList<OcrInvoiceVO>();
		CorpVO corpVO = corpService.queryByPk(pk_corp);
		for (VATSaleInvoiceVO2 svo : sList) {
			OcrInvoiceVO ovo = new OcrInvoiceVO();
			String randomUUID = UUID.randomUUID().toString();
			//回写分类时使用（相当于主键）
			svo.setTempvalue(randomUUID);
			ovo.setPk_invoice(randomUUID);
			ovo.setDatasource(ZncsConst.SJLY_4);//数据来源
			ovo.setVinvoicecode(svo.getFp_dm());// 发票代码
			ovo.setVinvoiceno(svo.getFp_hm());// 发票号
			ovo.setInvoicetype(svo.getIszhuan()==null?"增值税普通发票":(svo.getIszhuan().equals(DZFBoolean.FALSE)?"增值税普通发票":"增值税专用发票"));// 发票类型
			ovo.setNtotaltax(svo.getJshj()==null?null:svo.getJshj().toString());// 价税合计
			ovo.setNmny(svo.getHjje()==null?null:svo.getHjje().toString());// 金额合计
			ovo.setNtaxnmny(svo.getSpse()==null?null:svo.getSpse().toString());// 税额合计
			ovo.setTaxrate(svo.getSpsl()==null?null:svo.getSpsl().toString());//税率
			ovo.setPk_image_group(svo.getPk_image_group());// 图片信息组主键
			ovo.setVmemo(svo.getDemo());// 备注
			ovo.setVfirsrinvname(svo.getSpmc()); // 首件货物名称
			ovo.setPeriod(svo.getInperiod());//入账期间
			ovo.setPk_corp(pk_corp);//入账公司
			ovo.setVsalename(corpVO.getUnitname());//销方
			ovo.setVsaletaxno(svo.getXhfsbh());//销方识别号
			ovo.setVsalephoneaddr(svo.getXhfdzdh());//销方地址电话
			ovo.setVsaleopenacc(svo.getXhfyhzh());//销方银行账号
			ovo.setVpurchname(svo.getKhmc());//购方  为空生成凭证报错，用~！@#代替
			ovo.setVpurchtaxno(svo.getCustidentno());// 购方纳税号
			ovo.setVpurphoneaddr(svo.getGhfdzdh()); //购方地址电话
			ovo.setVpuropenacc(svo.getGhfyhzh());//购方银行账号
			ovo.setDinvoicedate(svo.getKprj().toString());//开票日期、交易日期
			ovo.setIstate(ZncsConst.SBZT_3);//发票类型
			ovo.setSettlement(svo.getSettlement());//结算方式
			ovo.setPk_subject(svo.getPk_subject());//入账科目
			ovo.setPk_settlementaccsubj(svo.getPk_settlementaccsubj());//结算方式
			ovo.setPk_taxaccsubj(svo.getPk_taxaccsubj());//税行科目
			ovo.setUpdateflag(DZFBoolean.FALSE);
			ovo.setPk_billcategory(svo.getPk_model_h());
			ovo.setPk_category_keyword(svo.getPk_category_keyword());
			ovo.setChildren((changeToOcrDetail((VATSaleInvoiceBVO2[])svo.getChildren(),svo.getPk_model_h(),ovo,svo)));;
			list.add(ovo);
		}
		
		return list;
	}
	/*
	 *  将销项子实体类转换成ocr子类
	 */
	private OcrInvoiceDetailVO[] changeToOcrDetail(VATSaleInvoiceBVO2[] sbArray,String pk_model_h,OcrInvoiceVO ovo,VATSaleInvoiceVO2 svo){
		List<OcrInvoiceDetailVO> list = new ArrayList<OcrInvoiceDetailVO>();
		if(sbArray!=null&&sbArray.length>0){
			for (int i = 0; i < sbArray.length; i++) {
				OcrInvoiceDetailVO odvo = new OcrInvoiceDetailVO();
				String randomUUID = UUID.randomUUID().toString();
				//回写分类时使用（相当于主键）
				sbArray[i].setTempvalue(randomUUID);
				odvo.setPk_invoice_detail(randomUUID);
				odvo.setRowno(sbArray[i].getRowno());//序号
				odvo.setInvname(sbArray[i].getBspmc());//商品名称
				odvo.setInvtype(sbArray[i].getInvspec());//规格
				odvo.setItemunit(sbArray[i].getMeasurename());//单位
				odvo.setItemamount(sbArray[i].getBnum()==null?null:sbArray[i].getBnum().toString());//数量
				odvo.setItemprice(sbArray[i].getBprice()==null?null:sbArray[i].getBprice().toString());//单价
				odvo.setItemmny(sbArray[i].getBhjje()==null?null:sbArray[i].getBhjje().toString());//金额
				odvo.setItemtaxrate(sbArray[i].getBspsl()==null?null:sbArray[i].getBspsl().toString());//税额
				odvo.setItemtaxmny(sbArray[i].getBspse()==null?null:sbArray[i].getBspse().toString());//税率
				odvo.setPk_billcategory(StringUtils.isEmpty(sbArray[i].getPk_billcategory())?pk_model_h:sbArray[i].getPk_billcategory());
				odvo.setPk_category_keyword(sbArray[i].getPk_category_keyword());
				list.add(odvo);
			}
		}else{
			OcrInvoiceDetailVO odvo = new OcrInvoiceDetailVO();
			String randomUUID = UUID.randomUUID().toString();
			//回写分类时使用（相当于主键）
			odvo.setInvname(svo.getSpmc());//商品名
			odvo.setPk_invoice_detail(randomUUID);
			odvo.setItemamount("1");//数量
			odvo.setItemprice(ovo.getNtotaltax());//单价
			odvo.setItemmny(ovo.getNmny());//金额
			odvo.setItemtaxmny(ovo.getNtaxnmny());//税额
			odvo.setItemtaxrate(svo.getSpsl()==null?null:svo.getSpsl().toString());
			odvo.setPk_billcategory(ovo.getPk_billcategory());
			odvo.setPk_category_keyword(ovo.getPk_category_keyword());
			list.add(odvo);
		}
		return list.toArray(new OcrInvoiceDetailVO[0]);
	}
	public List<VATSaleInvoiceVO2> changeToSale(List<VATSaleInvoiceVO2> sList,String pk_corp){

		CorpVO corpVO = corpService.queryByPk(pk_corp);
		List<OcrInvoiceVO> olist = changeToOcr(sList, pk_corp);
		if (olist != null&& olist.size() > 0) {	
			Map<String, List<OcrInvoiceVO>> map = DZfcommonTools.hashlizeObject(olist,
					new String[] {"pk_corp", "period"});//期间分组
			for (String key : map.keySet()) {
				ArrayList<String> pk_categoryList = new ArrayList<String>();
				boolean lock = false;
				try {
					if (StringUtils.isEmpty(key)) {
						continue;
					}
					lock = redissonDistributedLock.tryGetDistributedFairLock("zncsCategory_"+key.replace(",",""));
					if (lock) {
						schedulCategoryService.newSaveCorpCategory(map.get(key), corpVO.getPk_corp(), map.get(key).get(0).getPeriod(), corpVO);

						List<OcrInvoiceVO> ocrList = schedulCategoryService.updateInvCategory(map.get(key),
								corpVO.getPk_corp(), map.get(key).get(0).getPeriod(), corpVO);// 票据分类
						//查询类别全名称
						for (OcrInvoiceVO ocrInvoiceVO : ocrList) {
							pk_categoryList.add(ocrInvoiceVO.getPk_billcategory());
						}
						Map<String, String> fullNameMap = zncsVoucher.queryCategoryFullName(pk_categoryList, map.get(key).get(0).getPeriod(), pk_corp);
						for (OcrInvoiceVO ocrInvoiceVO : ocrList) {
							for (VATSaleInvoiceVO2 svo : sList) {
								if (ocrInvoiceVO.getPk_invoice().equals(svo.getTempvalue())) {
									svo.setPk_category_keyword(ocrInvoiceVO.getPk_category_keyword());// 关键字主键
									svo.setPk_model_h(ocrInvoiceVO.getPk_billcategory());// 类别主键
									svo.setBusitypetempname(fullNameMap.get(ocrInvoiceVO.getPk_billcategory()));// 类别名称
								}
								OcrInvoiceDetailVO[] odArray = (OcrInvoiceDetailVO[]) ocrInvoiceVO.getChildren();
								VATSaleInvoiceBVO2[] sbArray = (VATSaleInvoiceBVO2[]) svo.getChildren();
								if(odArray!=null&&odArray.length>0&&odArray!=null&&odArray.length>0){
									for (int i = 0; i < odArray.length; i++) {
										for (int j = 0; j < sbArray.length; j++) {
											if (!StringUtil.isEmpty(odArray[i].getPk_invoice_detail())&&odArray[i].getPk_invoice_detail().equals(sbArray[j].getTempvalue())) {
												sbArray[j].setPk_billcategory(odArray[i].getPk_billcategory());
												sbArray[j].setPk_category_keyword(odArray[i].getPk_category_keyword());
											}
										}
									}
								}
								// svo.setList(sbList);
							}
						}
					} else {
						continue;
					}
				} catch (Exception e) {
					log.error("分类任务异常", e);
				} finally {
					if (lock) {
						redissonDistributedLock.releaseDistributedFairLock("zncsCategory_"+key.replace(",",""));
					}
				}
			}
				
			
		}
		
		return sList;
	}
	@Override
	public List<BillCategoryVO> querySaleCategoryRef(String pk_corp, String period) throws DZFWarpException {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append("select distinct bi.pk_category as pk_category,bi.categoryname as categoryname,ca.settlement as szstylename,ca.zdyzy,ca.pk_accsubj,ca.pk_settlementaccsubj,bi.pk_basecategory as pk_basecategory,bi.categorycode "
				+ " from ynt_billcategory bi left join ynt_categoryset ca "
				+ " on bi.pk_category=ca.pk_category where nvl(ca.dr,0)=0 and  nvl(bi.dr,0)=0 and "
				+ " nvl(bi.isaccount,'N')='N' and bi.categorycode like '10%' and bi.categorycode !='10' and bi.categorycode != '1010' and bi.categorycode !='1011' "
				+ " and bi.categorytype != 4 and bi.pk_corp = ? and bi.period = ? order by bi.categorycode ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		List<BillCategoryVO> listVo = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), 
				sp, new BeanListProcessor(BillCategoryVO.class));
		return listVo;
	}
	@Override
	public void updateVO(String[] ids, String pk_model_h,String pk_corp,String pk_category_keyword,String busisztypecode,String rzkm,String jskm,String shkm) throws DZFWarpException {
		if (ids == null || ids.length == 0)
		{
			throw new BusinessException("请选择票据进行操作。");
		}
		List<String> pk_categoryList = new ArrayList<String>();
		SQLParameter params = new SQLParameter();
		String pkIns = SqlUtil.buildSqlForIn("pk_vatsaleinvoice", ids);
		VATSaleInvoiceVO2[] oldVOs = (VATSaleInvoiceVO2[])singleObjectBO.queryByCondition(VATSaleInvoiceVO2.class, pkIns + " and nvl(dr,0) = 0",  params);
		
		List<VATSaleInvoiceVO2> bList = new ArrayList<VATSaleInvoiceVO2>();
		
		String inPeriod = oldVOs[0].getInperiod();
		for (VATSaleInvoiceVO2 oldVO : oldVOs)
		{
			if(!StringUtil.isEmpty(oldVO.getPk_tzpz_h())){
				throw new BusinessException("号码为 ‘" + oldVO.getFp_hm() + "’ 的发票已经生成凭证，不能操作。");
			}
			if (inPeriod != null && inPeriod.equals(oldVO.getInperiod()) == false)
			{
				throw new BusinessException("所选票据属于不同入账期间，不能操作。");
			}
			//修改过分类，且有分类规则的，才进行学习
			if (!StringUtils.isEmpty(oldVO.getPk_category_keyword()) && !StringUtils.isEmpty(pk_model_h)
					&& !pk_model_h.equals(oldVO.getPk_model_h())) {// 有没有修改分类
				bList.add(oldVO);
			}
			if(!StringUtils.isEmpty(oldVO.getImgpath())){
				throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
			}
			
		}
//		List<VATSaleInvoiceBVO2> bvoList = queryBvoByPk(ids[i]);
//		ArrayList<String> bvopkList = new ArrayList<String>();
		//修改子表中和主表类型相同的业务类型
//		for (VATSaleInvoiceBVO2 bvo : bvoList) {
//			bvopkList.add(bvo.getPk_vatsaleinvoice_b());
//		}
		
		params = new SQLParameter();

		params.addParam(pk_model_h);
		params.addParam(pk_corp);
		params.addParam(oldVOs[0].getInperiod());
		
		StringBuffer updateDetailSql = new StringBuffer();
		updateDetailSql.append("update ynt_vatsaleinvoice_b b");
		updateDetailSql.append("  set pk_billcategory = ?");
		updateDetailSql.append("  where exists (select 1");
		updateDetailSql.append("     from ynt_vatsaleinvoice a");
		updateDetailSql.append("     where a.pk_corp= ? and a.inperiod= ? and a.pk_vatsaleinvoice = b.pk_vatsaleinvoice and ");
		updateDetailSql.append(SqlUtil.buildSqlForIn("a.pk_vatsaleinvoice", ids));
		updateDetailSql.append("         and (b.pk_billcategory = a.pk_model_h or b.pk_billcategory is null))");
		int icnt = singleObjectBO.executeUpdate(updateDetailSql.toString(), params);
		
		pk_categoryList.add(pk_model_h);
		//查询全名称
		Map<String, String> map = zncsVoucher.queryCategoryFullName(pk_categoryList,oldVOs[0].getInperiod(), pk_corp);
		String categoryfullname = map.get(pk_model_h);
		

		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append("update ynt_vatsaleinvoice set version=1.0, pk_model_h = ?,busitypetempname=?,settlement=?,pk_subject=?,pk_settlementaccsubj=?,pk_taxaccsubj=? where nvl(dr,0)=0 and ");
		sb.append(SqlUtil.buildSqlForIn("pk_vatsaleinvoice", ids));
		sp.addParam(pk_model_h);
		sp.addParam(categoryfullname);
		sp.addParam(busisztypecode);
		sp.addParam(rzkm);
		sp.addParam(jskm);
		sp.addParam(shkm);
//		sp.addParam(ids[i]);
		icnt = singleObjectBO.executeUpdate(sb.toString(), sp);
//		if(bvopkList!=null&&bvopkList.size()>0){
//			StringBuffer bsb = new StringBuffer();
//			SQLParameter bsp = new SQLParameter();
//			bsb.append("update ynt_vatsaleinvoice_b set pk_billcategory = ? where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_vatsaleinvoice_b", bvopkList.toArray(new String[0])));
//			bsp.addParam(pk_model_h);
//			singleObjectBO.executeUpdate(bsb.toString(), bsp);
//		}
		//自学习
		//查询修改前的分类主键
		
//		bList.add(oldVO);
		
		if (bList.size() > 0)
		{
			List<OcrInvoiceVO> OcrInvoiceVOList = changeToOcr(bList, pk_corp);
			for (OcrInvoiceVO ocrInvoiceVO : OcrInvoiceVOList) {
				ocrInvoiceVO.setPk_invoice(null);
			}
			iBillcategory.saveNewCategroy(OcrInvoiceVOList.toArray(new OcrInvoiceVO[0]),
					pk_model_h, pk_corp, OcrInvoiceVOList.get(0).getPeriod());
	
		}
		
	}
	private List<VATSaleInvoiceBVO2> queryBvoByPk(String pk_vatsaleinvoice) throws DZFWarpException{
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sb.append(" select * from ynt_vatsaleinvoice_b where nvl(dr,0)=0 and pk_vatsaleinvoice=?");
		sp.addParam(pk_vatsaleinvoice);
		List<VATSaleInvoiceBVO2> list = (List<VATSaleInvoiceBVO2>) singleObjectBO.executeQuery(sb.toString(), 
				sp, new BeanListProcessor(VATSaleInvoiceBVO2.class));
		return list;
	}
	/*
	 * 检查进项中是否包含存货
	 * 
	 */
	@Override
	public String checkNoStock(List<VATSaleInvoiceVO2> list,String pk_corp) throws DZFWarpException {
		
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFBoolean icinv = new DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));
		String mesg="请走存货匹配流程";
		if(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic())){//开启总账存货的时候
			InventorySetVO inventoryvo = gl_ic_invtorysetserv.query(corpvo.getPk_corp());
			int chcbjzfs = InventoryConstant.IC_NO_MXHS;
			if(inventoryvo!=null ){
				if(inventoryvo != null){
					chcbjzfs = inventoryvo.getChcbjzfs();
				}
				mesg = "包含需匹配存货票据,请点击[存货]进行存货匹配及入账处理";
				if(chcbjzfs == InventoryConstant.IC_NO_MXHS ){//不启用核算存货的时候不走
					return "";
				}
			}else{
				return "启用总账核算存货，请先设置存货成本核算方式！";
			}
		}else if(IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
			mesg = "包含需匹配存货票据,请点击[生成出库]进行存货匹配及入账处理";
		}else return "";
		
		Set<String> bpkSet = new HashSet<String>();
		
		
		StringBuffer msg = new StringBuffer();
		boolean flag=false;

		
		for (VATSaleInvoiceVO2 vo : list) {
			
			VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[])vo.getChildren();
			if(bvos!=null&&bvos.length>0){
				for (int i = 0; i < bvos.length; i++) {
					if(!StringUtils.isEmpty(bvos[i].getPk_billcategory())){
						bpkSet.add(bvos[i].getPk_billcategory());
					}
					
				}
			}
		}
		
		if (bpkSet != null && bpkSet.size() > 0) {
			SQLParameter sp = new SQLParameter();
			StringBuffer sb = new StringBuffer();
			sb.append(" select * from ynt_billcategory where nvl(dr,0)=0 and "
					+ SqlUtil.buildSqlForIn("pk_category", bpkSet.toArray(new String[0])));
			List<BillCategoryVO> billList = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(BillCategoryVO.class));
			if (billList != null && billList.size() > 0) {
				for (BillCategoryVO billCategoryVO : billList) {
					if (billCategoryVO.getCategorycode().startsWith("11") // 库存采购
							|| billCategoryVO.getCategorycode().startsWith("101110")// 销售材料收入
							|| billCategoryVO.getCategorycode().startsWith("101015")) {// 商品销售收入
						msg.append("<font color='red'><p>销项发票内包含的业务类型 [" + billCategoryVO.getCategoryname()
								+ "], " + mesg + "</p></font>");
					}
				}
			}
		}

		
		return msg.toString();
	}
	@Override
	public boolean checkIsStock(VATSaleInvoiceVO2 vo) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		StringBuffer msg = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		Set<String> bpkSet = new HashSet<String>();
		//for (VATSaleInvoiceVO2 vo : list) {
			boolean flag=false;
			VATSaleInvoiceBVO2[] bvos = (VATSaleInvoiceBVO2[])vo.getChildren();
			if(bvos!=null&&bvos.length>0){
				for (int i = 0; i < bvos.length; i++) {
					bpkSet.add(bvos[i].getPk_billcategory());
				}
			}
			sb.append(" select * from ynt_billcategory where nvl(dr,0)=0 and "+SqlUtil.buildSqlForIn("pk_category", bpkSet.toArray(new String[0])));
			List<BillCategoryVO> billList = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,
					new BeanListProcessor(BillCategoryVO.class));
			if(billList!=null&&billList.size()>0){
				for (BillCategoryVO billCategoryVO : billList) {
					if (billCategoryVO.getCategorycode().startsWith("11") //库存采购
							|| billCategoryVO.getCategorycode().startsWith("101110")//销售材料收入
							|| billCategoryVO.getCategorycode().startsWith("101015")){//商品销售收入
						return true;
					}
					
				}
				
			}
			
		//}
		
		 return false;
	}
	
	@Override
	public void checkvoPzMsg(String pk_id) throws DZFWarpException {
		VATSaleInvoiceVO2 vo = queryByID(pk_id);
		if(vo==null||!StringUtils.isEmpty(vo.getImgpath())){
			throw new BusinessException("智能识别票据，请至票据工作台进行相关处理！");
		}
		
	}
	
	@Override
	public IntradeHVO createIH(VATSaleInvoiceVO2 oldVO, CorpVO corpvo, String userid,Map<String, List<AuxiliaryAccountBVO>> fzhsBodyMap)
			throws DZFWarpException {
		if(!IcCostStyle.IC_ON.equals(corpvo.getBbuildic())){
			return null;
		}
		VATSaleInvoiceVO2 vo=(VATSaleInvoiceVO2)OcrUtil.clonAll(oldVO);
		String pk_corp = corpvo.getPk_corp();
		String pk_ictrade_h = vo.getPk_ictrade_h();
		if(!StringUtil.isEmpty(pk_ictrade_h)){
			return null;
		}

		String pk_model_h = vo.getPk_model_h();
		if (StringUtil.isEmpty(pk_model_h)) {
			throw new BusinessException("业务类型不能为空");
		}
		Map<String, BillCategoryVO> falseMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corpvo.getPk_corp(), vo.getInperiod(),"N");
		Map<String, BillCategoryVO> trueMap=iZncsNewTransService.queryCategoryVOs_IsAccount(corpvo.getPk_corp(), vo.getInperiod(),"Y");
		trueMap= iZncsNewTransService.newInsertCategoryVOs(falseMap, trueMap,corpvo.getPk_corp()+vo.getInperiod());
		turnBillCategoryMap(falseMap, trueMap);
		//过滤掉不需要生成出入库单的行
		
		vo=filterBodyVOs(vo, falseMap);
		if(vo.getChildren().length==0){
			return null;
		}
		Map<String, CategorysetVO> categorysetMap=queryCategorysetVO(vo,corpvo.getPk_corp());
		
		IntradeHVO ichvo = buildIctrade(vo, userid, categorysetMap, trueMap, falseMap,fzhsBodyMap);

		ic_saleoutserv.saveSale(ichvo, false, false);// 保存
		// 更新状态
		updateICStatus(vo.getPk_vatsaleinvoice(), corpvo.getPk_corp(), ichvo.getPk_ictrade_h());

		return ichvo;
	
	}
}

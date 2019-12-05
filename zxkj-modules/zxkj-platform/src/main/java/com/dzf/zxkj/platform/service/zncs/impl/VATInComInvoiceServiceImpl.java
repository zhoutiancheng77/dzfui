package com.dzf.zxkj.platform.service.zncs.impl;

import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.util.SQLHelper;
import com.dzf.zxkj.base.utils.DZFValueCheck;
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
import com.dzf.zxkj.platform.controller.sys.DcPzmbController;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.BdCurrencyVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
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
import com.dzf.zxkj.platform.model.tax.TaxitemVO;
import com.dzf.zxkj.platform.model.zncs.VATInComInvoiceBVO;
import com.dzf.zxkj.platform.model.zncs.VatImpConfSetVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccAliasService;
import com.dzf.zxkj.platform.service.glic.IInventoryAccSetService;
import com.dzf.zxkj.platform.service.glic.impl.CheckInventorySet;
import com.dzf.zxkj.platform.service.icbill.IPurchInService;
import com.dzf.zxkj.platform.service.icset.IInvAccSetService;
import com.dzf.zxkj.platform.service.icset.IInventoryService;
import com.dzf.zxkj.platform.service.icset.IMeasureService;
import com.dzf.zxkj.platform.service.jzcl.ICbComconstant;
import com.dzf.zxkj.platform.service.jzcl.IQmclService;
import com.dzf.zxkj.platform.service.jzcl.IQmgzService;
import com.dzf.zxkj.platform.service.pjgl.IImageGroupService;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.report.impl.YntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.tax.ITaxitemsetService;
import com.dzf.zxkj.platform.service.zncs.IAutoMatchName;
import com.dzf.zxkj.platform.service.zncs.IBankStatementService;
import com.dzf.zxkj.platform.service.zncs.IPiaoTongJinXiangService;
import com.dzf.zxkj.platform.service.zncs.IVATInComInvoiceService;
import com.dzf.zxkj.platform.service.zncs.image.IImageToVoucherExtendService;
import com.dzf.zxkj.platform.service.zncs.image.IImageToVoucherService;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import com.dzf.zxkj.platform.util.zncs.PjTypeEnum;
import com.dzf.zxkj.platform.util.zncs.VatUtil;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service("gl_vatincinvact")
public class VATInComInvoiceServiceImpl implements IVATInComInvoiceService {

	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private YntBoPubUtil yntBoPubUtil;
	@Autowired
	private IVoucherService voucher;
	// @Autowired
	// private ICpaccountService cpaccountService;
	@Autowired
	private IBankStatementService gl_yhdzdserv;
	@Autowired
	private IDcpzService dcpzjmbserv;
	@Autowired
	private IPiaoTongJinXiangService piaotongjxserv;
	@Autowired
	private ITaxitemsetService sys_taxsetserv;
	@Autowired
	private IInventoryService invservice;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	private IPurchInService ic_purchinserv;
	@Autowired
	private IAutoMatchName ocr_atuomatch;
	@Autowired
	private IMeasureService measervice;
	@Autowired
	@Qualifier("aitovoucherserv_vatinvoice2")
	private IImageToVoucherService aitovoucherserv_vatinvoice;
	@Autowired
	private ICbComconstant gl_cbconstant;
	@Autowired
	@Qualifier("aitovoucherserv_vatinvoice2")
	private IImageToVoucherExtendService aitovoucherserv_extend;
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
	private IBDCorpTaxService sys_corp_tax_serv;
	@Autowired
	private IImageGroupService img_groupserv;
	@Autowired
	private IQmclService gl_qmclserv;
	@Autowired
	private IInventoryAccSetService gl_ic_invtorysetserv;
    @Autowired
    private IAccountService accountService;
    @Autowired
    private ICorpService corpService;

	private static final String PASSRULT_DEFAULT = "通过";// 自定义、中兴通模板
	private static final String PASSRULT_CAISHUI = "正常";// 财税助手验证结果
	private static final String PASSRULT_CF = "重复";// 财税助手重复验证
	private static final String PASSRULT_HG = "不合规";// 财税助手
	private static final String FILTER_PLACE_HOLDER = "#$#";

	private static final String SMALL_TAX = "小规模纳税人";

	private static final Integer ifptype = 1;//// 0 销项 1进项

	// private Map<Integer, Object[][]> STYLE = null;
	private Map<Integer, String[][]> BSTYLE = null;

	// private static int[][] fp_dm_arr = {//匹配发票代码
	// {0, 1, 0},//前两个格子是坐标，后一个格子是文件来源
	// {1, 2, 3},
	// {2, 6, 2},
	// {0, 9, 4}
	// };

//	private static String STR_FP_DM = "发票代码";
	private static String VAT_SPECIAL_ZHUAN = "增值税专用发票";
	private static String VAT_SPECIAL_PU = "普通发票";

	@Override
	public List<VATInComInvoiceVO> quyerByPkcorp(InvoiceParamVO paramvo, String sort, String order)
			throws DZFWarpException {
		// public List<VATInComInvoiceVO> quyerByPkcorp(String pk_corp,
		// VATInComInvoiceVO vo, String sort,String order) throws
		// DZFWarpException{
		// try {
		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append(
				"  select y.pk_vatincominvoice, y.coperatorid, y.doperatedate, y.pk_corp, y.batchflag, y.iszhuan, y.fp_hm, y.fp_dm, y.xhfmc, y.spmc, ");
		sb.append(
				" y.spsl, y.spse, y.hjje, y.jshj, y.yfp_hm, y.yfp_dm, y.kprj, y.rzrj, y.rzjg, y.pk_tzpz_h, y.period, y.billstatus, y.modifyoperid, ic.dbillid, ic.pk_ictrade_h, ");
		sb.append(
				" y.modifydatetime, d.pk_model_h, d.busitypetempname, y.sourcetype, y.dr, y.ts, h.pzh, y.imgpath, y.kplx, y.pk_image_group,y.inperiod,y.ioperatetype,y.isettleway ,h.vicbillcode vicbillno");//y.isic,
		sb.append("    from ynt_vatincominvoice y ");
		sb.append("   left join ynt_dcmodel_h d ");
		sb.append("     on y.pk_model_h = d.pk_model_h ");
		sb.append("   left join ynt_ictrade_h ic ");
		sb.append("     on y.pk_ictrade_h = ic.pk_ictrade_h ");
		sb.append("    left join ynt_tzpz_h h ");
		sb.append("      on y.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append(" where y.pk_corp=? and nvl(y.version, 0) = 0 and nvl(y.dr,0)=0");
		sp.addParam(paramvo.getPk_corp());

		if (!StringUtil.isEmpty(paramvo.getIszh())) {
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

		if ("serDay".equals(paramvo.getSerdate())) {
			if (paramvo.getBegindate() != null) {
				sb.append(" and rzrj >= ? ");
				sp.addParam(paramvo.getBegindate());
			}

			if (paramvo.getEnddate() != null) {
				sb.append(" and rzrj <= ? ");
				sp.addParam(paramvo.getEnddate());
			}

			if ("id".equals(sort)) {
				sort = "srzrj";// 认证日期
			}

		} else if ("serDay1".equals(paramvo.getSerdate())) {
			if (paramvo.getBegindate2() != null) {
				sb.append(" and kprj >= ? ");
				sp.addParam(paramvo.getBegindate2());
			}

			if (paramvo.getEnddate2() != null) {
				sb.append(" and kprj <= ? ");
				sp.addParam(paramvo.getEnddate2());
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
		// else{
		// sb.append(" 1 != 1 ");
		// }

		if (paramvo.getIoperatetype() != null) {// 操作类型
			sb.append(" and y.ioperatetype = ? ");
			sp.addParam(paramvo.getIoperatetype());
		}

		if (!StringUtil.isEmpty(paramvo.getFphm())) {
			sb.append(" and fp_hm like ").append("'%").append(paramvo.getFphm()).append("%'");
		}

		if (!StringUtil.isEmpty(paramvo.getFpdm())) {
			sb.append(" and fp_dm like ").append("'%").append(paramvo.getFpdm()).append("%'");
		}

		if (!StringUtil.isEmpty(sort)) {// sort != null && !"".equals(sort)
			String sortb = FieldMapping.getFieldNameByAlias(new VATInComInvoiceVO(), sort);
			order = " order by " + (sortb == null ? sort : sortb) + " " + order + ", y.rowid " + order;
			sb.append(order);
		}
		List<VATInComInvoiceVO> listVo = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(VATInComInvoiceVO.class));
		return listVo;

	}

	public VATInComInvoiceVO queryByPrimaryKey(String PrimaryKey) throws DZFWarpException {
		SuperVO vo = singleObjectBO.queryByPrimaryKey(VATInComInvoiceVO.class, PrimaryKey);
		return (VATInComInvoiceVO) vo;
	}

	@Override
	public void delete(VATInComInvoiceVO vo, String pk_corp) throws DZFWarpException {
		// 删除前数据安全验证
		VATInComInvoiceVO getmsvo = queryByPrimaryKey(vo.getPrimaryKey());
		if (getmsvo == null || !getmsvo.getPk_corp().equals(getmsvo.getPk_corp()) || vo.getPk_corp() == null
				|| !pk_corp.equals(vo.getPk_corp())) {
			throw new BusinessException("正在处理中，请刷新重试！");
		}

//		DZFBoolean isic = vo.getIsic();
//		if (isic != null && isic.booleanValue()) {
//			throw new BusinessException("单据已生成入库单，请检查");
//		}
		String pk_ictrade_h = vo.getPk_ictrade_h();
		if(!StringUtil.isEmpty(pk_ictrade_h)){
			throw new BusinessException("单据已生成入库单，请检查");
		}

		if (!StringUtil.isEmpty(vo.getPzh()) || !StringUtil.isEmpty(vo.getPk_tzpz_h()))
			throw new BusinessException("单据已生成凭证，请检查。");

		singleObjectBO.deleteObjectByID(vo.getPrimaryKey(),
				new Class[] { VATInComInvoiceVO.class, VATInComInvoiceBVO.class });

		StringBuffer strb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state0);
		sp.addParam(getmsvo.getSourcebillid());
		strb.append(" update  ynt_image_group p  set istate=? where p.pk_image_group in ");
		strb.append(" (select pk_image_group from  ynt_image_library y where y.pk_image_library =? and nvl(dr,0)=0 ) ");
		singleObjectBO.executeUpdate(strb.toString(), sp);

		deleteRelationVO(getmsvo);
	}

	private void deleteRelationVO(VATInComInvoiceVO vo) {
		if ((ICaiFangTongConstant.LYDJLX_PT.equals(vo.getSourcebilltype()))
				&& !StringUtil.isEmpty(vo.getSourcebillid())) {
			singleObjectBO.deleteObjectByID(vo.getSourcebillid(),
					new Class[] { CaiFangTongHVO.class, CaiFangTongBVO.class });// 财房通主子表dr=1
		}
	}

	/**
	 * 废弃不用
	 */
	// public void updateVOArr(DZFBoolean isAddNew, String pk_corp, String
	// cuserid, String sort, String order,
	// List<VATInComInvoiceVO> list) throws DZFWarpException {
	// List<VATInComInvoiceVO> listNew = new ArrayList<VATInComInvoiceVO>();
	// InvoiceParamVO paramvo = new InvoiceParamVO();
	// paramvo.setPk_corp(pk_corp);
	// List<VATInComInvoiceVO> listAll = quyerByPkcorp(paramvo, sort, order);
	// String k = null;
	// VATInComInvoiceVO voTemp = null;
	// if(list == null || list.size()==0)
	// return;
	//
	// for(VATInComInvoiceVO vo : list){
	// k = vo.getPrimaryKey();
	// if(StringUtil.isEmpty(k)){
	// vo.setPk_corp(pk_corp);
	// vo.setCoperatorid(cuserid);
	// vo.setDoperatedate(new DZFDate(new Date()));
	// listNew.add(vo);
	// listAll.add(vo);
	// }else if(!isAddNew.booleanValue()){
	// voTemp = null;
	// for(VATInComInvoiceVO msvo:listAll){
	// if(msvo.getPrimaryKey().equals(k)){
	// voTemp = msvo;
	// break;
	// }
	// }
	// if(voTemp != null){
	// listAll.remove(voTemp);
	// listAll.add(vo);
	// }
	// }
	// }
	//
	// if (listNew != null && listNew.size() > 0){
	// VATInComInvoiceVO[] vos = listNew.toArray(new VATInComInvoiceVO[0]);
	// for(VATInComInvoiceVO msvo:vos){
	// if(msvo == null){
	// throw new BusinessException("数据为空不能添加");
	// }
	//// msvo.setPk_corp(pk_corp);
	//// msvo.setCreatetime(new DZFDateTime(new Date()));
	//// msvo.setCreator(cuserid);
	// }
	// singleObjectBO.insertVOArr(vos[0].getPk_corp(), vos);
	// }else if (list != null && list.size() > 0) {
	// VATInComInvoiceVO[] vos = list.toArray(new VATInComInvoiceVO[0]);
	// singleObjectBO.updateAry(vos);
	// }
	//
	// }

	@Override
	public VATInComInvoiceVO[] updateVOArr(String pk_corp, Map<String, VATInComInvoiceVO[]> sendData)
			throws DZFWarpException {

		VATInComInvoiceVO[] rtvos = null;

		VATInComInvoiceVO[] addvos = sendData.get("adddocvos");

		if (addvos != null && addvos.length > 0) {
			// 如是新增， 需返回新增vos
			String[] addpks = null;
			SuperVO[] bvos = null;

			resetSpsl(addvos);

			List<SuperVO> bvoList = new ArrayList<SuperVO>();
			addpks = singleObjectBO.insertVOArr(pk_corp, addvos);
			for (int i = 0; i < addvos.length; i++) {
				bvos = addvos[i].getChildren();
				if (bvos != null && bvos.length > 0) {
					for (SuperVO bvo : bvos) {
						bvo.setAttributeValue(bvo.getParentPKFieldName(), addpks[i]);
						bvoList.add(bvo);
					}
				}
			}

			if (bvoList.size() > 0) {
				singleObjectBO.insertVOArr(pk_corp, bvoList.toArray(new VATInComInvoiceBVO[0]));
			}

			rtvos = addvos;
		}

		VATInComInvoiceVO[] updvos = sendData.get("upddocvos");

		if (updvos != null && updvos.length > 0) {

			resetSpsl(updvos);

			String pk = null;
			VATInComInvoiceVO oldvo = null;
			SuperVO[] oldbvos = null;
			SuperVO[] newbvos = null;
			for (VATInComInvoiceVO vo : updvos) {
				pk = vo.getPrimaryKey();

				if (!StringUtil.isEmpty(pk)) {
					oldvo = queryByID(pk);
					oldbvos = oldvo.getChildren();
					if (oldbvos != null && oldbvos.length > 0) {
						singleObjectBO.deleteVOArray(oldbvos);
					}

					// 赋值
					vo.setPk_corp(oldvo.getPk_corp());
				}

				newbvos = vo.getChildren();
				for (SuperVO bvo : newbvos) {
					bvo.setAttributeValue(bvo.getParentPKFieldName(), pk);
				}

				if (vo.getSourcetype() == IBillManageConstants.OCR) {
					singleObjectBO.update(vo,
							new String[] { "iszhuan", "fp_hm", "fp_dm", "xhfmc", "spmc", "spsl", "spse", "hjje", "jshj",
									"kprj", "period", "rzjg", "rzrj", "modifyoperid", "modifydatetime", "demo",
									"pk_model_h", "busitypetempname", "xhfsbh", "xhfdzdh", "xhfyhzh", "ghfmc", "ghfsbh",
									"ghfdzdh", "ghfyhzh"

							});
				} else {
					singleObjectBO.update(vo,
							new String[] { "iszhuan", "fp_hm", "fp_dm", "xhfmc", "spmc", "spsl", "spse", "hjje", "jshj",
									"kprj", "inperiod", "period", "rzjg", "rzrj", "modifyoperid", "modifydatetime",
									"demo", "pk_model_h", "busitypetempname", "xhfsbh", "xhfdzdh", "xhfyhzh", "ghfmc",
									"ghfsbh", "ghfdzdh", "ghfyhzh"

							});
				}

				singleObjectBO.insertVOArr(vo.getPk_corp(), newbvos);
			}

			rtvos = updvos;
		}

		checkIsHasRepeation(pk_corp);

		return addvos;
	}

	//重新设置税率
	private void resetSpsl(VATInComInvoiceVO[] vos){
		if(vos == null || vos.length == 0)
			return;

		VATInComInvoiceBVO[] bvos;
		for(VATInComInvoiceVO vo : vos){
			bvos = (VATInComInvoiceBVO[]) vo.getChildren();
			if(bvos != null && bvos.length > 0){
				vo.setSpsl(bvos[0].getBspsl());//设置税率
			}
		}
	}

	/**
	 * 校验数据是否重复
	 *
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void checkIsHasRepeation(String pk_corp) throws DZFWarpException {
		StringBuffer sf = new StringBuffer();
		sf.append(" Select fp_hm ");
		sf.append("   From ynt_vatincominvoice y ");
		sf.append("  Where y.pk_corp = ? and nvl(y.dr,0) = 0  ");
		sf.append("  group by y.fp_hm ");
		sf.append(" having count(1) > 1  ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);

		List<VATInComInvoiceVO> list = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATInComInvoiceVO.class));

		if (list != null && list.size() > 0) {
			sf = new StringBuffer();
			sf.append("<p>发票号码:");
			for (VATInComInvoiceVO vo : list) {
				sf.append(vo.getFp_hm());
				sf.append("  ");
			}
			sf.append("重复，请检查！</p>");

			throw new BusinessException(sf.toString());
		}
	}

	// private static String ISTEMP = "ISTEMP";//暂存标识
	@Override
	public void createPZ(VATInComInvoiceVO vo, String pk_corp, String userid, Map<String, DcModelHVO> map,
                         VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

		List<VATInComInvoiceVO> ll = new ArrayList<VATInComInvoiceVO>();
		ll.add(vo);
		checkisGroup(ll, pk_corp);//校验

		// 首先先判断pk_model_h是否存在
		List<VATInComInvoiceVO> vatModelList = new ArrayList<VATInComInvoiceVO>();
		if (StringUtil.isEmpty(vo.getPk_model_h())) {
			setModelValue(vo, corpvo, vatModelList);
			if (StringUtil.isEmpty(vo.getPk_model_h())) {
				DcModelHVO defaultModel = getDefaultModelHVO(map, vo, corpvo);
				if (defaultModel != null) {
					vo.setPk_model_h(defaultModel.getPk_model_h());
					vo.setBusitypetempname(defaultModel.getBusitypetempname());
					vatModelList.add(vo);
					isT = true;
				}
			}
		}

		DcModelHVO mHVO = null;
		mHVO = map.get(vo.getPk_model_h());
		if (mHVO == null)
			throw new BusinessException("进项发票:未能找到对应的业务类型模板，请检查");

		DcModelBVO[] models = mHVO.getChildren();

		if (models != null && models.length > 0) {
			// 总账核算存货
			DZFBoolean icinv = new DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));// 启用总账库存
			if (icinv != null && icinv.booleanValue()) {
				for (DcModelBVO model : models) {
					String kcsp = gl_cbconstant.getKcsp_code();
					String ycl = gl_cbconstant.getYcl_code();

					String kmbm = kcsp;
					kmbm = model.getKmbm();
					if (!StringUtil.isEmpty(kmbm)) {
						if (kmbm.startsWith(kcsp) || kmbm.startsWith(ycl)) {
							throw new BusinessException("模板中包含1405或1403相关科目，请检查");
						}
					}
				}
			}
		}
		// 1-----普票 2----专票 3--- 未开票
		int fp_style =getFpStyle(vo);
		OcrImageLibraryVO lib = changeOcrInvoiceVO(vo, pk_corp, ifptype, fp_style);
		OcrInvoiceDetailVO[] detailvos = changeOcrInvoiceDetailVO(vo, pk_corp);
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		headVO.setFp_style(fp_style);

		aitovoucherserv_vatinvoice.getTzpzBVOList(headVO, models, lib, detailvos, tblist, accounts);
		aitovoucherserv_vatinvoice.setSpeaclTzpzBVO1(headVO, lib, tblist);

		//根据规则设置摘要
		setPzZy(tblist, setvo, vo);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		DZFDouble dfmny = DZFDouble.ZERO_DBL;

		for (TzpzBVO bvo : tblist) {
			dfmny = SafeCompute.add(dfmny, bvo.getDfmny());
		}

		vo.setCount(1);
		createTzpzHVO(headVO, vo, pk_corp, userid, null, null, dfmny, accway);
		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));
		headVO.setPk_image_group(vo.getPk_image_group());
		updateImageGroup(vo.getPk_image_group());
		if (isT) {
			createTempPZ(headVO, new VATInComInvoiceVO[] { vo }, pk_corp);
		} else {
			headVO = voucher.saveVoucher(corpvo, headVO);
		}

		if (lwflag != null && lwflag.booleanValue()) {
			List<VATInComInvoiceVO> list = new ArrayList<VATInComInvoiceVO>();
			list.add(vo);
			updateOtherType(list);// 其他
		}
		// 更新业务类型标识
		if (vatModelList != null && vatModelList.size() > 0) {
			singleObjectBO.updateAry(vatModelList.toArray(new VATInComInvoiceVO[0]),
					new String[] { "pk_model_h", "busitypetempname" });
		}
	}

	private DcModelHVO getDefaultModelHVO(Map<String, DcModelHVO> map, VATInComInvoiceVO vo, CorpVO corpvo) {
		if (map == null || map.size() == 0)
			return null;

		String charname = corpvo.getChargedeptname();
		DZFBoolean iszhuan = vo.getIszhuan();
		String vscode = iszhuan!=null && iszhuan.booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
//		String vscode = FieldConstant.FPSTYLE_02;
		String szcode = FieldConstant.SZSTYLE_06;// 其他支出
		String businame = "办公费";
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
	}

	private void setModelValue(VATInComInvoiceVO vo, CorpVO corpvo, List<VATInComInvoiceVO> vatModelList) {
		DZFBoolean iszh = vo.getIszhuan();
		iszh = iszh != null && iszh.booleanValue() ? iszh : DZFBoolean.FALSE;

		DcModelHVO modelvo = scanMatchBusiName2(vo, corpvo, iszh, corpvo.getChargedeptname());
		if (modelvo != null) {
			vo.setPk_model_h(modelvo.getPk_model_h());
			vo.setBusitypetempname(modelvo.getBusitypetempname());
			vatModelList.add(vo);
		}
	}

	private OcrImageLibraryVO changeOcrInvoiceVO(VATInComInvoiceVO vo, String pk_corp, int imagetype, int ifpkind) {

		OcrImageLibraryVO invvo = new OcrImageLibraryVO();
		invvo.setVinvoicecode(vo.getFp_dm());
		invvo.setVinvoiceno(vo.getFp_hm());
		invvo.setDinvoicedate(vo.getKprj().toString());
		invvo.setInvoicetype(ifpkind == 1 ? "04" : "01");
		invvo.setItype(imagetype);
		invvo.setNmny(getDefaultMny(vo.getHjje()));
		invvo.setNtaxnmny(getDefaultMny(vo.getSpse()));
		invvo.setNtotaltax(getDefaultMny(vo.getJshj()));
		invvo.setVpurchname(vo.getGhfmc());
		invvo.setVpurchtaxno(vo.getGhfsbh());
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

	private String getDefaultMny(DZFDouble mny) {

		return mny != null ? mny.toString() : DZFDouble.ZERO_DBL.toString();
	}

	private OcrInvoiceDetailVO[] changeOcrInvoiceDetailVO(VATInComInvoiceVO vo, String pk_corp) {

		VATInComInvoiceBVO[] bvos = (VATInComInvoiceBVO[]) vo.getChildren();

		int len = bvos == null ? 0 : bvos.length;

		List<OcrInvoiceDetailVO> detailList = new ArrayList<OcrInvoiceDetailVO>();

		OcrInvoiceDetailVO detailvo = null;
		VATInComInvoiceBVO bvo = null;
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

			detailList.add(detailvo);
		}

		return detailList.toArray(new OcrInvoiceDetailVO[0]);
	}

	/**
	 * 生成暂存态凭证
	 *
	 * @param headVO
	 * @param
	 * @param pk_corp
	 */
	private void createTempPZ(TzpzHVO headVO, VATInComInvoiceVO[] vos, String pk_corp) {
		gl_yhdzdserv.checkCreatePZ(pk_corp, headVO);
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
		headVO = (TzpzHVO) singleObjectBO.saveObject(headVO.getPk_corp(), headVO);

		//更新税目
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		voucher.saveTaxItem(headVO, corpvo);

		// 暂存态回写
		for (VATInComInvoiceVO vo : vos) {
			vo.setPk_tzpz_h(headVO.getPrimaryKey());
			vo.setPzh(headVO.getPzh());
		}

		singleObjectBO.updateAry(vos, new String[] { "pk_tzpz_h", "pzh" });
		//暂存态回写业务类型模板
		singleObjectBO.updateAry(vos,new String[] { "pk_model_h", "busitypetempname" });
	}

	private TzpzHVO createTzpzHVO(TzpzHVO headVO, VATInComInvoiceVO vo, String pk_corp, String userid, String pk_curr,
			Map<String, YntCpaccountVO> ccountMap, DZFDouble mny, boolean accway) {
		headVO.setPk_corp(pk_corp);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setJfmny(mny);
		headVO.setDfmny(mny);
		headVO.setCoperatorid(userid);
		headVO.setIshasjz(DZFBoolean.FALSE);

		String period = null;
		DZFDate voucherDate = null;
		if (vo.getRzjg() != null && vo.getRzjg() == 1 && vo.getRzrj() != null) {

			period = DateUtils.getPeriod(vo.getRzrj());
			voucherDate = vo.getRzrj();
		} else {
			voucherDate = vo.getKprj();
			period = DateUtils.getPeriod(vo.getKprj());
		}

		if (accway) {
			voucherDate = DateUtils.getPeriodEndDate(period);
		}

		if (StringUtil.isEmpty(vo.getInperiod())) {
			headVO.setDoperatedate(voucherDate);
		} else {
			period = vo.getInperiod();
			headVO.setDoperatedate(DateUtils.getPeriodEndDate(vo.getInperiod()));
		}

		// headVO.setDoperatedate(vo.getRzrj() == null ? new DZFDate() :
		// vo.getRzrj());
		// headVO.setPzh(yntBoPubUtil.getNewVoucherNo(pk_corp,
		// vo.getTradingdate()));
		headVO.setVbillstatus(IVoucherConstants.FREE);// 默认自由态
		// 记录单据来源
		headVO.setSourcebillid(vo.getPrimaryKey());
		headVO.setSourcebilltype(IBillTypeCode.HP95);

		headVO.setFp_style(getFpStyle(vo));// 1/2/3 普票/专票/未开票 空：不处理改字段

		headVO.setPeriod(period);
		headVO.setVyear(Integer.valueOf(period.substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);

		int count = vo.getCount();
		if(count == 0){
			count = 1;
		}

		headVO.setNbills(count);//

		headVO.setMemo(null);

		headVO.setIsqxsy(DZFBoolean.TRUE);//不校验期间损益是否结转

		return headVO;
	}

	// private List<TzpzBVO> createTzpzBVO(VATInComInvoiceVO vo,
	// DcModelHVO mHVO,
	// String pk_curr,
	// Map<String, YntCpaccountVO> ccountMap,
	// Map<String, Boolean> isTempMap,
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
	//// boolean isleaf = false;
	//// boolean iskhfz = false;
	//// YntCpaccountVO[] accounts = null;
	// int rowno = 1;//分录行排序
	// for(DcModelBVO bvo : mBVOs){
	//
	// tzpzbvo = new TzpzBVO();
	//
	// cvo = ccountMap.get(bvo.getPk_accsubj());
	//
	// isTemp = isTempMap.get(ISTEMP);
	// if(isTemp || StringUtil.isEmpty(vo.getXhfmc())){
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

	private void setTzpzBVOValue(TzpzBVO tzpzbvo, String pk_curr, YntCpaccountVO cvo, DcModelBVO bvo,
			VATInComInvoiceVO vo) {
		tzpzbvo.setPk_currency(pk_curr);
		tzpzbvo.setPk_accsubj(cvo.getPk_corp_account());
		tzpzbvo.setVcode(cvo.getAccountcode());
		tzpzbvo.setVname(cvo.getAccountname());

		tzpzbvo.setKmmchie(cvo.getFullname());
		tzpzbvo.setSubj_code(cvo.getAccountcode());
		tzpzbvo.setSubj_name(cvo.getAccountname());

		tzpzbvo.setZy(bvo.getZy());
		tzpzbvo.setNrate(DZFDouble.ONE_DBL);
		tzpzbvo.setPk_corp(vo.getPk_corp());
	}

	// private void dealTaxItem(Map<String, TaxitemVO> taxItemMap,
	// YntCpaccountVO cvo,
	// VATInComInvoiceVO vo,
	// TzpzBVO tzpzbvo,
	// String pk_corp,
	// String userid){
	//
	// List<TaxitemVO> list = (List<TaxitemVO>)
	// sys_taxsetserv.queryItembycode(userid, pk_corp, cvo.getAccountcode());
	//
	// list = filterTaxItem(list);
	// if(list != null && list.size() > 0){
	// Map<String, TaxitemVO> cpaTaxMap = hashliseTaxItem(list);
	// TaxitemVO taxitemvo = null;
	// VATInComInvoiceBVO[] bvos = (VATInComInvoiceBVO[]) vo.getChildren();
	// if(bvos != null
	// && bvos.length > 0){
	// String pk_taxitem = bvos[0].getPk_taxitem();
	// DZFDouble sl = SafeCompute.div(vo.getSpsl(), new DZFDouble(100));
	// if(!StringUtil.isEmpty(pk_taxitem)){
	//
	// taxitemvo = cpaTaxMap.get(pk_taxitem);
	// }else{
	// for(TaxitemVO taxvo : list){
	// if(taxvo.getTaxratio() != null && taxvo.getTaxratio().equals(sl)){
	// taxitemvo = taxvo;
	// break;
	// }
	// }
	//
	// }
	//
	// }
	//
	// if(taxitemvo != null){
	// tzpzbvo.setPk_taxitem(taxitemvo.getPk_taxitem());
	// tzpzbvo.setTaxcode(taxitemvo.getTaxcode());
	// tzpzbvo.setTaxname(taxitemvo.getTaxname());
	// tzpzbvo.setTaxratio(taxitemvo.getTaxratio());
	// }
	// }
	//
	// }
	//
	// private Map<String, TaxitemVO> hashliseTaxItem(List<TaxitemVO>
	// taxItemVOs){
	// Map<String, TaxitemVO> taxItemMap = new HashMap<String, TaxitemVO>();
	// if(taxItemVOs != null && taxItemVOs.size() > 0){
	// String key = null;
	// for(TaxitemVO vo : taxItemVOs){
	// key = vo.getPk_taxitem();
	// if(!taxItemMap.containsKey(key)){
	//
	// taxItemMap.put(key, vo);
	// }
	// }
	// }
	//
	// return taxItemMap;
	// }
	//
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
	// VATInComInvoiceVO vo,
	// CorpVO corpvo,
	// DcModelBVO modelbvo,
	// YntCpaccountVO[] accounts,
	// boolean isNewFz,
	// Map<String,Boolean> isTempMap,
	// int level){
	//
	// if(level > 4){
	// isTempMap.put("isTemp", true);
	// return cvo;
	// }
	//
	// boolean isleaf = cvo.getIsleaf().booleanValue();//是否是末级
	// boolean isgysfz = cvo.getIsfzhs().charAt(1) == '1';//是否是供应商辅助核算
	// boolean ischfz = cvo.getIsfzhs().charAt(5) == '1';//是否是存货辅助核算
	//
	// if(isgysfz || ischfz){
	// constructFzhs(cvo, bvo, tzpzList, vo, corpvo, accounts, modelbvo,
	// isgysfz, ischfz, isNewFz);
	// }else if(!isleaf){
	// accounts = AccountCache.getInstance().get(null, vo.getPk_corp());
	// cvo = getXJAccount(cvo, vo.getXhfmc(), accounts, isTempMap, bvo,
	// tzpzList, vo, corpvo, modelbvo, isNewFz, ++level);
	// }
	//
	// return cvo;
	// }

	// private TzpzBVO constructFzhs(YntCpaccountVO cvo,
	// TzpzBVO tzpzbvo,
	// List<TzpzBVO> tzpzList,
	// VATInComInvoiceVO vo,
	// CorpVO corpvo,
	// YntCpaccountVO[] accounts,
	// DcModelBVO modelbvo,
	// boolean isgysfz,
	// boolean ischfz,
	// boolean isNewFz){
	// List<AuxiliaryAccountBVO> fzhslist = new
	// ArrayList<AuxiliaryAccountBVO>();
	// AuxiliaryAccountBVO fzhsx = null;
	//
	// VATInComInvoiceBVO[] incomchildren = (VATInComInvoiceBVO[])
	// vo.getChildren();
	//
	// if(ischfz){
	// if(incomchildren == null || incomchildren.length == 0){
	// getInvFz(vo, null, corpvo, tzpzbvo, accounts);
	// }else{
	// TzpzBVO tzpzchild = null;
	// DZFDouble mny = null;
	// for(VATInComInvoiceBVO incomchild : incomchildren){
	// tzpzchild = new TzpzBVO();
	// mny = (DZFDouble) incomchild.getAttributeValue(modelbvo.getVfield());
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
	// getInvFz(vo, incomchild, corpvo, tzpzchild, accounts);
	//
	// if(cvo != null && cvo.getIsnum() != null &&
	// cvo.getIsnum().booleanValue()){
	// tzpzchild.setNnumber(incomchild.getBnum());
	// tzpzchild.setNprice(incomchild.getBprice());//单价
	// }
	//
	// tzpzList.add(tzpzchild);
	// }
	// }
	// }
	//
	// if(isgysfz){
	// fzhsx = getfzhsx(AuxiliaryConstant.ITEM_SUPPLIER, vo.getPk_corp(),
	// vo.getXhfmc(), null, null, isNewFz);
	// fzhslist.add(fzhsx);
	//
	// if(tzpzList != null && tzpzList.size() > 0){
	// for(TzpzBVO tzpzchild : tzpzList){
	// tzpzchild.setFzhsx2(fzhsx.getPk_auacount_b());
	//
	// if(fzhslist.size() > 0){
	// tzpzchild.setFzhs_list(fzhslist);
	// }
	// }
	// }else{
	// tzpzbvo.setFzhsx2(fzhsx.getPk_auacount_b());
	//
	// if(fzhslist.size() > 0){
	// tzpzbvo.setFzhs_list(fzhslist);
	// }
	// }
	// }
	//
	// return tzpzbvo;
	// }

	// private void getInvFz(VATInComInvoiceVO incomvo, VATInComInvoiceBVO
	// incomchild, CorpVO corpvo,
	// TzpzBVO entry, YntCpaccountVO[] accounts){
	// // 如果启用库存
	// if (corpvo.getBbuildic() != null && corpvo.getBbuildic().booleanValue())
	// {
	// InventoryVO bvo = matchInvtoryIC(incomvo, incomchild, corpvo, accounts);
	// if (bvo != null) {
	// entry.setPk_inventory(bvo.getPk_inventory());
	// }
	// } else {
	// // 不启用库存
	// AuxiliaryAccountBVO bvo = matchInvtoryNoIC(incomvo, incomchild, corpvo);
	// if (bvo != null) {
	// entry.setFzhsx6(bvo.getPk_auacount_b());
	// }
	// }
	// }

	private InventoryVO matchInvtoryIC(VATInComInvoiceVO incomvo, VATInComInvoiceBVO incomchild, CorpVO corpvo,
			YntCpaccountVO cpavo) {

		if (corpvo == null || incomchild == null)
			return null;

		MeasureVO meavo = getMeasureVO(incomvo, incomchild, corpvo);

		String pk_measure = null;
		if (meavo != null) {
			pk_measure = meavo.getPk_measure();
		}

		InventoryVO invvo = ocr_atuomatch.getInventoryVOByName(incomchild.getBspmc(), incomchild.getInvspec(),
				pk_measure, corpvo.getPk_corp());

		if (invvo == null && ((incomchild != null && !StringUtil.isEmpty(incomchild.getBspmc())))) {
			invvo = new InventoryVO();
			invvo.setPk_corp(corpvo.getPk_corp());
			invvo.setCreatetime(new DZFDateTime());
			if (meavo != null)
				invvo.setPk_measure(pk_measure);
			invvo.setCreator(incomvo.getCoperatorid());

			invvo.setInvspec(incomchild.getInvspec());
			invvo.setName(incomchild.getBspmc());

			invvo.setCode(yntBoPubUtil.getInventoryCode(corpvo.getPk_corp()));
			// invvo.setPk_measure(pk_measure);
			// for (YntCpaccountVO acc : accounts) {
			// if ("1405".equalsIgnoreCase(acc.getAccountcode()))
			// invvo.setPk_subject(acc.getPk_corp_account());
			// }
			if (cpavo != null) {
				invvo.setPk_subject(cpavo.getPk_corp_account());
			}
			invservice.save(corpvo.getPk_corp(), new InventoryVO[] { invvo });
		}
		return invvo;
	}

	private MeasureVO getMeasureVO(VATInComInvoiceVO incomvo, VATInComInvoiceBVO incomchild, CorpVO corpvo) {
		// 查找计量单位

		if (incomchild == null || StringUtil.isEmpty(incomchild.getMeasurename())) {
			return new MeasureVO();
		}

		StringBuffer sb = new StringBuffer();
		sb.append(" pk_corp=? and nvl(dr,0)=0 and name = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(corpvo.getPk_corp());
		sp.addParam(incomchild.getMeasurename());
		List<MeasureVO> listVo = (List<MeasureVO>) singleObjectBO.retrieveByClause(MeasureVO.class, sb.toString(), sp);
		MeasureVO meavo = null;
		if (listVo == null || listVo.size() == 0) {
			meavo = new MeasureVO();
			meavo.setPk_corp(corpvo.getPk_corp());
			meavo.setCreatetime(new DZFDateTime());
			meavo.setCreator(incomvo.getCoperatorid());
			meavo.setName(incomchild.getMeasurename());
			meavo.setCode(yntBoPubUtil.getMeasureCode(corpvo.getPk_corp()));
			listVo = new ArrayList<>();
			listVo.add(meavo);
			measervice.updateVOArr(corpvo.getPk_corp(), incomvo.getCoperatorid(),listVo);
		} else {
			meavo = listVo.get(0);
		}
		return meavo;
	}

	private AuxiliaryAccountBVO matchInvtoryNoIC(VATInComInvoiceVO incomvo, VATInComInvoiceBVO incomchild,
												 CorpVO corpvo) {
		if (corpvo == null || incomchild == null)
			return null;
		AuxiliaryAccountBVO bvo = null;
		String pk_auacount_h = AuxiliaryConstant.ITEM_INVENTORY;
		bvo = ocr_atuomatch.getAuxiliaryAccountBVOByInfo(incomchild.getBspmc(), incomchild.getInvspec(),
				incomchild.getMeasurename(), corpvo.getPk_corp(), pk_auacount_h);

		// 如果 匹配存货辅助 匹配不上新建

		if (bvo == null && ((incomchild != null && StringUtil.isEmpty(incomchild.getBspmc())))) {
			bvo = new AuxiliaryAccountBVO();
			bvo.setCode(yntBoPubUtil.getFZHsCode(corpvo.getPk_corp(), pk_auacount_h));

			bvo.setUnit(incomchild.getMeasurename());
			bvo.setSpec(incomchild.getInvspec());
			bvo.setName(incomchild.getBspmc());

			bvo.setPk_corp(corpvo.getPk_corp());
			bvo.setDr(0);
			bvo.setPk_auacount_h(AuxiliaryConstant.ITEM_INVENTORY);
			bvo = gl_fzhsserv.saveB(bvo);
		}
		return bvo;
	}

	// 设置下级科目
	// private YntCpaccountVO getXJAccount(YntCpaccountVO account,
	// String ghfmc,
	// YntCpaccountVO[] accounts,
	// Map<String,Boolean> isTempMap,
	// TzpzBVO tzpzbvo,
	// List<TzpzBVO> tzpzList,
	// VATInComInvoiceVO vo,
	// CorpVO corpvo,
	// DcModelBVO dcmodelvo,
	// boolean isNewFz,
	// int level){
	// List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();//存储下级科目
	//
	// boolean isgysfz;
	// boolean ischfz;
	// for(YntCpaccountVO accvo : accounts){
	// if(accvo.getIsleaf().booleanValue() && accvo.get__parentId() != null &&
	// accvo.get__parentId().startsWith(account.getAccountcode())){
	// if(accvo.getAccountname().equals(ghfmc)){
	// return accvo;
	// }else{
	// isgysfz = accvo.getIsfzhs().charAt(1) == '1';//是否是供应商辅助核算
	// ischfz = accvo.getIsfzhs().charAt(5) == '1';//是否是存货辅助核算
	// if(isgysfz || ischfz){
	// accvo = buildTzpzBVoByAccount(accvo, tzpzbvo, tzpzList, vo, corpvo,
	// dcmodelvo, accounts, isNewFz, isTempMap, level);
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
	// AuxiliaryAccountBVO fzhs = queryBByName(pk_corp, name, dw, gg,
	// pk_fzhslb);
	//
	// if(isNewFz)
	// return fzhs == null ? "1" : fzhs.getPk_auacount_b();
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
	// return fzhs.getPk_auacount_b();
	// }

	private String getPK_fzhsx(String pk_fzhslb, String pk_corp, String name, String dw, String gg, boolean isNewFz) {
		return getfzhsx(pk_fzhslb, pk_corp, name, dw, gg, isNewFz).getPk_auacount_b();
	}

	private AuxiliaryAccountBVO getfzhsx(String pk_fzhslb, String pk_corp, String name, String dw, String gg,
			boolean isNewFz) {

		AuxiliaryAccountBVO fzhs = queryBByName(pk_corp, name, dw, gg, pk_fzhslb);

		if (fzhs == null) {
			fzhs = new AuxiliaryAccountBVO();
			fzhs.setPk_auacount_h(pk_fzhslb);
			fzhs.setCode(getRandomCode());
			fzhs.setName(name);
			fzhs.setUnit(dw);
			fzhs.setSpec(gg);
			fzhs.setPk_corp(pk_corp);
			fzhs = (AuxiliaryAccountBVO) singleObjectBO.saveObject(pk_corp, fzhs);
		}
		return fzhs;
	}

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
	 * 此方法会过滤掉折扣行 调用时慎用
	 */
	public List<VATInComInvoiceVO> construcComInvoice(VATInComInvoiceVO[] vos, String pk_corp) {
		List<String> pks = new ArrayList<String>();
		for (VATInComInvoiceVO vo : vos) {
			pks.add(vo.getPrimaryKey());
		}

		String wherePart = SqlUtil.buildSqlForIn("pk_vatincominvoice", pks.toArray(new String[0]));

		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();
		sf.append(" select * from ynt_vatincominvoice where ");
		sf.append(wherePart);
		sf.append(" and pk_corp = ? and nvl(dr,0) = 0 ");// and (pk_tzpz_h is
															// null or pk_tzpz_h
															// = '')
		sf.append(" order by kprj asc, rowid asc ");

		sp.addParam(pk_corp);
		List<VATInComInvoiceVO> list = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATInComInvoiceVO.class));

		// 查询子表
		sf.delete(0, sf.length());
		sf.append(" Select * From ynt_vatincominvoice_b Where  ");
		sf.append(wherePart);
		sf.append("  and pk_corp = ? and nvl(dr,0) = 0 order by  pk_vatincominvoice_b asc, rowno asc, rowid asc ");

		List<VATInComInvoiceBVO> bList = (List<VATInComInvoiceBVO>) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanListProcessor(VATInComInvoiceBVO.class));

		if (bList != null && bList.size() > 0) {
			Map<String, List<VATInComInvoiceBVO>> bMap = hashlizeBodyMap(bList);

			if (list != null && list.size() > 0) {
				String key = null;
				List<VATInComInvoiceBVO> tempb = null;
				for (VATInComInvoiceVO vo : list) {
					key = vo.getPk_vatincominvoice();
					if (bMap.containsKey(key)) {
						tempb = bMap.get(key);

						vo.setChildren(tempb.toArray(new VATInComInvoiceBVO[0]));
					}
				}
			}
		}
		// 过滤掉折口行（也就是金额存在负数的行）
		list = filterZkRow(list, pk_corp);

		return list;
	}

	private List<VATInComInvoiceVO> filterZkRow(List<VATInComInvoiceVO> stoList, String pk_corp) {

		if (stoList == null || stoList.size() == 0)
			return null;

		for (VATInComInvoiceVO vo : stoList) {
			if (vo == null)
				continue;

			VATInComInvoiceBVO[] bvos = (VATInComInvoiceBVO[]) vo.getChildren();

			if (bvos == null || bvos.length == 0)
				continue;

			List<VATInComInvoiceBVO> list = Arrays.asList(bvos);
			List<VATInComInvoiceBVO> nlist = new ArrayList<>();
			int len = bvos.length;

			for (int i = 0; i < len; i++) {
				if (i != 0) {
					VATInComInvoiceBVO bvo = list.get(i);
					if (bvo.getBhjje() == null || bvo.getBhjje().doubleValue() < 0) {
						VATInComInvoiceBVO lastvo = list.get(i - 1);
						if (!StringUtil.isEmpty(bvo.getBspmc()) && !StringUtil.isEmpty(lastvo.getBspmc())) {
							if (bvo.getBspmc().equals(lastvo.getBspmc())) {
								lastvo.setBhjje(SafeCompute.add(lastvo.getBhjje(), bvo.getBhjje()));
								lastvo.setBspse(SafeCompute.add(lastvo.getBspse(), bvo.getBspse()));
								lastvo.setBnum(SafeCompute.add(lastvo.getBnum(), bvo.getBnum()));
								lastvo.setBprice(SafeCompute.div(lastvo.getBhjje(), lastvo.getBnum()));
								continue;
							}
						}
					}
				}
				nlist.add(list.get(i));
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
			vo.setChildren(nlist.toArray(new VATInComInvoiceBVO[nlist.size()]));
		}
		return stoList;
	}

	private Map<String, List<VATInComInvoiceBVO>> hashlizeBodyMap(List<VATInComInvoiceBVO> bList) {
		Map<String, List<VATInComInvoiceBVO>> map = new HashMap<String, List<VATInComInvoiceBVO>>();
		List<VATInComInvoiceBVO> tempList = null;
		String key = null;
		for (VATInComInvoiceBVO bvo : bList) {
			key = bvo.getPk_vatincominvoice();

			if (key != null) {
				if (!map.containsKey(key)) {
					tempList = new ArrayList<VATInComInvoiceBVO>();
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

	@Override
	public void saveImp(MultipartFile file, VATInComInvoiceVO paramvo, String pk_corp, String fileType, String userid,
						StringBuffer msg) throws DZFWarpException {

		List<VATInComInvoiceVO> list = null;
		DZFBoolean flag = DZFBoolean.FALSE;

		if("xml".equals(fileType)){
			flag = DZFBoolean.TRUE;
			list = getDataByXml(file, pk_corp, userid, paramvo, msg);
		}else{
			Map<String, DZFBoolean> map = new HashMap<String, DZFBoolean>();
			list = getDataByExcel(file, pk_corp, userid, fileType, paramvo, map, msg);
			flag = map.get("flag");
		}

		if (list == null || list.size() == 0) {
			String frag = "<p>导入文件数据为空，请检查。</p>";

			if (msg.length() == 0) {
				msg.append(frag);
			}

			throw new BusinessException(msg.toString());
		}

		list = filterRepeationData(list, pk_corp, msg);// 过滤重复数据

		// 重新封装详细数据
		if(flag != null && flag.booleanValue()){
			list = buildInComVO(list);
		}

		Map<String, VATInComInvoiceVO[]> sendData = new HashMap<String, VATInComInvoiceVO[]>();
		sendData.put("adddocvos", list.toArray(new VATInComInvoiceVO[0]));
		VATInComInvoiceVO[] vos = updateVOArr(pk_corp, sendData);

		if (vos != null && vos.length > 0) {
			paramvo.setCount(vos.length);// 供action提示使用
			msg.append("<p>文件导入成功</p>");
		}

		setAfterImportPeriod(vos, paramvo);
	}

	private List<VATInComInvoiceVO> getDataByExcel(MultipartFile file,
			String pk_corp,
			String userid,
			String fileType,
			VATInComInvoiceVO paramvo,
			Map<String, DZFBoolean> map,
			StringBuffer msg){
		InputStream is = null;
		List<VATInComInvoiceVO> list = null;
		try {
			is = file.getInputStream();

			Workbook impBook = null;
			if ("xls".equals(fileType)) {
				impBook = new HSSFWorkbook(is);
			} else if ("xlsx".equals(fileType)) {
				impBook = new XSSFWorkbook(is);
			} else {
				throw new BusinessException("不支持的文件格式");
			}

			int sheetno = impBook.getNumberOfSheets();
			if (sheetno == 0) {
				throw new Exception("需要导入的数据为空。");
			}

			// 判断导入文件来源
			int sourceType = getFileSourceType(impBook);
			Sheet sheet1 = null;
			if (sourceType > 1) {
				sheet1 = impBook.getSheetAt(0);
			} else {
				sheet1 = impBook.getSheetAt(1);
			}

			if(sourceType == IBillManageConstants.ZENGZHIAHUI_AUTO){
//				flag = DZFBoolean.TRUE;
				map.put("flag", DZFBoolean.TRUE);
			}

			list = doImport(sourceType, paramvo, sheet1, pk_corp, fileType, userid, msg);

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

		return list;
	}

	private List<VATInComInvoiceVO> getDataByXml(MultipartFile file,
			String pk_corp,
			String userid,
			VATInComInvoiceVO paramvo,
			StringBuffer msg){
		BufferedReader breader = null;
		InputStreamReader isreader = null;
		InputStream is = null;

		List<VATInComInvoiceVO> list = null;
		try {
			is = file.getInputStream();
			isreader = new InputStreamReader(is, "utf-8");
			breader = new BufferedReader(isreader);

			list = getDataByXml1(breader, pk_corp, userid, msg, paramvo);

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

		return list;
	}

	private List<VATInComInvoiceVO> getDataByXml1(BufferedReader file,
			String pk_corp,
			String userid,
			StringBuffer msg,
			VATInComInvoiceVO paramvo){

		List<VATInComInvoiceVO> list = new ArrayList<VATInComInvoiceVO>();
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFBoolean ISZHUAN = (StringUtil.isEmpty(corpvo.getChargedeptname())
				|| SMALL_TAX.equals(corpvo.getChargedeptname())) ? DZFBoolean.FALSE : DZFBoolean.TRUE;
		SAXReader reader = new SAXReader();
		StringBuffer sf = new StringBuffer();
		try {
			Document document = reader.read(file);

			Element bookStore = document.getRootElement().element("data");

			Iterator firstIt = bookStore.elementIterator("row");
			Map<String, String> instance = getInstance();
			// 遍历body节点
			int count = 0;
			StringBuffer innermsg = new StringBuffer();
			Element secondEl = null;
			String key;
			String value;
			String temp;
			VATInComInvoiceVO vo;
			while(firstIt.hasNext()){
				vo = new VATInComInvoiceVO();
				count++;
				secondEl = (Element) firstIt.next();
				for(Map.Entry<String, String> entry : instance.entrySet()){
					key = entry.getKey();
					temp = secondEl.elementText(key);
					if(!StringUtil.isEmpty(temp)){
						value = entry.getValue();
						vo.setAttributeValue(value, temp);
					}
				}

				innermsg.setLength(0);

				checkDataValid(vo, innermsg, count, pk_corp, IBillManageConstants.ZENGZHIAHUI_AUTO);
				if (innermsg.length() != 0) {
					msg.append(innermsg);
					continue;
				}

				setDefaultValue(pk_corp, userid, vo, ISZHUAN, IBillManageConstants.ZENGZHIAHUI_AUTO, paramvo);
				list.add(vo);
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

		return list;
	}

	private Map<String, String> getInstance(){
		Map<String, String> style = new HashMap<String, String>();
		style.put("dm", "fp_dm");
		style.put("hm", "fp_hm");
		style.put("gf", "ghfsbh");
		style.put("xf", "xhfsbh");
		style.put("kq", "kprj");
		style.put("je", "hjje");
		style.put("se", "spse");
		style.put("rq", "rzrj");

		return style;
	}

	/**
	 * 判断文件来源
	 *
	 * @param book
	 * @return
	 */
	private int getFileSourceType(Workbook book) {
		Sheet sheet = book.getSheetAt(0);
		int iBegin = 0;
		Cell aCell = null;
		String sTmp = null;
		String tmp;

		Map<Integer, Object[][]> style = getStyleMap();
		Integer maxCount = getMaxCountByStyleMap();

		Map<String, String> nameMap = null;
		Object[][] objarr = null;
		int flagCount = 0;

		Map<String, String> zzsmap = new HashMap<String, String>();//增值税平台
		Object[][] zzsobjs = style.get(IBillManageConstants.ZENGZHIAHUI_AUTO);
		if(zzsobjs != null && zzsobjs.length > 0){
			String str;
			for(Object[] obj : zzsobjs){
				str = (String) obj[3];
				if("Y".equals(str)){
					str = (String) obj[1];//字段名称
					zzsmap.put(str, str);
				}
			}
		}

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

			Integer key;
			for (Map.Entry<Integer, Object[][]> entry1 : style.entrySet()) {//除增值税平台外

				key = entry1.getKey();
				if(key == IBillManageConstants.ZENGZHIAHUI_AUTO){
					//判断增值税相关excel
					sTmp = null;
					flagCount = 0;
					for(Map.Entry<String, String> entry : nameMap.entrySet()){
						sTmp = entry.getKey();
						for(Map.Entry<String, String> innerEntry : zzsmap.entrySet()){
							tmp = innerEntry.getKey();
							if(tmp.contains(sTmp)){
								flagCount++;
								continue;
							}
						}
					}

					if (zzsmap.size() == flagCount) {
						return IBillManageConstants.ZENGZHIAHUI_AUTO;// 返回对应的模板类型
					}
				}else{
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


		}

		return IBillManageConstants.AUTO;
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

	private List<VATInComInvoiceVO> doImport(int sourceType, VATInComInvoiceVO paramvo, Sheet sheet, String pk_corp, String fileType,
			String userid, StringBuffer msg) {
		List<VATInComInvoiceVO> list = getDataByExcel(sourceType, paramvo, sheet, pk_corp, userid, msg);

		return list;
	}

	private List<VATInComInvoiceVO> buildInComVO(List<VATInComInvoiceVO> list){
//		List<CaiFangTongHVO> ll = new ArrayList<CaiFangTongHVO>();
		if(list != null && list.size() > 0){
			CaiFangTongHVO hvo;
			List<CaiFangTongHVO> hList = new ArrayList<CaiFangTongHVO>();
			for(VATInComInvoiceVO vo : list){
				try {
					hvo = new CaiFangTongHVO();
					hvo.setKprq(vo.getKprj().toString());
					hvo.setFpdm(vo.getFp_dm());
					hvo.setFphm(vo.getFp_hm());
					hvo.setHjbhsje(vo.getHjje().toString());

					hList = new ArrayList<CaiFangTongHVO>();
					hList.add(hvo);

					hList = VatUtil.reGetData(hList);
					if(hList == null || hList.size() == 0){
						continue;
					}

					transferVO(vo, hList.get(0));
				} catch (Exception e) {
					log.error(e.getMessage(), e);
				}


			}
		}

		return list;
	}

	private void transferVO(VATInComInvoiceVO vo, CaiFangTongHVO hvo){
		Map<String, String> hmap = getVATHMapping();
		Map<String, String> bmap = getVATBMapping();

		Object value = null;
		for(Map.Entry<String, String> entry : hmap.entrySet()){
			value = hvo.getAttributeValue(entry.getKey());

			if(value != null){
				vo.setAttributeValue(entry.getValue(), value);
			}
		}

		setCyKplx(vo);

		CaiFangTongBVO[] hbvos = hvo.getChildren();
		if(hbvos != null && hbvos.length > 0){
			List<VATInComInvoiceBVO> bvoList = new ArrayList<VATInComInvoiceBVO>();
			VATInComInvoiceBVO bvo = null;
			String spmc = null;
			DZFDouble spsl = null;

			for(CaiFangTongBVO hbvo : hbvos){
				bvo = new VATInComInvoiceBVO();
				bvo.setPk_corp(vo.getPk_corp());
				for(Map.Entry<String, String> entry1 : bmap.entrySet()){
					value = hbvo.getAttributeValue(entry1.getKey());
					if(value != null){
						bvo.setAttributeValue(entry1.getValue(), value);
					}

				}

				if(StringUtil.isEmpty(spmc)
						&& !StringUtil.isEmpty(bvo.getBspmc())){
					spmc = bvo.getBspmc();
				}

				if(spsl == null && bvo.getBspsl() != null){
					spsl = bvo.getBspsl();
				}

				bvoList.add(bvo);
			}

			vo.setChildren(
					bvoList.toArray(new VATInComInvoiceBVO[0]));

			vo.setSpsl(spsl);//设置税率
			if(!StringUtil.isEmpty(spmc)){
				vo.setSpmc(spmc);
			}

		}
	}

	private void setCyKplx(VATInComInvoiceVO vo){
		String kplx = vo.getKplx();
		DZFDouble je = vo.getHjje();
		if(ICaiFangTongConstant.FPLX_PT_N.equals(kplx)){
			if(je != null && je.doubleValue() >= 0){
				kplx = ICaiFangTongConstant.FPLX_1;
			}else if(je != null && je.doubleValue() < 0){
				kplx = ICaiFangTongConstant.FPLX_2;
			}else{
				kplx = null;
			}
		}else if(ICaiFangTongConstant.FPLX_PT_Y.equals(kplx)){
			if(je != null && je.doubleValue() >= 0){
				kplx = ICaiFangTongConstant.FPLX_4;
			}else if(je != null && je.doubleValue() < 0){
				kplx = ICaiFangTongConstant.FPLX_5;
			}else{
				kplx = null;
			}
		}else{//3失控、4异常暂不处理
			kplx = null;
		}

		vo.setKplx(kplx);
	}

	private Map<String, String> getVATHMapping(){
		Map<String, String> map = new HashMap<String, String>();

		map.put("xsf_dz", "xhfdzdh");//销方地址电话
		map.put("xsf_yh", "xhfyhzh");//销方开户行及账号
		map.put("gmf_dz", "ghfdzdh");//购方地址电话
		map.put("gmf_yh", "ghfyhzh");//购方开户行及账号
//		map.put("fpdm", "fp_dm");//发票代码
//		map.put("fphm", "fp_hm");//发票号码
//		map.put("kprq", "kprj");//开票日期
		map.put("gmf_nsrmc", "ghfmc");//购方名称
		map.put("gmf_nsrsbh", "ghfsbh");//购方纳税人识别号
		map.put("xsf_nsrmc", "xhfmc");//销方名称
		map.put("xsf_nsrsbh", "xhfsbh");//销方纳税人识别号
		map.put("kphjse", "spse");//合计税额
		map.put("kphjje", "jshj");//价税合计
//		map.put("hjbhsje", "hjje");//合计金额
		map.put("fp_zldm", "fpzl");//票种代码
		map.put("kplx", "kplx");//票据类型
		map.put("bz", "demo");////备注
//		map.put("vdef1", "rzjg");//认证状态
//		map.put("vdef2", "rzrj");//认证日期
		map.put("jym", "jym");//校验码

		return map;
	}

	private Map<String, String> getVATBMapping(){
		Map<String, String> map = new HashMap<String, String>();

		map.put("spmc", "bspmc");//商品名称
		map.put("ggxh", "invspec");//规格型号
		map.put("dw", "measurename");//单位
		map.put("spsl", "bnum");//数量
		map.put("spdj", "bprice");//单价
		map.put("spje", "bhjje");//金额
		map.put("sl", "bspsl");//税率
		map.put("se", "bspse");//税额

		return map;
	}

	private List<VATInComInvoiceVO> filterRepeationData(List<VATInComInvoiceVO> list, String pk_corp,
			StringBuffer msg) {
		Map<String, VATInComInvoiceVO> map = new LinkedHashMap<String, VATInComInvoiceVO>();
		List<VATInComInvoiceVO> result = new ArrayList<VATInComInvoiceVO>();
		String key;
		StringBuffer sqlf = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		sqlf.append(" select fp_hm, fp_dm from ynt_vatincominvoice where ( ");
		for (VATInComInvoiceVO vo : list) {
			key = vo.getFp_dm() + "_" + vo.getFp_hm();
			if (!map.containsKey(key)) {
				sqlf.append(" ( fp_hm = ? and fp_dm = ? ) or");
				sp.addParam(vo.getFp_hm());
				sp.addParam(vo.getFp_dm());

				map.put(key, vo);
			}
		}

		sqlf.delete(sqlf.length() - 2, sqlf.length());
		sqlf.append(" ) and pk_corp = ? and nvl(dr,0) = 0 ");
		sp.addParam(pk_corp);

		List<VATInComInvoiceVO> oldList = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sqlf.toString(), sp,
				new BeanListProcessor(VATInComInvoiceVO.class));

		if (oldList != null && oldList.size() > 0) {
			for (VATInComInvoiceVO oldvo : oldList) {
				key = oldvo.getFp_dm() + "_" + oldvo.getFp_hm();

				if (map.containsKey(key)) {
					msg.append("<p>发票号码:");
					msg.append(oldvo.getFp_hm());
					msg.append("重复,请检查</p>");

					map.remove(key);
				}

			}
		}

		for (Map.Entry<String, VATInComInvoiceVO> entry : map.entrySet()) {
			result.add(entry.getValue());
		}

		return result;
	}

	private void setAfterImportPeriod(VATInComInvoiceVO[] vos, VATInComInvoiceVO paramvo) {
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

	public List<VATInComInvoiceVO> getDataByExcel(int sourceType, VATInComInvoiceVO paramvo, Sheet sheet, String pk_corp, String userid,
			StringBuffer msg) throws BusinessException {
		List<VATInComInvoiceVO> blist = null;
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		DZFBoolean ISZHUAN = (StringUtil.isEmpty(corpvo.getChargedeptname())
				|| SMALL_TAX.equals(corpvo.getChargedeptname())) ? DZFBoolean.FALSE : DZFBoolean.TRUE;
		int iBegin = 0;
		blist = new ArrayList<VATInComInvoiceVO>();
		Cell aCell = null;
		String sTmp = "";
		String str;
		VATInComInvoiceVO excelvo = null;

		Object[][] STYLE_1 = getStyleMap().get(sourceType);
		Set<String> matchSet = null;
		Map<String, Integer> cursorMap = buildImpStypeMap(STYLE_1);
		Map<String, Integer> cursor2Map= new HashMap<String, Integer>();
		Integer maxCCount = getStyleCellCount().get(sourceType);// 遍历的最大列数

		// int loopCount = 0;
		for (; iBegin < (sheet.getLastRowNum() + 1); iBegin++) {
			if (sheet.getRow(iBegin) == null && iBegin != sheet.getLastRowNum())
				continue;
			if (iBegin == sheet.getLastRowNum())
				throw new BusinessException("导入失败,导入文件抬头格式不正确 !");
			// loopCount = 0;
			matchSet = new HashSet<String>();

			if(IBillManageConstants.ZENGZHIAHUI_AUTO == sourceType){
				for (int k = 0; k < maxCCount; k++) {

					aCell = sheet.getRow(iBegin).getCell(k);
					sTmp = getExcelCellValue(aCell);

					if(sTmp != null){
						for(Map.Entry<String, Integer> e1 : cursorMap.entrySet()){
							str = e1.getKey();
							if(str.contains(sTmp)){
								cursor2Map.put(sTmp, k);// 重新设置列行号
							}
						}
					}

				}

				if (cursor2Map.size() > 0) {
					String ss;
					int incount = 0;
					int judcount= 0;
					for(Object[]arr : STYLE_1){
						ss = (String) arr[3];
						if("Y".equals(ss)){
							judcount++;
						}
						ss = (String) arr[1];
						for(Map.Entry<String, Integer> ee : cursor2Map.entrySet()){
							if(ss.contains(ee.getKey())){
								incount++;
								break;
							}
						}
					}

					if(incount >= judcount){
						iBegin++;

						STYLE_1 = resetStyleCursor2(STYLE_1, cursor2Map);

						cursorMap = cursor2Map;
						break;
					}

				}
			}else{
				for (int k = 0; k < maxCCount; k++) {

					aCell = sheet.getRow(iBegin).getCell(k);
					sTmp = getExcelCellValue(aCell);

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
			}

			if (iBegin == sheet.getLastRowNum())
				throw new BusinessException("文件格式不正确，请检查");

		}
		boolean flag;
		int count;// 计数器的作用判断该行是不是空行，如count == STYLE_1.length 则为空行
		boolean isNullFlag;
		StringBuffer innermsg = new StringBuffer();
		for (; iBegin < (sheet.getLastRowNum() + 1); iBegin++) {
			excelvo = new VATInComInvoiceVO();
			flag = false;
			count = 0;
			isNullFlag = false;
			for (int j = 0; j < STYLE_1.length; j++) {
				if (sheet.getRow(iBegin) == null) {
					isNullFlag = true;
					break;
				}

				aCell = sheet.getRow(iBegin).getCell((new Integer(STYLE_1[j][0].toString())).intValue());
				sTmp = getExcelCellValue(aCell);

				sTmp = specialTreatCellValue(sourceType, j, sTmp);

				if (FILTER_PLACE_HOLDER.equals(sTmp)) {
					flag = true;
					break;
				}

				if (sTmp != null && !StringUtil.isEmpty(sTmp.trim())) {
					excelvo.setAttributeValue(STYLE_1[j][2].toString(), sTmp.replace(" ", ""));
				} else {
					count++;
				}
			}

			if (excelvo != null && !flag && count != STYLE_1.length && !isNullFlag) {
				innermsg.setLength(0);
				checkDataValid(excelvo, innermsg, iBegin, pk_corp, sourceType);

				if (innermsg.length() != 0) {
					msg.append(innermsg);
					continue;
				}

				setDefaultValue(pk_corp, userid, excelvo, ISZHUAN, sourceType, paramvo);

				blist.add(excelvo);
			}

		}

		blist = specialVatBVO(blist, sourceType, pk_corp);

		Map<String, DcModelHVO> dcMap1 = new HashMap<String, DcModelHVO>();
		Map<String, DcModelHVO> dcMap2 = new HashMap<String, DcModelHVO>();
		// Map<String, DcModelHVO> dcMap3 = new HashMap<String, DcModelHVO>();
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		hashlizeDcModel(dcList, dcMap1, dcMap2, pk_corp);
		// 通用模板导入时，如果填写业务类型字段时，走此特殊处理
		if (sourceType == IBillManageConstants.AUTO && blist != null && blist.size() > 0) {
			specialBusiNameValue(blist, pk_corp, dcMap2);
		}

		matchBusiName2(blist, dcMap1);

		return blist;
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

	private Object[][] resetStyleCursor2(Object[][] style, Map<String, Integer> cursorMap) {

		if (style != null && style.length > 0) {
			List<Object[]> list = new ArrayList<Object[]>();
			String name = null;
			Integer acursor = null;
			Integer mcursor = null;
			String key;

			for (Object[] arr : style) {
				acursor = (Integer) arr[0];
				name = (String) arr[1];

				for(Map.Entry<String, Integer> entry : cursorMap.entrySet()){
					key = entry.getKey();
					if(name.contains(key)){
						mcursor = cursorMap.get(key);
						arr[1] = key;
						arr[0] = mcursor;
						list.add(arr);
						break;
					}
				}


			}

			if(list.size() > 0){
				style = new Object[list.size()][4];
				for(int i=0; i < list.size();i++){

					style[i] =list.get(i);
				}
			}
		}

		return style;
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

	private void matchBusiName2(List<VATInComInvoiceVO> list, Map<String, DcModelHVO> dcMap) {
		if (list == null || list.size() == 0) {
			return;
		}

		for (VATInComInvoiceVO vo : list) {
			if (StringUtil.isEmpty(vo.getSpmc()) || !StringUtil.isEmpty(vo.getPk_model_h())) {
				continue;
			}

			scanMatchBusiName(vo, dcMap);

		}

	}

	private List<VATInComInvoiceVO> specialVatBVO(List<VATInComInvoiceVO> blist, int sourceType, String pk_corp) {
		if (blist == null || blist.size() == 0) {
			return null;
		}

		if(IBillManageConstants.ZENGZHIAHUI_AUTO == sourceType)
			return blist;

		String[][] STYLE_2 = getBStyleMap().get(sourceType);
		if (STYLE_2 == null)
			return null;

		List<VATInComInvoiceVO> list = new ArrayList<VATInComInvoiceVO>();
		Map<String, List<VATInComInvoiceBVO>> bmap = new HashMap<String, List<VATInComInvoiceBVO>>();
		List<VATInComInvoiceBVO> btempList = null;
		VATInComInvoiceBVO bvo = null;
		Object value = null;
		String key = null;
		DZFDouble sl = DZFDouble.ZERO_DBL;

		// List<TaxitemVO> taxvos = queryTaxItems(pk_corp);

		for (VATInComInvoiceVO vo : blist) {
			key = vo.getFp_dm() + "_" + vo.getFp_hm();

			bvo = new VATInComInvoiceBVO();
			for (String[] arr : STYLE_2) {
				value = vo.getAttributeValue(arr[0]);
				bvo.setAttributeValue(arr[1], value);
			}

			// 特殊处理 财税助手税率
//			if (IBillManageConstants.CAISHUI_AUTO == sourceType) {
				sl = SafeCompute.multiply(SafeCompute.div(bvo.getBspse(), bvo.getBhjje()), new DZFDouble(100));
				bvo.setBspsl(sl.setScale(0, DZFDouble.ROUND_HALF_UP));
//			}

			// dealBodyTaxItem(taxvos, bvo);

			if (bmap.containsKey(key)) {
				btempList = bmap.get(key);

				bvo.setRowno(btempList.size() + 1);

				btempList.add(bvo);
			} else {
				list.add(vo);

				btempList = new ArrayList<VATInComInvoiceBVO>();

				bvo.setRowno(1);// 序号

				btempList.add(bvo);
				bmap.put(key, btempList);
			}

		}

		for (VATInComInvoiceVO hvo : list) {
			key = hvo.getFp_dm() + "_" + hvo.getFp_hm();
			if (bmap.containsKey(key)) {
				btempList = bmap.get(key);
				hvo.setChildren(btempList.toArray(new VATInComInvoiceBVO[0]));
			}
		}

		return list;
	}
	/***
	 * 设置进项子表税目
	 */
	// public void dealBodyTaxItem(List<TaxitemVO> taxvos, VATInComInvoiceBVO
	// bvo) throws DZFWarpException{
	// if(taxvos == null || taxvos.size() == 0){
	// return;
	// }
	//
	// DZFDouble sl = bvo.getBspsl();
	// if(sl == null || sl.doubleValue() <= 0){
	// return;
	// }
	//
	// sl = SafeCompute.div(sl, new DZFDouble(100));
	//
	// TaxitemVO taxvo = null;
	//
	// for(TaxitemVO vo : taxvos){
	//
	// if(vo.getTaxratio() != null && vo.getTaxratio().equals(sl)){
	// taxvo = vo;
	// break;
	// }
	// }
	//
	// if(taxvo != null){
	// bvo.setPk_taxitem(taxvo.getPk_taxitem());
	// }
	//
	// }

	/**
	 * 业务类型字段特殊处理
	 *
	 * @param list
	 * @param pk_corp
	 */
	private void specialBusiNameValue(List<VATInComInvoiceVO> list, String pk_corp, Map<String, DcModelHVO> dcMap2) {
		String busiName = null;
		DcModelHVO tempDcVo = null;
		String zpflag;
		DZFBoolean zhuan;
		for (VATInComInvoiceVO vo : list) {
			busiName = vo.getBusitypetempname();

			if (!StringUtil.isEmpty(busiName)) {
				zhuan = vo.getIszhuan();
				zpflag = zhuan != null && zhuan.booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
				zpflag = busiName + "_" + zpflag;

				tempDcVo = dcMap2.get(zpflag);

				if (tempDcVo == null) {
					vo.setBusitypetempname(null);
				} else {
					vo.setBusitypetempname(tempDcVo.getBusitypetempname());
					vo.setPk_model_h(tempDcVo.getPk_model_h());
				}
			}

		}

	}

	/**
	 * 导入时，业务类型匹配规则
	 *
	 * @param
	 * @return
	 */
	// private String matchBusiValue(String busiName) {
	// StringBuffer keysf = new StringBuffer();
	// keysf.append(busiName);
	// if (busiName.startsWith("银行")) {
	// keysf.append("_");
	// keysf.append(FieldConstant.FPSTYLE_20);// 银行收付款回单
	// keysf.append("_");
	// keysf.append(FieldConstant.SZSTYLE_04);// 银行支出
	// } else if (busiName.contains("费") || busiName.endsWith("单")) {
	// keysf.append("_");
	// keysf.append(FieldConstant.FPSTYLE_21);
	// keysf.append("_");
	// keysf.append(FieldConstant.SZSTYLE_06);
	// } else if (busiName.startsWith("购买") || busiName.startsWith("资产购入")) {
	// keysf.append("_");
	// keysf.append(FieldConstant.FPSTYLE_02);
	// keysf.append("_");
	// keysf.append(FieldConstant.SZSTYLE_06);
	// }
	// return keysf.toString();
	// }

	private void hashlizeDcModel(List<DcModelHVO> dcModelList, Map<String, DcModelHVO> dcmap1,
			Map<String, DcModelHVO> dcmap2, String pk_corp) {

//		dcModelList = new DcPzmb().filterDataCommon(dcModelList, pk_corp, null, "Y", null, null);
		if (dcModelList == null || dcModelList.size() == 0) {
			return;
		}

		String key = null;
		String pk_gs = null;
		String szcode = null;
		String vscode = null;
		String businame;
		for (DcModelHVO hvo : dcModelList) {
			szcode = hvo.getSzstylecode();
			vscode = hvo.getVspstylecode();
			pk_gs = hvo.getPk_corp();
			businame = hvo.getBusitypetempname();
			key = businame + "_" + vscode;

			if ((FieldConstant.FPSTYLE_01.equals(vscode) || FieldConstant.FPSTYLE_02.equals(vscode))
					&& (FieldConstant.SZSTYLE_02.equals(szcode)// 只过滤出 支出 业务类型模板
					|| FieldConstant.SZSTYLE_04.equals(szcode) || FieldConstant.SZSTYLE_06.equals(szcode))) {

				if (!IDefaultValue.DefaultGroup.equals(pk_gs)) {
					dcmap1.put(key, hvo);
					dcmap2.put(key, hvo);
				}

				if (!dcmap1.containsKey(key)) {
					dcmap1.put(key, hvo);
				}

				if(FieldConstant.SZSTYLE_06.equals(szcode)
						&& !dcmap2.containsKey(key)){//匹配往来支出
					dcmap2.put(key, hvo);
				}

			}
		}

	}

	/**
	 * 废弃不用
	 *
	 * @param
	 */
	// private void specialAssignValue(List<VATInComInvoiceVO> blist){
	// String spmc = null;
	// char[] arr = null;
	// StringBuffer sf = null;
	// int count;
	// for(VATInComInvoiceVO vo : blist){
	// spmc = vo.getSpmc();
	// if(!StringUtil.isEmpty(spmc)){
	// sf = new StringBuffer();
	// arr = spmc.toCharArray();
	// count = 0;
	// if(arr != null && arr.length > 0){
	// for(int i = 0; i < arr.length; i++){
	// if(java.lang.Character.toString(arr[i]).matches("[A-Za-z0-9,.()?!]+")){
	// count = count + 1;
	// }else{
	// count = count + 2;
	// }
	// if(count <= 200){
	// sf.append(arr[i]);
	// }else{
	// break;
	// }
	// }
	// if(!StringUtil.isEmpty(sf.toString()) && count > 200){
	// vo.setSpmc(sf.toString());
	// }
	// }
	// }
	// }
	// }

	private String specialTreatCellValue(int sourceType, int j, String sTmp) {
		if (sTmp == null || StringUtil.isEmpty(sTmp.trim()))
			return sTmp;

		if (sourceType == IBillManageConstants.AUTO && j == 10) {// 8
			if (PASSRULT_DEFAULT.equals(sTmp.trim())) {
				sTmp = String.valueOf(IBillManageConstants.RSPASS);
			} else {
				sTmp = String.valueOf(IBillManageConstants.RSNOPASS);
			}
		} else if (sourceType == IBillManageConstants.AUTO && j == 3) {// 入账期间
			sTmp = sTmp.replace("-", "");
			if (sTmp.length() >= 6) {
				String year = sTmp.substring(0, 4);
				String month = sTmp.substring(4, 6);
				sTmp = year + "-" + month;
			} else {
				sTmp = "";
			}

		} else if (sourceType == IBillManageConstants.CAISHUI_AUTO && j == 1) {// 1
			if (PASSRULT_CAISHUI.equals(sTmp.trim())) {
				sTmp = String.valueOf(IBillManageConstants.RSPASS);
			} else {
				sTmp = FILTER_PLACE_HOLDER;
			}
		} else if (sourceType == IBillManageConstants.CAISHUI_AUTO && j == 2) {// 2
			if (PASSRULT_CF.equals(sTmp.trim())) {
				sTmp = FILTER_PLACE_HOLDER;
			}
		} else if (sourceType == IBillManageConstants.CAISHUI_AUTO && j == 3) {// 3
			if (PASSRULT_HG.equals(sTmp.trim())) {
				sTmp = FILTER_PLACE_HOLDER;
			}
		} else if (sourceType == IBillManageConstants.CAISHUI_AUTO && j == 0) {// 0
			DZFDateTime time = new DZFDateTime(sTmp.trim());
			sTmp = time.getDate().toString();
		} else if (sourceType == IBillManageConstants.ZHONGXING_AUTO && j == 6) {// 6
			if (sTmp.contains(PASSRULT_DEFAULT)) {
				sTmp = String.valueOf(IBillManageConstants.RSPASS);
			} else {
				sTmp = FILTER_PLACE_HOLDER;
			}
		}
//		else if (sourceType == IBillManageConstants.PIAOTONGSM_AUTO && j == 18) {// 票通扫描
//																					// 验证发票状态
//																					// 18
//			if (PASSRULT_CAISHUI.equals(sTmp.trim())) {// 正常
//				sTmp = String.valueOf(IBillManageConstants.RSPASS);
//			} else {
//				sTmp = String.valueOf(IBillManageConstants.RSNOPASS);
//			}
//		}

		return sTmp;
	}

	/**
	 * 校验导入文件
	 *
	 * @param vo
	 * @param sf
	 * @param index
	 */
	private void checkDataValid(VATInComInvoiceVO vo, StringBuffer sf, int index, String pk_corp, int sourceType) {
		CorpVO corpVO = corpService.queryByPk(pk_corp);

		StringBuffer msg = new StringBuffer();
		if (StringUtil.isEmpty(vo.getFp_hm())) {
			msg.append(" 发票号码不允许为空,请检查！ ");
		}
		if (StringUtil.isEmpty(vo.getFp_dm())) {
			msg.append(" 发票代码不允许为空,请检查！ ");
		}
		if (vo.getKprj() == null) {
			msg.append(" 开票日期不允许为空,请检查！ ");
		}
		if(!StringUtil.isEmpty(vo.getInperiod())){
			String jzperiod = DateUtils.getPeriod(corpVO.getBegindate());
			if(vo.getInperiod().compareTo(jzperiod) < 0){
				msg.append("入账期间不允许在建账期间前,请检查");
			}
		}
//		else if (vo.getKprj().before(corpVO.getBegindate())) {
//			msg.append(" 开票日期不允许在建账日期前,请检查！ ");
//		}
		if (StringUtil.isEmpty(vo.getXhfmc()) && sourceType == IBillManageConstants.AUTO) {
			msg.append(" 销货方不允许为空,请检查！ ");
		}
		if (vo.getSpse() == null) {
			msg.append(" 税额不允许为空,请检查！ ");
		}
		if (vo.getHjje() == null) {
			msg.append(" 金额不允许为空,请检查！ ");
		}

		if (vo.getRzjg() != null && vo.getRzjg() == 1) {

			if (vo.getRzrj() == null) {
				msg.append(" 勾选已认证，认证日期不允许为空,请检查！ ");
			} else if (vo.getRzrj() != null && vo.getRzrj().before(corpVO.getBegindate())) {
				msg.append(" 认证日期不允许在建账日期前,请检查！ ");
			} else if (vo.getKprj() != null && vo.getRzrj().before(vo.getKprj())) {
				msg.append(" 认证日期不允许在开票日期前,请检查！ ");
			}
		}

		if (!StringUtil.isEmpty(msg.toString())) {
			sf.append("<p>第").append(index + 1).append("行  ").append(msg.toString()).append(" </p> ");
		}
	}

	/**
	 * 导入复制
	 *
	 * @param pk_corp
	 * @param userid
	 * @param vo
	 */
	private void setDefaultValue(String pk_corp, String userid, VATInComInvoiceVO vo, DZFBoolean iszhuan,
			int sourceType, VATInComInvoiceVO paramvo) {
		vo.setPk_corp(pk_corp);
		vo.setCoperatorid(userid);
		vo.setDoperatedate(new DZFDate());

		if(sourceType == IBillManageConstants.ZENGZHIAHUI_AUTO){
			if(vo.getRzrj() != null){
				vo.setRzjg(1);
			}
		}

		// 设置期间
		String period = null;
		if (vo.getRzjg() != null && vo.getRzjg() == 1 && vo.getRzrj() != null) {
			period = DateUtils.getPeriod(vo.getRzrj());
		} else if (vo.getKprj() != null) {
			String inper = paramvo == null ? "" : paramvo.getInperiod();
			if(StringUtil.isEmpty(inper)){
				CorpVO corpvo = corpService.queryByPk(pk_corp);
				if(vo.getKprj().before(corpvo.getBegindate())){
					period = DateUtils.getPeriod(corpvo.getBegindate());
				}else{
					period = DateUtils.getPeriod(vo.getKprj());
				}
			}else{
				period = inper;
			}

		}

		if (sourceType != IBillManageConstants.CAISHUI_AUTO) {
			// 设置价税合计
			vo.setJshj(SafeCompute.add(vo.getSpse(), vo.getHjje()));

		}
		if (sourceType == IBillManageConstants.AUTO) {
			if (vo.getBnum() != null && vo.getHjje() != null) {
				vo.setBprice(SafeCompute.div(vo.getHjje(), vo.getBnum()));
			}

			if (!StringUtil.isEmpty(vo.getInperiod())) {
				period = vo.getInperiod();
			}
		}
		if (sourceType == IBillManageConstants.ZHONGXING_AUTO) {
			iszhuan = DZFBoolean.TRUE;
		}
		if (sourceType == IBillManageConstants.CAISHUI_AUTO && !StringUtil.isEmpty(vo.getFpzl())) {
			if (vo.getFpzl().endsWith(VAT_SPECIAL_ZHUAN)) {
				iszhuan = DZFBoolean.TRUE;
			} else if (vo.getFpzl().endsWith(VAT_SPECIAL_PU)) {
				iszhuan = DZFBoolean.FALSE;
			}
		}
		// 设置税率
//		DZFDouble sl = vo.getSpsl();
//		if (sl == null || sl.doubleValue() == DZFDouble.ZERO_DBL.doubleValue()) {
//			vo.setSpsl(SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100)));
//			vo.setSpsl(vo.getSpsl().setScale(0, DZFDouble.ROUND_HALF_UP));
//		}

		vo.setPeriod(period);
		vo.setInperiod(period);
		vo.setIszhuan(iszhuan);
		// 设置来源
		vo.setSourcetype(sourceType);
	}

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
				java.text.DecimalFormat formatter = new java.text.DecimalFormat("#########.##");
				if ("General".equals(cell.getCellStyle().getDataFormatString())) {
					ret = formatter.format(cell.getNumericCellValue());
				} else if (cell.getCellStyle().getDataFormatString().indexOf(".") >= 0) {
					ret = formatter.format(cell.getNumericCellValue());
				} else {
					ret = sdf.format(HSSFDateUtil.getJavaDate(cell.getNumericCellValue()));
				}
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {
				ret = cell.getCellFormula();
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
		return ret;
	}

	public Map<Integer, Object[][]> getStyleMap() {
		Map<Integer, Object[][]> STYLE = new HashMap<Integer, Object[][]>();
		Object[][] obj0 = new Object[][] { { 1, "发票代码", "fp_dm" }, { 2, "发票号码", "fp_hm" }, { 3, "开票日期", "kprj" },
				{ 4, "入账期间", "inperiod" }, // 入账期间
				{ 5, "开票项目", "spmc" }, { 6, "数量", "bnum" }, { 7, "金额", "hjje" }, { 8, "税额", "spse" },
				{ 9, "业务类型", "busitypetempname" }, { 10, "销货方名称", "xhfmc" }, { 11, "认证结果", "rzjg" },
				{ 12, "认证日期", "rzrj" } };

		Object[][] obj2 = new Object[][] { // 变量命名参考IBillManageConstants 财税助手
				{ 0, "验证时间", "rzrj" }, { 1, "验证结果", "rzjg" }, { 2, "重复验证", "cfyz" }, { 3, "合规性", "hgx" },
				{ 5, "发票种类", "fpzl" }, { 6, "发票代码", "fp_dm" }, { 7, "发票号码", "fp_hm" }, { 8, "开票日期", "kprj" },
				{ 9, "购方名称", "ghfmc" }, { 10, "销方名称", "xhfmc" }, // 销货方名称
				{ 11, "金额(汇总)", "hjje" }, // 合计金额
				{ 12, "税额(汇总)", "spse" }, // 税额
				{ 13, "价税合计(汇总)", "jshj" }, // 价税合计
				{ 14, "购方识别号", "ghfsbh" }, { 15, "购方地址电话", "ghfdzdh" }, { 16, "购方银行账号", "ghfyhzh" },
				{ 17, "销方识别号", "xhfsbh" }, { 18, "销方地址电话", "xhfdzdh" }, { 19, "销售银行账号", "xhfyhzh" }, // 销方银行账号
				{ 26, "货物或应税劳务名称", "spmc" }, // 开票项目
												// 下方数据存放在子表
				{ 26, "货物或应税劳务名称", "bspmc" }, // 开票项目
				{ 27, "规格型号", "invspec" }, { 28, "单位", "measurename" }, { 29, "数量", "bnum" }, { 30, "单价", "bprice" },
				{ 31, "金额", "bhjje" }, { 32, "税率", "bspsl" }, { 33, "税额", "bspse" } };

		Object[][] obj3 = new Object[][] { // 中兴通导入
				{ 2, "发票代码", "fp_dm" }, { 3, "发票号码", "fp_hm" }, { 4, "开票日期", "kprj" }, { 5, "金额", "hjje" },
				{ 6, "税额", "spse" }, { 7, "销方识别号", "xhfsbh" },
				// { 8, "状态", "billStatus" },
				{ 9, "认证结果", "rzjg" }, { 10, "认证日期", "rzrj" } };

//		Object[][] obj4 = new Object[][] { // 票通扫描仪导入
//				{ 12, "发票种类", "fpzl" }, { 9, "发票代码", "fp_dm" }, { 10, "发票号码", "fp_hm" }, { 11, "开票日期", "kprj" },
//				{ 13, "购方名称", "ghfmc" }, { 15, "销方名称", "xhfmc" }, // 销货方名称
//				{ 6, "金额", "hjje" }, // 合计金额
//				{ 7, "税额", "spse" }, // 税额
//				{ 8, "价税合计", "jshj" }, // 价税合计
//				{ 14, "购方纳税人识别号", "ghfsbh" }, { 16, "销方纳税识别号", "xhfsbh" },
//
//				{ 18, "认证状态", "rzjg" }, { 19, "认证日期", "rzrj" },
//
//				{ 1, "商品名称", "spmc" }, // 开票项目
//										// 下方数据存放在子表
//				{ 1, "商品名称", "bspmc" }, // 开票项目
//				{ 2, "规格型号", "invspec" }, { 3, "数量", "bnum" }, { 4, "单价", "bprice" }, { 5, "税率", "bspsl" },
//				{ 6, "金额", "bhjje" }, { 7, "税额", "bspse" } };

		//来源增值税平台
		Object[][] obj5 = getImpConfigObj();

		STYLE.put(IBillManageConstants.AUTO, obj0);
		STYLE.put(IBillManageConstants.CAISHUI_AUTO, obj2);
		STYLE.put(IBillManageConstants.ZHONGXING_AUTO, obj3);
//		STYLE.put(IBillManageConstants.PIAOTONGSM_AUTO, obj4);

		if(obj5 != null){
			STYLE.put(IBillManageConstants.ZENGZHIAHUI_AUTO, obj5);
		}

		return STYLE;
	}

	private Object[][] getImpConfigObj(){
		String sql = " select * from ynt_vatimpconfset where stype = ? and nvl(dr,0) = 0 ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillManageConstants.HEBING_JXFP);
		List<VatImpConfSetVO> ll = (List<VatImpConfSetVO>) singleObjectBO.executeQuery(sql,
				sp, new BeanListProcessor(VatImpConfSetVO.class));

		Object[][] objs = null;
		int length = ll == null || ll.size() == 0 ? 0 : ll.size();
		if(length > 0){
			objs = new Object[length][3];
			for(int i = 0; i < length; i++){
				VatImpConfSetVO vo = ll.get(i);
				Object[] obj = { 1000, vo.getColname(), vo.getFiled(), vo.getRequire() };
				objs[i] = obj;

			}
		}

		return objs;
	}

	public Map<Integer, Integer> getStyleCellCount() {
		Map<Integer, Integer> countMap = new HashMap<Integer, Integer>();
		countMap.put(IBillManageConstants.AUTO, 15);
		countMap.put(IBillManageConstants.CAISHUI_AUTO, 60);
		countMap.put(IBillManageConstants.ZHONGXING_AUTO, 20);
//		countMap.put(IBillManageConstants.PIAOTONGSM_AUTO, 50);
		countMap.put(IBillManageConstants.ZENGZHIAHUI_AUTO, 50);

		return countMap;
	}

	public synchronized Map<Integer, String[][]> getBStyleMap() {
		if (BSTYLE == null) {
			BSTYLE = new HashMap<Integer, String[][]>();
			String[][] obj0 = new String[][] { // 通用模板 (主表字段—子表字段排列)
					{ "spmc", "bspmc" }, { "hjje", "bhjje" }, { "spse", "bspse" }, { "spsl", "bspsl" },
					{ "bnum", "bnum" }, { "pk_corp", "pk_corp" } };

			String[][] obj2 = new String[][] { // 财税助手
					{ "bspmc", "bspmc" }, { "invspec", "invspec" }, { "measurename", "measurename" },
					{ "bnum", "bnum" }, { "bprice", "bprice" }, { "bhjje", "bhjje" }, { "bspse", "bspse" },
					{ "bspsl", "bspsl" }, { "pk_corp", "pk_corp" } };

			String[][] obj3 = new String[][] { // 中兴通
					{ "hjje", "bhjje" }, { "spse", "bspse" }, { "spsl", "bspsl" }, { "pk_corp", "pk_corp" } };

			BSTYLE.put(IBillManageConstants.AUTO, obj0);
			BSTYLE.put(IBillManageConstants.CAISHUI_AUTO, obj2);
			BSTYLE.put(IBillManageConstants.ZHONGXING_AUTO, obj3);
		}

		return BSTYLE;
	}

	private Map<String, DcModelHVO> hashliseBusiTypeMap(List<DcModelHVO> dcList){
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
	}

	@Override
	public String saveBusiType(VATInComInvoiceVO[] vos,
			String busiid,
			String businame,
			String selvalue,
			String userid,
			String pk_corp) throws DZFWarpException {
		VATInComInvoiceVO newVO = null;
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		//过滤数据
//		dcList = new DcPzmb().filterDataCommon(dcList, pk_corp, null, null, "Y", null);

//		Map<String, DcModelHVO> dcmap = DZfcommonTools.hashlizeObjectByPk(dcList,
//				new String[]{"busitypetempname", "vspstylecode", "szstylecode"});
		Map<String, DcModelHVO> dcmap = hashliseBusiTypeMap(dcList);

		int upCount = 0;
		int npCount = 0;
		List<VATInComInvoiceVO> list = new ArrayList<VATInComInvoiceVO>();
		String stylecode;
		String key;
		DcModelHVO dchvo;
		StringBuffer innermsg = new StringBuffer();
		String ims;
		for (VATInComInvoiceVO vo : vos) {

			if (!StringUtil.isEmptyWithTrim(vo.getPk_tzpz_h())) {
				ims = String.format("<font color='red'><p>进项发票[%s_%s]已生成凭证</p></font>",
						vo.getFp_dm(), vo.getFp_hm());
				innermsg.append(ims);
				npCount++;
				continue;
			}

			stylecode = vo.getIszhuan() != null &&
					vo.getIszhuan().booleanValue() ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;
			key = businame + "," + stylecode + "," + selvalue;
			dchvo = dcmap.get(key);
			if(dchvo == null){
				String zhflag = stylecode.equals(FieldConstant.FPSTYLE_01) ? "专票" : "普票";
				ims = String.format("<font color='red'><p>进项发票[%s_%s]为%s与入账模板票据类型不一致，请检查</p></font>",
						vo.getFp_dm(), vo.getFp_hm(), zhflag);
				innermsg.append(ims);
				npCount++;
				continue;
			}

			newVO = new VATInComInvoiceVO();
			newVO.setPk_vatincominvoice(vo.getPk_vatincominvoice());
			newVO.setPk_model_h(dchvo.getPk_model_h());
			newVO.setBusitypetempname(dchvo.getBusitypetempname());
			upCount++;
			list.add(newVO);
		}

		if (list != null && list.size() > 0) {
			singleObjectBO.updateAry(list.toArray(new VATInComInvoiceVO[0]),
					new String[] { "pk_model_h", "busitypetempname" });
		}

		VatBusinessTypeVO typevo = new VatBusinessTypeVO();
		if(StringUtil.isEmpty(busiid)){
			typevo.setBusiname(businame);
			typevo.setSelectvalue(selvalue);
			typevo.setCoperatorid(userid);
			typevo.setDoperatedate(new DZFDate());
			typevo.setPk_corp(pk_corp);
			typevo.setStype(IBillManageConstants.HEBING_XXFP);
			singleObjectBO.saveObject(pk_corp, typevo);
		}else{
			typevo.setSelectvalue(selvalue);
			typevo.setPk_vatbusitype(busiid);
			singleObjectBO.update(typevo, new String[]{"selectvalue"});
		}

		StringBuffer msg = new StringBuffer();
		msg.append("业务类型设置更新成功 ").append(upCount).append(" 条");
		msg.append(npCount > 0 ? ",未更新 " + npCount + " 条,详细原因如下:" + innermsg.toString() : "");
		return msg.toString();
	}

	@Override
	public TzpzHVO getTzpzHVOByID(VATInComInvoiceVO[] vos, String pk_corp, String userid, VatInvoiceSetVO setvo, boolean accway)
			throws DZFWarpException {

		List<VATInComInvoiceVO> sencondvos = new ArrayList<VATInComInvoiceVO>();
		List<String> pks = new ArrayList<String>();
		for (VATInComInvoiceVO vo : vos) {
			if (!StringUtil.isEmpty(vo.getPrimaryKey()) && StringUtil.isEmpty(vo.getPk_tzpz_h())
			// && (vo.getIsic() == null || vo.getIsic().booleanValue())
			) {
				sencondvos.add(vo);
				pks.add(vo.getPrimaryKey());
			}
		}

		if (pks.size() == 0) {
			return null;
		}

		List<VATInComInvoiceVO> afterlist = construcComInvoice(sencondvos.toArray(new VATInComInvoiceVO[0]), pk_corp);

		if (afterlist == null || afterlist.size() == 0)
			throw new BusinessException("查询进项发票信息不存在，请检查");

		checkisGroup(afterlist, pk_corp);

		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
		DcModelHVO mHVO = null;
		Map<String, DcModelHVO> dcmap = gl_yhdzdserv.queryDcModelVO(pk_corp);
		mHVO = dcmap.get(afterlist.get(0).getPk_model_h());

		if (mHVO == null)
			throw new BusinessException("进项发票:未能找到对应的业务类型模板，请检查");

		DcModelBVO[] models = mHVO.getChildren();
		OcrImageLibraryVO lib = null;
		OcrInvoiceDetailVO[] detailvos = null;
		int fp_style;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		List<TzpzBVO> inlist = null;

		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);

		for (VATInComInvoiceVO vo : afterlist) {
			fp_style =getFpStyle(vo);
			headVO.setFp_style(fp_style);
			lib = changeOcrInvoiceVO(vo, pk_corp, ifptype, fp_style);
			detailvos = changeOcrInvoiceDetailVO(vo, pk_corp);

			inlist = new ArrayList<TzpzBVO>();
			aitovoucherserv_vatinvoice.getTzpzBVOList(headVO, models, lib, detailvos, inlist, accounts);

			//根据规则设置摘要
			setPzZy(inlist, setvo, vo);
			tblist.addAll(inlist);
		}

		aitovoucherserv_vatinvoice.setSpeaclTzpzBVO1(headVO, lib, tblist);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		buildFzhs(tblist, pk_corp);

		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : tblist) {
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}

		afterlist.get(0).setCount(afterlist.size());
		createTzpzHVO(headVO, afterlist.get(0), pk_corp, userid, null, null, totalMny, accway);

		// headvo sourcebillid 重新赋值
		pks = new ArrayList<String>();
		for (VATInComInvoiceVO svo : afterlist) {
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

	// private List<TzpzBVO> createCombinTzpzBVO(List<VATInComInvoiceVO> vos,
	// Map<String, VATInComInvoiceVO> bkmap,
	// DcModelHVO mHVO,
	// String pk_curr,
	// Map<String, YntCpaccountVO> ccountMap,
	// Map<String, Boolean> isTempMap,
	// String pk_corp,
	// String userid,
	// boolean isNewFz){
	// List<TzpzBVO> bodyList = new ArrayList<TzpzBVO>();
	// Boolean isTemp = null;
	// DcModelBVO[] mBVOs = mHVO.getChildren();
	// DZFDouble mny = null;
	// TzpzBVO tzpzbvo = null;
	// YntCpaccountVO cvo = null;
	// VATInComInvoiceVO vo = null;
	// String key = null;
	// Map<String, TzpzBVO> tzpzbmap = new HashMap<String, TzpzBVO>();
	// CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
	// YntCpaccountVO[] account = AccountCache.getInstance().get(null, pk_corp);
	//
	// TzpzBVO temptzpz = null;
	// List<TzpzBVO> tzpzList = null;
	//
	// int rowno = 1;//分录行排序
	// for(DcModelBVO bvo : mBVOs){
	// for(Map.Entry<String, VATInComInvoiceVO> entry : bkmap.entrySet()){
	// key = entry.getKey();
	// vo = entry.getValue();
	// if("$$$".equals(key)){
	// vo.setXhfmc("");
	// }
	//
	// tzpzbvo = new TzpzBVO();
	//
	// cvo = ccountMap.get(bvo.getPk_accsubj());
	//
	// if(cvo == null){
	// log.error("业务类型模板涉及主表："+
	// mHVO.getPrimaryKey()+",子表："+bvo.getPrimaryKey());
	// throw new BusinessException(mHVO.getBusitypetempname() +
	// "业务类型科目未找到，请检查!");
	// }
	//
	// isTemp = isTempMap.get(ISTEMP);
	// if(StringUtil.isEmpty(vo.getXhfmc())){
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
	// cvo = null;
	// }
	// }
	// return bodyList;
	// }

	// private void buildTzpzBMap(TzpzBVO tzpzbvo, Map<String, TzpzBVO>
	// tzpzbmap, List<TzpzBVO> bodyList){
	// String key = new StringBuffer().append(tzpzbvo.getPk_accsubj())
	// .append("_")
	// .append(tzpzbvo.getFzhsx2())
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

	@Override
	public void checkBeforeCombine(VATInComInvoiceVO[] vos) throws DZFWarpException {
		String busipk = null;
		DZFDate temprzrj = null;
		String tempLastperiod = null;
		String period = null;
		for (VATInComInvoiceVO vo : vos) {

			if (!StringUtil.isEmpty(busipk) && !busipk.equals(vo.getPk_model_h())) {
				throw new BusinessException("业务类型不一致,请检查");
			}
			busipk = vo.getPk_model_h();

			// 判断认证日期
			// temprzrj = vo.getRzrj();
			// if(temprzrj != null && vo.getRzjg() != null && vo.getRzjg() ==
			// 1){
			// period = DateUtils.getPeriod(temprzrj);
			// if(StringUtil.isEmpty(tempLastperiod)){
			// tempLastperiod = period;
			// }else if(!tempLastperiod.equals(period)){
			// throw new BusinessException("认证日期不在一个月份的不能合并,请检查");
			// }
			// }

		}

		DcModelHVO modelhvo = (DcModelHVO) singleObjectBO.queryByPrimaryKey(DcModelHVO.class, busipk);

		if (modelhvo == null || modelhvo.getDr() == 1) {
			throw new BusinessException("业务类型不存在或已被删除,请重新选择业务类型");
		}

	}

	@Override
	public void saveCombinePZ(List<VATInComInvoiceVO> list, String pk_corp, String userid, Map<String, DcModelHVO> dcmap,
			VatInvoiceSetVO setvo, DZFBoolean lwflag, boolean accway, boolean isT) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);

		checkisGroup(list, pk_corp);//校验

		List<VATInComInvoiceVO> vatModelList = new ArrayList<VATInComInvoiceVO>();

		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);
		OcrImageLibraryVO lib = null;
		OcrInvoiceDetailVO[] detailvos = null;
		int fp_style;
		DcModelHVO mHVO = null;
		DcModelBVO[] models = null;
		DcModelHVO defaultModel = null;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		List<TzpzBVO> inlist  = null;

		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);

		DZFDate maxDate = null;
//		DZFBoolean iszhFlag = null;
		List<String> imageGroupList = new ArrayList<>();
		for (VATInComInvoiceVO vo : list) {
//			iszhFlag = getCheckZhuanFlag(vo, iszhFlag);

			if (StringUtil.isEmpty(vo.getPk_model_h())) {
				setModelValue(vo, corpvo, vatModelList);
				if(StringUtil.isEmpty(vo.getPk_model_h())){
					if(defaultModel == null){
						defaultModel = getDefaultModelHVO(dcmap, vo, corpvo);
					}

					if(defaultModel != null){
						vo.setPk_model_h(defaultModel.getPk_model_h());
						vo.setBusitypetempname(defaultModel.getBusitypetempname());
						vatModelList.add(vo);
						isT = true;
					}
				}
			}
			if (StringUtil.isEmpty(vo.getPk_model_h())) {
				throw new BusinessException("进项发票:[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]业务类型匹配失败,请选择相应业务类型");
			}

			mHVO = dcmap.get(vo.getPk_model_h());
			if(mHVO == null){
				throw new BusinessException("进项发票:[" + vo.getFp_hm() + "," + vo.getFp_hm() + "]未能找到对应的业务类型模板,请重新选择业务类型");
			}
			models = mHVO.getChildren();
			if (models != null && models.length > 0) {
				// 总账核算存货
				DZFBoolean icinv = new DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));// 启用总账库存
				if (icinv != null && icinv.booleanValue()) {
					for (DcModelBVO model : models) {
						String kcsp = gl_cbconstant.getKcsp_code();
						String ycl = gl_cbconstant.getYcl_code();

						String kmbm = kcsp;
						kmbm = model.getKmbm();
						if (!StringUtil.isEmpty(kmbm)) {
							if (kmbm.startsWith(kcsp) || kmbm.startsWith(ycl)) {
								throw new BusinessException("模板中包含1405或1403相关科目，请检查");
							}
						}
					}
				}
			}
			fp_style = getFpStyle(vo);
			headVO.setFp_style(fp_style);
			lib = changeOcrInvoiceVO(vo, pk_corp, ifptype, fp_style);
			detailvos = changeOcrInvoiceDetailVO(vo, pk_corp);

			inlist = new ArrayList<TzpzBVO>();

			aitovoucherserv_vatinvoice.getTzpzBVOList(headVO, models, lib, detailvos, inlist, accounts);

			// 找最大日期
			maxDate = getMaxDate(maxDate, vo);
			if (!StringUtil.isEmpty(vo.getPk_image_group()) && !imageGroupList.contains(vo.getPk_image_group())) {
				imageGroupList.add(vo.getPk_image_group());
			}

			//根据规则设置摘要
			setPzZy(inlist, setvo, vo);
			tblist.addAll(inlist);
		}
		aitovoucherserv_vatinvoice.setSpeaclTzpzBVO1(headVO, lib, tblist);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : tblist) {
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}

		VATInComInvoiceVO firstvo = list.get(0);
		if (maxDate != null) {
			firstvo.setInperiod(DateUtils.getPeriod(maxDate));
		}

		list.get(0).setCount(list.size());
		createTzpzHVO(headVO, list.get(0), pk_corp, userid, null, null, totalMny, accway);
		if (imageGroupList != null && imageGroupList.size() > 0) {
			//合并图片组
//			String groupId = mergeImage(pk_corp, imageGroupList);
			String groupId = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			headVO.setPk_image_group(groupId);
			updateImageGroup(groupId);
		}
		// headvo sourcebillid 重新赋值
		List<String> pks = new ArrayList<String>();
		for (VATInComInvoiceVO svo : list) {
			pks.add(svo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));

		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));

		if (isT) {
			createTempPZ(headVO, list.toArray(new VATInComInvoiceVO[list.size()]), pk_corp);
		} else {
			headVO.setSourcebillid(sourcebillid);
			headVO = voucher.saveVoucher(corpvo, headVO);
		}

		if (lwflag != null && lwflag.booleanValue()) {
			updateOtherType(list);// 其他
		}

		// 更新业务类型标识
		if (vatModelList != null && vatModelList.size() > 0) {
			singleObjectBO.updateAry(vatModelList.toArray(new VATInComInvoiceVO[0]),
					new String[] { "pk_model_h", "busitypetempname" });
		}

	}

//	private DZFBoolean getCheckZhuanFlag(VATInComInvoiceVO vo, DZFBoolean iszhFlag){
//		DZFBoolean iszh = vo.getIsZhuan();
//
//		iszh = iszh == null ? DZFBoolean.FALSE : iszh;
//		if(iszhFlag == null){
//			iszhFlag = iszh;
//		}else if(!iszh.equals(iszhFlag)){
//			throw new BusinessException("票据性质不同，专普票不允许合并，请检查");
//		}
//
//		return iszhFlag;
//	}

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
				"update ynt_vatincominvoice set pk_image_group = ? where pk_corp = ? and pk_image_group in " + inSQL
						+ " and nvl(dr,0)=0 ",
				sp);
		return groupId;
	}

	private void updateImageGroup(String pk_image_group) {
		// 图片生成凭证
		String sql = " update  ynt_image_group set  istate=?,isuer='Y' where  pk_image_group = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(PhotoState.state100);
		sp.addParam(pk_image_group);
		singleObjectBO.executeUpdate(sql, sp);
	}

	private DZFDate getMaxDate(DZFDate date, VATInComInvoiceVO vo) {
		DZFDate voucherDate = null;
		if (!StringUtil.isEmpty(vo.getInperiod())) {
			voucherDate = DateUtils.getPeriodEndDate(vo.getInperiod());
		} else if (vo.getRzjg() != null && vo.getRzjg() == 1 && vo.getRzrj() != null) {

			voucherDate = vo.getRzrj();
		} else {
			voucherDate = vo.getKprj();
		}

		if (date == null) {
			return voucherDate;
		} else if (date.before(voucherDate)) {
			date = voucherDate;
		}

		return date;
	}

	private void checkisGroup(List<VATInComInvoiceVO> list, String pk_corp){
		SQLParameter sp = new SQLParameter();
		StringBuffer sf = new StringBuffer();

		sf.append(" select y.fp_dm, y.fp_hm ");
		sf.append(" 	From ynt_vatincominvoice y where y.pk_corp = ? and nvl(dr, 0) = 0 ");
		sf.append(" and ( 1 <> 1 ");

		sp.addParam(pk_corp);
		boolean flag = true;
		List<String> libs = new ArrayList<String>();
		for(VATInComInvoiceVO vo : list){
			if(!StringUtil.isEmpty(vo.getPk_image_library())
					&& !StringUtil.isEmpty(vo.getPk_image_group())){
				sf.append(" or pk_image_group = ? ");

				sp.addParam(vo.getPk_image_group());
				libs.add(vo.getPk_image_library());

				flag = false;
			}
		}

		sf.append(" ) ");

		if(flag){
			return;
		}

		for(String s : libs){
			sf.append(" and y.pk_image_library != ? ");
			sp.addParam(s);
		}

		List<VATInComInvoiceVO> vos = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(VATInComInvoiceVO.class));

		if(vos != null && vos.size() > 0){
			sf.setLength(0);
			sf.append("<br>发票号码:");
			for(int i = 0; i < vos.size(); i++){
				sf.append(vos.get(i).getFp_hm());

				if(i != vos.size() - 1){
					sf.append(", ");
				}
			}

			sf.append("归属于合并生单的一组图片，需要勾选涉及该组图片一起生单");

			throw new BusinessException(sf.toString());
		}
	}

	private String buildZy(VatInvoiceSetVO setvo,
			VATInComInvoiceVO vo,
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
					}else if("selectWlZy".equals(ss) && !StringUtil.isEmpty(vo.getXhfmc())){
						sf.append("向").append(vo.getXhfmc());
						flag = true;
					}else if("selectLxZy".equals(ss)){
						sf.append("采购");
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
			taxCode = "^22210+10+(1|2)";
		} else if ("00000100000000Ig4yfE0005".equals(corpVO.getCorptype())) {
			// 企业会计制度
			taxCode = "^21710+10+(1|5)";
		} else if ("00000100000000Ig4yfE0003".equals(corpVO.getCorptype())) {
			// 事业单位
			taxCode = "^22060+10+(1|2)";
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
	            	String code1 = o1.getFullcode();
	            	String code2 = o2.getFullcode();
	            	if (taxMatch != null) {
	            		if (code1.matches(taxMatch) && !code2.matches(taxMatch)) {
		            		code1 = "999";
						} else if (code2.matches(taxMatch) && !code1.matches(taxMatch)) {
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
//		for (int i = 1; i <= 10; i++) {
//			String fzhsID = (String) vo.getAttributeValue("fzhsx" + i);
//			if (i == 6 && StringUtil.isEmpty(fzhsID)) {
//				fzhsID = vo.getPk_inventory();
//			}
//			if (!StringUtil.isEmpty(fzhsID)) {
//				AuxiliaryAccountBVO assist = assistMap.get(fzhsID);
//				if (assist != null) {
//					fullcode.append("_").append(assist.getCode());
//				}
//			}
//		}
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

	private List<TzpzBVO> constructItems(List<TzpzBVO> tzpzlist,
			VatInvoiceSetVO setvo,
			//VATInComInvoiceVO vo,
			String pk_corp) {
		String key = null;
		TzpzBVO tempbvo = null;

		Map<String, TzpzBVO> tzpzmap = new HashMap<String, TzpzBVO>();
		List<TzpzBVO> finalList = new ArrayList<TzpzBVO>();
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
		return finalList;
	}

	protected String constructTzpzKey(TzpzBVO bvo) {
		StringBuffer sf = new StringBuffer();
		sf.append("&").append(bvo.getVdirect()).append("&").append(bvo.getPk_accsubj()).append("&").append(bvo.getPk_inventory()).append("&")
				.append(bvo.getPk_taxitem()).append("&");

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();
	}

	@Override
	public List<String> getBusiTypes(String pk_corp) throws DZFWarpException {
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		List<String> busiList = new LinkedList<String>();
		if (dcList != null && dcList.size() > 0) {
			dcList = new DcPzmbController().filterDataCommon(dcList, pk_corp,
					null, "Y", null, null);
			String businame;
			Map<String, DcModelHVO> map = new HashMap<String, DcModelHVO>();
			for(DcModelHVO hvo : dcList){
				businame = hvo.getBusitypetempname();
				if(!map.containsKey(businame)){
					map.put(businame, hvo);
					busiList.add(businame);
				}
			}

//			String szcode = null;
//			String vscode = null;
//			for (DcModelHVO hvo : dcList) {
//
//				vscode = hvo.getVspstylecode();// 票据类型
//				if (FieldConstant.FPSTYLE_01.equals(vscode) || FieldConstant.FPSTYLE_02.equals(vscode)) {
//					szcode = hvo.getSzstylecode();
//					if ((FieldConstant.SZSTYLE_02.equals(szcode)// 只过滤出 支出
//																// 业务类型模板
//							|| FieldConstant.SZSTYLE_04.equals(szcode) || FieldConstant.SZSTYLE_06.equals(szcode))
//							&& !busiList.contains(hvo.getBusitypetempname())) {
//						busiList.add(hvo.getBusitypetempname());
//					}
//				}
//
//			}
		}
		return busiList;
	}

	@Override
	public VATInComInvoiceVO queryByID(String pk) throws DZFWarpException {
		if (StringUtil.isEmpty(pk))
			throw new BusinessException("参数为空，请检查");
		StringBuffer sf = new StringBuffer();
		sf.append(" Select y.*, h.busitypetempname, h.szstylecode as busisztypecode ");
		sf.append(" From ynt_vatincominvoice y left join ynt_dcmodel_h h on y.pk_model_h = h.pk_model_h ");
		sf.append(" Where nvl(y.version, 0) = 0 and nvl(y.dr,0) = 0 and y.pk_vatincominvoice = ? ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk);

		VATInComInvoiceVO vo = (VATInComInvoiceVO) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanProcessor(VATInComInvoiceVO.class));

		sf.setLength(0);
		sf.append(
				" select * from ynt_vatincominvoice_b b where nvl(dr,0) = 0 and pk_vatincominvoice = ? order by rowno asc, rowid asc ");
		List<VATInComInvoiceBVO> blsit = (List<VATInComInvoiceBVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(VATInComInvoiceBVO.class));

		if (blsit != null && blsit.size() > 0) {
			vo.setChildren(blsit.toArray(new VATInComInvoiceBVO[0]));
		}

		return vo;
//		StringBuffer sf = new StringBuffer();
//		sf.append(" nvl(dr,0) = 0 and pk_vatincominvoice = ? ");
//
//		SQLParameter sp = new SQLParameter();
//		sp.addParam(pk);
//
//		List<VATInComInvoiceVO> list = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sf.toString(), sp,
//				new Class[] { VATInComInvoiceVO.class, VATInComInvoiceBVO.class });
//		return list == null || list.size() == 0 ? null : list.get(0);
	}

	/**
	 * dcmap的key为 businame + "_" + Vspstylecode + "_" + Szstylecode dcmap1的key为
	 * businame + "_" + Vspstylecode
	 *
	 * @param incomvo
	 * @param dcmap
	 * @throws DZFWarpException
	 */
	public void scanMatchBusiName(VATInComInvoiceVO incomvo, Map<String, DcModelHVO> dcmap) throws DZFWarpException {
		if (dcmap == null || dcmap.size() == 0) {
			return;
		}
		String pk_corp = incomvo.getPk_corp();// 公司pk
		CorpVO corpvo = corpService.queryByPk(pk_corp);
		String chargedeptname = corpvo.getChargedeptname();
		DZFBoolean iszh = incomvo.getIszhuan();
		iszh = iszh != null && iszh.booleanValue() ? iszh : DZFBoolean.FALSE;

		String spmc = incomvo.getSpmc();
		//
		// String key = "";
		String key1 = "";
		//
		if (!StringUtil.isEmpty(spmc)) {
			//
			// key = matchFei(spmc, chargedeptname, iszh.booleanValue());
			key1 = matchFei1(spmc, iszh.booleanValue());
		}
		DcModelHVO hvo = dcmap.get(key1);

		// DcModelHVO hvo = null;
		if (hvo == null) {
			hvo = scanMatchBusiName2(incomvo, corpvo, iszh, chargedeptname);
		}

		if (hvo != null && !StringUtil.isEmpty(hvo.getPk_model_h())) {
			incomvo.setPk_model_h(hvo.getPk_model_h());
			incomvo.setBusitypetempname(hvo.getBusitypetempname());
		}

	}

	private DcModelHVO scanMatchBusiName2(VATInComInvoiceVO incomvo, CorpVO corpvo, DZFBoolean iszh,
			String chargedeptname) {
		TzpzHVO hvo = new TzpzHVO();
		hvo.setIfptype(1);// 进项
		hvo.setFp_style(getFpStyle(incomvo));// 1-----普票 2----专票 3---
														// 未开票

		OcrInvoiceVO invo = buildOcrInvoiceVO(incomvo, corpvo, iszh, chargedeptname);
		ImageGroupVO grpvo = new ImageGroupVO();
		// 总账核算存货
		DZFBoolean icinv = new DZFBoolean(IcCostStyle.IC_INVTENTORY.equals(corpvo.getBbuildic()));// 启用总账库存
		if (icinv != null && icinv.booleanValue()) {
			grpvo.setPjlxstatus(PjTypeEnum.OTHER.getValue());
		}
		DcModelHVO dcvo = aitovoucherserv_extend.getMatchModel(invo, corpvo, hvo, grpvo);
		return dcvo;
	}

	private OcrInvoiceVO buildOcrInvoiceVO(VATInComInvoiceVO incomvo, CorpVO corpvo, DZFBoolean iszh,
			String chargedeptname) {
		String invoicetype = iszh.booleanValue() ? "增值税专用发票" : "增值税普通发票";

		OcrInvoiceVO invo = new OcrInvoiceVO();
		invo.setPk_corp(incomvo.getPk_corp());
		invo.setInvoicetype(invoicetype);
		invo.setVmemo(incomvo.getDemo());// 备注

		String name = null;
		if ("一般纳税人".equals(chargedeptname) && iszh.booleanValue()
				&& (incomvo.getRzjg() == null || incomvo.getRzjg() == 0)) {
			name = "待认证";
		} else {
			name = incomvo.getSpmc();
		}
		invo.setVfirsrinvname(name);

		return invo;
	}

	private String matchFei1(String name, boolean iszh) {

		String fpstylecode = iszh ? FieldConstant.FPSTYLE_01 : FieldConstant.FPSTYLE_02;

		String key = name + "_" + fpstylecode;

		return key;
	}

	@Override
	public Map<String, VATInComInvoiceVO> savePt(String pk_corp,
			String userid,
			String ccrecode,
			String f2,
			VATInComInvoiceVO paramvo)
			throws DZFWarpException {
		CorpVO corpvo = getCorpVO(pk_corp);
		corpvo.setVsoccrecode(ccrecode);
		corpvo.setFax2(f2);
		// 此处也需要税盘口令
		String nsrsbh = corpvo.getVsoccrecode();
		String unitname = corpvo.getUnitname();

//		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
//		String golddiskno = taxvo.getGolddiskno();// 金税盘编号

		DZFDate loginDate = paramvo.getKprj();

		beforeCallCheck(nsrsbh, unitname, loginDate);

		Map<String, VATInComInvoiceVO> repMap = piaotongjxserv.savePt(corpvo, f2, userid, loginDate);

		VATInComInvoiceVO[] vos = buildBusiData(repMap);

		setAfterImportPeriod(vos, paramvo);

		checkIsHasRepeation(pk_corp);

		updateCcRecodeAfterTicket(corpvo);

		return repMap;
	}

	private void updateCcRecodeAfterTicket(CorpVO corpvo){
//		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
//		taxvo.setGolddiskno(jspbh);
//		if(StringUtil.isEmpty(taxvo.getPrimaryKey())){
//			singleObjectBO.saveObject(pk_corp, taxvo);
//		}else{
//			singleObjectBO.update(taxvo, new String[]{ "golddiskno" });
//		}
//
		singleObjectBO.update(corpvo, new String[]{ "vsoccrecode", "fax2" });
	}

	private VATInComInvoiceVO[] buildBusiData(Map<String, VATInComInvoiceVO> repMap) {
		if (repMap == null || repMap.size() == 0) {
			return null;
		}

		VATInComInvoiceVO vo = null;
		List<VATInComInvoiceVO> list = new ArrayList<VATInComInvoiceVO>();
		for (Map.Entry<String, VATInComInvoiceVO> entry : repMap.entrySet()) {
			vo = entry.getValue();

			list.add(vo);
		}

		return list.size() == 0 ? null : list.toArray(new VATInComInvoiceVO[0]);
	}

	private CorpVO getCorpVO(String pk_corp) {
		CorpVO corpvo = (CorpVO) singleObjectBO.queryByPrimaryKey(CorpVO.class, pk_corp);

		corpvo.setUnitname(CodeUtils1.deCode(corpvo.getUnitname()));

		return corpvo;
	}

	private void beforeCallCheck(String nsrsbh, String unitname, DZFDate loginDate) {
		if (StringUtil.isEmpty(nsrsbh)) {
			throw new BusinessException("纳税人识别号为空,请维护");
		}
		if (StringUtil.isEmpty(unitname)) {
			throw new BusinessException("公司名称为空,请维护");
		}
//		if (StringUtil.isEmpty(golddiskno)) {
//			throw new BusinessException("金税盘口令为空，请维护");
//		}

		if (loginDate == null) {
			throw new BusinessException("登录期间解析错误，请联系管理员");
		}
	}

	@Override
	public VATInComInvoiceVO queryByCGTId(String fphm, String fpdm, String pk_corp) throws DZFWarpException {

		StringBuffer sf = new StringBuffer();
		sf.append(" select pk_vatincominvoice, fp_hm, fp_dm, pk_tzpz_h ");
		sf.append(" from ynt_vatincominvoice where ");
		sf.append(" nvl(dr,0) = 0 and pk_corp = ? and fp_hm = ? and fp_dm = ? ");

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(fphm);
		sp.addParam(fpdm);

		VATInComInvoiceVO vo = (VATInComInvoiceVO) singleObjectBO.executeQuery(sf.toString(), sp,
				new BeanProcessor(VATInComInvoiceVO.class));

		return vo;
	}

	@Override
	public List<VATInComInvoiceVO> queryByPks(String[] pks, String pk_corp) throws DZFWarpException {

		String wherePart = SqlUtil.buildSqlForIn("pk_vatincominvoice", pks);

		SQLParameter sp = new SQLParameter();
		StringBuffer sb = new StringBuffer();
		sb.append(
				"  select y.pk_vatincominvoice, y.coperatorid, y.doperatedate, y.pk_corp, y.batchflag, y.iszhuan, y.fp_hm, y.fp_dm, y.xhfmc, y.spmc, ");
		sb.append(
				" y.spsl, y.spse, y.hjje, y.jshj, y.yfp_hm, y.yfp_dm, y.kprj, y.rzrj, y.rzjg, y.pk_tzpz_h, y.period, y.billstatus, y.modifyoperid, ");
		sb.append(" y.modifydatetime, y.pk_model_h, y.busitypetempname, y.sourcetype, y.dr, y.ts, h.pzh ");
		sb.append("    from ynt_vatincominvoice y ");
		sb.append("    left join ynt_tzpz_h h ");
		sb.append("      on y.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append(" where y.pk_corp=? and nvl(y.dr,0)=0");
		sp.addParam(pk_corp);

		List<VATInComInvoiceVO> listvo = (List<VATInComInvoiceVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(VATInComInvoiceVO.class));

		return listvo;
	}

	@Override
	public List<TaxitemVO> queryTaxItems(String pk_corp) throws DZFWarpException {
		List<TaxitemVO> vos = sys_taxsetserv.queryAllTaxitems();// 查询税目档案

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
			if (chargename.equals(vo.getChargedeptname()) && "2".equals(vo.getTaxstyle())) {// 资产类：库存商品、原材料
																							// 损益类：成本、费用
				aftervos.add(vo);
			}
		}

		return aftervos;
	}

	/**
	 * 1 生成入库单 2 规则入库单规则 生成凭证
	 */
	@Override
	public IntradeHVO createIC(VATInComInvoiceVO vo, YntCpaccountVO[] accounts, CorpVO corpvo, String userid)
			throws DZFWarpException {
		String pk_corp = corpvo.getPk_corp();

		// 判断
//		DZFBoolean isic = vo.getIsic();
//		if (isic != null && isic.booleanValue()) {
//			throw new BusinessException("已生成入库单，无需再次生成");
//		}
		String pk_ictrade_h = vo.getPk_ictrade_h();
		if(!StringUtil.isEmpty(pk_ictrade_h)){
			throw new BusinessException("已生成入库单，无需再次生成");
		}

		String pk_model_h = vo.getPk_model_h();
		if (StringUtil.isEmpty(pk_model_h)) {
			throw new BusinessException("尚未进行入账设置,请检查");
		}

		List<DcModelBVO> modelBList = dcpzjmbserv.queryByPId(pk_model_h, pk_corp);

		if (modelBList == null || modelBList.size() == 0)
			throw new BusinessException("尚未进行入账设置,请检查");

		int len = modelBList == null ? 0 : modelBList.size();
		boolean flag = false;
		DcModelBVO modelbvo = null;
		String kmbm = null;
		String kcsp = gl_cbconstant.getKcsp_code();
		String ycl = gl_cbconstant.getYcl_code();
		for (int i = 0; i < len; i++) {
			modelbvo = modelBList.get(i);
			kmbm = modelbvo.getKmbm();
			if (!StringUtil.isEmpty(kmbm)) {
				if (kmbm.startsWith(kcsp)) {
					kmbm = kcsp;
					flag = true;
					break;
				} else if (kmbm.startsWith(ycl)) {
					kmbm = ycl;
					flag = true;
					break;
				}
			}

		}

		if (!flag) {
			throw new BusinessException("入账设置的科目未找到原材料或库存商品,请检查");
		}

		YntCpaccountVO cpavo = null;
		for (YntCpaccountVO acc : accounts) {
			if (kmbm.equalsIgnoreCase(acc.getAccountcode()))
				cpavo = acc;
		}

		IntradeHVO ichvo = buildIntradeHVO(vo, cpavo, corpvo, userid);

		ic_purchinserv.save(ichvo, false);// 保存
		// 更新状态
		updateICStatus(vo.getPk_vatincominvoice(), pk_corp, ichvo.getPk_ictrade_h());

		return ichvo;
	}

	@Override
	public void updateICStatus(String pk_vatincominvoice, String pk_corp, String pk_ictrade_h) throws DZFWarpException {
		String sql = " update ynt_vatincominvoice set pk_ictrade_h = ? where pk_vatincominvoice = ? and pk_corp = ? and nvl(dr,0) = 0 ";

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_ictrade_h);
		sp.addParam(pk_vatincominvoice);
		sp.addParam(pk_corp);

		singleObjectBO.executeUpdate(sql, sp);
	}

	@Override
	public void saveGL(IntradeHVO hvo, String pk_corp, String userid) throws DZFWarpException {
		 CorpVO cvo = corpService.queryByPk(pk_corp);
		ic_purchinserv.saveIntradeHVOToZz(hvo, cvo);// 转总账
	}

	@Override
	public void saveTotalGL(IntradeHVO[] vos, String pk_corp, String userid) throws DZFWarpException {
		 CorpVO cvo = corpService.queryByPk(pk_corp);
		ic_purchinserv.saveIntradeHVOToZz(vos, cvo);// 汇总转总账
	}

	private IntradeHVO buildIntradeHVO(VATInComInvoiceVO vo, YntCpaccountVO cpavo, CorpVO corpvo, String userid) {
		VATInComInvoiceBVO[] inbodyvos = (VATInComInvoiceBVO[]) vo.getChildren();

		if (inbodyvos == null || inbodyvos.length == 0) {
			throw new BusinessException("进项表体行为空，不能生成入库单");
		}

		// 存货关系设置
		InvAccSetVO setvo = ic_chkmszserv.query(corpvo.getPk_corp());
		if (setvo == null) {
			throw new BusinessException("该公司存货关系设置不存在,请设置后再试!");
		}

		List<IctradeinVO> icList = new ArrayList<IctradeinVO>();
		String pk_corp = corpvo.getPk_corp();
		String chargedeptname = corpvo.getChargedeptname();
//		DZFBoolean flag = "一般纳税人".equals(chargedeptname) ? DZFBoolean.TRUE : DZFBoolean.FALSE;

		IctradeinVO icbvo = null;

		AuxiliaryAccountBVO suppliervo = null;// 供应商辅助
		InventoryVO inventoryvo = null;// 存货辅助

		String xfmc = null;
		String spmc = null;
		for (VATInComInvoiceBVO inbvo : inbodyvos) {
			spmc = inbvo.getBspmc();
			if (StringUtil.isEmpty(spmc))
				continue;

			inventoryvo = matchInvtoryIC(vo, inbvo, corpvo, cpavo);

			icbvo = new IctradeinVO();
			icbvo.setPk_inventory(inventoryvo.getPk_inventory());
			icbvo.setPk_subject(inventoryvo.getPk_subject());
			icbvo.setNnum(inbvo.getBnum());

//			if (flag != null && flag.booleanValue()) {
				icbvo.setNprice(inbvo.getBprice());
				icbvo.setNymny(inbvo.getBhjje());// 金额
				icbvo.setNtax(inbvo.getBspsl());
				icbvo.setNtaxmny(inbvo.getBspse());
				icbvo.setNtotaltaxmny(SafeCompute.add(icbvo.getNymny(), icbvo.getNtaxmny()));// 价税合计
//			} else {// 小规模纳税人金额 取价税合计金额
//				icbvo.setNymny(SafeCompute.add(inbvo.getBhjje(), inbvo.getBspse()));// 金额
//				icbvo.setNprice(SafeCompute.div(icbvo.getNymny(), icbvo.getNnum()));// 反算单价
//			}

			icList.add(icbvo);
		}

		if (icList.size() == 0) {
			throw new BusinessException("进项表体行货物或应税劳务名称为空，不能生成入库单");
		}

		IntradeHVO ichvo = new IntradeHVO();
		xfmc = vo.getXhfmc();
		if (!StringUtil.isEmpty(xfmc)) {
			suppliervo = matchSupplier(vo, pk_corp, userid, AuxiliaryConstant.ITEM_SUPPLIER);
			ichvo.setPk_cust(suppliervo.getPk_auacount_b());
		}

		if (StringUtil.isEmpty(vo.getInperiod())) {
			ichvo.setDbilldate(vo.getKprj());
		} else {
			ichvo.setDbilldate(DateUtils.getPeriodEndDate(vo.getInperiod()));
		}

		ichvo.setDbillid(null);// 设置为空，调用入库接口会重新生成
		ichvo.setCbusitype(IcConst.CGTYPE);// 采购入库
		ichvo.setIpayway(setvo.getCgfkfs());// // 取自库存入账设置节点
		ichvo.setPk_corp(pk_corp);
		ichvo.setIarristatus(1);

		// ichvo.setPk_bankaccount();
		ichvo.setDinvdate(vo.getKprj());
		ichvo.setDinvid(vo.getFp_hm());
		ichvo.setCreator(userid);
		ichvo.setIszg(DZFBoolean.FALSE);// 不是暂估
		if(DZFValueCheck.isNotEmpty(vo.getRzjg()) && vo.getRzjg().intValue()==1){
			ichvo.setIsrz(DZFBoolean.TRUE);
		}

		// 单据来源
		ichvo.setSourcebilltype(IBillTypeCode.HP95);
		ichvo.setSourcebillid(vo.getPrimaryKey());
		ichvo.setPk_image_group(vo.getPk_image_group());
		ichvo.setPk_image_library(vo.getPk_image_library());
		ichvo.setFp_style(getFpStyle(vo));// 1普票 2专票3未开票
//		DZFBoolean iszh = getIsZhuan(corpvo.getChargedeptname(), vo.getIsZhuan());
//		ichvo.setFp_style(iszh.booleanValue() ? 2 : 1);// 1普票 2专票3未开票

		ichvo.setChildren(icList.toArray(new IctradeinVO[0]));

		return ichvo;
	}

	/*
	 * 专普票标识 当前公司是一般纳税人，如果进项发票是专票则生成凭证时，不带任何标识；如果进项发票是普票则生成凭证时默认勾选“普票”标识
	 * 当前公司是小规模纳税人，如果进项发票是专票则生成凭证时，默认勾选“专票”标识；如果进项发票是普票则生成凭证时不带任何标识
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

	private AuxiliaryAccountBVO matchSupplier(VATInComInvoiceVO vo, String pk_corp, String userid,
			String pk_auacount_h) {
		AuxiliaryAccountBVO suppliervo = null;

		String payer = vo.getXhfsbh();// 销方识别号
		String name = vo.getXhfmc();// 销货方名称
		String address = vo.getXhfdzdh();// 销货方地址电话
		String bank = vo.getXhfyhzh();// 销货方开户账号

		suppliervo = ocr_atuomatch.getAuxiliaryAccountBVOByName(name, pk_corp, pk_auacount_h);

		if (suppliervo == null) {
			if (!StringUtil.isEmpty(payer) && !"000000000000000".equalsIgnoreCase(payer)) {
				suppliervo = ocr_atuomatch.getAuxiliaryAccountBVOByTaxNo(payer, pk_corp, pk_auacount_h);
			}
		}

		if (suppliervo == null && !StringUtil.isEmpty(name)) {
			suppliervo = new AuxiliaryAccountBVO();
			suppliervo.setCode(yntBoPubUtil.getFZHsCode(pk_corp, pk_auacount_h));
			suppliervo.setName(name);
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

	// 凭证删除后 更新进项凭证号
	public void deletePZH(String pk_corp, String pk_tzpz_h) throws DZFWarpException {
		String sql = "update ynt_vatincominvoice y set y.pk_tzpz_h = null,y.pzh = null Where y.pk_tzpz_h = ? and pk_corp = ? ";

		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_tzpz_h);
		sp.addParam(pk_corp);

		singleObjectBO.executeUpdate(sql, sp);
	}

	public void updatePZH(TzpzHVO headvo) throws DZFWarpException {

		String condition = " nvl(dr,0)=0 and  cbilltype = ? and pzid = ? and pk_corp = ? ";
		SQLParameter sp = new SQLParameter();
		sp.addParam(IBillTypeCode.HP70);
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
			if (IBillTypeCode.HP95.equals(type)) {
				pks.add(hvo.getSourcebillid());
			}
		}

		if (pks.size() == 0)
			return;

		StringBuffer sf = new StringBuffer();
		sf.append("  update ynt_vatincominvoice y set y.pk_tzpz_h = ?,y.pzh = ? Where ");// y.pk_vatincominvoice
																							// =
																							// ?
		sf.append(SqlUtil.buildSqlForIn("pk_vatincominvoice", pks.toArray(new String[0])));
		sp = new SQLParameter();
		sp.addParam(headvo.getPrimaryKey());
		sp.addParam(headvo.getPzh());

		singleObjectBO.executeUpdate(sf.toString(), sp);

	}

	@Override
	public List<InventoryAliasVO> matchInventoryData(String pk_corp, VATInComInvoiceVO[] vos, InventorySetVO invsetvo)
			throws DZFWarpException {

		List<VATInComInvoiceVO> saleList = construcComInvoice(vos, pk_corp);

		if (saleList == null || saleList.size() == 0)
			throw new BusinessException("未找进项发票数据，请检查");

		AuxiliaryAccountBVO[] invenvos = gl_fzhsserv.queryB(AuxiliaryConstant.ITEM_INVENTORY, pk_corp, null);

		Map<String, AuxiliaryAccountBVO> invenMap = new LinkedHashMap<>();
		Map<String, InventoryAliasVO> invenMap1 = new LinkedHashMap<>();

		if (invenvos != null && invenvos.length > 0) {
			List<AuxiliaryAccountBVO> invenList = Arrays.asList(invenvos);
			invenMap = DZfcommonTools.hashlizeObjectByPk(invenList, new String[] { "name", "spec", "unit" });
			Map<String, AuxiliaryAccountBVO> tempMap = DZfcommonTools.hashlizeObjectByPk(invenList,
					new String[] { "pk_auacount_b" });
			invenMap1 = buildInvenMapModel7(tempMap, invenMap, pk_corp);
		}

		Map<String, VATInComInvoiceBVO> bvoMap = buildGoodsInvenRelaMapModel7(saleList, invsetvo);
		List<InventoryAliasVO> list = null;
		if (bvoMap != null && bvoMap.size() > 0) {
			list = new ArrayList<InventoryAliasVO>();
			Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);
			String key;
			AuxiliaryAccountBVO invenvo;
			VATInComInvoiceBVO bvo;
			InventoryAliasVO relvo = null;
			InventoryAliasVO relvo1 = null;
			for (Map.Entry<String, VATInComInvoiceBVO> entry : bvoMap.entrySet()) {
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
						if (invsetvo != null) {
							relvo.setChukukmid(invsetvo.getKcspckkm());
							relvo.setKmclassify(invsetvo.getKcsprkkm());
							YntCpaccountVO accvo = getAccountVO(accmap, relvo.getChukukmid());
							if (accvo != null) {
								accvo = getFisrtNextLeafAccount(accvo.getAccountcode(), accmap);
							}
							if (accvo != null) {
								relvo.setKmmc_sale(accvo.getAccountname());
								relvo.setChukukmcode(accvo.getAccountcode());
								relvo.setChukukmid(accvo.getPk_corp_account());
							}

							accvo = getAccountVO(accmap, relvo.getKmclassify());
							if (accvo != null) {
								accvo = getFisrtNextLeafAccount(accvo.getAccountcode(), accmap);
							}
							if (accvo != null) {
								relvo.setKmmc_invcl(accvo.getAccountname());
								relvo.setKmclasscode(accvo.getAccountcode());
								relvo.setKmclassify(accvo.getPk_corp_account());
							}
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
		return list;
	}

	private YntCpaccountVO getAccountVO(Map<String, YntCpaccountVO> accmap, String accid) {
		if (accmap == null || accmap.size() == 0)
			return null;
		return accmap.get(accid);
	}

	private Map<String, InventoryAliasVO> buildInvenMapModel7(Map<String, AuxiliaryAccountBVO> tempinMap,
			Map<String, AuxiliaryAccountBVO> invenMap, String pk_corp) {

		Map<String, InventoryAliasVO> invenMap1 = new LinkedHashMap<String, InventoryAliasVO>();

		List<InventoryAliasVO> list = queryInventoryAliasVO(pk_corp);
		Map<String, YntCpaccountVO> accmap = accountService.queryMapByPk(pk_corp);

		if (list != null && list.size() > 0) {
			String key;
			String pk_inventory;
			AuxiliaryAccountBVO avo = null;
			for (InventoryAliasVO vo : list) {
				key = vo.getAliasname() + "," + vo.getSpec() + "," + vo.getUnit();

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

	private Map<String, VATInComInvoiceBVO> buildGoodsInvenRelaMapModel7(List<VATInComInvoiceVO> saleList,
			InventorySetVO invsetvo) {

		int pprule = invsetvo.getChppjscgz();//匹配规则
		Map<String, VATInComInvoiceBVO> map = new LinkedHashMap<>();

		String key;
		VATInComInvoiceBVO[] bvos = null;

		for (VATInComInvoiceVO vo : saleList) {
			if(!StringUtil.isEmpty(vo.getPk_tzpz_h())){
				continue;
			}
			bvos = (VATInComInvoiceBVO[]) vo.getChildren();

			if (bvos != null && bvos.length > 0) {
				for (VATInComInvoiceBVO bvo : bvos) {
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

		int pprule = invsetvo.getChppjscgz();
		// Map<String, InventoryAliasVO> alimap = new LinkedHashMap<>();
		String key = null;
		for (InventoryAliasVO vo : vos) {
			//预处理
			if(pprule == InventoryConstant.IC_RULE_1){
				vo.setSpec(null);
				vo.setInvtype(null);
			}


			// 存货存在 是否新增别名
			if (!StringUtil.isEmpty(vo.getPk_inventory())) {
				AuxiliaryAccountBVO invvo = invenMap.get(vo.getPk_inventory());
				if (invvo == null) {
					throw new BusinessException("存货[" + vo.getName() + "]不存在或者被删除");
				}
				String invkey = getNameInvInfoKey(invvo);
				String aliaskey = getNameAliasInfoKey(vo);
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
				// 新增存货
				AuxiliaryAccountBVO bvo = new AuxiliaryAccountBVO();
				bvo = new AuxiliaryAccountBVO();
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
				String invkey = getNameInvInfoKey(bvo);

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
				String key1 = buildByRule(vo2.getName(),
						vo2.getSpec(), vo2.getUnit(), pprule);
				for (InventoryAliasVO vo1 : vos) {
//					key = vo1.getName() + "," + vo1.getSpec() + "," + vo1.getUnit();
					key = buildByRule(vo1.getName(),
							vo1.getSpec(), vo2.getUnit(), pprule);
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
			//添加修改存货日志
			AuxiliaryAccountBVO updateVO = (AuxiliaryAccountBVO) singleObjectBO.queryByPrimaryKey(AuxiliaryAccountBVO.class, ulistb.get(0).getPk_auacount_b());
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

	private void checkInventorySet(String pk_corp, List<AuxiliaryAccountBVO> nlistb, List<AuxiliaryAccountBVO> ulistb) {

		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		for (AuxiliaryAccountBVO vo2 : nlistb) {
			set1.add(vo2.getKmclassify());
			set2.add(vo2.getPk_auacount_b());
		}

		for (AuxiliaryAccountBVO vo2 : ulistb) {
			set1.add(vo2.getKmclassify());
			set2.add(vo2.getPk_auacount_b());
		}

		Map<String, Set<String>> pzkmidmap = new HashMap<String, Set<String>>();
		pzkmidmap.put("KMID", set1);
		pzkmidmap.put("CHID", set2);
		CorpVO cpvo = corpService.queryByPk(pk_corp);
		String err =inventory_setcheck.checkInventorySetCommon("", cpvo, pzkmidmap);
		if(!StringUtil.isEmpty(err)){
			throw new BusinessException(err);
		}

	}
	private String getNameInvInfoKey(AuxiliaryAccountBVO invo) {
		StringBuffer strb = new StringBuffer();
		strb.append(appendIsNull(invo.getName()));
		strb.append(appendIsNull(invo.getSpec()));
		strb.append(appendIsNull(invo.getInvtype()));
		strb.append(appendIsNull(invo.getUnit()));
		return strb.toString();

	}

	private String getNameAliasInfoKey(InventoryAliasVO invo) {
		StringBuffer strb = new StringBuffer();
		strb.append(appendIsNull(invo.getAliasname()));
		strb.append(appendIsNull(invo.getSpec()));
		strb.append(appendIsNull(invo.getInvtype()));
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

	//设置摘要
	private void setPzZy(List<TzpzBVO> tblist, VatInvoiceSetVO setvo, VATInComInvoiceVO vo){
		Map<String, Boolean> map = new HashMap<String, Boolean>();
		String zy = buildZy(setvo, vo, map);
		Boolean isaddZy = map.get("flag");
		if(isaddZy.booleanValue()){
			for(TzpzBVO bvo : tblist){
				bvo.setZy(zy);
			}
		}
	}

	@Override
	public void createPZ(VATInComInvoiceVO vo, String pk_corp, String userid, boolean accway, boolean isT,
			InventorySetVO invsetvo, VatInvoiceSetVO setvo, String jsfs) throws DZFWarpException {

		CorpVO corpvo = corpService.queryByPk(pk_corp);
		// 1-----普票 2----专票 3--- 未开票
		int fp_style = getFpStyle(vo);
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();
		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);
		headVO.setFp_style(fp_style);

		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
		tblist = createTzpzBVOJx(vo, userid, invsetvo, ccountMap, jsfs, fp_style);
		sortTzpz(tblist);
		//根据规则设置摘要
		setPzZy(tblist, setvo, vo);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		DZFDouble dfmny = DZFDouble.ZERO_DBL;

		for (TzpzBVO bvo : tblist) {
			dfmny = SafeCompute.add(dfmny, bvo.getDfmny());
		}

		vo.setCount(1);
		createTzpzHVO(headVO, vo, pk_corp, userid, null, null, dfmny, accway);
		if (vo.getPzstatus() != null) {
			headVO.setVbillstatus(vo.getPzstatus());
		}
		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));
		headVO.setPk_image_group(vo.getPk_image_group());
		updateImageGroup(vo.getPk_image_group());
		if (isT) {
			createTempPZ(headVO, new VATInComInvoiceVO[] { vo }, pk_corp);
		} else {
			headVO = voucher.saveVoucher(corpvo, headVO);
		}
		List<VATInComInvoiceVO> list = new ArrayList<>();
		list.add(vo);
		updateOperate(list, jsfs, pk_corp);// 存货
	}

	@Override
	public void saveCombinePZ(List<VATInComInvoiceVO> list, String pk_corp, String userid, VatInvoiceSetVO setvo,
			boolean accway, boolean isT, InventorySetVO invsetvo, String jsfs) throws DZFWarpException {
		CorpVO corpvo = corpService.queryByPk(pk_corp);

		int fp_style;
		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();

		// 发票设置专普票 销进项
		TzpzHVO headVO = new TzpzHVO();
		headVO.setIfptype(ifptype);

		DZFDate maxDate = null;
		List<String> imageGroupList = new ArrayList<>();
		Map<String, YntCpaccountVO> ccountMap = accountService.queryMapByPk(pk_corp);
		Integer pzstatus = null;
//		DZFBoolean iszhFlag = null;
		for (VATInComInvoiceVO vo : list) {
//			iszhFlag = getCheckZhuanFlag(vo, iszhFlag);

			fp_style = getFpStyle(vo);
			List<TzpzBVO> pzlist = createTzpzBVOJx(vo, userid, invsetvo, ccountMap, jsfs, fp_style);
			if (pzstatus == null) {
				if (vo.getPzstatus() != null) {
					pzstatus = vo.getPzstatus();
				}
			}

			//根据规则设置摘要
			setPzZy(pzlist, setvo, vo);

			tblist.addAll(pzlist);
			headVO.setFp_style(fp_style);
			// 找最大日期
			maxDate = getMaxDate(maxDate, vo);
			if (!StringUtil.isEmpty(vo.getPk_image_group())) {
				imageGroupList.add(vo.getPk_image_group());
			}


		}
		sortTzpz(tblist);
		// 合并同类项
		tblist = constructItems(tblist, setvo, pk_corp);

		DZFDouble totalMny = DZFDouble.ZERO_DBL;
		for (TzpzBVO bvo : tblist) {
			totalMny = SafeCompute.add(bvo.getJfmny(), totalMny);
		}

		VATInComInvoiceVO firstvo = list.get(0);
		if (maxDate != null) {
			firstvo.setInperiod(DateUtils.getPeriod(maxDate));
		}

		list.get(0).setCount(list.size());
		createTzpzHVO(headVO, list.get(0), pk_corp, userid, null, null, totalMny, accway);
		if (pzstatus != null) {
			headVO.setVbillstatus(pzstatus);
		}
		if (imageGroupList != null && imageGroupList.size() > 0) {
			// 合并图片组
//			String groupId = mergeImage(pk_corp, imageGroupList);
			String groupId = img_groupserv.processMergeGroup(pk_corp, null, imageGroupList);
			headVO.setPk_image_group(groupId);
			updateImageGroup(groupId);
		}
		// headvo sourcebillid 重新赋值
		List<String> pks = new ArrayList<String>();
		for (VATInComInvoiceVO svo : list) {
			pks.add(svo.getPrimaryKey());
		}
		String sourcebillid = SqlUtil.buildSqlConditionForInWithoutDot(pks.toArray(new String[pks.size()]));

		headVO.setChildren(tblist.toArray(new TzpzBVO[0]));

		if (isT) {
			createTempPZ(headVO, list.toArray(new VATInComInvoiceVO[list.size()]), pk_corp);
		} else {
			headVO.setSourcebillid(sourcebillid);
			headVO = voucher.saveVoucher(corpvo, headVO);
		}

		updateOperate(list, jsfs, pk_corp);// 存货
	}

	private List<TzpzBVO> createTzpzBVOJx(VATInComInvoiceVO vo, String userid, InventorySetVO invsetvo,
			Map<String, YntCpaccountVO> ccountMap, String jsfs, int fp_style) {
		List<TzpzBVO> finBodyList = new ArrayList<TzpzBVO>();
		List<TzpzBVO> bodyList = null;

		String pk_accsunj = null;

		if (invsetvo == null)
			throw new BusinessException("存货设置未设置!");
		int chcbjzfs = invsetvo.getChcbjzfs();

		if (chcbjzfs == 0) {// 明细
			pk_accsunj = invsetvo.getKcsprkkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("入库科目未设置!");
			}
		} else if (chcbjzfs == 1) {// 大类

		} else if (chcbjzfs == 2) {// 不核算明细
			pk_accsunj = invsetvo.getKcsprkkm();
			if (StringUtil.isEmpty(pk_accsunj)) {
				throw new BusinessException("入库科目未设置!");
			}
		} else {
			throw new BusinessException("存货成本结转方式出错!");
		}

		// 公司性质为一般纳税人，增值税专用发票，
		// 根据存货绑定的类别科目入账取“金额”，税额发票“税额”，结算方式默认往来取“总金额”。
		// 公司性质为小规模，根据存货绑定的类别科目入账取“总金额”，不体现税额，结算方式默认往来取“总金额”

		CorpVO corpvo = corpService.queryByPk(vo.getPk_corp());

		String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? SMALL_TAX : corpvo.getChargedeptname();

		boolean isContainTax = true;
		if (!SMALL_TAX.equals(chargedeptname)) {
			if (fp_style == IFpStyleEnum.SPECINVOICE.getValue()) {
				isContainTax = false;
			}
		}
		String zy = null;
		if (StringUtil.isEmpty(vo.getXhfmc())) {
			zy = "向供应商采购商品";
		} else {
			zy = "向" + vo.getXhfmc() + "公司采购商品";
		}

		if (isContainTax) {
			bodyList = createCommonTzpzBVO(vo, pk_accsunj, userid, zy, "bhjje&bspse", 0, ccountMap, chcbjzfs, 0, true);
			pk_accsunj = null;
			finBodyList.addAll(bodyList);//
		} else {
			bodyList = createCommonTzpzBVO(vo, pk_accsunj, userid, zy, "bhjje", 0, ccountMap, chcbjzfs, 0, true);
			pk_accsunj = null;
			finBodyList.addAll(bodyList);//
			// 进项税额
			String jxsekm = invsetvo.getJxshuiekm();
			if (StringUtil.isEmpty(jxsekm)) {
				throw new BusinessException("进项税额科目未设置!");
			}
			bodyList = createCommonTzpzBVO(vo, jxsekm, userid, zy, "bspse", 0, ccountMap, chcbjzfs, 1, false);
			pk_accsunj = null;
			finBodyList.addAll(bodyList);//
		}

		if ("01".equals(jsfs)) {// 往来科目
			pk_accsunj = invsetvo.getYingfukm();
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
		bodyList = createCommonTzpzBVO(vo, pk_accsunj, userid, zy, "bhjje&bspse", 1, ccountMap, chcbjzfs, 2, false);
		pk_accsunj = null;
		finBodyList.addAll(bodyList);//
		return finBodyList;
	}

	private List<TzpzBVO> createCommonTzpzBVO(VATInComInvoiceVO vo, String pk_accsubj, String userid, String zy,
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
			VATInComInvoiceBVO ibody = (VATInComInvoiceBVO) body;
			String inv = ibody.getPk_inventory();

			if (chcbjzfs == 1) {// 大类
				if (checkinv) {
					// 获取存货科目 入库科目
					pk_accsubj = ibody.getPk_accsubj();
					if (StringUtil.isEmpty(pk_accsubj)) {
						throw new BusinessException("存货类别科目为空!");
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

					if (StringUtil.isEmpty(vo.getXhfmc())) {
						zy = "向供应商采购" + spmc;
					} else {
						zy = "向" + vo.getXhfmc() + "公司采购" + spmc;
					}
				}
			}
			// 金额为零的 不记录凭证行
			TzpzBVO bvo = createSingleTzpzBVO(cvo, zy, vo, vdirect, nmny, ibody, chcbjzfs,
					newRowno, userid, checkinv);
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

	private TzpzBVO createSingleTzpzBVO(YntCpaccountVO cvo, String zy, VATInComInvoiceVO vo, int vdirect,
			DZFDouble totalDebit,VATInComInvoiceBVO ibody , int chcbjzfs,
			int newRowno, String userid, boolean checkinv) {
		String priceStr = parameterserv.queryParamterValueByCode(vo.getPk_corp(), IParameterConstants.DZF010);
		int iprice = StringUtil.isEmpty(priceStr) ? 4 : Integer.parseInt(priceStr);
		TzpzBVO depvo = new TzpzBVO();
		depvo.setPk_accsubj(cvo.getPk_corp_account());
		depvo.setVcode(cvo.getAccountcode());
		depvo.setVname(cvo.getAccountname());
		depvo.setZy(zy);// 摘要
		depvo.setRowno(newRowno);

		if (cvo.getIsfzhs().charAt(0) == '1') {
			AuxiliaryAccountBVO bvo = matchSupplier(vo, vo.getPk_corp(), userid, AuxiliaryConstant.ITEM_CUSTOMER);
			if (bvo == null || StringUtil.isEmpty(bvo.getPk_auacount_b())) {
				// throw new BusinessException("科目【" + cvo.getAccountname() +
				// "】启用客户辅助核算,发票号"+vo.getFp_hm()+"购方名称必须录入!");
				vo.setPzstatus(IVoucherConstants.TEMPORARY);
			}
			if (bvo != null)
				depvo.setFzhsx1(bvo.getPk_auacount_b());
		}

		if (cvo.getIsfzhs().charAt(1) == '1') {
			AuxiliaryAccountBVO bvo = matchSupplier(vo, vo.getPk_corp(), userid, AuxiliaryConstant.ITEM_SUPPLIER);
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

//		DZFDouble taxratio = SafeCompute.div(ibody.getBspsl(), new DZFDouble(100));
//		TaxitemParamVO taxparam = new TaxitemParamVO.Builder(depvo.getPk_corp(), taxratio).UserId(vo.getCoperatorid())
//				.InvName(ibody.getBspmc()).Fp_style(null).build();
//		TaxItemUtil.dealTaxItem(depvo, taxparam, cvo);
		return depvo;
	}

	private void updateOtherType(List<VATInComInvoiceVO> list) {
		if (list == null || list.size() == 0)
			return;

		for (VATInComInvoiceVO vo : list) {
			vo.setIoperatetype(22);
		}
		singleObjectBO.updateAry(list.toArray(new VATInComInvoiceVO[list.size()]), new String[] { "ioperatetype" });
	}

	private void updateOperate(List<VATInComInvoiceVO> list, String jsfs, String pk_corp) {
		if (list == null || list.size() == 0)
			return;

		uglyDeal(list, jsfs, pk_corp, 21);

//		for (VATInComInvoiceVO vo : list) {
//			vo.setIoperatetype(21);
//			vo.setPk_model_h(null);
//			vo.setBusitypetempname(null);
//		}
		singleObjectBO.updateAry(list.toArray(new VATInComInvoiceVO[list.size()]),
				new String[] { "ioperatetype", "busitypetempname", "inperiod", "pk_model_h" });
	}

	private void uglyDeal(List<VATInComInvoiceVO> list, String jsfs, String pk_corp, int opeType){
		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);
		if(dcList == null || dcList.size() == 0){
			return;
		}
//		dcList = new DcPzmb().filterDataCommon(dcList,
//				pk_corp, null, null, "Y", null);

		Map<String, DcModelHVO> dcmap = DZfcommonTools.hashlizeObjectByPk(dcList,
				new String[]{"busitypetempname", "vspstylecode", "szstylecode"});

		String key;
		String spname = "采购库存商品";
		String vscode;//类型
		DcModelHVO dcvo;
		//01 往来科目  02 银行科目 03现金结算
		String szcode = "01".equals(jsfs) ?
					FieldConstant.SZSTYLE_06 : "02".equals(jsfs)
						? FieldConstant.SZSTYLE_04 : FieldConstant.SZSTYLE_02;//结算方式
		for(VATInComInvoiceVO vo : list){
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
	public String saveBusiPeriod(VATInComInvoiceVO[] vos,
			String pk_corp,
			String[] args) throws DZFWarpException {
		// 期间关账检查
		String tip = args[0];//提示信息
		String filed = args[1];//字段
		String arg1 = args[2];//期间

		boolean isgz = qmgzService.isGz(pk_corp, arg1);
		if (isgz) {
			throw new BusinessException("所选" + tip + arg1 + "已关账，请检查。");
		}

		StringBuffer msg = new StringBuffer();

		BigDecimal repeatCodeNum = gl_yhdzdserv.checkIsQjsyjz(pk_corp, arg1);
		if(repeatCodeNum != null && repeatCodeNum.intValue() > 0) {
			msg.append("<p>所选" + tip + arg1 + "已损益结转。</p>");
		}


		VATInComInvoiceVO newVO = null;
		int upCount = 0;
		int npCount = 0;

		StringBuffer part = new StringBuffer();
		List<VATInComInvoiceVO> list = new ArrayList<VATInComInvoiceVO>();
		boolean flag = false;
		String[] upFild = null;
		if("rzrj".equals(filed)){
			upFild = new String[]{"rzjg", "rzrj"};
		}else{
			upFild = new String[]{ filed };
		}

		for (VATInComInvoiceVO vo : vos) {

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

			newVO = new VATInComInvoiceVO();
			newVO.setPk_vatincominvoice(vo.getPk_vatincominvoice());
			if("rzrj".equals(filed)){
				newVO.setRzjg(1);
				newVO.setRzrj(DateUtils.getPeriodEndDate(arg1));
			}else{
				newVO.setAttributeValue(filed, arg1);
			}

			upCount++;
			list.add(newVO);
		}

		if (list != null && list.size() > 0 && upFild != null && upFild.length > 0) {
			singleObjectBO.updateAry(list.toArray(new VATInComInvoiceVO[0]), upFild);
		}

		msg.append("<p>" + tip + "更新成功 ").append(upCount).append(" 条")
				.append(npCount > 0 ? ",未更新 " + npCount + " 条。未更新详细原因如下:</p>" + part.toString() : "</p>");

		return msg.toString();
	}

	private Integer getFpStyle(VATInComInvoiceVO vo) {
		Integer fp_style = vo.getIszhuan() != null && vo.getIszhuan().booleanValue() ? IFpStyleEnum.SPECINVOICE.getValue() : IFpStyleEnum.COMMINVOICE.getValue();
		return fp_style;
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

	public CorpVO chooseTicketWay(String pk_corp) throws DZFWarpException {
		CorpVO vo = getCorpVO(pk_corp);
		CorpVO corpVO = new CorpVO();
		corpVO.setVsoccrecode(vo.getVsoccrecode());
		corpVO.setFax2(vo.getFax2());
//		CorpTaxVo taxvo = sys_corp_tax_serv.queryCorpTaxVO(pk_corp);
//		taxvo.setVsoccrecode(vo.getVsoccrecode());

		return corpVO;
	}

	@Override
	public List<VatBusinessTypeVO> getBusiType(String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			return null;

		List<DcModelHVO> dcList = dcpzjmbserv.query(pk_corp);

		if(dcList == null || dcList.size() == 0){
			return null;
		}
		//过滤数据
		DcPzmbController mb = new DcPzmbController();
		mb.sortH(dcList, null, "Y", null);
		dcList = mb.filterDataCommon(dcList, pk_corp, null, "Y", null, null);

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
		sp.addParam(IBillManageConstants.HEBING_XXFP);
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
	}
}

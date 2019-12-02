package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ObjectProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.utils.VOUtil;
import com.dzf.zxkj.common.constant.AuxiliaryConstant;
import com.dzf.zxkj.common.constant.IBillTypeCode;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.common.constant.IVoucherConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.DzfUtil;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.AuxiliaryAccountBVO;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.DZFBalanceBVO;
import com.dzf.zxkj.platform.service.bdset.IAuxiliaryAccountService;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.report.IYntBoPubUtil;
import com.dzf.zxkj.platform.service.sys.*;
import com.dzf.zxkj.platform.service.zncs.IAutoMatchName;
import com.dzf.zxkj.platform.service.zncs.image.IBalanceService;
import com.dzf.zxkj.platform.service.zncs.image.IImageToVoucherService;
import com.dzf.zxkj.platform.util.zncs.MatchTypeEnum;
import com.dzf.zxkj.platform.util.zncs.TransPjlxTypeModel;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service("aitovoucherserv_default")
public abstract class DefaultImageToVoucherServiceImpl implements IImageToVoucherService {

	private Logger log = Logger.getLogger(this.getClass());

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IAutoMatchName ocr_atuomatch;
	@Autowired
	private IAuxiliaryAccountService gl_fzhsserv;
	@Autowired
	protected IYntBoPubUtil yntBoPubUtil;
	@Autowired
	protected IDcpzService dcpzjmbserv;

	@Autowired
	private IBalanceService balanceServ;

	@Autowired
	private IFctpubService fctPubServ;

	@Autowired
	private IVersionMngService versionServ;

	@Autowired
	private ICpaccountService cpaccountService;

	@Autowired
	private ICorpService corpService;
	@Autowired
	private IAccountService accountService;

	public static String PK_USD = "002JRR00A1100000000000RT";

	@Override
	public TzpzHVO saveTzpzVO(ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo, TzpzHVO hvo, boolean isRecog)
			throws DZFWarpException {
		// ocr识别成功的 调用收费接口
		shoufei(grpvo, hvo, hvo.getDoperatedate());
		return hvo;
	}

	public TzpzHVO creatTzpzVO(List<OcrInvoiceVO> list, ImageGroupVO grpvo) throws DZFWarpException {

		if (list == null || list.size() == 0)
			return null;

		String pk_corp = grpvo.getPk_corp();
		CorpVO corpvo = corpService.queryByPk(grpvo.getPk_corp());

		// 0-- 销项 1---进项 -2---其他
		int ifptype = matchImageType(list.get(0), corpvo, grpvo);

		// 不确定发票类型返回
		if (ifptype == VATInvoiceTypeConst.UNDETERMINED_INVOICE) {
			throwBusinessException();
		}

		// 1-----普票 2----专票 3--- 未开票
		Integer fp_style = getFp_style(grpvo.getPk_corp(), list.get(0));

		// 发票设置专普票 销进项
		TzpzHVO hvo = new TzpzHVO();
		hvo.setIfptype(ifptype);
		hvo.setFp_style(fp_style);

		DcModelBVO[] models = getDcModelBVO(hvo, grpvo, list.get(0), corpvo);

		if (models == null || models.length == 0) {
			throw new BusinessException("未匹配到凭证模板");
		}

		List<TzpzBVO> tzpzblist = createTzpzBVO(hvo, pk_corp, models, list);

		createTzpzHVO(hvo, grpvo);
		hvo.setPk_model_h(models[0].getPk_model_h());

		if (hvo == null || tzpzblist == null || tzpzblist.size() < 2) {
			throw new BusinessException("生成凭证出错！");
		}

		DZFDouble dfmny = DZFDouble.ZERO_DBL;

		for (TzpzBVO bvo : tzpzblist) {
			dfmny = SafeCompute.add(dfmny, bvo.getDfmny());
		}
		hvo.setDfmny(dfmny);
		hvo.setJfmny(dfmny);

		hvo.setChildren(tzpzblist.toArray(new TzpzBVO[0]));

		return hvo;
	}

	// 根据关键字获取指定模板
	@Override
	public DcModelBVO[] getDcModelBVO(TzpzHVO headVO, ImageGroupVO grpvo, OcrInvoiceVO invvo, CorpVO corpvo) {
		DcModelBVO[] bvos = getCorpOrGroupModelByName(invvo.getVfirsrinvname(), corpvo);
		return bvos;
	}

	// 根据类型 获取指定模板
	protected DcModelBVO[] getCorpOrGroupModel(Map<String, String> trmap, CorpVO corpvo) {
		if (trmap == null || trmap.size() == 0)
			return null;

		DcModelBVO[] models = getModelBVO(trmap, corpvo, corpvo.getPk_corp());
		if (models == null || models.length == 0) {
			models = getModelBVO(trmap, corpvo, IDefaultValue.DefaultGroup);
		}
		return models;
	}

	protected DcModelHVO[] queryJtModelHVOBycode(String pk_corp, String fpstylecode, String szstylecode,
												 String vmemocode) {
		CorpVO vo = corpService.queryByPk(pk_corp);
		String qyxz = null;
		if ("一般纳税人".equals(vo.getChargedeptname())) {
			qyxz = "一般纳税人";
		} else {
			qyxz = "小规模纳税人";
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(vo.getCorptype());
		sp.addParam(qyxz);
		sp.addParam(fpstylecode);
		sp.addParam(szstylecode);
		sp.addParam(vmemocode);
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0   ");
		sf.append(
				" and (pk_corp = ? and pk_trade_accountschema = ? and (chargedeptname = ? or chargedeptname is null))   ");
		sf.append(" and vspstylecode = ? and szstylecode = ? and  busitypetempname = ? ");
		sf.append(" order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode ");
		DcModelHVO[] ancevos = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		return ancevos;
	}

	protected DcModelHVO[] queryGsModelHVOBycode(String pk_corp, String fpstylecode, String szstylecode,
			String vmemocode) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(fpstylecode);
		sp.addParam(szstylecode);
		sp.addParam(vmemocode);
		DcModelHVO[] gsdaga = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class,
				" nvl(dr,0) = 0 and pk_corp = ?  and vspstylecode = ? and szstylecode = ? and  busitypetempname = ?  order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode",
				sp);
		return gsdaga;
	}

	private DcModelBVO[] getModelBVO(Map<String, String> trmap, CorpVO corpvo, String pk_corp) {
		List<DcModelHVO> dcmodelhList = null;
		DcModelBVO[] bvos = null;
		String fpstylecode = trmap.get(TransPjlxTypeModel.fpstyle);
		String szstylecode = trmap.get(TransPjlxTypeModel.szstyle);
		String vmemocode = trmap.get(TransPjlxTypeModel.vbstype);
		DcModelHVO[] headvo = queryGsModelHVOBycode(pk_corp, fpstylecode, szstylecode, vmemocode);
		if (headvo == null || headvo.length == 0) {
			headvo = queryJtModelHVOBycode(pk_corp, fpstylecode, szstylecode, vmemocode);
		}
		if (headvo != null && headvo.length > 0) {
			dcmodelhList = new ArrayList<DcModelHVO>(Arrays.asList(headvo));
		}
		if (dcmodelhList == null || dcmodelhList.size() == 0)
			return null;
		List<DcModelBVO> models = dcpzjmbserv.queryByPId(dcmodelhList.get(0).getPk_model_h(), pk_corp);
		if (models != null && models.size() > 0) {// 排序
			Collections.sort(models, new Comparator<DcModelBVO>() {
				@Override
				public int compare(DcModelBVO o1, DcModelBVO o2) {
					return o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
				}
			});
			bvos = models.toArray(new DcModelBVO[0]);
		}
		return bvos;
	}

	public DcModelHVO getModelHVO(Map<String, String> trmap, CorpVO corpvo, String pk_corp) throws DZFWarpException {
		List<DcModelHVO> dcmodelhList = null;
		String fpstylecode = trmap.get(TransPjlxTypeModel.fpstyle);
		String szstylecode = trmap.get(TransPjlxTypeModel.szstyle);
		String vmemocode = trmap.get(TransPjlxTypeModel.vbstype);
		DcModelHVO[] headvo = queryGsModelHVOBycode(pk_corp, fpstylecode, szstylecode, vmemocode);
		if (headvo == null || headvo.length == 0) {
			headvo = queryJtModelHVOBycode(pk_corp, fpstylecode, szstylecode, vmemocode);
		}
		if (headvo != null && headvo.length > 0) {
			dcmodelhList = new ArrayList<DcModelHVO>(Arrays.asList(headvo));
		}
		if (dcmodelhList == null || dcmodelhList.size() == 0)
			return null;

		return dcmodelhList.get(0);
	}

	// 单据类型
	protected abstract int matchImageType(OcrInvoiceVO vo, CorpVO corpvo, ImageGroupVO grpvo);

	/**
	 * 设置发票类型 1 普票 2专票 3未开票
	 * 
	 * @param pk_corp
	 * @param vo
	 * @return
	 */
	protected Integer getFp_style(String pk_corp, OcrInvoiceVO vo) {
		// 默认专票
		return VATInvoiceTypeConst.NON_INVOICE;
	}

	protected List<TzpzBVO> createTzpzBVO(TzpzHVO hvo, String pk_corp, DcModelBVO[] models, List<OcrInvoiceVO> list) {

		OcrInvoiceDetailVO[] detailvos = null;

		List<TzpzBVO> tblist = new ArrayList<TzpzBVO>();

		OcrImageLibraryVO lib = null;
		YntCpaccountVO[] accounts = accountService.queryByPk(pk_corp);

		for (OcrInvoiceVO vo : list) {
			detailvos = (OcrInvoiceDetailVO[]) vo.getChildren();
			if (detailvos == null || detailvos.length == 0) {
				detailvos = (OcrInvoiceDetailVO[]) singleObjectBO.queryByCondition(OcrInvoiceDetailVO.class,
						"nvl(dr,0) =0 and pk_invoice ='" + vo.getPk_invoice() + "'", null);
			}
			//
			speaclDealVO(vo, detailvos);
			lib = changeOcrInvoiceVO(vo, pk_corp, hvo);
			filterSpeaclCharacter(detailvos);
			getTzpzBVOList(hvo, models, lib, detailvos, tblist, accounts);
		}

		setSpeaclTzpzBVO(hvo, lib, tblist);

		sortTzpz(tblist);
		// 合并同类项
		if (tblist != null && tblist.size() > 1) {
			Map<String, TzpzBVO> tzpzmap = new LinkedHashMap<String, TzpzBVO>();
			String key = null;
			TzpzBVO tempbvo = null;
			List<TzpzBVO> finalList = new ArrayList<TzpzBVO>();
			for (TzpzBVO bvo : tblist) {
				key = constructTzpzKey(bvo);
				if (tzpzmap.containsKey(key)) {
					tempbvo = tzpzmap.get(key);
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
					finalList.add(bvo);
				}
			}
			tblist = finalList;
		}
		return tblist;
	}

	protected void speaclDealVO(OcrInvoiceVO vo, OcrInvoiceDetailVO[] details) {

	}

	protected void setSpeaclTzpzBVO(TzpzHVO hvo, OcrImageLibraryVO lib, List<TzpzBVO> tblist) {

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

	protected void filterSpeaclCharacter(OcrInvoiceDetailVO[] detailvos) {
		if (detailvos == null || detailvos.length == 0)
			return;
		for (OcrInvoiceDetailVO vo : detailvos) {
			if (!StringUtil.isEmpty(vo.getInvname())) {
				vo.setInvname(replaceBlank(vo.getInvname()));
			}

			if (!StringUtil.isEmpty(vo.getInvtype())) {
				vo.setInvtype(replaceBlank(vo.getInvtype()));
			}

			if (!StringUtil.isEmpty(vo.getItemunit())) {
				vo.setItemunit(replaceBlank(vo.getItemunit()));
			}
		}
	}

	private String replaceBlank(String str) {
		String dest = "";
		if (!StringUtil.isEmpty(str)) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;
	}

	public abstract void getTzpzBVOList(TzpzHVO hvo, DcModelBVO[] models, OcrImageLibraryVO vo,
			OcrInvoiceDetailVO[] details, List<TzpzBVO> tblist, YntCpaccountVO[] accounts) throws DZFWarpException;

	protected OcrImageLibraryVO changeOcrInvoiceVO(OcrInvoiceVO invvo, String pk_corp, TzpzHVO hvo) {
		OcrImageLibraryVO imageVO = new OcrImageLibraryVO();
		imageVO.setVinvoicecode(invvo.getVinvoicecode());
		imageVO.setVinvoiceno(invvo.getVinvoiceno());
		imageVO.setDinvoicedate(invvo.getDinvoicedate());
		imageVO.setInvoicetype(invvo.getInvoicetype());
		imageVO.setItype(hvo.getIfptype());
		imageVO.setNmny(invvo.getNmny());
		imageVO.setNtaxnmny(invvo.getNtaxnmny());
		imageVO.setNtotaltax(invvo.getNtotaltax());
		imageVO.setVpurchname(invvo.getVpurchname());
		imageVO.setVpurchtaxno(invvo.getVpurchtaxno());
		imageVO.setVpuropenacc(invvo.getVpuropenacc());
		imageVO.setVpurphoneaddr(invvo.getVpurphoneaddr());
		imageVO.setVpurbankname(invvo.getVpurbankname());
		imageVO.setVsalename(invvo.getVsalename());
		imageVO.setVsaletaxno(invvo.getVsaletaxno());
		imageVO.setVsaleopenacc(invvo.getVsaleopenacc());
		imageVO.setVsalephoneaddr(invvo.getVsalephoneaddr());
		imageVO.setVsalebankname(invvo.getVsalebankname());
		imageVO.setInvname(invvo.getVfirsrinvname());
		imageVO.setVmemo(invvo.getVmemo());
		imageVO.setDkbs(invvo.getDkbs());
		imageVO.setUniquecode(invvo.getUniquecode());
		imageVO.setPk_corp(pk_corp);
		imageVO.setIfpkind(hvo.getFp_style());
		imageVO.setIinvoicetype(hvo.getIfeetype());// 费用类型
		imageVO.setPk_model_h(invvo.getPk_model_h());
		imageVO.setKeywords(invvo.getKeywords());

		if (invvo.getItype() == null) {
			imageVO.setIsinterface(DZFBoolean.FALSE);
		} else {
			if (invvo.getItype().intValue() == 1 || invvo.getItype().intValue() == 2) {
				imageVO.setIsinterface(DZFBoolean.TRUE);
			} else {
				imageVO.setIsinterface(DZFBoolean.FALSE);
			}
		}
		return imageVO;
	}

	protected String constructTzpzKey(TzpzBVO bvo) {
		StringBuffer sf = new StringBuffer();
		sf.append("&").append(bvo.getPk_accsubj()).append("&").append(bvo.getPk_inventory()).append("&")
				.append(bvo.getPk_taxitem()).append("&").append(bvo.getVdirect());

		for (int i = 1; i <= 10; i++) {
			sf.append(bvo.getAttributeValue("fzhsx" + i)).append("&");
		}

		return sf.toString();

	}

	protected TzpzHVO createTzpzHVO(TzpzHVO headVO, ImageGroupVO grpvo) {
		String pk_corp = grpvo.getPk_corp();

		DZFDate date = grpvo.getCvoucherdate();
		headVO.setDr(0);
		headVO.setPk_corp(pk_corp);
		headVO.setPzlb(0);// 凭证类别：记账
		headVO.setCoperatorid(grpvo.getCoperatorid());
		headVO.setIshasjz(DZFBoolean.FALSE);
		headVO.setDoperatedate(date);
		// headVO.setIsInsert("Y");
		headVO.setIsocr(DZFBoolean.TRUE);
		headVO.setPzh(OcrVoucherNoGenerator.getPzh(pk_corp, date, grpvo));
		headVO.setVbillstatus(IVoucherConstants.TEMPORARY);// ocr生成的凭证
		headVO.setSourcebillid(grpvo.getPrimaryKey());
		headVO.setSourcebilltype(IBillTypeCode.HP110);// 来源
		headVO.setPeriod(date.toString().substring(0, 7));
		headVO.setVyear(Integer.valueOf(date.toString().substring(0, 4)));
		headVO.setIsfpxjxm(DZFBoolean.FALSE);
		headVO.setNbills(grpvo.getChildren().length);// 设置单据张数
		headVO.setPk_image_group(grpvo.getPrimaryKey());
		return headVO;
	}

	protected DcModelBVO[] getCorpOrGroupModelByName(String businame, CorpVO corpvo) {
		DcModelBVO[] models = getModelByName(businame, corpvo, corpvo.getPk_corp());
		if (models == null || models.length == 0) {
			models = getModelByName(businame, corpvo, IDefaultValue.DefaultGroup);
		}
		return models;
	}

	protected DcModelHVO[] queryJtModelHVOByname(CorpVO corpvo, String pk_corp, String businame) {
		CorpVO vo = corpService.queryByPk(pk_corp);
		String qyxz = null;
		if ("一般纳税人".equals(vo.getChargedeptname())) {
			qyxz = "一般纳税人";
		} else {
			qyxz = "小规模纳税人";
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(businame);
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(corpvo.getCorptype());
		sp.addParam(qyxz);
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0  and  busitypetempname = ? ");
		sf.append(
				" and (pk_corp = ? and pk_trade_accountschema = ? and (chargedeptname = ? or chargedeptname is null))   ");
		sf.append(" order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode ");
		DcModelHVO[] ancevos = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		return ancevos;
	}

	protected DcModelHVO[] queryGsModelHVOByname(String pk_corp, String businame) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(businame);
		DcModelHVO[] gsdaga = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class,
				" nvl(dr,0) = 0 and pk_corp = ?  and  busitypetempname = ? order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode",
				sp);
		return gsdaga;
	}

	private DcModelBVO[] getModelByName(String businame, CorpVO corpvo, String pk_corp) {
		List<DcModelHVO> dcmodelhList = null;
		DcModelBVO[] bvos = null;
		DcModelHVO[] headvo = null;

		if (!IDefaultValue.DefaultGroup.equals(pk_corp)) {
			headvo = queryGsModelHVOByname(pk_corp, businame);
		}

		if (headvo == null || headvo.length == 0) {
			headvo = queryJtModelHVOByname(corpvo, pk_corp, businame);
		}
		if (headvo != null && headvo.length > 0) {
			dcmodelhList = new ArrayList<DcModelHVO>(Arrays.asList(headvo));
		}
		if (dcmodelhList == null || dcmodelhList.size() == 0 || dcmodelhList.size() > 1)
			return null;
		List<DcModelBVO> models = dcpzjmbserv.queryByPId(dcmodelhList.get(0).getPk_model_h(), pk_corp);
		if (models != null && models.size() > 0) {// 排序
			Collections.sort(models, new Comparator<DcModelBVO>() {
				@Override
				public int compare(DcModelBVO o1, DcModelBVO o2) {
					return o1.getPrimaryKey().compareTo(o2.getPrimaryKey());
				}
			});
			bvos = models.toArray(new DcModelBVO[0]);
		}
		return bvos;
	}

	protected DcModelHVO[] queryJtModelHVOByKeyWord(String pk_corp, CorpVO corpvo, String businame) {
		CorpVO vo = corpService.queryByPk(pk_corp);
		String qyxz = null;
		if ("一般纳税人".equals(vo.getChargedeptname())) {
			qyxz = "一般纳税人";
		} else {
			qyxz = "小规模纳税人";
		}
		SQLParameter sp = new SQLParameter();
		sp.addParam(businame + "%");
		sp.addParam(IDefaultValue.DefaultGroup);
		sp.addParam(corpvo.getCorptype());
		sp.addParam(qyxz);
		StringBuffer sf = new StringBuffer();
		sf.append(" nvl(dr,0) = 0  and  busitypetempname like ? ");
		sf.append(
				" and (pk_corp = ? and pk_trade_accountschema = ? and (chargedeptname = ? or chargedeptname is null))   ");
		sf.append(" order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode ");
		DcModelHVO[] ancevos = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class, sf.toString(), sp);
		return ancevos;
	}

	protected DcModelHVO[] queryGsModelHVOByKeyWord(String pk_corp, String businame) {
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(businame + "%");
		DcModelHVO[] gsdaga = (DcModelHVO[]) singleObjectBO.queryByCondition(DcModelHVO.class,
				" nvl(dr,0) = 0 and pk_corp = ?  and  busitypetempname like ? order by pk_trade_accountschema, busitypetempcode, busitypetempname, vspstylecode, szstylecode",
				sp);
		return gsdaga;
	}

	protected YntCpaccountVO getAccountByCode(String code, YntCpaccountVO[] accounts, String newrule) {
		YntCpaccountVO account = null;
		for (YntCpaccountVO yntCpaccountVO : accounts) {
			if (yntCpaccountVO.getAccountcode().equals(code))
				account = yntCpaccountVO;
		}

		if (code.startsWith("2241")) {
			// 取不到找上级
			// 往上-up--递归查找
			if (account == null) {
				String upcode = getUpCode(code, newrule);
				if (!StringUtil.isEmpty(upcode)) {
					account = getAccountByCode(upcode, accounts, newrule);
				}
			}
		}
		return account;
	}

	protected YntCpaccountVO getTzpzAccount(String pk_accsubj, YntCpaccountVO[] accounts) {

		if (accounts == null || accounts.length == 0)
			return null;

		YntCpaccountVO cpaccvo = null;
		for (YntCpaccountVO accvo : accounts) {
			if (accvo.getPk_corp_account().equals(pk_accsubj)) {
				cpaccvo = accvo;
				break;
			}
		}
		return cpaccvo;
	}

	// 查询第一分支的最末级科目
	protected YntCpaccountVO getFisrtNextLeafAccount(YntCpaccountVO account, String pk_corp,
			YntCpaccountVO[] accounts) {

		List<YntCpaccountVO> list = new ArrayList<YntCpaccountVO>();// 存储下级科目

		for (YntCpaccountVO accvo : accounts) {
			if (accvo.getIsleaf().booleanValue() && accvo.getAccountcode() != null
					&& accvo.getAccountcode().startsWith(account.getAccountcode())) {
				list.add(accvo);
			}
		}

		if (list == null || list.size() == 0) {
			return account;
		}
		YntCpaccountVO[] accountvo = list.toArray(new YntCpaccountVO[list.size()]);
		VOUtil.ascSort(accountvo, new String[] { "accountcode" });
		return accountvo[0];
	}

	// 匹配科目 匹配不上新建
	protected YntCpaccountVO matchInvAccount(OcrImageLibraryVO vo, YntCpaccountVO account, String name,
			YntCpaccountVO[] accounts, String newrule) {

		if (vo == null || StringUtil.isEmpty(vo.getPk_corp()) || account == null)
			return null;

		String pcode = account.getAccountcode();
		YntCpaccountVO nextvo = null;
		if (vo.getIsinterface() != null && vo.getIsinterface().booleanValue()) {
			nextvo = getXJAccountVOByInvName(name, pcode, vo.getPk_corp(), accounts);
		} else {
			nextvo = getXJAccountVOByInvName(name, pcode, vo.getPk_corp(), accounts);
		}

		if (nextvo == null) {
			if (StringUtil.isEmpty(name)) {
				return account;
			}
			nextvo = getnext(account, pcode, accounts, name, vo.getPk_corp(), newrule);
			cpaccountService.saveNew(nextvo);
		}
		return nextvo;

	}

	// 匹配科目 匹配不上新建
	protected YntCpaccountVO matchAccount(OcrImageLibraryVO vo, YntCpaccountVO account, String name,
			YntCpaccountVO[] accounts, String newrule) {

		if (vo == null || StringUtil.isEmpty(vo.getPk_corp()) || account == null)
			return null;

		String pcode = account.getAccountcode();
		YntCpaccountVO nextvo = null;
		if (vo.getIsinterface() != null && vo.getIsinterface().booleanValue()) {
			nextvo = getXJAccountVOByName(name, pcode, vo.getPk_corp(), accounts);
		} else {
			nextvo = getXJAccountVOByName(name, pcode, vo.getPk_corp(), accounts);
		}

		if (nextvo == null) {
			if (StringUtil.isEmpty(name)) {
				return account;
			}
			nextvo = getnext(account, pcode, accounts, name, vo.getPk_corp(), newrule);
			cpaccountService.saveNew(nextvo);
		}
		return nextvo;

	}

	protected YntCpaccountVO getnext(YntCpaccountVO account, String pcode, YntCpaccountVO[] accounts, String name,
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

	/**
	 * 新增科目时获取新增科目的编码长度
	 * 
	 * @param {}
	 *            parentCode 上级科目编码
	 * @param {}
	 *            codeRule 科目编码规则
	 * @return {Number}
	 */
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

	protected YntCpaccountVO getParentVOByID(YntCpaccountVO svo) throws BusinessException {
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String accrule = gl_cpacckmserv.queryAccountRule(svo.getPk_corp());
		YntCpaccountVO vo = null;
		if (svo.getAccountcode().equals(DZfcommonTools.getFirstCode(svo.getAccountcode(), accrule)))
			return vo;
		String qrysql = " SELECT * FROM YNT_CPACCOUNT WHERE PK_CORP = ? AND ACCOUNTCODE = ? AND NVL(DR,0)=0 ";
		SQLParameter SP = new SQLParameter();
		SP.addParam(svo.getPk_corp());
		String parentcode = DZfcommonTools.getParentCode(svo.getAccountcode(), accrule);
		SP.addParam(parentcode);
		List<YntCpaccountVO> cpvos = (List<YntCpaccountVO>) singleObjectBO.executeQuery(qrysql, SP,
				new BeanListProcessor(YntCpaccountVO.class));
		if (cpvos != null && cpvos.size() > 0) {
			vo = cpvos.get(0);
		}
		return vo;
	}

	private YntCpaccountVO getXJAccountVOByInvName(String name, String pcode, String pk_corp,
			YntCpaccountVO[] accounts) {

		if (accounts == null || accounts.length == 0)
			return null;
		String accname = null;
		for (YntCpaccountVO accvo : accounts) {
			boolean ifleaf = accvo.getIsleaf() == null ? false : accvo.getIsleaf().booleanValue();
			if (ifleaf && accvo.getAccountcode().startsWith(pcode)) {
				accname = accvo.getAccountname();
				if (accname.equals(name)) {
					return accvo;
				}
			}
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

	// 匹配供应商 客户辅助
	protected AuxiliaryAccountBVO matchCustOrSupplier(OcrImageLibraryVO vo, int matchtype, String name, String payer,
													  String address, String bank) {

		if (vo == null || StringUtil.isEmpty(vo.getPk_corp()))
			return null;

		AuxiliaryAccountBVO bvo = null;

		// 如果销方发票 在去匹配购方单位 的客户辅助 不存在 新建
		String pk_auacount_h = null;

		if (MatchTypeEnum.CUSTOMER.getValue() == matchtype) {
			pk_auacount_h = AuxiliaryConstant.ITEM_CUSTOMER;

		} else if (MatchTypeEnum.SUPPLIER.getValue() == matchtype) {
			pk_auacount_h = AuxiliaryConstant.ITEM_SUPPLIER;
		}

		if (vo.getIsinterface() != null && vo.getIsinterface().booleanValue()) {
			bvo = ocr_atuomatch.getAuxiliaryAccountBVOByName(name, vo.getPk_corp(), pk_auacount_h);
		} else {
			bvo = ocr_atuomatch.autoMatchAuxiliaryAccount(name, matchtype, vo.getPk_corp());
		}
		if (bvo == null) {
			if (!"000000000000000".equalsIgnoreCase(payer)) {
				bvo = ocr_atuomatch.getAuxiliaryAccountBVOByTaxNo(payer, vo.getPk_corp(), pk_auacount_h);
			}
		}
		if (bvo == null && !StringUtil.isEmpty(name)) {
			bvo = new AuxiliaryAccountBVO();
			bvo.setCode(yntBoPubUtil.getFZHsCode(vo.getPk_corp(), pk_auacount_h));
			bvo.setName(name);
			bvo.setPk_corp(vo.getPk_corp());
			if (!"000000000000000".equalsIgnoreCase(payer)) {
				bvo.setTaxpayer(payer);
				if (VATInvoiceTypeConst.VAT_SALE_INVOICE == vo.getItype()
						|| VATInvoiceTypeConst.VAT_INCOM_INVOICE == vo.getItype()) {
					setFzInfo(bvo, address, bank);
				}
			}
			// System.out.println(name.length());
			bvo.setDr(0);
			bvo.setPk_auacount_h(pk_auacount_h);
			bvo = gl_fzhsserv.saveB(bvo);
		}
		return bvo;
	}

	// 匹配供应商 客户辅助
	// protected AuxiliaryAccountBVO matchCustOrSupplier(OcrImageLibraryVO vo,
	// OcrInvoiceDetailVO dvo, int matchtype) {
	//
	// if (vo == null || StringUtil.isEmpty(vo.getPk_corp()))
	// return null;
	//
	// AuxiliaryAccountBVO bvo = null;
	//
	// // 如果销方发票 在去匹配购方单位 的客户辅助 不存在 新建
	// String pk_auacount_h = null;
	// // String code = null;
	// String payer = null;
	// String name = null;
	//
	// // 地址
	// String address = null;
	// // 开户行
	// String bank = null;
	//
	// if (MatchTypeEnum.CUSTOMER.getValue() == matchtype) {
	// pk_auacount_h = AuxiliaryConstant.ITEM_CUSTOMER;
	// // code = "KH";
	// payer = vo.getVpurchtaxno();
	// name = vo.getVpurchname();//付款方名称
	// address = vo.getVpurphoneaddr();
	// bank = vo.getVpuropenacc();
	//
	// } else if (MatchTypeEnum.SUPPLIER.getValue() == matchtype) {
	// pk_auacount_h = AuxiliaryConstant.ITEM_SUPPLIER;
	// // code = "GY";
	// payer = vo.getVsaletaxno();
	// name = vo.getVsalename();//收款方名称
	// address = vo.getVsalephoneaddr();
	// bank = vo.getVsaleopenacc();
	// }
	//
	// if (vo.getIsinterface() != null && vo.getIsinterface().booleanValue() &&
	// dvo != null) {
	// bvo = ocr_atuomatch.getAuxiliaryAccountBVOByName(name, vo.getPk_corp(),
	// pk_auacount_h);
	// } else {
	// bvo = ocr_atuomatch.autoMatchAuxiliaryAccount(name, matchtype,
	// vo.getPk_corp());
	// }
	// if (bvo == null) {
	// if (!"000000000000000".equalsIgnoreCase(payer)) {
	// bvo = ocr_atuomatch.getAuxiliaryAccountBVOByTaxNo(payer, vo.getPk_corp(),
	// pk_auacount_h);
	// }
	// }
	// if (bvo == null && !StringUtil.isEmpty(name)) {
	// bvo = new AuxiliaryAccountBVO();
	// bvo.setCode(yntBoPubUtil.getFZHsCode(vo.getPk_corp(), pk_auacount_h));
	// bvo.setName(name);
	// bvo.setPk_corp(vo.getPk_corp());
	// if (!"000000000000000".equalsIgnoreCase(payer)) {
	// bvo.setTaxpayer(payer);
	// if (VATInvoiceTypeConst.VAT_SALE_INVOICE == vo.getItype()
	// || VATInvoiceTypeConst.VAT_INCOM_INVOICE == vo.getItype()) {
	// setFzInfo(bvo, address, bank);
	// }
	// }
	// // System.out.println(name.length());
	// bvo.setDr(0);
	// bvo.setPk_auacount_h(pk_auacount_h);
	// bvo = gl_fzhsserv.saveB(bvo);
	// }
	// return bvo;
	// }

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

	protected void setDefaultValue(TzpzBVO entry, DcModelBVO model, OcrImageLibraryVO vo, OcrInvoiceDetailVO dvo,
			YntCpaccountVO[] accounts) {

		YntCpaccountVO account = getTzpzAccount(entry.getPk_accsubj(),
				accountService.queryByPk(vo.getPk_corp()));
		if (account == null)
			throw new BusinessException("获取科目出错！");

		DZFDouble ybmny = DZFDouble.ZERO_DBL;
		String smny = null;
		String smny1 = null;
		if ("wsmny".equals(model.getVfield())) {// 无税金额
			if (dvo != null) {
				smny = dvo.getItemmny();
			} else {
				smny = vo.getNmny();
			}

		} else if ("smny".equals(model.getVfield())) {// 税额
			if (dvo != null) {
				smny = dvo.getItemtaxmny();
			} else {
				smny = vo.getNtaxnmny();
			}

		} else if ("totalmny".equals(model.getVfield())) {// 总金额
			if (dvo != null) {
				smny = dvo.getItemmny();
				smny1 = dvo.getItemtaxmny();
			} else {
				smny = vo.getNtotaltax();
			}
		} else if ("mny".equals(model.getVfield())) {// 金额
			if (dvo != null) {
				smny = dvo.getItemmny();
			} else {
				smny = vo.getNmny();
			}
		} else if ("hsmny".equals(model.getVfield())) {// 含税金额
			if (dvo != null) {
				smny = dvo.getItemmny();
				smny1 = dvo.getItemtaxmny();
			} else {
				smny = vo.getNtotaltax();
			}
		}
		ybmny = SafeCompute.add(getSubMny(smny1), getSubMny(smny));

		if (StringUtil.isEmpty(entry.getPk_currency())) {
			entry.setNrate(new DZFDouble(1));
			entry.setPk_currency(DzfUtil.PK_CNY);
		}
		if (model.getDirection() == 0) {// 借方
			entry.setJfmny(ybmny);
			entry.setYbjfmny(ybmny);
			entry.setVdirect(0);
		} else {// 贷方
			entry.setDfmny(ybmny);
			entry.setYbdfmny(ybmny);
			entry.setVdirect(1);
		}

		setZy(entry, model, vo, dvo);
		entry.setRowno(model.getRowno());
		entry.setPk_corp(vo.getPk_corp());
		entry.setDr(0);
	}

	private void setZy(TzpzBVO entry, DcModelBVO model, OcrImageLibraryVO vo, OcrInvoiceDetailVO dvo) {
		String invname = "";
		if (dvo != null) {
			invname = dvo.getInvname();
		} else {
			invname = vo.getInvname();
		}

		String zy = null;
		if (VATInvoiceTypeConst.VAT_BANK_INVOICE == vo.getItype()
				|| VATInvoiceTypeConst.VAT_RECE_INVOICE == vo.getItype()
				|| VATInvoiceTypeConst.VAT_PAY_INVOICE == vo.getItype()) {
			if (!StringUtil.isEmpty(vo.getVsaleopenacc())) {
				if ("北京农商银行".equals(vo.getVsaleopenacc()) || "中国工商银行".equals(vo.getVsaleopenacc())
						|| "交通银行".equals(vo.getVsaleopenacc()) || "招商银行".equals(vo.getVsaleopenacc())
						|| "重庆三峡银行".equals(vo.getVsaleopenacc()) || "重庆银行".equals(vo.getVsaleopenacc())) {
					zy = getBankKeyWord(vo, "摘要");
				} else if ("中信银行".equals(vo.getVsaleopenacc())) {
					zy = getBankKeyWord(vo, "摘要");
					if (StringUtil.isEmpty(zy)) {
						// zy = getBankKeyWord(vo, "附言");
					}
				} else if ("中国建设银行".equals(vo.getVsaleopenacc()) || "中国邮政储蓄银行".equals(vo.getVsaleopenacc())
						|| "重庆农村商业银行".equals(vo.getVsaleopenacc())) {
					zy = getBankKeyWord(vo, "用途");
				} else if ("中国农业银行".equals(vo.getVsaleopenacc())) {
					zy = getBankKeyWord(vo, "备注");
				} else if ("中国民生银行".equals(vo.getVsaleopenacc())) {
					if (!StringUtil.isEmpty(vo.getInvoicetype()) && vo.getInvoicetype().contains("(付款)"))
						zy = getBankKeyWord(vo, "附言");
				}
			}

			if (!StringUtil.isEmpty(vo.getKeywords())) {
				if (vo.getKeywords().contains("户间转账")) {
					zy = "户间转账";
				}
			}

			if (!StringUtil.isEmpty(vo.getZyFromBillZy())) {// 来源于银行单，凭证的摘要取对账单摘要字段
				zy = vo.getZyFromBillZy();
			}
		} else {

			if (StringUtil.isEmpty(invname))
				invname = "商品";
			if (VATInvoiceTypeConst.VAT_SALE_INVOICE == vo.getItype()) {
				if (StringUtil.isEmpty(vo.getVpurchname())) {
					zy = "向客户销售" + invname;
				} else {
					zy = "向" + vo.getVpurchname() + "销售" + invname;
				}
			} else if (VATInvoiceTypeConst.VAT_INCOM_INVOICE == vo.getItype()) {
				if (vo.getIinvoicetype() != null && vo.getIinvoicetype().intValue() == 1) {
					// 费用类 发票
					zy = model.getZy();
				} else {
					// 进项发票
					if (StringUtil.isEmpty(vo.getVsalename())) {
						zy = "向供应商采购" + invname;
					} else {
						zy = "向" + vo.getVsalename() + "采购" + invname;
					}
				}
			} else {
				zy = model.getZy();

			}
			if (!StringUtil.isEmpty(vo.getVinvoiceno())) {
				zy = zy + "&发票号码" + vo.getVinvoiceno();
			}
		}
		if (StringUtil.isEmpty(zy)) {
			zy = model.getZy();
		}

		entry.setZy(zy);
	}

	private String getBankKeyWord(OcrImageLibraryVO vo, String key) {
		// 获取摘要信息
		if (!StringUtil.isEmpty(vo.getVsalephoneaddr())) {
			String vsalephoneaddr = vo.getVsalephoneaddr();
			vsalephoneaddr = vsalephoneaddr.replaceAll("”", "'");
			JSONObject rowobject = null;
			try {
				rowobject = JSON.parseObject(vsalephoneaddr);
			} catch (Exception e) {
				throw new BusinessException("JSON数据格式出错");
			}
			if (rowobject != null && rowobject.size() > 0) {
				String zy = (String) rowobject.get(key);
				return zy;
			}
		}
		return null;
	}

	protected DZFDouble getSubMny(String smny) {
		DZFDouble mny = DZFDouble.ZERO_DBL;
		try {

			if (StringUtil.isEmpty(smny)) {
				mny = DZFDouble.ZERO_DBL;
			} else {
				smny = replaceBlank(smny);
				smny = smny.replaceAll("[￥%$*免税]", "");
			}
			mny = new DZFDouble(smny);
		} catch (Exception e) {
			if (e instanceof NumberFormatException)
				throw new BusinessException("数字识别出错");
			else
				throw new BusinessException(e.getMessage());
		}
		return mny;
	}

	protected void shoufei(ImageGroupVO grpvo, TzpzHVO hvo, DZFDate date) {
		try {
			String pk_corp = hvo.getPk_corp();

			if (hvo.getIautorecognize() != 1) {
				return;
			}
			// 是否收费接口
			DZFBoolean isCharge = versionServ.isChargeByProduct(pk_corp, IDzfServiceConst.DzfServiceProduct_04);// 智能凭证
			log.info("开始 收费,图片id：" + hvo.getPk_image_group());
			if (isCharge != null && isCharge.booleanValue()) {// 是 收费
				CorpVO corpvo = corpService.queryByPk(pk_corp);
				DZFBalanceBVO bvo = new DZFBalanceBVO();
				bvo.setChangedcount(new DZFDouble(1));// 使用数量
				bvo.setIsadd(1);// 减少
				bvo.setPk_corp(pk_corp);
				bvo.setPk_user(hvo.getCoperatorid());
				bvo.setPk_corpkjgs(corpvo.getFathercorp());// 会计公司
				bvo.setPk_dzfservicedes(IDzfServiceConst.DzfServiceProduct_04);
				// 查询委托公司
				String wtcorp = fctPubServ.getAthorizeFactoryCorp(date, pk_corp);
				log.info("查询委托公司" + wtcorp);
				if (StringUtil.isEmpty(wtcorp)) {
					balanceServ.consumption(bvo);// 扣费
				} else {
					// 扣委托公司费用
					balanceServ.consumptionByFct(bvo, date);
				}
			}
		} catch (BusinessException e) {
			log.error("凭证id：" + hvo.getPk_tzpz_h(), e);
			clearTzpzvo(hvo);
			imageMsgHandle(hvo.getPk_corp(), hvo, grpvo);
			throw e;
		} catch (Exception e) {
			log.error("凭证id：" + hvo.getPk_tzpz_h(), e);
			clearTzpzvo(hvo);
			throw new BusinessException(e.getMessage());
		}
	}

	private void imageMsgHandle(String pk_corp, TzpzHVO headVO, ImageGroupVO grpvo) {
		// 消息回写
		IMsgService sys_msgtzserv = (IMsgService) SpringUtils.getBean("sys_msgtzserv");
		sys_msgtzserv.newSaveMsgVoFromImage(pk_corp, headVO, grpvo);

	}

	private void clearTzpzvo(TzpzHVO hvo) {
		hvo.setPk_tzpz_h(null);
		hvo.setDfmny(DZFDouble.ZERO_DBL);
		hvo.setJfmny(DZFDouble.ZERO_DBL);
		if (hvo != null && hvo.getChildren() != null && hvo.getChildren().length > 0) {
			for (SuperVO vo : hvo.getChildren()) {
				TzpzBVO bvo = (TzpzBVO) vo;
				bvo.setNnumber(DZFDouble.ZERO_DBL);
				bvo.setNprice(DZFDouble.ZERO_DBL);
				bvo.setJfmny(DZFDouble.ZERO_DBL);
				bvo.setYbjfmny(DZFDouble.ZERO_DBL);
				bvo.setDfmny(DZFDouble.ZERO_DBL);
				bvo.setYbdfmny(DZFDouble.ZERO_DBL);
				bvo.setPk_tzpz_b(null);
				// bvo.setZy("余额不足");
			}
		}
	}

	protected void updateKeyWord(OcrInvoiceVO invvo, DcModelHVO headvo) {
		StringBuffer strb = new StringBuffer();
		// 更新model 信息
		strb.append(
				" update ynt_interface_invoice  set pk_model_h =? ,keywords=? where pk_image_group =? and nvl(dr,0) = 0 and pk_corp = ? ");
		SQLParameter sp = new SQLParameter();
		sp.addParam(headvo.getPk_model_h());
		sp.addParam(headvo.getKeywords());
		sp.addParam(invvo.getPk_image_group());
		sp.addParam(invvo.getPk_corp());
		singleObjectBO.executeUpdate(strb.toString(), sp);
	}

	protected void throwBusinessException() {
		throw new BusinessException("票据类型未确定");
	}

	protected String filterName(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		name = getHanzi(name);
		return name;
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

	public void setSpeaclTzpzBVO1(TzpzHVO hvo, OcrImageLibraryVO lib, List<TzpzBVO> tblist) {
		setSpeaclTzpzBVO(hvo, lib, tblist);
	}

	protected String getUpCode(String code, String newrule) throws DZFWarpException {
		String upCode = null;
		if (!StringUtil.isEmpty(code) && code.length() > 4) {
			upCode = DZfcommonTools.getParentCode(code, newrule);// 取上级的值
		}
		return upCode;
	}
}

package com.dzf.zxkj.platform.service.zncs.image.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IBillManageConstants;
import com.dzf.zxkj.common.constant.InvoiceColumns;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.*;
import com.dzf.zxkj.platform.model.pjgl.VATSaleInvoiceVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.zncs.VATSaleInvoiceBVO;
import com.dzf.zxkj.platform.service.pzgl.IVoucherService;
import com.dzf.zxkj.platform.service.zncs.IVATSaleInvoiceService;
import com.dzf.zxkj.platform.util.zncs.ICaiFangTongConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service("image2bill_vatsale")
public class VATSaleImage2BillServiceImpl extends DefaultImage2BillServiceImpl {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IVoucherService gl_tzpzserv;
	@Autowired
	private IVATSaleInvoiceService gl_vatsalinvserv;

	@Override
	public TzpzHVO saveBill(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo, boolean isRecog)
			throws DZFWarpException {
		if (isRecog) {
			saveSaleInfoModel2(corpvo, hvo, invvo, grpvo);
		} else {
			saveSaleInfo(corpvo, hvo, invvo, grpvo);
		}
		return hvo;
	}

	// 需要判断是否存在销项清单 如果存在 没有生成凭证 不生成凭证 已经生成凭证 关联凭证
	private void saveSaleInfo(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo) {
		List<VATSaleInvoiceVO> list = quyerSaleInvoiceVO(invvo, corpvo.getPk_corp());
		if (list == null || list.size() == 0) {
			// 转成销项清单
			gl_tzpzserv.saveVoucher(corpvo, hvo);
			Map<String, VATSaleInvoiceVO[]> sendData = new HashMap<String, VATSaleInvoiceVO[]>();
			VATSaleInvoiceVO headvo = createVATSaleInvoiceVO(corpvo, invvo, hvo, grpvo);
			sendData.put("adddocvos", new VATSaleInvoiceVO[] { headvo });
			gl_vatsalinvserv.updateVOArr(corpvo.getPk_corp(), sendData);
		} else {
			String pk_tzpz_h = list.get(0).getPk_tzpz_h();
			String pk_image_group = list.get(0).getPk_image_group();
			if (!StringUtil.isEmpty(pk_tzpz_h)) {
				if (StringUtil.isEmpty(pk_image_group)) {
					// 凭证无图片 图片关联凭证 停止生成凭证流程
					hvo.setPk_tzpz_h(pk_tzpz_h);
					// 如果图片组存在凭证 删除
					TzpzHVO headvo = queryTzpzByImageGroup(grpvo);
					if (headvo != null) {
						singleObjectBO.deleteObject(headvo);
					}
					updatePzImageGroup(invvo.getPk_image_group(), pk_tzpz_h);

					String imagepath = null;
					ImageLibraryVO libvo = getImageLibraryVO(hvo.getPk_corp(), invvo);
					if (libvo != null) {
						imagepath = getImgpath(libvo);
					}
					if (!StringUtil.isEmpty(imagepath)) {
						String sql = "update ynt_vatsaleinvoice y set y.imgpath = ?,y.sourcebillid=?,y.pk_image_group=?,y.pk_image_library=? Where y.pk_vatsaleinvoice = ? ";
						SQLParameter sp = new SQLParameter();
						sp.addParam(imagepath);
						sp.addParam(libvo.getPk_image_library());
						sp.addParam(libvo.getPk_image_group());
						sp.addParam(libvo.getPk_image_library());
						sp.addParam(list.get(0).getPrimaryKey());
						singleObjectBO.executeUpdate(sql, sp);
						updateImageGroup(invvo);
					}
				} else {
					// 有图片 则不关联 不处理 停止生成凭证流程 更新重复标识 更新重复凭证id
					checkValidImgGrpData(grpvo);
					updateRepeatedInfo(pk_tzpz_h, invvo);
				}
			} else {
				gl_tzpzserv.saveVoucher(corpvo, hvo);
				String sql = "update ynt_vatsaleinvoice y set y.pk_tzpz_h = ?,y.pzh = ? Where y.pk_vatsaleinvoice = ? ";
				SQLParameter sp = new SQLParameter();
				sp.addParam(hvo.getPk_tzpz_h());
				sp.addParam(hvo.getPzh());
				sp.addParam(list.get(0).getPrimaryKey());
				singleObjectBO.executeUpdate(sql, sp);
				String imagepath = null;
				ImageLibraryVO libvo = getImageLibraryVO(hvo.getPk_corp(), invvo);
				if (libvo != null) {
					imagepath = getImgpath(libvo);
				}
				if (!StringUtil.isEmpty(imagepath)) {
					sql = "update ynt_vatsaleinvoice y set y.imgpath = ?,y.sourcebillid=?,y.pk_image_group=?,y.pk_image_library=? Where y.pk_vatsaleinvoice = ? ";
					sp = new SQLParameter();
					sp.addParam(imagepath);
					sp.addParam(libvo.getPk_image_library());
					sp.addParam(libvo.getPk_image_group());
					sp.addParam(libvo.getPk_image_library());
					sp.addParam(list.get(0).getPrimaryKey());
					singleObjectBO.executeUpdate(sql, sp);
					updateImageGroup(invvo);
				}
			}
		}
	}

	public List<VATSaleInvoiceVO> quyerSaleInvoiceVO(OcrInvoiceVO vo, String pk_corp) throws DZFWarpException {
		StringBuffer sb = new StringBuffer();
		sb.append(" select y.fp_hm,y.fp_dm ,y.pk_vatsaleinvoice,y.pk_image_group,h.pk_tzpz_h, h.pzh,h.pk_image_group pk_image_group1");
		sb.append(" from ynt_vatsaleinvoice y ");
		sb.append("   left join ynt_tzpz_h h ");
		sb.append("     on y.pk_tzpz_h = h.pk_tzpz_h ");
		sb.append(" where y.pk_corp=? and nvl(y.dr,0)=0");
		sb.append(" and fp_hm = ? and fp_dm = ?");
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(vo.getVinvoiceno());
		sp.addParam(vo.getVinvoicecode());
		List<VATSaleInvoiceVO> listVo = (List<VATSaleInvoiceVO>) singleObjectBO.executeQuery(sb.toString(), sp,
				new BeanListProcessor(VATSaleInvoiceVO.class));

		return listVo;

	}

	private VATSaleInvoiceVO createVATSaleInvoiceVO(CorpVO corpvo, OcrInvoiceVO invvo, TzpzHVO hvo, ImageGroupVO grpvo)
			throws BusinessException {
		List<VATSaleInvoiceBVO> blist = new ArrayList<VATSaleInvoiceBVO>();
		VATSaleInvoiceVO headvo = new VATSaleInvoiceVO();

		String[] hcodes = InvoiceColumns.INVOICE_HCODES;
		String[] hnames = InvoiceColumns.INCOM_HCODES1;

		int hlen = hcodes.length;

		for (int m = 0; m < hlen; m++) {
			headvo.setAttributeValue(hnames[m], invvo.getAttributeValue(hcodes[m]));
		}

		setDefaultValue(invvo, hvo, headvo, grpvo);
		OcrInvoiceDetailVO[] detailvos = (OcrInvoiceDetailVO[]) singleObjectBO.queryByCondition(
				OcrInvoiceDetailVO.class, "nvl(dr,0) =0 and pk_invoice ='" + invvo.getPk_invoice() + "'", null);

		String[] bcodes = InvoiceColumns.INVOICE_BCODES;
		String[] bnames = InvoiceColumns.INCOM_BCODES;
		int blen = bcodes.length;
		int i = 0;
		for (OcrInvoiceDetailVO detail : detailvos) {
			VATSaleInvoiceBVO bvo = new VATSaleInvoiceBVO();
			for (int m = 0; m < blen; m++) {
				if ("itemtaxrate".equals(bcodes[m]) || "itemmny".equals(bcodes[m]) || "itemtaxmny".equals(bcodes[m])
						|| "itemamount".equals(bcodes[m]) || "itemprice".equals(bcodes[m])) {
					bvo.setAttributeValue(bnames[m], getDZFDouble((String) detail.getAttributeValue(bcodes[m])));
				} else {
					bvo.setAttributeValue(bnames[m], replaceBlank((String) detail.getAttributeValue(bcodes[m])));
				}
			}
			bvo.setRowno(i);
			bvo.setPk_corp(hvo.getPk_corp());
			bvo.setDr(0);
			i++;
			blist.add(bvo);
		}

		if (blist != null && blist.size() > 0) {
			headvo.setChildren(blist.toArray(new VATSaleInvoiceBVO[blist.size()]));
		}
		return headvo;
	}

	/**
	 * 设置默认值
	 *
	 * @param pk_corp
	 * @param userid
	 * @param vo
	 */
	private void setDefaultValue(OcrInvoiceVO invvo, TzpzHVO hvo, VATSaleInvoiceVO vo, ImageGroupVO grpvo) {
		vo.setPk_corp(hvo.getPk_corp());
		vo.setCoperatorid(hvo.getCoperatorid());
		vo.setDoperatedate(hvo.getDoperatedate());
		vo.setKprj(new DZFDate(getStrFormateDate(invvo.getDinvoicedate())));
		// 设置期间
		if (vo.getKprj() != null) {
			vo.setPeriod(DateUtils.getPeriod(vo.getKprj()));
		}

		DZFDouble sl = vo.getSpsl();
		if (sl == null || sl.doubleValue() == DZFDouble.ZERO_DBL.doubleValue()) {
			vo.setSpsl(SafeCompute.multiply(SafeCompute.div(vo.getSpse(), vo.getHjje()), new DZFDouble(100)));
			vo.setSpsl(vo.getSpsl().setScale(0, DZFDouble.ROUND_HALF_UP));
		}

		// 设置来源
		if (hvo.getFp_style() == VATInvoiceTypeConst.VAT_SPECIA_INVOICE) {
			vo.setIszhuan(DZFBoolean.TRUE);
		} else {
			vo.setIszhuan(DZFBoolean.FALSE);
		}
		vo.setSourcetype(IBillManageConstants.OCR);
		// 设置来源
		vo.setSourcebilltype(ICaiFangTongConstant.LYDJLX_OCR);
		vo.setSourcebillid(hvo.getPk_tzpz_h());

		vo.setPk_tzpz_h(hvo.getPk_tzpz_h());
		vo.setPzh(hvo.getPzh());

		if (!StringUtil.isEmpty(hvo.getPk_model_h())) {
			vo.setPk_model_h(hvo.getPk_model_h());
			DcModelHVO dchvo = (DcModelHVO) singleObjectBO.queryByPrimaryKey(DcModelHVO.class, hvo.getPk_model_h());
			if (dchvo != null) {
				vo.setBusitypetempname(dchvo.getBusitypetempname());
			}
		}

		ImageLibraryVO libvo = getImageLibraryVO(hvo.getPk_corp(), invvo);
		if (libvo != null) {
			vo.setSourcebillid(libvo.getPk_image_library());
			vo.setPk_image_group(libvo.getPk_image_group());
			vo.setPk_image_library(libvo.getPk_image_library());
			String imagepath = getImgpath(libvo);
			if (!StringUtil.isEmpty(imagepath)) {
				vo.setImgpath(imagepath);
			}
		}
	}

	private void saveSaleInfoModel2(CorpVO corpvo, TzpzHVO hvo, OcrInvoiceVO invvo, ImageGroupVO grpvo) {
		List<VATSaleInvoiceVO> list = quyerSaleInvoiceVO(invvo, corpvo.getPk_corp());
		if (list == null || list.size() == 0) {
			// 转成销项清单
			gl_tzpzserv.saveVoucher(corpvo, hvo);
			Map<String, VATSaleInvoiceVO[]> sendData = new HashMap<String, VATSaleInvoiceVO[]>();
			VATSaleInvoiceVO headvo = createVATSaleInvoiceVO(corpvo, invvo, hvo, grpvo);
			sendData.put("adddocvos", new VATSaleInvoiceVO[] { headvo });
			gl_vatsalinvserv.updateVOArr(corpvo.getPk_corp(), sendData);
		} else {
			// 是否需要更新销项清单 暂不更新
			String pk_tzpz_h = list.get(0).getPk_tzpz_h();
			String pk_image_group = list.get(0).getPk_image_group();
			if (!StringUtil.isEmpty(pk_tzpz_h) && !StringUtil.isEmpty(pk_image_group)) {
				// 有图片 则不关联 不处理 停止生成凭证流程 更新重复标识 更新重复凭证id
				if (pk_image_group.equals(grpvo.getPk_image_group())) {
					gl_tzpzserv.saveVoucher(corpvo, hvo);
				} else {
					checkValidImgGrpData(grpvo);
					updateRepeatedInfo(pk_tzpz_h, invvo);
				}
			} else {
				gl_tzpzserv.saveVoucher(corpvo, hvo);
			}
		}
	}
}

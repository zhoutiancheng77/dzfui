package com.dzf.zxkj.platform.util;


import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzBVO;
import com.dzf.zxkj.platform.model.tax.TaxitemParamVO;
import com.dzf.zxkj.platform.model.tax.TaxitemVO;

public class TaxItemUtil {

	/*private static String INV_SERV_NAME = "服务";

	public static TaxitemVO getTaxitemVO(TaxitemParamVO vo, YntCpaccountVO accvo) {

		// 科目不勾选凭证显示税目不在匹配税目
		if (accvo == null) {
			return null;
		}

		checkTaxParam(vo);
		String pk_corp = vo.getPk_corp();
		String userid = vo.getUserid();

		String shumuid = accvo.getShuimuid();
		ITaxitemsetService sys_taxsetserv = (ITaxitemsetService) SpringUtils.getBean("sys_taxsetserv");
		SingleObjectBO singleObjectBO = (SingleObjectBO) SpringUtils.getBean("singleObjectBO");
		List<TaxitemVO> list = null;

		if (accvo != null) {
			if (!StringUtil.isEmpty(shumuid)) {
				String[] ids = shumuid.split(",");
				String where = SqlUtil.buildSqlForIn("pk_taxitem", ids);
				String sql = "select * from  ynt_taxitem  where nvl(dr,0) = 0 and " + where;
				list = (List<TaxitemVO>) singleObjectBO.executeQuery(sql, null, new BeanListProcessor(TaxitemVO.class));
			}
		}
		TaxitemVO itemvo = null;
		if (list != null && list.size() > 0) {
			itemvo = filterTaxItem(list, vo, singleObjectBO);
		}

		if (itemvo == null) {
			list = sys_taxsetserv.queryItembycode(userid, pk_corp, accvo.getAccountcode());
			if (list != null && list.size() > 0) {
				itemvo = filterTaxItem(list, vo, singleObjectBO);
			}
		}
		updateTaxItem(itemvo, accvo, pk_corp);
		return itemvo;
	}

	private static void updateTaxItem(TaxitemVO itemvo, YntCpaccountVO accvo, String pk_corp) {
		if (itemvo != null) {
			// 更新税目
			ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
			String shumuid = accvo.getShuimuid();
			if (!StringUtil.isEmpty(shumuid)) {
				if (!shumuid.contains(itemvo.getPk_taxitem()))
					shumuid = shumuid + "," + itemvo.getPk_taxitem();
			} else {
				shumuid = itemvo.getPk_taxitem();
			}
			ITaxitemsetService sys_taxsetserv = (ITaxitemsetService) SpringUtils.getBean("sys_taxsetserv");
			List<TaxitemPzShowVO> taxitemvos = sys_taxsetserv.queryKMShow(null, pk_corp);
			Map<String, List<TaxitemPzShowVO>> map = DZfcommonTools.hashlizeObject(taxitemvos,
					new String[] { "pk_accsubj" });
			if (map != null && map.size() > 0) {
				List<TaxitemPzShowVO> list = map.get(accvo.getPk_corp_account());
				if (list != null && list.size() > 0) {
					TaxitemPzShowVO showvo = list.get(0);
					if (showvo.getShuimushowpz() == null || !showvo.getShuimushowpz().booleanValue()) {
						accvo.setShuimushowpz(DZFBoolean.FALSE);
					} else {
						accvo.setShuimushowpz(DZFBoolean.TRUE);
					}
				}
			}
			accvo.setShuimuid(shumuid);
			CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
			gl_cpacckmserv.update(accvo, corpvo);
		}

	}

	private static TaxitemVO filterTaxItem(List<TaxitemVO> list, TaxitemParamVO vo, SingleObjectBO singleObjectBO) {

		List<TaxitemVO> remainlist = filterTaxItemByTax(list, vo);

		List<TaxitemVO> list1 = filterTaxItemByInv(vo, singleObjectBO, remainlist);

		if (list1 != null && list1.size() > 0) {
			return list1.get(0);
		}

		if (remainlist != null && remainlist.size() > 0) {
			return remainlist.get(0);
		}
		return null;
	}

	// 按税率过滤
	private static List<TaxitemVO> filterTaxItemByTax(List<TaxitemVO> filterList, TaxitemParamVO vo) {

		List<TaxitemVO> list = new ArrayList<>();
		if (filterList != null && filterList.size() > 0) {
			CorpVO corpvo = CorpCache.getInstance().get(null, vo.getPk_corp());
			DZFDouble taxratio = vo.getTaxratio();
			if (taxratio != null) {
				Integer fp_style = vo.getFp_style();
				// (1), 普票（开具的普通发票）
				// (2), 专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
				// 如果为null 为不区分专、普票
				for (TaxitemVO item : filterList) {
					if (item.getTaxratio() != null && item.getTaxratio().equals(taxratio)) {
						addList(corpvo, list, fp_style, item);
					}
				}
			}
		}
		return list;
	}

	// 按商品性质过滤
	private static List<TaxitemVO> filterTaxItemByInv(TaxitemParamVO vo, SingleObjectBO singleObjectBO,
			List<TaxitemVO> remainlist) {

		if (remainlist == null || remainlist.size() == 0) {
			return null;
		}

		String pk_corp = vo.getPk_corp();
		String invname = vo.getInvname();

		CorpVO corpvo = CorpCache.getInstance().get(null, pk_corp);
		boolean isserv = false;
		if (!StringUtil.isEmpty(invname)) {
			isserv = KeyWordMatchBusiType.isInvtory(invname);
		}
		List<TaxitemVO> list = new ArrayList<>();
		Integer fp_style = vo.getFp_style();
		// (1), 普票（开具的普通发票）
		// (2), 专票（一般人而言是开具的专用发票，小规模为代开的专用发票）
		// 如果为null 为不区分专、普票
		for (TaxitemVO item : remainlist) {
			if (item != null) {
				if (isserv) {
					if (item.getShortname().contains(INV_SERV_NAME)) {
						addList(corpvo, list, fp_style, item);
					}
				} else {
					if (!item.getShortname().contains(INV_SERV_NAME)) {
						addList(corpvo, list, fp_style, item);
					}
				}
			}
		}
		return list;
	}

	private static void addList(CorpVO corpvo, List<TaxitemVO> list, Integer fp_style, TaxitemVO item) {
		String chargedeptname = StringUtil.isEmpty(corpvo.getChargedeptname()) ? "小规模纳税人" : corpvo.getChargedeptname();
		boolean isxgm = chargedeptname.equals("小规模纳税人") ? true : false;
		if (fp_style == null || !isxgm) {
			list.add(item);
		} else {
			if (fp_style == IFpStyleEnum.NOINVOICE.getValue()) {
				fp_style = IFpStyleEnum.COMMINVOICE.getValue();
			}

			if (item.getFp_style() == null) {
				list.add(item);
			} else {
				if (fp_style.intValue() == item.getFp_style().intValue()) {
					list.add(item);
				}
			}
		}
	}

	private static void checkTaxParam(TaxitemParamVO vo) {

		if (StringUtil.isEmpty(vo.getPk_corp()))
			throw new BusinessException("匹配税目，公司不能为空！");

		if (vo.getTaxratio() == null)
			throw new BusinessException("匹配税目，税率不能为空！");
	}
*/
	// 增加税目
	public static void dealTaxItem(TzpzBVO depvo, TaxitemParamVO taxparam, YntCpaccountVO cvo) {
//		TaxitemVO itemvo = getTaxitemVO(taxparam, cvo);
//		dealTaxItem(depvo, itemvo);
	}

	// 增加税目
	public static void dealTaxItem(TzpzBVO depvo, TaxitemVO itemvo) {
		if (itemvo != null) {
			depvo.setPk_taxitem(itemvo.getPk_taxitem());
			depvo.setTaxcode(itemvo.getTaxcode());
			depvo.setTaxname(itemvo.getTaxname());
			depvo.setTaxratio(itemvo.getTaxratio());
		}
	}
}

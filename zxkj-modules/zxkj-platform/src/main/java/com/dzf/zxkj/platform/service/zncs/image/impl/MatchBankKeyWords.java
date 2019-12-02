package com.dzf.zxkj.platform.service.zncs.image.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.DcModelBVO;
import com.dzf.zxkj.platform.model.image.DcModelHVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.IDcpzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;


/**
 * 匹配银行关键字 本版不考虑行业
 *
 */
@Component("match_bankwords")
public class MatchBankKeyWords {

	@Autowired
	private IDcpzService dcpzjmbserv;

	// 获取到匹配合适的模板
	public DcModelHVO getMatchModel(OcrInvoiceVO invvo, CorpVO corpvo) {
		if (invvo == null)
			return null;
		List<DcModelBVO> lista = null;
		JSONObject rowobject = analyNote(invvo);
		String billtype = getBilltype(invvo, rowobject);
		String bankname = getBankName(invvo);
		String pk_corp = corpvo.getPk_corp();
		List<DcModelHVO> list = queryDcModelHVOs(pk_corp, bankname, billtype);
		DcModelHVO hvo = filterModelDataByKeyWords(list, corpvo, invvo, billtype);
		if (hvo != null) {
			lista = dcpzjmbserv.queryByPId(hvo.getPk_model_h(), pk_corp);
			if (lista != null && lista.size() > 0) {
				hvo.setChildren(lista.toArray(new DcModelBVO[0]));
			}
		}
		return hvo;
	}

	// 解析税项明细
	private JSONObject analyTaxDetail(OcrInvoiceVO invvo) {
		try {
			if (invvo == null || StringUtil.isEmpty(invvo.getVmemo()))
				return null;
			String taxdetail = invvo.getVmemo().replaceAll("”", "'");
			JSONObject rowobject = JSON.parseObject(taxdetail);
			return rowobject;
		} catch (Exception e) {
			throw new BusinessException("JSON数据格式出错");
		}
	}

	// 解析备注内字段。
	private JSONObject analyNote(OcrInvoiceVO invvo) {
		try {
			if (invvo == null || StringUtil.isEmpty(invvo.getVsalephoneaddr()))
				return null;
			String vnote = invvo.getVsalephoneaddr().replaceAll("”", "'");
			JSONObject rowobject = JSON.parseObject(vnote);
			return rowobject;
		} catch (Exception e) {
			throw new BusinessException("JSON数据格式出错");
		}
	}

	// 获取单据类型
	private String getBilltype(OcrInvoiceVO invvo, JSONObject rowobject) {
		if (invvo == null)
			return null;
		String invtype = invvo.getInvoicetype();
		if (!StringUtil.isEmpty(invtype)) {
			invtype = invtype.substring(1, invtype.length());
		}
		// if("重庆三峡银行".equals(getBankName(invvo))
		// && rowobject!=null
		// && rowobject.get("交易名称") != null){
		// invtype = (String)rowobject.get("交易名称");
		// }
		return invtype;
	}

	// 获取对应银行
	private String getBankName(OcrInvoiceVO invvo) {
		if (invvo == null || StringUtil.isEmpty(invvo.getVsaleopenacc()))
			return null;
		return invvo.getVsaleopenacc();
	}

	// 公司判断默认相等吧，如果需要根据关键字判断。后面在说
	private String getPayWay(CorpVO vo, OcrInvoiceVO invvo, String billtype, boolean isperson) {
		if (invvo == null)
			return null;
		String sfk = "";
		String person = "";
		String name = vo.getUnitname();
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		if (!StringUtil.isEmpty(invvo.getVpurchname())
				&& (name.contains(invvo.getVpurchname()) || invvo.getVpurchname().contains(name))) {// 付款方名称
			person = getPersonName(invvo.getVsalename());
			sfk = "付款";
		} else if (!StringUtil.isEmpty(invvo.getVsalename())
				&& (name.contains(invvo.getVsalename()) || invvo.getVsalename().contains(name))) {// 收款方名称
			person = getPersonName(invvo.getVpurchname());
			sfk = "收款";
		}
		// 生成 户间转账
		if (!StringUtil.isEmpty(invvo.getVpurchname()) && !StringUtil.isEmpty(invvo.getVsalename())
				&& invvo.getVpurchname().equals(invvo.getVsalename())) {
			person = "";
			sfk = "户间转账";
		}
		if (isperson) {
			sfk = sfk + person;
		}
		return sfk;
	}

	// 判断是否个人还是公司
	// 通过长度也不好判断，老外的名字，还是挺长的。
	// 如果是一些政府机关，比如说某某局，某某办事处，某某管理处，并不是以公司结尾的。
	// 设置默认值为 公司
	private String getPersonName(String name) {
		String defaultname = "公司";
		if (StringUtil.isEmpty(name))
			return defaultname;
		if (name.length() <= 5) {
			defaultname = "个人";
		}
		if (name.contains("公司")) {
			defaultname = "公司";
		}
		return defaultname;
	}

	// 查询符合条件的业务类型数据
	private List<DcModelHVO> queryDcModelHVOs(String pk_corp, String bankname, String billtype) {
		if (StringUtil.isEmpty(bankname) || StringUtil.isEmpty(billtype))
			return null;
		List<DcModelHVO> list = dcpzjmbserv.queryAccordBankModel(pk_corp, new String[] { bankname, billtype });
		return list;
	}

	// 通过关键字过滤合适的模板数据
	private DcModelHVO filterModelDataByKeyWords(List<DcModelHVO> list, CorpVO vo, OcrInvoiceVO invvo,
			String billtype) {
		if (list == null || list.size() == 0)
			return null;
		if (list.size() == 1)
			return list.get(0);
		List<ModelSelectVO> zmselectlist = new ArrayList<ModelSelectVO>();
		// 补充收付款性质
		String payname = getPayWay(vo, invvo, billtype, true);
		String payname1 = getPayWay(vo, invvo, billtype, false);
		String pipeistyle = "";
		for (DcModelHVO dc : list) {
			String keywords = dc.getKeywords();// 这个字段不可能为空，但还是判断一下
			if (StringUtil.isEmpty(keywords))
				continue;
			keywords = keywords.replace("*", "&");
			String[] kds = keywords.split("&");
			if (kds != null && kds.length == 1) {
				pipeistyle = ModelSelectVO.pipeistyle_5;
				zmselectlist.add(buildSelectVO(dc, pipeistyle));
				continue;
			}
			boolean isexist = false;
			JSONObject notejson = analyNote(invvo);
			// 按以下顺序识别，优先级
			// 0、户间转账
			if (ModelSelectVO.pipeistyle_0.equals(payname)) {
				JSONObject jsobj = new JSONObject();
				jsobj.put(payname, payname);
				isexist = isExistsVnoteTax(kds, jsobj, true);
				pipeistyle = ModelSelectVO.pipeistyle_0;
			}
			// 1、税项明细匹配
			if (!isexist && kds[0].contains("税")) {
				isexist = isExistsVnoteTax(kds, analyTaxDetail(invvo), false);
				pipeistyle = ModelSelectVO.pipeistyle_1;
			}
			// 2、备注匹配
			if (!isexist) {
				isexist = isExistsVnoteTax(kds, notejson, true);
				pipeistyle = ModelSelectVO.pipeistyle_2;
			}
			// 3、收付款(公司个人)匹配
			if (!isexist && !StringUtil.isEmpty(payname)) {
				JSONObject jsobj = new JSONObject();
				jsobj.put(payname, payname);
				isexist = isExistsVnoteTax(kds, jsobj, true);
				pipeistyle = ModelSelectVO.pipeistyle_3;

				if (isexist) {
					// 4、收付款匹配
					jsobj = new JSONObject();
					jsobj.put(payname1, payname1);
					boolean isexist1 = isExistsVnoteTax(kds, jsobj, true);
					if (isexist1)
						pipeistyle = ModelSelectVO.pipeistyle_4;
				}
			}

			if (isexist) {
				zmselectlist.add(buildSelectVO(dc, pipeistyle));
			}
		}
		DcModelHVO defaultmodel = null;
		// 选择其中级别最高的
		if (zmselectlist != null && zmselectlist.size() > 0) {
			Collections.sort(zmselectlist);
			defaultmodel = zmselectlist.get(zmselectlist.size() - 1).getDefaultmodel();
		}
		return defaultmodel;
	}

	private ModelSelectVO buildSelectVO(DcModelHVO defaultmodel, String pipeistyle) {
		ModelSelectVO vo = new ModelSelectVO();
		vo.setDefaultmodel(defaultmodel);
		vo.setPipeistyle(pipeistyle);
		return vo;
	}

	private boolean isExistsVnoteTax(String[] kds, JSONObject rowobject, boolean isValue) {
		if (kds == null || kds.length == 0)
			return false;
		boolean flag = false;
		if (rowobject == null || rowobject.size() == 0)
			return flag;
		Set<String> set = rowobject.keySet();
		for (String key : set) {
			String value = null;
			if (isValue) {
				value = (String) rowobject.get(key);
			} else {
				value = key;
			}
			if (!StringUtil.isEmpty(value)) {
				flag = isExistKeyWords(kds, value);
				if (flag)
					break;
			}
		}
		return flag;
	}

	private boolean isExistKeyWords(String[] kds, String value) {
		boolean flag = false;
		if (kds == null || kds.length == 0)
			return flag;
		if (StringUtil.isEmpty(value))
			return flag;
		for (int i = 0; i < kds.length; i++) {
			if (value.contains(kds[i])) {
				flag = true;
				break;
			}
		}
		return flag;
	}
}
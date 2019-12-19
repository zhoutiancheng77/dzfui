package com.dzf.zxkj.platform.service.zncs.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.common.enums.StateEnum;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.OcrImageLibraryVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceDetailVO;
import com.dzf.zxkj.platform.model.image.OcrInvoiceVO;
import com.dzf.zxkj.platform.util.zncs.*;
import org.jboss.logging.Logger;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

@Service("afreshAutoReco")
public class AfreshAutoRecognitionService {

	private static Logger log = Logger.getLogger(AfreshAutoRecognitionService.class);
	private static final String moduleName = "WEB_AFRESHAUTORECO";

	private OcrInvoiceVO getImageResult(OcrImageLibraryVO vo, List<String> list1, String fileId) {

		OcrInvoiceVO invvo = null;

		try {
			List<OcrInvoiceVO> olist = getMultipleInvoiceInfo(fileId, list1);

			if (olist == null || olist.size() == 0)
				throw new BusinessException("网站接口识别出数据为空");

			// 记录 接口发票信息 更新 状态
			for (OcrInvoiceVO ocrvo : olist) {
				String istate = ocrvo.getIstate();
				if (!StringUtil.isEmpty(ocrvo.getIstate())) {
					if ("识别正确".equals(istate) || "已被识别".equals(istate) || "过期发票".equals(istate)
							|| istate.startsWith("b") || istate.startsWith("c")) {
						invvo = ocrvo;
						break;
					}
				}
			}

			if (invvo == null) {
				invvo = new OcrInvoiceVO();
				throw new BusinessException("识别发票信息出错");
			} else {
				invvo.setPk_corp(vo.getPk_corp());
				invvo.setOcr_id(vo.getPk_image_ocrlibrary());
				invvo.setDr(0);
				invvo.setItype(WayEnum.IWEB.getValue());
			}

			OcrInvoiceDetailVO[] bvos = (OcrInvoiceDetailVO[]) invvo.getChildren();
			if (bvos != null && bvos.length > 0) {

				List<String> list = getNoContainList();
				for (OcrInvoiceDetailVO bvo : bvos) {

					if (StringUtil.isEmpty(bvo.getInvname())) {
						continue;
					}
					String name = filterName(bvo.getInvname());
					if (!list.contains(name)) {
						bvo.setDr(0);
					} else {
						bvo.setDr(1);
					}
					bvo.setPk_corp(vo.getPk_corp());
					bvo.setOcr_id(vo.getPk_image_ocrlibrary());
				}
				invvo.setVfirsrinvname(bvos[0].getInvname());
			}
			vo.setIstate(StateEnum.SUCCESS_INTER_GET.getValue());
			vo.setIway(WayEnum.IWEB.getValue());
			vo.setIsinterface(DZFBoolean.TRUE);
			vo.setReason("识别成功");
			vo.setBatchcode(vo.getBatchcode());
			invvo.setPk_corp(vo.getPk_corp());
			invvo.setOcr_id(vo.getPk_image_ocrlibrary());
			invvo.setDr(0);
			invvo.setItype(WayEnum.IWEB.getValue());
		} catch (BusinessException e) {
			vo.setIstate(StateEnum.FAIL_INTER_GET.getValue());
			vo.setReason(e.getMessage());
			vo.setBatchcode(vo.getBatchcode());
			vo.setReason(e.getMessage());
			ExceptionDealUtil.dealException(vo, log, moduleName, e.getMessage(), e);
		} catch (Exception e) {
			vo.setIstate(StateEnum.FAIL_INTER_GET.getValue());
			vo.setReason(e.getMessage());
			vo.setBatchcode(vo.getBatchcode());
			vo.setReason("调用网站获取结果");
			ExceptionDealUtil.dealException(vo, log, moduleName, "调用网站获取结果", e);
		}
		return invvo;
	}

	// 异步读取网站接口识别结果
	private JSONObject getInvoiceInfo(String fileID) throws IOException {
		String json = "{\"fileID\":\"" + fileID + "\"}";
		String data = getData(InvInvoiceConst.INTERFACE_INVINFOBYFILEID, InvInvoiceConst.TYPE_METHOD_POST, json);

		ExceptionDealUtil.loggerInfo(log, moduleName, "Web识别,图片ID" + fileID + ",返回识别结果信息json【" + data + "】");

		JSONObject object = JSON.parseObject(data);

		return object;
	}

	// 异步读取网站接口识别结果
	private List<OcrInvoiceVO> getMultipleInvoiceInfo(String fileId, List<String> list1) throws IOException {
		try {
			List<OcrInvoiceVO> list = atuoMultipleInvoiceInfo(fileId, list1);
			return list;
		} catch (Exception e) {
			OcrExceptionUtil.dealOcrException(e, StateEnum.FAIL_INTER_GET.getName(), "网站接口识别结果读取错误");
		}
		return null;
	}

	private List<OcrInvoiceVO> atuoMultipleInvoiceInfo(String fileId, List<String> list) throws IOException {

		JSONObject rowobject = getInvoiceInfo(fileId);

		if (rowobject == null || rowobject.size() == 0) {
			throw new BusinessException("未读取网站接口数据!");
		}

		if (rowobject.get("err_msg") != null) {
			throw new BusinessException((String) rowobject.get("err_msg"));
		}

		String[][] hbnames = OcrInvoiceColumns.OCR_BANK_CODEAMES;

		String fileid = (String) rowobject.get(OcrInvoiceColumns.fileID);
		if (!StringUtil.isEmpty(fileid))
			list.add(fileid);

		String istate = (String) rowobject.get("识别状态");
		if (!StringUtil.isEmpty(istate)) {
			if ("识别正确".equals(istate) || "已被识别".equals(istate) || "过期发票".equals(istate) || istate.startsWith("b")
					|| istate.startsWith("c")) {
			} else {
				throw new BusinessException("识别状态出错:" + istate);
			}
		}

		String invoicetype = (String) rowobject.get(hbnames[0][1]);
		if (StringUtil.isEmpty(invoicetype)) {
			String[] hnames = OcrInvoiceColumns.HEAD_NAMES;
			invoicetype = (String) rowobject.get(hnames[3]);
			if (StringUtil.isEmpty(invoicetype)) {
				throw new BusinessException("识别发票类型出错!");
			}
		}
		List<OcrInvoiceVO> hlist = null;
		// 银行 或者定额发票
		if (invoicetype.startsWith("b") || invoicetype.startsWith("c")) {
			hlist = getBankOrQuotaInvoiceVO(rowobject, invoicetype);
		} else {// 增值税发票
			hlist = getVatInvoiceVO(rowobject, invoicetype);
		}
		return hlist;
	}

	private List<OcrInvoiceVO> getVatInvoiceVO(JSONObject object, String invoicetype) {
		String[] hcodes = OcrInvoiceColumns.HEAD_CODES;
		String[] hnames = OcrInvoiceColumns.HEAD_NAMES;
		String[] bcodes = OcrInvoiceColumns.ITEM_CODES;
		String[] bnames = OcrInvoiceColumns.ITEM_NAMES;

		if (invoicetype.startsWith("通行费")) {
			bnames = OcrInvoiceColumns.ITEM_NAMES_PASSMNY;
		}

		int hlen = hcodes.length;
		int blen = bnames.length;
		int rlen = Integer.parseInt(PropertyGetter.webrow);
		OcrInvoiceVO invvo = null;
		List<OcrInvoiceVO> hlist = new ArrayList<>();
		OcrInvoiceDetailVO itemvo = null;
		List<OcrInvoiceDetailVO> blist = null;
		invvo = new OcrInvoiceVO();
		for (int m = 0; m < hlen; m++) {
			invvo.setAttributeValue(hcodes[m], object.get(hnames[m]));
		}
		blist = new ArrayList<>();
		for (int l = 0; l < rlen; l++) {
			itemvo = new OcrInvoiceDetailVO();
			if (object.get((l + 1) + bnames[0]) == null) {
				break;
			}
			for (int m = 0; m < blen; m++) {

				itemvo.setAttributeValue(bcodes[m], object.get((l + 1) + bnames[m]));
			}
			blist.add(itemvo);
		}
		if (blist != null && blist.size() > 0) {
			invvo.setChildren(blist.toArray(new OcrInvoiceDetailVO[blist.size()]));
		}
		hlist.add(invvo);
		return hlist;
	}

	private List<OcrInvoiceVO> getBankOrQuotaInvoiceVO(JSONObject object, String invoicetype) {
		OcrInvoiceVO invvo = null;
		String[][] codenames = OcrInvoiceColumns.OCR_BANK_CODEAMES;
		if (invoicetype.startsWith("c")) {
			codenames = OcrInvoiceColumns.OCR_QUOTAINVOICE_CODENAMES;
		}

		int hlen = codenames.length;
		List<OcrInvoiceVO> hlist = new ArrayList<>();
		invvo = new OcrInvoiceVO();
		for (int m = 0; m < hlen; m++) {
			invvo.setAttributeValue(codenames[m][0], object.get(codenames[m][1]));
		}
		hlist.add(invvo);
		return hlist;

	}

	private String filterName(String name) {
		if (!StringUtil.isEmpty(name)) {
			name = name.replaceAll("[()（）\\[\\]]", "");
		} else {
			name = "";
		}
		return name;

	}

	// 不包含的行
	private List<String> getNoContainList() {
		List<String> list = new ArrayList<>();
		list.add("详见购货清单");
		list.add("详见销货清单");
		list.add("原价合计");
		list.add("折扣额合计");
		return list;
	}

	private String getData(String name, String type, String json) {
		HttpURLConnection httpConnection = null;
		BufferedReader responseBuffer = null;
		InputStreamReader inread = null;
		InputStream instream = null;
		OutputStream outputStream = null;
		String data = null;
		try {
			String url = getUrlByName(name);
			if (InvInvoiceConst.TYPE_METHOD_POST.equals(type)) {
				URL targetUrl = new URL(url);

				httpConnection = (HttpURLConnection) targetUrl.openConnection();
				httpConnection.setDoOutput(true);
				httpConnection.setRequestMethod(type);
				httpConnection.setRequestProperty("Content-Type", "application/json");
				outputStream = httpConnection.getOutputStream();
				outputStream.write(json.getBytes());
				outputStream.flush();
			} else if (InvInvoiceConst.TYPE_METHOD_GET.equals(type)) {
				URL targetUrl = new URL(url + "/" + json);
				httpConnection = (HttpURLConnection) targetUrl.openConnection();
				httpConnection.setDoOutput(true);
				httpConnection.setRequestMethod(type);
				httpConnection.setRequestProperty("Content-Type", "application/json");
			} else {
				throw new BusinessException("Failed : HTTP error : 传入类型参数出错! ");
			}

			if (httpConnection.getResponseCode() != 200) {
				throw new BusinessException("Failed : HTTP error code : " + httpConnection.getResponseCode());
			}
			instream = httpConnection.getInputStream();
			inread = new InputStreamReader(instream);
			responseBuffer = new BufferedReader(inread);

			String output = null;
			while ((output = responseBuffer.readLine()) != null) {
				data = output;
			}
			httpConnection.disconnect();
		} catch (IOException e) {
			throw new BusinessException("Failed : HTTP error :" + e.getMessage());
		} catch (Exception e) {
			throw new BusinessException("Failed : unknown error :" + e.getMessage());
		} finally {
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}

			if (instream != null) {
				try {
					instream.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}

			if (inread != null) {
				try {
					inread.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}

			if (responseBuffer != null) {
				try {
					responseBuffer.close();
				} catch (IOException e) {
					log.error(e.getMessage(),e);
				}
			}
			if (httpConnection != null) {
				httpConnection.disconnect();
			}
		}
		return data;
	}

	private String getUrlByName(String name) {
		String url = "http://" + PropertyGetter.inv_ip + "/" + name;
		return url;
	}

	public OcrInvoiceVO getOcrInvoiceVO(OcrImageLibraryVO imagevo, String fileId) {
		OcrInvoiceVO invvo = null;
		try {
			if (imagevo == null)
				return null;
			List<String> list = new ArrayList<>();
			// 获取网站识别结果
			// 图片id
			invvo = getImageResult(imagevo, list, fileId);

			if (invvo == null) {
				invvo = new OcrInvoiceVO();
				// 以后处理 组装OcrImageLibraryVO中结果
				changeOcrImageLibraryVO(imagevo, invvo);
			}
			// 组合唯一标识 OcrInvoiceVO 表中vkeywordinfo
			addKeyWordInfo(invvo, imagevo);

			// 如果 图片id为空 在重新获取一次
			if (StringUtil.isEmpty(invvo.getWebid())) {
				if (list != null && list.size() > 0) {
					invvo.setWebid(list.get(0));
				}
			}
		} catch (Exception e) {
			ExceptionDealUtil.loggerInfo(log, moduleName, "获取识别数据出错" + e.getMessage());
		}
		return invvo;
	}

	private void changeOcrImageLibraryVO(OcrImageLibraryVO invvo, OcrInvoiceVO vo) {

		vo.setVinvoicecode(invvo.getVinvoicecode());
		vo.setVinvoiceno(invvo.getVinvoiceno());
		vo.setVpurchname(invvo.getVpurchname());
		vo.setVpurchtaxno(invvo.getVpurchtaxno());
		vo.setVsalename(invvo.getVsalename());
		vo.setVsaletaxno(invvo.getVsaletaxno());

		if (!StringUtil.isEmpty(invvo.getDinvoicedate())) {
			if (invvo.getDinvoicedate().length() == 6) {
				vo.setDinvoicedate("20" + invvo.getDinvoicedate());
			} else {
				vo.setDinvoicedate(invvo.getDinvoicedate());
			}
		}
		vo.setNmny(invvo.getNmny());
		vo.setNtaxnmny(substring(invvo.getNtaxnmny(), 16));
		vo.setNtotaltax(substring(invvo.getNtotaltax(), 16));
		vo.setInvoicetype(substring(invvo.getInvoicetype(), 30));
		vo.setPk_corp(invvo.getPk_corp());
	}

	private String substring(String str, int len) {
		if (StringUtil.isEmpty(str)) {
			return null;
		} else {
			int len1 = str.length();
			if (len1 > len) {
				str = str.substring(0, len);
			}
			return str;
		}
	}

	private void addKeyWordInfo(OcrInvoiceVO invvo, OcrImageLibraryVO imagevo) {

		if (invvo == null)
			return;
		String invoicetype = invvo.getInvoicetype();

		if (StringUtil.isEmpty(invoicetype)) {
			return;
		}

		String[] columns = null;
		// 银行回单
		if (invoicetype.startsWith("b")) {
			columns = new String[] { "vpurchname", // 付款方名称
					"vsalename", // 收款方名称
					"dinvoicedate", // 日期
					"ntotaltax", // 金额
					"vsaleopenacc", // 银行名称
					"vsalephoneaddr", // 备注
					"uniquecode", // 单据标识号
					"vpurchtaxno", // 付款方账号
					"vsaletaxno"// 收款方账号
			};
		} else if (invoicetype.startsWith("c")) {
			// 定额发票 火车票 机打发票
			columns = new String[] { "vpurchname", // 目的地
					"vsalename", // 出发地
					"dinvoicedate", // 日期
					"ntotaltax", // 金额
					"vinvoicecode", // 发票代码
					"vinvoiceno", // 发票号码
					"vsalephoneaddr" };// 备注
		} else {
			// 增值税票
			columns = new String[] { "vinvoicecode", "vinvoiceno", "dinvoicedate" };
		}

		ExceptionDealUtil.loggerInfo(log, moduleName, "***********开始加密*****************");
		StringBuffer plainText = new StringBuffer();

		for (String column : columns) {
			String value = (String) invvo.getAttributeValue(column);
			if (StringUtil.isEmpty(value)) {
				value = "null";
			}
			plainText.append(value);
		}

		String plain = plainText.toString().replaceAll("null", "");

		if (StringUtil.isEmpty(plain))
			return;

		plain = plain + invvo.getInvoicetype();

		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.update(plainText.toString().getBytes());
			BigInteger bi = new BigInteger(1, md5.digest());
			String value = bi.toString(16);
			invvo.setVkeywordinfo(value);
		} catch (NoSuchAlgorithmException e) {
			ExceptionDealUtil.dealException(imagevo, log, moduleName, "MD5转换出错", e);
		} catch (Exception e) {
			ExceptionDealUtil.dealException(imagevo, log, moduleName, "MD5转换未知异常", e);
		}
		ExceptionDealUtil.loggerInfo(log, moduleName, "加密后" + invvo.getVkeywordinfo());
	}

	public String callBackNlWeb(String jsondata, String fileID) {
		String rtnmsg = null;
		try {
			ExceptionDealUtil.loggerInfo(log, moduleName, "修订结果反馈,图片ID" + fileID + ",调用信息json【" + jsondata + "】");
			String data = getData(InvInvoiceConst.INTERFACE_INVKEYSBYHAND, InvInvoiceConst.TYPE_METHOD_POST, jsondata);
			ExceptionDealUtil.loggerInfo(log, moduleName, "修订结果反馈,图片ID" + fileID + ",返回信息json【" + data + "】");
			
			if(data.contains("Success:")){
				String index = data.substring(9, 10);
				if(Integer.parseInt(index)==0){
					throw new BusinessException("修订失败,ID为" + fileID);
				}
			}else{
				throw new BusinessException(data+",ID为" + fileID);
			}
			
		} catch (Exception e) {
			ExceptionDealUtil.loggerInfo(log, moduleName, "修订结果反馈数据出错" + e.getMessage());
			throw new BusinessException(e.getMessage());
		}
		return rtnmsg;
	}
	public void notRecheck (String json){
		try {
			String data = getData(InvInvoiceConst.batchessetinvalid, InvInvoiceConst.TYPE_METHOD_POST, json);
			if(!data.contains("Sucess")){
				throw new BusinessException("票据作废失败，请稍后再试");
			}
		} catch (Exception e) {
			log.error("作废票据调用取消复检失败"+e);
			throw new BusinessException("票据作废失败，请稍后再试");
		}
		
	}

}

package com.dzf.zxkj.platform.util.taxrpt.shandong.deal;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.taxrpt.shandong.TaxConst;

import java.util.HashMap;

public class ParseJsonData {

	// 根据json 解析出格式
	public static HashMap<String, String> getJsonData(String reStrYz) {

		HashMap<String, String> map = new HashMap<>();

		if (StringUtil.isEmpty(reStrYz)) {
			map.put(TaxConst.RETURN_ITEMKEY_NO, "-2");
			map.put(TaxConst.RETURN_ITEMKEY_OBJ, "未返回信息");
			return map;
		}
		JSONObject jobject = (JSONObject) JSON.parseObject(reStrYz);

		if (jobject == null || jobject.size() == 0) {
			map.put(TaxConst.RETURN_ITEMKEY_NO, "-2");
			map.put(TaxConst.RETURN_ITEMKEY_OBJ, "解析返回json失败");
			return map;
		}

		if (jobject.get(TaxConst.RETURN_ITEMKEY_NO) == null) {
			map.put(TaxConst.RETURN_ITEMKEY_NO, "-2");
			map.put(TaxConst.RETURN_ITEMKEY_OBJ, "解析返回标志失败");
		} else {
			map.put(TaxConst.RETURN_ITEMKEY_NO, jobject.getInteger(TaxConst.RETURN_ITEMKEY_NO).toString());
			map.put(TaxConst.RETURN_ITEMKEY_MSG, jobject.getString(TaxConst.RETURN_ITEMKEY_MSG));

			if (jobject.getString(TaxConst.RETURN_ITEMKEY_OBJ) != null) {
				Object o = jobject.get(TaxConst.RETURN_ITEMKEY_OBJ);
				if (o instanceof String) {
					map.put(TaxConst.RETURN_ITEMKEY_OBJ, jobject.getString(TaxConst.RETURN_ITEMKEY_OBJ));
				} else if (o instanceof JSONArray) {
					JSONArray arr = (JSONArray) o;
					JSONObject object = arr.getJSONObject(0);
					map.put(TaxConst.RETURN_ITEMKEY_XML, object.getString(TaxConst.RETURN_ITEMKEY_XML));
					map.put(TaxConst.RETURN_ITEMKEY_QC, object.getString(TaxConst.RETURN_ITEMKEY_QC));
					map.put(TaxConst.RETURN_ITEMKEY_TOKEN, object.getString(TaxConst.RETURN_ITEMKEY_TOKEN));
				}
			}
		}
		String xml = map.get(TaxConst.RETURN_ITEMKEY_XML);
		if (!StringUtil.isEmpty(xml))
			ParseXml.readXml(xml, map);
		return map;
	}

}

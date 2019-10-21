package com.dzf.zxkj.platform.service.gzgl.impl;

import com.dzf.zxkj.base.utils.DZFValueCheck;
import com.dzf.zxkj.jackson.utils.JsonUtils;
import com.dzf.zxkj.platform.model.gzgl.SalaryAccSetVO;
import com.dzf.zxkj.platform.model.gzgl.SalarySetTableVO;

import java.util.*;

public class SalaryTableGetter {

	public static SalarySetTableVO[] getGzJt(String pk_corp, SalaryAccSetVO vo, int iflag) {

		Map<String, String> map = null;

		String zy = null;
		SalarySetTableVO[] tables = null;

		switch (iflag) {
		case 1:
			map = getGzJt();
			zy = "工资计提";
			break;
		case 2:
			map = getGzff();
			zy = "工资发放";
			break;
		case 3:
			map = getGzQyJt();
			zy = "社保计提(企业部分)";
			break;
		}
		tables = getCommonSaraylTable(vo, map, zy);
		return tables;
	}

	private static SalarySetTableVO[] getCommonSaraylTable(SalaryAccSetVO vo, Map<String, String> map, String zy) {
		if (DZFValueCheck.isEmpty(map)) return new SalarySetTableVO[0];
		int i = 1;
		Iterator<Map.Entry<String, String>> entries = map.entrySet().iterator();
		List<SalarySetTableVO> list = new ArrayList<>();
		while (entries.hasNext()) {
			Map.Entry<String, String> entry = entries.next();
			SalarySetTableVO jtvo1 = new SalarySetTableVO();
			jtvo1.setXh(Integer.toString(i));
			jtvo1.setZy(zy);
			
			String value =entry.getValue();
			jtvo1.setFx(value.split("-")[1]);
			jtvo1.setKmsz(entry.getKey());
			jtvo1.setKjkm((String) vo.getAttributeValue(entry.getKey()));
//			if(StringUtil.isEmpty(jtvo1.getKjkm())){
//				continue;
//			}
			i = i + 1;
			list.add(jtvo1);
		}
		return list.toArray(new SalarySetTableVO[list.size()]);
	}

	public static SalaryAccSetVO setGzJt(SalaryAccSetVO vo, Object[] objs) {

		vo = getCommonSalaryAccSetVO(vo, objs);
		return vo;
	}

	private static SalaryAccSetVO getCommonSalaryAccSetVO(SalaryAccSetVO setvo, Object[] objs) {

		for (Object o : objs) {
			SalarySetTableVO tablevo = JsonUtils.deserialize(JsonUtils.serialize(o), SalarySetTableVO.class);
			setvo.setAttributeValue(tablevo.getKmsz(), tablevo.getKjkm());
		}
		return setvo;
	}

	private static Map<String, String> getGzJt() {
		Map<String, String> map = new LinkedHashMap<>();
		map.put("jtgz_gzfykm", "工资费用科目-借");
		map.put("jtgz_yfgzkm", "应付工资科目-贷");
		return map;
	}

	private static Map<String, String> getGzff() {

		Map<String, String> map = new LinkedHashMap<>();
		map.put("ffgz_yfgzkm", "应付工资科目-借");
		map.put("ffgz_sbgrbf", "养老保险科目-贷");
		map.put("ffgz_yilbxbf", "医疗保险科目-贷");
		map.put("ffgz_sybxbf", "失业保险科目-贷");
		map.put("ffgz_gjjgrbf", "公积金部分-贷");
		map.put("ffgz_grsds", "应交个税科目-贷");
		map.put("ffgz_xjlkm", "工资发放科目-贷");
		return map;
	}

	public static Map<String, String> getGzQyJt() {

		Map<String, String> map = new LinkedHashMap<>();
		map.put("jtgz_sbfykm", "社保费用科目-借");
		map.put("jitgz_qyfysbgrbf", "养老保险科目-借");
		map.put("jitgz_qyfyyilbxbf", "医疗保险科目-借");
		map.put("jitgz_qyfysybxbf", "失业保险科目-借");
		map.put("jitgz_qyfygjjgrbf", "公积金部分-借");
		map.put("jitgz_qyfygsbxkm", "工伤保险科目-借");
		map.put("jitgz_qyfyshybxkm", "生育保险科目-借");

		map.put("jtgz_yfsbkm", "应付社保科目-贷");
		map.put("jitgz_qyyfsbgrbf", "养老保险科目-贷");
		map.put("jitgz_qyyfyilbxbf", "医疗保险科目-贷");
		map.put("jitgz_qyyfsybxbf", "失业保险科目-贷");
		map.put("jitgz_qyyfgjjgrbf", "公积金部分-贷");
		map.put("jitgz_qyyfgsbxkm", "工伤保险科目-贷");
		map.put("jitgz_qyyfshybxkm", "生育保险科目-贷");
		return map;
	}

}

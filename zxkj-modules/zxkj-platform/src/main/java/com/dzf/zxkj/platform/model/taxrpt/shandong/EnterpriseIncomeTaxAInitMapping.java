package com.dzf.zxkj.platform.model.taxrpt.shandong;

import com.dzf.zxkj.platform.model.report.FzjgRptVO;
import com.dzf.zxkj.platform.service.taxrpt.shandong.InitFiledMapParse;

import java.util.HashMap;
import java.util.Map;

public class EnterpriseIncomeTaxAInitMapping implements InitFiledMapParse {

	private static String[] fields = new String[] { "sbQysdsczzsyjdsbqtxxVO", "sbQysdsczzsyjdsbQcsVO",
			"SBQysdsczzsyjdsbBzssrFbOneVO", "SBQysdsczzsyjdsbBzssrFbTwoVO", "SBQysdsczzsyjdsbJmsdseFbThreeVO",
			"fzjgxxGrid" };
	private static Map<String, String> mainNameMap = new HashMap<String, String>();
	private static Map<String, String> mainNameMap_1 = new HashMap<String, String>();
	private static Map<String, String> attach1NameMap = new HashMap<String, String>();
	private static Map<String, Map<String, String>> attach2NameMap = new HashMap<String, Map<String, String>>();

	private static Map<String, String> attach3NameMap = new HashMap<String, String>();

	private static Map<String, String> fzjgMap = new HashMap<String, String>();

	// 主表
	static {
		// 累计金额_前5年库中wmbwks值合计
		mainNameMap_1.put("mbksehj", "mbksehj");
		// 上期已缴金额，可用预缴余额，已用预缴余额
		mainNameMap_1.put("h13_ljje_sqyjje", "sqyjje");
		mainNameMap_1.put("h13_ljje_kyyjye", "kyyjye");
		mainNameMap_1.put("h13_ljje_yyyjje", "yyyjje");

		// 上期累计金额_营业收入
		mainNameMap.put("h2_ljje", "yysrLj");
		// 上期累计金额_营业成本
		mainNameMap.put("h3_ljje", "yycbLj");
		// 上期累计金额_利润总额
		mainNameMap.put("h4_ljje", "lrzeLj");
		// 上期累计金额_加:特定业务计算的应纳税所得额
		mainNameMap.put("h5_ljje", "tdywjsdynssdeLj");
		// 上期累计金额_减：不征税收入和税基减免应纳税所得额（请填附表1）
		mainNameMap.put("h6_ljje", "bzssrLj");
		// 上期累计金额_固定资产加速折旧（扣除）调减额 （请填附表２）
		mainNameMap.put("h7_ljje", "jzmzynssdeLj");
		// 累计金额_弥补以前年度亏损
		mainNameMap.put("h8_ljje", "mbyqndksLj");
		// 上期累计金额_特定业务预缴（征）所得税额
		mainNameMap.put("h14_ljje", "tdywyjzsdseLj");
		// 上期累计金额_上一纳税年度应纳税所得额
		mainNameMap.put("h19_ljje", "synsndynssdeLj");
		// 上期累计金额_本月（季）应纳税所得额
		mainNameMap.put("h20_ljje", "byjynssdeLj");
		// 上期累计金额_本月（季）应纳税所得额
		mainNameMap.put("h22_ljje", "byjynsdseLj");
		// 上期累计金额_本月（季）实际应纳所得税额
		mainNameMap.put("h24_ljje", "byjsjynsdseLj");
		// 上期累计金额_本月（季）税务机关确定的预缴所得税额
		mainNameMap.put("h26_ljje", "byjswjgqdyjsdseLj");
		// 上期累计金额_总机构分摊所得税额
		mainNameMap.put("h28_ljje", "zjgljyftdsdse");
		// 上期累计金额_财政集中分配所得税额
		mainNameMap.put("h29_ljje", "czjzfpsdseLj");
		// 上期累计金额_分支机构分摊所得税额
		mainNameMap.put("h30_ljje", "fzjgyftsdseLj");
		// 上期累计金额_其中：总机构独立生产经营部门应分摊所得税额
		mainNameMap.put("h31_ljje", "zjgdlscjybmyftsdseLj");
		// 上期累计金额_分支机构分配所得税额
		mainNameMap.put("h33_ljje", "fzjgfpsdseLj");

		// 上期累计金额_一、不征税收入
		attach1NameMap.put("bzssrh2ljje", "bzzsrLj");
		// 上期累计金额_1.国债利息收入
		attach1NameMap.put("bzssrh4ljje", "gzlxsrLj");
		// 上期累计金额_ 2.地方政府债券利息收入
		attach1NameMap.put("bzssrh5ljje", "dfzfzqlxsrLj");
		// 上期累计金额_3.符合条件的居民企业之间的股息、红利等权益性投资收益
		attach1NameMap.put("bzssrh6ljje", "fhtjdjmqyzjdgxhldqyxsyLj");
		// 上期累计金额_4.符合条件的非营利组织的收入
		attach1NameMap.put("bzssrh7ljje", "fhtjdfylzzdsrLj");
		// 上期累计金额_5.证券投资基金投资者取得的免税收入
		attach1NameMap.put("bzssrh8ljje", "zqtzjjtzzqddmssrLj");
		// 上期累计金额_6.证券投资基金管理人取得的免税收入
		attach1NameMap.put("bzssrh9ljje", "zqtzjjglrqddmssrLj");
		// 上期累计金额_7.中国清洁发展机制基金取得的收入
		attach1NameMap.put("bzssrh10ljje", "zgqjfzjzjjqddsrLj");
		// 上期累计金额_ 1.综合利用资源生产产品取得的收入
		attach1NameMap.put("bzssrh15ljje", "zhlyzysccpqddsrLj");
		// 上期累计金额_2.金融、保险等机构取得的涉农利息、保费收入
		attach1NameMap.put("bzssrh16ljje", "jrbxdjgqddsllxbfsrLj");
		// 上期累计金额_3.取得的中国铁路建设债券利息收入
		attach1NameMap.put("bzssrh17ljje", "qddzgtljszqlxsrLj");
		// 上期累计金额_4.其他：（请填写或选择减免项目名称及减免性质代码）
		attach1NameMap.put("bzssrh18ljje", "jjsrqtLj");
		// 上期累计金额_ 其中：免税项目
		attach1NameMap.put("bzssrh21ljje", "msxmLj");
		// 上期累计金额_减半征收项目
		attach1NameMap.put("bzssrh22ljje", "jbzsxmLj");
		// 上期累计金额_2.国家重点扶持的公共基础设施项目
		attach1NameMap.put("bzssrh23ljje", "gjzdfcdggjcssxmLj");
		// 上期累计金额_3.符合条件的环境保护、节能节水项目
		attach1NameMap.put("bzssrh24ljje", "fhtjdhjbhjnjsxmLj");
		// 上期累计金额_ 4.符合条件的技术转让项目
		attach1NameMap.put("bzssrh25ljje", "fhtjdjszrxmLj");
		// 上期累计金额_ 5.实施清洁发展机制项目
		attach1NameMap.put("bzssrh26ljje", "ssqjfzjzxmLj");
		// 上期累计金额_6.节能服务公司实施合同能源管理项目
		attach1NameMap.put("bzssrh27ljje", "jnfwgssshtnyglxmLj");

		Map<String, String> attach2NameMap_2 = new HashMap<String, String>();
		attach2NameMap.put("2", attach2NameMap_2);
		// 重要行业固定资产加速折旧_税会处理一致_房屋、建筑物_原值上期数据
		attach2NameMap_2.put("h3fwyz", "fwjzwyz");
		// 重要行业固定资产加速折旧_税会处理一致_房屋、建筑物_税收折旧（扣除）额_累计上期数据
		attach2NameMap_2.put("h3fwljzj", "fwjzwljzjkce");
		// 重要行业固定资产加速折旧_税会处理一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_2.put("h3jqzy", "jqsbhqtgdzcyz");
		// 重要行业固定资产加速折旧_税会处理一致_机器设备和其他固定资产_税收折旧（扣除）额_累计上期数据
		attach2NameMap_2.put("h3jqljzj", "jqsbhqtgdzcljzjkce");
		// 重要行业固定资产加速折旧_税会处理一致_合计_本期折旧（扣除）额_正常折旧额上期数据
		attach2NameMap_2.put("h3ljzjzczje", "hjzczjeljzjkce");

		Map<String, String> attach2NameMap_3 = new HashMap<String, String>();
		attach2NameMap.put("3", attach2NameMap_3);
		// 重要行业固定资产加速折旧_税会处理不一致_房屋、建筑物_原值上期数据
		attach2NameMap_3.put("h4fwyz", "fwjzwyz");
		// 重要行业固定资产加速折旧_税会处理不一致_房屋、建筑物_税收折旧（扣除）额_累计上期数据
		attach2NameMap_3.put("h4fwljzj", "fwjzwljzjkce");
		// 重要行业固定资产加速折旧_税会处理不一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_3.put("h4jqzy", "jqsbhqtgdzcyz");
		// 重要行业固定资产加速折旧_税会处理不一致_机器设备和其他固定资产_税收折旧（扣除）额_累计上期数据
		attach2NameMap_3.put("h4jqljzj", "jqsbhqtgdzcljzjkce");
		// 重要行业固定资产加速折旧_税会处理不一致_合计_累计折旧（扣除）额
		attach2NameMap_3.put("h4ljzjhjzje", "hjkjzjeljzjkce");

		Map<String, String> attach2NameMap_5 = new HashMap<String, String>();
		attach2NameMap.put("5", attach2NameMap_5);
		// 单价100万元以上专用研发设备_税会处理一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_5.put("h6jqzy", "jqsbhqtgdzcyz");
		// 单价100万元以上专用研发设备_税会处理一致_机器设备和其他固定资产_税收折旧（扣除）额_累计上期数据
		attach2NameMap_5.put("h6jqljzj", "jqsbhqtgdzcljzjkce");
		// 单价100万元以上专用研发设备_税会处理一致_合计_本期折旧（扣除）额_正常折旧额上期数据
		attach2NameMap_5.put("h6ljzjzczje", "hjzczjeljzjkce");

		Map<String, String> attach2NameMap_6 = new HashMap<String, String>();
		attach2NameMap.put("6", attach2NameMap_6);
		// 单价100万元以上专用研发设备_税会处理不一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_6.put("h7jqzy", "jqsbhqtgdzcyz");
		// 单价100万元以上专用研发设备_税会处理不一致_机器设备和其他固定资产_税收折旧（扣除）额_累计上期数据
		attach2NameMap_6.put("h7jqljzj", "jqsbhqtgdzcljzjkce");
		// 单价100万元以上专用研发设备_税会处理不一致_合计_累计折旧（扣除）额 _会计折旧额上期数据
		attach2NameMap_6.put("h7ljzjhjzje", "hjkjzjeljzjkce");

		Map<String, String> attach2NameMap_9 = new HashMap<String, String>();
		attach2NameMap.put("9", attach2NameMap_9);
		// （单价不超过100万元研发设备）税会处理一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_9.put("h10jqzy", "jqsbhqtgdzcyz");

		attach2NameMap_9.put("h10jqljzj", "jqsbhqtgdzcljzjkce");

		// （单价不超过100万元研发设备）税会处理一致_合计_本期折旧（扣除）额_正常折旧额上期数据
		attach2NameMap_9.put("h10ljzjzczje", "hjzczjeljzjkce");

		Map<String, String> attach2NameMap_10 = new HashMap<String, String>();
		attach2NameMap.put("10", attach2NameMap_10);
		// （单价不超过100万元研发设备）税会处理不一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_10.put("h11jqzy", "jqsbhqtgdzcyz");

		attach2NameMap_10.put("h11jqljzj", "jqsbhqtgdzcljzjkce");

		// （单价不超过100万元研发设备）税会处理不一致_合计_累计折旧（扣除）额
		attach2NameMap_10.put("h11ljzjhjzje", "hjkjzjeljzjkce");

		Map<String, String> attach2NameMap_12 = new HashMap<String, String>();
		attach2NameMap.put("12", attach2NameMap_12);
		// (5000元以下固定资产)税会处理一致_房屋、建筑物_原值上期数据
		attach2NameMap_12.put("h13fwyz", "fwjzwyz");

		attach2NameMap_12.put("h13fwljzj", "fwjzwljzjkce");

		attach2NameMap_12.put("h13jqljzj", "jqsbhqtgdzcljzjkce");

		// (5000元以下固定资产)税会处理一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_12.put("h13jqzy", "jqsbhqtgdzcyz");
		// (5000元以下固定资产)税会处理一致_合计_本期折旧（扣除）额_正常折旧额上期数据
		attach2NameMap_12.put("h13ljzjzczje", "hjzczjeljzjkce");

		Map<String, String> attach2NameMap_13 = new HashMap<String, String>();
		attach2NameMap.put("13", attach2NameMap_13);
		// (5000元以下固定资产)税会处理不一致_房屋、建筑物_原值上期数据
		attach2NameMap_13.put("h14fwyz", "fwjzwyz");

		attach2NameMap_13.put("h14fwljzj", "fwjzwljzjkce");

		attach2NameMap_13.put("h14jqljzj", "jqsbhqtgdzcljzjkce");

		// (5000元以下固定资产)税会处理不一致_机器设备和其他固定资产_原值上期数据
		attach2NameMap_13.put("h14jqzy", "jqsbhqtgdzcyz");
		// (5000元以下固定资产)_税会处理不一致_合计_累计折旧（扣除）额 _会计折旧额上期数据
		attach2NameMap_13.put("h14ljzjhjzje", "hjkjzjeljzjkce");

		// 上期累计金额_合计
		attach3NameMap.put("jmsdseh1ljje", "hjLj");
		// 上期累计金额_一、符合条件的小型微利企业
		attach3NameMap.put("jmsdseh2ljje", "fhtjdxxwlqyLj");
		// 上期累计金额_其中：减半征税
		attach3NameMap.put("jmsdseh3ljje", "jbzsLj");
		// 上期累计金额_二、国家需要重点扶持的高新技术企业
		attach3NameMap.put("jmsdseh4ljje", "gjxyzdfcdgxjsqyLj");
		// 上期累计金额_四、其他专项优惠
		attach3NameMap.put("jmsdseh6ljje", "qtzxyhLj");
		// 上期累计金额_（二）经营性文化事业单位转制企业
		attach3NameMap.put("jmsdseh8ljje", "yjxwhsydwzzqyLj");
		// 上期累计金额_（三）动漫企业
		attach3NameMap.put("jmsdseh9ljje", "dmqyLj");
		// 上期累计金额_（七）技术先进型服务企业
		attach3NameMap.put("jmsdseh13ljje", "jsxjxfwqyLj");
		// 上期累计金额_（十二）集成电路线宽小于（含）0.25微米的集成电路生产企业
		attach3NameMap.put("jmsdseh17ljje", "jcdlxkxyldbjcdlqyLj");
		// 上期累计金额_（十三）投资额超过80亿元人民币的集成电路生产企业
		attach3NameMap.put("jmsdseh18ljje", "jcdlxkxyldewjcdlqyLj");
		// 上期累计金额_（十三）投资额超过80亿元人民币的集成电路生产企业
		attach3NameMap.put("jmsdseh19ljje", "tzcgbsyrmbjcdlqyLj");
		// 上期累计金额_（十四）新办集成电路设计企业
		attach3NameMap.put("jmsdseh20ljje", "xbjcdlsjqyLj");
		// 上期累计金额_（十五）国家规划布局内重点集成电路设计企业
		attach3NameMap.put("jmsdseh21ljje", "gjghbjnzdjcdlsjqyLj");
		// 上期累计金额_（十六）符合条件的软件企业
		attach3NameMap.put("jmsdseh22ljje", "fhtjdrjqyLj");
		// 上期累计金额_（十七）国家规划布局内重点软件企业
		attach3NameMap.put("jmsdseh23ljje", "gjghbjnzdrjqyLj");
		// 上期累计金额_（十八）设在西部地区的鼓励类产业企业
		attach3NameMap.put("jmsdseh24ljje", "szxbdqdgllcyqyLj");
		// 上期累计金额_（十九）符合条件的生产和装配伤残人员专门用品企业
		attach3NameMap.put("jmsdseh25ljje", "fhtjdschzpscryzmypqyLj");
		// 上期累计金额_（二十一）享受过渡期税收优惠企业
		attach3NameMap.put("jmsdseh27ljje", "xsgdqssyhqyLj");
		// 上期累计金额_（二十三）其他1：
		attach3NameMap.put("jmsdseh29ljje", "qtzxyhqt1Lj");

		fzjgMap.put("sb10412005vo.data1.fzjgqk", null);

	}

	@Override
	public Map<String, String> getNameMap(String key) {

		String[] keys = key.split("--");
		String vo = keys[0];
		String index = keys[1];
		if ("sbQysdsczzsyjdsbQcsVO".equals(vo)) {
			return mainNameMap;
		} else if ("sbQysdsczzsyjdsbqtxxVO".equals(vo)) {
			return mainNameMap_1;
		} else if ("SBQysdsczzsyjdsbBzssrFbOneVO".equals(vo)) {
			return attach1NameMap;
		} else if ("SBQysdsczzsyjdsbBzssrFbTwoVO".equals(vo)) {
			return attach2NameMap.get(index);
		} else if ("SBQysdsczzsyjdsbJmsdseFbThreeVO".equals(vo)) {
			return attach3NameMap;
		} else if ("fzjgxxGrid".equals(vo)) {
			return fzjgMap;
		}
		return null;

	}

	@Override
	public String getIndexField(String vname) {
		if ("fzjgxxGrid".equals(vname)) {
			return "rawType";
		}
		return "ewbhxh";
	}

	@Override
	public String[] getFields() {
		return fields;
	}

	@Override
	public Class<?> getFieldType(String fieldName) {
		if ("fzjgxxGrid".equals(fieldName)) {
			return FzjgRptVO.class;
		}
		return null;
	}
}

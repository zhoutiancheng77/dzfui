package com.dzf.zxkj.report.service.cwbb.impl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.common.constant.DZFConstant;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.SafeCompute;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.report.FseJyeVO;
import com.dzf.zxkj.platform.model.report.YyFpVO;
import com.dzf.zxkj.platform.service.IZxkjPlatformService;
import com.dzf.zxkj.report.service.cwbb.IYyFpService;
import com.dzf.zxkj.report.service.cwzb.IFsYeReport;
import com.dzf.zxkj.report.utils.VoUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service("yyfpser")
public class YyFpServiceImpl implements IYyFpService {

	@Autowired
	private IZxkjPlatformService zxkjPlatformService;

	@Autowired
	private IFsYeReport gl_rep_fsyebserv;


	private String[][] defaultstr = new String[][] { { "本年盈余", "", "", "盈余分配", "", "" },
			{ "一、经营收入", "1", "ljfsdf(5001)", "四、本年盈余", "16", "hc15+0" },
			{ "　加：投资收益", "2", "ljfsdf(5101)", "　加：年初未分配盈余", "17", "ncye(3302)" },
			{ "　减：经营支出", "5", "ljfsjf(5201)", "　　　其它转入", "18", "ljfsdf(330201 )" },
			{ "　　　管理费用", "7", "ljfsjf(5202)", "五、可分配盈余", "21", "hc16+hc17+hc18" },
			{ "二、经营收益", "10", "hc1+hc2-hc5-hc7", "　减：可提取盈余公积", "22", "ljfsjf(330202)" },
			{ "　加：其它收入", "11", "ljfsdf(5002)", "　　应付盈余返还", "24", "ljfsjf(330203)" },
			{ "　 减：其它支出", "12", "ljfsjf(5209)", "　　应付剩余盈余", "25", "ljfsjf(330204)" },
			{ "三、本年盈余", "15", "hc10+hc11-hc12", "六、年末未分配盈余", "28", "hc21-hc22-hc24-hc25" }, };

	private List<YyFpVO> getDefaultList() {
		List<YyFpVO> list = new ArrayList<YyFpVO>();
		for (String[] strs : defaultstr) {
			YyFpVO vo = new YyFpVO();
			vo.setXm1(strs[0]);
			vo.setHc1(strs[1]);
			vo.setFormula1(strs[2]);

			vo.setXm2(strs[3]);
			vo.setHc2(strs[4]);
			vo.setFormula2(strs[5]);
			list.add(vo);
		}

		return list;
	}

	@Override
	public List<YyFpVO> queryList(QueryParamVO paramvo) throws DZFWarpException {
		paramvo.setQjq(DateUtils.getPeriod(paramvo.getBegindate1()));
		paramvo.setQjz(DateUtils.getPeriod(paramvo.getBegindate1()));
		paramvo.setEnddate(paramvo.getBegindate1());
		paramvo.setXswyewfs(DZFBoolean.FALSE);
		FseJyeVO[] fsjevos = gl_rep_fsyebserv.getFsJyeVOs(paramvo, 1);
		String queryAccountRule = zxkjPlatformService.queryAccountRule(paramvo.getPk_corp());
		// 查询发生额余额表数据
		List<YyFpVO> list = getDefaultList();
		if(fsjevos!=null && fsjevos.length>0){
			for(YyFpVO yyfpvo:list){
				if(!StringUtil.isEmpty(yyfpvo.getFormula1())){
					putJe("je1",yyfpvo,fsjevos,yyfpvo.getFormula1(),queryAccountRule,list);
				}
			}
			for(YyFpVO yyfpvo:list){
				if(!StringUtil.isEmpty(yyfpvo.getFormula2())){
					putJe("je2",yyfpvo,fsjevos,yyfpvo.getFormula2(),queryAccountRule,list);
				}
			}
		}
		return list;
	}

	private void putJe(String column, YyFpVO yyfpvo, FseJyeVO[] fsjevos, String formula, String queryAccountRule,List<YyFpVO> lists) {
		DZFDouble res = DZFDouble.ZERO_DBL;
		if (!StringUtil.isEmpty(column)) {
			String replanstr = "";
			String splitstr = "";
			if (formula.indexOf("ljfsdf") >= 0) {//累计发生贷方
				replanstr ="ljfsdf";
			} else if (formula.indexOf("ljfsjf") >= 0) {//累计发生借方
				replanstr ="ljfsjf";
			} else if (formula.indexOf("ncye") >= 0) {//期初余额
				replanstr ="ncye";
			} else if (formula.indexOf("+") >= 0 || formula.indexOf("-") >= 0) {//
				splitstr ="\\+|\\-";
			}
			if (!StringUtil.isEmpty(replanstr)) {
				String kmbm = formula.replace(replanstr, "");
				kmbm = kmbm.replace("(", "");
				kmbm = kmbm.replace(")", "");
				// 考虑科目编码升级
				String newkmbm = zxkjPlatformService.getNewRuleCode(kmbm, DZFConstant.ACCOUNTCODERULE, queryAccountRule);
				for (FseJyeVO fsvo : fsjevos) {
					if (newkmbm.equals(fsvo.getKmbm())) {
						if ("ljfsdf".equals(replanstr)) {
							res = VoUtils.getDZFDouble(fsvo.getDftotal());
						} else if ("ljfsjf".equals(replanstr)) {
							res = VoUtils.getDZFDouble(fsvo.getJftotal());
						} else if ("ncye".equals(replanstr)) {
							//只是会一方面有值
							res = SafeCompute.add(fsvo.getQcjf(), fsvo.getQcdf());
						}
					}
				}
			}
			
			if (!StringUtil.isEmpty(splitstr)) {
				String[] hcs = formula.split(splitstr);
				if (hcs != null && hcs.length > 0) {
					for (String hc : hcs) {
						for (YyFpVO fpvo : lists) {
							if (!StringUtil.isEmpty(hc)) {
								if (hc.equals("hc"+fpvo.getHc1())) {
									formula = formula.replace(hc, "" + VoUtils.getDZFDouble(fpvo.getJe1()));
								} else if (hc.equals("hc"+fpvo.getHc2())) {
									formula = formula.replace(hc, "" + VoUtils.getDZFDouble(fpvo.getJe2()));
								}
							}
						}
					}
					ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
					log.info("公式:"+formula);
					 try {
						Object value = jse.eval(formula);
						res = new DZFDouble(value.toString());
					} catch (ScriptException e) {
						throw new WiseRunException(e);
					}
				}
			}
		}
		yyfpvo.setAttributeValue(column, res);
	}

}

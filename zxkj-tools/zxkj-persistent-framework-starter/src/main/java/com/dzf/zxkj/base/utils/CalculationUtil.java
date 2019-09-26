package com.dzf.zxkj.base.utils;

import com.dzf.zxkj.base.exception.DZFWarpException;
import lombok.extern.slf4j.Slf4j;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

/**
 * 公式计算
 * @author zhangj
 *
 */
@Slf4j
public class CalculationUtil {

	public static Object CalculationFormula(String formula) throws DZFWarpException {
		try {
			ScriptEngineManager manager = new ScriptEngineManager();
			ScriptEngine se = manager.getEngineByName("js");
//			se.eval(formula);
			return se.eval(formula);
		} catch (ScriptException e) {
			log.error("错误公式:"+formula);
			return 2;//未知
		}
	}
	
	
	
}

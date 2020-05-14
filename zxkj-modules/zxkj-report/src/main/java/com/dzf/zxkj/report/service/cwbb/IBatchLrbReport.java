package com.dzf.zxkj.report.service.cwbb;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.report.LrbVO;

import java.util.Map;

/**
 * 利润表批量操作
 */
public interface IBatchLrbReport {

    Map<String, double[]> queryLrbFromCorpids (String period, String[] corpids)  throws DZFWarpException;

}

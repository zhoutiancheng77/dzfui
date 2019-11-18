package com.dzf.zxkj.platform.service.taxrpt;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.report.KmQmJzExtVO;

import java.util.List;
import java.util.Map;

/**
 * 科目的公共查询接口
 * @author zhangj
 *
 */
public interface IKmQryService {

	public Map<String,List<KmQmJzExtVO>> resmapvos(String[] corpvos, String[] kms, String period) throws DZFWarpException;
	
}

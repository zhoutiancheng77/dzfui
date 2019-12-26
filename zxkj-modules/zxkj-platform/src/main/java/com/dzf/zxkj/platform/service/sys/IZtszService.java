package com.dzf.zxkj.platform.service.sys;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.query.QueryParamVO;
import com.dzf.zxkj.platform.model.sys.CorpTaxVo;
import com.dzf.zxkj.platform.model.sys.UserVO;

import java.util.List;
import java.util.Set;

public interface IZtszService {

	public void updateCorpTaxVo(CorpTaxVo corptaxvo, String selTaxReportIds, String unselTaxReportIds) throws DZFWarpException;

	List<CorpTaxVo> query(QueryParamVO queryvo, UserVO uservo, Set<String> clist) throws DZFWarpException;
}

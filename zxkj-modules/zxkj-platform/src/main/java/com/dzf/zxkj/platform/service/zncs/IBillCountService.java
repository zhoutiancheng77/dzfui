package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.zncs.BillCountVO;
import com.dzf.zxkj.platform.model.zncs.BillDetailVO;

import java.util.List;

public interface IBillCountService {

	public List<BillCountVO> queryList(String period, String pk_corp)throws DZFWarpException;
	
	public List<BillDetailVO> queryIntertemporal(String period, String pk_corp, String flag)throws DZFWarpException;
	
}

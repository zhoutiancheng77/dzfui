package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.jzcl.YJJZSetVO;
import com.dzf.zxkj.platform.model.jzcl.YjjzVO;

import java.util.List;

public interface IYJJZService {
	
	public YJJZSetVO queryset(String userid, String pk_corp) throws DZFWarpException;
	
	public void savejzset(List<YJJZSetVO> zlist) throws DZFWarpException;
	
	public List<YjjzVO> query(List<String> corppks, DZFDate dateq, DZFDate datez, String usesrid, String dopedate) throws DZFWarpException;

	public void updateYjjz(YjjzVO[] vos, String userid, String pk_corp)  throws DZFWarpException;
	
	public QmclVO queryqmcl(String qmid, String pk_corp)throws DZFWarpException;
	
}

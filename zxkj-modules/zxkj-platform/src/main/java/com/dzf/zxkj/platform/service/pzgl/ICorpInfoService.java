package com.dzf.zxkj.platform.service.pzgl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.jzcl.QmclVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;

public interface ICorpInfoService {
	
	//查询企业信息
	public CorpVO[] queryCorpInfo(String pk_corp) throws DZFWarpException;
	
	//查询公司是否填制凭证
	public List<TzpzHVO> isVoucher(String pk_corp)throws  DZFWarpException;
	
	//查询公司是否期间损益
	public QmclVO isQjsy(String pk_corp, String mounth)throws  DZFWarpException;
	

	/***
	 * 
	 * @param pk_corp
	 * @param yhzc
	 * @param cbjzlx
	 * @param cbtax
	 * @param leatax
	 * @throws DZFWarpException
	 */
	public void update(StringBuffer msg, String pk_corp, String yhzc,
                       String cbjzlx, String cbtax, String leatax, String demo)throws DZFWarpException;
	
	public void checkCbjzlx(String buildic, int cblx) throws DZFWarpException;

	/**
	 * 获取增值税税负率
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public DZFDouble getTaxBurdenRate(String pk_corp) throws DZFWarpException;
}

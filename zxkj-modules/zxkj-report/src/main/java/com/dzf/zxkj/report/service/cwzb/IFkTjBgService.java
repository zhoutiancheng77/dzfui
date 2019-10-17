package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.report.FkTjBgVo;
import com.dzf.zxkj.platform.model.report.FkTjSetVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IFkTjBgService {

	/**
	 * 查询
	 * @return
	 * @throws DZFWarpException
	 */
	public FkTjSetVo[] query(String pk_corp, DZFDate begdate, DZFDate enddate) throws DZFWarpException;
	
	
	/**
	 * 保存
	 * @param vo
	 * @throws DZFWarpException
	 */
	public void save(FkTjSetVo vo) throws DZFWarpException;
	
	/**
	 * 计算增值税
	 * @param year
	 * @param cpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] queryZzsBg(String year, CorpVO cpvo) throws DZFWarpException;

	/**
	 * 计算所得税
	 * 季度税负：本季度的税负率=(所得税*25%）/（本季度营业收入）*100%
	 * 累计税负：当前季度累计的税负率=(所得税*25%）/（本年营业收入）*100%
	 * @param year
	 * @param cpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public Object[] querySdsBg(String year, CorpVO cpvo) throws DZFWarpException;
	
	/**
	 * 风控体检报告
	 * @param year
	 * @param cpvo
	 * @return
	 * @throws DZFWarpException
	 */
	public FkTjBgVo[] queryFktj(DZFDate enddate, CorpVO cpvo) throws DZFWarpException;

}

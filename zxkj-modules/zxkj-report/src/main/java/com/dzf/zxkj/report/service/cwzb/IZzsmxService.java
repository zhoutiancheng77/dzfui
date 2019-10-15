package com.dzf.zxkj.report.service.cwzb;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.query.QueryParamVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.yscs.DzfpscReqBVO;

import java.util.List;

/****
 *  增值税明细
 * @author asoka
 *
 */
public interface IZzsmxService {

	/****
	 * 获取增值税明细
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DzfpscReqBVO> getZzsmx(QueryParamVO vo) throws DZFWarpException;
	
	
	/****
	 * 获取增值税明细行数
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public long getZzsmxCount(QueryParamVO vo) throws  DZFWarpException ;
	
	
	/****
	 * 生成凭证
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public List<DzfpscReqBVO> saveAsVoucher(CorpVO corpvo, UserVO uservo, QueryParamVO vo, List<DzfpscReqBVO> vos) throws  DZFWarpException ;
	
	
	/****
	 * 根据发票号码获取对应的pdf文件路径
	 * @param vo
	 * @return
	 * @throws DZFWarpException
	 */
	public String getFilePath(String fphm) throws  DZFWarpException ;
}

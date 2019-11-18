package com.dzf.zxkj.platform.service.jzcl;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgSetVO;
import com.dzf.zxkj.platform.model.jzcl.QmGzBgVo;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.util.List;
import java.util.Map;


/**
 * 期末关账报表
 * 
 * @author admin
 *
 */
public interface IQmGzBgService {

	public Map<String, List<QmGzBgVo>> queryQmgzZb(String pk_corp, String period) throws DZFWarpException;
	
	/**
	 * 财务处理完整性(单独拉出来)
	 * 
	 * @param reslist
	 * @param pk_corp
	 * @param period
	 */
	public void handCwcl(Map<String, List<QmGzBgVo>> qmgzbgmap, String pk_corp, String period, CorpVO cpvo) ;
	
	
	public void saveQmgzBg(QmGzBgSetVO setvo, String operid) throws DZFWarpException;

}

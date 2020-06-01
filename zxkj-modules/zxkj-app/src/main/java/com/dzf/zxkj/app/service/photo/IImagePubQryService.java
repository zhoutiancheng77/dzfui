package com.dzf.zxkj.app.service.photo;

import com.dzf.zxkj.app.model.app.remote.AppCorpCtrlVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;

import java.util.List;
import java.util.Map;

/**
 * 图片查询公共接口
 * 
 * @author zhangj
 *
 */
public interface IImagePubQryService {

	/**
	 * 根据条件查询图片组信息
	 * 
	 * @param uBean
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ImageGroupVO> queryImgGroupvo(String[] corpids, String account_id, DZFDate startdate, DZFDate enddate,
											  String groupid, String wherepart) throws DZFWarpException;
	
	
	/**
	 * 图片组的权限
	 * @param mapcorp
	 * @param gplist
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String,Integer> queryUserImgPower(Map<String, AppCorpCtrlVO> mapcorp, List<ImageGroupVO> gplist) throws DZFWarpException;

}

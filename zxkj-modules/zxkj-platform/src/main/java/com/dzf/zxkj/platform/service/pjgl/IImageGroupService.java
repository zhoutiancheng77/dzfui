package com.dzf.zxkj.platform.service.pjgl;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageLibraryVO;
import com.dzf.zxkj.platform.model.pjgl.FastOcrStateInfoVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface IImageGroupService {

	// 查询
	public List<ImageLibraryVO> queryLibrary(String period, String pk_corp) throws DZFWarpException;

	public List<ImageGroupVO> queryGroup(String period, String pk_corp) throws DZFWarpException;

	public ImageGroupVO queryGrpByID(String pk_corp, String id) throws DZFWarpException;

	public ImageLibraryVO queryLibByID(String pk_corp, String id) throws DZFWarpException;

	public void update(ImageLibraryVO vo) throws DZFWarpException;

	public void save(ImageLibraryVO vo) throws DZFWarpException;

	public void save(ImageGroupVO vo) throws DZFWarpException;

	// 获取今天Code最大的图片组
	public long getNowMaxImageGroupCode(String pk_corp);

	public long queryLibCountByGID(String pk_corp, String gid) throws DZFWarpException;

	public String queryLibByName(String pk_corp, String imgName) throws DZFWarpException;

	public void deleteImg(ImageLibraryVO vo) throws DZFWarpException;

	// 备份图片组信息
	public void saveImageGroupBackUp(Map<String, ImageGroupVO> imageGroupMap, int type);

	public void saveImageGroupBackUp(ImageGroupVO imageGroupVO, int type);

	public List<ImageGroupVO> queryImageGroupByCondition(CorpVO corpvo, String value, String Condition);

	public void isQjSyJz(String pk_corp, String cvoucherdate) throws DZFWarpException;

	// 图片删除，方法供会计端删除图片信息，deleteImg方法也是删除图片信息，但不能满足要求
	public void deleteKJImg(String pk_corp, String[] imageKeys) throws DZFWarpException;
	
	//该删除供图片浏览使用 因为手机端上传的进行退回操作
	public void deleteImgFromTpll(String pk_corp, String userid, String desc, String[] delTelIds, String[] delOthIds,String[] clzBidDate) throws DZFWarpException;

	// 根据文件MD5查询图片信息
	public ImageLibraryVO[] queryLibByMD(String pk_corp, String md) throws DZFWarpException;

	public ImageLibraryVO uploadSingFile(CorpVO corpvo, UserVO uservo, MultipartFile infiles, String g_id, String period, String pjlxType) throws DZFWarpException;
	
	public String getUploadImgMD(MultipartFile file, CorpVO corpvo) throws DZFWarpException;
	
	public void saveCreatePz(ImageLibraryVO libvo, CorpVO corpVO, String gid, String pjlxType) throws DZFWarpException;
	
	public long queryBackLibCount(String pk_corp, String gid) throws DZFWarpException;
	
	public void processImg(String gid, ImageGroupVO grpvo, ImageLibraryVO libvo) throws DZFWarpException;
	/**
	 * 获取上次图片统计信息
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, Object> getPicStatistics(String pk_corp,
												String beginDate, String endDate, String serdate) throws DZFWarpException;

	public ImageLibraryVO uploadSingFileByFastTax(CorpVO corpvo, UserVO uservo, MultipartFile infiles, String[] filenames, String g_id, String period, String invoicedata, String pjlxType) throws DZFWarpException;

	/**
	 * 拆分图片组
	 * @param pk_corp
	 * @param pk_image_group
	 * @throws DZFWarpException
	 */
	public void processSplitGroup(String pk_corp, String pk_image_group) throws DZFWarpException;
	/**
	 * 拆分合并的图片组
	 * @param pk_corp
	 * @param pk_image_group
	 * @throws DZFWarpException
	 */
	public void processSplitMergedGroup(String pk_corp, String pk_image_group) throws DZFWarpException;
	/**
	 * 给有该公司权限的会计人员发送消息
	 * @param msgkey
	 * @param pk_corp
	 * @param selYear
	 * @param selMon
	 * @param uservo
	 * @param il
	 * @throws DZFWarpException
	 */
	public void saveMsg(String msgkey, String pk_corp, String selYear, String selMon, UserVO uservo, ImageLibraryVO il) throws DZFWarpException;

	/**
	 * 查询扫描上传状态vo
	 * @param pk_corp
	 * @param sourceids
	 * @throws DZFWarpException
	 */
	public  List<FastOcrStateInfoVO>  getOcrStateInfoVOS(String pk_corp, String sourceids) throws DZFWarpException;
	
	/**
	 * 图片重新生成凭证
	 * @author 
	 * @param pk_corp
	 * @param imageKeys
	 * @throws DZFWarpException
	 */
	public void recogImage(String pk_corp, String[] imageKeys) throws DZFWarpException;
	
	/**
	 * 合并图片组
	 * 
	 * @param pk_corp
	 * @param destGroup 为空时取groups中第一个
	 * @param groups
	 * @return 合并后图片组主键
	 * @throws DZFWarpException
	 */
	public String processMergeGroup(String pk_corp, String destGroup, List<String> groups) throws DZFWarpException;
}

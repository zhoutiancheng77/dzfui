package com.dzf.zxkj.app.service.app.act;


import com.dzf.zxkj.app.model.ticket.ZzsTicketHVO;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDouble;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;

/**
 * 业务处理
 * 
 * @author zhangj
 *
 */
public interface IAppBusinessService {

	/**
	 * 根据图片信息生成凭证
	 * @param groupvo
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	public String saveVoucherFromPic(String groupvo, String pk_corp, SingleObjectBO sbo) throws DZFWarpException;

		/**
	 * 根据票据信息生成对应的凭证信息
	 *
	 * @param uvo
	 * @param cpvo
	 * @param zzshvo
	 * @param groupvo
	 */
	public void saveVoucherFromTicket(DZFDate kprq, DZFDouble mny, String paymetod, String memo, String pk_corp, String account_id,
									  ZzsTicketHVO zzshvo, ImageGroupVO groupvo) throws DZFWarpException;




//	/**
//	 *
//	 * @param uvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BusinessResonseBeanVO getWorkTips(UserBeanVO uvo) throws DZFWarpException;
//
//	/**
//	 * 生成凭证+图片
//	 *
//	 * @param uvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public void saveVoucherFromTicket(UserBeanVO uvo, ImageGroupVO groupvo) throws DZFWarpException;
//
//	/**
//	 * 生成图片
//	 * @param uvo
//	 * @throws DZFWarpException
//	 */
//	public ImageGroupVO saveImgFromTicket(UserBeanVO uvo) throws DZFWarpException;
//
//	/**
//	 * 获取票据信息
//	 *
//	 * @param uvo
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public BusinessResonseBeanVO saveTickMsg(UserBeanVO uvo) throws DZFWarpException;
//

//	/**
//	 * 根据二维码，获取票据信息，同时保存发票信息
//	 * @param pk_corp
//	 * @param drcode
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public SuperVO saveTickFromPt(String pk_corp, String admincorpid,
//                                  String drcode, String account_id, Integer power, Integer limitcount)  throws DZFWarpException;
//
//	/**
//	 * 获取常见问题
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public List<ProblemVo>  getProblems() throws DZFWarpException;
//
//
//	/**
//	 * 查询更多服务
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public List<MoreServiceHVo> queryMoreService(String pk_corp) throws DZFWarpException;
//
//	/**
//	 * 查询服务详情
//	 * @param pk_corp
//	 * @param xlid
//	 * @return
//	 * @throws DZFWarpException
//	 */
//	public MoreServiceBVo queryMoreServiceDetail(String pk_corp, String xlid, String domain) throws DZFWarpException;
}

package com.dzf.zxkj.app.service.message;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.enums.MsgtypeEnum;
import com.dzf.zxkj.platform.model.message.MsgAdminVO;

/**
 * app消息发送
 * @author zhangj
 *
 */
public interface IMessageSendService {

	/**
	 * 保存类型消息(给代账公司发送消息)
	 * @param userid 发送人
	 * @param pk_account
	 * @param content
	 * @param pk_corp
	 * @param pk_temp_corp
	 * @param id
	 * @param enumtype
	 * @throws DZFWarpException
	 */
	public  void saveTypeMsg(String userid, String pk_account, String content, String pk_corp, String pk_temp_corp,
							 String id, MsgtypeEnum enumtype, String period) throws DZFWarpException;
	
	

	/**
	 * app上传图片需要处理的发送消息
	 * @param pk_corp
	 * @param grouvo
	 * @throws DZFWarpException
	 */
	public void saveMsgVoFromImage(String pk_corp, String pk_image_group) throws DZFWarpException;
	
	
	
	/**
	 * 删除类型消息
	 * @throws DZFWarpException
	 */
	public void deleTypeMsg(String sourceid,String pk_corp,String pk_temp_corp,Integer msgtype) throws DZFWarpException;
	
	/**
	 * 根据消息处理历史
	 * @param pk_corp
	 * @param period
	 * @param msg_id
	 * @throws DZFWarpException
	 */
	public void saveAdminNsMsg(String pk_corp,String account_id,String period,String msg_id,Object msg_hand) throws DZFWarpException;
	
	/**
	 * 根据消息处理历史
	 * @param pk_corp
	 * @param period
	 * @param msg_id
	 * @throws DZFWarpException
	 */
	public void saveAdminFWPJ(String pk_corp,String account_id,String msg_id,Integer ztpj,String ztpj_cotent,String khpj) throws DZFWarpException;
	
	/**
	 * 根据消息VO
	 * @param pk_corp
	 * @param period
	 * @param msg_id
	 * @throws DZFWarpException
	 */
	public void saveAdminNsMsg(String pk_corp, String account_id, String period, MsgAdminVO msgadminvo, Object msg_hand) throws DZFWarpException;
	
	/**
	 * 根据工作日志发送消息
	 * @param id
	 * @param pk_corp
	 * @param account_id
	 * @throws DZFWarpException
	 */
	public void saveMsgForWork(String id, String pk_corp, String pk_temp_corp,
			String account_id,String jsr,MsgtypeEnum typeenum,Object obj) throws DZFWarpException;
}

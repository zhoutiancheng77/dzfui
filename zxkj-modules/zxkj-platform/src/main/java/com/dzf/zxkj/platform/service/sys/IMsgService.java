package com.dzf.zxkj.platform.service.sys;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import com.dzf.zxkj.platform.model.image.ImageTurnMsgVO;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;

/**
 * 消息记录表
 * @author zhangj
 *
 */
public interface IMsgService {

	/**
	 * 根据图片生成消息vo
	 * @param grouvo
	 * @throws DZFWarpException
	 */
	public void saveMsgVoFromImage(String pk_corp, String currid, ImageTurnMsgVO msgvo, ImageGroupVO grouvo) throws DZFWarpException;


	/**
	 * 根据来源删除消息信息
	 * @param pripk
	 * @throws DZFWarpException
	 */
	public void  deleteMsg(ImageGroupVO grouvo) throws DZFWarpException;


	/**
	 * 保存图片记录信息
	 * @throws DZFWarpException
	 */
	public void saveImageRecord(String pk_image_group,
                                String pk_source_id, String[] currs, String[] nextid, String currope,
                                String pk_corp, String pk_temp_corp, String vmemo) throws DZFWarpException;

	public void newSaveMsgVoFromImage(String pk_corp, TzpzHVO headVO, ImageGroupVO grpvo) throws DZFWarpException;
	
}

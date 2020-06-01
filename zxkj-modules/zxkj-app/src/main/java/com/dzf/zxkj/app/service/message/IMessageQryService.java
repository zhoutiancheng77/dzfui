package com.dzf.zxkj.app.service.message;


import com.dzf.zxkj.app.model.resp.bean.MessageResponVo;
import com.dzf.zxkj.app.model.resp.bean.ResponseBaseBeanVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.util.List;

/**
 * 获取消息的分类
 * @author zhangj
 *
 */
public interface IMessageQryService {

	
	/**
	 * 获取系统消息(无具体的内容，只有分类和未读取数量)
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO getAllMessage(UserBeanVO userBean) throws DZFWarpException;
	
	
	/**
	 * 获取某个类型的消息
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO getTypeMessage(UserBeanVO userBean) throws DZFWarpException;
	
	/**
	 * 获取所有消息的未读取的数量
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public Integer getAllMessageUnRead(String pk_corp ,String pk_tempcorp,String account_id) throws DZFWarpException;
	
	
	/**
	 * 获取系统消息
	 * @param userBean
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO  getMessage(UserBeanVO userBean) throws DZFWarpException;
	
	/**
	 * 获取应缴纳税申报
	 * @param id
	 * @return
	 * @throws DZFWarpException
	 */
	public ResponseBaseBeanVO getYJNssb(String id) throws DZFWarpException ;
	
	/**
	 * 
	 * @param pk_corp
	 * @param account_id
	 * @param pk_tempcorp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<MessageResponVo> getMsgRespon(String pk_corp, String account_id, String pk_tempcorp) throws DZFWarpException;
	
}

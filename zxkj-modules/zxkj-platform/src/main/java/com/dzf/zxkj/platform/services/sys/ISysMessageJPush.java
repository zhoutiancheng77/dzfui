package com.dzf.zxkj.platform.services.sys;

import com.dzf.zxkj.common.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.JPMessageBean;

public interface ISysMessageJPush {
	/**
	 * 指定内容，对象推送消息
	 * 消息指定参考Bean注释
	 * */
	public void sendSysMessage(JPMessageBean jpmessagebean) throws DZFWarpException;
	
	/**
	 * 给所有手机用户发送系统消息
	 * */
	public void sendSysMessageAll(String content) throws DZFWarpException;
	
	/**
	 * 小薇无忧APP推送消息
	 * 指定内容，对象推送消息
	 * 消息指定参考Bean注释
	 * */
	public void sendAdminMessage(JPMessageBean jpmessagebean) throws DZFWarpException;
	
	/**
	 * 小薇无忧APP推送消息
	 * 给所有手机用户发送系统消息
	 * */
	public void sendAdminMessageAll(String content) throws DZFWarpException;
	
}

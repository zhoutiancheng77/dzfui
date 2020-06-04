package com.dzf.zxkj.app.service.ticket;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;

import java.util.List;

/**
 * 票通接口
 * @author zhangj
 *
 */
public interface IAppTicketService {
	
	
	/**
	 * 根据公司+发票信息，获取图片组信息
	 * @param pk_corp
	 * @param pk_ticket_h
	 * @return
	 * @throws DZFWarpException
	 */
	public List<ImageGroupVO> qryGroupFromPt(String pk_corp, String pk_ticket_h) throws DZFWarpException;

}

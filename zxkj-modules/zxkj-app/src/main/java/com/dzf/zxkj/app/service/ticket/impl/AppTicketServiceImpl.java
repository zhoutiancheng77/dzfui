package com.dzf.zxkj.app.service.ticket.impl;

import java.util.List;

import com.dzf.zxkj.app.service.ticket.IAppTicketService;
import com.dzf.zxkj.app.utils.AppCheckValidUtils;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.image.ImageGroupVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("apppthand")
public class AppTicketServiceImpl implements IAppTicketService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Override
	public List<ImageGroupVO> qryGroupFromPt(String pk_corp, String pk_ticket_h) throws DZFWarpException {
		
		
		if(AppCheckValidUtils.isEmptyCorp(pk_corp)){
			throw new BusinessException("公司不能为空!");
		}
		if(StringUtil.isEmpty(pk_ticket_h)){
			throw new BusinessException("发票信息不能为空!");
		}

		String qrysql = "select * from ynt_image_group where pk_corp =? and pk_ticket_h = ? and nvl(dr,0)=0";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_ticket_h);
		List<ImageGroupVO> gplist = (List<ImageGroupVO>) singleObjectBO.executeQuery(qrysql, sp,
				new BeanListProcessor(ImageGroupVO.class));

		return gplist;
	}

}

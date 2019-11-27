package com.dzf.zxkj.platform.service.pzgl.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pzgl.TzpzHVO;
import com.dzf.zxkj.platform.service.pzgl.IPzqzService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 凭证签字业务处理
 * @author zhangj
 *
 */
@Service("gl_pzqzserv")
public class PzqzServiceImpl implements IPzqzService {

	@Autowired
	private SingleObjectBO singleObjectBO = null;
	
	@Override
	public void saveSignPz(TzpzHVO hvo, String cashid, DZFDate signdate) throws DZFWarpException {
		//批量操作
		if(StringUtil.isEmpty(cashid)){
			throw new BusinessException("签字人不能为空!");
		}
		
		if(hvo == null){
			throw new BusinessException("数据为空!");
		}
		
		hvo.setVcashid(cashid);
		hvo.setDcashdate(signdate);
		hvo.setBsign(DZFBoolean.TRUE);
		singleObjectBO.update(hvo, new String[]{"vcashid","dcashdate","bsign"});
		
	}

	@Override
	public void saveCancelSignPz(TzpzHVO hvo, String cashid) throws DZFWarpException {
		//当前已经审核，已经记账的凭证，不能取消签字
		if(hvo == null){
			throw new BusinessException("数据为空!");
		}
		if(hvo.getBsign()!=null && hvo.getBsign().booleanValue() ){
			if(StringUtil.isEmpty(hvo.getVcashid()) &&
					cashid.equals(hvo.getVcashid())){
				throw new BusinessException("当前登录人非签字人，取消签字失败!");
			}
			hvo.setVcashid(null);
			hvo.setDcashdate(null);
			hvo.setBsign(DZFBoolean.FALSE);
			singleObjectBO.update(hvo, new String[]{"vcashid","dcashdate","bsign"});
		}else{
			throw new BusinessException("凭证尚未签字，不能取消签字！");
		}
		
		
	}

}

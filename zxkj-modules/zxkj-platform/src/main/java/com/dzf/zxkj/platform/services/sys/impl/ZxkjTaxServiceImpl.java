package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.sys.IZxkjTaxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("zxkj_taxserv")
public class ZxkjTaxServiceImpl implements IZxkjTaxService {

	@Autowired
	private SingleObjectBO singleObjectBO;

	@Autowired
	private ICorpService corpService;

	@Override
	public UserVO[] queryUser(String pk_corp) throws DZFWarpException {
		CorpVO cpvo = corpService.queryByPk(pk_corp);
		String usersql = "select * from sm_user where pk_corp =? and nvl(dr,0)=0 and nvl(locked_tag,'N') = 'N' ";// 过滤锁定用户
		SQLParameter userparamater = new SQLParameter();
		userparamater.addParam(cpvo.getFathercorp());
		List<UserVO> resuservo = (List<UserVO>) singleObjectBO.executeQuery(usersql, userparamater,
				new BeanListProcessor(UserVO.class));

		return resuservo.toArray(new UserVO[0]);
	}

}

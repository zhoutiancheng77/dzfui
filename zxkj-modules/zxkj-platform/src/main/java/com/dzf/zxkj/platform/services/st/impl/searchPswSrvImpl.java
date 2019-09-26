package com.dzf.zxkj.platform.services.st.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.services.st.ISearchPswSrv;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("searchPsw")
public class searchPswSrvImpl implements ISearchPswSrv {

	private SingleObjectBO singleObjectBO = null;
	
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public UserVO getPhoByUcode(String ucode) throws DZFWarpException {
		
		if(ucode == null || "".equals(ucode)){
			throw new BusinessException("发送验证码失败！");
		}
		String sql = "select * from sm_user where nvl(dr,0)=0 and user_code = ?";
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(ucode);
		List<UserVO> uVo = (List<UserVO>) singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(UserVO.class));
		return uVo.get(0);
	}

	@Override
	public UserVO savePsw(UserVO uvo) throws DZFWarpException{
		
		String sql = "update sm_user set user_password = ? where nvl(dr,0)=0 and user_code = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(uvo.getUser_password());
		sp.addParam(uvo.getUser_code());
		int flag = singleObjectBO.executeUpdate(sql, sp);
		if(flag > 0){
			return uvo;
		}else{
			return new UserVO();
		}
	}

	/**
	 * param:UserVO
	 * 校验用户编码是否存在
	*/
	public UserVO UCodeIsExist(String user_code) throws DZFWarpException{
		
		String sql = "select * from sm_user where nvl(dr,0)=0 and user_code = ?";
		SQLParameter sp = new SQLParameter();
		sp.addParam(user_code);
		
		List<UserVO> uVo = (List<UserVO>) singleObjectBO.executeQuery(sql,sp,new BeanListProcessor(UserVO.class));
		
		return uVo.get(0);
	}
}

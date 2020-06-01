package com.dzf.zxkj.app.service.user;


import com.dzf.zxkj.app.model.app.corp.TempCorpVO;
import com.dzf.zxkj.app.model.app.user.AppUserVO;
import com.dzf.zxkj.app.model.app.user.TempUserVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

public interface IAppUserActService {

	public String saveTempCorp(TempCorpVO corpVO) throws DZFWarpException;
	public String saveTempUser(TempUserVO userVO) throws DZFWarpException;
	public int approveUser(String userID,String pk_corp,String pk_temp_corp,String bdata,String baccount,String bbillapply)throws DZFWarpException;
	public int cancelApproveUser(UserBeanVO userBean)throws DZFWarpException;
	public int saveSvOrg(UserBeanVO userBean)throws DZFWarpException;
	public String saveUser(AppUserVO userVO)throws DZFWarpException;
}

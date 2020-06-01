package com.dzf.zxkj.app.service.login;


import com.dzf.zxkj.app.model.resp.bean.LoginResponseBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.sys.CorpVO;

public interface IAppDemoCorpService {

	/**
	 * 发送demo公司
	 * 
	 * @param bean
	 * @param user_code
	 * @param user_name
	 * @param userid
	 * @param vdevicemsg
	 * @param corpvo
	 * @throws DZFWarpException
	 */
	public void sendDemoCorp(LoginResponseBeanVO bean, String user_code, String user_name, String userid,
							 String vdevicemsg, CorpVO[] corpvo, String sourcesys, String pk_sovrg) throws DZFWarpException;

}

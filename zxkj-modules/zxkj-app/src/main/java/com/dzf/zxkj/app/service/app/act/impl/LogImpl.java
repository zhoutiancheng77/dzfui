package com.dzf.zxkj.app.service.app.act.impl;

import com.dzf.zxkj.app.model.app.LogVO;
import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.app.service.app.act.ILog;
import com.dzf.zxkj.app.utils.CommonServ;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.base.framework.DataSourceFactory;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service("applog")
public class LogImpl implements ILog {


	public LogImpl() {
	}

	@Override
	public void savelog(UserBeanVO appBean) throws DZFWarpException {
		String sys = null;
		if(!StringUtil.isEmpty(appBean.getSystype())){
			switch (appBean.getSystype()) {
			case "0":
				sys = "android";
				break;
			case "1":
				sys = "ios";
				break;
			default:
				break;
			}
		}
		LogVO vo = new LogVO();
		vo.setAccout(appBean.getAccount());
		vo.setAccout_id(appBean.getAccount_id());// 用户信息
		vo.setOpttime(new DZFDateTime());
		vo.setOptcontent(appBean.getOptype());// 暂时不用
		vo.setMsg(appBean.getJson());
		vo.setPk_corp(appBean.getPk_corp());
		vo.setPk_temp_corp(appBean.getPk_tempcorp());
		vo.setVersionno(String.valueOf(appBean.getVersionno()));//版本号
		vo.setOptsys(sys);
		vo.setOptnumber(appBean.getOperate());
		try {
			String pk_corp = StringUtil.isEmptyWithTrim(appBean.getPk_corp()) ? IGlobalConstants.DefaultGroup
					: appBean.getPk_corp();
			SingleObjectBO sbo = new SingleObjectBO(DataSourceFactory.getDataSource(null, pk_corp));
			CommonServ.initUser(appBean);
			sbo.saveObject(pk_corp, vo);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			throw new WiseRunException(e);
		}

	}

}

package com.dzf.zxkj.app.service.app.act;

import com.dzf.zxkj.app.model.resp.bean.UserBeanVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

public interface ILog {
	public void savelog(UserBeanVO ubvo) throws DZFWarpException;
}

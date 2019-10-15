/*
 * Created on 2005-6-2
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.dzf.zxkj.platform.service.common;

import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * @author liujian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface IInSqlBatchCallBack
{
	public Object doWithInSql(String inSql) throws DZFWarpException;
}

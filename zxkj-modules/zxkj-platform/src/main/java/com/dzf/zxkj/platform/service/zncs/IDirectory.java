package com.dzf.zxkj.platform.service.zncs;

import com.dzf.zxkj.base.exception.DZFWarpException;

/**
 * 目录相关
 * @author mfz
 *
 */
public interface IDirectory {
	/**
	 * 创建目录	
	 * @param dirName 目录名称
	 * @param pk_parent 上级分类
	 * @param pk_corp 公司
	 * @param period 期间
	 * @throws DZFWarpException
	 */
	public void saveNewDirectory(String dirName, String pk_parent, String pk_corp, String period, String pk_user)throws DZFWarpException;
	
	/**
	 * 删除目录
	 */
	public void deleteDirectory(String primaryKey, String pk_parent, String pk_corp, String period)throws DZFWarpException;
}

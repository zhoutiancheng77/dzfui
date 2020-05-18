package com.dzf.zxkj.backup.service;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.secret.SecretKeyVo;

import java.util.List;
import java.util.Map;

public interface ISecretKeyService {

	
	/**
	 * 查询秘钥信息（公司+来源+版本号）
	 * @param pk_corp
	 * @param versionno
	 * @param versiondate 时间戳类型
	 * @return
	 * @throws DZFWarpException
	 */
	public List<SecretKeyVo> querySecretKeyFromNo(String pk_corp, Integer isourcesys, String versionno) throws DZFWarpException;


	
	/**
	 * 查询秘钥信息 (公司+来源+版本日期) 来确定唯一
	 * @param pk_corp
	 * @param versionno
	 * @param versiondate 时间戳类型
	 * @return
	 * @throws DZFWarpException
	 */
	public List<SecretKeyVo>  querySecretKeyFromDate(String pk_corp, Integer isourcesys, String versionbegdate, String versino) throws DZFWarpException;

	
	/**
	 * 获取文件内容（RSA）
	 * @param filename
	 * @return
	 * @throws DZFWarpException
	 */
	public Map<String, String> getRsaCodeValue(String filename) throws DZFWarpException;
	
}

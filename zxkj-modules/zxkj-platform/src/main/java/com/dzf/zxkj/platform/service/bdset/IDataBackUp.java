package com.dzf.zxkj.platform.service.bdset;

import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.BackupVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.io.File;
import java.io.InputStream;
import java.util.List;

/**
 * 数据备份
 * @author zhangj
 *
 */
public interface IDataBackUp {
	
	
	/**
	 * 数据查询
	 * @param pk_corp
	 * @return
	 * @throws DZFWarpException
	 */
	public List<BackupVO> query(String pk_corp) throws DZFWarpException;

	/**
	 * 公司的备份数据
	 * @param pk_corp
	 * @throws BusinessException
	 */
	public String updatedataBackUp(CorpVO corpvo) throws DZFWarpException;
	
	/**
	 * 公司数据更新
	 * @param upvo
	 * @return
	 * @throws DZFWarpException
	 */
	public void updateBackVo(BackupVO upvo) throws DZFWarpException;
	
	
	/**
	 * 公司数据还原
	 * @param data
	 * @throws BusinessException
	 */
	public void updatedateReturn(BackupVO upvo) throws DZFWarpException;
	

	/**
	 * 根据主键查询
	 * @param pk_backup
	 * @return
	 * @throws DZFWarpException
	 */
	public BackupVO queryByID(String pk_backup) throws DZFWarpException;
	
	
	public BackupVO queryNewByCorp(String pk_corp) throws DZFWarpException;
	
	
	public List<CorpVO> queryCorpBackDate(List<CorpVO> cplist) throws DZFWarpException;
	
	public void delete(BackupVO vo) throws DZFWarpException;

	
	public void saveUpFile(InputStream is, String fileName, CorpVO corp) throws DZFWarpException;

	
}

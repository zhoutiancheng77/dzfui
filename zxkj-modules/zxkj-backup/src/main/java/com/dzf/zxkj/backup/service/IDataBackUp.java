package com.dzf.zxkj.backup.service;


import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.platform.model.bdset.BackupVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;

import java.io.InputStream;
import java.util.List;

/**
 * 数据备份
 *
 * @author zhangj
 */
public interface IDataBackUp {


    /**
     * 数据查询
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     */
    List<BackupVO> query(String pk_corp) throws DZFWarpException;

    /**
     * 公司的备份数据
     *
     * @param corpvo
     * @throws DZFWarpException
     */
    String updatedataBackUp(CorpVO corpvo) throws DZFWarpException;

    /**
     * 公司数据更新
     *
     * @param upvo
     * @return
     * @throws DZFWarpException
     */
    void updateBackVo(BackupVO upvo) throws DZFWarpException;


    /**
     * 公司数据还原
     *
     * @param upvo
     * @throws DZFWarpException
     */
    void updatedateReturn(BackupVO upvo) throws DZFWarpException;


    /**
     * 根据主键查询
     *
     * @param pk_backup
     * @return
     * @throws DZFWarpException
     */
    BackupVO queryByID(String pk_backup) throws DZFWarpException;


    BackupVO queryNewByCorp(String pk_corp) throws DZFWarpException;


    List<CorpVO> queryCorpBackDate(List<CorpVO> cplist) throws DZFWarpException;

    void delete(BackupVO vo) throws DZFWarpException;


    void saveUpFile(InputStream is, String fileName, CorpVO corp) throws DZFWarpException;


}

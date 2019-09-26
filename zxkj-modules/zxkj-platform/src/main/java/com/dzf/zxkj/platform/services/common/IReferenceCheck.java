package com.dzf.zxkj.platform.services.common;


import com.dzf.zxkj.base.model.SuperVO;
import com.dzf.zxkj.base.exception.DZFWarpException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * 检查基本档案是否被应用
 */
public interface IReferenceCheck {

    /**
     * 在指定的基本档案主键列表中查询被公司引用的基本档案主键集合.
     *
     * @param tableName
     * @param basePks
     * @param pk_corp
     * @return
     */
    Set<String> getBasePkReferencedInCorp(String tableName,
                                          List<String> basePks, String pk_corp) throws DZFWarpException;

    /**
     * 检查表tableName中,指定的多个主键keys的引用情况.
     * 返回一个HashMap,Map的key为keys中的各个元素，value为一个UFBoolean对象。UFBoolean为True
     * 表示对应的key被引用了，为Flase则表示没被引用。
     *
     * @param tableName 被查询引用的表
     * @param keys      被查询引用的表的主键值数组
     * @return
     * @throws DZFWarpException
     */
    @SuppressWarnings("unchecked")
    HashMap getIsReferencedByKeys(String tableName, String[] keys)
            throws DZFWarpException;

    @SuppressWarnings("unchecked")
    HashMap getIsReferencedByKeysWhenModify(String tableName,
                                            String[] keys) throws DZFWarpException;

    /**
     * 检查表tableName中,指定的多个主键keys的引用情况. 返回被引用的主键数组,如果所有keys均未被引用,则返回null.
     *
     * @param tableName
     * @param keys
     * @return
     * @throws DZFWarpException
     */
    String[] getReferencedKeys(String tableName, String[] keys)
            throws DZFWarpException;

    /**
     * 检查表tableName中,指定的多个主键keys在档案修改时的的引用情况. 返回被引用的主键数组,如果所有keys均未被引用,则返回null.
     *
     * @param tableName
     * @param keys
     * @return
     * @throws DZFWarpException
     */
    String[] getReferencedKeysWhenModify(String tableName, String[] keys)
            throws DZFWarpException;

    /**
     * 指定管理档案表中的基本档案主键字段在指定公司是否被引用.
     *
     * @param tableName 管理档案表名
     * @param basePks   基本档案主键列表
     * @param pk_corp   公司
     * @return
     * @throws DZFWarpException
     */
    boolean isBasePkReferencedInCorp(String tableName,
                                     List<String> basePks, String pk_corp) throws DZFWarpException;

    /**
     * 指定管理档案表中的基本档案主键字段在指定公司是否被引用.
     *
     * @param tableName 管理档案表名
     * @param basePk    基本档案主键
     * @param pk_corp   公司
     * @return
     * @throws DZFWarpException
     */
    boolean isBasePkReferencedInCorp(String tableName, String basePk,
                                     String pk_corp) throws DZFWarpException;

    /**
     * 指定管理档案表中的基本档案主键字段在指定公司是否被引用.
     *
     * @param tableName          管理档案表名
     * @param basePk             基本档案主键
     * @param pk_corp            公司
     * @param excludedTableNames 不需要进行校验的表名
     * @return
     * @throws DZFWarpException
     */
    boolean isBasePkReferencedInCorp(String tableName, String basePk,
                                     String pk_corp, String[] excludedTableNames)
            throws DZFWarpException;

    /**
     * 查询tableName中主键字段的值为keys的记录是否被引用了 任何一个记录被引用将返回true 没有一个记录被引用将返回false
     *
     * @param tableName
     * @param keys
     * @return
     * @throws DZFWarpException
     */
    @SuppressWarnings("unchecked")
    boolean isReferenced(final String tableName, ArrayList keys)
            throws DZFWarpException;

    /**
     * 查询tableName中主键字段的值为key的记录是否被引用了
     *
     * @param tableName
     * @param key
     * @return ture 如果被引用,否则为false
     * @throws DZFWarpException
     */
    boolean isReferenced(String tableName, String key)
            throws DZFWarpException;

    /**
     * 检查tableNaem中主键字段为key的纪录是否被除excludedTableNames之外的表引用。
     * 如果被引用返回true,否则返回false。
     *
     * @param tableName
     * @param key
     * @param excludedTableNames
     * @return
     */
    boolean isReferenced(String tableName, String key,
                         String[] excludedTableNames) throws DZFWarpException;

    @SuppressWarnings("unchecked")
    boolean isReferencedWhenModify(String tableName, ArrayList keys)
            throws DZFWarpException;

    boolean isReferencedWhenModify(String tableName, String key)
            throws DZFWarpException;

    boolean isReferencedWhenModify(String tableName, String key,
                                   String[] excludedTableNames) throws DZFWarpException;

    //校验数据的有效性
    void isDataEffective(SuperVO vo) throws DZFWarpException;


    /**
     * 返回引用说明信息
     *
     * @param tableName
     * @param key
     * @throws DZFWarpException
     * @author gejw
     * @time 上午10:31:19
     */
    void isReferencedRefmsg(String tableName, String key)
            throws DZFWarpException;
}

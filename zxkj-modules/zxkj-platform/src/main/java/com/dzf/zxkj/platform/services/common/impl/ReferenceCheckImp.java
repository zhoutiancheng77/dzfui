/*
 * Created on 2005-10-25
 *
 */
package com.dzf.zxkj.platform.services.common.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.services.common.IReferenceCheck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * @author liujian 引用检查的服务实现类. 调用NewReferenceManagerDMO.其实可以让后者直接实现
 *         IReferenceCheck.
 */
@Service("refchecksrv")
public class ReferenceCheckImp implements IReferenceCheck {

	private SingleObjectBO singleObjectBO = null;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}
	
	public Set<String> getBasePkReferencedInCorp(String tableName,
												 List<String> basePks, String pk_corp) throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).getReferencedBasePksInCorp(
				tableName, basePks, pk_corp);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.itf.uap.bd.refcheck.IReferenceCheck#getIsReferencedByKeys(java.lang.String,
	 *      java.lang.String[])
	 */
	@SuppressWarnings("unchecked")
	public HashMap getIsReferencedByKeys(String tableName, String[] keys)
			throws DZFWarpException {
		if (tableName == null)
			throw new IllegalArgumentException("talbeName cann't be null");
		if (keys == null || keys.length == 0)
			return new HashMap();
		HashMap result = new HashMap();
		for (int i = 0; i < keys.length; i++) {
			String key = keys[i];
			boolean referenceFlag = isReferenced(tableName, key);
			result.put(key, DZFBoolean.valueOf(referenceFlag));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public HashMap getIsReferencedByKeysWhenModify(String tableName,
			String[] keys) throws DZFWarpException {
		if (tableName == null)
			throw new IllegalArgumentException("talbeName cann't be null");
		if (keys == null || keys.length == 0)
			return new HashMap();
		HashMap result = new HashMap();
		int len=keys.length;
		String key ;
		boolean referenceFlag;
		for (int i = 0; i < len; i++) {
			 key = keys[i];
			 referenceFlag = isReferencedWhenModify(tableName, key);
			result.put(key, DZFBoolean.valueOf(referenceFlag));
		}
		return result;
	}

	public String[] getReferencedKeys(String tableName, String[] keys)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).getReferencedKeys(tableName, keys,
				false);
	}

	public String[] getReferencedKeysWhenModify(String tableName, String[] keys)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).getReferencedKeys(tableName, keys,
				true);
	}

	public boolean isBasePkReferencedInCorp(String tableName,
			List<String> basePks, String pk_corp) throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isBasePksReferencedInCorp(
				tableName, pk_corp, basePks, false);
	}

	public boolean isBasePkReferencedInCorp(String tableName, String basePk,
			String pk_corp) throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isBasePkReferencedInCorp(tableName,
				pk_corp, basePk, false);
	}

	public boolean isBasePkReferencedInCorp(String tableName, String basePk,
			String pk_corp, String[] excludedTableNames)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isBasePkReferencedInCorp(tableName,
				pk_corp, basePk, excludedTableNames, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.itf.uap.bd.refcheck.IReferenceCheck#isReferenced(java.lang.String,
	 *      java.util.ArrayList)
	 */
	@SuppressWarnings("unchecked")
	public boolean isReferenced(String tableName, ArrayList keys)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO())
				.isReferenced(tableName, keys, false);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see nc.itf.uap.bd.refcheck.IReferenceCheck#isReferenced(java.lang.String,
	 *      java.lang.String)
	 */
	public boolean isReferenced(String tableName, String key)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isReferenced(tableName, key, false);
	}

	public boolean isReferenced(String tableName, String key,
			String[] excludedTableNames) throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isReferenced(tableName, key,
				excludedTableNames, false);
	}

	@SuppressWarnings("unchecked")
	public boolean isReferencedWhenModify(String tableName, ArrayList keys)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isReferenced(tableName, keys, true);
	}

	public boolean isReferencedWhenModify(String tableName, String key)
			throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isReferenced(tableName, key, true);
	}

	public boolean isReferencedWhenModify(String tableName, String key,
			String[] excludedTableNames) throws DZFWarpException {
		return new NewReferenceManagerDMO(getSingleObjectBO()).isReferenced(tableName, key,
				excludedTableNames, true);
	}

	@Override
	public void isDataEffective(SuperVO vo) throws DZFWarpException {
		if(vo == null)
			return;
		if(vo.getUpdatets() == null)
			return;
		SuperVO v1 = singleObjectBO.queryByPrimaryKey(vo.getClass(), vo.getPrimaryKey());
		if(v1 == null || v1.getUpdatets() == null)
			return;
		if(!v1.getUpdatets().equals(vo.getUpdatets())){
			throw new BusinessException("当前数据已被修改，请刷新后重新操作！");
		}
	}

    @Override
    public void isReferencedRefmsg(String tableName, String key) throws DZFWarpException {
        new ReferenceManagerDMO(getSingleObjectBO()).isReferencedRefmsg(tableName, key, false);
    }
}
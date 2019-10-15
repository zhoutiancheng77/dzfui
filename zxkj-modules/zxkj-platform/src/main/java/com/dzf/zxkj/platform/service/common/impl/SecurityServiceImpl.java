package com.dzf.zxkj.platform.service.common.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.service.common.ISecurityService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("ic_securityserv")
public class SecurityServiceImpl implements ISecurityService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private IUserService userService;

	@Override
	public void checkSecurityForSave(Class className, String primaryKey, String pk_corp, String logincorp,
			String cuserid) throws DZFWarpException {

		checkSecurity(className, primaryKey, pk_corp, logincorp, cuserid);

	}

	@Override
	public void checkSecurityForDelete(Class className, String primaryKey, String pk_corp, String logincorp,
			String cuserid) throws DZFWarpException {
		checkSecurity(className, primaryKey, pk_corp, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForQuery(Class className, String pk_corp, String logincorp, String cuserid)
			throws DZFWarpException {
		checkSecurity(className, null, pk_corp, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForOther(Class className, String primaryKey, String pk_corp, String logincorp,
			String cuserid) throws DZFWarpException {
		checkSecurity(className, primaryKey, pk_corp, logincorp, cuserid);
	}

	private void checkSecurity(Class className, String primaryKey, String pk_corp, String logincorp, String cuserid) {

		if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(logincorp) || !pk_corp.equals(logincorp)) {
			throw new BusinessException("出现数据无权问题，无权操作！");
		}

		if (!StringUtil.isEmpty(cuserid)) {
			Set<String> powerCorpSet = userService.querypowercorpSet(cuserid);
			if (!powerCorpSet.contains(logincorp)) {
				throw new BusinessException("出现数据无权问题，无权操作！");
			}
		}

		if (!StringUtil.isEmpty(primaryKey) && className != null) {
			SuperVO svo1 = null;
			try {
				svo1 = (SuperVO) className.newInstance();
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			}
			String sql = " select " + svo1.getPKFieldName() + ",pk_corp from " + svo1.getTableName()
					+ " where nvl(dr,0) = 0 and  " + svo1.getPKFieldName() + " = ?";
			SQLParameter sp = new SQLParameter();
			sp.addParam(primaryKey);
			List<SuperVO> list = (List<SuperVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(className));
			if (list == null || list.size() == 0) {
				throw new BusinessException("数据被删除，请刷新后操作！");
			} else {
				if (!logincorp.equals(list.get(0).getAttributeValue("pk_corp"))) {
					throw new BusinessException("出现数据无权问题，无权操作！");
				}
			}
		}
	}

	public boolean isExists(String pk_corp, SuperVO supervo) throws DAOException {

		if (supervo == null || StringUtil.isEmpty(supervo.getPrimaryKey()))
			return false;

		SQLParameter sp = new SQLParameter();
		sp.addParam(supervo.getPrimaryKey());
		String sql = " select " + supervo.getPKFieldName() + " from " + supervo.getTableName()
				+ " where nvl(dr,0) = 0 and  " + supervo.getPKFieldName() + " =?";
		return singleObjectBO.isExists(pk_corp, sql, sp);
	}

	@Override
	public void checkSecurityForSave(Class className, String primaryKey, String pk_corp, String logincorp)
			throws DZFWarpException {
		checkSecurityForSave(className, primaryKey, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForDelete(Class className, String primaryKey, String pk_corp, String logincorp)
			throws DZFWarpException {
		checkSecurityForDelete(className, primaryKey, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForQuery(Class className, String pk_corp, String logincorp) throws DZFWarpException {
		checkSecurityForQuery(className, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForOther(Class className, String primaryKey, String pk_corp, String logincorp)
			throws DZFWarpException {
		checkSecurityForOther(className, primaryKey, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForSave(String pk_corp, String logincorp) throws DZFWarpException {
		checkSecurityForSave(null, null, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForDelete(String pk_corp, String logincorp) throws DZFWarpException {
		checkSecurityForDelete(null, null, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForQuery(String pk_corp, String logincorp) throws DZFWarpException {
		checkSecurityForQuery(null, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForOther(String pk_corp, String logincorp) throws DZFWarpException {
		checkSecurityForOther(null, null, pk_corp, logincorp, null);
	}

	@Override
	public void checkSecurityForSave(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurityForSave(null, null, pk_corp, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForDelete(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurityForDelete(null, null, pk_corp, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForQuery(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurityForQuery(null, null, logincorp, cuserid);
	}

	@Override
	public void checkSecurityForOther(String pk_corp, String logincorp, String cuserid) throws DZFWarpException {
		checkSecurityForOther(null, null, pk_corp, logincorp, cuserid);
	}
}

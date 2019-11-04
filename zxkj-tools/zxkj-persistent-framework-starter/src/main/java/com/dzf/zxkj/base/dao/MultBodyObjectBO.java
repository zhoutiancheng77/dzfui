package com.dzf.zxkj.base.dao;

import com.dzf.zxkj.base.exception.DAOException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.ColumnProcessor;
import com.dzf.zxkj.common.model.IExAggVO;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.IDGenerate;
import com.dzf.zxkj.common.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dzf
 * 1、多子表保存
 * 2、分页查询
 */
@SuppressWarnings("all")
public class MultBodyObjectBO {

	private DataSource dataSource;

	public DataSource getDataSource() {
		return dataSource;
	}

	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	private SingleObjectBO singleObjectBO = null;
	
	

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	public MultBodyObjectBO() {

	}

	public MultBodyObjectBO(DataSource d) {
		super();
		this.dataSource = d;
	}

	/**
	 * 多子表保存
	 * 
	 * @param corp
	 * @param svo
	 * @return
	 * @throws BusinessException
	 */
	public SuperVO saveMultBObject(String corp, SuperVO svo) throws DZFWarpException {
		try {
			String pkid = svo.getPrimaryKey();
			if (StringUtil.isEmpty(pkid) == false) {
				return updateMultBVO(corp, svo);
			} else {
				return insertMultBVO(corp, svo);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}
	
	public SuperVO saveObject(String corp, SuperVO svo) throws DZFWarpException {
		try {
			String pkid = svo.getPrimaryKey();
			if (StringUtil.isEmpty(pkid) == false) {
				return updateBVO(corp, svo);
			} else {
				return insertBVO(corp, svo);
			}
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}


	/**
	 * 多子表新增
	 * @param corp
	 * @param svo
	 * @return
	 */
	private SuperVO insertMultBVO(String corp, SuperVO svo) {
		int len = 0;
		try {
			String pkid = IDGenerate.getInstance().getNextID(corp);
			svo.setPrimaryKey(pkid);
			BaseDAO dao = new BaseDAO(dataSource);

			pkid = dao.insertVOWithPK(corp, svo);

			if (svo instanceof IExAggVO) {
				String[] tablecodes = ((IExAggVO) svo).getTableCodes();
				for (String tablecode : tablecodes) {
					SuperVO[] svos = (SuperVO[]) ((IExAggVO) svo).getTableVO(tablecode);
					len = svos == null ? 0 : svos.length;
					String[] pks = IDGenerate.getInstance().getNextIDS(corp, len);
					String rField = null;
					if (len > 0)
						rField = svos[0].getParentPKFieldName();
					for (int i = 0; i < len; i++) {
						svos[i].setPrimaryKey(pks[i]);
						svos[i].setAttributeValue(rField, pkid);
					}
					if (len > 0)
						dao.insertVOArray(corp, svos);
				}
			}
			return svo;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}
	
	private SuperVO insertBVO(String corp, SuperVO svo) {
		int len = 0;
		try {
			String pkid = IDGenerate.getInstance().getNextID(corp);
			svo.setPrimaryKey(pkid);
			BaseDAO dao = new BaseDAO(dataSource);

			pkid = dao.insertVOWithPK(corp, svo);

			SuperVO[] svos = (SuperVO[]) svo.getChildren();
			len = svos == null ? 0 : svos.length;
			String[] pks = IDGenerate.getInstance().getNextIDS(corp, len);
			String rField = null;
			if (len > 0)
				rField = svos[0].getParentPKFieldName();
			for (int i = 0; i < len; i++) {
				svos[i].setPrimaryKey(pks[i]);
				svos[i].setAttributeValue(rField, pkid);
			}
			if (len > 0)
				dao.insertVOArray(corp, svos);
			return svo;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}
	/**
	 * 多子表修改
	 * @param corp
	 * @param svo
	 * @return
	 */
	private SuperVO updateMultBVO(String corp, SuperVO svo) {
		int len = 0;
		try {
			BaseDAO dao = new BaseDAO(dataSource);
			dao.updateVO(svo);
			String pkid = svo.getPrimaryKey();
			if (svo instanceof IExAggVO) {
				String[] tablecodes = ((IExAggVO) svo).getTableCodes();
				for (String tablecode : tablecodes) {
					SuperVO[] svos = (SuperVO[]) ((IExAggVO) svo).getTableVO(tablecode);
					len = svos == null ? 0 : svos.length;
					String rField = null;
					if (len > 0) {
						List<SuperVO> invo = new ArrayList<SuperVO>();
						List<SuperVO> upvo = new ArrayList<SuperVO>();
						List<SuperVO> delvo = new ArrayList<SuperVO>();
						List<SuperVO> listvos = new ArrayList<SuperVO>();
						rField = svos[0].getParentPKFieldName();
						for (int i = 0; i < len; i++) {
							String bodypk = svos[i].getPrimaryKey();
							if (StringUtil.isEmpty(bodypk) == false) {
								if(svos[i].getStatus() == 1){//删除
									svos[i].setAttributeValue(rField, pkid);
									delvo.add(svos[i]);
								}else{
									svos[i].setAttributeValue(rField, pkid);
									upvo.add(svos[i]);
									listvos.add(svos[i]);
								}
								
							} else {
								String bodyid = IDGenerate.getInstance().getNextID(corp);
								svos[i].setPrimaryKey(bodyid);
								svos[i].setAttributeValue(rField, pkid);
								invo.add(svos[i]);
								listvos.add(svos[i]);
							}
						}
						if (invo.size() > 0)
							dao.insertVOList(corp, invo);
						if (upvo.size() > 0)
							dao.updateVOList(upvo);
						if (delvo.size() > 0)
							dao.deleteVOList(delvo);
						svos = listvos.toArray(new SuperVO[0]);
					}
					((IExAggVO) svo).setTableVO(tablecode, svos);
				}
			}
			return svo;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}
	
	private SuperVO updateBVO(String corp, SuperVO svo) {
		int len = 0;
		try {
			BaseDAO dao = new BaseDAO(dataSource);
			dao.updateVO(svo);
			String pkid = svo.getPrimaryKey();
			SuperVO[] svos = (SuperVO[])svo.getChildren();
			len = svos == null ? 0 : svos.length;
			String rField = null;
			if (len > 0) {
				List<SuperVO> invo = new ArrayList<SuperVO>();
				List<SuperVO> upvo = new ArrayList<SuperVO>();
				List<SuperVO> delvo = new ArrayList<SuperVO>();
				List<SuperVO> listvos = new ArrayList<SuperVO>();
				rField = svos[0].getParentPKFieldName();
				for (int i = 0; i < len; i++) {
					String bodypk = svos[i].getPrimaryKey();
					if (StringUtil.isEmpty(bodypk) == false) {
						if(svos[i].getStatus() == 1){//删除
							svos[i].setAttributeValue(rField, pkid);
							delvo.add(svos[i]);
						}else{
							svos[i].setAttributeValue(rField, pkid);
							upvo.add(svos[i]);
							listvos.add(svos[i]);
						}
						
					} else {
						String bodyid = IDGenerate.getInstance().getNextID(corp);
						svos[i].setPrimaryKey(bodyid);
						svos[i].setAttributeValue(rField, pkid);
						invo.add(svos[i]);
						listvos.add(svos[i]);
					}
				}
				if (invo.size() > 0)
					dao.insertVOList(corp, invo);
				if (upvo.size() > 0)
					dao.updateVOList(upvo);
				if (delvo.size() > 0)
					dao.deleteVOList(delvo);
				svos = listvos.toArray(new SuperVO[0]);
			}
			svo.setChildren(svos);
			return svo;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}
	
	/**
	 * 单独处理子表数据集
	 * @param corp：公司主键
	 * @param svos
	 * @return
	 */
	public SuperVO[] updateArrayVO(String corp, SuperVO[] svos) {
		int len = 0;
		try {
			BaseDAO dao = new BaseDAO(dataSource);
			len = svos == null ? 0 : svos.length;
			if (len > 0) {
				List<SuperVO> invo = new ArrayList<SuperVO>();
				List<SuperVO> upvo = new ArrayList<SuperVO>();
				List<SuperVO> delvo = new ArrayList<SuperVO>();
				List<SuperVO> listvos = new ArrayList<SuperVO>();
				for (int i = 0; i < len; i++) {
					String bodypk = svos[i].getPrimaryKey();
					if (StringUtil.isEmpty(bodypk) == false) {
						if(svos[i].getStatus() == 1){//删除
							delvo.add(svos[i]);
						}else{
							upvo.add(svos[i]);
							listvos.add(svos[i]);
						}
						
					} else {
						String bodyid = IDGenerate.getInstance().getNextID(corp);
						svos[i].setPrimaryKey(bodyid);
						invo.add(svos[i]);
						listvos.add(svos[i]);
					}
				}
				if (invo.size() > 0)
					dao.insertVOList(corp, invo);
				if (upvo.size() > 0)
					dao.updateVOList(upvo);
				if (delvo.size() > 0)
					dao.deleteVOList(delvo);
				svos = listvos.toArray(new SuperVO[0]);
			}
			return svos;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {

		}
	}
	
	/** 分页查询
	 * @param className
	 * @param sql 查询语句
	 * @param params 条件参数
	 * @param pageNo 页数
	 * @param pageSize 每页记录条数
	 * @param order 排序字段
	  */
	public List<?> queryDataPage(Class className, String sql, SQLParameter params, int pageNo, int pageSize, String order) throws DZFWarpException {
		StringBuffer qrysql = new StringBuffer();
		qrysql.append(" select * from ( SELECT ROWNUM AS ROWNO, tt.* FROM ( ");
		qrysql.append(sql);
		if (order != null) {
			qrysql.append(" order by " + order + " desc) tt WHERE ROWNUM<="
					+ pageNo * pageSize);
		} else {
			qrysql.append(" ) tt WHERE ROWNUM<=" + pageNo * pageSize + " ");
		}
		qrysql.append(" ) WHERE ROWNO> " + (pageNo - 1) * pageSize + " ");
		return (List<?>)singleObjectBO.executeQuery(qrysql.toString(), params, new BeanListProcessor(className));
	}
	/**
	 * 根据条件 获取总条数
	 * @param sql ： select count(*) from table
	 * @param params
	 * @return
	 * @throws DAOException
	 */
	public int getDataTotal(String sql,SQLParameter params) throws DZFWarpException{
		Object obj = singleObjectBO.executeQuery(sql, params, new ColumnProcessor());
		if(obj == null){
            return 0;
        }
        return Integer.parseInt(obj.toString());
	}
	
	public int queryDataTotal(Class className,String sql,SQLParameter params) throws DZFWarpException{
		List list = (List<?>)singleObjectBO.executeQuery(sql, params, new BeanListProcessor(className));
		if(list != null && list.size() > 0){
			return list.size();
		}
		return 0;
	}
}

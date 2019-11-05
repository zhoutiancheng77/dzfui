package com.dzf.zxkj.platform.service.common.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.base.utils.FieldMapping;
import com.dzf.zxkj.common.entity.ConditionVO;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.exception.WiseRunException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.common.IBgPubService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("i_pubservice")
@Slf4j
public class BgPubServiceImpl implements IBgPubService {

	private SingleObjectBO singleObjectBO;
	
	
	
	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	@Override
	public List<SuperVO> queryInfovo(Class className, String tableName, String condition,
                                     SQLParameter params, int pageNo, int pageSize, String order) throws DZFWarpException {
		List<SuperVO>  rs = (List<SuperVO>)singleObjectBO.execQueryWithPage(className, tableName, condition,
				params,pageNo,pageSize,order);
		if(rs == null || rs.size() == 0)
			return null;
		String pk_corp = rs.get(0).getAttributeValue("pk_corp").toString();
		return completinfo(rs,pk_corp);
	}

	
	@Override
	public<T> List<T> queryInfovo(Class className, CorpVO corpVo,
                                  UserVO uservo, String sort, String order) throws DZFWarpException {

		try {
			SuperVO svo = (SuperVO) className.newInstance();

			SQLParameter sp = new SQLParameter();

			StringBuffer sb = new StringBuffer();
			sb.append(" select * from ").append(svo.getTableName());
			if (corpVo != null) {
				sb.append(" where pk_corp=? and nvl(dr,0)=0");
				sp.addParam(corpVo.getPk_corp());
			}
			if (sort != null) {
				String sortb = FieldMapping.getFieldNameByAlias(svo, sort);
				order = " order by " + (sortb == null ? sort : sortb) + " "
						+ order;
				sb.append(order);
			}
			List<SuperVO> rs = (List<SuperVO>) singleObjectBO.executeQuery(
					sb.toString(), sp, new BeanListProcessor(className));
			if (rs == null || rs.size() == 0)
				return null;
			//String pk_corp = rs.get(0).getAttributeValue("pk_corp").toString();
			
			return completinfo(rs, corpVo.getPk_corp());
			
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}
	
	/**
	 * 传入查询条件查询
	 * */
	@Override
	public<T> List<T> queryWithCondtion(Class className, ConditionVO[] cd, String sort, String order)  throws DZFWarpException {
		List<SuperVO> rs;
		StringBuffer sb = new StringBuffer();
		SQLParameter sp = new SQLParameter();
		try {

			SuperVO svo = (SuperVO) className.newInstance();
			sb.append(" select * from " + svo.getTableName()+" where nvl(dr,0)=0 ");

			if(cd==null){
				if (sort != null) {
					String sortb = FieldMapping.getFieldNameByAlias(svo, sort);
					order = " order by " + (sortb == null ? sort : sortb) + " "
							+ order;
					sb.append(order);
				}
				rs = (List<SuperVO>)getSingleObjectBO().executeQuery(sb.toString(), sp, new BeanListProcessor(className));
			}else{
				for(ConditionVO pra: cd){
					if(pra==null||pra.getCdname()==null){
						continue;
					}
					if(pra.getCdsymbol()==null){
						sb.append(" and "+pra.getCdname()+" =?");
					}else{
						sb.append(" and"+pra.getCdsymbol());
					}
					sp.addParam(pra.getCdvalue());
				}
				
				if (sort != null) {
					String sortb = FieldMapping.getFieldNameByAlias(svo, sort);
					order = " order by " + (sortb == null ? sort : sortb) + " "
							+ order;
					sb.append(order);
				}
				rs = (List<SuperVO>) singleObjectBO.executeQuery(
						sb.toString(), sp, new BeanListProcessor(className));
				
			}
			if (rs == null || rs.size() == 0)
				return null;
			
			String pk_corp = (String)rs.get(0).getAttributeValue("pk_corp");
			return completinfo(rs, pk_corp);
			
		} catch (Exception e) {
			throw new WiseRunException(e);
		}
	}
	
	
	// 获取总页数
	public int getTotalRow(String sql) throws DZFWarpException  {
		return singleObjectBO.getTotalRow(sql);
	}

	@Override
	public void deleteInfovo(SuperVO bean)  throws DZFWarpException {
		SuperVO tvo = (SuperVO)bean.clone();
		singleObjectBO.deleteObject(tvo);
	

	}
	@Override
	public List<SuperVO> queryByPkcorp(Class className,String pk_corp) throws DZFWarpException{
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			List<SuperVO> listVo = (List<SuperVO>) singleObjectBO.retrieveByClause(className, "pk_corp=? and nvl(dr,0) = 0 ", sp);
			return completinfo(listVo,pk_corp);
	}
	
	
	@Override
	public SuperVO saveNew(SuperVO vo) throws DZFWarpException {
		SuperVO tvo =(SuperVO)vo.clone();
		checkBeforeSaveNew(tvo);
		
		return (SuperVO)singleObjectBO.saveObject((String)tvo.getAttributeValue("pk_corp"), tvo);
	};
	@Override
	public void update(SuperVO vo) throws DZFWarpException {
		SuperVO tvo =(SuperVO)vo.clone();
		checkBeforeUpdata(tvo);
		
		singleObjectBO.saveObject((String)tvo.getAttributeValue("pk_corp"), tvo);
	}
	@Override
	public void updateDzf(SuperVO vo) throws DZFWarpException {
		SuperVO tvo =(SuperVO)vo.clone();
		singleObjectBO.saveObject((String)tvo.getAttributeValue("pk_corp"), tvo);
	}
	
	
	/**
	 * 按字段更新
	 * **/
	@Override
	public void updateByColumn(SuperVO vo,String[] columns) throws DZFWarpException {
		if(columns==null||columns.length<=0){
			throw new BusinessException(" 更新字段不可为空");
		}
		SuperVO tvo =(SuperVO)vo.clone();
		checkBeforeUpdata(tvo);//保存前检查
		
		getSingleObjectBO().update(tvo, columns);// executeUpdate(updatesql, sp);
	}
	
	@Override
	public int getTotalRow(String tablename, String condition, SQLParameter sp)
			throws DZFWarpException {
		return singleObjectBO.getTotalRow(tablename, condition, sp);
	}

	public<T> List<T> completinfo(List<SuperVO>  rs,String pk_corp) throws DZFWarpException{
		return (List<T>)rs;
	}
	

	@SuppressWarnings("unchecked")
	public<T> Map<String,T> queryMap(Class className,String pk_corp) throws DZFWarpException{
		
		Map<String,T> rsmap = new HashMap<String,T>();
		try{
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		
		
		List<T> listVo = (List<T>) getSingleObjectBO().retrieveByClause(className, "pk_corp=? and nvl(dr,0) = 0 ", sp);

		if(listVo != null && listVo.size() > 0){
			for(T pvo : listVo){
				rsmap.put(((SuperVO)pvo).getPrimaryKey(), pvo);
			}
		}
		}catch(Exception e){
			throw new WiseRunException(e);
		}
		return rsmap;
	}
	
	//protected String errmsg ;
	
	/**
	 * 更新前检查
	 * */
	public DZFBoolean checkBeforeUpdata(SuperVO vo)throws  DZFWarpException{
		return DZFBoolean.TRUE;
	}
	
	/**
	 * 新增前检查
	 * */
	public DZFBoolean checkBeforeSaveNew(SuperVO vo)throws  DZFWarpException{
		return DZFBoolean.TRUE;
	}
	@Override
	public void deleteInfovoDzf(SuperVO bean) throws DZFWarpException {
		// TODO Auto-generated method stub
		
	}
	
}

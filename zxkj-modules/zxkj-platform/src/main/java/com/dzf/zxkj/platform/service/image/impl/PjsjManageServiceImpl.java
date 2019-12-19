package com.dzf.zxkj.platform.service.image.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanProcessor;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.lang.DZFDateTime;
import com.dzf.zxkj.common.model.SuperVO;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.pjgl.PjCheckBVO;
import com.dzf.zxkj.platform.model.pjgl.PjCheckHVO;
import com.dzf.zxkj.platform.model.pjgl.PjCollectionBVO;
import com.dzf.zxkj.platform.model.pjgl.PjCollectionHVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.image.IPjsjManageService;
import com.dzf.zxkj.platform.util.zncs.PjTypeEnum;
import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service("pjsjmanager_serv")
public class PjsjManageServiceImpl implements IPjsjManageService {
	private static final String FROM_SP = "1";//上传来自收票
	private static final String FROM_JP = "2";//上传来自检票
	private Logger log = Logger.getLogger(this.getClass());
	@Autowired
	private SingleObjectBO singleObjectBO;
	@Autowired
	private RedissonDistributedLock redissonDistributedLock;
	@Override
	public void updateCountByPjlx(String selcorp, 
			String qj, 
			String pjlxType, 
			String fromOpen, 
			String pk_qj,
			UserVO userVO,
			CorpVO corpVo,
			int calNum)
			throws DZFWarpException {
		
		if(isEmpty(new String[]{selcorp, qj, pjlxType})) 
			return;

		boolean lock = false;
		int pjlxInt = Integer.parseInt(pjlxType);
		
		if(StringUtil.isEmpty(fromOpen)){
			fromOpen = FROM_JP;
		}
		
		SuperVO hvo = null;
		SuperVO bvo = null;
		
		if(FROM_SP.equals(fromOpen)){
			hvo = new PjCollectionHVO();
			bvo = new PjCollectionBVO();
		}else if(FROM_JP.equals(fromOpen)){
			hvo = new PjCheckHVO();
			bvo = new PjCheckBVO();
		}else{
			return;
		}
		
		String key = selcorp 
				+ "_" + qj 
				+ "_" + pjlxType;
		try {
			lock = redissonDistributedLock.tryGetDistributedFairLock(hvo.getTableName()+key);
			if(lock){
				updateMergeCount(selcorp, qj, pjlxInt, fromOpen, userVO, corpVo, hvo, bvo, calNum);
			}
		} catch (Exception e) {
			log.error("错误",e);
			throw new BusinessException(e.getMessage());
		}finally {
			if(lock){
				redissonDistributedLock.releaseDistributedFairLock(hvo.getTableName()+key);
			}
		}
		
	}
	
	/**
	 * 收检票统计
	 * @param selcorp
	 * @param qj
	 * @param pjlxInt
	 * @param fromOpen
	 * @param userVO
	 * @param corpVo
	 * @param hvo
	 * @param bvo
	 * @throws DZFWarpException
	 */
	private void updateMergeCount(String selcorp, 
			String qj, 
			int pjlxInt, 
			String fromOpen,
			UserVO userVO,
			CorpVO corpVo,
			SuperVO hvo,
			SuperVO bvo,
			int calNum) throws DZFWarpException{
		
		SQLParameter sp = new SQLParameter();
		sp.addParam(selcorp);
		sp.addParam(qj);
		StringBuilder sf = new StringBuilder();
		sf.append(" select  * ");
		sf.append(" from ");
		sf.append(hvo.getTableName());
		sf.append(" where pk_customer = ? and period = ? and nvl(dr,0) = 0 ");
		
		List<SuperVO> hvos = (List<SuperVO>) singleObjectBO.executeQuery(sf.toString(),
				sp, new BeanListProcessor(hvo.getClass()));
		
		if(hvos == null || hvos.size() == 0){
			return;
		}
		
		hvo = hvos.get(0);
		
		sf.delete(0, sf.length());
		sf.append(" select * from ");
		sf.append(bvo.getTableName());
		sf.append(" where ");
		sf.append(hvo.getPKFieldName());
		sf.append(" = ? and billstyle = ? and nvl(dr,0) = 0 ");
		
		sp = new SQLParameter();
		sp.addParam(hvos.get(0).getPrimaryKey());
		sp.addParam(pjlxInt);
		
		SuperVO newBvo = (SuperVO) singleObjectBO.executeQuery(sf.toString(), sp, new BeanProcessor(bvo.getClass()));
		
		boolean isNew = false;
		if(newBvo == null){
			isNew = true;
			newBvo = bvo;
			newBvo.setAttributeValue("pk_corp", hvo.getAttributeValue("pk_corp"));
			newBvo.setAttributeValue("billstyle", pjlxInt);
			newBvo.setAttributeValue(newBvo.getParentPKFieldName(), hvo.getPrimaryKey());
		}
		
		int zs = calNum > 0 ? calNum : Math.abs(calNum);
		
		//更新子表
		Integer billNum = (Integer) newBvo.getAttributeValue("billnum");
		Integer upNum = (Integer) newBvo.getAttributeValue("upsucnum");
		Integer upTotalNum = (Integer) newBvo.getAttributeValue("uptotalnum");
		
		billNum = billNum == null ? 0 : billNum;
		upNum = upNum == null ? 0 : upNum;
		upTotalNum = upTotalNum == null ? 0 : upTotalNum;
		if(billNum.equals(upNum) && calNum > 0){
			upNum += calNum;
			billNum += calNum;
		}else if(calNum > 0){
			upNum += calNum;
		}
		
		upTotalNum += zs;
		
		newBvo.setAttributeValue("upsucnum", upNum);
		newBvo.setAttributeValue("billnum", billNum);
		newBvo.setAttributeValue("uptotalnum", upTotalNum);
		
		if(isNew){
			singleObjectBO.saveObject((String) hvo.getAttributeValue("pk_corp"), newBvo);
		}else{
			singleObjectBO.update(newBvo, new String[]{ "billnum", "upsucnum", "uptotalnum"});
		}
		
		//更新主表
		if(calNum > 0){
			Integer totalnum = (Integer) hvo.getAttributeValue("totalnum");
			totalnum = totalnum == null ? 0 : totalnum;
			
			totalnum += zs;
			hvo.setAttributeValue("totalnum", totalnum);
			
			singleObjectBO.update(hvo, new String[]{ "totalnum"});
		}
		
	}
	
	/**
	 * 暂时废弃不用
	 */
	private void insertBodyDataByPjlx(String selcorp, 
			String qj, 
			int pjlxInt, 
			String fromOpen,
			UserVO userVO,
			CorpVO corpVo,
			SuperVO hvo,
			SuperVO bvo) throws DZFWarpException{
		
		hvo.setAttributeValue("pk_customer", selcorp);
		hvo.setAttributeValue("coperatorid", userVO.getCuserid());
		hvo.setAttributeValue("doperatedate", new DZFDate());
		hvo.setAttributeValue("modifyid", userVO.getCuserid());
		hvo.setAttributeValue("modifydatetime", new DZFDateTime());
		hvo.setAttributeValue("period", qj);
		hvo.setAttributeValue("pk_corp", corpVo.getPk_corp());
		hvo.setAttributeValue("dr", 0);
		
		bvo.setAttributeValue("pk_corp", corpVo.getPk_corp());
		bvo.setAttributeValue("billstyle", pjlxInt);
		bvo.setAttributeValue("billnum", 1);
		bvo.setAttributeValue("upsucnum", 1);
		
		hvo.setChildren(new SuperVO[]{ bvo});
		
		singleObjectBO.saveObject(corpVo.getPk_corp(), hvo);
	}
	
	private boolean isEmpty(String[] params){
		boolean isEmp = false;
		for(String param : params){
			if(StringUtil.isEmpty(param)){
				isEmp = true;
				break;
			}
		}
		
		return isEmp;
	}

	@Override
	public List<PjCheckBVO> queryPjlxTypes(String pk_corp) throws DZFWarpException {
		
		if(StringUtil.isEmpty(pk_corp)){
			return null;
		}
		
		String sql = "select pk_bill_check_h from (select h.pk_bill_check_h from ynt_bill_check_h h Where h.pk_customer = ? and nvl(dr,0) = 0 order by h.period desc) t Where rownum = 1";
		SQLParameter sp = new SQLParameter();
		sp.addParam(pk_corp);
		String pk_check_h = (String) singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor() {
			@Override
			public Object handleResultSet(ResultSet rs) throws SQLException {
				String pk = null;
				if(rs.next()){
					pk = rs.getString("pk_bill_check_h");
				}
				
				return pk;
			}
		});
		
		List<PjCheckBVO> bvoList = null;
		if(!StringUtil.isEmpty(pk_check_h)){
			
			sql = "select * from ynt_bill_check_b where nvl(dr,0) = 0 and pk_bill_check_h = ? ";
			sp.clearParams();
			sp.addParam(pk_check_h);
			bvoList = (List<PjCheckBVO>) singleObjectBO.executeQuery(sql, 
					sp, new BeanListProcessor(PjCheckBVO.class));
			
		}
		
		if(bvoList == null || bvoList.size() == 0){
			PjCheckBVO bvo = null;
			bvoList = new ArrayList<PjCheckBVO>();
			for(PjTypeEnum e : PjTypeEnum.values()){
				bvo = new PjCheckBVO();
				bvo.setBillstyle(e.getValue());
				bvoList.add(bvo);
			}
		}
		
		if(bvoList != null && bvoList.size() > 0){
			for(PjCheckBVO bvo : bvoList){
				if(bvo.getBillstyle() != null){//在备注临时存票据类型名称
					bvo.setMemo(PjTypeEnum.getPjTypeEnumByValue(bvo.getBillstyle()).getName());
				}
			}
			Collections.sort(bvoList, new Comparator<PjCheckBVO>() {
				@Override
				public int compare(PjCheckBVO o1, PjCheckBVO o2) {
					int i1 = PjTypeEnum.getPjTypeEnumByValue(o1.getBillstyle()).getOrder();
					int i2 = PjTypeEnum.getPjTypeEnumByValue(o2.getBillstyle()).getOrder();
					return i1 - i2;
				}
			});
		}
		
		return bvoList;
	}

	
}

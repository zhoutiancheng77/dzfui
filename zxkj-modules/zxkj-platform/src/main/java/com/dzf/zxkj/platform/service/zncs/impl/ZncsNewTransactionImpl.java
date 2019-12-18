package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.cloud.redis.lock.RedissonDistributedLock;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.zncs.BillCategoryVO;
import com.dzf.zxkj.platform.service.zncs.IZncsNewTransService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Slf4j
@Service
public class ZncsNewTransactionImpl implements IZncsNewTransService {
	@Autowired
	SingleObjectBO singleObjectBO;
	@Autowired
	private RedissonDistributedLock redissonDistributedLock;
	
	private List<BillCategoryVO> getAddList(Map<String, BillCategoryVO> falseMap, Map<String, BillCategoryVO> trueMap)throws DZFWarpException{
		Set<String> setCategoryCode = new HashSet<String>();
		for (String key : trueMap.keySet())//遍历已制证树
		{
			BillCategoryVO vo = trueMap.get(key);
			if (!setCategoryCode.contains(vo.getCategorycode()))
			{
				setCategoryCode.add(vo.getCategorycode());
			}
		}

		Iterator<String> itor = falseMap.keySet().iterator();
		List<BillCategoryVO> addList=new ArrayList<BillCategoryVO>();
		while(itor.hasNext()){//遍历未制证树
			String fullname=itor.next();//未制证全名称
			BillCategoryVO falseVO=falseMap.get(fullname);//未制证VO
			BillCategoryVO trueVO=trueMap.get(fullname);//已制证VO
			if(falseVO.getFullcategoryname().equals(fullname) && trueVO==null){//如果没有这个，就增加
				BillCategoryVO newVO=(BillCategoryVO)falseVO.clone();//复制未制证VO
				newVO.setPk_category(null);
				boolean isFull = false;	//编号是否占满
				//没有此已制证节点，如果存在上级，通过未制证树找到它的上级，再通过未制证的上级全名称找到已制证的上级全名称
				if (falseVO.getCategorytype() != null && falseVO.getCategorytype() > 0 && StringUtil.isEmpty(falseVO.getPk_parentcategory()) == false)
				{
					BillCategoryVO trueParentVo = trueMap.get(falseMap.get(falseVO.getPk_parentcategory()).getFullcategoryname());
					//重新生成已制证的分类编码
					String trueParentCode = trueParentVo.getCategorycode();
					String categorycode = trueParentCode + "01";
					int i = 1;
					while (setCategoryCode.contains(categorycode) && i < 100)	//从末尾是01的编码里循环累加，找未使用的最小编码
					{
						i++;
						categorycode = trueParentCode + (i < 10 ? "0" : "") + i;
					}
					if (i <= 99)
					{
						if (i == 99)
						{
							newVO.setCategoryname("其他");
							
						}
						setCategoryCode.add(categorycode);
						newVO.setCategorycode(categorycode);
					}
					else
					{
						//全部占满
						isFull = true;
					}
				}
				if (!isFull)
				{
					addList.add(newVO);
					trueMap.put(newVO.getFullcategoryname(), newVO);
				}
			}
		}
		return addList;
	}
	@Override
	public Map<String, BillCategoryVO> newInsertCategoryVOs(Map<String, BillCategoryVO> falseMap,Map<String, BillCategoryVO> trueMap,String key) throws DZFWarpException {
		List<BillCategoryVO> addList=getAddList(falseMap, trueMap);
		if(addList.size()>0){
			String requestid=null;
			boolean lock=redissonDistributedLock.tryGetDistributedFairLock(key);
			try {
//				if(!redissonDistributedLock.tryGetDistributedFairLock("zncs_accounttree")){
//					return null;
//				}
				requestid = UUID.randomUUID().toString();
//				lock = LockUtil.getInstance().addLockKey("zncs_accounttree", key, requestid, 60);// 设置60秒

				long starttime = System.currentTimeMillis();
				while (!lock)
				{
					if (System.currentTimeMillis() - starttime > 10000)
					{
						throw new BusinessException("操作失败，请稍后再试");
					}
					Thread.sleep(100);
//					lock = LockUtil.getInstance().addLockKey("zncs_accounttree", key, requestid, 60);// 设置60秒
					lock = redissonDistributedLock.tryGetDistributedFairLock(key);// 设置60秒
				}
				//加锁成功重新查询已制证分类树
				trueMap=queryCategoryVOs_IsAccount(key.substring(0, 6), key.substring(6),"Y");
				
				addList=getAddList(falseMap, trueMap);
				
				for (int i = 0; i < addList.size(); i++) {
					BillCategoryVO newVO=addList.get(i);
					newVO.setIsaccount(DZFBoolean.TRUE);//变成已制证
					newVO.setPk_category(null);//清空主键
					BillCategoryVO falseVO =falseMap.get(newVO.getFullcategoryname());
					if(!StringUtil.isEmpty(falseVO.getPk_parentcategory())){//如果有上级，要设置上级主键
						BillCategoryVO falseparentvo = falseMap.get(falseVO.getPk_parentcategory());
//						
						BillCategoryVO parentVO=trueMap.get(falseparentvo.getFullcategoryname());//从已制证map找VO,肯定找得到
						newVO.setPk_parentcategory(parentVO.getPk_category());//设置已制证父主键
					}
					newVO=(BillCategoryVO)singleObjectBO.insertVO(falseVO.getPk_corp(), newVO);//保存
					updateFullName(trueMap, newVO);	//更新一下新增节点的全名称
					trueMap.put(newVO.getPk_category(), newVO);
					trueMap.put(newVO.getFullcategoryname(), newVO);//缓存到已制证map
				}
			} catch (Exception e) {
				log.error(e.getMessage(), e);
			} finally {
//				if(lock){
//					LockUtil.getInstance().unLock_Key("zncs_accounttree", key, requestid);
//				}
				redissonDistributedLock.releaseDistributedFairLock(key);
			}
		}
		return trueMap;
	}

	public Map<String, BillCategoryVO> queryCategoryVOs_IsAccount(String pk_corp,String period,String flag)throws DZFWarpException{
		StringBuffer sb=new StringBuffer();
		SQLParameter sp=new SQLParameter();
		sb.append("select * from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and period=? and nvl(isaccount,'N')=? ");
		sb.append(" order by categorylevel ");
		sp.addParam(pk_corp);
		sp.addParam(period);
		sp.addParam(flag);
		Map<String, BillCategoryVO> returnMap=new LinkedHashMap<String, BillCategoryVO>();
		List<BillCategoryVO> list = (List<BillCategoryVO>) singleObjectBO.executeQuery(sb.toString(), sp,new BeanListProcessor(BillCategoryVO.class));
		if(list!=null&&list.size()>0){
			for(int i=0;i<list.size();i++){
				returnMap.put(list.get(i).getPk_category(), list.get(i));
				updateFullName(returnMap, list.get(i));
				returnMap.put(list.get(i).getFullcategoryname(), list.get(i));
			}
		}
		return returnMap;
	}
	
	private void updateFullName(Map map, BillCategoryVO categoryvo)
	{
		StringBuffer sbuf = new StringBuffer();
		sbuf.append(categoryvo.getCategoryname());
		BillCategoryVO vo = categoryvo;
		while (!StringUtil.isEmpty(vo.getPk_parentcategory()))
		{
			vo = (BillCategoryVO)map.get(vo.getPk_parentcategory());
			if (vo != null)
			{
				sbuf.insert(0, "~");
				sbuf.insert(0, vo.getCategoryname());

			}
			else
			{
				break;
			}
		}
		categoryvo.setFullcategoryname(sbuf.toString());

	}
}

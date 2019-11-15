package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.zncs.BlackListVO;
import com.dzf.zxkj.platform.service.zncs.IBlackList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BlackListImpl implements IBlackList {
	@Autowired
	SingleObjectBO singleObjectBO ;
	@Override
	public List<BlackListVO> queryBlackListVOs(String pk_corp) throws DZFWarpException {
		String sql="select * from ynt_blacklist where nvl(dr,0)=0 and pk_corp=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<BlackListVO> list=(List<BlackListVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BlackListVO.class));
		return list;
	}

	@Override
	public List<BlackListVO> saveBlackListVO(String pk_corp, String blackListnames) throws DZFWarpException {
		List<BlackListVO> saveList=new ArrayList<BlackListVO>();
		if(!StringUtil.isEmpty(blackListnames)){
			String[] names=blackListnames.split(",");
			for(int i=0;i<names.length;i++){
				BlackListVO blackVO=new BlackListVO();
				blackVO.setPk_corp(pk_corp);
				blackVO.setBlacklistname(names[i]);
				blackVO.setDr(0);
				blackVO=(BlackListVO)singleObjectBO.insertVO(pk_corp, blackVO);
				saveList.add(blackVO);
			}
			checkIsRepeat(pk_corp);
		}
		return saveList;
	}
	/**
	 * 检查黑名单是否设置重复
	 * @param pk_corp
	 * @throws DZFWarpException
	 */
	private void checkIsRepeat(String pk_corp)throws DZFWarpException{
		String sql="select blacklistname,count(1) from ynt_blacklist where pk_corp=? and nvl(dr,0)=0 group by blacklistname having count(1)!=1";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
		if(list!=null&&list.size()>0){
			StringBuffer sb=new StringBuffer("以下关键字设置重复：【");
			for(int i=0;i<list.size();i++){
				Object[] obj=list.get(i);
				sb.append(obj[0].toString()+"、");
			}
			throw new BusinessException(sb.substring(0, sb.length()-1)+"】");
		}
	}
	/**
	 * 删除黑名单关键字
	 */
	@Override
	public void deleteBlackListVO(String pk_corp, String pk_blacklist) throws DZFWarpException {
		String sql="delete from ynt_blacklist where nvl(dr,0)=0 and pk_corp=? and pk_blacklist=?";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_blacklist);
		singleObjectBO.executeUpdate(sql, sp);
	}

}

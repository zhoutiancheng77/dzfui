package com.dzf.zxkj.platform.service.zncs.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.zncs.BaseCategoryVO;
import com.dzf.zxkj.platform.model.zncs.VouchertempletBVO;
import com.dzf.zxkj.platform.model.zncs.VouchertempletHVO;
import com.dzf.zxkj.platform.service.zncs.IVoucherTemplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VoucherTempletImpl implements IVoucherTemplet {
	@Autowired
    SingleObjectBO singleObjectBO;
	@Override
	public void saveVoucherTempletList(List<VouchertempletHVO> templetList, String pk_corp) throws DZFWarpException {
		if(templetList!=null&&templetList.size()>0){
			VouchertempletHVO headVO1=templetList.get(0);
			//查集团分类主键
			String sql="select * from ynt_basecategory where nvl(dr,0)=0 and pk_basecategory=(select pk_basecategory from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and pk_category=?)";
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(headVO1.getPk_category());
			List<BaseCategoryVO> baseList=(List<BaseCategoryVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BaseCategoryVO.class));
			if(baseList==null||baseList.size()==0){
				throw new BusinessException("保存失败，没有对应的预制自定义分类。");
			}
			//删子表
			String pk_basecategory=baseList.get(0).getPk_basecategory();
			sql="delete from ynt_vouchertemplet_b where pk_vouchertemplet_h in(select pk_vouchertemplet_h from ynt_vouchertemplet_h where pk_corp=? and pk_basecategory=?)";
			sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(pk_basecategory);
			singleObjectBO.executeUpdate(sql, sp);
			//删主表
			sql="delete from ynt_vouchertemplet_h where pk_corp=? and pk_basecategory=?";
			singleObjectBO.executeUpdate(sql, sp);
			//保存
			for(int i=0;i<templetList.size();i++){
				VouchertempletHVO headVO=templetList.get(i);
				VouchertempletBVO[] bodyVOs=(VouchertempletBVO[])headVO.getChildren();
				headVO.setPk_basecategory(pk_basecategory);//基础分类主键
				headVO.setPk_corp(pk_corp);
				headVO.setDr(0);
				headVO.setTempletname(baseList.get(0).getCategoryname());//模板名称
				for(int j=0;j<bodyVOs.length;j++){
					if(bodyVOs[j].getCreditmny()==null&&bodyVOs[j].getDebitmny()==null||bodyVOs[j].getCreditmny()!=null&&bodyVOs[j].getDebitmny()!=null){
						throw new BusinessException("借方或贷方必须选择一个");
					}
					bodyVOs[j].setPk_corp(pk_corp);
					bodyVOs[j].setDr(0);
				}
				headVO.setChildren(bodyVOs);
				singleObjectBO.saveObject(pk_corp, headVO);
			}
		}else{
			throw new BusinessException("删除失败，数据为空。");
		}
	}
	@Override
	public List<VouchertempletHVO> queryVoucherTempletList(String pk_corp, String pk_category) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_category)){
			throw new BusinessException("查询失败，参数错误.");
		}
		String sql="select * from ynt_basecategory where nvl(dr,0)=0 and pk_basecategory=(select pk_basecategory from ynt_billcategory where nvl(dr,0)=0 and pk_corp=? and pk_category=?)";
		SQLParameter sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_category);
		List<BaseCategoryVO> baseList=(List<BaseCategoryVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BaseCategoryVO.class));
		if(baseList==null||baseList.size()==0){
			throw new BusinessException("查询失败，没有对应的预制自定义分类。");
		}
		String pk_basecategory=baseList.get(0).getPk_basecategory();
		sql="select * from ynt_vouchertemplet_h where pk_corp=? and pk_basecategory=?";
		sp=new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(pk_basecategory);
		List<VouchertempletHVO> headList=(List<VouchertempletHVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VouchertempletHVO.class));
		if(headList!=null&&headList.size()>0){
			for(int i=0;i<headList.size();i++){
				String pk_vouchertemplet_h=headList.get(i).getPk_vouchertemplet_h();
				sql="select * from ynt_vouchertemplet_b where pk_vouchertemplet_h=? and nvl(dr,0)=0";
				sp=new SQLParameter();
				sp.addParam(pk_vouchertemplet_h);
				List<VouchertempletBVO> bodyList=(List<VouchertempletBVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(VouchertempletBVO.class));
				headList.get(i).setChildren(bodyList.toArray(new VouchertempletBVO[0]));
			}
		}
		return headList;
	}

}

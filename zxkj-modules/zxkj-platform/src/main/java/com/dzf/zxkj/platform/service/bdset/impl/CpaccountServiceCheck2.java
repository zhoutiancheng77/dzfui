package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.common.exception.BusinessException;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.service.bdset.ICpaccountService;
import com.dzf.zxkj.base.utils.SpringUtils;

/**
 * 科目校验类----更新
 *
 */
public class CpaccountServiceCheck2 extends CpaccountServiceBaseCheck{
	
	public CpaccountServiceCheck2(SingleObjectBO singleObjectBO){
		super(singleObjectBO);
	}
	
	/**
	 * 更新校验入口
	 */
	public void checkUpdate(YntCpaccountVO vo)throws BusinessException {
		//1、常规性校验
		if(vo == null)
			throw new BusinessException("更新数据为空！");
		YntCpaccountVO oldvo = queryYntvoByid(vo.getPk_corp_account());
		if(oldvo == null)
			throw new BusinessException("当前科目信息已被删除！请检查！");
		if(StringUtil.isEmpty(vo.getAccountcode()) || StringUtil.isEmpty(vo.getAccountname()))
			throw new BusinessException("科目编码或者名称不能为空！");
		if(vo.getAccountcode().length() != vo.getAccountcode().getBytes().length)
			throw new BusinessException("科目编码不能输入汉字！");
		YntCpaccountVO parentvo = getParentVOByID(vo);
		if(parentvo != null){
			if(parentvo.getAccountkind().intValue() != vo.getAccountkind().intValue())
				throw new BusinessException("子科目的科目类型必须和父科目一致！");
			if(parentvo.getDirection().intValue() != vo.getDirection().intValue())
				throw new BusinessException("子科目的科目方向必须和父科目一致！");
			if(vo.getPrimaryKey()!=null && parentvo.getBisseal().booleanValue())
				throw new BusinessException("父科目已封存，不允许操作!");
		}
		
		ICpaccountService gl_cpacckmserv = (ICpaccountService) SpringUtils.getBean("gl_cpacckmserv");
		String rule = gl_cpacckmserv.queryAccountRule(vo.getPk_corp());
		//2、校验编码规则
		checkCodeRule(vo,rule);
		//3、校验是否有修改权限
		checkSysCode(vo,oldvo);
		//4、 校验科目编码是否已经存在
		if(!vo.getAccountcode().equals(oldvo.getAccountcode())){
			if(isAccountCodeExist(vo.getAccountcode() , vo.getPk_corp()))
				throw new BusinessException("当前科目编码" + vo.getAccountcode() + "已经存在！");
		}
		//5、 校验同级科目名称是否已经存在
		if(!vo.getAccountname().equals(oldvo.getAccountname())){
			if(isAccountNameExist(vo.getAccountcode() ,vo.getAccountname(), vo.getPk_corp()))
				throw new BusinessException("当前科目名称" + vo.getAccountname() + "已经存在！");
		}
//		CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
//		if(corpvo == null || corpvo.getBbuildic() == null || !corpvo.getBbuildic().booleanValue()){
//			//throw new BusinessException("当前公司没有启用进销存！");
//		}else{
//			//6、是否有子科目
//			boolean haschild = hasChildren(vo.getPk_corp(),vo);
//			if(haschild){
//				throw new BusinessException("当前科目存在子科目，不允许修改！");
//			}
//		}
		//6、是否有子科目
//		boolean haschild = hasChildren(vo.getPk_corp(),vo);
//		if(haschild){
//			throw new BusinessException("当前科目存在子科目，不允许修改！");
//		}
		//7、校验科目是否被凭证引用
//		checkpz(vo.getPk_corp(),vo,oldvo);
		//8、判断当前公司是否启用库存管理
		//if(vo.getIsnum() != null && vo.getIsnum().booleanValue()){
		//	CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
//			if(corpvo == null || corpvo.getBbuildic() == null || !corpvo.getBbuildic().booleanValue()){
//				throw new BusinessException("当前公司没有启用进销存！");
//			}
	//	}
//		//9、判断当前公司是否启用外币(先不用判断)
//		if(vo.getIswhhs() != null && vo.getIswhhs().booleanValue()){
//			CorpVO corpvo = (CorpVO)singleObjectBO.queryByPrimaryKey(CorpVO.class, vo.getPk_corp());
//			if(corpvo == null || corpvo.getIscurr() == null || !corpvo.getIscurr().booleanValue()){
//				throw new BusinessException("当前公司没有启用外币！");
//			}
//		}
	}
	

	
	/**
	 * 校验---是否有修改权限
	 */
	private void checkSysCode(YntCpaccountVO newvo,YntCpaccountVO oldvo)throws BusinessException{
		if(newvo == null || oldvo == null)
			return;
		if(oldvo.getIssyscode()!=null && oldvo.getIssyscode().booleanValue()){
			//校验编码
			if(!newvo.getAccountcode().equals(oldvo.getAccountcode())){
				throw new BusinessException("系统科目不允许修改编码！");
			}
			//校验名称
			if(!newvo.getAccountname().equals(oldvo.getAccountname())){
				throw new BusinessException("系统科目不允许修改名称！");
			}
			//类型
			if(!newvo.getAccountkind().equals(oldvo.getAccountkind())){
				throw new BusinessException("系统科目不允许修改类型！");
			}
			//方向
			if(!newvo.getDirection().equals(oldvo.getDirection())){
				throw new BusinessException("系统科目不允许修改方向！");
			}
		}
	}
	/**
	 * 校验----科目是否被凭证引用
	 */
	private void checkpz(String pk_corp,YntCpaccountVO vo,YntCpaccountVO oldvo)throws BusinessException{
		SQLParameter sqlp2 =new SQLParameter();
		sqlp2.addParam(pk_corp);
		sqlp2.addParam(vo.getPrimaryKey());
		boolean bpz=isExists(pk_corp,  "select 1 from ynt_tzpz_b where pk_corp = ? and  pk_accsubj= ? and nvl(dr,0)=0 ",sqlp2);
		if(bpz){
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被凭证引用，不允许修改!");
		}
	}
	
}

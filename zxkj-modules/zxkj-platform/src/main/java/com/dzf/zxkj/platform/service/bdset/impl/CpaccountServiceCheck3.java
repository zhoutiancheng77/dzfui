package com.dzf.zxkj.platform.service.bdset.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.utils.SpringUtils;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.platform.model.bdset.YntCpaccountVO;
import com.dzf.zxkj.platform.model.qcset.QcYeVO;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;

import java.util.List;

/**
 * 科目校验类
 *
 */
public class CpaccountServiceCheck3 extends CpaccountServiceBaseCheck{
	
	public CpaccountServiceCheck3(SingleObjectBO singleObjectBO){
		super(singleObjectBO);
	}
	
	/**
	 * 删除校验入口
	 */
	public void checkDelete(YntCpaccountVO vo)throws BusinessException {
		//0、常规性校验
		if(vo == null)
			throw new BusinessException("删除数据为空！请刷新重新操作！");
		//1、是否有删除权限
		checkSysCode(vo,vo);
		//2、是否已录入期初
		checkQC(vo.getPk_corp(),vo);
		//3、是否有凭证
		checkpz(vo.getPk_corp(),vo);
		//3.5、是否有会计工厂凭证
		checkFctPz(vo.getPk_corp(),vo);
		//4、是否有模板引用
		checkpzmb(vo.getPk_corp(),vo);
		//4.1、是否工资表引用
		checkgzb(vo.getPk_corp(),vo);
		//5、是否有下级科目
		boolean haschild = hasChildren(vo.getPk_corp(),vo);
		if(haschild){
			throw new BusinessException("当前科目存在子科目，不允许删除！");
		}
		//6、是否被收入预警表引用
		checkRef(vo);
		// 校验是否是新库存模式
		CorpVO corpvo = SpringUtils.getBean(ICorpService.class).queryByPk(vo.getPk_corp());
		if (corpvo.getIbuildicstyle() != null && corpvo.getIbuildicstyle() == 1) {
			checkIsIc(vo.getPk_corp(), vo);
		}
	}
	/*
	 * 校验----是否被库存模块引用
	 */
	private void checkIsIc(String pk_corp, YntCpaccountVO vo) throws BusinessException {

		SQLParameter sqlp1 = new SQLParameter();
		sqlp1.addParam(vo.getPrimaryKey());
		sqlp1.addParam(pk_corp);
		//库存期初
		boolean ret = isExists(pk_corp,
				"select 1 from ynt_icbalance where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ", sqlp1);
		if (ret) {
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被库存期初引用，不允许删除!");
		}
		//存货
		ret = isExists(pk_corp, "select 1 from ynt_inventory where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ",
				sqlp1);
		if (ret) {
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被存货引用，不允许删除!");
		}
		//入库单
		ret = isExists(pk_corp, "select 1 from ynt_ictradein where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ",
				sqlp1);
		if (ret) {
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被入库单引用，不允许删除!");
		}
		//出库单
		ret = isExists(pk_corp, "select 1 from ynt_ictradeout where  pk_subject= ? and pk_corp = ?  and nvl(dr,0)=0 ",
				sqlp1);
		if (ret) {
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被出库单引用，不允许删除!");
		}

	}
	
	private void checkRef(YntCpaccountVO vo) {
		SQLParameter sqlp =new SQLParameter();
		sqlp.addParam(vo.getPk_corp());
		sqlp.addParam(vo.getPrimaryKey());
		StringBuffer sql = new StringBuffer();
		sql.append(" select yi.*　from ynt_IncomeWarning yi ");
//		sql.append(" left join ynt_cpaccount yc on yi.pk_corp = yc.pk_corp and yi.pk_accsubj = yc.pk_corp_account ");
		sql.append(" where nvl(yi.dr,0)=0 and yi.pk_corp = ? and yi.pk_accsubj = ? ");	
		boolean bpz=isExists(vo.getPk_corp(),  " select 1　from ynt_IncomeWarning yi where nvl(yi.dr,0)=0 and yi.pk_corp = ? and yi.pk_accsubj = ? ",sqlp);
		if(bpz){
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被收入预警引用，不允许删除!");
		}
		
	}

	/**
	 * 校验---是否有删除权限
	 */
	private void checkSysCode(YntCpaccountVO newvo,YntCpaccountVO oldvo)throws BusinessException{
		if(newvo == null || oldvo == null)
			return;
		if(oldvo.getIssyscode()!=null && oldvo.getIssyscode().booleanValue()){
			throw new BusinessException("系统科目不允许删除!");
		}
	}
	
	
	/**
	 * 校验----科目是否被凭证引用
	 */
	private void checkpz(String pk_corp,YntCpaccountVO vo)throws BusinessException{
		SQLParameter sqlp =new SQLParameter();
		sqlp.addParam(pk_corp);
		sqlp.addParam(vo.getPrimaryKey());
		boolean bpz=isExists(pk_corp,  "select 1 from ynt_tzpz_b where pk_corp = ? and pk_accsubj= ? and nvl(dr,0)=0 ",sqlp);
		if(bpz){
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被凭证引用，不允许删除!");
		}
	}
	
	/**
	 * 会计工厂是否引用
	 * @param pk_corp
	 * @param vo
	 * @throws BusinessException
	 */
	private void checkFctPz(String pk_corp,YntCpaccountVO vo)throws BusinessException{
		SQLParameter sqlp =new SQLParameter();
		sqlp.addParam(pk_corp);
		sqlp.addParam(vo.getPrimaryKey());
		boolean bpz=isExists(pk_corp,  "select 1 from fct_ynt_tzpz_b where pk_corp = ? and pk_accsubj= ? and nvl(dr,0)=0 ",sqlp);
		if(bpz){
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被会计工厂凭证凭证引用，不允许删除!");
		}
	}
	
	private void checkgzb(String pk_corp,YntCpaccountVO vo)throws BusinessException{
		SQLParameter sqlp =new SQLParameter();
		sqlp.addParam(pk_corp);
		sqlp.addParam(vo.getPrimaryKey());
		boolean bpz=isExists(pk_corp,  "select 1 from ynt_salarykmdept where pk_corp = ? and ckjkmid= ? and nvl(dr,0)=0 ",sqlp);
		if(bpz){
			throw new BusinessException("科目【"+vo.getAccountcode()+"  "+vo.getAccountname()+"】已被工资表部门费用设置引用，不允许删除!");
		}
	}
	/**
	 * 校验----科目是否被凭证模板引用
	 */
	private void checkpzmb(String pk_corp,YntCpaccountVO parentVO)throws BusinessException{
		//成本模板
		SQLParameter sqlp =new SQLParameter();
		sqlp.addParam(parentVO.getPrimaryKey());
		sqlp.addParam(parentVO.getPrimaryKey());
		sqlp.addParam(parentVO.getPrimaryKey());
		boolean bmb1=isExists(pk_corp,  "select 1 from ynt_cpcosttrans where"
				+ "  (pk_fillaccount= ? or pk_creditaccount= ? or pk_debitaccount= ? ) and nvl(dr,0)=0 ",sqlp);
		if(bmb1){
			throw new BusinessException("当前科目已被成本模板引用，不能删除!");
		}
		//常用模板
		SQLParameter sqlp1 =new SQLParameter();
		sqlp1.addParam(parentVO.getPrimaryKey());
		boolean bmb2=isExists(pk_corp,  "select 1 from ynt_cppztemmb_b where  pk_accsubj= ? and nvl(dr,0)=0 ",sqlp1);
		if(bmb2){
			throw new BusinessException("当前科目已被凭证常用模板引用，不能删除!");
		}
		//折旧清理模板
		boolean bmb5=isExists(pk_corp,  "select 1 from ynt_cpmb_b where  pk_account= ? and nvl(dr,0)=0 ",sqlp1);
		if(bmb5){
			throw new BusinessException("当前科目已被折旧清理模板引用，不能删除!");
		}
		//汇兑损益模板
		SQLParameter sqlp2 =new SQLParameter();
		sqlp2.addParam(parentVO.getPrimaryKey());
		sqlp2.addParam(parentVO.getPrimaryKey());
		boolean bmb6=isExists(pk_corp,  "select 1 from ynt_remittance where  (pk_corp_account= ? or  pk_out_account= ? ) and nvl(dr,0)=0 ",sqlp2);
		if(bmb6){
			throw new BusinessException("当前科目已被汇兑损益模板引用，不能删除!");
		}
		//期间损益模板
		boolean bmb7=isExists(pk_corp,  "select 1 from ynt_cptransmb where  pk_transferinaccount= ? and nvl(dr,0)=0 ",sqlp1);
		if(bmb7){
			throw new BusinessException("当前科目已被期间损益模板引用，不能删除!");
		}
		
		//业务类型模板
		boolean businessTemp = isExists(pk_corp,  "select 1 from ynt_dcmodel_b where pk_accsubj = ? and nvl(dr,0)=0 ", sqlp1);
		if(businessTemp){
			throw new BusinessException("当前科目已被业务类型模板引用，不能删除!");
		}
	}
	
	/**
	 * 校验----父科目是否已经录入了期初余额
	 */
	private void checkQC(String pk_corp,YntCpaccountVO vo)throws BusinessException{
		String where = " pk_corp=? and pk_accsubj=? and nvl(dr,0)=0 ";
		SQLParameter sp =new SQLParameter();
		sp.addParam(pk_corp);
		sp.addParam(vo.getPrimaryKey());
		List<QcYeVO> rslist = (List<QcYeVO>)singleObjectBO.retrieveByClause(QcYeVO.class, where, sp);
		if(rslist!=null&&rslist.size()>0){
			if(rslist.get(0).getThismonthqc()!=null&&rslist.get(0).getThismonthqc().doubleValue()!=0){
				throw new BusinessException("当前科目已被期初余额引用，不能删除!");
			}else if(rslist.get(0).getYeardffse()!=null&&rslist.get(0).getYeardffse().doubleValue()!=0){
				throw new BusinessException("当前科目已被期初余额引用，不能删除!");
			}else if(rslist.get(0).getYearjffse()!=null&&rslist.get(0).getYearjffse().doubleValue()!=0){
				throw new BusinessException("当前科目已被期初余额引用，不能删除!");
			}else if(rslist.get(0).getYearqc()!=null&&rslist.get(0).getYearqc().doubleValue()!=0){
				throw new BusinessException("当前科目已被期初余额引用，不能删除!");
			}
		}
		
		boolean hasVerifyBegin = singleObjectBO.isExists(pk_corp,
				" select 1 from ynt_verify_begin where pk_corp=? and pk_accsubj=? and nvl(dr,0)=0 ", sp);
		if (hasVerifyBegin) {
			throw new BusinessException("当前科目已被未核销期初引用，不能删除!");
		}
	}
}

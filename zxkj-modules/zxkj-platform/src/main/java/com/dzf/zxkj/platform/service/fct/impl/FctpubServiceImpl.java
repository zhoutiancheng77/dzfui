package com.dzf.zxkj.platform.service.fct.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.FactoryConst;
import com.dzf.zxkj.common.constant.IRoleConstants;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.model.zncs.BusiapplyBVO;
import com.dzf.zxkj.platform.model.zncs.BusiapplyHVO;
import com.dzf.zxkj.platform.service.fct.IFctpubService;
import com.dzf.zxkj.platform.service.sys.IUserService;
import com.dzf.zxkj.secret.CorpSecretUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("pubFctService")
public class FctpubServiceImpl implements IFctpubService {

	@Autowired
	private SingleObjectBO singleObjectBO;
	
	@Autowired
	private IUserService userServiceImpl;
	
	@Override
	public boolean isFctCorp(DZFDate curDate, String pk_corp) throws DZFWarpException {
		if(curDate!=null&&!StringUtil.isEmpty(pk_corp)){
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("  select *                  ");
			sb.append("    from fct_busiapply_b    ");
			sb.append("   where nvl(dr, 0) = 0     ");
			sb.append("     and pk_customer = ?    ");
			sb.append("     and begindate <= ?     ");
			sb.append("     and enddate >= ?       ");
			sb.append("     and vstatus = ?        ");
			sp.addParam(pk_corp);//公司
			sp.addParam(curDate);//日期
			sp.addParam(curDate);//日期
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			List<BusiapplyBVO> list=(List<BusiapplyBVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BusiapplyBVO.class));
			if(list!=null&&list.size()>0){
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isConfirmCorp(String pk_corp) throws DZFWarpException {
		if(!StringUtil.isEmpty(pk_corp)){
			String sql="select * from fct_busiapply where pk_corp=? and nvl(dr,0)=0 and vstatus=?";
			SQLParameter sp=new SQLParameter();
			sp.addParam(pk_corp);
			sp.addParam(FactoryConst.ContractStatus_3);
			List<BusiapplyHVO> list=(List<BusiapplyHVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(BusiapplyHVO.class));
			if(list!=null&&list.size()>0){
				return true;
			}
		}
		return false;
	}

	@Override
	public String getAthorizeFactoryCorp(DZFDate curDate,String pk_corp) throws DZFWarpException {
		if(curDate!=null&&!StringUtil.isEmpty(pk_corp)){
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("  select h.pk_factory         ");
			sb.append("    from fct_busiapply_b b,fct_busiapply h    ");
			sb.append("   where h.pk_busiapply=b.pk_busiapply and nvl(b.dr, 0) = 0  and nvl(h.dr, 0) = 0   ");
			sb.append("     and b.pk_customer = ?    ");
			sb.append("     and b.begindate <= ?     ");
			sb.append("     and b.enddate >= ?       ");
			sb.append("     and b.vstatus = ? and h.vstatus = ?       ");
			sp.addParam(pk_corp);//公司
			sp.addParam(curDate);//日期
			sp.addParam(curDate);//日期
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			List<BusiapplyHVO> list=(List<BusiapplyHVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(BusiapplyHVO.class));
			if(list!=null&&list.size()>0){//如果一个客户委托给了多个会计工厂?
				return list.get(0).getPk_factory();
			}
		}
		return null;
	}

	@Override
	public List<String> queryPowerCustomer(String pk_factory, String pk_user,String period) throws DZFWarpException {
		if(!StringUtil.isEmpty(pk_factory)&&!StringUtil.isEmpty(pk_user)){
			DZFDate curDate=null;
			if(!StringUtil.isEmpty(period)){
				curDate=new DZFDate(period+"-01");
			}else{
				curDate=new DZFDate();
			}
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("  select distinct b.pk_customer        ");
			sb.append("    from fct_busiapply_b b,fct_busiapply h    ");
			sb.append("   where h.pk_busiapply=b.pk_busiapply and nvl(b.dr, 0) = 0  and nvl(h.dr, 0) = 0   ");
			sb.append("     and h.pk_factory = ?    ");
			sb.append("     and b.begindate <= ?     ");
			sb.append("     and b.enddate >= ?       ");
			sb.append("     and b.vstatus = ? and h.vstatus = ?       ");
			sp.addParam(pk_factory);//会计工厂
			sp.addParam(curDate);//日期
			sp.addParam(curDate);//日期
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			
			UserVO userVo=  userServiceImpl.queryUserJmVOByID(pk_user);
			DZFBoolean ismanager = userVo.getIsmanager() == null ? DZFBoolean.FALSE:userVo.getIsmanager();
			if(DZFBoolean.FALSE.equals(ismanager)){
				sb.append("     and b.pk_customer in(select pk_corp from sm_user_role where nvl(dr,0)=0 ");
				sb.append("     and pk_role in(select pk_role from sm_role where roletype=? and nvl(dr,0)=0 and nvl(seal,'N')='N')  ");
				sb.append("     and cuserid=? )");
				sp.addParam(IRoleConstants.ROLE_6);
				sp.addParam(pk_user);
			}
			List<Object[]> list=(List<Object[]>)singleObjectBO.executeQuery(sb.toString(), sp, new ArrayListProcessor());
			if(list!=null&&list.size()>0){
				List<String> returnList=new ArrayList<String>();
				for(int i=0;i<list.size();i++){
					returnList.add(list.get(i)[0].toString());
				}
				return returnList;
			}else{
				return null;
			}
		}
		return null;
	}

	@Override
	public List<CorpVO> queryCustomersByFactory(String pk_factory, String[] queryParam) throws DZFWarpException {
		if(!StringUtil.isEmpty(pk_factory)){
			int page = Integer.parseInt(queryParam[0]);
			int size = Integer.parseInt(queryParam[1]);
			
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("select * from (select ROWNUM AS ROWNO,bc.* from bd_corp bc where nvl(dr,0)=0 and pk_corp in (");
			sb.append("  select distinct b.pk_customer        ");
			sb.append("    from fct_busiapply_b b,fct_busiapply h    ");
			sb.append("   where h.pk_busiapply=b.pk_busiapply and nvl(b.dr, 0) = 0  and nvl(h.dr, 0) = 0   ");
			sb.append("     and h.pk_factory = ?    ");
			sp.addParam(pk_factory);//会计工厂
			if (queryParam[2] != null && !queryParam[2].equals("")) {
				sb.append("     and b.applydate >= ?     ");
				sp.addParam(queryParam[2]);
			}
			if (queryParam[3] != null && !queryParam[3].equals("")) {
				sb.append(" and b.applydate<=? ");
				sp.addParam(queryParam[3]);
			}
			
			sb.append("     and b.vstatus = ? and h.vstatus = ?) ");
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			if (queryParam[6] != null && !queryParam[6].equals("")) {
				sb.append(" and bc.innercode like '%" + queryParam[6] + "%'");
			}
//			if (queryParam[7] != null && !queryParam[7].equals("")) {
//				sb.append(" and instr(bc.unitname,'" + CodeUtils1.enCode(queryParam[7]) + "') > 0");
//			}
			
			if (queryParam[7] != null && !queryParam[7].equals("")) {
			    sb.append(" and bc.pk_corp = ? ");
	            sp.addParam(queryParam[7]);
	        }
			sb.append("  and ROWNUM<="+ page * size + " )  WHERE ROWNO> " + (page - 1) * size + "     ");
			
			List<CorpVO> list=(List<CorpVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CorpVO.class));
			return list;
		}
		return null;
	}

	@Override
	public int queryTotalCustomersByFactory(String pk_factory, String[] queryParam) throws DZFWarpException {
		if(!StringUtil.isEmpty(pk_factory)){
			StringBuffer sb=new StringBuffer();
			SQLParameter sp=new SQLParameter();
			sb.append("select * from (select ROWNUM AS ROWNO,bc.* from bd_corp bc where nvl(dr,0)=0 and pk_corp in (");
			sb.append("  select distinct b.pk_customer        ");
			sb.append("    from fct_busiapply_b b,fct_busiapply h    ");
			sb.append("   where h.pk_busiapply=b.pk_busiapply and nvl(b.dr, 0) = 0  and nvl(h.dr, 0) = 0   ");
			sb.append("     and h.pk_factory = ?    ");
			sp.addParam(pk_factory);//会计工厂
			if (queryParam[2] != null && !queryParam[2].equals("")) {
				sb.append("     and b.applydate >= ?     ");
				sp.addParam(queryParam[2]);
			}
			if (queryParam[3] != null && !queryParam[3].equals("")) {
				sb.append(" and b.applydate<=? ");
				sp.addParam(queryParam[3]);
			}
			
			sb.append("     and b.vstatus = ? and h.vstatus = ?) ");
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			sp.addParam(FactoryConst.ContractStatus_3);//状态:在执行
			if (queryParam[6] != null && !queryParam[6].equals("")) {
				sb.append(" and bc.innercode like '%" + queryParam[6] + "%'");
			}
			if (queryParam[7] != null && !queryParam[7].equals("")) {
				sb.append(" and instr(bc.unitname,'" + CorpSecretUtil.enCode(queryParam[7]) + "') > 0");
			}
			sb.append("   ) ");
			
			List<CorpVO> list=(List<CorpVO>)singleObjectBO.executeQuery(sb.toString(), sp, new BeanListProcessor(CorpVO.class));
			return list.size();
		}
		return 0;
	}

}

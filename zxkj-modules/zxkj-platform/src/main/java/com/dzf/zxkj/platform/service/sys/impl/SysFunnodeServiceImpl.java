package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IcCostStyle;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.IGlobalConstants;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.SysFunNodeVO;
import com.dzf.zxkj.platform.model.sys.UserVO;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import com.dzf.zxkj.platform.service.sys.ISysFunnodeService;
import com.dzf.zxkj.platform.service.sys.IVersionMngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


/**
 * 权限节点
 * 
 */
@Service("sys_funnodeserv")
public class SysFunnodeServiceImpl implements ISysFunnodeService {

	private SingleObjectBO singleObjectBO = null;
	
	@Autowired
	private IVersionMngService sys_funnodeversionserv;
	@Autowired
	private ICorpService corpService;

	public SingleObjectBO getSingleObjectBO() {
		return singleObjectBO;
	}

	
	@Autowired
	public void setSingleObjectBO(SingleObjectBO singleObjectBO) {
		this.singleObjectBO = singleObjectBO;
	}

	/**
	 * 
	 */
	public SysFunNodeVO[] querySysNode(String pk_parent) throws DZFWarpException {
		SysFunNodeVO[] vos= null;
		if(pk_parent == null || pk_parent.trim().length()<1){
			vos = (SysFunNodeVO[]) singleObjectBO.queryByCondition(SysFunNodeVO.class, " (pk_parent is null or pk_parent = '') and nvl(dr,0)=0 order by show_order", null);
		}else{
			SQLParameter params = new SQLParameter();
			params.addParam(pk_parent);
			String condition = " pk_parent = ? and nvl(dr,0)=0 order by show_order  ";
		    vos= (SysFunNodeVO[]) singleObjectBO.queryByCondition(SysFunNodeVO.class, condition, params);
		}
		if(vos == null || vos.length == 0)
			return null;
		return vos;

	}


	@Override
	public SysFunNodeVO save(SysFunNodeVO vo) throws DZFWarpException {
		if(!StringUtil.isEmpty(vo.getPk_funnode())){
			update(vo);
			return vo;
		}
		return (SysFunNodeVO) singleObjectBO.saveObject(IDefaultValue.DefaultGroup, vo);
	}
	
	@Override
	public SysFunNodeVO queryVOByID(String pk_funnode) throws DZFWarpException{
		SysFunNodeVO vo = (SysFunNodeVO)singleObjectBO.queryVOByID(pk_funnode, SysFunNodeVO.class);
		return vo;
	}
	
	

	@Override
	public void delete(SysFunNodeVO vo) throws DZFWarpException {
		singleObjectBO.deleteObject(vo);
	}
	
	public void update(SysFunNodeVO vo) throws DZFWarpException {
		singleObjectBO.update(vo);
	}


	@Override
	public SysFunNodeVO[] querySysnodeByUserId(String userId)
			throws DZFWarpException {
		StringBuffer corpsql = new StringBuffer();
		corpsql.append("select s.* from sys_funnode s where s.id in (select )"); 
		return null;
	}
	
	public List<SysFunNodeVO> querySysnodeByUserAndCorp(UserVO user, CorpVO corp, String path) throws DZFWarpException{
//		String sql = new String("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER "+
//				" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE "+
//				" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE "+
//				" WHERE ROL.CUSERID =? AND ROL.PK_CORP=?  AND MODULE = ? AND NVL(POWER.DR,0)=0 AND NVL(ROL.DR,0)=0 "
//				+ "ORDER BY SHOW_ORDER");
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER ");
		sql.append(" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE ");
		sql.append(" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE ");
		sql.append(" WHERE ROL.CUSERID =? AND ROL.PK_CORP=?  AND MODULE = ? ");
		sql.append(" AND NVL(POWER.DR,0)=0 AND NVL(ROL.DR,0)=0 AND NVL(FUN.DR,0)=0");
		DZFBoolean holdflag = corp.getHoldflag()==null ?DZFBoolean.FALSE:corp.getHoldflag();//holdflag
		if(!holdflag.booleanValue()){
			sql.append(" AND FUN.fun_code != 'kj177'");
		}
		if (IcCostStyle.IC_ON.equals(corp.getBbuildic())) {//不启用库存
			if (corp.getIbuildicstyle() != null && corp.getIbuildicstyle() == 1) {
				sql.append(" AND FUN.fun_code != 'kj217'");
			} else {
				sql.append(" AND FUN.fun_code != 'kj517'");
			}
		} else {
			sql.append(" AND FUN.fun_code != 'kj517'");
			sql.append(" AND FUN.fun_code != 'kj217'");
		}
		CorpVO fvo  = corpService.queryByPk(user.getPk_corp());
		if(fvo.getIschannel() == null || !fvo.getIschannel().booleanValue()){
            sql.append(" AND FUN.fun_code != 'admin009'");
        }else if(fvo.getIschannel() != null && fvo.getIschannel().booleanValue()){
            sql.append(" and FUN.fun_code != 'admin134' and FUN.fun_code != 'admin052' and FUN.fun_code != 'admin059' ");
        }
		//如果是服务网点，不显示批量建账节点
		if(!StringUtil.isEmpty(fvo.getDef20()) && "F2".equals(fvo.getDef20())){
		    sql.append(" and FUN.fun_code != 'admin039'");
		}
//		if(fvo.getIsfactory() == null || !fvo.getIsfactory().booleanValue()){
//		    boolean isConfirm = pubFctService.isConfirmCorp(fvo.getPk_corp());
//            sql.append(" AND FUN.fun_code != 'admin102' AND FUN.fun_code != 'admin101' AND FUN.fun_code != 'admin098'");
//            sql.append(" AND FUN.fun_code != 'admin100' and fun.fun_code != 'admin115' and fun.fun_code != 'admin116' and fun.fun_code != 'admin117' ");
//            if(!isConfirm){
//                sql.append(" AND FUN.fun_code != 'admin097' ");
//            }
//        }
		Integer isweixin = fvo.getIsweixin()==null ?1:fvo.getIsweixin();
		if(isweixin==1){//未开通
			sql.append(" AND FUN.fun_code != 'admin818' AND FUN.fun_code != 'admin820'");
		}else if(isweixin==0){//开通
			sql.append(" AND FUN.fun_code != 'admin818'");
		}
		//如果代账公司是服务网点，可以使用财税平台模块
		if(fvo.getDef20() == null || fvo.getDef20().equals("F1")){
		    sql.append(" AND FUN.fun_code != 'admin166'");
		}
		///在线端和会计端，增加收费过滤 zpm
		if(IGlobalConstants.DZF_KJ.equals(path) || IGlobalConstants.ADMIN_KJ.equals(path)){
			String[] funcodes = sys_funnodeversionserv.queryCorpVersion(fvo.getPk_corp());
			if(funcodes != null && funcodes.length>0){
				String data = SqlUtil.buildSqlConditionForIn(funcodes);
				sql.append(" AND FUN.pk_funnode in("+data+") ");
			}
		}
		
		sql.append(" ORDER BY SHOW_ORDER");
		SQLParameter sp = new SQLParameter();
		sp.addParam(user.getCuserid());
		sp.addParam(corp.getPk_corp());
		sp.addParam(path);
		List<SysFunNodeVO> list = (ArrayList<SysFunNodeVO>)getSingleObjectBO().executeQuery(sql.toString(), sp, new BeanListProcessor(SysFunNodeVO.class));
		return list;
	}
	
	public List<SysFunNodeVO> querySysnodeByUser(UserVO user,String path) throws DZFWarpException{
//		String sql = new String("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER "+
//				" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE "+
//				" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE "+
//				" WHERE ROL.CUSERID =? AND MODULE = ? AND NVL(POWER.DR,0)=0 and nvl(FUN.dr,0) = 0 AND NVL(ROL.DR,0)=0 ORDER BY SHOW_ORDER");
	    StringBuffer sql = new StringBuffer();
	    sql.append("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER ");
	    sql.append(" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE ");
	    sql.append(" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE ");
	    sql.append(" WHERE ROL.CUSERID =? AND MODULE = ? AND NVL(POWER.DR,0)=0 ");
	    sql.append(" and nvl(FUN.dr,0) = 0 AND NVL(ROL.DR,0)=0");
		CorpVO corp =corpService.queryByPk(user.getPk_corp());
//		if(corpVo != null){
//		    if(corpVo.getIschannel() == null || !corpVo.getIschannel().booleanValue()){
//		        sql.append(" AND FUN.fun_code != 'admin69'");
//		    }else if(corpVo.getIschannel() != null && corpVo.getIschannel().booleanValue()){
//		        sql.append(" and FUN.fun_code != 'xwwy012'");
//		    }
//		}
		Integer isweixin = corp.getIsweixin()==null ?1:corp.getIsweixin();
		if(isweixin==1){//未开通
            sql.append(" AND FUN.fun_code != 'admin818' AND FUN.fun_code != 'admin820'");
        }else if(isweixin==0){//开通
            sql.append(" AND FUN.fun_code != 'admin818'");
        }
		//如果代账公司是服务网点，可以使用财税平台模块
        if(corp.getDef20() == null || corp.getDef20().equals("F1")){
            sql.append(" AND FUN.fun_code != 'admin166'");
        }
      //如果是服务网点，不显示批量建账节点
        if(!StringUtil.isEmpty(corp.getDef20()) && "F2".equals(corp.getDef20())){
            sql.append(" and FUN.fun_code != 'admin039'");
        }
	  ///在线端和会计端，增加收费过滤 zpm
        if(IGlobalConstants.DZF_KJ.equals(path) || IGlobalConstants.ADMIN_KJ.equals(path)){
            String[] funcodes = sys_funnodeversionserv.queryCorpVersion(corp.getPk_corp());
            if(funcodes != null && funcodes.length>0){
                String data = SqlUtil.buildSqlConditionForIn(funcodes);
                sql.append(" AND FUN.pk_funnode in("+data+") ");
            }
        }
		sql.append(" ORDER BY SHOW_ORDER");
				SQLParameter sp = new SQLParameter();
				sp.addParam(user.getCuserid());
				sp.addParam(path);
				List<SysFunNodeVO> list = (ArrayList<SysFunNodeVO>)getSingleObjectBO().executeQuery(sql.toString(), sp, new BeanListProcessor(SysFunNodeVO.class));
				return list;
	}
	
	
	public List<SysFunNodeVO> querySysnodeByUser1(UserVO user,String path,String pk_corp) throws DZFWarpException{
//		String sql = new String("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER "+
//				" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE "+
//				" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE "+
//				" WHERE ROL.CUSERID =? AND MODULE = ? AND NVL(POWER.DR,0)=0 and nvl(FUN.dr,0) = 0 AND NVL(ROL.DR,0)=0 ORDER BY SHOW_ORDER");
	    StringBuffer sql = new StringBuffer();
	    sql.append("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER ");
	    sql.append(" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE ");
	    sql.append(" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE ");
	    sql.append(" WHERE ROL.CUSERID =? AND MODULE = ? and rol.pk_corp = ? AND NVL(POWER.DR,0)=0 ");
	    sql.append(" and nvl(FUN.dr,0) = 0 AND NVL(ROL.DR,0)=0");
		CorpVO corpVo =corpService.queryByPk(user.getPk_corp());
//		if(corpVo != null){
//		    if(corpVo.getIschannel() == null || !corpVo.getIschannel().booleanValue()){
//		        sql.append(" AND FUN.fun_code != 'admin69'");
//		    }else if(corpVo.getIschannel() != null && corpVo.getIschannel().booleanValue()){
//		        sql.append(" and FUN.fun_code != 'xwwy012'");
//		    }
//		}
	    Integer isweixin = corpVo.getIsweixin()==null ?1:corpVo.getIsweixin();
	    if(isweixin==1){//未开通
            sql.append(" AND FUN.fun_code != 'admin818' AND FUN.fun_code != 'admin820'");
        }else if(isweixin==0){//开通
            sql.append(" AND FUN.fun_code != 'admin818'");
        }
		//如果代账公司是服务网点，可以使用财税平台模块
        if(corpVo.getDef20() == null || corpVo.getDef20().equals("F1")){
            sql.append(" AND FUN.fun_code != 'admin166'");
        }
      //如果是服务网点，不显示批量建账节点
        if(!StringUtil.isEmpty(corpVo.getDef20()) && "F2".equals(corpVo.getDef20())){
            sql.append(" and FUN.fun_code != 'admin039'");
        }
	  ///在线端和会计端，增加收费过滤 zpm
        if(IGlobalConstants.DZF_KJ.equals(path) || IGlobalConstants.ADMIN_KJ.equals(path)){
            String[] funcodes = sys_funnodeversionserv.queryCorpVersion(corpVo.getPk_corp());
            if(funcodes != null && funcodes.length>0){
                String data = SqlUtil.buildSqlConditionForIn(funcodes);
                sql.append(" AND FUN.pk_funnode in("+data+") ");
            }
        }
		sql.append(" ORDER BY SHOW_ORDER");
				SQLParameter sp = new SQLParameter();
				sp.addParam(user.getCuserid());
				sp.addParam(path);
				sp.addParam(pk_corp);
				List<SysFunNodeVO> list = (ArrayList<SysFunNodeVO>)getSingleObjectBO().executeQuery(sql.toString(), sp, new BeanListProcessor(SysFunNodeVO.class));
				return list;
	}


    @Override
    public ArrayList<SysFunNodeVO> queryFatSysnodeByUser(UserVO user, String path) throws DZFWarpException {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DISTINCT FUN.* FROM SM_POWER_FUNC POWER ");
        sql.append(" INNER JOIN SM_FUNNODE FUN ON POWER.RESOURCE_DATA_ID=FUN.PK_FUNNODE ");
        sql.append(" INNER JOIN SM_USER_ROLE ROL ON ROL.PK_ROLE=POWER.PK_ROLE ");
        sql.append(" WHERE ROL.CUSERID =? AND ROL.PK_CORP=?  AND MODULE = ? ");
        sql.append(" AND NVL(POWER.DR,0)=0 AND NVL(ROL.DR,0)=0 AND NVL(FUN.DR,0)=0");
        
        sql.append(" ORDER BY SHOW_ORDER");
        SQLParameter sp = new SQLParameter();
        sp.addParam(user.getCuserid());
        sp.addParam(IDefaultValue.DefaultGroup);
        sp.addParam(path);
        ArrayList<SysFunNodeVO> list = (ArrayList<SysFunNodeVO>)getSingleObjectBO().executeQuery(sql.toString(), sp, new BeanListProcessor(SysFunNodeVO.class));
        return list;
    
    }
}
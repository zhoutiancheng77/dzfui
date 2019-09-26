package com.dzf.zxkj.platform.services.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ArrayListProcessor;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.DatatruansVO;
import com.dzf.zxkj.platform.services.sys.IChargeEnableService;
import com.dzf.zxkj.platform.services.sys.ICorpService;
import com.dzf.zxkj.platform.services.sys.IVersionMngService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 版本查询后台实现类
 */
@Service("sys_funnodeversionserv")
public class VersionMngServiceImpl implements IVersionMngService {
    @Autowired
    private SingleObjectBO singleObjectBO = null;
    @Autowired
    private IChargeEnableService enableServ;

    @Autowired
    private ICorpService corpService;
    @Override
    public String[] queryCorpVersion(String pk_corp) throws DZFWarpException {
        DZFDate enableDate = enableServ.queryByType(IDzfServiceConst.ChargeType_04);
        if(enableDate == null){
            return null;
        }else if(enableDate.compareTo(new DZFDate()) > 0){
            return null;
        }
        List<DatatruansVO> list = queryCascadeCorps(pk_corp);
        DatatruansVO vo = queryBuyRecords(list);
        List<DatatruansVO> funnodes = null;
        CorpVO corpvo = corpService.queryByPk(list.get(0).getPk_corp());
        if(corpvo != null && corpvo.getIschannel() != null && corpvo.getIschannel().booleanValue()){
            funnodes = queryJmsFunnocde();
        }else{
            funnodes = queryFunnodes(vo);
            if(funnodes == null || funnodes.size() == 0){
                //查询免费版
                funnodes = queryFreeFunnocde();
            }
        }
        String[] nodes = createNodes(funnodes);
        Set<String> set =queryAllParent(nodes);
        if(set != null && set.size() > 0){
            nodes = set.toArray(new String[0]);
        }
        return nodes;
    }
    
    private String[] createNodes(List<DatatruansVO> funnodes){
        if(funnodes == null || funnodes.size() == 0)
            return null;
        List<String> list = new ArrayList<String>();
        for(DatatruansVO vv : funnodes){
            list.add(vv.getPk_funnode());
        }
        return list.toArray(new String[0]);
    }
    
    /**
     * 加盟商节点
     * @author gejw
     * @time 下午4:45:19
     * @return
     * @throws DZFWarpException
     */
    private List<DatatruansVO> queryJmsFunnocde()throws DZFWarpException {
        String sql = " select pk_funnode from ynt_versionfun where pk_version = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(IDzfServiceConst.DzfVersion_06);
        List<DatatruansVO> list = (List<DatatruansVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
        if(list == null || list.size() == 0)
            return null;
        return list;
    }
    
    private List<DatatruansVO> queryFreeFunnocde()throws DZFWarpException {
        String sql = " select pk_funnode from ynt_versionfun where pk_version = ? and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam("000000000000000000000001");
        List<DatatruansVO> list = (List<DatatruansVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
        if(list == null || list.size() == 0)
            return null;
        return list;
    }
    
    private Set<String> queryAllParent(String[] nodes){
        if(nodes == null || nodes.length == 0)
            return null;
        String data = SqlUtil.buildSqlConditionForIn(nodes);
        StringBuffer sf = new StringBuffer();
        sf.append(" select pk_funnode from sm_funnode  start with pk_funnode in ("+data+")");
        sf.append(" connect by  pk_funnode = prior  pk_parent and nvl(dr,0) = 0 ");
        List<DatatruansVO> list = (List<DatatruansVO>)singleObjectBO.executeQuery(sf.toString(), null, new BeanListProcessor(DatatruansVO.class));
        if(list == null || list.size() == 0)
            return null;
        Set<String> set = new HashSet<String>();
        for(DatatruansVO dc : list){
            set.add(dc.getPk_funnode());
        }
        return set;
    }
    
    
    private DatatruansVO queryBuyRecords(List<DatatruansVO> list) throws DZFWarpException {
        if(list == null || list.size() == 0)
            return null;
        DatatruansVO vo = null;
        SQLParameter sp = new SQLParameter();
        if(list != null && list.size() > 0){
//          String sql = "select 1 from wz_buyrecord where pk_corpkjgs = ? and nvl(dr,0) = 0 and vstatus = 1 and effectivedate <=? and duedate >= ? order by purchasedate desc";
            String sql = "select pk_corpkjgs as pk_corp,versiontype from wz_buyrecord where pk_corpkjgs = ? and nvl(dr,0) = 0 and vstatus = 1 and effectivedate <=? and duedate >= ? order by purchasedate desc";
            for(DatatruansVO ds : list){
                sp.clearParams();
                if(IDefaultValue.DefaultGroup.equals(ds.getPk_corp()))
                    break;
                sp.addParam(ds.getPk_corp());
                sp.addParam(new DZFDate());
                sp.addParam(new DZFDate());
                ArrayList<DatatruansVO> alist = (ArrayList<DatatruansVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
//              boolean isexists = singleObjectBO.isExists(ds.getPk_corp(), sql, sp);
//              if(isexists){
//                  vo = ds;
//                  break;
//              }
                if(alist != null && alist.size() > 0){
                    vo = alist.get(0);
                    break;
                }
            }
        }
        return vo;
    }
    
    private List<DatatruansVO> queryFunnodes(DatatruansVO vo){
        if(vo == null)
            return null;
        if(StringUtil.isEmpty(vo.getPk_corp())){
            return null;
        }
        SQLParameter sp = new SQLParameter();
        sp.addParam(vo.getPk_corp());
        StringBuffer sf = new StringBuffer();
        sf.append(" select distinct fun.pk_funnode from wz_buyrecord wb ");
        sf.append(" join wz_dzfservicedes ce on ce.pk_dzfservicedes = wb.versiontype ");
        sf.append(" join ynt_versionfun fun on fun.pk_version = ce.pk_version ");
        sf.append(" where nvl(wb.dr,0) = 0 and nvl(ce.dr,0) = 0 and nvl(fun.dr,0) = 0 and wb.vstatus = 1 ");
        sf.append(" and wb.pk_corpkjgs = ? and wb.versiontype = ?");
        sp.addParam(vo.getVersiontype());
        List<DatatruansVO> list = (List<DatatruansVO>)singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(DatatruansVO.class));
        if(list == null || list.size() == 0)
            return null;
        return list;
    }
    
    
    private List<DatatruansVO> queryCascadeCorps(String pk_corp) throws DZFWarpException {
        if(StringUtil.isEmpty(pk_corp))
            return null;
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        String sql = "select pk_corp,fathercorp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
        List<DatatruansVO> list = (List<DatatruansVO>)singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
        if(list == null || list.size() == 0)
            return null;
        return list;
    }

	@Override
	public String queryKjgsVersion(String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			throw new BusinessException("参数错误。");
		String pk_corpkjgs=null;
		List<DatatruansVO> list = queryCascadeCorps(pk_corp);
		if(list != null && list.size() > 0){
            for(DatatruansVO dvo : list){
                if(dvo.getFathercorp().equals(IDefaultValue.DefaultGroup)){
                	pk_corpkjgs= dvo.getPk_corp();
                	break;
                }
            }
        } 
		if(StringUtil.isEmpty(pk_corpkjgs)){
			throw new BusinessException("会计公司取值错误。");
		}
        SQLParameter sp = new SQLParameter();
        String sql = "select pk_corpkjgs as pk_corp,versiontype from wz_buyrecord where pk_corpkjgs = ? and nvl(dr,0) = 0 and vstatus = 1 and effectivedate <=? and duedate >= ? order by purchasedate desc";
        sp.addParam(pk_corpkjgs);
        sp.addParam(new DZFDate());
        sp.addParam(new DZFDate());
        ArrayList<DatatruansVO> alist = (ArrayList<DatatruansVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
        if(alist != null && alist.size() > 0){
            return alist.get(0).getVersiontype();
        }else{
        	return null;
        }
	}

	@Override
	public String queryKjgsBigVersion(String pk_corp) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp))
			throw new BusinessException("参数错误。");
		String version=queryKjgsVersion(pk_corp);
		if(StringUtil.isEmpty(version)){
			version=IDzfServiceConst.DzfServiceProduct_06;
		}
		return IDzfServiceConst.versionMap.get(version);
	}

	@Override
	public DZFBoolean isChargeByProduct(String pk_corp, String product) throws DZFWarpException {
		if(StringUtil.isEmpty(pk_corp)||StringUtil.isEmpty(product)||(!product.equals(IDzfServiceConst.DzfServiceProduct_04)
				&&!product.equals(IDzfServiceConst.DzfServiceProduct_05)&&!product.equals(IDzfServiceConst.DzfServiceProduct_03)))
			throw new BusinessException("参数错误。");
//		IChargeEnableService enableServ = (IChargeEnableService) SpringUtils.getBean("enableServ");
		String ChargeType=product.equals(IDzfServiceConst.DzfServiceProduct_05)?IDzfServiceConst.ChargeType_03:product.equals(IDzfServiceConst.DzfServiceProduct_03)?IDzfServiceConst.ChargeType_01:IDzfServiceConst.ChargeType_02;
		DZFDate enableDate = enableServ.queryByType(ChargeType);
		if (enableDate != null && enableDate.compareTo(new DZFDate()) <= 0) {
			DZFDate curDate=new DZFDate();//当前系统日期
			int month=curDate.getMonth()-1==0?12:curDate.getMonth()-1;
			String sMonth=month<10?"0"+month:""+month;
			if(sMonth.equals("12")){
				curDate=new DZFDate(Integer.parseInt(curDate.toString().substring(0, 4))-1+"-"+sMonth+"-01");//得到上个月
			}else{
				curDate=new DZFDate(curDate.toString().substring(0, 5)+sMonth+"-01");//得到上个月
			}
			String pk_factory= null;
//                    ifctService.getAthorizeFactoryCorp(curDate, pk_corp);
			String bigVersion=null;
			if(StringUtil.isEmpty(pk_factory)){//如果客户委托给了会计工厂，看会计工厂的版本
				bigVersion=queryKjgsBigVersion(pk_corp);
			}else{
				bigVersion=queryFctBigVersion(pk_factory);
			}
			String sql="select pk_versionpro from ynt_versionpro where nvl(dr,0)=0 and pk_product=? and pk_version=?";
			SQLParameter sp=new SQLParameter();
			sp.addParam(product);
			sp.addParam(bigVersion);
			ArrayList<Object[]> list=(ArrayList<Object[]>)singleObjectBO.executeQuery(sql, sp, new ArrayListProcessor());
			if(list!=null&&list.size()>0){
				return DZFBoolean.FALSE;
			}else{
				return DZFBoolean.TRUE;
			}
		}else{
			return DZFBoolean.FALSE;
		}
		
	}
	
	private String queryFctBigVersion(String pk_factory){
		SQLParameter sp = new SQLParameter();
        String sql = "select pk_corpkjgs as pk_corp,versiontype from wz_buyrecord where pk_corpkjgs = ? and nvl(dr,0) = 0 and vstatus = 1 and effectivedate <=? and duedate >= ? order by purchasedate desc";
        sp.addParam(pk_factory);
        sp.addParam(new DZFDate());
        sp.addParam(new DZFDate());
        ArrayList<DatatruansVO> alist = (ArrayList<DatatruansVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
        String version=null;
        if(alist != null && alist.size() > 0){
        	version= alist.get(0).getVersiontype();
        }
        if(StringUtil.isEmpty(version)){
			version=IDzfServiceConst.DzfServiceProduct_06;
		}
		return IDzfServiceConst.versionMap.get(version);
	}
}
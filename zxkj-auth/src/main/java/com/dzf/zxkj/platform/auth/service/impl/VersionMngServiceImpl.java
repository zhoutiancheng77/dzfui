package com.dzf.zxkj.platform.auth.service.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.SqlUtil;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.auth.entity.FunNode;
import com.dzf.zxkj.platform.auth.mapper.FunNodeMapper;
import com.dzf.zxkj.platform.auth.model.sys.CorpModel;
import com.dzf.zxkj.platform.auth.service.IChargeEnableService;
import com.dzf.zxkj.platform.auth.service.ISysService;
import com.dzf.zxkj.platform.auth.service.IVersionMngService;
import com.dzf.zxkj.platform.model.sys.DatatruansVO;
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
    private FunNodeMapper funNodeMapper;

    @Autowired
    private ISysService sysService;

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
        CorpModel corpModel = sysService.queryCorpByPk(list.get(0).getPk_corp());
        if(corpModel != null && corpModel.getIschannel() != null && corpModel.getIschannel().booleanValue()){
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

    @Override
//    @CacheInvalidate(name = "corp_user_perssion", key = "#userid+'-'+#pk_corp")
    public List<FunNode> getFunNodeByUseridAndPkCorp(String userid, String pk_corp) {
        return funNodeMapper.getFunNodeByUseridAndPkCorp(userid, pk_corp);
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

}
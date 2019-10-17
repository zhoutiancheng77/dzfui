package com.dzf.zxkj.platform.service.sys.impl;


import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.IDzfServiceConst;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.common.lang.DZFBoolean;
import com.dzf.zxkj.common.lang.DZFDate;
import com.dzf.zxkj.common.utils.DateUtils;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.model.sys.DZFBalanceVO;
import com.dzf.zxkj.platform.model.sys.DatatruansVO;
import com.dzf.zxkj.platform.service.sys.IBDCorpService;
import com.dzf.zxkj.platform.service.sys.IChargeEnableService;
import com.dzf.zxkj.platform.service.sys.ICorpAccountService;
import com.dzf.zxkj.platform.service.sys.ICorpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("corpAccountServiceImpl")
@SuppressWarnings("all")
public class CorpAccountServiceImpl implements ICorpAccountService {

    @Autowired
    private IBDCorpService corpServiceImpl;
    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private IChargeEnableService enableServ;

    @Autowired
    private ICorpService corpService;

    @Override
    public String checkCorpAccount(final String pk_corp, final Integer totalNums, boolean isReadRedis, String corpkid) throws DZFWarpException {
//        remove(pk_corp);
        //代账机构
        CorpVO cvo = corpService.queryByPk(pk_corp);
        if (cvo != null) {
            if (cvo.getIschannel() != null && cvo.getIschannel().booleanValue()) {
                return checkChannelContract(pk_corp, corpkid);
            }
        }
        return checkCorpAccountNums(pk_corp, totalNums, isReadRedis, corpkid);
    }

    @Override
    public String checkCorpAccountNums(final String pk_corp, final Integer totalNums, boolean isReadRedis, String corpkid)
            throws DZFWarpException {

        DZFDate enableDate = enableServ.queryByType(IDzfServiceConst.ChargeType_04);
        if (enableDate == null) {
            return null;
        } else if (enableDate.compareTo(new DZFDate()) > 0) {
            return null;
        }
        Integer num = null;

        if (num == null) {
            num = corpServiceImpl.queryAcountCorps(pk_corp);
        }
        if (num == null) {
            num = 0;
        }
        if (num >= totalNums) {
            return "贵公司购买的" + new DZFDate().getYear() + "年度产品可用户数为" + totalNums
                    + ",小于贵公司代账客户数,请联系大账房购买补足户数。";
        }
        return null;

    }

    /**
     * 加盟商合同校验客户合同是否在服务周期内
     *
     * @param corpid
     * @return
     */
    @Override
    public String checkChannelContract(String pk_corp, String corpkid) {
        if (StringUtil.isEmpty(corpkid)) {
            return "客户没有有效服务期内的服务合同";
        }

        CorpVO cvo = corpServiceImpl.queryByID(corpkid);
        //如果是存量客户，不做控制
        if (cvo != null && cvo.getIsncust() != null && cvo.getIsncust().booleanValue()) {
            return null;
        }
        String vperiod = DateUtils.getPreviousPeriod(DateUtils.getPeriod(new DZFDate()));
        StringBuffer sql = new StringBuffer();
        SQLParameter params = new SQLParameter();
        params.addParam(pk_corp);
        params.addParam(corpkid);
        params.addParam(vperiod);
        //先检查合同有没有到期，如果合同已到期，再检查在线业务系统有没有设置延期做账
        sql.append(" select vcontcode from ynt_contract ");
        sql.append(" where nvl(dr,0) = 0 and pk_corp = ? and pk_corpk = ?");
        sql.append(" and nvl(icontracttype,0) = 2 and vendperiod >= ? and vstatus = 1");
        boolean exist = singleObjectBO.isExists(corpkid, sql.toString(), params);
        if (!exist) {
//            return "客户的合同已到期，请续签合同后再使用！所造成的不便，请谅解！";
            //检查有没有设置延期做账
            sql = new StringBuffer();
            sql.append(" select vcontcode from cn_accountset ");
            sql.append(" where nvl(dr,0) = 0 and pk_corp = ? and pk_corpk = ?");
            sql.append(" and istatus = 0 and vchangeperiod >= ? ");
            exist = singleObjectBO.isExists(corpkid, sql.toString(), params);
            if (!exist) {
                return "客户的合同已到期，请续签合同后再使用！所造成的不便，请谅解！";
            }
        }
        return null;
    }

    private Integer getCorpNums(String key, String field, String pk_corp) {
        String keyStr = key + field;
        int num = 0;
        Integer nums = corpServiceImpl.queryAcountCorps(pk_corp);
        num = nums;
        return num;
    }


    @Override
    public DatatruansVO queryBuyRecords(String pk_corp) throws DZFWarpException {

        DatatruansVO bvo = null;

        DatatruansVO dvo = queryCascadeCorps(pk_corp);
        if (dvo != null) {
            CorpVO topCorpVO = corpService.queryByPk(dvo.getPk_corp());
            DZFDate enableDate = enableServ.queryByType(IDzfServiceConst.ChargeType_04);
            if (enableDate == null) {
                bvo = new DatatruansVO();
                bvo.setUsercount(99999);//免费不限制户数
                bvo.setPk_corp(dvo.getPk_corp());
            } else if (enableDate.compareTo(new DZFDate()) > 0) {
                bvo = new DatatruansVO();
                bvo.setUsercount(99999);//免费不限制户数
                bvo.setPk_corp(dvo.getPk_corp());
            } else {
                if (topCorpVO != null && topCorpVO.getIschannel() != null && topCorpVO.getIschannel().booleanValue()) {
                    bvo = new DatatruansVO();
                    bvo.setUsercount(99999);//免费不限制户数
                    bvo.setPk_corp(dvo.getPk_corp());
                } else {
                    StringBuffer sql = new StringBuffer();
                    sql.append(" select sum(usercount) as  usercount,pk_corpkjgs as pk_corp from wz_buyrecord ");
                    sql.append(" where pk_corpkjgs = ? and nvl(dr,0) = 0");
                    sql.append(" and vstatus = 1 ");
                    sql.append(" and effectivedate <=? and duedate >= ?");
                    sql.append(" group by pk_corpkjgs");
                    SQLParameter params = new SQLParameter();
                    params.addParam(dvo.getPk_corp());
                    params.addParam(new DZFDate());
                    params.addParam(new DZFDate());
                    List<DatatruansVO> bvos = (List<DatatruansVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(DatatruansVO.class));
                    if (bvos != null && bvos.size() > 0) {
                        bvo = bvos.get(0);
                        if (bvo.getUsercount() == null || bvo.getUsercount() == 0) {
                            bvo.setUsercount(99999);//代表不限制
                        }
                    } else {
                        sql = new StringBuffer();
                        sql.append(" select sum(usercount) as  usercount,pk_corpkjgs as pk_corp from wz_buyrecord ");
                        sql.append(" where pk_corpkjgs = ? and nvl(dr,0) = 0");
                        sql.append(" and vstatus = 1 ");
                        sql.append(" group by pk_corpkjgs");
                        params.clearParams();
                        params.addParam(dvo.getPk_corp());
                        bvos = (List<DatatruansVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(DatatruansVO.class));
                        if (bvos != null && bvos.size() > 0) {
                            throw new BusinessException("服务购买已到期，请续费！所造成的不便，请谅解！");
                        } else {
                            bvo = new DatatruansVO();
                            bvo.setUsercount(99999);//免费不限制户数
                            bvo.setPk_corp(dvo.getPk_corp());
                        }
                    }
                }
            }
        } else {
            throw new BusinessException("未找到总代账机构");
        }
        return bvo;
    }

    /**
     * 返回顶级代账公司
     *
     * @param pk_corp
     * @return
     * @throws DZFWarpException
     * @author gejw
     * @time 下午3:04:06
     */
    private DatatruansVO queryCascadeCorps(String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp))
            return null;
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        String sql = "select pk_corp,fathercorp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
        List<DatatruansVO> list = (List<DatatruansVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DatatruansVO.class));
        if (list != null && list.size() > 0) {
            for (DatatruansVO dvo : list) {
                if (dvo.getFathercorp().equals(IDefaultValue.DefaultGroup)) {
                    return dvo;
                }
            }
        }
        return null;
    }
    /**
     * 递增
     */
//    public void incr(final String pk_corp) {
//        final String key = pk_corp + "_accountnum";
//        RedisClient.getInstance().exec(new IRedisCallback() {
//            public Object exec(Jedis jedis) {
//                if(jedis == null){
//                    return null;
//                }
//                return null;
//            }
//        });
//    }

    /**
     * 递减
     *
     * @param nums
     * @param pk_corp
     */
//    @Override
//    public void decr(final String pk_corp) {
//       final String key = pk_corp + "_accountnum";
//       RedisClient.getInstance().exec(new IRedisCallback() {
//            public Object exec(Jedis jedis) {
//                if(jedis == null){
//                    return null;
//                }
//                return null;
//            }
//        });
//    }

    @Override
    public void checkServicePeriod(String pk_corp) throws DZFWarpException {
        DatatruansVO dvo = queryCascadeCorps(pk_corp);
        if (dvo != null) {
            StringBuffer sql = new StringBuffer();
            sql.append(" select sum(usercount) as  usercount,pk_corpkjgs as pk_corp from wz_buyrecord ");
            sql.append(" where pk_corpkjgs = ? and nvl(dr,0) = 0");
            sql.append(" and vstatus = 1 ");
            sql.append(" and effectivedate <=? and duedate >= ?");
            sql.append(" group by pk_corpkjgs");
            SQLParameter params = new SQLParameter();
            params.addParam(dvo.getPk_corp());
            params.addParam(new DZFDate());
            params.addParam(new DZFDate());
            List<DatatruansVO> bvos = (List<DatatruansVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(DatatruansVO.class));
            if (bvos == null || bvos.size() == 0) {
                sql = new StringBuffer();
                sql.append(" select sum(usercount) as  usercount,pk_corpkjgs as pk_corp from wz_buyrecord ");
                sql.append(" where pk_corpkjgs = ? and nvl(dr,0) = 0");
                sql.append(" and vstatus = 1 ");
                sql.append(" group by pk_corpkjgs");
                params.clearParams();
                params.addParam(dvo.getPk_corp());
                bvos = (List<DatatruansVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(DatatruansVO.class));
                if (bvos != null && bvos.size() > 0) {
                    throw new BusinessException("服务购买已到期，请续费！所造成的不便，请谅解！");
                }
            }
        } else {
            throw new BusinessException("未找到总代账机构");
        }
    }

    @Override
    public DZFBoolean hasBuyVoucher(String pk_corpkjgs) throws DZFWarpException {
        String sql = "select * from dzf_balance where nvl(dr,0)=0 and pk_corp_yy=? and pk_dzfservicedes=?";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corpkjgs);
        sp.addParam(IDzfServiceConst.DzfServiceProduct_04);
        List<DZFBalanceVO> list = (List<DZFBalanceVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(DZFBalanceVO.class));
        if (list != null && list.size() > 0) {
            return DZFBoolean.TRUE;
        } else {
            return DZFBoolean.FALSE;
        }

    }

    public DatatruansVO queryBuyRecord(CorpVO cvo) throws DZFWarpException {
        String pk_corp = null;
        if (!StringUtil.isEmpty(cvo.getFathercorp()) && cvo.getFathercorp().equals(IDefaultValue.DefaultGroup)) {
            pk_corp = cvo.getPk_corp();
        } else {
            DatatruansVO dvo = queryCascadeCorps(pk_corp);
            if (dvo != null) {
                pk_corp = dvo.getPk_corp();
            }
        }
        if (!StringUtil.isEmpty(pk_corp)) {
            StringBuffer sql = new StringBuffer();
            sql.append(" select pk_corpkjgs as pk_corp,duedate from wz_buyrecord ");
            sql.append(" where pk_corpkjgs = ? and nvl(dr,0) = 0");
            sql.append(" and vstatus = 1 ");
            sql.append(" and duedate >= ?");
            sql.append(" order by duedate desc");
            SQLParameter params = new SQLParameter();
            params.addParam(pk_corp);
            params.addParam(new DZFDate());
            List<DatatruansVO> bvos = (List<DatatruansVO>) singleObjectBO.executeQuery(sql.toString(), params, new BeanListProcessor(DatatruansVO.class));
            if (bvos != null && bvos.size() > 0) {
                return bvos.get(0);
            }
        } else {
            throw new BusinessException("未找到总代账机构");
        }
        return null;
    }
}

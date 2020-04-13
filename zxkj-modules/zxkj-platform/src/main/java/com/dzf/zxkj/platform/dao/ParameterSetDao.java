package com.dzf.zxkj.platform.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.constant.RedisCacheConstant;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class ParameterSetDao {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Cached(name = RedisCacheConstant.PARAMETER_NAME, expire = 7, key = "#pk_corp", cacheType = CacheType.REMOTE, timeUnit = TimeUnit.DAYS)
    public List<YntParameterSet> queryParamters(String pk_corp) throws DZFWarpException {
        String where = " select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior fathercorp and nvl(dr,0) = 0 ";
        StringBuffer sf = new StringBuffer();
        sf.append("select * from ynt_parameter where nvl(dr,0) = 0 and pk_corp in(");
        sf.append(where);
        sf.append(")  ");
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        return (List<YntParameterSet>)
                singleObjectBO.executeQuery(sf.toString(), sp, new BeanListProcessor(YntParameterSet.class));
    }
}

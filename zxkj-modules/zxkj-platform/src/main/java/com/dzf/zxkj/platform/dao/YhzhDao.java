package com.dzf.zxkj.platform.dao;

import com.alicp.jetcache.anno.CacheType;
import com.alicp.jetcache.anno.Cached;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.platform.model.bdset.BankAccountVO;
import com.dzf.zxkj.redis.ZxkjRedisCachePrefix;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Repository
public class YhzhDao {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Cached(name = ZxkjRedisCachePrefix.BANK_ACCOUNT, expire = 3, key = "#pk_corp", cacheType = CacheType.REMOTE, timeUnit = TimeUnit.DAYS)
    public List<BankAccountVO> queryByPkCorp(String pk_corp) {
        StringBuffer sf = new StringBuffer();
        SQLParameter sp = new SQLParameter();
        sf.append(" select y.*,cp.accountcode,cp.accountname ");
        sf.append("   from ynt_bankaccount y ");
        sf.append("   left join ynt_cpaccount cp ");
        sf.append("     on y.relatedsubj = cp.pk_corp_account ");
        sf.append("  Where nvl(y.dr, 0) = 0 ");

        sf.append("    and y.pk_corp = ? ");
        sf.append("    order by y.ts  ");
        sp.addParam(pk_corp);

        return (List<BankAccountVO>) singleObjectBO.executeQuery(sf.toString(),
                sp, new BeanListProcessor(BankAccountVO.class));

    }

}

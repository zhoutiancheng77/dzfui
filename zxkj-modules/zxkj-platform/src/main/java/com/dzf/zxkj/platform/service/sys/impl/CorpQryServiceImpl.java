package com.dzf.zxkj.platform.service.sys.impl;

import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.BeanListProcessor;
import com.dzf.zxkj.common.utils.IDefaultValue;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.CorpVO;
import com.dzf.zxkj.platform.service.sys.ICorpQryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 客户档案查询
 *
 * @author gejw
 * @time 2018年5月23日 上午9:47:31
 */
@Service("corpQryImpl")
@Slf4j
public class CorpQryServiceImpl implements ICorpQryService {
    @Autowired
    private SingleObjectBO singleObjectBO;

    @Override
    public CorpVO queryTopCorp(String pk_corp) throws DZFWarpException {
        List<CorpVO> list = queryCascadeCorps(pk_corp);
        if (list != null && list.size() > 0) {
            for (CorpVO dvo : list) {
                if (dvo.getFathercorp().equals(IDefaultValue.DefaultGroup)) {
                    return dvo;
                }
            }
        }
        return null;
    }


    private List<CorpVO> queryCascadeCorps(String pk_corp) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp))
            return null;
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        String sql = "select * from bd_corp  start with pk_corp = ? connect by  pk_corp = prior  fathercorp and nvl(dr,0) = 0";
        List<CorpVO> list = (List<CorpVO>) singleObjectBO.executeQuery(sql, sp, new BeanListProcessor(CorpVO.class));
        if (list == null || list.size() == 0)
            return null;
        return list;
    }
}
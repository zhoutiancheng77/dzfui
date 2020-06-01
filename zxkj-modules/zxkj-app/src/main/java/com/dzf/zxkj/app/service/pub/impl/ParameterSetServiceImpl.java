package com.dzf.zxkj.app.service.pub.impl;

import com.dzf.zxkj.app.dao.ParameterSetDao;
import com.dzf.zxkj.app.service.pub.IParameterSetService;
import com.dzf.zxkj.base.dao.SingleObjectBO;
import com.dzf.zxkj.base.exception.BusinessException;
import com.dzf.zxkj.base.exception.DZFWarpException;
import com.dzf.zxkj.base.framework.SQLParameter;
import com.dzf.zxkj.base.framework.processor.ResultSetProcessor;
import com.dzf.zxkj.base.utils.DZfcommonTools;
import com.dzf.zxkj.common.utils.ObjectUtils;
import com.dzf.zxkj.common.utils.StringUtil;
import com.dzf.zxkj.platform.model.sys.YntParameterSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("sys_parameteract")
public class ParameterSetServiceImpl implements IParameterSetService {

    @Autowired
    private SingleObjectBO singleObjectBO;

    @Autowired
    private ParameterSetDao parameterSetDao;

    @Override
    public YntParameterSet queryParamterbyCode(String pk_corp, String paramcode) throws DZFWarpException {
        if (StringUtil.isEmpty(pk_corp) || StringUtil.isEmpty(paramcode)) {
            throw new BusinessException("参数为空，请确认参数!");
        }
        YntParameterSet set = null;
        List<YntParameterSet> ancevos = queryParamters(pk_corp, paramcode);
        List<String> lista = querycorpsByorder(pk_corp);
        if (ancevos == null || ancevos.size() == 0)
            return set;
        List<YntParameterSet> z1 = setGroup(ancevos, lista);
        if (z1 != null && z1.size() > 0) {
            set = z1.get(0);
            set.setPk_corp(pk_corp);
            set.setPk_parameter(null);
        }
        return set;
    }

    private List<YntParameterSet> queryParamters(String pk_corp, String parameterbm) throws DZFWarpException {

        List<YntParameterSet> parameterSetList = parameterSetDao.queryParamters(pk_corp);

        if (!StringUtil.isEmpty(parameterbm)) {
            return ObjectUtils.notEmpty(parameterSetList) ? parameterSetList.stream().filter(v -> parameterbm.equalsIgnoreCase(v.getParameterbm())).collect(Collectors.toList()) : new ArrayList<YntParameterSet>();
        }
        return parameterSetList;
    }

    private List<String> querycorpsByorder(String pk_corp) throws DZFWarpException {
        String sql = " select pk_corp from bd_corp  start with pk_corp = ? connect by  pk_corp = prior fathercorp and nvl(dr,0) = 0 ";
        SQLParameter sp = new SQLParameter();
        sp.addParam(pk_corp);
        List<String> ancevos = (List<String>)
                singleObjectBO.executeQuery(sql, sp, new ResultSetProcessor() {

                    @Override
                    public Object handleResultSet(ResultSet resultset) throws SQLException {
                        List<String> list = new ArrayList<String>();
                        while (resultset.next()) {
                            list.add(resultset.getString("pk_corp"));
                        }
                        return list;
                    }

                });
        return ancevos;
    }

    private List<YntParameterSet> setGroup(List<YntParameterSet> ancevos, List<String> lista) {
        Map<String, List<YntParameterSet>> map = (Map<String, List<YntParameterSet>>)
                DZfcommonTools.hashlizeObject(ancevos, new String[]{"parameterbm"});
        Iterator<String> it = map.keySet().iterator();
        List<YntParameterSet> z1 = new ArrayList<YntParameterSet>();
//		sortParameter sort = new sortParameter();
        while (it.hasNext()) {
            String key = it.next();
            List<YntParameterSet> z = map.get(key);
            if (z != null && z.size() > 0) {
                //排序，取pk_corp 最大的
//				Collections.sort(z,sort);
                YntParameterSet set = getYntParameterSet(z, lista);
                if (set != null) {
                    z1.add(set);
                }
            }
        }
        return z1;
    }

    private YntParameterSet getYntParameterSet(List<YntParameterSet> z1, List<String> lista) {
        if (z1 == null || z1.size() == 0) {
            return null;
        }
        YntParameterSet set = null;
        for (String s : lista) {
            for (YntParameterSet s1 : z1) {
                if (s.equals(s1.getPk_corp())) {
                    set = s1;
                    break;
                }
            }
            if (set != null) {
                break;
            }
        }
        return set;
    }

}
